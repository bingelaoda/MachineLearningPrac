/*  1:   */ package org.boon.functions;
/*  2:   */ 
/*  3:   */ import org.boon.Str;
/*  4:   */ import org.boon.core.Function;
/*  5:   */ 
/*  6:   */ public class PropertyNameUtils
/*  7:   */ {
/*  8:37 */   public static Function<String, String> underBarCase = new Function()
/*  9:   */   {
/* 10:   */     public String apply(String in)
/* 11:   */     {
/* 12:40 */       return Str.underBarCase(in);
/* 13:   */     }
/* 14:   */   };
/* 15:43 */   public static Function<String, String> camelCase = new Function()
/* 16:   */   {
/* 17:   */     public String apply(String in)
/* 18:   */     {
/* 19:46 */       return Str.camelCase(in);
/* 20:   */     }
/* 21:   */   };
/* 22:51 */   public static Function<String, String> camelCaseUpper = new Function()
/* 23:   */   {
/* 24:   */     public String apply(String in)
/* 25:   */     {
/* 26:54 */       return Str.camelCaseUpper(in);
/* 27:   */     }
/* 28:   */   };
/* 29:59 */   public static Function<String, String> camelCaseLower = new Function()
/* 30:   */   {
/* 31:   */     public String apply(String in)
/* 32:   */     {
/* 33:62 */       return Str.camelCaseLower(in);
/* 34:   */     }
/* 35:   */   };
/* 36:66 */   public static Function<String, String> upperCase = new Function()
/* 37:   */   {
/* 38:   */     public String apply(String in)
/* 39:   */     {
/* 40:69 */       return in.toUpperCase();
/* 41:   */     }
/* 42:   */   };
/* 43:73 */   public static Function<String, String> lowerCase = new Function()
/* 44:   */   {
/* 45:   */     public String apply(String in)
/* 46:   */     {
/* 47:76 */       return in.toLowerCase();
/* 48:   */     }
/* 49:   */   };
/* 50:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.functions.PropertyNameUtils
 * JD-Core Version:    0.7.0.1
 */