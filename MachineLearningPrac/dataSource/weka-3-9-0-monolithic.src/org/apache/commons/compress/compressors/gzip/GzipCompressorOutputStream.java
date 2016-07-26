/*   1:    */ package org.apache.commons.compress.compressors.gzip;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.OutputStream;
/*   5:    */ import java.nio.ByteBuffer;
/*   6:    */ import java.nio.ByteOrder;
/*   7:    */ import java.util.zip.CRC32;
/*   8:    */ import java.util.zip.Deflater;
/*   9:    */ import org.apache.commons.compress.compressors.CompressorOutputStream;
/*  10:    */ 
/*  11:    */ public class GzipCompressorOutputStream
/*  12:    */   extends CompressorOutputStream
/*  13:    */ {
/*  14:    */   private static final int FNAME = 8;
/*  15:    */   private static final int FCOMMENT = 16;
/*  16:    */   private final OutputStream out;
/*  17:    */   private final Deflater deflater;
/*  18: 56 */   private final byte[] deflateBuffer = new byte[512];
/*  19:    */   private boolean closed;
/*  20: 62 */   private final CRC32 crc = new CRC32();
/*  21:    */   
/*  22:    */   public GzipCompressorOutputStream(OutputStream out)
/*  23:    */     throws IOException
/*  24:    */   {
/*  25: 68 */     this(out, new GzipParameters());
/*  26:    */   }
/*  27:    */   
/*  28:    */   public GzipCompressorOutputStream(OutputStream out, GzipParameters parameters)
/*  29:    */     throws IOException
/*  30:    */   {
/*  31: 77 */     this.out = out;
/*  32: 78 */     this.deflater = new Deflater(parameters.getCompressionLevel(), true);
/*  33:    */     
/*  34: 80 */     writeHeader(parameters);
/*  35:    */   }
/*  36:    */   
/*  37:    */   private void writeHeader(GzipParameters parameters)
/*  38:    */     throws IOException
/*  39:    */   {
/*  40: 84 */     String filename = parameters.getFilename();
/*  41: 85 */     String comment = parameters.getComment();
/*  42:    */     
/*  43: 87 */     ByteBuffer buffer = ByteBuffer.allocate(10);
/*  44: 88 */     buffer.order(ByteOrder.LITTLE_ENDIAN);
/*  45: 89 */     buffer.putShort((short)-29921);
/*  46: 90 */     buffer.put((byte)8);
/*  47: 91 */     buffer.put((byte)((filename != null ? 8 : 0) | (comment != null ? 16 : 0)));
/*  48: 92 */     buffer.putInt((int)(parameters.getModificationTime() / 1000L));
/*  49:    */     
/*  50:    */ 
/*  51: 95 */     int compressionLevel = parameters.getCompressionLevel();
/*  52: 96 */     if (compressionLevel == 9) {
/*  53: 97 */       buffer.put((byte)2);
/*  54: 98 */     } else if (compressionLevel == 1) {
/*  55: 99 */       buffer.put((byte)4);
/*  56:    */     } else {
/*  57:101 */       buffer.put((byte)0);
/*  58:    */     }
/*  59:104 */     buffer.put((byte)parameters.getOperatingSystem());
/*  60:    */     
/*  61:106 */     this.out.write(buffer.array());
/*  62:108 */     if (filename != null)
/*  63:    */     {
/*  64:109 */       this.out.write(filename.getBytes("ISO-8859-1"));
/*  65:110 */       this.out.write(0);
/*  66:    */     }
/*  67:113 */     if (comment != null)
/*  68:    */     {
/*  69:114 */       this.out.write(comment.getBytes("ISO-8859-1"));
/*  70:115 */       this.out.write(0);
/*  71:    */     }
/*  72:    */   }
/*  73:    */   
/*  74:    */   private void writeTrailer()
/*  75:    */     throws IOException
/*  76:    */   {
/*  77:120 */     ByteBuffer buffer = ByteBuffer.allocate(8);
/*  78:121 */     buffer.order(ByteOrder.LITTLE_ENDIAN);
/*  79:122 */     buffer.putInt((int)this.crc.getValue());
/*  80:123 */     buffer.putInt(this.deflater.getTotalIn());
/*  81:    */     
/*  82:125 */     this.out.write(buffer.array());
/*  83:    */   }
/*  84:    */   
/*  85:    */   public void write(int b)
/*  86:    */     throws IOException
/*  87:    */   {
/*  88:130 */     write(new byte[] { (byte)(b & 0xFF) }, 0, 1);
/*  89:    */   }
/*  90:    */   
/*  91:    */   public void write(byte[] buffer)
/*  92:    */     throws IOException
/*  93:    */   {
/*  94:140 */     write(buffer, 0, buffer.length);
/*  95:    */   }
/*  96:    */   
/*  97:    */   public void write(byte[] buffer, int offset, int length)
/*  98:    */     throws IOException
/*  99:    */   {
/* 100:150 */     if (this.deflater.finished()) {
/* 101:151 */       throw new IOException("Cannot write more data, the end of the compressed data stream has been reached");
/* 102:    */     }
/* 103:153 */     if (length > 0)
/* 104:    */     {
/* 105:154 */       this.deflater.setInput(buffer, offset, length);
/* 106:156 */       while (!this.deflater.needsInput()) {
/* 107:157 */         deflate();
/* 108:    */       }
/* 109:160 */       this.crc.update(buffer, offset, length);
/* 110:    */     }
/* 111:    */   }
/* 112:    */   
/* 113:    */   private void deflate()
/* 114:    */     throws IOException
/* 115:    */   {
/* 116:165 */     int length = this.deflater.deflate(this.deflateBuffer, 0, this.deflateBuffer.length);
/* 117:166 */     if (length > 0) {
/* 118:167 */       this.out.write(this.deflateBuffer, 0, length);
/* 119:    */     }
/* 120:    */   }
/* 121:    */   
/* 122:    */   public void finish()
/* 123:    */     throws IOException
/* 124:    */   {
/* 125:177 */     if (!this.deflater.finished())
/* 126:    */     {
/* 127:178 */       this.deflater.finish();
/* 128:180 */       while (!this.deflater.finished()) {
/* 129:181 */         deflate();
/* 130:    */       }
/* 131:184 */       writeTrailer();
/* 132:    */     }
/* 133:    */   }
/* 134:    */   
/* 135:    */   public void flush()
/* 136:    */     throws IOException
/* 137:    */   {
/* 138:195 */     this.out.flush();
/* 139:    */   }
/* 140:    */   
/* 141:    */   public void close()
/* 142:    */     throws IOException
/* 143:    */   {
/* 144:200 */     if (!this.closed)
/* 145:    */     {
/* 146:201 */       finish();
/* 147:202 */       this.deflater.end();
/* 148:203 */       this.out.close();
/* 149:204 */       this.closed = true;
/* 150:    */     }
/* 151:    */   }
/* 152:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
 * JD-Core Version:    0.7.0.1
 */