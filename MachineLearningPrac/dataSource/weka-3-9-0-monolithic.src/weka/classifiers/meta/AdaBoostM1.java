/*   1:    */ package weka.classifiers.meta;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Random;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.classifiers.Classifier;
/*   9:    */ import weka.classifiers.Evaluation;
/*  10:    */ import weka.classifiers.IterativeClassifier;
/*  11:    */ import weka.classifiers.RandomizableIteratedSingleClassifierEnhancer;
/*  12:    */ import weka.classifiers.Sourcable;
/*  13:    */ import weka.classifiers.rules.ZeroR;
/*  14:    */ import weka.classifiers.trees.DecisionStump;
/*  15:    */ import weka.core.Capabilities;
/*  16:    */ import weka.core.Capabilities.Capability;
/*  17:    */ import weka.core.Instance;
/*  18:    */ import weka.core.Instances;
/*  19:    */ import weka.core.Option;
/*  20:    */ import weka.core.Randomizable;
/*  21:    */ import weka.core.RevisionUtils;
/*  22:    */ import weka.core.TechnicalInformation;
/*  23:    */ import weka.core.TechnicalInformation.Field;
/*  24:    */ import weka.core.TechnicalInformation.Type;
/*  25:    */ import weka.core.TechnicalInformationHandler;
/*  26:    */ import weka.core.Utils;
/*  27:    */ import weka.core.WeightedInstancesHandler;
/*  28:    */ 
/*  29:    */ public class AdaBoostM1
/*  30:    */   extends RandomizableIteratedSingleClassifierEnhancer
/*  31:    */   implements WeightedInstancesHandler, Sourcable, TechnicalInformationHandler, IterativeClassifier
/*  32:    */ {
/*  33:    */   static final long serialVersionUID = -1178107808933117974L;
/*  34:141 */   private static int MAX_NUM_RESAMPLING_ITERATIONS = 10;
/*  35:    */   protected double[] m_Betas;
/*  36:    */   protected int m_NumIterationsPerformed;
/*  37:150 */   protected int m_WeightThreshold = 100;
/*  38:    */   protected boolean m_UseResampling;
/*  39:    */   protected int m_NumClasses;
/*  40:    */   protected Classifier m_ZeroR;
/*  41:    */   protected Instances m_TrainingData;
/*  42:    */   protected Random m_RandomInstance;
/*  43:    */   
/*  44:    */   public AdaBoostM1()
/*  45:    */   {
/*  46:172 */     this.m_Classifier = new DecisionStump();
/*  47:    */   }
/*  48:    */   
/*  49:    */   public String globalInfo()
/*  50:    */   {
/*  51:183 */     return "Class for boosting a nominal class classifier using the Adaboost M1 method. Only nominal class problems can be tackled. Often dramatically improves performance, but sometimes overfits.\n\nFor more information, see\n\n" + getTechnicalInformation().toString();
/*  52:    */   }
/*  53:    */   
/*  54:    */   public TechnicalInformation getTechnicalInformation()
/*  55:    */   {
/*  56:200 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  57:201 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Yoav Freund and Robert E. Schapire");
/*  58:202 */     result.setValue(TechnicalInformation.Field.TITLE, "Experiments with a new boosting algorithm");
/*  59:203 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Thirteenth International Conference on Machine Learning");
/*  60:    */     
/*  61:205 */     result.setValue(TechnicalInformation.Field.YEAR, "1996");
/*  62:206 */     result.setValue(TechnicalInformation.Field.PAGES, "148-156");
/*  63:207 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Morgan Kaufmann");
/*  64:208 */     result.setValue(TechnicalInformation.Field.ADDRESS, "San Francisco");
/*  65:    */     
/*  66:210 */     return result;
/*  67:    */   }
/*  68:    */   
/*  69:    */   protected String defaultClassifierString()
/*  70:    */   {
/*  71:221 */     return "weka.classifiers.trees.DecisionStump";
/*  72:    */   }
/*  73:    */   
/*  74:    */   protected Instances selectWeightQuantile(Instances data, double quantile)
/*  75:    */   {
/*  76:235 */     int numInstances = data.numInstances();
/*  77:236 */     Instances trainData = new Instances(data, numInstances);
/*  78:237 */     double[] weights = new double[numInstances];
/*  79:    */     
/*  80:239 */     double sumOfWeights = 0.0D;
/*  81:240 */     for (int i = 0; i < numInstances; i++)
/*  82:    */     {
/*  83:241 */       weights[i] = data.instance(i).weight();
/*  84:242 */       sumOfWeights += weights[i];
/*  85:    */     }
/*  86:244 */     double weightMassToSelect = sumOfWeights * quantile;
/*  87:245 */     int[] sortedIndices = Utils.sort(weights);
/*  88:    */     
/*  89:    */ 
/*  90:248 */     sumOfWeights = 0.0D;
/*  91:249 */     for (int i = numInstances - 1; i >= 0; i--)
/*  92:    */     {
/*  93:250 */       Instance instance = (Instance)data.instance(sortedIndices[i]).copy();
/*  94:251 */       trainData.add(instance);
/*  95:252 */       sumOfWeights += weights[sortedIndices[i]];
/*  96:253 */       if ((sumOfWeights > weightMassToSelect) && (i > 0) && (weights[sortedIndices[i]] != weights[sortedIndices[(i - 1)]])) {
/*  97:    */         break;
/*  98:    */       }
/*  99:    */     }
/* 100:258 */     if (this.m_Debug) {
/* 101:259 */       System.err.println("Selected " + trainData.numInstances() + " out of " + numInstances);
/* 102:    */     }
/* 103:262 */     return trainData;
/* 104:    */   }
/* 105:    */   
/* 106:    */   public Enumeration<Option> listOptions()
/* 107:    */   {
/* 108:273 */     Vector<Option> newVector = new Vector();
/* 109:    */     
/* 110:275 */     newVector.addElement(new Option("\tPercentage of weight mass to base training on.\n\t(default 100, reduce to around 90 speed up)", "P", 1, "-P <num>"));
/* 111:    */     
/* 112:    */ 
/* 113:    */ 
/* 114:279 */     newVector.addElement(new Option("\tUse resampling for boosting.", "Q", 0, "-Q"));
/* 115:    */     
/* 116:    */ 
/* 117:282 */     newVector.addAll(Collections.list(super.listOptions()));
/* 118:    */     
/* 119:284 */     return newVector.elements();
/* 120:    */   }
/* 121:    */   
/* 122:    */   public void setOptions(String[] options)
/* 123:    */     throws Exception
/* 124:    */   {
/* 125:350 */     String thresholdString = Utils.getOption('P', options);
/* 126:351 */     if (thresholdString.length() != 0) {
/* 127:352 */       setWeightThreshold(Integer.parseInt(thresholdString));
/* 128:    */     } else {
/* 129:354 */       setWeightThreshold(100);
/* 130:    */     }
/* 131:357 */     setUseResampling(Utils.getFlag('Q', options));
/* 132:    */     
/* 133:359 */     super.setOptions(options);
/* 134:    */     
/* 135:361 */     Utils.checkForRemainingOptions(options);
/* 136:    */   }
/* 137:    */   
/* 138:    */   public String[] getOptions()
/* 139:    */   {
/* 140:371 */     Vector<String> result = new Vector();
/* 141:373 */     if (getUseResampling()) {
/* 142:374 */       result.add("-Q");
/* 143:    */     }
/* 144:377 */     result.add("-P");
/* 145:378 */     result.add("" + getWeightThreshold());
/* 146:    */     
/* 147:380 */     Collections.addAll(result, super.getOptions());
/* 148:    */     
/* 149:382 */     return (String[])result.toArray(new String[result.size()]);
/* 150:    */   }
/* 151:    */   
/* 152:    */   public String weightThresholdTipText()
/* 153:    */   {
/* 154:392 */     return "Weight threshold for weight pruning.";
/* 155:    */   }
/* 156:    */   
/* 157:    */   public void setWeightThreshold(int threshold)
/* 158:    */   {
/* 159:402 */     this.m_WeightThreshold = threshold;
/* 160:    */   }
/* 161:    */   
/* 162:    */   public int getWeightThreshold()
/* 163:    */   {
/* 164:412 */     return this.m_WeightThreshold;
/* 165:    */   }
/* 166:    */   
/* 167:    */   public String useResamplingTipText()
/* 168:    */   {
/* 169:422 */     return "Whether resampling is used instead of reweighting.";
/* 170:    */   }
/* 171:    */   
/* 172:    */   public void setUseResampling(boolean r)
/* 173:    */   {
/* 174:432 */     this.m_UseResampling = r;
/* 175:    */   }
/* 176:    */   
/* 177:    */   public boolean getUseResampling()
/* 178:    */   {
/* 179:442 */     return this.m_UseResampling;
/* 180:    */   }
/* 181:    */   
/* 182:    */   public Capabilities getCapabilities()
/* 183:    */   {
/* 184:452 */     Capabilities result = super.getCapabilities();
/* 185:    */     
/* 186:    */ 
/* 187:455 */     result.disableAllClasses();
/* 188:456 */     result.disableAllClassDependencies();
/* 189:457 */     if (super.getCapabilities().handles(Capabilities.Capability.NOMINAL_CLASS)) {
/* 190:458 */       result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 191:    */     }
/* 192:460 */     if (super.getCapabilities().handles(Capabilities.Capability.BINARY_CLASS)) {
/* 193:461 */       result.enable(Capabilities.Capability.BINARY_CLASS);
/* 194:    */     }
/* 195:464 */     return result;
/* 196:    */   }
/* 197:    */   
/* 198:    */   public void buildClassifier(Instances data)
/* 199:    */     throws Exception
/* 200:    */   {
/* 201:473 */     initializeClassifier(data);
/* 202:476 */     while (next()) {}
/* 203:479 */     done();
/* 204:    */   }
/* 205:    */   
/* 206:    */   public void initializeClassifier(Instances data)
/* 207:    */     throws Exception
/* 208:    */   {
/* 209:491 */     super.buildClassifier(data);
/* 210:    */     
/* 211:    */ 
/* 212:494 */     getCapabilities().testWithFail(data);
/* 213:    */     
/* 214:    */ 
/* 215:497 */     data = new Instances(data);
/* 216:498 */     data.deleteWithMissingClass();
/* 217:    */     
/* 218:500 */     this.m_ZeroR = new ZeroR();
/* 219:501 */     this.m_ZeroR.buildClassifier(data);
/* 220:    */     
/* 221:503 */     this.m_NumClasses = data.numClasses();
/* 222:504 */     this.m_Betas = new double[this.m_Classifiers.length];
/* 223:505 */     this.m_NumIterationsPerformed = 0;
/* 224:506 */     this.m_TrainingData = new Instances(data);
/* 225:    */     
/* 226:508 */     this.m_RandomInstance = new Random(this.m_Seed);
/* 227:510 */     if ((this.m_UseResampling) || (!(this.m_Classifier instanceof WeightedInstancesHandler)))
/* 228:    */     {
/* 229:514 */       double sumProbs = this.m_TrainingData.sumOfWeights();
/* 230:515 */       for (int i = 0; i < this.m_TrainingData.numInstances(); i++) {
/* 231:516 */         this.m_TrainingData.instance(i).setWeight(this.m_TrainingData.instance(i).weight() / sumProbs);
/* 232:    */       }
/* 233:    */     }
/* 234:    */   }
/* 235:    */   
/* 236:    */   public boolean next()
/* 237:    */     throws Exception
/* 238:    */   {
/* 239:529 */     if (this.m_NumIterationsPerformed >= this.m_NumIterations) {
/* 240:530 */       return false;
/* 241:    */     }
/* 242:534 */     if (this.m_TrainingData.numAttributes() == 1) {
/* 243:535 */       return false;
/* 244:    */     }
/* 245:538 */     if (this.m_Debug) {
/* 246:539 */       System.err.println("Training classifier " + (this.m_NumIterationsPerformed + 1));
/* 247:    */     }
/* 248:544 */     Instances trainData = null;
/* 249:545 */     if (this.m_WeightThreshold < 100) {
/* 250:546 */       trainData = selectWeightQuantile(this.m_TrainingData, this.m_WeightThreshold / 100.0D);
/* 251:    */     } else {
/* 252:549 */       trainData = new Instances(this.m_TrainingData);
/* 253:    */     }
/* 254:552 */     double epsilon = 0.0D;
/* 255:553 */     if ((this.m_UseResampling) || (!(this.m_Classifier instanceof WeightedInstancesHandler)))
/* 256:    */     {
/* 257:557 */       int resamplingIterations = 0;
/* 258:558 */       double[] weights = new double[trainData.numInstances()];
/* 259:559 */       for (int i = 0; i < weights.length; i++) {
/* 260:560 */         weights[i] = trainData.instance(i).weight();
/* 261:    */       }
/* 262:    */       do
/* 263:    */       {
/* 264:563 */         Instances sample = trainData.resampleWithWeights(this.m_RandomInstance, weights);
/* 265:    */         
/* 266:    */ 
/* 267:566 */         this.m_Classifiers[this.m_NumIterationsPerformed].buildClassifier(sample);
/* 268:567 */         Evaluation evaluation = new Evaluation(this.m_TrainingData);
/* 269:568 */         evaluation.evaluateModel(this.m_Classifiers[this.m_NumIterationsPerformed], this.m_TrainingData, new Object[0]);
/* 270:    */         
/* 271:570 */         epsilon = evaluation.errorRate();
/* 272:571 */         resamplingIterations++;
/* 273:573 */       } while ((Utils.eq(epsilon, 0.0D)) && (resamplingIterations < MAX_NUM_RESAMPLING_ITERATIONS));
/* 274:    */     }
/* 275:    */     else
/* 276:    */     {
/* 277:577 */       if ((this.m_Classifiers[this.m_NumIterationsPerformed] instanceof Randomizable)) {
/* 278:578 */         ((Randomizable)this.m_Classifiers[this.m_NumIterationsPerformed]).setSeed(this.m_RandomInstance.nextInt());
/* 279:    */       }
/* 280:581 */       this.m_Classifiers[this.m_NumIterationsPerformed].buildClassifier(trainData);
/* 281:    */       
/* 282:    */ 
/* 283:584 */       Evaluation evaluation = new Evaluation(this.m_TrainingData);
/* 284:585 */       evaluation.evaluateModel(this.m_Classifiers[this.m_NumIterationsPerformed], this.m_TrainingData, new Object[0]);
/* 285:    */       
/* 286:587 */       epsilon = evaluation.errorRate();
/* 287:    */     }
/* 288:591 */     if ((Utils.grOrEq(epsilon, 0.5D)) || (Utils.eq(epsilon, 0.0D)))
/* 289:    */     {
/* 290:592 */       if (this.m_NumIterationsPerformed == 0) {
/* 291:593 */         this.m_NumIterationsPerformed = 1;
/* 292:    */       }
/* 293:595 */       return false;
/* 294:    */     }
/* 295:599 */     double reweight = (1.0D - epsilon) / epsilon;
/* 296:600 */     this.m_Betas[this.m_NumIterationsPerformed] = Math.log(reweight);
/* 297:601 */     if (this.m_Debug) {
/* 298:602 */       System.err.println("\terror rate = " + epsilon + "  beta = " + this.m_Betas[this.m_NumIterationsPerformed]);
/* 299:    */     }
/* 300:607 */     setWeights(this.m_TrainingData, reweight);
/* 301:    */     
/* 302:    */ 
/* 303:610 */     this.m_NumIterationsPerformed += 1;
/* 304:611 */     return true;
/* 305:    */   }
/* 306:    */   
/* 307:    */   public void done()
/* 308:    */   {
/* 309:619 */     this.m_TrainingData = null;
/* 310:622 */     if (this.m_NumIterationsPerformed > 0) {
/* 311:623 */       this.m_ZeroR = null;
/* 312:    */     }
/* 313:    */   }
/* 314:    */   
/* 315:    */   protected void setWeights(Instances training, double reweight)
/* 316:    */     throws Exception
/* 317:    */   {
/* 318:639 */     double oldSumOfWeights = training.sumOfWeights();
/* 319:640 */     Enumeration<Instance> enu = training.enumerateInstances();
/* 320:641 */     while (enu.hasMoreElements())
/* 321:    */     {
/* 322:642 */       Instance instance = (Instance)enu.nextElement();
/* 323:643 */       if (!Utils.eq(this.m_Classifiers[this.m_NumIterationsPerformed].classifyInstance(instance), instance.classValue())) {
/* 324:646 */         instance.setWeight(instance.weight() * reweight);
/* 325:    */       }
/* 326:    */     }
/* 327:651 */     double newSumOfWeights = training.sumOfWeights();
/* 328:652 */     enu = training.enumerateInstances();
/* 329:653 */     while (enu.hasMoreElements())
/* 330:    */     {
/* 331:654 */       Instance instance = (Instance)enu.nextElement();
/* 332:655 */       instance.setWeight(instance.weight() * oldSumOfWeights / newSumOfWeights);
/* 333:    */     }
/* 334:    */   }
/* 335:    */   
/* 336:    */   public double[] distributionForInstance(Instance instance)
/* 337:    */     throws Exception
/* 338:    */   {
/* 339:670 */     if (this.m_NumIterationsPerformed == 0) {
/* 340:671 */       return this.m_ZeroR.distributionForInstance(instance);
/* 341:    */     }
/* 342:674 */     if (this.m_NumIterationsPerformed == 0) {
/* 343:675 */       throw new Exception("No model built");
/* 344:    */     }
/* 345:677 */     double[] sums = new double[instance.numClasses()];
/* 346:679 */     if (this.m_NumIterationsPerformed == 1) {
/* 347:680 */       return this.m_Classifiers[0].distributionForInstance(instance);
/* 348:    */     }
/* 349:682 */     for (int i = 0; i < this.m_NumIterationsPerformed; i++) {
/* 350:683 */       sums[((int)this.m_Classifiers[i].classifyInstance(instance))] += this.m_Betas[i];
/* 351:    */     }
/* 352:685 */     return Utils.logs2probs(sums);
/* 353:    */   }
/* 354:    */   
/* 355:    */   public String toSource(String className)
/* 356:    */     throws Exception
/* 357:    */   {
/* 358:699 */     if (this.m_NumIterationsPerformed == 0) {
/* 359:700 */       throw new Exception("No model built yet");
/* 360:    */     }
/* 361:702 */     if (!(this.m_Classifiers[0] instanceof Sourcable)) {
/* 362:703 */       throw new Exception("Base learner " + this.m_Classifier.getClass().getName() + " is not Sourcable");
/* 363:    */     }
/* 364:707 */     StringBuffer text = new StringBuffer("class ");
/* 365:708 */     text.append(className).append(" {\n\n");
/* 366:    */     
/* 367:710 */     text.append("  public static double classify(Object[] i) {\n");
/* 368:712 */     if (this.m_NumIterationsPerformed == 1)
/* 369:    */     {
/* 370:713 */       text.append("    return " + className + "_0.classify(i);\n");
/* 371:    */     }
/* 372:    */     else
/* 373:    */     {
/* 374:715 */       text.append("    double [] sums = new double [" + this.m_NumClasses + "];\n");
/* 375:716 */       for (int i = 0; i < this.m_NumIterationsPerformed; i++) {
/* 376:717 */         text.append("    sums[(int) " + className + '_' + i + ".classify(i)] += " + this.m_Betas[i] + ";\n");
/* 377:    */       }
/* 378:720 */       text.append("    double maxV = sums[0];\n    int maxI = 0;\n    for (int j = 1; j < " + this.m_NumClasses + "; j++) {\n" + "      if (sums[j] > maxV) { maxV = sums[j]; maxI = j; }\n" + "    }\n    return (double) maxI;\n");
/* 379:    */     }
/* 380:725 */     text.append("  }\n}\n");
/* 381:727 */     for (int i = 0; i < this.m_Classifiers.length; i++) {
/* 382:728 */       text.append(((Sourcable)this.m_Classifiers[i]).toSource(className + '_' + i));
/* 383:    */     }
/* 384:730 */     return text.toString();
/* 385:    */   }
/* 386:    */   
/* 387:    */   public String toString()
/* 388:    */   {
/* 389:742 */     if (this.m_NumIterationsPerformed == 0)
/* 390:    */     {
/* 391:743 */       StringBuffer buf = new StringBuffer();
/* 392:744 */       if (this.m_ZeroR == null)
/* 393:    */       {
/* 394:745 */         buf.append("AdaBoostM1: No model built yet.\n");
/* 395:    */       }
/* 396:    */       else
/* 397:    */       {
/* 398:747 */         buf.append(getClass().getName().replaceAll(".*\\.", "") + "\n");
/* 399:748 */         buf.append(getClass().getName().replaceAll(".*\\.", "").replaceAll(".", "=") + "\n\n");
/* 400:    */         
/* 401:    */ 
/* 402:751 */         buf.append("Warning: No model could be built, hence ZeroR model is used:\n\n");
/* 403:    */         
/* 404:753 */         buf.append(this.m_ZeroR.toString());
/* 405:    */       }
/* 406:755 */       return buf.toString();
/* 407:    */     }
/* 408:758 */     StringBuffer text = new StringBuffer();
/* 409:759 */     if (this.m_NumIterationsPerformed == 1)
/* 410:    */     {
/* 411:760 */       text.append("AdaBoostM1: No boosting possible, one classifier used!\n");
/* 412:761 */       text.append(this.m_Classifiers[0].toString() + "\n");
/* 413:    */     }
/* 414:    */     else
/* 415:    */     {
/* 416:763 */       text.append("AdaBoostM1: Base classifiers and their weights: \n\n");
/* 417:764 */       for (int i = 0; i < this.m_NumIterationsPerformed; i++)
/* 418:    */       {
/* 419:765 */         text.append(this.m_Classifiers[i].toString() + "\n\n");
/* 420:766 */         text.append("Weight: " + Utils.roundDouble(this.m_Betas[i], 2) + "\n\n");
/* 421:    */       }
/* 422:768 */       text.append("Number of performed Iterations: " + this.m_NumIterationsPerformed + "\n");
/* 423:    */     }
/* 424:772 */     return text.toString();
/* 425:    */   }
/* 426:    */   
/* 427:    */   public String getRevision()
/* 428:    */   {
/* 429:782 */     return RevisionUtils.extract("$Revision: 10969 $");
/* 430:    */   }
/* 431:    */   
/* 432:    */   public static void main(String[] argv)
/* 433:    */   {
/* 434:791 */     runClassifier(new AdaBoostM1(), argv);
/* 435:    */   }
/* 436:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.AdaBoostM1
 * JD-Core Version:    0.7.0.1
 */