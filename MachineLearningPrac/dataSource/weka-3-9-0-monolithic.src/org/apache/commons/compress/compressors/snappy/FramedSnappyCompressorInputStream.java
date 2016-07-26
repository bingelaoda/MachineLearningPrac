/*   1:    */ package org.apache.commons.compress.compressors.snappy;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.io.PushbackInputStream;
/*   6:    */ import java.util.Arrays;
/*   7:    */ import org.apache.commons.compress.compressors.CompressorInputStream;
/*   8:    */ import org.apache.commons.compress.utils.BoundedInputStream;
/*   9:    */ import org.apache.commons.compress.utils.IOUtils;
/*  10:    */ 
/*  11:    */ public class FramedSnappyCompressorInputStream
/*  12:    */   extends CompressorInputStream
/*  13:    */ {
/*  14:    */   static final long MASK_OFFSET = 2726488792L;
/*  15:    */   private static final int STREAM_IDENTIFIER_TYPE = 255;
/*  16:    */   private static final int COMPRESSED_CHUNK_TYPE = 0;
/*  17:    */   private static final int UNCOMPRESSED_CHUNK_TYPE = 1;
/*  18:    */   private static final int PADDING_CHUNK_TYPE = 254;
/*  19:    */   private static final int MIN_UNSKIPPABLE_TYPE = 2;
/*  20:    */   private static final int MAX_UNSKIPPABLE_TYPE = 127;
/*  21:    */   private static final int MAX_SKIPPABLE_TYPE = 253;
/*  22: 52 */   private static final byte[] SZ_SIGNATURE = { -1, 6, 0, 0, 115, 78, 97, 80, 112, 89 };
/*  23:    */   private final PushbackInputStream in;
/*  24:    */   private SnappyCompressorInputStream currentCompressedChunk;
/*  25: 64 */   private final byte[] oneByte = new byte[1];
/*  26:    */   private boolean endReached;
/*  27:    */   private boolean inUncompressedChunk;
/*  28:    */   private int uncompressedBytesRemaining;
/*  29: 69 */   private long expectedChecksum = -1L;
/*  30: 70 */   private final PureJavaCrc32C checksum = new PureJavaCrc32C();
/*  31:    */   
/*  32:    */   public FramedSnappyCompressorInputStream(InputStream in)
/*  33:    */     throws IOException
/*  34:    */   {
/*  35: 78 */     this.in = new PushbackInputStream(in, 1);
/*  36: 79 */     readStreamIdentifier();
/*  37:    */   }
/*  38:    */   
/*  39:    */   public int read()
/*  40:    */     throws IOException
/*  41:    */   {
/*  42: 85 */     return read(this.oneByte, 0, 1) == -1 ? -1 : this.oneByte[0] & 0xFF;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void close()
/*  46:    */     throws IOException
/*  47:    */   {
/*  48: 91 */     if (this.currentCompressedChunk != null)
/*  49:    */     {
/*  50: 92 */       this.currentCompressedChunk.close();
/*  51: 93 */       this.currentCompressedChunk = null;
/*  52:    */     }
/*  53: 95 */     this.in.close();
/*  54:    */   }
/*  55:    */   
/*  56:    */   public int read(byte[] b, int off, int len)
/*  57:    */     throws IOException
/*  58:    */   {
/*  59:101 */     int read = readOnce(b, off, len);
/*  60:102 */     if (read == -1)
/*  61:    */     {
/*  62:103 */       readNextBlock();
/*  63:104 */       if (this.endReached) {
/*  64:105 */         return -1;
/*  65:    */       }
/*  66:107 */       read = readOnce(b, off, len);
/*  67:    */     }
/*  68:109 */     return read;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public int available()
/*  72:    */     throws IOException
/*  73:    */   {
/*  74:115 */     if (this.inUncompressedChunk) {
/*  75:116 */       return Math.min(this.uncompressedBytesRemaining, this.in.available());
/*  76:    */     }
/*  77:118 */     if (this.currentCompressedChunk != null) {
/*  78:119 */       return this.currentCompressedChunk.available();
/*  79:    */     }
/*  80:121 */     return 0;
/*  81:    */   }
/*  82:    */   
/*  83:    */   private int readOnce(byte[] b, int off, int len)
/*  84:    */     throws IOException
/*  85:    */   {
/*  86:132 */     int read = -1;
/*  87:133 */     if (this.inUncompressedChunk)
/*  88:    */     {
/*  89:134 */       int amount = Math.min(this.uncompressedBytesRemaining, len);
/*  90:135 */       if (amount == 0) {
/*  91:136 */         return -1;
/*  92:    */       }
/*  93:138 */       read = this.in.read(b, off, amount);
/*  94:139 */       if (read != -1)
/*  95:    */       {
/*  96:140 */         this.uncompressedBytesRemaining -= read;
/*  97:141 */         count(read);
/*  98:    */       }
/*  99:    */     }
/* 100:143 */     else if (this.currentCompressedChunk != null)
/* 101:    */     {
/* 102:144 */       long before = this.currentCompressedChunk.getBytesRead();
/* 103:145 */       read = this.currentCompressedChunk.read(b, off, len);
/* 104:146 */       if (read == -1)
/* 105:    */       {
/* 106:147 */         this.currentCompressedChunk.close();
/* 107:148 */         this.currentCompressedChunk = null;
/* 108:    */       }
/* 109:    */       else
/* 110:    */       {
/* 111:150 */         count(this.currentCompressedChunk.getBytesRead() - before);
/* 112:    */       }
/* 113:    */     }
/* 114:153 */     if (read > 0) {
/* 115:154 */       this.checksum.update(b, off, read);
/* 116:    */     }
/* 117:156 */     return read;
/* 118:    */   }
/* 119:    */   
/* 120:    */   private void readNextBlock()
/* 121:    */     throws IOException
/* 122:    */   {
/* 123:160 */     verifyLastChecksumAndReset();
/* 124:161 */     this.inUncompressedChunk = false;
/* 125:162 */     int type = readOneByte();
/* 126:163 */     if (type == -1)
/* 127:    */     {
/* 128:164 */       this.endReached = true;
/* 129:    */     }
/* 130:165 */     else if (type == 255)
/* 131:    */     {
/* 132:166 */       this.in.unread(type);
/* 133:167 */       pushedBackBytes(1L);
/* 134:168 */       readStreamIdentifier();
/* 135:169 */       readNextBlock();
/* 136:    */     }
/* 137:170 */     else if ((type == 254) || ((type > 127) && (type <= 253)))
/* 138:    */     {
/* 139:172 */       skipBlock();
/* 140:173 */       readNextBlock();
/* 141:    */     }
/* 142:    */     else
/* 143:    */     {
/* 144:174 */       if ((type >= 2) && (type <= 127)) {
/* 145:175 */         throw new IOException("unskippable chunk with type " + type + " (hex " + Integer.toHexString(type) + ")" + " detected.");
/* 146:    */       }
/* 147:178 */       if (type == 1)
/* 148:    */       {
/* 149:179 */         this.inUncompressedChunk = true;
/* 150:180 */         this.uncompressedBytesRemaining = (readSize() - 4);
/* 151:181 */         this.expectedChecksum = unmask(readCrc());
/* 152:    */       }
/* 153:182 */       else if (type == 0)
/* 154:    */       {
/* 155:183 */         long size = readSize() - 4;
/* 156:184 */         this.expectedChecksum = unmask(readCrc());
/* 157:185 */         this.currentCompressedChunk = new SnappyCompressorInputStream(new BoundedInputStream(this.in, size));
/* 158:    */         
/* 159:    */ 
/* 160:188 */         count(this.currentCompressedChunk.getBytesRead());
/* 161:    */       }
/* 162:    */       else
/* 163:    */       {
/* 164:191 */         throw new IOException("unknown chunk type " + type + " detected.");
/* 165:    */       }
/* 166:    */     }
/* 167:    */   }
/* 168:    */   
/* 169:    */   private long readCrc()
/* 170:    */     throws IOException
/* 171:    */   {
/* 172:197 */     byte[] b = new byte[4];
/* 173:198 */     int read = IOUtils.readFully(this.in, b);
/* 174:199 */     count(read);
/* 175:200 */     if (read != 4) {
/* 176:201 */       throw new IOException("premature end of stream");
/* 177:    */     }
/* 178:203 */     long crc = 0L;
/* 179:204 */     for (int i = 0; i < 4; i++) {
/* 180:205 */       crc |= (b[i] & 0xFF) << 8 * i;
/* 181:    */     }
/* 182:207 */     return crc;
/* 183:    */   }
/* 184:    */   
/* 185:    */   static long unmask(long x)
/* 186:    */   {
/* 187:213 */     x -= 2726488792L;
/* 188:214 */     x &= 0xFFFFFFFF;
/* 189:215 */     return (x >> 17 | x << 15) & 0xFFFFFFFF;
/* 190:    */   }
/* 191:    */   
/* 192:    */   private int readSize()
/* 193:    */     throws IOException
/* 194:    */   {
/* 195:219 */     int b = 0;
/* 196:220 */     int sz = 0;
/* 197:221 */     for (int i = 0; i < 3; i++)
/* 198:    */     {
/* 199:222 */       b = readOneByte();
/* 200:223 */       if (b == -1) {
/* 201:224 */         throw new IOException("premature end of stream");
/* 202:    */       }
/* 203:226 */       sz |= b << i * 8;
/* 204:    */     }
/* 205:228 */     return sz;
/* 206:    */   }
/* 207:    */   
/* 208:    */   private void skipBlock()
/* 209:    */     throws IOException
/* 210:    */   {
/* 211:232 */     int size = readSize();
/* 212:233 */     long read = IOUtils.skip(this.in, size);
/* 213:234 */     count(read);
/* 214:235 */     if (read != size) {
/* 215:236 */       throw new IOException("premature end of stream");
/* 216:    */     }
/* 217:    */   }
/* 218:    */   
/* 219:    */   private void readStreamIdentifier()
/* 220:    */     throws IOException
/* 221:    */   {
/* 222:241 */     byte[] b = new byte[10];
/* 223:242 */     int read = IOUtils.readFully(this.in, b);
/* 224:243 */     count(read);
/* 225:244 */     if ((10 != read) || (!matches(b, 10))) {
/* 226:245 */       throw new IOException("Not a framed Snappy stream");
/* 227:    */     }
/* 228:    */   }
/* 229:    */   
/* 230:    */   private int readOneByte()
/* 231:    */     throws IOException
/* 232:    */   {
/* 233:250 */     int b = this.in.read();
/* 234:251 */     if (b != -1)
/* 235:    */     {
/* 236:252 */       count(1);
/* 237:253 */       return b & 0xFF;
/* 238:    */     }
/* 239:255 */     return -1;
/* 240:    */   }
/* 241:    */   
/* 242:    */   private void verifyLastChecksumAndReset()
/* 243:    */     throws IOException
/* 244:    */   {
/* 245:259 */     if ((this.expectedChecksum >= 0L) && (this.expectedChecksum != this.checksum.getValue())) {
/* 246:260 */       throw new IOException("Checksum verification failed");
/* 247:    */     }
/* 248:262 */     this.expectedChecksum = -1L;
/* 249:263 */     this.checksum.reset();
/* 250:    */   }
/* 251:    */   
/* 252:    */   public static boolean matches(byte[] signature, int length)
/* 253:    */   {
/* 254:277 */     if (length < SZ_SIGNATURE.length) {
/* 255:278 */       return false;
/* 256:    */     }
/* 257:281 */     byte[] shortenedSig = signature;
/* 258:282 */     if (signature.length > SZ_SIGNATURE.length)
/* 259:    */     {
/* 260:283 */       shortenedSig = new byte[SZ_SIGNATURE.length];
/* 261:284 */       System.arraycopy(signature, 0, shortenedSig, 0, SZ_SIGNATURE.length);
/* 262:    */     }
/* 263:287 */     return Arrays.equals(shortenedSig, SZ_SIGNATURE);
/* 264:    */   }
/* 265:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorInputStream
 * JD-Core Version:    0.7.0.1
 */