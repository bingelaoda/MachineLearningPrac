/*  1:   */ package weka.classifiers.mi.miti;
/*  2:   */ 
/*  3:   */ public class AlgorithmConfiguration
/*  4:   */ {
/*  5:   */   public int method;
/*  6:   */   public boolean unbiasedEstimate;
/*  7:   */   public int kBEPPConstant;
/*  8:   */   public boolean useBagStatistics;
/*  9:   */   public double bagCountMultiplier;
/* 10:   */   public int attributesToSplit;
/* 11:   */   public int attributeSplitChoices;
/* 12:   */   
/* 13:   */   public AlgorithmConfiguration()
/* 14:   */   {
/* 15:32 */     this.method = 2;
/* 16:33 */     this.unbiasedEstimate = false;
/* 17:34 */     this.useBagStatistics = false;
/* 18:35 */     this.kBEPPConstant = 5;
/* 19:36 */     this.bagCountMultiplier = 0.5D;
/* 20:37 */     this.attributeSplitChoices = 1;
/* 21:38 */     this.attributesToSplit = -1;
/* 22:   */   }
/* 23:   */   
/* 24:   */   public AlgorithmConfiguration(int method, boolean unbiasedEstimate, int kBEPPConstant, boolean bagStatistics, double bagCountMultiplier, int attributesToSplit, int attributeSplitChoices)
/* 25:   */   {
/* 26:48 */     this.method = method;
/* 27:49 */     this.unbiasedEstimate = unbiasedEstimate;
/* 28:50 */     this.useBagStatistics = bagStatistics;
/* 29:51 */     this.kBEPPConstant = kBEPPConstant;
/* 30:52 */     this.bagCountMultiplier = bagCountMultiplier;
/* 31:53 */     this.attributesToSplit = attributesToSplit;
/* 32:54 */     this.attributeSplitChoices = attributeSplitChoices;
/* 33:   */   }
/* 34:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.miti.AlgorithmConfiguration
 * JD-Core Version:    0.7.0.1
 */