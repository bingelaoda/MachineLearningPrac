/*   1:    */ package org.apache.commons.compress.archivers.zip;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Arrays;
/*   6:    */ import java.util.Date;
/*   7:    */ import java.util.List;
/*   8:    */ import java.util.NoSuchElementException;
/*   9:    */ import java.util.zip.ZipEntry;
/*  10:    */ import java.util.zip.ZipException;
/*  11:    */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*  12:    */ 
/*  13:    */ public class ZipArchiveEntry
/*  14:    */   extends ZipEntry
/*  15:    */   implements ArchiveEntry
/*  16:    */ {
/*  17:    */   public static final int PLATFORM_UNIX = 3;
/*  18:    */   public static final int PLATFORM_FAT = 0;
/*  19:    */   public static final int CRC_UNKNOWN = -1;
/*  20:    */   private static final int SHORT_MASK = 65535;
/*  21:    */   private static final int SHORT_SHIFT = 16;
/*  22: 58 */   private static final byte[] EMPTY = new byte[0];
/*  23: 70 */   private int method = -1;
/*  24: 78 */   private long size = -1L;
/*  25: 80 */   private int internalAttributes = 0;
/*  26: 81 */   private int platform = 0;
/*  27: 82 */   private long externalAttributes = 0L;
/*  28:    */   private ZipExtraField[] extraFields;
/*  29: 84 */   private UnparseableExtraFieldData unparseableExtra = null;
/*  30: 85 */   private String name = null;
/*  31: 86 */   private byte[] rawName = null;
/*  32: 87 */   private GeneralPurposeBit gpb = new GeneralPurposeBit();
/*  33: 88 */   private static final ZipExtraField[] noExtraFields = new ZipExtraField[0];
/*  34:    */   
/*  35:    */   public ZipArchiveEntry(String name)
/*  36:    */   {
/*  37: 99 */     super(name);
/*  38:100 */     setName(name);
/*  39:    */   }
/*  40:    */   
/*  41:    */   public ZipArchiveEntry(ZipEntry entry)
/*  42:    */     throws ZipException
/*  43:    */   {
/*  44:113 */     super(entry);
/*  45:114 */     setName(entry.getName());
/*  46:115 */     byte[] extra = entry.getExtra();
/*  47:116 */     if (extra != null) {
/*  48:117 */       setExtraFields(ExtraFieldUtils.parse(extra, true, ExtraFieldUtils.UnparseableExtraField.READ));
/*  49:    */     } else {
/*  50:122 */       setExtra();
/*  51:    */     }
/*  52:124 */     setMethod(entry.getMethod());
/*  53:125 */     this.size = entry.getSize();
/*  54:    */   }
/*  55:    */   
/*  56:    */   public ZipArchiveEntry(ZipArchiveEntry entry)
/*  57:    */     throws ZipException
/*  58:    */   {
/*  59:138 */     this(entry);
/*  60:139 */     setInternalAttributes(entry.getInternalAttributes());
/*  61:140 */     setExternalAttributes(entry.getExternalAttributes());
/*  62:141 */     setExtraFields(getAllExtraFieldsNoCopy());
/*  63:142 */     setPlatform(entry.getPlatform());
/*  64:143 */     GeneralPurposeBit other = entry.getGeneralPurposeBit();
/*  65:144 */     setGeneralPurposeBit(other == null ? null : (GeneralPurposeBit)other.clone());
/*  66:    */   }
/*  67:    */   
/*  68:    */   protected ZipArchiveEntry()
/*  69:    */   {
/*  70:151 */     this("");
/*  71:    */   }
/*  72:    */   
/*  73:    */   public ZipArchiveEntry(File inputFile, String entryName)
/*  74:    */   {
/*  75:166 */     this((inputFile.isDirectory()) && (!entryName.endsWith("/")) ? entryName + "/" : entryName);
/*  76:168 */     if (inputFile.isFile()) {
/*  77:169 */       setSize(inputFile.length());
/*  78:    */     }
/*  79:171 */     setTime(inputFile.lastModified());
/*  80:    */   }
/*  81:    */   
/*  82:    */   public Object clone()
/*  83:    */   {
/*  84:181 */     ZipArchiveEntry e = (ZipArchiveEntry)super.clone();
/*  85:    */     
/*  86:183 */     e.setInternalAttributes(getInternalAttributes());
/*  87:184 */     e.setExternalAttributes(getExternalAttributes());
/*  88:185 */     e.setExtraFields(getAllExtraFieldsNoCopy());
/*  89:186 */     return e;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public int getMethod()
/*  93:    */   {
/*  94:199 */     return this.method;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public void setMethod(int method)
/*  98:    */   {
/*  99:211 */     if (method < 0) {
/* 100:212 */       throw new IllegalArgumentException("ZIP compression method can not be negative: " + method);
/* 101:    */     }
/* 102:215 */     this.method = method;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public int getInternalAttributes()
/* 106:    */   {
/* 107:228 */     return this.internalAttributes;
/* 108:    */   }
/* 109:    */   
/* 110:    */   public void setInternalAttributes(int value)
/* 111:    */   {
/* 112:236 */     this.internalAttributes = value;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public long getExternalAttributes()
/* 116:    */   {
/* 117:249 */     return this.externalAttributes;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public void setExternalAttributes(long value)
/* 121:    */   {
/* 122:257 */     this.externalAttributes = value;
/* 123:    */   }
/* 124:    */   
/* 125:    */   public void setUnixMode(int mode)
/* 126:    */   {
/* 127:267 */     setExternalAttributes(mode << 16 | ((mode & 0x80) == 0 ? 1 : 0) | (isDirectory() ? 16 : 0));
/* 128:    */     
/* 129:    */ 
/* 130:    */ 
/* 131:    */ 
/* 132:    */ 
/* 133:273 */     this.platform = 3;
/* 134:    */   }
/* 135:    */   
/* 136:    */   public int getUnixMode()
/* 137:    */   {
/* 138:281 */     return this.platform != 3 ? 0 : (int)(getExternalAttributes() >> 16 & 0xFFFF);
/* 139:    */   }
/* 140:    */   
/* 141:    */   public boolean isUnixSymlink()
/* 142:    */   {
/* 143:294 */     return (getUnixMode() & 0xA000) == 40960;
/* 144:    */   }
/* 145:    */   
/* 146:    */   public int getPlatform()
/* 147:    */   {
/* 148:305 */     return this.platform;
/* 149:    */   }
/* 150:    */   
/* 151:    */   protected void setPlatform(int platform)
/* 152:    */   {
/* 153:313 */     this.platform = platform;
/* 154:    */   }
/* 155:    */   
/* 156:    */   public void setExtraFields(ZipExtraField[] fields)
/* 157:    */   {
/* 158:321 */     List<ZipExtraField> newFields = new ArrayList();
/* 159:322 */     for (ZipExtraField field : fields) {
/* 160:323 */       if ((field instanceof UnparseableExtraFieldData)) {
/* 161:324 */         this.unparseableExtra = ((UnparseableExtraFieldData)field);
/* 162:    */       } else {
/* 163:326 */         newFields.add(field);
/* 164:    */       }
/* 165:    */     }
/* 166:329 */     this.extraFields = ((ZipExtraField[])newFields.toArray(new ZipExtraField[newFields.size()]));
/* 167:330 */     setExtra();
/* 168:    */   }
/* 169:    */   
/* 170:    */   public ZipExtraField[] getExtraFields()
/* 171:    */   {
/* 172:344 */     return getParseableExtraFields();
/* 173:    */   }
/* 174:    */   
/* 175:    */   public ZipExtraField[] getExtraFields(boolean includeUnparseable)
/* 176:    */   {
/* 177:357 */     return includeUnparseable ? getAllExtraFields() : getParseableExtraFields();
/* 178:    */   }
/* 179:    */   
/* 180:    */   private ZipExtraField[] getParseableExtraFieldsNoCopy()
/* 181:    */   {
/* 182:363 */     if (this.extraFields == null) {
/* 183:364 */       return noExtraFields;
/* 184:    */     }
/* 185:366 */     return this.extraFields;
/* 186:    */   }
/* 187:    */   
/* 188:    */   private ZipExtraField[] getParseableExtraFields()
/* 189:    */   {
/* 190:370 */     ZipExtraField[] parseableExtraFields = getParseableExtraFieldsNoCopy();
/* 191:371 */     return parseableExtraFields == this.extraFields ? copyOf(parseableExtraFields) : parseableExtraFields;
/* 192:    */   }
/* 193:    */   
/* 194:    */   private ZipExtraField[] getAllExtraFieldsNoCopy()
/* 195:    */   {
/* 196:379 */     if (this.extraFields == null) {
/* 197:380 */       return getUnparseableOnly();
/* 198:    */     }
/* 199:382 */     return this.unparseableExtra != null ? getMergedFields() : this.extraFields;
/* 200:    */   }
/* 201:    */   
/* 202:    */   private ZipExtraField[] copyOf(ZipExtraField[] src)
/* 203:    */   {
/* 204:386 */     return copyOf(src, src.length);
/* 205:    */   }
/* 206:    */   
/* 207:    */   private ZipExtraField[] copyOf(ZipExtraField[] src, int length)
/* 208:    */   {
/* 209:390 */     ZipExtraField[] cpy = new ZipExtraField[length];
/* 210:391 */     System.arraycopy(src, 0, cpy, 0, Math.min(src.length, length));
/* 211:392 */     return cpy;
/* 212:    */   }
/* 213:    */   
/* 214:    */   private ZipExtraField[] getMergedFields()
/* 215:    */   {
/* 216:396 */     ZipExtraField[] zipExtraFields = copyOf(this.extraFields, this.extraFields.length + 1);
/* 217:397 */     zipExtraFields[this.extraFields.length] = this.unparseableExtra;
/* 218:398 */     return zipExtraFields;
/* 219:    */   }
/* 220:    */   
/* 221:    */   private ZipExtraField[] getUnparseableOnly()
/* 222:    */   {
/* 223:402 */     return new ZipExtraField[] { this.unparseableExtra == null ? noExtraFields : this.unparseableExtra };
/* 224:    */   }
/* 225:    */   
/* 226:    */   private ZipExtraField[] getAllExtraFields()
/* 227:    */   {
/* 228:406 */     ZipExtraField[] allExtraFieldsNoCopy = getAllExtraFieldsNoCopy();
/* 229:407 */     return allExtraFieldsNoCopy == this.extraFields ? copyOf(allExtraFieldsNoCopy) : allExtraFieldsNoCopy;
/* 230:    */   }
/* 231:    */   
/* 232:    */   public void addExtraField(ZipExtraField ze)
/* 233:    */   {
/* 234:418 */     if ((ze instanceof UnparseableExtraFieldData))
/* 235:    */     {
/* 236:419 */       this.unparseableExtra = ((UnparseableExtraFieldData)ze);
/* 237:    */     }
/* 238:421 */     else if (this.extraFields == null)
/* 239:    */     {
/* 240:422 */       this.extraFields = new ZipExtraField[] { ze };
/* 241:    */     }
/* 242:    */     else
/* 243:    */     {
/* 244:424 */       if (getExtraField(ze.getHeaderId()) != null) {
/* 245:425 */         removeExtraField(ze.getHeaderId());
/* 246:    */       }
/* 247:427 */       ZipExtraField[] zipExtraFields = copyOf(this.extraFields, this.extraFields.length + 1);
/* 248:428 */       zipExtraFields[(zipExtraFields.length - 1)] = ze;
/* 249:429 */       this.extraFields = zipExtraFields;
/* 250:    */     }
/* 251:432 */     setExtra();
/* 252:    */   }
/* 253:    */   
/* 254:    */   public void addAsFirstExtraField(ZipExtraField ze)
/* 255:    */   {
/* 256:443 */     if ((ze instanceof UnparseableExtraFieldData))
/* 257:    */     {
/* 258:444 */       this.unparseableExtra = ((UnparseableExtraFieldData)ze);
/* 259:    */     }
/* 260:    */     else
/* 261:    */     {
/* 262:446 */       if (getExtraField(ze.getHeaderId()) != null) {
/* 263:447 */         removeExtraField(ze.getHeaderId());
/* 264:    */       }
/* 265:449 */       ZipExtraField[] copy = this.extraFields;
/* 266:450 */       int newLen = this.extraFields != null ? this.extraFields.length + 1 : 1;
/* 267:451 */       this.extraFields = new ZipExtraField[newLen];
/* 268:452 */       this.extraFields[0] = ze;
/* 269:453 */       if (copy != null) {
/* 270:454 */         System.arraycopy(copy, 0, this.extraFields, 1, this.extraFields.length - 1);
/* 271:    */       }
/* 272:    */     }
/* 273:457 */     setExtra();
/* 274:    */   }
/* 275:    */   
/* 276:    */   public void removeExtraField(ZipShort type)
/* 277:    */   {
/* 278:465 */     if (this.extraFields == null) {
/* 279:466 */       throw new NoSuchElementException();
/* 280:    */     }
/* 281:469 */     List<ZipExtraField> newResult = new ArrayList();
/* 282:470 */     for (ZipExtraField extraField : this.extraFields) {
/* 283:471 */       if (!type.equals(extraField.getHeaderId())) {
/* 284:472 */         newResult.add(extraField);
/* 285:    */       }
/* 286:    */     }
/* 287:475 */     if (this.extraFields.length == newResult.size()) {
/* 288:476 */       throw new NoSuchElementException();
/* 289:    */     }
/* 290:478 */     this.extraFields = ((ZipExtraField[])newResult.toArray(new ZipExtraField[newResult.size()]));
/* 291:479 */     setExtra();
/* 292:    */   }
/* 293:    */   
/* 294:    */   public void removeUnparseableExtraFieldData()
/* 295:    */   {
/* 296:488 */     if (this.unparseableExtra == null) {
/* 297:489 */       throw new NoSuchElementException();
/* 298:    */     }
/* 299:491 */     this.unparseableExtra = null;
/* 300:492 */     setExtra();
/* 301:    */   }
/* 302:    */   
/* 303:    */   public ZipExtraField getExtraField(ZipShort type)
/* 304:    */   {
/* 305:502 */     if (this.extraFields != null) {
/* 306:503 */       for (ZipExtraField extraField : this.extraFields) {
/* 307:504 */         if (type.equals(extraField.getHeaderId())) {
/* 308:505 */           return extraField;
/* 309:    */         }
/* 310:    */       }
/* 311:    */     }
/* 312:509 */     return null;
/* 313:    */   }
/* 314:    */   
/* 315:    */   public UnparseableExtraFieldData getUnparseableExtraFieldData()
/* 316:    */   {
/* 317:520 */     return this.unparseableExtra;
/* 318:    */   }
/* 319:    */   
/* 320:    */   public void setExtra(byte[] extra)
/* 321:    */     throws RuntimeException
/* 322:    */   {
/* 323:    */     try
/* 324:    */     {
/* 325:534 */       ZipExtraField[] local = ExtraFieldUtils.parse(extra, true, ExtraFieldUtils.UnparseableExtraField.READ);
/* 326:    */       
/* 327:    */ 
/* 328:537 */       mergeExtraFields(local, true);
/* 329:    */     }
/* 330:    */     catch (ZipException e)
/* 331:    */     {
/* 332:540 */       throw new RuntimeException("Error parsing extra fields for entry: " + getName() + " - " + e.getMessage(), e);
/* 333:    */     }
/* 334:    */   }
/* 335:    */   
/* 336:    */   protected void setExtra()
/* 337:    */   {
/* 338:552 */     super.setExtra(ExtraFieldUtils.mergeLocalFileDataData(getAllExtraFieldsNoCopy()));
/* 339:    */   }
/* 340:    */   
/* 341:    */   public void setCentralDirectoryExtra(byte[] b)
/* 342:    */   {
/* 343:    */     try
/* 344:    */     {
/* 345:561 */       ZipExtraField[] central = ExtraFieldUtils.parse(b, false, ExtraFieldUtils.UnparseableExtraField.READ);
/* 346:    */       
/* 347:    */ 
/* 348:564 */       mergeExtraFields(central, false);
/* 349:    */     }
/* 350:    */     catch (ZipException e)
/* 351:    */     {
/* 352:566 */       throw new RuntimeException(e.getMessage(), e);
/* 353:    */     }
/* 354:    */   }
/* 355:    */   
/* 356:    */   public byte[] getLocalFileDataExtra()
/* 357:    */   {
/* 358:575 */     byte[] extra = getExtra();
/* 359:576 */     return extra != null ? extra : EMPTY;
/* 360:    */   }
/* 361:    */   
/* 362:    */   public byte[] getCentralDirectoryExtra()
/* 363:    */   {
/* 364:584 */     return ExtraFieldUtils.mergeCentralDirectoryData(getAllExtraFieldsNoCopy());
/* 365:    */   }
/* 366:    */   
/* 367:    */   public String getName()
/* 368:    */   {
/* 369:593 */     return this.name == null ? super.getName() : this.name;
/* 370:    */   }
/* 371:    */   
/* 372:    */   public boolean isDirectory()
/* 373:    */   {
/* 374:602 */     return getName().endsWith("/");
/* 375:    */   }
/* 376:    */   
/* 377:    */   protected void setName(String name)
/* 378:    */   {
/* 379:610 */     if ((name != null) && (getPlatform() == 0) && (!name.contains("/"))) {
/* 380:612 */       name = name.replace('\\', '/');
/* 381:    */     }
/* 382:614 */     this.name = name;
/* 383:    */   }
/* 384:    */   
/* 385:    */   public long getSize()
/* 386:    */   {
/* 387:628 */     return this.size;
/* 388:    */   }
/* 389:    */   
/* 390:    */   public void setSize(long size)
/* 391:    */   {
/* 392:639 */     if (size < 0L) {
/* 393:640 */       throw new IllegalArgumentException("invalid entry size");
/* 394:    */     }
/* 395:642 */     this.size = size;
/* 396:    */   }
/* 397:    */   
/* 398:    */   protected void setName(String name, byte[] rawName)
/* 399:    */   {
/* 400:655 */     setName(name);
/* 401:656 */     this.rawName = rawName;
/* 402:    */   }
/* 403:    */   
/* 404:    */   public byte[] getRawName()
/* 405:    */   {
/* 406:670 */     if (this.rawName != null)
/* 407:    */     {
/* 408:671 */       byte[] b = new byte[this.rawName.length];
/* 409:672 */       System.arraycopy(this.rawName, 0, b, 0, this.rawName.length);
/* 410:673 */       return b;
/* 411:    */     }
/* 412:675 */     return null;
/* 413:    */   }
/* 414:    */   
/* 415:    */   public int hashCode()
/* 416:    */   {
/* 417:689 */     return getName().hashCode();
/* 418:    */   }
/* 419:    */   
/* 420:    */   public GeneralPurposeBit getGeneralPurposeBit()
/* 421:    */   {
/* 422:698 */     return this.gpb;
/* 423:    */   }
/* 424:    */   
/* 425:    */   public void setGeneralPurposeBit(GeneralPurposeBit b)
/* 426:    */   {
/* 427:707 */     this.gpb = b;
/* 428:    */   }
/* 429:    */   
/* 430:    */   private void mergeExtraFields(ZipExtraField[] f, boolean local)
/* 431:    */     throws ZipException
/* 432:    */   {
/* 433:720 */     if (this.extraFields == null)
/* 434:    */     {
/* 435:721 */       setExtraFields(f);
/* 436:    */     }
/* 437:    */     else
/* 438:    */     {
/* 439:723 */       for (ZipExtraField element : f)
/* 440:    */       {
/* 441:    */         ZipExtraField existing;
/* 442:    */         ZipExtraField existing;
/* 443:725 */         if ((element instanceof UnparseableExtraFieldData)) {
/* 444:726 */           existing = this.unparseableExtra;
/* 445:    */         } else {
/* 446:728 */           existing = getExtraField(element.getHeaderId());
/* 447:    */         }
/* 448:730 */         if (existing == null)
/* 449:    */         {
/* 450:731 */           addExtraField(element);
/* 451:    */         }
/* 452:733 */         else if (local)
/* 453:    */         {
/* 454:734 */           byte[] b = element.getLocalFileDataData();
/* 455:735 */           existing.parseFromLocalFileData(b, 0, b.length);
/* 456:    */         }
/* 457:    */         else
/* 458:    */         {
/* 459:737 */           byte[] b = element.getCentralDirectoryData();
/* 460:738 */           existing.parseFromCentralDirectoryData(b, 0, b.length);
/* 461:    */         }
/* 462:    */       }
/* 463:742 */       setExtra();
/* 464:    */     }
/* 465:    */   }
/* 466:    */   
/* 467:    */   public Date getLastModifiedDate()
/* 468:    */   {
/* 469:747 */     return new Date(getTime());
/* 470:    */   }
/* 471:    */   
/* 472:    */   public boolean equals(Object obj)
/* 473:    */   {
/* 474:755 */     if (this == obj) {
/* 475:756 */       return true;
/* 476:    */     }
/* 477:758 */     if ((obj == null) || (getClass() != obj.getClass())) {
/* 478:759 */       return false;
/* 479:    */     }
/* 480:761 */     ZipArchiveEntry other = (ZipArchiveEntry)obj;
/* 481:762 */     String myName = getName();
/* 482:763 */     String otherName = other.getName();
/* 483:764 */     if (myName == null)
/* 484:    */     {
/* 485:765 */       if (otherName != null) {
/* 486:766 */         return false;
/* 487:    */       }
/* 488:    */     }
/* 489:768 */     else if (!myName.equals(otherName)) {
/* 490:769 */       return false;
/* 491:    */     }
/* 492:771 */     String myComment = getComment();
/* 493:772 */     String otherComment = other.getComment();
/* 494:773 */     if (myComment == null) {
/* 495:774 */       myComment = "";
/* 496:    */     }
/* 497:776 */     if (otherComment == null) {
/* 498:777 */       otherComment = "";
/* 499:    */     }
/* 500:779 */     return (getTime() == other.getTime()) && (myComment.equals(otherComment)) && (getInternalAttributes() == other.getInternalAttributes()) && (getPlatform() == other.getPlatform()) && (getExternalAttributes() == other.getExternalAttributes()) && (getMethod() == other.getMethod()) && (getSize() == other.getSize()) && (getCrc() == other.getCrc()) && (getCompressedSize() == other.getCompressedSize()) && (Arrays.equals(getCentralDirectoryExtra(), other.getCentralDirectoryExtra())) && (Arrays.equals(getLocalFileDataExtra(), other.getLocalFileDataExtra())) && (this.gpb.equals(other.gpb));
/* 501:    */   }
/* 502:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.ZipArchiveEntry
 * JD-Core Version:    0.7.0.1
 */