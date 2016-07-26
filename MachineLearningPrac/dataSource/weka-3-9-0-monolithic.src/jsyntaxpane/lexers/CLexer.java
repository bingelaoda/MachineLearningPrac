/*   1:    */ package jsyntaxpane.lexers;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.io.InputStreamReader;
/*   6:    */ import java.io.Reader;
/*   7:    */ import jsyntaxpane.Token;
/*   8:    */ import jsyntaxpane.TokenType;
/*   9:    */ 
/*  10:    */ public final class CLexer
/*  11:    */   extends DefaultJFlexLexer
/*  12:    */ {
/*  13:    */   public static final int YYEOF = -1;
/*  14:    */   private static final int ZZ_BUFFERSIZE = 16384;
/*  15:    */   public static final int YYINITIAL = 0;
/*  16: 54 */   private static final int[] ZZ_LEXSTATE = { 0, 0 };
/*  17:    */   private static final String ZZ_CMAP_PACKED = "";
/*  18:156 */   private static final char[] ZZ_CMAP = zzUnpackCMap("");
/*  19:161 */   private static final int[] ZZ_ACTION = zzUnpackAction();
/*  20:    */   private static final String ZZ_ACTION_PACKED_0 = "";
/*  21:    */   
/*  22:    */   private static int[] zzUnpackAction()
/*  23:    */   {
/*  24:173 */     int[] result = new int['ì'];
/*  25:174 */     int offset = 0;
/*  26:175 */     offset = zzUnpackAction("", offset, result);
/*  27:176 */     return result;
/*  28:    */   }
/*  29:    */   
/*  30:    */   private static int zzUnpackAction(String packed, int offset, int[] result)
/*  31:    */   {
/*  32:180 */     int i = 0;
/*  33:181 */     int j = offset;
/*  34:182 */     int l = packed.length();
/*  35:    */     int count;
/*  36:183 */     for (; i < l; count > 0)
/*  37:    */     {
/*  38:184 */       count = packed.charAt(i++);
/*  39:185 */       int value = packed.charAt(i++);
/*  40:186 */       result[(j++)] = value;count--;
/*  41:    */     }
/*  42:188 */     return j;
/*  43:    */   }
/*  44:    */   
/*  45:195 */   private static final int[] ZZ_ROWMAP = zzUnpackRowMap();
/*  46:    */   private static final String ZZ_ROWMAP_PACKED_0 = "";
/*  47:    */   
/*  48:    */   private static int[] zzUnpackRowMap()
/*  49:    */   {
/*  50:230 */     int[] result = new int['ì'];
/*  51:231 */     int offset = 0;
/*  52:232 */     offset = zzUnpackRowMap("", offset, result);
/*  53:233 */     return result;
/*  54:    */   }
/*  55:    */   
/*  56:    */   private static int zzUnpackRowMap(String packed, int offset, int[] result)
/*  57:    */   {
/*  58:237 */     int i = 0;
/*  59:238 */     int j = offset;
/*  60:239 */     int l = packed.length();
/*  61:240 */     while (i < l)
/*  62:    */     {
/*  63:241 */       int high = packed.charAt(i++) << '\020';
/*  64:242 */       result[(j++)] = (high | packed.charAt(i++));
/*  65:    */     }
/*  66:244 */     return j;
/*  67:    */   }
/*  68:    */   
/*  69:250 */   private static final int[] ZZ_TRANS = zzUnpackTrans();
/*  70:    */   private static final String ZZ_TRANS_PACKED_0 = "";
/*  71:    */   private static final int ZZ_UNKNOWN_ERROR = 0;
/*  72:    */   private static final int ZZ_NO_MATCH = 1;
/*  73:    */   private static final int ZZ_PUSHBACK_2BIG = 2;
/*  74:    */   
/*  75:    */   private static int[] zzUnpackTrans()
/*  76:    */   {
/*  77:471 */     int[] result = new int[12644];
/*  78:472 */     int offset = 0;
/*  79:473 */     offset = zzUnpackTrans("", offset, result);
/*  80:474 */     return result;
/*  81:    */   }
/*  82:    */   
/*  83:    */   private static int zzUnpackTrans(String packed, int offset, int[] result)
/*  84:    */   {
/*  85:478 */     int i = 0;
/*  86:479 */     int j = offset;
/*  87:480 */     int l = packed.length();
/*  88:    */     int count;
/*  89:481 */     for (; i < l; count > 0)
/*  90:    */     {
/*  91:482 */       count = packed.charAt(i++);
/*  92:483 */       int value = packed.charAt(i++);
/*  93:484 */       value--;
/*  94:485 */       result[(j++)] = value;count--;
/*  95:    */     }
/*  96:487 */     return j;
/*  97:    */   }
/*  98:    */   
/*  99:497 */   private static final String[] ZZ_ERROR_MSG = { "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large" };
/* 100:506 */   private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
/* 101:    */   private static final String ZZ_ATTRIBUTE_PACKED_0 = "";
/* 102:    */   private Reader zzReader;
/* 103:    */   private int zzState;
/* 104:    */   
/* 105:    */   private static int[] zzUnpackAttribute()
/* 106:    */   {
/* 107:516 */     int[] result = new int['ì'];
/* 108:517 */     int offset = 0;
/* 109:518 */     offset = zzUnpackAttribute("", offset, result);
/* 110:519 */     return result;
/* 111:    */   }
/* 112:    */   
/* 113:    */   private static int zzUnpackAttribute(String packed, int offset, int[] result)
/* 114:    */   {
/* 115:523 */     int i = 0;
/* 116:524 */     int j = offset;
/* 117:525 */     int l = packed.length();
/* 118:    */     int count;
/* 119:526 */     for (; i < l; count > 0)
/* 120:    */     {
/* 121:527 */       count = packed.charAt(i++);
/* 122:528 */       int value = packed.charAt(i++);
/* 123:529 */       result[(j++)] = value;count--;
/* 124:    */     }
/* 125:531 */     return j;
/* 126:    */   }
/* 127:    */   
/* 128:541 */   private int zzLexicalState = 0;
/* 129:545 */   private char[] zzBuffer = new char[16384];
/* 130:    */   private int zzMarkedPos;
/* 131:    */   private int zzCurrentPos;
/* 132:    */   private int zzStartRead;
/* 133:    */   private int zzEndRead;
/* 134:    */   private int yyline;
/* 135:    */   private int yychar;
/* 136:    */   private int yycolumn;
/* 137:575 */   private boolean zzAtBOL = true;
/* 138:    */   private boolean zzAtEOF;
/* 139:    */   private boolean zzEOFDone;
/* 140:    */   private static final byte PARAN = 1;
/* 141:    */   private static final byte BRACKET = 2;
/* 142:    */   private static final byte CURLY = 3;
/* 143:    */   
/* 144:    */   public CLexer() {}
/* 145:    */   
/* 146:    */   public int yychar()
/* 147:    */   {
/* 148:595 */     return this.yychar;
/* 149:    */   }
/* 150:    */   
/* 151:    */   public CLexer(Reader in)
/* 152:    */   {
/* 153:606 */     this.zzReader = in;
/* 154:    */   }
/* 155:    */   
/* 156:    */   public CLexer(InputStream in)
/* 157:    */   {
/* 158:616 */     this(new InputStreamReader(in));
/* 159:    */   }
/* 160:    */   
/* 161:    */   private static char[] zzUnpackCMap(String packed)
/* 162:    */   {
/* 163:626 */     char[] map = new char[65536];
/* 164:627 */     int i = 0;
/* 165:628 */     int j = 0;
/* 166:    */     int count;
/* 167:629 */     for (; i < 1782; count > 0)
/* 168:    */     {
/* 169:630 */       count = packed.charAt(i++);
/* 170:631 */       char value = packed.charAt(i++);
/* 171:632 */       map[(j++)] = value;count--;
/* 172:    */     }
/* 173:634 */     return map;
/* 174:    */   }
/* 175:    */   
/* 176:    */   private boolean zzRefill()
/* 177:    */     throws IOException
/* 178:    */   {
/* 179:648 */     if (this.zzStartRead > 0)
/* 180:    */     {
/* 181:649 */       System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
/* 182:    */       
/* 183:    */ 
/* 184:    */ 
/* 185:    */ 
/* 186:654 */       this.zzEndRead -= this.zzStartRead;
/* 187:655 */       this.zzCurrentPos -= this.zzStartRead;
/* 188:656 */       this.zzMarkedPos -= this.zzStartRead;
/* 189:657 */       this.zzStartRead = 0;
/* 190:    */     }
/* 191:661 */     if (this.zzCurrentPos >= this.zzBuffer.length)
/* 192:    */     {
/* 193:663 */       char[] newBuffer = new char[this.zzCurrentPos * 2];
/* 194:664 */       System.arraycopy(this.zzBuffer, 0, newBuffer, 0, this.zzBuffer.length);
/* 195:665 */       this.zzBuffer = newBuffer;
/* 196:    */     }
/* 197:669 */     int numRead = this.zzReader.read(this.zzBuffer, this.zzEndRead, this.zzBuffer.length - this.zzEndRead);
/* 198:672 */     if (numRead > 0)
/* 199:    */     {
/* 200:673 */       this.zzEndRead += numRead;
/* 201:674 */       return false;
/* 202:    */     }
/* 203:677 */     if (numRead == 0)
/* 204:    */     {
/* 205:678 */       int c = this.zzReader.read();
/* 206:679 */       if (c == -1) {
/* 207:680 */         return true;
/* 208:    */       }
/* 209:682 */       this.zzBuffer[(this.zzEndRead++)] = ((char)c);
/* 210:683 */       return false;
/* 211:    */     }
/* 212:688 */     return true;
/* 213:    */   }
/* 214:    */   
/* 215:    */   public final void yyclose()
/* 216:    */     throws IOException
/* 217:    */   {
/* 218:696 */     this.zzAtEOF = true;
/* 219:697 */     this.zzEndRead = this.zzStartRead;
/* 220:699 */     if (this.zzReader != null) {
/* 221:700 */       this.zzReader.close();
/* 222:    */     }
/* 223:    */   }
/* 224:    */   
/* 225:    */   public final void yyreset(Reader reader)
/* 226:    */   {
/* 227:715 */     this.zzReader = reader;
/* 228:716 */     this.zzAtBOL = true;
/* 229:717 */     this.zzAtEOF = false;
/* 230:718 */     this.zzEOFDone = false;
/* 231:719 */     this.zzEndRead = (this.zzStartRead = 0);
/* 232:720 */     this.zzCurrentPos = (this.zzMarkedPos = 0);
/* 233:721 */     this.yyline = (this.yychar = this.yycolumn = 0);
/* 234:722 */     this.zzLexicalState = 0;
/* 235:    */   }
/* 236:    */   
/* 237:    */   public final int yystate()
/* 238:    */   {
/* 239:730 */     return this.zzLexicalState;
/* 240:    */   }
/* 241:    */   
/* 242:    */   public final void yybegin(int newState)
/* 243:    */   {
/* 244:740 */     this.zzLexicalState = newState;
/* 245:    */   }
/* 246:    */   
/* 247:    */   public final String yytext()
/* 248:    */   {
/* 249:748 */     return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
/* 250:    */   }
/* 251:    */   
/* 252:    */   public final char yycharat(int pos)
/* 253:    */   {
/* 254:764 */     return this.zzBuffer[(this.zzStartRead + pos)];
/* 255:    */   }
/* 256:    */   
/* 257:    */   public final int yylength()
/* 258:    */   {
/* 259:772 */     return this.zzMarkedPos - this.zzStartRead;
/* 260:    */   }
/* 261:    */   
/* 262:    */   private void zzScanError(int errorCode)
/* 263:    */   {
/* 264:    */     String message;
/* 265:    */     try
/* 266:    */     {
/* 267:793 */       message = ZZ_ERROR_MSG[errorCode];
/* 268:    */     }
/* 269:    */     catch (ArrayIndexOutOfBoundsException e)
/* 270:    */     {
/* 271:796 */       message = ZZ_ERROR_MSG[0];
/* 272:    */     }
/* 273:799 */     throw new Error(message);
/* 274:    */   }
/* 275:    */   
/* 276:    */   public void yypushback(int number)
/* 277:    */   {
/* 278:812 */     if (number > yylength()) {
/* 279:813 */       zzScanError(2);
/* 280:    */     }
/* 281:815 */     this.zzMarkedPos -= number;
/* 282:    */   }
/* 283:    */   
/* 284:    */   public Token yylex()
/* 285:    */     throws IOException
/* 286:    */   {
/* 287:833 */     int zzEndReadL = this.zzEndRead;
/* 288:834 */     char[] zzBufferL = this.zzBuffer;
/* 289:835 */     char[] zzCMapL = ZZ_CMAP;
/* 290:    */     
/* 291:837 */     int[] zzTransL = ZZ_TRANS;
/* 292:838 */     int[] zzRowMapL = ZZ_ROWMAP;
/* 293:839 */     int[] zzAttrL = ZZ_ATTRIBUTE;
/* 294:    */     for (;;)
/* 295:    */     {
/* 296:842 */       int zzMarkedPosL = this.zzMarkedPos;
/* 297:    */       
/* 298:844 */       this.yychar += zzMarkedPosL - this.zzStartRead;
/* 299:    */       
/* 300:846 */       int zzAction = -1;
/* 301:    */       
/* 302:848 */       int zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
/* 303:    */       
/* 304:850 */       this.zzState = ZZ_LEXSTATE[this.zzLexicalState];
/* 305:    */       int zzInput;
/* 306:    */       for (;;)
/* 307:    */       {
/* 308:    */         int zzInput;
/* 309:856 */         if (zzCurrentPosL < zzEndReadL)
/* 310:    */         {
/* 311:857 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/* 312:    */         }
/* 313:    */         else
/* 314:    */         {
/* 315:858 */           if (this.zzAtEOF)
/* 316:    */           {
/* 317:859 */             int zzInput = -1;
/* 318:860 */             break;
/* 319:    */           }
/* 320:864 */           this.zzCurrentPos = zzCurrentPosL;
/* 321:865 */           this.zzMarkedPos = zzMarkedPosL;
/* 322:866 */           boolean eof = zzRefill();
/* 323:    */           
/* 324:868 */           zzCurrentPosL = this.zzCurrentPos;
/* 325:869 */           zzMarkedPosL = this.zzMarkedPos;
/* 326:870 */           zzBufferL = this.zzBuffer;
/* 327:871 */           zzEndReadL = this.zzEndRead;
/* 328:872 */           if (eof)
/* 329:    */           {
/* 330:873 */             int zzInput = -1;
/* 331:874 */             break;
/* 332:    */           }
/* 333:877 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/* 334:    */         }
/* 335:880 */         int zzNext = zzTransL[(zzRowMapL[this.zzState] + zzCMapL[zzInput])];
/* 336:881 */         if (zzNext == -1) {
/* 337:    */           break;
/* 338:    */         }
/* 339:882 */         this.zzState = zzNext;
/* 340:    */         
/* 341:884 */         int zzAttributes = zzAttrL[this.zzState];
/* 342:885 */         if ((zzAttributes & 0x1) == 1)
/* 343:    */         {
/* 344:886 */           zzAction = this.zzState;
/* 345:887 */           zzMarkedPosL = zzCurrentPosL;
/* 346:888 */           if ((zzAttributes & 0x8) == 8) {
/* 347:    */             break;
/* 348:    */           }
/* 349:    */         }
/* 350:    */       }
/* 351:895 */       this.zzMarkedPos = zzMarkedPosL;
/* 352:897 */       switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction])
/* 353:    */       {
/* 354:    */       case 12: 
/* 355:899 */         return token(TokenType.KEYWORD);
/* 356:    */       case 16: 
/* 357:    */         break;
/* 358:    */       case 7: 
/* 359:903 */         return token(TokenType.OPERATOR, 3);
/* 360:    */       case 17: 
/* 361:    */         break;
/* 362:    */       case 9: 
/* 363:907 */         return token(TokenType.OPERATOR, 2);
/* 364:    */       case 18: 
/* 365:    */         break;
/* 366:    */       case 3: 
/* 367:911 */         return token(TokenType.OPERATOR);
/* 368:    */       case 19: 
/* 369:    */         break;
/* 370:    */       case 6: 
/* 371:915 */         return token(TokenType.OPERATOR, -1);
/* 372:    */       case 20: 
/* 373:    */         break;
/* 374:    */       case 14: 
/* 375:919 */         return token(TokenType.KEYWORD2);
/* 376:    */       case 21: 
/* 377:    */         break;
/* 378:    */       case 2: 
/* 379:923 */         return token(TokenType.IDENTIFIER);
/* 380:    */       case 22: 
/* 381:    */         break;
/* 382:    */       case 5: 
/* 383:927 */         return token(TokenType.OPERATOR, 1);
/* 384:    */       case 23: 
/* 385:    */         break;
/* 386:    */       case 13: 
/* 387:931 */         return token(TokenType.STRING);
/* 388:    */       case 24: 
/* 389:    */         break;
/* 390:    */       case 11: 
/* 391:935 */         return token(TokenType.COMMENT);
/* 392:    */       case 25: 
/* 393:    */         break;
/* 394:    */       case 10: 
/* 395:939 */         return token(TokenType.OPERATOR, -2);
/* 396:    */       case 26: 
/* 397:    */         break;
/* 398:    */       case 8: 
/* 399:943 */         return token(TokenType.OPERATOR, -3);
/* 400:    */       case 27: 
/* 401:    */         break;
/* 402:    */       case 15: 
/* 403:947 */         return token(TokenType.TYPE);
/* 404:    */       case 28: 
/* 405:    */         break;
/* 406:    */       case 4: 
/* 407:951 */         return token(TokenType.NUMBER);
/* 408:    */       case 29: 
/* 409:    */         break;
/* 410:    */       case 1: 
/* 411:    */       case 30: 
/* 412:    */         break;
/* 413:    */       default: 
/* 414:959 */         if ((zzInput == -1) && (this.zzStartRead == this.zzCurrentPos))
/* 415:    */         {
/* 416:960 */           this.zzAtEOF = true;
/* 417:    */           
/* 418:962 */           return null;
/* 419:    */         }
/* 420:966 */         zzScanError(1);
/* 421:    */       }
/* 422:    */     }
/* 423:    */   }
/* 424:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.lexers.CLexer
 * JD-Core Version:    0.7.0.1
 */