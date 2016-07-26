/*    1:     */ package org.apache.commons.compress.compressors.bzip2;
/*    2:     */ 
/*    3:     */ import java.io.IOException;
/*    4:     */ import java.io.InputStream;
/*    5:     */ import org.apache.commons.compress.compressors.CompressorInputStream;
/*    6:     */ 
/*    7:     */ public class BZip2CompressorInputStream
/*    8:     */   extends CompressorInputStream
/*    9:     */   implements BZip2Constants
/*   10:     */ {
/*   11:     */   private int last;
/*   12:     */   private int origPtr;
/*   13:     */   private int blockSize100k;
/*   14:     */   private boolean blockRandomised;
/*   15:     */   private int bsBuff;
/*   16:     */   private int bsLive;
/*   17:  60 */   private final CRC crc = new CRC();
/*   18:     */   private int nInUse;
/*   19:     */   private InputStream in;
/*   20:     */   private final boolean decompressConcatenated;
/*   21:     */   private static final int EOF = 0;
/*   22:     */   private static final int START_BLOCK_STATE = 1;
/*   23:     */   private static final int RAND_PART_A_STATE = 2;
/*   24:     */   private static final int RAND_PART_B_STATE = 3;
/*   25:     */   private static final int RAND_PART_C_STATE = 4;
/*   26:     */   private static final int NO_RAND_PART_A_STATE = 5;
/*   27:     */   private static final int NO_RAND_PART_B_STATE = 6;
/*   28:     */   private static final int NO_RAND_PART_C_STATE = 7;
/*   29:  76 */   private int currentState = 1;
/*   30:     */   private int storedBlockCRC;
/*   31:     */   private int storedCombinedCRC;
/*   32:     */   private int computedBlockCRC;
/*   33:     */   private int computedCombinedCRC;
/*   34:     */   private int su_count;
/*   35:     */   private int su_ch2;
/*   36:     */   private int su_chPrev;
/*   37:     */   private int su_i2;
/*   38:     */   private int su_j2;
/*   39:     */   private int su_rNToGo;
/*   40:     */   private int su_rTPos;
/*   41:     */   private int su_tPos;
/*   42:     */   private char su_z;
/*   43:     */   private Data data;
/*   44:     */   
/*   45:     */   public BZip2CompressorInputStream(InputStream in)
/*   46:     */     throws IOException
/*   47:     */   {
/*   48: 110 */     this(in, false);
/*   49:     */   }
/*   50:     */   
/*   51:     */   public BZip2CompressorInputStream(InputStream in, boolean decompressConcatenated)
/*   52:     */     throws IOException
/*   53:     */   {
/*   54: 130 */     this.in = in;
/*   55: 131 */     this.decompressConcatenated = decompressConcatenated;
/*   56:     */     
/*   57: 133 */     init(true);
/*   58: 134 */     initBlock();
/*   59:     */   }
/*   60:     */   
/*   61:     */   public int read()
/*   62:     */     throws IOException
/*   63:     */   {
/*   64: 139 */     if (this.in != null)
/*   65:     */     {
/*   66: 140 */       int r = read0();
/*   67: 141 */       count(r < 0 ? -1 : 1);
/*   68: 142 */       return r;
/*   69:     */     }
/*   70: 144 */     throw new IOException("stream closed");
/*   71:     */   }
/*   72:     */   
/*   73:     */   public int read(byte[] dest, int offs, int len)
/*   74:     */     throws IOException
/*   75:     */   {
/*   76: 156 */     if (offs < 0) {
/*   77: 157 */       throw new IndexOutOfBoundsException("offs(" + offs + ") < 0.");
/*   78:     */     }
/*   79: 159 */     if (len < 0) {
/*   80: 160 */       throw new IndexOutOfBoundsException("len(" + len + ") < 0.");
/*   81:     */     }
/*   82: 162 */     if (offs + len > dest.length) {
/*   83: 163 */       throw new IndexOutOfBoundsException("offs(" + offs + ") + len(" + len + ") > dest.length(" + dest.length + ").");
/*   84:     */     }
/*   85: 166 */     if (this.in == null) {
/*   86: 167 */       throw new IOException("stream closed");
/*   87:     */     }
/*   88: 169 */     if (len == 0) {
/*   89: 170 */       return 0;
/*   90:     */     }
/*   91: 173 */     int hi = offs + len;
/*   92: 174 */     int destOffs = offs;
/*   93:     */     int b;
/*   94: 176 */     while ((destOffs < hi) && ((b = read0()) >= 0))
/*   95:     */     {
/*   96: 177 */       dest[(destOffs++)] = ((byte)b);
/*   97: 178 */       count(1);
/*   98:     */     }
/*   99: 181 */     int c = destOffs == offs ? -1 : destOffs - offs;
/*  100: 182 */     return c;
/*  101:     */   }
/*  102:     */   
/*  103:     */   private void makeMaps()
/*  104:     */   {
/*  105: 186 */     boolean[] inUse = this.data.inUse;
/*  106: 187 */     byte[] seqToUnseq = this.data.seqToUnseq;
/*  107:     */     
/*  108: 189 */     int nInUseShadow = 0;
/*  109: 191 */     for (int i = 0; i < 256; i++) {
/*  110: 192 */       if (inUse[i] != 0) {
/*  111: 193 */         seqToUnseq[(nInUseShadow++)] = ((byte)i);
/*  112:     */       }
/*  113:     */     }
/*  114: 197 */     this.nInUse = nInUseShadow;
/*  115:     */   }
/*  116:     */   
/*  117:     */   private int read0()
/*  118:     */     throws IOException
/*  119:     */   {
/*  120: 201 */     switch (this.currentState)
/*  121:     */     {
/*  122:     */     case 0: 
/*  123: 203 */       return -1;
/*  124:     */     case 1: 
/*  125: 206 */       return setupBlock();
/*  126:     */     case 2: 
/*  127: 209 */       throw new IllegalStateException();
/*  128:     */     case 3: 
/*  129: 212 */       return setupRandPartB();
/*  130:     */     case 4: 
/*  131: 215 */       return setupRandPartC();
/*  132:     */     case 5: 
/*  133: 218 */       throw new IllegalStateException();
/*  134:     */     case 6: 
/*  135: 221 */       return setupNoRandPartB();
/*  136:     */     case 7: 
/*  137: 224 */       return setupNoRandPartC();
/*  138:     */     }
/*  139: 227 */     throw new IllegalStateException();
/*  140:     */   }
/*  141:     */   
/*  142:     */   private boolean init(boolean isFirstStream)
/*  143:     */     throws IOException
/*  144:     */   {
/*  145: 232 */     if (null == this.in) {
/*  146: 233 */       throw new IOException("No InputStream");
/*  147:     */     }
/*  148: 236 */     int magic0 = this.in.read();
/*  149: 237 */     if ((magic0 == -1) && (!isFirstStream)) {
/*  150: 238 */       return false;
/*  151:     */     }
/*  152: 240 */     int magic1 = this.in.read();
/*  153: 241 */     int magic2 = this.in.read();
/*  154: 243 */     if ((magic0 != 66) || (magic1 != 90) || (magic2 != 104)) {
/*  155: 244 */       throw new IOException(isFirstStream ? "Stream is not in the BZip2 format" : "Garbage after a valid BZip2 stream");
/*  156:     */     }
/*  157: 249 */     int blockSize = this.in.read();
/*  158: 250 */     if ((blockSize < 49) || (blockSize > 57)) {
/*  159: 251 */       throw new IOException("BZip2 block size is invalid");
/*  160:     */     }
/*  161: 254 */     this.blockSize100k = (blockSize - 48);
/*  162:     */     
/*  163: 256 */     this.bsLive = 0;
/*  164: 257 */     this.computedCombinedCRC = 0;
/*  165:     */     
/*  166: 259 */     return true;
/*  167:     */   }
/*  168:     */   
/*  169:     */   private void initBlock()
/*  170:     */     throws IOException
/*  171:     */   {
/*  172:     */     char magic0;
/*  173:     */     char magic1;
/*  174:     */     char magic2;
/*  175:     */     char magic3;
/*  176:     */     char magic4;
/*  177:     */     char magic5;
/*  178:     */     do
/*  179:     */     {
/*  180: 272 */       magic0 = bsGetUByte();
/*  181: 273 */       magic1 = bsGetUByte();
/*  182: 274 */       magic2 = bsGetUByte();
/*  183: 275 */       magic3 = bsGetUByte();
/*  184: 276 */       magic4 = bsGetUByte();
/*  185: 277 */       magic5 = bsGetUByte();
/*  186: 280 */       if ((magic0 != '\027') || (magic1 != 'r') || (magic2 != 'E') || (magic3 != '8') || (magic4 != 'P') || (magic5 != '')) {
/*  187:     */         break;
/*  188:     */       }
/*  189: 288 */     } while (!complete());
/*  190: 289 */     return;
/*  191: 293 */     if ((magic0 != '1') || (magic1 != 'A') || (magic2 != 'Y') || (magic3 != '&') || (magic4 != 'S') || (magic5 != 'Y'))
/*  192:     */     {
/*  193: 300 */       this.currentState = 0;
/*  194: 301 */       throw new IOException("bad block header");
/*  195:     */     }
/*  196: 303 */     this.storedBlockCRC = bsGetInt();
/*  197: 304 */     this.blockRandomised = (bsR(1) == 1);
/*  198: 310 */     if (this.data == null) {
/*  199: 311 */       this.data = new Data(this.blockSize100k);
/*  200:     */     }
/*  201: 315 */     getAndMoveToFrontDecode();
/*  202:     */     
/*  203: 317 */     this.crc.initialiseCRC();
/*  204: 318 */     this.currentState = 1;
/*  205:     */   }
/*  206:     */   
/*  207:     */   private void endBlock()
/*  208:     */     throws IOException
/*  209:     */   {
/*  210: 323 */     this.computedBlockCRC = this.crc.getFinalCRC();
/*  211: 326 */     if (this.storedBlockCRC != this.computedBlockCRC)
/*  212:     */     {
/*  213: 329 */       this.computedCombinedCRC = (this.storedCombinedCRC << 1 | this.storedCombinedCRC >>> 31);
/*  214:     */       
/*  215: 331 */       this.computedCombinedCRC ^= this.storedBlockCRC;
/*  216:     */       
/*  217: 333 */       throw new IOException("BZip2 CRC error");
/*  218:     */     }
/*  219: 336 */     this.computedCombinedCRC = (this.computedCombinedCRC << 1 | this.computedCombinedCRC >>> 31);
/*  220:     */     
/*  221: 338 */     this.computedCombinedCRC ^= this.computedBlockCRC;
/*  222:     */   }
/*  223:     */   
/*  224:     */   private boolean complete()
/*  225:     */     throws IOException
/*  226:     */   {
/*  227: 342 */     this.storedCombinedCRC = bsGetInt();
/*  228: 343 */     this.currentState = 0;
/*  229: 344 */     this.data = null;
/*  230: 346 */     if (this.storedCombinedCRC != this.computedCombinedCRC) {
/*  231: 347 */       throw new IOException("BZip2 CRC error");
/*  232:     */     }
/*  233: 352 */     return (!this.decompressConcatenated) || (!init(false));
/*  234:     */   }
/*  235:     */   
/*  236:     */   public void close()
/*  237:     */     throws IOException
/*  238:     */   {
/*  239: 357 */     InputStream inShadow = this.in;
/*  240: 358 */     if (inShadow != null) {
/*  241:     */       try
/*  242:     */       {
/*  243: 360 */         if (inShadow != System.in) {
/*  244: 361 */           inShadow.close();
/*  245:     */         }
/*  246:     */       }
/*  247:     */       finally
/*  248:     */       {
/*  249: 364 */         this.data = null;
/*  250: 365 */         this.in = null;
/*  251:     */       }
/*  252:     */     }
/*  253:     */   }
/*  254:     */   
/*  255:     */   private int bsR(int n)
/*  256:     */     throws IOException
/*  257:     */   {
/*  258: 371 */     int bsLiveShadow = this.bsLive;
/*  259: 372 */     int bsBuffShadow = this.bsBuff;
/*  260: 374 */     if (bsLiveShadow < n)
/*  261:     */     {
/*  262: 375 */       InputStream inShadow = this.in;
/*  263:     */       do
/*  264:     */       {
/*  265: 377 */         int thech = inShadow.read();
/*  266: 379 */         if (thech < 0) {
/*  267: 380 */           throw new IOException("unexpected end of stream");
/*  268:     */         }
/*  269: 383 */         bsBuffShadow = bsBuffShadow << 8 | thech;
/*  270: 384 */         bsLiveShadow += 8;
/*  271: 385 */       } while (bsLiveShadow < n);
/*  272: 387 */       this.bsBuff = bsBuffShadow;
/*  273:     */     }
/*  274: 390 */     this.bsLive = (bsLiveShadow - n);
/*  275: 391 */     return bsBuffShadow >> bsLiveShadow - n & (1 << n) - 1;
/*  276:     */   }
/*  277:     */   
/*  278:     */   private boolean bsGetBit()
/*  279:     */     throws IOException
/*  280:     */   {
/*  281: 395 */     int bsLiveShadow = this.bsLive;
/*  282: 396 */     int bsBuffShadow = this.bsBuff;
/*  283: 398 */     if (bsLiveShadow < 1)
/*  284:     */     {
/*  285: 399 */       int thech = this.in.read();
/*  286: 401 */       if (thech < 0) {
/*  287: 402 */         throw new IOException("unexpected end of stream");
/*  288:     */       }
/*  289: 405 */       bsBuffShadow = bsBuffShadow << 8 | thech;
/*  290: 406 */       bsLiveShadow += 8;
/*  291: 407 */       this.bsBuff = bsBuffShadow;
/*  292:     */     }
/*  293: 410 */     this.bsLive = (bsLiveShadow - 1);
/*  294: 411 */     return (bsBuffShadow >> bsLiveShadow - 1 & 0x1) != 0;
/*  295:     */   }
/*  296:     */   
/*  297:     */   private char bsGetUByte()
/*  298:     */     throws IOException
/*  299:     */   {
/*  300: 415 */     return (char)bsR(8);
/*  301:     */   }
/*  302:     */   
/*  303:     */   private int bsGetInt()
/*  304:     */     throws IOException
/*  305:     */   {
/*  306: 419 */     return ((bsR(8) << 8 | bsR(8)) << 8 | bsR(8)) << 8 | bsR(8);
/*  307:     */   }
/*  308:     */   
/*  309:     */   private static void hbCreateDecodeTables(int[] limit, int[] base, int[] perm, char[] length, int minLen, int maxLen, int alphaSize)
/*  310:     */   {
/*  311: 428 */     int i = minLen;
/*  312: 428 */     for (int pp = 0; i <= maxLen; i++) {
/*  313: 429 */       for (int j = 0; j < alphaSize; j++) {
/*  314: 430 */         if (length[j] == i) {
/*  315: 431 */           perm[(pp++)] = j;
/*  316:     */         }
/*  317:     */       }
/*  318:     */     }
/*  319: 436 */     int i = 23;
/*  320:     */     for (;;)
/*  321:     */     {
/*  322: 436 */       i--;
/*  323: 436 */       if (i <= 0) {
/*  324:     */         break;
/*  325:     */       }
/*  326: 437 */       base[i] = 0;
/*  327: 438 */       limit[i] = 0;
/*  328:     */     }
/*  329: 441 */     for (int i = 0; i < alphaSize; i++) {
/*  330: 442 */       base[(length[i] + '\001')] += 1;
/*  331:     */     }
/*  332: 445 */     int i = 1;
/*  333: 445 */     for (int b = base[0]; i < 23; i++)
/*  334:     */     {
/*  335: 446 */       b += base[i];
/*  336: 447 */       base[i] = b;
/*  337:     */     }
/*  338: 450 */     int i = minLen;int vec = 0;
/*  339: 450 */     for (int b = base[i]; i <= maxLen; i++)
/*  340:     */     {
/*  341: 451 */       int nb = base[(i + 1)];
/*  342: 452 */       vec += nb - b;
/*  343: 453 */       b = nb;
/*  344: 454 */       limit[i] = (vec - 1);
/*  345: 455 */       vec <<= 1;
/*  346:     */     }
/*  347: 458 */     for (int i = minLen + 1; i <= maxLen; i++) {
/*  348: 459 */       base[i] = ((limit[(i - 1)] + 1 << 1) - base[i]);
/*  349:     */     }
/*  350:     */   }
/*  351:     */   
/*  352:     */   private void recvDecodingTables()
/*  353:     */     throws IOException
/*  354:     */   {
/*  355: 464 */     Data dataShadow = this.data;
/*  356: 465 */     boolean[] inUse = dataShadow.inUse;
/*  357: 466 */     byte[] pos = dataShadow.recvDecodingTables_pos;
/*  358: 467 */     byte[] selector = dataShadow.selector;
/*  359: 468 */     byte[] selectorMtf = dataShadow.selectorMtf;
/*  360:     */     
/*  361: 470 */     int inUse16 = 0;
/*  362: 473 */     for (int i = 0; i < 16; i++) {
/*  363: 474 */       if (bsGetBit()) {
/*  364: 475 */         inUse16 |= 1 << i;
/*  365:     */       }
/*  366:     */     }
/*  367: 479 */     int i = 256;
/*  368:     */     for (;;)
/*  369:     */     {
/*  370: 479 */       i--;
/*  371: 479 */       if (i < 0) {
/*  372:     */         break;
/*  373:     */       }
/*  374: 480 */       inUse[i] = false;
/*  375:     */     }
/*  376: 483 */     for (int i = 0; i < 16; i++) {
/*  377: 484 */       if ((inUse16 & 1 << i) != 0)
/*  378:     */       {
/*  379: 485 */         int i16 = i << 4;
/*  380: 486 */         for (int j = 0; j < 16; j++) {
/*  381: 487 */           if (bsGetBit()) {
/*  382: 488 */             inUse[(i16 + j)] = true;
/*  383:     */           }
/*  384:     */         }
/*  385:     */       }
/*  386:     */     }
/*  387: 494 */     makeMaps();
/*  388: 495 */     int alphaSize = this.nInUse + 2;
/*  389:     */     
/*  390:     */ 
/*  391: 498 */     int nGroups = bsR(3);
/*  392: 499 */     int nSelectors = bsR(15);
/*  393: 501 */     for (int i = 0; i < nSelectors; i++)
/*  394:     */     {
/*  395: 502 */       int j = 0;
/*  396: 503 */       while (bsGetBit()) {
/*  397: 504 */         j++;
/*  398:     */       }
/*  399: 506 */       selectorMtf[i] = ((byte)j);
/*  400:     */     }
/*  401: 510 */     int v = nGroups;
/*  402:     */     for (;;)
/*  403:     */     {
/*  404: 510 */       v--;
/*  405: 510 */       if (v < 0) {
/*  406:     */         break;
/*  407:     */       }
/*  408: 511 */       pos[v] = ((byte)v);
/*  409:     */     }
/*  410: 514 */     for (int i = 0; i < nSelectors; i++)
/*  411:     */     {
/*  412: 515 */       int v = selectorMtf[i] & 0xFF;
/*  413: 516 */       byte tmp = pos[v];
/*  414: 517 */       while (v > 0)
/*  415:     */       {
/*  416: 519 */         pos[v] = pos[(v - 1)];
/*  417: 520 */         v--;
/*  418:     */       }
/*  419: 522 */       pos[0] = tmp;
/*  420: 523 */       selector[i] = tmp;
/*  421:     */     }
/*  422: 526 */     char[][] len = dataShadow.temp_charArray2d;
/*  423: 529 */     for (int t = 0; t < nGroups; t++)
/*  424:     */     {
/*  425: 530 */       int curr = bsR(5);
/*  426: 531 */       char[] len_t = len[t];
/*  427: 532 */       for (int i = 0; i < alphaSize; i++)
/*  428:     */       {
/*  429: 533 */         while (bsGetBit()) {
/*  430: 534 */           curr += (bsGetBit() ? -1 : 1);
/*  431:     */         }
/*  432: 536 */         len_t[i] = ((char)curr);
/*  433:     */       }
/*  434:     */     }
/*  435: 541 */     createHuffmanDecodingTables(alphaSize, nGroups);
/*  436:     */   }
/*  437:     */   
/*  438:     */   private void createHuffmanDecodingTables(int alphaSize, int nGroups)
/*  439:     */   {
/*  440: 549 */     Data dataShadow = this.data;
/*  441: 550 */     char[][] len = dataShadow.temp_charArray2d;
/*  442: 551 */     int[] minLens = dataShadow.minLens;
/*  443: 552 */     int[][] limit = dataShadow.limit;
/*  444: 553 */     int[][] base = dataShadow.base;
/*  445: 554 */     int[][] perm = dataShadow.perm;
/*  446: 556 */     for (int t = 0; t < nGroups; t++)
/*  447:     */     {
/*  448: 557 */       int minLen = 32;
/*  449: 558 */       int maxLen = 0;
/*  450: 559 */       char[] len_t = len[t];
/*  451: 560 */       int i = alphaSize;
/*  452:     */       for (;;)
/*  453:     */       {
/*  454: 560 */         i--;
/*  455: 560 */         if (i < 0) {
/*  456:     */           break;
/*  457:     */         }
/*  458: 561 */         char lent = len_t[i];
/*  459: 562 */         if (lent > maxLen) {
/*  460: 563 */           maxLen = lent;
/*  461:     */         }
/*  462: 565 */         if (lent < minLen) {
/*  463: 566 */           minLen = lent;
/*  464:     */         }
/*  465:     */       }
/*  466: 569 */       hbCreateDecodeTables(limit[t], base[t], perm[t], len[t], minLen, maxLen, alphaSize);
/*  467:     */       
/*  468: 571 */       minLens[t] = minLen;
/*  469:     */     }
/*  470:     */   }
/*  471:     */   
/*  472:     */   private void getAndMoveToFrontDecode()
/*  473:     */     throws IOException
/*  474:     */   {
/*  475: 576 */     this.origPtr = bsR(24);
/*  476: 577 */     recvDecodingTables();
/*  477:     */     
/*  478: 579 */     InputStream inShadow = this.in;
/*  479: 580 */     Data dataShadow = this.data;
/*  480: 581 */     byte[] ll8 = dataShadow.ll8;
/*  481: 582 */     int[] unzftab = dataShadow.unzftab;
/*  482: 583 */     byte[] selector = dataShadow.selector;
/*  483: 584 */     byte[] seqToUnseq = dataShadow.seqToUnseq;
/*  484: 585 */     char[] yy = dataShadow.getAndMoveToFrontDecode_yy;
/*  485: 586 */     int[] minLens = dataShadow.minLens;
/*  486: 587 */     int[][] limit = dataShadow.limit;
/*  487: 588 */     int[][] base = dataShadow.base;
/*  488: 589 */     int[][] perm = dataShadow.perm;
/*  489: 590 */     int limitLast = this.blockSize100k * 100000;
/*  490:     */     
/*  491:     */ 
/*  492:     */ 
/*  493:     */ 
/*  494:     */ 
/*  495:     */ 
/*  496: 597 */     int i = 256;
/*  497:     */     for (;;)
/*  498:     */     {
/*  499: 597 */       i--;
/*  500: 597 */       if (i < 0) {
/*  501:     */         break;
/*  502:     */       }
/*  503: 598 */       yy[i] = ((char)i);
/*  504: 599 */       unzftab[i] = 0;
/*  505:     */     }
/*  506: 602 */     int groupNo = 0;
/*  507: 603 */     int groupPos = 49;
/*  508: 604 */     int eob = this.nInUse + 1;
/*  509: 605 */     int nextSym = getAndMoveToFrontDecode0(0);
/*  510: 606 */     int bsBuffShadow = this.bsBuff;
/*  511: 607 */     int bsLiveShadow = this.bsLive;
/*  512: 608 */     int lastShadow = -1;
/*  513: 609 */     int zt = selector[groupNo] & 0xFF;
/*  514: 610 */     int[] base_zt = base[zt];
/*  515: 611 */     int[] limit_zt = limit[zt];
/*  516: 612 */     int[] perm_zt = perm[zt];
/*  517: 613 */     int minLens_zt = minLens[zt];
/*  518: 615 */     while (nextSym != eob) {
/*  519: 616 */       if ((nextSym == 0) || (nextSym == 1))
/*  520:     */       {
/*  521: 617 */         int s = -1;
/*  522: 619 */         for (int n = 1;; n <<= 1)
/*  523:     */         {
/*  524: 620 */           if (nextSym == 0)
/*  525:     */           {
/*  526: 621 */             s += n;
/*  527:     */           }
/*  528:     */           else
/*  529:     */           {
/*  530: 622 */             if (nextSym != 1) {
/*  531:     */               break;
/*  532:     */             }
/*  533: 623 */             s += (n << 1);
/*  534:     */           }
/*  535: 628 */           if (groupPos == 0)
/*  536:     */           {
/*  537: 629 */             groupPos = 49;
/*  538: 630 */             zt = selector[(++groupNo)] & 0xFF;
/*  539: 631 */             base_zt = base[zt];
/*  540: 632 */             limit_zt = limit[zt];
/*  541: 633 */             perm_zt = perm[zt];
/*  542: 634 */             minLens_zt = minLens[zt];
/*  543:     */           }
/*  544:     */           else
/*  545:     */           {
/*  546: 636 */             groupPos--;
/*  547:     */           }
/*  548: 639 */           int zn = minLens_zt;
/*  549: 643 */           while (bsLiveShadow < zn)
/*  550:     */           {
/*  551: 644 */             int thech = inShadow.read();
/*  552: 645 */             if (thech >= 0)
/*  553:     */             {
/*  554: 646 */               bsBuffShadow = bsBuffShadow << 8 | thech;
/*  555: 647 */               bsLiveShadow += 8;
/*  556:     */             }
/*  557:     */             else
/*  558:     */             {
/*  559: 650 */               throw new IOException("unexpected end of stream");
/*  560:     */             }
/*  561:     */           }
/*  562: 653 */           int zvec = bsBuffShadow >> bsLiveShadow - zn & (1 << zn) - 1;
/*  563:     */           
/*  564: 655 */           bsLiveShadow -= zn;
/*  565: 657 */           while (zvec > limit_zt[zn])
/*  566:     */           {
/*  567: 658 */             zn++;
/*  568: 659 */             while (bsLiveShadow < 1)
/*  569:     */             {
/*  570: 660 */               int thech = inShadow.read();
/*  571: 661 */               if (thech >= 0)
/*  572:     */               {
/*  573: 662 */                 bsBuffShadow = bsBuffShadow << 8 | thech;
/*  574: 663 */                 bsLiveShadow += 8;
/*  575:     */               }
/*  576:     */               else
/*  577:     */               {
/*  578: 666 */                 throw new IOException("unexpected end of stream");
/*  579:     */               }
/*  580:     */             }
/*  581: 670 */             bsLiveShadow--;
/*  582: 671 */             zvec = zvec << 1 | bsBuffShadow >> bsLiveShadow & 0x1;
/*  583:     */           }
/*  584: 674 */           nextSym = perm_zt[(zvec - base_zt[zn])];
/*  585:     */         }
/*  586: 677 */         byte ch = seqToUnseq[yy[0]];
/*  587: 678 */         unzftab[(ch & 0xFF)] += s + 1;
/*  588: 680 */         while (s-- >= 0) {
/*  589: 681 */           ll8[(++lastShadow)] = ch;
/*  590:     */         }
/*  591: 684 */         if (lastShadow >= limitLast) {
/*  592: 685 */           throw new IOException("block overrun");
/*  593:     */         }
/*  594:     */       }
/*  595:     */       else
/*  596:     */       {
/*  597: 688 */         lastShadow++;
/*  598: 688 */         if (lastShadow >= limitLast) {
/*  599: 689 */           throw new IOException("block overrun");
/*  600:     */         }
/*  601: 692 */         char tmp = yy[(nextSym - 1)];
/*  602: 693 */         unzftab[(seqToUnseq[tmp] & 0xFF)] += 1;
/*  603: 694 */         ll8[lastShadow] = seqToUnseq[tmp];
/*  604:     */         int j;
/*  605: 701 */         if (nextSym <= 16) {
/*  606: 702 */           for (j = nextSym - 1; j > 0;) {
/*  607: 703 */             yy[j] = yy[(--j)];
/*  608:     */           }
/*  609:     */         } else {
/*  610: 706 */           System.arraycopy(yy, 0, yy, 1, nextSym - 1);
/*  611:     */         }
/*  612: 709 */         yy[0] = tmp;
/*  613: 711 */         if (groupPos == 0)
/*  614:     */         {
/*  615: 712 */           groupPos = 49;
/*  616: 713 */           zt = selector[(++groupNo)] & 0xFF;
/*  617: 714 */           base_zt = base[zt];
/*  618: 715 */           limit_zt = limit[zt];
/*  619: 716 */           perm_zt = perm[zt];
/*  620: 717 */           minLens_zt = minLens[zt];
/*  621:     */         }
/*  622:     */         else
/*  623:     */         {
/*  624: 719 */           groupPos--;
/*  625:     */         }
/*  626: 722 */         int zn = minLens_zt;
/*  627: 726 */         while (bsLiveShadow < zn)
/*  628:     */         {
/*  629: 727 */           int thech = inShadow.read();
/*  630: 728 */           if (thech >= 0)
/*  631:     */           {
/*  632: 729 */             bsBuffShadow = bsBuffShadow << 8 | thech;
/*  633: 730 */             bsLiveShadow += 8;
/*  634:     */           }
/*  635:     */           else
/*  636:     */           {
/*  637: 733 */             throw new IOException("unexpected end of stream");
/*  638:     */           }
/*  639:     */         }
/*  640: 736 */         int zvec = bsBuffShadow >> bsLiveShadow - zn & (1 << zn) - 1;
/*  641:     */         
/*  642: 738 */         bsLiveShadow -= zn;
/*  643: 740 */         while (zvec > limit_zt[zn])
/*  644:     */         {
/*  645: 741 */           zn++;
/*  646: 742 */           while (bsLiveShadow < 1)
/*  647:     */           {
/*  648: 743 */             int thech = inShadow.read();
/*  649: 744 */             if (thech >= 0)
/*  650:     */             {
/*  651: 745 */               bsBuffShadow = bsBuffShadow << 8 | thech;
/*  652: 746 */               bsLiveShadow += 8;
/*  653:     */             }
/*  654:     */             else
/*  655:     */             {
/*  656: 749 */               throw new IOException("unexpected end of stream");
/*  657:     */             }
/*  658:     */           }
/*  659: 752 */           bsLiveShadow--;
/*  660: 753 */           zvec = zvec << 1 | bsBuffShadow >> bsLiveShadow & 0x1;
/*  661:     */         }
/*  662: 755 */         nextSym = perm_zt[(zvec - base_zt[zn])];
/*  663:     */       }
/*  664:     */     }
/*  665: 759 */     this.last = lastShadow;
/*  666: 760 */     this.bsLive = bsLiveShadow;
/*  667: 761 */     this.bsBuff = bsBuffShadow;
/*  668:     */   }
/*  669:     */   
/*  670:     */   private int getAndMoveToFrontDecode0(int groupNo)
/*  671:     */     throws IOException
/*  672:     */   {
/*  673: 765 */     InputStream inShadow = this.in;
/*  674: 766 */     Data dataShadow = this.data;
/*  675: 767 */     int zt = dataShadow.selector[groupNo] & 0xFF;
/*  676: 768 */     int[] limit_zt = dataShadow.limit[zt];
/*  677: 769 */     int zn = dataShadow.minLens[zt];
/*  678: 770 */     int zvec = bsR(zn);
/*  679: 771 */     int bsLiveShadow = this.bsLive;
/*  680: 772 */     int bsBuffShadow = this.bsBuff;
/*  681: 774 */     while (zvec > limit_zt[zn])
/*  682:     */     {
/*  683: 775 */       zn++;
/*  684: 776 */       while (bsLiveShadow < 1)
/*  685:     */       {
/*  686: 777 */         int thech = inShadow.read();
/*  687: 779 */         if (thech >= 0)
/*  688:     */         {
/*  689: 780 */           bsBuffShadow = bsBuffShadow << 8 | thech;
/*  690: 781 */           bsLiveShadow += 8;
/*  691:     */         }
/*  692:     */         else
/*  693:     */         {
/*  694: 784 */           throw new IOException("unexpected end of stream");
/*  695:     */         }
/*  696:     */       }
/*  697: 787 */       bsLiveShadow--;
/*  698: 788 */       zvec = zvec << 1 | bsBuffShadow >> bsLiveShadow & 0x1;
/*  699:     */     }
/*  700: 791 */     this.bsLive = bsLiveShadow;
/*  701: 792 */     this.bsBuff = bsBuffShadow;
/*  702:     */     
/*  703: 794 */     return dataShadow.perm[zt][(zvec - dataShadow.base[zt][zn])];
/*  704:     */   }
/*  705:     */   
/*  706:     */   private int setupBlock()
/*  707:     */     throws IOException
/*  708:     */   {
/*  709: 798 */     if ((this.currentState == 0) || (this.data == null)) {
/*  710: 799 */       return -1;
/*  711:     */     }
/*  712: 802 */     int[] cftab = this.data.cftab;
/*  713: 803 */     int[] tt = this.data.initTT(this.last + 1);
/*  714: 804 */     byte[] ll8 = this.data.ll8;
/*  715: 805 */     cftab[0] = 0;
/*  716: 806 */     System.arraycopy(this.data.unzftab, 0, cftab, 1, 256);
/*  717:     */     
/*  718: 808 */     int i = 1;
/*  719: 808 */     for (int c = cftab[0]; i <= 256; i++)
/*  720:     */     {
/*  721: 809 */       c += cftab[i];
/*  722: 810 */       cftab[i] = c;
/*  723:     */     }
/*  724: 813 */     int i = 0;
/*  725: 813 */     for (int lastShadow = this.last; i <= lastShadow; i++)
/*  726:     */     {
/*  727: 814 */       byte tmp129_128 = (ll8[i] & 0xFF); int[] tmp129_120 = cftab; int tmp131_130 = tmp129_120[tmp129_128];tmp129_120[tmp129_128] = (tmp131_130 + 1);tt[tmp131_130] = i;
/*  728:     */     }
/*  729: 817 */     if ((this.origPtr < 0) || (this.origPtr >= tt.length)) {
/*  730: 818 */       throw new IOException("stream corrupted");
/*  731:     */     }
/*  732: 821 */     this.su_tPos = tt[this.origPtr];
/*  733: 822 */     this.su_count = 0;
/*  734: 823 */     this.su_i2 = 0;
/*  735: 824 */     this.su_ch2 = 256;
/*  736: 826 */     if (this.blockRandomised)
/*  737:     */     {
/*  738: 827 */       this.su_rNToGo = 0;
/*  739: 828 */       this.su_rTPos = 0;
/*  740: 829 */       return setupRandPartA();
/*  741:     */     }
/*  742: 831 */     return setupNoRandPartA();
/*  743:     */   }
/*  744:     */   
/*  745:     */   private int setupRandPartA()
/*  746:     */     throws IOException
/*  747:     */   {
/*  748: 835 */     if (this.su_i2 <= this.last)
/*  749:     */     {
/*  750: 836 */       this.su_chPrev = this.su_ch2;
/*  751: 837 */       int su_ch2Shadow = this.data.ll8[this.su_tPos] & 0xFF;
/*  752: 838 */       this.su_tPos = this.data.tt[this.su_tPos];
/*  753: 839 */       if (this.su_rNToGo == 0)
/*  754:     */       {
/*  755: 840 */         this.su_rNToGo = (Rand.rNums(this.su_rTPos) - 1);
/*  756: 841 */         if (++this.su_rTPos == 512) {
/*  757: 842 */           this.su_rTPos = 0;
/*  758:     */         }
/*  759:     */       }
/*  760:     */       else
/*  761:     */       {
/*  762: 845 */         this.su_rNToGo -= 1;
/*  763:     */       }
/*  764: 847 */       this.su_ch2 = (su_ch2Shadow ^= (this.su_rNToGo == 1 ? 1 : 0));
/*  765: 848 */       this.su_i2 += 1;
/*  766: 849 */       this.currentState = 3;
/*  767: 850 */       this.crc.updateCRC(su_ch2Shadow);
/*  768: 851 */       return su_ch2Shadow;
/*  769:     */     }
/*  770: 853 */     endBlock();
/*  771: 854 */     initBlock();
/*  772: 855 */     return setupBlock();
/*  773:     */   }
/*  774:     */   
/*  775:     */   private int setupNoRandPartA()
/*  776:     */     throws IOException
/*  777:     */   {
/*  778: 860 */     if (this.su_i2 <= this.last)
/*  779:     */     {
/*  780: 861 */       this.su_chPrev = this.su_ch2;
/*  781: 862 */       int su_ch2Shadow = this.data.ll8[this.su_tPos] & 0xFF;
/*  782: 863 */       this.su_ch2 = su_ch2Shadow;
/*  783: 864 */       this.su_tPos = this.data.tt[this.su_tPos];
/*  784: 865 */       this.su_i2 += 1;
/*  785: 866 */       this.currentState = 6;
/*  786: 867 */       this.crc.updateCRC(su_ch2Shadow);
/*  787: 868 */       return su_ch2Shadow;
/*  788:     */     }
/*  789: 870 */     this.currentState = 5;
/*  790: 871 */     endBlock();
/*  791: 872 */     initBlock();
/*  792: 873 */     return setupBlock();
/*  793:     */   }
/*  794:     */   
/*  795:     */   private int setupRandPartB()
/*  796:     */     throws IOException
/*  797:     */   {
/*  798: 878 */     if (this.su_ch2 != this.su_chPrev)
/*  799:     */     {
/*  800: 879 */       this.currentState = 2;
/*  801: 880 */       this.su_count = 1;
/*  802: 881 */       return setupRandPartA();
/*  803:     */     }
/*  804: 882 */     if (++this.su_count >= 4)
/*  805:     */     {
/*  806: 883 */       this.su_z = ((char)(this.data.ll8[this.su_tPos] & 0xFF));
/*  807: 884 */       this.su_tPos = this.data.tt[this.su_tPos];
/*  808: 885 */       if (this.su_rNToGo == 0)
/*  809:     */       {
/*  810: 886 */         this.su_rNToGo = (Rand.rNums(this.su_rTPos) - 1);
/*  811: 887 */         if (++this.su_rTPos == 512) {
/*  812: 888 */           this.su_rTPos = 0;
/*  813:     */         }
/*  814:     */       }
/*  815:     */       else
/*  816:     */       {
/*  817: 891 */         this.su_rNToGo -= 1;
/*  818:     */       }
/*  819: 893 */       this.su_j2 = 0;
/*  820: 894 */       this.currentState = 4;
/*  821: 895 */       if (this.su_rNToGo == 1) {
/*  822: 896 */         this.su_z = ((char)(this.su_z ^ 0x1));
/*  823:     */       }
/*  824: 898 */       return setupRandPartC();
/*  825:     */     }
/*  826: 900 */     this.currentState = 2;
/*  827: 901 */     return setupRandPartA();
/*  828:     */   }
/*  829:     */   
/*  830:     */   private int setupRandPartC()
/*  831:     */     throws IOException
/*  832:     */   {
/*  833: 906 */     if (this.su_j2 < this.su_z)
/*  834:     */     {
/*  835: 907 */       this.crc.updateCRC(this.su_ch2);
/*  836: 908 */       this.su_j2 += 1;
/*  837: 909 */       return this.su_ch2;
/*  838:     */     }
/*  839: 911 */     this.currentState = 2;
/*  840: 912 */     this.su_i2 += 1;
/*  841: 913 */     this.su_count = 0;
/*  842: 914 */     return setupRandPartA();
/*  843:     */   }
/*  844:     */   
/*  845:     */   private int setupNoRandPartB()
/*  846:     */     throws IOException
/*  847:     */   {
/*  848: 919 */     if (this.su_ch2 != this.su_chPrev)
/*  849:     */     {
/*  850: 920 */       this.su_count = 1;
/*  851: 921 */       return setupNoRandPartA();
/*  852:     */     }
/*  853: 922 */     if (++this.su_count >= 4)
/*  854:     */     {
/*  855: 923 */       this.su_z = ((char)(this.data.ll8[this.su_tPos] & 0xFF));
/*  856: 924 */       this.su_tPos = this.data.tt[this.su_tPos];
/*  857: 925 */       this.su_j2 = 0;
/*  858: 926 */       return setupNoRandPartC();
/*  859:     */     }
/*  860: 928 */     return setupNoRandPartA();
/*  861:     */   }
/*  862:     */   
/*  863:     */   private int setupNoRandPartC()
/*  864:     */     throws IOException
/*  865:     */   {
/*  866: 933 */     if (this.su_j2 < this.su_z)
/*  867:     */     {
/*  868: 934 */       int su_ch2Shadow = this.su_ch2;
/*  869: 935 */       this.crc.updateCRC(su_ch2Shadow);
/*  870: 936 */       this.su_j2 += 1;
/*  871: 937 */       this.currentState = 7;
/*  872: 938 */       return su_ch2Shadow;
/*  873:     */     }
/*  874: 940 */     this.su_i2 += 1;
/*  875: 941 */     this.su_count = 0;
/*  876: 942 */     return setupNoRandPartA();
/*  877:     */   }
/*  878:     */   
/*  879:     */   private static final class Data
/*  880:     */   {
/*  881: 949 */     final boolean[] inUse = new boolean[256];
/*  882: 951 */     final byte[] seqToUnseq = new byte[256];
/*  883: 952 */     final byte[] selector = new byte[18002];
/*  884: 953 */     final byte[] selectorMtf = new byte[18002];
/*  885: 959 */     final int[] unzftab = new int[256];
/*  886: 961 */     final int[][] limit = new int[6][258];
/*  887: 962 */     final int[][] base = new int[6][258];
/*  888: 963 */     final int[][] perm = new int[6][258];
/*  889: 964 */     final int[] minLens = new int[6];
/*  890: 966 */     final int[] cftab = new int[257];
/*  891: 967 */     final char[] getAndMoveToFrontDecode_yy = new char[256];
/*  892: 968 */     final char[][] temp_charArray2d = new char[6][258];
/*  893: 970 */     final byte[] recvDecodingTables_pos = new byte[6];
/*  894:     */     int[] tt;
/*  895:     */     byte[] ll8;
/*  896:     */     
/*  897:     */     Data(int blockSize100k)
/*  898:     */     {
/*  899: 982 */       this.ll8 = new byte[blockSize100k * 100000];
/*  900:     */     }
/*  901:     */     
/*  902:     */     int[] initTT(int length)
/*  903:     */     {
/*  904: 993 */       int[] ttShadow = this.tt;
/*  905: 999 */       if ((ttShadow == null) || (ttShadow.length < length)) {
/*  906:1000 */         this.tt = (ttShadow = new int[length]);
/*  907:     */       }
/*  908:1003 */       return ttShadow;
/*  909:     */     }
/*  910:     */   }
/*  911:     */   
/*  912:     */   public static boolean matches(byte[] signature, int length)
/*  913:     */   {
/*  914:1021 */     if (length < 3) {
/*  915:1022 */       return false;
/*  916:     */     }
/*  917:1025 */     if (signature[0] != 66) {
/*  918:1026 */       return false;
/*  919:     */     }
/*  920:1029 */     if (signature[1] != 90) {
/*  921:1030 */       return false;
/*  922:     */     }
/*  923:1033 */     if (signature[2] != 104) {
/*  924:1034 */       return false;
/*  925:     */     }
/*  926:1037 */     return true;
/*  927:     */   }
/*  928:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
 * JD-Core Version:    0.7.0.1
 */