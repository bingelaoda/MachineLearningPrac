/*  1:   */ package jsyntaxpane.syntaxkits;
/*  2:   */ 
/*  3:   */ import jsyntaxpane.DefaultSyntaxKit;
/*  4:   */ import jsyntaxpane.lexers.DOSBatchLexer;
/*  5:   */ 
/*  6:   */ public class DOSBatchSyntaxKit
/*  7:   */   extends DefaultSyntaxKit
/*  8:   */ {
/*  9:   */   public DOSBatchSyntaxKit()
/* 10:   */   {
/* 11:26 */     super(new DOSBatchLexer());
/* 12:   */   }
/* 13:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.syntaxkits.DOSBatchSyntaxKit
 * JD-Core Version:    0.7.0.1
 */