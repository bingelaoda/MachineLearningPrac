/*   1:    */ package org.apache.commons.compress.archivers.dump;
/*   2:    */ 
/*   3:    */ import java.io.EOFException;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.InputStream;
/*   6:    */ import java.util.Arrays;
/*   7:    */ import java.util.Comparator;
/*   8:    */ import java.util.HashMap;
/*   9:    */ import java.util.Map;
/*  10:    */ import java.util.Map.Entry;
/*  11:    */ import java.util.PriorityQueue;
/*  12:    */ import java.util.Queue;
/*  13:    */ import java.util.Stack;
/*  14:    */ import org.apache.commons.compress.archivers.ArchiveException;
/*  15:    */ import org.apache.commons.compress.archivers.ArchiveInputStream;
/*  16:    */ import org.apache.commons.compress.archivers.zip.ZipEncoding;
/*  17:    */ import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
/*  18:    */ 
/*  19:    */ public class DumpArchiveInputStream
/*  20:    */   extends ArchiveInputStream
/*  21:    */ {
/*  22:    */   private DumpArchiveSummary summary;
/*  23:    */   private DumpArchiveEntry active;
/*  24:    */   private boolean isClosed;
/*  25:    */   private boolean hasHitEOF;
/*  26:    */   private long entrySize;
/*  27:    */   private long entryOffset;
/*  28:    */   private int readIdx;
/*  29: 59 */   private final byte[] readBuf = new byte[1024];
/*  30:    */   private byte[] blockBuffer;
/*  31:    */   private int recordOffset;
/*  32:    */   private long filepos;
/*  33:    */   protected TapeInputStream raw;
/*  34: 66 */   private final Map<Integer, Dirent> names = new HashMap();
/*  35: 69 */   private final Map<Integer, DumpArchiveEntry> pending = new HashMap();
/*  36:    */   private Queue<DumpArchiveEntry> queue;
/*  37:    */   private final ZipEncoding zipEncoding;
/*  38:    */   final String encoding;
/*  39:    */   
/*  40:    */   public DumpArchiveInputStream(InputStream is)
/*  41:    */     throws ArchiveException
/*  42:    */   {
/*  43: 90 */     this(is, null);
/*  44:    */   }
/*  45:    */   
/*  46:    */   public DumpArchiveInputStream(InputStream is, String encoding)
/*  47:    */     throws ArchiveException
/*  48:    */   {
/*  49:104 */     this.raw = new TapeInputStream(is);
/*  50:105 */     this.hasHitEOF = false;
/*  51:106 */     this.encoding = encoding;
/*  52:107 */     this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
/*  53:    */     try
/*  54:    */     {
/*  55:111 */       byte[] headerBytes = this.raw.readRecord();
/*  56:113 */       if (!DumpArchiveUtil.verify(headerBytes)) {
/*  57:114 */         throw new UnrecognizedFormatException();
/*  58:    */       }
/*  59:118 */       this.summary = new DumpArchiveSummary(headerBytes, this.zipEncoding);
/*  60:    */       
/*  61:    */ 
/*  62:121 */       this.raw.resetBlockSize(this.summary.getNTRec(), this.summary.isCompressed());
/*  63:    */       
/*  64:    */ 
/*  65:124 */       this.blockBuffer = new byte[4096];
/*  66:    */       
/*  67:    */ 
/*  68:127 */       readCLRI();
/*  69:128 */       readBITS();
/*  70:    */     }
/*  71:    */     catch (IOException ex)
/*  72:    */     {
/*  73:130 */       throw new ArchiveException(ex.getMessage(), ex);
/*  74:    */     }
/*  75:134 */     Dirent root = new Dirent(2, 2, 4, ".");
/*  76:135 */     this.names.put(Integer.valueOf(2), root);
/*  77:    */     
/*  78:    */ 
/*  79:    */ 
/*  80:139 */     this.queue = new PriorityQueue(10, new Comparator()
/*  81:    */     {
/*  82:    */       public int compare(DumpArchiveEntry p, DumpArchiveEntry q)
/*  83:    */       {
/*  84:142 */         if ((p.getOriginalName() == null) || (q.getOriginalName() == null)) {
/*  85:143 */           return 2147483647;
/*  86:    */         }
/*  87:146 */         return p.getOriginalName().compareTo(q.getOriginalName());
/*  88:    */       }
/*  89:    */     });
/*  90:    */   }
/*  91:    */   
/*  92:    */   @Deprecated
/*  93:    */   public int getCount()
/*  94:    */   {
/*  95:154 */     return (int)getBytesRead();
/*  96:    */   }
/*  97:    */   
/*  98:    */   public long getBytesRead()
/*  99:    */   {
/* 100:159 */     return this.raw.getBytesRead();
/* 101:    */   }
/* 102:    */   
/* 103:    */   public DumpArchiveSummary getSummary()
/* 104:    */   {
/* 105:167 */     return this.summary;
/* 106:    */   }
/* 107:    */   
/* 108:    */   private void readCLRI()
/* 109:    */     throws IOException
/* 110:    */   {
/* 111:174 */     byte[] buffer = this.raw.readRecord();
/* 112:176 */     if (!DumpArchiveUtil.verify(buffer)) {
/* 113:177 */       throw new InvalidFormatException();
/* 114:    */     }
/* 115:180 */     this.active = DumpArchiveEntry.parse(buffer);
/* 116:182 */     if (DumpArchiveConstants.SEGMENT_TYPE.CLRI != this.active.getHeaderType()) {
/* 117:183 */       throw new InvalidFormatException();
/* 118:    */     }
/* 119:187 */     if (this.raw.skip(1024 * this.active.getHeaderCount()) == -1L) {
/* 120:189 */       throw new EOFException();
/* 121:    */     }
/* 122:191 */     this.readIdx = this.active.getHeaderCount();
/* 123:    */   }
/* 124:    */   
/* 125:    */   private void readBITS()
/* 126:    */     throws IOException
/* 127:    */   {
/* 128:198 */     byte[] buffer = this.raw.readRecord();
/* 129:200 */     if (!DumpArchiveUtil.verify(buffer)) {
/* 130:201 */       throw new InvalidFormatException();
/* 131:    */     }
/* 132:204 */     this.active = DumpArchiveEntry.parse(buffer);
/* 133:206 */     if (DumpArchiveConstants.SEGMENT_TYPE.BITS != this.active.getHeaderType()) {
/* 134:207 */       throw new InvalidFormatException();
/* 135:    */     }
/* 136:211 */     if (this.raw.skip(1024 * this.active.getHeaderCount()) == -1L) {
/* 137:213 */       throw new EOFException();
/* 138:    */     }
/* 139:215 */     this.readIdx = this.active.getHeaderCount();
/* 140:    */   }
/* 141:    */   
/* 142:    */   public DumpArchiveEntry getNextDumpEntry()
/* 143:    */     throws IOException
/* 144:    */   {
/* 145:224 */     return getNextEntry();
/* 146:    */   }
/* 147:    */   
/* 148:    */   public DumpArchiveEntry getNextEntry()
/* 149:    */     throws IOException
/* 150:    */   {
/* 151:229 */     DumpArchiveEntry entry = null;
/* 152:230 */     String path = null;
/* 153:233 */     if (!this.queue.isEmpty()) {
/* 154:234 */       return (DumpArchiveEntry)this.queue.remove();
/* 155:    */     }
/* 156:237 */     while (entry == null)
/* 157:    */     {
/* 158:238 */       if (this.hasHitEOF) {
/* 159:239 */         return null;
/* 160:    */       }
/* 161:246 */       while (this.readIdx < this.active.getHeaderCount()) {
/* 162:247 */         if ((!this.active.isSparseRecord(this.readIdx++)) && (this.raw.skip(1024L) == -1L)) {
/* 163:249 */           throw new EOFException();
/* 164:    */         }
/* 165:    */       }
/* 166:253 */       this.readIdx = 0;
/* 167:254 */       this.filepos = this.raw.getBytesRead();
/* 168:    */       
/* 169:256 */       byte[] headerBytes = this.raw.readRecord();
/* 170:258 */       if (!DumpArchiveUtil.verify(headerBytes)) {
/* 171:259 */         throw new InvalidFormatException();
/* 172:    */       }
/* 173:262 */       this.active = DumpArchiveEntry.parse(headerBytes);
/* 174:265 */       while (DumpArchiveConstants.SEGMENT_TYPE.ADDR == this.active.getHeaderType())
/* 175:    */       {
/* 176:266 */         if (this.raw.skip(1024 * (this.active.getHeaderCount() - this.active.getHeaderHoles())) == -1L) {
/* 177:269 */           throw new EOFException();
/* 178:    */         }
/* 179:272 */         this.filepos = this.raw.getBytesRead();
/* 180:273 */         headerBytes = this.raw.readRecord();
/* 181:275 */         if (!DumpArchiveUtil.verify(headerBytes)) {
/* 182:276 */           throw new InvalidFormatException();
/* 183:    */         }
/* 184:279 */         this.active = DumpArchiveEntry.parse(headerBytes);
/* 185:    */       }
/* 186:283 */       if (DumpArchiveConstants.SEGMENT_TYPE.END == this.active.getHeaderType())
/* 187:    */       {
/* 188:284 */         this.hasHitEOF = true;
/* 189:    */         
/* 190:286 */         return null;
/* 191:    */       }
/* 192:289 */       entry = this.active;
/* 193:291 */       if (entry.isDirectory())
/* 194:    */       {
/* 195:292 */         readDirectoryEntry(this.active);
/* 196:    */         
/* 197:    */ 
/* 198:295 */         this.entryOffset = 0L;
/* 199:296 */         this.entrySize = 0L;
/* 200:297 */         this.readIdx = this.active.getHeaderCount();
/* 201:    */       }
/* 202:    */       else
/* 203:    */       {
/* 204:299 */         this.entryOffset = 0L;
/* 205:300 */         this.entrySize = this.active.getEntrySize();
/* 206:301 */         this.readIdx = 0;
/* 207:    */       }
/* 208:304 */       this.recordOffset = this.readBuf.length;
/* 209:    */       
/* 210:306 */       path = getPath(entry);
/* 211:308 */       if (path == null) {
/* 212:309 */         entry = null;
/* 213:    */       }
/* 214:    */     }
/* 215:313 */     entry.setName(path);
/* 216:314 */     entry.setSimpleName(((Dirent)this.names.get(Integer.valueOf(entry.getIno()))).getName());
/* 217:315 */     entry.setOffset(this.filepos);
/* 218:    */     
/* 219:317 */     return entry;
/* 220:    */   }
/* 221:    */   
/* 222:    */   private void readDirectoryEntry(DumpArchiveEntry entry)
/* 223:    */     throws IOException
/* 224:    */   {
/* 225:325 */     long size = entry.getEntrySize();
/* 226:326 */     boolean first = true;
/* 227:328 */     while ((first) || (DumpArchiveConstants.SEGMENT_TYPE.ADDR == entry.getHeaderType()))
/* 228:    */     {
/* 229:331 */       if (!first) {
/* 230:332 */         this.raw.readRecord();
/* 231:    */       }
/* 232:335 */       if ((!this.names.containsKey(Integer.valueOf(entry.getIno()))) && (DumpArchiveConstants.SEGMENT_TYPE.INODE == entry.getHeaderType())) {
/* 233:337 */         this.pending.put(Integer.valueOf(entry.getIno()), entry);
/* 234:    */       }
/* 235:340 */       int datalen = 1024 * entry.getHeaderCount();
/* 236:342 */       if (this.blockBuffer.length < datalen) {
/* 237:343 */         this.blockBuffer = new byte[datalen];
/* 238:    */       }
/* 239:346 */       if (this.raw.read(this.blockBuffer, 0, datalen) != datalen) {
/* 240:347 */         throw new EOFException();
/* 241:    */       }
/* 242:350 */       int reclen = 0;
/* 243:352 */       for (int i = 0; (i < datalen - 8) && (i < size - 8L); i += reclen)
/* 244:    */       {
/* 245:354 */         int ino = DumpArchiveUtil.convert32(this.blockBuffer, i);
/* 246:355 */         reclen = DumpArchiveUtil.convert16(this.blockBuffer, i + 4);
/* 247:    */         
/* 248:357 */         byte type = this.blockBuffer[(i + 6)];
/* 249:    */         
/* 250:359 */         String name = DumpArchiveUtil.decode(this.zipEncoding, this.blockBuffer, i + 8, this.blockBuffer[(i + 7)]);
/* 251:361 */         if ((!".".equals(name)) && (!"..".equals(name)))
/* 252:    */         {
/* 253:366 */           Dirent d = new Dirent(ino, entry.getIno(), type, name);
/* 254:    */           
/* 255:    */ 
/* 256:    */ 
/* 257:    */ 
/* 258:    */ 
/* 259:    */ 
/* 260:    */ 
/* 261:    */ 
/* 262:375 */           this.names.put(Integer.valueOf(ino), d);
/* 263:378 */           for (Map.Entry<Integer, DumpArchiveEntry> e : this.pending.entrySet())
/* 264:    */           {
/* 265:379 */             String path = getPath((DumpArchiveEntry)e.getValue());
/* 266:381 */             if (path != null)
/* 267:    */             {
/* 268:382 */               ((DumpArchiveEntry)e.getValue()).setName(path);
/* 269:383 */               ((DumpArchiveEntry)e.getValue()).setSimpleName(((Dirent)this.names.get(e.getKey())).getName());
/* 270:    */               
/* 271:385 */               this.queue.add(e.getValue());
/* 272:    */             }
/* 273:    */           }
/* 274:391 */           for (DumpArchiveEntry e : this.queue) {
/* 275:392 */             this.pending.remove(Integer.valueOf(e.getIno()));
/* 276:    */           }
/* 277:    */         }
/* 278:    */       }
/* 279:396 */       byte[] peekBytes = this.raw.peek();
/* 280:398 */       if (!DumpArchiveUtil.verify(peekBytes)) {
/* 281:399 */         throw new InvalidFormatException();
/* 282:    */       }
/* 283:402 */       entry = DumpArchiveEntry.parse(peekBytes);
/* 284:403 */       first = false;
/* 285:404 */       size -= 1024L;
/* 286:    */     }
/* 287:    */   }
/* 288:    */   
/* 289:    */   private String getPath(DumpArchiveEntry entry)
/* 290:    */   {
/* 291:417 */     Stack<String> elements = new Stack();
/* 292:418 */     Dirent dirent = null;
/* 293:420 */     for (int i = entry.getIno();; i = dirent.getParentIno()) {
/* 294:421 */       if (!this.names.containsKey(Integer.valueOf(i)))
/* 295:    */       {
/* 296:422 */         elements.clear();
/* 297:    */       }
/* 298:    */       else
/* 299:    */       {
/* 300:426 */         dirent = (Dirent)this.names.get(Integer.valueOf(i));
/* 301:427 */         elements.push(dirent.getName());
/* 302:429 */         if (dirent.getIno() == dirent.getParentIno()) {
/* 303:    */           break;
/* 304:    */         }
/* 305:    */       }
/* 306:    */     }
/* 307:435 */     if (elements.isEmpty())
/* 308:    */     {
/* 309:436 */       this.pending.put(Integer.valueOf(entry.getIno()), entry);
/* 310:    */       
/* 311:438 */       return null;
/* 312:    */     }
/* 313:442 */     StringBuilder sb = new StringBuilder((String)elements.pop());
/* 314:444 */     while (!elements.isEmpty())
/* 315:    */     {
/* 316:445 */       sb.append('/');
/* 317:446 */       sb.append((String)elements.pop());
/* 318:    */     }
/* 319:449 */     return sb.toString();
/* 320:    */   }
/* 321:    */   
/* 322:    */   public int read(byte[] buf, int off, int len)
/* 323:    */     throws IOException
/* 324:    */   {
/* 325:467 */     int totalRead = 0;
/* 326:469 */     if ((this.hasHitEOF) || (this.isClosed) || (this.entryOffset >= this.entrySize)) {
/* 327:470 */       return -1;
/* 328:    */     }
/* 329:473 */     if (this.active == null) {
/* 330:474 */       throw new IllegalStateException("No current dump entry");
/* 331:    */     }
/* 332:477 */     if (len + this.entryOffset > this.entrySize) {
/* 333:478 */       len = (int)(this.entrySize - this.entryOffset);
/* 334:    */     }
/* 335:481 */     while (len > 0)
/* 336:    */     {
/* 337:482 */       int sz = len > this.readBuf.length - this.recordOffset ? this.readBuf.length - this.recordOffset : len;
/* 338:486 */       if (this.recordOffset + sz <= this.readBuf.length)
/* 339:    */       {
/* 340:487 */         System.arraycopy(this.readBuf, this.recordOffset, buf, off, sz);
/* 341:488 */         totalRead += sz;
/* 342:489 */         this.recordOffset += sz;
/* 343:490 */         len -= sz;
/* 344:491 */         off += sz;
/* 345:    */       }
/* 346:495 */       if (len > 0)
/* 347:    */       {
/* 348:496 */         if (this.readIdx >= 512)
/* 349:    */         {
/* 350:497 */           byte[] headerBytes = this.raw.readRecord();
/* 351:499 */           if (!DumpArchiveUtil.verify(headerBytes)) {
/* 352:500 */             throw new InvalidFormatException();
/* 353:    */           }
/* 354:503 */           this.active = DumpArchiveEntry.parse(headerBytes);
/* 355:504 */           this.readIdx = 0;
/* 356:    */         }
/* 357:507 */         if (!this.active.isSparseRecord(this.readIdx++))
/* 358:    */         {
/* 359:508 */           int r = this.raw.read(this.readBuf, 0, this.readBuf.length);
/* 360:509 */           if (r != this.readBuf.length) {
/* 361:510 */             throw new EOFException();
/* 362:    */           }
/* 363:    */         }
/* 364:    */         else
/* 365:    */         {
/* 366:513 */           Arrays.fill(this.readBuf, (byte)0);
/* 367:    */         }
/* 368:516 */         this.recordOffset = 0;
/* 369:    */       }
/* 370:    */     }
/* 371:520 */     this.entryOffset += totalRead;
/* 372:    */     
/* 373:522 */     return totalRead;
/* 374:    */   }
/* 375:    */   
/* 376:    */   public void close()
/* 377:    */     throws IOException
/* 378:    */   {
/* 379:530 */     if (!this.isClosed)
/* 380:    */     {
/* 381:531 */       this.isClosed = true;
/* 382:532 */       this.raw.close();
/* 383:    */     }
/* 384:    */   }
/* 385:    */   
/* 386:    */   public static boolean matches(byte[] buffer, int length)
/* 387:    */   {
/* 388:546 */     if (length < 32) {
/* 389:547 */       return false;
/* 390:    */     }
/* 391:551 */     if (length >= 1024) {
/* 392:552 */       return DumpArchiveUtil.verify(buffer);
/* 393:    */     }
/* 394:556 */     return 60012 == DumpArchiveUtil.convert32(buffer, 24);
/* 395:    */   }
/* 396:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.dump.DumpArchiveInputStream
 * JD-Core Version:    0.7.0.1
 */