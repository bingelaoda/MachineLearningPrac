/*   1:    */ package org.boon.core.reflection.impl;
/*   2:    */ 
/*   3:    */ import java.lang.invoke.ConstantCallSite;
/*   4:    */ import java.lang.invoke.MethodHandle;
/*   5:    */ import java.lang.invoke.MethodHandles;
/*   6:    */ import java.lang.invoke.MethodHandles.Lookup;
/*   7:    */ import java.lang.invoke.MethodType;
/*   8:    */ import java.lang.reflect.Method;
/*   9:    */ import java.lang.reflect.Modifier;
/*  10:    */ import java.lang.reflect.Type;
/*  11:    */ import java.util.ArrayList;
/*  12:    */ import java.util.Iterator;
/*  13:    */ import java.util.List;
/*  14:    */ import java.util.Map;
/*  15:    */ import java.util.concurrent.ConcurrentHashMap;
/*  16:    */ import org.boon.Exceptions;
/*  17:    */ import org.boon.Lists;
/*  18:    */ import org.boon.core.Conversions;
/*  19:    */ import org.boon.core.TypeType;
/*  20:    */ import org.boon.core.reflection.AnnotationData;
/*  21:    */ import org.boon.core.reflection.Annotations;
/*  22:    */ import org.boon.core.reflection.Invoker;
/*  23:    */ import org.boon.core.reflection.MethodAccess;
/*  24:    */ import org.boon.primitive.Arry;
/*  25:    */ 
/*  26:    */ public class MethodAccessImpl
/*  27:    */   implements MethodAccess
/*  28:    */ {
/*  29:    */   public final Method method;
/*  30:    */   final List<AnnotationData> annotationData;
/*  31:    */   final List<List<AnnotationData>> annotationDataForParams;
/*  32:    */   final Map<String, AnnotationData> annotationMap;
/*  33: 65 */   final List<TypeType> paramTypeEnumList = new ArrayList();
/*  34: 68 */   final MethodHandles.Lookup lookup = MethodHandles.lookup();
/*  35:    */   MethodHandle methodHandle;
/*  36:    */   Object instance;
/*  37:    */   private int score;
/*  38:    */   
/*  39:    */   public MethodAccessImpl()
/*  40:    */   {
/*  41: 75 */     this.method = null;
/*  42: 76 */     this.annotationData = null;
/*  43: 77 */     this.annotationMap = null;
/*  44: 78 */     this.methodHandle = null;
/*  45: 79 */     this.annotationDataForParams = null;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public List<List<AnnotationData>> annotationDataForParams()
/*  49:    */   {
/*  50: 83 */     return this.annotationDataForParams;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public MethodAccessImpl(Method method)
/*  54:    */   {
/*  55: 88 */     this.method = method;
/*  56: 89 */     this.method.setAccessible(true);
/*  57: 90 */     this.annotationData = Annotations.getAnnotationDataForMethod(method);
/*  58: 91 */     this.annotationDataForParams = Annotations.getAnnotationDataForMethodParams(method);
/*  59: 95 */     for (Class<?> cls : method.getParameterTypes()) {
/*  60: 96 */       this.paramTypeEnumList.add(TypeType.getType(cls));
/*  61:    */     }
/*  62:101 */     this.annotationMap = new ConcurrentHashMap();
/*  63:102 */     for (AnnotationData data : this.annotationData)
/*  64:    */     {
/*  65:103 */       this.annotationMap.put(data.getName(), data);
/*  66:104 */       this.annotationMap.put(data.getSimpleClassName(), data);
/*  67:105 */       this.annotationMap.put(data.getFullClassName(), data);
/*  68:    */     }
/*  69:108 */     score(method);
/*  70:    */   }
/*  71:    */   
/*  72:    */   private void score(Method method)
/*  73:    */   {
/*  74:113 */     Class<?>[] parameterTypes = method.getParameterTypes();
/*  75:114 */     int index = 0;
/*  76:116 */     for (Class<?> paramType : parameterTypes) {
/*  77:119 */       if (paramType.isPrimitive())
/*  78:    */       {
/*  79:120 */         this.score += 100;
/*  80:    */       }
/*  81:    */       else
/*  82:    */       {
/*  83:123 */         TypeType type = (TypeType)this.paramTypeEnumList.get(index);
/*  84:125 */         switch (3.$SwitchMap$org$boon$core$TypeType[type.ordinal()])
/*  85:    */         {
/*  86:    */         case 1: 
/*  87:128 */           this.score += 85;
/*  88:129 */           break;
/*  89:    */         case 2: 
/*  90:132 */           this.score += 75;
/*  91:133 */           break;
/*  92:    */         case 3: 
/*  93:    */         case 4: 
/*  94:137 */           this.score += 65;
/*  95:138 */           break;
/*  96:    */         case 5: 
/*  97:141 */           this.score += 60;
/*  98:142 */           break;
/*  99:    */         case 6: 
/* 100:146 */           this.score += 55;
/* 101:147 */           break;
/* 102:    */         case 7: 
/* 103:150 */           this.score += 50;
/* 104:151 */           break;
/* 105:    */         case 8: 
/* 106:154 */           this.score += 45;
/* 107:155 */           break;
/* 108:    */         case 9: 
/* 109:159 */           this.score += 40;
/* 110:160 */           break;
/* 111:    */         case 10: 
/* 112:163 */           this.score += 30;
/* 113:164 */           break;
/* 114:    */         case 11: 
/* 115:168 */           this.score += 25;
/* 116:    */         }
/* 117:174 */         index++;
/* 118:    */       }
/* 119:    */     }
/* 120:177 */     if (method.isVarArgs()) {
/* 121:178 */       this.score += -10000;
/* 122:    */     }
/* 123:    */   }
/* 124:    */   
/* 125:    */   public Object invokeDynamicList(Object object, List<?> args)
/* 126:    */   {
/* 127:187 */     return invokeDynamic(object, Arry.objectArray(args));
/* 128:    */   }
/* 129:    */   
/* 130:    */   public Object invokeDynamicObject(Object object, Object args)
/* 131:    */   {
/* 132:192 */     if ((args instanceof List)) {
/* 133:193 */       return invokeDynamicList(object, (List)args);
/* 134:    */     }
/* 135:195 */     return invokeDynamic(object, new Object[] { args });
/* 136:    */   }
/* 137:    */   
/* 138:    */   public Object invokeDynamic(Object object, Object... args)
/* 139:    */   {
/* 140:202 */     Class<?>[] parameterTypes = parameterTypes();
/* 141:203 */     int paramLength = parameterTypes.length;
/* 142:204 */     int argsLength = args.length;
/* 143:208 */     if (paramLength == 0) {
/* 144:209 */       return invoke(object, new Object[0]);
/* 145:    */     }
/* 146:213 */     if (paramLength == argsLength)
/* 147:    */     {
/* 148:215 */       if (argsLength == 1)
/* 149:    */       {
/* 150:217 */         Object arg = args[0];
/* 151:218 */         Class<?> paramType = parameterTypes[0];
/* 152:219 */         if (!paramType.isInstance(arg))
/* 153:    */         {
/* 154:220 */           TypeType type = (TypeType)this.paramTypeEnumList.get(0);
/* 155:221 */           arg = Conversions.coerce(type, paramType, arg);
/* 156:    */         }
/* 157:224 */         return invoke(object, new Object[] { arg });
/* 158:    */       }
/* 159:230 */       Object[] newArgs = new Object[argsLength];
/* 160:232 */       for (int index = 0; index < argsLength; index++)
/* 161:    */       {
/* 162:234 */         Object arg = args[index];
/* 163:235 */         Class<?> paramType = parameterTypes[index];
/* 164:237 */         if (!paramType.isInstance(arg))
/* 165:    */         {
/* 166:238 */           TypeType type = (TypeType)this.paramTypeEnumList.get(index);
/* 167:239 */           newArgs[index] = Conversions.coerce(type, paramType, arg);
/* 168:    */         }
/* 169:    */         else
/* 170:    */         {
/* 171:241 */           newArgs[index] = arg;
/* 172:    */         }
/* 173:    */       }
/* 174:247 */       return invoke(object, newArgs);
/* 175:    */     }
/* 176:251 */     if ((this.method.isVarArgs()) && (paramLength == 1)) {
/* 177:253 */       return invoke(object, new Object[] { args });
/* 178:    */     }
/* 179:255 */     return Invoker.invokeOverloadedFromList(object, name(), Lists.list(args));
/* 180:    */   }
/* 181:    */   
/* 182:    */   public Object invoke(Object object, Object... args)
/* 183:    */   {
/* 184:    */     try
/* 185:    */     {
/* 186:265 */       return this.method.invoke(object, args);
/* 187:    */     }
/* 188:    */     catch (Throwable ex)
/* 189:    */     {
/* 190:268 */       return Exceptions.handle(Object.class, ex, new Object[] { "unable to invoke method", this.method, " on object ", object, "with arguments", args, "\nparameter types", parameterTypes(), "\nargument types are", args });
/* 191:    */     }
/* 192:    */   }
/* 193:    */   
/* 194:    */   public Object invokeBound(Object... args)
/* 195:    */   {
/* 196:    */     try
/* 197:    */     {
/* 198:278 */       return this.method.invoke(this.instance, args);
/* 199:    */     }
/* 200:    */     catch (Throwable ex)
/* 201:    */     {
/* 202:281 */       return Exceptions.handle(Object.class, ex, new Object[] { "unable to invoke method", this.method, " on object with arguments", args, "\nparameter types", parameterTypes(), "\nargument types are" });
/* 203:    */     }
/* 204:    */   }
/* 205:    */   
/* 206:    */   public Object invokeStatic(Object... args)
/* 207:    */   {
/* 208:    */     try
/* 209:    */     {
/* 210:292 */       return this.method.invoke(null, args);
/* 211:    */     }
/* 212:    */     catch (Throwable ex)
/* 213:    */     {
/* 214:294 */       return Exceptions.handle(Object.class, ex, new Object[] { "unable to invoke method", this.method, " with arguments", args });
/* 215:    */     }
/* 216:    */   }
/* 217:    */   
/* 218:    */   public MethodAccess bind(Object instance)
/* 219:    */   {
/* 220:302 */     Exceptions.die("Bind does not work for cached methodAccess make a copy with methodAccsess() first");
/* 221:303 */     return null;
/* 222:    */   }
/* 223:    */   
/* 224:    */   public MethodHandle methodHandle()
/* 225:    */   {
/* 226:    */     MethodHandle m;
/* 227:    */     try
/* 228:    */     {
/* 229:312 */       m = this.lookup.unreflect(this.method);
/* 230:    */     }
/* 231:    */     catch (Exception e)
/* 232:    */     {
/* 233:315 */       m = null;
/* 234:316 */       Exceptions.handle(e);
/* 235:    */     }
/* 236:319 */     return m;
/* 237:    */   }
/* 238:    */   
/* 239:    */   public MethodAccess methodAccess()
/* 240:    */   {
/* 241:324 */     if (this.methodHandle == null) {
/* 242:325 */       this.methodHandle = methodHandle();
/* 243:    */     }
/* 244:327 */     new MethodAccessImpl(this.method)
/* 245:    */     {
/* 246:    */       public MethodAccess bind(Object instance)
/* 247:    */       {
/* 248:332 */         this.methodHandle.bindTo(instance);
/* 249:333 */         this.instance = instance;
/* 250:334 */         return this;
/* 251:    */       }
/* 252:    */       
/* 253:    */       public Object bound()
/* 254:    */       {
/* 255:340 */         return this.instance;
/* 256:    */       }
/* 257:    */     };
/* 258:    */   }
/* 259:    */   
/* 260:    */   public Object bound()
/* 261:    */   {
/* 262:349 */     return null;
/* 263:    */   }
/* 264:    */   
/* 265:    */   public <T> ConstantCallSite invokeReducerLongIntReturnLongMethodHandle(T object)
/* 266:    */   {
/* 267:355 */     MethodType methodType = MethodType.methodType(Long.TYPE, Long.TYPE, new Class[] { Integer.TYPE });
/* 268:    */     try
/* 269:    */     {
/* 270:357 */       return new ConstantCallSite(this.lookup.bind(object, name(), methodType));
/* 271:    */     }
/* 272:    */     catch (NoSuchMethodException e)
/* 273:    */     {
/* 274:359 */       Exceptions.handle(e, new Object[] { "Method not found", name() });
/* 275:    */     }
/* 276:    */     catch (IllegalAccessException e)
/* 277:    */     {
/* 278:361 */       Exceptions.handle(e, new Object[] { "Illegal access to method", name() });
/* 279:    */     }
/* 280:363 */     return null;
/* 281:    */   }
/* 282:    */   
/* 283:    */   public Method method()
/* 284:    */   {
/* 285:368 */     return this.method;
/* 286:    */   }
/* 287:    */   
/* 288:    */   public int score()
/* 289:    */   {
/* 290:373 */     return this.score;
/* 291:    */   }
/* 292:    */   
/* 293:    */   public Iterable<AnnotationData> annotationData()
/* 294:    */   {
/* 295:379 */     new Iterable()
/* 296:    */     {
/* 297:    */       public Iterator<AnnotationData> iterator()
/* 298:    */       {
/* 299:382 */         return MethodAccessImpl.this.annotationData.iterator();
/* 300:    */       }
/* 301:    */     };
/* 302:    */   }
/* 303:    */   
/* 304:    */   public boolean hasAnnotation(String annotationName)
/* 305:    */   {
/* 306:389 */     return this.annotationMap.containsKey(annotationName);
/* 307:    */   }
/* 308:    */   
/* 309:    */   public AnnotationData annotation(String annotationName)
/* 310:    */   {
/* 311:394 */     return (AnnotationData)this.annotationMap.get(annotationName);
/* 312:    */   }
/* 313:    */   
/* 314:    */   public boolean isStatic()
/* 315:    */   {
/* 316:399 */     return Modifier.isStatic(this.method.getModifiers());
/* 317:    */   }
/* 318:    */   
/* 319:    */   public boolean isPublic()
/* 320:    */   {
/* 321:405 */     return Modifier.isPublic(this.method.getModifiers());
/* 322:    */   }
/* 323:    */   
/* 324:    */   public boolean isPrivate()
/* 325:    */   {
/* 326:411 */     return Modifier.isPrivate(this.method.getModifiers());
/* 327:    */   }
/* 328:    */   
/* 329:    */   public String name()
/* 330:    */   {
/* 331:416 */     return this.method.getName();
/* 332:    */   }
/* 333:    */   
/* 334:    */   public Class<?> declaringType()
/* 335:    */   {
/* 336:421 */     return this.method.getDeclaringClass();
/* 337:    */   }
/* 338:    */   
/* 339:    */   public Class<?> returnType()
/* 340:    */   {
/* 341:426 */     return this.method.getReturnType();
/* 342:    */   }
/* 343:    */   
/* 344:    */   public boolean respondsTo(Class<?>[] parametersToMatch)
/* 345:    */   {
/* 346:431 */     boolean match = true;
/* 347:    */     
/* 348:433 */     Class<?>[] parameterTypes = this.method.getParameterTypes();
/* 349:436 */     if (parameterTypes.length != parametersToMatch.length) {
/* 350:437 */       return false;
/* 351:    */     }
/* 352:441 */     for (int index = 0; index < parameterTypes.length; index++)
/* 353:    */     {
/* 354:442 */       Class<?> type = parameterTypes[index];
/* 355:443 */       Class<?> matchToType = parametersToMatch[index];
/* 356:444 */       if (type.isPrimitive())
/* 357:    */       {
/* 358:446 */         if (((type != Integer.TYPE) || ((matchToType != Integer.class) && (matchToType != Integer.TYPE))) && ((type != Boolean.TYPE) || ((matchToType != Boolean.class) && (matchToType != Boolean.TYPE))) && ((type != Long.TYPE) || ((matchToType != Long.class) && (matchToType != Long.TYPE))) && ((type != Float.TYPE) || ((matchToType != Float.class) && (matchToType != Float.TYPE))) && ((type != Double.TYPE) || ((matchToType != Double.class) && (matchToType != Double.TYPE))) && ((type != Short.TYPE) || ((matchToType != Short.class) && (matchToType != Short.TYPE))) && ((type != Byte.TYPE) || ((matchToType != Byte.class) && (matchToType != Byte.TYPE))) && ((type != Character.TYPE) || ((matchToType != Character.class) && (matchToType != Character.TYPE))))
/* 359:    */         {
/* 360:456 */           match = false;
/* 361:457 */           break;
/* 362:    */         }
/* 363:    */       }
/* 364:461 */       else if (!type.isAssignableFrom(matchToType))
/* 365:    */       {
/* 366:462 */         match = false;
/* 367:463 */         break;
/* 368:    */       }
/* 369:    */     }
/* 370:467 */     return match;
/* 371:    */   }
/* 372:    */   
/* 373:    */   public boolean respondsTo(Object... args)
/* 374:    */   {
/* 375:475 */     boolean match = true;
/* 376:476 */     Class<?>[] parameterTypes = this.method.getParameterTypes();
/* 377:480 */     if (parameterTypes.length != args.length) {
/* 378:481 */       return false;
/* 379:    */     }
/* 380:484 */     for (int index = 0; index < parameterTypes.length; index++)
/* 381:    */     {
/* 382:485 */       Object arg = args[index];
/* 383:486 */       Class<?> type = parameterTypes[index];
/* 384:487 */       Class<?> matchToType = arg != null ? arg.getClass() : null;
/* 385:489 */       if (type.isPrimitive())
/* 386:    */       {
/* 387:491 */         if (arg == null)
/* 388:    */         {
/* 389:492 */           match = false;
/* 390:493 */           break;
/* 391:    */         }
/* 392:495 */         if (((type != Integer.TYPE) || (matchToType != Integer.class)) && ((type != Boolean.TYPE) || (matchToType != Boolean.class)) && ((type != Long.TYPE) || (matchToType != Long.class)) && ((type != Float.TYPE) || (matchToType != Float.class)) && ((type != Double.TYPE) || (matchToType != Double.class)) && ((type != Short.TYPE) || (matchToType != Short.class)) && ((type != Byte.TYPE) || (matchToType != Byte.class)) && ((type != Character.TYPE) || (matchToType != Character.class)))
/* 393:    */         {
/* 394:505 */           match = false;
/* 395:506 */           break;
/* 396:    */         }
/* 397:    */       }
/* 398:510 */       else if (arg != null)
/* 399:    */       {
/* 400:512 */         if (!type.isInstance(arg))
/* 401:    */         {
/* 402:513 */           match = false;
/* 403:514 */           break;
/* 404:    */         }
/* 405:    */       }
/* 406:    */     }
/* 407:518 */     return match;
/* 408:    */   }
/* 409:    */   
/* 410:    */   public Class<?>[] parameterTypes()
/* 411:    */   {
/* 412:524 */     return this.method.getParameterTypes();
/* 413:    */   }
/* 414:    */   
/* 415:    */   public Type[] getGenericParameterTypes()
/* 416:    */   {
/* 417:529 */     return this.method.getGenericParameterTypes();
/* 418:    */   }
/* 419:    */   
/* 420:    */   public String toString()
/* 421:    */   {
/* 422:535 */     return "MethodAccessImpl{method=" + this.method + ", annotationData=" + this.annotationData + ", instance=" + this.instance + '}';
/* 423:    */   }
/* 424:    */   
/* 425:    */   public boolean equals(Object o)
/* 426:    */   {
/* 427:544 */     if (this == o) {
/* 428:544 */       return true;
/* 429:    */     }
/* 430:545 */     if ((o == null) || (getClass() != o.getClass())) {
/* 431:545 */       return false;
/* 432:    */     }
/* 433:547 */     MethodAccessImpl that = (MethodAccessImpl)o;
/* 434:549 */     if (this.annotationData != null ? !this.annotationData.equals(that.annotationData) : that.annotationData != null) {
/* 435:550 */       return false;
/* 436:    */     }
/* 437:551 */     if (this.annotationMap != null ? !this.annotationMap.equals(that.annotationMap) : that.annotationMap != null) {
/* 438:552 */       return false;
/* 439:    */     }
/* 440:553 */     if (this.instance != null ? !this.instance.equals(that.instance) : that.instance != null) {
/* 441:553 */       return false;
/* 442:    */     }
/* 443:554 */     if (this.method != null ? !this.method.equals(that.method) : that.method != null) {
/* 444:554 */       return false;
/* 445:    */     }
/* 446:556 */     return true;
/* 447:    */   }
/* 448:    */   
/* 449:    */   public int hashCode()
/* 450:    */   {
/* 451:561 */     int result = this.method != null ? this.method.hashCode() : 0;
/* 452:562 */     result = 31 * result + (this.annotationData != null ? this.annotationData.hashCode() : 0);
/* 453:563 */     result = 31 * result + (this.annotationMap != null ? this.annotationMap.hashCode() : 0);
/* 454:564 */     result = 31 * result + (this.instance != null ? this.instance.hashCode() : 0);
/* 455:565 */     return result;
/* 456:    */   }
/* 457:    */   
/* 458:    */   public int compareTo(MethodAccess o2)
/* 459:    */   {
/* 460:571 */     if (score() > o2.score()) {
/* 461:572 */       return -1;
/* 462:    */     }
/* 463:573 */     if (score() < o2.score()) {
/* 464:574 */       return 1;
/* 465:    */     }
/* 466:576 */     return 0;
/* 467:    */   }
/* 468:    */   
/* 469:    */   public List<TypeType> paramTypeEnumList()
/* 470:    */   {
/* 471:582 */     return this.paramTypeEnumList;
/* 472:    */   }
/* 473:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.reflection.impl.MethodAccessImpl
 * JD-Core Version:    0.7.0.1
 */