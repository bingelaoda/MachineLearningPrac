/*   1:    */ package org.apache.commons.compress.archivers.dump;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Date;
/*   5:    */ import java.util.EnumSet;
/*   6:    */ import java.util.HashSet;
/*   7:    */ import java.util.Set;
/*   8:    */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*   9:    */ 
/*  10:    */ public class DumpArchiveEntry
/*  11:    */   implements ArchiveEntry
/*  12:    */ {
/*  13:    */   private String name;
/*  14:183 */   private TYPE type = TYPE.UNKNOWN;
/*  15:    */   private int mode;
/*  16:185 */   private Set<PERMISSION> permissions = Collections.emptySet();
/*  17:    */   private long size;
/*  18:    */   private long atime;
/*  19:    */   private long mtime;
/*  20:    */   private int uid;
/*  21:    */   private int gid;
/*  22:195 */   private final DumpArchiveSummary summary = null;
/*  23:198 */   private final TapeSegmentHeader header = new TapeSegmentHeader();
/*  24:    */   private String simpleName;
/*  25:    */   private String originalName;
/*  26:    */   private int volume;
/*  27:    */   private long offset;
/*  28:    */   private int ino;
/*  29:    */   private int nlink;
/*  30:    */   private long ctime;
/*  31:    */   private int generation;
/*  32:    */   private boolean isDeleted;
/*  33:    */   
/*  34:    */   public DumpArchiveEntry() {}
/*  35:    */   
/*  36:    */   public DumpArchiveEntry(String name, String simpleName)
/*  37:    */   {
/*  38:223 */     setName(name);
/*  39:224 */     this.simpleName = simpleName;
/*  40:    */   }
/*  41:    */   
/*  42:    */   protected DumpArchiveEntry(String name, String simpleName, int ino, TYPE type)
/*  43:    */   {
/*  44:237 */     setType(type);
/*  45:238 */     setName(name);
/*  46:239 */     this.simpleName = simpleName;
/*  47:240 */     this.ino = ino;
/*  48:241 */     this.offset = 0L;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public String getSimpleName()
/*  52:    */   {
/*  53:249 */     return this.simpleName;
/*  54:    */   }
/*  55:    */   
/*  56:    */   protected void setSimpleName(String simpleName)
/*  57:    */   {
/*  58:257 */     this.simpleName = simpleName;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public int getIno()
/*  62:    */   {
/*  63:265 */     return this.header.getIno();
/*  64:    */   }
/*  65:    */   
/*  66:    */   public int getNlink()
/*  67:    */   {
/*  68:273 */     return this.nlink;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void setNlink(int nlink)
/*  72:    */   {
/*  73:281 */     this.nlink = nlink;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public Date getCreationTime()
/*  77:    */   {
/*  78:289 */     return new Date(this.ctime);
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void setCreationTime(Date ctime)
/*  82:    */   {
/*  83:297 */     this.ctime = ctime.getTime();
/*  84:    */   }
/*  85:    */   
/*  86:    */   public int getGeneration()
/*  87:    */   {
/*  88:305 */     return this.generation;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public void setGeneration(int generation)
/*  92:    */   {
/*  93:313 */     this.generation = generation;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public boolean isDeleted()
/*  97:    */   {
/*  98:321 */     return this.isDeleted;
/*  99:    */   }
/* 100:    */   
/* 101:    */   public void setDeleted(boolean isDeleted)
/* 102:    */   {
/* 103:329 */     this.isDeleted = isDeleted;
/* 104:    */   }
/* 105:    */   
/* 106:    */   public long getOffset()
/* 107:    */   {
/* 108:337 */     return this.offset;
/* 109:    */   }
/* 110:    */   
/* 111:    */   public void setOffset(long offset)
/* 112:    */   {
/* 113:345 */     this.offset = offset;
/* 114:    */   }
/* 115:    */   
/* 116:    */   public int getVolume()
/* 117:    */   {
/* 118:353 */     return this.volume;
/* 119:    */   }
/* 120:    */   
/* 121:    */   public void setVolume(int volume)
/* 122:    */   {
/* 123:361 */     this.volume = volume;
/* 124:    */   }
/* 125:    */   
/* 126:    */   public DumpArchiveConstants.SEGMENT_TYPE getHeaderType()
/* 127:    */   {
/* 128:369 */     return this.header.getType();
/* 129:    */   }
/* 130:    */   
/* 131:    */   public int getHeaderCount()
/* 132:    */   {
/* 133:377 */     return this.header.getCount();
/* 134:    */   }
/* 135:    */   
/* 136:    */   public int getHeaderHoles()
/* 137:    */   {
/* 138:385 */     return this.header.getHoles();
/* 139:    */   }
/* 140:    */   
/* 141:    */   public boolean isSparseRecord(int idx)
/* 142:    */   {
/* 143:394 */     return (this.header.getCdata(idx) & 0x1) == 0;
/* 144:    */   }
/* 145:    */   
/* 146:    */   public int hashCode()
/* 147:    */   {
/* 148:399 */     return this.ino;
/* 149:    */   }
/* 150:    */   
/* 151:    */   public boolean equals(Object o)
/* 152:    */   {
/* 153:404 */     if (o == this) {
/* 154:405 */       return true;
/* 155:    */     }
/* 156:406 */     if ((o == null) || (!o.getClass().equals(getClass()))) {
/* 157:407 */       return false;
/* 158:    */     }
/* 159:410 */     DumpArchiveEntry rhs = (DumpArchiveEntry)o;
/* 160:412 */     if ((this.header == null) || (rhs.header == null)) {
/* 161:413 */       return false;
/* 162:    */     }
/* 163:416 */     if (this.ino != rhs.ino) {
/* 164:417 */       return false;
/* 165:    */     }
/* 166:420 */     if (((this.summary == null) && (rhs.summary != null)) || ((this.summary != null) && (!this.summary.equals(rhs.summary)))) {
/* 167:422 */       return false;
/* 168:    */     }
/* 169:425 */     return true;
/* 170:    */   }
/* 171:    */   
/* 172:    */   public String toString()
/* 173:    */   {
/* 174:430 */     return getName();
/* 175:    */   }
/* 176:    */   
/* 177:    */   static DumpArchiveEntry parse(byte[] buffer)
/* 178:    */   {
/* 179:440 */     DumpArchiveEntry entry = new DumpArchiveEntry();
/* 180:441 */     TapeSegmentHeader header = entry.header;
/* 181:    */     
/* 182:443 */     header.type = DumpArchiveConstants.SEGMENT_TYPE.find(DumpArchiveUtil.convert32(buffer, 0));
/* 183:    */     
/* 184:    */ 
/* 185:    */ 
/* 186:    */ 
/* 187:    */ 
/* 188:449 */     header.volume = DumpArchiveUtil.convert32(buffer, 12);
/* 189:    */     
/* 190:451 */     entry.ino = header.ino = DumpArchiveUtil.convert32(buffer, 20);
/* 191:    */     
/* 192:    */ 
/* 193:    */ 
/* 194:455 */     int m = DumpArchiveUtil.convert16(buffer, 32);
/* 195:    */     
/* 196:    */ 
/* 197:458 */     entry.setType(TYPE.find(m >> 12 & 0xF));
/* 198:    */     
/* 199:    */ 
/* 200:461 */     entry.setMode(m);
/* 201:    */     
/* 202:463 */     entry.nlink = DumpArchiveUtil.convert16(buffer, 34);
/* 203:    */     
/* 204:465 */     entry.setSize(DumpArchiveUtil.convert64(buffer, 40));
/* 205:    */     
/* 206:467 */     long t = 1000L * DumpArchiveUtil.convert32(buffer, 48) + DumpArchiveUtil.convert32(buffer, 52) / 1000;
/* 207:    */     
/* 208:469 */     entry.setAccessTime(new Date(t));
/* 209:470 */     t = 1000L * DumpArchiveUtil.convert32(buffer, 56) + DumpArchiveUtil.convert32(buffer, 60) / 1000;
/* 210:    */     
/* 211:472 */     entry.setLastModifiedDate(new Date(t));
/* 212:473 */     t = 1000L * DumpArchiveUtil.convert32(buffer, 64) + DumpArchiveUtil.convert32(buffer, 68) / 1000;
/* 213:    */     
/* 214:475 */     entry.ctime = t;
/* 215:    */     
/* 216:    */ 
/* 217:    */ 
/* 218:    */ 
/* 219:    */ 
/* 220:481 */     entry.generation = DumpArchiveUtil.convert32(buffer, 140);
/* 221:482 */     entry.setUserId(DumpArchiveUtil.convert32(buffer, 144));
/* 222:483 */     entry.setGroupId(DumpArchiveUtil.convert32(buffer, 148));
/* 223:    */     
/* 224:485 */     header.count = DumpArchiveUtil.convert32(buffer, 160);
/* 225:    */     
/* 226:487 */     header.holes = 0;
/* 227:489 */     for (int i = 0; (i < 512) && (i < header.count); i++) {
/* 228:490 */       if (buffer[(164 + i)] == 0) {
/* 229:491 */         TapeSegmentHeader.access$408(header);
/* 230:    */       }
/* 231:    */     }
/* 232:495 */     System.arraycopy(buffer, 164, header.cdata, 0, 512);
/* 233:    */     
/* 234:497 */     entry.volume = header.getVolume();
/* 235:    */     
/* 236:    */ 
/* 237:500 */     return entry;
/* 238:    */   }
/* 239:    */   
/* 240:    */   void update(byte[] buffer)
/* 241:    */   {
/* 242:507 */     this.header.volume = DumpArchiveUtil.convert32(buffer, 16);
/* 243:508 */     this.header.count = DumpArchiveUtil.convert32(buffer, 160);
/* 244:    */     
/* 245:510 */     this.header.holes = 0;
/* 246:512 */     for (int i = 0; (i < 512) && (i < this.header.count); i++) {
/* 247:513 */       if (buffer[(164 + i)] == 0) {
/* 248:514 */         TapeSegmentHeader.access$408(this.header);
/* 249:    */       }
/* 250:    */     }
/* 251:518 */     System.arraycopy(buffer, 164, this.header.cdata, 0, 512);
/* 252:    */   }
/* 253:    */   
/* 254:    */   static class TapeSegmentHeader
/* 255:    */   {
/* 256:    */     private DumpArchiveConstants.SEGMENT_TYPE type;
/* 257:    */     private int volume;
/* 258:    */     private int ino;
/* 259:    */     private int count;
/* 260:    */     private int holes;
/* 261:531 */     private final byte[] cdata = new byte[512];
/* 262:    */     
/* 263:    */     public DumpArchiveConstants.SEGMENT_TYPE getType()
/* 264:    */     {
/* 265:534 */       return this.type;
/* 266:    */     }
/* 267:    */     
/* 268:    */     public int getVolume()
/* 269:    */     {
/* 270:538 */       return this.volume;
/* 271:    */     }
/* 272:    */     
/* 273:    */     public int getIno()
/* 274:    */     {
/* 275:542 */       return this.ino;
/* 276:    */     }
/* 277:    */     
/* 278:    */     void setIno(int ino)
/* 279:    */     {
/* 280:546 */       this.ino = ino;
/* 281:    */     }
/* 282:    */     
/* 283:    */     public int getCount()
/* 284:    */     {
/* 285:550 */       return this.count;
/* 286:    */     }
/* 287:    */     
/* 288:    */     public int getHoles()
/* 289:    */     {
/* 290:554 */       return this.holes;
/* 291:    */     }
/* 292:    */     
/* 293:    */     public int getCdata(int idx)
/* 294:    */     {
/* 295:558 */       return this.cdata[idx];
/* 296:    */     }
/* 297:    */   }
/* 298:    */   
/* 299:    */   public String getName()
/* 300:    */   {
/* 301:567 */     return this.name;
/* 302:    */   }
/* 303:    */   
/* 304:    */   String getOriginalName()
/* 305:    */   {
/* 306:575 */     return this.originalName;
/* 307:    */   }
/* 308:    */   
/* 309:    */   public final void setName(String name)
/* 310:    */   {
/* 311:583 */     this.originalName = name;
/* 312:584 */     if (name != null)
/* 313:    */     {
/* 314:585 */       if ((isDirectory()) && (!name.endsWith("/"))) {
/* 315:586 */         name = name + "/";
/* 316:    */       }
/* 317:588 */       if (name.startsWith("./")) {
/* 318:589 */         name = name.substring(2);
/* 319:    */       }
/* 320:    */     }
/* 321:592 */     this.name = name;
/* 322:    */   }
/* 323:    */   
/* 324:    */   public Date getLastModifiedDate()
/* 325:    */   {
/* 326:600 */     return new Date(this.mtime);
/* 327:    */   }
/* 328:    */   
/* 329:    */   public boolean isDirectory()
/* 330:    */   {
/* 331:608 */     return this.type == TYPE.DIRECTORY;
/* 332:    */   }
/* 333:    */   
/* 334:    */   public boolean isFile()
/* 335:    */   {
/* 336:616 */     return this.type == TYPE.FILE;
/* 337:    */   }
/* 338:    */   
/* 339:    */   public boolean isSocket()
/* 340:    */   {
/* 341:624 */     return this.type == TYPE.SOCKET;
/* 342:    */   }
/* 343:    */   
/* 344:    */   public boolean isChrDev()
/* 345:    */   {
/* 346:632 */     return this.type == TYPE.CHRDEV;
/* 347:    */   }
/* 348:    */   
/* 349:    */   public boolean isBlkDev()
/* 350:    */   {
/* 351:640 */     return this.type == TYPE.BLKDEV;
/* 352:    */   }
/* 353:    */   
/* 354:    */   public boolean isFifo()
/* 355:    */   {
/* 356:648 */     return this.type == TYPE.FIFO;
/* 357:    */   }
/* 358:    */   
/* 359:    */   public TYPE getType()
/* 360:    */   {
/* 361:656 */     return this.type;
/* 362:    */   }
/* 363:    */   
/* 364:    */   public void setType(TYPE type)
/* 365:    */   {
/* 366:664 */     this.type = type;
/* 367:    */   }
/* 368:    */   
/* 369:    */   public int getMode()
/* 370:    */   {
/* 371:672 */     return this.mode;
/* 372:    */   }
/* 373:    */   
/* 374:    */   public void setMode(int mode)
/* 375:    */   {
/* 376:680 */     this.mode = (mode & 0xFFF);
/* 377:681 */     this.permissions = PERMISSION.find(mode);
/* 378:    */   }
/* 379:    */   
/* 380:    */   public Set<PERMISSION> getPermissions()
/* 381:    */   {
/* 382:689 */     return this.permissions;
/* 383:    */   }
/* 384:    */   
/* 385:    */   public long getSize()
/* 386:    */   {
/* 387:697 */     return isDirectory() ? -1L : this.size;
/* 388:    */   }
/* 389:    */   
/* 390:    */   long getEntrySize()
/* 391:    */   {
/* 392:704 */     return this.size;
/* 393:    */   }
/* 394:    */   
/* 395:    */   public void setSize(long size)
/* 396:    */   {
/* 397:712 */     this.size = size;
/* 398:    */   }
/* 399:    */   
/* 400:    */   public void setLastModifiedDate(Date mtime)
/* 401:    */   {
/* 402:720 */     this.mtime = mtime.getTime();
/* 403:    */   }
/* 404:    */   
/* 405:    */   public Date getAccessTime()
/* 406:    */   {
/* 407:728 */     return new Date(this.atime);
/* 408:    */   }
/* 409:    */   
/* 410:    */   public void setAccessTime(Date atime)
/* 411:    */   {
/* 412:736 */     this.atime = atime.getTime();
/* 413:    */   }
/* 414:    */   
/* 415:    */   public int getUserId()
/* 416:    */   {
/* 417:744 */     return this.uid;
/* 418:    */   }
/* 419:    */   
/* 420:    */   public void setUserId(int uid)
/* 421:    */   {
/* 422:752 */     this.uid = uid;
/* 423:    */   }
/* 424:    */   
/* 425:    */   public int getGroupId()
/* 426:    */   {
/* 427:760 */     return this.gid;
/* 428:    */   }
/* 429:    */   
/* 430:    */   public void setGroupId(int gid)
/* 431:    */   {
/* 432:768 */     this.gid = gid;
/* 433:    */   }
/* 434:    */   
/* 435:    */   public static enum TYPE
/* 436:    */   {
/* 437:772 */     WHITEOUT(14),  SOCKET(12),  LINK(10),  FILE(8),  BLKDEV(6),  DIRECTORY(4),  CHRDEV(2),  FIFO(1),  UNKNOWN(15);
/* 438:    */     
/* 439:    */     private int code;
/* 440:    */     
/* 441:    */     private TYPE(int code)
/* 442:    */     {
/* 443:785 */       this.code = code;
/* 444:    */     }
/* 445:    */     
/* 446:    */     public static TYPE find(int code)
/* 447:    */     {
/* 448:789 */       TYPE type = UNKNOWN;
/* 449:791 */       for (TYPE t : values()) {
/* 450:792 */         if (code == t.code) {
/* 451:793 */           type = t;
/* 452:    */         }
/* 453:    */       }
/* 454:797 */       return type;
/* 455:    */     }
/* 456:    */   }
/* 457:    */   
/* 458:    */   public static enum PERMISSION
/* 459:    */   {
/* 460:802 */     SETUID(2048),  SETGUI(1024),  STICKY(512),  USER_READ(256),  USER_WRITE(128),  USER_EXEC(64),  GROUP_READ(32),  GROUP_WRITE(16),  GROUP_EXEC(8),  WORLD_READ(4),  WORLD_WRITE(2),  WORLD_EXEC(1);
/* 461:    */     
/* 462:    */     private int code;
/* 463:    */     
/* 464:    */     private PERMISSION(int code)
/* 465:    */     {
/* 466:818 */       this.code = code;
/* 467:    */     }
/* 468:    */     
/* 469:    */     public static Set<PERMISSION> find(int code)
/* 470:    */     {
/* 471:822 */       Set<PERMISSION> set = new HashSet();
/* 472:824 */       for (PERMISSION p : values()) {
/* 473:825 */         if ((code & p.code) == p.code) {
/* 474:826 */           set.add(p);
/* 475:    */         }
/* 476:    */       }
/* 477:830 */       if (set.isEmpty()) {
/* 478:831 */         return Collections.emptySet();
/* 479:    */       }
/* 480:834 */       return EnumSet.copyOf(set);
/* 481:    */     }
/* 482:    */   }
/* 483:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.dump.DumpArchiveEntry
 * JD-Core Version:    0.7.0.1
 */