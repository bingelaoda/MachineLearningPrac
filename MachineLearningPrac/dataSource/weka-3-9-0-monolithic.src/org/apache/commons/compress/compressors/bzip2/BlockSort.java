/*    1:     */ package org.apache.commons.compress.compressors.bzip2;
/*    2:     */ 
/*    3:     */ import java.util.BitSet;
/*    4:     */ 
/*    5:     */ class BlockSort
/*    6:     */ {
/*    7:     */   private static final int QSORT_STACK_SIZE = 1000;
/*    8:     */   private static final int FALLBACK_QSORT_STACK_SIZE = 100;
/*    9:     */   private static final int STACK_SIZE = 1000;
/*   10:     */   private int workDone;
/*   11:     */   private int workLimit;
/*   12:     */   private boolean firstAttempt;
/*   13: 132 */   private final int[] stack_ll = new int[1000];
/*   14: 133 */   private final int[] stack_hh = new int[1000];
/*   15: 134 */   private final int[] stack_dd = new int[1000];
/*   16: 136 */   private final int[] mainSort_runningOrder = new int[256];
/*   17: 137 */   private final int[] mainSort_copy = new int[256];
/*   18: 138 */   private final boolean[] mainSort_bigDone = new boolean[256];
/*   19: 140 */   private final int[] ftab = new int[65537];
/*   20:     */   private final char[] quadrant;
/*   21:     */   private static final int FALLBACK_QSORT_SMALL_THRESH = 10;
/*   22:     */   private int[] eclass;
/*   23:     */   
/*   24:     */   BlockSort(BZip2CompressorOutputStream.Data data)
/*   25:     */   {
/*   26: 150 */     this.quadrant = data.sfmap;
/*   27:     */   }
/*   28:     */   
/*   29:     */   void blockSort(BZip2CompressorOutputStream.Data data, int last)
/*   30:     */   {
/*   31: 154 */     this.workLimit = (30 * last);
/*   32: 155 */     this.workDone = 0;
/*   33: 156 */     this.firstAttempt = true;
/*   34: 158 */     if (last + 1 < 10000)
/*   35:     */     {
/*   36: 159 */       fallbackSort(data, last);
/*   37:     */     }
/*   38:     */     else
/*   39:     */     {
/*   40: 161 */       mainSort(data, last);
/*   41: 163 */       if ((this.firstAttempt) && (this.workDone > this.workLimit)) {
/*   42: 164 */         fallbackSort(data, last);
/*   43:     */       }
/*   44:     */     }
/*   45: 168 */     int[] fmap = data.fmap;
/*   46: 169 */     data.origPtr = -1;
/*   47: 170 */     for (int i = 0; i <= last; i++) {
/*   48: 171 */       if (fmap[i] == 0)
/*   49:     */       {
/*   50: 172 */         data.origPtr = i;
/*   51: 173 */         break;
/*   52:     */       }
/*   53:     */     }
/*   54:     */   }
/*   55:     */   
/*   56:     */   final void fallbackSort(BZip2CompressorOutputStream.Data data, int last)
/*   57:     */   {
/*   58: 187 */     data.block[0] = data.block[(last + 1)];
/*   59: 188 */     fallbackSort(data.fmap, data.block, last + 1);
/*   60: 189 */     for (int i = 0; i < last + 1; i++) {
/*   61: 190 */       data.fmap[i] -= 1;
/*   62:     */     }
/*   63: 192 */     for (int i = 0; i < last + 1; i++) {
/*   64: 193 */       if (data.fmap[i] == -1)
/*   65:     */       {
/*   66: 194 */         data.fmap[i] = last;
/*   67: 195 */         break;
/*   68:     */       }
/*   69:     */     }
/*   70:     */   }
/*   71:     */   
/*   72:     */   private void fallbackSimpleSort(int[] fmap, int[] eclass, int lo, int hi)
/*   73:     */   {
/*   74: 271 */     if (lo == hi) {
/*   75: 272 */       return;
/*   76:     */     }
/*   77: 276 */     if (hi - lo > 3) {
/*   78: 277 */       for (int i = hi - 4; i >= lo; i--)
/*   79:     */       {
/*   80: 278 */         int tmp = fmap[i];
/*   81: 279 */         int ec_tmp = eclass[tmp];
/*   82: 280 */         for (int j = i + 4; (j <= hi) && (ec_tmp > eclass[fmap[j]]); j += 4) {
/*   83: 282 */           fmap[(j - 4)] = fmap[j];
/*   84:     */         }
/*   85: 284 */         fmap[(j - 4)] = tmp;
/*   86:     */       }
/*   87:     */     }
/*   88: 288 */     for (int i = hi - 1; i >= lo; i--)
/*   89:     */     {
/*   90: 289 */       int tmp = fmap[i];
/*   91: 290 */       int ec_tmp = eclass[tmp];
/*   92: 291 */       for (int j = i + 1; (j <= hi) && (ec_tmp > eclass[fmap[j]]); j++) {
/*   93: 292 */         fmap[(j - 1)] = fmap[j];
/*   94:     */       }
/*   95: 294 */       fmap[(j - 1)] = tmp;
/*   96:     */     }
/*   97:     */   }
/*   98:     */   
/*   99:     */   private void fswap(int[] fmap, int zz1, int zz2)
/*  100:     */   {
/*  101: 304 */     int zztmp = fmap[zz1];
/*  102: 305 */     fmap[zz1] = fmap[zz2];
/*  103: 306 */     fmap[zz2] = zztmp;
/*  104:     */   }
/*  105:     */   
/*  106:     */   private void fvswap(int[] fmap, int yyp1, int yyp2, int yyn)
/*  107:     */   {
/*  108: 313 */     while (yyn > 0)
/*  109:     */     {
/*  110: 314 */       fswap(fmap, yyp1, yyp2);
/*  111: 315 */       yyp1++;yyp2++;yyn--;
/*  112:     */     }
/*  113:     */   }
/*  114:     */   
/*  115:     */   private int fmin(int a, int b)
/*  116:     */   {
/*  117: 320 */     return a < b ? a : b;
/*  118:     */   }
/*  119:     */   
/*  120:     */   private void fpush(int sp, int lz, int hz)
/*  121:     */   {
/*  122: 324 */     this.stack_ll[sp] = lz;
/*  123: 325 */     this.stack_hh[sp] = hz;
/*  124:     */   }
/*  125:     */   
/*  126:     */   private int[] fpop(int sp)
/*  127:     */   {
/*  128: 329 */     return new int[] { this.stack_ll[sp], this.stack_hh[sp] };
/*  129:     */   }
/*  130:     */   
/*  131:     */   private void fallbackQSort3(int[] fmap, int[] eclass, int loSt, int hiSt)
/*  132:     */   {
/*  133: 348 */     long r = 0L;
/*  134: 349 */     int sp = 0;
/*  135: 350 */     fpush(sp++, loSt, hiSt);
/*  136: 352 */     while (sp > 0)
/*  137:     */     {
/*  138: 353 */       int[] s = fpop(--sp);
/*  139: 354 */       int lo = s[0];int hi = s[1];
/*  140: 356 */       if (hi - lo < 10)
/*  141:     */       {
/*  142: 357 */         fallbackSimpleSort(fmap, eclass, lo, hi);
/*  143:     */       }
/*  144:     */       else
/*  145:     */       {
/*  146: 368 */         r = (r * 7621L + 1L) % 32768L;
/*  147: 369 */         long r3 = r % 3L;
/*  148:     */         long med;
/*  149:     */         long med;
/*  150: 370 */         if (r3 == 0L)
/*  151:     */         {
/*  152: 371 */           med = eclass[fmap[lo]];
/*  153:     */         }
/*  154:     */         else
/*  155:     */         {
/*  156:     */           long med;
/*  157: 372 */           if (r3 == 1L) {
/*  158: 373 */             med = eclass[fmap[(lo + hi >>> 1)]];
/*  159:     */           } else {
/*  160: 375 */             med = eclass[fmap[hi]];
/*  161:     */           }
/*  162:     */         }
/*  163:     */         int ltLo;
/*  164: 378 */         int unLo = ltLo = lo;
/*  165:     */         int gtHi;
/*  166: 379 */         int unHi = gtHi = hi;
/*  167:     */         for (;;)
/*  168:     */         {
/*  169: 385 */           if (unLo <= unHi)
/*  170:     */           {
/*  171: 388 */             int n = eclass[fmap[unLo]] - (int)med;
/*  172: 389 */             if (n == 0)
/*  173:     */             {
/*  174: 390 */               fswap(fmap, unLo, ltLo);
/*  175: 391 */               ltLo++;unLo++;
/*  176: 392 */               continue;
/*  177:     */             }
/*  178: 394 */             if (n <= 0)
/*  179:     */             {
/*  180: 397 */               unLo++; continue;
/*  181:     */             }
/*  182:     */           }
/*  183: 400 */           while (unLo <= unHi)
/*  184:     */           {
/*  185: 403 */             int n = eclass[fmap[unHi]] - (int)med;
/*  186: 404 */             if (n == 0)
/*  187:     */             {
/*  188: 405 */               fswap(fmap, unHi, gtHi);
/*  189: 406 */               gtHi--;unHi--;
/*  190:     */             }
/*  191:     */             else
/*  192:     */             {
/*  193: 409 */               if (n < 0) {
/*  194:     */                 break;
/*  195:     */               }
/*  196: 412 */               unHi--;
/*  197:     */             }
/*  198:     */           }
/*  199: 414 */           if (unLo > unHi) {
/*  200:     */             break;
/*  201:     */           }
/*  202: 417 */           fswap(fmap, unLo, unHi);unLo++;unHi--;
/*  203:     */         }
/*  204: 420 */         if (gtHi >= ltLo)
/*  205:     */         {
/*  206: 424 */           int n = fmin(ltLo - lo, unLo - ltLo);
/*  207: 425 */           fvswap(fmap, lo, unLo - n, n);
/*  208: 426 */           int m = fmin(hi - gtHi, gtHi - unHi);
/*  209: 427 */           fvswap(fmap, unHi + 1, hi - m + 1, m);
/*  210:     */           
/*  211: 429 */           n = lo + unLo - ltLo - 1;
/*  212: 430 */           m = hi - (gtHi - unHi) + 1;
/*  213: 432 */           if (n - lo > hi - m)
/*  214:     */           {
/*  215: 433 */             fpush(sp++, lo, n);
/*  216: 434 */             fpush(sp++, m, hi);
/*  217:     */           }
/*  218:     */           else
/*  219:     */           {
/*  220: 436 */             fpush(sp++, m, hi);
/*  221: 437 */             fpush(sp++, lo, n);
/*  222:     */           }
/*  223:     */         }
/*  224:     */       }
/*  225:     */     }
/*  226:     */   }
/*  227:     */   
/*  228:     */   private int[] getEclass()
/*  229:     */   {
/*  230: 448 */     return this.eclass == null ? (this.eclass = new int[this.quadrant.length / 2]) : this.eclass;
/*  231:     */   }
/*  232:     */   
/*  233:     */   final void fallbackSort(int[] fmap, byte[] block, int nblock)
/*  234:     */   {
/*  235: 471 */     int[] ftab = new int[257];
/*  236:     */     
/*  237:     */ 
/*  238:     */ 
/*  239: 475 */     int[] eclass = getEclass();
/*  240: 477 */     for (int i = 0; i < nblock; i++) {
/*  241: 478 */       eclass[i] = 0;
/*  242:     */     }
/*  243: 484 */     for (i = 0; i < nblock; i++) {
/*  244: 485 */       ftab[(block[i] & 0xFF)] += 1;
/*  245:     */     }
/*  246: 487 */     for (i = 1; i < 257; i++) {
/*  247: 488 */       ftab[i] += ftab[(i - 1)];
/*  248:     */     }
/*  249: 491 */     for (i = 0; i < nblock; i++)
/*  250:     */     {
/*  251: 492 */       int j = block[i] & 0xFF;
/*  252: 493 */       int k = ftab[j] - 1;
/*  253: 494 */       ftab[j] = k;
/*  254: 495 */       fmap[k] = i;
/*  255:     */     }
/*  256: 498 */     int nBhtab = 64 + nblock;
/*  257: 499 */     BitSet bhtab = new BitSet(nBhtab);
/*  258: 500 */     for (i = 0; i < 256; i++) {
/*  259: 501 */       bhtab.set(ftab[i]);
/*  260:     */     }
/*  261: 511 */     for (i = 0; i < 32; i++)
/*  262:     */     {
/*  263: 512 */       bhtab.set(nblock + 2 * i);
/*  264: 513 */       bhtab.clear(nblock + 2 * i + 1);
/*  265:     */     }
/*  266: 517 */     int H = 1;
/*  267:     */     for (;;)
/*  268:     */     {
/*  269: 520 */       int j = 0;
/*  270: 521 */       for (i = 0; i < nblock; i++)
/*  271:     */       {
/*  272: 522 */         if (bhtab.get(i)) {
/*  273: 523 */           j = i;
/*  274:     */         }
/*  275: 525 */         int k = fmap[i] - H;
/*  276: 526 */         if (k < 0) {
/*  277: 527 */           k += nblock;
/*  278:     */         }
/*  279: 529 */         eclass[k] = j;
/*  280:     */       }
/*  281: 532 */       int nNotDone = 0;
/*  282: 533 */       int r = -1;
/*  283:     */       for (;;)
/*  284:     */       {
/*  285: 537 */         int k = r + 1;
/*  286: 538 */         k = bhtab.nextClearBit(k);
/*  287: 539 */         int l = k - 1;
/*  288: 540 */         if (l >= nblock) {
/*  289:     */           break;
/*  290:     */         }
/*  291: 543 */         k = bhtab.nextSetBit(k + 1);
/*  292: 544 */         r = k - 1;
/*  293: 545 */         if (r >= nblock) {
/*  294:     */           break;
/*  295:     */         }
/*  296: 550 */         if (r > l)
/*  297:     */         {
/*  298: 551 */           nNotDone += r - l + 1;
/*  299: 552 */           fallbackQSort3(fmap, eclass, l, r);
/*  300:     */           
/*  301:     */ 
/*  302: 555 */           int cc = -1;
/*  303: 556 */           for (i = l; i <= r; i++)
/*  304:     */           {
/*  305: 557 */             int cc1 = eclass[fmap[i]];
/*  306: 558 */             if (cc != cc1)
/*  307:     */             {
/*  308: 559 */               bhtab.set(i);
/*  309: 560 */               cc = cc1;
/*  310:     */             }
/*  311:     */           }
/*  312:     */         }
/*  313:     */       }
/*  314: 566 */       H *= 2;
/*  315: 567 */       if (H <= nblock) {
/*  316: 567 */         if (nNotDone == 0) {
/*  317:     */           break;
/*  318:     */         }
/*  319:     */       }
/*  320:     */     }
/*  321:     */   }
/*  322:     */   
/*  323: 580 */   private static final int[] INCS = { 1, 4, 13, 40, 121, 364, 1093, 3280, 9841, 29524, 88573, 265720, 797161, 2391484 };
/*  324:     */   private static final int SMALL_THRESH = 20;
/*  325:     */   private static final int DEPTH_THRESH = 10;
/*  326:     */   private static final int WORK_FACTOR = 30;
/*  327:     */   private static final int SETMASK = 2097152;
/*  328:     */   private static final int CLEARMASK = -2097153;
/*  329:     */   
/*  330:     */   private boolean mainSimpleSort(BZip2CompressorOutputStream.Data dataShadow, int lo, int hi, int d, int lastShadow)
/*  331:     */   {
/*  332: 597 */     int bigN = hi - lo + 1;
/*  333: 598 */     if (bigN < 2) {
/*  334: 599 */       return (this.firstAttempt) && (this.workDone > this.workLimit);
/*  335:     */     }
/*  336: 602 */     int hp = 0;
/*  337: 603 */     while (INCS[hp] < bigN) {
/*  338: 604 */       hp++;
/*  339:     */     }
/*  340: 607 */     int[] fmap = dataShadow.fmap;
/*  341: 608 */     char[] quadrant = this.quadrant;
/*  342: 609 */     byte[] block = dataShadow.block;
/*  343: 610 */     int lastPlus1 = lastShadow + 1;
/*  344: 611 */     boolean firstAttemptShadow = this.firstAttempt;
/*  345: 612 */     int workLimitShadow = this.workLimit;
/*  346: 613 */     int workDoneShadow = this.workDone;
/*  347:     */     int h;
/*  348:     */     int mj;
/*  349:     */     int i;
/*  350:     */     label536:
/*  351:     */     label564:
/*  352:     */     label584:
/*  353:     */     label612:
/*  354:     */     label632:
/*  355:     */     for (;;)
/*  356:     */     {
/*  357: 618 */       hp--;
/*  358: 618 */       if (hp < 0) {
/*  359:     */         break;
/*  360:     */       }
/*  361: 619 */       h = INCS[hp];
/*  362: 620 */       mj = lo + h - 1;
/*  363: 622 */       for (i = lo + h; i <= hi;)
/*  364:     */       {
/*  365: 624 */         for (int k = 3; i <= hi; i++)
/*  366:     */         {
/*  367: 624 */           k--;
/*  368: 624 */           if (k < 0) {
/*  369:     */             break;
/*  370:     */           }
/*  371: 625 */           int v = fmap[i];
/*  372: 626 */           int vd = v + d;
/*  373: 627 */           int j = i;
/*  374:     */           
/*  375:     */ 
/*  376:     */ 
/*  377:     */ 
/*  378:     */ 
/*  379:     */ 
/*  380:     */ 
/*  381:     */ 
/*  382:     */ 
/*  383:     */ 
/*  384:     */ 
/*  385: 639 */           boolean onceRunned = false;
/*  386: 640 */           int a = 0;
/*  387:     */           int i1;
/*  388:     */           int i2;
/*  389:     */           do
/*  390:     */           {
/*  391:     */             for (;;)
/*  392:     */             {
/*  393: 643 */               if (onceRunned)
/*  394:     */               {
/*  395: 644 */                 fmap[j] = a;
/*  396: 645 */                 if (j -= h <= mj) {
/*  397:     */                   break label868;
/*  398:     */                 }
/*  399:     */               }
/*  400:     */               else
/*  401:     */               {
/*  402: 649 */                 onceRunned = true;
/*  403:     */               }
/*  404: 652 */               a = fmap[(j - h)];
/*  405: 653 */               i1 = a + d;
/*  406: 654 */               i2 = vd;
/*  407: 658 */               if (block[(i1 + 1)] != block[(i2 + 1)]) {
/*  408:     */                 break;
/*  409:     */               }
/*  410: 659 */               if (block[(i1 + 2)] == block[(i2 + 2)])
/*  411:     */               {
/*  412: 660 */                 if (block[(i1 + 3)] == block[(i2 + 3)])
/*  413:     */                 {
/*  414: 661 */                   if (block[(i1 + 4)] == block[(i2 + 4)])
/*  415:     */                   {
/*  416: 662 */                     if (block[(i1 + 5)] == block[(i2 + 5)])
/*  417:     */                     {
/*  418: 663 */                       i1 += 6;i2 += 6;
/*  419: 663 */                       if (block[i1] == block[i2])
/*  420:     */                       {
/*  421: 664 */                         int x = lastShadow;
/*  422:     */                         for (;;)
/*  423:     */                         {
/*  424: 665 */                           if (x <= 0) {
/*  425:     */                             break label868;
/*  426:     */                           }
/*  427: 666 */                           x -= 4;
/*  428: 668 */                           if (block[(i1 + 1)] != block[(i2 + 1)]) {
/*  429:     */                             break label676;
/*  430:     */                           }
/*  431: 669 */                           if (quadrant[i1] != quadrant[i2]) {
/*  432:     */                             break label660;
/*  433:     */                           }
/*  434: 670 */                           if (block[(i1 + 2)] != block[(i2 + 2)]) {
/*  435:     */                             break label632;
/*  436:     */                           }
/*  437: 671 */                           if (quadrant[(i1 + 1)] != quadrant[(i2 + 1)]) {
/*  438:     */                             break label612;
/*  439:     */                           }
/*  440: 672 */                           if (block[(i1 + 3)] != block[(i2 + 3)]) {
/*  441:     */                             break label584;
/*  442:     */                           }
/*  443: 673 */                           if (quadrant[(i1 + 2)] != quadrant[(i2 + 2)]) {
/*  444:     */                             break label564;
/*  445:     */                           }
/*  446: 674 */                           if (block[(i1 + 4)] != block[(i2 + 4)]) {
/*  447:     */                             break label536;
/*  448:     */                           }
/*  449: 675 */                           if (quadrant[(i1 + 3)] != quadrant[(i2 + 3)]) {
/*  450:     */                             break;
/*  451:     */                           }
/*  452: 676 */                           i1 += 4;
/*  453: 676 */                           if (i1 >= lastPlus1) {
/*  454: 677 */                             i1 -= lastPlus1;
/*  455:     */                           }
/*  456: 679 */                           i2 += 4;
/*  457: 679 */                           if (i2 >= lastPlus1) {
/*  458: 680 */                             i2 -= lastPlus1;
/*  459:     */                           }
/*  460: 682 */                           workDoneShadow++;
/*  461:     */                         }
/*  462: 684 */                         if (quadrant[(i1 + 3)] <= quadrant[(i2 + 3)]) {
/*  463:     */                           break label868;
/*  464:     */                         }
/*  465: 685 */                         continue;
/*  466: 689 */                         if ((block[(i1 + 4)] & 0xFF) <= (block[(i2 + 4)] & 0xFF)) {
/*  467:     */                           break label868;
/*  468:     */                         }
/*  469: 690 */                         continue;
/*  470: 694 */                         if (quadrant[(i1 + 2)] <= quadrant[(i2 + 2)]) {
/*  471:     */                           break label868;
/*  472:     */                         }
/*  473: 695 */                         continue;
/*  474: 699 */                         if ((block[(i1 + 3)] & 0xFF) <= (block[(i2 + 3)] & 0xFF)) {
/*  475:     */                           break label868;
/*  476:     */                         }
/*  477: 700 */                         continue;
/*  478: 704 */                         if (quadrant[(i1 + 1)] <= quadrant[(i2 + 1)]) {
/*  479:     */                           break label868;
/*  480:     */                         }
/*  481: 705 */                         continue;
/*  482: 709 */                         if ((block[(i1 + 2)] & 0xFF) <= (block[(i2 + 2)] & 0xFF)) {
/*  483:     */                           break label868;
/*  484:     */                         }
/*  485: 710 */                         continue;
/*  486: 714 */                         if (quadrant[i1] <= quadrant[i2]) {
/*  487:     */                           break label868;
/*  488:     */                         }
/*  489: 715 */                         continue;
/*  490: 719 */                         if ((block[(i1 + 1)] & 0xFF) <= (block[(i2 + 1)] & 0xFF)) {
/*  491:     */                           break label868;
/*  492:     */                         }
/*  493:     */                       }
/*  494:     */                       else
/*  495:     */                       {
/*  496: 729 */                         if ((block[i1] & 0xFF) <= (block[i2] & 0xFF)) {
/*  497:     */                           break label868;
/*  498:     */                         }
/*  499:     */                       }
/*  500:     */                     }
/*  501:     */                     else
/*  502:     */                     {
/*  503: 735 */                       if ((block[(i1 + 5)] & 0xFF) <= (block[(i2 + 5)] & 0xFF)) {
/*  504:     */                         break label868;
/*  505:     */                       }
/*  506:     */                     }
/*  507:     */                   }
/*  508:     */                   else {
/*  509: 740 */                     if ((block[(i1 + 4)] & 0xFF) <= (block[(i2 + 4)] & 0xFF)) {
/*  510:     */                       break label868;
/*  511:     */                     }
/*  512:     */                   }
/*  513:     */                 }
/*  514:     */                 else {
/*  515: 745 */                   if ((block[(i1 + 3)] & 0xFF) <= (block[(i2 + 3)] & 0xFF)) {
/*  516:     */                     break label868;
/*  517:     */                   }
/*  518:     */                 }
/*  519:     */               }
/*  520:     */               else {
/*  521: 750 */                 if ((block[(i1 + 2)] & 0xFF) <= (block[(i2 + 2)] & 0xFF)) {
/*  522:     */                   break label868;
/*  523:     */                 }
/*  524:     */               }
/*  525:     */             }
/*  526: 755 */           } while ((block[(i1 + 1)] & 0xFF) > (block[(i2 + 1)] & 0xFF));
/*  527: 764 */           fmap[j] = v;
/*  528:     */         }
/*  529: 767 */         if ((firstAttemptShadow) && (i <= hi) && (workDoneShadow > workLimitShadow)) {
/*  530:     */           break label905;
/*  531:     */         }
/*  532:     */       }
/*  533:     */     }
/*  534:     */     label660:
/*  535:     */     label676:
/*  536:     */     label868:
/*  537: 774 */     this.workDone = workDoneShadow;
/*  538:     */     label905:
/*  539: 775 */     return (firstAttemptShadow) && (workDoneShadow > workLimitShadow);
/*  540:     */   }
/*  541:     */   
/*  542:     */   private static void vswap(int[] fmap, int p1, int p2, int n)
/*  543:     */   {
/*  544: 787 */     n += p1;
/*  545: 788 */     while (p1 < n)
/*  546:     */     {
/*  547: 789 */       int t = fmap[p1];
/*  548: 790 */       fmap[(p1++)] = fmap[p2];
/*  549: 791 */       fmap[(p2++)] = t;
/*  550:     */     }
/*  551:     */   }
/*  552:     */   
/*  553:     */   private static byte med3(byte a, byte b, byte c)
/*  554:     */   {
/*  555: 796 */     return a > c ? c : b > c ? b : a < b ? a : a < c ? c : b < c ? b : a;
/*  556:     */   }
/*  557:     */   
/*  558:     */   private void mainQSort3(BZip2CompressorOutputStream.Data dataShadow, int loSt, int hiSt, int dSt, int last)
/*  559:     */   {
/*  560: 810 */     int[] stack_ll = this.stack_ll;
/*  561: 811 */     int[] stack_hh = this.stack_hh;
/*  562: 812 */     int[] stack_dd = this.stack_dd;
/*  563: 813 */     int[] fmap = dataShadow.fmap;
/*  564: 814 */     byte[] block = dataShadow.block;
/*  565:     */     
/*  566: 816 */     stack_ll[0] = loSt;
/*  567: 817 */     stack_hh[0] = hiSt;
/*  568: 818 */     stack_dd[0] = dSt;
/*  569:     */     
/*  570: 820 */     int sp = 1;
/*  571:     */     for (;;)
/*  572:     */     {
/*  573: 820 */       sp--;
/*  574: 820 */       if (sp < 0) {
/*  575:     */         break;
/*  576:     */       }
/*  577: 821 */       int lo = stack_ll[sp];
/*  578: 822 */       int hi = stack_hh[sp];
/*  579: 823 */       int d = stack_dd[sp];
/*  580: 825 */       if ((hi - lo < 20) || (d > 10))
/*  581:     */       {
/*  582: 826 */         if (!mainSimpleSort(dataShadow, lo, hi, d, last)) {}
/*  583:     */       }
/*  584:     */       else
/*  585:     */       {
/*  586: 830 */         int d1 = d + 1;
/*  587: 831 */         int med = med3(block[(fmap[lo] + d1)], block[(fmap[hi] + d1)], block[(fmap[(lo + hi >>> 1)] + d1)]) & 0xFF;
/*  588:     */         
/*  589:     */ 
/*  590: 834 */         int unLo = lo;
/*  591: 835 */         int unHi = hi;
/*  592: 836 */         int ltLo = lo;
/*  593: 837 */         int gtHi = hi;
/*  594:     */         for (;;)
/*  595:     */         {
/*  596: 840 */           if (unLo <= unHi)
/*  597:     */           {
/*  598: 841 */             int n = (block[(fmap[unLo] + d1)] & 0xFF) - med;
/*  599: 843 */             if (n == 0)
/*  600:     */             {
/*  601: 844 */               int temp = fmap[unLo];
/*  602: 845 */               fmap[(unLo++)] = fmap[ltLo];
/*  603: 846 */               fmap[(ltLo++)] = temp;
/*  604:     */             }
/*  605:     */             else
/*  606:     */             {
/*  607: 847 */               if (n >= 0) {
/*  608:     */                 break label257;
/*  609:     */               }
/*  610: 848 */               unLo++;
/*  611:     */             }
/*  612: 852 */             continue;
/*  613:     */           }
/*  614:     */           label257:
/*  615: 854 */           while (unLo <= unHi)
/*  616:     */           {
/*  617: 855 */             int n = (block[(fmap[unHi] + d1)] & 0xFF) - med;
/*  618: 857 */             if (n == 0)
/*  619:     */             {
/*  620: 858 */               int temp = fmap[unHi];
/*  621: 859 */               fmap[(unHi--)] = fmap[gtHi];
/*  622: 860 */               fmap[(gtHi--)] = temp;
/*  623:     */             }
/*  624:     */             else
/*  625:     */             {
/*  626: 861 */               if (n <= 0) {
/*  627:     */                 break;
/*  628:     */               }
/*  629: 862 */               unHi--;
/*  630:     */             }
/*  631:     */           }
/*  632: 868 */           if (unLo > unHi) {
/*  633:     */             break;
/*  634:     */           }
/*  635: 869 */           int temp = fmap[unLo];
/*  636: 870 */           fmap[(unLo++)] = fmap[unHi];
/*  637: 871 */           fmap[(unHi--)] = temp;
/*  638:     */         }
/*  639: 877 */         if (gtHi < ltLo)
/*  640:     */         {
/*  641: 878 */           stack_ll[sp] = lo;
/*  642: 879 */           stack_hh[sp] = hi;
/*  643: 880 */           stack_dd[sp] = d1;
/*  644: 881 */           sp++;
/*  645:     */         }
/*  646:     */         else
/*  647:     */         {
/*  648: 883 */           int n = ltLo - lo < unLo - ltLo ? ltLo - lo : unLo - ltLo;
/*  649:     */           
/*  650: 885 */           vswap(fmap, lo, unLo - n, n);
/*  651: 886 */           int m = hi - gtHi < gtHi - unHi ? hi - gtHi : gtHi - unHi;
/*  652:     */           
/*  653: 888 */           vswap(fmap, unLo, hi - m + 1, m);
/*  654:     */           
/*  655: 890 */           n = lo + unLo - ltLo - 1;
/*  656: 891 */           m = hi - (gtHi - unHi) + 1;
/*  657:     */           
/*  658: 893 */           stack_ll[sp] = lo;
/*  659: 894 */           stack_hh[sp] = n;
/*  660: 895 */           stack_dd[sp] = d;
/*  661: 896 */           sp++;
/*  662:     */           
/*  663: 898 */           stack_ll[sp] = (n + 1);
/*  664: 899 */           stack_hh[sp] = (m - 1);
/*  665: 900 */           stack_dd[sp] = d1;
/*  666: 901 */           sp++;
/*  667:     */           
/*  668: 903 */           stack_ll[sp] = m;
/*  669: 904 */           stack_hh[sp] = hi;
/*  670: 905 */           stack_dd[sp] = d;
/*  671: 906 */           sp++;
/*  672:     */         }
/*  673:     */       }
/*  674:     */     }
/*  675:     */   }
/*  676:     */   
/*  677:     */   final void mainSort(BZip2CompressorOutputStream.Data dataShadow, int lastShadow)
/*  678:     */   {
/*  679: 917 */     int[] runningOrder = this.mainSort_runningOrder;
/*  680: 918 */     int[] copy = this.mainSort_copy;
/*  681: 919 */     boolean[] bigDone = this.mainSort_bigDone;
/*  682: 920 */     int[] ftab = this.ftab;
/*  683: 921 */     byte[] block = dataShadow.block;
/*  684: 922 */     int[] fmap = dataShadow.fmap;
/*  685: 923 */     char[] quadrant = this.quadrant;
/*  686: 924 */     int workLimitShadow = this.workLimit;
/*  687: 925 */     boolean firstAttemptShadow = this.firstAttempt;
/*  688:     */     
/*  689:     */ 
/*  690: 928 */     int i = 65537;
/*  691:     */     for (;;)
/*  692:     */     {
/*  693: 928 */       i--;
/*  694: 928 */       if (i < 0) {
/*  695:     */         break;
/*  696:     */       }
/*  697: 929 */       ftab[i] = 0;
/*  698:     */     }
/*  699: 937 */     for (int i = 0; i < 20; i++) {
/*  700: 938 */       block[(lastShadow + i + 2)] = block[(i % (lastShadow + 1) + 1)];
/*  701:     */     }
/*  702: 940 */     int i = lastShadow + 20 + 1;
/*  703:     */     for (;;)
/*  704:     */     {
/*  705: 940 */       i--;
/*  706: 940 */       if (i < 0) {
/*  707:     */         break;
/*  708:     */       }
/*  709: 941 */       quadrant[i] = '\000';
/*  710:     */     }
/*  711: 943 */     block[0] = block[(lastShadow + 1)];
/*  712:     */     
/*  713:     */ 
/*  714:     */ 
/*  715: 947 */     int c1 = block[0] & 0xFF;
/*  716: 948 */     for (int i = 0; i <= lastShadow; i++)
/*  717:     */     {
/*  718: 949 */       int c2 = block[(i + 1)] & 0xFF;
/*  719: 950 */       ftab[((c1 << 8) + c2)] += 1;
/*  720: 951 */       c1 = c2;
/*  721:     */     }
/*  722: 954 */     for (int i = 1; i <= 65536; i++) {
/*  723: 955 */       ftab[i] += ftab[(i - 1)];
/*  724:     */     }
/*  725: 958 */     c1 = block[1] & 0xFF;
/*  726: 959 */     for (int i = 0; i < lastShadow; i++)
/*  727:     */     {
/*  728: 960 */       int c2 = block[(i + 2)] & 0xFF; int 
/*  729: 961 */         tmp277_276 = ((c1 << 8) + c2); int[] tmp277_267 = ftab; int tmp281_280 = (tmp277_267[tmp277_276] - 1);tmp277_267[tmp277_276] = tmp281_280;fmap[tmp281_280] = i;
/*  730: 962 */       c1 = c2;
/*  731:     */     }
/*  732: 965 */     int tmp322_321 = (((block[(lastShadow + 1)] & 0xFF) << 8) + (block[1] & 0xFF)); int[] tmp322_298 = ftab; int tmp326_325 = (tmp322_298[tmp322_321] - 1);tmp322_298[tmp322_321] = tmp326_325;fmap[tmp326_325] = lastShadow;
/*  733:     */     
/*  734:     */ 
/*  735:     */ 
/*  736:     */ 
/*  737:     */ 
/*  738: 971 */     int i = 256;
/*  739:     */     for (;;)
/*  740:     */     {
/*  741: 971 */       i--;
/*  742: 971 */       if (i < 0) {
/*  743:     */         break;
/*  744:     */       }
/*  745: 972 */       bigDone[i] = false;
/*  746: 973 */       runningOrder[i] = i;
/*  747:     */     }
/*  748: 976 */     for (int h = 364; h != 1;)
/*  749:     */     {
/*  750: 977 */       h /= 3;
/*  751: 978 */       for (int i = h; i <= 255; i++)
/*  752:     */       {
/*  753: 979 */         int vv = runningOrder[i];
/*  754: 980 */         int a = ftab[(vv + 1 << 8)] - ftab[(vv << 8)];
/*  755: 981 */         int b = h - 1;
/*  756: 982 */         int j = i;
/*  757: 983 */         for (int ro = runningOrder[(j - h)]; ftab[(ro + 1 << 8)] - ftab[(ro << 8)] > a; ro = runningOrder[(j - h)])
/*  758:     */         {
/*  759: 985 */           runningOrder[j] = ro;
/*  760: 986 */           j -= h;
/*  761: 987 */           if (j <= b) {
/*  762:     */             break;
/*  763:     */           }
/*  764:     */         }
/*  765: 991 */         runningOrder[j] = vv;
/*  766:     */       }
/*  767:     */     }
/*  768: 998 */     for (int i = 0; i <= 255; i++)
/*  769:     */     {
/*  770:1002 */       int ss = runningOrder[i];
/*  771:1011 */       for (int j = 0; j <= 255; j++)
/*  772:     */       {
/*  773:1012 */         int sb = (ss << 8) + j;
/*  774:1013 */         int ftab_sb = ftab[sb];
/*  775:1014 */         if ((ftab_sb & 0x200000) != 2097152)
/*  776:     */         {
/*  777:1015 */           int lo = ftab_sb & 0xFFDFFFFF;
/*  778:1016 */           int hi = (ftab[(sb + 1)] & 0xFFDFFFFF) - 1;
/*  779:1017 */           if (hi > lo)
/*  780:     */           {
/*  781:1018 */             mainQSort3(dataShadow, lo, hi, 2, lastShadow);
/*  782:1019 */             if ((firstAttemptShadow) && (this.workDone > workLimitShadow)) {
/*  783:1021 */               return;
/*  784:     */             }
/*  785:     */           }
/*  786:1024 */           ftab[sb] = (ftab_sb | 0x200000);
/*  787:     */         }
/*  788:     */       }
/*  789:1032 */       for (int j = 0; j <= 255; j++) {
/*  790:1033 */         copy[j] = (ftab[((j << 8) + ss)] & 0xFFDFFFFF);
/*  791:     */       }
/*  792:1036 */       int j = ftab[(ss << 8)] & 0xFFDFFFFF;
/*  793:1036 */       for (int hj = ftab[(ss + 1 << 8)] & 0xFFDFFFFF; j < hj; j++)
/*  794:     */       {
/*  795:1037 */         int fmap_j = fmap[j];
/*  796:1038 */         c1 = block[fmap_j] & 0xFF;
/*  797:1039 */         if (bigDone[c1] == 0)
/*  798:     */         {
/*  799:1040 */           fmap[copy[c1]] = (fmap_j == 0 ? lastShadow : fmap_j - 1);
/*  800:1041 */           copy[c1] += 1;
/*  801:     */         }
/*  802:     */       }
/*  803:1045 */       int j = 256;
/*  804:     */       for (;;)
/*  805:     */       {
/*  806:1045 */         j--;
/*  807:1045 */         if (j < 0) {
/*  808:     */           break;
/*  809:     */         }
/*  810:1046 */         ftab[((j << 8) + ss)] |= 0x200000;
/*  811:     */       }
/*  812:1057 */       bigDone[ss] = true;
/*  813:1059 */       if (i < 255)
/*  814:     */       {
/*  815:1060 */         int bbStart = ftab[(ss << 8)] & 0xFFDFFFFF;
/*  816:1061 */         int bbSize = (ftab[(ss + 1 << 8)] & 0xFFDFFFFF) - bbStart;
/*  817:1062 */         int shifts = 0;
/*  818:1064 */         while (bbSize >> shifts > 65534) {
/*  819:1065 */           shifts++;
/*  820:     */         }
/*  821:1068 */         for (int j = 0; j < bbSize; j++)
/*  822:     */         {
/*  823:1069 */           int a2update = fmap[(bbStart + j)];
/*  824:1070 */           char qVal = (char)(j >> shifts);
/*  825:1071 */           quadrant[a2update] = qVal;
/*  826:1072 */           if (a2update < 20) {
/*  827:1073 */             quadrant[(a2update + lastShadow + 1)] = qVal;
/*  828:     */           }
/*  829:     */         }
/*  830:     */       }
/*  831:     */     }
/*  832:     */   }
/*  833:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.compressors.bzip2.BlockSort
 * JD-Core Version:    0.7.0.1
 */