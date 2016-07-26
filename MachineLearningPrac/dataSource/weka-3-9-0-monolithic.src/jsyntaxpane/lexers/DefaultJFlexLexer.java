/*   1:    */ package jsyntaxpane.lexers;
/*   2:    */ 
/*   3:    */ import java.io.CharArrayReader;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.Reader;
/*   6:    */ import java.util.List;
/*   7:    */ import java.util.logging.Level;
/*   8:    */ import java.util.logging.Logger;
/*   9:    */ import javax.swing.text.Segment;
/*  10:    */ import jsyntaxpane.Lexer;
/*  11:    */ import jsyntaxpane.Token;
/*  12:    */ import jsyntaxpane.TokenType;
/*  13:    */ 
/*  14:    */ public abstract class DefaultJFlexLexer
/*  15:    */   implements Lexer
/*  16:    */ {
/*  17:    */   protected int tokenStart;
/*  18:    */   protected int tokenLength;
/*  19:    */   protected int offset;
/*  20:    */   
/*  21:    */   protected Token token(TokenType type, int tStart, int tLength, int newStart, int newLength)
/*  22:    */   {
/*  23: 50 */     this.tokenStart = newStart;
/*  24: 51 */     this.tokenLength = newLength;
/*  25: 52 */     return new Token(type, tStart + this.offset, tLength);
/*  26:    */   }
/*  27:    */   
/*  28:    */   protected Token token(TokenType type, int start, int length)
/*  29:    */   {
/*  30: 64 */     return new Token(type, start + this.offset, length);
/*  31:    */   }
/*  32:    */   
/*  33:    */   protected Token token(TokenType type)
/*  34:    */   {
/*  35: 76 */     return new Token(type, yychar() + this.offset, yylength());
/*  36:    */   }
/*  37:    */   
/*  38:    */   protected Token token(TokenType type, int pairValue)
/*  39:    */   {
/*  40: 90 */     return new Token(type, yychar() + this.offset, yylength(), (byte)pairValue);
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void parse(Segment segment, int ofst, List<Token> tokens)
/*  44:    */   {
/*  45:    */     try
/*  46:    */     {
/*  47:100 */       CharArrayReader reader = new CharArrayReader(segment.array, segment.offset, segment.count);
/*  48:101 */       yyreset(reader);
/*  49:102 */       this.offset = ofst;
/*  50:103 */       for (Token t = yylex(); t != null; t = yylex()) {
/*  51:104 */         tokens.add(t);
/*  52:    */       }
/*  53:    */     }
/*  54:    */     catch (IOException ex)
/*  55:    */     {
/*  56:107 */       Logger.getLogger(DefaultJFlexLexer.class.getName()).log(Level.SEVERE, null, ex);
/*  57:    */     }
/*  58:    */   }
/*  59:    */   
/*  60:    */   public abstract void yyreset(Reader paramReader);
/*  61:    */   
/*  62:    */   public abstract Token yylex()
/*  63:    */     throws IOException;
/*  64:    */   
/*  65:    */   public abstract char yycharat(int paramInt);
/*  66:    */   
/*  67:    */   public abstract int yylength();
/*  68:    */   
/*  69:    */   public abstract String yytext();
/*  70:    */   
/*  71:    */   public abstract int yychar();
/*  72:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.lexers.DefaultJFlexLexer
 * JD-Core Version:    0.7.0.1
 */