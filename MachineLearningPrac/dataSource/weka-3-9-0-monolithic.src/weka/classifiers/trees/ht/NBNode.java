/*   1:    */ package weka.classifiers.trees.ht;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import weka.classifiers.bayes.NaiveBayesUpdateable;
/*   5:    */ import weka.core.Attribute;
/*   6:    */ import weka.core.Instance;
/*   7:    */ import weka.core.Instances;
/*   8:    */ 
/*   9:    */ public class NBNode
/*  10:    */   extends ActiveHNode
/*  11:    */   implements LearningNode, Serializable
/*  12:    */ {
/*  13:    */   private static final long serialVersionUID = -1872415764817690961L;
/*  14:    */   protected NaiveBayesUpdateable m_bayes;
/*  15:    */   protected double m_nbWeightThreshold;
/*  16:    */   
/*  17:    */   public NBNode(Instances header, double nbWeightThreshold)
/*  18:    */     throws Exception
/*  19:    */   {
/*  20: 63 */     this.m_nbWeightThreshold = nbWeightThreshold;
/*  21: 64 */     this.m_bayes = new NaiveBayesUpdateable();
/*  22: 65 */     this.m_bayes.buildClassifier(header);
/*  23:    */   }
/*  24:    */   
/*  25:    */   public void updateNode(Instance inst)
/*  26:    */     throws Exception
/*  27:    */   {
/*  28: 70 */     super.updateNode(inst);
/*  29:    */     try
/*  30:    */     {
/*  31: 73 */       this.m_bayes.updateClassifier(inst);
/*  32:    */     }
/*  33:    */     catch (Exception e)
/*  34:    */     {
/*  35: 75 */       e.printStackTrace();
/*  36:    */     }
/*  37:    */   }
/*  38:    */   
/*  39:    */   protected double[] bypassNB(Instance inst, Attribute classAtt)
/*  40:    */     throws Exception
/*  41:    */   {
/*  42: 81 */     return super.getDistribution(inst, classAtt);
/*  43:    */   }
/*  44:    */   
/*  45:    */   public double[] getDistribution(Instance inst, Attribute classAtt)
/*  46:    */     throws Exception
/*  47:    */   {
/*  48: 91 */     boolean doNB = this.m_nbWeightThreshold == 0.0D;
/*  49: 94 */     if (doNB) {
/*  50: 95 */       return this.m_bayes.distributionForInstance(inst);
/*  51:    */     }
/*  52: 98 */     return super.getDistribution(inst, classAtt);
/*  53:    */   }
/*  54:    */   
/*  55:    */   protected int dumpTree(int depth, int leafCount, StringBuffer buff)
/*  56:    */   {
/*  57:103 */     leafCount = super.dumpTree(depth, leafCount, buff);
/*  58:    */     
/*  59:105 */     buff.append(" NB" + this.m_leafNum);
/*  60:    */     
/*  61:107 */     return leafCount;
/*  62:    */   }
/*  63:    */   
/*  64:    */   protected void printLeafModels(StringBuffer buff)
/*  65:    */   {
/*  66:112 */     buff.append("NB" + this.m_leafNum).append("\n").append(this.m_bayes.toString());
/*  67:    */   }
/*  68:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.ht.NBNode
 * JD-Core Version:    0.7.0.1
 */