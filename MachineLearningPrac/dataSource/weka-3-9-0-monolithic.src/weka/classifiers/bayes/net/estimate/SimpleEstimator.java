/*   1:    */ package weka.classifiers.bayes.net.estimate;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import weka.classifiers.bayes.BayesNet;
/*   5:    */ import weka.classifiers.bayes.net.ParentSet;
/*   6:    */ import weka.core.Attribute;
/*   7:    */ import weka.core.Instance;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.RevisionUtils;
/*  10:    */ import weka.core.Utils;
/*  11:    */ import weka.estimators.Estimator;
/*  12:    */ 
/*  13:    */ public class SimpleEstimator
/*  14:    */   extends BayesNetEstimator
/*  15:    */ {
/*  16:    */   static final long serialVersionUID = 5874941612331806172L;
/*  17:    */   
/*  18:    */   public String globalInfo()
/*  19:    */   {
/*  20: 66 */     return "SimpleEstimator is used for estimating the conditional probability tables of a Bayes network once the structure has been learned. Estimates probabilities directly from data.";
/*  21:    */   }
/*  22:    */   
/*  23:    */   public void estimateCPTs(BayesNet bayesNet)
/*  24:    */     throws Exception
/*  25:    */   {
/*  26: 80 */     initCPTs(bayesNet);
/*  27:    */     
/*  28:    */ 
/*  29: 83 */     Enumeration<Instance> enumInsts = bayesNet.m_Instances.enumerateInstances();
/*  30: 84 */     while (enumInsts.hasMoreElements())
/*  31:    */     {
/*  32: 85 */       Instance instance = (Instance)enumInsts.nextElement();
/*  33:    */       
/*  34: 87 */       updateClassifier(bayesNet, instance);
/*  35:    */     }
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void updateClassifier(BayesNet bayesNet, Instance instance)
/*  39:    */     throws Exception
/*  40:    */   {
/*  41:101 */     for (int iAttribute = 0; iAttribute < bayesNet.m_Instances.numAttributes(); iAttribute++)
/*  42:    */     {
/*  43:102 */       double iCPT = 0.0D;
/*  44:104 */       for (int iParent = 0; iParent < bayesNet.getParentSet(iAttribute).getNrOfParents(); iParent++)
/*  45:    */       {
/*  46:106 */         int nParent = bayesNet.getParentSet(iAttribute).getParent(iParent);
/*  47:    */         
/*  48:108 */         iCPT = iCPT * bayesNet.m_Instances.attribute(nParent).numValues() + instance.value(nParent);
/*  49:    */       }
/*  50:112 */       bayesNet.m_Distributions[iAttribute][((int)iCPT)].addValue(instance.value(iAttribute), instance.weight());
/*  51:    */     }
/*  52:    */   }
/*  53:    */   
/*  54:    */   public void initCPTs(BayesNet bayesNet)
/*  55:    */     throws Exception
/*  56:    */   {
/*  57:125 */     Instances instances = bayesNet.m_Instances;
/*  58:    */     
/*  59:    */ 
/*  60:128 */     int nMaxParentCardinality = 1;
/*  61:129 */     for (int iAttribute = 0; iAttribute < instances.numAttributes(); iAttribute++) {
/*  62:130 */       if (bayesNet.getParentSet(iAttribute).getCardinalityOfParents() > nMaxParentCardinality) {
/*  63:131 */         nMaxParentCardinality = bayesNet.getParentSet(iAttribute).getCardinalityOfParents();
/*  64:    */       }
/*  65:    */     }
/*  66:137 */     bayesNet.m_Distributions = new Estimator[instances.numAttributes()][nMaxParentCardinality];
/*  67:140 */     for (int iAttribute = 0; iAttribute < instances.numAttributes(); iAttribute++) {
/*  68:141 */       for (int iParent = 0; iParent < bayesNet.getParentSet(iAttribute).getCardinalityOfParents(); iParent++) {
/*  69:143 */         bayesNet.m_Distributions[iAttribute][iParent] = new DiscreteEstimatorBayes(instances.attribute(iAttribute).numValues(), this.m_fAlpha);
/*  70:    */       }
/*  71:    */     }
/*  72:    */   }
/*  73:    */   
/*  74:    */   public double[] distributionForInstance(BayesNet bayesNet, Instance instance)
/*  75:    */     throws Exception
/*  76:    */   {
/*  77:160 */     Instances instances = bayesNet.m_Instances;
/*  78:161 */     int nNumClasses = instances.numClasses();
/*  79:162 */     double[] fProbs = new double[nNumClasses];
/*  80:164 */     for (int iClass = 0; iClass < nNumClasses; iClass++)
/*  81:    */     {
/*  82:165 */       double logfP = 0.0D;
/*  83:167 */       for (int iAttribute = 0; iAttribute < instances.numAttributes(); iAttribute++)
/*  84:    */       {
/*  85:168 */         double iCPT = 0.0D;
/*  86:170 */         for (int iParent = 0; iParent < bayesNet.getParentSet(iAttribute).getNrOfParents(); iParent++)
/*  87:    */         {
/*  88:172 */           int nParent = bayesNet.getParentSet(iAttribute).getParent(iParent);
/*  89:174 */           if (nParent == instances.classIndex()) {
/*  90:175 */             iCPT = iCPT * nNumClasses + iClass;
/*  91:    */           } else {
/*  92:177 */             iCPT = iCPT * instances.attribute(nParent).numValues() + instance.value(nParent);
/*  93:    */           }
/*  94:    */         }
/*  95:182 */         if (iAttribute == instances.classIndex()) {
/*  96:185 */           logfP += Math.log(bayesNet.m_Distributions[iAttribute][((int)iCPT)].getProbability(iClass));
/*  97:    */         } else {
/*  98:191 */           logfP += Math.log(bayesNet.m_Distributions[iAttribute][((int)iCPT)].getProbability(instance.value(iAttribute)));
/*  99:    */         }
/* 100:    */       }
/* 101:197 */       fProbs[iClass] += logfP;
/* 102:    */     }
/* 103:201 */     double fMax = fProbs[0];
/* 104:202 */     for (int iClass = 0; iClass < nNumClasses; iClass++) {
/* 105:203 */       if (fProbs[iClass] > fMax) {
/* 106:204 */         fMax = fProbs[iClass];
/* 107:    */       }
/* 108:    */     }
/* 109:208 */     for (int iClass = 0; iClass < nNumClasses; iClass++) {
/* 110:209 */       fProbs[iClass] = Math.exp(fProbs[iClass] - fMax);
/* 111:    */     }
/* 112:    */     try
/* 113:    */     {
/* 114:214 */       Utils.normalize(fProbs);
/* 115:    */     }
/* 116:    */     catch (IllegalArgumentException ex)
/* 117:    */     {
/* 118:216 */       return new double[nNumClasses];
/* 119:    */     }
/* 120:219 */     return fProbs;
/* 121:    */   }
/* 122:    */   
/* 123:    */   public String getRevision()
/* 124:    */   {
/* 125:229 */     return RevisionUtils.extract("$Revision: 11325 $");
/* 126:    */   }
/* 127:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.estimate.SimpleEstimator
 * JD-Core Version:    0.7.0.1
 */