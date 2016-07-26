/*   1:    */ package org.boon.primitive;
/*   2:    */ 
/*   3:    */ import java.io.UnsupportedEncodingException;
/*   4:    */ import org.boon.Exceptions;
/*   5:    */ 
/*   6:    */ public class ByteBuf
/*   7:    */   implements Output
/*   8:    */ {
/*   9: 39 */   protected int capacity = 16;
/*  10: 42 */   protected int length = 0;
/*  11:    */   protected byte[] buffer;
/*  12:    */   
/*  13:    */   public static ByteBuf createExact(int capacity)
/*  14:    */   {
/*  15: 48 */     new ByteBuf(capacity)
/*  16:    */     {
/*  17:    */       public ByteBuf add(byte[] chars)
/*  18:    */       {
/*  19: 50 */         Byt._idx(this.buffer, this.length, chars);
/*  20: 51 */         this.length += chars.length;
/*  21: 52 */         return this;
/*  22:    */       }
/*  23:    */     };
/*  24:    */   }
/*  25:    */   
/*  26:    */   public static ByteBuf create(int capacity)
/*  27:    */   {
/*  28: 58 */     return new ByteBuf(capacity);
/*  29:    */   }
/*  30:    */   
/*  31:    */   public static ByteBuf create(byte[] buffer)
/*  32:    */   {
/*  33: 62 */     ByteBuf buf = new ByteBuf(buffer.length);
/*  34: 63 */     buf.buffer = buffer;
/*  35: 64 */     return buf;
/*  36:    */   }
/*  37:    */   
/*  38:    */   protected ByteBuf(int capacity)
/*  39:    */   {
/*  40: 68 */     this.capacity = capacity;
/*  41: 69 */     init();
/*  42:    */   }
/*  43:    */   
/*  44:    */   protected ByteBuf()
/*  45:    */   {
/*  46: 74 */     init();
/*  47:    */   }
/*  48:    */   
/*  49:    */   private void init()
/*  50:    */   {
/*  51: 78 */     this.buffer = new byte[this.capacity];
/*  52:    */   }
/*  53:    */   
/*  54:    */   public ByteBuf add(String str)
/*  55:    */   {
/*  56: 83 */     add(Byt.bytes(str));
/*  57: 84 */     return this;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public ByteBuf add(int value)
/*  61:    */   {
/*  62: 91 */     if (4 + this.length < this.capacity)
/*  63:    */     {
/*  64: 92 */       Byt.intTo(this.buffer, this.length, value);
/*  65:    */     }
/*  66:    */     else
/*  67:    */     {
/*  68: 94 */       this.buffer = Byt.grow(this.buffer, this.buffer.length * 2 + 4);
/*  69: 95 */       this.capacity = this.buffer.length;
/*  70:    */       
/*  71: 97 */       Byt.intTo(this.buffer, this.length, value);
/*  72:    */     }
/*  73:100 */     this.length += 4;
/*  74:101 */     return this;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public ByteBuf add(float value)
/*  78:    */   {
/*  79:109 */     if (4 + this.length < this.capacity)
/*  80:    */     {
/*  81:110 */       Byt.floatTo(this.buffer, this.length, value);
/*  82:    */     }
/*  83:    */     else
/*  84:    */     {
/*  85:112 */       this.buffer = Byt.grow(this.buffer, this.buffer.length * 2 + 4);
/*  86:113 */       this.capacity = this.buffer.length;
/*  87:    */       
/*  88:115 */       Byt.floatTo(this.buffer, this.length, value);
/*  89:    */     }
/*  90:118 */     this.length += 4;
/*  91:119 */     return this;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public ByteBuf add(char value)
/*  95:    */   {
/*  96:127 */     if (2 + this.length < this.capacity)
/*  97:    */     {
/*  98:128 */       Byt.charTo(this.buffer, this.length, value);
/*  99:    */     }
/* 100:    */     else
/* 101:    */     {
/* 102:130 */       this.buffer = Byt.grow(this.buffer, this.buffer.length * 2 + 2);
/* 103:131 */       this.capacity = this.buffer.length;
/* 104:    */       
/* 105:133 */       Byt.charTo(this.buffer, this.length, value);
/* 106:    */     }
/* 107:136 */     this.length += 2;
/* 108:137 */     return this;
/* 109:    */   }
/* 110:    */   
/* 111:    */   public ByteBuf add(short value)
/* 112:    */   {
/* 113:145 */     if (2 + this.length < this.capacity)
/* 114:    */     {
/* 115:146 */       Byt.shortTo(this.buffer, this.length, value);
/* 116:    */     }
/* 117:    */     else
/* 118:    */     {
/* 119:148 */       this.buffer = Byt.grow(this.buffer, this.buffer.length * 2 + 2);
/* 120:149 */       this.capacity = this.buffer.length;
/* 121:    */       
/* 122:151 */       Byt.shortTo(this.buffer, this.length, value);
/* 123:    */     }
/* 124:154 */     this.length += 2;
/* 125:155 */     return this;
/* 126:    */   }
/* 127:    */   
/* 128:    */   public ByteBuf addByte(int value)
/* 129:    */   {
/* 130:161 */     add((byte)value);
/* 131:162 */     return this;
/* 132:    */   }
/* 133:    */   
/* 134:    */   public ByteBuf add(byte value)
/* 135:    */   {
/* 136:167 */     if (1 + this.length < this.capacity)
/* 137:    */     {
/* 138:168 */       Byt.idx(this.buffer, this.length, value);
/* 139:    */     }
/* 140:    */     else
/* 141:    */     {
/* 142:170 */       this.buffer = Byt.grow(this.buffer);
/* 143:171 */       this.capacity = this.buffer.length;
/* 144:    */       
/* 145:173 */       Byt.idx(this.buffer, this.length, value);
/* 146:    */     }
/* 147:176 */     this.length += 1;
/* 148:    */     
/* 149:178 */     return this;
/* 150:    */   }
/* 151:    */   
/* 152:    */   public ByteBuf add(long value)
/* 153:    */   {
/* 154:184 */     if (8 + this.length < this.capacity)
/* 155:    */     {
/* 156:185 */       Byt.longTo(this.buffer, this.length, value);
/* 157:    */     }
/* 158:    */     else
/* 159:    */     {
/* 160:187 */       this.buffer = Byt.grow(this.buffer, this.buffer.length * 2 + 8);
/* 161:188 */       this.capacity = this.buffer.length;
/* 162:    */       
/* 163:190 */       Byt.longTo(this.buffer, this.length, value);
/* 164:    */     }
/* 165:193 */     this.length += 8;
/* 166:194 */     return this;
/* 167:    */   }
/* 168:    */   
/* 169:    */   public ByteBuf addUnsignedInt(long value)
/* 170:    */   {
/* 171:200 */     if (4 + this.length < this.capacity)
/* 172:    */     {
/* 173:201 */       Byt.unsignedIntTo(this.buffer, this.length, value);
/* 174:    */     }
/* 175:    */     else
/* 176:    */     {
/* 177:203 */       this.buffer = Byt.grow(this.buffer, this.buffer.length * 2 + 4);
/* 178:204 */       this.capacity = this.buffer.length;
/* 179:    */       
/* 180:206 */       Byt.unsignedIntTo(this.buffer, this.length, value);
/* 181:    */     }
/* 182:209 */     this.length += 4;
/* 183:210 */     return this;
/* 184:    */   }
/* 185:    */   
/* 186:    */   public ByteBuf add(double value)
/* 187:    */   {
/* 188:216 */     if (8 + this.length < this.capacity)
/* 189:    */     {
/* 190:217 */       Byt.doubleTo(this.buffer, this.length, value);
/* 191:    */     }
/* 192:    */     else
/* 193:    */     {
/* 194:219 */       this.buffer = Byt.grow(this.buffer, this.buffer.length * 2 + 8);
/* 195:220 */       this.capacity = this.buffer.length;
/* 196:    */       
/* 197:222 */       Byt.doubleTo(this.buffer, this.length, value);
/* 198:    */     }
/* 199:225 */     this.length += 8;
/* 200:226 */     return this;
/* 201:    */   }
/* 202:    */   
/* 203:    */   public ByteBuf add(byte[] array)
/* 204:    */   {
/* 205:232 */     if (array.length + this.length < this.capacity)
/* 206:    */     {
/* 207:233 */       Byt._idx(this.buffer, this.length, array);
/* 208:    */     }
/* 209:    */     else
/* 210:    */     {
/* 211:235 */       this.buffer = Byt.grow(this.buffer, this.buffer.length * 2 + array.length);
/* 212:236 */       this.capacity = this.buffer.length;
/* 213:    */       
/* 214:238 */       Byt._idx(this.buffer, this.length, array);
/* 215:    */     }
/* 216:241 */     this.length += array.length;
/* 217:242 */     return this;
/* 218:    */   }
/* 219:    */   
/* 220:    */   public ByteBuf add(byte[] array, int length)
/* 221:    */   {
/* 222:247 */     if (this.length + length < this.capacity)
/* 223:    */     {
/* 224:248 */       Byt._idx(this.buffer, this.length, array, length);
/* 225:    */     }
/* 226:    */     else
/* 227:    */     {
/* 228:250 */       this.buffer = Byt.grow(this.buffer, this.buffer.length * 2 + length);
/* 229:251 */       this.capacity = this.buffer.length;
/* 230:    */       
/* 231:253 */       Byt._idx(this.buffer, length, array, length);
/* 232:    */     }
/* 233:256 */     this.length += length;
/* 234:257 */     return this;
/* 235:    */   }
/* 236:    */   
/* 237:    */   public ByteBuf add(byte[] array, int offset, int length)
/* 238:    */   {
/* 239:261 */     if (this.length + length < this.capacity)
/* 240:    */     {
/* 241:262 */       Byt._idx(this.buffer, length, array, offset, length);
/* 242:    */     }
/* 243:    */     else
/* 244:    */     {
/* 245:264 */       this.buffer = Byt.grow(this.buffer, this.buffer.length * 2 + length);
/* 246:265 */       this.capacity = this.buffer.length;
/* 247:    */       
/* 248:267 */       Byt._idx(this.buffer, length, array, offset, length);
/* 249:    */     }
/* 250:270 */     this.length += length;
/* 251:271 */     return this;
/* 252:    */   }
/* 253:    */   
/* 254:    */   public byte[] readAndReset()
/* 255:    */   {
/* 256:275 */     byte[] bytes = this.buffer;
/* 257:276 */     this.buffer = null;
/* 258:277 */     return bytes;
/* 259:    */   }
/* 260:    */   
/* 261:    */   public byte[] readForRecycle()
/* 262:    */   {
/* 263:281 */     this.length = 0;
/* 264:282 */     return this.buffer;
/* 265:    */   }
/* 266:    */   
/* 267:    */   public int len()
/* 268:    */   {
/* 269:286 */     return this.length;
/* 270:    */   }
/* 271:    */   
/* 272:    */   public ByteBuf addUrlEncodedByteArray(byte[] value)
/* 273:    */   {
/* 274:292 */     byte[] encoded = new byte[2];
/* 275:294 */     for (int index = 0; index < value.length; index++)
/* 276:    */     {
/* 277:295 */       int i = value[index];
/* 278:297 */       if ((i >= 97) && (i <= 122))
/* 279:    */       {
/* 280:298 */         addByte(i);
/* 281:    */       }
/* 282:299 */       else if ((i >= 65) && (i <= 90))
/* 283:    */       {
/* 284:300 */         addByte(i);
/* 285:    */       }
/* 286:301 */       else if ((i >= 48) && (i <= 57))
/* 287:    */       {
/* 288:302 */         addByte(i);
/* 289:    */       }
/* 290:303 */       else if ((i == 95) || (i == 45) || (i == 46) || (i == 42))
/* 291:    */       {
/* 292:304 */         addByte(i);
/* 293:    */       }
/* 294:305 */       else if (i == 32)
/* 295:    */       {
/* 296:306 */         addByte(43);
/* 297:    */       }
/* 298:    */       else
/* 299:    */       {
/* 300:308 */         ByteScanner.encodeByteIntoTwoAsciiCharBytes(i, encoded);
/* 301:309 */         addByte(37);
/* 302:310 */         addByte(encoded[0]);
/* 303:311 */         addByte(encoded[1]);
/* 304:    */       }
/* 305:    */     }
/* 306:315 */     return this;
/* 307:    */   }
/* 308:    */   
/* 309:    */   public ByteBuf addJSONEncodedByteArray(byte[] value)
/* 310:    */   {
/* 311:320 */     if (value == null)
/* 312:    */     {
/* 313:321 */       add("null");
/* 314:322 */       return this;
/* 315:    */     }
/* 316:326 */     addByte(34);
/* 317:328 */     for (int index = 0; index < value.length; index++)
/* 318:    */     {
/* 319:329 */       int ch = value[index];
/* 320:332 */       switch (ch)
/* 321:    */       {
/* 322:    */       case 34: 
/* 323:334 */         addByte(92);
/* 324:335 */         addByte(34);
/* 325:336 */         break;
/* 326:    */       case 92: 
/* 327:339 */         addByte(92);
/* 328:340 */         addByte(92);
/* 329:341 */         break;
/* 330:    */       case 47: 
/* 331:344 */         addByte(92);
/* 332:345 */         addByte(47);
/* 333:346 */         break;
/* 334:    */       case 10: 
/* 335:349 */         addByte(92);
/* 336:350 */         addByte(110);
/* 337:351 */         break;
/* 338:    */       case 9: 
/* 339:354 */         addByte(92);
/* 340:355 */         addByte(116);
/* 341:356 */         break;
/* 342:    */       case 13: 
/* 343:359 */         addByte(92);
/* 344:360 */         addByte(114);
/* 345:361 */         break;
/* 346:    */       case 8: 
/* 347:364 */         addByte(92);
/* 348:365 */         addByte(98);
/* 349:366 */         break;
/* 350:    */       case 12: 
/* 351:369 */         addByte(92);
/* 352:370 */         addByte(102);
/* 353:371 */         break;
/* 354:    */       default: 
/* 355:375 */         if (ch > 127)
/* 356:    */         {
/* 357:376 */           addByte(92);
/* 358:377 */           addByte(117);
/* 359:378 */           addByte(48);
/* 360:379 */           addByte(48);
/* 361:380 */           byte[] encoded = new byte[2];
/* 362:381 */           ByteScanner.encodeByteIntoTwoAsciiCharBytes(ch, encoded);
/* 363:382 */           addByte(encoded[0]);
/* 364:383 */           addByte(encoded[1]);
/* 365:    */         }
/* 366:    */         else
/* 367:    */         {
/* 368:386 */           addByte(ch);
/* 369:    */         }
/* 370:    */         break;
/* 371:    */       }
/* 372:    */     }
/* 373:392 */     addByte(34);
/* 374:393 */     return this;
/* 375:    */   }
/* 376:    */   
/* 377:    */   public ByteBuf addUrlEncoded(String key)
/* 378:    */   {
/* 379:    */     try
/* 380:    */     {
/* 381:399 */       addUrlEncodedByteArray(key.getBytes("UTF-8"));
/* 382:    */     }
/* 383:    */     catch (UnsupportedEncodingException e)
/* 384:    */     {
/* 385:401 */       Exceptions.handle(e);
/* 386:    */     }
/* 387:403 */     return this;
/* 388:    */   }
/* 389:    */   
/* 390:    */   public ByteBuf addJSONEncodedString(String value)
/* 391:    */   {
/* 392:    */     try
/* 393:    */     {
/* 394:408 */       addJSONEncodedByteArray(value == null ? null : value.getBytes("UTF-8"));
/* 395:    */     }
/* 396:    */     catch (UnsupportedEncodingException e)
/* 397:    */     {
/* 398:410 */       Exceptions.handle(e);
/* 399:    */     }
/* 400:412 */     return this;
/* 401:    */   }
/* 402:    */   
/* 403:    */   public void write(int b)
/* 404:    */   {
/* 405:417 */     addByte(b);
/* 406:    */   }
/* 407:    */   
/* 408:    */   public void write(byte[] b)
/* 409:    */   {
/* 410:422 */     add(b);
/* 411:    */   }
/* 412:    */   
/* 413:    */   public void write(byte[] b, int off, int len)
/* 414:    */   {
/* 415:427 */     add(b, len);
/* 416:    */   }
/* 417:    */   
/* 418:    */   public void writeBoolean(boolean v)
/* 419:    */   {
/* 420:432 */     if (v == true) {
/* 421:433 */       addByte(1);
/* 422:    */     } else {
/* 423:435 */       addByte(0);
/* 424:    */     }
/* 425:    */   }
/* 426:    */   
/* 427:    */   public void writeByte(byte v)
/* 428:    */   {
/* 429:441 */     addByte(v);
/* 430:    */   }
/* 431:    */   
/* 432:    */   public void writeUnsignedByte(short v)
/* 433:    */   {
/* 434:446 */     addUnsignedByte(v);
/* 435:    */   }
/* 436:    */   
/* 437:    */   public void addUnsignedByte(short value)
/* 438:    */   {
/* 439:450 */     if (1 + this.length < this.capacity)
/* 440:    */     {
/* 441:451 */       Byt.unsignedByteTo(this.buffer, this.length, value);
/* 442:    */     }
/* 443:    */     else
/* 444:    */     {
/* 445:453 */       this.buffer = Byt.grow(this.buffer, this.buffer.length * 2 + 1);
/* 446:454 */       this.capacity = this.buffer.length;
/* 447:    */       
/* 448:456 */       Byt.unsignedByteTo(this.buffer, this.length, value);
/* 449:    */     }
/* 450:459 */     this.length += 1;
/* 451:    */   }
/* 452:    */   
/* 453:    */   public void writeShort(short v)
/* 454:    */   {
/* 455:465 */     add(v);
/* 456:    */   }
/* 457:    */   
/* 458:    */   public void writeUnsignedShort(int v)
/* 459:    */   {
/* 460:470 */     addUnsignedShort(v);
/* 461:    */   }
/* 462:    */   
/* 463:    */   public void addUnsignedShort(int value)
/* 464:    */   {
/* 465:475 */     if (2 + this.length < this.capacity)
/* 466:    */     {
/* 467:476 */       Byt.unsignedShortTo(this.buffer, this.length, value);
/* 468:    */     }
/* 469:    */     else
/* 470:    */     {
/* 471:478 */       this.buffer = Byt.grow(this.buffer, this.buffer.length * 2 + 2);
/* 472:479 */       this.capacity = this.buffer.length;
/* 473:    */       
/* 474:481 */       Byt.unsignedShortTo(this.buffer, this.length, value);
/* 475:    */     }
/* 476:484 */     this.length += 2;
/* 477:    */   }
/* 478:    */   
/* 479:    */   public void writeChar(char v)
/* 480:    */   {
/* 481:492 */     add(v);
/* 482:    */   }
/* 483:    */   
/* 484:    */   public void writeInt(int v)
/* 485:    */   {
/* 486:497 */     add(v);
/* 487:    */   }
/* 488:    */   
/* 489:    */   public void writeUnsignedInt(long v)
/* 490:    */   {
/* 491:502 */     addUnsignedInt(v);
/* 492:    */   }
/* 493:    */   
/* 494:    */   public void writeLong(long v)
/* 495:    */   {
/* 496:507 */     add(v);
/* 497:    */   }
/* 498:    */   
/* 499:    */   public void writeFloat(float v)
/* 500:    */   {
/* 501:512 */     add(v);
/* 502:    */   }
/* 503:    */   
/* 504:    */   public void writeDouble(double v)
/* 505:    */   {
/* 506:517 */     add(v);
/* 507:    */   }
/* 508:    */   
/* 509:    */   public void writeLargeString(String s)
/* 510:    */   {
/* 511:522 */     byte[] bytes = Byt.bytes(s);
/* 512:523 */     add(bytes.length);
/* 513:524 */     add(bytes);
/* 514:    */   }
/* 515:    */   
/* 516:    */   public void writeSmallString(String s)
/* 517:    */   {
/* 518:529 */     byte[] bytes = Byt.bytes(s);
/* 519:530 */     addUnsignedByte((short)bytes.length);
/* 520:531 */     add(bytes);
/* 521:    */   }
/* 522:    */   
/* 523:    */   public void writeMediumString(String s)
/* 524:    */   {
/* 525:536 */     byte[] bytes = Byt.bytes(s);
/* 526:537 */     addUnsignedShort(bytes.length);
/* 527:538 */     add(bytes);
/* 528:    */   }
/* 529:    */   
/* 530:    */   public void writeLargeByteArray(byte[] bytes)
/* 531:    */   {
/* 532:543 */     add(bytes.length);
/* 533:544 */     add(bytes);
/* 534:    */   }
/* 535:    */   
/* 536:    */   public void writeSmallByteArray(byte[] bytes)
/* 537:    */   {
/* 538:549 */     addUnsignedByte((short)bytes.length);
/* 539:550 */     add(bytes);
/* 540:    */   }
/* 541:    */   
/* 542:    */   public void writeMediumByteArray(byte[] bytes)
/* 543:    */   {
/* 544:555 */     addUnsignedShort(bytes.length);
/* 545:556 */     add(bytes);
/* 546:    */   }
/* 547:    */   
/* 548:    */   public void writeLargeShortArray(short[] values)
/* 549:    */   {
/* 550:561 */     int byteSize = values.length * 2 + 4;
/* 551:562 */     add(values.length);
/* 552:563 */     doWriteShortArray(values, byteSize);
/* 553:    */   }
/* 554:    */   
/* 555:    */   public void writeSmallShortArray(short[] values)
/* 556:    */   {
/* 557:568 */     int byteSize = values.length * 2 + 1;
/* 558:569 */     addUnsignedByte((short)values.length);
/* 559:570 */     doWriteShortArray(values, byteSize);
/* 560:    */   }
/* 561:    */   
/* 562:    */   public void writeMediumShortArray(short[] values)
/* 563:    */   {
/* 564:575 */     int byteSize = values.length * 2 + 2;
/* 565:576 */     addUnsignedShort(values.length);
/* 566:577 */     doWriteShortArray(values, byteSize);
/* 567:    */   }
/* 568:    */   
/* 569:    */   private void doWriteShortArray(short[] values, int byteSize)
/* 570:    */   {
/* 571:582 */     if (byteSize + this.length >= this.capacity) {
/* 572:583 */       this.buffer = Byt.grow(this.buffer, this.buffer.length * 2 + byteSize);
/* 573:    */     }
/* 574:585 */     for (int index = 0; index < values.length; index++) {
/* 575:586 */       add(values[index]);
/* 576:    */     }
/* 577:    */   }
/* 578:    */   
/* 579:    */   public void writeLargeIntArray(int[] values)
/* 580:    */   {
/* 581:593 */     int byteSize = values.length * 4 + 4;
/* 582:594 */     add(values.length);
/* 583:595 */     doWriteIntArray(values, byteSize);
/* 584:    */   }
/* 585:    */   
/* 586:    */   public void writeSmallIntArray(int[] values)
/* 587:    */   {
/* 588:600 */     int byteSize = values.length * 4 + 1;
/* 589:601 */     addUnsignedByte((short)values.length);
/* 590:602 */     doWriteIntArray(values, byteSize);
/* 591:    */   }
/* 592:    */   
/* 593:    */   public void writeMediumIntArray(int[] values)
/* 594:    */   {
/* 595:607 */     int byteSize = values.length * 4 + 2;
/* 596:608 */     addUnsignedShort(values.length);
/* 597:609 */     doWriteIntArray(values, byteSize);
/* 598:    */   }
/* 599:    */   
/* 600:    */   private void doWriteIntArray(int[] values, int byteSize)
/* 601:    */   {
/* 602:614 */     if (byteSize + this.length >= this.capacity) {
/* 603:615 */       this.buffer = Byt.grow(this.buffer, this.buffer.length * 2 + byteSize);
/* 604:    */     }
/* 605:617 */     for (int index = 0; index < values.length; index++) {
/* 606:618 */       add(values[index]);
/* 607:    */     }
/* 608:    */   }
/* 609:    */   
/* 610:    */   public Input input()
/* 611:    */   {
/* 612:623 */     return new InputByteArray(this.buffer);
/* 613:    */   }
/* 614:    */   
/* 615:    */   public void writeLargeLongArray(long[] values)
/* 616:    */   {
/* 617:629 */     int byteSize = values.length * 8 + 4;
/* 618:630 */     add(values.length);
/* 619:631 */     doWriteLongArray(values, byteSize);
/* 620:    */   }
/* 621:    */   
/* 622:    */   public void writeSmallLongArray(long[] values)
/* 623:    */   {
/* 624:636 */     int byteSize = values.length * 8 + 1;
/* 625:637 */     addUnsignedByte((short)values.length);
/* 626:638 */     doWriteLongArray(values, byteSize);
/* 627:    */   }
/* 628:    */   
/* 629:    */   public void writeMediumLongArray(long[] values)
/* 630:    */   {
/* 631:643 */     int byteSize = values.length * 8 + 2;
/* 632:644 */     addUnsignedShort(values.length);
/* 633:645 */     doWriteLongArray(values, byteSize);
/* 634:    */   }
/* 635:    */   
/* 636:    */   private void doWriteLongArray(long[] values, int byteSize)
/* 637:    */   {
/* 638:650 */     if (byteSize + this.length >= this.capacity) {
/* 639:651 */       this.buffer = Byt.grow(this.buffer, this.buffer.length * 2 + byteSize);
/* 640:    */     }
/* 641:653 */     for (int index = 0; index < values.length; index++) {
/* 642:654 */       add(values[index]);
/* 643:    */     }
/* 644:    */   }
/* 645:    */   
/* 646:    */   public void writeLargeFloatArray(float[] values)
/* 647:    */   {
/* 648:661 */     int byteSize = values.length * 4 + 4;
/* 649:662 */     add(values.length);
/* 650:663 */     doWriteFloatArray(values, byteSize);
/* 651:    */   }
/* 652:    */   
/* 653:    */   public void writeSmallFloatArray(float[] values)
/* 654:    */   {
/* 655:669 */     int byteSize = values.length * 4 + 1;
/* 656:670 */     addUnsignedByte((short)values.length);
/* 657:671 */     doWriteFloatArray(values, byteSize);
/* 658:    */   }
/* 659:    */   
/* 660:    */   public void writeMediumFloatArray(float[] values)
/* 661:    */   {
/* 662:676 */     int byteSize = values.length * 4 + 2;
/* 663:677 */     addUnsignedShort(values.length);
/* 664:678 */     doWriteFloatArray(values, byteSize);
/* 665:    */   }
/* 666:    */   
/* 667:    */   private void doWriteFloatArray(float[] values, int byteSize)
/* 668:    */   {
/* 669:683 */     if (byteSize + this.length >= this.capacity) {
/* 670:684 */       this.buffer = Byt.grow(this.buffer, this.buffer.length * 2 + byteSize);
/* 671:    */     }
/* 672:686 */     for (int index = 0; index < values.length; index++) {
/* 673:687 */       add(values[index]);
/* 674:    */     }
/* 675:    */   }
/* 676:    */   
/* 677:    */   public void writeLargeDoubleArray(double[] values)
/* 678:    */   {
/* 679:694 */     int byteSize = values.length * 8 + 4;
/* 680:695 */     add(values.length);
/* 681:696 */     doWriteDoubleArray(values, byteSize);
/* 682:    */   }
/* 683:    */   
/* 684:    */   public void writeSmallDoubleArray(double[] values)
/* 685:    */   {
/* 686:703 */     int byteSize = values.length * 8 + 1;
/* 687:704 */     addUnsignedByte((short)values.length);
/* 688:705 */     doWriteDoubleArray(values, byteSize);
/* 689:    */   }
/* 690:    */   
/* 691:    */   public void writeMediumDoubleArray(double[] values)
/* 692:    */   {
/* 693:711 */     int byteSize = values.length * 8 + 2;
/* 694:712 */     addUnsignedShort(values.length);
/* 695:713 */     doWriteDoubleArray(values, byteSize);
/* 696:    */   }
/* 697:    */   
/* 698:    */   private void doWriteDoubleArray(double[] values, int byteSize)
/* 699:    */   {
/* 700:719 */     if (byteSize + this.length >= this.capacity) {
/* 701:720 */       this.buffer = Byt.grow(this.buffer, this.buffer.length * 2 + byteSize);
/* 702:    */     }
/* 703:722 */     for (int index = 0; index < values.length; index++) {
/* 704:723 */       add(values[index]);
/* 705:    */     }
/* 706:    */   }
/* 707:    */   
/* 708:    */   public String toString()
/* 709:    */   {
/* 710:729 */     int len = len();
/* 711:    */     
/* 712:731 */     char[] chars = new char[this.buffer.length];
/* 713:732 */     for (int index = 0; index < chars.length; index++) {
/* 714:733 */       chars[index] = ((char)this.buffer[index]);
/* 715:    */     }
/* 716:735 */     return new String(chars, 0, len);
/* 717:    */   }
/* 718:    */   
/* 719:    */   public byte[] toBytes()
/* 720:    */   {
/* 721:741 */     return Byt.slc(this.buffer, 0, this.length);
/* 722:    */   }
/* 723:    */   
/* 724:    */   public byte[] slc(int startIndex, int endIndex)
/* 725:    */   {
/* 726:746 */     return Byt.slc(this.buffer, startIndex, endIndex);
/* 727:    */   }
/* 728:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.primitive.ByteBuf
 * JD-Core Version:    0.7.0.1
 */