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
/*   11:     */ import weka.classifiers.rules.DecisionTableHashKey;
/*   12:     */ import weka.core.Attribute;
/*   13:     */ import weka.core.AttributeStats;
/*   14:     */ import weka.core.Capabilities;
/*   15:     */ import weka.core.Capabilities.Capability;
/*   16:     */ import weka.core.DenseInstance;
/*   17:     */ import weka.core.EuclideanDistance;
/*   18:     */ import weka.core.Instance;
/*   19:     */ import weka.core.Instances;
/*   20:     */ import weka.core.NormalizableDistance;
/*   21:     */ import weka.core.Option;
/*   22:     */ import weka.core.OptionHandler;
/*   23:     */ import weka.core.SparseInstance;
/*   24:     */ import weka.core.TechnicalInformation;
/*   25:     */ import weka.core.TechnicalInformation.Field;
/*   26:     */ import weka.core.TechnicalInformation.Type;
/*   27:     */ import weka.core.TechnicalInformationHandler;
/*   28:     */ import weka.core.Utils;
/*   29:     */ import weka.experiment.Stats;
/*   30:     */ import weka.filters.Filter;
/*   31:     */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*   32:     */ 
/*   33:     */ public class Canopy
/*   34:     */   extends RandomizableClusterer
/*   35:     */   implements UpdateableClusterer, NumberOfClustersRequestable, OptionHandler, TechnicalInformationHandler
/*   36:     */ {
/*   37:     */   private static final long serialVersionUID = 2067574593448223334L;
/*   38:     */   protected Instances m_canopies;
/*   39:     */   protected List<double[]> m_canopyT2Density;
/*   40:     */   protected List<double[][]> m_canopyCenters;
/*   41:     */   protected List<double[]> m_canopyNumMissingForNumerics;
/*   42:     */   protected List<long[]> m_clusterCanopies;
/*   43:     */   public static final double DEFAULT_T2 = -1.0D;
/*   44:     */   public static final double DEFAULT_T1 = -1.25D;
/*   45: 155 */   protected double m_userT2 = -1.0D;
/*   46: 161 */   protected double m_userT1 = -1.25D;
/*   47: 164 */   protected double m_t1 = this.m_userT1;
/*   48: 167 */   protected double m_t2 = this.m_userT2;
/*   49: 172 */   protected int m_periodicPruningRate = 10000;
/*   50: 178 */   protected double m_minClusterDensity = 2.0D;
/*   51: 181 */   protected int m_maxCanopyCandidates = 100;
/*   52: 187 */   protected boolean m_didPruneLastTime = true;
/*   53:     */   protected int m_instanceCount;
/*   54: 196 */   protected int m_numClustersRequested = -1;
/*   55:     */   protected Filter m_missingValuesReplacer;
/*   56: 207 */   protected boolean m_dontReplaceMissing = false;
/*   57: 210 */   protected NormalizableDistance m_distanceFunction = new EuclideanDistance();
/*   58:     */   protected Instances m_trainingData;
/*   59:     */   
/*   60:     */   public String globalInfo()
/*   61:     */   {
/*   62: 225 */     return "Cluster data using the capopy clustering algorithm, which requires just one pass over the data. Can run in eitherbatch or incremental mode. Results are generally not as good when running incrementally as the min/max for each numeric attribute is not known in advance. Has a heuristic (based on attribute std. deviations), that can be used in batch mode, for setting the T2 distance. The T2 distance determines how many canopies (clusters) are formed. When the user specifies a specific number (N) of clusters to generate, the algorithm will return the top N canopies (as determined by T2 density) when N < number of canopies (this applies to both batch and incremental learning); when N > number of canopies, the difference is made up by selecting training instances randomly (this can only be done when batch training). For more information see:\n\n" + getTechnicalInformation().toString();
/*   63:     */   }
/*   64:     */   
/*   65:     */   public TechnicalInformation getTechnicalInformation()
/*   66:     */   {
/*   67: 245 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*   68: 246 */     result.setValue(TechnicalInformation.Field.AUTHOR, "A. McCallum and K. Nigam and L.H. Ungar");
/*   69: 247 */     result.setValue(TechnicalInformation.Field.TITLE, "Efficient Clustering of High Dimensional Data Sets with Application to Reference Matching");
/*   70:     */     
/*   71:     */ 
/*   72:     */ 
/*   73: 251 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Proceedings of the sixth ACM SIGKDD internation conference on knowledge discovery and data mining ACM-SIAM symposium on Discrete algorithms");
/*   74:     */     
/*   75:     */ 
/*   76:     */ 
/*   77: 255 */     result.setValue(TechnicalInformation.Field.YEAR, "2000");
/*   78: 256 */     result.setValue(TechnicalInformation.Field.PAGES, "169-178");
/*   79:     */     
/*   80: 258 */     return result;
/*   81:     */   }
/*   82:     */   
/*   83:     */   public Capabilities getCapabilities()
/*   84:     */   {
/*   85: 268 */     Capabilities result = super.getCapabilities();
/*   86: 269 */     result.disableAll();
/*   87: 270 */     result.enable(Capabilities.Capability.NO_CLASS);
/*   88:     */     
/*   89:     */ 
/*   90: 273 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*   91: 274 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*   92: 275 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*   93:     */     
/*   94: 277 */     return result;
/*   95:     */   }
/*   96:     */   
/*   97:     */   public Enumeration<Option> listOptions()
/*   98:     */   {
/*   99: 287 */     Vector<Option> result = new Vector();
/*  100:     */     
/*  101: 289 */     result.addElement(new Option("\tNumber of clusters.\n\t(default 2).", "N", 1, "-N <num>"));
/*  102:     */     
/*  103:     */ 
/*  104: 292 */     result.addElement(new Option("\tMaximum number of candidate canopies to retain in memory\n\tat any one time. T2 distance plus, data characteristics,\n\twill determine how many candidate canopies are formed before\n\tperiodic and final pruning are performed, which might result\n\tin exceess memory consumption. This setting avoids large numbers\n\tof candidate canopies consuming memory. (default = 100)", "-max-candidates", 1, "-max-candidates <num>"));
/*  105:     */     
/*  106:     */ 
/*  107:     */ 
/*  108:     */ 
/*  109:     */ 
/*  110:     */ 
/*  111:     */ 
/*  112:     */ 
/*  113:     */ 
/*  114: 302 */     result.addElement(new Option("\tHow often to prune low density canopies. \n\t(default = every 10,000 training instances)", "periodic-pruning", 1, "-periodic-pruning <num>"));
/*  115:     */     
/*  116:     */ 
/*  117:     */ 
/*  118:     */ 
/*  119: 307 */     result.addElement(new Option("\tMinimum canopy density, below which a canopy will be pruned\n\tduring periodic pruning. (default = 2 instances)", "min-density", 1, "-min-density"));
/*  120:     */     
/*  121:     */ 
/*  122:     */ 
/*  123:     */ 
/*  124: 312 */     result.addElement(new Option("\tThe T2 distance to use. Values < 0 indicate that\n\ta heuristic based on attribute std. deviation should be used to set this.\n\tNote that this heuristic can only be used when batch training\n\t(default = -1.0)", "t2", 1, "-t2"));
/*  125:     */     
/*  126:     */ 
/*  127:     */ 
/*  128:     */ 
/*  129:     */ 
/*  130:     */ 
/*  131: 319 */     result.addElement(new Option("\tThe T1 distance to use. A value < 0 is taken as a\n\tpositive multiplier for T2. (default = -1.5)", "t1", 1, "-t1"));
/*  132:     */     
/*  133:     */ 
/*  134:     */ 
/*  135: 323 */     result.addElement(new Option("\tDon't replace missing values with mean/mode when running in batch mode.\n", "M", 0, "-M"));
/*  136:     */     
/*  137:     */ 
/*  138:     */ 
/*  139: 327 */     result.addAll(Collections.list(super.listOptions()));
/*  140:     */     
/*  141: 329 */     return result.elements();
/*  142:     */   }
/*  143:     */   
/*  144:     */   public void setOptions(String[] options)
/*  145:     */     throws Exception
/*  146:     */   {
/*  147: 393 */     String temp = Utils.getOption('N', options);
/*  148: 394 */     if (temp.length() > 0) {
/*  149: 395 */       setNumClusters(Integer.parseInt(temp));
/*  150:     */     }
/*  151: 398 */     temp = Utils.getOption("max-candidates", options);
/*  152: 399 */     if (temp.length() > 0) {
/*  153: 400 */       setMaxNumCandidateCanopiesToHoldInMemory(Integer.parseInt(temp));
/*  154:     */     }
/*  155: 403 */     temp = Utils.getOption("periodic-pruning", options);
/*  156: 404 */     if (temp.length() > 0) {
/*  157: 405 */       setPeriodicPruningRate(Integer.parseInt(temp));
/*  158:     */     }
/*  159: 408 */     temp = Utils.getOption("min-density", options);
/*  160: 409 */     if (temp.length() > 0) {
/*  161: 410 */       setMinimumCanopyDensity(Double.parseDouble(temp));
/*  162:     */     }
/*  163: 413 */     temp = Utils.getOption("t2", options);
/*  164: 414 */     if (temp.length() > 0) {
/*  165: 415 */       setT2(Double.parseDouble(temp));
/*  166:     */     }
/*  167: 418 */     temp = Utils.getOption("t1", options);
/*  168: 419 */     if (temp.length() > 0) {
/*  169: 420 */       setT1(Double.parseDouble(temp));
/*  170:     */     }
/*  171: 423 */     setDontReplaceMissingValues(Utils.getFlag('M', options));
/*  172:     */     
/*  173: 425 */     super.setOptions(options);
/*  174:     */   }
/*  175:     */   
/*  176:     */   public String[] getOptions()
/*  177:     */   {
/*  178: 436 */     Vector<String> result = new Vector();
/*  179:     */     
/*  180: 438 */     result.add("-N");
/*  181: 439 */     result.add("" + getNumClusters());
/*  182: 440 */     result.add("-max-candidates");
/*  183: 441 */     result.add("" + getMaxNumCandidateCanopiesToHoldInMemory());
/*  184: 442 */     result.add("-periodic-pruning");
/*  185: 443 */     result.add("" + getPeriodicPruningRate());
/*  186: 444 */     result.add("-min-density");
/*  187: 445 */     result.add("" + getMinimumCanopyDensity());
/*  188: 446 */     result.add("-t2");
/*  189: 447 */     result.add("" + getT2());
/*  190: 448 */     result.add("-t1");
/*  191: 449 */     result.add("" + getT1());
/*  192: 451 */     if (getDontReplaceMissingValues()) {
/*  193: 452 */       result.add("-M");
/*  194:     */     }
/*  195: 455 */     Collections.addAll(result, super.getOptions());
/*  196:     */     
/*  197: 457 */     return (String[])result.toArray(new String[result.size()]);
/*  198:     */   }
/*  199:     */   
/*  200:     */   public static boolean nonEmptyCanopySetIntersection(long[] first, long[] second)
/*  201:     */     throws Exception
/*  202:     */   {
/*  203: 470 */     if (first.length != second.length) {
/*  204: 471 */       throw new Exception("Canopy lists need to be the same length");
/*  205:     */     }
/*  206: 474 */     if ((first.length == 0) || (second.length == 0)) {
/*  207: 475 */       return false;
/*  208:     */     }
/*  209: 478 */     for (int i = 0; i < first.length; i++)
/*  210:     */     {
/*  211: 479 */       long firstBlock = first[i];
/*  212: 480 */       long secondBlock = second[i];
/*  213: 482 */       if ((firstBlock & secondBlock) != 0L) {
/*  214: 483 */         return true;
/*  215:     */       }
/*  216:     */     }
/*  217: 487 */     return false;
/*  218:     */   }
/*  219:     */   
/*  220:     */   private static void updateCanopyAssignment(long[] assigned, int toAssign)
/*  221:     */   {
/*  222: 491 */     int whichLong = toAssign / 64;
/*  223: 492 */     int whichBitPosition = toAssign % 64;
/*  224: 493 */     long mask = 1L << whichBitPosition;
/*  225:     */     
/*  226: 495 */     assigned[whichLong] |= mask;
/*  227:     */   }
/*  228:     */   
/*  229:     */   public long[] assignCanopies(Instance inst)
/*  230:     */     throws Exception
/*  231:     */   {
/*  232: 509 */     if (this.m_missingValuesReplacer != null)
/*  233:     */     {
/*  234: 510 */       this.m_missingValuesReplacer.input(inst);
/*  235: 511 */       inst = this.m_missingValuesReplacer.output();
/*  236:     */     }
/*  237: 514 */     int numLongs = this.m_canopies.size() / 64 + 1;
/*  238: 515 */     long[] assigned = new long[numLongs];
/*  239:     */     
/*  240: 517 */     double minDist = 1.7976931348623157E+308D;
/*  241: 518 */     double bitsSet = 0.0D;
/*  242: 519 */     int index = -1;
/*  243: 520 */     for (int i = 0; i < this.m_canopies.numInstances(); i++)
/*  244:     */     {
/*  245: 521 */       double dist = this.m_distanceFunction.distance(inst, this.m_canopies.instance(i));
/*  246: 522 */       if (dist < minDist)
/*  247:     */       {
/*  248: 523 */         minDist = dist;
/*  249: 524 */         index = i;
/*  250:     */       }
/*  251: 527 */       if (dist < this.m_t1)
/*  252:     */       {
/*  253: 528 */         updateCanopyAssignment(assigned, i);
/*  254: 529 */         bitsSet += 1.0D;
/*  255:     */       }
/*  256:     */     }
/*  257: 537 */     if (bitsSet == 0.0D) {
/*  258: 539 */       updateCanopyAssignment(assigned, index);
/*  259:     */     }
/*  260: 542 */     return assigned;
/*  261:     */   }
/*  262:     */   
/*  263:     */   protected void updateCanopyCenter(Instance newInstance, double[][] center, double[] numMissingNumerics)
/*  264:     */   {
/*  265: 547 */     for (int i = 0; i < newInstance.numAttributes(); i++) {
/*  266: 548 */       if (newInstance.attribute(i).isNumeric())
/*  267:     */       {
/*  268: 549 */         if (center[i].length == 0) {
/*  269: 550 */           center[i] = new double[1];
/*  270:     */         }
/*  271: 553 */         if (!newInstance.isMissing(i)) {
/*  272: 554 */           center[i][0] += newInstance.value(i);
/*  273:     */         } else {
/*  274: 556 */           numMissingNumerics[i] += 1.0D;
/*  275:     */         }
/*  276:     */       }
/*  277: 558 */       else if (newInstance.attribute(i).isNominal())
/*  278:     */       {
/*  279: 559 */         if (center[i].length == 0) {
/*  280: 561 */           center[i] = new double[newInstance.attribute(i).numValues() + 1];
/*  281:     */         }
/*  282: 563 */         if (newInstance.isMissing(i)) {
/*  283: 564 */           center[i][(center[i].length - 1)] += 1.0D;
/*  284:     */         } else {
/*  285: 566 */           center[i][((int)newInstance.value(i))] += 1.0D;
/*  286:     */         }
/*  287:     */       }
/*  288:     */     }
/*  289:     */   }
/*  290:     */   
/*  291:     */   public void updateClusterer(Instance newInstance)
/*  292:     */     throws Exception
/*  293:     */   {
/*  294: 575 */     if ((this.m_instanceCount > 0) && 
/*  295: 576 */       (this.m_instanceCount % this.m_periodicPruningRate == 0)) {
/*  296: 577 */       pruneCandidateCanopies();
/*  297:     */     }
/*  298: 581 */     this.m_instanceCount += 1;
/*  299: 582 */     if (this.m_missingValuesReplacer != null)
/*  300:     */     {
/*  301: 583 */       this.m_missingValuesReplacer.input(newInstance);
/*  302: 584 */       newInstance = this.m_missingValuesReplacer.output();
/*  303:     */     }
/*  304: 587 */     this.m_distanceFunction.update(newInstance);
/*  305: 588 */     boolean addPoint = true;
/*  306: 590 */     for (int i = 0; i < this.m_canopies.numInstances(); i++) {
/*  307: 591 */       if (this.m_distanceFunction.distance(newInstance, this.m_canopies.instance(i)) < this.m_t2)
/*  308:     */       {
/*  309: 592 */         double[] density = (double[])this.m_canopyT2Density.get(i);
/*  310: 593 */         density[0] += 1.0D;
/*  311: 594 */         addPoint = false;
/*  312:     */         
/*  313: 596 */         double[][] center = (double[][])this.m_canopyCenters.get(i);
/*  314: 597 */         double[] numMissingNumerics = (double[])this.m_canopyNumMissingForNumerics.get(i);
/*  315: 598 */         updateCanopyCenter(newInstance, center, numMissingNumerics);
/*  316:     */         
/*  317: 600 */         break;
/*  318:     */       }
/*  319:     */     }
/*  320: 604 */     if ((addPoint) && (this.m_canopies.numInstances() < this.m_maxCanopyCandidates))
/*  321:     */     {
/*  322: 605 */       this.m_canopies.add(newInstance);
/*  323: 606 */       double[] density = new double[1];
/*  324: 607 */       density[0] = 1.0D;
/*  325: 608 */       this.m_canopyT2Density.add(density);
/*  326:     */       
/*  327: 610 */       double[][] center = new double[newInstance.numAttributes()][0];
/*  328: 611 */       double[] numMissingNumerics = new double[newInstance.numAttributes()];
/*  329: 612 */       updateCanopyCenter(newInstance, center, numMissingNumerics);
/*  330: 613 */       this.m_canopyCenters.add(center);
/*  331: 614 */       this.m_canopyNumMissingForNumerics.add(numMissingNumerics);
/*  332:     */     }
/*  333:     */   }
/*  334:     */   
/*  335:     */   protected void pruneCandidateCanopies()
/*  336:     */   {
/*  337: 622 */     if ((!this.m_didPruneLastTime) && (this.m_canopies.size() == this.m_maxCanopyCandidates)) {
/*  338: 624 */       return;
/*  339:     */     }
/*  340: 627 */     this.m_didPruneLastTime = false;
/*  341: 628 */     for (int i = this.m_canopies.numInstances() - 1; i >= 0; i--)
/*  342:     */     {
/*  343: 629 */       double dens = ((double[])this.m_canopyT2Density.get(i))[0];
/*  344: 630 */       if (dens < this.m_minClusterDensity)
/*  345:     */       {
/*  346: 631 */         double[] tempDens = (double[])this.m_canopyT2Density.remove(this.m_canopyT2Density.size() - 1);
/*  347: 633 */         if (i < this.m_canopyT2Density.size()) {
/*  348: 634 */           this.m_canopyT2Density.set(i, tempDens);
/*  349:     */         }
/*  350: 636 */         if (getDebug()) {
/*  351: 637 */           System.err.println("Pruning a candidate canopy with density: " + dens);
/*  352:     */         }
/*  353: 640 */         this.m_didPruneLastTime = true;
/*  354:     */         
/*  355: 642 */         double[][] tempCenter = (double[][])this.m_canopyCenters.remove(this.m_canopyCenters.size() - 1);
/*  356: 644 */         if (i < this.m_canopyCenters.size()) {
/*  357: 645 */           this.m_canopyCenters.set(i, tempCenter);
/*  358:     */         }
/*  359: 648 */         double[] tempNumMissingNumerics = (double[])this.m_canopyNumMissingForNumerics.remove(this.m_canopyNumMissingForNumerics.size() - 1);
/*  360: 651 */         if (i < this.m_canopyNumMissingForNumerics.size()) {
/*  361: 652 */           this.m_canopyNumMissingForNumerics.set(i, tempNumMissingNumerics);
/*  362:     */         }
/*  363: 655 */         if (i != this.m_canopies.numInstances() - 1) {
/*  364: 656 */           this.m_canopies.swap(i, this.m_canopies.numInstances() - 1);
/*  365:     */         }
/*  366: 658 */         this.m_canopies.delete(this.m_canopies.numInstances() - 1);
/*  367:     */       }
/*  368:     */     }
/*  369:     */   }
/*  370:     */   
/*  371:     */   public double[] distributionForInstance(Instance instance)
/*  372:     */     throws Exception
/*  373:     */   {
/*  374: 665 */     if ((this.m_canopies == null) || (this.m_canopies.size() == 0)) {
/*  375: 666 */       throw new Exception("No canopies available to cluster with!");
/*  376:     */     }
/*  377: 669 */     double[] d = new double[numberOfClusters()];
/*  378: 671 */     if (this.m_missingValuesReplacer != null)
/*  379:     */     {
/*  380: 672 */       this.m_missingValuesReplacer.input(instance);
/*  381: 673 */       instance = this.m_missingValuesReplacer.output();
/*  382:     */     }
/*  383: 676 */     for (int i = 0; i < this.m_canopies.numInstances(); i++)
/*  384:     */     {
/*  385: 677 */       double distance = this.m_distanceFunction.distance(instance, this.m_canopies.instance(i));
/*  386:     */       
/*  387:     */ 
/*  388: 680 */       d[i] = (1.0D / (1.0D + distance));
/*  389:     */     }
/*  390: 683 */     Utils.normalize(d);
/*  391: 684 */     return d;
/*  392:     */   }
/*  393:     */   
/*  394:     */   private void assignCanopiesToCanopyCenters()
/*  395:     */   {
/*  396: 689 */     this.m_clusterCanopies = new ArrayList();
/*  397: 690 */     for (int i = 0; i < this.m_canopies.size(); i++)
/*  398:     */     {
/*  399: 691 */       Instance inst = this.m_canopies.instance(i);
/*  400:     */       try
/*  401:     */       {
/*  402: 693 */         long[] assignments = assignCanopies(inst);
/*  403: 694 */         this.m_clusterCanopies.add(assignments);
/*  404:     */       }
/*  405:     */       catch (Exception ex)
/*  406:     */       {
/*  407: 696 */         ex.printStackTrace();
/*  408:     */       }
/*  409:     */     }
/*  410:     */   }
/*  411:     */   
/*  412:     */   protected void adjustCanopies(double[] densities)
/*  413:     */   {
/*  414: 708 */     if (this.m_numClustersRequested < 0)
/*  415:     */     {
/*  416: 709 */       assignCanopiesToCanopyCenters();
/*  417:     */       
/*  418: 711 */       this.m_trainingData = new Instances(this.m_canopies, 0);
/*  419: 712 */       return;
/*  420:     */     }
/*  421: 716 */     if (this.m_canopies.numInstances() > this.m_numClustersRequested)
/*  422:     */     {
/*  423: 717 */       int[] sortedIndexes = Utils.stableSort(densities);
/*  424:     */       
/*  425: 719 */       Instances finalCanopies = new Instances(this.m_canopies, 0);
/*  426: 720 */       int count = 0;
/*  427: 721 */       for (int i = sortedIndexes.length - 1; count < this.m_numClustersRequested; i--)
/*  428:     */       {
/*  429: 722 */         finalCanopies.add(this.m_canopies.instance(sortedIndexes[i]));
/*  430: 723 */         count++;
/*  431:     */       }
/*  432: 726 */       this.m_canopies = finalCanopies;
/*  433: 727 */       List<double[][]> tempCanopyCenters = new ArrayList();
/*  434: 728 */       List<double[]> tempT2Dists = new ArrayList();
/*  435: 729 */       List<double[]> tempMissings = new ArrayList();
/*  436:     */       
/*  437:     */ 
/*  438:     */ 
/*  439: 733 */       count = 0;
/*  440: 734 */       for (int i = sortedIndexes.length - 1; count < finalCanopies.numInstances(); i--)
/*  441:     */       {
/*  442: 736 */         tempCanopyCenters.add(this.m_canopyCenters.get(sortedIndexes[i]));
/*  443: 737 */         tempT2Dists.add(this.m_canopyT2Density.get(sortedIndexes[i]));
/*  444: 738 */         tempMissings.add(this.m_canopyNumMissingForNumerics.get(sortedIndexes[i]));
/*  445: 739 */         count++;
/*  446:     */       }
/*  447: 741 */       this.m_canopyCenters = tempCanopyCenters;
/*  448: 742 */       this.m_canopyT2Density = tempT2Dists;
/*  449: 743 */       this.m_canopyNumMissingForNumerics = tempMissings;
/*  450:     */     }
/*  451: 745 */     else if ((this.m_canopies.numInstances() < this.m_numClustersRequested) && (this.m_trainingData != null) && (this.m_trainingData.numInstances() > 0))
/*  452:     */     {
/*  453: 749 */       Random r = new Random(getSeed());
/*  454: 750 */       for (int i = 0; i < 10; i++) {
/*  455: 751 */         r.nextInt();
/*  456:     */       }
/*  457: 753 */       HashMap<DecisionTableHashKey, Integer> initC = new HashMap();
/*  458: 754 */       DecisionTableHashKey hk = null;
/*  459: 757 */       for (int i = 0; i < this.m_canopies.numInstances(); i++) {
/*  460:     */         try
/*  461:     */         {
/*  462: 759 */           hk = new DecisionTableHashKey(this.m_canopies.instance(i), this.m_canopies.numAttributes(), true);
/*  463:     */           
/*  464:     */ 
/*  465: 762 */           initC.put(hk, null);
/*  466:     */         }
/*  467:     */         catch (Exception e)
/*  468:     */         {
/*  469: 764 */           e.printStackTrace();
/*  470:     */         }
/*  471:     */       }
/*  472: 768 */       for (int j = this.m_trainingData.numInstances() - 1; j >= 0; j--)
/*  473:     */       {
/*  474: 769 */         int instIndex = r.nextInt(j + 1);
/*  475:     */         try
/*  476:     */         {
/*  477: 771 */           hk = new DecisionTableHashKey(this.m_trainingData.instance(instIndex), this.m_trainingData.numAttributes(), true);
/*  478:     */         }
/*  479:     */         catch (Exception e)
/*  480:     */         {
/*  481: 774 */           e.printStackTrace();
/*  482:     */         }
/*  483: 776 */         if (!initC.containsKey(hk))
/*  484:     */         {
/*  485: 777 */           Instance newInstance = this.m_trainingData.instance(instIndex);
/*  486: 778 */           this.m_canopies.add(newInstance);
/*  487:     */           
/*  488: 780 */           double[] density = new double[1];
/*  489: 781 */           density[0] = 1.0D;
/*  490: 782 */           this.m_canopyT2Density.add(density);
/*  491:     */           
/*  492: 784 */           double[][] center = new double[newInstance.numAttributes()][0];
/*  493: 785 */           double[] numMissingNumerics = new double[newInstance.numAttributes()];
/*  494: 786 */           updateCanopyCenter(newInstance, center, numMissingNumerics);
/*  495: 787 */           this.m_canopyCenters.add(center);
/*  496: 788 */           this.m_canopyNumMissingForNumerics.add(numMissingNumerics);
/*  497:     */           
/*  498: 790 */           initC.put(hk, null);
/*  499:     */         }
/*  500: 792 */         this.m_trainingData.swap(j, instIndex);
/*  501: 794 */         if (this.m_canopies.numInstances() == this.m_numClustersRequested) {
/*  502:     */           break;
/*  503:     */         }
/*  504:     */       }
/*  505:     */     }
/*  506: 800 */     assignCanopiesToCanopyCenters();
/*  507:     */     
/*  508:     */ 
/*  509: 803 */     this.m_trainingData = new Instances(this.m_canopies, 0);
/*  510:     */   }
/*  511:     */   
/*  512:     */   public void updateFinished()
/*  513:     */   {
/*  514: 808 */     if ((this.m_canopies == null) || (this.m_canopies.numInstances() == 0)) {
/*  515: 809 */       return;
/*  516:     */     }
/*  517: 812 */     pruneCandidateCanopies();
/*  518:     */     
/*  519:     */ 
/*  520: 815 */     double[] densities = new double[this.m_canopies.size()];
/*  521: 816 */     for (int i = 0; i < this.m_canopies.numInstances(); i++)
/*  522:     */     {
/*  523: 817 */       double[] density = (double[])this.m_canopyT2Density.get(i);
/*  524: 818 */       double[][] centerSums = (double[][])this.m_canopyCenters.get(i);
/*  525: 819 */       double[] numMissingForNumerics = (double[])this.m_canopyNumMissingForNumerics.get(i);
/*  526: 820 */       double[] finalCenter = new double[this.m_canopies.numAttributes()];
/*  527: 821 */       for (int j = 0; j < this.m_canopies.numAttributes(); j++) {
/*  528: 822 */         if (this.m_canopies.attribute(j).isNumeric())
/*  529:     */         {
/*  530: 823 */           if (numMissingForNumerics[j] == density[0]) {
/*  531: 824 */             finalCenter[j] = Utils.missingValue();
/*  532:     */           } else {
/*  533: 826 */             finalCenter[j] = (centerSums[j][0] / (density[0] - numMissingForNumerics[j]));
/*  534:     */           }
/*  535:     */         }
/*  536: 829 */         else if (this.m_canopies.attribute(j).isNominal())
/*  537:     */         {
/*  538: 830 */           int mode = Utils.maxIndex(centerSums[j]);
/*  539: 831 */           if (mode == centerSums[j].length - 1) {
/*  540: 832 */             finalCenter[j] = Utils.missingValue();
/*  541:     */           } else {
/*  542: 834 */             finalCenter[j] = mode;
/*  543:     */           }
/*  544:     */         }
/*  545:     */       }
/*  546: 839 */       Instance finalCenterInst = (this.m_canopies.instance(i) instanceof SparseInstance) ? new SparseInstance(1.0D, finalCenter) : new DenseInstance(1.0D, finalCenter);
/*  547:     */       
/*  548: 841 */       this.m_canopies.set(i, finalCenterInst);
/*  549:     */       
/*  550: 843 */       this.m_canopies.instance(i).setWeight(density[0]);
/*  551: 844 */       densities[i] = density[0];
/*  552:     */     }
/*  553: 847 */     adjustCanopies(densities);
/*  554:     */   }
/*  555:     */   
/*  556:     */   public void initializeDistanceFunction(Instances init)
/*  557:     */     throws Exception
/*  558:     */   {
/*  559: 858 */     if (this.m_missingValuesReplacer != null) {
/*  560: 859 */       init = Filter.useFilter(init, this.m_missingValuesReplacer);
/*  561:     */     }
/*  562: 862 */     this.m_distanceFunction.setInstances(init);
/*  563:     */   }
/*  564:     */   
/*  565:     */   protected void setT2T1BasedOnStdDev(Instances trainingBatch)
/*  566:     */     throws Exception
/*  567:     */   {
/*  568: 873 */     double normalizedStdDevSum = 0.0D;
/*  569: 875 */     for (int i = 0; i < trainingBatch.numAttributes(); i++) {
/*  570: 876 */       if (trainingBatch.attribute(i).isNominal())
/*  571:     */       {
/*  572: 877 */         normalizedStdDevSum += 0.25D;
/*  573:     */       }
/*  574: 878 */       else if (trainingBatch.attribute(i).isNumeric())
/*  575:     */       {
/*  576: 879 */         AttributeStats stats = trainingBatch.attributeStats(i);
/*  577: 880 */         if (trainingBatch.numInstances() - stats.missingCount > 2)
/*  578:     */         {
/*  579: 881 */           double stdDev = stats.numericStats.stdDev;
/*  580: 882 */           double min = stats.numericStats.min;
/*  581: 883 */           double max = stats.numericStats.max;
/*  582: 884 */           if ((!Utils.isMissingValue(stdDev)) && (max - min > 0.0D))
/*  583:     */           {
/*  584: 885 */             stdDev = 0.5D * stdDev / (max - min);
/*  585: 886 */             normalizedStdDevSum += stdDev;
/*  586:     */           }
/*  587:     */         }
/*  588:     */       }
/*  589:     */     }
/*  590: 892 */     normalizedStdDevSum = Math.sqrt(normalizedStdDevSum);
/*  591: 893 */     if (normalizedStdDevSum > 0.0D) {
/*  592: 894 */       this.m_t2 = normalizedStdDevSum;
/*  593:     */     }
/*  594:     */   }
/*  595:     */   
/*  596:     */   public void buildClusterer(Instances data)
/*  597:     */     throws Exception
/*  598:     */   {
/*  599: 900 */     this.m_t1 = this.m_userT1;
/*  600: 901 */     this.m_t2 = this.m_userT2;
/*  601: 903 */     if ((data.numInstances() == 0) && (this.m_userT2 < 0.0D))
/*  602:     */     {
/*  603: 904 */       System.err.println("The heuristic for setting T2 based on std. dev. can't be used when running in incremental mode. Using default of 1.0.");
/*  604:     */       
/*  605:     */ 
/*  606: 907 */       this.m_t2 = 1.0D;
/*  607:     */     }
/*  608: 910 */     this.m_canopyT2Density = new ArrayList();
/*  609: 911 */     this.m_canopyCenters = new ArrayList();
/*  610: 912 */     this.m_canopyNumMissingForNumerics = new ArrayList();
/*  611: 914 */     if (data.numInstances() > 0)
/*  612:     */     {
/*  613: 915 */       if (!this.m_dontReplaceMissing)
/*  614:     */       {
/*  615: 916 */         this.m_missingValuesReplacer = new ReplaceMissingValues();
/*  616: 917 */         this.m_missingValuesReplacer.setInputFormat(data);
/*  617: 918 */         data = Filter.useFilter(data, this.m_missingValuesReplacer);
/*  618:     */       }
/*  619: 920 */       Random r = new Random(getSeed());
/*  620: 921 */       for (int i = 0; i < 10; i++) {
/*  621: 922 */         r.nextInt();
/*  622:     */       }
/*  623: 924 */       data.randomize(r);
/*  624: 926 */       if (this.m_userT2 < 0.0D) {
/*  625: 927 */         setT2T1BasedOnStdDev(data);
/*  626:     */       }
/*  627:     */     }
/*  628: 930 */     this.m_t1 = (this.m_userT1 > 0.0D ? this.m_userT1 : -this.m_userT1 * this.m_t2);
/*  629:     */     
/*  630:     */ 
/*  631:     */ 
/*  632:     */ 
/*  633:     */ 
/*  634: 936 */     this.m_distanceFunction.setInstances(data);
/*  635:     */     
/*  636: 938 */     this.m_canopies = new Instances(data, 0);
/*  637: 939 */     if (data.numInstances() > 0) {
/*  638: 940 */       this.m_trainingData = new Instances(data);
/*  639:     */     }
/*  640: 943 */     for (int i = 0; i < data.numInstances(); i++)
/*  641:     */     {
/*  642: 944 */       if ((getDebug()) && (i % this.m_periodicPruningRate == 0)) {
/*  643: 945 */         System.err.println("Processed: " + i);
/*  644:     */       }
/*  645: 947 */       updateClusterer(data.instance(i));
/*  646:     */     }
/*  647: 950 */     updateFinished();
/*  648:     */   }
/*  649:     */   
/*  650:     */   public int numberOfClusters()
/*  651:     */     throws Exception
/*  652:     */   {
/*  653: 955 */     return this.m_canopies.numInstances();
/*  654:     */   }
/*  655:     */   
/*  656:     */   public void setMissingValuesReplacer(Filter missingReplacer)
/*  657:     */   {
/*  658: 964 */     this.m_missingValuesReplacer = missingReplacer;
/*  659:     */   }
/*  660:     */   
/*  661:     */   public Instances getCanopies()
/*  662:     */   {
/*  663: 973 */     return this.m_canopies;
/*  664:     */   }
/*  665:     */   
/*  666:     */   public void setCanopies(Instances canopies)
/*  667:     */   {
/*  668: 982 */     this.m_canopies = canopies;
/*  669:     */   }
/*  670:     */   
/*  671:     */   public List<long[]> getClusterCanopyAssignments()
/*  672:     */   {
/*  673: 991 */     return this.m_clusterCanopies;
/*  674:     */   }
/*  675:     */   
/*  676:     */   public void setClusterCanopyAssignments(List<long[]> clusterCanopies)
/*  677:     */   {
/*  678:1000 */     this.m_clusterCanopies = clusterCanopies;
/*  679:     */   }
/*  680:     */   
/*  681:     */   public double getActualT2()
/*  682:     */   {
/*  683:1010 */     return this.m_t2;
/*  684:     */   }
/*  685:     */   
/*  686:     */   public double getActualT1()
/*  687:     */   {
/*  688:1020 */     return this.m_t1;
/*  689:     */   }
/*  690:     */   
/*  691:     */   public String t1TipText()
/*  692:     */   {
/*  693:1029 */     return "The T1 distance to use. Values < 0 are taken as a positive multiplier for the T2 distance";
/*  694:     */   }
/*  695:     */   
/*  696:     */   public void setT1(double t1)
/*  697:     */   {
/*  698:1040 */     this.m_userT1 = t1;
/*  699:     */   }
/*  700:     */   
/*  701:     */   public double getT1()
/*  702:     */   {
/*  703:1050 */     return this.m_userT1;
/*  704:     */   }
/*  705:     */   
/*  706:     */   public String t2TipText()
/*  707:     */   {
/*  708:1059 */     return "The T2 distance to use. Values < 0 indicate that this should be set using a heuristic based on attribute standard deviation (note that this onlyworks when batch training)";
/*  709:     */   }
/*  710:     */   
/*  711:     */   public void setT2(double t2)
/*  712:     */   {
/*  713:1072 */     this.m_userT2 = t2;
/*  714:     */   }
/*  715:     */   
/*  716:     */   public double getT2()
/*  717:     */   {
/*  718:1083 */     return this.m_userT2;
/*  719:     */   }
/*  720:     */   
/*  721:     */   public String numClustersTipText()
/*  722:     */   {
/*  723:1093 */     return "Set number of clusters. -1 means number of clusters is determined by T2 distance";
/*  724:     */   }
/*  725:     */   
/*  726:     */   public void setNumClusters(int numClusters)
/*  727:     */     throws Exception
/*  728:     */   {
/*  729:1099 */     this.m_numClustersRequested = numClusters;
/*  730:     */   }
/*  731:     */   
/*  732:     */   public int getNumClusters()
/*  733:     */   {
/*  734:1108 */     return this.m_numClustersRequested;
/*  735:     */   }
/*  736:     */   
/*  737:     */   public String periodicPruningRateTipText()
/*  738:     */   {
/*  739:1118 */     return "How often to prune low density canopies during training";
/*  740:     */   }
/*  741:     */   
/*  742:     */   public void setPeriodicPruningRate(int p)
/*  743:     */   {
/*  744:1127 */     this.m_periodicPruningRate = p;
/*  745:     */   }
/*  746:     */   
/*  747:     */   public int getPeriodicPruningRate()
/*  748:     */   {
/*  749:1136 */     return this.m_periodicPruningRate;
/*  750:     */   }
/*  751:     */   
/*  752:     */   public String minimumCanopyDensityTipText()
/*  753:     */   {
/*  754:1146 */     return "The minimum T2-based density below which a canopy will be pruned during periodic pruning";
/*  755:     */   }
/*  756:     */   
/*  757:     */   public void setMinimumCanopyDensity(double dens)
/*  758:     */   {
/*  759:1156 */     this.m_minClusterDensity = dens;
/*  760:     */   }
/*  761:     */   
/*  762:     */   public double getMinimumCanopyDensity()
/*  763:     */   {
/*  764:1166 */     return this.m_minClusterDensity;
/*  765:     */   }
/*  766:     */   
/*  767:     */   public String maxNumCandidateCanopiesToHoldInMemory()
/*  768:     */   {
/*  769:1176 */     return "The maximum number of candidate canopies to retain in main memory during training. T2 distance and data characteristics determine how many candidate canopies are formed before periodic and final pruning are performed. There may not be enough memory available if T2 is set too low.";
/*  770:     */   }
/*  771:     */   
/*  772:     */   public void setMaxNumCandidateCanopiesToHoldInMemory(int max)
/*  773:     */   {
/*  774:1192 */     this.m_maxCanopyCandidates = max;
/*  775:     */   }
/*  776:     */   
/*  777:     */   public int getMaxNumCandidateCanopiesToHoldInMemory()
/*  778:     */   {
/*  779:1205 */     return this.m_maxCanopyCandidates;
/*  780:     */   }
/*  781:     */   
/*  782:     */   public String dontReplaceMissingValuesTipText()
/*  783:     */   {
/*  784:1215 */     return "Replace missing values globally with mean/mode.";
/*  785:     */   }
/*  786:     */   
/*  787:     */   public void setDontReplaceMissingValues(boolean r)
/*  788:     */   {
/*  789:1224 */     this.m_dontReplaceMissing = r;
/*  790:     */   }
/*  791:     */   
/*  792:     */   public boolean getDontReplaceMissingValues()
/*  793:     */   {
/*  794:1233 */     return this.m_dontReplaceMissing;
/*  795:     */   }
/*  796:     */   
/*  797:     */   public static String printSingleAssignment(long[] assignments)
/*  798:     */   {
/*  799:1237 */     StringBuilder temp = new StringBuilder();
/*  800:     */     
/*  801:1239 */     boolean first = true;
/*  802:1240 */     temp.append(" <");
/*  803:1241 */     for (int j = 0; j < assignments.length; j++)
/*  804:     */     {
/*  805:1242 */       long block = assignments[j];
/*  806:1243 */       int offset = j * 64;
/*  807:1245 */       for (int k = 0; k < 64; k++)
/*  808:     */       {
/*  809:1246 */         long mask = 1L << k;
/*  810:1248 */         if ((mask & block) != 0L)
/*  811:     */         {
/*  812:1249 */           temp.append("" + (!first ? "," : "") + (offset + k));
/*  813:1250 */           if (first) {
/*  814:1251 */             first = false;
/*  815:     */           }
/*  816:     */         }
/*  817:     */       }
/*  818:     */     }
/*  819:1257 */     temp.append(">");
/*  820:1258 */     return temp.toString();
/*  821:     */   }
/*  822:     */   
/*  823:     */   public static String printCanopyAssignments(Instances dataPoints, List<long[]> canopyAssignments)
/*  824:     */   {
/*  825:1271 */     StringBuilder temp = new StringBuilder();
/*  826:1273 */     for (int i = 0; i < dataPoints.size(); i++)
/*  827:     */     {
/*  828:1274 */       temp.append("Cluster " + i + ": ");
/*  829:1275 */       temp.append(dataPoints.instance(i));
/*  830:1276 */       if ((canopyAssignments != null) && (canopyAssignments.size() == dataPoints.size()))
/*  831:     */       {
/*  832:1278 */         long[] assignments = (long[])canopyAssignments.get(i);
/*  833:1279 */         temp.append(printSingleAssignment(assignments));
/*  834:     */       }
/*  835:1281 */       temp.append("\n");
/*  836:     */     }
/*  837:1284 */     return temp.toString();
/*  838:     */   }
/*  839:     */   
/*  840:     */   public String toString(boolean header)
/*  841:     */   {
/*  842:1294 */     StringBuffer temp = new StringBuffer();
/*  843:1296 */     if (this.m_canopies == null) {
/*  844:1297 */       return "No clusterer built yet";
/*  845:     */     }
/*  846:1300 */     if (header)
/*  847:     */     {
/*  848:1301 */       temp.append("\nCanopy clustering\n=================\n");
/*  849:1302 */       temp.append("\nNumber of canopies (cluster centers) found: " + this.m_canopies.numInstances());
/*  850:     */     }
/*  851:1306 */     temp.append("\nT2 radius: " + String.format("%-10.3f", new Object[] { Double.valueOf(this.m_t2) }));
/*  852:1307 */     temp.append("\nT1 radius: " + String.format("%-10.3f", new Object[] { Double.valueOf(this.m_t1) }));
/*  853:1308 */     temp.append("\n\n");
/*  854:     */     
/*  855:1310 */     temp.append(printCanopyAssignments(this.m_canopies, this.m_clusterCanopies));
/*  856:     */     
/*  857:1312 */     temp.append("\n");
/*  858:     */     
/*  859:1314 */     return temp.toString();
/*  860:     */   }
/*  861:     */   
/*  862:     */   public String toString()
/*  863:     */   {
/*  864:1319 */     return toString(true);
/*  865:     */   }
/*  866:     */   
/*  867:     */   public void cleanUp()
/*  868:     */   {
/*  869:1326 */     this.m_canopyNumMissingForNumerics = null;
/*  870:1327 */     this.m_canopyT2Density = null;
/*  871:1328 */     this.m_canopyCenters = null;
/*  872:     */   }
/*  873:     */   
/*  874:     */   public static Canopy aggregateCanopies(List<Canopy> canopies, double aggregationT1, double aggregationT2, NormalizableDistance finalDistanceFunction, Filter missingValuesReplacer, int finalNumCanopies)
/*  875:     */   {
/*  876:1351 */     Instances collectedCanopies = new Instances(((Canopy)canopies.get(0)).getCanopies(), 0);
/*  877:     */     
/*  878:1353 */     Instances finalCanopies = new Instances(collectedCanopies, 0);
/*  879:     */     
/*  880:1355 */     List<double[][]> finalCenters = new ArrayList();
/*  881:1356 */     List<double[]> finalMissingNumerics = new ArrayList();
/*  882:1357 */     List<double[]> finalT2Densities = new ArrayList();
/*  883:1358 */     List<Instance> finalCanopiesList = new ArrayList();
/*  884:1359 */     List<double[][]> centersForEachCanopy = new ArrayList();
/*  885:1360 */     List<double[]> numMissingNumericsForEachCanopy = new ArrayList();
/*  886:1362 */     for (Canopy c : canopies)
/*  887:     */     {
/*  888:1363 */       Instances tempC = c.getCanopies();
/*  889:1365 */       for (int i = 0; i < tempC.numInstances(); i++)
/*  890:     */       {
/*  891:1366 */         collectedCanopies.add(tempC.instance(i));
/*  892:1367 */         centersForEachCanopy.add(c.m_canopyCenters.get(i));
/*  893:1368 */         numMissingNumericsForEachCanopy.add(c.m_canopyNumMissingForNumerics.get(i));
/*  894:     */       }
/*  895:     */     }
/*  896:1373 */     for (int i = 0; i < collectedCanopies.numInstances(); i++)
/*  897:     */     {
/*  898:1374 */       boolean addPoint = true;
/*  899:1375 */       Instance candidate = collectedCanopies.instance(i);
/*  900:1376 */       double[][] candidateCenter = (double[][])centersForEachCanopy.get(i);
/*  901:1377 */       double[] candidateMissingNumerics = (double[])numMissingNumericsForEachCanopy.get(i);
/*  902:1380 */       for (int j = 0; j < finalCanopiesList.size(); j++)
/*  903:     */       {
/*  904:1381 */         Instance fc = (Instance)finalCanopiesList.get(j);
/*  905:1383 */         if (finalDistanceFunction.distance(candidate, fc) < aggregationT2)
/*  906:     */         {
/*  907:1384 */           addPoint = false;
/*  908:     */           
/*  909:     */ 
/*  910:1387 */           double[][] center = (double[][])finalCenters.get(j);
/*  911:1388 */           double[] missingNumerics = (double[])finalMissingNumerics.get(j);
/*  912:     */           
/*  913:1390 */           ((double[])finalT2Densities.get(j))[0] += candidate.weight();
/*  914:1392 */           for (int k = 0; k < candidate.numAttributes(); k++)
/*  915:     */           {
/*  916:1393 */             missingNumerics[k] += candidateMissingNumerics[k];
/*  917:1394 */             for (int l = 0; l < center[k].length; l++) {
/*  918:1395 */               center[k][l] += candidateCenter[k][l];
/*  919:     */             }
/*  920:     */           }
/*  921:1399 */           break;
/*  922:     */         }
/*  923:     */       }
/*  924:1403 */       if (addPoint)
/*  925:     */       {
/*  926:1404 */         finalCanopiesList.add(candidate);
/*  927:1405 */         finalCanopies.add(candidate);
/*  928:1406 */         finalCenters.add(candidateCenter);
/*  929:1407 */         finalMissingNumerics.add(candidateMissingNumerics);
/*  930:1408 */         double[] dens = new double[1];
/*  931:1409 */         dens[0] = candidate.weight();
/*  932:1410 */         finalT2Densities.add(dens);
/*  933:     */       }
/*  934:     */     }
/*  935:1416 */     Canopy finalC = new Canopy();
/*  936:1417 */     finalC.setCanopies(finalCanopies);
/*  937:1418 */     finalC.setMissingValuesReplacer(missingValuesReplacer);
/*  938:1419 */     finalC.m_distanceFunction = finalDistanceFunction;
/*  939:1420 */     finalC.m_canopyCenters = finalCenters;
/*  940:1421 */     finalC.m_canopyNumMissingForNumerics = finalMissingNumerics;
/*  941:1422 */     finalC.m_canopyT2Density = finalT2Densities;
/*  942:1423 */     finalC.m_t2 = aggregationT2;
/*  943:1424 */     finalC.m_t1 = aggregationT1;
/*  944:     */     try
/*  945:     */     {
/*  946:1426 */       finalC.setNumClusters(finalNumCanopies);
/*  947:     */     }
/*  948:     */     catch (Exception e) {}
/*  949:1431 */     finalC.updateFinished();
/*  950:     */     
/*  951:1433 */     return finalC;
/*  952:     */   }
/*  953:     */   
/*  954:     */   public static void main(String[] args)
/*  955:     */   {
/*  956:1437 */     runClusterer(new Canopy(), args);
/*  957:     */   }
/*  958:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.clusterers.Canopy
 * JD-Core Version:    0.7.0.1
 */