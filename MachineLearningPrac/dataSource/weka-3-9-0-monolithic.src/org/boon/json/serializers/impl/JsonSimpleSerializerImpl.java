/*   1:    */ package org.boon.json.serializers.impl;
/*   2:    */ 
/*   3:    */ import java.lang.reflect.Array;
/*   4:    */ import java.math.BigDecimal;
/*   5:    */ import java.math.BigInteger;
/*   6:    */ import java.util.ArrayList;
/*   7:    */ import java.util.Collection;
/*   8:    */ import java.util.Currency;
/*   9:    */ import java.util.Date;
/*  10:    */ import java.util.List;
/*  11:    */ import java.util.Map;
/*  12:    */ import java.util.Map.Entry;
/*  13:    */ import java.util.Set;
/*  14:    */ import java.util.TimeZone;
/*  15:    */ import java.util.concurrent.ConcurrentHashMap;
/*  16:    */ import org.boon.Boon;
/*  17:    */ import org.boon.Exceptions;
/*  18:    */ import org.boon.Maps;
/*  19:    */ import org.boon.Str;
/*  20:    */ import org.boon.cache.SimpleCache;
/*  21:    */ import org.boon.core.TypeType;
/*  22:    */ import org.boon.core.reflection.FastStringUtils;
/*  23:    */ import org.boon.core.reflection.Invoker;
/*  24:    */ import org.boon.core.reflection.Reflection;
/*  25:    */ import org.boon.core.reflection.fields.FieldAccess;
/*  26:    */ import org.boon.json.serializers.JsonSerializerInternal;
/*  27:    */ import org.boon.primitive.CharBuf;
/*  28:    */ 
/*  29:    */ public class JsonSimpleSerializerImpl
/*  30:    */   implements JsonSerializerInternal
/*  31:    */ {
/*  32: 62 */   private final Map<Class<?>, Map<String, FieldAccess>> fieldMap = new ConcurrentHashMap();
/*  33:    */   private final String view;
/*  34:    */   private final boolean encodeStrings;
/*  35:    */   private final boolean serializeAsSupport;
/*  36:    */   private final CharBuf builder;
/*  37:    */   private int level;
/*  38:    */   private boolean asciiOnly;
/*  39:    */   SimpleCache<String, String> stringCache;
/*  40:    */   CharBuf encodedJsonChars;
/*  41:    */   
/*  42:    */   public JsonSimpleSerializerImpl()
/*  43:    */   {
/*  44: 77 */     this.view = null;
/*  45: 78 */     this.encodeStrings = true;
/*  46: 79 */     this.serializeAsSupport = true;
/*  47: 80 */     this.builder = CharBuf.create(4000);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public JsonSimpleSerializerImpl(boolean encodeStrings)
/*  51:    */   {
/*  52: 88 */     this.view = null;
/*  53: 89 */     this.encodeStrings = encodeStrings;
/*  54:    */     
/*  55: 91 */     this.serializeAsSupport = true;
/*  56: 92 */     this.builder = CharBuf.create(4000);
/*  57:    */   }
/*  58:    */   
/*  59:    */   public JsonSimpleSerializerImpl(String view, boolean encodeStrings, boolean serializeAsSupport, boolean asciiOnly)
/*  60:    */   {
/*  61:103 */     this.encodeStrings = encodeStrings;
/*  62:104 */     this.serializeAsSupport = serializeAsSupport;
/*  63:105 */     this.view = view;
/*  64:106 */     this.builder = CharBuf.create(4000);
/*  65:107 */     this.asciiOnly = asciiOnly;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public final void serializeString(String str, CharBuf builder)
/*  69:    */   {
/*  70:120 */     if (this.encodeStrings)
/*  71:    */     {
/*  72:122 */       if (this.stringCache == null)
/*  73:    */       {
/*  74:123 */         this.stringCache = new SimpleCache(1000);
/*  75:124 */         this.encodedJsonChars = CharBuf.create(str.length());
/*  76:    */       }
/*  77:128 */       String encodedString = (String)this.stringCache.get(str);
/*  78:129 */       if (encodedString == null)
/*  79:    */       {
/*  80:133 */         this.encodedJsonChars.asJsonString(str, this.asciiOnly);
/*  81:134 */         encodedString = this.encodedJsonChars.toStringAndRecycle();
/*  82:135 */         this.stringCache.put(str, encodedString);
/*  83:    */       }
/*  84:138 */       builder.add(encodedString);
/*  85:    */     }
/*  86:    */     else
/*  87:    */     {
/*  88:141 */       builder.addQuoted(str);
/*  89:    */     }
/*  90:    */   }
/*  91:    */   
/*  92:    */   public CharBuf serialize(Object obj)
/*  93:    */   {
/*  94:148 */     this.level = 0;
/*  95:    */     
/*  96:150 */     this.builder.readForRecycle();
/*  97:    */     try
/*  98:    */     {
/*  99:152 */       serializeObject(obj, this.builder);
/* 100:    */     }
/* 101:    */     catch (Exception ex)
/* 102:    */     {
/* 103:154 */       return (CharBuf)Exceptions.handle(CharBuf.class, "unable to serializeObject", ex);
/* 104:    */     }
/* 105:156 */     return this.builder;
/* 106:    */   }
/* 107:    */   
/* 108:    */   public void serialize(CharBuf builder, Object obj)
/* 109:    */   {
/* 110:161 */     this.level = 0;
/* 111:    */     try
/* 112:    */     {
/* 113:164 */       serializeObject(obj, builder);
/* 114:    */     }
/* 115:    */     catch (Exception ex)
/* 116:    */     {
/* 117:166 */       Exceptions.handle("unable to serializeObject", ex);
/* 118:    */     }
/* 119:    */   }
/* 120:    */   
/* 121:    */   public final boolean serializeField(Object parent, FieldAccess fieldAccess, CharBuf builder)
/* 122:    */   {
/* 123:175 */     String fieldName = fieldAccess.name();
/* 124:176 */     TypeType typeEnum = fieldAccess.typeEnum();
/* 125:182 */     if ((this.view != null) && 
/* 126:183 */       (!fieldAccess.isViewActive(this.view))) {
/* 127:184 */       return false;
/* 128:    */     }
/* 129:188 */     switch (1.$SwitchMap$org$boon$core$TypeType[typeEnum.ordinal()])
/* 130:    */     {
/* 131:    */     case 1: 
/* 132:191 */       int value = fieldAccess.getInt(parent);
/* 133:192 */       if (value != 0)
/* 134:    */       {
/* 135:193 */         serializeFieldName(fieldName, builder);
/* 136:194 */         builder.addInt(value);
/* 137:195 */         return true;
/* 138:    */       }
/* 139:197 */       return false;
/* 140:    */     case 2: 
/* 141:199 */       boolean bvalue = fieldAccess.getBoolean(parent);
/* 142:200 */       if (bvalue)
/* 143:    */       {
/* 144:201 */         serializeFieldName(fieldName, builder);
/* 145:202 */         builder.addBoolean(bvalue);
/* 146:203 */         return true;
/* 147:    */       }
/* 148:205 */       return false;
/* 149:    */     case 3: 
/* 150:208 */       byte byvalue = fieldAccess.getByte(parent);
/* 151:209 */       if (byvalue != 0)
/* 152:    */       {
/* 153:210 */         serializeFieldName(fieldName, builder);
/* 154:211 */         builder.addByte(byvalue);
/* 155:212 */         return true;
/* 156:    */       }
/* 157:214 */       return false;
/* 158:    */     case 4: 
/* 159:216 */       long lvalue = fieldAccess.getLong(parent);
/* 160:217 */       if (lvalue != 0L)
/* 161:    */       {
/* 162:218 */         serializeFieldName(fieldName, builder);
/* 163:219 */         builder.addLong(lvalue);
/* 164:220 */         return true;
/* 165:    */       }
/* 166:222 */       return false;
/* 167:    */     case 5: 
/* 168:225 */       double dvalue = fieldAccess.getDouble(parent);
/* 169:226 */       if (dvalue != 0.0D)
/* 170:    */       {
/* 171:227 */         serializeFieldName(fieldName, builder);
/* 172:228 */         builder.addDouble(dvalue);
/* 173:229 */         return true;
/* 174:    */       }
/* 175:231 */       return false;
/* 176:    */     case 6: 
/* 177:233 */       float fvalue = fieldAccess.getFloat(parent);
/* 178:234 */       if (fvalue != 0.0F)
/* 179:    */       {
/* 180:235 */         serializeFieldName(fieldName, builder);
/* 181:236 */         builder.addFloat(fvalue);
/* 182:237 */         return true;
/* 183:    */       }
/* 184:239 */       return false;
/* 185:    */     case 7: 
/* 186:241 */       short svalue = fieldAccess.getShort(parent);
/* 187:242 */       if (svalue != 0)
/* 188:    */       {
/* 189:243 */         serializeFieldName(fieldName, builder);
/* 190:244 */         builder.addShort(svalue);
/* 191:245 */         return true;
/* 192:    */       }
/* 193:247 */       return false;
/* 194:    */     case 8: 
/* 195:249 */       char cvalue = fieldAccess.getChar(parent);
/* 196:250 */       if (cvalue != 0)
/* 197:    */       {
/* 198:251 */         serializeFieldName(fieldName, builder);
/* 199:252 */         builder.addQuoted("" + cvalue);
/* 200:253 */         return true;
/* 201:    */       }
/* 202:255 */       return false;
/* 203:    */     }
/* 204:259 */     Object value = fieldAccess.getObject(parent);
/* 205:261 */     if (value == null) {
/* 206:262 */       return false;
/* 207:    */     }
/* 208:266 */     if (value == parent) {
/* 209:267 */       return false;
/* 210:    */     }
/* 211:270 */     switch (1.$SwitchMap$org$boon$core$TypeType[typeEnum.ordinal()])
/* 212:    */     {
/* 213:    */     case 9: 
/* 214:272 */       serializeFieldName(fieldName, builder);
/* 215:273 */       builder.addBigDecimal((BigDecimal)value);
/* 216:274 */       return true;
/* 217:    */     case 10: 
/* 218:276 */       Number nvalue = (Number)fieldAccess.getObject(parent);
/* 219:277 */       if (nvalue.intValue() != 0)
/* 220:    */       {
/* 221:278 */         serializeFieldName(fieldName, builder);
/* 222:279 */         builder.addString(nvalue.toString());
/* 223:280 */         return true;
/* 224:    */       }
/* 225:282 */       return false;
/* 226:    */     case 11: 
/* 227:284 */       serializeFieldName(fieldName, builder);
/* 228:285 */       builder.addBigInteger((BigInteger)value);
/* 229:286 */       return true;
/* 230:    */     case 12: 
/* 231:288 */       serializeFieldName(fieldName, builder);
/* 232:289 */       serializeDate((Date)value, builder);
/* 233:290 */       return true;
/* 234:    */     case 13: 
/* 235:292 */       serializeFieldName(fieldName, builder);
/* 236:293 */       serializeString((String)value, builder);
/* 237:294 */       return true;
/* 238:    */     case 14: 
/* 239:296 */       serializeFieldName(fieldName, builder);
/* 240:297 */       builder.addQuoted(((Class)value).getName());
/* 241:298 */       return true;
/* 242:    */     case 15: 
/* 243:301 */       serializeFieldName(fieldName, builder);
/* 244:302 */       serializeString(value.toString(), builder);
/* 245:303 */       return true;
/* 246:    */     case 16: 
/* 247:305 */       serializeFieldName(fieldName, builder);
/* 248:306 */       builder.addInt((Integer)value);
/* 249:307 */       return true;
/* 250:    */     case 17: 
/* 251:309 */       serializeFieldName(fieldName, builder);
/* 252:310 */       builder.addLong((Long)value);
/* 253:311 */       return true;
/* 254:    */     case 18: 
/* 255:313 */       serializeFieldName(fieldName, builder);
/* 256:314 */       builder.addFloat((Float)value);
/* 257:315 */       return true;
/* 258:    */     case 19: 
/* 259:317 */       serializeFieldName(fieldName, builder);
/* 260:318 */       builder.addDouble((Double)value);
/* 261:319 */       return true;
/* 262:    */     case 20: 
/* 263:321 */       serializeFieldName(fieldName, builder);
/* 264:322 */       builder.addShort(((Short)value).shortValue());
/* 265:323 */       return true;
/* 266:    */     case 21: 
/* 267:325 */       serializeFieldName(fieldName, builder);
/* 268:326 */       builder.addByte(((Byte)value).byteValue());
/* 269:327 */       return true;
/* 270:    */     case 22: 
/* 271:329 */       serializeFieldName(fieldName, builder);
/* 272:330 */       builder.addQuoted(((Character)value).toString());
/* 273:331 */       return true;
/* 274:    */     case 23: 
/* 275:333 */       serializeFieldName(fieldName, builder);
/* 276:334 */       builder.addQuoted(value.toString());
/* 277:335 */       return true;
/* 278:    */     case 24: 
/* 279:    */     case 25: 
/* 280:    */     case 26: 
/* 281:339 */       Collection collection = (Collection)value;
/* 282:340 */       if (collection.size() > 0)
/* 283:    */       {
/* 284:341 */         serializeFieldName(fieldName, builder);
/* 285:342 */         serializeCollection(collection, builder);
/* 286:343 */         return true;
/* 287:    */       }
/* 288:345 */       return false;
/* 289:    */     case 27: 
/* 290:347 */       Map map = (Map)value;
/* 291:348 */       if (map.size() > 0)
/* 292:    */       {
/* 293:349 */         serializeFieldName(fieldName, builder);
/* 294:350 */         serializeMap(map, builder);
/* 295:351 */         return true;
/* 296:    */       }
/* 297:353 */       return false;
/* 298:    */     case 28: 
/* 299:    */     case 29: 
/* 300:    */     case 30: 
/* 301:    */     case 31: 
/* 302:    */     case 32: 
/* 303:    */     case 33: 
/* 304:    */     case 34: 
/* 305:    */     case 35: 
/* 306:    */     case 36: 
/* 307:365 */       if ((value.getClass().isArray()) && 
/* 308:366 */         (Array.getLength(value) > 0))
/* 309:    */       {
/* 310:367 */         serializeFieldName(fieldName, builder);
/* 311:368 */         serializeArray(fieldAccess.componentType(), value, builder);
/* 312:369 */         return true;
/* 313:    */       }
/* 314:372 */       return false;
/* 315:    */     case 37: 
/* 316:    */     case 38: 
/* 317:376 */       serializeFieldName(fieldName, builder);
/* 318:377 */       serializeSubtypeInstance(value, builder);
/* 319:378 */       return true;
/* 320:    */     case 39: 
/* 321:381 */       serializeFieldName(fieldName, builder);
/* 322:382 */       TypeType instanceType = TypeType.getInstanceType(value);
/* 323:383 */       if (instanceType == TypeType.INSTANCE) {
/* 324:384 */         serializeSubtypeInstance(value, builder);
/* 325:    */       } else {
/* 326:386 */         serializeObject(value, builder);
/* 327:    */       }
/* 328:388 */       return true;
/* 329:    */     case 40: 
/* 330:391 */       serializeFieldName(fieldName, builder);
/* 331:392 */       serializeInstance(value, builder);
/* 332:393 */       return true;
/* 333:    */     case 41: 
/* 334:396 */       return false;
/* 335:    */     case 42: 
/* 336:401 */       serializeFieldName(fieldName, builder);
/* 337:402 */       TimeZone zone = (TimeZone)value;
/* 338:    */       
/* 339:404 */       builder.addQuoted(zone.getID());
/* 340:405 */       return true;
/* 341:    */     case 43: 
/* 342:408 */       serializeFieldName(fieldName, builder);
/* 343:409 */       serializeCurrency((Currency)value, builder);
/* 344:410 */       return true;
/* 345:    */     }
/* 346:413 */     serializeFieldName(fieldName, builder);
/* 347:414 */     serializeUnknown(value, builder);
/* 348:415 */     return true;
/* 349:    */   }
/* 350:    */   
/* 351:    */   public final void serializeDate(Date date, CharBuf builder)
/* 352:    */   {
/* 353:429 */     builder.addLong(date.getTime());
/* 354:    */   }
/* 355:    */   
/* 356:    */   public final void serializeCurrency(Currency currency, CharBuf builder)
/* 357:    */   {
/* 358:434 */     builder.addChar('"');
/* 359:435 */     builder.addString(currency.getCurrencyCode());
/* 360:436 */     builder.addChar('"');
/* 361:    */   }
/* 362:    */   
/* 363:    */   public final void serializeObject(Object obj, CharBuf builder)
/* 364:    */   {
/* 365:442 */     TypeType type = TypeType.getInstanceType(obj);
/* 366:444 */     switch (1.$SwitchMap$org$boon$core$TypeType[type.ordinal()])
/* 367:    */     {
/* 368:    */     case 44: 
/* 369:447 */       return;
/* 370:    */     case 1: 
/* 371:449 */       builder.addInt((Integer)Integer.TYPE.cast(obj));
/* 372:450 */       return;
/* 373:    */     case 2: 
/* 374:452 */       builder.addBoolean(((Boolean)Boolean.TYPE.cast(obj)).booleanValue());
/* 375:453 */       return;
/* 376:    */     case 3: 
/* 377:455 */       builder.addByte(((Byte)Byte.TYPE.cast(obj)).byteValue());
/* 378:456 */       return;
/* 379:    */     case 4: 
/* 380:458 */       builder.addLong((Long)Long.TYPE.cast(obj));
/* 381:459 */       return;
/* 382:    */     case 5: 
/* 383:461 */       builder.addDouble((Double)Double.TYPE.cast(obj));
/* 384:462 */       return;
/* 385:    */     case 6: 
/* 386:464 */       builder.addFloat((Float)Float.TYPE.cast(obj));
/* 387:465 */       return;
/* 388:    */     case 7: 
/* 389:467 */       builder.addShort(((Short)Short.TYPE.cast(obj)).shortValue());
/* 390:468 */       return;
/* 391:    */     case 8: 
/* 392:470 */       builder.addChar(((Character)Character.TYPE.cast(obj)).charValue());
/* 393:471 */       return;
/* 394:    */     case 9: 
/* 395:473 */       builder.addBigDecimal((BigDecimal)obj);
/* 396:474 */       return;
/* 397:    */     case 11: 
/* 398:476 */       builder.addBigInteger((BigInteger)obj);
/* 399:477 */       return;
/* 400:    */     case 12: 
/* 401:479 */       serializeDate((Date)obj, builder);
/* 402:480 */       return;
/* 403:    */     case 14: 
/* 404:482 */       builder.addQuoted(((Class)obj).getName());
/* 405:483 */       return;
/* 406:    */     case 13: 
/* 407:486 */       serializeString((String)obj, builder);
/* 408:487 */       return;
/* 409:    */     case 15: 
/* 410:489 */       serializeString(obj.toString(), builder);
/* 411:490 */       return;
/* 412:    */     case 45: 
/* 413:492 */       builder.addBoolean(((Boolean)obj).booleanValue());
/* 414:493 */       return;
/* 415:    */     case 16: 
/* 416:495 */       builder.addInt((Integer)obj);
/* 417:496 */       return;
/* 418:    */     case 17: 
/* 419:498 */       builder.addLong((Long)obj);
/* 420:499 */       return;
/* 421:    */     case 18: 
/* 422:501 */       builder.addFloat((Float)obj);
/* 423:502 */       return;
/* 424:    */     case 19: 
/* 425:504 */       builder.addDouble((Double)obj);
/* 426:505 */       return;
/* 427:    */     case 20: 
/* 428:507 */       builder.addShort(((Short)obj).shortValue());
/* 429:508 */       return;
/* 430:    */     case 21: 
/* 431:510 */       builder.addByte(((Byte)obj).byteValue());
/* 432:511 */       return;
/* 433:    */     case 22: 
/* 434:513 */       builder.addChar(((Character)obj).charValue());
/* 435:514 */       return;
/* 436:    */     case 23: 
/* 437:516 */       builder.addQuoted(obj.toString());
/* 438:517 */       return;
/* 439:    */     case 42: 
/* 440:520 */       TimeZone zone = (TimeZone)obj;
/* 441:    */       
/* 442:522 */       builder.addQuoted(zone.getID());
/* 443:523 */       return;
/* 444:    */     case 24: 
/* 445:    */     case 25: 
/* 446:    */     case 26: 
/* 447:529 */       serializeCollection((Collection)obj, builder);
/* 448:530 */       return;
/* 449:    */     case 27: 
/* 450:532 */       serializeMap((Map)obj, builder);
/* 451:533 */       return;
/* 452:    */     case 28: 
/* 453:    */     case 29: 
/* 454:    */     case 30: 
/* 455:    */     case 31: 
/* 456:    */     case 32: 
/* 457:    */     case 33: 
/* 458:    */     case 34: 
/* 459:    */     case 35: 
/* 460:    */     case 36: 
/* 461:545 */       serializeArray(obj, builder);
/* 462:546 */       return;
/* 463:    */     case 37: 
/* 464:    */     case 38: 
/* 465:549 */       serializeSubtypeInstance(obj, builder);
/* 466:550 */       return;
/* 467:    */     case 40: 
/* 468:553 */       serializeInstance(obj, builder);
/* 469:554 */       return;
/* 470:    */     case 43: 
/* 471:556 */       serializeCurrency((Currency)obj, builder);
/* 472:557 */       return;
/* 473:    */     }
/* 474:560 */     serializeUnknown(obj, builder);
/* 475:    */   }
/* 476:    */   
/* 477:    */   public void serializeUnknown(Object obj, CharBuf builder)
/* 478:    */   {
/* 479:571 */     builder.addQuoted(obj.toString());
/* 480:    */   }
/* 481:    */   
/* 482:    */   public final void serializeInstance(Object instance, CharBuf builder)
/* 483:    */   {
/* 484:    */     try
/* 485:    */     {
/* 486:584 */       this.level += 1;
/* 487:586 */       if (this.level > 100) {
/* 488:587 */         Exceptions.die(new Object[] { "Detected circular dependency", builder.toString() });
/* 489:    */       }
/* 490:590 */       if ((this.serializeAsSupport) && (Reflection.respondsTo(instance, "serializeAs")))
/* 491:    */       {
/* 492:591 */         serializeObject(Invoker.invoke(instance, "serializeAs", new Object[0]), builder);
/* 493:    */       }
/* 494:    */       else
/* 495:    */       {
/* 496:595 */         Collection<FieldAccess> fields = getFields(instance.getClass()).values();
/* 497:    */         
/* 498:    */ 
/* 499:598 */         builder.addChar('{');
/* 500:    */         
/* 501:600 */         int index = 0;
/* 502:601 */         for (FieldAccess fieldAccess : fields) {
/* 503:602 */           if (serializeField(instance, fieldAccess, builder))
/* 504:    */           {
/* 505:603 */             builder.add(',');
/* 506:604 */             index++;
/* 507:    */           }
/* 508:    */         }
/* 509:607 */         if (index > 0) {
/* 510:608 */           builder.removeLastChar();
/* 511:    */         }
/* 512:610 */         builder.addChar('}');
/* 513:    */       }
/* 514:    */     }
/* 515:    */     catch (Exception ex)
/* 516:    */     {
/* 517:614 */       Exceptions.handle(ex, new Object[] { "serialize instance", instance, "class name of instance", Boon.className(instance), "obj", instance });
/* 518:    */     }
/* 519:    */     finally
/* 520:    */     {
/* 521:618 */       this.level -= 1;
/* 522:    */     }
/* 523:    */   }
/* 524:    */   
/* 525:    */   public void serializeInstance(Object obj, CharBuf builder, boolean includeTypeInfo)
/* 526:    */   {
/* 527:626 */     serializeInstance(obj, builder);
/* 528:    */   }
/* 529:    */   
/* 530:    */   public Map<String, FieldAccess> getFields(Class<? extends Object> aClass)
/* 531:    */   {
/* 532:632 */     Map<String, FieldAccess> map = (Map)this.fieldMap.get(aClass);
/* 533:633 */     if (map == null)
/* 534:    */     {
/* 535:634 */       map = doGetFields(aClass);
/* 536:635 */       this.fieldMap.put(aClass, map);
/* 537:    */     }
/* 538:637 */     return map;
/* 539:    */   }
/* 540:    */   
/* 541:    */   private final Map<String, FieldAccess> doGetFields(Class<? extends Object> aClass)
/* 542:    */   {
/* 543:642 */     Map<String, FieldAccess> fields = Maps.copy(Reflection.getPropertyFieldAccessMapFieldFirstForSerializer(aClass));
/* 544:    */     
/* 545:644 */     List<FieldAccess> removeFields = new ArrayList();
/* 546:646 */     for (FieldAccess field : fields.values()) {
/* 547:647 */       if (field.isWriteOnly()) {
/* 548:648 */         removeFields.add(field);
/* 549:    */       }
/* 550:    */     }
/* 551:652 */     for (FieldAccess fieldAccess : removeFields) {
/* 552:653 */       fields.remove(fieldAccess.name());
/* 553:    */     }
/* 554:655 */     return fields;
/* 555:    */   }
/* 556:    */   
/* 557:659 */   private static final char[] EMPTY_MAP_CHARS = { '{', '}' };
/* 558:    */   
/* 559:    */   public final void serializeMap(Map<Object, Object> smap, CharBuf builder)
/* 560:    */   {
/* 561:663 */     Map map = smap;
/* 562:664 */     if (map.size() == 0)
/* 563:    */     {
/* 564:665 */       builder.addChars(EMPTY_MAP_CHARS);
/* 565:666 */       return;
/* 566:    */     }
/* 567:670 */     builder.addChar('{');
/* 568:    */     
/* 569:672 */     int index = 0;
/* 570:673 */     Set<Map.Entry> entrySet = map.entrySet();
/* 571:674 */     for (Map.Entry entry : entrySet) {
/* 572:675 */       if (entry.getValue() != null)
/* 573:    */       {
/* 574:676 */         serializeFieldName(Str.toString(entry.getKey()), builder);
/* 575:677 */         serializeObject(entry.getValue(), builder);
/* 576:678 */         builder.addChar(',');
/* 577:679 */         index++;
/* 578:    */       }
/* 579:    */     }
/* 580:682 */     if (index > 0) {
/* 581:683 */       builder.removeLastChar();
/* 582:    */     }
/* 583:684 */     builder.addChar('}');
/* 584:    */   }
/* 585:    */   
/* 586:    */   public final void serializeArray(TypeType componentType, Object objectArray, CharBuf builder)
/* 587:    */   {
/* 588:692 */     switch (1.$SwitchMap$org$boon$core$TypeType[componentType.ordinal()])
/* 589:    */     {
/* 590:    */     case 13: 
/* 591:694 */       String[] array = (String[])objectArray;
/* 592:695 */       int length = array.length;
/* 593:    */       
/* 594:697 */       builder.addChar('[');
/* 595:698 */       for (int index = 0; index < length; index++)
/* 596:    */       {
/* 597:699 */         serializeString(array[index], builder);
/* 598:700 */         builder.addChar(',');
/* 599:    */       }
/* 600:702 */       builder.removeLastChar();
/* 601:703 */       builder.addChar(']');
/* 602:704 */       break;
/* 603:    */     default: 
/* 604:706 */       serializeArray(objectArray, builder);
/* 605:    */     }
/* 606:    */   }
/* 607:    */   
/* 608:    */   public final void serializeArray(Object array, CharBuf builder)
/* 609:    */   {
/* 610:712 */     if (Array.getLength(array) == 0)
/* 611:    */     {
/* 612:713 */       builder.addChars(EMPTY_LIST_CHARS);
/* 613:714 */       return;
/* 614:    */     }
/* 615:717 */     builder.addChar('[');
/* 616:718 */     int length = Array.getLength(array);
/* 617:719 */     for (int index = 0; index < length; index++)
/* 618:    */     {
/* 619:720 */       serializeObject(Array.get(array, index), builder);
/* 620:721 */       builder.addChar(',');
/* 621:    */     }
/* 622:723 */     builder.removeLastChar();
/* 623:724 */     builder.addChar(']');
/* 624:    */   }
/* 625:    */   
/* 626:    */   private void serializeFieldName(String name, CharBuf builder)
/* 627:    */   {
/* 628:729 */     builder.addJsonFieldName(FastStringUtils.toCharArray(name));
/* 629:    */   }
/* 630:    */   
/* 631:733 */   private static final char[] EMPTY_LIST_CHARS = { '[', ']' };
/* 632:    */   
/* 633:    */   public final void serializeCollection(Collection<?> collection, CharBuf builder)
/* 634:    */   {
/* 635:737 */     if (collection.size() == 0)
/* 636:    */     {
/* 637:738 */       builder.addChars(EMPTY_LIST_CHARS);
/* 638:739 */       return;
/* 639:    */     }
/* 640:742 */     builder.addChar('[');
/* 641:743 */     for (Object o : collection)
/* 642:    */     {
/* 643:744 */       if (o == null) {
/* 644:745 */         builder.addNull();
/* 645:    */       } else {
/* 646:747 */         serializeObject(o, builder);
/* 647:    */       }
/* 648:749 */       builder.addChar(',');
/* 649:    */     }
/* 650:752 */     builder.removeLastChar();
/* 651:753 */     builder.addChar(']');
/* 652:    */   }
/* 653:    */   
/* 654:    */   public void serializeSubtypeInstance(Object instance, CharBuf builder)
/* 655:    */   {
/* 656:764 */     this.level += 1;
/* 657:766 */     if (this.level > 100) {
/* 658:767 */       Exceptions.die(new Object[] { "Detected circular dependency", builder.toString() });
/* 659:    */     }
/* 660:770 */     Map<String, FieldAccess> fieldAccessors = getFields(instance.getClass());
/* 661:771 */     Collection<FieldAccess> values = fieldAccessors.values();
/* 662:    */     
/* 663:773 */     builder.addString("{\"class\":");
/* 664:774 */     builder.addQuoted(instance.getClass().getName());
/* 665:    */     
/* 666:776 */     int index = 0;
/* 667:777 */     int length = values.size();
/* 668:779 */     if (length > 0)
/* 669:    */     {
/* 670:780 */       builder.addChar(',');
/* 671:782 */       for (FieldAccess fieldAccess : values) {
/* 672:783 */         if (serializeField(instance, fieldAccess, builder))
/* 673:    */         {
/* 674:784 */           builder.addChar(',');
/* 675:785 */           index++;
/* 676:    */         }
/* 677:    */       }
/* 678:788 */       if (index > 0) {
/* 679:789 */         builder.removeLastChar();
/* 680:    */       }
/* 681:791 */       builder.addChar('}');
/* 682:    */     }
/* 683:795 */     this.level -= 1;
/* 684:    */   }
/* 685:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.serializers.impl.JsonSimpleSerializerImpl
 * JD-Core Version:    0.7.0.1
 */