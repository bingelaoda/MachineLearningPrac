/*   1:    */ package org.apache.commons.compress.compressors.gzip;
/*   2:    */ 
/*   3:    */ public class GzipParameters
/*   4:    */ {
/*   5: 31 */   private int compressionLevel = -1;
/*   6:    */   private long modificationTime;
/*   7:    */   private String filename;
/*   8:    */   private String comment;
/*   9: 35 */   private int operatingSystem = 255;
/*  10:    */   
/*  11:    */   public int getCompressionLevel()
/*  12:    */   {
/*  13: 38 */     return this.compressionLevel;
/*  14:    */   }
/*  15:    */   
/*  16:    */   public void setCompressionLevel(int compressionLevel)
/*  17:    */   {
/*  18: 51 */     if ((compressionLevel < -1) || (compressionLevel > 9)) {
/*  19: 52 */       throw new IllegalArgumentException("Invalid gzip compression level: " + compressionLevel);
/*  20:    */     }
/*  21: 54 */     this.compressionLevel = compressionLevel;
/*  22:    */   }
/*  23:    */   
/*  24:    */   public long getModificationTime()
/*  25:    */   {
/*  26: 58 */     return this.modificationTime;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public void setModificationTime(long modificationTime)
/*  30:    */   {
/*  31: 67 */     this.modificationTime = modificationTime;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public String getFilename()
/*  35:    */   {
/*  36: 71 */     return this.filename;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void setFilename(String filename)
/*  40:    */   {
/*  41: 80 */     this.filename = filename;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public String getComment()
/*  45:    */   {
/*  46: 84 */     return this.comment;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void setComment(String comment)
/*  50:    */   {
/*  51: 88 */     this.comment = comment;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public int getOperatingSystem()
/*  55:    */   {
/*  56: 92 */     return this.operatingSystem;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public void setOperatingSystem(int operatingSystem)
/*  60:    */   {
/*  61:119 */     this.operatingSystem = operatingSystem;
/*  62:    */   }
/*  63:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.compressors.gzip.GzipParameters
 * JD-Core Version:    0.7.0.1
 */