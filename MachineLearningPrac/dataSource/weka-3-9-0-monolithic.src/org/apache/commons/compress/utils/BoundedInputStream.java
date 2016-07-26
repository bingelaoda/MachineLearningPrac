/*  1:   */ package org.apache.commons.compress.utils;
/*  2:   */ 
/*  3:   */ import java.io.IOException;
/*  4:   */ import java.io.InputStream;
/*  5:   */ 
/*  6:   */ public class BoundedInputStream
/*  7:   */   extends InputStream
/*  8:   */ {
/*  9:   */   private final InputStream in;
/* 10:   */   private long bytesRemaining;
/* 11:   */   
/* 12:   */   public BoundedInputStream(InputStream in, long size)
/* 13:   */   {
/* 14:39 */     this.in = in;
/* 15:40 */     this.bytesRemaining = size;
/* 16:   */   }
/* 17:   */   
/* 18:   */   public int read()
/* 19:   */     throws IOException
/* 20:   */   {
/* 21:45 */     if (this.bytesRemaining > 0L)
/* 22:   */     {
/* 23:46 */       this.bytesRemaining -= 1L;
/* 24:47 */       return this.in.read();
/* 25:   */     }
/* 26:49 */     return -1;
/* 27:   */   }
/* 28:   */   
/* 29:   */   public int read(byte[] b, int off, int len)
/* 30:   */     throws IOException
/* 31:   */   {
/* 32:55 */     if (this.bytesRemaining == 0L) {
/* 33:56 */       return -1;
/* 34:   */     }
/* 35:58 */     int bytesToRead = len;
/* 36:59 */     if (bytesToRead > this.bytesRemaining) {
/* 37:60 */       bytesToRead = (int)this.bytesRemaining;
/* 38:   */     }
/* 39:62 */     int bytesRead = this.in.read(b, off, bytesToRead);
/* 40:63 */     if (bytesRead >= 0) {
/* 41:64 */       this.bytesRemaining -= bytesRead;
/* 42:   */     }
/* 43:66 */     return bytesRead;
/* 44:   */   }
/* 45:   */   
/* 46:   */   public void close() {}
/* 47:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.utils.BoundedInputStream
 * JD-Core Version:    0.7.0.1
 */