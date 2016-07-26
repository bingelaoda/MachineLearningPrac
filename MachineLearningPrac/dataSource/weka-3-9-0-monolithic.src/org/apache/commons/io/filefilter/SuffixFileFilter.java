/*   1:    */ package org.apache.commons.io.filefilter;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.util.List;
/*   5:    */ 
/*   6:    */ public class SuffixFileFilter
/*   7:    */   extends AbstractFileFilter
/*   8:    */ {
/*   9:    */   private String[] suffixes;
/*  10:    */   
/*  11:    */   public SuffixFileFilter(String suffix)
/*  12:    */   {
/*  13: 57 */     if (suffix == null) {
/*  14: 58 */       throw new IllegalArgumentException("The suffix must not be null");
/*  15:    */     }
/*  16: 60 */     this.suffixes = new String[] { suffix };
/*  17:    */   }
/*  18:    */   
/*  19:    */   public SuffixFileFilter(String[] suffixes)
/*  20:    */   {
/*  21: 73 */     if (suffixes == null) {
/*  22: 74 */       throw new IllegalArgumentException("The array of suffixes must not be null");
/*  23:    */     }
/*  24: 76 */     this.suffixes = suffixes;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public SuffixFileFilter(List suffixes)
/*  28:    */   {
/*  29: 87 */     if (suffixes == null) {
/*  30: 88 */       throw new IllegalArgumentException("The list of suffixes must not be null");
/*  31:    */     }
/*  32: 90 */     this.suffixes = ((String[])suffixes.toArray(new String[suffixes.size()]));
/*  33:    */   }
/*  34:    */   
/*  35:    */   public boolean accept(File file)
/*  36:    */   {
/*  37:100 */     String name = file.getName();
/*  38:101 */     for (int i = 0; i < this.suffixes.length; i++) {
/*  39:102 */       if (name.endsWith(this.suffixes[i])) {
/*  40:103 */         return true;
/*  41:    */       }
/*  42:    */     }
/*  43:106 */     return false;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public boolean accept(File file, String name)
/*  47:    */   {
/*  48:117 */     for (int i = 0; i < this.suffixes.length; i++) {
/*  49:118 */       if (name.endsWith(this.suffixes[i])) {
/*  50:119 */         return true;
/*  51:    */       }
/*  52:    */     }
/*  53:122 */     return false;
/*  54:    */   }
/*  55:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.filefilter.SuffixFileFilter
 * JD-Core Version:    0.7.0.1
 */