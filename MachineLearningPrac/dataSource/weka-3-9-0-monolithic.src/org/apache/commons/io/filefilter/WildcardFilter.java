/*   1:    */ package org.apache.commons.io.filefilter;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.util.List;
/*   5:    */ import org.apache.commons.io.FilenameUtils;
/*   6:    */ 
/*   7:    */ /**
/*   8:    */  * @deprecated
/*   9:    */  */
/*  10:    */ public class WildcardFilter
/*  11:    */   extends AbstractFileFilter
/*  12:    */ {
/*  13:    */   private String[] wildcards;
/*  14:    */   
/*  15:    */   public WildcardFilter(String wildcard)
/*  16:    */   {
/*  17: 64 */     if (wildcard == null) {
/*  18: 65 */       throw new IllegalArgumentException("The wildcard must not be null");
/*  19:    */     }
/*  20: 67 */     this.wildcards = new String[] { wildcard };
/*  21:    */   }
/*  22:    */   
/*  23:    */   public WildcardFilter(String[] wildcards)
/*  24:    */   {
/*  25: 77 */     if (wildcards == null) {
/*  26: 78 */       throw new IllegalArgumentException("The wildcard array must not be null");
/*  27:    */     }
/*  28: 80 */     this.wildcards = wildcards;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public WildcardFilter(List wildcards)
/*  32:    */   {
/*  33: 91 */     if (wildcards == null) {
/*  34: 92 */       throw new IllegalArgumentException("The wildcard list must not be null");
/*  35:    */     }
/*  36: 94 */     this.wildcards = ((String[])wildcards.toArray(new String[wildcards.size()]));
/*  37:    */   }
/*  38:    */   
/*  39:    */   public boolean accept(File dir, String name)
/*  40:    */   {
/*  41:106 */     if ((dir != null) && (new File(dir, name).isDirectory())) {
/*  42:107 */       return false;
/*  43:    */     }
/*  44:110 */     for (int i = 0; i < this.wildcards.length; i++) {
/*  45:111 */       if (FilenameUtils.wildcardMatch(name, this.wildcards[i])) {
/*  46:112 */         return true;
/*  47:    */       }
/*  48:    */     }
/*  49:116 */     return false;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public boolean accept(File file)
/*  53:    */   {
/*  54:126 */     if (file.isDirectory()) {
/*  55:127 */       return false;
/*  56:    */     }
/*  57:130 */     for (int i = 0; i < this.wildcards.length; i++) {
/*  58:131 */       if (FilenameUtils.wildcardMatch(file.getName(), this.wildcards[i])) {
/*  59:132 */         return true;
/*  60:    */       }
/*  61:    */     }
/*  62:136 */     return false;
/*  63:    */   }
/*  64:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.filefilter.WildcardFilter
 * JD-Core Version:    0.7.0.1
 */