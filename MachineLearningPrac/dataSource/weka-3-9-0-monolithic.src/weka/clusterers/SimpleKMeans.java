/*    1:     */ package weka.clusterers;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.util.ArrayList;
/*    5:     */ import java.util.Collections;
/*    6:     */ import java.util.Enumeration;
/*    7:     */ import java.util.HashMap;
/*    8:     */ import java.util.List;
/*    9:     */ import java.util.Random;
/*   10:     */ import java.util.Vector;
/*   11:     */ import java.util.concurrent.Callable;
/*   12:     */ import java.util.concurrent.ExecutorService;
/*   13:     */ import java.util.concurrent.Executors;
/*   14:     */ import java.util.concurrent.Future;
/*   15:     */ import weka.classifiers.rules.DecisionTableHashKey;
/*   16:     */ import weka.core.Attribute;
/*   17:     */ import weka.core.Capabilities;
/*   18:     */ import weka.core.Capabilities.Capability;
/*   19:     */ import weka.core.DenseInstance;
/*   20:     */ import weka.core.DistanceFunction;
/*   21:     */ import weka.core.EuclideanDistance;
/*   22:     */ import weka.core.Instance;
/*   23:     */ import weka.core.Instances;
/*   24:     */ import weka.core.ManhattanDistance;
/*   25:     */ import weka.core.Option;
/*   26:     */ import weka.core.RevisionUtils;
/*   27:     */ import weka.core.SelectedTag;
/*   28:     */ import weka.core.Tag;
/*   29:     */ import weka.core.TechnicalInformation;
/*   30:     */ import weka.core.TechnicalInformation.Field;
/*   31:     */ import weka.core.TechnicalInformation.Type;
/*   32:     */ import weka.core.TechnicalInformationHandler;
/*   33:     */ import weka.core.Utils;
/*   34:     */ import weka.core.WeightedInstancesHandler;
/*   35:     */ import weka.filters.Filter;
/*   36:     */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*   37:     */ 
/*   38:     */ public class SimpleKMeans
/*   39:     */   extends RandomizableClusterer
/*   40:     */   implements NumberOfClustersRequestable, WeightedInstancesHandler, TechnicalInformationHandler
/*   41:     */ {
/*   42:     */   static final long serialVersionUID = -3235809600124455376L;
/*   43:     */   protected ReplaceMissingValues m_ReplaceMissingFilter;
/*   44: 219 */   protected int m_NumClusters = 2;
/*   45:     */   protected Instances m_initialStartPoints;
/*   46:     */   protected Instances m_ClusterCentroids;
/*   47:     */   protected Instances m_ClusterStdDevs;
/*   48:     */   protected double[][][] m_ClusterNominalCounts;
/*   49:     */   protected double[][] m_ClusterMissingCounts;
/*   50:     */   protected double[] m_FullMeansOrMediansOrModes;
/*   51:     */   protected double[] m_FullStdDevs;
/*   52:     */   protected double[][] m_FullNominalCounts;
/*   53:     */   protected double[] m_FullMissingCounts;
/*   54:     */   protected boolean m_displayStdDevs;
/*   55: 263 */   protected boolean m_dontReplaceMissing = false;
/*   56:     */   protected double[] m_ClusterSizes;
/*   57: 273 */   protected int m_MaxIterations = 500;
/*   58: 278 */   protected int m_Iterations = 0;
/*   59:     */   protected double[] m_squaredErrors;
/*   60: 286 */   protected DistanceFunction m_DistanceFunction = new EuclideanDistance();
/*   61: 291 */   protected boolean m_PreserveOrder = false;
/*   62: 296 */   protected int[] m_Assignments = null;
/*   63: 299 */   protected boolean m_FastDistanceCalc = false;
/*   64:     */   public static final int RANDOM = 0;
/*   65:     */   public static final int KMEANS_PLUS_PLUS = 1;
/*   66:     */   public static final int CANOPY = 2;
/*   67:     */   public static final int FARTHEST_FIRST = 3;
/*   68: 307 */   public static final Tag[] TAGS_SELECTION = { new Tag(0, "Random"), new Tag(1, "k-means++"), new Tag(2, "Canopy"), new Tag(3, "Farthest first") };
/*   69: 312 */   protected int m_initializationMethod = 0;
/*   70: 318 */   protected boolean m_speedUpDistanceCompWithCanopies = false;
/*   71:     */   protected List<long[]> m_centroidCanopyAssignments;
/*   72:     */   protected List<long[]> m_dataPointCanopyAssignments;
/*   73:     */   protected Canopy m_canopyClusters;
/*   74: 333 */   protected int m_maxCanopyCandidates = 100;
/*   75: 339 */   protected int m_periodicPruningRate = 10000;
/*   76: 345 */   protected double m_minClusterDensity = 2.0D;
/*   77: 348 */   protected double m_t2 = -1.0D;
/*   78: 351 */   protected double m_t1 = -1.25D;
/*   79: 354 */   protected int m_executionSlots = 1;
/*   80:     */   protected transient ExecutorService m_executorPool;
/*   81:     */   protected int m_completed;
/*   82:     */   protected int m_failed;
/*   83:     */   
/*   84:     */   public SimpleKMeans()
/*   85:     */   {
/*   86: 365 */     this.m_SeedDefault = 10;
/*   87: 366 */     setSeed(this.m_SeedDefault);
/*   88:     */   }
/*   89:     */   
/*   90:     */   protected void startExecutorPool()
/*   91:     */   {
/*   92: 373 */     if (this.m_executorPool != null) {
/*   93: 374 */       this.m_executorPool.shutdownNow();
/*   94:     */     }
/*   95: 377 */     this.m_executorPool = Executors.newFixedThreadPool(this.m_executionSlots);
/*   96:     */   }
/*   97:     */   
/*   98:     */   public TechnicalInformation getTechnicalInformation()
/*   99:     */   {
/*  100: 387 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  101: 388 */     result.setValue(TechnicalInformation.Field.AUTHOR, "D. Arthur and S. Vassilvitskii");
/*  102: 389 */     result.setValue(TechnicalInformation.Field.TITLE, "k-means++: the advantages of carefull seeding");
/*  103:     */     
/*  104: 391 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Proceedings of the eighteenth annual ACM-SIAM symposium on Discrete algorithms");
/*  105:     */     
/*  106: 393 */     result.setValue(TechnicalInformation.Field.YEAR, "2007");
/*  107: 394 */     result.setValue(TechnicalInformation.Field.PAGES, "1027-1035");
/*  108:     */     
/*  109: 396 */     return result;
/*  110:     */   }
/*  111:     */   
/*  112:     */   public String globalInfo()
/*  113:     */   {
/*  114: 406 */     return "Cluster data using the k means algorithm. Can use either the Euclidean distance (default) or the Manhattan distance. If the Manhattan distance is used, then centroids are computed as the component-wise median rather than mean. For more information see:\n\n" + getTechnicalInformation().toString();
/*  115:     */   }
/*  116:     */   
/*  117:     */   public Capabilities getCapabilities()
/*  118:     */   {
/*  119: 420 */     Capabilities result = super.getCapabilities();
/*  120: 421 */     result.disableAll();
/*  121: 422 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  122:     */     
/*  123:     */ 
/*  124: 425 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  125: 426 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  126: 427 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  127:     */     
/*  128: 429 */     return result;
/*  129:     */   }
/*  130:     */   
/*  131:     */   private class KMeansComputeCentroidTask
/*  132:     */     implements Callable<double[]>
/*  133:     */   {
/*  134:     */     protected Instances m_cluster;
/*  135:     */     protected int m_centroidIndex;
/*  136:     */     
/*  137:     */     public KMeansComputeCentroidTask(int centroidIndex, Instances cluster)
/*  138:     */     {
/*  139: 438 */       this.m_cluster = cluster;
/*  140: 439 */       this.m_centroidIndex = centroidIndex;
/*  141:     */     }
/*  142:     */     
/*  143:     */     public double[] call()
/*  144:     */     {
/*  145: 444 */       return SimpleKMeans.this.moveCentroid(this.m_centroidIndex, this.m_cluster, true, false);
/*  146:     */     }
/*  147:     */   }
/*  148:     */   
/*  149:     */   protected int launchMoveCentroids(Instances[] clusters)
/*  150:     */   {
/*  151: 455 */     int emptyClusterCount = 0;
/*  152: 456 */     List<Future<double[]>> results = new ArrayList();
/*  153: 458 */     for (int i = 0; i < this.m_NumClusters; i++) {
/*  154: 459 */       if (clusters[i].numInstances() == 0)
/*  155:     */       {
/*  156: 460 */         emptyClusterCount++;
/*  157:     */       }
/*  158:     */       else
/*  159:     */       {
/*  160: 462 */         Future<double[]> futureCentroid = this.m_executorPool.submit(new KMeansComputeCentroidTask(i, clusters[i]));
/*  161:     */         
/*  162: 464 */         results.add(futureCentroid);
/*  163:     */       }
/*  164:     */     }
/*  165:     */     try
/*  166:     */     {
/*  167: 469 */       for (Future<double[]> d : results) {
/*  168: 470 */         this.m_ClusterCentroids.add(new DenseInstance(1.0D, (double[])d.get()));
/*  169:     */       }
/*  170:     */     }
/*  171:     */     catch (Exception ex)
/*  172:     */     {
/*  173: 473 */       ex.printStackTrace();
/*  174:     */     }
/*  175: 476 */     return emptyClusterCount;
/*  176:     */   }
/*  177:     */   
/*  178:     */   private class KMeansClusterTask
/*  179:     */     implements Callable<Boolean>
/*  180:     */   {
/*  181:     */     protected int m_start;
/*  182:     */     protected int m_end;
/*  183:     */     protected Instances m_inst;
/*  184:     */     protected int[] m_clusterAssignments;
/*  185:     */     
/*  186:     */     public KMeansClusterTask(Instances inst, int start, int end, int[] clusterAssignments)
/*  187:     */     {
/*  188: 488 */       this.m_start = start;
/*  189: 489 */       this.m_end = end;
/*  190: 490 */       this.m_inst = inst;
/*  191: 491 */       this.m_clusterAssignments = clusterAssignments;
/*  192:     */     }
/*  193:     */     
/*  194:     */     public Boolean call()
/*  195:     */     {
/*  196: 496 */       boolean converged = true;
/*  197: 497 */       for (int i = this.m_start; i < this.m_end; i++)
/*  198:     */       {
/*  199: 498 */         Instance toCluster = this.m_inst.instance(i);
/*  200: 499 */         long[] instanceCanopies = SimpleKMeans.this.m_speedUpDistanceCompWithCanopies ? (long[])SimpleKMeans.this.m_dataPointCanopyAssignments.get(i) : null;
/*  201:     */         
/*  202:     */ 
/*  203: 502 */         int newC = clusterInstance(toCluster, instanceCanopies);
/*  204: 503 */         if (newC != this.m_clusterAssignments[i]) {
/*  205: 504 */           converged = false;
/*  206:     */         }
/*  207: 506 */         this.m_clusterAssignments[i] = newC;
/*  208:     */       }
/*  209: 509 */       return Boolean.valueOf(converged);
/*  210:     */     }
/*  211:     */     
/*  212:     */     protected int clusterInstance(Instance inst, long[] instanceCanopies)
/*  213:     */     {
/*  214: 513 */       double minDist = 2147483647.0D;
/*  215: 514 */       int bestCluster = 0;
/*  216: 515 */       for (int i = 0; i < SimpleKMeans.this.m_NumClusters; i++) {
/*  217: 518 */         if ((SimpleKMeans.this.m_speedUpDistanceCompWithCanopies) && (instanceCanopies != null) && (instanceCanopies.length > 0))
/*  218:     */         {
/*  219:     */           try
/*  220:     */           {
/*  221: 521 */             if (!Canopy.nonEmptyCanopySetIntersection((long[])SimpleKMeans.this.m_centroidCanopyAssignments.get(i), instanceCanopies)) {
/*  222:     */               continue;
/*  223:     */             }
/*  224:     */           }
/*  225:     */           catch (Exception ex)
/*  226:     */           {
/*  227: 528 */             ex.printStackTrace();
/*  228:     */           }
/*  229:     */         }
/*  230:     */         else
/*  231:     */         {
/*  232: 532 */           double dist = SimpleKMeans.this.m_DistanceFunction.distance(inst, SimpleKMeans.this.m_ClusterCentroids.instance(i), minDist);
/*  233: 536 */           if (dist < minDist)
/*  234:     */           {
/*  235: 537 */             minDist = dist;
/*  236: 538 */             bestCluster = i;
/*  237:     */           }
/*  238:     */         }
/*  239:     */       }
/*  240: 542 */       return bestCluster;
/*  241:     */     }
/*  242:     */   }
/*  243:     */   
/*  244:     */   protected boolean launchAssignToClusters(Instances insts, int[] clusterAssignments)
/*  245:     */     throws Exception
/*  246:     */   {
/*  247: 556 */     int numPerTask = insts.numInstances() / this.m_executionSlots;
/*  248:     */     
/*  249: 558 */     List<Future<Boolean>> results = new ArrayList();
/*  250: 559 */     for (int i = 0; i < this.m_executionSlots; i++)
/*  251:     */     {
/*  252: 560 */       int start = i * numPerTask;
/*  253: 561 */       int end = start + numPerTask;
/*  254: 562 */       if (i == this.m_executionSlots - 1) {
/*  255: 563 */         end = insts.numInstances();
/*  256:     */       }
/*  257: 566 */       Future<Boolean> futureKM = this.m_executorPool.submit(new KMeansClusterTask(insts, start, end, clusterAssignments));
/*  258:     */       
/*  259:     */ 
/*  260: 569 */       results.add(futureKM);
/*  261:     */     }
/*  262: 572 */     boolean converged = true;
/*  263: 573 */     for (Future<Boolean> f : results) {
/*  264: 574 */       if (!((Boolean)f.get()).booleanValue()) {
/*  265: 575 */         converged = false;
/*  266:     */       }
/*  267:     */     }
/*  268: 579 */     return converged;
/*  269:     */   }
/*  270:     */   
/*  271:     */   public void buildClusterer(Instances data)
/*  272:     */     throws Exception
/*  273:     */   {
/*  274: 592 */     this.m_canopyClusters = null;
/*  275:     */     
/*  276:     */ 
/*  277: 595 */     getCapabilities().testWithFail(data);
/*  278:     */     
/*  279: 597 */     this.m_Iterations = 0;
/*  280:     */     
/*  281: 599 */     this.m_ReplaceMissingFilter = new ReplaceMissingValues();
/*  282: 600 */     Instances instances = new Instances(data);
/*  283:     */     
/*  284: 602 */     instances.setClassIndex(-1);
/*  285: 603 */     if (!this.m_dontReplaceMissing)
/*  286:     */     {
/*  287: 604 */       this.m_ReplaceMissingFilter.setInputFormat(instances);
/*  288: 605 */       instances = Filter.useFilter(instances, this.m_ReplaceMissingFilter);
/*  289:     */     }
/*  290: 608 */     this.m_ClusterNominalCounts = new double[this.m_NumClusters][instances.numAttributes()][];
/*  291: 609 */     this.m_ClusterMissingCounts = new double[this.m_NumClusters][instances.numAttributes()];
/*  292: 610 */     if (this.m_displayStdDevs) {
/*  293: 611 */       this.m_FullStdDevs = instances.variances();
/*  294:     */     }
/*  295: 614 */     this.m_FullMeansOrMediansOrModes = moveCentroid(0, instances, true, false);
/*  296:     */     
/*  297: 616 */     this.m_FullMissingCounts = this.m_ClusterMissingCounts[0];
/*  298: 617 */     this.m_FullNominalCounts = this.m_ClusterNominalCounts[0];
/*  299: 618 */     double sumOfWeights = instances.sumOfWeights();
/*  300: 619 */     for (int i = 0; i < instances.numAttributes(); i++) {
/*  301: 620 */       if (instances.attribute(i).isNumeric())
/*  302:     */       {
/*  303: 621 */         if (this.m_displayStdDevs) {
/*  304: 622 */           this.m_FullStdDevs[i] = Math.sqrt(this.m_FullStdDevs[i]);
/*  305:     */         }
/*  306: 624 */         if (this.m_FullMissingCounts[i] == sumOfWeights) {
/*  307: 625 */           this.m_FullMeansOrMediansOrModes[i] = (0.0D / 0.0D);
/*  308:     */         }
/*  309:     */       }
/*  310: 628 */       else if (this.m_FullMissingCounts[i] > this.m_FullNominalCounts[i][Utils.maxIndex(this.m_FullNominalCounts[i])])
/*  311:     */       {
/*  312: 630 */         this.m_FullMeansOrMediansOrModes[i] = -1.0D;
/*  313:     */       }
/*  314:     */     }
/*  315: 636 */     this.m_ClusterCentroids = new Instances(instances, this.m_NumClusters);
/*  316: 637 */     int[] clusterAssignments = new int[instances.numInstances()];
/*  317: 639 */     if (this.m_PreserveOrder) {
/*  318: 640 */       this.m_Assignments = clusterAssignments;
/*  319:     */     }
/*  320: 643 */     this.m_DistanceFunction.setInstances(instances);
/*  321:     */     
/*  322: 645 */     Random RandomO = new Random(getSeed());
/*  323:     */     
/*  324: 647 */     HashMap<DecisionTableHashKey, Integer> initC = new HashMap();
/*  325:     */     
/*  326: 649 */     DecisionTableHashKey hk = null;
/*  327:     */     
/*  328: 651 */     Instances initInstances = null;
/*  329: 652 */     if (this.m_PreserveOrder) {
/*  330: 653 */       initInstances = new Instances(instances);
/*  331:     */     } else {
/*  332: 655 */       initInstances = instances;
/*  333:     */     }
/*  334: 658 */     if (this.m_speedUpDistanceCompWithCanopies)
/*  335:     */     {
/*  336: 659 */       this.m_canopyClusters = new Canopy();
/*  337: 660 */       this.m_canopyClusters.setNumClusters(this.m_NumClusters);
/*  338: 661 */       this.m_canopyClusters.setSeed(getSeed());
/*  339: 662 */       this.m_canopyClusters.setT2(getCanopyT2());
/*  340: 663 */       this.m_canopyClusters.setT1(getCanopyT1());
/*  341: 664 */       this.m_canopyClusters.setMaxNumCandidateCanopiesToHoldInMemory(getCanopyMaxNumCanopiesToHoldInMemory());
/*  342:     */       
/*  343: 666 */       this.m_canopyClusters.setPeriodicPruningRate(getCanopyPeriodicPruningRate());
/*  344: 667 */       this.m_canopyClusters.setMinimumCanopyDensity(getCanopyMinimumCanopyDensity());
/*  345: 668 */       this.m_canopyClusters.setDebug(getDebug());
/*  346: 669 */       this.m_canopyClusters.buildClusterer(initInstances);
/*  347:     */       
/*  348: 671 */       this.m_centroidCanopyAssignments = new ArrayList();
/*  349: 672 */       this.m_dataPointCanopyAssignments = new ArrayList();
/*  350:     */     }
/*  351: 675 */     if (this.m_initializationMethod == 1)
/*  352:     */     {
/*  353: 676 */       kMeansPlusPlusInit(initInstances);
/*  354:     */       
/*  355: 678 */       this.m_initialStartPoints = new Instances(this.m_ClusterCentroids);
/*  356:     */     }
/*  357: 679 */     else if (this.m_initializationMethod == 2)
/*  358:     */     {
/*  359: 680 */       canopyInit(initInstances);
/*  360:     */       
/*  361: 682 */       this.m_initialStartPoints = new Instances(this.m_canopyClusters.getCanopies());
/*  362:     */     }
/*  363: 683 */     else if (this.m_initializationMethod == 3)
/*  364:     */     {
/*  365: 684 */       farthestFirstInit(initInstances);
/*  366:     */       
/*  367: 686 */       this.m_initialStartPoints = new Instances(this.m_ClusterCentroids);
/*  368:     */     }
/*  369:     */     else
/*  370:     */     {
/*  371: 689 */       for (int j = initInstances.numInstances() - 1; j >= 0; j--)
/*  372:     */       {
/*  373: 690 */         int instIndex = RandomO.nextInt(j + 1);
/*  374: 691 */         hk = new DecisionTableHashKey(initInstances.instance(instIndex), initInstances.numAttributes(), true);
/*  375: 694 */         if (!initC.containsKey(hk))
/*  376:     */         {
/*  377: 695 */           this.m_ClusterCentroids.add(initInstances.instance(instIndex));
/*  378: 696 */           initC.put(hk, null);
/*  379:     */         }
/*  380: 698 */         initInstances.swap(j, instIndex);
/*  381: 700 */         if (this.m_ClusterCentroids.numInstances() == this.m_NumClusters) {
/*  382:     */           break;
/*  383:     */         }
/*  384:     */       }
/*  385: 705 */       this.m_initialStartPoints = new Instances(this.m_ClusterCentroids);
/*  386:     */     }
/*  387: 708 */     if (this.m_speedUpDistanceCompWithCanopies) {
/*  388: 710 */       for (int i = 0; i < instances.numInstances(); i++) {
/*  389: 711 */         this.m_dataPointCanopyAssignments.add(this.m_canopyClusters.assignCanopies(instances.instance(i)));
/*  390:     */       }
/*  391:     */     }
/*  392: 716 */     this.m_NumClusters = this.m_ClusterCentroids.numInstances();
/*  393:     */     
/*  394:     */ 
/*  395: 719 */     initInstances = null;
/*  396:     */     
/*  397:     */ 
/*  398: 722 */     boolean converged = false;
/*  399:     */     
/*  400: 724 */     Instances[] tempI = new Instances[this.m_NumClusters];
/*  401: 725 */     this.m_squaredErrors = new double[this.m_NumClusters];
/*  402: 726 */     this.m_ClusterNominalCounts = new double[this.m_NumClusters][instances.numAttributes()][0];
/*  403: 727 */     this.m_ClusterMissingCounts = new double[this.m_NumClusters][instances.numAttributes()];
/*  404: 728 */     startExecutorPool();
/*  405: 730 */     while (!converged)
/*  406:     */     {
/*  407: 731 */       if (this.m_speedUpDistanceCompWithCanopies)
/*  408:     */       {
/*  409: 733 */         this.m_centroidCanopyAssignments.clear();
/*  410: 734 */         for (int kk = 0; kk < this.m_ClusterCentroids.numInstances(); kk++) {
/*  411: 735 */           this.m_centroidCanopyAssignments.add(this.m_canopyClusters.assignCanopies(this.m_ClusterCentroids.instance(kk)));
/*  412:     */         }
/*  413:     */       }
/*  414: 740 */       int emptyClusterCount = 0;
/*  415: 741 */       this.m_Iterations += 1;
/*  416: 742 */       converged = true;
/*  417:     */       int i;
/*  418: 744 */       if ((this.m_executionSlots <= 1) || (instances.numInstances() < 2 * this.m_executionSlots)) {
/*  419: 746 */         for (i = 0; i < instances.numInstances();)
/*  420:     */         {
/*  421: 747 */           Instance toCluster = instances.instance(i);
/*  422: 748 */           int newC = clusterProcessedInstance(toCluster, false, true, this.m_speedUpDistanceCompWithCanopies ? (long[])this.m_dataPointCanopyAssignments.get(i) : null);
/*  423: 755 */           if (newC != clusterAssignments[i]) {
/*  424: 756 */             converged = false;
/*  425:     */           }
/*  426: 758 */           clusterAssignments[i] = newC;i++; continue;
/*  427:     */           
/*  428:     */ 
/*  429: 761 */           converged = launchAssignToClusters(instances, clusterAssignments);
/*  430:     */         }
/*  431:     */       }
/*  432: 765 */       this.m_ClusterCentroids = new Instances(instances, this.m_NumClusters);
/*  433: 766 */       for (int i = 0; i < this.m_NumClusters; i++) {
/*  434: 767 */         tempI[i] = new Instances(instances, 0);
/*  435:     */       }
/*  436: 769 */       for (i = 0; i < instances.numInstances(); i++) {
/*  437: 770 */         tempI[clusterAssignments[i]].add(instances.instance(i));
/*  438:     */       }
/*  439: 772 */       if ((this.m_executionSlots <= 1) || (instances.numInstances() < 2 * this.m_executionSlots)) {
/*  440: 774 */         for (i = 0; i < this.m_NumClusters;)
/*  441:     */         {
/*  442: 775 */           if (tempI[i].numInstances() == 0) {
/*  443: 777 */             emptyClusterCount++;
/*  444:     */           } else {
/*  445: 779 */             moveCentroid(i, tempI[i], true, true);
/*  446:     */           }
/*  447: 774 */           i++; continue;
/*  448:     */           
/*  449:     */ 
/*  450:     */ 
/*  451:     */ 
/*  452:     */ 
/*  453:     */ 
/*  454:     */ 
/*  455:     */ 
/*  456: 783 */           emptyClusterCount = launchMoveCentroids(tempI);
/*  457:     */         }
/*  458:     */       }
/*  459: 786 */       if (this.m_Iterations == this.m_MaxIterations) {
/*  460: 787 */         converged = true;
/*  461:     */       }
/*  462: 790 */       if (emptyClusterCount > 0)
/*  463:     */       {
/*  464: 791 */         this.m_NumClusters -= emptyClusterCount;
/*  465: 792 */         if (converged)
/*  466:     */         {
/*  467: 793 */           Instances[] t = new Instances[this.m_NumClusters];
/*  468: 794 */           int index = 0;
/*  469: 795 */           for (int k = 0; k < tempI.length; k++) {
/*  470: 796 */             if (tempI[k].numInstances() > 0)
/*  471:     */             {
/*  472: 797 */               t[index] = tempI[k];
/*  473: 799 */               for (i = 0; i < tempI[k].numAttributes(); i++) {
/*  474: 800 */                 this.m_ClusterNominalCounts[index][i] = this.m_ClusterNominalCounts[k][i];
/*  475:     */               }
/*  476: 802 */               index++;
/*  477:     */             }
/*  478:     */           }
/*  479: 805 */           tempI = t;
/*  480:     */         }
/*  481:     */         else
/*  482:     */         {
/*  483: 807 */           tempI = new Instances[this.m_NumClusters];
/*  484:     */         }
/*  485:     */       }
/*  486: 811 */       if (!converged) {
/*  487: 812 */         this.m_ClusterNominalCounts = new double[this.m_NumClusters][instances.numAttributes()][0];
/*  488:     */       }
/*  489:     */     }
/*  490: 817 */     if (!this.m_FastDistanceCalc) {
/*  491: 818 */       for (int i = 0; i < instances.numInstances(); i++) {
/*  492: 819 */         clusterProcessedInstance(instances.instance(i), true, false, null);
/*  493:     */       }
/*  494:     */     }
/*  495: 823 */     if (this.m_displayStdDevs) {
/*  496: 824 */       this.m_ClusterStdDevs = new Instances(instances, this.m_NumClusters);
/*  497:     */     }
/*  498: 826 */     this.m_ClusterSizes = new double[this.m_NumClusters];
/*  499: 827 */     for (int i = 0; i < this.m_NumClusters; i++)
/*  500:     */     {
/*  501: 828 */       if (this.m_displayStdDevs)
/*  502:     */       {
/*  503: 829 */         double[] vals2 = tempI[i].variances();
/*  504: 830 */         for (int j = 0; j < instances.numAttributes(); j++) {
/*  505: 831 */           if (instances.attribute(j).isNumeric()) {
/*  506: 832 */             vals2[j] = Math.sqrt(vals2[j]);
/*  507:     */           } else {
/*  508: 834 */             vals2[j] = Utils.missingValue();
/*  509:     */           }
/*  510:     */         }
/*  511: 837 */         this.m_ClusterStdDevs.add(new DenseInstance(1.0D, vals2));
/*  512:     */       }
/*  513: 839 */       this.m_ClusterSizes[i] = tempI[i].sumOfWeights();
/*  514:     */     }
/*  515: 842 */     this.m_executorPool.shutdown();
/*  516:     */     
/*  517:     */ 
/*  518: 845 */     this.m_DistanceFunction.clean();
/*  519:     */   }
/*  520:     */   
/*  521:     */   protected void canopyInit(Instances data)
/*  522:     */     throws Exception
/*  523:     */   {
/*  524: 855 */     if (this.m_canopyClusters == null)
/*  525:     */     {
/*  526: 856 */       this.m_canopyClusters = new Canopy();
/*  527: 857 */       this.m_canopyClusters.setNumClusters(this.m_NumClusters);
/*  528: 858 */       this.m_canopyClusters.setSeed(getSeed());
/*  529: 859 */       this.m_canopyClusters.setT2(getCanopyT2());
/*  530: 860 */       this.m_canopyClusters.setT1(getCanopyT1());
/*  531: 861 */       this.m_canopyClusters.setMaxNumCandidateCanopiesToHoldInMemory(getCanopyMaxNumCanopiesToHoldInMemory());
/*  532:     */       
/*  533: 863 */       this.m_canopyClusters.setPeriodicPruningRate(getCanopyPeriodicPruningRate());
/*  534: 864 */       this.m_canopyClusters.setMinimumCanopyDensity(getCanopyMinimumCanopyDensity());
/*  535: 865 */       this.m_canopyClusters.setDebug(getDebug());
/*  536: 866 */       this.m_canopyClusters.buildClusterer(data);
/*  537:     */     }
/*  538: 868 */     this.m_ClusterCentroids = this.m_canopyClusters.getCanopies();
/*  539:     */   }
/*  540:     */   
/*  541:     */   protected void farthestFirstInit(Instances data)
/*  542:     */     throws Exception
/*  543:     */   {
/*  544: 878 */     FarthestFirst ff = new FarthestFirst();
/*  545: 879 */     ff.setNumClusters(this.m_NumClusters);
/*  546: 880 */     ff.buildClusterer(data);
/*  547:     */     
/*  548: 882 */     this.m_ClusterCentroids = ff.getClusterCentroids();
/*  549:     */   }
/*  550:     */   
/*  551:     */   protected void kMeansPlusPlusInit(Instances data)
/*  552:     */     throws Exception
/*  553:     */   {
/*  554: 892 */     Random randomO = new Random(getSeed());
/*  555: 893 */     HashMap<DecisionTableHashKey, String> initC = new HashMap();
/*  556:     */     
/*  557:     */ 
/*  558:     */ 
/*  559: 897 */     int index = randomO.nextInt(data.numInstances());
/*  560: 898 */     this.m_ClusterCentroids.add(data.instance(index));
/*  561: 899 */     DecisionTableHashKey hk = new DecisionTableHashKey(data.instance(index), data.numAttributes(), true);
/*  562:     */     
/*  563: 901 */     initC.put(hk, null);
/*  564:     */     
/*  565: 903 */     int iteration = 0;
/*  566: 904 */     int remainingInstances = data.numInstances() - 1;
/*  567: 905 */     if (this.m_NumClusters > 1)
/*  568:     */     {
/*  569: 909 */       double[] distances = new double[data.numInstances()];
/*  570: 910 */       double[] cumProbs = new double[data.numInstances()];
/*  571: 911 */       for (int i = 0; i < data.numInstances(); i++) {
/*  572: 912 */         distances[i] = this.m_DistanceFunction.distance(data.instance(i), this.m_ClusterCentroids.instance(iteration));
/*  573:     */       }
/*  574: 918 */       for (int i = 1; i < this.m_NumClusters; i++)
/*  575:     */       {
/*  576: 921 */         double[] weights = new double[data.numInstances()];
/*  577: 922 */         System.arraycopy(distances, 0, weights, 0, distances.length);
/*  578: 923 */         Utils.normalize(weights);
/*  579:     */         
/*  580: 925 */         double sumOfProbs = 0.0D;
/*  581: 926 */         for (int k = 0; k < data.numInstances(); k++)
/*  582:     */         {
/*  583: 927 */           sumOfProbs += weights[k];
/*  584: 928 */           cumProbs[k] = sumOfProbs;
/*  585:     */         }
/*  586: 931 */         cumProbs[(data.numInstances() - 1)] = 1.0D;
/*  587:     */         
/*  588:     */ 
/*  589:     */ 
/*  590: 935 */         double prob = randomO.nextDouble();
/*  591: 936 */         for (int k = 0; k < cumProbs.length; k++) {
/*  592: 937 */           if (prob < cumProbs[k])
/*  593:     */           {
/*  594: 938 */             Instance candidateCenter = data.instance(k);
/*  595: 939 */             hk = new DecisionTableHashKey(candidateCenter, data.numAttributes(), true);
/*  596: 942 */             if (!initC.containsKey(hk))
/*  597:     */             {
/*  598: 943 */               initC.put(hk, null);
/*  599: 944 */               this.m_ClusterCentroids.add(candidateCenter);
/*  600:     */             }
/*  601:     */             else
/*  602:     */             {
/*  603: 951 */               System.err.println("We shouldn't get here....");
/*  604:     */             }
/*  605: 953 */             remainingInstances--;
/*  606: 954 */             break;
/*  607:     */           }
/*  608:     */         }
/*  609: 957 */         iteration++;
/*  610: 959 */         if (remainingInstances == 0) {
/*  611:     */           break;
/*  612:     */         }
/*  613: 965 */         for (int k = 0; k < data.numInstances(); k++) {
/*  614: 966 */           if (distances[k] > 0.0D)
/*  615:     */           {
/*  616: 967 */             double newDist = this.m_DistanceFunction.distance(data.instance(k), this.m_ClusterCentroids.instance(iteration));
/*  617: 970 */             if (newDist < distances[k]) {
/*  618: 971 */               distances[k] = newDist;
/*  619:     */             }
/*  620:     */           }
/*  621:     */         }
/*  622:     */       }
/*  623:     */     }
/*  624:     */   }
/*  625:     */   
/*  626:     */   protected double[] moveCentroid(int centroidIndex, Instances members, boolean updateClusterInfo, boolean addToCentroidInstances)
/*  627:     */   {
/*  628: 997 */     double[] vals = new double[members.numAttributes()];
/*  629: 998 */     double[][] nominalDists = new double[members.numAttributes()][];
/*  630: 999 */     double[] weightMissing = new double[members.numAttributes()];
/*  631:1000 */     double[] weightNonMissing = new double[members.numAttributes()];
/*  632:1003 */     for (int j = 0; j < members.numAttributes(); j++) {
/*  633:1004 */       if (members.attribute(j).isNominal()) {
/*  634:1005 */         nominalDists[j] = new double[members.attribute(j).numValues()];
/*  635:     */       }
/*  636:     */     }
/*  637:1008 */     for (Instance inst : members) {
/*  638:1009 */       for (int j = 0; j < members.numAttributes(); j++) {
/*  639:1010 */         if (inst.isMissing(j))
/*  640:     */         {
/*  641:1011 */           weightMissing[j] += inst.weight();
/*  642:     */         }
/*  643:     */         else
/*  644:     */         {
/*  645:1013 */           weightNonMissing[j] += inst.weight();
/*  646:1014 */           if (members.attribute(j).isNumeric()) {
/*  647:1015 */             vals[j] += inst.weight() * inst.value(j);
/*  648:     */           } else {
/*  649:1017 */             nominalDists[j][((int)inst.value(j))] += inst.weight();
/*  650:     */           }
/*  651:     */         }
/*  652:     */       }
/*  653:     */     }
/*  654:1022 */     for (int j = 0; j < members.numAttributes(); j++) {
/*  655:1023 */       if (members.attribute(j).isNumeric())
/*  656:     */       {
/*  657:1024 */         if (weightNonMissing[j] > 0.0D) {
/*  658:1025 */           vals[j] /= weightNonMissing[j];
/*  659:     */         } else {
/*  660:1027 */           vals[j] = Utils.missingValue();
/*  661:     */         }
/*  662:     */       }
/*  663:     */       else
/*  664:     */       {
/*  665:1030 */         double max = -1.797693134862316E+308D;
/*  666:1031 */         double maxIndex = -1.0D;
/*  667:1032 */         for (int i = 0; i < nominalDists[j].length; i++)
/*  668:     */         {
/*  669:1033 */           if (nominalDists[j][i] > max)
/*  670:     */           {
/*  671:1034 */             max = nominalDists[j][i];
/*  672:1035 */             maxIndex = i;
/*  673:     */           }
/*  674:1037 */           if (max < weightMissing[j]) {
/*  675:1038 */             vals[j] = Utils.missingValue();
/*  676:     */           } else {
/*  677:1040 */             vals[j] = maxIndex;
/*  678:     */           }
/*  679:     */         }
/*  680:     */       }
/*  681:     */     }
/*  682:1046 */     if ((this.m_DistanceFunction instanceof ManhattanDistance))
/*  683:     */     {
/*  684:1049 */       Instances sortedMembers = null;
/*  685:1050 */       int middle = (members.numInstances() - 1) / 2;
/*  686:1051 */       boolean dataIsEven = members.numInstances() % 2 == 0;
/*  687:1052 */       if (this.m_PreserveOrder) {
/*  688:1053 */         sortedMembers = members;
/*  689:     */       } else {
/*  690:1055 */         sortedMembers = new Instances(members);
/*  691:     */       }
/*  692:1057 */       for (int j = 0; j < members.numAttributes(); j++) {
/*  693:1058 */         if ((weightNonMissing[j] > 0.0D) && (members.attribute(j).isNumeric())) {
/*  694:1060 */           if (members.numInstances() == 1)
/*  695:     */           {
/*  696:1061 */             vals[j] = members.instance(0).value(j);
/*  697:     */           }
/*  698:     */           else
/*  699:     */           {
/*  700:1063 */             vals[j] = sortedMembers.kthSmallestValue(j, middle + 1);
/*  701:1064 */             if (dataIsEven) {
/*  702:1065 */               vals[j] = ((vals[j] + sortedMembers.kthSmallestValue(j, middle + 2)) / 2.0D);
/*  703:     */             }
/*  704:     */           }
/*  705:     */         }
/*  706:     */       }
/*  707:     */     }
/*  708:1072 */     if (updateClusterInfo) {
/*  709:1073 */       for (int j = 0; j < members.numAttributes(); j++)
/*  710:     */       {
/*  711:1074 */         this.m_ClusterMissingCounts[centroidIndex][j] = weightMissing[j];
/*  712:1075 */         this.m_ClusterNominalCounts[centroidIndex][j] = nominalDists[j];
/*  713:     */       }
/*  714:     */     }
/*  715:1079 */     if (addToCentroidInstances) {
/*  716:1080 */       this.m_ClusterCentroids.add(new DenseInstance(1.0D, vals));
/*  717:     */     }
/*  718:1083 */     return vals;
/*  719:     */   }
/*  720:     */   
/*  721:     */   private int clusterProcessedInstance(Instance instance, boolean updateErrors, boolean useFastDistCalc, long[] instanceCanopies)
/*  722:     */   {
/*  723:1099 */     double minDist = 2147483647.0D;
/*  724:1100 */     int bestCluster = 0;
/*  725:1101 */     for (int i = 0; i < this.m_NumClusters; i++)
/*  726:     */     {
/*  727:     */       double dist;
/*  728:     */       double dist;
/*  729:1103 */       if (useFastDistCalc)
/*  730:     */       {
/*  731:     */         double dist;
/*  732:1104 */         if ((this.m_speedUpDistanceCompWithCanopies) && (instanceCanopies != null) && (instanceCanopies.length > 0))
/*  733:     */         {
/*  734:     */           try
/*  735:     */           {
/*  736:1107 */             if (!Canopy.nonEmptyCanopySetIntersection((long[])this.m_centroidCanopyAssignments.get(i), instanceCanopies)) {
/*  737:     */               continue;
/*  738:     */             }
/*  739:     */           }
/*  740:     */           catch (Exception ex)
/*  741:     */           {
/*  742:1112 */             ex.printStackTrace();
/*  743:     */           }
/*  744:1114 */           dist = this.m_DistanceFunction.distance(instance, this.m_ClusterCentroids.instance(i), minDist);
/*  745:     */         }
/*  746:     */         else
/*  747:     */         {
/*  748:1118 */           dist = this.m_DistanceFunction.distance(instance, this.m_ClusterCentroids.instance(i), minDist);
/*  749:     */         }
/*  750:     */       }
/*  751:     */       else
/*  752:     */       {
/*  753:1123 */         dist = this.m_DistanceFunction.distance(instance, this.m_ClusterCentroids.instance(i));
/*  754:     */       }
/*  755:1126 */       if (dist < minDist)
/*  756:     */       {
/*  757:1127 */         minDist = dist;
/*  758:1128 */         bestCluster = i;
/*  759:     */       }
/*  760:     */     }
/*  761:1131 */     if (updateErrors)
/*  762:     */     {
/*  763:1132 */       if ((this.m_DistanceFunction instanceof EuclideanDistance)) {
/*  764:1134 */         minDist *= minDist * instance.weight();
/*  765:     */       }
/*  766:1136 */       this.m_squaredErrors[bestCluster] += minDist;
/*  767:     */     }
/*  768:1138 */     return bestCluster;
/*  769:     */   }
/*  770:     */   
/*  771:     */   public int clusterInstance(Instance instance)
/*  772:     */     throws Exception
/*  773:     */   {
/*  774:1151 */     Instance inst = null;
/*  775:1152 */     if (!this.m_dontReplaceMissing)
/*  776:     */     {
/*  777:1153 */       this.m_ReplaceMissingFilter.input(instance);
/*  778:1154 */       this.m_ReplaceMissingFilter.batchFinished();
/*  779:1155 */       inst = this.m_ReplaceMissingFilter.output();
/*  780:     */     }
/*  781:     */     else
/*  782:     */     {
/*  783:1157 */       inst = instance;
/*  784:     */     }
/*  785:1160 */     return clusterProcessedInstance(inst, false, true, null);
/*  786:     */   }
/*  787:     */   
/*  788:     */   public int numberOfClusters()
/*  789:     */     throws Exception
/*  790:     */   {
/*  791:1171 */     return this.m_NumClusters;
/*  792:     */   }
/*  793:     */   
/*  794:     */   public Enumeration<Option> listOptions()
/*  795:     */   {
/*  796:1181 */     Vector<Option> result = new Vector();
/*  797:     */     
/*  798:1183 */     result.addElement(new Option("\tNumber of clusters.\n\t(default 2).", "N", 1, "-N <num>"));
/*  799:     */     
/*  800:     */ 
/*  801:1186 */     result.addElement(new Option("\tInitialization method to use.\n\t0 = random, 1 = k-means++, 2 = canopy, 3 = farthest first.\n\t(default = 0)", "init", 1, "-init"));
/*  802:     */     
/*  803:     */ 
/*  804:     */ 
/*  805:     */ 
/*  806:1191 */     result.addElement(new Option("\tUse canopies to reduce the number of distance calculations.", "C", 0, "-C"));
/*  807:     */     
/*  808:     */ 
/*  809:     */ 
/*  810:1195 */     result.addElement(new Option("\tMaximum number of candidate canopies to retain in memory\n\tat any one time when using canopy clustering.\n\tT2 distance plus, data characteristics,\n\twill determine how many candidate canopies are formed before\n\tperiodic and final pruning are performed, which might result\n\tin exceess memory consumption. This setting avoids large numbers\n\tof candidate canopies consuming memory. (default = 100)", "-max-candidates", 1, "-max-candidates <num>"));
/*  811:     */     
/*  812:     */ 
/*  813:     */ 
/*  814:     */ 
/*  815:     */ 
/*  816:     */ 
/*  817:     */ 
/*  818:     */ 
/*  819:     */ 
/*  820:     */ 
/*  821:1206 */     result.addElement(new Option("\tHow often to prune low density canopies when using canopy clustering. \n\t(default = every 10,000 training instances)", "periodic-pruning", 1, "-periodic-pruning <num>"));
/*  822:     */     
/*  823:     */ 
/*  824:     */ 
/*  825:     */ 
/*  826:     */ 
/*  827:     */ 
/*  828:1213 */     result.addElement(new Option("\tMinimum canopy density, when using canopy clustering, below which\n\t a canopy will be pruned during periodic pruning. (default = 2 instances)", "min-density", 1, "-min-density"));
/*  829:     */     
/*  830:     */ 
/*  831:     */ 
/*  832:     */ 
/*  833:     */ 
/*  834:1219 */     result.addElement(new Option("\tThe T2 distance to use when using canopy clustering. Values < 0 indicate that\n\ta heuristic based on attribute std. deviation should be used to set this.\n\t(default = -1.0)", "t2", 1, "-t2"));
/*  835:     */     
/*  836:     */ 
/*  837:     */ 
/*  838:     */ 
/*  839:     */ 
/*  840:1225 */     result.addElement(new Option("\tThe T1 distance to use when using canopy clustering. A value < 0 is taken as a\n\tpositive multiplier for T2. (default = -1.5)", "t1", 1, "-t1"));
/*  841:     */     
/*  842:     */ 
/*  843:     */ 
/*  844:     */ 
/*  845:1230 */     result.addElement(new Option("\tDisplay std. deviations for centroids.\n", "V", 0, "-V"));
/*  846:     */     
/*  847:1232 */     result.addElement(new Option("\tDon't replace missing values with mean/mode.\n", "M", 0, "-M"));
/*  848:     */     
/*  849:     */ 
/*  850:1235 */     result.add(new Option("\tDistance function to use.\n\t(default: weka.core.EuclideanDistance)", "A", 1, "-A <classname and options>"));
/*  851:     */     
/*  852:     */ 
/*  853:     */ 
/*  854:1239 */     result.add(new Option("\tMaximum number of iterations.\n", "I", 1, "-I <num>"));
/*  855:     */     
/*  856:     */ 
/*  857:1242 */     result.addElement(new Option("\tPreserve order of instances.\n", "O", 0, "-O"));
/*  858:     */     
/*  859:     */ 
/*  860:1245 */     result.addElement(new Option("\tEnables faster distance calculations, using cut-off values.\n\tDisables the calculation/output of squared errors/distances.\n", "fast", 0, "-fast"));
/*  861:     */     
/*  862:     */ 
/*  863:     */ 
/*  864:     */ 
/*  865:1250 */     result.addElement(new Option("\tNumber of execution slots.\n\t(default 1 - i.e. no parallelism)", "num-slots", 1, "-num-slots <num>"));
/*  866:     */     
/*  867:     */ 
/*  868:     */ 
/*  869:1254 */     result.addAll(Collections.list(super.listOptions()));
/*  870:     */     
/*  871:1256 */     return result.elements();
/*  872:     */   }
/*  873:     */   
/*  874:     */   public String numClustersTipText()
/*  875:     */   {
/*  876:1266 */     return "set number of clusters";
/*  877:     */   }
/*  878:     */   
/*  879:     */   public void setNumClusters(int n)
/*  880:     */     throws Exception
/*  881:     */   {
/*  882:1277 */     if (n <= 0) {
/*  883:1278 */       throw new Exception("Number of clusters must be > 0");
/*  884:     */     }
/*  885:1280 */     this.m_NumClusters = n;
/*  886:     */   }
/*  887:     */   
/*  888:     */   public int getNumClusters()
/*  889:     */   {
/*  890:1289 */     return this.m_NumClusters;
/*  891:     */   }
/*  892:     */   
/*  893:     */   public String initializationMethodTipText()
/*  894:     */   {
/*  895:1299 */     return "The initialization method to use. Random, k-means++, Canopy or farthest first";
/*  896:     */   }
/*  897:     */   
/*  898:     */   public void setInitializationMethod(SelectedTag method)
/*  899:     */   {
/*  900:1308 */     if (method.getTags() == TAGS_SELECTION) {
/*  901:1309 */       this.m_initializationMethod = method.getSelectedTag().getID();
/*  902:     */     }
/*  903:     */   }
/*  904:     */   
/*  905:     */   public SelectedTag getInitializationMethod()
/*  906:     */   {
/*  907:1319 */     return new SelectedTag(this.m_initializationMethod, TAGS_SELECTION);
/*  908:     */   }
/*  909:     */   
/*  910:     */   public String reduceNumberOfDistanceCalcsViaCanopiesTipText()
/*  911:     */   {
/*  912:1329 */     return "Use canopy clustering to reduce the number of distance calculations performed by k-means";
/*  913:     */   }
/*  914:     */   
/*  915:     */   public void setReduceNumberOfDistanceCalcsViaCanopies(boolean c)
/*  916:     */   {
/*  917:1341 */     this.m_speedUpDistanceCompWithCanopies = c;
/*  918:     */   }
/*  919:     */   
/*  920:     */   public boolean getReduceNumberOfDistanceCalcsViaCanopies()
/*  921:     */   {
/*  922:1352 */     return this.m_speedUpDistanceCompWithCanopies;
/*  923:     */   }
/*  924:     */   
/*  925:     */   public String canopyPeriodicPruningRateTipText()
/*  926:     */   {
/*  927:1362 */     return "If using canopy clustering for initialization and/or speedup this is how often to prune low density canopies during training";
/*  928:     */   }
/*  929:     */   
/*  930:     */   public void setCanopyPeriodicPruningRate(int p)
/*  931:     */   {
/*  932:1373 */     this.m_periodicPruningRate = p;
/*  933:     */   }
/*  934:     */   
/*  935:     */   public int getCanopyPeriodicPruningRate()
/*  936:     */   {
/*  937:1383 */     return this.m_periodicPruningRate;
/*  938:     */   }
/*  939:     */   
/*  940:     */   public String canopyMinimumCanopyDensityTipText()
/*  941:     */   {
/*  942:1393 */     return "If using canopy clustering for initialization and/or speedup this is the minimum T2-based density below which a canopy will be pruned during periodic pruning";
/*  943:     */   }
/*  944:     */   
/*  945:     */   public void setCanopyMinimumCanopyDensity(double dens)
/*  946:     */   {
/*  947:1405 */     this.m_minClusterDensity = dens;
/*  948:     */   }
/*  949:     */   
/*  950:     */   public double getCanopyMinimumCanopyDensity()
/*  951:     */   {
/*  952:1415 */     return this.m_minClusterDensity;
/*  953:     */   }
/*  954:     */   
/*  955:     */   public String canopyMaxNumCanopiesToHoldInMemoryTipText()
/*  956:     */   {
/*  957:1425 */     return "If using canopy clustering for initialization and/or speedup this is the maximum number of candidate canopies to retain in main memory during training of the canopy clusterer. T2 distance and data characteristics determine how many candidate canopies are formed before periodic and final pruning are performed. There may not be enough memory available if T2 is set too low.";
/*  958:     */   }
/*  959:     */   
/*  960:     */   public void setCanopyMaxNumCanopiesToHoldInMemory(int max)
/*  961:     */   {
/*  962:1443 */     this.m_maxCanopyCandidates = max;
/*  963:     */   }
/*  964:     */   
/*  965:     */   public int getCanopyMaxNumCanopiesToHoldInMemory()
/*  966:     */   {
/*  967:1456 */     return this.m_maxCanopyCandidates;
/*  968:     */   }
/*  969:     */   
/*  970:     */   public String canopyT2TipText()
/*  971:     */   {
/*  972:1465 */     return "The T2 distance to use when using canopy clustering. Values < 0 indicate that this should be set using a heuristic based on attribute standard deviation";
/*  973:     */   }
/*  974:     */   
/*  975:     */   public void setCanopyT2(double t2)
/*  976:     */   {
/*  977:1476 */     this.m_t2 = t2;
/*  978:     */   }
/*  979:     */   
/*  980:     */   public double getCanopyT2()
/*  981:     */   {
/*  982:1486 */     return this.m_t2;
/*  983:     */   }
/*  984:     */   
/*  985:     */   public String canopyT1TipText()
/*  986:     */   {
/*  987:1495 */     return "The T1 distance to use when using canopy clustering. Values < 0 are taken as a positive multiplier for the T2 distance";
/*  988:     */   }
/*  989:     */   
/*  990:     */   public void setCanopyT1(double t1)
/*  991:     */   {
/*  992:1506 */     this.m_t1 = t1;
/*  993:     */   }
/*  994:     */   
/*  995:     */   public double getCanopyT1()
/*  996:     */   {
/*  997:1516 */     return this.m_t1;
/*  998:     */   }
/*  999:     */   
/* 1000:     */   public String maxIterationsTipText()
/* 1001:     */   {
/* 1002:1526 */     return "set maximum number of iterations";
/* 1003:     */   }
/* 1004:     */   
/* 1005:     */   public void setMaxIterations(int n)
/* 1006:     */     throws Exception
/* 1007:     */   {
/* 1008:1536 */     if (n <= 0) {
/* 1009:1537 */       throw new Exception("Maximum number of iterations must be > 0");
/* 1010:     */     }
/* 1011:1539 */     this.m_MaxIterations = n;
/* 1012:     */   }
/* 1013:     */   
/* 1014:     */   public int getMaxIterations()
/* 1015:     */   {
/* 1016:1548 */     return this.m_MaxIterations;
/* 1017:     */   }
/* 1018:     */   
/* 1019:     */   public String displayStdDevsTipText()
/* 1020:     */   {
/* 1021:1558 */     return "Display std deviations of numeric attributes and counts of nominal attributes.";
/* 1022:     */   }
/* 1023:     */   
/* 1024:     */   public void setDisplayStdDevs(boolean stdD)
/* 1025:     */   {
/* 1026:1569 */     this.m_displayStdDevs = stdD;
/* 1027:     */   }
/* 1028:     */   
/* 1029:     */   public boolean getDisplayStdDevs()
/* 1030:     */   {
/* 1031:1579 */     return this.m_displayStdDevs;
/* 1032:     */   }
/* 1033:     */   
/* 1034:     */   public String dontReplaceMissingValuesTipText()
/* 1035:     */   {
/* 1036:1589 */     return "Replace missing values globally with mean/mode.";
/* 1037:     */   }
/* 1038:     */   
/* 1039:     */   public void setDontReplaceMissingValues(boolean r)
/* 1040:     */   {
/* 1041:1598 */     this.m_dontReplaceMissing = r;
/* 1042:     */   }
/* 1043:     */   
/* 1044:     */   public boolean getDontReplaceMissingValues()
/* 1045:     */   {
/* 1046:1607 */     return this.m_dontReplaceMissing;
/* 1047:     */   }
/* 1048:     */   
/* 1049:     */   public String distanceFunctionTipText()
/* 1050:     */   {
/* 1051:1617 */     return "The distance function to use for instances comparison (default: weka.core.EuclideanDistance). ";
/* 1052:     */   }
/* 1053:     */   
/* 1054:     */   public DistanceFunction getDistanceFunction()
/* 1055:     */   {
/* 1056:1627 */     return this.m_DistanceFunction;
/* 1057:     */   }
/* 1058:     */   
/* 1059:     */   public void setDistanceFunction(DistanceFunction df)
/* 1060:     */     throws Exception
/* 1061:     */   {
/* 1062:1637 */     if ((!(df instanceof EuclideanDistance)) && (!(df instanceof ManhattanDistance))) {
/* 1063:1639 */       throw new Exception("SimpleKMeans currently only supports the Euclidean and Manhattan distances.");
/* 1064:     */     }
/* 1065:1642 */     this.m_DistanceFunction = df;
/* 1066:     */   }
/* 1067:     */   
/* 1068:     */   public String preserveInstancesOrderTipText()
/* 1069:     */   {
/* 1070:1652 */     return "Preserve order of instances.";
/* 1071:     */   }
/* 1072:     */   
/* 1073:     */   public void setPreserveInstancesOrder(boolean r)
/* 1074:     */   {
/* 1075:1661 */     this.m_PreserveOrder = r;
/* 1076:     */   }
/* 1077:     */   
/* 1078:     */   public boolean getPreserveInstancesOrder()
/* 1079:     */   {
/* 1080:1670 */     return this.m_PreserveOrder;
/* 1081:     */   }
/* 1082:     */   
/* 1083:     */   public String fastDistanceCalcTipText()
/* 1084:     */   {
/* 1085:1680 */     return "Uses cut-off values for speeding up distance calculation, but suppresses also the calculation and output of the within cluster sum of squared errors/sum of distances.";
/* 1086:     */   }
/* 1087:     */   
/* 1088:     */   public void setFastDistanceCalc(boolean value)
/* 1089:     */   {
/* 1090:1691 */     this.m_FastDistanceCalc = value;
/* 1091:     */   }
/* 1092:     */   
/* 1093:     */   public boolean getFastDistanceCalc()
/* 1094:     */   {
/* 1095:1700 */     return this.m_FastDistanceCalc;
/* 1096:     */   }
/* 1097:     */   
/* 1098:     */   public String numExecutionSlotsTipText()
/* 1099:     */   {
/* 1100:1710 */     return "The number of execution slots (threads) to use. Set equal to the number of available cpu/cores";
/* 1101:     */   }
/* 1102:     */   
/* 1103:     */   public void setNumExecutionSlots(int slots)
/* 1104:     */   {
/* 1105:1722 */     this.m_executionSlots = slots;
/* 1106:     */   }
/* 1107:     */   
/* 1108:     */   public int getNumExecutionSlots()
/* 1109:     */   {
/* 1110:1733 */     return this.m_executionSlots;
/* 1111:     */   }
/* 1112:     */   
/* 1113:     */   public void setOptions(String[] options)
/* 1114:     */     throws Exception
/* 1115:     */   {
/* 1116:1861 */     this.m_displayStdDevs = Utils.getFlag("V", options);
/* 1117:1862 */     this.m_dontReplaceMissing = Utils.getFlag("M", options);
/* 1118:     */     
/* 1119:1864 */     String initM = Utils.getOption("init", options);
/* 1120:1865 */     if (initM.length() > 0) {
/* 1121:1866 */       setInitializationMethod(new SelectedTag(Integer.parseInt(initM), TAGS_SELECTION));
/* 1122:     */     }
/* 1123:1870 */     this.m_speedUpDistanceCompWithCanopies = Utils.getFlag('C', options);
/* 1124:     */     
/* 1125:1872 */     String temp = Utils.getOption("max-candidates", options);
/* 1126:1873 */     if (temp.length() > 0) {
/* 1127:1874 */       setCanopyMaxNumCanopiesToHoldInMemory(Integer.parseInt(temp));
/* 1128:     */     }
/* 1129:1877 */     temp = Utils.getOption("periodic-pruning", options);
/* 1130:1878 */     if (temp.length() > 0) {
/* 1131:1879 */       setCanopyPeriodicPruningRate(Integer.parseInt(temp));
/* 1132:     */     }
/* 1133:1882 */     temp = Utils.getOption("min-density", options);
/* 1134:1883 */     if (temp.length() > 0) {
/* 1135:1884 */       setCanopyMinimumCanopyDensity(Double.parseDouble(temp));
/* 1136:     */     }
/* 1137:1887 */     temp = Utils.getOption("t2", options);
/* 1138:1888 */     if (temp.length() > 0) {
/* 1139:1889 */       setCanopyT2(Double.parseDouble(temp));
/* 1140:     */     }
/* 1141:1892 */     temp = Utils.getOption("t1", options);
/* 1142:1893 */     if (temp.length() > 0) {
/* 1143:1894 */       setCanopyT1(Double.parseDouble(temp));
/* 1144:     */     }
/* 1145:1897 */     String optionString = Utils.getOption('N', options);
/* 1146:1899 */     if (optionString.length() != 0) {
/* 1147:1900 */       setNumClusters(Integer.parseInt(optionString));
/* 1148:     */     }
/* 1149:1903 */     optionString = Utils.getOption("I", options);
/* 1150:1904 */     if (optionString.length() != 0) {
/* 1151:1905 */       setMaxIterations(Integer.parseInt(optionString));
/* 1152:     */     }
/* 1153:1908 */     String distFunctionClass = Utils.getOption('A', options);
/* 1154:1909 */     if (distFunctionClass.length() != 0)
/* 1155:     */     {
/* 1156:1910 */       String[] distFunctionClassSpec = Utils.splitOptions(distFunctionClass);
/* 1157:1911 */       if (distFunctionClassSpec.length == 0) {
/* 1158:1912 */         throw new Exception("Invalid DistanceFunction specification string.");
/* 1159:     */       }
/* 1160:1914 */       String className = distFunctionClassSpec[0];
/* 1161:1915 */       distFunctionClassSpec[0] = "";
/* 1162:     */       
/* 1163:1917 */       setDistanceFunction((DistanceFunction)Utils.forName(DistanceFunction.class, className, distFunctionClassSpec));
/* 1164:     */     }
/* 1165:     */     else
/* 1166:     */     {
/* 1167:1920 */       setDistanceFunction(new EuclideanDistance());
/* 1168:     */     }
/* 1169:1923 */     this.m_PreserveOrder = Utils.getFlag("O", options);
/* 1170:     */     
/* 1171:1925 */     this.m_FastDistanceCalc = Utils.getFlag("fast", options);
/* 1172:     */     
/* 1173:1927 */     String slotsS = Utils.getOption("num-slots", options);
/* 1174:1928 */     if (slotsS.length() > 0) {
/* 1175:1929 */       setNumExecutionSlots(Integer.parseInt(slotsS));
/* 1176:     */     }
/* 1177:1932 */     super.setOptions(options);
/* 1178:     */     
/* 1179:1934 */     Utils.checkForRemainingOptions(options);
/* 1180:     */   }
/* 1181:     */   
/* 1182:     */   public String[] getOptions()
/* 1183:     */   {
/* 1184:1945 */     Vector<String> result = new Vector();
/* 1185:     */     
/* 1186:1947 */     result.add("-init");
/* 1187:1948 */     result.add("" + getInitializationMethod().getSelectedTag().getID());
/* 1188:1950 */     if (this.m_speedUpDistanceCompWithCanopies) {
/* 1189:1951 */       result.add("-C");
/* 1190:     */     }
/* 1191:1954 */     result.add("-max-candidates");
/* 1192:1955 */     result.add("" + getCanopyMaxNumCanopiesToHoldInMemory());
/* 1193:1956 */     result.add("-periodic-pruning");
/* 1194:1957 */     result.add("" + getCanopyPeriodicPruningRate());
/* 1195:1958 */     result.add("-min-density");
/* 1196:1959 */     result.add("" + getCanopyMinimumCanopyDensity());
/* 1197:1960 */     result.add("-t1");
/* 1198:1961 */     result.add("" + getCanopyT1());
/* 1199:1962 */     result.add("-t2");
/* 1200:1963 */     result.add("" + getCanopyT2());
/* 1201:1965 */     if (this.m_displayStdDevs) {
/* 1202:1966 */       result.add("-V");
/* 1203:     */     }
/* 1204:1969 */     if (this.m_dontReplaceMissing) {
/* 1205:1970 */       result.add("-M");
/* 1206:     */     }
/* 1207:1973 */     result.add("-N");
/* 1208:1974 */     result.add("" + getNumClusters());
/* 1209:     */     
/* 1210:1976 */     result.add("-A");
/* 1211:1977 */     result.add((this.m_DistanceFunction.getClass().getName() + " " + Utils.joinOptions(this.m_DistanceFunction.getOptions())).trim());
/* 1212:     */     
/* 1213:     */ 
/* 1214:1980 */     result.add("-I");
/* 1215:1981 */     result.add("" + getMaxIterations());
/* 1216:1983 */     if (this.m_PreserveOrder) {
/* 1217:1984 */       result.add("-O");
/* 1218:     */     }
/* 1219:1987 */     if (this.m_FastDistanceCalc) {
/* 1220:1988 */       result.add("-fast");
/* 1221:     */     }
/* 1222:1991 */     result.add("-num-slots");
/* 1223:1992 */     result.add("" + getNumExecutionSlots());
/* 1224:     */     
/* 1225:1994 */     Collections.addAll(result, super.getOptions());
/* 1226:     */     
/* 1227:1996 */     return (String[])result.toArray(new String[result.size()]);
/* 1228:     */   }
/* 1229:     */   
/* 1230:     */   public String toString()
/* 1231:     */   {
/* 1232:2006 */     if (this.m_ClusterCentroids == null) {
/* 1233:2007 */       return "No clusterer built yet!";
/* 1234:     */     }
/* 1235:2010 */     int maxWidth = 0;
/* 1236:2011 */     int maxAttWidth = 0;
/* 1237:2012 */     boolean containsNumeric = false;
/* 1238:2013 */     for (int i = 0; i < this.m_NumClusters; i++) {
/* 1239:2014 */       for (int j = 0; j < this.m_ClusterCentroids.numAttributes(); j++)
/* 1240:     */       {
/* 1241:2015 */         if (this.m_ClusterCentroids.attribute(j).name().length() > maxAttWidth) {
/* 1242:2016 */           maxAttWidth = this.m_ClusterCentroids.attribute(j).name().length();
/* 1243:     */         }
/* 1244:2018 */         if (this.m_ClusterCentroids.attribute(j).isNumeric())
/* 1245:     */         {
/* 1246:2019 */           containsNumeric = true;
/* 1247:2020 */           double width = Math.log(Math.abs(this.m_ClusterCentroids.instance(i).value(j))) / Math.log(10.0D);
/* 1248:2024 */           if (width < 0.0D) {
/* 1249:2025 */             width = 1.0D;
/* 1250:     */           }
/* 1251:2028 */           width += 6.0D;
/* 1252:2029 */           if ((int)width > maxWidth) {
/* 1253:2030 */             maxWidth = (int)width;
/* 1254:     */           }
/* 1255:     */         }
/* 1256:     */       }
/* 1257:     */     }
/* 1258:2036 */     for (int i = 0; i < this.m_ClusterCentroids.numAttributes(); i++) {
/* 1259:2037 */       if (this.m_ClusterCentroids.attribute(i).isNominal())
/* 1260:     */       {
/* 1261:2038 */         Attribute a = this.m_ClusterCentroids.attribute(i);
/* 1262:2039 */         for (int j = 0; j < this.m_ClusterCentroids.numInstances(); j++)
/* 1263:     */         {
/* 1264:2040 */           String val = a.value((int)this.m_ClusterCentroids.instance(j).value(i));
/* 1265:2041 */           if (val.length() > maxWidth) {
/* 1266:2042 */             maxWidth = val.length();
/* 1267:     */           }
/* 1268:     */         }
/* 1269:2045 */         for (int j = 0; j < a.numValues(); j++)
/* 1270:     */         {
/* 1271:2046 */           String val = a.value(j) + " ";
/* 1272:2047 */           if (val.length() > maxAttWidth) {
/* 1273:2048 */             maxAttWidth = val.length();
/* 1274:     */           }
/* 1275:     */         }
/* 1276:     */       }
/* 1277:     */     }
/* 1278:2054 */     if (this.m_displayStdDevs) {
/* 1279:2056 */       for (int i = 0; i < this.m_ClusterCentroids.numAttributes(); i++) {
/* 1280:2057 */         if (this.m_ClusterCentroids.attribute(i).isNominal())
/* 1281:     */         {
/* 1282:2058 */           int maxV = Utils.maxIndex(this.m_FullNominalCounts[i]);
/* 1283:     */           
/* 1284:     */ 
/* 1285:     */ 
/* 1286:     */ 
/* 1287:2063 */           int percent = 6;
/* 1288:2064 */           String nomV = "" + this.m_FullNominalCounts[i][maxV];
/* 1289:2066 */           if (nomV.length() + percent > maxWidth) {
/* 1290:2067 */             maxWidth = nomV.length() + 1;
/* 1291:     */           }
/* 1292:     */         }
/* 1293:     */       }
/* 1294:     */     }
/* 1295:2074 */     for (double m_ClusterSize : this.m_ClusterSizes)
/* 1296:     */     {
/* 1297:2075 */       String size = "(" + m_ClusterSize + ")";
/* 1298:2076 */       if (size.length() > maxWidth) {
/* 1299:2077 */         maxWidth = size.length();
/* 1300:     */       }
/* 1301:     */     }
/* 1302:2081 */     if ((this.m_displayStdDevs) && (maxAttWidth < "missing".length())) {
/* 1303:2082 */       maxAttWidth = "missing".length();
/* 1304:     */     }
/* 1305:2085 */     String plusMinus = "+/-";
/* 1306:2086 */     maxAttWidth += 2;
/* 1307:2087 */     if ((this.m_displayStdDevs) && (containsNumeric)) {
/* 1308:2088 */       maxWidth += plusMinus.length();
/* 1309:     */     }
/* 1310:2090 */     if (maxAttWidth < "Attribute".length() + 2) {
/* 1311:2091 */       maxAttWidth = "Attribute".length() + 2;
/* 1312:     */     }
/* 1313:2094 */     if (maxWidth < "Full Data".length()) {
/* 1314:2095 */       maxWidth = "Full Data".length() + 1;
/* 1315:     */     }
/* 1316:2098 */     if (maxWidth < "missing".length()) {
/* 1317:2099 */       maxWidth = "missing".length() + 1;
/* 1318:     */     }
/* 1319:2102 */     StringBuffer temp = new StringBuffer();
/* 1320:2103 */     temp.append("\nkMeans\n======\n");
/* 1321:2104 */     temp.append("\nNumber of iterations: " + this.m_Iterations);
/* 1322:2106 */     if (!this.m_FastDistanceCalc)
/* 1323:     */     {
/* 1324:2107 */       temp.append("\n");
/* 1325:2108 */       if ((this.m_DistanceFunction instanceof EuclideanDistance)) {
/* 1326:2109 */         temp.append("Within cluster sum of squared errors: " + Utils.sum(this.m_squaredErrors));
/* 1327:     */       } else {
/* 1328:2112 */         temp.append("Sum of within cluster distances: " + Utils.sum(this.m_squaredErrors));
/* 1329:     */       }
/* 1330:     */     }
/* 1331:2117 */     temp.append("\n\nInitial starting points (");
/* 1332:2118 */     switch (this.m_initializationMethod)
/* 1333:     */     {
/* 1334:     */     case 3: 
/* 1335:2120 */       temp.append("farthest first");
/* 1336:2121 */       break;
/* 1337:     */     case 1: 
/* 1338:2123 */       temp.append("k-means++");
/* 1339:2124 */       break;
/* 1340:     */     case 2: 
/* 1341:2126 */       temp.append("canopy");
/* 1342:2127 */       break;
/* 1343:     */     default: 
/* 1344:2129 */       temp.append("random");
/* 1345:     */     }
/* 1346:2131 */     temp.append("):\n");
/* 1347:2132 */     if (this.m_initializationMethod != 2)
/* 1348:     */     {
/* 1349:2133 */       temp.append("\n");
/* 1350:2134 */       for (int i = 0; i < this.m_initialStartPoints.numInstances(); i++) {
/* 1351:2135 */         temp.append("Cluster " + i + ": " + this.m_initialStartPoints.instance(i)).append("\n");
/* 1352:     */       }
/* 1353:     */     }
/* 1354:     */     else
/* 1355:     */     {
/* 1356:2139 */       temp.append(this.m_canopyClusters.toString(false));
/* 1357:     */     }
/* 1358:2142 */     if (this.m_speedUpDistanceCompWithCanopies)
/* 1359:     */     {
/* 1360:2143 */       temp.append("\nReduced number of distance calculations by using canopies.");
/* 1361:2145 */       if (this.m_initializationMethod != 2)
/* 1362:     */       {
/* 1363:2146 */         temp.append("\nCanopy T2 radius: " + String.format("%-10.3f", new Object[] { Double.valueOf(this.m_canopyClusters.getActualT2()) }));
/* 1364:     */         
/* 1365:2148 */         temp.append("\nCanopy T1 radius: " + String.format("%-10.3f", new Object[] { Double.valueOf(this.m_canopyClusters.getActualT1()) })).append("\n");
/* 1366:     */       }
/* 1367:     */     }
/* 1368:2155 */     if (!this.m_dontReplaceMissing) {
/* 1369:2156 */       temp.append("\nMissing values globally replaced with mean/mode");
/* 1370:     */     }
/* 1371:2159 */     temp.append("\n\nFinal cluster centroids:\n");
/* 1372:2160 */     temp.append(pad("Cluster#", " ", maxAttWidth + (maxWidth * 2 + 2) - "Cluster#".length(), true));
/* 1373:     */     
/* 1374:     */ 
/* 1375:2163 */     temp.append("\n");
/* 1376:2164 */     temp.append(pad("Attribute", " ", maxAttWidth - "Attribute".length(), false));
/* 1377:     */     
/* 1378:     */ 
/* 1379:2167 */     temp.append(pad("Full Data", " ", maxWidth + 1 - "Full Data".length(), true));
/* 1380:2171 */     for (int i = 0; i < this.m_NumClusters; i++)
/* 1381:     */     {
/* 1382:2172 */       String clustNum = "" + i;
/* 1383:2173 */       temp.append(pad(clustNum, " ", maxWidth + 1 - clustNum.length(), true));
/* 1384:     */     }
/* 1385:2175 */     temp.append("\n");
/* 1386:     */     
/* 1387:     */ 
/* 1388:2178 */     String cSize = "(" + Utils.sum(this.m_ClusterSizes) + ")";
/* 1389:2179 */     temp.append(pad(cSize, " ", maxAttWidth + maxWidth + 1 - cSize.length(), true));
/* 1390:2181 */     for (int i = 0; i < this.m_NumClusters; i++)
/* 1391:     */     {
/* 1392:2182 */       cSize = "(" + this.m_ClusterSizes[i] + ")";
/* 1393:2183 */       temp.append(pad(cSize, " ", maxWidth + 1 - cSize.length(), true));
/* 1394:     */     }
/* 1395:2185 */     temp.append("\n");
/* 1396:     */     
/* 1397:2187 */     temp.append(pad("", "=", maxAttWidth + (maxWidth * (this.m_ClusterCentroids.numInstances() + 1) + this.m_ClusterCentroids.numInstances() + 1), true));
/* 1398:     */     
/* 1399:     */ 
/* 1400:     */ 
/* 1401:2191 */     temp.append("\n");
/* 1402:2193 */     for (int i = 0; i < this.m_ClusterCentroids.numAttributes(); i++)
/* 1403:     */     {
/* 1404:2194 */       String attName = this.m_ClusterCentroids.attribute(i).name();
/* 1405:2195 */       temp.append(attName);
/* 1406:2196 */       for (int j = 0; j < maxAttWidth - attName.length(); j++) {
/* 1407:2197 */         temp.append(" ");
/* 1408:     */       }
/* 1409:     */       String valMeanMode;
/* 1410:     */       String valMeanMode;
/* 1411:2203 */       if (this.m_ClusterCentroids.attribute(i).isNominal())
/* 1412:     */       {
/* 1413:     */         String valMeanMode;
/* 1414:2204 */         if (this.m_FullMeansOrMediansOrModes[i] == -1.0D)
/* 1415:     */         {
/* 1416:2205 */           valMeanMode = pad("missing", " ", maxWidth + 1 - "missing".length(), true);
/* 1417:     */         }
/* 1418:     */         else
/* 1419:     */         {
/* 1420:     */           String strVal;
/* 1421:2208 */           valMeanMode = pad(strVal = this.m_ClusterCentroids.attribute(i).value((int)this.m_FullMeansOrMediansOrModes[i]), " ", maxWidth + 1 - strVal.length(), true);
/* 1422:     */         }
/* 1423:     */       }
/* 1424:     */       else
/* 1425:     */       {
/* 1426:     */         String valMeanMode;
/* 1427:2216 */         if (Double.isNaN(this.m_FullMeansOrMediansOrModes[i]))
/* 1428:     */         {
/* 1429:2217 */           valMeanMode = pad("missing", " ", maxWidth + 1 - "missing".length(), true);
/* 1430:     */         }
/* 1431:     */         else
/* 1432:     */         {
/* 1433:     */           String strVal;
/* 1434:2220 */           valMeanMode = pad(strVal = Utils.doubleToString(this.m_FullMeansOrMediansOrModes[i], maxWidth, 4).trim(), " ", maxWidth + 1 - strVal.length(), true);
/* 1435:     */         }
/* 1436:     */       }
/* 1437:2227 */       temp.append(valMeanMode);
/* 1438:2229 */       for (int j = 0; j < this.m_NumClusters; j++)
/* 1439:     */       {
/* 1440:2230 */         if (this.m_ClusterCentroids.attribute(i).isNominal())
/* 1441:     */         {
/* 1442:2231 */           if (this.m_ClusterCentroids.instance(j).isMissing(i))
/* 1443:     */           {
/* 1444:2232 */             valMeanMode = pad("missing", " ", maxWidth + 1 - "missing".length(), true);
/* 1445:     */           }
/* 1446:     */           else
/* 1447:     */           {
/* 1448:     */             String strVal;
/* 1449:2235 */             valMeanMode = pad(strVal = this.m_ClusterCentroids.attribute(i).value((int)this.m_ClusterCentroids.instance(j).value(i)), " ", maxWidth + 1 - strVal.length(), true);
/* 1450:     */           }
/* 1451:     */         }
/* 1452:2243 */         else if (this.m_ClusterCentroids.instance(j).isMissing(i))
/* 1453:     */         {
/* 1454:2244 */           valMeanMode = pad("missing", " ", maxWidth + 1 - "missing".length(), true);
/* 1455:     */         }
/* 1456:     */         else
/* 1457:     */         {
/* 1458:     */           String strVal;
/* 1459:2247 */           valMeanMode = pad(strVal = Utils.doubleToString(this.m_ClusterCentroids.instance(j).value(i), maxWidth, 4).trim(), " ", maxWidth + 1 - strVal.length(), true);
/* 1460:     */         }
/* 1461:2255 */         temp.append(valMeanMode);
/* 1462:     */       }
/* 1463:2257 */       temp.append("\n");
/* 1464:2259 */       if (this.m_displayStdDevs)
/* 1465:     */       {
/* 1466:2261 */         String stdDevVal = "";
/* 1467:2263 */         if (this.m_ClusterCentroids.attribute(i).isNominal())
/* 1468:     */         {
/* 1469:2265 */           Attribute a = this.m_ClusterCentroids.attribute(i);
/* 1470:2266 */           for (int j = 0; j < a.numValues(); j++)
/* 1471:     */           {
/* 1472:2268 */             String val = "  " + a.value(j);
/* 1473:2269 */             temp.append(pad(val, " ", maxAttWidth + 1 - val.length(), false));
/* 1474:2270 */             double count = this.m_FullNominalCounts[i][j];
/* 1475:2271 */             int percent = (int)(this.m_FullNominalCounts[i][j] / Utils.sum(this.m_ClusterSizes) * 100.0D);
/* 1476:     */             
/* 1477:     */ 
/* 1478:2274 */             String percentS = "" + percent + "%)";
/* 1479:2275 */             percentS = pad(percentS, " ", 5 - percentS.length(), true);
/* 1480:2276 */             stdDevVal = "" + count + " (" + percentS;
/* 1481:2277 */             stdDevVal = pad(stdDevVal, " ", maxWidth + 1 - stdDevVal.length(), true);
/* 1482:     */             
/* 1483:2279 */             temp.append(stdDevVal);
/* 1484:2282 */             for (int k = 0; k < this.m_NumClusters; k++)
/* 1485:     */             {
/* 1486:2283 */               percent = (int)(this.m_ClusterNominalCounts[k][i][j] / this.m_ClusterSizes[k] * 100.0D);
/* 1487:     */               
/* 1488:     */ 
/* 1489:2286 */               percentS = "" + percent + "%)";
/* 1490:2287 */               percentS = pad(percentS, " ", 5 - percentS.length(), true);
/* 1491:2288 */               stdDevVal = "" + this.m_ClusterNominalCounts[k][i][j] + " (" + percentS;
/* 1492:2289 */               stdDevVal = pad(stdDevVal, " ", maxWidth + 1 - stdDevVal.length(), true);
/* 1493:     */               
/* 1494:2291 */               temp.append(stdDevVal);
/* 1495:     */             }
/* 1496:2293 */             temp.append("\n");
/* 1497:     */           }
/* 1498:2296 */           if (this.m_FullMissingCounts[i] > 0.0D)
/* 1499:     */           {
/* 1500:2298 */             temp.append(pad("  missing", " ", maxAttWidth + 1 - "  missing".length(), false));
/* 1501:     */             
/* 1502:2300 */             double count = this.m_FullMissingCounts[i];
/* 1503:2301 */             int percent = (int)(this.m_FullMissingCounts[i] / Utils.sum(this.m_ClusterSizes) * 100.0D);
/* 1504:     */             
/* 1505:     */ 
/* 1506:2304 */             String percentS = "" + percent + "%)";
/* 1507:2305 */             percentS = pad(percentS, " ", 5 - percentS.length(), true);
/* 1508:2306 */             stdDevVal = "" + count + " (" + percentS;
/* 1509:2307 */             stdDevVal = pad(stdDevVal, " ", maxWidth + 1 - stdDevVal.length(), true);
/* 1510:     */             
/* 1511:2309 */             temp.append(stdDevVal);
/* 1512:2312 */             for (int k = 0; k < this.m_NumClusters; k++)
/* 1513:     */             {
/* 1514:2313 */               percent = (int)(this.m_ClusterMissingCounts[k][i] / this.m_ClusterSizes[k] * 100.0D);
/* 1515:     */               
/* 1516:     */ 
/* 1517:2316 */               percentS = "" + percent + "%)";
/* 1518:2317 */               percentS = pad(percentS, " ", 5 - percentS.length(), true);
/* 1519:2318 */               stdDevVal = "" + this.m_ClusterMissingCounts[k][i] + " (" + percentS;
/* 1520:2319 */               stdDevVal = pad(stdDevVal, " ", maxWidth + 1 - stdDevVal.length(), true);
/* 1521:     */               
/* 1522:2321 */               temp.append(stdDevVal);
/* 1523:     */             }
/* 1524:2324 */             temp.append("\n");
/* 1525:     */           }
/* 1526:2327 */           temp.append("\n");
/* 1527:     */         }
/* 1528:     */         else
/* 1529:     */         {
/* 1530:2330 */           if (Double.isNaN(this.m_FullMeansOrMediansOrModes[i]))
/* 1531:     */           {
/* 1532:2331 */             stdDevVal = pad("--", " ", maxAttWidth + maxWidth + 1 - 2, true);
/* 1533:     */           }
/* 1534:     */           else
/* 1535:     */           {
/* 1536:     */             String strVal;
/* 1537:2333 */             stdDevVal = pad(strVal = plusMinus + Utils.doubleToString(this.m_FullStdDevs[i], maxWidth, 4).trim(), " ", maxWidth + maxAttWidth + 1 - strVal.length(), true);
/* 1538:     */           }
/* 1539:2341 */           temp.append(stdDevVal);
/* 1540:2344 */           for (int j = 0; j < this.m_NumClusters; j++)
/* 1541:     */           {
/* 1542:2345 */             if (this.m_ClusterCentroids.instance(j).isMissing(i))
/* 1543:     */             {
/* 1544:2346 */               stdDevVal = pad("--", " ", maxWidth + 1 - 2, true);
/* 1545:     */             }
/* 1546:     */             else
/* 1547:     */             {
/* 1548:     */               String strVal;
/* 1549:2348 */               stdDevVal = pad(strVal = plusMinus + Utils.doubleToString(this.m_ClusterStdDevs.instance(j).value(i), maxWidth, 4).trim(), " ", maxWidth + 1 - strVal.length(), true);
/* 1550:     */             }
/* 1551:2356 */             temp.append(stdDevVal);
/* 1552:     */           }
/* 1553:2358 */           temp.append("\n\n");
/* 1554:     */         }
/* 1555:     */       }
/* 1556:     */     }
/* 1557:2363 */     temp.append("\n\n");
/* 1558:2364 */     return temp.toString();
/* 1559:     */   }
/* 1560:     */   
/* 1561:     */   private String pad(String source, String padChar, int length, boolean leftPad)
/* 1562:     */   {
/* 1563:2369 */     StringBuffer temp = new StringBuffer();
/* 1564:2371 */     if (leftPad)
/* 1565:     */     {
/* 1566:2372 */       for (int i = 0; i < length; i++) {
/* 1567:2373 */         temp.append(padChar);
/* 1568:     */       }
/* 1569:2375 */       temp.append(source);
/* 1570:     */     }
/* 1571:     */     else
/* 1572:     */     {
/* 1573:2377 */       temp.append(source);
/* 1574:2378 */       for (int i = 0; i < length; i++) {
/* 1575:2379 */         temp.append(padChar);
/* 1576:     */       }
/* 1577:     */     }
/* 1578:2382 */     return temp.toString();
/* 1579:     */   }
/* 1580:     */   
/* 1581:     */   public Instances getClusterCentroids()
/* 1582:     */   {
/* 1583:2391 */     return this.m_ClusterCentroids;
/* 1584:     */   }
/* 1585:     */   
/* 1586:     */   public Instances getClusterStandardDevs()
/* 1587:     */   {
/* 1588:2400 */     return this.m_ClusterStdDevs;
/* 1589:     */   }
/* 1590:     */   
/* 1591:     */   public double[][][] getClusterNominalCounts()
/* 1592:     */   {
/* 1593:2410 */     return this.m_ClusterNominalCounts;
/* 1594:     */   }
/* 1595:     */   
/* 1596:     */   public double getSquaredError()
/* 1597:     */   {
/* 1598:2420 */     if (this.m_FastDistanceCalc) {
/* 1599:2421 */       return (0.0D / 0.0D);
/* 1600:     */     }
/* 1601:2423 */     return Utils.sum(this.m_squaredErrors);
/* 1602:     */   }
/* 1603:     */   
/* 1604:     */   public double[] getClusterSizes()
/* 1605:     */   {
/* 1606:2433 */     return this.m_ClusterSizes;
/* 1607:     */   }
/* 1608:     */   
/* 1609:     */   public int[] getAssignments()
/* 1610:     */     throws Exception
/* 1611:     */   {
/* 1612:2444 */     if (!this.m_PreserveOrder) {
/* 1613:2445 */       throw new Exception("The assignments are only available when order of instances is preserved (-O)");
/* 1614:     */     }
/* 1615:2448 */     if (this.m_Assignments == null) {
/* 1616:2449 */       throw new Exception("No assignments made.");
/* 1617:     */     }
/* 1618:2451 */     return this.m_Assignments;
/* 1619:     */   }
/* 1620:     */   
/* 1621:     */   public String getRevision()
/* 1622:     */   {
/* 1623:2461 */     return RevisionUtils.extract("$Revision: 11444 $");
/* 1624:     */   }
/* 1625:     */   
/* 1626:     */   public static void main(String[] args)
/* 1627:     */   {
/* 1628:2470 */     runClusterer(new SimpleKMeans(), args);
/* 1629:     */   }
/* 1630:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.clusterers.SimpleKMeans
 * JD-Core Version:    0.7.0.1
 */