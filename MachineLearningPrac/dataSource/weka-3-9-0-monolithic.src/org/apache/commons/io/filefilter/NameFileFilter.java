/*   1:    */ package org.apache.commons.io.filefilter;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.util.List;
/*   5:    */ import org.apache.commons.io.IOCase;
/*   6:    */ 
/*   7:    */ public class NameFileFilter
/*   8:    */   extends AbstractFileFilter
/*   9:    */ {
/*  10:    */   private String[] names;
/*  11:    */   private IOCase caseSensitivity;
/*  12:    */   
/*  13:    */   public NameFileFilter(String name)
/*  14:    */   {
/*  15: 60 */     this(name, null);
/*  16:    */   }
/*  17:    */   
/*  18:    */   public NameFileFilter(String name, IOCase caseSensitivity)
/*  19:    */   {
/*  20: 71 */     if (name == null) {
/*  21: 72 */       throw new IllegalArgumentException("The wildcard must not be null");
/*  22:    */     }
/*  23: 74 */     this.names = new String[] { name };
/*  24: 75 */     this.caseSensitivity = (caseSensitivity == null ? IOCase.SENSITIVE : caseSensitivity);
/*  25:    */   }
/*  26:    */   
/*  27:    */   public NameFileFilter(String[] names)
/*  28:    */   {
/*  29: 88 */     this(names, null);
/*  30:    */   }
/*  31:    */   
/*  32:    */   public NameFileFilter(String[] names, IOCase caseSensitivity)
/*  33:    */   {
/*  34:102 */     if (names == null) {
/*  35:103 */       throw new IllegalArgumentException("The array of names must not be null");
/*  36:    */     }
/*  37:105 */     this.names = names;
/*  38:106 */     this.caseSensitivity = (caseSensitivity == null ? IOCase.SENSITIVE : caseSensitivity);
/*  39:    */   }
/*  40:    */   
/*  41:    */   public NameFileFilter(List names)
/*  42:    */   {
/*  43:117 */     this(names, null);
/*  44:    */   }
/*  45:    */   
/*  46:    */   public NameFileFilter(List names, IOCase caseSensitivity)
/*  47:    */   {
/*  48:129 */     if (names == null) {
/*  49:130 */       throw new IllegalArgumentException("The list of names must not be null");
/*  50:    */     }
/*  51:132 */     this.names = ((String[])names.toArray(new String[names.size()]));
/*  52:133 */     this.caseSensitivity = (caseSensitivity == null ? IOCase.SENSITIVE : caseSensitivity);
/*  53:    */   }
/*  54:    */   
/*  55:    */   public boolean accept(File file)
/*  56:    */   {
/*  57:144 */     String name = file.getName();
/*  58:145 */     for (int i = 0; i < this.names.length; i++) {
/*  59:146 */       if (this.caseSensitivity.checkEquals(name, this.names[i])) {
/*  60:147 */         return true;
/*  61:    */       }
/*  62:    */     }
/*  63:150 */     return false;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public boolean accept(File file, String name)
/*  67:    */   {
/*  68:161 */     for (int i = 0; i < this.names.length; i++) {
/*  69:162 */       if (this.caseSensitivity.checkEquals(name, this.names[i])) {
/*  70:163 */         return true;
/*  71:    */       }
/*  72:    */     }
/*  73:166 */     return false;
/*  74:    */   }
/*  75:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.filefilter.NameFileFilter
 * JD-Core Version:    0.7.0.1
 */