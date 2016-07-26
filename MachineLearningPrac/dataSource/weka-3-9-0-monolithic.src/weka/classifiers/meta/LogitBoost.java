/*    1:     */ package weka.classifiers.meta;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.util.ArrayList;
/*    5:     */ import java.util.Collections;
/*    6:     */ import java.util.Enumeration;
/*    7:     */ import java.util.HashSet;
/*    8:     */ import java.util.Random;
/*    9:     */ import java.util.Set;
/*   10:     */ import java.util.Vector;
/*   11:     */ import java.util.concurrent.Callable;
/*   12:     */ import java.util.concurrent.ExecutorService;
/*   13:     */ import java.util.concurrent.Executors;
/*   14:     */ import java.util.concurrent.Future;
/*   15:     */ import weka.classifiers.AbstractClassifier;
/*   16:     */ import weka.classifiers.Classifier;
/*   17:     */ import weka.classifiers.IterativeClassifier;
/*   18:     */ import weka.classifiers.RandomizableIteratedSingleClassifierEnhancer;
/*   19:     */ import weka.classifiers.Sourcable;
/*   20:     */ import weka.classifiers.rules.ZeroR;
/*   21:     */ import weka.classifiers.trees.DecisionStump;
/*   22:     */ import weka.core.Attribute;
/*   23:     */ import weka.core.BatchPredictor;
/*   24:     */ import weka.core.Capabilities;
/*   25:     */ import weka.core.Capabilities.Capability;
/*   26:     */ import weka.core.Instance;
/*   27:     */ import weka.core.Instances;
/*   28:     */ import weka.core.Option;
/*   29:     */ import weka.core.RevisionUtils;
/*   30:     */ import weka.core.TechnicalInformation;
/*   31:     */ import weka.core.TechnicalInformation.Field;
/*   32:     */ import weka.core.TechnicalInformation.Type;
/*   33:     */ import weka.core.TechnicalInformationHandler;
/*   34:     */ import weka.core.UnassignedClassException;
/*   35:     */ import weka.core.Utils;
/*   36:     */ import weka.core.WeightedInstancesHandler;
/*   37:     */ 
/*   38:     */ public class LogitBoost
/*   39:     */   extends RandomizableIteratedSingleClassifierEnhancer
/*   40:     */   implements Sourcable, WeightedInstancesHandler, TechnicalInformationHandler, IterativeClassifier, BatchPredictor
/*   41:     */ {
/*   42:     */   static final long serialVersionUID = -1105660358715833753L;
/*   43:     */   protected ArrayList<Classifier[]> m_Classifiers;
/*   44:     */   protected int m_NumClasses;
/*   45:     */   protected int m_NumGenerated;
/*   46: 202 */   protected int m_WeightThreshold = 100;
/*   47:     */   protected static final double DEFAULT_Z_MAX = 3.0D;
/*   48:     */   protected Instances m_NumericClassData;
/*   49:     */   protected Attribute m_ClassAttribute;
/*   50:     */   protected boolean m_UseResampling;
/*   51: 217 */   protected double m_Precision = -1.797693134862316E+308D;
/*   52: 220 */   protected double m_Shrinkage = 1.0D;
/*   53: 223 */   protected Random m_RandomInstance = null;
/*   54: 228 */   protected double m_Offset = 0.0D;
/*   55:     */   protected Classifier m_ZeroR;
/*   56: 234 */   protected double m_zMax = 3.0D;
/*   57:     */   protected double[][] m_trainYs;
/*   58:     */   protected double[][] m_trainFs;
/*   59:     */   protected double[][] m_probs;
/*   60:     */   protected double m_logLikelihood;
/*   61:     */   protected double m_sumOfWeights;
/*   62:     */   protected Instances m_data;
/*   63: 255 */   protected int m_numThreads = 1;
/*   64: 258 */   protected int m_poolSize = 1;
/*   65:     */   
/*   66:     */   public String globalInfo()
/*   67:     */   {
/*   68: 268 */     return "Class for performing additive logistic regression. \nThis class performs classification using a regression scheme as the base learner, and can handle multi-class problems.  For more information, see\n\n" + getTechnicalInformation().toString() + "\n\n" + "Can do efficient internal cross-validation to determine " + "appropriate number of iterations.";
/*   69:     */   }
/*   70:     */   
/*   71:     */   public LogitBoost()
/*   72:     */   {
/*   73: 281 */     this.m_Classifier = new DecisionStump();
/*   74:     */   }
/*   75:     */   
/*   76:     */   public TechnicalInformation getTechnicalInformation()
/*   77:     */   {
/*   78: 294 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.TECHREPORT);
/*   79: 295 */     result.setValue(TechnicalInformation.Field.AUTHOR, "J. Friedman and T. Hastie and R. Tibshirani");
/*   80:     */     
/*   81: 297 */     result.setValue(TechnicalInformation.Field.YEAR, "1998");
/*   82: 298 */     result.setValue(TechnicalInformation.Field.TITLE, "Additive Logistic Regression: a Statistical View of Boosting");
/*   83:     */     
/*   84: 300 */     result.setValue(TechnicalInformation.Field.ADDRESS, "Stanford University");
/*   85: 301 */     result.setValue(TechnicalInformation.Field.PS, "http://www-stat.stanford.edu/~jhf/ftp/boost.ps");
/*   86:     */     
/*   87: 303 */     return result;
/*   88:     */   }
/*   89:     */   
/*   90:     */   protected String defaultClassifierString()
/*   91:     */   {
/*   92: 313 */     return "weka.classifiers.trees.DecisionStump";
/*   93:     */   }
/*   94:     */   
/*   95:     */   protected Instances selectWeightQuantile(Instances data, double quantile)
/*   96:     */   {
/*   97: 327 */     int numInstances = data.numInstances();
/*   98: 328 */     Instances trainData = new Instances(data, numInstances);
/*   99: 329 */     double[] weights = new double[numInstances];
/*  100:     */     
/*  101: 331 */     double sumOfWeights = 0.0D;
/*  102: 332 */     for (int i = 0; i < numInstances; i++)
/*  103:     */     {
/*  104: 333 */       weights[i] = data.instance(i).weight();
/*  105: 334 */       sumOfWeights += weights[i];
/*  106:     */     }
/*  107: 336 */     double weightMassToSelect = sumOfWeights * quantile;
/*  108: 337 */     int[] sortedIndices = Utils.sort(weights);
/*  109:     */     
/*  110:     */ 
/*  111: 340 */     sumOfWeights = 0.0D;
/*  112: 341 */     for (int i = numInstances - 1; i >= 0; i--)
/*  113:     */     {
/*  114: 342 */       Instance instance = (Instance)data.instance(sortedIndices[i]).copy();
/*  115: 343 */       trainData.add(instance);
/*  116: 344 */       sumOfWeights += weights[sortedIndices[i]];
/*  117: 345 */       if ((sumOfWeights > weightMassToSelect) && (i > 0) && (weights[sortedIndices[i]] != weights[sortedIndices[(i - 1)]])) {
/*  118:     */         break;
/*  119:     */       }
/*  120:     */     }
/*  121: 350 */     if (this.m_Debug) {
/*  122: 351 */       System.err.println("Selected " + trainData.numInstances() + " out of " + numInstances);
/*  123:     */     }
/*  124: 354 */     return trainData;
/*  125:     */   }
/*  126:     */   
/*  127:     */   public Enumeration<Option> listOptions()
/*  128:     */   {
/*  129: 364 */     Vector<Option> newVector = new Vector(5);
/*  130:     */     
/*  131: 366 */     newVector.addElement(new Option("\tUse resampling instead of reweighting for boosting.", "Q", 0, "-Q"));
/*  132:     */     
/*  133: 368 */     newVector.addElement(new Option("\tPercentage of weight mass to base training on.\n\t(default 100, reduce to around 90 speed up)", "P", 1, "-P <percent>"));
/*  134:     */     
/*  135:     */ 
/*  136:     */ 
/*  137: 372 */     newVector.addElement(new Option("\tThreshold on the improvement of the likelihood.\n\t(default -Double.MAX_VALUE)", "L", 1, "-L <num>"));
/*  138:     */     
/*  139:     */ 
/*  140: 375 */     newVector.addElement(new Option("\tShrinkage parameter.\n\t(default 1)", "H", 1, "-H <num>"));
/*  141:     */     
/*  142: 377 */     newVector.addElement(new Option("\tZ max threshold for responses.\n\t(default 3)", "Z", 1, "-Z <num>"));
/*  143:     */     
/*  144: 379 */     newVector.addElement(new Option("\t" + poolSizeTipText() + " (default 1)", "O", 1, "-O <int>"));
/*  145:     */     
/*  146: 381 */     newVector.addElement(new Option("\t" + numThreadsTipText() + "\n" + "\t(default 1)", "E", 1, "-E <int>"));
/*  147:     */     
/*  148:     */ 
/*  149: 384 */     newVector.addAll(Collections.list(super.listOptions()));
/*  150:     */     
/*  151: 386 */     return newVector.elements();
/*  152:     */   }
/*  153:     */   
/*  154:     */   public void setOptions(String[] options)
/*  155:     */     throws Exception
/*  156:     */   {
/*  157: 492 */     String thresholdString = Utils.getOption('P', options);
/*  158: 493 */     if (thresholdString.length() != 0) {
/*  159: 494 */       setWeightThreshold(Integer.parseInt(thresholdString));
/*  160:     */     } else {
/*  161: 496 */       setWeightThreshold(100);
/*  162:     */     }
/*  163: 499 */     String precisionString = Utils.getOption('L', options);
/*  164: 500 */     if (precisionString.length() != 0) {
/*  165: 501 */       setLikelihoodThreshold(new Double(precisionString).doubleValue());
/*  166:     */     } else {
/*  167: 503 */       setLikelihoodThreshold(-1.797693134862316E+308D);
/*  168:     */     }
/*  169: 506 */     String shrinkageString = Utils.getOption('H', options);
/*  170: 507 */     if (shrinkageString.length() != 0) {
/*  171: 508 */       setShrinkage(new Double(shrinkageString).doubleValue());
/*  172:     */     } else {
/*  173: 510 */       setShrinkage(1.0D);
/*  174:     */     }
/*  175: 513 */     String zString = Utils.getOption('Z', options);
/*  176: 514 */     if (zString.length() > 0) {
/*  177: 515 */       setZMax(Double.parseDouble(zString));
/*  178:     */     }
/*  179: 518 */     setUseResampling(Utils.getFlag('Q', options));
/*  180: 519 */     if ((this.m_UseResampling) && (thresholdString.length() != 0)) {
/*  181: 520 */       throw new Exception("Weight pruning with resamplingnot allowed.");
/*  182:     */     }
/*  183: 522 */     String PoolSize = Utils.getOption('O', options);
/*  184: 523 */     if (PoolSize.length() != 0) {
/*  185: 524 */       setPoolSize(Integer.parseInt(PoolSize));
/*  186:     */     } else {
/*  187: 526 */       setPoolSize(1);
/*  188:     */     }
/*  189: 528 */     String NumThreads = Utils.getOption('E', options);
/*  190: 529 */     if (NumThreads.length() != 0) {
/*  191: 530 */       setNumThreads(Integer.parseInt(NumThreads));
/*  192:     */     } else {
/*  193: 532 */       setNumThreads(1);
/*  194:     */     }
/*  195: 535 */     super.setOptions(options);
/*  196:     */     
/*  197: 537 */     Utils.checkForRemainingOptions(options);
/*  198:     */   }
/*  199:     */   
/*  200:     */   public String[] getOptions()
/*  201:     */   {
/*  202: 547 */     Vector<String> options = new Vector();
/*  203: 549 */     if (getUseResampling())
/*  204:     */     {
/*  205: 550 */       options.add("-Q");
/*  206:     */     }
/*  207:     */     else
/*  208:     */     {
/*  209: 552 */       options.add("-P");
/*  210: 553 */       options.add("" + getWeightThreshold());
/*  211:     */     }
/*  212: 555 */     options.add("-L");
/*  213: 556 */     options.add("" + getLikelihoodThreshold());
/*  214: 557 */     options.add("-H");
/*  215: 558 */     options.add("" + getShrinkage());
/*  216: 559 */     options.add("-Z");
/*  217: 560 */     options.add("" + getZMax());
/*  218:     */     
/*  219: 562 */     options.add("-O");
/*  220: 563 */     options.add("" + getPoolSize());
/*  221:     */     
/*  222: 565 */     options.add("-E");
/*  223: 566 */     options.add("" + getNumThreads());
/*  224:     */     
/*  225: 568 */     Collections.addAll(options, super.getOptions());
/*  226:     */     
/*  227: 570 */     return (String[])options.toArray(new String[0]);
/*  228:     */   }
/*  229:     */   
/*  230:     */   public String ZMaxTipText()
/*  231:     */   {
/*  232: 580 */     return "Z max threshold for responses";
/*  233:     */   }
/*  234:     */   
/*  235:     */   public void setZMax(double zMax)
/*  236:     */   {
/*  237: 589 */     this.m_zMax = zMax;
/*  238:     */   }
/*  239:     */   
/*  240:     */   public double getZMax()
/*  241:     */   {
/*  242: 598 */     return this.m_zMax;
/*  243:     */   }
/*  244:     */   
/*  245:     */   public String shrinkageTipText()
/*  246:     */   {
/*  247: 608 */     return "Shrinkage parameter (use small value like 0.1 to reduce overfitting).";
/*  248:     */   }
/*  249:     */   
/*  250:     */   public double getShrinkage()
/*  251:     */   {
/*  252: 619 */     return this.m_Shrinkage;
/*  253:     */   }
/*  254:     */   
/*  255:     */   public void setShrinkage(double newShrinkage)
/*  256:     */   {
/*  257: 629 */     this.m_Shrinkage = newShrinkage;
/*  258:     */   }
/*  259:     */   
/*  260:     */   public String likelihoodThresholdTipText()
/*  261:     */   {
/*  262: 639 */     return "Threshold on improvement in likelihood.";
/*  263:     */   }
/*  264:     */   
/*  265:     */   public double getLikelihoodThreshold()
/*  266:     */   {
/*  267: 649 */     return this.m_Precision;
/*  268:     */   }
/*  269:     */   
/*  270:     */   public void setLikelihoodThreshold(double newPrecision)
/*  271:     */   {
/*  272: 659 */     this.m_Precision = newPrecision;
/*  273:     */   }
/*  274:     */   
/*  275:     */   public String useResamplingTipText()
/*  276:     */   {
/*  277: 669 */     return "Whether resampling is used instead of reweighting.";
/*  278:     */   }
/*  279:     */   
/*  280:     */   public void setUseResampling(boolean r)
/*  281:     */   {
/*  282: 679 */     this.m_UseResampling = r;
/*  283:     */   }
/*  284:     */   
/*  285:     */   public boolean getUseResampling()
/*  286:     */   {
/*  287: 689 */     return this.m_UseResampling;
/*  288:     */   }
/*  289:     */   
/*  290:     */   public String weightThresholdTipText()
/*  291:     */   {
/*  292: 699 */     return "Weight threshold for weight pruning (reduce to 90 for speeding up learning process).";
/*  293:     */   }
/*  294:     */   
/*  295:     */   public void setWeightThreshold(int threshold)
/*  296:     */   {
/*  297: 710 */     this.m_WeightThreshold = threshold;
/*  298:     */   }
/*  299:     */   
/*  300:     */   public int getWeightThreshold()
/*  301:     */   {
/*  302: 720 */     return this.m_WeightThreshold;
/*  303:     */   }
/*  304:     */   
/*  305:     */   public String numThreadsTipText()
/*  306:     */   {
/*  307: 728 */     return "The number of threads to use for batch prediction, which should be >= size of thread pool.";
/*  308:     */   }
/*  309:     */   
/*  310:     */   public int getNumThreads()
/*  311:     */   {
/*  312: 736 */     return this.m_numThreads;
/*  313:     */   }
/*  314:     */   
/*  315:     */   public void setNumThreads(int nT)
/*  316:     */   {
/*  317: 744 */     this.m_numThreads = nT;
/*  318:     */   }
/*  319:     */   
/*  320:     */   public String poolSizeTipText()
/*  321:     */   {
/*  322: 752 */     return "The size of the thread pool, for example, the number of cores in the CPU.";
/*  323:     */   }
/*  324:     */   
/*  325:     */   public int getPoolSize()
/*  326:     */   {
/*  327: 760 */     return this.m_poolSize;
/*  328:     */   }
/*  329:     */   
/*  330:     */   public void setPoolSize(int nT)
/*  331:     */   {
/*  332: 768 */     this.m_poolSize = nT;
/*  333:     */   }
/*  334:     */   
/*  335:     */   public Capabilities getCapabilities()
/*  336:     */   {
/*  337: 777 */     Capabilities result = super.getCapabilities();
/*  338:     */     
/*  339:     */ 
/*  340: 780 */     result.disableAllClasses();
/*  341: 781 */     result.disableAllClassDependencies();
/*  342: 782 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  343:     */     
/*  344: 784 */     return result;
/*  345:     */   }
/*  346:     */   
/*  347:     */   public void buildClassifier(Instances data)
/*  348:     */     throws Exception
/*  349:     */   {
/*  350: 793 */     initializeClassifier(data);
/*  351: 796 */     while (next()) {}
/*  352: 801 */     done();
/*  353:     */   }
/*  354:     */   
/*  355:     */   public void initializeClassifier(Instances data)
/*  356:     */     throws Exception
/*  357:     */   {
/*  358: 812 */     this.m_RandomInstance = new Random(this.m_Seed);
/*  359: 813 */     int classIndex = data.classIndex();
/*  360: 815 */     if (this.m_Classifier == null) {
/*  361: 816 */       throw new Exception("A base classifier has not been specified!");
/*  362:     */     }
/*  363: 819 */     if ((!(this.m_Classifier instanceof WeightedInstancesHandler)) && (!this.m_UseResampling)) {
/*  364: 820 */       this.m_UseResampling = true;
/*  365:     */     }
/*  366: 824 */     getCapabilities().testWithFail(data);
/*  367: 826 */     if (this.m_Debug) {
/*  368: 827 */       System.err.println("Creating copy of the training data");
/*  369:     */     }
/*  370: 831 */     this.m_data = new Instances(data);
/*  371: 832 */     this.m_data.deleteWithMissingClass();
/*  372: 835 */     if (this.m_data.numAttributes() == 1)
/*  373:     */     {
/*  374: 836 */       System.err.println("Cannot build model (only class attribute present in data!), using ZeroR model instead!");
/*  375:     */       
/*  376:     */ 
/*  377: 839 */       this.m_ZeroR = new ZeroR();
/*  378: 840 */       this.m_ZeroR.buildClassifier(this.m_data);
/*  379: 841 */       return;
/*  380:     */     }
/*  381: 843 */     this.m_ZeroR = null;
/*  382:     */     
/*  383:     */ 
/*  384: 846 */     this.m_NumClasses = this.m_data.numClasses();
/*  385: 847 */     this.m_ClassAttribute = this.m_data.classAttribute();
/*  386: 850 */     if (this.m_Debug) {
/*  387: 851 */       System.err.println("Creating base classifiers");
/*  388:     */     }
/*  389: 853 */     this.m_Classifiers = new ArrayList();
/*  390:     */     
/*  391:     */ 
/*  392: 856 */     int numInstances = this.m_data.numInstances();
/*  393: 857 */     this.m_trainFs = new double[numInstances][this.m_NumClasses];
/*  394: 858 */     this.m_trainYs = new double[numInstances][this.m_NumClasses];
/*  395: 859 */     for (int j = 0; j < this.m_NumClasses; j++)
/*  396:     */     {
/*  397: 860 */       int i = 0;
/*  398: 860 */       for (int k = 0; i < numInstances; k++)
/*  399:     */       {
/*  400: 861 */         this.m_trainYs[i][j] = (this.m_data.instance(k).classValue() == j ? 1.0D - this.m_Offset : 0.0D + this.m_Offset / this.m_NumClasses);i++;
/*  401:     */       }
/*  402:     */     }
/*  403: 868 */     this.m_data.setClassIndex(-1);
/*  404: 869 */     this.m_data.deleteAttributeAt(classIndex);
/*  405: 870 */     this.m_data.insertAttributeAt(new Attribute("'pseudo class'"), classIndex);
/*  406: 871 */     this.m_data.setClassIndex(classIndex);
/*  407: 872 */     this.m_NumericClassData = new Instances(this.m_data, 0);
/*  408:     */     
/*  409:     */ 
/*  410: 875 */     this.m_probs = initialProbs(numInstances);
/*  411: 876 */     this.m_logLikelihood = logLikelihood(this.m_trainYs, this.m_probs);
/*  412: 877 */     this.m_NumGenerated = 0;
/*  413: 878 */     if (this.m_Debug) {
/*  414: 879 */       System.err.println("Avg. log-likelihood: " + this.m_logLikelihood);
/*  415:     */     }
/*  416: 881 */     this.m_sumOfWeights = this.m_data.sumOfWeights();
/*  417:     */   }
/*  418:     */   
/*  419:     */   public boolean next()
/*  420:     */     throws Exception
/*  421:     */   {
/*  422: 889 */     if (this.m_NumGenerated >= this.m_NumIterations) {
/*  423: 890 */       return false;
/*  424:     */     }
/*  425: 894 */     if (this.m_ZeroR != null) {
/*  426: 895 */       return false;
/*  427:     */     }
/*  428: 898 */     double previousLoglikelihood = this.m_logLikelihood;
/*  429: 899 */     performIteration(this.m_trainYs, this.m_trainFs, this.m_probs, this.m_data, this.m_sumOfWeights);
/*  430: 900 */     this.m_logLikelihood = logLikelihood(this.m_trainYs, this.m_probs);
/*  431: 901 */     if (this.m_Debug) {
/*  432: 902 */       System.err.println("Avg. log-likelihood: " + this.m_logLikelihood);
/*  433:     */     }
/*  434: 904 */     if (Math.abs(previousLoglikelihood - this.m_logLikelihood) < this.m_Precision) {
/*  435: 905 */       return false;
/*  436:     */     }
/*  437: 907 */     return true;
/*  438:     */   }
/*  439:     */   
/*  440:     */   public void done()
/*  441:     */   {
/*  442: 915 */     this.m_trainYs = (this.m_trainFs = this.m_probs = (double[][])null);
/*  443: 916 */     this.m_data = null;
/*  444:     */   }
/*  445:     */   
/*  446:     */   private double[][] initialProbs(int numInstances)
/*  447:     */   {
/*  448: 927 */     double[][] probs = new double[numInstances][this.m_NumClasses];
/*  449: 928 */     for (int i = 0; i < numInstances; i++) {
/*  450: 929 */       for (int j = 0; j < this.m_NumClasses; j++) {
/*  451: 930 */         probs[i][j] = (1.0D / this.m_NumClasses);
/*  452:     */       }
/*  453:     */     }
/*  454: 933 */     return probs;
/*  455:     */   }
/*  456:     */   
/*  457:     */   private double logLikelihood(double[][] trainYs, double[][] probs)
/*  458:     */   {
/*  459: 945 */     double logLikelihood = 0.0D;
/*  460: 946 */     for (int i = 0; i < trainYs.length; i++) {
/*  461: 947 */       for (int j = 0; j < this.m_NumClasses; j++) {
/*  462: 948 */         if (trainYs[i][j] == 1.0D - this.m_Offset) {
/*  463: 949 */           logLikelihood -= Math.log(probs[i][j]);
/*  464:     */         }
/*  465:     */       }
/*  466:     */     }
/*  467: 953 */     return logLikelihood / trainYs.length;
/*  468:     */   }
/*  469:     */   
/*  470:     */   private void performIteration(double[][] trainYs, double[][] trainFs, double[][] probs, Instances data, double origSumOfWeights)
/*  471:     */     throws Exception
/*  472:     */   {
/*  473: 969 */     if (this.m_Debug) {
/*  474: 970 */       System.err.println("Training classifier " + (this.m_NumGenerated + 1));
/*  475:     */     }
/*  476: 974 */     Classifier[] classifiers = new Classifier[this.m_NumClasses];
/*  477: 977 */     for (int j = 0; j < this.m_NumClasses; j++)
/*  478:     */     {
/*  479: 978 */       if (this.m_Debug) {
/*  480: 979 */         System.err.println("\t...for class " + (j + 1) + " (" + this.m_ClassAttribute.name() + "=" + this.m_ClassAttribute.value(j) + ")");
/*  481:     */       }
/*  482: 984 */       Instances boostData = new Instances(data);
/*  483: 987 */       for (int i = 0; i < probs.length; i++)
/*  484:     */       {
/*  485: 990 */         double p = probs[i][j];
/*  486: 991 */         double actual = trainYs[i][j];
/*  487:     */         double z;
/*  488: 992 */         if (actual == 1.0D - this.m_Offset)
/*  489:     */         {
/*  490: 993 */           double z = 1.0D / p;
/*  491: 994 */           if (z > this.m_zMax) {
/*  492: 995 */             z = this.m_zMax;
/*  493:     */           }
/*  494:     */         }
/*  495:     */         else
/*  496:     */         {
/*  497: 998 */           z = -1.0D / (1.0D - p);
/*  498: 999 */           if (z < -this.m_zMax) {
/*  499:1000 */             z = -this.m_zMax;
/*  500:     */           }
/*  501:     */         }
/*  502:1003 */         double w = (actual - p) / z;
/*  503:     */         
/*  504:     */ 
/*  505:1006 */         Instance current = boostData.instance(i);
/*  506:1007 */         current.setValue(boostData.classIndex(), z);
/*  507:1008 */         current.setWeight(current.weight() * w);
/*  508:     */       }
/*  509:1012 */       double sumOfWeights = boostData.sumOfWeights();
/*  510:1013 */       double scalingFactor = origSumOfWeights / sumOfWeights;
/*  511:1014 */       for (int i = 0; i < probs.length; i++)
/*  512:     */       {
/*  513:1015 */         Instance current = boostData.instance(i);
/*  514:1016 */         current.setWeight(current.weight() * scalingFactor);
/*  515:     */       }
/*  516:1020 */       Instances trainData = boostData;
/*  517:1021 */       if (this.m_WeightThreshold < 100)
/*  518:     */       {
/*  519:1022 */         trainData = selectWeightQuantile(boostData, this.m_WeightThreshold / 100.0D);
/*  520:     */       }
/*  521:1025 */       else if (this.m_UseResampling)
/*  522:     */       {
/*  523:1026 */         double[] weights = new double[boostData.numInstances()];
/*  524:1027 */         for (int kk = 0; kk < weights.length; kk++) {
/*  525:1028 */           weights[kk] = boostData.instance(kk).weight();
/*  526:     */         }
/*  527:1030 */         trainData = boostData.resampleWithWeights(this.m_RandomInstance, weights);
/*  528:     */       }
/*  529:1035 */       classifiers[j] = AbstractClassifier.makeCopy(this.m_Classifier);
/*  530:1036 */       classifiers[j].buildClassifier(trainData);
/*  531:1037 */       if (this.m_NumClasses == 2) {
/*  532:     */         break;
/*  533:     */       }
/*  534:     */     }
/*  535:1042 */     this.m_Classifiers.add(classifiers);
/*  536:1045 */     for (int i = 0; i < trainFs.length; i++)
/*  537:     */     {
/*  538:1046 */       double[] pred = new double[this.m_NumClasses];
/*  539:1047 */       double predSum = 0.0D;
/*  540:1048 */       for (int j = 0; j < this.m_NumClasses; j++)
/*  541:     */       {
/*  542:1049 */         double tempPred = this.m_Shrinkage * classifiers[j].classifyInstance(data.instance(i));
/*  543:1051 */         if (Utils.isMissingValue(tempPred)) {
/*  544:1052 */           throw new UnassignedClassException("LogitBoost: base learner predicted missing value.");
/*  545:     */         }
/*  546:1055 */         pred[j] = tempPred;
/*  547:1056 */         if (this.m_NumClasses == 2)
/*  548:     */         {
/*  549:1057 */           pred[1] = (-tempPred);
/*  550:1058 */           break;
/*  551:     */         }
/*  552:1060 */         predSum += pred[j];
/*  553:     */       }
/*  554:1062 */       predSum /= this.m_NumClasses;
/*  555:1063 */       for (int j = 0; j < this.m_NumClasses; j++) {
/*  556:1064 */         trainFs[i][j] += (pred[j] - predSum) * (this.m_NumClasses - 1) / this.m_NumClasses;
/*  557:     */       }
/*  558:     */     }
/*  559:1068 */     this.m_NumGenerated += 1;
/*  560:1071 */     for (int i = 0; i < trainYs.length; i++) {
/*  561:1072 */       probs[i] = probs(trainFs[i]);
/*  562:     */     }
/*  563:     */   }
/*  564:     */   
/*  565:     */   public Classifier[][] classifiers()
/*  566:     */   {
/*  567:1083 */     return (Classifier[][])this.m_Classifiers.toArray(new Classifier[0][0]);
/*  568:     */   }
/*  569:     */   
/*  570:     */   private double[] probs(double[] Fs)
/*  571:     */   {
/*  572:1094 */     double maxF = -1.797693134862316E+308D;
/*  573:1095 */     for (int i = 0; i < Fs.length; i++) {
/*  574:1096 */       if (Fs[i] > maxF) {
/*  575:1097 */         maxF = Fs[i];
/*  576:     */       }
/*  577:     */     }
/*  578:1100 */     double sum = 0.0D;
/*  579:1101 */     double[] probs = new double[Fs.length];
/*  580:1102 */     for (int i = 0; i < Fs.length; i++)
/*  581:     */     {
/*  582:1103 */       probs[i] = Math.exp(Fs[i] - maxF);
/*  583:1104 */       sum += probs[i];
/*  584:     */     }
/*  585:1106 */     Utils.normalize(probs, sum);
/*  586:1107 */     return probs;
/*  587:     */   }
/*  588:     */   
/*  589:     */   public String batchSizeTipText()
/*  590:     */   {
/*  591:1116 */     return "Batch size option is not used in LogitBoost";
/*  592:     */   }
/*  593:     */   
/*  594:     */   public void setBatchSize(String i) {}
/*  595:     */   
/*  596:     */   public String getBatchSize()
/*  597:     */   {
/*  598:1129 */     return "";
/*  599:     */   }
/*  600:     */   
/*  601:     */   public boolean implementsMoreEfficientBatchPrediction()
/*  602:     */   {
/*  603:1139 */     return true;
/*  604:     */   }
/*  605:     */   
/*  606:     */   public double[] distributionForInstance(Instance inst)
/*  607:     */     throws Exception
/*  608:     */   {
/*  609:1152 */     if (this.m_ZeroR != null) {
/*  610:1153 */       return this.m_ZeroR.distributionForInstance(inst);
/*  611:     */     }
/*  612:1156 */     double[] Fs = new double[this.m_NumClasses];
/*  613:1157 */     double[] pred = new double[this.m_NumClasses];
/*  614:1158 */     Instance instance = (Instance)inst.copy();
/*  615:1159 */     instance.setDataset(this.m_NumericClassData);
/*  616:1160 */     for (int i = 0; i < this.m_NumGenerated; i++)
/*  617:     */     {
/*  618:1161 */       double predSum = 0.0D;
/*  619:1162 */       for (int j = 0; j < this.m_NumClasses; j++)
/*  620:     */       {
/*  621:1163 */         double tempPred = this.m_Shrinkage * ((Classifier[])this.m_Classifiers.get(i))[j].classifyInstance(instance);
/*  622:1165 */         if (Utils.isMissingValue(tempPred)) {
/*  623:1166 */           throw new UnassignedClassException("LogitBoost: base learner predicted missing value.");
/*  624:     */         }
/*  625:1169 */         pred[j] = tempPred;
/*  626:1170 */         if (this.m_NumClasses == 2)
/*  627:     */         {
/*  628:1171 */           pred[1] = (-tempPred);
/*  629:1172 */           break;
/*  630:     */         }
/*  631:1174 */         predSum += pred[j];
/*  632:     */       }
/*  633:1176 */       predSum /= this.m_NumClasses;
/*  634:1177 */       for (int j = 0; j < this.m_NumClasses; j++) {
/*  635:1178 */         Fs[j] += (pred[j] - predSum) * (this.m_NumClasses - 1) / this.m_NumClasses;
/*  636:     */       }
/*  637:     */     }
/*  638:1182 */     return probs(Fs);
/*  639:     */   }
/*  640:     */   
/*  641:     */   public double[][] distributionsForInstances(Instances insts)
/*  642:     */     throws Exception
/*  643:     */   {
/*  644:1196 */     if (this.m_ZeroR != null)
/*  645:     */     {
/*  646:1197 */       double[][] preds = new double[insts.numInstances()][];
/*  647:1198 */       for (int i = 0; i < preds.length; i++) {
/*  648:1199 */         preds[i] = this.m_ZeroR.distributionForInstance(insts.instance(i));
/*  649:     */       }
/*  650:1201 */       return preds;
/*  651:     */     }
/*  652:1204 */     final Instances numericClassInsts = new Instances(this.m_NumericClassData);
/*  653:1205 */     for (int i = 0; i < insts.numInstances(); i++) {
/*  654:1206 */       numericClassInsts.add(insts.instance(i));
/*  655:     */     }
/*  656:1210 */     ExecutorService pool = Executors.newFixedThreadPool(this.m_poolSize);
/*  657:     */     
/*  658:1212 */     double[][] Fs = new double[insts.numInstances()][this.m_NumClasses];
/*  659:     */     
/*  660:     */ 
/*  661:1215 */     int chunksize = this.m_NumGenerated / this.m_numThreads;
/*  662:1216 */     Set<Future<double[][]>> results = new HashSet();
/*  663:1219 */     for (int j = 0; j < this.m_numThreads; j++)
/*  664:     */     {
/*  665:1222 */       final int lo = j * chunksize;
/*  666:1223 */       final int hi = j < this.m_numThreads - 1 ? lo + chunksize : this.m_NumGenerated;
/*  667:     */       
/*  668:     */ 
/*  669:1226 */       Future<double[][]> futureT = pool.submit(new Callable()
/*  670:     */       {
/*  671:     */         public double[][] call()
/*  672:     */           throws Exception
/*  673:     */         {
/*  674:1229 */           double[][] localFs = new double[numericClassInsts.numInstances()][LogitBoost.this.m_NumClasses];
/*  675:1231 */           for (int k = 0; k < numericClassInsts.numInstances(); k++)
/*  676:     */           {
/*  677:1232 */             Instance instance = numericClassInsts.instance(k);
/*  678:1233 */             for (int i = lo; i < hi; i++)
/*  679:     */             {
/*  680:1234 */               double predSum = 0.0D;
/*  681:1235 */               double[] pred = new double[LogitBoost.this.m_NumClasses];
/*  682:1236 */               for (int j = 0; j < LogitBoost.this.m_NumClasses; j++)
/*  683:     */               {
/*  684:1237 */                 double tempPred = LogitBoost.this.m_Shrinkage * ((Classifier[])LogitBoost.this.m_Classifiers.get(i))[j].classifyInstance(instance);
/*  685:1240 */                 if (Utils.isMissingValue(tempPred)) {
/*  686:1241 */                   throw new UnassignedClassException("LogitBoost: base learner predicted missing value.");
/*  687:     */                 }
/*  688:1244 */                 pred[j] = tempPred;
/*  689:1245 */                 if (LogitBoost.this.m_NumClasses == 2)
/*  690:     */                 {
/*  691:1246 */                   pred[1] = (-tempPred);
/*  692:1247 */                   break;
/*  693:     */                 }
/*  694:1249 */                 predSum += pred[j];
/*  695:     */               }
/*  696:1251 */               predSum /= LogitBoost.this.m_NumClasses;
/*  697:1252 */               for (int j = 0; j < LogitBoost.this.m_NumClasses; j++) {
/*  698:1253 */                 localFs[k][j] += (pred[j] - predSum) * (LogitBoost.this.m_NumClasses - 1) / LogitBoost.this.m_NumClasses;
/*  699:     */               }
/*  700:     */             }
/*  701:     */           }
/*  702:1258 */           return localFs;
/*  703:     */         }
/*  704:1260 */       });
/*  705:1261 */       results.add(futureT);
/*  706:     */     }
/*  707:     */     try
/*  708:     */     {
/*  709:1266 */       for (Future<double[][]> futureT : results)
/*  710:     */       {
/*  711:1267 */         double[][] f = (double[][])futureT.get();
/*  712:1268 */         for (int j = 0; j < Fs.length; j++) {
/*  713:1269 */           for (int i = 0; i < Fs[j].length; i++) {
/*  714:1270 */             Fs[j][i] += f[j][i];
/*  715:     */           }
/*  716:     */         }
/*  717:     */       }
/*  718:     */     }
/*  719:     */     catch (Exception e)
/*  720:     */     {
/*  721:1275 */       System.out.println("Predictions could not be generated.");
/*  722:1276 */       e.printStackTrace();
/*  723:     */     }
/*  724:1279 */     pool.shutdown();
/*  725:     */     
/*  726:1281 */     double[][] preds = new double[insts.numInstances()][];
/*  727:1282 */     for (int i = 0; i < preds.length; i++) {
/*  728:1283 */       preds[i] = probs(Fs[i]);
/*  729:     */     }
/*  730:1285 */     return preds;
/*  731:     */   }
/*  732:     */   
/*  733:     */   public String toSource(String className)
/*  734:     */     throws Exception
/*  735:     */   {
/*  736:1297 */     if (this.m_NumGenerated == 0) {
/*  737:1298 */       throw new Exception("No model built yet");
/*  738:     */     }
/*  739:1300 */     if (!(((Classifier[])this.m_Classifiers.get(0))[0] instanceof Sourcable)) {
/*  740:1301 */       throw new Exception("Base learner " + this.m_Classifier.getClass().getName() + " is not Sourcable");
/*  741:     */     }
/*  742:1305 */     StringBuffer text = new StringBuffer("class ");
/*  743:1306 */     text.append(className).append(" {\n\n");
/*  744:1307 */     text.append("  private static double RtoP(double []R, int j) {\n    double Rcenter = 0;\n    for (int i = 0; i < R.length; i++) {\n      Rcenter += R[i];\n    }\n    Rcenter /= R.length;\n    double Rsum = 0;\n    for (int i = 0; i < R.length; i++) {\n      Rsum += Math.exp(R[i] - Rcenter);\n    }\n    return Math.exp(R[j]) / Rsum;\n  }\n\n");
/*  745:     */     
/*  746:     */ 
/*  747:     */ 
/*  748:     */ 
/*  749:     */ 
/*  750:     */ 
/*  751:     */ 
/*  752:1315 */     text.append("  public static double classify(Object[] i) {\n    double [] d = distribution(i);\n    double maxV = d[0];\n    int maxI = 0;\n    for (int j = 1; j < " + this.m_NumClasses + "; j++) {\n" + "      if (d[j] > maxV) { maxV = d[j]; maxI = j; }\n" + "    }\n    return (double) maxI;\n  }\n\n");
/*  753:     */     
/*  754:     */ 
/*  755:     */ 
/*  756:     */ 
/*  757:     */ 
/*  758:1321 */     text.append("  public static double [] distribution(Object [] i) {\n");
/*  759:1322 */     text.append("    double [] Fs = new double [" + this.m_NumClasses + "];\n");
/*  760:1323 */     text.append("    double [] Fi = new double [" + this.m_NumClasses + "];\n");
/*  761:1324 */     text.append("    double Fsum;\n");
/*  762:1325 */     for (int i = 0; i < this.m_NumGenerated; i++)
/*  763:     */     {
/*  764:1326 */       text.append("    Fsum = 0;\n");
/*  765:1327 */       for (int j = 0; j < this.m_NumClasses; j++)
/*  766:     */       {
/*  767:1328 */         text.append("    Fi[" + j + "] = " + className + '_' + j + '_' + i + ".classify(i); Fsum += Fi[" + j + "];\n");
/*  768:1330 */         if (this.m_NumClasses == 2)
/*  769:     */         {
/*  770:1331 */           text.append("    Fi[1] = -Fi[0];\n");
/*  771:1332 */           break;
/*  772:     */         }
/*  773:     */       }
/*  774:1335 */       text.append("    Fsum /= " + this.m_NumClasses + ";\n");
/*  775:1336 */       text.append("    for (int j = 0; j < " + this.m_NumClasses + "; j++) {");
/*  776:1337 */       text.append(" Fs[j] += (Fi[j] - Fsum) * " + (this.m_NumClasses - 1) + " / " + this.m_NumClasses + "; }\n");
/*  777:     */     }
/*  778:1341 */     text.append("    double [] dist = new double [" + this.m_NumClasses + "];\n" + "    for (int j = 0; j < " + this.m_NumClasses + "; j++) {\n" + "      dist[j] = RtoP(Fs, j);\n" + "    }\n    return dist;\n");
/*  779:     */     
/*  780:     */ 
/*  781:1344 */     text.append("  }\n}\n");
/*  782:1346 */     for (int i = 0; i < ((Classifier[])this.m_Classifiers.get(0)).length; i++)
/*  783:     */     {
/*  784:1347 */       for (int j = 0; j < this.m_Classifiers.size(); j++) {
/*  785:1348 */         text.append(((Sourcable)((Classifier[])this.m_Classifiers.get(j))[i]).toSource(className + '_' + i + '_' + j));
/*  786:     */       }
/*  787:1351 */       if (this.m_NumClasses == 2) {
/*  788:     */         break;
/*  789:     */       }
/*  790:     */     }
/*  791:1355 */     return text.toString();
/*  792:     */   }
/*  793:     */   
/*  794:     */   public String toString()
/*  795:     */   {
/*  796:1366 */     if (this.m_ZeroR != null)
/*  797:     */     {
/*  798:1367 */       StringBuffer buf = new StringBuffer();
/*  799:1368 */       buf.append(getClass().getName().replaceAll(".*\\.", "") + "\n");
/*  800:1369 */       buf.append(getClass().getName().replaceAll(".*\\.", "").replaceAll(".", "=") + "\n\n");
/*  801:     */       
/*  802:     */ 
/*  803:1372 */       buf.append("Warning: No model could be built, hence ZeroR model is used:\n\n");
/*  804:     */       
/*  805:     */ 
/*  806:1375 */       buf.append(this.m_ZeroR.toString());
/*  807:1376 */       return buf.toString();
/*  808:     */     }
/*  809:1379 */     StringBuffer text = new StringBuffer();
/*  810:1381 */     if (this.m_NumGenerated == 0)
/*  811:     */     {
/*  812:1382 */       text.append("LogitBoost: No model built yet.");
/*  813:     */     }
/*  814:     */     else
/*  815:     */     {
/*  816:1385 */       text.append("LogitBoost: Base classifiers and their weights: \n");
/*  817:1386 */       for (int i = 0; i < this.m_NumGenerated; i++)
/*  818:     */       {
/*  819:1387 */         text.append("\nIteration " + (i + 1));
/*  820:1388 */         for (int j = 0; j < this.m_NumClasses; j++)
/*  821:     */         {
/*  822:1389 */           text.append("\n\tClass " + (j + 1) + " (" + this.m_ClassAttribute.name() + "=" + this.m_ClassAttribute.value(j) + ")\n\n" + ((Classifier[])this.m_Classifiers.get(i))[j].toString() + "\n");
/*  823:1392 */           if (this.m_NumClasses == 2)
/*  824:     */           {
/*  825:1393 */             text.append("Two-class case: second classifier predicts additive inverse of first classifier and is not explicitly computed.\n\n");
/*  826:     */             
/*  827:     */ 
/*  828:1396 */             break;
/*  829:     */           }
/*  830:     */         }
/*  831:     */       }
/*  832:1400 */       text.append("Number of performed iterations: " + this.m_NumGenerated + "\n");
/*  833:     */     }
/*  834:1403 */     return text.toString();
/*  835:     */   }
/*  836:     */   
/*  837:     */   public String getRevision()
/*  838:     */   {
/*  839:1412 */     return RevisionUtils.extract("$Revision: 11958 $");
/*  840:     */   }
/*  841:     */   
/*  842:     */   public static void main(String[] argv)
/*  843:     */   {
/*  844:1421 */     runClassifier(new LogitBoost(), argv);
/*  845:     */   }
/*  846:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.LogitBoost
 * JD-Core Version:    0.7.0.1
 */