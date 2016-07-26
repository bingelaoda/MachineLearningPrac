/*   1:    */ package weka.classifiers.bayes.net.search.global;
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
/*  19:    */   extends GlobalScoreSearchAlgorithm
/*  20:    */   implements TechnicalInformationHandler
/*  21:    */ {
/*  22:    */   static final long serialVersionUID = -6626871067466338256L;
/*  23:128 */   boolean m_bRandomOrder = false;
/*  24:    */   
/*  25:    */   public TechnicalInformation getTechnicalInformation()
/*  26:    */   {
/*  27:142 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.PROCEEDINGS);
/*  28:143 */     result.setValue(TechnicalInformation.Field.AUTHOR, "G.F. Cooper and E. Herskovits");
/*  29:144 */     result.setValue(TechnicalInformation.Field.YEAR, "1990");
/*  30:145 */     result.setValue(TechnicalInformation.Field.TITLE, "A Bayesian method for constructing Bayesian belief networks from databases");
/*  31:    */     
/*  32:    */ 
/*  33:148 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Proceedings of the Conference on Uncertainty in AI");
/*  34:    */     
/*  35:150 */     result.setValue(TechnicalInformation.Field.PAGES, "86-94");
/*  36:    */     
/*  37:152 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.ARTICLE);
/*  38:153 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "G. Cooper and E. Herskovits");
/*  39:154 */     additional.setValue(TechnicalInformation.Field.YEAR, "1992");
/*  40:155 */     additional.setValue(TechnicalInformation.Field.TITLE, "A Bayesian method for the induction of probabilistic networks from data");
/*  41:    */     
/*  42:    */ 
/*  43:158 */     additional.setValue(TechnicalInformation.Field.JOURNAL, "Machine Learning");
/*  44:159 */     additional.setValue(TechnicalInformation.Field.VOLUME, "9");
/*  45:160 */     additional.setValue(TechnicalInformation.Field.NUMBER, "4");
/*  46:161 */     additional.setValue(TechnicalInformation.Field.PAGES, "309-347");
/*  47:    */     
/*  48:163 */     return result;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void search(BayesNet bayesNet, Instances instances)
/*  52:    */     throws Exception
/*  53:    */   {
/*  54:177 */     int[] nOrder = new int[instances.numAttributes()];
/*  55:178 */     nOrder[0] = instances.classIndex();
/*  56:    */     
/*  57:180 */     int nAttribute = 0;
/*  58:182 */     for (int iOrder = 1; iOrder < instances.numAttributes(); iOrder++)
/*  59:    */     {
/*  60:183 */       if (nAttribute == instances.classIndex()) {
/*  61:184 */         nAttribute++;
/*  62:    */       }
/*  63:186 */       nOrder[iOrder] = (nAttribute++);
/*  64:    */     }
/*  65:189 */     if (this.m_bRandomOrder)
/*  66:    */     {
/*  67:191 */       Random random = new Random();
/*  68:    */       int iClass;
/*  69:    */       int iClass;
/*  70:193 */       if (getInitAsNaiveBayes()) {
/*  71:194 */         iClass = 0;
/*  72:    */       } else {
/*  73:196 */         iClass = -1;
/*  74:    */       }
/*  75:198 */       for (int iOrder = 0; iOrder < instances.numAttributes(); iOrder++)
/*  76:    */       {
/*  77:199 */         int iOrder2 = random.nextInt(instances.numAttributes());
/*  78:200 */         if ((iOrder != iClass) && (iOrder2 != iClass))
/*  79:    */         {
/*  80:201 */           int nTmp = nOrder[iOrder];
/*  81:202 */           nOrder[iOrder] = nOrder[iOrder2];
/*  82:203 */           nOrder[iOrder2] = nTmp;
/*  83:    */         }
/*  84:    */       }
/*  85:    */     }
/*  86:209 */     double fBaseScore = calcScore(bayesNet);
/*  87:212 */     for (int iOrder = 1; iOrder < instances.numAttributes(); iOrder++)
/*  88:    */     {
/*  89:213 */       int iAttribute = nOrder[iOrder];
/*  90:214 */       double fBestScore = fBaseScore;
/*  91:    */       
/*  92:216 */       boolean bProgress = bayesNet.getParentSet(iAttribute).getNrOfParents() < getMaxNrOfParents();
/*  93:218 */       while ((bProgress) && (bayesNet.getParentSet(iAttribute).getNrOfParents() < getMaxNrOfParents()))
/*  94:    */       {
/*  95:219 */         int nBestAttribute = -1;
/*  96:220 */         for (int iOrder2 = 0; iOrder2 < iOrder; iOrder2++)
/*  97:    */         {
/*  98:221 */           int iAttribute2 = nOrder[iOrder2];
/*  99:222 */           double fScore = calcScoreWithExtraParent(iAttribute, iAttribute2);
/* 100:223 */           if (fScore > fBestScore)
/* 101:    */           {
/* 102:224 */             fBestScore = fScore;
/* 103:225 */             nBestAttribute = iAttribute2;
/* 104:    */           }
/* 105:    */         }
/* 106:228 */         if (nBestAttribute != -1)
/* 107:    */         {
/* 108:229 */           bayesNet.getParentSet(iAttribute).addParent(nBestAttribute, instances);
/* 109:    */           
/* 110:231 */           fBaseScore = fBestScore;
/* 111:232 */           bProgress = true;
/* 112:    */         }
/* 113:    */         else
/* 114:    */         {
/* 115:234 */           bProgress = false;
/* 116:    */         }
/* 117:    */       }
/* 118:    */     }
/* 119:    */   }
/* 120:    */   
/* 121:    */   public void setMaxNrOfParents(int nMaxNrOfParents)
/* 122:    */   {
/* 123:246 */     this.m_nMaxNrOfParents = nMaxNrOfParents;
/* 124:    */   }
/* 125:    */   
/* 126:    */   public int getMaxNrOfParents()
/* 127:    */   {
/* 128:255 */     return this.m_nMaxNrOfParents;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public void setInitAsNaiveBayes(boolean bInitAsNaiveBayes)
/* 132:    */   {
/* 133:264 */     this.m_bInitAsNaiveBayes = bInitAsNaiveBayes;
/* 134:    */   }
/* 135:    */   
/* 136:    */   public boolean getInitAsNaiveBayes()
/* 137:    */   {
/* 138:273 */     return this.m_bInitAsNaiveBayes;
/* 139:    */   }
/* 140:    */   
/* 141:    */   public void setRandomOrder(boolean bRandomOrder)
/* 142:    */   {
/* 143:282 */     this.m_bRandomOrder = bRandomOrder;
/* 144:    */   }
/* 145:    */   
/* 146:    */   public boolean getRandomOrder()
/* 147:    */   {
/* 148:291 */     return this.m_bRandomOrder;
/* 149:    */   }
/* 150:    */   
/* 151:    */   public Enumeration<Option> listOptions()
/* 152:    */   {
/* 153:301 */     Vector<Option> newVector = new Vector(0);
/* 154:    */     
/* 155:303 */     newVector.addElement(new Option("\tInitial structure is empty (instead of Naive Bayes)", "N", 0, "-N"));
/* 156:    */     
/* 157:    */ 
/* 158:306 */     newVector.addElement(new Option("\tMaximum number of parents", "P", 1, "-P <nr of parents>"));
/* 159:    */     
/* 160:    */ 
/* 161:309 */     newVector.addElement(new Option("\tRandom order.\n\t(default false)", "R", 0, "-R"));
/* 162:    */     
/* 163:    */ 
/* 164:312 */     newVector.addAll(Collections.list(super.listOptions()));
/* 165:    */     
/* 166:314 */     return newVector.elements();
/* 167:    */   }
/* 168:    */   
/* 169:    */   public void setOptions(String[] options)
/* 170:    */     throws Exception
/* 171:    */   {
/* 172:367 */     setRandomOrder(Utils.getFlag('R', options));
/* 173:    */     
/* 174:369 */     this.m_bInitAsNaiveBayes = (!Utils.getFlag('N', options));
/* 175:    */     
/* 176:371 */     String sMaxNrOfParents = Utils.getOption('P', options);
/* 177:373 */     if (sMaxNrOfParents.length() != 0) {
/* 178:374 */       setMaxNrOfParents(Integer.parseInt(sMaxNrOfParents));
/* 179:    */     } else {
/* 180:376 */       setMaxNrOfParents(100000);
/* 181:    */     }
/* 182:378 */     super.setOptions(options);
/* 183:    */   }
/* 184:    */   
/* 185:    */   public String[] getOptions()
/* 186:    */   {
/* 187:389 */     Vector<String> options = new Vector();
/* 188:    */     
/* 189:391 */     options.add("-P");
/* 190:392 */     options.add("" + this.m_nMaxNrOfParents);
/* 191:393 */     if (!this.m_bInitAsNaiveBayes) {
/* 192:394 */       options.add("-N");
/* 193:    */     }
/* 194:396 */     if (getRandomOrder()) {
/* 195:397 */       options.add("-R");
/* 196:    */     }
/* 197:400 */     Collections.addAll(options, super.getOptions());
/* 198:    */     
/* 199:    */ 
/* 200:403 */     return (String[])options.toArray(new String[0]);
/* 201:    */   }
/* 202:    */   
/* 203:    */   public String randomOrderTipText()
/* 204:    */   {
/* 205:410 */     return "When set to true, the order of the nodes in the network is random. Default random order is false and the order of the nodes in the dataset is used. In any case, when the network was initialized as Naive Bayes Network, the class variable is first in the ordering though.";
/* 206:    */   }
/* 207:    */   
/* 208:    */   public String globalInfo()
/* 209:    */   {
/* 210:424 */     return "This Bayes Network learning algorithm uses a hill climbing algorithm restricted by an order on the variables.\n\nFor more information see:\n\n" + getTechnicalInformation().toString() + "\n\n" + "Works with nominal variables and no missing values only.";
/* 211:    */   }
/* 212:    */   
/* 213:    */   public String getRevision()
/* 214:    */   {
/* 215:438 */     return RevisionUtils.extract("$Revision: 11247 $");
/* 216:    */   }
/* 217:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.search.global.K2
 * JD-Core Version:    0.7.0.1
 */