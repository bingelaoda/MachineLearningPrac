/*   1:    */ package weka.core.expressionlanguage.parser;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.Reader;
/*   5:    */ import java_cup.runtime.Symbol;
/*   6:    */ import weka.core.expressionlanguage.core.SyntaxException;
/*   7:    */ 
/*   8:    */ public class Scanner
/*   9:    */   implements java_cup.runtime.Scanner
/*  10:    */ {
/*  11:    */   public static final int YYEOF = -1;
/*  12:    */   private static final int ZZ_BUFFERSIZE = 16384;
/*  13:    */   public static final int YYINITIAL = 0;
/*  14:    */   public static final int STRING1 = 2;
/*  15:    */   public static final int STRING2 = 4;
/*  16: 61 */   private static final int[] ZZ_LEXSTATE = { 0, 0, 1, 1, 2, 2 };
/*  17: 68 */   private static final char[] ZZ_CMAP = { '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '#', '&', '#', '#', '#', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '#', '\032', '\013', '\000', '\000', '\000', '\025', '\f', '\r', '\016', '\022', '\020', '\017', '\021', '\002', '\023', '\001', '\001', '\001', '\001', '\001', '\001', '\001', '\001', '\001', '\001', '\000', '\000', '\034', '\033', '\035', '\000', '\000', '"', '"', '"', '"', '"', '"', '"', '"', '"', '"', '"', '"', '"', '"', '"', '"', '"', '"', '"', '"', '"', '"', '"', '"', '"', '"', '\000', '$', '\000', '\024', '"', '\000', '\b', '%', '"', '\027', '\006', '\007', '\037', '"', '\036', '"', '"', '\t', '"', '\026', '\031', '!', '"', '\004', '\n', '\003', '\005', '"', '"', ' ', '"', '"', '\000', '\030', '\000', '\000', '\000' };
/*  18: 82 */   private static final int[] ZZ_ACTION = zzUnpackAction();
/*  19:    */   private static final String ZZ_ACTION_PACKED_0 = "";
/*  20:    */   
/*  21:    */   private static int[] zzUnpackAction()
/*  22:    */   {
/*  23: 94 */     int[] result = new int[63];
/*  24: 95 */     int offset = 0;
/*  25: 96 */     offset = zzUnpackAction("", offset, result);
/*  26: 97 */     return result;
/*  27:    */   }
/*  28:    */   
/*  29:    */   private static int zzUnpackAction(String packed, int offset, int[] result)
/*  30:    */   {
/*  31:101 */     int i = 0;
/*  32:102 */     int j = offset;
/*  33:103 */     int l = packed.length();
/*  34:    */     int count;
/*  35:104 */     for (; i < l; count > 0)
/*  36:    */     {
/*  37:105 */       count = packed.charAt(i++);
/*  38:106 */       int value = packed.charAt(i++);
/*  39:107 */       result[(j++)] = value;count--;
/*  40:    */     }
/*  41:109 */     return j;
/*  42:    */   }
/*  43:    */   
/*  44:116 */   private static final int[] ZZ_ROWMAP = zzUnpackRowMap();
/*  45:    */   private static final String ZZ_ROWMAP_PACKED_0 = "";
/*  46:    */   
/*  47:    */   private static int[] zzUnpackRowMap()
/*  48:    */   {
/*  49:129 */     int[] result = new int[63];
/*  50:130 */     int offset = 0;
/*  51:131 */     offset = zzUnpackRowMap("", offset, result);
/*  52:132 */     return result;
/*  53:    */   }
/*  54:    */   
/*  55:    */   private static int zzUnpackRowMap(String packed, int offset, int[] result)
/*  56:    */   {
/*  57:136 */     int i = 0;
/*  58:137 */     int j = offset;
/*  59:138 */     int l = packed.length();
/*  60:139 */     while (i < l)
/*  61:    */     {
/*  62:140 */       int high = packed.charAt(i++) << '\020';
/*  63:141 */       result[(j++)] = (high | packed.charAt(i++));
/*  64:    */     }
/*  65:143 */     return j;
/*  66:    */   }
/*  67:    */   
/*  68:149 */   private static final int[] ZZ_TRANS = zzUnpackTrans();
/*  69:    */   private static final String ZZ_TRANS_PACKED_0 = "";
/*  70:    */   private static final int ZZ_UNKNOWN_ERROR = 0;
/*  71:    */   private static final int ZZ_NO_MATCH = 1;
/*  72:    */   private static final int ZZ_PUSHBACK_2BIG = 2;
/*  73:    */   
/*  74:    */   private static int[] zzUnpackTrans()
/*  75:    */   {
/*  76:195 */     int[] result = new int[1092];
/*  77:196 */     int offset = 0;
/*  78:197 */     offset = zzUnpackTrans("", offset, result);
/*  79:198 */     return result;
/*  80:    */   }
/*  81:    */   
/*  82:    */   private static int zzUnpackTrans(String packed, int offset, int[] result)
/*  83:    */   {
/*  84:202 */     int i = 0;
/*  85:203 */     int j = offset;
/*  86:204 */     int l = packed.length();
/*  87:    */     int count;
/*  88:205 */     for (; i < l; count > 0)
/*  89:    */     {
/*  90:206 */       count = packed.charAt(i++);
/*  91:207 */       int value = packed.charAt(i++);
/*  92:208 */       value--;
/*  93:209 */       result[(j++)] = value;count--;
/*  94:    */     }
/*  95:211 */     return j;
/*  96:    */   }
/*  97:    */   
/*  98:221 */   private static final String[] ZZ_ERROR_MSG = { "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large" };
/*  99:230 */   private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
/* 100:    */   private static final String ZZ_ATTRIBUTE_PACKED_0 = "";
/* 101:    */   private Reader zzReader;
/* 102:    */   private int zzState;
/* 103:    */   
/* 104:    */   private static int[] zzUnpackAttribute()
/* 105:    */   {
/* 106:237 */     int[] result = new int[63];
/* 107:238 */     int offset = 0;
/* 108:239 */     offset = zzUnpackAttribute("", offset, result);
/* 109:240 */     return result;
/* 110:    */   }
/* 111:    */   
/* 112:    */   private static int zzUnpackAttribute(String packed, int offset, int[] result)
/* 113:    */   {
/* 114:244 */     int i = 0;
/* 115:245 */     int j = offset;
/* 116:246 */     int l = packed.length();
/* 117:    */     int count;
/* 118:247 */     for (; i < l; count > 0)
/* 119:    */     {
/* 120:248 */       count = packed.charAt(i++);
/* 121:249 */       int value = packed.charAt(i++);
/* 122:250 */       result[(j++)] = value;count--;
/* 123:    */     }
/* 124:252 */     return j;
/* 125:    */   }
/* 126:    */   
/* 127:262 */   private int zzLexicalState = 0;
/* 128:266 */   private char[] zzBuffer = new char[16384];
/* 129:    */   private int zzMarkedPos;
/* 130:    */   private int zzCurrentPos;
/* 131:    */   private int zzStartRead;
/* 132:    */   private int zzEndRead;
/* 133:    */   private int yyline;
/* 134:    */   private int yychar;
/* 135:    */   private int yycolumn;
/* 136:296 */   private boolean zzAtBOL = true;
/* 137:    */   private boolean zzAtEOF;
/* 138:    */   private boolean zzEOFDone;
/* 139:310 */   private int zzFinalHighSurrogate = 0;
/* 140:313 */   private StringBuilder string = new StringBuilder();
/* 141:    */   
/* 142:    */   private Symbol symbol(int type)
/* 143:    */   {
/* 144:316 */     return new Symbol(type);
/* 145:    */   }
/* 146:    */   
/* 147:    */   private Symbol symbol(int type, Object obj)
/* 148:    */   {
/* 149:320 */     return new Symbol(type, obj);
/* 150:    */   }
/* 151:    */   
/* 152:    */   public Scanner(Reader in)
/* 153:    */   {
/* 154:330 */     this.zzReader = in;
/* 155:    */   }
/* 156:    */   
/* 157:    */   private boolean zzRefill()
/* 158:    */     throws IOException
/* 159:    */   {
/* 160:345 */     if (this.zzStartRead > 0)
/* 161:    */     {
/* 162:346 */       this.zzEndRead += this.zzFinalHighSurrogate;
/* 163:347 */       this.zzFinalHighSurrogate = 0;
/* 164:348 */       System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
/* 165:    */       
/* 166:    */ 
/* 167:    */ 
/* 168:    */ 
/* 169:353 */       this.zzEndRead -= this.zzStartRead;
/* 170:354 */       this.zzCurrentPos -= this.zzStartRead;
/* 171:355 */       this.zzMarkedPos -= this.zzStartRead;
/* 172:356 */       this.zzStartRead = 0;
/* 173:    */     }
/* 174:360 */     if (this.zzCurrentPos >= this.zzBuffer.length - this.zzFinalHighSurrogate)
/* 175:    */     {
/* 176:362 */       char[] newBuffer = new char[this.zzBuffer.length * 2];
/* 177:363 */       System.arraycopy(this.zzBuffer, 0, newBuffer, 0, this.zzBuffer.length);
/* 178:364 */       this.zzBuffer = newBuffer;
/* 179:365 */       this.zzEndRead += this.zzFinalHighSurrogate;
/* 180:366 */       this.zzFinalHighSurrogate = 0;
/* 181:    */     }
/* 182:370 */     int requested = this.zzBuffer.length - this.zzEndRead;
/* 183:371 */     int totalRead = 0;
/* 184:372 */     while (totalRead < requested)
/* 185:    */     {
/* 186:373 */       int numRead = this.zzReader.read(this.zzBuffer, this.zzEndRead + totalRead, requested - totalRead);
/* 187:374 */       if (numRead == -1) {
/* 188:    */         break;
/* 189:    */       }
/* 190:377 */       totalRead += numRead;
/* 191:    */     }
/* 192:380 */     if (totalRead > 0)
/* 193:    */     {
/* 194:381 */       this.zzEndRead += totalRead;
/* 195:382 */       if ((totalRead == requested) && 
/* 196:383 */         (Character.isHighSurrogate(this.zzBuffer[(this.zzEndRead - 1)])))
/* 197:    */       {
/* 198:384 */         this.zzEndRead -= 1;
/* 199:385 */         this.zzFinalHighSurrogate = 1;
/* 200:    */       }
/* 201:388 */       return false;
/* 202:    */     }
/* 203:392 */     return true;
/* 204:    */   }
/* 205:    */   
/* 206:    */   public final void yyclose()
/* 207:    */     throws IOException
/* 208:    */   {
/* 209:400 */     this.zzAtEOF = true;
/* 210:401 */     this.zzEndRead = this.zzStartRead;
/* 211:403 */     if (this.zzReader != null) {
/* 212:404 */       this.zzReader.close();
/* 213:    */     }
/* 214:    */   }
/* 215:    */   
/* 216:    */   public final void yyreset(Reader reader)
/* 217:    */   {
/* 218:421 */     this.zzReader = reader;
/* 219:422 */     this.zzAtBOL = true;
/* 220:423 */     this.zzAtEOF = false;
/* 221:424 */     this.zzEOFDone = false;
/* 222:425 */     this.zzEndRead = (this.zzStartRead = 0);
/* 223:426 */     this.zzCurrentPos = (this.zzMarkedPos = 0);
/* 224:427 */     this.zzFinalHighSurrogate = 0;
/* 225:428 */     this.yyline = (this.yychar = this.yycolumn = 0);
/* 226:429 */     this.zzLexicalState = 0;
/* 227:430 */     if (this.zzBuffer.length > 16384) {
/* 228:431 */       this.zzBuffer = new char[16384];
/* 229:    */     }
/* 230:    */   }
/* 231:    */   
/* 232:    */   public final int yystate()
/* 233:    */   {
/* 234:439 */     return this.zzLexicalState;
/* 235:    */   }
/* 236:    */   
/* 237:    */   public final void yybegin(int newState)
/* 238:    */   {
/* 239:449 */     this.zzLexicalState = newState;
/* 240:    */   }
/* 241:    */   
/* 242:    */   public final String yytext()
/* 243:    */   {
/* 244:457 */     return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
/* 245:    */   }
/* 246:    */   
/* 247:    */   public final char yycharat(int pos)
/* 248:    */   {
/* 249:473 */     return this.zzBuffer[(this.zzStartRead + pos)];
/* 250:    */   }
/* 251:    */   
/* 252:    */   public final int yylength()
/* 253:    */   {
/* 254:481 */     return this.zzMarkedPos - this.zzStartRead;
/* 255:    */   }
/* 256:    */   
/* 257:    */   private void zzScanError(int errorCode)
/* 258:    */     throws SyntaxException
/* 259:    */   {
/* 260:    */     String message;
/* 261:    */     try
/* 262:    */     {
/* 263:502 */       message = ZZ_ERROR_MSG[errorCode];
/* 264:    */     }
/* 265:    */     catch (ArrayIndexOutOfBoundsException e)
/* 266:    */     {
/* 267:505 */       message = ZZ_ERROR_MSG[0];
/* 268:    */     }
/* 269:508 */     throw new SyntaxException(message);
/* 270:    */   }
/* 271:    */   
/* 272:    */   public void yypushback(int number)
/* 273:    */     throws SyntaxException
/* 274:    */   {
/* 275:521 */     if (number > yylength()) {
/* 276:522 */       zzScanError(2);
/* 277:    */     }
/* 278:524 */     this.zzMarkedPos -= number;
/* 279:    */   }
/* 280:    */   
/* 281:    */   public Symbol next_token()
/* 282:    */     throws IOException, SyntaxException
/* 283:    */   {
/* 284:542 */     int zzEndReadL = this.zzEndRead;
/* 285:543 */     char[] zzBufferL = this.zzBuffer;
/* 286:544 */     char[] zzCMapL = ZZ_CMAP;
/* 287:    */     
/* 288:546 */     int[] zzTransL = ZZ_TRANS;
/* 289:547 */     int[] zzRowMapL = ZZ_ROWMAP;
/* 290:548 */     int[] zzAttrL = ZZ_ATTRIBUTE;
/* 291:    */     for (;;)
/* 292:    */     {
/* 293:551 */       int zzMarkedPosL = this.zzMarkedPos;
/* 294:    */       
/* 295:553 */       int zzAction = -1;
/* 296:    */       
/* 297:555 */       int zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
/* 298:    */       
/* 299:557 */       this.zzState = ZZ_LEXSTATE[this.zzLexicalState];
/* 300:    */       
/* 301:    */ 
/* 302:560 */       int zzAttributes = zzAttrL[this.zzState];
/* 303:561 */       if ((zzAttributes & 0x1) == 1) {
/* 304:562 */         zzAction = this.zzState;
/* 305:    */       }
/* 306:    */       int zzInput;
/* 307:    */       for (;;)
/* 308:    */       {
/* 309:569 */         if (zzCurrentPosL < zzEndReadL)
/* 310:    */         {
/* 311:570 */           int zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
/* 312:571 */           zzCurrentPosL += Character.charCount(zzInput);
/* 313:    */         }
/* 314:    */         else
/* 315:    */         {
/* 316:573 */           if (this.zzAtEOF)
/* 317:    */           {
/* 318:574 */             int zzInput = -1;
/* 319:575 */             break;
/* 320:    */           }
/* 321:579 */           this.zzCurrentPos = zzCurrentPosL;
/* 322:580 */           this.zzMarkedPos = zzMarkedPosL;
/* 323:581 */           boolean eof = zzRefill();
/* 324:    */           
/* 325:583 */           zzCurrentPosL = this.zzCurrentPos;
/* 326:584 */           zzMarkedPosL = this.zzMarkedPos;
/* 327:585 */           zzBufferL = this.zzBuffer;
/* 328:586 */           zzEndReadL = this.zzEndRead;
/* 329:587 */           if (eof)
/* 330:    */           {
/* 331:588 */             int zzInput = -1;
/* 332:589 */             break;
/* 333:    */           }
/* 334:592 */           zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
/* 335:593 */           zzCurrentPosL += Character.charCount(zzInput);
/* 336:    */         }
/* 337:596 */         int zzNext = zzTransL[(zzRowMapL[this.zzState] + zzCMapL[zzInput])];
/* 338:597 */         if (zzNext == -1) {
/* 339:    */           break;
/* 340:    */         }
/* 341:598 */         this.zzState = zzNext;
/* 342:    */         
/* 343:600 */         zzAttributes = zzAttrL[this.zzState];
/* 344:601 */         if ((zzAttributes & 0x1) == 1)
/* 345:    */         {
/* 346:602 */           zzAction = this.zzState;
/* 347:603 */           zzMarkedPosL = zzCurrentPosL;
/* 348:604 */           if ((zzAttributes & 0x8) == 8) {
/* 349:    */             break;
/* 350:    */           }
/* 351:    */         }
/* 352:    */       }
/* 353:611 */       this.zzMarkedPos = zzMarkedPosL;
/* 354:613 */       switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction])
/* 355:    */       {
/* 356:    */       case 1: 
/* 357:615 */         throw new SyntaxException("Illegal character " + yytext() + "!");
/* 358:    */       case 38: 
/* 359:    */         break;
/* 360:    */       case 2: 
/* 361:619 */         return symbol(3, Double.valueOf(yytext()));
/* 362:    */       case 39: 
/* 363:    */         break;
/* 364:    */       case 3: 
/* 365:623 */         return symbol(2, yytext());
/* 366:    */       case 40: 
/* 367:    */         break;
/* 368:    */       case 4: 
/* 369:627 */         yybegin(2);this.string.setLength(0);
/* 370:    */       case 41: 
/* 371:    */         break;
/* 372:    */       case 5: 
/* 373:631 */         yybegin(4);this.string.setLength(0);
/* 374:    */       case 42: 
/* 375:    */         break;
/* 376:    */       case 6: 
/* 377:635 */         return symbol(6);
/* 378:    */       case 43: 
/* 379:    */         break;
/* 380:    */       case 7: 
/* 381:639 */         return symbol(7);
/* 382:    */       case 44: 
/* 383:    */         break;
/* 384:    */       case 8: 
/* 385:643 */         return symbol(8);
/* 386:    */       case 45: 
/* 387:    */         break;
/* 388:    */       case 9: 
/* 389:647 */         return symbol(9);
/* 390:    */       case 46: 
/* 391:    */         break;
/* 392:    */       case 10: 
/* 393:651 */         return symbol(10);
/* 394:    */       case 47: 
/* 395:    */         break;
/* 396:    */       case 11: 
/* 397:655 */         return symbol(11);
/* 398:    */       case 48: 
/* 399:    */         break;
/* 400:    */       case 12: 
/* 401:659 */         return symbol(12);
/* 402:    */       case 49: 
/* 403:    */         break;
/* 404:    */       case 13: 
/* 405:663 */         return symbol(13);
/* 406:    */       case 50: 
/* 407:    */         break;
/* 408:    */       case 14: 
/* 409:667 */         return symbol(16);
/* 410:    */       case 51: 
/* 411:    */         break;
/* 412:    */       case 15: 
/* 413:671 */         return symbol(17);
/* 414:    */       case 52: 
/* 415:    */         break;
/* 416:    */       case 16: 
/* 417:675 */         return symbol(18);
/* 418:    */       case 53: 
/* 419:    */         break;
/* 420:    */       case 17: 
/* 421:679 */         return symbol(19);
/* 422:    */       case 54: 
/* 423:    */         break;
/* 424:    */       case 18: 
/* 425:683 */         return symbol(20);
/* 426:    */       case 55: 
/* 427:    */         break;
/* 428:    */       case 19: 
/* 429:687 */         return symbol(22);
/* 430:    */       case 56: 
/* 431:    */         break;
/* 432:    */       case 20: 
/* 433:    */       case 57: 
/* 434:    */         break;
/* 435:    */       case 21: 
/* 436:695 */         this.string.append(yytext());
/* 437:    */       case 58: 
/* 438:    */         break;
/* 439:    */       case 22: 
/* 440:699 */         yybegin(0);return symbol(4, this.string.toString());
/* 441:    */       case 59: 
/* 442:    */         break;
/* 443:    */       case 23: 
/* 444:703 */         return symbol(21);
/* 445:    */       case 60: 
/* 446:    */         break;
/* 447:    */       case 24: 
/* 448:707 */         return symbol(23);
/* 449:    */       case 61: 
/* 450:    */         break;
/* 451:    */       case 25: 
/* 452:711 */         return symbol(24);
/* 453:    */       case 62: 
/* 454:    */         break;
/* 455:    */       case 26: 
/* 456:715 */         throw new SyntaxException("Invalid escape sequence '" + yytext() + "'!");
/* 457:    */       case 63: 
/* 458:    */         break;
/* 459:    */       case 27: 
/* 460:719 */         this.string.append('\t');
/* 461:    */       case 64: 
/* 462:    */         break;
/* 463:    */       case 28: 
/* 464:723 */         this.string.append('\r');
/* 465:    */       case 65: 
/* 466:    */         break;
/* 467:    */       case 29: 
/* 468:727 */         this.string.append('\f');
/* 469:    */       case 66: 
/* 470:    */         break;
/* 471:    */       case 30: 
/* 472:731 */         this.string.append('"');
/* 473:    */       case 67: 
/* 474:    */         break;
/* 475:    */       case 31: 
/* 476:735 */         this.string.append('\'');
/* 477:    */       case 68: 
/* 478:    */         break;
/* 479:    */       case 32: 
/* 480:739 */         this.string.append('\n');
/* 481:    */       case 69: 
/* 482:    */         break;
/* 483:    */       case 33: 
/* 484:743 */         this.string.append('\\');
/* 485:    */       case 70: 
/* 486:    */         break;
/* 487:    */       case 34: 
/* 488:747 */         this.string.append('\b');
/* 489:    */       case 71: 
/* 490:    */         break;
/* 491:    */       case 35: 
/* 492:751 */         return symbol(5, Boolean.valueOf(true));
/* 493:    */       case 72: 
/* 494:    */         break;
/* 495:    */       case 36: 
/* 496:755 */         return symbol(5, Boolean.valueOf(false));
/* 497:    */       case 73: 
/* 498:    */         break;
/* 499:    */       case 37: 
/* 500:759 */         return symbol(25);
/* 501:    */       case 74: 
/* 502:    */         break;
/* 503:    */       default: 
/* 504:763 */         if ((zzInput == -1) && (this.zzStartRead == this.zzCurrentPos))
/* 505:    */         {
/* 506:764 */           this.zzAtEOF = true;
/* 507:765 */           return new Symbol(0);
/* 508:    */         }
/* 509:768 */         zzScanError(1);
/* 510:    */       }
/* 511:    */     }
/* 512:    */   }
/* 513:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.expressionlanguage.parser.Scanner
 * JD-Core Version:    0.7.0.1
 */