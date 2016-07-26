/*  1:   */ package org.apache.commons.compress.compressors.deflate;
/*  2:   */ 
/*  3:   */ public class DeflateParameters
/*  4:   */ {
/*  5:30 */   private boolean zlibHeader = true;
/*  6:31 */   private int compressionLevel = -1;
/*  7:   */   
/*  8:   */   public boolean withZlibHeader()
/*  9:   */   {
/* 10:38 */     return this.zlibHeader;
/* 11:   */   }
/* 12:   */   
/* 13:   */   public void setWithZlibHeader(boolean zlibHeader)
/* 14:   */   {
/* 15:50 */     this.zlibHeader = zlibHeader;
/* 16:   */   }
/* 17:   */   
/* 18:   */   public int getCompressionLevel()
/* 19:   */   {
/* 20:58 */     return this.compressionLevel;
/* 21:   */   }
/* 22:   */   
/* 23:   */   public void setCompressionLevel(int compressionLevel)
/* 24:   */   {
/* 25:71 */     if ((compressionLevel < -1) || (compressionLevel > 9)) {
/* 26:72 */       throw new IllegalArgumentException("Invalid Deflate compression level: " + compressionLevel);
/* 27:   */     }
/* 28:74 */     this.compressionLevel = compressionLevel;
/* 29:   */   }
/* 30:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.compressors.deflate.DeflateParameters
 * JD-Core Version:    0.7.0.1
 */