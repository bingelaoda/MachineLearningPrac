/*   1:    */ package org.boon.di.impl;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import java.util.Map;
/*   6:    */ import java.util.concurrent.atomic.AtomicReference;
/*   7:    */ import org.boon.Boon;
/*   8:    */ import org.boon.Exceptions;
/*   9:    */ import org.boon.Logger;
/*  10:    */ import org.boon.collections.ConcurrentLinkedHashSet;
/*  11:    */ import org.boon.core.Supplier;
/*  12:    */ import org.boon.core.Typ;
/*  13:    */ import org.boon.core.reflection.BeanUtils;
/*  14:    */ import org.boon.core.reflection.Fields;
/*  15:    */ import org.boon.core.reflection.Invoker;
/*  16:    */ import org.boon.core.reflection.MapObjectConversion;
/*  17:    */ import org.boon.core.reflection.Reflection;
/*  18:    */ import org.boon.core.reflection.fields.FieldAccess;
/*  19:    */ import org.boon.di.Context;
/*  20:    */ import org.boon.di.Module;
/*  21:    */ import org.boon.di.ProviderInfo;
/*  22:    */ import org.boon.json.JsonFactory;
/*  23:    */ import org.boon.logging.LogLevel;
/*  24:    */ import org.boon.logging.TerminalLogger;
/*  25:    */ 
/*  26:    */ public class ContextImpl
/*  27:    */   implements Context, Module
/*  28:    */ {
/*  29: 61 */   protected ConcurrentLinkedHashSet<Module> modules = new ConcurrentLinkedHashSet();
/*  30:    */   private String name;
/*  31: 63 */   private AtomicReference<Context> parent = new AtomicReference();
/*  32: 65 */   private Class<ContextImpl> contextImpl = ContextImpl.class;
/*  33: 66 */   private Logger logger = Boon.configurableLogger(this.contextImpl);
/*  34:    */   private boolean debug;
/*  35:    */   
/*  36:    */   public void initDebug()
/*  37:    */   {
/*  38: 72 */     if (Boon.debugOn())
/*  39:    */     {
/*  40: 73 */       this.logger.level(LogLevel.DEBUG);
/*  41: 74 */       this.logger.tee(new TerminalLogger());
/*  42: 75 */       this.debug = true;
/*  43:    */     }
/*  44: 78 */     if (this.logger.debugOn()) {
/*  45: 79 */       this.debug = true;
/*  46:    */     }
/*  47:    */   }
/*  48:    */   
/*  49:    */   public ContextImpl()
/*  50:    */   {
/*  51: 84 */     initDebug();
/*  52:    */   }
/*  53:    */   
/*  54:    */   public ContextImpl(Module... modules)
/*  55:    */   {
/*  56: 89 */     initDebug();
/*  57: 90 */     for (Module module : modules)
/*  58:    */     {
/*  59: 91 */       module.parent(this);
/*  60: 92 */       this.modules.add(module);
/*  61:    */     }
/*  62:    */   }
/*  63:    */   
/*  64:    */   public Context combine(Context newContext)
/*  65:    */   {
/*  66: 99 */     ContextImpl newContextImpl = (ContextImpl)newContext;
/*  67:101 */     for (Module module : newContextImpl.modules)
/*  68:    */     {
/*  69:102 */       module.parent(this);
/*  70:103 */       this.modules.add(module);
/*  71:    */     }
/*  72:105 */     return this;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public Context combineFirst(Context newContext)
/*  76:    */   {
/*  77:111 */     ContextImpl newContextImpl = (ContextImpl)newContext;
/*  78:113 */     for (Module module : newContextImpl.modules)
/*  79:    */     {
/*  80:114 */       module.parent(this);
/*  81:115 */       this.modules.addFirst(module);
/*  82:    */     }
/*  83:117 */     return this;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public void parent(Context context)
/*  87:    */   {
/*  88:122 */     if (this.debug) {
/*  89:122 */       this.logger.debug(new Object[] { this.contextImpl, "parent" });
/*  90:    */     }
/*  91:123 */     this.parent.set(context);
/*  92:    */   }
/*  93:    */   
/*  94:    */   public Iterable<Object> values()
/*  95:    */   {
/*  96:130 */     if (this.debug) {
/*  97:130 */       this.logger.debug(new Object[] { this.contextImpl, "values()", "IN" });
/*  98:    */     }
/*  99:132 */     List list = new ArrayList();
/* 100:133 */     for (Module m : this.modules) {
/* 101:135 */       for (Object o : m.values()) {
/* 102:136 */         list.add(o);
/* 103:    */       }
/* 104:    */     }
/* 105:141 */     if (this.debug) {
/* 106:141 */       this.logger.debug(new Object[] { this.contextImpl, "values()", "OUT", list });
/* 107:    */     }
/* 108:142 */     return list;
/* 109:    */   }
/* 110:    */   
/* 111:    */   public Iterable<String> names()
/* 112:    */   {
/* 113:147 */     if (this.debug) {
/* 114:147 */       this.logger.debug(new Object[] { this.contextImpl, "names()", "IN" });
/* 115:    */     }
/* 116:150 */     List list = new ArrayList();
/* 117:151 */     for (Module m : this.modules) {
/* 118:153 */       for (String n : m.names()) {
/* 119:154 */         list.add(n);
/* 120:    */       }
/* 121:    */     }
/* 122:158 */     if (this.debug) {
/* 123:158 */       this.logger.debug(new Object[] { this.contextImpl, "names()", "OUT", list });
/* 124:    */     }
/* 125:159 */     return list;
/* 126:    */   }
/* 127:    */   
/* 128:    */   public Iterable<Class<?>> types()
/* 129:    */   {
/* 130:165 */     if (this.debug) {
/* 131:165 */       this.logger.debug(new Object[] { this.contextImpl, "types()", "IN" });
/* 132:    */     }
/* 133:166 */     List list = new ArrayList();
/* 134:167 */     for (Module m : this.modules) {
/* 135:169 */       for (Class<?> c : m.types()) {
/* 136:170 */         list.add(c);
/* 137:    */       }
/* 138:    */     }
/* 139:174 */     if (this.debug) {
/* 140:174 */       this.logger.debug(new Object[] { this.contextImpl, "types()", "OUT", list });
/* 141:    */     }
/* 142:175 */     return list;
/* 143:    */   }
/* 144:    */   
/* 145:    */   public Iterable<Module> children()
/* 146:    */   {
/* 147:181 */     return this.modules;
/* 148:    */   }
/* 149:    */   
/* 150:    */   public void setName(String name)
/* 151:    */   {
/* 152:185 */     this.name = name;
/* 153:    */   }
/* 154:    */   
/* 155:    */   public <T> T get(Class<T> type)
/* 156:    */   {
/* 157:    */     try
/* 158:    */     {
/* 159:195 */       if (this.debug) {
/* 160:195 */         this.logger.debug(new Object[] { this.contextImpl, "get(type)", "IN", type });
/* 161:    */       }
/* 162:197 */       Object object = null;
/* 163:198 */       for (Module module : this.modules) {
/* 164:200 */         if (module.has(type))
/* 165:    */         {
/* 166:201 */           object = module.get(type);
/* 167:202 */           break;
/* 168:    */         }
/* 169:    */       }
/* 170:206 */       resolveProperties(true, object, getProviderInfo(type));
/* 171:209 */       if (this.debug) {
/* 172:209 */         this.logger.debug(new Object[] { this.contextImpl, "get(type)", "OUT", object });
/* 173:    */       }
/* 174:210 */       return object;
/* 175:    */     }
/* 176:    */     catch (Exception ex)
/* 177:    */     {
/* 178:212 */       return Exceptions.handle(Object.class, ex, new Object[] { "Unable to get type", type });
/* 179:    */     }
/* 180:    */   }
/* 181:    */   
/* 182:    */   public <T> T get(Class<T> type, String name)
/* 183:    */   {
/* 184:220 */     if (this.debug) {
/* 185:220 */       this.logger.debug(new Object[] { this.contextImpl, "get(type, name)", "IN", type, name });
/* 186:    */     }
/* 187:    */     try
/* 188:    */     {
/* 189:226 */       T object = null;
/* 190:227 */       for (Module module : this.modules) {
/* 191:229 */         if (module.has(name))
/* 192:    */         {
/* 193:230 */           object = module.get(type, name);
/* 194:231 */           break;
/* 195:    */         }
/* 196:    */       }
/* 197:235 */       resolveProperties(true, object, getProviderInfo(type, name));
/* 198:238 */       if (this.debug) {
/* 199:238 */         this.logger.debug(new Object[] { this.contextImpl, "get(type, name)", "IN", type, name, "OUT", object });
/* 200:    */       }
/* 201:240 */       return object;
/* 202:    */     }
/* 203:    */     catch (Exception ex)
/* 204:    */     {
/* 205:242 */       return Exceptions.handle(Object.class, ex, new Object[] { "Unable to get type", type, name });
/* 206:    */     }
/* 207:    */   }
/* 208:    */   
/* 209:    */   public ProviderInfo getProviderInfo(Class<?> type)
/* 210:    */   {
/* 211:250 */     if (this.debug) {
/* 212:250 */       this.logger.debug(new Object[] { this.contextImpl, "getProviderInfo(type)", "IN", type });
/* 213:    */     }
/* 214:    */     try
/* 215:    */     {
/* 216:253 */       ProviderInfo pi = null;
/* 217:254 */       for (Module module : this.modules) {
/* 218:256 */         if (module.has(type))
/* 219:    */         {
/* 220:257 */           pi = module.getProviderInfo(type);
/* 221:258 */           break;
/* 222:    */         }
/* 223:    */       }
/* 224:263 */       if (this.debug) {
/* 225:263 */         this.logger.debug(new Object[] { this.contextImpl, "getProviderInfo(type)", "IN", type, "OUT", pi });
/* 226:    */       }
/* 227:264 */       return pi;
/* 228:    */     }
/* 229:    */     catch (Exception ex)
/* 230:    */     {
/* 231:266 */       return (ProviderInfo)Exceptions.handle(ProviderInfo.class, ex, new Object[] { "Unable to get type", type });
/* 232:    */     }
/* 233:    */   }
/* 234:    */   
/* 235:    */   public ProviderInfo getProviderInfo(String name)
/* 236:    */   {
/* 237:    */     try
/* 238:    */     {
/* 239:277 */       if (this.debug) {
/* 240:277 */         this.logger.debug(new Object[] { this.contextImpl, "getProviderInfo(name)", "IN", name });
/* 241:    */       }
/* 242:279 */       ProviderInfo pi = null;
/* 243:280 */       for (Module module : this.modules) {
/* 244:282 */         if (module.has(name))
/* 245:    */         {
/* 246:283 */           pi = module.getProviderInfo(name);
/* 247:284 */           break;
/* 248:    */         }
/* 249:    */       }
/* 250:289 */       if (this.debug) {
/* 251:289 */         this.logger.debug(new Object[] { this.contextImpl, "getProviderInfo(name)", "IN", name, "OUT", pi });
/* 252:    */       }
/* 253:290 */       return pi;
/* 254:    */     }
/* 255:    */     catch (Exception ex)
/* 256:    */     {
/* 257:292 */       return (ProviderInfo)Exceptions.handle(ProviderInfo.class, ex, new Object[] { "Unable to get name", name });
/* 258:    */     }
/* 259:    */   }
/* 260:    */   
/* 261:    */   public ProviderInfo getProviderInfo(Class<?> type, String name)
/* 262:    */   {
/* 263:302 */     if (this.debug) {
/* 264:302 */       this.logger.debug(new Object[] { this.contextImpl, "getProviderInfo( type, name )", "IN", type, name });
/* 265:    */     }
/* 266:    */     try
/* 267:    */     {
/* 268:305 */       ProviderInfo pi = null;
/* 269:306 */       for (Module module : this.modules) {
/* 270:308 */         if (module.has(name))
/* 271:    */         {
/* 272:309 */           pi = module.getProviderInfo(type, name);
/* 273:310 */           break;
/* 274:    */         }
/* 275:    */       }
/* 276:315 */       if (this.debug) {
/* 277:315 */         this.logger.debug(new Object[] { this.contextImpl, "getProviderInfo( type, name )", "IN", type, name, "OUT", pi });
/* 278:    */       }
/* 279:316 */       return pi;
/* 280:    */     }
/* 281:    */     catch (Exception ex)
/* 282:    */     {
/* 283:318 */       return (ProviderInfo)Exceptions.handle(ProviderInfo.class, ex, new Object[] { "Unable to get type/name", type, name });
/* 284:    */     }
/* 285:    */   }
/* 286:    */   
/* 287:    */   public boolean has(Class type)
/* 288:    */   {
/* 289:327 */     if (this.debug) {
/* 290:327 */       this.logger.debug(new Object[] { this.contextImpl, "has( type )", "IN", type });
/* 291:    */     }
/* 292:330 */     if (this.debug) {
/* 293:330 */       this.logger.debug(new Object[] { this.contextImpl, "has( type )", "IN", type });
/* 294:    */     }
/* 295:332 */     for (Module module : this.modules) {
/* 296:334 */       if (module.has(type))
/* 297:    */       {
/* 298:336 */         if (this.debug) {
/* 299:336 */           this.logger.debug(new Object[] { this.contextImpl, "has( type )", "IN", type, "OUT", Boolean.valueOf(true) });
/* 300:    */         }
/* 301:337 */         return true;
/* 302:    */       }
/* 303:    */     }
/* 304:342 */     if (this.debug) {
/* 305:342 */       this.logger.debug(new Object[] { this.contextImpl, "has( type )", "IN", type, "OUT", Boolean.valueOf(false) });
/* 306:    */     }
/* 307:343 */     return false;
/* 308:    */   }
/* 309:    */   
/* 310:    */   public boolean has(String name)
/* 311:    */   {
/* 312:349 */     for (Module module : this.modules) {
/* 313:351 */       if (module.has(name))
/* 314:    */       {
/* 315:353 */         if (this.debug) {
/* 316:353 */           this.logger.debug(new Object[] { this.contextImpl, "has( name )", "IN", name, "OUT", Boolean.valueOf(true) });
/* 317:    */         }
/* 318:354 */         return true;
/* 319:    */       }
/* 320:    */     }
/* 321:359 */     if (this.debug) {
/* 322:359 */       this.logger.debug(new Object[] { this.contextImpl, "has( name )", "IN", name, "OUT", Boolean.valueOf(false) });
/* 323:    */     }
/* 324:360 */     return false;
/* 325:    */   }
/* 326:    */   
/* 327:    */   public <T> Supplier<T> getSupplier(final Class<T> type, final String name)
/* 328:    */   {
/* 329:    */     try
/* 330:    */     {
/* 331:370 */       Supplier<T> supplier = null;
/* 332:371 */       for (Module module : this.modules) {
/* 333:373 */         if (module.has(name))
/* 334:    */         {
/* 335:374 */           supplier = module.getSupplier(type, name);
/* 336:375 */           break;
/* 337:    */         }
/* 338:    */       }
/* 339:379 */       final Supplier<T> s = supplier;
/* 340:380 */       final Context resolver = this;
/* 341:    */       
/* 342:    */ 
/* 343:383 */       new Supplier()
/* 344:    */       {
/* 345:384 */         String supplierName = name;
/* 346:385 */         Class<T> supplierType = type;
/* 347:    */         
/* 348:    */         public T get()
/* 349:    */         {
/* 350:389 */           T o = s.get();
/* 351:390 */           resolver.resolveProperties(o);
/* 352:391 */           return o;
/* 353:    */         }
/* 354:    */       };
/* 355:    */     }
/* 356:    */     catch (Exception ex)
/* 357:    */     {
/* 358:397 */       return (Supplier)Exceptions.handle(Supplier.class, ex, new Object[] { "Unable to get type", type, name });
/* 359:    */     }
/* 360:    */   }
/* 361:    */   
/* 362:    */   public <T> Supplier<T> getSupplier(Class<T> type)
/* 363:    */   {
/* 364:    */     try
/* 365:    */     {
/* 366:408 */       Supplier<T> supplier = null;
/* 367:409 */       for (Module module : this.modules) {
/* 368:411 */         if (module.has(type))
/* 369:    */         {
/* 370:412 */           supplier = module.getSupplier(type);
/* 371:413 */           break;
/* 372:    */         }
/* 373:    */       }
/* 374:417 */       final Supplier<T> s = supplier;
/* 375:    */       
/* 376:    */ 
/* 377:420 */       Context resolver = this;
/* 378:    */       
/* 379:422 */       new Supplier()
/* 380:    */       {
/* 381:    */         public T get()
/* 382:    */         {
/* 383:425 */           T o = s.get();
/* 384:426 */           ContextImpl.this.resolveProperties(o);
/* 385:427 */           return o;
/* 386:    */         }
/* 387:    */       };
/* 388:    */     }
/* 389:    */     catch (Exception ex)
/* 390:    */     {
/* 391:433 */       return (Supplier)Exceptions.handle(Supplier.class, ex, new Object[] { "Unable to get type", type });
/* 392:    */     }
/* 393:    */   }
/* 394:    */   
/* 395:    */   private void resolveProperties(boolean enforce, Object object, ProviderInfo info)
/* 396:    */   {
/* 397:440 */     if (this.debug) {
/* 398:440 */       this.logger.debug(new Object[] { this.contextImpl, "resolveProperties(enforce, object, info )", "IN", Boolean.valueOf(enforce), object, info });
/* 399:    */     }
/* 400:444 */     if (object != null)
/* 401:    */     {
/* 402:449 */       if ((Fields.hasField(object, "__init__")) && 
/* 403:450 */         (BeanUtils.idxBoolean(object, "__init__")))
/* 404:    */       {
/* 405:453 */         if (this.debug) {
/* 406:453 */           this.logger.debug(new Object[] { this.contextImpl, "Object was initialized already" });
/* 407:    */         }
/* 408:455 */         return;
/* 409:    */       }
/* 410:459 */       if ((info != null) && (info.isPostConstructCalled()) && (info.value() != null) && (!info.prototype())) {
/* 411:460 */         return;
/* 412:    */       }
/* 413:463 */       Map<String, FieldAccess> fields = Reflection.getAllAccessorFields(object.getClass(), true);
/* 414:464 */       for (FieldAccess field : fields.values()) {
/* 415:466 */         if (field.injectable()) {
/* 416:468 */           handleInjectionOfField(enforce, object, field);
/* 417:    */         }
/* 418:    */       }
/* 419:475 */       if (this.debug) {
/* 420:475 */         this.logger.debug(new Object[] { this.contextImpl, "Invoking post construct start...", object });
/* 421:    */       }
/* 422:476 */       Invoker.invokeMethodWithAnnotationNoReturn(object, "postConstruct");
/* 423:477 */       if (this.debug) {
/* 424:477 */         this.logger.debug(new Object[] { this.contextImpl, "Invoking post construct done...", object });
/* 425:    */       }
/* 426:479 */       if ((info != null) && (info.value() != null) && (!info.prototype()))
/* 427:    */       {
/* 428:481 */         if (this.debug) {
/* 429:481 */           this.logger.debug(new Object[] { this.contextImpl, "Setting post construct property on provider info...", object });
/* 430:    */         }
/* 431:483 */         info.setPostConstructCalled(true);
/* 432:    */       }
/* 433:    */     }
/* 434:487 */     if (this.debug) {
/* 435:487 */       this.logger.debug(new Object[] { this.contextImpl, "resolveProperties(enforce, object, info )", "OUT", Boolean.valueOf(enforce), object, info });
/* 436:    */     }
/* 437:    */   }
/* 438:    */   
/* 439:    */   public void resolveProperties(Object object)
/* 440:    */   {
/* 441:495 */     resolveProperties(true, object, null);
/* 442:    */   }
/* 443:    */   
/* 444:    */   public void resolvePropertiesIgnoreRequired(Object object)
/* 445:    */   {
/* 446:502 */     resolveProperties(false, object, null);
/* 447:    */   }
/* 448:    */   
/* 449:    */   private void handleInjectionOfField(boolean enforce, Object object, FieldAccess field)
/* 450:    */   {
/* 451:508 */     if (this.debug) {
/* 452:508 */       this.logger.debug(new Object[] { this.contextImpl, "handleInjectionOfField(enforce, object, field )", "IN", Boolean.valueOf(enforce), object, field });
/* 453:    */     }
/* 454:    */     try
/* 455:    */     {
/* 456:513 */       Object value = null;
/* 457:515 */       if ((field.type().isPrimitive()) || (Typ.isBasicType(field.type())))
/* 458:    */       {
/* 459:516 */         handleInjectionOfBasicField(enforce, object, field);
/* 460:517 */         return;
/* 461:    */       }
/* 462:521 */       boolean fieldNamed = field.isNamed();
/* 463:522 */       if ((fieldNamed) && (field.type() != Supplier.class)) {
/* 464:523 */         value = get(field.type(), field.named());
/* 465:524 */       } else if ((fieldNamed) && (field.type() == Supplier.class)) {
/* 466:525 */         value = getSupplier(field.getComponentClass(), field.named());
/* 467:    */       } else {
/* 468:527 */         value = get(field.type());
/* 469:    */       }
/* 470:530 */       if ((value == null) && (field.isNamed()))
/* 471:    */       {
/* 472:531 */         value = get(field.named());
/* 473:532 */         if (value != null) {
/* 474:533 */           field.type().isAssignableFrom(value.getClass());
/* 475:    */         }
/* 476:    */       }
/* 477:537 */       if ((enforce) && (field.requiresInjection()) && 
/* 478:538 */         (value == null))
/* 479:    */       {
/* 480:540 */         debug();
/* 481:541 */         Exceptions.die(Boon.sputs(new Object[] { "Unable to inject into", field.name(), " of ", field.parent(), "with alias\n", field.named(), "was named", Boolean.valueOf(field.isNamed()), "field info", field, "\n" }));
/* 482:    */       }
/* 483:549 */       if (this.debug) {
/* 484:549 */         this.logger.debug(new Object[] { this.contextImpl, "handleInjectionOfField(enforce, object, field )", "IN", Boolean.valueOf(enforce), object, field, "\n", "FIELD INJECTION", "into", object, field.name(), "with value", value, "VALUE TYPE", Boon.className(value), field.type() });
/* 485:    */       }
/* 486:553 */       field.setValue(object, value);
/* 487:    */     }
/* 488:    */     catch (Exception ex)
/* 489:    */     {
/* 490:556 */       Exceptions.handle(ex, new Object[] { "handleInjectionOfField failed for ", "enforce", Boolean.valueOf(enforce), "Object", object, "FieldAccess", field });
/* 491:    */     }
/* 492:    */   }
/* 493:    */   
/* 494:    */   private void handleInjectionOfBasicField(boolean enforce, Object object, FieldAccess field)
/* 495:    */   {
/* 496:    */     try
/* 497:    */     {
/* 498:566 */       if (this.debug) {
/* 499:566 */         this.logger.debug(new Object[] { this.contextImpl, "handleInjectionOfBasicField(enforce, object, field )", "IN", Boolean.valueOf(enforce), object, field });
/* 500:    */       }
/* 501:569 */       String name = null;
/* 502:570 */       if (field.isNamed())
/* 503:    */       {
/* 504:571 */         if (this.debug) {
/* 505:571 */           this.logger.debug(new Object[] { this.contextImpl, "handleInjectionOfBasicField", "FIELD IS NAMED" });
/* 506:    */         }
/* 507:573 */         name = field.alias();
/* 508:575 */         if (this.debug) {
/* 509:575 */           this.logger.debug(new Object[] { this.contextImpl, "handleInjectionOfBasicField", "FIELD IS NAMED", "name", name });
/* 510:    */         }
/* 511:    */       }
/* 512:578 */       if (name == null)
/* 513:    */       {
/* 514:580 */         name = field.name();
/* 515:581 */         if (this.debug) {
/* 516:582 */           this.logger.debug(new Object[] { this.contextImpl, "handleInjectionOfBasicField", "USING FIELD NAME AS NAME", "name", name });
/* 517:    */         }
/* 518:    */       }
/* 519:586 */       Object value = get(name);
/* 520:588 */       if (value == null)
/* 521:    */       {
/* 522:589 */         if (this.debug) {
/* 523:590 */           this.logger.debug(new Object[] { this.contextImpl, "handleInjectionOfBasicField", "NAME NOT FOUND IN CONTEXT", "name", name });
/* 524:    */         }
/* 525:592 */         name = Boon.add(new String[] { field.declaringParent().getName(), ".", field.alias() });
/* 526:    */       }
/* 527:595 */       value = get(name);
/* 528:597 */       if (value == null)
/* 529:    */       {
/* 530:598 */         if (this.debug) {
/* 531:599 */           this.logger.debug(new Object[] { this.contextImpl, "handleInjectionOfBasicField", "NAME NOT FOUND IN CONTEXT", "name", name });
/* 532:    */         }
/* 533:601 */         name = Boon.add(new String[] { field.declaringParent().getPackage().getName(), ".", field.alias() });
/* 534:    */       }
/* 535:605 */       value = get(name);
/* 536:607 */       if ((this.debug) && (value == null)) {
/* 537:608 */         this.logger.debug(new Object[] { this.contextImpl, "handleInjectionOfBasicField", "NAME NOT FOUND IN CONTEXT", "name", name });
/* 538:    */       }
/* 539:611 */       if ((enforce) && (value == null) && (field.requiresInjection())) {
/* 540:612 */         Exceptions.die(new Object[] { "Basic field", field.name(), "needs injection for class", field.declaringParent() });
/* 541:    */       }
/* 542:616 */       if (this.debug) {
/* 543:616 */         this.logger.debug(new Object[] { this.contextImpl, "handleInjectionOfBasicField(enforce, object, field )", "IN", Boolean.valueOf(enforce), object, field, "\n", "FIELD INJECTION", "into", object, field.name(), "with value", value, "VALUE TYPE", Boon.className(value), field.type() });
/* 544:    */       }
/* 545:621 */       if (value != null) {
/* 546:622 */         field.setValue(object, value);
/* 547:    */       }
/* 548:625 */       if (this.debug) {
/* 549:625 */         this.logger.debug(new Object[] { this.contextImpl, "handleInjectionOfBasicField(enforce, object, field )", "OUT", Boolean.valueOf(enforce), object, field });
/* 550:    */       }
/* 551:    */     }
/* 552:    */     catch (Exception ex)
/* 553:    */     {
/* 554:629 */       Exceptions.handle(ex, new Object[] { "BASIC handleInjectionOfBasicField failed for ", "enforce", Boolean.valueOf(enforce), "Object", object, "FieldAccess", field });
/* 555:    */     }
/* 556:    */   }
/* 557:    */   
/* 558:    */   public void debug()
/* 559:    */   {
/* 560:638 */     Boon.puts(new Object[] { this, "----debug----" });
/* 561:640 */     if (this.parent.get() != null)
/* 562:    */     {
/* 563:642 */       Boon.puts(new Object[] { this, "delegating to parent----" });
/* 564:643 */       ((Context)this.parent.get()).debug();
/* 565:    */     }
/* 566:    */     else
/* 567:    */     {
/* 568:647 */       displayModuleInfo();
/* 569:    */     }
/* 570:    */   }
/* 571:    */   
/* 572:    */   private void displayModuleInfo()
/* 573:    */   {
/* 574:653 */     int index = 0;
/* 575:655 */     for (Module module : this.modules)
/* 576:    */     {
/* 577:657 */       if ((module instanceof ContextImpl))
/* 578:    */       {
/* 579:658 */         ContextImpl context = (ContextImpl)module;
/* 580:659 */         context.displayModuleInfo();
/* 581:    */       }
/* 582:    */       else
/* 583:    */       {
/* 584:661 */         Boon.puts(new Object[] { Integer.valueOf(index), module });
/* 585:    */         
/* 586:663 */         Boon.puts(new Object[] { "Names:---------------------------" });
/* 587:664 */         for (String name : module.names()) {
/* 588:665 */           Boon.puts(new Object[] { "              ", name });
/* 589:    */         }
/* 590:669 */         Boon.puts(new Object[] { "TypeType--:---------------------------" });
/* 591:670 */         for (Class<?> cls : module.types()) {
/* 592:671 */           Boon.puts(new Object[] { "              ", this.name });
/* 593:    */         }
/* 594:675 */         Boon.puts(new Object[] { "Object--:---------------------------" });
/* 595:676 */         for (Object value : module.values()) {
/* 596:677 */           Boon.puts(new Object[] { "              ", this.name });
/* 597:    */         }
/* 598:    */       }
/* 599:681 */       index++;
/* 600:    */     }
/* 601:    */   }
/* 602:    */   
/* 603:    */   public Object get(String name)
/* 604:    */   {
/* 605:    */     try
/* 606:    */     {
/* 607:691 */       Object object = null;
/* 608:692 */       for (Module module : this.modules) {
/* 609:694 */         if (module.has(name))
/* 610:    */         {
/* 611:695 */           object = module.get(name);
/* 612:696 */           break;
/* 613:    */         }
/* 614:    */       }
/* 615:700 */       if ((object instanceof Map))
/* 616:    */       {
/* 617:701 */         Map map = (Map)object;
/* 618:702 */         if (map.containsKey("class")) {
/* 619:703 */           object = MapObjectConversion.fromMap(map);
/* 620:    */         }
/* 621:    */       }
/* 622:707 */       resolveProperties(true, object, getProviderInfo(name));
/* 623:    */       
/* 624:709 */       return object;
/* 625:    */     }
/* 626:    */     catch (Exception ex)
/* 627:    */     {
/* 628:711 */       return Exceptions.handle(Object.class, ex, new Object[] { "name", name });
/* 629:    */     }
/* 630:    */   }
/* 631:    */   
/* 632:    */   public Object invoke(String objectName, String methodName, Object args)
/* 633:    */   {
/* 634:717 */     Object object = get(objectName);
/* 635:718 */     return Invoker.invokeFromObject(object, methodName, args);
/* 636:    */   }
/* 637:    */   
/* 638:    */   public Object invokeOverload(String objectName, String methodName, Object args)
/* 639:    */   {
/* 640:723 */     Object object = get(objectName);
/* 641:724 */     return Invoker.invokeOverloadedFromObject(object, methodName, args);
/* 642:    */   }
/* 643:    */   
/* 644:    */   public Object invokeFromJson(String objectName, String methodName, String args)
/* 645:    */   {
/* 646:729 */     return invoke(objectName, methodName, JsonFactory.fromJson(args));
/* 647:    */   }
/* 648:    */   
/* 649:    */   public Object invokeOverloadFromJson(String objectName, String methodName, String args)
/* 650:    */   {
/* 651:734 */     return invokeOverload(objectName, methodName, JsonFactory.fromJson(args));
/* 652:    */   }
/* 653:    */   
/* 654:    */   public Context add(Module module)
/* 655:    */   {
/* 656:739 */     module.parent(this);
/* 657:740 */     this.modules.add(module);
/* 658:741 */     return this;
/* 659:    */   }
/* 660:    */   
/* 661:    */   public Context remove(Module module)
/* 662:    */   {
/* 663:746 */     module.parent(null);
/* 664:747 */     this.modules.remove(module);
/* 665:748 */     return this;
/* 666:    */   }
/* 667:    */   
/* 668:    */   public Context addFirst(Module module)
/* 669:    */   {
/* 670:753 */     module.parent(this);
/* 671:754 */     this.modules.addFirst(module);
/* 672:755 */     return this;
/* 673:    */   }
/* 674:    */   
/* 675:    */   public String toString()
/* 676:    */   {
/* 677:761 */     return "ContextImpl{, name='" + this.name + '\'' + '}';
/* 678:    */   }
/* 679:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.di.impl.ContextImpl
 * JD-Core Version:    0.7.0.1
 */