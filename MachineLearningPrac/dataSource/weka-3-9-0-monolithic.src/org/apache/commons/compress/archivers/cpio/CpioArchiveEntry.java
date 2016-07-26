/*   1:    */ package org.apache.commons.compress.archivers.cpio;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.util.Date;
/*   5:    */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*   6:    */ 
/*   7:    */ public class CpioArchiveEntry
/*   8:    */   implements CpioConstants, ArchiveEntry
/*   9:    */ {
/*  10:    */   private final short fileFormat;
/*  11:    */   private final int headerSize;
/*  12:    */   private final int alignmentBoundary;
/*  13:163 */   private long chksum = 0L;
/*  14:166 */   private long filesize = 0L;
/*  15:168 */   private long gid = 0L;
/*  16:170 */   private long inode = 0L;
/*  17:172 */   private long maj = 0L;
/*  18:174 */   private long min = 0L;
/*  19:176 */   private long mode = 0L;
/*  20:178 */   private long mtime = 0L;
/*  21:    */   private String name;
/*  22:182 */   private long nlink = 0L;
/*  23:184 */   private long rmaj = 0L;
/*  24:186 */   private long rmin = 0L;
/*  25:188 */   private long uid = 0L;
/*  26:    */   
/*  27:    */   public CpioArchiveEntry(short format)
/*  28:    */   {
/*  29:205 */     switch (format)
/*  30:    */     {
/*  31:    */     case 1: 
/*  32:207 */       this.headerSize = 110;
/*  33:208 */       this.alignmentBoundary = 4;
/*  34:209 */       break;
/*  35:    */     case 2: 
/*  36:211 */       this.headerSize = 110;
/*  37:212 */       this.alignmentBoundary = 4;
/*  38:213 */       break;
/*  39:    */     case 4: 
/*  40:215 */       this.headerSize = 76;
/*  41:216 */       this.alignmentBoundary = 0;
/*  42:217 */       break;
/*  43:    */     case 8: 
/*  44:219 */       this.headerSize = 26;
/*  45:220 */       this.alignmentBoundary = 2;
/*  46:221 */       break;
/*  47:    */     case 3: 
/*  48:    */     case 5: 
/*  49:    */     case 6: 
/*  50:    */     case 7: 
/*  51:    */     default: 
/*  52:223 */       throw new IllegalArgumentException("Unknown header type");
/*  53:    */     }
/*  54:225 */     this.fileFormat = format;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public CpioArchiveEntry(String name)
/*  58:    */   {
/*  59:236 */     this((short)1, name);
/*  60:    */   }
/*  61:    */   
/*  62:    */   public CpioArchiveEntry(short format, String name)
/*  63:    */   {
/*  64:258 */     this(format);
/*  65:259 */     this.name = name;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public CpioArchiveEntry(String name, long size)
/*  69:    */   {
/*  70:272 */     this(name);
/*  71:273 */     setSize(size);
/*  72:    */   }
/*  73:    */   
/*  74:    */   public CpioArchiveEntry(short format, String name, long size)
/*  75:    */   {
/*  76:298 */     this(format, name);
/*  77:299 */     setSize(size);
/*  78:    */   }
/*  79:    */   
/*  80:    */   public CpioArchiveEntry(File inputFile, String entryName)
/*  81:    */   {
/*  82:313 */     this((short)1, inputFile, entryName);
/*  83:    */   }
/*  84:    */   
/*  85:    */   public CpioArchiveEntry(short format, File inputFile, String entryName)
/*  86:    */   {
/*  87:339 */     this(format, entryName, inputFile.isFile() ? inputFile.length() : 0L);
/*  88:340 */     if (inputFile.isDirectory()) {
/*  89:341 */       setMode(16384L);
/*  90:342 */     } else if (inputFile.isFile()) {
/*  91:343 */       setMode(32768L);
/*  92:    */     } else {
/*  93:345 */       throw new IllegalArgumentException("Cannot determine type of file " + inputFile.getName());
/*  94:    */     }
/*  95:349 */     setTime(inputFile.lastModified() / 1000L);
/*  96:    */   }
/*  97:    */   
/*  98:    */   private void checkNewFormat()
/*  99:    */   {
/* 100:356 */     if ((this.fileFormat & 0x3) == 0) {
/* 101:357 */       throw new UnsupportedOperationException();
/* 102:    */     }
/* 103:    */   }
/* 104:    */   
/* 105:    */   private void checkOldFormat()
/* 106:    */   {
/* 107:365 */     if ((this.fileFormat & 0xC) == 0) {
/* 108:366 */       throw new UnsupportedOperationException();
/* 109:    */     }
/* 110:    */   }
/* 111:    */   
/* 112:    */   public long getChksum()
/* 113:    */   {
/* 114:378 */     checkNewFormat();
/* 115:379 */     return this.chksum;
/* 116:    */   }
/* 117:    */   
/* 118:    */   public long getDevice()
/* 119:    */   {
/* 120:391 */     checkOldFormat();
/* 121:392 */     return this.min;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public long getDeviceMaj()
/* 125:    */   {
/* 126:404 */     checkNewFormat();
/* 127:405 */     return this.maj;
/* 128:    */   }
/* 129:    */   
/* 130:    */   public long getDeviceMin()
/* 131:    */   {
/* 132:415 */     checkNewFormat();
/* 133:416 */     return this.min;
/* 134:    */   }
/* 135:    */   
/* 136:    */   public long getSize()
/* 137:    */   {
/* 138:426 */     return this.filesize;
/* 139:    */   }
/* 140:    */   
/* 141:    */   public short getFormat()
/* 142:    */   {
/* 143:435 */     return this.fileFormat;
/* 144:    */   }
/* 145:    */   
/* 146:    */   public long getGID()
/* 147:    */   {
/* 148:444 */     return this.gid;
/* 149:    */   }
/* 150:    */   
/* 151:    */   public int getHeaderSize()
/* 152:    */   {
/* 153:453 */     return this.headerSize;
/* 154:    */   }
/* 155:    */   
/* 156:    */   public int getAlignmentBoundary()
/* 157:    */   {
/* 158:462 */     return this.alignmentBoundary;
/* 159:    */   }
/* 160:    */   
/* 161:    */   public int getHeaderPadCount()
/* 162:    */   {
/* 163:471 */     if (this.alignmentBoundary == 0) {
/* 164:471 */       return 0;
/* 165:    */     }
/* 166:472 */     int size = this.headerSize + 1;
/* 167:473 */     if (this.name != null) {
/* 168:474 */       size += this.name.length();
/* 169:    */     }
/* 170:476 */     int remain = size % this.alignmentBoundary;
/* 171:477 */     if (remain > 0) {
/* 172:478 */       return this.alignmentBoundary - remain;
/* 173:    */     }
/* 174:480 */     return 0;
/* 175:    */   }
/* 176:    */   
/* 177:    */   public int getDataPadCount()
/* 178:    */   {
/* 179:489 */     if (this.alignmentBoundary == 0) {
/* 180:489 */       return 0;
/* 181:    */     }
/* 182:490 */     long size = this.filesize;
/* 183:491 */     int remain = (int)(size % this.alignmentBoundary);
/* 184:492 */     if (remain > 0) {
/* 185:493 */       return this.alignmentBoundary - remain;
/* 186:    */     }
/* 187:495 */     return 0;
/* 188:    */   }
/* 189:    */   
/* 190:    */   public long getInode()
/* 191:    */   {
/* 192:504 */     return this.inode;
/* 193:    */   }
/* 194:    */   
/* 195:    */   public long getMode()
/* 196:    */   {
/* 197:513 */     return (this.mode == 0L) && (!"TRAILER!!!".equals(this.name)) ? 32768L : this.mode;
/* 198:    */   }
/* 199:    */   
/* 200:    */   public String getName()
/* 201:    */   {
/* 202:522 */     return this.name;
/* 203:    */   }
/* 204:    */   
/* 205:    */   public long getNumberOfLinks()
/* 206:    */   {
/* 207:531 */     return this.nlink == 0L ? 1L : isDirectory() ? 2L : this.nlink;
/* 208:    */   }
/* 209:    */   
/* 210:    */   public long getRemoteDevice()
/* 211:    */   {
/* 212:545 */     checkOldFormat();
/* 213:546 */     return this.rmin;
/* 214:    */   }
/* 215:    */   
/* 216:    */   public long getRemoteDeviceMaj()
/* 217:    */   {
/* 218:558 */     checkNewFormat();
/* 219:559 */     return this.rmaj;
/* 220:    */   }
/* 221:    */   
/* 222:    */   public long getRemoteDeviceMin()
/* 223:    */   {
/* 224:571 */     checkNewFormat();
/* 225:572 */     return this.rmin;
/* 226:    */   }
/* 227:    */   
/* 228:    */   public long getTime()
/* 229:    */   {
/* 230:581 */     return this.mtime;
/* 231:    */   }
/* 232:    */   
/* 233:    */   public Date getLastModifiedDate()
/* 234:    */   {
/* 235:585 */     return new Date(1000L * getTime());
/* 236:    */   }
/* 237:    */   
/* 238:    */   public long getUID()
/* 239:    */   {
/* 240:594 */     return this.uid;
/* 241:    */   }
/* 242:    */   
/* 243:    */   public boolean isBlockDevice()
/* 244:    */   {
/* 245:603 */     return CpioUtil.fileType(this.mode) == 24576L;
/* 246:    */   }
/* 247:    */   
/* 248:    */   public boolean isCharacterDevice()
/* 249:    */   {
/* 250:612 */     return CpioUtil.fileType(this.mode) == 8192L;
/* 251:    */   }
/* 252:    */   
/* 253:    */   public boolean isDirectory()
/* 254:    */   {
/* 255:621 */     return CpioUtil.fileType(this.mode) == 16384L;
/* 256:    */   }
/* 257:    */   
/* 258:    */   public boolean isNetwork()
/* 259:    */   {
/* 260:630 */     return CpioUtil.fileType(this.mode) == 36864L;
/* 261:    */   }
/* 262:    */   
/* 263:    */   public boolean isPipe()
/* 264:    */   {
/* 265:639 */     return CpioUtil.fileType(this.mode) == 4096L;
/* 266:    */   }
/* 267:    */   
/* 268:    */   public boolean isRegularFile()
/* 269:    */   {
/* 270:648 */     return CpioUtil.fileType(this.mode) == 32768L;
/* 271:    */   }
/* 272:    */   
/* 273:    */   public boolean isSocket()
/* 274:    */   {
/* 275:657 */     return CpioUtil.fileType(this.mode) == 49152L;
/* 276:    */   }
/* 277:    */   
/* 278:    */   public boolean isSymbolicLink()
/* 279:    */   {
/* 280:666 */     return CpioUtil.fileType(this.mode) == 40960L;
/* 281:    */   }
/* 282:    */   
/* 283:    */   public void setChksum(long chksum)
/* 284:    */   {
/* 285:677 */     checkNewFormat();
/* 286:678 */     this.chksum = chksum;
/* 287:    */   }
/* 288:    */   
/* 289:    */   public void setDevice(long device)
/* 290:    */   {
/* 291:691 */     checkOldFormat();
/* 292:692 */     this.min = device;
/* 293:    */   }
/* 294:    */   
/* 295:    */   public void setDeviceMaj(long maj)
/* 296:    */   {
/* 297:702 */     checkNewFormat();
/* 298:703 */     this.maj = maj;
/* 299:    */   }
/* 300:    */   
/* 301:    */   public void setDeviceMin(long min)
/* 302:    */   {
/* 303:713 */     checkNewFormat();
/* 304:714 */     this.min = min;
/* 305:    */   }
/* 306:    */   
/* 307:    */   public void setSize(long size)
/* 308:    */   {
/* 309:724 */     if ((size < 0L) || (size > 4294967295L)) {
/* 310:725 */       throw new IllegalArgumentException("invalid entry size <" + size + ">");
/* 311:    */     }
/* 312:728 */     this.filesize = size;
/* 313:    */   }
/* 314:    */   
/* 315:    */   public void setGID(long gid)
/* 316:    */   {
/* 317:738 */     this.gid = gid;
/* 318:    */   }
/* 319:    */   
/* 320:    */   public void setInode(long inode)
/* 321:    */   {
/* 322:748 */     this.inode = inode;
/* 323:    */   }
/* 324:    */   
/* 325:    */   public void setMode(long mode)
/* 326:    */   {
/* 327:758 */     long maskedMode = mode & 0xF000;
/* 328:759 */     switch ((int)maskedMode)
/* 329:    */     {
/* 330:    */     case 4096: 
/* 331:    */     case 8192: 
/* 332:    */     case 16384: 
/* 333:    */     case 24576: 
/* 334:    */     case 32768: 
/* 335:    */     case 36864: 
/* 336:    */     case 40960: 
/* 337:    */     case 49152: 
/* 338:    */       break;
/* 339:    */     default: 
/* 340:770 */       throw new IllegalArgumentException("Unknown mode. Full: " + Long.toHexString(mode) + " Masked: " + Long.toHexString(maskedMode));
/* 341:    */     }
/* 342:776 */     this.mode = mode;
/* 343:    */   }
/* 344:    */   
/* 345:    */   public void setName(String name)
/* 346:    */   {
/* 347:786 */     this.name = name;
/* 348:    */   }
/* 349:    */   
/* 350:    */   public void setNumberOfLinks(long nlink)
/* 351:    */   {
/* 352:796 */     this.nlink = nlink;
/* 353:    */   }
/* 354:    */   
/* 355:    */   public void setRemoteDevice(long device)
/* 356:    */   {
/* 357:809 */     checkOldFormat();
/* 358:810 */     this.rmin = device;
/* 359:    */   }
/* 360:    */   
/* 361:    */   public void setRemoteDeviceMaj(long rmaj)
/* 362:    */   {
/* 363:823 */     checkNewFormat();
/* 364:824 */     this.rmaj = rmaj;
/* 365:    */   }
/* 366:    */   
/* 367:    */   public void setRemoteDeviceMin(long rmin)
/* 368:    */   {
/* 369:837 */     checkNewFormat();
/* 370:838 */     this.rmin = rmin;
/* 371:    */   }
/* 372:    */   
/* 373:    */   public void setTime(long time)
/* 374:    */   {
/* 375:848 */     this.mtime = time;
/* 376:    */   }
/* 377:    */   
/* 378:    */   public void setUID(long uid)
/* 379:    */   {
/* 380:858 */     this.uid = uid;
/* 381:    */   }
/* 382:    */   
/* 383:    */   public int hashCode()
/* 384:    */   {
/* 385:866 */     int prime = 31;
/* 386:867 */     int result = 1;
/* 387:868 */     result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
/* 388:869 */     return result;
/* 389:    */   }
/* 390:    */   
/* 391:    */   public boolean equals(Object obj)
/* 392:    */   {
/* 393:877 */     if (this == obj) {
/* 394:878 */       return true;
/* 395:    */     }
/* 396:880 */     if ((obj == null) || (getClass() != obj.getClass())) {
/* 397:881 */       return false;
/* 398:    */     }
/* 399:883 */     CpioArchiveEntry other = (CpioArchiveEntry)obj;
/* 400:884 */     if (this.name == null)
/* 401:    */     {
/* 402:885 */       if (other.name != null) {
/* 403:886 */         return false;
/* 404:    */       }
/* 405:    */     }
/* 406:888 */     else if (!this.name.equals(other.name)) {
/* 407:889 */       return false;
/* 408:    */     }
/* 409:891 */     return true;
/* 410:    */   }
/* 411:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.cpio.CpioArchiveEntry
 * JD-Core Version:    0.7.0.1
 */