/*  1:   */ package org.apache.commons.compress.compressors.deflate;
/*  2:   */ 
/*  3:   */ import java.io.IOException;
/*  4:   */ import java.io.OutputStream;
/*  5:   */ import java.util.zip.Deflater;
/*  6:   */ import java.util.zip.DeflaterOutputStream;
/*  7:   */ import org.apache.commons.compress.compressors.CompressorOutputStream;
/*  8:   */ 
/*  9:   */ public class DeflateCompressorOutputStream
/* 10:   */   extends CompressorOutputStream
/* 11:   */ {
/* 12:   */   private final DeflaterOutputStream out;
/* 13:   */   
/* 14:   */   public DeflateCompressorOutputStream(OutputStream outputStream)
/* 15:   */     throws IOException
/* 16:   */   {
/* 17:41 */     this(outputStream, new DeflateParameters());
/* 18:   */   }
/* 19:   */   
/* 20:   */   public DeflateCompressorOutputStream(OutputStream outputStream, DeflateParameters parameters)
/* 21:   */     throws IOException
/* 22:   */   {
/* 23:52 */     this.out = new DeflaterOutputStream(outputStream, new Deflater(parameters.getCompressionLevel(), !parameters.withZlibHeader()));
/* 24:   */   }
/* 25:   */   
/* 26:   */   public void write(int b)
/* 27:   */     throws IOException
/* 28:   */   {
/* 29:57 */     this.out.write(b);
/* 30:   */   }
/* 31:   */   
/* 32:   */   public void write(byte[] buf, int off, int len)
/* 33:   */     throws IOException
/* 34:   */   {
/* 35:62 */     this.out.write(buf, off, len);
/* 36:   */   }
/* 37:   */   
/* 38:   */   public void flush()
/* 39:   */     throws IOException
/* 40:   */   {
/* 41:73 */     this.out.flush();
/* 42:   */   }
/* 43:   */   
/* 44:   */   public void finish()
/* 45:   */     throws IOException
/* 46:   */   {
/* 47:81 */     this.out.finish();
/* 48:   */   }
/* 49:   */   
/* 50:   */   public void close()
/* 51:   */     throws IOException
/* 52:   */   {
/* 53:86 */     this.out.close();
/* 54:   */   }
/* 55:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.compressors.deflate.DeflateCompressorOutputStream
 * JD-Core Version:    0.7.0.1
 */