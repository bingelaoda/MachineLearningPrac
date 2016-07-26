/*   1:    */ package weka.attributeSelection;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import java.util.Random;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.classifiers.AbstractClassifier;
/*   7:    */ import weka.classifiers.Classifier;
/*   8:    */ import weka.classifiers.Evaluation;
/*   9:    */ import weka.core.Capabilities;
/*  10:    */ import weka.core.Capabilities.Capability;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.Option;
/*  13:    */ import weka.core.OptionHandler;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.Utils;
/*  16:    */ import weka.filters.Filter;
/*  17:    */ import weka.filters.unsupervised.attribute.Remove;
/*  18:    */ 
/*  19:    */ public class OneRAttributeEval
/*  20:    */   extends ASEvaluation
/*  21:    */   implements AttributeEvaluator, OptionHandler
/*  22:    */ {
/*  23:    */   static final long serialVersionUID = 4386514823886856980L;
/*  24:    */   private Instances m_trainInstances;
/*  25:    */   private int m_randomSeed;
/*  26:    */   private int m_folds;
/*  27:    */   private boolean m_evalUsingTrainingData;
/*  28:    */   private int m_minBucketSize;
/*  29:    */   
/*  30:    */   public String globalInfo()
/*  31:    */   {
/*  32:107 */     return "OneRAttributeEval :\n\nEvaluates the worth of an attribute by using the OneR classifier.\n";
/*  33:    */   }
/*  34:    */   
/*  35:    */   public String seedTipText()
/*  36:    */   {
/*  37:118 */     return "Set the seed for use in cross validation.";
/*  38:    */   }
/*  39:    */   
/*  40:    */   public void setSeed(int seed)
/*  41:    */   {
/*  42:127 */     this.m_randomSeed = seed;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public int getSeed()
/*  46:    */   {
/*  47:136 */     return this.m_randomSeed;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public String foldsTipText()
/*  51:    */   {
/*  52:146 */     return "Set the number of folds for cross validation.";
/*  53:    */   }
/*  54:    */   
/*  55:    */   public void setFolds(int folds)
/*  56:    */   {
/*  57:155 */     this.m_folds = folds;
/*  58:156 */     if (this.m_folds < 2) {
/*  59:157 */       this.m_folds = 2;
/*  60:    */     }
/*  61:    */   }
/*  62:    */   
/*  63:    */   public int getFolds()
/*  64:    */   {
/*  65:167 */     return this.m_folds;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public String evalUsingTrainingDataTipText()
/*  69:    */   {
/*  70:177 */     return "Use the training data to evaluate attributes rather than cross validation.";
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void setEvalUsingTrainingData(boolean e)
/*  74:    */   {
/*  75:187 */     this.m_evalUsingTrainingData = e;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public String minimumBucketSizeTipText()
/*  79:    */   {
/*  80:197 */     return "The minimum number of objects in a bucket (passed to OneR).";
/*  81:    */   }
/*  82:    */   
/*  83:    */   public void setMinimumBucketSize(int minB)
/*  84:    */   {
/*  85:206 */     this.m_minBucketSize = minB;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public int getMinimumBucketSize()
/*  89:    */   {
/*  90:215 */     return this.m_minBucketSize;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public boolean getEvalUsingTrainingData()
/*  94:    */   {
/*  95:224 */     return this.m_evalUsingTrainingData;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public Enumeration<Option> listOptions()
/*  99:    */   {
/* 100:235 */     Vector<Option> newVector = new Vector(4);
/* 101:    */     
/* 102:237 */     newVector.addElement(new Option("\tRandom number seed for cross validation\n\t(default = 1)", "S", 1, "-S <seed>"));
/* 103:    */     
/* 104:    */ 
/* 105:    */ 
/* 106:241 */     newVector.addElement(new Option("\tNumber of folds for cross validation\n\t(default = 10)", "F", 1, "-F <folds>"));
/* 107:    */     
/* 108:    */ 
/* 109:244 */     newVector.addElement(new Option("\tUse training data for evaluation rather than cross validaton", "D", 0, "-D"));
/* 110:    */     
/* 111:    */ 
/* 112:    */ 
/* 113:248 */     newVector.addElement(new Option("\tMinimum number of objects in a bucket\n\t(passed on to OneR, default = 6)", "B", 1, "-B <minimum bucket size>"));
/* 114:    */     
/* 115:    */ 
/* 116:    */ 
/* 117:252 */     return newVector.elements();
/* 118:    */   }
/* 119:    */   
/* 120:    */   public void setOptions(String[] options)
/* 121:    */     throws Exception
/* 122:    */   {
/* 123:292 */     String temp = Utils.getOption('S', options);
/* 124:294 */     if (temp.length() != 0) {
/* 125:295 */       setSeed(Integer.parseInt(temp));
/* 126:    */     }
/* 127:298 */     temp = Utils.getOption('F', options);
/* 128:299 */     if (temp.length() != 0) {
/* 129:300 */       setFolds(Integer.parseInt(temp));
/* 130:    */     }
/* 131:303 */     temp = Utils.getOption('B', options);
/* 132:304 */     if (temp.length() != 0) {
/* 133:305 */       setMinimumBucketSize(Integer.parseInt(temp));
/* 134:    */     }
/* 135:308 */     setEvalUsingTrainingData(Utils.getFlag('D', options));
/* 136:309 */     Utils.checkForRemainingOptions(options);
/* 137:    */   }
/* 138:    */   
/* 139:    */   public String[] getOptions()
/* 140:    */   {
/* 141:320 */     Vector<String> options = new Vector();
/* 142:322 */     if (getEvalUsingTrainingData()) {
/* 143:323 */       options.add("-D");
/* 144:    */     }
/* 145:326 */     options.add("-S");
/* 146:327 */     options.add("" + getSeed());
/* 147:328 */     options.add("-F");
/* 148:329 */     options.add("" + getFolds());
/* 149:330 */     options.add("-B");
/* 150:331 */     options.add("" + getMinimumBucketSize());
/* 151:    */     
/* 152:333 */     return (String[])options.toArray(new String[0]);
/* 153:    */   }
/* 154:    */   
/* 155:    */   public OneRAttributeEval()
/* 156:    */   {
/* 157:340 */     resetOptions();
/* 158:    */   }
/* 159:    */   
/* 160:    */   public Capabilities getCapabilities()
/* 161:    */   {
/* 162:351 */     Capabilities result = super.getCapabilities();
/* 163:352 */     result.disableAll();
/* 164:    */     
/* 165:    */ 
/* 166:355 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 167:356 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 168:357 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/* 169:358 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 170:    */     
/* 171:    */ 
/* 172:361 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 173:362 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 174:    */     
/* 175:364 */     return result;
/* 176:    */   }
/* 177:    */   
/* 178:    */   public void buildEvaluator(Instances data)
/* 179:    */     throws Exception
/* 180:    */   {
/* 181:378 */     getCapabilities().testWithFail(data);
/* 182:    */     
/* 183:380 */     this.m_trainInstances = data;
/* 184:    */   }
/* 185:    */   
/* 186:    */   protected void resetOptions()
/* 187:    */   {
/* 188:387 */     this.m_trainInstances = null;
/* 189:388 */     this.m_randomSeed = 1;
/* 190:389 */     this.m_folds = 10;
/* 191:390 */     this.m_evalUsingTrainingData = false;
/* 192:391 */     this.m_minBucketSize = 6;
/* 193:    */   }
/* 194:    */   
/* 195:    */   public double evaluateAttribute(int attribute)
/* 196:    */     throws Exception
/* 197:    */   {
/* 198:403 */     int[] featArray = new int[2];
/* 199:    */     
/* 200:    */ 
/* 201:406 */     Remove delTransform = new Remove();
/* 202:407 */     delTransform.setInvertSelection(true);
/* 203:    */     
/* 204:409 */     Instances trainCopy = new Instances(this.m_trainInstances);
/* 205:410 */     featArray[0] = attribute;
/* 206:411 */     featArray[1] = trainCopy.classIndex();
/* 207:412 */     delTransform.setAttributeIndicesArray(featArray);
/* 208:413 */     delTransform.setInputFormat(trainCopy);
/* 209:414 */     trainCopy = Filter.useFilter(trainCopy, delTransform);
/* 210:415 */     Evaluation o_Evaluation = new Evaluation(trainCopy);
/* 211:416 */     String[] oneROpts = { "-B", "" + getMinimumBucketSize() };
/* 212:417 */     Classifier oneR = AbstractClassifier.forName("weka.classifiers.rules.OneR", oneROpts);
/* 213:419 */     if (this.m_evalUsingTrainingData)
/* 214:    */     {
/* 215:420 */       oneR.buildClassifier(trainCopy);
/* 216:421 */       o_Evaluation.evaluateModel(oneR, trainCopy, new Object[0]);
/* 217:    */     }
/* 218:    */     else
/* 219:    */     {
/* 220:427 */       o_Evaluation.crossValidateModel(oneR, trainCopy, this.m_folds, new Random(this.m_randomSeed), new Object[0]);
/* 221:    */     }
/* 222:430 */     double errorRate = o_Evaluation.errorRate();
/* 223:431 */     return (1.0D - errorRate) * 100.0D;
/* 224:    */   }
/* 225:    */   
/* 226:    */   public String toString()
/* 227:    */   {
/* 228:441 */     StringBuffer text = new StringBuffer();
/* 229:443 */     if (this.m_trainInstances == null)
/* 230:    */     {
/* 231:444 */       text.append("\tOneR feature evaluator has not been built yet");
/* 232:    */     }
/* 233:    */     else
/* 234:    */     {
/* 235:446 */       text.append("\tOneR feature evaluator.\n\n");
/* 236:447 */       text.append("\tUsing ");
/* 237:448 */       if (this.m_evalUsingTrainingData) {
/* 238:449 */         text.append("training data for evaluation of attributes.");
/* 239:    */       } else {
/* 240:451 */         text.append("" + getFolds() + " fold cross validation for evaluating " + "attributes.");
/* 241:    */       }
/* 242:454 */       text.append("\n\tMinimum bucket size for OneR: " + getMinimumBucketSize());
/* 243:    */     }
/* 244:458 */     text.append("\n");
/* 245:459 */     return text.toString();
/* 246:    */   }
/* 247:    */   
/* 248:    */   public String getRevision()
/* 249:    */   {
/* 250:469 */     return RevisionUtils.extract("$Revision: 11215 $");
/* 251:    */   }
/* 252:    */   
/* 253:    */   public int[] postProcess(int[] attributeSet)
/* 254:    */   {
/* 255:476 */     this.m_trainInstances = new Instances(this.m_trainInstances, 0);
/* 256:    */     
/* 257:478 */     return attributeSet;
/* 258:    */   }
/* 259:    */   
/* 260:    */   public static void main(String[] args)
/* 261:    */   {
/* 262:490 */     runEvaluator(new OneRAttributeEval(), args);
/* 263:    */   }
/* 264:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.OneRAttributeEval
 * JD-Core Version:    0.7.0.1
 */