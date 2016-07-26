/*  1:   */ package weka.classifiers.mi.miti;
/*  2:   */ 
/*  3:   */ public class MaxBEPP
/*  4:   */   implements IBestSplitMeasure
/*  5:   */ {
/*  6:   */   public static double getMaxBEPP(SufficientStatistics ss, int kBEPPConstant, boolean unbiasedEstimate)
/*  7:   */   {
/*  8:36 */     double leftBEPP = BEPP.GetLeftBEPP(ss, kBEPPConstant, unbiasedEstimate);
/*  9:37 */     double rightBEPP = BEPP.GetRightBEPP(ss, kBEPPConstant, unbiasedEstimate);
/* 10:   */     
/* 11:39 */     return Math.max(leftBEPP, rightBEPP);
/* 12:   */   }
/* 13:   */   
/* 14:   */   public static double getMaxBEPP(double[] totalCounts, double[] positiveCounts, int kBEPPConstant, boolean unbiasedEstimate)
/* 15:   */   {
/* 16:47 */     double max = 0.0D;
/* 17:48 */     for (int i = 0; i < totalCounts.length; i++)
/* 18:   */     {
/* 19:50 */       double bepp = BEPP.GetBEPP(totalCounts[i], positiveCounts[i], kBEPPConstant, unbiasedEstimate);
/* 20:51 */       if (bepp > max) {
/* 21:52 */         max = bepp;
/* 22:   */       }
/* 23:   */     }
/* 24:54 */     return max;
/* 25:   */   }
/* 26:   */   
/* 27:   */   public double getScore(SufficientStatistics ss, int kBEPPConstant, boolean unbiasedEstimate)
/* 28:   */   {
/* 29:62 */     return getMaxBEPP(ss, kBEPPConstant, unbiasedEstimate);
/* 30:   */   }
/* 31:   */   
/* 32:   */   public double getScore(double[] totalCounts, double[] positiveCounts, int kBEPPConstant, boolean unbiasedEstimate)
/* 33:   */   {
/* 34:70 */     return getMaxBEPP(totalCounts, positiveCounts, kBEPPConstant, unbiasedEstimate);
/* 35:   */   }
/* 36:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.miti.MaxBEPP
 * JD-Core Version:    0.7.0.1
 */