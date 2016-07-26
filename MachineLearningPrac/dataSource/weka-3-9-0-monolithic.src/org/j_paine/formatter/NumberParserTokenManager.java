/*   1:    */ package org.j_paine.formatter;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ 
/*   6:    */ public class NumberParserTokenManager
/*   7:    */   implements NumberParserConstants
/*   8:    */ {
/*   9:  6 */   public PrintStream debugStream = System.out;
/*  10:    */   
/*  11:    */   public void setDebugStream(PrintStream paramPrintStream)
/*  12:    */   {
/*  13:  7 */     this.debugStream = paramPrintStream;
/*  14:    */   }
/*  15:    */   
/*  16:    */   private final int jjStopStringLiteralDfa_0(int paramInt, long paramLong)
/*  17:    */   {
/*  18: 10 */     switch (paramInt)
/*  19:    */     {
/*  20:    */     }
/*  21: 13 */     return -1;
/*  22:    */   }
/*  23:    */   
/*  24:    */   private final int jjStartNfa_0(int paramInt, long paramLong)
/*  25:    */   {
/*  26: 18 */     return jjMoveNfa_0(jjStopStringLiteralDfa_0(paramInt, paramLong), paramInt + 1);
/*  27:    */   }
/*  28:    */   
/*  29:    */   private final int jjStopAtPos(int paramInt1, int paramInt2)
/*  30:    */   {
/*  31: 22 */     this.jjmatchedKind = paramInt2;
/*  32: 23 */     this.jjmatchedPos = paramInt1;
/*  33: 24 */     return paramInt1 + 1;
/*  34:    */   }
/*  35:    */   
/*  36:    */   private final int jjStartNfaWithStates_0(int paramInt1, int paramInt2, int paramInt3)
/*  37:    */   {
/*  38: 28 */     this.jjmatchedKind = paramInt2;
/*  39: 29 */     this.jjmatchedPos = paramInt1;
/*  40:    */     try
/*  41:    */     {
/*  42: 30 */       this.curChar = this.input_stream.readChar();
/*  43:    */     }
/*  44:    */     catch (IOException localIOException)
/*  45:    */     {
/*  46: 31 */       return paramInt1 + 1;
/*  47:    */     }
/*  48: 32 */     return jjMoveNfa_0(paramInt3, paramInt1 + 1);
/*  49:    */   }
/*  50:    */   
/*  51:    */   private final int jjMoveStringLiteralDfa0_0()
/*  52:    */   {
/*  53: 36 */     switch (this.curChar)
/*  54:    */     {
/*  55:    */     case ' ': 
/*  56: 39 */       return jjStopAtPos(0, 6);
/*  57:    */     case '+': 
/*  58: 41 */       return jjStopAtPos(0, 8);
/*  59:    */     case '-': 
/*  60: 43 */       return jjStopAtPos(0, 7);
/*  61:    */     }
/*  62: 45 */     return jjMoveNfa_0(0, 0);
/*  63:    */   }
/*  64:    */   
/*  65:    */   private final void jjCheckNAdd(int paramInt)
/*  66:    */   {
/*  67: 50 */     if (this.jjrounds[paramInt] != this.jjround)
/*  68:    */     {
/*  69: 52 */       this.jjstateSet[(this.jjnewStateCnt++)] = paramInt;
/*  70: 53 */       this.jjrounds[paramInt] = this.jjround;
/*  71:    */     }
/*  72:    */   }
/*  73:    */   
/*  74:    */   private final void jjAddStates(int paramInt1, int paramInt2)
/*  75:    */   {
/*  76:    */     do
/*  77:    */     {
/*  78: 59 */       this.jjstateSet[(this.jjnewStateCnt++)] = jjnextStates[paramInt1];
/*  79: 60 */     } while (paramInt1++ != paramInt2);
/*  80:    */   }
/*  81:    */   
/*  82:    */   private final void jjCheckNAddTwoStates(int paramInt1, int paramInt2)
/*  83:    */   {
/*  84: 64 */     jjCheckNAdd(paramInt1);
/*  85: 65 */     jjCheckNAdd(paramInt2);
/*  86:    */   }
/*  87:    */   
/*  88:    */   private final void jjCheckNAddStates(int paramInt1, int paramInt2)
/*  89:    */   {
/*  90:    */     do
/*  91:    */     {
/*  92: 70 */       jjCheckNAdd(jjnextStates[paramInt1]);
/*  93: 71 */     } while (paramInt1++ != paramInt2);
/*  94:    */   }
/*  95:    */   
/*  96:    */   private final void jjCheckNAddStates(int paramInt)
/*  97:    */   {
/*  98: 75 */     jjCheckNAdd(jjnextStates[paramInt]);
/*  99: 76 */     jjCheckNAdd(jjnextStates[(paramInt + 1)]);
/* 100:    */   }
/* 101:    */   
/* 102:    */   private final int jjMoveNfa_0(int paramInt1, int paramInt2)
/* 103:    */   {
/* 104: 81 */     int i = 0;
/* 105: 82 */     this.jjnewStateCnt = 24;
/* 106: 83 */     int j = 1;
/* 107: 84 */     this.jjstateSet[0] = paramInt1;
/* 108: 85 */     int k = 2147483647;
/* 109:    */     for (;;)
/* 110:    */     {
/* 111: 88 */       if (++this.jjround == 2147483647) {
/* 112: 89 */         ReInitRounds();
/* 113:    */       }
/* 114:    */       long l1;
/* 115: 90 */       if (this.curChar < '@')
/* 116:    */       {
/* 117: 92 */         l1 = 1L << this.curChar;
/* 118:    */         do
/* 119:    */         {
/* 120: 95 */           switch (this.jjstateSet[(--j)])
/* 121:    */           {
/* 122:    */           case 0: 
/* 123: 98 */             if ((0x0 & l1) != 0L)
/* 124:    */             {
/* 125:100 */               if (k > 4) {
/* 126:101 */                 k = 4;
/* 127:    */               }
/* 128:102 */               jjCheckNAddStates(0, 5);
/* 129:    */             }
/* 130:104 */             else if (this.curChar == '.')
/* 131:    */             {
/* 132:105 */               jjCheckNAdd(5);
/* 133:    */             }
/* 134:106 */             if ((0x0 & l1) != 0L)
/* 135:    */             {
/* 136:108 */               if (k > 1) {
/* 137:109 */                 k = 1;
/* 138:    */               }
/* 139:110 */               jjCheckNAdd(2);
/* 140:    */             }
/* 141:112 */             else if (this.curChar == '0')
/* 142:    */             {
/* 143:114 */               if (k > 1) {
/* 144:115 */                 k = 1;
/* 145:    */               }
/* 146:    */             }
/* 147:    */             break;
/* 148:    */           case 1: 
/* 149:119 */             if ((0x0 & l1) != 0L)
/* 150:    */             {
/* 151:121 */               if (k > 1) {
/* 152:122 */                 k = 1;
/* 153:    */               }
/* 154:123 */               jjCheckNAdd(2);
/* 155:    */             }
/* 156:124 */             break;
/* 157:    */           case 2: 
/* 158:126 */             if ((0x0 & l1) != 0L)
/* 159:    */             {
/* 160:128 */               if (k > 1) {
/* 161:129 */                 k = 1;
/* 162:    */               }
/* 163:130 */               jjCheckNAdd(2);
/* 164:    */             }
/* 165:131 */             break;
/* 166:    */           case 4: 
/* 167:133 */             if (this.curChar == '.') {
/* 168:134 */               jjCheckNAdd(5);
/* 169:    */             }
/* 170:    */             break;
/* 171:    */           case 5: 
/* 172:137 */             if ((0x0 & l1) != 0L)
/* 173:    */             {
/* 174:139 */               if (k > 4) {
/* 175:140 */                 k = 4;
/* 176:    */               }
/* 177:141 */               jjCheckNAddTwoStates(5, 6);
/* 178:    */             }
/* 179:142 */             break;
/* 180:    */           case 7: 
/* 181:144 */             if ((0x0 & l1) != 0L) {
/* 182:145 */               jjCheckNAdd(8);
/* 183:    */             }
/* 184:    */             break;
/* 185:    */           case 8: 
/* 186:148 */             if ((0x0 & l1) != 0L)
/* 187:    */             {
/* 188:150 */               if (k > 4) {
/* 189:151 */                 k = 4;
/* 190:    */               }
/* 191:152 */               jjCheckNAdd(8);
/* 192:    */             }
/* 193:153 */             break;
/* 194:    */           case 9: 
/* 195:155 */             if ((0x0 & l1) != 0L)
/* 196:    */             {
/* 197:157 */               if (k > 4) {
/* 198:158 */                 k = 4;
/* 199:    */               }
/* 200:159 */               jjCheckNAddStates(0, 5);
/* 201:    */             }
/* 202:160 */             break;
/* 203:    */           case 10: 
/* 204:162 */             if ((0x0 & l1) != 0L) {
/* 205:163 */               jjCheckNAddTwoStates(10, 11);
/* 206:    */             }
/* 207:    */             break;
/* 208:    */           case 11: 
/* 209:166 */             if (this.curChar == '.')
/* 210:    */             {
/* 211:168 */               if (k > 4) {
/* 212:169 */                 k = 4;
/* 213:    */               }
/* 214:170 */               jjCheckNAddTwoStates(12, 13);
/* 215:    */             }
/* 216:171 */             break;
/* 217:    */           case 12: 
/* 218:173 */             if ((0x0 & l1) != 0L)
/* 219:    */             {
/* 220:175 */               if (k > 4) {
/* 221:176 */                 k = 4;
/* 222:    */               }
/* 223:177 */               jjCheckNAddTwoStates(12, 13);
/* 224:    */             }
/* 225:178 */             break;
/* 226:    */           case 14: 
/* 227:180 */             if ((0x0 & l1) != 0L) {
/* 228:181 */               jjCheckNAdd(15);
/* 229:    */             }
/* 230:    */             break;
/* 231:    */           case 15: 
/* 232:184 */             if ((0x0 & l1) != 0L)
/* 233:    */             {
/* 234:186 */               if (k > 4) {
/* 235:187 */                 k = 4;
/* 236:    */               }
/* 237:188 */               jjCheckNAdd(15);
/* 238:    */             }
/* 239:189 */             break;
/* 240:    */           case 16: 
/* 241:191 */             if ((0x0 & l1) != 0L) {
/* 242:192 */               jjCheckNAddTwoStates(16, 17);
/* 243:    */             }
/* 244:    */             break;
/* 245:    */           case 18: 
/* 246:195 */             if ((0x0 & l1) != 0L) {
/* 247:196 */               jjCheckNAdd(19);
/* 248:    */             }
/* 249:    */             break;
/* 250:    */           case 19: 
/* 251:199 */             if ((0x0 & l1) != 0L)
/* 252:    */             {
/* 253:201 */               if (k > 4) {
/* 254:202 */                 k = 4;
/* 255:    */               }
/* 256:203 */               jjCheckNAdd(19);
/* 257:    */             }
/* 258:204 */             break;
/* 259:    */           case 20: 
/* 260:206 */             if ((0x0 & l1) != 0L)
/* 261:    */             {
/* 262:208 */               if (k > 4) {
/* 263:209 */                 k = 4;
/* 264:    */               }
/* 265:210 */               jjCheckNAddTwoStates(20, 21);
/* 266:    */             }
/* 267:211 */             break;
/* 268:    */           case 22: 
/* 269:213 */             if ((0x0 & l1) != 0L) {
/* 270:214 */               jjCheckNAdd(23);
/* 271:    */             }
/* 272:    */             break;
/* 273:    */           case 23: 
/* 274:217 */             if ((0x0 & l1) != 0L)
/* 275:    */             {
/* 276:219 */               if (k > 4) {
/* 277:220 */                 k = 4;
/* 278:    */               }
/* 279:221 */               jjCheckNAdd(23);
/* 280:    */             }
/* 281:    */             break;
/* 282:    */           }
/* 283:225 */         } while (j != i);
/* 284:    */       }
/* 285:227 */       else if (this.curChar < '')
/* 286:    */       {
/* 287:229 */         l1 = 1L << (this.curChar & 0x3F);
/* 288:    */         do
/* 289:    */         {
/* 290:232 */           switch (this.jjstateSet[(--j)])
/* 291:    */           {
/* 292:    */           case 0: 
/* 293:235 */             if ((0x100040 & l1) != 0L) {
/* 294:236 */               k = 3;
/* 295:    */             }
/* 296:    */             break;
/* 297:    */           case 6: 
/* 298:239 */             if ((0x20 & l1) != 0L) {
/* 299:240 */               jjAddStates(6, 7);
/* 300:    */             }
/* 301:    */             break;
/* 302:    */           case 13: 
/* 303:243 */             if ((0x20 & l1) != 0L) {
/* 304:244 */               jjAddStates(8, 9);
/* 305:    */             }
/* 306:    */             break;
/* 307:    */           case 17: 
/* 308:247 */             if ((0x20 & l1) != 0L) {
/* 309:248 */               jjAddStates(10, 11);
/* 310:    */             }
/* 311:    */             break;
/* 312:    */           case 21: 
/* 313:251 */             if ((0x20 & l1) != 0L) {
/* 314:252 */               jjAddStates(12, 13);
/* 315:    */             }
/* 316:    */             break;
/* 317:    */           }
/* 318:256 */         } while (j != i);
/* 319:    */       }
/* 320:    */       else
/* 321:    */       {
/* 322:260 */         int m = (this.curChar & 0xFF) >> '\006';
/* 323:261 */         long l2 = 1L << (this.curChar & 0x3F);
/* 324:    */         do
/* 325:    */         {
/* 326:264 */           switch (this.jjstateSet[(--j)])
/* 327:    */           {
/* 328:    */           }
/* 329:268 */         } while (j != i);
/* 330:    */       }
/* 331:270 */       if (k != 2147483647)
/* 332:    */       {
/* 333:272 */         this.jjmatchedKind = k;
/* 334:273 */         this.jjmatchedPos = paramInt2;
/* 335:274 */         k = 2147483647;
/* 336:    */       }
/* 337:276 */       paramInt2++;
/* 338:277 */       if ((j = this.jjnewStateCnt) == (i = 24 - (this.jjnewStateCnt = i))) {
/* 339:278 */         return paramInt2;
/* 340:    */       }
/* 341:    */       try
/* 342:    */       {
/* 343:279 */         this.curChar = this.input_stream.readChar();
/* 344:    */       }
/* 345:    */       catch (IOException localIOException) {}
/* 346:    */     }
/* 347:280 */     return paramInt2;
/* 348:    */   }
/* 349:    */   
/* 350:283 */   static final int[] jjnextStates = { 10, 11, 16, 17, 20, 21, 7, 8, 14, 15, 18, 19, 22, 23 };
/* 351:286 */   public static final String[] jjstrLiteralImages = { "", null, null, null, null, null, " ", "-", "+" };
/* 352:288 */   public static final String[] lexStateNames = { "DEFAULT" };
/* 353:    */   protected SimpleCharStream input_stream;
/* 354:292 */   private final int[] jjrounds = new int[24];
/* 355:293 */   private final int[] jjstateSet = new int[48];
/* 356:    */   protected char curChar;
/* 357:    */   
/* 358:    */   public NumberParserTokenManager(SimpleCharStream paramSimpleCharStream)
/* 359:    */   {
/* 360:298 */     this.input_stream = paramSimpleCharStream;
/* 361:    */   }
/* 362:    */   
/* 363:    */   public NumberParserTokenManager(SimpleCharStream paramSimpleCharStream, int paramInt)
/* 364:    */   {
/* 365:301 */     this(paramSimpleCharStream);
/* 366:302 */     SwitchTo(paramInt);
/* 367:    */   }
/* 368:    */   
/* 369:    */   public void ReInit(SimpleCharStream paramSimpleCharStream)
/* 370:    */   {
/* 371:306 */     this.jjmatchedPos = (this.jjnewStateCnt = 0);
/* 372:307 */     this.curLexState = this.defaultLexState;
/* 373:308 */     this.input_stream = paramSimpleCharStream;
/* 374:309 */     ReInitRounds();
/* 375:    */   }
/* 376:    */   
/* 377:    */   private final void ReInitRounds()
/* 378:    */   {
/* 379:314 */     this.jjround = -2147483647;
/* 380:315 */     for (int i = 24; i-- > 0;) {
/* 381:316 */       this.jjrounds[i] = -2147483648;
/* 382:    */     }
/* 383:    */   }
/* 384:    */   
/* 385:    */   public void ReInit(SimpleCharStream paramSimpleCharStream, int paramInt)
/* 386:    */   {
/* 387:320 */     ReInit(paramSimpleCharStream);
/* 388:321 */     SwitchTo(paramInt);
/* 389:    */   }
/* 390:    */   
/* 391:    */   public void SwitchTo(int paramInt)
/* 392:    */   {
/* 393:325 */     if ((paramInt >= 1) || (paramInt < 0)) {
/* 394:326 */       throw new TokenMgrError("Error: Ignoring invalid lexical state : " + paramInt + ". State unchanged.", 2);
/* 395:    */     }
/* 396:328 */     this.curLexState = paramInt;
/* 397:    */   }
/* 398:    */   
/* 399:    */   protected Token jjFillToken()
/* 400:    */   {
/* 401:333 */     Token localToken = Token.newToken(this.jjmatchedKind);
/* 402:334 */     localToken.kind = this.jjmatchedKind;
/* 403:335 */     String str = jjstrLiteralImages[this.jjmatchedKind];
/* 404:336 */     localToken.image = (str == null ? this.input_stream.GetImage() : str);
/* 405:337 */     localToken.beginLine = this.input_stream.getBeginLine();
/* 406:338 */     localToken.beginColumn = this.input_stream.getBeginColumn();
/* 407:339 */     localToken.endLine = this.input_stream.getEndLine();
/* 408:340 */     localToken.endColumn = this.input_stream.getEndColumn();
/* 409:341 */     return localToken;
/* 410:    */   }
/* 411:    */   
/* 412:344 */   int curLexState = 0;
/* 413:345 */   int defaultLexState = 0;
/* 414:    */   int jjnewStateCnt;
/* 415:    */   int jjround;
/* 416:    */   int jjmatchedPos;
/* 417:    */   int jjmatchedKind;
/* 418:    */   
/* 419:    */   public Token getNextToken()
/* 420:    */   {
/* 421:354 */     Object localObject = null;
/* 422:    */     
/* 423:356 */     int i = 0;
/* 424:    */     Token localToken;
/* 425:    */     try
/* 426:    */     {
/* 427:363 */       this.curChar = this.input_stream.BeginToken();
/* 428:    */     }
/* 429:    */     catch (IOException localIOException1)
/* 430:    */     {
/* 431:367 */       this.jjmatchedKind = 0;
/* 432:368 */       return jjFillToken();
/* 433:    */     }
/* 434:372 */     this.jjmatchedKind = 2147483647;
/* 435:373 */     this.jjmatchedPos = 0;
/* 436:374 */     i = jjMoveStringLiteralDfa0_0();
/* 437:375 */     if (this.jjmatchedKind != 2147483647)
/* 438:    */     {
/* 439:377 */       if (this.jjmatchedPos + 1 < i) {
/* 440:378 */         this.input_stream.backup(i - this.jjmatchedPos - 1);
/* 441:    */       }
/* 442:379 */       localToken = jjFillToken();
/* 443:380 */       return localToken;
/* 444:    */     }
/* 445:382 */     int j = this.input_stream.getEndLine();
/* 446:383 */     int k = this.input_stream.getEndColumn();
/* 447:384 */     String str = null;
/* 448:385 */     boolean bool = false;
/* 449:    */     try
/* 450:    */     {
/* 451:386 */       this.input_stream.readChar();this.input_stream.backup(1);
/* 452:    */     }
/* 453:    */     catch (IOException localIOException2)
/* 454:    */     {
/* 455:388 */       bool = true;
/* 456:389 */       str = i <= 1 ? "" : this.input_stream.GetImage();
/* 457:390 */       if ((this.curChar == '\n') || (this.curChar == '\r'))
/* 458:    */       {
/* 459:391 */         j++;
/* 460:392 */         k = 0;
/* 461:    */       }
/* 462:    */       else
/* 463:    */       {
/* 464:395 */         k++;
/* 465:    */       }
/* 466:    */     }
/* 467:397 */     if (!bool)
/* 468:    */     {
/* 469:398 */       this.input_stream.backup(1);
/* 470:399 */       str = i <= 1 ? "" : this.input_stream.GetImage();
/* 471:    */     }
/* 472:401 */     throw new TokenMgrError(bool, this.curLexState, j, k, str, this.curChar, 0);
/* 473:    */   }
/* 474:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.NumberParserTokenManager
 * JD-Core Version:    0.7.0.1
 */