/*  1:   */ package org.apache.commons.compress.compressors.gzip;
/*  2:   */ 
/*  3:   */ import java.util.LinkedHashMap;
/*  4:   */ import java.util.Map;
/*  5:   */ import org.apache.commons.compress.compressors.FileNameUtil;
/*  6:   */ 
/*  7:   */ public class GzipUtils
/*  8:   */ {
/*  9:   */   private static final FileNameUtil fileNameUtil;
/* 10:   */   
/* 11:   */   static
/* 12:   */   {
/* 13:37 */     Map<String, String> uncompressSuffix = new LinkedHashMap();
/* 14:   */     
/* 15:39 */     uncompressSuffix.put(".tgz", ".tar");
/* 16:40 */     uncompressSuffix.put(".taz", ".tar");
/* 17:41 */     uncompressSuffix.put(".svgz", ".svg");
/* 18:42 */     uncompressSuffix.put(".cpgz", ".cpio");
/* 19:43 */     uncompressSuffix.put(".wmz", ".wmf");
/* 20:44 */     uncompressSuffix.put(".emz", ".emf");
/* 21:45 */     uncompressSuffix.put(".gz", "");
/* 22:46 */     uncompressSuffix.put(".z", "");
/* 23:47 */     uncompressSuffix.put("-gz", "");
/* 24:48 */     uncompressSuffix.put("-z", "");
/* 25:49 */     uncompressSuffix.put("_z", "");
/* 26:50 */     fileNameUtil = new FileNameUtil(uncompressSuffix, ".gz");
/* 27:   */   }
/* 28:   */   
/* 29:   */   public static boolean isCompressedFilename(String filename)
/* 30:   */   {
/* 31:65 */     return fileNameUtil.isCompressedFilename(filename);
/* 32:   */   }
/* 33:   */   
/* 34:   */   public static String getUncompressedFilename(String filename)
/* 35:   */   {
/* 36:82 */     return fileNameUtil.getUncompressedFilename(filename);
/* 37:   */   }
/* 38:   */   
/* 39:   */   public static String getCompressedFilename(String filename)
/* 40:   */   {
/* 41:97 */     return fileNameUtil.getCompressedFilename(filename);
/* 42:   */   }
/* 43:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.compressors.gzip.GzipUtils
 * JD-Core Version:    0.7.0.1
 */