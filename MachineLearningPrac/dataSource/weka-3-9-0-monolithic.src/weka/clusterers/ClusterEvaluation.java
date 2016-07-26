/*    1:     */ package weka.clusterers;
/*    2:     */ 
/*    3:     */ import java.beans.BeanInfo;
/*    4:     */ import java.beans.Introspector;
/*    5:     */ import java.beans.MethodDescriptor;
/*    6:     */ import java.io.BufferedInputStream;
/*    7:     */ import java.io.BufferedOutputStream;
/*    8:     */ import java.io.BufferedWriter;
/*    9:     */ import java.io.FileInputStream;
/*   10:     */ import java.io.FileOutputStream;
/*   11:     */ import java.io.FileWriter;
/*   12:     */ import java.io.ObjectInputStream;
/*   13:     */ import java.io.ObjectOutputStream;
/*   14:     */ import java.io.PrintStream;
/*   15:     */ import java.io.Serializable;
/*   16:     */ import java.lang.reflect.Method;
/*   17:     */ import java.util.Enumeration;
/*   18:     */ import java.util.Random;
/*   19:     */ import java.util.Vector;
/*   20:     */ import weka.core.Attribute;
/*   21:     */ import weka.core.BatchPredictor;
/*   22:     */ import weka.core.Drawable;
/*   23:     */ import weka.core.Instance;
/*   24:     */ import weka.core.Instances;
/*   25:     */ import weka.core.Option;
/*   26:     */ import weka.core.OptionHandler;
/*   27:     */ import weka.core.Range;
/*   28:     */ import weka.core.RevisionHandler;
/*   29:     */ import weka.core.RevisionUtils;
/*   30:     */ import weka.core.Utils;
/*   31:     */ import weka.core.converters.ConverterUtils.DataSource;
/*   32:     */ import weka.filters.Filter;
/*   33:     */ import weka.filters.unsupervised.attribute.Remove;
/*   34:     */ 
/*   35:     */ public class ClusterEvaluation
/*   36:     */   implements Serializable, RevisionHandler
/*   37:     */ {
/*   38:     */   static final long serialVersionUID = -830188327319128005L;
/*   39:     */   private Clusterer m_Clusterer;
/*   40:     */   private final StringBuffer m_clusteringResults;
/*   41:     */   private int m_numClusters;
/*   42:     */   private double[] m_clusterAssignments;
/*   43:     */   private double m_logL;
/*   44: 136 */   private int[] m_classToCluster = null;
/*   45:     */   
/*   46:     */   public void setClusterer(Clusterer clusterer)
/*   47:     */   {
/*   48: 144 */     this.m_Clusterer = clusterer;
/*   49:     */   }
/*   50:     */   
/*   51:     */   public String clusterResultsToString()
/*   52:     */   {
/*   53: 153 */     return this.m_clusteringResults.toString();
/*   54:     */   }
/*   55:     */   
/*   56:     */   public int getNumClusters()
/*   57:     */   {
/*   58: 163 */     return this.m_numClusters;
/*   59:     */   }
/*   60:     */   
/*   61:     */   public double[] getClusterAssignments()
/*   62:     */   {
/*   63: 173 */     return this.m_clusterAssignments;
/*   64:     */   }
/*   65:     */   
/*   66:     */   public int[] getClassesToClusters()
/*   67:     */   {
/*   68: 183 */     return this.m_classToCluster;
/*   69:     */   }
/*   70:     */   
/*   71:     */   public double getLogLikelihood()
/*   72:     */   {
/*   73: 193 */     return this.m_logL;
/*   74:     */   }
/*   75:     */   
/*   76:     */   public ClusterEvaluation()
/*   77:     */   {
/*   78: 201 */     setClusterer(new SimpleKMeans());
/*   79: 202 */     this.m_clusteringResults = new StringBuffer();
/*   80: 203 */     this.m_clusterAssignments = null;
/*   81:     */   }
/*   82:     */   
/*   83:     */   public void evaluateClusterer(Instances test)
/*   84:     */     throws Exception
/*   85:     */   {
/*   86: 215 */     evaluateClusterer(test, "");
/*   87:     */   }
/*   88:     */   
/*   89:     */   public void evaluateClusterer(Instances test, String testFileName)
/*   90:     */     throws Exception
/*   91:     */   {
/*   92: 231 */     evaluateClusterer(test, testFileName, true);
/*   93:     */   }
/*   94:     */   
/*   95:     */   public void evaluateClusterer(Instances test, String testFileName, boolean outputModel)
/*   96:     */     throws Exception
/*   97:     */   {
/*   98: 249 */     int i = 0;
/*   99:     */     
/*  100: 251 */     double loglk = 0.0D;
/*  101: 252 */     int cc = this.m_Clusterer.numberOfClusters();
/*  102: 253 */     this.m_numClusters = cc;
/*  103: 254 */     double[] instanceStats = new double[cc];
/*  104: 255 */     Instances testRaw = null;
/*  105: 256 */     boolean hasClass = test.classIndex() >= 0;
/*  106: 257 */     int unclusteredInstances = 0;
/*  107: 258 */     Vector<Double> clusterAssignments = new Vector();
/*  108: 259 */     Filter filter = null;
/*  109: 260 */     ConverterUtils.DataSource source = null;
/*  110: 263 */     if (testFileName == null) {
/*  111: 264 */       testFileName = "";
/*  112:     */     }
/*  113: 268 */     if (testFileName.length() != 0) {
/*  114: 269 */       source = new ConverterUtils.DataSource(testFileName);
/*  115:     */     } else {
/*  116: 271 */       source = new ConverterUtils.DataSource(test);
/*  117:     */     }
/*  118: 273 */     testRaw = source.getStructure(test.classIndex());
/*  119: 276 */     if (hasClass)
/*  120:     */     {
/*  121: 277 */       if (testRaw.classAttribute().isNumeric()) {
/*  122: 278 */         throw new Exception("ClusterEvaluation: Class must be nominal!");
/*  123:     */       }
/*  124: 281 */       filter = new Remove();
/*  125: 282 */       ((Remove)filter).setAttributeIndices("" + (testRaw.classIndex() + 1));
/*  126: 283 */       ((Remove)filter).setInvertSelection(false);
/*  127: 284 */       filter.setInputFormat(testRaw);
/*  128:     */     }
/*  129: 287 */     Instances forBatchPredictors = filter != null ? new Instances(filter.getOutputFormat(), 0) : new Instances(source.getStructure(), 0);
/*  130:     */     
/*  131:     */ 
/*  132: 290 */     i = 0;
/*  133: 291 */     while (source.hasMoreElements(testRaw))
/*  134:     */     {
/*  135: 293 */       Instance inst = source.nextElement(testRaw);
/*  136: 294 */       if (filter != null)
/*  137:     */       {
/*  138: 295 */         filter.input(inst);
/*  139: 296 */         filter.batchFinished();
/*  140: 297 */         inst = filter.output();
/*  141:     */       }
/*  142: 300 */       if (((this.m_Clusterer instanceof BatchPredictor)) && (((BatchPredictor)this.m_Clusterer).implementsMoreEfficientBatchPrediction()))
/*  143:     */       {
/*  144: 303 */         forBatchPredictors.add(inst);
/*  145:     */       }
/*  146:     */       else
/*  147:     */       {
/*  148: 305 */         int cnum = -1;
/*  149:     */         try
/*  150:     */         {
/*  151: 307 */           if ((this.m_Clusterer instanceof DensityBasedClusterer))
/*  152:     */           {
/*  153: 308 */             loglk += ((DensityBasedClusterer)this.m_Clusterer).logDensityForInstance(inst);
/*  154:     */             
/*  155: 310 */             cnum = this.m_Clusterer.clusterInstance(inst);
/*  156: 311 */             clusterAssignments.add(Double.valueOf(cnum));
/*  157:     */           }
/*  158:     */           else
/*  159:     */           {
/*  160: 313 */             cnum = this.m_Clusterer.clusterInstance(inst);
/*  161: 314 */             clusterAssignments.add(Double.valueOf(cnum));
/*  162:     */           }
/*  163:     */         }
/*  164:     */         catch (Exception e)
/*  165:     */         {
/*  166: 317 */           clusterAssignments.add(Double.valueOf(-1.0D));
/*  167: 318 */           unclusteredInstances++;
/*  168:     */         }
/*  169: 321 */         if (cnum != -1) {
/*  170: 322 */           instanceStats[cnum] += 1.0D;
/*  171:     */         }
/*  172:     */       }
/*  173:     */     }
/*  174: 327 */     if (((this.m_Clusterer instanceof BatchPredictor)) && (((BatchPredictor)this.m_Clusterer).implementsMoreEfficientBatchPrediction()))
/*  175:     */     {
/*  176: 330 */       double[][] dists = ((BatchPredictor)this.m_Clusterer).distributionsForInstances(forBatchPredictors);
/*  177: 333 */       for (double[] d : dists)
/*  178:     */       {
/*  179: 334 */         int cnum = Utils.maxIndex(d);
/*  180: 335 */         clusterAssignments.add(Double.valueOf(cnum));
/*  181: 336 */         instanceStats[cnum] += 1.0D;
/*  182:     */       }
/*  183:     */     }
/*  184: 340 */     double sum = Utils.sum(instanceStats);
/*  185: 341 */     loglk /= sum;
/*  186: 342 */     this.m_logL = loglk;
/*  187: 343 */     this.m_clusterAssignments = new double[clusterAssignments.size()];
/*  188: 344 */     for (i = 0; i < clusterAssignments.size(); i++) {
/*  189: 345 */       this.m_clusterAssignments[i] = ((Double)clusterAssignments.get(i)).doubleValue();
/*  190:     */     }
/*  191: 347 */     int numInstFieldWidth = (int)(Math.log(clusterAssignments.size()) / Math.log(10.0D) + 1.0D);
/*  192: 350 */     if (outputModel) {
/*  193: 351 */       this.m_clusteringResults.append(this.m_Clusterer.toString());
/*  194:     */     }
/*  195: 353 */     this.m_clusteringResults.append("Clustered Instances\n\n");
/*  196: 354 */     int clustFieldWidth = (int)(Math.log(cc) / Math.log(10.0D) + 1.0D);
/*  197: 355 */     for (i = 0; i < cc; i++) {
/*  198: 356 */       if (instanceStats[i] > 0.0D) {
/*  199: 357 */         this.m_clusteringResults.append(Utils.doubleToString(i, clustFieldWidth, 0) + "      " + Utils.doubleToString(instanceStats[i], numInstFieldWidth, 0) + " (" + Utils.doubleToString(instanceStats[i] / sum * 100.0D, 3, 0) + "%)\n");
/*  200:     */       }
/*  201:     */     }
/*  202: 365 */     if (unclusteredInstances > 0) {
/*  203: 366 */       this.m_clusteringResults.append("\nUnclustered instances : " + unclusteredInstances);
/*  204:     */     }
/*  205: 370 */     if ((this.m_Clusterer instanceof DensityBasedClusterer)) {
/*  206: 371 */       this.m_clusteringResults.append("\n\nLog likelihood: " + Utils.doubleToString(loglk, 1, 5) + "\n");
/*  207:     */     }
/*  208: 375 */     if (hasClass) {
/*  209: 376 */       evaluateClustersWithRespectToClass(test, testFileName);
/*  210:     */     }
/*  211:     */   }
/*  212:     */   
/*  213:     */   private void evaluateClustersWithRespectToClass(Instances inst, String fileName)
/*  214:     */     throws Exception
/*  215:     */   {
/*  216: 392 */     int numClasses = inst.classAttribute().numValues();
/*  217: 393 */     int[][] counts = new int[this.m_numClusters][numClasses];
/*  218: 394 */     int[] clusterTotals = new int[this.m_numClusters];
/*  219: 395 */     double[] best = new double[this.m_numClusters + 1];
/*  220: 396 */     double[] current = new double[this.m_numClusters + 1];
/*  221: 397 */     ConverterUtils.DataSource source = null;
/*  222: 398 */     Instances instances = null;
/*  223: 399 */     Instance instance = null;
/*  224: 403 */     if (fileName == null) {
/*  225: 404 */       fileName = "";
/*  226:     */     }
/*  227: 407 */     if (fileName.length() != 0) {
/*  228: 408 */       source = new ConverterUtils.DataSource(fileName);
/*  229:     */     } else {
/*  230: 410 */       source = new ConverterUtils.DataSource(inst);
/*  231:     */     }
/*  232: 412 */     instances = source.getStructure(inst.classIndex());
/*  233:     */     
/*  234: 414 */     int i = 0;
/*  235: 415 */     while (source.hasMoreElements(instances))
/*  236:     */     {
/*  237: 416 */       instance = source.nextElement(instances);
/*  238: 417 */       if (this.m_clusterAssignments[i] >= 0.0D)
/*  239:     */       {
/*  240: 418 */         counts[((int)this.m_clusterAssignments[i])][((int)instance.classValue())] += 1;
/*  241: 419 */         clusterTotals[((int)this.m_clusterAssignments[i])] += 1;
/*  242:     */       }
/*  243: 421 */       i++;
/*  244:     */     }
/*  245: 423 */     int numInstances = i;
/*  246:     */     
/*  247: 425 */     best[this.m_numClusters] = 1.7976931348623157E+308D;
/*  248: 426 */     mapClasses(this.m_numClusters, 0, counts, clusterTotals, current, best, 0);
/*  249:     */     
/*  250: 428 */     this.m_clusteringResults.append("\n\nClass attribute: " + inst.classAttribute().name() + "\n");
/*  251:     */     
/*  252: 430 */     this.m_clusteringResults.append("Classes to Clusters:\n");
/*  253: 431 */     String matrixString = toMatrixString(counts, clusterTotals, new Instances(inst, 0));
/*  254:     */     
/*  255: 433 */     this.m_clusteringResults.append(matrixString).append("\n");
/*  256:     */     
/*  257: 435 */     int Cwidth = 1 + (int)(Math.log(this.m_numClusters) / Math.log(10.0D));
/*  258: 437 */     for (i = 0; i < this.m_numClusters; i++) {
/*  259: 438 */       if (clusterTotals[i] > 0)
/*  260:     */       {
/*  261: 439 */         this.m_clusteringResults.append("Cluster " + Utils.doubleToString(i, Cwidth, 0));
/*  262:     */         
/*  263: 441 */         this.m_clusteringResults.append(" <-- ");
/*  264: 443 */         if (best[i] < 0.0D) {
/*  265: 444 */           this.m_clusteringResults.append("No class\n");
/*  266:     */         } else {
/*  267: 446 */           this.m_clusteringResults.append(inst.classAttribute().value((int)best[i])).append("\n");
/*  268:     */         }
/*  269:     */       }
/*  270:     */     }
/*  271: 451 */     this.m_clusteringResults.append("\nIncorrectly clustered instances :\t" + best[this.m_numClusters] + "\t" + Utils.doubleToString(best[this.m_numClusters] / numInstances * 100.0D, 8, 4) + " %\n");
/*  272:     */     
/*  273:     */ 
/*  274:     */ 
/*  275:     */ 
/*  276:     */ 
/*  277:     */ 
/*  278: 458 */     this.m_classToCluster = new int[this.m_numClusters];
/*  279: 459 */     for (i = 0; i < this.m_numClusters; i++) {
/*  280: 460 */       this.m_classToCluster[i] = ((int)best[i]);
/*  281:     */     }
/*  282:     */   }
/*  283:     */   
/*  284:     */   private String toMatrixString(int[][] counts, int[] clusterTotals, Instances inst)
/*  285:     */     throws Exception
/*  286:     */   {
/*  287: 475 */     StringBuffer ms = new StringBuffer();
/*  288:     */     
/*  289: 477 */     int maxval = 0;
/*  290: 478 */     for (int i = 0; i < this.m_numClusters; i++) {
/*  291: 479 */       for (int j = 0; j < counts[i].length; j++) {
/*  292: 480 */         if (counts[i][j] > maxval) {
/*  293: 481 */           maxval = counts[i][j];
/*  294:     */         }
/*  295:     */       }
/*  296:     */     }
/*  297: 486 */     int Cwidth = 1 + Math.max((int)(Math.log(maxval) / Math.log(10.0D)), (int)(Math.log(this.m_numClusters) / Math.log(10.0D)));
/*  298:     */     
/*  299:     */ 
/*  300:     */ 
/*  301: 490 */     ms.append("\n");
/*  302: 492 */     for (int i = 0; i < this.m_numClusters; i++) {
/*  303: 493 */       if (clusterTotals[i] > 0) {
/*  304: 494 */         ms.append(" ").append(Utils.doubleToString(i, Cwidth, 0));
/*  305:     */       }
/*  306:     */     }
/*  307: 497 */     ms.append("  <-- assigned to cluster\n");
/*  308: 499 */     for (int i = 0; i < counts[0].length; i++)
/*  309:     */     {
/*  310: 501 */       for (int j = 0; j < this.m_numClusters; j++) {
/*  311: 502 */         if (clusterTotals[j] > 0) {
/*  312: 503 */           ms.append(" ").append(Utils.doubleToString(counts[j][i], Cwidth, 0));
/*  313:     */         }
/*  314:     */       }
/*  315: 506 */       ms.append(" | ").append(inst.classAttribute().value(i)).append("\n");
/*  316:     */     }
/*  317: 509 */     return ms.toString();
/*  318:     */   }
/*  319:     */   
/*  320:     */   public static void mapClasses(int numClusters, int lev, int[][] counts, int[] clusterTotals, double[] current, double[] best, int error)
/*  321:     */   {
/*  322: 528 */     if (lev == numClusters)
/*  323:     */     {
/*  324: 529 */       if (error < best[numClusters])
/*  325:     */       {
/*  326: 530 */         best[numClusters] = error;
/*  327: 531 */         for (int i = 0; i < numClusters; i++) {
/*  328: 532 */           best[i] = current[i];
/*  329:     */         }
/*  330:     */       }
/*  331:     */     }
/*  332: 537 */     else if (clusterTotals[lev] == 0)
/*  333:     */     {
/*  334: 538 */       current[lev] = -1.0D;
/*  335: 539 */       mapClasses(numClusters, lev + 1, counts, clusterTotals, current, best, error);
/*  336:     */     }
/*  337:     */     else
/*  338:     */     {
/*  339: 543 */       current[lev] = -1.0D;
/*  340: 544 */       mapClasses(numClusters, lev + 1, counts, clusterTotals, current, best, error + clusterTotals[lev]);
/*  341: 547 */       for (int i = 0; i < counts[0].length; i++) {
/*  342: 548 */         if (counts[lev][i] > 0)
/*  343:     */         {
/*  344: 549 */           boolean ok = true;
/*  345: 551 */           for (int j = 0; j < lev; j++) {
/*  346: 552 */             if ((int)current[j] == i)
/*  347:     */             {
/*  348: 553 */               ok = false;
/*  349: 554 */               break;
/*  350:     */             }
/*  351:     */           }
/*  352: 557 */           if (ok)
/*  353:     */           {
/*  354: 558 */             current[lev] = i;
/*  355: 559 */             mapClasses(numClusters, lev + 1, counts, clusterTotals, current, best, error + (clusterTotals[lev] - counts[lev][i]));
/*  356:     */           }
/*  357:     */         }
/*  358:     */       }
/*  359:     */     }
/*  360:     */   }
/*  361:     */   
/*  362:     */   public static String evaluateClusterer(Clusterer clusterer, String[] options)
/*  363:     */     throws Exception
/*  364:     */   {
/*  365: 588 */     int seed = 1;int folds = 10;
/*  366: 589 */     boolean doXval = false;
/*  367: 590 */     Instances train = null;
/*  368:     */     
/*  369:     */ 
/*  370:     */ 
/*  371:     */ 
/*  372: 595 */     String[] savedOptions = null;
/*  373: 596 */     boolean printClusterAssignments = false;
/*  374: 597 */     Range attributesToOutput = null;
/*  375: 598 */     StringBuffer text = new StringBuffer();
/*  376: 599 */     int theClass = -1;
/*  377: 600 */     boolean forceBatch = Utils.getFlag("force-batch-training", options);
/*  378: 601 */     boolean updateable = ((clusterer instanceof UpdateableClusterer)) && (!forceBatch);
/*  379:     */     
/*  380: 603 */     ConverterUtils.DataSource source = null;
/*  381: 606 */     if ((Utils.getFlag('h', options)) || (Utils.getFlag("help", options)))
/*  382:     */     {
/*  383: 609 */       boolean globalInfo = (Utils.getFlag("synopsis", options)) || (Utils.getFlag("info", options));
/*  384:     */       
/*  385:     */ 
/*  386: 612 */       throw new Exception("Help requested." + makeOptionString(clusterer, globalInfo));
/*  387:     */     }
/*  388:     */     String objectInputFileName;
/*  389:     */     String objectOutputFileName;
/*  390:     */     String trainFileName;
/*  391:     */     String testFileName;
/*  392:     */     String graphFileName;
/*  393:     */     try
/*  394:     */     {
/*  395: 619 */       objectInputFileName = Utils.getOption('l', options);
/*  396: 620 */       objectOutputFileName = Utils.getOption('d', options);
/*  397: 621 */       trainFileName = Utils.getOption('t', options);
/*  398: 622 */       testFileName = Utils.getOption('T', options);
/*  399: 623 */       graphFileName = Utils.getOption('g', options);
/*  400:     */       String attributeRangeString;
/*  401:     */       try
/*  402:     */       {
/*  403: 627 */         attributeRangeString = Utils.getOption('p', options);
/*  404:     */       }
/*  405:     */       catch (Exception e)
/*  406:     */       {
/*  407: 629 */         throw new Exception(e.getMessage() + "\nNOTE: the -p option has changed. " + "It now expects a parameter specifying a range of attributes " + "to list with the predictions. Use '-p 0' for none.");
/*  408:     */       }
/*  409: 634 */       if (attributeRangeString.length() != 0)
/*  410:     */       {
/*  411: 635 */         printClusterAssignments = true;
/*  412: 636 */         if (!attributeRangeString.equals("0")) {
/*  413: 637 */           attributesToOutput = new Range(attributeRangeString);
/*  414:     */         }
/*  415:     */       }
/*  416: 641 */       if (trainFileName.length() == 0)
/*  417:     */       {
/*  418: 642 */         if (objectInputFileName.length() == 0) {
/*  419: 643 */           throw new Exception("No training file and no object input file given.");
/*  420:     */         }
/*  421: 647 */         if (testFileName.length() == 0) {
/*  422: 648 */           throw new Exception("No training file and no test file given.");
/*  423:     */         }
/*  424:     */       }
/*  425: 651 */       else if ((objectInputFileName.length() != 0) && (!printClusterAssignments))
/*  426:     */       {
/*  427: 653 */         throw new Exception("Can't use both train and model file unless -p specified.");
/*  428:     */       }
/*  429: 658 */       String seedString = Utils.getOption('s', options);
/*  430: 660 */       if (seedString.length() != 0) {
/*  431: 661 */         seed = Integer.parseInt(seedString);
/*  432:     */       }
/*  433: 664 */       String foldsString = Utils.getOption('x', options);
/*  434: 666 */       if (foldsString.length() != 0)
/*  435:     */       {
/*  436: 667 */         folds = Integer.parseInt(foldsString);
/*  437: 668 */         doXval = true;
/*  438:     */       }
/*  439:     */     }
/*  440:     */     catch (Exception e)
/*  441:     */     {
/*  442: 671 */       throw new Exception('\n' + e.getMessage() + makeOptionString(clusterer, false));
/*  443:     */     }
/*  444:     */     try
/*  445:     */     {
/*  446: 676 */       if (trainFileName.length() != 0)
/*  447:     */       {
/*  448: 677 */         source = new ConverterUtils.DataSource(trainFileName);
/*  449: 678 */         train = source.getStructure();
/*  450:     */         
/*  451: 680 */         String classString = Utils.getOption('c', options);
/*  452: 681 */         if (classString.length() != 0)
/*  453:     */         {
/*  454: 682 */           if (classString.compareTo("last") == 0) {
/*  455: 683 */             theClass = train.numAttributes();
/*  456: 684 */           } else if (classString.compareTo("first") == 0) {
/*  457: 685 */             theClass = 1;
/*  458:     */           } else {
/*  459: 687 */             theClass = Integer.parseInt(classString);
/*  460:     */           }
/*  461: 690 */           if (theClass != -1)
/*  462:     */           {
/*  463: 691 */             if ((doXval) || (testFileName.length() != 0)) {
/*  464: 692 */               throw new Exception("Can only do class based evaluation on the training data");
/*  465:     */             }
/*  466: 696 */             if (objectInputFileName.length() != 0) {
/*  467: 697 */               throw new Exception("Can't load a clusterer and do class based evaluation");
/*  468:     */             }
/*  469: 701 */             if (objectOutputFileName.length() != 0) {
/*  470: 702 */               throw new Exception("Can't do class based evaluation and save clusterer");
/*  471:     */             }
/*  472:     */           }
/*  473:     */         }
/*  474: 708 */         else if (train.classIndex() != -1)
/*  475:     */         {
/*  476: 709 */           theClass = train.classIndex() + 1;
/*  477: 710 */           System.err.println("Note: using class attribute from dataset, i.e., attribute #" + theClass);
/*  478:     */         }
/*  479: 716 */         if (theClass != -1)
/*  480:     */         {
/*  481: 717 */           if ((theClass < 1) || (theClass > train.numAttributes())) {
/*  482: 718 */             throw new Exception("Class is out of range!");
/*  483:     */           }
/*  484: 721 */           if (!train.attribute(theClass - 1).isNominal()) {
/*  485: 722 */             throw new Exception("Class must be nominal!");
/*  486:     */           }
/*  487: 725 */           train.setClassIndex(theClass - 1);
/*  488:     */         }
/*  489:     */       }
/*  490:     */     }
/*  491:     */     catch (Exception e)
/*  492:     */     {
/*  493: 729 */       throw new Exception("ClusterEvaluation: " + e.getMessage() + '.');
/*  494:     */     }
/*  495: 733 */     if (options != null)
/*  496:     */     {
/*  497: 734 */       savedOptions = new String[options.length];
/*  498: 735 */       System.arraycopy(options, 0, savedOptions, 0, options.length);
/*  499:     */     }
/*  500: 738 */     if (objectInputFileName.length() != 0) {
/*  501: 739 */       Utils.checkForRemainingOptions(options);
/*  502:     */     }
/*  503: 743 */     if ((clusterer instanceof OptionHandler)) {
/*  504: 744 */       ((OptionHandler)clusterer).setOptions(options);
/*  505:     */     }
/*  506: 747 */     Utils.checkForRemainingOptions(options);
/*  507:     */     
/*  508: 749 */     Instances trainHeader = train;
/*  509: 750 */     if (objectInputFileName.length() != 0)
/*  510:     */     {
/*  511: 753 */       ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(objectInputFileName)));
/*  512:     */       
/*  513:     */ 
/*  514: 756 */       clusterer = (Clusterer)ois.readObject();
/*  515:     */       try
/*  516:     */       {
/*  517: 759 */         trainHeader = (Instances)ois.readObject();
/*  518:     */       }
/*  519:     */       catch (Exception ex) {}
/*  520: 763 */       ois.close();
/*  521:     */     }
/*  522: 766 */     else if (theClass == -1)
/*  523:     */     {
/*  524: 767 */       if (updateable)
/*  525:     */       {
/*  526: 768 */         clusterer.buildClusterer(source.getStructure());
/*  527: 769 */         while (source.hasMoreElements(train))
/*  528:     */         {
/*  529: 770 */           Instance inst = source.nextElement(train);
/*  530: 771 */           ((UpdateableClusterer)clusterer).updateClusterer(inst);
/*  531:     */         }
/*  532: 773 */         ((UpdateableClusterer)clusterer).updateFinished();
/*  533:     */       }
/*  534:     */       else
/*  535:     */       {
/*  536: 775 */         clusterer.buildClusterer(source.getDataSet());
/*  537:     */       }
/*  538:     */     }
/*  539:     */     else
/*  540:     */     {
/*  541: 778 */       Remove removeClass = new Remove();
/*  542: 779 */       removeClass.setAttributeIndices("" + theClass);
/*  543: 780 */       removeClass.setInvertSelection(false);
/*  544: 781 */       removeClass.setInputFormat(train);
/*  545: 782 */       if (updateable)
/*  546:     */       {
/*  547: 783 */         Instances clusterTrain = Filter.useFilter(train, removeClass);
/*  548: 784 */         clusterer.buildClusterer(clusterTrain);
/*  549: 785 */         trainHeader = clusterTrain;
/*  550: 786 */         while (source.hasMoreElements(train))
/*  551:     */         {
/*  552: 787 */           Instance inst = source.nextElement(train);
/*  553: 788 */           removeClass.input(inst);
/*  554: 789 */           removeClass.batchFinished();
/*  555: 790 */           Instance clusterTrainInst = removeClass.output();
/*  556: 791 */           ((UpdateableClusterer)clusterer).updateClusterer(clusterTrainInst);
/*  557:     */         }
/*  558: 793 */         ((UpdateableClusterer)clusterer).updateFinished();
/*  559:     */       }
/*  560:     */       else
/*  561:     */       {
/*  562: 795 */         Instances clusterTrain = Filter.useFilter(source.getDataSet(), removeClass);
/*  563:     */         
/*  564: 797 */         clusterer.buildClusterer(clusterTrain);
/*  565: 798 */         trainHeader = clusterTrain;
/*  566:     */       }
/*  567: 800 */       ClusterEvaluation ce = new ClusterEvaluation();
/*  568: 801 */       ce.setClusterer(clusterer);
/*  569: 802 */       ce.evaluateClusterer(train, trainFileName);
/*  570:     */       
/*  571: 804 */       return "\n\n=== Clustering stats for training data ===\n\n" + ce.clusterResultsToString();
/*  572:     */     }
/*  573: 813 */     if (printClusterAssignments) {
/*  574: 814 */       return printClusterings(clusterer, trainFileName, testFileName, attributesToOutput);
/*  575:     */     }
/*  576: 818 */     text.append(clusterer.toString());
/*  577: 819 */     text.append("\n\n=== Clustering stats for training data ===\n\n" + printClusterStats(clusterer, trainFileName));
/*  578: 822 */     if (testFileName.length() != 0)
/*  579:     */     {
/*  580: 824 */       ConverterUtils.DataSource test = new ConverterUtils.DataSource(testFileName);
/*  581: 825 */       Instances testStructure = test.getStructure();
/*  582: 826 */       if (!trainHeader.equalHeaders(testStructure)) {
/*  583: 827 */         throw new Exception("Training and testing data are not compatible\n" + trainHeader.equalHeadersMsg(testStructure));
/*  584:     */       }
/*  585: 831 */       text.append("\n\n=== Clustering stats for testing data ===\n\n" + printClusterStats(clusterer, testFileName));
/*  586:     */     }
/*  587: 835 */     if (((clusterer instanceof DensityBasedClusterer)) && (doXval == true) && (testFileName.length() == 0) && (objectInputFileName.length() == 0))
/*  588:     */     {
/*  589: 838 */       Random random = new Random(seed);
/*  590: 839 */       random.setSeed(seed);
/*  591: 840 */       train = source.getDataSet();
/*  592: 841 */       train.randomize(random);
/*  593: 842 */       text.append(crossValidateModel(clusterer.getClass().getName(), train, folds, savedOptions, random));
/*  594:     */     }
/*  595: 847 */     if (objectOutputFileName.length() != 0) {
/*  596: 849 */       saveClusterer(objectOutputFileName, clusterer, trainHeader);
/*  597:     */     }
/*  598: 853 */     if (((clusterer instanceof Drawable)) && (graphFileName.length() != 0))
/*  599:     */     {
/*  600: 854 */       BufferedWriter writer = new BufferedWriter(new FileWriter(graphFileName));
/*  601: 855 */       writer.write(((Drawable)clusterer).graph());
/*  602: 856 */       writer.newLine();
/*  603: 857 */       writer.flush();
/*  604: 858 */       writer.close();
/*  605:     */     }
/*  606: 861 */     return text.toString();
/*  607:     */   }
/*  608:     */   
/*  609:     */   private static void saveClusterer(String fileName, Clusterer clusterer, Instances header)
/*  610:     */     throws Exception
/*  611:     */   {
/*  612: 866 */     ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
/*  613:     */     
/*  614:     */ 
/*  615:     */ 
/*  616: 870 */     oos.writeObject(clusterer);
/*  617: 871 */     if (header != null) {
/*  618: 872 */       oos.writeObject(header);
/*  619:     */     }
/*  620: 874 */     oos.flush();
/*  621: 875 */     oos.close();
/*  622:     */   }
/*  623:     */   
/*  624:     */   public static double crossValidateModel(DensityBasedClusterer clusterer, Instances data, int numFolds, Random random)
/*  625:     */     throws Exception
/*  626:     */   {
/*  627: 891 */     double foldAv = 0.0D;
/*  628:     */     
/*  629: 893 */     data = new Instances(data);
/*  630: 894 */     data.randomize(random);
/*  631: 896 */     for (int i = 0; i < numFolds; i++)
/*  632:     */     {
/*  633: 898 */       Instances train = data.trainCV(numFolds, i, random);
/*  634:     */       
/*  635: 900 */       clusterer.buildClusterer(train);
/*  636:     */       
/*  637: 902 */       Instances test = data.testCV(numFolds, i);
/*  638: 904 */       for (int j = 0; j < test.numInstances(); j++) {
/*  639:     */         try
/*  640:     */         {
/*  641: 906 */           foldAv += clusterer.logDensityForInstance(test.instance(j));
/*  642:     */         }
/*  643:     */         catch (Exception ex) {}
/*  644:     */       }
/*  645:     */     }
/*  646: 916 */     return foldAv / data.numInstances();
/*  647:     */   }
/*  648:     */   
/*  649:     */   public static String crossValidateModel(String clustererString, Instances data, int numFolds, String[] options, Random random)
/*  650:     */     throws Exception
/*  651:     */   {
/*  652: 934 */     Clusterer clusterer = null;
/*  653: 935 */     String[] savedOptions = null;
/*  654: 936 */     double CvAv = 0.0D;
/*  655: 937 */     StringBuffer CvString = new StringBuffer();
/*  656: 939 */     if (options != null) {
/*  657: 940 */       savedOptions = new String[options.length];
/*  658:     */     }
/*  659: 943 */     data = new Instances(data);
/*  660:     */     try
/*  661:     */     {
/*  662: 947 */       clusterer = (Clusterer)Class.forName(clustererString).newInstance();
/*  663:     */     }
/*  664:     */     catch (Exception e)
/*  665:     */     {
/*  666: 949 */       throw new Exception("Can't find class with name " + clustererString + '.');
/*  667:     */     }
/*  668: 952 */     if (!(clusterer instanceof DensityBasedClusterer)) {
/*  669: 953 */       throw new Exception(clustererString + " must be a distrinbution " + "clusterer.");
/*  670:     */     }
/*  671: 958 */     if (options != null) {
/*  672: 959 */       System.arraycopy(options, 0, savedOptions, 0, options.length);
/*  673:     */     }
/*  674: 963 */     if ((clusterer instanceof OptionHandler)) {
/*  675:     */       try
/*  676:     */       {
/*  677: 965 */         ((OptionHandler)clusterer).setOptions(savedOptions);
/*  678: 966 */         Utils.checkForRemainingOptions(savedOptions);
/*  679:     */       }
/*  680:     */       catch (Exception e)
/*  681:     */       {
/*  682: 968 */         throw new Exception("Can't parse given options in cross-validation!");
/*  683:     */       }
/*  684:     */     }
/*  685: 972 */     CvAv = crossValidateModel((DensityBasedClusterer)clusterer, data, numFolds, random);
/*  686:     */     
/*  687:     */ 
/*  688:     */ 
/*  689: 976 */     CvString.append("\n" + numFolds + " fold CV Log Likelihood: " + Utils.doubleToString(CvAv, 6, 4) + "\n");
/*  690:     */     
/*  691: 978 */     return CvString.toString();
/*  692:     */   }
/*  693:     */   
/*  694:     */   private static String printClusterStats(Clusterer clusterer, String fileName)
/*  695:     */     throws Exception
/*  696:     */   {
/*  697: 994 */     StringBuffer text = new StringBuffer();
/*  698: 995 */     int i = 0;
/*  699:     */     
/*  700: 997 */     double loglk = 0.0D;
/*  701: 998 */     int cc = clusterer.numberOfClusters();
/*  702: 999 */     double[] instanceStats = new double[cc];
/*  703:1000 */     int unclusteredInstances = 0;
/*  704:1002 */     if (fileName.length() != 0)
/*  705:     */     {
/*  706:1003 */       ConverterUtils.DataSource source = new ConverterUtils.DataSource(fileName);
/*  707:1004 */       Instances structure = source.getStructure();
/*  708:1005 */       Instances forBatchPredictors = ((clusterer instanceof BatchPredictor)) && (((BatchPredictor)clusterer).implementsMoreEfficientBatchPrediction()) ? new Instances(source.getStructure(), 0) : null;
/*  709:     */       int cnum;
/*  710:1011 */       while (source.hasMoreElements(structure))
/*  711:     */       {
/*  712:1012 */         Instance inst = source.nextElement(structure);
/*  713:1013 */         if (forBatchPredictors != null)
/*  714:     */         {
/*  715:1014 */           forBatchPredictors.add(inst);
/*  716:     */         }
/*  717:     */         else
/*  718:     */         {
/*  719:     */           try
/*  720:     */           {
/*  721:1017 */             cnum = clusterer.clusterInstance(inst);
/*  722:1019 */             if ((clusterer instanceof DensityBasedClusterer)) {
/*  723:1020 */               loglk += ((DensityBasedClusterer)clusterer).logDensityForInstance(inst);
/*  724:     */             }
/*  725:1024 */             instanceStats[cnum] += 1.0D;
/*  726:     */           }
/*  727:     */           catch (Exception e)
/*  728:     */           {
/*  729:1026 */             unclusteredInstances++;
/*  730:     */           }
/*  731:1028 */           i++;
/*  732:     */         }
/*  733:     */       }
/*  734:1032 */       if (forBatchPredictors != null)
/*  735:     */       {
/*  736:1033 */         double[][] dists = ((BatchPredictor)clusterer).distributionsForInstances(forBatchPredictors);
/*  737:1036 */         for (double[] d : dists)
/*  738:     */         {
/*  739:1037 */           cnum = Utils.maxIndex(d);
/*  740:1038 */           instanceStats[cnum] += 1.0D;
/*  741:     */         }
/*  742:     */       }
/*  743:1050 */       int clustFieldWidth = (int)(Math.log(cc) / Math.log(10.0D) + 1.0D);
/*  744:1051 */       int numInstFieldWidth = (int)(Math.log(i) / Math.log(10.0D) + 1.0D);
/*  745:1052 */       double sum = Utils.sum(instanceStats);
/*  746:1053 */       loglk /= sum;
/*  747:1054 */       text.append("Clustered Instances\n");
/*  748:1056 */       for (i = 0; i < cc; i++) {
/*  749:1057 */         if (instanceStats[i] > 0.0D) {
/*  750:1058 */           text.append(Utils.doubleToString(i, clustFieldWidth, 0) + "      " + Utils.doubleToString(instanceStats[i], numInstFieldWidth, 0) + " (" + Utils.doubleToString(instanceStats[i] / sum * 100.0D, 3, 0) + "%)\n");
/*  751:     */         }
/*  752:     */       }
/*  753:1065 */       if (unclusteredInstances > 0) {
/*  754:1066 */         text.append("\nUnclustered Instances : " + unclusteredInstances);
/*  755:     */       }
/*  756:1069 */       if ((clusterer instanceof DensityBasedClusterer)) {
/*  757:1070 */         text.append("\n\nLog likelihood: " + Utils.doubleToString(loglk, 1, 5) + "\n");
/*  758:     */       }
/*  759:     */     }
/*  760:1075 */     return text.toString();
/*  761:     */   }
/*  762:     */   
/*  763:     */   private static String printClusterings(Clusterer clusterer, String trainFileName, String testFileName, Range attributesToOutput)
/*  764:     */     throws Exception
/*  765:     */   {
/*  766:1092 */     StringBuffer text = new StringBuffer();
/*  767:1093 */     int i = 0;
/*  768:     */     
/*  769:1095 */     ConverterUtils.DataSource source = null;
/*  770:1099 */     if (testFileName.length() != 0) {
/*  771:1100 */       source = new ConverterUtils.DataSource(testFileName);
/*  772:     */     } else {
/*  773:1102 */       source = new ConverterUtils.DataSource(trainFileName);
/*  774:     */     }
/*  775:1105 */     Instances structure = source.getStructure();
/*  776:1106 */     Instances forBatchPredictors = ((clusterer instanceof BatchPredictor)) && (((BatchPredictor)clusterer).implementsMoreEfficientBatchPrediction()) ? new Instances(source.getStructure(), 0) : null;
/*  777:     */     int cnum;
/*  778:1110 */     while (source.hasMoreElements(structure))
/*  779:     */     {
/*  780:1111 */       Instance inst = source.nextElement(structure);
/*  781:1112 */       if (forBatchPredictors != null)
/*  782:     */       {
/*  783:1113 */         forBatchPredictors.add(inst);
/*  784:     */       }
/*  785:     */       else
/*  786:     */       {
/*  787:     */         try
/*  788:     */         {
/*  789:1116 */           cnum = clusterer.clusterInstance(inst);
/*  790:     */           
/*  791:1118 */           text.append(i + " " + cnum + " " + attributeValuesString(inst, attributesToOutput) + "\n");
/*  792:     */         }
/*  793:     */         catch (Exception e)
/*  794:     */         {
/*  795:1125 */           text.append(i + " Unclustered " + attributeValuesString(inst, attributesToOutput) + "\n");
/*  796:     */         }
/*  797:1128 */         i++;
/*  798:     */       }
/*  799:     */     }
/*  800:1132 */     if (forBatchPredictors != null)
/*  801:     */     {
/*  802:1133 */       double[][] dists = ((BatchPredictor)clusterer).distributionsForInstances(forBatchPredictors);
/*  803:1136 */       for (double[] d : dists)
/*  804:     */       {
/*  805:1137 */         cnum = Utils.maxIndex(d);
/*  806:1138 */         text.append(i + " " + cnum + " " + attributeValuesString(forBatchPredictors.instance(i), attributesToOutput) + "\n");
/*  807:     */         
/*  808:     */ 
/*  809:     */ 
/*  810:     */ 
/*  811:     */ 
/*  812:1144 */         i++;
/*  813:     */       }
/*  814:     */     }
/*  815:1148 */     return text.toString();
/*  816:     */   }
/*  817:     */   
/*  818:     */   private static String attributeValuesString(Instance instance, Range attRange)
/*  819:     */   {
/*  820:1161 */     StringBuffer text = new StringBuffer();
/*  821:1162 */     if (attRange != null)
/*  822:     */     {
/*  823:1163 */       boolean firstOutput = true;
/*  824:1164 */       attRange.setUpper(instance.numAttributes() - 1);
/*  825:1165 */       for (int i = 0; i < instance.numAttributes(); i++) {
/*  826:1166 */         if (attRange.isInRange(i))
/*  827:     */         {
/*  828:1167 */           if (firstOutput) {
/*  829:1168 */             text.append("(");
/*  830:     */           } else {
/*  831:1170 */             text.append(",");
/*  832:     */           }
/*  833:1172 */           text.append(instance.toString(i));
/*  834:1173 */           firstOutput = false;
/*  835:     */         }
/*  836:     */       }
/*  837:1176 */       if (!firstOutput) {
/*  838:1177 */         text.append(")");
/*  839:     */       }
/*  840:     */     }
/*  841:1180 */     return text.toString();
/*  842:     */   }
/*  843:     */   
/*  844:     */   private static String makeOptionString(Clusterer clusterer, boolean globalInfo)
/*  845:     */   {
/*  846:1191 */     StringBuffer optionsText = new StringBuffer("");
/*  847:     */     
/*  848:1193 */     optionsText.append("\n\nGeneral options:\n\n");
/*  849:1194 */     optionsText.append("-h or -help\n");
/*  850:1195 */     optionsText.append("\tOutput help information.\n");
/*  851:1196 */     optionsText.append("-synopsis or -info\n");
/*  852:1197 */     optionsText.append("\tOutput synopsis for clusterer (use in conjunction  with -h)\n");
/*  853:     */     
/*  854:1199 */     optionsText.append("-t <name of training file>\n");
/*  855:1200 */     optionsText.append("\tSets training file.\n");
/*  856:1201 */     optionsText.append("-T <name of test file>\n");
/*  857:1202 */     optionsText.append("\tSets test file.\n");
/*  858:1203 */     optionsText.append("-force-batch-training\n");
/*  859:1204 */     optionsText.append("\tAlways train the clusterer in batch mode, never incrementally.\n");
/*  860:     */     
/*  861:1206 */     optionsText.append("-l <name of input file>\n");
/*  862:1207 */     optionsText.append("\tSets model input file.\n");
/*  863:1208 */     optionsText.append("-d <name of output file>\n");
/*  864:1209 */     optionsText.append("\tSets model output file.\n");
/*  865:1210 */     optionsText.append("-p <attribute range>\n");
/*  866:1211 */     optionsText.append("\tOutput predictions. Predictions are for training file\n\tif only training file is specified,\n\totherwise predictions are for the test file.\n\tThe range specifies attribute values to be output\n\twith the predictions. Use '-p 0' for none.\n");
/*  867:     */     
/*  868:     */ 
/*  869:     */ 
/*  870:     */ 
/*  871:1216 */     optionsText.append("-x <number of folds>\n");
/*  872:1217 */     optionsText.append("\tOnly Distribution Clusterers can be cross validated.\n");
/*  873:     */     
/*  874:1219 */     optionsText.append("-s <random number seed>\n");
/*  875:1220 */     optionsText.append("\tSets the seed for randomizing the data in cross-validation\n");
/*  876:     */     
/*  877:1222 */     optionsText.append("-c <class index>\n");
/*  878:1223 */     optionsText.append("\tSet class attribute. If supplied, class is ignored");
/*  879:1224 */     optionsText.append("\n\tduring clustering but is used in a classes to");
/*  880:1225 */     optionsText.append("\n\tclusters evaluation.\n");
/*  881:1226 */     if ((clusterer instanceof Drawable))
/*  882:     */     {
/*  883:1227 */       optionsText.append("-g <name of graph file>\n");
/*  884:1228 */       optionsText.append("\tOutputs the graph representation of the clusterer to the file.\n");
/*  885:     */     }
/*  886:1233 */     if ((clusterer instanceof OptionHandler))
/*  887:     */     {
/*  888:1234 */       optionsText.append("\nOptions specific to " + clusterer.getClass().getName() + ":\n\n");
/*  889:     */       
/*  890:1236 */       Enumeration<Option> enu = ((OptionHandler)clusterer).listOptions();
/*  891:1238 */       while (enu.hasMoreElements())
/*  892:     */       {
/*  893:1239 */         Option option = (Option)enu.nextElement();
/*  894:1240 */         optionsText.append(option.synopsis() + '\n');
/*  895:1241 */         optionsText.append(option.description() + "\n");
/*  896:     */       }
/*  897:     */     }
/*  898:1246 */     if (globalInfo) {
/*  899:     */       try
/*  900:     */       {
/*  901:1248 */         String gi = getGlobalInfo(clusterer);
/*  902:1249 */         optionsText.append(gi);
/*  903:     */       }
/*  904:     */       catch (Exception ex) {}
/*  905:     */     }
/*  906:1255 */     return optionsText.toString();
/*  907:     */   }
/*  908:     */   
/*  909:     */   protected static String getGlobalInfo(Clusterer clusterer)
/*  910:     */     throws Exception
/*  911:     */   {
/*  912:1266 */     BeanInfo bi = Introspector.getBeanInfo(clusterer.getClass());
/*  913:     */     
/*  914:1268 */     MethodDescriptor[] methods = bi.getMethodDescriptors();
/*  915:1269 */     Object[] args = new Object[0];
/*  916:1270 */     String result = "\nSynopsis for " + clusterer.getClass().getName() + ":\n\n";
/*  917:1273 */     for (MethodDescriptor method : methods)
/*  918:     */     {
/*  919:1274 */       String name = method.getDisplayName();
/*  920:1275 */       Method meth = method.getMethod();
/*  921:1276 */       if (name.equals("globalInfo"))
/*  922:     */       {
/*  923:1277 */         String globalInfo = (String)meth.invoke(clusterer, args);
/*  924:1278 */         result = result + globalInfo;
/*  925:1279 */         break;
/*  926:     */       }
/*  927:     */     }
/*  928:1283 */     return result;
/*  929:     */   }
/*  930:     */   
/*  931:     */   public boolean equals(Object obj)
/*  932:     */   {
/*  933:1295 */     if ((obj == null) || (!obj.getClass().equals(getClass()))) {
/*  934:1296 */       return false;
/*  935:     */     }
/*  936:1299 */     ClusterEvaluation cmp = (ClusterEvaluation)obj;
/*  937:1301 */     if ((this.m_classToCluster != null ? 1 : 0) != (cmp.m_classToCluster != null ? 1 : 0)) {
/*  938:1302 */       return false;
/*  939:     */     }
/*  940:1304 */     if (this.m_classToCluster != null) {
/*  941:1305 */       for (int i = 0; i < this.m_classToCluster.length; i++) {
/*  942:1306 */         if (this.m_classToCluster[i] != cmp.m_classToCluster[i]) {
/*  943:1307 */           return false;
/*  944:     */         }
/*  945:     */       }
/*  946:     */     }
/*  947:1312 */     if ((this.m_clusterAssignments != null ? 1 : 0) != (cmp.m_clusterAssignments != null ? 1 : 0)) {
/*  948:1313 */       return false;
/*  949:     */     }
/*  950:1315 */     if (this.m_clusterAssignments != null) {
/*  951:1316 */       for (int i = 0; i < this.m_clusterAssignments.length; i++) {
/*  952:1317 */         if (this.m_clusterAssignments[i] != cmp.m_clusterAssignments[i]) {
/*  953:1318 */           return false;
/*  954:     */         }
/*  955:     */       }
/*  956:     */     }
/*  957:1323 */     if (Double.isNaN(this.m_logL) != Double.isNaN(cmp.m_logL)) {
/*  958:1324 */       return false;
/*  959:     */     }
/*  960:1326 */     if ((!Double.isNaN(this.m_logL)) && 
/*  961:1327 */       (this.m_logL != cmp.m_logL)) {
/*  962:1328 */       return false;
/*  963:     */     }
/*  964:1332 */     if (this.m_numClusters != cmp.m_numClusters) {
/*  965:1333 */       return false;
/*  966:     */     }
/*  967:1337 */     String clusteringResults1 = this.m_clusteringResults.toString().replaceAll("Elapsed time.*", "");
/*  968:     */     
/*  969:1339 */     String clusteringResults2 = cmp.m_clusteringResults.toString().replaceAll("Elapsed time.*", "");
/*  970:1341 */     if (!clusteringResults1.equals(clusteringResults2)) {
/*  971:1342 */       return false;
/*  972:     */     }
/*  973:1345 */     return true;
/*  974:     */   }
/*  975:     */   
/*  976:     */   public String getRevision()
/*  977:     */   {
/*  978:1355 */     return RevisionUtils.extract("$Revision: 11958 $");
/*  979:     */   }
/*  980:     */   
/*  981:     */   public static void main(String[] args)
/*  982:     */   {
/*  983:     */     try
/*  984:     */     {
/*  985:1365 */       if (args.length == 0) {
/*  986:1366 */         throw new Exception("The first argument must be the name of a clusterer");
/*  987:     */       }
/*  988:1370 */       String ClustererString = args[0];
/*  989:1371 */       args[0] = "";
/*  990:1372 */       Clusterer newClusterer = AbstractClusterer.forName(ClustererString, null);
/*  991:1373 */       System.out.println(evaluateClusterer(newClusterer, args));
/*  992:     */     }
/*  993:     */     catch (Exception e)
/*  994:     */     {
/*  995:1375 */       System.out.println(e.getMessage());
/*  996:     */     }
/*  997:     */   }
/*  998:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.clusterers.ClusterEvaluation
 * JD-Core Version:    0.7.0.1
 */