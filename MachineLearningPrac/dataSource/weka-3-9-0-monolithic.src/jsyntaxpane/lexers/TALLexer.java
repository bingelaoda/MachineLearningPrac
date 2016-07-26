/*   1:    */ package jsyntaxpane.lexers;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.io.InputStreamReader;
/*   6:    */ import java.io.Reader;
/*   7:    */ import jsyntaxpane.Token;
/*   8:    */ import jsyntaxpane.TokenType;
/*   9:    */ 
/*  10:    */ public final class TALLexer
/*  11:    */   extends DefaultJFlexLexer
/*  12:    */ {
/*  13:    */   public static final int YYEOF = -1;
/*  14:    */   private static final int ZZ_BUFFERSIZE = 16384;
/*  15:    */   public static final int YYINITIAL = 0;
/*  16: 46 */   private static final int[] ZZ_LEXSTATE = { 0, 0 };
/*  17:    */   private static final String ZZ_CMAP_PACKED = "";
/*  18: 68 */   private static final char[] ZZ_CMAP = zzUnpackCMap("");
/*  19: 73 */   private static final int[] ZZ_ACTION = zzUnpackAction();
/*  20:    */   private static final String ZZ_ACTION_PACKED_0 = "";
/*  21:    */   
/*  22:    */   private static int[] zzUnpackAction()
/*  23:    */   {
/*  24: 91 */     int[] result = new int['§'];
/*  25: 92 */     int offset = 0;
/*  26: 93 */     offset = zzUnpackAction("", offset, result);
/*  27: 94 */     return result;
/*  28:    */   }
/*  29:    */   
/*  30:    */   private static int zzUnpackAction(String packed, int offset, int[] result)
/*  31:    */   {
/*  32: 98 */     int i = 0;
/*  33: 99 */     int j = offset;
/*  34:100 */     int l = packed.length();
/*  35:    */     int count;
/*  36:101 */     for (; i < l; count > 0)
/*  37:    */     {
/*  38:102 */       count = packed.charAt(i++);
/*  39:103 */       int value = packed.charAt(i++);
/*  40:104 */       result[(j++)] = value;count--;
/*  41:    */     }
/*  42:106 */     return j;
/*  43:    */   }
/*  44:    */   
/*  45:113 */   private static final int[] ZZ_ROWMAP = zzUnpackRowMap();
/*  46:    */   private static final String ZZ_ROWMAP_PACKED_0 = "";
/*  47:    */   
/*  48:    */   private static int[] zzUnpackRowMap()
/*  49:    */   {
/*  50:139 */     int[] result = new int['§'];
/*  51:140 */     int offset = 0;
/*  52:141 */     offset = zzUnpackRowMap("", offset, result);
/*  53:142 */     return result;
/*  54:    */   }
/*  55:    */   
/*  56:    */   private static int zzUnpackRowMap(String packed, int offset, int[] result)
/*  57:    */   {
/*  58:146 */     int i = 0;
/*  59:147 */     int j = offset;
/*  60:148 */     int l = packed.length();
/*  61:149 */     while (i < l)
/*  62:    */     {
/*  63:150 */       int high = packed.charAt(i++) << '\020';
/*  64:151 */       result[(j++)] = (high | packed.charAt(i++));
/*  65:    */     }
/*  66:153 */     return j;
/*  67:    */   }
/*  68:    */   
/*  69:159 */   private static final int[] ZZ_TRANS = zzUnpackTrans();
/*  70:    */   private static final String ZZ_TRANS_PACKED_0 = "";
/*  71:    */   private static final int ZZ_UNKNOWN_ERROR = 0;
/*  72:    */   private static final int ZZ_NO_MATCH = 1;
/*  73:    */   private static final int ZZ_PUSHBACK_2BIG = 2;
/*  74:    */   
/*  75:    */   private static int[] zzUnpackTrans()
/*  76:    */   {
/*  77:344 */     int[] result = new int[7222];
/*  78:345 */     int offset = 0;
/*  79:346 */     offset = zzUnpackTrans("", offset, result);
/*  80:347 */     return result;
/*  81:    */   }
/*  82:    */   
/*  83:    */   private static int zzUnpackTrans(String packed, int offset, int[] result)
/*  84:    */   {
/*  85:351 */     int i = 0;
/*  86:352 */     int j = offset;
/*  87:353 */     int l = packed.length();
/*  88:    */     int count;
/*  89:354 */     for (; i < l; count > 0)
/*  90:    */     {
/*  91:355 */       count = packed.charAt(i++);
/*  92:356 */       int value = packed.charAt(i++);
/*  93:357 */       value--;
/*  94:358 */       result[(j++)] = value;count--;
/*  95:    */     }
/*  96:360 */     return j;
/*  97:    */   }
/*  98:    */   
/*  99:370 */   private static final String[] ZZ_ERROR_MSG = { "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large" };
/* 100:379 */   private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
/* 101:    */   private static final String ZZ_ATTRIBUTE_PACKED_0 = "";
/* 102:    */   private Reader zzReader;
/* 103:    */   private int zzState;
/* 104:    */   
/* 105:    */   private static int[] zzUnpackAttribute()
/* 106:    */   {
/* 107:393 */     int[] result = new int['§'];
/* 108:394 */     int offset = 0;
/* 109:395 */     offset = zzUnpackAttribute("", offset, result);
/* 110:396 */     return result;
/* 111:    */   }
/* 112:    */   
/* 113:    */   private static int zzUnpackAttribute(String packed, int offset, int[] result)
/* 114:    */   {
/* 115:400 */     int i = 0;
/* 116:401 */     int j = offset;
/* 117:402 */     int l = packed.length();
/* 118:    */     int count;
/* 119:403 */     for (; i < l; count > 0)
/* 120:    */     {
/* 121:404 */       count = packed.charAt(i++);
/* 122:405 */       int value = packed.charAt(i++);
/* 123:406 */       result[(j++)] = value;count--;
/* 124:    */     }
/* 125:408 */     return j;
/* 126:    */   }
/* 127:    */   
/* 128:418 */   private int zzLexicalState = 0;
/* 129:422 */   private char[] zzBuffer = new char[16384];
/* 130:    */   private int zzMarkedPos;
/* 131:    */   private int zzCurrentPos;
/* 132:    */   private int zzStartRead;
/* 133:    */   private int zzEndRead;
/* 134:    */   private int yyline;
/* 135:    */   private int yychar;
/* 136:    */   private int yycolumn;
/* 137:452 */   private boolean zzAtBOL = true;
/* 138:    */   private boolean zzAtEOF;
/* 139:    */   private boolean zzEOFDone;
/* 140:    */   
/* 141:    */   public TALLexer() {}
/* 142:    */   
/* 143:    */   public int yychar()
/* 144:    */   {
/* 145:471 */     return this.yychar;
/* 146:    */   }
/* 147:    */   
/* 148:    */   public TALLexer(Reader in)
/* 149:    */   {
/* 150:482 */     this.zzReader = in;
/* 151:    */   }
/* 152:    */   
/* 153:    */   public TALLexer(InputStream in)
/* 154:    */   {
/* 155:492 */     this(new InputStreamReader(in));
/* 156:    */   }
/* 157:    */   
/* 158:    */   private static char[] zzUnpackCMap(String packed)
/* 159:    */   {
/* 160:502 */     char[] map = new char[65536];
/* 161:503 */     int i = 0;
/* 162:504 */     int j = 0;
/* 163:    */     int count;
/* 164:505 */     for (; i < 190; count > 0)
/* 165:    */     {
/* 166:506 */       count = packed.charAt(i++);
/* 167:507 */       char value = packed.charAt(i++);
/* 168:508 */       map[(j++)] = value;count--;
/* 169:    */     }
/* 170:510 */     return map;
/* 171:    */   }
/* 172:    */   
/* 173:    */   private boolean zzRefill()
/* 174:    */     throws IOException
/* 175:    */   {
/* 176:524 */     if (this.zzStartRead > 0)
/* 177:    */     {
/* 178:525 */       System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
/* 179:    */       
/* 180:    */ 
/* 181:    */ 
/* 182:    */ 
/* 183:530 */       this.zzEndRead -= this.zzStartRead;
/* 184:531 */       this.zzCurrentPos -= this.zzStartRead;
/* 185:532 */       this.zzMarkedPos -= this.zzStartRead;
/* 186:533 */       this.zzStartRead = 0;
/* 187:    */     }
/* 188:537 */     if (this.zzCurrentPos >= this.zzBuffer.length)
/* 189:    */     {
/* 190:539 */       char[] newBuffer = new char[this.zzCurrentPos * 2];
/* 191:540 */       System.arraycopy(this.zzBuffer, 0, newBuffer, 0, this.zzBuffer.length);
/* 192:541 */       this.zzBuffer = newBuffer;
/* 193:    */     }
/* 194:545 */     int numRead = this.zzReader.read(this.zzBuffer, this.zzEndRead, this.zzBuffer.length - this.zzEndRead);
/* 195:548 */     if (numRead > 0)
/* 196:    */     {
/* 197:549 */       this.zzEndRead += numRead;
/* 198:550 */       return false;
/* 199:    */     }
/* 200:553 */     if (numRead == 0)
/* 201:    */     {
/* 202:554 */       int c = this.zzReader.read();
/* 203:555 */       if (c == -1) {
/* 204:556 */         return true;
/* 205:    */       }
/* 206:558 */       this.zzBuffer[(this.zzEndRead++)] = ((char)c);
/* 207:559 */       return false;
/* 208:    */     }
/* 209:564 */     return true;
/* 210:    */   }
/* 211:    */   
/* 212:    */   public final void yyclose()
/* 213:    */     throws IOException
/* 214:    */   {
/* 215:572 */     this.zzAtEOF = true;
/* 216:573 */     this.zzEndRead = this.zzStartRead;
/* 217:575 */     if (this.zzReader != null) {
/* 218:576 */       this.zzReader.close();
/* 219:    */     }
/* 220:    */   }
/* 221:    */   
/* 222:    */   public final void yyreset(Reader reader)
/* 223:    */   {
/* 224:591 */     this.zzReader = reader;
/* 225:592 */     this.zzAtBOL = true;
/* 226:593 */     this.zzAtEOF = false;
/* 227:594 */     this.zzEOFDone = false;
/* 228:595 */     this.zzEndRead = (this.zzStartRead = 0);
/* 229:596 */     this.zzCurrentPos = (this.zzMarkedPos = 0);
/* 230:597 */     this.yyline = (this.yychar = this.yycolumn = 0);
/* 231:598 */     this.zzLexicalState = 0;
/* 232:    */   }
/* 233:    */   
/* 234:    */   public final int yystate()
/* 235:    */   {
/* 236:606 */     return this.zzLexicalState;
/* 237:    */   }
/* 238:    */   
/* 239:    */   public final void yybegin(int newState)
/* 240:    */   {
/* 241:616 */     this.zzLexicalState = newState;
/* 242:    */   }
/* 243:    */   
/* 244:    */   public final String yytext()
/* 245:    */   {
/* 246:624 */     return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
/* 247:    */   }
/* 248:    */   
/* 249:    */   public final char yycharat(int pos)
/* 250:    */   {
/* 251:640 */     return this.zzBuffer[(this.zzStartRead + pos)];
/* 252:    */   }
/* 253:    */   
/* 254:    */   public final int yylength()
/* 255:    */   {
/* 256:648 */     return this.zzMarkedPos - this.zzStartRead;
/* 257:    */   }
/* 258:    */   
/* 259:    */   private void zzScanError(int errorCode)
/* 260:    */   {
/* 261:    */     String message;
/* 262:    */     try
/* 263:    */     {
/* 264:669 */       message = ZZ_ERROR_MSG[errorCode];
/* 265:    */     }
/* 266:    */     catch (ArrayIndexOutOfBoundsException e)
/* 267:    */     {
/* 268:672 */       message = ZZ_ERROR_MSG[0];
/* 269:    */     }
/* 270:675 */     throw new Error(message);
/* 271:    */   }
/* 272:    */   
/* 273:    */   public void yypushback(int number)
/* 274:    */   {
/* 275:688 */     if (number > yylength()) {
/* 276:689 */       zzScanError(2);
/* 277:    */     }
/* 278:691 */     this.zzMarkedPos -= number;
/* 279:    */   }
/* 280:    */   
/* 281:    */   public Token yylex()
/* 282:    */     throws IOException
/* 283:    */   {
/* 284:709 */     int zzEndReadL = this.zzEndRead;
/* 285:710 */     char[] zzBufferL = this.zzBuffer;
/* 286:711 */     char[] zzCMapL = ZZ_CMAP;
/* 287:    */     
/* 288:713 */     int[] zzTransL = ZZ_TRANS;
/* 289:714 */     int[] zzRowMapL = ZZ_ROWMAP;
/* 290:715 */     int[] zzAttrL = ZZ_ATTRIBUTE;
/* 291:    */     for (;;)
/* 292:    */     {
/* 293:718 */       int zzMarkedPosL = this.zzMarkedPos;
/* 294:    */       
/* 295:720 */       this.yychar += zzMarkedPosL - this.zzStartRead;
/* 296:    */       
/* 297:722 */       int zzAction = -1;
/* 298:    */       
/* 299:724 */       int zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
/* 300:    */       
/* 301:726 */       this.zzState = ZZ_LEXSTATE[this.zzLexicalState];
/* 302:    */       int zzInput;
/* 303:    */       for (;;)
/* 304:    */       {
/* 305:    */         int zzInput;
/* 306:732 */         if (zzCurrentPosL < zzEndReadL)
/* 307:    */         {
/* 308:733 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/* 309:    */         }
/* 310:    */         else
/* 311:    */         {
/* 312:734 */           if (this.zzAtEOF)
/* 313:    */           {
/* 314:735 */             int zzInput = -1;
/* 315:736 */             break;
/* 316:    */           }
/* 317:740 */           this.zzCurrentPos = zzCurrentPosL;
/* 318:741 */           this.zzMarkedPos = zzMarkedPosL;
/* 319:742 */           boolean eof = zzRefill();
/* 320:    */           
/* 321:744 */           zzCurrentPosL = this.zzCurrentPos;
/* 322:745 */           zzMarkedPosL = this.zzMarkedPos;
/* 323:746 */           zzBufferL = this.zzBuffer;
/* 324:747 */           zzEndReadL = this.zzEndRead;
/* 325:748 */           if (eof)
/* 326:    */           {
/* 327:749 */             int zzInput = -1;
/* 328:750 */             break;
/* 329:    */           }
/* 330:753 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/* 331:    */         }
/* 332:756 */         int zzNext = zzTransL[(zzRowMapL[this.zzState] + zzCMapL[zzInput])];
/* 333:757 */         if (zzNext == -1) {
/* 334:    */           break;
/* 335:    */         }
/* 336:758 */         this.zzState = zzNext;
/* 337:    */         
/* 338:760 */         int zzAttributes = zzAttrL[this.zzState];
/* 339:761 */         if ((zzAttributes & 0x1) == 1)
/* 340:    */         {
/* 341:762 */           zzAction = this.zzState;
/* 342:763 */           zzMarkedPosL = zzCurrentPosL;
/* 343:764 */           if ((zzAttributes & 0x8) == 8) {
/* 344:    */             break;
/* 345:    */           }
/* 346:    */         }
/* 347:    */       }
/* 348:771 */       this.zzMarkedPos = zzMarkedPosL;
/* 349:773 */       switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction])
/* 350:    */       {
/* 351:    */       case 8: 
/* 352:775 */         return token(TokenType.STRING);
/* 353:    */       case 9: 
/* 354:    */         break;
/* 355:    */       case 6: 
/* 356:779 */         return token(TokenType.KEYWORD);
/* 357:    */       case 10: 
/* 358:    */         break;
/* 359:    */       case 7: 
/* 360:783 */         return token(TokenType.TYPE);
/* 361:    */       case 11: 
/* 362:    */         break;
/* 363:    */       case 2: 
/* 364:787 */         return token(TokenType.OPERATOR);
/* 365:    */       case 12: 
/* 366:    */         break;
/* 367:    */       case 4: 
/* 368:791 */         return token(TokenType.NUMBER);
/* 369:    */       case 13: 
/* 370:    */         break;
/* 371:    */       case 3: 
/* 372:795 */         return token(TokenType.IDENTIFIER);
/* 373:    */       case 14: 
/* 374:    */         break;
/* 375:    */       case 5: 
/* 376:799 */         return token(TokenType.COMMENT);
/* 377:    */       case 15: 
/* 378:    */         break;
/* 379:    */       case 1: 
/* 380:    */       case 16: 
/* 381:    */         break;
/* 382:    */       default: 
/* 383:807 */         if ((zzInput == -1) && (this.zzStartRead == this.zzCurrentPos))
/* 384:    */         {
/* 385:808 */           this.zzAtEOF = true;
/* 386:    */           
/* 387:810 */           return null;
/* 388:    */         }
/* 389:814 */         zzScanError(1);
/* 390:    */       }
/* 391:    */     }
/* 392:    */   }
/* 393:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.lexers.TALLexer
 * JD-Core Version:    0.7.0.1
 */