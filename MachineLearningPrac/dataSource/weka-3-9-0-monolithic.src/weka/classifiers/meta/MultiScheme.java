/*   1:    */ package weka.classifiers.meta;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Random;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.classifiers.Classifier;
/*   9:    */ import weka.classifiers.Evaluation;
/*  10:    */ import weka.classifiers.RandomizableMultipleClassifiersCombiner;
/*  11:    */ import weka.core.Attribute;
/*  12:    */ import weka.core.Capabilities;
/*  13:    */ import weka.core.Instance;
/*  14:    */ import weka.core.Instances;
/*  15:    */ import weka.core.Option;
/*  16:    */ import weka.core.OptionHandler;
/*  17:    */ import weka.core.RevisionUtils;
/*  18:    */ import weka.core.Utils;
/*  19:    */ 
/*  20:    */ public class MultiScheme
/*  21:    */   extends RandomizableMultipleClassifiersCombiner
/*  22:    */ {
/*  23:    */   static final long serialVersionUID = 5710744346128957520L;
/*  24:    */   protected Classifier m_Classifier;
/*  25:    */   protected int m_ClassifierIndex;
/*  26:    */   protected int m_NumXValFolds;
/*  27:    */   
/*  28:    */   public String globalInfo()
/*  29:    */   {
/*  30: 96 */     return "Class for selecting a classifier from among several using cross validation on the training data or the performance on the training data. Performance is measured based on percent correct (classification) or mean-squared error (regression).";
/*  31:    */   }
/*  32:    */   
/*  33:    */   public Enumeration<Option> listOptions()
/*  34:    */   {
/*  35:109 */     Vector<Option> newVector = new Vector(1);
/*  36:110 */     newVector.addElement(new Option("\tUse cross validation for model selection using the\n\tgiven number of folds. (default 0, is to\n\tuse training error)", "X", 1, "-X <number of folds>"));
/*  37:    */     
/*  38:    */ 
/*  39:    */ 
/*  40:    */ 
/*  41:    */ 
/*  42:116 */     newVector.addAll(Collections.list(super.listOptions()));
/*  43:    */     
/*  44:118 */     return newVector.elements();
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void setOptions(String[] options)
/*  48:    */     throws Exception
/*  49:    */   {
/*  50:152 */     String numFoldsString = Utils.getOption('X', options);
/*  51:153 */     if (numFoldsString.length() != 0) {
/*  52:154 */       setNumFolds(Integer.parseInt(numFoldsString));
/*  53:    */     } else {
/*  54:156 */       setNumFolds(0);
/*  55:    */     }
/*  56:158 */     super.setOptions(options);
/*  57:    */   }
/*  58:    */   
/*  59:    */   public String[] getOptions()
/*  60:    */   {
/*  61:169 */     String[] superOptions = super.getOptions();
/*  62:170 */     String[] options = new String[superOptions.length + 2];
/*  63:    */     
/*  64:172 */     int current = 0;
/*  65:173 */     options[(current++)] = "-X";options[(current++)] = ("" + getNumFolds());
/*  66:    */     
/*  67:175 */     System.arraycopy(superOptions, 0, options, current, superOptions.length);
/*  68:    */     
/*  69:    */ 
/*  70:178 */     return options;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public String classifiersTipText()
/*  74:    */   {
/*  75:187 */     return "The classifiers to be chosen from.";
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void setClassifiers(Classifier[] classifiers)
/*  79:    */   {
/*  80:197 */     this.m_Classifiers = classifiers;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public Classifier[] getClassifiers()
/*  84:    */   {
/*  85:207 */     return this.m_Classifiers;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public Classifier getClassifier(int index)
/*  89:    */   {
/*  90:218 */     return this.m_Classifiers[index];
/*  91:    */   }
/*  92:    */   
/*  93:    */   protected String getClassifierSpec(int index)
/*  94:    */   {
/*  95:232 */     if (this.m_Classifiers.length < index) {
/*  96:233 */       return "";
/*  97:    */     }
/*  98:235 */     Classifier c = getClassifier(index);
/*  99:236 */     if ((c instanceof OptionHandler)) {
/* 100:237 */       return c.getClass().getName() + " " + Utils.joinOptions(((OptionHandler)c).getOptions());
/* 101:    */     }
/* 102:240 */     return c.getClass().getName();
/* 103:    */   }
/* 104:    */   
/* 105:    */   public String seedTipText()
/* 106:    */   {
/* 107:249 */     return "The seed used for randomizing the data for cross-validation.";
/* 108:    */   }
/* 109:    */   
/* 110:    */   public void setSeed(int seed)
/* 111:    */   {
/* 112:260 */     this.m_Seed = seed;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public int getSeed()
/* 116:    */   {
/* 117:270 */     return this.m_Seed;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public String numFoldsTipText()
/* 121:    */   {
/* 122:279 */     return "The number of folds used for cross-validation (if 0, performance on training data will be used).";
/* 123:    */   }
/* 124:    */   
/* 125:    */   public int getNumFolds()
/* 126:    */   {
/* 127:291 */     return this.m_NumXValFolds;
/* 128:    */   }
/* 129:    */   
/* 130:    */   public void setNumFolds(int numFolds)
/* 131:    */   {
/* 132:302 */     this.m_NumXValFolds = numFolds;
/* 133:    */   }
/* 134:    */   
/* 135:    */   public String debugTipText()
/* 136:    */   {
/* 137:311 */     return "Whether debug information is output to console.";
/* 138:    */   }
/* 139:    */   
/* 140:    */   public void setDebug(boolean debug)
/* 141:    */   {
/* 142:321 */     this.m_Debug = debug;
/* 143:    */   }
/* 144:    */   
/* 145:    */   public boolean getDebug()
/* 146:    */   {
/* 147:331 */     return this.m_Debug;
/* 148:    */   }
/* 149:    */   
/* 150:    */   public int getBestClassifierIndex()
/* 151:    */   {
/* 152:341 */     return this.m_ClassifierIndex;
/* 153:    */   }
/* 154:    */   
/* 155:    */   public void buildClassifier(Instances data)
/* 156:    */     throws Exception
/* 157:    */   {
/* 158:354 */     if (this.m_Classifiers.length == 0) {
/* 159:355 */       throw new Exception("No base classifiers have been set!");
/* 160:    */     }
/* 161:359 */     getCapabilities().testWithFail(data);
/* 162:    */     
/* 163:    */ 
/* 164:362 */     Instances newData = new Instances(data);
/* 165:363 */     newData.deleteWithMissingClass();
/* 166:    */     
/* 167:365 */     Random random = new Random(this.m_Seed);
/* 168:366 */     newData.randomize(random);
/* 169:367 */     if ((newData.classAttribute().isNominal()) && (this.m_NumXValFolds > 1)) {
/* 170:368 */       newData.stratify(this.m_NumXValFolds);
/* 171:    */     }
/* 172:370 */     Instances train = newData;
/* 173:371 */     Instances test = newData;
/* 174:372 */     Classifier bestClassifier = null;
/* 175:373 */     int bestIndex = -1;
/* 176:374 */     double bestPerformance = (0.0D / 0.0D);
/* 177:375 */     int numClassifiers = this.m_Classifiers.length;
/* 178:376 */     for (int i = 0; i < numClassifiers; i++)
/* 179:    */     {
/* 180:377 */       Classifier currentClassifier = getClassifier(i);
/* 181:    */       Evaluation evaluation;
/* 182:379 */       if (this.m_NumXValFolds > 1)
/* 183:    */       {
/* 184:380 */         Evaluation evaluation = new Evaluation(newData);
/* 185:381 */         for (int j = 0; j < this.m_NumXValFolds; j++)
/* 186:    */         {
/* 187:385 */           train = newData.trainCV(this.m_NumXValFolds, j, new Random(1L));
/* 188:386 */           test = newData.testCV(this.m_NumXValFolds, j);
/* 189:387 */           currentClassifier.buildClassifier(train);
/* 190:388 */           evaluation.setPriors(train);
/* 191:389 */           evaluation.evaluateModel(currentClassifier, test, new Object[0]);
/* 192:    */         }
/* 193:    */       }
/* 194:    */       else
/* 195:    */       {
/* 196:392 */         currentClassifier.buildClassifier(train);
/* 197:393 */         evaluation = new Evaluation(train);
/* 198:394 */         evaluation.evaluateModel(currentClassifier, test, new Object[0]);
/* 199:    */       }
/* 200:397 */       double error = evaluation.errorRate();
/* 201:398 */       if (this.m_Debug) {
/* 202:399 */         System.err.println("Error rate: " + Utils.doubleToString(error, 6, 4) + " for classifier " + currentClassifier.getClass().getName());
/* 203:    */       }
/* 204:404 */       if ((i == 0) || (error < bestPerformance))
/* 205:    */       {
/* 206:405 */         bestClassifier = currentClassifier;
/* 207:406 */         bestPerformance = error;
/* 208:407 */         bestIndex = i;
/* 209:    */       }
/* 210:    */     }
/* 211:410 */     this.m_ClassifierIndex = bestIndex;
/* 212:411 */     if (this.m_NumXValFolds > 1) {
/* 213:412 */       bestClassifier.buildClassifier(newData);
/* 214:    */     }
/* 215:414 */     this.m_Classifier = bestClassifier;
/* 216:    */   }
/* 217:    */   
/* 218:    */   public double[] distributionForInstance(Instance instance)
/* 219:    */     throws Exception
/* 220:    */   {
/* 221:427 */     return this.m_Classifier.distributionForInstance(instance);
/* 222:    */   }
/* 223:    */   
/* 224:    */   public String toString()
/* 225:    */   {
/* 226:436 */     if (this.m_Classifier == null) {
/* 227:437 */       return "MultiScheme: No model built yet.";
/* 228:    */     }
/* 229:440 */     String result = "MultiScheme selection using";
/* 230:441 */     if (this.m_NumXValFolds > 1) {
/* 231:442 */       result = result + " cross validation error";
/* 232:    */     } else {
/* 233:444 */       result = result + " error on training data";
/* 234:    */     }
/* 235:446 */     result = result + " from the following:\n";
/* 236:447 */     for (int i = 0; i < this.m_Classifiers.length; i++) {
/* 237:448 */       result = result + '\t' + getClassifierSpec(i) + '\n';
/* 238:    */     }
/* 239:451 */     result = result + "Selected scheme: " + getClassifierSpec(this.m_ClassifierIndex) + "\n\n" + this.m_Classifier.toString();
/* 240:    */     
/* 241:    */ 
/* 242:    */ 
/* 243:455 */     return result;
/* 244:    */   }
/* 245:    */   
/* 246:    */   public String getRevision()
/* 247:    */   {
/* 248:464 */     return RevisionUtils.extract("$Revision: 10141 $");
/* 249:    */   }
/* 250:    */   
/* 251:    */   public static void main(String[] argv)
/* 252:    */   {
/* 253:474 */     runClassifier(new MultiScheme(), argv);
/* 254:    */   }
/* 255:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.MultiScheme
 * JD-Core Version:    0.7.0.1
 */