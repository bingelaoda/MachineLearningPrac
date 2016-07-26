/*   1:    */ package org.apache.commons.compress.archivers.tar;
/*   2:    */ 
/*   3:    */ import java.io.ByteArrayOutputStream;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.InputStream;
/*   6:    */ import java.util.HashMap;
/*   7:    */ import java.util.Map;
/*   8:    */ import java.util.Map.Entry;
/*   9:    */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*  10:    */ import org.apache.commons.compress.archivers.ArchiveInputStream;
/*  11:    */ import org.apache.commons.compress.archivers.zip.ZipEncoding;
/*  12:    */ import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
/*  13:    */ import org.apache.commons.compress.utils.ArchiveUtils;
/*  14:    */ import org.apache.commons.compress.utils.IOUtils;
/*  15:    */ 
/*  16:    */ public class TarArchiveInputStream
/*  17:    */   extends ArchiveInputStream
/*  18:    */ {
/*  19:    */   private static final int SMALL_BUFFER_SIZE = 256;
/*  20: 52 */   private final byte[] SMALL_BUF = new byte[256];
/*  21:    */   private final int recordSize;
/*  22:    */   private final int blockSize;
/*  23:    */   private boolean hasHitEOF;
/*  24:    */   private long entrySize;
/*  25:    */   private long entryOffset;
/*  26:    */   private final InputStream is;
/*  27:    */   private TarArchiveEntry currEntry;
/*  28:    */   private final ZipEncoding zipEncoding;
/*  29:    */   final String encoding;
/*  30:    */   
/*  31:    */   public TarArchiveInputStream(InputStream is)
/*  32:    */   {
/*  33: 86 */     this(is, 10240, 512);
/*  34:    */   }
/*  35:    */   
/*  36:    */   public TarArchiveInputStream(InputStream is, String encoding)
/*  37:    */   {
/*  38: 96 */     this(is, 10240, 512, encoding);
/*  39:    */   }
/*  40:    */   
/*  41:    */   public TarArchiveInputStream(InputStream is, int blockSize)
/*  42:    */   {
/*  43:106 */     this(is, blockSize, 512);
/*  44:    */   }
/*  45:    */   
/*  46:    */   public TarArchiveInputStream(InputStream is, int blockSize, String encoding)
/*  47:    */   {
/*  48:118 */     this(is, blockSize, 512, encoding);
/*  49:    */   }
/*  50:    */   
/*  51:    */   public TarArchiveInputStream(InputStream is, int blockSize, int recordSize)
/*  52:    */   {
/*  53:128 */     this(is, blockSize, recordSize, null);
/*  54:    */   }
/*  55:    */   
/*  56:    */   public TarArchiveInputStream(InputStream is, int blockSize, int recordSize, String encoding)
/*  57:    */   {
/*  58:141 */     this.is = is;
/*  59:142 */     this.hasHitEOF = false;
/*  60:143 */     this.encoding = encoding;
/*  61:144 */     this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
/*  62:145 */     this.recordSize = recordSize;
/*  63:146 */     this.blockSize = blockSize;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public void close()
/*  67:    */     throws IOException
/*  68:    */   {
/*  69:155 */     this.is.close();
/*  70:    */   }
/*  71:    */   
/*  72:    */   public int getRecordSize()
/*  73:    */   {
/*  74:164 */     return this.recordSize;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public int available()
/*  78:    */     throws IOException
/*  79:    */   {
/*  80:181 */     if (this.entrySize - this.entryOffset > 2147483647L) {
/*  81:182 */       return 2147483647;
/*  82:    */     }
/*  83:184 */     return (int)(this.entrySize - this.entryOffset);
/*  84:    */   }
/*  85:    */   
/*  86:    */   public long skip(long n)
/*  87:    */     throws IOException
/*  88:    */   {
/*  89:206 */     if (n <= 0L) {
/*  90:207 */       return 0L;
/*  91:    */     }
/*  92:210 */     long available = this.entrySize - this.entryOffset;
/*  93:211 */     long skipped = this.is.skip(Math.min(n, available));
/*  94:212 */     count(skipped);
/*  95:213 */     this.entryOffset += skipped;
/*  96:214 */     return skipped;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public boolean markSupported()
/* 100:    */   {
/* 101:224 */     return false;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public void mark(int markLimit) {}
/* 105:    */   
/* 106:    */   public synchronized void reset() {}
/* 107:    */   
/* 108:    */   public TarArchiveEntry getNextTarEntry()
/* 109:    */     throws IOException
/* 110:    */   {
/* 111:257 */     if (this.hasHitEOF) {
/* 112:258 */       return null;
/* 113:    */     }
/* 114:261 */     if (this.currEntry != null)
/* 115:    */     {
/* 116:263 */       IOUtils.skip(this, 9223372036854775807L);
/* 117:    */       
/* 118:    */ 
/* 119:266 */       skipRecordPadding();
/* 120:    */     }
/* 121:269 */     byte[] headerBuf = getRecord();
/* 122:271 */     if (headerBuf == null)
/* 123:    */     {
/* 124:273 */       this.currEntry = null;
/* 125:274 */       return null;
/* 126:    */     }
/* 127:    */     try
/* 128:    */     {
/* 129:278 */       this.currEntry = new TarArchiveEntry(headerBuf, this.zipEncoding);
/* 130:    */     }
/* 131:    */     catch (IllegalArgumentException e)
/* 132:    */     {
/* 133:280 */       IOException ioe = new IOException("Error detected parsing the header");
/* 134:281 */       ioe.initCause(e);
/* 135:282 */       throw ioe;
/* 136:    */     }
/* 137:285 */     this.entryOffset = 0L;
/* 138:286 */     this.entrySize = this.currEntry.getSize();
/* 139:288 */     if (this.currEntry.isGNULongLinkEntry())
/* 140:    */     {
/* 141:289 */       byte[] longLinkData = getLongNameData();
/* 142:290 */       if (longLinkData == null) {
/* 143:294 */         return null;
/* 144:    */       }
/* 145:296 */       this.currEntry.setLinkName(this.zipEncoding.decode(longLinkData));
/* 146:    */     }
/* 147:299 */     if (this.currEntry.isGNULongNameEntry())
/* 148:    */     {
/* 149:300 */       byte[] longNameData = getLongNameData();
/* 150:301 */       if (longNameData == null) {
/* 151:305 */         return null;
/* 152:    */       }
/* 153:307 */       this.currEntry.setName(this.zipEncoding.decode(longNameData));
/* 154:    */     }
/* 155:310 */     if (this.currEntry.isPaxHeader()) {
/* 156:311 */       paxHeaders();
/* 157:    */     }
/* 158:314 */     if (this.currEntry.isGNUSparse()) {
/* 159:315 */       readGNUSparse();
/* 160:    */     }
/* 161:322 */     this.entrySize = this.currEntry.getSize();
/* 162:    */     
/* 163:324 */     return this.currEntry;
/* 164:    */   }
/* 165:    */   
/* 166:    */   private void skipRecordPadding()
/* 167:    */     throws IOException
/* 168:    */   {
/* 169:332 */     if ((this.entrySize > 0L) && (this.entrySize % this.recordSize != 0L))
/* 170:    */     {
/* 171:333 */       long numRecords = this.entrySize / this.recordSize + 1L;
/* 172:334 */       long padding = numRecords * this.recordSize - this.entrySize;
/* 173:335 */       long skipped = IOUtils.skip(this.is, padding);
/* 174:336 */       count(skipped);
/* 175:    */     }
/* 176:    */   }
/* 177:    */   
/* 178:    */   protected byte[] getLongNameData()
/* 179:    */     throws IOException
/* 180:    */   {
/* 181:348 */     ByteArrayOutputStream longName = new ByteArrayOutputStream();
/* 182:349 */     int length = 0;
/* 183:350 */     while ((length = read(this.SMALL_BUF)) >= 0) {
/* 184:351 */       longName.write(this.SMALL_BUF, 0, length);
/* 185:    */     }
/* 186:353 */     getNextEntry();
/* 187:354 */     if (this.currEntry == null) {
/* 188:357 */       return null;
/* 189:    */     }
/* 190:359 */     byte[] longNameData = longName.toByteArray();
/* 191:    */     
/* 192:361 */     length = longNameData.length;
/* 193:362 */     while ((length > 0) && (longNameData[(length - 1)] == 0)) {
/* 194:363 */       length--;
/* 195:    */     }
/* 196:365 */     if (length != longNameData.length)
/* 197:    */     {
/* 198:366 */       byte[] l = new byte[length];
/* 199:367 */       System.arraycopy(longNameData, 0, l, 0, length);
/* 200:368 */       longNameData = l;
/* 201:    */     }
/* 202:370 */     return longNameData;
/* 203:    */   }
/* 204:    */   
/* 205:    */   private byte[] getRecord()
/* 206:    */     throws IOException
/* 207:    */   {
/* 208:388 */     byte[] headerBuf = readRecord();
/* 209:389 */     this.hasHitEOF = isEOFRecord(headerBuf);
/* 210:390 */     if ((this.hasHitEOF) && (headerBuf != null))
/* 211:    */     {
/* 212:391 */       tryToConsumeSecondEOFRecord();
/* 213:392 */       consumeRemainderOfLastBlock();
/* 214:393 */       headerBuf = null;
/* 215:    */     }
/* 216:395 */     return headerBuf;
/* 217:    */   }
/* 218:    */   
/* 219:    */   protected boolean isEOFRecord(byte[] record)
/* 220:    */   {
/* 221:406 */     return (record == null) || (ArchiveUtils.isArrayZero(record, this.recordSize));
/* 222:    */   }
/* 223:    */   
/* 224:    */   protected byte[] readRecord()
/* 225:    */     throws IOException
/* 226:    */   {
/* 227:417 */     byte[] record = new byte[this.recordSize];
/* 228:    */     
/* 229:419 */     int readNow = IOUtils.readFully(this.is, record);
/* 230:420 */     count(readNow);
/* 231:421 */     if (readNow != this.recordSize) {
/* 232:422 */       return null;
/* 233:    */     }
/* 234:425 */     return record;
/* 235:    */   }
/* 236:    */   
/* 237:    */   private void paxHeaders()
/* 238:    */     throws IOException
/* 239:    */   {
/* 240:429 */     Map<String, String> headers = parsePaxHeaders(this);
/* 241:430 */     getNextEntry();
/* 242:431 */     applyPaxHeadersToCurrentEntry(headers);
/* 243:    */   }
/* 244:    */   
/* 245:    */   Map<String, String> parsePaxHeaders(InputStream i)
/* 246:    */     throws IOException
/* 247:    */   {
/* 248:435 */     Map<String, String> headers = new HashMap();
/* 249:    */     for (;;)
/* 250:    */     {
/* 251:439 */       int len = 0;
/* 252:440 */       int read = 0;
/* 253:    */       int ch;
/* 254:441 */       while ((ch = i.read()) != -1)
/* 255:    */       {
/* 256:442 */         read++;
/* 257:443 */         if (ch == 32)
/* 258:    */         {
/* 259:445 */           ByteArrayOutputStream coll = new ByteArrayOutputStream();
/* 260:446 */           while ((ch = i.read()) != -1)
/* 261:    */           {
/* 262:447 */             read++;
/* 263:448 */             if (ch == 61)
/* 264:    */             {
/* 265:449 */               String keyword = coll.toString("UTF-8");
/* 266:    */               
/* 267:451 */               int restLen = len - read;
/* 268:452 */               byte[] rest = new byte[restLen];
/* 269:453 */               int got = IOUtils.readFully(i, rest);
/* 270:454 */               if (got != restLen) {
/* 271:455 */                 throw new IOException("Failed to read Paxheader. Expected " + restLen + " bytes, read " + got);
/* 272:    */               }
/* 273:462 */               String value = new String(rest, 0, restLen - 1, "UTF-8");
/* 274:    */               
/* 275:464 */               headers.put(keyword, value);
/* 276:465 */               break;
/* 277:    */             }
/* 278:467 */             coll.write((byte)ch);
/* 279:    */           }
/* 280:    */         }
/* 281:471 */         len *= 10;
/* 282:472 */         len += ch - 48;
/* 283:    */       }
/* 284:474 */       if (ch == -1) {
/* 285:    */         break;
/* 286:    */       }
/* 287:    */     }
/* 288:478 */     return headers;
/* 289:    */   }
/* 290:    */   
/* 291:    */   private void applyPaxHeadersToCurrentEntry(Map<String, String> headers)
/* 292:    */   {
/* 293:493 */     for (Map.Entry<String, String> ent : headers.entrySet())
/* 294:    */     {
/* 295:494 */       String key = (String)ent.getKey();
/* 296:495 */       String val = (String)ent.getValue();
/* 297:496 */       if ("path".equals(key)) {
/* 298:497 */         this.currEntry.setName(val);
/* 299:498 */       } else if ("linkpath".equals(key)) {
/* 300:499 */         this.currEntry.setLinkName(val);
/* 301:500 */       } else if ("gid".equals(key)) {
/* 302:501 */         this.currEntry.setGroupId(Long.parseLong(val));
/* 303:502 */       } else if ("gname".equals(key)) {
/* 304:503 */         this.currEntry.setGroupName(val);
/* 305:504 */       } else if ("uid".equals(key)) {
/* 306:505 */         this.currEntry.setUserId(Long.parseLong(val));
/* 307:506 */       } else if ("uname".equals(key)) {
/* 308:507 */         this.currEntry.setUserName(val);
/* 309:508 */       } else if ("size".equals(key)) {
/* 310:509 */         this.currEntry.setSize(Long.parseLong(val));
/* 311:510 */       } else if ("mtime".equals(key)) {
/* 312:511 */         this.currEntry.setModTime((Double.parseDouble(val) * 1000.0D));
/* 313:512 */       } else if ("SCHILY.devminor".equals(key)) {
/* 314:513 */         this.currEntry.setDevMinor(Integer.parseInt(val));
/* 315:514 */       } else if ("SCHILY.devmajor".equals(key)) {
/* 316:515 */         this.currEntry.setDevMajor(Integer.parseInt(val));
/* 317:    */       }
/* 318:    */     }
/* 319:    */   }
/* 320:    */   
/* 321:    */   private void readGNUSparse()
/* 322:    */     throws IOException
/* 323:    */   {
/* 324:533 */     if (this.currEntry.isExtended())
/* 325:    */     {
/* 326:    */       TarArchiveSparseEntry entry;
/* 327:    */       do
/* 328:    */       {
/* 329:536 */         byte[] headerBuf = getRecord();
/* 330:537 */         if (headerBuf == null)
/* 331:    */         {
/* 332:538 */           this.currEntry = null;
/* 333:539 */           break;
/* 334:    */         }
/* 335:541 */         entry = new TarArchiveSparseEntry(headerBuf);
/* 336:545 */       } while (entry.isExtended());
/* 337:    */     }
/* 338:    */   }
/* 339:    */   
/* 340:    */   public ArchiveEntry getNextEntry()
/* 341:    */     throws IOException
/* 342:    */   {
/* 343:558 */     return getNextTarEntry();
/* 344:    */   }
/* 345:    */   
/* 346:    */   private void tryToConsumeSecondEOFRecord()
/* 347:    */     throws IOException
/* 348:    */   {
/* 349:572 */     boolean shouldReset = true;
/* 350:573 */     boolean marked = this.is.markSupported();
/* 351:574 */     if (marked) {
/* 352:575 */       this.is.mark(this.recordSize);
/* 353:    */     }
/* 354:    */     try
/* 355:    */     {
/* 356:578 */       shouldReset = !isEOFRecord(readRecord());
/* 357:    */     }
/* 358:    */     finally
/* 359:    */     {
/* 360:580 */       if ((shouldReset) && (marked))
/* 361:    */       {
/* 362:581 */         pushedBackBytes(this.recordSize);
/* 363:582 */         this.is.reset();
/* 364:    */       }
/* 365:    */     }
/* 366:    */   }
/* 367:    */   
/* 368:    */   public int read(byte[] buf, int offset, int numToRead)
/* 369:    */     throws IOException
/* 370:    */   {
/* 371:602 */     int totalRead = 0;
/* 372:604 */     if ((this.hasHitEOF) || (this.entryOffset >= this.entrySize)) {
/* 373:605 */       return -1;
/* 374:    */     }
/* 375:608 */     if (this.currEntry == null) {
/* 376:609 */       throw new IllegalStateException("No current tar entry");
/* 377:    */     }
/* 378:612 */     numToRead = Math.min(numToRead, available());
/* 379:    */     
/* 380:614 */     totalRead = this.is.read(buf, offset, numToRead);
/* 381:616 */     if (totalRead == -1)
/* 382:    */     {
/* 383:617 */       if (numToRead > 0) {
/* 384:618 */         throw new IOException("Truncated TAR archive");
/* 385:    */       }
/* 386:620 */       this.hasHitEOF = true;
/* 387:    */     }
/* 388:    */     else
/* 389:    */     {
/* 390:622 */       count(totalRead);
/* 391:623 */       this.entryOffset += totalRead;
/* 392:    */     }
/* 393:626 */     return totalRead;
/* 394:    */   }
/* 395:    */   
/* 396:    */   public boolean canReadEntryData(ArchiveEntry ae)
/* 397:    */   {
/* 398:636 */     if ((ae instanceof TarArchiveEntry))
/* 399:    */     {
/* 400:637 */       TarArchiveEntry te = (TarArchiveEntry)ae;
/* 401:638 */       return !te.isGNUSparse();
/* 402:    */     }
/* 403:640 */     return false;
/* 404:    */   }
/* 405:    */   
/* 406:    */   public TarArchiveEntry getCurrentEntry()
/* 407:    */   {
/* 408:649 */     return this.currEntry;
/* 409:    */   }
/* 410:    */   
/* 411:    */   protected final void setCurrentEntry(TarArchiveEntry e)
/* 412:    */   {
/* 413:653 */     this.currEntry = e;
/* 414:    */   }
/* 415:    */   
/* 416:    */   protected final boolean isAtEOF()
/* 417:    */   {
/* 418:657 */     return this.hasHitEOF;
/* 419:    */   }
/* 420:    */   
/* 421:    */   protected final void setAtEOF(boolean b)
/* 422:    */   {
/* 423:661 */     this.hasHitEOF = b;
/* 424:    */   }
/* 425:    */   
/* 426:    */   private void consumeRemainderOfLastBlock()
/* 427:    */     throws IOException
/* 428:    */   {
/* 429:670 */     long bytesReadOfLastBlock = getBytesRead() % this.blockSize;
/* 430:671 */     if (bytesReadOfLastBlock > 0L)
/* 431:    */     {
/* 432:672 */       long skipped = IOUtils.skip(this.is, this.blockSize - bytesReadOfLastBlock);
/* 433:673 */       count(skipped);
/* 434:    */     }
/* 435:    */   }
/* 436:    */   
/* 437:    */   public static boolean matches(byte[] signature, int length)
/* 438:    */   {
/* 439:687 */     if (length < 265) {
/* 440:688 */       return false;
/* 441:    */     }
/* 442:691 */     if ((ArchiveUtils.matchAsciiBuffer("", signature, 257, 6)) && (ArchiveUtils.matchAsciiBuffer("00", signature, 263, 2))) {
/* 443:697 */       return true;
/* 444:    */     }
/* 445:699 */     if ((ArchiveUtils.matchAsciiBuffer("ustar ", signature, 257, 6)) && ((ArchiveUtils.matchAsciiBuffer("", signature, 263, 2)) || (ArchiveUtils.matchAsciiBuffer("", signature, 263, 2)))) {
/* 446:710 */       return true;
/* 447:    */     }
/* 448:713 */     if ((ArchiveUtils.matchAsciiBuffer("", signature, 257, 6)) && (ArchiveUtils.matchAsciiBuffer("", signature, 263, 2))) {
/* 449:719 */       return true;
/* 450:    */     }
/* 451:721 */     return false;
/* 452:    */   }
/* 453:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.tar.TarArchiveInputStream
 * JD-Core Version:    0.7.0.1
 */