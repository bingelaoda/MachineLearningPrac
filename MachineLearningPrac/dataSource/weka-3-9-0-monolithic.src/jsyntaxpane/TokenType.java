/*  1:   */ package jsyntaxpane;
/*  2:   */ 
/*  3:   */ public enum TokenType
/*  4:   */ {
/*  5:23 */   OPERATOR,  DELIMITER,  KEYWORD,  KEYWORD2,  IDENTIFIER,  NUMBER,  STRING,  STRING2,  COMMENT,  COMMENT2,  REGEX,  REGEX2,  TYPE,  TYPE2,  TYPE3,  DEFAULT,  WARNING,  ERROR;
/*  6:   */   
/*  7:   */   private TokenType() {}
/*  8:   */   
/*  9:   */   public static boolean isComment(Token t)
/* 10:   */   {
/* 11:48 */     if ((t != null) && ((t.type == COMMENT) || (t.type == COMMENT2))) {
/* 12:49 */       return true;
/* 13:   */     }
/* 14:51 */     return false;
/* 15:   */   }
/* 16:   */   
/* 17:   */   public static boolean isKeyword(Token t)
/* 18:   */   {
/* 19:61 */     if ((t != null) && ((t.type == KEYWORD) || (t.type == KEYWORD2))) {
/* 20:62 */       return true;
/* 21:   */     }
/* 22:64 */     return false;
/* 23:   */   }
/* 24:   */   
/* 25:   */   public static boolean isString(Token t)
/* 26:   */   {
/* 27:75 */     if ((t != null) && ((t.type == STRING) || (t.type == STRING2))) {
/* 28:76 */       return true;
/* 29:   */     }
/* 30:78 */     return false;
/* 31:   */   }
/* 32:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.TokenType
 * JD-Core Version:    0.7.0.1
 */