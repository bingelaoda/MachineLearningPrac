/*  1:   */ package jsyntaxpane.syntaxkits;
/*  2:   */ 
/*  3:   */ import jsyntaxpane.DefaultSyntaxKit;
/*  4:   */ import jsyntaxpane.Lexer;
/*  5:   */ import jsyntaxpane.lexers.JavaLexer;
/*  6:   */ 
/*  7:   */ public class JavaSyntaxKit
/*  8:   */   extends DefaultSyntaxKit
/*  9:   */ {
/* 10:   */   public JavaSyntaxKit()
/* 11:   */   {
/* 12:27 */     super(new JavaLexer());
/* 13:   */   }
/* 14:   */   
/* 15:   */   JavaSyntaxKit(Lexer lexer)
/* 16:   */   {
/* 17:31 */     super(lexer);
/* 18:   */   }
/* 19:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.syntaxkits.JavaSyntaxKit
 * JD-Core Version:    0.7.0.1
 */