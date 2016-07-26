/*   1:    */ package weka.classifiers.bayes.net.search.global;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.classifiers.bayes.BayesNet;
/*   7:    */ import weka.classifiers.bayes.net.ParentSet;
/*   8:    */ import weka.classifiers.bayes.net.search.SearchAlgorithm;
/*   9:    */ import weka.core.Instance;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.Option;
/*  12:    */ import weka.core.RevisionUtils;
/*  13:    */ import weka.core.SelectedTag;
/*  14:    */ import weka.core.Tag;
/*  15:    */ import weka.core.Utils;
/*  16:    */ 
/*  17:    */ public class GlobalScoreSearchAlgorithm
/*  18:    */   extends SearchAlgorithm
/*  19:    */ {
/*  20:    */   static final long serialVersionUID = 7341389867906199781L;
/*  21:    */   BayesNet m_BayesNet;
/*  22: 84 */   boolean m_bUseProb = true;
/*  23: 87 */   int m_nNrOfFolds = 10;
/*  24:    */   static final int LOOCV = 0;
/*  25:    */   static final int KFOLDCV = 1;
/*  26:    */   static final int CUMCV = 2;
/*  27: 97 */   public static final Tag[] TAGS_CV_TYPE = { new Tag(0, "LOO-CV"), new Tag(1, "k-Fold-CV"), new Tag(2, "Cumulative-CV") };
/*  28:102 */   int m_nCVType = 0;
/*  29:    */   
/*  30:    */   public double calcScore(BayesNet bayesNet)
/*  31:    */     throws Exception
/*  32:    */   {
/*  33:114 */     switch (this.m_nCVType)
/*  34:    */     {
/*  35:    */     case 0: 
/*  36:116 */       return leaveOneOutCV(bayesNet);
/*  37:    */     case 2: 
/*  38:118 */       return cumulativeCV(bayesNet);
/*  39:    */     case 1: 
/*  40:120 */       return kFoldCV(bayesNet, this.m_nNrOfFolds);
/*  41:    */     }
/*  42:122 */     throw new Exception("Unrecognized cross validation type encountered: " + this.m_nCVType);
/*  43:    */   }
/*  44:    */   
/*  45:    */   public double calcScoreWithExtraParent(int nNode, int nCandidateParent)
/*  46:    */     throws Exception
/*  47:    */   {
/*  48:137 */     ParentSet oParentSet = this.m_BayesNet.getParentSet(nNode);
/*  49:138 */     Instances instances = this.m_BayesNet.m_Instances;
/*  50:141 */     for (int iParent = 0; iParent < oParentSet.getNrOfParents(); iParent++) {
/*  51:142 */       if (oParentSet.getParent(iParent) == nCandidateParent) {
/*  52:143 */         return -1.0E+100D;
/*  53:    */       }
/*  54:    */     }
/*  55:148 */     oParentSet.addParent(nCandidateParent, instances);
/*  56:    */     
/*  57:    */ 
/*  58:151 */     double fAccuracy = calcScore(this.m_BayesNet);
/*  59:    */     
/*  60:    */ 
/*  61:154 */     oParentSet.deleteLastParent(instances);
/*  62:    */     
/*  63:156 */     return fAccuracy;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public double calcScoreWithMissingParent(int nNode, int nCandidateParent)
/*  67:    */     throws Exception
/*  68:    */   {
/*  69:170 */     ParentSet oParentSet = this.m_BayesNet.getParentSet(nNode);
/*  70:171 */     Instances instances = this.m_BayesNet.m_Instances;
/*  71:174 */     if (!oParentSet.contains(nCandidateParent)) {
/*  72:175 */       return -1.0E+100D;
/*  73:    */     }
/*  74:179 */     int iParent = oParentSet.deleteParent(nCandidateParent, instances);
/*  75:    */     
/*  76:    */ 
/*  77:182 */     double fAccuracy = calcScore(this.m_BayesNet);
/*  78:    */     
/*  79:    */ 
/*  80:185 */     oParentSet.addParent(nCandidateParent, iParent, instances);
/*  81:    */     
/*  82:187 */     return fAccuracy;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public double calcScoreWithReversedParent(int nNode, int nCandidateParent)
/*  86:    */     throws Exception
/*  87:    */   {
/*  88:201 */     ParentSet oParentSet = this.m_BayesNet.getParentSet(nNode);
/*  89:202 */     ParentSet oParentSet2 = this.m_BayesNet.getParentSet(nCandidateParent);
/*  90:203 */     Instances instances = this.m_BayesNet.m_Instances;
/*  91:206 */     if (!oParentSet.contains(nCandidateParent)) {
/*  92:207 */       return -1.0E+100D;
/*  93:    */     }
/*  94:211 */     int iParent = oParentSet.deleteParent(nCandidateParent, instances);
/*  95:212 */     oParentSet2.addParent(nNode, instances);
/*  96:    */     
/*  97:    */ 
/*  98:215 */     double fAccuracy = calcScore(this.m_BayesNet);
/*  99:    */     
/* 100:    */ 
/* 101:218 */     oParentSet2.deleteLastParent(instances);
/* 102:219 */     oParentSet.addParent(nCandidateParent, iParent, instances);
/* 103:    */     
/* 104:221 */     return fAccuracy;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public double leaveOneOutCV(BayesNet bayesNet)
/* 108:    */     throws Exception
/* 109:    */   {
/* 110:234 */     this.m_BayesNet = bayesNet;
/* 111:235 */     double fAccuracy = 0.0D;
/* 112:236 */     double fWeight = 0.0D;
/* 113:237 */     Instances instances = bayesNet.m_Instances;
/* 114:238 */     bayesNet.estimateCPTs();
/* 115:239 */     for (int iInstance = 0; iInstance < instances.numInstances(); iInstance++)
/* 116:    */     {
/* 117:240 */       Instance instance = instances.instance(iInstance);
/* 118:241 */       instance.setWeight(-instance.weight());
/* 119:242 */       bayesNet.updateClassifier(instance);
/* 120:243 */       fAccuracy += accuracyIncrease(instance);
/* 121:244 */       fWeight += instance.weight();
/* 122:245 */       instance.setWeight(-instance.weight());
/* 123:246 */       bayesNet.updateClassifier(instance);
/* 124:    */     }
/* 125:248 */     return fAccuracy / fWeight;
/* 126:    */   }
/* 127:    */   
/* 128:    */   public double cumulativeCV(BayesNet bayesNet)
/* 129:    */     throws Exception
/* 130:    */   {
/* 131:262 */     this.m_BayesNet = bayesNet;
/* 132:263 */     double fAccuracy = 0.0D;
/* 133:264 */     double fWeight = 0.0D;
/* 134:265 */     Instances instances = bayesNet.m_Instances;
/* 135:266 */     bayesNet.initCPTs();
/* 136:267 */     for (int iInstance = 0; iInstance < instances.numInstances(); iInstance++)
/* 137:    */     {
/* 138:268 */       Instance instance = instances.instance(iInstance);
/* 139:269 */       fAccuracy += accuracyIncrease(instance);
/* 140:270 */       bayesNet.updateClassifier(instance);
/* 141:271 */       fWeight += instance.weight();
/* 142:    */     }
/* 143:273 */     return fAccuracy / fWeight;
/* 144:    */   }
/* 145:    */   
/* 146:    */   public double kFoldCV(BayesNet bayesNet, int nNrOfFolds)
/* 147:    */     throws Exception
/* 148:    */   {
/* 149:286 */     this.m_BayesNet = bayesNet;
/* 150:287 */     double fAccuracy = 0.0D;
/* 151:288 */     double fWeight = 0.0D;
/* 152:289 */     Instances instances = bayesNet.m_Instances;
/* 153:    */     
/* 154:291 */     bayesNet.estimateCPTs();
/* 155:292 */     int nFoldStart = 0;
/* 156:293 */     int nFoldEnd = instances.numInstances() / nNrOfFolds;
/* 157:294 */     int iFold = 1;
/* 158:295 */     while (nFoldStart < instances.numInstances())
/* 159:    */     {
/* 160:297 */       for (int iInstance = nFoldStart; iInstance < nFoldEnd; iInstance++)
/* 161:    */       {
/* 162:298 */         Instance instance = instances.instance(iInstance);
/* 163:299 */         instance.setWeight(-instance.weight());
/* 164:300 */         bayesNet.updateClassifier(instance);
/* 165:    */       }
/* 166:304 */       for (int iInstance = nFoldStart; iInstance < nFoldEnd; iInstance++)
/* 167:    */       {
/* 168:305 */         Instance instance = instances.instance(iInstance);
/* 169:306 */         instance.setWeight(-instance.weight());
/* 170:307 */         fAccuracy += accuracyIncrease(instance);
/* 171:308 */         fWeight += instance.weight();
/* 172:309 */         instance.setWeight(-instance.weight());
/* 173:    */       }
/* 174:313 */       for (int iInstance = nFoldStart; iInstance < nFoldEnd; iInstance++)
/* 175:    */       {
/* 176:314 */         Instance instance = instances.instance(iInstance);
/* 177:315 */         instance.setWeight(-instance.weight());
/* 178:316 */         bayesNet.updateClassifier(instance);
/* 179:    */       }
/* 180:320 */       nFoldStart = nFoldEnd;
/* 181:321 */       iFold++;
/* 182:322 */       nFoldEnd = iFold * instances.numInstances() / nNrOfFolds;
/* 183:    */     }
/* 184:324 */     return fAccuracy / fWeight;
/* 185:    */   }
/* 186:    */   
/* 187:    */   double accuracyIncrease(Instance instance)
/* 188:    */     throws Exception
/* 189:    */   {
/* 190:336 */     if (this.m_bUseProb)
/* 191:    */     {
/* 192:337 */       double[] fProb = this.m_BayesNet.distributionForInstance(instance);
/* 193:338 */       return fProb[((int)instance.classValue())] * instance.weight();
/* 194:    */     }
/* 195:340 */     if (this.m_BayesNet.classifyInstance(instance) == instance.classValue()) {
/* 196:341 */       return instance.weight();
/* 197:    */     }
/* 198:344 */     return 0.0D;
/* 199:    */   }
/* 200:    */   
/* 201:    */   public boolean getUseProb()
/* 202:    */   {
/* 203:351 */     return this.m_bUseProb;
/* 204:    */   }
/* 205:    */   
/* 206:    */   public void setUseProb(boolean useProb)
/* 207:    */   {
/* 208:358 */     this.m_bUseProb = useProb;
/* 209:    */   }
/* 210:    */   
/* 211:    */   public void setCVType(SelectedTag newCVType)
/* 212:    */   {
/* 213:367 */     if (newCVType.getTags() == TAGS_CV_TYPE) {
/* 214:368 */       this.m_nCVType = newCVType.getSelectedTag().getID();
/* 215:    */     }
/* 216:    */   }
/* 217:    */   
/* 218:    */   public SelectedTag getCVType()
/* 219:    */   {
/* 220:378 */     return new SelectedTag(this.m_nCVType, TAGS_CV_TYPE);
/* 221:    */   }
/* 222:    */   
/* 223:    */   public void setMarkovBlanketClassifier(boolean bMarkovBlanketClassifier)
/* 224:    */   {
/* 225:387 */     super.setMarkovBlanketClassifier(bMarkovBlanketClassifier);
/* 226:    */   }
/* 227:    */   
/* 228:    */   public boolean getMarkovBlanketClassifier()
/* 229:    */   {
/* 230:396 */     return super.getMarkovBlanketClassifier();
/* 231:    */   }
/* 232:    */   
/* 233:    */   public Enumeration<Option> listOptions()
/* 234:    */   {
/* 235:406 */     Vector<Option> newVector = new Vector();
/* 236:    */     
/* 237:408 */     newVector.addElement(new Option("\tApplies a Markov Blanket correction to the network structure, \n\tafter a network structure is learned. This ensures that all \n\tnodes in the network are part of the Markov blanket of the \n\tclassifier node.", "mbc", 0, "-mbc"));
/* 238:    */     
/* 239:    */ 
/* 240:    */ 
/* 241:    */ 
/* 242:    */ 
/* 243:414 */     newVector.addElement(new Option("\tScore type (LOO-CV,k-Fold-CV,Cumulative-CV)", "S", 1, "-S [LOO-CV|k-Fold-CV|Cumulative-CV]"));
/* 244:    */     
/* 245:    */ 
/* 246:    */ 
/* 247:418 */     newVector.addElement(new Option("\tUse probabilistic or 0/1 scoring.\n\t(default probabilistic scoring)", "Q", 0, "-Q"));
/* 248:    */     
/* 249:    */ 
/* 250:    */ 
/* 251:422 */     newVector.addAll(Collections.list(super.listOptions()));
/* 252:    */     
/* 253:424 */     return newVector.elements();
/* 254:    */   }
/* 255:    */   
/* 256:    */   public void setOptions(String[] options)
/* 257:    */     throws Exception
/* 258:    */   {
/* 259:461 */     setMarkovBlanketClassifier(Utils.getFlag("mbc", options));
/* 260:    */     
/* 261:463 */     String sScore = Utils.getOption('S', options);
/* 262:465 */     if (sScore.compareTo("LOO-CV") == 0) {
/* 263:466 */       setCVType(new SelectedTag(0, TAGS_CV_TYPE));
/* 264:    */     }
/* 265:468 */     if (sScore.compareTo("k-Fold-CV") == 0) {
/* 266:469 */       setCVType(new SelectedTag(1, TAGS_CV_TYPE));
/* 267:    */     }
/* 268:471 */     if (sScore.compareTo("Cumulative-CV") == 0) {
/* 269:472 */       setCVType(new SelectedTag(2, TAGS_CV_TYPE));
/* 270:    */     }
/* 271:474 */     setUseProb(!Utils.getFlag('Q', options));
/* 272:475 */     super.setOptions(options);
/* 273:    */   }
/* 274:    */   
/* 275:    */   public String[] getOptions()
/* 276:    */   {
/* 277:486 */     Vector<String> options = new Vector();
/* 278:488 */     if (getMarkovBlanketClassifier()) {
/* 279:489 */       options.add("-mbc");
/* 280:    */     }
/* 281:492 */     options.add("-S");
/* 282:494 */     switch (this.m_nCVType)
/* 283:    */     {
/* 284:    */     case 0: 
/* 285:496 */       options.add("LOO-CV");
/* 286:497 */       break;
/* 287:    */     case 1: 
/* 288:499 */       options.add("k-Fold-CV");
/* 289:500 */       break;
/* 290:    */     case 2: 
/* 291:502 */       options.add("Cumulative-CV");
/* 292:    */     }
/* 293:506 */     if (!getUseProb()) {
/* 294:507 */       options.add("-Q");
/* 295:    */     }
/* 296:510 */     Collections.addAll(options, super.getOptions());
/* 297:    */     
/* 298:512 */     return (String[])options.toArray(new String[0]);
/* 299:    */   }
/* 300:    */   
/* 301:    */   public String CVTypeTipText()
/* 302:    */   {
/* 303:519 */     return "Select cross validation strategy to be used in searching for networks.LOO-CV = Leave one out cross validation\nk-Fold-CV = k fold cross validation\nCumulative-CV = cumulative cross validation.";
/* 304:    */   }
/* 305:    */   
/* 306:    */   public String useProbTipText()
/* 307:    */   {
/* 308:529 */     return "If set to true, the probability of the class if returned in the estimate of the accuracy. If set to false, the accuracy estimate is only increased if the classifier returns exactly the correct class.";
/* 309:    */   }
/* 310:    */   
/* 311:    */   public String globalInfo()
/* 312:    */   {
/* 313:540 */     return "This Bayes Network learning algorithm uses cross validation to estimate classification accuracy.";
/* 314:    */   }
/* 315:    */   
/* 316:    */   public String markovBlanketClassifierTipText()
/* 317:    */   {
/* 318:549 */     return super.markovBlanketClassifierTipText();
/* 319:    */   }
/* 320:    */   
/* 321:    */   public String getRevision()
/* 322:    */   {
/* 323:559 */     return RevisionUtils.extract("$Revision: 10154 $");
/* 324:    */   }
/* 325:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.search.global.GlobalScoreSearchAlgorithm
 * JD-Core Version:    0.7.0.1
 */