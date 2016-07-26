/*   1:    */ package org.apache.commons.compress.archivers.sevenz;
/*   2:    */ 
/*   3:    */ import java.util.Calendar;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Date;
/*   6:    */ import java.util.LinkedList;
/*   7:    */ import java.util.TimeZone;
/*   8:    */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*   9:    */ 
/*  10:    */ public class SevenZArchiveEntry
/*  11:    */   implements ArchiveEntry
/*  12:    */ {
/*  13:    */   private String name;
/*  14:    */   private boolean hasStream;
/*  15:    */   private boolean isDirectory;
/*  16:    */   private boolean isAntiItem;
/*  17:    */   private boolean hasCreationDate;
/*  18:    */   private boolean hasLastModifiedDate;
/*  19:    */   private boolean hasAccessDate;
/*  20:    */   private long creationDate;
/*  21:    */   private long lastModifiedDate;
/*  22:    */   private long accessDate;
/*  23:    */   private boolean hasWindowsAttributes;
/*  24:    */   private int windowsAttributes;
/*  25:    */   private boolean hasCrc;
/*  26:    */   private long crc;
/*  27:    */   private long compressedCrc;
/*  28:    */   private long size;
/*  29:    */   private long compressedSize;
/*  30:    */   private Iterable<? extends SevenZMethodConfiguration> contentMethods;
/*  31:    */   
/*  32:    */   public String getName()
/*  33:    */   {
/*  34: 61 */     return this.name;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public void setName(String name)
/*  38:    */   {
/*  39: 70 */     this.name = name;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public boolean hasStream()
/*  43:    */   {
/*  44: 78 */     return this.hasStream;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void setHasStream(boolean hasStream)
/*  48:    */   {
/*  49: 86 */     this.hasStream = hasStream;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public boolean isDirectory()
/*  53:    */   {
/*  54: 95 */     return this.isDirectory;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void setDirectory(boolean isDirectory)
/*  58:    */   {
/*  59:104 */     this.isDirectory = isDirectory;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public boolean isAntiItem()
/*  63:    */   {
/*  64:113 */     return this.isAntiItem;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void setAntiItem(boolean isAntiItem)
/*  68:    */   {
/*  69:122 */     this.isAntiItem = isAntiItem;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public boolean getHasCreationDate()
/*  73:    */   {
/*  74:130 */     return this.hasCreationDate;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public void setHasCreationDate(boolean hasCreationDate)
/*  78:    */   {
/*  79:138 */     this.hasCreationDate = hasCreationDate;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public Date getCreationDate()
/*  83:    */   {
/*  84:148 */     if (this.hasCreationDate) {
/*  85:149 */       return ntfsTimeToJavaTime(this.creationDate);
/*  86:    */     }
/*  87:151 */     throw new UnsupportedOperationException("The entry doesn't have this timestamp");
/*  88:    */   }
/*  89:    */   
/*  90:    */   public void setCreationDate(long ntfsCreationDate)
/*  91:    */   {
/*  92:162 */     this.creationDate = ntfsCreationDate;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public void setCreationDate(Date creationDate)
/*  96:    */   {
/*  97:170 */     this.hasCreationDate = (creationDate != null);
/*  98:171 */     if (this.hasCreationDate) {
/*  99:172 */       this.creationDate = javaTimeToNtfsTime(creationDate);
/* 100:    */     }
/* 101:    */   }
/* 102:    */   
/* 103:    */   public boolean getHasLastModifiedDate()
/* 104:    */   {
/* 105:181 */     return this.hasLastModifiedDate;
/* 106:    */   }
/* 107:    */   
/* 108:    */   public void setHasLastModifiedDate(boolean hasLastModifiedDate)
/* 109:    */   {
/* 110:190 */     this.hasLastModifiedDate = hasLastModifiedDate;
/* 111:    */   }
/* 112:    */   
/* 113:    */   public Date getLastModifiedDate()
/* 114:    */   {
/* 115:200 */     if (this.hasLastModifiedDate) {
/* 116:201 */       return ntfsTimeToJavaTime(this.lastModifiedDate);
/* 117:    */     }
/* 118:203 */     throw new UnsupportedOperationException("The entry doesn't have this timestamp");
/* 119:    */   }
/* 120:    */   
/* 121:    */   public void setLastModifiedDate(long ntfsLastModifiedDate)
/* 122:    */   {
/* 123:214 */     this.lastModifiedDate = ntfsLastModifiedDate;
/* 124:    */   }
/* 125:    */   
/* 126:    */   public void setLastModifiedDate(Date lastModifiedDate)
/* 127:    */   {
/* 128:222 */     this.hasLastModifiedDate = (lastModifiedDate != null);
/* 129:223 */     if (this.hasLastModifiedDate) {
/* 130:224 */       this.lastModifiedDate = javaTimeToNtfsTime(lastModifiedDate);
/* 131:    */     }
/* 132:    */   }
/* 133:    */   
/* 134:    */   public boolean getHasAccessDate()
/* 135:    */   {
/* 136:233 */     return this.hasAccessDate;
/* 137:    */   }
/* 138:    */   
/* 139:    */   public void setHasAccessDate(boolean hasAcessDate)
/* 140:    */   {
/* 141:241 */     this.hasAccessDate = hasAcessDate;
/* 142:    */   }
/* 143:    */   
/* 144:    */   public Date getAccessDate()
/* 145:    */   {
/* 146:251 */     if (this.hasAccessDate) {
/* 147:252 */       return ntfsTimeToJavaTime(this.accessDate);
/* 148:    */     }
/* 149:254 */     throw new UnsupportedOperationException("The entry doesn't have this timestamp");
/* 150:    */   }
/* 151:    */   
/* 152:    */   public void setAccessDate(long ntfsAccessDate)
/* 153:    */   {
/* 154:265 */     this.accessDate = ntfsAccessDate;
/* 155:    */   }
/* 156:    */   
/* 157:    */   public void setAccessDate(Date accessDate)
/* 158:    */   {
/* 159:273 */     this.hasAccessDate = (accessDate != null);
/* 160:274 */     if (this.hasAccessDate) {
/* 161:275 */       this.accessDate = javaTimeToNtfsTime(accessDate);
/* 162:    */     }
/* 163:    */   }
/* 164:    */   
/* 165:    */   public boolean getHasWindowsAttributes()
/* 166:    */   {
/* 167:284 */     return this.hasWindowsAttributes;
/* 168:    */   }
/* 169:    */   
/* 170:    */   public void setHasWindowsAttributes(boolean hasWindowsAttributes)
/* 171:    */   {
/* 172:292 */     this.hasWindowsAttributes = hasWindowsAttributes;
/* 173:    */   }
/* 174:    */   
/* 175:    */   public int getWindowsAttributes()
/* 176:    */   {
/* 177:300 */     return this.windowsAttributes;
/* 178:    */   }
/* 179:    */   
/* 180:    */   public void setWindowsAttributes(int windowsAttributes)
/* 181:    */   {
/* 182:308 */     this.windowsAttributes = windowsAttributes;
/* 183:    */   }
/* 184:    */   
/* 185:    */   public boolean getHasCrc()
/* 186:    */   {
/* 187:318 */     return this.hasCrc;
/* 188:    */   }
/* 189:    */   
/* 190:    */   public void setHasCrc(boolean hasCrc)
/* 191:    */   {
/* 192:326 */     this.hasCrc = hasCrc;
/* 193:    */   }
/* 194:    */   
/* 195:    */   @Deprecated
/* 196:    */   public int getCrc()
/* 197:    */   {
/* 198:336 */     return (int)this.crc;
/* 199:    */   }
/* 200:    */   
/* 201:    */   @Deprecated
/* 202:    */   public void setCrc(int crc)
/* 203:    */   {
/* 204:346 */     this.crc = crc;
/* 205:    */   }
/* 206:    */   
/* 207:    */   public long getCrcValue()
/* 208:    */   {
/* 209:355 */     return this.crc;
/* 210:    */   }
/* 211:    */   
/* 212:    */   public void setCrcValue(long crc)
/* 213:    */   {
/* 214:364 */     this.crc = crc;
/* 215:    */   }
/* 216:    */   
/* 217:    */   @Deprecated
/* 218:    */   int getCompressedCrc()
/* 219:    */   {
/* 220:374 */     return (int)this.compressedCrc;
/* 221:    */   }
/* 222:    */   
/* 223:    */   @Deprecated
/* 224:    */   void setCompressedCrc(int crc)
/* 225:    */   {
/* 226:384 */     this.compressedCrc = crc;
/* 227:    */   }
/* 228:    */   
/* 229:    */   long getCompressedCrcValue()
/* 230:    */   {
/* 231:393 */     return this.compressedCrc;
/* 232:    */   }
/* 233:    */   
/* 234:    */   void setCompressedCrcValue(long crc)
/* 235:    */   {
/* 236:402 */     this.compressedCrc = crc;
/* 237:    */   }
/* 238:    */   
/* 239:    */   public long getSize()
/* 240:    */   {
/* 241:411 */     return this.size;
/* 242:    */   }
/* 243:    */   
/* 244:    */   public void setSize(long size)
/* 245:    */   {
/* 246:420 */     this.size = size;
/* 247:    */   }
/* 248:    */   
/* 249:    */   long getCompressedSize()
/* 250:    */   {
/* 251:429 */     return this.compressedSize;
/* 252:    */   }
/* 253:    */   
/* 254:    */   void setCompressedSize(long size)
/* 255:    */   {
/* 256:438 */     this.compressedSize = size;
/* 257:    */   }
/* 258:    */   
/* 259:    */   public void setContentMethods(Iterable<? extends SevenZMethodConfiguration> methods)
/* 260:    */   {
/* 261:456 */     if (methods != null)
/* 262:    */     {
/* 263:457 */       LinkedList<SevenZMethodConfiguration> l = new LinkedList();
/* 264:458 */       for (SevenZMethodConfiguration m : methods) {
/* 265:459 */         l.addLast(m);
/* 266:    */       }
/* 267:461 */       this.contentMethods = Collections.unmodifiableList(l);
/* 268:    */     }
/* 269:    */     else
/* 270:    */     {
/* 271:463 */       this.contentMethods = null;
/* 272:    */     }
/* 273:    */   }
/* 274:    */   
/* 275:    */   public Iterable<? extends SevenZMethodConfiguration> getContentMethods()
/* 276:    */   {
/* 277:482 */     return this.contentMethods;
/* 278:    */   }
/* 279:    */   
/* 280:    */   public static Date ntfsTimeToJavaTime(long ntfsTime)
/* 281:    */   {
/* 282:492 */     Calendar ntfsEpoch = Calendar.getInstance();
/* 283:493 */     ntfsEpoch.setTimeZone(TimeZone.getTimeZone("GMT+0"));
/* 284:494 */     ntfsEpoch.set(1601, 0, 1, 0, 0, 0);
/* 285:495 */     ntfsEpoch.set(14, 0);
/* 286:496 */     long realTime = ntfsEpoch.getTimeInMillis() + ntfsTime / 10000L;
/* 287:497 */     return new Date(realTime);
/* 288:    */   }
/* 289:    */   
/* 290:    */   public static long javaTimeToNtfsTime(Date date)
/* 291:    */   {
/* 292:506 */     Calendar ntfsEpoch = Calendar.getInstance();
/* 293:507 */     ntfsEpoch.setTimeZone(TimeZone.getTimeZone("GMT+0"));
/* 294:508 */     ntfsEpoch.set(1601, 0, 1, 0, 0, 0);
/* 295:509 */     ntfsEpoch.set(14, 0);
/* 296:510 */     return (date.getTime() - ntfsEpoch.getTimeInMillis()) * 1000L * 10L;
/* 297:    */   }
/* 298:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry
 * JD-Core Version:    0.7.0.1
 */