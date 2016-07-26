/*  1:   */ package org.apache.commons.compress.utils;
/*  2:   */ 
/*  3:   */ import java.io.FilterOutputStream;
/*  4:   */ import java.io.IOException;
/*  5:   */ import java.io.OutputStream;
/*  6:   */ 
/*  7:   */ public class CountingOutputStream
/*  8:   */   extends FilterOutputStream
/*  9:   */ {
/* 10:31 */   private long bytesWritten = 0L;
/* 11:   */   
/* 12:   */   public CountingOutputStream(OutputStream out)
/* 13:   */   {
/* 14:34 */     super(out);
/* 15:   */   }
/* 16:   */   
/* 17:   */   public void write(int b)
/* 18:   */     throws IOException
/* 19:   */   {
/* 20:39 */     this.out.write(b);
/* 21:40 */     count(1L);
/* 22:   */   }
/* 23:   */   
/* 24:   */   public void write(byte[] b)
/* 25:   */     throws IOException
/* 26:   */   {
/* 27:44 */     write(b, 0, b.length);
/* 28:   */   }
/* 29:   */   
/* 30:   */   public void write(byte[] b, int off, int len)
/* 31:   */     throws IOException
/* 32:   */   {
/* 33:48 */     this.out.write(b, off, len);
/* 34:49 */     count(len);
/* 35:   */   }
/* 36:   */   
/* 37:   */   protected void count(long written)
/* 38:   */   {
/* 39:59 */     if (written != -1L) {
/* 40:60 */       this.bytesWritten += written;
/* 41:   */     }
/* 42:   */   }
/* 43:   */   
/* 44:   */   public long getBytesWritten()
/* 45:   */   {
/* 46:69 */     return this.bytesWritten;
/* 47:   */   }
/* 48:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.utils.CountingOutputStream
 * JD-Core Version:    0.7.0.1
 */