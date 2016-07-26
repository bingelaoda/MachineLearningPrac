/*   1:    */ package org.apache.commons.compress.archivers.sevenz;
/*   2:    */ 
/*   3:    */ import java.io.ByteArrayOutputStream;
/*   4:    */ import java.io.Closeable;
/*   5:    */ import java.io.DataOutput;
/*   6:    */ import java.io.DataOutputStream;
/*   7:    */ import java.io.File;
/*   8:    */ import java.io.IOException;
/*   9:    */ import java.io.OutputStream;
/*  10:    */ import java.io.RandomAccessFile;
/*  11:    */ import java.util.ArrayList;
/*  12:    */ import java.util.BitSet;
/*  13:    */ import java.util.Collections;
/*  14:    */ import java.util.Date;
/*  15:    */ import java.util.HashMap;
/*  16:    */ import java.util.LinkedList;
/*  17:    */ import java.util.List;
/*  18:    */ import java.util.Map;
/*  19:    */ import java.util.zip.CRC32;
/*  20:    */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*  21:    */ import org.apache.commons.compress.utils.CountingOutputStream;
/*  22:    */ 
/*  23:    */ public class SevenZOutputFile
/*  24:    */   implements Closeable
/*  25:    */ {
/*  26:    */   private final RandomAccessFile file;
/*  27: 47 */   private final List<SevenZArchiveEntry> files = new ArrayList();
/*  28: 48 */   private int numNonEmptyStreams = 0;
/*  29: 49 */   private final CRC32 crc32 = new CRC32();
/*  30: 50 */   private final CRC32 compressedCrc32 = new CRC32();
/*  31: 51 */   private long fileBytesWritten = 0L;
/*  32: 52 */   private boolean finished = false;
/*  33:    */   private CountingOutputStream currentOutputStream;
/*  34:    */   private CountingOutputStream[] additionalCountingStreams;
/*  35: 55 */   private Iterable<? extends SevenZMethodConfiguration> contentMethods = Collections.singletonList(new SevenZMethodConfiguration(SevenZMethod.LZMA2));
/*  36: 57 */   private final Map<SevenZArchiveEntry, long[]> additionalSizes = new HashMap();
/*  37:    */   
/*  38:    */   public SevenZOutputFile(File filename)
/*  39:    */     throws IOException
/*  40:    */   {
/*  41: 66 */     this.file = new RandomAccessFile(filename, "rw");
/*  42: 67 */     this.file.seek(32L);
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void setContentCompression(SevenZMethod method)
/*  46:    */   {
/*  47: 83 */     setContentMethods(Collections.singletonList(new SevenZMethodConfiguration(method)));
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void setContentMethods(Iterable<? extends SevenZMethodConfiguration> methods)
/*  51:    */   {
/*  52:101 */     this.contentMethods = reverse(methods);
/*  53:    */   }
/*  54:    */   
/*  55:    */   public void close()
/*  56:    */     throws IOException
/*  57:    */   {
/*  58:110 */     if (!this.finished) {
/*  59:111 */       finish();
/*  60:    */     }
/*  61:113 */     this.file.close();
/*  62:    */   }
/*  63:    */   
/*  64:    */   public SevenZArchiveEntry createArchiveEntry(File inputFile, String entryName)
/*  65:    */     throws IOException
/*  66:    */   {
/*  67:127 */     SevenZArchiveEntry entry = new SevenZArchiveEntry();
/*  68:128 */     entry.setDirectory(inputFile.isDirectory());
/*  69:129 */     entry.setName(entryName);
/*  70:130 */     entry.setLastModifiedDate(new Date(inputFile.lastModified()));
/*  71:131 */     return entry;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void putArchiveEntry(ArchiveEntry archiveEntry)
/*  75:    */     throws IOException
/*  76:    */   {
/*  77:144 */     SevenZArchiveEntry entry = (SevenZArchiveEntry)archiveEntry;
/*  78:145 */     this.files.add(entry);
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void closeArchiveEntry()
/*  82:    */     throws IOException
/*  83:    */   {
/*  84:153 */     if (this.currentOutputStream != null)
/*  85:    */     {
/*  86:154 */       this.currentOutputStream.flush();
/*  87:155 */       this.currentOutputStream.close();
/*  88:    */     }
/*  89:158 */     SevenZArchiveEntry entry = (SevenZArchiveEntry)this.files.get(this.files.size() - 1);
/*  90:159 */     if (this.fileBytesWritten > 0L)
/*  91:    */     {
/*  92:160 */       entry.setHasStream(true);
/*  93:161 */       this.numNonEmptyStreams += 1;
/*  94:162 */       entry.setSize(this.currentOutputStream.getBytesWritten());
/*  95:163 */       entry.setCompressedSize(this.fileBytesWritten);
/*  96:164 */       entry.setCrcValue(this.crc32.getValue());
/*  97:165 */       entry.setCompressedCrcValue(this.compressedCrc32.getValue());
/*  98:166 */       entry.setHasCrc(true);
/*  99:167 */       if (this.additionalCountingStreams != null)
/* 100:    */       {
/* 101:168 */         long[] sizes = new long[this.additionalCountingStreams.length];
/* 102:169 */         for (int i = 0; i < this.additionalCountingStreams.length; i++) {
/* 103:170 */           sizes[i] = this.additionalCountingStreams[i].getBytesWritten();
/* 104:    */         }
/* 105:172 */         this.additionalSizes.put(entry, sizes);
/* 106:    */       }
/* 107:    */     }
/* 108:    */     else
/* 109:    */     {
/* 110:175 */       entry.setHasStream(false);
/* 111:176 */       entry.setSize(0L);
/* 112:177 */       entry.setCompressedSize(0L);
/* 113:178 */       entry.setHasCrc(false);
/* 114:    */     }
/* 115:180 */     this.currentOutputStream = null;
/* 116:181 */     this.additionalCountingStreams = null;
/* 117:182 */     this.crc32.reset();
/* 118:183 */     this.compressedCrc32.reset();
/* 119:184 */     this.fileBytesWritten = 0L;
/* 120:    */   }
/* 121:    */   
/* 122:    */   public void write(int b)
/* 123:    */     throws IOException
/* 124:    */   {
/* 125:193 */     getCurrentOutputStream().write(b);
/* 126:    */   }
/* 127:    */   
/* 128:    */   public void write(byte[] b)
/* 129:    */     throws IOException
/* 130:    */   {
/* 131:202 */     write(b, 0, b.length);
/* 132:    */   }
/* 133:    */   
/* 134:    */   public void write(byte[] b, int off, int len)
/* 135:    */     throws IOException
/* 136:    */   {
/* 137:213 */     if (len > 0) {
/* 138:214 */       getCurrentOutputStream().write(b, off, len);
/* 139:    */     }
/* 140:    */   }
/* 141:    */   
/* 142:    */   public void finish()
/* 143:    */     throws IOException
/* 144:    */   {
/* 145:224 */     if (this.finished) {
/* 146:225 */       throw new IOException("This archive has already been finished");
/* 147:    */     }
/* 148:227 */     this.finished = true;
/* 149:    */     
/* 150:229 */     long headerPosition = this.file.getFilePointer();
/* 151:    */     
/* 152:231 */     ByteArrayOutputStream headerBaos = new ByteArrayOutputStream();
/* 153:232 */     DataOutputStream header = new DataOutputStream(headerBaos);
/* 154:    */     
/* 155:234 */     writeHeader(header);
/* 156:235 */     header.flush();
/* 157:236 */     byte[] headerBytes = headerBaos.toByteArray();
/* 158:237 */     this.file.write(headerBytes);
/* 159:    */     
/* 160:239 */     CRC32 crc32 = new CRC32();
/* 161:    */     
/* 162:    */ 
/* 163:242 */     this.file.seek(0L);
/* 164:243 */     this.file.write(SevenZFile.sevenZSignature);
/* 165:    */     
/* 166:245 */     this.file.write(0);
/* 167:246 */     this.file.write(2);
/* 168:    */     
/* 169:    */ 
/* 170:249 */     ByteArrayOutputStream startHeaderBaos = new ByteArrayOutputStream();
/* 171:250 */     DataOutputStream startHeaderStream = new DataOutputStream(startHeaderBaos);
/* 172:251 */     startHeaderStream.writeLong(Long.reverseBytes(headerPosition - 32L));
/* 173:252 */     startHeaderStream.writeLong(Long.reverseBytes(0xFFFFFFFF & headerBytes.length));
/* 174:253 */     crc32.reset();
/* 175:254 */     crc32.update(headerBytes);
/* 176:255 */     startHeaderStream.writeInt(Integer.reverseBytes((int)crc32.getValue()));
/* 177:256 */     startHeaderStream.flush();
/* 178:257 */     byte[] startHeaderBytes = startHeaderBaos.toByteArray();
/* 179:258 */     crc32.reset();
/* 180:259 */     crc32.update(startHeaderBytes);
/* 181:260 */     this.file.writeInt(Integer.reverseBytes((int)crc32.getValue()));
/* 182:261 */     this.file.write(startHeaderBytes);
/* 183:    */   }
/* 184:    */   
/* 185:    */   private OutputStream getCurrentOutputStream()
/* 186:    */     throws IOException
/* 187:    */   {
/* 188:270 */     if (this.currentOutputStream == null) {
/* 189:271 */       this.currentOutputStream = setupFileOutputStream();
/* 190:    */     }
/* 191:273 */     return this.currentOutputStream;
/* 192:    */   }
/* 193:    */   
/* 194:    */   private CountingOutputStream setupFileOutputStream()
/* 195:    */     throws IOException
/* 196:    */   {
/* 197:277 */     if (this.files.isEmpty()) {
/* 198:278 */       throw new IllegalStateException("No current 7z entry");
/* 199:    */     }
/* 200:281 */     OutputStream out = new OutputStreamWrapper(null);
/* 201:282 */     ArrayList<CountingOutputStream> moreStreams = new ArrayList();
/* 202:283 */     boolean first = true;
/* 203:284 */     for (SevenZMethodConfiguration m : getContentMethods((SevenZArchiveEntry)this.files.get(this.files.size() - 1)))
/* 204:    */     {
/* 205:285 */       if (!first)
/* 206:    */       {
/* 207:286 */         CountingOutputStream cos = new CountingOutputStream(out);
/* 208:287 */         moreStreams.add(cos);
/* 209:288 */         out = cos;
/* 210:    */       }
/* 211:290 */       out = Coders.addEncoder(out, m.getMethod(), m.getOptions());
/* 212:291 */       first = false;
/* 213:    */     }
/* 214:293 */     if (!moreStreams.isEmpty()) {
/* 215:294 */       this.additionalCountingStreams = ((CountingOutputStream[])moreStreams.toArray(new CountingOutputStream[moreStreams.size()]));
/* 216:    */     }
/* 217:296 */     new CountingOutputStream(out)
/* 218:    */     {
/* 219:    */       public void write(int b)
/* 220:    */         throws IOException
/* 221:    */       {
/* 222:299 */         super.write(b);
/* 223:300 */         SevenZOutputFile.this.crc32.update(b);
/* 224:    */       }
/* 225:    */       
/* 226:    */       public void write(byte[] b)
/* 227:    */         throws IOException
/* 228:    */       {
/* 229:305 */         super.write(b);
/* 230:306 */         SevenZOutputFile.this.crc32.update(b);
/* 231:    */       }
/* 232:    */       
/* 233:    */       public void write(byte[] b, int off, int len)
/* 234:    */         throws IOException
/* 235:    */       {
/* 236:312 */         super.write(b, off, len);
/* 237:313 */         SevenZOutputFile.this.crc32.update(b, off, len);
/* 238:    */       }
/* 239:    */     };
/* 240:    */   }
/* 241:    */   
/* 242:    */   private Iterable<? extends SevenZMethodConfiguration> getContentMethods(SevenZArchiveEntry entry)
/* 243:    */   {
/* 244:319 */     Iterable<? extends SevenZMethodConfiguration> ms = entry.getContentMethods();
/* 245:320 */     return ms == null ? this.contentMethods : ms;
/* 246:    */   }
/* 247:    */   
/* 248:    */   private void writeHeader(DataOutput header)
/* 249:    */     throws IOException
/* 250:    */   {
/* 251:324 */     header.write(1);
/* 252:    */     
/* 253:326 */     header.write(4);
/* 254:327 */     writeStreamsInfo(header);
/* 255:328 */     writeFilesInfo(header);
/* 256:329 */     header.write(0);
/* 257:    */   }
/* 258:    */   
/* 259:    */   private void writeStreamsInfo(DataOutput header)
/* 260:    */     throws IOException
/* 261:    */   {
/* 262:333 */     if (this.numNonEmptyStreams > 0)
/* 263:    */     {
/* 264:334 */       writePackInfo(header);
/* 265:335 */       writeUnpackInfo(header);
/* 266:    */     }
/* 267:338 */     writeSubStreamsInfo(header);
/* 268:    */     
/* 269:340 */     header.write(0);
/* 270:    */   }
/* 271:    */   
/* 272:    */   private void writePackInfo(DataOutput header)
/* 273:    */     throws IOException
/* 274:    */   {
/* 275:344 */     header.write(6);
/* 276:    */     
/* 277:346 */     writeUint64(header, 0L);
/* 278:347 */     writeUint64(header, 0xFFFFFFFF & this.numNonEmptyStreams);
/* 279:    */     
/* 280:349 */     header.write(9);
/* 281:350 */     for (SevenZArchiveEntry entry : this.files) {
/* 282:351 */       if (entry.hasStream()) {
/* 283:352 */         writeUint64(header, entry.getCompressedSize());
/* 284:    */       }
/* 285:    */     }
/* 286:356 */     header.write(10);
/* 287:357 */     header.write(1);
/* 288:358 */     for (SevenZArchiveEntry entry : this.files) {
/* 289:359 */       if (entry.hasStream()) {
/* 290:360 */         header.writeInt(Integer.reverseBytes((int)entry.getCompressedCrcValue()));
/* 291:    */       }
/* 292:    */     }
/* 293:364 */     header.write(0);
/* 294:    */   }
/* 295:    */   
/* 296:    */   private void writeUnpackInfo(DataOutput header)
/* 297:    */     throws IOException
/* 298:    */   {
/* 299:368 */     header.write(7);
/* 300:    */     
/* 301:370 */     header.write(11);
/* 302:371 */     writeUint64(header, this.numNonEmptyStreams);
/* 303:372 */     header.write(0);
/* 304:373 */     for (SevenZArchiveEntry entry : this.files) {
/* 305:374 */       if (entry.hasStream()) {
/* 306:375 */         writeFolder(header, entry);
/* 307:    */       }
/* 308:    */     }
/* 309:379 */     header.write(12);
/* 310:380 */     for (SevenZArchiveEntry entry : this.files) {
/* 311:381 */       if (entry.hasStream())
/* 312:    */       {
/* 313:382 */         long[] moreSizes = (long[])this.additionalSizes.get(entry);
/* 314:383 */         if (moreSizes != null) {
/* 315:384 */           for (long s : moreSizes) {
/* 316:385 */             writeUint64(header, s);
/* 317:    */           }
/* 318:    */         }
/* 319:388 */         writeUint64(header, entry.getSize());
/* 320:    */       }
/* 321:    */     }
/* 322:392 */     header.write(10);
/* 323:393 */     header.write(1);
/* 324:394 */     for (SevenZArchiveEntry entry : this.files) {
/* 325:395 */       if (entry.hasStream()) {
/* 326:396 */         header.writeInt(Integer.reverseBytes((int)entry.getCrcValue()));
/* 327:    */       }
/* 328:    */     }
/* 329:400 */     header.write(0);
/* 330:    */   }
/* 331:    */   
/* 332:    */   private void writeFolder(DataOutput header, SevenZArchiveEntry entry)
/* 333:    */     throws IOException
/* 334:    */   {
/* 335:404 */     ByteArrayOutputStream bos = new ByteArrayOutputStream();
/* 336:405 */     int numCoders = 0;
/* 337:406 */     for (SevenZMethodConfiguration m : getContentMethods(entry))
/* 338:    */     {
/* 339:407 */       numCoders++;
/* 340:408 */       writeSingleCodec(m, bos);
/* 341:    */     }
/* 342:411 */     writeUint64(header, numCoders);
/* 343:412 */     header.write(bos.toByteArray());
/* 344:413 */     for (int i = 0; i < numCoders - 1; i++)
/* 345:    */     {
/* 346:414 */       writeUint64(header, i + 1);
/* 347:415 */       writeUint64(header, i);
/* 348:    */     }
/* 349:    */   }
/* 350:    */   
/* 351:    */   private void writeSingleCodec(SevenZMethodConfiguration m, OutputStream bos)
/* 352:    */     throws IOException
/* 353:    */   {
/* 354:420 */     byte[] id = m.getMethod().getId();
/* 355:421 */     byte[] properties = Coders.findByMethod(m.getMethod()).getOptionsAsProperties(m.getOptions());
/* 356:    */     
/* 357:    */ 
/* 358:424 */     int codecFlags = id.length;
/* 359:425 */     if (properties.length > 0) {
/* 360:426 */       codecFlags |= 0x20;
/* 361:    */     }
/* 362:428 */     bos.write(codecFlags);
/* 363:429 */     bos.write(id);
/* 364:431 */     if (properties.length > 0)
/* 365:    */     {
/* 366:432 */       bos.write(properties.length);
/* 367:433 */       bos.write(properties);
/* 368:    */     }
/* 369:    */   }
/* 370:    */   
/* 371:    */   private void writeSubStreamsInfo(DataOutput header)
/* 372:    */     throws IOException
/* 373:    */   {
/* 374:438 */     header.write(8);
/* 375:    */     
/* 376:    */ 
/* 377:    */ 
/* 378:    */ 
/* 379:    */ 
/* 380:    */ 
/* 381:    */ 
/* 382:    */ 
/* 383:    */ 
/* 384:448 */     header.write(0);
/* 385:    */   }
/* 386:    */   
/* 387:    */   private void writeFilesInfo(DataOutput header)
/* 388:    */     throws IOException
/* 389:    */   {
/* 390:452 */     header.write(5);
/* 391:    */     
/* 392:454 */     writeUint64(header, this.files.size());
/* 393:    */     
/* 394:456 */     writeFileEmptyStreams(header);
/* 395:457 */     writeFileEmptyFiles(header);
/* 396:458 */     writeFileAntiItems(header);
/* 397:459 */     writeFileNames(header);
/* 398:460 */     writeFileCTimes(header);
/* 399:461 */     writeFileATimes(header);
/* 400:462 */     writeFileMTimes(header);
/* 401:463 */     writeFileWindowsAttributes(header);
/* 402:464 */     header.write(0);
/* 403:    */   }
/* 404:    */   
/* 405:    */   private void writeFileEmptyStreams(DataOutput header)
/* 406:    */     throws IOException
/* 407:    */   {
/* 408:468 */     boolean hasEmptyStreams = false;
/* 409:469 */     for (SevenZArchiveEntry entry : this.files) {
/* 410:470 */       if (!entry.hasStream())
/* 411:    */       {
/* 412:471 */         hasEmptyStreams = true;
/* 413:472 */         break;
/* 414:    */       }
/* 415:    */     }
/* 416:475 */     if (hasEmptyStreams)
/* 417:    */     {
/* 418:476 */       header.write(14);
/* 419:477 */       BitSet emptyStreams = new BitSet(this.files.size());
/* 420:478 */       for (int i = 0; i < this.files.size(); i++) {
/* 421:479 */         emptyStreams.set(i, !((SevenZArchiveEntry)this.files.get(i)).hasStream());
/* 422:    */       }
/* 423:481 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 424:482 */       DataOutputStream out = new DataOutputStream(baos);
/* 425:483 */       writeBits(out, emptyStreams, this.files.size());
/* 426:484 */       out.flush();
/* 427:485 */       byte[] contents = baos.toByteArray();
/* 428:486 */       writeUint64(header, contents.length);
/* 429:487 */       header.write(contents);
/* 430:    */     }
/* 431:    */   }
/* 432:    */   
/* 433:    */   private void writeFileEmptyFiles(DataOutput header)
/* 434:    */     throws IOException
/* 435:    */   {
/* 436:492 */     boolean hasEmptyFiles = false;
/* 437:493 */     int emptyStreamCounter = 0;
/* 438:494 */     BitSet emptyFiles = new BitSet(0);
/* 439:495 */     for (SevenZArchiveEntry file1 : this.files) {
/* 440:496 */       if (!file1.hasStream())
/* 441:    */       {
/* 442:497 */         boolean isDir = file1.isDirectory();
/* 443:498 */         emptyFiles.set(emptyStreamCounter++, !isDir);
/* 444:499 */         hasEmptyFiles |= !isDir;
/* 445:    */       }
/* 446:    */     }
/* 447:502 */     if (hasEmptyFiles)
/* 448:    */     {
/* 449:503 */       header.write(15);
/* 450:504 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 451:505 */       DataOutputStream out = new DataOutputStream(baos);
/* 452:506 */       writeBits(out, emptyFiles, emptyStreamCounter);
/* 453:507 */       out.flush();
/* 454:508 */       byte[] contents = baos.toByteArray();
/* 455:509 */       writeUint64(header, contents.length);
/* 456:510 */       header.write(contents);
/* 457:    */     }
/* 458:    */   }
/* 459:    */   
/* 460:    */   private void writeFileAntiItems(DataOutput header)
/* 461:    */     throws IOException
/* 462:    */   {
/* 463:515 */     boolean hasAntiItems = false;
/* 464:516 */     BitSet antiItems = new BitSet(0);
/* 465:517 */     int antiItemCounter = 0;
/* 466:518 */     for (SevenZArchiveEntry file1 : this.files) {
/* 467:519 */       if (!file1.hasStream())
/* 468:    */       {
/* 469:520 */         boolean isAnti = file1.isAntiItem();
/* 470:521 */         antiItems.set(antiItemCounter++, isAnti);
/* 471:522 */         hasAntiItems |= isAnti;
/* 472:    */       }
/* 473:    */     }
/* 474:525 */     if (hasAntiItems)
/* 475:    */     {
/* 476:526 */       header.write(16);
/* 477:527 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 478:528 */       DataOutputStream out = new DataOutputStream(baos);
/* 479:529 */       writeBits(out, antiItems, antiItemCounter);
/* 480:530 */       out.flush();
/* 481:531 */       byte[] contents = baos.toByteArray();
/* 482:532 */       writeUint64(header, contents.length);
/* 483:533 */       header.write(contents);
/* 484:    */     }
/* 485:    */   }
/* 486:    */   
/* 487:    */   private void writeFileNames(DataOutput header)
/* 488:    */     throws IOException
/* 489:    */   {
/* 490:538 */     header.write(17);
/* 491:    */     
/* 492:540 */     ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 493:541 */     DataOutputStream out = new DataOutputStream(baos);
/* 494:542 */     out.write(0);
/* 495:543 */     for (SevenZArchiveEntry entry : this.files)
/* 496:    */     {
/* 497:544 */       out.write(entry.getName().getBytes("UTF-16LE"));
/* 498:545 */       out.writeShort(0);
/* 499:    */     }
/* 500:547 */     out.flush();
/* 501:548 */     byte[] contents = baos.toByteArray();
/* 502:549 */     writeUint64(header, contents.length);
/* 503:550 */     header.write(contents);
/* 504:    */   }
/* 505:    */   
/* 506:    */   private void writeFileCTimes(DataOutput header)
/* 507:    */     throws IOException
/* 508:    */   {
/* 509:554 */     int numCreationDates = 0;
/* 510:555 */     for (SevenZArchiveEntry entry : this.files) {
/* 511:556 */       if (entry.getHasCreationDate()) {
/* 512:557 */         numCreationDates++;
/* 513:    */       }
/* 514:    */     }
/* 515:560 */     if (numCreationDates > 0)
/* 516:    */     {
/* 517:561 */       header.write(18);
/* 518:    */       
/* 519:563 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 520:564 */       DataOutputStream out = new DataOutputStream(baos);
/* 521:565 */       if (numCreationDates != this.files.size())
/* 522:    */       {
/* 523:566 */         out.write(0);
/* 524:567 */         BitSet cTimes = new BitSet(this.files.size());
/* 525:568 */         for (int i = 0; i < this.files.size(); i++) {
/* 526:569 */           cTimes.set(i, ((SevenZArchiveEntry)this.files.get(i)).getHasCreationDate());
/* 527:    */         }
/* 528:571 */         writeBits(out, cTimes, this.files.size());
/* 529:    */       }
/* 530:    */       else
/* 531:    */       {
/* 532:573 */         out.write(1);
/* 533:    */       }
/* 534:575 */       out.write(0);
/* 535:576 */       for (SevenZArchiveEntry entry : this.files) {
/* 536:577 */         if (entry.getHasCreationDate()) {
/* 537:578 */           out.writeLong(Long.reverseBytes(SevenZArchiveEntry.javaTimeToNtfsTime(entry.getCreationDate())));
/* 538:    */         }
/* 539:    */       }
/* 540:582 */       out.flush();
/* 541:583 */       byte[] contents = baos.toByteArray();
/* 542:584 */       writeUint64(header, contents.length);
/* 543:585 */       header.write(contents);
/* 544:    */     }
/* 545:    */   }
/* 546:    */   
/* 547:    */   private void writeFileATimes(DataOutput header)
/* 548:    */     throws IOException
/* 549:    */   {
/* 550:590 */     int numAccessDates = 0;
/* 551:591 */     for (SevenZArchiveEntry entry : this.files) {
/* 552:592 */       if (entry.getHasAccessDate()) {
/* 553:593 */         numAccessDates++;
/* 554:    */       }
/* 555:    */     }
/* 556:596 */     if (numAccessDates > 0)
/* 557:    */     {
/* 558:597 */       header.write(19);
/* 559:    */       
/* 560:599 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 561:600 */       DataOutputStream out = new DataOutputStream(baos);
/* 562:601 */       if (numAccessDates != this.files.size())
/* 563:    */       {
/* 564:602 */         out.write(0);
/* 565:603 */         BitSet aTimes = new BitSet(this.files.size());
/* 566:604 */         for (int i = 0; i < this.files.size(); i++) {
/* 567:605 */           aTimes.set(i, ((SevenZArchiveEntry)this.files.get(i)).getHasAccessDate());
/* 568:    */         }
/* 569:607 */         writeBits(out, aTimes, this.files.size());
/* 570:    */       }
/* 571:    */       else
/* 572:    */       {
/* 573:609 */         out.write(1);
/* 574:    */       }
/* 575:611 */       out.write(0);
/* 576:612 */       for (SevenZArchiveEntry entry : this.files) {
/* 577:613 */         if (entry.getHasAccessDate()) {
/* 578:614 */           out.writeLong(Long.reverseBytes(SevenZArchiveEntry.javaTimeToNtfsTime(entry.getAccessDate())));
/* 579:    */         }
/* 580:    */       }
/* 581:618 */       out.flush();
/* 582:619 */       byte[] contents = baos.toByteArray();
/* 583:620 */       writeUint64(header, contents.length);
/* 584:621 */       header.write(contents);
/* 585:    */     }
/* 586:    */   }
/* 587:    */   
/* 588:    */   private void writeFileMTimes(DataOutput header)
/* 589:    */     throws IOException
/* 590:    */   {
/* 591:626 */     int numLastModifiedDates = 0;
/* 592:627 */     for (SevenZArchiveEntry entry : this.files) {
/* 593:628 */       if (entry.getHasLastModifiedDate()) {
/* 594:629 */         numLastModifiedDates++;
/* 595:    */       }
/* 596:    */     }
/* 597:632 */     if (numLastModifiedDates > 0)
/* 598:    */     {
/* 599:633 */       header.write(20);
/* 600:    */       
/* 601:635 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 602:636 */       DataOutputStream out = new DataOutputStream(baos);
/* 603:637 */       if (numLastModifiedDates != this.files.size())
/* 604:    */       {
/* 605:638 */         out.write(0);
/* 606:639 */         BitSet mTimes = new BitSet(this.files.size());
/* 607:640 */         for (int i = 0; i < this.files.size(); i++) {
/* 608:641 */           mTimes.set(i, ((SevenZArchiveEntry)this.files.get(i)).getHasLastModifiedDate());
/* 609:    */         }
/* 610:643 */         writeBits(out, mTimes, this.files.size());
/* 611:    */       }
/* 612:    */       else
/* 613:    */       {
/* 614:645 */         out.write(1);
/* 615:    */       }
/* 616:647 */       out.write(0);
/* 617:648 */       for (SevenZArchiveEntry entry : this.files) {
/* 618:649 */         if (entry.getHasLastModifiedDate()) {
/* 619:650 */           out.writeLong(Long.reverseBytes(SevenZArchiveEntry.javaTimeToNtfsTime(entry.getLastModifiedDate())));
/* 620:    */         }
/* 621:    */       }
/* 622:654 */       out.flush();
/* 623:655 */       byte[] contents = baos.toByteArray();
/* 624:656 */       writeUint64(header, contents.length);
/* 625:657 */       header.write(contents);
/* 626:    */     }
/* 627:    */   }
/* 628:    */   
/* 629:    */   private void writeFileWindowsAttributes(DataOutput header)
/* 630:    */     throws IOException
/* 631:    */   {
/* 632:662 */     int numWindowsAttributes = 0;
/* 633:663 */     for (SevenZArchiveEntry entry : this.files) {
/* 634:664 */       if (entry.getHasWindowsAttributes()) {
/* 635:665 */         numWindowsAttributes++;
/* 636:    */       }
/* 637:    */     }
/* 638:668 */     if (numWindowsAttributes > 0)
/* 639:    */     {
/* 640:669 */       header.write(21);
/* 641:    */       
/* 642:671 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 643:672 */       DataOutputStream out = new DataOutputStream(baos);
/* 644:673 */       if (numWindowsAttributes != this.files.size())
/* 645:    */       {
/* 646:674 */         out.write(0);
/* 647:675 */         BitSet attributes = new BitSet(this.files.size());
/* 648:676 */         for (int i = 0; i < this.files.size(); i++) {
/* 649:677 */           attributes.set(i, ((SevenZArchiveEntry)this.files.get(i)).getHasWindowsAttributes());
/* 650:    */         }
/* 651:679 */         writeBits(out, attributes, this.files.size());
/* 652:    */       }
/* 653:    */       else
/* 654:    */       {
/* 655:681 */         out.write(1);
/* 656:    */       }
/* 657:683 */       out.write(0);
/* 658:684 */       for (SevenZArchiveEntry entry : this.files) {
/* 659:685 */         if (entry.getHasWindowsAttributes()) {
/* 660:686 */           out.writeInt(Integer.reverseBytes(entry.getWindowsAttributes()));
/* 661:    */         }
/* 662:    */       }
/* 663:689 */       out.flush();
/* 664:690 */       byte[] contents = baos.toByteArray();
/* 665:691 */       writeUint64(header, contents.length);
/* 666:692 */       header.write(contents);
/* 667:    */     }
/* 668:    */   }
/* 669:    */   
/* 670:    */   private void writeUint64(DataOutput header, long value)
/* 671:    */     throws IOException
/* 672:    */   {
/* 673:697 */     int firstByte = 0;
/* 674:698 */     int mask = 128;
/* 675:700 */     for (int i = 0; i < 8; i++)
/* 676:    */     {
/* 677:701 */       if (value < 1L << 7 * (i + 1))
/* 678:    */       {
/* 679:702 */         firstByte = (int)(firstByte | value >>> 8 * i);
/* 680:703 */         break;
/* 681:    */       }
/* 682:705 */       firstByte |= mask;
/* 683:706 */       mask >>>= 1;
/* 684:    */     }
/* 685:708 */     header.write(firstByte);
/* 686:709 */     for (; i > 0; i--)
/* 687:    */     {
/* 688:710 */       header.write((int)(0xFF & value));
/* 689:711 */       value >>>= 8;
/* 690:    */     }
/* 691:    */   }
/* 692:    */   
/* 693:    */   private void writeBits(DataOutput header, BitSet bits, int length)
/* 694:    */     throws IOException
/* 695:    */   {
/* 696:716 */     int cache = 0;
/* 697:717 */     int shift = 7;
/* 698:718 */     for (int i = 0; i < length; i++)
/* 699:    */     {
/* 700:719 */       cache |= (bits.get(i) ? 1 : 0) << shift;
/* 701:720 */       shift--;
/* 702:720 */       if (shift < 0)
/* 703:    */       {
/* 704:721 */         header.write(cache);
/* 705:722 */         shift = 7;
/* 706:723 */         cache = 0;
/* 707:    */       }
/* 708:    */     }
/* 709:726 */     if (shift != 7) {
/* 710:727 */       header.write(cache);
/* 711:    */     }
/* 712:    */   }
/* 713:    */   
/* 714:    */   private static <T> Iterable<T> reverse(Iterable<T> i)
/* 715:    */   {
/* 716:732 */     LinkedList<T> l = new LinkedList();
/* 717:733 */     for (T t : i) {
/* 718:734 */       l.addFirst(t);
/* 719:    */     }
/* 720:736 */     return l;
/* 721:    */   }
/* 722:    */   
/* 723:    */   private class OutputStreamWrapper
/* 724:    */     extends OutputStream
/* 725:    */   {
/* 726:    */     private OutputStreamWrapper() {}
/* 727:    */     
/* 728:    */     public void write(int b)
/* 729:    */       throws IOException
/* 730:    */     {
/* 731:742 */       SevenZOutputFile.this.file.write(b);
/* 732:743 */       SevenZOutputFile.this.compressedCrc32.update(b);
/* 733:744 */       SevenZOutputFile.access$408(SevenZOutputFile.this);
/* 734:    */     }
/* 735:    */     
/* 736:    */     public void write(byte[] b)
/* 737:    */       throws IOException
/* 738:    */     {
/* 739:749 */       write(b, 0, b.length);
/* 740:    */     }
/* 741:    */     
/* 742:    */     public void write(byte[] b, int off, int len)
/* 743:    */       throws IOException
/* 744:    */     {
/* 745:755 */       SevenZOutputFile.this.file.write(b, off, len);
/* 746:756 */       SevenZOutputFile.this.compressedCrc32.update(b, off, len);
/* 747:757 */       SevenZOutputFile.access$414(SevenZOutputFile.this, len);
/* 748:    */     }
/* 749:    */     
/* 750:    */     public void flush()
/* 751:    */       throws IOException
/* 752:    */     {}
/* 753:    */     
/* 754:    */     public void close()
/* 755:    */       throws IOException
/* 756:    */     {}
/* 757:    */   }
/* 758:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.sevenz.SevenZOutputFile
 * JD-Core Version:    0.7.0.1
 */