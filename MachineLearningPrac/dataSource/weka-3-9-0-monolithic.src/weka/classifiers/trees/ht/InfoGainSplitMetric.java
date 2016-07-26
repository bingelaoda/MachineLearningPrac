/*   1:    */ package weka.classifiers.trees.ht;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.List;
/*   5:    */ import java.util.Map;
/*   6:    */ import java.util.Map.Entry;
/*   7:    */ import weka.core.ContingencyTables;
/*   8:    */ import weka.core.Utils;
/*   9:    */ 
/*  10:    */ public class InfoGainSplitMetric
/*  11:    */   extends SplitMetric
/*  12:    */   implements Serializable
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = 2173840581308675428L;
/*  15:    */   protected double m_minFracWeightForTwoBranches;
/*  16:    */   
/*  17:    */   public InfoGainSplitMetric(double minFracWeightForTwoBranches)
/*  18:    */   {
/*  19: 48 */     this.m_minFracWeightForTwoBranches = minFracWeightForTwoBranches;
/*  20:    */   }
/*  21:    */   
/*  22:    */   public double evaluateSplit(Map<String, WeightMass> preDist, List<Map<String, WeightMass>> postDist)
/*  23:    */   {
/*  24: 55 */     double[] pre = new double[preDist.size()];
/*  25: 56 */     int count = 0;
/*  26: 57 */     for (Map.Entry<String, WeightMass> e : preDist.entrySet()) {
/*  27: 58 */       pre[(count++)] = ((WeightMass)e.getValue()).m_weight;
/*  28:    */     }
/*  29: 61 */     double preEntropy = ContingencyTables.entropy(pre);
/*  30:    */     
/*  31: 63 */     double[] distWeights = new double[postDist.size()];
/*  32: 64 */     double totalWeight = 0.0D;
/*  33: 65 */     for (int i = 0; i < postDist.size(); i++)
/*  34:    */     {
/*  35: 66 */       distWeights[i] = SplitMetric.sum((Map)postDist.get(i));
/*  36: 67 */       totalWeight += distWeights[i];
/*  37:    */     }
/*  38: 70 */     int fracCount = 0;
/*  39: 71 */     for (double d : distWeights) {
/*  40: 72 */       if (d / totalWeight > this.m_minFracWeightForTwoBranches) {
/*  41: 73 */         fracCount++;
/*  42:    */       }
/*  43:    */     }
/*  44: 77 */     if (fracCount < 2) {
/*  45: 78 */       return (-1.0D / 0.0D);
/*  46:    */     }
/*  47: 81 */     double postEntropy = 0.0D;
/*  48: 82 */     for (int i = 0; i < postDist.size(); i++)
/*  49:    */     {
/*  50: 83 */       Map<String, WeightMass> d = (Map)postDist.get(i);
/*  51: 84 */       double[] post = new double[d.size()];
/*  52: 85 */       count = 0;
/*  53: 86 */       for (Map.Entry<String, WeightMass> e : d.entrySet()) {
/*  54: 87 */         post[(count++)] = ((WeightMass)e.getValue()).m_weight;
/*  55:    */       }
/*  56: 89 */       postEntropy += distWeights[i] * ContingencyTables.entropy(post);
/*  57:    */     }
/*  58: 92 */     if (totalWeight > 0.0D) {
/*  59: 93 */       postEntropy /= totalWeight;
/*  60:    */     }
/*  61: 96 */     return preEntropy - postEntropy;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public double getMetricRange(Map<String, WeightMass> preDist)
/*  65:    */   {
/*  66:102 */     int numClasses = preDist.size();
/*  67:103 */     if (numClasses < 2) {
/*  68:104 */       numClasses = 2;
/*  69:    */     }
/*  70:107 */     return Utils.log2(numClasses);
/*  71:    */   }
/*  72:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.ht.InfoGainSplitMetric
 * JD-Core Version:    0.7.0.1
 */