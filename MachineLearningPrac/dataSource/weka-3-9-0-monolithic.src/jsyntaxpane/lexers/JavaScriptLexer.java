/*   1:    */ package jsyntaxpane.lexers;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.io.InputStreamReader;
/*   6:    */ import java.io.Reader;
/*   7:    */ import jsyntaxpane.Token;
/*   8:    */ import jsyntaxpane.TokenType;
/*   9:    */ 
/*  10:    */ public final class JavaScriptLexer
/*  11:    */   extends DefaultJFlexLexer
/*  12:    */ {
/*  13:    */   public static final int YYEOF = -1;
/*  14:    */   private static final int ZZ_BUFFERSIZE = 16384;
/*  15:    */   public static final int STRING = 2;
/*  16:    */   public static final int YYINITIAL = 0;
/*  17: 48 */   private static final int[] ZZ_LEXSTATE = { 0, 0, 1, 1 };
/*  18:    */   private static final String ZZ_CMAP_PACKED = "";
/*  19:150 */   private static final char[] ZZ_CMAP = zzUnpackCMap("");
/*  20:155 */   private static final int[] ZZ_ACTION = zzUnpackAction();
/*  21:    */   private static final String ZZ_ACTION_PACKED_0 = "";
/*  22:    */   
/*  23:    */   private static int[] zzUnpackAction()
/*  24:    */   {
/*  25:168 */     int[] result = new int['§'];
/*  26:169 */     int offset = 0;
/*  27:170 */     offset = zzUnpackAction("", offset, result);
/*  28:171 */     return result;
/*  29:    */   }
/*  30:    */   
/*  31:    */   private static int zzUnpackAction(String packed, int offset, int[] result)
/*  32:    */   {
/*  33:175 */     int i = 0;
/*  34:176 */     int j = offset;
/*  35:177 */     int l = packed.length();
/*  36:    */     int count;
/*  37:178 */     for (; i < l; count > 0)
/*  38:    */     {
/*  39:179 */       count = packed.charAt(i++);
/*  40:180 */       int value = packed.charAt(i++);
/*  41:181 */       result[(j++)] = value;count--;
/*  42:    */     }
/*  43:183 */     return j;
/*  44:    */   }
/*  45:    */   
/*  46:190 */   private static final int[] ZZ_ROWMAP = zzUnpackRowMap();
/*  47:    */   private static final String ZZ_ROWMAP_PACKED_0 = "";
/*  48:    */   
/*  49:    */   private static int[] zzUnpackRowMap()
/*  50:    */   {
/*  51:216 */     int[] result = new int['§'];
/*  52:217 */     int offset = 0;
/*  53:218 */     offset = zzUnpackRowMap("", offset, result);
/*  54:219 */     return result;
/*  55:    */   }
/*  56:    */   
/*  57:    */   private static int zzUnpackRowMap(String packed, int offset, int[] result)
/*  58:    */   {
/*  59:223 */     int i = 0;
/*  60:224 */     int j = offset;
/*  61:225 */     int l = packed.length();
/*  62:226 */     while (i < l)
/*  63:    */     {
/*  64:227 */       int high = packed.charAt(i++) << '\020';
/*  65:228 */       result[(j++)] = (high | packed.charAt(i++));
/*  66:    */     }
/*  67:230 */     return j;
/*  68:    */   }
/*  69:    */   
/*  70:236 */   private static final int[] ZZ_TRANS = zzUnpackTrans();
/*  71:    */   private static final String ZZ_TRANS_PACKED_0 = "";
/*  72:    */   private static final int ZZ_UNKNOWN_ERROR = 0;
/*  73:    */   private static final int ZZ_NO_MATCH = 1;
/*  74:    */   private static final int ZZ_PUSHBACK_2BIG = 2;
/*  75:    */   
/*  76:    */   private static int[] zzUnpackTrans()
/*  77:    */   {
/*  78:442 */     int[] result = new int[8892];
/*  79:443 */     int offset = 0;
/*  80:444 */     offset = zzUnpackTrans("", offset, result);
/*  81:445 */     return result;
/*  82:    */   }
/*  83:    */   
/*  84:    */   private static int zzUnpackTrans(String packed, int offset, int[] result)
/*  85:    */   {
/*  86:449 */     int i = 0;
/*  87:450 */     int j = offset;
/*  88:451 */     int l = packed.length();
/*  89:    */     int count;
/*  90:452 */     for (; i < l; count > 0)
/*  91:    */     {
/*  92:453 */       count = packed.charAt(i++);
/*  93:454 */       int value = packed.charAt(i++);
/*  94:455 */       value--;
/*  95:456 */       result[(j++)] = value;count--;
/*  96:    */     }
/*  97:458 */     return j;
/*  98:    */   }
/*  99:    */   
/* 100:468 */   private static final String[] ZZ_ERROR_MSG = { "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large" };
/* 101:477 */   private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
/* 102:    */   private static final String ZZ_ATTRIBUTE_PACKED_0 = "";
/* 103:    */   private Reader zzReader;
/* 104:    */   private int zzState;
/* 105:    */   
/* 106:    */   private static int[] zzUnpackAttribute()
/* 107:    */   {
/* 108:488 */     int[] result = new int['§'];
/* 109:489 */     int offset = 0;
/* 110:490 */     offset = zzUnpackAttribute("", offset, result);
/* 111:491 */     return result;
/* 112:    */   }
/* 113:    */   
/* 114:    */   private static int zzUnpackAttribute(String packed, int offset, int[] result)
/* 115:    */   {
/* 116:495 */     int i = 0;
/* 117:496 */     int j = offset;
/* 118:497 */     int l = packed.length();
/* 119:    */     int count;
/* 120:498 */     for (; i < l; count > 0)
/* 121:    */     {
/* 122:499 */       count = packed.charAt(i++);
/* 123:500 */       int value = packed.charAt(i++);
/* 124:501 */       result[(j++)] = value;count--;
/* 125:    */     }
/* 126:503 */     return j;
/* 127:    */   }
/* 128:    */   
/* 129:513 */   private int zzLexicalState = 0;
/* 130:517 */   private char[] zzBuffer = new char[16384];
/* 131:    */   private int zzMarkedPos;
/* 132:    */   private int zzCurrentPos;
/* 133:    */   private int zzStartRead;
/* 134:    */   private int zzEndRead;
/* 135:    */   private int yyline;
/* 136:    */   private int yychar;
/* 137:    */   private int yycolumn;
/* 138:547 */   private boolean zzAtBOL = true;
/* 139:    */   private boolean zzAtEOF;
/* 140:    */   private boolean zzEOFDone;
/* 141:    */   
/* 142:    */   public JavaScriptLexer() {}
/* 143:    */   
/* 144:    */   public int yychar()
/* 145:    */   {
/* 146:566 */     return this.yychar;
/* 147:    */   }
/* 148:    */   
/* 149:    */   public JavaScriptLexer(Reader in)
/* 150:    */   {
/* 151:577 */     this.zzReader = in;
/* 152:    */   }
/* 153:    */   
/* 154:    */   public JavaScriptLexer(InputStream in)
/* 155:    */   {
/* 156:587 */     this(new InputStreamReader(in));
/* 157:    */   }
/* 158:    */   
/* 159:    */   private static char[] zzUnpackCMap(String packed)
/* 160:    */   {
/* 161:597 */     char[] map = new char[65536];
/* 162:598 */     int i = 0;
/* 163:599 */     int j = 0;
/* 164:    */     int count;
/* 165:600 */     for (; i < 1792; count > 0)
/* 166:    */     {
/* 167:601 */       count = packed.charAt(i++);
/* 168:602 */       char value = packed.charAt(i++);
/* 169:603 */       map[(j++)] = value;count--;
/* 170:    */     }
/* 171:605 */     return map;
/* 172:    */   }
/* 173:    */   
/* 174:    */   private boolean zzRefill()
/* 175:    */     throws IOException
/* 176:    */   {
/* 177:619 */     if (this.zzStartRead > 0)
/* 178:    */     {
/* 179:620 */       System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
/* 180:    */       
/* 181:    */ 
/* 182:    */ 
/* 183:    */ 
/* 184:625 */       this.zzEndRead -= this.zzStartRead;
/* 185:626 */       this.zzCurrentPos -= this.zzStartRead;
/* 186:627 */       this.zzMarkedPos -= this.zzStartRead;
/* 187:628 */       this.zzStartRead = 0;
/* 188:    */     }
/* 189:632 */     if (this.zzCurrentPos >= this.zzBuffer.length)
/* 190:    */     {
/* 191:634 */       char[] newBuffer = new char[this.zzCurrentPos * 2];
/* 192:635 */       System.arraycopy(this.zzBuffer, 0, newBuffer, 0, this.zzBuffer.length);
/* 193:636 */       this.zzBuffer = newBuffer;
/* 194:    */     }
/* 195:640 */     int numRead = this.zzReader.read(this.zzBuffer, this.zzEndRead, this.zzBuffer.length - this.zzEndRead);
/* 196:643 */     if (numRead > 0)
/* 197:    */     {
/* 198:644 */       this.zzEndRead += numRead;
/* 199:645 */       return false;
/* 200:    */     }
/* 201:648 */     if (numRead == 0)
/* 202:    */     {
/* 203:649 */       int c = this.zzReader.read();
/* 204:650 */       if (c == -1) {
/* 205:651 */         return true;
/* 206:    */       }
/* 207:653 */       this.zzBuffer[(this.zzEndRead++)] = ((char)c);
/* 208:654 */       return false;
/* 209:    */     }
/* 210:659 */     return true;
/* 211:    */   }
/* 212:    */   
/* 213:    */   public final void yyclose()
/* 214:    */     throws IOException
/* 215:    */   {
/* 216:667 */     this.zzAtEOF = true;
/* 217:668 */     this.zzEndRead = this.zzStartRead;
/* 218:670 */     if (this.zzReader != null) {
/* 219:671 */       this.zzReader.close();
/* 220:    */     }
/* 221:    */   }
/* 222:    */   
/* 223:    */   public final void yyreset(Reader reader)
/* 224:    */   {
/* 225:686 */     this.zzReader = reader;
/* 226:687 */     this.zzAtBOL = true;
/* 227:688 */     this.zzAtEOF = false;
/* 228:689 */     this.zzEOFDone = false;
/* 229:690 */     this.zzEndRead = (this.zzStartRead = 0);
/* 230:691 */     this.zzCurrentPos = (this.zzMarkedPos = 0);
/* 231:692 */     this.yyline = (this.yychar = this.yycolumn = 0);
/* 232:693 */     this.zzLexicalState = 0;
/* 233:    */   }
/* 234:    */   
/* 235:    */   public final int yystate()
/* 236:    */   {
/* 237:701 */     return this.zzLexicalState;
/* 238:    */   }
/* 239:    */   
/* 240:    */   public final void yybegin(int newState)
/* 241:    */   {
/* 242:711 */     this.zzLexicalState = newState;
/* 243:    */   }
/* 244:    */   
/* 245:    */   public final String yytext()
/* 246:    */   {
/* 247:719 */     return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
/* 248:    */   }
/* 249:    */   
/* 250:    */   public final char yycharat(int pos)
/* 251:    */   {
/* 252:735 */     return this.zzBuffer[(this.zzStartRead + pos)];
/* 253:    */   }
/* 254:    */   
/* 255:    */   public final int yylength()
/* 256:    */   {
/* 257:743 */     return this.zzMarkedPos - this.zzStartRead;
/* 258:    */   }
/* 259:    */   
/* 260:    */   private void zzScanError(int errorCode)
/* 261:    */   {
/* 262:    */     String message;
/* 263:    */     try
/* 264:    */     {
/* 265:764 */       message = ZZ_ERROR_MSG[errorCode];
/* 266:    */     }
/* 267:    */     catch (ArrayIndexOutOfBoundsException e)
/* 268:    */     {
/* 269:767 */       message = ZZ_ERROR_MSG[0];
/* 270:    */     }
/* 271:770 */     throw new Error(message);
/* 272:    */   }
/* 273:    */   
/* 274:    */   public void yypushback(int number)
/* 275:    */   {
/* 276:783 */     if (number > yylength()) {
/* 277:784 */       zzScanError(2);
/* 278:    */     }
/* 279:786 */     this.zzMarkedPos -= number;
/* 280:    */   }
/* 281:    */   
/* 282:    */   public Token yylex()
/* 283:    */     throws IOException
/* 284:    */   {
/* 285:804 */     int zzEndReadL = this.zzEndRead;
/* 286:805 */     char[] zzBufferL = this.zzBuffer;
/* 287:806 */     char[] zzCMapL = ZZ_CMAP;
/* 288:    */     
/* 289:808 */     int[] zzTransL = ZZ_TRANS;
/* 290:809 */     int[] zzRowMapL = ZZ_ROWMAP;
/* 291:810 */     int[] zzAttrL = ZZ_ATTRIBUTE;
/* 292:    */     for (;;)
/* 293:    */     {
/* 294:813 */       int zzMarkedPosL = this.zzMarkedPos;
/* 295:    */       
/* 296:815 */       this.yychar += zzMarkedPosL - this.zzStartRead;
/* 297:    */       
/* 298:817 */       int zzAction = -1;
/* 299:    */       
/* 300:819 */       int zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
/* 301:    */       
/* 302:821 */       this.zzState = ZZ_LEXSTATE[this.zzLexicalState];
/* 303:    */       int zzInput;
/* 304:    */       for (;;)
/* 305:    */       {
/* 306:    */         int zzInput;
/* 307:827 */         if (zzCurrentPosL < zzEndReadL)
/* 308:    */         {
/* 309:828 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/* 310:    */         }
/* 311:    */         else
/* 312:    */         {
/* 313:829 */           if (this.zzAtEOF)
/* 314:    */           {
/* 315:830 */             int zzInput = -1;
/* 316:831 */             break;
/* 317:    */           }
/* 318:835 */           this.zzCurrentPos = zzCurrentPosL;
/* 319:836 */           this.zzMarkedPos = zzMarkedPosL;
/* 320:837 */           boolean eof = zzRefill();
/* 321:    */           
/* 322:839 */           zzCurrentPosL = this.zzCurrentPos;
/* 323:840 */           zzMarkedPosL = this.zzMarkedPos;
/* 324:841 */           zzBufferL = this.zzBuffer;
/* 325:842 */           zzEndReadL = this.zzEndRead;
/* 326:843 */           if (eof)
/* 327:    */           {
/* 328:844 */             int zzInput = -1;
/* 329:845 */             break;
/* 330:    */           }
/* 331:848 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/* 332:    */         }
/* 333:851 */         int zzNext = zzTransL[(zzRowMapL[this.zzState] + zzCMapL[zzInput])];
/* 334:852 */         if (zzNext == -1) {
/* 335:    */           break;
/* 336:    */         }
/* 337:853 */         this.zzState = zzNext;
/* 338:    */         
/* 339:855 */         int zzAttributes = zzAttrL[this.zzState];
/* 340:856 */         if ((zzAttributes & 0x1) == 1)
/* 341:    */         {
/* 342:857 */           zzAction = this.zzState;
/* 343:858 */           zzMarkedPosL = zzCurrentPosL;
/* 344:859 */           if ((zzAttributes & 0x8) == 8) {
/* 345:    */             break;
/* 346:    */           }
/* 347:    */         }
/* 348:    */       }
/* 349:866 */       this.zzMarkedPos = zzMarkedPosL;
/* 350:868 */       switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction])
/* 351:    */       {
/* 352:    */       case 11: 
/* 353:870 */         return token(TokenType.KEYWORD);
/* 354:    */       case 13: 
/* 355:    */         break;
/* 356:    */       case 5: 
/* 357:874 */         yybegin(2);
/* 358:875 */         this.tokenStart = this.yychar;
/* 359:876 */         this.tokenLength = 1;
/* 360:    */       case 14: 
/* 361:    */         break;
/* 362:    */       case 8: 
/* 363:880 */         yybegin(0);
/* 364:    */         
/* 365:882 */         return token(TokenType.STRING, this.tokenStart, this.tokenLength + 1);
/* 366:    */       case 15: 
/* 367:    */         break;
/* 368:    */       case 2: 
/* 369:886 */         return token(TokenType.OPERATOR);
/* 370:    */       case 16: 
/* 371:    */         break;
/* 372:    */       case 12: 
/* 373:890 */         this.tokenLength += 2;
/* 374:    */       case 17: 
/* 375:    */         break;
/* 376:    */       case 3: 
/* 377:894 */         return token(TokenType.IDENTIFIER);
/* 378:    */       case 18: 
/* 379:    */         break;
/* 380:    */       case 9: 
/* 381:898 */         return token(TokenType.COMMENT);
/* 382:    */       case 19: 
/* 383:    */         break;
/* 384:    */       case 10: 
/* 385:902 */         return token(TokenType.TYPE);
/* 386:    */       case 20: 
/* 387:    */         break;
/* 388:    */       case 4: 
/* 389:906 */         return token(TokenType.NUMBER);
/* 390:    */       case 21: 
/* 391:    */         break;
/* 392:    */       case 7: 
/* 393:910 */         yybegin(0);
/* 394:    */       case 22: 
/* 395:    */         break;
/* 396:    */       case 1: 
/* 397:    */       case 23: 
/* 398:    */         break;
/* 399:    */       case 6: 
/* 400:918 */         this.tokenLength += yylength();
/* 401:    */       case 24: 
/* 402:    */         break;
/* 403:    */       default: 
/* 404:922 */         if ((zzInput == -1) && (this.zzStartRead == this.zzCurrentPos))
/* 405:    */         {
/* 406:923 */           this.zzAtEOF = true;
/* 407:    */           
/* 408:925 */           return null;
/* 409:    */         }
/* 410:929 */         zzScanError(1);
/* 411:    */       }
/* 412:    */     }
/* 413:    */   }
/* 414:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.lexers.JavaScriptLexer
 * JD-Core Version:    0.7.0.1
 */