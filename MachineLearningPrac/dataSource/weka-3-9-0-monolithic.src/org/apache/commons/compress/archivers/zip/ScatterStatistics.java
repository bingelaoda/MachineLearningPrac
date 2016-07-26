/*  1:   */ package org.apache.commons.compress.archivers.zip;
/*  2:   */ 
/*  3:   */ public class ScatterStatistics
/*  4:   */ {
/*  5:   */   private final long compressionElapsed;
/*  6:   */   private final long mergingElapsed;
/*  7:   */   
/*  8:   */   ScatterStatistics(long compressionElapsed, long mergingElapsed)
/*  9:   */   {
/* 10:31 */     this.compressionElapsed = compressionElapsed;
/* 11:32 */     this.mergingElapsed = mergingElapsed;
/* 12:   */   }
/* 13:   */   
/* 14:   */   public long getCompressionElapsed()
/* 15:   */   {
/* 16:40 */     return this.compressionElapsed;
/* 17:   */   }
/* 18:   */   
/* 19:   */   public long getMergingElapsed()
/* 20:   */   {
/* 21:48 */     return this.mergingElapsed;
/* 22:   */   }
/* 23:   */   
/* 24:   */   public String toString()
/* 25:   */   {
/* 26:53 */     return "compressionElapsed=" + this.compressionElapsed + "ms, mergingElapsed=" + this.mergingElapsed + "ms";
/* 27:   */   }
/* 28:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.ScatterStatistics
 * JD-Core Version:    0.7.0.1
 */