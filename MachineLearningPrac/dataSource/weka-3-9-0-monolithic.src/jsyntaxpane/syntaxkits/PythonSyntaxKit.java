/*  1:   */ package jsyntaxpane.syntaxkits;
/*  2:   */ 
/*  3:   */ import jsyntaxpane.DefaultSyntaxKit;
/*  4:   */ import jsyntaxpane.lexers.PythonLexer;
/*  5:   */ 
/*  6:   */ public class PythonSyntaxKit
/*  7:   */   extends DefaultSyntaxKit
/*  8:   */ {
/*  9:   */   public PythonSyntaxKit()
/* 10:   */   {
/* 11:26 */     super(new PythonLexer());
/* 12:   */   }
/* 13:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.syntaxkits.PythonSyntaxKit
 * JD-Core Version:    0.7.0.1
 */