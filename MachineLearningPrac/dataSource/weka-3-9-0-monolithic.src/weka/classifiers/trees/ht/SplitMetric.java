/*  1:   */ package weka.classifiers.trees.ht;
/*  2:   */ 
/*  3:   */ import java.io.Serializable;
/*  4:   */ import java.util.List;
/*  5:   */ import java.util.Map;
/*  6:   */ import java.util.Map.Entry;
/*  7:   */ 
/*  8:   */ public abstract class SplitMetric
/*  9:   */   implements Serializable
/* 10:   */ {
/* 11:   */   private static final long serialVersionUID = 2891555018707080818L;
/* 12:   */   
/* 13:   */   public static double sum(Map<String, WeightMass> dist)
/* 14:   */   {
/* 15:49 */     double sum = 0.0D;
/* 16:51 */     for (Map.Entry<String, WeightMass> e : dist.entrySet()) {
/* 17:52 */       sum += ((WeightMass)e.getValue()).m_weight;
/* 18:   */     }
/* 19:55 */     return sum;
/* 20:   */   }
/* 21:   */   
/* 22:   */   public abstract double evaluateSplit(Map<String, WeightMass> paramMap, List<Map<String, WeightMass>> paramList);
/* 23:   */   
/* 24:   */   public abstract double getMetricRange(Map<String, WeightMass> paramMap);
/* 25:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.ht.SplitMetric
 * JD-Core Version:    0.7.0.1
 */