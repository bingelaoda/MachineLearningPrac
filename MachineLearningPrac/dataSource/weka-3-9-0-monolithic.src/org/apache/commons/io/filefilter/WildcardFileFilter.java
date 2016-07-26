/*   1:    */ package org.apache.commons.io.filefilter;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.util.List;
/*   5:    */ import org.apache.commons.io.FilenameUtils;
/*   6:    */ import org.apache.commons.io.IOCase;
/*   7:    */ 
/*   8:    */ public class WildcardFileFilter
/*   9:    */   extends AbstractFileFilter
/*  10:    */ {
/*  11:    */   private String[] wildcards;
/*  12:    */   private IOCase caseSensitivity;
/*  13:    */   
/*  14:    */   public WildcardFileFilter(String wildcard)
/*  15:    */   {
/*  16: 65 */     this(wildcard, null);
/*  17:    */   }
/*  18:    */   
/*  19:    */   public WildcardFileFilter(String wildcard, IOCase caseSensitivity)
/*  20:    */   {
/*  21: 76 */     if (wildcard == null) {
/*  22: 77 */       throw new IllegalArgumentException("The wildcard must not be null");
/*  23:    */     }
/*  24: 79 */     this.wildcards = new String[] { wildcard };
/*  25: 80 */     this.caseSensitivity = (caseSensitivity == null ? IOCase.SENSITIVE : caseSensitivity);
/*  26:    */   }
/*  27:    */   
/*  28:    */   public WildcardFileFilter(String[] wildcards)
/*  29:    */   {
/*  30: 93 */     this(wildcards, null);
/*  31:    */   }
/*  32:    */   
/*  33:    */   public WildcardFileFilter(String[] wildcards, IOCase caseSensitivity)
/*  34:    */   {
/*  35:107 */     if (wildcards == null) {
/*  36:108 */       throw new IllegalArgumentException("The wildcard array must not be null");
/*  37:    */     }
/*  38:110 */     this.wildcards = wildcards;
/*  39:111 */     this.caseSensitivity = (caseSensitivity == null ? IOCase.SENSITIVE : caseSensitivity);
/*  40:    */   }
/*  41:    */   
/*  42:    */   public WildcardFileFilter(List wildcards)
/*  43:    */   {
/*  44:122 */     this(wildcards, null);
/*  45:    */   }
/*  46:    */   
/*  47:    */   public WildcardFileFilter(List wildcards, IOCase caseSensitivity)
/*  48:    */   {
/*  49:134 */     if (wildcards == null) {
/*  50:135 */       throw new IllegalArgumentException("The wildcard list must not be null");
/*  51:    */     }
/*  52:137 */     this.wildcards = ((String[])wildcards.toArray(new String[wildcards.size()]));
/*  53:138 */     this.caseSensitivity = (caseSensitivity == null ? IOCase.SENSITIVE : caseSensitivity);
/*  54:    */   }
/*  55:    */   
/*  56:    */   public boolean accept(File dir, String name)
/*  57:    */   {
/*  58:150 */     for (int i = 0; i < this.wildcards.length; i++) {
/*  59:151 */       if (FilenameUtils.wildcardMatch(name, this.wildcards[i], this.caseSensitivity)) {
/*  60:152 */         return true;
/*  61:    */       }
/*  62:    */     }
/*  63:155 */     return false;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public boolean accept(File file)
/*  67:    */   {
/*  68:165 */     String name = file.getName();
/*  69:166 */     for (int i = 0; i < this.wildcards.length; i++) {
/*  70:167 */       if (FilenameUtils.wildcardMatch(name, this.wildcards[i], this.caseSensitivity)) {
/*  71:168 */         return true;
/*  72:    */       }
/*  73:    */     }
/*  74:171 */     return false;
/*  75:    */   }
/*  76:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.filefilter.WildcardFileFilter
 * JD-Core Version:    0.7.0.1
 */