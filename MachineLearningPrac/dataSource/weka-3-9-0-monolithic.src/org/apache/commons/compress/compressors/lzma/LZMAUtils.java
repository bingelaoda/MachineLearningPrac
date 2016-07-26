/*   1:    */ package org.apache.commons.compress.compressors.lzma;
/*   2:    */ 
/*   3:    */ import java.util.HashMap;
/*   4:    */ import java.util.Map;
/*   5:    */ import org.apache.commons.compress.compressors.FileNameUtil;
/*   6:    */ 
/*   7:    */ public class LZMAUtils
/*   8:    */ {
/*   9:    */   private static final FileNameUtil fileNameUtil;
/*  10: 37 */   private static final byte[] HEADER_MAGIC = { 93, 0, 0 };
/*  11:    */   private static volatile CachedAvailability cachedLZMAAvailability;
/*  12:    */   
/*  13:    */   static enum CachedAvailability
/*  14:    */   {
/*  15: 42 */     DONT_CACHE,  CACHED_AVAILABLE,  CACHED_UNAVAILABLE;
/*  16:    */     
/*  17:    */     private CachedAvailability() {}
/*  18:    */   }
/*  19:    */   
/*  20:    */   static
/*  21:    */   {
/*  22: 48 */     Map<String, String> uncompressSuffix = new HashMap();
/*  23: 49 */     uncompressSuffix.put(".lzma", "");
/*  24: 50 */     uncompressSuffix.put("-lzma", "");
/*  25: 51 */     fileNameUtil = new FileNameUtil(uncompressSuffix, ".lzma");
/*  26: 52 */     cachedLZMAAvailability = CachedAvailability.DONT_CACHE;
/*  27:    */     try
/*  28:    */     {
/*  29: 54 */       Class.forName("org.osgi.framework.BundleEvent");
/*  30:    */     }
/*  31:    */     catch (Exception ex)
/*  32:    */     {
/*  33: 56 */       setCacheLZMAAvailablity(true);
/*  34:    */     }
/*  35:    */   }
/*  36:    */   
/*  37:    */   public static boolean matches(byte[] signature, int length)
/*  38:    */   {
/*  39: 72 */     if (length < HEADER_MAGIC.length) {
/*  40: 73 */       return false;
/*  41:    */     }
/*  42: 76 */     for (int i = 0; i < HEADER_MAGIC.length; i++) {
/*  43: 77 */       if (signature[i] != HEADER_MAGIC[i]) {
/*  44: 78 */         return false;
/*  45:    */       }
/*  46:    */     }
/*  47: 82 */     return true;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public static boolean isLZMACompressionAvailable()
/*  51:    */   {
/*  52: 89 */     CachedAvailability cachedResult = cachedLZMAAvailability;
/*  53: 90 */     if (cachedResult != CachedAvailability.DONT_CACHE) {
/*  54: 91 */       return cachedResult == CachedAvailability.CACHED_AVAILABLE;
/*  55:    */     }
/*  56: 93 */     return internalIsLZMACompressionAvailable();
/*  57:    */   }
/*  58:    */   
/*  59:    */   private static boolean internalIsLZMACompressionAvailable()
/*  60:    */   {
/*  61:    */     try
/*  62:    */     {
/*  63: 98 */       LZMACompressorInputStream.matches(null, 0);
/*  64: 99 */       return true;
/*  65:    */     }
/*  66:    */     catch (NoClassDefFoundError error) {}
/*  67:101 */     return false;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public static boolean isCompressedFilename(String filename)
/*  71:    */   {
/*  72:113 */     return fileNameUtil.isCompressedFilename(filename);
/*  73:    */   }
/*  74:    */   
/*  75:    */   public static String getUncompressedFilename(String filename)
/*  76:    */   {
/*  77:127 */     return fileNameUtil.getUncompressedFilename(filename);
/*  78:    */   }
/*  79:    */   
/*  80:    */   public static String getCompressedFilename(String filename)
/*  81:    */   {
/*  82:138 */     return fileNameUtil.getCompressedFilename(filename);
/*  83:    */   }
/*  84:    */   
/*  85:    */   public static void setCacheLZMAAvailablity(boolean doCache)
/*  86:    */   {
/*  87:148 */     if (!doCache)
/*  88:    */     {
/*  89:149 */       cachedLZMAAvailability = CachedAvailability.DONT_CACHE;
/*  90:    */     }
/*  91:150 */     else if (cachedLZMAAvailability == CachedAvailability.DONT_CACHE)
/*  92:    */     {
/*  93:151 */       boolean hasLzma = internalIsLZMACompressionAvailable();
/*  94:152 */       cachedLZMAAvailability = hasLzma ? CachedAvailability.CACHED_AVAILABLE : CachedAvailability.CACHED_UNAVAILABLE;
/*  95:    */     }
/*  96:    */   }
/*  97:    */   
/*  98:    */   static CachedAvailability getCachedLZMAAvailability()
/*  99:    */   {
/* 100:159 */     return cachedLZMAAvailability;
/* 101:    */   }
/* 102:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.compressors.lzma.LZMAUtils
 * JD-Core Version:    0.7.0.1
 */