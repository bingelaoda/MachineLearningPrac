/*    1:     */ package weka.clusterers;
/*    2:     */ 
/*    3:     */ import java.io.BufferedReader;
/*    4:     */ import java.io.File;
/*    5:     */ import java.io.FileOutputStream;
/*    6:     */ import java.io.FileReader;
/*    7:     */ import java.io.PrintStream;
/*    8:     */ import java.io.PrintWriter;
/*    9:     */ import java.io.Reader;
/*   10:     */ import java.util.Collections;
/*   11:     */ import java.util.Enumeration;
/*   12:     */ import java.util.Random;
/*   13:     */ import java.util.Vector;
/*   14:     */ import weka.core.AlgVector;
/*   15:     */ import weka.core.Attribute;
/*   16:     */ import weka.core.Capabilities;
/*   17:     */ import weka.core.Capabilities.Capability;
/*   18:     */ import weka.core.DistanceFunction;
/*   19:     */ import weka.core.EuclideanDistance;
/*   20:     */ import weka.core.Instance;
/*   21:     */ import weka.core.Instances;
/*   22:     */ import weka.core.Option;
/*   23:     */ import weka.core.OptionHandler;
/*   24:     */ import weka.core.RevisionUtils;
/*   25:     */ import weka.core.TechnicalInformation;
/*   26:     */ import weka.core.TechnicalInformation.Field;
/*   27:     */ import weka.core.TechnicalInformation.Type;
/*   28:     */ import weka.core.TechnicalInformationHandler;
/*   29:     */ import weka.core.Utils;
/*   30:     */ import weka.core.neighboursearch.KDTree;
/*   31:     */ import weka.filters.Filter;
/*   32:     */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*   33:     */ 
/*   34:     */ public class XMeans
/*   35:     */   extends RandomizableClusterer
/*   36:     */   implements TechnicalInformationHandler
/*   37:     */ {
/*   38:     */   private static final long serialVersionUID = -7941793078404132616L;
/*   39: 202 */   protected Instances m_Instances = null;
/*   40: 205 */   protected Instances m_Model = null;
/*   41:     */   protected ReplaceMissingValues m_ReplaceMissingFilter;
/*   42: 214 */   protected double m_BinValue = 1.0D;
/*   43: 217 */   protected double m_Bic = 4.9E-324D;
/*   44: 220 */   protected double[] m_Mle = null;
/*   45: 223 */   protected int m_MaxIterations = 1;
/*   46: 229 */   protected int m_MaxKMeans = 1000;
/*   47: 234 */   protected int m_MaxKMeansForChildren = 1000;
/*   48: 237 */   protected int m_NumClusters = 2;
/*   49: 240 */   protected int m_MinNumClusters = 2;
/*   50: 243 */   protected int m_MaxNumClusters = 4;
/*   51: 246 */   protected DistanceFunction m_DistanceF = new EuclideanDistance();
/*   52:     */   protected Instances m_ClusterCenters;
/*   53: 252 */   protected File m_InputCenterFile = new File(System.getProperty("user.dir"));
/*   54: 256 */   protected Reader m_DebugVectorsInput = null;
/*   55: 258 */   protected int m_DebugVectorsIndex = 0;
/*   56: 260 */   protected Instances m_DebugVectors = null;
/*   57: 263 */   protected File m_DebugVectorsFile = new File(System.getProperty("user.dir"));
/*   58: 266 */   protected transient Reader m_CenterInput = null;
/*   59: 269 */   protected File m_OutputCenterFile = new File(System.getProperty("user.dir"));
/*   60: 272 */   protected transient PrintWriter m_CenterOutput = null;
/*   61:     */   protected int[] m_ClusterAssignments;
/*   62: 283 */   protected double m_CutOffFactor = 0.5D;
/*   63: 286 */   public static int R_LOW = 0;
/*   64: 288 */   public static int R_HIGH = 1;
/*   65: 290 */   public static int R_WIDTH = 2;
/*   66: 295 */   protected KDTree m_KDTree = new KDTree();
/*   67: 301 */   protected boolean m_UseKDTree = false;
/*   68: 304 */   protected int m_IterationCount = 0;
/*   69: 307 */   protected int m_KMeansStopped = 0;
/*   70: 310 */   protected int m_NumSplits = 0;
/*   71: 313 */   protected int m_NumSplitsDone = 0;
/*   72: 316 */   protected int m_NumSplitsStillDone = 0;
/*   73: 321 */   protected int m_DebugLevel = 0;
/*   74: 324 */   public static int D_PRINTCENTERS = 1;
/*   75: 326 */   public static int D_FOLLOWSPLIT = 2;
/*   76: 328 */   public static int D_CONVCHCLOSER = 3;
/*   77: 330 */   public static int D_RANDOMVECTOR = 4;
/*   78: 332 */   public static int D_KDTREE = 5;
/*   79: 334 */   public static int D_ITERCOUNT = 6;
/*   80: 336 */   public static int D_METH_MISUSE = 80;
/*   81: 338 */   public static int D_CURR = 88;
/*   82: 340 */   public static int D_GENERAL = 99;
/*   83: 343 */   public boolean m_CurrDebugFlag = true;
/*   84:     */   
/*   85:     */   public XMeans()
/*   86:     */   {
/*   87: 351 */     this.m_SeedDefault = 10;
/*   88: 352 */     setSeed(this.m_SeedDefault);
/*   89:     */   }
/*   90:     */   
/*   91:     */   public String globalInfo()
/*   92:     */   {
/*   93: 362 */     return "Cluster data using the X-means algorithm.\n\nX-Means is K-Means extended by an Improve-Structure part In this part of the algorithm the centers are attempted to be split in its region. The decision between the children of each center and itself is done comparing the BIC-values of the two structures.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*   94:     */   }
/*   95:     */   
/*   96:     */   public TechnicalInformation getTechnicalInformation()
/*   97:     */   {
/*   98: 381 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*   99: 382 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Dan Pelleg and Andrew W. Moore");
/*  100: 383 */     result.setValue(TechnicalInformation.Field.TITLE, "X-means: Extending K-means with Efficient Estimation of the Number of Clusters");
/*  101:     */     
/*  102:     */ 
/*  103:     */ 
/*  104: 387 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Seventeenth International Conference on Machine Learning");
/*  105:     */     
/*  106: 389 */     result.setValue(TechnicalInformation.Field.YEAR, "2000");
/*  107: 390 */     result.setValue(TechnicalInformation.Field.PAGES, "727-734");
/*  108: 391 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Morgan Kaufmann");
/*  109:     */     
/*  110: 393 */     return result;
/*  111:     */   }
/*  112:     */   
/*  113:     */   public Capabilities getCapabilities()
/*  114:     */   {
/*  115: 403 */     Capabilities result = super.getCapabilities();
/*  116: 404 */     result.disableAll();
/*  117: 405 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  118:     */     
/*  119:     */ 
/*  120: 408 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  121: 409 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  122: 410 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  123:     */     
/*  124: 412 */     return result;
/*  125:     */   }
/*  126:     */   
/*  127:     */   public void buildClusterer(Instances data)
/*  128:     */     throws Exception
/*  129:     */   {
/*  130: 425 */     getCapabilities().testWithFail(data);
/*  131: 427 */     if (this.m_MinNumClusters > this.m_MaxNumClusters) {
/*  132: 428 */       throw new Exception("XMeans: min number of clusters can't be greater than max number of clusters!");
/*  133:     */     }
/*  134: 432 */     this.m_NumSplits = 0;
/*  135: 433 */     this.m_NumSplitsDone = 0;
/*  136: 434 */     this.m_NumSplitsStillDone = 0;
/*  137:     */     
/*  138:     */ 
/*  139: 437 */     this.m_ReplaceMissingFilter = new ReplaceMissingValues();
/*  140: 438 */     this.m_ReplaceMissingFilter.setInputFormat(data);
/*  141: 439 */     this.m_Instances = Filter.useFilter(data, this.m_ReplaceMissingFilter);
/*  142:     */     
/*  143:     */ 
/*  144: 442 */     Random random0 = new Random(this.m_Seed);
/*  145:     */     
/*  146:     */ 
/*  147: 445 */     this.m_NumClusters = this.m_MinNumClusters;
/*  148: 448 */     if (this.m_DistanceF == null) {
/*  149: 449 */       this.m_DistanceF = new EuclideanDistance();
/*  150:     */     }
/*  151: 452 */     this.m_DistanceF.setInstances(this.m_Instances);
/*  152: 453 */     checkInstances();
/*  153: 455 */     if ((this.m_DebugVectorsFile.exists()) && (this.m_DebugVectorsFile.isFile())) {
/*  154: 456 */       initDebugVectorsInput();
/*  155:     */     }
/*  156: 460 */     int[] allInstList = new int[this.m_Instances.numInstances()];
/*  157: 461 */     for (int i = 0; i < this.m_Instances.numInstances(); i++) {
/*  158: 462 */       allInstList[i] = i;
/*  159:     */     }
/*  160: 466 */     this.m_Model = new Instances(this.m_Instances, 0);
/*  161: 469 */     if (this.m_CenterInput != null)
/*  162:     */     {
/*  163: 471 */       this.m_ClusterCenters = new Instances(this.m_CenterInput);
/*  164: 472 */       this.m_NumClusters = this.m_ClusterCenters.numInstances();
/*  165:     */     }
/*  166:     */     else
/*  167:     */     {
/*  168: 475 */       this.m_ClusterCenters = makeCentersRandomly(random0, this.m_Instances, this.m_NumClusters);
/*  169:     */     }
/*  170: 478 */     PFD(D_FOLLOWSPLIT, "\n*** Starting centers ");
/*  171: 479 */     for (int k = 0; k < this.m_ClusterCenters.numInstances(); k++) {
/*  172: 480 */       PFD(D_FOLLOWSPLIT, "Center " + k + ": " + this.m_ClusterCenters.instance(k));
/*  173:     */     }
/*  174: 483 */     PrCentersFD(D_PRINTCENTERS);
/*  175:     */     
/*  176: 485 */     boolean finished = false;
/*  177: 489 */     if (this.m_UseKDTree) {
/*  178: 490 */       this.m_KDTree.setInstances(this.m_Instances);
/*  179:     */     }
/*  180: 494 */     this.m_IterationCount = 0;
/*  181: 503 */     while ((!finished) && (!stopIteration(this.m_IterationCount, this.m_MaxIterations)))
/*  182:     */     {
/*  183: 510 */       PFD(D_FOLLOWSPLIT, "\nBeginning of main loop - centers:");
/*  184: 511 */       PrCentersFD(D_FOLLOWSPLIT);
/*  185:     */       
/*  186: 513 */       PFD(D_ITERCOUNT, "\n*** 1. Improve-Params " + this.m_IterationCount + ". time");
/*  187: 514 */       this.m_IterationCount += 1;
/*  188:     */       
/*  189:     */ 
/*  190: 517 */       boolean converged = false;
/*  191:     */       
/*  192:     */ 
/*  193: 520 */       this.m_ClusterAssignments = initAssignments(this.m_Instances.numInstances());
/*  194:     */       
/*  195: 522 */       int[][] instOfCent = new int[this.m_ClusterCenters.numInstances()][];
/*  196:     */       
/*  197:     */ 
/*  198: 525 */       int kMeansIteration = 0;
/*  199:     */       
/*  200:     */ 
/*  201: 528 */       PFD(D_FOLLOWSPLIT, "\nConverge in K-Means:");
/*  202: 529 */       while ((!converged) && (!stopKMeansIteration(kMeansIteration, this.m_MaxKMeans)))
/*  203:     */       {
/*  204: 531 */         kMeansIteration++;
/*  205: 532 */         converged = true;
/*  206:     */         
/*  207:     */ 
/*  208: 535 */         converged = assignToCenters(this.m_UseKDTree ? this.m_KDTree : null, this.m_ClusterCenters, instOfCent, allInstList, this.m_ClusterAssignments, kMeansIteration);
/*  209:     */         
/*  210:     */ 
/*  211:     */ 
/*  212: 539 */         PFD(D_FOLLOWSPLIT, "\nMain loop - Assign - centers:");
/*  213: 540 */         PrCentersFD(D_FOLLOWSPLIT);
/*  214:     */         
/*  215: 542 */         converged = recomputeCenters(this.m_ClusterCenters, instOfCent, this.m_Model);
/*  216:     */         
/*  217:     */ 
/*  218: 545 */         PFD(D_FOLLOWSPLIT, "\nMain loop - Recompute - centers:");
/*  219: 546 */         PrCentersFD(D_FOLLOWSPLIT);
/*  220:     */       }
/*  221: 548 */       PFD(D_FOLLOWSPLIT, "");
/*  222: 549 */       PFD(D_FOLLOWSPLIT, "End of Part: 1. Improve-Params - conventional K-means");
/*  223:     */       
/*  224:     */ 
/*  225:     */ 
/*  226:     */ 
/*  227:     */ 
/*  228:     */ 
/*  229:     */ 
/*  230:     */ 
/*  231: 558 */       this.m_Mle = distortion(instOfCent, this.m_ClusterCenters);
/*  232: 559 */       this.m_Bic = calculateBIC(instOfCent, this.m_ClusterCenters, this.m_Mle);
/*  233: 560 */       PFD(D_FOLLOWSPLIT, "m_Bic " + this.m_Bic);
/*  234:     */       
/*  235: 562 */       int currNumCent = this.m_ClusterCenters.numInstances();
/*  236: 563 */       Instances splitCenters = new Instances(this.m_ClusterCenters, currNumCent * 2);
/*  237:     */       
/*  238:     */ 
/*  239: 566 */       double[] pbic = new double[currNumCent];
/*  240: 567 */       double[] cbic = new double[currNumCent];
/*  241: 570 */       for (int i = 0; i < currNumCent; i++)
/*  242:     */       {
/*  243: 575 */         PFD(D_FOLLOWSPLIT, "\nsplit center " + i + " " + this.m_ClusterCenters.instance(i));
/*  244:     */         
/*  245: 577 */         Instance currCenter = this.m_ClusterCenters.instance(i);
/*  246: 578 */         int[] currInstList = instOfCent[i];
/*  247: 579 */         int currNumInst = instOfCent[i].length;
/*  248: 582 */         if (currNumInst <= 2)
/*  249:     */         {
/*  250: 583 */           pbic[i] = 1.7976931348623157E+308D;
/*  251: 584 */           cbic[i] = 0.0D;
/*  252:     */           
/*  253: 586 */           splitCenters.add(currCenter);
/*  254: 587 */           splitCenters.add(currCenter);
/*  255:     */         }
/*  256:     */         else
/*  257:     */         {
/*  258: 592 */           double variance = this.m_Mle[i] / currNumInst;
/*  259: 593 */           Instances children = splitCenter(random0, currCenter, variance, this.m_Model);
/*  260:     */           
/*  261:     */ 
/*  262: 596 */           int[] oneCentAssignments = initAssignments(currNumInst);
/*  263: 597 */           int[][] instOfChCent = new int[2][];
/*  264:     */           
/*  265:     */ 
/*  266: 600 */           converged = false;
/*  267: 601 */           int kMeansForChildrenIteration = 0;
/*  268: 602 */           PFD(D_FOLLOWSPLIT, "\nConverge, K-Means for children: " + i);
/*  269: 604 */           while ((!converged) && (!stopKMeansIteration(kMeansForChildrenIteration, this.m_MaxKMeansForChildren)))
/*  270:     */           {
/*  271: 606 */             kMeansForChildrenIteration++;
/*  272:     */             
/*  273: 608 */             converged = assignToCenters(children, instOfChCent, currInstList, oneCentAssignments);
/*  274: 611 */             if (!converged) {
/*  275: 612 */               recomputeCentersFast(children, instOfChCent, this.m_Model);
/*  276:     */             }
/*  277:     */           }
/*  278: 617 */           splitCenters.add(children.instance(0));
/*  279: 618 */           splitCenters.add(children.instance(1));
/*  280:     */           
/*  281: 620 */           PFD(D_FOLLOWSPLIT, "\nconverged cildren ");
/*  282: 621 */           PFD(D_FOLLOWSPLIT, " " + children.instance(0));
/*  283: 622 */           PFD(D_FOLLOWSPLIT, " " + children.instance(1));
/*  284:     */           
/*  285:     */ 
/*  286: 625 */           pbic[i] = calculateBIC(currInstList, currCenter, this.m_Mle[i], this.m_Model);
/*  287: 626 */           double[] chMLE = distortion(instOfChCent, children);
/*  288: 627 */           cbic[i] = calculateBIC(instOfChCent, children, chMLE);
/*  289:     */         }
/*  290:     */       }
/*  291: 632 */       Instances newClusterCenters = null;
/*  292: 633 */       newClusterCenters = newCentersAfterSplit(pbic, cbic, this.m_CutOffFactor, splitCenters);
/*  293:     */       
/*  294:     */ 
/*  295:     */ 
/*  296:     */ 
/*  297: 638 */       int newNumClusters = newClusterCenters.numInstances();
/*  298: 639 */       if (newNumClusters != this.m_NumClusters)
/*  299:     */       {
/*  300: 641 */         PFD(D_FOLLOWSPLIT, "Compare with non-split");
/*  301:     */         
/*  302:     */ 
/*  303: 644 */         int[] newClusterAssignments = initAssignments(this.m_Instances.numInstances());
/*  304:     */         
/*  305:     */ 
/*  306:     */ 
/*  307: 648 */         int[][] newInstOfCent = new int[newClusterCenters.numInstances()][];
/*  308:     */         
/*  309:     */ 
/*  310: 651 */         converged = assignToCenters(this.m_UseKDTree ? this.m_KDTree : null, newClusterCenters, newInstOfCent, allInstList, newClusterAssignments, this.m_IterationCount);
/*  311:     */         
/*  312:     */ 
/*  313:     */ 
/*  314: 655 */         double[] newMle = distortion(newInstOfCent, newClusterCenters);
/*  315: 656 */         double newBic = calculateBIC(newInstOfCent, newClusterCenters, newMle);
/*  316: 657 */         PFD(D_FOLLOWSPLIT, "newBic " + newBic);
/*  317: 658 */         if (newBic > this.m_Bic)
/*  318:     */         {
/*  319: 659 */           PFD(D_FOLLOWSPLIT, "*** decide for new clusters");
/*  320: 660 */           this.m_Bic = newBic;
/*  321: 661 */           this.m_ClusterCenters = newClusterCenters;
/*  322: 662 */           this.m_ClusterAssignments = newClusterAssignments;
/*  323:     */         }
/*  324:     */         else
/*  325:     */         {
/*  326: 664 */           PFD(D_FOLLOWSPLIT, "*** keep old clusters");
/*  327:     */         }
/*  328:     */       }
/*  329: 668 */       newNumClusters = this.m_ClusterCenters.numInstances();
/*  330: 671 */       if ((newNumClusters >= this.m_MaxNumClusters) || (newNumClusters == this.m_NumClusters)) {
/*  331: 673 */         finished = true;
/*  332:     */       }
/*  333: 675 */       this.m_NumClusters = newNumClusters;
/*  334:     */     }
/*  335: 678 */     if ((this.m_ClusterCenters.numInstances() > 0) && (this.m_CenterOutput != null))
/*  336:     */     {
/*  337: 679 */       this.m_CenterOutput.println(this.m_ClusterCenters.toString());
/*  338: 680 */       this.m_CenterOutput.close();
/*  339: 681 */       this.m_CenterOutput = null;
/*  340:     */     }
/*  341:     */   }
/*  342:     */   
/*  343:     */   public boolean checkForNominalAttributes(Instances data)
/*  344:     */   {
/*  345: 693 */     int i = 0;
/*  346: 694 */     while (i < data.numAttributes()) {
/*  347: 695 */       if ((i != data.classIndex()) && (data.attribute(i++).isNominal())) {
/*  348: 696 */         return true;
/*  349:     */       }
/*  350:     */     }
/*  351: 699 */     return false;
/*  352:     */   }
/*  353:     */   
/*  354:     */   protected int[] initAssignments(int[] ass)
/*  355:     */   {
/*  356: 709 */     for (int i = 0; i < ass.length; i++) {
/*  357: 710 */       ass[i] = -1;
/*  358:     */     }
/*  359: 712 */     return ass;
/*  360:     */   }
/*  361:     */   
/*  362:     */   protected int[] initAssignments(int numInstances)
/*  363:     */   {
/*  364: 722 */     int[] ass = new int[numInstances];
/*  365: 723 */     for (int i = 0; i < numInstances; i++) {
/*  366: 724 */       ass[i] = -1;
/*  367:     */     }
/*  368: 726 */     return ass;
/*  369:     */   }
/*  370:     */   
/*  371:     */   boolean[] initBoolArray(int len)
/*  372:     */   {
/*  373: 736 */     boolean[] boolArray = new boolean[len];
/*  374: 737 */     for (int i = 0; i < len; i++) {
/*  375: 738 */       boolArray[i] = false;
/*  376:     */     }
/*  377: 740 */     return boolArray;
/*  378:     */   }
/*  379:     */   
/*  380:     */   protected Instances newCentersAfterSplit(double[] pbic, double[] cbic, double cutoffFactor, Instances splitCenters)
/*  381:     */   {
/*  382: 766 */     boolean splitPerCutoff = false;
/*  383: 767 */     boolean takeSomeAway = false;
/*  384: 768 */     boolean[] splitWon = initBoolArray(this.m_ClusterCenters.numInstances());
/*  385: 769 */     int numToSplit = 0;
/*  386: 770 */     Instances newCenters = null;
/*  387: 773 */     for (int i = 0; i < cbic.length; i++) {
/*  388: 774 */       if (cbic[i] > pbic[i])
/*  389:     */       {
/*  390: 776 */         splitWon[i] = true;
/*  391: 777 */         numToSplit++;
/*  392: 778 */         PFD(D_FOLLOWSPLIT, "Center " + i + " decide for children");
/*  393:     */       }
/*  394:     */       else
/*  395:     */       {
/*  396: 781 */         PFD(D_FOLLOWSPLIT, "Center " + i + " decide for parent");
/*  397:     */       }
/*  398:     */     }
/*  399: 786 */     if ((numToSplit == 0) && (cutoffFactor > 0.0D))
/*  400:     */     {
/*  401: 787 */       splitPerCutoff = true;
/*  402:     */       
/*  403:     */ 
/*  404: 790 */       numToSplit = (int)(this.m_ClusterCenters.numInstances() * this.m_CutOffFactor);
/*  405:     */     }
/*  406: 794 */     double[] diff = new double[this.m_NumClusters];
/*  407: 795 */     for (int j = 0; j < diff.length; j++) {
/*  408: 796 */       pbic[j] -= cbic[j];
/*  409:     */     }
/*  410: 798 */     int[] sortOrder = Utils.sort(diff);
/*  411:     */     
/*  412:     */ 
/*  413: 801 */     int possibleToSplit = this.m_MaxNumClusters - this.m_NumClusters;
/*  414: 803 */     if (possibleToSplit > numToSplit) {
/*  415: 805 */       possibleToSplit = numToSplit;
/*  416:     */     } else {
/*  417: 807 */       takeSomeAway = true;
/*  418:     */     }
/*  419: 811 */     if (splitPerCutoff)
/*  420:     */     {
/*  421: 812 */       for (int j = 0; (j < possibleToSplit) && (cbic[sortOrder[j]] > 0.0D); j++) {
/*  422: 813 */         splitWon[sortOrder[j]] = true;
/*  423:     */       }
/*  424: 815 */       this.m_NumSplitsStillDone += possibleToSplit;
/*  425:     */     }
/*  426: 818 */     else if (takeSomeAway)
/*  427:     */     {
/*  428: 819 */       int count = 0;
/*  429: 820 */       for (int j = 0; (j < splitWon.length) && (count < possibleToSplit); j++) {
/*  430: 822 */         if (splitWon[sortOrder[j]] == 1) {
/*  431: 823 */           count++;
/*  432:     */         }
/*  433:     */       }
/*  434: 827 */       while (j < splitWon.length)
/*  435:     */       {
/*  436: 828 */         splitWon[sortOrder[j]] = false;
/*  437: 829 */         j++;
/*  438:     */       }
/*  439:     */     }
/*  440: 835 */     if (possibleToSplit > 0) {
/*  441: 836 */       newCenters = newCentersAfterSplit(splitWon, splitCenters);
/*  442:     */     } else {
/*  443: 838 */       newCenters = this.m_ClusterCenters;
/*  444:     */     }
/*  445: 840 */     return newCenters;
/*  446:     */   }
/*  447:     */   
/*  448:     */   protected Instances newCentersAfterSplit(boolean[] splitWon, Instances splitCenters)
/*  449:     */   {
/*  450: 853 */     Instances newCenters = new Instances(splitCenters, 0);
/*  451:     */     
/*  452: 855 */     int sIndex = 0;
/*  453: 856 */     for (int i = 0; i < splitWon.length; i++) {
/*  454: 857 */       if (splitWon[i] != 0)
/*  455:     */       {
/*  456: 858 */         this.m_NumSplitsDone += 1;
/*  457: 859 */         newCenters.add(splitCenters.instance(sIndex++));
/*  458: 860 */         newCenters.add(splitCenters.instance(sIndex++));
/*  459:     */       }
/*  460:     */       else
/*  461:     */       {
/*  462: 862 */         sIndex++;
/*  463: 863 */         sIndex++;
/*  464: 864 */         newCenters.add(this.m_ClusterCenters.instance(i));
/*  465:     */       }
/*  466:     */     }
/*  467: 867 */     return newCenters;
/*  468:     */   }
/*  469:     */   
/*  470:     */   protected boolean stopKMeansIteration(int iterationCount, int max)
/*  471:     */   {
/*  472: 879 */     boolean stopIterate = false;
/*  473: 880 */     if (max >= 0) {
/*  474: 881 */       stopIterate = iterationCount >= max;
/*  475:     */     }
/*  476: 883 */     if (stopIterate) {
/*  477: 884 */       this.m_KMeansStopped += 1;
/*  478:     */     }
/*  479: 886 */     return stopIterate;
/*  480:     */   }
/*  481:     */   
/*  482:     */   protected boolean stopIteration(int iterationCount, int max)
/*  483:     */   {
/*  484: 898 */     boolean stopIterate = false;
/*  485: 899 */     if (max >= 0) {
/*  486: 900 */       stopIterate = iterationCount >= max;
/*  487:     */     }
/*  488: 902 */     return stopIterate;
/*  489:     */   }
/*  490:     */   
/*  491:     */   protected boolean recomputeCenters(Instances centers, int[][] instOfCent, Instances model)
/*  492:     */   {
/*  493: 916 */     boolean converged = true;
/*  494: 918 */     for (int i = 0; i < centers.numInstances(); i++) {
/*  495: 920 */       for (int j = 0; j < model.numAttributes(); j++)
/*  496:     */       {
/*  497: 921 */         double val = meanOrMode(this.m_Instances, instOfCent[i], j);
/*  498: 923 */         for (int k = 0; k < instOfCent[i].length; k++) {
/*  499: 924 */           if ((converged) && (this.m_ClusterCenters.instance(i).value(j) != val)) {
/*  500: 925 */             converged = false;
/*  501:     */           }
/*  502:     */         }
/*  503: 928 */         if (!converged) {
/*  504: 929 */           this.m_ClusterCenters.instance(i).setValue(j, val);
/*  505:     */         }
/*  506:     */       }
/*  507:     */     }
/*  508: 933 */     return converged;
/*  509:     */   }
/*  510:     */   
/*  511:     */   protected void recomputeCentersFast(Instances centers, int[][] instOfCentIndexes, Instances model)
/*  512:     */   {
/*  513: 946 */     for (int i = 0; i < centers.numInstances(); i++) {
/*  514: 948 */       for (int j = 0; j < model.numAttributes(); j++)
/*  515:     */       {
/*  516: 949 */         double val = meanOrMode(this.m_Instances, instOfCentIndexes[i], j);
/*  517: 950 */         centers.instance(i).setValue(j, val);
/*  518:     */       }
/*  519:     */     }
/*  520:     */   }
/*  521:     */   
/*  522:     */   protected double meanOrMode(Instances instances, int[] instList, int attIndex)
/*  523:     */   {
/*  524: 967 */     int numInst = instList.length;
/*  525: 969 */     if (instances.attribute(attIndex).isNumeric())
/*  526:     */     {
/*  527:     */       double found;
/*  528: 970 */       double result = found = 0.0D;
/*  529: 971 */       for (int j = 0; j < numInst; j++)
/*  530:     */       {
/*  531: 972 */         Instance currInst = instances.instance(instList[j]);
/*  532: 973 */         if (!currInst.isMissing(attIndex))
/*  533:     */         {
/*  534: 974 */           found += currInst.weight();
/*  535: 975 */           result += currInst.weight() * currInst.value(attIndex);
/*  536:     */         }
/*  537:     */       }
/*  538: 978 */       if (Utils.eq(found, 0.0D)) {
/*  539: 979 */         return 0.0D;
/*  540:     */       }
/*  541: 981 */       return result / found;
/*  542:     */     }
/*  543: 983 */     if (instances.attribute(attIndex).isNominal())
/*  544:     */     {
/*  545: 984 */       int[] counts = new int[instances.attribute(attIndex).numValues()];
/*  546: 985 */       for (int j = 0; j < numInst; j++)
/*  547:     */       {
/*  548: 986 */         Instance currInst = instances.instance(instList[j]);
/*  549: 987 */         if (!currInst.isMissing(attIndex))
/*  550:     */         {
/*  551: 988 */           int tmp173_172 = ((int)currInst.value(attIndex)); int[] tmp173_162 = counts;tmp173_162[tmp173_172] = ((int)(tmp173_162[tmp173_172] + currInst.weight()));
/*  552:     */         }
/*  553:     */       }
/*  554: 991 */       return Utils.maxIndex(counts);
/*  555:     */     }
/*  556: 993 */     return 0.0D;
/*  557:     */   }
/*  558:     */   
/*  559:     */   protected boolean assignToCenters(KDTree tree, Instances centers, int[][] instOfCent, int[] allInstList, int[] assignments, int iterationCount)
/*  560:     */     throws Exception
/*  561:     */   {
/*  562:1013 */     boolean converged = true;
/*  563:1014 */     if (tree != null) {
/*  564:1016 */       converged = assignToCenters(tree, centers, instOfCent, assignments, iterationCount);
/*  565:     */     } else {
/*  566:1019 */       converged = assignToCenters(centers, instOfCent, allInstList, assignments);
/*  567:     */     }
/*  568:1021 */     return converged;
/*  569:     */   }
/*  570:     */   
/*  571:     */   protected boolean assignToCenters(KDTree kdtree, Instances centers, int[][] instOfCent, int[] assignments, int iterationCount)
/*  572:     */     throws Exception
/*  573:     */   {
/*  574:1039 */     int numCent = centers.numInstances();
/*  575:1040 */     int numInst = this.m_Instances.numInstances();
/*  576:1041 */     int[] oldAssignments = new int[numInst];
/*  577:1045 */     if (assignments == null)
/*  578:     */     {
/*  579:1046 */       assignments = new int[numInst];
/*  580:1047 */       for (int i = 0; i < numInst; i++) {
/*  581:1048 */         assignments[0] = -1;
/*  582:     */       }
/*  583:     */     }
/*  584:1054 */     if (instOfCent == null) {
/*  585:1055 */       instOfCent = new int[numCent][];
/*  586:     */     }
/*  587:1059 */     for (int i = 0; i < assignments.length; i++) {
/*  588:1060 */       oldAssignments[i] = assignments[i];
/*  589:     */     }
/*  590:1064 */     kdtree.centerInstances(centers, assignments, Math.pow(0.8D, iterationCount));
/*  591:1065 */     boolean converged = true;
/*  592:1068 */     for (int i = 0; (converged) && (i < assignments.length); i++)
/*  593:     */     {
/*  594:1069 */       converged = oldAssignments[i] == assignments[i];
/*  595:1070 */       if (assignments[i] == -1) {
/*  596:1071 */         throw new Exception("Instance " + i + " has not been assigned to cluster.");
/*  597:     */       }
/*  598:     */     }
/*  599:1076 */     if (!converged)
/*  600:     */     {
/*  601:1077 */       int[] numInstOfCent = new int[numCent];
/*  602:1078 */       for (int i = 0; i < numCent; i++) {
/*  603:1079 */         numInstOfCent[i] = 0;
/*  604:     */       }
/*  605:1083 */       for (int i = 0; i < numInst; i++) {
/*  606:1084 */         numInstOfCent[assignments[i]] += 1;
/*  607:     */       }
/*  608:1088 */       for (int i = 0; i < numCent; i++) {
/*  609:1089 */         instOfCent[i] = new int[numInstOfCent[i]];
/*  610:     */       }
/*  611:1092 */       for (int i = 0; i < numCent; i++)
/*  612:     */       {
/*  613:1093 */         int index = -1;
/*  614:1094 */         for (int j = 0; j < numInstOfCent[i]; j++)
/*  615:     */         {
/*  616:1095 */           index = nextAssignedOne(i, index, assignments);
/*  617:1096 */           instOfCent[i][j] = index;
/*  618:     */         }
/*  619:     */       }
/*  620:     */     }
/*  621:1101 */     return converged;
/*  622:     */   }
/*  623:     */   
/*  624:     */   protected boolean assignToCenters(Instances centers, int[][] instOfCent, int[] allInstList, int[] assignments)
/*  625:     */     throws Exception
/*  626:     */   {
/*  627:1119 */     boolean converged = true;
/*  628:     */     
/*  629:     */ 
/*  630:1122 */     int numInst = allInstList.length;
/*  631:1123 */     int numCent = centers.numInstances();
/*  632:1124 */     int[] numInstOfCent = new int[numCent];
/*  633:1125 */     for (int i = 0; i < numCent; i++) {
/*  634:1126 */       numInstOfCent[i] = 0;
/*  635:     */     }
/*  636:1131 */     if (assignments == null)
/*  637:     */     {
/*  638:1132 */       assignments = new int[numInst];
/*  639:1133 */       for (int i = 0; i < numInst; i++) {
/*  640:1134 */         assignments[i] = -1;
/*  641:     */       }
/*  642:     */     }
/*  643:1140 */     if (instOfCent == null) {
/*  644:1141 */       instOfCent = new int[numCent][];
/*  645:     */     }
/*  646:1145 */     for (int i = 0; i < numInst; i++)
/*  647:     */     {
/*  648:1146 */       Instance inst = this.m_Instances.instance(allInstList[i]);
/*  649:1147 */       int newC = clusterProcessedInstance(inst, centers);
/*  650:1149 */       if ((converged) && (newC != assignments[i])) {
/*  651:1150 */         converged = false;
/*  652:     */       }
/*  653:1153 */       numInstOfCent[newC] += 1;
/*  654:1154 */       if (!converged) {
/*  655:1155 */         assignments[i] = newC;
/*  656:     */       }
/*  657:     */     }
/*  658:1161 */     if (!converged)
/*  659:     */     {
/*  660:1162 */       PFD(D_FOLLOWSPLIT, "assignToCenters -> it has NOT converged");
/*  661:1163 */       for (int i = 0; i < numCent; i++) {
/*  662:1164 */         instOfCent[i] = new int[numInstOfCent[i]];
/*  663:     */       }
/*  664:1167 */       for (int i = 0; i < numCent; i++)
/*  665:     */       {
/*  666:1168 */         int index = -1;
/*  667:1169 */         for (int j = 0; j < numInstOfCent[i]; j++)
/*  668:     */         {
/*  669:1170 */           index = nextAssignedOne(i, index, assignments);
/*  670:1171 */           instOfCent[i][j] = allInstList[index];
/*  671:     */         }
/*  672:     */       }
/*  673:     */     }
/*  674:     */     else
/*  675:     */     {
/*  676:1175 */       PFD(D_FOLLOWSPLIT, "assignToCenters -> it has converged");
/*  677:     */     }
/*  678:1178 */     return converged;
/*  679:     */   }
/*  680:     */   
/*  681:     */   protected int nextAssignedOne(int cent, int lastIndex, int[] assignments)
/*  682:     */   {
/*  683:1191 */     int len = assignments.length;
/*  684:1192 */     int index = lastIndex + 1;
/*  685:1193 */     while (index < len)
/*  686:     */     {
/*  687:1194 */       if (assignments[index] == cent) {
/*  688:1195 */         return index;
/*  689:     */       }
/*  690:1197 */       index++;
/*  691:     */     }
/*  692:1199 */     return -1;
/*  693:     */   }
/*  694:     */   
/*  695:     */   protected Instances splitCenter(Random random, Instance center, double variance, Instances model)
/*  696:     */     throws Exception
/*  697:     */   {
/*  698:1215 */     this.m_NumSplits += 1;
/*  699:1216 */     AlgVector r = null;
/*  700:1217 */     Instances children = new Instances(model, 2);
/*  701:1219 */     if ((this.m_DebugVectorsFile.exists()) && (this.m_DebugVectorsFile.isFile()))
/*  702:     */     {
/*  703:1220 */       Instance nextVector = getNextDebugVectorsInstance(model);
/*  704:1221 */       PFD(D_RANDOMVECTOR, "Random Vector from File " + nextVector);
/*  705:1222 */       r = new AlgVector(nextVector);
/*  706:     */     }
/*  707:     */     else
/*  708:     */     {
/*  709:1225 */       r = new AlgVector(model, random);
/*  710:     */     }
/*  711:1227 */     r.changeLength(Math.pow(variance, 0.5D));
/*  712:1228 */     PFD(D_RANDOMVECTOR, "random vector *variance " + r);
/*  713:     */     
/*  714:     */ 
/*  715:1231 */     AlgVector c = new AlgVector(center);
/*  716:1232 */     AlgVector c2 = (AlgVector)c.clone();
/*  717:1233 */     c = c.add(r);
/*  718:1234 */     Instance newCenter = c.getAsInstance(model, random);
/*  719:1235 */     children.add(newCenter);
/*  720:1236 */     PFD(D_FOLLOWSPLIT, "first child " + newCenter);
/*  721:     */     
/*  722:     */ 
/*  723:1239 */     c2 = c2.substract(r);
/*  724:1240 */     newCenter = c2.getAsInstance(model, random);
/*  725:1241 */     children.add(newCenter);
/*  726:1242 */     PFD(D_FOLLOWSPLIT, "second child " + newCenter);
/*  727:     */     
/*  728:1244 */     return children;
/*  729:     */   }
/*  730:     */   
/*  731:     */   protected Instances splitCenters(Random random, Instances instances, Instances model)
/*  732:     */   {
/*  733:1258 */     Instances children = new Instances(model, 2);
/*  734:1259 */     int instIndex = Math.abs(random.nextInt()) % instances.numInstances();
/*  735:1260 */     children.add(instances.instance(instIndex));
/*  736:1261 */     int instIndex2 = instIndex;
/*  737:1262 */     int count = 0;
/*  738:1263 */     while ((instIndex2 == instIndex) && (count < 10))
/*  739:     */     {
/*  740:1264 */       count++;
/*  741:1265 */       instIndex2 = Math.abs(random.nextInt()) % instances.numInstances();
/*  742:     */     }
/*  743:1267 */     children.add(instances.instance(instIndex2));
/*  744:     */     
/*  745:1269 */     return children;
/*  746:     */   }
/*  747:     */   
/*  748:     */   protected Instances makeCentersRandomly(Random random0, Instances model, int numClusters)
/*  749:     */   {
/*  750:1282 */     Instances clusterCenters = new Instances(model, numClusters);
/*  751:1283 */     this.m_NumClusters = numClusters;
/*  752:1286 */     for (int i = 0; i < numClusters; i++)
/*  753:     */     {
/*  754:1287 */       int instIndex = Math.abs(random0.nextInt()) % this.m_Instances.numInstances();
/*  755:1288 */       clusterCenters.add(this.m_Instances.instance(instIndex));
/*  756:     */     }
/*  757:1290 */     return clusterCenters;
/*  758:     */   }
/*  759:     */   
/*  760:     */   protected double calculateBIC(int[] instList, Instance center, double mle, Instances model)
/*  761:     */   {
/*  762:1304 */     int[][] w1 = new int[1][instList.length];
/*  763:1305 */     for (int i = 0; i < instList.length; i++) {
/*  764:1306 */       w1[0][i] = instList[i];
/*  765:     */     }
/*  766:1308 */     double[] m = { mle };
/*  767:1309 */     Instances w2 = new Instances(model, 1);
/*  768:1310 */     w2.add(center);
/*  769:1311 */     return calculateBIC(w1, w2, m);
/*  770:     */   }
/*  771:     */   
/*  772:     */   protected double calculateBIC(int[][] instOfCent, Instances centers, double[] mle)
/*  773:     */   {
/*  774:1324 */     double loglike = 0.0D;
/*  775:1325 */     int numInstTotal = 0;
/*  776:1326 */     int numCenters = centers.numInstances();
/*  777:1327 */     int numDimensions = centers.numAttributes();
/*  778:1328 */     int numParameters = numCenters - 1 + numCenters * numDimensions + numCenters;
/*  779:1331 */     for (int i = 0; i < centers.numInstances(); i++)
/*  780:     */     {
/*  781:1332 */       loglike += logLikelihoodEstimate(instOfCent[i].length, centers.instance(i), mle[i], centers.numInstances() * 2);
/*  782:     */       
/*  783:1334 */       numInstTotal += instOfCent[i].length;
/*  784:     */     }
/*  785:1340 */     loglike -= numInstTotal * Math.log(numInstTotal);
/*  786:     */     
/*  787:     */ 
/*  788:1343 */     loglike -= numParameters / 2.0D * Math.log(numInstTotal);
/*  789:     */     
/*  790:     */ 
/*  791:     */ 
/*  792:1347 */     return loglike;
/*  793:     */   }
/*  794:     */   
/*  795:     */   protected double logLikelihoodEstimate(int numInst, Instance center, double distortion, int numCent)
/*  796:     */   {
/*  797:1366 */     double loglike = 0.0D;
/*  798:1368 */     if (numInst > 1)
/*  799:     */     {
/*  800:1376 */       double variance = distortion / (numInst - 1.0D);
/*  801:     */       
/*  802:     */ 
/*  803:     */ 
/*  804:     */ 
/*  805:1381 */       double p1 = -(numInst / 2.0D) * Math.log(6.283185307179586D);
/*  806:     */       
/*  807:     */ 
/*  808:     */ 
/*  809:     */ 
/*  810:     */ 
/*  811:     */ 
/*  812:     */ 
/*  813:1389 */       double p2 = -(numInst * center.numAttributes()) / 2 * Math.log(variance);
/*  814:     */       
/*  815:     */ 
/*  816:     */ 
/*  817:     */ 
/*  818:     */ 
/*  819:     */ 
/*  820:     */ 
/*  821:     */ 
/*  822:1398 */       double p3 = -(numInst - 1.0D) / 2.0D;
/*  823:     */       
/*  824:     */ 
/*  825:     */ 
/*  826:     */ 
/*  827:1403 */       double p4 = numInst * Math.log(numInst);
/*  828:     */       
/*  829:     */ 
/*  830:     */ 
/*  831:     */ 
/*  832:     */ 
/*  833:     */ 
/*  834:     */ 
/*  835:     */ 
/*  836:     */ 
/*  837:     */ 
/*  838:     */ 
/*  839:1415 */       loglike = p1 + p2 + p3 + p4;
/*  840:     */     }
/*  841:1420 */     return loglike;
/*  842:     */   }
/*  843:     */   
/*  844:     */   protected double[] distortion(int[][] instOfCent, Instances centers)
/*  845:     */   {
/*  846:1431 */     double[] distortion = new double[centers.numInstances()];
/*  847:1432 */     for (int i = 0; i < centers.numInstances(); i++)
/*  848:     */     {
/*  849:1433 */       distortion[i] = 0.0D;
/*  850:1434 */       for (int j = 0; j < instOfCent[i].length; j++) {
/*  851:1435 */         distortion[i] += this.m_DistanceF.distance(this.m_Instances.instance(instOfCent[i][j]), centers.instance(i));
/*  852:     */       }
/*  853:     */     }
/*  854:1442 */     return distortion;
/*  855:     */   }
/*  856:     */   
/*  857:     */   protected int clusterProcessedInstance(Instance instance, Instances centers)
/*  858:     */   {
/*  859:1454 */     double minDist = 2147483647.0D;
/*  860:1455 */     int bestCluster = 0;
/*  861:1456 */     for (int i = 0; i < centers.numInstances(); i++)
/*  862:     */     {
/*  863:1457 */       double dist = this.m_DistanceF.distance(instance, centers.instance(i));
/*  864:1459 */       if (dist < minDist)
/*  865:     */       {
/*  866:1460 */         minDist = dist;
/*  867:1461 */         bestCluster = i;
/*  868:     */       }
/*  869:     */     }
/*  870:1465 */     return bestCluster;
/*  871:     */   }
/*  872:     */   
/*  873:     */   protected int clusterProcessedInstance(Instance instance)
/*  874:     */   {
/*  875:1475 */     double minDist = 2147483647.0D;
/*  876:1476 */     int bestCluster = 0;
/*  877:1477 */     for (int i = 0; i < this.m_NumClusters; i++)
/*  878:     */     {
/*  879:1478 */       double dist = this.m_DistanceF.distance(instance, this.m_ClusterCenters.instance(i));
/*  880:1480 */       if (dist < minDist)
/*  881:     */       {
/*  882:1481 */         minDist = dist;
/*  883:1482 */         bestCluster = i;
/*  884:     */       }
/*  885:     */     }
/*  886:1485 */     return bestCluster;
/*  887:     */   }
/*  888:     */   
/*  889:     */   public int clusterInstance(Instance instance)
/*  890:     */     throws Exception
/*  891:     */   {
/*  892:1498 */     this.m_ReplaceMissingFilter.input(instance);
/*  893:1499 */     Instance inst = this.m_ReplaceMissingFilter.output();
/*  894:     */     
/*  895:1501 */     return clusterProcessedInstance(inst);
/*  896:     */   }
/*  897:     */   
/*  898:     */   public int numberOfClusters()
/*  899:     */   {
/*  900:1511 */     return this.m_NumClusters;
/*  901:     */   }
/*  902:     */   
/*  903:     */   public Enumeration<Option> listOptions()
/*  904:     */   {
/*  905:1522 */     Vector<Option> result = new Vector();
/*  906:     */     
/*  907:1524 */     result.addElement(new Option("\tmaximum number of overall iterations\n\t(default 1).", "I", 1, "-I <num>"));
/*  908:     */     
/*  909:     */ 
/*  910:1527 */     result.addElement(new Option("\tmaximum number of iterations in the kMeans loop in\n\tthe Improve-Parameter part \n\t(default 1000).", "M", 1, "-M <num>"));
/*  911:     */     
/*  912:     */ 
/*  913:     */ 
/*  914:     */ 
/*  915:1532 */     result.addElement(new Option("\tmaximum number of iterations in the kMeans loop\n\tfor the splitted centroids in the Improve-Structure part \n\t(default 1000).", "J", 1, "-J <num>"));
/*  916:     */     
/*  917:     */ 
/*  918:     */ 
/*  919:     */ 
/*  920:1537 */     result.addElement(new Option("\tminimum number of clusters\n\t(default 2).", "L", 1, "-L <num>"));
/*  921:     */     
/*  922:     */ 
/*  923:1540 */     result.addElement(new Option("\tmaximum number of clusters\n\t(default 4).", "H", 1, "-H <num>"));
/*  924:     */     
/*  925:     */ 
/*  926:1543 */     result.addElement(new Option("\tdistance value for binary attributes\n\t(default 1.0).", "B", 1, "-B <value>"));
/*  927:     */     
/*  928:     */ 
/*  929:1546 */     result.addElement(new Option("\tUses the KDTree internally\n\t(default no).", "use-kdtree", 0, "-use-kdtree"));
/*  930:     */     
/*  931:     */ 
/*  932:1549 */     result.addElement(new Option("\tFull class name of KDTree class to use, followed\n\tby scheme options.\n\teg: \"weka.core.neighboursearch.kdtrees.KDTree -P\"\n\t(default no KDTree class used).", "K", 1, "-K <KDTree class specification>"));
/*  933:     */     
/*  934:     */ 
/*  935:     */ 
/*  936:     */ 
/*  937:     */ 
/*  938:     */ 
/*  939:1556 */     result.addElement(new Option("\tcutoff factor, takes the given percentage of the splitted \n\tcentroids if none of the children win\n\t(default 0.0).", "C", 1, "-C <value>"));
/*  940:     */     
/*  941:     */ 
/*  942:     */ 
/*  943:     */ 
/*  944:1561 */     result.addElement(new Option("\tFull class name of Distance function class to use, followed\n\tby scheme options.\n\t(default weka.core.EuclideanDistance).", "D", 1, "-D <distance function class specification>"));
/*  945:     */     
/*  946:     */ 
/*  947:     */ 
/*  948:     */ 
/*  949:     */ 
/*  950:     */ 
/*  951:1568 */     result.addElement(new Option("\tfile to read starting centers from (ARFF format).", "N", 1, "-N <file name>"));
/*  952:     */     
/*  953:     */ 
/*  954:     */ 
/*  955:1572 */     result.addElement(new Option("\tfile to write centers to (ARFF format).", "O", 1, "-O <file name>"));
/*  956:     */     
/*  957:     */ 
/*  958:1575 */     result.addElement(new Option("\tThe debug level.\n\t(default 0)", "U", 1, "-U <int>"));
/*  959:     */     
/*  960:     */ 
/*  961:1578 */     result.addElement(new Option("\tThe debug vectors file.", "Y", 1, "-Y <file name>"));
/*  962:     */     
/*  963:     */ 
/*  964:1581 */     result.addAll(Collections.list(super.listOptions()));
/*  965:     */     
/*  966:1583 */     return result.elements();
/*  967:     */   }
/*  968:     */   
/*  969:     */   public String minNumClustersTipText()
/*  970:     */   {
/*  971:1592 */     return "set minimum number of clusters";
/*  972:     */   }
/*  973:     */   
/*  974:     */   public void setMinNumClusters(int n)
/*  975:     */   {
/*  976:1601 */     this.m_MinNumClusters = n;
/*  977:     */   }
/*  978:     */   
/*  979:     */   public int getMinNumClusters()
/*  980:     */   {
/*  981:1610 */     return this.m_MinNumClusters;
/*  982:     */   }
/*  983:     */   
/*  984:     */   public String maxNumClustersTipText()
/*  985:     */   {
/*  986:1619 */     return "set maximum number of clusters";
/*  987:     */   }
/*  988:     */   
/*  989:     */   public void setMaxNumClusters(int n)
/*  990:     */   {
/*  991:1628 */     if (n >= this.m_MinNumClusters) {
/*  992:1629 */       this.m_MaxNumClusters = n;
/*  993:     */     }
/*  994:     */   }
/*  995:     */   
/*  996:     */   public int getMaxNumClusters()
/*  997:     */   {
/*  998:1639 */     return this.m_MaxNumClusters;
/*  999:     */   }
/* 1000:     */   
/* 1001:     */   public String maxIterationsTipText()
/* 1002:     */   {
/* 1003:1648 */     return "the maximum number of iterations to perform";
/* 1004:     */   }
/* 1005:     */   
/* 1006:     */   public void setMaxIterations(int i)
/* 1007:     */     throws Exception
/* 1008:     */   {
/* 1009:1658 */     if (i < 0) {
/* 1010:1659 */       throw new Exception("Only positive values for iteration number allowed (Option I).");
/* 1011:     */     }
/* 1012:1662 */     this.m_MaxIterations = i;
/* 1013:     */   }
/* 1014:     */   
/* 1015:     */   public int getMaxIterations()
/* 1016:     */   {
/* 1017:1671 */     return this.m_MaxIterations;
/* 1018:     */   }
/* 1019:     */   
/* 1020:     */   public String maxKMeansTipText()
/* 1021:     */   {
/* 1022:1680 */     return "the maximum number of iterations to perform in KMeans";
/* 1023:     */   }
/* 1024:     */   
/* 1025:     */   public void setMaxKMeans(int i)
/* 1026:     */   {
/* 1027:1689 */     this.m_MaxKMeans = i;
/* 1028:1690 */     this.m_MaxKMeansForChildren = i;
/* 1029:     */   }
/* 1030:     */   
/* 1031:     */   public int getMaxKMeans()
/* 1032:     */   {
/* 1033:1699 */     return this.m_MaxKMeans;
/* 1034:     */   }
/* 1035:     */   
/* 1036:     */   public String maxKMeansForChildrenTipText()
/* 1037:     */   {
/* 1038:1708 */     return "the maximum number of iterations KMeans that is performed on the child centers";
/* 1039:     */   }
/* 1040:     */   
/* 1041:     */   public void setMaxKMeansForChildren(int i)
/* 1042:     */   {
/* 1043:1718 */     this.m_MaxKMeansForChildren = i;
/* 1044:     */   }
/* 1045:     */   
/* 1046:     */   public int getMaxKMeansForChildren()
/* 1047:     */   {
/* 1048:1727 */     return this.m_MaxKMeansForChildren;
/* 1049:     */   }
/* 1050:     */   
/* 1051:     */   public String cutOffFactorTipText()
/* 1052:     */   {
/* 1053:1736 */     return "the cut-off factor to use";
/* 1054:     */   }
/* 1055:     */   
/* 1056:     */   public void setCutOffFactor(double i)
/* 1057:     */   {
/* 1058:1745 */     this.m_CutOffFactor = i;
/* 1059:     */   }
/* 1060:     */   
/* 1061:     */   public double getCutOffFactor()
/* 1062:     */   {
/* 1063:1754 */     return this.m_CutOffFactor;
/* 1064:     */   }
/* 1065:     */   
/* 1066:     */   public String binValueTipText()
/* 1067:     */   {
/* 1068:1764 */     return "Set the value that represents true in the new attributes.";
/* 1069:     */   }
/* 1070:     */   
/* 1071:     */   public double getBinValue()
/* 1072:     */   {
/* 1073:1774 */     return this.m_BinValue;
/* 1074:     */   }
/* 1075:     */   
/* 1076:     */   public void setBinValue(double value)
/* 1077:     */   {
/* 1078:1784 */     this.m_BinValue = value;
/* 1079:     */   }
/* 1080:     */   
/* 1081:     */   public String distanceFTipText()
/* 1082:     */   {
/* 1083:1794 */     return "The distance function to use.";
/* 1084:     */   }
/* 1085:     */   
/* 1086:     */   public void setDistanceF(DistanceFunction distanceF)
/* 1087:     */   {
/* 1088:1803 */     this.m_DistanceF = distanceF;
/* 1089:     */   }
/* 1090:     */   
/* 1091:     */   public DistanceFunction getDistanceF()
/* 1092:     */   {
/* 1093:1812 */     return this.m_DistanceF;
/* 1094:     */   }
/* 1095:     */   
/* 1096:     */   protected String getDistanceFSpec()
/* 1097:     */   {
/* 1098:1823 */     DistanceFunction d = getDistanceF();
/* 1099:1824 */     if ((d instanceof OptionHandler)) {
/* 1100:1825 */       return d.getClass().getName() + " " + Utils.joinOptions(d.getOptions());
/* 1101:     */     }
/* 1102:1828 */     return d.getClass().getName();
/* 1103:     */   }
/* 1104:     */   
/* 1105:     */   public String debugVectorsFileTipText()
/* 1106:     */   {
/* 1107:1838 */     return "The file containing the debug vectors (only for debugging!).";
/* 1108:     */   }
/* 1109:     */   
/* 1110:     */   public void setDebugVectorsFile(File value)
/* 1111:     */   {
/* 1112:1848 */     this.m_DebugVectorsFile = value;
/* 1113:     */   }
/* 1114:     */   
/* 1115:     */   public File getDebugVectorsFile()
/* 1116:     */   {
/* 1117:1858 */     return this.m_DebugVectorsFile;
/* 1118:     */   }
/* 1119:     */   
/* 1120:     */   public void initDebugVectorsInput()
/* 1121:     */     throws Exception
/* 1122:     */   {
/* 1123:1867 */     this.m_DebugVectorsInput = new BufferedReader(new FileReader(this.m_DebugVectorsFile));
/* 1124:1868 */     this.m_DebugVectors = new Instances(this.m_DebugVectorsInput);
/* 1125:1869 */     this.m_DebugVectorsIndex = 0;
/* 1126:     */   }
/* 1127:     */   
/* 1128:     */   public Instance getNextDebugVectorsInstance(Instances model)
/* 1129:     */     throws Exception
/* 1130:     */   {
/* 1131:1880 */     if (this.m_DebugVectorsIndex >= this.m_DebugVectors.numInstances()) {
/* 1132:1881 */       throw new Exception("no more prefabricated Vectors");
/* 1133:     */     }
/* 1134:1883 */     Instance nex = this.m_DebugVectors.instance(this.m_DebugVectorsIndex);
/* 1135:1884 */     nex.setDataset(model);
/* 1136:1885 */     this.m_DebugVectorsIndex += 1;
/* 1137:1886 */     return nex;
/* 1138:     */   }
/* 1139:     */   
/* 1140:     */   public String inputCenterFileTipText()
/* 1141:     */   {
/* 1142:1896 */     return "The file to read the list of centers from.";
/* 1143:     */   }
/* 1144:     */   
/* 1145:     */   public void setInputCenterFile(File value)
/* 1146:     */   {
/* 1147:1905 */     this.m_InputCenterFile = value;
/* 1148:     */   }
/* 1149:     */   
/* 1150:     */   public File getInputCenterFile()
/* 1151:     */   {
/* 1152:1914 */     return this.m_InputCenterFile;
/* 1153:     */   }
/* 1154:     */   
/* 1155:     */   public String outputCenterFileTipText()
/* 1156:     */   {
/* 1157:1924 */     return "The file to write the list of centers to.";
/* 1158:     */   }
/* 1159:     */   
/* 1160:     */   public void setOutputCenterFile(File value)
/* 1161:     */   {
/* 1162:1933 */     this.m_OutputCenterFile = value;
/* 1163:     */   }
/* 1164:     */   
/* 1165:     */   public File getOutputCenterFile()
/* 1166:     */   {
/* 1167:1942 */     return this.m_OutputCenterFile;
/* 1168:     */   }
/* 1169:     */   
/* 1170:     */   public String KDTreeTipText()
/* 1171:     */   {
/* 1172:1952 */     return "The KDTree to use.";
/* 1173:     */   }
/* 1174:     */   
/* 1175:     */   public void setKDTree(KDTree k)
/* 1176:     */   {
/* 1177:1961 */     this.m_KDTree = k;
/* 1178:     */   }
/* 1179:     */   
/* 1180:     */   public KDTree getKDTree()
/* 1181:     */   {
/* 1182:1970 */     return this.m_KDTree;
/* 1183:     */   }
/* 1184:     */   
/* 1185:     */   public String useKDTreeTipText()
/* 1186:     */   {
/* 1187:1980 */     return "Whether to use the KDTree.";
/* 1188:     */   }
/* 1189:     */   
/* 1190:     */   public void setUseKDTree(boolean value)
/* 1191:     */   {
/* 1192:1989 */     this.m_UseKDTree = value;
/* 1193:     */   }
/* 1194:     */   
/* 1195:     */   public boolean getUseKDTree()
/* 1196:     */   {
/* 1197:1998 */     return this.m_UseKDTree;
/* 1198:     */   }
/* 1199:     */   
/* 1200:     */   protected String getKDTreeSpec()
/* 1201:     */   {
/* 1202:2009 */     KDTree c = getKDTree();
/* 1203:2010 */     if ((c instanceof OptionHandler)) {
/* 1204:2011 */       return c.getClass().getName() + " " + Utils.joinOptions(c.getOptions());
/* 1205:     */     }
/* 1206:2014 */     return c.getClass().getName();
/* 1207:     */   }
/* 1208:     */   
/* 1209:     */   public String debugLevelTipText()
/* 1210:     */   {
/* 1211:2024 */     return "The debug level to use.";
/* 1212:     */   }
/* 1213:     */   
/* 1214:     */   public void setDebugLevel(int d)
/* 1215:     */   {
/* 1216:2033 */     this.m_DebugLevel = d;
/* 1217:     */   }
/* 1218:     */   
/* 1219:     */   public int getDebugLevel()
/* 1220:     */   {
/* 1221:2042 */     return this.m_DebugLevel;
/* 1222:     */   }
/* 1223:     */   
/* 1224:     */   protected void checkInstances() {}
/* 1225:     */   
/* 1226:     */   public void setOptions(String[] options)
/* 1227:     */     throws Exception
/* 1228:     */   {
/* 1229:2165 */     String optionString = Utils.getOption('I', options);
/* 1230:2166 */     if (optionString.length() != 0) {
/* 1231:2167 */       setMaxIterations(Integer.parseInt(optionString));
/* 1232:     */     } else {
/* 1233:2169 */       setMaxIterations(1);
/* 1234:     */     }
/* 1235:2172 */     optionString = Utils.getOption('M', options);
/* 1236:2173 */     if (optionString.length() != 0) {
/* 1237:2174 */       setMaxKMeans(Integer.parseInt(optionString));
/* 1238:     */     } else {
/* 1239:2176 */       setMaxKMeans(1000);
/* 1240:     */     }
/* 1241:2179 */     optionString = Utils.getOption('J', options);
/* 1242:2180 */     if (optionString.length() != 0) {
/* 1243:2181 */       setMaxKMeansForChildren(Integer.parseInt(optionString));
/* 1244:     */     } else {
/* 1245:2183 */       setMaxKMeansForChildren(1000);
/* 1246:     */     }
/* 1247:2186 */     optionString = Utils.getOption('L', options);
/* 1248:2187 */     if (optionString.length() != 0) {
/* 1249:2188 */       setMinNumClusters(Integer.parseInt(optionString));
/* 1250:     */     } else {
/* 1251:2190 */       setMinNumClusters(2);
/* 1252:     */     }
/* 1253:2193 */     optionString = Utils.getOption('H', options);
/* 1254:2194 */     if (optionString.length() != 0) {
/* 1255:2195 */       setMaxNumClusters(Integer.parseInt(optionString));
/* 1256:     */     } else {
/* 1257:2197 */       setMaxNumClusters(4);
/* 1258:     */     }
/* 1259:2200 */     optionString = Utils.getOption('B', options);
/* 1260:2201 */     if (optionString.length() != 0) {
/* 1261:2202 */       setBinValue(Double.parseDouble(optionString));
/* 1262:     */     } else {
/* 1263:2204 */       setBinValue(1.0D);
/* 1264:     */     }
/* 1265:2207 */     setUseKDTree(Utils.getFlag("use-kdtree", options));
/* 1266:2209 */     if (getUseKDTree())
/* 1267:     */     {
/* 1268:2210 */       String funcString = Utils.getOption('K', options);
/* 1269:2211 */       if (funcString.length() != 0)
/* 1270:     */       {
/* 1271:2212 */         String[] funcSpec = Utils.splitOptions(funcString);
/* 1272:2213 */         if (funcSpec.length == 0) {
/* 1273:2214 */           throw new Exception("Invalid function specification string");
/* 1274:     */         }
/* 1275:2216 */         String funcName = funcSpec[0];
/* 1276:2217 */         funcSpec[0] = "";
/* 1277:2218 */         setKDTree((KDTree)Utils.forName(KDTree.class, funcName, funcSpec));
/* 1278:     */       }
/* 1279:     */       else
/* 1280:     */       {
/* 1281:2220 */         setKDTree(new KDTree());
/* 1282:     */       }
/* 1283:     */     }
/* 1284:     */     else
/* 1285:     */     {
/* 1286:2223 */       setKDTree(new KDTree());
/* 1287:     */     }
/* 1288:2226 */     optionString = Utils.getOption('C', options);
/* 1289:2227 */     if (optionString.length() != 0) {
/* 1290:2228 */       setCutOffFactor(Double.parseDouble(optionString));
/* 1291:     */     } else {
/* 1292:2230 */       setCutOffFactor(0.0D);
/* 1293:     */     }
/* 1294:2233 */     String funcString = Utils.getOption('D', options);
/* 1295:2234 */     if (funcString.length() != 0)
/* 1296:     */     {
/* 1297:2235 */       String[] funcSpec = Utils.splitOptions(funcString);
/* 1298:2236 */       if (funcSpec.length == 0) {
/* 1299:2237 */         throw new Exception("Invalid function specification string");
/* 1300:     */       }
/* 1301:2239 */       String funcName = funcSpec[0];
/* 1302:2240 */       funcSpec[0] = "";
/* 1303:2241 */       setDistanceF((DistanceFunction)Utils.forName(DistanceFunction.class, funcName, funcSpec));
/* 1304:     */     }
/* 1305:     */     else
/* 1306:     */     {
/* 1307:2244 */       setDistanceF(new EuclideanDistance());
/* 1308:     */     }
/* 1309:2247 */     optionString = Utils.getOption('N', options);
/* 1310:2248 */     if (optionString.length() != 0)
/* 1311:     */     {
/* 1312:2249 */       setInputCenterFile(new File(optionString));
/* 1313:2250 */       this.m_CenterInput = new BufferedReader(new FileReader(optionString));
/* 1314:     */     }
/* 1315:     */     else
/* 1316:     */     {
/* 1317:2252 */       setInputCenterFile(new File(System.getProperty("user.dir")));
/* 1318:2253 */       this.m_CenterInput = null;
/* 1319:     */     }
/* 1320:2256 */     optionString = Utils.getOption('O', options);
/* 1321:2257 */     if (optionString.length() != 0)
/* 1322:     */     {
/* 1323:2258 */       setOutputCenterFile(new File(optionString));
/* 1324:2259 */       this.m_CenterOutput = new PrintWriter(new FileOutputStream(optionString));
/* 1325:     */     }
/* 1326:     */     else
/* 1327:     */     {
/* 1328:2261 */       setOutputCenterFile(new File(System.getProperty("user.dir")));
/* 1329:2262 */       this.m_CenterOutput = null;
/* 1330:     */     }
/* 1331:2265 */     optionString = Utils.getOption('U', options);
/* 1332:2266 */     int debugLevel = 0;
/* 1333:2267 */     if (optionString.length() != 0) {
/* 1334:     */       try
/* 1335:     */       {
/* 1336:2269 */         debugLevel = Integer.parseInt(optionString);
/* 1337:     */       }
/* 1338:     */       catch (NumberFormatException e)
/* 1339:     */       {
/* 1340:2271 */         throw new Exception(optionString + "is an illegal value for option -U");
/* 1341:     */       }
/* 1342:     */     }
/* 1343:2274 */     setDebugLevel(debugLevel);
/* 1344:     */     
/* 1345:2276 */     optionString = Utils.getOption('Y', options);
/* 1346:2277 */     if (optionString.length() != 0)
/* 1347:     */     {
/* 1348:2278 */       setDebugVectorsFile(new File(optionString));
/* 1349:     */     }
/* 1350:     */     else
/* 1351:     */     {
/* 1352:2280 */       setDebugVectorsFile(new File(System.getProperty("user.dir")));
/* 1353:2281 */       this.m_DebugVectorsInput = null;
/* 1354:2282 */       this.m_DebugVectors = null;
/* 1355:     */     }
/* 1356:2285 */     super.setOptions(options);
/* 1357:     */     
/* 1358:2287 */     Utils.checkForRemainingOptions(options);
/* 1359:     */   }
/* 1360:     */   
/* 1361:     */   public String[] getOptions()
/* 1362:     */   {
/* 1363:2298 */     Vector<String> result = new Vector();
/* 1364:     */     
/* 1365:2300 */     result.add("-I");
/* 1366:2301 */     result.add("" + getMaxIterations());
/* 1367:     */     
/* 1368:2303 */     result.add("-M");
/* 1369:2304 */     result.add("" + getMaxKMeans());
/* 1370:     */     
/* 1371:2306 */     result.add("-J");
/* 1372:2307 */     result.add("" + getMaxKMeansForChildren());
/* 1373:     */     
/* 1374:2309 */     result.add("-L");
/* 1375:2310 */     result.add("" + getMinNumClusters());
/* 1376:     */     
/* 1377:2312 */     result.add("-H");
/* 1378:2313 */     result.add("" + getMaxNumClusters());
/* 1379:     */     
/* 1380:2315 */     result.add("-B");
/* 1381:2316 */     result.add("" + getBinValue());
/* 1382:2318 */     if (getUseKDTree())
/* 1383:     */     {
/* 1384:2319 */       result.add("-use-kdtree");
/* 1385:2320 */       result.add("-K");
/* 1386:2321 */       result.add("" + getKDTreeSpec());
/* 1387:     */     }
/* 1388:2324 */     result.add("-C");
/* 1389:2325 */     result.add("" + getCutOffFactor());
/* 1390:2327 */     if (getDistanceF() != null)
/* 1391:     */     {
/* 1392:2328 */       result.add("-D");
/* 1393:2329 */       result.add("" + getDistanceFSpec());
/* 1394:     */     }
/* 1395:2332 */     if ((getInputCenterFile().exists()) && (getInputCenterFile().isFile()))
/* 1396:     */     {
/* 1397:2333 */       result.add("-N");
/* 1398:2334 */       result.add("" + getInputCenterFile());
/* 1399:     */     }
/* 1400:2337 */     if ((getOutputCenterFile().exists()) && (getOutputCenterFile().isFile()))
/* 1401:     */     {
/* 1402:2338 */       result.add("-O");
/* 1403:2339 */       result.add("" + getOutputCenterFile());
/* 1404:     */     }
/* 1405:2342 */     int dL = getDebugLevel();
/* 1406:2343 */     if (dL > 0)
/* 1407:     */     {
/* 1408:2344 */       result.add("-U");
/* 1409:2345 */       result.add("" + getDebugLevel());
/* 1410:     */     }
/* 1411:2348 */     if ((getDebugVectorsFile().exists()) && (getDebugVectorsFile().isFile()))
/* 1412:     */     {
/* 1413:2349 */       result.add("-Y");
/* 1414:2350 */       result.add("" + getDebugVectorsFile());
/* 1415:     */     }
/* 1416:2353 */     Collections.addAll(result, super.getOptions());
/* 1417:     */     
/* 1418:2355 */     return (String[])result.toArray(new String[result.size()]);
/* 1419:     */   }
/* 1420:     */   
/* 1421:     */   public String toString()
/* 1422:     */   {
/* 1423:2365 */     StringBuffer temp = new StringBuffer();
/* 1424:     */     
/* 1425:2367 */     temp.append("\nXMeans\n======\n");
/* 1426:     */     
/* 1427:2369 */     temp.append("Requested iterations            : " + this.m_MaxIterations + "\n");
/* 1428:2370 */     temp.append("Iterations performed            : " + this.m_IterationCount + "\n");
/* 1429:2371 */     if (this.m_KMeansStopped > 0)
/* 1430:     */     {
/* 1431:2372 */       temp.append("kMeans did not converge\n");
/* 1432:2373 */       temp.append("  but was stopped by max-loops " + this.m_KMeansStopped + " times (max kMeans-iter)\n");
/* 1433:     */     }
/* 1434:2376 */     temp.append("Splits prepared                 : " + this.m_NumSplits + "\n");
/* 1435:2377 */     temp.append("Splits performed                : " + this.m_NumSplitsDone + "\n");
/* 1436:2378 */     temp.append("Cutoff factor                   : " + this.m_CutOffFactor + "\n");
/* 1437:     */     double perc;
/* 1438:     */     double perc;
/* 1439:2380 */     if (this.m_NumSplitsDone > 0) {
/* 1440:2381 */       perc = this.m_NumSplitsStillDone / this.m_NumSplitsDone * 100.0D;
/* 1441:     */     } else {
/* 1442:2383 */       perc = 0.0D;
/* 1443:     */     }
/* 1444:2385 */     temp.append("Percentage of splits accepted \nby cutoff factor                : " + Utils.doubleToString(perc, 2) + " %\n");
/* 1445:     */     
/* 1446:     */ 
/* 1447:2388 */     temp.append("------\n");
/* 1448:     */     
/* 1449:2390 */     temp.append("Cutoff factor                   : " + this.m_CutOffFactor + "\n");
/* 1450:2391 */     temp.append("------\n");
/* 1451:2392 */     temp.append("\nCluster centers                 : " + this.m_NumClusters + " centers\n");
/* 1452:2394 */     for (int i = 0; i < this.m_NumClusters; i++)
/* 1453:     */     {
/* 1454:2395 */       temp.append("\nCluster " + i + "\n           ");
/* 1455:2396 */       for (int j = 0; j < this.m_ClusterCenters.numAttributes(); j++) {
/* 1456:2397 */         if (this.m_ClusterCenters.attribute(j).isNominal()) {
/* 1457:2398 */           temp.append(" " + this.m_ClusterCenters.attribute(j).value((int)this.m_ClusterCenters.instance(i).value(j)));
/* 1458:     */         } else {
/* 1459:2402 */           temp.append(" " + this.m_ClusterCenters.instance(i).value(j));
/* 1460:     */         }
/* 1461:     */       }
/* 1462:     */     }
/* 1463:2406 */     if (this.m_Mle != null) {
/* 1464:2407 */       temp.append("\n\nDistortion: " + Utils.doubleToString(Utils.sum(this.m_Mle), 6) + "\n");
/* 1465:     */     }
/* 1466:2410 */     temp.append("BIC-Value : " + Utils.doubleToString(this.m_Bic, 6) + "\n");
/* 1467:2411 */     return temp.toString();
/* 1468:     */   }
/* 1469:     */   
/* 1470:     */   public Instances getClusterCenters()
/* 1471:     */   {
/* 1472:2420 */     return this.m_ClusterCenters;
/* 1473:     */   }
/* 1474:     */   
/* 1475:     */   protected void PrCentersFD(int debugLevel)
/* 1476:     */   {
/* 1477:2429 */     if (debugLevel == this.m_DebugLevel) {
/* 1478:2430 */       for (int i = 0; i < this.m_ClusterCenters.numInstances(); i++) {
/* 1479:2431 */         System.out.println(this.m_ClusterCenters.instance(i));
/* 1480:     */       }
/* 1481:     */     }
/* 1482:     */   }
/* 1483:     */   
/* 1484:     */   protected boolean TFD(int debugLevel)
/* 1485:     */   {
/* 1486:2443 */     return debugLevel == this.m_DebugLevel;
/* 1487:     */   }
/* 1488:     */   
/* 1489:     */   protected void PFD(int debugLevel, String output)
/* 1490:     */   {
/* 1491:2453 */     if (debugLevel == this.m_DebugLevel) {
/* 1492:2454 */       System.out.println(output);
/* 1493:     */     }
/* 1494:     */   }
/* 1495:     */   
/* 1496:     */   protected void PFD_CURR(String output)
/* 1497:     */   {
/* 1498:2464 */     if (this.m_CurrDebugFlag) {
/* 1499:2465 */       System.out.println(output);
/* 1500:     */     }
/* 1501:     */   }
/* 1502:     */   
/* 1503:     */   public String getRevision()
/* 1504:     */   {
/* 1505:2476 */     return RevisionUtils.extract("$Revision: 10390 $");
/* 1506:     */   }
/* 1507:     */   
/* 1508:     */   public static void main(String[] argv)
/* 1509:     */   {
/* 1510:2485 */     runClusterer(new XMeans(), argv);
/* 1511:     */   }
/* 1512:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.clusterers.XMeans
 * JD-Core Version:    0.7.0.1
 */