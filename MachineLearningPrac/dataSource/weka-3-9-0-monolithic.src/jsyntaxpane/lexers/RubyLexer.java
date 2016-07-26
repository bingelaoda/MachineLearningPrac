/*   1:    */ package jsyntaxpane.lexers;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.io.InputStreamReader;
/*   6:    */ import java.io.Reader;
/*   7:    */ import jsyntaxpane.Token;
/*   8:    */ import jsyntaxpane.TokenType;
/*   9:    */ 
/*  10:    */ public final class RubyLexer
/*  11:    */   extends DefaultJFlexLexer
/*  12:    */ {
/*  13:    */   public static final int YYEOF = -1;
/*  14:    */   private static final int ZZ_BUFFERSIZE = 16384;
/*  15:    */   public static final int ML_STRING = 4;
/*  16:    */   public static final int STRING = 2;
/*  17:    */   public static final int YYINITIAL = 0;
/*  18: 49 */   private static final int[] ZZ_LEXSTATE = { 0, 0, 1, 1, 2, 2 };
/*  19:    */   private static final String ZZ_CMAP_PACKED = "";
/*  20: 70 */   private static final char[] ZZ_CMAP = zzUnpackCMap("");
/*  21: 75 */   private static final int[] ZZ_ACTION = zzUnpackAction();
/*  22:    */   private static final String ZZ_ACTION_PACKED_0 = "";
/*  23:    */   
/*  24:    */   private static int[] zzUnpackAction()
/*  25:    */   {
/*  26: 90 */     int[] result = new int['»'];
/*  27: 91 */     int offset = 0;
/*  28: 92 */     offset = zzUnpackAction("", offset, result);
/*  29: 93 */     return result;
/*  30:    */   }
/*  31:    */   
/*  32:    */   private static int zzUnpackAction(String packed, int offset, int[] result)
/*  33:    */   {
/*  34: 97 */     int i = 0;
/*  35: 98 */     int j = offset;
/*  36: 99 */     int l = packed.length();
/*  37:    */     int count;
/*  38:100 */     for (; i < l; count > 0)
/*  39:    */     {
/*  40:101 */       count = packed.charAt(i++);
/*  41:102 */       int value = packed.charAt(i++);
/*  42:103 */       result[(j++)] = value;count--;
/*  43:    */     }
/*  44:105 */     return j;
/*  45:    */   }
/*  46:    */   
/*  47:112 */   private static final int[] ZZ_ROWMAP = zzUnpackRowMap();
/*  48:    */   private static final String ZZ_ROWMAP_PACKED_0 = "";
/*  49:    */   
/*  50:    */   private static int[] zzUnpackRowMap()
/*  51:    */   {
/*  52:141 */     int[] result = new int['»'];
/*  53:142 */     int offset = 0;
/*  54:143 */     offset = zzUnpackRowMap("", offset, result);
/*  55:144 */     return result;
/*  56:    */   }
/*  57:    */   
/*  58:    */   private static int zzUnpackRowMap(String packed, int offset, int[] result)
/*  59:    */   {
/*  60:148 */     int i = 0;
/*  61:149 */     int j = offset;
/*  62:150 */     int l = packed.length();
/*  63:151 */     while (i < l)
/*  64:    */     {
/*  65:152 */       int high = packed.charAt(i++) << '\020';
/*  66:153 */       result[(j++)] = (high | packed.charAt(i++));
/*  67:    */     }
/*  68:155 */     return j;
/*  69:    */   }
/*  70:    */   
/*  71:161 */   private static final int[] ZZ_TRANS = zzUnpackTrans();
/*  72:    */   private static final String ZZ_TRANS_PACKED_0 = "";
/*  73:    */   private static final int ZZ_UNKNOWN_ERROR = 0;
/*  74:    */   private static final int ZZ_NO_MATCH = 1;
/*  75:    */   private static final int ZZ_PUSHBACK_2BIG = 2;
/*  76:    */   
/*  77:    */   private static int[] zzUnpackTrans()
/*  78:    */   {
/*  79:394 */     int[] result = new int[10560];
/*  80:395 */     int offset = 0;
/*  81:396 */     offset = zzUnpackTrans("", offset, result);
/*  82:397 */     return result;
/*  83:    */   }
/*  84:    */   
/*  85:    */   private static int zzUnpackTrans(String packed, int offset, int[] result)
/*  86:    */   {
/*  87:401 */     int i = 0;
/*  88:402 */     int j = offset;
/*  89:403 */     int l = packed.length();
/*  90:    */     int count;
/*  91:404 */     for (; i < l; count > 0)
/*  92:    */     {
/*  93:405 */       count = packed.charAt(i++);
/*  94:406 */       int value = packed.charAt(i++);
/*  95:407 */       value--;
/*  96:408 */       result[(j++)] = value;count--;
/*  97:    */     }
/*  98:410 */     return j;
/*  99:    */   }
/* 100:    */   
/* 101:420 */   private static final String[] ZZ_ERROR_MSG = { "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large" };
/* 102:429 */   private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
/* 103:    */   private static final String ZZ_ATTRIBUTE_PACKED_0 = "";
/* 104:    */   private Reader zzReader;
/* 105:    */   private int zzState;
/* 106:    */   
/* 107:    */   private static int[] zzUnpackAttribute()
/* 108:    */   {
/* 109:441 */     int[] result = new int['»'];
/* 110:442 */     int offset = 0;
/* 111:443 */     offset = zzUnpackAttribute("", offset, result);
/* 112:444 */     return result;
/* 113:    */   }
/* 114:    */   
/* 115:    */   private static int zzUnpackAttribute(String packed, int offset, int[] result)
/* 116:    */   {
/* 117:448 */     int i = 0;
/* 118:449 */     int j = offset;
/* 119:450 */     int l = packed.length();
/* 120:    */     int count;
/* 121:451 */     for (; i < l; count > 0)
/* 122:    */     {
/* 123:452 */       count = packed.charAt(i++);
/* 124:453 */       int value = packed.charAt(i++);
/* 125:454 */       result[(j++)] = value;count--;
/* 126:    */     }
/* 127:456 */     return j;
/* 128:    */   }
/* 129:    */   
/* 130:466 */   private int zzLexicalState = 0;
/* 131:470 */   private char[] zzBuffer = new char[16384];
/* 132:    */   private int zzMarkedPos;
/* 133:    */   private int zzCurrentPos;
/* 134:    */   private int zzStartRead;
/* 135:    */   private int zzEndRead;
/* 136:    */   private int yyline;
/* 137:    */   private int yychar;
/* 138:    */   private int yycolumn;
/* 139:500 */   private boolean zzAtBOL = true;
/* 140:    */   private boolean zzAtEOF;
/* 141:    */   private boolean zzEOFDone;
/* 142:    */   private static final byte PARAN = 1;
/* 143:    */   private static final byte BRACKET = 2;
/* 144:    */   private static final byte CURLY = 3;
/* 145:    */   private static final byte WORD = 4;
/* 146:    */   
/* 147:    */   public RubyLexer() {}
/* 148:    */   
/* 149:    */   public int yychar()
/* 150:    */   {
/* 151:519 */     return this.yychar;
/* 152:    */   }
/* 153:    */   
/* 154:    */   public RubyLexer(Reader in)
/* 155:    */   {
/* 156:536 */     this.zzReader = in;
/* 157:    */   }
/* 158:    */   
/* 159:    */   public RubyLexer(InputStream in)
/* 160:    */   {
/* 161:546 */     this(new InputStreamReader(in));
/* 162:    */   }
/* 163:    */   
/* 164:    */   private static char[] zzUnpackCMap(String packed)
/* 165:    */   {
/* 166:556 */     char[] map = new char[65536];
/* 167:557 */     int i = 0;
/* 168:558 */     int j = 0;
/* 169:    */     int count;
/* 170:559 */     for (; i < 178; count > 0)
/* 171:    */     {
/* 172:560 */       count = packed.charAt(i++);
/* 173:561 */       char value = packed.charAt(i++);
/* 174:562 */       map[(j++)] = value;count--;
/* 175:    */     }
/* 176:564 */     return map;
/* 177:    */   }
/* 178:    */   
/* 179:    */   private boolean zzRefill()
/* 180:    */     throws IOException
/* 181:    */   {
/* 182:578 */     if (this.zzStartRead > 0)
/* 183:    */     {
/* 184:579 */       System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
/* 185:    */       
/* 186:    */ 
/* 187:    */ 
/* 188:    */ 
/* 189:584 */       this.zzEndRead -= this.zzStartRead;
/* 190:585 */       this.zzCurrentPos -= this.zzStartRead;
/* 191:586 */       this.zzMarkedPos -= this.zzStartRead;
/* 192:587 */       this.zzStartRead = 0;
/* 193:    */     }
/* 194:591 */     if (this.zzCurrentPos >= this.zzBuffer.length)
/* 195:    */     {
/* 196:593 */       char[] newBuffer = new char[this.zzCurrentPos * 2];
/* 197:594 */       System.arraycopy(this.zzBuffer, 0, newBuffer, 0, this.zzBuffer.length);
/* 198:595 */       this.zzBuffer = newBuffer;
/* 199:    */     }
/* 200:599 */     int numRead = this.zzReader.read(this.zzBuffer, this.zzEndRead, this.zzBuffer.length - this.zzEndRead);
/* 201:602 */     if (numRead > 0)
/* 202:    */     {
/* 203:603 */       this.zzEndRead += numRead;
/* 204:604 */       return false;
/* 205:    */     }
/* 206:607 */     if (numRead == 0)
/* 207:    */     {
/* 208:608 */       int c = this.zzReader.read();
/* 209:609 */       if (c == -1) {
/* 210:610 */         return true;
/* 211:    */       }
/* 212:612 */       this.zzBuffer[(this.zzEndRead++)] = ((char)c);
/* 213:613 */       return false;
/* 214:    */     }
/* 215:618 */     return true;
/* 216:    */   }
/* 217:    */   
/* 218:    */   public final void yyclose()
/* 219:    */     throws IOException
/* 220:    */   {
/* 221:626 */     this.zzAtEOF = true;
/* 222:627 */     this.zzEndRead = this.zzStartRead;
/* 223:629 */     if (this.zzReader != null) {
/* 224:630 */       this.zzReader.close();
/* 225:    */     }
/* 226:    */   }
/* 227:    */   
/* 228:    */   public final void yyreset(Reader reader)
/* 229:    */   {
/* 230:645 */     this.zzReader = reader;
/* 231:646 */     this.zzAtBOL = true;
/* 232:647 */     this.zzAtEOF = false;
/* 233:648 */     this.zzEOFDone = false;
/* 234:649 */     this.zzEndRead = (this.zzStartRead = 0);
/* 235:650 */     this.zzCurrentPos = (this.zzMarkedPos = 0);
/* 236:651 */     this.yyline = (this.yychar = this.yycolumn = 0);
/* 237:652 */     this.zzLexicalState = 0;
/* 238:    */   }
/* 239:    */   
/* 240:    */   public final int yystate()
/* 241:    */   {
/* 242:660 */     return this.zzLexicalState;
/* 243:    */   }
/* 244:    */   
/* 245:    */   public final void yybegin(int newState)
/* 246:    */   {
/* 247:670 */     this.zzLexicalState = newState;
/* 248:    */   }
/* 249:    */   
/* 250:    */   public final String yytext()
/* 251:    */   {
/* 252:678 */     return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
/* 253:    */   }
/* 254:    */   
/* 255:    */   public final char yycharat(int pos)
/* 256:    */   {
/* 257:694 */     return this.zzBuffer[(this.zzStartRead + pos)];
/* 258:    */   }
/* 259:    */   
/* 260:    */   public final int yylength()
/* 261:    */   {
/* 262:702 */     return this.zzMarkedPos - this.zzStartRead;
/* 263:    */   }
/* 264:    */   
/* 265:    */   private void zzScanError(int errorCode)
/* 266:    */   {
/* 267:    */     String message;
/* 268:    */     try
/* 269:    */     {
/* 270:723 */       message = ZZ_ERROR_MSG[errorCode];
/* 271:    */     }
/* 272:    */     catch (ArrayIndexOutOfBoundsException e)
/* 273:    */     {
/* 274:726 */       message = ZZ_ERROR_MSG[0];
/* 275:    */     }
/* 276:729 */     throw new Error(message);
/* 277:    */   }
/* 278:    */   
/* 279:    */   public void yypushback(int number)
/* 280:    */   {
/* 281:742 */     if (number > yylength()) {
/* 282:743 */       zzScanError(2);
/* 283:    */     }
/* 284:745 */     this.zzMarkedPos -= number;
/* 285:    */   }
/* 286:    */   
/* 287:    */   public Token yylex()
/* 288:    */     throws IOException
/* 289:    */   {
/* 290:763 */     int zzEndReadL = this.zzEndRead;
/* 291:764 */     char[] zzBufferL = this.zzBuffer;
/* 292:765 */     char[] zzCMapL = ZZ_CMAP;
/* 293:    */     
/* 294:767 */     int[] zzTransL = ZZ_TRANS;
/* 295:768 */     int[] zzRowMapL = ZZ_ROWMAP;
/* 296:769 */     int[] zzAttrL = ZZ_ATTRIBUTE;
/* 297:    */     for (;;)
/* 298:    */     {
/* 299:772 */       int zzMarkedPosL = this.zzMarkedPos;
/* 300:    */       
/* 301:774 */       this.yychar += zzMarkedPosL - this.zzStartRead;
/* 302:    */       
/* 303:776 */       int zzAction = -1;
/* 304:    */       
/* 305:778 */       int zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
/* 306:    */       
/* 307:780 */       this.zzState = ZZ_LEXSTATE[this.zzLexicalState];
/* 308:    */       int zzInput;
/* 309:    */       for (;;)
/* 310:    */       {
/* 311:    */         int zzInput;
/* 312:786 */         if (zzCurrentPosL < zzEndReadL)
/* 313:    */         {
/* 314:787 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/* 315:    */         }
/* 316:    */         else
/* 317:    */         {
/* 318:788 */           if (this.zzAtEOF)
/* 319:    */           {
/* 320:789 */             int zzInput = -1;
/* 321:790 */             break;
/* 322:    */           }
/* 323:794 */           this.zzCurrentPos = zzCurrentPosL;
/* 324:795 */           this.zzMarkedPos = zzMarkedPosL;
/* 325:796 */           boolean eof = zzRefill();
/* 326:    */           
/* 327:798 */           zzCurrentPosL = this.zzCurrentPos;
/* 328:799 */           zzMarkedPosL = this.zzMarkedPos;
/* 329:800 */           zzBufferL = this.zzBuffer;
/* 330:801 */           zzEndReadL = this.zzEndRead;
/* 331:802 */           if (eof)
/* 332:    */           {
/* 333:803 */             int zzInput = -1;
/* 334:804 */             break;
/* 335:    */           }
/* 336:807 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/* 337:    */         }
/* 338:810 */         int zzNext = zzTransL[(zzRowMapL[this.zzState] + zzCMapL[zzInput])];
/* 339:811 */         if (zzNext == -1) {
/* 340:    */           break;
/* 341:    */         }
/* 342:812 */         this.zzState = zzNext;
/* 343:    */         
/* 344:814 */         int zzAttributes = zzAttrL[this.zzState];
/* 345:815 */         if ((zzAttributes & 0x1) == 1)
/* 346:    */         {
/* 347:816 */           zzAction = this.zzState;
/* 348:817 */           zzMarkedPosL = zzCurrentPosL;
/* 349:818 */           if ((zzAttributes & 0x8) == 8) {
/* 350:    */             break;
/* 351:    */           }
/* 352:    */         }
/* 353:    */       }
/* 354:825 */       this.zzMarkedPos = zzMarkedPosL;
/* 355:827 */       switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction])
/* 356:    */       {
/* 357:    */       case 7: 
/* 358:829 */         return token(TokenType.OPERATOR, -1);
/* 359:    */       case 25: 
/* 360:    */         break;
/* 361:    */       case 18: 
/* 362:833 */         return token(TokenType.KEYWORD);
/* 363:    */       case 26: 
/* 364:    */         break;
/* 365:    */       case 4: 
/* 366:837 */         return token(TokenType.NUMBER);
/* 367:    */       case 27: 
/* 368:    */         break;
/* 369:    */       case 5: 
/* 370:841 */         return token(TokenType.OPERATOR);
/* 371:    */       case 28: 
/* 372:    */         break;
/* 373:    */       case 8: 
/* 374:845 */         return token(TokenType.OPERATOR, 3);
/* 375:    */       case 29: 
/* 376:    */         break;
/* 377:    */       case 9: 
/* 378:849 */         return token(TokenType.OPERATOR, -3);
/* 379:    */       case 30: 
/* 380:    */         break;
/* 381:    */       case 23: 
/* 382:853 */         yybegin(0);
/* 383:    */         
/* 384:855 */         return token(TokenType.STRING, this.tokenStart, this.tokenLength + 3);
/* 385:    */       case 31: 
/* 386:    */         break;
/* 387:    */       case 21: 
/* 388:859 */         return token(TokenType.KEYWORD, -4);
/* 389:    */       case 32: 
/* 390:    */         break;
/* 391:    */       case 22: 
/* 392:863 */         yybegin(4);
/* 393:864 */         this.tokenStart = this.yychar;
/* 394:865 */         this.tokenLength = 3;
/* 395:    */       case 33: 
/* 396:    */         break;
/* 397:    */       case 13: 
/* 398:869 */         this.tokenLength += yylength();
/* 399:    */       case 34: 
/* 400:    */         break;
/* 401:    */       case 14: 
/* 402:873 */         yybegin(0);
/* 403:    */       case 35: 
/* 404:    */         break;
/* 405:    */       case 12: 
/* 406:877 */         yybegin(2);
/* 407:878 */         this.tokenStart = this.yychar;
/* 408:879 */         this.tokenLength = 1;
/* 409:    */       case 36: 
/* 410:    */         break;
/* 411:    */       case 15: 
/* 412:883 */         yybegin(0);
/* 413:    */         
/* 414:885 */         return token(TokenType.STRING, this.tokenStart, this.tokenLength + 1);
/* 415:    */       case 37: 
/* 416:    */         break;
/* 417:    */       case 24: 
/* 418:889 */         return token(TokenType.TYPE);
/* 419:    */       case 38: 
/* 420:    */         break;
/* 421:    */       case 11: 
/* 422:893 */         return token(TokenType.OPERATOR, -2);
/* 423:    */       case 39: 
/* 424:    */         break;
/* 425:    */       case 19: 
/* 426:897 */         return token(TokenType.KEYWORD, 4);
/* 427:    */       case 40: 
/* 428:    */         break;
/* 429:    */       case 6: 
/* 430:901 */         return token(TokenType.OPERATOR, 1);
/* 431:    */       case 41: 
/* 432:    */         break;
/* 433:    */       case 3: 
/* 434:905 */         return token(TokenType.IDENTIFIER);
/* 435:    */       case 42: 
/* 436:    */         break;
/* 437:    */       case 20: 
/* 438:909 */         this.tokenLength += 2;
/* 439:    */       case 43: 
/* 440:    */         break;
/* 441:    */       case 17: 
/* 442:913 */         return token(TokenType.TYPE2);
/* 443:    */       case 44: 
/* 444:    */         break;
/* 445:    */       case 16: 
/* 446:917 */         this.tokenLength += 1;
/* 447:    */       case 45: 
/* 448:    */         break;
/* 449:    */       case 10: 
/* 450:921 */         return token(TokenType.OPERATOR, 2);
/* 451:    */       case 46: 
/* 452:    */         break;
/* 453:    */       case 2: 
/* 454:925 */         return token(TokenType.COMMENT);
/* 455:    */       case 47: 
/* 456:    */         break;
/* 457:    */       case 1: 
/* 458:    */       case 48: 
/* 459:    */         break;
/* 460:    */       default: 
/* 461:933 */         if ((zzInput == -1) && (this.zzStartRead == this.zzCurrentPos))
/* 462:    */         {
/* 463:934 */           this.zzAtEOF = true;
/* 464:    */           
/* 465:936 */           return null;
/* 466:    */         }
/* 467:940 */         zzScanError(1);
/* 468:    */       }
/* 469:    */     }
/* 470:    */   }
/* 471:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.lexers.RubyLexer
 * JD-Core Version:    0.7.0.1
 */