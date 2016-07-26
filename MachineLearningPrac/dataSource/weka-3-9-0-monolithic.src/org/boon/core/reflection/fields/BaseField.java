/*   1:    */ package org.boon.core.reflection.fields;
/*   2:    */ 
/*   3:    */ import java.lang.reflect.Field;
/*   4:    */ import java.lang.reflect.Method;
/*   5:    */ import java.lang.reflect.Modifier;
/*   6:    */ import java.lang.reflect.ParameterizedType;
/*   7:    */ import java.lang.reflect.Type;
/*   8:    */ import java.math.BigDecimal;
/*   9:    */ import java.math.BigInteger;
/*  10:    */ import java.util.BitSet;
/*  11:    */ import java.util.Collection;
/*  12:    */ import java.util.Collections;
/*  13:    */ import java.util.Currency;
/*  14:    */ import java.util.HashSet;
/*  15:    */ import java.util.Map;
/*  16:    */ import java.util.Set;
/*  17:    */ import java.util.concurrent.ConcurrentHashMap;
/*  18:    */ import org.boon.Boon;
/*  19:    */ import org.boon.Exceptions;
/*  20:    */ import org.boon.Sets;
/*  21:    */ import org.boon.Str;
/*  22:    */ import org.boon.core.Conversions;
/*  23:    */ import org.boon.core.TypeType;
/*  24:    */ import org.boon.core.Value;
/*  25:    */ import org.boon.core.reflection.AnnotationData;
/*  26:    */ import org.boon.core.reflection.Annotations;
/*  27:    */ import org.boon.core.value.ValueContainer;
/*  28:    */ 
/*  29:    */ public abstract class BaseField
/*  30:    */   implements FieldAccess
/*  31:    */ {
/*  32:    */   private static final int PRIMITIVE = 0;
/*  33:    */   private static final int FINAL = 1;
/*  34:    */   private static final int STATIC = 2;
/*  35:    */   private static final int VOLATILE = 3;
/*  36:    */   private static final int QUALIFIED = 4;
/*  37:    */   private static final int READ_ONLY = 5;
/*  38:    */   private static final int INCLUDE = 6;
/*  39:    */   private static final int IGNORE = 7;
/*  40:    */   private static final int WRITE_ONLY = 8;
/*  41:    */   private static final int HAS_INJECT = 9;
/*  42:    */   private static final int NAMED = 10;
/*  43:    */   private static final int REQUIRES_INJECTION = 11;
/*  44: 67 */   protected final BitSet bits = new BitSet();
/*  45:    */   protected final Class<?> type;
/*  46:    */   protected final Class<?> parentType;
/*  47:    */   protected final String name;
/*  48:    */   protected final ParameterizedType parameterizedType;
/*  49:    */   protected final Class<?> componentClass;
/*  50:    */   protected final String typeName;
/*  51:    */   public final TypeType typeEnum;
/*  52:    */   private final TypeType componentType;
/*  53: 76 */   private Map<String, Map<String, Object>> annotationMap = new ConcurrentHashMap();
/*  54:    */   private HashSet<String> includedViews;
/*  55:    */   private HashSet<String> ignoreWithViews;
/*  56:    */   private final String alias;
/*  57: 80 */   private static Set<String> annotationsThatHaveAliases = Sets.set(new String[] { "JsonProperty", "SerializedName", "Named", "id", "In", "Qualifier" });
/*  58:    */   
/*  59:    */   private void initAnnotationData(Class clazz)
/*  60:    */   {
/*  61: 86 */     Collection<AnnotationData> annotationDataForFieldAndProperty = Annotations.getAnnotationDataForFieldAndProperty(clazz, this.name, Collections.EMPTY_SET);
/*  62: 89 */     for (AnnotationData data : annotationDataForFieldAndProperty)
/*  63:    */     {
/*  64: 90 */       this.annotationMap.put(data.getSimpleClassName(), data.getValues());
/*  65: 91 */       this.annotationMap.put(data.getFullClassName(), data.getValues());
/*  66:    */     }
/*  67: 95 */     if (hasAnnotation("JsonViews"))
/*  68:    */     {
/*  69: 96 */       Map<String, Object> jsonViews = getAnnotationData("JsonViews");
/*  70: 97 */       String[] includeWithViews = (String[])jsonViews.get("includeWithViews");
/*  71: 98 */       String[] ignoreWithViews = (String[])jsonViews.get("ignoreWithViews");
/*  72:101 */       if (includeWithViews != null)
/*  73:    */       {
/*  74:102 */         this.includedViews = new HashSet();
/*  75:103 */         for (String view : includeWithViews) {
/*  76:104 */           this.includedViews.add(view);
/*  77:    */         }
/*  78:    */       }
/*  79:109 */       if (ignoreWithViews != null)
/*  80:    */       {
/*  81:110 */         this.ignoreWithViews = new HashSet();
/*  82:111 */         for (String view : ignoreWithViews) {
/*  83:112 */           this.ignoreWithViews.add(view);
/*  84:    */         }
/*  85:    */       }
/*  86:    */     }
/*  87:118 */     if (hasAnnotation("JsonIgnore"))
/*  88:    */     {
/*  89:119 */       Map<String, Object> jsonIgnore = getAnnotationData("JsonIgnore");
/*  90:120 */       boolean ignore = ((Boolean)jsonIgnore.get("value")).booleanValue();
/*  91:121 */       this.bits.set(7, ignore);
/*  92:    */     }
/*  93:124 */     if (hasAnnotation("JsonInclude"))
/*  94:    */     {
/*  95:125 */       Map<String, Object> jsonIgnore = getAnnotationData("JsonInclude");
/*  96:126 */       String include = (String)jsonIgnore.get("value");
/*  97:127 */       if (include.equals("ALWAYS")) {
/*  98:128 */         this.bits.set(6);
/*  99:    */       }
/* 100:    */     }
/* 101:132 */     if (hasAnnotation("Expose"))
/* 102:    */     {
/* 103:133 */       Map<String, Object> jsonIgnore = getAnnotationData("Expose");
/* 104:134 */       boolean serialize = ((Boolean)jsonIgnore.get("serialize")).booleanValue();
/* 105:135 */       this.bits.set(6, serialize);
/* 106:136 */       this.bits.set(7, !serialize);
/* 107:    */     }
/* 108:139 */     if ((hasAnnotation("Inject")) || (hasAnnotation("Autowired")) || (hasAnnotation("In"))) {
/* 109:140 */       this.bits.set(9);
/* 110:    */     }
/* 111:143 */     if (hasAnnotation("Autowired"))
/* 112:    */     {
/* 113:144 */       Map<String, Object> props = getAnnotationData("Autowired");
/* 114:    */       
/* 115:146 */       boolean required = ((Boolean)props.get("required")).booleanValue();
/* 116:147 */       if (required) {
/* 117:148 */         this.bits.set(11);
/* 118:    */       }
/* 119:    */     }
/* 120:152 */     if (hasAnnotation("In"))
/* 121:    */     {
/* 122:153 */       Map<String, Object> props = getAnnotationData("In");
/* 123:    */       
/* 124:155 */       boolean required = ((Boolean)props.get("required")).booleanValue();
/* 125:156 */       if (required) {
/* 126:157 */         this.bits.set(11);
/* 127:    */       }
/* 128:    */     }
/* 129:161 */     if (hasAnnotation("Required")) {
/* 130:162 */       this.bits.set(11);
/* 131:    */     }
/* 132:169 */     if (this.parentType != null)
/* 133:    */     {
/* 134:170 */       Map<String, AnnotationData> classAnnotations = Annotations.getAnnotationDataForClassAsMap(this.parentType);
/* 135:    */       
/* 136:    */ 
/* 137:173 */       AnnotationData jsonIgnoreProperties = (AnnotationData)classAnnotations.get("JsonIgnoreProperties");
/* 138:175 */       if (jsonIgnoreProperties != null)
/* 139:    */       {
/* 140:176 */         String[] props = (String[])jsonIgnoreProperties.getValues().get("value");
/* 141:177 */         if (props != null) {
/* 142:178 */           for (String prop : props) {
/* 143:179 */             if (prop.equals(this.name))
/* 144:    */             {
/* 145:180 */               this.bits.set(7, true);
/* 146:181 */               break;
/* 147:    */             }
/* 148:    */           }
/* 149:    */         }
/* 150:    */       }
/* 151:    */     }
/* 152:    */   }
/* 153:    */   
/* 154:    */   private String findAlias()
/* 155:    */   {
/* 156:193 */     String alias = null;
/* 157:194 */     for (String aliasAnnotation : annotationsThatHaveAliases)
/* 158:    */     {
/* 159:195 */       alias = getAlias(aliasAnnotation);
/* 160:196 */       if (!Str.isEmpty(alias))
/* 161:    */       {
/* 162:197 */         this.bits.set(10);
/* 163:198 */         break;
/* 164:    */       }
/* 165:    */     }
/* 166:202 */     return Str.isEmpty(alias) ? this.name : alias;
/* 167:    */   }
/* 168:    */   
/* 169:    */   private String getAlias(String annotationName)
/* 170:    */   {
/* 171:208 */     String alias = null;
/* 172:209 */     if (hasAnnotation(annotationName))
/* 173:    */     {
/* 174:211 */       Map<String, Object> aliasD = getAnnotationData(annotationName);
/* 175:212 */       alias = (String)aliasD.get("value");
/* 176:    */     }
/* 177:215 */     return alias;
/* 178:    */   }
/* 179:    */   
/* 180:    */   protected BaseField(String name, Method getter, Method setter)
/* 181:    */   {
/* 182:    */     try
/* 183:    */     {
/* 184:221 */       if (setter == null)
/* 185:    */       {
/* 186:222 */         this.bits.set(5);
/* 187:    */       }
/* 188:224 */       else if (getter == null)
/* 189:    */       {
/* 190:225 */         this.bits.set(8);
/* 191:226 */         this.bits.set(7);
/* 192:    */       }
/* 193:229 */       this.name = name.intern();
/* 194:    */       
/* 195:231 */       this.bits.set(3, false);
/* 196:232 */       this.bits.set(4, false);
/* 197:236 */       if (getter != null)
/* 198:    */       {
/* 199:237 */         this.bits.set(2, Modifier.isStatic(getter.getModifiers()));
/* 200:238 */         this.bits.set(1, Modifier.isFinal(getter.getModifiers()));
/* 201:    */         
/* 202:240 */         this.type = getter.getReturnType();
/* 203:241 */         this.parentType = getter.getDeclaringClass();
/* 204:242 */         this.bits.set(0, this.type.isPrimitive());
/* 205:243 */         this.typeName = this.type.getName().intern();
/* 206:244 */         Object obj = getter.getGenericReturnType();
/* 207:246 */         if ((obj instanceof ParameterizedType)) {
/* 208:248 */           this.parameterizedType = ((ParameterizedType)obj);
/* 209:    */         } else {
/* 210:250 */           this.parameterizedType = null;
/* 211:    */         }
/* 212:254 */         if (name.startsWith("$")) {
/* 213:255 */           this.typeEnum = TypeType.SYSTEM;
/* 214:    */         } else {
/* 215:257 */           this.typeEnum = TypeType.getType(this.type);
/* 216:    */         }
/* 217:260 */         if (this.typeEnum.isArray())
/* 218:    */         {
/* 219:261 */           this.componentClass = this.type.getComponentType();
/* 220:262 */           this.componentType = TypeType.getType(this.componentClass);
/* 221:    */         }
/* 222:263 */         else if (this.parameterizedType == null)
/* 223:    */         {
/* 224:264 */           this.componentClass = Object.class;
/* 225:265 */           this.componentType = TypeType.OBJECT;
/* 226:    */         }
/* 227:    */         else
/* 228:    */         {
/* 229:267 */           Object obj2 = this.parameterizedType.getActualTypeArguments()[0];
/* 230:268 */           if ((obj2 instanceof Class)) {
/* 231:269 */             this.componentClass = ((Class)this.parameterizedType.getActualTypeArguments()[0]);
/* 232:    */           } else {
/* 233:271 */             this.componentClass = Object.class;
/* 234:    */           }
/* 235:274 */           this.componentType = TypeType.getType(this.componentClass);
/* 236:    */         }
/* 237:278 */         getter.setAccessible(true);
/* 238:    */         
/* 239:280 */         initAnnotationData(getter.getDeclaringClass());
/* 240:    */       }
/* 241:    */       else
/* 242:    */       {
/* 243:283 */         this.bits.set(2, Modifier.isStatic(setter.getModifiers()));
/* 244:284 */         this.bits.set(1, Modifier.isFinal(setter.getModifiers()));
/* 245:285 */         this.type = setter.getParameterTypes()[0];
/* 246:287 */         if (name.startsWith("$")) {
/* 247:288 */           this.typeEnum = TypeType.SYSTEM;
/* 248:    */         } else {
/* 249:290 */           this.typeEnum = TypeType.getType(this.type);
/* 250:    */         }
/* 251:292 */         this.bits.set(0, this.type.isPrimitive());
/* 252:293 */         this.typeName = this.type.getName().intern();
/* 253:294 */         this.parameterizedType = null;
/* 254:295 */         this.componentClass = Object.class;
/* 255:296 */         this.componentType = TypeType.getType(this.componentClass);
/* 256:    */         
/* 257:298 */         this.parentType = setter.getDeclaringClass();
/* 258:    */         
/* 259:300 */         initAnnotationData(setter.getDeclaringClass());
/* 260:    */       }
/* 261:306 */       String alias = findAlias();
/* 262:    */       
/* 263:    */ 
/* 264:    */ 
/* 265:310 */       this.alias = (alias != null ? alias : name);
/* 266:    */     }
/* 267:    */     catch (Exception ex)
/* 268:    */     {
/* 269:313 */       Exceptions.handle("name " + name + " setter " + setter + " getter " + getter, ex);
/* 270:314 */       throw new RuntimeException("die");
/* 271:    */     }
/* 272:    */   }
/* 273:    */   
/* 274:    */   protected BaseField(Field field)
/* 275:    */   {
/* 276:323 */     this.name = field.getName().intern();
/* 277:    */     
/* 278:325 */     this.bits.set(2, Modifier.isStatic(field.getModifiers()));
/* 279:326 */     this.bits.set(1, Modifier.isFinal(field.getModifiers()));
/* 280:327 */     this.bits.set(3, Modifier.isVolatile(field.getModifiers()));
/* 281:328 */     this.bits.set(4, (this.bits.get(1)) || (this.bits.get(3)));
/* 282:329 */     this.bits.set(5, this.bits.get(1));
/* 283:330 */     this.bits.set(7, (Modifier.isTransient(field.getModifiers())) || (Modifier.isStatic(field.getModifiers())));
/* 284:    */     
/* 285:    */ 
/* 286:333 */     this.parentType = field.getDeclaringClass();
/* 287:    */     
/* 288:    */ 
/* 289:336 */     this.type = field.getType();
/* 290:337 */     this.typeName = this.type.getName().intern();
/* 291:    */     
/* 292:339 */     this.bits.set(0, this.type.isPrimitive());
/* 293:341 */     if (field != null)
/* 294:    */     {
/* 295:342 */       Object obj = field.getGenericType();
/* 296:344 */       if ((obj instanceof ParameterizedType)) {
/* 297:346 */         this.parameterizedType = ((ParameterizedType)obj);
/* 298:    */       } else {
/* 299:348 */         this.parameterizedType = null;
/* 300:    */       }
/* 301:    */     }
/* 302:    */     else
/* 303:    */     {
/* 304:352 */       this.parameterizedType = null;
/* 305:    */     }
/* 306:356 */     if (this.name.startsWith("$")) {
/* 307:357 */       this.typeEnum = TypeType.SYSTEM;
/* 308:    */     } else {
/* 309:359 */       this.typeEnum = TypeType.getType(this.type);
/* 310:    */     }
/* 311:362 */     if (this.typeEnum.isArray())
/* 312:    */     {
/* 313:364 */       this.componentClass = this.type.getComponentType();
/* 314:    */     }
/* 315:367 */     else if (this.parameterizedType == null)
/* 316:    */     {
/* 317:368 */       this.componentClass = Object.class;
/* 318:    */     }
/* 319:    */     else
/* 320:    */     {
/* 321:372 */       Type[] actualTypeArguments = this.parameterizedType.getActualTypeArguments();
/* 322:374 */       if (actualTypeArguments.length > 0)
/* 323:    */       {
/* 324:376 */         Object obj = this.parameterizedType.getActualTypeArguments()[0];
/* 325:377 */         if ((obj instanceof Class)) {
/* 326:378 */           this.componentClass = ((Class)this.parameterizedType.getActualTypeArguments()[0]);
/* 327:    */         } else {
/* 328:380 */           this.componentClass = Object.class;
/* 329:    */         }
/* 330:    */       }
/* 331:    */       else
/* 332:    */       {
/* 333:383 */         this.componentClass = Object.class;
/* 334:    */       }
/* 335:    */     }
/* 336:388 */     this.componentType = TypeType.getType(this.componentClass);
/* 337:    */     
/* 338:    */ 
/* 339:    */ 
/* 340:    */ 
/* 341:393 */     initAnnotationData(field.getDeclaringClass());
/* 342:    */     
/* 343:395 */     this.alias = findAlias();
/* 344:    */   }
/* 345:    */   
/* 346:    */   public final Object getValue(Object obj)
/* 347:    */   {
/* 348:405 */     switch (1.$SwitchMap$org$boon$core$TypeType[this.typeEnum.ordinal()])
/* 349:    */     {
/* 350:    */     case 1: 
/* 351:407 */       return Integer.valueOf(getInt(obj));
/* 352:    */     case 2: 
/* 353:409 */       return Long.valueOf(getLong(obj));
/* 354:    */     case 3: 
/* 355:411 */       return Boolean.valueOf(getBoolean(obj));
/* 356:    */     case 4: 
/* 357:413 */       return Byte.valueOf(getByte(obj));
/* 358:    */     case 5: 
/* 359:415 */       return Short.valueOf(getShort(obj));
/* 360:    */     case 6: 
/* 361:417 */       return Character.valueOf(getChar(obj));
/* 362:    */     case 7: 
/* 363:419 */       return Double.valueOf(getDouble(obj));
/* 364:    */     case 8: 
/* 365:421 */       return Float.valueOf(getFloat(obj));
/* 366:    */     }
/* 367:423 */     return getObject(obj);
/* 368:    */   }
/* 369:    */   
/* 370:    */   public final void setValue(Object obj, Object value)
/* 371:    */   {
/* 372:432 */     if ((isPrimitive()) && (value == null)) {
/* 373:433 */       return;
/* 374:    */     }
/* 375:436 */     switch (1.$SwitchMap$org$boon$core$TypeType[this.typeEnum.ordinal()])
/* 376:    */     {
/* 377:    */     case 1: 
/* 378:438 */       setInt(obj, Conversions.toInt(value));
/* 379:439 */       return;
/* 380:    */     case 2: 
/* 381:441 */       setLong(obj, Conversions.toLong(value));
/* 382:442 */       return;
/* 383:    */     case 3: 
/* 384:444 */       setBoolean(obj, Conversions.toBoolean(value));
/* 385:445 */       return;
/* 386:    */     case 4: 
/* 387:447 */       setByte(obj, Conversions.toByte(value));
/* 388:448 */       return;
/* 389:    */     case 5: 
/* 390:450 */       setShort(obj, Conversions.toShort(value));
/* 391:451 */       return;
/* 392:    */     case 6: 
/* 393:453 */       setChar(obj, Conversions.toChar(value));
/* 394:454 */       return;
/* 395:    */     case 7: 
/* 396:456 */       setDouble(obj, Conversions.toDouble(value));
/* 397:457 */       return;
/* 398:    */     case 8: 
/* 399:459 */       setFloat(obj, Conversions.toFloat(value));
/* 400:460 */       return;
/* 401:    */     case 9: 
/* 402:462 */       setObject(obj, Conversions.toDate(value));
/* 403:463 */       return;
/* 404:    */     case 10: 
/* 405:466 */       if ((value instanceof String)) {
/* 406:467 */         setObject(obj, value);
/* 407:    */       } else {
/* 408:469 */         setObject(obj, Conversions.toString(value));
/* 409:    */       }
/* 410:471 */       return;
/* 411:    */     case 11: 
/* 412:473 */       if (value.getClass() == this.type) {
/* 413:474 */         setObject(obj, value);
/* 414:    */       } else {
/* 415:476 */         setObject(obj, Conversions.toEnum(this.type, value));
/* 416:    */       }
/* 417:478 */       return;
/* 418:    */     case 12: 
/* 419:480 */       if ((value instanceof BigDecimal)) {
/* 420:481 */         setObject(obj, value);
/* 421:    */       } else {
/* 422:483 */         setObject(obj, Conversions.toBigDecimal(value));
/* 423:    */       }
/* 424:485 */       return;
/* 425:    */     case 13: 
/* 426:487 */       if ((value instanceof Number)) {
/* 427:488 */         setObject(obj, value);
/* 428:    */       } else {
/* 429:490 */         setObject(obj, Double.valueOf(Conversions.toDouble(value)));
/* 430:    */       }
/* 431:492 */       return;
/* 432:    */     case 14: 
/* 433:495 */       if ((value instanceof BigInteger)) {
/* 434:496 */         setObject(obj, value);
/* 435:    */       } else {
/* 436:498 */         setObject(obj, Conversions.toBigInteger(value));
/* 437:    */       }
/* 438:500 */       return;
/* 439:    */     case 15: 
/* 440:    */     case 16: 
/* 441:    */     case 17: 
/* 442:504 */       setObject(obj, Conversions.toCollection(this.type, value));
/* 443:505 */       return;
/* 444:    */     case 18: 
/* 445:507 */       if ((value instanceof Currency)) {
/* 446:508 */         setObject(obj, value);
/* 447:    */       } else {
/* 448:510 */         setObject(obj, Conversions.toCurrency(value));
/* 449:    */       }
/* 450:512 */       return;
/* 451:    */     }
/* 452:515 */     if (value == null)
/* 453:    */     {
/* 454:516 */       setObject(obj, null);
/* 455:517 */       return;
/* 456:    */     }
/* 457:520 */     if (value.getClass() == this.type)
/* 458:    */     {
/* 459:521 */       setObject(obj, value);
/* 460:522 */       return;
/* 461:    */     }
/* 462:523 */     if (this.type.isInstance(value))
/* 463:    */     {
/* 464:524 */       setObject(obj, value);
/* 465:525 */       return;
/* 466:    */     }
/* 467:528 */     Object object = Conversions.coerce(this.typeEnum, this.type, value);
/* 468:530 */     if (object == null)
/* 469:    */     {
/* 470:531 */       Exceptions.die(new Object[] { "Unable to convert", value, "to", this.typeEnum, this.type });
/* 471:532 */       return;
/* 472:    */     }
/* 473:535 */     if (object.getClass() == this.type)
/* 474:    */     {
/* 475:536 */       setObject(obj, object);
/* 476:537 */       return;
/* 477:    */     }
/* 478:538 */     if (this.type.isInstance(object))
/* 479:    */     {
/* 480:539 */       setObject(obj, object);
/* 481:540 */       return;
/* 482:    */     }
/* 483:541 */     if (object != null) {
/* 484:542 */       Exceptions.die(Boon.sputs(new Object[] { "Unable to set value into field after conversion was called", this, "converted value", object, "original value", value, "field", this, "converted object type", object.getClass() }));
/* 485:    */     }
/* 486:    */   }
/* 487:    */   
/* 488:    */   public final void setFromValue(Object obj, Value value)
/* 489:    */   {
/* 490:559 */     if (value == ValueContainer.NULL)
/* 491:    */     {
/* 492:560 */       setObject(obj, null);
/* 493:561 */       return;
/* 494:    */     }
/* 495:565 */     switch (1.$SwitchMap$org$boon$core$TypeType[this.typeEnum.ordinal()])
/* 496:    */     {
/* 497:    */     case 1: 
/* 498:567 */       setInt(obj, value.intValue());
/* 499:568 */       return;
/* 500:    */     case 2: 
/* 501:570 */       setLong(obj, value.longValue());
/* 502:571 */       return;
/* 503:    */     case 3: 
/* 504:573 */       setBoolean(obj, value.booleanValue());
/* 505:574 */       return;
/* 506:    */     case 4: 
/* 507:576 */       setByte(obj, value.byteValue());
/* 508:577 */       return;
/* 509:    */     case 5: 
/* 510:579 */       setShort(obj, value.shortValue());
/* 511:580 */       return;
/* 512:    */     case 6: 
/* 513:582 */       setChar(obj, value.charValue());
/* 514:583 */       return;
/* 515:    */     case 7: 
/* 516:585 */       setDouble(obj, value.doubleValue());
/* 517:586 */       return;
/* 518:    */     case 8: 
/* 519:588 */       setFloat(obj, value.floatValue());
/* 520:589 */       return;
/* 521:    */     case 19: 
/* 522:591 */       setObject(obj, Integer.valueOf(value.intValue()));
/* 523:592 */       return;
/* 524:    */     case 20: 
/* 525:594 */       setObject(obj, Long.valueOf(value.longValue()));
/* 526:595 */       return;
/* 527:    */     case 21: 
/* 528:597 */       setObject(obj, Boolean.valueOf(value.booleanValue()));
/* 529:598 */       return;
/* 530:    */     case 22: 
/* 531:600 */       setObject(obj, Byte.valueOf(value.byteValue()));
/* 532:601 */       return;
/* 533:    */     case 23: 
/* 534:603 */       setObject(obj, Short.valueOf(value.shortValue()));
/* 535:604 */       return;
/* 536:    */     case 24: 
/* 537:606 */       setObject(obj, Character.valueOf(value.charValue()));
/* 538:607 */       return;
/* 539:    */     case 25: 
/* 540:609 */       setObject(obj, Double.valueOf(value.doubleValue()));
/* 541:610 */       return;
/* 542:    */     case 26: 
/* 543:612 */       setObject(obj, Float.valueOf(value.floatValue()));
/* 544:613 */       return;
/* 545:    */     case 10: 
/* 546:    */     case 27: 
/* 547:616 */       setObject(obj, value.stringValue());
/* 548:617 */       return;
/* 549:    */     case 12: 
/* 550:619 */       setObject(obj, value.bigDecimalValue());
/* 551:620 */       return;
/* 552:    */     case 14: 
/* 553:622 */       setObject(obj, value.bigIntegerValue());
/* 554:623 */       return;
/* 555:    */     case 9: 
/* 556:625 */       setObject(obj, value.dateValue());
/* 557:626 */       return;
/* 558:    */     case 11: 
/* 559:632 */       setObject(obj, value.toEnum(this.type));
/* 560:633 */       return;
/* 561:    */     case 18: 
/* 562:635 */       setObject(obj, value.currencyValue());
/* 563:636 */       return;
/* 564:    */     }
/* 565:638 */     setValue(obj, value.toValue());
/* 566:    */   }
/* 567:    */   
/* 568:    */   public final ParameterizedType getParameterizedType()
/* 569:    */   {
/* 570:646 */     return this.parameterizedType;
/* 571:    */   }
/* 572:    */   
/* 573:    */   public final Class<?> getComponentClass()
/* 574:    */   {
/* 575:652 */     return this.componentClass;
/* 576:    */   }
/* 577:    */   
/* 578:    */   protected void analyzeError(Exception e, Object obj)
/* 579:    */   {
/* 580:658 */     Exceptions.handle(Str.lines(new String[] { e.getClass().getName(), String.format("cause %s", new Object[] { e.getCause() }), String.format("Field info name %s, type %s, class that declared field %s", new Object[] { name(), type(), getField().getDeclaringClass() }), String.format("TypeType of object passed %s", new Object[] { obj.getClass().getName() }) }), e);
/* 581:    */   }
/* 582:    */   
/* 583:    */   public final boolean hasAnnotation(String annotationName)
/* 584:    */   {
/* 585:671 */     return this.annotationMap.containsKey(annotationName);
/* 586:    */   }
/* 587:    */   
/* 588:    */   public final Map<String, Object> getAnnotationData(String annotationName)
/* 589:    */   {
/* 590:677 */     return (Map)this.annotationMap.get(annotationName);
/* 591:    */   }
/* 592:    */   
/* 593:    */   public final boolean isPrimitive()
/* 594:    */   {
/* 595:682 */     return this.bits.get(0);
/* 596:    */   }
/* 597:    */   
/* 598:    */   public final TypeType typeEnum()
/* 599:    */   {
/* 600:692 */     return this.typeEnum;
/* 601:    */   }
/* 602:    */   
/* 603:    */   public final boolean isFinal()
/* 604:    */   {
/* 605:699 */     return this.bits.get(1);
/* 606:    */   }
/* 607:    */   
/* 608:    */   public final boolean isStatic()
/* 609:    */   {
/* 610:707 */     return this.bits.get(2);
/* 611:    */   }
/* 612:    */   
/* 613:    */   public final boolean isVolatile()
/* 614:    */   {
/* 615:712 */     return this.bits.get(3);
/* 616:    */   }
/* 617:    */   
/* 618:    */   public final boolean isQualified()
/* 619:    */   {
/* 620:718 */     return this.bits.get(4);
/* 621:    */   }
/* 622:    */   
/* 623:    */   public final boolean isReadOnly()
/* 624:    */   {
/* 625:723 */     return this.bits.get(5);
/* 626:    */   }
/* 627:    */   
/* 628:    */   public boolean isWriteOnly()
/* 629:    */   {
/* 630:729 */     return this.bits.get(8);
/* 631:    */   }
/* 632:    */   
/* 633:    */   public final Class<?> type()
/* 634:    */   {
/* 635:735 */     return this.type;
/* 636:    */   }
/* 637:    */   
/* 638:    */   public final String name()
/* 639:    */   {
/* 640:740 */     return this.name;
/* 641:    */   }
/* 642:    */   
/* 643:    */   public final String alias()
/* 644:    */   {
/* 645:747 */     return this.alias;
/* 646:    */   }
/* 647:    */   
/* 648:    */   public String toString()
/* 649:    */   {
/* 650:752 */     return "FieldInfo [name=" + this.name + ", type=" + this.type + ", parentType=" + this.parentType + "]";
/* 651:    */   }
/* 652:    */   
/* 653:    */   public final boolean isViewActive(String activeView)
/* 654:    */   {
/* 655:764 */     if ((this.includedViews == null) && (this.ignoreWithViews == null)) {
/* 656:765 */       return true;
/* 657:    */     }
/* 658:767 */     if (this.includedViews != null)
/* 659:    */     {
/* 660:768 */       if (this.includedViews.contains(activeView)) {
/* 661:769 */         return true;
/* 662:    */       }
/* 663:771 */       return false;
/* 664:    */     }
/* 665:774 */     if (this.ignoreWithViews != null)
/* 666:    */     {
/* 667:775 */       if (this.ignoreWithViews.contains(activeView)) {
/* 668:776 */         return false;
/* 669:    */       }
/* 670:778 */       return true;
/* 671:    */     }
/* 672:781 */     return true;
/* 673:    */   }
/* 674:    */   
/* 675:    */   public final boolean include()
/* 676:    */   {
/* 677:786 */     return this.bits.get(6);
/* 678:    */   }
/* 679:    */   
/* 680:    */   public final boolean ignore()
/* 681:    */   {
/* 682:791 */     return this.bits.get(7);
/* 683:    */   }
/* 684:    */   
/* 685:    */   public boolean injectable()
/* 686:    */   {
/* 687:797 */     return this.bits.get(9);
/* 688:    */   }
/* 689:    */   
/* 690:    */   public boolean requiresInjection()
/* 691:    */   {
/* 692:803 */     return this.bits.get(11);
/* 693:    */   }
/* 694:    */   
/* 695:    */   public boolean isNamed()
/* 696:    */   {
/* 697:809 */     return this.bits.get(10);
/* 698:    */   }
/* 699:    */   
/* 700:    */   public boolean hasAlias()
/* 701:    */   {
/* 702:814 */     return this.bits.get(10);
/* 703:    */   }
/* 704:    */   
/* 705:    */   public String named()
/* 706:    */   {
/* 707:821 */     return this.alias;
/* 708:    */   }
/* 709:    */   
/* 710:    */   public Object parent()
/* 711:    */   {
/* 712:828 */     return this.parentType;
/* 713:    */   }
/* 714:    */   
/* 715:    */   public Class<?> declaringParent()
/* 716:    */   {
/* 717:834 */     return this.parentType;
/* 718:    */   }
/* 719:    */   
/* 720:    */   public TypeType componentType()
/* 721:    */   {
/* 722:840 */     return this.componentType;
/* 723:    */   }
/* 724:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.reflection.fields.BaseField
 * JD-Core Version:    0.7.0.1
 */