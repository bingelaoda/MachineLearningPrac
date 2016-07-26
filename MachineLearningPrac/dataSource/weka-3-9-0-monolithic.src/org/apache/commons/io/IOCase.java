/*   1:    */ package org.apache.commons.io;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ 
/*   5:    */ public final class IOCase
/*   6:    */   implements Serializable
/*   7:    */ {
/*   8: 43 */   public static final IOCase SENSITIVE = new IOCase("Sensitive", true);
/*   9: 47 */   public static final IOCase INSENSITIVE = new IOCase("Insensitive", false);
/*  10: 55 */   public static final IOCase SYSTEM = new IOCase("System", !FilenameUtils.isSystemWindows());
/*  11:    */   private static final long serialVersionUID = -6343169151696340687L;
/*  12:    */   private final String name;
/*  13:    */   private final transient boolean sensitive;
/*  14:    */   
/*  15:    */   public static IOCase forName(String name)
/*  16:    */   {
/*  17: 74 */     if (SENSITIVE.name.equals(name)) {
/*  18: 75 */       return SENSITIVE;
/*  19:    */     }
/*  20: 77 */     if (INSENSITIVE.name.equals(name)) {
/*  21: 78 */       return INSENSITIVE;
/*  22:    */     }
/*  23: 80 */     if (SYSTEM.name.equals(name)) {
/*  24: 81 */       return SYSTEM;
/*  25:    */     }
/*  26: 83 */     throw new IllegalArgumentException("Invalid IOCase name: " + name);
/*  27:    */   }
/*  28:    */   
/*  29:    */   private IOCase(String name, boolean sensitive)
/*  30:    */   {
/*  31: 94 */     this.name = name;
/*  32: 95 */     this.sensitive = sensitive;
/*  33:    */   }
/*  34:    */   
/*  35:    */   private Object readResolve()
/*  36:    */   {
/*  37:105 */     return forName(this.name);
/*  38:    */   }
/*  39:    */   
/*  40:    */   public String getName()
/*  41:    */   {
/*  42:115 */     return this.name;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public boolean isCaseSensitive()
/*  46:    */   {
/*  47:124 */     return this.sensitive;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public boolean checkEquals(String str1, String str2)
/*  51:    */   {
/*  52:140 */     if ((str1 == null) || (str2 == null)) {
/*  53:141 */       throw new NullPointerException("The strings must not be null");
/*  54:    */     }
/*  55:143 */     return this.sensitive ? str1.equals(str2) : str1.equalsIgnoreCase(str2);
/*  56:    */   }
/*  57:    */   
/*  58:    */   public boolean checkStartsWith(String str, String start)
/*  59:    */   {
/*  60:158 */     return str.regionMatches(!this.sensitive, 0, start, 0, start.length());
/*  61:    */   }
/*  62:    */   
/*  63:    */   public boolean checkEndsWith(String str, String end)
/*  64:    */   {
/*  65:173 */     int endLen = end.length();
/*  66:174 */     return str.regionMatches(!this.sensitive, str.length() - endLen, end, 0, endLen);
/*  67:    */   }
/*  68:    */   
/*  69:    */   public boolean checkRegionMatches(String str, int strStartIndex, String search)
/*  70:    */   {
/*  71:190 */     return str.regionMatches(!this.sensitive, strStartIndex, search, 0, search.length());
/*  72:    */   }
/*  73:    */   
/*  74:    */   String convertCase(String str)
/*  75:    */   {
/*  76:201 */     if (str == null) {
/*  77:202 */       return null;
/*  78:    */     }
/*  79:204 */     return this.sensitive ? str : str.toLowerCase();
/*  80:    */   }
/*  81:    */   
/*  82:    */   public String toString()
/*  83:    */   {
/*  84:214 */     return this.name;
/*  85:    */   }
/*  86:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.IOCase
 * JD-Core Version:    0.7.0.1
 */