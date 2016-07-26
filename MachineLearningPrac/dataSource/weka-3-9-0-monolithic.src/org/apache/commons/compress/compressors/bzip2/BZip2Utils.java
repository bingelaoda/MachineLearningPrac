/*  1:   */ package org.apache.commons.compress.compressors.bzip2;
/*  2:   */ 
/*  3:   */ import java.util.LinkedHashMap;
/*  4:   */ import java.util.Map;
/*  5:   */ import org.apache.commons.compress.compressors.FileNameUtil;
/*  6:   */ 
/*  7:   */ public abstract class BZip2Utils
/*  8:   */ {
/*  9:   */   private static final FileNameUtil fileNameUtil;
/* 10:   */   
/* 11:   */   static
/* 12:   */   {
/* 13:35 */     Map<String, String> uncompressSuffix = new LinkedHashMap();
/* 14:   */     
/* 15:   */ 
/* 16:   */ 
/* 17:39 */     uncompressSuffix.put(".tar.bz2", ".tar");
/* 18:40 */     uncompressSuffix.put(".tbz2", ".tar");
/* 19:41 */     uncompressSuffix.put(".tbz", ".tar");
/* 20:42 */     uncompressSuffix.put(".bz2", "");
/* 21:43 */     uncompressSuffix.put(".bz", "");
/* 22:44 */     fileNameUtil = new FileNameUtil(uncompressSuffix, ".bz2");
/* 23:   */   }
/* 24:   */   
/* 25:   */   public static boolean isCompressedFilename(String filename)
/* 26:   */   {
/* 27:59 */     return fileNameUtil.isCompressedFilename(filename);
/* 28:   */   }
/* 29:   */   
/* 30:   */   public static String getUncompressedFilename(String filename)
/* 31:   */   {
/* 32:76 */     return fileNameUtil.getUncompressedFilename(filename);
/* 33:   */   }
/* 34:   */   
/* 35:   */   public static String getCompressedFilename(String filename)
/* 36:   */   {
/* 37:90 */     return fileNameUtil.getCompressedFilename(filename);
/* 38:   */   }
/* 39:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.compressors.bzip2.BZip2Utils
 * JD-Core Version:    0.7.0.1
 */