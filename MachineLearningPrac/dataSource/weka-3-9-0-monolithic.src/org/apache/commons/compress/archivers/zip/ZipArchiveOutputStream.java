/*    1:     */ package org.apache.commons.compress.archivers.zip;
/*    2:     */ 
/*    3:     */ import java.io.ByteArrayOutputStream;
/*    4:     */ import java.io.File;
/*    5:     */ import java.io.FileOutputStream;
/*    6:     */ import java.io.IOException;
/*    7:     */ import java.io.InputStream;
/*    8:     */ import java.io.OutputStream;
/*    9:     */ import java.io.RandomAccessFile;
/*   10:     */ import java.nio.ByteBuffer;
/*   11:     */ import java.util.Calendar;
/*   12:     */ import java.util.HashMap;
/*   13:     */ import java.util.LinkedList;
/*   14:     */ import java.util.List;
/*   15:     */ import java.util.Map;
/*   16:     */ import java.util.zip.Deflater;
/*   17:     */ import java.util.zip.ZipException;
/*   18:     */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*   19:     */ import org.apache.commons.compress.archivers.ArchiveOutputStream;
/*   20:     */ import org.apache.commons.compress.utils.IOUtils;
/*   21:     */ 
/*   22:     */ public class ZipArchiveOutputStream
/*   23:     */   extends ArchiveOutputStream
/*   24:     */ {
/*   25:     */   static final int BUFFER_SIZE = 512;
/*   26:     */   private static final int LFH_SIG_OFFSET = 0;
/*   27:     */   private static final int LFH_VERSION_NEEDED_OFFSET = 4;
/*   28:     */   private static final int LFH_GPB_OFFSET = 6;
/*   29:     */   private static final int LFH_METHOD_OFFSET = 8;
/*   30:     */   private static final int LFH_TIME_OFFSET = 10;
/*   31:     */   private static final int LFH_CRC_OFFSET = 14;
/*   32:     */   private static final int LFH_COMPRESSED_SIZE_OFFSET = 18;
/*   33:     */   private static final int LFH_ORIGINAL_SIZE_OFFSET = 22;
/*   34:     */   private static final int LFH_FILENAME_LENGTH_OFFSET = 26;
/*   35:     */   private static final int LFH_EXTRA_LENGTH_OFFSET = 28;
/*   36:     */   private static final int LFH_FILENAME_OFFSET = 30;
/*   37:     */   private static final int CFH_SIG_OFFSET = 0;
/*   38:     */   private static final int CFH_VERSION_MADE_BY_OFFSET = 4;
/*   39:     */   private static final int CFH_VERSION_NEEDED_OFFSET = 6;
/*   40:     */   private static final int CFH_GPB_OFFSET = 8;
/*   41:     */   private static final int CFH_METHOD_OFFSET = 10;
/*   42:     */   private static final int CFH_TIME_OFFSET = 12;
/*   43:     */   private static final int CFH_CRC_OFFSET = 16;
/*   44:     */   private static final int CFH_COMPRESSED_SIZE_OFFSET = 20;
/*   45:     */   private static final int CFH_ORIGINAL_SIZE_OFFSET = 24;
/*   46:     */   private static final int CFH_FILENAME_LENGTH_OFFSET = 28;
/*   47:     */   private static final int CFH_EXTRA_LENGTH_OFFSET = 30;
/*   48:     */   private static final int CFH_COMMENT_LENGTH_OFFSET = 32;
/*   49:     */   private static final int CFH_DISK_NUMBER_OFFSET = 34;
/*   50:     */   private static final int CFH_INTERNAL_ATTRIBUTES_OFFSET = 36;
/*   51:     */   private static final int CFH_EXTERNAL_ATTRIBUTES_OFFSET = 38;
/*   52:     */   private static final int CFH_LFH_OFFSET = 42;
/*   53:     */   private static final int CFH_FILENAME_OFFSET = 46;
/*   54: 112 */   protected boolean finished = false;
/*   55:     */   private static final int DEFLATER_BLOCK_SIZE = 8192;
/*   56:     */   public static final int DEFLATED = 8;
/*   57:     */   public static final int DEFAULT_COMPRESSION = -1;
/*   58:     */   public static final int STORED = 0;
/*   59:     */   static final String DEFAULT_ENCODING = "UTF8";
/*   60:     */   @Deprecated
/*   61:     */   public static final int EFS_FLAG = 2048;
/*   62: 151 */   private static final byte[] EMPTY = new byte[0];
/*   63:     */   private CurrentEntry entry;
/*   64: 161 */   private String comment = "";
/*   65: 166 */   private int level = -1;
/*   66: 172 */   private boolean hasCompressionLevelChanged = false;
/*   67: 177 */   private int method = 8;
/*   68: 182 */   private final List<ZipArchiveEntry> entries = new LinkedList();
/*   69:     */   private final StreamCompressor streamCompressor;
/*   70: 190 */   private long cdOffset = 0L;
/*   71: 195 */   private long cdLength = 0L;
/*   72: 200 */   private static final byte[] ZERO = { 0, 0 };
/*   73: 205 */   private static final byte[] LZERO = { 0, 0, 0, 0 };
/*   74: 207 */   private static final byte[] ONE = ZipLong.getBytes(1L);
/*   75: 212 */   private final Map<ZipArchiveEntry, Long> offsets = new HashMap();
/*   76: 222 */   private String encoding = "UTF8";
/*   77: 230 */   private ZipEncoding zipEncoding = ZipEncodingHelper.getZipEncoding("UTF8");
/*   78:     */   protected final Deflater def;
/*   79:     */   private final RandomAccessFile raf;
/*   80:     */   private final OutputStream out;
/*   81: 250 */   private boolean useUTF8Flag = true;
/*   82: 255 */   private boolean fallbackToUTF8 = false;
/*   83: 260 */   private UnicodeExtraFieldPolicy createUnicodeExtraFields = UnicodeExtraFieldPolicy.NEVER;
/*   84: 267 */   private boolean hasUsedZip64 = false;
/*   85: 269 */   private Zip64Mode zip64Mode = Zip64Mode.AsNeeded;
/*   86: 271 */   private final byte[] copyBuffer = new byte[32768];
/*   87: 272 */   private final Calendar calendarInstance = Calendar.getInstance();
/*   88:     */   
/*   89:     */   public ZipArchiveOutputStream(OutputStream out)
/*   90:     */   {
/*   91: 279 */     this.out = out;
/*   92: 280 */     this.raf = null;
/*   93: 281 */     this.def = new Deflater(this.level, true);
/*   94: 282 */     this.streamCompressor = StreamCompressor.create(out, this.def);
/*   95:     */   }
/*   96:     */   
/*   97:     */   public ZipArchiveOutputStream(File file)
/*   98:     */     throws IOException
/*   99:     */   {
/*  100: 292 */     OutputStream o = null;
/*  101: 293 */     RandomAccessFile _raf = null;
/*  102:     */     try
/*  103:     */     {
/*  104: 295 */       _raf = new RandomAccessFile(file, "rw");
/*  105: 296 */       _raf.setLength(0L);
/*  106:     */     }
/*  107:     */     catch (IOException e)
/*  108:     */     {
/*  109: 298 */       IOUtils.closeQuietly(_raf);
/*  110: 299 */       _raf = null;
/*  111: 300 */       o = new FileOutputStream(file);
/*  112:     */     }
/*  113: 302 */     this.def = new Deflater(this.level, true);
/*  114: 303 */     this.streamCompressor = StreamCompressor.create(_raf, this.def);
/*  115: 304 */     this.out = o;
/*  116: 305 */     this.raf = _raf;
/*  117:     */   }
/*  118:     */   
/*  119:     */   public boolean isSeekable()
/*  120:     */   {
/*  121: 318 */     return this.raf != null;
/*  122:     */   }
/*  123:     */   
/*  124:     */   public void setEncoding(String encoding)
/*  125:     */   {
/*  126: 331 */     this.encoding = encoding;
/*  127: 332 */     this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
/*  128: 333 */     if ((this.useUTF8Flag) && (!ZipEncodingHelper.isUTF8(encoding))) {
/*  129: 334 */       this.useUTF8Flag = false;
/*  130:     */     }
/*  131:     */   }
/*  132:     */   
/*  133:     */   public String getEncoding()
/*  134:     */   {
/*  135: 344 */     return this.encoding;
/*  136:     */   }
/*  137:     */   
/*  138:     */   public void setUseLanguageEncodingFlag(boolean b)
/*  139:     */   {
/*  140: 357 */     this.useUTF8Flag = ((b) && (ZipEncodingHelper.isUTF8(this.encoding)));
/*  141:     */   }
/*  142:     */   
/*  143:     */   public void setCreateUnicodeExtraFields(UnicodeExtraFieldPolicy b)
/*  144:     */   {
/*  145: 368 */     this.createUnicodeExtraFields = b;
/*  146:     */   }
/*  147:     */   
/*  148:     */   public void setFallbackToUTF8(boolean b)
/*  149:     */   {
/*  150: 382 */     this.fallbackToUTF8 = b;
/*  151:     */   }
/*  152:     */   
/*  153:     */   public void setUseZip64(Zip64Mode mode)
/*  154:     */   {
/*  155: 431 */     this.zip64Mode = mode;
/*  156:     */   }
/*  157:     */   
/*  158:     */   public void finish()
/*  159:     */     throws IOException
/*  160:     */   {
/*  161: 442 */     if (this.finished) {
/*  162: 443 */       throw new IOException("This archive has already been finished");
/*  163:     */     }
/*  164: 446 */     if (this.entry != null) {
/*  165: 447 */       throw new IOException("This archive contains unclosed entries.");
/*  166:     */     }
/*  167: 450 */     this.cdOffset = this.streamCompressor.getTotalBytesWritten();
/*  168: 451 */     writeCentralDirectoryInChunks();
/*  169:     */     
/*  170: 453 */     this.cdLength = (this.streamCompressor.getTotalBytesWritten() - this.cdOffset);
/*  171: 454 */     writeZip64CentralDirectory();
/*  172: 455 */     writeCentralDirectoryEnd();
/*  173: 456 */     this.offsets.clear();
/*  174: 457 */     this.entries.clear();
/*  175: 458 */     this.streamCompressor.close();
/*  176: 459 */     this.finished = true;
/*  177:     */   }
/*  178:     */   
/*  179:     */   private void writeCentralDirectoryInChunks()
/*  180:     */     throws IOException
/*  181:     */   {
/*  182: 463 */     int NUM_PER_WRITE = 1000;
/*  183: 464 */     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(70 * NUM_PER_WRITE);
/*  184: 465 */     int count = 0;
/*  185: 466 */     for (ZipArchiveEntry ze : this.entries)
/*  186:     */     {
/*  187: 467 */       byteArrayOutputStream.write(createCentralFileHeader(ze));
/*  188: 468 */       count++;
/*  189: 468 */       if (count > NUM_PER_WRITE)
/*  190:     */       {
/*  191: 469 */         writeCounted(byteArrayOutputStream.toByteArray());
/*  192: 470 */         byteArrayOutputStream.reset();
/*  193: 471 */         count = 0;
/*  194:     */       }
/*  195:     */     }
/*  196: 474 */     writeCounted(byteArrayOutputStream.toByteArray());
/*  197:     */   }
/*  198:     */   
/*  199:     */   public void closeArchiveEntry()
/*  200:     */     throws IOException
/*  201:     */   {
/*  202: 486 */     preClose();
/*  203:     */     
/*  204: 488 */     flushDeflater();
/*  205:     */     
/*  206: 490 */     long bytesWritten = this.streamCompressor.getTotalBytesWritten() - this.entry.dataStart;
/*  207: 491 */     long realCrc = this.streamCompressor.getCrc32();
/*  208: 492 */     this.entry.bytesRead = this.streamCompressor.getBytesRead();
/*  209: 493 */     Zip64Mode effectiveMode = getEffectiveZip64Mode(this.entry.entry);
/*  210: 494 */     boolean actuallyNeedsZip64 = handleSizesAndCrc(bytesWritten, realCrc, effectiveMode);
/*  211: 495 */     closeEntry(actuallyNeedsZip64, false);
/*  212: 496 */     this.streamCompressor.reset();
/*  213:     */   }
/*  214:     */   
/*  215:     */   private void closeCopiedEntry(boolean phased)
/*  216:     */     throws IOException
/*  217:     */   {
/*  218: 510 */     preClose();
/*  219: 511 */     this.entry.bytesRead = this.entry.entry.getSize();
/*  220: 512 */     Zip64Mode effectiveMode = getEffectiveZip64Mode(this.entry.entry);
/*  221: 513 */     boolean actuallyNeedsZip64 = checkIfNeedsZip64(effectiveMode);
/*  222: 514 */     closeEntry(actuallyNeedsZip64, phased);
/*  223:     */   }
/*  224:     */   
/*  225:     */   private void closeEntry(boolean actuallyNeedsZip64, boolean phased)
/*  226:     */     throws IOException
/*  227:     */   {
/*  228: 518 */     if ((!phased) && (this.raf != null)) {
/*  229: 519 */       rewriteSizesAndCrc(actuallyNeedsZip64);
/*  230:     */     }
/*  231: 522 */     writeDataDescriptor(this.entry.entry);
/*  232: 523 */     this.entry = null;
/*  233:     */   }
/*  234:     */   
/*  235:     */   private void preClose()
/*  236:     */     throws IOException
/*  237:     */   {
/*  238: 527 */     if (this.finished) {
/*  239: 528 */       throw new IOException("Stream has already been finished");
/*  240:     */     }
/*  241: 531 */     if (this.entry == null) {
/*  242: 532 */       throw new IOException("No current entry to close");
/*  243:     */     }
/*  244: 535 */     if (!this.entry.hasWritten) {
/*  245: 536 */       write(EMPTY, 0, 0);
/*  246:     */     }
/*  247:     */   }
/*  248:     */   
/*  249:     */   public void addRawArchiveEntry(ZipArchiveEntry entry, InputStream rawStream)
/*  250:     */     throws IOException
/*  251:     */   {
/*  252: 555 */     ZipArchiveEntry ae = new ZipArchiveEntry(entry);
/*  253: 556 */     if (hasZip64Extra(ae)) {
/*  254: 560 */       ae.removeExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
/*  255:     */     }
/*  256: 562 */     boolean is2PhaseSource = (ae.getCrc() != -1L) && (ae.getSize() != -1L) && (ae.getCompressedSize() != -1L);
/*  257:     */     
/*  258:     */ 
/*  259: 565 */     putArchiveEntry(ae, is2PhaseSource);
/*  260: 566 */     copyFromZipInputStream(rawStream);
/*  261: 567 */     closeCopiedEntry(is2PhaseSource);
/*  262:     */   }
/*  263:     */   
/*  264:     */   private void flushDeflater()
/*  265:     */     throws IOException
/*  266:     */   {
/*  267: 574 */     if (this.entry.entry.getMethod() == 8) {
/*  268: 575 */       this.streamCompressor.flushDeflater();
/*  269:     */     }
/*  270:     */   }
/*  271:     */   
/*  272:     */   private boolean handleSizesAndCrc(long bytesWritten, long crc, Zip64Mode effectiveMode)
/*  273:     */     throws ZipException
/*  274:     */   {
/*  275: 588 */     if (this.entry.entry.getMethod() == 8)
/*  276:     */     {
/*  277: 593 */       this.entry.entry.setSize(this.entry.bytesRead);
/*  278: 594 */       this.entry.entry.setCompressedSize(bytesWritten);
/*  279: 595 */       this.entry.entry.setCrc(crc);
/*  280:     */     }
/*  281: 597 */     else if (this.raf == null)
/*  282:     */     {
/*  283: 598 */       if (this.entry.entry.getCrc() != crc) {
/*  284: 599 */         throw new ZipException("bad CRC checksum for entry " + this.entry.entry.getName() + ": " + Long.toHexString(this.entry.entry.getCrc()) + " instead of " + Long.toHexString(crc));
/*  285:     */       }
/*  286: 606 */       if (this.entry.entry.getSize() != bytesWritten) {
/*  287: 607 */         throw new ZipException("bad size for entry " + this.entry.entry.getName() + ": " + this.entry.entry.getSize() + " instead of " + bytesWritten);
/*  288:     */       }
/*  289:     */     }
/*  290:     */     else
/*  291:     */     {
/*  292: 614 */       this.entry.entry.setSize(bytesWritten);
/*  293: 615 */       this.entry.entry.setCompressedSize(bytesWritten);
/*  294: 616 */       this.entry.entry.setCrc(crc);
/*  295:     */     }
/*  296: 619 */     return checkIfNeedsZip64(effectiveMode);
/*  297:     */   }
/*  298:     */   
/*  299:     */   private boolean checkIfNeedsZip64(Zip64Mode effectiveMode)
/*  300:     */     throws ZipException
/*  301:     */   {
/*  302: 630 */     boolean actuallyNeedsZip64 = isZip64Required(this.entry.entry, effectiveMode);
/*  303: 631 */     if ((actuallyNeedsZip64) && (effectiveMode == Zip64Mode.Never)) {
/*  304: 632 */       throw new Zip64RequiredException(Zip64RequiredException.getEntryTooBigMessage(this.entry.entry));
/*  305:     */     }
/*  306: 634 */     return actuallyNeedsZip64;
/*  307:     */   }
/*  308:     */   
/*  309:     */   private boolean isZip64Required(ZipArchiveEntry entry1, Zip64Mode requestedMode)
/*  310:     */   {
/*  311: 638 */     return (requestedMode == Zip64Mode.Always) || (isTooLageForZip32(entry1));
/*  312:     */   }
/*  313:     */   
/*  314:     */   private boolean isTooLageForZip32(ZipArchiveEntry zipArchiveEntry)
/*  315:     */   {
/*  316: 642 */     return (zipArchiveEntry.getSize() >= 4294967295L) || (zipArchiveEntry.getCompressedSize() >= 4294967295L);
/*  317:     */   }
/*  318:     */   
/*  319:     */   private void rewriteSizesAndCrc(boolean actuallyNeedsZip64)
/*  320:     */     throws IOException
/*  321:     */   {
/*  322: 652 */     long save = this.raf.getFilePointer();
/*  323:     */     
/*  324: 654 */     this.raf.seek(this.entry.localDataStart);
/*  325: 655 */     writeOut(ZipLong.getBytes(this.entry.entry.getCrc()));
/*  326: 656 */     if ((!hasZip64Extra(this.entry.entry)) || (!actuallyNeedsZip64))
/*  327:     */     {
/*  328: 657 */       writeOut(ZipLong.getBytes(this.entry.entry.getCompressedSize()));
/*  329: 658 */       writeOut(ZipLong.getBytes(this.entry.entry.getSize()));
/*  330:     */     }
/*  331:     */     else
/*  332:     */     {
/*  333: 660 */       writeOut(ZipLong.ZIP64_MAGIC.getBytes());
/*  334: 661 */       writeOut(ZipLong.ZIP64_MAGIC.getBytes());
/*  335:     */     }
/*  336: 664 */     if (hasZip64Extra(this.entry.entry))
/*  337:     */     {
/*  338: 665 */       ByteBuffer name = getName(this.entry.entry);
/*  339: 666 */       int nameLen = name.limit() - name.position();
/*  340:     */       
/*  341: 668 */       this.raf.seek(this.entry.localDataStart + 12L + 4L + nameLen + 4L);
/*  342:     */       
/*  343:     */ 
/*  344:     */ 
/*  345: 672 */       writeOut(ZipEightByteInteger.getBytes(this.entry.entry.getSize()));
/*  346: 673 */       writeOut(ZipEightByteInteger.getBytes(this.entry.entry.getCompressedSize()));
/*  347: 675 */       if (!actuallyNeedsZip64)
/*  348:     */       {
/*  349: 678 */         this.raf.seek(this.entry.localDataStart - 10L);
/*  350: 679 */         writeOut(ZipShort.getBytes(10));
/*  351:     */         
/*  352:     */ 
/*  353:     */ 
/*  354: 683 */         this.entry.entry.removeExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
/*  355:     */         
/*  356: 685 */         this.entry.entry.setExtra();
/*  357: 689 */         if (this.entry.causedUseOfZip64) {
/*  358: 690 */           this.hasUsedZip64 = false;
/*  359:     */         }
/*  360:     */       }
/*  361:     */     }
/*  362: 694 */     this.raf.seek(save);
/*  363:     */   }
/*  364:     */   
/*  365:     */   public void putArchiveEntry(ArchiveEntry archiveEntry)
/*  366:     */     throws IOException
/*  367:     */   {
/*  368: 706 */     putArchiveEntry(archiveEntry, false);
/*  369:     */   }
/*  370:     */   
/*  371:     */   private void putArchiveEntry(ArchiveEntry archiveEntry, boolean phased)
/*  372:     */     throws IOException
/*  373:     */   {
/*  374: 722 */     if (this.finished) {
/*  375: 723 */       throw new IOException("Stream has already been finished");
/*  376:     */     }
/*  377: 726 */     if (this.entry != null) {
/*  378: 727 */       closeArchiveEntry();
/*  379:     */     }
/*  380: 730 */     this.entry = new CurrentEntry((ZipArchiveEntry)archiveEntry, null);
/*  381: 731 */     this.entries.add(this.entry.entry);
/*  382:     */     
/*  383: 733 */     setDefaults(this.entry.entry);
/*  384:     */     
/*  385: 735 */     Zip64Mode effectiveMode = getEffectiveZip64Mode(this.entry.entry);
/*  386: 736 */     validateSizeInformation(effectiveMode);
/*  387: 738 */     if (shouldAddZip64Extra(this.entry.entry, effectiveMode))
/*  388:     */     {
/*  389: 740 */       Zip64ExtendedInformationExtraField z64 = getZip64Extra(this.entry.entry);
/*  390:     */       
/*  391:     */ 
/*  392:     */ 
/*  393: 744 */       ZipEightByteInteger size = ZipEightByteInteger.ZERO;
/*  394: 745 */       ZipEightByteInteger compressedSize = ZipEightByteInteger.ZERO;
/*  395: 746 */       if (phased)
/*  396:     */       {
/*  397: 747 */         size = new ZipEightByteInteger(this.entry.entry.getSize());
/*  398: 748 */         compressedSize = new ZipEightByteInteger(this.entry.entry.getCompressedSize());
/*  399:     */       }
/*  400: 749 */       else if ((this.entry.entry.getMethod() == 0) && (this.entry.entry.getSize() != -1L))
/*  401:     */       {
/*  402: 752 */         size = new ZipEightByteInteger(this.entry.entry.getSize());
/*  403: 753 */         compressedSize = size;
/*  404:     */       }
/*  405: 755 */       z64.setSize(size);
/*  406: 756 */       z64.setCompressedSize(compressedSize);
/*  407: 757 */       this.entry.entry.setExtra();
/*  408:     */     }
/*  409: 760 */     if ((this.entry.entry.getMethod() == 8) && (this.hasCompressionLevelChanged))
/*  410:     */     {
/*  411: 761 */       this.def.setLevel(this.level);
/*  412: 762 */       this.hasCompressionLevelChanged = false;
/*  413:     */     }
/*  414: 764 */     writeLocalFileHeader((ZipArchiveEntry)archiveEntry, phased);
/*  415:     */   }
/*  416:     */   
/*  417:     */   private void setDefaults(ZipArchiveEntry entry)
/*  418:     */   {
/*  419: 772 */     if (entry.getMethod() == -1) {
/*  420: 773 */       entry.setMethod(this.method);
/*  421:     */     }
/*  422: 776 */     if (entry.getTime() == -1L) {
/*  423: 777 */       entry.setTime(System.currentTimeMillis());
/*  424:     */     }
/*  425:     */   }
/*  426:     */   
/*  427:     */   private void validateSizeInformation(Zip64Mode effectiveMode)
/*  428:     */     throws ZipException
/*  429:     */   {
/*  430: 790 */     if ((this.entry.entry.getMethod() == 0) && (this.raf == null))
/*  431:     */     {
/*  432: 791 */       if (this.entry.entry.getSize() == -1L) {
/*  433: 792 */         throw new ZipException("uncompressed size is required for STORED method when not writing to a file");
/*  434:     */       }
/*  435: 796 */       if (this.entry.entry.getCrc() == -1L) {
/*  436: 797 */         throw new ZipException("crc checksum is required for STORED method when not writing to a file");
/*  437:     */       }
/*  438: 800 */       this.entry.entry.setCompressedSize(this.entry.entry.getSize());
/*  439:     */     }
/*  440: 803 */     if (((this.entry.entry.getSize() >= 4294967295L) || (this.entry.entry.getCompressedSize() >= 4294967295L)) && (effectiveMode == Zip64Mode.Never)) {
/*  441: 806 */       throw new Zip64RequiredException(Zip64RequiredException.getEntryTooBigMessage(this.entry.entry));
/*  442:     */     }
/*  443:     */   }
/*  444:     */   
/*  445:     */   private boolean shouldAddZip64Extra(ZipArchiveEntry entry, Zip64Mode mode)
/*  446:     */   {
/*  447: 826 */     return (mode == Zip64Mode.Always) || (entry.getSize() >= 4294967295L) || (entry.getCompressedSize() >= 4294967295L) || ((entry.getSize() == -1L) && (this.raf != null) && (mode != Zip64Mode.Never));
/*  448:     */   }
/*  449:     */   
/*  450:     */   public void setComment(String comment)
/*  451:     */   {
/*  452: 838 */     this.comment = comment;
/*  453:     */   }
/*  454:     */   
/*  455:     */   public void setLevel(int level)
/*  456:     */   {
/*  457: 850 */     if ((level < -1) || (level > 9)) {
/*  458: 852 */       throw new IllegalArgumentException("Invalid compression level: " + level);
/*  459:     */     }
/*  460: 855 */     this.hasCompressionLevelChanged = (this.level != level);
/*  461: 856 */     this.level = level;
/*  462:     */   }
/*  463:     */   
/*  464:     */   public void setMethod(int method)
/*  465:     */   {
/*  466: 866 */     this.method = method;
/*  467:     */   }
/*  468:     */   
/*  469:     */   public boolean canWriteEntryData(ArchiveEntry ae)
/*  470:     */   {
/*  471: 878 */     if ((ae instanceof ZipArchiveEntry))
/*  472:     */     {
/*  473: 879 */       ZipArchiveEntry zae = (ZipArchiveEntry)ae;
/*  474: 880 */       return (zae.getMethod() != ZipMethod.IMPLODING.getCode()) && (zae.getMethod() != ZipMethod.UNSHRINKING.getCode()) && (ZipUtil.canHandleEntryData(zae));
/*  475:     */     }
/*  476: 884 */     return false;
/*  477:     */   }
/*  478:     */   
/*  479:     */   public void write(byte[] b, int offset, int length)
/*  480:     */     throws IOException
/*  481:     */   {
/*  482: 896 */     if (this.entry == null) {
/*  483: 897 */       throw new IllegalStateException("No current entry");
/*  484:     */     }
/*  485: 899 */     ZipUtil.checkRequestedFeatures(this.entry.entry);
/*  486: 900 */     long writtenThisTime = this.streamCompressor.write(b, offset, length, this.entry.entry.getMethod());
/*  487: 901 */     count(writtenThisTime);
/*  488:     */   }
/*  489:     */   
/*  490:     */   private void writeCounted(byte[] data)
/*  491:     */     throws IOException
/*  492:     */   {
/*  493: 910 */     this.streamCompressor.writeCounted(data);
/*  494:     */   }
/*  495:     */   
/*  496:     */   private void copyFromZipInputStream(InputStream src)
/*  497:     */     throws IOException
/*  498:     */   {
/*  499: 914 */     if (this.entry == null) {
/*  500: 915 */       throw new IllegalStateException("No current entry");
/*  501:     */     }
/*  502: 917 */     ZipUtil.checkRequestedFeatures(this.entry.entry);
/*  503: 918 */     this.entry.hasWritten = true;
/*  504:     */     int length;
/*  505: 920 */     while ((length = src.read(this.copyBuffer)) >= 0)
/*  506:     */     {
/*  507: 922 */       this.streamCompressor.writeCounted(this.copyBuffer, 0, length);
/*  508: 923 */       count(length);
/*  509:     */     }
/*  510:     */   }
/*  511:     */   
/*  512:     */   public void close()
/*  513:     */     throws IOException
/*  514:     */   {
/*  515: 938 */     if (!this.finished) {
/*  516: 939 */       finish();
/*  517:     */     }
/*  518: 941 */     destroy();
/*  519:     */   }
/*  520:     */   
/*  521:     */   public void flush()
/*  522:     */     throws IOException
/*  523:     */   {
/*  524: 952 */     if (this.out != null) {
/*  525: 953 */       this.out.flush();
/*  526:     */     }
/*  527:     */   }
/*  528:     */   
/*  529: 963 */   static final byte[] LFH_SIG = ZipLong.LFH_SIG.getBytes();
/*  530: 967 */   static final byte[] DD_SIG = ZipLong.DD_SIG.getBytes();
/*  531: 971 */   static final byte[] CFH_SIG = ZipLong.CFH_SIG.getBytes();
/*  532: 975 */   static final byte[] EOCD_SIG = ZipLong.getBytes(101010256L);
/*  533: 979 */   static final byte[] ZIP64_EOCD_SIG = ZipLong.getBytes(101075792L);
/*  534: 983 */   static final byte[] ZIP64_EOCD_LOC_SIG = ZipLong.getBytes(117853008L);
/*  535:     */   
/*  536:     */   protected final void deflate()
/*  537:     */     throws IOException
/*  538:     */   {
/*  539: 990 */     this.streamCompressor.deflate();
/*  540:     */   }
/*  541:     */   
/*  542:     */   protected void writeLocalFileHeader(ZipArchiveEntry ze)
/*  543:     */     throws IOException
/*  544:     */   {
/*  545: 999 */     writeLocalFileHeader(ze, false);
/*  546:     */   }
/*  547:     */   
/*  548:     */   private void writeLocalFileHeader(ZipArchiveEntry ze, boolean phased)
/*  549:     */     throws IOException
/*  550:     */   {
/*  551:1003 */     boolean encodable = this.zipEncoding.canEncode(ze.getName());
/*  552:1004 */     ByteBuffer name = getName(ze);
/*  553:1006 */     if (this.createUnicodeExtraFields != UnicodeExtraFieldPolicy.NEVER) {
/*  554:1007 */       addUnicodeExtraFields(ze, encodable, name);
/*  555:     */     }
/*  556:1010 */     byte[] localHeader = createLocalFileHeader(ze, name, encodable, phased);
/*  557:1011 */     long localHeaderStart = this.streamCompressor.getTotalBytesWritten();
/*  558:1012 */     this.offsets.put(ze, Long.valueOf(localHeaderStart));
/*  559:1013 */     this.entry.localDataStart = (localHeaderStart + 14L);
/*  560:1014 */     writeCounted(localHeader);
/*  561:1015 */     this.entry.dataStart = this.streamCompressor.getTotalBytesWritten();
/*  562:     */   }
/*  563:     */   
/*  564:     */   private byte[] createLocalFileHeader(ZipArchiveEntry ze, ByteBuffer name, boolean encodable, boolean phased)
/*  565:     */   {
/*  566:1021 */     byte[] extra = ze.getLocalFileDataExtra();
/*  567:1022 */     int nameLen = name.limit() - name.position();
/*  568:1023 */     int len = 30 + nameLen + extra.length;
/*  569:1024 */     byte[] buf = new byte[len];
/*  570:     */     
/*  571:1026 */     System.arraycopy(LFH_SIG, 0, buf, 0, 4);
/*  572:     */     
/*  573:     */ 
/*  574:1029 */     int zipMethod = ze.getMethod();
/*  575:1031 */     if ((phased) && (!isZip64Required(this.entry.entry, this.zip64Mode))) {
/*  576:1032 */       ZipShort.putShort(10, buf, 4);
/*  577:     */     } else {
/*  578:1034 */       ZipShort.putShort(versionNeededToExtract(zipMethod, hasZip64Extra(ze)), buf, 4);
/*  579:     */     }
/*  580:1037 */     GeneralPurposeBit generalPurposeBit = getGeneralPurposeBits(zipMethod, (!encodable) && (this.fallbackToUTF8));
/*  581:1038 */     generalPurposeBit.encode(buf, 6);
/*  582:     */     
/*  583:     */ 
/*  584:1041 */     ZipShort.putShort(zipMethod, buf, 8);
/*  585:     */     
/*  586:1043 */     ZipUtil.toDosTime(this.calendarInstance, ze.getTime(), buf, 10);
/*  587:1046 */     if (phased) {
/*  588:1047 */       ZipLong.putLong(ze.getCrc(), buf, 14);
/*  589:1048 */     } else if ((zipMethod == 8) || (this.raf != null)) {
/*  590:1049 */       System.arraycopy(LZERO, 0, buf, 14, 4);
/*  591:     */     } else {
/*  592:1051 */       ZipLong.putLong(ze.getCrc(), buf, 14);
/*  593:     */     }
/*  594:1056 */     if (hasZip64Extra(this.entry.entry))
/*  595:     */     {
/*  596:1060 */       ZipLong.ZIP64_MAGIC.putLong(buf, 18);
/*  597:1061 */       ZipLong.ZIP64_MAGIC.putLong(buf, 22);
/*  598:     */     }
/*  599:1062 */     else if (phased)
/*  600:     */     {
/*  601:1063 */       ZipLong.putLong(ze.getCompressedSize(), buf, 18);
/*  602:1064 */       ZipLong.putLong(ze.getSize(), buf, 22);
/*  603:     */     }
/*  604:1065 */     else if ((zipMethod == 8) || (this.raf != null))
/*  605:     */     {
/*  606:1066 */       System.arraycopy(LZERO, 0, buf, 18, 4);
/*  607:1067 */       System.arraycopy(LZERO, 0, buf, 22, 4);
/*  608:     */     }
/*  609:     */     else
/*  610:     */     {
/*  611:1069 */       ZipLong.putLong(ze.getSize(), buf, 18);
/*  612:1070 */       ZipLong.putLong(ze.getSize(), buf, 22);
/*  613:     */     }
/*  614:1073 */     ZipShort.putShort(nameLen, buf, 26);
/*  615:     */     
/*  616:     */ 
/*  617:1076 */     ZipShort.putShort(extra.length, buf, 28);
/*  618:     */     
/*  619:     */ 
/*  620:1079 */     System.arraycopy(name.array(), name.arrayOffset(), buf, 30, nameLen);
/*  621:     */     
/*  622:1081 */     System.arraycopy(extra, 0, buf, 30 + nameLen, extra.length);
/*  623:1082 */     return buf;
/*  624:     */   }
/*  625:     */   
/*  626:     */   private void addUnicodeExtraFields(ZipArchiveEntry ze, boolean encodable, ByteBuffer name)
/*  627:     */     throws IOException
/*  628:     */   {
/*  629:1094 */     if ((this.createUnicodeExtraFields == UnicodeExtraFieldPolicy.ALWAYS) || (!encodable)) {
/*  630:1096 */       ze.addExtraField(new UnicodePathExtraField(ze.getName(), name.array(), name.arrayOffset(), name.limit() - name.position()));
/*  631:     */     }
/*  632:1103 */     String comm = ze.getComment();
/*  633:1104 */     if ((comm != null) && (!"".equals(comm)))
/*  634:     */     {
/*  635:1106 */       boolean commentEncodable = this.zipEncoding.canEncode(comm);
/*  636:1108 */       if ((this.createUnicodeExtraFields == UnicodeExtraFieldPolicy.ALWAYS) || (!commentEncodable))
/*  637:     */       {
/*  638:1110 */         ByteBuffer commentB = getEntryEncoding(ze).encode(comm);
/*  639:1111 */         ze.addExtraField(new UnicodeCommentExtraField(comm, commentB.array(), commentB.arrayOffset(), commentB.limit() - commentB.position()));
/*  640:     */       }
/*  641:     */     }
/*  642:     */   }
/*  643:     */   
/*  644:     */   protected void writeDataDescriptor(ZipArchiveEntry ze)
/*  645:     */     throws IOException
/*  646:     */   {
/*  647:1127 */     if ((ze.getMethod() != 8) || (this.raf != null)) {
/*  648:1128 */       return;
/*  649:     */     }
/*  650:1130 */     writeCounted(DD_SIG);
/*  651:1131 */     writeCounted(ZipLong.getBytes(ze.getCrc()));
/*  652:1132 */     if (!hasZip64Extra(ze))
/*  653:     */     {
/*  654:1133 */       writeCounted(ZipLong.getBytes(ze.getCompressedSize()));
/*  655:1134 */       writeCounted(ZipLong.getBytes(ze.getSize()));
/*  656:     */     }
/*  657:     */     else
/*  658:     */     {
/*  659:1136 */       writeCounted(ZipEightByteInteger.getBytes(ze.getCompressedSize()));
/*  660:1137 */       writeCounted(ZipEightByteInteger.getBytes(ze.getSize()));
/*  661:     */     }
/*  662:     */   }
/*  663:     */   
/*  664:     */   protected void writeCentralFileHeader(ZipArchiveEntry ze)
/*  665:     */     throws IOException
/*  666:     */   {
/*  667:1150 */     byte[] centralFileHeader = createCentralFileHeader(ze);
/*  668:1151 */     writeCounted(centralFileHeader);
/*  669:     */   }
/*  670:     */   
/*  671:     */   private byte[] createCentralFileHeader(ZipArchiveEntry ze)
/*  672:     */     throws IOException
/*  673:     */   {
/*  674:1156 */     long lfhOffset = ((Long)this.offsets.get(ze)).longValue();
/*  675:1157 */     boolean needsZip64Extra = (hasZip64Extra(ze)) || (ze.getCompressedSize() >= 4294967295L) || (ze.getSize() >= 4294967295L) || (lfhOffset >= 4294967295L);
/*  676:1162 */     if ((needsZip64Extra) && (this.zip64Mode == Zip64Mode.Never)) {
/*  677:1166 */       throw new Zip64RequiredException("archive's size exceeds the limit of 4GByte.");
/*  678:     */     }
/*  679:1171 */     handleZip64Extra(ze, lfhOffset, needsZip64Extra);
/*  680:     */     
/*  681:1173 */     return createCentralFileHeader(ze, getName(ze), lfhOffset, needsZip64Extra);
/*  682:     */   }
/*  683:     */   
/*  684:     */   private byte[] createCentralFileHeader(ZipArchiveEntry ze, ByteBuffer name, long lfhOffset, boolean needsZip64Extra)
/*  685:     */     throws IOException
/*  686:     */   {
/*  687:1185 */     byte[] extra = ze.getCentralDirectoryExtra();
/*  688:     */     
/*  689:     */ 
/*  690:1188 */     String comm = ze.getComment();
/*  691:1189 */     if (comm == null) {
/*  692:1190 */       comm = "";
/*  693:     */     }
/*  694:1193 */     ByteBuffer commentB = getEntryEncoding(ze).encode(comm);
/*  695:1194 */     int nameLen = name.limit() - name.position();
/*  696:1195 */     int commentLen = commentB.limit() - commentB.position();
/*  697:1196 */     int len = 46 + nameLen + extra.length + commentLen;
/*  698:1197 */     byte[] buf = new byte[len];
/*  699:     */     
/*  700:1199 */     System.arraycopy(CFH_SIG, 0, buf, 0, 4);
/*  701:     */     
/*  702:     */ 
/*  703:     */ 
/*  704:1203 */     ZipShort.putShort(ze.getPlatform() << 8 | (!this.hasUsedZip64 ? 20 : 45), buf, 4);
/*  705:     */     
/*  706:     */ 
/*  707:1206 */     int zipMethod = ze.getMethod();
/*  708:1207 */     boolean encodable = this.zipEncoding.canEncode(ze.getName());
/*  709:1208 */     ZipShort.putShort(versionNeededToExtract(zipMethod, needsZip64Extra), buf, 6);
/*  710:1209 */     getGeneralPurposeBits(zipMethod, (!encodable) && (this.fallbackToUTF8)).encode(buf, 8);
/*  711:     */     
/*  712:     */ 
/*  713:1212 */     ZipShort.putShort(zipMethod, buf, 10);
/*  714:     */     
/*  715:     */ 
/*  716:     */ 
/*  717:1216 */     ZipUtil.toDosTime(this.calendarInstance, ze.getTime(), buf, 12);
/*  718:     */     
/*  719:     */ 
/*  720:     */ 
/*  721:     */ 
/*  722:1221 */     ZipLong.putLong(ze.getCrc(), buf, 16);
/*  723:1222 */     if ((ze.getCompressedSize() >= 4294967295L) || (ze.getSize() >= 4294967295L))
/*  724:     */     {
/*  725:1224 */       ZipLong.ZIP64_MAGIC.putLong(buf, 20);
/*  726:1225 */       ZipLong.ZIP64_MAGIC.putLong(buf, 24);
/*  727:     */     }
/*  728:     */     else
/*  729:     */     {
/*  730:1227 */       ZipLong.putLong(ze.getCompressedSize(), buf, 20);
/*  731:1228 */       ZipLong.putLong(ze.getSize(), buf, 24);
/*  732:     */     }
/*  733:1231 */     ZipShort.putShort(nameLen, buf, 28);
/*  734:     */     
/*  735:     */ 
/*  736:1234 */     ZipShort.putShort(extra.length, buf, 30);
/*  737:     */     
/*  738:1236 */     ZipShort.putShort(commentLen, buf, 32);
/*  739:     */     
/*  740:     */ 
/*  741:1239 */     System.arraycopy(ZERO, 0, buf, 34, 2);
/*  742:     */     
/*  743:     */ 
/*  744:1242 */     ZipShort.putShort(ze.getInternalAttributes(), buf, 36);
/*  745:     */     
/*  746:     */ 
/*  747:1245 */     ZipLong.putLong(ze.getExternalAttributes(), buf, 38);
/*  748:     */     
/*  749:     */ 
/*  750:1248 */     ZipLong.putLong(Math.min(lfhOffset, 4294967295L), buf, 42);
/*  751:     */     
/*  752:     */ 
/*  753:1251 */     System.arraycopy(name.array(), name.arrayOffset(), buf, 46, nameLen);
/*  754:     */     
/*  755:1253 */     int extraStart = 46 + nameLen;
/*  756:1254 */     System.arraycopy(extra, 0, buf, extraStart, extra.length);
/*  757:     */     
/*  758:1256 */     int commentStart = extraStart + extra.length;
/*  759:     */     
/*  760:     */ 
/*  761:1259 */     System.arraycopy(commentB.array(), commentB.arrayOffset(), buf, commentStart, commentLen);
/*  762:1260 */     return buf;
/*  763:     */   }
/*  764:     */   
/*  765:     */   private void handleZip64Extra(ZipArchiveEntry ze, long lfhOffset, boolean needsZip64Extra)
/*  766:     */   {
/*  767:1269 */     if (needsZip64Extra)
/*  768:     */     {
/*  769:1270 */       Zip64ExtendedInformationExtraField z64 = getZip64Extra(ze);
/*  770:1271 */       if ((ze.getCompressedSize() >= 4294967295L) || (ze.getSize() >= 4294967295L))
/*  771:     */       {
/*  772:1273 */         z64.setCompressedSize(new ZipEightByteInteger(ze.getCompressedSize()));
/*  773:1274 */         z64.setSize(new ZipEightByteInteger(ze.getSize()));
/*  774:     */       }
/*  775:     */       else
/*  776:     */       {
/*  777:1277 */         z64.setCompressedSize(null);
/*  778:1278 */         z64.setSize(null);
/*  779:     */       }
/*  780:1280 */       if (lfhOffset >= 4294967295L) {
/*  781:1281 */         z64.setRelativeHeaderOffset(new ZipEightByteInteger(lfhOffset));
/*  782:     */       }
/*  783:1283 */       ze.setExtra();
/*  784:     */     }
/*  785:     */   }
/*  786:     */   
/*  787:     */   protected void writeCentralDirectoryEnd()
/*  788:     */     throws IOException
/*  789:     */   {
/*  790:1295 */     writeCounted(EOCD_SIG);
/*  791:     */     
/*  792:     */ 
/*  793:1298 */     writeCounted(ZERO);
/*  794:1299 */     writeCounted(ZERO);
/*  795:     */     
/*  796:     */ 
/*  797:1302 */     int numberOfEntries = this.entries.size();
/*  798:1303 */     if ((numberOfEntries > 65535) && (this.zip64Mode == Zip64Mode.Never)) {
/*  799:1305 */       throw new Zip64RequiredException("archive contains more than 65535 entries.");
/*  800:     */     }
/*  801:1308 */     if ((this.cdOffset > 4294967295L) && (this.zip64Mode == Zip64Mode.Never)) {
/*  802:1309 */       throw new Zip64RequiredException("archive's size exceeds the limit of 4GByte.");
/*  803:     */     }
/*  804:1313 */     byte[] num = ZipShort.getBytes(Math.min(numberOfEntries, 65535));
/*  805:     */     
/*  806:1315 */     writeCounted(num);
/*  807:1316 */     writeCounted(num);
/*  808:     */     
/*  809:     */ 
/*  810:1319 */     writeCounted(ZipLong.getBytes(Math.min(this.cdLength, 4294967295L)));
/*  811:1320 */     writeCounted(ZipLong.getBytes(Math.min(this.cdOffset, 4294967295L)));
/*  812:     */     
/*  813:     */ 
/*  814:1323 */     ByteBuffer data = this.zipEncoding.encode(this.comment);
/*  815:1324 */     int dataLen = data.limit() - data.position();
/*  816:1325 */     writeCounted(ZipShort.getBytes(dataLen));
/*  817:1326 */     this.streamCompressor.writeCounted(data.array(), data.arrayOffset(), dataLen);
/*  818:     */   }
/*  819:     */   
/*  820:     */   protected void writeZip64CentralDirectory()
/*  821:     */     throws IOException
/*  822:     */   {
/*  823:1336 */     if (this.zip64Mode == Zip64Mode.Never) {
/*  824:1337 */       return;
/*  825:     */     }
/*  826:1340 */     if ((!this.hasUsedZip64) && ((this.cdOffset >= 4294967295L) || (this.cdLength >= 4294967295L) || (this.entries.size() >= 65535))) {
/*  827:1344 */       this.hasUsedZip64 = true;
/*  828:     */     }
/*  829:1347 */     if (!this.hasUsedZip64) {
/*  830:1348 */       return;
/*  831:     */     }
/*  832:1351 */     long offset = this.streamCompressor.getTotalBytesWritten();
/*  833:     */     
/*  834:1353 */     writeOut(ZIP64_EOCD_SIG);
/*  835:     */     
/*  836:     */ 
/*  837:1356 */     writeOut(ZipEightByteInteger.getBytes(44L));
/*  838:     */     
/*  839:     */ 
/*  840:     */ 
/*  841:     */ 
/*  842:     */ 
/*  843:     */ 
/*  844:     */ 
/*  845:     */ 
/*  846:     */ 
/*  847:     */ 
/*  848:     */ 
/*  849:1368 */     writeOut(ZipShort.getBytes(45));
/*  850:1369 */     writeOut(ZipShort.getBytes(45));
/*  851:     */     
/*  852:     */ 
/*  853:1372 */     writeOut(LZERO);
/*  854:1373 */     writeOut(LZERO);
/*  855:     */     
/*  856:     */ 
/*  857:1376 */     byte[] num = ZipEightByteInteger.getBytes(this.entries.size());
/*  858:1377 */     writeOut(num);
/*  859:1378 */     writeOut(num);
/*  860:     */     
/*  861:     */ 
/*  862:1381 */     writeOut(ZipEightByteInteger.getBytes(this.cdLength));
/*  863:1382 */     writeOut(ZipEightByteInteger.getBytes(this.cdOffset));
/*  864:     */     
/*  865:     */ 
/*  866:     */ 
/*  867:     */ 
/*  868:1387 */     writeOut(ZIP64_EOCD_LOC_SIG);
/*  869:     */     
/*  870:     */ 
/*  871:1390 */     writeOut(LZERO);
/*  872:     */     
/*  873:1392 */     writeOut(ZipEightByteInteger.getBytes(offset));
/*  874:     */     
/*  875:1394 */     writeOut(ONE);
/*  876:     */   }
/*  877:     */   
/*  878:     */   protected final void writeOut(byte[] data)
/*  879:     */     throws IOException
/*  880:     */   {
/*  881:1403 */     this.streamCompressor.writeOut(data, 0, data.length);
/*  882:     */   }
/*  883:     */   
/*  884:     */   protected final void writeOut(byte[] data, int offset, int length)
/*  885:     */     throws IOException
/*  886:     */   {
/*  887:1416 */     this.streamCompressor.writeOut(data, offset, length);
/*  888:     */   }
/*  889:     */   
/*  890:     */   private GeneralPurposeBit getGeneralPurposeBits(int zipMethod, boolean utfFallback)
/*  891:     */   {
/*  892:1421 */     GeneralPurposeBit b = new GeneralPurposeBit();
/*  893:1422 */     b.useUTF8ForNames((this.useUTF8Flag) || (utfFallback));
/*  894:1423 */     if (isDeflatedToOutputStream(zipMethod)) {
/*  895:1424 */       b.useDataDescriptor(true);
/*  896:     */     }
/*  897:1426 */     return b;
/*  898:     */   }
/*  899:     */   
/*  900:     */   private int versionNeededToExtract(int zipMethod, boolean zip64)
/*  901:     */   {
/*  902:1430 */     if (zip64) {
/*  903:1431 */       return 45;
/*  904:     */     }
/*  905:1435 */     return isDeflatedToOutputStream(zipMethod) ? 20 : 10;
/*  906:     */   }
/*  907:     */   
/*  908:     */   private boolean isDeflatedToOutputStream(int zipMethod)
/*  909:     */   {
/*  910:1441 */     return (zipMethod == 8) && (this.raf == null);
/*  911:     */   }
/*  912:     */   
/*  913:     */   public ArchiveEntry createArchiveEntry(File inputFile, String entryName)
/*  914:     */     throws IOException
/*  915:     */   {
/*  916:1459 */     if (this.finished) {
/*  917:1460 */       throw new IOException("Stream has already been finished");
/*  918:     */     }
/*  919:1462 */     return new ZipArchiveEntry(inputFile, entryName);
/*  920:     */   }
/*  921:     */   
/*  922:     */   private Zip64ExtendedInformationExtraField getZip64Extra(ZipArchiveEntry ze)
/*  923:     */   {
/*  924:1473 */     if (this.entry != null) {
/*  925:1474 */       this.entry.causedUseOfZip64 = (!this.hasUsedZip64);
/*  926:     */     }
/*  927:1476 */     this.hasUsedZip64 = true;
/*  928:1477 */     Zip64ExtendedInformationExtraField z64 = (Zip64ExtendedInformationExtraField)ze.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
/*  929:1481 */     if (z64 == null) {
/*  930:1488 */       z64 = new Zip64ExtendedInformationExtraField();
/*  931:     */     }
/*  932:1492 */     ze.addAsFirstExtraField(z64);
/*  933:     */     
/*  934:1494 */     return z64;
/*  935:     */   }
/*  936:     */   
/*  937:     */   private boolean hasZip64Extra(ZipArchiveEntry ze)
/*  938:     */   {
/*  939:1504 */     return ze.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID) != null;
/*  940:     */   }
/*  941:     */   
/*  942:     */   private Zip64Mode getEffectiveZip64Mode(ZipArchiveEntry ze)
/*  943:     */   {
/*  944:1517 */     if ((this.zip64Mode != Zip64Mode.AsNeeded) || (this.raf != null) || (ze.getMethod() != 8) || (ze.getSize() != -1L)) {
/*  945:1521 */       return this.zip64Mode;
/*  946:     */     }
/*  947:1523 */     return Zip64Mode.Never;
/*  948:     */   }
/*  949:     */   
/*  950:     */   private ZipEncoding getEntryEncoding(ZipArchiveEntry ze)
/*  951:     */   {
/*  952:1527 */     boolean encodable = this.zipEncoding.canEncode(ze.getName());
/*  953:1528 */     return (!encodable) && (this.fallbackToUTF8) ? ZipEncodingHelper.UTF8_ZIP_ENCODING : this.zipEncoding;
/*  954:     */   }
/*  955:     */   
/*  956:     */   private ByteBuffer getName(ZipArchiveEntry ze)
/*  957:     */     throws IOException
/*  958:     */   {
/*  959:1533 */     return getEntryEncoding(ze).encode(ze.getName());
/*  960:     */   }
/*  961:     */   
/*  962:     */   void destroy()
/*  963:     */     throws IOException
/*  964:     */   {
/*  965:1544 */     if (this.raf != null) {
/*  966:1545 */       this.raf.close();
/*  967:     */     }
/*  968:1547 */     if (this.out != null) {
/*  969:1548 */       this.out.close();
/*  970:     */     }
/*  971:     */   }
/*  972:     */   
/*  973:     */   public static final class UnicodeExtraFieldPolicy
/*  974:     */   {
/*  975:1560 */     public static final UnicodeExtraFieldPolicy ALWAYS = new UnicodeExtraFieldPolicy("always");
/*  976:1564 */     public static final UnicodeExtraFieldPolicy NEVER = new UnicodeExtraFieldPolicy("never");
/*  977:1569 */     public static final UnicodeExtraFieldPolicy NOT_ENCODEABLE = new UnicodeExtraFieldPolicy("not encodeable");
/*  978:     */     private final String name;
/*  979:     */     
/*  980:     */     private UnicodeExtraFieldPolicy(String n)
/*  981:     */     {
/*  982:1574 */       this.name = n;
/*  983:     */     }
/*  984:     */     
/*  985:     */     public String toString()
/*  986:     */     {
/*  987:1578 */       return this.name;
/*  988:     */     }
/*  989:     */   }
/*  990:     */   
/*  991:     */   private static final class CurrentEntry
/*  992:     */   {
/*  993:     */     private final ZipArchiveEntry entry;
/*  994:     */     
/*  995:     */     private CurrentEntry(ZipArchiveEntry entry)
/*  996:     */     {
/*  997:1588 */       this.entry = entry;
/*  998:     */     }
/*  999:     */     
/* 1000:1598 */     private long localDataStart = 0L;
/* 1001:1602 */     private long dataStart = 0L;
/* 1002:1607 */     private long bytesRead = 0L;
/* 1003:1611 */     private boolean causedUseOfZip64 = false;
/* 1004:     */     private boolean hasWritten;
/* 1005:     */   }
/* 1006:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
 * JD-Core Version:    0.7.0.1
 */