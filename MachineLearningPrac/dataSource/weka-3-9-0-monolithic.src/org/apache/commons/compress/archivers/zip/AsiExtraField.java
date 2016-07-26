/*   1:    */ package org.apache.commons.compress.archivers.zip;
/*   2:    */ 
/*   3:    */ import java.util.zip.CRC32;
/*   4:    */ import java.util.zip.ZipException;
/*   5:    */ 
/*   6:    */ public class AsiExtraField
/*   7:    */   implements ZipExtraField, UnixStat, Cloneable
/*   8:    */ {
/*   9: 54 */   private static final ZipShort HEADER_ID = new ZipShort(30062);
/*  10:    */   private static final int WORD = 4;
/*  11: 59 */   private int mode = 0;
/*  12: 63 */   private int uid = 0;
/*  13: 67 */   private int gid = 0;
/*  14: 73 */   private String link = "";
/*  15: 77 */   private boolean dirFlag = false;
/*  16: 82 */   private CRC32 crc = new CRC32();
/*  17:    */   
/*  18:    */   public ZipShort getHeaderId()
/*  19:    */   {
/*  20: 93 */     return HEADER_ID;
/*  21:    */   }
/*  22:    */   
/*  23:    */   public ZipShort getLocalFileDataLength()
/*  24:    */   {
/*  25:102 */     return new ZipShort(14 + getLinkedFile().getBytes().length);
/*  26:    */   }
/*  27:    */   
/*  28:    */   public ZipShort getCentralDirectoryLength()
/*  29:    */   {
/*  30:116 */     return getLocalFileDataLength();
/*  31:    */   }
/*  32:    */   
/*  33:    */   public byte[] getLocalFileDataData()
/*  34:    */   {
/*  35:126 */     byte[] data = new byte[getLocalFileDataLength().getValue() - 4];
/*  36:127 */     System.arraycopy(ZipShort.getBytes(getMode()), 0, data, 0, 2);
/*  37:    */     
/*  38:129 */     byte[] linkArray = getLinkedFile().getBytes();
/*  39:    */     
/*  40:131 */     System.arraycopy(ZipLong.getBytes(linkArray.length), 0, data, 2, 4);
/*  41:    */     
/*  42:    */ 
/*  43:134 */     System.arraycopy(ZipShort.getBytes(getUserId()), 0, data, 6, 2);
/*  44:    */     
/*  45:136 */     System.arraycopy(ZipShort.getBytes(getGroupId()), 0, data, 8, 2);
/*  46:    */     
/*  47:    */ 
/*  48:139 */     System.arraycopy(linkArray, 0, data, 10, linkArray.length);
/*  49:    */     
/*  50:    */ 
/*  51:142 */     this.crc.reset();
/*  52:143 */     this.crc.update(data);
/*  53:144 */     long checksum = this.crc.getValue();
/*  54:    */     
/*  55:146 */     byte[] result = new byte[data.length + 4];
/*  56:147 */     System.arraycopy(ZipLong.getBytes(checksum), 0, result, 0, 4);
/*  57:148 */     System.arraycopy(data, 0, result, 4, data.length);
/*  58:149 */     return result;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public byte[] getCentralDirectoryData()
/*  62:    */   {
/*  63:157 */     return getLocalFileDataData();
/*  64:    */   }
/*  65:    */   
/*  66:    */   public void setUserId(int uid)
/*  67:    */   {
/*  68:165 */     this.uid = uid;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public int getUserId()
/*  72:    */   {
/*  73:173 */     return this.uid;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void setGroupId(int gid)
/*  77:    */   {
/*  78:181 */     this.gid = gid;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public int getGroupId()
/*  82:    */   {
/*  83:189 */     return this.gid;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public void setLinkedFile(String name)
/*  87:    */   {
/*  88:199 */     this.link = name;
/*  89:200 */     this.mode = getMode(this.mode);
/*  90:    */   }
/*  91:    */   
/*  92:    */   public String getLinkedFile()
/*  93:    */   {
/*  94:210 */     return this.link;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public boolean isLink()
/*  98:    */   {
/*  99:218 */     return getLinkedFile().length() != 0;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public void setMode(int mode)
/* 103:    */   {
/* 104:226 */     this.mode = getMode(mode);
/* 105:    */   }
/* 106:    */   
/* 107:    */   public int getMode()
/* 108:    */   {
/* 109:234 */     return this.mode;
/* 110:    */   }
/* 111:    */   
/* 112:    */   public void setDirectory(boolean dirFlag)
/* 113:    */   {
/* 114:242 */     this.dirFlag = dirFlag;
/* 115:243 */     this.mode = getMode(this.mode);
/* 116:    */   }
/* 117:    */   
/* 118:    */   public boolean isDirectory()
/* 119:    */   {
/* 120:251 */     return (this.dirFlag) && (!isLink());
/* 121:    */   }
/* 122:    */   
/* 123:    */   public void parseFromLocalFileData(byte[] data, int offset, int length)
/* 124:    */     throws ZipException
/* 125:    */   {
/* 126:264 */     long givenChecksum = ZipLong.getValue(data, offset);
/* 127:265 */     byte[] tmp = new byte[length - 4];
/* 128:266 */     System.arraycopy(data, offset + 4, tmp, 0, length - 4);
/* 129:267 */     this.crc.reset();
/* 130:268 */     this.crc.update(tmp);
/* 131:269 */     long realChecksum = this.crc.getValue();
/* 132:270 */     if (givenChecksum != realChecksum) {
/* 133:271 */       throw new ZipException("bad CRC checksum " + Long.toHexString(givenChecksum) + " instead of " + Long.toHexString(realChecksum));
/* 134:    */     }
/* 135:277 */     int newMode = ZipShort.getValue(tmp, 0);
/* 136:    */     
/* 137:279 */     byte[] linkArray = new byte[(int)ZipLong.getValue(tmp, 2)];
/* 138:280 */     this.uid = ZipShort.getValue(tmp, 6);
/* 139:281 */     this.gid = ZipShort.getValue(tmp, 8);
/* 140:283 */     if (linkArray.length == 0)
/* 141:    */     {
/* 142:284 */       this.link = "";
/* 143:    */     }
/* 144:    */     else
/* 145:    */     {
/* 146:286 */       System.arraycopy(tmp, 10, linkArray, 0, linkArray.length);
/* 147:287 */       this.link = new String(linkArray);
/* 148:    */     }
/* 149:290 */     setDirectory((newMode & 0x4000) != 0);
/* 150:291 */     setMode(newMode);
/* 151:    */   }
/* 152:    */   
/* 153:    */   public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length)
/* 154:    */     throws ZipException
/* 155:    */   {
/* 156:301 */     parseFromLocalFileData(buffer, offset, length);
/* 157:    */   }
/* 158:    */   
/* 159:    */   protected int getMode(int mode)
/* 160:    */   {
/* 161:310 */     int type = 32768;
/* 162:311 */     if (isLink()) {
/* 163:312 */       type = 40960;
/* 164:313 */     } else if (isDirectory()) {
/* 165:314 */       type = 16384;
/* 166:    */     }
/* 167:316 */     return type | mode & 0xFFF;
/* 168:    */   }
/* 169:    */   
/* 170:    */   public Object clone()
/* 171:    */   {
/* 172:    */     try
/* 173:    */     {
/* 174:322 */       AsiExtraField cloned = (AsiExtraField)super.clone();
/* 175:323 */       cloned.crc = new CRC32();
/* 176:324 */       return cloned;
/* 177:    */     }
/* 178:    */     catch (CloneNotSupportedException cnfe)
/* 179:    */     {
/* 180:327 */       throw new RuntimeException(cnfe);
/* 181:    */     }
/* 182:    */   }
/* 183:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.AsiExtraField
 * JD-Core Version:    0.7.0.1
 */