/*   1:    */ package weka.classifiers.bayes;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.classifiers.AbstractClassifier;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.Capabilities;
/*   9:    */ import weka.core.Capabilities.Capability;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.Option;
/*  13:    */ import weka.core.OptionHandler;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.TechnicalInformation;
/*  16:    */ import weka.core.TechnicalInformation.Field;
/*  17:    */ import weka.core.TechnicalInformation.Type;
/*  18:    */ import weka.core.TechnicalInformationHandler;
/*  19:    */ import weka.core.Utils;
/*  20:    */ import weka.core.WeightedInstancesHandler;
/*  21:    */ 
/*  22:    */ public class ComplementNaiveBayes
/*  23:    */   extends AbstractClassifier
/*  24:    */   implements OptionHandler, WeightedInstancesHandler, TechnicalInformationHandler
/*  25:    */ {
/*  26:    */   static final long serialVersionUID = 7246302925903086397L;
/*  27:    */   private double[][] wordWeights;
/*  28:105 */   private double smoothingParameter = 1.0D;
/*  29:108 */   private boolean m_normalizeWordWeights = false;
/*  30:    */   private int numClasses;
/*  31:    */   private Instances header;
/*  32:    */   
/*  33:    */   public Enumeration<Option> listOptions()
/*  34:    */   {
/*  35:126 */     Vector<Option> newVector = new Vector(2);
/*  36:    */     
/*  37:128 */     newVector.addElement(new Option("\tNormalize the word weights for each class\n", "N", 0, "-N"));
/*  38:    */     
/*  39:130 */     newVector.addElement(new Option("\tSmoothing value to avoid zero WordGivenClass probabilities (default=1.0).\n", "S", 1, "-S"));
/*  40:    */     
/*  41:    */ 
/*  42:    */ 
/*  43:134 */     newVector.addAll(Collections.list(super.listOptions()));
/*  44:    */     
/*  45:136 */     return newVector.elements();
/*  46:    */   }
/*  47:    */   
/*  48:    */   public String[] getOptions()
/*  49:    */   {
/*  50:147 */     Vector<String> options = new Vector();
/*  51:149 */     if (getNormalizeWordWeights()) {
/*  52:150 */       options.add("-N");
/*  53:    */     }
/*  54:153 */     options.add("-S");
/*  55:154 */     options.add(Double.toString(this.smoothingParameter));
/*  56:    */     
/*  57:156 */     Collections.addAll(options, super.getOptions());
/*  58:    */     
/*  59:158 */     return (String[])options.toArray(new String[0]);
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void setOptions(String[] options)
/*  63:    */     throws Exception
/*  64:    */   {
/*  65:186 */     setNormalizeWordWeights(Utils.getFlag('N', options));
/*  66:    */     
/*  67:188 */     String val = Utils.getOption('S', options);
/*  68:189 */     if (val.length() != 0) {
/*  69:190 */       setSmoothingParameter(Double.parseDouble(val));
/*  70:    */     } else {
/*  71:192 */       setSmoothingParameter(1.0D);
/*  72:    */     }
/*  73:195 */     super.setOptions(options);
/*  74:    */     
/*  75:197 */     Utils.checkForRemainingOptions(options);
/*  76:    */   }
/*  77:    */   
/*  78:    */   public boolean getNormalizeWordWeights()
/*  79:    */   {
/*  80:206 */     return this.m_normalizeWordWeights;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public void setNormalizeWordWeights(boolean doNormalize)
/*  84:    */   {
/*  85:215 */     this.m_normalizeWordWeights = doNormalize;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public String normalizeWordWeightsTipText()
/*  89:    */   {
/*  90:225 */     return "Normalizes the word weights for each class.";
/*  91:    */   }
/*  92:    */   
/*  93:    */   public double getSmoothingParameter()
/*  94:    */   {
/*  95:235 */     return this.smoothingParameter;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public void setSmoothingParameter(double val)
/*  99:    */   {
/* 100:244 */     this.smoothingParameter = val;
/* 101:    */   }
/* 102:    */   
/* 103:    */   public String smoothingParameterTipText()
/* 104:    */   {
/* 105:254 */     return "Sets the smoothing parameter to avoid zero WordGivenClass probabilities (default=1.0).";
/* 106:    */   }
/* 107:    */   
/* 108:    */   public String globalInfo()
/* 109:    */   {
/* 110:266 */     return "Class for building and using a Complement class Naive Bayes classifier.\n\nFor more information see, \n\n" + getTechnicalInformation().toString() + "\n\n" + "P.S.: TF, IDF and length normalization transforms, as " + "described in the paper, can be performed through " + "weka.filters.unsupervised.StringToWordVector.";
/* 111:    */   }
/* 112:    */   
/* 113:    */   public TechnicalInformation getTechnicalInformation()
/* 114:    */   {
/* 115:285 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/* 116:286 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Jason D. Rennie and Lawrence Shih and Jaime Teevan and David R. Karger");
/* 117:    */     
/* 118:288 */     result.setValue(TechnicalInformation.Field.TITLE, "Tackling the Poor Assumptions of Naive Bayes Text Classifiers");
/* 119:    */     
/* 120:290 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "ICML");
/* 121:291 */     result.setValue(TechnicalInformation.Field.YEAR, "2003");
/* 122:292 */     result.setValue(TechnicalInformation.Field.PAGES, "616-623");
/* 123:293 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "AAAI Press");
/* 124:    */     
/* 125:295 */     return result;
/* 126:    */   }
/* 127:    */   
/* 128:    */   public Capabilities getCapabilities()
/* 129:    */   {
/* 130:305 */     Capabilities result = super.getCapabilities();
/* 131:306 */     result.disableAll();
/* 132:    */     
/* 133:    */ 
/* 134:309 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 135:310 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 136:    */     
/* 137:    */ 
/* 138:313 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 139:314 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 140:    */     
/* 141:316 */     return result;
/* 142:    */   }
/* 143:    */   
/* 144:    */   public void buildClassifier(Instances instances)
/* 145:    */     throws Exception
/* 146:    */   {
/* 147:329 */     getCapabilities().testWithFail(instances);
/* 148:    */     
/* 149:    */ 
/* 150:332 */     instances = new Instances(instances);
/* 151:333 */     instances.deleteWithMissingClass();
/* 152:    */     
/* 153:335 */     this.numClasses = instances.numClasses();
/* 154:336 */     int numAttributes = instances.numAttributes();
/* 155:    */     
/* 156:338 */     this.header = new Instances(instances, 0);
/* 157:339 */     double[][] ocrnceOfWordInClass = new double[this.numClasses][numAttributes];
/* 158:340 */     this.wordWeights = new double[this.numClasses][numAttributes];
/* 159:    */     
/* 160:342 */     double[] wordsPerClass = new double[this.numClasses];
/* 161:343 */     double totalWordOccurrences = 0.0D;
/* 162:344 */     double sumOfSmoothingParams = (numAttributes - 1) * this.smoothingParameter;
/* 163:345 */     int classIndex = instances.instance(0).classIndex();
/* 164:    */     
/* 165:    */ 
/* 166:    */ 
/* 167:    */ 
/* 168:350 */     Enumeration<Instance> enumInsts = instances.enumerateInstances();
/* 169:351 */     while (enumInsts.hasMoreElements())
/* 170:    */     {
/* 171:352 */       Instance instance = (Instance)enumInsts.nextElement();
/* 172:353 */       int docClass = (int)instance.value(classIndex);
/* 173:356 */       for (int a = 0; a < instance.numValues(); a++) {
/* 174:357 */         if ((instance.index(a) != instance.classIndex()) && 
/* 175:358 */           (!instance.isMissing(a)))
/* 176:    */         {
/* 177:359 */           double numOccurrences = instance.valueSparse(a) * instance.weight();
/* 178:360 */           if (numOccurrences < 0.0D) {
/* 179:361 */             throw new Exception("Numeric attribute values must all be greater or equal to zero.");
/* 180:    */           }
/* 181:364 */           totalWordOccurrences += numOccurrences;
/* 182:365 */           wordsPerClass[docClass] += numOccurrences;
/* 183:366 */           ocrnceOfWordInClass[docClass][instance.index(a)] += numOccurrences;
/* 184:    */           
/* 185:    */ 
/* 186:    */ 
/* 187:370 */           this.wordWeights[0][instance.index(a)] += numOccurrences;
/* 188:    */         }
/* 189:    */       }
/* 190:    */     }
/* 191:377 */     for (int c = 1; c < this.numClasses; c++)
/* 192:    */     {
/* 193:379 */       double totalWordOcrnces = totalWordOccurrences - wordsPerClass[c];
/* 194:381 */       for (int w = 0; w < numAttributes; w++) {
/* 195:382 */         if (w != classIndex)
/* 196:    */         {
/* 197:384 */           double ocrncesOfWord = this.wordWeights[0][w] - ocrnceOfWordInClass[c][w];
/* 198:    */           
/* 199:386 */           this.wordWeights[c][w] = Math.log((ocrncesOfWord + this.smoothingParameter) / (totalWordOcrnces + sumOfSmoothingParams));
/* 200:    */         }
/* 201:    */       }
/* 202:    */     }
/* 203:393 */     for (int w = 0; w < numAttributes; w++) {
/* 204:394 */       if (w != classIndex)
/* 205:    */       {
/* 206:396 */         double ocrncesOfWord = this.wordWeights[0][w] - ocrnceOfWordInClass[0][w];
/* 207:    */         
/* 208:398 */         double totalWordOcrnces = totalWordOccurrences - wordsPerClass[0];
/* 209:    */         
/* 210:400 */         this.wordWeights[0][w] = Math.log((ocrncesOfWord + this.smoothingParameter) / (totalWordOcrnces + sumOfSmoothingParams));
/* 211:    */       }
/* 212:    */     }
/* 213:406 */     if (this.m_normalizeWordWeights == true) {
/* 214:407 */       for (int c = 0; c < this.numClasses; c++)
/* 215:    */       {
/* 216:408 */         double sum = 0.0D;
/* 217:409 */         for (int w = 0; w < numAttributes; w++) {
/* 218:410 */           if (w != classIndex) {
/* 219:411 */             sum += Math.abs(this.wordWeights[c][w]);
/* 220:    */           }
/* 221:    */         }
/* 222:414 */         for (int w = 0; w < numAttributes; w++) {
/* 223:415 */           if (w != classIndex) {
/* 224:416 */             this.wordWeights[c][w] /= sum;
/* 225:    */           }
/* 226:    */         }
/* 227:    */       }
/* 228:    */     }
/* 229:    */   }
/* 230:    */   
/* 231:    */   public double classifyInstance(Instance instance)
/* 232:    */     throws Exception
/* 233:    */   {
/* 234:445 */     if (this.wordWeights == null) {
/* 235:446 */       throw new Exception("Error. The classifier has not been built properly.");
/* 236:    */     }
/* 237:450 */     double[] valueForClass = new double[this.numClasses];
/* 238:451 */     for (int c = 0; c < this.numClasses; c++)
/* 239:    */     {
/* 240:452 */       double sumOfWordValues = 0.0D;
/* 241:453 */       for (int w = 0; w < instance.numValues(); w++) {
/* 242:454 */         if (instance.index(w) != instance.classIndex())
/* 243:    */         {
/* 244:455 */           double freqOfWordInDoc = instance.valueSparse(w);
/* 245:456 */           sumOfWordValues += freqOfWordInDoc * this.wordWeights[c][instance.index(w)];
/* 246:    */         }
/* 247:    */       }
/* 248:461 */       valueForClass[c] = sumOfWordValues;
/* 249:    */     }
/* 250:464 */     int minidx = 0;
/* 251:465 */     for (int i = 0; i < this.numClasses; i++) {
/* 252:466 */       if (valueForClass[i] < valueForClass[minidx]) {
/* 253:467 */         minidx = i;
/* 254:    */       }
/* 255:    */     }
/* 256:471 */     return minidx;
/* 257:    */   }
/* 258:    */   
/* 259:    */   public String toString()
/* 260:    */   {
/* 261:480 */     if (this.wordWeights == null) {
/* 262:481 */       return "The classifier hasn't been built yet.";
/* 263:    */     }
/* 264:484 */     int numAttributes = this.header.numAttributes();
/* 265:485 */     StringBuffer result = new StringBuffer("The word weights for each class are: \n------------------------------------\n\t");
/* 266:489 */     for (int c = 0; c < this.numClasses; c++) {
/* 267:490 */       result.append(this.header.classAttribute().value(c)).append("\t");
/* 268:    */     }
/* 269:493 */     result.append("\n");
/* 270:495 */     for (int w = 0; w < numAttributes; w++)
/* 271:    */     {
/* 272:496 */       result.append(this.header.attribute(w).name()).append("\t");
/* 273:497 */       for (int c = 0; c < this.numClasses; c++) {
/* 274:498 */         result.append(Double.toString(this.wordWeights[c][w])).append("\t");
/* 275:    */       }
/* 276:500 */       result.append("\n");
/* 277:    */     }
/* 278:503 */     return result.toString();
/* 279:    */   }
/* 280:    */   
/* 281:    */   public String getRevision()
/* 282:    */   {
/* 283:513 */     return RevisionUtils.extract("$Revision: 10334 $");
/* 284:    */   }
/* 285:    */   
/* 286:    */   public static void main(String[] argv)
/* 287:    */   {
/* 288:522 */     runClassifier(new ComplementNaiveBayes(), argv);
/* 289:    */   }
/* 290:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.ComplementNaiveBayes
 * JD-Core Version:    0.7.0.1
 */