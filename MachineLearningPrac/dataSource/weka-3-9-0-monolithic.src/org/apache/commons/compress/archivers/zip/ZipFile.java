/*    1:     */ package org.apache.commons.compress.archivers.zip;
/*    2:     */ 
/*    3:     */ import java.io.BufferedInputStream;
/*    4:     */ import java.io.Closeable;
/*    5:     */ import java.io.EOFException;
/*    6:     */ import java.io.File;
/*    7:     */ import java.io.IOException;
/*    8:     */ import java.io.InputStream;
/*    9:     */ import java.io.PrintStream;
/*   10:     */ import java.io.RandomAccessFile;
/*   11:     */ import java.util.Arrays;
/*   12:     */ import java.util.Collections;
/*   13:     */ import java.util.Comparator;
/*   14:     */ import java.util.Enumeration;
/*   15:     */ import java.util.HashMap;
/*   16:     */ import java.util.LinkedList;
/*   17:     */ import java.util.List;
/*   18:     */ import java.util.Map;
/*   19:     */ import java.util.zip.Inflater;
/*   20:     */ import java.util.zip.InflaterInputStream;
/*   21:     */ import java.util.zip.ZipException;
/*   22:     */ import org.apache.commons.compress.utils.IOUtils;
/*   23:     */ 
/*   24:     */ public class ZipFile
/*   25:     */   implements Closeable
/*   26:     */ {
/*   27:     */   private static final int HASH_SIZE = 509;
/*   28:     */   static final int NIBLET_MASK = 15;
/*   29:     */   static final int BYTE_SHIFT = 8;
/*   30:     */   private static final int POS_0 = 0;
/*   31:     */   private static final int POS_1 = 1;
/*   32:     */   private static final int POS_2 = 2;
/*   33:     */   private static final int POS_3 = 3;
/*   34:  91 */   private final List<ZipArchiveEntry> entries = new LinkedList();
/*   35:  97 */   private final Map<String, LinkedList<ZipArchiveEntry>> nameMap = new HashMap(509);
/*   36:     */   private final String encoding;
/*   37:     */   private final ZipEncoding zipEncoding;
/*   38:     */   private final String archiveName;
/*   39:     */   private final RandomAccessFile archive;
/*   40:     */   private final boolean useUnicodeExtraFields;
/*   41:     */   
/*   42:     */   private static final class OffsetEntry
/*   43:     */   {
/*   44: 101 */     private long headerOffset = -1L;
/*   45: 102 */     private long dataOffset = -1L;
/*   46:     */   }
/*   47:     */   
/*   48: 137 */   private volatile boolean closed = true;
/*   49: 140 */   private final byte[] DWORD_BUF = new byte[8];
/*   50: 141 */   private final byte[] WORD_BUF = new byte[4];
/*   51: 142 */   private final byte[] CFH_BUF = new byte[42];
/*   52: 143 */   private final byte[] SHORT_BUF = new byte[2];
/*   53:     */   private static final int CFH_LEN = 42;
/*   54:     */   
/*   55:     */   public ZipFile(File f)
/*   56:     */     throws IOException
/*   57:     */   {
/*   58: 153 */     this(f, "UTF8");
/*   59:     */   }
/*   60:     */   
/*   61:     */   public ZipFile(String name)
/*   62:     */     throws IOException
/*   63:     */   {
/*   64: 164 */     this(new File(name), "UTF8");
/*   65:     */   }
/*   66:     */   
/*   67:     */   public ZipFile(String name, String encoding)
/*   68:     */     throws IOException
/*   69:     */   {
/*   70: 178 */     this(new File(name), encoding, true);
/*   71:     */   }
/*   72:     */   
/*   73:     */   public ZipFile(File f, String encoding)
/*   74:     */     throws IOException
/*   75:     */   {
/*   76: 192 */     this(f, encoding, true);
/*   77:     */   }
/*   78:     */   
/*   79:     */   public ZipFile(File f, String encoding, boolean useUnicodeExtraFields)
/*   80:     */     throws IOException
/*   81:     */   {
/*   82: 209 */     this.archiveName = f.getAbsolutePath();
/*   83: 210 */     this.encoding = encoding;
/*   84: 211 */     this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
/*   85: 212 */     this.useUnicodeExtraFields = useUnicodeExtraFields;
/*   86: 213 */     this.archive = new RandomAccessFile(f, "r");
/*   87: 214 */     boolean success = false;
/*   88:     */     try
/*   89:     */     {
/*   90: 216 */       Map<ZipArchiveEntry, NameAndComment> entriesWithoutUTF8Flag = populateFromCentralDirectory();
/*   91:     */       
/*   92: 218 */       resolveLocalFileHeaderData(entriesWithoutUTF8Flag);
/*   93: 219 */       success = true;
/*   94:     */     }
/*   95:     */     finally
/*   96:     */     {
/*   97: 221 */       this.closed = (!success);
/*   98: 222 */       if (!success) {
/*   99: 223 */         IOUtils.closeQuietly(this.archive);
/*  100:     */       }
/*  101:     */     }
/*  102:     */   }
/*  103:     */   
/*  104:     */   public String getEncoding()
/*  105:     */   {
/*  106: 234 */     return this.encoding;
/*  107:     */   }
/*  108:     */   
/*  109:     */   public void close()
/*  110:     */     throws IOException
/*  111:     */   {
/*  112: 245 */     this.closed = true;
/*  113:     */     
/*  114: 247 */     this.archive.close();
/*  115:     */   }
/*  116:     */   
/*  117:     */   public static void closeQuietly(ZipFile zipfile)
/*  118:     */   {
/*  119: 256 */     IOUtils.closeQuietly(zipfile);
/*  120:     */   }
/*  121:     */   
/*  122:     */   public Enumeration<ZipArchiveEntry> getEntries()
/*  123:     */   {
/*  124: 268 */     return Collections.enumeration(this.entries);
/*  125:     */   }
/*  126:     */   
/*  127:     */   public Enumeration<ZipArchiveEntry> getEntriesInPhysicalOrder()
/*  128:     */   {
/*  129: 282 */     ZipArchiveEntry[] allEntries = (ZipArchiveEntry[])this.entries.toArray(new ZipArchiveEntry[this.entries.size()]);
/*  130: 283 */     Arrays.sort(allEntries, this.OFFSET_COMPARATOR);
/*  131: 284 */     return Collections.enumeration(Arrays.asList(allEntries));
/*  132:     */   }
/*  133:     */   
/*  134:     */   public ZipArchiveEntry getEntry(String name)
/*  135:     */   {
/*  136: 300 */     LinkedList<ZipArchiveEntry> entriesOfThatName = (LinkedList)this.nameMap.get(name);
/*  137: 301 */     return entriesOfThatName != null ? (ZipArchiveEntry)entriesOfThatName.getFirst() : null;
/*  138:     */   }
/*  139:     */   
/*  140:     */   public Iterable<ZipArchiveEntry> getEntries(String name)
/*  141:     */   {
/*  142: 314 */     List<ZipArchiveEntry> entriesOfThatName = (List)this.nameMap.get(name);
/*  143: 315 */     return entriesOfThatName != null ? entriesOfThatName : Collections.emptyList();
/*  144:     */   }
/*  145:     */   
/*  146:     */   public Iterable<ZipArchiveEntry> getEntriesInPhysicalOrder(String name)
/*  147:     */   {
/*  148: 329 */     ZipArchiveEntry[] entriesOfThatName = new ZipArchiveEntry[0];
/*  149: 330 */     if (this.nameMap.containsKey(name))
/*  150:     */     {
/*  151: 331 */       entriesOfThatName = (ZipArchiveEntry[])((LinkedList)this.nameMap.get(name)).toArray(entriesOfThatName);
/*  152: 332 */       Arrays.sort(entriesOfThatName, this.OFFSET_COMPARATOR);
/*  153:     */     }
/*  154: 334 */     return Arrays.asList(entriesOfThatName);
/*  155:     */   }
/*  156:     */   
/*  157:     */   public boolean canReadEntryData(ZipArchiveEntry ze)
/*  158:     */   {
/*  159: 347 */     return ZipUtil.canHandleEntryData(ze);
/*  160:     */   }
/*  161:     */   
/*  162:     */   private InputStream getRawInputStream(ZipArchiveEntry ze)
/*  163:     */   {
/*  164: 360 */     if (!(ze instanceof Entry)) {
/*  165: 361 */       return null;
/*  166:     */     }
/*  167: 363 */     OffsetEntry offsetEntry = ((Entry)ze).getOffsetEntry();
/*  168: 364 */     long start = offsetEntry.dataOffset;
/*  169: 365 */     return new BoundedInputStream(start, ze.getCompressedSize());
/*  170:     */   }
/*  171:     */   
/*  172:     */   public void copyRawEntries(ZipArchiveOutputStream target, ZipArchiveEntryPredicate predicate)
/*  173:     */     throws IOException
/*  174:     */   {
/*  175: 380 */     Enumeration<ZipArchiveEntry> src = getEntriesInPhysicalOrder();
/*  176: 381 */     while (src.hasMoreElements())
/*  177:     */     {
/*  178: 382 */       ZipArchiveEntry entry = (ZipArchiveEntry)src.nextElement();
/*  179: 383 */       if (predicate.test(entry)) {
/*  180: 384 */         target.addRawArchiveEntry(entry, getRawInputStream(entry));
/*  181:     */       }
/*  182:     */     }
/*  183:     */   }
/*  184:     */   
/*  185:     */   public InputStream getInputStream(ZipArchiveEntry ze)
/*  186:     */     throws IOException, ZipException
/*  187:     */   {
/*  188: 399 */     if (!(ze instanceof Entry)) {
/*  189: 400 */       return null;
/*  190:     */     }
/*  191: 403 */     OffsetEntry offsetEntry = ((Entry)ze).getOffsetEntry();
/*  192: 404 */     ZipUtil.checkRequestedFeatures(ze);
/*  193: 405 */     long start = offsetEntry.dataOffset;
/*  194: 406 */     BoundedInputStream bis = new BoundedInputStream(start, ze.getCompressedSize());
/*  195: 408 */     switch (3.$SwitchMap$org$apache$commons$compress$archivers$zip$ZipMethod[ZipMethod.getMethodByCode(ze.getMethod()).ordinal()])
/*  196:     */     {
/*  197:     */     case 1: 
/*  198: 410 */       return bis;
/*  199:     */     case 2: 
/*  200: 412 */       return new UnshrinkingInputStream(bis);
/*  201:     */     case 3: 
/*  202: 414 */       return new ExplodingInputStream(ze.getGeneralPurposeBit().getSlidingDictionarySize(), ze.getGeneralPurposeBit().getNumberOfShannonFanoTrees(), new BufferedInputStream(bis));
/*  203:     */     case 4: 
/*  204: 417 */       bis.addDummy();
/*  205: 418 */       final Inflater inflater = new Inflater(true);
/*  206: 419 */       new InflaterInputStream(bis, inflater)
/*  207:     */       {
/*  208:     */         public void close()
/*  209:     */           throws IOException
/*  210:     */         {
/*  211: 422 */           super.close();
/*  212: 423 */           inflater.end();
/*  213:     */         }
/*  214:     */       };
/*  215:     */     }
/*  216: 427 */     throw new ZipException("Found unsupported compression method " + ze.getMethod());
/*  217:     */   }
/*  218:     */   
/*  219:     */   public String getUnixSymlink(ZipArchiveEntry entry)
/*  220:     */     throws IOException
/*  221:     */   {
/*  222: 447 */     if ((entry != null) && (entry.isUnixSymlink()))
/*  223:     */     {
/*  224: 448 */       InputStream in = null;
/*  225:     */       try
/*  226:     */       {
/*  227: 450 */         in = getInputStream(entry);
/*  228: 451 */         byte[] symlinkBytes = IOUtils.toByteArray(in);
/*  229: 452 */         return this.zipEncoding.decode(symlinkBytes);
/*  230:     */       }
/*  231:     */       finally
/*  232:     */       {
/*  233: 454 */         if (in != null) {
/*  234: 455 */           in.close();
/*  235:     */         }
/*  236:     */       }
/*  237:     */     }
/*  238: 459 */     return null;
/*  239:     */   }
/*  240:     */   
/*  241:     */   protected void finalize()
/*  242:     */     throws Throwable
/*  243:     */   {
/*  244:     */     try
/*  245:     */     {
/*  246: 471 */       if (!this.closed)
/*  247:     */       {
/*  248: 472 */         System.err.println("Cleaning up unclosed ZipFile for archive " + this.archiveName);
/*  249:     */         
/*  250: 474 */         close();
/*  251:     */       }
/*  252:     */     }
/*  253:     */     finally
/*  254:     */     {
/*  255: 477 */       super.finalize();
/*  256:     */     }
/*  257:     */   }
/*  258:     */   
/*  259: 503 */   private static final long CFH_SIG = ZipLong.getValue(ZipArchiveOutputStream.CFH_SIG);
/*  260:     */   static final int MIN_EOCD_SIZE = 22;
/*  261:     */   private static final int MAX_EOCD_SIZE = 65557;
/*  262:     */   private static final int CFD_LOCATOR_OFFSET = 16;
/*  263:     */   private static final int ZIP64_EOCDL_LENGTH = 20;
/*  264:     */   private static final int ZIP64_EOCDL_LOCATOR_OFFSET = 8;
/*  265:     */   private static final int ZIP64_EOCD_CFD_LOCATOR_OFFSET = 48;
/*  266:     */   private static final long LFH_OFFSET_FOR_FILENAME_LENGTH = 26L;
/*  267:     */   
/*  268:     */   private Map<ZipArchiveEntry, NameAndComment> populateFromCentralDirectory()
/*  269:     */     throws IOException
/*  270:     */   {
/*  271: 519 */     HashMap<ZipArchiveEntry, NameAndComment> noUTF8Flag = new HashMap();
/*  272:     */     
/*  273:     */ 
/*  274: 522 */     positionAtCentralDirectory();
/*  275:     */     
/*  276: 524 */     this.archive.readFully(this.WORD_BUF);
/*  277: 525 */     long sig = ZipLong.getValue(this.WORD_BUF);
/*  278: 527 */     if ((sig != CFH_SIG) && (startsWithLocalFileHeader())) {
/*  279: 528 */       throw new IOException("central directory is empty, can't expand corrupt archive.");
/*  280:     */     }
/*  281: 532 */     while (sig == CFH_SIG)
/*  282:     */     {
/*  283: 533 */       readCentralDirectoryEntry(noUTF8Flag);
/*  284: 534 */       this.archive.readFully(this.WORD_BUF);
/*  285: 535 */       sig = ZipLong.getValue(this.WORD_BUF);
/*  286:     */     }
/*  287: 537 */     return noUTF8Flag;
/*  288:     */   }
/*  289:     */   
/*  290:     */   private void readCentralDirectoryEntry(Map<ZipArchiveEntry, NameAndComment> noUTF8Flag)
/*  291:     */     throws IOException
/*  292:     */   {
/*  293: 552 */     this.archive.readFully(this.CFH_BUF);
/*  294: 553 */     int off = 0;
/*  295: 554 */     OffsetEntry offset = new OffsetEntry(null);
/*  296: 555 */     Entry ze = new Entry(offset);
/*  297:     */     
/*  298: 557 */     int versionMadeBy = ZipShort.getValue(this.CFH_BUF, off);
/*  299: 558 */     off += 2;
/*  300: 559 */     ze.setPlatform(versionMadeBy >> 8 & 0xF);
/*  301:     */     
/*  302: 561 */     off += 2;
/*  303:     */     
/*  304: 563 */     GeneralPurposeBit gpFlag = GeneralPurposeBit.parse(this.CFH_BUF, off);
/*  305: 564 */     boolean hasUTF8Flag = gpFlag.usesUTF8ForNames();
/*  306: 565 */     ZipEncoding entryEncoding = hasUTF8Flag ? ZipEncodingHelper.UTF8_ZIP_ENCODING : this.zipEncoding;
/*  307:     */     
/*  308: 567 */     ze.setGeneralPurposeBit(gpFlag);
/*  309:     */     
/*  310: 569 */     off += 2;
/*  311:     */     
/*  312: 571 */     ze.setMethod(ZipShort.getValue(this.CFH_BUF, off));
/*  313: 572 */     off += 2;
/*  314:     */     
/*  315: 574 */     long time = ZipUtil.dosToJavaTime(ZipLong.getValue(this.CFH_BUF, off));
/*  316: 575 */     ze.setTime(time);
/*  317: 576 */     off += 4;
/*  318:     */     
/*  319: 578 */     ze.setCrc(ZipLong.getValue(this.CFH_BUF, off));
/*  320: 579 */     off += 4;
/*  321:     */     
/*  322: 581 */     ze.setCompressedSize(ZipLong.getValue(this.CFH_BUF, off));
/*  323: 582 */     off += 4;
/*  324:     */     
/*  325: 584 */     ze.setSize(ZipLong.getValue(this.CFH_BUF, off));
/*  326: 585 */     off += 4;
/*  327:     */     
/*  328: 587 */     int fileNameLen = ZipShort.getValue(this.CFH_BUF, off);
/*  329: 588 */     off += 2;
/*  330:     */     
/*  331: 590 */     int extraLen = ZipShort.getValue(this.CFH_BUF, off);
/*  332: 591 */     off += 2;
/*  333:     */     
/*  334: 593 */     int commentLen = ZipShort.getValue(this.CFH_BUF, off);
/*  335: 594 */     off += 2;
/*  336:     */     
/*  337: 596 */     int diskStart = ZipShort.getValue(this.CFH_BUF, off);
/*  338: 597 */     off += 2;
/*  339:     */     
/*  340: 599 */     ze.setInternalAttributes(ZipShort.getValue(this.CFH_BUF, off));
/*  341: 600 */     off += 2;
/*  342:     */     
/*  343: 602 */     ze.setExternalAttributes(ZipLong.getValue(this.CFH_BUF, off));
/*  344: 603 */     off += 4;
/*  345:     */     
/*  346: 605 */     byte[] fileName = new byte[fileNameLen];
/*  347: 606 */     this.archive.readFully(fileName);
/*  348: 607 */     ze.setName(entryEncoding.decode(fileName), fileName);
/*  349:     */     
/*  350:     */ 
/*  351: 610 */     offset.headerOffset = ZipLong.getValue(this.CFH_BUF, off);
/*  352:     */     
/*  353: 612 */     this.entries.add(ze);
/*  354:     */     
/*  355: 614 */     byte[] cdExtraData = new byte[extraLen];
/*  356: 615 */     this.archive.readFully(cdExtraData);
/*  357: 616 */     ze.setCentralDirectoryExtra(cdExtraData);
/*  358:     */     
/*  359: 618 */     setSizesAndOffsetFromZip64Extra(ze, offset, diskStart);
/*  360:     */     
/*  361: 620 */     byte[] comment = new byte[commentLen];
/*  362: 621 */     this.archive.readFully(comment);
/*  363: 622 */     ze.setComment(entryEncoding.decode(comment));
/*  364: 624 */     if ((!hasUTF8Flag) && (this.useUnicodeExtraFields)) {
/*  365: 625 */       noUTF8Flag.put(ze, new NameAndComment(fileName, comment, null));
/*  366:     */     }
/*  367:     */   }
/*  368:     */   
/*  369:     */   private void setSizesAndOffsetFromZip64Extra(ZipArchiveEntry ze, OffsetEntry offset, int diskStart)
/*  370:     */     throws IOException
/*  371:     */   {
/*  372: 645 */     Zip64ExtendedInformationExtraField z64 = (Zip64ExtendedInformationExtraField)ze.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
/*  373: 648 */     if (z64 != null)
/*  374:     */     {
/*  375: 649 */       boolean hasUncompressedSize = ze.getSize() == 4294967295L;
/*  376: 650 */       boolean hasCompressedSize = ze.getCompressedSize() == 4294967295L;
/*  377: 651 */       boolean hasRelativeHeaderOffset = offset.headerOffset == 4294967295L;
/*  378:     */       
/*  379: 653 */       z64.reparseCentralDirectoryData(hasUncompressedSize, hasCompressedSize, hasRelativeHeaderOffset, diskStart == 65535);
/*  380: 658 */       if (hasUncompressedSize) {
/*  381: 659 */         ze.setSize(z64.getSize().getLongValue());
/*  382: 660 */       } else if (hasCompressedSize) {
/*  383: 661 */         z64.setSize(new ZipEightByteInteger(ze.getSize()));
/*  384:     */       }
/*  385: 664 */       if (hasCompressedSize) {
/*  386: 665 */         ze.setCompressedSize(z64.getCompressedSize().getLongValue());
/*  387: 666 */       } else if (hasUncompressedSize) {
/*  388: 667 */         z64.setCompressedSize(new ZipEightByteInteger(ze.getCompressedSize()));
/*  389:     */       }
/*  390: 670 */       if (hasRelativeHeaderOffset) {
/*  391: 671 */         offset.headerOffset = z64.getRelativeHeaderOffset().getLongValue();
/*  392:     */       }
/*  393:     */     }
/*  394:     */   }
/*  395:     */   
/*  396:     */   private void positionAtCentralDirectory()
/*  397:     */     throws IOException
/*  398:     */   {
/*  399: 777 */     positionAtEndOfCentralDirectoryRecord();
/*  400: 778 */     boolean found = false;
/*  401: 779 */     boolean searchedForZip64EOCD = this.archive.getFilePointer() > 20L;
/*  402: 781 */     if (searchedForZip64EOCD)
/*  403:     */     {
/*  404: 782 */       this.archive.seek(this.archive.getFilePointer() - 20L);
/*  405: 783 */       this.archive.readFully(this.WORD_BUF);
/*  406: 784 */       found = Arrays.equals(ZipArchiveOutputStream.ZIP64_EOCD_LOC_SIG, this.WORD_BUF);
/*  407:     */     }
/*  408: 787 */     if (!found)
/*  409:     */     {
/*  410: 789 */       if (searchedForZip64EOCD) {
/*  411: 790 */         skipBytes(16);
/*  412:     */       }
/*  413: 792 */       positionAtCentralDirectory32();
/*  414:     */     }
/*  415:     */     else
/*  416:     */     {
/*  417: 794 */       positionAtCentralDirectory64();
/*  418:     */     }
/*  419:     */   }
/*  420:     */   
/*  421:     */   private void positionAtCentralDirectory64()
/*  422:     */     throws IOException
/*  423:     */   {
/*  424: 809 */     skipBytes(4);
/*  425:     */     
/*  426: 811 */     this.archive.readFully(this.DWORD_BUF);
/*  427: 812 */     this.archive.seek(ZipEightByteInteger.getLongValue(this.DWORD_BUF));
/*  428: 813 */     this.archive.readFully(this.WORD_BUF);
/*  429: 814 */     if (!Arrays.equals(this.WORD_BUF, ZipArchiveOutputStream.ZIP64_EOCD_SIG)) {
/*  430: 815 */       throw new ZipException("archive's ZIP64 end of central directory locator is corrupt.");
/*  431:     */     }
/*  432: 818 */     skipBytes(44);
/*  433:     */     
/*  434: 820 */     this.archive.readFully(this.DWORD_BUF);
/*  435: 821 */     this.archive.seek(ZipEightByteInteger.getLongValue(this.DWORD_BUF));
/*  436:     */   }
/*  437:     */   
/*  438:     */   private void positionAtCentralDirectory32()
/*  439:     */     throws IOException
/*  440:     */   {
/*  441: 833 */     skipBytes(16);
/*  442: 834 */     this.archive.readFully(this.WORD_BUF);
/*  443: 835 */     this.archive.seek(ZipLong.getValue(this.WORD_BUF));
/*  444:     */   }
/*  445:     */   
/*  446:     */   private void positionAtEndOfCentralDirectoryRecord()
/*  447:     */     throws IOException
/*  448:     */   {
/*  449: 844 */     boolean found = tryToLocateSignature(22L, 65557L, ZipArchiveOutputStream.EOCD_SIG);
/*  450: 846 */     if (!found) {
/*  451: 847 */       throw new ZipException("archive is not a ZIP archive");
/*  452:     */     }
/*  453:     */   }
/*  454:     */   
/*  455:     */   private boolean tryToLocateSignature(long minDistanceFromEnd, long maxDistanceFromEnd, byte[] sig)
/*  456:     */     throws IOException
/*  457:     */   {
/*  458: 859 */     boolean found = false;
/*  459: 860 */     long off = this.archive.length() - minDistanceFromEnd;
/*  460: 861 */     long stopSearching = Math.max(0L, this.archive.length() - maxDistanceFromEnd);
/*  461: 863 */     if (off >= 0L) {
/*  462: 864 */       for (; off >= stopSearching; off -= 1L)
/*  463:     */       {
/*  464: 865 */         this.archive.seek(off);
/*  465: 866 */         int curr = this.archive.read();
/*  466: 867 */         if (curr == -1) {
/*  467:     */           break;
/*  468:     */         }
/*  469: 870 */         if (curr == sig[0])
/*  470:     */         {
/*  471: 871 */           curr = this.archive.read();
/*  472: 872 */           if (curr == sig[1])
/*  473:     */           {
/*  474: 873 */             curr = this.archive.read();
/*  475: 874 */             if (curr == sig[2])
/*  476:     */             {
/*  477: 875 */               curr = this.archive.read();
/*  478: 876 */               if (curr == sig[3])
/*  479:     */               {
/*  480: 877 */                 found = true;
/*  481: 878 */                 break;
/*  482:     */               }
/*  483:     */             }
/*  484:     */           }
/*  485:     */         }
/*  486:     */       }
/*  487:     */     }
/*  488: 885 */     if (found) {
/*  489: 886 */       this.archive.seek(off);
/*  490:     */     }
/*  491: 888 */     return found;
/*  492:     */   }
/*  493:     */   
/*  494:     */   private void skipBytes(int count)
/*  495:     */     throws IOException
/*  496:     */   {
/*  497: 896 */     int totalSkipped = 0;
/*  498: 897 */     while (totalSkipped < count)
/*  499:     */     {
/*  500: 898 */       int skippedNow = this.archive.skipBytes(count - totalSkipped);
/*  501: 899 */       if (skippedNow <= 0) {
/*  502: 900 */         throw new EOFException();
/*  503:     */       }
/*  504: 902 */       totalSkipped += skippedNow;
/*  505:     */     }
/*  506:     */   }
/*  507:     */   
/*  508:     */   private void resolveLocalFileHeaderData(Map<ZipArchiveEntry, NameAndComment> entriesWithoutUTF8Flag)
/*  509:     */     throws IOException
/*  510:     */   {
/*  511: 931 */     for (ZipArchiveEntry zipArchiveEntry : this.entries)
/*  512:     */     {
/*  513: 934 */       Entry ze = (Entry)zipArchiveEntry;
/*  514: 935 */       OffsetEntry offsetEntry = ze.getOffsetEntry();
/*  515: 936 */       long offset = offsetEntry.headerOffset;
/*  516: 937 */       this.archive.seek(offset + 26L);
/*  517: 938 */       this.archive.readFully(this.SHORT_BUF);
/*  518: 939 */       int fileNameLen = ZipShort.getValue(this.SHORT_BUF);
/*  519: 940 */       this.archive.readFully(this.SHORT_BUF);
/*  520: 941 */       int extraFieldLen = ZipShort.getValue(this.SHORT_BUF);
/*  521: 942 */       int lenToSkip = fileNameLen;
/*  522: 943 */       while (lenToSkip > 0)
/*  523:     */       {
/*  524: 944 */         int skipped = this.archive.skipBytes(lenToSkip);
/*  525: 945 */         if (skipped <= 0) {
/*  526: 946 */           throw new IOException("failed to skip file name in local file header");
/*  527:     */         }
/*  528: 949 */         lenToSkip -= skipped;
/*  529:     */       }
/*  530: 951 */       byte[] localExtraData = new byte[extraFieldLen];
/*  531: 952 */       this.archive.readFully(localExtraData);
/*  532: 953 */       ze.setExtra(localExtraData);
/*  533: 954 */       offsetEntry.dataOffset = (offset + 26L + 2L + 2L + fileNameLen + extraFieldLen);
/*  534: 957 */       if (entriesWithoutUTF8Flag.containsKey(ze))
/*  535:     */       {
/*  536: 958 */         NameAndComment nc = (NameAndComment)entriesWithoutUTF8Flag.get(ze);
/*  537: 959 */         ZipUtil.setNameAndCommentFromExtraFields(ze, nc.name, nc.comment);
/*  538:     */       }
/*  539: 963 */       String name = ze.getName();
/*  540: 964 */       LinkedList<ZipArchiveEntry> entriesOfThatName = (LinkedList)this.nameMap.get(name);
/*  541: 965 */       if (entriesOfThatName == null)
/*  542:     */       {
/*  543: 966 */         entriesOfThatName = new LinkedList();
/*  544: 967 */         this.nameMap.put(name, entriesOfThatName);
/*  545:     */       }
/*  546: 969 */       entriesOfThatName.addLast(ze);
/*  547:     */     }
/*  548:     */   }
/*  549:     */   
/*  550:     */   private boolean startsWithLocalFileHeader()
/*  551:     */     throws IOException
/*  552:     */   {
/*  553: 978 */     this.archive.seek(0L);
/*  554: 979 */     this.archive.readFully(this.WORD_BUF);
/*  555: 980 */     return Arrays.equals(this.WORD_BUF, ZipArchiveOutputStream.LFH_SIG);
/*  556:     */   }
/*  557:     */   
/*  558:     */   private class BoundedInputStream
/*  559:     */     extends InputStream
/*  560:     */   {
/*  561:     */     private long remaining;
/*  562:     */     private long loc;
/*  563: 991 */     private boolean addDummyByte = false;
/*  564:     */     
/*  565:     */     BoundedInputStream(long start, long remaining)
/*  566:     */     {
/*  567: 994 */       this.remaining = remaining;
/*  568: 995 */       this.loc = start;
/*  569:     */     }
/*  570:     */     
/*  571:     */     public int read()
/*  572:     */       throws IOException
/*  573:     */     {
/*  574:1000 */       if (this.remaining-- <= 0L)
/*  575:     */       {
/*  576:1001 */         if (this.addDummyByte)
/*  577:     */         {
/*  578:1002 */           this.addDummyByte = false;
/*  579:1003 */           return 0;
/*  580:     */         }
/*  581:1005 */         return -1;
/*  582:     */       }
/*  583:1007 */       synchronized (ZipFile.this.archive)
/*  584:     */       {
/*  585:1008 */         ZipFile.this.archive.seek(this.loc++);
/*  586:1009 */         return ZipFile.this.archive.read();
/*  587:     */       }
/*  588:     */     }
/*  589:     */     
/*  590:     */     public int read(byte[] b, int off, int len)
/*  591:     */       throws IOException
/*  592:     */     {
/*  593:1015 */       if (this.remaining <= 0L)
/*  594:     */       {
/*  595:1016 */         if (this.addDummyByte)
/*  596:     */         {
/*  597:1017 */           this.addDummyByte = false;
/*  598:1018 */           b[off] = 0;
/*  599:1019 */           return 1;
/*  600:     */         }
/*  601:1021 */         return -1;
/*  602:     */       }
/*  603:1024 */       if (len <= 0) {
/*  604:1025 */         return 0;
/*  605:     */       }
/*  606:1028 */       if (len > this.remaining) {
/*  607:1029 */         len = (int)this.remaining;
/*  608:     */       }
/*  609:1031 */       int ret = -1;
/*  610:1032 */       synchronized (ZipFile.this.archive)
/*  611:     */       {
/*  612:1033 */         ZipFile.this.archive.seek(this.loc);
/*  613:1034 */         ret = ZipFile.this.archive.read(b, off, len);
/*  614:     */       }
/*  615:1036 */       if (ret > 0)
/*  616:     */       {
/*  617:1037 */         this.loc += ret;
/*  618:1038 */         this.remaining -= ret;
/*  619:     */       }
/*  620:1040 */       return ret;
/*  621:     */     }
/*  622:     */     
/*  623:     */     void addDummy()
/*  624:     */     {
/*  625:1048 */       this.addDummyByte = true;
/*  626:     */     }
/*  627:     */   }
/*  628:     */   
/*  629:     */   private static final class NameAndComment
/*  630:     */   {
/*  631:     */     private final byte[] name;
/*  632:     */     private final byte[] comment;
/*  633:     */     
/*  634:     */     private NameAndComment(byte[] name, byte[] comment)
/*  635:     */     {
/*  636:1056 */       this.name = name;
/*  637:1057 */       this.comment = comment;
/*  638:     */     }
/*  639:     */   }
/*  640:     */   
/*  641:1069 */   private final Comparator<ZipArchiveEntry> OFFSET_COMPARATOR = new Comparator()
/*  642:     */   {
/*  643:     */     public int compare(ZipArchiveEntry e1, ZipArchiveEntry e2)
/*  644:     */     {
/*  645:1072 */       if (e1 == e2) {
/*  646:1073 */         return 0;
/*  647:     */       }
/*  648:1076 */       ZipFile.Entry ent1 = (e1 instanceof ZipFile.Entry) ? (ZipFile.Entry)e1 : null;
/*  649:1077 */       ZipFile.Entry ent2 = (e2 instanceof ZipFile.Entry) ? (ZipFile.Entry)e2 : null;
/*  650:1078 */       if (ent1 == null) {
/*  651:1079 */         return 1;
/*  652:     */       }
/*  653:1081 */       if (ent2 == null) {
/*  654:1082 */         return -1;
/*  655:     */       }
/*  656:1084 */       long val = ent1.getOffsetEntry().headerOffset - ent2.getOffsetEntry().headerOffset;
/*  657:     */       
/*  658:1086 */       return val < 0L ? -1 : val == 0L ? 0 : 1;
/*  659:     */     }
/*  660:     */   };
/*  661:     */   
/*  662:     */   private static class Entry
/*  663:     */     extends ZipArchiveEntry
/*  664:     */   {
/*  665:     */     private final ZipFile.OffsetEntry offsetEntry;
/*  666:     */     
/*  667:     */     Entry(ZipFile.OffsetEntry offset)
/*  668:     */     {
/*  669:1098 */       this.offsetEntry = offset;
/*  670:     */     }
/*  671:     */     
/*  672:     */     ZipFile.OffsetEntry getOffsetEntry()
/*  673:     */     {
/*  674:1102 */       return this.offsetEntry;
/*  675:     */     }
/*  676:     */     
/*  677:     */     public int hashCode()
/*  678:     */     {
/*  679:1107 */       return 3 * super.hashCode() + (int)(ZipFile.OffsetEntry.access$200(this.offsetEntry) % 2147483647L);
/*  680:     */     }
/*  681:     */     
/*  682:     */     public boolean equals(Object other)
/*  683:     */     {
/*  684:1113 */       if (super.equals(other))
/*  685:     */       {
/*  686:1115 */         Entry otherEntry = (Entry)other;
/*  687:1116 */         return (ZipFile.OffsetEntry.access$200(this.offsetEntry) == ZipFile.OffsetEntry.access$200(otherEntry.offsetEntry)) && (ZipFile.OffsetEntry.access$000(this.offsetEntry) == ZipFile.OffsetEntry.access$000(otherEntry.offsetEntry));
/*  688:     */       }
/*  689:1121 */       return false;
/*  690:     */     }
/*  691:     */   }
/*  692:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.ZipFile
 * JD-Core Version:    0.7.0.1
 */