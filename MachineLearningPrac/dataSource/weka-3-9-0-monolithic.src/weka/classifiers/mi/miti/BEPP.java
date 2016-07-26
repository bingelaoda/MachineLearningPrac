/*  1:   */ package weka.classifiers.mi.miti;
/*  2:   */ 
/*  3:   */ public class BEPP
/*  4:   */ {
/*  5:   */   public static double GetLeftBEPP(SufficientStatistics ss, int kBEPPConstant, boolean unbiasedEstimate)
/*  6:   */   {
/*  7:37 */     return GetBEPP(ss.totalCountLeft(), ss.positiveCountLeft(), kBEPPConstant, unbiasedEstimate);
/*  8:   */   }
/*  9:   */   
/* 10:   */   public static double GetRightBEPP(SufficientStatistics ss, int kBEPPConstant, boolean unbiasedEstimate)
/* 11:   */   {
/* 12:45 */     return GetBEPP(ss.totalCountRight(), ss.positiveCountRight(), kBEPPConstant, unbiasedEstimate);
/* 13:   */   }
/* 14:   */   
/* 15:   */   public static double GetBEPP(SufficientStatistics ss, int kBEPPConstant, boolean unbiasedEstimate)
/* 16:   */   {
/* 17:54 */     return GetBEPP(ss.totalCountLeft() + ss.totalCountRight(), ss.positiveCountLeft() + ss.positiveCountRight(), kBEPPConstant, unbiasedEstimate);
/* 18:   */   }
/* 19:   */   
/* 20:   */   public static double GetBEPP(double totalCount, double positiveCount, int kBEPPConstant, boolean unbiasedEstimate)
/* 21:   */   {
/* 22:62 */     if (unbiasedEstimate) {
/* 23:63 */       return (positiveCount + kBEPPConstant / 2.0D) / (totalCount + kBEPPConstant);
/* 24:   */     }
/* 25:65 */     return positiveCount / (totalCount + kBEPPConstant);
/* 26:   */   }
/* 27:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.miti.BEPP
 * JD-Core Version:    0.7.0.1
 */