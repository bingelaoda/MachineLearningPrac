/*  1:   */ package weka.core.stopwords;
/*  2:   */ 
/*  3:   */ public class Null
/*  4:   */   extends AbstractStopwords
/*  5:   */ {
/*  6:   */   private static final long serialVersionUID = -3319681866579617385L;
/*  7:   */   
/*  8:   */   public String globalInfo()
/*  9:   */   {
/* 10:54 */     return "Dummy stopwords scheme, always returns false.";
/* 11:   */   }
/* 12:   */   
/* 13:   */   protected boolean is(String word)
/* 14:   */   {
/* 15:66 */     return false;
/* 16:   */   }
/* 17:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.stopwords.Null
 * JD-Core Version:    0.7.0.1
 */