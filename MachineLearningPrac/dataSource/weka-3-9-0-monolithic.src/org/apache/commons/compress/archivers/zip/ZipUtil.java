/*   1:    */ package org.apache.commons.compress.archivers.zip;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.math.BigInteger;
/*   5:    */ import java.util.Calendar;
/*   6:    */ import java.util.Date;
/*   7:    */ import java.util.zip.CRC32;
/*   8:    */ 
/*   9:    */ public abstract class ZipUtil
/*  10:    */ {
/*  11: 35 */   private static final byte[] DOS_TIME_MIN = ZipLong.getBytes(8448L);
/*  12:    */   
/*  13:    */   public static ZipLong toDosTime(Date time)
/*  14:    */   {
/*  15: 43 */     return new ZipLong(toDosTime(time.getTime()));
/*  16:    */   }
/*  17:    */   
/*  18:    */   public static byte[] toDosTime(long t)
/*  19:    */   {
/*  20: 54 */     byte[] result = new byte[4];
/*  21: 55 */     toDosTime(t, result, 0);
/*  22: 56 */     return result;
/*  23:    */   }
/*  24:    */   
/*  25:    */   public static void toDosTime(long t, byte[] buf, int offset)
/*  26:    */   {
/*  27: 70 */     toDosTime(Calendar.getInstance(), t, buf, offset);
/*  28:    */   }
/*  29:    */   
/*  30:    */   static void toDosTime(Calendar c, long t, byte[] buf, int offset)
/*  31:    */   {
/*  32: 74 */     c.setTimeInMillis(t);
/*  33:    */     
/*  34: 76 */     int year = c.get(1);
/*  35: 77 */     if (year < 1980)
/*  36:    */     {
/*  37: 78 */       System.arraycopy(DOS_TIME_MIN, 0, buf, offset, DOS_TIME_MIN.length);
/*  38: 79 */       return;
/*  39:    */     }
/*  40: 81 */     int month = c.get(2) + 1;
/*  41: 82 */     long value = year - 1980 << 25 | month << 21 | c.get(5) << 16 | c.get(11) << 11 | c.get(12) << 5 | c.get(13) >> 1;
/*  42:    */     
/*  43:    */ 
/*  44:    */ 
/*  45:    */ 
/*  46:    */ 
/*  47: 88 */     ZipLong.putLong(value, buf, offset);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public static long adjustToLong(int i)
/*  51:    */   {
/*  52:100 */     if (i < 0) {
/*  53:101 */       return 4294967296L + i;
/*  54:    */     }
/*  55:103 */     return i;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public static byte[] reverse(byte[] array)
/*  59:    */   {
/*  60:119 */     int z = array.length - 1;
/*  61:120 */     for (int i = 0; i < array.length / 2; i++)
/*  62:    */     {
/*  63:121 */       byte x = array[i];
/*  64:122 */       array[i] = array[(z - i)];
/*  65:123 */       array[(z - i)] = x;
/*  66:    */     }
/*  67:125 */     return array;
/*  68:    */   }
/*  69:    */   
/*  70:    */   static long bigToLong(BigInteger big)
/*  71:    */   {
/*  72:136 */     if (big.bitLength() <= 63) {
/*  73:137 */       return big.longValue();
/*  74:    */     }
/*  75:139 */     throw new NumberFormatException("The BigInteger cannot fit inside a 64 bit java long: [" + big + "]");
/*  76:    */   }
/*  77:    */   
/*  78:    */   static BigInteger longToBig(long l)
/*  79:    */   {
/*  80:155 */     if (l < -2147483648L) {
/*  81:156 */       throw new IllegalArgumentException("Negative longs < -2^31 not permitted: [" + l + "]");
/*  82:    */     }
/*  83:157 */     if ((l < 0L) && (l >= -2147483648L)) {
/*  84:160 */       l = adjustToLong((int)l);
/*  85:    */     }
/*  86:162 */     return BigInteger.valueOf(l);
/*  87:    */   }
/*  88:    */   
/*  89:    */   public static int signedByteToUnsignedInt(byte b)
/*  90:    */   {
/*  91:174 */     if (b >= 0) {
/*  92:175 */       return b;
/*  93:    */     }
/*  94:177 */     return 256 + b;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public static byte unsignedIntToSignedByte(int i)
/*  98:    */   {
/*  99:190 */     if ((i > 255) || (i < 0)) {
/* 100:191 */       throw new IllegalArgumentException("Can only convert non-negative integers between [0,255] to byte: [" + i + "]");
/* 101:    */     }
/* 102:193 */     if (i < 128) {
/* 103:194 */       return (byte)i;
/* 104:    */     }
/* 105:196 */     return (byte)(i - 256);
/* 106:    */   }
/* 107:    */   
/* 108:    */   public static Date fromDosTime(ZipLong zipDosTime)
/* 109:    */   {
/* 110:207 */     long dosTime = zipDosTime.getValue();
/* 111:208 */     return new Date(dosToJavaTime(dosTime));
/* 112:    */   }
/* 113:    */   
/* 114:    */   public static long dosToJavaTime(long dosTime)
/* 115:    */   {
/* 116:218 */     Calendar cal = Calendar.getInstance();
/* 117:    */     
/* 118:220 */     cal.set(1, (int)(dosTime >> 25 & 0x7F) + 1980);
/* 119:221 */     cal.set(2, (int)(dosTime >> 21 & 0xF) - 1);
/* 120:222 */     cal.set(5, (int)(dosTime >> 16) & 0x1F);
/* 121:223 */     cal.set(11, (int)(dosTime >> 11) & 0x1F);
/* 122:224 */     cal.set(12, (int)(dosTime >> 5) & 0x3F);
/* 123:225 */     cal.set(13, (int)(dosTime << 1) & 0x3E);
/* 124:226 */     cal.set(14, 0);
/* 125:    */     
/* 126:228 */     return cal.getTime().getTime();
/* 127:    */   }
/* 128:    */   
/* 129:    */   static void setNameAndCommentFromExtraFields(ZipArchiveEntry ze, byte[] originalNameBytes, byte[] commentBytes)
/* 130:    */   {
/* 131:239 */     UnicodePathExtraField name = (UnicodePathExtraField)ze.getExtraField(UnicodePathExtraField.UPATH_ID);
/* 132:    */     
/* 133:241 */     String originalName = ze.getName();
/* 134:242 */     String newName = getUnicodeStringIfOriginalMatches(name, originalNameBytes);
/* 135:244 */     if ((newName != null) && (!originalName.equals(newName))) {
/* 136:245 */       ze.setName(newName);
/* 137:    */     }
/* 138:248 */     if ((commentBytes != null) && (commentBytes.length > 0))
/* 139:    */     {
/* 140:249 */       UnicodeCommentExtraField cmt = (UnicodeCommentExtraField)ze.getExtraField(UnicodeCommentExtraField.UCOM_ID);
/* 141:    */       
/* 142:251 */       String newComment = getUnicodeStringIfOriginalMatches(cmt, commentBytes);
/* 143:253 */       if (newComment != null) {
/* 144:254 */         ze.setComment(newComment);
/* 145:    */       }
/* 146:    */     }
/* 147:    */   }
/* 148:    */   
/* 149:    */   private static String getUnicodeStringIfOriginalMatches(AbstractUnicodeExtraField f, byte[] orig)
/* 150:    */   {
/* 151:269 */     if (f != null)
/* 152:    */     {
/* 153:270 */       CRC32 crc32 = new CRC32();
/* 154:271 */       crc32.update(orig);
/* 155:272 */       long origCRC32 = crc32.getValue();
/* 156:274 */       if (origCRC32 == f.getNameCRC32()) {
/* 157:    */         try
/* 158:    */         {
/* 159:276 */           return ZipEncodingHelper.UTF8_ZIP_ENCODING.decode(f.getUnicodeName());
/* 160:    */         }
/* 161:    */         catch (IOException ex)
/* 162:    */         {
/* 163:283 */           return null;
/* 164:    */         }
/* 165:    */       }
/* 166:    */     }
/* 167:287 */     return null;
/* 168:    */   }
/* 169:    */   
/* 170:    */   static byte[] copy(byte[] from)
/* 171:    */   {
/* 172:295 */     if (from != null)
/* 173:    */     {
/* 174:296 */       byte[] to = new byte[from.length];
/* 175:297 */       System.arraycopy(from, 0, to, 0, to.length);
/* 176:298 */       return to;
/* 177:    */     }
/* 178:300 */     return null;
/* 179:    */   }
/* 180:    */   
/* 181:    */   static void copy(byte[] from, byte[] to, int offset)
/* 182:    */   {
/* 183:303 */     if (from != null) {
/* 184:304 */       System.arraycopy(from, 0, to, offset, from.length);
/* 185:    */     }
/* 186:    */   }
/* 187:    */   
/* 188:    */   static boolean canHandleEntryData(ZipArchiveEntry entry)
/* 189:    */   {
/* 190:313 */     return (supportsEncryptionOf(entry)) && (supportsMethodOf(entry));
/* 191:    */   }
/* 192:    */   
/* 193:    */   private static boolean supportsEncryptionOf(ZipArchiveEntry entry)
/* 194:    */   {
/* 195:323 */     return !entry.getGeneralPurposeBit().usesEncryption();
/* 196:    */   }
/* 197:    */   
/* 198:    */   private static boolean supportsMethodOf(ZipArchiveEntry entry)
/* 199:    */   {
/* 200:333 */     return (entry.getMethod() == 0) || (entry.getMethod() == ZipMethod.UNSHRINKING.getCode()) || (entry.getMethod() == ZipMethod.IMPLODING.getCode()) || (entry.getMethod() == 8);
/* 201:    */   }
/* 202:    */   
/* 203:    */   static void checkRequestedFeatures(ZipArchiveEntry ze)
/* 204:    */     throws UnsupportedZipFeatureException
/* 205:    */   {
/* 206:345 */     if (!supportsEncryptionOf(ze)) {
/* 207:346 */       throw new UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature.ENCRYPTION, ze);
/* 208:    */     }
/* 209:350 */     if (!supportsMethodOf(ze))
/* 210:    */     {
/* 211:351 */       ZipMethod m = ZipMethod.getMethodByCode(ze.getMethod());
/* 212:352 */       if (m == null) {
/* 213:353 */         throw new UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature.METHOD, ze);
/* 214:    */       }
/* 215:357 */       throw new UnsupportedZipFeatureException(m, ze);
/* 216:    */     }
/* 217:    */   }
/* 218:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.ZipUtil
 * JD-Core Version:    0.7.0.1
 */