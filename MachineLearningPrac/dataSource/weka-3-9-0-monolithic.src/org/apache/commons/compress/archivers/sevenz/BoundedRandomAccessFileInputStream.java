/*  1:   */ package org.apache.commons.compress.archivers.sevenz;
/*  2:   */ 
/*  3:   */ import java.io.IOException;
/*  4:   */ import java.io.InputStream;
/*  5:   */ import java.io.RandomAccessFile;
/*  6:   */ 
/*  7:   */ class BoundedRandomAccessFileInputStream
/*  8:   */   extends InputStream
/*  9:   */ {
/* 10:   */   private final RandomAccessFile file;
/* 11:   */   private long bytesRemaining;
/* 12:   */   
/* 13:   */   public BoundedRandomAccessFileInputStream(RandomAccessFile file, long size)
/* 14:   */   {
/* 15:30 */     this.file = file;
/* 16:31 */     this.bytesRemaining = size;
/* 17:   */   }
/* 18:   */   
/* 19:   */   public int read()
/* 20:   */     throws IOException
/* 21:   */   {
/* 22:36 */     if (this.bytesRemaining > 0L)
/* 23:   */     {
/* 24:37 */       this.bytesRemaining -= 1L;
/* 25:38 */       return this.file.read();
/* 26:   */     }
/* 27:40 */     return -1;
/* 28:   */   }
/* 29:   */   
/* 30:   */   public int read(byte[] b, int off, int len)
/* 31:   */     throws IOException
/* 32:   */   {
/* 33:46 */     if (this.bytesRemaining == 0L) {
/* 34:47 */       return -1;
/* 35:   */     }
/* 36:49 */     int bytesToRead = len;
/* 37:50 */     if (bytesToRead > this.bytesRemaining) {
/* 38:51 */       bytesToRead = (int)this.bytesRemaining;
/* 39:   */     }
/* 40:53 */     int bytesRead = this.file.read(b, off, bytesToRead);
/* 41:54 */     if (bytesRead >= 0) {
/* 42:55 */       this.bytesRemaining -= bytesRead;
/* 43:   */     }
/* 44:57 */     return bytesRead;
/* 45:   */   }
/* 46:   */   
/* 47:   */   public void close() {}
/* 48:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.sevenz.BoundedRandomAccessFileInputStream
 * JD-Core Version:    0.7.0.1
 */