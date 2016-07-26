/*  1:   */ package jsyntaxpane.syntaxkits;
/*  2:   */ 
/*  3:   */ import jsyntaxpane.DefaultSyntaxKit;
/*  4:   */ import jsyntaxpane.lexers.EmptyLexer;
/*  5:   */ 
/*  6:   */ public class PlainSyntaxKit
/*  7:   */   extends DefaultSyntaxKit
/*  8:   */ {
/*  9:   */   public PlainSyntaxKit()
/* 10:   */   {
/* 11:26 */     super(new EmptyLexer());
/* 12:   */   }
/* 13:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.syntaxkits.PlainSyntaxKit
 * JD-Core Version:    0.7.0.1
 */