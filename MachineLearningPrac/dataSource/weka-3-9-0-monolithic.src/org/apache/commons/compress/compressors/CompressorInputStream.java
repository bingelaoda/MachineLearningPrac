/*  1:   */ package org.apache.commons.compress.compressors;
/*  2:   */ 
/*  3:   */ import java.io.InputStream;
/*  4:   */ 
/*  5:   */ public abstract class CompressorInputStream
/*  6:   */   extends InputStream
/*  7:   */ {
/*  8:24 */   private long bytesRead = 0L;
/*  9:   */   
/* 10:   */   protected void count(int read)
/* 11:   */   {
/* 12:35 */     count(read);
/* 13:   */   }
/* 14:   */   
/* 15:   */   protected void count(long read)
/* 16:   */   {
/* 17:45 */     if (read != -1L) {
/* 18:46 */       this.bytesRead += read;
/* 19:   */     }
/* 20:   */   }
/* 21:   */   
/* 22:   */   protected void pushedBackBytes(long pushedBack)
/* 23:   */   {
/* 24:57 */     this.bytesRead -= pushedBack;
/* 25:   */   }
/* 26:   */   
/* 27:   */   @Deprecated
/* 28:   */   public int getCount()
/* 29:   */   {
/* 30:68 */     return (int)this.bytesRead;
/* 31:   */   }
/* 32:   */   
/* 33:   */   public long getBytesRead()
/* 34:   */   {
/* 35:78 */     return this.bytesRead;
/* 36:   */   }
/* 37:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.compressors.CompressorInputStream
 * JD-Core Version:    0.7.0.1
 */