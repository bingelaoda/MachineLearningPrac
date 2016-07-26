/*   1:    */ package org.boon.core.reflection.fields;
/*   2:    */ 
/*   3:    */ import java.lang.reflect.Field;
/*   4:    */ import java.lang.reflect.Modifier;
/*   5:    */ import org.boon.Exceptions;
/*   6:    */ import org.boon.core.Typ;
/*   7:    */ import sun.misc.Unsafe;
/*   8:    */ 
/*   9:    */ public abstract class UnsafeField
/*  10:    */   extends BaseField
/*  11:    */ {
/*  12:    */   protected long offset;
/*  13:    */   protected final Object base;
/*  14:    */   private final Field field;
/*  15:    */   
/*  16:    */   protected UnsafeField(Field field)
/*  17:    */   {
/*  18: 50 */     super(field);
/*  19: 51 */     if (super.isStatic())
/*  20:    */     {
/*  21: 52 */       this.base = unsafe.staticFieldBase(field);
/*  22: 53 */       this.offset = unsafe.staticFieldOffset(field);
/*  23:    */     }
/*  24:    */     else
/*  25:    */     {
/*  26: 55 */       this.offset = unsafe.objectFieldOffset(field);
/*  27: 56 */       this.base = null;
/*  28:    */     }
/*  29: 58 */     this.field = field;
/*  30:    */   }
/*  31:    */   
/*  32:    */   private static Unsafe getUnsafe()
/*  33:    */   {
/*  34:    */     try
/*  35:    */     {
/*  36: 64 */       Field f = Unsafe.class.getDeclaredField("theUnsafe");
/*  37: 65 */       f.setAccessible(true);
/*  38: 66 */       return (Unsafe)f.get(null);
/*  39:    */     }
/*  40:    */     catch (Exception e) {}
/*  41: 68 */     return null;
/*  42:    */   }
/*  43:    */   
/*  44: 72 */   protected static final Unsafe unsafe = ;
/*  45:    */   
/*  46:    */   public static UnsafeField createUnsafeField(Field field)
/*  47:    */   {
/*  48: 75 */     Class<?> type = field.getType();
/*  49: 76 */     boolean isVolatile = Modifier.isVolatile(field.getModifiers());
/*  50: 77 */     if (!isVolatile)
/*  51:    */     {
/*  52: 78 */       if (type == Typ.intgr) {
/*  53: 79 */         return new IntUnsafeField(field);
/*  54:    */       }
/*  55: 80 */       if (type == Typ.lng) {
/*  56: 81 */         return new LongUnsafeField(field);
/*  57:    */       }
/*  58: 82 */       if (type == Typ.bt) {
/*  59: 83 */         return new ByteUnsafeField(field);
/*  60:    */       }
/*  61: 84 */       if (type == Typ.shrt) {
/*  62: 85 */         return new ShortUnsafeField(field);
/*  63:    */       }
/*  64: 86 */       if (type == Typ.chr) {
/*  65: 87 */         return new CharUnsafeField(field);
/*  66:    */       }
/*  67: 88 */       if (type == Typ.dbl) {
/*  68: 89 */         return new DoubleUnsafeField(field);
/*  69:    */       }
/*  70: 90 */       if (type == Typ.flt) {
/*  71: 91 */         return new FloatUnsafeField(field);
/*  72:    */       }
/*  73: 92 */       if (type == Typ.bln) {
/*  74: 93 */         return new BooleanUnsafeField(field);
/*  75:    */       }
/*  76: 95 */       return new ObjectUnsafeField(field);
/*  77:    */     }
/*  78: 98 */     if (type == Typ.intgr) {
/*  79: 99 */       return new VolatileIntUnsafeField(field);
/*  80:    */     }
/*  81:100 */     if (type == Typ.lng) {
/*  82:101 */       return new VolatileLongUnsafeField(field);
/*  83:    */     }
/*  84:102 */     if (type == Typ.bt) {
/*  85:103 */       return new VolatileByteUnsafeField(field);
/*  86:    */     }
/*  87:104 */     if (type == Typ.shrt) {
/*  88:105 */       return new VolatileShortUnsafeField(field);
/*  89:    */     }
/*  90:106 */     if (type == Typ.chr) {
/*  91:107 */       return new VolatileCharUnsafeField(field);
/*  92:    */     }
/*  93:108 */     if (type == Typ.dbl) {
/*  94:109 */       return new VolatileDoubleUnsafeField(field);
/*  95:    */     }
/*  96:110 */     if (type == Typ.flt) {
/*  97:111 */       return new VolatileFloatUnsafeField(field);
/*  98:    */     }
/*  99:112 */     if (type == Typ.bln) {
/* 100:113 */       return new VolatileBooleanUnsafeField(field);
/* 101:    */     }
/* 102:115 */     return new ObjectUnsafeField(field);
/* 103:    */   }
/* 104:    */   
/* 105:    */   public void setStaticValue(Object newValue)
/* 106:    */   {
/* 107:    */     try
/* 108:    */     {
/* 109:125 */       this.field.set(null, newValue);
/* 110:    */     }
/* 111:    */     catch (IllegalAccessException e)
/* 112:    */     {
/* 113:127 */       Exceptions.handle(e);
/* 114:    */     }
/* 115:    */   }
/* 116:    */   
/* 117:    */   public int getInt(Object obj)
/* 118:    */   {
/* 119:136 */     Exceptions.die(String.format("Can't call this method on this type %s", new Object[] { this.type }));
/* 120:137 */     return 0;
/* 121:    */   }
/* 122:    */   
/* 123:    */   public boolean getBoolean(Object obj)
/* 124:    */   {
/* 125:142 */     Exceptions.die(String.format("Can't call this method on this type %s", new Object[] { this.type }));
/* 126:143 */     return false;
/* 127:    */   }
/* 128:    */   
/* 129:    */   public short getShort(Object obj)
/* 130:    */   {
/* 131:149 */     Exceptions.die(String.format("Can't call this method on this type %s", new Object[] { this.type }));
/* 132:150 */     return 0;
/* 133:    */   }
/* 134:    */   
/* 135:    */   public char getChar(Object obj)
/* 136:    */   {
/* 137:156 */     Exceptions.die(String.format("Can't call this method on this type %s", new Object[] { this.type }));
/* 138:157 */     return '\000';
/* 139:    */   }
/* 140:    */   
/* 141:    */   public long getLong(Object obj)
/* 142:    */   {
/* 143:163 */     Exceptions.die(String.format("Can't call this method on this type %s", new Object[] { this.type }));
/* 144:164 */     return 0L;
/* 145:    */   }
/* 146:    */   
/* 147:    */   public double getDouble(Object obj)
/* 148:    */   {
/* 149:170 */     Exceptions.die(String.format("Can't call this method on this type %s", new Object[] { this.type }));
/* 150:171 */     return 0.0D;
/* 151:    */   }
/* 152:    */   
/* 153:    */   public float getFloat(Object obj)
/* 154:    */   {
/* 155:177 */     Exceptions.die(String.format("Can't call this method on this type %s", new Object[] { this.type }));
/* 156:178 */     return 0.0F;
/* 157:    */   }
/* 158:    */   
/* 159:    */   public byte getByte(Object obj)
/* 160:    */   {
/* 161:184 */     Exceptions.die(String.format("Can't call this method on this type %s", new Object[] { this.type }));
/* 162:185 */     return 0;
/* 163:    */   }
/* 164:    */   
/* 165:    */   public Object getObject(Object obj)
/* 166:    */   {
/* 167:191 */     Exceptions.die(String.format("Can't call this method on this type %s for field %s", new Object[] { this.type, this.name }));
/* 168:192 */     return Integer.valueOf(0);
/* 169:    */   }
/* 170:    */   
/* 171:    */   public boolean getStaticBoolean()
/* 172:    */   {
/* 173:197 */     return getBoolean(this.base);
/* 174:    */   }
/* 175:    */   
/* 176:    */   public int getStaticInt()
/* 177:    */   {
/* 178:202 */     return getInt(this.base);
/* 179:    */   }
/* 180:    */   
/* 181:    */   public short getStaticShort()
/* 182:    */   {
/* 183:207 */     return getShort(this.base);
/* 184:    */   }
/* 185:    */   
/* 186:    */   public long getStaticLong()
/* 187:    */   {
/* 188:212 */     return getLong(this.base);
/* 189:    */   }
/* 190:    */   
/* 191:    */   public double getStaticDouble()
/* 192:    */   {
/* 193:216 */     return getDouble(this.base);
/* 194:    */   }
/* 195:    */   
/* 196:    */   public float getStaticFloat()
/* 197:    */   {
/* 198:221 */     return getFloat(this.base);
/* 199:    */   }
/* 200:    */   
/* 201:    */   public byte getStaticByte()
/* 202:    */   {
/* 203:226 */     return getByte(this.base);
/* 204:    */   }
/* 205:    */   
/* 206:    */   public Object getObject()
/* 207:    */   {
/* 208:231 */     return getObject(this.base);
/* 209:    */   }
/* 210:    */   
/* 211:    */   public Field getField()
/* 212:    */   {
/* 213:237 */     return this.field;
/* 214:    */   }
/* 215:    */   
/* 216:    */   public Object getBase()
/* 217:    */   {
/* 218:243 */     return this.base;
/* 219:    */   }
/* 220:    */   
/* 221:    */   public void setBoolean(Object obj, boolean value)
/* 222:    */   {
/* 223:252 */     Exceptions.die(String.format("Can't call this method on this type %s", new Object[] { this.type }));
/* 224:    */   }
/* 225:    */   
/* 226:    */   public void setInt(Object obj, int value)
/* 227:    */   {
/* 228:259 */     Exceptions.die(String.format("Can't call this method on this type %s", new Object[] { this.type }));
/* 229:    */   }
/* 230:    */   
/* 231:    */   public void setShort(Object obj, short value)
/* 232:    */   {
/* 233:266 */     Exceptions.die(String.format("Can't call this method on this type %s", new Object[] { this.type }));
/* 234:    */   }
/* 235:    */   
/* 236:    */   public void setChar(Object obj, char value)
/* 237:    */   {
/* 238:274 */     Exceptions.die(String.format("Can't call this method on this type %s", new Object[] { this.type }));
/* 239:    */   }
/* 240:    */   
/* 241:    */   public void setLong(Object obj, long value)
/* 242:    */   {
/* 243:281 */     Exceptions.die(String.format("Can't call this method on this type %s", new Object[] { this.type }));
/* 244:    */   }
/* 245:    */   
/* 246:    */   public void setDouble(Object obj, double value)
/* 247:    */   {
/* 248:288 */     Exceptions.die(String.format("Can't call this method on this type %s", new Object[] { this.type }));
/* 249:    */   }
/* 250:    */   
/* 251:    */   public void setFloat(Object obj, float value)
/* 252:    */   {
/* 253:295 */     Exceptions.die(String.format("Can't call this method on this type %s", new Object[] { this.type }));
/* 254:    */   }
/* 255:    */   
/* 256:    */   public void setByte(Object obj, byte value)
/* 257:    */   {
/* 258:301 */     Exceptions.die(String.format("Can't call this method on this type %s", new Object[] { this.type }));
/* 259:    */   }
/* 260:    */   
/* 261:    */   public void setObject(Object obj, Object value)
/* 262:    */   {
/* 263:307 */     Exceptions.die(String.format("Can't call this method on this type %s name = %s  value type = %s", new Object[] { this.type, this.name, value == null ? "null" : value.getClass() }));
/* 264:    */   }
/* 265:    */   
/* 266:    */   private static final class IntUnsafeField
/* 267:    */     extends UnsafeField
/* 268:    */   {
/* 269:    */     protected IntUnsafeField(Field f)
/* 270:    */     {
/* 271:318 */       super();
/* 272:    */     }
/* 273:    */     
/* 274:    */     public final void setInt(Object obj, int value)
/* 275:    */     {
/* 276:323 */       unsafe.putInt(obj, this.offset, value);
/* 277:    */     }
/* 278:    */     
/* 279:    */     public final int getInt(Object obj)
/* 280:    */     {
/* 281:329 */       return unsafe.getInt(obj, this.offset);
/* 282:    */     }
/* 283:    */   }
/* 284:    */   
/* 285:    */   private static class LongUnsafeField
/* 286:    */     extends UnsafeField
/* 287:    */   {
/* 288:    */     protected LongUnsafeField(Field f)
/* 289:    */     {
/* 290:336 */       super();
/* 291:    */     }
/* 292:    */     
/* 293:    */     public void setLong(Object obj, long value)
/* 294:    */     {
/* 295:341 */       unsafe.putLong(obj, this.offset, value);
/* 296:    */     }
/* 297:    */     
/* 298:    */     public long getLong(Object obj)
/* 299:    */     {
/* 300:346 */       return unsafe.getLong(obj, this.offset);
/* 301:    */     }
/* 302:    */   }
/* 303:    */   
/* 304:    */   private static class CharUnsafeField
/* 305:    */     extends UnsafeField
/* 306:    */   {
/* 307:    */     protected CharUnsafeField(Field f)
/* 308:    */     {
/* 309:353 */       super();
/* 310:    */     }
/* 311:    */     
/* 312:    */     public void setChar(Object obj, char value)
/* 313:    */     {
/* 314:358 */       unsafe.putChar(obj, this.offset, value);
/* 315:    */     }
/* 316:    */     
/* 317:    */     public char getChar(Object obj)
/* 318:    */     {
/* 319:363 */       return unsafe.getChar(obj, this.offset);
/* 320:    */     }
/* 321:    */   }
/* 322:    */   
/* 323:    */   private static class ByteUnsafeField
/* 324:    */     extends UnsafeField
/* 325:    */   {
/* 326:    */     protected ByteUnsafeField(Field f)
/* 327:    */     {
/* 328:370 */       super();
/* 329:    */     }
/* 330:    */     
/* 331:    */     public void setByte(Object obj, byte value)
/* 332:    */     {
/* 333:375 */       unsafe.putByte(obj, this.offset, value);
/* 334:    */     }
/* 335:    */     
/* 336:    */     public byte getByte(Object obj)
/* 337:    */     {
/* 338:380 */       return unsafe.getByte(obj, this.offset);
/* 339:    */     }
/* 340:    */   }
/* 341:    */   
/* 342:    */   private static class ShortUnsafeField
/* 343:    */     extends UnsafeField
/* 344:    */   {
/* 345:    */     protected ShortUnsafeField(Field f)
/* 346:    */     {
/* 347:387 */       super();
/* 348:    */     }
/* 349:    */     
/* 350:    */     public void setShort(Object obj, short value)
/* 351:    */     {
/* 352:392 */       unsafe.putShort(obj, this.offset, value);
/* 353:    */     }
/* 354:    */     
/* 355:    */     public short getShort(Object obj)
/* 356:    */     {
/* 357:397 */       return unsafe.getShort(obj, this.offset);
/* 358:    */     }
/* 359:    */   }
/* 360:    */   
/* 361:    */   private static class ObjectUnsafeField
/* 362:    */     extends UnsafeField
/* 363:    */   {
/* 364:    */     protected ObjectUnsafeField(Field f)
/* 365:    */     {
/* 366:404 */       super();
/* 367:    */     }
/* 368:    */     
/* 369:    */     public void setObject(Object obj, Object value)
/* 370:    */     {
/* 371:409 */       unsafe.putObject(obj, this.offset, value);
/* 372:    */     }
/* 373:    */     
/* 374:    */     public Object getObject(Object obj)
/* 375:    */     {
/* 376:414 */       return unsafe.getObject(obj, this.offset);
/* 377:    */     }
/* 378:    */   }
/* 379:    */   
/* 380:    */   private static class FloatUnsafeField
/* 381:    */     extends UnsafeField
/* 382:    */   {
/* 383:    */     protected FloatUnsafeField(Field f)
/* 384:    */     {
/* 385:421 */       super();
/* 386:    */     }
/* 387:    */     
/* 388:    */     public void setFloat(Object obj, float value)
/* 389:    */     {
/* 390:426 */       unsafe.putFloat(obj, this.offset, value);
/* 391:    */     }
/* 392:    */     
/* 393:    */     public float getFloat(Object obj)
/* 394:    */     {
/* 395:431 */       return unsafe.getFloat(obj, this.offset);
/* 396:    */     }
/* 397:    */   }
/* 398:    */   
/* 399:    */   private static class DoubleUnsafeField
/* 400:    */     extends UnsafeField
/* 401:    */   {
/* 402:    */     protected DoubleUnsafeField(Field f)
/* 403:    */     {
/* 404:438 */       super();
/* 405:    */     }
/* 406:    */     
/* 407:    */     public void setDouble(Object obj, double value)
/* 408:    */     {
/* 409:443 */       unsafe.putDouble(obj, this.offset, value);
/* 410:    */     }
/* 411:    */     
/* 412:    */     public double getDouble(Object obj)
/* 413:    */     {
/* 414:448 */       return unsafe.getDouble(obj, this.offset);
/* 415:    */     }
/* 416:    */   }
/* 417:    */   
/* 418:    */   private static class BooleanUnsafeField
/* 419:    */     extends UnsafeField
/* 420:    */   {
/* 421:    */     protected BooleanUnsafeField(Field f)
/* 422:    */     {
/* 423:456 */       super();
/* 424:    */     }
/* 425:    */     
/* 426:    */     public void setBoolean(Object obj, boolean value)
/* 427:    */     {
/* 428:461 */       unsafe.putBoolean(obj, this.offset, value);
/* 429:    */     }
/* 430:    */     
/* 431:    */     public boolean getBoolean(Object obj)
/* 432:    */     {
/* 433:466 */       return unsafe.getBoolean(obj, this.offset);
/* 434:    */     }
/* 435:    */   }
/* 436:    */   
/* 437:    */   private static class VolatileIntUnsafeField
/* 438:    */     extends UnsafeField
/* 439:    */   {
/* 440:    */     protected VolatileIntUnsafeField(Field f)
/* 441:    */     {
/* 442:474 */       super();
/* 443:    */     }
/* 444:    */     
/* 445:    */     public void setInt(Object obj, int value)
/* 446:    */     {
/* 447:479 */       unsafe.putIntVolatile(obj, this.offset, value);
/* 448:    */     }
/* 449:    */     
/* 450:    */     public int getInt(Object obj)
/* 451:    */     {
/* 452:484 */       return unsafe.getIntVolatile(obj, this.offset);
/* 453:    */     }
/* 454:    */   }
/* 455:    */   
/* 456:    */   private static class VolatileBooleanUnsafeField
/* 457:    */     extends UnsafeField
/* 458:    */   {
/* 459:    */     protected VolatileBooleanUnsafeField(Field f)
/* 460:    */     {
/* 461:492 */       super();
/* 462:    */     }
/* 463:    */     
/* 464:    */     public void setBoolean(Object obj, boolean value)
/* 465:    */     {
/* 466:497 */       unsafe.putBooleanVolatile(obj, this.offset, value);
/* 467:    */     }
/* 468:    */     
/* 469:    */     public boolean getBoolean(Object obj)
/* 470:    */     {
/* 471:502 */       return unsafe.getBooleanVolatile(obj, this.offset);
/* 472:    */     }
/* 473:    */   }
/* 474:    */   
/* 475:    */   private static class VolatileLongUnsafeField
/* 476:    */     extends UnsafeField
/* 477:    */   {
/* 478:    */     protected VolatileLongUnsafeField(Field f)
/* 479:    */     {
/* 480:509 */       super();
/* 481:    */     }
/* 482:    */     
/* 483:    */     public void setLong(Object obj, long value)
/* 484:    */     {
/* 485:514 */       unsafe.putLongVolatile(obj, this.offset, value);
/* 486:    */     }
/* 487:    */     
/* 488:    */     public long getLong(Object obj)
/* 489:    */     {
/* 490:519 */       return unsafe.getLongVolatile(obj, this.offset);
/* 491:    */     }
/* 492:    */   }
/* 493:    */   
/* 494:    */   private static class VolatileCharUnsafeField
/* 495:    */     extends UnsafeField
/* 496:    */   {
/* 497:    */     protected VolatileCharUnsafeField(Field f)
/* 498:    */     {
/* 499:526 */       super();
/* 500:    */     }
/* 501:    */     
/* 502:    */     public void setChar(Object obj, char value)
/* 503:    */     {
/* 504:531 */       unsafe.putCharVolatile(obj, this.offset, value);
/* 505:    */     }
/* 506:    */     
/* 507:    */     public char getChar(Object obj)
/* 508:    */     {
/* 509:536 */       return unsafe.getCharVolatile(obj, this.offset);
/* 510:    */     }
/* 511:    */   }
/* 512:    */   
/* 513:    */   private static class VolatileByteUnsafeField
/* 514:    */     extends UnsafeField
/* 515:    */   {
/* 516:    */     protected VolatileByteUnsafeField(Field f)
/* 517:    */     {
/* 518:543 */       super();
/* 519:    */     }
/* 520:    */     
/* 521:    */     public void setByte(Object obj, byte value)
/* 522:    */     {
/* 523:548 */       unsafe.putByteVolatile(obj, this.offset, value);
/* 524:    */     }
/* 525:    */     
/* 526:    */     public byte getByte(Object obj)
/* 527:    */     {
/* 528:553 */       return unsafe.getByteVolatile(obj, this.offset);
/* 529:    */     }
/* 530:    */   }
/* 531:    */   
/* 532:    */   private static class VolatileShortUnsafeField
/* 533:    */     extends UnsafeField
/* 534:    */   {
/* 535:    */     protected VolatileShortUnsafeField(Field f)
/* 536:    */     {
/* 537:560 */       super();
/* 538:    */     }
/* 539:    */     
/* 540:    */     public void setShort(Object obj, short value)
/* 541:    */     {
/* 542:565 */       unsafe.putShortVolatile(obj, this.offset, value);
/* 543:    */     }
/* 544:    */     
/* 545:    */     public short getShort(Object obj)
/* 546:    */     {
/* 547:570 */       return unsafe.getShortVolatile(obj, this.offset);
/* 548:    */     }
/* 549:    */   }
/* 550:    */   
/* 551:    */   private static class VolatileObjectUnsafeField
/* 552:    */     extends UnsafeField
/* 553:    */   {
/* 554:    */     protected VolatileObjectUnsafeField(Field f)
/* 555:    */     {
/* 556:577 */       super();
/* 557:    */     }
/* 558:    */     
/* 559:    */     public void setObject(Object obj, Object value)
/* 560:    */     {
/* 561:582 */       unsafe.putObjectVolatile(obj, this.offset, value);
/* 562:    */     }
/* 563:    */     
/* 564:    */     public Object getObject(Object obj)
/* 565:    */     {
/* 566:587 */       return unsafe.getObjectVolatile(obj, this.offset);
/* 567:    */     }
/* 568:    */   }
/* 569:    */   
/* 570:    */   private static class VolatileFloatUnsafeField
/* 571:    */     extends UnsafeField
/* 572:    */   {
/* 573:    */     protected VolatileFloatUnsafeField(Field f)
/* 574:    */     {
/* 575:594 */       super();
/* 576:    */     }
/* 577:    */     
/* 578:    */     public void setFloat(Object obj, float value)
/* 579:    */     {
/* 580:599 */       unsafe.putFloatVolatile(obj, this.offset, value);
/* 581:    */     }
/* 582:    */     
/* 583:    */     public float getFloat(Object obj)
/* 584:    */     {
/* 585:604 */       return unsafe.getFloatVolatile(obj, this.offset);
/* 586:    */     }
/* 587:    */   }
/* 588:    */   
/* 589:    */   private static class VolatileDoubleUnsafeField
/* 590:    */     extends UnsafeField
/* 591:    */   {
/* 592:    */     protected VolatileDoubleUnsafeField(Field f)
/* 593:    */     {
/* 594:611 */       super();
/* 595:    */     }
/* 596:    */     
/* 597:    */     public void setDouble(Object obj, double value)
/* 598:    */     {
/* 599:616 */       unsafe.putDoubleVolatile(obj, this.offset, value);
/* 600:    */     }
/* 601:    */     
/* 602:    */     public double getDouble(Object obj)
/* 603:    */     {
/* 604:621 */       return unsafe.getDoubleVolatile(obj, this.offset);
/* 605:    */     }
/* 606:    */   }
/* 607:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.reflection.fields.UnsafeField
 * JD-Core Version:    0.7.0.1
 */