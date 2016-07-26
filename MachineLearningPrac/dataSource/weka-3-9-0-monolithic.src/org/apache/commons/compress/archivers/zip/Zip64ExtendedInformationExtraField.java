/*   1:    */ package org.apache.commons.compress.archivers.zip;
/*   2:    */ 
/*   3:    */ import java.util.zip.ZipException;
/*   4:    */ 
/*   5:    */ public class Zip64ExtendedInformationExtraField
/*   6:    */   implements ZipExtraField
/*   7:    */ {
/*   8: 45 */   static final ZipShort HEADER_ID = new ZipShort(1);
/*   9:    */   private static final String LFH_MUST_HAVE_BOTH_SIZES_MSG = "Zip64 extended information must contain both size values in the local file header.";
/*  10: 50 */   private static final byte[] EMPTY = new byte[0];
/*  11:    */   private ZipEightByteInteger size;
/*  12:    */   private ZipEightByteInteger compressedSize;
/*  13:    */   private ZipEightByteInteger relativeHeaderOffset;
/*  14:    */   private ZipLong diskStart;
/*  15:    */   private byte[] rawCentralDirectoryData;
/*  16:    */   
/*  17:    */   public Zip64ExtendedInformationExtraField() {}
/*  18:    */   
/*  19:    */   public Zip64ExtendedInformationExtraField(ZipEightByteInteger size, ZipEightByteInteger compressedSize)
/*  20:    */   {
/*  21: 83 */     this(size, compressedSize, null, null);
/*  22:    */   }
/*  23:    */   
/*  24:    */   public Zip64ExtendedInformationExtraField(ZipEightByteInteger size, ZipEightByteInteger compressedSize, ZipEightByteInteger relativeHeaderOffset, ZipLong diskStart)
/*  25:    */   {
/*  26:100 */     this.size = size;
/*  27:101 */     this.compressedSize = compressedSize;
/*  28:102 */     this.relativeHeaderOffset = relativeHeaderOffset;
/*  29:103 */     this.diskStart = diskStart;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public ZipShort getHeaderId()
/*  33:    */   {
/*  34:107 */     return HEADER_ID;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public ZipShort getLocalFileDataLength()
/*  38:    */   {
/*  39:111 */     return new ZipShort(this.size != null ? 16 : 0);
/*  40:    */   }
/*  41:    */   
/*  42:    */   public ZipShort getCentralDirectoryLength()
/*  43:    */   {
/*  44:115 */     return new ZipShort((this.size != null ? 8 : 0) + (this.compressedSize != null ? 8 : 0) + (this.relativeHeaderOffset != null ? 8 : 0) + (this.diskStart != null ? 4 : 0));
/*  45:    */   }
/*  46:    */   
/*  47:    */   public byte[] getLocalFileDataData()
/*  48:    */   {
/*  49:122 */     if ((this.size != null) || (this.compressedSize != null))
/*  50:    */     {
/*  51:123 */       if ((this.size == null) || (this.compressedSize == null)) {
/*  52:124 */         throw new IllegalArgumentException("Zip64 extended information must contain both size values in the local file header.");
/*  53:    */       }
/*  54:126 */       byte[] data = new byte[16];
/*  55:127 */       addSizes(data);
/*  56:128 */       return data;
/*  57:    */     }
/*  58:130 */     return EMPTY;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public byte[] getCentralDirectoryData()
/*  62:    */   {
/*  63:134 */     byte[] data = new byte[getCentralDirectoryLength().getValue()];
/*  64:135 */     int off = addSizes(data);
/*  65:136 */     if (this.relativeHeaderOffset != null)
/*  66:    */     {
/*  67:137 */       System.arraycopy(this.relativeHeaderOffset.getBytes(), 0, data, off, 8);
/*  68:138 */       off += 8;
/*  69:    */     }
/*  70:140 */     if (this.diskStart != null)
/*  71:    */     {
/*  72:141 */       System.arraycopy(this.diskStart.getBytes(), 0, data, off, 4);
/*  73:142 */       off += 4;
/*  74:    */     }
/*  75:144 */     return data;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void parseFromLocalFileData(byte[] buffer, int offset, int length)
/*  79:    */     throws ZipException
/*  80:    */   {
/*  81:149 */     if (length == 0) {
/*  82:154 */       return;
/*  83:    */     }
/*  84:156 */     if (length < 16) {
/*  85:157 */       throw new ZipException("Zip64 extended information must contain both size values in the local file header.");
/*  86:    */     }
/*  87:159 */     this.size = new ZipEightByteInteger(buffer, offset);
/*  88:160 */     offset += 8;
/*  89:161 */     this.compressedSize = new ZipEightByteInteger(buffer, offset);
/*  90:162 */     offset += 8;
/*  91:163 */     int remaining = length - 16;
/*  92:164 */     if (remaining >= 8)
/*  93:    */     {
/*  94:165 */       this.relativeHeaderOffset = new ZipEightByteInteger(buffer, offset);
/*  95:166 */       offset += 8;
/*  96:167 */       remaining -= 8;
/*  97:    */     }
/*  98:169 */     if (remaining >= 4)
/*  99:    */     {
/* 100:170 */       this.diskStart = new ZipLong(buffer, offset);
/* 101:171 */       offset += 4;
/* 102:172 */       remaining -= 4;
/* 103:    */     }
/* 104:    */   }
/* 105:    */   
/* 106:    */   public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length)
/* 107:    */     throws ZipException
/* 108:    */   {
/* 109:180 */     this.rawCentralDirectoryData = new byte[length];
/* 110:181 */     System.arraycopy(buffer, offset, this.rawCentralDirectoryData, 0, length);
/* 111:189 */     if (length >= 28)
/* 112:    */     {
/* 113:190 */       parseFromLocalFileData(buffer, offset, length);
/* 114:    */     }
/* 115:191 */     else if (length == 24)
/* 116:    */     {
/* 117:192 */       this.size = new ZipEightByteInteger(buffer, offset);
/* 118:193 */       offset += 8;
/* 119:194 */       this.compressedSize = new ZipEightByteInteger(buffer, offset);
/* 120:195 */       offset += 8;
/* 121:196 */       this.relativeHeaderOffset = new ZipEightByteInteger(buffer, offset);
/* 122:    */     }
/* 123:197 */     else if (length % 8 == 4)
/* 124:    */     {
/* 125:198 */       this.diskStart = new ZipLong(buffer, offset + length - 4);
/* 126:    */     }
/* 127:    */   }
/* 128:    */   
/* 129:    */   public void reparseCentralDirectoryData(boolean hasUncompressedSize, boolean hasCompressedSize, boolean hasRelativeHeaderOffset, boolean hasDiskStart)
/* 130:    */     throws ZipException
/* 131:    */   {
/* 132:222 */     if (this.rawCentralDirectoryData != null)
/* 133:    */     {
/* 134:223 */       int expectedLength = (hasUncompressedSize ? 8 : 0) + (hasCompressedSize ? 8 : 0) + (hasRelativeHeaderOffset ? 8 : 0) + (hasDiskStart ? 4 : 0);
/* 135:227 */       if (this.rawCentralDirectoryData.length < expectedLength) {
/* 136:228 */         throw new ZipException("central directory zip64 extended information extra field's length doesn't match central directory data.  Expected length " + expectedLength + " but is " + this.rawCentralDirectoryData.length);
/* 137:    */       }
/* 138:235 */       int offset = 0;
/* 139:236 */       if (hasUncompressedSize)
/* 140:    */       {
/* 141:237 */         this.size = new ZipEightByteInteger(this.rawCentralDirectoryData, offset);
/* 142:238 */         offset += 8;
/* 143:    */       }
/* 144:240 */       if (hasCompressedSize)
/* 145:    */       {
/* 146:241 */         this.compressedSize = new ZipEightByteInteger(this.rawCentralDirectoryData, offset);
/* 147:    */         
/* 148:243 */         offset += 8;
/* 149:    */       }
/* 150:245 */       if (hasRelativeHeaderOffset)
/* 151:    */       {
/* 152:246 */         this.relativeHeaderOffset = new ZipEightByteInteger(this.rawCentralDirectoryData, offset);
/* 153:    */         
/* 154:248 */         offset += 8;
/* 155:    */       }
/* 156:250 */       if (hasDiskStart)
/* 157:    */       {
/* 158:251 */         this.diskStart = new ZipLong(this.rawCentralDirectoryData, offset);
/* 159:252 */         offset += 4;
/* 160:    */       }
/* 161:    */     }
/* 162:    */   }
/* 163:    */   
/* 164:    */   public ZipEightByteInteger getSize()
/* 165:    */   {
/* 166:262 */     return this.size;
/* 167:    */   }
/* 168:    */   
/* 169:    */   public void setSize(ZipEightByteInteger size)
/* 170:    */   {
/* 171:270 */     this.size = size;
/* 172:    */   }
/* 173:    */   
/* 174:    */   public ZipEightByteInteger getCompressedSize()
/* 175:    */   {
/* 176:278 */     return this.compressedSize;
/* 177:    */   }
/* 178:    */   
/* 179:    */   public void setCompressedSize(ZipEightByteInteger compressedSize)
/* 180:    */   {
/* 181:286 */     this.compressedSize = compressedSize;
/* 182:    */   }
/* 183:    */   
/* 184:    */   public ZipEightByteInteger getRelativeHeaderOffset()
/* 185:    */   {
/* 186:294 */     return this.relativeHeaderOffset;
/* 187:    */   }
/* 188:    */   
/* 189:    */   public void setRelativeHeaderOffset(ZipEightByteInteger rho)
/* 190:    */   {
/* 191:302 */     this.relativeHeaderOffset = rho;
/* 192:    */   }
/* 193:    */   
/* 194:    */   public ZipLong getDiskStartNumber()
/* 195:    */   {
/* 196:310 */     return this.diskStart;
/* 197:    */   }
/* 198:    */   
/* 199:    */   public void setDiskStartNumber(ZipLong ds)
/* 200:    */   {
/* 201:318 */     this.diskStart = ds;
/* 202:    */   }
/* 203:    */   
/* 204:    */   private int addSizes(byte[] data)
/* 205:    */   {
/* 206:322 */     int off = 0;
/* 207:323 */     if (this.size != null)
/* 208:    */     {
/* 209:324 */       System.arraycopy(this.size.getBytes(), 0, data, 0, 8);
/* 210:325 */       off += 8;
/* 211:    */     }
/* 212:327 */     if (this.compressedSize != null)
/* 213:    */     {
/* 214:328 */       System.arraycopy(this.compressedSize.getBytes(), 0, data, off, 8);
/* 215:329 */       off += 8;
/* 216:    */     }
/* 217:331 */     return off;
/* 218:    */   }
/* 219:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.Zip64ExtendedInformationExtraField
 * JD-Core Version:    0.7.0.1
 */