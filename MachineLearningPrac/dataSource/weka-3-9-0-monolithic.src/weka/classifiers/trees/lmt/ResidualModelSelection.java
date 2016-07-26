/*   1:    */ package weka.classifiers.trees.lmt;
/*   2:    */ 
/*   3:    */ import weka.classifiers.trees.j48.ClassifierSplitModel;
/*   4:    */ import weka.classifiers.trees.j48.Distribution;
/*   5:    */ import weka.classifiers.trees.j48.ModelSelection;
/*   6:    */ import weka.classifiers.trees.j48.NoSplit;
/*   7:    */ import weka.core.Instances;
/*   8:    */ import weka.core.RevisionUtils;
/*   9:    */ 
/*  10:    */ public class ResidualModelSelection
/*  11:    */   extends ModelSelection
/*  12:    */ {
/*  13:    */   private static final long serialVersionUID = -293098783159385148L;
/*  14:    */   protected int m_minNumInstances;
/*  15:    */   protected double m_minInfoGain;
/*  16:    */   
/*  17:    */   public ResidualModelSelection(int minNumInstances)
/*  18:    */   {
/*  19: 55 */     this.m_minNumInstances = minNumInstances;
/*  20: 56 */     this.m_minInfoGain = 0.0001D;
/*  21:    */   }
/*  22:    */   
/*  23:    */   public void cleanup() {}
/*  24:    */   
/*  25:    */   public final ClassifierSplitModel selectModel(Instances data, double[][] dataZs, double[][] dataWs)
/*  26:    */     throws Exception
/*  27:    */   {
/*  28: 70 */     int numAttributes = data.numAttributes();
/*  29: 72 */     if (numAttributes < 2) {
/*  30: 72 */       throw new Exception("Can't select Model without non-class attribute");
/*  31:    */     }
/*  32: 73 */     if (data.numInstances() < this.m_minNumInstances) {
/*  33: 73 */       return new NoSplit(new Distribution(data));
/*  34:    */     }
/*  35: 76 */     double bestGain = -1.797693134862316E+308D;
/*  36: 77 */     int bestAttribute = -1;
/*  37: 80 */     for (int i = 0; i < numAttributes; i++) {
/*  38: 81 */       if (i != data.classIndex())
/*  39:    */       {
/*  40: 84 */         ResidualSplit split = new ResidualSplit(i);
/*  41: 85 */         split.buildClassifier(data, dataZs, dataWs);
/*  42: 87 */         if (split.checkModel(this.m_minNumInstances))
/*  43:    */         {
/*  44: 90 */           double gain = split.entropyGain();
/*  45: 91 */           if (gain > bestGain)
/*  46:    */           {
/*  47: 92 */             bestGain = gain;
/*  48: 93 */             bestAttribute = i;
/*  49:    */           }
/*  50:    */         }
/*  51:    */       }
/*  52:    */     }
/*  53: 99 */     if (bestGain >= this.m_minInfoGain)
/*  54:    */     {
/*  55:101 */       ResidualSplit split = new ResidualSplit(bestAttribute);
/*  56:102 */       split.buildClassifier(data, dataZs, dataWs);
/*  57:103 */       return split;
/*  58:    */     }
/*  59:106 */     return new NoSplit(new Distribution(data));
/*  60:    */   }
/*  61:    */   
/*  62:    */   public final ClassifierSplitModel selectModel(Instances train)
/*  63:    */   {
/*  64:113 */     return null;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public final ClassifierSplitModel selectModel(Instances train, Instances test)
/*  68:    */   {
/*  69:119 */     return null;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public String getRevision()
/*  73:    */   {
/*  74:128 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  75:    */   }
/*  76:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.lmt.ResidualModelSelection
 * JD-Core Version:    0.7.0.1
 */