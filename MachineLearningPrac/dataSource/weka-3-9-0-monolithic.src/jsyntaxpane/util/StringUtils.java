/*  1:   */ package jsyntaxpane.util;
/*  2:   */ 
/*  3:   */ import java.io.Serializable;
/*  4:   */ import java.util.Comparator;
/*  5:   */ 
/*  6:   */ public class StringUtils
/*  7:   */ {
/*  8:   */   public static boolean camelCaseMatch(String word, String abbr)
/*  9:   */   {
/* 10:37 */     StringBuilder sb = new StringBuilder();
/* 11:38 */     sb.append(word.charAt(0));
/* 12:39 */     for (int i = 1; i < word.length(); i++)
/* 13:   */     {
/* 14:40 */       char c = word.charAt(i);
/* 15:41 */       if (Character.isUpperCase(c)) {
/* 16:42 */         sb.append(c);
/* 17:   */       }
/* 18:   */     }
/* 19:45 */     String cc = sb.toString();
/* 20:46 */     if (cc.startsWith(abbr)) {
/* 21:47 */       return true;
/* 22:   */     }
/* 23:49 */     return word.startsWith(abbr);
/* 24:   */   }
/* 25:   */   
/* 26:   */   static class CamelCaseCompare
/* 27:   */     implements Comparator<String>, Serializable
/* 28:   */   {
/* 29:   */     public int compare(String o1, String o2)
/* 30:   */     {
/* 31:57 */       throw new UnsupportedOperationException("Not supported yet.");
/* 32:   */     }
/* 33:   */   }
/* 34:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.util.StringUtils
 * JD-Core Version:    0.7.0.1
 */