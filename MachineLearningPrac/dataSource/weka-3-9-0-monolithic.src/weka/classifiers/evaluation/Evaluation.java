/*    1:     */ package weka.classifiers.evaluation;
/*    2:     */ 
/*    3:     */ import java.beans.BeanInfo;
/*    4:     */ import java.beans.Introspector;
/*    5:     */ import java.beans.MethodDescriptor;
/*    6:     */ import java.io.BufferedInputStream;
/*    7:     */ import java.io.BufferedOutputStream;
/*    8:     */ import java.io.BufferedReader;
/*    9:     */ import java.io.FileInputStream;
/*   10:     */ import java.io.FileOutputStream;
/*   11:     */ import java.io.FileReader;
/*   12:     */ import java.io.InputStream;
/*   13:     */ import java.io.ObjectInputStream;
/*   14:     */ import java.io.ObjectOutputStream;
/*   15:     */ import java.io.OutputStream;
/*   16:     */ import java.io.PrintStream;
/*   17:     */ import java.io.Reader;
/*   18:     */ import java.io.Serializable;
/*   19:     */ import java.lang.reflect.Method;
/*   20:     */ import java.util.ArrayList;
/*   21:     */ import java.util.Arrays;
/*   22:     */ import java.util.Date;
/*   23:     */ import java.util.Enumeration;
/*   24:     */ import java.util.Iterator;
/*   25:     */ import java.util.List;
/*   26:     */ import java.util.Random;
/*   27:     */ import java.util.zip.GZIPInputStream;
/*   28:     */ import java.util.zip.GZIPOutputStream;
/*   29:     */ import weka.classifiers.AbstractClassifier;
/*   30:     */ import weka.classifiers.Classifier;
/*   31:     */ import weka.classifiers.ConditionalDensityEstimator;
/*   32:     */ import weka.classifiers.CostMatrix;
/*   33:     */ import weka.classifiers.IntervalEstimator;
/*   34:     */ import weka.classifiers.Sourcable;
/*   35:     */ import weka.classifiers.UpdateableBatchProcessor;
/*   36:     */ import weka.classifiers.UpdateableClassifier;
/*   37:     */ import weka.classifiers.evaluation.output.prediction.AbstractOutput;
/*   38:     */ import weka.classifiers.evaluation.output.prediction.PlainText;
/*   39:     */ import weka.classifiers.misc.InputMappedClassifier;
/*   40:     */ import weka.classifiers.pmml.consumer.PMMLClassifier;
/*   41:     */ import weka.classifiers.xml.XMLClassifier;
/*   42:     */ import weka.core.Attribute;
/*   43:     */ import weka.core.BatchPredictor;
/*   44:     */ import weka.core.Capabilities;
/*   45:     */ import weka.core.Drawable;
/*   46:     */ import weka.core.Instance;
/*   47:     */ import weka.core.Instances;
/*   48:     */ import weka.core.Option;
/*   49:     */ import weka.core.OptionHandler;
/*   50:     */ import weka.core.RevisionHandler;
/*   51:     */ import weka.core.RevisionUtils;
/*   52:     */ import weka.core.Summarizable;
/*   53:     */ import weka.core.Utils;
/*   54:     */ import weka.core.Version;
/*   55:     */ import weka.core.converters.ConverterUtils.DataSink;
/*   56:     */ import weka.core.converters.ConverterUtils.DataSource;
/*   57:     */ import weka.core.pmml.PMMLFactory;
/*   58:     */ import weka.core.pmml.PMMLModel;
/*   59:     */ import weka.core.xml.KOML;
/*   60:     */ import weka.core.xml.XMLOptions;
/*   61:     */ import weka.core.xml.XMLSerialization;
/*   62:     */ import weka.estimators.UnivariateKernelEstimator;
/*   63:     */ import weka.experiment.Stats;
/*   64:     */ 
/*   65:     */ public class Evaluation
/*   66:     */   implements Summarizable, RevisionHandler, Serializable
/*   67:     */ {
/*   68:     */   private static final long serialVersionUID = -7010314486866816271L;
/*   69:     */   protected int m_NumClasses;
/*   70:     */   protected int m_NumFolds;
/*   71:     */   protected double m_Incorrect;
/*   72:     */   protected double m_Correct;
/*   73:     */   protected double m_Unclassified;
/*   74:     */   protected double m_MissingClass;
/*   75:     */   protected double m_WithClass;
/*   76:     */   protected double[][] m_ConfusionMatrix;
/*   77:     */   protected String[] m_ClassNames;
/*   78:     */   protected boolean m_ClassIsNominal;
/*   79:     */   protected double[] m_ClassPriors;
/*   80:     */   protected double m_ClassPriorsSum;
/*   81:     */   protected CostMatrix m_CostMatrix;
/*   82:     */   protected double m_TotalCost;
/*   83:     */   protected double m_SumErr;
/*   84:     */   protected double m_SumAbsErr;
/*   85:     */   protected double m_SumSqrErr;
/*   86:     */   protected double m_SumClass;
/*   87:     */   protected double m_SumSqrClass;
/*   88:     */   protected double m_SumPredicted;
/*   89:     */   protected double m_SumSqrPredicted;
/*   90:     */   protected double m_SumClassPredicted;
/*   91:     */   protected double m_SumPriorAbsErr;
/*   92:     */   protected double m_SumPriorSqrErr;
/*   93:     */   protected double m_SumKBInfo;
/*   94: 318 */   protected static int k_MarginResolution = 500;
/*   95:     */   protected double[] m_MarginCounts;
/*   96:     */   protected int m_NumTrainClassVals;
/*   97:     */   protected double[] m_TrainClassVals;
/*   98:     */   protected double[] m_TrainClassWeights;
/*   99:     */   protected UnivariateKernelEstimator m_PriorEstimator;
/*  100: 336 */   protected boolean m_ComplexityStatisticsAvailable = true;
/*  101:     */   protected static final double MIN_SF_PROB = 4.9E-324D;
/*  102:     */   protected double m_SumPriorEntropy;
/*  103:     */   protected double m_SumSchemeEntropy;
/*  104: 351 */   protected boolean m_CoverageStatisticsAvailable = true;
/*  105: 354 */   protected double m_ConfLevel = 0.95D;
/*  106:     */   protected double m_TotalSizeOfRegions;
/*  107:     */   protected double m_TotalCoverage;
/*  108:     */   protected double m_MinTarget;
/*  109:     */   protected double m_MaxTarget;
/*  110:     */   protected ArrayList<Prediction> m_Predictions;
/*  111: 375 */   protected boolean m_NoPriors = false;
/*  112:     */   protected Instances m_Header;
/*  113:     */   protected boolean m_DiscardPredictions;
/*  114:     */   protected List<AbstractEvaluationMetric> m_pluginMetrics;
/*  115: 387 */   protected List<String> m_metricsToDisplay = new ArrayList();
/*  116: 389 */   public static final String[] BUILT_IN_EVAL_METRICS = { "Correct", "Incorrect", "Kappa", "Total cost", "Average cost", "KB relative", "KB information", "Correlation", "Complexity 0", "Complexity scheme", "Complexity improvement", "MAE", "RMSE", "RAE", "RRSE", "Coverage", "Region size", "TP rate", "FP rate", "Precision", "Recall", "F-measure", "MCC", "ROC area", "PRC area" };
/*  117:     */   
/*  118:     */   public static List<String> getAllEvaluationMetricNames()
/*  119:     */   {
/*  120: 403 */     List<String> allEvals = new ArrayList();
/*  121: 405 */     for (String s : BUILT_IN_EVAL_METRICS) {
/*  122: 406 */       allEvals.add(s);
/*  123:     */     }
/*  124: 408 */     List<AbstractEvaluationMetric> pluginMetrics = AbstractEvaluationMetric.getPluginMetrics();
/*  125: 411 */     if (pluginMetrics != null) {
/*  126: 412 */       for (AbstractEvaluationMetric m : pluginMetrics) {
/*  127: 413 */         if ((m instanceof InformationRetrievalEvaluationMetric))
/*  128:     */         {
/*  129: 414 */           List<String> statNames = m.getStatisticNames();
/*  130: 415 */           for (String s : statNames) {
/*  131: 416 */             allEvals.add(s);
/*  132:     */           }
/*  133:     */         }
/*  134:     */         else
/*  135:     */         {
/*  136: 419 */           allEvals.add(m.getMetricName());
/*  137:     */         }
/*  138:     */       }
/*  139:     */     }
/*  140: 424 */     return allEvals;
/*  141:     */   }
/*  142:     */   
/*  143:     */   public Evaluation(Instances data)
/*  144:     */     throws Exception
/*  145:     */   {
/*  146: 441 */     this(data, null);
/*  147:     */   }
/*  148:     */   
/*  149:     */   public Evaluation(Instances data, CostMatrix costMatrix)
/*  150:     */     throws Exception
/*  151:     */   {
/*  152: 460 */     this.m_Header = new Instances(data, 0);
/*  153: 461 */     this.m_NumClasses = data.numClasses();
/*  154: 462 */     this.m_NumFolds = 1;
/*  155: 463 */     this.m_ClassIsNominal = data.classAttribute().isNominal();
/*  156: 465 */     if (this.m_ClassIsNominal)
/*  157:     */     {
/*  158: 466 */       this.m_ConfusionMatrix = new double[this.m_NumClasses][this.m_NumClasses];
/*  159: 467 */       this.m_ClassNames = new String[this.m_NumClasses];
/*  160: 468 */       for (int i = 0; i < this.m_NumClasses; i++) {
/*  161: 469 */         this.m_ClassNames[i] = data.classAttribute().value(i);
/*  162:     */       }
/*  163:     */     }
/*  164: 472 */     this.m_CostMatrix = costMatrix;
/*  165: 473 */     if (this.m_CostMatrix != null)
/*  166:     */     {
/*  167: 474 */       if (!this.m_ClassIsNominal) {
/*  168: 475 */         throw new Exception("Class has to be nominal if cost matrix given!");
/*  169:     */       }
/*  170: 477 */       if (this.m_CostMatrix.size() != this.m_NumClasses) {
/*  171: 478 */         throw new Exception("Cost matrix not compatible with data!");
/*  172:     */       }
/*  173:     */     }
/*  174: 481 */     this.m_ClassPriors = new double[this.m_NumClasses];
/*  175: 482 */     setPriors(data);
/*  176: 483 */     this.m_MarginCounts = new double[k_MarginResolution + 1];
/*  177: 485 */     for (String s : BUILT_IN_EVAL_METRICS) {
/*  178: 486 */       if ((!s.equalsIgnoreCase("Coverage")) && (!s.equalsIgnoreCase("Region size"))) {
/*  179: 487 */         this.m_metricsToDisplay.add(s.toLowerCase());
/*  180:     */       }
/*  181:     */     }
/*  182: 491 */     this.m_pluginMetrics = AbstractEvaluationMetric.getPluginMetrics();
/*  183: 492 */     if (this.m_pluginMetrics != null) {
/*  184: 493 */       for (AbstractEvaluationMetric m : this.m_pluginMetrics)
/*  185:     */       {
/*  186: 494 */         m.setBaseEvaluation(this);
/*  187: 495 */         if ((m instanceof InformationRetrievalEvaluationMetric))
/*  188:     */         {
/*  189: 496 */           List<String> statNames = m.getStatisticNames();
/*  190: 497 */           for (String s : statNames) {
/*  191: 498 */             this.m_metricsToDisplay.add(s.toLowerCase());
/*  192:     */           }
/*  193:     */         }
/*  194:     */         else
/*  195:     */         {
/*  196: 501 */           this.m_metricsToDisplay.add(m.getMetricName().toLowerCase());
/*  197:     */         }
/*  198:     */       }
/*  199:     */     }
/*  200:     */   }
/*  201:     */   
/*  202:     */   public Instances getHeader()
/*  203:     */   {
/*  204: 513 */     return this.m_Header;
/*  205:     */   }
/*  206:     */   
/*  207:     */   public void setDiscardPredictions(boolean value)
/*  208:     */   {
/*  209: 524 */     this.m_DiscardPredictions = value;
/*  210: 525 */     if (this.m_DiscardPredictions) {
/*  211: 526 */       this.m_Predictions = null;
/*  212:     */     }
/*  213:     */   }
/*  214:     */   
/*  215:     */   public boolean getDiscardPredictions()
/*  216:     */   {
/*  217: 538 */     return this.m_DiscardPredictions;
/*  218:     */   }
/*  219:     */   
/*  220:     */   public List<AbstractEvaluationMetric> getPluginMetrics()
/*  221:     */   {
/*  222: 547 */     return this.m_pluginMetrics;
/*  223:     */   }
/*  224:     */   
/*  225:     */   public void setMetricsToDisplay(List<String> display)
/*  226:     */   {
/*  227: 559 */     this.m_metricsToDisplay.clear();
/*  228: 560 */     for (String s : display) {
/*  229: 561 */       this.m_metricsToDisplay.add(s.trim().toLowerCase());
/*  230:     */     }
/*  231:     */   }
/*  232:     */   
/*  233:     */   public List<String> getMetricsToDisplay()
/*  234:     */   {
/*  235: 573 */     return this.m_metricsToDisplay;
/*  236:     */   }
/*  237:     */   
/*  238:     */   public void toggleEvalMetrics(List<String> metricsToToggle)
/*  239:     */   {
/*  240: 582 */     for (String s : metricsToToggle) {
/*  241: 583 */       if (this.m_metricsToDisplay.contains(s.toLowerCase())) {
/*  242: 584 */         this.m_metricsToDisplay.remove(s.toLowerCase());
/*  243:     */       } else {
/*  244: 586 */         this.m_metricsToDisplay.add(s.toLowerCase());
/*  245:     */       }
/*  246:     */     }
/*  247:     */   }
/*  248:     */   
/*  249:     */   public AbstractEvaluationMetric getPluginMetric(String name)
/*  250:     */   {
/*  251: 602 */     AbstractEvaluationMetric match = null;
/*  252: 604 */     if (this.m_pluginMetrics != null) {
/*  253: 605 */       for (AbstractEvaluationMetric m : this.m_pluginMetrics) {
/*  254: 606 */         if ((m.getMetricName().equals(name)) || (m.getClass().getName().equals(name)))
/*  255:     */         {
/*  256: 608 */           match = m;
/*  257: 609 */           break;
/*  258:     */         }
/*  259:     */       }
/*  260:     */     }
/*  261: 614 */     return match;
/*  262:     */   }
/*  263:     */   
/*  264:     */   public double areaUnderROC(int classIndex)
/*  265:     */   {
/*  266: 628 */     if (this.m_Predictions == null) {
/*  267: 629 */       return Utils.missingValue();
/*  268:     */     }
/*  269: 631 */     ThresholdCurve tc = new ThresholdCurve();
/*  270: 632 */     Instances result = tc.getCurve(this.m_Predictions, classIndex);
/*  271: 633 */     return ThresholdCurve.getROCArea(result);
/*  272:     */   }
/*  273:     */   
/*  274:     */   public double weightedAreaUnderROC()
/*  275:     */   {
/*  276: 643 */     double[] classCounts = new double[this.m_NumClasses];
/*  277: 644 */     double classCountSum = 0.0D;
/*  278: 646 */     for (int i = 0; i < this.m_NumClasses; i++)
/*  279:     */     {
/*  280: 647 */       for (int j = 0; j < this.m_NumClasses; j++) {
/*  281: 648 */         classCounts[i] += this.m_ConfusionMatrix[i][j];
/*  282:     */       }
/*  283: 650 */       classCountSum += classCounts[i];
/*  284:     */     }
/*  285: 653 */     double aucTotal = 0.0D;
/*  286: 654 */     for (int i = 0; i < this.m_NumClasses; i++)
/*  287:     */     {
/*  288: 655 */       double temp = areaUnderROC(i);
/*  289: 656 */       if (!Utils.isMissingValue(temp)) {
/*  290: 657 */         aucTotal += temp * classCounts[i];
/*  291:     */       }
/*  292:     */     }
/*  293: 661 */     return aucTotal / classCountSum;
/*  294:     */   }
/*  295:     */   
/*  296:     */   public double areaUnderPRC(int classIndex)
/*  297:     */   {
/*  298: 674 */     if (this.m_Predictions == null) {
/*  299: 675 */       return Utils.missingValue();
/*  300:     */     }
/*  301: 677 */     ThresholdCurve tc = new ThresholdCurve();
/*  302: 678 */     Instances result = tc.getCurve(this.m_Predictions, classIndex);
/*  303: 679 */     return ThresholdCurve.getPRCArea(result);
/*  304:     */   }
/*  305:     */   
/*  306:     */   public double weightedAreaUnderPRC()
/*  307:     */   {
/*  308: 689 */     double[] classCounts = new double[this.m_NumClasses];
/*  309: 690 */     double classCountSum = 0.0D;
/*  310: 692 */     for (int i = 0; i < this.m_NumClasses; i++)
/*  311:     */     {
/*  312: 693 */       for (int j = 0; j < this.m_NumClasses; j++) {
/*  313: 694 */         classCounts[i] += this.m_ConfusionMatrix[i][j];
/*  314:     */       }
/*  315: 696 */       classCountSum += classCounts[i];
/*  316:     */     }
/*  317: 699 */     double auprcTotal = 0.0D;
/*  318: 700 */     for (int i = 0; i < this.m_NumClasses; i++)
/*  319:     */     {
/*  320: 701 */       double temp = areaUnderPRC(i);
/*  321: 702 */       if (!Utils.isMissingValue(temp)) {
/*  322: 703 */         auprcTotal += temp * classCounts[i];
/*  323:     */       }
/*  324:     */     }
/*  325: 707 */     return auprcTotal / classCountSum;
/*  326:     */   }
/*  327:     */   
/*  328:     */   public double[][] confusionMatrix()
/*  329:     */   {
/*  330: 717 */     double[][] newMatrix = new double[this.m_ConfusionMatrix.length][0];
/*  331: 719 */     for (int i = 0; i < this.m_ConfusionMatrix.length; i++)
/*  332:     */     {
/*  333: 720 */       newMatrix[i] = new double[this.m_ConfusionMatrix[i].length];
/*  334: 721 */       System.arraycopy(this.m_ConfusionMatrix[i], 0, newMatrix[i], 0, this.m_ConfusionMatrix[i].length);
/*  335:     */     }
/*  336: 724 */     return newMatrix;
/*  337:     */   }
/*  338:     */   
/*  339:     */   public void crossValidateModel(Classifier classifier, Instances data, int numFolds, Random random, Object... forPredictionsPrinting)
/*  340:     */     throws Exception
/*  341:     */   {
/*  342: 749 */     data = new Instances(data);
/*  343: 750 */     data.randomize(random);
/*  344: 751 */     if (data.classAttribute().isNominal()) {
/*  345: 752 */       data.stratify(numFolds);
/*  346:     */     }
/*  347: 757 */     AbstractOutput classificationOutput = null;
/*  348: 758 */     if (forPredictionsPrinting.length > 0)
/*  349:     */     {
/*  350: 760 */       classificationOutput = (AbstractOutput)forPredictionsPrinting[0];
/*  351: 761 */       classificationOutput.setHeader(data);
/*  352: 762 */       classificationOutput.printHeader();
/*  353:     */     }
/*  354: 766 */     for (int i = 0; i < numFolds; i++)
/*  355:     */     {
/*  356: 767 */       Instances train = data.trainCV(numFolds, i, random);
/*  357: 768 */       setPriors(train);
/*  358: 769 */       Classifier copiedClassifier = AbstractClassifier.makeCopy(classifier);
/*  359: 770 */       copiedClassifier.buildClassifier(train);
/*  360: 771 */       Instances test = data.testCV(numFolds, i);
/*  361: 772 */       evaluateModel(copiedClassifier, test, forPredictionsPrinting);
/*  362:     */     }
/*  363: 774 */     this.m_NumFolds = numFolds;
/*  364: 776 */     if (classificationOutput != null) {
/*  365: 777 */       classificationOutput.printFooter();
/*  366:     */     }
/*  367:     */   }
/*  368:     */   
/*  369:     */   public void crossValidateModel(String classifierString, Instances data, int numFolds, String[] options, Random random)
/*  370:     */     throws Exception
/*  371:     */   {
/*  372: 797 */     crossValidateModel(AbstractClassifier.forName(classifierString, options), data, numFolds, random, new Object[0]);
/*  373:     */   }
/*  374:     */   
/*  375:     */   public static String evaluateModel(String classifierString, String[] options)
/*  376:     */     throws Exception
/*  377:     */   {
/*  378:     */     Classifier classifier;
/*  379:     */     try
/*  380:     */     {
/*  381: 935 */       classifier = AbstractClassifier.forName(classifierString, null);
/*  382:     */     }
/*  383:     */     catch (Exception e)
/*  384:     */     {
/*  385: 939 */       throw new Exception("Can't find class with name " + classifierString + '.');
/*  386:     */     }
/*  387: 942 */     return evaluateModel(classifier, options);
/*  388:     */   }
/*  389:     */   
/*  390:     */   public static void main(String[] args)
/*  391:     */   {
/*  392:     */     try
/*  393:     */     {
/*  394: 955 */       if (args.length == 0) {
/*  395: 956 */         throw new Exception("The first argument must be the class name of a classifier");
/*  396:     */       }
/*  397: 959 */       String classifier = args[0];
/*  398: 960 */       args[0] = "";
/*  399: 961 */       System.out.println(evaluateModel(classifier, args));
/*  400:     */     }
/*  401:     */     catch (Exception ex)
/*  402:     */     {
/*  403: 963 */       ex.printStackTrace();
/*  404: 964 */       System.err.println(ex.getMessage());
/*  405:     */     }
/*  406:     */   }
/*  407:     */   
/*  408:     */   public static String evaluateModel(Classifier classifier, String[] options)
/*  409:     */     throws Exception
/*  410:     */   {
/*  411:1088 */     Instances train = null;Instances test = null;Instances template = null;
/*  412:1089 */     int seed = 1;int folds = 10;int classIndex = -1;
/*  413:1090 */     boolean noCrossValidation = false;
/*  414:     */     
/*  415:1092 */     boolean noOutput = false;boolean trainStatistics = true;boolean printMargins = false;boolean printComplexityStatistics = false;
/*  416:1093 */     boolean printGraph = false;boolean classStatistics = false;boolean printSource = false;
/*  417:1094 */     StringBuffer text = new StringBuffer();
/*  418:1095 */     ConverterUtils.DataSource trainSource = null;ConverterUtils.DataSource testSource = null;
/*  419:1096 */     ObjectInputStream objectInputStream = null;
/*  420:1097 */     BufferedInputStream xmlInputStream = null;
/*  421:1098 */     CostMatrix costMatrix = null;
/*  422:1099 */     StringBuffer schemeOptionsText = null;
/*  423:1100 */     long trainTimeStart = 0L;long trainTimeElapsed = 0L;long testTimeStart = 0L;long testTimeElapsed = 0L;
/*  424:     */     
/*  425:1102 */     String xml = "";
/*  426:1103 */     String[] optionsTmp = null;
/*  427:1104 */     Classifier classifierBackup = null;
/*  428:1105 */     int actualClassIndex = -1;
/*  429:1106 */     String splitPercentageString = "";
/*  430:1107 */     double splitPercentage = -1.0D;
/*  431:1108 */     boolean preserveOrder = false;
/*  432:1109 */     boolean forceBatchTraining = false;
/*  433:     */     
/*  434:     */ 
/*  435:1112 */     boolean trainSetPresent = false;
/*  436:1113 */     boolean testSetPresent = false;
/*  437:1114 */     boolean discardPredictions = false;
/*  438:     */     
/*  439:     */ 
/*  440:1117 */     StringBuffer predsBuff = null;
/*  441:1118 */     AbstractOutput classificationOutput = null;
/*  442:1121 */     if ((Utils.getFlag("h", options)) || (Utils.getFlag("help", options)))
/*  443:     */     {
/*  444:1124 */       boolean globalInfo = (Utils.getFlag("synopsis", options)) || (Utils.getFlag("info", options));
/*  445:     */       
/*  446:     */ 
/*  447:1127 */       throw new Exception("\nHelp requested." + makeOptionString(classifier, globalInfo));
/*  448:     */     }
/*  449:1131 */     String metricsToToggle = Utils.getOption("toggle", options);
/*  450:1132 */     List<String> toggleList = new ArrayList();
/*  451:1133 */     if (metricsToToggle.length() > 0)
/*  452:     */     {
/*  453:1134 */       String[] parts = metricsToToggle.split(",");
/*  454:1135 */       for (String p : parts) {
/*  455:1136 */         toggleList.add(p.trim().toLowerCase());
/*  456:     */       }
/*  457:     */     }
/*  458:     */     String objectInputFileName;
/*  459:     */     String objectOutputFileName;
/*  460:     */     String sourceClass;
/*  461:     */     String thresholdFile;
/*  462:     */     String thresholdLabel;
/*  463:     */     try
/*  464:     */     {
/*  465:1142 */       xml = Utils.getOption("xml", options);
/*  466:1143 */       if (!xml.equals("")) {
/*  467:1144 */         options = new XMLOptions(xml).toArray();
/*  468:     */       }
/*  469:1148 */       optionsTmp = new String[options.length];
/*  470:1149 */       for (int i = 0; i < options.length; i++) {
/*  471:1150 */         optionsTmp[i] = options[i];
/*  472:     */       }
/*  473:1153 */       String tmpO = Utils.getOption('l', optionsTmp);
/*  474:1155 */       if (tmpO.endsWith(".xml"))
/*  475:     */       {
/*  476:1157 */         boolean success = false;
/*  477:     */         try
/*  478:     */         {
/*  479:1159 */           PMMLModel pmmlModel = PMMLFactory.getPMMLModel(tmpO);
/*  480:1160 */           if ((pmmlModel instanceof PMMLClassifier))
/*  481:     */           {
/*  482:1161 */             classifier = (PMMLClassifier)pmmlModel;
/*  483:1162 */             success = true;
/*  484:     */           }
/*  485:     */         }
/*  486:     */         catch (IllegalArgumentException ex)
/*  487:     */         {
/*  488:1165 */           success = false;
/*  489:     */         }
/*  490:1167 */         if (!success)
/*  491:     */         {
/*  492:1169 */           XMLClassifier xmlserial = new XMLClassifier();
/*  493:1170 */           OptionHandler cl = (OptionHandler)xmlserial.read(Utils.getOption('l', options));
/*  494:     */           
/*  495:     */ 
/*  496:     */ 
/*  497:1174 */           optionsTmp = new String[options.length + cl.getOptions().length];
/*  498:1175 */           System.arraycopy(cl.getOptions(), 0, optionsTmp, 0, cl.getOptions().length);
/*  499:     */           
/*  500:1177 */           System.arraycopy(options, 0, optionsTmp, cl.getOptions().length, options.length);
/*  501:     */           
/*  502:1179 */           options = optionsTmp;
/*  503:     */         }
/*  504:     */       }
/*  505:1183 */       noCrossValidation = Utils.getFlag("no-cv", options);
/*  506:     */       
/*  507:1185 */       String classIndexString = Utils.getOption('c', options);
/*  508:1186 */       if (classIndexString.length() != 0) {
/*  509:1187 */         if (classIndexString.equals("first")) {
/*  510:1188 */           classIndex = 1;
/*  511:1189 */         } else if (classIndexString.equals("last")) {
/*  512:1190 */           classIndex = -1;
/*  513:     */         } else {
/*  514:1192 */           classIndex = Integer.parseInt(classIndexString);
/*  515:     */         }
/*  516:     */       }
/*  517:1195 */       String trainFileName = Utils.getOption('t', options);
/*  518:1196 */       objectInputFileName = Utils.getOption('l', options);
/*  519:1197 */       objectOutputFileName = Utils.getOption('d', options);
/*  520:1198 */       String testFileName = Utils.getOption('T', options);
/*  521:1199 */       String foldsString = Utils.getOption('x', options);
/*  522:1200 */       if (foldsString.length() != 0) {
/*  523:1201 */         folds = Integer.parseInt(foldsString);
/*  524:     */       }
/*  525:1203 */       String seedString = Utils.getOption('s', options);
/*  526:1204 */       if (seedString.length() != 0) {
/*  527:1205 */         seed = Integer.parseInt(seedString);
/*  528:     */       }
/*  529:1207 */       if (trainFileName.length() == 0)
/*  530:     */       {
/*  531:1208 */         if (objectInputFileName.length() == 0) {
/*  532:1209 */           throw new Exception("No training file and no object input file given.");
/*  533:     */         }
/*  534:1212 */         if (testFileName.length() == 0) {
/*  535:1213 */           throw new Exception("No training file and no test file given.");
/*  536:     */         }
/*  537:     */       }
/*  538:1215 */       else if ((objectInputFileName.length() != 0) && ((!(classifier instanceof UpdateableClassifier)) || (testFileName.length() == 0)))
/*  539:     */       {
/*  540:1218 */         throw new Exception("Classifier not incremental, or no test file provided: can't use both train and model file.");
/*  541:     */       }
/*  542:     */       try
/*  543:     */       {
/*  544:1222 */         if (trainFileName.length() != 0)
/*  545:     */         {
/*  546:1223 */           trainSetPresent = true;
/*  547:1224 */           trainSource = new ConverterUtils.DataSource(trainFileName);
/*  548:     */         }
/*  549:1226 */         if (testFileName.length() != 0)
/*  550:     */         {
/*  551:1227 */           testSetPresent = true;
/*  552:1228 */           testSource = new ConverterUtils.DataSource(testFileName);
/*  553:     */         }
/*  554:1230 */         if (objectInputFileName.length() != 0) {
/*  555:1231 */           if (objectInputFileName.endsWith(".xml"))
/*  556:     */           {
/*  557:1234 */             objectInputStream = null;
/*  558:1235 */             xmlInputStream = null;
/*  559:     */           }
/*  560:     */           else
/*  561:     */           {
/*  562:1237 */             InputStream is = new FileInputStream(objectInputFileName);
/*  563:1238 */             if (objectInputFileName.endsWith(".gz")) {
/*  564:1239 */               is = new GZIPInputStream(is);
/*  565:     */             }
/*  566:1242 */             if ((!objectInputFileName.endsWith(".koml")) || (!KOML.isPresent()))
/*  567:     */             {
/*  568:1243 */               objectInputStream = new ObjectInputStream(is);
/*  569:1244 */               xmlInputStream = null;
/*  570:     */             }
/*  571:     */             else
/*  572:     */             {
/*  573:1246 */               objectInputStream = null;
/*  574:1247 */               xmlInputStream = new BufferedInputStream(is);
/*  575:     */             }
/*  576:     */           }
/*  577:     */         }
/*  578:     */       }
/*  579:     */       catch (Exception e)
/*  580:     */       {
/*  581:1252 */         throw new Exception("Can't open file " + e.getMessage() + '.');
/*  582:     */       }
/*  583:1254 */       if (testSetPresent)
/*  584:     */       {
/*  585:1255 */         template = test = testSource.getStructure();
/*  586:1256 */         if (classIndex != -1) {
/*  587:1257 */           test.setClassIndex(classIndex - 1);
/*  588:1259 */         } else if ((test.classIndex() == -1) || (classIndexString.length() != 0)) {
/*  589:1260 */           test.setClassIndex(test.numAttributes() - 1);
/*  590:     */         }
/*  591:1263 */         actualClassIndex = test.classIndex();
/*  592:     */       }
/*  593:     */       else
/*  594:     */       {
/*  595:1266 */         splitPercentageString = Utils.getOption("split-percentage", options);
/*  596:1267 */         if (splitPercentageString.length() != 0)
/*  597:     */         {
/*  598:1268 */           if (foldsString.length() != 0) {
/*  599:1269 */             throw new Exception("Percentage split cannot be used in conjunction with cross-validation ('-x').");
/*  600:     */           }
/*  601:1273 */           splitPercentage = Double.parseDouble(splitPercentageString);
/*  602:1274 */           if ((splitPercentage <= 0.0D) || (splitPercentage >= 100.0D)) {
/*  603:1275 */             throw new Exception("Percentage split value needs be >0 and <100.");
/*  604:     */           }
/*  605:     */         }
/*  606:     */         else
/*  607:     */         {
/*  608:1278 */           splitPercentage = -1.0D;
/*  609:     */         }
/*  610:1280 */         preserveOrder = Utils.getFlag("preserve-order", options);
/*  611:1281 */         if ((preserveOrder) && 
/*  612:1282 */           (splitPercentage == -1.0D)) {
/*  613:1283 */           throw new Exception("Percentage split ('-split-percentage') is missing.");
/*  614:     */         }
/*  615:1288 */         if (splitPercentage > 0.0D)
/*  616:     */         {
/*  617:1289 */           testSetPresent = true;
/*  618:1290 */           Instances tmpInst = trainSource.getDataSet(actualClassIndex);
/*  619:1291 */           if (!preserveOrder) {
/*  620:1292 */             tmpInst.randomize(new Random(seed));
/*  621:     */           }
/*  622:1294 */           int trainSize = (int)Math.round(tmpInst.numInstances() * splitPercentage / 100.0D);
/*  623:     */           
/*  624:1296 */           int testSize = tmpInst.numInstances() - trainSize;
/*  625:1297 */           Instances trainInst = new Instances(tmpInst, 0, trainSize);
/*  626:1298 */           Instances testInst = new Instances(tmpInst, trainSize, testSize);
/*  627:1299 */           trainSource = new ConverterUtils.DataSource(trainInst);
/*  628:1300 */           testSource = new ConverterUtils.DataSource(testInst);
/*  629:1301 */           template = test = testSource.getStructure();
/*  630:1302 */           if (classIndex != -1) {
/*  631:1303 */             test.setClassIndex(classIndex - 1);
/*  632:1305 */           } else if ((test.classIndex() == -1) || (classIndexString.length() != 0)) {
/*  633:1306 */             test.setClassIndex(test.numAttributes() - 1);
/*  634:     */           }
/*  635:1309 */           actualClassIndex = test.classIndex();
/*  636:     */         }
/*  637:     */       }
/*  638:1312 */       if (trainSetPresent)
/*  639:     */       {
/*  640:1313 */         template = train = trainSource.getStructure();
/*  641:1314 */         if (classIndex != -1) {
/*  642:1315 */           train.setClassIndex(classIndex - 1);
/*  643:1317 */         } else if ((train.classIndex() == -1) || (classIndexString.length() != 0)) {
/*  644:1318 */           train.setClassIndex(train.numAttributes() - 1);
/*  645:     */         }
/*  646:1321 */         actualClassIndex = train.classIndex();
/*  647:1322 */         if ((!(classifier instanceof InputMappedClassifier)) && 
/*  648:1323 */           (testSetPresent) && (!test.equalHeaders(train))) {
/*  649:1324 */           throw new IllegalArgumentException("Train and test file not compatible!\n" + test.equalHeadersMsg(train));
/*  650:     */         }
/*  651:     */       }
/*  652:1330 */       if (template == null) {
/*  653:1331 */         throw new Exception("No actual dataset provided to use as template");
/*  654:     */       }
/*  655:1333 */       costMatrix = handleCostOption(Utils.getOption('m', options), template.numClasses());
/*  656:     */       
/*  657:     */ 
/*  658:1336 */       classStatistics = !Utils.getFlag("do-not-output-per-class-statistics", options);
/*  659:     */       
/*  660:1338 */       noOutput = Utils.getFlag('o', options);
/*  661:1339 */       trainStatistics = !Utils.getFlag('v', options);
/*  662:1340 */       printComplexityStatistics = Utils.getFlag('k', options);
/*  663:1341 */       printMargins = Utils.getFlag('r', options);
/*  664:1342 */       printGraph = Utils.getFlag('g', options);
/*  665:1343 */       sourceClass = Utils.getOption('z', options);
/*  666:1344 */       printSource = sourceClass.length() != 0;
/*  667:1345 */       thresholdFile = Utils.getOption("threshold-file", options);
/*  668:1346 */       thresholdLabel = Utils.getOption("threshold-label", options);
/*  669:1347 */       forceBatchTraining = Utils.getFlag("force-batch-training", options);
/*  670:     */       
/*  671:1349 */       String classifications = Utils.getOption("classifications", options);
/*  672:1350 */       String classificationsOld = Utils.getOption("p", options);
/*  673:1351 */       if (classifications.length() > 0)
/*  674:     */       {
/*  675:1352 */         noOutput = true;
/*  676:1353 */         classificationOutput = AbstractOutput.fromCommandline(classifications);
/*  677:1354 */         if (classificationOutput == null) {
/*  678:1355 */           throw new Exception("Failed to instantiate class for classification output: " + classifications);
/*  679:     */         }
/*  680:1359 */         classificationOutput.setHeader(template);
/*  681:     */       }
/*  682:1362 */       else if (classificationsOld.length() > 0)
/*  683:     */       {
/*  684:1363 */         noOutput = true;
/*  685:1364 */         classificationOutput = new PlainText();
/*  686:1365 */         classificationOutput.setHeader(template);
/*  687:1366 */         if (!classificationsOld.equals("0")) {
/*  688:1367 */           classificationOutput.setAttributes(classificationsOld);
/*  689:     */         }
/*  690:1369 */         classificationOutput.setOutputDistribution(Utils.getFlag("distribution", options));
/*  691:     */       }
/*  692:1374 */       else if (Utils.getFlag("distribution", options))
/*  693:     */       {
/*  694:1375 */         throw new Exception("Cannot print distribution without '-p' option!");
/*  695:     */       }
/*  696:1378 */       discardPredictions = Utils.getFlag("no-predictions", options);
/*  697:1379 */       if ((discardPredictions) && (classificationOutput != null)) {
/*  698:1380 */         throw new Exception("Cannot discard predictions ('-no-predictions') and output predictions at the same time ('-classifications/-p')!");
/*  699:     */       }
/*  700:1385 */       if ((!trainSetPresent) && (printComplexityStatistics)) {
/*  701:1386 */         throw new Exception("Cannot print complexity statistics ('-k') without training file ('-t')!");
/*  702:     */       }
/*  703:1392 */       if (objectInputFileName.length() != 0)
/*  704:     */       {
/*  705:1393 */         Utils.checkForRemainingOptions(options);
/*  706:     */       }
/*  707:1397 */       else if ((classifier instanceof OptionHandler))
/*  708:     */       {
/*  709:1398 */         for (String option : options) {
/*  710:1399 */           if (option.length() != 0)
/*  711:     */           {
/*  712:1400 */             if (schemeOptionsText == null) {
/*  713:1401 */               schemeOptionsText = new StringBuffer();
/*  714:     */             }
/*  715:1403 */             if (option.indexOf(' ') != -1) {
/*  716:1404 */               schemeOptionsText.append('"' + option + "\" ");
/*  717:     */             } else {
/*  718:1406 */               schemeOptionsText.append(option + " ");
/*  719:     */             }
/*  720:     */           }
/*  721:     */         }
/*  722:1410 */         ((OptionHandler)classifier).setOptions(options);
/*  723:     */       }
/*  724:1414 */       Utils.checkForRemainingOptions(options);
/*  725:     */     }
/*  726:     */     catch (Exception e)
/*  727:     */     {
/*  728:1416 */       throw new Exception("\nWeka exception: " + e.getMessage() + makeOptionString(classifier, false));
/*  729:     */     }
/*  730:1420 */     boolean serializedClassifierLoaded = false;
/*  731:1421 */     if (objectInputFileName.length() != 0) {
/*  732:1423 */       if (objectInputStream != null)
/*  733:     */       {
/*  734:1424 */         classifier = (Classifier)objectInputStream.readObject();
/*  735:     */         
/*  736:1426 */         Instances savedStructure = null;
/*  737:     */         try
/*  738:     */         {
/*  739:1428 */           savedStructure = (Instances)objectInputStream.readObject();
/*  740:     */         }
/*  741:     */         catch (Exception ex) {}
/*  742:1432 */         if (savedStructure != null) {
/*  743:1434 */           if (!template.equalHeaders(savedStructure)) {
/*  744:1435 */             throw new Exception("training and test set are not compatible\n" + template.equalHeadersMsg(savedStructure));
/*  745:     */           }
/*  746:     */         }
/*  747:1439 */         objectInputStream.close();
/*  748:1440 */         serializedClassifierLoaded = true;
/*  749:     */       }
/*  750:1441 */       else if (xmlInputStream != null)
/*  751:     */       {
/*  752:1444 */         classifier = (Classifier)KOML.read(xmlInputStream);
/*  753:1445 */         xmlInputStream.close();
/*  754:1446 */         serializedClassifierLoaded = true;
/*  755:     */       }
/*  756:     */     }
/*  757:1451 */     Evaluation trainingEvaluation = new Evaluation(new Instances(template, 0), costMatrix);
/*  758:     */     
/*  759:1453 */     Evaluation testingEvaluation = new Evaluation(new Instances(template, 0), costMatrix);
/*  760:1455 */     if ((classifier instanceof InputMappedClassifier))
/*  761:     */     {
/*  762:1456 */       Instances mappedClassifierHeader = ((InputMappedClassifier)classifier).getModelHeader(new Instances(template, 0));
/*  763:     */       
/*  764:     */ 
/*  765:     */ 
/*  766:1460 */       trainingEvaluation = new Evaluation(new Instances(mappedClassifierHeader, 0), costMatrix);
/*  767:     */       
/*  768:1462 */       testingEvaluation = new Evaluation(new Instances(mappedClassifierHeader, 0), costMatrix);
/*  769:     */     }
/*  770:1465 */     trainingEvaluation.setDiscardPredictions(discardPredictions);
/*  771:1466 */     trainingEvaluation.toggleEvalMetrics(toggleList);
/*  772:1467 */     testingEvaluation.setDiscardPredictions(discardPredictions);
/*  773:1468 */     testingEvaluation.toggleEvalMetrics(toggleList);
/*  774:1471 */     if (!trainSetPresent) {
/*  775:1472 */       testingEvaluation.useNoPriors();
/*  776:     */     }
/*  777:1475 */     if (!serializedClassifierLoaded) {
/*  778:1477 */       classifierBackup = AbstractClassifier.makeCopy(classifier);
/*  779:     */     }
/*  780:1481 */     if (((classifier instanceof UpdateableClassifier)) && ((testSetPresent) || (noCrossValidation)) && (costMatrix == null) && (trainSetPresent) && (!forceBatchTraining))
/*  781:     */     {
/*  782:1485 */       trainingEvaluation.setPriors(train);
/*  783:1486 */       testingEvaluation.setPriors(train);
/*  784:1487 */       trainTimeStart = System.currentTimeMillis();
/*  785:1488 */       if (objectInputFileName.length() == 0) {
/*  786:1489 */         classifier.buildClassifier(train);
/*  787:     */       }
/*  788:1492 */       while (trainSource.hasMoreElements(train))
/*  789:     */       {
/*  790:1493 */         Instance trainInst = trainSource.nextElement(train);
/*  791:1494 */         trainingEvaluation.updatePriors(trainInst);
/*  792:1495 */         testingEvaluation.updatePriors(trainInst);
/*  793:1496 */         ((UpdateableClassifier)classifier).updateClassifier(trainInst);
/*  794:     */       }
/*  795:1498 */       if ((classifier instanceof UpdateableBatchProcessor)) {
/*  796:1499 */         ((UpdateableBatchProcessor)classifier).batchFinished();
/*  797:     */       }
/*  798:1501 */       trainTimeElapsed = System.currentTimeMillis() - trainTimeStart;
/*  799:     */     }
/*  800:1502 */     else if (objectInputFileName.length() == 0)
/*  801:     */     {
/*  802:1504 */       Instances tempTrain = trainSource.getDataSet(actualClassIndex);
/*  803:1506 */       if (((classifier instanceof InputMappedClassifier)) && (!trainingEvaluation.getHeader().equalHeaders(tempTrain)))
/*  804:     */       {
/*  805:1512 */         Instances mappedClassifierDataset = ((InputMappedClassifier)classifier).getModelHeader(new Instances(template, 0));
/*  806:1515 */         for (int zz = 0; zz < tempTrain.numInstances(); zz++)
/*  807:     */         {
/*  808:1516 */           Instance mapped = ((InputMappedClassifier)classifier).constructMappedInstance(tempTrain.instance(zz));
/*  809:     */           
/*  810:     */ 
/*  811:1519 */           mappedClassifierDataset.add(mapped);
/*  812:     */         }
/*  813:1521 */         tempTrain = mappedClassifierDataset;
/*  814:     */       }
/*  815:1524 */       trainingEvaluation.setPriors(tempTrain);
/*  816:1525 */       testingEvaluation.setPriors(tempTrain);
/*  817:1526 */       trainTimeStart = System.currentTimeMillis();
/*  818:1527 */       classifier.buildClassifier(tempTrain);
/*  819:1528 */       trainTimeElapsed = System.currentTimeMillis() - trainTimeStart;
/*  820:     */     }
/*  821:1532 */     if ((classificationOutput != null) && 
/*  822:1533 */       ((classifier instanceof InputMappedClassifier))) {
/*  823:1534 */       classificationOutput.setHeader(trainingEvaluation.getHeader());
/*  824:     */     }
/*  825:1539 */     if (objectOutputFileName.length() != 0)
/*  826:     */     {
/*  827:1540 */       OutputStream os = new FileOutputStream(objectOutputFileName);
/*  828:1542 */       if ((!objectOutputFileName.endsWith(".xml")) && ((!objectOutputFileName.endsWith(".koml")) || (!KOML.isPresent())))
/*  829:     */       {
/*  830:1544 */         if (objectOutputFileName.endsWith(".gz")) {
/*  831:1545 */           os = new GZIPOutputStream(os);
/*  832:     */         }
/*  833:1547 */         ObjectOutputStream objectOutputStream = new ObjectOutputStream(os);
/*  834:1548 */         objectOutputStream.writeObject(classifier);
/*  835:1549 */         if (template != null) {
/*  836:1550 */           objectOutputStream.writeObject(template);
/*  837:     */         }
/*  838:1552 */         objectOutputStream.flush();
/*  839:1553 */         objectOutputStream.close();
/*  840:     */       }
/*  841:     */       else
/*  842:     */       {
/*  843:1557 */         BufferedOutputStream xmlOutputStream = new BufferedOutputStream(os);
/*  844:1558 */         if (objectOutputFileName.endsWith(".xml"))
/*  845:     */         {
/*  846:1559 */           XMLSerialization xmlSerial = new XMLClassifier();
/*  847:1560 */           xmlSerial.write(xmlOutputStream, classifier);
/*  848:     */         }
/*  849:1564 */         else if (objectOutputFileName.endsWith(".koml"))
/*  850:     */         {
/*  851:1565 */           KOML.write(xmlOutputStream, classifier);
/*  852:     */         }
/*  853:1567 */         xmlOutputStream.close();
/*  854:     */       }
/*  855:     */     }
/*  856:1572 */     if (((classifier instanceof Drawable)) && (printGraph)) {
/*  857:1573 */       return ((Drawable)classifier).graph();
/*  858:     */     }
/*  859:1577 */     if (((classifier instanceof Sourcable)) && (printSource)) {
/*  860:1578 */       return wekaStaticWrapper((Sourcable)classifier, sourceClass);
/*  861:     */     }
/*  862:1582 */     if ((!noOutput) && (!printMargins))
/*  863:     */     {
/*  864:1583 */       if (((classifier instanceof OptionHandler)) && 
/*  865:1584 */         (schemeOptionsText != null))
/*  866:     */       {
/*  867:1585 */         text.append("\nOptions: " + schemeOptionsText);
/*  868:1586 */         text.append("\n");
/*  869:     */       }
/*  870:1589 */       text.append("\n" + classifier.toString() + "\n");
/*  871:     */     }
/*  872:1592 */     if ((!printMargins) && (costMatrix != null))
/*  873:     */     {
/*  874:1593 */       text.append("\n=== Evaluation Cost Matrix ===\n\n");
/*  875:1594 */       text.append(costMatrix.toString());
/*  876:     */     }
/*  877:1598 */     if (classificationOutput != null)
/*  878:     */     {
/*  879:1599 */       ConverterUtils.DataSource source = testSource;
/*  880:1600 */       predsBuff = new StringBuffer();
/*  881:1601 */       classificationOutput.setBuffer(predsBuff);
/*  882:1603 */       if ((source == null) && (noCrossValidation))
/*  883:     */       {
/*  884:1604 */         source = trainSource;
/*  885:1605 */         predsBuff.append("\n=== Predictions on training data ===\n\n");
/*  886:     */       }
/*  887:     */       else
/*  888:     */       {
/*  889:1607 */         predsBuff.append("\n=== Predictions on test data ===\n\n");
/*  890:     */       }
/*  891:1609 */       if (source != null) {
/*  892:1610 */         classificationOutput.print(classifier, source);
/*  893:     */       }
/*  894:     */     }
/*  895:1615 */     if ((trainStatistics) && (trainSetPresent))
/*  896:     */     {
/*  897:1617 */       if (((classifier instanceof UpdateableClassifier)) && ((testSetPresent) || (noCrossValidation)) && (costMatrix == null) && (!forceBatchTraining))
/*  898:     */       {
/*  899:1623 */         trainSource.reset();
/*  900:     */         
/*  901:     */ 
/*  902:1626 */         train = trainSource.getStructure(actualClassIndex);
/*  903:1627 */         testTimeStart = System.currentTimeMillis();
/*  904:1629 */         while (trainSource.hasMoreElements(train))
/*  905:     */         {
/*  906:1630 */           Instance trainInst = trainSource.nextElement(train);
/*  907:1631 */           trainingEvaluation.evaluateModelOnce(classifier, trainInst);
/*  908:     */         }
/*  909:1633 */         testTimeElapsed = System.currentTimeMillis() - testTimeStart;
/*  910:     */       }
/*  911:     */       else
/*  912:     */       {
/*  913:1635 */         testTimeStart = System.currentTimeMillis();
/*  914:1636 */         trainingEvaluation.evaluateModel(classifier, trainSource.getDataSet(actualClassIndex), new Object[0]);
/*  915:     */         
/*  916:1638 */         testTimeElapsed = System.currentTimeMillis() - testTimeStart;
/*  917:     */       }
/*  918:1642 */       if (printMargins) {
/*  919:1643 */         return trainingEvaluation.toCumulativeMarginDistributionString();
/*  920:     */       }
/*  921:1645 */       if (classificationOutput == null)
/*  922:     */       {
/*  923:1646 */         text.append("\nTime taken to build model: " + Utils.doubleToString(trainTimeElapsed / 1000.0D, 2) + " seconds");
/*  924:1649 */         if (splitPercentage > 0.0D) {
/*  925:1650 */           text.append("\nTime taken to test model on training split: ");
/*  926:     */         } else {
/*  927:1652 */           text.append("\nTime taken to test model on training data: ");
/*  928:     */         }
/*  929:1654 */         text.append(Utils.doubleToString(testTimeElapsed / 1000.0D, 2) + " seconds");
/*  930:1657 */         if (splitPercentage > 0.0D) {
/*  931:1658 */           text.append(trainingEvaluation.toSummaryString("\n\n=== Error on training split ===\n", printComplexityStatistics));
/*  932:     */         } else {
/*  933:1662 */           text.append(trainingEvaluation.toSummaryString("\n\n=== Error on training data ===\n", printComplexityStatistics));
/*  934:     */         }
/*  935:1667 */         if (template.classAttribute().isNominal())
/*  936:     */         {
/*  937:1668 */           if (classStatistics) {
/*  938:1669 */             text.append("\n\n" + trainingEvaluation.toClassDetailsString());
/*  939:     */           }
/*  940:1671 */           text.append("\n\n" + trainingEvaluation.toMatrixString());
/*  941:     */         }
/*  942:     */       }
/*  943:     */     }
/*  944:1678 */     if (testSource != null)
/*  945:     */     {
/*  946:1680 */       testSource.reset();
/*  947:1682 */       if (((classifier instanceof BatchPredictor)) && (((BatchPredictor)classifier).implementsMoreEfficientBatchPrediction()))
/*  948:     */       {
/*  949:1685 */         testingEvaluation.evaluateModel(classifier, testSource.getDataSet(test.classIndex()), new Object[0]);
/*  950:     */       }
/*  951:     */       else
/*  952:     */       {
/*  953:1688 */         test = testSource.getStructure(test.classIndex());
/*  954:1690 */         while (testSource.hasMoreElements(test))
/*  955:     */         {
/*  956:1691 */           Instance testInst = testSource.nextElement(test);
/*  957:1692 */           testingEvaluation.evaluateModelOnceAndRecordPrediction(classifier, testInst);
/*  958:     */         }
/*  959:     */       }
/*  960:1697 */       if (splitPercentage > 0.0D)
/*  961:     */       {
/*  962:1698 */         if (classificationOutput == null) {
/*  963:1699 */           text.append("\n\n" + testingEvaluation.toSummaryString("=== Error on test split ===\n", printComplexityStatistics));
/*  964:     */         }
/*  965:     */       }
/*  966:1704 */       else if (classificationOutput == null) {
/*  967:1705 */         text.append("\n\n" + testingEvaluation.toSummaryString("=== Error on test data ===\n", printComplexityStatistics));
/*  968:     */       }
/*  969:     */     }
/*  970:1711 */     else if ((trainSource != null) && 
/*  971:1712 */       (!noCrossValidation))
/*  972:     */     {
/*  973:1714 */       Random random = new Random(seed);
/*  974:     */       
/*  975:1716 */       classifier = AbstractClassifier.makeCopy(classifierBackup);
/*  976:1717 */       if (classificationOutput == null)
/*  977:     */       {
/*  978:1718 */         testingEvaluation.crossValidateModel(classifier, trainSource.getDataSet(actualClassIndex), folds, random, new Object[0]);
/*  979:1720 */         if (template.classAttribute().isNumeric()) {
/*  980:1721 */           text.append("\n\n\n" + testingEvaluation.toSummaryString("=== Cross-validation ===\n", printComplexityStatistics));
/*  981:     */         } else {
/*  982:1725 */           text.append("\n\n\n" + testingEvaluation.toSummaryString("=== Stratified cross-validation ===\n", printComplexityStatistics));
/*  983:     */         }
/*  984:     */       }
/*  985:     */       else
/*  986:     */       {
/*  987:1730 */         predsBuff = new StringBuffer();
/*  988:1731 */         classificationOutput.setBuffer(predsBuff);
/*  989:1732 */         predsBuff.append("\n=== Predictions under cross-validation ===\n\n");
/*  990:1733 */         testingEvaluation.crossValidateModel(classifier, trainSource.getDataSet(actualClassIndex), folds, random, new Object[] { classificationOutput });
/*  991:     */       }
/*  992:     */     }
/*  993:1739 */     if ((template.classAttribute().isNominal()) && (classificationOutput == null) && ((!noCrossValidation) || (testSource != null)))
/*  994:     */     {
/*  995:1741 */       if (classStatistics) {
/*  996:1742 */         text.append("\n\n" + testingEvaluation.toClassDetailsString());
/*  997:     */       }
/*  998:1744 */       text.append("\n\n" + testingEvaluation.toMatrixString());
/*  999:     */     }
/* 1000:1748 */     if (predsBuff != null) {
/* 1001:1749 */       text.append("\n" + predsBuff);
/* 1002:     */     }
/* 1003:1752 */     if ((thresholdFile.length() != 0) && (template.classAttribute().isNominal()))
/* 1004:     */     {
/* 1005:1753 */       int labelIndex = 0;
/* 1006:1754 */       if (thresholdLabel.length() != 0) {
/* 1007:1755 */         labelIndex = template.classAttribute().indexOfValue(thresholdLabel);
/* 1008:     */       }
/* 1009:1757 */       if (labelIndex == -1) {
/* 1010:1758 */         throw new IllegalArgumentException("Class label '" + thresholdLabel + "' is unknown!");
/* 1011:     */       }
/* 1012:1761 */       ThresholdCurve tc = new ThresholdCurve();
/* 1013:1762 */       Instances result = tc.getCurve(testingEvaluation.predictions(), labelIndex);
/* 1014:     */       
/* 1015:1764 */       ConverterUtils.DataSink.write(thresholdFile, result);
/* 1016:     */     }
/* 1017:1767 */     return text.toString();
/* 1018:     */   }
/* 1019:     */   
/* 1020:     */   protected static CostMatrix handleCostOption(String costFileName, int numClasses)
/* 1021:     */     throws Exception
/* 1022:     */   {
/* 1023:1782 */     if ((costFileName != null) && (costFileName.length() != 0))
/* 1024:     */     {
/* 1025:1783 */       System.out.println("NOTE: The behaviour of the -m option has changed between WEKA 3.0 and WEKA 3.1. -m now carries out cost-sensitive *evaluation* only. For cost-sensitive *prediction*, use one of the cost-sensitive metaschemes such as weka.classifiers.meta.CostSensitiveClassifier or weka.classifiers.meta.MetaCost");
/* 1026:     */       
/* 1027:     */ 
/* 1028:     */ 
/* 1029:     */ 
/* 1030:     */ 
/* 1031:     */ 
/* 1032:     */ 
/* 1033:1791 */       Reader costReader = null;
/* 1034:     */       try
/* 1035:     */       {
/* 1036:1793 */         costReader = new BufferedReader(new FileReader(costFileName));
/* 1037:     */       }
/* 1038:     */       catch (Exception e)
/* 1039:     */       {
/* 1040:1795 */         throw new Exception("Can't open file " + e.getMessage() + '.');
/* 1041:     */       }
/* 1042:     */       try
/* 1043:     */       {
/* 1044:1799 */         return new CostMatrix(costReader);
/* 1045:     */       }
/* 1046:     */       catch (Exception ex)
/* 1047:     */       {
/* 1048:     */         try
/* 1049:     */         {
/* 1050:     */           try
/* 1051:     */           {
/* 1052:1805 */             costReader.close();
/* 1053:1806 */             costReader = new BufferedReader(new FileReader(costFileName));
/* 1054:     */           }
/* 1055:     */           catch (Exception e)
/* 1056:     */           {
/* 1057:1808 */             throw new Exception("Can't open file " + e.getMessage() + '.');
/* 1058:     */           }
/* 1059:1810 */           CostMatrix costMatrix = new CostMatrix(numClasses);
/* 1060:     */           
/* 1061:1812 */           costMatrix.readOldFormat(costReader);
/* 1062:1813 */           return costMatrix;
/* 1063:     */         }
/* 1064:     */         catch (Exception e2)
/* 1065:     */         {
/* 1066:1818 */           throw ex;
/* 1067:     */         }
/* 1068:     */       }
/* 1069:     */     }
/* 1070:1822 */     return null;
/* 1071:     */   }
/* 1072:     */   
/* 1073:     */   public double[] evaluateModel(Classifier classifier, Instances data, Object... forPredictionsPrinting)
/* 1074:     */     throws Exception
/* 1075:     */   {
/* 1076:1844 */     AbstractOutput classificationOutput = null;
/* 1077:     */     
/* 1078:1846 */     double[] predictions = new double[data.numInstances()];
/* 1079:1848 */     if (forPredictionsPrinting.length > 0) {
/* 1080:1849 */       classificationOutput = (AbstractOutput)forPredictionsPrinting[0];
/* 1081:     */     }
/* 1082:1852 */     if (((classifier instanceof BatchPredictor)) && (((BatchPredictor)classifier).implementsMoreEfficientBatchPrediction()))
/* 1083:     */     {
/* 1084:1855 */       Instances dataPred = new Instances(data);
/* 1085:1856 */       for (int i = 0; i < data.numInstances(); i++) {
/* 1086:1857 */         dataPred.instance(i).setClassMissing();
/* 1087:     */       }
/* 1088:1859 */       double[][] preds = ((BatchPredictor)classifier).distributionsForInstances(dataPred);
/* 1089:1861 */       for (int i = 0; i < data.numInstances(); i++)
/* 1090:     */       {
/* 1091:1862 */         double[] p = preds[i];
/* 1092:     */         
/* 1093:1864 */         predictions[i] = evaluationForSingleInstance(p, data.instance(i), true);
/* 1094:1866 */         if (classificationOutput != null) {
/* 1095:1867 */           classificationOutput.printClassification(p, data.instance(i), i);
/* 1096:     */         }
/* 1097:     */       }
/* 1098:     */     }
/* 1099:     */     else
/* 1100:     */     {
/* 1101:1873 */       for (int i = 0; i < data.numInstances(); i++)
/* 1102:     */       {
/* 1103:1874 */         predictions[i] = evaluateModelOnceAndRecordPrediction(classifier, data.instance(i));
/* 1104:1876 */         if (classificationOutput != null) {
/* 1105:1877 */           classificationOutput.printClassification(classifier, data.instance(i), i);
/* 1106:     */         }
/* 1107:     */       }
/* 1108:     */     }
/* 1109:1883 */     return predictions;
/* 1110:     */   }
/* 1111:     */   
/* 1112:     */   public double evaluationForSingleInstance(double[] dist, Instance instance, boolean storePredictions)
/* 1113:     */     throws Exception
/* 1114:     */   {
/* 1115:     */     double pred;
/* 1116:1900 */     if (this.m_ClassIsNominal)
/* 1117:     */     {
/* 1118:1901 */       double pred = Utils.maxIndex(dist);
/* 1119:1902 */       if (dist[((int)pred)] <= 0.0D) {
/* 1120:1903 */         pred = Utils.missingValue();
/* 1121:     */       }
/* 1122:1905 */       updateStatsForClassifier(dist, instance);
/* 1123:1906 */       if ((storePredictions) && (!this.m_DiscardPredictions))
/* 1124:     */       {
/* 1125:1907 */         if (this.m_Predictions == null) {
/* 1126:1908 */           this.m_Predictions = new ArrayList();
/* 1127:     */         }
/* 1128:1910 */         this.m_Predictions.add(new NominalPrediction(instance.classValue(), dist, instance.weight()));
/* 1129:     */       }
/* 1130:     */     }
/* 1131:     */     else
/* 1132:     */     {
/* 1133:1914 */       pred = dist[0];
/* 1134:1915 */       updateStatsForPredictor(pred, instance);
/* 1135:1916 */       if ((storePredictions) && (!this.m_DiscardPredictions))
/* 1136:     */       {
/* 1137:1917 */         if (this.m_Predictions == null) {
/* 1138:1918 */           this.m_Predictions = new ArrayList();
/* 1139:     */         }
/* 1140:1920 */         this.m_Predictions.add(new NumericPrediction(instance.classValue(), pred, instance.weight()));
/* 1141:     */       }
/* 1142:     */     }
/* 1143:1925 */     return pred;
/* 1144:     */   }
/* 1145:     */   
/* 1146:     */   protected double evaluationForSingleInstance(Classifier classifier, Instance instance, boolean storePredictions)
/* 1147:     */     throws Exception
/* 1148:     */   {
/* 1149:1941 */     Instance classMissing = (Instance)instance.copy();
/* 1150:1942 */     classMissing.setDataset(instance.dataset());
/* 1151:1944 */     if ((classifier instanceof InputMappedClassifier))
/* 1152:     */     {
/* 1153:1945 */       instance = (Instance)instance.copy();
/* 1154:1946 */       instance = ((InputMappedClassifier)classifier).constructMappedInstance(instance);
/* 1155:     */       
/* 1156:     */ 
/* 1157:     */ 
/* 1158:1950 */       int mappedClass = ((InputMappedClassifier)classifier).getMappedClassIndex();
/* 1159:     */       
/* 1160:     */ 
/* 1161:1953 */       classMissing.setMissing(mappedClass);
/* 1162:     */     }
/* 1163:     */     else
/* 1164:     */     {
/* 1165:1955 */       classMissing.setClassMissing();
/* 1166:     */     }
/* 1167:1959 */     double pred = evaluationForSingleInstance(classifier.distributionForInstance(classMissing), instance, storePredictions);
/* 1168:1967 */     if ((!this.m_ClassIsNominal) && 
/* 1169:1968 */       (!instance.classIsMissing()) && (!Utils.isMissingValue(pred)))
/* 1170:     */     {
/* 1171:1969 */       if ((classifier instanceof IntervalEstimator)) {
/* 1172:1970 */         updateStatsForIntervalEstimator((IntervalEstimator)classifier, classMissing, instance.classValue());
/* 1173:     */       } else {
/* 1174:1973 */         this.m_CoverageStatisticsAvailable = false;
/* 1175:     */       }
/* 1176:1975 */       if ((classifier instanceof ConditionalDensityEstimator)) {
/* 1177:1976 */         updateStatsForConditionalDensityEstimator((ConditionalDensityEstimator)classifier, classMissing, instance.classValue());
/* 1178:     */       } else {
/* 1179:1980 */         this.m_ComplexityStatisticsAvailable = false;
/* 1180:     */       }
/* 1181:     */     }
/* 1182:1984 */     return pred;
/* 1183:     */   }
/* 1184:     */   
/* 1185:     */   public double evaluateModelOnceAndRecordPrediction(Classifier classifier, Instance instance)
/* 1186:     */     throws Exception
/* 1187:     */   {
/* 1188:1999 */     return evaluationForSingleInstance(classifier, instance, true);
/* 1189:     */   }
/* 1190:     */   
/* 1191:     */   public double evaluateModelOnce(Classifier classifier, Instance instance)
/* 1192:     */     throws Exception
/* 1193:     */   {
/* 1194:2014 */     return evaluationForSingleInstance(classifier, instance, false);
/* 1195:     */   }
/* 1196:     */   
/* 1197:     */   public double evaluateModelOnce(double[] dist, Instance instance)
/* 1198:     */     throws Exception
/* 1199:     */   {
/* 1200:2028 */     return evaluationForSingleInstance(dist, instance, false);
/* 1201:     */   }
/* 1202:     */   
/* 1203:     */   public double evaluateModelOnceAndRecordPrediction(double[] dist, Instance instance)
/* 1204:     */     throws Exception
/* 1205:     */   {
/* 1206:2042 */     return evaluationForSingleInstance(dist, instance, true);
/* 1207:     */   }
/* 1208:     */   
/* 1209:     */   public void evaluateModelOnce(double prediction, Instance instance)
/* 1210:     */     throws Exception
/* 1211:     */   {
/* 1212:2055 */     evaluateModelOnce(makeDistribution(prediction), instance);
/* 1213:     */   }
/* 1214:     */   
/* 1215:     */   public ArrayList<Prediction> predictions()
/* 1216:     */   {
/* 1217:2066 */     if (this.m_DiscardPredictions) {
/* 1218:2067 */       return null;
/* 1219:     */     }
/* 1220:2069 */     return this.m_Predictions;
/* 1221:     */   }
/* 1222:     */   
/* 1223:     */   public static String wekaStaticWrapper(Sourcable classifier, String className)
/* 1224:     */     throws Exception
/* 1225:     */   {
/* 1226:2086 */     StringBuffer result = new StringBuffer();
/* 1227:2087 */     String staticClassifier = classifier.toSource(className);
/* 1228:     */     
/* 1229:2089 */     result.append("// Generated with Weka " + Version.VERSION + "\n");
/* 1230:2090 */     result.append("//\n");
/* 1231:2091 */     result.append("// This code is public domain and comes with no warranty.\n");
/* 1232:     */     
/* 1233:2093 */     result.append("//\n");
/* 1234:2094 */     result.append("// Timestamp: " + new Date() + "\n");
/* 1235:2095 */     result.append("\n");
/* 1236:2096 */     result.append("package weka.classifiers;\n");
/* 1237:2097 */     result.append("\n");
/* 1238:2098 */     result.append("import weka.core.Attribute;\n");
/* 1239:2099 */     result.append("import weka.core.Capabilities;\n");
/* 1240:2100 */     result.append("import weka.core.Capabilities.Capability;\n");
/* 1241:2101 */     result.append("import weka.core.Instance;\n");
/* 1242:2102 */     result.append("import weka.core.Instances;\n");
/* 1243:2103 */     result.append("import weka.core.RevisionUtils;\n");
/* 1244:2104 */     result.append("import weka.classifiers.Classifier;\nimport weka.classifiers.AbstractClassifier;\n");
/* 1245:     */     
/* 1246:2106 */     result.append("\n");
/* 1247:2107 */     result.append("public class WekaWrapper\n");
/* 1248:2108 */     result.append("  extends AbstractClassifier {\n");
/* 1249:     */     
/* 1250:     */ 
/* 1251:2111 */     result.append("\n");
/* 1252:2112 */     result.append("  /**\n");
/* 1253:2113 */     result.append("   * Returns only the toString() method.\n");
/* 1254:2114 */     result.append("   *\n");
/* 1255:2115 */     result.append("   * @return a string describing the classifier\n");
/* 1256:2116 */     result.append("   */\n");
/* 1257:2117 */     result.append("  public String globalInfo() {\n");
/* 1258:2118 */     result.append("    return toString();\n");
/* 1259:2119 */     result.append("  }\n");
/* 1260:     */     
/* 1261:     */ 
/* 1262:2122 */     result.append("\n");
/* 1263:2123 */     result.append("  /**\n");
/* 1264:2124 */     result.append("   * Returns the capabilities of this classifier.\n");
/* 1265:2125 */     result.append("   *\n");
/* 1266:2126 */     result.append("   * @return the capabilities\n");
/* 1267:2127 */     result.append("   */\n");
/* 1268:2128 */     result.append("  public Capabilities getCapabilities() {\n");
/* 1269:2129 */     result.append(((Classifier)classifier).getCapabilities().toSource("result", 4));
/* 1270:     */     
/* 1271:2131 */     result.append("    return result;\n");
/* 1272:2132 */     result.append("  }\n");
/* 1273:     */     
/* 1274:     */ 
/* 1275:2135 */     result.append("\n");
/* 1276:2136 */     result.append("  /**\n");
/* 1277:2137 */     result.append("   * only checks the data against its capabilities.\n");
/* 1278:2138 */     result.append("   *\n");
/* 1279:2139 */     result.append("   * @param i the training data\n");
/* 1280:2140 */     result.append("   */\n");
/* 1281:2141 */     result.append("  public void buildClassifier(Instances i) throws Exception {\n");
/* 1282:     */     
/* 1283:2143 */     result.append("    // can classifier handle the data?\n");
/* 1284:2144 */     result.append("    getCapabilities().testWithFail(i);\n");
/* 1285:2145 */     result.append("  }\n");
/* 1286:     */     
/* 1287:     */ 
/* 1288:2148 */     result.append("\n");
/* 1289:2149 */     result.append("  /**\n");
/* 1290:2150 */     result.append("   * Classifies the given instance.\n");
/* 1291:2151 */     result.append("   *\n");
/* 1292:2152 */     result.append("   * @param i the instance to classify\n");
/* 1293:2153 */     result.append("   * @return the classification result\n");
/* 1294:2154 */     result.append("   */\n");
/* 1295:2155 */     result.append("  public double classifyInstance(Instance i) throws Exception {\n");
/* 1296:     */     
/* 1297:2157 */     result.append("    Object[] s = new Object[i.numAttributes()];\n");
/* 1298:2158 */     result.append("    \n");
/* 1299:2159 */     result.append("    for (int j = 0; j < s.length; j++) {\n");
/* 1300:2160 */     result.append("      if (!i.isMissing(j)) {\n");
/* 1301:2161 */     result.append("        if (i.attribute(j).isNominal())\n");
/* 1302:2162 */     result.append("          s[j] = new String(i.stringValue(j));\n");
/* 1303:2163 */     result.append("        else if (i.attribute(j).isNumeric())\n");
/* 1304:2164 */     result.append("          s[j] = new Double(i.value(j));\n");
/* 1305:2165 */     result.append("      }\n");
/* 1306:2166 */     result.append("    }\n");
/* 1307:2167 */     result.append("    \n");
/* 1308:2168 */     result.append("    // set class value to missing\n");
/* 1309:2169 */     result.append("    s[i.classIndex()] = null;\n");
/* 1310:2170 */     result.append("    \n");
/* 1311:2171 */     result.append("    return " + className + ".classify(s);\n");
/* 1312:2172 */     result.append("  }\n");
/* 1313:     */     
/* 1314:     */ 
/* 1315:2175 */     result.append("\n");
/* 1316:2176 */     result.append("  /**\n");
/* 1317:2177 */     result.append("   * Returns the revision string.\n");
/* 1318:2178 */     result.append("   * \n");
/* 1319:2179 */     result.append("   * @return        the revision\n");
/* 1320:2180 */     result.append("   */\n");
/* 1321:2181 */     result.append("  public String getRevision() {\n");
/* 1322:2182 */     result.append("    return RevisionUtils.extract(\"1.0\");\n");
/* 1323:2183 */     result.append("  }\n");
/* 1324:     */     
/* 1325:     */ 
/* 1326:2186 */     result.append("\n");
/* 1327:2187 */     result.append("  /**\n");
/* 1328:2188 */     result.append("   * Returns only the classnames and what classifier it is based on.\n");
/* 1329:     */     
/* 1330:2190 */     result.append("   *\n");
/* 1331:2191 */     result.append("   * @return a short description\n");
/* 1332:2192 */     result.append("   */\n");
/* 1333:2193 */     result.append("  public String toString() {\n");
/* 1334:2194 */     result.append("    return \"Auto-generated classifier wrapper, based on " + classifier.getClass().getName() + " (generated with Weka " + Version.VERSION + ").\\n" + "\" + this.getClass().getName() + \"/" + className + "\";\n");
/* 1335:     */     
/* 1336:     */ 
/* 1337:     */ 
/* 1338:2198 */     result.append("  }\n");
/* 1339:     */     
/* 1340:     */ 
/* 1341:2201 */     result.append("\n");
/* 1342:2202 */     result.append("  /**\n");
/* 1343:2203 */     result.append("   * Runs the classfier from commandline.\n");
/* 1344:2204 */     result.append("   *\n");
/* 1345:2205 */     result.append("   * @param args the commandline arguments\n");
/* 1346:2206 */     result.append("   */\n");
/* 1347:2207 */     result.append("  public static void main(String args[]) {\n");
/* 1348:2208 */     result.append("    runClassifier(new WekaWrapper(), args);\n");
/* 1349:2209 */     result.append("  }\n");
/* 1350:2210 */     result.append("}\n");
/* 1351:     */     
/* 1352:     */ 
/* 1353:2213 */     result.append("\n");
/* 1354:2214 */     result.append(staticClassifier);
/* 1355:     */     
/* 1356:2216 */     return result.toString();
/* 1357:     */   }
/* 1358:     */   
/* 1359:     */   public final double numInstances()
/* 1360:     */   {
/* 1361:2227 */     return this.m_WithClass;
/* 1362:     */   }
/* 1363:     */   
/* 1364:     */   public final double coverageOfTestCasesByPredictedRegions()
/* 1365:     */   {
/* 1366:2238 */     if (!this.m_CoverageStatisticsAvailable) {
/* 1367:2239 */       return (0.0D / 0.0D);
/* 1368:     */     }
/* 1369:2242 */     return 100.0D * this.m_TotalCoverage / this.m_WithClass;
/* 1370:     */   }
/* 1371:     */   
/* 1372:     */   public final double sizeOfPredictedRegions()
/* 1373:     */   {
/* 1374:2254 */     if ((this.m_NoPriors) || (!this.m_CoverageStatisticsAvailable)) {
/* 1375:2255 */       return (0.0D / 0.0D);
/* 1376:     */     }
/* 1377:2258 */     return 100.0D * this.m_TotalSizeOfRegions / this.m_WithClass;
/* 1378:     */   }
/* 1379:     */   
/* 1380:     */   public final double incorrect()
/* 1381:     */   {
/* 1382:2270 */     return this.m_Incorrect;
/* 1383:     */   }
/* 1384:     */   
/* 1385:     */   public final double pctIncorrect()
/* 1386:     */   {
/* 1387:2281 */     return 100.0D * this.m_Incorrect / this.m_WithClass;
/* 1388:     */   }
/* 1389:     */   
/* 1390:     */   public final double totalCost()
/* 1391:     */   {
/* 1392:2292 */     return this.m_TotalCost;
/* 1393:     */   }
/* 1394:     */   
/* 1395:     */   public final double avgCost()
/* 1396:     */   {
/* 1397:2303 */     return this.m_TotalCost / this.m_WithClass;
/* 1398:     */   }
/* 1399:     */   
/* 1400:     */   public final double correct()
/* 1401:     */   {
/* 1402:2315 */     return this.m_Correct;
/* 1403:     */   }
/* 1404:     */   
/* 1405:     */   public final double pctCorrect()
/* 1406:     */   {
/* 1407:2326 */     return 100.0D * this.m_Correct / this.m_WithClass;
/* 1408:     */   }
/* 1409:     */   
/* 1410:     */   public final double unclassified()
/* 1411:     */   {
/* 1412:2338 */     return this.m_Unclassified;
/* 1413:     */   }
/* 1414:     */   
/* 1415:     */   public final double pctUnclassified()
/* 1416:     */   {
/* 1417:2349 */     return 100.0D * this.m_Unclassified / this.m_WithClass;
/* 1418:     */   }
/* 1419:     */   
/* 1420:     */   public final double errorRate()
/* 1421:     */   {
/* 1422:2362 */     if (!this.m_ClassIsNominal) {
/* 1423:2363 */       return Math.sqrt(this.m_SumSqrErr / (this.m_WithClass - this.m_Unclassified));
/* 1424:     */     }
/* 1425:2365 */     if (this.m_CostMatrix == null) {
/* 1426:2366 */       return this.m_Incorrect / this.m_WithClass;
/* 1427:     */     }
/* 1428:2368 */     return avgCost();
/* 1429:     */   }
/* 1430:     */   
/* 1431:     */   public final double kappa()
/* 1432:     */   {
/* 1433:2379 */     double[] sumRows = new double[this.m_ConfusionMatrix.length];
/* 1434:2380 */     double[] sumColumns = new double[this.m_ConfusionMatrix.length];
/* 1435:2381 */     double sumOfWeights = 0.0D;
/* 1436:2382 */     for (int i = 0; i < this.m_ConfusionMatrix.length; i++) {
/* 1437:2383 */       for (int j = 0; j < this.m_ConfusionMatrix.length; j++)
/* 1438:     */       {
/* 1439:2384 */         sumRows[i] += this.m_ConfusionMatrix[i][j];
/* 1440:2385 */         sumColumns[j] += this.m_ConfusionMatrix[i][j];
/* 1441:2386 */         sumOfWeights += this.m_ConfusionMatrix[i][j];
/* 1442:     */       }
/* 1443:     */     }
/* 1444:2389 */     double correct = 0.0D;double chanceAgreement = 0.0D;
/* 1445:2390 */     for (int i = 0; i < this.m_ConfusionMatrix.length; i++)
/* 1446:     */     {
/* 1447:2391 */       chanceAgreement += sumRows[i] * sumColumns[i];
/* 1448:2392 */       correct += this.m_ConfusionMatrix[i][i];
/* 1449:     */     }
/* 1450:2394 */     chanceAgreement /= sumOfWeights * sumOfWeights;
/* 1451:2395 */     correct /= sumOfWeights;
/* 1452:2397 */     if (chanceAgreement < 1.0D) {
/* 1453:2398 */       return (correct - chanceAgreement) / (1.0D - chanceAgreement);
/* 1454:     */     }
/* 1455:2400 */     return 1.0D;
/* 1456:     */   }
/* 1457:     */   
/* 1458:     */   public final double correlationCoefficient()
/* 1459:     */     throws Exception
/* 1460:     */   {
/* 1461:2412 */     if (this.m_ClassIsNominal) {
/* 1462:2413 */       throw new Exception("Can't compute correlation coefficient: class is nominal!");
/* 1463:     */     }
/* 1464:2417 */     double correlation = 0.0D;
/* 1465:2418 */     double varActual = this.m_SumSqrClass - this.m_SumClass * this.m_SumClass / (this.m_WithClass - this.m_Unclassified);
/* 1466:     */     
/* 1467:2420 */     double varPredicted = this.m_SumSqrPredicted - this.m_SumPredicted * this.m_SumPredicted / (this.m_WithClass - this.m_Unclassified);
/* 1468:     */     
/* 1469:     */ 
/* 1470:2423 */     double varProd = this.m_SumClassPredicted - this.m_SumClass * this.m_SumPredicted / (this.m_WithClass - this.m_Unclassified);
/* 1471:2427 */     if (varActual * varPredicted <= 0.0D) {
/* 1472:2428 */       correlation = 0.0D;
/* 1473:     */     } else {
/* 1474:2430 */       correlation = varProd / Math.sqrt(varActual * varPredicted);
/* 1475:     */     }
/* 1476:2433 */     return correlation;
/* 1477:     */   }
/* 1478:     */   
/* 1479:     */   public final double meanAbsoluteError()
/* 1480:     */   {
/* 1481:2445 */     return this.m_SumAbsErr / (this.m_WithClass - this.m_Unclassified);
/* 1482:     */   }
/* 1483:     */   
/* 1484:     */   public final double meanPriorAbsoluteError()
/* 1485:     */   {
/* 1486:2455 */     if (this.m_NoPriors) {
/* 1487:2456 */       return (0.0D / 0.0D);
/* 1488:     */     }
/* 1489:2459 */     return this.m_SumPriorAbsErr / this.m_WithClass;
/* 1490:     */   }
/* 1491:     */   
/* 1492:     */   public final double relativeAbsoluteError()
/* 1493:     */     throws Exception
/* 1494:     */   {
/* 1495:2470 */     if (this.m_NoPriors) {
/* 1496:2471 */       return (0.0D / 0.0D);
/* 1497:     */     }
/* 1498:2474 */     return 100.0D * meanAbsoluteError() / meanPriorAbsoluteError();
/* 1499:     */   }
/* 1500:     */   
/* 1501:     */   public final double rootMeanSquaredError()
/* 1502:     */   {
/* 1503:2484 */     return Math.sqrt(this.m_SumSqrErr / (this.m_WithClass - this.m_Unclassified));
/* 1504:     */   }
/* 1505:     */   
/* 1506:     */   public final double rootMeanPriorSquaredError()
/* 1507:     */   {
/* 1508:2494 */     if (this.m_NoPriors) {
/* 1509:2495 */       return (0.0D / 0.0D);
/* 1510:     */     }
/* 1511:2498 */     return Math.sqrt(this.m_SumPriorSqrErr / this.m_WithClass);
/* 1512:     */   }
/* 1513:     */   
/* 1514:     */   public final double rootRelativeSquaredError()
/* 1515:     */   {
/* 1516:2508 */     if (this.m_NoPriors) {
/* 1517:2509 */       return (0.0D / 0.0D);
/* 1518:     */     }
/* 1519:2512 */     return 100.0D * rootMeanSquaredError() / rootMeanPriorSquaredError();
/* 1520:     */   }
/* 1521:     */   
/* 1522:     */   public final double priorEntropy()
/* 1523:     */     throws Exception
/* 1524:     */   {
/* 1525:2523 */     if (!this.m_ClassIsNominal) {
/* 1526:2524 */       throw new Exception("Can't compute entropy of class prior: class numeric!");
/* 1527:     */     }
/* 1528:2528 */     if (this.m_NoPriors) {
/* 1529:2529 */       return (0.0D / 0.0D);
/* 1530:     */     }
/* 1531:2532 */     double entropy = 0.0D;
/* 1532:2533 */     for (int i = 0; i < this.m_NumClasses; i++) {
/* 1533:2534 */       entropy -= this.m_ClassPriors[i] / this.m_ClassPriorsSum * Utils.log2(this.m_ClassPriors[i] / this.m_ClassPriorsSum);
/* 1534:     */     }
/* 1535:2538 */     return entropy;
/* 1536:     */   }
/* 1537:     */   
/* 1538:     */   public final double KBInformation()
/* 1539:     */     throws Exception
/* 1540:     */   {
/* 1541:2549 */     if (!this.m_ClassIsNominal) {
/* 1542:2550 */       throw new Exception("Can't compute K&B Info score: class numeric!");
/* 1543:     */     }
/* 1544:2553 */     if (this.m_NoPriors) {
/* 1545:2554 */       return (0.0D / 0.0D);
/* 1546:     */     }
/* 1547:2557 */     return this.m_SumKBInfo;
/* 1548:     */   }
/* 1549:     */   
/* 1550:     */   public final double KBMeanInformation()
/* 1551:     */     throws Exception
/* 1552:     */   {
/* 1553:2568 */     if (!this.m_ClassIsNominal) {
/* 1554:2569 */       throw new Exception("Can't compute K&B Info score: class numeric!");
/* 1555:     */     }
/* 1556:2572 */     if (this.m_NoPriors) {
/* 1557:2573 */       return (0.0D / 0.0D);
/* 1558:     */     }
/* 1559:2576 */     return this.m_SumKBInfo / (this.m_WithClass - this.m_Unclassified);
/* 1560:     */   }
/* 1561:     */   
/* 1562:     */   public final double KBRelativeInformation()
/* 1563:     */     throws Exception
/* 1564:     */   {
/* 1565:2587 */     if (!this.m_ClassIsNominal) {
/* 1566:2588 */       throw new Exception("Can't compute K&B Info score: class numeric!");
/* 1567:     */     }
/* 1568:2591 */     if (this.m_NoPriors) {
/* 1569:2592 */       return (0.0D / 0.0D);
/* 1570:     */     }
/* 1571:2595 */     return 100.0D * KBInformation() / priorEntropy();
/* 1572:     */   }
/* 1573:     */   
/* 1574:     */   public final double SFPriorEntropy()
/* 1575:     */   {
/* 1576:2605 */     if ((this.m_NoPriors) || (!this.m_ComplexityStatisticsAvailable)) {
/* 1577:2606 */       return (0.0D / 0.0D);
/* 1578:     */     }
/* 1579:2609 */     return this.m_SumPriorEntropy;
/* 1580:     */   }
/* 1581:     */   
/* 1582:     */   public final double SFMeanPriorEntropy()
/* 1583:     */   {
/* 1584:2619 */     if ((this.m_NoPriors) || (!this.m_ComplexityStatisticsAvailable)) {
/* 1585:2620 */       return (0.0D / 0.0D);
/* 1586:     */     }
/* 1587:2623 */     return this.m_SumPriorEntropy / this.m_WithClass;
/* 1588:     */   }
/* 1589:     */   
/* 1590:     */   public final double SFSchemeEntropy()
/* 1591:     */   {
/* 1592:2633 */     if (!this.m_ComplexityStatisticsAvailable) {
/* 1593:2634 */       return (0.0D / 0.0D);
/* 1594:     */     }
/* 1595:2637 */     return this.m_SumSchemeEntropy;
/* 1596:     */   }
/* 1597:     */   
/* 1598:     */   public final double SFMeanSchemeEntropy()
/* 1599:     */   {
/* 1600:2647 */     if (!this.m_ComplexityStatisticsAvailable) {
/* 1601:2648 */       return (0.0D / 0.0D);
/* 1602:     */     }
/* 1603:2651 */     return this.m_SumSchemeEntropy / (this.m_WithClass - this.m_Unclassified);
/* 1604:     */   }
/* 1605:     */   
/* 1606:     */   public final double SFEntropyGain()
/* 1607:     */   {
/* 1608:2662 */     if ((this.m_NoPriors) || (!this.m_ComplexityStatisticsAvailable)) {
/* 1609:2663 */       return (0.0D / 0.0D);
/* 1610:     */     }
/* 1611:2666 */     return this.m_SumPriorEntropy - this.m_SumSchemeEntropy;
/* 1612:     */   }
/* 1613:     */   
/* 1614:     */   public final double SFMeanEntropyGain()
/* 1615:     */   {
/* 1616:2677 */     if ((this.m_NoPriors) || (!this.m_ComplexityStatisticsAvailable)) {
/* 1617:2678 */       return (0.0D / 0.0D);
/* 1618:     */     }
/* 1619:2681 */     return (this.m_SumPriorEntropy - this.m_SumSchemeEntropy) / (this.m_WithClass - this.m_Unclassified);
/* 1620:     */   }
/* 1621:     */   
/* 1622:     */   public String toCumulativeMarginDistributionString()
/* 1623:     */     throws Exception
/* 1624:     */   {
/* 1625:2694 */     if (!this.m_ClassIsNominal) {
/* 1626:2695 */       throw new Exception("Class must be nominal for margin distributions");
/* 1627:     */     }
/* 1628:2697 */     String result = "";
/* 1629:2698 */     double cumulativeCount = 0.0D;
/* 1630:2700 */     for (int i = 0; i <= k_MarginResolution; i++) {
/* 1631:2701 */       if (this.m_MarginCounts[i] != 0.0D)
/* 1632:     */       {
/* 1633:2702 */         cumulativeCount += this.m_MarginCounts[i];
/* 1634:2703 */         double margin = i * 2.0D / k_MarginResolution - 1.0D;
/* 1635:2704 */         result = result + Utils.doubleToString(margin, 7, 3) + ' ' + Utils.doubleToString(cumulativeCount * 100.0D / this.m_WithClass, 7, 3) + '\n';
/* 1636:     */       }
/* 1637:2708 */       else if (i == 0)
/* 1638:     */       {
/* 1639:2709 */         result = Utils.doubleToString(-1.0D, 7, 3) + ' ' + Utils.doubleToString(0.0D, 7, 3) + '\n';
/* 1640:     */       }
/* 1641:     */     }
/* 1642:2714 */     return result;
/* 1643:     */   }
/* 1644:     */   
/* 1645:     */   public String toSummaryString()
/* 1646:     */   {
/* 1647:2725 */     return toSummaryString("", false);
/* 1648:     */   }
/* 1649:     */   
/* 1650:     */   public String toSummaryString(boolean printComplexityStatistics)
/* 1651:     */   {
/* 1652:2737 */     return toSummaryString("=== Summary ===\n", printComplexityStatistics);
/* 1653:     */   }
/* 1654:     */   
/* 1655:     */   public String toSummaryString(String title, boolean printComplexityStatistics)
/* 1656:     */   {
/* 1657:2754 */     StringBuffer text = new StringBuffer();
/* 1658:2756 */     if ((printComplexityStatistics) && (this.m_NoPriors))
/* 1659:     */     {
/* 1660:2757 */       printComplexityStatistics = false;
/* 1661:2758 */       System.err.println("Priors disabled, cannot print complexity statistics!");
/* 1662:     */     }
/* 1663:2762 */     text.append(title + "\n");
/* 1664:     */     try
/* 1665:     */     {
/* 1666:2764 */       if (this.m_WithClass > 0.0D)
/* 1667:     */       {
/* 1668:2765 */         if (this.m_ClassIsNominal)
/* 1669:     */         {
/* 1670:2766 */           boolean displayCorrect = this.m_metricsToDisplay.contains("correct");
/* 1671:2767 */           boolean displayIncorrect = this.m_metricsToDisplay.contains("incorrect");
/* 1672:2768 */           boolean displayKappa = this.m_metricsToDisplay.contains("kappa");
/* 1673:2769 */           boolean displayTotalCost = this.m_metricsToDisplay.contains("total cost");
/* 1674:2770 */           boolean displayAverageCost = this.m_metricsToDisplay.contains("average cost");
/* 1675:2773 */           if (displayCorrect)
/* 1676:     */           {
/* 1677:2774 */             text.append("Correctly Classified Instances     ");
/* 1678:2775 */             text.append(Utils.doubleToString(correct(), 12, 4) + "     " + Utils.doubleToString(pctCorrect(), 12, 4) + " %\n");
/* 1679:     */           }
/* 1680:2778 */           if (displayIncorrect)
/* 1681:     */           {
/* 1682:2779 */             text.append("Incorrectly Classified Instances   ");
/* 1683:2780 */             text.append(Utils.doubleToString(incorrect(), 12, 4) + "     " + Utils.doubleToString(pctIncorrect(), 12, 4) + " %\n");
/* 1684:     */           }
/* 1685:2783 */           if (displayKappa)
/* 1686:     */           {
/* 1687:2784 */             text.append("Kappa statistic                    ");
/* 1688:2785 */             text.append(Utils.doubleToString(kappa(), 12, 4) + "\n");
/* 1689:     */           }
/* 1690:2788 */           if (this.m_CostMatrix != null)
/* 1691:     */           {
/* 1692:2789 */             if (displayTotalCost)
/* 1693:     */             {
/* 1694:2790 */               text.append("Total Cost                         ");
/* 1695:2791 */               text.append(Utils.doubleToString(totalCost(), 12, 4) + "\n");
/* 1696:     */             }
/* 1697:2793 */             if (displayAverageCost)
/* 1698:     */             {
/* 1699:2794 */               text.append("Average Cost                       ");
/* 1700:2795 */               text.append(Utils.doubleToString(avgCost(), 12, 4) + "\n");
/* 1701:     */             }
/* 1702:     */           }
/* 1703:2798 */           if (printComplexityStatistics)
/* 1704:     */           {
/* 1705:2799 */             boolean displayKBRelative = this.m_metricsToDisplay.contains("kb relative");
/* 1706:     */             
/* 1707:2801 */             boolean displayKBInfo = this.m_metricsToDisplay.contains("kb information");
/* 1708:2803 */             if (displayKBRelative)
/* 1709:     */             {
/* 1710:2804 */               text.append("K&B Relative Info Score            ");
/* 1711:2805 */               text.append(Utils.doubleToString(KBRelativeInformation(), 12, 4) + " %\n");
/* 1712:     */             }
/* 1713:2808 */             if (displayKBInfo)
/* 1714:     */             {
/* 1715:2809 */               text.append("K&B Information Score              ");
/* 1716:2810 */               text.append(Utils.doubleToString(KBInformation(), 12, 4) + " bits");
/* 1717:     */               
/* 1718:2812 */               text.append(Utils.doubleToString(KBMeanInformation(), 12, 4) + " bits/instance\n");
/* 1719:     */             }
/* 1720:     */           }
/* 1721:2817 */           if (this.m_pluginMetrics != null) {
/* 1722:2818 */             for (AbstractEvaluationMetric m : this.m_pluginMetrics) {
/* 1723:2819 */               if (((m instanceof StandardEvaluationMetric)) && (m.appliesToNominalClass()) && (!m.appliesToNumericClass()))
/* 1724:     */               {
/* 1725:2821 */                 String metricName = m.getMetricName().toLowerCase();
/* 1726:2822 */                 boolean display = this.m_metricsToDisplay.contains(metricName);
/* 1727:2827 */                 if (display)
/* 1728:     */                 {
/* 1729:2828 */                   String formattedS = ((StandardEvaluationMetric)m).toSummaryString();
/* 1730:     */                   
/* 1731:2830 */                   text.append(formattedS);
/* 1732:     */                 }
/* 1733:     */               }
/* 1734:     */             }
/* 1735:     */           }
/* 1736:     */         }
/* 1737:     */         else
/* 1738:     */         {
/* 1739:2836 */           boolean displayCorrelation = this.m_metricsToDisplay.contains("correlation");
/* 1740:2838 */           if (displayCorrelation)
/* 1741:     */           {
/* 1742:2839 */             text.append("Correlation coefficient            ");
/* 1743:2840 */             text.append(Utils.doubleToString(correlationCoefficient(), 12, 4) + "\n");
/* 1744:     */           }
/* 1745:2844 */           if (this.m_pluginMetrics != null) {
/* 1746:2845 */             for (AbstractEvaluationMetric m : this.m_pluginMetrics) {
/* 1747:2846 */               if (((m instanceof StandardEvaluationMetric)) && (!m.appliesToNominalClass()) && (m.appliesToNumericClass()))
/* 1748:     */               {
/* 1749:2848 */                 String metricName = m.getMetricName().toLowerCase();
/* 1750:2849 */                 boolean display = this.m_metricsToDisplay.contains(metricName);
/* 1751:2851 */                 if (display)
/* 1752:     */                 {
/* 1753:2852 */                   String formattedS = ((StandardEvaluationMetric)m).toSummaryString();
/* 1754:     */                   
/* 1755:2854 */                   text.append(formattedS);
/* 1756:     */                 }
/* 1757:     */               }
/* 1758:     */             }
/* 1759:     */           }
/* 1760:     */         }
/* 1761:2860 */         if ((printComplexityStatistics) && (this.m_ComplexityStatisticsAvailable))
/* 1762:     */         {
/* 1763:2861 */           boolean displayComplexityOrder0 = this.m_metricsToDisplay.contains("complexity 0");
/* 1764:     */           
/* 1765:2863 */           boolean displayComplexityScheme = this.m_metricsToDisplay.contains("complexity scheme");
/* 1766:     */           
/* 1767:2865 */           boolean displayComplexityImprovement = this.m_metricsToDisplay.contains("complexity improvement");
/* 1768:2867 */           if (displayComplexityOrder0)
/* 1769:     */           {
/* 1770:2868 */             text.append("Class complexity | order 0         ");
/* 1771:2869 */             text.append(Utils.doubleToString(SFPriorEntropy(), 12, 4) + " bits");
/* 1772:     */             
/* 1773:2871 */             text.append(Utils.doubleToString(SFMeanPriorEntropy(), 12, 4) + " bits/instance\n");
/* 1774:     */           }
/* 1775:2874 */           if (displayComplexityScheme)
/* 1776:     */           {
/* 1777:2875 */             text.append("Class complexity | scheme          ");
/* 1778:2876 */             text.append(Utils.doubleToString(SFSchemeEntropy(), 12, 4) + " bits");
/* 1779:     */             
/* 1780:2878 */             text.append(Utils.doubleToString(SFMeanSchemeEntropy(), 12, 4) + " bits/instance\n");
/* 1781:     */           }
/* 1782:2881 */           if (displayComplexityImprovement)
/* 1783:     */           {
/* 1784:2882 */             text.append("Complexity improvement     (Sf)    ");
/* 1785:2883 */             text.append(Utils.doubleToString(SFEntropyGain(), 12, 4) + " bits");
/* 1786:2884 */             text.append(Utils.doubleToString(SFMeanEntropyGain(), 12, 4) + " bits/instance\n");
/* 1787:     */           }
/* 1788:     */         }
/* 1789:2889 */         if ((printComplexityStatistics) && (this.m_pluginMetrics != null)) {
/* 1790:2890 */           for (AbstractEvaluationMetric m : this.m_pluginMetrics) {
/* 1791:2891 */             if (((m instanceof InformationTheoreticEvaluationMetric)) && (
/* 1792:2892 */               ((this.m_ClassIsNominal) && (m.appliesToNominalClass())) || ((!this.m_ClassIsNominal) && (m.appliesToNumericClass()))))
/* 1793:     */             {
/* 1794:2894 */               String metricName = m.getMetricName().toLowerCase();
/* 1795:2895 */               boolean display = this.m_metricsToDisplay.contains(metricName);
/* 1796:2896 */               List<String> statNames = m.getStatisticNames();
/* 1797:2897 */               for (String s : statNames) {
/* 1798:2898 */                 display = (display) && (this.m_metricsToDisplay.contains(s.toLowerCase()));
/* 1799:     */               }
/* 1800:2901 */               if (display)
/* 1801:     */               {
/* 1802:2902 */                 String formattedS = ((InformationTheoreticEvaluationMetric)m).toSummaryString();
/* 1803:     */                 
/* 1804:     */ 
/* 1805:2905 */                 text.append(formattedS);
/* 1806:     */               }
/* 1807:     */             }
/* 1808:     */           }
/* 1809:     */         }
/* 1810:2912 */         boolean displayMAE = this.m_metricsToDisplay.contains("mae");
/* 1811:2913 */         boolean displayRMSE = this.m_metricsToDisplay.contains("rmse");
/* 1812:2914 */         boolean displayRAE = this.m_metricsToDisplay.contains("rae");
/* 1813:2915 */         boolean displayRRSE = this.m_metricsToDisplay.contains("rrse");
/* 1814:2917 */         if (displayMAE)
/* 1815:     */         {
/* 1816:2918 */           text.append("Mean absolute error                ");
/* 1817:2919 */           text.append(Utils.doubleToString(meanAbsoluteError(), 12, 4) + "\n");
/* 1818:     */         }
/* 1819:2921 */         if (displayRMSE)
/* 1820:     */         {
/* 1821:2922 */           text.append("Root mean squared error            ");
/* 1822:2923 */           text.append(Utils.doubleToString(rootMeanSquaredError(), 12, 4) + "\n");
/* 1823:     */         }
/* 1824:2926 */         if (!this.m_NoPriors)
/* 1825:     */         {
/* 1826:2927 */           if (displayRAE)
/* 1827:     */           {
/* 1828:2928 */             text.append("Relative absolute error            ");
/* 1829:2929 */             text.append(Utils.doubleToString(relativeAbsoluteError(), 12, 4) + " %\n");
/* 1830:     */           }
/* 1831:2932 */           if (displayRRSE)
/* 1832:     */           {
/* 1833:2933 */             text.append("Root relative squared error        ");
/* 1834:2934 */             text.append(Utils.doubleToString(rootRelativeSquaredError(), 12, 4) + " %\n");
/* 1835:     */           }
/* 1836:     */         }
/* 1837:2938 */         if (this.m_pluginMetrics != null) {
/* 1838:2939 */           for (AbstractEvaluationMetric m : this.m_pluginMetrics) {
/* 1839:2940 */             if (((m instanceof StandardEvaluationMetric)) && (m.appliesToNominalClass()) && (m.appliesToNumericClass()))
/* 1840:     */             {
/* 1841:2942 */               String metricName = m.getMetricName().toLowerCase();
/* 1842:2943 */               boolean display = this.m_metricsToDisplay.contains(metricName);
/* 1843:2944 */               List<String> statNames = m.getStatisticNames();
/* 1844:2945 */               for (String s : statNames) {
/* 1845:2946 */                 display = (display) && (this.m_metricsToDisplay.contains(s.toLowerCase()));
/* 1846:     */               }
/* 1847:2949 */               if (display)
/* 1848:     */               {
/* 1849:2950 */                 String formattedS = ((StandardEvaluationMetric)m).toSummaryString();
/* 1850:     */                 
/* 1851:2952 */                 text.append(formattedS);
/* 1852:     */               }
/* 1853:     */             }
/* 1854:     */           }
/* 1855:     */         }
/* 1856:2958 */         if (this.m_CoverageStatisticsAvailable)
/* 1857:     */         {
/* 1858:2959 */           boolean displayCoverage = this.m_metricsToDisplay.contains("coverage");
/* 1859:2960 */           boolean displayRegionSize = this.m_metricsToDisplay.contains("region size");
/* 1860:2963 */           if (displayCoverage)
/* 1861:     */           {
/* 1862:2964 */             text.append("Coverage of cases (" + Utils.doubleToString(this.m_ConfLevel, 4, 2) + " level)     ");
/* 1863:     */             
/* 1864:2966 */             text.append(Utils.doubleToString(coverageOfTestCasesByPredictedRegions(), 12, 4) + " %\n");
/* 1865:     */           }
/* 1866:2969 */           if ((!this.m_NoPriors) && 
/* 1867:2970 */             (displayRegionSize))
/* 1868:     */           {
/* 1869:2971 */             text.append("Mean rel. region size (" + Utils.doubleToString(this.m_ConfLevel, 4, 2) + " level) ");
/* 1870:     */             
/* 1871:2973 */             text.append(Utils.doubleToString(sizeOfPredictedRegions(), 12, 4) + " %\n");
/* 1872:     */           }
/* 1873:     */         }
/* 1874:     */       }
/* 1875:2979 */       if (Utils.gr(unclassified(), 0.0D))
/* 1876:     */       {
/* 1877:2980 */         text.append("UnClassified Instances             ");
/* 1878:2981 */         text.append(Utils.doubleToString(unclassified(), 12, 4) + "     " + Utils.doubleToString(pctUnclassified(), 12, 4) + " %\n");
/* 1879:     */       }
/* 1880:2984 */       text.append("Total Number of Instances          ");
/* 1881:2985 */       text.append(Utils.doubleToString(this.m_WithClass, 12, 4) + "\n");
/* 1882:2986 */       if (this.m_MissingClass > 0.0D)
/* 1883:     */       {
/* 1884:2987 */         text.append("Ignored Class Unknown Instances            ");
/* 1885:2988 */         text.append(Utils.doubleToString(this.m_MissingClass, 12, 4) + "\n");
/* 1886:     */       }
/* 1887:     */     }
/* 1888:     */     catch (Exception ex)
/* 1889:     */     {
/* 1890:2993 */       System.err.println("Arggh - Must be a bug in Evaluation class");
/* 1891:2994 */       ex.printStackTrace();
/* 1892:     */     }
/* 1893:2997 */     return text.toString();
/* 1894:     */   }
/* 1895:     */   
/* 1896:     */   public String toMatrixString()
/* 1897:     */     throws Exception
/* 1898:     */   {
/* 1899:3008 */     return toMatrixString("=== Confusion Matrix ===\n");
/* 1900:     */   }
/* 1901:     */   
/* 1902:     */   public String toMatrixString(String title)
/* 1903:     */     throws Exception
/* 1904:     */   {
/* 1905:3021 */     StringBuffer text = new StringBuffer();
/* 1906:3022 */     char[] IDChars = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
/* 1907:     */     
/* 1908:     */ 
/* 1909:     */ 
/* 1910:3026 */     boolean fractional = false;
/* 1911:3028 */     if (!this.m_ClassIsNominal) {
/* 1912:3029 */       throw new Exception("Evaluation: No confusion matrix possible!");
/* 1913:     */     }
/* 1914:3034 */     double maxval = 0.0D;
/* 1915:3035 */     for (int i = 0; i < this.m_NumClasses; i++) {
/* 1916:3036 */       for (int j = 0; j < this.m_NumClasses; j++)
/* 1917:     */       {
/* 1918:3037 */         double current = this.m_ConfusionMatrix[i][j];
/* 1919:3038 */         if (current < 0.0D) {
/* 1920:3039 */           current *= -10.0D;
/* 1921:     */         }
/* 1922:3041 */         if (current > maxval) {
/* 1923:3042 */           maxval = current;
/* 1924:     */         }
/* 1925:3044 */         double fract = current - Math.rint(current);
/* 1926:3045 */         if ((!fractional) && (Math.log(fract) / Math.log(10.0D) >= -2.0D)) {
/* 1927:3046 */           fractional = true;
/* 1928:     */         }
/* 1929:     */       }
/* 1930:     */     }
/* 1931:3051 */     int IDWidth = 1 + Math.max((int)(Math.log(maxval) / Math.log(10.0D) + (fractional ? 3 : 0)), (int)(Math.log(this.m_NumClasses) / Math.log(IDChars.length)));
/* 1932:     */     
/* 1933:     */ 
/* 1934:     */ 
/* 1935:3055 */     text.append(title).append("\n");
/* 1936:3056 */     for (int i = 0; i < this.m_NumClasses; i++) {
/* 1937:3057 */       if (fractional) {
/* 1938:3058 */         text.append(" ").append(num2ShortID(i, IDChars, IDWidth - 3)).append("   ");
/* 1939:     */       } else {
/* 1940:3061 */         text.append(" ").append(num2ShortID(i, IDChars, IDWidth));
/* 1941:     */       }
/* 1942:     */     }
/* 1943:3064 */     text.append("   <-- classified as\n");
/* 1944:3065 */     for (int i = 0; i < this.m_NumClasses; i++)
/* 1945:     */     {
/* 1946:3066 */       for (int j = 0; j < this.m_NumClasses; j++) {
/* 1947:3067 */         text.append(" ").append(Utils.doubleToString(this.m_ConfusionMatrix[i][j], IDWidth, fractional ? 2 : 0));
/* 1948:     */       }
/* 1949:3071 */       text.append(" | ").append(num2ShortID(i, IDChars, IDWidth)).append(" = ").append(this.m_ClassNames[i]).append("\n");
/* 1950:     */     }
/* 1951:3074 */     return text.toString();
/* 1952:     */   }
/* 1953:     */   
/* 1954:     */   public String toClassDetailsString()
/* 1955:     */     throws Exception
/* 1956:     */   {
/* 1957:3088 */     return toClassDetailsString("=== Detailed Accuracy By Class ===\n");
/* 1958:     */   }
/* 1959:     */   
/* 1960:     */   public String toClassDetailsString(String title)
/* 1961:     */     throws Exception
/* 1962:     */   {
/* 1963:3103 */     if (!this.m_ClassIsNominal) {
/* 1964:3104 */       throw new Exception("Evaluation: No per class statistics possible!");
/* 1965:     */     }
/* 1966:3107 */     boolean displayTP = this.m_metricsToDisplay.contains("tp rate");
/* 1967:3108 */     boolean displayFP = this.m_metricsToDisplay.contains("fp rate");
/* 1968:3109 */     boolean displayP = this.m_metricsToDisplay.contains("precision");
/* 1969:3110 */     boolean displayR = this.m_metricsToDisplay.contains("recall");
/* 1970:3111 */     boolean displayFM = this.m_metricsToDisplay.contains("f-measure");
/* 1971:3112 */     boolean displayMCC = this.m_metricsToDisplay.contains("mcc");
/* 1972:3113 */     boolean displayROC = this.m_metricsToDisplay.contains("roc area");
/* 1973:3114 */     boolean displayPRC = this.m_metricsToDisplay.contains("prc area");
/* 1974:     */     
/* 1975:3116 */     StringBuffer text = new StringBuffer(title + "\n                 " + (displayTP ? "TP Rate  " : "") + (displayFP ? "FP Rate  " : "") + (displayP ? "Precision  " : "") + (displayR ? "Recall   " : "") + (displayFM ? "F-Measure  " : "") + (displayMCC ? "MCC      " : "") + (displayROC ? "ROC Area  " : "") + (displayPRC ? "PRC Area  " : ""));
/* 1976:3123 */     if ((this.m_pluginMetrics != null) && (this.m_pluginMetrics.size() > 0)) {
/* 1977:3124 */       for (AbstractEvaluationMetric m : this.m_pluginMetrics) {
/* 1978:3125 */         if (((m instanceof InformationRetrievalEvaluationMetric)) && (m.appliesToNominalClass()))
/* 1979:     */         {
/* 1980:3127 */           String metricName = m.getMetricName().toLowerCase();
/* 1981:3128 */           if (this.m_metricsToDisplay.contains(metricName))
/* 1982:     */           {
/* 1983:3129 */             List<String> statNames = m.getStatisticNames();
/* 1984:3130 */             for (String name : statNames) {
/* 1985:3131 */               if (this.m_metricsToDisplay.contains(name.toLowerCase()))
/* 1986:     */               {
/* 1987:3132 */                 if (name.length() < 7) {
/* 1988:3133 */                   name = Utils.padRight(name, 7);
/* 1989:     */                 }
/* 1990:3135 */                 text.append(name).append("  ");
/* 1991:     */               }
/* 1992:     */             }
/* 1993:     */           }
/* 1994:     */         }
/* 1995:     */       }
/* 1996:     */     }
/* 1997:3143 */     text.append("Class\n");
/* 1998:3144 */     for (int i = 0; i < this.m_NumClasses; i++)
/* 1999:     */     {
/* 2000:3145 */       text.append("                 ");
/* 2001:3146 */       if (displayTP) {
/* 2002:3147 */         text.append(String.format("%-9.3f", new Object[] { Double.valueOf(truePositiveRate(i)) }));
/* 2003:     */       }
/* 2004:3149 */       if (displayFP) {
/* 2005:3150 */         text.append(String.format("%-9.3f", new Object[] { Double.valueOf(falsePositiveRate(i)) }));
/* 2006:     */       }
/* 2007:3152 */       if (displayP) {
/* 2008:3153 */         text.append(String.format("%-11.3f", new Object[] { Double.valueOf(precision(i)) }));
/* 2009:     */       }
/* 2010:3155 */       if (displayR) {
/* 2011:3156 */         text.append(String.format("%-9.3f", new Object[] { Double.valueOf(recall(i)) }));
/* 2012:     */       }
/* 2013:3158 */       if (displayFM) {
/* 2014:3159 */         text.append(String.format("%-11.3f", new Object[] { Double.valueOf(fMeasure(i)) }));
/* 2015:     */       }
/* 2016:3161 */       if (displayMCC)
/* 2017:     */       {
/* 2018:3162 */         double mat = matthewsCorrelationCoefficient(i);
/* 2019:3163 */         if (Utils.isMissingValue(mat)) {
/* 2020:3164 */           text.append("?       ");
/* 2021:     */         } else {
/* 2022:3166 */           text.append(String.format("%-9.3f", new Object[] { Double.valueOf(matthewsCorrelationCoefficient(i)) }));
/* 2023:     */         }
/* 2024:     */       }
/* 2025:3171 */       if (displayROC)
/* 2026:     */       {
/* 2027:3172 */         double rocVal = areaUnderROC(i);
/* 2028:3173 */         if (Utils.isMissingValue(rocVal)) {
/* 2029:3174 */           text.append("?         ");
/* 2030:     */         } else {
/* 2031:3176 */           text.append(String.format("%-10.3f", new Object[] { Double.valueOf(rocVal) }));
/* 2032:     */         }
/* 2033:     */       }
/* 2034:3179 */       if (displayPRC)
/* 2035:     */       {
/* 2036:3180 */         double prcVal = areaUnderPRC(i);
/* 2037:3181 */         if (Utils.isMissingValue(prcVal)) {
/* 2038:3182 */           text.append("?         ");
/* 2039:     */         } else {
/* 2040:3184 */           text.append(String.format("%-10.3f", new Object[] { Double.valueOf(prcVal) }));
/* 2041:     */         }
/* 2042:     */       }
/* 2043:     */       Iterator i$;
/* 2044:3188 */       if ((this.m_pluginMetrics != null) && (this.m_pluginMetrics.size() > 0)) {
/* 2045:3189 */         for (i$ = this.m_pluginMetrics.iterator(); i$.hasNext();)
/* 2046:     */         {
/* 2047:3189 */           m = (AbstractEvaluationMetric)i$.next();
/* 2048:3190 */           if (((m instanceof InformationRetrievalEvaluationMetric)) && (m.appliesToNominalClass()))
/* 2049:     */           {
/* 2050:3192 */             String metricName = m.getMetricName().toLowerCase();
/* 2051:3193 */             if (this.m_metricsToDisplay.contains(metricName))
/* 2052:     */             {
/* 2053:3194 */               List<String> statNames = m.getStatisticNames();
/* 2054:3195 */               for (String name : statNames) {
/* 2055:3196 */                 if (this.m_metricsToDisplay.contains(name.toLowerCase()))
/* 2056:     */                 {
/* 2057:3197 */                   double stat = ((InformationRetrievalEvaluationMetric)m).getStatistic(name, i);
/* 2058:3200 */                   if (name.length() < 7) {
/* 2059:3201 */                     name = Utils.padRight(name, 7);
/* 2060:     */                   }
/* 2061:3203 */                   if (Utils.isMissingValue(stat)) {
/* 2062:3204 */                     Utils.padRight("?", name.length());
/* 2063:     */                   } else {
/* 2064:3206 */                     text.append(String.format("%-" + name.length() + ".3f", new Object[] { Double.valueOf(stat) })).append("  ");
/* 2065:     */                   }
/* 2066:     */                 }
/* 2067:     */               }
/* 2068:     */             }
/* 2069:     */           }
/* 2070:     */         }
/* 2071:     */       }
/* 2072:     */       AbstractEvaluationMetric m;
/* 2073:3217 */       text.append(this.m_ClassNames[i]).append('\n');
/* 2074:     */     }
/* 2075:3220 */     text.append("Weighted Avg.    ");
/* 2076:3221 */     if (displayTP) {
/* 2077:3222 */       text.append(String.format("%-9.3f", new Object[] { Double.valueOf(weightedTruePositiveRate()) }));
/* 2078:     */     }
/* 2079:3224 */     if (displayFP) {
/* 2080:3225 */       text.append(String.format("%-9.3f", new Object[] { Double.valueOf(weightedFalsePositiveRate()) }));
/* 2081:     */     }
/* 2082:3227 */     if (displayP) {
/* 2083:3228 */       text.append(String.format("%-11.3f", new Object[] { Double.valueOf(weightedPrecision()) }));
/* 2084:     */     }
/* 2085:3230 */     if (displayR) {
/* 2086:3231 */       text.append(String.format("%-9.3f", new Object[] { Double.valueOf(weightedRecall()) }));
/* 2087:     */     }
/* 2088:3233 */     if (displayFM) {
/* 2089:3234 */       text.append(String.format("%-11.3f", new Object[] { Double.valueOf(weightedFMeasure()) }));
/* 2090:     */     }
/* 2091:3236 */     if (displayMCC) {
/* 2092:3237 */       text.append(String.format("%-9.3f", new Object[] { Double.valueOf(weightedMatthewsCorrelation()) }));
/* 2093:     */     }
/* 2094:3239 */     if (displayROC) {
/* 2095:3240 */       text.append(String.format("%-10.3f", new Object[] { Double.valueOf(weightedAreaUnderROC()) }));
/* 2096:     */     }
/* 2097:3242 */     if (displayPRC) {
/* 2098:3243 */       text.append(String.format("%-10.3f", new Object[] { Double.valueOf(weightedAreaUnderPRC()) }));
/* 2099:     */     }
/* 2100:     */     Iterator i$;
/* 2101:3246 */     if ((this.m_pluginMetrics != null) && (this.m_pluginMetrics.size() > 0)) {
/* 2102:3247 */       for (i$ = this.m_pluginMetrics.iterator(); i$.hasNext();)
/* 2103:     */       {
/* 2104:3247 */         m = (AbstractEvaluationMetric)i$.next();
/* 2105:3248 */         if (((m instanceof InformationRetrievalEvaluationMetric)) && (m.appliesToNominalClass()))
/* 2106:     */         {
/* 2107:3250 */           String metricName = m.getMetricName().toLowerCase();
/* 2108:3251 */           if (this.m_metricsToDisplay.contains(metricName))
/* 2109:     */           {
/* 2110:3252 */             List<String> statNames = m.getStatisticNames();
/* 2111:3253 */             for (String name : statNames) {
/* 2112:3254 */               if (this.m_metricsToDisplay.contains(name.toLowerCase()))
/* 2113:     */               {
/* 2114:3255 */                 double stat = ((InformationRetrievalEvaluationMetric)m).getClassWeightedAverageStatistic(name);
/* 2115:3258 */                 if (name.length() < 7) {
/* 2116:3259 */                   name = Utils.padRight(name, 7);
/* 2117:     */                 }
/* 2118:3261 */                 if (Utils.isMissingValue(stat)) {
/* 2119:3262 */                   Utils.padRight("?", name.length());
/* 2120:     */                 } else {
/* 2121:3264 */                   text.append(String.format("%-" + name.length() + ".3f", new Object[] { Double.valueOf(stat) })).append("  ");
/* 2122:     */                 }
/* 2123:     */               }
/* 2124:     */             }
/* 2125:     */           }
/* 2126:     */         }
/* 2127:     */       }
/* 2128:     */     }
/* 2129:     */     AbstractEvaluationMetric m;
/* 2130:3275 */     text.append("\n");
/* 2131:     */     
/* 2132:3277 */     return text.toString();
/* 2133:     */   }
/* 2134:     */   
/* 2135:     */   public double numTruePositives(int classIndex)
/* 2136:     */   {
/* 2137:3294 */     double correct = 0.0D;
/* 2138:3295 */     for (int j = 0; j < this.m_NumClasses; j++) {
/* 2139:3296 */       if (j == classIndex) {
/* 2140:3297 */         correct += this.m_ConfusionMatrix[classIndex][j];
/* 2141:     */       }
/* 2142:     */     }
/* 2143:3300 */     return correct;
/* 2144:     */   }
/* 2145:     */   
/* 2146:     */   public double truePositiveRate(int classIndex)
/* 2147:     */   {
/* 2148:3319 */     double correct = 0.0D;double total = 0.0D;
/* 2149:3320 */     for (int j = 0; j < this.m_NumClasses; j++)
/* 2150:     */     {
/* 2151:3321 */       if (j == classIndex) {
/* 2152:3322 */         correct += this.m_ConfusionMatrix[classIndex][j];
/* 2153:     */       }
/* 2154:3324 */       total += this.m_ConfusionMatrix[classIndex][j];
/* 2155:     */     }
/* 2156:3326 */     if (total == 0.0D) {
/* 2157:3327 */       return 0.0D;
/* 2158:     */     }
/* 2159:3329 */     return correct / total;
/* 2160:     */   }
/* 2161:     */   
/* 2162:     */   public double weightedTruePositiveRate()
/* 2163:     */   {
/* 2164:3338 */     double[] classCounts = new double[this.m_NumClasses];
/* 2165:3339 */     double classCountSum = 0.0D;
/* 2166:3341 */     for (int i = 0; i < this.m_NumClasses; i++)
/* 2167:     */     {
/* 2168:3342 */       for (int j = 0; j < this.m_NumClasses; j++) {
/* 2169:3343 */         classCounts[i] += this.m_ConfusionMatrix[i][j];
/* 2170:     */       }
/* 2171:3345 */       classCountSum += classCounts[i];
/* 2172:     */     }
/* 2173:3348 */     double truePosTotal = 0.0D;
/* 2174:3349 */     for (int i = 0; i < this.m_NumClasses; i++)
/* 2175:     */     {
/* 2176:3350 */       double temp = truePositiveRate(i);
/* 2177:3351 */       truePosTotal += temp * classCounts[i];
/* 2178:     */     }
/* 2179:3354 */     return truePosTotal / classCountSum;
/* 2180:     */   }
/* 2181:     */   
/* 2182:     */   public double numTrueNegatives(int classIndex)
/* 2183:     */   {
/* 2184:3371 */     double correct = 0.0D;
/* 2185:3372 */     for (int i = 0; i < this.m_NumClasses; i++) {
/* 2186:3373 */       if (i != classIndex) {
/* 2187:3374 */         for (int j = 0; j < this.m_NumClasses; j++) {
/* 2188:3375 */           if (j != classIndex) {
/* 2189:3376 */             correct += this.m_ConfusionMatrix[i][j];
/* 2190:     */           }
/* 2191:     */         }
/* 2192:     */       }
/* 2193:     */     }
/* 2194:3381 */     return correct;
/* 2195:     */   }
/* 2196:     */   
/* 2197:     */   public double trueNegativeRate(int classIndex)
/* 2198:     */   {
/* 2199:3400 */     double correct = 0.0D;double total = 0.0D;
/* 2200:3401 */     for (int i = 0; i < this.m_NumClasses; i++) {
/* 2201:3402 */       if (i != classIndex) {
/* 2202:3403 */         for (int j = 0; j < this.m_NumClasses; j++)
/* 2203:     */         {
/* 2204:3404 */           if (j != classIndex) {
/* 2205:3405 */             correct += this.m_ConfusionMatrix[i][j];
/* 2206:     */           }
/* 2207:3407 */           total += this.m_ConfusionMatrix[i][j];
/* 2208:     */         }
/* 2209:     */       }
/* 2210:     */     }
/* 2211:3411 */     if (total == 0.0D) {
/* 2212:3412 */       return 0.0D;
/* 2213:     */     }
/* 2214:3414 */     return correct / total;
/* 2215:     */   }
/* 2216:     */   
/* 2217:     */   public double weightedTrueNegativeRate()
/* 2218:     */   {
/* 2219:3423 */     double[] classCounts = new double[this.m_NumClasses];
/* 2220:3424 */     double classCountSum = 0.0D;
/* 2221:3426 */     for (int i = 0; i < this.m_NumClasses; i++)
/* 2222:     */     {
/* 2223:3427 */       for (int j = 0; j < this.m_NumClasses; j++) {
/* 2224:3428 */         classCounts[i] += this.m_ConfusionMatrix[i][j];
/* 2225:     */       }
/* 2226:3430 */       classCountSum += classCounts[i];
/* 2227:     */     }
/* 2228:3433 */     double trueNegTotal = 0.0D;
/* 2229:3434 */     for (int i = 0; i < this.m_NumClasses; i++)
/* 2230:     */     {
/* 2231:3435 */       double temp = trueNegativeRate(i);
/* 2232:3436 */       trueNegTotal += temp * classCounts[i];
/* 2233:     */     }
/* 2234:3439 */     return trueNegTotal / classCountSum;
/* 2235:     */   }
/* 2236:     */   
/* 2237:     */   public double numFalsePositives(int classIndex)
/* 2238:     */   {
/* 2239:3456 */     double incorrect = 0.0D;
/* 2240:3457 */     for (int i = 0; i < this.m_NumClasses; i++) {
/* 2241:3458 */       if (i != classIndex) {
/* 2242:3459 */         for (int j = 0; j < this.m_NumClasses; j++) {
/* 2243:3460 */           if (j == classIndex) {
/* 2244:3461 */             incorrect += this.m_ConfusionMatrix[i][j];
/* 2245:     */           }
/* 2246:     */         }
/* 2247:     */       }
/* 2248:     */     }
/* 2249:3466 */     return incorrect;
/* 2250:     */   }
/* 2251:     */   
/* 2252:     */   public double falsePositiveRate(int classIndex)
/* 2253:     */   {
/* 2254:3485 */     double incorrect = 0.0D;double total = 0.0D;
/* 2255:3486 */     for (int i = 0; i < this.m_NumClasses; i++) {
/* 2256:3487 */       if (i != classIndex) {
/* 2257:3488 */         for (int j = 0; j < this.m_NumClasses; j++)
/* 2258:     */         {
/* 2259:3489 */           if (j == classIndex) {
/* 2260:3490 */             incorrect += this.m_ConfusionMatrix[i][j];
/* 2261:     */           }
/* 2262:3492 */           total += this.m_ConfusionMatrix[i][j];
/* 2263:     */         }
/* 2264:     */       }
/* 2265:     */     }
/* 2266:3496 */     if (total == 0.0D) {
/* 2267:3497 */       return 0.0D;
/* 2268:     */     }
/* 2269:3499 */     return incorrect / total;
/* 2270:     */   }
/* 2271:     */   
/* 2272:     */   public double weightedFalsePositiveRate()
/* 2273:     */   {
/* 2274:3508 */     double[] classCounts = new double[this.m_NumClasses];
/* 2275:3509 */     double classCountSum = 0.0D;
/* 2276:3511 */     for (int i = 0; i < this.m_NumClasses; i++)
/* 2277:     */     {
/* 2278:3512 */       for (int j = 0; j < this.m_NumClasses; j++) {
/* 2279:3513 */         classCounts[i] += this.m_ConfusionMatrix[i][j];
/* 2280:     */       }
/* 2281:3515 */       classCountSum += classCounts[i];
/* 2282:     */     }
/* 2283:3518 */     double falsePosTotal = 0.0D;
/* 2284:3519 */     for (int i = 0; i < this.m_NumClasses; i++)
/* 2285:     */     {
/* 2286:3520 */       double temp = falsePositiveRate(i);
/* 2287:3521 */       falsePosTotal += temp * classCounts[i];
/* 2288:     */     }
/* 2289:3524 */     return falsePosTotal / classCountSum;
/* 2290:     */   }
/* 2291:     */   
/* 2292:     */   public double numFalseNegatives(int classIndex)
/* 2293:     */   {
/* 2294:3541 */     double incorrect = 0.0D;
/* 2295:3542 */     for (int i = 0; i < this.m_NumClasses; i++) {
/* 2296:3543 */       if (i == classIndex) {
/* 2297:3544 */         for (int j = 0; j < this.m_NumClasses; j++) {
/* 2298:3545 */           if (j != classIndex) {
/* 2299:3546 */             incorrect += this.m_ConfusionMatrix[i][j];
/* 2300:     */           }
/* 2301:     */         }
/* 2302:     */       }
/* 2303:     */     }
/* 2304:3551 */     return incorrect;
/* 2305:     */   }
/* 2306:     */   
/* 2307:     */   public double falseNegativeRate(int classIndex)
/* 2308:     */   {
/* 2309:3570 */     double incorrect = 0.0D;double total = 0.0D;
/* 2310:3571 */     for (int i = 0; i < this.m_NumClasses; i++) {
/* 2311:3572 */       if (i == classIndex) {
/* 2312:3573 */         for (int j = 0; j < this.m_NumClasses; j++)
/* 2313:     */         {
/* 2314:3574 */           if (j != classIndex) {
/* 2315:3575 */             incorrect += this.m_ConfusionMatrix[i][j];
/* 2316:     */           }
/* 2317:3577 */           total += this.m_ConfusionMatrix[i][j];
/* 2318:     */         }
/* 2319:     */       }
/* 2320:     */     }
/* 2321:3581 */     if (total == 0.0D) {
/* 2322:3582 */       return 0.0D;
/* 2323:     */     }
/* 2324:3584 */     return incorrect / total;
/* 2325:     */   }
/* 2326:     */   
/* 2327:     */   public double weightedFalseNegativeRate()
/* 2328:     */   {
/* 2329:3593 */     double[] classCounts = new double[this.m_NumClasses];
/* 2330:3594 */     double classCountSum = 0.0D;
/* 2331:3596 */     for (int i = 0; i < this.m_NumClasses; i++)
/* 2332:     */     {
/* 2333:3597 */       for (int j = 0; j < this.m_NumClasses; j++) {
/* 2334:3598 */         classCounts[i] += this.m_ConfusionMatrix[i][j];
/* 2335:     */       }
/* 2336:3600 */       classCountSum += classCounts[i];
/* 2337:     */     }
/* 2338:3603 */     double falseNegTotal = 0.0D;
/* 2339:3604 */     for (int i = 0; i < this.m_NumClasses; i++)
/* 2340:     */     {
/* 2341:3605 */       double temp = falseNegativeRate(i);
/* 2342:3606 */       falseNegTotal += temp * classCounts[i];
/* 2343:     */     }
/* 2344:3609 */     return falseNegTotal / classCountSum;
/* 2345:     */   }
/* 2346:     */   
/* 2347:     */   public double matthewsCorrelationCoefficient(int classIndex)
/* 2348:     */   {
/* 2349:3622 */     double numTP = numTruePositives(classIndex);
/* 2350:3623 */     double numTN = numTrueNegatives(classIndex);
/* 2351:3624 */     double numFP = numFalsePositives(classIndex);
/* 2352:3625 */     double numFN = numFalseNegatives(classIndex);
/* 2353:3626 */     double n = numTP * numTN - numFP * numFN;
/* 2354:3627 */     double d = (numTP + numFP) * (numTP + numFN) * (numTN + numFP) * (numTN + numFN);
/* 2355:     */     
/* 2356:3629 */     d = Math.sqrt(d);
/* 2357:3630 */     if (d == 0.0D) {
/* 2358:3631 */       d = 1.0D;
/* 2359:     */     }
/* 2360:3634 */     return n / d;
/* 2361:     */   }
/* 2362:     */   
/* 2363:     */   public double weightedMatthewsCorrelation()
/* 2364:     */   {
/* 2365:3643 */     double[] classCounts = new double[this.m_NumClasses];
/* 2366:3644 */     double classCountSum = 0.0D;
/* 2367:3646 */     for (int i = 0; i < this.m_NumClasses; i++)
/* 2368:     */     {
/* 2369:3647 */       for (int j = 0; j < this.m_NumClasses; j++) {
/* 2370:3648 */         classCounts[i] += this.m_ConfusionMatrix[i][j];
/* 2371:     */       }
/* 2372:3650 */       classCountSum += classCounts[i];
/* 2373:     */     }
/* 2374:3653 */     double mccTotal = 0.0D;
/* 2375:3654 */     for (int i = 0; i < this.m_NumClasses; i++)
/* 2376:     */     {
/* 2377:3655 */       double temp = matthewsCorrelationCoefficient(i);
/* 2378:3656 */       if (!Utils.isMissingValue(temp)) {
/* 2379:3657 */         mccTotal += temp * classCounts[i];
/* 2380:     */       }
/* 2381:     */     }
/* 2382:3661 */     return mccTotal / classCountSum;
/* 2383:     */   }
/* 2384:     */   
/* 2385:     */   public double recall(int classIndex)
/* 2386:     */   {
/* 2387:3681 */     return truePositiveRate(classIndex);
/* 2388:     */   }
/* 2389:     */   
/* 2390:     */   public double weightedRecall()
/* 2391:     */   {
/* 2392:3690 */     return weightedTruePositiveRate();
/* 2393:     */   }
/* 2394:     */   
/* 2395:     */   public double precision(int classIndex)
/* 2396:     */   {
/* 2397:3709 */     double correct = 0.0D;double total = 0.0D;
/* 2398:3710 */     for (int i = 0; i < this.m_NumClasses; i++)
/* 2399:     */     {
/* 2400:3711 */       if (i == classIndex) {
/* 2401:3712 */         correct += this.m_ConfusionMatrix[i][classIndex];
/* 2402:     */       }
/* 2403:3714 */       total += this.m_ConfusionMatrix[i][classIndex];
/* 2404:     */     }
/* 2405:3716 */     if (total == 0.0D) {
/* 2406:3717 */       return 0.0D;
/* 2407:     */     }
/* 2408:3719 */     return correct / total;
/* 2409:     */   }
/* 2410:     */   
/* 2411:     */   public double weightedPrecision()
/* 2412:     */   {
/* 2413:3728 */     double[] classCounts = new double[this.m_NumClasses];
/* 2414:3729 */     double classCountSum = 0.0D;
/* 2415:3731 */     for (int i = 0; i < this.m_NumClasses; i++)
/* 2416:     */     {
/* 2417:3732 */       for (int j = 0; j < this.m_NumClasses; j++) {
/* 2418:3733 */         classCounts[i] += this.m_ConfusionMatrix[i][j];
/* 2419:     */       }
/* 2420:3735 */       classCountSum += classCounts[i];
/* 2421:     */     }
/* 2422:3738 */     double precisionTotal = 0.0D;
/* 2423:3739 */     for (int i = 0; i < this.m_NumClasses; i++)
/* 2424:     */     {
/* 2425:3740 */       double temp = precision(i);
/* 2426:3741 */       precisionTotal += temp * classCounts[i];
/* 2427:     */     }
/* 2428:3744 */     return precisionTotal / classCountSum;
/* 2429:     */   }
/* 2430:     */   
/* 2431:     */   public double fMeasure(int classIndex)
/* 2432:     */   {
/* 2433:3763 */     double precision = precision(classIndex);
/* 2434:3764 */     double recall = recall(classIndex);
/* 2435:3765 */     if (precision + recall == 0.0D) {
/* 2436:3766 */       return 0.0D;
/* 2437:     */     }
/* 2438:3768 */     return 2.0D * precision * recall / (precision + recall);
/* 2439:     */   }
/* 2440:     */   
/* 2441:     */   public double weightedFMeasure()
/* 2442:     */   {
/* 2443:3777 */     double[] classCounts = new double[this.m_NumClasses];
/* 2444:3778 */     double classCountSum = 0.0D;
/* 2445:3780 */     for (int i = 0; i < this.m_NumClasses; i++)
/* 2446:     */     {
/* 2447:3781 */       for (int j = 0; j < this.m_NumClasses; j++) {
/* 2448:3782 */         classCounts[i] += this.m_ConfusionMatrix[i][j];
/* 2449:     */       }
/* 2450:3784 */       classCountSum += classCounts[i];
/* 2451:     */     }
/* 2452:3787 */     double fMeasureTotal = 0.0D;
/* 2453:3788 */     for (int i = 0; i < this.m_NumClasses; i++)
/* 2454:     */     {
/* 2455:3789 */       double temp = fMeasure(i);
/* 2456:3790 */       fMeasureTotal += temp * classCounts[i];
/* 2457:     */     }
/* 2458:3793 */     return fMeasureTotal / classCountSum;
/* 2459:     */   }
/* 2460:     */   
/* 2461:     */   public double unweightedMacroFmeasure()
/* 2462:     */   {
/* 2463:3803 */     Stats rr = new Stats();
/* 2464:3804 */     for (int c = 0; c < this.m_NumClasses; c++) {
/* 2465:3806 */       if (numTruePositives(c) + numFalseNegatives(c) > 0.0D) {
/* 2466:3807 */         rr.add(fMeasure(c));
/* 2467:     */       }
/* 2468:     */     }
/* 2469:3810 */     rr.calculateDerived();
/* 2470:3811 */     return rr.mean;
/* 2471:     */   }
/* 2472:     */   
/* 2473:     */   public double unweightedMicroFmeasure()
/* 2474:     */   {
/* 2475:3823 */     double tp = 0.0D;
/* 2476:3824 */     double fn = 0.0D;
/* 2477:3825 */     double fp = 0.0D;
/* 2478:3826 */     for (int c = 0; c < this.m_NumClasses; c++)
/* 2479:     */     {
/* 2480:3827 */       tp += numTruePositives(c);
/* 2481:3828 */       fn += numFalseNegatives(c);
/* 2482:3829 */       fp += numFalsePositives(c);
/* 2483:     */     }
/* 2484:3831 */     return 2.0D * tp / (2.0D * tp + fn + fp);
/* 2485:     */   }
/* 2486:     */   
/* 2487:     */   public void setPriors(Instances train)
/* 2488:     */     throws Exception
/* 2489:     */   {
/* 2490:3843 */     this.m_NoPriors = false;
/* 2491:3845 */     if (!this.m_ClassIsNominal)
/* 2492:     */     {
/* 2493:3847 */       this.m_NumTrainClassVals = 0;
/* 2494:3848 */       this.m_TrainClassVals = null;
/* 2495:3849 */       this.m_TrainClassWeights = null;
/* 2496:3850 */       this.m_PriorEstimator = null;
/* 2497:     */       
/* 2498:3852 */       this.m_MinTarget = 1.7976931348623157E+308D;
/* 2499:3853 */       this.m_MaxTarget = -1.797693134862316E+308D;
/* 2500:3855 */       for (int i = 0; i < train.numInstances(); i++)
/* 2501:     */       {
/* 2502:3856 */         Instance currentInst = train.instance(i);
/* 2503:3857 */         if (!currentInst.classIsMissing()) {
/* 2504:3858 */           addNumericTrainClass(currentInst.classValue(), currentInst.weight());
/* 2505:     */         }
/* 2506:     */       }
/* 2507:3862 */       double tmp100_99 = 0.0D;this.m_ClassPriorsSum = tmp100_99;this.m_ClassPriors[0] = tmp100_99;
/* 2508:3863 */       for (int i = 0; i < train.numInstances(); i++) {
/* 2509:3864 */         if (!train.instance(i).classIsMissing())
/* 2510:     */         {
/* 2511:3865 */           this.m_ClassPriors[0] += train.instance(i).classValue() * train.instance(i).weight();
/* 2512:     */           
/* 2513:3867 */           this.m_ClassPriorsSum += train.instance(i).weight();
/* 2514:     */         }
/* 2515:     */       }
/* 2516:     */     }
/* 2517:     */     else
/* 2518:     */     {
/* 2519:3872 */       for (int i = 0; i < this.m_NumClasses; i++) {
/* 2520:3873 */         this.m_ClassPriors[i] = 1.0D;
/* 2521:     */       }
/* 2522:3875 */       this.m_ClassPriorsSum = this.m_NumClasses;
/* 2523:3876 */       for (int i = 0; i < train.numInstances(); i++) {
/* 2524:3877 */         if (!train.instance(i).classIsMissing())
/* 2525:     */         {
/* 2526:3878 */           this.m_ClassPriors[((int)train.instance(i).classValue())] += train.instance(i).weight();
/* 2527:     */           
/* 2528:3880 */           this.m_ClassPriorsSum += train.instance(i).weight();
/* 2529:     */         }
/* 2530:     */       }
/* 2531:3883 */       this.m_MaxTarget = this.m_NumClasses;
/* 2532:3884 */       this.m_MinTarget = 0.0D;
/* 2533:     */     }
/* 2534:     */   }
/* 2535:     */   
/* 2536:     */   public double[] getClassPriors()
/* 2537:     */   {
/* 2538:3894 */     return this.m_ClassPriors;
/* 2539:     */   }
/* 2540:     */   
/* 2541:     */   public void updatePriors(Instance instance)
/* 2542:     */     throws Exception
/* 2543:     */   {
/* 2544:3905 */     if (!instance.classIsMissing()) {
/* 2545:3906 */       if (!this.m_ClassIsNominal)
/* 2546:     */       {
/* 2547:3907 */         addNumericTrainClass(instance.classValue(), instance.weight());
/* 2548:3908 */         this.m_ClassPriors[0] += instance.classValue() * instance.weight();
/* 2549:3909 */         this.m_ClassPriorsSum += instance.weight();
/* 2550:     */       }
/* 2551:     */       else
/* 2552:     */       {
/* 2553:3911 */         this.m_ClassPriors[((int)instance.classValue())] += instance.weight();
/* 2554:3912 */         this.m_ClassPriorsSum += instance.weight();
/* 2555:     */       }
/* 2556:     */     }
/* 2557:     */   }
/* 2558:     */   
/* 2559:     */   public void useNoPriors()
/* 2560:     */   {
/* 2561:3923 */     this.m_NoPriors = true;
/* 2562:     */   }
/* 2563:     */   
/* 2564:     */   public boolean equals(Object obj)
/* 2565:     */   {
/* 2566:3936 */     if ((obj == null) || (!obj.getClass().equals(getClass()))) {
/* 2567:3937 */       return false;
/* 2568:     */     }
/* 2569:3939 */     Evaluation cmp = (Evaluation)obj;
/* 2570:3940 */     if (this.m_ClassIsNominal != cmp.m_ClassIsNominal) {
/* 2571:3941 */       return false;
/* 2572:     */     }
/* 2573:3943 */     if (this.m_NumClasses != cmp.m_NumClasses) {
/* 2574:3944 */       return false;
/* 2575:     */     }
/* 2576:3947 */     if (this.m_Incorrect != cmp.m_Incorrect) {
/* 2577:3948 */       return false;
/* 2578:     */     }
/* 2579:3950 */     if (this.m_Correct != cmp.m_Correct) {
/* 2580:3951 */       return false;
/* 2581:     */     }
/* 2582:3953 */     if (this.m_Unclassified != cmp.m_Unclassified) {
/* 2583:3954 */       return false;
/* 2584:     */     }
/* 2585:3956 */     if (this.m_MissingClass != cmp.m_MissingClass) {
/* 2586:3957 */       return false;
/* 2587:     */     }
/* 2588:3959 */     if (this.m_WithClass != cmp.m_WithClass) {
/* 2589:3960 */       return false;
/* 2590:     */     }
/* 2591:3963 */     if (this.m_SumErr != cmp.m_SumErr) {
/* 2592:3964 */       return false;
/* 2593:     */     }
/* 2594:3966 */     if (this.m_SumAbsErr != cmp.m_SumAbsErr) {
/* 2595:3967 */       return false;
/* 2596:     */     }
/* 2597:3969 */     if (this.m_SumSqrErr != cmp.m_SumSqrErr) {
/* 2598:3970 */       return false;
/* 2599:     */     }
/* 2600:3972 */     if (this.m_SumClass != cmp.m_SumClass) {
/* 2601:3973 */       return false;
/* 2602:     */     }
/* 2603:3975 */     if (this.m_SumSqrClass != cmp.m_SumSqrClass) {
/* 2604:3976 */       return false;
/* 2605:     */     }
/* 2606:3978 */     if (this.m_SumPredicted != cmp.m_SumPredicted) {
/* 2607:3979 */       return false;
/* 2608:     */     }
/* 2609:3981 */     if (this.m_SumSqrPredicted != cmp.m_SumSqrPredicted) {
/* 2610:3982 */       return false;
/* 2611:     */     }
/* 2612:3984 */     if (this.m_SumClassPredicted != cmp.m_SumClassPredicted) {
/* 2613:3985 */       return false;
/* 2614:     */     }
/* 2615:3988 */     if (this.m_ClassIsNominal) {
/* 2616:3989 */       for (int i = 0; i < this.m_NumClasses; i++) {
/* 2617:3990 */         for (int j = 0; j < this.m_NumClasses; j++) {
/* 2618:3991 */           if (this.m_ConfusionMatrix[i][j] != cmp.m_ConfusionMatrix[i][j]) {
/* 2619:3992 */             return false;
/* 2620:     */           }
/* 2621:     */         }
/* 2622:     */       }
/* 2623:     */     }
/* 2624:3998 */     return true;
/* 2625:     */   }
/* 2626:     */   
/* 2627:     */   protected static String makeOptionString(Classifier classifier, boolean globalInfo)
/* 2628:     */   {
/* 2629:4012 */     StringBuffer optionsText = new StringBuffer("");
/* 2630:     */     
/* 2631:     */ 
/* 2632:4015 */     optionsText.append("\n\nGeneral options:\n\n");
/* 2633:4016 */     optionsText.append("-h or -help\n");
/* 2634:4017 */     optionsText.append("\tOutput help information.\n");
/* 2635:4018 */     optionsText.append("-synopsis or -info\n");
/* 2636:4019 */     optionsText.append("\tOutput synopsis for classifier (use in conjunction  with -h)\n");
/* 2637:     */     
/* 2638:4021 */     optionsText.append("-t <name of training file>\n");
/* 2639:4022 */     optionsText.append("\tSets training file.\n");
/* 2640:4023 */     optionsText.append("-T <name of test file>\n");
/* 2641:4024 */     optionsText.append("\tSets test file. If missing, a cross-validation will be performed\n");
/* 2642:     */     
/* 2643:4026 */     optionsText.append("\ton the training data.\n");
/* 2644:4027 */     optionsText.append("-c <class index>\n");
/* 2645:4028 */     optionsText.append("\tSets index of class attribute (default: last).\n");
/* 2646:4029 */     optionsText.append("-x <number of folds>\n");
/* 2647:4030 */     optionsText.append("\tSets number of folds for cross-validation (default: 10).\n");
/* 2648:     */     
/* 2649:4032 */     optionsText.append("-no-cv\n");
/* 2650:4033 */     optionsText.append("\tDo not perform any cross validation.\n");
/* 2651:4034 */     optionsText.append("-force-batch-training\n");
/* 2652:4035 */     optionsText.append("\tAlways train classifier in batch mode, never incrementally.\n");
/* 2653:     */     
/* 2654:4037 */     optionsText.append("-split-percentage <percentage>\n");
/* 2655:4038 */     optionsText.append("\tSets the percentage for the train/test set split, e.g., 66.\n");
/* 2656:     */     
/* 2657:4040 */     optionsText.append("-preserve-order\n");
/* 2658:4041 */     optionsText.append("\tPreserves the order in the percentage split.\n");
/* 2659:4042 */     optionsText.append("-s <random number seed>\n");
/* 2660:4043 */     optionsText.append("\tSets random number seed for cross-validation or percentage split\n");
/* 2661:     */     
/* 2662:4045 */     optionsText.append("\t(default: 1).\n");
/* 2663:4046 */     optionsText.append("-m <name of file with cost matrix>\n");
/* 2664:4047 */     optionsText.append("\tSets file with cost matrix.\n");
/* 2665:4048 */     optionsText.append("-toggle <comma-separated list of evaluation metric names>\n");
/* 2666:     */     
/* 2667:4050 */     optionsText.append("\tComma separated list of metric names to toggle in the output.\n\tAll metrics are output by default with the exception of 'Coverage' and 'Region size'.\n\t");
/* 2668:     */     
/* 2669:     */ 
/* 2670:     */ 
/* 2671:4054 */     optionsText.append("Available metrics:\n\t");
/* 2672:4055 */     List<String> metricsToDisplay = new ArrayList(Arrays.asList(BUILT_IN_EVAL_METRICS));
/* 2673:     */     
/* 2674:     */ 
/* 2675:4058 */     List<AbstractEvaluationMetric> pluginMetrics = AbstractEvaluationMetric.getPluginMetrics();
/* 2676:4060 */     if (pluginMetrics != null) {
/* 2677:4061 */       for (AbstractEvaluationMetric m : pluginMetrics) {
/* 2678:4062 */         if ((m instanceof InformationRetrievalEvaluationMetric))
/* 2679:     */         {
/* 2680:4063 */           List<String> statNames = m.getStatisticNames();
/* 2681:4064 */           for (String s : statNames) {
/* 2682:4065 */             metricsToDisplay.add(s.toLowerCase());
/* 2683:     */           }
/* 2684:     */         }
/* 2685:     */         else
/* 2686:     */         {
/* 2687:4068 */           metricsToDisplay.add(m.getMetricName().toLowerCase());
/* 2688:     */         }
/* 2689:     */       }
/* 2690:     */     }
/* 2691:4073 */     int length = 0;
/* 2692:4074 */     for (int i = 0; i < metricsToDisplay.size(); i++)
/* 2693:     */     {
/* 2694:4075 */       optionsText.append((String)metricsToDisplay.get(i));
/* 2695:4076 */       length += ((String)metricsToDisplay.get(i)).length();
/* 2696:4077 */       if (i != metricsToDisplay.size() - 1) {
/* 2697:4078 */         optionsText.append(",");
/* 2698:     */       }
/* 2699:4080 */       if (length >= 60)
/* 2700:     */       {
/* 2701:4081 */         optionsText.append("\n\t");
/* 2702:4082 */         length = 0;
/* 2703:     */       }
/* 2704:     */     }
/* 2705:4085 */     optionsText.append("\n");
/* 2706:4086 */     optionsText.append("-l <name of input file>\n");
/* 2707:4087 */     optionsText.append("\tSets model input file. In case the filename ends with '.xml',\n");
/* 2708:     */     
/* 2709:4089 */     optionsText.append("\ta PMML file is loaded or, if that fails, options are loaded\n");
/* 2710:     */     
/* 2711:4091 */     optionsText.append("\tfrom the XML file.\n");
/* 2712:4092 */     optionsText.append("-d <name of output file>\n");
/* 2713:4093 */     optionsText.append("\tSets model output file. In case the filename ends with '.xml',\n");
/* 2714:     */     
/* 2715:4095 */     optionsText.append("\tonly the options are saved to the XML file, not the model.\n");
/* 2716:     */     
/* 2717:4097 */     optionsText.append("-v\n");
/* 2718:4098 */     optionsText.append("\tOutputs no statistics for training data.\n");
/* 2719:4099 */     optionsText.append("-o\n");
/* 2720:4100 */     optionsText.append("\tOutputs statistics only, not the classifier.\n");
/* 2721:4101 */     optionsText.append("-do-not-output-per-class-statistics\n");
/* 2722:4102 */     optionsText.append("\tDo not output statistics for each class.\n");
/* 2723:4103 */     optionsText.append("-k\n");
/* 2724:4104 */     optionsText.append("\tOutputs information-theoretic statistics.\n");
/* 2725:4105 */     optionsText.append("-classifications \"weka.classifiers.evaluation.output.prediction.AbstractOutput + options\"\n");
/* 2726:     */     
/* 2727:4107 */     optionsText.append("\tUses the specified class for generating the classification output.\n");
/* 2728:     */     
/* 2729:4109 */     optionsText.append("\tE.g.: " + PlainText.class.getName() + "\n");
/* 2730:4110 */     optionsText.append("-p range\n");
/* 2731:4111 */     optionsText.append("\tOutputs predictions for test instances (or the train instances if\n");
/* 2732:     */     
/* 2733:4113 */     optionsText.append("\tno test instances provided and -no-cv is used), along with the \n");
/* 2734:     */     
/* 2735:4115 */     optionsText.append("\tattributes in the specified range (and nothing else). \n");
/* 2736:     */     
/* 2737:4117 */     optionsText.append("\tUse '-p 0' if no attributes are desired.\n");
/* 2738:4118 */     optionsText.append("\tDeprecated: use \"-classifications ...\" instead.\n");
/* 2739:4119 */     optionsText.append("-distribution\n");
/* 2740:4120 */     optionsText.append("\tOutputs the distribution instead of only the prediction\n");
/* 2741:     */     
/* 2742:4122 */     optionsText.append("\tin conjunction with the '-p' option (only nominal classes).\n");
/* 2743:     */     
/* 2744:4124 */     optionsText.append("\tDeprecated: use \"-classifications ...\" instead.\n");
/* 2745:4125 */     optionsText.append("-r\n");
/* 2746:4126 */     optionsText.append("\tOnly outputs cumulative margin distribution.\n");
/* 2747:4127 */     if ((classifier instanceof Sourcable))
/* 2748:     */     {
/* 2749:4128 */       optionsText.append("-z <class name>\n");
/* 2750:4129 */       optionsText.append("\tOnly outputs the source representation of the classifier,\n\tgiving it the supplied name.\n");
/* 2751:     */     }
/* 2752:4132 */     if ((classifier instanceof Drawable))
/* 2753:     */     {
/* 2754:4133 */       optionsText.append("-g\n");
/* 2755:4134 */       optionsText.append("\tOnly outputs the graph representation of the classifier.\n");
/* 2756:     */     }
/* 2757:4137 */     optionsText.append("-xml filename | xml-string\n");
/* 2758:4138 */     optionsText.append("\tRetrieves the options from the XML-data instead of the command line.\n");
/* 2759:     */     
/* 2760:     */ 
/* 2761:4141 */     optionsText.append("-threshold-file <file>\n");
/* 2762:4142 */     optionsText.append("\tThe file to save the threshold data to.\n\tThe format is determined by the extensions, e.g., '.arff' for ARFF \n\tformat or '.csv' for CSV.\n");
/* 2763:     */     
/* 2764:     */ 
/* 2765:     */ 
/* 2766:4146 */     optionsText.append("-threshold-label <label>\n");
/* 2767:4147 */     optionsText.append("\tThe class label to determine the threshold data for\n\t(default is the first label)\n");
/* 2768:     */     
/* 2769:     */ 
/* 2770:4150 */     optionsText.append("-no-predictions\n");
/* 2771:4151 */     optionsText.append("\tTurns off the collection of predictions in order to conserve memory.\n");
/* 2772:4155 */     if ((classifier instanceof OptionHandler))
/* 2773:     */     {
/* 2774:4156 */       optionsText.append("\nOptions specific to " + classifier.getClass().getName() + ":\n\n");
/* 2775:     */       
/* 2776:4158 */       Enumeration<Option> enu = ((OptionHandler)classifier).listOptions();
/* 2777:4159 */       while (enu.hasMoreElements())
/* 2778:     */       {
/* 2779:4160 */         Option option = (Option)enu.nextElement();
/* 2780:4161 */         optionsText.append(option.synopsis() + '\n');
/* 2781:4162 */         optionsText.append(option.description() + "\n");
/* 2782:     */       }
/* 2783:     */     }
/* 2784:4167 */     if (globalInfo) {
/* 2785:     */       try
/* 2786:     */       {
/* 2787:4169 */         String gi = getGlobalInfo(classifier);
/* 2788:4170 */         optionsText.append(gi);
/* 2789:     */       }
/* 2790:     */       catch (Exception ex) {}
/* 2791:     */     }
/* 2792:4175 */     return optionsText.toString();
/* 2793:     */   }
/* 2794:     */   
/* 2795:     */   protected static String getGlobalInfo(Classifier classifier)
/* 2796:     */     throws Exception
/* 2797:     */   {
/* 2798:4186 */     BeanInfo bi = Introspector.getBeanInfo(classifier.getClass());
/* 2799:     */     
/* 2800:4188 */     MethodDescriptor[] methods = bi.getMethodDescriptors();
/* 2801:4189 */     Object[] args = new Object[0];
/* 2802:4190 */     String result = "\nSynopsis for " + classifier.getClass().getName() + ":\n\n";
/* 2803:4193 */     for (MethodDescriptor method : methods)
/* 2804:     */     {
/* 2805:4194 */       String name = method.getDisplayName();
/* 2806:4195 */       Method meth = method.getMethod();
/* 2807:4196 */       if (name.equals("globalInfo"))
/* 2808:     */       {
/* 2809:4197 */         String globalInfo = (String)meth.invoke(classifier, args);
/* 2810:4198 */         result = result + globalInfo;
/* 2811:4199 */         break;
/* 2812:     */       }
/* 2813:     */     }
/* 2814:4203 */     return result;
/* 2815:     */   }
/* 2816:     */   
/* 2817:     */   protected String num2ShortID(int num, char[] IDChars, int IDWidth)
/* 2818:     */   {
/* 2819:4216 */     char[] ID = new char[IDWidth];
/* 2820:4219 */     for (int i = IDWidth - 1; i >= 0; i--)
/* 2821:     */     {
/* 2822:4220 */       ID[i] = IDChars[(num % IDChars.length)];
/* 2823:4221 */       num = num / IDChars.length - 1;
/* 2824:4222 */       if (num < 0) {
/* 2825:     */         break;
/* 2826:     */       }
/* 2827:     */     }
/* 2828:4226 */     for (i--; i >= 0; i--) {
/* 2829:4227 */       ID[i] = ' ';
/* 2830:     */     }
/* 2831:4230 */     return new String(ID);
/* 2832:     */   }
/* 2833:     */   
/* 2834:     */   protected double[] makeDistribution(double predictedClass)
/* 2835:     */   {
/* 2836:4242 */     double[] result = new double[this.m_NumClasses];
/* 2837:4243 */     if (Utils.isMissingValue(predictedClass)) {
/* 2838:4244 */       return result;
/* 2839:     */     }
/* 2840:4246 */     if (this.m_ClassIsNominal) {
/* 2841:4247 */       result[((int)predictedClass)] = 1.0D;
/* 2842:     */     } else {
/* 2843:4249 */       result[0] = predictedClass;
/* 2844:     */     }
/* 2845:4251 */     return result;
/* 2846:     */   }
/* 2847:     */   
/* 2848:     */   protected void updateStatsForClassifier(double[] predictedDistribution, Instance instance)
/* 2849:     */     throws Exception
/* 2850:     */   {
/* 2851:4265 */     int actualClass = (int)instance.classValue();
/* 2852:4267 */     if (!instance.classIsMissing())
/* 2853:     */     {
/* 2854:4268 */       updateMargins(predictedDistribution, actualClass, instance.weight());
/* 2855:     */       
/* 2856:     */ 
/* 2857:     */ 
/* 2858:4272 */       int predictedClass = -1;
/* 2859:4273 */       double bestProb = 0.0D;
/* 2860:4274 */       for (int i = 0; i < this.m_NumClasses; i++) {
/* 2861:4275 */         if (predictedDistribution[i] > bestProb)
/* 2862:     */         {
/* 2863:4276 */           predictedClass = i;
/* 2864:4277 */           bestProb = predictedDistribution[i];
/* 2865:     */         }
/* 2866:     */       }
/* 2867:4281 */       this.m_WithClass += instance.weight();
/* 2868:4284 */       if (this.m_CostMatrix != null) {
/* 2869:4285 */         if (predictedClass < 0) {
/* 2870:4291 */           this.m_TotalCost += instance.weight() * this.m_CostMatrix.getMaxCost(actualClass, instance);
/* 2871:     */         } else {
/* 2872:4294 */           this.m_TotalCost += instance.weight() * this.m_CostMatrix.getElement(actualClass, predictedClass, instance);
/* 2873:     */         }
/* 2874:     */       }
/* 2875:4301 */       if (predictedClass < 0)
/* 2876:     */       {
/* 2877:4302 */         this.m_Unclassified += instance.weight();
/* 2878:4303 */         return;
/* 2879:     */       }
/* 2880:4306 */       double predictedProb = Math.max(4.9E-324D, predictedDistribution[actualClass]);
/* 2881:     */       
/* 2882:4308 */       double priorProb = Math.max(4.9E-324D, this.m_ClassPriors[actualClass] / this.m_ClassPriorsSum);
/* 2883:4310 */       if (predictedProb >= priorProb) {
/* 2884:4311 */         this.m_SumKBInfo += (Utils.log2(predictedProb) - Utils.log2(priorProb)) * instance.weight();
/* 2885:     */       } else {
/* 2886:4315 */         this.m_SumKBInfo -= (Utils.log2(1.0D - predictedProb) - Utils.log2(1.0D - priorProb)) * instance.weight();
/* 2887:     */       }
/* 2888:4320 */       this.m_SumSchemeEntropy -= Utils.log2(predictedProb) * instance.weight();
/* 2889:4321 */       this.m_SumPriorEntropy -= Utils.log2(priorProb) * instance.weight();
/* 2890:     */       
/* 2891:4323 */       updateNumericScores(predictedDistribution, makeDistribution(instance.classValue()), instance.weight());
/* 2892:     */       
/* 2893:     */ 
/* 2894:     */ 
/* 2895:4327 */       int[] indices = Utils.stableSort(predictedDistribution);
/* 2896:4328 */       double sum = 0.0D;double sizeOfRegions = 0.0D;
/* 2897:4329 */       for (int i = predictedDistribution.length - 1; i >= 0; i--)
/* 2898:     */       {
/* 2899:4330 */         if (sum >= this.m_ConfLevel) {
/* 2900:     */           break;
/* 2901:     */         }
/* 2902:4333 */         sum += predictedDistribution[indices[i]];
/* 2903:4334 */         sizeOfRegions += 1.0D;
/* 2904:4335 */         if (actualClass == indices[i]) {
/* 2905:4336 */           this.m_TotalCoverage += instance.weight();
/* 2906:     */         }
/* 2907:     */       }
/* 2908:4339 */       this.m_TotalSizeOfRegions += instance.weight() * sizeOfRegions / (this.m_MaxTarget - this.m_MinTarget);
/* 2909:     */       
/* 2910:     */ 
/* 2911:4342 */       this.m_ConfusionMatrix[actualClass][predictedClass] += instance.weight();
/* 2912:4343 */       if (predictedClass != actualClass) {
/* 2913:4344 */         this.m_Incorrect += instance.weight();
/* 2914:     */       } else {
/* 2915:4346 */         this.m_Correct += instance.weight();
/* 2916:     */       }
/* 2917:     */     }
/* 2918:     */     else
/* 2919:     */     {
/* 2920:4349 */       this.m_MissingClass += instance.weight();
/* 2921:     */     }
/* 2922:4352 */     if (this.m_pluginMetrics != null) {
/* 2923:4353 */       for (AbstractEvaluationMetric m : this.m_pluginMetrics) {
/* 2924:4354 */         if ((m instanceof StandardEvaluationMetric)) {
/* 2925:4355 */           ((StandardEvaluationMetric)m).updateStatsForClassifier(predictedDistribution, instance);
/* 2926:4357 */         } else if ((m instanceof InformationRetrievalEvaluationMetric)) {
/* 2927:4358 */           ((InformationRetrievalEvaluationMetric)m).updateStatsForClassifier(predictedDistribution, instance);
/* 2928:4360 */         } else if ((m instanceof InformationTheoreticEvaluationMetric)) {
/* 2929:4361 */           ((InformationTheoreticEvaluationMetric)m).updateStatsForClassifier(predictedDistribution, instance);
/* 2930:     */         }
/* 2931:     */       }
/* 2932:     */     }
/* 2933:     */   }
/* 2934:     */   
/* 2935:     */   protected void updateStatsForIntervalEstimator(IntervalEstimator classifier, Instance classMissing, double classValue)
/* 2936:     */     throws Exception
/* 2937:     */   {
/* 2938:4380 */     double[][] preds = classifier.predictIntervals(classMissing, this.m_ConfLevel);
/* 2939:4381 */     if (this.m_Predictions != null) {
/* 2940:4382 */       ((NumericPrediction)this.m_Predictions.get(this.m_Predictions.size() - 1)).setPredictionIntervals(preds);
/* 2941:     */     }
/* 2942:4385 */     for (double[] pred : preds) {
/* 2943:4386 */       this.m_TotalSizeOfRegions += classMissing.weight() * (pred[1] - pred[0]) / (this.m_MaxTarget - this.m_MinTarget);
/* 2944:     */     }
/* 2945:4388 */     for (double[] pred : preds) {
/* 2946:4389 */       if ((pred[1] >= classValue) && (pred[0] <= classValue))
/* 2947:     */       {
/* 2948:4390 */         this.m_TotalCoverage += classMissing.weight();
/* 2949:4391 */         break;
/* 2950:     */       }
/* 2951:     */     }
/* 2952:4395 */     if (this.m_pluginMetrics != null) {
/* 2953:4396 */       for (AbstractEvaluationMetric m : this.m_pluginMetrics) {
/* 2954:4397 */         if ((m instanceof IntervalBasedEvaluationMetric)) {
/* 2955:4398 */           ((IntervalBasedEvaluationMetric)m).updateStatsForIntervalEstimator(classifier, classMissing, classValue);
/* 2956:     */         }
/* 2957:     */       }
/* 2958:     */     }
/* 2959:     */   }
/* 2960:     */   
/* 2961:     */   protected void updateStatsForConditionalDensityEstimator(ConditionalDensityEstimator classifier, Instance classMissing, double classValue)
/* 2962:     */     throws Exception
/* 2963:     */   {
/* 2964:4419 */     if (this.m_PriorEstimator == null) {
/* 2965:4420 */       setNumericPriorsFromBuffer();
/* 2966:     */     }
/* 2967:4422 */     this.m_SumSchemeEntropy -= classifier.logDensity(classMissing, classValue) * classMissing.weight() / Utils.log2;
/* 2968:     */     
/* 2969:     */ 
/* 2970:4425 */     this.m_SumPriorEntropy -= this.m_PriorEstimator.logDensity(classValue) * classMissing.weight() / Utils.log2;
/* 2971:     */   }
/* 2972:     */   
/* 2973:     */   protected void updateStatsForPredictor(double predictedValue, Instance instance)
/* 2974:     */     throws Exception
/* 2975:     */   {
/* 2976:4441 */     if (!instance.classIsMissing())
/* 2977:     */     {
/* 2978:4444 */       this.m_WithClass += instance.weight();
/* 2979:4445 */       if (Utils.isMissingValue(predictedValue))
/* 2980:     */       {
/* 2981:4446 */         this.m_Unclassified += instance.weight();
/* 2982:4447 */         return;
/* 2983:     */       }
/* 2984:4449 */       this.m_SumClass += instance.weight() * instance.classValue();
/* 2985:4450 */       this.m_SumSqrClass += instance.weight() * instance.classValue() * instance.classValue();
/* 2986:     */       
/* 2987:4452 */       this.m_SumClassPredicted += instance.weight() * instance.classValue() * predictedValue;
/* 2988:     */       
/* 2989:4454 */       this.m_SumPredicted += instance.weight() * predictedValue;
/* 2990:4455 */       this.m_SumSqrPredicted += instance.weight() * predictedValue * predictedValue;
/* 2991:     */       
/* 2992:4457 */       updateNumericScores(makeDistribution(predictedValue), makeDistribution(instance.classValue()), instance.weight());
/* 2993:     */     }
/* 2994:     */     else
/* 2995:     */     {
/* 2996:4461 */       this.m_MissingClass += instance.weight();
/* 2997:     */     }
/* 2998:4464 */     if (this.m_pluginMetrics != null) {
/* 2999:4465 */       for (AbstractEvaluationMetric m : this.m_pluginMetrics) {
/* 3000:4466 */         if ((m instanceof StandardEvaluationMetric)) {
/* 3001:4467 */           ((StandardEvaluationMetric)m).updateStatsForPredictor(predictedValue, instance);
/* 3002:4469 */         } else if ((m instanceof InformationTheoreticEvaluationMetric)) {
/* 3003:4470 */           ((InformationTheoreticEvaluationMetric)m).updateStatsForPredictor(predictedValue, instance);
/* 3004:     */         }
/* 3005:     */       }
/* 3006:     */     }
/* 3007:     */   }
/* 3008:     */   
/* 3009:     */   protected void updateMargins(double[] predictedDistribution, int actualClass, double weight)
/* 3010:     */   {
/* 3011:4488 */     double probActual = predictedDistribution[actualClass];
/* 3012:4489 */     double probNext = 0.0D;
/* 3013:4491 */     for (int i = 0; i < this.m_NumClasses; i++) {
/* 3014:4492 */       if ((i != actualClass) && (predictedDistribution[i] > probNext)) {
/* 3015:4493 */         probNext = predictedDistribution[i];
/* 3016:     */       }
/* 3017:     */     }
/* 3018:4497 */     double margin = probActual - probNext;
/* 3019:4498 */     int bin = (int)((margin + 1.0D) / 2.0D * k_MarginResolution);
/* 3020:4499 */     this.m_MarginCounts[bin] += weight;
/* 3021:     */   }
/* 3022:     */   
/* 3023:     */   protected void updateNumericScores(double[] predicted, double[] actual, double weight)
/* 3024:     */   {
/* 3025:4515 */     double sumErr = 0.0D;double sumAbsErr = 0.0D;double sumSqrErr = 0.0D;
/* 3026:4516 */     double sumPriorAbsErr = 0.0D;double sumPriorSqrErr = 0.0D;
/* 3027:4517 */     for (int i = 0; i < this.m_NumClasses; i++)
/* 3028:     */     {
/* 3029:4518 */       double diff = predicted[i] - actual[i];
/* 3030:4519 */       sumErr += diff;
/* 3031:4520 */       sumAbsErr += Math.abs(diff);
/* 3032:4521 */       sumSqrErr += diff * diff;
/* 3033:4522 */       diff = this.m_ClassPriors[i] / this.m_ClassPriorsSum - actual[i];
/* 3034:4523 */       sumPriorAbsErr += Math.abs(diff);
/* 3035:4524 */       sumPriorSqrErr += diff * diff;
/* 3036:     */     }
/* 3037:4526 */     this.m_SumErr += weight * sumErr / this.m_NumClasses;
/* 3038:4527 */     this.m_SumAbsErr += weight * sumAbsErr / this.m_NumClasses;
/* 3039:4528 */     this.m_SumSqrErr += weight * sumSqrErr / this.m_NumClasses;
/* 3040:4529 */     this.m_SumPriorAbsErr += weight * sumPriorAbsErr / this.m_NumClasses;
/* 3041:4530 */     this.m_SumPriorSqrErr += weight * sumPriorSqrErr / this.m_NumClasses;
/* 3042:     */   }
/* 3043:     */   
/* 3044:     */   protected void addNumericTrainClass(double classValue, double weight)
/* 3045:     */   {
/* 3046:4543 */     if (classValue > this.m_MaxTarget) {
/* 3047:4544 */       this.m_MaxTarget = classValue;
/* 3048:     */     }
/* 3049:4546 */     if (classValue < this.m_MinTarget) {
/* 3050:4547 */       this.m_MinTarget = classValue;
/* 3051:     */     }
/* 3052:4551 */     if (this.m_TrainClassVals == null)
/* 3053:     */     {
/* 3054:4552 */       this.m_TrainClassVals = new double[100];
/* 3055:4553 */       this.m_TrainClassWeights = new double[100];
/* 3056:     */     }
/* 3057:4555 */     if (this.m_NumTrainClassVals == this.m_TrainClassVals.length)
/* 3058:     */     {
/* 3059:4556 */       double[] temp = new double[this.m_TrainClassVals.length * 2];
/* 3060:4557 */       System.arraycopy(this.m_TrainClassVals, 0, temp, 0, this.m_TrainClassVals.length);
/* 3061:4558 */       this.m_TrainClassVals = temp;
/* 3062:     */       
/* 3063:4560 */       temp = new double[this.m_TrainClassWeights.length * 2];
/* 3064:4561 */       System.arraycopy(this.m_TrainClassWeights, 0, temp, 0, this.m_TrainClassWeights.length);
/* 3065:     */       
/* 3066:4563 */       this.m_TrainClassWeights = temp;
/* 3067:     */     }
/* 3068:4565 */     this.m_TrainClassVals[this.m_NumTrainClassVals] = classValue;
/* 3069:4566 */     this.m_TrainClassWeights[this.m_NumTrainClassVals] = weight;
/* 3070:4567 */     this.m_NumTrainClassVals += 1;
/* 3071:     */   }
/* 3072:     */   
/* 3073:     */   protected void setNumericPriorsFromBuffer()
/* 3074:     */   {
/* 3075:4576 */     this.m_PriorEstimator = new UnivariateKernelEstimator();
/* 3076:4577 */     for (int i = 0; i < this.m_NumTrainClassVals; i++) {
/* 3077:4578 */       this.m_PriorEstimator.addValue(this.m_TrainClassVals[i], this.m_TrainClassWeights[i]);
/* 3078:     */     }
/* 3079:     */   }
/* 3080:     */   
/* 3081:     */   public String getRevision()
/* 3082:     */   {
/* 3083:4589 */     return RevisionUtils.extract("$Revision: 12780 $");
/* 3084:     */   }
/* 3085:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.evaluation.Evaluation
 * JD-Core Version:    0.7.0.1
 */