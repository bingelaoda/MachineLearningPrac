/*    1:     */ package weka.filters.unsupervised.attribute;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.io.Serializable;
/*    5:     */ import java.util.ArrayList;
/*    6:     */ import java.util.Enumeration;
/*    7:     */ import java.util.HashMap;
/*    8:     */ import java.util.Map;
/*    9:     */ import java.util.Vector;
/*   10:     */ import java.util.concurrent.ConcurrentHashMap;
/*   11:     */ import java.util.concurrent.LinkedBlockingQueue;
/*   12:     */ import java.util.concurrent.ThreadPoolExecutor;
/*   13:     */ import java.util.concurrent.TimeUnit;
/*   14:     */ import weka.classifiers.rules.DecisionTableHashKey;
/*   15:     */ import weka.core.Attribute;
/*   16:     */ import weka.core.Capabilities;
/*   17:     */ import weka.core.Capabilities.Capability;
/*   18:     */ import weka.core.DenseInstance;
/*   19:     */ import weka.core.Environment;
/*   20:     */ import weka.core.EnvironmentHandler;
/*   21:     */ import weka.core.Instance;
/*   22:     */ import weka.core.Instances;
/*   23:     */ import weka.core.Option;
/*   24:     */ import weka.core.OptionHandler;
/*   25:     */ import weka.core.RevisionUtils;
/*   26:     */ import weka.core.SerializedObject;
/*   27:     */ import weka.core.SparseInstance;
/*   28:     */ import weka.core.TechnicalInformation;
/*   29:     */ import weka.core.TechnicalInformation.Field;
/*   30:     */ import weka.core.TechnicalInformation.Type;
/*   31:     */ import weka.core.TechnicalInformationHandler;
/*   32:     */ import weka.core.Utils;
/*   33:     */ import weka.core.WeightedInstancesHandler;
/*   34:     */ import weka.core.neighboursearch.LinearNNSearch;
/*   35:     */ import weka.core.neighboursearch.NearestNeighbourSearch;
/*   36:     */ import weka.filters.Filter;
/*   37:     */ 
/*   38:     */ public class LOF
/*   39:     */   extends Filter
/*   40:     */   implements OptionHandler, WeightedInstancesHandler, TechnicalInformationHandler, EnvironmentHandler
/*   41:     */ {
/*   42:     */   private static final long serialVersionUID = 3843951651734143371L;
/*   43:     */   protected String m_minPtsLB;
/*   44:     */   protected String m_minPtsUB;
/*   45:     */   protected NearestNeighbourSearch m_nnSearch;
/*   46:     */   protected NearestNeighbourSearch m_nnTemplate;
/*   47:     */   protected transient Environment m_env;
/*   48:     */   protected int m_lbK;
/*   49:     */   protected int m_ubK;
/*   50:     */   protected boolean m_classSet;
/*   51:     */   protected transient DecisionTableHashKey[] m_instKeys;
/*   52:     */   protected Map<DecisionTableHashKey, Neighborhood> m_kDistanceContainer;
/*   53:     */   protected transient ThreadPoolExecutor m_executorPool;
/*   54:     */   protected String m_numSlots;
/*   55:     */   protected int m_numExecutionSlots;
/*   56:     */   protected int m_completed;
/*   57:     */   protected int m_failed;
/*   58:     */   protected boolean m_classifierMode;
/*   59:     */   
/*   60:     */   public LOF()
/*   61:     */   {
/*   62: 130 */     this.m_minPtsLB = "10";
/*   63:     */     
/*   64:     */ 
/*   65: 133 */     this.m_minPtsUB = "40";
/*   66:     */     
/*   67:     */ 
/*   68:     */ 
/*   69:     */ 
/*   70:     */ 
/*   71: 139 */     this.m_nnTemplate = new LinearNNSearch();
/*   72:     */     
/*   73:     */ 
/*   74:     */ 
/*   75:     */ 
/*   76:     */ 
/*   77: 145 */     this.m_lbK = 10;
/*   78:     */     
/*   79:     */ 
/*   80: 148 */     this.m_ubK = 40;
/*   81:     */     
/*   82:     */ 
/*   83: 151 */     this.m_classSet = false;
/*   84:     */     
/*   85:     */ 
/*   86:     */ 
/*   87:     */ 
/*   88:     */ 
/*   89:     */ 
/*   90:     */ 
/*   91:     */ 
/*   92:     */ 
/*   93: 161 */     this.m_numSlots = "1";
/*   94: 162 */     this.m_numExecutionSlots = 1;
/*   95:     */     
/*   96:     */ 
/*   97:     */ 
/*   98:     */ 
/*   99:     */ 
/*  100:     */ 
/*  101:     */ 
/*  102: 170 */     this.m_classifierMode = false;
/*  103:     */   }
/*  104:     */   
/*  105:     */   public String globalInfo()
/*  106:     */   {
/*  107: 179 */     return "A filter that applies the LOF (Local Outlier Factor) algorithm to compute an \"outlier\" score for each instance in the data. Can use multiple cores/cpus to speed up the LOF computation for large datasets. Nearest neighbor search methods and distance functions are pluggable.\n\nFor more information, see:\n\n" + getTechnicalInformation().toString();
/*  108:     */   }
/*  109:     */   
/*  110:     */   public Capabilities getCapabilities()
/*  111:     */   {
/*  112: 195 */     Capabilities result = super.getCapabilities();
/*  113: 196 */     result.disableAll();
/*  114:     */     
/*  115:     */ 
/*  116: 199 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  117: 200 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  118: 201 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  119: 202 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  120:     */     
/*  121:     */ 
/*  122: 205 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  123: 206 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*  124: 207 */     result.enable(Capabilities.Capability.UNARY_CLASS);
/*  125: 208 */     result.enable(Capabilities.Capability.DATE_CLASS);
/*  126: 209 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  127: 210 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  128:     */     
/*  129:     */ 
/*  130: 213 */     result.setMinimumNumberInstances(0);
/*  131:     */     
/*  132: 215 */     return result;
/*  133:     */   }
/*  134:     */   
/*  135:     */   public TechnicalInformation getTechnicalInformation()
/*  136:     */   {
/*  137: 229 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  138: 230 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Markus M. Breunig and Hans-Peter Kriegel and Raymond T. Ng and Jorg Sander");
/*  139:     */     
/*  140: 232 */     result.setValue(TechnicalInformation.Field.TITLE, "LOF: Identifying Density-Based Local Outliers");
/*  141:     */     
/*  142: 234 */     result.setValue(TechnicalInformation.Field.JOURNAL, "ACM SIGMOD Record");
/*  143: 235 */     result.setValue(TechnicalInformation.Field.YEAR, "2000");
/*  144: 236 */     result.setValue(TechnicalInformation.Field.VOLUME, "29");
/*  145: 237 */     result.setValue(TechnicalInformation.Field.NUMBER, "2");
/*  146: 238 */     result.setValue(TechnicalInformation.Field.PAGES, "93-104");
/*  147: 239 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "ACM New York");
/*  148: 240 */     return result;
/*  149:     */   }
/*  150:     */   
/*  151:     */   public Enumeration<Option> listOptions()
/*  152:     */   {
/*  153: 251 */     Vector<Option> newVector = new Vector();
/*  154: 252 */     newVector.add(new Option("\tLower bound on the k nearest neighbors for finding max LOF (minPtsLB)\n\t(default = 10)", "min", 1, "-min <num>"));
/*  155:     */     
/*  156:     */ 
/*  157: 255 */     newVector.add(new Option("\tUpper bound on the k nearest neighbors for finding max LOF (minPtsUB)\n\t(default = 40)", "max", 1, "-max <num>"));
/*  158:     */     
/*  159:     */ 
/*  160: 258 */     newVector.addElement(new Option("\tThe nearest neighbour search algorithm to use (default: weka.core.neighboursearch.LinearNNSearch).\n", "A", 0, "-A"));
/*  161:     */     
/*  162:     */ 
/*  163:     */ 
/*  164: 262 */     newVector.addElement(new Option("\tNumber of execution slots.\n\t(default 1 - i.e. no parallelism)", "num-slots", 1, "-num-slots <num>"));
/*  165:     */     
/*  166:     */ 
/*  167:     */ 
/*  168: 266 */     return newVector.elements();
/*  169:     */   }
/*  170:     */   
/*  171:     */   public void setOptions(String[] options)
/*  172:     */     throws Exception
/*  173:     */   {
/*  174: 306 */     String minP = Utils.getOption("min", options);
/*  175: 307 */     if (minP.length() > 0) {
/*  176: 308 */       setMinPointsLowerBound(minP);
/*  177:     */     }
/*  178: 311 */     String maxP = Utils.getOption("max", options);
/*  179: 312 */     if (maxP.length() > 0) {
/*  180: 313 */       setMinPointsUpperBound(maxP);
/*  181:     */     }
/*  182: 316 */     String nnSearchClass = Utils.getOption('A', options);
/*  183: 317 */     if (nnSearchClass.length() != 0)
/*  184:     */     {
/*  185: 318 */       String[] nnSearchClassSpec = Utils.splitOptions(nnSearchClass);
/*  186: 319 */       if (nnSearchClassSpec.length == 0) {
/*  187: 320 */         throw new Exception("Invalid NearestNeighbourSearch algorithm specification string.");
/*  188:     */       }
/*  189: 323 */       String className = nnSearchClassSpec[0];
/*  190: 324 */       nnSearchClassSpec[0] = "";
/*  191:     */       
/*  192: 326 */       setNNSearch((NearestNeighbourSearch)Utils.forName(NearestNeighbourSearch.class, className, nnSearchClassSpec));
/*  193:     */     }
/*  194:     */     else
/*  195:     */     {
/*  196: 329 */       setNNSearch(new LinearNNSearch());
/*  197:     */     }
/*  198: 332 */     String slotsS = Utils.getOption("num-slots", options);
/*  199: 333 */     if (slotsS.length() > 0) {
/*  200: 334 */       setNumExecutionSlots(slotsS);
/*  201:     */     }
/*  202: 337 */     Utils.checkForRemainingOptions(options);
/*  203:     */   }
/*  204:     */   
/*  205:     */   public String[] getOptions()
/*  206:     */   {
/*  207: 348 */     Vector<String> options = new Vector();
/*  208:     */     
/*  209: 350 */     options.add("-min");
/*  210: 351 */     options.add(getMinPointsLowerBound());
/*  211: 352 */     options.add("-max");
/*  212: 353 */     options.add(getMinPointsUpperBound());
/*  213:     */     
/*  214: 355 */     options.add("-A");
/*  215: 356 */     options.add(this.m_nnTemplate.getClass().getName() + " " + Utils.joinOptions(this.m_nnTemplate.getOptions()));
/*  216:     */     
/*  217: 358 */     options.add("-num-slots");
/*  218: 359 */     options.add(getNumExecutionSlots());
/*  219:     */     
/*  220: 361 */     return (String[])options.toArray(new String[0]);
/*  221:     */   }
/*  222:     */   
/*  223:     */   public void setClassifierMode(boolean cm)
/*  224:     */   {
/*  225: 372 */     this.m_classifierMode = cm;
/*  226:     */   }
/*  227:     */   
/*  228:     */   public String minPointsLowerBoundTipText()
/*  229:     */   {
/*  230: 382 */     return "The lower bound (minPtsLB) to use on the range for k when determining the maximum LOF value";
/*  231:     */   }
/*  232:     */   
/*  233:     */   public void setMinPointsLowerBound(String pts)
/*  234:     */   {
/*  235: 393 */     this.m_minPtsLB = pts;
/*  236:     */   }
/*  237:     */   
/*  238:     */   public String getMinPointsLowerBound()
/*  239:     */   {
/*  240: 403 */     return this.m_minPtsLB;
/*  241:     */   }
/*  242:     */   
/*  243:     */   public String minPointsUpperBoundTipText()
/*  244:     */   {
/*  245: 413 */     return "The upper bound (minPtsUB) to use on the range for k when determining the maximum LOF value";
/*  246:     */   }
/*  247:     */   
/*  248:     */   public void setMinPointsUpperBound(String pts)
/*  249:     */   {
/*  250: 424 */     this.m_minPtsUB = pts;
/*  251:     */   }
/*  252:     */   
/*  253:     */   public String getMinPointsUpperBound()
/*  254:     */   {
/*  255: 434 */     return this.m_minPtsUB;
/*  256:     */   }
/*  257:     */   
/*  258:     */   public String NNSearchTipText()
/*  259:     */   {
/*  260: 444 */     return "The nearest neighbour search algorithm to use (Default: weka.core.neighboursearch.LinearNNSearch).";
/*  261:     */   }
/*  262:     */   
/*  263:     */   public void setNNSearch(NearestNeighbourSearch s)
/*  264:     */   {
/*  265: 454 */     this.m_nnTemplate = s;
/*  266:     */   }
/*  267:     */   
/*  268:     */   public NearestNeighbourSearch getNNSearch()
/*  269:     */   {
/*  270: 463 */     return this.m_nnTemplate;
/*  271:     */   }
/*  272:     */   
/*  273:     */   public String numExecutionSlotsTipText()
/*  274:     */   {
/*  275: 473 */     return "The number of execution slots (threads) to use for finding LOF values.";
/*  276:     */   }
/*  277:     */   
/*  278:     */   public void setNumExecutionSlots(String slots)
/*  279:     */   {
/*  280: 485 */     this.m_numSlots = slots;
/*  281:     */   }
/*  282:     */   
/*  283:     */   public String getNumExecutionSlots()
/*  284:     */   {
/*  285: 496 */     return this.m_numSlots;
/*  286:     */   }
/*  287:     */   
/*  288:     */   public boolean setInputFormat(Instances instanceInfo)
/*  289:     */     throws Exception
/*  290:     */   {
/*  291: 510 */     super.setInputFormat(instanceInfo);
/*  292:     */     
/*  293: 512 */     this.m_nnSearch = null;
/*  294:     */     
/*  295: 514 */     return false;
/*  296:     */   }
/*  297:     */   
/*  298:     */   public boolean input(Instance instance)
/*  299:     */   {
/*  300: 528 */     if (getInputFormat() == null) {
/*  301: 529 */       throw new IllegalStateException("No input instance format defined");
/*  302:     */     }
/*  303: 531 */     if (this.m_NewBatch)
/*  304:     */     {
/*  305: 532 */       resetQueue();
/*  306: 533 */       this.m_NewBatch = false;
/*  307:     */     }
/*  308: 536 */     if (this.m_nnSearch != null)
/*  309:     */     {
/*  310:     */       try
/*  311:     */       {
/*  312: 538 */         postFirstBatch(instance);
/*  313:     */       }
/*  314:     */       catch (Exception ex)
/*  315:     */       {
/*  316: 540 */         ex.printStackTrace();
/*  317: 541 */         throw new IllegalStateException(ex.getMessage());
/*  318:     */       }
/*  319: 543 */       return true;
/*  320:     */     }
/*  321: 546 */     bufferInput(instance);
/*  322: 547 */     return false;
/*  323:     */   }
/*  324:     */   
/*  325:     */   public boolean batchFinished()
/*  326:     */   {
/*  327: 560 */     if (getInputFormat() == null) {
/*  328: 561 */       throw new IllegalStateException("No input instance format defined");
/*  329:     */     }
/*  330: 563 */     if (this.m_env == null) {
/*  331: 564 */       this.m_env = Environment.getSystemWide();
/*  332:     */     }
/*  333: 567 */     if (this.m_nnSearch == null)
/*  334:     */     {
/*  335: 568 */       setOutputFormat();
/*  336: 570 */       if ((this.m_numSlots != null) && (this.m_numSlots.length() > 0))
/*  337:     */       {
/*  338: 571 */         String nS = this.m_numSlots;
/*  339:     */         try
/*  340:     */         {
/*  341: 573 */           nS = this.m_env.substitute(nS);
/*  342: 574 */           this.m_numExecutionSlots = Integer.parseInt(nS);
/*  343: 576 */           if (this.m_numExecutionSlots < 1) {
/*  344: 577 */             this.m_numExecutionSlots = 1;
/*  345:     */           }
/*  346:     */         }
/*  347:     */         catch (Exception ex) {}
/*  348:     */       }
/*  349:     */       try
/*  350:     */       {
/*  351: 585 */         if ((getInputFormat().classIndex() >= 0) && (getInputFormat().numAttributes() == 1))
/*  352:     */         {
/*  353: 587 */           Instances input = getInputFormat();
/*  354: 588 */           for (int i = 0; i < input.numInstances(); i++)
/*  355:     */           {
/*  356: 589 */             Instance inst = makeOutputInstance(input.instance(i), 1.0D);
/*  357: 590 */             push(inst);
/*  358:     */           }
/*  359:     */         }
/*  360: 593 */         else if (this.m_numExecutionSlots < 2)
/*  361:     */         {
/*  362: 594 */           LOFFirstBatch();
/*  363:     */         }
/*  364:     */         else
/*  365:     */         {
/*  366: 596 */           LOFFirstBatchParallel();
/*  367:     */         }
/*  368:     */       }
/*  369:     */       catch (Exception ex)
/*  370:     */       {
/*  371: 600 */         ex.printStackTrace();
/*  372: 601 */         throw new IllegalStateException(ex.getMessage());
/*  373:     */       }
/*  374:     */     }
/*  375: 605 */     flushInput();
/*  376:     */     
/*  377: 607 */     this.m_NewBatch = true;
/*  378: 608 */     return numPendingOutput() != 0;
/*  379:     */   }
/*  380:     */   
/*  381:     */   protected void setOutputFormat()
/*  382:     */   {
/*  383: 616 */     ArrayList<Attribute> atts = new ArrayList();
/*  384: 618 */     for (int i = 0; i < getInputFormat().numAttributes(); i++) {
/*  385: 619 */       atts.add((Attribute)getInputFormat().attribute(i).copy());
/*  386:     */     }
/*  387: 622 */     atts.add(new Attribute("LOF"));
/*  388:     */     
/*  389: 624 */     Instances outputFormat = new Instances(getInputFormat().relationName(), atts, 0);
/*  390:     */     
/*  391: 626 */     outputFormat.setClassIndex(getInputFormat().classIndex());
/*  392: 627 */     setOutputFormat(outputFormat);
/*  393:     */   }
/*  394:     */   
/*  395:     */   protected void postFirstBatch(Instance inst)
/*  396:     */     throws Exception
/*  397:     */   {
/*  398: 662 */     if ((inst.classIndex() >= 0) && (inst.numAttributes() == 1))
/*  399:     */     {
/*  400: 663 */       Instance newInst = makeOutputInstance(inst, 1.0D);
/*  401: 664 */       push(newInst);
/*  402: 665 */       return;
/*  403:     */     }
/*  404: 668 */     Instances nn = null;
/*  405: 669 */     int nnFactor = 2;
/*  406: 670 */     Neighborhood currentN = new Neighborhood();
/*  407:     */     do
/*  408:     */     {
/*  409: 673 */       nn = this.m_nnSearch.kNearestNeighbours(inst, this.m_ubK * nnFactor);
/*  410: 674 */       currentN.m_neighbors = nn;
/*  411: 675 */       currentN.m_distances = this.m_nnSearch.getDistances();
/*  412: 676 */       trimZeroDistances(currentN);
/*  413:     */       
/*  414: 678 */       nnFactor++;
/*  415: 679 */     } while (nn.numInstances() < this.m_ubK);
/*  416: 681 */     currentN.m_tempCardinality = new double[this.m_ubK - this.m_lbK];
/*  417: 682 */     currentN.m_lof = new double[this.m_ubK - this.m_lbK];
/*  418: 683 */     currentN.m_lrd = new double[this.m_ubK - this.m_lbK];
/*  419: 686 */     for (int k = this.m_lbK; k < this.m_ubK; k++)
/*  420:     */     {
/*  421: 688 */       int indexOfKDistanceForK = k - 1;
/*  422: 690 */       while ((indexOfKDistanceForK < currentN.m_distances.length - 1) && (currentN.m_distances[indexOfKDistanceForK] == currentN.m_distances[(indexOfKDistanceForK + 1)])) {
/*  423: 691 */         indexOfKDistanceForK++;
/*  424:     */       }
/*  425: 695 */       double cardinality = 0.0D;
/*  426: 696 */       double sumReachability = 0.0D;
/*  427: 698 */       for (int j = 0; j <= indexOfKDistanceForK; j++)
/*  428:     */       {
/*  429: 699 */         Instance b = currentN.m_neighbors.instance(j);
/*  430: 700 */         cardinality += b.weight();
/*  431: 701 */         sumReachability += reachability(inst, b, currentN.m_distances[j], k);
/*  432:     */       }
/*  433: 704 */       currentN.m_lrd[(k - this.m_lbK)] = (cardinality / sumReachability);
/*  434:     */       
/*  435: 706 */       currentN.m_tempCardinality[(k - this.m_lbK)] = cardinality;
/*  436:     */       
/*  437: 708 */       double lofK = lof(currentN, k);
/*  438: 711 */       if (lofK > currentN.m_lof[(k - this.m_lbK)]) {
/*  439: 712 */         currentN.m_lof[(k - this.m_lbK)] = lofK;
/*  440:     */       }
/*  441:     */     }
/*  442: 716 */     double maxLOF = currentN.m_lof[Utils.maxIndex(currentN.m_lof)];
/*  443:     */     
/*  444: 718 */     Instance newInst = makeOutputInstance(inst, maxLOF);
/*  445: 719 */     push(newInst);
/*  446:     */   }
/*  447:     */   
/*  448:     */   protected void init(Instances training)
/*  449:     */     throws Exception
/*  450:     */   {
/*  451: 729 */     this.m_classSet = (training.classIndex() >= 0);
/*  452: 731 */     if (this.m_env == null) {
/*  453: 732 */       this.m_env = Environment.getSystemWide();
/*  454:     */     }
/*  455: 735 */     String lbKS = this.m_minPtsLB;
/*  456: 736 */     String ubKS = this.m_minPtsUB;
/*  457:     */     try
/*  458:     */     {
/*  459: 739 */       lbKS = this.m_env.substitute(lbKS);
/*  460: 740 */       this.m_lbK = Integer.parseInt(lbKS);
/*  461:     */     }
/*  462:     */     catch (Exception ex) {}
/*  463:     */     try
/*  464:     */     {
/*  465: 744 */       ubKS = this.m_env.substitute(ubKS);
/*  466: 745 */       this.m_ubK = Integer.parseInt(ubKS);
/*  467:     */     }
/*  468:     */     catch (Exception ex) {}
/*  469: 749 */     this.m_ubK += 1;
/*  470: 751 */     if (this.m_ubK >= training.numInstances())
/*  471:     */     {
/*  472: 752 */       System.err.println("Can't have more neighbors than data points.");
/*  473: 753 */       this.m_ubK = (training.numInstances() - 1);
/*  474:     */     }
/*  475: 755 */     if (this.m_ubK <= this.m_lbK)
/*  476:     */     {
/*  477: 756 */       System.err.println("Upper bound on k can't be < lower bound - setting equal to the lower bound");
/*  478:     */       
/*  479: 758 */       this.m_ubK = (this.m_lbK + 1);
/*  480:     */     }
/*  481: 762 */     SerializedObject o = new SerializedObject(this.m_nnTemplate);
/*  482: 763 */     this.m_nnSearch = ((NearestNeighbourSearch)o.getObject());
/*  483: 764 */     this.m_nnSearch.setInstances(new Instances(training));
/*  484: 766 */     if (this.m_numExecutionSlots > 1) {
/*  485: 767 */       this.m_kDistanceContainer = new ConcurrentHashMap();
/*  486:     */     } else {
/*  487: 769 */       this.m_kDistanceContainer = new HashMap();
/*  488:     */     }
/*  489: 772 */     this.m_instKeys = new DecisionTableHashKey[training.numInstances()];
/*  490:     */   }
/*  491:     */   
/*  492:     */   protected class Neighborhood
/*  493:     */     implements Serializable
/*  494:     */   {
/*  495:     */     private static final long serialVersionUID = 3381174623146672703L;
/*  496:     */     public Instances m_neighbors;
/*  497:     */     public double[] m_distances;
/*  498:     */     public double[] m_tempCardinality;
/*  499:     */     public double[] m_lrd;
/*  500:     */     public double[] m_lof;
/*  501:     */     
/*  502:     */     protected Neighborhood() {}
/*  503:     */   }
/*  504:     */   
/*  505:     */   protected class NNFinder
/*  506:     */     implements Runnable
/*  507:     */   {
/*  508:     */     protected Instances m_nnTrain;
/*  509:     */     protected int m_start;
/*  510:     */     protected int m_end;
/*  511:     */     protected NearestNeighbourSearch m_search;
/*  512:     */     
/*  513:     */     public NNFinder(Instances training, int start, int end, NearestNeighbourSearch search)
/*  514:     */     {
/*  515: 789 */       this.m_nnTrain = training;
/*  516: 790 */       this.m_start = start;
/*  517: 791 */       this.m_end = end;
/*  518: 792 */       this.m_search = search;
/*  519:     */     }
/*  520:     */     
/*  521:     */     public void run()
/*  522:     */     {
/*  523:     */       try
/*  524:     */       {
/*  525: 798 */         for (int i = this.m_start; i < this.m_end; i++)
/*  526:     */         {
/*  527: 799 */           Instance current = this.m_nnTrain.get(i);
/*  528: 800 */           DecisionTableHashKey key = new DecisionTableHashKey(current, current.numAttributes(), !LOF.this.m_classSet);
/*  529:     */           
/*  530: 802 */           LOF.Neighborhood n = new LOF.Neighborhood(LOF.this);
/*  531: 803 */           if (LOF.this.addToKDistanceContainer(key, n))
/*  532:     */           {
/*  533: 804 */             Instances nn = null;
/*  534: 805 */             int nnFactor = 2;
/*  535:     */             do
/*  536:     */             {
/*  537: 807 */               nn = this.m_search.kNearestNeighbours(current, LOF.this.m_ubK * nnFactor);
/*  538: 808 */               n.m_neighbors = nn;
/*  539: 809 */               n.m_distances = this.m_search.getDistances();
/*  540: 810 */               LOF.this.trimZeroDistances(n);
/*  541:     */               
/*  542: 812 */               nnFactor++;
/*  543: 813 */             } while (nn.numInstances() < LOF.this.m_ubK);
/*  544: 815 */             n.m_tempCardinality = new double[LOF.this.m_ubK - LOF.this.m_lbK];
/*  545: 816 */             n.m_lrd = new double[LOF.this.m_ubK - LOF.this.m_lbK];
/*  546: 817 */             n.m_lof = new double[LOF.this.m_ubK - LOF.this.m_lbK];
/*  547:     */           }
/*  548: 819 */           LOF.this.m_instKeys[i] = key;
/*  549:     */         }
/*  550: 821 */         LOF.this.completedTask("NN search", true, LOF.this.m_numExecutionSlots);
/*  551:     */       }
/*  552:     */       catch (Exception ex)
/*  553:     */       {
/*  554: 823 */         ex.printStackTrace();
/*  555: 824 */         LOF.this.completedTask("NN search", false, LOF.this.m_numExecutionSlots);
/*  556:     */       }
/*  557:     */     }
/*  558:     */   }
/*  559:     */   
/*  560:     */   protected class LOFFinder
/*  561:     */     implements Runnable
/*  562:     */   {
/*  563:     */     protected Instances m_lofTrain;
/*  564:     */     protected int m_k;
/*  565:     */     
/*  566:     */     public LOFFinder(Instances training, int k)
/*  567:     */     {
/*  568: 839 */       this.m_lofTrain = training;
/*  569: 840 */       this.m_k = k;
/*  570:     */     }
/*  571:     */     
/*  572:     */     public void run()
/*  573:     */     {
/*  574:     */       try
/*  575:     */       {
/*  576: 847 */         for (int i = 0; i < this.m_lofTrain.numInstances(); i++)
/*  577:     */         {
/*  578: 848 */           Instance current = this.m_lofTrain.instance(i);
/*  579:     */           
/*  580: 850 */           LOF.Neighborhood currentN = (LOF.Neighborhood)LOF.this.m_kDistanceContainer.get(LOF.this.m_instKeys[i]);
/*  581:     */           
/*  582:     */ 
/*  583:     */ 
/*  584: 854 */           int indexOfKDistanceForK = this.m_k - 1;
/*  585: 856 */           while ((indexOfKDistanceForK < currentN.m_distances.length - 1) && (currentN.m_distances[indexOfKDistanceForK] == currentN.m_distances[(indexOfKDistanceForK + 1)])) {
/*  586: 857 */             indexOfKDistanceForK++;
/*  587:     */           }
/*  588: 861 */           double cardinality = 0.0D;
/*  589: 862 */           double sumReachability = 0.0D;
/*  590: 864 */           for (int j = 0; j <= indexOfKDistanceForK; j++)
/*  591:     */           {
/*  592: 865 */             Instance b = currentN.m_neighbors.instance(j);
/*  593:     */             
/*  594: 867 */             cardinality += b.weight();
/*  595: 868 */             sumReachability += LOF.this.reachability(current, b, currentN.m_distances[j], this.m_k);
/*  596:     */           }
/*  597: 872 */           currentN.m_lrd[(this.m_k - LOF.this.m_lbK)] = (cardinality / sumReachability);
/*  598:     */           
/*  599:     */ 
/*  600: 875 */           currentN.m_tempCardinality[(this.m_k - LOF.this.m_lbK)] = cardinality;
/*  601:     */         }
/*  602: 879 */         for (int i = 0; i < this.m_lofTrain.numInstances(); i++)
/*  603:     */         {
/*  604: 880 */           LOF.Neighborhood currentN = (LOF.Neighborhood)LOF.this.m_kDistanceContainer.get(LOF.this.m_instKeys[i]);
/*  605:     */           
/*  606: 882 */           double lofK = LOF.this.lof(currentN, this.m_k);
/*  607:     */           
/*  608:     */ 
/*  609: 885 */           currentN.m_lof[(this.m_k - LOF.this.m_lbK)] = lofK;
/*  610:     */         }
/*  611: 887 */         LOF.this.completedTask("LOF finder", true, LOF.this.m_ubK - LOF.this.m_lbK);
/*  612:     */       }
/*  613:     */       catch (Exception ex)
/*  614:     */       {
/*  615: 889 */         ex.printStackTrace();
/*  616: 890 */         LOF.this.completedTask("LOF finder", false, LOF.this.m_ubK - LOF.this.m_lbK);
/*  617:     */       }
/*  618:     */     }
/*  619:     */   }
/*  620:     */   
/*  621:     */   protected void LOFFirstBatchParallel()
/*  622:     */     throws Exception
/*  623:     */   {
/*  624: 904 */     Instances training = getInputFormat();
/*  625: 905 */     init(training);
/*  626: 906 */     this.m_completed = 0;
/*  627: 907 */     this.m_failed = 0;
/*  628: 908 */     startExecutorPool();
/*  629:     */     
/*  630:     */ 
/*  631: 911 */     int numPerThread = training.numInstances() / this.m_numExecutionSlots;
/*  632: 912 */     if (numPerThread < 1)
/*  633:     */     {
/*  634: 913 */       this.m_numExecutionSlots = training.numInstances();
/*  635: 914 */       numPerThread = 1;
/*  636:     */     }
/*  637: 916 */     int start = 0;
/*  638: 917 */     int end = 0;
/*  639: 918 */     for (int i = 0; i < this.m_numExecutionSlots; i++)
/*  640:     */     {
/*  641: 919 */       if (i == this.m_numExecutionSlots - 1) {
/*  642: 920 */         end = training.numInstances();
/*  643:     */       } else {
/*  644: 922 */         end = start + numPerThread;
/*  645:     */       }
/*  646: 924 */       SerializedObject oo = new SerializedObject(this.m_nnTemplate);
/*  647: 925 */       NearestNeighbourSearch s = (NearestNeighbourSearch)oo.getObject();
/*  648: 926 */       s.setInstances(new Instances(training));
/*  649: 927 */       NNFinder finder = new NNFinder(new Instances(training), start, end, s);
/*  650: 928 */       this.m_executorPool.execute(finder);
/*  651: 929 */       start += numPerThread;
/*  652:     */     }
/*  653: 932 */     if (this.m_completed + this.m_failed < this.m_numExecutionSlots) {
/*  654: 933 */       block(true, this.m_numExecutionSlots);
/*  655:     */     }
/*  656: 936 */     if (this.m_failed > 0) {
/*  657: 937 */       throw new Exception("Can't continue - some tasks failed during the nearest neighbour phase");
/*  658:     */     }
/*  659: 944 */     this.m_completed = 0;
/*  660: 945 */     this.m_failed = 0;
/*  661: 946 */     int numLOFFinders = this.m_ubK - this.m_lbK;
/*  662: 949 */     for (int k = this.m_lbK; k < this.m_ubK; k++)
/*  663:     */     {
/*  664: 950 */       LOFFinder finder = new LOFFinder(training, k);
/*  665: 951 */       this.m_executorPool.execute(finder);
/*  666:     */     }
/*  667: 954 */     if (this.m_completed + this.m_failed < numLOFFinders) {
/*  668: 955 */       block(true, numLOFFinders);
/*  669:     */     }
/*  670: 958 */     if (this.m_failed > 0) {
/*  671: 959 */       throw new Exception("Can't continue - some tasks failed during the LOF phase");
/*  672:     */     }
/*  673: 962 */     this.m_executorPool.shutdown();
/*  674: 965 */     if (!this.m_classifierMode) {
/*  675: 966 */       for (int i = 0; i < training.numInstances(); i++)
/*  676:     */       {
/*  677: 967 */         Instance current = training.instance(i);
/*  678: 968 */         Neighborhood currentN = (Neighborhood)this.m_kDistanceContainer.get(this.m_instKeys[i]);
/*  679:     */         
/*  680:     */ 
/*  681: 971 */         double maxLOF = currentN.m_lof[Utils.maxIndex(currentN.m_lof)];
/*  682: 972 */         Instance inst = makeOutputInstance(current, maxLOF);
/*  683: 973 */         push(inst);
/*  684:     */       }
/*  685:     */     }
/*  686:     */   }
/*  687:     */   
/*  688:     */   protected synchronized boolean addToKDistanceContainer(DecisionTableHashKey key, Neighborhood n)
/*  689:     */   {
/*  690: 981 */     if (!this.m_kDistanceContainer.containsKey(key))
/*  691:     */     {
/*  692: 982 */       this.m_kDistanceContainer.put(key, n);
/*  693: 983 */       return true;
/*  694:     */     }
/*  695: 986 */     return false;
/*  696:     */   }
/*  697:     */   
/*  698:     */   protected synchronized void completedTask(String taskType, boolean success, int totalTasks)
/*  699:     */   {
/*  700: 991 */     if (!success)
/*  701:     */     {
/*  702: 992 */       System.err.println("A " + taskType + " task failed!");
/*  703: 993 */       this.m_failed += 1;
/*  704:     */     }
/*  705:     */     else
/*  706:     */     {
/*  707: 995 */       this.m_completed += 1;
/*  708:     */     }
/*  709: 998 */     if (this.m_completed + this.m_failed == totalTasks)
/*  710:     */     {
/*  711: 999 */       if (this.m_failed > 0) {
/*  712:1000 */         System.err.println("Problem executing " + taskType + " tasks - some iterations failed.");
/*  713:     */       }
/*  714:1004 */       block(false, totalTasks);
/*  715:     */     }
/*  716:     */   }
/*  717:     */   
/*  718:     */   private synchronized void block(boolean tf, int totalTasks)
/*  719:     */   {
/*  720:1009 */     if (tf) {
/*  721:     */       try
/*  722:     */       {
/*  723:1011 */         if (this.m_completed + this.m_failed < totalTasks) {
/*  724:1012 */           wait();
/*  725:     */         }
/*  726:     */       }
/*  727:     */       catch (InterruptedException ex) {}
/*  728:     */     } else {
/*  729:1017 */       notifyAll();
/*  730:     */     }
/*  731:     */   }
/*  732:     */   
/*  733:     */   protected void startExecutorPool()
/*  734:     */   {
/*  735:1025 */     if (this.m_executorPool != null) {
/*  736:1026 */       this.m_executorPool.shutdownNow();
/*  737:     */     }
/*  738:1029 */     this.m_executorPool = new ThreadPoolExecutor(this.m_numExecutionSlots, this.m_numExecutionSlots, 120L, TimeUnit.SECONDS, new LinkedBlockingQueue());
/*  739:     */   }
/*  740:     */   
/*  741:     */   protected synchronized void trimZeroDistances(Neighborhood n)
/*  742:     */   {
/*  743:1035 */     int index = n.m_neighbors.numInstances();
/*  744:1036 */     for (int i = 0; i < n.m_neighbors.numInstances(); i++) {
/*  745:1037 */       if (n.m_distances[i] > 0.0D)
/*  746:     */       {
/*  747:1038 */         index = i;
/*  748:1039 */         break;
/*  749:     */       }
/*  750:     */     }
/*  751:1043 */     if (index > 0)
/*  752:     */     {
/*  753:1045 */       for (int i = 0; i < index; i++) {
/*  754:1046 */         n.m_neighbors.remove(0);
/*  755:     */       }
/*  756:1048 */       double[] newDist = new double[n.m_distances.length - index];
/*  757:1049 */       System.arraycopy(n.m_distances, index, newDist, 0, newDist.length);
/*  758:1050 */       n.m_distances = newDist;
/*  759:     */     }
/*  760:     */   }
/*  761:     */   
/*  762:     */   protected void LOFFirstBatch()
/*  763:     */     throws Exception
/*  764:     */   {
/*  765:1060 */     Instances training = getInputFormat();
/*  766:1061 */     init(training);
/*  767:1063 */     for (int i = 0; i < training.numInstances(); i++)
/*  768:     */     {
/*  769:1064 */       Instance current = training.get(i);
/*  770:1065 */       DecisionTableHashKey key = new DecisionTableHashKey(current, current.numAttributes(), !this.m_classSet);
/*  771:1067 */       if (!this.m_kDistanceContainer.containsKey(key))
/*  772:     */       {
/*  773:1069 */         int nnFactor = 2;
/*  774:1070 */         Instances nn = null;
/*  775:1071 */         Neighborhood n = new Neighborhood();
/*  776:     */         do
/*  777:     */         {
/*  778:1073 */           nn = this.m_nnSearch.kNearestNeighbours(current, this.m_ubK * nnFactor);
/*  779:1074 */           n.m_neighbors = nn;
/*  780:1075 */           n.m_distances = this.m_nnSearch.getDistances();
/*  781:1076 */           trimZeroDistances(n);
/*  782:     */           
/*  783:1078 */           nnFactor++;
/*  784:1079 */         } while (nn.numInstances() < this.m_ubK);
/*  785:1081 */         n.m_tempCardinality = new double[this.m_ubK - this.m_lbK];
/*  786:1082 */         n.m_lrd = new double[this.m_ubK - this.m_lbK];
/*  787:1083 */         n.m_lof = new double[this.m_ubK - this.m_lbK];
/*  788:     */         
/*  789:1085 */         this.m_kDistanceContainer.put(key, n);
/*  790:     */       }
/*  791:1087 */       this.m_instKeys[i] = key;
/*  792:     */     }
/*  793:1091 */     for (int k = this.m_lbK; k < this.m_ubK; k++)
/*  794:     */     {
/*  795:1094 */       for (int i = 0; i < training.numInstances(); i++)
/*  796:     */       {
/*  797:1095 */         Instance current = training.instance(i);
/*  798:     */         
/*  799:1097 */         Neighborhood currentN = (Neighborhood)this.m_kDistanceContainer.get(this.m_instKeys[i]);
/*  800:     */         
/*  801:     */ 
/*  802:     */ 
/*  803:1101 */         int indexOfKDistanceForK = k - 1;
/*  804:1103 */         while ((indexOfKDistanceForK < currentN.m_distances.length - 1) && (currentN.m_distances[indexOfKDistanceForK] == currentN.m_distances[(indexOfKDistanceForK + 1)])) {
/*  805:1104 */           indexOfKDistanceForK++;
/*  806:     */         }
/*  807:1108 */         double cardinality = 0.0D;
/*  808:1109 */         double sumReachability = 0.0D;
/*  809:1111 */         for (int j = 0; j <= indexOfKDistanceForK; j++)
/*  810:     */         {
/*  811:1112 */           Instance b = currentN.m_neighbors.instance(j);
/*  812:1113 */           cardinality += b.weight();
/*  813:1114 */           sumReachability += reachability(current, b, currentN.m_distances[j], k);
/*  814:     */         }
/*  815:1118 */         currentN.m_lrd[(k - this.m_lbK)] = (cardinality / sumReachability);
/*  816:     */         
/*  817:     */ 
/*  818:1121 */         currentN.m_tempCardinality[(k - this.m_lbK)] = cardinality;
/*  819:     */       }
/*  820:1125 */       for (int i = 0; i < training.numInstances(); i++)
/*  821:     */       {
/*  822:1126 */         Neighborhood currentN = (Neighborhood)this.m_kDistanceContainer.get(this.m_instKeys[i]);
/*  823:     */         
/*  824:1128 */         double lofK = lof(currentN, k);
/*  825:     */         
/*  826:     */ 
/*  827:1131 */         currentN.m_lof[(k - this.m_lbK)] = lofK;
/*  828:     */       }
/*  829:     */     }
/*  830:1136 */     if (!this.m_classifierMode) {
/*  831:1137 */       for (int i = 0; i < training.numInstances(); i++)
/*  832:     */       {
/*  833:1138 */         Instance current = training.instance(i);
/*  834:1139 */         Neighborhood currentN = (Neighborhood)this.m_kDistanceContainer.get(this.m_instKeys[i]);
/*  835:     */         
/*  836:     */ 
/*  837:1142 */         double maxLOF = currentN.m_lof[Utils.maxIndex(currentN.m_lof)];
/*  838:1143 */         Instance inst = makeOutputInstance(current, maxLOF);
/*  839:1144 */         push(inst);
/*  840:     */       }
/*  841:     */     }
/*  842:     */   }
/*  843:     */   
/*  844:     */   protected Instance makeOutputInstance(Instance original, double lof)
/*  845:     */   {
/*  846:1159 */     int numAtts = getInputFormat().numAttributes() + 1;
/*  847:     */     
/*  848:1161 */     double[] vals = new double[numAtts];
/*  849:1164 */     for (int j = 0; j < original.numAttributes(); j++) {
/*  850:1165 */       vals[j] = original.value(j);
/*  851:     */     }
/*  852:1169 */     vals[(numAtts - 1)] = lof;
/*  853:1170 */     Instance inst = null;
/*  854:1171 */     if ((original instanceof SparseInstance)) {
/*  855:1172 */       inst = new SparseInstance(original.weight(), vals);
/*  856:     */     } else {
/*  857:1174 */       inst = new DenseInstance(original.weight(), vals);
/*  858:     */     }
/*  859:1176 */     inst.setDataset(getOutputFormat());
/*  860:1177 */     copyValues(inst, false, original.dataset(), getOutputFormat());
/*  861:1178 */     inst.setDataset(getOutputFormat());
/*  862:     */     
/*  863:1180 */     return inst;
/*  864:     */   }
/*  865:     */   
/*  866:     */   protected double lof(Neighborhood neighborhoodA, int k)
/*  867:     */   {
/*  868:1191 */     double sumlrdb = 0.0D;
/*  869:     */     
/*  870:1193 */     int indexOfKDistanceForK = k - 1;
/*  871:1195 */     while ((indexOfKDistanceForK < neighborhoodA.m_distances.length - 1) && (neighborhoodA.m_distances[indexOfKDistanceForK] == neighborhoodA.m_distances[(indexOfKDistanceForK + 1)])) {
/*  872:1196 */       indexOfKDistanceForK++;
/*  873:     */     }
/*  874:1199 */     for (int i = 0; i <= indexOfKDistanceForK; i++)
/*  875:     */     {
/*  876:1200 */       Instance b = neighborhoodA.m_neighbors.get(i);
/*  877:1201 */       DecisionTableHashKey bkey = null;
/*  878:     */       try
/*  879:     */       {
/*  880:1203 */         bkey = new DecisionTableHashKey(b, b.numAttributes(), !this.m_classSet);
/*  881:     */       }
/*  882:     */       catch (Exception ex) {}
/*  883:1206 */       Neighborhood bTemp = (Neighborhood)this.m_kDistanceContainer.get(bkey);
/*  884:     */       
/*  885:1208 */       sumlrdb += bTemp.m_lrd[(k - this.m_lbK)];
/*  886:     */     }
/*  887:1211 */     return sumlrdb / (neighborhoodA.m_tempCardinality[(k - this.m_lbK)] * neighborhoodA.m_lrd[(k - this.m_lbK)]);
/*  888:     */   }
/*  889:     */   
/*  890:     */   protected double reachability(Instance a, Instance b, double distAB, int k)
/*  891:     */   {
/*  892:1228 */     DecisionTableHashKey bkey = null;
/*  893:     */     try
/*  894:     */     {
/*  895:1230 */       bkey = new DecisionTableHashKey(b, b.numAttributes(), !this.m_classSet);
/*  896:     */     }
/*  897:     */     catch (Exception ex) {}
/*  898:1233 */     Neighborhood bN = (Neighborhood)this.m_kDistanceContainer.get(bkey);
/*  899:     */     
/*  900:     */ 
/*  901:1236 */     k--;
/*  902:1237 */     double kDistanceB = bN.m_distances[k];
/*  903:1238 */     while ((k < bN.m_distances.length - 1) && (kDistanceB == bN.m_distances[(k + 1)]))
/*  904:     */     {
/*  905:1239 */       k++;
/*  906:1240 */       kDistanceB = bN.m_distances[k];
/*  907:     */     }
/*  908:1243 */     return Math.max(kDistanceB, distAB);
/*  909:     */   }
/*  910:     */   
/*  911:     */   public void setEnvironment(Environment env)
/*  912:     */   {
/*  913:1253 */     this.m_env = env;
/*  914:     */   }
/*  915:     */   
/*  916:     */   public String getRevision()
/*  917:     */   {
/*  918:1263 */     return RevisionUtils.extract("$Revision: 12154 $");
/*  919:     */   }
/*  920:     */   
/*  921:     */   public static void main(String[] args)
/*  922:     */   {
/*  923:1272 */     runFilter(new LOF(), args);
/*  924:     */   }
/*  925:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.LOF
 * JD-Core Version:    0.7.0.1
 */