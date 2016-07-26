/*    1:     */ package weka.classifiers.trees.lmt;
/*    2:     */ 
/*    3:     */ import weka.classifiers.AbstractClassifier;
/*    4:     */ import weka.classifiers.Evaluation;
/*    5:     */ import weka.core.Attribute;
/*    6:     */ import weka.core.DenseInstance;
/*    7:     */ import weka.core.Instance;
/*    8:     */ import weka.core.Instances;
/*    9:     */ import weka.core.RevisionUtils;
/*   10:     */ import weka.core.Utils;
/*   11:     */ import weka.core.WeightedInstancesHandler;
/*   12:     */ 
/*   13:     */ public class LogisticBase
/*   14:     */   extends AbstractClassifier
/*   15:     */   implements WeightedInstancesHandler
/*   16:     */ {
/*   17:     */   static final long serialVersionUID = 168765678097825064L;
/*   18:     */   protected Instances m_numericDataHeader;
/*   19:     */   protected Instances m_numericData;
/*   20:     */   protected Instances m_train;
/*   21:     */   protected boolean m_useCrossValidation;
/*   22:     */   protected boolean m_errorOnProbabilities;
/*   23:     */   protected int m_fixedNumIterations;
/*   24:  91 */   protected int m_heuristicStop = 50;
/*   25:  94 */   protected int m_numRegressions = 0;
/*   26:     */   protected int m_maxIterations;
/*   27:     */   protected int m_numClasses;
/*   28:     */   protected SimpleLinearRegression[][] m_regressions;
/*   29: 106 */   protected static int m_numFoldsBoosting = 5;
/*   30:     */   protected static final double Z_MAX = 3.0D;
/*   31: 112 */   private boolean m_useAIC = false;
/*   32: 115 */   protected double m_numParameters = 0.0D;
/*   33: 121 */   protected double m_weightTrimBeta = 0.0D;
/*   34:     */   
/*   35:     */   public LogisticBase()
/*   36:     */   {
/*   37: 127 */     this.m_fixedNumIterations = -1;
/*   38: 128 */     this.m_useCrossValidation = true;
/*   39: 129 */     this.m_errorOnProbabilities = false;
/*   40: 130 */     this.m_maxIterations = 500;
/*   41: 131 */     this.m_useAIC = false;
/*   42: 132 */     this.m_numParameters = 0.0D;
/*   43: 133 */     this.m_numDecimalPlaces = 2;
/*   44:     */   }
/*   45:     */   
/*   46:     */   public LogisticBase(int numBoostingIterations, boolean useCrossValidation, boolean errorOnProbabilities)
/*   47:     */   {
/*   48: 149 */     this.m_fixedNumIterations = numBoostingIterations;
/*   49: 150 */     this.m_useCrossValidation = useCrossValidation;
/*   50: 151 */     this.m_errorOnProbabilities = errorOnProbabilities;
/*   51: 152 */     this.m_maxIterations = 500;
/*   52: 153 */     this.m_useAIC = false;
/*   53: 154 */     this.m_numParameters = 0.0D;
/*   54: 155 */     this.m_numDecimalPlaces = 2;
/*   55:     */   }
/*   56:     */   
/*   57:     */   public void buildClassifier(Instances data)
/*   58:     */     throws Exception
/*   59:     */   {
/*   60: 167 */     this.m_train = new Instances(data);
/*   61:     */     
/*   62: 169 */     this.m_numClasses = this.m_train.numClasses();
/*   63:     */     
/*   64:     */ 
/*   65:     */ 
/*   66: 173 */     this.m_numericData = getNumericData(this.m_train);
/*   67:     */     
/*   68:     */ 
/*   69: 176 */     this.m_regressions = initRegressions();
/*   70: 177 */     this.m_numRegressions = 0;
/*   71: 179 */     if (this.m_fixedNumIterations > 0) {
/*   72: 181 */       performBoosting(this.m_fixedNumIterations);
/*   73: 182 */     } else if (this.m_useAIC) {
/*   74: 185 */       performBoostingInfCriterion();
/*   75: 186 */     } else if (this.m_useCrossValidation) {
/*   76: 188 */       performBoostingCV();
/*   77:     */     } else {
/*   78: 192 */       performBoosting();
/*   79:     */     }
/*   80: 196 */     cleanup();
/*   81:     */   }
/*   82:     */   
/*   83:     */   protected void performBoostingCV()
/*   84:     */     throws Exception
/*   85:     */   {
/*   86: 211 */     int completedIterations = this.m_maxIterations;
/*   87:     */     
/*   88: 213 */     Instances allData = new Instances(this.m_train);
/*   89:     */     
/*   90: 215 */     allData.stratify(m_numFoldsBoosting);
/*   91:     */     
/*   92: 217 */     double[] error = new double[this.m_maxIterations + 1];
/*   93:     */     
/*   94: 219 */     SimpleLinearRegression[][] backup = this.m_regressions;
/*   95: 221 */     for (int i = 0; i < m_numFoldsBoosting; i++)
/*   96:     */     {
/*   97: 223 */       Instances train = allData.trainCV(m_numFoldsBoosting, i);
/*   98: 224 */       Instances test = allData.testCV(m_numFoldsBoosting, i);
/*   99:     */       
/*  100:     */ 
/*  101: 227 */       this.m_numRegressions = 0;
/*  102: 228 */       this.m_regressions = copyRegressions(backup);
/*  103:     */       
/*  104:     */ 
/*  105: 231 */       int iterations = performBoosting(train, test, error, completedIterations);
/*  106: 232 */       if (iterations < completedIterations) {
/*  107: 233 */         completedIterations = iterations;
/*  108:     */       }
/*  109:     */     }
/*  110: 238 */     int bestIteration = getBestIteration(error, completedIterations);
/*  111:     */     
/*  112:     */ 
/*  113: 241 */     this.m_numRegressions = 0;
/*  114: 242 */     this.m_regressions = backup;
/*  115: 243 */     performBoosting(bestIteration);
/*  116:     */   }
/*  117:     */   
/*  118:     */   protected SimpleLinearRegression[][] copyRegressions(SimpleLinearRegression[][] a)
/*  119:     */     throws Exception
/*  120:     */   {
/*  121: 256 */     SimpleLinearRegression[][] result = initRegressions();
/*  122: 257 */     for (int i = 0; i < a.length; i++) {
/*  123: 258 */       for (int j = 0; j < a[i].length; j++) {
/*  124: 259 */         if (j != this.m_numericDataHeader.classIndex()) {
/*  125: 260 */           result[i][j].addModel(a[i][j]);
/*  126:     */         }
/*  127:     */       }
/*  128:     */     }
/*  129: 264 */     return result;
/*  130:     */   }
/*  131:     */   
/*  132:     */   protected void performBoostingInfCriterion()
/*  133:     */     throws Exception
/*  134:     */   {
/*  135: 273 */     double bestCriterion = 1.7976931348623157E+308D;
/*  136: 274 */     int bestIteration = 0;
/*  137: 275 */     int noMin = 0;
/*  138:     */     
/*  139:     */ 
/*  140: 278 */     double criterionValue = 1.7976931348623157E+308D;
/*  141:     */     
/*  142:     */ 
/*  143: 281 */     double[][] trainYs = getYs(this.m_train);
/*  144: 282 */     double[][] trainFs = getFs(this.m_numericData);
/*  145: 283 */     double[][] probs = getProbs(trainFs);
/*  146:     */     
/*  147: 285 */     int iteration = 0;
/*  148: 286 */     while (iteration < this.m_maxIterations)
/*  149:     */     {
/*  150: 289 */       boolean foundAttribute = performIteration(iteration, trainYs, trainFs, probs, this.m_numericData);
/*  151: 291 */       if (!foundAttribute) {
/*  152:     */         break;
/*  153:     */       }
/*  154: 292 */       iteration++;
/*  155: 293 */       this.m_numRegressions = iteration;
/*  156:     */       
/*  157:     */ 
/*  158:     */ 
/*  159:     */ 
/*  160:     */ 
/*  161: 299 */       double numberOfAttributes = this.m_numParameters + iteration;
/*  162:     */       
/*  163:     */ 
/*  164: 302 */       criterionValue = 2.0D * negativeLogLikelihood(trainYs, probs) + 2.0D * numberOfAttributes;
/*  165: 307 */       if (noMin > this.m_heuristicStop) {
/*  166:     */         break;
/*  167:     */       }
/*  168: 310 */       if (criterionValue < bestCriterion)
/*  169:     */       {
/*  170: 311 */         bestCriterion = criterionValue;
/*  171: 312 */         bestIteration = iteration;
/*  172: 313 */         noMin = 0;
/*  173:     */       }
/*  174:     */       else
/*  175:     */       {
/*  176: 315 */         noMin++;
/*  177:     */       }
/*  178:     */     }
/*  179: 319 */     this.m_numRegressions = 0;
/*  180: 320 */     this.m_regressions = initRegressions();
/*  181: 321 */     performBoosting(bestIteration);
/*  182:     */   }
/*  183:     */   
/*  184:     */   protected int performBoosting(Instances train, Instances test, double[] error, int maxIterations)
/*  185:     */     throws Exception
/*  186:     */   {
/*  187: 342 */     Instances numericTrain = getNumericData(train);
/*  188:     */     
/*  189:     */ 
/*  190: 345 */     double[][] trainYs = getYs(train);
/*  191: 346 */     double[][] trainFs = getFs(numericTrain);
/*  192: 347 */     double[][] probs = getProbs(trainFs);
/*  193:     */     
/*  194: 349 */     int iteration = 0;
/*  195:     */     
/*  196: 351 */     int noMin = 0;
/*  197: 352 */     double lastMin = 1.7976931348623157E+308D;
/*  198: 354 */     if (this.m_errorOnProbabilities) {
/*  199: 355 */       error[0] += getMeanAbsoluteError(test);
/*  200:     */     } else {
/*  201: 357 */       error[0] += getErrorRate(test);
/*  202:     */     }
/*  203: 360 */     while (iteration < maxIterations)
/*  204:     */     {
/*  205: 363 */       boolean foundAttribute = performIteration(iteration, trainYs, trainFs, probs, numericTrain);
/*  206: 365 */       if (!foundAttribute) {
/*  207:     */         break;
/*  208:     */       }
/*  209: 366 */       iteration++;
/*  210: 367 */       this.m_numRegressions = iteration;
/*  211: 373 */       if (this.m_errorOnProbabilities) {
/*  212: 374 */         error[iteration] += getMeanAbsoluteError(test);
/*  213:     */       } else {
/*  214: 376 */         error[iteration] += getErrorRate(test);
/*  215:     */       }
/*  216: 381 */       if (noMin > this.m_heuristicStop) {
/*  217:     */         break;
/*  218:     */       }
/*  219: 384 */       if (error[iteration] < lastMin)
/*  220:     */       {
/*  221: 385 */         lastMin = error[iteration];
/*  222: 386 */         noMin = 0;
/*  223:     */       }
/*  224:     */       else
/*  225:     */       {
/*  226: 388 */         noMin++;
/*  227:     */       }
/*  228:     */     }
/*  229: 392 */     return iteration;
/*  230:     */   }
/*  231:     */   
/*  232:     */   protected void performBoosting(int numIterations)
/*  233:     */     throws Exception
/*  234:     */   {
/*  235: 404 */     double[][] trainYs = getYs(this.m_train);
/*  236: 405 */     double[][] trainFs = getFs(this.m_numericData);
/*  237: 406 */     double[][] probs = getProbs(trainFs);
/*  238:     */     
/*  239: 408 */     int iteration = 0;
/*  240: 411 */     while (iteration < numIterations)
/*  241:     */     {
/*  242: 412 */       boolean foundAttribute = performIteration(iteration, trainYs, trainFs, probs, this.m_numericData);
/*  243: 414 */       if (!foundAttribute) {
/*  244:     */         break;
/*  245:     */       }
/*  246: 415 */       iteration++;
/*  247:     */     }
/*  248: 421 */     this.m_numRegressions = iteration;
/*  249:     */   }
/*  250:     */   
/*  251:     */   protected void performBoosting()
/*  252:     */     throws Exception
/*  253:     */   {
/*  254: 435 */     double[][] trainYs = getYs(this.m_train);
/*  255: 436 */     double[][] trainFs = getFs(this.m_numericData);
/*  256: 437 */     double[][] probs = getProbs(trainFs);
/*  257:     */     
/*  258: 439 */     int iteration = 0;
/*  259:     */     
/*  260: 441 */     double[] trainErrors = new double[this.m_maxIterations + 1];
/*  261: 442 */     trainErrors[0] = getErrorRate(this.m_train);
/*  262:     */     
/*  263: 444 */     int noMin = 0;
/*  264: 445 */     double lastMin = 1.7976931348623157E+308D;
/*  265: 447 */     while (iteration < this.m_maxIterations)
/*  266:     */     {
/*  267: 448 */       boolean foundAttribute = performIteration(iteration, trainYs, trainFs, probs, this.m_numericData);
/*  268: 450 */       if (!foundAttribute) {
/*  269:     */         break;
/*  270:     */       }
/*  271: 451 */       iteration++;
/*  272: 452 */       this.m_numRegressions = iteration;
/*  273:     */       
/*  274:     */ 
/*  275:     */ 
/*  276:     */ 
/*  277:     */ 
/*  278: 458 */       trainErrors[iteration] = getErrorRate(this.m_train);
/*  279: 462 */       if (noMin > this.m_heuristicStop) {
/*  280:     */         break;
/*  281:     */       }
/*  282: 465 */       if (trainErrors[iteration] < lastMin)
/*  283:     */       {
/*  284: 466 */         lastMin = trainErrors[iteration];
/*  285: 467 */         noMin = 0;
/*  286:     */       }
/*  287:     */       else
/*  288:     */       {
/*  289: 469 */         noMin++;
/*  290:     */       }
/*  291:     */     }
/*  292: 474 */     int bestIteration = getBestIteration(trainErrors, iteration);
/*  293: 475 */     this.m_numRegressions = 0;
/*  294: 476 */     this.m_regressions = initRegressions();
/*  295: 477 */     performBoosting(bestIteration);
/*  296:     */   }
/*  297:     */   
/*  298:     */   protected double getErrorRate(Instances data)
/*  299:     */     throws Exception
/*  300:     */   {
/*  301: 489 */     Evaluation eval = new Evaluation(data);
/*  302: 490 */     eval.evaluateModel(this, data, new Object[0]);
/*  303: 491 */     return eval.errorRate();
/*  304:     */   }
/*  305:     */   
/*  306:     */   protected double getMeanAbsoluteError(Instances data)
/*  307:     */     throws Exception
/*  308:     */   {
/*  309: 503 */     Evaluation eval = new Evaluation(data);
/*  310: 504 */     eval.evaluateModel(this, data, new Object[0]);
/*  311: 505 */     return eval.meanAbsoluteError();
/*  312:     */   }
/*  313:     */   
/*  314:     */   protected int getBestIteration(double[] errors, int maxIteration)
/*  315:     */   {
/*  316: 516 */     double bestError = errors[0];
/*  317: 517 */     int bestIteration = 0;
/*  318: 518 */     for (int i = 1; i <= maxIteration; i++) {
/*  319: 519 */       if (errors[i] < bestError)
/*  320:     */       {
/*  321: 520 */         bestError = errors[i];
/*  322: 521 */         bestIteration = i;
/*  323:     */       }
/*  324:     */     }
/*  325: 524 */     return bestIteration;
/*  326:     */   }
/*  327:     */   
/*  328:     */   protected boolean performIteration(int iteration, double[][] trainYs, double[][] trainFs, double[][] probs, Instances trainNumeric)
/*  329:     */     throws Exception
/*  330:     */   {
/*  331: 548 */     SimpleLinearRegression[] linearRegressionForEachClass = new SimpleLinearRegression[this.m_numClasses];
/*  332:     */     
/*  333:     */ 
/*  334: 551 */     double[] oldWeights = new double[trainNumeric.numInstances()];
/*  335: 552 */     for (int i = 0; i < oldWeights.length; i++) {
/*  336: 553 */       oldWeights[i] = trainNumeric.instance(i).weight();
/*  337:     */     }
/*  338: 556 */     for (int j = 0; j < this.m_numClasses; j++)
/*  339:     */     {
/*  340: 558 */       double weightSum = 0.0D;
/*  341: 560 */       for (int i = 0; i < trainNumeric.numInstances(); i++)
/*  342:     */       {
/*  343: 563 */         double p = probs[i][j];
/*  344: 564 */         double actual = trainYs[i][j];
/*  345: 565 */         double z = getZ(actual, p);
/*  346: 566 */         double w = (actual - p) / z;
/*  347:     */         
/*  348:     */ 
/*  349: 569 */         Instance current = trainNumeric.instance(i);
/*  350: 570 */         current.setValue(trainNumeric.classIndex(), z);
/*  351: 571 */         current.setWeight(oldWeights[i] * w);
/*  352:     */         
/*  353: 573 */         weightSum += current.weight();
/*  354:     */       }
/*  355: 576 */       Instances instancesCopy = trainNumeric;
/*  356:     */       double multiplier;
/*  357: 578 */       if (weightSum > 0.0D)
/*  358:     */       {
/*  359: 582 */         if (this.m_weightTrimBeta > 0.0D)
/*  360:     */         {
/*  361: 585 */           instancesCopy = new Instances(trainNumeric, trainNumeric.numInstances());
/*  362:     */           
/*  363:     */ 
/*  364:     */ 
/*  365: 589 */           double[] weights = new double[oldWeights.length];
/*  366: 590 */           for (int i = 0; i < oldWeights.length; i++) {
/*  367: 591 */             weights[i] = trainNumeric.instance(i).weight();
/*  368:     */           }
/*  369: 594 */           double weightPercentage = 0.0D;
/*  370: 595 */           int[] weightsOrder = Utils.sort(weights);
/*  371: 597 */           for (int i = weightsOrder.length - 1; (i >= 0) && (weightPercentage < 1.0D - this.m_weightTrimBeta); i--)
/*  372:     */           {
/*  373: 599 */             instancesCopy.add(trainNumeric.instance(weightsOrder[i]));
/*  374: 600 */             weightPercentage += weights[weightsOrder[i]] / weightSum;
/*  375:     */           }
/*  376: 605 */           weightSum = instancesCopy.sumOfWeights();
/*  377:     */         }
/*  378: 609 */         multiplier = instancesCopy.numInstances() / weightSum;
/*  379: 610 */         for (Instance current : instancesCopy) {
/*  380: 611 */           current.setWeight(current.weight() * multiplier);
/*  381:     */         }
/*  382:     */       }
/*  383: 616 */       linearRegressionForEachClass[j] = new SimpleLinearRegression();
/*  384: 617 */       linearRegressionForEachClass[j].buildClassifier(instancesCopy);
/*  385:     */       
/*  386: 619 */       boolean foundAttribute = linearRegressionForEachClass[j].foundUsefulAttribute();
/*  387: 621 */       if (!foundAttribute)
/*  388:     */       {
/*  389: 625 */         for (int i = 0; i < oldWeights.length; i++) {
/*  390: 626 */           trainNumeric.instance(i).setWeight(oldWeights[i]);
/*  391:     */         }
/*  392: 628 */         return false;
/*  393:     */       }
/*  394:     */     }
/*  395: 633 */     for (int i = 0; i < this.m_numClasses; i++) {
/*  396: 634 */       this.m_regressions[i][linearRegressionForEachClass[i].getAttributeIndex()].addModel(linearRegressionForEachClass[i]);
/*  397:     */     }
/*  398: 639 */     for (int i = 0; i < trainFs.length; i++)
/*  399:     */     {
/*  400: 640 */       double[] pred = new double[this.m_numClasses];
/*  401: 641 */       double predSum = 0.0D;
/*  402: 642 */       for (int j = 0; j < this.m_numClasses; j++)
/*  403:     */       {
/*  404: 643 */         pred[j] = linearRegressionForEachClass[j].classifyInstance(trainNumeric.instance(i));
/*  405:     */         
/*  406: 645 */         predSum += pred[j];
/*  407:     */       }
/*  408: 647 */       predSum /= this.m_numClasses;
/*  409: 648 */       for (int j = 0; j < this.m_numClasses; j++) {
/*  410: 649 */         trainFs[i][j] += (pred[j] - predSum) * (this.m_numClasses - 1) / this.m_numClasses;
/*  411:     */       }
/*  412:     */     }
/*  413: 655 */     for (int i = 0; i < trainYs.length; i++) {
/*  414: 656 */       probs[i] = probs(trainFs[i]);
/*  415:     */     }
/*  416: 660 */     for (int i = 0; i < oldWeights.length; i++) {
/*  417: 661 */       trainNumeric.instance(i).setWeight(oldWeights[i]);
/*  418:     */     }
/*  419: 663 */     return true;
/*  420:     */   }
/*  421:     */   
/*  422:     */   protected SimpleLinearRegression[][] initRegressions()
/*  423:     */     throws Exception
/*  424:     */   {
/*  425: 672 */     SimpleLinearRegression[][] classifiers = new SimpleLinearRegression[this.m_numClasses][this.m_numericDataHeader.numAttributes()];
/*  426: 674 */     for (int j = 0; j < this.m_numClasses; j++) {
/*  427: 675 */       for (int i = 0; i < this.m_numericDataHeader.numAttributes(); i++) {
/*  428: 676 */         if (i != this.m_numericDataHeader.classIndex()) {
/*  429: 677 */           classifiers[j][i] = new SimpleLinearRegression(i, 0.0D, 0.0D);
/*  430:     */         }
/*  431:     */       }
/*  432:     */     }
/*  433: 681 */     return classifiers;
/*  434:     */   }
/*  435:     */   
/*  436:     */   private class UnsafeInstance
/*  437:     */     extends DenseInstance
/*  438:     */   {
/*  439:     */     private static final long serialVersionUID = 3210674215118962869L;
/*  440:     */     
/*  441:     */     public UnsafeInstance(Instance vals)
/*  442:     */     {
/*  443: 702 */       super();
/*  444: 703 */       for (int i = 0; i < vals.numAttributes(); i++) {
/*  445: 704 */         this.m_AttValues[i] = vals.value(i);
/*  446:     */       }
/*  447: 706 */       this.m_Weight = vals.weight();
/*  448:     */     }
/*  449:     */     
/*  450:     */     public void setValue(int attIndex, double value)
/*  451:     */     {
/*  452: 715 */       this.m_AttValues[attIndex] = value;
/*  453:     */     }
/*  454:     */     
/*  455:     */     public Object copy()
/*  456:     */     {
/*  457: 724 */       return this;
/*  458:     */     }
/*  459:     */   }
/*  460:     */   
/*  461:     */   protected Instances getNumericData(Instances data)
/*  462:     */     throws Exception
/*  463:     */   {
/*  464: 738 */     if (this.m_numericDataHeader == null)
/*  465:     */     {
/*  466: 739 */       this.m_numericDataHeader = new Instances(data, 0);
/*  467:     */       
/*  468: 741 */       int classIndex = this.m_numericDataHeader.classIndex();
/*  469: 742 */       this.m_numericDataHeader.setClassIndex(-1);
/*  470: 743 */       this.m_numericDataHeader.replaceAttributeAt(new Attribute("'pseudo class'"), classIndex);
/*  471:     */       
/*  472: 745 */       this.m_numericDataHeader.setClassIndex(classIndex);
/*  473:     */     }
/*  474: 747 */     Instances numericData = new Instances(this.m_numericDataHeader, data.numInstances());
/*  475: 749 */     for (Instance inst : data) {
/*  476: 750 */       numericData.add(new UnsafeInstance(inst));
/*  477:     */     }
/*  478: 753 */     return numericData;
/*  479:     */   }
/*  480:     */   
/*  481:     */   protected double getZ(double actual, double p)
/*  482:     */   {
/*  483:     */     double z;
/*  484: 766 */     if (actual == 1.0D)
/*  485:     */     {
/*  486: 767 */       double z = 1.0D / p;
/*  487: 768 */       if (z > 3.0D) {
/*  488: 769 */         z = 3.0D;
/*  489:     */       }
/*  490:     */     }
/*  491:     */     else
/*  492:     */     {
/*  493: 772 */       z = -1.0D / (1.0D - p);
/*  494: 773 */       if (z < -3.0D) {
/*  495: 774 */         z = -3.0D;
/*  496:     */       }
/*  497:     */     }
/*  498: 777 */     return z;
/*  499:     */   }
/*  500:     */   
/*  501:     */   protected double[][] getZs(double[][] probs, double[][] dataYs)
/*  502:     */   {
/*  503: 790 */     double[][] dataZs = new double[probs.length][this.m_numClasses];
/*  504: 791 */     for (int j = 0; j < this.m_numClasses; j++) {
/*  505: 792 */       for (int i = 0; i < probs.length; i++) {
/*  506: 793 */         dataZs[i][j] = getZ(dataYs[i][j], probs[i][j]);
/*  507:     */       }
/*  508:     */     }
/*  509: 796 */     return dataZs;
/*  510:     */   }
/*  511:     */   
/*  512:     */   protected double[][] getWs(double[][] probs, double[][] dataYs)
/*  513:     */   {
/*  514: 809 */     double[][] dataWs = new double[probs.length][this.m_numClasses];
/*  515: 810 */     for (int j = 0; j < this.m_numClasses; j++) {
/*  516: 811 */       for (int i = 0; i < probs.length; i++)
/*  517:     */       {
/*  518: 812 */         double z = getZ(dataYs[i][j], probs[i][j]);
/*  519: 813 */         dataWs[i][j] = ((dataYs[i][j] - probs[i][j]) / z);
/*  520:     */       }
/*  521:     */     }
/*  522: 816 */     return dataWs;
/*  523:     */   }
/*  524:     */   
/*  525:     */   protected double[] probs(double[] Fs)
/*  526:     */   {
/*  527: 828 */     double maxF = -1.797693134862316E+308D;
/*  528: 829 */     for (double element : Fs) {
/*  529: 830 */       if (element > maxF) {
/*  530: 831 */         maxF = element;
/*  531:     */       }
/*  532:     */     }
/*  533: 834 */     double sum = 0.0D;
/*  534: 835 */     double[] probs = new double[Fs.length];
/*  535: 836 */     for (int i = 0; i < Fs.length; i++)
/*  536:     */     {
/*  537: 837 */       probs[i] = Math.exp(Fs[i] - maxF);
/*  538: 838 */       sum += probs[i];
/*  539:     */     }
/*  540: 841 */     Utils.normalize(probs, sum);
/*  541: 842 */     return probs;
/*  542:     */   }
/*  543:     */   
/*  544:     */   protected double[][] getYs(Instances data)
/*  545:     */   {
/*  546: 853 */     double[][] dataYs = new double[data.numInstances()][this.m_numClasses];
/*  547: 854 */     for (int j = 0; j < this.m_numClasses; j++) {
/*  548: 855 */       for (int k = 0; k < data.numInstances(); k++) {
/*  549: 856 */         dataYs[k][j] = (data.instance(k).classValue() == j ? 1.0D : 0.0D);
/*  550:     */       }
/*  551:     */     }
/*  552: 859 */     return dataYs;
/*  553:     */   }
/*  554:     */   
/*  555:     */   protected double[] getFs(Instance instance)
/*  556:     */     throws Exception
/*  557:     */   {
/*  558: 871 */     double[] pred = new double[this.m_numClasses];
/*  559: 872 */     double[] instanceFs = new double[this.m_numClasses];
/*  560: 875 */     for (int i = 0; i < this.m_numericDataHeader.numAttributes(); i++) {
/*  561: 876 */       if (i != this.m_numericDataHeader.classIndex())
/*  562:     */       {
/*  563: 877 */         double predSum = 0.0D;
/*  564: 878 */         for (int j = 0; j < this.m_numClasses; j++)
/*  565:     */         {
/*  566: 879 */           pred[j] = this.m_regressions[j][i].classifyInstance(instance);
/*  567: 880 */           predSum += pred[j];
/*  568:     */         }
/*  569: 882 */         predSum /= this.m_numClasses;
/*  570: 883 */         for (int j = 0; j < this.m_numClasses; j++) {
/*  571: 884 */           instanceFs[j] += (pred[j] - predSum) * (this.m_numClasses - 1) / this.m_numClasses;
/*  572:     */         }
/*  573:     */       }
/*  574:     */     }
/*  575: 890 */     return instanceFs;
/*  576:     */   }
/*  577:     */   
/*  578:     */   protected double[][] getFs(Instances data)
/*  579:     */     throws Exception
/*  580:     */   {
/*  581: 902 */     double[][] dataFs = new double[data.numInstances()][];
/*  582: 904 */     for (int k = 0; k < data.numInstances(); k++) {
/*  583: 905 */       dataFs[k] = getFs(data.instance(k));
/*  584:     */     }
/*  585: 908 */     return dataFs;
/*  586:     */   }
/*  587:     */   
/*  588:     */   protected double[][] getProbs(double[][] dataFs)
/*  589:     */   {
/*  590: 920 */     int numInstances = dataFs.length;
/*  591: 921 */     double[][] probs = new double[numInstances][];
/*  592: 923 */     for (int k = 0; k < numInstances; k++) {
/*  593: 924 */       probs[k] = probs(dataFs[k]);
/*  594:     */     }
/*  595: 926 */     return probs;
/*  596:     */   }
/*  597:     */   
/*  598:     */   protected double negativeLogLikelihood(double[][] dataYs, double[][] probs)
/*  599:     */   {
/*  600: 939 */     double logLikelihood = 0.0D;
/*  601: 940 */     for (int i = 0; i < dataYs.length; i++) {
/*  602: 941 */       for (int j = 0; j < this.m_numClasses; j++) {
/*  603: 942 */         if (dataYs[i][j] == 1.0D) {
/*  604: 943 */           logLikelihood -= Math.log(probs[i][j]);
/*  605:     */         }
/*  606:     */       }
/*  607:     */     }
/*  608: 947 */     return logLikelihood;
/*  609:     */   }
/*  610:     */   
/*  611:     */   public int[][] getUsedAttributes()
/*  612:     */   {
/*  613: 959 */     int[][] usedAttributes = new int[this.m_numClasses][];
/*  614:     */     
/*  615:     */ 
/*  616: 962 */     double[][] coefficients = getCoefficients();
/*  617: 964 */     for (int j = 0; j < this.m_numClasses; j++)
/*  618:     */     {
/*  619: 967 */       boolean[] attributes = new boolean[this.m_numericDataHeader.numAttributes()];
/*  620: 968 */       for (int i = 0; i < attributes.length; i++) {
/*  621: 970 */         if (!Utils.eq(coefficients[j][(i + 1)], 0.0D)) {
/*  622: 971 */           attributes[i] = true;
/*  623:     */         }
/*  624:     */       }
/*  625: 975 */       int numAttributes = 0;
/*  626: 976 */       for (int i = 0; i < this.m_numericDataHeader.numAttributes(); i++) {
/*  627: 977 */         if (attributes[i] != 0) {
/*  628: 978 */           numAttributes++;
/*  629:     */         }
/*  630:     */       }
/*  631: 983 */       int[] usedAttributesClass = new int[numAttributes];
/*  632: 984 */       int count = 0;
/*  633: 985 */       for (int i = 0; i < this.m_numericDataHeader.numAttributes(); i++) {
/*  634: 986 */         if (attributes[i] != 0)
/*  635:     */         {
/*  636: 987 */           usedAttributesClass[count] = i;
/*  637: 988 */           count++;
/*  638:     */         }
/*  639:     */       }
/*  640: 992 */       usedAttributes[j] = usedAttributesClass;
/*  641:     */     }
/*  642: 995 */     return usedAttributes;
/*  643:     */   }
/*  644:     */   
/*  645:     */   public int getNumRegressions()
/*  646:     */   {
/*  647:1005 */     return this.m_numRegressions;
/*  648:     */   }
/*  649:     */   
/*  650:     */   public double getWeightTrimBeta()
/*  651:     */   {
/*  652:1014 */     return this.m_weightTrimBeta;
/*  653:     */   }
/*  654:     */   
/*  655:     */   public boolean getUseAIC()
/*  656:     */   {
/*  657:1023 */     return this.m_useAIC;
/*  658:     */   }
/*  659:     */   
/*  660:     */   public void setMaxIterations(int maxIterations)
/*  661:     */   {
/*  662:1032 */     this.m_maxIterations = maxIterations;
/*  663:     */   }
/*  664:     */   
/*  665:     */   public void setHeuristicStop(int heuristicStop)
/*  666:     */   {
/*  667:1041 */     this.m_heuristicStop = heuristicStop;
/*  668:     */   }
/*  669:     */   
/*  670:     */   public void setWeightTrimBeta(double w)
/*  671:     */   {
/*  672:1048 */     this.m_weightTrimBeta = w;
/*  673:     */   }
/*  674:     */   
/*  675:     */   public void setUseAIC(boolean c)
/*  676:     */   {
/*  677:1057 */     this.m_useAIC = c;
/*  678:     */   }
/*  679:     */   
/*  680:     */   public int getMaxIterations()
/*  681:     */   {
/*  682:1066 */     return this.m_maxIterations;
/*  683:     */   }
/*  684:     */   
/*  685:     */   protected double[][] getCoefficients()
/*  686:     */   {
/*  687:1078 */     double[][] coefficients = new double[this.m_numClasses][this.m_numericDataHeader.numAttributes() + 1];
/*  688:1080 */     for (int j = 0; j < this.m_numClasses; j++) {
/*  689:1084 */       for (int i = 0; i < this.m_numericDataHeader.numAttributes(); i++) {
/*  690:1085 */         if (i != this.m_numericDataHeader.classIndex())
/*  691:     */         {
/*  692:1086 */           double slope = this.m_regressions[j][i].getSlope();
/*  693:1087 */           double intercept = this.m_regressions[j][i].getIntercept();
/*  694:1088 */           int attribute = this.m_regressions[j][i].getAttributeIndex();
/*  695:     */           
/*  696:1090 */           coefficients[j][0] += intercept;
/*  697:1091 */           coefficients[j][(attribute + 1)] += slope;
/*  698:     */         }
/*  699:     */       }
/*  700:     */     }
/*  701:1097 */     for (int j = 0; j < coefficients.length; j++) {
/*  702:1098 */       for (int i = 0; i < coefficients[0].length; i++) {
/*  703:1099 */         coefficients[j][i] *= (this.m_numClasses - 1) / this.m_numClasses;
/*  704:     */       }
/*  705:     */     }
/*  706:1104 */     return coefficients;
/*  707:     */   }
/*  708:     */   
/*  709:     */   public double percentAttributesUsed()
/*  710:     */   {
/*  711:1115 */     boolean[] attributes = new boolean[this.m_numericDataHeader.numAttributes()];
/*  712:     */     
/*  713:1117 */     double[][] coefficients = getCoefficients();
/*  714:1118 */     for (int j = 0; j < this.m_numClasses; j++) {
/*  715:1119 */       for (int i = 1; i < this.m_numericDataHeader.numAttributes() + 1; i++) {
/*  716:1123 */         if (!Utils.eq(coefficients[j][i], 0.0D)) {
/*  717:1124 */           attributes[(i - 1)] = true;
/*  718:     */         }
/*  719:     */       }
/*  720:     */     }
/*  721:1130 */     double count = 0.0D;
/*  722:1131 */     for (boolean attribute : attributes) {
/*  723:1132 */       if (attribute) {
/*  724:1133 */         count += 1.0D;
/*  725:     */       }
/*  726:     */     }
/*  727:1136 */     return count / (this.m_numericDataHeader.numAttributes() - 1) * 100.0D;
/*  728:     */   }
/*  729:     */   
/*  730:     */   public String toString()
/*  731:     */   {
/*  732:1148 */     StringBuffer s = new StringBuffer();
/*  733:     */     
/*  734:     */ 
/*  735:1151 */     int[][] attributes = getUsedAttributes();
/*  736:     */     
/*  737:     */ 
/*  738:1154 */     double[][] coefficients = getCoefficients();
/*  739:1156 */     for (int j = 0; j < this.m_numClasses; j++)
/*  740:     */     {
/*  741:1157 */       s.append("\nClass " + j + " :\n");
/*  742:     */       
/*  743:1159 */       s.append(Utils.doubleToString(coefficients[j][0], 2 + this.m_numDecimalPlaces, this.m_numDecimalPlaces) + " + \n");
/*  744:1160 */       for (int i = 0; i < attributes[j].length; i++)
/*  745:     */       {
/*  746:1162 */         s.append("[" + this.m_numericDataHeader.attribute(attributes[j][i]).name() + "]");
/*  747:     */         
/*  748:1164 */         s.append(" * " + Utils.doubleToString(coefficients[j][(attributes[j][i] + 1)], 2 + this.m_numDecimalPlaces, this.m_numDecimalPlaces));
/*  749:1166 */         if (i != attributes[j].length - 1) {
/*  750:1167 */           s.append(" +");
/*  751:     */         }
/*  752:1169 */         s.append("\n");
/*  753:     */       }
/*  754:     */     }
/*  755:1172 */     return new String(s);
/*  756:     */   }
/*  757:     */   
/*  758:     */   public double[] distributionForInstance(Instance instance)
/*  759:     */     throws Exception
/*  760:     */   {
/*  761:1185 */     instance = (Instance)instance.copy();
/*  762:     */     
/*  763:     */ 
/*  764:1188 */     instance.setDataset(this.m_numericDataHeader);
/*  765:     */     
/*  766:     */ 
/*  767:1191 */     return probs(getFs(instance));
/*  768:     */   }
/*  769:     */   
/*  770:     */   public void cleanup()
/*  771:     */   {
/*  772:1199 */     this.m_train = new Instances(this.m_train, 0);
/*  773:1200 */     this.m_numericData = null;
/*  774:     */   }
/*  775:     */   
/*  776:     */   public String getRevision()
/*  777:     */   {
/*  778:1210 */     return RevisionUtils.extract("$Revision: 11568 $");
/*  779:     */   }
/*  780:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.lmt.LogisticBase
 * JD-Core Version:    0.7.0.1
 */