/*  1:   */ package jsyntaxpane.syntaxkits;
/*  2:   */ 
/*  3:   */ import jsyntaxpane.DefaultSyntaxKit;
/*  4:   */ import jsyntaxpane.Lexer;
/*  5:   */ import jsyntaxpane.lexers.ClojureLexer;
/*  6:   */ 
/*  7:   */ public class ClojureSyntaxKit
/*  8:   */   extends DefaultSyntaxKit
/*  9:   */ {
/* 10:   */   public ClojureSyntaxKit()
/* 11:   */   {
/* 12:27 */     super(new ClojureLexer());
/* 13:   */   }
/* 14:   */   
/* 15:   */   public ClojureSyntaxKit(Lexer lexer)
/* 16:   */   {
/* 17:31 */     super(lexer);
/* 18:   */   }
/* 19:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.syntaxkits.ClojureSyntaxKit
 * JD-Core Version:    0.7.0.1
 */