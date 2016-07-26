/*  1:   */ package org.apache.commons.compress.utils;
/*  2:   */ 
/*  3:   */ import java.io.FilterInputStream;
/*  4:   */ import java.io.IOException;
/*  5:   */ import java.io.InputStream;
/*  6:   */ 
/*  7:   */ public class CountingInputStream
/*  8:   */   extends FilterInputStream
/*  9:   */ {
/* 10:   */   private long bytesRead;
/* 11:   */   
/* 12:   */   public CountingInputStream(InputStream in)
/* 13:   */   {
/* 14:34 */     super(in);
/* 15:   */   }
/* 16:   */   
/* 17:   */   public int read()
/* 18:   */     throws IOException
/* 19:   */   {
/* 20:39 */     int r = this.in.read();
/* 21:40 */     if (r >= 0) {
/* 22:41 */       count(1L);
/* 23:   */     }
/* 24:43 */     return r;
/* 25:   */   }
/* 26:   */   
/* 27:   */   public int read(byte[] b)
/* 28:   */     throws IOException
/* 29:   */   {
/* 30:47 */     return read(b, 0, b.length);
/* 31:   */   }
/* 32:   */   
/* 33:   */   public int read(byte[] b, int off, int len)
/* 34:   */     throws IOException
/* 35:   */   {
/* 36:51 */     int r = this.in.read(b, off, len);
/* 37:52 */     if (r >= 0) {
/* 38:53 */       count(r);
/* 39:   */     }
/* 40:55 */     return r;
/* 41:   */   }
/* 42:   */   
/* 43:   */   protected final void count(long read)
/* 44:   */   {
/* 45:64 */     if (read != -1L) {
/* 46:65 */       this.bytesRead += read;
/* 47:   */     }
/* 48:   */   }
/* 49:   */   
/* 50:   */   public long getBytesRead()
/* 51:   */   {
/* 52:74 */     return this.bytesRead;
/* 53:   */   }
/* 54:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.utils.CountingInputStream
 * JD-Core Version:    0.7.0.1
 */