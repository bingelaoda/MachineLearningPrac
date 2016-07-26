/*   1:    */ package org.boon.core.reflection;
/*   2:    */ 
/*   3:    */ import java.lang.ref.WeakReference;
/*   4:    */ import java.lang.reflect.Field;
/*   5:    */ import java.lang.reflect.Method;
/*   6:    */ import java.util.ArrayList;
/*   7:    */ import java.util.Collection;
/*   8:    */ import java.util.Iterator;
/*   9:    */ import java.util.LinkedHashMap;
/*  10:    */ import java.util.List;
/*  11:    */ import java.util.Map;
/*  12:    */ import java.util.Map.Entry;
/*  13:    */ import java.util.concurrent.ConcurrentHashMap;
/*  14:    */ import java.util.logging.Logger;
/*  15:    */ import org.boon.Boon;
/*  16:    */ import org.boon.Exceptions;
/*  17:    */ import org.boon.Lists;
/*  18:    */ import org.boon.Pair;
/*  19:    */ import org.boon.Str;
/*  20:    */ import org.boon.core.Function;
/*  21:    */ import org.boon.core.Sys;
/*  22:    */ import org.boon.core.Typ;
/*  23:    */ import org.boon.core.reflection.fields.FieldAccess;
/*  24:    */ import org.boon.core.reflection.fields.PropertyField;
/*  25:    */ import org.boon.core.reflection.fields.ReflectField;
/*  26:    */ import org.boon.core.reflection.fields.UnsafeField;
/*  27:    */ import sun.misc.Unsafe;
/*  28:    */ 
/*  29:    */ public class Reflection
/*  30:    */ {
/*  31:    */   public static List<Field> getFields(Class<? extends Object> theClass)
/*  32:    */   {
/*  33: 63 */     List<Field> fields = (List)context().__fields.get(theClass);
/*  34: 64 */     if (fields != null) {
/*  35: 65 */       return fields;
/*  36:    */     }
/*  37: 68 */     fields = Lists.list(theClass.getDeclaredFields());
/*  38: 69 */     boolean foundCrap = false;
/*  39: 70 */     for (Field field : fields) {
/*  40: 71 */       if (field.getName().indexOf('$') != -1) {
/*  41: 72 */         foundCrap = true;
/*  42:    */       } else {
/*  43: 75 */         field.setAccessible(true);
/*  44:    */       }
/*  45:    */     }
/*  46: 79 */     if (foundCrap)
/*  47:    */     {
/*  48: 80 */       List<Field> copy = Lists.copy(fields);
/*  49: 82 */       for (Field field : copy) {
/*  50: 83 */         if (field.getName().indexOf('$') != -1) {
/*  51: 84 */           fields.remove(field);
/*  52:    */         }
/*  53:    */       }
/*  54:    */     }
/*  55: 91 */     return fields;
/*  56:    */   }
/*  57:    */   
/*  58: 95 */   private static final Logger log = Logger.getLogger(Reflection.class.getName());
/*  59:    */   private static boolean _useUnsafe;
/*  60:    */   private static final boolean useUnsafe;
/*  61:    */   private static final Context _context;
/*  62:    */   private static WeakReference<Context> weakContext;
/*  63:    */   
/*  64:    */   public static Unsafe getUnsafe()
/*  65:    */   {
/*  66:137 */     if (context().control == null) {
/*  67:    */       try
/*  68:    */       {
/*  69:139 */         Field f = Unsafe.class.getDeclaredField("theUnsafe");
/*  70:140 */         f.setAccessible(true);
/*  71:141 */         context().control = ((Unsafe)f.get(null));
/*  72:142 */         return context().control;
/*  73:    */       }
/*  74:    */       catch (Exception e)
/*  75:    */       {
/*  76:144 */         return null;
/*  77:    */       }
/*  78:    */     }
/*  79:147 */     return context().control;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public static Object contextToHold()
/*  83:    */   {
/*  84:153 */     return context();
/*  85:    */   }
/*  86:    */   
/*  87:    */   static Context context()
/*  88:    */   {
/*  89:159 */     if (_context != null) {
/*  90:160 */       return _context;
/*  91:    */     }
/*  92:162 */     Context context = (Context)weakContext.get();
/*  93:163 */     if (context == null)
/*  94:    */     {
/*  95:164 */       context = new Context();
/*  96:165 */       weakContext = new WeakReference(context);
/*  97:    */     }
/*  98:167 */     return context;
/*  99:    */   }
/* 100:    */   
/* 101:    */   static class Context
/* 102:    */   {
/* 103:174 */     Map<Class<?>, List<Field>> __fields = new ConcurrentHashMap(200);
/* 104:    */     Unsafe control;
/* 105:177 */     Map<String, String> _sortableFields = new ConcurrentHashMap();
/* 106:179 */     Map<Class<?>, ClassMeta<?>> _classMetaMap = new ConcurrentHashMap(200);
/* 107:181 */     Map<Class<?>, Map<String, FieldAccess>> _allAccessorReflectionFieldsCache = new ConcurrentHashMap(200);
/* 108:182 */     Map<Class<?>, Map<String, FieldAccess>> _allAccessorPropertyFieldsCache = new ConcurrentHashMap(200);
/* 109:183 */     Map<Class<?>, Map<String, FieldAccess>> _allAccessorUnsafeFieldsCache = new ConcurrentHashMap(200);
/* 110:185 */     Map<Class<?>, Map<String, FieldAccess>> _combinedFieldsFieldsFirst = new ConcurrentHashMap(200);
/* 111:186 */     Map<Class<?>, Map<String, FieldAccess>> _combinedFieldsFieldsFirstForSerializer = new ConcurrentHashMap(200);
/* 112:188 */     Map<Class<?>, Map<String, FieldAccess>> _combinedFieldsPropertyFirst = new ConcurrentHashMap(200);
/* 113:189 */     Map<Class<?>, Map<String, FieldAccess>> _combinedFieldsPropertyFirstForSerializer = new ConcurrentHashMap(200);
/* 114:    */   }
/* 115:    */   
/* 116:    */   private static Map<String, FieldAccess> getCombinedFieldsPropertyFirst(Class<? extends Object> theClass)
/* 117:    */   {
/* 118:196 */     return (Map)context()._combinedFieldsPropertyFirst.get(theClass);
/* 119:    */   }
/* 120:    */   
/* 121:    */   private static Map<String, FieldAccess> getCombinedFieldsPropertyFirstForSerializer(Class<? extends Object> theClass)
/* 122:    */   {
/* 123:201 */     return (Map)context()._combinedFieldsPropertyFirstForSerializer.get(theClass);
/* 124:    */   }
/* 125:    */   
/* 126:    */   private static Map<String, FieldAccess> getCombinedFieldsFieldFirst(Class<? extends Object> theClass)
/* 127:    */   {
/* 128:205 */     return (Map)context()._combinedFieldsFieldsFirst.get(theClass);
/* 129:    */   }
/* 130:    */   
/* 131:    */   private static Map<String, FieldAccess> getCombinedFieldsFieldFirstForSerializer(Class<? extends Object> theClass)
/* 132:    */   {
/* 133:209 */     return (Map)context()._combinedFieldsFieldsFirstForSerializer.get(theClass);
/* 134:    */   }
/* 135:    */   
/* 136:    */   private static void putCombinedFieldsPropertyFirst(Class<?> theClass, Map<String, FieldAccess> map)
/* 137:    */   {
/* 138:213 */     context()._combinedFieldsPropertyFirst.put(theClass, map);
/* 139:    */   }
/* 140:    */   
/* 141:    */   private static void putCombinedFieldsPropertyFirstForSerializer(Class<?> theClass, Map<String, FieldAccess> map)
/* 142:    */   {
/* 143:218 */     context()._combinedFieldsPropertyFirst.put(theClass, map);
/* 144:    */   }
/* 145:    */   
/* 146:    */   private static void putCombinedFieldsFieldFirst(Class<?> theClass, Map<String, FieldAccess> map)
/* 147:    */   {
/* 148:222 */     context()._combinedFieldsFieldsFirst.put(theClass, map);
/* 149:    */   }
/* 150:    */   
/* 151:    */   private static void putCombinedFieldsFieldFirstForSerializer(Class<?> theClass, Map<String, FieldAccess> map)
/* 152:    */   {
/* 153:226 */     context()._combinedFieldsFieldsFirstForSerializer.put(theClass, map);
/* 154:    */   }
/* 155:    */   
/* 156:    */   static
/* 157:    */   {
/* 158:    */     try
/* 159:    */     {
/* 160:101 */       Class.forName("sun.misc.Unsafe");
/* 161:102 */       _useUnsafe = true;
/* 162:    */     }
/* 163:    */     catch (ClassNotFoundException e)
/* 164:    */     {
/* 165:104 */       e.printStackTrace();
/* 166:105 */       _useUnsafe = false;
/* 167:    */     }
/* 168:108 */     _useUnsafe = (_useUnsafe) && (!Boolean.getBoolean("org.boon.noUnsafe"));
/* 169:    */     
/* 170:    */ 
/* 171:111 */     useUnsafe = _useUnsafe;
/* 172:    */     
/* 173:    */ 
/* 174:    */ 
/* 175:    */ 
/* 176:116 */     weakContext = new WeakReference(null);
/* 177:    */     
/* 178:    */ 
/* 179:    */ 
/* 180:    */ 
/* 181:121 */     boolean noStatics = Boolean.getBoolean("org.boon.noStatics");
/* 182:122 */     if ((noStatics) || (Sys.inContainer()))
/* 183:    */     {
/* 184:124 */       _context = null;
/* 185:125 */       weakContext = new WeakReference(new Context());
/* 186:    */     }
/* 187:    */     else
/* 188:    */     {
/* 189:129 */       _context = new Context();
/* 190:    */     }
/* 191:    */     try
/* 192:    */     {
/* 193:231 */       if (_useUnsafe) {
/* 194:232 */         field = String.class.getDeclaredField("value");
/* 195:    */       }
/* 196:    */     }
/* 197:    */     catch (Exception ex)
/* 198:    */     {
/* 199:    */       Field field;
/* 200:235 */       Exceptions.handle(ex);
/* 201:    */     }
/* 202:    */   }
/* 203:    */   
/* 204:    */   private static void setAccessorFieldInCache(Class<? extends Object> theClass, boolean useUnsafe, Map<String, FieldAccess> map)
/* 205:    */   {
/* 206:241 */     if (useUnsafe) {
/* 207:242 */       context()._allAccessorUnsafeFieldsCache.put(theClass, map);
/* 208:    */     } else {
/* 209:244 */       context()._allAccessorReflectionFieldsCache.put(theClass, map);
/* 210:    */     }
/* 211:    */   }
/* 212:    */   
/* 213:    */   private static void setPropertyAccessorFieldsInCache(Class<? extends Object> theClass, Map<String, FieldAccess> map)
/* 214:    */   {
/* 215:250 */     context()._allAccessorPropertyFieldsCache.put(theClass, map);
/* 216:    */   }
/* 217:    */   
/* 218:    */   private static Map<String, FieldAccess> getPropertyAccessorFieldsFromCache(Class<? extends Object> theClass)
/* 219:    */   {
/* 220:255 */     return (Map)context()._allAccessorPropertyFieldsCache.get(theClass);
/* 221:    */   }
/* 222:    */   
/* 223:    */   private static Map<String, FieldAccess> getAccessorFieldsFromCache(Class<? extends Object> theClass, boolean useUnsafe)
/* 224:    */   {
/* 225:260 */     if (useUnsafe) {
/* 226:261 */       return (Map)context()._allAccessorUnsafeFieldsCache.get(theClass);
/* 227:    */     }
/* 228:263 */     return (Map)context()._allAccessorReflectionFieldsCache.get(theClass);
/* 229:    */   }
/* 230:    */   
/* 231:    */   public static Map<String, FieldAccess> getPropertyFieldAccessMapFieldFirst(Class<?> clazz)
/* 232:    */   {
/* 233:278 */     Map<String, FieldAccess> combinedFieldsFieldFirst = getCombinedFieldsFieldFirst(clazz);
/* 234:280 */     if (combinedFieldsFieldFirst != null) {
/* 235:281 */       return combinedFieldsFieldFirst;
/* 236:    */     }
/* 237:285 */     Map<String, FieldAccess> fieldsFallbacks = null;
/* 238:    */     
/* 239:    */ 
/* 240:288 */     Map<String, FieldAccess> fieldsPrimary = null;
/* 241:    */     
/* 242:    */ 
/* 243:    */ 
/* 244:292 */     fieldsPrimary = getAllAccessorFields(clazz, true);
/* 245:    */     
/* 246:294 */     fieldsFallbacks = getPropertyFieldAccessors(clazz);
/* 247:    */     
/* 248:    */ 
/* 249:297 */     combineFieldMaps(fieldsFallbacks, fieldsPrimary);
/* 250:    */     
/* 251:299 */     combinedFieldsFieldFirst = fieldsPrimary;
/* 252:    */     
/* 253:301 */     putCombinedFieldsFieldFirst(clazz, combinedFieldsFieldFirst);
/* 254:302 */     return combinedFieldsFieldFirst;
/* 255:    */   }
/* 256:    */   
/* 257:    */   public static Map<String, FieldAccess> getPropertyFieldAccessMapFieldFirstForSerializer(Class<?> clazz)
/* 258:    */   {
/* 259:318 */     Map<String, FieldAccess> combinedFieldsFieldFirst = getCombinedFieldsFieldFirstForSerializer(clazz);
/* 260:320 */     if (combinedFieldsFieldFirst != null) {
/* 261:321 */       return combinedFieldsFieldFirst;
/* 262:    */     }
/* 263:325 */     Map<String, FieldAccess> fieldsFallbacks = null;
/* 264:    */     
/* 265:    */ 
/* 266:328 */     Map<String, FieldAccess> fieldsPrimary = null;
/* 267:    */     
/* 268:    */ 
/* 269:    */ 
/* 270:332 */     fieldsPrimary = getAllAccessorFields(clazz, true);
/* 271:333 */     fieldsFallbacks = getPropertyFieldAccessors(clazz);
/* 272:    */     
/* 273:335 */     fieldsPrimary = removeNonSerializable(fieldsPrimary);
/* 274:336 */     fieldsFallbacks = removeNonSerializable(fieldsFallbacks);
/* 275:    */     
/* 276:338 */     combineFieldMaps(fieldsFallbacks, fieldsPrimary);
/* 277:    */     
/* 278:340 */     combinedFieldsFieldFirst = fieldsPrimary;
/* 279:    */     
/* 280:342 */     putCombinedFieldsFieldFirstForSerializer(clazz, combinedFieldsFieldFirst);
/* 281:343 */     return combinedFieldsFieldFirst;
/* 282:    */   }
/* 283:    */   
/* 284:    */   private static void combineFieldMaps(Map<String, FieldAccess> fieldsFallbacks, Map<String, FieldAccess> fieldsPrimary)
/* 285:    */   {
/* 286:353 */     for (Map.Entry<String, FieldAccess> field : fieldsFallbacks.entrySet()) {
/* 287:354 */       if (!fieldsPrimary.containsKey(field.getKey())) {
/* 288:355 */         fieldsPrimary.put(field.getKey(), field.getValue());
/* 289:    */       }
/* 290:    */     }
/* 291:    */   }
/* 292:    */   
/* 293:    */   public static Map<String, FieldAccess> getPropertyFieldAccessMapPropertyFirst(Class<?> clazz)
/* 294:    */   {
/* 295:362 */     Map<String, FieldAccess> combinedFields = getCombinedFieldsPropertyFirst(clazz);
/* 296:364 */     if (combinedFields != null) {
/* 297:365 */       return combinedFields;
/* 298:    */     }
/* 299:368 */     Map<String, FieldAccess> fieldsFallbacks = null;
/* 300:    */     
/* 301:    */ 
/* 302:371 */     Map<String, FieldAccess> fieldsPrimary = null;
/* 303:    */     
/* 304:    */ 
/* 305:    */ 
/* 306:    */ 
/* 307:376 */     fieldsFallbacks = getAllAccessorFields(clazz, true);
/* 308:377 */     fieldsPrimary = getPropertyFieldAccessors(clazz);
/* 309:    */     
/* 310:    */ 
/* 311:    */ 
/* 312:381 */     combineFieldMaps(fieldsFallbacks, fieldsPrimary);
/* 313:    */     
/* 314:383 */     combinedFields = fieldsPrimary;
/* 315:384 */     putCombinedFieldsPropertyFirst(clazz, combinedFields);
/* 316:385 */     return combinedFields;
/* 317:    */   }
/* 318:    */   
/* 319:    */   public static Map<String, FieldAccess> getPropertyFieldAccessMapPropertyFirstForSerializer(Class<?> clazz)
/* 320:    */   {
/* 321:392 */     Map<String, FieldAccess> combinedFields = getCombinedFieldsPropertyFirstForSerializer(clazz);
/* 322:394 */     if (combinedFields != null) {
/* 323:395 */       return combinedFields;
/* 324:    */     }
/* 325:398 */     Map<String, FieldAccess> fieldsFallbacks = null;
/* 326:    */     
/* 327:    */ 
/* 328:401 */     Map<String, FieldAccess> fieldsPrimary = null;
/* 329:    */     
/* 330:    */ 
/* 331:    */ 
/* 332:    */ 
/* 333:406 */     fieldsFallbacks = getAllAccessorFields(clazz, true);
/* 334:407 */     fieldsFallbacks = removeNonSerializable(fieldsFallbacks);
/* 335:    */     
/* 336:409 */     fieldsPrimary = getPropertyFieldAccessors(clazz);
/* 337:410 */     fieldsPrimary = removeNonSerializable(fieldsPrimary);
/* 338:    */     
/* 339:    */ 
/* 340:413 */     combineFieldMaps(fieldsFallbacks, fieldsPrimary);
/* 341:    */     
/* 342:415 */     combinedFields = fieldsPrimary;
/* 343:416 */     putCombinedFieldsPropertyFirstForSerializer(clazz, combinedFields);
/* 344:417 */     return combinedFields;
/* 345:    */   }
/* 346:    */   
/* 347:    */   private static Map<String, FieldAccess> removeNonSerializable(Map<String, FieldAccess> fieldAccessMap)
/* 348:    */   {
/* 349:423 */     LinkedHashMap<String, FieldAccess> map = new LinkedHashMap(fieldAccessMap);
/* 350:424 */     List<String> set = new ArrayList(fieldAccessMap.keySet());
/* 351:425 */     for (String key : set)
/* 352:    */     {
/* 353:426 */       FieldAccess fieldAccess = (FieldAccess)fieldAccessMap.get(key);
/* 354:427 */       if ((fieldAccess.isStatic()) || (fieldAccess.ignore())) {
/* 355:428 */         map.remove(key);
/* 356:    */       }
/* 357:    */     }
/* 358:431 */     return map;
/* 359:    */   }
/* 360:    */   
/* 361:    */   public static class ReflectionException
/* 362:    */     extends RuntimeException
/* 363:    */   {
/* 364:    */     public ReflectionException() {}
/* 365:    */     
/* 366:    */     public ReflectionException(String message, Throwable cause)
/* 367:    */     {
/* 368:442 */       super(cause);
/* 369:    */     }
/* 370:    */     
/* 371:    */     public ReflectionException(String message)
/* 372:    */     {
/* 373:446 */       super();
/* 374:    */     }
/* 375:    */     
/* 376:    */     public ReflectionException(Throwable cause)
/* 377:    */     {
/* 378:450 */       super();
/* 379:    */     }
/* 380:    */   }
/* 381:    */   
/* 382:    */   private static void handle(Exception ex)
/* 383:    */   {
/* 384:456 */     throw new ReflectionException(ex);
/* 385:    */   }
/* 386:    */   
/* 387:    */   public static Class<?> loadClass(String className)
/* 388:    */   {
/* 389:    */     try
/* 390:    */     {
/* 391:466 */       return Class.forName(className);
/* 392:    */     }
/* 393:    */     catch (Exception ex)
/* 394:    */     {
/* 395:473 */       log.info(String.format("Unable to create load class %s", new Object[] { className }));
/* 396:    */     }
/* 397:474 */     return null;
/* 398:    */   }
/* 399:    */   
/* 400:    */   public static Object newInstance(String className)
/* 401:    */   {
/* 402:    */     try
/* 403:    */     {
/* 404:480 */       Class<?> clazz = Class.forName(className);
/* 405:    */       
/* 406:    */ 
/* 407:483 */       return newInstance(clazz);
/* 408:    */     }
/* 409:    */     catch (Exception ex)
/* 410:    */     {
/* 411:487 */       log.info(String.format("Unable to create this class %s", new Object[] { className }));
/* 412:    */     }
/* 413:488 */     return null;
/* 414:    */   }
/* 415:    */   
/* 416:    */   public static <T> T newInstance(Class<T> clazz)
/* 417:    */   {
/* 418:493 */     T newInstance = null;
/* 419:494 */     ClassMeta<T> cls = ClassMeta.classMeta(clazz);
/* 420:    */     try
/* 421:    */     {
/* 422:498 */       ConstructorAccess<T> declaredConstructor = cls.noArgConstructor();
/* 423:499 */       if (declaredConstructor != null) {
/* 424:501 */         newInstance = declaredConstructor.create(new Object[0]);
/* 425:503 */       } else if (_useUnsafe) {
/* 426:504 */         newInstance = getUnsafe().allocateInstance(clazz);
/* 427:    */       } else {
/* 428:506 */         Exceptions.die(Boon.sputs(new Object[] { clazz.getName(), "does not have a no arg constructor and unsafe is not turned on" }));
/* 429:    */       }
/* 430:    */     }
/* 431:    */     catch (Exception ex)
/* 432:    */     {
/* 433:    */       try
/* 434:    */       {
/* 435:512 */         if (_useUnsafe) {
/* 436:513 */           return getUnsafe().allocateInstance(clazz);
/* 437:    */         }
/* 438:    */       }
/* 439:    */       catch (Exception ex2)
/* 440:    */       {
/* 441:517 */         handle(ex2);
/* 442:    */       }
/* 443:520 */       handle(ex);
/* 444:    */     }
/* 445:523 */     return newInstance;
/* 446:    */   }
/* 447:    */   
/* 448:    */   public static <T> T newInstance(Class<T> clazz, Object arg)
/* 449:    */   {
/* 450:528 */     T newInstance = null;
/* 451:    */     
/* 452:    */ 
/* 453:531 */     ClassMeta<T> cls = ClassMeta.classMeta(clazz);
/* 454:    */     try
/* 455:    */     {
/* 456:534 */       ConstructorAccess<T> declaredConstructor = cls.declaredConstructor(arg.getClass());
/* 457:535 */       if (declaredConstructor != null) {
/* 458:537 */         newInstance = declaredConstructor.create(new Object[] { arg });
/* 459:    */       }
/* 460:    */     }
/* 461:    */     catch (Exception ex)
/* 462:    */     {
/* 463:540 */       handle(ex);
/* 464:    */     }
/* 465:542 */     return newInstance;
/* 466:    */   }
/* 467:    */   
/* 468:    */   public static Class<?> getComponentType(Collection<?> collection, FieldAccess fieldAccess)
/* 469:    */   {
/* 470:546 */     Class<?> clz = fieldAccess.getComponentClass();
/* 471:547 */     if (clz == null) {
/* 472:548 */       clz = getComponentType(collection);
/* 473:    */     }
/* 474:550 */     return clz;
/* 475:    */   }
/* 476:    */   
/* 477:    */   public static Class<?> getComponentType(Collection<?> value)
/* 478:    */   {
/* 479:555 */     if (value.size() > 0)
/* 480:    */     {
/* 481:556 */       Object next = value.iterator().next();
/* 482:557 */       return next.getClass();
/* 483:    */     }
/* 484:559 */     return Typ.object;
/* 485:    */   }
/* 486:    */   
/* 487:    */   private static class FieldConverter
/* 488:    */     implements Function<Field, FieldAccess>
/* 489:    */   {
/* 490:    */     boolean thisUseUnsafe;
/* 491:    */     
/* 492:    */     FieldConverter(boolean useUnsafe)
/* 493:    */     {
/* 494:568 */       this.thisUseUnsafe = useUnsafe;
/* 495:    */     }
/* 496:    */     
/* 497:    */     public FieldAccess apply(Field from)
/* 498:    */     {
/* 499:573 */       if ((Reflection.useUnsafe) && (this.thisUseUnsafe)) {
/* 500:574 */         return UnsafeField.createUnsafeField(from);
/* 501:    */       }
/* 502:576 */       return new ReflectField(from);
/* 503:    */     }
/* 504:    */   }
/* 505:    */   
/* 506:    */   public static Map<String, FieldAccess> getAllAccessorFields(Class<? extends Object> theClass)
/* 507:    */   {
/* 508:583 */     return getAllAccessorFields(theClass, true);
/* 509:    */   }
/* 510:    */   
/* 511:    */   public static Map<String, FieldAccess> getAllAccessorFields(Class<? extends Object> theClass, boolean useUnsafe)
/* 512:    */   {
/* 513:588 */     Map<String, FieldAccess> map = getAccessorFieldsFromCache(theClass, useUnsafe);
/* 514:589 */     if (map == null)
/* 515:    */     {
/* 516:590 */       List<FieldAccess> list = Lists.mapBy(getAllFields(theClass), new FieldConverter(useUnsafe));
/* 517:591 */       map = new LinkedHashMap(list.size());
/* 518:592 */       for (FieldAccess fieldAccess : list) {
/* 519:593 */         map.put(fieldAccess.name(), fieldAccess);
/* 520:    */       }
/* 521:595 */       setAccessorFieldInCache(theClass, useUnsafe, map);
/* 522:    */     }
/* 523:597 */     return map;
/* 524:    */   }
/* 525:    */   
/* 526:    */   public static List<Field> getAllFields(Class<? extends Object> theClass)
/* 527:    */   {
/* 528:    */     try
/* 529:    */     {
/* 530:604 */       List<Field> list = getFields(theClass);
/* 531:605 */       while (theClass != Typ.object)
/* 532:    */       {
/* 533:607 */         theClass = theClass.getSuperclass();
/* 534:608 */         getFields(theClass, list);
/* 535:    */       }
/* 536:610 */       return list;
/* 537:    */     }
/* 538:    */     catch (Exception ex)
/* 539:    */     {
/* 540:612 */       return (List)Exceptions.handle(List.class, ex, new Object[] { "getAllFields the class", theClass });
/* 541:    */     }
/* 542:    */   }
/* 543:    */   
/* 544:    */   public static Map<String, FieldAccess> getPropertyFieldAccessors(Class<? extends Object> theClass)
/* 545:    */   {
/* 546:621 */     Map<String, FieldAccess> fields = getPropertyAccessorFieldsFromCache(theClass);
/* 547:622 */     if (fields == null)
/* 548:    */     {
/* 549:623 */       Map<String, Pair<Method, Method>> methods = getPropertySetterGetterMethods(theClass);
/* 550:    */       
/* 551:625 */       fields = new LinkedHashMap();
/* 552:628 */       for (Map.Entry<String, Pair<Method, Method>> entry : methods.entrySet())
/* 553:    */       {
/* 554:630 */         Pair<Method, Method> methodPair = (Pair)entry.getValue();
/* 555:631 */         String key = (String)entry.getKey();
/* 556:    */         
/* 557:633 */         PropertyField pf = new PropertyField(key, (Method)methodPair.getFirst(), (Method)methodPair.getSecond());
/* 558:    */         
/* 559:635 */         fields.put(pf.alias(), pf);
/* 560:    */       }
/* 561:639 */       setPropertyAccessorFieldsInCache(theClass, fields);
/* 562:    */     }
/* 563:643 */     return fields;
/* 564:    */   }
/* 565:    */   
/* 566:    */   public static Map<String, Pair<Method, Method>> getPropertySetterGetterMethods(Class<? extends Object> theClass)
/* 567:    */   {
/* 568:    */     try
/* 569:    */     {
/* 570:650 */       Method[] methods = theClass.getMethods();
/* 571:    */       
/* 572:652 */       Map<String, Pair<Method, Method>> methodMap = new LinkedHashMap(methods.length);
/* 573:653 */       List<Method> getterMethodList = new ArrayList(methods.length);
/* 574:655 */       for (int index = 0; index < methods.length; index++)
/* 575:    */       {
/* 576:656 */         Method method = methods[index];
/* 577:657 */         if (extractPropertyInfoFromMethodPair(methodMap, getterMethodList, method)) {}
/* 578:    */       }
/* 579:660 */       for (Method method : getterMethodList) {
/* 580:661 */         extractProperty(methodMap, method);
/* 581:    */       }
/* 582:664 */       return methodMap;
/* 583:    */     }
/* 584:    */     catch (Exception ex)
/* 585:    */     {
/* 586:666 */       ex.printStackTrace();
/* 587:667 */       return (Map)Exceptions.handle(Map.class, ex, new Object[] { theClass });
/* 588:    */     }
/* 589:    */   }
/* 590:    */   
/* 591:    */   private static boolean extractPropertyInfoFromMethodPair(Map<String, Pair<Method, Method>> methodMap, List<Method> getterMethodList, Method method)
/* 592:    */   {
/* 593:674 */     String name = method.getName();
/* 594:    */     try
/* 595:    */     {
/* 596:678 */       if ((method.getParameterTypes().length == 1) && (name.startsWith("set")))
/* 597:    */       {
/* 598:680 */         Pair<Method, Method> pair = new Pair();
/* 599:681 */         pair.setFirst(method);
/* 600:682 */         String propertyName = Str.slc(name, 3);
/* 601:    */         
/* 602:684 */         propertyName = Str.lower(Str.slc(propertyName, 0, 1)) + Str.slc(propertyName, 1);
/* 603:685 */         methodMap.put(propertyName, pair);
/* 604:    */       }
/* 605:688 */       if ((method.getParameterTypes().length > 0) || (method.getReturnType() == Void.TYPE) || ((!name.startsWith("get")) && (!name.startsWith("is"))) || (name.equals("getClass")) || (name.equals("get")) || (name.equals("is"))) {
/* 606:692 */         return true;
/* 607:    */       }
/* 608:694 */       getterMethodList.add(method);
/* 609:695 */       return false;
/* 610:    */     }
/* 611:    */     catch (Exception ex)
/* 612:    */     {
/* 613:698 */       return ((Boolean)Exceptions.handle(Boolean.class, ex, new Object[] { name, method })).booleanValue();
/* 614:    */     }
/* 615:    */   }
/* 616:    */   
/* 617:    */   private static void extractProperty(Map<String, Pair<Method, Method>> methodMap, Method method)
/* 618:    */   {
/* 619:    */     try
/* 620:    */     {
/* 621:705 */       String name = method.getName();
/* 622:706 */       String propertyName = null;
/* 623:707 */       if (name.startsWith("is")) {
/* 624:708 */         propertyName = name.substring(2);
/* 625:709 */       } else if (name.startsWith("get")) {
/* 626:710 */         propertyName = name.substring(3);
/* 627:    */       }
/* 628:713 */       propertyName = Str.lower(propertyName.substring(0, 1)) + propertyName.substring(1);
/* 629:    */       
/* 630:715 */       Pair<Method, Method> pair = (Pair)methodMap.get(propertyName);
/* 631:716 */       if (pair == null)
/* 632:    */       {
/* 633:717 */         pair = new Pair();
/* 634:718 */         methodMap.put(propertyName, pair);
/* 635:    */       }
/* 636:720 */       pair.setSecond(method);
/* 637:    */     }
/* 638:    */     catch (Exception ex)
/* 639:    */     {
/* 640:723 */       Exceptions.handle(ex, new Object[] { "extractProperty property extract of getPropertySetterGetterMethods", method });
/* 641:    */     }
/* 642:    */   }
/* 643:    */   
/* 644:    */   public static void getFields(Class<? extends Object> theClass, List<Field> list)
/* 645:    */   {
/* 646:    */     try
/* 647:    */     {
/* 648:731 */       List<Field> more = getFields(theClass);
/* 649:732 */       list.addAll(more);
/* 650:    */     }
/* 651:    */     catch (Exception ex)
/* 652:    */     {
/* 653:735 */       Exceptions.handle(ex, new Object[] { "getFields", theClass, list });
/* 654:    */     }
/* 655:    */   }
/* 656:    */   
/* 657:    */   public static boolean respondsTo(Class<?> type, String methodName)
/* 658:    */   {
/* 659:741 */     return ClassMeta.classMeta(type).respondsTo(methodName);
/* 660:    */   }
/* 661:    */   
/* 662:    */   public static boolean respondsTo(Class<?> type, String methodName, Class<?>... params)
/* 663:    */   {
/* 664:745 */     return ClassMeta.classMeta(type).respondsTo(methodName, params);
/* 665:    */   }
/* 666:    */   
/* 667:    */   public static boolean respondsTo(Class<?> type, String methodName, Object... params)
/* 668:    */   {
/* 669:750 */     return ClassMeta.classMeta(type).respondsTo(methodName, params);
/* 670:    */   }
/* 671:    */   
/* 672:    */   public static boolean respondsTo(Class<?> type, String methodName, List<?> params)
/* 673:    */   {
/* 674:755 */     return ClassMeta.classMeta(type).respondsTo(methodName, params);
/* 675:    */   }
/* 676:    */   
/* 677:    */   public static boolean respondsTo(Object object, String methodName)
/* 678:    */   {
/* 679:760 */     if ((object == null) || (methodName == null)) {
/* 680:761 */       return false;
/* 681:    */     }
/* 682:763 */     return ClassMeta.classMeta(object.getClass()).respondsTo(methodName);
/* 683:    */   }
/* 684:    */   
/* 685:    */   public static boolean respondsTo(Object object, String methodName, Class<?>... params)
/* 686:    */   {
/* 687:767 */     return ClassMeta.classMeta(object.getClass()).respondsTo(methodName, params);
/* 688:    */   }
/* 689:    */   
/* 690:    */   public static boolean respondsTo(Object object, String methodName, Object... params)
/* 691:    */   {
/* 692:772 */     return ClassMeta.classMeta(object.getClass()).respondsTo(methodName, params);
/* 693:    */   }
/* 694:    */   
/* 695:    */   public static boolean respondsTo(Object object, String methodName, List<?> params)
/* 696:    */   {
/* 697:777 */     return ClassMeta.classMeta(object.getClass()).respondsTo(methodName, params);
/* 698:    */   }
/* 699:    */   
/* 700:    */   public static boolean handles(Object object, Class<?> interfaceCls)
/* 701:    */   {
/* 702:782 */     return ClassMeta.classMeta(object.getClass()).handles(interfaceCls);
/* 703:    */   }
/* 704:    */   
/* 705:    */   public static boolean handles(Class cls, Class<?> interfaceCls)
/* 706:    */   {
/* 707:787 */     return ClassMeta.classMeta(cls).handles(interfaceCls);
/* 708:    */   }
/* 709:    */   
/* 710:    */   public static Object invoke(Object object, String name, Object... args)
/* 711:    */   {
/* 712:792 */     return ClassMeta.classMeta(object.getClass()).invokeUntyped(object, name, args);
/* 713:    */   }
/* 714:    */   
/* 715:    */   public static Object invoke(Object object, String name, List<?> args)
/* 716:    */   {
/* 717:797 */     return ClassMeta.classMeta(object.getClass()).invokeUntyped(object, name, args.toArray(new Object[args.size()]));
/* 718:    */   }
/* 719:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.reflection.Reflection
 * JD-Core Version:    0.7.0.1
 */