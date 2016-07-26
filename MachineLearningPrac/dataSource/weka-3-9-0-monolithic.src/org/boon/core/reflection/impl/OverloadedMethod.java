/*   1:    */ package org.boon.core.reflection.impl;
/*   2:    */ 
/*   3:    */ import java.lang.invoke.ConstantCallSite;
/*   4:    */ import java.lang.invoke.MethodHandle;
/*   5:    */ import java.lang.reflect.Method;
/*   6:    */ import java.lang.reflect.Type;
/*   7:    */ import java.util.ArrayList;
/*   8:    */ import java.util.Collections;
/*   9:    */ import java.util.List;
/*  10:    */ import org.boon.Exceptions;
/*  11:    */ import org.boon.core.TypeType;
/*  12:    */ import org.boon.core.reflection.AnnotationData;
/*  13:    */ import org.boon.core.reflection.MethodAccess;
/*  14:    */ import org.boon.primitive.Arry;
/*  15:    */ 
/*  16:    */ public class OverloadedMethod
/*  17:    */   implements MethodAccess
/*  18:    */ {
/*  19:    */   List<MethodAccess> methodAccessList;
/*  20:    */   List<List<MethodAccess>> methodAccessListByArgNumber;
/*  21:    */   List<List<MethodAccess>> methodAccessListByArgNumberWithVarArg;
/*  22:    */   private boolean lock;
/*  23:    */   
/*  24:    */   public OverloadedMethod()
/*  25:    */   {
/*  26: 23 */     this.methodAccessList = new ArrayList();
/*  27:    */     
/*  28: 25 */     this.methodAccessListByArgNumber = new ArrayList();
/*  29:    */     
/*  30:    */ 
/*  31: 28 */     this.methodAccessListByArgNumberWithVarArg = new ArrayList();
/*  32: 32 */     for (int index = 0; index < 25; index++)
/*  33:    */     {
/*  34: 33 */       this.methodAccessListByArgNumberWithVarArg.add(null);
/*  35: 34 */       this.methodAccessListByArgNumber.add(null);
/*  36:    */     }
/*  37:    */   }
/*  38:    */   
/*  39:    */   public OverloadedMethod add(MethodAccess methodAccess)
/*  40:    */   {
/*  41: 43 */     if (this.lock) {
/*  42: 44 */       Exceptions.die();
/*  43:    */     }
/*  44: 47 */     this.methodAccessList.add(methodAccess);
/*  45: 49 */     if (!methodAccess.method().isVarArgs())
/*  46:    */     {
/*  47: 50 */       List<MethodAccess> methodAccesses = (List)this.methodAccessListByArgNumber.get(methodAccess.parameterTypes().length);
/*  48: 51 */       if (methodAccesses == null)
/*  49:    */       {
/*  50: 52 */         methodAccesses = new ArrayList();
/*  51: 53 */         this.methodAccessListByArgNumber.set(methodAccess.parameterTypes().length, methodAccesses);
/*  52:    */       }
/*  53: 55 */       methodAccesses.add(methodAccess);
/*  54:    */     }
/*  55:    */     else
/*  56:    */     {
/*  57: 57 */       List<MethodAccess> methodAccesses = (List)this.methodAccessListByArgNumberWithVarArg.get(methodAccess.parameterTypes().length);
/*  58: 58 */       if (methodAccesses == null)
/*  59:    */       {
/*  60: 59 */         methodAccesses = new ArrayList();
/*  61: 60 */         this.methodAccessListByArgNumberWithVarArg.set(methodAccess.parameterTypes().length, methodAccesses);
/*  62:    */       }
/*  63: 62 */       methodAccesses.add(methodAccess);
/*  64:    */     }
/*  65: 64 */     return this;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public OverloadedMethod init()
/*  69:    */   {
/*  70: 69 */     if (this.lock) {
/*  71: 70 */       Exceptions.die();
/*  72:    */     }
/*  73: 73 */     for (List<MethodAccess> methodAccesses : this.methodAccessListByArgNumber) {
/*  74: 74 */       Collections.sort(methodAccesses);
/*  75:    */     }
/*  76: 77 */     lock();
/*  77: 78 */     return this;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public void lock()
/*  81:    */   {
/*  82: 84 */     this.lock = true;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public Object invokeDynamic(Object object, Object... args)
/*  86:    */   {
/*  87: 90 */     int length = args.length;
/*  88:    */     
/*  89:    */ 
/*  90: 93 */     List<MethodAccess> methodAccesses = (List)this.methodAccessListByArgNumber.get(length);
/*  91:    */     
/*  92:    */ 
/*  93: 96 */     int maxScore = -2147483648;
/*  94: 97 */     MethodAccess methodAccess = null;
/*  95: 99 */     for (MethodAccess m : methodAccesses)
/*  96:    */     {
/*  97:100 */       int score = 1;
/*  98:101 */       List<TypeType> paramTypeEnumList = m.paramTypeEnumList();
/*  99:103 */       if ((object != null) || (m.isStatic()))
/* 100:    */       {
/* 101:109 */         for (int argIndex = 0; argIndex < args.length; argIndex++)
/* 102:    */         {
/* 103:111 */           TypeType type = (TypeType)paramTypeEnumList.get(argIndex);
/* 104:112 */           Object arg = args[argIndex];
/* 105:    */           
/* 106:114 */           TypeType instanceType = TypeType.getInstanceType(arg);
/* 107:116 */           if (instanceType == type) {
/* 108:117 */             score += 2000;
/* 109:    */           } else {
/* 110:121 */             switch (1.$SwitchMap$org$boon$core$TypeType[type.ordinal()])
/* 111:    */             {
/* 112:    */             case 1: 
/* 113:    */             case 2: 
/* 114:124 */               score = handleByteArg(score, arg, instanceType);
/* 115:125 */               break;
/* 116:    */             case 3: 
/* 117:    */             case 4: 
/* 118:129 */               score = handleShortArg(score, arg, instanceType);
/* 119:130 */               break;
/* 120:    */             case 5: 
/* 121:    */             case 6: 
/* 122:134 */               score = handleIntArg(score, arg, instanceType);
/* 123:135 */               break;
/* 124:    */             case 7: 
/* 125:139 */               score--;
/* 126:140 */               break;
/* 127:    */             case 8: 
/* 128:    */             case 9: 
/* 129:144 */               score = handleLongArg(score, arg, instanceType);
/* 130:145 */               break;
/* 131:    */             case 10: 
/* 132:    */             case 11: 
/* 133:149 */               score = handleFloatArg(score, arg, instanceType);
/* 134:150 */               break;
/* 135:    */             case 12: 
/* 136:    */             case 13: 
/* 137:155 */               score = handleDoubleArg(score, arg, instanceType);
/* 138:156 */               break;
/* 139:    */             case 14: 
/* 140:    */             case 15: 
/* 141:161 */               if ((instanceType == TypeType.CHAR) || (instanceType == TypeType.CHAR_WRAPPER)) {
/* 142:163 */                 score += 1000;
/* 143:    */               }
/* 144:    */               break;
/* 145:    */             case 16: 
/* 146:168 */               if (instanceType == TypeType.STRING) {
/* 147:169 */                 score += 1000;
/* 148:170 */               } else if ((instanceType == TypeType.CHAR_SEQUENCE) || ((arg instanceof CharSequence))) {
/* 149:172 */                 score += 500;
/* 150:    */               }
/* 151:    */               break;
/* 152:    */             case 17: 
/* 153:178 */               if (instanceType == TypeType.INSTANCE)
/* 154:    */               {
/* 155:179 */                 if (m.parameterTypes()[argIndex].isInstance(arg)) {
/* 156:180 */                   score += 1000;
/* 157:    */                 }
/* 158:    */               }
/* 159:183 */               else if (instanceType == TypeType.MAP) {
/* 160:184 */                 score += 1000;
/* 161:185 */               } else if (instanceType == TypeType.LIST) {
/* 162:186 */                 score += 500;
/* 163:    */               }
/* 164:    */               break;
/* 165:    */             default: 
/* 166:191 */               if (instanceType == type) {
/* 167:192 */                 score += 1000;
/* 168:194 */               } else if (m.parameterTypes()[argIndex].isInstance(arg)) {
/* 169:195 */                 score += 1000;
/* 170:    */               }
/* 171:    */               break;
/* 172:    */             }
/* 173:    */           }
/* 174:    */         }
/* 175:204 */         if (score > maxScore)
/* 176:    */         {
/* 177:205 */           maxScore = score;
/* 178:206 */           methodAccess = m;
/* 179:    */         }
/* 180:    */       }
/* 181:    */     }
/* 182:210 */     if (methodAccess != null) {
/* 183:211 */       return methodAccess.invokeDynamic(object, args);
/* 184:    */     }
/* 185:214 */     List<MethodAccess> varargMethods = (List)this.methodAccessListByArgNumberWithVarArg.get(0);
/* 186:215 */     if (varargMethods != null) {
/* 187:216 */       ((MethodAccess)varargMethods.get(0)).invokeDynamic(args, new Object[0]);
/* 188:    */     }
/* 189:220 */     return null;
/* 190:    */   }
/* 191:    */   
/* 192:    */   private int handleLongArg(int score, Object arg, TypeType instanceType)
/* 193:    */   {
/* 194:224 */     switch (1.$SwitchMap$org$boon$core$TypeType[instanceType.ordinal()])
/* 195:    */     {
/* 196:    */     case 9: 
/* 197:228 */       score += 1000;
/* 198:229 */       break;
/* 199:    */     case 8: 
/* 200:232 */       score += 1000;
/* 201:233 */       break;
/* 202:    */     case 6: 
/* 203:236 */       score += 800;
/* 204:237 */       break;
/* 205:    */     case 5: 
/* 206:240 */       score += 700;
/* 207:241 */       break;
/* 208:    */     case 13: 
/* 209:244 */       score += 700;
/* 210:245 */       break;
/* 211:    */     case 2: 
/* 212:    */     case 4: 
/* 213:    */     case 11: 
/* 214:250 */       score += 600;
/* 215:251 */       break;
/* 216:    */     case 1: 
/* 217:    */     case 3: 
/* 218:    */     case 10: 
/* 219:    */     case 12: 
/* 220:259 */       score += 500;
/* 221:260 */       break;
/* 222:    */     case 16: 
/* 223:265 */       score += 400;
/* 224:    */       try
/* 225:    */       {
/* 226:267 */         arg = Integer.valueOf(arg.toString());
/* 227:    */       }
/* 228:    */       catch (Exception ex)
/* 229:    */       {
/* 230:270 */         score = -2147483648;
/* 231:    */       }
/* 232:    */     }
/* 233:274 */     return score;
/* 234:    */   }
/* 235:    */   
/* 236:    */   private int handleByteArg(int score, Object arg, TypeType instanceType)
/* 237:    */   {
/* 238:279 */     if ((instanceType == TypeType.BYTE) || (instanceType == TypeType.BYTE_WRAPPER)) {
/* 239:280 */       return score + 1010;
/* 240:    */     }
/* 241:282 */     return handleIntArg(score, arg, instanceType);
/* 242:    */   }
/* 243:    */   
/* 244:    */   private int handleShortArg(int score, Object arg, TypeType instanceType)
/* 245:    */   {
/* 246:287 */     if ((instanceType == TypeType.SHORT) || (instanceType == TypeType.SHORT_WRAPPER)) {
/* 247:288 */       return score + 1010;
/* 248:    */     }
/* 249:290 */     return handleIntArg(score, arg, instanceType);
/* 250:    */   }
/* 251:    */   
/* 252:    */   private int handleIntArg(int score, Object arg, TypeType instanceType)
/* 253:    */   {
/* 254:295 */     switch (1.$SwitchMap$org$boon$core$TypeType[instanceType.ordinal()])
/* 255:    */     {
/* 256:    */     case 6: 
/* 257:298 */       score += 1000;
/* 258:299 */       break;
/* 259:    */     case 5: 
/* 260:302 */       score += 900;
/* 261:303 */       break;
/* 262:    */     case 2: 
/* 263:    */     case 4: 
/* 264:307 */       score += 800;
/* 265:308 */       break;
/* 266:    */     case 9: 
/* 267:    */     case 11: 
/* 268:    */     case 13: 
/* 269:313 */       score += 700;
/* 270:314 */       break;
/* 271:    */     case 1: 
/* 272:    */     case 3: 
/* 273:319 */       score += 600;
/* 274:320 */       break;
/* 275:    */     case 8: 
/* 276:    */     case 10: 
/* 277:    */     case 12: 
/* 278:326 */       score += 500;
/* 279:327 */       break;
/* 280:    */     case 16: 
/* 281:331 */       score += 400;
/* 282:    */       try
/* 283:    */       {
/* 284:333 */         arg = Integer.valueOf(arg.toString());
/* 285:    */       }
/* 286:    */       catch (Exception ex)
/* 287:    */       {
/* 288:336 */         score = -2147483648;
/* 289:    */       }
/* 290:    */     }
/* 291:340 */     return score;
/* 292:    */   }
/* 293:    */   
/* 294:    */   private int handleFloatArg(int score, Object arg, TypeType instanceType)
/* 295:    */   {
/* 296:345 */     switch (1.$SwitchMap$org$boon$core$TypeType[instanceType.ordinal()])
/* 297:    */     {
/* 298:    */     case 11: 
/* 299:348 */       score += 1000;
/* 300:349 */       break;
/* 301:    */     case 10: 
/* 302:352 */       score += 900;
/* 303:353 */       break;
/* 304:    */     case 2: 
/* 305:    */     case 4: 
/* 306:357 */       score += 800;
/* 307:358 */       break;
/* 308:    */     case 13: 
/* 309:361 */       score += 700;
/* 310:362 */       break;
/* 311:    */     case 1: 
/* 312:    */     case 3: 
/* 313:    */     case 6: 
/* 314:    */     case 9: 
/* 315:369 */       score += 600;
/* 316:370 */       break;
/* 317:    */     case 5: 
/* 318:    */     case 8: 
/* 319:    */     case 12: 
/* 320:376 */       score += 500;
/* 321:377 */       break;
/* 322:    */     case 16: 
/* 323:381 */       score += 400;
/* 324:    */       try
/* 325:    */       {
/* 326:383 */         arg = Float.valueOf(arg.toString());
/* 327:    */       }
/* 328:    */       catch (Exception ex)
/* 329:    */       {
/* 330:386 */         score = -2147483648;
/* 331:    */       }
/* 332:    */     }
/* 333:390 */     return score;
/* 334:    */   }
/* 335:    */   
/* 336:    */   private int handleDoubleArg(int score, Object arg, TypeType instanceType)
/* 337:    */   {
/* 338:395 */     switch (1.$SwitchMap$org$boon$core$TypeType[instanceType.ordinal()])
/* 339:    */     {
/* 340:    */     case 13: 
/* 341:398 */       score += 1000;
/* 342:399 */       break;
/* 343:    */     case 12: 
/* 344:402 */       score += 900;
/* 345:403 */       break;
/* 346:    */     case 2: 
/* 347:    */     case 4: 
/* 348:407 */       score += 800;
/* 349:408 */       break;
/* 350:    */     case 11: 
/* 351:411 */       score += 700;
/* 352:412 */       break;
/* 353:    */     case 1: 
/* 354:    */     case 3: 
/* 355:    */     case 6: 
/* 356:    */     case 9: 
/* 357:419 */       score += 600;
/* 358:420 */       break;
/* 359:    */     case 5: 
/* 360:    */     case 8: 
/* 361:    */     case 10: 
/* 362:426 */       score += 500;
/* 363:427 */       break;
/* 364:    */     case 16: 
/* 365:431 */       score += 400;
/* 366:    */       try
/* 367:    */       {
/* 368:433 */         arg = Double.valueOf(arg.toString());
/* 369:    */       }
/* 370:    */       catch (Exception ex)
/* 371:    */       {
/* 372:435 */         score = -2147483648;
/* 373:    */       }
/* 374:    */     }
/* 375:439 */     return score;
/* 376:    */   }
/* 377:    */   
/* 378:    */   public Object invoke(Object object, Object... args)
/* 379:    */   {
/* 380:444 */     return null;
/* 381:    */   }
/* 382:    */   
/* 383:    */   public boolean isStatic()
/* 384:    */   {
/* 385:449 */     return false;
/* 386:    */   }
/* 387:    */   
/* 388:    */   public boolean isPublic()
/* 389:    */   {
/* 390:454 */     return false;
/* 391:    */   }
/* 392:    */   
/* 393:    */   public boolean isPrivate()
/* 394:    */   {
/* 395:459 */     return false;
/* 396:    */   }
/* 397:    */   
/* 398:    */   public String name()
/* 399:    */   {
/* 400:464 */     return null;
/* 401:    */   }
/* 402:    */   
/* 403:    */   public Class<?> declaringType()
/* 404:    */   {
/* 405:469 */     return null;
/* 406:    */   }
/* 407:    */   
/* 408:    */   public Class<?> returnType()
/* 409:    */   {
/* 410:474 */     return null;
/* 411:    */   }
/* 412:    */   
/* 413:    */   public boolean respondsTo(Class<?>... types)
/* 414:    */   {
/* 415:479 */     return false;
/* 416:    */   }
/* 417:    */   
/* 418:    */   public boolean respondsTo(Object... args)
/* 419:    */   {
/* 420:484 */     return false;
/* 421:    */   }
/* 422:    */   
/* 423:    */   public Object invokeStatic(Object... args)
/* 424:    */   {
/* 425:489 */     return null;
/* 426:    */   }
/* 427:    */   
/* 428:    */   public MethodAccess bind(Object instance)
/* 429:    */   {
/* 430:494 */     return null;
/* 431:    */   }
/* 432:    */   
/* 433:    */   public MethodHandle methodHandle()
/* 434:    */   {
/* 435:499 */     return null;
/* 436:    */   }
/* 437:    */   
/* 438:    */   public MethodAccess methodAccess()
/* 439:    */   {
/* 440:504 */     return null;
/* 441:    */   }
/* 442:    */   
/* 443:    */   public Object bound()
/* 444:    */   {
/* 445:509 */     return null;
/* 446:    */   }
/* 447:    */   
/* 448:    */   public <T> ConstantCallSite invokeReducerLongIntReturnLongMethodHandle(T object)
/* 449:    */   {
/* 450:514 */     return null;
/* 451:    */   }
/* 452:    */   
/* 453:    */   public Method method()
/* 454:    */   {
/* 455:519 */     return null;
/* 456:    */   }
/* 457:    */   
/* 458:    */   public int score()
/* 459:    */   {
/* 460:524 */     return 0;
/* 461:    */   }
/* 462:    */   
/* 463:    */   public List<TypeType> paramTypeEnumList()
/* 464:    */   {
/* 465:529 */     return Collections.emptyList();
/* 466:    */   }
/* 467:    */   
/* 468:    */   public Object invokeDynamicObject(Object object, Object args)
/* 469:    */   {
/* 470:535 */     if ((args instanceof List)) {
/* 471:536 */       return invokeDynamicList(object, (List)args);
/* 472:    */     }
/* 473:538 */     return invokeDynamic(object, new Object[] { args });
/* 474:    */   }
/* 475:    */   
/* 476:    */   public List<List<AnnotationData>> annotationDataForParams()
/* 477:    */   {
/* 478:544 */     return null;
/* 479:    */   }
/* 480:    */   
/* 481:    */   public Object invokeDynamicList(Object object, List<?> args)
/* 482:    */   {
/* 483:549 */     return invokeDynamic(object, Arry.objectArray(args));
/* 484:    */   }
/* 485:    */   
/* 486:    */   public Class<?>[] parameterTypes()
/* 487:    */   {
/* 488:555 */     return new Class[0];
/* 489:    */   }
/* 490:    */   
/* 491:    */   public Type[] getGenericParameterTypes()
/* 492:    */   {
/* 493:560 */     return new Type[0];
/* 494:    */   }
/* 495:    */   
/* 496:    */   public Iterable<AnnotationData> annotationData()
/* 497:    */   {
/* 498:565 */     return null;
/* 499:    */   }
/* 500:    */   
/* 501:    */   public boolean hasAnnotation(String annotationName)
/* 502:    */   {
/* 503:570 */     return false;
/* 504:    */   }
/* 505:    */   
/* 506:    */   public AnnotationData annotation(String annotationName)
/* 507:    */   {
/* 508:575 */     return null;
/* 509:    */   }
/* 510:    */   
/* 511:    */   public int compareTo(MethodAccess o)
/* 512:    */   {
/* 513:580 */     return 0;
/* 514:    */   }
/* 515:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.reflection.impl.OverloadedMethod
 * JD-Core Version:    0.7.0.1
 */