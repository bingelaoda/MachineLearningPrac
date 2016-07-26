/*  1:   */ package jsyntaxpane.syntaxkits;
/*  2:   */ 
/*  3:   */ import jsyntaxpane.DefaultSyntaxKit;
/*  4:   */ import jsyntaxpane.Lexer;
/*  5:   */ import jsyntaxpane.lexers.CLexer;
/*  6:   */ 
/*  7:   */ public class CSyntaxKit
/*  8:   */   extends DefaultSyntaxKit
/*  9:   */ {
/* 10:   */   public CSyntaxKit()
/* 11:   */   {
/* 12:27 */     super(new CLexer());
/* 13:   */   }
/* 14:   */   
/* 15:   */   CSyntaxKit(Lexer lexer)
/* 16:   */   {
/* 17:36 */     super(lexer);
/* 18:   */   }
/* 19:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.syntaxkits.CSyntaxKit
 * JD-Core Version:    0.7.0.1
 */