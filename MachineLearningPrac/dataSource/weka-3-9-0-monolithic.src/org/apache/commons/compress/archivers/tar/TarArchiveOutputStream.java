/*   1:    */ package org.apache.commons.compress.archivers.tar;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.OutputStream;
/*   6:    */ import java.io.StringWriter;
/*   7:    */ import java.nio.ByteBuffer;
/*   8:    */ import java.util.Arrays;
/*   9:    */ import java.util.Date;
/*  10:    */ import java.util.HashMap;
/*  11:    */ import java.util.Map;
/*  12:    */ import java.util.Map.Entry;
/*  13:    */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*  14:    */ import org.apache.commons.compress.archivers.ArchiveOutputStream;
/*  15:    */ import org.apache.commons.compress.archivers.zip.ZipEncoding;
/*  16:    */ import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
/*  17:    */ import org.apache.commons.compress.utils.CountingOutputStream;
/*  18:    */ 
/*  19:    */ public class TarArchiveOutputStream
/*  20:    */   extends ArchiveOutputStream
/*  21:    */ {
/*  22:    */   public static final int LONGFILE_ERROR = 0;
/*  23:    */   public static final int LONGFILE_TRUNCATE = 1;
/*  24:    */   public static final int LONGFILE_GNU = 2;
/*  25:    */   public static final int LONGFILE_POSIX = 3;
/*  26:    */   public static final int BIGNUMBER_ERROR = 0;
/*  27:    */   public static final int BIGNUMBER_STAR = 1;
/*  28:    */   public static final int BIGNUMBER_POSIX = 2;
/*  29:    */   private long currSize;
/*  30:    */   private String currName;
/*  31:    */   private long currBytes;
/*  32:    */   private final byte[] recordBuf;
/*  33:    */   private int assemLen;
/*  34:    */   private final byte[] assemBuf;
/*  35: 71 */   private int longFileMode = 0;
/*  36: 72 */   private int bigNumberMode = 0;
/*  37:    */   private int recordsWritten;
/*  38:    */   private final int recordsPerBlock;
/*  39:    */   private final int recordSize;
/*  40: 77 */   private boolean closed = false;
/*  41: 80 */   private boolean haveUnclosedEntry = false;
/*  42: 83 */   private boolean finished = false;
/*  43:    */   private final OutputStream out;
/*  44:    */   private final ZipEncoding zipEncoding;
/*  45:    */   final String encoding;
/*  46: 92 */   private boolean addPaxHeadersForNonAsciiNames = false;
/*  47: 93 */   private static final ZipEncoding ASCII = ZipEncodingHelper.getZipEncoding("ASCII");
/*  48:    */   
/*  49:    */   public TarArchiveOutputStream(OutputStream os)
/*  50:    */   {
/*  51:101 */     this(os, 10240, 512);
/*  52:    */   }
/*  53:    */   
/*  54:    */   public TarArchiveOutputStream(OutputStream os, String encoding)
/*  55:    */   {
/*  56:111 */     this(os, 10240, 512, encoding);
/*  57:    */   }
/*  58:    */   
/*  59:    */   public TarArchiveOutputStream(OutputStream os, int blockSize)
/*  60:    */   {
/*  61:120 */     this(os, blockSize, 512);
/*  62:    */   }
/*  63:    */   
/*  64:    */   public TarArchiveOutputStream(OutputStream os, int blockSize, String encoding)
/*  65:    */   {
/*  66:132 */     this(os, blockSize, 512, encoding);
/*  67:    */   }
/*  68:    */   
/*  69:    */   public TarArchiveOutputStream(OutputStream os, int blockSize, int recordSize)
/*  70:    */   {
/*  71:142 */     this(os, blockSize, recordSize, null);
/*  72:    */   }
/*  73:    */   
/*  74:    */   public TarArchiveOutputStream(OutputStream os, int blockSize, int recordSize, String encoding)
/*  75:    */   {
/*  76:155 */     this.out = new CountingOutputStream(os);
/*  77:156 */     this.encoding = encoding;
/*  78:157 */     this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
/*  79:    */     
/*  80:159 */     this.assemLen = 0;
/*  81:160 */     this.assemBuf = new byte[recordSize];
/*  82:161 */     this.recordBuf = new byte[recordSize];
/*  83:162 */     this.recordSize = recordSize;
/*  84:163 */     this.recordsPerBlock = (blockSize / recordSize);
/*  85:    */   }
/*  86:    */   
/*  87:    */   public void setLongFileMode(int longFileMode)
/*  88:    */   {
/*  89:174 */     this.longFileMode = longFileMode;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public void setBigNumberMode(int bigNumberMode)
/*  93:    */   {
/*  94:186 */     this.bigNumberMode = bigNumberMode;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public void setAddPaxHeadersForNonAsciiNames(boolean b)
/*  98:    */   {
/*  99:195 */     this.addPaxHeadersForNonAsciiNames = b;
/* 100:    */   }
/* 101:    */   
/* 102:    */   @Deprecated
/* 103:    */   public int getCount()
/* 104:    */   {
/* 105:201 */     return (int)getBytesWritten();
/* 106:    */   }
/* 107:    */   
/* 108:    */   public long getBytesWritten()
/* 109:    */   {
/* 110:206 */     return ((CountingOutputStream)this.out).getBytesWritten();
/* 111:    */   }
/* 112:    */   
/* 113:    */   public void finish()
/* 114:    */     throws IOException
/* 115:    */   {
/* 116:220 */     if (this.finished) {
/* 117:221 */       throw new IOException("This archive has already been finished");
/* 118:    */     }
/* 119:224 */     if (this.haveUnclosedEntry) {
/* 120:225 */       throw new IOException("This archives contains unclosed entries.");
/* 121:    */     }
/* 122:227 */     writeEOFRecord();
/* 123:228 */     writeEOFRecord();
/* 124:229 */     padAsNeeded();
/* 125:230 */     this.out.flush();
/* 126:231 */     this.finished = true;
/* 127:    */   }
/* 128:    */   
/* 129:    */   public void close()
/* 130:    */     throws IOException
/* 131:    */   {
/* 132:240 */     if (!this.finished) {
/* 133:241 */       finish();
/* 134:    */     }
/* 135:244 */     if (!this.closed)
/* 136:    */     {
/* 137:245 */       this.out.close();
/* 138:246 */       this.closed = true;
/* 139:    */     }
/* 140:    */   }
/* 141:    */   
/* 142:    */   public int getRecordSize()
/* 143:    */   {
/* 144:256 */     return this.recordSize;
/* 145:    */   }
/* 146:    */   
/* 147:    */   public void putArchiveEntry(ArchiveEntry archiveEntry)
/* 148:    */     throws IOException
/* 149:    */   {
/* 150:274 */     if (this.finished) {
/* 151:275 */       throw new IOException("Stream has already been finished");
/* 152:    */     }
/* 153:277 */     TarArchiveEntry entry = (TarArchiveEntry)archiveEntry;
/* 154:278 */     Map<String, String> paxHeaders = new HashMap();
/* 155:279 */     String entryName = entry.getName();
/* 156:280 */     boolean paxHeaderContainsPath = handleLongName(entry, entryName, paxHeaders, "path", (byte)76, "file name");
/* 157:    */     
/* 158:    */ 
/* 159:283 */     String linkName = entry.getLinkName();
/* 160:284 */     boolean paxHeaderContainsLinkPath = (linkName != null) && (linkName.length() > 0) && (handleLongName(entry, linkName, paxHeaders, "linkpath", (byte)75, "link name"));
/* 161:288 */     if (this.bigNumberMode == 2) {
/* 162:289 */       addPaxHeadersForBigNumbers(paxHeaders, entry);
/* 163:290 */     } else if (this.bigNumberMode != 1) {
/* 164:291 */       failForBigNumbers(entry);
/* 165:    */     }
/* 166:294 */     if ((this.addPaxHeadersForNonAsciiNames) && (!paxHeaderContainsPath) && (!ASCII.canEncode(entryName))) {
/* 167:296 */       paxHeaders.put("path", entryName);
/* 168:    */     }
/* 169:299 */     if ((this.addPaxHeadersForNonAsciiNames) && (!paxHeaderContainsLinkPath) && ((entry.isLink()) || (entry.isSymbolicLink())) && (!ASCII.canEncode(linkName))) {
/* 170:302 */       paxHeaders.put("linkpath", linkName);
/* 171:    */     }
/* 172:305 */     if (paxHeaders.size() > 0) {
/* 173:306 */       writePaxHeaders(entry, entryName, paxHeaders);
/* 174:    */     }
/* 175:309 */     entry.writeEntryHeader(this.recordBuf, this.zipEncoding, this.bigNumberMode == 1);
/* 176:    */     
/* 177:311 */     writeRecord(this.recordBuf);
/* 178:    */     
/* 179:313 */     this.currBytes = 0L;
/* 180:315 */     if (entry.isDirectory()) {
/* 181:316 */       this.currSize = 0L;
/* 182:    */     } else {
/* 183:318 */       this.currSize = entry.getSize();
/* 184:    */     }
/* 185:320 */     this.currName = entryName;
/* 186:321 */     this.haveUnclosedEntry = true;
/* 187:    */   }
/* 188:    */   
/* 189:    */   public void closeArchiveEntry()
/* 190:    */     throws IOException
/* 191:    */   {
/* 192:336 */     if (this.finished) {
/* 193:337 */       throw new IOException("Stream has already been finished");
/* 194:    */     }
/* 195:339 */     if (!this.haveUnclosedEntry) {
/* 196:340 */       throw new IOException("No current entry to close");
/* 197:    */     }
/* 198:342 */     if (this.assemLen > 0)
/* 199:    */     {
/* 200:343 */       for (int i = this.assemLen; i < this.assemBuf.length; i++) {
/* 201:344 */         this.assemBuf[i] = 0;
/* 202:    */       }
/* 203:347 */       writeRecord(this.assemBuf);
/* 204:    */       
/* 205:349 */       this.currBytes += this.assemLen;
/* 206:350 */       this.assemLen = 0;
/* 207:    */     }
/* 208:353 */     if (this.currBytes < this.currSize) {
/* 209:354 */       throw new IOException("entry '" + this.currName + "' closed at '" + this.currBytes + "' before the '" + this.currSize + "' bytes specified in the header were written");
/* 210:    */     }
/* 211:359 */     this.haveUnclosedEntry = false;
/* 212:    */   }
/* 213:    */   
/* 214:    */   public void write(byte[] wBuf, int wOffset, int numToWrite)
/* 215:    */     throws IOException
/* 216:    */   {
/* 217:378 */     if (!this.haveUnclosedEntry) {
/* 218:379 */       throw new IllegalStateException("No current tar entry");
/* 219:    */     }
/* 220:381 */     if (this.currBytes + numToWrite > this.currSize) {
/* 221:382 */       throw new IOException("request to write '" + numToWrite + "' bytes exceeds size in header of '" + this.currSize + "' bytes for entry '" + this.currName + "'");
/* 222:    */     }
/* 223:396 */     if (this.assemLen > 0) {
/* 224:397 */       if (this.assemLen + numToWrite >= this.recordBuf.length)
/* 225:    */       {
/* 226:398 */         int aLen = this.recordBuf.length - this.assemLen;
/* 227:    */         
/* 228:400 */         System.arraycopy(this.assemBuf, 0, this.recordBuf, 0, this.assemLen);
/* 229:    */         
/* 230:402 */         System.arraycopy(wBuf, wOffset, this.recordBuf, this.assemLen, aLen);
/* 231:    */         
/* 232:404 */         writeRecord(this.recordBuf);
/* 233:    */         
/* 234:406 */         this.currBytes += this.recordBuf.length;
/* 235:407 */         wOffset += aLen;
/* 236:408 */         numToWrite -= aLen;
/* 237:409 */         this.assemLen = 0;
/* 238:    */       }
/* 239:    */       else
/* 240:    */       {
/* 241:411 */         System.arraycopy(wBuf, wOffset, this.assemBuf, this.assemLen, numToWrite);
/* 242:    */         
/* 243:    */ 
/* 244:414 */         wOffset += numToWrite;
/* 245:415 */         this.assemLen += numToWrite;
/* 246:416 */         numToWrite = 0;
/* 247:    */       }
/* 248:    */     }
/* 249:425 */     while (numToWrite > 0)
/* 250:    */     {
/* 251:426 */       if (numToWrite < this.recordBuf.length)
/* 252:    */       {
/* 253:427 */         System.arraycopy(wBuf, wOffset, this.assemBuf, this.assemLen, numToWrite);
/* 254:    */         
/* 255:    */ 
/* 256:430 */         this.assemLen += numToWrite;
/* 257:    */         
/* 258:432 */         break;
/* 259:    */       }
/* 260:435 */       writeRecord(wBuf, wOffset);
/* 261:    */       
/* 262:437 */       int num = this.recordBuf.length;
/* 263:    */       
/* 264:439 */       this.currBytes += num;
/* 265:440 */       numToWrite -= num;
/* 266:441 */       wOffset += num;
/* 267:    */     }
/* 268:    */   }
/* 269:    */   
/* 270:    */   void writePaxHeaders(TarArchiveEntry entry, String entryName, Map<String, String> headers)
/* 271:    */     throws IOException
/* 272:    */   {
/* 273:452 */     String name = "./PaxHeaders.X/" + stripTo7Bits(entryName);
/* 274:453 */     if (name.length() >= 100) {
/* 275:454 */       name = name.substring(0, 99);
/* 276:    */     }
/* 277:456 */     TarArchiveEntry pex = new TarArchiveEntry(name, (byte)120);
/* 278:    */     
/* 279:458 */     transferModTime(entry, pex);
/* 280:    */     
/* 281:460 */     StringWriter w = new StringWriter();
/* 282:461 */     for (Map.Entry<String, String> h : headers.entrySet())
/* 283:    */     {
/* 284:462 */       String key = (String)h.getKey();
/* 285:463 */       String value = (String)h.getValue();
/* 286:464 */       int len = key.length() + value.length() + 3 + 2;
/* 287:    */       
/* 288:    */ 
/* 289:467 */       String line = len + " " + key + "=" + value + "\n";
/* 290:468 */       int actualLength = line.getBytes("UTF-8").length;
/* 291:469 */       while (len != actualLength)
/* 292:    */       {
/* 293:475 */         len = actualLength;
/* 294:476 */         line = len + " " + key + "=" + value + "\n";
/* 295:477 */         actualLength = line.getBytes("UTF-8").length;
/* 296:    */       }
/* 297:479 */       w.write(line);
/* 298:    */     }
/* 299:481 */     byte[] data = w.toString().getBytes("UTF-8");
/* 300:482 */     pex.setSize(data.length);
/* 301:483 */     putArchiveEntry(pex);
/* 302:484 */     write(data);
/* 303:485 */     closeArchiveEntry();
/* 304:    */   }
/* 305:    */   
/* 306:    */   private String stripTo7Bits(String name)
/* 307:    */   {
/* 308:489 */     int length = name.length();
/* 309:490 */     StringBuilder result = new StringBuilder(length);
/* 310:491 */     for (int i = 0; i < length; i++)
/* 311:    */     {
/* 312:492 */       char stripped = (char)(name.charAt(i) & 0x7F);
/* 313:493 */       if (shouldBeReplaced(stripped)) {
/* 314:494 */         result.append("_");
/* 315:    */       } else {
/* 316:496 */         result.append(stripped);
/* 317:    */       }
/* 318:    */     }
/* 319:499 */     return result.toString();
/* 320:    */   }
/* 321:    */   
/* 322:    */   private boolean shouldBeReplaced(char c)
/* 323:    */   {
/* 324:507 */     return (c == 0) || (c == '/') || (c == '\\');
/* 325:    */   }
/* 326:    */   
/* 327:    */   private void writeEOFRecord()
/* 328:    */     throws IOException
/* 329:    */   {
/* 330:517 */     Arrays.fill(this.recordBuf, (byte)0);
/* 331:518 */     writeRecord(this.recordBuf);
/* 332:    */   }
/* 333:    */   
/* 334:    */   public void flush()
/* 335:    */     throws IOException
/* 336:    */   {
/* 337:523 */     this.out.flush();
/* 338:    */   }
/* 339:    */   
/* 340:    */   public ArchiveEntry createArchiveEntry(File inputFile, String entryName)
/* 341:    */     throws IOException
/* 342:    */   {
/* 343:529 */     if (this.finished) {
/* 344:530 */       throw new IOException("Stream has already been finished");
/* 345:    */     }
/* 346:532 */     return new TarArchiveEntry(inputFile, entryName);
/* 347:    */   }
/* 348:    */   
/* 349:    */   private void writeRecord(byte[] record)
/* 350:    */     throws IOException
/* 351:    */   {
/* 352:542 */     if (record.length != this.recordSize) {
/* 353:543 */       throw new IOException("record to write has length '" + record.length + "' which is not the record size of '" + this.recordSize + "'");
/* 354:    */     }
/* 355:549 */     this.out.write(record);
/* 356:550 */     this.recordsWritten += 1;
/* 357:    */   }
/* 358:    */   
/* 359:    */   private void writeRecord(byte[] buf, int offset)
/* 360:    */     throws IOException
/* 361:    */   {
/* 362:564 */     if (offset + this.recordSize > buf.length) {
/* 363:565 */       throw new IOException("record has length '" + buf.length + "' with offset '" + offset + "' which is less than the record size of '" + this.recordSize + "'");
/* 364:    */     }
/* 365:571 */     this.out.write(buf, offset, this.recordSize);
/* 366:572 */     this.recordsWritten += 1;
/* 367:    */   }
/* 368:    */   
/* 369:    */   private void padAsNeeded()
/* 370:    */     throws IOException
/* 371:    */   {
/* 372:576 */     int start = this.recordsWritten % this.recordsPerBlock;
/* 373:577 */     if (start != 0) {
/* 374:578 */       for (int i = start; i < this.recordsPerBlock; i++) {
/* 375:579 */         writeEOFRecord();
/* 376:    */       }
/* 377:    */     }
/* 378:    */   }
/* 379:    */   
/* 380:    */   private void addPaxHeadersForBigNumbers(Map<String, String> paxHeaders, TarArchiveEntry entry)
/* 381:    */   {
/* 382:586 */     addPaxHeaderForBigNumber(paxHeaders, "size", entry.getSize(), 8589934591L);
/* 383:    */     
/* 384:588 */     addPaxHeaderForBigNumber(paxHeaders, "gid", entry.getLongGroupId(), 2097151L);
/* 385:    */     
/* 386:590 */     addPaxHeaderForBigNumber(paxHeaders, "mtime", entry.getModTime().getTime() / 1000L, 8589934591L);
/* 387:    */     
/* 388:    */ 
/* 389:593 */     addPaxHeaderForBigNumber(paxHeaders, "uid", entry.getLongUserId(), 2097151L);
/* 390:    */     
/* 391:    */ 
/* 392:596 */     addPaxHeaderForBigNumber(paxHeaders, "SCHILY.devmajor", entry.getDevMajor(), 2097151L);
/* 393:    */     
/* 394:598 */     addPaxHeaderForBigNumber(paxHeaders, "SCHILY.devminor", entry.getDevMinor(), 2097151L);
/* 395:    */     
/* 396:    */ 
/* 397:601 */     failForBigNumber("mode", entry.getMode(), 2097151L);
/* 398:    */   }
/* 399:    */   
/* 400:    */   private void addPaxHeaderForBigNumber(Map<String, String> paxHeaders, String header, long value, long maxValue)
/* 401:    */   {
/* 402:607 */     if ((value < 0L) || (value > maxValue)) {
/* 403:608 */       paxHeaders.put(header, String.valueOf(value));
/* 404:    */     }
/* 405:    */   }
/* 406:    */   
/* 407:    */   private void failForBigNumbers(TarArchiveEntry entry)
/* 408:    */   {
/* 409:613 */     failForBigNumber("entry size", entry.getSize(), 8589934591L);
/* 410:614 */     failForBigNumberWithPosixMessage("group id", entry.getLongGroupId(), 2097151L);
/* 411:615 */     failForBigNumber("last modification time", entry.getModTime().getTime() / 1000L, 8589934591L);
/* 412:    */     
/* 413:    */ 
/* 414:618 */     failForBigNumber("user id", entry.getLongUserId(), 2097151L);
/* 415:619 */     failForBigNumber("mode", entry.getMode(), 2097151L);
/* 416:620 */     failForBigNumber("major device number", entry.getDevMajor(), 2097151L);
/* 417:    */     
/* 418:622 */     failForBigNumber("minor device number", entry.getDevMinor(), 2097151L);
/* 419:    */   }
/* 420:    */   
/* 421:    */   private void failForBigNumber(String field, long value, long maxValue)
/* 422:    */   {
/* 423:627 */     failForBigNumber(field, value, maxValue, "");
/* 424:    */   }
/* 425:    */   
/* 426:    */   private void failForBigNumberWithPosixMessage(String field, long value, long maxValue)
/* 427:    */   {
/* 428:631 */     failForBigNumber(field, value, maxValue, " Use STAR or POSIX extensions to overcome this limit");
/* 429:    */   }
/* 430:    */   
/* 431:    */   private void failForBigNumber(String field, long value, long maxValue, String additionalMsg)
/* 432:    */   {
/* 433:635 */     if ((value < 0L) || (value > maxValue)) {
/* 434:636 */       throw new RuntimeException(field + " '" + value + "' is too big ( > " + maxValue + " )." + additionalMsg);
/* 435:    */     }
/* 436:    */   }
/* 437:    */   
/* 438:    */   private boolean handleLongName(TarArchiveEntry entry, String name, Map<String, String> paxHeaders, String paxHeaderName, byte linkType, String fieldName)
/* 439:    */     throws IOException
/* 440:    */   {
/* 441:668 */     ByteBuffer encodedName = this.zipEncoding.encode(name);
/* 442:669 */     int len = encodedName.limit() - encodedName.position();
/* 443:670 */     if (len >= 100)
/* 444:    */     {
/* 445:672 */       if (this.longFileMode == 3)
/* 446:    */       {
/* 447:673 */         paxHeaders.put(paxHeaderName, name);
/* 448:674 */         return true;
/* 449:    */       }
/* 450:675 */       if (this.longFileMode == 2)
/* 451:    */       {
/* 452:678 */         TarArchiveEntry longLinkEntry = new TarArchiveEntry("././@LongLink", linkType);
/* 453:    */         
/* 454:680 */         longLinkEntry.setSize(len + 1);
/* 455:681 */         transferModTime(entry, longLinkEntry);
/* 456:682 */         putArchiveEntry(longLinkEntry);
/* 457:683 */         write(encodedName.array(), encodedName.arrayOffset(), len);
/* 458:684 */         write(0);
/* 459:685 */         closeArchiveEntry();
/* 460:    */       }
/* 461:686 */       else if (this.longFileMode != 1)
/* 462:    */       {
/* 463:687 */         throw new RuntimeException(fieldName + " '" + name + "' is too long ( > " + 100 + " bytes)");
/* 464:    */       }
/* 465:    */     }
/* 466:692 */     return false;
/* 467:    */   }
/* 468:    */   
/* 469:    */   private void transferModTime(TarArchiveEntry from, TarArchiveEntry to)
/* 470:    */   {
/* 471:696 */     Date fromModTime = from.getModTime();
/* 472:697 */     long fromModTimeSeconds = fromModTime.getTime() / 1000L;
/* 473:698 */     if ((fromModTimeSeconds < 0L) || (fromModTimeSeconds > 8589934591L)) {
/* 474:699 */       fromModTime = new Date(0L);
/* 475:    */     }
/* 476:701 */     to.setModTime(fromModTime);
/* 477:    */   }
/* 478:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
 * JD-Core Version:    0.7.0.1
 */