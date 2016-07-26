/*   1:    */ package org.j_paine.formatter;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ 
/*   6:    */ public class FormatParserTokenManager
/*   7:    */   implements FormatParserConstants
/*   8:    */ {
/*   9:  6 */   public static PrintStream debugStream = System.out;
/*  10:    */   
/*  11:    */   public static void setDebugStream(PrintStream paramPrintStream)
/*  12:    */   {
/*  13:  7 */     debugStream = paramPrintStream;
/*  14:    */   }
/*  15:    */   
/*  16:    */   private static final int jjStopStringLiteralDfa_0(int paramInt, long paramLong)
/*  17:    */   {
/*  18: 10 */     switch (paramInt)
/*  19:    */     {
/*  20:    */     }
/*  21: 13 */     return -1;
/*  22:    */   }
/*  23:    */   
/*  24:    */   private static final int jjStartNfa_0(int paramInt, long paramLong)
/*  25:    */   {
/*  26: 18 */     return jjMoveNfa_0(jjStopStringLiteralDfa_0(paramInt, paramLong), paramInt + 1);
/*  27:    */   }
/*  28:    */   
/*  29:    */   private static final int jjStopAtPos(int paramInt1, int paramInt2)
/*  30:    */   {
/*  31: 22 */     jjmatchedKind = paramInt2;
/*  32: 23 */     jjmatchedPos = paramInt1;
/*  33: 24 */     return paramInt1 + 1;
/*  34:    */   }
/*  35:    */   
/*  36:    */   private static final int jjStartNfaWithStates_0(int paramInt1, int paramInt2, int paramInt3)
/*  37:    */   {
/*  38: 28 */     jjmatchedKind = paramInt2;
/*  39: 29 */     jjmatchedPos = paramInt1;
/*  40:    */     try
/*  41:    */     {
/*  42: 30 */       curChar = input_stream.readChar();
/*  43:    */     }
/*  44:    */     catch (IOException localIOException)
/*  45:    */     {
/*  46: 31 */       return paramInt1 + 1;
/*  47:    */     }
/*  48: 32 */     return jjMoveNfa_0(paramInt3, paramInt1 + 1);
/*  49:    */   }
/*  50:    */   
/*  51:    */   private static final int jjMoveStringLiteralDfa0_0()
/*  52:    */   {
/*  53: 36 */     switch (curChar)
/*  54:    */     {
/*  55:    */     case '(': 
/*  56: 39 */       return jjStopAtPos(0, 15);
/*  57:    */     case ')': 
/*  58: 41 */       return jjStopAtPos(0, 16);
/*  59:    */     case ',': 
/*  60: 43 */       return jjStopAtPos(0, 17);
/*  61:    */     case '.': 
/*  62: 45 */       return jjStopAtPos(0, 13);
/*  63:    */     case '/': 
/*  64: 47 */       return jjStopAtPos(0, 14);
/*  65:    */     }
/*  66: 49 */     return jjMoveNfa_0(2, 0);
/*  67:    */   }
/*  68:    */   
/*  69:    */   private static final void jjCheckNAdd(int paramInt)
/*  70:    */   {
/*  71: 54 */     if (jjrounds[paramInt] != jjround)
/*  72:    */     {
/*  73: 56 */       jjstateSet[(jjnewStateCnt++)] = paramInt;
/*  74: 57 */       jjrounds[paramInt] = jjround;
/*  75:    */     }
/*  76:    */   }
/*  77:    */   
/*  78:    */   private static final void jjAddStates(int paramInt1, int paramInt2)
/*  79:    */   {
/*  80:    */     do
/*  81:    */     {
/*  82: 63 */       jjstateSet[(jjnewStateCnt++)] = jjnextStates[paramInt1];
/*  83: 64 */     } while (paramInt1++ != paramInt2);
/*  84:    */   }
/*  85:    */   
/*  86:    */   private static final void jjCheckNAddTwoStates(int paramInt1, int paramInt2)
/*  87:    */   {
/*  88: 68 */     jjCheckNAdd(paramInt1);
/*  89: 69 */     jjCheckNAdd(paramInt2);
/*  90:    */   }
/*  91:    */   
/*  92:    */   private static final void jjCheckNAddStates(int paramInt1, int paramInt2)
/*  93:    */   {
/*  94:    */     do
/*  95:    */     {
/*  96: 74 */       jjCheckNAdd(jjnextStates[paramInt1]);
/*  97: 75 */     } while (paramInt1++ != paramInt2);
/*  98:    */   }
/*  99:    */   
/* 100:    */   private static final void jjCheckNAddStates(int paramInt)
/* 101:    */   {
/* 102: 79 */     jjCheckNAdd(jjnextStates[paramInt]);
/* 103: 80 */     jjCheckNAdd(jjnextStates[(paramInt + 1)]);
/* 104:    */   }
/* 105:    */   
/* 106: 82 */   static final long[] jjbitVec0 = { 0L, 0L, -1L, -1L };
/* 107:    */   
/* 108:    */   private static final int jjMoveNfa_0(int paramInt1, int paramInt2)
/* 109:    */   {
/* 110: 88 */     int i = 0;
/* 111: 89 */     jjnewStateCnt = 14;
/* 112: 90 */     int j = 1;
/* 113: 91 */     jjstateSet[0] = paramInt1;
/* 114: 92 */     int k = 2147483647;
/* 115:    */     for (;;)
/* 116:    */     {
/* 117: 95 */       if (++jjround == 2147483647) {
/* 118: 96 */         ReInitRounds();
/* 119:    */       }
/* 120:    */       long l1;
/* 121: 97 */       if (curChar < '@')
/* 122:    */       {
/* 123: 99 */         l1 = 1L << curChar;
/* 124:    */         do
/* 125:    */         {
/* 126:102 */           switch (jjstateSet[(--j)])
/* 127:    */           {
/* 128:    */           case 2: 
/* 129:105 */             if ((0x0 & l1) != 0L)
/* 130:    */             {
/* 131:107 */               if (k > 2) {
/* 132:108 */                 k = 2;
/* 133:    */               }
/* 134:109 */               jjCheckNAdd(1);
/* 135:    */             }
/* 136:111 */             else if (curChar == '\'')
/* 137:    */             {
/* 138:112 */               jjCheckNAddTwoStates(3, 4);
/* 139:    */             }
/* 140:113 */             else if (curChar == ' ')
/* 141:    */             {
/* 142:115 */               if (k > 1) {
/* 143:116 */                 k = 1;
/* 144:    */               }
/* 145:117 */               jjCheckNAdd(0);
/* 146:    */             }
/* 147:    */             break;
/* 148:    */           case 0: 
/* 149:121 */             if (curChar == ' ')
/* 150:    */             {
/* 151:123 */               if (k > 1) {
/* 152:124 */                 k = 1;
/* 153:    */               }
/* 154:125 */               jjCheckNAdd(0);
/* 155:    */             }
/* 156:126 */             break;
/* 157:    */           case 1: 
/* 158:128 */             if ((0x0 & l1) != 0L)
/* 159:    */             {
/* 160:130 */               if (k > 2) {
/* 161:131 */                 k = 2;
/* 162:    */               }
/* 163:132 */               jjCheckNAdd(1);
/* 164:    */             }
/* 165:133 */             break;
/* 166:    */           case 3: 
/* 167:135 */             if ((0xFFFFFFFF & l1) != 0L) {
/* 168:136 */               jjCheckNAddTwoStates(3, 4);
/* 169:    */             }
/* 170:    */             break;
/* 171:    */           case 4: 
/* 172:139 */             if ((curChar == '\'') && (k > 3)) {
/* 173:140 */               k = 3;
/* 174:    */             }
/* 175:    */             break;
/* 176:    */           }
/* 177:144 */         } while (j != i);
/* 178:    */       }
/* 179:146 */       else if (curChar < '')
/* 180:    */       {
/* 181:148 */         l1 = 1L << (curChar & 0x3F);
/* 182:    */         do
/* 183:    */         {
/* 184:151 */           switch (jjstateSet[(--j)])
/* 185:    */           {
/* 186:    */           case 2: 
/* 187:154 */             if ((0x1000 & l1) != 0L)
/* 188:    */             {
/* 189:156 */               if (k > 12) {
/* 190:157 */                 k = 12;
/* 191:    */               }
/* 192:    */             }
/* 193:159 */             else if ((0x80 & l1) != 0L)
/* 194:    */             {
/* 195:161 */               if (k > 11) {
/* 196:162 */                 k = 11;
/* 197:    */               }
/* 198:    */             }
/* 199:164 */             else if ((0x20 & l1) != 0L)
/* 200:    */             {
/* 201:166 */               if (k > 10) {
/* 202:167 */                 k = 10;
/* 203:    */               }
/* 204:    */             }
/* 205:169 */             else if ((0x10 & l1) != 0L)
/* 206:    */             {
/* 207:171 */               if (k > 9) {
/* 208:172 */                 k = 9;
/* 209:    */               }
/* 210:    */             }
/* 211:174 */             else if ((0x40 & l1) != 0L)
/* 212:    */             {
/* 213:176 */               if (k > 8) {
/* 214:177 */                 k = 8;
/* 215:    */               }
/* 216:    */             }
/* 217:179 */             else if ((0x200 & l1) != 0L)
/* 218:    */             {
/* 219:181 */               if (k > 7) {
/* 220:182 */                 k = 7;
/* 221:    */               }
/* 222:    */             }
/* 223:184 */             else if ((0x1000000 & l1) != 0L)
/* 224:    */             {
/* 225:186 */               if (k > 6) {
/* 226:187 */                 k = 6;
/* 227:    */               }
/* 228:    */             }
/* 229:189 */             else if ((0x10000 & l1) != 0L)
/* 230:    */             {
/* 231:191 */               if (k > 5) {
/* 232:192 */                 k = 5;
/* 233:    */               }
/* 234:    */             }
/* 235:194 */             else if ((0x2 & l1) != 0L) {
/* 236:196 */               if (k > 4) {
/* 237:197 */                 k = 4;
/* 238:    */               }
/* 239:    */             }
/* 240:    */             break;
/* 241:    */           case 3: 
/* 242:201 */             jjAddStates(0, 1);
/* 243:202 */             break;
/* 244:    */           case 5: 
/* 245:204 */             if (((0x2 & l1) != 0L) && (k > 4)) {
/* 246:205 */               k = 4;
/* 247:    */             }
/* 248:    */             break;
/* 249:    */           case 6: 
/* 250:208 */             if (((0x10000 & l1) != 0L) && (k > 5)) {
/* 251:209 */               k = 5;
/* 252:    */             }
/* 253:    */             break;
/* 254:    */           case 7: 
/* 255:212 */             if (((0x1000000 & l1) != 0L) && (k > 6)) {
/* 256:213 */               k = 6;
/* 257:    */             }
/* 258:    */             break;
/* 259:    */           case 8: 
/* 260:216 */             if (((0x200 & l1) != 0L) && (k > 7)) {
/* 261:217 */               k = 7;
/* 262:    */             }
/* 263:    */             break;
/* 264:    */           case 9: 
/* 265:220 */             if (((0x40 & l1) != 0L) && (k > 8)) {
/* 266:221 */               k = 8;
/* 267:    */             }
/* 268:    */             break;
/* 269:    */           case 10: 
/* 270:224 */             if (((0x10 & l1) != 0L) && (k > 9)) {
/* 271:225 */               k = 9;
/* 272:    */             }
/* 273:    */             break;
/* 274:    */           case 11: 
/* 275:228 */             if (((0x20 & l1) != 0L) && (k > 10)) {
/* 276:229 */               k = 10;
/* 277:    */             }
/* 278:    */             break;
/* 279:    */           case 12: 
/* 280:232 */             if (((0x80 & l1) != 0L) && (k > 11)) {
/* 281:233 */               k = 11;
/* 282:    */             }
/* 283:    */             break;
/* 284:    */           case 13: 
/* 285:236 */             if (((0x1000 & l1) != 0L) && (k > 12)) {
/* 286:237 */               k = 12;
/* 287:    */             }
/* 288:    */             break;
/* 289:    */           }
/* 290:241 */         } while (j != i);
/* 291:    */       }
/* 292:    */       else
/* 293:    */       {
/* 294:245 */         int m = (curChar & 0xFF) >> '\006';
/* 295:246 */         long l2 = 1L << (curChar & 0x3F);
/* 296:    */         do
/* 297:    */         {
/* 298:249 */           switch (jjstateSet[(--j)])
/* 299:    */           {
/* 300:    */           case 3: 
/* 301:252 */             if ((jjbitVec0[m] & l2) != 0L) {
/* 302:253 */               jjAddStates(0, 1);
/* 303:    */             }
/* 304:    */             break;
/* 305:    */           }
/* 306:257 */         } while (j != i);
/* 307:    */       }
/* 308:259 */       if (k != 2147483647)
/* 309:    */       {
/* 310:261 */         jjmatchedKind = k;
/* 311:262 */         jjmatchedPos = paramInt2;
/* 312:263 */         k = 2147483647;
/* 313:    */       }
/* 314:265 */       paramInt2++;
/* 315:266 */       if ((j = jjnewStateCnt) == (i = 14 - (FormatParserTokenManager.jjnewStateCnt = i))) {
/* 316:267 */         return paramInt2;
/* 317:    */       }
/* 318:    */       try
/* 319:    */       {
/* 320:268 */         curChar = input_stream.readChar();
/* 321:    */       }
/* 322:    */       catch (IOException localIOException) {}
/* 323:    */     }
/* 324:269 */     return paramInt2;
/* 325:    */   }
/* 326:    */   
/* 327:272 */   static final int[] jjnextStates = { 3, 4 };
/* 328:275 */   public static final String[] jjstrLiteralImages = { "", null, null, null, null, null, null, null, null, null, null, null, null, ".", "/", "(", ")", "," };
/* 329:278 */   public static final String[] lexStateNames = { "DEFAULT" };
/* 330:281 */   static final long[] jjtoToken = { 262141L };
/* 331:284 */   static final long[] jjtoSkip = { 2L };
/* 332:    */   protected static SimpleCharStream input_stream;
/* 333:288 */   private static final int[] jjrounds = new int[14];
/* 334:289 */   private static final int[] jjstateSet = new int[28];
/* 335:    */   protected static char curChar;
/* 336:    */   
/* 337:    */   public FormatParserTokenManager(SimpleCharStream paramSimpleCharStream)
/* 338:    */   {
/* 339:292 */     if (input_stream != null) {
/* 340:293 */       throw new TokenMgrError("ERROR: Second call to constructor of static lexer. You must use ReInit() to initialize the static variables.", 1);
/* 341:    */     }
/* 342:294 */     input_stream = paramSimpleCharStream;
/* 343:    */   }
/* 344:    */   
/* 345:    */   public FormatParserTokenManager(SimpleCharStream paramSimpleCharStream, int paramInt)
/* 346:    */   {
/* 347:297 */     this(paramSimpleCharStream);
/* 348:298 */     SwitchTo(paramInt);
/* 349:    */   }
/* 350:    */   
/* 351:    */   public static void ReInit(SimpleCharStream paramSimpleCharStream)
/* 352:    */   {
/* 353:302 */     jjmatchedPos = FormatParserTokenManager.jjnewStateCnt = 0;
/* 354:303 */     curLexState = defaultLexState;
/* 355:304 */     input_stream = paramSimpleCharStream;
/* 356:305 */     ReInitRounds();
/* 357:    */   }
/* 358:    */   
/* 359:    */   private static final void ReInitRounds()
/* 360:    */   {
/* 361:310 */     jjround = -2147483647;
/* 362:311 */     for (int i = 14; i-- > 0;) {
/* 363:312 */       jjrounds[i] = -2147483648;
/* 364:    */     }
/* 365:    */   }
/* 366:    */   
/* 367:    */   public static void ReInit(SimpleCharStream paramSimpleCharStream, int paramInt)
/* 368:    */   {
/* 369:316 */     ReInit(paramSimpleCharStream);
/* 370:317 */     SwitchTo(paramInt);
/* 371:    */   }
/* 372:    */   
/* 373:    */   public static void SwitchTo(int paramInt)
/* 374:    */   {
/* 375:321 */     if ((paramInt >= 1) || (paramInt < 0)) {
/* 376:322 */       throw new TokenMgrError("Error: Ignoring invalid lexical state : " + paramInt + ". State unchanged.", 2);
/* 377:    */     }
/* 378:324 */     curLexState = paramInt;
/* 379:    */   }
/* 380:    */   
/* 381:    */   protected static Token jjFillToken()
/* 382:    */   {
/* 383:329 */     Token localToken = Token.newToken(jjmatchedKind);
/* 384:330 */     localToken.kind = jjmatchedKind;
/* 385:331 */     String str = jjstrLiteralImages[jjmatchedKind];
/* 386:332 */     localToken.image = (str == null ? input_stream.GetImage() : str);
/* 387:333 */     localToken.beginLine = input_stream.getBeginLine();
/* 388:334 */     localToken.beginColumn = input_stream.getBeginColumn();
/* 389:335 */     localToken.endLine = input_stream.getEndLine();
/* 390:336 */     localToken.endColumn = input_stream.getEndColumn();
/* 391:337 */     return localToken;
/* 392:    */   }
/* 393:    */   
/* 394:340 */   static int curLexState = 0;
/* 395:341 */   static int defaultLexState = 0;
/* 396:    */   static int jjnewStateCnt;
/* 397:    */   static int jjround;
/* 398:    */   static int jjmatchedPos;
/* 399:    */   static int jjmatchedKind;
/* 400:    */   
/* 401:    */   public static Token getNextToken()
/* 402:    */   {
/* 403:350 */     Object localObject = null;
/* 404:    */     
/* 405:352 */     int i = 0;
/* 406:    */     do
/* 407:    */     {
/* 408:    */       try
/* 409:    */       {
/* 410:359 */         curChar = input_stream.BeginToken();
/* 411:    */       }
/* 412:    */       catch (IOException localIOException1)
/* 413:    */       {
/* 414:363 */         jjmatchedKind = 0;
/* 415:364 */         return jjFillToken();
/* 416:    */       }
/* 417:368 */       jjmatchedKind = 2147483647;
/* 418:369 */       jjmatchedPos = 0;
/* 419:370 */       i = jjMoveStringLiteralDfa0_0();
/* 420:371 */       if (jjmatchedKind == 2147483647) {
/* 421:    */         break;
/* 422:    */       }
/* 423:373 */       if (jjmatchedPos + 1 < i) {
/* 424:374 */         input_stream.backup(i - jjmatchedPos - 1);
/* 425:    */       }
/* 426:375 */     } while ((jjtoToken[(jjmatchedKind >> 6)] & 1L << (jjmatchedKind & 0x3F)) == 0L);
/* 427:377 */     Token localToken = jjFillToken();
/* 428:378 */     return localToken;
/* 429:    */     
/* 430:    */ 
/* 431:    */ 
/* 432:    */ 
/* 433:    */ 
/* 434:    */ 
/* 435:385 */     int j = input_stream.getEndLine();
/* 436:386 */     int k = input_stream.getEndColumn();
/* 437:387 */     String str = null;
/* 438:388 */     boolean bool = false;
/* 439:    */     try
/* 440:    */     {
/* 441:389 */       input_stream.readChar();input_stream.backup(1);
/* 442:    */     }
/* 443:    */     catch (IOException localIOException2)
/* 444:    */     {
/* 445:391 */       bool = true;
/* 446:392 */       str = i <= 1 ? "" : input_stream.GetImage();
/* 447:393 */       if ((curChar == '\n') || (curChar == '\r'))
/* 448:    */       {
/* 449:394 */         j++;
/* 450:395 */         k = 0;
/* 451:    */       }
/* 452:    */       else
/* 453:    */       {
/* 454:398 */         k++;
/* 455:    */       }
/* 456:    */     }
/* 457:400 */     if (!bool)
/* 458:    */     {
/* 459:401 */       input_stream.backup(1);
/* 460:402 */       str = i <= 1 ? "" : input_stream.GetImage();
/* 461:    */     }
/* 462:404 */     throw new TokenMgrError(bool, curLexState, j, k, str, curChar, 0);
/* 463:    */   }
/* 464:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.FormatParserTokenManager
 * JD-Core Version:    0.7.0.1
 */