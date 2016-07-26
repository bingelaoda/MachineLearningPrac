/*   1:    */ package org.apache.commons.compress.archivers.zip;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.Date;
/*   5:    */ import java.util.zip.ZipException;
/*   6:    */ 
/*   7:    */ public class X5455_ExtendedTimestamp
/*   8:    */   implements ZipExtraField, Cloneable, Serializable
/*   9:    */ {
/*  10: 84 */   private static final ZipShort HEADER_ID = new ZipShort(21589);
/*  11:    */   private static final long serialVersionUID = 1L;
/*  12:    */   public static final byte MODIFY_TIME_BIT = 1;
/*  13:    */   public static final byte ACCESS_TIME_BIT = 2;
/*  14:    */   public static final byte CREATE_TIME_BIT = 4;
/*  15:    */   private byte flags;
/*  16:    */   private boolean bit0_modifyTimePresent;
/*  17:    */   private boolean bit1_accessTimePresent;
/*  18:    */   private boolean bit2_createTimePresent;
/*  19:    */   private ZipLong modifyTime;
/*  20:    */   private ZipLong accessTime;
/*  21:    */   private ZipLong createTime;
/*  22:    */   
/*  23:    */   public ZipShort getHeaderId()
/*  24:    */   {
/*  25:130 */     return HEADER_ID;
/*  26:    */   }
/*  27:    */   
/*  28:    */   public ZipShort getLocalFileDataLength()
/*  29:    */   {
/*  30:140 */     return new ZipShort(1 + (this.bit0_modifyTimePresent ? 4 : 0) + ((this.bit1_accessTimePresent) && (this.accessTime != null) ? 4 : 0) + ((this.bit2_createTimePresent) && (this.createTime != null) ? 4 : 0));
/*  31:    */   }
/*  32:    */   
/*  33:    */   public ZipShort getCentralDirectoryLength()
/*  34:    */   {
/*  35:158 */     return new ZipShort(1 + (this.bit0_modifyTimePresent ? 4 : 0));
/*  36:    */   }
/*  37:    */   
/*  38:    */   public byte[] getLocalFileDataData()
/*  39:    */   {
/*  40:170 */     byte[] data = new byte[getLocalFileDataLength().getValue()];
/*  41:171 */     int pos = 0;
/*  42:172 */     data[(pos++)] = 0;
/*  43:173 */     if (this.bit0_modifyTimePresent)
/*  44:    */     {
/*  45:174 */       int tmp28_27 = 0; byte[] tmp28_26 = data;tmp28_26[tmp28_27] = ((byte)(tmp28_26[tmp28_27] | 0x1));
/*  46:175 */       System.arraycopy(this.modifyTime.getBytes(), 0, data, pos, 4);
/*  47:176 */       pos += 4;
/*  48:    */     }
/*  49:178 */     if ((this.bit1_accessTimePresent) && (this.accessTime != null))
/*  50:    */     {
/*  51:179 */       int tmp67_66 = 0; byte[] tmp67_65 = data;tmp67_65[tmp67_66] = ((byte)(tmp67_65[tmp67_66] | 0x2));
/*  52:180 */       System.arraycopy(this.accessTime.getBytes(), 0, data, pos, 4);
/*  53:181 */       pos += 4;
/*  54:    */     }
/*  55:183 */     if ((this.bit2_createTimePresent) && (this.createTime != null))
/*  56:    */     {
/*  57:184 */       int tmp106_105 = 0; byte[] tmp106_104 = data;tmp106_104[tmp106_105] = ((byte)(tmp106_104[tmp106_105] | 0x4));
/*  58:185 */       System.arraycopy(this.createTime.getBytes(), 0, data, pos, 4);
/*  59:186 */       pos += 4;
/*  60:    */     }
/*  61:188 */     return data;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public byte[] getCentralDirectoryData()
/*  65:    */   {
/*  66:198 */     byte[] centralData = new byte[getCentralDirectoryLength().getValue()];
/*  67:199 */     byte[] localData = getLocalFileDataData();
/*  68:    */     
/*  69:    */ 
/*  70:    */ 
/*  71:203 */     System.arraycopy(localData, 0, centralData, 0, centralData.length);
/*  72:204 */     return centralData;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public void parseFromLocalFileData(byte[] data, int offset, int length)
/*  76:    */     throws ZipException
/*  77:    */   {
/*  78:218 */     reset();
/*  79:219 */     int len = offset + length;
/*  80:220 */     setFlags(data[(offset++)]);
/*  81:221 */     if (this.bit0_modifyTimePresent)
/*  82:    */     {
/*  83:222 */       this.modifyTime = new ZipLong(data, offset);
/*  84:223 */       offset += 4;
/*  85:    */     }
/*  86:228 */     if ((this.bit1_accessTimePresent) && (offset + 4 <= len))
/*  87:    */     {
/*  88:229 */       this.accessTime = new ZipLong(data, offset);
/*  89:230 */       offset += 4;
/*  90:    */     }
/*  91:232 */     if ((this.bit2_createTimePresent) && (offset + 4 <= len))
/*  92:    */     {
/*  93:233 */       this.createTime = new ZipLong(data, offset);
/*  94:234 */       offset += 4;
/*  95:    */     }
/*  96:    */   }
/*  97:    */   
/*  98:    */   public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length)
/*  99:    */     throws ZipException
/* 100:    */   {
/* 101:245 */     reset();
/* 102:246 */     parseFromLocalFileData(buffer, offset, length);
/* 103:    */   }
/* 104:    */   
/* 105:    */   private void reset()
/* 106:    */   {
/* 107:254 */     setFlags((byte)0);
/* 108:255 */     this.modifyTime = null;
/* 109:256 */     this.accessTime = null;
/* 110:257 */     this.createTime = null;
/* 111:    */   }
/* 112:    */   
/* 113:    */   public void setFlags(byte flags)
/* 114:    */   {
/* 115:275 */     this.flags = flags;
/* 116:276 */     this.bit0_modifyTimePresent = ((flags & 0x1) == 1);
/* 117:277 */     this.bit1_accessTimePresent = ((flags & 0x2) == 2);
/* 118:278 */     this.bit2_createTimePresent = ((flags & 0x4) == 4);
/* 119:    */   }
/* 120:    */   
/* 121:    */   public byte getFlags()
/* 122:    */   {
/* 123:295 */     return this.flags;
/* 124:    */   }
/* 125:    */   
/* 126:    */   public boolean isBit0_modifyTimePresent()
/* 127:    */   {
/* 128:304 */     return this.bit0_modifyTimePresent;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public boolean isBit1_accessTimePresent()
/* 132:    */   {
/* 133:313 */     return this.bit1_accessTimePresent;
/* 134:    */   }
/* 135:    */   
/* 136:    */   public boolean isBit2_createTimePresent()
/* 137:    */   {
/* 138:322 */     return this.bit2_createTimePresent;
/* 139:    */   }
/* 140:    */   
/* 141:    */   public ZipLong getModifyTime()
/* 142:    */   {
/* 143:331 */     return this.modifyTime;
/* 144:    */   }
/* 145:    */   
/* 146:    */   public ZipLong getAccessTime()
/* 147:    */   {
/* 148:340 */     return this.accessTime;
/* 149:    */   }
/* 150:    */   
/* 151:    */   public ZipLong getCreateTime()
/* 152:    */   {
/* 153:355 */     return this.createTime;
/* 154:    */   }
/* 155:    */   
/* 156:    */   public Date getModifyJavaTime()
/* 157:    */   {
/* 158:366 */     return this.modifyTime != null ? new Date(this.modifyTime.getValue() * 1000L) : null;
/* 159:    */   }
/* 160:    */   
/* 161:    */   public Date getAccessJavaTime()
/* 162:    */   {
/* 163:378 */     return this.accessTime != null ? new Date(this.accessTime.getValue() * 1000L) : null;
/* 164:    */   }
/* 165:    */   
/* 166:    */   public Date getCreateJavaTime()
/* 167:    */   {
/* 168:396 */     return this.createTime != null ? new Date(this.createTime.getValue() * 1000L) : null;
/* 169:    */   }
/* 170:    */   
/* 171:    */   public void setModifyTime(ZipLong l)
/* 172:    */   {
/* 173:412 */     this.bit0_modifyTimePresent = (l != null);
/* 174:413 */     this.flags = ((byte)(l != null ? this.flags | 0x1 : this.flags & 0xFFFFFFFE));
/* 175:    */     
/* 176:415 */     this.modifyTime = l;
/* 177:    */   }
/* 178:    */   
/* 179:    */   public void setAccessTime(ZipLong l)
/* 180:    */   {
/* 181:431 */     this.bit1_accessTimePresent = (l != null);
/* 182:432 */     this.flags = ((byte)(l != null ? this.flags | 0x2 : this.flags & 0xFFFFFFFD));
/* 183:    */     
/* 184:434 */     this.accessTime = l;
/* 185:    */   }
/* 186:    */   
/* 187:    */   public void setCreateTime(ZipLong l)
/* 188:    */   {
/* 189:450 */     this.bit2_createTimePresent = (l != null);
/* 190:451 */     this.flags = ((byte)(l != null ? this.flags | 0x4 : this.flags & 0xFFFFFFFB));
/* 191:    */     
/* 192:453 */     this.createTime = l;
/* 193:    */   }
/* 194:    */   
/* 195:    */   public void setModifyJavaTime(Date d)
/* 196:    */   {
/* 197:469 */     setModifyTime(dateToZipLong(d));
/* 198:    */   }
/* 199:    */   
/* 200:    */   public void setAccessJavaTime(Date d)
/* 201:    */   {
/* 202:484 */     setAccessTime(dateToZipLong(d));
/* 203:    */   }
/* 204:    */   
/* 205:    */   public void setCreateJavaTime(Date d)
/* 206:    */   {
/* 207:499 */     setCreateTime(dateToZipLong(d));
/* 208:    */   }
/* 209:    */   
/* 210:    */   private static ZipLong dateToZipLong(Date d)
/* 211:    */   {
/* 212:512 */     if (d == null) {
/* 213:512 */       return null;
/* 214:    */     }
/* 215:514 */     long TWO_TO_32 = 4294967296L;
/* 216:515 */     long l = d.getTime() / 1000L;
/* 217:516 */     if (l >= 4294967296L) {
/* 218:517 */       throw new IllegalArgumentException("Cannot set an X5455 timestamp larger than 2^32: " + l);
/* 219:    */     }
/* 220:519 */     return new ZipLong(l);
/* 221:    */   }
/* 222:    */   
/* 223:    */   public String toString()
/* 224:    */   {
/* 225:531 */     StringBuilder buf = new StringBuilder();
/* 226:532 */     buf.append("0x5455 Zip Extra Field: Flags=");
/* 227:533 */     buf.append(Integer.toBinaryString(ZipUtil.unsignedIntToSignedByte(this.flags))).append(" ");
/* 228:534 */     if ((this.bit0_modifyTimePresent) && (this.modifyTime != null))
/* 229:    */     {
/* 230:535 */       Date m = getModifyJavaTime();
/* 231:536 */       buf.append(" Modify:[").append(m).append("] ");
/* 232:    */     }
/* 233:538 */     if ((this.bit1_accessTimePresent) && (this.accessTime != null))
/* 234:    */     {
/* 235:539 */       Date a = getAccessJavaTime();
/* 236:540 */       buf.append(" Access:[").append(a).append("] ");
/* 237:    */     }
/* 238:542 */     if ((this.bit2_createTimePresent) && (this.createTime != null))
/* 239:    */     {
/* 240:543 */       Date c = getCreateJavaTime();
/* 241:544 */       buf.append(" Create:[").append(c).append("] ");
/* 242:    */     }
/* 243:546 */     return buf.toString();
/* 244:    */   }
/* 245:    */   
/* 246:    */   public Object clone()
/* 247:    */     throws CloneNotSupportedException
/* 248:    */   {
/* 249:551 */     return super.clone();
/* 250:    */   }
/* 251:    */   
/* 252:    */   public boolean equals(Object o)
/* 253:    */   {
/* 254:556 */     if ((o instanceof X5455_ExtendedTimestamp))
/* 255:    */     {
/* 256:557 */       X5455_ExtendedTimestamp xf = (X5455_ExtendedTimestamp)o;
/* 257:    */       
/* 258:    */ 
/* 259:    */ 
/* 260:561 */       return ((this.flags & 0x7) == (xf.flags & 0x7)) && ((this.modifyTime == xf.modifyTime) || ((this.modifyTime != null) && (this.modifyTime.equals(xf.modifyTime)))) && ((this.accessTime == xf.accessTime) || ((this.accessTime != null) && (this.accessTime.equals(xf.accessTime)))) && ((this.createTime == xf.createTime) || ((this.createTime != null) && (this.createTime.equals(xf.createTime))));
/* 261:    */     }
/* 262:566 */     return false;
/* 263:    */   }
/* 264:    */   
/* 265:    */   public int hashCode()
/* 266:    */   {
/* 267:572 */     int hc = -123 * (this.flags & 0x7);
/* 268:573 */     if (this.modifyTime != null) {
/* 269:574 */       hc ^= this.modifyTime.hashCode();
/* 270:    */     }
/* 271:576 */     if (this.accessTime != null) {
/* 272:579 */       hc ^= Integer.rotateLeft(this.accessTime.hashCode(), 11);
/* 273:    */     }
/* 274:581 */     if (this.createTime != null) {
/* 275:582 */       hc ^= Integer.rotateLeft(this.createTime.hashCode(), 22);
/* 276:    */     }
/* 277:584 */     return hc;
/* 278:    */   }
/* 279:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.X5455_ExtendedTimestamp
 * JD-Core Version:    0.7.0.1
 */