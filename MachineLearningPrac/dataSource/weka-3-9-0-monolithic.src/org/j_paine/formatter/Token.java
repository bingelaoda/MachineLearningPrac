/*  1:   */ package org.j_paine.formatter;
/*  2:   */ 
/*  3:   */ public class Token
/*  4:   */ {
/*  5:   */   public int kind;
/*  6:   */   public int beginLine;
/*  7:   */   public int beginColumn;
/*  8:   */   public int endLine;
/*  9:   */   public int endColumn;
/* 10:   */   public String image;
/* 11:   */   public Token next;
/* 12:   */   public Token specialToken;
/* 13:   */   
/* 14:   */   public String toString()
/* 15:   */   {
/* 16:58 */     return this.image;
/* 17:   */   }
/* 18:   */   
/* 19:   */   public static final Token newToken(int paramInt)
/* 20:   */   {
/* 21:75 */     switch (paramInt)
/* 22:   */     {
/* 23:   */     }
/* 24:77 */     return new Token();
/* 25:   */   }
/* 26:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.Token
 * JD-Core Version:    0.7.0.1
 */