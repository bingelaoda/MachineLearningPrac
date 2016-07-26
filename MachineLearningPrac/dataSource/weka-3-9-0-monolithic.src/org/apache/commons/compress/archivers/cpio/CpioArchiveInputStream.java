/*   1:    */ package org.apache.commons.compress.archivers.cpio;
/*   2:    */ 
/*   3:    */ import java.io.EOFException;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.InputStream;
/*   6:    */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*   7:    */ import org.apache.commons.compress.archivers.ArchiveInputStream;
/*   8:    */ import org.apache.commons.compress.archivers.zip.ZipEncoding;
/*   9:    */ import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
/*  10:    */ import org.apache.commons.compress.utils.ArchiveUtils;
/*  11:    */ import org.apache.commons.compress.utils.IOUtils;
/*  12:    */ 
/*  13:    */ public class CpioArchiveInputStream
/*  14:    */   extends ArchiveInputStream
/*  15:    */   implements CpioConstants
/*  16:    */ {
/*  17: 70 */   private boolean closed = false;
/*  18:    */   private CpioArchiveEntry entry;
/*  19: 74 */   private long entryBytesRead = 0L;
/*  20: 76 */   private boolean entryEOF = false;
/*  21: 78 */   private final byte[] tmpbuf = new byte[4096];
/*  22: 80 */   private long crc = 0L;
/*  23:    */   private final InputStream in;
/*  24: 85 */   private final byte[] TWO_BYTES_BUF = new byte[2];
/*  25: 86 */   private final byte[] FOUR_BYTES_BUF = new byte[4];
/*  26: 87 */   private final byte[] SIX_BYTES_BUF = new byte[6];
/*  27:    */   private final int blockSize;
/*  28:    */   private final ZipEncoding zipEncoding;
/*  29:    */   final String encoding;
/*  30:    */   
/*  31:    */   public CpioArchiveInputStream(InputStream in)
/*  32:    */   {
/*  33:108 */     this(in, 512, "US-ASCII");
/*  34:    */   }
/*  35:    */   
/*  36:    */   public CpioArchiveInputStream(InputStream in, String encoding)
/*  37:    */   {
/*  38:123 */     this(in, 512, encoding);
/*  39:    */   }
/*  40:    */   
/*  41:    */   public CpioArchiveInputStream(InputStream in, int blockSize)
/*  42:    */   {
/*  43:138 */     this(in, blockSize, "US-ASCII");
/*  44:    */   }
/*  45:    */   
/*  46:    */   public CpioArchiveInputStream(InputStream in, int blockSize, String encoding)
/*  47:    */   {
/*  48:154 */     this.in = in;
/*  49:155 */     this.blockSize = blockSize;
/*  50:156 */     this.encoding = encoding;
/*  51:157 */     this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
/*  52:    */   }
/*  53:    */   
/*  54:    */   public int available()
/*  55:    */     throws IOException
/*  56:    */   {
/*  57:174 */     ensureOpen();
/*  58:175 */     if (this.entryEOF) {
/*  59:176 */       return 0;
/*  60:    */     }
/*  61:178 */     return 1;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public void close()
/*  65:    */     throws IOException
/*  66:    */   {
/*  67:189 */     if (!this.closed)
/*  68:    */     {
/*  69:190 */       this.in.close();
/*  70:191 */       this.closed = true;
/*  71:    */     }
/*  72:    */   }
/*  73:    */   
/*  74:    */   private void closeEntry()
/*  75:    */     throws IOException
/*  76:    */   {
/*  77:206 */     while (skip(2147483647L) == 2147483647L) {}
/*  78:    */   }
/*  79:    */   
/*  80:    */   private void ensureOpen()
/*  81:    */     throws IOException
/*  82:    */   {
/*  83:218 */     if (this.closed) {
/*  84:219 */       throw new IOException("Stream closed");
/*  85:    */     }
/*  86:    */   }
/*  87:    */   
/*  88:    */   public CpioArchiveEntry getNextCPIOEntry()
/*  89:    */     throws IOException
/*  90:    */   {
/*  91:233 */     ensureOpen();
/*  92:234 */     if (this.entry != null) {
/*  93:235 */       closeEntry();
/*  94:    */     }
/*  95:237 */     readFully(this.TWO_BYTES_BUF, 0, this.TWO_BYTES_BUF.length);
/*  96:238 */     if (CpioUtil.byteArray2long(this.TWO_BYTES_BUF, false) == 29127L)
/*  97:    */     {
/*  98:239 */       this.entry = readOldBinaryEntry(false);
/*  99:    */     }
/* 100:240 */     else if (CpioUtil.byteArray2long(this.TWO_BYTES_BUF, true) == 29127L)
/* 101:    */     {
/* 102:242 */       this.entry = readOldBinaryEntry(true);
/* 103:    */     }
/* 104:    */     else
/* 105:    */     {
/* 106:244 */       System.arraycopy(this.TWO_BYTES_BUF, 0, this.SIX_BYTES_BUF, 0, this.TWO_BYTES_BUF.length);
/* 107:    */       
/* 108:246 */       readFully(this.SIX_BYTES_BUF, this.TWO_BYTES_BUF.length, this.FOUR_BYTES_BUF.length);
/* 109:    */       
/* 110:248 */       String magicString = ArchiveUtils.toAsciiString(this.SIX_BYTES_BUF);
/* 111:249 */       if (magicString.equals("070701")) {
/* 112:250 */         this.entry = readNewEntry(false);
/* 113:251 */       } else if (magicString.equals("070702")) {
/* 114:252 */         this.entry = readNewEntry(true);
/* 115:253 */       } else if (magicString.equals("070707")) {
/* 116:254 */         this.entry = readOldAsciiEntry();
/* 117:    */       } else {
/* 118:256 */         throw new IOException("Unknown magic [" + magicString + "]. Occured at byte: " + getBytesRead());
/* 119:    */       }
/* 120:    */     }
/* 121:260 */     this.entryBytesRead = 0L;
/* 122:261 */     this.entryEOF = false;
/* 123:262 */     this.crc = 0L;
/* 124:264 */     if (this.entry.getName().equals("TRAILER!!!"))
/* 125:    */     {
/* 126:265 */       this.entryEOF = true;
/* 127:266 */       skipRemainderOfLastBlock();
/* 128:267 */       return null;
/* 129:    */     }
/* 130:269 */     return this.entry;
/* 131:    */   }
/* 132:    */   
/* 133:    */   private void skip(int bytes)
/* 134:    */     throws IOException
/* 135:    */   {
/* 136:274 */     if (bytes > 0) {
/* 137:275 */       readFully(this.FOUR_BYTES_BUF, 0, bytes);
/* 138:    */     }
/* 139:    */   }
/* 140:    */   
/* 141:    */   public int read(byte[] b, int off, int len)
/* 142:    */     throws IOException
/* 143:    */   {
/* 144:298 */     ensureOpen();
/* 145:299 */     if ((off < 0) || (len < 0) || (off > b.length - len)) {
/* 146:300 */       throw new IndexOutOfBoundsException();
/* 147:    */     }
/* 148:301 */     if (len == 0) {
/* 149:302 */       return 0;
/* 150:    */     }
/* 151:305 */     if ((this.entry == null) || (this.entryEOF)) {
/* 152:306 */       return -1;
/* 153:    */     }
/* 154:308 */     if (this.entryBytesRead == this.entry.getSize())
/* 155:    */     {
/* 156:309 */       skip(this.entry.getDataPadCount());
/* 157:310 */       this.entryEOF = true;
/* 158:311 */       if ((this.entry.getFormat() == 2) && (this.crc != this.entry.getChksum())) {
/* 159:313 */         throw new IOException("CRC Error. Occured at byte: " + getBytesRead());
/* 160:    */       }
/* 161:316 */       return -1;
/* 162:    */     }
/* 163:318 */     int tmplength = (int)Math.min(len, this.entry.getSize() - this.entryBytesRead);
/* 164:320 */     if (tmplength < 0) {
/* 165:321 */       return -1;
/* 166:    */     }
/* 167:324 */     int tmpread = readFully(b, off, tmplength);
/* 168:325 */     if (this.entry.getFormat() == 2) {
/* 169:326 */       for (int pos = 0; pos < tmpread; pos++) {
/* 170:327 */         this.crc += (b[pos] & 0xFF);
/* 171:    */       }
/* 172:    */     }
/* 173:330 */     this.entryBytesRead += tmpread;
/* 174:    */     
/* 175:332 */     return tmpread;
/* 176:    */   }
/* 177:    */   
/* 178:    */   private final int readFully(byte[] b, int off, int len)
/* 179:    */     throws IOException
/* 180:    */   {
/* 181:337 */     int count = IOUtils.readFully(this.in, b, off, len);
/* 182:338 */     count(count);
/* 183:339 */     if (count < len) {
/* 184:340 */       throw new EOFException();
/* 185:    */     }
/* 186:342 */     return count;
/* 187:    */   }
/* 188:    */   
/* 189:    */   private long readBinaryLong(int length, boolean swapHalfWord)
/* 190:    */     throws IOException
/* 191:    */   {
/* 192:347 */     byte[] tmp = new byte[length];
/* 193:348 */     readFully(tmp, 0, tmp.length);
/* 194:349 */     return CpioUtil.byteArray2long(tmp, swapHalfWord);
/* 195:    */   }
/* 196:    */   
/* 197:    */   private long readAsciiLong(int length, int radix)
/* 198:    */     throws IOException
/* 199:    */   {
/* 200:354 */     byte[] tmpBuffer = new byte[length];
/* 201:355 */     readFully(tmpBuffer, 0, tmpBuffer.length);
/* 202:356 */     return Long.parseLong(ArchiveUtils.toAsciiString(tmpBuffer), radix);
/* 203:    */   }
/* 204:    */   
/* 205:    */   private CpioArchiveEntry readNewEntry(boolean hasCrc)
/* 206:    */     throws IOException
/* 207:    */   {
/* 208:    */     CpioArchiveEntry ret;
/* 209:    */     CpioArchiveEntry ret;
/* 210:362 */     if (hasCrc) {
/* 211:363 */       ret = new CpioArchiveEntry((short)2);
/* 212:    */     } else {
/* 213:365 */       ret = new CpioArchiveEntry((short)1);
/* 214:    */     }
/* 215:368 */     ret.setInode(readAsciiLong(8, 16));
/* 216:369 */     long mode = readAsciiLong(8, 16);
/* 217:370 */     if (CpioUtil.fileType(mode) != 0L) {
/* 218:371 */       ret.setMode(mode);
/* 219:    */     }
/* 220:373 */     ret.setUID(readAsciiLong(8, 16));
/* 221:374 */     ret.setGID(readAsciiLong(8, 16));
/* 222:375 */     ret.setNumberOfLinks(readAsciiLong(8, 16));
/* 223:376 */     ret.setTime(readAsciiLong(8, 16));
/* 224:377 */     ret.setSize(readAsciiLong(8, 16));
/* 225:378 */     ret.setDeviceMaj(readAsciiLong(8, 16));
/* 226:379 */     ret.setDeviceMin(readAsciiLong(8, 16));
/* 227:380 */     ret.setRemoteDeviceMaj(readAsciiLong(8, 16));
/* 228:381 */     ret.setRemoteDeviceMin(readAsciiLong(8, 16));
/* 229:382 */     long namesize = readAsciiLong(8, 16);
/* 230:383 */     ret.setChksum(readAsciiLong(8, 16));
/* 231:384 */     String name = readCString((int)namesize);
/* 232:385 */     ret.setName(name);
/* 233:386 */     if ((CpioUtil.fileType(mode) == 0L) && (!name.equals("TRAILER!!!"))) {
/* 234:387 */       throw new IOException("Mode 0 only allowed in the trailer. Found entry name: " + name + " Occured at byte: " + getBytesRead());
/* 235:    */     }
/* 236:389 */     skip(ret.getHeaderPadCount());
/* 237:    */     
/* 238:391 */     return ret;
/* 239:    */   }
/* 240:    */   
/* 241:    */   private CpioArchiveEntry readOldAsciiEntry()
/* 242:    */     throws IOException
/* 243:    */   {
/* 244:395 */     CpioArchiveEntry ret = new CpioArchiveEntry((short)4);
/* 245:    */     
/* 246:397 */     ret.setDevice(readAsciiLong(6, 8));
/* 247:398 */     ret.setInode(readAsciiLong(6, 8));
/* 248:399 */     long mode = readAsciiLong(6, 8);
/* 249:400 */     if (CpioUtil.fileType(mode) != 0L) {
/* 250:401 */       ret.setMode(mode);
/* 251:    */     }
/* 252:403 */     ret.setUID(readAsciiLong(6, 8));
/* 253:404 */     ret.setGID(readAsciiLong(6, 8));
/* 254:405 */     ret.setNumberOfLinks(readAsciiLong(6, 8));
/* 255:406 */     ret.setRemoteDevice(readAsciiLong(6, 8));
/* 256:407 */     ret.setTime(readAsciiLong(11, 8));
/* 257:408 */     long namesize = readAsciiLong(6, 8);
/* 258:409 */     ret.setSize(readAsciiLong(11, 8));
/* 259:410 */     String name = readCString((int)namesize);
/* 260:411 */     ret.setName(name);
/* 261:412 */     if ((CpioUtil.fileType(mode) == 0L) && (!name.equals("TRAILER!!!"))) {
/* 262:413 */       throw new IOException("Mode 0 only allowed in the trailer. Found entry: " + name + " Occured at byte: " + getBytesRead());
/* 263:    */     }
/* 264:416 */     return ret;
/* 265:    */   }
/* 266:    */   
/* 267:    */   private CpioArchiveEntry readOldBinaryEntry(boolean swapHalfWord)
/* 268:    */     throws IOException
/* 269:    */   {
/* 270:421 */     CpioArchiveEntry ret = new CpioArchiveEntry((short)8);
/* 271:    */     
/* 272:423 */     ret.setDevice(readBinaryLong(2, swapHalfWord));
/* 273:424 */     ret.setInode(readBinaryLong(2, swapHalfWord));
/* 274:425 */     long mode = readBinaryLong(2, swapHalfWord);
/* 275:426 */     if (CpioUtil.fileType(mode) != 0L) {
/* 276:427 */       ret.setMode(mode);
/* 277:    */     }
/* 278:429 */     ret.setUID(readBinaryLong(2, swapHalfWord));
/* 279:430 */     ret.setGID(readBinaryLong(2, swapHalfWord));
/* 280:431 */     ret.setNumberOfLinks(readBinaryLong(2, swapHalfWord));
/* 281:432 */     ret.setRemoteDevice(readBinaryLong(2, swapHalfWord));
/* 282:433 */     ret.setTime(readBinaryLong(4, swapHalfWord));
/* 283:434 */     long namesize = readBinaryLong(2, swapHalfWord);
/* 284:435 */     ret.setSize(readBinaryLong(4, swapHalfWord));
/* 285:436 */     String name = readCString((int)namesize);
/* 286:437 */     ret.setName(name);
/* 287:438 */     if ((CpioUtil.fileType(mode) == 0L) && (!name.equals("TRAILER!!!"))) {
/* 288:439 */       throw new IOException("Mode 0 only allowed in the trailer. Found entry: " + name + "Occured at byte: " + getBytesRead());
/* 289:    */     }
/* 290:441 */     skip(ret.getHeaderPadCount());
/* 291:    */     
/* 292:443 */     return ret;
/* 293:    */   }
/* 294:    */   
/* 295:    */   private String readCString(int length)
/* 296:    */     throws IOException
/* 297:    */   {
/* 298:448 */     byte[] tmpBuffer = new byte[length - 1];
/* 299:449 */     readFully(tmpBuffer, 0, tmpBuffer.length);
/* 300:450 */     this.in.read();
/* 301:451 */     return this.zipEncoding.decode(tmpBuffer);
/* 302:    */   }
/* 303:    */   
/* 304:    */   public long skip(long n)
/* 305:    */     throws IOException
/* 306:    */   {
/* 307:467 */     if (n < 0L) {
/* 308:468 */       throw new IllegalArgumentException("negative skip length");
/* 309:    */     }
/* 310:470 */     ensureOpen();
/* 311:471 */     int max = (int)Math.min(n, 2147483647L);
/* 312:472 */     int total = 0;
/* 313:474 */     while (total < max)
/* 314:    */     {
/* 315:475 */       int len = max - total;
/* 316:476 */       if (len > this.tmpbuf.length) {
/* 317:477 */         len = this.tmpbuf.length;
/* 318:    */       }
/* 319:479 */       len = read(this.tmpbuf, 0, len);
/* 320:480 */       if (len == -1)
/* 321:    */       {
/* 322:481 */         this.entryEOF = true;
/* 323:482 */         break;
/* 324:    */       }
/* 325:484 */       total += len;
/* 326:    */     }
/* 327:486 */     return total;
/* 328:    */   }
/* 329:    */   
/* 330:    */   public ArchiveEntry getNextEntry()
/* 331:    */     throws IOException
/* 332:    */   {
/* 333:491 */     return getNextCPIOEntry();
/* 334:    */   }
/* 335:    */   
/* 336:    */   private void skipRemainderOfLastBlock()
/* 337:    */     throws IOException
/* 338:    */   {
/* 339:498 */     long readFromLastBlock = getBytesRead() % this.blockSize;
/* 340:499 */     long remainingBytes = readFromLastBlock == 0L ? 0L : this.blockSize - readFromLastBlock;
/* 341:501 */     while (remainingBytes > 0L)
/* 342:    */     {
/* 343:502 */       long skipped = skip(this.blockSize - readFromLastBlock);
/* 344:503 */       if (skipped <= 0L) {
/* 345:    */         break;
/* 346:    */       }
/* 347:506 */       remainingBytes -= skipped;
/* 348:    */     }
/* 349:    */   }
/* 350:    */   
/* 351:    */   public static boolean matches(byte[] signature, int length)
/* 352:    */   {
/* 353:527 */     if (length < 6) {
/* 354:528 */       return false;
/* 355:    */     }
/* 356:532 */     if ((signature[0] == 113) && ((signature[1] & 0xFF) == 199)) {
/* 357:533 */       return true;
/* 358:    */     }
/* 359:535 */     if ((signature[1] == 113) && ((signature[0] & 0xFF) == 199)) {
/* 360:536 */       return true;
/* 361:    */     }
/* 362:541 */     if (signature[0] != 48) {
/* 363:542 */       return false;
/* 364:    */     }
/* 365:544 */     if (signature[1] != 55) {
/* 366:545 */       return false;
/* 367:    */     }
/* 368:547 */     if (signature[2] != 48) {
/* 369:548 */       return false;
/* 370:    */     }
/* 371:550 */     if (signature[3] != 55) {
/* 372:551 */       return false;
/* 373:    */     }
/* 374:553 */     if (signature[4] != 48) {
/* 375:554 */       return false;
/* 376:    */     }
/* 377:557 */     if (signature[5] == 49) {
/* 378:558 */       return true;
/* 379:    */     }
/* 380:560 */     if (signature[5] == 50) {
/* 381:561 */       return true;
/* 382:    */     }
/* 383:563 */     if (signature[5] == 55) {
/* 384:564 */       return true;
/* 385:    */     }
/* 386:567 */     return false;
/* 387:    */   }
/* 388:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream
 * JD-Core Version:    0.7.0.1
 */