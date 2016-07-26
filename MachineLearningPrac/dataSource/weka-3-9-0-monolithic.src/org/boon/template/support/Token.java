/*  1:   */ package org.boon.template.support;
/*  2:   */ 
/*  3:   */ public class Token
/*  4:   */ {
/*  5:   */   int start;
/*  6:   */   int stop;
/*  7:   */   TokenTypes type;
/*  8:   */   
/*  9:   */   public int start()
/* 10:   */   {
/* 11:13 */     return this.start;
/* 12:   */   }
/* 13:   */   
/* 14:   */   public int stop()
/* 15:   */   {
/* 16:18 */     return this.stop;
/* 17:   */   }
/* 18:   */   
/* 19:   */   public TokenTypes type()
/* 20:   */   {
/* 21:22 */     return this.type;
/* 22:   */   }
/* 23:   */   
/* 24:   */   public Token(int start, int stop, TokenTypes type)
/* 25:   */   {
/* 26:26 */     this.start = start;
/* 27:27 */     this.stop = stop;
/* 28:28 */     this.type = type;
/* 29:   */   }
/* 30:   */   
/* 31:   */   public Token() {}
/* 32:   */   
/* 33:   */   public static Token text(int start, int stop)
/* 34:   */   {
/* 35:35 */     Token token = new Token();
/* 36:36 */     token.start = start;
/* 37:37 */     token.stop = stop;
/* 38:38 */     token.type = TokenTypes.TEXT;
/* 39:39 */     return token;
/* 40:   */   }
/* 41:   */   
/* 42:   */   public static Token commandStart(int start, int stop)
/* 43:   */   {
/* 44:44 */     Token token = new Token();
/* 45:45 */     token.start = start;
/* 46:46 */     token.stop = stop;
/* 47:47 */     token.type = TokenTypes.COMMAND;
/* 48:48 */     return token;
/* 49:   */   }
/* 50:   */   
/* 51:   */   public static Token commandBody(int start, int stop)
/* 52:   */   {
/* 53:52 */     Token token = new Token();
/* 54:53 */     token.start = start;
/* 55:54 */     token.stop = stop;
/* 56:55 */     token.type = TokenTypes.COMMAND_BODY;
/* 57:56 */     return token;
/* 58:   */   }
/* 59:   */   
/* 60:   */   public static Token expression(int start, int stop)
/* 61:   */   {
/* 62:60 */     Token token = new Token();
/* 63:61 */     token.start = start;
/* 64:62 */     token.stop = stop;
/* 65:63 */     token.type = TokenTypes.EXPRESSION;
/* 66:64 */     return token;
/* 67:   */   }
/* 68:   */   
/* 69:   */   public String toString()
/* 70:   */   {
/* 71:69 */     return "Token{start=" + this.start + ", stop=" + this.stop + ", type=" + this.type + '}';
/* 72:   */   }
/* 73:   */   
/* 74:   */   public void stop(int index)
/* 75:   */   {
/* 76:77 */     this.stop = index;
/* 77:   */   }
/* 78:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.template.support.Token
 * JD-Core Version:    0.7.0.1
 */