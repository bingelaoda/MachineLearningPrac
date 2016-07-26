/*   1:    */ package org.apache.commons.compress.archivers.sevenz;
/*   2:    */ 
/*   3:    */ import java.io.ByteArrayInputStream;
/*   4:    */ import java.io.Closeable;
/*   5:    */ import java.io.DataInput;
/*   6:    */ import java.io.DataInputStream;
/*   7:    */ import java.io.File;
/*   8:    */ import java.io.IOException;
/*   9:    */ import java.io.InputStream;
/*  10:    */ import java.io.RandomAccessFile;
/*  11:    */ import java.util.Arrays;
/*  12:    */ import java.util.BitSet;
/*  13:    */ import java.util.LinkedList;
/*  14:    */ import java.util.zip.CRC32;
/*  15:    */ import org.apache.commons.compress.utils.BoundedInputStream;
/*  16:    */ import org.apache.commons.compress.utils.CRC32VerifyingInputStream;
/*  17:    */ import org.apache.commons.compress.utils.IOUtils;
/*  18:    */ 
/*  19:    */ public class SevenZFile
/*  20:    */   implements Closeable
/*  21:    */ {
/*  22:    */   static final int SIGNATURE_HEADER_SIZE = 32;
/*  23:    */   private final String fileName;
/*  24:    */   private RandomAccessFile file;
/*  25:    */   private final Archive archive;
/*  26: 73 */   private int currentEntryIndex = -1;
/*  27: 74 */   private int currentFolderIndex = -1;
/*  28: 75 */   private InputStream currentFolderInputStream = null;
/*  29: 76 */   private InputStream currentEntryInputStream = null;
/*  30:    */   private byte[] password;
/*  31: 79 */   static final byte[] sevenZSignature = { 55, 122, -68, -81, 39, 28 };
/*  32:    */   
/*  33:    */   public SevenZFile(File filename, byte[] password)
/*  34:    */     throws IOException
/*  35:    */   {
/*  36: 93 */     boolean succeeded = false;
/*  37: 94 */     this.file = new RandomAccessFile(filename, "r");
/*  38: 95 */     this.fileName = filename.getAbsolutePath();
/*  39:    */     try
/*  40:    */     {
/*  41: 97 */       this.archive = readHeaders(password);
/*  42: 98 */       if (password != null)
/*  43:    */       {
/*  44: 99 */         this.password = new byte[password.length];
/*  45:100 */         System.arraycopy(password, 0, this.password, 0, password.length);
/*  46:    */       }
/*  47:    */       else
/*  48:    */       {
/*  49:102 */         this.password = null;
/*  50:    */       }
/*  51:104 */       succeeded = true;
/*  52:    */     }
/*  53:    */     finally
/*  54:    */     {
/*  55:106 */       if (!succeeded) {
/*  56:107 */         this.file.close();
/*  57:    */       }
/*  58:    */     }
/*  59:    */   }
/*  60:    */   
/*  61:    */   public SevenZFile(File filename)
/*  62:    */     throws IOException
/*  63:    */   {
/*  64:119 */     this(filename, null);
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void close()
/*  68:    */     throws IOException
/*  69:    */   {
/*  70:127 */     if (this.file != null) {
/*  71:    */       try
/*  72:    */       {
/*  73:129 */         this.file.close();
/*  74:    */       }
/*  75:    */       finally
/*  76:    */       {
/*  77:131 */         this.file = null;
/*  78:132 */         if (this.password != null) {
/*  79:133 */           Arrays.fill(this.password, (byte)0);
/*  80:    */         }
/*  81:135 */         this.password = null;
/*  82:    */       }
/*  83:    */     }
/*  84:    */   }
/*  85:    */   
/*  86:    */   public SevenZArchiveEntry getNextEntry()
/*  87:    */     throws IOException
/*  88:    */   {
/*  89:148 */     if (this.currentEntryIndex >= this.archive.files.length - 1) {
/*  90:149 */       return null;
/*  91:    */     }
/*  92:151 */     this.currentEntryIndex += 1;
/*  93:152 */     SevenZArchiveEntry entry = this.archive.files[this.currentEntryIndex];
/*  94:153 */     buildDecodingStream();
/*  95:154 */     return entry;
/*  96:    */   }
/*  97:    */   
/*  98:    */   private Archive readHeaders(byte[] password)
/*  99:    */     throws IOException
/* 100:    */   {
/* 101:158 */     byte[] signature = new byte[6];
/* 102:159 */     this.file.readFully(signature);
/* 103:160 */     if (!Arrays.equals(signature, sevenZSignature)) {
/* 104:161 */       throw new IOException("Bad 7z signature");
/* 105:    */     }
/* 106:164 */     byte archiveVersionMajor = this.file.readByte();
/* 107:165 */     byte archiveVersionMinor = this.file.readByte();
/* 108:166 */     if (archiveVersionMajor != 0) {
/* 109:167 */       throw new IOException(String.format("Unsupported 7z version (%d,%d)", new Object[] { Byte.valueOf(archiveVersionMajor), Byte.valueOf(archiveVersionMinor) }));
/* 110:    */     }
/* 111:171 */     long startHeaderCrc = 0xFFFFFFFF & Integer.reverseBytes(this.file.readInt());
/* 112:172 */     StartHeader startHeader = readStartHeader(startHeaderCrc);
/* 113:    */     
/* 114:174 */     int nextHeaderSizeInt = (int)startHeader.nextHeaderSize;
/* 115:175 */     if (nextHeaderSizeInt != startHeader.nextHeaderSize) {
/* 116:176 */       throw new IOException("cannot handle nextHeaderSize " + startHeader.nextHeaderSize);
/* 117:    */     }
/* 118:178 */     this.file.seek(32L + startHeader.nextHeaderOffset);
/* 119:179 */     byte[] nextHeader = new byte[nextHeaderSizeInt];
/* 120:180 */     this.file.readFully(nextHeader);
/* 121:181 */     CRC32 crc = new CRC32();
/* 122:182 */     crc.update(nextHeader);
/* 123:183 */     if (startHeader.nextHeaderCrc != crc.getValue()) {
/* 124:184 */       throw new IOException("NextHeader CRC mismatch");
/* 125:    */     }
/* 126:187 */     ByteArrayInputStream byteStream = new ByteArrayInputStream(nextHeader);
/* 127:188 */     DataInputStream nextHeaderInputStream = new DataInputStream(byteStream);
/* 128:    */     
/* 129:190 */     Archive archive = new Archive();
/* 130:191 */     int nid = nextHeaderInputStream.readUnsignedByte();
/* 131:192 */     if (nid == 23)
/* 132:    */     {
/* 133:193 */       nextHeaderInputStream = readEncodedHeader(nextHeaderInputStream, archive, password);
/* 134:    */       
/* 135:    */ 
/* 136:196 */       archive = new Archive();
/* 137:197 */       nid = nextHeaderInputStream.readUnsignedByte();
/* 138:    */     }
/* 139:199 */     if (nid == 1)
/* 140:    */     {
/* 141:200 */       readHeader(nextHeaderInputStream, archive);
/* 142:201 */       nextHeaderInputStream.close();
/* 143:    */     }
/* 144:    */     else
/* 145:    */     {
/* 146:203 */       throw new IOException("Broken or unsupported archive: no Header");
/* 147:    */     }
/* 148:205 */     return archive;
/* 149:    */   }
/* 150:    */   
/* 151:    */   private StartHeader readStartHeader(long startHeaderCrc)
/* 152:    */     throws IOException
/* 153:    */   {
/* 154:209 */     StartHeader startHeader = new StartHeader();
/* 155:210 */     DataInputStream dataInputStream = null;
/* 156:    */     try
/* 157:    */     {
/* 158:212 */       dataInputStream = new DataInputStream(new CRC32VerifyingInputStream(new BoundedRandomAccessFileInputStream(this.file, 20L), 20L, startHeaderCrc));
/* 159:    */       
/* 160:214 */       startHeader.nextHeaderOffset = Long.reverseBytes(dataInputStream.readLong());
/* 161:215 */       startHeader.nextHeaderSize = Long.reverseBytes(dataInputStream.readLong());
/* 162:216 */       startHeader.nextHeaderCrc = (0xFFFFFFFF & Integer.reverseBytes(dataInputStream.readInt()));
/* 163:217 */       return startHeader;
/* 164:    */     }
/* 165:    */     finally
/* 166:    */     {
/* 167:219 */       if (dataInputStream != null) {
/* 168:220 */         dataInputStream.close();
/* 169:    */       }
/* 170:    */     }
/* 171:    */   }
/* 172:    */   
/* 173:    */   private void readHeader(DataInput header, Archive archive)
/* 174:    */     throws IOException
/* 175:    */   {
/* 176:226 */     int nid = header.readUnsignedByte();
/* 177:228 */     if (nid == 2)
/* 178:    */     {
/* 179:229 */       readArchiveProperties(header);
/* 180:230 */       nid = header.readUnsignedByte();
/* 181:    */     }
/* 182:233 */     if (nid == 3) {
/* 183:234 */       throw new IOException("Additional streams unsupported");
/* 184:    */     }
/* 185:238 */     if (nid == 4)
/* 186:    */     {
/* 187:239 */       readStreamsInfo(header, archive);
/* 188:240 */       nid = header.readUnsignedByte();
/* 189:    */     }
/* 190:243 */     if (nid == 5)
/* 191:    */     {
/* 192:244 */       readFilesInfo(header, archive);
/* 193:245 */       nid = header.readUnsignedByte();
/* 194:    */     }
/* 195:248 */     if (nid != 0) {
/* 196:249 */       throw new IOException("Badly terminated header, found " + nid);
/* 197:    */     }
/* 198:    */   }
/* 199:    */   
/* 200:    */   private void readArchiveProperties(DataInput input)
/* 201:    */     throws IOException
/* 202:    */   {
/* 203:255 */     int nid = input.readUnsignedByte();
/* 204:256 */     while (nid != 0)
/* 205:    */     {
/* 206:257 */       long propertySize = readUint64(input);
/* 207:258 */       byte[] property = new byte[(int)propertySize];
/* 208:259 */       input.readFully(property);
/* 209:260 */       nid = input.readUnsignedByte();
/* 210:    */     }
/* 211:    */   }
/* 212:    */   
/* 213:    */   private DataInputStream readEncodedHeader(DataInputStream header, Archive archive, byte[] password)
/* 214:    */     throws IOException
/* 215:    */   {
/* 216:266 */     readStreamsInfo(header, archive);
/* 217:    */     
/* 218:    */ 
/* 219:269 */     Folder folder = archive.folders[0];
/* 220:270 */     int firstPackStreamIndex = 0;
/* 221:271 */     long folderOffset = 32L + archive.packPos + 0L;
/* 222:    */     
/* 223:    */ 
/* 224:274 */     this.file.seek(folderOffset);
/* 225:275 */     InputStream inputStreamStack = new BoundedRandomAccessFileInputStream(this.file, archive.packSizes[0]);
/* 226:277 */     for (Coder coder : folder.getOrderedCoders())
/* 227:    */     {
/* 228:278 */       if ((coder.numInStreams != 1L) || (coder.numOutStreams != 1L)) {
/* 229:279 */         throw new IOException("Multi input/output stream coders are not yet supported");
/* 230:    */       }
/* 231:281 */       inputStreamStack = Coders.addDecoder(this.fileName, inputStreamStack, folder.getUnpackSizeForCoder(coder), coder, password);
/* 232:    */     }
/* 233:284 */     if (folder.hasCrc) {
/* 234:285 */       inputStreamStack = new CRC32VerifyingInputStream(inputStreamStack, folder.getUnpackSize(), folder.crc);
/* 235:    */     }
/* 236:288 */     byte[] nextHeader = new byte[(int)folder.getUnpackSize()];
/* 237:289 */     DataInputStream nextHeaderInputStream = new DataInputStream(inputStreamStack);
/* 238:    */     try
/* 239:    */     {
/* 240:291 */       nextHeaderInputStream.readFully(nextHeader);
/* 241:    */     }
/* 242:    */     finally
/* 243:    */     {
/* 244:293 */       nextHeaderInputStream.close();
/* 245:    */     }
/* 246:295 */     return new DataInputStream(new ByteArrayInputStream(nextHeader));
/* 247:    */   }
/* 248:    */   
/* 249:    */   private void readStreamsInfo(DataInput header, Archive archive)
/* 250:    */     throws IOException
/* 251:    */   {
/* 252:299 */     int nid = header.readUnsignedByte();
/* 253:301 */     if (nid == 6)
/* 254:    */     {
/* 255:302 */       readPackInfo(header, archive);
/* 256:303 */       nid = header.readUnsignedByte();
/* 257:    */     }
/* 258:306 */     if (nid == 7)
/* 259:    */     {
/* 260:307 */       readUnpackInfo(header, archive);
/* 261:308 */       nid = header.readUnsignedByte();
/* 262:    */     }
/* 263:    */     else
/* 264:    */     {
/* 265:311 */       archive.folders = new Folder[0];
/* 266:    */     }
/* 267:314 */     if (nid == 8)
/* 268:    */     {
/* 269:315 */       readSubStreamsInfo(header, archive);
/* 270:316 */       nid = header.readUnsignedByte();
/* 271:    */     }
/* 272:319 */     if (nid != 0) {
/* 273:320 */       throw new IOException("Badly terminated StreamsInfo");
/* 274:    */     }
/* 275:    */   }
/* 276:    */   
/* 277:    */   private void readPackInfo(DataInput header, Archive archive)
/* 278:    */     throws IOException
/* 279:    */   {
/* 280:325 */     archive.packPos = readUint64(header);
/* 281:326 */     long numPackStreams = readUint64(header);
/* 282:327 */     int nid = header.readUnsignedByte();
/* 283:328 */     if (nid == 9)
/* 284:    */     {
/* 285:329 */       archive.packSizes = new long[(int)numPackStreams];
/* 286:330 */       for (int i = 0; i < archive.packSizes.length; i++) {
/* 287:331 */         archive.packSizes[i] = readUint64(header);
/* 288:    */       }
/* 289:333 */       nid = header.readUnsignedByte();
/* 290:    */     }
/* 291:336 */     if (nid == 10)
/* 292:    */     {
/* 293:337 */       archive.packCrcsDefined = readAllOrBits(header, (int)numPackStreams);
/* 294:338 */       archive.packCrcs = new long[(int)numPackStreams];
/* 295:339 */       for (int i = 0; i < (int)numPackStreams; i++) {
/* 296:340 */         if (archive.packCrcsDefined.get(i)) {
/* 297:341 */           archive.packCrcs[i] = (0xFFFFFFFF & Integer.reverseBytes(header.readInt()));
/* 298:    */         }
/* 299:    */       }
/* 300:345 */       nid = header.readUnsignedByte();
/* 301:    */     }
/* 302:348 */     if (nid != 0) {
/* 303:349 */       throw new IOException("Badly terminated PackInfo (" + nid + ")");
/* 304:    */     }
/* 305:    */   }
/* 306:    */   
/* 307:    */   private void readUnpackInfo(DataInput header, Archive archive)
/* 308:    */     throws IOException
/* 309:    */   {
/* 310:354 */     int nid = header.readUnsignedByte();
/* 311:355 */     if (nid != 11) {
/* 312:356 */       throw new IOException("Expected kFolder, got " + nid);
/* 313:    */     }
/* 314:358 */     long numFolders = readUint64(header);
/* 315:359 */     Folder[] folders = new Folder[(int)numFolders];
/* 316:360 */     archive.folders = folders;
/* 317:361 */     int external = header.readUnsignedByte();
/* 318:362 */     if (external != 0) {
/* 319:363 */       throw new IOException("External unsupported");
/* 320:    */     }
/* 321:365 */     for (int i = 0; i < (int)numFolders; i++) {
/* 322:366 */       folders[i] = readFolder(header);
/* 323:    */     }
/* 324:370 */     nid = header.readUnsignedByte();
/* 325:371 */     if (nid != 12) {
/* 326:372 */       throw new IOException("Expected kCodersUnpackSize, got " + nid);
/* 327:    */     }
/* 328:374 */     for (Folder folder : folders)
/* 329:    */     {
/* 330:375 */       folder.unpackSizes = new long[(int)folder.totalOutputStreams];
/* 331:376 */       for (int i = 0; i < folder.totalOutputStreams; i++) {
/* 332:377 */         folder.unpackSizes[i] = readUint64(header);
/* 333:    */       }
/* 334:    */     }
/* 335:381 */     nid = header.readUnsignedByte();
/* 336:382 */     if (nid == 10)
/* 337:    */     {
/* 338:383 */       BitSet crcsDefined = readAllOrBits(header, (int)numFolders);
/* 339:384 */       for (int i = 0; i < (int)numFolders; i++) {
/* 340:385 */         if (crcsDefined.get(i))
/* 341:    */         {
/* 342:386 */           folders[i].hasCrc = true;
/* 343:387 */           folders[i].crc = (0xFFFFFFFF & Integer.reverseBytes(header.readInt()));
/* 344:    */         }
/* 345:    */         else
/* 346:    */         {
/* 347:389 */           folders[i].hasCrc = false;
/* 348:    */         }
/* 349:    */       }
/* 350:393 */       nid = header.readUnsignedByte();
/* 351:    */     }
/* 352:396 */     if (nid != 0) {
/* 353:397 */       throw new IOException("Badly terminated UnpackInfo");
/* 354:    */     }
/* 355:    */   }
/* 356:    */   
/* 357:    */   private void readSubStreamsInfo(DataInput header, Archive archive)
/* 358:    */     throws IOException
/* 359:    */   {
/* 360:402 */     for (Folder folder : archive.folders) {
/* 361:403 */       folder.numUnpackSubStreams = 1;
/* 362:    */     }
/* 363:405 */     int totalUnpackStreams = archive.folders.length;
/* 364:    */     
/* 365:407 */     int nid = header.readUnsignedByte();
/* 366:408 */     if (nid == 13)
/* 367:    */     {
/* 368:409 */       totalUnpackStreams = 0;
/* 369:410 */       for (Folder folder : archive.folders)
/* 370:    */       {
/* 371:411 */         long numStreams = readUint64(header);
/* 372:412 */         folder.numUnpackSubStreams = ((int)numStreams);
/* 373:413 */         totalUnpackStreams = (int)(totalUnpackStreams + numStreams);
/* 374:    */       }
/* 375:415 */       nid = header.readUnsignedByte();
/* 376:    */     }
/* 377:418 */     SubStreamsInfo subStreamsInfo = new SubStreamsInfo();
/* 378:419 */     subStreamsInfo.unpackSizes = new long[totalUnpackStreams];
/* 379:420 */     subStreamsInfo.hasCrc = new BitSet(totalUnpackStreams);
/* 380:421 */     subStreamsInfo.crcs = new long[totalUnpackStreams];
/* 381:    */     
/* 382:423 */     int nextUnpackStream = 0;
/* 383:424 */     for (Folder folder : archive.folders) {
/* 384:425 */       if (folder.numUnpackSubStreams != 0)
/* 385:    */       {
/* 386:428 */         long sum = 0L;
/* 387:429 */         if (nid == 9) {
/* 388:430 */           for (int i = 0; i < folder.numUnpackSubStreams - 1; i++)
/* 389:    */           {
/* 390:431 */             long size = readUint64(header);
/* 391:432 */             subStreamsInfo.unpackSizes[(nextUnpackStream++)] = size;
/* 392:433 */             sum += size;
/* 393:    */           }
/* 394:    */         }
/* 395:436 */         subStreamsInfo.unpackSizes[(nextUnpackStream++)] = (folder.getUnpackSize() - sum);
/* 396:    */       }
/* 397:    */     }
/* 398:438 */     if (nid == 9) {
/* 399:439 */       nid = header.readUnsignedByte();
/* 400:    */     }
/* 401:442 */     int numDigests = 0;
/* 402:443 */     for (Folder folder : archive.folders) {
/* 403:444 */       if ((folder.numUnpackSubStreams != 1) || (!folder.hasCrc)) {
/* 404:445 */         numDigests += folder.numUnpackSubStreams;
/* 405:    */       }
/* 406:    */     }
/* 407:449 */     if (nid == 10)
/* 408:    */     {
/* 409:450 */       BitSet hasMissingCrc = readAllOrBits(header, numDigests);
/* 410:451 */       long[] missingCrcs = new long[numDigests];
/* 411:452 */       for (int i = 0; i < numDigests; i++) {
/* 412:453 */         if (hasMissingCrc.get(i)) {
/* 413:454 */           missingCrcs[i] = (0xFFFFFFFF & Integer.reverseBytes(header.readInt()));
/* 414:    */         }
/* 415:    */       }
/* 416:457 */       int nextCrc = 0;
/* 417:458 */       int nextMissingCrc = 0;
/* 418:459 */       for (Folder folder : archive.folders) {
/* 419:460 */         if ((folder.numUnpackSubStreams == 1) && (folder.hasCrc))
/* 420:    */         {
/* 421:461 */           subStreamsInfo.hasCrc.set(nextCrc, true);
/* 422:462 */           subStreamsInfo.crcs[nextCrc] = folder.crc;
/* 423:463 */           nextCrc++;
/* 424:    */         }
/* 425:    */         else
/* 426:    */         {
/* 427:465 */           for (int i = 0; i < folder.numUnpackSubStreams; i++)
/* 428:    */           {
/* 429:466 */             subStreamsInfo.hasCrc.set(nextCrc, hasMissingCrc.get(nextMissingCrc));
/* 430:467 */             subStreamsInfo.crcs[nextCrc] = missingCrcs[nextMissingCrc];
/* 431:468 */             nextCrc++;
/* 432:469 */             nextMissingCrc++;
/* 433:    */           }
/* 434:    */         }
/* 435:    */       }
/* 436:474 */       nid = header.readUnsignedByte();
/* 437:    */     }
/* 438:477 */     if (nid != 0) {
/* 439:478 */       throw new IOException("Badly terminated SubStreamsInfo");
/* 440:    */     }
/* 441:481 */     archive.subStreamsInfo = subStreamsInfo;
/* 442:    */   }
/* 443:    */   
/* 444:    */   private Folder readFolder(DataInput header)
/* 445:    */     throws IOException
/* 446:    */   {
/* 447:485 */     Folder folder = new Folder();
/* 448:    */     
/* 449:487 */     long numCoders = readUint64(header);
/* 450:488 */     Coder[] coders = new Coder[(int)numCoders];
/* 451:489 */     long totalInStreams = 0L;
/* 452:490 */     long totalOutStreams = 0L;
/* 453:491 */     for (int i = 0; i < coders.length; i++)
/* 454:    */     {
/* 455:492 */       coders[i] = new Coder();
/* 456:493 */       int bits = header.readUnsignedByte();
/* 457:494 */       int idSize = bits & 0xF;
/* 458:495 */       boolean isSimple = (bits & 0x10) == 0;
/* 459:496 */       boolean hasAttributes = (bits & 0x20) != 0;
/* 460:497 */       boolean moreAlternativeMethods = (bits & 0x80) != 0;
/* 461:    */       
/* 462:499 */       coders[i].decompressionMethodId = new byte[idSize];
/* 463:500 */       header.readFully(coders[i].decompressionMethodId);
/* 464:501 */       if (isSimple)
/* 465:    */       {
/* 466:502 */         coders[i].numInStreams = 1L;
/* 467:503 */         coders[i].numOutStreams = 1L;
/* 468:    */       }
/* 469:    */       else
/* 470:    */       {
/* 471:505 */         coders[i].numInStreams = readUint64(header);
/* 472:506 */         coders[i].numOutStreams = readUint64(header);
/* 473:    */       }
/* 474:508 */       totalInStreams += coders[i].numInStreams;
/* 475:509 */       totalOutStreams += coders[i].numOutStreams;
/* 476:510 */       if (hasAttributes)
/* 477:    */       {
/* 478:511 */         long propertiesSize = readUint64(header);
/* 479:512 */         coders[i].properties = new byte[(int)propertiesSize];
/* 480:513 */         header.readFully(coders[i].properties);
/* 481:    */       }
/* 482:516 */       if (moreAlternativeMethods) {
/* 483:517 */         throw new IOException("Alternative methods are unsupported, please report. The reference implementation doesn't support them either.");
/* 484:    */       }
/* 485:    */     }
/* 486:521 */     folder.coders = coders;
/* 487:522 */     folder.totalInputStreams = totalInStreams;
/* 488:523 */     folder.totalOutputStreams = totalOutStreams;
/* 489:525 */     if (totalOutStreams == 0L) {
/* 490:526 */       throw new IOException("Total output streams can't be 0");
/* 491:    */     }
/* 492:528 */     long numBindPairs = totalOutStreams - 1L;
/* 493:529 */     BindPair[] bindPairs = new BindPair[(int)numBindPairs];
/* 494:530 */     for (int i = 0; i < bindPairs.length; i++)
/* 495:    */     {
/* 496:531 */       bindPairs[i] = new BindPair();
/* 497:532 */       bindPairs[i].inIndex = readUint64(header);
/* 498:533 */       bindPairs[i].outIndex = readUint64(header);
/* 499:    */     }
/* 500:535 */     folder.bindPairs = bindPairs;
/* 501:537 */     if (totalInStreams < numBindPairs) {
/* 502:538 */       throw new IOException("Total input streams can't be less than the number of bind pairs");
/* 503:    */     }
/* 504:540 */     long numPackedStreams = totalInStreams - numBindPairs;
/* 505:541 */     long[] packedStreams = new long[(int)numPackedStreams];
/* 506:542 */     if (numPackedStreams == 1L)
/* 507:    */     {
/* 508:544 */       for (int i = 0; i < (int)totalInStreams; i++) {
/* 509:545 */         if (folder.findBindPairForInStream(i) < 0) {
/* 510:    */           break;
/* 511:    */         }
/* 512:    */       }
/* 513:549 */       if (i == (int)totalInStreams) {
/* 514:550 */         throw new IOException("Couldn't find stream's bind pair index");
/* 515:    */       }
/* 516:552 */       packedStreams[0] = i;
/* 517:    */     }
/* 518:    */     else
/* 519:    */     {
/* 520:554 */       for (int i = 0; i < (int)numPackedStreams; i++) {
/* 521:555 */         packedStreams[i] = readUint64(header);
/* 522:    */       }
/* 523:    */     }
/* 524:558 */     folder.packedStreams = packedStreams;
/* 525:    */     
/* 526:560 */     return folder;
/* 527:    */   }
/* 528:    */   
/* 529:    */   private BitSet readAllOrBits(DataInput header, int size)
/* 530:    */     throws IOException
/* 531:    */   {
/* 532:564 */     int areAllDefined = header.readUnsignedByte();
/* 533:    */     BitSet bits;
/* 534:566 */     if (areAllDefined != 0)
/* 535:    */     {
/* 536:567 */       BitSet bits = new BitSet(size);
/* 537:568 */       for (int i = 0; i < size; i++) {
/* 538:569 */         bits.set(i, true);
/* 539:    */       }
/* 540:    */     }
/* 541:    */     else
/* 542:    */     {
/* 543:572 */       bits = readBits(header, size);
/* 544:    */     }
/* 545:574 */     return bits;
/* 546:    */   }
/* 547:    */   
/* 548:    */   private BitSet readBits(DataInput header, int size)
/* 549:    */     throws IOException
/* 550:    */   {
/* 551:578 */     BitSet bits = new BitSet(size);
/* 552:579 */     int mask = 0;
/* 553:580 */     int cache = 0;
/* 554:581 */     for (int i = 0; i < size; i++)
/* 555:    */     {
/* 556:582 */       if (mask == 0)
/* 557:    */       {
/* 558:583 */         mask = 128;
/* 559:584 */         cache = header.readUnsignedByte();
/* 560:    */       }
/* 561:586 */       bits.set(i, (cache & mask) != 0);
/* 562:587 */       mask >>>= 1;
/* 563:    */     }
/* 564:589 */     return bits;
/* 565:    */   }
/* 566:    */   
/* 567:    */   private void readFilesInfo(DataInput header, Archive archive)
/* 568:    */     throws IOException
/* 569:    */   {
/* 570:593 */     long numFiles = readUint64(header);
/* 571:594 */     SevenZArchiveEntry[] files = new SevenZArchiveEntry[(int)numFiles];
/* 572:595 */     for (int i = 0; i < files.length; i++) {
/* 573:596 */       files[i] = new SevenZArchiveEntry();
/* 574:    */     }
/* 575:598 */     BitSet isEmptyStream = null;
/* 576:599 */     BitSet isEmptyFile = null;
/* 577:600 */     BitSet isAnti = null;
/* 578:    */     for (;;)
/* 579:    */     {
/* 580:602 */       int propertyType = header.readUnsignedByte();
/* 581:603 */       if (propertyType == 0) {
/* 582:    */         break;
/* 583:    */       }
/* 584:606 */       long size = readUint64(header);
/* 585:607 */       switch (propertyType)
/* 586:    */       {
/* 587:    */       case 14: 
/* 588:609 */         isEmptyStream = readBits(header, files.length);
/* 589:610 */         break;
/* 590:    */       case 15: 
/* 591:613 */         if (isEmptyStream == null) {
/* 592:614 */           throw new IOException("Header format error: kEmptyStream must appear before kEmptyFile");
/* 593:    */         }
/* 594:616 */         isEmptyFile = readBits(header, isEmptyStream.cardinality());
/* 595:617 */         break;
/* 596:    */       case 16: 
/* 597:620 */         if (isEmptyStream == null) {
/* 598:621 */           throw new IOException("Header format error: kEmptyStream must appear before kAnti");
/* 599:    */         }
/* 600:623 */         isAnti = readBits(header, isEmptyStream.cardinality());
/* 601:624 */         break;
/* 602:    */       case 17: 
/* 603:627 */         int external = header.readUnsignedByte();
/* 604:628 */         if (external != 0) {
/* 605:629 */           throw new IOException("Not implemented");
/* 606:    */         }
/* 607:631 */         if ((size - 1L & 1L) != 0L) {
/* 608:632 */           throw new IOException("File names length invalid");
/* 609:    */         }
/* 610:634 */         byte[] names = new byte[(int)(size - 1L)];
/* 611:635 */         header.readFully(names);
/* 612:636 */         int nextFile = 0;
/* 613:637 */         int nextName = 0;
/* 614:638 */         for (int i = 0; i < names.length; i += 2) {
/* 615:639 */           if ((names[i] == 0) && (names[(i + 1)] == 0))
/* 616:    */           {
/* 617:640 */             files[(nextFile++)].setName(new String(names, nextName, i - nextName, "UTF-16LE"));
/* 618:641 */             nextName = i + 2;
/* 619:    */           }
/* 620:    */         }
/* 621:644 */         if ((nextName != names.length) || (nextFile != files.length)) {
/* 622:645 */           throw new IOException("Error parsing file names");
/* 623:    */         }
/* 624:648 */         break;
/* 625:    */       case 18: 
/* 626:651 */         BitSet timesDefined = readAllOrBits(header, files.length);
/* 627:652 */         int external = header.readUnsignedByte();
/* 628:653 */         if (external != 0) {
/* 629:654 */           throw new IOException("Unimplemented");
/* 630:    */         }
/* 631:656 */         for (int i = 0; i < files.length; i++)
/* 632:    */         {
/* 633:657 */           files[i].setHasCreationDate(timesDefined.get(i));
/* 634:658 */           if (files[i].getHasCreationDate()) {
/* 635:659 */             files[i].setCreationDate(Long.reverseBytes(header.readLong()));
/* 636:    */           }
/* 637:    */         }
/* 638:663 */         break;
/* 639:    */       case 19: 
/* 640:666 */         BitSet timesDefined = readAllOrBits(header, files.length);
/* 641:667 */         int external = header.readUnsignedByte();
/* 642:668 */         if (external != 0) {
/* 643:669 */           throw new IOException("Unimplemented");
/* 644:    */         }
/* 645:671 */         for (int i = 0; i < files.length; i++)
/* 646:    */         {
/* 647:672 */           files[i].setHasAccessDate(timesDefined.get(i));
/* 648:673 */           if (files[i].getHasAccessDate()) {
/* 649:674 */             files[i].setAccessDate(Long.reverseBytes(header.readLong()));
/* 650:    */           }
/* 651:    */         }
/* 652:678 */         break;
/* 653:    */       case 20: 
/* 654:681 */         BitSet timesDefined = readAllOrBits(header, files.length);
/* 655:682 */         int external = header.readUnsignedByte();
/* 656:683 */         if (external != 0) {
/* 657:684 */           throw new IOException("Unimplemented");
/* 658:    */         }
/* 659:686 */         for (int i = 0; i < files.length; i++)
/* 660:    */         {
/* 661:687 */           files[i].setHasLastModifiedDate(timesDefined.get(i));
/* 662:688 */           if (files[i].getHasLastModifiedDate()) {
/* 663:689 */             files[i].setLastModifiedDate(Long.reverseBytes(header.readLong()));
/* 664:    */           }
/* 665:    */         }
/* 666:693 */         break;
/* 667:    */       case 21: 
/* 668:696 */         BitSet attributesDefined = readAllOrBits(header, files.length);
/* 669:697 */         int external = header.readUnsignedByte();
/* 670:698 */         if (external != 0) {
/* 671:699 */           throw new IOException("Unimplemented");
/* 672:    */         }
/* 673:701 */         for (int i = 0; i < files.length; i++)
/* 674:    */         {
/* 675:702 */           files[i].setHasWindowsAttributes(attributesDefined.get(i));
/* 676:703 */           if (files[i].getHasWindowsAttributes()) {
/* 677:704 */             files[i].setWindowsAttributes(Integer.reverseBytes(header.readInt()));
/* 678:    */           }
/* 679:    */         }
/* 680:708 */         break;
/* 681:    */       case 24: 
/* 682:711 */         throw new IOException("kStartPos is unsupported, please report");
/* 683:    */       case 25: 
/* 684:717 */         if (skipBytesFully(header, size) < size) {
/* 685:718 */           throw new IOException("Incomplete kDummy property");
/* 686:    */         }
/* 687:    */         break;
/* 688:    */       case 22: 
/* 689:    */       case 23: 
/* 690:    */       default: 
/* 691:725 */         if (skipBytesFully(header, size) < size) {
/* 692:726 */           throw new IOException("Incomplete property of type " + propertyType);
/* 693:    */         }
/* 694:    */         break;
/* 695:    */       }
/* 696:    */     }
/* 697:732 */     int nonEmptyFileCounter = 0;
/* 698:733 */     int emptyFileCounter = 0;
/* 699:734 */     for (int i = 0; i < files.length; i++)
/* 700:    */     {
/* 701:735 */       files[i].setHasStream(isEmptyStream == null);
/* 702:736 */       if (files[i].hasStream())
/* 703:    */       {
/* 704:737 */         files[i].setDirectory(false);
/* 705:738 */         files[i].setAntiItem(false);
/* 706:739 */         files[i].setHasCrc(archive.subStreamsInfo.hasCrc.get(nonEmptyFileCounter));
/* 707:740 */         files[i].setCrcValue(archive.subStreamsInfo.crcs[nonEmptyFileCounter]);
/* 708:741 */         files[i].setSize(archive.subStreamsInfo.unpackSizes[nonEmptyFileCounter]);
/* 709:742 */         nonEmptyFileCounter++;
/* 710:    */       }
/* 711:    */       else
/* 712:    */       {
/* 713:744 */         files[i].setDirectory(isEmptyFile == null);
/* 714:745 */         files[i].setAntiItem(isAnti == null ? false : isAnti.get(emptyFileCounter));
/* 715:746 */         files[i].setHasCrc(false);
/* 716:747 */         files[i].setSize(0L);
/* 717:748 */         emptyFileCounter++;
/* 718:    */       }
/* 719:    */     }
/* 720:751 */     archive.files = files;
/* 721:752 */     calculateStreamMap(archive);
/* 722:    */   }
/* 723:    */   
/* 724:    */   private void calculateStreamMap(Archive archive)
/* 725:    */     throws IOException
/* 726:    */   {
/* 727:756 */     StreamMap streamMap = new StreamMap();
/* 728:    */     
/* 729:758 */     int nextFolderPackStreamIndex = 0;
/* 730:759 */     int numFolders = archive.folders != null ? archive.folders.length : 0;
/* 731:760 */     streamMap.folderFirstPackStreamIndex = new int[numFolders];
/* 732:761 */     for (int i = 0; i < numFolders; i++)
/* 733:    */     {
/* 734:762 */       streamMap.folderFirstPackStreamIndex[i] = nextFolderPackStreamIndex;
/* 735:763 */       nextFolderPackStreamIndex += archive.folders[i].packedStreams.length;
/* 736:    */     }
/* 737:766 */     long nextPackStreamOffset = 0L;
/* 738:767 */     int numPackSizes = archive.packSizes != null ? archive.packSizes.length : 0;
/* 739:768 */     streamMap.packStreamOffsets = new long[numPackSizes];
/* 740:769 */     for (int i = 0; i < numPackSizes; i++)
/* 741:    */     {
/* 742:770 */       streamMap.packStreamOffsets[i] = nextPackStreamOffset;
/* 743:771 */       nextPackStreamOffset += archive.packSizes[i];
/* 744:    */     }
/* 745:774 */     streamMap.folderFirstFileIndex = new int[numFolders];
/* 746:775 */     streamMap.fileFolderIndex = new int[archive.files.length];
/* 747:776 */     int nextFolderIndex = 0;
/* 748:777 */     int nextFolderUnpackStreamIndex = 0;
/* 749:778 */     for (int i = 0; i < archive.files.length; i++) {
/* 750:779 */       if ((!archive.files[i].hasStream()) && (nextFolderUnpackStreamIndex == 0))
/* 751:    */       {
/* 752:780 */         streamMap.fileFolderIndex[i] = -1;
/* 753:    */       }
/* 754:    */       else
/* 755:    */       {
/* 756:783 */         if (nextFolderUnpackStreamIndex == 0)
/* 757:    */         {
/* 758:784 */           for (; nextFolderIndex < archive.folders.length; nextFolderIndex++)
/* 759:    */           {
/* 760:785 */             streamMap.folderFirstFileIndex[nextFolderIndex] = i;
/* 761:786 */             if (archive.folders[nextFolderIndex].numUnpackSubStreams > 0) {
/* 762:    */               break;
/* 763:    */             }
/* 764:    */           }
/* 765:790 */           if (nextFolderIndex >= archive.folders.length) {
/* 766:791 */             throw new IOException("Too few folders in archive");
/* 767:    */           }
/* 768:    */         }
/* 769:794 */         streamMap.fileFolderIndex[i] = nextFolderIndex;
/* 770:795 */         if (archive.files[i].hasStream())
/* 771:    */         {
/* 772:798 */           nextFolderUnpackStreamIndex++;
/* 773:799 */           if (nextFolderUnpackStreamIndex >= archive.folders[nextFolderIndex].numUnpackSubStreams)
/* 774:    */           {
/* 775:800 */             nextFolderIndex++;
/* 776:801 */             nextFolderUnpackStreamIndex = 0;
/* 777:    */           }
/* 778:    */         }
/* 779:    */       }
/* 780:    */     }
/* 781:805 */     archive.streamMap = streamMap;
/* 782:    */   }
/* 783:    */   
/* 784:    */   private void buildDecodingStream()
/* 785:    */     throws IOException
/* 786:    */   {
/* 787:809 */     int folderIndex = this.archive.streamMap.fileFolderIndex[this.currentEntryIndex];
/* 788:810 */     if (folderIndex < 0)
/* 789:    */     {
/* 790:811 */       this.currentEntryInputStream = new BoundedInputStream(new ByteArrayInputStream(new byte[0]), 0L);
/* 791:    */       
/* 792:813 */       return;
/* 793:    */     }
/* 794:815 */     SevenZArchiveEntry file = this.archive.files[this.currentEntryIndex];
/* 795:816 */     if (this.currentFolderIndex == folderIndex)
/* 796:    */     {
/* 797:818 */       drainPreviousEntry();
/* 798:819 */       file.setContentMethods(this.archive.files[(this.currentEntryIndex - 1)].getContentMethods());
/* 799:    */     }
/* 800:    */     else
/* 801:    */     {
/* 802:821 */       this.currentFolderIndex = folderIndex;
/* 803:822 */       if (this.currentFolderInputStream != null)
/* 804:    */       {
/* 805:823 */         this.currentFolderInputStream.close();
/* 806:824 */         this.currentFolderInputStream = null;
/* 807:    */       }
/* 808:827 */       Folder folder = this.archive.folders[folderIndex];
/* 809:828 */       int firstPackStreamIndex = this.archive.streamMap.folderFirstPackStreamIndex[folderIndex];
/* 810:829 */       long folderOffset = 32L + this.archive.packPos + this.archive.streamMap.packStreamOffsets[firstPackStreamIndex];
/* 811:    */       
/* 812:831 */       this.currentFolderInputStream = buildDecoderStack(folder, folderOffset, firstPackStreamIndex, file);
/* 813:    */     }
/* 814:833 */     InputStream fileStream = new BoundedInputStream(this.currentFolderInputStream, file.getSize());
/* 815:835 */     if (file.getHasCrc()) {
/* 816:836 */       this.currentEntryInputStream = new CRC32VerifyingInputStream(fileStream, file.getSize(), file.getCrcValue());
/* 817:    */     } else {
/* 818:839 */       this.currentEntryInputStream = fileStream;
/* 819:    */     }
/* 820:    */   }
/* 821:    */   
/* 822:    */   private void drainPreviousEntry()
/* 823:    */     throws IOException
/* 824:    */   {
/* 825:845 */     if (this.currentEntryInputStream != null)
/* 826:    */     {
/* 827:847 */       IOUtils.skip(this.currentEntryInputStream, 9223372036854775807L);
/* 828:848 */       this.currentEntryInputStream.close();
/* 829:849 */       this.currentEntryInputStream = null;
/* 830:    */     }
/* 831:    */   }
/* 832:    */   
/* 833:    */   private InputStream buildDecoderStack(Folder folder, long folderOffset, int firstPackStreamIndex, SevenZArchiveEntry entry)
/* 834:    */     throws IOException
/* 835:    */   {
/* 836:855 */     this.file.seek(folderOffset);
/* 837:856 */     InputStream inputStreamStack = new BoundedRandomAccessFileInputStream(this.file, this.archive.packSizes[firstPackStreamIndex]);
/* 838:    */     
/* 839:858 */     LinkedList<SevenZMethodConfiguration> methods = new LinkedList();
/* 840:859 */     for (Coder coder : folder.getOrderedCoders())
/* 841:    */     {
/* 842:860 */       if ((coder.numInStreams != 1L) || (coder.numOutStreams != 1L)) {
/* 843:861 */         throw new IOException("Multi input/output stream coders are not yet supported");
/* 844:    */       }
/* 845:863 */       SevenZMethod method = SevenZMethod.byId(coder.decompressionMethodId);
/* 846:864 */       inputStreamStack = Coders.addDecoder(this.fileName, inputStreamStack, folder.getUnpackSizeForCoder(coder), coder, this.password);
/* 847:    */       
/* 848:866 */       methods.addFirst(new SevenZMethodConfiguration(method, Coders.findByMethod(method).getOptionsFromCoder(coder, inputStreamStack)));
/* 849:    */     }
/* 850:869 */     entry.setContentMethods(methods);
/* 851:870 */     if (folder.hasCrc) {
/* 852:871 */       return new CRC32VerifyingInputStream(inputStreamStack, folder.getUnpackSize(), folder.crc);
/* 853:    */     }
/* 854:874 */     return inputStreamStack;
/* 855:    */   }
/* 856:    */   
/* 857:    */   public int read()
/* 858:    */     throws IOException
/* 859:    */   {
/* 860:886 */     if (this.currentEntryInputStream == null) {
/* 861:887 */       throw new IllegalStateException("No current 7z entry");
/* 862:    */     }
/* 863:889 */     return this.currentEntryInputStream.read();
/* 864:    */   }
/* 865:    */   
/* 866:    */   public int read(byte[] b)
/* 867:    */     throws IOException
/* 868:    */   {
/* 869:901 */     return read(b, 0, b.length);
/* 870:    */   }
/* 871:    */   
/* 872:    */   public int read(byte[] b, int off, int len)
/* 873:    */     throws IOException
/* 874:    */   {
/* 875:915 */     if (this.currentEntryInputStream == null) {
/* 876:916 */       throw new IllegalStateException("No current 7z entry");
/* 877:    */     }
/* 878:918 */     return this.currentEntryInputStream.read(b, off, len);
/* 879:    */   }
/* 880:    */   
/* 881:    */   private static long readUint64(DataInput in)
/* 882:    */     throws IOException
/* 883:    */   {
/* 884:923 */     long firstByte = in.readUnsignedByte();
/* 885:924 */     int mask = 128;
/* 886:925 */     long value = 0L;
/* 887:926 */     for (int i = 0; i < 8; i++)
/* 888:    */     {
/* 889:927 */       if ((firstByte & mask) == 0L) {
/* 890:928 */         return value | (firstByte & mask - 1) << 8 * i;
/* 891:    */       }
/* 892:930 */       long nextByte = in.readUnsignedByte();
/* 893:931 */       value |= nextByte << 8 * i;
/* 894:932 */       mask >>>= 1;
/* 895:    */     }
/* 896:934 */     return value;
/* 897:    */   }
/* 898:    */   
/* 899:    */   public static boolean matches(byte[] signature, int length)
/* 900:    */   {
/* 901:948 */     if (length < sevenZSignature.length) {
/* 902:949 */       return false;
/* 903:    */     }
/* 904:952 */     for (int i = 0; i < sevenZSignature.length; i++) {
/* 905:953 */       if (signature[i] != sevenZSignature[i]) {
/* 906:954 */         return false;
/* 907:    */       }
/* 908:    */     }
/* 909:957 */     return true;
/* 910:    */   }
/* 911:    */   
/* 912:    */   private static long skipBytesFully(DataInput input, long bytesToSkip)
/* 913:    */     throws IOException
/* 914:    */   {
/* 915:961 */     if (bytesToSkip < 1L) {
/* 916:962 */       return 0L;
/* 917:    */     }
/* 918:964 */     long skipped = 0L;
/* 919:965 */     while (bytesToSkip > 2147483647L)
/* 920:    */     {
/* 921:966 */       long skippedNow = skipBytesFully(input, 2147483647L);
/* 922:967 */       if (skippedNow == 0L) {
/* 923:968 */         return skipped;
/* 924:    */       }
/* 925:970 */       skipped += skippedNow;
/* 926:971 */       bytesToSkip -= skippedNow;
/* 927:    */     }
/* 928:973 */     while (bytesToSkip > 0L)
/* 929:    */     {
/* 930:974 */       int skippedNow = input.skipBytes((int)bytesToSkip);
/* 931:975 */       if (skippedNow == 0) {
/* 932:976 */         return skipped;
/* 933:    */       }
/* 934:978 */       skipped += skippedNow;
/* 935:979 */       bytesToSkip -= skippedNow;
/* 936:    */     }
/* 937:981 */     return skipped;
/* 938:    */   }
/* 939:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.sevenz.SevenZFile
 * JD-Core Version:    0.7.0.1
 */