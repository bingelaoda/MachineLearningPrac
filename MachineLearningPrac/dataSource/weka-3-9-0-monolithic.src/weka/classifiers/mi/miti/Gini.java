/*  1:   */ package weka.classifiers.mi.miti;
/*  2:   */ 
/*  3:   */ public class Gini
/*  4:   */   implements IBestSplitMeasure
/*  5:   */ {
/*  6:   */   public static double getGiniImpurity(SufficientStatistics ss, int kBEPPConstant, boolean unbiasedEstimate)
/*  7:   */   {
/*  8:37 */     double leftBEPP = BEPP.GetLeftBEPP(ss, kBEPPConstant, unbiasedEstimate);
/*  9:38 */     double rightBEPP = BEPP.GetRightBEPP(ss, kBEPPConstant, unbiasedEstimate);
/* 10:   */     
/* 11:   */ 
/* 12:   */ 
/* 13:   */ 
/* 14:   */ 
/* 15:   */ 
/* 16:45 */     return (leftBEPP * (1.0D - leftBEPP) * ss.totalCountLeft() + rightBEPP * (1.0D - rightBEPP) * ss.totalCountRight()) / (ss.totalCountLeft() + ss.totalCountRight());
/* 17:   */   }
/* 18:   */   
/* 19:   */   public static double getGiniImpurity(double[] totalCounts, double[] positiveCounts, int kBEPPConstant, boolean unbiasedEstimate)
/* 20:   */   {
/* 21:56 */     double score = 0.0D;
/* 22:57 */     double fullTotal = 0.0D;
/* 23:58 */     for (int i = 0; i < totalCounts.length; i++) {
/* 24:59 */       fullTotal += totalCounts[i];
/* 25:   */     }
/* 26:61 */     for (int i = 0; i < totalCounts.length; i++)
/* 27:   */     {
/* 28:62 */       double beppScore = BEPP.GetBEPP(totalCounts[i], positiveCounts[i], kBEPPConstant, unbiasedEstimate);
/* 29:   */       
/* 30:64 */       double gini = beppScore * (1.0D - beppScore);
/* 31:   */       
/* 32:   */ 
/* 33:   */ 
/* 34:68 */       score += gini * totalCounts[i] / fullTotal;
/* 35:   */     }
/* 36:71 */     return score;
/* 37:   */   }
/* 38:   */   
/* 39:   */   public double getScore(SufficientStatistics ss, int kBEPPConstant, boolean unbiasedEstimate)
/* 40:   */   {
/* 41:82 */     return 1.0D / getGiniImpurity(ss, kBEPPConstant, unbiasedEstimate);
/* 42:   */   }
/* 43:   */   
/* 44:   */   public double getScore(double[] totalCounts, double[] positiveCounts, int kBEPPConstant, boolean unbiasedEstimate)
/* 45:   */   {
/* 46:93 */     return 1.0D / getGiniImpurity(totalCounts, positiveCounts, kBEPPConstant, unbiasedEstimate);
/* 47:   */   }
/* 48:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.miti.Gini
 * JD-Core Version:    0.7.0.1
 */