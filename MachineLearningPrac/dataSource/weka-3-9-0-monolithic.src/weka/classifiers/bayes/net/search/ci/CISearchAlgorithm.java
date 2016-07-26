/*   1:    */ package weka.classifiers.bayes.net.search.ci;
/*   2:    */ 
/*   3:    */ import weka.classifiers.bayes.BayesNet;
/*   4:    */ import weka.classifiers.bayes.net.ParentSet;
/*   5:    */ import weka.classifiers.bayes.net.search.local.LocalScoreSearchAlgorithm;
/*   6:    */ import weka.core.Instances;
/*   7:    */ import weka.core.RevisionUtils;
/*   8:    */ 
/*   9:    */ public class CISearchAlgorithm
/*  10:    */   extends LocalScoreSearchAlgorithm
/*  11:    */ {
/*  12:    */   static final long serialVersionUID = 3165802334119704560L;
/*  13:    */   BayesNet m_BayesNet;
/*  14:    */   Instances m_instances;
/*  15:    */   
/*  16:    */   public String globalInfo()
/*  17:    */   {
/*  18: 68 */     return "The CISearchAlgorithm class supports Bayes net structure search algorithms that are based on conditional independence test (as opposed to for example score based of cross validation based search algorithms).";
/*  19:    */   }
/*  20:    */   
/*  21:    */   protected boolean isConditionalIndependent(int iAttributeX, int iAttributeY, int[] iAttributesZ, int nAttributesZ)
/*  22:    */   {
/*  23: 89 */     ParentSet oParentSetX = this.m_BayesNet.getParentSet(iAttributeX);
/*  24: 91 */     while (oParentSetX.getNrOfParents() > 0) {
/*  25: 92 */       oParentSetX.deleteLastParent(this.m_instances);
/*  26:    */     }
/*  27: 96 */     for (int iAttributeZ = 0; iAttributeZ < nAttributesZ; iAttributeZ++) {
/*  28: 97 */       oParentSetX.addParent(iAttributesZ[iAttributeZ], this.m_instances);
/*  29:    */     }
/*  30:100 */     double fScoreZ = calcNodeScore(iAttributeX);
/*  31:101 */     double fScoreZY = calcScoreWithExtraParent(iAttributeX, iAttributeY);
/*  32:102 */     if (fScoreZY <= fScoreZ) {
/*  33:106 */       return true;
/*  34:    */     }
/*  35:108 */     return false;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public String getRevision()
/*  39:    */   {
/*  40:117 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  41:    */   }
/*  42:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.search.ci.CISearchAlgorithm
 * JD-Core Version:    0.7.0.1
 */