/*    1:     */ package weka.attributeSelection;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.util.BitSet;
/*    5:     */ import java.util.Enumeration;
/*    6:     */ import java.util.HashSet;
/*    7:     */ import java.util.Set;
/*    8:     */ import java.util.Vector;
/*    9:     */ import java.util.concurrent.Callable;
/*   10:     */ import java.util.concurrent.ExecutorService;
/*   11:     */ import java.util.concurrent.Executors;
/*   12:     */ import java.util.concurrent.Future;
/*   13:     */ import java.util.concurrent.atomic.AtomicInteger;
/*   14:     */ import weka.core.Attribute;
/*   15:     */ import weka.core.Capabilities;
/*   16:     */ import weka.core.Capabilities.Capability;
/*   17:     */ import weka.core.ContingencyTables;
/*   18:     */ import weka.core.Instance;
/*   19:     */ import weka.core.Instances;
/*   20:     */ import weka.core.Option;
/*   21:     */ import weka.core.OptionHandler;
/*   22:     */ import weka.core.RevisionUtils;
/*   23:     */ import weka.core.TechnicalInformation;
/*   24:     */ import weka.core.TechnicalInformation.Field;
/*   25:     */ import weka.core.TechnicalInformation.Type;
/*   26:     */ import weka.core.TechnicalInformationHandler;
/*   27:     */ import weka.core.ThreadSafe;
/*   28:     */ import weka.core.Utils;
/*   29:     */ import weka.filters.Filter;
/*   30:     */ import weka.filters.supervised.attribute.Discretize;
/*   31:     */ 
/*   32:     */ public class CfsSubsetEval
/*   33:     */   extends ASEvaluation
/*   34:     */   implements SubsetEvaluator, ThreadSafe, OptionHandler, TechnicalInformationHandler
/*   35:     */ {
/*   36:     */   static final long serialVersionUID = 747878400813276317L;
/*   37:     */   private Instances m_trainInstances;
/*   38:     */   private Discretize m_disTransform;
/*   39:     */   private int m_classIndex;
/*   40:     */   private boolean m_isNumeric;
/*   41:     */   private int m_numAttribs;
/*   42:     */   private int m_numInstances;
/*   43:     */   private boolean m_missingSeparate;
/*   44:     */   private boolean m_locallyPredictive;
/*   45:     */   private float[][] m_corr_matrix;
/*   46:     */   private double[] m_std_devs;
/*   47:     */   private double m_c_Threshold;
/*   48:     */   protected boolean m_debug;
/*   49:     */   protected int m_numEntries;
/*   50:     */   protected AtomicInteger m_numFilled;
/*   51:     */   protected boolean m_preComputeCorrelationMatrix;
/*   52: 167 */   protected int m_numThreads = 1;
/*   53: 173 */   protected int m_poolSize = 1;
/*   54: 176 */   protected transient ExecutorService m_pool = null;
/*   55:     */   
/*   56:     */   public String globalInfo()
/*   57:     */   {
/*   58: 185 */     return "CfsSubsetEval :\n\nEvaluates the worth of a subset of attributes by considering the individual predictive ability of each feature along with the degree of redundancy between them.\n\nSubsets of features that are highly correlated with the class while having low intercorrelation are preferred.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*   59:     */   }
/*   60:     */   
/*   61:     */   public TechnicalInformation getTechnicalInformation()
/*   62:     */   {
/*   63: 204 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.PHDTHESIS);
/*   64: 205 */     result.setValue(TechnicalInformation.Field.AUTHOR, "M. A. Hall");
/*   65: 206 */     result.setValue(TechnicalInformation.Field.YEAR, "1998");
/*   66: 207 */     result.setValue(TechnicalInformation.Field.TITLE, "Correlation-based Feature Subset Selection for Machine Learning");
/*   67:     */     
/*   68: 209 */     result.setValue(TechnicalInformation.Field.SCHOOL, "University of Waikato");
/*   69: 210 */     result.setValue(TechnicalInformation.Field.ADDRESS, "Hamilton, New Zealand");
/*   70:     */     
/*   71: 212 */     return result;
/*   72:     */   }
/*   73:     */   
/*   74:     */   public CfsSubsetEval()
/*   75:     */   {
/*   76: 219 */     resetOptions();
/*   77:     */   }
/*   78:     */   
/*   79:     */   public Enumeration<Option> listOptions()
/*   80:     */   {
/*   81: 230 */     Vector<Option> newVector = new Vector(6);
/*   82: 231 */     newVector.addElement(new Option("\tTreat missing values as a separate value.", "M", 0, "-M"));
/*   83:     */     
/*   84: 233 */     newVector.addElement(new Option("\tDon't include locally predictive attributes.", "L", 0, "-L"));
/*   85:     */     
/*   86:     */ 
/*   87: 236 */     newVector.addElement(new Option("\t" + preComputeCorrelationMatrixTipText(), "Z", 0, "-Z"));
/*   88:     */     
/*   89:     */ 
/*   90: 239 */     newVector.addElement(new Option("\t" + poolSizeTipText() + " (default 1)\n", "P", 1, "-P <int>"));
/*   91:     */     
/*   92: 241 */     newVector.addElement(new Option("\t" + numThreadsTipText() + " (default 1)\n", "E", 1, "-E <int>"));
/*   93:     */     
/*   94: 243 */     newVector.addElement(new Option("\tOutput debugging info.", "D", 0, "-D"));
/*   95:     */     
/*   96: 245 */     return newVector.elements();
/*   97:     */   }
/*   98:     */   
/*   99:     */   public void setOptions(String[] options)
/*  100:     */     throws Exception
/*  101:     */   {
/*  102: 294 */     resetOptions();
/*  103: 295 */     setMissingSeparate(Utils.getFlag('M', options));
/*  104: 296 */     setLocallyPredictive(!Utils.getFlag('L', options));
/*  105: 297 */     setPreComputeCorrelationMatrix(Utils.getFlag('Z', options));
/*  106:     */     
/*  107: 299 */     String PoolSize = Utils.getOption('P', options);
/*  108: 300 */     if (PoolSize.length() != 0) {
/*  109: 301 */       setPoolSize(Integer.parseInt(PoolSize));
/*  110:     */     } else {
/*  111: 303 */       setPoolSize(1);
/*  112:     */     }
/*  113: 305 */     String NumThreads = Utils.getOption('E', options);
/*  114: 306 */     if (NumThreads.length() != 0) {
/*  115: 307 */       setNumThreads(Integer.parseInt(NumThreads));
/*  116:     */     } else {
/*  117: 309 */       setNumThreads(1);
/*  118:     */     }
/*  119: 312 */     setDebug(Utils.getFlag('D', options));
/*  120:     */   }
/*  121:     */   
/*  122:     */   public String preComputeCorrelationMatrixTipText()
/*  123:     */   {
/*  124: 319 */     return "Precompute the full correlation matrix at the outset, rather than compute correlations lazily (as needed) during the search. Use this in conjuction with parallel processing in order to speed up a backward search.";
/*  125:     */   }
/*  126:     */   
/*  127:     */   public void setPreComputeCorrelationMatrix(boolean p)
/*  128:     */   {
/*  129: 333 */     this.m_preComputeCorrelationMatrix = p;
/*  130:     */   }
/*  131:     */   
/*  132:     */   public boolean getPreComputeCorrelationMatrix()
/*  133:     */   {
/*  134: 344 */     return this.m_preComputeCorrelationMatrix;
/*  135:     */   }
/*  136:     */   
/*  137:     */   public String numThreadsTipText()
/*  138:     */   {
/*  139: 352 */     return "The number of threads to use, which should be >= size of thread pool.";
/*  140:     */   }
/*  141:     */   
/*  142:     */   public int getNumThreads()
/*  143:     */   {
/*  144: 360 */     return this.m_numThreads;
/*  145:     */   }
/*  146:     */   
/*  147:     */   public void setNumThreads(int nT)
/*  148:     */   {
/*  149: 368 */     this.m_numThreads = nT;
/*  150:     */   }
/*  151:     */   
/*  152:     */   public String poolSizeTipText()
/*  153:     */   {
/*  154: 376 */     return "The size of the thread pool, for example, the number of cores in the CPU.";
/*  155:     */   }
/*  156:     */   
/*  157:     */   public int getPoolSize()
/*  158:     */   {
/*  159: 384 */     return this.m_poolSize;
/*  160:     */   }
/*  161:     */   
/*  162:     */   public void setPoolSize(int nT)
/*  163:     */   {
/*  164: 392 */     this.m_poolSize = nT;
/*  165:     */   }
/*  166:     */   
/*  167:     */   public String locallyPredictiveTipText()
/*  168:     */   {
/*  169: 402 */     return "Identify locally predictive attributes. Iteratively adds attributes with the highest correlation with the class as long as there is not already an attribute in the subset that has a higher correlation with the attribute in question";
/*  170:     */   }
/*  171:     */   
/*  172:     */   public void setLocallyPredictive(boolean b)
/*  173:     */   {
/*  174: 414 */     this.m_locallyPredictive = b;
/*  175:     */   }
/*  176:     */   
/*  177:     */   public boolean getLocallyPredictive()
/*  178:     */   {
/*  179: 423 */     return this.m_locallyPredictive;
/*  180:     */   }
/*  181:     */   
/*  182:     */   public String missingSeparateTipText()
/*  183:     */   {
/*  184: 433 */     return "Treat missing as a separate value. Otherwise, counts for missing values are distributed across other values in proportion to their frequency.";
/*  185:     */   }
/*  186:     */   
/*  187:     */   public void setMissingSeparate(boolean b)
/*  188:     */   {
/*  189: 444 */     this.m_missingSeparate = b;
/*  190:     */   }
/*  191:     */   
/*  192:     */   public boolean getMissingSeparate()
/*  193:     */   {
/*  194: 453 */     return this.m_missingSeparate;
/*  195:     */   }
/*  196:     */   
/*  197:     */   public void setDebug(boolean d)
/*  198:     */   {
/*  199: 462 */     this.m_debug = d;
/*  200:     */   }
/*  201:     */   
/*  202:     */   public boolean getDebug()
/*  203:     */   {
/*  204: 471 */     return this.m_debug;
/*  205:     */   }
/*  206:     */   
/*  207:     */   public String debugTipText()
/*  208:     */   {
/*  209: 481 */     return "Output debugging info";
/*  210:     */   }
/*  211:     */   
/*  212:     */   public String[] getOptions()
/*  213:     */   {
/*  214: 492 */     Vector<String> options = new Vector();
/*  215: 494 */     if (getMissingSeparate()) {
/*  216: 495 */       options.add("-M");
/*  217:     */     }
/*  218: 498 */     if (!getLocallyPredictive()) {
/*  219: 499 */       options.add("-L");
/*  220:     */     }
/*  221: 502 */     if (getPreComputeCorrelationMatrix()) {
/*  222: 503 */       options.add("-Z");
/*  223:     */     }
/*  224: 506 */     options.add("-P");
/*  225: 507 */     options.add("" + getPoolSize());
/*  226:     */     
/*  227: 509 */     options.add("-E");
/*  228: 510 */     options.add("" + getNumThreads());
/*  229: 512 */     if (getDebug()) {
/*  230: 513 */       options.add("-D");
/*  231:     */     }
/*  232: 516 */     return (String[])options.toArray(new String[0]);
/*  233:     */   }
/*  234:     */   
/*  235:     */   public Capabilities getCapabilities()
/*  236:     */   {
/*  237: 527 */     Capabilities result = super.getCapabilities();
/*  238: 528 */     result.disableAll();
/*  239:     */     
/*  240:     */ 
/*  241: 531 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  242: 532 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  243: 533 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  244: 534 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  245:     */     
/*  246:     */ 
/*  247: 537 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  248: 538 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*  249: 539 */     result.enable(Capabilities.Capability.DATE_CLASS);
/*  250: 540 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  251:     */     
/*  252: 542 */     return result;
/*  253:     */   }
/*  254:     */   
/*  255:     */   public void buildEvaluator(Instances data)
/*  256:     */     throws Exception
/*  257:     */   {
/*  258: 559 */     getCapabilities().testWithFail(data);
/*  259:     */     
/*  260: 561 */     this.m_numEntries = 0;
/*  261: 562 */     this.m_numFilled = new AtomicInteger();
/*  262:     */     
/*  263: 564 */     this.m_trainInstances = new Instances(data);
/*  264: 565 */     this.m_trainInstances.deleteWithMissingClass();
/*  265: 566 */     this.m_classIndex = this.m_trainInstances.classIndex();
/*  266: 567 */     this.m_numAttribs = this.m_trainInstances.numAttributes();
/*  267: 568 */     this.m_numInstances = this.m_trainInstances.numInstances();
/*  268: 569 */     this.m_isNumeric = this.m_trainInstances.attribute(this.m_classIndex).isNumeric();
/*  269: 571 */     if (!this.m_isNumeric)
/*  270:     */     {
/*  271: 572 */       this.m_disTransform = new Discretize();
/*  272: 573 */       this.m_disTransform.setUseBetterEncoding(true);
/*  273: 574 */       this.m_disTransform.setInputFormat(this.m_trainInstances);
/*  274: 575 */       this.m_trainInstances = Filter.useFilter(this.m_trainInstances, this.m_disTransform);
/*  275: 576 */       if (this.m_debug) {
/*  276: 577 */         System.err.println("Finished discretizing input data");
/*  277:     */       }
/*  278:     */     }
/*  279: 581 */     this.m_std_devs = new double[this.m_numAttribs];
/*  280: 582 */     this.m_corr_matrix = new float[this.m_numAttribs][];
/*  281: 583 */     for (int i = 0; i < this.m_numAttribs; i++)
/*  282:     */     {
/*  283: 584 */       this.m_corr_matrix[i] = new float[i + 1];
/*  284: 585 */       this.m_numEntries += i + 1;
/*  285:     */     }
/*  286: 587 */     this.m_numEntries -= this.m_numAttribs;
/*  287: 589 */     for (int i = 0; i < this.m_corr_matrix.length; i++)
/*  288:     */     {
/*  289: 590 */       this.m_corr_matrix[i][i] = 1.0F;
/*  290: 591 */       this.m_std_devs[i] = 1.0D;
/*  291:     */     }
/*  292: 594 */     for (int i = 0; i < this.m_numAttribs; i++) {
/*  293: 595 */       for (int j = 0; j < this.m_corr_matrix[i].length - 1; j++) {
/*  294: 596 */         this.m_corr_matrix[i][j] = -999.0F;
/*  295:     */       }
/*  296:     */     }
/*  297: 600 */     if ((this.m_preComputeCorrelationMatrix) && (this.m_poolSize > 1))
/*  298:     */     {
/*  299: 601 */       this.m_pool = Executors.newFixedThreadPool(this.m_poolSize);
/*  300:     */       
/*  301: 603 */       Set<Future<Void>> results = new HashSet();
/*  302: 604 */       int numEntriesPerThread = (this.m_numEntries + this.m_numAttribs) / this.m_numThreads;
/*  303: 605 */       numEntriesPerThread = numEntriesPerThread < 1 ? 1 : numEntriesPerThread;
/*  304:     */       
/*  305: 607 */       int startRow = 0;
/*  306: 608 */       int startCol = 0;
/*  307:     */       
/*  308: 610 */       int count = 0;
/*  309: 611 */       for (int i = 0; i < this.m_corr_matrix.length; i++) {
/*  310: 612 */         for (int j = 0; j < this.m_corr_matrix[i].length; j++)
/*  311:     */         {
/*  312: 613 */           count++;
/*  313: 614 */           if ((count == numEntriesPerThread) || ((i == this.m_corr_matrix.length - 1) && (j == this.m_corr_matrix[i].length - 1)))
/*  314:     */           {
/*  315: 616 */             final int sR = startRow;
/*  316: 617 */             final int sC = startCol;
/*  317: 618 */             final int eR = i;
/*  318: 619 */             final int eC = j;
/*  319:     */             
/*  320: 621 */             startRow = i;
/*  321: 622 */             startCol = j;
/*  322: 623 */             count = 0;
/*  323:     */             
/*  324: 625 */             Future<Void> future = this.m_pool.submit(new Callable()
/*  325:     */             {
/*  326:     */               public Void call()
/*  327:     */                 throws Exception
/*  328:     */               {
/*  329: 628 */                 if (CfsSubsetEval.this.m_debug) {
/*  330: 629 */                   System.err.println("Starting correlation computation task...");
/*  331:     */                 }
/*  332: 632 */                 for (int i = sR; i <= eR; i++) {
/*  333: 633 */                   for (int j = i == sR ? sC : 0; j < (i == eR ? eC : CfsSubsetEval.this.m_corr_matrix[i].length); j++) {
/*  334: 635 */                     if (CfsSubsetEval.this.m_corr_matrix[i][j] == -999.0F)
/*  335:     */                     {
/*  336: 636 */                       float corr = CfsSubsetEval.this.correlate(i, j);
/*  337: 637 */                       CfsSubsetEval.this.m_corr_matrix[i][j] = corr;
/*  338:     */                     }
/*  339:     */                   }
/*  340:     */                 }
/*  341: 641 */                 if (CfsSubsetEval.this.m_debug) {
/*  342: 642 */                   System.err.println("Percentage of correlation matrix computed: " + Utils.doubleToString(CfsSubsetEval.this.m_numFilled.get() / CfsSubsetEval.this.m_numEntries * 100.0D, 2) + "%");
/*  343:     */                 }
/*  344: 648 */                 return null;
/*  345:     */               }
/*  346: 650 */             });
/*  347: 651 */             results.add(future);
/*  348:     */           }
/*  349:     */         }
/*  350:     */       }
/*  351: 656 */       for (Future<Void> f : results) {
/*  352: 657 */         f.get();
/*  353:     */       }
/*  354: 661 */       this.m_pool.shutdown();
/*  355:     */     }
/*  356:     */   }
/*  357:     */   
/*  358:     */   public double evaluateSubset(BitSet subset)
/*  359:     */     throws Exception
/*  360:     */   {
/*  361: 674 */     double num = 0.0D;
/*  362: 675 */     double denom = 0.0D;
/*  363: 679 */     for (int i = 0; i < this.m_numAttribs; i++) {
/*  364: 680 */       if ((i != this.m_classIndex) && 
/*  365: 681 */         (subset.get(i)))
/*  366:     */       {
/*  367:     */         int smaller;
/*  368:     */         int smaller;
/*  369:     */         int larger;
/*  370: 682 */         if (i > this.m_classIndex)
/*  371:     */         {
/*  372: 683 */           int larger = i;
/*  373: 684 */           smaller = this.m_classIndex;
/*  374:     */         }
/*  375:     */         else
/*  376:     */         {
/*  377: 686 */           smaller = i;
/*  378: 687 */           larger = this.m_classIndex;
/*  379:     */         }
/*  380: 693 */         if (this.m_corr_matrix[larger][smaller] == -999.0F)
/*  381:     */         {
/*  382: 694 */           float corr = correlate(i, this.m_classIndex);
/*  383: 695 */           this.m_corr_matrix[larger][smaller] = corr;
/*  384: 696 */           num += this.m_std_devs[i] * corr;
/*  385:     */         }
/*  386:     */         else
/*  387:     */         {
/*  388: 698 */           num += this.m_std_devs[i] * this.m_corr_matrix[larger][smaller];
/*  389:     */         }
/*  390:     */       }
/*  391:     */     }
/*  392: 705 */     for (int i = 0; i < this.m_numAttribs; i++) {
/*  393: 706 */       if ((i != this.m_classIndex) && 
/*  394: 707 */         (subset.get(i)))
/*  395:     */       {
/*  396: 708 */         denom += 1.0D * this.m_std_devs[i] * this.m_std_devs[i];
/*  397: 710 */         for (int j = 0; j < this.m_corr_matrix[i].length - 1; j++) {
/*  398: 711 */           if (subset.get(j)) {
/*  399: 712 */             if (this.m_corr_matrix[i][j] == -999.0F)
/*  400:     */             {
/*  401: 713 */               float corr = correlate(i, j);
/*  402: 714 */               this.m_corr_matrix[i][j] = corr;
/*  403: 715 */               denom += 2.0D * this.m_std_devs[i] * this.m_std_devs[j] * corr;
/*  404:     */             }
/*  405:     */             else
/*  406:     */             {
/*  407: 717 */               denom += 2.0D * this.m_std_devs[i] * this.m_std_devs[j] * this.m_corr_matrix[i][j];
/*  408:     */             }
/*  409:     */           }
/*  410:     */         }
/*  411:     */       }
/*  412:     */     }
/*  413: 726 */     if (denom < 0.0D) {
/*  414: 727 */       denom *= -1.0D;
/*  415:     */     }
/*  416: 730 */     if (denom == 0.0D) {
/*  417: 731 */       return 0.0D;
/*  418:     */     }
/*  419: 734 */     double merit = num / Math.sqrt(denom);
/*  420: 736 */     if (merit < 0.0D) {
/*  421: 737 */       merit *= -1.0D;
/*  422:     */     }
/*  423: 740 */     return merit;
/*  424:     */   }
/*  425:     */   
/*  426:     */   private float correlate(int att1, int att2)
/*  427:     */   {
/*  428: 745 */     this.m_numFilled.addAndGet(1);
/*  429: 747 */     if (!this.m_isNumeric) {
/*  430: 748 */       return (float)symmUncertCorr(att1, att2);
/*  431:     */     }
/*  432: 751 */     boolean att1_is_num = this.m_trainInstances.attribute(att1).isNumeric();
/*  433: 752 */     boolean att2_is_num = this.m_trainInstances.attribute(att2).isNumeric();
/*  434: 754 */     if ((att1_is_num) && (att2_is_num)) {
/*  435: 755 */       return (float)num_num(att1, att2);
/*  436:     */     }
/*  437: 757 */     if (att2_is_num) {
/*  438: 758 */       return (float)num_nom2(att1, att2);
/*  439:     */     }
/*  440: 760 */     if (att1_is_num) {
/*  441: 761 */       return (float)num_nom2(att2, att1);
/*  442:     */     }
/*  443: 766 */     return (float)nom_nom(att1, att2);
/*  444:     */   }
/*  445:     */   
/*  446:     */   private double symmUncertCorr(int att1, int att2)
/*  447:     */   {
/*  448: 772 */     double sum = 0.0D;
/*  449:     */     
/*  450:     */ 
/*  451:     */ 
/*  452:     */ 
/*  453: 777 */     boolean flag = false;
/*  454: 778 */     double temp = 0.0D;
/*  455: 780 */     if ((att1 == this.m_classIndex) || (att2 == this.m_classIndex)) {
/*  456: 781 */       flag = true;
/*  457:     */     }
/*  458: 784 */     int ni = this.m_trainInstances.attribute(att1).numValues() + 1;
/*  459: 785 */     int nj = this.m_trainInstances.attribute(att2).numValues() + 1;
/*  460: 786 */     double[][] counts = new double[ni][nj];
/*  461: 787 */     double[] sumi = new double[ni];
/*  462: 788 */     double[] sumj = new double[nj];
/*  463: 790 */     for (int i = 0; i < ni; i++)
/*  464:     */     {
/*  465: 791 */       sumi[i] = 0.0D;
/*  466: 793 */       for (int j = 0; j < nj; j++)
/*  467:     */       {
/*  468: 794 */         sumj[j] = 0.0D;
/*  469: 795 */         counts[i][j] = 0.0D;
/*  470:     */       }
/*  471:     */     }
/*  472: 800 */     for (i = 0; i < this.m_numInstances; i++)
/*  473:     */     {
/*  474: 801 */       Instance inst = this.m_trainInstances.instance(i);
/*  475:     */       int ii;
/*  476:     */       int ii;
/*  477: 803 */       if (inst.isMissing(att1)) {
/*  478: 804 */         ii = ni - 1;
/*  479:     */       } else {
/*  480: 806 */         ii = (int)inst.value(att1);
/*  481:     */       }
/*  482:     */       int jj;
/*  483:     */       int jj;
/*  484: 809 */       if (inst.isMissing(att2)) {
/*  485: 810 */         jj = nj - 1;
/*  486:     */       } else {
/*  487: 812 */         jj = (int)inst.value(att2);
/*  488:     */       }
/*  489: 815 */       counts[ii][jj] += 1.0D;
/*  490:     */     }
/*  491: 819 */     for (i = 0; i < ni; i++)
/*  492:     */     {
/*  493: 820 */       sumi[i] = 0.0D;
/*  494: 822 */       for (int j = 0; j < nj; j++)
/*  495:     */       {
/*  496: 823 */         sumi[i] += counts[i][j];
/*  497: 824 */         sum += counts[i][j];
/*  498:     */       }
/*  499:     */     }
/*  500: 829 */     for (int j = 0; j < nj; j++)
/*  501:     */     {
/*  502: 830 */       sumj[j] = 0.0D;
/*  503: 832 */       for (i = 0; i < ni; i++) {
/*  504: 833 */         sumj[j] += counts[i][j];
/*  505:     */       }
/*  506:     */     }
/*  507: 838 */     if ((!this.m_missingSeparate) && (sumi[(ni - 1)] < this.m_numInstances) && (sumj[(nj - 1)] < this.m_numInstances))
/*  508:     */     {
/*  509: 840 */       double[] i_copy = new double[sumi.length];
/*  510: 841 */       double[] j_copy = new double[sumj.length];
/*  511: 842 */       double[][] counts_copy = new double[sumi.length][sumj.length];
/*  512: 844 */       for (i = 0; i < ni; i++) {
/*  513: 845 */         System.arraycopy(counts[i], 0, counts_copy[i], 0, sumj.length);
/*  514:     */       }
/*  515: 848 */       System.arraycopy(sumi, 0, i_copy, 0, sumi.length);
/*  516: 849 */       System.arraycopy(sumj, 0, j_copy, 0, sumj.length);
/*  517: 850 */       double total_missing = sumi[(ni - 1)] + sumj[(nj - 1)] - counts[(ni - 1)][(nj - 1)];
/*  518: 854 */       if (sumi[(ni - 1)] > 0.0D) {
/*  519: 855 */         for (j = 0; j < nj - 1; j++) {
/*  520: 856 */           if (counts[(ni - 1)][j] > 0.0D)
/*  521:     */           {
/*  522: 857 */             for (i = 0; i < ni - 1; i++)
/*  523:     */             {
/*  524: 858 */               temp = i_copy[i] / (sum - i_copy[(ni - 1)]) * counts[(ni - 1)][j];
/*  525: 859 */               counts[i][j] += temp;
/*  526: 860 */               sumi[i] += temp;
/*  527:     */             }
/*  528: 863 */             counts[(ni - 1)][j] = 0.0D;
/*  529:     */           }
/*  530:     */         }
/*  531:     */       }
/*  532: 868 */       sumi[(ni - 1)] = 0.0D;
/*  533: 871 */       if (sumj[(nj - 1)] > 0.0D) {
/*  534: 872 */         for (i = 0; i < ni - 1; i++) {
/*  535: 873 */           if (counts[i][(nj - 1)] > 0.0D)
/*  536:     */           {
/*  537: 874 */             for (j = 0; j < nj - 1; j++)
/*  538:     */             {
/*  539: 875 */               temp = j_copy[j] / (sum - j_copy[(nj - 1)]) * counts[i][(nj - 1)];
/*  540: 876 */               counts[i][j] += temp;
/*  541: 877 */               sumj[j] += temp;
/*  542:     */             }
/*  543: 880 */             counts[i][(nj - 1)] = 0.0D;
/*  544:     */           }
/*  545:     */         }
/*  546:     */       }
/*  547: 885 */       sumj[(nj - 1)] = 0.0D;
/*  548: 888 */       if ((counts[(ni - 1)][(nj - 1)] > 0.0D) && (total_missing != sum))
/*  549:     */       {
/*  550: 889 */         for (i = 0; i < ni - 1; i++) {
/*  551: 890 */           for (j = 0; j < nj - 1; j++)
/*  552:     */           {
/*  553: 891 */             temp = counts_copy[i][j] / (sum - total_missing) * counts_copy[(ni - 1)][(nj - 1)];
/*  554:     */             
/*  555:     */ 
/*  556:     */ 
/*  557: 895 */             counts[i][j] += temp;
/*  558: 896 */             sumi[i] += temp;
/*  559: 897 */             sumj[j] += temp;
/*  560:     */           }
/*  561:     */         }
/*  562: 901 */         counts[(ni - 1)][(nj - 1)] = 0.0D;
/*  563:     */       }
/*  564:     */     }
/*  565: 905 */     double corr_measure = ContingencyTables.symmetricalUncertainty(counts);
/*  566: 907 */     if (Utils.eq(corr_measure, 0.0D))
/*  567:     */     {
/*  568: 908 */       if (flag == true) {
/*  569: 909 */         return 0.0D;
/*  570:     */       }
/*  571: 911 */       return 1.0D;
/*  572:     */     }
/*  573: 914 */     return corr_measure;
/*  574:     */   }
/*  575:     */   
/*  576:     */   private double num_num(int att1, int att2)
/*  577:     */   {
/*  578: 921 */     double num = 0.0D;double sx = 0.0D;double sy = 0.0D;
/*  579: 922 */     double mx = this.m_trainInstances.meanOrMode(this.m_trainInstances.attribute(att1));
/*  580: 923 */     double my = this.m_trainInstances.meanOrMode(this.m_trainInstances.attribute(att2));
/*  581: 925 */     for (int i = 0; i < this.m_numInstances; i++)
/*  582:     */     {
/*  583: 926 */       Instance inst = this.m_trainInstances.instance(i);
/*  584: 927 */       double diff1 = inst.isMissing(att1) ? 0.0D : inst.value(att1) - mx;
/*  585: 928 */       double diff2 = inst.isMissing(att2) ? 0.0D : inst.value(att2) - my;
/*  586: 929 */       num += diff1 * diff2;
/*  587: 930 */       sx += diff1 * diff1;
/*  588: 931 */       sy += diff2 * diff2;
/*  589:     */     }
/*  590: 934 */     if ((sx != 0.0D) && 
/*  591: 935 */       (this.m_std_devs[att1] == 1.0D)) {
/*  592: 936 */       this.m_std_devs[att1] = Math.sqrt(sx / this.m_numInstances);
/*  593:     */     }
/*  594: 940 */     if ((sy != 0.0D) && 
/*  595: 941 */       (this.m_std_devs[att2] == 1.0D)) {
/*  596: 942 */       this.m_std_devs[att2] = Math.sqrt(sy / this.m_numInstances);
/*  597:     */     }
/*  598: 946 */     if (sx * sy > 0.0D)
/*  599:     */     {
/*  600: 947 */       double r = num / Math.sqrt(sx * sy);
/*  601: 948 */       return r < 0.0D ? -r : r;
/*  602:     */     }
/*  603: 950 */     if ((att1 != this.m_classIndex) && (att2 != this.m_classIndex)) {
/*  604: 951 */       return 1.0D;
/*  605:     */     }
/*  606: 953 */     return 0.0D;
/*  607:     */   }
/*  608:     */   
/*  609:     */   private double num_nom2(int att1, int att2)
/*  610:     */   {
/*  611: 962 */     int mx = (int)this.m_trainInstances.meanOrMode(this.m_trainInstances.attribute(att1));
/*  612:     */     
/*  613: 964 */     double my = this.m_trainInstances.meanOrMode(this.m_trainInstances.attribute(att2));
/*  614: 965 */     double stdv_num = 0.0D;
/*  615:     */     
/*  616: 967 */     double r = 0.0D;
/*  617: 968 */     int nx = !this.m_missingSeparate ? this.m_trainInstances.attribute(att1).numValues() : this.m_trainInstances.attribute(att1).numValues() + 1;
/*  618:     */     
/*  619:     */ 
/*  620:     */ 
/*  621: 972 */     double[] prior_nom = new double[nx];
/*  622: 973 */     double[] stdvs_nom = new double[nx];
/*  623: 974 */     double[] covs = new double[nx];
/*  624: 976 */     for (int i = 0; i < nx; i++)
/*  625:     */     {
/*  626: 977 */       double tmp115_114 = (prior_nom[i] = 0.0D);covs[i] = tmp115_114;stdvs_nom[i] = tmp115_114;
/*  627:     */     }
/*  628: 982 */     for (i = 0; i < this.m_numInstances; i++)
/*  629:     */     {
/*  630: 983 */       Instance inst = this.m_trainInstances.instance(i);
/*  631:     */       int ii;
/*  632:     */       int ii;
/*  633: 985 */       if (inst.isMissing(att1))
/*  634:     */       {
/*  635:     */         int ii;
/*  636: 986 */         if (!this.m_missingSeparate) {
/*  637: 987 */           ii = mx;
/*  638:     */         } else {
/*  639: 989 */           ii = nx - 1;
/*  640:     */         }
/*  641:     */       }
/*  642:     */       else
/*  643:     */       {
/*  644: 992 */         ii = (int)inst.value(att1);
/*  645:     */       }
/*  646: 996 */       prior_nom[ii] += 1.0D;
/*  647:     */     }
/*  648: 999 */     for (int k = 0; k < this.m_numInstances; k++)
/*  649:     */     {
/*  650:1000 */       Instance inst = this.m_trainInstances.instance(k);
/*  651:     */       
/*  652:1002 */       double diff2 = inst.isMissing(att2) ? 0.0D : inst.value(att2) - my;
/*  653:1003 */       stdv_num += diff2 * diff2;
/*  654:1006 */       for (i = 0; i < nx; i++)
/*  655:     */       {
/*  656:     */         double temp;
/*  657:     */         double temp;
/*  658:1007 */         if (inst.isMissing(att1))
/*  659:     */         {
/*  660:     */           double temp;
/*  661:1008 */           if (!this.m_missingSeparate) {
/*  662:1009 */             temp = i == mx ? 1.0D : 0.0D;
/*  663:     */           } else {
/*  664:1011 */             temp = i == nx - 1 ? 1.0D : 0.0D;
/*  665:     */           }
/*  666:     */         }
/*  667:     */         else
/*  668:     */         {
/*  669:1014 */           temp = i == inst.value(att1) ? 1.0D : 0.0D;
/*  670:     */         }
/*  671:1017 */         double diff1 = temp - prior_nom[i] / this.m_numInstances;
/*  672:1018 */         stdvs_nom[i] += diff1 * diff1;
/*  673:1019 */         tmp115_114[i] += diff1 * diff2;
/*  674:     */       }
/*  675:     */     }
/*  676:1024 */     i = 0;
/*  677:1024 */     for (double temp = 0.0D; i < nx; i++)
/*  678:     */     {
/*  679:1026 */       temp += prior_nom[i] / this.m_numInstances * (stdvs_nom[i] / this.m_numInstances);
/*  680:1029 */       if (stdvs_nom[i] * stdv_num > 0.0D)
/*  681:     */       {
/*  682:1031 */         double rr = tmp115_114[i] / Math.sqrt(stdvs_nom[i] * stdv_num);
/*  683:1033 */         if (rr < 0.0D) {
/*  684:1034 */           rr = -rr;
/*  685:     */         }
/*  686:1037 */         r += prior_nom[i] / this.m_numInstances * rr;
/*  687:     */       }
/*  688:1046 */       else if ((att1 != this.m_classIndex) && (att2 != this.m_classIndex))
/*  689:     */       {
/*  690:1047 */         r += prior_nom[i] / this.m_numInstances * 1.0D;
/*  691:     */       }
/*  692:     */     }
/*  693:1054 */     if ((temp != 0.0D) && 
/*  694:1055 */       (this.m_std_devs[att1] == 1.0D)) {
/*  695:1056 */       this.m_std_devs[att1] = Math.sqrt(temp);
/*  696:     */     }
/*  697:1060 */     if ((stdv_num != 0.0D) && 
/*  698:1061 */       (this.m_std_devs[att2] == 1.0D)) {
/*  699:1062 */       this.m_std_devs[att2] = Math.sqrt(stdv_num / this.m_numInstances);
/*  700:     */     }
/*  701:1066 */     if ((r == 0.0D) && 
/*  702:1067 */       (att1 != this.m_classIndex) && (att2 != this.m_classIndex)) {
/*  703:1068 */       r = 1.0D;
/*  704:     */     }
/*  705:1072 */     return r;
/*  706:     */   }
/*  707:     */   
/*  708:     */   private double nom_nom(int att1, int att2)
/*  709:     */   {
/*  710:1079 */     int mx = (int)this.m_trainInstances.meanOrMode(this.m_trainInstances.attribute(att1));
/*  711:     */     
/*  712:1081 */     int my = (int)this.m_trainInstances.meanOrMode(this.m_trainInstances.attribute(att2));
/*  713:     */     
/*  714:     */ 
/*  715:1084 */     double r = 0.0D;
/*  716:1085 */     int nx = !this.m_missingSeparate ? this.m_trainInstances.attribute(att1).numValues() : this.m_trainInstances.attribute(att1).numValues() + 1;
/*  717:     */     
/*  718:     */ 
/*  719:     */ 
/*  720:1089 */     int ny = !this.m_missingSeparate ? this.m_trainInstances.attribute(att2).numValues() : this.m_trainInstances.attribute(att2).numValues() + 1;
/*  721:     */     
/*  722:     */ 
/*  723:     */ 
/*  724:1093 */     double[][] prior_nom = new double[nx][ny];
/*  725:1094 */     double[] sumx = new double[nx];
/*  726:1095 */     double[] sumy = new double[ny];
/*  727:1096 */     double[] stdvsx = new double[nx];
/*  728:1097 */     double[] stdvsy = new double[ny];
/*  729:1098 */     double[][] covs = new double[nx][ny];
/*  730:1100 */     for (int i = 0; i < nx; i++)
/*  731:     */     {
/*  732:1101 */       double tmp170_169 = 0.0D;stdvsx[i] = tmp170_169;sumx[i] = tmp170_169;
/*  733:     */     }
/*  734:1104 */     for (int j = 0; j < ny; j++)
/*  735:     */     {
/*  736:1105 */       double tmp198_197 = 0.0D;stdvsy[j] = tmp198_197;sumy[j] = tmp198_197;
/*  737:     */     }
/*  738:1108 */     for (i = 0; i < nx; i++) {
/*  739:1109 */       for (j = 0; j < ny; j++)
/*  740:     */       {
/*  741:1110 */         double tmp238_237 = 0.0D;prior_nom[i][j] = tmp238_237;covs[i][j] = tmp238_237;
/*  742:     */       }
/*  743:     */     }
/*  744:1116 */     for (i = 0; i < this.m_numInstances; i++)
/*  745:     */     {
/*  746:1117 */       Instance inst = this.m_trainInstances.instance(i);
/*  747:     */       int ii;
/*  748:     */       int ii;
/*  749:1119 */       if (inst.isMissing(att1))
/*  750:     */       {
/*  751:     */         int ii;
/*  752:1120 */         if (!this.m_missingSeparate) {
/*  753:1121 */           ii = mx;
/*  754:     */         } else {
/*  755:1123 */           ii = nx - 1;
/*  756:     */         }
/*  757:     */       }
/*  758:     */       else
/*  759:     */       {
/*  760:1126 */         ii = (int)inst.value(att1);
/*  761:     */       }
/*  762:     */       int jj;
/*  763:     */       int jj;
/*  764:1129 */       if (inst.isMissing(att2))
/*  765:     */       {
/*  766:     */         int jj;
/*  767:1130 */         if (!this.m_missingSeparate) {
/*  768:1131 */           jj = my;
/*  769:     */         } else {
/*  770:1133 */           jj = ny - 1;
/*  771:     */         }
/*  772:     */       }
/*  773:     */       else
/*  774:     */       {
/*  775:1136 */         jj = (int)inst.value(att2);
/*  776:     */       }
/*  777:1140 */       prior_nom[ii][jj] += 1.0D;
/*  778:1141 */       sumx[ii] += 1.0D;
/*  779:1142 */       sumy[jj] += 1.0D;
/*  780:     */     }
/*  781:1145 */     for (int z = 0; z < this.m_numInstances; z++)
/*  782:     */     {
/*  783:1146 */       Instance inst = this.m_trainInstances.instance(z);
/*  784:1148 */       for (j = 0; j < ny; j++)
/*  785:     */       {
/*  786:     */         double temp2;
/*  787:     */         double temp2;
/*  788:1149 */         if (inst.isMissing(att2))
/*  789:     */         {
/*  790:     */           double temp2;
/*  791:1150 */           if (!this.m_missingSeparate) {
/*  792:1151 */             temp2 = j == my ? 1.0D : 0.0D;
/*  793:     */           } else {
/*  794:1153 */             temp2 = j == ny - 1 ? 1.0D : 0.0D;
/*  795:     */           }
/*  796:     */         }
/*  797:     */         else
/*  798:     */         {
/*  799:1156 */           temp2 = j == inst.value(att2) ? 1.0D : 0.0D;
/*  800:     */         }
/*  801:1159 */         double diff2 = temp2 - sumy[j] / this.m_numInstances;
/*  802:1160 */         stdvsy[j] += diff2 * diff2;
/*  803:     */       }
/*  804:1164 */       for (i = 0; i < nx; i++)
/*  805:     */       {
/*  806:     */         double temp1;
/*  807:     */         double temp1;
/*  808:1165 */         if (inst.isMissing(att1))
/*  809:     */         {
/*  810:     */           double temp1;
/*  811:1166 */           if (!this.m_missingSeparate) {
/*  812:1167 */             temp1 = i == mx ? 1.0D : 0.0D;
/*  813:     */           } else {
/*  814:1169 */             temp1 = i == nx - 1 ? 1.0D : 0.0D;
/*  815:     */           }
/*  816:     */         }
/*  817:     */         else
/*  818:     */         {
/*  819:1172 */           temp1 = i == inst.value(att1) ? 1.0D : 0.0D;
/*  820:     */         }
/*  821:1175 */         double diff1 = temp1 - sumx[i] / this.m_numInstances;
/*  822:1176 */         stdvsx[i] += diff1 * diff1;
/*  823:1178 */         for (j = 0; j < ny; j++)
/*  824:     */         {
/*  825:     */           double temp2;
/*  826:     */           double temp2;
/*  827:1179 */           if (inst.isMissing(att2))
/*  828:     */           {
/*  829:     */             double temp2;
/*  830:1180 */             if (!this.m_missingSeparate) {
/*  831:1181 */               temp2 = j == my ? 1.0D : 0.0D;
/*  832:     */             } else {
/*  833:1183 */               temp2 = j == ny - 1 ? 1.0D : 0.0D;
/*  834:     */             }
/*  835:     */           }
/*  836:     */           else
/*  837:     */           {
/*  838:1186 */             temp2 = j == inst.value(att2) ? 1.0D : 0.0D;
/*  839:     */           }
/*  840:1189 */           double diff2 = temp2 - sumy[j] / this.m_numInstances;
/*  841:1190 */           covs[i][j] += diff1 * diff2;
/*  842:     */         }
/*  843:     */       }
/*  844:     */     }
/*  845:1196 */     for (i = 0; i < nx; i++) {
/*  846:1197 */       for (j = 0; j < ny; j++) {
/*  847:1198 */         if (stdvsx[i] * stdvsy[j] > 0.0D)
/*  848:     */         {
/*  849:1200 */           double rr = covs[i][j] / Math.sqrt(stdvsx[i] * stdvsy[j]);
/*  850:1202 */           if (rr < 0.0D) {
/*  851:1203 */             rr = -rr;
/*  852:     */           }
/*  853:1206 */           r += prior_nom[i][j] / this.m_numInstances * rr;
/*  854:     */         }
/*  855:1213 */         else if ((att1 != this.m_classIndex) && (att2 != this.m_classIndex))
/*  856:     */         {
/*  857:1214 */           r += prior_nom[i][j] / this.m_numInstances * 1.0D;
/*  858:     */         }
/*  859:     */       }
/*  860:     */     }
/*  861:1222 */     i = 0;
/*  862:1222 */     for (double temp1 = 0.0D; i < nx; i++) {
/*  863:1223 */       temp1 += sumx[i] / this.m_numInstances * (stdvsx[i] / this.m_numInstances);
/*  864:     */     }
/*  865:1226 */     if ((temp1 != 0.0D) && 
/*  866:1227 */       (this.m_std_devs[att1] == 1.0D)) {
/*  867:1228 */       this.m_std_devs[att1] = Math.sqrt(temp1);
/*  868:     */     }
/*  869:1232 */     j = 0;
/*  870:1232 */     for (double temp2 = 0.0D; j < ny; j++) {
/*  871:1233 */       temp2 += sumy[j] / this.m_numInstances * (stdvsy[j] / this.m_numInstances);
/*  872:     */     }
/*  873:1236 */     if ((temp2 != 0.0D) && 
/*  874:1237 */       (this.m_std_devs[att2] == 1.0D)) {
/*  875:1238 */       this.m_std_devs[att2] = Math.sqrt(temp2);
/*  876:     */     }
/*  877:1242 */     if ((r == 0.0D) && 
/*  878:1243 */       (att1 != this.m_classIndex) && (att2 != this.m_classIndex)) {
/*  879:1244 */       r = 1.0D;
/*  880:     */     }
/*  881:1248 */     return r;
/*  882:     */   }
/*  883:     */   
/*  884:     */   public String toString()
/*  885:     */   {
/*  886:1258 */     StringBuffer text = new StringBuffer();
/*  887:1260 */     if (this.m_trainInstances == null)
/*  888:     */     {
/*  889:1261 */       text.append("CFS subset evaluator has not been built yet\n");
/*  890:     */     }
/*  891:     */     else
/*  892:     */     {
/*  893:1263 */       text.append("\tCFS Subset Evaluator\n");
/*  894:1265 */       if (this.m_missingSeparate) {
/*  895:1266 */         text.append("\tTreating missing values as a separate value\n");
/*  896:     */       }
/*  897:1269 */       if (this.m_locallyPredictive) {
/*  898:1270 */         text.append("\tIncluding locally predictive attributes\n");
/*  899:     */       }
/*  900:     */     }
/*  901:1274 */     return text.toString();
/*  902:     */   }
/*  903:     */   
/*  904:     */   private void addLocallyPredictive(BitSet best_group)
/*  905:     */   {
/*  906:1279 */     boolean done = false;
/*  907:1280 */     boolean ok = true;
/*  908:1281 */     double temp_best = -1.0D;
/*  909:     */     
/*  910:1283 */     int j = 0;
/*  911:1284 */     BitSet temp_group = (BitSet)best_group.clone();
/*  912:1287 */     while (!done)
/*  913:     */     {
/*  914:1288 */       temp_best = -1.0D;
/*  915:1291 */       for (int i = 0; i < this.m_numAttribs; i++)
/*  916:     */       {
/*  917:     */         int smaller;
/*  918:     */         int smaller;
/*  919:     */         int larger;
/*  920:1292 */         if (i > this.m_classIndex)
/*  921:     */         {
/*  922:1293 */           int larger = i;
/*  923:1294 */           smaller = this.m_classIndex;
/*  924:     */         }
/*  925:     */         else
/*  926:     */         {
/*  927:1296 */           smaller = i;
/*  928:1297 */           larger = this.m_classIndex;
/*  929:     */         }
/*  930:1303 */         if ((!temp_group.get(i)) && (i != this.m_classIndex))
/*  931:     */         {
/*  932:1304 */           if (this.m_corr_matrix[larger][smaller] == -999.0F)
/*  933:     */           {
/*  934:1305 */             float corr = correlate(i, this.m_classIndex);
/*  935:1306 */             this.m_corr_matrix[larger][smaller] = corr;
/*  936:     */           }
/*  937:1309 */           if (this.m_corr_matrix[larger][smaller] > temp_best)
/*  938:     */           {
/*  939:1310 */             temp_best = this.m_corr_matrix[larger][smaller];
/*  940:1311 */             j = i;
/*  941:     */           }
/*  942:     */         }
/*  943:     */       }
/*  944:1316 */       if (temp_best == -1.0D)
/*  945:     */       {
/*  946:1317 */         done = true;
/*  947:     */       }
/*  948:     */       else
/*  949:     */       {
/*  950:1319 */         ok = true;
/*  951:1320 */         temp_group.set(j);
/*  952:1324 */         for (i = 0; i < this.m_numAttribs; i++)
/*  953:     */         {
/*  954:     */           int smaller;
/*  955:     */           int larger;
/*  956:     */           int smaller;
/*  957:1325 */           if (i > j)
/*  958:     */           {
/*  959:1326 */             int larger = i;
/*  960:1327 */             smaller = j;
/*  961:     */           }
/*  962:     */           else
/*  963:     */           {
/*  964:1329 */             larger = j;
/*  965:1330 */             smaller = i;
/*  966:     */           }
/*  967:1335 */           if (best_group.get(i))
/*  968:     */           {
/*  969:1336 */             if (this.m_corr_matrix[larger][smaller] == -999.0F)
/*  970:     */             {
/*  971:1337 */               float corr = correlate(i, j);
/*  972:1338 */               this.m_corr_matrix[larger][smaller] = corr;
/*  973:     */             }
/*  974:1341 */             if (this.m_corr_matrix[larger][smaller] > temp_best - this.m_c_Threshold)
/*  975:     */             {
/*  976:1342 */               ok = false;
/*  977:1343 */               break;
/*  978:     */             }
/*  979:     */           }
/*  980:     */         }
/*  981:1349 */         if (ok) {
/*  982:1350 */           best_group.set(j);
/*  983:     */         }
/*  984:     */       }
/*  985:     */     }
/*  986:     */   }
/*  987:     */   
/*  988:     */   public int[] postProcess(int[] attributeSet)
/*  989:     */     throws Exception
/*  990:     */   {
/*  991:1367 */     if (this.m_debug) {
/*  992:1368 */       System.err.println("Percentage of correlation matrix computed over the search: " + Utils.doubleToString(this.m_numFilled.get() / this.m_numEntries * 100.0D, 2) + "%");
/*  993:     */     }
/*  994:1374 */     int j = 0;
/*  995:1376 */     if (!this.m_locallyPredictive) {
/*  996:1377 */       return attributeSet;
/*  997:     */     }
/*  998:1380 */     BitSet bestGroup = new BitSet(this.m_numAttribs);
/*  999:1382 */     for (int element : attributeSet) {
/* 1000:1383 */       bestGroup.set(element);
/* 1001:     */     }
/* 1002:1386 */     addLocallyPredictive(bestGroup);
/* 1003:1389 */     for (int i = 0; i < this.m_numAttribs; i++) {
/* 1004:1390 */       if (bestGroup.get(i)) {
/* 1005:1391 */         j++;
/* 1006:     */       }
/* 1007:     */     }
/* 1008:1395 */     int[] newSet = new int[j];
/* 1009:1396 */     j = 0;
/* 1010:1398 */     for (int i = 0; i < this.m_numAttribs; i++) {
/* 1011:1399 */       if (bestGroup.get(i)) {
/* 1012:1400 */         newSet[(j++)] = i;
/* 1013:     */       }
/* 1014:     */     }
/* 1015:1404 */     return newSet;
/* 1016:     */   }
/* 1017:     */   
/* 1018:     */   public void clean()
/* 1019:     */   {
/* 1020:1409 */     if (this.m_trainInstances != null) {
/* 1021:1411 */       this.m_trainInstances = new Instances(this.m_trainInstances, 0);
/* 1022:     */     }
/* 1023:     */   }
/* 1024:     */   
/* 1025:     */   protected void resetOptions()
/* 1026:     */   {
/* 1027:1416 */     this.m_trainInstances = null;
/* 1028:1417 */     this.m_missingSeparate = false;
/* 1029:1418 */     this.m_locallyPredictive = true;
/* 1030:1419 */     this.m_c_Threshold = 0.0D;
/* 1031:     */   }
/* 1032:     */   
/* 1033:     */   public String getRevision()
/* 1034:     */   {
/* 1035:1429 */     return RevisionUtils.extract("$Revision: 11852 $");
/* 1036:     */   }
/* 1037:     */   
/* 1038:     */   public static void main(String[] args)
/* 1039:     */   {
/* 1040:1438 */     runEvaluator(new CfsSubsetEval(), args);
/* 1041:     */   }
/* 1042:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.CfsSubsetEval
 * JD-Core Version:    0.7.0.1
 */