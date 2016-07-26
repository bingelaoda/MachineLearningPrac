/*   1:    */ package org.apache.commons.compress.archivers.zip;
/*   2:    */ 
/*   3:    */ import java.io.Closeable;
/*   4:    */ import java.io.DataOutput;
/*   5:    */ import java.io.IOException;
/*   6:    */ import java.io.InputStream;
/*   7:    */ import java.io.OutputStream;
/*   8:    */ import java.util.zip.CRC32;
/*   9:    */ import java.util.zip.Deflater;
/*  10:    */ import org.apache.commons.compress.parallel.ScatterGatherBackingStore;
/*  11:    */ 
/*  12:    */ public abstract class StreamCompressor
/*  13:    */   implements Closeable
/*  14:    */ {
/*  15:    */   private static final int DEFLATER_BLOCK_SIZE = 8192;
/*  16:    */   private final Deflater def;
/*  17: 50 */   private final CRC32 crc = new CRC32();
/*  18: 52 */   private long writtenToOutputStreamForLastEntry = 0L;
/*  19: 53 */   private long sourcePayloadLength = 0L;
/*  20: 54 */   private long totalWrittenToOutputStream = 0L;
/*  21:    */   private static final int bufferSize = 4096;
/*  22: 57 */   private final byte[] outputBuffer = new byte[4096];
/*  23: 58 */   private final byte[] readerBuf = new byte[4096];
/*  24:    */   
/*  25:    */   StreamCompressor(Deflater deflater)
/*  26:    */   {
/*  27: 61 */     this.def = deflater;
/*  28:    */   }
/*  29:    */   
/*  30:    */   static StreamCompressor create(OutputStream os, Deflater deflater)
/*  31:    */   {
/*  32: 72 */     return new OutputStreamCompressor(deflater, os);
/*  33:    */   }
/*  34:    */   
/*  35:    */   static StreamCompressor create(OutputStream os)
/*  36:    */   {
/*  37: 82 */     return create(os, new Deflater(-1, true));
/*  38:    */   }
/*  39:    */   
/*  40:    */   static StreamCompressor create(DataOutput os, Deflater deflater)
/*  41:    */   {
/*  42: 93 */     return new DataOutputCompressor(deflater, os);
/*  43:    */   }
/*  44:    */   
/*  45:    */   public static StreamCompressor create(int compressionLevel, ScatterGatherBackingStore bs)
/*  46:    */   {
/*  47:104 */     Deflater deflater = new Deflater(compressionLevel, true);
/*  48:105 */     return new ScatterGatherBackingStoreCompressor(deflater, bs);
/*  49:    */   }
/*  50:    */   
/*  51:    */   public static StreamCompressor create(ScatterGatherBackingStore bs)
/*  52:    */   {
/*  53:115 */     return create(-1, bs);
/*  54:    */   }
/*  55:    */   
/*  56:    */   public long getCrc32()
/*  57:    */   {
/*  58:125 */     return this.crc.getValue();
/*  59:    */   }
/*  60:    */   
/*  61:    */   public long getBytesRead()
/*  62:    */   {
/*  63:134 */     return this.sourcePayloadLength;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public long getBytesWrittenForLastEntry()
/*  67:    */   {
/*  68:143 */     return this.writtenToOutputStreamForLastEntry;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public long getTotalBytesWritten()
/*  72:    */   {
/*  73:152 */     return this.totalWrittenToOutputStream;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void deflate(InputStream source, int method)
/*  77:    */     throws IOException
/*  78:    */   {
/*  79:165 */     reset();
/*  80:    */     int length;
/*  81:168 */     while ((length = source.read(this.readerBuf, 0, this.readerBuf.length)) >= 0) {
/*  82:169 */       write(this.readerBuf, 0, length, method);
/*  83:    */     }
/*  84:171 */     if (method == 8) {
/*  85:172 */       flushDeflater();
/*  86:    */     }
/*  87:    */   }
/*  88:    */   
/*  89:    */   long write(byte[] b, int offset, int length, int method)
/*  90:    */     throws IOException
/*  91:    */   {
/*  92:187 */     long current = this.writtenToOutputStreamForLastEntry;
/*  93:188 */     this.crc.update(b, offset, length);
/*  94:189 */     if (method == 8) {
/*  95:190 */       writeDeflated(b, offset, length);
/*  96:    */     } else {
/*  97:192 */       writeCounted(b, offset, length);
/*  98:    */     }
/*  99:194 */     this.sourcePayloadLength += length;
/* 100:195 */     return this.writtenToOutputStreamForLastEntry - current;
/* 101:    */   }
/* 102:    */   
/* 103:    */   void reset()
/* 104:    */   {
/* 105:200 */     this.crc.reset();
/* 106:201 */     this.def.reset();
/* 107:202 */     this.sourcePayloadLength = 0L;
/* 108:203 */     this.writtenToOutputStreamForLastEntry = 0L;
/* 109:    */   }
/* 110:    */   
/* 111:    */   public void close()
/* 112:    */     throws IOException
/* 113:    */   {
/* 114:207 */     this.def.end();
/* 115:    */   }
/* 116:    */   
/* 117:    */   void flushDeflater()
/* 118:    */     throws IOException
/* 119:    */   {
/* 120:211 */     this.def.finish();
/* 121:212 */     while (!this.def.finished()) {
/* 122:213 */       deflate();
/* 123:    */     }
/* 124:    */   }
/* 125:    */   
/* 126:    */   private void writeDeflated(byte[] b, int offset, int length)
/* 127:    */     throws IOException
/* 128:    */   {
/* 129:219 */     if ((length > 0) && (!this.def.finished())) {
/* 130:220 */       if (length <= 8192)
/* 131:    */       {
/* 132:221 */         this.def.setInput(b, offset, length);
/* 133:222 */         deflateUntilInputIsNeeded();
/* 134:    */       }
/* 135:    */       else
/* 136:    */       {
/* 137:224 */         int fullblocks = length / 8192;
/* 138:225 */         for (int i = 0; i < fullblocks; i++)
/* 139:    */         {
/* 140:226 */           this.def.setInput(b, offset + i * 8192, 8192);
/* 141:    */           
/* 142:228 */           deflateUntilInputIsNeeded();
/* 143:    */         }
/* 144:230 */         int done = fullblocks * 8192;
/* 145:231 */         if (done < length)
/* 146:    */         {
/* 147:232 */           this.def.setInput(b, offset + done, length - done);
/* 148:233 */           deflateUntilInputIsNeeded();
/* 149:    */         }
/* 150:    */       }
/* 151:    */     }
/* 152:    */   }
/* 153:    */   
/* 154:    */   private void deflateUntilInputIsNeeded()
/* 155:    */     throws IOException
/* 156:    */   {
/* 157:240 */     while (!this.def.needsInput()) {
/* 158:241 */       deflate();
/* 159:    */     }
/* 160:    */   }
/* 161:    */   
/* 162:    */   void deflate()
/* 163:    */     throws IOException
/* 164:    */   {
/* 165:246 */     int len = this.def.deflate(this.outputBuffer, 0, this.outputBuffer.length);
/* 166:247 */     if (len > 0) {
/* 167:248 */       writeCounted(this.outputBuffer, 0, len);
/* 168:    */     }
/* 169:    */   }
/* 170:    */   
/* 171:    */   public void writeCounted(byte[] data)
/* 172:    */     throws IOException
/* 173:    */   {
/* 174:253 */     writeCounted(data, 0, data.length);
/* 175:    */   }
/* 176:    */   
/* 177:    */   public void writeCounted(byte[] data, int offset, int length)
/* 178:    */     throws IOException
/* 179:    */   {
/* 180:257 */     writeOut(data, offset, length);
/* 181:258 */     this.writtenToOutputStreamForLastEntry += length;
/* 182:259 */     this.totalWrittenToOutputStream += length;
/* 183:    */   }
/* 184:    */   
/* 185:    */   protected abstract void writeOut(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/* 186:    */     throws IOException;
/* 187:    */   
/* 188:    */   private static final class ScatterGatherBackingStoreCompressor
/* 189:    */     extends StreamCompressor
/* 190:    */   {
/* 191:    */     private final ScatterGatherBackingStore bs;
/* 192:    */     
/* 193:    */     public ScatterGatherBackingStoreCompressor(Deflater deflater, ScatterGatherBackingStore bs)
/* 194:    */     {
/* 195:268 */       super();
/* 196:269 */       this.bs = bs;
/* 197:    */     }
/* 198:    */     
/* 199:    */     protected final void writeOut(byte[] data, int offset, int length)
/* 200:    */       throws IOException
/* 201:    */     {
/* 202:274 */       this.bs.writeOut(data, offset, length);
/* 203:    */     }
/* 204:    */   }
/* 205:    */   
/* 206:    */   private static final class OutputStreamCompressor
/* 207:    */     extends StreamCompressor
/* 208:    */   {
/* 209:    */     private final OutputStream os;
/* 210:    */     
/* 211:    */     public OutputStreamCompressor(Deflater deflater, OutputStream os)
/* 212:    */     {
/* 213:282 */       super();
/* 214:283 */       this.os = os;
/* 215:    */     }
/* 216:    */     
/* 217:    */     protected final void writeOut(byte[] data, int offset, int length)
/* 218:    */       throws IOException
/* 219:    */     {
/* 220:288 */       this.os.write(data, offset, length);
/* 221:    */     }
/* 222:    */   }
/* 223:    */   
/* 224:    */   private static final class DataOutputCompressor
/* 225:    */     extends StreamCompressor
/* 226:    */   {
/* 227:    */     private final DataOutput raf;
/* 228:    */     
/* 229:    */     public DataOutputCompressor(Deflater deflater, DataOutput raf)
/* 230:    */     {
/* 231:296 */       super();
/* 232:297 */       this.raf = raf;
/* 233:    */     }
/* 234:    */     
/* 235:    */     protected final void writeOut(byte[] data, int offset, int length)
/* 236:    */       throws IOException
/* 237:    */     {
/* 238:302 */       this.raf.write(data, offset, length);
/* 239:    */     }
/* 240:    */   }
/* 241:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.StreamCompressor
 * JD-Core Version:    0.7.0.1
 */