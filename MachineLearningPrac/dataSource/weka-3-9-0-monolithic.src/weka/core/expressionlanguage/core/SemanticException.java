/*  1:   */ package weka.core.expressionlanguage.core;
/*  2:   */ 
/*  3:   */ public class SemanticException
/*  4:   */   extends Exception
/*  5:   */ {
/*  6:   */   private static final long serialVersionUID = -1212116135142845573L;
/*  7:   */   
/*  8:   */   public SemanticException(String msg, Exception e)
/*  9:   */   {
/* 10:42 */     super(msg, e);
/* 11:   */   }
/* 12:   */   
/* 13:   */   public SemanticException(String msg)
/* 14:   */   {
/* 15:51 */     super(msg);
/* 16:   */   }
/* 17:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.expressionlanguage.core.SemanticException
 * JD-Core Version:    0.7.0.1
 */