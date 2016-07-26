/*    1:     */ package weka.clusterers;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.util.ArrayList;
/*    5:     */ import java.util.Collections;
/*    6:     */ import java.util.Enumeration;
/*    7:     */ import java.util.List;
/*    8:     */ import java.util.Random;
/*    9:     */ import java.util.Vector;
/*   10:     */ import java.util.concurrent.Callable;
/*   11:     */ import java.util.concurrent.ExecutorService;
/*   12:     */ import java.util.concurrent.Executors;
/*   13:     */ import java.util.concurrent.Future;
/*   14:     */ import weka.core.Attribute;
/*   15:     */ import weka.core.Capabilities;
/*   16:     */ import weka.core.Instance;
/*   17:     */ import weka.core.Instances;
/*   18:     */ import weka.core.Option;
/*   19:     */ import weka.core.RevisionUtils;
/*   20:     */ import weka.core.Utils;
/*   21:     */ import weka.core.WeightedInstancesHandler;
/*   22:     */ import weka.estimators.DiscreteEstimator;
/*   23:     */ import weka.estimators.Estimator;
/*   24:     */ import weka.filters.Filter;
/*   25:     */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*   26:     */ 
/*   27:     */ public class EM
/*   28:     */   extends RandomizableDensityBasedClusterer
/*   29:     */   implements NumberOfClustersRequestable, WeightedInstancesHandler
/*   30:     */ {
/*   31:     */   static final long serialVersionUID = 8348181483812829475L;
/*   32:     */   private Estimator[][] m_modelPrev;
/*   33:     */   private double[][][] m_modelNormalPrev;
/*   34:     */   private double[] m_priorsPrev;
/*   35:     */   private Estimator[][] m_model;
/*   36:     */   private double[][][] m_modelNormal;
/*   37: 149 */   private double m_minStdDev = 1.0E-006D;
/*   38:     */   private double[] m_minStdDevPerAtt;
/*   39:     */   private double[][] m_weights;
/*   40:     */   private double[] m_priors;
/*   41: 160 */   private Instances m_theInstances = null;
/*   42:     */   private int m_num_clusters;
/*   43:     */   private int m_initialNumClusters;
/*   44: 172 */   private int m_upperBoundNumClustersCV = -1;
/*   45:     */   private int m_num_attribs;
/*   46:     */   private int m_num_instances;
/*   47:     */   private int m_max_iterations;
/*   48:     */   private double[] m_minValues;
/*   49:     */   private double[] m_maxValues;
/*   50:     */   private Random m_rr;
/*   51:     */   private boolean m_verbose;
/*   52:     */   private ReplaceMissingValues m_replaceMissing;
/*   53:     */   private boolean m_displayModelInOldFormat;
/*   54: 202 */   protected int m_executionSlots = 1;
/*   55:     */   protected transient ExecutorService m_executorPool;
/*   56:     */   protected boolean m_training;
/*   57:     */   protected int m_iterationsPerformed;
/*   58: 214 */   protected double m_minLogLikelihoodImprovementIterating = 1.0E-006D;
/*   59: 217 */   protected double m_minLogLikelihoodImprovementCV = 1.0E-006D;
/*   60: 220 */   protected int m_cvFolds = 10;
/*   61: 223 */   protected int m_NumKMeansRuns = 10;
/*   62:     */   
/*   63:     */   public String globalInfo()
/*   64:     */   {
/*   65: 232 */     return "Simple EM (expectation maximisation) class.\n\nEM assigns a probability distribution to each instance which indicates the probability of it belonging to each of the clusters. EM can decide how many clusters to create by cross validation, or you may specify apriori how many clusters to generate.\n\nThe cross validation performed to determine the number of clusters is done in the following steps:\n1. the number of clusters is set to 1\n2. the training set is split randomly into 10 folds.\n3. EM is performed 10 times using the 10 folds the usual CV way.\n4. the loglikelihood is averaged over all 10 results.\n5. if loglikelihood has increased the number of clusters is increased by 1 and the program continues at step 2. \n\nThe number of folds is fixed to 10, as long as the number of instances in the training set is not smaller 10. If this is the case the number of folds is set equal to the number of instances.\n\nMissing values are globally replaced with ReplaceMissingValues.";
/*   66:     */   }
/*   67:     */   
/*   68:     */   public Enumeration<Option> listOptions()
/*   69:     */   {
/*   70: 258 */     Vector<Option> result = new Vector();
/*   71:     */     
/*   72: 260 */     result.addElement(new Option("\tnumber of clusters. If omitted or -1 specified, then \n\tcross validation is used to select the number of clusters.", "N", 1, "-N <num>"));
/*   73:     */     
/*   74:     */ 
/*   75:     */ 
/*   76:     */ 
/*   77: 265 */     result.addElement(new Option("\tNumber of folds to use when cross-validating to find the best number of clusters.", "X", 1, "-X <num>"));
/*   78:     */     
/*   79:     */ 
/*   80:     */ 
/*   81:     */ 
/*   82: 270 */     result.addElement(new Option("\tNumber of runs of k-means to perform.\n\t(default 10)", "K", 1, "-K <num>"));
/*   83:     */     
/*   84:     */ 
/*   85: 273 */     result.addElement(new Option("\tMaximum number of clusters to consider during cross-validation. If omitted or -1 specified, then \n\tthere is no upper limit on the number of clusters.", "max", 1, "-max <num>"));
/*   86:     */     
/*   87:     */ 
/*   88:     */ 
/*   89:     */ 
/*   90:     */ 
/*   91: 279 */     result.addElement(new Option("\tMinimum improvement in cross-validated log likelihood required\n\tto consider increasing the number of clusters.\n\t(default 1e-6)", "ll-cv", 1, "-ll-cv <num>"));
/*   92:     */     
/*   93:     */ 
/*   94:     */ 
/*   95:     */ 
/*   96: 284 */     result.addElement(new Option("\tmax iterations.\n\t(default 100)", "I", 1, "-I <num>"));
/*   97:     */     
/*   98:     */ 
/*   99: 287 */     result.addElement(new Option("\tMinimum improvement in log likelihood required\n\tto perform another iteration of the E and M steps.\n\t(default 1e-6)", "ll-iter", 1, "-ll-iter <num>"));
/*  100:     */     
/*  101:     */ 
/*  102:     */ 
/*  103:     */ 
/*  104: 292 */     result.addElement(new Option("\tverbose.", "V", 0, "-V"));
/*  105:     */     
/*  106: 294 */     result.addElement(new Option("\tminimum allowable standard deviation for normal density\n\tcomputation\n\t(default 1e-6)", "M", 1, "-M <num>"));
/*  107:     */     
/*  108:     */ 
/*  109:     */ 
/*  110: 298 */     result.addElement(new Option("\tDisplay model in old format (good when there are many clusters)\n", "O", 0, "-O"));
/*  111:     */     
/*  112:     */ 
/*  113:     */ 
/*  114: 302 */     result.addElement(new Option("\tNumber of execution slots.\n\t(default 1 - i.e. no parallelism)", "num-slots", 1, "-num-slots <num>"));
/*  115:     */     
/*  116:     */ 
/*  117:     */ 
/*  118: 306 */     result.addAll(Collections.list(super.listOptions()));
/*  119:     */     
/*  120: 308 */     return result.elements();
/*  121:     */   }
/*  122:     */   
/*  123:     */   public void setOptions(String[] options)
/*  124:     */     throws Exception
/*  125:     */   {
/*  126: 382 */     resetOptions();
/*  127: 383 */     setDebug(Utils.getFlag('V', options));
/*  128: 384 */     String optionString = Utils.getOption('I', options);
/*  129: 386 */     if (optionString.length() != 0) {
/*  130: 387 */       setMaxIterations(Integer.parseInt(optionString));
/*  131:     */     }
/*  132: 390 */     optionString = Utils.getOption('X', options);
/*  133: 391 */     if (optionString.length() > 0) {
/*  134: 392 */       setNumFolds(Integer.parseInt(optionString));
/*  135:     */     }
/*  136: 395 */     optionString = Utils.getOption("ll-iter", options);
/*  137: 396 */     if (optionString.length() > 0) {
/*  138: 397 */       setMinLogLikelihoodImprovementIterating(Double.parseDouble(optionString));
/*  139:     */     }
/*  140: 400 */     optionString = Utils.getOption("ll-cv", options);
/*  141: 401 */     if (optionString.length() > 0) {
/*  142: 402 */       setMinLogLikelihoodImprovementCV(Double.parseDouble(optionString));
/*  143:     */     }
/*  144: 405 */     optionString = Utils.getOption('N', options);
/*  145: 406 */     if (optionString.length() != 0) {
/*  146: 407 */       setNumClusters(Integer.parseInt(optionString));
/*  147:     */     }
/*  148: 410 */     optionString = Utils.getOption("max", options);
/*  149: 411 */     if (optionString.length() > 0) {
/*  150: 412 */       setMaximumNumberOfClusters(Integer.parseInt(optionString));
/*  151:     */     }
/*  152: 415 */     optionString = Utils.getOption('M', options);
/*  153: 416 */     if (optionString.length() != 0) {
/*  154: 417 */       setMinStdDev(new Double(optionString).doubleValue());
/*  155:     */     }
/*  156: 420 */     optionString = Utils.getOption('K', options);
/*  157: 421 */     if (optionString.length() != 0) {
/*  158: 422 */       setNumKMeansRuns(new Integer(optionString).intValue());
/*  159:     */     }
/*  160: 425 */     setDisplayModelInOldFormat(Utils.getFlag('O', options));
/*  161:     */     
/*  162: 427 */     String slotsS = Utils.getOption("num-slots", options);
/*  163: 428 */     if (slotsS.length() > 0) {
/*  164: 429 */       setNumExecutionSlots(Integer.parseInt(slotsS));
/*  165:     */     }
/*  166: 432 */     super.setOptions(options);
/*  167:     */     
/*  168: 434 */     Utils.checkForRemainingOptions(options);
/*  169:     */   }
/*  170:     */   
/*  171:     */   public String numKMeansRunsTipText()
/*  172:     */   {
/*  173: 445 */     return "The number of runs of k-means to perform.";
/*  174:     */   }
/*  175:     */   
/*  176:     */   public int getNumKMeansRuns()
/*  177:     */   {
/*  178: 454 */     return this.m_NumKMeansRuns;
/*  179:     */   }
/*  180:     */   
/*  181:     */   public void setNumKMeansRuns(int intValue)
/*  182:     */   {
/*  183: 464 */     this.m_NumKMeansRuns = intValue;
/*  184:     */   }
/*  185:     */   
/*  186:     */   public String numFoldsTipText()
/*  187:     */   {
/*  188: 474 */     return "The number of folds to use when cross-validating to find the best number of clusters (default = 10)";
/*  189:     */   }
/*  190:     */   
/*  191:     */   public void setNumFolds(int folds)
/*  192:     */   {
/*  193: 485 */     this.m_cvFolds = folds;
/*  194:     */   }
/*  195:     */   
/*  196:     */   public int getNumFolds()
/*  197:     */   {
/*  198: 495 */     return this.m_cvFolds;
/*  199:     */   }
/*  200:     */   
/*  201:     */   public String minLogLikelihoodImprovementCVTipText()
/*  202:     */   {
/*  203: 505 */     return "The minimum improvement in cross-validated log likelihood required in order to consider increasing the number of clusters when cross-validiting to find the best number of clusters";
/*  204:     */   }
/*  205:     */   
/*  206:     */   public void setMinLogLikelihoodImprovementCV(double min)
/*  207:     */   {
/*  208: 518 */     this.m_minLogLikelihoodImprovementCV = min;
/*  209:     */   }
/*  210:     */   
/*  211:     */   public double getMinLogLikelihoodImprovementCV()
/*  212:     */   {
/*  213: 529 */     return this.m_minLogLikelihoodImprovementCV;
/*  214:     */   }
/*  215:     */   
/*  216:     */   public String minLogLikelihoodImprovementIteratingTipText()
/*  217:     */   {
/*  218: 539 */     return "The minimum improvement in log likelihood required to perform another iteration of the E and M steps";
/*  219:     */   }
/*  220:     */   
/*  221:     */   public void setMinLogLikelihoodImprovementIterating(double min)
/*  222:     */   {
/*  223: 550 */     this.m_minLogLikelihoodImprovementIterating = min;
/*  224:     */   }
/*  225:     */   
/*  226:     */   public double getMinLogLikelihoodImprovementIterating()
/*  227:     */   {
/*  228: 560 */     return this.m_minLogLikelihoodImprovementIterating;
/*  229:     */   }
/*  230:     */   
/*  231:     */   public String numExecutionSlotsTipText()
/*  232:     */   {
/*  233: 570 */     return "The number of execution slots (threads) to use. Set equal to the number of available cpu/cores";
/*  234:     */   }
/*  235:     */   
/*  236:     */   public void setNumExecutionSlots(int slots)
/*  237:     */   {
/*  238: 582 */     this.m_executionSlots = slots;
/*  239:     */   }
/*  240:     */   
/*  241:     */   public int getNumExecutionSlots()
/*  242:     */   {
/*  243: 593 */     return this.m_executionSlots;
/*  244:     */   }
/*  245:     */   
/*  246:     */   public String displayModelInOldFormatTipText()
/*  247:     */   {
/*  248: 603 */     return "Use old format for model output. The old format is better when there are many clusters. The new format is better when there are fewer clusters and many attributes.";
/*  249:     */   }
/*  250:     */   
/*  251:     */   public void setDisplayModelInOldFormat(boolean d)
/*  252:     */   {
/*  253: 614 */     this.m_displayModelInOldFormat = d;
/*  254:     */   }
/*  255:     */   
/*  256:     */   public boolean getDisplayModelInOldFormat()
/*  257:     */   {
/*  258: 623 */     return this.m_displayModelInOldFormat;
/*  259:     */   }
/*  260:     */   
/*  261:     */   public String minStdDevTipText()
/*  262:     */   {
/*  263: 633 */     return "set minimum allowable standard deviation";
/*  264:     */   }
/*  265:     */   
/*  266:     */   public void setMinStdDev(double m)
/*  267:     */   {
/*  268: 645 */     this.m_minStdDev = m;
/*  269:     */   }
/*  270:     */   
/*  271:     */   public void setMinStdDevPerAtt(double[] m)
/*  272:     */   {
/*  273: 649 */     this.m_minStdDevPerAtt = m;
/*  274:     */   }
/*  275:     */   
/*  276:     */   public double getMinStdDev()
/*  277:     */   {
/*  278: 658 */     return this.m_minStdDev;
/*  279:     */   }
/*  280:     */   
/*  281:     */   public String numClustersTipText()
/*  282:     */   {
/*  283: 668 */     return "set number of clusters. -1 to select number of clusters automatically by cross validation.";
/*  284:     */   }
/*  285:     */   
/*  286:     */   public void setNumClusters(int n)
/*  287:     */     throws Exception
/*  288:     */   {
/*  289: 681 */     if (n == 0) {
/*  290: 682 */       throw new Exception("Number of clusters must be > 0. (or -1 to select by cross validation).");
/*  291:     */     }
/*  292: 686 */     if (n < 0)
/*  293:     */     {
/*  294: 687 */       this.m_num_clusters = -1;
/*  295: 688 */       this.m_initialNumClusters = -1;
/*  296:     */     }
/*  297:     */     else
/*  298:     */     {
/*  299: 690 */       this.m_num_clusters = n;
/*  300: 691 */       this.m_initialNumClusters = n;
/*  301:     */     }
/*  302:     */   }
/*  303:     */   
/*  304:     */   public int getNumClusters()
/*  305:     */   {
/*  306: 701 */     return this.m_initialNumClusters;
/*  307:     */   }
/*  308:     */   
/*  309:     */   public void setMaximumNumberOfClusters(int n)
/*  310:     */   {
/*  311: 710 */     this.m_upperBoundNumClustersCV = n;
/*  312:     */   }
/*  313:     */   
/*  314:     */   public int getMaximumNumberOfClusters()
/*  315:     */   {
/*  316: 719 */     return this.m_upperBoundNumClustersCV;
/*  317:     */   }
/*  318:     */   
/*  319:     */   public String maximumNumberOfClustersTipText()
/*  320:     */   {
/*  321: 729 */     return "The maximum number of clusters to consider during cross-validation to select the best number of clusters";
/*  322:     */   }
/*  323:     */   
/*  324:     */   public String maxIterationsTipText()
/*  325:     */   {
/*  326: 740 */     return "maximum number of iterations";
/*  327:     */   }
/*  328:     */   
/*  329:     */   public void setMaxIterations(int i)
/*  330:     */     throws Exception
/*  331:     */   {
/*  332: 750 */     if (i < 1) {
/*  333: 751 */       throw new Exception("Maximum number of iterations must be > 0!");
/*  334:     */     }
/*  335: 754 */     this.m_max_iterations = i;
/*  336:     */   }
/*  337:     */   
/*  338:     */   public int getMaxIterations()
/*  339:     */   {
/*  340: 763 */     return this.m_max_iterations;
/*  341:     */   }
/*  342:     */   
/*  343:     */   public String debugTipText()
/*  344:     */   {
/*  345: 774 */     return "If set to true, clusterer may output additional info to the console.";
/*  346:     */   }
/*  347:     */   
/*  348:     */   public void setDebug(boolean v)
/*  349:     */   {
/*  350: 785 */     this.m_verbose = v;
/*  351:     */   }
/*  352:     */   
/*  353:     */   public boolean getDebug()
/*  354:     */   {
/*  355: 795 */     return this.m_verbose;
/*  356:     */   }
/*  357:     */   
/*  358:     */   public String[] getOptions()
/*  359:     */   {
/*  360: 806 */     Vector<String> result = new Vector();
/*  361:     */     
/*  362: 808 */     result.add("-I");
/*  363: 809 */     result.add("" + this.m_max_iterations);
/*  364: 810 */     result.add("-N");
/*  365: 811 */     result.add("" + getNumClusters());
/*  366: 812 */     result.add("-X");
/*  367: 813 */     result.add("" + getNumFolds());
/*  368: 814 */     result.add("-max");
/*  369: 815 */     result.add("" + getMaximumNumberOfClusters());
/*  370: 816 */     result.add("-ll-cv");
/*  371: 817 */     result.add("" + getMinLogLikelihoodImprovementCV());
/*  372: 818 */     result.add("-ll-iter");
/*  373: 819 */     result.add("" + getMinLogLikelihoodImprovementIterating());
/*  374: 820 */     result.add("-M");
/*  375: 821 */     result.add("" + getMinStdDev());
/*  376: 822 */     result.add("-K");
/*  377: 823 */     result.add("" + getNumKMeansRuns());
/*  378: 824 */     if (this.m_displayModelInOldFormat) {
/*  379: 825 */       result.add("-O");
/*  380:     */     }
/*  381: 828 */     result.add("-num-slots");
/*  382: 829 */     result.add("" + getNumExecutionSlots());
/*  383:     */     
/*  384: 831 */     Collections.addAll(result, super.getOptions());
/*  385:     */     
/*  386: 833 */     return (String[])result.toArray(new String[result.size()]);
/*  387:     */   }
/*  388:     */   
/*  389:     */   private void EM_Init(Instances inst)
/*  390:     */     throws Exception
/*  391:     */   {
/*  392: 846 */     SimpleKMeans bestK = null;
/*  393: 847 */     double bestSqE = 1.7976931348623157E+308D;
/*  394: 848 */     for (int i = 0; i < this.m_NumKMeansRuns; i++)
/*  395:     */     {
/*  396: 849 */       SimpleKMeans sk = new SimpleKMeans();
/*  397: 850 */       sk.setSeed(this.m_rr.nextInt());
/*  398: 851 */       sk.setNumClusters(this.m_num_clusters);
/*  399: 852 */       sk.setNumExecutionSlots(this.m_executionSlots);
/*  400: 853 */       sk.setDisplayStdDevs(true);
/*  401: 854 */       sk.setDoNotCheckCapabilities(true);
/*  402: 855 */       sk.setDontReplaceMissingValues(true);
/*  403: 856 */       sk.buildClusterer(inst);
/*  404: 857 */       if (sk.getSquaredError() < bestSqE)
/*  405:     */       {
/*  406: 858 */         bestSqE = sk.getSquaredError();
/*  407: 859 */         bestK = sk;
/*  408:     */       }
/*  409:     */     }
/*  410: 864 */     this.m_num_clusters = bestK.numberOfClusters();
/*  411: 865 */     this.m_weights = new double[inst.numInstances()][this.m_num_clusters];
/*  412: 866 */     this.m_model = new DiscreteEstimator[this.m_num_clusters][this.m_num_attribs];
/*  413: 867 */     this.m_modelNormal = new double[this.m_num_clusters][this.m_num_attribs][3];
/*  414: 868 */     this.m_priors = new double[this.m_num_clusters];
/*  415:     */     
/*  416: 870 */     this.m_modelPrev = new DiscreteEstimator[this.m_num_clusters][this.m_num_attribs];
/*  417: 871 */     this.m_modelNormalPrev = new double[this.m_num_clusters][this.m_num_attribs][3];
/*  418: 872 */     this.m_priorsPrev = new double[this.m_num_clusters];
/*  419:     */     
/*  420: 874 */     Instances centers = bestK.getClusterCentroids();
/*  421: 875 */     Instances stdD = bestK.getClusterStandardDevs();
/*  422: 876 */     double[][][] nominalCounts = bestK.getClusterNominalCounts();
/*  423: 877 */     double[] clusterSizes = bestK.getClusterSizes();
/*  424: 879 */     for (i = 0; i < this.m_num_clusters; i++)
/*  425:     */     {
/*  426: 880 */       Instance center = centers.instance(i);
/*  427: 881 */       for (int j = 0; j < this.m_num_attribs; j++)
/*  428:     */       {
/*  429: 882 */         if (inst.attribute(j).isNominal())
/*  430:     */         {
/*  431: 883 */           this.m_model[i][j] = new DiscreteEstimator(this.m_theInstances.attribute(j).numValues(), true);
/*  432: 885 */           for (int k = 0; k < inst.attribute(j).numValues(); k++) {
/*  433: 886 */             this.m_model[i][j].addValue(k, nominalCounts[i][j][k]);
/*  434:     */           }
/*  435:     */         }
/*  436: 889 */         double minStdD = this.m_minStdDevPerAtt != null ? this.m_minStdDevPerAtt[j] : this.m_minStdDev;
/*  437:     */         
/*  438: 891 */         this.m_modelNormal[i][j][0] = center.value(j);
/*  439: 892 */         double stdv = stdD.instance(i).value(j);
/*  440: 893 */         if (stdv < minStdD)
/*  441:     */         {
/*  442: 894 */           stdv = Math.sqrt(inst.variance(j));
/*  443: 895 */           if (Double.isInfinite(stdv)) {
/*  444: 896 */             stdv = minStdD;
/*  445:     */           }
/*  446: 898 */           if (stdv < minStdD) {
/*  447: 899 */             stdv = minStdD;
/*  448:     */           }
/*  449:     */         }
/*  450: 902 */         if ((stdv <= 0.0D) || (Double.isNaN(stdv))) {
/*  451: 903 */           stdv = this.m_minStdDev;
/*  452:     */         }
/*  453: 906 */         this.m_modelNormal[i][j][1] = stdv;
/*  454: 907 */         this.m_modelNormal[i][j][2] = 1.0D;
/*  455:     */       }
/*  456:     */     }
/*  457: 912 */     for (int j = 0; j < this.m_num_clusters; j++) {
/*  458: 914 */       this.m_priors[j] = clusterSizes[j];
/*  459:     */     }
/*  460: 916 */     Utils.normalize(this.m_priors);
/*  461:     */   }
/*  462:     */   
/*  463:     */   private void estimate_priors(Instances inst)
/*  464:     */     throws Exception
/*  465:     */   {
/*  466: 927 */     for (int i = 0; i < this.m_num_clusters; i++)
/*  467:     */     {
/*  468: 928 */       this.m_priorsPrev[i] = this.m_priors[i];
/*  469: 929 */       this.m_priors[i] = 0.0D;
/*  470:     */     }
/*  471: 932 */     for (int i = 0; i < inst.numInstances(); i++) {
/*  472: 933 */       for (int j = 0; j < this.m_num_clusters; j++) {
/*  473: 934 */         this.m_priors[j] += inst.instance(i).weight() * this.m_weights[i][j];
/*  474:     */       }
/*  475:     */     }
/*  476: 938 */     Utils.normalize(this.m_priors);
/*  477:     */   }
/*  478:     */   
/*  479: 942 */   private static double m_normConst = Math.log(Math.sqrt(6.283185307179586D));
/*  480:     */   
/*  481:     */   private double logNormalDens(double x, double mean, double stdDev)
/*  482:     */   {
/*  483: 954 */     double diff = x - mean;
/*  484:     */     
/*  485:     */ 
/*  486:     */ 
/*  487:     */ 
/*  488: 959 */     return -(diff * diff / (2.0D * stdDev * stdDev)) - m_normConst - Math.log(stdDev);
/*  489:     */   }
/*  490:     */   
/*  491:     */   private void new_estimators()
/*  492:     */   {
/*  493: 967 */     for (int i = 0; i < this.m_num_clusters; i++) {
/*  494: 968 */       for (int j = 0; j < this.m_num_attribs; j++) {
/*  495: 969 */         if (this.m_theInstances.attribute(j).isNominal())
/*  496:     */         {
/*  497: 970 */           this.m_modelPrev[i][j] = this.m_model[i][j];
/*  498: 971 */           this.m_model[i][j] = new DiscreteEstimator(this.m_theInstances.attribute(j).numValues(), true);
/*  499:     */         }
/*  500:     */         else
/*  501:     */         {
/*  502: 974 */           this.m_modelNormalPrev[i][j][0] = this.m_modelNormal[i][j][0];
/*  503: 975 */           this.m_modelNormalPrev[i][j][1] = this.m_modelNormal[i][j][1];
/*  504: 976 */           this.m_modelNormalPrev[i][j][2] = this.m_modelNormal[i][j][2]; double 
/*  505: 977 */             tmp170_169 = (this.m_modelNormal[i][j][2] = 0.0D);this.m_modelNormal[i][j][1] = tmp170_169;this.m_modelNormal[i][j][0] = tmp170_169;
/*  506:     */         }
/*  507:     */       }
/*  508:     */     }
/*  509:     */   }
/*  510:     */   
/*  511:     */   protected void startExecutorPool()
/*  512:     */   {
/*  513: 987 */     if (this.m_executorPool != null) {
/*  514: 988 */       this.m_executorPool.shutdownNow();
/*  515:     */     }
/*  516: 991 */     this.m_executorPool = Executors.newFixedThreadPool(this.m_executionSlots);
/*  517:     */   }
/*  518:     */   
/*  519:     */   private class ETask
/*  520:     */     implements Callable<double[]>
/*  521:     */   {
/*  522:     */     protected int m_lowNum;
/*  523:     */     protected int m_highNum;
/*  524:     */     protected boolean m_changeWeights;
/*  525:     */     protected Instances m_eData;
/*  526:     */     
/*  527:     */     public ETask(Instances data, int lowInstNum, int highInstNum, boolean changeWeights)
/*  528:     */     {
/*  529:1003 */       this.m_eData = data;
/*  530:1004 */       this.m_lowNum = lowInstNum;
/*  531:1005 */       this.m_highNum = highInstNum;
/*  532:1006 */       this.m_changeWeights = changeWeights;
/*  533:     */     }
/*  534:     */     
/*  535:     */     public double[] call()
/*  536:     */     {
/*  537:1011 */       double[] llk = new double[2];
/*  538:1012 */       double loglk = 0.0D;double sOW = 0.0D;
/*  539:     */       try
/*  540:     */       {
/*  541:1015 */         for (int i = this.m_lowNum; i < this.m_highNum; i++)
/*  542:     */         {
/*  543:1016 */           Instance in = this.m_eData.instance(i);
/*  544:     */           
/*  545:1018 */           loglk += in.weight() * EM.this.logDensityForInstance(in);
/*  546:1019 */           sOW += in.weight();
/*  547:1021 */           if (this.m_changeWeights) {
/*  548:1022 */             EM.this.m_weights[i] = EM.this.distributionForInstance(in);
/*  549:     */           }
/*  550:     */         }
/*  551:     */       }
/*  552:     */       catch (Exception ex) {}
/*  553:1030 */       llk[0] = loglk;
/*  554:1031 */       llk[1] = sOW;
/*  555:     */       
/*  556:1033 */       return llk;
/*  557:     */     }
/*  558:     */   }
/*  559:     */   
/*  560:     */   private class MTask
/*  561:     */     implements Callable<MTask>
/*  562:     */   {
/*  563:     */     protected int m_start;
/*  564:     */     protected int m_end;
/*  565:     */     protected Instances m_inst;
/*  566:     */     protected DiscreteEstimator[][] m_taskModel;
/*  567:     */     double[][][] m_taskModelNormal;
/*  568:     */     
/*  569:     */     public MTask(Instances inst, int start, int end, DiscreteEstimator[][] discEst, double[][][] numericEst)
/*  570:     */     {
/*  571:1050 */       this.m_start = start;
/*  572:1051 */       this.m_end = end;
/*  573:1052 */       this.m_inst = inst;
/*  574:1053 */       this.m_taskModel = discEst;
/*  575:1054 */       this.m_taskModelNormal = numericEst;
/*  576:     */     }
/*  577:     */     
/*  578:     */     public MTask call()
/*  579:     */     {
/*  580:1060 */       for (int l = this.m_start; l < this.m_end; l++)
/*  581:     */       {
/*  582:1061 */         Instance in = this.m_inst.instance(l);
/*  583:1062 */         for (int i = 0; i < EM.this.m_num_clusters; i++) {
/*  584:1063 */           for (int j = 0; j < EM.this.m_num_attribs; j++) {
/*  585:1064 */             if (this.m_inst.attribute(j).isNominal())
/*  586:     */             {
/*  587:1065 */               this.m_taskModel[i][j].addValue(in.value(j), in.weight() * EM.this.m_weights[l][i]);
/*  588:     */             }
/*  589:     */             else
/*  590:     */             {
/*  591:1068 */               this.m_taskModelNormal[i][j][0] += in.value(j) * in.weight() * EM.this.m_weights[l][i];
/*  592:1069 */               this.m_taskModelNormal[i][j][2] += in.weight() * EM.this.m_weights[l][i];
/*  593:1070 */               this.m_taskModelNormal[i][j][1] += in.value(j) * in.value(j) * in.weight() * EM.this.m_weights[l][i];
/*  594:     */             }
/*  595:     */           }
/*  596:     */         }
/*  597:     */       }
/*  598:1078 */       return this;
/*  599:     */     }
/*  600:     */   }
/*  601:     */   
/*  602:     */   private void M_reEstimate(Instances inst)
/*  603:     */   {
/*  604:1084 */     for (int i = 0; i < this.m_num_clusters; i++) {
/*  605:1085 */       for (int j = 0; j < this.m_num_attribs; j++) {
/*  606:1086 */         if (!inst.attribute(j).isNominal()) {
/*  607:1087 */           if (this.m_modelNormal[i][j][2] <= 0.0D)
/*  608:     */           {
/*  609:1088 */             this.m_modelNormal[i][j][1] = 1.7976931348623157E+308D;
/*  610:     */             
/*  611:1090 */             this.m_modelNormal[i][j][0] = this.m_minStdDev;
/*  612:     */           }
/*  613:     */           else
/*  614:     */           {
/*  615:1094 */             this.m_modelNormal[i][j][1] = ((this.m_modelNormal[i][j][1] - this.m_modelNormal[i][j][0] * this.m_modelNormal[i][j][0] / this.m_modelNormal[i][j][2]) / this.m_modelNormal[i][j][2]);
/*  616:1098 */             if (this.m_modelNormal[i][j][1] < 0.0D) {
/*  617:1099 */               this.m_modelNormal[i][j][1] = 0.0D;
/*  618:     */             }
/*  619:1103 */             double minStdD = this.m_minStdDevPerAtt != null ? this.m_minStdDevPerAtt[j] : this.m_minStdDev;
/*  620:     */             
/*  621:     */ 
/*  622:1106 */             this.m_modelNormal[i][j][1] = Math.sqrt(this.m_modelNormal[i][j][1]);
/*  623:1108 */             if (this.m_modelNormal[i][j][1] <= minStdD)
/*  624:     */             {
/*  625:1109 */               this.m_modelNormal[i][j][1] = Math.sqrt(inst.variance(j));
/*  626:1110 */               if (this.m_modelNormal[i][j][1] <= minStdD) {
/*  627:1111 */                 this.m_modelNormal[i][j][1] = minStdD;
/*  628:     */               }
/*  629:     */             }
/*  630:1114 */             if (this.m_modelNormal[i][j][1] <= 0.0D) {
/*  631:1115 */               this.m_modelNormal[i][j][1] = this.m_minStdDev;
/*  632:     */             }
/*  633:1117 */             if (Double.isInfinite(this.m_modelNormal[i][j][1])) {
/*  634:1118 */               this.m_modelNormal[i][j][1] = this.m_minStdDev;
/*  635:     */             }
/*  636:1122 */             this.m_modelNormal[i][j][0] /= this.m_modelNormal[i][j][2];
/*  637:     */           }
/*  638:     */         }
/*  639:     */       }
/*  640:     */     }
/*  641:     */   }
/*  642:     */   
/*  643:     */   private void M(Instances inst)
/*  644:     */     throws Exception
/*  645:     */   {
/*  646:1139 */     new_estimators();
/*  647:1140 */     estimate_priors(inst);
/*  648:1143 */     for (int l = 0; l < inst.numInstances(); l++)
/*  649:     */     {
/*  650:1144 */       Instance in = inst.instance(l);
/*  651:1145 */       for (int i = 0; i < this.m_num_clusters; i++) {
/*  652:1146 */         for (int j = 0; j < this.m_num_attribs; j++) {
/*  653:1147 */           if (inst.attribute(j).isNominal())
/*  654:     */           {
/*  655:1148 */             this.m_model[i][j].addValue(in.value(j), in.weight() * this.m_weights[l][i]);
/*  656:     */           }
/*  657:     */           else
/*  658:     */           {
/*  659:1151 */             this.m_modelNormal[i][j][0] += in.value(j) * in.weight() * this.m_weights[l][i];
/*  660:1152 */             this.m_modelNormal[i][j][2] += in.weight() * this.m_weights[l][i];
/*  661:1153 */             this.m_modelNormal[i][j][1] += in.value(j) * in.value(j) * in.weight() * this.m_weights[l][i];
/*  662:     */           }
/*  663:     */         }
/*  664:     */       }
/*  665:     */     }
/*  666:1161 */     M_reEstimate(inst);
/*  667:     */   }
/*  668:     */   
/*  669:     */   private double E(Instances inst, boolean change_weights)
/*  670:     */     throws Exception
/*  671:     */   {
/*  672:1174 */     double loglk = 0.0D;double sOW = 0.0D;
/*  673:1176 */     for (int l = 0; l < inst.numInstances(); l++)
/*  674:     */     {
/*  675:1178 */       Instance in = inst.instance(l);
/*  676:     */       
/*  677:1180 */       loglk += in.weight() * logDensityForInstance(in);
/*  678:1181 */       sOW += in.weight();
/*  679:1183 */       if (change_weights) {
/*  680:1184 */         this.m_weights[l] = distributionForInstance(in);
/*  681:     */       }
/*  682:     */     }
/*  683:1188 */     if (sOW <= 0.0D) {
/*  684:1189 */       return 0.0D;
/*  685:     */     }
/*  686:1196 */     return loglk / sOW;
/*  687:     */   }
/*  688:     */   
/*  689:     */   public EM()
/*  690:     */   {
/*  691:1206 */     this.m_SeedDefault = 100;
/*  692:1207 */     resetOptions();
/*  693:     */   }
/*  694:     */   
/*  695:     */   protected void resetOptions()
/*  696:     */   {
/*  697:1214 */     this.m_minStdDev = 1.0E-006D;
/*  698:1215 */     this.m_max_iterations = 100;
/*  699:1216 */     this.m_Seed = this.m_SeedDefault;
/*  700:1217 */     this.m_num_clusters = -1;
/*  701:1218 */     this.m_initialNumClusters = -1;
/*  702:1219 */     this.m_verbose = false;
/*  703:1220 */     this.m_minLogLikelihoodImprovementIterating = 1.0E-006D;
/*  704:1221 */     this.m_minLogLikelihoodImprovementCV = 1.0E-006D;
/*  705:1222 */     this.m_executionSlots = 1;
/*  706:1223 */     this.m_cvFolds = 10;
/*  707:     */   }
/*  708:     */   
/*  709:     */   public double[][][] getClusterModelsNumericAtts()
/*  710:     */   {
/*  711:1232 */     return this.m_modelNormal;
/*  712:     */   }
/*  713:     */   
/*  714:     */   public double[] getClusterPriors()
/*  715:     */   {
/*  716:1241 */     return this.m_priors;
/*  717:     */   }
/*  718:     */   
/*  719:     */   public String toString()
/*  720:     */   {
/*  721:1251 */     if (this.m_displayModelInOldFormat) {
/*  722:1252 */       return toStringOriginal();
/*  723:     */     }
/*  724:1255 */     if (this.m_priors == null) {
/*  725:1256 */       return "No clusterer built yet!";
/*  726:     */     }
/*  727:1258 */     StringBuffer temp = new StringBuffer();
/*  728:1259 */     temp.append("\nEM\n==\n");
/*  729:1260 */     if (this.m_initialNumClusters == -1) {
/*  730:1261 */       temp.append("\nNumber of clusters selected by cross validation: " + this.m_num_clusters + "\n");
/*  731:     */     } else {
/*  732:1264 */       temp.append("\nNumber of clusters: " + this.m_num_clusters + "\n");
/*  733:     */     }
/*  734:1267 */     temp.append("Number of iterations performed: " + this.m_iterationsPerformed + "\n");
/*  735:     */     
/*  736:     */ 
/*  737:1270 */     int maxWidth = 0;
/*  738:1271 */     int maxAttWidth = 0;
/*  739:1274 */     for (int i = 0; i < this.m_num_attribs; i++)
/*  740:     */     {
/*  741:1275 */       Attribute a = this.m_theInstances.attribute(i);
/*  742:1276 */       if (a.name().length() > maxAttWidth) {
/*  743:1277 */         maxAttWidth = this.m_theInstances.attribute(i).name().length();
/*  744:     */       }
/*  745:1279 */       if (a.isNominal()) {
/*  746:1281 */         for (int j = 0; j < a.numValues(); j++)
/*  747:     */         {
/*  748:1282 */           String val = a.value(j) + "  ";
/*  749:1283 */           if (val.length() > maxAttWidth) {
/*  750:1284 */             maxAttWidth = val.length();
/*  751:     */           }
/*  752:     */         }
/*  753:     */       }
/*  754:     */     }
/*  755:1290 */     for (int i = 0; i < this.m_num_clusters; i++) {
/*  756:1291 */       for (int j = 0; j < this.m_num_attribs; j++) {
/*  757:1292 */         if (this.m_theInstances.attribute(j).isNumeric())
/*  758:     */         {
/*  759:1294 */           double mean = Math.log(Math.abs(this.m_modelNormal[i][j][0])) / Math.log(10.0D);
/*  760:     */           
/*  761:1296 */           double stdD = Math.log(Math.abs(this.m_modelNormal[i][j][1])) / Math.log(10.0D);
/*  762:     */           
/*  763:1298 */           double width = mean > stdD ? mean : stdD;
/*  764:1299 */           if (width < 0.0D) {
/*  765:1300 */             width = 1.0D;
/*  766:     */           }
/*  767:1303 */           width += 6.0D;
/*  768:1304 */           if ((int)width > maxWidth) {
/*  769:1305 */             maxWidth = (int)width;
/*  770:     */           }
/*  771:     */         }
/*  772:     */         else
/*  773:     */         {
/*  774:1309 */           DiscreteEstimator d = (DiscreteEstimator)this.m_model[i][j];
/*  775:1310 */           for (int k = 0; k < d.getNumSymbols(); k++)
/*  776:     */           {
/*  777:1311 */             String size = Utils.doubleToString(d.getCount(k), maxWidth, 4).trim();
/*  778:1313 */             if (size.length() > maxWidth) {
/*  779:1314 */               maxWidth = size.length();
/*  780:     */             }
/*  781:     */           }
/*  782:1317 */           int sum = Utils.doubleToString(d.getSumOfCounts(), maxWidth, 4).trim().length();
/*  783:1319 */           if (sum > maxWidth) {
/*  784:1320 */             maxWidth = sum;
/*  785:     */           }
/*  786:     */         }
/*  787:     */       }
/*  788:     */     }
/*  789:1326 */     if (maxAttWidth < "Attribute".length()) {
/*  790:1327 */       maxAttWidth = "Attribute".length();
/*  791:     */     }
/*  792:1330 */     maxAttWidth += 2;
/*  793:     */     
/*  794:1332 */     temp.append("\n\n");
/*  795:1333 */     temp.append(pad("Cluster", " ", maxAttWidth + maxWidth + 1 - "Cluster".length(), true));
/*  796:     */     
/*  797:     */ 
/*  798:1336 */     temp.append("\n");
/*  799:1337 */     temp.append(pad("Attribute", " ", maxAttWidth - "Attribute".length(), false));
/*  800:1341 */     for (int i = 0; i < this.m_num_clusters; i++)
/*  801:     */     {
/*  802:1342 */       String classL = "" + i;
/*  803:1343 */       temp.append(pad(classL, " ", maxWidth + 1 - classL.length(), true));
/*  804:     */     }
/*  805:1345 */     temp.append("\n");
/*  806:     */     
/*  807:     */ 
/*  808:1348 */     temp.append(pad("", " ", maxAttWidth, true));
/*  809:1349 */     for (int i = 0; i < this.m_num_clusters; i++)
/*  810:     */     {
/*  811:1350 */       String priorP = Utils.doubleToString(this.m_priors[i], maxWidth, 2).trim();
/*  812:1351 */       priorP = "(" + priorP + ")";
/*  813:1352 */       temp.append(pad(priorP, " ", maxWidth + 1 - priorP.length(), true));
/*  814:     */     }
/*  815:1355 */     temp.append("\n");
/*  816:1356 */     temp.append(pad("", "=", maxAttWidth + maxWidth * this.m_num_clusters + this.m_num_clusters + 1, true));
/*  817:     */     
/*  818:1358 */     temp.append("\n");
/*  819:1360 */     for (int i = 0; i < this.m_num_attribs; i++)
/*  820:     */     {
/*  821:1361 */       String attName = this.m_theInstances.attribute(i).name();
/*  822:1362 */       temp.append(attName + "\n");
/*  823:1364 */       if (this.m_theInstances.attribute(i).isNumeric())
/*  824:     */       {
/*  825:1365 */         String meanL = "  mean";
/*  826:1366 */         temp.append(pad(meanL, " ", maxAttWidth + 1 - meanL.length(), false));
/*  827:1367 */         for (int j = 0; j < this.m_num_clusters; j++)
/*  828:     */         {
/*  829:1369 */           String mean = Utils.doubleToString(this.m_modelNormal[j][i][0], maxWidth, 4).trim();
/*  830:     */           
/*  831:1371 */           temp.append(pad(mean, " ", maxWidth + 1 - mean.length(), true));
/*  832:     */         }
/*  833:1373 */         temp.append("\n");
/*  834:     */         
/*  835:1375 */         String stdDevL = "  std. dev.";
/*  836:1376 */         temp.append(pad(stdDevL, " ", maxAttWidth + 1 - stdDevL.length(), false));
/*  837:1378 */         for (int j = 0; j < this.m_num_clusters; j++)
/*  838:     */         {
/*  839:1379 */           String stdDev = Utils.doubleToString(this.m_modelNormal[j][i][1], maxWidth, 4).trim();
/*  840:     */           
/*  841:1381 */           temp.append(pad(stdDev, " ", maxWidth + 1 - stdDev.length(), true));
/*  842:     */         }
/*  843:1383 */         temp.append("\n\n");
/*  844:     */       }
/*  845:     */       else
/*  846:     */       {
/*  847:1385 */         Attribute a = this.m_theInstances.attribute(i);
/*  848:1386 */         for (int j = 0; j < a.numValues(); j++)
/*  849:     */         {
/*  850:1387 */           String val = "  " + a.value(j);
/*  851:1388 */           temp.append(pad(val, " ", maxAttWidth + 1 - val.length(), false));
/*  852:1389 */           for (int k = 0; k < this.m_num_clusters; k++)
/*  853:     */           {
/*  854:1390 */             DiscreteEstimator d = (DiscreteEstimator)this.m_model[k][i];
/*  855:1391 */             String count = Utils.doubleToString(d.getCount(j), maxWidth, 4).trim();
/*  856:     */             
/*  857:1393 */             temp.append(pad(count, " ", maxWidth + 1 - count.length(), true));
/*  858:     */           }
/*  859:1395 */           temp.append("\n");
/*  860:     */         }
/*  861:1398 */         String total = "  [total]";
/*  862:1399 */         temp.append(pad(total, " ", maxAttWidth + 1 - total.length(), false));
/*  863:1400 */         for (int k = 0; k < this.m_num_clusters; k++)
/*  864:     */         {
/*  865:1401 */           DiscreteEstimator d = (DiscreteEstimator)this.m_model[k][i];
/*  866:1402 */           String count = Utils.doubleToString(d.getSumOfCounts(), maxWidth, 4).trim();
/*  867:     */           
/*  868:1404 */           temp.append(pad(count, " ", maxWidth + 1 - count.length(), true));
/*  869:     */         }
/*  870:1406 */         temp.append("\n");
/*  871:     */       }
/*  872:     */     }
/*  873:1410 */     return temp.toString();
/*  874:     */   }
/*  875:     */   
/*  876:     */   private String pad(String source, String padChar, int length, boolean leftPad)
/*  877:     */   {
/*  878:1414 */     StringBuffer temp = new StringBuffer();
/*  879:1416 */     if (leftPad)
/*  880:     */     {
/*  881:1417 */       for (int i = 0; i < length; i++) {
/*  882:1418 */         temp.append(padChar);
/*  883:     */       }
/*  884:1420 */       temp.append(source);
/*  885:     */     }
/*  886:     */     else
/*  887:     */     {
/*  888:1422 */       temp.append(source);
/*  889:1423 */       for (int i = 0; i < length; i++) {
/*  890:1424 */         temp.append(padChar);
/*  891:     */       }
/*  892:     */     }
/*  893:1427 */     return temp.toString();
/*  894:     */   }
/*  895:     */   
/*  896:     */   protected String toStringOriginal()
/*  897:     */   {
/*  898:1436 */     if (this.m_priors == null) {
/*  899:1437 */       return "No clusterer built yet!";
/*  900:     */     }
/*  901:1439 */     StringBuffer temp = new StringBuffer();
/*  902:1440 */     temp.append("\nEM\n==\n");
/*  903:1441 */     if (this.m_initialNumClusters == -1) {
/*  904:1442 */       temp.append("\nNumber of clusters selected by cross validation: " + this.m_num_clusters + "\n");
/*  905:     */     } else {
/*  906:1445 */       temp.append("\nNumber of clusters: " + this.m_num_clusters + "\n");
/*  907:     */     }
/*  908:1448 */     for (int j = 0; j < this.m_num_clusters; j++)
/*  909:     */     {
/*  910:1449 */       temp.append("\nCluster: " + j + " Prior probability: " + Utils.doubleToString(this.m_priors[j], 4) + "\n\n");
/*  911:1452 */       for (int i = 0; i < this.m_num_attribs; i++)
/*  912:     */       {
/*  913:1453 */         temp.append("Attribute: " + this.m_theInstances.attribute(i).name() + "\n");
/*  914:1455 */         if (this.m_theInstances.attribute(i).isNominal())
/*  915:     */         {
/*  916:1456 */           if (this.m_model[j][i] != null) {
/*  917:1457 */             temp.append(this.m_model[j][i].toString());
/*  918:     */           }
/*  919:     */         }
/*  920:     */         else {
/*  921:1460 */           temp.append("Normal Distribution. Mean = " + Utils.doubleToString(this.m_modelNormal[j][i][0], 4) + " StdDev = " + Utils.doubleToString(this.m_modelNormal[j][i][1], 4) + "\n");
/*  922:     */         }
/*  923:     */       }
/*  924:     */     }
/*  925:1467 */     return temp.toString();
/*  926:     */   }
/*  927:     */   
/*  928:     */   private void EM_Report(Instances inst)
/*  929:     */   {
/*  930:1477 */     System.out.println("======================================");
/*  931:1479 */     for (int j = 0; j < this.m_num_clusters; j++) {
/*  932:1480 */       for (int i = 0; i < this.m_num_attribs; i++)
/*  933:     */       {
/*  934:1481 */         System.out.println("Clust: " + j + " att: " + i + "\n");
/*  935:1483 */         if (this.m_theInstances.attribute(i).isNominal())
/*  936:     */         {
/*  937:1484 */           if (this.m_model[j][i] != null) {
/*  938:1485 */             System.out.println(this.m_model[j][i].toString());
/*  939:     */           }
/*  940:     */         }
/*  941:     */         else {
/*  942:1488 */           System.out.println("Normal Distribution. Mean = " + Utils.doubleToString(this.m_modelNormal[j][i][0], 8, 4) + " StandardDev = " + Utils.doubleToString(this.m_modelNormal[j][i][1], 8, 4) + " WeightSum = " + Utils.doubleToString(this.m_modelNormal[j][i][2], 8, 4));
/*  943:     */         }
/*  944:     */       }
/*  945:     */     }
/*  946:1498 */     for (int l = 0; l < inst.numInstances(); l++)
/*  947:     */     {
/*  948:1499 */       int m = Utils.maxIndex(this.m_weights[l]);
/*  949:1500 */       System.out.print("Inst " + Utils.doubleToString(l, 5, 0) + " Class " + m + "\t");
/*  950:1502 */       for (j = 0; j < this.m_num_clusters; j++) {
/*  951:1503 */         System.out.print(Utils.doubleToString(this.m_weights[l][j], 7, 5) + "  ");
/*  952:     */       }
/*  953:1505 */       System.out.println();
/*  954:     */     }
/*  955:     */   }
/*  956:     */   
/*  957:     */   private void CVClusters()
/*  958:     */     throws Exception
/*  959:     */   {
/*  960:1515 */     double CVLogLikely = -1.797693134862316E+308D;
/*  961:     */     
/*  962:1517 */     boolean CVincreased = true;
/*  963:1518 */     this.m_num_clusters = 1;
/*  964:1519 */     int upperBoundMaxClusters = this.m_upperBoundNumClustersCV > 0 ? this.m_upperBoundNumClustersCV : 2147483647;
/*  965:     */     
/*  966:1521 */     int num_clusters = this.m_num_clusters;
/*  967:     */     
/*  968:     */ 
/*  969:     */ 
/*  970:1525 */     int numFolds = this.m_theInstances.numInstances() < this.m_cvFolds ? this.m_theInstances.numInstances() : this.m_cvFolds;
/*  971:     */     
/*  972:     */ 
/*  973:1528 */     boolean ok = true;
/*  974:1529 */     int seed = getSeed();
/*  975:1530 */     int restartCount = 0;
/*  976:1531 */     while ((CVincreased) && 
/*  977:1532 */       (num_clusters <= upperBoundMaxClusters))
/*  978:     */     {
/*  979:1537 */       CVincreased = false;
/*  980:1538 */       Random cvr = new Random(getSeed());
/*  981:1539 */       Instances trainCopy = new Instances(this.m_theInstances);
/*  982:1540 */       trainCopy.randomize(cvr);
/*  983:1541 */       double templl = 0.0D;
/*  984:1542 */       for (int i = 0; i < numFolds; i++)
/*  985:     */       {
/*  986:1543 */         Instances cvTrain = trainCopy.trainCV(numFolds, i, cvr);
/*  987:1544 */         if (num_clusters > cvTrain.numInstances()) {
/*  988:     */           break label473;
/*  989:     */         }
/*  990:1547 */         Instances cvTest = trainCopy.testCV(numFolds, i);
/*  991:1548 */         this.m_rr = new Random(seed);
/*  992:1549 */         for (int z = 0; z < 10; z++) {
/*  993:1550 */           this.m_rr.nextDouble();
/*  994:     */         }
/*  995:1552 */         this.m_num_clusters = num_clusters;
/*  996:1553 */         EM_Init(cvTrain);
/*  997:     */         try
/*  998:     */         {
/*  999:1555 */           iterate(cvTrain, false);
/* 1000:     */         }
/* 1001:     */         catch (Exception ex)
/* 1002:     */         {
/* 1003:1558 */           ex.printStackTrace();
/* 1004:     */           
/* 1005:1560 */           seed++;
/* 1006:1561 */           restartCount++;
/* 1007:1562 */           ok = false;
/* 1008:1563 */           if (restartCount <= 5) {
/* 1009:     */             break label265;
/* 1010:     */           }
/* 1011:     */         }
/* 1012:     */         break label473;
/* 1013:     */         label265:
/* 1014:1566 */         break;
/* 1015:     */         double tll;
/* 1016:     */         try
/* 1017:     */         {
/* 1018:1569 */           tll = E(cvTest, false);
/* 1019:     */         }
/* 1020:     */         catch (Exception ex)
/* 1021:     */         {
/* 1022:1573 */           ex.printStackTrace();
/* 1023:     */           
/* 1024:     */ 
/* 1025:1576 */           seed++;
/* 1026:1577 */           restartCount++;
/* 1027:1578 */           ok = false;
/* 1028:1579 */           if (restartCount <= 5) {
/* 1029:     */             break label305;
/* 1030:     */           }
/* 1031:     */         }
/* 1032:     */         break label473;
/* 1033:     */         label305:
/* 1034:1582 */         break;
/* 1035:1585 */         if (this.m_verbose) {
/* 1036:1586 */           System.out.println("# clust: " + num_clusters + " Fold: " + i + " Loglikely: " + tll);
/* 1037:     */         }
/* 1038:1589 */         templl += tll;
/* 1039:     */       }
/* 1040:1592 */       if (ok)
/* 1041:     */       {
/* 1042:1593 */         restartCount = 0;
/* 1043:1594 */         seed = getSeed();
/* 1044:1595 */         templl /= numFolds;
/* 1045:1597 */         if (this.m_verbose) {
/* 1046:1598 */           System.out.println("=================================================\n# clust: " + num_clusters + " Mean Loglikely: " + templl + "\n================================" + "=================");
/* 1047:     */         }
/* 1048:1605 */         if (templl - CVLogLikely > this.m_minLogLikelihoodImprovementCV)
/* 1049:     */         {
/* 1050:1606 */           CVLogLikely = templl;
/* 1051:1607 */           CVincreased = true;
/* 1052:1608 */           num_clusters++;
/* 1053:     */         }
/* 1054:     */       }
/* 1055:     */     }
/* 1056:     */     label473:
/* 1057:1613 */     if (this.m_verbose) {
/* 1058:1614 */       System.out.println("Number of clusters: " + (num_clusters - 1));
/* 1059:     */     }
/* 1060:1617 */     this.m_num_clusters = (num_clusters - 1);
/* 1061:     */   }
/* 1062:     */   
/* 1063:     */   public int numberOfClusters()
/* 1064:     */     throws Exception
/* 1065:     */   {
/* 1066:1628 */     if (this.m_num_clusters == -1) {
/* 1067:1629 */       throw new Exception("Haven't generated any clusters!");
/* 1068:     */     }
/* 1069:1632 */     return this.m_num_clusters;
/* 1070:     */   }
/* 1071:     */   
/* 1072:     */   private void updateMinMax(Instance instance)
/* 1073:     */   {
/* 1074:1643 */     for (int j = 0; j < this.m_theInstances.numAttributes(); j++) {
/* 1075:1644 */       if (instance.value(j) < this.m_minValues[j]) {
/* 1076:1645 */         this.m_minValues[j] = instance.value(j);
/* 1077:1647 */       } else if (instance.value(j) > this.m_maxValues[j]) {
/* 1078:1648 */         this.m_maxValues[j] = instance.value(j);
/* 1079:     */       }
/* 1080:     */     }
/* 1081:     */   }
/* 1082:     */   
/* 1083:     */   public Capabilities getCapabilities()
/* 1084:     */   {
/* 1085:1662 */     Capabilities result = new SimpleKMeans().getCapabilities();
/* 1086:1663 */     result.setOwner(this);
/* 1087:1664 */     return result;
/* 1088:     */   }
/* 1089:     */   
/* 1090:     */   public void buildClusterer(Instances data)
/* 1091:     */     throws Exception
/* 1092:     */   {
/* 1093:1676 */     this.m_training = true;
/* 1094:     */     
/* 1095:     */ 
/* 1096:1679 */     getCapabilities().testWithFail(data);
/* 1097:     */     
/* 1098:1681 */     this.m_replaceMissing = new ReplaceMissingValues();
/* 1099:1682 */     Instances instances = new Instances(data);
/* 1100:1683 */     instances.setClassIndex(-1);
/* 1101:1684 */     this.m_replaceMissing.setInputFormat(instances);
/* 1102:1685 */     data = Filter.useFilter(instances, this.m_replaceMissing);
/* 1103:1686 */     instances = null;
/* 1104:     */     
/* 1105:1688 */     this.m_theInstances = data;
/* 1106:     */     
/* 1107:     */ 
/* 1108:1691 */     this.m_minValues = new double[this.m_theInstances.numAttributes()];
/* 1109:1692 */     this.m_maxValues = new double[this.m_theInstances.numAttributes()];
/* 1110:1693 */     for (int i = 0; i < this.m_theInstances.numAttributes(); i++)
/* 1111:     */     {
/* 1112:1694 */       this.m_minValues[i] = 1.7976931348623157E+308D;
/* 1113:1695 */       this.m_maxValues[i] = -1.797693134862316E+308D;
/* 1114:     */     }
/* 1115:1697 */     for (int i = 0; i < this.m_theInstances.numInstances(); i++) {
/* 1116:1698 */       updateMinMax(this.m_theInstances.instance(i));
/* 1117:     */     }
/* 1118:1701 */     doEM();
/* 1119:     */     
/* 1120:     */ 
/* 1121:1704 */     this.m_theInstances = new Instances(this.m_theInstances, 0);
/* 1122:1705 */     this.m_training = false;
/* 1123:     */   }
/* 1124:     */   
/* 1125:     */   public double[] clusterPriors()
/* 1126:     */   {
/* 1127:1716 */     double[] n = new double[this.m_priors.length];
/* 1128:     */     
/* 1129:1718 */     System.arraycopy(this.m_priors, 0, n, 0, n.length);
/* 1130:1719 */     return n;
/* 1131:     */   }
/* 1132:     */   
/* 1133:     */   public double[] logDensityPerClusterForInstance(Instance inst)
/* 1134:     */     throws Exception
/* 1135:     */   {
/* 1136:1736 */     double[] wghts = new double[this.m_num_clusters];
/* 1137:1738 */     if (!this.m_training)
/* 1138:     */     {
/* 1139:1739 */       this.m_replaceMissing.input(inst);
/* 1140:1740 */       inst = this.m_replaceMissing.output();
/* 1141:     */     }
/* 1142:1743 */     for (int i = 0; i < this.m_num_clusters; i++)
/* 1143:     */     {
/* 1144:1745 */       double logprob = 0.0D;
/* 1145:1747 */       for (int j = 0; j < this.m_num_attribs; j++) {
/* 1146:1748 */         if (inst.attribute(j).isNominal()) {
/* 1147:1749 */           logprob += Math.log(this.m_model[i][j].getProbability(inst.value(j)));
/* 1148:     */         } else {
/* 1149:1751 */           logprob += logNormalDens(inst.value(j), this.m_modelNormal[i][j][0], this.m_modelNormal[i][j][1]);
/* 1150:     */         }
/* 1151:     */       }
/* 1152:1761 */       wghts[i] = logprob;
/* 1153:     */     }
/* 1154:1763 */     return wghts;
/* 1155:     */   }
/* 1156:     */   
/* 1157:     */   private void doEM()
/* 1158:     */     throws Exception
/* 1159:     */   {
/* 1160:1773 */     if (this.m_verbose) {
/* 1161:1774 */       System.out.println("Seed: " + getSeed());
/* 1162:     */     }
/* 1163:1777 */     this.m_rr = new Random(getSeed());
/* 1164:1781 */     for (int i = 0; i < 10; i++) {
/* 1165:1782 */       this.m_rr.nextDouble();
/* 1166:     */     }
/* 1167:1785 */     this.m_num_instances = this.m_theInstances.numInstances();
/* 1168:1786 */     this.m_num_attribs = this.m_theInstances.numAttributes();
/* 1169:1788 */     if (this.m_verbose) {
/* 1170:1789 */       System.out.println("Number of instances: " + this.m_num_instances + "\nNumber of atts: " + this.m_num_attribs + "\n");
/* 1171:     */     }
/* 1172:1792 */     startExecutorPool();
/* 1173:1796 */     if (this.m_initialNumClusters == -1) {
/* 1174:1797 */       if (this.m_theInstances.numInstances() > 9)
/* 1175:     */       {
/* 1176:1798 */         CVClusters();
/* 1177:1799 */         this.m_rr = new Random(getSeed());
/* 1178:1800 */         for (int i = 0; i < 10; i++) {
/* 1179:1801 */           this.m_rr.nextDouble();
/* 1180:     */         }
/* 1181:     */       }
/* 1182:     */       else
/* 1183:     */       {
/* 1184:1804 */         this.m_num_clusters = 1;
/* 1185:     */       }
/* 1186:     */     }
/* 1187:1809 */     EM_Init(this.m_theInstances);
/* 1188:1810 */     double loglikely = iterate(this.m_theInstances, this.m_verbose);
/* 1189:1811 */     if (this.m_Debug) {
/* 1190:1812 */       System.err.println("Current log-likelihood: " + loglikely);
/* 1191:     */     }
/* 1192:1815 */     this.m_executorPool.shutdown();
/* 1193:     */   }
/* 1194:     */   
/* 1195:     */   protected double launchESteps(Instances inst)
/* 1196:     */     throws Exception
/* 1197:     */   {
/* 1198:1826 */     int numPerTask = inst.numInstances() / this.m_executionSlots;
/* 1199:1827 */     double eStepLogL = 0.0D;
/* 1200:1828 */     double eStepSow = 0.0D;
/* 1201:1830 */     if ((this.m_executionSlots <= 1) || (inst.numInstances() < 2 * this.m_executionSlots)) {
/* 1202:1831 */       return E(inst, true);
/* 1203:     */     }
/* 1204:1834 */     List<Future<double[]>> results = new ArrayList();
/* 1205:1836 */     for (int i = 0; i < this.m_executionSlots; i++)
/* 1206:     */     {
/* 1207:1837 */       int start = i * numPerTask;
/* 1208:1838 */       int end = start + numPerTask;
/* 1209:1839 */       if (i == this.m_executionSlots - 1) {
/* 1210:1840 */         end = inst.numInstances();
/* 1211:     */       }
/* 1212:1842 */       ETask newTask = new ETask(inst, start, end, true);
/* 1213:1843 */       Future<double[]> futureE = this.m_executorPool.submit(newTask);
/* 1214:1844 */       results.add(futureE);
/* 1215:     */     }
/* 1216:1850 */     for (int i = 0; i < results.size(); i++)
/* 1217:     */     {
/* 1218:1851 */       double[] r = (double[])((Future)results.get(i)).get();
/* 1219:     */       
/* 1220:1853 */       eStepLogL += r[0];
/* 1221:1854 */       eStepSow += r[1];
/* 1222:     */     }
/* 1223:1857 */     eStepLogL /= eStepSow;
/* 1224:     */     
/* 1225:1859 */     return eStepLogL;
/* 1226:     */   }
/* 1227:     */   
/* 1228:     */   protected void launchMSteps(Instances inst)
/* 1229:     */     throws Exception
/* 1230:     */   {
/* 1231:1869 */     if ((this.m_executionSlots <= 1) || (inst.numInstances() < 2 * this.m_executionSlots))
/* 1232:     */     {
/* 1233:1870 */       M(inst);
/* 1234:1871 */       return;
/* 1235:     */     }
/* 1236:1875 */     new_estimators();
/* 1237:1876 */     estimate_priors(inst);
/* 1238:     */     
/* 1239:1878 */     int numPerTask = inst.numInstances() / this.m_executionSlots;
/* 1240:1879 */     List<Future<MTask>> results = new ArrayList();
/* 1241:1881 */     for (int i = 0; i < this.m_executionSlots; i++)
/* 1242:     */     {
/* 1243:1882 */       int start = i * numPerTask;
/* 1244:1883 */       int end = start + numPerTask;
/* 1245:1884 */       if (i == this.m_executionSlots - 1) {
/* 1246:1885 */         end = inst.numInstances();
/* 1247:     */       }
/* 1248:1888 */       DiscreteEstimator[][] model = new DiscreteEstimator[this.m_num_clusters][this.m_num_attribs];
/* 1249:1889 */       double[][][] normal = new double[this.m_num_clusters][this.m_num_attribs][3];
/* 1250:1890 */       for (int ii = 0; ii < this.m_num_clusters; ii++) {
/* 1251:1891 */         for (int j = 0; j < this.m_num_attribs; j++) {
/* 1252:1892 */           if (this.m_theInstances.attribute(j).isNominal())
/* 1253:     */           {
/* 1254:1893 */             model[ii][j] = new DiscreteEstimator(this.m_theInstances.attribute(j).numValues(), false);
/* 1255:     */           }
/* 1256:     */           else
/* 1257:     */           {
/* 1258:1896 */             double tmp224_223 = (normal[ii][j][2] = 0.0D);normal[ii][j][1] = tmp224_223;normal[ii][j][0] = tmp224_223;
/* 1259:     */           }
/* 1260:     */         }
/* 1261:     */       }
/* 1262:1901 */       MTask newTask = new MTask(inst, start, end, model, normal);
/* 1263:1902 */       Future<MTask> futureM = this.m_executorPool.submit(newTask);
/* 1264:1903 */       results.add(futureM);
/* 1265:     */     }
/* 1266:1907 */     for (Future<MTask> t : results)
/* 1267:     */     {
/* 1268:1908 */       MTask m = (MTask)t.get();
/* 1269:1911 */       for (int i = 0; i < this.m_num_clusters; i++) {
/* 1270:1912 */         for (int j = 0; j < this.m_num_attribs; j++) {
/* 1271:1913 */           if (this.m_theInstances.attribute(j).isNominal())
/* 1272:     */           {
/* 1273:1914 */             for (int k = 0; k < this.m_theInstances.attribute(j).numValues(); k++) {
/* 1274:1915 */               this.m_model[i][j].addValue(k, m.m_taskModel[i][j].getCount(k));
/* 1275:     */             }
/* 1276:     */           }
/* 1277:     */           else
/* 1278:     */           {
/* 1279:1918 */             this.m_modelNormal[i][j][0] += m.m_taskModelNormal[i][j][0];
/* 1280:1919 */             this.m_modelNormal[i][j][2] += m.m_taskModelNormal[i][j][2];
/* 1281:1920 */             this.m_modelNormal[i][j][1] += m.m_taskModelNormal[i][j][1];
/* 1282:     */           }
/* 1283:     */         }
/* 1284:     */       }
/* 1285:     */     }
/* 1286:1927 */     M_reEstimate(inst);
/* 1287:     */   }
/* 1288:     */   
/* 1289:     */   private double iterate(Instances inst, boolean report)
/* 1290:     */     throws Exception
/* 1291:     */   {
/* 1292:1941 */     double llkold = 0.0D;
/* 1293:1942 */     double llk = 0.0D;
/* 1294:1944 */     if (report) {
/* 1295:1945 */       EM_Report(inst);
/* 1296:     */     }
/* 1297:1948 */     boolean ok = false;
/* 1298:1949 */     int seed = getSeed();
/* 1299:1950 */     int restartCount = 0;
/* 1300:1951 */     this.m_iterationsPerformed = -1;
/* 1301:1952 */     while (!ok) {
/* 1302:     */       try
/* 1303:     */       {
/* 1304:1954 */         for (int i = 0; i < this.m_max_iterations; i++)
/* 1305:     */         {
/* 1306:1955 */           llkold = llk;
/* 1307:     */           
/* 1308:1957 */           llk = launchESteps(inst);
/* 1309:1959 */           if (report) {
/* 1310:1960 */             System.out.println("Loglikely: " + llk);
/* 1311:     */           }
/* 1312:1963 */           if ((i > 0) && 
/* 1313:1964 */             (llk - llkold < this.m_minLogLikelihoodImprovementIterating))
/* 1314:     */           {
/* 1315:1965 */             if (llk - llkold < 0.0D)
/* 1316:     */             {
/* 1317:1968 */               this.m_modelNormal = this.m_modelNormalPrev;
/* 1318:1969 */               this.m_model = this.m_modelPrev;
/* 1319:1970 */               this.m_priors = this.m_priorsPrev;
/* 1320:1971 */               this.m_iterationsPerformed = (i - 1); break;
/* 1321:     */             }
/* 1322:1973 */             this.m_iterationsPerformed = i;
/* 1323:     */             
/* 1324:1975 */             break;
/* 1325:     */           }
/* 1326:1979 */           launchMSteps(inst);
/* 1327:     */         }
/* 1328:1981 */         ok = true;
/* 1329:     */       }
/* 1330:     */       catch (Exception ex)
/* 1331:     */       {
/* 1332:1984 */         ex.printStackTrace();
/* 1333:1985 */         seed++;
/* 1334:1986 */         restartCount++;
/* 1335:1987 */         this.m_rr = new Random(seed);
/* 1336:1988 */         for (int z = 0; z < 10; z++)
/* 1337:     */         {
/* 1338:1989 */           this.m_rr.nextDouble();
/* 1339:1990 */           this.m_rr.nextInt();
/* 1340:     */         }
/* 1341:1992 */         if (restartCount > 5)
/* 1342:     */         {
/* 1343:1994 */           this.m_num_clusters -= 1;
/* 1344:1995 */           restartCount = 0;
/* 1345:     */         }
/* 1346:1997 */         EM_Init(this.m_theInstances);
/* 1347:1998 */         startExecutorPool();
/* 1348:     */       }
/* 1349:     */     }
/* 1350:2002 */     if (this.m_iterationsPerformed == -1) {
/* 1351:2003 */       this.m_iterationsPerformed = this.m_max_iterations;
/* 1352:     */     }
/* 1353:2006 */     if (this.m_verbose) {
/* 1354:2007 */       System.out.println("# iterations performed: " + this.m_iterationsPerformed);
/* 1355:     */     }
/* 1356:2010 */     if (report) {
/* 1357:2011 */       EM_Report(inst);
/* 1358:     */     }
/* 1359:2014 */     return llk;
/* 1360:     */   }
/* 1361:     */   
/* 1362:     */   public String getRevision()
/* 1363:     */   {
/* 1364:2024 */     return RevisionUtils.extract("$Revision: 11451 $");
/* 1365:     */   }
/* 1366:     */   
/* 1367:     */   public static void main(String[] argv)
/* 1368:     */   {
/* 1369:2039 */     runClusterer(new EM(), argv);
/* 1370:     */   }
/* 1371:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.clusterers.EM
 * JD-Core Version:    0.7.0.1
 */