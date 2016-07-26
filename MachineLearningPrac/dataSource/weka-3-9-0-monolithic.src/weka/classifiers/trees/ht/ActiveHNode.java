/*   1:    */ package weka.classifiers.trees.ht;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.HashMap;
/*   6:    */ import java.util.List;
/*   7:    */ import java.util.Map;
/*   8:    */ import java.util.Map.Entry;
/*   9:    */ import weka.core.Attribute;
/*  10:    */ import weka.core.Instance;
/*  11:    */ 
/*  12:    */ public class ActiveHNode
/*  13:    */   extends LeafNode
/*  14:    */   implements LearningNode, Serializable
/*  15:    */ {
/*  16:    */   private static final long serialVersionUID = 3284585939739561683L;
/*  17: 48 */   public double m_weightSeenAtLastSplitEval = 0.0D;
/*  18: 51 */   protected Map<String, ConditionalSufficientStats> m_nodeStats = new HashMap();
/*  19:    */   
/*  20:    */   public void updateNode(Instance inst)
/*  21:    */     throws Exception
/*  22:    */   {
/*  23: 55 */     super.updateDistribution(inst);
/*  24: 57 */     for (int i = 0; i < inst.numAttributes(); i++)
/*  25:    */     {
/*  26: 58 */       Attribute a = inst.attribute(i);
/*  27: 59 */       if (i != inst.classIndex())
/*  28:    */       {
/*  29: 60 */         ConditionalSufficientStats stats = (ConditionalSufficientStats)this.m_nodeStats.get(a.name());
/*  30: 61 */         if (stats == null)
/*  31:    */         {
/*  32: 62 */           if (a.isNumeric()) {
/*  33: 63 */             stats = new GaussianConditionalSufficientStats();
/*  34:    */           } else {
/*  35: 65 */             stats = new NominalConditionalSufficientStats();
/*  36:    */           }
/*  37: 67 */           this.m_nodeStats.put(a.name(), stats);
/*  38:    */         }
/*  39: 70 */         stats.update(inst.value(a), inst.classAttribute().value((int)inst.classValue()), inst.weight());
/*  40:    */       }
/*  41:    */     }
/*  42:    */   }
/*  43:    */   
/*  44:    */   public List<SplitCandidate> getPossibleSplits(SplitMetric splitMetric)
/*  45:    */   {
/*  46: 86 */     List<SplitCandidate> splits = new ArrayList();
/*  47:    */     
/*  48:    */ 
/*  49: 89 */     List<Map<String, WeightMass>> nullDist = new ArrayList();
/*  50: 90 */     nullDist.add(this.m_classDistribution);
/*  51: 91 */     SplitCandidate nullSplit = new SplitCandidate(null, nullDist, splitMetric.evaluateSplit(this.m_classDistribution, nullDist));
/*  52:    */     
/*  53: 93 */     splits.add(nullSplit);
/*  54: 95 */     for (Map.Entry<String, ConditionalSufficientStats> e : this.m_nodeStats.entrySet())
/*  55:    */     {
/*  56: 97 */       ConditionalSufficientStats stat = (ConditionalSufficientStats)e.getValue();
/*  57:    */       
/*  58: 99 */       SplitCandidate splitCandidate = stat.bestSplit(splitMetric, this.m_classDistribution, (String)e.getKey());
/*  59:102 */       if (splitCandidate != null) {
/*  60:103 */         splits.add(splitCandidate);
/*  61:    */       }
/*  62:    */     }
/*  63:107 */     return splits;
/*  64:    */   }
/*  65:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.ht.ActiveHNode
 * JD-Core Version:    0.7.0.1
 */