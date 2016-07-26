/*   1:    */ package weka.classifiers.trees.j48;
/*   2:    */ 
/*   3:    */ import weka.core.Instance;
/*   4:    */ import weka.core.Instances;
/*   5:    */ import weka.core.RevisionUtils;
/*   6:    */ 
/*   7:    */ public final class NoSplit
/*   8:    */   extends ClassifierSplitModel
/*   9:    */ {
/*  10:    */   private static final long serialVersionUID = -1292620749331337546L;
/*  11:    */   
/*  12:    */   public NoSplit(Distribution distribution)
/*  13:    */   {
/*  14: 45 */     this.m_distribution = new Distribution(distribution);
/*  15: 46 */     this.m_numSubsets = 1;
/*  16:    */   }
/*  17:    */   
/*  18:    */   public final void buildClassifier(Instances instances)
/*  19:    */     throws Exception
/*  20:    */   {
/*  21: 57 */     this.m_distribution = new Distribution(instances);
/*  22: 58 */     this.m_numSubsets = 1;
/*  23:    */   }
/*  24:    */   
/*  25:    */   public final int whichSubset(Instance instance)
/*  26:    */   {
/*  27: 66 */     return 0;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public final double[] weights(Instance instance)
/*  31:    */   {
/*  32: 74 */     return null;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public final String leftSide(Instances instances)
/*  36:    */   {
/*  37: 82 */     return "";
/*  38:    */   }
/*  39:    */   
/*  40:    */   public final String rightSide(int index, Instances instances)
/*  41:    */   {
/*  42: 90 */     return "";
/*  43:    */   }
/*  44:    */   
/*  45:    */   public final String sourceExpression(int index, Instances data)
/*  46:    */   {
/*  47:103 */     return "true";
/*  48:    */   }
/*  49:    */   
/*  50:    */   public String getRevision()
/*  51:    */   {
/*  52:112 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  53:    */   }
/*  54:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.j48.NoSplit
 * JD-Core Version:    0.7.0.1
 */