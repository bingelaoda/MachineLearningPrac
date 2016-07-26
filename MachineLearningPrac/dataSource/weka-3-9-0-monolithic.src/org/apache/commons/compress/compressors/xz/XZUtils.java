/*   1:    */ package org.apache.commons.compress.compressors.xz;
/*   2:    */ 
/*   3:    */ import java.util.HashMap;
/*   4:    */ import java.util.Map;
/*   5:    */ import org.apache.commons.compress.compressors.FileNameUtil;
/*   6:    */ 
/*   7:    */ public class XZUtils
/*   8:    */ {
/*   9:    */   private static final FileNameUtil fileNameUtil;
/*  10: 40 */   private static final byte[] HEADER_MAGIC = { -3, 55, 122, 88, 90, 0 };
/*  11:    */   private static volatile CachedAvailability cachedXZAvailability;
/*  12:    */   
/*  13:    */   static enum CachedAvailability
/*  14:    */   {
/*  15: 45 */     DONT_CACHE,  CACHED_AVAILABLE,  CACHED_UNAVAILABLE;
/*  16:    */     
/*  17:    */     private CachedAvailability() {}
/*  18:    */   }
/*  19:    */   
/*  20:    */   static
/*  21:    */   {
/*  22: 51 */     Map<String, String> uncompressSuffix = new HashMap();
/*  23: 52 */     uncompressSuffix.put(".txz", ".tar");
/*  24: 53 */     uncompressSuffix.put(".xz", "");
/*  25: 54 */     uncompressSuffix.put("-xz", "");
/*  26: 55 */     fileNameUtil = new FileNameUtil(uncompressSuffix, ".xz");
/*  27: 56 */     cachedXZAvailability = CachedAvailability.DONT_CACHE;
/*  28:    */     try
/*  29:    */     {
/*  30: 58 */       Class.forName("org.osgi.framework.BundleEvent");
/*  31:    */     }
/*  32:    */     catch (Exception ex)
/*  33:    */     {
/*  34: 60 */       setCacheXZAvailablity(true);
/*  35:    */     }
/*  36:    */   }
/*  37:    */   
/*  38:    */   public static boolean matches(byte[] signature, int length)
/*  39:    */   {
/*  40: 81 */     if (length < HEADER_MAGIC.length) {
/*  41: 82 */       return false;
/*  42:    */     }
/*  43: 85 */     for (int i = 0; i < HEADER_MAGIC.length; i++) {
/*  44: 86 */       if (signature[i] != HEADER_MAGIC[i]) {
/*  45: 87 */         return false;
/*  46:    */       }
/*  47:    */     }
/*  48: 91 */     return true;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public static boolean isXZCompressionAvailable()
/*  52:    */   {
/*  53:100 */     CachedAvailability cachedResult = cachedXZAvailability;
/*  54:101 */     if (cachedResult != CachedAvailability.DONT_CACHE) {
/*  55:102 */       return cachedResult == CachedAvailability.CACHED_AVAILABLE;
/*  56:    */     }
/*  57:104 */     return internalIsXZCompressionAvailable();
/*  58:    */   }
/*  59:    */   
/*  60:    */   private static boolean internalIsXZCompressionAvailable()
/*  61:    */   {
/*  62:    */     try
/*  63:    */     {
/*  64:109 */       XZCompressorInputStream.matches(null, 0);
/*  65:110 */       return true;
/*  66:    */     }
/*  67:    */     catch (NoClassDefFoundError error) {}
/*  68:112 */     return false;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public static boolean isCompressedFilename(String filename)
/*  72:    */   {
/*  73:124 */     return fileNameUtil.isCompressedFilename(filename);
/*  74:    */   }
/*  75:    */   
/*  76:    */   public static String getUncompressedFilename(String filename)
/*  77:    */   {
/*  78:141 */     return fileNameUtil.getUncompressedFilename(filename);
/*  79:    */   }
/*  80:    */   
/*  81:    */   public static String getCompressedFilename(String filename)
/*  82:    */   {
/*  83:156 */     return fileNameUtil.getCompressedFilename(filename);
/*  84:    */   }
/*  85:    */   
/*  86:    */   public static void setCacheXZAvailablity(boolean doCache)
/*  87:    */   {
/*  88:167 */     if (!doCache)
/*  89:    */     {
/*  90:168 */       cachedXZAvailability = CachedAvailability.DONT_CACHE;
/*  91:    */     }
/*  92:169 */     else if (cachedXZAvailability == CachedAvailability.DONT_CACHE)
/*  93:    */     {
/*  94:170 */       boolean hasXz = internalIsXZCompressionAvailable();
/*  95:171 */       cachedXZAvailability = hasXz ? CachedAvailability.CACHED_AVAILABLE : CachedAvailability.CACHED_UNAVAILABLE;
/*  96:    */     }
/*  97:    */   }
/*  98:    */   
/*  99:    */   static CachedAvailability getCachedXZAvailability()
/* 100:    */   {
/* 101:178 */     return cachedXZAvailability;
/* 102:    */   }
/* 103:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.compressors.xz.XZUtils
 * JD-Core Version:    0.7.0.1
 */