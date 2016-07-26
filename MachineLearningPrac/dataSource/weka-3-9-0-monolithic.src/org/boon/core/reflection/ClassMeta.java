/*   1:    */ package org.boon.core.reflection;
/*   2:    */ 
/*   3:    */ import java.lang.invoke.ConstantCallSite;
/*   4:    */ import java.lang.reflect.Constructor;
/*   5:    */ import java.lang.reflect.Method;
/*   6:    */ import java.lang.reflect.Type;
/*   7:    */ import java.util.ArrayList;
/*   8:    */ import java.util.Collections;
/*   9:    */ import java.util.Iterator;
/*  10:    */ import java.util.LinkedHashSet;
/*  11:    */ import java.util.List;
/*  12:    */ import java.util.Map;
/*  13:    */ import java.util.Set;
/*  14:    */ import java.util.concurrent.ConcurrentHashMap;
/*  15:    */ import org.boon.Exceptions;
/*  16:    */ import org.boon.Lists;
/*  17:    */ import org.boon.collections.MultiMapImpl;
/*  18:    */ import org.boon.core.Typ;
/*  19:    */ import org.boon.core.reflection.fields.FieldAccess;
/*  20:    */ import org.boon.core.reflection.impl.ConstructorAccessImpl;
/*  21:    */ import org.boon.core.reflection.impl.MethodAccessImpl;
/*  22:    */ 
/*  23:    */ public class ClassMeta<T>
/*  24:    */   implements Annotated
/*  25:    */ {
/*  26:    */   final Class<T> cls;
/*  27:    */   final Map<String, MethodAccess> methodMap;
/*  28:    */   final List<ConstructorAccess<T>> constructorAccessSet;
/*  29:    */   final MultiMapImpl<String, MethodAccess> methodsMulti;
/*  30:    */   final List<MethodAccess> methods;
/*  31:    */   final Map<String, FieldAccess> fieldMap;
/*  32:    */   final Map<String, FieldAccess> propertyMap;
/*  33:    */   final List<FieldAccess> fields;
/*  34:    */   final List<FieldAccess> properties;
/*  35:    */   final Set<String> instanceMethods;
/*  36:    */   final Set<String> classMethods;
/*  37:    */   final ConstructorAccess<T> noArgConstructor;
/*  38: 81 */   static final MethodAccess MANY_METHODS = new MethodAccessImpl()
/*  39:    */   {
/*  40:    */     public Object invokeDynamic(Object object, Object... args)
/*  41:    */     {
/*  42: 84 */       return null;
/*  43:    */     }
/*  44:    */     
/*  45:    */     public Object invoke(Object object, Object... args)
/*  46:    */     {
/*  47: 89 */       return Exceptions.die(Object.class, new Object[] { "Unable to invoke method as there are more than one with that same name", object, args });
/*  48:    */     }
/*  49:    */     
/*  50:    */     public boolean respondsTo(Class<?>[] parametersToMatch)
/*  51:    */     {
/*  52: 94 */       return false;
/*  53:    */     }
/*  54:    */     
/*  55:    */     public Iterable<AnnotationData> annotationData()
/*  56:    */     {
/*  57: 99 */       return (Iterable)Exceptions.die(Iterable.class, "Unable to use method as there are more than one with that same name");
/*  58:    */     }
/*  59:    */     
/*  60:    */     public boolean hasAnnotation(String annotationName)
/*  61:    */     {
/*  62:104 */       return ((Boolean)Exceptions.die(Boolean.class, "Unable to invoke method as there are more than one with that same name")).booleanValue();
/*  63:    */     }
/*  64:    */     
/*  65:    */     public AnnotationData annotation(String annotationName)
/*  66:    */     {
/*  67:109 */       return (AnnotationData)Exceptions.die(AnnotationData.class, "Unable to invoke method as there are more than one with that same name");
/*  68:    */     }
/*  69:    */     
/*  70:    */     public Class<?>[] parameterTypes()
/*  71:    */     {
/*  72:114 */       return (Class[])Exceptions.die([Ljava.lang.Class.class, "Unable to invoke method as there are more than one with that same name");
/*  73:    */     }
/*  74:    */     
/*  75:    */     public Type[] getGenericParameterTypes()
/*  76:    */     {
/*  77:119 */       return (Type[])Exceptions.die([Ljava.lang.reflect.Type.class, "Unable to invoke method as there are more than one with that same name");
/*  78:    */     }
/*  79:    */   };
/*  80:    */   private final Map<String, AnnotationData> annotationMap;
/*  81:    */   private final List<AnnotationData> annotations;
/*  82:    */   
/*  83:    */   public Set<String> instanceMethods()
/*  84:    */   {
/*  85:127 */     return new LinkedHashSet(this.instanceMethods);
/*  86:    */   }
/*  87:    */   
/*  88:    */   public Set<String> classMethods()
/*  89:    */   {
/*  90:132 */     return new LinkedHashSet(this.classMethods);
/*  91:    */   }
/*  92:    */   
/*  93:    */   public ClassMeta(Class<T> cls)
/*  94:    */   {
/*  95:137 */     Exceptions.requireNonNull(cls);
/*  96:    */     
/*  97:139 */     this.cls = cls;
/*  98:141 */     if (!cls.isInterface())
/*  99:    */     {
/* 100:143 */       this.fieldMap = Reflection.getAllAccessorFields(this.cls);
/* 101:144 */       this.fields = Lists.list(this.fieldMap.values());
/* 102:    */     }
/* 103:    */     else
/* 104:    */     {
/* 105:146 */       this.fieldMap = Collections.EMPTY_MAP;
/* 106:147 */       this.fields = Collections.EMPTY_LIST;
/* 107:    */     }
/* 108:149 */     this.propertyMap = Reflection.getPropertyFieldAccessors(this.cls);
/* 109:150 */     this.properties = Lists.list(this.propertyMap.values());
/* 110:    */     
/* 111:    */ 
/* 112:153 */     Constructor<?>[] constructors = cls.getDeclaredConstructors();
/* 113:    */     
/* 114:    */ 
/* 115:156 */     ConstructorAccess noArg = null;
/* 116:    */     
/* 117:158 */     Set set = new LinkedHashSet();
/* 118:160 */     for (Constructor constructor : constructors)
/* 119:    */     {
/* 120:161 */       if (constructor.getParameterTypes().length == 0) {
/* 121:162 */         noArg = new ConstructorAccessImpl(constructor);
/* 122:    */       }
/* 123:164 */       set.add(new ConstructorAccessImpl(constructor));
/* 124:    */     }
/* 125:168 */     this.noArgConstructor = noArg;
/* 126:    */     
/* 127:170 */     this.constructorAccessSet = Lists.safeList(set);
/* 128:    */     
/* 129:172 */     List<Class<?>> classes = getBaseClassesSuperFirst(cls);
/* 130:    */     
/* 131:    */ 
/* 132:    */ 
/* 133:176 */     this.methodMap = new ConcurrentHashMap();
/* 134:177 */     this.methodsMulti = new MultiMapImpl();
/* 135:178 */     this.instanceMethods = new LinkedHashSet();
/* 136:179 */     this.classMethods = new LinkedHashSet();
/* 137:183 */     for (Class clasz : classes)
/* 138:    */     {
/* 139:184 */       Method[] methods_ = clasz.getDeclaredMethods();
/* 140:186 */       for (Method m : methods_)
/* 141:    */       {
/* 142:187 */         if (this.methodMap.containsKey(m.getName()))
/* 143:    */         {
/* 144:190 */           MethodAccessImpl invoker = (MethodAccessImpl)this.methodMap.get(m.getName());
/* 145:191 */           if (invoker != MANY_METHODS) {
/* 146:194 */             if (invoker.method.getParameterTypes().length != m.getParameterTypes().length)
/* 147:    */             {
/* 148:195 */               this.methodMap.put(m.getName(), MANY_METHODS);
/* 149:    */             }
/* 150:    */             else
/* 151:    */             {
/* 152:197 */               boolean match = true;
/* 153:198 */               for (int index = 0; index < m.getParameterTypes().length; index++) {
/* 154:199 */                 if (m.getParameterTypes()[index] != invoker.method.getParameterTypes()[index]) {
/* 155:200 */                   match = false;
/* 156:    */                 }
/* 157:    */               }
/* 158:204 */               if (match) {
/* 159:205 */                 this.methodMap.put(m.getName(), new MethodAccessImpl(m));
/* 160:    */               } else {
/* 161:208 */                 this.methodMap.put(m.getName(), MANY_METHODS);
/* 162:    */               }
/* 163:    */             }
/* 164:    */           }
/* 165:    */         }
/* 166:    */         else
/* 167:    */         {
/* 168:213 */           this.methodMap.put(m.getName(), new MethodAccessImpl(m));
/* 169:    */         }
/* 170:216 */         MethodAccessImpl mai = new MethodAccessImpl(m);
/* 171:218 */         if (!mai.isStatic()) {
/* 172:220 */           this.instanceMethods.add(mai.name());
/* 173:    */         } else {
/* 174:223 */           this.classMethods.add(mai.name());
/* 175:    */         }
/* 176:226 */         this.methodsMulti.put(m.getName(), mai);
/* 177:    */       }
/* 178:    */     }
/* 179:230 */     this.methods = Lists.list(this.methodsMulti.values());
/* 180:    */     
/* 181:    */ 
/* 182:    */ 
/* 183:234 */     this.annotationMap = Annotations.getAnnotationDataForClassAsMap(cls);
/* 184:235 */     this.annotations = Annotations.getAnnotationDataForClass(cls);
/* 185:    */   }
/* 186:    */   
/* 187:    */   public static <T> ClassMeta<T> classMeta(Class<T> aClass)
/* 188:    */   {
/* 189:240 */     ClassMeta meta = (ClassMeta)Reflection.context()._classMetaMap.get(aClass);
/* 190:241 */     if (meta == null)
/* 191:    */     {
/* 192:242 */       meta = new ClassMeta(aClass);
/* 193:243 */       Reflection.context()._classMetaMap.put(aClass, meta);
/* 194:    */     }
/* 195:245 */     return meta;
/* 196:    */   }
/* 197:    */   
/* 198:    */   public static ClassMeta<?> classMetaUnTyped(Class<?> aClass)
/* 199:    */   {
/* 200:250 */     ClassMeta meta = (ClassMeta)Reflection.context()._classMetaMap.get(aClass);
/* 201:251 */     if (meta == null)
/* 202:    */     {
/* 203:252 */       meta = new ClassMeta(aClass);
/* 204:253 */       Reflection.context()._classMetaMap.put(aClass, meta);
/* 205:    */     }
/* 206:255 */     return meta;
/* 207:    */   }
/* 208:    */   
/* 209:    */   public static ClassMeta classMetaEither(Object obj)
/* 210:    */   {
/* 211:260 */     if ((obj instanceof Class)) {
/* 212:261 */       return classMeta((Class)obj);
/* 213:    */     }
/* 214:263 */     return classMeta(obj.getClass());
/* 215:    */   }
/* 216:    */   
/* 217:    */   public MethodAccess method(String name)
/* 218:    */   {
/* 219:268 */     return (MethodAccess)this.methodMap.get(name);
/* 220:    */   }
/* 221:    */   
/* 222:    */   public Iterable<MethodAccess> methods(String name)
/* 223:    */   {
/* 224:273 */     return this.methodsMulti.getAll(name);
/* 225:    */   }
/* 226:    */   
/* 227:    */   private List<Class<?>> getBaseClassesSuperFirst(Class<?> cls)
/* 228:    */   {
/* 229:278 */     if (!cls.isInterface())
/* 230:    */     {
/* 231:279 */       List<Class<?>> classes = new ArrayList(10);
/* 232:280 */       Class<?> currentClass = cls;
/* 233:281 */       while (currentClass != Object.class)
/* 234:    */       {
/* 235:282 */         classes.add(currentClass);
/* 236:283 */         currentClass = currentClass.getSuperclass();
/* 237:    */       }
/* 238:285 */       Collections.reverse(classes);
/* 239:    */       
/* 240:287 */       return classes;
/* 241:    */     }
/* 242:289 */     List<Class<?>> classes = Lists.list(cls.getInterfaces());
/* 243:290 */     classes.add(cls);
/* 244:291 */     return classes;
/* 245:    */   }
/* 246:    */   
/* 247:    */   public Map<String, FieldAccess> fieldMap()
/* 248:    */   {
/* 249:299 */     return this.fieldMap;
/* 250:    */   }
/* 251:    */   
/* 252:    */   public Map<String, FieldAccess> propertyMap()
/* 253:    */   {
/* 254:303 */     return this.propertyMap;
/* 255:    */   }
/* 256:    */   
/* 257:    */   public Iterator<FieldAccess> fields()
/* 258:    */   {
/* 259:307 */     return this.fields.iterator();
/* 260:    */   }
/* 261:    */   
/* 262:    */   public Iterable<MethodAccess> methods()
/* 263:    */   {
/* 264:312 */     new Iterable()
/* 265:    */     {
/* 266:    */       public Iterator<MethodAccess> iterator()
/* 267:    */       {
/* 268:315 */         return ClassMeta.this.methods.iterator();
/* 269:    */       }
/* 270:    */     };
/* 271:    */   }
/* 272:    */   
/* 273:    */   public Iterator<FieldAccess> properties()
/* 274:    */   {
/* 275:321 */     return this.properties.iterator();
/* 276:    */   }
/* 277:    */   
/* 278:    */   public Iterable<ConstructorAccess<T>> constructors()
/* 279:    */   {
/* 280:327 */     new Iterable()
/* 281:    */     {
/* 282:    */       public Iterator<ConstructorAccess<T>> iterator()
/* 283:    */       {
/* 284:330 */         return ClassMeta.this.constructorAccessSet.iterator();
/* 285:    */       }
/* 286:    */     };
/* 287:    */   }
/* 288:    */   
/* 289:    */   public ConstructorAccess<T> noArgConstructor()
/* 290:    */   {
/* 291:336 */     return this.noArgConstructor;
/* 292:    */   }
/* 293:    */   
/* 294:    */   public <T> ConstructorAccess<T> declaredConstructor(Class<? extends Object> singleArg)
/* 295:    */   {
/* 296:340 */     for (ConstructorAccess constructorAccess : this.constructorAccessSet) {
/* 297:341 */       if ((constructorAccess.parameterTypes().length == 1) && 
/* 298:342 */         (constructorAccess.parameterTypes()[0].isAssignableFrom(singleArg))) {
/* 299:343 */         return constructorAccess;
/* 300:    */       }
/* 301:    */     }
/* 302:347 */     return null;
/* 303:    */   }
/* 304:    */   
/* 305:    */   public List<ConstructorAccess> oneArgumentConstructors()
/* 306:    */   {
/* 307:353 */     List<ConstructorAccess> constructors = new ArrayList();
/* 308:354 */     for (ConstructorAccess constructorAccess : this.constructorAccessSet) {
/* 309:355 */       if (constructorAccess.parameterTypes().length == 1) {
/* 310:357 */         constructors.add(constructorAccess);
/* 311:    */       }
/* 312:    */     }
/* 313:361 */     return constructors;
/* 314:    */   }
/* 315:    */   
/* 316:    */   public Iterable<AnnotationData> annotationData()
/* 317:    */   {
/* 318:366 */     new Iterable()
/* 319:    */     {
/* 320:    */       public Iterator<AnnotationData> iterator()
/* 321:    */       {
/* 322:369 */         return ClassMeta.this.annotations.iterator();
/* 323:    */       }
/* 324:    */     };
/* 325:    */   }
/* 326:    */   
/* 327:    */   public boolean hasAnnotation(String annotationName)
/* 328:    */   {
/* 329:375 */     return this.annotationMap.containsKey(annotationName);
/* 330:    */   }
/* 331:    */   
/* 332:    */   public AnnotationData annotation(String annotationName)
/* 333:    */   {
/* 334:379 */     return (AnnotationData)this.annotationMap.get(annotationName);
/* 335:    */   }
/* 336:    */   
/* 337:    */   public boolean respondsTo(String methodName)
/* 338:    */   {
/* 339:384 */     return this.methodMap.containsKey(methodName);
/* 340:    */   }
/* 341:    */   
/* 342:    */   public boolean respondsTo(String methodName, Class<?>... types)
/* 343:    */   {
/* 344:390 */     Iterable<MethodAccess> methods = this.methodsMulti.getAll(methodName);
/* 345:391 */     for (MethodAccess methodAccess : methods) {
/* 346:392 */       if (!methodAccess.isStatic()) {
/* 347:393 */         if (methodAccess.respondsTo(types)) {
/* 348:394 */           return true;
/* 349:    */         }
/* 350:    */       }
/* 351:    */     }
/* 352:397 */     return false;
/* 353:    */   }
/* 354:    */   
/* 355:    */   public boolean respondsTo(String methodName, Object... args)
/* 356:    */   {
/* 357:404 */     Iterable<MethodAccess> methods = this.methodsMulti.getAll(methodName);
/* 358:405 */     for (MethodAccess methodAccess : methods) {
/* 359:406 */       if (!methodAccess.isStatic()) {
/* 360:407 */         if (methodAccess.respondsTo(args)) {
/* 361:408 */           return true;
/* 362:    */         }
/* 363:    */       }
/* 364:    */     }
/* 365:411 */     return false;
/* 366:    */   }
/* 367:    */   
/* 368:    */   public boolean respondsTo(String methodName, List list)
/* 369:    */   {
/* 370:419 */     Object[] args = list.toArray(new Object[list.size()]);
/* 371:420 */     return respondsTo(methodName, args);
/* 372:    */   }
/* 373:    */   
/* 374:    */   public boolean handles(Class<?> interfaceMethods)
/* 375:    */   {
/* 376:425 */     Method[] declaredMethods = interfaceMethods.getDeclaredMethods();
/* 377:426 */     for (Method method : declaredMethods) {
/* 378:427 */       if (!respondsTo(method.getName(), method.getParameterTypes())) {
/* 379:428 */         return false;
/* 380:    */       }
/* 381:    */     }
/* 382:431 */     return true;
/* 383:    */   }
/* 384:    */   
/* 385:    */   public Object invoke(T instance, String methodName, Object... args)
/* 386:    */   {
/* 387:436 */     return ((MethodAccess)this.methodMap.get(methodName)).invoke(instance, args);
/* 388:    */   }
/* 389:    */   
/* 390:    */   public Object invokeUntyped(Object instance, String methodName, Object... args)
/* 391:    */   {
/* 392:440 */     return ((MethodAccess)this.methodMap.get(methodName)).invoke(instance, args);
/* 393:    */   }
/* 394:    */   
/* 395:    */   public MethodAccess invokeMethodAccess(String methodName)
/* 396:    */   {
/* 397:445 */     return (MethodAccess)this.methodMap.get(methodName);
/* 398:    */   }
/* 399:    */   
/* 400:    */   public Object invokeStatic(String methodName, Object... args)
/* 401:    */   {
/* 402:449 */     return ((MethodAccess)this.methodMap.get(methodName)).invokeStatic(args);
/* 403:    */   }
/* 404:    */   
/* 405:    */   public Object invoke(T instance, String methodName, List<?> args)
/* 406:    */   {
/* 407:455 */     Object[] array = args.toArray(new Object[args.size()]);
/* 408:456 */     return ((MethodAccess)this.methodMap.get(methodName)).invoke(instance, array);
/* 409:    */   }
/* 410:    */   
/* 411:    */   public boolean invokePredicate(Object instance, Object arg)
/* 412:    */   {
/* 413:461 */     MethodAccess methodAccess = null;
/* 414:463 */     if (this.methods.size() == 1) {
/* 415:464 */       methodAccess = (MethodAccess)this.methods.get(0);
/* 416:    */     } else {
/* 417:466 */       methodAccess = (MethodAccess)this.methodMap.get("test");
/* 418:    */     }
/* 419:469 */     return ((Boolean)methodAccess.invoke(instance, new Object[] { arg })).booleanValue();
/* 420:    */   }
/* 421:    */   
/* 422:    */   public Object invokeReducer(Object instance, Object sum, Object value)
/* 423:    */   {
/* 424:    */     MethodAccess methodAccess;
/* 425:    */     MethodAccess methodAccess;
/* 426:476 */     if (this.methods.size() == 1) {
/* 427:477 */       methodAccess = (MethodAccess)this.methods.get(0);
/* 428:    */     } else {
/* 429:479 */       methodAccess = (MethodAccess)this.methodMap.get("test");
/* 430:    */     }
/* 431:482 */     Class<?> arg1 = methodAccess.parameterTypes()[0];
/* 432:483 */     if ((Typ.isPrimitiveNumber(arg1)) && (sum == null)) {
/* 433:484 */       return methodAccess.invoke(instance, new Object[] { Integer.valueOf(0), value });
/* 434:    */     }
/* 435:486 */     return methodAccess.invoke(instance, new Object[] { sum, value });
/* 436:    */   }
/* 437:    */   
/* 438:    */   public Object invokeFunction(Object instance, Object arg)
/* 439:    */   {
/* 440:492 */     MethodAccess methodAccess = invokeFunctionMethodAccess();
/* 441:493 */     return methodAccess.invoke(instance, new Object[] { arg });
/* 442:    */   }
/* 443:    */   
/* 444:    */   public MethodAccess invokeFunctionMethodAccess()
/* 445:    */   {
/* 446:499 */     if (this.methods.size() == 1) {
/* 447:500 */       return ((MethodAccess)this.methods.get(0)).methodAccess();
/* 448:    */     }
/* 449:502 */     return ((MethodAccess)this.methodMap.get("apply")).methodAccess();
/* 450:    */   }
/* 451:    */   
/* 452:    */   public String name()
/* 453:    */   {
/* 454:507 */     return this.cls.getSimpleName();
/* 455:    */   }
/* 456:    */   
/* 457:    */   public Class<T> cls()
/* 458:    */   {
/* 459:512 */     return this.cls;
/* 460:    */   }
/* 461:    */   
/* 462:    */   public String longName()
/* 463:    */   {
/* 464:517 */     return this.cls.getName();
/* 465:    */   }
/* 466:    */   
/* 467:    */   public ConstantCallSite invokeReducerLongIntReturnLongMethodHandle(Object object)
/* 468:    */   {
/* 469:    */     MethodAccess methodAccess;
/* 470:    */     MethodAccess methodAccess;
/* 471:523 */     if (this.methods.size() == 1) {
/* 472:524 */       methodAccess = (MethodAccess)this.methods.get(0);
/* 473:    */     } else {
/* 474:526 */       methodAccess = (MethodAccess)this.methodMap.get("reduce");
/* 475:    */     }
/* 476:528 */     ConstantCallSite methodHandle = methodAccess.invokeReducerLongIntReturnLongMethodHandle(object);
/* 477:529 */     return methodHandle;
/* 478:    */   }
/* 479:    */   
/* 480:    */   public ConstantCallSite invokeReducerLongIntReturnLongMethodHandle(Object object, String methodName)
/* 481:    */   {
/* 482:534 */     MethodAccess methodAccess = (MethodAccess)this.methodMap.get(methodName);
/* 483:535 */     ConstantCallSite methodHandle = methodAccess.invokeReducerLongIntReturnLongMethodHandle(object);
/* 484:536 */     return methodHandle;
/* 485:    */   }
/* 486:    */   
/* 487:    */   public Method invokeReducerLongIntReturnLongMethod(Object object)
/* 488:    */   {
/* 489:    */     MethodAccess methodAccess;
/* 490:    */     MethodAccess methodAccess;
/* 491:543 */     if (this.methods.size() == 1) {
/* 492:544 */       methodAccess = (MethodAccess)this.methods.get(0);
/* 493:    */     } else {
/* 494:546 */       methodAccess = (MethodAccess)this.methodMap.get("reduce");
/* 495:    */     }
/* 496:548 */     return methodAccess.method();
/* 497:    */   }
/* 498:    */   
/* 499:    */   public Method invokeReducerLongIntReturnLongMethod(Object object, String methodName)
/* 500:    */   {
/* 501:553 */     MethodAccess methodAccess = (MethodAccess)this.methodMap.get(methodName);
/* 502:554 */     return methodAccess.method();
/* 503:    */   }
/* 504:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.reflection.ClassMeta
 * JD-Core Version:    0.7.0.1
 */