/*  1:   */ package weka.classifiers.trees.ht;
/*  2:   */ 
/*  3:   */ import java.io.Serializable;
/*  4:   */ import java.util.List;
/*  5:   */ import java.util.Map;
/*  6:   */ import java.util.Map.Entry;
/*  7:   */ 
/*  8:   */ public class GiniSplitMetric
/*  9:   */   extends SplitMetric
/* 10:   */   implements Serializable
/* 11:   */ {
/* 12:   */   private static final long serialVersionUID = -2037586582742660298L;
/* 13:   */   
/* 14:   */   public double evaluateSplit(Map<String, WeightMass> preDist, List<Map<String, WeightMass>> postDist)
/* 15:   */   {
/* 16:45 */     double totalWeight = 0.0D;
/* 17:46 */     double[] distWeights = new double[postDist.size()];
/* 18:48 */     for (int i = 0; i < postDist.size(); i++)
/* 19:   */     {
/* 20:49 */       distWeights[i] = SplitMetric.sum((Map)postDist.get(i));
/* 21:50 */       totalWeight += distWeights[i];
/* 22:   */     }
/* 23:52 */     double gini = 0.0D;
/* 24:53 */     for (int i = 0; i < postDist.size(); i++) {
/* 25:54 */       gini += distWeights[i] / totalWeight * gini((Map)postDist.get(i), distWeights[i]);
/* 26:   */     }
/* 27:58 */     return 1.0D - gini;
/* 28:   */   }
/* 29:   */   
/* 30:   */   protected static double gini(Map<String, WeightMass> dist, double sumOfWeights)
/* 31:   */   {
/* 32:69 */     double gini = 1.0D;
/* 33:71 */     for (Map.Entry<String, WeightMass> e : dist.entrySet())
/* 34:   */     {
/* 35:72 */       double frac = ((WeightMass)e.getValue()).m_weight / sumOfWeights;
/* 36:73 */       gini -= frac * frac;
/* 37:   */     }
/* 38:76 */     return gini;
/* 39:   */   }
/* 40:   */   
/* 41:   */   public static double gini(Map<String, WeightMass> dist)
/* 42:   */   {
/* 43:86 */     return gini(dist, SplitMetric.sum(dist));
/* 44:   */   }
/* 45:   */   
/* 46:   */   public double getMetricRange(Map<String, WeightMass> preDist)
/* 47:   */   {
/* 48:91 */     return 1.0D;
/* 49:   */   }
/* 50:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.ht.GiniSplitMetric
 * JD-Core Version:    0.7.0.1
 */