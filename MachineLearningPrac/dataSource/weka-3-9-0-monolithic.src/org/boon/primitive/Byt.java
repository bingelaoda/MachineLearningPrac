/*   1:    */ package org.boon.primitive;
/*   2:    */ 
/*   3:    */ import java.nio.charset.StandardCharsets;
/*   4:    */ import org.boon.Exceptions;
/*   5:    */ import org.boon.Universal;
/*   6:    */ import org.boon.core.reflection.Invoker;
/*   7:    */ 
/*   8:    */ public class Byt
/*   9:    */ {
/*  10:    */   public static byte[] grow(byte[] array, int size)
/*  11:    */   {
/*  12: 47 */     byte[] newArray = new byte[array.length + size];
/*  13: 48 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*  14: 49 */     return newArray;
/*  15:    */   }
/*  16:    */   
/*  17:    */   public static byte[] grow(byte[] array)
/*  18:    */   {
/*  19: 55 */     byte[] newArray = new byte[array.length * 2];
/*  20: 56 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*  21: 57 */     return newArray;
/*  22:    */   }
/*  23:    */   
/*  24:    */   public static byte[] shrink(byte[] array, int size)
/*  25:    */   {
/*  26: 63 */     byte[] newArray = new byte[array.length - size];
/*  27:    */     
/*  28: 65 */     System.arraycopy(array, 0, newArray, 0, array.length - size);
/*  29: 66 */     return newArray;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public static byte[] compact(byte[] array)
/*  33:    */   {
/*  34: 72 */     int nullCount = 0;
/*  35: 73 */     for (byte ch : array) {
/*  36: 75 */       if (ch == 0) {
/*  37: 76 */         nullCount++;
/*  38:    */       }
/*  39:    */     }
/*  40: 79 */     byte[] newArray = new byte[array.length - nullCount];
/*  41:    */     
/*  42: 81 */     int j = 0;
/*  43: 82 */     for (byte ch : array) {
/*  44: 84 */       if (ch != 0)
/*  45:    */       {
/*  46: 88 */         newArray[j] = ch;
/*  47: 89 */         j++;
/*  48:    */       }
/*  49:    */     }
/*  50: 91 */     return newArray;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public static byte[] arrayOfByte(int size)
/*  54:    */   {
/*  55:102 */     return new byte[size];
/*  56:    */   }
/*  57:    */   
/*  58:    */   @Universal
/*  59:    */   public static byte[] array(byte... array)
/*  60:    */   {
/*  61:111 */     return array;
/*  62:    */   }
/*  63:    */   
/*  64:    */   @Universal
/*  65:    */   public static byte[] bytes(byte... array)
/*  66:    */   {
/*  67:120 */     return array;
/*  68:    */   }
/*  69:    */   
/*  70:    */   @Universal
/*  71:    */   public static byte[] bytes(String str)
/*  72:    */   {
/*  73:129 */     return str.getBytes(StandardCharsets.UTF_8);
/*  74:    */   }
/*  75:    */   
/*  76:    */   @Universal
/*  77:    */   public static int len(byte[] array)
/*  78:    */   {
/*  79:135 */     return array.length;
/*  80:    */   }
/*  81:    */   
/*  82:    */   @Universal
/*  83:    */   public static int lengthOf(byte[] array)
/*  84:    */   {
/*  85:141 */     return array.length;
/*  86:    */   }
/*  87:    */   
/*  88:    */   @Universal
/*  89:    */   public static byte atIndex(byte[] array, int index)
/*  90:    */   {
/*  91:146 */     return idx(array, index);
/*  92:    */   }
/*  93:    */   
/*  94:    */   @Universal
/*  95:    */   public static byte idx(byte[] array, int index)
/*  96:    */   {
/*  97:151 */     int i = calculateIndex(array, index);
/*  98:    */     
/*  99:153 */     return array[i];
/* 100:    */   }
/* 101:    */   
/* 102:    */   @Universal
/* 103:    */   public static void atIndex(byte[] array, int index, byte value)
/* 104:    */   {
/* 105:159 */     idx(array, index, value);
/* 106:    */   }
/* 107:    */   
/* 108:    */   @Universal
/* 109:    */   public static void idx(byte[] array, int index, byte value)
/* 110:    */   {
/* 111:165 */     int i = calculateIndex(array, index);
/* 112:    */     
/* 113:167 */     array[i] = value;
/* 114:    */   }
/* 115:    */   
/* 116:    */   @Universal
/* 117:    */   public static byte[] sliceOf(byte[] array, int startIndex, int endIndex)
/* 118:    */   {
/* 119:173 */     return slc(array, startIndex, endIndex);
/* 120:    */   }
/* 121:    */   
/* 122:    */   @Universal
/* 123:    */   public static byte[] slc(byte[] array, int startIndex, int endIndex)
/* 124:    */   {
/* 125:179 */     int start = calculateIndex(array, startIndex);
/* 126:180 */     int end = calculateEndIndex(array, endIndex);
/* 127:181 */     int newLength = end - start;
/* 128:183 */     if (newLength < 0) {
/* 129:184 */       throw new ArrayIndexOutOfBoundsException(String.format("start index %d, end index %d, length %d", new Object[] { Integer.valueOf(startIndex), Integer.valueOf(endIndex), Integer.valueOf(array.length) }));
/* 130:    */     }
/* 131:190 */     byte[] newArray = new byte[newLength];
/* 132:191 */     System.arraycopy(array, start, newArray, 0, newLength);
/* 133:192 */     return newArray;
/* 134:    */   }
/* 135:    */   
/* 136:    */   @Universal
/* 137:    */   public static byte[] sliceOf(byte[] array, int startIndex)
/* 138:    */   {
/* 139:198 */     return slc(array, startIndex);
/* 140:    */   }
/* 141:    */   
/* 142:    */   @Universal
/* 143:    */   public static byte[] slc(byte[] array, int startIndex)
/* 144:    */   {
/* 145:204 */     int start = calculateIndex(array, startIndex);
/* 146:205 */     int newLength = array.length - start;
/* 147:207 */     if (newLength < 0) {
/* 148:208 */       throw new ArrayIndexOutOfBoundsException(String.format("start index %d, length %d", new Object[] { Integer.valueOf(startIndex), Integer.valueOf(array.length) }));
/* 149:    */     }
/* 150:214 */     byte[] newArray = new byte[newLength];
/* 151:215 */     System.arraycopy(array, start, newArray, 0, newLength);
/* 152:216 */     return newArray;
/* 153:    */   }
/* 154:    */   
/* 155:    */   @Universal
/* 156:    */   public static byte[] endSliceOf(byte[] array, int endIndex)
/* 157:    */   {
/* 158:222 */     return slcEnd(array, endIndex);
/* 159:    */   }
/* 160:    */   
/* 161:    */   @Universal
/* 162:    */   public static byte[] slcEnd(byte[] array, int endIndex)
/* 163:    */   {
/* 164:228 */     int end = calculateEndIndex(array, endIndex);
/* 165:229 */     int newLength = end;
/* 166:231 */     if (newLength < 0) {
/* 167:232 */       throw new ArrayIndexOutOfBoundsException(String.format("start index %d, length %d", new Object[] { Integer.valueOf(endIndex), Integer.valueOf(array.length) }));
/* 168:    */     }
/* 169:238 */     byte[] newArray = new byte[newLength];
/* 170:239 */     System.arraycopy(array, 0, newArray, 0, newLength);
/* 171:240 */     return newArray;
/* 172:    */   }
/* 173:    */   
/* 174:    */   @Universal
/* 175:    */   public static boolean in(int value, byte... array)
/* 176:    */   {
/* 177:245 */     for (int currentValue : array) {
/* 178:246 */       if (currentValue == value) {
/* 179:247 */         return true;
/* 180:    */       }
/* 181:    */     }
/* 182:250 */     return false;
/* 183:    */   }
/* 184:    */   
/* 185:    */   @Universal
/* 186:    */   public static boolean inIntArray(byte value, int[] array)
/* 187:    */   {
/* 188:256 */     for (int currentValue : array) {
/* 189:257 */       if (currentValue == value) {
/* 190:258 */         return true;
/* 191:    */       }
/* 192:    */     }
/* 193:261 */     return false;
/* 194:    */   }
/* 195:    */   
/* 196:    */   @Universal
/* 197:    */   public static boolean in(int value, int offset, byte[] array)
/* 198:    */   {
/* 199:267 */     for (int index = offset; index < array.length; index++)
/* 200:    */     {
/* 201:268 */       int currentValue = array[index];
/* 202:269 */       if (currentValue == value) {
/* 203:270 */         return true;
/* 204:    */       }
/* 205:    */     }
/* 206:273 */     return false;
/* 207:    */   }
/* 208:    */   
/* 209:    */   @Universal
/* 210:    */   public static boolean in(int value, int offset, int end, byte[] array)
/* 211:    */   {
/* 212:278 */     for (int index = offset; index < end; index++)
/* 213:    */     {
/* 214:279 */       int currentValue = array[index];
/* 215:280 */       if (currentValue == value) {
/* 216:281 */         return true;
/* 217:    */       }
/* 218:    */     }
/* 219:284 */     return false;
/* 220:    */   }
/* 221:    */   
/* 222:    */   @Universal
/* 223:    */   public static byte[] copy(byte[] array)
/* 224:    */   {
/* 225:290 */     Exceptions.requireNonNull(array);
/* 226:291 */     byte[] newArray = new byte[array.length];
/* 227:292 */     System.arraycopy(array, 0, newArray, 0, array.length);
/* 228:293 */     return newArray;
/* 229:    */   }
/* 230:    */   
/* 231:    */   @Universal
/* 232:    */   public static byte[] copy(byte[] array, int offset, int length)
/* 233:    */   {
/* 234:298 */     Exceptions.requireNonNull(array);
/* 235:299 */     byte[] newArray = new byte[length];
/* 236:300 */     System.arraycopy(array, offset, newArray, 0, length);
/* 237:301 */     return newArray;
/* 238:    */   }
/* 239:    */   
/* 240:    */   @Universal
/* 241:    */   public static byte[] add(byte[] array, byte v)
/* 242:    */   {
/* 243:307 */     byte[] newArray = new byte[array.length + 1];
/* 244:308 */     System.arraycopy(array, 0, newArray, 0, array.length);
/* 245:309 */     newArray[array.length] = v;
/* 246:310 */     return newArray;
/* 247:    */   }
/* 248:    */   
/* 249:    */   @Universal
/* 250:    */   public static byte[] add(byte[] array, byte[] array2)
/* 251:    */   {
/* 252:315 */     byte[] newArray = new byte[array.length + array2.length];
/* 253:316 */     System.arraycopy(array, 0, newArray, 0, array.length);
/* 254:317 */     System.arraycopy(array2, 0, newArray, array.length, array2.length);
/* 255:318 */     return newArray;
/* 256:    */   }
/* 257:    */   
/* 258:    */   @Universal
/* 259:    */   public static byte[] insert(byte[] array, int idx, byte v)
/* 260:    */   {
/* 261:325 */     if (idx >= array.length) {
/* 262:326 */       return add(array, v);
/* 263:    */     }
/* 264:329 */     int index = calculateIndex(array, idx);
/* 265:    */     
/* 266:    */ 
/* 267:332 */     byte[] newArray = new byte[array.length + 1];
/* 268:334 */     if (index != 0) {
/* 269:337 */       System.arraycopy(array, 0, newArray, 0, index);
/* 270:    */     }
/* 271:341 */     boolean lastIndex = index == array.length - 1;
/* 272:342 */     int remainingIndex = array.length - index;
/* 273:344 */     if (lastIndex) {
/* 274:347 */       System.arraycopy(array, index, newArray, index + 1, remainingIndex);
/* 275:    */     } else {
/* 276:352 */       System.arraycopy(array, index, newArray, index + 1, remainingIndex);
/* 277:    */     }
/* 278:356 */     newArray[index] = v;
/* 279:357 */     return newArray;
/* 280:    */   }
/* 281:    */   
/* 282:    */   @Universal
/* 283:    */   public static byte[] insert(byte[] array, int fromIndex, byte[] values)
/* 284:    */   {
/* 285:364 */     if (fromIndex >= array.length) {
/* 286:365 */       return add(array, values);
/* 287:    */     }
/* 288:368 */     int index = calculateIndex(array, fromIndex);
/* 289:    */     
/* 290:    */ 
/* 291:371 */     byte[] newArray = new byte[array.length + values.length];
/* 292:373 */     if (index != 0) {
/* 293:376 */       System.arraycopy(array, 0, newArray, 0, index);
/* 294:    */     }
/* 295:380 */     boolean lastIndex = index == array.length - 1;
/* 296:    */     
/* 297:382 */     int toIndex = index + values.length;
/* 298:383 */     int remainingIndex = newArray.length - toIndex;
/* 299:385 */     if (lastIndex) {
/* 300:388 */       System.arraycopy(array, index, newArray, index + values.length, remainingIndex);
/* 301:    */     } else {
/* 302:393 */       System.arraycopy(array, index, newArray, index + values.length, remainingIndex);
/* 303:    */     }
/* 304:397 */     int i = index;
/* 305:397 */     for (int j = 0; i < toIndex; j++)
/* 306:    */     {
/* 307:398 */       newArray[i] = values[j];i++;
/* 308:    */     }
/* 309:400 */     return newArray;
/* 310:    */   }
/* 311:    */   
/* 312:    */   private static int calculateIndex(byte[] array, int originalIndex)
/* 313:    */   {
/* 314:406 */     int length = array.length;
/* 315:    */     
/* 316:    */ 
/* 317:    */ 
/* 318:410 */     int index = originalIndex;
/* 319:415 */     if (index < 0) {
/* 320:416 */       index = length + index;
/* 321:    */     }
/* 322:427 */     if (index < 0) {
/* 323:428 */       index = 0;
/* 324:    */     }
/* 325:430 */     if (index >= length) {
/* 326:431 */       index = length - 1;
/* 327:    */     }
/* 328:433 */     return index;
/* 329:    */   }
/* 330:    */   
/* 331:    */   private static int calculateEndIndex(byte[] array, int originalIndex)
/* 332:    */   {
/* 333:439 */     int length = array.length;
/* 334:    */     
/* 335:    */ 
/* 336:    */ 
/* 337:443 */     int index = originalIndex;
/* 338:448 */     if (index < 0) {
/* 339:449 */       index = length + index;
/* 340:    */     }
/* 341:460 */     if (index < 0) {
/* 342:461 */       index = 0;
/* 343:    */     }
/* 344:463 */     if (index > length) {
/* 345:464 */       index = length;
/* 346:    */     }
/* 347:466 */     return index;
/* 348:    */   }
/* 349:    */   
/* 350:    */   public static int idxInt(byte[] bytes, int off)
/* 351:    */   {
/* 352:470 */     return (bytes[(off + 3)] & 0xFF) + ((bytes[(off + 2)] & 0xFF) << 8) + ((bytes[(off + 1)] & 0xFF) << 16) + (bytes[off] << 24);
/* 353:    */   }
/* 354:    */   
/* 355:    */   public static byte[] addInt(byte[] array, int v)
/* 356:    */   {
/* 357:479 */     byte[] arrayToHoldInt = new byte[4];
/* 358:480 */     intTo(arrayToHoldInt, 0, v);
/* 359:481 */     return add(array, arrayToHoldInt);
/* 360:    */   }
/* 361:    */   
/* 362:    */   public static byte[] insertIntInto(byte[] array, int index, int v)
/* 363:    */   {
/* 364:487 */     byte[] arrayToHoldInt = new byte[4];
/* 365:488 */     intTo(arrayToHoldInt, 0, v);
/* 366:489 */     return insert(array, index, arrayToHoldInt);
/* 367:    */   }
/* 368:    */   
/* 369:    */   public static void intTo(byte[] b, int off, int val)
/* 370:    */   {
/* 371:495 */     b[(off + 3)] = ((byte)val);
/* 372:496 */     b[(off + 2)] = ((byte)(val >>> 8));
/* 373:497 */     b[(off + 1)] = ((byte)(val >>> 16));
/* 374:498 */     b[off] = ((byte)(val >>> 24));
/* 375:    */   }
/* 376:    */   
/* 377:    */   public static void longTo(byte[] b, int off, long val)
/* 378:    */   {
/* 379:502 */     b[(off + 7)] = ((byte)(int)val);
/* 380:503 */     b[(off + 6)] = ((byte)(int)(val >>> 8));
/* 381:504 */     b[(off + 5)] = ((byte)(int)(val >>> 16));
/* 382:505 */     b[(off + 4)] = ((byte)(int)(val >>> 24));
/* 383:506 */     b[(off + 3)] = ((byte)(int)(val >>> 32));
/* 384:507 */     b[(off + 2)] = ((byte)(int)(val >>> 40));
/* 385:508 */     b[(off + 1)] = ((byte)(int)(val >>> 48));
/* 386:509 */     b[off] = ((byte)(int)(val >>> 56));
/* 387:    */   }
/* 388:    */   
/* 389:    */   public static byte[] addLong(byte[] array, long value)
/* 390:    */   {
/* 391:514 */     byte[] holder = new byte[8];
/* 392:515 */     longTo(holder, 0, value);
/* 393:516 */     return add(array, holder);
/* 394:    */   }
/* 395:    */   
/* 396:    */   public static long idxUnsignedInt(byte[] bytes, int off)
/* 397:    */   {
/* 398:522 */     return (bytes[(off + 3)] & 0xFF) + ((bytes[(off + 2)] & 0xFF) << 8) + ((bytes[(off + 1)] & 0xFF) << 16) + ((bytes[off] & 0xFF) << 24);
/* 399:    */   }
/* 400:    */   
/* 401:    */   public static long idxLong(byte[] b, int off)
/* 402:    */   {
/* 403:529 */     return (b[(off + 7)] & 0xFF) + ((b[(off + 6)] & 0xFF) << 8) + ((b[(off + 5)] & 0xFF) << 16) + ((b[(off + 4)] & 0xFF) << 24) + ((b[(off + 3)] & 0xFF) << 32) + ((b[(off + 2)] & 0xFF) << 40) + ((b[(off + 1)] & 0xFF) << 48) + (b[off] << 56);
/* 404:    */   }
/* 405:    */   
/* 406:    */   public static short idxShort(byte[] b, int off)
/* 407:    */   {
/* 408:541 */     return (short)((b[(off + 1)] & 0xFF) + (b[off] << 8));
/* 409:    */   }
/* 410:    */   
/* 411:    */   public static byte[] addShort(byte[] array, short value)
/* 412:    */   {
/* 413:547 */     byte[] holder = new byte[2];
/* 414:548 */     shortTo(holder, 0, value);
/* 415:549 */     return add(array, holder);
/* 416:    */   }
/* 417:    */   
/* 418:    */   public static byte[] insertShortInto(byte[] array, int index, short value)
/* 419:    */   {
/* 420:556 */     byte[] holder = new byte[2];
/* 421:557 */     shortTo(holder, 0, value);
/* 422:558 */     return insert(array, index, holder);
/* 423:    */   }
/* 424:    */   
/* 425:    */   public static void shortTo(byte[] b, int off, short val)
/* 426:    */   {
/* 427:564 */     b[(off + 1)] = ((byte)val);
/* 428:565 */     b[off] = ((byte)(val >>> 8));
/* 429:    */   }
/* 430:    */   
/* 431:    */   public static char idxChar(byte[] b, int off)
/* 432:    */   {
/* 433:570 */     return (char)((b[(off + 1)] & 0xFF) + (b[off] << 8));
/* 434:    */   }
/* 435:    */   
/* 436:    */   public static byte[] addChar(byte[] array, char value)
/* 437:    */   {
/* 438:575 */     byte[] holder = new byte[2];
/* 439:576 */     charTo(holder, 0, value);
/* 440:577 */     return add(array, holder);
/* 441:    */   }
/* 442:    */   
/* 443:    */   public static byte[] insertCharInto(byte[] array, int index, char value)
/* 444:    */   {
/* 445:582 */     byte[] holder = new byte[2];
/* 446:583 */     charTo(holder, 0, value);
/* 447:584 */     return insert(array, index, holder);
/* 448:    */   }
/* 449:    */   
/* 450:    */   public static void charTo(byte[] b, int off, char val)
/* 451:    */   {
/* 452:590 */     b[(off + 1)] = ((byte)val);
/* 453:591 */     b[off] = ((byte)(val >>> '\b'));
/* 454:    */   }
/* 455:    */   
/* 456:    */   public static void charTo(byte[] b, char val)
/* 457:    */   {
/* 458:596 */     b[1] = ((byte)val);
/* 459:597 */     b[0] = ((byte)(val >>> '\b'));
/* 460:    */   }
/* 461:    */   
/* 462:    */   public static float idxFloat(byte[] array, int off)
/* 463:    */   {
/* 464:601 */     return Float.intBitsToFloat(idxInt(array, off));
/* 465:    */   }
/* 466:    */   
/* 467:    */   public static byte[] addFloat(byte[] array, float value)
/* 468:    */   {
/* 469:606 */     byte[] holder = new byte[4];
/* 470:607 */     floatTo(holder, 0, value);
/* 471:608 */     return add(array, holder);
/* 472:    */   }
/* 473:    */   
/* 474:    */   public static byte[] insertFloatInto(byte[] array, int index, float value)
/* 475:    */   {
/* 476:614 */     byte[] holder = new byte[4];
/* 477:615 */     floatTo(holder, 0, value);
/* 478:616 */     return insert(array, index, holder);
/* 479:    */   }
/* 480:    */   
/* 481:    */   public static void floatTo(byte[] array, int off, float val)
/* 482:    */   {
/* 483:621 */     intTo(array, off, Float.floatToIntBits(val));
/* 484:    */   }
/* 485:    */   
/* 486:    */   public static byte[] addDouble(byte[] array, double value)
/* 487:    */   {
/* 488:626 */     Exceptions.requireNonNull(array);
/* 489:    */     
/* 490:628 */     byte[] holder = new byte[4];
/* 491:629 */     doubleTo(holder, 0, value);
/* 492:630 */     return add(array, holder);
/* 493:    */   }
/* 494:    */   
/* 495:    */   public static byte[] insertDoubleInto(byte[] array, int index, double value)
/* 496:    */   {
/* 497:636 */     Exceptions.requireNonNull(array);
/* 498:    */     
/* 499:638 */     byte[] holder = new byte[4];
/* 500:639 */     doubleTo(holder, 0, value);
/* 501:640 */     return insert(array, index, holder);
/* 502:    */   }
/* 503:    */   
/* 504:    */   public static void doubleTo(byte[] b, int off, double val)
/* 505:    */   {
/* 506:646 */     longTo(b, off, Double.doubleToLongBits(val));
/* 507:    */   }
/* 508:    */   
/* 509:    */   public static double idxDouble(byte[] b, int off)
/* 510:    */   {
/* 511:650 */     return Double.longBitsToDouble(idxLong(b, off));
/* 512:    */   }
/* 513:    */   
/* 514:    */   public static void _idx(byte[] array, int startIndex, byte[] input)
/* 515:    */   {
/* 516:    */     try
/* 517:    */     {
/* 518:755 */       System.arraycopy(input, 0, array, startIndex, input.length);
/* 519:    */     }
/* 520:    */     catch (Exception ex)
/* 521:    */     {
/* 522:757 */       Exceptions.handle(String.format("array size %d, startIndex %d, input length %d", new Object[] { Integer.valueOf(array.length), Integer.valueOf(startIndex), Integer.valueOf(input.length) }), ex);
/* 523:    */     }
/* 524:    */   }
/* 525:    */   
/* 526:    */   public static void _idx(byte[] array, int startIndex, byte[] input, int length)
/* 527:    */   {
/* 528:    */     try
/* 529:    */     {
/* 530:765 */       System.arraycopy(input, 0, array, startIndex, length);
/* 531:    */     }
/* 532:    */     catch (Exception ex)
/* 533:    */     {
/* 534:767 */       Exceptions.handle(String.format("array size %d, startIndex %d, input length %d", new Object[] { Integer.valueOf(array.length), Integer.valueOf(startIndex), Integer.valueOf(input.length) }), ex);
/* 535:    */     }
/* 536:    */   }
/* 537:    */   
/* 538:    */   public static void _idx(byte[] output, int ouputStartIndex, byte[] input, int inputOffset, int length)
/* 539:    */   {
/* 540:    */     try
/* 541:    */     {
/* 542:776 */       System.arraycopy(input, inputOffset, output, ouputStartIndex, length);
/* 543:    */     }
/* 544:    */     catch (Exception ex)
/* 545:    */     {
/* 546:778 */       Exceptions.handle(String.format("array size %d, startIndex %d, input length %d", new Object[] { Integer.valueOf(output.length), Integer.valueOf(ouputStartIndex), Integer.valueOf(input.length) }), ex);
/* 547:    */     }
/* 548:    */   }
/* 549:    */   
/* 550:    */   public static int idxUnsignedShort(byte[] buffer, int off)
/* 551:    */   {
/* 552:786 */     int ch1 = buffer[off] & 0xFF;
/* 553:787 */     int ch2 = buffer[(off + 1)] & 0xFF;
/* 554:    */     
/* 555:789 */     return (ch1 << 8) + (ch2 << 0);
/* 556:    */   }
/* 557:    */   
/* 558:    */   public static short idxUnsignedByte(byte[] array, int location)
/* 559:    */   {
/* 560:795 */     return (short)(array[location] & 0xFF);
/* 561:    */   }
/* 562:    */   
/* 563:    */   public static void unsignedIntTo(byte[] b, int off, long val)
/* 564:    */   {
/* 565:800 */     b[(off + 3)] = ((byte)(int)val);
/* 566:801 */     b[(off + 2)] = ((byte)(int)(val >>> 8));
/* 567:802 */     b[(off + 1)] = ((byte)(int)(val >>> 16));
/* 568:803 */     b[off] = ((byte)(int)(val >>> 24));
/* 569:    */   }
/* 570:    */   
/* 571:    */   public static void unsignedShortTo(byte[] buffer, int off, int value)
/* 572:    */   {
/* 573:808 */     buffer[(off + 1)] = ((byte)value);
/* 574:809 */     buffer[off] = ((byte)(value >>> 8));
/* 575:    */   }
/* 576:    */   
/* 577:    */   public static void unsignedByteTo(byte[] buffer, int off, short value)
/* 578:    */   {
/* 579:814 */     buffer[off] = ((byte)value);
/* 580:    */   }
/* 581:    */   
/* 582:    */   public static String utfString(byte[] jsonBytes)
/* 583:    */   {
/* 584:819 */     return new String(jsonBytes, StandardCharsets.UTF_8);
/* 585:    */   }
/* 586:    */   
/* 587:    */   public static int reduceBy(byte[] array, Object object)
/* 588:    */   {
/* 589:826 */     int sum = 0;
/* 590:827 */     for (byte v : array) {
/* 591:828 */       sum = ((Integer)Invoker.invokeReducer(object, Integer.valueOf(sum), Byte.valueOf(v))).intValue();
/* 592:    */     }
/* 593:830 */     return sum;
/* 594:    */   }
/* 595:    */   
/* 596:    */   public static boolean equalsOrDie(byte[] expected, byte[] got)
/* 597:    */   {
/* 598:842 */     if (expected.length != got.length) {
/* 599:843 */       Exceptions.die(new Object[] { "Lengths did not match, expected length", Integer.valueOf(expected.length), "but got", Integer.valueOf(got.length) });
/* 600:    */     }
/* 601:847 */     for (int index = 0; index < expected.length; index++) {
/* 602:848 */       if (expected[index] != got[index]) {
/* 603:849 */         Exceptions.die(new Object[] { "value at index did not match index", Integer.valueOf(index), "expected value", Byte.valueOf(expected[index]), "but got", Byte.valueOf(got[index]) });
/* 604:    */       }
/* 605:    */     }
/* 606:855 */     return true;
/* 607:    */   }
/* 608:    */   
/* 609:    */   public static boolean equals(byte[] expected, byte[] got)
/* 610:    */   {
/* 611:867 */     if (expected.length != got.length) {
/* 612:868 */       return false;
/* 613:    */     }
/* 614:871 */     for (int index = 0; index < expected.length; index++) {
/* 615:872 */       if (expected[index] != got[index]) {
/* 616:873 */         return false;
/* 617:    */       }
/* 618:    */     }
/* 619:876 */     return true;
/* 620:    */   }
/* 621:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.primitive.Byt
 * JD-Core Version:    0.7.0.1
 */