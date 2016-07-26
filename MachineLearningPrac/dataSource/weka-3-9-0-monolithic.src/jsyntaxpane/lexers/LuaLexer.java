/*   1:    */ package jsyntaxpane.lexers;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.io.InputStreamReader;
/*   6:    */ import java.io.Reader;
/*   7:    */ import jsyntaxpane.Token;
/*   8:    */ import jsyntaxpane.TokenType;
/*   9:    */ 
/*  10:    */ public final class LuaLexer
/*  11:    */   extends DefaultJFlexLexer
/*  12:    */ {
/*  13:    */   public static final int YYEOF = -1;
/*  14:    */   private static final int ZZ_BUFFERSIZE = 16384;
/*  15:    */   public static final int STRING = 2;
/*  16:    */   public static final int YYINITIAL = 0;
/*  17: 48 */   private static final int[] ZZ_LEXSTATE = { 0, 0, 1, 1 };
/*  18:    */   private static final String ZZ_CMAP_PACKED = "";
/*  19:148 */   private static final char[] ZZ_CMAP = zzUnpackCMap("");
/*  20:153 */   private static final int[] ZZ_ACTION = zzUnpackAction();
/*  21:    */   private static final String ZZ_ACTION_PACKED_0 = "";
/*  22:    */   
/*  23:    */   private static int[] zzUnpackAction()
/*  24:    */   {
/*  25:164 */     int[] result = new int[97];
/*  26:165 */     int offset = 0;
/*  27:166 */     offset = zzUnpackAction("", offset, result);
/*  28:167 */     return result;
/*  29:    */   }
/*  30:    */   
/*  31:    */   private static int zzUnpackAction(String packed, int offset, int[] result)
/*  32:    */   {
/*  33:171 */     int i = 0;
/*  34:172 */     int j = offset;
/*  35:173 */     int l = packed.length();
/*  36:    */     int count;
/*  37:174 */     for (; i < l; count > 0)
/*  38:    */     {
/*  39:175 */       count = packed.charAt(i++);
/*  40:176 */       int value = packed.charAt(i++);
/*  41:177 */       result[(j++)] = value;count--;
/*  42:    */     }
/*  43:179 */     return j;
/*  44:    */   }
/*  45:    */   
/*  46:186 */   private static final int[] ZZ_ROWMAP = zzUnpackRowMap();
/*  47:    */   private static final String ZZ_ROWMAP_PACKED_0 = "";
/*  48:    */   
/*  49:    */   private static int[] zzUnpackRowMap()
/*  50:    */   {
/*  51:204 */     int[] result = new int[97];
/*  52:205 */     int offset = 0;
/*  53:206 */     offset = zzUnpackRowMap("", offset, result);
/*  54:207 */     return result;
/*  55:    */   }
/*  56:    */   
/*  57:    */   private static int zzUnpackRowMap(String packed, int offset, int[] result)
/*  58:    */   {
/*  59:211 */     int i = 0;
/*  60:212 */     int j = offset;
/*  61:213 */     int l = packed.length();
/*  62:214 */     while (i < l)
/*  63:    */     {
/*  64:215 */       int high = packed.charAt(i++) << '\020';
/*  65:216 */       result[(j++)] = (high | packed.charAt(i++));
/*  66:    */     }
/*  67:218 */     return j;
/*  68:    */   }
/*  69:    */   
/*  70:224 */   private static final int[] ZZ_TRANS = zzUnpackTrans();
/*  71:    */   private static final String ZZ_TRANS_PACKED_0 = "";
/*  72:    */   private static final int ZZ_UNKNOWN_ERROR = 0;
/*  73:    */   private static final int ZZ_NO_MATCH = 1;
/*  74:    */   private static final int ZZ_PUSHBACK_2BIG = 2;
/*  75:    */   
/*  76:    */   private static int[] zzUnpackTrans()
/*  77:    */   {
/*  78:314 */     int[] result = new int[3485];
/*  79:315 */     int offset = 0;
/*  80:316 */     offset = zzUnpackTrans("", offset, result);
/*  81:317 */     return result;
/*  82:    */   }
/*  83:    */   
/*  84:    */   private static int zzUnpackTrans(String packed, int offset, int[] result)
/*  85:    */   {
/*  86:321 */     int i = 0;
/*  87:322 */     int j = offset;
/*  88:323 */     int l = packed.length();
/*  89:    */     int count;
/*  90:324 */     for (; i < l; count > 0)
/*  91:    */     {
/*  92:325 */       count = packed.charAt(i++);
/*  93:326 */       int value = packed.charAt(i++);
/*  94:327 */       value--;
/*  95:328 */       result[(j++)] = value;count--;
/*  96:    */     }
/*  97:330 */     return j;
/*  98:    */   }
/*  99:    */   
/* 100:340 */   private static final String[] ZZ_ERROR_MSG = { "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large" };
/* 101:349 */   private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
/* 102:    */   private static final String ZZ_ATTRIBUTE_PACKED_0 = "";
/* 103:    */   private Reader zzReader;
/* 104:    */   private int zzState;
/* 105:    */   
/* 106:    */   private static int[] zzUnpackAttribute()
/* 107:    */   {
/* 108:358 */     int[] result = new int[97];
/* 109:359 */     int offset = 0;
/* 110:360 */     offset = zzUnpackAttribute("", offset, result);
/* 111:361 */     return result;
/* 112:    */   }
/* 113:    */   
/* 114:    */   private static int zzUnpackAttribute(String packed, int offset, int[] result)
/* 115:    */   {
/* 116:365 */     int i = 0;
/* 117:366 */     int j = offset;
/* 118:367 */     int l = packed.length();
/* 119:    */     int count;
/* 120:368 */     for (; i < l; count > 0)
/* 121:    */     {
/* 122:369 */       count = packed.charAt(i++);
/* 123:370 */       int value = packed.charAt(i++);
/* 124:371 */       result[(j++)] = value;count--;
/* 125:    */     }
/* 126:373 */     return j;
/* 127:    */   }
/* 128:    */   
/* 129:383 */   private int zzLexicalState = 0;
/* 130:387 */   private char[] zzBuffer = new char[16384];
/* 131:    */   private int zzMarkedPos;
/* 132:    */   private int zzCurrentPos;
/* 133:    */   private int zzStartRead;
/* 134:    */   private int zzEndRead;
/* 135:    */   private int yyline;
/* 136:    */   private int yychar;
/* 137:    */   private int yycolumn;
/* 138:417 */   private boolean zzAtBOL = true;
/* 139:    */   private boolean zzAtEOF;
/* 140:    */   private boolean zzEOFDone;
/* 141:    */   
/* 142:    */   public LuaLexer() {}
/* 143:    */   
/* 144:    */   public int yychar()
/* 145:    */   {
/* 146:436 */     return this.yychar;
/* 147:    */   }
/* 148:    */   
/* 149:    */   public LuaLexer(Reader in)
/* 150:    */   {
/* 151:447 */     this.zzReader = in;
/* 152:    */   }
/* 153:    */   
/* 154:    */   public LuaLexer(InputStream in)
/* 155:    */   {
/* 156:457 */     this(new InputStreamReader(in));
/* 157:    */   }
/* 158:    */   
/* 159:    */   private static char[] zzUnpackCMap(String packed)
/* 160:    */   {
/* 161:467 */     char[] map = new char[65536];
/* 162:468 */     int i = 0;
/* 163:469 */     int j = 0;
/* 164:    */     int count;
/* 165:470 */     for (; i < 1760; count > 0)
/* 166:    */     {
/* 167:471 */       count = packed.charAt(i++);
/* 168:472 */       char value = packed.charAt(i++);
/* 169:473 */       map[(j++)] = value;count--;
/* 170:    */     }
/* 171:475 */     return map;
/* 172:    */   }
/* 173:    */   
/* 174:    */   private boolean zzRefill()
/* 175:    */     throws IOException
/* 176:    */   {
/* 177:489 */     if (this.zzStartRead > 0)
/* 178:    */     {
/* 179:490 */       System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
/* 180:    */       
/* 181:    */ 
/* 182:    */ 
/* 183:    */ 
/* 184:495 */       this.zzEndRead -= this.zzStartRead;
/* 185:496 */       this.zzCurrentPos -= this.zzStartRead;
/* 186:497 */       this.zzMarkedPos -= this.zzStartRead;
/* 187:498 */       this.zzStartRead = 0;
/* 188:    */     }
/* 189:502 */     if (this.zzCurrentPos >= this.zzBuffer.length)
/* 190:    */     {
/* 191:504 */       char[] newBuffer = new char[this.zzCurrentPos * 2];
/* 192:505 */       System.arraycopy(this.zzBuffer, 0, newBuffer, 0, this.zzBuffer.length);
/* 193:506 */       this.zzBuffer = newBuffer;
/* 194:    */     }
/* 195:510 */     int numRead = this.zzReader.read(this.zzBuffer, this.zzEndRead, this.zzBuffer.length - this.zzEndRead);
/* 196:513 */     if (numRead > 0)
/* 197:    */     {
/* 198:514 */       this.zzEndRead += numRead;
/* 199:515 */       return false;
/* 200:    */     }
/* 201:518 */     if (numRead == 0)
/* 202:    */     {
/* 203:519 */       int c = this.zzReader.read();
/* 204:520 */       if (c == -1) {
/* 205:521 */         return true;
/* 206:    */       }
/* 207:523 */       this.zzBuffer[(this.zzEndRead++)] = ((char)c);
/* 208:524 */       return false;
/* 209:    */     }
/* 210:529 */     return true;
/* 211:    */   }
/* 212:    */   
/* 213:    */   public final void yyclose()
/* 214:    */     throws IOException
/* 215:    */   {
/* 216:537 */     this.zzAtEOF = true;
/* 217:538 */     this.zzEndRead = this.zzStartRead;
/* 218:540 */     if (this.zzReader != null) {
/* 219:541 */       this.zzReader.close();
/* 220:    */     }
/* 221:    */   }
/* 222:    */   
/* 223:    */   public final void yyreset(Reader reader)
/* 224:    */   {
/* 225:556 */     this.zzReader = reader;
/* 226:557 */     this.zzAtBOL = true;
/* 227:558 */     this.zzAtEOF = false;
/* 228:559 */     this.zzEOFDone = false;
/* 229:560 */     this.zzEndRead = (this.zzStartRead = 0);
/* 230:561 */     this.zzCurrentPos = (this.zzMarkedPos = 0);
/* 231:562 */     this.yyline = (this.yychar = this.yycolumn = 0);
/* 232:563 */     this.zzLexicalState = 0;
/* 233:    */   }
/* 234:    */   
/* 235:    */   public final int yystate()
/* 236:    */   {
/* 237:571 */     return this.zzLexicalState;
/* 238:    */   }
/* 239:    */   
/* 240:    */   public final void yybegin(int newState)
/* 241:    */   {
/* 242:581 */     this.zzLexicalState = newState;
/* 243:    */   }
/* 244:    */   
/* 245:    */   public final String yytext()
/* 246:    */   {
/* 247:589 */     return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
/* 248:    */   }
/* 249:    */   
/* 250:    */   public final char yycharat(int pos)
/* 251:    */   {
/* 252:605 */     return this.zzBuffer[(this.zzStartRead + pos)];
/* 253:    */   }
/* 254:    */   
/* 255:    */   public final int yylength()
/* 256:    */   {
/* 257:613 */     return this.zzMarkedPos - this.zzStartRead;
/* 258:    */   }
/* 259:    */   
/* 260:    */   private void zzScanError(int errorCode)
/* 261:    */   {
/* 262:    */     String message;
/* 263:    */     try
/* 264:    */     {
/* 265:634 */       message = ZZ_ERROR_MSG[errorCode];
/* 266:    */     }
/* 267:    */     catch (ArrayIndexOutOfBoundsException e)
/* 268:    */     {
/* 269:637 */       message = ZZ_ERROR_MSG[0];
/* 270:    */     }
/* 271:640 */     throw new Error(message);
/* 272:    */   }
/* 273:    */   
/* 274:    */   public void yypushback(int number)
/* 275:    */   {
/* 276:653 */     if (number > yylength()) {
/* 277:654 */       zzScanError(2);
/* 278:    */     }
/* 279:656 */     this.zzMarkedPos -= number;
/* 280:    */   }
/* 281:    */   
/* 282:    */   public Token yylex()
/* 283:    */     throws IOException
/* 284:    */   {
/* 285:674 */     int zzEndReadL = this.zzEndRead;
/* 286:675 */     char[] zzBufferL = this.zzBuffer;
/* 287:676 */     char[] zzCMapL = ZZ_CMAP;
/* 288:    */     
/* 289:678 */     int[] zzTransL = ZZ_TRANS;
/* 290:679 */     int[] zzRowMapL = ZZ_ROWMAP;
/* 291:680 */     int[] zzAttrL = ZZ_ATTRIBUTE;
/* 292:    */     for (;;)
/* 293:    */     {
/* 294:683 */       int zzMarkedPosL = this.zzMarkedPos;
/* 295:    */       
/* 296:685 */       this.yychar += zzMarkedPosL - this.zzStartRead;
/* 297:    */       
/* 298:687 */       int zzAction = -1;
/* 299:    */       
/* 300:689 */       int zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
/* 301:    */       
/* 302:691 */       this.zzState = ZZ_LEXSTATE[this.zzLexicalState];
/* 303:    */       int zzInput;
/* 304:    */       for (;;)
/* 305:    */       {
/* 306:    */         int zzInput;
/* 307:697 */         if (zzCurrentPosL < zzEndReadL)
/* 308:    */         {
/* 309:698 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/* 310:    */         }
/* 311:    */         else
/* 312:    */         {
/* 313:699 */           if (this.zzAtEOF)
/* 314:    */           {
/* 315:700 */             int zzInput = -1;
/* 316:701 */             break;
/* 317:    */           }
/* 318:705 */           this.zzCurrentPos = zzCurrentPosL;
/* 319:706 */           this.zzMarkedPos = zzMarkedPosL;
/* 320:707 */           boolean eof = zzRefill();
/* 321:    */           
/* 322:709 */           zzCurrentPosL = this.zzCurrentPos;
/* 323:710 */           zzMarkedPosL = this.zzMarkedPos;
/* 324:711 */           zzBufferL = this.zzBuffer;
/* 325:712 */           zzEndReadL = this.zzEndRead;
/* 326:713 */           if (eof)
/* 327:    */           {
/* 328:714 */             int zzInput = -1;
/* 329:715 */             break;
/* 330:    */           }
/* 331:718 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/* 332:    */         }
/* 333:721 */         int zzNext = zzTransL[(zzRowMapL[this.zzState] + zzCMapL[zzInput])];
/* 334:722 */         if (zzNext == -1) {
/* 335:    */           break;
/* 336:    */         }
/* 337:723 */         this.zzState = zzNext;
/* 338:    */         
/* 339:725 */         int zzAttributes = zzAttrL[this.zzState];
/* 340:726 */         if ((zzAttributes & 0x1) == 1)
/* 341:    */         {
/* 342:727 */           zzAction = this.zzState;
/* 343:728 */           zzMarkedPosL = zzCurrentPosL;
/* 344:729 */           if ((zzAttributes & 0x8) == 8) {
/* 345:    */             break;
/* 346:    */           }
/* 347:    */         }
/* 348:    */       }
/* 349:736 */       this.zzMarkedPos = zzMarkedPosL;
/* 350:738 */       switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction])
/* 351:    */       {
/* 352:    */       case 10: 
/* 353:740 */         return token(TokenType.KEYWORD);
/* 354:    */       case 12: 
/* 355:    */         break;
/* 356:    */       case 5: 
/* 357:744 */         yybegin(2);
/* 358:745 */         this.tokenStart = this.yychar;
/* 359:746 */         this.tokenLength = 1;
/* 360:    */       case 13: 
/* 361:    */         break;
/* 362:    */       case 8: 
/* 363:750 */         yybegin(0);
/* 364:    */         
/* 365:752 */         return token(TokenType.STRING, this.tokenStart, this.tokenLength + 1);
/* 366:    */       case 14: 
/* 367:    */         break;
/* 368:    */       case 2: 
/* 369:756 */         return token(TokenType.OPERATOR);
/* 370:    */       case 15: 
/* 371:    */         break;
/* 372:    */       case 11: 
/* 373:760 */         this.tokenLength += 2;
/* 374:    */       case 16: 
/* 375:    */         break;
/* 376:    */       case 3: 
/* 377:764 */         return token(TokenType.IDENTIFIER);
/* 378:    */       case 17: 
/* 379:    */         break;
/* 380:    */       case 9: 
/* 381:768 */         return token(TokenType.COMMENT);
/* 382:    */       case 18: 
/* 383:    */         break;
/* 384:    */       case 4: 
/* 385:772 */         return token(TokenType.NUMBER);
/* 386:    */       case 19: 
/* 387:    */         break;
/* 388:    */       case 7: 
/* 389:776 */         yybegin(0);
/* 390:    */       case 20: 
/* 391:    */         break;
/* 392:    */       case 1: 
/* 393:    */       case 21: 
/* 394:    */         break;
/* 395:    */       case 6: 
/* 396:784 */         this.tokenLength += yylength();
/* 397:    */       case 22: 
/* 398:    */         break;
/* 399:    */       default: 
/* 400:788 */         if ((zzInput == -1) && (this.zzStartRead == this.zzCurrentPos))
/* 401:    */         {
/* 402:789 */           this.zzAtEOF = true;
/* 403:    */           
/* 404:791 */           return null;
/* 405:    */         }
/* 406:795 */         zzScanError(1);
/* 407:    */       }
/* 408:    */     }
/* 409:    */   }
/* 410:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.lexers.LuaLexer
 * JD-Core Version:    0.7.0.1
 */