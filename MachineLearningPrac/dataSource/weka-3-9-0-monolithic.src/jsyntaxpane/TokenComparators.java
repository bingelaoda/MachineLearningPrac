/*  1:   */ package jsyntaxpane;
/*  2:   */ 
/*  3:   */ import java.io.Serializable;
/*  4:   */ import java.util.Comparator;
/*  5:   */ 
/*  6:   */ public class TokenComparators
/*  7:   */ {
/*  8:25 */   public static final Comparator<Token> LONGEST_FIRST = new LongestFirst(null);
/*  9:26 */   public static final Comparator<Token> SHORTEST_FIRST = new ShortestFirst(null);
/* 10:   */   
/* 11:   */   private static class LongestFirst
/* 12:   */     implements Comparator<Token>, Serializable
/* 13:   */   {
/* 14:   */     public int compare(Token t1, Token t2)
/* 15:   */     {
/* 16:32 */       if (t1.start != t2.start) {
/* 17:33 */         return t1.start - t2.start;
/* 18:   */       }
/* 19:34 */       if (t1.length != t2.length) {
/* 20:35 */         return t2.length - t1.length;
/* 21:   */       }
/* 22:37 */       return t1.type.compareTo(t2.type);
/* 23:   */     }
/* 24:   */   }
/* 25:   */   
/* 26:   */   private static class ShortestFirst
/* 27:   */     implements Comparator<Token>, Serializable
/* 28:   */   {
/* 29:   */     public int compare(Token t1, Token t2)
/* 30:   */     {
/* 31:46 */       if (t1.start != t2.start) {
/* 32:47 */         return t1.start - t2.start;
/* 33:   */       }
/* 34:48 */       if (t1.length != t2.length) {
/* 35:49 */         return t1.length - t2.length;
/* 36:   */       }
/* 37:51 */       return t1.type.compareTo(t2.type);
/* 38:   */     }
/* 39:   */   }
/* 40:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.TokenComparators
 * JD-Core Version:    0.7.0.1
 */