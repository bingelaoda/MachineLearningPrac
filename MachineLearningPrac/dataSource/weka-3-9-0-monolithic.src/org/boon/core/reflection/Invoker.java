/*   1:    */ package org.boon.core.reflection;
/*   2:    */ 
/*   3:    */ import java.lang.invoke.ConstantCallSite;
/*   4:    */ import java.lang.reflect.Method;
/*   5:    */ import java.lang.reflect.ParameterizedType;
/*   6:    */ import java.lang.reflect.Type;
/*   7:    */ import java.util.ArrayList;
/*   8:    */ import java.util.Collection;
/*   9:    */ import java.util.List;
/*  10:    */ import java.util.Map;
/*  11:    */ import java.util.Map.Entry;
/*  12:    */ import java.util.Set;
/*  13:    */ import org.boon.Boon;
/*  14:    */ import org.boon.Exceptions;
/*  15:    */ import org.boon.Lists;
/*  16:    */ import org.boon.core.Conversions;
/*  17:    */ import org.boon.core.Typ;
/*  18:    */ import org.boon.core.TypeType;
/*  19:    */ import org.boon.core.reflection.fields.FieldAccessMode;
/*  20:    */ import org.boon.core.reflection.fields.FieldsAccessor;
/*  21:    */ import org.boon.core.value.ValueContainer;
/*  22:    */ import org.boon.primitive.CharBuf;
/*  23:    */ 
/*  24:    */ public class Invoker
/*  25:    */ {
/*  26:    */   public static Object invokeOverloadedFromObject(Object object, String name, Object args)
/*  27:    */   {
/*  28: 65 */     return invokeOverloadedFromObject(false, null, null, object, name, args);
/*  29:    */   }
/*  30:    */   
/*  31:    */   public static Object invokeOverloadedFromObject(boolean respectIgnore, String view, Set<String> ignoreProperties, Object object, String name, Object args)
/*  32:    */   {
/*  33:    */     try
/*  34:    */     {
/*  35: 74 */       if ((args instanceof Map)) {
/*  36: 75 */         return invokeOverloadedFromList(respectIgnore, view, ignoreProperties, object, name, Lists.list(new Object[] { args }));
/*  37:    */       }
/*  38: 76 */       if ((args instanceof List))
/*  39:    */       {
/*  40: 77 */         List list = (List)args;
/*  41: 78 */         ClassMeta classMeta = ClassMeta.classMeta(object.getClass());
/*  42: 79 */         MethodAccess m = classMeta.method(name);
/*  43: 80 */         if ((m.parameterTypes().length == 1) && (list.size() > 0))
/*  44:    */         {
/*  45: 82 */           Object firstArg = list.get(0);
/*  46: 83 */           if (((firstArg instanceof Map)) || ((firstArg instanceof List))) {
/*  47: 84 */             return invokeOverloadedFromList(respectIgnore, view, ignoreProperties, object, name, list);
/*  48:    */           }
/*  49: 87 */           return invokeOverloadedFromList(respectIgnore, view, ignoreProperties, object, name, Lists.list(new Object[] { args }));
/*  50:    */         }
/*  51: 91 */         return invokeOverloadedFromList(respectIgnore, view, ignoreProperties, object, name, list);
/*  52:    */       }
/*  53: 94 */       if (args == null) {
/*  54: 95 */         return invoke(object, name, new Object[0]);
/*  55:    */       }
/*  56: 97 */       return invokeOverloadedFromList(respectIgnore, view, ignoreProperties, object, name, Lists.list(new Object[] { args }));
/*  57:    */     }
/*  58:    */     catch (Exception ex)
/*  59:    */     {
/*  60:101 */       return Exceptions.handle(Object.class, ex, new Object[] { "Unable to invoke method object", object, "name", name, "args", args });
/*  61:    */     }
/*  62:    */   }
/*  63:    */   
/*  64:    */   public static Object invokeFromObject(Object object, String name, Object args)
/*  65:    */   {
/*  66:107 */     return invokeFromObject(false, null, null, object, name, args);
/*  67:    */   }
/*  68:    */   
/*  69:    */   public static Object invokeFromObject(Class<?> cls, String name, Object args)
/*  70:    */   {
/*  71:113 */     return invokeFromObject(false, null, null, cls, null, name, args);
/*  72:    */   }
/*  73:    */   
/*  74:    */   public static Object invokeMethodFromObjectArg(Object object, MethodAccess method, Object args)
/*  75:    */   {
/*  76:126 */     return invokeMethodFromObjectArg(false, null, null, object, method, args);
/*  77:    */   }
/*  78:    */   
/*  79:    */   public static Object invokeMethodFromObjectArg(boolean respectIgnore, String view, Set<String> ignoreProperties, Object object, MethodAccess method, Object args)
/*  80:    */   {
/*  81:    */     try
/*  82:    */     {
/*  83:135 */       if ((args instanceof Map)) {
/*  84:136 */         return invokeMethodFromList(respectIgnore, view, ignoreProperties, object, method, Lists.list(new Object[] { args }));
/*  85:    */       }
/*  86:137 */       if ((args instanceof List))
/*  87:    */       {
/*  88:138 */         List list = (List)args;
/*  89:    */         
/*  90:140 */         Class<?>[] paramTypes = method.parameterTypes();
/*  91:142 */         if ((paramTypes.length == 1) && (list.size() > 0))
/*  92:    */         {
/*  93:144 */           Class<?> firstParamType = paramTypes[0];
/*  94:145 */           Object firstArg = list.get(0);
/*  95:148 */           if ((firstArg instanceof Map)) {
/*  96:149 */             return invokeMethodFromList(respectIgnore, view, ignoreProperties, object, method, list);
/*  97:    */           }
/*  98:153 */           if (((firstArg instanceof List)) && (!Typ.isCollection(firstParamType)) && (!firstParamType.isArray())) {
/*  99:156 */             return invokeMethodFromList(respectIgnore, view, ignoreProperties, object, method, list);
/* 100:    */           }
/* 101:159 */           return invokeMethodFromList(respectIgnore, view, ignoreProperties, object, method, Lists.list(new Object[] { args }));
/* 102:    */         }
/* 103:164 */         return invokeMethodFromList(respectIgnore, view, ignoreProperties, object, method, list);
/* 104:    */       }
/* 105:167 */       if (args == null) {
/* 106:168 */         return method.invoke(object, new Object[0]);
/* 107:    */       }
/* 108:170 */       return invokeMethodFromList(respectIgnore, view, ignoreProperties, object, method, Lists.list(new Object[] { args }));
/* 109:    */     }
/* 110:    */     catch (Exception ex)
/* 111:    */     {
/* 112:173 */       return Exceptions.handle(Object.class, ex, new Object[] { "Unable to invoke method object", object, "method", method, "args", args });
/* 113:    */     }
/* 114:    */   }
/* 115:    */   
/* 116:    */   public static Object invokeFromObject(boolean respectIgnore, String view, Set<String> ignoreProperties, Object object, String name, Object args)
/* 117:    */   {
/* 118:183 */     return invokeFromObject(respectIgnore, view, ignoreProperties, object.getClass(), object, name, args);
/* 119:    */   }
/* 120:    */   
/* 121:    */   public static Object invokeFromObject(boolean respectIgnore, String view, Set<String> ignoreProperties, Class<?> cls, Object object, String name, Object args)
/* 122:    */   {
/* 123:    */     try
/* 124:    */     {
/* 125:191 */       if ((args instanceof Map)) {
/* 126:192 */         return invokeFromList(respectIgnore, view, ignoreProperties, cls, object, name, Lists.list(new Object[] { args }));
/* 127:    */       }
/* 128:193 */       if ((args instanceof List))
/* 129:    */       {
/* 130:194 */         List list = (List)args;
/* 131:195 */         ClassMeta classMeta = ClassMeta.classMeta(cls);
/* 132:196 */         MethodAccess m = classMeta.method(name);
/* 133:197 */         if ((m.parameterTypes().length == 1) && (list.size() > 0))
/* 134:    */         {
/* 135:199 */           Object firstArg = list.get(0);
/* 136:200 */           if (((firstArg instanceof Map)) || ((firstArg instanceof List))) {
/* 137:201 */             return invokeFromList(respectIgnore, view, ignoreProperties, cls, object, name, list);
/* 138:    */           }
/* 139:204 */           Class<?> aClass = m.parameterTypes()[0];
/* 140:205 */           TypeType type = TypeType.getType(aClass);
/* 141:206 */           switch (1.$SwitchMap$org$boon$core$TypeType[type.ordinal()])
/* 142:    */           {
/* 143:    */           case 1: 
/* 144:208 */             return invokeFromList(respectIgnore, view, ignoreProperties, cls, object, name, Lists.list(new Object[] { args }));
/* 145:    */           }
/* 146:211 */           return invokeFromList(respectIgnore, view, ignoreProperties, cls, object, name, list);
/* 147:    */         }
/* 148:217 */         return invokeFromList(respectIgnore, view, ignoreProperties, cls, object, name, list);
/* 149:    */       }
/* 150:220 */       if (args == null) {
/* 151:221 */         return invoke(object, name, new Object[0]);
/* 152:    */       }
/* 153:223 */       return invokeFromList(respectIgnore, view, ignoreProperties, cls, object, name, Lists.list(new Object[] { args }));
/* 154:    */     }
/* 155:    */     catch (Exception ex)
/* 156:    */     {
/* 157:226 */       return Exceptions.handle(Object.class, ex, new Object[] { "Unable to invoke method object", object, "name", name, "args", args });
/* 158:    */     }
/* 159:    */   }
/* 160:    */   
/* 161:    */   public static Object invokeFromList(Object object, String name, List<?> args)
/* 162:    */   {
/* 163:232 */     return invokeFromList(true, null, null, object, name, args);
/* 164:    */   }
/* 165:    */   
/* 166:    */   public static Object invokeFromList(Class<?> cls, String name, List<?> args)
/* 167:    */   {
/* 168:237 */     return invokeFromList(true, null, null, cls, null, name, args);
/* 169:    */   }
/* 170:    */   
/* 171:    */   public static Object invokeFromList(boolean respectIgnore, String view, Set<String> ignoreProperties, Object object, String name, List<?> args)
/* 172:    */   {
/* 173:243 */     return invokeFromList(respectIgnore, view, ignoreProperties, object.getClass(), object, name, args);
/* 174:    */   }
/* 175:    */   
/* 176:    */   private static Object[] convertArguments(boolean respectIgnore, String view, Set<String> ignoreProperties, Object object, List<?> argsList, MethodAccess methodAccess)
/* 177:    */   {
/* 178:251 */     List<Object> convertedArguments = new ArrayList(argsList);
/* 179:252 */     Class<?>[] parameterTypes = methodAccess.parameterTypes();
/* 180:    */     
/* 181:254 */     boolean[] flag = new boolean[1];
/* 182:256 */     if (convertedArguments.size() != parameterTypes.length) {
/* 183:257 */       return (Object[])Exceptions.die([Ljava.lang.Object.class, new Object[] { "The list size does not match the parameter length of the method. Unable to invoke method", methodAccess.name(), "on object", object, "with arguments", convertedArguments });
/* 184:    */     }
/* 185:261 */     FieldsAccessor fieldsAccessor = FieldAccessMode.FIELD.create(true);
/* 186:265 */     for (int index = 0; index < parameterTypes.length; index++) {
/* 187:267 */       if (!matchAndConvertArgs(respectIgnore, view, fieldsAccessor, convertedArguments, methodAccess, parameterTypes, index, ignoreProperties, flag, true)) {
/* 188:268 */         return (Object[])Exceptions.die([Ljava.lang.Object.class, new Object[] { Integer.valueOf(index), "Unable to invoke method as argument types did not match", methodAccess.name(), "on object", object, "with arguments", convertedArguments, "\nValue at index = ", convertedArguments.get(index) });
/* 189:    */       }
/* 190:    */     }
/* 191:275 */     return convertedArguments.toArray(new Object[convertedArguments.size()]);
/* 192:    */   }
/* 193:    */   
/* 194:    */   public static Object invokeFromList(boolean respectIgnore, String view, Set<String> ignoreProperties, Class<?> cls, Object object, String name, List<?> argsList)
/* 195:    */   {
/* 196:286 */     Object[] finalArgs = null;
/* 197:    */     
/* 198:    */ 
/* 199:    */ 
/* 200:    */ 
/* 201:291 */     ClassMeta classMeta = ClassMeta.classMeta(cls);
/* 202:292 */     MethodAccess methodAccess = classMeta.method(name);
/* 203:    */     try
/* 204:    */     {
/* 205:296 */       finalArgs = convertArguments(respectIgnore, view, ignoreProperties, object, argsList, methodAccess);
/* 206:    */       
/* 207:    */ 
/* 208:    */ 
/* 209:300 */       return methodAccess.invoke(object, finalArgs);
/* 210:    */     }
/* 211:    */     catch (Exception ex)
/* 212:    */     {
/* 213:307 */       if (methodAccess != null)
/* 214:    */       {
/* 215:310 */         CharBuf buf = CharBuf.create(200);
/* 216:311 */         buf.addLine();
/* 217:312 */         buf.multiply('-', 10).add("FINAL ARGUMENTS").multiply('-', 10).addLine();
/* 218:313 */         if (finalArgs != null) {
/* 219:314 */           for (Object o : finalArgs) {
/* 220:315 */             buf.puts(new Object[] { "argument type    ", Boon.className(o) });
/* 221:    */           }
/* 222:    */         }
/* 223:320 */         buf.multiply('-', 10).add("INVOKE METHOD").add(methodAccess).multiply('-', 10).addLine();
/* 224:321 */         buf.multiply('-', 10).add("INVOKE METHOD PARAMS").multiply('-', 10).addLine();
/* 225:322 */         for (Class<?> c : methodAccess.parameterTypes()) {
/* 226:323 */           buf.puts(new Object[] { "constructor type ", c });
/* 227:    */         }
/* 228:326 */         buf.multiply('-', 35).addLine();
/* 229:328 */         if (Boon.debugOn()) {
/* 230:329 */           Boon.puts(new Object[] { buf });
/* 231:    */         }
/* 232:332 */         Boon.error(ex, new Object[] { "unable to create invoke method", buf });
/* 233:    */         
/* 234:    */ 
/* 235:335 */         return Exceptions.handle(Object.class, ex, new Object[] { buf.toString(), "\nconstructor parameter types", methodAccess.parameterTypes(), "\noriginal args\n", argsList, "\nlist args after conversion", finalArgs, "\nconverted types\n", TypeType.gatherTypes(finalArgs), "original types\n", TypeType.gatherTypes(argsList), "\n" });
/* 236:    */       }
/* 237:342 */       return Exceptions.handle(Object.class, ex, new Object[] { "\nlist args after conversion", finalArgs, "types", TypeType.gatherTypes(finalArgs), "\noriginal args", argsList, "original types\n", TypeType.gatherTypes(argsList), "\n" });
/* 238:    */     }
/* 239:    */   }
/* 240:    */   
/* 241:    */   public static Object invokeMethodFromList(boolean respectIgnore, String view, Set<String> ignoreProperties, Object object, MethodAccess method, List<?> argsList)
/* 242:    */   {
/* 243:    */     try
/* 244:    */     {
/* 245:363 */       if ((argsList == null) && (method.parameterTypes().length == 0)) {
/* 246:364 */         return method.invoke(object, new Object[0]);
/* 247:    */       }
/* 248:367 */       Object[] finalArgs = convertArguments(respectIgnore, view, ignoreProperties, object, argsList, method);
/* 249:    */       
/* 250:    */ 
/* 251:    */ 
/* 252:    */ 
/* 253:372 */       return method.invoke(object, finalArgs);
/* 254:    */     }
/* 255:    */     catch (Exception ex)
/* 256:    */     {
/* 257:375 */       return Exceptions.handle(Object.class, ex, new Object[] { "Unable to invoke method object", object, "method", method, "args", argsList });
/* 258:    */     }
/* 259:    */   }
/* 260:    */   
/* 261:    */   public static Object invokeEither(Object object, String name, Object... args)
/* 262:    */   {
/* 263:382 */     if ((object instanceof Class)) {
/* 264:383 */       return invoke((Class)object, name, args);
/* 265:    */     }
/* 266:385 */     return invoke(object, name, args);
/* 267:    */   }
/* 268:    */   
/* 269:    */   public static Object invoke(Object object, String name, Object... args)
/* 270:    */   {
/* 271:390 */     return ClassMeta.classMetaUnTyped(object.getClass()).invokeUntyped(object, name, args);
/* 272:    */   }
/* 273:    */   
/* 274:    */   public static MethodAccess invokeMethodAccess(Object object, String name)
/* 275:    */   {
/* 276:395 */     return ClassMeta.classMeta(object.getClass()).invokeMethodAccess(name);
/* 277:    */   }
/* 278:    */   
/* 279:    */   public static MethodAccess invokeMethodAccess(Class<?> cls, String name)
/* 280:    */   {
/* 281:400 */     return ClassMeta.classMeta(cls).invokeMethodAccess(name);
/* 282:    */   }
/* 283:    */   
/* 284:    */   public static Object invoke(Class cls, String name, Object... args)
/* 285:    */   {
/* 286:404 */     return ClassMeta.classMeta(cls).invokeStatic(name, args);
/* 287:    */   }
/* 288:    */   
/* 289:    */   public static Object invokeOverloaded(Object object, String name, Object... args)
/* 290:    */   {
/* 291:409 */     ClassMeta classMeta = ClassMeta.classMeta(object.getClass());
/* 292:410 */     Iterable<MethodAccess> invokers = classMeta.methods(name);
/* 293:412 */     for (MethodAccess m : invokers) {
/* 294:413 */       if (m.respondsTo(args)) {
/* 295:414 */         return m.invoke(object, args);
/* 296:    */       }
/* 297:    */     }
/* 298:417 */     return Exceptions.die(Object.class, new Object[] { "Unable to invoke method", name, "on object", object, "with arguments", args });
/* 299:    */   }
/* 300:    */   
/* 301:    */   public static Object invokeOverloadedFromList(Object object, String name, List<?> args)
/* 302:    */   {
/* 303:423 */     return invokeOverloadedFromList(true, null, null, object, name, args);
/* 304:    */   }
/* 305:    */   
/* 306:    */   public static Object invokeOverloadedFromList(boolean respectIgnore, String view, Set<String> ignoreProperties, Object object, String name, List<?> args)
/* 307:    */   {
/* 308:433 */     ClassMeta classMeta = ClassMeta.classMeta(object.getClass());
/* 309:434 */     Iterable<MethodAccess> invokers = classMeta.methods(name);
/* 310:    */     
/* 311:436 */     List<Object> list = new ArrayList(args);
/* 312:437 */     FieldsAccessor fieldsAccessor = FieldAccessMode.FIELD.create(true);
/* 313:    */     
/* 314:439 */     boolean[] flag = new boolean[1];
/* 315:    */     
/* 316:441 */     MethodAccess method = lookupOverloadedMethod(respectIgnore, view, ignoreProperties, invokers, list, fieldsAccessor, flag, false);
/* 317:443 */     if (method == null) {
/* 318:444 */       method = lookupOverloadedMethod(respectIgnore, view, ignoreProperties, invokers, list, fieldsAccessor, flag, true);
/* 319:    */     }
/* 320:447 */     if (method != null) {
/* 321:448 */       return method.invoke(object, list.toArray(new Object[list.size()]));
/* 322:    */     }
/* 323:450 */     return Exceptions.die(Object.class, new Object[] { "Unable to invoke method", name, "on object", object, "with arguments", args });
/* 324:    */   }
/* 325:    */   
/* 326:    */   private static MethodAccess lookupOverloadedMethod(boolean respectIgnore, String view, Set<String> ignoreProperties, Iterable<MethodAccess> invokers, List<Object> list, FieldsAccessor fieldsAccessor, boolean[] flag, boolean loose)
/* 327:    */   {
/* 328:460 */     MethodAccess method = null;
/* 329:463 */     for (MethodAccess m : invokers)
/* 330:    */     {
/* 331:464 */       Class<?>[] parameterTypes = m.parameterTypes();
/* 332:465 */       if (parameterTypes.length == list.size())
/* 333:    */       {
/* 334:468 */         for (int index = 0;; index++)
/* 335:    */         {
/* 336:468 */           if (index >= parameterTypes.length) {
/* 337:    */             break label101;
/* 338:    */           }
/* 339:469 */           if (!matchAndConvertArgs(respectIgnore, view, fieldsAccessor, list, m, parameterTypes, index, ignoreProperties, flag, loose)) {
/* 340:    */             break;
/* 341:    */           }
/* 342:    */         }
/* 343:475 */         method = m;
/* 344:    */       }
/* 345:    */     }
/* 346:    */     label101:
/* 347:479 */     return method;
/* 348:    */   }
/* 349:    */   
/* 350:    */   public static void invokeMethodWithAnnotationNoReturn(Object object, String annotation)
/* 351:    */   {
/* 352:483 */     invokeMethodWithAnnotationWithReturnType(object, annotation, Void.TYPE);
/* 353:    */   }
/* 354:    */   
/* 355:    */   public static void invokeMethodWithAnnotationWithReturnType(Object object, String annotation, Class<?> returnType)
/* 356:    */   {
/* 357:487 */     invokeMethodWithAnnotationWithReturnType(object.getClass(), object, annotation, returnType);
/* 358:    */   }
/* 359:    */   
/* 360:    */   public static void invokeMethodWithAnnotationWithReturnType(Class<?> type, Object object, String annotation, Class<?> returnType)
/* 361:    */   {
/* 362:491 */     ClassMeta classMeta = ClassMeta.classMeta(type);
/* 363:492 */     Iterable<MethodAccess> iterate = classMeta.methods();
/* 364:493 */     for (MethodAccess m : iterate) {
/* 365:494 */       if ((m.hasAnnotation(annotation)) && 
/* 366:495 */         (m.parameterTypes().length == 0) && (m.returnType() == Void.TYPE))
/* 367:    */       {
/* 368:496 */         m.invoke(object, new Object[0]);
/* 369:497 */         break;
/* 370:    */       }
/* 371:    */     }
/* 372:    */   }
/* 373:    */   
/* 374:    */   public static <T> boolean invokeBooleanReturn(Object object, T v)
/* 375:    */   {
/* 376:506 */     Object instance = null;
/* 377:    */     Class cls;
/* 378:    */     Class cls;
/* 379:507 */     if ((object instanceof Class))
/* 380:    */     {
/* 381:508 */       cls = (Class)object;
/* 382:    */     }
/* 383:    */     else
/* 384:    */     {
/* 385:510 */       cls = object.getClass();
/* 386:511 */       instance = object;
/* 387:    */     }
/* 388:514 */     ClassMeta meta = ClassMeta.classMeta(cls);
/* 389:515 */     return meta.invokePredicate(instance, v);
/* 390:    */   }
/* 391:    */   
/* 392:    */   public static Object invokeReducer(Object object, Object sum, Object value)
/* 393:    */   {
/* 394:520 */     if ((object instanceof Class))
/* 395:    */     {
/* 396:521 */       ClassMeta meta = ClassMeta.classMeta((Class)object);
/* 397:522 */       return meta.invokeReducer(null, sum, value);
/* 398:    */     }
/* 399:524 */     ClassMeta meta = ClassMeta.classMeta(object.getClass());
/* 400:    */     
/* 401:526 */     return meta.invokeReducer(object, sum, value);
/* 402:    */   }
/* 403:    */   
/* 404:    */   public static Object invokeFunction(Object object, Object arg)
/* 405:    */   {
/* 406:533 */     if ((object instanceof Class))
/* 407:    */     {
/* 408:534 */       ClassMeta meta = ClassMeta.classMeta((Class)object);
/* 409:535 */       return meta.invokeFunction(null, arg);
/* 410:    */     }
/* 411:537 */     ClassMeta meta = ClassMeta.classMeta(object.getClass());
/* 412:    */     
/* 413:539 */     return meta.invokeFunction(object, arg);
/* 414:    */   }
/* 415:    */   
/* 416:    */   public static MethodAccess invokeFunctionMethodAccess(Object object)
/* 417:    */   {
/* 418:548 */     if ((object instanceof Class))
/* 419:    */     {
/* 420:549 */       ClassMeta meta = ClassMeta.classMeta((Class)object);
/* 421:550 */       return meta.invokeFunctionMethodAccess();
/* 422:    */     }
/* 423:552 */     ClassMeta meta = ClassMeta.classMeta(object.getClass());
/* 424:    */     
/* 425:554 */     return meta.invokeFunctionMethodAccess();
/* 426:    */   }
/* 427:    */   
/* 428:    */   public static ConstantCallSite invokeReducerLongIntReturnLongMethodHandle(Object object)
/* 429:    */   {
/* 430:561 */     ClassMeta meta = ClassMeta.classMeta(object.getClass());
/* 431:562 */     return meta.invokeReducerLongIntReturnLongMethodHandle(object);
/* 432:    */   }
/* 433:    */   
/* 434:    */   public static <T> ConstantCallSite invokeReducerLongIntReturnLongMethodHandle(T object, String methodName)
/* 435:    */   {
/* 436:568 */     ClassMeta meta = ClassMeta.classMeta(object.getClass());
/* 437:569 */     return meta.invokeReducerLongIntReturnLongMethodHandle(object, methodName);
/* 438:    */   }
/* 439:    */   
/* 440:    */   public static Method invokeReducerLongIntReturnLongMethod(Object object)
/* 441:    */   {
/* 442:574 */     ClassMeta meta = ClassMeta.classMeta(object.getClass());
/* 443:575 */     return meta.invokeReducerLongIntReturnLongMethod(object);
/* 444:    */   }
/* 445:    */   
/* 446:    */   public static <T> Method invokeReducerLongIntReturnLongMethod(T object, String methodName)
/* 447:    */   {
/* 448:581 */     ClassMeta meta = ClassMeta.classMeta(object.getClass());
/* 449:582 */     return meta.invokeReducerLongIntReturnLongMethod(object, methodName);
/* 450:    */   }
/* 451:    */   
/* 452:    */   public static boolean matchAndConvertArgs(boolean respectIgnore, String view, FieldsAccessor fieldsAccessor, List<Object> convertedArgumentList, BaseAccess methodAccess, Class[] parameterTypes, int index, Set<String> ignoreSet, boolean[] flag, boolean loose)
/* 453:    */   {
/* 454:    */     try
/* 455:    */     {
/* 456:627 */       Class parameterClass = parameterTypes[index];
/* 457:628 */       Object item = convertedArgumentList.get(index);
/* 458:    */       
/* 459:    */ 
/* 460:631 */       TypeType parameterType = TypeType.getType(parameterClass);
/* 461:634 */       if ((item instanceof ValueContainer))
/* 462:    */       {
/* 463:635 */         item = ((ValueContainer)item).toValue();
/* 464:    */         
/* 465:637 */         convertedArgumentList.set(index, item);
/* 466:    */       }
/* 467:643 */       if (item == null) {
/* 468:644 */         return true;
/* 469:    */       }
/* 470:    */       Object value;
/* 471:647 */       switch (1.$SwitchMap$org$boon$core$TypeType[parameterType.ordinal()])
/* 472:    */       {
/* 473:    */       case 6: 
/* 474:    */       case 7: 
/* 475:    */       case 8: 
/* 476:    */       case 9: 
/* 477:    */       case 10: 
/* 478:    */       case 11: 
/* 479:    */       case 12: 
/* 480:    */       case 24: 
/* 481:656 */         if (item == null) {
/* 482:657 */           return false;
/* 483:    */         }
/* 484:    */       case 5: 
/* 485:    */       case 13: 
/* 486:    */       case 14: 
/* 487:    */       case 15: 
/* 488:    */       case 16: 
/* 489:    */       case 17: 
/* 490:    */       case 18: 
/* 491:    */       case 19: 
/* 492:    */       case 22: 
/* 493:    */       case 25: 
/* 494:672 */         if (!loose)
/* 495:    */         {
/* 496:673 */           if ((item instanceof Number))
/* 497:    */           {
/* 498:674 */             Object value = Conversions.coerceWithFlag(parameterType, parameterClass, flag, item);
/* 499:675 */             convertedArgumentList.set(index, value);
/* 500:    */             
/* 501:677 */             return flag[0];
/* 502:    */           }
/* 503:679 */           return false;
/* 504:    */         }
/* 505:683 */         value = Conversions.coerceWithFlag(parameterType, parameterClass, flag, item);
/* 506:684 */         convertedArgumentList.set(index, value);
/* 507:    */         
/* 508:686 */         return flag[0];
/* 509:    */       case 20: 
/* 510:    */       case 23: 
/* 511:    */       case 26: 
/* 512:696 */         if ((!loose) && (!(item instanceof CharSequence))) {
/* 513:697 */           return false;
/* 514:    */         }
/* 515:701 */         value = Conversions.coerceWithFlag(parameterType, parameterClass, flag, item);
/* 516:703 */         if (flag[0] == 0) {
/* 517:704 */           return false;
/* 518:    */         }
/* 519:706 */         convertedArgumentList.set(index, value);
/* 520:707 */         return true;
/* 521:    */       case 3: 
/* 522:    */       case 4: 
/* 523:712 */         if (!(item instanceof Map)) {
/* 524:    */           break label1566;
/* 525:    */         }
/* 526:713 */         Map itemMap = (Map)item;
/* 527:    */         
/* 528:    */ 
/* 529:    */ 
/* 530:    */ 
/* 531:    */ 
/* 532:719 */         Type type = methodAccess.getGenericParameterTypes()[index];
/* 533:720 */         if ((type instanceof ParameterizedType))
/* 534:    */         {
/* 535:721 */           ParameterizedType pType = (ParameterizedType)type;
/* 536:722 */           Class<?> keyType = (Class)pType.getActualTypeArguments()[0];
/* 537:    */           
/* 538:724 */           Class<?> valueType = (Class)pType.getActualTypeArguments()[1];
/* 539:    */           
/* 540:    */ 
/* 541:727 */           Map newMap = Conversions.createMap(parameterClass, itemMap.size());
/* 542:734 */           for (Object o : itemMap.entrySet())
/* 543:    */           {
/* 544:735 */             Map.Entry entry = (Map.Entry)o;
/* 545:    */             
/* 546:737 */             Object key = entry.getKey();
/* 547:738 */             value = entry.getValue();
/* 548:    */             
/* 549:740 */             key = ValueContainer.toObject(key);
/* 550:    */             
/* 551:742 */             value = ValueContainer.toObject(value);
/* 552:748 */             if ((value instanceof List)) {
/* 553:749 */               value = MapObjectConversion.fromList(respectIgnore, view, fieldsAccessor, (List)value, valueType, ignoreSet);
/* 554:751 */             } else if ((value instanceof Map)) {
/* 555:752 */               value = MapObjectConversion.fromMap(respectIgnore, view, fieldsAccessor, (Map)value, valueType, ignoreSet);
/* 556:    */             } else {
/* 557:755 */               value = Conversions.coerce(valueType, value);
/* 558:    */             }
/* 559:759 */             if ((key instanceof List)) {
/* 560:760 */               key = MapObjectConversion.fromList(respectIgnore, view, fieldsAccessor, (List)key, keyType, ignoreSet);
/* 561:762 */             } else if ((value instanceof Map)) {
/* 562:763 */               key = MapObjectConversion.fromMap(respectIgnore, view, fieldsAccessor, (Map)key, keyType, ignoreSet);
/* 563:    */             } else {
/* 564:766 */               key = Conversions.coerce(keyType, key);
/* 565:    */             }
/* 566:769 */             newMap.put(key, value);
/* 567:    */           }
/* 568:771 */           convertedArgumentList.set(index, newMap);
/* 569:772 */           return true;
/* 570:    */         }
/* 571:774 */         break;
/* 572:    */       case 1: 
/* 573:777 */         if (parameterClass.isInstance(item)) {
/* 574:778 */           return true;
/* 575:    */         }
/* 576:781 */         if ((item instanceof Map))
/* 577:    */         {
/* 578:782 */           item = MapObjectConversion.fromMap(respectIgnore, view, fieldsAccessor, (Map)item, parameterClass, ignoreSet);
/* 579:783 */           convertedArgumentList.set(index, item);
/* 580:784 */           return true;
/* 581:    */         }
/* 582:785 */         if ((item instanceof List))
/* 583:    */         {
/* 584:787 */           List<Object> listItem = null;
/* 585:    */           
/* 586:789 */           listItem = (List)item;
/* 587:    */           
/* 588:791 */           Object value = MapObjectConversion.fromList(respectIgnore, view, fieldsAccessor, listItem, parameterClass, ignoreSet);
/* 589:    */           
/* 590:793 */           convertedArgumentList.set(index, value);
/* 591:794 */           return true;
/* 592:    */         }
/* 593:797 */         convertedArgumentList.set(index, Conversions.coerce(parameterClass, item));
/* 594:798 */         return true;
/* 595:    */       case 27: 
/* 596:    */       case 28: 
/* 597:803 */         if (parameterClass.isInstance(item)) {
/* 598:804 */           return true;
/* 599:    */         }
/* 600:807 */         if (!(item instanceof Map)) {
/* 601:    */           break label1566;
/* 602:    */         }
/* 603:810 */         String className = (String)((Map)item).get("class");
/* 604:811 */         if (className != null)
/* 605:    */         {
/* 606:812 */           item = MapObjectConversion.fromMap(respectIgnore, view, fieldsAccessor, (Map)item, Reflection.loadClass(className), ignoreSet);
/* 607:813 */           convertedArgumentList.set(index, item);
/* 608:814 */           return true;
/* 609:    */         }
/* 610:816 */         return false;
/* 611:    */       case 2: 
/* 612:    */       case 29: 
/* 613:    */       case 30: 
/* 614:826 */         if ((item instanceof List))
/* 615:    */         {
/* 616:828 */           List<Object> itemList = (List)item;
/* 617:834 */           if ((itemList.size() > 0) && (((itemList.get(0) instanceof List)) || ((itemList.get(0) instanceof ValueContainer))))
/* 618:    */           {
/* 619:838 */             Type type = methodAccess.getGenericParameterTypes()[index];
/* 620:843 */             if ((type instanceof ParameterizedType))
/* 621:    */             {
/* 622:844 */               ParameterizedType pType = (ParameterizedType)type;
/* 623:    */               Class<?> componentType;
/* 624:    */               Class<?> componentType;
/* 625:848 */               if (!(pType.getActualTypeArguments()[0] instanceof Class)) {
/* 626:849 */                 componentType = Object.class;
/* 627:    */               } else {
/* 628:851 */                 componentType = (Class)pType.getActualTypeArguments()[0];
/* 629:    */               }
/* 630:854 */               Collection newList = Conversions.createCollection(parameterClass, itemList.size());
/* 631:856 */               for (Object o : itemList)
/* 632:    */               {
/* 633:857 */                 if ((o instanceof ValueContainer)) {
/* 634:858 */                   o = ((ValueContainer)o).toValue();
/* 635:    */                 }
/* 636:861 */                 if (componentType == Object.class)
/* 637:    */                 {
/* 638:862 */                   newList.add(o);
/* 639:    */                 }
/* 640:    */                 else
/* 641:    */                 {
/* 642:865 */                   List fromList = (List)o;
/* 643:866 */                   o = MapObjectConversion.fromList(respectIgnore, view, fieldsAccessor, fromList, componentType, ignoreSet);
/* 644:867 */                   newList.add(o);
/* 645:    */                 }
/* 646:    */               }
/* 647:870 */               convertedArgumentList.set(index, newList);
/* 648:871 */               return true;
/* 649:    */             }
/* 650:    */           }
/* 651:    */           else
/* 652:    */           {
/* 653:879 */             Type type = methodAccess.getGenericParameterTypes()[index];
/* 654:880 */             if ((type instanceof ParameterizedType))
/* 655:    */             {
/* 656:881 */               ParameterizedType pType = (ParameterizedType)type;
/* 657:    */               
/* 658:883 */               Class<?> componentType = (pType.getActualTypeArguments()[0] instanceof Class) ? (Class)pType.getActualTypeArguments()[0] : Object.class;
/* 659:    */               
/* 660:885 */               Collection newList = Conversions.createCollection(parameterClass, itemList.size());
/* 661:888 */               for (Object o : itemList)
/* 662:    */               {
/* 663:889 */                 if ((o instanceof ValueContainer)) {
/* 664:890 */                   o = ((ValueContainer)o).toValue();
/* 665:    */                 }
/* 666:892 */                 if ((o instanceof List))
/* 667:    */                 {
/* 668:894 */                   if (componentType != Object.class)
/* 669:    */                   {
/* 670:896 */                     List fromList = (List)o;
/* 671:897 */                     o = MapObjectConversion.fromList(fieldsAccessor, fromList, componentType);
/* 672:    */                   }
/* 673:899 */                   newList.add(o);
/* 674:    */                 }
/* 675:900 */                 else if ((o instanceof Map))
/* 676:    */                 {
/* 677:901 */                   Map fromMap = (Map)o;
/* 678:902 */                   o = MapObjectConversion.fromMap(respectIgnore, view, fieldsAccessor, fromMap, componentType, ignoreSet);
/* 679:903 */                   newList.add(o);
/* 680:    */                 }
/* 681:    */                 else
/* 682:    */                 {
/* 683:906 */                   newList.add(Conversions.coerce(componentType, o));
/* 684:    */                 }
/* 685:    */               }
/* 686:909 */               convertedArgumentList.set(index, newList);
/* 687:910 */               return true;
/* 688:    */             }
/* 689:    */           }
/* 690:    */         }
/* 691:916 */         return false;
/* 692:    */       }
/* 693:920 */       TypeType itemType = TypeType.getInstanceType(item);
/* 694:    */       Object value;
/* 695:922 */       switch (1.$SwitchMap$org$boon$core$TypeType[itemType.ordinal()])
/* 696:    */       {
/* 697:    */       case 2: 
/* 698:924 */         convertedArgumentList.set(index, MapObjectConversion.fromList(respectIgnore, view, fieldsAccessor, (List)item, parameterClass, ignoreSet));
/* 699:    */       case 3: 
/* 700:    */       case 4: 
/* 701:927 */         convertedArgumentList.set(index, MapObjectConversion.fromMap(respectIgnore, view, fieldsAccessor, (Map)item, parameterClass, ignoreSet));
/* 702:    */       case 5: 
/* 703:    */       case 6: 
/* 704:    */       case 7: 
/* 705:    */       case 8: 
/* 706:    */       case 9: 
/* 707:    */       case 10: 
/* 708:    */       case 11: 
/* 709:    */       case 12: 
/* 710:    */       case 13: 
/* 711:    */       case 14: 
/* 712:    */       case 15: 
/* 713:    */       case 16: 
/* 714:    */       case 17: 
/* 715:    */       case 18: 
/* 716:    */       case 19: 
/* 717:    */       case 20: 
/* 718:    */       case 21: 
/* 719:946 */         value = Conversions.coerceWithFlag(parameterClass, flag, item);
/* 720:948 */         if (flag[0] == 0) {
/* 721:949 */           return false;
/* 722:    */         }
/* 723:951 */         convertedArgumentList.set(index, value);
/* 724:952 */         return true;
/* 725:    */       case 22: 
/* 726:    */       case 23: 
/* 727:959 */         value = Conversions.coerceWithFlag(parameterClass, flag, item);
/* 728:961 */         if (flag[0] == 0) {
/* 729:962 */           return false;
/* 730:    */         }
/* 731:964 */         convertedArgumentList.set(index, value);
/* 732:965 */         return true;
/* 733:    */       }
/* 734:    */       label1566:
/* 735:976 */       if (parameterClass.isInstance(item)) {
/* 736:977 */         return true;
/* 737:    */       }
/* 738:    */     }
/* 739:    */     catch (Exception ex)
/* 740:    */     {
/* 741:982 */       Boon.error(ex, new Object[] { "PROBLEM WITH oldMatchAndConvertArgs", "respectIgnore", Boolean.valueOf(respectIgnore), "view", view, "fieldsAccessor", fieldsAccessor, "list", convertedArgumentList, "constructor", methodAccess, "parameters", parameterTypes, "index", Integer.valueOf(index), "ignoreSet", ignoreSet });
/* 742:    */       
/* 743:    */ 
/* 744:    */ 
/* 745:    */ 
/* 746:987 */       return false;
/* 747:    */     }
/* 748:990 */     return false;
/* 749:    */   }
/* 750:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.reflection.Invoker
 * JD-Core Version:    0.7.0.1
 */