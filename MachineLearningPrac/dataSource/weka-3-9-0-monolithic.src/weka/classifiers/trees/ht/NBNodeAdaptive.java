/*   1:    */ package weka.classifiers.trees.ht;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.Map;
/*   5:    */ import java.util.Map.Entry;
/*   6:    */ import weka.classifiers.bayes.NaiveBayesUpdateable;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ 
/*  11:    */ public class NBNodeAdaptive
/*  12:    */   extends NBNode
/*  13:    */   implements LearningNode, Serializable
/*  14:    */ {
/*  15:    */   private static final long serialVersionUID = -4509802312019989686L;
/*  16: 48 */   protected double m_majClassCorrectWeight = 0.0D;
/*  17: 51 */   protected double m_nbCorrectWeight = 0.0D;
/*  18:    */   
/*  19:    */   public NBNodeAdaptive(Instances header, double nbWeightThreshold)
/*  20:    */     throws Exception
/*  21:    */   {
/*  22: 63 */     super(header, nbWeightThreshold);
/*  23:    */   }
/*  24:    */   
/*  25:    */   protected String majorityClass()
/*  26:    */   {
/*  27: 67 */     String mc = "";
/*  28: 68 */     double max = -1.0D;
/*  29: 70 */     for (Map.Entry<String, WeightMass> e : this.m_classDistribution.entrySet()) {
/*  30: 71 */       if (((WeightMass)e.getValue()).m_weight > max)
/*  31:    */       {
/*  32: 72 */         max = ((WeightMass)e.getValue()).m_weight;
/*  33: 73 */         mc = (String)e.getKey();
/*  34:    */       }
/*  35:    */     }
/*  36: 77 */     return mc;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void updateNode(Instance inst)
/*  40:    */     throws Exception
/*  41:    */   {
/*  42: 83 */     String trueClass = inst.classAttribute().value((int)inst.classValue());
/*  43: 84 */     int trueClassIndex = (int)inst.classValue();
/*  44: 86 */     if (majorityClass().equals(trueClass)) {
/*  45: 87 */       this.m_majClassCorrectWeight += inst.weight();
/*  46:    */     }
/*  47: 90 */     if (this.m_bayes.classifyInstance(inst) == trueClassIndex) {
/*  48: 91 */       this.m_nbCorrectWeight += inst.weight();
/*  49:    */     }
/*  50: 94 */     super.updateNode(inst);
/*  51:    */   }
/*  52:    */   
/*  53:    */   public double[] getDistribution(Instance inst, Attribute classAtt)
/*  54:    */     throws Exception
/*  55:    */   {
/*  56:101 */     if (this.m_majClassCorrectWeight > this.m_nbCorrectWeight) {
/*  57:102 */       return super.bypassNB(inst, classAtt);
/*  58:    */     }
/*  59:105 */     return super.getDistribution(inst, classAtt);
/*  60:    */   }
/*  61:    */   
/*  62:    */   protected int dumpTree(int depth, int leafCount, StringBuffer buff)
/*  63:    */   {
/*  64:110 */     leafCount = super.dumpTree(depth, leafCount, buff);
/*  65:    */     
/*  66:112 */     buff.append(" NB adaptive" + this.m_leafNum);
/*  67:    */     
/*  68:114 */     return leafCount;
/*  69:    */   }
/*  70:    */   
/*  71:    */   protected void printLeafModels(StringBuffer buff)
/*  72:    */   {
/*  73:119 */     buff.append("NB adaptive" + this.m_leafNum).append("\n").append(this.m_bayes.toString());
/*  74:    */   }
/*  75:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.ht.NBNodeAdaptive
 * JD-Core Version:    0.7.0.1
 */