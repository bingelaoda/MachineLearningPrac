/*   1:    */ package weka.classifiers.trees.j48;
/*   2:    */ 
/*   3:    */ import java.util.Random;
/*   4:    */ import weka.classifiers.AbstractClassifier;
/*   5:    */ import weka.classifiers.Classifier;
/*   6:    */ import weka.classifiers.Evaluation;
/*   7:    */ import weka.classifiers.bayes.NaiveBayesUpdateable;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.RevisionUtils;
/*  11:    */ import weka.filters.Filter;
/*  12:    */ import weka.filters.supervised.attribute.Discretize;
/*  13:    */ 
/*  14:    */ public final class NBTreeNoSplit
/*  15:    */   extends ClassifierSplitModel
/*  16:    */ {
/*  17:    */   private static final long serialVersionUID = 7824804381545259618L;
/*  18:    */   protected NaiveBayesUpdateable m_nb;
/*  19:    */   protected Discretize m_disc;
/*  20:    */   protected double m_errors;
/*  21:    */   
/*  22:    */   public NBTreeNoSplit()
/*  23:    */   {
/*  24: 59 */     this.m_numSubsets = 1;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public final void buildClassifier(Instances instances)
/*  28:    */     throws Exception
/*  29:    */   {
/*  30: 69 */     this.m_nb = new NaiveBayesUpdateable();
/*  31: 70 */     this.m_disc = new Discretize();
/*  32: 71 */     this.m_disc.setInputFormat(instances);
/*  33: 72 */     Instances temp = Filter.useFilter(instances, this.m_disc);
/*  34: 73 */     this.m_nb.buildClassifier(temp);
/*  35: 74 */     if (temp.numInstances() >= 5) {
/*  36: 75 */       this.m_errors = crossValidate(this.m_nb, temp, new Random(1L));
/*  37:    */     }
/*  38: 77 */     this.m_numSubsets = 1;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public double getErrors()
/*  42:    */   {
/*  43: 86 */     return this.m_errors;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public Discretize getDiscretizer()
/*  47:    */   {
/*  48: 95 */     return this.m_disc;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public NaiveBayesUpdateable getNaiveBayesModel()
/*  52:    */   {
/*  53:104 */     return this.m_nb;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public final int whichSubset(Instance instance)
/*  57:    */   {
/*  58:112 */     return 0;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public final double[] weights(Instance instance)
/*  62:    */   {
/*  63:120 */     return null;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public final String leftSide(Instances instances)
/*  67:    */   {
/*  68:128 */     return "";
/*  69:    */   }
/*  70:    */   
/*  71:    */   public final String rightSide(int index, Instances instances)
/*  72:    */   {
/*  73:136 */     return "";
/*  74:    */   }
/*  75:    */   
/*  76:    */   public final String sourceExpression(int index, Instances data)
/*  77:    */   {
/*  78:149 */     return "true";
/*  79:    */   }
/*  80:    */   
/*  81:    */   public double classProb(int classIndex, Instance instance, int theSubset)
/*  82:    */     throws Exception
/*  83:    */   {
/*  84:163 */     this.m_disc.input(instance);
/*  85:164 */     Instance temp = this.m_disc.output();
/*  86:165 */     return this.m_nb.distributionForInstance(temp)[classIndex];
/*  87:    */   }
/*  88:    */   
/*  89:    */   public String toString()
/*  90:    */   {
/*  91:174 */     return this.m_nb.toString();
/*  92:    */   }
/*  93:    */   
/*  94:    */   public static double crossValidate(NaiveBayesUpdateable fullModel, Instances trainingSet, Random r)
/*  95:    */     throws Exception
/*  96:    */   {
/*  97:191 */     Classifier[] copies = AbstractClassifier.makeCopies(fullModel, 5);
/*  98:192 */     Evaluation eval = new Evaluation(trainingSet);
/*  99:194 */     for (int j = 0; j < 5; j++)
/* 100:    */     {
/* 101:195 */       Instances test = trainingSet.testCV(5, j);
/* 102:197 */       for (int k = 0; k < test.numInstances(); k++)
/* 103:    */       {
/* 104:198 */         test.instance(k).setWeight(-test.instance(k).weight());
/* 105:199 */         ((NaiveBayesUpdateable)copies[j]).updateClassifier(test.instance(k));
/* 106:    */         
/* 107:201 */         test.instance(k).setWeight(-test.instance(k).weight());
/* 108:    */       }
/* 109:203 */       eval.evaluateModel(copies[j], test, new Object[0]);
/* 110:    */     }
/* 111:205 */     return eval.incorrect();
/* 112:    */   }
/* 113:    */   
/* 114:    */   public String getRevision()
/* 115:    */   {
/* 116:214 */     return RevisionUtils.extract("$Revision: 10531 $");
/* 117:    */   }
/* 118:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.j48.NBTreeNoSplit
 * JD-Core Version:    0.7.0.1
 */