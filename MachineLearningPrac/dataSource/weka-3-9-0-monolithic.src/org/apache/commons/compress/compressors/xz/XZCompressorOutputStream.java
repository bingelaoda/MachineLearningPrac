/*  1:   */ package org.apache.commons.compress.compressors.xz;
/*  2:   */ 
/*  3:   */ import java.io.IOException;
/*  4:   */ import java.io.OutputStream;
/*  5:   */ import org.apache.commons.compress.compressors.CompressorOutputStream;
/*  6:   */ import org.tukaani.xz.LZMA2Options;
/*  7:   */ import org.tukaani.xz.XZOutputStream;
/*  8:   */ 
/*  9:   */ public class XZCompressorOutputStream
/* 10:   */   extends CompressorOutputStream
/* 11:   */ {
/* 12:   */   private final XZOutputStream out;
/* 13:   */   
/* 14:   */   public XZCompressorOutputStream(OutputStream outputStream)
/* 15:   */     throws IOException
/* 16:   */   {
/* 17:43 */     this.out = new XZOutputStream(outputStream, new LZMA2Options());
/* 18:   */   }
/* 19:   */   
/* 20:   */   public XZCompressorOutputStream(OutputStream outputStream, int preset)
/* 21:   */     throws IOException
/* 22:   */   {
/* 23:64 */     this.out = new XZOutputStream(outputStream, new LZMA2Options(preset));
/* 24:   */   }
/* 25:   */   
/* 26:   */   public void write(int b)
/* 27:   */     throws IOException
/* 28:   */   {
/* 29:69 */     this.out.write(b);
/* 30:   */   }
/* 31:   */   
/* 32:   */   public void write(byte[] buf, int off, int len)
/* 33:   */     throws IOException
/* 34:   */   {
/* 35:74 */     this.out.write(buf, off, len);
/* 36:   */   }
/* 37:   */   
/* 38:   */   public void flush()
/* 39:   */     throws IOException
/* 40:   */   {
/* 41:85 */     this.out.flush();
/* 42:   */   }
/* 43:   */   
/* 44:   */   public void finish()
/* 45:   */     throws IOException
/* 46:   */   {
/* 47:93 */     this.out.finish();
/* 48:   */   }
/* 49:   */   
/* 50:   */   public void close()
/* 51:   */     throws IOException
/* 52:   */   {
/* 53:98 */     this.out.close();
/* 54:   */   }
/* 55:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.compressors.xz.XZCompressorOutputStream
 * JD-Core Version:    0.7.0.1
 */