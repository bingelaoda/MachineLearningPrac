/*  1:   */ package weka.classifiers.evaluation.output.prediction;
/*  2:   */ 
/*  3:   */ import weka.classifiers.Classifier;
/*  4:   */ import weka.core.Instance;
/*  5:   */ 
/*  6:   */ public class Null
/*  7:   */   extends AbstractOutput
/*  8:   */ {
/*  9:   */   private static final long serialVersionUID = 4988413155999044966L;
/* 10:   */   
/* 11:   */   public String globalInfo()
/* 12:   */   {
/* 13:76 */     return "Suppresses all output.";
/* 14:   */   }
/* 15:   */   
/* 16:   */   public String getDisplay()
/* 17:   */   {
/* 18:85 */     return "No output";
/* 19:   */   }
/* 20:   */   
/* 21:   */   public boolean generatesOutput()
/* 22:   */   {
/* 23:94 */     return false;
/* 24:   */   }
/* 25:   */   
/* 26:   */   protected void doPrintHeader() {}
/* 27:   */   
/* 28:   */   protected void doPrintClassification(Classifier classifier, Instance inst, int index)
/* 29:   */     throws Exception
/* 30:   */   {}
/* 31:   */   
/* 32:   */   protected void doPrintClassification(double[] dist, Instance inst, int index)
/* 33:   */     throws Exception
/* 34:   */   {}
/* 35:   */   
/* 36:   */   protected void doPrintFooter() {}
/* 37:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.evaluation.output.prediction.Null
 * JD-Core Version:    0.7.0.1
 */