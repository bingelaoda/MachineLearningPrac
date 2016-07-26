/*    1:     */ package org.apache.commons.compress.archivers.zip;
/*    2:     */ 
/*    3:     */ import java.io.ByteArrayInputStream;
/*    4:     */ import java.io.ByteArrayOutputStream;
/*    5:     */ import java.io.EOFException;
/*    6:     */ import java.io.IOException;
/*    7:     */ import java.io.InputStream;
/*    8:     */ import java.io.PushbackInputStream;
/*    9:     */ import java.nio.Buffer;
/*   10:     */ import java.nio.ByteBuffer;
/*   11:     */ import java.util.zip.CRC32;
/*   12:     */ import java.util.zip.DataFormatException;
/*   13:     */ import java.util.zip.Inflater;
/*   14:     */ import java.util.zip.ZipException;
/*   15:     */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*   16:     */ import org.apache.commons.compress.archivers.ArchiveInputStream;
/*   17:     */ import org.apache.commons.compress.utils.IOUtils;
/*   18:     */ 
/*   19:     */ public class ZipArchiveInputStream
/*   20:     */   extends ArchiveInputStream
/*   21:     */ {
/*   22:     */   private final ZipEncoding zipEncoding;
/*   23:     */   final String encoding;
/*   24:     */   private final boolean useUnicodeExtraFields;
/*   25:     */   private final InputStream in;
/*   26:  90 */   private final Inflater inf = new Inflater(true);
/*   27:  93 */   private final ByteBuffer buf = ByteBuffer.allocate(512);
/*   28:  96 */   private CurrentEntry current = null;
/*   29:  99 */   private boolean closed = false;
/*   30: 102 */   private boolean hitCentralDirectory = false;
/*   31: 109 */   private ByteArrayInputStream lastStoredEntry = null;
/*   32: 112 */   private boolean allowStoredEntriesWithDataDescriptor = false;
/*   33:     */   private static final int LFH_LEN = 30;
/*   34:     */   private static final int CFH_LEN = 46;
/*   35:     */   private static final long TWO_EXP_32 = 4294967296L;
/*   36: 153 */   private final byte[] LFH_BUF = new byte[30];
/*   37: 154 */   private final byte[] SKIP_BUF = new byte[1024];
/*   38: 155 */   private final byte[] SHORT_BUF = new byte[2];
/*   39: 156 */   private final byte[] WORD_BUF = new byte[4];
/*   40: 157 */   private final byte[] TWO_DWORD_BUF = new byte[16];
/*   41: 159 */   private int entriesRead = 0;
/*   42:     */   
/*   43:     */   public ZipArchiveInputStream(InputStream inputStream)
/*   44:     */   {
/*   45: 166 */     this(inputStream, "UTF8");
/*   46:     */   }
/*   47:     */   
/*   48:     */   public ZipArchiveInputStream(InputStream inputStream, String encoding)
/*   49:     */   {
/*   50: 177 */     this(inputStream, encoding, true);
/*   51:     */   }
/*   52:     */   
/*   53:     */   public ZipArchiveInputStream(InputStream inputStream, String encoding, boolean useUnicodeExtraFields)
/*   54:     */   {
/*   55: 189 */     this(inputStream, encoding, useUnicodeExtraFields, false);
/*   56:     */   }
/*   57:     */   
/*   58:     */   public ZipArchiveInputStream(InputStream inputStream, String encoding, boolean useUnicodeExtraFields, boolean allowStoredEntriesWithDataDescriptor)
/*   59:     */   {
/*   60: 207 */     this.encoding = encoding;
/*   61: 208 */     this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
/*   62: 209 */     this.useUnicodeExtraFields = useUnicodeExtraFields;
/*   63: 210 */     this.in = new PushbackInputStream(inputStream, this.buf.capacity());
/*   64: 211 */     this.allowStoredEntriesWithDataDescriptor = allowStoredEntriesWithDataDescriptor;
/*   65:     */     
/*   66:     */ 
/*   67: 214 */     this.buf.limit(0);
/*   68:     */   }
/*   69:     */   
/*   70:     */   public ZipArchiveEntry getNextZipEntry()
/*   71:     */     throws IOException
/*   72:     */   {
/*   73: 218 */     boolean firstEntry = true;
/*   74: 219 */     if ((this.closed) || (this.hitCentralDirectory)) {
/*   75: 220 */       return null;
/*   76:     */     }
/*   77: 222 */     if (this.current != null)
/*   78:     */     {
/*   79: 223 */       closeEntry();
/*   80: 224 */       firstEntry = false;
/*   81:     */     }
/*   82:     */     try
/*   83:     */     {
/*   84: 228 */       if (firstEntry) {
/*   85: 233 */         readFirstLocalFileHeader(this.LFH_BUF);
/*   86:     */       } else {
/*   87: 235 */         readFully(this.LFH_BUF);
/*   88:     */       }
/*   89:     */     }
/*   90:     */     catch (EOFException e)
/*   91:     */     {
/*   92: 238 */       return null;
/*   93:     */     }
/*   94: 241 */     ZipLong sig = new ZipLong(this.LFH_BUF);
/*   95: 242 */     if ((sig.equals(ZipLong.CFH_SIG)) || (sig.equals(ZipLong.AED_SIG)))
/*   96:     */     {
/*   97: 243 */       this.hitCentralDirectory = true;
/*   98: 244 */       skipRemainderOfArchive();
/*   99:     */     }
/*  100: 246 */     if (!sig.equals(ZipLong.LFH_SIG)) {
/*  101: 247 */       return null;
/*  102:     */     }
/*  103: 250 */     int off = 4;
/*  104: 251 */     this.current = new CurrentEntry(null);
/*  105:     */     
/*  106: 253 */     int versionMadeBy = ZipShort.getValue(this.LFH_BUF, off);
/*  107: 254 */     off += 2;
/*  108: 255 */     this.current.entry.setPlatform(versionMadeBy >> 8 & 0xF);
/*  109:     */     
/*  110: 257 */     GeneralPurposeBit gpFlag = GeneralPurposeBit.parse(this.LFH_BUF, off);
/*  111: 258 */     boolean hasUTF8Flag = gpFlag.usesUTF8ForNames();
/*  112: 259 */     ZipEncoding entryEncoding = hasUTF8Flag ? ZipEncodingHelper.UTF8_ZIP_ENCODING : this.zipEncoding;
/*  113: 260 */     this.current.hasDataDescriptor = gpFlag.usesDataDescriptor();
/*  114: 261 */     this.current.entry.setGeneralPurposeBit(gpFlag);
/*  115:     */     
/*  116: 263 */     off += 2;
/*  117:     */     
/*  118: 265 */     this.current.entry.setMethod(ZipShort.getValue(this.LFH_BUF, off));
/*  119: 266 */     off += 2;
/*  120:     */     
/*  121: 268 */     long time = ZipUtil.dosToJavaTime(ZipLong.getValue(this.LFH_BUF, off));
/*  122: 269 */     this.current.entry.setTime(time);
/*  123: 270 */     off += 4;
/*  124:     */     
/*  125: 272 */     ZipLong size = null;ZipLong cSize = null;
/*  126: 273 */     if (!this.current.hasDataDescriptor)
/*  127:     */     {
/*  128: 274 */       this.current.entry.setCrc(ZipLong.getValue(this.LFH_BUF, off));
/*  129: 275 */       off += 4;
/*  130:     */       
/*  131: 277 */       cSize = new ZipLong(this.LFH_BUF, off);
/*  132: 278 */       off += 4;
/*  133:     */       
/*  134: 280 */       size = new ZipLong(this.LFH_BUF, off);
/*  135: 281 */       off += 4;
/*  136:     */     }
/*  137:     */     else
/*  138:     */     {
/*  139: 283 */       off += 12;
/*  140:     */     }
/*  141: 286 */     int fileNameLen = ZipShort.getValue(this.LFH_BUF, off);
/*  142:     */     
/*  143: 288 */     off += 2;
/*  144:     */     
/*  145: 290 */     int extraLen = ZipShort.getValue(this.LFH_BUF, off);
/*  146: 291 */     off += 2;
/*  147:     */     
/*  148: 293 */     byte[] fileName = new byte[fileNameLen];
/*  149: 294 */     readFully(fileName);
/*  150: 295 */     this.current.entry.setName(entryEncoding.decode(fileName), fileName);
/*  151:     */     
/*  152: 297 */     byte[] extraData = new byte[extraLen];
/*  153: 298 */     readFully(extraData);
/*  154: 299 */     this.current.entry.setExtra(extraData);
/*  155: 301 */     if ((!hasUTF8Flag) && (this.useUnicodeExtraFields)) {
/*  156: 302 */       ZipUtil.setNameAndCommentFromExtraFields(this.current.entry, fileName, null);
/*  157:     */     }
/*  158: 305 */     processZip64Extra(size, cSize);
/*  159: 307 */     if (this.current.entry.getCompressedSize() != -1L) {
/*  160: 308 */       if (this.current.entry.getMethod() == ZipMethod.UNSHRINKING.getCode()) {
/*  161: 309 */         this.current.in = new UnshrinkingInputStream(new BoundedInputStream(this.in, this.current.entry.getCompressedSize()));
/*  162: 310 */       } else if (this.current.entry.getMethod() == ZipMethod.IMPLODING.getCode()) {
/*  163: 311 */         this.current.in = new ExplodingInputStream(this.current.entry.getGeneralPurposeBit().getSlidingDictionarySize(), this.current.entry.getGeneralPurposeBit().getNumberOfShannonFanoTrees(), new BoundedInputStream(this.in, this.current.entry.getCompressedSize()));
/*  164:     */       }
/*  165:     */     }
/*  166: 318 */     this.entriesRead += 1;
/*  167: 319 */     return this.current.entry;
/*  168:     */   }
/*  169:     */   
/*  170:     */   private void readFirstLocalFileHeader(byte[] lfh)
/*  171:     */     throws IOException
/*  172:     */   {
/*  173: 328 */     readFully(lfh);
/*  174: 329 */     ZipLong sig = new ZipLong(lfh);
/*  175: 330 */     if (sig.equals(ZipLong.DD_SIG)) {
/*  176: 331 */       throw new UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature.SPLITTING);
/*  177:     */     }
/*  178: 334 */     if (sig.equals(ZipLong.SINGLE_SEGMENT_SPLIT_MARKER))
/*  179:     */     {
/*  180: 337 */       byte[] missedLfhBytes = new byte[4];
/*  181: 338 */       readFully(missedLfhBytes);
/*  182: 339 */       System.arraycopy(lfh, 4, lfh, 0, 26);
/*  183: 340 */       System.arraycopy(missedLfhBytes, 0, lfh, 26, 4);
/*  184:     */     }
/*  185:     */   }
/*  186:     */   
/*  187:     */   private void processZip64Extra(ZipLong size, ZipLong cSize)
/*  188:     */   {
/*  189: 350 */     Zip64ExtendedInformationExtraField z64 = (Zip64ExtendedInformationExtraField)this.current.entry.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
/*  190:     */     
/*  191:     */ 
/*  192: 353 */     this.current.usesZip64 = (z64 != null);
/*  193: 354 */     if (!this.current.hasDataDescriptor) {
/*  194: 355 */       if ((z64 != null) && ((cSize.equals(ZipLong.ZIP64_MAGIC)) || (size.equals(ZipLong.ZIP64_MAGIC))))
/*  195:     */       {
/*  196: 357 */         this.current.entry.setCompressedSize(z64.getCompressedSize().getLongValue());
/*  197: 358 */         this.current.entry.setSize(z64.getSize().getLongValue());
/*  198:     */       }
/*  199:     */       else
/*  200:     */       {
/*  201: 360 */         this.current.entry.setCompressedSize(cSize.getValue());
/*  202: 361 */         this.current.entry.setSize(size.getValue());
/*  203:     */       }
/*  204:     */     }
/*  205:     */   }
/*  206:     */   
/*  207:     */   public ArchiveEntry getNextEntry()
/*  208:     */     throws IOException
/*  209:     */   {
/*  210: 368 */     return getNextZipEntry();
/*  211:     */   }
/*  212:     */   
/*  213:     */   public boolean canReadEntryData(ArchiveEntry ae)
/*  214:     */   {
/*  215: 380 */     if ((ae instanceof ZipArchiveEntry))
/*  216:     */     {
/*  217: 381 */       ZipArchiveEntry ze = (ZipArchiveEntry)ae;
/*  218: 382 */       return (ZipUtil.canHandleEntryData(ze)) && (supportsDataDescriptorFor(ze));
/*  219:     */     }
/*  220: 386 */     return false;
/*  221:     */   }
/*  222:     */   
/*  223:     */   public int read(byte[] buffer, int offset, int length)
/*  224:     */     throws IOException
/*  225:     */   {
/*  226: 391 */     if (this.closed) {
/*  227: 392 */       throw new IOException("The stream is closed");
/*  228:     */     }
/*  229: 395 */     if (this.current == null) {
/*  230: 396 */       return -1;
/*  231:     */     }
/*  232: 400 */     if ((offset > buffer.length) || (length < 0) || (offset < 0) || (buffer.length - offset < length)) {
/*  233: 401 */       throw new ArrayIndexOutOfBoundsException();
/*  234:     */     }
/*  235: 404 */     ZipUtil.checkRequestedFeatures(this.current.entry);
/*  236: 405 */     if (!supportsDataDescriptorFor(this.current.entry)) {
/*  237: 406 */       throw new UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature.DATA_DESCRIPTOR, this.current.entry);
/*  238:     */     }
/*  239:     */     int read;
/*  240: 411 */     if (this.current.entry.getMethod() == 0)
/*  241:     */     {
/*  242: 412 */       read = readStored(buffer, offset, length);
/*  243:     */     }
/*  244:     */     else
/*  245:     */     {
/*  246:     */       int read;
/*  247: 413 */       if (this.current.entry.getMethod() == 8)
/*  248:     */       {
/*  249: 414 */         read = readDeflated(buffer, offset, length);
/*  250:     */       }
/*  251:     */       else
/*  252:     */       {
/*  253:     */         int read;
/*  254: 415 */         if ((this.current.entry.getMethod() == ZipMethod.UNSHRINKING.getCode()) || (this.current.entry.getMethod() == ZipMethod.IMPLODING.getCode())) {
/*  255: 417 */           read = this.current.in.read(buffer, offset, length);
/*  256:     */         } else {
/*  257: 419 */           throw new UnsupportedZipFeatureException(ZipMethod.getMethodByCode(this.current.entry.getMethod()), this.current.entry);
/*  258:     */         }
/*  259:     */       }
/*  260:     */     }
/*  261:     */     int read;
/*  262: 423 */     if (read >= 0) {
/*  263: 424 */       this.current.crc.update(buffer, offset, read);
/*  264:     */     }
/*  265: 427 */     return read;
/*  266:     */   }
/*  267:     */   
/*  268:     */   private int readStored(byte[] buffer, int offset, int length)
/*  269:     */     throws IOException
/*  270:     */   {
/*  271: 435 */     if (this.current.hasDataDescriptor)
/*  272:     */     {
/*  273: 436 */       if (this.lastStoredEntry == null) {
/*  274: 437 */         readStoredEntry();
/*  275:     */       }
/*  276: 439 */       return this.lastStoredEntry.read(buffer, offset, length);
/*  277:     */     }
/*  278: 442 */     long csize = this.current.entry.getSize();
/*  279: 443 */     if (this.current.bytesRead >= csize) {
/*  280: 444 */       return -1;
/*  281:     */     }
/*  282: 447 */     if (this.buf.position() >= this.buf.limit())
/*  283:     */     {
/*  284: 448 */       this.buf.position(0);
/*  285: 449 */       int l = this.in.read(this.buf.array());
/*  286: 450 */       if (l == -1) {
/*  287: 451 */         return -1;
/*  288:     */       }
/*  289: 453 */       this.buf.limit(l);
/*  290:     */       
/*  291: 455 */       count(l);
/*  292: 456 */       CurrentEntry.access$714(this.current, l);
/*  293:     */     }
/*  294: 459 */     int toRead = Math.min(this.buf.remaining(), length);
/*  295: 460 */     if (csize - this.current.bytesRead < toRead) {
/*  296: 462 */       toRead = (int)(csize - this.current.bytesRead);
/*  297:     */     }
/*  298: 464 */     this.buf.get(buffer, offset, toRead);
/*  299: 465 */     CurrentEntry.access$614(this.current, toRead);
/*  300: 466 */     return toRead;
/*  301:     */   }
/*  302:     */   
/*  303:     */   private int readDeflated(byte[] buffer, int offset, int length)
/*  304:     */     throws IOException
/*  305:     */   {
/*  306: 473 */     int read = readFromInflater(buffer, offset, length);
/*  307: 474 */     if (read <= 0)
/*  308:     */     {
/*  309: 475 */       if (this.inf.finished()) {
/*  310: 476 */         return -1;
/*  311:     */       }
/*  312: 477 */       if (this.inf.needsDictionary()) {
/*  313: 478 */         throw new ZipException("This archive needs a preset dictionary which is not supported by Commons Compress.");
/*  314:     */       }
/*  315: 481 */       if (read == -1) {
/*  316: 482 */         throw new IOException("Truncated ZIP file");
/*  317:     */       }
/*  318:     */     }
/*  319: 485 */     return read;
/*  320:     */   }
/*  321:     */   
/*  322:     */   private int readFromInflater(byte[] buffer, int offset, int length)
/*  323:     */     throws IOException
/*  324:     */   {
/*  325: 493 */     int read = 0;
/*  326:     */     do
/*  327:     */     {
/*  328: 495 */       if (this.inf.needsInput())
/*  329:     */       {
/*  330: 496 */         int l = fill();
/*  331: 497 */         if (l > 0)
/*  332:     */         {
/*  333: 498 */           CurrentEntry.access$714(this.current, this.buf.limit());
/*  334:     */         }
/*  335:     */         else
/*  336:     */         {
/*  337: 499 */           if (l != -1) {
/*  338:     */             break;
/*  339:     */           }
/*  340: 500 */           return -1;
/*  341:     */         }
/*  342:     */       }
/*  343:     */       try
/*  344:     */       {
/*  345: 506 */         read = this.inf.inflate(buffer, offset, length);
/*  346:     */       }
/*  347:     */       catch (DataFormatException e)
/*  348:     */       {
/*  349: 508 */         throw ((IOException)new ZipException(e.getMessage()).initCause(e));
/*  350:     */       }
/*  351: 510 */     } while ((read == 0) && (this.inf.needsInput()));
/*  352: 511 */     return read;
/*  353:     */   }
/*  354:     */   
/*  355:     */   public void close()
/*  356:     */     throws IOException
/*  357:     */   {
/*  358: 516 */     if (!this.closed)
/*  359:     */     {
/*  360: 517 */       this.closed = true;
/*  361: 518 */       this.in.close();
/*  362: 519 */       this.inf.end();
/*  363:     */     }
/*  364:     */   }
/*  365:     */   
/*  366:     */   public long skip(long value)
/*  367:     */     throws IOException
/*  368:     */   {
/*  369: 540 */     if (value >= 0L)
/*  370:     */     {
/*  371: 541 */       long skipped = 0L;
/*  372: 542 */       while (skipped < value)
/*  373:     */       {
/*  374: 543 */         long rem = value - skipped;
/*  375: 544 */         int x = read(this.SKIP_BUF, 0, (int)(this.SKIP_BUF.length > rem ? rem : this.SKIP_BUF.length));
/*  376: 545 */         if (x == -1) {
/*  377: 546 */           return skipped;
/*  378:     */         }
/*  379: 548 */         skipped += x;
/*  380:     */       }
/*  381: 550 */       return skipped;
/*  382:     */     }
/*  383: 552 */     throw new IllegalArgumentException();
/*  384:     */   }
/*  385:     */   
/*  386:     */   public static boolean matches(byte[] signature, int length)
/*  387:     */   {
/*  388: 565 */     if (length < ZipArchiveOutputStream.LFH_SIG.length) {
/*  389: 566 */       return false;
/*  390:     */     }
/*  391: 569 */     return (checksig(signature, ZipArchiveOutputStream.LFH_SIG)) || (checksig(signature, ZipArchiveOutputStream.EOCD_SIG)) || (checksig(signature, ZipArchiveOutputStream.DD_SIG)) || (checksig(signature, ZipLong.SINGLE_SEGMENT_SPLIT_MARKER.getBytes()));
/*  392:     */   }
/*  393:     */   
/*  394:     */   private static boolean checksig(byte[] signature, byte[] expected)
/*  395:     */   {
/*  396: 576 */     for (int i = 0; i < expected.length; i++) {
/*  397: 577 */       if (signature[i] != expected[i]) {
/*  398: 578 */         return false;
/*  399:     */       }
/*  400:     */     }
/*  401: 581 */     return true;
/*  402:     */   }
/*  403:     */   
/*  404:     */   private void closeEntry()
/*  405:     */     throws IOException
/*  406:     */   {
/*  407: 603 */     if (this.closed) {
/*  408: 604 */       throw new IOException("The stream is closed");
/*  409:     */     }
/*  410: 606 */     if (this.current == null) {
/*  411: 607 */       return;
/*  412:     */     }
/*  413: 611 */     if ((this.current.bytesReadFromStream <= this.current.entry.getCompressedSize()) && (!this.current.hasDataDescriptor))
/*  414:     */     {
/*  415: 613 */       drainCurrentEntryData();
/*  416:     */     }
/*  417:     */     else
/*  418:     */     {
/*  419: 615 */       skip(9223372036854775807L);
/*  420:     */       
/*  421: 617 */       long inB = this.current.entry.getMethod() == 8 ? getBytesInflated() : this.current.bytesRead;
/*  422:     */       
/*  423:     */ 
/*  424:     */ 
/*  425:     */ 
/*  426: 622 */       int diff = (int)(this.current.bytesReadFromStream - inB);
/*  427: 625 */       if (diff > 0) {
/*  428: 626 */         pushback(this.buf.array(), this.buf.limit() - diff, diff);
/*  429:     */       }
/*  430:     */     }
/*  431: 630 */     if ((this.lastStoredEntry == null) && (this.current.hasDataDescriptor)) {
/*  432: 631 */       readDataDescriptor();
/*  433:     */     }
/*  434: 634 */     this.inf.reset();
/*  435: 635 */     this.buf.clear().flip();
/*  436: 636 */     this.current = null;
/*  437: 637 */     this.lastStoredEntry = null;
/*  438:     */   }
/*  439:     */   
/*  440:     */   private void drainCurrentEntryData()
/*  441:     */     throws IOException
/*  442:     */   {
/*  443: 645 */     long remaining = this.current.entry.getCompressedSize() - this.current.bytesReadFromStream;
/*  444: 646 */     while (remaining > 0L)
/*  445:     */     {
/*  446: 647 */       long n = this.in.read(this.buf.array(), 0, (int)Math.min(this.buf.capacity(), remaining));
/*  447: 648 */       if (n < 0L) {
/*  448: 649 */         throw new EOFException("Truncated ZIP entry: " + this.current.entry.getName());
/*  449:     */       }
/*  450: 651 */       count(n);
/*  451: 652 */       remaining -= n;
/*  452:     */     }
/*  453:     */   }
/*  454:     */   
/*  455:     */   private long getBytesInflated()
/*  456:     */   {
/*  457: 673 */     long inB = this.inf.getBytesRead();
/*  458: 674 */     if (this.current.bytesReadFromStream >= 4294967296L) {
/*  459: 675 */       while (inB + 4294967296L <= this.current.bytesReadFromStream) {
/*  460: 676 */         inB += 4294967296L;
/*  461:     */       }
/*  462:     */     }
/*  463: 679 */     return inB;
/*  464:     */   }
/*  465:     */   
/*  466:     */   private int fill()
/*  467:     */     throws IOException
/*  468:     */   {
/*  469: 683 */     if (this.closed) {
/*  470: 684 */       throw new IOException("The stream is closed");
/*  471:     */     }
/*  472: 686 */     int length = this.in.read(this.buf.array());
/*  473: 687 */     if (length > 0)
/*  474:     */     {
/*  475: 688 */       this.buf.limit(length);
/*  476: 689 */       count(this.buf.limit());
/*  477: 690 */       this.inf.setInput(this.buf.array(), 0, this.buf.limit());
/*  478:     */     }
/*  479: 692 */     return length;
/*  480:     */   }
/*  481:     */   
/*  482:     */   private void readFully(byte[] b)
/*  483:     */     throws IOException
/*  484:     */   {
/*  485: 696 */     int count = IOUtils.readFully(this.in, b);
/*  486: 697 */     count(count);
/*  487: 698 */     if (count < b.length) {
/*  488: 699 */       throw new EOFException();
/*  489:     */     }
/*  490:     */   }
/*  491:     */   
/*  492:     */   private void readDataDescriptor()
/*  493:     */     throws IOException
/*  494:     */   {
/*  495: 704 */     readFully(this.WORD_BUF);
/*  496: 705 */     ZipLong val = new ZipLong(this.WORD_BUF);
/*  497: 706 */     if (ZipLong.DD_SIG.equals(val))
/*  498:     */     {
/*  499: 708 */       readFully(this.WORD_BUF);
/*  500: 709 */       val = new ZipLong(this.WORD_BUF);
/*  501:     */     }
/*  502: 711 */     this.current.entry.setCrc(val.getValue());
/*  503:     */     
/*  504:     */ 
/*  505:     */ 
/*  506:     */ 
/*  507:     */ 
/*  508:     */ 
/*  509:     */ 
/*  510:     */ 
/*  511:     */ 
/*  512:     */ 
/*  513:     */ 
/*  514:     */ 
/*  515: 724 */     readFully(this.TWO_DWORD_BUF);
/*  516: 725 */     ZipLong potentialSig = new ZipLong(this.TWO_DWORD_BUF, 8);
/*  517: 726 */     if ((potentialSig.equals(ZipLong.CFH_SIG)) || (potentialSig.equals(ZipLong.LFH_SIG)))
/*  518:     */     {
/*  519: 727 */       pushback(this.TWO_DWORD_BUF, 8, 8);
/*  520: 728 */       this.current.entry.setCompressedSize(ZipLong.getValue(this.TWO_DWORD_BUF));
/*  521: 729 */       this.current.entry.setSize(ZipLong.getValue(this.TWO_DWORD_BUF, 4));
/*  522:     */     }
/*  523:     */     else
/*  524:     */     {
/*  525: 731 */       this.current.entry.setCompressedSize(ZipEightByteInteger.getLongValue(this.TWO_DWORD_BUF));
/*  526: 732 */       this.current.entry.setSize(ZipEightByteInteger.getLongValue(this.TWO_DWORD_BUF, 8));
/*  527:     */     }
/*  528:     */   }
/*  529:     */   
/*  530:     */   private boolean supportsDataDescriptorFor(ZipArchiveEntry entry)
/*  531:     */   {
/*  532: 744 */     return (!entry.getGeneralPurposeBit().usesDataDescriptor()) || ((this.allowStoredEntriesWithDataDescriptor) && (entry.getMethod() == 0)) || (entry.getMethod() == 8);
/*  533:     */   }
/*  534:     */   
/*  535:     */   private void readStoredEntry()
/*  536:     */     throws IOException
/*  537:     */   {
/*  538: 768 */     ByteArrayOutputStream bos = new ByteArrayOutputStream();
/*  539: 769 */     int off = 0;
/*  540: 770 */     boolean done = false;
/*  541:     */     
/*  542:     */ 
/*  543: 773 */     int ddLen = this.current.usesZip64 ? 20 : 12;
/*  544: 775 */     while (!done)
/*  545:     */     {
/*  546: 776 */       int r = this.in.read(this.buf.array(), off, 512 - off);
/*  547: 777 */       if (r <= 0) {
/*  548: 780 */         throw new IOException("Truncated ZIP file");
/*  549:     */       }
/*  550: 782 */       if (r + off < 4)
/*  551:     */       {
/*  552: 784 */         off += r;
/*  553:     */       }
/*  554:     */       else
/*  555:     */       {
/*  556: 788 */         done = bufferContainsSignature(bos, off, r, ddLen);
/*  557: 789 */         if (!done) {
/*  558: 790 */           off = cacheBytesRead(bos, off, r, ddLen);
/*  559:     */         }
/*  560:     */       }
/*  561:     */     }
/*  562: 794 */     byte[] b = bos.toByteArray();
/*  563: 795 */     this.lastStoredEntry = new ByteArrayInputStream(b);
/*  564:     */   }
/*  565:     */   
/*  566: 798 */   private static final byte[] LFH = ZipLong.LFH_SIG.getBytes();
/*  567: 799 */   private static final byte[] CFH = ZipLong.CFH_SIG.getBytes();
/*  568: 800 */   private static final byte[] DD = ZipLong.DD_SIG.getBytes();
/*  569:     */   
/*  570:     */   private boolean bufferContainsSignature(ByteArrayOutputStream bos, int offset, int lastRead, int expectedDDLen)
/*  571:     */     throws IOException
/*  572:     */   {
/*  573: 813 */     boolean done = false;
/*  574: 814 */     int readTooMuch = 0;
/*  575: 815 */     for (int i = 0; (!done) && (i < lastRead - 4); i++) {
/*  576: 816 */       if ((this.buf.array()[i] == LFH[0]) && (this.buf.array()[(i + 1)] == LFH[1]))
/*  577:     */       {
/*  578: 817 */         if (((this.buf.array()[(i + 2)] == LFH[2]) && (this.buf.array()[(i + 3)] == LFH[3])) || ((this.buf.array()[i] == CFH[2]) && (this.buf.array()[(i + 3)] == CFH[3])))
/*  579:     */         {
/*  580: 820 */           readTooMuch = offset + lastRead - i - expectedDDLen;
/*  581: 821 */           done = true;
/*  582:     */         }
/*  583: 823 */         else if ((this.buf.array()[(i + 2)] == DD[2]) && (this.buf.array()[(i + 3)] == DD[3]))
/*  584:     */         {
/*  585: 825 */           readTooMuch = offset + lastRead - i;
/*  586: 826 */           done = true;
/*  587:     */         }
/*  588: 828 */         if (done)
/*  589:     */         {
/*  590: 833 */           pushback(this.buf.array(), offset + lastRead - readTooMuch, readTooMuch);
/*  591: 834 */           bos.write(this.buf.array(), 0, i);
/*  592: 835 */           readDataDescriptor();
/*  593:     */         }
/*  594:     */       }
/*  595:     */     }
/*  596: 839 */     return done;
/*  597:     */   }
/*  598:     */   
/*  599:     */   private int cacheBytesRead(ByteArrayOutputStream bos, int offset, int lastRead, int expecteDDLen)
/*  600:     */   {
/*  601: 852 */     int cacheable = offset + lastRead - expecteDDLen - 3;
/*  602: 853 */     if (cacheable > 0)
/*  603:     */     {
/*  604: 854 */       bos.write(this.buf.array(), 0, cacheable);
/*  605: 855 */       System.arraycopy(this.buf.array(), cacheable, this.buf.array(), 0, expecteDDLen + 3);
/*  606: 856 */       offset = expecteDDLen + 3;
/*  607:     */     }
/*  608:     */     else
/*  609:     */     {
/*  610: 858 */       offset += lastRead;
/*  611:     */     }
/*  612: 860 */     return offset;
/*  613:     */   }
/*  614:     */   
/*  615:     */   private void pushback(byte[] buf, int offset, int length)
/*  616:     */     throws IOException
/*  617:     */   {
/*  618: 864 */     ((PushbackInputStream)this.in).unread(buf, offset, length);
/*  619: 865 */     pushedBackBytes(length);
/*  620:     */   }
/*  621:     */   
/*  622:     */   private void skipRemainderOfArchive()
/*  623:     */     throws IOException
/*  624:     */   {
/*  625: 893 */     realSkip(this.entriesRead * 46 - 30);
/*  626: 894 */     findEocdRecord();
/*  627: 895 */     realSkip(16L);
/*  628: 896 */     readFully(this.SHORT_BUF);
/*  629:     */     
/*  630: 898 */     realSkip(ZipShort.getValue(this.SHORT_BUF));
/*  631:     */   }
/*  632:     */   
/*  633:     */   private void findEocdRecord()
/*  634:     */     throws IOException
/*  635:     */   {
/*  636: 906 */     int currentByte = -1;
/*  637: 907 */     boolean skipReadCall = false;
/*  638: 908 */     while ((skipReadCall) || ((currentByte = readOneByte()) > -1))
/*  639:     */     {
/*  640: 909 */       skipReadCall = false;
/*  641: 910 */       if (isFirstByteOfEocdSig(currentByte))
/*  642:     */       {
/*  643: 913 */         currentByte = readOneByte();
/*  644: 914 */         if (currentByte != ZipArchiveOutputStream.EOCD_SIG[1])
/*  645:     */         {
/*  646: 915 */           if (currentByte == -1) {
/*  647:     */             break;
/*  648:     */           }
/*  649: 918 */           skipReadCall = isFirstByteOfEocdSig(currentByte);
/*  650:     */         }
/*  651:     */         else
/*  652:     */         {
/*  653: 921 */           currentByte = readOneByte();
/*  654: 922 */           if (currentByte != ZipArchiveOutputStream.EOCD_SIG[2])
/*  655:     */           {
/*  656: 923 */             if (currentByte == -1) {
/*  657:     */               break;
/*  658:     */             }
/*  659: 926 */             skipReadCall = isFirstByteOfEocdSig(currentByte);
/*  660:     */           }
/*  661:     */           else
/*  662:     */           {
/*  663: 929 */             currentByte = readOneByte();
/*  664: 930 */             if ((currentByte == -1) || (currentByte == ZipArchiveOutputStream.EOCD_SIG[3])) {
/*  665:     */               break;
/*  666:     */             }
/*  667: 934 */             skipReadCall = isFirstByteOfEocdSig(currentByte);
/*  668:     */           }
/*  669:     */         }
/*  670:     */       }
/*  671:     */     }
/*  672:     */   }
/*  673:     */   
/*  674:     */   private void realSkip(long value)
/*  675:     */     throws IOException
/*  676:     */   {
/*  677: 946 */     if (value >= 0L)
/*  678:     */     {
/*  679: 947 */       long skipped = 0L;
/*  680: 948 */       while (skipped < value)
/*  681:     */       {
/*  682: 949 */         long rem = value - skipped;
/*  683: 950 */         int x = this.in.read(this.SKIP_BUF, 0, (int)(this.SKIP_BUF.length > rem ? rem : this.SKIP_BUF.length));
/*  684: 951 */         if (x == -1) {
/*  685: 952 */           return;
/*  686:     */         }
/*  687: 954 */         count(x);
/*  688: 955 */         skipped += x;
/*  689:     */       }
/*  690: 957 */       return;
/*  691:     */     }
/*  692: 959 */     throw new IllegalArgumentException();
/*  693:     */   }
/*  694:     */   
/*  695:     */   private int readOneByte()
/*  696:     */     throws IOException
/*  697:     */   {
/*  698: 969 */     int b = this.in.read();
/*  699: 970 */     if (b != -1) {
/*  700: 971 */       count(1);
/*  701:     */     }
/*  702: 973 */     return b;
/*  703:     */   }
/*  704:     */   
/*  705:     */   private boolean isFirstByteOfEocdSig(int b)
/*  706:     */   {
/*  707: 977 */     return b == ZipArchiveOutputStream.EOCD_SIG[0];
/*  708:     */   }
/*  709:     */   
/*  710:     */   private static final class CurrentEntry
/*  711:     */   {
/*  712: 989 */     private final ZipArchiveEntry entry = new ZipArchiveEntry();
/*  713:     */     private boolean hasDataDescriptor;
/*  714:     */     private boolean usesZip64;
/*  715:     */     private long bytesRead;
/*  716:     */     private long bytesReadFromStream;
/*  717:1019 */     private final CRC32 crc = new CRC32();
/*  718:     */     private InputStream in;
/*  719:     */   }
/*  720:     */   
/*  721:     */   private class BoundedInputStream
/*  722:     */     extends InputStream
/*  723:     */   {
/*  724:     */     private final InputStream in;
/*  725:     */     private final long max;
/*  726:1039 */     private long pos = 0L;
/*  727:     */     
/*  728:     */     public BoundedInputStream(InputStream in, long size)
/*  729:     */     {
/*  730:1049 */       this.max = size;
/*  731:1050 */       this.in = in;
/*  732:     */     }
/*  733:     */     
/*  734:     */     public int read()
/*  735:     */       throws IOException
/*  736:     */     {
/*  737:1055 */       if ((this.max >= 0L) && (this.pos >= this.max)) {
/*  738:1056 */         return -1;
/*  739:     */       }
/*  740:1058 */       int result = this.in.read();
/*  741:1059 */       this.pos += 1L;
/*  742:1060 */       ZipArchiveInputStream.this.count(1);
/*  743:1061 */       ZipArchiveInputStream.CurrentEntry.access$708(ZipArchiveInputStream.this.current);
/*  744:1062 */       return result;
/*  745:     */     }
/*  746:     */     
/*  747:     */     public int read(byte[] b)
/*  748:     */       throws IOException
/*  749:     */     {
/*  750:1067 */       return read(b, 0, b.length);
/*  751:     */     }
/*  752:     */     
/*  753:     */     public int read(byte[] b, int off, int len)
/*  754:     */       throws IOException
/*  755:     */     {
/*  756:1072 */       if ((this.max >= 0L) && (this.pos >= this.max)) {
/*  757:1073 */         return -1;
/*  758:     */       }
/*  759:1075 */       long maxRead = this.max >= 0L ? Math.min(len, this.max - this.pos) : len;
/*  760:1076 */       int bytesRead = this.in.read(b, off, (int)maxRead);
/*  761:1078 */       if (bytesRead == -1) {
/*  762:1079 */         return -1;
/*  763:     */       }
/*  764:1082 */       this.pos += bytesRead;
/*  765:1083 */       ZipArchiveInputStream.this.count(bytesRead);
/*  766:1084 */       ZipArchiveInputStream.CurrentEntry.access$714(ZipArchiveInputStream.this.current, bytesRead);
/*  767:1085 */       return bytesRead;
/*  768:     */     }
/*  769:     */     
/*  770:     */     public long skip(long n)
/*  771:     */       throws IOException
/*  772:     */     {
/*  773:1090 */       long toSkip = this.max >= 0L ? Math.min(n, this.max - this.pos) : n;
/*  774:1091 */       long skippedBytes = this.in.skip(toSkip);
/*  775:1092 */       this.pos += skippedBytes;
/*  776:1093 */       return skippedBytes;
/*  777:     */     }
/*  778:     */     
/*  779:     */     public int available()
/*  780:     */       throws IOException
/*  781:     */     {
/*  782:1098 */       if ((this.max >= 0L) && (this.pos >= this.max)) {
/*  783:1099 */         return 0;
/*  784:     */       }
/*  785:1101 */       return this.in.available();
/*  786:     */     }
/*  787:     */   }
/*  788:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
 * JD-Core Version:    0.7.0.1
 */