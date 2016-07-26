/*   1:    */ package weka.classifiers.meta;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Random;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.classifiers.Classifier;
/*   9:    */ import weka.classifiers.RandomizableIteratedSingleClassifierEnhancer;
/*  10:    */ import weka.classifiers.rules.ZeroR;
/*  11:    */ import weka.classifiers.trees.DecisionStump;
/*  12:    */ import weka.core.Capabilities;
/*  13:    */ import weka.core.Capabilities.Capability;
/*  14:    */ import weka.core.Instance;
/*  15:    */ import weka.core.Instances;
/*  16:    */ import weka.core.Option;
/*  17:    */ import weka.core.Randomizable;
/*  18:    */ import weka.core.RevisionUtils;
/*  19:    */ import weka.core.TechnicalInformation;
/*  20:    */ import weka.core.TechnicalInformation.Field;
/*  21:    */ import weka.core.TechnicalInformation.Type;
/*  22:    */ import weka.core.TechnicalInformationHandler;
/*  23:    */ import weka.core.Utils;
/*  24:    */ import weka.core.WeightedInstancesHandler;
/*  25:    */ 
/*  26:    */ public class RealAdaBoost
/*  27:    */   extends RandomizableIteratedSingleClassifierEnhancer
/*  28:    */   implements WeightedInstancesHandler, TechnicalInformationHandler
/*  29:    */ {
/*  30:    */   static final long serialVersionUID = -7378109809933197974L;
/*  31:    */   protected int m_NumIterationsPerformed;
/*  32:133 */   protected int m_WeightThreshold = 100;
/*  33:136 */   protected double m_Shrinkage = 1.0D;
/*  34:    */   protected boolean m_UseResampling;
/*  35:    */   protected Classifier m_ZeroR;
/*  36:    */   protected double m_SumOfWeights;
/*  37:    */   
/*  38:    */   public RealAdaBoost()
/*  39:    */   {
/*  40:152 */     this.m_Classifier = new DecisionStump();
/*  41:    */   }
/*  42:    */   
/*  43:    */   public String globalInfo()
/*  44:    */   {
/*  45:163 */     return "Class for boosting a 2-class classifier using the Real Adaboost method.\n\nFor more information, see\n\n" + getTechnicalInformation().toString();
/*  46:    */   }
/*  47:    */   
/*  48:    */   public TechnicalInformation getTechnicalInformation()
/*  49:    */   {
/*  50:178 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  51:179 */     result.setValue(TechnicalInformation.Field.AUTHOR, "J. Friedman and T. Hastie and R. Tibshirani");
/*  52:    */     
/*  53:181 */     result.setValue(TechnicalInformation.Field.TITLE, "Additive Logistic Regression: a Statistical View of Boosting");
/*  54:    */     
/*  55:183 */     result.setValue(TechnicalInformation.Field.JOURNAL, "Annals of Statistics");
/*  56:184 */     result.setValue(TechnicalInformation.Field.VOLUME, "95");
/*  57:185 */     result.setValue(TechnicalInformation.Field.NUMBER, "2");
/*  58:186 */     result.setValue(TechnicalInformation.Field.PAGES, "337-407");
/*  59:187 */     result.setValue(TechnicalInformation.Field.YEAR, "2000");
/*  60:    */     
/*  61:189 */     return result;
/*  62:    */   }
/*  63:    */   
/*  64:    */   protected String defaultClassifierString()
/*  65:    */   {
/*  66:200 */     return "weka.classifiers.trees.DecisionStump";
/*  67:    */   }
/*  68:    */   
/*  69:    */   protected Instances selectWeightQuantile(Instances data, double quantile)
/*  70:    */   {
/*  71:214 */     int numInstances = data.numInstances();
/*  72:215 */     Instances trainData = new Instances(data, numInstances);
/*  73:216 */     double[] weights = new double[numInstances];
/*  74:    */     
/*  75:218 */     double sumOfWeights = 0.0D;
/*  76:219 */     for (int i = 0; i < numInstances; i++)
/*  77:    */     {
/*  78:220 */       weights[i] = data.instance(i).weight();
/*  79:221 */       sumOfWeights += weights[i];
/*  80:    */     }
/*  81:223 */     double weightMassToSelect = sumOfWeights * quantile;
/*  82:224 */     int[] sortedIndices = Utils.sort(weights);
/*  83:    */     
/*  84:    */ 
/*  85:227 */     sumOfWeights = 0.0D;
/*  86:228 */     for (int i = numInstances - 1; i >= 0; i--)
/*  87:    */     {
/*  88:229 */       Instance instance = (Instance)data.instance(sortedIndices[i]).copy();
/*  89:230 */       trainData.add(instance);
/*  90:231 */       sumOfWeights += weights[sortedIndices[i]];
/*  91:232 */       if ((sumOfWeights > weightMassToSelect) && (i > 0) && (weights[sortedIndices[i]] != weights[sortedIndices[(i - 1)]])) {
/*  92:    */         break;
/*  93:    */       }
/*  94:    */     }
/*  95:237 */     if (this.m_Debug) {
/*  96:238 */       System.err.println("Selected " + trainData.numInstances() + " out of " + numInstances);
/*  97:    */     }
/*  98:241 */     return trainData;
/*  99:    */   }
/* 100:    */   
/* 101:    */   public Enumeration<Option> listOptions()
/* 102:    */   {
/* 103:252 */     Vector<Option> newVector = new Vector();
/* 104:    */     
/* 105:254 */     newVector.addElement(new Option("\tPercentage of weight mass to base training on.\n\t(default 100, reduce to around 90 speed up)", "P", 1, "-P <num>"));
/* 106:    */     
/* 107:    */ 
/* 108:    */ 
/* 109:258 */     newVector.addElement(new Option("\tUse resampling for boosting.", "Q", 0, "-Q"));
/* 110:    */     
/* 111:    */ 
/* 112:261 */     newVector.addElement(new Option("\tShrinkage parameter.\n\t(default 1)", "H", 1, "-H <num>"));
/* 113:    */     
/* 114:    */ 
/* 115:264 */     newVector.addAll(Collections.list(super.listOptions()));
/* 116:    */     
/* 117:266 */     return newVector.elements();
/* 118:    */   }
/* 119:    */   
/* 120:    */   public void setOptions(String[] options)
/* 121:    */     throws Exception
/* 122:    */   {
/* 123:326 */     String thresholdString = Utils.getOption('P', options);
/* 124:327 */     if (thresholdString.length() != 0) {
/* 125:328 */       setWeightThreshold(Integer.parseInt(thresholdString));
/* 126:    */     } else {
/* 127:330 */       setWeightThreshold(100);
/* 128:    */     }
/* 129:333 */     String shrinkageString = Utils.getOption('H', options);
/* 130:334 */     if (shrinkageString.length() != 0) {
/* 131:335 */       setShrinkage(new Double(shrinkageString).doubleValue());
/* 132:    */     } else {
/* 133:337 */       setShrinkage(1.0D);
/* 134:    */     }
/* 135:340 */     setUseResampling(Utils.getFlag('Q', options));
/* 136:    */     
/* 137:342 */     super.setOptions(options);
/* 138:    */     
/* 139:344 */     Utils.checkForRemainingOptions(options);
/* 140:    */   }
/* 141:    */   
/* 142:    */   public String[] getOptions()
/* 143:    */   {
/* 144:355 */     Vector<String> result = new Vector();
/* 145:357 */     if (getUseResampling()) {
/* 146:358 */       result.add("-Q");
/* 147:    */     }
/* 148:361 */     result.add("-P");
/* 149:362 */     result.add("" + getWeightThreshold());
/* 150:    */     
/* 151:364 */     result.add("-H");
/* 152:365 */     result.add("" + getShrinkage());
/* 153:    */     
/* 154:367 */     Collections.addAll(result, super.getOptions());
/* 155:    */     
/* 156:369 */     return (String[])result.toArray(new String[result.size()]);
/* 157:    */   }
/* 158:    */   
/* 159:    */   public String shrinkageTipText()
/* 160:    */   {
/* 161:379 */     return "Shrinkage parameter (use small value like 0.1 to reduce overfitting).";
/* 162:    */   }
/* 163:    */   
/* 164:    */   public double getShrinkage()
/* 165:    */   {
/* 166:390 */     return this.m_Shrinkage;
/* 167:    */   }
/* 168:    */   
/* 169:    */   public void setShrinkage(double newShrinkage)
/* 170:    */   {
/* 171:400 */     this.m_Shrinkage = newShrinkage;
/* 172:    */   }
/* 173:    */   
/* 174:    */   public String weightThresholdTipText()
/* 175:    */   {
/* 176:410 */     return "Weight threshold for weight pruning.";
/* 177:    */   }
/* 178:    */   
/* 179:    */   public void setWeightThreshold(int threshold)
/* 180:    */   {
/* 181:420 */     this.m_WeightThreshold = threshold;
/* 182:    */   }
/* 183:    */   
/* 184:    */   public int getWeightThreshold()
/* 185:    */   {
/* 186:430 */     return this.m_WeightThreshold;
/* 187:    */   }
/* 188:    */   
/* 189:    */   public String useResamplingTipText()
/* 190:    */   {
/* 191:440 */     return "Whether resampling is used instead of reweighting.";
/* 192:    */   }
/* 193:    */   
/* 194:    */   public void setUseResampling(boolean r)
/* 195:    */   {
/* 196:450 */     this.m_UseResampling = r;
/* 197:    */   }
/* 198:    */   
/* 199:    */   public boolean getUseResampling()
/* 200:    */   {
/* 201:460 */     return this.m_UseResampling;
/* 202:    */   }
/* 203:    */   
/* 204:    */   public Capabilities getCapabilities()
/* 205:    */   {
/* 206:470 */     Capabilities result = super.getCapabilities();
/* 207:    */     
/* 208:    */ 
/* 209:473 */     result.disableAllClasses();
/* 210:474 */     result.disableAllClassDependencies();
/* 211:475 */     if (super.getCapabilities().handles(Capabilities.Capability.BINARY_CLASS)) {
/* 212:476 */       result.enable(Capabilities.Capability.BINARY_CLASS);
/* 213:    */     }
/* 214:479 */     return result;
/* 215:    */   }
/* 216:    */   
/* 217:    */   public void buildClassifier(Instances data)
/* 218:    */     throws Exception
/* 219:    */   {
/* 220:493 */     super.buildClassifier(data);
/* 221:    */     
/* 222:    */ 
/* 223:496 */     getCapabilities().testWithFail(data);
/* 224:    */     
/* 225:    */ 
/* 226:499 */     data = new Instances(data);
/* 227:500 */     data.deleteWithMissingClass();
/* 228:    */     
/* 229:502 */     this.m_SumOfWeights = data.sumOfWeights();
/* 230:504 */     if ((!this.m_UseResampling) && ((this.m_Classifier instanceof WeightedInstancesHandler))) {
/* 231:506 */       buildClassifierWithWeights(data);
/* 232:    */     } else {
/* 233:508 */       buildClassifierUsingResampling(data);
/* 234:    */     }
/* 235:    */   }
/* 236:    */   
/* 237:    */   protected void buildClassifierUsingResampling(Instances data)
/* 238:    */     throws Exception
/* 239:    */   {
/* 240:523 */     int numInstances = data.numInstances();
/* 241:524 */     Random randomInstance = new Random(this.m_Seed);
/* 242:525 */     double minLoss = 1.7976931348623157E+308D;
/* 243:    */     
/* 244:    */ 
/* 245:    */ 
/* 246:529 */     Instances trainingWeightsNotNormalized = new Instances(data, 0, numInstances);
/* 247:532 */     for (this.m_NumIterationsPerformed = -1; this.m_NumIterationsPerformed < this.m_Classifiers.length; this.m_NumIterationsPerformed += 1)
/* 248:    */     {
/* 249:533 */       if (this.m_Debug) {
/* 250:534 */         System.err.println("Training classifier " + (this.m_NumIterationsPerformed + 1));
/* 251:    */       }
/* 252:538 */       Instances training = new Instances(trainingWeightsNotNormalized);
/* 253:539 */       normalizeWeights(training, 1.0D);
/* 254:    */       Instances trainData;
/* 255:    */       Instances trainData;
/* 256:542 */       if (this.m_WeightThreshold < 100) {
/* 257:543 */         trainData = selectWeightQuantile(training, this.m_WeightThreshold / 100.0D);
/* 258:    */       } else {
/* 259:546 */         trainData = new Instances(training);
/* 260:    */       }
/* 261:550 */       double[] weights = new double[trainData.numInstances()];
/* 262:551 */       for (int i = 0; i < weights.length; i++) {
/* 263:552 */         weights[i] = trainData.instance(i).weight();
/* 264:    */       }
/* 265:555 */       Instances sample = trainData.resampleWithWeights(randomInstance, weights);
/* 266:558 */       if (this.m_NumIterationsPerformed == -1)
/* 267:    */       {
/* 268:559 */         this.m_ZeroR = new ZeroR();
/* 269:560 */         this.m_ZeroR.buildClassifier(data);
/* 270:    */       }
/* 271:    */       else
/* 272:    */       {
/* 273:562 */         this.m_Classifiers[this.m_NumIterationsPerformed].buildClassifier(sample);
/* 274:    */       }
/* 275:566 */       setWeights(trainingWeightsNotNormalized, this.m_NumIterationsPerformed);
/* 276:    */       
/* 277:    */ 
/* 278:569 */       double loss = 0.0D;
/* 279:570 */       for (Instance inst : trainingWeightsNotNormalized) {
/* 280:571 */         loss += Math.log(inst.weight());
/* 281:    */       }
/* 282:573 */       if (this.m_Debug) {
/* 283:574 */         System.err.println("Current loss on log scale: " + loss);
/* 284:    */       }
/* 285:576 */       if ((this.m_NumIterationsPerformed > -1) && (loss > minLoss))
/* 286:    */       {
/* 287:577 */         if (!this.m_Debug) {
/* 288:    */           break;
/* 289:    */         }
/* 290:578 */         System.err.println("Loss has increased: bailing out."); break;
/* 291:    */       }
/* 292:582 */       minLoss = loss;
/* 293:    */     }
/* 294:    */   }
/* 295:    */   
/* 296:    */   protected void setWeights(Instances training, int iteration)
/* 297:    */     throws Exception
/* 298:    */   {
/* 299:594 */     for (Instance instance : training)
/* 300:    */     {
/* 301:595 */       double reweight = 1.0D;
/* 302:596 */       double prob = 1.0D;double shrinkage = this.m_Shrinkage;
/* 303:598 */       if (iteration == -1)
/* 304:    */       {
/* 305:599 */         prob = this.m_ZeroR.distributionForInstance(instance)[0];
/* 306:600 */         shrinkage = 1.0D;
/* 307:    */       }
/* 308:    */       else
/* 309:    */       {
/* 310:602 */         prob = this.m_Classifiers[iteration].distributionForInstance(instance)[0];
/* 311:    */         
/* 312:    */ 
/* 313:605 */         prob = (this.m_SumOfWeights * prob + 1.0D) / (this.m_SumOfWeights + 2.0D);
/* 314:    */       }
/* 315:608 */       if (instance.classValue() == 1.0D) {
/* 316:609 */         reweight = shrinkage * 0.5D * (Math.log(prob) - Math.log(1.0D - prob));
/* 317:    */       } else {
/* 318:611 */         reweight = shrinkage * 0.5D * (Math.log(1.0D - prob) - Math.log(prob));
/* 319:    */       }
/* 320:613 */       instance.setWeight(instance.weight() * Math.exp(reweight));
/* 321:    */     }
/* 322:    */   }
/* 323:    */   
/* 324:    */   protected void normalizeWeights(Instances training, double oldSumOfWeights)
/* 325:    */     throws Exception
/* 326:    */   {
/* 327:627 */     double newSumOfWeights = training.sumOfWeights();
/* 328:628 */     for (Instance instance : training) {
/* 329:629 */       instance.setWeight(instance.weight() * oldSumOfWeights / newSumOfWeights);
/* 330:    */     }
/* 331:    */   }
/* 332:    */   
/* 333:    */   protected void buildClassifierWithWeights(Instances data)
/* 334:    */     throws Exception
/* 335:    */   {
/* 336:643 */     int numInstances = data.numInstances();
/* 337:644 */     Random randomInstance = new Random(this.m_Seed);
/* 338:645 */     double minLoss = 1.7976931348623157E+308D;
/* 339:    */     
/* 340:    */ 
/* 341:    */ 
/* 342:649 */     Instances trainingWeightsNotNormalized = new Instances(data, 0, numInstances);
/* 343:652 */     for (this.m_NumIterationsPerformed = -1; this.m_NumIterationsPerformed < this.m_Classifiers.length; this.m_NumIterationsPerformed += 1)
/* 344:    */     {
/* 345:653 */       if (this.m_Debug) {
/* 346:654 */         System.err.println("Training classifier " + (this.m_NumIterationsPerformed + 1));
/* 347:    */       }
/* 348:658 */       Instances training = new Instances(trainingWeightsNotNormalized);
/* 349:659 */       normalizeWeights(training, this.m_SumOfWeights);
/* 350:    */       Instances trainData;
/* 351:    */       Instances trainData;
/* 352:662 */       if (this.m_WeightThreshold < 100) {
/* 353:663 */         trainData = selectWeightQuantile(training, this.m_WeightThreshold / 100.0D);
/* 354:    */       } else {
/* 355:666 */         trainData = new Instances(training, 0, numInstances);
/* 356:    */       }
/* 357:670 */       if (this.m_NumIterationsPerformed == -1)
/* 358:    */       {
/* 359:671 */         this.m_ZeroR = new ZeroR();
/* 360:672 */         this.m_ZeroR.buildClassifier(data);
/* 361:    */       }
/* 362:    */       else
/* 363:    */       {
/* 364:674 */         if ((this.m_Classifiers[this.m_NumIterationsPerformed] instanceof Randomizable)) {
/* 365:675 */           ((Randomizable)this.m_Classifiers[this.m_NumIterationsPerformed]).setSeed(randomInstance.nextInt());
/* 366:    */         }
/* 367:678 */         this.m_Classifiers[this.m_NumIterationsPerformed].buildClassifier(trainData);
/* 368:    */       }
/* 369:682 */       setWeights(trainingWeightsNotNormalized, this.m_NumIterationsPerformed);
/* 370:    */       
/* 371:    */ 
/* 372:685 */       double loss = 0.0D;
/* 373:686 */       for (Instance inst : trainingWeightsNotNormalized) {
/* 374:687 */         loss += Math.log(inst.weight());
/* 375:    */       }
/* 376:689 */       if (this.m_Debug) {
/* 377:690 */         System.err.println("Current loss on log scale: " + loss);
/* 378:    */       }
/* 379:692 */       if ((this.m_NumIterationsPerformed > -1) && (loss > minLoss))
/* 380:    */       {
/* 381:693 */         if (!this.m_Debug) {
/* 382:    */           break;
/* 383:    */         }
/* 384:694 */         System.err.println("Loss has increased: bailing out."); break;
/* 385:    */       }
/* 386:698 */       minLoss = loss;
/* 387:    */     }
/* 388:    */   }
/* 389:    */   
/* 390:    */   public double[] distributionForInstance(Instance instance)
/* 391:    */     throws Exception
/* 392:    */   {
/* 393:712 */     double[] sums = new double[instance.numClasses()];
/* 394:713 */     for (int i = -1; i < this.m_NumIterationsPerformed; i++)
/* 395:    */     {
/* 396:714 */       double prob = 1.0D;double shrinkage = this.m_Shrinkage;
/* 397:715 */       if (i == -1)
/* 398:    */       {
/* 399:716 */         prob = this.m_ZeroR.distributionForInstance(instance)[0];
/* 400:717 */         shrinkage = 1.0D;
/* 401:    */       }
/* 402:    */       else
/* 403:    */       {
/* 404:719 */         prob = this.m_Classifiers[i].distributionForInstance(instance)[0];
/* 405:    */         
/* 406:    */ 
/* 407:722 */         prob = (this.m_SumOfWeights * prob + 1.0D) / (this.m_SumOfWeights + 2.0D);
/* 408:    */       }
/* 409:724 */       sums[0] += shrinkage * 0.5D * (Math.log(prob) - Math.log(1.0D - prob));
/* 410:    */     }
/* 411:726 */     sums[1] = (-sums[0]);
/* 412:727 */     return Utils.logs2probs(sums);
/* 413:    */   }
/* 414:    */   
/* 415:    */   public String toString()
/* 416:    */   {
/* 417:738 */     StringBuffer text = new StringBuffer();
/* 418:740 */     if (this.m_ZeroR == null)
/* 419:    */     {
/* 420:741 */       text.append("No model built yet.\n\n");
/* 421:    */     }
/* 422:    */     else
/* 423:    */     {
/* 424:743 */       text.append("RealAdaBoost: Base classifiers: \n\n");
/* 425:744 */       text.append(this.m_ZeroR.toString() + "\n\n");
/* 426:745 */       for (int i = 0; i < this.m_NumIterationsPerformed; i++) {
/* 427:746 */         text.append(this.m_Classifiers[i].toString() + "\n\n");
/* 428:    */       }
/* 429:748 */       text.append("Number of performed Iterations: " + this.m_NumIterationsPerformed + "\n");
/* 430:    */     }
/* 431:752 */     return text.toString();
/* 432:    */   }
/* 433:    */   
/* 434:    */   public String getRevision()
/* 435:    */   {
/* 436:762 */     return RevisionUtils.extract("$Revision: 10375 $");
/* 437:    */   }
/* 438:    */   
/* 439:    */   public static void main(String[] argv)
/* 440:    */   {
/* 441:771 */     runClassifier(new RealAdaBoost(), argv);
/* 442:    */   }
/* 443:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.RealAdaBoost
 * JD-Core Version:    0.7.0.1
 */