/*   1:    */ package jsyntaxpane.lexers;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.io.InputStreamReader;
/*   6:    */ import java.io.Reader;
/*   7:    */ import jsyntaxpane.Token;
/*   8:    */ import jsyntaxpane.TokenType;
/*   9:    */ 
/*  10:    */ public final class XmlLexer
/*  11:    */   extends DefaultJFlexLexer
/*  12:    */ {
/*  13:    */   public static final int YYEOF = -1;
/*  14:    */   private static final int ZZ_BUFFERSIZE = 16384;
/*  15:    */   public static final int INSTR = 8;
/*  16:    */   public static final int YYINITIAL = 0;
/*  17:    */   public static final int COMMENT = 2;
/*  18:    */   public static final int CDATA = 4;
/*  19:    */   public static final int TAG = 6;
/*  20: 51 */   private static final int[] ZZ_LEXSTATE = { 0, 0, 1, 1, 2, 2, 3, 3, 4, 4 };
/*  21:    */   private static final String ZZ_CMAP_PACKED = "";
/*  22: 74 */   private static final char[] ZZ_CMAP = zzUnpackCMap("");
/*  23: 79 */   private static final int[] ZZ_ACTION = zzUnpackAction();
/*  24:    */   private static final String ZZ_ACTION_PACKED_0 = "";
/*  25:    */   
/*  26:    */   private static int[] zzUnpackAction()
/*  27:    */   {
/*  28: 88 */     int[] result = new int[57];
/*  29: 89 */     int offset = 0;
/*  30: 90 */     offset = zzUnpackAction("", offset, result);
/*  31: 91 */     return result;
/*  32:    */   }
/*  33:    */   
/*  34:    */   private static int zzUnpackAction(String packed, int offset, int[] result)
/*  35:    */   {
/*  36: 95 */     int i = 0;
/*  37: 96 */     int j = offset;
/*  38: 97 */     int l = packed.length();
/*  39:    */     int count;
/*  40: 98 */     for (; i < l; count > 0)
/*  41:    */     {
/*  42: 99 */       count = packed.charAt(i++);
/*  43:100 */       int value = packed.charAt(i++);
/*  44:101 */       result[(j++)] = value;count--;
/*  45:    */     }
/*  46:103 */     return j;
/*  47:    */   }
/*  48:    */   
/*  49:110 */   private static final int[] ZZ_ROWMAP = zzUnpackRowMap();
/*  50:    */   private static final String ZZ_ROWMAP_PACKED_0 = "";
/*  51:    */   
/*  52:    */   private static int[] zzUnpackRowMap()
/*  53:    */   {
/*  54:123 */     int[] result = new int[57];
/*  55:124 */     int offset = 0;
/*  56:125 */     offset = zzUnpackRowMap("", offset, result);
/*  57:126 */     return result;
/*  58:    */   }
/*  59:    */   
/*  60:    */   private static int zzUnpackRowMap(String packed, int offset, int[] result)
/*  61:    */   {
/*  62:130 */     int i = 0;
/*  63:131 */     int j = offset;
/*  64:132 */     int l = packed.length();
/*  65:133 */     while (i < l)
/*  66:    */     {
/*  67:134 */       int high = packed.charAt(i++) << '\020';
/*  68:135 */       result[(j++)] = (high | packed.charAt(i++));
/*  69:    */     }
/*  70:137 */     return j;
/*  71:    */   }
/*  72:    */   
/*  73:143 */   private static final int[] ZZ_TRANS = zzUnpackTrans();
/*  74:    */   private static final String ZZ_TRANS_PACKED_0 = "";
/*  75:    */   private static final int ZZ_UNKNOWN_ERROR = 0;
/*  76:    */   private static final int ZZ_NO_MATCH = 1;
/*  77:    */   private static final int ZZ_PUSHBACK_2BIG = 2;
/*  78:    */   
/*  79:    */   private static int[] zzUnpackTrans()
/*  80:    */   {
/*  81:169 */     int[] result = new int[1040];
/*  82:170 */     int offset = 0;
/*  83:171 */     offset = zzUnpackTrans("", offset, result);
/*  84:172 */     return result;
/*  85:    */   }
/*  86:    */   
/*  87:    */   private static int zzUnpackTrans(String packed, int offset, int[] result)
/*  88:    */   {
/*  89:176 */     int i = 0;
/*  90:177 */     int j = offset;
/*  91:178 */     int l = packed.length();
/*  92:    */     int count;
/*  93:179 */     for (; i < l; count > 0)
/*  94:    */     {
/*  95:180 */       count = packed.charAt(i++);
/*  96:181 */       int value = packed.charAt(i++);
/*  97:182 */       value--;
/*  98:183 */       result[(j++)] = value;count--;
/*  99:    */     }
/* 100:185 */     return j;
/* 101:    */   }
/* 102:    */   
/* 103:195 */   private static final String[] ZZ_ERROR_MSG = { "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large" };
/* 104:204 */   private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
/* 105:    */   private static final String ZZ_ATTRIBUTE_PACKED_0 = "";
/* 106:    */   private Reader zzReader;
/* 107:    */   private int zzState;
/* 108:    */   
/* 109:    */   private static int[] zzUnpackAttribute()
/* 110:    */   {
/* 111:213 */     int[] result = new int[57];
/* 112:214 */     int offset = 0;
/* 113:215 */     offset = zzUnpackAttribute("", offset, result);
/* 114:216 */     return result;
/* 115:    */   }
/* 116:    */   
/* 117:    */   private static int zzUnpackAttribute(String packed, int offset, int[] result)
/* 118:    */   {
/* 119:220 */     int i = 0;
/* 120:221 */     int j = offset;
/* 121:222 */     int l = packed.length();
/* 122:    */     int count;
/* 123:223 */     for (; i < l; count > 0)
/* 124:    */     {
/* 125:224 */       count = packed.charAt(i++);
/* 126:225 */       int value = packed.charAt(i++);
/* 127:226 */       result[(j++)] = value;count--;
/* 128:    */     }
/* 129:228 */     return j;
/* 130:    */   }
/* 131:    */   
/* 132:238 */   private int zzLexicalState = 0;
/* 133:242 */   private char[] zzBuffer = new char[16384];
/* 134:    */   private int zzMarkedPos;
/* 135:    */   private int zzCurrentPos;
/* 136:    */   private int zzStartRead;
/* 137:    */   private int zzEndRead;
/* 138:    */   private int yyline;
/* 139:    */   private int yychar;
/* 140:    */   private int yycolumn;
/* 141:272 */   private boolean zzAtBOL = true;
/* 142:    */   private boolean zzAtEOF;
/* 143:    */   private boolean zzEOFDone;
/* 144:    */   private static final byte TAG_OPEN = 1;
/* 145:    */   private static final byte TAG_CLOSE = -1;
/* 146:    */   private static final byte INSTR_OPEN = 2;
/* 147:    */   private static final byte INSTR_CLOSE = -2;
/* 148:    */   private static final byte CDATA_OPEN = 3;
/* 149:    */   private static final byte CDATA_CLOSE = -3;
/* 150:    */   private static final byte COMMENT_OPEN = 4;
/* 151:    */   private static final byte COMMENT_CLOSE = -4;
/* 152:    */   
/* 153:    */   public XmlLexer() {}
/* 154:    */   
/* 155:    */   public int yychar()
/* 156:    */   {
/* 157:291 */     return this.yychar;
/* 158:    */   }
/* 159:    */   
/* 160:    */   public XmlLexer(Reader in)
/* 161:    */   {
/* 162:314 */     this.zzReader = in;
/* 163:    */   }
/* 164:    */   
/* 165:    */   public XmlLexer(InputStream in)
/* 166:    */   {
/* 167:324 */     this(new InputStreamReader(in));
/* 168:    */   }
/* 169:    */   
/* 170:    */   private static char[] zzUnpackCMap(String packed)
/* 171:    */   {
/* 172:334 */     char[] map = new char[65536];
/* 173:335 */     int i = 0;
/* 174:336 */     int j = 0;
/* 175:    */     int count;
/* 176:337 */     for (; i < 218; count > 0)
/* 177:    */     {
/* 178:338 */       count = packed.charAt(i++);
/* 179:339 */       char value = packed.charAt(i++);
/* 180:340 */       map[(j++)] = value;count--;
/* 181:    */     }
/* 182:342 */     return map;
/* 183:    */   }
/* 184:    */   
/* 185:    */   private boolean zzRefill()
/* 186:    */     throws IOException
/* 187:    */   {
/* 188:356 */     if (this.zzStartRead > 0)
/* 189:    */     {
/* 190:357 */       System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
/* 191:    */       
/* 192:    */ 
/* 193:    */ 
/* 194:    */ 
/* 195:362 */       this.zzEndRead -= this.zzStartRead;
/* 196:363 */       this.zzCurrentPos -= this.zzStartRead;
/* 197:364 */       this.zzMarkedPos -= this.zzStartRead;
/* 198:365 */       this.zzStartRead = 0;
/* 199:    */     }
/* 200:369 */     if (this.zzCurrentPos >= this.zzBuffer.length)
/* 201:    */     {
/* 202:371 */       char[] newBuffer = new char[this.zzCurrentPos * 2];
/* 203:372 */       System.arraycopy(this.zzBuffer, 0, newBuffer, 0, this.zzBuffer.length);
/* 204:373 */       this.zzBuffer = newBuffer;
/* 205:    */     }
/* 206:377 */     int numRead = this.zzReader.read(this.zzBuffer, this.zzEndRead, this.zzBuffer.length - this.zzEndRead);
/* 207:380 */     if (numRead > 0)
/* 208:    */     {
/* 209:381 */       this.zzEndRead += numRead;
/* 210:382 */       return false;
/* 211:    */     }
/* 212:385 */     if (numRead == 0)
/* 213:    */     {
/* 214:386 */       int c = this.zzReader.read();
/* 215:387 */       if (c == -1) {
/* 216:388 */         return true;
/* 217:    */       }
/* 218:390 */       this.zzBuffer[(this.zzEndRead++)] = ((char)c);
/* 219:391 */       return false;
/* 220:    */     }
/* 221:396 */     return true;
/* 222:    */   }
/* 223:    */   
/* 224:    */   public final void yyclose()
/* 225:    */     throws IOException
/* 226:    */   {
/* 227:404 */     this.zzAtEOF = true;
/* 228:405 */     this.zzEndRead = this.zzStartRead;
/* 229:407 */     if (this.zzReader != null) {
/* 230:408 */       this.zzReader.close();
/* 231:    */     }
/* 232:    */   }
/* 233:    */   
/* 234:    */   public final void yyreset(Reader reader)
/* 235:    */   {
/* 236:423 */     this.zzReader = reader;
/* 237:424 */     this.zzAtBOL = true;
/* 238:425 */     this.zzAtEOF = false;
/* 239:426 */     this.zzEOFDone = false;
/* 240:427 */     this.zzEndRead = (this.zzStartRead = 0);
/* 241:428 */     this.zzCurrentPos = (this.zzMarkedPos = 0);
/* 242:429 */     this.yyline = (this.yychar = this.yycolumn = 0);
/* 243:430 */     this.zzLexicalState = 0;
/* 244:    */   }
/* 245:    */   
/* 246:    */   public final int yystate()
/* 247:    */   {
/* 248:438 */     return this.zzLexicalState;
/* 249:    */   }
/* 250:    */   
/* 251:    */   public final void yybegin(int newState)
/* 252:    */   {
/* 253:448 */     this.zzLexicalState = newState;
/* 254:    */   }
/* 255:    */   
/* 256:    */   public final String yytext()
/* 257:    */   {
/* 258:456 */     return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
/* 259:    */   }
/* 260:    */   
/* 261:    */   public final char yycharat(int pos)
/* 262:    */   {
/* 263:472 */     return this.zzBuffer[(this.zzStartRead + pos)];
/* 264:    */   }
/* 265:    */   
/* 266:    */   public final int yylength()
/* 267:    */   {
/* 268:480 */     return this.zzMarkedPos - this.zzStartRead;
/* 269:    */   }
/* 270:    */   
/* 271:    */   private void zzScanError(int errorCode)
/* 272:    */   {
/* 273:    */     String message;
/* 274:    */     try
/* 275:    */     {
/* 276:501 */       message = ZZ_ERROR_MSG[errorCode];
/* 277:    */     }
/* 278:    */     catch (ArrayIndexOutOfBoundsException e)
/* 279:    */     {
/* 280:504 */       message = ZZ_ERROR_MSG[0];
/* 281:    */     }
/* 282:507 */     throw new Error(message);
/* 283:    */   }
/* 284:    */   
/* 285:    */   public void yypushback(int number)
/* 286:    */   {
/* 287:520 */     if (number > yylength()) {
/* 288:521 */       zzScanError(2);
/* 289:    */     }
/* 290:523 */     this.zzMarkedPos -= number;
/* 291:    */   }
/* 292:    */   
/* 293:    */   public Token yylex()
/* 294:    */     throws IOException
/* 295:    */   {
/* 296:541 */     int zzEndReadL = this.zzEndRead;
/* 297:542 */     char[] zzBufferL = this.zzBuffer;
/* 298:543 */     char[] zzCMapL = ZZ_CMAP;
/* 299:    */     
/* 300:545 */     int[] zzTransL = ZZ_TRANS;
/* 301:546 */     int[] zzRowMapL = ZZ_ROWMAP;
/* 302:547 */     int[] zzAttrL = ZZ_ATTRIBUTE;
/* 303:    */     for (;;)
/* 304:    */     {
/* 305:550 */       int zzMarkedPosL = this.zzMarkedPos;
/* 306:    */       
/* 307:552 */       this.yychar += zzMarkedPosL - this.zzStartRead;
/* 308:    */       
/* 309:554 */       int zzAction = -1;
/* 310:    */       
/* 311:556 */       int zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
/* 312:    */       
/* 313:558 */       this.zzState = ZZ_LEXSTATE[this.zzLexicalState];
/* 314:    */       int zzInput;
/* 315:    */       for (;;)
/* 316:    */       {
/* 317:    */         int zzInput;
/* 318:564 */         if (zzCurrentPosL < zzEndReadL)
/* 319:    */         {
/* 320:565 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/* 321:    */         }
/* 322:    */         else
/* 323:    */         {
/* 324:566 */           if (this.zzAtEOF)
/* 325:    */           {
/* 326:567 */             int zzInput = -1;
/* 327:568 */             break;
/* 328:    */           }
/* 329:572 */           this.zzCurrentPos = zzCurrentPosL;
/* 330:573 */           this.zzMarkedPos = zzMarkedPosL;
/* 331:574 */           boolean eof = zzRefill();
/* 332:    */           
/* 333:576 */           zzCurrentPosL = this.zzCurrentPos;
/* 334:577 */           zzMarkedPosL = this.zzMarkedPos;
/* 335:578 */           zzBufferL = this.zzBuffer;
/* 336:579 */           zzEndReadL = this.zzEndRead;
/* 337:580 */           if (eof)
/* 338:    */           {
/* 339:581 */             int zzInput = -1;
/* 340:582 */             break;
/* 341:    */           }
/* 342:585 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/* 343:    */         }
/* 344:588 */         int zzNext = zzTransL[(zzRowMapL[this.zzState] + zzCMapL[zzInput])];
/* 345:589 */         if (zzNext == -1) {
/* 346:    */           break;
/* 347:    */         }
/* 348:590 */         this.zzState = zzNext;
/* 349:    */         
/* 350:592 */         int zzAttributes = zzAttrL[this.zzState];
/* 351:593 */         if ((zzAttributes & 0x1) == 1)
/* 352:    */         {
/* 353:594 */           zzAction = this.zzState;
/* 354:595 */           zzMarkedPosL = zzCurrentPosL;
/* 355:596 */           if ((zzAttributes & 0x8) == 8) {
/* 356:    */             break;
/* 357:    */           }
/* 358:    */         }
/* 359:    */       }
/* 360:603 */       this.zzMarkedPos = zzMarkedPosL;
/* 361:605 */       switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction])
/* 362:    */       {
/* 363:    */       case 12: 
/* 364:607 */         yybegin(2);
/* 365:608 */         return token(TokenType.COMMENT2, 4);
/* 366:    */       case 16: 
/* 367:    */         break;
/* 368:    */       case 10: 
/* 369:612 */         yybegin(0);
/* 370:613 */         return token(TokenType.COMMENT2, -4);
/* 371:    */       case 17: 
/* 372:    */         break;
/* 373:    */       case 5: 
/* 374:617 */         yybegin(0);
/* 375:618 */         return token(TokenType.TYPE, -1);
/* 376:    */       case 18: 
/* 377:    */         break;
/* 378:    */       case 15: 
/* 379:622 */         yybegin(4);
/* 380:623 */         return token(TokenType.COMMENT2, 3);
/* 381:    */       case 19: 
/* 382:    */         break;
/* 383:    */       case 11: 
/* 384:627 */         yybegin(0);
/* 385:628 */         return token(TokenType.COMMENT2, -3);
/* 386:    */       case 20: 
/* 387:    */         break;
/* 388:    */       case 2: 
/* 389:632 */         yybegin(0);
/* 390:633 */         return token(TokenType.TYPE);
/* 391:    */       case 21: 
/* 392:    */         break;
/* 393:    */       case 9: 
/* 394:637 */         return token(TokenType.KEYWORD2);
/* 395:    */       case 22: 
/* 396:    */         break;
/* 397:    */       case 13: 
/* 398:641 */         return token(TokenType.TYPE, -1);
/* 399:    */       case 23: 
/* 400:    */         break;
/* 401:    */       case 4: 
/* 402:645 */         return token(TokenType.IDENTIFIER);
/* 403:    */       case 24: 
/* 404:    */         break;
/* 405:    */       case 3: 
/* 406:649 */         yybegin(6);
/* 407:650 */         return token(TokenType.TYPE, 1);
/* 408:    */       case 25: 
/* 409:    */         break;
/* 410:    */       case 6: 
/* 411:654 */         return token(TokenType.STRING);
/* 412:    */       case 26: 
/* 413:    */         break;
/* 414:    */       case 14: 
/* 415:658 */         yypushback(3);
/* 416:659 */         return token(TokenType.COMMENT);
/* 417:    */       case 27: 
/* 418:    */         break;
/* 419:    */       case 7: 
/* 420:663 */         yybegin(0);
/* 421:664 */         return token(TokenType.TYPE2, -2);
/* 422:    */       case 28: 
/* 423:    */         break;
/* 424:    */       case 1: 
/* 425:    */       case 29: 
/* 426:    */         break;
/* 427:    */       case 8: 
/* 428:672 */         yybegin(8);
/* 429:673 */         return token(TokenType.TYPE2, 2);
/* 430:    */       case 30: 
/* 431:    */         break;
/* 432:    */       default: 
/* 433:677 */         if ((zzInput == -1) && (this.zzStartRead == this.zzCurrentPos)) {
/* 434:678 */           this.zzAtEOF = true;
/* 435:    */         }
/* 436:679 */         switch (this.zzLexicalState)
/* 437:    */         {
/* 438:    */         case 8: 
/* 439:681 */           return null;
/* 440:    */         case 58: 
/* 441:    */           break;
/* 442:    */         case 0: 
/* 443:685 */           return null;
/* 444:    */         case 59: 
/* 445:    */           break;
/* 446:    */         case 2: 
/* 447:689 */           return null;
/* 448:    */         case 60: 
/* 449:    */           break;
/* 450:    */         case 4: 
/* 451:693 */           return null;
/* 452:    */         case 61: 
/* 453:    */           break;
/* 454:    */         case 6: 
/* 455:697 */           return null;
/* 456:    */         case 62: 
/* 457:    */           break;
/* 458:    */         default: 
/* 459:701 */           return null;
/* 460:    */           
/* 461:    */ 
/* 462:    */ 
/* 463:705 */           zzScanError(1);
/* 464:    */         }
/* 465:    */         break;
/* 466:    */       }
/* 467:    */     }
/* 468:    */   }
/* 469:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.lexers.XmlLexer
 * JD-Core Version:    0.7.0.1
 */