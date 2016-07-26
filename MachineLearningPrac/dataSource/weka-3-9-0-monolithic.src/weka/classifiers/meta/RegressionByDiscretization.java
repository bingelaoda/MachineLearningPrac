/*   1:    */ package weka.classifiers.meta;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.classifiers.Classifier;
/*   9:    */ import weka.classifiers.ConditionalDensityEstimator;
/*  10:    */ import weka.classifiers.IntervalEstimator;
/*  11:    */ import weka.classifiers.SingleClassifierEnhancer;
/*  12:    */ import weka.classifiers.trees.J48;
/*  13:    */ import weka.core.Attribute;
/*  14:    */ import weka.core.Capabilities;
/*  15:    */ import weka.core.Capabilities.Capability;
/*  16:    */ import weka.core.Instance;
/*  17:    */ import weka.core.Instances;
/*  18:    */ import weka.core.Option;
/*  19:    */ import weka.core.OptionHandler;
/*  20:    */ import weka.core.RevisionUtils;
/*  21:    */ import weka.core.SerializedObject;
/*  22:    */ import weka.core.TechnicalInformation;
/*  23:    */ import weka.core.TechnicalInformation.Field;
/*  24:    */ import weka.core.TechnicalInformation.Type;
/*  25:    */ import weka.core.Utils;
/*  26:    */ import weka.estimators.UnivariateDensityEstimator;
/*  27:    */ import weka.estimators.UnivariateEqualFrequencyHistogramEstimator;
/*  28:    */ import weka.estimators.UnivariateIntervalEstimator;
/*  29:    */ import weka.estimators.UnivariateQuantileEstimator;
/*  30:    */ import weka.filters.Filter;
/*  31:    */ import weka.filters.unsupervised.attribute.Discretize;
/*  32:    */ 
/*  33:    */ public class RegressionByDiscretization
/*  34:    */   extends SingleClassifierEnhancer
/*  35:    */   implements IntervalEstimator, ConditionalDensityEstimator
/*  36:    */ {
/*  37:    */   static final long serialVersionUID = 5066426153134050378L;
/*  38:133 */   protected Discretize m_Discretizer = new Discretize();
/*  39:136 */   protected int m_NumBins = 10;
/*  40:    */   protected double[] m_ClassMeans;
/*  41:    */   protected int[] m_ClassCounts;
/*  42:    */   protected boolean m_DeleteEmptyBins;
/*  43:    */   protected int[] m_OldIndexToNewIndex;
/*  44:151 */   protected Instances m_DiscretizedHeader = null;
/*  45:154 */   protected boolean m_UseEqualFrequency = false;
/*  46:157 */   protected boolean m_MinimizeAbsoluteError = false;
/*  47:160 */   protected UnivariateDensityEstimator m_Estimator = new UnivariateEqualFrequencyHistogramEstimator();
/*  48:163 */   protected double[] m_OriginalTargetValues = null;
/*  49:166 */   protected int[] m_NewTargetValues = null;
/*  50:    */   
/*  51:    */   public String globalInfo()
/*  52:    */   {
/*  53:175 */     return "A regression scheme that employs any classifier on a copy of the data that has the class attribute discretized. The predicted value is the expected value of the mean class value for each discretized interval (based on the predicted probabilities for each interval). This class now also supports conditional density estimation by building a univariate density estimator from the target values in the training data, weighted by the class probabilities. \n\nFor more information on this process, see\n\n" + getTechnicalInformation().toString();
/*  54:    */   }
/*  55:    */   
/*  56:    */   public TechnicalInformation getTechnicalInformation()
/*  57:    */   {
/*  58:197 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  59:198 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Eibe Frank and Remco R. Bouckaert");
/*  60:199 */     result.setValue(TechnicalInformation.Field.TITLE, "Conditional Density Estimation with Class Probability Estimators");
/*  61:200 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "First Asian Conference on Machine Learning");
/*  62:201 */     result.setValue(TechnicalInformation.Field.YEAR, "2009");
/*  63:202 */     result.setValue(TechnicalInformation.Field.PAGES, "65-81");
/*  64:203 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Springer Verlag");
/*  65:204 */     result.setValue(TechnicalInformation.Field.ADDRESS, "Berlin");
/*  66:    */     
/*  67:206 */     return result;
/*  68:    */   }
/*  69:    */   
/*  70:    */   protected String defaultClassifierString()
/*  71:    */   {
/*  72:216 */     return "weka.classifiers.trees.J48";
/*  73:    */   }
/*  74:    */   
/*  75:    */   public RegressionByDiscretization()
/*  76:    */   {
/*  77:224 */     this.m_Classifier = new J48();
/*  78:    */   }
/*  79:    */   
/*  80:    */   public Capabilities getCapabilities()
/*  81:    */   {
/*  82:233 */     Capabilities result = super.getCapabilities();
/*  83:    */     
/*  84:    */ 
/*  85:236 */     result.disableAllClasses();
/*  86:237 */     result.disableAllClassDependencies();
/*  87:238 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*  88:239 */     result.enable(Capabilities.Capability.DATE_CLASS);
/*  89:    */     
/*  90:241 */     result.setMinimumNumberInstances(2);
/*  91:    */     
/*  92:243 */     return result;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public void buildClassifier(Instances instances)
/*  96:    */     throws Exception
/*  97:    */   {
/*  98:255 */     getCapabilities().testWithFail(instances);
/*  99:    */     
/* 100:    */ 
/* 101:258 */     instances = new Instances(instances);
/* 102:259 */     instances.deleteWithMissingClass();
/* 103:    */     
/* 104:    */ 
/* 105:262 */     this.m_Discretizer.setIgnoreClass(true);
/* 106:263 */     this.m_Discretizer.setAttributeIndices("" + (instances.classIndex() + 1));
/* 107:264 */     this.m_Discretizer.setBins(getNumBins());
/* 108:265 */     this.m_Discretizer.setUseEqualFrequency(getUseEqualFrequency());
/* 109:266 */     this.m_Discretizer.setInputFormat(instances);
/* 110:267 */     Instances newTrain = Filter.useFilter(instances, this.m_Discretizer);
/* 111:    */     
/* 112:    */ 
/* 113:270 */     this.m_OldIndexToNewIndex = null;
/* 114:271 */     if (this.m_DeleteEmptyBins)
/* 115:    */     {
/* 116:274 */       int numNonEmptyClasses = 0;
/* 117:275 */       boolean[] notEmptyClass = new boolean[newTrain.numClasses()];
/* 118:276 */       for (int i = 0; i < newTrain.numInstances(); i++) {
/* 119:277 */         if (notEmptyClass[((int)newTrain.instance(i).classValue())] == 0)
/* 120:    */         {
/* 121:278 */           numNonEmptyClasses++;
/* 122:279 */           notEmptyClass[((int)newTrain.instance(i).classValue())] = true;
/* 123:    */         }
/* 124:    */       }
/* 125:284 */       ArrayList<String> newClassVals = new ArrayList(numNonEmptyClasses);
/* 126:285 */       this.m_OldIndexToNewIndex = new int[newTrain.numClasses()];
/* 127:286 */       for (int i = 0; i < newTrain.numClasses(); i++) {
/* 128:287 */         if (notEmptyClass[i] != 0)
/* 129:    */         {
/* 130:288 */           this.m_OldIndexToNewIndex[i] = newClassVals.size();
/* 131:289 */           newClassVals.add(newTrain.classAttribute().value(i));
/* 132:    */         }
/* 133:    */       }
/* 134:294 */       Attribute newClass = new Attribute(newTrain.classAttribute().name(), newClassVals);
/* 135:    */       
/* 136:296 */       ArrayList<Attribute> newAttributes = new ArrayList(newTrain.numAttributes());
/* 137:297 */       for (int i = 0; i < newTrain.numAttributes(); i++) {
/* 138:298 */         if (i != newTrain.classIndex()) {
/* 139:299 */           newAttributes.add((Attribute)newTrain.attribute(i).copy());
/* 140:    */         } else {
/* 141:301 */           newAttributes.add(newClass);
/* 142:    */         }
/* 143:    */       }
/* 144:306 */       Instances newTrainTransformed = new Instances(newTrain.relationName(), newAttributes, newTrain.numInstances());
/* 145:    */       
/* 146:    */ 
/* 147:309 */       newTrainTransformed.setClassIndex(newTrain.classIndex());
/* 148:310 */       for (int i = 0; i < newTrain.numInstances(); i++)
/* 149:    */       {
/* 150:311 */         Instance inst = newTrain.instance(i);
/* 151:312 */         newTrainTransformed.add(inst);
/* 152:313 */         newTrainTransformed.lastInstance().setClassValue(this.m_OldIndexToNewIndex[((int)inst.classValue())]);
/* 153:    */       }
/* 154:316 */       newTrain = newTrainTransformed;
/* 155:    */     }
/* 156:320 */     this.m_OriginalTargetValues = new double[instances.numInstances()];
/* 157:321 */     this.m_NewTargetValues = new int[instances.numInstances()];
/* 158:322 */     for (int i = 0; i < this.m_OriginalTargetValues.length; i++)
/* 159:    */     {
/* 160:323 */       this.m_OriginalTargetValues[i] = instances.instance(i).classValue();
/* 161:324 */       this.m_NewTargetValues[i] = ((int)newTrain.instance(i).classValue());
/* 162:    */     }
/* 163:327 */     this.m_DiscretizedHeader = new Instances(newTrain, 0);
/* 164:    */     
/* 165:329 */     int numClasses = newTrain.numClasses();
/* 166:    */     
/* 167:    */ 
/* 168:332 */     this.m_ClassMeans = new double[numClasses];
/* 169:333 */     this.m_ClassCounts = new int[numClasses];
/* 170:334 */     for (int i = 0; i < instances.numInstances(); i++)
/* 171:    */     {
/* 172:335 */       Instance inst = newTrain.instance(i);
/* 173:336 */       if (!inst.classIsMissing())
/* 174:    */       {
/* 175:337 */         int classVal = (int)inst.classValue();
/* 176:338 */         this.m_ClassCounts[classVal] += 1;
/* 177:339 */         this.m_ClassMeans[classVal] += instances.instance(i).classValue();
/* 178:    */       }
/* 179:    */     }
/* 180:343 */     for (int i = 0; i < numClasses; i++) {
/* 181:344 */       if (this.m_ClassCounts[i] > 0) {
/* 182:345 */         this.m_ClassMeans[i] /= this.m_ClassCounts[i];
/* 183:    */       }
/* 184:    */     }
/* 185:349 */     if (this.m_Debug)
/* 186:    */     {
/* 187:350 */       System.out.println("Bin Means");
/* 188:351 */       System.out.println("==========");
/* 189:352 */       for (int i = 0; i < this.m_ClassMeans.length; i++) {
/* 190:353 */         System.out.println(this.m_ClassMeans[i]);
/* 191:    */       }
/* 192:355 */       System.out.println();
/* 193:    */     }
/* 194:359 */     this.m_Classifier.buildClassifier(newTrain);
/* 195:    */   }
/* 196:    */   
/* 197:    */   protected UnivariateDensityEstimator getDensityEstimator(Instance instance, boolean print)
/* 198:    */     throws Exception
/* 199:    */   {
/* 200:372 */     UnivariateDensityEstimator e = (UnivariateDensityEstimator)new SerializedObject(this.m_Estimator).getObject();
/* 201:374 */     if ((e instanceof UnivariateEqualFrequencyHistogramEstimator))
/* 202:    */     {
/* 203:377 */       ((UnivariateEqualFrequencyHistogramEstimator)e).setNumBins(getNumBins());
/* 204:380 */       for (int i = 0; i < this.m_OriginalTargetValues.length; i++) {
/* 205:381 */         e.addValue(this.m_OriginalTargetValues[i], 1.0D);
/* 206:    */       }
/* 207:385 */       ((UnivariateEqualFrequencyHistogramEstimator)e).initializeStatistics();
/* 208:    */       
/* 209:    */ 
/* 210:388 */       ((UnivariateEqualFrequencyHistogramEstimator)e).setUpdateWeightsOnly(true);
/* 211:    */     }
/* 212:392 */     this.m_Discretizer.input(instance);
/* 213:393 */     this.m_Discretizer.batchFinished();
/* 214:394 */     Instance newInstance = this.m_Discretizer.output();
/* 215:395 */     if (this.m_OldIndexToNewIndex != null) {
/* 216:396 */       newInstance.setClassValue(this.m_OldIndexToNewIndex[((int)newInstance.classValue())]);
/* 217:    */     }
/* 218:398 */     newInstance.setDataset(this.m_DiscretizedHeader);
/* 219:399 */     double[] probs = this.m_Classifier.distributionForInstance(newInstance);
/* 220:402 */     for (int i = 0; i < this.m_OriginalTargetValues.length; i++) {
/* 221:403 */       e.addValue(this.m_OriginalTargetValues[i], probs[this.m_NewTargetValues[i]] * this.m_OriginalTargetValues.length / this.m_ClassCounts[this.m_NewTargetValues[i]]);
/* 222:    */     }
/* 223:408 */     return e;
/* 224:    */   }
/* 225:    */   
/* 226:    */   public double[][] predictIntervals(Instance instance, double confidenceLevel)
/* 227:    */     throws Exception
/* 228:    */   {
/* 229:425 */     UnivariateIntervalEstimator e = (UnivariateIntervalEstimator)getDensityEstimator(instance, false);
/* 230:    */     
/* 231:    */ 
/* 232:428 */     return e.predictIntervals(confidenceLevel);
/* 233:    */   }
/* 234:    */   
/* 235:    */   public double logDensity(Instance instance, double value)
/* 236:    */     throws Exception
/* 237:    */   {
/* 238:442 */     UnivariateDensityEstimator e = getDensityEstimator(instance, true);
/* 239:    */     
/* 240:    */ 
/* 241:445 */     return e.logDensity(value);
/* 242:    */   }
/* 243:    */   
/* 244:    */   public double classifyInstance(Instance instance)
/* 245:    */     throws Exception
/* 246:    */   {
/* 247:458 */     this.m_Discretizer.input(instance);
/* 248:459 */     this.m_Discretizer.batchFinished();
/* 249:460 */     Instance newInstance = this.m_Discretizer.output();
/* 250:461 */     if (this.m_OldIndexToNewIndex != null) {
/* 251:462 */       newInstance.setClassValue(this.m_OldIndexToNewIndex[((int)newInstance.classValue())]);
/* 252:    */     }
/* 253:464 */     newInstance.setDataset(this.m_DiscretizedHeader);
/* 254:465 */     double[] probs = this.m_Classifier.distributionForInstance(newInstance);
/* 255:467 */     if (!this.m_MinimizeAbsoluteError)
/* 256:    */     {
/* 257:470 */       double prediction = 0.0D;double probSum = 0.0D;
/* 258:471 */       for (int j = 0; j < probs.length; j++)
/* 259:    */       {
/* 260:472 */         prediction += probs[j] * this.m_ClassMeans[j];
/* 261:473 */         probSum += probs[j];
/* 262:    */       }
/* 263:476 */       return prediction / probSum;
/* 264:    */     }
/* 265:480 */     UnivariateQuantileEstimator e = (UnivariateQuantileEstimator)getDensityEstimator(instance, true);
/* 266:    */     
/* 267:    */ 
/* 268:483 */     return e.predictQuantile(0.5D);
/* 269:    */   }
/* 270:    */   
/* 271:    */   public Enumeration<Option> listOptions()
/* 272:    */   {
/* 273:494 */     Vector<Option> newVector = new Vector(5);
/* 274:    */     
/* 275:496 */     newVector.addElement(new Option("\tNumber of bins for equal-width discretization\n\t(default 10).\n", "B", 1, "-B <int>"));
/* 276:    */     
/* 277:    */ 
/* 278:    */ 
/* 279:    */ 
/* 280:501 */     newVector.addElement(new Option("\tWhether to delete empty bins after discretization\n\t(default false).\n", "E", 0, "-E"));
/* 281:    */     
/* 282:    */ 
/* 283:    */ 
/* 284:    */ 
/* 285:506 */     newVector.addElement(new Option("\tWhether to minimize absolute error, rather than squared error.\n\t(default false).\n", "A", 0, "-A"));
/* 286:    */     
/* 287:    */ 
/* 288:    */ 
/* 289:    */ 
/* 290:511 */     newVector.addElement(new Option("\tUse equal-frequency instead of equal-width discretization.", "F", 0, "-F"));
/* 291:    */     
/* 292:    */ 
/* 293:    */ 
/* 294:515 */     newVector.addElement(new Option("\tThe density estimator to use (including parameters).", "K", 1, "-K <estimator name and parameters"));
/* 295:    */     
/* 296:    */ 
/* 297:    */ 
/* 298:519 */     newVector.addAll(Collections.list(super.listOptions()));
/* 299:    */     
/* 300:521 */     return newVector.elements();
/* 301:    */   }
/* 302:    */   
/* 303:    */   public void setOptions(String[] options)
/* 304:    */     throws Exception
/* 305:    */   {
/* 306:535 */     String binsString = Utils.getOption('B', options);
/* 307:536 */     if (binsString.length() != 0) {
/* 308:537 */       setNumBins(Integer.parseInt(binsString));
/* 309:    */     } else {
/* 310:539 */       setNumBins(10);
/* 311:    */     }
/* 312:542 */     setDeleteEmptyBins(Utils.getFlag('E', options));
/* 313:543 */     setUseEqualFrequency(Utils.getFlag('F', options));
/* 314:544 */     setMinimizeAbsoluteError(Utils.getFlag('A', options));
/* 315:    */     
/* 316:546 */     String tmpStr = Utils.getOption('K', options);
/* 317:547 */     String[] tmpOptions = Utils.splitOptions(tmpStr);
/* 318:548 */     if (tmpOptions.length != 0)
/* 319:    */     {
/* 320:549 */       tmpStr = tmpOptions[0];
/* 321:550 */       tmpOptions[0] = "";
/* 322:551 */       setEstimator((UnivariateDensityEstimator)Utils.forName(UnivariateDensityEstimator.class, tmpStr, tmpOptions));
/* 323:    */     }
/* 324:555 */     super.setOptions(options);
/* 325:    */     
/* 326:557 */     Utils.checkForRemainingOptions(options);
/* 327:    */   }
/* 328:    */   
/* 329:    */   public String[] getOptions()
/* 330:    */   {
/* 331:567 */     Vector<String> options = new Vector();
/* 332:    */     
/* 333:569 */     options.add("-B");
/* 334:570 */     options.add("" + getNumBins());
/* 335:572 */     if (getDeleteEmptyBins()) {
/* 336:573 */       options.add("-E");
/* 337:    */     }
/* 338:576 */     if (getUseEqualFrequency()) {
/* 339:577 */       options.add("-F");
/* 340:    */     }
/* 341:580 */     if (getMinimizeAbsoluteError()) {
/* 342:581 */       options.add("-A");
/* 343:    */     }
/* 344:584 */     options.add("-K");
/* 345:585 */     if ((getEstimator() instanceof OptionHandler)) {
/* 346:586 */       options.add("" + getEstimator().getClass().getName() + " " + Utils.joinOptions(((OptionHandler)getEstimator()).getOptions()));
/* 347:    */     } else {
/* 348:589 */       options.add("" + getEstimator().getClass().getName());
/* 349:    */     }
/* 350:592 */     Collections.addAll(options, super.getOptions());
/* 351:    */     
/* 352:594 */     return (String[])options.toArray(new String[0]);
/* 353:    */   }
/* 354:    */   
/* 355:    */   public String numBinsTipText()
/* 356:    */   {
/* 357:605 */     return "Number of bins for discretization.";
/* 358:    */   }
/* 359:    */   
/* 360:    */   public int getNumBins()
/* 361:    */   {
/* 362:615 */     return this.m_NumBins;
/* 363:    */   }
/* 364:    */   
/* 365:    */   public void setNumBins(int numBins)
/* 366:    */   {
/* 367:625 */     this.m_NumBins = numBins;
/* 368:    */   }
/* 369:    */   
/* 370:    */   public String deleteEmptyBinsTipText()
/* 371:    */   {
/* 372:637 */     return "Whether to delete empty bins after discretization.";
/* 373:    */   }
/* 374:    */   
/* 375:    */   public boolean getDeleteEmptyBins()
/* 376:    */   {
/* 377:648 */     return this.m_DeleteEmptyBins;
/* 378:    */   }
/* 379:    */   
/* 380:    */   public void setDeleteEmptyBins(boolean b)
/* 381:    */   {
/* 382:658 */     this.m_DeleteEmptyBins = b;
/* 383:    */   }
/* 384:    */   
/* 385:    */   public String minimizeAbsoluteErrorTipText()
/* 386:    */   {
/* 387:669 */     return "Whether to minimize absolute error.";
/* 388:    */   }
/* 389:    */   
/* 390:    */   public boolean getMinimizeAbsoluteError()
/* 391:    */   {
/* 392:680 */     return this.m_MinimizeAbsoluteError;
/* 393:    */   }
/* 394:    */   
/* 395:    */   public void setMinimizeAbsoluteError(boolean b)
/* 396:    */   {
/* 397:690 */     this.m_MinimizeAbsoluteError = b;
/* 398:    */   }
/* 399:    */   
/* 400:    */   public String useEqualFrequencyTipText()
/* 401:    */   {
/* 402:701 */     return "If set to true, equal-frequency binning will be used instead of equal-width binning.";
/* 403:    */   }
/* 404:    */   
/* 405:    */   public boolean getUseEqualFrequency()
/* 406:    */   {
/* 407:712 */     return this.m_UseEqualFrequency;
/* 408:    */   }
/* 409:    */   
/* 410:    */   public void setUseEqualFrequency(boolean newUseEqualFrequency)
/* 411:    */   {
/* 412:722 */     this.m_UseEqualFrequency = newUseEqualFrequency;
/* 413:    */   }
/* 414:    */   
/* 415:    */   public String estimatorTipText()
/* 416:    */   {
/* 417:733 */     return "The density estimator to use.";
/* 418:    */   }
/* 419:    */   
/* 420:    */   public UnivariateDensityEstimator getEstimator()
/* 421:    */   {
/* 422:743 */     return this.m_Estimator;
/* 423:    */   }
/* 424:    */   
/* 425:    */   public void setEstimator(UnivariateDensityEstimator estimator)
/* 426:    */   {
/* 427:753 */     this.m_Estimator = estimator;
/* 428:    */   }
/* 429:    */   
/* 430:    */   public String toString()
/* 431:    */   {
/* 432:763 */     StringBuffer text = new StringBuffer();
/* 433:    */     
/* 434:765 */     text.append("Regression by discretization");
/* 435:766 */     if (this.m_ClassMeans == null)
/* 436:    */     {
/* 437:767 */       text.append(": No model built yet.");
/* 438:    */     }
/* 439:    */     else
/* 440:    */     {
/* 441:769 */       text.append("\n\nClass attribute discretized into " + this.m_ClassMeans.length + " values\n");
/* 442:    */       
/* 443:    */ 
/* 444:772 */       text.append("\nClassifier spec: " + getClassifierSpec() + "\n");
/* 445:    */       
/* 446:774 */       text.append(this.m_Classifier.toString());
/* 447:    */     }
/* 448:776 */     return text.toString();
/* 449:    */   }
/* 450:    */   
/* 451:    */   public String getRevision()
/* 452:    */   {
/* 453:785 */     return RevisionUtils.extract("$Revision: 11326 $");
/* 454:    */   }
/* 455:    */   
/* 456:    */   public static void main(String[] argv)
/* 457:    */   {
/* 458:794 */     runClassifier(new RegressionByDiscretization(), argv);
/* 459:    */   }
/* 460:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.RegressionByDiscretization
 * JD-Core Version:    0.7.0.1
 */