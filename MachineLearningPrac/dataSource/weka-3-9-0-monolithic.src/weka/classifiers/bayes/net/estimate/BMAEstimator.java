/*   1:    */ package weka.classifiers.bayes.net.estimate;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.classifiers.bayes.BayesNet;
/*   7:    */ import weka.classifiers.bayes.net.ParentSet;
/*   8:    */ import weka.classifiers.bayes.net.search.local.K2;
/*   9:    */ import weka.core.Attribute;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.Option;
/*  13:    */ import weka.core.RevisionUtils;
/*  14:    */ import weka.core.Statistics;
/*  15:    */ import weka.core.Utils;
/*  16:    */ import weka.estimators.Estimator;
/*  17:    */ 
/*  18:    */ public class BMAEstimator
/*  19:    */   extends SimpleEstimator
/*  20:    */ {
/*  21:    */   static final long serialVersionUID = -1846028304233257309L;
/*  22: 68 */   protected boolean m_bUseK2Prior = false;
/*  23:    */   
/*  24:    */   public String globalInfo()
/*  25:    */   {
/*  26: 78 */     return "BMAEstimator estimates conditional probability tables of a Bayes network using Bayes Model Averaging (BMA).";
/*  27:    */   }
/*  28:    */   
/*  29:    */   public void estimateCPTs(BayesNet bayesNet)
/*  30:    */     throws Exception
/*  31:    */   {
/*  32: 91 */     initCPTs(bayesNet);
/*  33:    */     
/*  34: 93 */     Instances instances = bayesNet.m_Instances;
/*  35: 95 */     for (int iAttribute = 0; iAttribute < instances.numAttributes(); iAttribute++) {
/*  36: 96 */       if (bayesNet.getParentSet(iAttribute).getNrOfParents() > 1) {
/*  37: 97 */         throw new Exception("Cannot handle networks with nodes with more than 1 parent (yet).");
/*  38:    */       }
/*  39:    */     }
/*  40:102 */     BayesNet EmptyNet = new BayesNet();
/*  41:103 */     K2 oSearchAlgorithm = new K2();
/*  42:104 */     oSearchAlgorithm.setInitAsNaiveBayes(false);
/*  43:105 */     oSearchAlgorithm.setMaxNrOfParents(0);
/*  44:106 */     EmptyNet.setSearchAlgorithm(oSearchAlgorithm);
/*  45:107 */     EmptyNet.buildClassifier(instances);
/*  46:    */     
/*  47:109 */     BayesNet NBNet = new BayesNet();
/*  48:110 */     oSearchAlgorithm.setInitAsNaiveBayes(true);
/*  49:111 */     oSearchAlgorithm.setMaxNrOfParents(1);
/*  50:112 */     NBNet.setSearchAlgorithm(oSearchAlgorithm);
/*  51:113 */     NBNet.buildClassifier(instances);
/*  52:116 */     for (int iAttribute = 0; iAttribute < instances.numAttributes(); iAttribute++) {
/*  53:117 */       if (iAttribute != instances.classIndex())
/*  54:    */       {
/*  55:118 */         double w1 = 0.0D;double w2 = 0.0D;
/*  56:119 */         int nAttValues = instances.attribute(iAttribute).numValues();
/*  57:120 */         if (this.m_bUseK2Prior == true)
/*  58:    */         {
/*  59:122 */           for (int iAttValue = 0; iAttValue < nAttValues; iAttValue++) {
/*  60:123 */             w1 += Statistics.lnGamma(1.0D + ((DiscreteEstimatorBayes)EmptyNet.m_Distributions[iAttribute][0]).getCount(iAttValue)) - Statistics.lnGamma(1.0D);
/*  61:    */           }
/*  62:128 */           w1 += Statistics.lnGamma(nAttValues) - Statistics.lnGamma(nAttValues + instances.numInstances());
/*  63:131 */           for (int iParent = 0; iParent < bayesNet.getParentSet(iAttribute).getCardinalityOfParents(); iParent++)
/*  64:    */           {
/*  65:133 */             int nTotal = 0;
/*  66:134 */             for (int iAttValue = 0; iAttValue < nAttValues; iAttValue++)
/*  67:    */             {
/*  68:135 */               double nCount = ((DiscreteEstimatorBayes)NBNet.m_Distributions[iAttribute][iParent]).getCount(iAttValue);
/*  69:    */               
/*  70:137 */               w2 += Statistics.lnGamma(1.0D + nCount) - Statistics.lnGamma(1.0D);
/*  71:138 */               nTotal = (int)(nTotal + nCount);
/*  72:    */             }
/*  73:140 */             w2 += Statistics.lnGamma(nAttValues) - Statistics.lnGamma(nAttValues + nTotal);
/*  74:    */           }
/*  75:    */         }
/*  76:    */         else
/*  77:    */         {
/*  78:145 */           for (int iAttValue = 0; iAttValue < nAttValues; iAttValue++) {
/*  79:146 */             w1 += Statistics.lnGamma(1.0D / nAttValues + ((DiscreteEstimatorBayes)EmptyNet.m_Distributions[iAttribute][0]).getCount(iAttValue)) - Statistics.lnGamma(1.0D / nAttValues);
/*  80:    */           }
/*  81:153 */           w1 += Statistics.lnGamma(1.0D) - Statistics.lnGamma(1 + instances.numInstances());
/*  82:    */           
/*  83:    */ 
/*  84:156 */           int nParentValues = bayesNet.getParentSet(iAttribute).getCardinalityOfParents();
/*  85:158 */           for (int iParent = 0; iParent < nParentValues; iParent++)
/*  86:    */           {
/*  87:159 */             int nTotal = 0;
/*  88:160 */             for (int iAttValue = 0; iAttValue < nAttValues; iAttValue++)
/*  89:    */             {
/*  90:161 */               double nCount = ((DiscreteEstimatorBayes)NBNet.m_Distributions[iAttribute][iParent]).getCount(iAttValue);
/*  91:    */               
/*  92:163 */               w2 += Statistics.lnGamma(1.0D / (nAttValues * nParentValues) + nCount) - Statistics.lnGamma(1.0D / (nAttValues * nParentValues));
/*  93:    */               
/*  94:    */ 
/*  95:166 */               nTotal = (int)(nTotal + nCount);
/*  96:    */             }
/*  97:168 */             w2 += Statistics.lnGamma(1.0D) - Statistics.lnGamma(1 + nTotal);
/*  98:    */           }
/*  99:    */         }
/* 100:173 */         if (w1 < w2)
/* 101:    */         {
/* 102:174 */           w2 -= w1;
/* 103:175 */           w1 = 0.0D;
/* 104:176 */           w1 = 1.0D / (1.0D + Math.exp(w2));
/* 105:177 */           w2 = Math.exp(w2) / (1.0D + Math.exp(w2));
/* 106:    */         }
/* 107:    */         else
/* 108:    */         {
/* 109:179 */           w1 -= w2;
/* 110:180 */           w2 = 0.0D;
/* 111:181 */           w2 = 1.0D / (1.0D + Math.exp(w1));
/* 112:182 */           w1 = Math.exp(w1) / (1.0D + Math.exp(w1));
/* 113:    */         }
/* 114:185 */         for (int iParent = 0; iParent < bayesNet.getParentSet(iAttribute).getCardinalityOfParents(); iParent++) {
/* 115:187 */           bayesNet.m_Distributions[iAttribute][iParent] = new DiscreteEstimatorFullBayes(instances.attribute(iAttribute).numValues(), w1, w2, (DiscreteEstimatorBayes)EmptyNet.m_Distributions[iAttribute][0], (DiscreteEstimatorBayes)NBNet.m_Distributions[iAttribute][iParent], this.m_fAlpha);
/* 116:    */         }
/* 117:    */       }
/* 118:    */     }
/* 119:197 */     int iAttribute = instances.classIndex();
/* 120:198 */     bayesNet.m_Distributions[iAttribute][0] = EmptyNet.m_Distributions[iAttribute][0];
/* 121:    */   }
/* 122:    */   
/* 123:    */   public void updateClassifier(BayesNet bayesNet, Instance instance)
/* 124:    */     throws Exception
/* 125:    */   {
/* 126:211 */     throw new Exception("updateClassifier does not apply to BMA estimator");
/* 127:    */   }
/* 128:    */   
/* 129:    */   public void initCPTs(BayesNet bayesNet)
/* 130:    */     throws Exception
/* 131:    */   {
/* 132:223 */     int nMaxParentCardinality = 1;
/* 133:225 */     for (int iAttribute = 0; iAttribute < bayesNet.m_Instances.numAttributes(); iAttribute++) {
/* 134:226 */       if (bayesNet.getParentSet(iAttribute).getCardinalityOfParents() > nMaxParentCardinality) {
/* 135:227 */         nMaxParentCardinality = bayesNet.getParentSet(iAttribute).getCardinalityOfParents();
/* 136:    */       }
/* 137:    */     }
/* 138:233 */     bayesNet.m_Distributions = new Estimator[bayesNet.m_Instances.numAttributes()][nMaxParentCardinality];
/* 139:    */   }
/* 140:    */   
/* 141:    */   public boolean isUseK2Prior()
/* 142:    */   {
/* 143:243 */     return this.m_bUseK2Prior;
/* 144:    */   }
/* 145:    */   
/* 146:    */   public void setUseK2Prior(boolean bUseK2Prior)
/* 147:    */   {
/* 148:252 */     this.m_bUseK2Prior = bUseK2Prior;
/* 149:    */   }
/* 150:    */   
/* 151:    */   public Enumeration<Option> listOptions()
/* 152:    */   {
/* 153:262 */     Vector<Option> newVector = new Vector(1);
/* 154:    */     
/* 155:264 */     newVector.addElement(new Option("\tWhether to use K2 prior.\n", "k2", 0, "-k2"));
/* 156:    */     
/* 157:    */ 
/* 158:267 */     newVector.addAll(Collections.list(super.listOptions()));
/* 159:    */     
/* 160:269 */     return newVector.elements();
/* 161:    */   }
/* 162:    */   
/* 163:    */   public void setOptions(String[] options)
/* 164:    */     throws Exception
/* 165:    */   {
/* 166:296 */     setUseK2Prior(Utils.getFlag("k2", options));
/* 167:    */     
/* 168:298 */     super.setOptions(options);
/* 169:    */   }
/* 170:    */   
/* 171:    */   public String[] getOptions()
/* 172:    */   {
/* 173:309 */     Vector<String> options = new Vector();
/* 174:311 */     if (isUseK2Prior()) {
/* 175:312 */       options.add("-k2");
/* 176:    */     }
/* 177:315 */     Collections.addAll(options, super.getOptions());
/* 178:    */     
/* 179:317 */     return (String[])options.toArray(new String[0]);
/* 180:    */   }
/* 181:    */   
/* 182:    */   public String getRevision()
/* 183:    */   {
/* 184:327 */     return RevisionUtils.extract("$Revision: 10154 $");
/* 185:    */   }
/* 186:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.estimate.BMAEstimator
 * JD-Core Version:    0.7.0.1
 */