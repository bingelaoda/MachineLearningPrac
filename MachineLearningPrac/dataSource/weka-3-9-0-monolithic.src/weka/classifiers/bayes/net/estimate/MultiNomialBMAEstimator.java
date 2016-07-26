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
/*  18:    */ public class MultiNomialBMAEstimator
/*  19:    */   extends BayesNetEstimator
/*  20:    */ {
/*  21:    */   static final long serialVersionUID = 8330705772601586313L;
/*  22: 70 */   protected boolean m_bUseK2Prior = true;
/*  23:    */   
/*  24:    */   public String globalInfo()
/*  25:    */   {
/*  26: 80 */     return "Multinomial BMA Estimator.";
/*  27:    */   }
/*  28:    */   
/*  29:    */   public void estimateCPTs(BayesNet bayesNet)
/*  30:    */     throws Exception
/*  31:    */   {
/*  32: 92 */     initCPTs(bayesNet);
/*  33: 95 */     for (int iAttribute = 0; iAttribute < bayesNet.m_Instances.numAttributes(); iAttribute++) {
/*  34: 96 */       if (bayesNet.getParentSet(iAttribute).getNrOfParents() > 1) {
/*  35: 97 */         throw new Exception("Cannot handle networks with nodes with more than 1 parent (yet).");
/*  36:    */       }
/*  37:    */     }
/*  38:103 */     Instances instances = new Instances(bayesNet.m_Instances);
/*  39:104 */     for (int iAttribute = instances.numAttributes() - 1; iAttribute >= 0; iAttribute--) {
/*  40:105 */       if (instances.attribute(iAttribute).numValues() != 2) {
/*  41:106 */         throw new Exception("MultiNomialBMAEstimator can only handle binary nominal attributes!");
/*  42:    */       }
/*  43:    */     }
/*  44:113 */     BayesNet EmptyNet = new BayesNet();
/*  45:114 */     K2 oSearchAlgorithm = new K2();
/*  46:115 */     oSearchAlgorithm.setInitAsNaiveBayes(false);
/*  47:116 */     oSearchAlgorithm.setMaxNrOfParents(0);
/*  48:117 */     EmptyNet.setSearchAlgorithm(oSearchAlgorithm);
/*  49:118 */     EmptyNet.buildClassifier(instances);
/*  50:    */     
/*  51:120 */     BayesNet NBNet = new BayesNet();
/*  52:121 */     oSearchAlgorithm.setInitAsNaiveBayes(true);
/*  53:122 */     oSearchAlgorithm.setMaxNrOfParents(1);
/*  54:123 */     NBNet.setSearchAlgorithm(oSearchAlgorithm);
/*  55:124 */     NBNet.buildClassifier(instances);
/*  56:127 */     for (int iAttribute = 0; iAttribute < instances.numAttributes(); iAttribute++) {
/*  57:128 */       if (iAttribute != instances.classIndex())
/*  58:    */       {
/*  59:129 */         double w1 = 0.0D;double w2 = 0.0D;
/*  60:130 */         int nAttValues = instances.attribute(iAttribute).numValues();
/*  61:131 */         if (this.m_bUseK2Prior == true)
/*  62:    */         {
/*  63:133 */           for (int iAttValue = 0; iAttValue < nAttValues; iAttValue++) {
/*  64:134 */             w1 += Statistics.lnGamma(1.0D + ((DiscreteEstimatorBayes)EmptyNet.m_Distributions[iAttribute][0]).getCount(iAttValue)) - Statistics.lnGamma(1.0D);
/*  65:    */           }
/*  66:139 */           w1 += Statistics.lnGamma(nAttValues) - Statistics.lnGamma(nAttValues + instances.numInstances());
/*  67:142 */           for (int iParent = 0; iParent < bayesNet.getParentSet(iAttribute).getCardinalityOfParents(); iParent++)
/*  68:    */           {
/*  69:144 */             int nTotal = 0;
/*  70:145 */             for (int iAttValue = 0; iAttValue < nAttValues; iAttValue++)
/*  71:    */             {
/*  72:146 */               double nCount = ((DiscreteEstimatorBayes)NBNet.m_Distributions[iAttribute][iParent]).getCount(iAttValue);
/*  73:    */               
/*  74:148 */               w2 += Statistics.lnGamma(1.0D + nCount) - Statistics.lnGamma(1.0D);
/*  75:149 */               nTotal = (int)(nTotal + nCount);
/*  76:    */             }
/*  77:151 */             w2 += Statistics.lnGamma(nAttValues) - Statistics.lnGamma(nAttValues + nTotal);
/*  78:    */           }
/*  79:    */         }
/*  80:    */         else
/*  81:    */         {
/*  82:156 */           for (int iAttValue = 0; iAttValue < nAttValues; iAttValue++) {
/*  83:157 */             w1 += Statistics.lnGamma(1.0D / nAttValues + ((DiscreteEstimatorBayes)EmptyNet.m_Distributions[iAttribute][0]).getCount(iAttValue)) - Statistics.lnGamma(1.0D / nAttValues);
/*  84:    */           }
/*  85:164 */           w1 += Statistics.lnGamma(1.0D) - Statistics.lnGamma(1 + instances.numInstances());
/*  86:    */           
/*  87:    */ 
/*  88:167 */           int nParentValues = bayesNet.getParentSet(iAttribute).getCardinalityOfParents();
/*  89:169 */           for (int iParent = 0; iParent < nParentValues; iParent++)
/*  90:    */           {
/*  91:170 */             int nTotal = 0;
/*  92:171 */             for (int iAttValue = 0; iAttValue < nAttValues; iAttValue++)
/*  93:    */             {
/*  94:172 */               double nCount = ((DiscreteEstimatorBayes)NBNet.m_Distributions[iAttribute][iParent]).getCount(iAttValue);
/*  95:    */               
/*  96:174 */               w2 += Statistics.lnGamma(1.0D / (nAttValues * nParentValues) + nCount) - Statistics.lnGamma(1.0D / (nAttValues * nParentValues));
/*  97:    */               
/*  98:    */ 
/*  99:177 */               nTotal = (int)(nTotal + nCount);
/* 100:    */             }
/* 101:179 */             w2 += Statistics.lnGamma(1.0D) - Statistics.lnGamma(1 + nTotal);
/* 102:    */           }
/* 103:    */         }
/* 104:185 */         if (w1 < w2)
/* 105:    */         {
/* 106:186 */           w2 -= w1;
/* 107:187 */           w1 = 0.0D;
/* 108:188 */           w1 = 1.0D / (1.0D + Math.exp(w2));
/* 109:189 */           w2 = Math.exp(w2) / (1.0D + Math.exp(w2));
/* 110:    */         }
/* 111:    */         else
/* 112:    */         {
/* 113:191 */           w1 -= w2;
/* 114:192 */           w2 = 0.0D;
/* 115:193 */           w2 = 1.0D / (1.0D + Math.exp(w1));
/* 116:194 */           w1 = Math.exp(w1) / (1.0D + Math.exp(w1));
/* 117:    */         }
/* 118:197 */         for (int iParent = 0; iParent < bayesNet.getParentSet(iAttribute).getCardinalityOfParents(); iParent++) {
/* 119:199 */           bayesNet.m_Distributions[iAttribute][iParent] = new DiscreteEstimatorFullBayes(instances.attribute(iAttribute).numValues(), w1, w2, (DiscreteEstimatorBayes)EmptyNet.m_Distributions[iAttribute][0], (DiscreteEstimatorBayes)NBNet.m_Distributions[iAttribute][iParent], this.m_fAlpha);
/* 120:    */         }
/* 121:    */       }
/* 122:    */     }
/* 123:209 */     int iAttribute = instances.classIndex();
/* 124:210 */     bayesNet.m_Distributions[iAttribute][0] = EmptyNet.m_Distributions[iAttribute][0];
/* 125:    */   }
/* 126:    */   
/* 127:    */   public void updateClassifier(BayesNet bayesNet, Instance instance)
/* 128:    */     throws Exception
/* 129:    */   {
/* 130:223 */     throw new Exception("updateClassifier does not apply to BMA estimator");
/* 131:    */   }
/* 132:    */   
/* 133:    */   public void initCPTs(BayesNet bayesNet)
/* 134:    */     throws Exception
/* 135:    */   {
/* 136:235 */     bayesNet.m_Distributions = new Estimator[bayesNet.m_Instances.numAttributes()][2];
/* 137:    */   }
/* 138:    */   
/* 139:    */   public boolean isUseK2Prior()
/* 140:    */   {
/* 141:243 */     return this.m_bUseK2Prior;
/* 142:    */   }
/* 143:    */   
/* 144:    */   public void setUseK2Prior(boolean bUseK2Prior)
/* 145:    */   {
/* 146:252 */     this.m_bUseK2Prior = bUseK2Prior;
/* 147:    */   }
/* 148:    */   
/* 149:    */   public double[] distributionForInstance(BayesNet bayesNet, Instance instance)
/* 150:    */     throws Exception
/* 151:    */   {
/* 152:266 */     Instances instances = bayesNet.m_Instances;
/* 153:267 */     int nNumClasses = instances.numClasses();
/* 154:268 */     double[] fProbs = new double[nNumClasses];
/* 155:270 */     for (int iClass = 0; iClass < nNumClasses; iClass++) {
/* 156:271 */       fProbs[iClass] = 1.0D;
/* 157:    */     }
/* 158:274 */     for (int iClass = 0; iClass < nNumClasses; iClass++)
/* 159:    */     {
/* 160:275 */       double logfP = 0.0D;
/* 161:277 */       for (int iAttribute = 0; iAttribute < instances.numAttributes(); iAttribute++)
/* 162:    */       {
/* 163:278 */         double iCPT = 0.0D;
/* 164:280 */         for (int iParent = 0; iParent < bayesNet.getParentSet(iAttribute).getNrOfParents(); iParent++)
/* 165:    */         {
/* 166:282 */           int nParent = bayesNet.getParentSet(iAttribute).getParent(iParent);
/* 167:284 */           if (nParent == instances.classIndex()) {
/* 168:285 */             iCPT = iCPT * nNumClasses + iClass;
/* 169:    */           } else {
/* 170:287 */             iCPT = iCPT * instances.attribute(nParent).numValues() + instance.value(nParent);
/* 171:    */           }
/* 172:    */         }
/* 173:292 */         if (iAttribute == instances.classIndex()) {
/* 174:293 */           logfP += Math.log(bayesNet.m_Distributions[iAttribute][((int)iCPT)].getProbability(iClass));
/* 175:    */         } else {
/* 176:296 */           logfP += instance.value(iAttribute) * Math.log(bayesNet.m_Distributions[iAttribute][((int)iCPT)].getProbability(instance.value(1)));
/* 177:    */         }
/* 178:    */       }
/* 179:302 */       fProbs[iClass] += logfP;
/* 180:    */     }
/* 181:306 */     double fMax = fProbs[0];
/* 182:307 */     for (int iClass = 0; iClass < nNumClasses; iClass++) {
/* 183:308 */       if (fProbs[iClass] > fMax) {
/* 184:309 */         fMax = fProbs[iClass];
/* 185:    */       }
/* 186:    */     }
/* 187:313 */     for (int iClass = 0; iClass < nNumClasses; iClass++) {
/* 188:314 */       fProbs[iClass] = Math.exp(fProbs[iClass] - fMax);
/* 189:    */     }
/* 190:318 */     Utils.normalize(fProbs);
/* 191:    */     
/* 192:320 */     return fProbs;
/* 193:    */   }
/* 194:    */   
/* 195:    */   public Enumeration<Option> listOptions()
/* 196:    */   {
/* 197:330 */     Vector<Option> newVector = new Vector(1);
/* 198:    */     
/* 199:332 */     newVector.addElement(new Option("\tWhether to use K2 prior.\n", "k2", 0, "-k2"));
/* 200:    */     
/* 201:    */ 
/* 202:335 */     newVector.addAll(Collections.list(super.listOptions()));
/* 203:    */     
/* 204:337 */     return newVector.elements();
/* 205:    */   }
/* 206:    */   
/* 207:    */   public void setOptions(String[] options)
/* 208:    */     throws Exception
/* 209:    */   {
/* 210:364 */     setUseK2Prior(Utils.getFlag("k2", options));
/* 211:    */     
/* 212:366 */     super.setOptions(options);
/* 213:    */   }
/* 214:    */   
/* 215:    */   public String[] getOptions()
/* 216:    */   {
/* 217:377 */     Vector<String> options = new Vector();
/* 218:379 */     if (isUseK2Prior()) {
/* 219:380 */       options.add("-k2");
/* 220:    */     }
/* 221:383 */     return (String[])options.toArray(new String[0]);
/* 222:    */   }
/* 223:    */   
/* 224:    */   public String getRevision()
/* 225:    */   {
/* 226:393 */     return RevisionUtils.extract("$Revision: 12470 $");
/* 227:    */   }
/* 228:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.estimate.MultiNomialBMAEstimator
 * JD-Core Version:    0.7.0.1
 */