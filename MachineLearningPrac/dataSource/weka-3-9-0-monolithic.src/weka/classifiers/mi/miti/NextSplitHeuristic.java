/*  1:   */ package weka.classifiers.mi.miti;
/*  2:   */ 
/*  3:   */ import java.util.Comparator;
/*  4:   */ import java.util.HashMap;
/*  5:   */ import java.util.List;
/*  6:   */ import weka.core.Instance;
/*  7:   */ 
/*  8:   */ public class NextSplitHeuristic
/*  9:   */   implements Comparator<TreeNode>
/* 10:   */ {
/* 11:   */   public int compare(TreeNode o1, TreeNode o2)
/* 12:   */   {
/* 13:42 */     return Double.compare(o1.nodeScore(), o2.nodeScore());
/* 14:   */   }
/* 15:   */   
/* 16:   */   public static double getBepp(List<Instance> instances, HashMap<Instance, Bag> instanceBags, boolean unbiasedEstimate, int kBEPPConstant, boolean bagCount, double multiplier)
/* 17:   */   {
/* 18:   */     SufficientStatistics ss;
/* 19:   */     SufficientStatistics ss;
/* 20:51 */     if (!bagCount) {
/* 21:52 */       ss = new SufficientInstanceStatistics(instances, instanceBags);
/* 22:   */     } else {
/* 23:54 */       ss = new SufficientBagStatistics(instances, instanceBags, multiplier);
/* 24:   */     }
/* 25:56 */     return BEPP.GetBEPP(ss.totalCountRight(), ss.positiveCountRight(), kBEPPConstant, unbiasedEstimate);
/* 26:   */   }
/* 27:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.miti.NextSplitHeuristic
 * JD-Core Version:    0.7.0.1
 */