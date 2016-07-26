/*   1:    */ package org.apache.commons.compress.archivers.zip;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.math.BigInteger;
/*   5:    */ import java.util.zip.ZipException;
/*   6:    */ 
/*   7:    */ public class X7875_NewUnix
/*   8:    */   implements ZipExtraField, Cloneable, Serializable
/*   9:    */ {
/*  10: 48 */   private static final ZipShort HEADER_ID = new ZipShort(30837);
/*  11: 49 */   private static final BigInteger ONE_THOUSAND = BigInteger.valueOf(1000L);
/*  12:    */   private static final long serialVersionUID = 1L;
/*  13: 52 */   private int version = 1;
/*  14:    */   private BigInteger uid;
/*  15:    */   private BigInteger gid;
/*  16:    */   
/*  17:    */   public X7875_NewUnix()
/*  18:    */   {
/*  19: 66 */     reset();
/*  20:    */   }
/*  21:    */   
/*  22:    */   public ZipShort getHeaderId()
/*  23:    */   {
/*  24: 75 */     return HEADER_ID;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public long getUID()
/*  28:    */   {
/*  29: 86 */     return ZipUtil.bigToLong(this.uid);
/*  30:    */   }
/*  31:    */   
/*  32:    */   public long getGID()
/*  33:    */   {
/*  34: 96 */     return ZipUtil.bigToLong(this.gid);
/*  35:    */   }
/*  36:    */   
/*  37:    */   public void setUID(long l)
/*  38:    */   {
/*  39:104 */     this.uid = ZipUtil.longToBig(l);
/*  40:    */   }
/*  41:    */   
/*  42:    */   public void setGID(long l)
/*  43:    */   {
/*  44:113 */     this.gid = ZipUtil.longToBig(l);
/*  45:    */   }
/*  46:    */   
/*  47:    */   public ZipShort getLocalFileDataLength()
/*  48:    */   {
/*  49:123 */     int uidSize = trimLeadingZeroesForceMinLength(this.uid.toByteArray()).length;
/*  50:124 */     int gidSize = trimLeadingZeroesForceMinLength(this.gid.toByteArray()).length;
/*  51:    */     
/*  52:    */ 
/*  53:127 */     return new ZipShort(3 + uidSize + gidSize);
/*  54:    */   }
/*  55:    */   
/*  56:    */   public ZipShort getCentralDirectoryLength()
/*  57:    */   {
/*  58:137 */     return getLocalFileDataLength();
/*  59:    */   }
/*  60:    */   
/*  61:    */   public byte[] getLocalFileDataData()
/*  62:    */   {
/*  63:147 */     byte[] uidBytes = this.uid.toByteArray();
/*  64:148 */     byte[] gidBytes = this.gid.toByteArray();
/*  65:    */     
/*  66:    */ 
/*  67:    */ 
/*  68:    */ 
/*  69:153 */     uidBytes = trimLeadingZeroesForceMinLength(uidBytes);
/*  70:154 */     gidBytes = trimLeadingZeroesForceMinLength(gidBytes);
/*  71:    */     
/*  72:    */ 
/*  73:    */ 
/*  74:    */ 
/*  75:    */ 
/*  76:    */ 
/*  77:161 */     byte[] data = new byte[3 + uidBytes.length + gidBytes.length];
/*  78:    */     
/*  79:    */ 
/*  80:164 */     ZipUtil.reverse(uidBytes);
/*  81:165 */     ZipUtil.reverse(gidBytes);
/*  82:    */     
/*  83:167 */     int pos = 0;
/*  84:168 */     data[(pos++)] = ZipUtil.unsignedIntToSignedByte(this.version);
/*  85:169 */     data[(pos++)] = ZipUtil.unsignedIntToSignedByte(uidBytes.length);
/*  86:170 */     System.arraycopy(uidBytes, 0, data, pos, uidBytes.length);
/*  87:171 */     pos += uidBytes.length;
/*  88:172 */     data[(pos++)] = ZipUtil.unsignedIntToSignedByte(gidBytes.length);
/*  89:173 */     System.arraycopy(gidBytes, 0, data, pos, gidBytes.length);
/*  90:174 */     return data;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public byte[] getCentralDirectoryData()
/*  94:    */   {
/*  95:184 */     return getLocalFileDataData();
/*  96:    */   }
/*  97:    */   
/*  98:    */   public void parseFromLocalFileData(byte[] data, int offset, int length)
/*  99:    */     throws ZipException
/* 100:    */   {
/* 101:198 */     reset();
/* 102:199 */     this.version = ZipUtil.signedByteToUnsignedInt(data[(offset++)]);
/* 103:200 */     int uidSize = ZipUtil.signedByteToUnsignedInt(data[(offset++)]);
/* 104:201 */     byte[] uidBytes = new byte[uidSize];
/* 105:202 */     System.arraycopy(data, offset, uidBytes, 0, uidSize);
/* 106:203 */     offset += uidSize;
/* 107:204 */     this.uid = new BigInteger(1, ZipUtil.reverse(uidBytes));
/* 108:    */     
/* 109:206 */     int gidSize = ZipUtil.signedByteToUnsignedInt(data[(offset++)]);
/* 110:207 */     byte[] gidBytes = new byte[gidSize];
/* 111:208 */     System.arraycopy(data, offset, gidBytes, 0, gidSize);
/* 112:209 */     this.gid = new BigInteger(1, ZipUtil.reverse(gidBytes));
/* 113:    */   }
/* 114:    */   
/* 115:    */   public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length)
/* 116:    */     throws ZipException
/* 117:    */   {
/* 118:219 */     reset();
/* 119:220 */     parseFromLocalFileData(buffer, offset, length);
/* 120:    */   }
/* 121:    */   
/* 122:    */   private void reset()
/* 123:    */   {
/* 124:229 */     this.uid = ONE_THOUSAND;
/* 125:230 */     this.gid = ONE_THOUSAND;
/* 126:    */   }
/* 127:    */   
/* 128:    */   public String toString()
/* 129:    */   {
/* 130:242 */     return "0x7875 Zip Extra Field: UID=" + this.uid + " GID=" + this.gid;
/* 131:    */   }
/* 132:    */   
/* 133:    */   public Object clone()
/* 134:    */     throws CloneNotSupportedException
/* 135:    */   {
/* 136:247 */     return super.clone();
/* 137:    */   }
/* 138:    */   
/* 139:    */   public boolean equals(Object o)
/* 140:    */   {
/* 141:252 */     if ((o instanceof X7875_NewUnix))
/* 142:    */     {
/* 143:253 */       X7875_NewUnix xf = (X7875_NewUnix)o;
/* 144:    */       
/* 145:255 */       return (this.version == xf.version) && (this.uid.equals(xf.uid)) && (this.gid.equals(xf.gid));
/* 146:    */     }
/* 147:257 */     return false;
/* 148:    */   }
/* 149:    */   
/* 150:    */   public int hashCode()
/* 151:    */   {
/* 152:262 */     int hc = -1234567 * this.version;
/* 153:    */     
/* 154:    */ 
/* 155:    */ 
/* 156:266 */     hc ^= Integer.rotateLeft(this.uid.hashCode(), 16);
/* 157:267 */     hc ^= this.gid.hashCode();
/* 158:268 */     return hc;
/* 159:    */   }
/* 160:    */   
/* 161:    */   static byte[] trimLeadingZeroesForceMinLength(byte[] array)
/* 162:    */   {
/* 163:281 */     if (array == null) {
/* 164:282 */       return array;
/* 165:    */     }
/* 166:285 */     int pos = 0;
/* 167:286 */     for (byte b : array)
/* 168:    */     {
/* 169:287 */       if (b != 0) {
/* 170:    */         break;
/* 171:    */       }
/* 172:288 */       pos++;
/* 173:    */     }
/* 174:331 */     int MIN_LENGTH = 1;
/* 175:    */     
/* 176:333 */     byte[] trimmedArray = new byte[Math.max(1, array.length - pos)];
/* 177:334 */     int startPos = trimmedArray.length - (array.length - pos);
/* 178:335 */     System.arraycopy(array, pos, trimmedArray, startPos, trimmedArray.length - startPos);
/* 179:336 */     return trimmedArray;
/* 180:    */   }
/* 181:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.X7875_NewUnix
 * JD-Core Version:    0.7.0.1
 */