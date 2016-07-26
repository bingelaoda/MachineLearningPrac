/*   1:    */ package org.apache.commons.compress.compressors;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.HashMap;
/*   5:    */ import java.util.Locale;
/*   6:    */ import java.util.Map;
/*   7:    */ import java.util.Map.Entry;
/*   8:    */ 
/*   9:    */ public class FileNameUtil
/*  10:    */ {
/*  11: 37 */   private final Map<String, String> compressSuffix = new HashMap();
/*  12:    */   private final Map<String, String> uncompressSuffix;
/*  13:    */   private final int longestCompressedSuffix;
/*  14:    */   private final int shortestCompressedSuffix;
/*  15:    */   private final int longestUncompressedSuffix;
/*  16:    */   private final int shortestUncompressedSuffix;
/*  17:    */   private final String defaultExtension;
/*  18:    */   
/*  19:    */   public FileNameUtil(Map<String, String> uncompressSuffix, String defaultExtension)
/*  20:    */   {
/*  21: 93 */     this.uncompressSuffix = Collections.unmodifiableMap(uncompressSuffix);
/*  22: 94 */     int lc = -2147483648;int sc = 2147483647;
/*  23: 95 */     int lu = -2147483648;int su = 2147483647;
/*  24: 96 */     for (Map.Entry<String, String> ent : uncompressSuffix.entrySet())
/*  25:    */     {
/*  26: 97 */       int cl = ((String)ent.getKey()).length();
/*  27: 98 */       if (cl > lc) {
/*  28: 99 */         lc = cl;
/*  29:    */       }
/*  30:101 */       if (cl < sc) {
/*  31:102 */         sc = cl;
/*  32:    */       }
/*  33:105 */       String u = (String)ent.getValue();
/*  34:106 */       int ul = u.length();
/*  35:107 */       if (ul > 0)
/*  36:    */       {
/*  37:108 */         if (!this.compressSuffix.containsKey(u)) {
/*  38:109 */           this.compressSuffix.put(u, ent.getKey());
/*  39:    */         }
/*  40:111 */         if (ul > lu) {
/*  41:112 */           lu = ul;
/*  42:    */         }
/*  43:114 */         if (ul < su) {
/*  44:115 */           su = ul;
/*  45:    */         }
/*  46:    */       }
/*  47:    */     }
/*  48:119 */     this.longestCompressedSuffix = lc;
/*  49:120 */     this.longestUncompressedSuffix = lu;
/*  50:121 */     this.shortestCompressedSuffix = sc;
/*  51:122 */     this.shortestUncompressedSuffix = su;
/*  52:123 */     this.defaultExtension = defaultExtension;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public boolean isCompressedFilename(String filename)
/*  56:    */   {
/*  57:134 */     String lower = filename.toLowerCase(Locale.ENGLISH);
/*  58:135 */     int n = lower.length();
/*  59:136 */     for (int i = this.shortestCompressedSuffix; (i <= this.longestCompressedSuffix) && (i < n); i++) {
/*  60:138 */       if (this.uncompressSuffix.containsKey(lower.substring(n - i))) {
/*  61:139 */         return true;
/*  62:    */       }
/*  63:    */     }
/*  64:142 */     return false;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public String getUncompressedFilename(String filename)
/*  68:    */   {
/*  69:159 */     String lower = filename.toLowerCase(Locale.ENGLISH);
/*  70:160 */     int n = lower.length();
/*  71:161 */     for (int i = this.shortestCompressedSuffix; (i <= this.longestCompressedSuffix) && (i < n); i++)
/*  72:    */     {
/*  73:163 */       String suffix = (String)this.uncompressSuffix.get(lower.substring(n - i));
/*  74:164 */       if (suffix != null) {
/*  75:165 */         return filename.substring(0, n - i) + suffix;
/*  76:    */       }
/*  77:    */     }
/*  78:168 */     return filename;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public String getCompressedFilename(String filename)
/*  82:    */   {
/*  83:183 */     String lower = filename.toLowerCase(Locale.ENGLISH);
/*  84:184 */     int n = lower.length();
/*  85:185 */     for (int i = this.shortestUncompressedSuffix; (i <= this.longestUncompressedSuffix) && (i < n); i++)
/*  86:    */     {
/*  87:187 */       String suffix = (String)this.compressSuffix.get(lower.substring(n - i));
/*  88:188 */       if (suffix != null) {
/*  89:189 */         return filename.substring(0, n - i) + suffix;
/*  90:    */       }
/*  91:    */     }
/*  92:193 */     return filename + this.defaultExtension;
/*  93:    */   }
/*  94:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.compressors.FileNameUtil
 * JD-Core Version:    0.7.0.1
 */