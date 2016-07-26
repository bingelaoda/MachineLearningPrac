/*  1:   */ package jsyntaxpane.syntaxkits;
/*  2:   */ 
/*  3:   */ import jsyntaxpane.DefaultSyntaxKit;
/*  4:   */ import jsyntaxpane.Lexer;
/*  5:   */ import jsyntaxpane.lexers.ScalaLexer;
/*  6:   */ 
/*  7:   */ public class ScalaSyntaxKit
/*  8:   */   extends DefaultSyntaxKit
/*  9:   */ {
/* 10:   */   public ScalaSyntaxKit()
/* 11:   */   {
/* 12:27 */     super(new ScalaLexer());
/* 13:   */   }
/* 14:   */   
/* 15:   */   public ScalaSyntaxKit(Lexer lexer)
/* 16:   */   {
/* 17:31 */     super(lexer);
/* 18:   */   }
/* 19:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.syntaxkits.ScalaSyntaxKit
 * JD-Core Version:    0.7.0.1
 */