/*   1:    */ package org.apache.commons.compress.archivers.cpio;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.OutputStream;
/*   6:    */ import java.nio.ByteBuffer;
/*   7:    */ import java.util.HashMap;
/*   8:    */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*   9:    */ import org.apache.commons.compress.archivers.ArchiveOutputStream;
/*  10:    */ import org.apache.commons.compress.archivers.zip.ZipEncoding;
/*  11:    */ import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
/*  12:    */ import org.apache.commons.compress.utils.ArchiveUtils;
/*  13:    */ 
/*  14:    */ public class CpioArchiveOutputStream
/*  15:    */   extends ArchiveOutputStream
/*  16:    */   implements CpioConstants
/*  17:    */ {
/*  18:    */   private CpioArchiveEntry entry;
/*  19: 69 */   private boolean closed = false;
/*  20:    */   private boolean finished;
/*  21:    */   private final short entryFormat;
/*  22: 79 */   private final HashMap<String, CpioArchiveEntry> names = new HashMap();
/*  23: 82 */   private long crc = 0L;
/*  24:    */   private long written;
/*  25:    */   private final OutputStream out;
/*  26:    */   private final int blockSize;
/*  27: 90 */   private long nextArtificalDeviceAndInode = 1L;
/*  28:    */   private final ZipEncoding zipEncoding;
/*  29:    */   final String encoding;
/*  30:    */   
/*  31:    */   public CpioArchiveOutputStream(OutputStream out, short format)
/*  32:    */   {
/*  33:111 */     this(out, format, 512, "US-ASCII");
/*  34:    */   }
/*  35:    */   
/*  36:    */   public CpioArchiveOutputStream(OutputStream out, short format, int blockSize)
/*  37:    */   {
/*  38:129 */     this(out, format, blockSize, "US-ASCII");
/*  39:    */   }
/*  40:    */   
/*  41:    */   public CpioArchiveOutputStream(OutputStream out, short format, int blockSize, String encoding)
/*  42:    */   {
/*  43:150 */     this.out = out;
/*  44:151 */     switch (format)
/*  45:    */     {
/*  46:    */     case 1: 
/*  47:    */     case 2: 
/*  48:    */     case 4: 
/*  49:    */     case 8: 
/*  50:    */       break;
/*  51:    */     case 3: 
/*  52:    */     case 5: 
/*  53:    */     case 6: 
/*  54:    */     case 7: 
/*  55:    */     default: 
/*  56:158 */       throw new IllegalArgumentException("Unknown format: " + format);
/*  57:    */     }
/*  58:161 */     this.entryFormat = format;
/*  59:162 */     this.blockSize = blockSize;
/*  60:163 */     this.encoding = encoding;
/*  61:164 */     this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
/*  62:    */   }
/*  63:    */   
/*  64:    */   public CpioArchiveOutputStream(OutputStream out)
/*  65:    */   {
/*  66:175 */     this(out, (short)1);
/*  67:    */   }
/*  68:    */   
/*  69:    */   public CpioArchiveOutputStream(OutputStream out, String encoding)
/*  70:    */   {
/*  71:190 */     this(out, (short)1, 512, encoding);
/*  72:    */   }
/*  73:    */   
/*  74:    */   private void ensureOpen()
/*  75:    */     throws IOException
/*  76:    */   {
/*  77:200 */     if (this.closed) {
/*  78:201 */       throw new IOException("Stream closed");
/*  79:    */     }
/*  80:    */   }
/*  81:    */   
/*  82:    */   public void putArchiveEntry(ArchiveEntry entry)
/*  83:    */     throws IOException
/*  84:    */   {
/*  85:221 */     if (this.finished) {
/*  86:222 */       throw new IOException("Stream has already been finished");
/*  87:    */     }
/*  88:225 */     CpioArchiveEntry e = (CpioArchiveEntry)entry;
/*  89:226 */     ensureOpen();
/*  90:227 */     if (this.entry != null) {
/*  91:228 */       closeArchiveEntry();
/*  92:    */     }
/*  93:230 */     if (e.getTime() == -1L) {
/*  94:231 */       e.setTime(System.currentTimeMillis() / 1000L);
/*  95:    */     }
/*  96:234 */     short format = e.getFormat();
/*  97:235 */     if (format != this.entryFormat) {
/*  98:236 */       throw new IOException("Header format: " + format + " does not match existing format: " + this.entryFormat);
/*  99:    */     }
/* 100:239 */     if (this.names.put(e.getName(), e) != null) {
/* 101:240 */       throw new IOException("duplicate entry: " + e.getName());
/* 102:    */     }
/* 103:243 */     writeHeader(e);
/* 104:244 */     this.entry = e;
/* 105:245 */     this.written = 0L;
/* 106:    */   }
/* 107:    */   
/* 108:    */   private void writeHeader(CpioArchiveEntry e)
/* 109:    */     throws IOException
/* 110:    */   {
/* 111:249 */     switch (e.getFormat())
/* 112:    */     {
/* 113:    */     case 1: 
/* 114:251 */       this.out.write(ArchiveUtils.toAsciiBytes("070701"));
/* 115:252 */       count(6);
/* 116:253 */       writeNewEntry(e);
/* 117:254 */       break;
/* 118:    */     case 2: 
/* 119:256 */       this.out.write(ArchiveUtils.toAsciiBytes("070702"));
/* 120:257 */       count(6);
/* 121:258 */       writeNewEntry(e);
/* 122:259 */       break;
/* 123:    */     case 4: 
/* 124:261 */       this.out.write(ArchiveUtils.toAsciiBytes("070707"));
/* 125:262 */       count(6);
/* 126:263 */       writeOldAsciiEntry(e);
/* 127:264 */       break;
/* 128:    */     case 8: 
/* 129:266 */       boolean swapHalfWord = true;
/* 130:267 */       writeBinaryLong(29127L, 2, swapHalfWord);
/* 131:268 */       writeOldBinaryEntry(e, swapHalfWord);
/* 132:269 */       break;
/* 133:    */     case 3: 
/* 134:    */     case 5: 
/* 135:    */     case 6: 
/* 136:    */     case 7: 
/* 137:    */     default: 
/* 138:271 */       throw new IOException("unknown format " + e.getFormat());
/* 139:    */     }
/* 140:    */   }
/* 141:    */   
/* 142:    */   private void writeNewEntry(CpioArchiveEntry entry)
/* 143:    */     throws IOException
/* 144:    */   {
/* 145:276 */     long inode = entry.getInode();
/* 146:277 */     long devMin = entry.getDeviceMin();
/* 147:278 */     if ("TRAILER!!!".equals(entry.getName()))
/* 148:    */     {
/* 149:279 */       inode = devMin = 0L;
/* 150:    */     }
/* 151:281 */     else if ((inode == 0L) && (devMin == 0L))
/* 152:    */     {
/* 153:282 */       inode = this.nextArtificalDeviceAndInode & 0xFFFFFFFF;
/* 154:283 */       devMin = this.nextArtificalDeviceAndInode++ >> 32 & 0xFFFFFFFF;
/* 155:    */     }
/* 156:    */     else
/* 157:    */     {
/* 158:285 */       this.nextArtificalDeviceAndInode = (Math.max(this.nextArtificalDeviceAndInode, inode + 4294967296L * devMin) + 1L);
/* 159:    */     }
/* 160:291 */     writeAsciiLong(inode, 8, 16);
/* 161:292 */     writeAsciiLong(entry.getMode(), 8, 16);
/* 162:293 */     writeAsciiLong(entry.getUID(), 8, 16);
/* 163:294 */     writeAsciiLong(entry.getGID(), 8, 16);
/* 164:295 */     writeAsciiLong(entry.getNumberOfLinks(), 8, 16);
/* 165:296 */     writeAsciiLong(entry.getTime(), 8, 16);
/* 166:297 */     writeAsciiLong(entry.getSize(), 8, 16);
/* 167:298 */     writeAsciiLong(entry.getDeviceMaj(), 8, 16);
/* 168:299 */     writeAsciiLong(devMin, 8, 16);
/* 169:300 */     writeAsciiLong(entry.getRemoteDeviceMaj(), 8, 16);
/* 170:301 */     writeAsciiLong(entry.getRemoteDeviceMin(), 8, 16);
/* 171:302 */     writeAsciiLong(entry.getName().length() + 1, 8, 16);
/* 172:303 */     writeAsciiLong(entry.getChksum(), 8, 16);
/* 173:304 */     writeCString(entry.getName());
/* 174:305 */     pad(entry.getHeaderPadCount());
/* 175:    */   }
/* 176:    */   
/* 177:    */   private void writeOldAsciiEntry(CpioArchiveEntry entry)
/* 178:    */     throws IOException
/* 179:    */   {
/* 180:310 */     long inode = entry.getInode();
/* 181:311 */     long device = entry.getDevice();
/* 182:312 */     if ("TRAILER!!!".equals(entry.getName()))
/* 183:    */     {
/* 184:313 */       inode = device = 0L;
/* 185:    */     }
/* 186:315 */     else if ((inode == 0L) && (device == 0L))
/* 187:    */     {
/* 188:316 */       inode = this.nextArtificalDeviceAndInode & 0x3FFFF;
/* 189:317 */       device = this.nextArtificalDeviceAndInode++ >> 18 & 0x3FFFF;
/* 190:    */     }
/* 191:    */     else
/* 192:    */     {
/* 193:319 */       this.nextArtificalDeviceAndInode = (Math.max(this.nextArtificalDeviceAndInode, inode + 262144L * device) + 1L);
/* 194:    */     }
/* 195:325 */     writeAsciiLong(device, 6, 8);
/* 196:326 */     writeAsciiLong(inode, 6, 8);
/* 197:327 */     writeAsciiLong(entry.getMode(), 6, 8);
/* 198:328 */     writeAsciiLong(entry.getUID(), 6, 8);
/* 199:329 */     writeAsciiLong(entry.getGID(), 6, 8);
/* 200:330 */     writeAsciiLong(entry.getNumberOfLinks(), 6, 8);
/* 201:331 */     writeAsciiLong(entry.getRemoteDevice(), 6, 8);
/* 202:332 */     writeAsciiLong(entry.getTime(), 11, 8);
/* 203:333 */     writeAsciiLong(entry.getName().length() + 1, 6, 8);
/* 204:334 */     writeAsciiLong(entry.getSize(), 11, 8);
/* 205:335 */     writeCString(entry.getName());
/* 206:    */   }
/* 207:    */   
/* 208:    */   private void writeOldBinaryEntry(CpioArchiveEntry entry, boolean swapHalfWord)
/* 209:    */     throws IOException
/* 210:    */   {
/* 211:340 */     long inode = entry.getInode();
/* 212:341 */     long device = entry.getDevice();
/* 213:342 */     if ("TRAILER!!!".equals(entry.getName()))
/* 214:    */     {
/* 215:343 */       inode = device = 0L;
/* 216:    */     }
/* 217:345 */     else if ((inode == 0L) && (device == 0L))
/* 218:    */     {
/* 219:346 */       inode = this.nextArtificalDeviceAndInode & 0xFFFF;
/* 220:347 */       device = this.nextArtificalDeviceAndInode++ >> 16 & 0xFFFF;
/* 221:    */     }
/* 222:    */     else
/* 223:    */     {
/* 224:349 */       this.nextArtificalDeviceAndInode = (Math.max(this.nextArtificalDeviceAndInode, inode + 65536L * device) + 1L);
/* 225:    */     }
/* 226:355 */     writeBinaryLong(device, 2, swapHalfWord);
/* 227:356 */     writeBinaryLong(inode, 2, swapHalfWord);
/* 228:357 */     writeBinaryLong(entry.getMode(), 2, swapHalfWord);
/* 229:358 */     writeBinaryLong(entry.getUID(), 2, swapHalfWord);
/* 230:359 */     writeBinaryLong(entry.getGID(), 2, swapHalfWord);
/* 231:360 */     writeBinaryLong(entry.getNumberOfLinks(), 2, swapHalfWord);
/* 232:361 */     writeBinaryLong(entry.getRemoteDevice(), 2, swapHalfWord);
/* 233:362 */     writeBinaryLong(entry.getTime(), 4, swapHalfWord);
/* 234:363 */     writeBinaryLong(entry.getName().length() + 1, 2, swapHalfWord);
/* 235:364 */     writeBinaryLong(entry.getSize(), 4, swapHalfWord);
/* 236:365 */     writeCString(entry.getName());
/* 237:366 */     pad(entry.getHeaderPadCount());
/* 238:    */   }
/* 239:    */   
/* 240:    */   public void closeArchiveEntry()
/* 241:    */     throws IOException
/* 242:    */   {
/* 243:377 */     if (this.finished) {
/* 244:378 */       throw new IOException("Stream has already been finished");
/* 245:    */     }
/* 246:381 */     ensureOpen();
/* 247:383 */     if (this.entry == null) {
/* 248:384 */       throw new IOException("Trying to close non-existent entry");
/* 249:    */     }
/* 250:387 */     if (this.entry.getSize() != this.written) {
/* 251:388 */       throw new IOException("invalid entry size (expected " + this.entry.getSize() + " but got " + this.written + " bytes)");
/* 252:    */     }
/* 253:392 */     pad(this.entry.getDataPadCount());
/* 254:393 */     if ((this.entry.getFormat() == 2) && (this.crc != this.entry.getChksum())) {
/* 255:395 */       throw new IOException("CRC Error");
/* 256:    */     }
/* 257:397 */     this.entry = null;
/* 258:398 */     this.crc = 0L;
/* 259:399 */     this.written = 0L;
/* 260:    */   }
/* 261:    */   
/* 262:    */   public void write(byte[] b, int off, int len)
/* 263:    */     throws IOException
/* 264:    */   {
/* 265:419 */     ensureOpen();
/* 266:420 */     if ((off < 0) || (len < 0) || (off > b.length - len)) {
/* 267:421 */       throw new IndexOutOfBoundsException();
/* 268:    */     }
/* 269:422 */     if (len == 0) {
/* 270:423 */       return;
/* 271:    */     }
/* 272:426 */     if (this.entry == null) {
/* 273:427 */       throw new IOException("no current CPIO entry");
/* 274:    */     }
/* 275:429 */     if (this.written + len > this.entry.getSize()) {
/* 276:430 */       throw new IOException("attempt to write past end of STORED entry");
/* 277:    */     }
/* 278:432 */     this.out.write(b, off, len);
/* 279:433 */     this.written += len;
/* 280:434 */     if (this.entry.getFormat() == 2) {
/* 281:435 */       for (int pos = 0; pos < len; pos++) {
/* 282:436 */         this.crc += (b[pos] & 0xFF);
/* 283:    */       }
/* 284:    */     }
/* 285:439 */     count(len);
/* 286:    */   }
/* 287:    */   
/* 288:    */   public void finish()
/* 289:    */     throws IOException
/* 290:    */   {
/* 291:453 */     ensureOpen();
/* 292:454 */     if (this.finished) {
/* 293:455 */       throw new IOException("This archive has already been finished");
/* 294:    */     }
/* 295:458 */     if (this.entry != null) {
/* 296:459 */       throw new IOException("This archive contains unclosed entries.");
/* 297:    */     }
/* 298:461 */     this.entry = new CpioArchiveEntry(this.entryFormat);
/* 299:462 */     this.entry.setName("TRAILER!!!");
/* 300:463 */     this.entry.setNumberOfLinks(1L);
/* 301:464 */     writeHeader(this.entry);
/* 302:465 */     closeArchiveEntry();
/* 303:    */     
/* 304:467 */     int lengthOfLastBlock = (int)(getBytesWritten() % this.blockSize);
/* 305:468 */     if (lengthOfLastBlock != 0) {
/* 306:469 */       pad(this.blockSize - lengthOfLastBlock);
/* 307:    */     }
/* 308:472 */     this.finished = true;
/* 309:    */   }
/* 310:    */   
/* 311:    */   public void close()
/* 312:    */     throws IOException
/* 313:    */   {
/* 314:484 */     if (!this.finished) {
/* 315:485 */       finish();
/* 316:    */     }
/* 317:488 */     if (!this.closed)
/* 318:    */     {
/* 319:489 */       this.out.close();
/* 320:490 */       this.closed = true;
/* 321:    */     }
/* 322:    */   }
/* 323:    */   
/* 324:    */   private void pad(int count)
/* 325:    */     throws IOException
/* 326:    */   {
/* 327:495 */     if (count > 0)
/* 328:    */     {
/* 329:496 */       byte[] buff = new byte[count];
/* 330:497 */       this.out.write(buff);
/* 331:498 */       count(count);
/* 332:    */     }
/* 333:    */   }
/* 334:    */   
/* 335:    */   private void writeBinaryLong(long number, int length, boolean swapHalfWord)
/* 336:    */     throws IOException
/* 337:    */   {
/* 338:504 */     byte[] tmp = CpioUtil.long2byteArray(number, length, swapHalfWord);
/* 339:505 */     this.out.write(tmp);
/* 340:506 */     count(tmp.length);
/* 341:    */   }
/* 342:    */   
/* 343:    */   private void writeAsciiLong(long number, int length, int radix)
/* 344:    */     throws IOException
/* 345:    */   {
/* 346:511 */     StringBuilder tmp = new StringBuilder();
/* 347:513 */     if (radix == 16) {
/* 348:514 */       tmp.append(Long.toHexString(number));
/* 349:515 */     } else if (radix == 8) {
/* 350:516 */       tmp.append(Long.toOctalString(number));
/* 351:    */     } else {
/* 352:518 */       tmp.append(Long.toString(number));
/* 353:    */     }
/* 354:    */     String tmpStr;
/* 355:    */     String tmpStr;
/* 356:521 */     if (tmp.length() <= length)
/* 357:    */     {
/* 358:522 */       long insertLength = length - tmp.length();
/* 359:523 */       for (int pos = 0; pos < insertLength; pos++) {
/* 360:524 */         tmp.insert(0, "0");
/* 361:    */       }
/* 362:526 */       tmpStr = tmp.toString();
/* 363:    */     }
/* 364:    */     else
/* 365:    */     {
/* 366:528 */       tmpStr = tmp.substring(tmp.length() - length);
/* 367:    */     }
/* 368:530 */     byte[] b = ArchiveUtils.toAsciiBytes(tmpStr);
/* 369:531 */     this.out.write(b);
/* 370:532 */     count(b.length);
/* 371:    */   }
/* 372:    */   
/* 373:    */   private void writeCString(String str)
/* 374:    */     throws IOException
/* 375:    */   {
/* 376:541 */     ByteBuffer buf = this.zipEncoding.encode(str);
/* 377:542 */     int len = buf.limit() - buf.position();
/* 378:543 */     this.out.write(buf.array(), buf.arrayOffset(), len);
/* 379:544 */     this.out.write(0);
/* 380:545 */     count(len + 1);
/* 381:    */   }
/* 382:    */   
/* 383:    */   public ArchiveEntry createArchiveEntry(File inputFile, String entryName)
/* 384:    */     throws IOException
/* 385:    */   {
/* 386:556 */     if (this.finished) {
/* 387:557 */       throw new IOException("Stream has already been finished");
/* 388:    */     }
/* 389:559 */     return new CpioArchiveEntry(inputFile, entryName);
/* 390:    */   }
/* 391:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.cpio.CpioArchiveOutputStream
 * JD-Core Version:    0.7.0.1
 */