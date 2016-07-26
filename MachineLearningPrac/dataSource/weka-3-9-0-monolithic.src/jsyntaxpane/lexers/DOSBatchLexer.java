/*   1:    */ package jsyntaxpane.lexers;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.io.InputStreamReader;
/*   6:    */ import java.io.Reader;
/*   7:    */ import jsyntaxpane.Token;
/*   8:    */ import jsyntaxpane.TokenType;
/*   9:    */ 
/*  10:    */ public final class DOSBatchLexer
/*  11:    */   extends DefaultJFlexLexer
/*  12:    */ {
/*  13:    */   public static final int YYEOF = -1;
/*  14:    */   private static final int ZZ_BUFFERSIZE = 16384;
/*  15:    */   public static final int ECHO_TEXT = 2;
/*  16:    */   public static final int YYINITIAL = 0;
/*  17: 48 */   private static final int[] ZZ_LEXSTATE = { 0, 0, 1, 1 };
/*  18:    */   private static final String ZZ_CMAP_PACKED = "";
/*  19:149 */   private static final char[] ZZ_CMAP = zzUnpackCMap("");
/*  20:154 */   private static final int[] ZZ_ACTION = zzUnpackAction();
/*  21:    */   private static final String ZZ_ACTION_PACKED_0 = "";
/*  22:    */   
/*  23:    */   private static int[] zzUnpackAction()
/*  24:    */   {
/*  25:165 */     int[] result = new int['¤'];
/*  26:166 */     int offset = 0;
/*  27:167 */     offset = zzUnpackAction("", offset, result);
/*  28:168 */     return result;
/*  29:    */   }
/*  30:    */   
/*  31:    */   private static int zzUnpackAction(String packed, int offset, int[] result)
/*  32:    */   {
/*  33:172 */     int i = 0;
/*  34:173 */     int j = offset;
/*  35:174 */     int l = packed.length();
/*  36:    */     int count;
/*  37:175 */     for (; i < l; count > 0)
/*  38:    */     {
/*  39:176 */       count = packed.charAt(i++);
/*  40:177 */       int value = packed.charAt(i++);
/*  41:178 */       result[(j++)] = value;count--;
/*  42:    */     }
/*  43:180 */     return j;
/*  44:    */   }
/*  45:    */   
/*  46:187 */   private static final int[] ZZ_ROWMAP = zzUnpackRowMap();
/*  47:    */   private static final String ZZ_ROWMAP_PACKED_0 = "";
/*  48:    */   
/*  49:    */   private static int[] zzUnpackRowMap()
/*  50:    */   {
/*  51:213 */     int[] result = new int['¤'];
/*  52:214 */     int offset = 0;
/*  53:215 */     offset = zzUnpackRowMap("", offset, result);
/*  54:216 */     return result;
/*  55:    */   }
/*  56:    */   
/*  57:    */   private static int zzUnpackRowMap(String packed, int offset, int[] result)
/*  58:    */   {
/*  59:220 */     int i = 0;
/*  60:221 */     int j = offset;
/*  61:222 */     int l = packed.length();
/*  62:223 */     while (i < l)
/*  63:    */     {
/*  64:224 */       int high = packed.charAt(i++) << '\020';
/*  65:225 */       result[(j++)] = (high | packed.charAt(i++));
/*  66:    */     }
/*  67:227 */     return j;
/*  68:    */   }
/*  69:    */   
/*  70:233 */   private static final int[] ZZ_TRANS = zzUnpackTrans();
/*  71:    */   private static final String ZZ_TRANS_PACKED_0 = "";
/*  72:    */   private static final int ZZ_UNKNOWN_ERROR = 0;
/*  73:    */   private static final int ZZ_NO_MATCH = 1;
/*  74:    */   private static final int ZZ_PUSHBACK_2BIG = 2;
/*  75:    */   
/*  76:    */   private static int[] zzUnpackTrans()
/*  77:    */   {
/*  78:435 */     int[] result = new int[5270];
/*  79:436 */     int offset = 0;
/*  80:437 */     offset = zzUnpackTrans("", offset, result);
/*  81:438 */     return result;
/*  82:    */   }
/*  83:    */   
/*  84:    */   private static int zzUnpackTrans(String packed, int offset, int[] result)
/*  85:    */   {
/*  86:442 */     int i = 0;
/*  87:443 */     int j = offset;
/*  88:444 */     int l = packed.length();
/*  89:    */     int count;
/*  90:445 */     for (; i < l; count > 0)
/*  91:    */     {
/*  92:446 */       count = packed.charAt(i++);
/*  93:447 */       int value = packed.charAt(i++);
/*  94:448 */       value--;
/*  95:449 */       result[(j++)] = value;count--;
/*  96:    */     }
/*  97:451 */     return j;
/*  98:    */   }
/*  99:    */   
/* 100:461 */   private static final String[] ZZ_ERROR_MSG = { "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large" };
/* 101:470 */   private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
/* 102:    */   private static final String ZZ_ATTRIBUTE_PACKED_0 = "";
/* 103:    */   private Reader zzReader;
/* 104:    */   private int zzState;
/* 105:    */   
/* 106:    */   private static int[] zzUnpackAttribute()
/* 107:    */   {
/* 108:477 */     int[] result = new int['¤'];
/* 109:478 */     int offset = 0;
/* 110:479 */     offset = zzUnpackAttribute("", offset, result);
/* 111:480 */     return result;
/* 112:    */   }
/* 113:    */   
/* 114:    */   private static int zzUnpackAttribute(String packed, int offset, int[] result)
/* 115:    */   {
/* 116:484 */     int i = 0;
/* 117:485 */     int j = offset;
/* 118:486 */     int l = packed.length();
/* 119:    */     int count;
/* 120:487 */     for (; i < l; count > 0)
/* 121:    */     {
/* 122:488 */       count = packed.charAt(i++);
/* 123:489 */       int value = packed.charAt(i++);
/* 124:490 */       result[(j++)] = value;count--;
/* 125:    */     }
/* 126:492 */     return j;
/* 127:    */   }
/* 128:    */   
/* 129:502 */   private int zzLexicalState = 0;
/* 130:506 */   private char[] zzBuffer = new char[16384];
/* 131:    */   private int zzMarkedPos;
/* 132:    */   private int zzCurrentPos;
/* 133:    */   private int zzStartRead;
/* 134:    */   private int zzEndRead;
/* 135:    */   private int yyline;
/* 136:    */   private int yychar;
/* 137:    */   private int yycolumn;
/* 138:536 */   private boolean zzAtBOL = true;
/* 139:    */   private boolean zzAtEOF;
/* 140:    */   private boolean zzEOFDone;
/* 141:    */   
/* 142:    */   public DOSBatchLexer() {}
/* 143:    */   
/* 144:    */   public int yychar()
/* 145:    */   {
/* 146:555 */     return this.yychar;
/* 147:    */   }
/* 148:    */   
/* 149:    */   public DOSBatchLexer(Reader in)
/* 150:    */   {
/* 151:566 */     this.zzReader = in;
/* 152:    */   }
/* 153:    */   
/* 154:    */   public DOSBatchLexer(InputStream in)
/* 155:    */   {
/* 156:576 */     this(new InputStreamReader(in));
/* 157:    */   }
/* 158:    */   
/* 159:    */   private static char[] zzUnpackCMap(String packed)
/* 160:    */   {
/* 161:586 */     char[] map = new char[65536];
/* 162:587 */     int i = 0;
/* 163:588 */     int j = 0;
/* 164:    */     int count;
/* 165:589 */     for (; i < 1762; count > 0)
/* 166:    */     {
/* 167:590 */       count = packed.charAt(i++);
/* 168:591 */       char value = packed.charAt(i++);
/* 169:592 */       map[(j++)] = value;count--;
/* 170:    */     }
/* 171:594 */     return map;
/* 172:    */   }
/* 173:    */   
/* 174:    */   private boolean zzRefill()
/* 175:    */     throws IOException
/* 176:    */   {
/* 177:608 */     if (this.zzStartRead > 0)
/* 178:    */     {
/* 179:609 */       System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
/* 180:    */       
/* 181:    */ 
/* 182:    */ 
/* 183:    */ 
/* 184:614 */       this.zzEndRead -= this.zzStartRead;
/* 185:615 */       this.zzCurrentPos -= this.zzStartRead;
/* 186:616 */       this.zzMarkedPos -= this.zzStartRead;
/* 187:617 */       this.zzStartRead = 0;
/* 188:    */     }
/* 189:621 */     if (this.zzCurrentPos >= this.zzBuffer.length)
/* 190:    */     {
/* 191:623 */       char[] newBuffer = new char[this.zzCurrentPos * 2];
/* 192:624 */       System.arraycopy(this.zzBuffer, 0, newBuffer, 0, this.zzBuffer.length);
/* 193:625 */       this.zzBuffer = newBuffer;
/* 194:    */     }
/* 195:629 */     int numRead = this.zzReader.read(this.zzBuffer, this.zzEndRead, this.zzBuffer.length - this.zzEndRead);
/* 196:632 */     if (numRead > 0)
/* 197:    */     {
/* 198:633 */       this.zzEndRead += numRead;
/* 199:634 */       return false;
/* 200:    */     }
/* 201:637 */     if (numRead == 0)
/* 202:    */     {
/* 203:638 */       int c = this.zzReader.read();
/* 204:639 */       if (c == -1) {
/* 205:640 */         return true;
/* 206:    */       }
/* 207:642 */       this.zzBuffer[(this.zzEndRead++)] = ((char)c);
/* 208:643 */       return false;
/* 209:    */     }
/* 210:648 */     return true;
/* 211:    */   }
/* 212:    */   
/* 213:    */   public final void yyclose()
/* 214:    */     throws IOException
/* 215:    */   {
/* 216:656 */     this.zzAtEOF = true;
/* 217:657 */     this.zzEndRead = this.zzStartRead;
/* 218:659 */     if (this.zzReader != null) {
/* 219:660 */       this.zzReader.close();
/* 220:    */     }
/* 221:    */   }
/* 222:    */   
/* 223:    */   public final void yyreset(Reader reader)
/* 224:    */   {
/* 225:675 */     this.zzReader = reader;
/* 226:676 */     this.zzAtBOL = true;
/* 227:677 */     this.zzAtEOF = false;
/* 228:678 */     this.zzEOFDone = false;
/* 229:679 */     this.zzEndRead = (this.zzStartRead = 0);
/* 230:680 */     this.zzCurrentPos = (this.zzMarkedPos = 0);
/* 231:681 */     this.yyline = (this.yychar = this.yycolumn = 0);
/* 232:682 */     this.zzLexicalState = 0;
/* 233:    */   }
/* 234:    */   
/* 235:    */   public final int yystate()
/* 236:    */   {
/* 237:690 */     return this.zzLexicalState;
/* 238:    */   }
/* 239:    */   
/* 240:    */   public final void yybegin(int newState)
/* 241:    */   {
/* 242:700 */     this.zzLexicalState = newState;
/* 243:    */   }
/* 244:    */   
/* 245:    */   public final String yytext()
/* 246:    */   {
/* 247:708 */     return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
/* 248:    */   }
/* 249:    */   
/* 250:    */   public final char yycharat(int pos)
/* 251:    */   {
/* 252:724 */     return this.zzBuffer[(this.zzStartRead + pos)];
/* 253:    */   }
/* 254:    */   
/* 255:    */   public final int yylength()
/* 256:    */   {
/* 257:732 */     return this.zzMarkedPos - this.zzStartRead;
/* 258:    */   }
/* 259:    */   
/* 260:    */   private void zzScanError(int errorCode)
/* 261:    */   {
/* 262:    */     String message;
/* 263:    */     try
/* 264:    */     {
/* 265:753 */       message = ZZ_ERROR_MSG[errorCode];
/* 266:    */     }
/* 267:    */     catch (ArrayIndexOutOfBoundsException e)
/* 268:    */     {
/* 269:756 */       message = ZZ_ERROR_MSG[0];
/* 270:    */     }
/* 271:759 */     throw new Error(message);
/* 272:    */   }
/* 273:    */   
/* 274:    */   public void yypushback(int number)
/* 275:    */   {
/* 276:772 */     if (number > yylength()) {
/* 277:773 */       zzScanError(2);
/* 278:    */     }
/* 279:775 */     this.zzMarkedPos -= number;
/* 280:    */   }
/* 281:    */   
/* 282:    */   public Token yylex()
/* 283:    */     throws IOException
/* 284:    */   {
/* 285:793 */     int zzEndReadL = this.zzEndRead;
/* 286:794 */     char[] zzBufferL = this.zzBuffer;
/* 287:795 */     char[] zzCMapL = ZZ_CMAP;
/* 288:    */     
/* 289:797 */     int[] zzTransL = ZZ_TRANS;
/* 290:798 */     int[] zzRowMapL = ZZ_ROWMAP;
/* 291:799 */     int[] zzAttrL = ZZ_ATTRIBUTE;
/* 292:    */     for (;;)
/* 293:    */     {
/* 294:802 */       int zzMarkedPosL = this.zzMarkedPos;
/* 295:    */       
/* 296:804 */       this.yychar += zzMarkedPosL - this.zzStartRead;
/* 297:    */       
/* 298:806 */       int zzAction = -1;
/* 299:    */       
/* 300:808 */       int zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
/* 301:    */       
/* 302:810 */       this.zzState = ZZ_LEXSTATE[this.zzLexicalState];
/* 303:    */       int zzInput;
/* 304:    */       for (;;)
/* 305:    */       {
/* 306:    */         int zzInput;
/* 307:816 */         if (zzCurrentPosL < zzEndReadL)
/* 308:    */         {
/* 309:817 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/* 310:    */         }
/* 311:    */         else
/* 312:    */         {
/* 313:818 */           if (this.zzAtEOF)
/* 314:    */           {
/* 315:819 */             int zzInput = -1;
/* 316:820 */             break;
/* 317:    */           }
/* 318:824 */           this.zzCurrentPos = zzCurrentPosL;
/* 319:825 */           this.zzMarkedPos = zzMarkedPosL;
/* 320:826 */           boolean eof = zzRefill();
/* 321:    */           
/* 322:828 */           zzCurrentPosL = this.zzCurrentPos;
/* 323:829 */           zzMarkedPosL = this.zzMarkedPos;
/* 324:830 */           zzBufferL = this.zzBuffer;
/* 325:831 */           zzEndReadL = this.zzEndRead;
/* 326:832 */           if (eof)
/* 327:    */           {
/* 328:833 */             int zzInput = -1;
/* 329:834 */             break;
/* 330:    */           }
/* 331:837 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/* 332:    */         }
/* 333:840 */         int zzNext = zzTransL[(zzRowMapL[this.zzState] + zzCMapL[zzInput])];
/* 334:841 */         if (zzNext == -1) {
/* 335:    */           break;
/* 336:    */         }
/* 337:842 */         this.zzState = zzNext;
/* 338:    */         
/* 339:844 */         int zzAttributes = zzAttrL[this.zzState];
/* 340:845 */         if ((zzAttributes & 0x1) == 1)
/* 341:    */         {
/* 342:846 */           zzAction = this.zzState;
/* 343:847 */           zzMarkedPosL = zzCurrentPosL;
/* 344:848 */           if ((zzAttributes & 0x8) == 8) {
/* 345:    */             break;
/* 346:    */           }
/* 347:    */         }
/* 348:    */       }
/* 349:855 */       this.zzMarkedPos = zzMarkedPosL;
/* 350:857 */       switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction])
/* 351:    */       {
/* 352:    */       case 5: 
/* 353:859 */         yybegin(0);
/* 354:    */       case 11: 
/* 355:    */         break;
/* 356:    */       case 8: 
/* 357:863 */         return token(TokenType.STRING2);
/* 358:    */       case 12: 
/* 359:    */         break;
/* 360:    */       case 10: 
/* 361:867 */         yybegin(2);
/* 362:868 */         return token(TokenType.KEYWORD);
/* 363:    */       case 13: 
/* 364:    */         break;
/* 365:    */       case 4: 
/* 366:872 */         return token(TokenType.KEYWORD);
/* 367:    */       case 14: 
/* 368:    */         break;
/* 369:    */       case 7: 
/* 370:876 */         return token(TokenType.TYPE3);
/* 371:    */       case 15: 
/* 372:    */         break;
/* 373:    */       case 6: 
/* 374:880 */         return token(TokenType.KEYWORD2);
/* 375:    */       case 16: 
/* 376:    */         break;
/* 377:    */       case 3: 
/* 378:884 */         return token(TokenType.IDENTIFIER);
/* 379:    */       case 17: 
/* 380:    */         break;
/* 381:    */       case 1: 
/* 382:888 */         return token(TokenType.STRING);
/* 383:    */       case 18: 
/* 384:    */         break;
/* 385:    */       case 9: 
/* 386:892 */         return token(TokenType.COMMENT);
/* 387:    */       case 19: 
/* 388:    */         break;
/* 389:    */       case 2: 
/* 390:    */       case 20: 
/* 391:    */         break;
/* 392:    */       default: 
/* 393:900 */         if ((zzInput == -1) && (this.zzStartRead == this.zzCurrentPos))
/* 394:    */         {
/* 395:901 */           this.zzAtEOF = true;
/* 396:    */           
/* 397:903 */           return null;
/* 398:    */         }
/* 399:907 */         zzScanError(1);
/* 400:    */       }
/* 401:    */     }
/* 402:    */   }
/* 403:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.lexers.DOSBatchLexer
 * JD-Core Version:    0.7.0.1
 */