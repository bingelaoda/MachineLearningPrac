/*   1:    */ package org.apache.commons.compress.archivers.dump;
/*   2:    */ 
/*   3:    */ import java.io.FilterInputStream;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.InputStream;
/*   6:    */ import java.util.Arrays;
/*   7:    */ import java.util.zip.DataFormatException;
/*   8:    */ import java.util.zip.Inflater;
/*   9:    */ import org.apache.commons.compress.utils.IOUtils;
/*  10:    */ 
/*  11:    */ class TapeInputStream
/*  12:    */   extends FilterInputStream
/*  13:    */ {
/*  14: 38 */   private byte[] blockBuffer = new byte[1024];
/*  15: 39 */   private int currBlkIdx = -1;
/*  16: 40 */   private int blockSize = 1024;
/*  17:    */   private static final int recordSize = 1024;
/*  18: 42 */   private int readOffset = 1024;
/*  19: 43 */   private boolean isCompressed = false;
/*  20: 44 */   private long bytesRead = 0L;
/*  21:    */   
/*  22:    */   public TapeInputStream(InputStream in)
/*  23:    */   {
/*  24: 50 */     super(in);
/*  25:    */   }
/*  26:    */   
/*  27:    */   public void resetBlockSize(int recsPerBlock, boolean isCompressed)
/*  28:    */     throws IOException
/*  29:    */   {
/*  30: 69 */     this.isCompressed = isCompressed;
/*  31:    */     
/*  32: 71 */     this.blockSize = (1024 * recsPerBlock);
/*  33:    */     
/*  34:    */ 
/*  35: 74 */     byte[] oldBuffer = this.blockBuffer;
/*  36:    */     
/*  37:    */ 
/*  38: 77 */     this.blockBuffer = new byte[this.blockSize];
/*  39: 78 */     System.arraycopy(oldBuffer, 0, this.blockBuffer, 0, 1024);
/*  40: 79 */     readFully(this.blockBuffer, 1024, this.blockSize - 1024);
/*  41:    */     
/*  42: 81 */     this.currBlkIdx = 0;
/*  43: 82 */     this.readOffset = 1024;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public int available()
/*  47:    */     throws IOException
/*  48:    */   {
/*  49: 90 */     if (this.readOffset < this.blockSize) {
/*  50: 91 */       return this.blockSize - this.readOffset;
/*  51:    */     }
/*  52: 94 */     return this.in.available();
/*  53:    */   }
/*  54:    */   
/*  55:    */   public int read()
/*  56:    */     throws IOException
/*  57:    */   {
/*  58:102 */     throw new IllegalArgumentException("all reads must be multiple of record size (1024 bytes.");
/*  59:    */   }
/*  60:    */   
/*  61:    */   public int read(byte[] b, int off, int len)
/*  62:    */     throws IOException
/*  63:    */   {
/*  64:117 */     if (len % 1024 != 0) {
/*  65:118 */       throw new IllegalArgumentException("all reads must be multiple of record size (1024 bytes.");
/*  66:    */     }
/*  67:123 */     int bytes = 0;
/*  68:125 */     while (bytes < len)
/*  69:    */     {
/*  70:129 */       if ((this.readOffset == this.blockSize) && (!readBlock(true))) {
/*  71:130 */         return -1;
/*  72:    */       }
/*  73:133 */       int n = 0;
/*  74:135 */       if (this.readOffset + (len - bytes) <= this.blockSize) {
/*  75:137 */         n = len - bytes;
/*  76:    */       } else {
/*  77:140 */         n = this.blockSize - this.readOffset;
/*  78:    */       }
/*  79:144 */       System.arraycopy(this.blockBuffer, this.readOffset, b, off, n);
/*  80:145 */       this.readOffset += n;
/*  81:146 */       bytes += n;
/*  82:147 */       off += n;
/*  83:    */     }
/*  84:150 */     return bytes;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public long skip(long len)
/*  88:    */     throws IOException
/*  89:    */   {
/*  90:163 */     if (len % 1024L != 0L) {
/*  91:164 */       throw new IllegalArgumentException("all reads must be multiple of record size (1024 bytes.");
/*  92:    */     }
/*  93:169 */     long bytes = 0L;
/*  94:171 */     while (bytes < len)
/*  95:    */     {
/*  96:176 */       if (this.readOffset == this.blockSize) {
/*  97:176 */         if (!readBlock(len - bytes < this.blockSize)) {
/*  98:178 */           return -1L;
/*  99:    */         }
/* 100:    */       }
/* 101:181 */       long n = 0L;
/* 102:183 */       if (this.readOffset + (len - bytes) <= this.blockSize) {
/* 103:185 */         n = len - bytes;
/* 104:    */       } else {
/* 105:188 */         n = this.blockSize - this.readOffset;
/* 106:    */       }
/* 107:192 */       this.readOffset = ((int)(this.readOffset + n));
/* 108:193 */       bytes += n;
/* 109:    */     }
/* 110:196 */     return bytes;
/* 111:    */   }
/* 112:    */   
/* 113:    */   public void close()
/* 114:    */     throws IOException
/* 115:    */   {
/* 116:206 */     if ((this.in != null) && (this.in != System.in)) {
/* 117:207 */       this.in.close();
/* 118:    */     }
/* 119:    */   }
/* 120:    */   
/* 121:    */   public byte[] peek()
/* 122:    */     throws IOException
/* 123:    */   {
/* 124:221 */     if ((this.readOffset == this.blockSize) && (!readBlock(true))) {
/* 125:222 */       return null;
/* 126:    */     }
/* 127:226 */     byte[] b = new byte[1024];
/* 128:227 */     System.arraycopy(this.blockBuffer, this.readOffset, b, 0, b.length);
/* 129:    */     
/* 130:229 */     return b;
/* 131:    */   }
/* 132:    */   
/* 133:    */   public byte[] readRecord()
/* 134:    */     throws IOException
/* 135:    */   {
/* 136:239 */     byte[] result = new byte[1024];
/* 137:243 */     if (-1 == read(result, 0, result.length)) {
/* 138:244 */       throw new ShortFileException();
/* 139:    */     }
/* 140:247 */     return result;
/* 141:    */   }
/* 142:    */   
/* 143:    */   private boolean readBlock(boolean decompress)
/* 144:    */     throws IOException
/* 145:    */   {
/* 146:258 */     boolean success = true;
/* 147:260 */     if (this.in == null) {
/* 148:261 */       throw new IOException("input buffer is closed");
/* 149:    */     }
/* 150:264 */     if ((!this.isCompressed) || (this.currBlkIdx == -1))
/* 151:    */     {
/* 152:266 */       success = readFully(this.blockBuffer, 0, this.blockSize);
/* 153:267 */       this.bytesRead += this.blockSize;
/* 154:    */     }
/* 155:    */     else
/* 156:    */     {
/* 157:269 */       if (!readFully(this.blockBuffer, 0, 4)) {
/* 158:270 */         return false;
/* 159:    */       }
/* 160:272 */       this.bytesRead += 4L;
/* 161:    */       
/* 162:274 */       int h = DumpArchiveUtil.convert32(this.blockBuffer, 0);
/* 163:275 */       boolean compressed = (h & 0x1) == 1;
/* 164:277 */       if (!compressed)
/* 165:    */       {
/* 166:279 */         success = readFully(this.blockBuffer, 0, this.blockSize);
/* 167:280 */         this.bytesRead += this.blockSize;
/* 168:    */       }
/* 169:    */       else
/* 170:    */       {
/* 171:283 */         int flags = h >> 1 & 0x7;
/* 172:284 */         int length = h >> 4 & 0xFFFFFFF;
/* 173:285 */         byte[] compBuffer = new byte[length];
/* 174:286 */         success = readFully(compBuffer, 0, length);
/* 175:287 */         this.bytesRead += length;
/* 176:289 */         if (!decompress) {
/* 177:291 */           Arrays.fill(this.blockBuffer, (byte)0);
/* 178:    */         } else {
/* 179:293 */           switch (1.$SwitchMap$org$apache$commons$compress$archivers$dump$DumpArchiveConstants$COMPRESSION_TYPE[DumpArchiveConstants.COMPRESSION_TYPE.find(flags & 0x3).ordinal()])
/* 180:    */           {
/* 181:    */           case 1: 
/* 182:    */             try
/* 183:    */             {
/* 184:298 */               Inflater inflator = new Inflater();
/* 185:299 */               inflator.setInput(compBuffer, 0, compBuffer.length);
/* 186:300 */               length = inflator.inflate(this.blockBuffer);
/* 187:302 */               if (length != this.blockSize) {
/* 188:303 */                 throw new ShortFileException();
/* 189:    */               }
/* 190:306 */               inflator.end();
/* 191:    */             }
/* 192:    */             catch (DataFormatException e)
/* 193:    */             {
/* 194:308 */               throw new DumpArchiveException("bad data", e);
/* 195:    */             }
/* 196:    */           case 2: 
/* 197:314 */             throw new UnsupportedCompressionAlgorithmException("BZLIB2");
/* 198:    */           case 3: 
/* 199:318 */             throw new UnsupportedCompressionAlgorithmException("LZO");
/* 200:    */           default: 
/* 201:322 */             throw new UnsupportedCompressionAlgorithmException();
/* 202:    */           }
/* 203:    */         }
/* 204:    */       }
/* 205:    */     }
/* 206:328 */     this.currBlkIdx += 1;
/* 207:329 */     this.readOffset = 0;
/* 208:    */     
/* 209:331 */     return success;
/* 210:    */   }
/* 211:    */   
/* 212:    */   private boolean readFully(byte[] b, int off, int len)
/* 213:    */     throws IOException
/* 214:    */   {
/* 215:339 */     int count = IOUtils.readFully(this.in, b, off, len);
/* 216:340 */     if (count < len) {
/* 217:341 */       throw new ShortFileException();
/* 218:    */     }
/* 219:344 */     return true;
/* 220:    */   }
/* 221:    */   
/* 222:    */   public long getBytesRead()
/* 223:    */   {
/* 224:351 */     return this.bytesRead;
/* 225:    */   }
/* 226:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.dump.TapeInputStream
 * JD-Core Version:    0.7.0.1
 */