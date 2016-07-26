/*  1:   */ package weka.classifiers.mi.miti;
/*  2:   */ 
/*  3:   */ import java.io.PrintStream;
/*  4:   */ 
/*  5:   */ public class SSBEPP
/*  6:   */   implements IBestSplitMeasure
/*  7:   */ {
/*  8:   */   public static double getSSBEPP(SufficientStatistics ss, int kBEPPConstant, boolean unbiasedEstimate)
/*  9:   */   {
/* 10:37 */     double leftBEPP = BEPP.GetLeftBEPP(ss, kBEPPConstant, unbiasedEstimate);
/* 11:38 */     double rightBEPP = BEPP.GetRightBEPP(ss, kBEPPConstant, unbiasedEstimate);
/* 12:39 */     return leftBEPP * leftBEPP / 2.0D + rightBEPP * rightBEPP / 2.0D;
/* 13:   */   }
/* 14:   */   
/* 15:   */   public double getScore(SufficientStatistics ss, int kBEPPConstant, boolean unbiasedEstimate)
/* 16:   */   {
/* 17:47 */     return getSSBEPP(ss, kBEPPConstant, unbiasedEstimate);
/* 18:   */   }
/* 19:   */   
/* 20:   */   public double getScore(double[] totalCounts, double[] positiveCounts, int kBEPPConstant, boolean unbiasedEstimate)
/* 21:   */   {
/* 22:56 */     System.out.println("Implementation of SSBEPP not available for nominal attributes");
/* 23:57 */     System.exit(1);
/* 24:58 */     return 0.0D;
/* 25:   */   }
/* 26:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.miti.SSBEPP
 * JD-Core Version:    0.7.0.1
 */