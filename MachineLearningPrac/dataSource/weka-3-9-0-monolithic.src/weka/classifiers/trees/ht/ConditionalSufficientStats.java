/*  1:   */ package weka.classifiers.trees.ht;
/*  2:   */ 
/*  3:   */ import java.io.Serializable;
/*  4:   */ import java.util.HashMap;
/*  5:   */ import java.util.Map;
/*  6:   */ 
/*  7:   */ public abstract class ConditionalSufficientStats
/*  8:   */   implements Serializable
/*  9:   */ {
/* 10:   */   private static final long serialVersionUID = 8724787722646808376L;
/* 11:43 */   protected Map<String, Object> m_classLookup = new HashMap();
/* 12:   */   
/* 13:   */   public abstract void update(double paramDouble1, String paramString, double paramDouble2);
/* 14:   */   
/* 15:   */   public abstract double probabilityOfAttValConditionedOnClass(double paramDouble, String paramString);
/* 16:   */   
/* 17:   */   public abstract SplitCandidate bestSplit(SplitMetric paramSplitMetric, Map<String, WeightMass> paramMap, String paramString);
/* 18:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.ht.ConditionalSufficientStats
 * JD-Core Version:    0.7.0.1
 */