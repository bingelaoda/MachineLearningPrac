/*    1:     */ package weka.classifiers.meta;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.util.ArrayList;
/*    5:     */ import java.util.Collections;
/*    6:     */ import java.util.Enumeration;
/*    7:     */ import java.util.Random;
/*    8:     */ import java.util.Vector;
/*    9:     */ import weka.classifiers.Classifier;
/*   10:     */ import weka.classifiers.RandomizableSingleClassifierEnhancer;
/*   11:     */ import weka.classifiers.evaluation.EvaluationUtils;
/*   12:     */ import weka.classifiers.evaluation.Prediction;
/*   13:     */ import weka.classifiers.evaluation.ThresholdCurve;
/*   14:     */ import weka.classifiers.functions.Logistic;
/*   15:     */ import weka.core.Attribute;
/*   16:     */ import weka.core.AttributeStats;
/*   17:     */ import weka.core.Capabilities;
/*   18:     */ import weka.core.Capabilities.Capability;
/*   19:     */ import weka.core.Drawable;
/*   20:     */ import weka.core.Instance;
/*   21:     */ import weka.core.Instances;
/*   22:     */ import weka.core.Option;
/*   23:     */ import weka.core.OptionHandler;
/*   24:     */ import weka.core.RevisionUtils;
/*   25:     */ import weka.core.SelectedTag;
/*   26:     */ import weka.core.Tag;
/*   27:     */ import weka.core.Utils;
/*   28:     */ 
/*   29:     */ public class ThresholdSelector
/*   30:     */   extends RandomizableSingleClassifierEnhancer
/*   31:     */   implements OptionHandler, Drawable
/*   32:     */ {
/*   33:     */   static final long serialVersionUID = -1795038053239867444L;
/*   34:     */   public static final int RANGE_NONE = 0;
/*   35:     */   public static final int RANGE_BOUNDS = 1;
/*   36: 166 */   public static final Tag[] TAGS_RANGE = { new Tag(0, "No range correction"), new Tag(1, "Correct based on min/max observed") };
/*   37:     */   public static final int EVAL_TRAINING_SET = 2;
/*   38:     */   public static final int EVAL_TUNED_SPLIT = 1;
/*   39:     */   public static final int EVAL_CROSS_VALIDATION = 0;
/*   40: 177 */   public static final Tag[] TAGS_EVAL = { new Tag(2, "Entire training set"), new Tag(1, "Single tuned fold"), new Tag(0, "N-Fold cross validation") };
/*   41:     */   public static final int OPTIMIZE_0 = 0;
/*   42:     */   public static final int OPTIMIZE_1 = 1;
/*   43:     */   public static final int OPTIMIZE_LFREQ = 2;
/*   44:     */   public static final int OPTIMIZE_MFREQ = 3;
/*   45:     */   public static final int OPTIMIZE_POS_NAME = 4;
/*   46: 193 */   public static final Tag[] TAGS_OPTIMIZE = { new Tag(0, "First class value"), new Tag(1, "Second class value"), new Tag(2, "Least frequent class value"), new Tag(3, "Most frequent class value"), new Tag(4, "Class value named: \"yes\", \"pos(itive)\",\"1\"") };
/*   47:     */   public static final int FMEASURE = 1;
/*   48:     */   public static final int ACCURACY = 2;
/*   49:     */   public static final int TRUE_POS = 3;
/*   50:     */   public static final int TRUE_NEG = 4;
/*   51:     */   public static final int TP_RATE = 5;
/*   52:     */   public static final int PRECISION = 6;
/*   53:     */   public static final int RECALL = 7;
/*   54: 216 */   public static final Tag[] TAGS_MEASURE = { new Tag(1, "FMEASURE"), new Tag(2, "ACCURACY"), new Tag(3, "TRUE_POS"), new Tag(4, "TRUE_NEG"), new Tag(5, "TP_RATE"), new Tag(6, "PRECISION"), new Tag(7, "RECALL") };
/*   55: 222 */   protected double m_HighThreshold = 1.0D;
/*   56: 225 */   protected double m_LowThreshold = 0.0D;
/*   57: 228 */   protected double m_BestThreshold = -1.797693134862316E+308D;
/*   58: 231 */   protected double m_BestValue = -1.797693134862316E+308D;
/*   59: 234 */   protected int m_NumXValFolds = 3;
/*   60: 237 */   protected int m_DesignatedClass = 0;
/*   61: 240 */   protected int m_ClassMode = 4;
/*   62: 243 */   protected int m_EvalMode = 1;
/*   63: 246 */   protected int m_RangeMode = 0;
/*   64: 249 */   int m_nMeasure = 1;
/*   65: 252 */   protected boolean m_manualThreshold = false;
/*   66: 254 */   protected double m_manualThresholdValue = -1.0D;
/*   67:     */   protected static final double MIN_VALUE = 0.05D;
/*   68:     */   
/*   69:     */   public ThresholdSelector()
/*   70:     */   {
/*   71: 267 */     this.m_Classifier = new Logistic();
/*   72:     */   }
/*   73:     */   
/*   74:     */   protected String defaultClassifierString()
/*   75:     */   {
/*   76: 278 */     return "weka.classifiers.functions.Logistic";
/*   77:     */   }
/*   78:     */   
/*   79:     */   protected ArrayList<Prediction> getPredictions(Instances instances, int mode, int numFolds)
/*   80:     */     throws Exception
/*   81:     */   {
/*   82: 295 */     EvaluationUtils eu = new EvaluationUtils();
/*   83: 296 */     eu.setSeed(this.m_Seed);
/*   84: 298 */     switch (mode)
/*   85:     */     {
/*   86:     */     case 1: 
/*   87: 300 */       Instances trainData = null;
/*   88: 301 */       Instances evalData = null;
/*   89: 302 */       Instances data = new Instances(instances);
/*   90: 303 */       Random random = new Random(this.m_Seed);
/*   91: 304 */       data.randomize(random);
/*   92: 305 */       data.stratify(numFolds);
/*   93: 308 */       for (int subsetIndex = 0; subsetIndex < numFolds; subsetIndex++)
/*   94:     */       {
/*   95: 309 */         trainData = data.trainCV(numFolds, subsetIndex, random);
/*   96: 310 */         evalData = data.testCV(numFolds, subsetIndex);
/*   97: 311 */         if ((checkForInstance(trainData)) && (checkForInstance(evalData))) {
/*   98:     */           break;
/*   99:     */         }
/*  100:     */       }
/*  101: 315 */       return eu.getTrainTestPredictions(this.m_Classifier, trainData, evalData);
/*  102:     */     case 2: 
/*  103: 317 */       return eu.getTrainTestPredictions(this.m_Classifier, instances, instances);
/*  104:     */     case 0: 
/*  105: 319 */       return eu.getCVPredictions(this.m_Classifier, instances, numFolds);
/*  106:     */     }
/*  107: 321 */     throw new RuntimeException("Unrecognized evaluation mode");
/*  108:     */   }
/*  109:     */   
/*  110:     */   public String measureTipText()
/*  111:     */   {
/*  112: 332 */     return "Sets the measure for determining the threshold.";
/*  113:     */   }
/*  114:     */   
/*  115:     */   public void setMeasure(SelectedTag newMeasure)
/*  116:     */   {
/*  117: 341 */     if (newMeasure.getTags() == TAGS_MEASURE) {
/*  118: 342 */       this.m_nMeasure = newMeasure.getSelectedTag().getID();
/*  119:     */     }
/*  120:     */   }
/*  121:     */   
/*  122:     */   public SelectedTag getMeasure()
/*  123:     */   {
/*  124: 352 */     return new SelectedTag(this.m_nMeasure, TAGS_MEASURE);
/*  125:     */   }
/*  126:     */   
/*  127:     */   protected void findThreshold(ArrayList<Prediction> predictions)
/*  128:     */   {
/*  129: 364 */     Instances curve = new ThresholdCurve().getCurve(predictions, this.m_DesignatedClass);
/*  130:     */     
/*  131:     */ 
/*  132: 367 */     double low = 1.0D;
/*  133: 368 */     double high = 0.0D;
/*  134: 371 */     if (curve.numInstances() > 0)
/*  135:     */     {
/*  136: 372 */       Instance maxInst = curve.instance(0);
/*  137: 373 */       double maxValue = 0.0D;
/*  138: 374 */       int index1 = 0;
/*  139: 375 */       int index2 = 0;
/*  140: 376 */       switch (this.m_nMeasure)
/*  141:     */       {
/*  142:     */       case 1: 
/*  143: 378 */         index1 = curve.attribute("FMeasure").index();
/*  144: 379 */         maxValue = maxInst.value(index1);
/*  145: 380 */         break;
/*  146:     */       case 3: 
/*  147: 382 */         index1 = curve.attribute("True Positives").index();
/*  148: 383 */         maxValue = maxInst.value(index1);
/*  149: 384 */         break;
/*  150:     */       case 4: 
/*  151: 386 */         index1 = curve.attribute("True Negatives").index();
/*  152: 387 */         maxValue = maxInst.value(index1);
/*  153: 388 */         break;
/*  154:     */       case 5: 
/*  155: 390 */         index1 = curve.attribute("True Positive Rate").index();
/*  156: 391 */         maxValue = maxInst.value(index1);
/*  157: 392 */         break;
/*  158:     */       case 6: 
/*  159: 394 */         index1 = curve.attribute("Precision").index();
/*  160: 395 */         maxValue = maxInst.value(index1);
/*  161: 396 */         break;
/*  162:     */       case 7: 
/*  163: 398 */         index1 = curve.attribute("Recall").index();
/*  164: 399 */         maxValue = maxInst.value(index1);
/*  165: 400 */         break;
/*  166:     */       case 2: 
/*  167: 402 */         index1 = curve.attribute("True Positives").index();
/*  168: 403 */         index2 = curve.attribute("True Negatives").index();
/*  169: 404 */         maxValue = maxInst.value(index1) + maxInst.value(index2);
/*  170:     */       }
/*  171: 407 */       int indexThreshold = curve.attribute("Threshold").index();
/*  172: 409 */       for (int i = 1; i < curve.numInstances(); i++)
/*  173:     */       {
/*  174: 410 */         Instance current = curve.instance(i);
/*  175: 411 */         double currentValue = 0.0D;
/*  176: 412 */         if (this.m_nMeasure == 2) {
/*  177: 413 */           currentValue = current.value(index1) + current.value(index2);
/*  178:     */         } else {
/*  179: 415 */           currentValue = current.value(index1);
/*  180:     */         }
/*  181: 418 */         if (currentValue > maxValue)
/*  182:     */         {
/*  183: 419 */           maxInst = current;
/*  184: 420 */           maxValue = currentValue;
/*  185:     */         }
/*  186: 422 */         if (this.m_RangeMode == 1)
/*  187:     */         {
/*  188: 423 */           double thresh = current.value(indexThreshold);
/*  189: 424 */           if (thresh < low) {
/*  190: 425 */             low = thresh;
/*  191:     */           }
/*  192: 427 */           if (thresh > high) {
/*  193: 428 */             high = thresh;
/*  194:     */           }
/*  195:     */         }
/*  196:     */       }
/*  197: 432 */       if (maxValue > 0.05D)
/*  198:     */       {
/*  199: 433 */         this.m_BestThreshold = maxInst.value(indexThreshold);
/*  200: 434 */         this.m_BestValue = maxValue;
/*  201:     */       }
/*  202: 437 */       if (this.m_RangeMode == 1)
/*  203:     */       {
/*  204: 438 */         this.m_LowThreshold = low;
/*  205: 439 */         this.m_HighThreshold = high;
/*  206:     */       }
/*  207:     */     }
/*  208:     */   }
/*  209:     */   
/*  210:     */   public Enumeration<Option> listOptions()
/*  211:     */   {
/*  212: 454 */     Vector<Option> newVector = new Vector(6);
/*  213:     */     
/*  214: 456 */     newVector.addElement(new Option("\tThe class for which threshold is determined. Valid values are:\n\t1, 2 (for first and second classes, respectively), 3 (for whichever\n\tclass is least frequent), and 4 (for whichever class value is most\n\tfrequent), and 5 (for the first class named any of \"yes\",\"pos(itive)\"\n\t\"1\", or method 3 if no matches). (default 5).", "C", 1, "-C <integer>"));
/*  215:     */     
/*  216:     */ 
/*  217:     */ 
/*  218:     */ 
/*  219:     */ 
/*  220:     */ 
/*  221:     */ 
/*  222:     */ 
/*  223: 465 */     newVector.addElement(new Option("\tNumber of folds used for cross validation. If just a\n\thold-out set is used, this determines the size of the hold-out set\n\t(default 3).", "X", 1, "-X <number of folds>"));
/*  224:     */     
/*  225:     */ 
/*  226:     */ 
/*  227:     */ 
/*  228:     */ 
/*  229: 471 */     newVector.addElement(new Option("\tSets whether confidence range correction is applied. This\n\tcan be used to ensure the confidences range from 0 to 1.\n\tUse 0 for no range correction, 1 for correction based on\n\tthe min/max values seen during threshold selection\n\t(default 0).", "R", 1, "-R <integer>"));
/*  230:     */     
/*  231:     */ 
/*  232:     */ 
/*  233:     */ 
/*  234:     */ 
/*  235:     */ 
/*  236: 478 */     newVector.addElement(new Option("\tSets the evaluation mode. Use 0 for\n\tevaluation using cross-validation,\n\t1 for evaluation using hold-out set,\n\tand 2 for evaluation on the\n\ttraining data (default 1).", "E", 1, "-E <integer>"));
/*  237:     */     
/*  238:     */ 
/*  239:     */ 
/*  240:     */ 
/*  241:     */ 
/*  242: 484 */     newVector.addElement(new Option("\tMeasure used for evaluation (default is FMEASURE).\n", "M", 1, "-M [FMEASURE|ACCURACY|TRUE_POS|TRUE_NEG|TP_RATE|PRECISION|RECALL]"));
/*  243:     */     
/*  244:     */ 
/*  245:     */ 
/*  246: 488 */     newVector.addElement(new Option("\tSet a manual threshold to use. This option overrides\n\tautomatic selection and options pertaining to\n\tautomatic selection will be ignored.\n\t(default -1, i.e. do not use a manual threshold).", "manual", 1, "-manual <real>"));
/*  247:     */     
/*  248:     */ 
/*  249:     */ 
/*  250:     */ 
/*  251:     */ 
/*  252:     */ 
/*  253: 495 */     newVector.addAll(Collections.list(super.listOptions()));
/*  254:     */     
/*  255: 497 */     return newVector.elements();
/*  256:     */   }
/*  257:     */   
/*  258:     */   public void setOptions(String[] options)
/*  259:     */     throws Exception
/*  260:     */   {
/*  261: 602 */     String manualS = Utils.getOption("manual", options);
/*  262: 603 */     if (manualS.length() > 0)
/*  263:     */     {
/*  264: 604 */       double val = Double.parseDouble(manualS);
/*  265: 605 */       if (val >= 0.0D) {
/*  266: 606 */         setManualThresholdValue(val);
/*  267:     */       }
/*  268:     */     }
/*  269: 610 */     String classString = Utils.getOption('C', options);
/*  270: 611 */     if (classString.length() != 0) {
/*  271: 612 */       setDesignatedClass(new SelectedTag(Integer.parseInt(classString) - 1, TAGS_OPTIMIZE));
/*  272:     */     } else {
/*  273: 615 */       setDesignatedClass(new SelectedTag(4, TAGS_OPTIMIZE));
/*  274:     */     }
/*  275: 618 */     String modeString = Utils.getOption('E', options);
/*  276: 619 */     if (modeString.length() != 0) {
/*  277: 620 */       setEvaluationMode(new SelectedTag(Integer.parseInt(modeString), TAGS_EVAL));
/*  278:     */     } else {
/*  279: 622 */       setEvaluationMode(new SelectedTag(1, TAGS_EVAL));
/*  280:     */     }
/*  281: 625 */     String rangeString = Utils.getOption('R', options);
/*  282: 626 */     if (rangeString.length() != 0) {
/*  283: 627 */       setRangeCorrection(new SelectedTag(Integer.parseInt(rangeString), TAGS_RANGE));
/*  284:     */     } else {
/*  285: 630 */       setRangeCorrection(new SelectedTag(0, TAGS_RANGE));
/*  286:     */     }
/*  287: 633 */     String measureString = Utils.getOption('M', options);
/*  288: 634 */     if (measureString.length() != 0) {
/*  289: 635 */       setMeasure(new SelectedTag(measureString, TAGS_MEASURE));
/*  290:     */     } else {
/*  291: 637 */       setMeasure(new SelectedTag(1, TAGS_MEASURE));
/*  292:     */     }
/*  293: 640 */     String foldsString = Utils.getOption('X', options);
/*  294: 641 */     if (foldsString.length() != 0) {
/*  295: 642 */       setNumXValFolds(Integer.parseInt(foldsString));
/*  296:     */     } else {
/*  297: 644 */       setNumXValFolds(3);
/*  298:     */     }
/*  299: 647 */     super.setOptions(options);
/*  300:     */     
/*  301: 649 */     Utils.checkForRemainingOptions(options);
/*  302:     */   }
/*  303:     */   
/*  304:     */   public String[] getOptions()
/*  305:     */   {
/*  306: 660 */     Vector<String> options = new Vector();
/*  307: 662 */     if (this.m_manualThreshold)
/*  308:     */     {
/*  309: 663 */       options.add("-manual");
/*  310: 664 */       options.add("" + getManualThresholdValue());
/*  311:     */     }
/*  312: 666 */     options.add("-C");
/*  313: 667 */     options.add("" + (this.m_ClassMode + 1));
/*  314: 668 */     options.add("-X");
/*  315: 669 */     options.add("" + getNumXValFolds());
/*  316: 670 */     options.add("-E");
/*  317: 671 */     options.add("" + this.m_EvalMode);
/*  318: 672 */     options.add("-R");
/*  319: 673 */     options.add("" + this.m_RangeMode);
/*  320: 674 */     options.add("-M");
/*  321: 675 */     options.add("" + getMeasure().getSelectedTag().getReadable());
/*  322:     */     
/*  323: 677 */     Collections.addAll(options, super.getOptions());
/*  324:     */     
/*  325: 679 */     return (String[])options.toArray(new String[0]);
/*  326:     */   }
/*  327:     */   
/*  328:     */   public Capabilities getCapabilities()
/*  329:     */   {
/*  330: 689 */     Capabilities result = super.getCapabilities();
/*  331:     */     
/*  332:     */ 
/*  333: 692 */     result.disableAllClasses();
/*  334: 693 */     result.disableAllClassDependencies();
/*  335: 694 */     result.enable(Capabilities.Capability.BINARY_CLASS);
/*  336:     */     
/*  337: 696 */     return result;
/*  338:     */   }
/*  339:     */   
/*  340:     */   public void buildClassifier(Instances instances)
/*  341:     */     throws Exception
/*  342:     */   {
/*  343: 709 */     getCapabilities().testWithFail(instances);
/*  344:     */     
/*  345:     */ 
/*  346: 712 */     instances = new Instances(instances);
/*  347: 713 */     instances.deleteWithMissingClass();
/*  348:     */     
/*  349: 715 */     AttributeStats stats = instances.attributeStats(instances.classIndex());
/*  350: 716 */     if (this.m_manualThreshold) {
/*  351: 717 */       this.m_BestThreshold = this.m_manualThresholdValue;
/*  352:     */     } else {
/*  353: 719 */       this.m_BestThreshold = 0.5D;
/*  354:     */     }
/*  355: 721 */     this.m_BestValue = 0.05D;
/*  356: 722 */     this.m_HighThreshold = 1.0D;
/*  357: 723 */     this.m_LowThreshold = 0.0D;
/*  358: 727 */     if (stats.distinctCount != 2)
/*  359:     */     {
/*  360: 728 */       System.err.println("Couldn't find examples of both classes. No adjustment.");
/*  361:     */       
/*  362: 730 */       this.m_Classifier.buildClassifier(instances);
/*  363:     */     }
/*  364:     */     else
/*  365:     */     {
/*  366: 734 */       switch (this.m_ClassMode)
/*  367:     */       {
/*  368:     */       case 0: 
/*  369: 736 */         this.m_DesignatedClass = 0;
/*  370: 737 */         break;
/*  371:     */       case 1: 
/*  372: 739 */         this.m_DesignatedClass = 1;
/*  373: 740 */         break;
/*  374:     */       case 4: 
/*  375: 742 */         Attribute cAtt = instances.classAttribute();
/*  376: 743 */         boolean found = false;
/*  377: 744 */         for (int i = 0; (i < cAtt.numValues()) && (!found); i++)
/*  378:     */         {
/*  379: 745 */           String name = cAtt.value(i).toLowerCase();
/*  380: 746 */           if ((name.startsWith("yes")) || (name.equals("1")) || (name.startsWith("pos")))
/*  381:     */           {
/*  382: 748 */             found = true;
/*  383: 749 */             this.m_DesignatedClass = i;
/*  384:     */           }
/*  385:     */         }
/*  386: 752 */         if (found) {
/*  387:     */           break;
/*  388:     */         }
/*  389:     */       case 2: 
/*  390: 757 */         this.m_DesignatedClass = (stats.nominalCounts[0] > stats.nominalCounts[1] ? 1 : 0);
/*  391:     */         
/*  392: 759 */         break;
/*  393:     */       case 3: 
/*  394: 761 */         this.m_DesignatedClass = (stats.nominalCounts[0] > stats.nominalCounts[1] ? 0 : 1);
/*  395:     */         
/*  396: 763 */         break;
/*  397:     */       default: 
/*  398: 765 */         throw new Exception("Unrecognized class value selection mode");
/*  399:     */       }
/*  400: 776 */       if (this.m_manualThreshold)
/*  401:     */       {
/*  402: 777 */         this.m_Classifier.buildClassifier(instances);
/*  403: 778 */         return;
/*  404:     */       }
/*  405: 781 */       if (stats.nominalCounts[this.m_DesignatedClass] == 1)
/*  406:     */       {
/*  407: 782 */         System.err.println("Only 1 positive found: optimizing on training data");
/*  408:     */         
/*  409: 784 */         findThreshold(getPredictions(instances, 2, 0));
/*  410:     */       }
/*  411:     */       else
/*  412:     */       {
/*  413: 786 */         int numFolds = Math.min(this.m_NumXValFolds, stats.nominalCounts[this.m_DesignatedClass]);
/*  414:     */         
/*  415:     */ 
/*  416:     */ 
/*  417: 790 */         findThreshold(getPredictions(instances, this.m_EvalMode, numFolds));
/*  418: 791 */         if (this.m_EvalMode != 2) {
/*  419: 792 */           this.m_Classifier.buildClassifier(instances);
/*  420:     */         }
/*  421:     */       }
/*  422:     */     }
/*  423:     */   }
/*  424:     */   
/*  425:     */   private boolean checkForInstance(Instances data)
/*  426:     */     throws Exception
/*  427:     */   {
/*  428: 807 */     for (int i = 0; i < data.numInstances(); i++) {
/*  429: 808 */       if ((int)data.instance(i).classValue() == this.m_DesignatedClass) {
/*  430: 809 */         return true;
/*  431:     */       }
/*  432:     */     }
/*  433: 812 */     return false;
/*  434:     */   }
/*  435:     */   
/*  436:     */   public double[] distributionForInstance(Instance instance)
/*  437:     */     throws Exception
/*  438:     */   {
/*  439: 825 */     double[] pred = this.m_Classifier.distributionForInstance(instance);
/*  440: 826 */     double prob = pred[this.m_DesignatedClass];
/*  441: 829 */     if (prob > this.m_BestThreshold) {
/*  442: 830 */       prob = 0.5D + (prob - this.m_BestThreshold) / ((this.m_HighThreshold - this.m_BestThreshold) * 2.0D);
/*  443:     */     } else {
/*  444: 833 */       prob = (prob - this.m_LowThreshold) / ((this.m_BestThreshold - this.m_LowThreshold) * 2.0D);
/*  445:     */     }
/*  446: 835 */     if (prob < 0.0D) {
/*  447: 836 */       prob = 0.0D;
/*  448: 837 */     } else if (prob > 1.0D) {
/*  449: 838 */       prob = 1.0D;
/*  450:     */     }
/*  451: 842 */     pred[this.m_DesignatedClass] = prob;
/*  452: 843 */     if (pred.length == 2) {
/*  453: 844 */       pred[((this.m_DesignatedClass + 1) % 2)] = (1.0D - prob);
/*  454:     */     }
/*  455: 846 */     return pred;
/*  456:     */   }
/*  457:     */   
/*  458:     */   public String globalInfo()
/*  459:     */   {
/*  460: 855 */     return "A metaclassifier that selecting a mid-point threshold on the probability output by a Classifier. The midpoint threshold is set so that a given performance measure is optimized. Currently this is the F-measure. Performance is measured either on the training data, a hold-out set or using cross-validation. In addition, the probabilities returned by the base learner can have their range expanded so that the output probabilities will reside between 0 and 1 (this is useful if the scheme normally produces probabilities in a very narrow range).";
/*  461:     */   }
/*  462:     */   
/*  463:     */   public String designatedClassTipText()
/*  464:     */   {
/*  465: 872 */     return "Sets the class value for which the optimization is performed. The options are: pick the first class value; pick the second class value; pick whichever class is least frequent; pick whichever class value is most frequent; pick the first class named any of \"yes\",\"pos(itive)\", \"1\", or the least frequent if no matches).";
/*  466:     */   }
/*  467:     */   
/*  468:     */   public SelectedTag getDesignatedClass()
/*  469:     */   {
/*  470: 887 */     return new SelectedTag(this.m_ClassMode, TAGS_OPTIMIZE);
/*  471:     */   }
/*  472:     */   
/*  473:     */   public void setDesignatedClass(SelectedTag newMethod)
/*  474:     */   {
/*  475: 898 */     if (newMethod.getTags() == TAGS_OPTIMIZE) {
/*  476: 899 */       this.m_ClassMode = newMethod.getSelectedTag().getID();
/*  477:     */     }
/*  478:     */   }
/*  479:     */   
/*  480:     */   public String evaluationModeTipText()
/*  481:     */   {
/*  482: 909 */     return "Sets the method used to determine the threshold/performance curve. The options are: perform optimization based on the entire training set (may result in overfitting); perform an n-fold cross-validation (may be time consuming); perform one fold of an n-fold cross-validation (faster but likely less accurate).";
/*  483:     */   }
/*  484:     */   
/*  485:     */   public void setEvaluationMode(SelectedTag newMethod)
/*  486:     */   {
/*  487: 924 */     if (newMethod.getTags() == TAGS_EVAL) {
/*  488: 925 */       this.m_EvalMode = newMethod.getSelectedTag().getID();
/*  489:     */     }
/*  490:     */   }
/*  491:     */   
/*  492:     */   public SelectedTag getEvaluationMode()
/*  493:     */   {
/*  494: 937 */     return new SelectedTag(this.m_EvalMode, TAGS_EVAL);
/*  495:     */   }
/*  496:     */   
/*  497:     */   public String rangeCorrectionTipText()
/*  498:     */   {
/*  499: 946 */     return "Sets the type of prediction range correction performed. The options are: do not do any range correction; expand predicted probabilities so that the minimum probability observed during the optimization maps to 0, and the maximum maps to 1 (values outside this range are clipped to 0 and 1).";
/*  500:     */   }
/*  501:     */   
/*  502:     */   public void setRangeCorrection(SelectedTag newMethod)
/*  503:     */   {
/*  504: 961 */     if (newMethod.getTags() == TAGS_RANGE) {
/*  505: 962 */       this.m_RangeMode = newMethod.getSelectedTag().getID();
/*  506:     */     }
/*  507:     */   }
/*  508:     */   
/*  509:     */   public SelectedTag getRangeCorrection()
/*  510:     */   {
/*  511: 974 */     return new SelectedTag(this.m_RangeMode, TAGS_RANGE);
/*  512:     */   }
/*  513:     */   
/*  514:     */   public String numXValFoldsTipText()
/*  515:     */   {
/*  516: 983 */     return "Sets the number of folds used during full cross-validation and tuned fold evaluation. This number will be automatically reduced if there are insufficient positive examples.";
/*  517:     */   }
/*  518:     */   
/*  519:     */   public int getNumXValFolds()
/*  520:     */   {
/*  521: 995 */     return this.m_NumXValFolds;
/*  522:     */   }
/*  523:     */   
/*  524:     */   public void setNumXValFolds(int newNumFolds)
/*  525:     */   {
/*  526:1005 */     if (newNumFolds < 2) {
/*  527:1006 */       throw new IllegalArgumentException("Number of folds must be greater than 1");
/*  528:     */     }
/*  529:1009 */     this.m_NumXValFolds = newNumFolds;
/*  530:     */   }
/*  531:     */   
/*  532:     */   public int graphType()
/*  533:     */   {
/*  534:1020 */     if ((this.m_Classifier instanceof Drawable)) {
/*  535:1021 */       return ((Drawable)this.m_Classifier).graphType();
/*  536:     */     }
/*  537:1023 */     return 0;
/*  538:     */   }
/*  539:     */   
/*  540:     */   public String graph()
/*  541:     */     throws Exception
/*  542:     */   {
/*  543:1036 */     if ((this.m_Classifier instanceof Drawable)) {
/*  544:1037 */       return ((Drawable)this.m_Classifier).graph();
/*  545:     */     }
/*  546:1039 */     throw new Exception("Classifier: " + getClassifierSpec() + " cannot be graphed");
/*  547:     */   }
/*  548:     */   
/*  549:     */   public String manualThresholdValueTipText()
/*  550:     */   {
/*  551:1050 */     return "Sets a manual threshold value to use. If this is set (non-negative value between 0 and 1), then all options pertaining to automatic threshold selection are ignored. ";
/*  552:     */   }
/*  553:     */   
/*  554:     */   public void setManualThresholdValue(double threshold)
/*  555:     */     throws Exception
/*  556:     */   {
/*  557:1064 */     this.m_manualThresholdValue = threshold;
/*  558:1065 */     if ((threshold >= 0.0D) && (threshold <= 1.0D))
/*  559:     */     {
/*  560:1066 */       this.m_manualThreshold = true;
/*  561:     */     }
/*  562:     */     else
/*  563:     */     {
/*  564:1068 */       this.m_manualThreshold = false;
/*  565:1069 */       if (threshold >= 0.0D) {
/*  566:1070 */         throw new IllegalArgumentException("Threshold must be in the range 0..1.");
/*  567:     */       }
/*  568:     */     }
/*  569:     */   }
/*  570:     */   
/*  571:     */   public double getManualThresholdValue()
/*  572:     */   {
/*  573:1083 */     return this.m_manualThresholdValue;
/*  574:     */   }
/*  575:     */   
/*  576:     */   public String toString()
/*  577:     */   {
/*  578:1094 */     if (this.m_BestValue == -1.797693134862316E+308D) {
/*  579:1095 */       return "ThresholdSelector: No model built yet.";
/*  580:     */     }
/*  581:1098 */     String result = "Threshold Selector.\nClassifier: " + this.m_Classifier.getClass().getName() + "\n";
/*  582:     */     
/*  583:     */ 
/*  584:1101 */     result = result + "Index of designated class: " + this.m_DesignatedClass + "\n";
/*  585:1103 */     if (this.m_manualThreshold)
/*  586:     */     {
/*  587:1104 */       result = result + "User supplied threshold: " + this.m_BestThreshold + "\n";
/*  588:     */     }
/*  589:     */     else
/*  590:     */     {
/*  591:1106 */       result = result + "Evaluation mode: ";
/*  592:1107 */       switch (this.m_EvalMode)
/*  593:     */       {
/*  594:     */       case 0: 
/*  595:1109 */         result = result + this.m_NumXValFolds + "-fold cross-validation";
/*  596:1110 */         break;
/*  597:     */       case 1: 
/*  598:1112 */         result = result + "tuning on 1/" + this.m_NumXValFolds + " of the data";
/*  599:1113 */         break;
/*  600:     */       case 2: 
/*  601:     */       default: 
/*  602:1116 */         result = result + "tuning on the training data";
/*  603:     */       }
/*  604:1118 */       result = result + "\n";
/*  605:     */       
/*  606:1120 */       result = result + "Threshold: " + this.m_BestThreshold + "\n";
/*  607:1121 */       result = result + "Best value: " + this.m_BestValue + "\n";
/*  608:1122 */       if (this.m_RangeMode == 1) {
/*  609:1123 */         result = result + "Expanding range [" + this.m_LowThreshold + "," + this.m_HighThreshold + "] to [0, 1]\n";
/*  610:     */       }
/*  611:1126 */       result = result + "Measure: " + getMeasure().getSelectedTag().getReadable() + "\n";
/*  612:     */     }
/*  613:1129 */     result = result + this.m_Classifier.toString();
/*  614:1130 */     return result;
/*  615:     */   }
/*  616:     */   
/*  617:     */   public String getRevision()
/*  618:     */   {
/*  619:1140 */     return RevisionUtils.extract("$Revision: 10390 $");
/*  620:     */   }
/*  621:     */   
/*  622:     */   public static void main(String[] argv)
/*  623:     */   {
/*  624:1149 */     runClassifier(new ThresholdSelector(), argv);
/*  625:     */   }
/*  626:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.ThresholdSelector
 * JD-Core Version:    0.7.0.1
 */