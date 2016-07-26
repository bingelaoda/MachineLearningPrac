/*   1:    */ package weka.classifiers.bayes.net.search.local;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Random;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.classifiers.bayes.BayesNet;
/*   8:    */ import weka.classifiers.bayes.net.ParentSet;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.Option;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ import weka.core.TechnicalInformation;
/*  13:    */ import weka.core.TechnicalInformation.Field;
/*  14:    */ import weka.core.TechnicalInformation.Type;
/*  15:    */ import weka.core.TechnicalInformationHandler;
/*  16:    */ import weka.core.Utils;
/*  17:    */ 
/*  18:    */ public class K2
/*  19:    */   extends LocalScoreSearchAlgorithm
/*  20:    */   implements TechnicalInformationHandler
/*  21:    */ {
/*  22:    */   static final long serialVersionUID = 6176545934752116631L;
/*  23:122 */   boolean m_bRandomOrder = false;
/*  24:    */   
/*  25:    */   public TechnicalInformation getTechnicalInformation()
/*  26:    */   {
/*  27:136 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.PROCEEDINGS);
/*  28:137 */     result.setValue(TechnicalInformation.Field.AUTHOR, "G.F. Cooper and E. Herskovits");
/*  29:138 */     result.setValue(TechnicalInformation.Field.YEAR, "1990");
/*  30:139 */     result.setValue(TechnicalInformation.Field.TITLE, "A Bayesian method for constructing Bayesian belief networks from databases");
/*  31:    */     
/*  32:    */ 
/*  33:142 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Proceedings of the Conference on Uncertainty in AI");
/*  34:    */     
/*  35:144 */     result.setValue(TechnicalInformation.Field.PAGES, "86-94");
/*  36:    */     
/*  37:146 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.ARTICLE);
/*  38:147 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "G. Cooper and E. Herskovits");
/*  39:148 */     additional.setValue(TechnicalInformation.Field.YEAR, "1992");
/*  40:149 */     additional.setValue(TechnicalInformation.Field.TITLE, "A Bayesian method for the induction of probabilistic networks from data");
/*  41:    */     
/*  42:    */ 
/*  43:152 */     additional.setValue(TechnicalInformation.Field.JOURNAL, "Machine Learning");
/*  44:153 */     additional.setValue(TechnicalInformation.Field.VOLUME, "9");
/*  45:154 */     additional.setValue(TechnicalInformation.Field.NUMBER, "4");
/*  46:155 */     additional.setValue(TechnicalInformation.Field.PAGES, "309-347");
/*  47:    */     
/*  48:157 */     return result;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void search(BayesNet bayesNet, Instances instances)
/*  52:    */     throws Exception
/*  53:    */   {
/*  54:171 */     int[] nOrder = new int[instances.numAttributes()];
/*  55:172 */     nOrder[0] = instances.classIndex();
/*  56:    */     
/*  57:174 */     int nAttribute = 0;
/*  58:176 */     for (int iOrder = 1; iOrder < instances.numAttributes(); iOrder++)
/*  59:    */     {
/*  60:177 */       if (nAttribute == instances.classIndex()) {
/*  61:178 */         nAttribute++;
/*  62:    */       }
/*  63:180 */       nOrder[iOrder] = (nAttribute++);
/*  64:    */     }
/*  65:183 */     if (this.m_bRandomOrder)
/*  66:    */     {
/*  67:185 */       Random random = new Random();
/*  68:    */       int iClass;
/*  69:    */       int iClass;
/*  70:187 */       if (getInitAsNaiveBayes()) {
/*  71:188 */         iClass = 0;
/*  72:    */       } else {
/*  73:190 */         iClass = -1;
/*  74:    */       }
/*  75:192 */       for (int iOrder = 0; iOrder < instances.numAttributes(); iOrder++)
/*  76:    */       {
/*  77:193 */         int iOrder2 = random.nextInt(instances.numAttributes());
/*  78:194 */         if ((iOrder != iClass) && (iOrder2 != iClass))
/*  79:    */         {
/*  80:195 */           int nTmp = nOrder[iOrder];
/*  81:196 */           nOrder[iOrder] = nOrder[iOrder2];
/*  82:197 */           nOrder[iOrder2] = nTmp;
/*  83:    */         }
/*  84:    */       }
/*  85:    */     }
/*  86:203 */     double[] fBaseScores = new double[instances.numAttributes()];
/*  87:204 */     for (int iOrder = 0; iOrder < instances.numAttributes(); iOrder++)
/*  88:    */     {
/*  89:205 */       int iAttribute = nOrder[iOrder];
/*  90:206 */       fBaseScores[iAttribute] = calcNodeScore(iAttribute);
/*  91:    */     }
/*  92:210 */     for (int iOrder = 1; iOrder < instances.numAttributes(); iOrder++)
/*  93:    */     {
/*  94:211 */       int iAttribute = nOrder[iOrder];
/*  95:212 */       double fBestScore = fBaseScores[iAttribute];
/*  96:    */       
/*  97:214 */       boolean bProgress = bayesNet.getParentSet(iAttribute).getNrOfParents() < getMaxNrOfParents();
/*  98:215 */       while (bProgress)
/*  99:    */       {
/* 100:216 */         int nBestAttribute = -1;
/* 101:217 */         for (int iOrder2 = 0; iOrder2 < iOrder; iOrder2++)
/* 102:    */         {
/* 103:218 */           int iAttribute2 = nOrder[iOrder2];
/* 104:219 */           double fScore = calcScoreWithExtraParent(iAttribute, iAttribute2);
/* 105:220 */           if (fScore > fBestScore)
/* 106:    */           {
/* 107:221 */             fBestScore = fScore;
/* 108:222 */             nBestAttribute = iAttribute2;
/* 109:    */           }
/* 110:    */         }
/* 111:225 */         if (nBestAttribute != -1)
/* 112:    */         {
/* 113:226 */           bayesNet.getParentSet(iAttribute).addParent(nBestAttribute, instances);
/* 114:    */           
/* 115:228 */           fBaseScores[iAttribute] = fBestScore;
/* 116:229 */           bProgress = bayesNet.getParentSet(iAttribute).getNrOfParents() < getMaxNrOfParents();
/* 117:    */         }
/* 118:    */         else
/* 119:    */         {
/* 120:231 */           bProgress = false;
/* 121:    */         }
/* 122:    */       }
/* 123:    */     }
/* 124:    */   }
/* 125:    */   
/* 126:    */   public void setMaxNrOfParents(int nMaxNrOfParents)
/* 127:    */   {
/* 128:243 */     this.m_nMaxNrOfParents = nMaxNrOfParents;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public int getMaxNrOfParents()
/* 132:    */   {
/* 133:252 */     return this.m_nMaxNrOfParents;
/* 134:    */   }
/* 135:    */   
/* 136:    */   public void setInitAsNaiveBayes(boolean bInitAsNaiveBayes)
/* 137:    */   {
/* 138:261 */     this.m_bInitAsNaiveBayes = bInitAsNaiveBayes;
/* 139:    */   }
/* 140:    */   
/* 141:    */   public boolean getInitAsNaiveBayes()
/* 142:    */   {
/* 143:270 */     return this.m_bInitAsNaiveBayes;
/* 144:    */   }
/* 145:    */   
/* 146:    */   public void setRandomOrder(boolean bRandomOrder)
/* 147:    */   {
/* 148:279 */     this.m_bRandomOrder = bRandomOrder;
/* 149:    */   }
/* 150:    */   
/* 151:    */   public boolean getRandomOrder()
/* 152:    */   {
/* 153:288 */     return this.m_bRandomOrder;
/* 154:    */   }
/* 155:    */   
/* 156:    */   public Enumeration<Option> listOptions()
/* 157:    */   {
/* 158:298 */     Vector<Option> newVector = new Vector(2);
/* 159:    */     
/* 160:300 */     newVector.addElement(new Option("\tInitial structure is empty (instead of Naive Bayes)", "N", 0, "-N"));
/* 161:    */     
/* 162:    */ 
/* 163:303 */     newVector.addElement(new Option("\tMaximum number of parents", "P", 1, "-P <nr of parents>"));
/* 164:    */     
/* 165:    */ 
/* 166:306 */     newVector.addElement(new Option("\tRandom order.\n\t(default false)", "R", 0, "-R"));
/* 167:    */     
/* 168:    */ 
/* 169:309 */     newVector.addAll(Collections.list(super.listOptions()));
/* 170:    */     
/* 171:311 */     return newVector.elements();
/* 172:    */   }
/* 173:    */   
/* 174:    */   public void setOptions(String[] options)
/* 175:    */     throws Exception
/* 176:    */   {
/* 177:358 */     setRandomOrder(Utils.getFlag('R', options));
/* 178:    */     
/* 179:360 */     this.m_bInitAsNaiveBayes = (!Utils.getFlag('N', options));
/* 180:    */     
/* 181:362 */     String sMaxNrOfParents = Utils.getOption('P', options);
/* 182:364 */     if (sMaxNrOfParents.length() != 0) {
/* 183:365 */       setMaxNrOfParents(Integer.parseInt(sMaxNrOfParents));
/* 184:    */     } else {
/* 185:367 */       setMaxNrOfParents(100000);
/* 186:    */     }
/* 187:369 */     super.setOptions(options);
/* 188:    */   }
/* 189:    */   
/* 190:    */   public String[] getOptions()
/* 191:    */   {
/* 192:380 */     Vector<String> options = new Vector();
/* 193:    */     
/* 194:382 */     options.add("-P");
/* 195:383 */     options.add("" + this.m_nMaxNrOfParents);
/* 196:384 */     if (!this.m_bInitAsNaiveBayes) {
/* 197:385 */       options.add("-N");
/* 198:    */     }
/* 199:387 */     if (getRandomOrder()) {
/* 200:388 */       options.add("-R");
/* 201:    */     }
/* 202:391 */     Collections.addAll(options, super.getOptions());
/* 203:    */     
/* 204:    */ 
/* 205:394 */     return (String[])options.toArray(new String[0]);
/* 206:    */   }
/* 207:    */   
/* 208:    */   public String globalInfo()
/* 209:    */   {
/* 210:404 */     return "This Bayes Network learning algorithm uses a hill climbing algorithm restricted by an order on the variables.\n\nFor more information see:\n\n" + getTechnicalInformation().toString() + "\n\n" + "Works with nominal variables and no missing values only.";
/* 211:    */   }
/* 212:    */   
/* 213:    */   public String randomOrderTipText()
/* 214:    */   {
/* 215:415 */     return "When set to true, the order of the nodes in the network is random. Default random order is false and the order of the nodes in the dataset is used. In any case, when the network was initialized as Naive Bayes Network, the class variable is first in the ordering though.";
/* 216:    */   }
/* 217:    */   
/* 218:    */   public String getRevision()
/* 219:    */   {
/* 220:429 */     return RevisionUtils.extract("$Revision: 11247 $");
/* 221:    */   }
/* 222:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.search.local.K2
 * JD-Core Version:    0.7.0.1
 */