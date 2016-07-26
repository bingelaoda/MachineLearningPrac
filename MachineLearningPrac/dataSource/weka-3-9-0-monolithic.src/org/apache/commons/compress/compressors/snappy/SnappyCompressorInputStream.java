/*   1:    */ package org.apache.commons.compress.compressors.snappy;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import org.apache.commons.compress.compressors.CompressorInputStream;
/*   6:    */ import org.apache.commons.compress.utils.IOUtils;
/*   7:    */ 
/*   8:    */ public class SnappyCompressorInputStream
/*   9:    */   extends CompressorInputStream
/*  10:    */ {
/*  11:    */   private static final int TAG_MASK = 3;
/*  12:    */   public static final int DEFAULT_BLOCK_SIZE = 32768;
/*  13:    */   private final byte[] decompressBuf;
/*  14:    */   private int writeIndex;
/*  15:    */   private int readIndex;
/*  16:    */   private final int blockSize;
/*  17:    */   private final InputStream in;
/*  18:    */   private final int size;
/*  19:    */   private int uncompressedBytesRemaining;
/*  20: 71 */   private final byte[] oneByte = new byte[1];
/*  21: 73 */   private boolean endReached = false;
/*  22:    */   
/*  23:    */   public SnappyCompressorInputStream(InputStream is)
/*  24:    */     throws IOException
/*  25:    */   {
/*  26: 84 */     this(is, 32768);
/*  27:    */   }
/*  28:    */   
/*  29:    */   public SnappyCompressorInputStream(InputStream is, int blockSize)
/*  30:    */     throws IOException
/*  31:    */   {
/*  32: 99 */     this.in = is;
/*  33:100 */     this.blockSize = blockSize;
/*  34:101 */     this.decompressBuf = new byte[blockSize * 3];
/*  35:102 */     this.writeIndex = (this.readIndex = 0);
/*  36:103 */     this.uncompressedBytesRemaining = (this.size = (int)readSize());
/*  37:    */   }
/*  38:    */   
/*  39:    */   public int read()
/*  40:    */     throws IOException
/*  41:    */   {
/*  42:109 */     return read(this.oneByte, 0, 1) == -1 ? -1 : this.oneByte[0] & 0xFF;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void close()
/*  46:    */     throws IOException
/*  47:    */   {
/*  48:115 */     this.in.close();
/*  49:    */   }
/*  50:    */   
/*  51:    */   public int available()
/*  52:    */   {
/*  53:121 */     return this.writeIndex - this.readIndex;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public int read(byte[] b, int off, int len)
/*  57:    */     throws IOException
/*  58:    */   {
/*  59:129 */     if (this.endReached) {
/*  60:130 */       return -1;
/*  61:    */     }
/*  62:132 */     int avail = available();
/*  63:133 */     if (len > avail) {
/*  64:134 */       fill(len - avail);
/*  65:    */     }
/*  66:137 */     int readable = Math.min(len, available());
/*  67:138 */     System.arraycopy(this.decompressBuf, this.readIndex, b, off, readable);
/*  68:139 */     this.readIndex += readable;
/*  69:140 */     if (this.readIndex > this.blockSize) {
/*  70:141 */       slideBuffer();
/*  71:    */     }
/*  72:143 */     return readable;
/*  73:    */   }
/*  74:    */   
/*  75:    */   private void fill(int len)
/*  76:    */     throws IOException
/*  77:    */   {
/*  78:153 */     if (this.uncompressedBytesRemaining == 0) {
/*  79:154 */       this.endReached = true;
/*  80:    */     }
/*  81:156 */     int readNow = Math.min(len, this.uncompressedBytesRemaining);
/*  82:158 */     while (readNow > 0)
/*  83:    */     {
/*  84:159 */       int b = readOneByte();
/*  85:160 */       int length = 0;
/*  86:161 */       long offset = 0L;
/*  87:163 */       switch (b & 0x3)
/*  88:    */       {
/*  89:    */       case 0: 
/*  90:167 */         length = readLiteralLength(b);
/*  91:169 */         if (expandLiteral(length)) {
/*  92:    */           return;
/*  93:    */         }
/*  94:    */         break;
/*  95:    */       case 1: 
/*  96:185 */         length = 4 + (b >> 2 & 0x7);
/*  97:186 */         offset = (b & 0xE0) << 3;
/*  98:187 */         offset |= readOneByte();
/*  99:189 */         if (expandCopy(offset, length)) {
/* 100:    */           return;
/* 101:    */         }
/* 102:    */         break;
/* 103:    */       case 2: 
/* 104:204 */         length = (b >> 2) + 1;
/* 105:    */         
/* 106:206 */         offset = readOneByte();
/* 107:207 */         offset |= readOneByte() << 8;
/* 108:209 */         if (expandCopy(offset, length)) {
/* 109:    */           return;
/* 110:    */         }
/* 111:    */         break;
/* 112:    */       case 3: 
/* 113:223 */         length = (b >> 2) + 1;
/* 114:    */         
/* 115:225 */         offset = readOneByte();
/* 116:226 */         offset |= readOneByte() << 8;
/* 117:227 */         offset |= readOneByte() << 16;
/* 118:228 */         offset |= readOneByte() << 24;
/* 119:230 */         if (expandCopy(offset, length)) {
/* 120:    */           return;
/* 121:    */         }
/* 122:    */         break;
/* 123:    */       }
/* 124:236 */       readNow -= length;
/* 125:237 */       this.uncompressedBytesRemaining -= length;
/* 126:    */     }
/* 127:    */   }
/* 128:    */   
/* 129:    */   private void slideBuffer()
/* 130:    */   {
/* 131:248 */     System.arraycopy(this.decompressBuf, this.blockSize, this.decompressBuf, 0, this.blockSize * 2);
/* 132:    */     
/* 133:250 */     this.writeIndex -= this.blockSize;
/* 134:251 */     this.readIndex -= this.blockSize;
/* 135:    */   }
/* 136:    */   
/* 137:    */   private int readLiteralLength(int b)
/* 138:    */     throws IOException
/* 139:    */   {
/* 140:    */     int length;
/* 141:267 */     switch (b >> 2)
/* 142:    */     {
/* 143:    */     case 60: 
/* 144:269 */       length = readOneByte();
/* 145:270 */       break;
/* 146:    */     case 61: 
/* 147:272 */       length = readOneByte();
/* 148:273 */       length |= readOneByte() << 8;
/* 149:274 */       break;
/* 150:    */     case 62: 
/* 151:276 */       length = readOneByte();
/* 152:277 */       length |= readOneByte() << 8;
/* 153:278 */       length |= readOneByte() << 16;
/* 154:279 */       break;
/* 155:    */     case 63: 
/* 156:281 */       length = readOneByte();
/* 157:282 */       length |= readOneByte() << 8;
/* 158:283 */       length |= readOneByte() << 16;
/* 159:284 */       length = (int)(length | readOneByte() << 24);
/* 160:285 */       break;
/* 161:    */     default: 
/* 162:287 */       length = b >> 2;
/* 163:    */     }
/* 164:291 */     return length + 1;
/* 165:    */   }
/* 166:    */   
/* 167:    */   private boolean expandLiteral(int length)
/* 168:    */     throws IOException
/* 169:    */   {
/* 170:307 */     int bytesRead = IOUtils.readFully(this.in, this.decompressBuf, this.writeIndex, length);
/* 171:308 */     count(bytesRead);
/* 172:309 */     if (length != bytesRead) {
/* 173:310 */       throw new IOException("Premature end of stream");
/* 174:    */     }
/* 175:313 */     this.writeIndex += length;
/* 176:314 */     return this.writeIndex >= 2 * this.blockSize;
/* 177:    */   }
/* 178:    */   
/* 179:    */   private boolean expandCopy(long off, int length)
/* 180:    */     throws IOException
/* 181:    */   {
/* 182:337 */     if (off > this.blockSize) {
/* 183:338 */       throw new IOException("Offset is larger than block size");
/* 184:    */     }
/* 185:340 */     int offset = (int)off;
/* 186:342 */     if (offset == 1)
/* 187:    */     {
/* 188:343 */       byte lastChar = this.decompressBuf[(this.writeIndex - 1)];
/* 189:344 */       for (int i = 0; i < length; i++) {
/* 190:345 */         this.decompressBuf[(this.writeIndex++)] = lastChar;
/* 191:    */       }
/* 192:    */     }
/* 193:347 */     else if (length < offset)
/* 194:    */     {
/* 195:348 */       System.arraycopy(this.decompressBuf, this.writeIndex - offset, this.decompressBuf, this.writeIndex, length);
/* 196:    */       
/* 197:350 */       this.writeIndex += length;
/* 198:    */     }
/* 199:    */     else
/* 200:    */     {
/* 201:352 */       int fullRotations = length / offset;
/* 202:353 */       int pad = length - offset * fullRotations;
/* 203:355 */       while (fullRotations-- != 0)
/* 204:    */       {
/* 205:356 */         System.arraycopy(this.decompressBuf, this.writeIndex - offset, this.decompressBuf, this.writeIndex, offset);
/* 206:    */         
/* 207:358 */         this.writeIndex += offset;
/* 208:    */       }
/* 209:361 */       if (pad > 0)
/* 210:    */       {
/* 211:362 */         System.arraycopy(this.decompressBuf, this.writeIndex - offset, this.decompressBuf, this.writeIndex, pad);
/* 212:    */         
/* 213:    */ 
/* 214:365 */         this.writeIndex += pad;
/* 215:    */       }
/* 216:    */     }
/* 217:368 */     return this.writeIndex >= 2 * this.blockSize;
/* 218:    */   }
/* 219:    */   
/* 220:    */   private int readOneByte()
/* 221:    */     throws IOException
/* 222:    */   {
/* 223:382 */     int b = this.in.read();
/* 224:383 */     if (b == -1) {
/* 225:384 */       throw new IOException("Premature end of stream");
/* 226:    */     }
/* 227:386 */     count(1);
/* 228:387 */     return b & 0xFF;
/* 229:    */   }
/* 230:    */   
/* 231:    */   private long readSize()
/* 232:    */     throws IOException
/* 233:    */   {
/* 234:404 */     int index = 0;
/* 235:405 */     long sz = 0L;
/* 236:406 */     int b = 0;
/* 237:    */     do
/* 238:    */     {
/* 239:409 */       b = readOneByte();
/* 240:410 */       sz |= (b & 0x7F) << index++ * 7;
/* 241:411 */     } while (0 != (b & 0x80));
/* 242:412 */     return sz;
/* 243:    */   }
/* 244:    */   
/* 245:    */   public int getSize()
/* 246:    */   {
/* 247:421 */     return this.size;
/* 248:    */   }
/* 249:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.compressors.snappy.SnappyCompressorInputStream
 * JD-Core Version:    0.7.0.1
 */