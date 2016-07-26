/*   1:    */ package org.apache.commons.io.filefilter;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.util.List;
/*   5:    */ 
/*   6:    */ public class PrefixFileFilter
/*   7:    */   extends AbstractFileFilter
/*   8:    */ {
/*   9:    */   private String[] prefixes;
/*  10:    */   
/*  11:    */   public PrefixFileFilter(String prefix)
/*  12:    */   {
/*  13: 56 */     if (prefix == null) {
/*  14: 57 */       throw new IllegalArgumentException("The prefix must not be null");
/*  15:    */     }
/*  16: 59 */     this.prefixes = new String[] { prefix };
/*  17:    */   }
/*  18:    */   
/*  19:    */   public PrefixFileFilter(String[] prefixes)
/*  20:    */   {
/*  21: 72 */     if (prefixes == null) {
/*  22: 73 */       throw new IllegalArgumentException("The array of prefixes must not be null");
/*  23:    */     }
/*  24: 75 */     this.prefixes = prefixes;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public PrefixFileFilter(List prefixes)
/*  28:    */   {
/*  29: 86 */     if (prefixes == null) {
/*  30: 87 */       throw new IllegalArgumentException("The list of prefixes must not be null");
/*  31:    */     }
/*  32: 89 */     this.prefixes = ((String[])prefixes.toArray(new String[prefixes.size()]));
/*  33:    */   }
/*  34:    */   
/*  35:    */   public boolean accept(File file)
/*  36:    */   {
/*  37: 99 */     String name = file.getName();
/*  38:100 */     for (int i = 0; i < this.prefixes.length; i++) {
/*  39:101 */       if (name.startsWith(this.prefixes[i])) {
/*  40:102 */         return true;
/*  41:    */       }
/*  42:    */     }
/*  43:105 */     return false;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public boolean accept(File file, String name)
/*  47:    */   {
/*  48:116 */     for (int i = 0; i < this.prefixes.length; i++) {
/*  49:117 */       if (name.startsWith(this.prefixes[i])) {
/*  50:118 */         return true;
/*  51:    */       }
/*  52:    */     }
/*  53:121 */     return false;
/*  54:    */   }
/*  55:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.filefilter.PrefixFileFilter
 * JD-Core Version:    0.7.0.1
 */