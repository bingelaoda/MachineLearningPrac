/*   1:    */ package weka.core.json;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.io.InputStreamReader;
/*   6:    */ import java.io.PrintStream;
/*   7:    */ import java.io.Reader;
/*   8:    */ import java_cup.runtime.Symbol;
/*   9:    */ import java_cup.runtime.SymbolFactory;
/*  10:    */ 
/*  11:    */ public class Scanner
/*  12:    */   implements java_cup.runtime.Scanner
/*  13:    */ {
/*  14:    */   public static final int YYEOF = -1;
/*  15:    */   private static final int ZZ_BUFFERSIZE = 16384;
/*  16:    */   public static final int YYINITIAL = 0;
/*  17:    */   public static final int STRING = 2;
/*  18: 53 */   private static final int[] ZZ_LEXSTATE = { 0, 0, 1, 1 };
/*  19: 60 */   private static final char[] ZZ_CMAP = { '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\024', '\030', '\000', '\024', '\026', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\024', '\000', '\023', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\005', '\022', '\021', '\000', '\020', '\020', '\020', '\020', '\020', '\020', '\020', '\020', '\020', '\020', '\006', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\003', '\025', '\004', '\000', '\000', '\000', '\016', '\027', '\000', '\000', '\f', '\r', '\000', '\000', '\000', '\000', '\000', '\t', '\000', '\007', '\000', '\000', '\000', '\013', '\017', '\n', '\b', '\000', '\000', '\000', '\000', '\000', '\001', '\000', '\002', '\000', '\000' };
/*  20: 74 */   private static final int[] ZZ_ACTION = zzUnpackAction();
/*  21:    */   private static final String ZZ_ACTION_PACKED_0 = "";
/*  22:    */   
/*  23:    */   private static int[] zzUnpackAction()
/*  24:    */   {
/*  25: 83 */     int[] result = new int[35];
/*  26: 84 */     int offset = 0;
/*  27: 85 */     offset = zzUnpackAction("", offset, result);
/*  28: 86 */     return result;
/*  29:    */   }
/*  30:    */   
/*  31:    */   private static int zzUnpackAction(String packed, int offset, int[] result)
/*  32:    */   {
/*  33: 90 */     int i = 0;
/*  34: 91 */     int j = offset;
/*  35: 92 */     int l = packed.length();
/*  36:    */     int count;
/*  37: 93 */     for (; i < l; count > 0)
/*  38:    */     {
/*  39: 94 */       count = packed.charAt(i++);
/*  40: 95 */       int value = packed.charAt(i++);
/*  41: 96 */       result[(j++)] = value;count--;
/*  42:    */     }
/*  43: 98 */     return j;
/*  44:    */   }
/*  45:    */   
/*  46:105 */   private static final int[] ZZ_ROWMAP = zzUnpackRowMap();
/*  47:    */   private static final String ZZ_ROWMAP_PACKED_0 = "";
/*  48:    */   
/*  49:    */   private static int[] zzUnpackRowMap()
/*  50:    */   {
/*  51:115 */     int[] result = new int[35];
/*  52:116 */     int offset = 0;
/*  53:117 */     offset = zzUnpackRowMap("", offset, result);
/*  54:118 */     return result;
/*  55:    */   }
/*  56:    */   
/*  57:    */   private static int zzUnpackRowMap(String packed, int offset, int[] result)
/*  58:    */   {
/*  59:122 */     int i = 0;
/*  60:123 */     int j = offset;
/*  61:124 */     int l = packed.length();
/*  62:125 */     while (i < l)
/*  63:    */     {
/*  64:126 */       int high = packed.charAt(i++) << '\020';
/*  65:127 */       result[(j++)] = (high | packed.charAt(i++));
/*  66:    */     }
/*  67:129 */     return j;
/*  68:    */   }
/*  69:    */   
/*  70:135 */   private static final int[] ZZ_TRANS = zzUnpackTrans();
/*  71:    */   private static final String ZZ_TRANS_PACKED_0 = "";
/*  72:    */   private static final int ZZ_UNKNOWN_ERROR = 0;
/*  73:    */   private static final int ZZ_NO_MATCH = 1;
/*  74:    */   private static final int ZZ_PUSHBACK_2BIG = 2;
/*  75:    */   
/*  76:    */   private static int[] zzUnpackTrans()
/*  77:    */   {
/*  78:150 */     int[] result = new int[450];
/*  79:151 */     int offset = 0;
/*  80:152 */     offset = zzUnpackTrans("", offset, result);
/*  81:153 */     return result;
/*  82:    */   }
/*  83:    */   
/*  84:    */   private static int zzUnpackTrans(String packed, int offset, int[] result)
/*  85:    */   {
/*  86:157 */     int i = 0;
/*  87:158 */     int j = offset;
/*  88:159 */     int l = packed.length();
/*  89:    */     int count;
/*  90:160 */     for (; i < l; count > 0)
/*  91:    */     {
/*  92:161 */       count = packed.charAt(i++);
/*  93:162 */       int value = packed.charAt(i++);
/*  94:163 */       value--;
/*  95:164 */       result[(j++)] = value;count--;
/*  96:    */     }
/*  97:166 */     return j;
/*  98:    */   }
/*  99:    */   
/* 100:176 */   private static final String[] ZZ_ERROR_MSG = { "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large" };
/* 101:185 */   private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
/* 102:    */   private static final String ZZ_ATTRIBUTE_PACKED_0 = "";
/* 103:    */   private Reader zzReader;
/* 104:    */   private int zzState;
/* 105:    */   
/* 106:    */   private static int[] zzUnpackAttribute()
/* 107:    */   {
/* 108:192 */     int[] result = new int[35];
/* 109:193 */     int offset = 0;
/* 110:194 */     offset = zzUnpackAttribute("", offset, result);
/* 111:195 */     return result;
/* 112:    */   }
/* 113:    */   
/* 114:    */   private static int zzUnpackAttribute(String packed, int offset, int[] result)
/* 115:    */   {
/* 116:199 */     int i = 0;
/* 117:200 */     int j = offset;
/* 118:201 */     int l = packed.length();
/* 119:    */     int count;
/* 120:202 */     for (; i < l; count > 0)
/* 121:    */     {
/* 122:203 */       count = packed.charAt(i++);
/* 123:204 */       int value = packed.charAt(i++);
/* 124:205 */       result[(j++)] = value;count--;
/* 125:    */     }
/* 126:207 */     return j;
/* 127:    */   }
/* 128:    */   
/* 129:217 */   private int zzLexicalState = 0;
/* 130:221 */   private char[] zzBuffer = new char[16384];
/* 131:    */   private int zzMarkedPos;
/* 132:    */   private int zzCurrentPos;
/* 133:    */   private int zzStartRead;
/* 134:    */   private int zzEndRead;
/* 135:    */   private int yyline;
/* 136:    */   private int yychar;
/* 137:    */   private int yycolumn;
/* 138:251 */   private boolean zzAtBOL = true;
/* 139:    */   private boolean zzAtEOF;
/* 140:    */   private boolean zzEOFDone;
/* 141:265 */   private int zzFinalHighSurrogate = 0;
/* 142:    */   protected SymbolFactory m_SF;
/* 143:272 */   protected StringBuffer m_String = new StringBuffer();
/* 144:    */   
/* 145:    */   public Scanner(InputStream r, SymbolFactory sf)
/* 146:    */   {
/* 147:275 */     this(new InputStreamReader(r));
/* 148:276 */     this.m_SF = sf;
/* 149:    */   }
/* 150:    */   
/* 151:    */   public Scanner(Reader r, SymbolFactory sf)
/* 152:    */   {
/* 153:280 */     this(r);
/* 154:281 */     this.m_SF = sf;
/* 155:    */   }
/* 156:    */   
/* 157:    */   public Scanner(Reader in)
/* 158:    */   {
/* 159:291 */     this.zzReader = in;
/* 160:    */   }
/* 161:    */   
/* 162:    */   private boolean zzRefill()
/* 163:    */     throws IOException
/* 164:    */   {
/* 165:306 */     if (this.zzStartRead > 0)
/* 166:    */     {
/* 167:307 */       this.zzEndRead += this.zzFinalHighSurrogate;
/* 168:308 */       this.zzFinalHighSurrogate = 0;
/* 169:309 */       System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
/* 170:    */       
/* 171:    */ 
/* 172:    */ 
/* 173:    */ 
/* 174:314 */       this.zzEndRead -= this.zzStartRead;
/* 175:315 */       this.zzCurrentPos -= this.zzStartRead;
/* 176:316 */       this.zzMarkedPos -= this.zzStartRead;
/* 177:317 */       this.zzStartRead = 0;
/* 178:    */     }
/* 179:321 */     if (this.zzCurrentPos >= this.zzBuffer.length - this.zzFinalHighSurrogate)
/* 180:    */     {
/* 181:323 */       char[] newBuffer = new char[this.zzBuffer.length * 2];
/* 182:324 */       System.arraycopy(this.zzBuffer, 0, newBuffer, 0, this.zzBuffer.length);
/* 183:325 */       this.zzBuffer = newBuffer;
/* 184:326 */       this.zzEndRead += this.zzFinalHighSurrogate;
/* 185:327 */       this.zzFinalHighSurrogate = 0;
/* 186:    */     }
/* 187:331 */     int requested = this.zzBuffer.length - this.zzEndRead;
/* 188:332 */     int totalRead = 0;
/* 189:333 */     while (totalRead < requested)
/* 190:    */     {
/* 191:334 */       int numRead = this.zzReader.read(this.zzBuffer, this.zzEndRead + totalRead, requested - totalRead);
/* 192:335 */       if (numRead == -1) {
/* 193:    */         break;
/* 194:    */       }
/* 195:338 */       totalRead += numRead;
/* 196:    */     }
/* 197:341 */     if (totalRead > 0)
/* 198:    */     {
/* 199:342 */       this.zzEndRead += totalRead;
/* 200:343 */       if ((totalRead == requested) && 
/* 201:344 */         (Character.isHighSurrogate(this.zzBuffer[(this.zzEndRead - 1)])))
/* 202:    */       {
/* 203:345 */         this.zzEndRead -= 1;
/* 204:346 */         this.zzFinalHighSurrogate = 1;
/* 205:    */       }
/* 206:349 */       return false;
/* 207:    */     }
/* 208:353 */     return true;
/* 209:    */   }
/* 210:    */   
/* 211:    */   public final void yyclose()
/* 212:    */     throws IOException
/* 213:    */   {
/* 214:361 */     this.zzAtEOF = true;
/* 215:362 */     this.zzEndRead = this.zzStartRead;
/* 216:364 */     if (this.zzReader != null) {
/* 217:365 */       this.zzReader.close();
/* 218:    */     }
/* 219:    */   }
/* 220:    */   
/* 221:    */   public final void yyreset(Reader reader)
/* 222:    */   {
/* 223:382 */     this.zzReader = reader;
/* 224:383 */     this.zzAtBOL = true;
/* 225:384 */     this.zzAtEOF = false;
/* 226:385 */     this.zzEOFDone = false;
/* 227:386 */     this.zzEndRead = (this.zzStartRead = 0);
/* 228:387 */     this.zzCurrentPos = (this.zzMarkedPos = 0);
/* 229:388 */     this.zzFinalHighSurrogate = 0;
/* 230:389 */     this.yyline = (this.yychar = this.yycolumn = 0);
/* 231:390 */     this.zzLexicalState = 0;
/* 232:391 */     if (this.zzBuffer.length > 16384) {
/* 233:392 */       this.zzBuffer = new char[16384];
/* 234:    */     }
/* 235:    */   }
/* 236:    */   
/* 237:    */   public final int yystate()
/* 238:    */   {
/* 239:400 */     return this.zzLexicalState;
/* 240:    */   }
/* 241:    */   
/* 242:    */   public final void yybegin(int newState)
/* 243:    */   {
/* 244:410 */     this.zzLexicalState = newState;
/* 245:    */   }
/* 246:    */   
/* 247:    */   public final String yytext()
/* 248:    */   {
/* 249:418 */     return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
/* 250:    */   }
/* 251:    */   
/* 252:    */   public final char yycharat(int pos)
/* 253:    */   {
/* 254:434 */     return this.zzBuffer[(this.zzStartRead + pos)];
/* 255:    */   }
/* 256:    */   
/* 257:    */   public final int yylength()
/* 258:    */   {
/* 259:442 */     return this.zzMarkedPos - this.zzStartRead;
/* 260:    */   }
/* 261:    */   
/* 262:    */   private void zzScanError(int errorCode)
/* 263:    */   {
/* 264:    */     String message;
/* 265:    */     try
/* 266:    */     {
/* 267:463 */       message = ZZ_ERROR_MSG[errorCode];
/* 268:    */     }
/* 269:    */     catch (ArrayIndexOutOfBoundsException e)
/* 270:    */     {
/* 271:466 */       message = ZZ_ERROR_MSG[0];
/* 272:    */     }
/* 273:469 */     throw new Error(message);
/* 274:    */   }
/* 275:    */   
/* 276:    */   public void yypushback(int number)
/* 277:    */   {
/* 278:482 */     if (number > yylength()) {
/* 279:483 */       zzScanError(2);
/* 280:    */     }
/* 281:485 */     this.zzMarkedPos -= number;
/* 282:    */   }
/* 283:    */   
/* 284:    */   public Symbol next_token()
/* 285:    */     throws IOException
/* 286:    */   {
/* 287:503 */     int zzEndReadL = this.zzEndRead;
/* 288:504 */     char[] zzBufferL = this.zzBuffer;
/* 289:505 */     char[] zzCMapL = ZZ_CMAP;
/* 290:    */     
/* 291:507 */     int[] zzTransL = ZZ_TRANS;
/* 292:508 */     int[] zzRowMapL = ZZ_ROWMAP;
/* 293:509 */     int[] zzAttrL = ZZ_ATTRIBUTE;
/* 294:    */     for (;;)
/* 295:    */     {
/* 296:512 */       int zzMarkedPosL = this.zzMarkedPos;
/* 297:    */       
/* 298:514 */       int zzAction = -1;
/* 299:    */       
/* 300:516 */       int zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
/* 301:    */       
/* 302:518 */       this.zzState = ZZ_LEXSTATE[this.zzLexicalState];
/* 303:    */       
/* 304:    */ 
/* 305:521 */       int zzAttributes = zzAttrL[this.zzState];
/* 306:522 */       if ((zzAttributes & 0x1) == 1) {
/* 307:523 */         zzAction = this.zzState;
/* 308:    */       }
/* 309:    */       int zzInput;
/* 310:    */       for (;;)
/* 311:    */       {
/* 312:530 */         if (zzCurrentPosL < zzEndReadL)
/* 313:    */         {
/* 314:531 */           int zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
/* 315:532 */           zzCurrentPosL += Character.charCount(zzInput);
/* 316:    */         }
/* 317:    */         else
/* 318:    */         {
/* 319:534 */           if (this.zzAtEOF)
/* 320:    */           {
/* 321:535 */             int zzInput = -1;
/* 322:536 */             break;
/* 323:    */           }
/* 324:540 */           this.zzCurrentPos = zzCurrentPosL;
/* 325:541 */           this.zzMarkedPos = zzMarkedPosL;
/* 326:542 */           boolean eof = zzRefill();
/* 327:    */           
/* 328:544 */           zzCurrentPosL = this.zzCurrentPos;
/* 329:545 */           zzMarkedPosL = this.zzMarkedPos;
/* 330:546 */           zzBufferL = this.zzBuffer;
/* 331:547 */           zzEndReadL = this.zzEndRead;
/* 332:548 */           if (eof)
/* 333:    */           {
/* 334:549 */             int zzInput = -1;
/* 335:550 */             break;
/* 336:    */           }
/* 337:553 */           zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
/* 338:554 */           zzCurrentPosL += Character.charCount(zzInput);
/* 339:    */         }
/* 340:557 */         int zzNext = zzTransL[(zzRowMapL[this.zzState] + zzCMapL[zzInput])];
/* 341:558 */         if (zzNext == -1) {
/* 342:    */           break;
/* 343:    */         }
/* 344:559 */         this.zzState = zzNext;
/* 345:    */         
/* 346:561 */         zzAttributes = zzAttrL[this.zzState];
/* 347:562 */         if ((zzAttributes & 0x1) == 1)
/* 348:    */         {
/* 349:563 */           zzAction = this.zzState;
/* 350:564 */           zzMarkedPosL = zzCurrentPosL;
/* 351:565 */           if ((zzAttributes & 0x8) == 8) {
/* 352:    */             break;
/* 353:    */           }
/* 354:    */         }
/* 355:    */       }
/* 356:572 */       this.zzMarkedPos = zzMarkedPosL;
/* 357:574 */       switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction])
/* 358:    */       {
/* 359:    */       case 1: 
/* 360:576 */         System.err.println("Illegal character: " + yytext());
/* 361:    */       case 23: 
/* 362:    */         break;
/* 363:    */       case 2: 
/* 364:580 */         return this.m_SF.newSymbol("Left curly bracket", 5);
/* 365:    */       case 24: 
/* 366:    */         break;
/* 367:    */       case 3: 
/* 368:584 */         return this.m_SF.newSymbol("Right curly bracket", 6);
/* 369:    */       case 25: 
/* 370:    */         break;
/* 371:    */       case 4: 
/* 372:588 */         return this.m_SF.newSymbol("Left square bracket", 3);
/* 373:    */       case 26: 
/* 374:    */         break;
/* 375:    */       case 5: 
/* 376:592 */         return this.m_SF.newSymbol("Right square bracket", 4);
/* 377:    */       case 27: 
/* 378:    */         break;
/* 379:    */       case 6: 
/* 380:596 */         return this.m_SF.newSymbol("Comma", 2);
/* 381:    */       case 28: 
/* 382:    */         break;
/* 383:    */       case 7: 
/* 384:600 */         return this.m_SF.newSymbol("Colon", 7);
/* 385:    */       case 29: 
/* 386:    */         break;
/* 387:    */       case 8: 
/* 388:604 */         return this.m_SF.newSymbol("Integer", 10, new Integer(yytext()));
/* 389:    */       case 30: 
/* 390:    */         break;
/* 391:    */       case 9: 
/* 392:608 */         this.m_String.setLength(0);yybegin(2);
/* 393:    */       case 31: 
/* 394:    */         break;
/* 395:    */       case 10: 
/* 396:    */       case 32: 
/* 397:    */         break;
/* 398:    */       case 11: 
/* 399:616 */         this.m_String.append(yytext());
/* 400:    */       case 33: 
/* 401:    */         break;
/* 402:    */       case 12: 
/* 403:620 */         yybegin(0);return this.m_SF.newSymbol("String", 12, this.m_String.toString());
/* 404:    */       case 34: 
/* 405:    */         break;
/* 406:    */       case 13: 
/* 407:624 */         this.m_String.append('\\');
/* 408:    */       case 35: 
/* 409:    */         break;
/* 410:    */       case 14: 
/* 411:628 */         return this.m_SF.newSymbol("Double", 11, new Double(yytext()));
/* 412:    */       case 36: 
/* 413:    */         break;
/* 414:    */       case 15: 
/* 415:632 */         this.m_String.append('\n');
/* 416:    */       case 37: 
/* 417:    */         break;
/* 418:    */       case 16: 
/* 419:636 */         this.m_String.append('\t');
/* 420:    */       case 38: 
/* 421:    */         break;
/* 422:    */       case 17: 
/* 423:640 */         this.m_String.append('\r');
/* 424:    */       case 39: 
/* 425:    */         break;
/* 426:    */       case 18: 
/* 427:644 */         this.m_String.append('\f');
/* 428:    */       case 40: 
/* 429:    */         break;
/* 430:    */       case 19: 
/* 431:648 */         this.m_String.append('"');
/* 432:    */       case 41: 
/* 433:    */         break;
/* 434:    */       case 20: 
/* 435:652 */         this.m_String.append('\b');
/* 436:    */       case 42: 
/* 437:    */         break;
/* 438:    */       case 21: 
/* 439:656 */         return this.m_SF.newSymbol("Null", 8);
/* 440:    */       case 43: 
/* 441:    */         break;
/* 442:    */       case 22: 
/* 443:660 */         return this.m_SF.newSymbol("Boolean", 9, new Boolean(yytext()));
/* 444:    */       case 44: 
/* 445:    */         break;
/* 446:    */       default: 
/* 447:664 */         if ((zzInput == -1) && (this.zzStartRead == this.zzCurrentPos))
/* 448:    */         {
/* 449:665 */           this.zzAtEOF = true;
/* 450:666 */           return this.m_SF.newSymbol("EOF", 0);
/* 451:    */         }
/* 452:670 */         zzScanError(1);
/* 453:    */       }
/* 454:    */     }
/* 455:    */   }
/* 456:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.json.Scanner
 * JD-Core Version:    0.7.0.1
 */