/*    1:     */ package org.apache.commons.compress.compressors.bzip2;
/*    2:     */ 
/*    3:     */ import java.io.IOException;
/*    4:     */ import java.io.OutputStream;
/*    5:     */ import org.apache.commons.compress.compressors.CompressorOutputStream;
/*    6:     */ 
/*    7:     */ public class BZip2CompressorOutputStream
/*    8:     */   extends CompressorOutputStream
/*    9:     */   implements BZip2Constants
/*   10:     */ {
/*   11:     */   public static final int MIN_BLOCKSIZE = 1;
/*   12:     */   public static final int MAX_BLOCKSIZE = 9;
/*   13:     */   private static final int GREATER_ICOST = 15;
/*   14:     */   private static final int LESSER_ICOST = 0;
/*   15:     */   private int last;
/*   16:     */   private final int blockSize100k;
/*   17:     */   private int bsBuff;
/*   18:     */   private int bsLive;
/*   19:     */   
/*   20:     */   private static void hbMakeCodeLengths(byte[] len, int[] freq, Data dat, int alphaSize, int maxLen)
/*   21:     */   {
/*   22: 148 */     int[] heap = dat.heap;
/*   23: 149 */     int[] weight = dat.weight;
/*   24: 150 */     int[] parent = dat.parent;
/*   25:     */     
/*   26: 152 */     int i = alphaSize;
/*   27:     */     for (;;)
/*   28:     */     {
/*   29: 152 */       i--;
/*   30: 152 */       if (i < 0) {
/*   31:     */         break;
/*   32:     */       }
/*   33: 153 */       weight[(i + 1)] = ((freq[i] == 0 ? 1 : freq[i]) << 8);
/*   34:     */     }
/*   35: 156 */     for (boolean tooLong = true; tooLong;)
/*   36:     */     {
/*   37: 157 */       tooLong = false;
/*   38:     */       
/*   39: 159 */       int nNodes = alphaSize;
/*   40: 160 */       int nHeap = 0;
/*   41: 161 */       heap[0] = 0;
/*   42: 162 */       weight[0] = 0;
/*   43: 163 */       parent[0] = -2;
/*   44: 165 */       for (int i = 1; i <= alphaSize; i++)
/*   45:     */       {
/*   46: 166 */         parent[i] = -1;
/*   47: 167 */         nHeap++;
/*   48: 168 */         heap[nHeap] = i;
/*   49:     */         
/*   50: 170 */         int zz = nHeap;
/*   51: 171 */         int tmp = heap[zz];
/*   52: 172 */         while (weight[tmp] < weight[heap[(zz >> 1)]])
/*   53:     */         {
/*   54: 173 */           heap[zz] = heap[(zz >> 1)];
/*   55: 174 */           zz >>= 1;
/*   56:     */         }
/*   57: 176 */         heap[zz] = tmp;
/*   58:     */       }
/*   59: 179 */       while (nHeap > 1)
/*   60:     */       {
/*   61: 180 */         int n1 = heap[1];
/*   62: 181 */         heap[1] = heap[nHeap];
/*   63: 182 */         nHeap--;
/*   64:     */         
/*   65: 184 */         int yy = 0;
/*   66: 185 */         int zz = 1;
/*   67: 186 */         int tmp = heap[1];
/*   68:     */         for (;;)
/*   69:     */         {
/*   70: 189 */           yy = zz << 1;
/*   71: 191 */           if (yy > nHeap) {
/*   72:     */             break;
/*   73:     */           }
/*   74: 195 */           if ((yy < nHeap) && (weight[heap[(yy + 1)]] < weight[heap[yy]])) {
/*   75: 197 */             yy++;
/*   76:     */           }
/*   77: 200 */           if (weight[tmp] < weight[heap[yy]]) {
/*   78:     */             break;
/*   79:     */           }
/*   80: 204 */           heap[zz] = heap[yy];
/*   81: 205 */           zz = yy;
/*   82:     */         }
/*   83: 208 */         heap[zz] = tmp;
/*   84:     */         
/*   85: 210 */         int n2 = heap[1];
/*   86: 211 */         heap[1] = heap[nHeap];
/*   87: 212 */         nHeap--;
/*   88:     */         
/*   89: 214 */         yy = 0;
/*   90: 215 */         zz = 1;
/*   91: 216 */         tmp = heap[1];
/*   92:     */         for (;;)
/*   93:     */         {
/*   94: 219 */           yy = zz << 1;
/*   95: 221 */           if (yy > nHeap) {
/*   96:     */             break;
/*   97:     */           }
/*   98: 225 */           if ((yy < nHeap) && (weight[heap[(yy + 1)]] < weight[heap[yy]])) {
/*   99: 227 */             yy++;
/*  100:     */           }
/*  101: 230 */           if (weight[tmp] < weight[heap[yy]]) {
/*  102:     */             break;
/*  103:     */           }
/*  104: 234 */           heap[zz] = heap[yy];
/*  105: 235 */           zz = yy;
/*  106:     */         }
/*  107: 238 */         heap[zz] = tmp;
/*  108: 239 */         nNodes++; int 
/*  109: 240 */           tmp437_435 = nNodes;parent[n2] = tmp437_435;parent[n1] = tmp437_435;
/*  110:     */         
/*  111: 242 */         int weight_n1 = weight[n1];
/*  112: 243 */         int weight_n2 = weight[n2];
/*  113: 244 */         weight[nNodes] = ((weight_n1 & 0xFFFFFF00) + (weight_n2 & 0xFFFFFF00) | 1 + ((weight_n1 & 0xFF) > (weight_n2 & 0xFF) ? weight_n1 & 0xFF : weight_n2 & 0xFF));
/*  114:     */         
/*  115:     */ 
/*  116:     */ 
/*  117:     */ 
/*  118:     */ 
/*  119:     */ 
/*  120: 251 */         parent[nNodes] = -1;
/*  121: 252 */         nHeap++;
/*  122: 253 */         heap[nHeap] = nNodes;
/*  123:     */         
/*  124: 255 */         tmp = 0;
/*  125: 256 */         zz = nHeap;
/*  126: 257 */         tmp = heap[zz];
/*  127: 258 */         int weight_tmp = weight[tmp];
/*  128: 259 */         while (weight_tmp < weight[heap[(zz >> 1)]])
/*  129:     */         {
/*  130: 260 */           heap[zz] = heap[(zz >> 1)];
/*  131: 261 */           zz >>= 1;
/*  132:     */         }
/*  133: 263 */         heap[zz] = tmp;
/*  134:     */       }
/*  135: 267 */       for (int i = 1; i <= alphaSize; i++)
/*  136:     */       {
/*  137: 268 */         int j = 0;
/*  138: 269 */         int k = i;
/*  139:     */         int parent_k;
/*  140: 271 */         while ((parent_k = parent[k]) >= 0)
/*  141:     */         {
/*  142: 272 */           k = parent_k;
/*  143: 273 */           j++;
/*  144:     */         }
/*  145: 276 */         len[(i - 1)] = ((byte)j);
/*  146: 277 */         if (j > maxLen) {
/*  147: 278 */           tooLong = true;
/*  148:     */         }
/*  149:     */       }
/*  150: 282 */       if (tooLong) {
/*  151: 283 */         for (int i = 1; i < alphaSize; i++)
/*  152:     */         {
/*  153: 284 */           int j = weight[i] >> 8;
/*  154: 285 */           j = 1 + (j >> 1);
/*  155: 286 */           weight[i] = (j << 8);
/*  156:     */         }
/*  157:     */       }
/*  158:     */     }
/*  159:     */   }
/*  160:     */   
/*  161: 305 */   private final CRC crc = new CRC();
/*  162:     */   private int nInUse;
/*  163:     */   private int nMTF;
/*  164: 311 */   private int currentChar = -1;
/*  165: 312 */   private int runLength = 0;
/*  166:     */   private int blockCRC;
/*  167:     */   private int combinedCRC;
/*  168:     */   private final int allowableBlockSize;
/*  169:     */   private Data data;
/*  170:     */   private BlockSort blockSorter;
/*  171:     */   private OutputStream out;
/*  172:     */   
/*  173:     */   public static int chooseBlockSize(long inputLength)
/*  174:     */   {
/*  175: 339 */     return inputLength > 0L ? (int)Math.min(inputLength / 132000L + 1L, 9L) : 9;
/*  176:     */   }
/*  177:     */   
/*  178:     */   public BZip2CompressorOutputStream(OutputStream out)
/*  179:     */     throws IOException
/*  180:     */   {
/*  181: 356 */     this(out, 9);
/*  182:     */   }
/*  183:     */   
/*  184:     */   public BZip2CompressorOutputStream(OutputStream out, int blockSize)
/*  185:     */     throws IOException
/*  186:     */   {
/*  187: 378 */     if (blockSize < 1) {
/*  188: 379 */       throw new IllegalArgumentException("blockSize(" + blockSize + ") < 1");
/*  189:     */     }
/*  190: 381 */     if (blockSize > 9) {
/*  191: 382 */       throw new IllegalArgumentException("blockSize(" + blockSize + ") > 9");
/*  192:     */     }
/*  193: 385 */     this.blockSize100k = blockSize;
/*  194: 386 */     this.out = out;
/*  195:     */     
/*  196:     */ 
/*  197: 389 */     this.allowableBlockSize = (this.blockSize100k * 100000 - 20);
/*  198: 390 */     init();
/*  199:     */   }
/*  200:     */   
/*  201:     */   public void write(int b)
/*  202:     */     throws IOException
/*  203:     */   {
/*  204: 395 */     if (this.out != null) {
/*  205: 396 */       write0(b);
/*  206:     */     } else {
/*  207: 398 */       throw new IOException("closed");
/*  208:     */     }
/*  209:     */   }
/*  210:     */   
/*  211:     */   private void writeRun()
/*  212:     */     throws IOException
/*  213:     */   {
/*  214: 416 */     int lastShadow = this.last;
/*  215: 418 */     if (lastShadow < this.allowableBlockSize)
/*  216:     */     {
/*  217: 419 */       int currentCharShadow = this.currentChar;
/*  218: 420 */       Data dataShadow = this.data;
/*  219: 421 */       dataShadow.inUse[currentCharShadow] = true;
/*  220: 422 */       byte ch = (byte)currentCharShadow;
/*  221:     */       
/*  222: 424 */       int runLengthShadow = this.runLength;
/*  223: 425 */       this.crc.updateCRC(currentCharShadow, runLengthShadow);
/*  224: 427 */       switch (runLengthShadow)
/*  225:     */       {
/*  226:     */       case 1: 
/*  227: 429 */         dataShadow.block[(lastShadow + 2)] = ch;
/*  228: 430 */         this.last = (lastShadow + 1);
/*  229: 431 */         break;
/*  230:     */       case 2: 
/*  231: 434 */         dataShadow.block[(lastShadow + 2)] = ch;
/*  232: 435 */         dataShadow.block[(lastShadow + 3)] = ch;
/*  233: 436 */         this.last = (lastShadow + 2);
/*  234: 437 */         break;
/*  235:     */       case 3: 
/*  236: 440 */         byte[] block = dataShadow.block;
/*  237: 441 */         block[(lastShadow + 2)] = ch;
/*  238: 442 */         block[(lastShadow + 3)] = ch;
/*  239: 443 */         block[(lastShadow + 4)] = ch;
/*  240: 444 */         this.last = (lastShadow + 3);
/*  241:     */         
/*  242: 446 */         break;
/*  243:     */       default: 
/*  244: 449 */         runLengthShadow -= 4;
/*  245: 450 */         dataShadow.inUse[runLengthShadow] = true;
/*  246: 451 */         byte[] block = dataShadow.block;
/*  247: 452 */         block[(lastShadow + 2)] = ch;
/*  248: 453 */         block[(lastShadow + 3)] = ch;
/*  249: 454 */         block[(lastShadow + 4)] = ch;
/*  250: 455 */         block[(lastShadow + 5)] = ch;
/*  251: 456 */         block[(lastShadow + 6)] = ((byte)runLengthShadow);
/*  252: 457 */         this.last = (lastShadow + 5);
/*  253:     */       }
/*  254:     */     }
/*  255:     */     else
/*  256:     */     {
/*  257: 463 */       endBlock();
/*  258: 464 */       initBlock();
/*  259: 465 */       writeRun();
/*  260:     */     }
/*  261:     */   }
/*  262:     */   
/*  263:     */   protected void finalize()
/*  264:     */     throws Throwable
/*  265:     */   {
/*  266: 474 */     finish();
/*  267: 475 */     super.finalize();
/*  268:     */   }
/*  269:     */   
/*  270:     */   public void finish()
/*  271:     */     throws IOException
/*  272:     */   {
/*  273: 480 */     if (this.out != null) {
/*  274:     */       try
/*  275:     */       {
/*  276: 482 */         if (this.runLength > 0) {
/*  277: 483 */           writeRun();
/*  278:     */         }
/*  279: 485 */         this.currentChar = -1;
/*  280: 486 */         endBlock();
/*  281: 487 */         endCompression();
/*  282:     */       }
/*  283:     */       finally
/*  284:     */       {
/*  285: 489 */         this.out = null;
/*  286: 490 */         this.data = null;
/*  287: 491 */         this.blockSorter = null;
/*  288:     */       }
/*  289:     */     }
/*  290:     */   }
/*  291:     */   
/*  292:     */   public void close()
/*  293:     */     throws IOException
/*  294:     */   {
/*  295: 498 */     if (this.out != null)
/*  296:     */     {
/*  297: 499 */       OutputStream outShadow = this.out;
/*  298: 500 */       finish();
/*  299: 501 */       outShadow.close();
/*  300:     */     }
/*  301:     */   }
/*  302:     */   
/*  303:     */   public void flush()
/*  304:     */     throws IOException
/*  305:     */   {
/*  306: 507 */     OutputStream outShadow = this.out;
/*  307: 508 */     if (outShadow != null) {
/*  308: 509 */       outShadow.flush();
/*  309:     */     }
/*  310:     */   }
/*  311:     */   
/*  312:     */   private void init()
/*  313:     */     throws IOException
/*  314:     */   {
/*  315: 520 */     bsPutUByte(66);
/*  316: 521 */     bsPutUByte(90);
/*  317:     */     
/*  318: 523 */     this.data = new Data(this.blockSize100k);
/*  319: 524 */     this.blockSorter = new BlockSort(this.data);
/*  320:     */     
/*  321:     */ 
/*  322: 527 */     bsPutUByte(104);
/*  323: 528 */     bsPutUByte(48 + this.blockSize100k);
/*  324:     */     
/*  325: 530 */     this.combinedCRC = 0;
/*  326: 531 */     initBlock();
/*  327:     */   }
/*  328:     */   
/*  329:     */   private void initBlock()
/*  330:     */   {
/*  331: 536 */     this.crc.initialiseCRC();
/*  332: 537 */     this.last = -1;
/*  333:     */     
/*  334:     */ 
/*  335: 540 */     boolean[] inUse = this.data.inUse;
/*  336: 541 */     int i = 256;
/*  337:     */     for (;;)
/*  338:     */     {
/*  339: 541 */       i--;
/*  340: 541 */       if (i < 0) {
/*  341:     */         break;
/*  342:     */       }
/*  343: 542 */       inUse[i] = false;
/*  344:     */     }
/*  345:     */   }
/*  346:     */   
/*  347:     */   private void endBlock()
/*  348:     */     throws IOException
/*  349:     */   {
/*  350: 548 */     this.blockCRC = this.crc.getFinalCRC();
/*  351: 549 */     this.combinedCRC = (this.combinedCRC << 1 | this.combinedCRC >>> 31);
/*  352: 550 */     this.combinedCRC ^= this.blockCRC;
/*  353: 553 */     if (this.last == -1) {
/*  354: 554 */       return;
/*  355:     */     }
/*  356: 558 */     blockSort();
/*  357:     */     
/*  358:     */ 
/*  359:     */ 
/*  360:     */ 
/*  361:     */ 
/*  362:     */ 
/*  363:     */ 
/*  364:     */ 
/*  365:     */ 
/*  366:     */ 
/*  367:     */ 
/*  368:     */ 
/*  369: 571 */     bsPutUByte(49);
/*  370: 572 */     bsPutUByte(65);
/*  371: 573 */     bsPutUByte(89);
/*  372: 574 */     bsPutUByte(38);
/*  373: 575 */     bsPutUByte(83);
/*  374: 576 */     bsPutUByte(89);
/*  375:     */     
/*  376:     */ 
/*  377: 579 */     bsPutInt(this.blockCRC);
/*  378:     */     
/*  379:     */ 
/*  380: 582 */     bsW(1, 0);
/*  381:     */     
/*  382:     */ 
/*  383: 585 */     moveToFrontCodeAndSend();
/*  384:     */   }
/*  385:     */   
/*  386:     */   private void endCompression()
/*  387:     */     throws IOException
/*  388:     */   {
/*  389: 595 */     bsPutUByte(23);
/*  390: 596 */     bsPutUByte(114);
/*  391: 597 */     bsPutUByte(69);
/*  392: 598 */     bsPutUByte(56);
/*  393: 599 */     bsPutUByte(80);
/*  394: 600 */     bsPutUByte(144);
/*  395:     */     
/*  396: 602 */     bsPutInt(this.combinedCRC);
/*  397: 603 */     bsFinishedWithStream();
/*  398:     */   }
/*  399:     */   
/*  400:     */   public final int getBlockSize()
/*  401:     */   {
/*  402: 610 */     return this.blockSize100k;
/*  403:     */   }
/*  404:     */   
/*  405:     */   public void write(byte[] buf, int offs, int len)
/*  406:     */     throws IOException
/*  407:     */   {
/*  408: 616 */     if (offs < 0) {
/*  409: 617 */       throw new IndexOutOfBoundsException("offs(" + offs + ") < 0.");
/*  410:     */     }
/*  411: 619 */     if (len < 0) {
/*  412: 620 */       throw new IndexOutOfBoundsException("len(" + len + ") < 0.");
/*  413:     */     }
/*  414: 622 */     if (offs + len > buf.length) {
/*  415: 623 */       throw new IndexOutOfBoundsException("offs(" + offs + ") + len(" + len + ") > buf.length(" + buf.length + ").");
/*  416:     */     }
/*  417: 627 */     if (this.out == null) {
/*  418: 628 */       throw new IOException("stream closed");
/*  419:     */     }
/*  420: 631 */     for (int hi = offs + len; offs < hi;) {
/*  421: 632 */       write0(buf[(offs++)]);
/*  422:     */     }
/*  423:     */   }
/*  424:     */   
/*  425:     */   private void write0(int b)
/*  426:     */     throws IOException
/*  427:     */   {
/*  428: 641 */     if (this.currentChar != -1)
/*  429:     */     {
/*  430: 642 */       b &= 0xFF;
/*  431: 643 */       if (this.currentChar == b)
/*  432:     */       {
/*  433: 644 */         if (++this.runLength > 254)
/*  434:     */         {
/*  435: 645 */           writeRun();
/*  436: 646 */           this.currentChar = -1;
/*  437: 647 */           this.runLength = 0;
/*  438:     */         }
/*  439:     */       }
/*  440:     */       else
/*  441:     */       {
/*  442: 651 */         writeRun();
/*  443: 652 */         this.runLength = 1;
/*  444: 653 */         this.currentChar = b;
/*  445:     */       }
/*  446:     */     }
/*  447:     */     else
/*  448:     */     {
/*  449: 656 */       this.currentChar = (b & 0xFF);
/*  450: 657 */       this.runLength += 1;
/*  451:     */     }
/*  452:     */   }
/*  453:     */   
/*  454:     */   private static void hbAssignCodes(int[] code, byte[] length, int minLen, int maxLen, int alphaSize)
/*  455:     */   {
/*  456: 664 */     int vec = 0;
/*  457: 665 */     for (int n = minLen; n <= maxLen; n++)
/*  458:     */     {
/*  459: 666 */       for (int i = 0; i < alphaSize; i++) {
/*  460: 667 */         if ((length[i] & 0xFF) == n)
/*  461:     */         {
/*  462: 668 */           code[i] = vec;
/*  463: 669 */           vec++;
/*  464:     */         }
/*  465:     */       }
/*  466: 672 */       vec <<= 1;
/*  467:     */     }
/*  468:     */   }
/*  469:     */   
/*  470:     */   private void bsFinishedWithStream()
/*  471:     */     throws IOException
/*  472:     */   {
/*  473: 677 */     while (this.bsLive > 0)
/*  474:     */     {
/*  475: 678 */       int ch = this.bsBuff >> 24;
/*  476: 679 */       this.out.write(ch);
/*  477: 680 */       this.bsBuff <<= 8;
/*  478: 681 */       this.bsLive -= 8;
/*  479:     */     }
/*  480:     */   }
/*  481:     */   
/*  482:     */   private void bsW(int n, int v)
/*  483:     */     throws IOException
/*  484:     */   {
/*  485: 686 */     OutputStream outShadow = this.out;
/*  486: 687 */     int bsLiveShadow = this.bsLive;
/*  487: 688 */     int bsBuffShadow = this.bsBuff;
/*  488: 690 */     while (bsLiveShadow >= 8)
/*  489:     */     {
/*  490: 691 */       outShadow.write(bsBuffShadow >> 24);
/*  491: 692 */       bsBuffShadow <<= 8;
/*  492: 693 */       bsLiveShadow -= 8;
/*  493:     */     }
/*  494: 696 */     this.bsBuff = (bsBuffShadow | v << 32 - bsLiveShadow - n);
/*  495: 697 */     this.bsLive = (bsLiveShadow + n);
/*  496:     */   }
/*  497:     */   
/*  498:     */   private void bsPutUByte(int c)
/*  499:     */     throws IOException
/*  500:     */   {
/*  501: 701 */     bsW(8, c);
/*  502:     */   }
/*  503:     */   
/*  504:     */   private void bsPutInt(int u)
/*  505:     */     throws IOException
/*  506:     */   {
/*  507: 705 */     bsW(8, u >> 24 & 0xFF);
/*  508: 706 */     bsW(8, u >> 16 & 0xFF);
/*  509: 707 */     bsW(8, u >> 8 & 0xFF);
/*  510: 708 */     bsW(8, u & 0xFF);
/*  511:     */   }
/*  512:     */   
/*  513:     */   private void sendMTFValues()
/*  514:     */     throws IOException
/*  515:     */   {
/*  516: 712 */     byte[][] len = this.data.sendMTFValues_len;
/*  517: 713 */     int alphaSize = this.nInUse + 2;
/*  518:     */     
/*  519: 715 */     int t = 6;
/*  520:     */     for (;;)
/*  521:     */     {
/*  522: 715 */       t--;
/*  523: 715 */       if (t < 0) {
/*  524:     */         break;
/*  525:     */       }
/*  526: 716 */       byte[] len_t = len[t];
/*  527: 717 */       int v = alphaSize;
/*  528:     */       for (;;)
/*  529:     */       {
/*  530: 717 */         v--;
/*  531: 717 */         if (v < 0) {
/*  532:     */           break;
/*  533:     */         }
/*  534: 718 */         len_t[v] = 15;
/*  535:     */       }
/*  536:     */     }
/*  537: 724 */     int nGroups = this.nMTF < 2400 ? 5 : this.nMTF < 1200 ? 4 : this.nMTF < 600 ? 3 : this.nMTF < 200 ? 2 : 6;
/*  538:     */     
/*  539:     */ 
/*  540:     */ 
/*  541: 728 */     sendMTFValues0(nGroups, alphaSize);
/*  542:     */     
/*  543:     */ 
/*  544:     */ 
/*  545:     */ 
/*  546: 733 */     int nSelectors = sendMTFValues1(nGroups, alphaSize);
/*  547:     */     
/*  548:     */ 
/*  549: 736 */     sendMTFValues2(nGroups, nSelectors);
/*  550:     */     
/*  551:     */ 
/*  552: 739 */     sendMTFValues3(nGroups, alphaSize);
/*  553:     */     
/*  554:     */ 
/*  555: 742 */     sendMTFValues4();
/*  556:     */     
/*  557:     */ 
/*  558: 745 */     sendMTFValues5(nGroups, nSelectors);
/*  559:     */     
/*  560:     */ 
/*  561: 748 */     sendMTFValues6(nGroups, alphaSize);
/*  562:     */     
/*  563:     */ 
/*  564: 751 */     sendMTFValues7();
/*  565:     */   }
/*  566:     */   
/*  567:     */   private void sendMTFValues0(int nGroups, int alphaSize)
/*  568:     */   {
/*  569: 755 */     byte[][] len = this.data.sendMTFValues_len;
/*  570: 756 */     int[] mtfFreq = this.data.mtfFreq;
/*  571:     */     
/*  572: 758 */     int remF = this.nMTF;
/*  573: 759 */     int gs = 0;
/*  574: 761 */     for (int nPart = nGroups; nPart > 0; nPart--)
/*  575:     */     {
/*  576: 762 */       int tFreq = remF / nPart;
/*  577: 763 */       int ge = gs - 1;
/*  578: 764 */       int aFreq = 0;
/*  579: 766 */       for (int a = alphaSize - 1; (aFreq < tFreq) && (ge < a);) {
/*  580: 767 */         aFreq += mtfFreq[(++ge)];
/*  581:     */       }
/*  582: 770 */       if ((ge > gs) && (nPart != nGroups) && (nPart != 1) && ((nGroups - nPart & 0x1) != 0)) {
/*  583: 772 */         aFreq -= mtfFreq[(ge--)];
/*  584:     */       }
/*  585: 775 */       byte[] len_np = len[(nPart - 1)];
/*  586: 776 */       int v = alphaSize;
/*  587:     */       for (;;)
/*  588:     */       {
/*  589: 776 */         v--;
/*  590: 776 */         if (v < 0) {
/*  591:     */           break;
/*  592:     */         }
/*  593: 777 */         if ((v >= gs) && (v <= ge)) {
/*  594: 778 */           len_np[v] = 0;
/*  595:     */         } else {
/*  596: 780 */           len_np[v] = 15;
/*  597:     */         }
/*  598:     */       }
/*  599: 784 */       gs = ge + 1;
/*  600: 785 */       remF -= aFreq;
/*  601:     */     }
/*  602:     */   }
/*  603:     */   
/*  604:     */   private int sendMTFValues1(int nGroups, int alphaSize)
/*  605:     */   {
/*  606: 790 */     Data dataShadow = this.data;
/*  607: 791 */     int[][] rfreq = dataShadow.sendMTFValues_rfreq;
/*  608: 792 */     int[] fave = dataShadow.sendMTFValues_fave;
/*  609: 793 */     short[] cost = dataShadow.sendMTFValues_cost;
/*  610: 794 */     char[] sfmap = dataShadow.sfmap;
/*  611: 795 */     byte[] selector = dataShadow.selector;
/*  612: 796 */     byte[][] len = dataShadow.sendMTFValues_len;
/*  613: 797 */     byte[] len_0 = len[0];
/*  614: 798 */     byte[] len_1 = len[1];
/*  615: 799 */     byte[] len_2 = len[2];
/*  616: 800 */     byte[] len_3 = len[3];
/*  617: 801 */     byte[] len_4 = len[4];
/*  618: 802 */     byte[] len_5 = len[5];
/*  619: 803 */     int nMTFShadow = this.nMTF;
/*  620:     */     
/*  621: 805 */     int nSelectors = 0;
/*  622: 807 */     for (int iter = 0; iter < 4; iter++)
/*  623:     */     {
/*  624: 808 */       int t = nGroups;
/*  625:     */       for (;;)
/*  626:     */       {
/*  627: 808 */         t--;
/*  628: 808 */         if (t < 0) {
/*  629:     */           break;
/*  630:     */         }
/*  631: 809 */         fave[t] = 0;
/*  632: 810 */         int[] rfreqt = rfreq[t];
/*  633: 811 */         int i = alphaSize;
/*  634:     */         for (;;)
/*  635:     */         {
/*  636: 811 */           i--;
/*  637: 811 */           if (i < 0) {
/*  638:     */             break;
/*  639:     */           }
/*  640: 812 */           rfreqt[i] = 0;
/*  641:     */         }
/*  642:     */       }
/*  643: 816 */       nSelectors = 0;
/*  644: 818 */       for (int gs = 0; gs < this.nMTF;)
/*  645:     */       {
/*  646: 826 */         int ge = Math.min(gs + 50 - 1, nMTFShadow - 1);
/*  647: 828 */         if (nGroups == 6)
/*  648:     */         {
/*  649: 831 */           short cost0 = 0;
/*  650: 832 */           short cost1 = 0;
/*  651: 833 */           short cost2 = 0;
/*  652: 834 */           short cost3 = 0;
/*  653: 835 */           short cost4 = 0;
/*  654: 836 */           short cost5 = 0;
/*  655: 838 */           for (int i = gs; i <= ge; i++)
/*  656:     */           {
/*  657: 839 */             int icv = sfmap[i];
/*  658: 840 */             cost0 = (short)(cost0 + (len_0[icv] & 0xFF));
/*  659: 841 */             cost1 = (short)(cost1 + (len_1[icv] & 0xFF));
/*  660: 842 */             cost2 = (short)(cost2 + (len_2[icv] & 0xFF));
/*  661: 843 */             cost3 = (short)(cost3 + (len_3[icv] & 0xFF));
/*  662: 844 */             cost4 = (short)(cost4 + (len_4[icv] & 0xFF));
/*  663: 845 */             cost5 = (short)(cost5 + (len_5[icv] & 0xFF));
/*  664:     */           }
/*  665: 848 */           cost[0] = cost0;
/*  666: 849 */           cost[1] = cost1;
/*  667: 850 */           cost[2] = cost2;
/*  668: 851 */           cost[3] = cost3;
/*  669: 852 */           cost[4] = cost4;
/*  670: 853 */           cost[5] = cost5;
/*  671:     */         }
/*  672:     */         else
/*  673:     */         {
/*  674: 856 */           int t = nGroups;
/*  675:     */           for (;;)
/*  676:     */           {
/*  677: 856 */             t--;
/*  678: 856 */             if (t < 0) {
/*  679:     */               break;
/*  680:     */             }
/*  681: 857 */             cost[t] = 0;
/*  682:     */           }
/*  683: 860 */           for (int i = gs; i <= ge; i++)
/*  684:     */           {
/*  685: 861 */             int icv = sfmap[i];
/*  686: 862 */             int t = nGroups;
/*  687:     */             for (;;)
/*  688:     */             {
/*  689: 862 */               t--;
/*  690: 862 */               if (t < 0) {
/*  691:     */                 break;
/*  692:     */               }
/*  693: 863 */               int tmp403_401 = t; short[] tmp403_399 = cost;tmp403_399[tmp403_401] = ((short)(tmp403_399[tmp403_401] + (len[t][icv] & 0xFF)));
/*  694:     */             }
/*  695:     */           }
/*  696:     */         }
/*  697: 872 */         int bt = -1;
/*  698: 873 */         int t = nGroups;int bc = 999999999;
/*  699:     */         for (;;)
/*  700:     */         {
/*  701: 873 */           t--;
/*  702: 873 */           if (t < 0) {
/*  703:     */             break;
/*  704:     */           }
/*  705: 874 */           int cost_t = cost[t];
/*  706: 875 */           if (cost_t < bc)
/*  707:     */           {
/*  708: 876 */             bc = cost_t;
/*  709: 877 */             bt = t;
/*  710:     */           }
/*  711:     */         }
/*  712: 881 */         fave[bt] += 1;
/*  713: 882 */         selector[nSelectors] = ((byte)bt);
/*  714: 883 */         nSelectors++;
/*  715:     */         
/*  716:     */ 
/*  717:     */ 
/*  718:     */ 
/*  719: 888 */         int[] rfreq_bt = rfreq[bt];
/*  720: 889 */         for (int i = gs; i <= ge; i++) {
/*  721: 890 */           rfreq_bt[sfmap[i]] += 1;
/*  722:     */         }
/*  723: 893 */         gs = ge + 1;
/*  724:     */       }
/*  725: 899 */       for (int t = 0; t < nGroups; t++) {
/*  726: 900 */         hbMakeCodeLengths(len[t], rfreq[t], this.data, alphaSize, 20);
/*  727:     */       }
/*  728:     */     }
/*  729: 904 */     return nSelectors;
/*  730:     */   }
/*  731:     */   
/*  732:     */   private void sendMTFValues2(int nGroups, int nSelectors)
/*  733:     */   {
/*  734: 910 */     Data dataShadow = this.data;
/*  735: 911 */     byte[] pos = dataShadow.sendMTFValues2_pos;
/*  736:     */     
/*  737: 913 */     int i = nGroups;
/*  738:     */     for (;;)
/*  739:     */     {
/*  740: 913 */       i--;
/*  741: 913 */       if (i < 0) {
/*  742:     */         break;
/*  743:     */       }
/*  744: 914 */       pos[i] = ((byte)i);
/*  745:     */     }
/*  746: 917 */     for (int i = 0; i < nSelectors; i++)
/*  747:     */     {
/*  748: 918 */       byte ll_i = dataShadow.selector[i];
/*  749: 919 */       byte tmp = pos[0];
/*  750: 920 */       int j = 0;
/*  751: 922 */       while (ll_i != tmp)
/*  752:     */       {
/*  753: 923 */         j++;
/*  754: 924 */         byte tmp2 = tmp;
/*  755: 925 */         tmp = pos[j];
/*  756: 926 */         pos[j] = tmp2;
/*  757:     */       }
/*  758: 929 */       pos[0] = tmp;
/*  759: 930 */       dataShadow.selectorMtf[i] = ((byte)j);
/*  760:     */     }
/*  761:     */   }
/*  762:     */   
/*  763:     */   private void sendMTFValues3(int nGroups, int alphaSize)
/*  764:     */   {
/*  765: 935 */     int[][] code = this.data.sendMTFValues_code;
/*  766: 936 */     byte[][] len = this.data.sendMTFValues_len;
/*  767: 938 */     for (int t = 0; t < nGroups; t++)
/*  768:     */     {
/*  769: 939 */       int minLen = 32;
/*  770: 940 */       int maxLen = 0;
/*  771: 941 */       byte[] len_t = len[t];
/*  772: 942 */       int i = alphaSize;
/*  773:     */       for (;;)
/*  774:     */       {
/*  775: 942 */         i--;
/*  776: 942 */         if (i < 0) {
/*  777:     */           break;
/*  778:     */         }
/*  779: 943 */         int l = len_t[i] & 0xFF;
/*  780: 944 */         if (l > maxLen) {
/*  781: 945 */           maxLen = l;
/*  782:     */         }
/*  783: 947 */         if (l < minLen) {
/*  784: 948 */           minLen = l;
/*  785:     */         }
/*  786:     */       }
/*  787: 955 */       hbAssignCodes(code[t], len[t], minLen, maxLen, alphaSize);
/*  788:     */     }
/*  789:     */   }
/*  790:     */   
/*  791:     */   private void sendMTFValues4()
/*  792:     */     throws IOException
/*  793:     */   {
/*  794: 960 */     boolean[] inUse = this.data.inUse;
/*  795: 961 */     boolean[] inUse16 = this.data.sentMTFValues4_inUse16;
/*  796:     */     
/*  797: 963 */     int i = 16;
/*  798:     */     for (;;)
/*  799:     */     {
/*  800: 963 */       i--;
/*  801: 963 */       if (i < 0) {
/*  802:     */         break;
/*  803:     */       }
/*  804: 964 */       inUse16[i] = false;
/*  805: 965 */       int i16 = i * 16;
/*  806: 966 */       int j = 16;
/*  807:     */       for (;;)
/*  808:     */       {
/*  809: 966 */         j--;
/*  810: 966 */         if (j < 0) {
/*  811:     */           break;
/*  812:     */         }
/*  813: 967 */         if (inUse[(i16 + j)] != 0) {
/*  814: 968 */           inUse16[i] = true;
/*  815:     */         }
/*  816:     */       }
/*  817:     */     }
/*  818: 973 */     for (int i = 0; i < 16; i++) {
/*  819: 974 */       bsW(1, inUse16[i] != 0 ? 1 : 0);
/*  820:     */     }
/*  821: 977 */     OutputStream outShadow = this.out;
/*  822: 978 */     int bsLiveShadow = this.bsLive;
/*  823: 979 */     int bsBuffShadow = this.bsBuff;
/*  824: 981 */     for (int i = 0; i < 16; i++) {
/*  825: 982 */       if (inUse16[i] != 0)
/*  826:     */       {
/*  827: 983 */         int i16 = i * 16;
/*  828: 984 */         for (int j = 0; j < 16; j++)
/*  829:     */         {
/*  830: 986 */           while (bsLiveShadow >= 8)
/*  831:     */           {
/*  832: 987 */             outShadow.write(bsBuffShadow >> 24);
/*  833: 988 */             bsBuffShadow <<= 8;
/*  834: 989 */             bsLiveShadow -= 8;
/*  835:     */           }
/*  836: 991 */           if (inUse[(i16 + j)] != 0) {
/*  837: 992 */             bsBuffShadow |= 1 << 32 - bsLiveShadow - 1;
/*  838:     */           }
/*  839: 994 */           bsLiveShadow++;
/*  840:     */         }
/*  841:     */       }
/*  842:     */     }
/*  843: 999 */     this.bsBuff = bsBuffShadow;
/*  844:1000 */     this.bsLive = bsLiveShadow;
/*  845:     */   }
/*  846:     */   
/*  847:     */   private void sendMTFValues5(int nGroups, int nSelectors)
/*  848:     */     throws IOException
/*  849:     */   {
/*  850:1005 */     bsW(3, nGroups);
/*  851:1006 */     bsW(15, nSelectors);
/*  852:     */     
/*  853:1008 */     OutputStream outShadow = this.out;
/*  854:1009 */     byte[] selectorMtf = this.data.selectorMtf;
/*  855:     */     
/*  856:1011 */     int bsLiveShadow = this.bsLive;
/*  857:1012 */     int bsBuffShadow = this.bsBuff;
/*  858:1014 */     for (int i = 0; i < nSelectors; i++)
/*  859:     */     {
/*  860:1015 */       int j = 0;
/*  861:1015 */       for (int hj = selectorMtf[i] & 0xFF; j < hj; j++)
/*  862:     */       {
/*  863:1017 */         while (bsLiveShadow >= 8)
/*  864:     */         {
/*  865:1018 */           outShadow.write(bsBuffShadow >> 24);
/*  866:1019 */           bsBuffShadow <<= 8;
/*  867:1020 */           bsLiveShadow -= 8;
/*  868:     */         }
/*  869:1022 */         bsBuffShadow |= 1 << 32 - bsLiveShadow - 1;
/*  870:1023 */         bsLiveShadow++;
/*  871:     */       }
/*  872:1027 */       while (bsLiveShadow >= 8)
/*  873:     */       {
/*  874:1028 */         outShadow.write(bsBuffShadow >> 24);
/*  875:1029 */         bsBuffShadow <<= 8;
/*  876:1030 */         bsLiveShadow -= 8;
/*  877:     */       }
/*  878:1033 */       bsLiveShadow++;
/*  879:     */     }
/*  880:1036 */     this.bsBuff = bsBuffShadow;
/*  881:1037 */     this.bsLive = bsLiveShadow;
/*  882:     */   }
/*  883:     */   
/*  884:     */   private void sendMTFValues6(int nGroups, int alphaSize)
/*  885:     */     throws IOException
/*  886:     */   {
/*  887:1042 */     byte[][] len = this.data.sendMTFValues_len;
/*  888:1043 */     OutputStream outShadow = this.out;
/*  889:     */     
/*  890:1045 */     int bsLiveShadow = this.bsLive;
/*  891:1046 */     int bsBuffShadow = this.bsBuff;
/*  892:1048 */     for (int t = 0; t < nGroups; t++)
/*  893:     */     {
/*  894:1049 */       byte[] len_t = len[t];
/*  895:1050 */       int curr = len_t[0] & 0xFF;
/*  896:1053 */       while (bsLiveShadow >= 8)
/*  897:     */       {
/*  898:1054 */         outShadow.write(bsBuffShadow >> 24);
/*  899:1055 */         bsBuffShadow <<= 8;
/*  900:1056 */         bsLiveShadow -= 8;
/*  901:     */       }
/*  902:1058 */       bsBuffShadow |= curr << 32 - bsLiveShadow - 5;
/*  903:1059 */       bsLiveShadow += 5;
/*  904:1061 */       for (int i = 0; i < alphaSize; i++)
/*  905:     */       {
/*  906:1062 */         int lti = len_t[i] & 0xFF;
/*  907:1063 */         while (curr < lti)
/*  908:     */         {
/*  909:1065 */           while (bsLiveShadow >= 8)
/*  910:     */           {
/*  911:1066 */             outShadow.write(bsBuffShadow >> 24);
/*  912:1067 */             bsBuffShadow <<= 8;
/*  913:1068 */             bsLiveShadow -= 8;
/*  914:     */           }
/*  915:1070 */           bsBuffShadow |= 2 << 32 - bsLiveShadow - 2;
/*  916:1071 */           bsLiveShadow += 2;
/*  917:     */           
/*  918:1073 */           curr++;
/*  919:     */         }
/*  920:1076 */         while (curr > lti)
/*  921:     */         {
/*  922:1078 */           while (bsLiveShadow >= 8)
/*  923:     */           {
/*  924:1079 */             outShadow.write(bsBuffShadow >> 24);
/*  925:1080 */             bsBuffShadow <<= 8;
/*  926:1081 */             bsLiveShadow -= 8;
/*  927:     */           }
/*  928:1083 */           bsBuffShadow |= 3 << 32 - bsLiveShadow - 2;
/*  929:1084 */           bsLiveShadow += 2;
/*  930:     */           
/*  931:1086 */           curr--;
/*  932:     */         }
/*  933:1090 */         while (bsLiveShadow >= 8)
/*  934:     */         {
/*  935:1091 */           outShadow.write(bsBuffShadow >> 24);
/*  936:1092 */           bsBuffShadow <<= 8;
/*  937:1093 */           bsLiveShadow -= 8;
/*  938:     */         }
/*  939:1096 */         bsLiveShadow++;
/*  940:     */       }
/*  941:     */     }
/*  942:1100 */     this.bsBuff = bsBuffShadow;
/*  943:1101 */     this.bsLive = bsLiveShadow;
/*  944:     */   }
/*  945:     */   
/*  946:     */   private void sendMTFValues7()
/*  947:     */     throws IOException
/*  948:     */   {
/*  949:1105 */     Data dataShadow = this.data;
/*  950:1106 */     byte[][] len = dataShadow.sendMTFValues_len;
/*  951:1107 */     int[][] code = dataShadow.sendMTFValues_code;
/*  952:1108 */     OutputStream outShadow = this.out;
/*  953:1109 */     byte[] selector = dataShadow.selector;
/*  954:1110 */     char[] sfmap = dataShadow.sfmap;
/*  955:1111 */     int nMTFShadow = this.nMTF;
/*  956:     */     
/*  957:1113 */     int selCtr = 0;
/*  958:     */     
/*  959:1115 */     int bsLiveShadow = this.bsLive;
/*  960:1116 */     int bsBuffShadow = this.bsBuff;
/*  961:1118 */     for (int gs = 0; gs < nMTFShadow;)
/*  962:     */     {
/*  963:1119 */       int ge = Math.min(gs + 50 - 1, nMTFShadow - 1);
/*  964:1120 */       int selector_selCtr = selector[selCtr] & 0xFF;
/*  965:1121 */       int[] code_selCtr = code[selector_selCtr];
/*  966:1122 */       byte[] len_selCtr = len[selector_selCtr];
/*  967:1124 */       while (gs <= ge)
/*  968:     */       {
/*  969:1125 */         int sfmap_i = sfmap[gs];
/*  970:1131 */         while (bsLiveShadow >= 8)
/*  971:     */         {
/*  972:1132 */           outShadow.write(bsBuffShadow >> 24);
/*  973:1133 */           bsBuffShadow <<= 8;
/*  974:1134 */           bsLiveShadow -= 8;
/*  975:     */         }
/*  976:1136 */         int n = len_selCtr[sfmap_i] & 0xFF;
/*  977:1137 */         bsBuffShadow |= code_selCtr[sfmap_i] << 32 - bsLiveShadow - n;
/*  978:1138 */         bsLiveShadow += n;
/*  979:     */         
/*  980:1140 */         gs++;
/*  981:     */       }
/*  982:1143 */       gs = ge + 1;
/*  983:1144 */       selCtr++;
/*  984:     */     }
/*  985:1147 */     this.bsBuff = bsBuffShadow;
/*  986:1148 */     this.bsLive = bsLiveShadow;
/*  987:     */   }
/*  988:     */   
/*  989:     */   private void moveToFrontCodeAndSend()
/*  990:     */     throws IOException
/*  991:     */   {
/*  992:1152 */     bsW(24, this.data.origPtr);
/*  993:1153 */     generateMTFValues();
/*  994:1154 */     sendMTFValues();
/*  995:     */   }
/*  996:     */   
/*  997:     */   private void blockSort()
/*  998:     */   {
/*  999:1158 */     this.blockSorter.blockSort(this.data, this.last);
/* 1000:     */   }
/* 1001:     */   
/* 1002:     */   private void generateMTFValues()
/* 1003:     */   {
/* 1004:1169 */     int lastShadow = this.last;
/* 1005:1170 */     Data dataShadow = this.data;
/* 1006:1171 */     boolean[] inUse = dataShadow.inUse;
/* 1007:1172 */     byte[] block = dataShadow.block;
/* 1008:1173 */     int[] fmap = dataShadow.fmap;
/* 1009:1174 */     char[] sfmap = dataShadow.sfmap;
/* 1010:1175 */     int[] mtfFreq = dataShadow.mtfFreq;
/* 1011:1176 */     byte[] unseqToSeq = dataShadow.unseqToSeq;
/* 1012:1177 */     byte[] yy = dataShadow.generateMTFValues_yy;
/* 1013:     */     
/* 1014:     */ 
/* 1015:1180 */     int nInUseShadow = 0;
/* 1016:1181 */     for (int i = 0; i < 256; i++) {
/* 1017:1182 */       if (inUse[i] != 0)
/* 1018:     */       {
/* 1019:1183 */         unseqToSeq[i] = ((byte)nInUseShadow);
/* 1020:1184 */         nInUseShadow++;
/* 1021:     */       }
/* 1022:     */     }
/* 1023:1187 */     this.nInUse = nInUseShadow;
/* 1024:     */     
/* 1025:1189 */     int eob = nInUseShadow + 1;
/* 1026:1191 */     for (int i = eob; i >= 0; i--) {
/* 1027:1192 */       mtfFreq[i] = 0;
/* 1028:     */     }
/* 1029:1195 */     int i = nInUseShadow;
/* 1030:     */     for (;;)
/* 1031:     */     {
/* 1032:1195 */       i--;
/* 1033:1195 */       if (i < 0) {
/* 1034:     */         break;
/* 1035:     */       }
/* 1036:1196 */       yy[i] = ((byte)i);
/* 1037:     */     }
/* 1038:1199 */     int wr = 0;
/* 1039:1200 */     int zPend = 0;
/* 1040:1202 */     for (int i = 0; i <= lastShadow; i++)
/* 1041:     */     {
/* 1042:1203 */       byte ll_i = unseqToSeq[(block[fmap[i]] & 0xFF)];
/* 1043:1204 */       byte tmp = yy[0];
/* 1044:1205 */       int j = 0;
/* 1045:1207 */       while (ll_i != tmp)
/* 1046:     */       {
/* 1047:1208 */         j++;
/* 1048:1209 */         byte tmp2 = tmp;
/* 1049:1210 */         tmp = yy[j];
/* 1050:1211 */         yy[j] = tmp2;
/* 1051:     */       }
/* 1052:1213 */       yy[0] = tmp;
/* 1053:1215 */       if (j == 0)
/* 1054:     */       {
/* 1055:1216 */         zPend++;
/* 1056:     */       }
/* 1057:     */       else
/* 1058:     */       {
/* 1059:1218 */         if (zPend > 0)
/* 1060:     */         {
/* 1061:1219 */           zPend--;
/* 1062:     */           for (;;)
/* 1063:     */           {
/* 1064:1221 */             if ((zPend & 0x1) == 0)
/* 1065:     */             {
/* 1066:1222 */               sfmap[wr] = '\000';
/* 1067:1223 */               wr++;
/* 1068:1224 */               mtfFreq[0] += 1;
/* 1069:     */             }
/* 1070:     */             else
/* 1071:     */             {
/* 1072:1226 */               sfmap[wr] = '\001';
/* 1073:1227 */               wr++;
/* 1074:1228 */               mtfFreq[1] += 1;
/* 1075:     */             }
/* 1076:1231 */             if (zPend < 2) {
/* 1077:     */               break;
/* 1078:     */             }
/* 1079:1232 */             zPend = zPend - 2 >> 1;
/* 1080:     */           }
/* 1081:1237 */           zPend = 0;
/* 1082:     */         }
/* 1083:1239 */         sfmap[wr] = ((char)(j + 1));
/* 1084:1240 */         wr++;
/* 1085:1241 */         mtfFreq[(j + 1)] += 1;
/* 1086:     */       }
/* 1087:     */     }
/* 1088:1245 */     if (zPend > 0)
/* 1089:     */     {
/* 1090:1246 */       zPend--;
/* 1091:     */       for (;;)
/* 1092:     */       {
/* 1093:1248 */         if ((zPend & 0x1) == 0)
/* 1094:     */         {
/* 1095:1249 */           sfmap[wr] = '\000';
/* 1096:1250 */           wr++;
/* 1097:1251 */           mtfFreq[0] += 1;
/* 1098:     */         }
/* 1099:     */         else
/* 1100:     */         {
/* 1101:1253 */           sfmap[wr] = '\001';
/* 1102:1254 */           wr++;
/* 1103:1255 */           mtfFreq[1] += 1;
/* 1104:     */         }
/* 1105:1258 */         if (zPend < 2) {
/* 1106:     */           break;
/* 1107:     */         }
/* 1108:1259 */         zPend = zPend - 2 >> 1;
/* 1109:     */       }
/* 1110:     */     }
/* 1111:1266 */     sfmap[wr] = ((char)eob);
/* 1112:1267 */     mtfFreq[eob] += 1;
/* 1113:1268 */     this.nMTF = (wr + 1);
/* 1114:     */   }
/* 1115:     */   
/* 1116:     */   static final class Data
/* 1117:     */   {
/* 1118:1275 */     final boolean[] inUse = new boolean[256];
/* 1119:1276 */     final byte[] unseqToSeq = new byte[256];
/* 1120:1277 */     final int[] mtfFreq = new int[258];
/* 1121:1278 */     final byte[] selector = new byte[18002];
/* 1122:1279 */     final byte[] selectorMtf = new byte[18002];
/* 1123:1281 */     final byte[] generateMTFValues_yy = new byte[256];
/* 1124:1282 */     final byte[][] sendMTFValues_len = new byte[6][258];
/* 1125:1284 */     final int[][] sendMTFValues_rfreq = new int[6][258];
/* 1126:1286 */     final int[] sendMTFValues_fave = new int[6];
/* 1127:1287 */     final short[] sendMTFValues_cost = new short[6];
/* 1128:1288 */     final int[][] sendMTFValues_code = new int[6][258];
/* 1129:1290 */     final byte[] sendMTFValues2_pos = new byte[6];
/* 1130:1291 */     final boolean[] sentMTFValues4_inUse16 = new boolean[16];
/* 1131:1293 */     final int[] heap = new int[260];
/* 1132:1294 */     final int[] weight = new int[516];
/* 1133:1295 */     final int[] parent = new int[516];
/* 1134:     */     final byte[] block;
/* 1135:     */     final int[] fmap;
/* 1136:     */     final char[] sfmap;
/* 1137:     */     int origPtr;
/* 1138:     */     
/* 1139:     */     Data(int blockSize100k)
/* 1140:     */     {
/* 1141:1321 */       int n = blockSize100k * 100000;
/* 1142:1322 */       this.block = new byte[n + 1 + 20];
/* 1143:1323 */       this.fmap = new int[n];
/* 1144:1324 */       this.sfmap = new char[2 * n];
/* 1145:     */     }
/* 1146:     */   }
/* 1147:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream
 * JD-Core Version:    0.7.0.1
 */