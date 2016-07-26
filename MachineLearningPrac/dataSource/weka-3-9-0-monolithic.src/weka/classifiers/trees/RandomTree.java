/*    1:     */ package weka.classifiers.trees;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.io.Serializable;
/*    5:     */ import java.util.Collections;
/*    6:     */ import java.util.Enumeration;
/*    7:     */ import java.util.LinkedList;
/*    8:     */ import java.util.Queue;
/*    9:     */ import java.util.Random;
/*   10:     */ import java.util.Vector;
/*   11:     */ import weka.classifiers.AbstractClassifier;
/*   12:     */ import weka.classifiers.Classifier;
/*   13:     */ import weka.classifiers.rules.ZeroR;
/*   14:     */ import weka.core.Attribute;
/*   15:     */ import weka.core.Capabilities;
/*   16:     */ import weka.core.Capabilities.Capability;
/*   17:     */ import weka.core.ContingencyTables;
/*   18:     */ import weka.core.Drawable;
/*   19:     */ import weka.core.Instance;
/*   20:     */ import weka.core.Instances;
/*   21:     */ import weka.core.Option;
/*   22:     */ import weka.core.OptionHandler;
/*   23:     */ import weka.core.PartitionGenerator;
/*   24:     */ import weka.core.Randomizable;
/*   25:     */ import weka.core.RevisionUtils;
/*   26:     */ import weka.core.Utils;
/*   27:     */ import weka.core.WeightedInstancesHandler;
/*   28:     */ 
/*   29:     */ public class RandomTree
/*   30:     */   extends AbstractClassifier
/*   31:     */   implements OptionHandler, WeightedInstancesHandler, Randomizable, Drawable, PartitionGenerator
/*   32:     */ {
/*   33:     */   private static final long serialVersionUID = -9051119597407396024L;
/*   34:     */   protected Tree m_Tree;
/*   35:     */   protected Instances m_Info;
/*   36:     */   protected double m_MinNum;
/*   37:     */   protected int m_KValue;
/*   38:     */   protected int m_randomSeed;
/*   39:     */   protected int m_MaxDepth;
/*   40:     */   protected int m_NumFolds;
/*   41:     */   protected boolean m_AllowUnclassifiedInstances;
/*   42:     */   protected boolean m_BreakTiesRandomly;
/*   43:     */   protected Classifier m_zeroR;
/*   44:     */   protected double m_MinVarianceProp;
/*   45:     */   
/*   46:     */   public RandomTree()
/*   47:     */   {
/*   48: 111 */     this.m_Tree = null;
/*   49:     */     
/*   50:     */ 
/*   51: 114 */     this.m_Info = null;
/*   52:     */     
/*   53:     */ 
/*   54: 117 */     this.m_MinNum = 1.0D;
/*   55:     */     
/*   56:     */ 
/*   57: 120 */     this.m_KValue = 0;
/*   58:     */     
/*   59:     */ 
/*   60: 123 */     this.m_randomSeed = 1;
/*   61:     */     
/*   62:     */ 
/*   63: 126 */     this.m_MaxDepth = 0;
/*   64:     */     
/*   65:     */ 
/*   66: 129 */     this.m_NumFolds = 0;
/*   67:     */     
/*   68:     */ 
/*   69: 132 */     this.m_AllowUnclassifiedInstances = false;
/*   70:     */     
/*   71:     */ 
/*   72: 135 */     this.m_BreakTiesRandomly = false;
/*   73:     */     
/*   74:     */ 
/*   75:     */ 
/*   76:     */ 
/*   77:     */ 
/*   78:     */ 
/*   79:     */ 
/*   80:     */ 
/*   81: 144 */     this.m_MinVarianceProp = 0.001D;
/*   82:     */   }
/*   83:     */   
/*   84:     */   public String globalInfo()
/*   85:     */   {
/*   86: 154 */     return "Class for constructing a tree that considers K randomly  chosen attributes at each node. Performs no pruning. Also has an option to allow estimation of class probabilities (or target mean in the regression case) based on a hold-out set (backfitting).";
/*   87:     */   }
/*   88:     */   
/*   89:     */   public String minNumTipText()
/*   90:     */   {
/*   91: 167 */     return "The minimum total weight of the instances in a leaf.";
/*   92:     */   }
/*   93:     */   
/*   94:     */   public double getMinNum()
/*   95:     */   {
/*   96: 177 */     return this.m_MinNum;
/*   97:     */   }
/*   98:     */   
/*   99:     */   public void setMinNum(double newMinNum)
/*  100:     */   {
/*  101: 187 */     this.m_MinNum = newMinNum;
/*  102:     */   }
/*  103:     */   
/*  104:     */   public String minVariancePropTipText()
/*  105:     */   {
/*  106: 197 */     return "The minimum proportion of the variance on all the data that needs to be present at a node in order for splitting to be performed in regression trees.";
/*  107:     */   }
/*  108:     */   
/*  109:     */   public double getMinVarianceProp()
/*  110:     */   {
/*  111: 209 */     return this.m_MinVarianceProp;
/*  112:     */   }
/*  113:     */   
/*  114:     */   public void setMinVarianceProp(double newMinVarianceProp)
/*  115:     */   {
/*  116: 219 */     this.m_MinVarianceProp = newMinVarianceProp;
/*  117:     */   }
/*  118:     */   
/*  119:     */   public String KValueTipText()
/*  120:     */   {
/*  121: 229 */     return "Sets the number of randomly chosen attributes. If 0, int(log_2(#predictors) + 1) is used.";
/*  122:     */   }
/*  123:     */   
/*  124:     */   public int getKValue()
/*  125:     */   {
/*  126: 239 */     return this.m_KValue;
/*  127:     */   }
/*  128:     */   
/*  129:     */   public void setKValue(int k)
/*  130:     */   {
/*  131: 249 */     this.m_KValue = k;
/*  132:     */   }
/*  133:     */   
/*  134:     */   public String seedTipText()
/*  135:     */   {
/*  136: 259 */     return "The random number seed used for selecting attributes.";
/*  137:     */   }
/*  138:     */   
/*  139:     */   public void setSeed(int seed)
/*  140:     */   {
/*  141: 270 */     this.m_randomSeed = seed;
/*  142:     */   }
/*  143:     */   
/*  144:     */   public int getSeed()
/*  145:     */   {
/*  146: 281 */     return this.m_randomSeed;
/*  147:     */   }
/*  148:     */   
/*  149:     */   public String maxDepthTipText()
/*  150:     */   {
/*  151: 291 */     return "The maximum depth of the tree, 0 for unlimited.";
/*  152:     */   }
/*  153:     */   
/*  154:     */   public int getMaxDepth()
/*  155:     */   {
/*  156: 300 */     return this.m_MaxDepth;
/*  157:     */   }
/*  158:     */   
/*  159:     */   public void setMaxDepth(int value)
/*  160:     */   {
/*  161: 309 */     this.m_MaxDepth = value;
/*  162:     */   }
/*  163:     */   
/*  164:     */   public String numFoldsTipText()
/*  165:     */   {
/*  166: 319 */     return "Determines the amount of data used for backfitting. One fold is used for backfitting, the rest for growing the tree. (Default: 0, no backfitting)";
/*  167:     */   }
/*  168:     */   
/*  169:     */   public int getNumFolds()
/*  170:     */   {
/*  171: 330 */     return this.m_NumFolds;
/*  172:     */   }
/*  173:     */   
/*  174:     */   public void setNumFolds(int newNumFolds)
/*  175:     */   {
/*  176: 340 */     this.m_NumFolds = newNumFolds;
/*  177:     */   }
/*  178:     */   
/*  179:     */   public String allowUnclassifiedInstancesTipText()
/*  180:     */   {
/*  181: 350 */     return "Whether to allow unclassified instances.";
/*  182:     */   }
/*  183:     */   
/*  184:     */   public boolean getAllowUnclassifiedInstances()
/*  185:     */   {
/*  186: 360 */     return this.m_AllowUnclassifiedInstances;
/*  187:     */   }
/*  188:     */   
/*  189:     */   public void setAllowUnclassifiedInstances(boolean newAllowUnclassifiedInstances)
/*  190:     */   {
/*  191: 370 */     this.m_AllowUnclassifiedInstances = newAllowUnclassifiedInstances;
/*  192:     */   }
/*  193:     */   
/*  194:     */   public String breakTiesRandomlyTipText()
/*  195:     */   {
/*  196: 380 */     return "Break ties randomly when several attributes look equally good.";
/*  197:     */   }
/*  198:     */   
/*  199:     */   public boolean getBreakTiesRandomly()
/*  200:     */   {
/*  201: 390 */     return this.m_BreakTiesRandomly;
/*  202:     */   }
/*  203:     */   
/*  204:     */   public void setBreakTiesRandomly(boolean newBreakTiesRandomly)
/*  205:     */   {
/*  206: 400 */     this.m_BreakTiesRandomly = newBreakTiesRandomly;
/*  207:     */   }
/*  208:     */   
/*  209:     */   public Enumeration<Option> listOptions()
/*  210:     */   {
/*  211: 411 */     Vector<Option> newVector = new Vector();
/*  212:     */     
/*  213: 413 */     newVector.addElement(new Option("\tNumber of attributes to randomly investigate.\t(default 0)\n\t(<1 = int(log_2(#predictors)+1)).", "K", 1, "-K <number of attributes>"));
/*  214:     */     
/*  215:     */ 
/*  216:     */ 
/*  217:     */ 
/*  218: 418 */     newVector.addElement(new Option("\tSet minimum number of instances per leaf.\n\t(default 1)", "M", 1, "-M <minimum number of instances>"));
/*  219:     */     
/*  220:     */ 
/*  221:     */ 
/*  222: 422 */     newVector.addElement(new Option("\tSet minimum numeric class variance proportion\n\tof train variance for split (default 1e-3).", "V", 1, "-V <minimum variance for split>"));
/*  223:     */     
/*  224:     */ 
/*  225:     */ 
/*  226:     */ 
/*  227: 427 */     newVector.addElement(new Option("\tSeed for random number generator.\n\t(default 1)", "S", 1, "-S <num>"));
/*  228:     */     
/*  229:     */ 
/*  230: 430 */     newVector.addElement(new Option("\tThe maximum depth of the tree, 0 for unlimited.\n\t(default 0)", "depth", 1, "-depth <num>"));
/*  231:     */     
/*  232:     */ 
/*  233:     */ 
/*  234: 434 */     newVector.addElement(new Option("\tNumber of folds for backfitting (default 0, no backfitting).", "N", 1, "-N <num>"));
/*  235:     */     
/*  236: 436 */     newVector.addElement(new Option("\tAllow unclassified instances.", "U", 0, "-U"));
/*  237:     */     
/*  238: 438 */     newVector.addElement(new Option("\t" + breakTiesRandomlyTipText(), "B", 0, "-B"));
/*  239:     */     
/*  240: 440 */     newVector.addAll(Collections.list(super.listOptions()));
/*  241:     */     
/*  242: 442 */     return newVector.elements();
/*  243:     */   }
/*  244:     */   
/*  245:     */   public String[] getOptions()
/*  246:     */   {
/*  247: 452 */     Vector<String> result = new Vector();
/*  248:     */     
/*  249: 454 */     result.add("-K");
/*  250: 455 */     result.add("" + getKValue());
/*  251:     */     
/*  252: 457 */     result.add("-M");
/*  253: 458 */     result.add("" + getMinNum());
/*  254:     */     
/*  255: 460 */     result.add("-V");
/*  256: 461 */     result.add("" + getMinVarianceProp());
/*  257:     */     
/*  258: 463 */     result.add("-S");
/*  259: 464 */     result.add("" + getSeed());
/*  260: 466 */     if (getMaxDepth() > 0)
/*  261:     */     {
/*  262: 467 */       result.add("-depth");
/*  263: 468 */       result.add("" + getMaxDepth());
/*  264:     */     }
/*  265: 471 */     if (getNumFolds() > 0)
/*  266:     */     {
/*  267: 472 */       result.add("-N");
/*  268: 473 */       result.add("" + getNumFolds());
/*  269:     */     }
/*  270: 476 */     if (getAllowUnclassifiedInstances()) {
/*  271: 477 */       result.add("-U");
/*  272:     */     }
/*  273: 480 */     if (getBreakTiesRandomly()) {
/*  274: 481 */       result.add("-B");
/*  275:     */     }
/*  276: 484 */     Collections.addAll(result, super.getOptions());
/*  277:     */     
/*  278: 486 */     return (String[])result.toArray(new String[result.size()]);
/*  279:     */   }
/*  280:     */   
/*  281:     */   public void setOptions(String[] options)
/*  282:     */     throws Exception
/*  283:     */   {
/*  284: 545 */     String tmpStr = Utils.getOption('K', options);
/*  285: 546 */     if (tmpStr.length() != 0) {
/*  286: 547 */       this.m_KValue = Integer.parseInt(tmpStr);
/*  287:     */     } else {
/*  288: 549 */       this.m_KValue = 0;
/*  289:     */     }
/*  290: 552 */     tmpStr = Utils.getOption('M', options);
/*  291: 553 */     if (tmpStr.length() != 0) {
/*  292: 554 */       this.m_MinNum = Double.parseDouble(tmpStr);
/*  293:     */     } else {
/*  294: 556 */       this.m_MinNum = 1.0D;
/*  295:     */     }
/*  296: 559 */     String minVarString = Utils.getOption('V', options);
/*  297: 560 */     if (minVarString.length() != 0) {
/*  298: 561 */       this.m_MinVarianceProp = Double.parseDouble(minVarString);
/*  299:     */     } else {
/*  300: 563 */       this.m_MinVarianceProp = 0.001D;
/*  301:     */     }
/*  302: 566 */     tmpStr = Utils.getOption('S', options);
/*  303: 567 */     if (tmpStr.length() != 0) {
/*  304: 568 */       setSeed(Integer.parseInt(tmpStr));
/*  305:     */     } else {
/*  306: 570 */       setSeed(1);
/*  307:     */     }
/*  308: 573 */     tmpStr = Utils.getOption("depth", options);
/*  309: 574 */     if (tmpStr.length() != 0) {
/*  310: 575 */       setMaxDepth(Integer.parseInt(tmpStr));
/*  311:     */     } else {
/*  312: 577 */       setMaxDepth(0);
/*  313:     */     }
/*  314: 579 */     String numFoldsString = Utils.getOption('N', options);
/*  315: 580 */     if (numFoldsString.length() != 0) {
/*  316: 581 */       this.m_NumFolds = Integer.parseInt(numFoldsString);
/*  317:     */     } else {
/*  318: 583 */       this.m_NumFolds = 0;
/*  319:     */     }
/*  320: 586 */     setAllowUnclassifiedInstances(Utils.getFlag('U', options));
/*  321:     */     
/*  322: 588 */     setBreakTiesRandomly(Utils.getFlag('B', options));
/*  323:     */     
/*  324: 590 */     super.setOptions(options);
/*  325:     */     
/*  326: 592 */     Utils.checkForRemainingOptions(options);
/*  327:     */   }
/*  328:     */   
/*  329:     */   public Capabilities getCapabilities()
/*  330:     */   {
/*  331: 602 */     Capabilities result = super.getCapabilities();
/*  332: 603 */     result.disableAll();
/*  333:     */     
/*  334:     */ 
/*  335: 606 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  336: 607 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  337: 608 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  338: 609 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  339:     */     
/*  340:     */ 
/*  341: 612 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  342: 613 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*  343: 614 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  344:     */     
/*  345: 616 */     return result;
/*  346:     */   }
/*  347:     */   
/*  348:     */   public void buildClassifier(Instances data)
/*  349:     */     throws Exception
/*  350:     */   {
/*  351: 629 */     if (this.m_KValue > data.numAttributes() - 1) {
/*  352: 630 */       this.m_KValue = (data.numAttributes() - 1);
/*  353:     */     }
/*  354: 632 */     if (this.m_KValue < 1) {
/*  355: 633 */       this.m_KValue = ((int)Utils.log2(data.numAttributes() - 1) + 1);
/*  356:     */     }
/*  357: 637 */     getCapabilities().testWithFail(data);
/*  358:     */     
/*  359:     */ 
/*  360: 640 */     data = new Instances(data);
/*  361: 641 */     data.deleteWithMissingClass();
/*  362: 644 */     if (data.numAttributes() == 1)
/*  363:     */     {
/*  364: 645 */       System.err.println("Cannot build model (only class attribute present in data!), using ZeroR model instead!");
/*  365:     */       
/*  366:     */ 
/*  367: 648 */       this.m_zeroR = new ZeroR();
/*  368: 649 */       this.m_zeroR.buildClassifier(data);
/*  369: 650 */       return;
/*  370:     */     }
/*  371: 652 */     this.m_zeroR = null;
/*  372:     */     
/*  373:     */ 
/*  374:     */ 
/*  375: 656 */     Instances train = null;
/*  376: 657 */     Instances backfit = null;
/*  377: 658 */     Random rand = data.getRandomNumberGenerator(this.m_randomSeed);
/*  378: 659 */     if (this.m_NumFolds <= 0)
/*  379:     */     {
/*  380: 660 */       train = data;
/*  381:     */     }
/*  382:     */     else
/*  383:     */     {
/*  384: 662 */       data.randomize(rand);
/*  385: 663 */       data.stratify(this.m_NumFolds);
/*  386: 664 */       train = data.trainCV(this.m_NumFolds, 1, rand);
/*  387: 665 */       backfit = data.testCV(this.m_NumFolds, 1);
/*  388:     */     }
/*  389: 669 */     int[] attIndicesWindow = new int[data.numAttributes() - 1];
/*  390: 670 */     int j = 0;
/*  391: 671 */     for (int i = 0; i < attIndicesWindow.length; i++)
/*  392:     */     {
/*  393: 672 */       if (j == data.classIndex()) {
/*  394: 673 */         j++;
/*  395:     */       }
/*  396: 675 */       attIndicesWindow[i] = (j++);
/*  397:     */     }
/*  398: 678 */     double totalWeight = 0.0D;
/*  399: 679 */     double totalSumSquared = 0.0D;
/*  400:     */     
/*  401:     */ 
/*  402: 682 */     double[] classProbs = new double[train.numClasses()];
/*  403: 683 */     for (int i = 0; i < train.numInstances(); i++)
/*  404:     */     {
/*  405: 684 */       Instance inst = train.instance(i);
/*  406: 685 */       if (data.classAttribute().isNominal())
/*  407:     */       {
/*  408: 686 */         classProbs[((int)inst.classValue())] += inst.weight();
/*  409: 687 */         totalWeight += inst.weight();
/*  410:     */       }
/*  411:     */       else
/*  412:     */       {
/*  413: 689 */         classProbs[0] += inst.classValue() * inst.weight();
/*  414: 690 */         totalSumSquared += inst.classValue() * inst.classValue() * inst.weight();
/*  415:     */         
/*  416: 692 */         totalWeight += inst.weight();
/*  417:     */       }
/*  418:     */     }
/*  419: 696 */     double trainVariance = 0.0D;
/*  420: 697 */     if (data.classAttribute().isNumeric())
/*  421:     */     {
/*  422: 698 */       trainVariance = singleVariance(classProbs[0], totalSumSquared, totalWeight) / totalWeight;
/*  423:     */       
/*  424: 700 */       classProbs[0] /= totalWeight;
/*  425:     */     }
/*  426: 704 */     this.m_Tree = new Tree();
/*  427: 705 */     this.m_Info = new Instances(data, 0);
/*  428: 706 */     this.m_Tree.buildTree(train, classProbs, attIndicesWindow, totalWeight, rand, 0, this.m_MinVarianceProp * trainVariance);
/*  429: 710 */     if (backfit != null) {
/*  430: 711 */       this.m_Tree.backfitData(backfit);
/*  431:     */     }
/*  432:     */   }
/*  433:     */   
/*  434:     */   public double[] distributionForInstance(Instance instance)
/*  435:     */     throws Exception
/*  436:     */   {
/*  437: 725 */     if (this.m_zeroR != null) {
/*  438: 726 */       return this.m_zeroR.distributionForInstance(instance);
/*  439:     */     }
/*  440: 728 */     return this.m_Tree.distributionForInstance(instance);
/*  441:     */   }
/*  442:     */   
/*  443:     */   public String toString()
/*  444:     */   {
/*  445: 741 */     if (this.m_zeroR != null)
/*  446:     */     {
/*  447: 742 */       StringBuffer buf = new StringBuffer();
/*  448: 743 */       buf.append(getClass().getName().replaceAll(".*\\.", "") + "\n");
/*  449: 744 */       buf.append(getClass().getName().replaceAll(".*\\.", "").replaceAll(".", "=") + "\n\n");
/*  450:     */       
/*  451:     */ 
/*  452: 747 */       buf.append("Warning: No model could be built, hence ZeroR model is used:\n\n");
/*  453:     */       
/*  454: 749 */       buf.append(this.m_zeroR.toString());
/*  455: 750 */       return buf.toString();
/*  456:     */     }
/*  457: 753 */     if (this.m_Tree == null) {
/*  458: 754 */       return "RandomTree: no model has been built yet.";
/*  459:     */     }
/*  460: 756 */     return "\nRandomTree\n==========\n" + this.m_Tree.toString(0) + "\n" + "\nSize of the tree : " + this.m_Tree.numNodes() + (getMaxDepth() > 0 ? "\nMax depth of tree: " + getMaxDepth() : "");
/*  461:     */   }
/*  462:     */   
/*  463:     */   public String graph()
/*  464:     */     throws Exception
/*  465:     */   {
/*  466: 774 */     if (this.m_Tree == null) {
/*  467: 775 */       throw new Exception("RandomTree: No model built yet.");
/*  468:     */     }
/*  469: 777 */     StringBuffer resultBuff = new StringBuffer();
/*  470: 778 */     this.m_Tree.toGraph(resultBuff, 0, null);
/*  471: 779 */     String result = "digraph RandomTree {\nedge [style=bold]\n" + resultBuff.toString() + "\n}\n";
/*  472:     */     
/*  473: 781 */     return result;
/*  474:     */   }
/*  475:     */   
/*  476:     */   public int graphType()
/*  477:     */   {
/*  478: 791 */     return 1;
/*  479:     */   }
/*  480:     */   
/*  481:     */   public void generatePartition(Instances data)
/*  482:     */     throws Exception
/*  483:     */   {
/*  484: 800 */     buildClassifier(data);
/*  485:     */   }
/*  486:     */   
/*  487:     */   public double[] getMembershipValues(Instance instance)
/*  488:     */     throws Exception
/*  489:     */   {
/*  490: 810 */     if (this.m_zeroR != null)
/*  491:     */     {
/*  492: 811 */       double[] m = new double[1];
/*  493: 812 */       m[0] = instance.weight();
/*  494: 813 */       return m;
/*  495:     */     }
/*  496: 817 */     double[] a = new double[numElements()];
/*  497:     */     
/*  498:     */ 
/*  499: 820 */     Queue<Double> queueOfWeights = new LinkedList();
/*  500: 821 */     Queue<Tree> queueOfNodes = new LinkedList();
/*  501: 822 */     queueOfWeights.add(Double.valueOf(instance.weight()));
/*  502: 823 */     queueOfNodes.add(this.m_Tree);
/*  503: 824 */     int index = 0;
/*  504: 827 */     while (!queueOfNodes.isEmpty())
/*  505:     */     {
/*  506: 829 */       a[(index++)] = ((Double)queueOfWeights.poll()).doubleValue();
/*  507: 830 */       Tree node = (Tree)queueOfNodes.poll();
/*  508: 833 */       if (node.m_Attribute > -1)
/*  509:     */       {
/*  510: 838 */         double[] weights = new double[node.m_Successors.length];
/*  511: 839 */         if (instance.isMissing(node.m_Attribute)) {
/*  512: 840 */           System.arraycopy(node.m_Prop, 0, weights, 0, node.m_Prop.length);
/*  513: 841 */         } else if (this.m_Info.attribute(node.m_Attribute).isNominal()) {
/*  514: 842 */           weights[((int)instance.value(node.m_Attribute))] = 1.0D;
/*  515: 844 */         } else if (instance.value(node.m_Attribute) < node.m_SplitPoint) {
/*  516: 845 */           weights[0] = 1.0D;
/*  517:     */         } else {
/*  518: 847 */           weights[1] = 1.0D;
/*  519:     */         }
/*  520: 850 */         for (int i = 0; i < node.m_Successors.length; i++)
/*  521:     */         {
/*  522: 851 */           queueOfNodes.add(node.m_Successors[i]);
/*  523: 852 */           queueOfWeights.add(Double.valueOf(a[(index - 1)] * weights[i]));
/*  524:     */         }
/*  525:     */       }
/*  526:     */     }
/*  527: 855 */     return a;
/*  528:     */   }
/*  529:     */   
/*  530:     */   public int numElements()
/*  531:     */     throws Exception
/*  532:     */   {
/*  533: 865 */     if (this.m_zeroR != null) {
/*  534: 866 */       return 1;
/*  535:     */     }
/*  536: 868 */     return this.m_Tree.numNodes();
/*  537:     */   }
/*  538:     */   
/*  539:     */   protected class Tree
/*  540:     */     implements Serializable
/*  541:     */   {
/*  542:     */     private static final long serialVersionUID = 3549573538656522569L;
/*  543:     */     protected Tree[] m_Successors;
/*  544: 883 */     protected int m_Attribute = -1;
/*  545: 886 */     protected double m_SplitPoint = (0.0D / 0.0D);
/*  546: 889 */     protected double[] m_Prop = null;
/*  547: 895 */     protected double[] m_ClassDistribution = null;
/*  548: 900 */     protected double[] m_Distribution = null;
/*  549:     */     
/*  550:     */     protected Tree() {}
/*  551:     */     
/*  552:     */     public void backfitData(Instances data)
/*  553:     */       throws Exception
/*  554:     */     {
/*  555: 907 */       double totalWeight = 0.0D;
/*  556: 908 */       double totalSumSquared = 0.0D;
/*  557:     */       
/*  558:     */ 
/*  559: 911 */       double[] classProbs = new double[data.numClasses()];
/*  560: 912 */       for (int i = 0; i < data.numInstances(); i++)
/*  561:     */       {
/*  562: 913 */         Instance inst = data.instance(i);
/*  563: 914 */         if (data.classAttribute().isNominal())
/*  564:     */         {
/*  565: 915 */           classProbs[((int)inst.classValue())] += inst.weight();
/*  566: 916 */           totalWeight += inst.weight();
/*  567:     */         }
/*  568:     */         else
/*  569:     */         {
/*  570: 918 */           classProbs[0] += inst.classValue() * inst.weight();
/*  571: 919 */           totalSumSquared += inst.classValue() * inst.classValue() * inst.weight();
/*  572:     */           
/*  573: 921 */           totalWeight += inst.weight();
/*  574:     */         }
/*  575:     */       }
/*  576: 925 */       double trainVariance = 0.0D;
/*  577: 926 */       if (data.classAttribute().isNumeric())
/*  578:     */       {
/*  579: 927 */         trainVariance = RandomTree.singleVariance(classProbs[0], totalSumSquared, totalWeight) / totalWeight;
/*  580:     */         
/*  581: 929 */         classProbs[0] /= totalWeight;
/*  582:     */       }
/*  583: 933 */       backfitData(data, classProbs, totalWeight);
/*  584:     */     }
/*  585:     */     
/*  586:     */     public double[] distributionForInstance(Instance instance)
/*  587:     */       throws Exception
/*  588:     */     {
/*  589: 945 */       double[] returnedDist = null;
/*  590: 947 */       if (this.m_Attribute > -1) {
/*  591: 950 */         if (instance.isMissing(this.m_Attribute))
/*  592:     */         {
/*  593: 953 */           returnedDist = new double[RandomTree.this.m_Info.numClasses()];
/*  594: 956 */           for (int i = 0; i < this.m_Successors.length; i++)
/*  595:     */           {
/*  596: 957 */             double[] help = this.m_Successors[i].distributionForInstance(instance);
/*  597: 958 */             if (help != null) {
/*  598: 959 */               for (int j = 0; j < help.length; j++) {
/*  599: 960 */                 returnedDist[j] += this.m_Prop[i] * help[j];
/*  600:     */               }
/*  601:     */             }
/*  602:     */           }
/*  603:     */         }
/*  604: 964 */         else if (RandomTree.this.m_Info.attribute(this.m_Attribute).isNominal())
/*  605:     */         {
/*  606: 967 */           returnedDist = this.m_Successors[((int)instance.value(this.m_Attribute))].distributionForInstance(instance);
/*  607:     */         }
/*  608: 972 */         else if (instance.value(this.m_Attribute) < this.m_SplitPoint)
/*  609:     */         {
/*  610: 973 */           returnedDist = this.m_Successors[0].distributionForInstance(instance);
/*  611:     */         }
/*  612:     */         else
/*  613:     */         {
/*  614: 975 */           returnedDist = this.m_Successors[1].distributionForInstance(instance);
/*  615:     */         }
/*  616:     */       }
/*  617: 981 */       if ((this.m_Attribute == -1) || (returnedDist == null))
/*  618:     */       {
/*  619: 984 */         if (this.m_ClassDistribution == null)
/*  620:     */         {
/*  621: 985 */           if (RandomTree.this.getAllowUnclassifiedInstances())
/*  622:     */           {
/*  623: 986 */             double[] result = new double[RandomTree.this.m_Info.numClasses()];
/*  624: 987 */             if (RandomTree.this.m_Info.classAttribute().isNumeric()) {
/*  625: 988 */               result[0] = Utils.missingValue();
/*  626:     */             }
/*  627: 990 */             return result;
/*  628:     */           }
/*  629: 992 */           return null;
/*  630:     */         }
/*  631: 997 */         double[] normalizedDistribution = (double[])this.m_ClassDistribution.clone();
/*  632: 998 */         if (RandomTree.this.m_Info.classAttribute().isNominal()) {
/*  633: 999 */           Utils.normalize(normalizedDistribution);
/*  634:     */         }
/*  635:1001 */         return normalizedDistribution;
/*  636:     */       }
/*  637:1003 */       return returnedDist;
/*  638:     */     }
/*  639:     */     
/*  640:     */     public int toGraph(StringBuffer text, int num)
/*  641:     */       throws Exception
/*  642:     */     {
/*  643:1017 */       int maxIndex = Utils.maxIndex(this.m_ClassDistribution);
/*  644:1018 */       String classValue = RandomTree.this.m_Info.classAttribute().isNominal() ? RandomTree.this.m_Info.classAttribute().value(maxIndex) : Utils.doubleToString(this.m_ClassDistribution[0], 2);
/*  645:     */       
/*  646:     */ 
/*  647:     */ 
/*  648:1022 */       num++;
/*  649:1023 */       if (this.m_Attribute == -1)
/*  650:     */       {
/*  651:1024 */         text.append("N" + Integer.toHexString(hashCode()) + " [label=\"" + num + ": " + classValue + "\"" + "shape=box]\n");
/*  652:     */       }
/*  653:     */       else
/*  654:     */       {
/*  655:1027 */         text.append("N" + Integer.toHexString(hashCode()) + " [label=\"" + num + ": " + classValue + "\"]\n");
/*  656:1029 */         for (int i = 0; i < this.m_Successors.length; i++)
/*  657:     */         {
/*  658:1030 */           text.append("N" + Integer.toHexString(hashCode()) + "->" + "N" + Integer.toHexString(this.m_Successors[i].hashCode()) + " [label=\"" + RandomTree.this.m_Info.attribute(this.m_Attribute).name());
/*  659:1033 */           if (RandomTree.this.m_Info.attribute(this.m_Attribute).isNumeric())
/*  660:     */           {
/*  661:1034 */             if (i == 0) {
/*  662:1035 */               text.append(" < " + Utils.doubleToString(this.m_SplitPoint, 2));
/*  663:     */             } else {
/*  664:1037 */               text.append(" >= " + Utils.doubleToString(this.m_SplitPoint, 2));
/*  665:     */             }
/*  666:     */           }
/*  667:     */           else {
/*  668:1040 */             text.append(" = " + RandomTree.this.m_Info.attribute(this.m_Attribute).value(i));
/*  669:     */           }
/*  670:1042 */           text.append("\"]\n");
/*  671:1043 */           num = this.m_Successors[i].toGraph(text, num);
/*  672:     */         }
/*  673:     */       }
/*  674:1047 */       return num;
/*  675:     */     }
/*  676:     */     
/*  677:     */     protected String leafString()
/*  678:     */       throws Exception
/*  679:     */     {
/*  680:1058 */       double sum = 0.0D;double maxCount = 0.0D;
/*  681:1059 */       int maxIndex = 0;
/*  682:1060 */       double classMean = 0.0D;
/*  683:1061 */       double avgError = 0.0D;
/*  684:1062 */       if (this.m_ClassDistribution != null) {
/*  685:1063 */         if (RandomTree.this.m_Info.classAttribute().isNominal())
/*  686:     */         {
/*  687:1064 */           sum = Utils.sum(this.m_ClassDistribution);
/*  688:1065 */           maxIndex = Utils.maxIndex(this.m_ClassDistribution);
/*  689:1066 */           maxCount = this.m_ClassDistribution[maxIndex];
/*  690:     */         }
/*  691:     */         else
/*  692:     */         {
/*  693:1068 */           classMean = this.m_ClassDistribution[0];
/*  694:1069 */           if (this.m_Distribution[1] > 0.0D) {
/*  695:1070 */             avgError = this.m_Distribution[0] / this.m_Distribution[1];
/*  696:     */           }
/*  697:     */         }
/*  698:     */       }
/*  699:1075 */       if (RandomTree.this.m_Info.classAttribute().isNumeric()) {
/*  700:1076 */         return " : " + Utils.doubleToString(classMean, 2) + " (" + Utils.doubleToString(this.m_Distribution[1], 2) + "/" + Utils.doubleToString(avgError, 2) + ")";
/*  701:     */       }
/*  702:1081 */       return " : " + RandomTree.this.m_Info.classAttribute().value(maxIndex) + " (" + Utils.doubleToString(sum, 2) + "/" + Utils.doubleToString(sum - maxCount, 2) + ")";
/*  703:     */     }
/*  704:     */     
/*  705:     */     protected String toString(int level)
/*  706:     */     {
/*  707:     */       try
/*  708:     */       {
/*  709:1095 */         StringBuffer text = new StringBuffer();
/*  710:1097 */         if (this.m_Attribute == -1) {
/*  711:1100 */           return leafString();
/*  712:     */         }
/*  713:1101 */         if (RandomTree.this.m_Info.attribute(this.m_Attribute).isNominal())
/*  714:     */         {
/*  715:1104 */           for (int i = 0; i < this.m_Successors.length; i++)
/*  716:     */           {
/*  717:1105 */             text.append("\n");
/*  718:1106 */             for (int j = 0; j < level; j++) {
/*  719:1107 */               text.append("|   ");
/*  720:     */             }
/*  721:1109 */             text.append(RandomTree.this.m_Info.attribute(this.m_Attribute).name() + " = " + RandomTree.this.m_Info.attribute(this.m_Attribute).value(i));
/*  722:     */             
/*  723:1111 */             text.append(this.m_Successors[i].toString(level + 1));
/*  724:     */           }
/*  725:     */         }
/*  726:     */         else
/*  727:     */         {
/*  728:1116 */           text.append("\n");
/*  729:1117 */           for (int j = 0; j < level; j++) {
/*  730:1118 */             text.append("|   ");
/*  731:     */           }
/*  732:1120 */           text.append(RandomTree.this.m_Info.attribute(this.m_Attribute).name() + " < " + Utils.doubleToString(this.m_SplitPoint, 2));
/*  733:     */           
/*  734:1122 */           text.append(this.m_Successors[0].toString(level + 1));
/*  735:1123 */           text.append("\n");
/*  736:1124 */           for (int j = 0; j < level; j++) {
/*  737:1125 */             text.append("|   ");
/*  738:     */           }
/*  739:1127 */           text.append(RandomTree.this.m_Info.attribute(this.m_Attribute).name() + " >= " + Utils.doubleToString(this.m_SplitPoint, 2));
/*  740:     */           
/*  741:1129 */           text.append(this.m_Successors[1].toString(level + 1));
/*  742:     */         }
/*  743:1132 */         return text.toString();
/*  744:     */       }
/*  745:     */       catch (Exception e)
/*  746:     */       {
/*  747:1134 */         e.printStackTrace();
/*  748:     */       }
/*  749:1135 */       return "RandomTree: tree can't be printed";
/*  750:     */     }
/*  751:     */     
/*  752:     */     protected void backfitData(Instances data, double[] classProbs, double totalWeight)
/*  753:     */       throws Exception
/*  754:     */     {
/*  755:1150 */       if (data.numInstances() == 0)
/*  756:     */       {
/*  757:1151 */         this.m_Attribute = -1;
/*  758:1152 */         this.m_ClassDistribution = null;
/*  759:1153 */         if (data.classAttribute().isNumeric()) {
/*  760:1154 */           this.m_Distribution = new double[2];
/*  761:     */         }
/*  762:1156 */         this.m_Prop = null;
/*  763:1157 */         return;
/*  764:     */       }
/*  765:1160 */       double priorVar = 0.0D;
/*  766:1161 */       if (data.classAttribute().isNumeric())
/*  767:     */       {
/*  768:1164 */         double totalSum = 0.0D;double totalSumSquared = 0.0D;double totalSumOfWeights = 0.0D;
/*  769:1165 */         for (int i = 0; i < data.numInstances(); i++)
/*  770:     */         {
/*  771:1166 */           Instance inst = data.instance(i);
/*  772:1167 */           totalSum += inst.classValue() * inst.weight();
/*  773:1168 */           totalSumSquared += inst.classValue() * inst.classValue() * inst.weight();
/*  774:     */           
/*  775:1170 */           totalSumOfWeights += inst.weight();
/*  776:     */         }
/*  777:1172 */         priorVar = RandomTree.singleVariance(totalSum, totalSumSquared, totalSumOfWeights);
/*  778:     */       }
/*  779:1178 */       this.m_ClassDistribution = ((double[])classProbs.clone());
/*  780:1189 */       if (this.m_Attribute > -1)
/*  781:     */       {
/*  782:1192 */         this.m_Prop = new double[this.m_Successors.length];
/*  783:1193 */         for (int i = 0; i < data.numInstances(); i++)
/*  784:     */         {
/*  785:1194 */           Instance inst = data.instance(i);
/*  786:1195 */           if (!inst.isMissing(this.m_Attribute)) {
/*  787:1196 */             if (data.attribute(this.m_Attribute).isNominal()) {
/*  788:1197 */               this.m_Prop[((int)inst.value(this.m_Attribute))] += inst.weight();
/*  789:     */             } else {
/*  790:1199 */               this.m_Prop[(inst.value(this.m_Attribute) < this.m_SplitPoint ? 0 : 1)] += inst.weight();
/*  791:     */             }
/*  792:     */           }
/*  793:     */         }
/*  794:1206 */         if (Utils.sum(this.m_Prop) <= 0.0D)
/*  795:     */         {
/*  796:1207 */           this.m_Attribute = -1;
/*  797:1208 */           this.m_Prop = null;
/*  798:1210 */           if (data.classAttribute().isNumeric())
/*  799:     */           {
/*  800:1211 */             this.m_Distribution = new double[2];
/*  801:1212 */             this.m_Distribution[0] = priorVar;
/*  802:1213 */             this.m_Distribution[1] = totalWeight;
/*  803:     */           }
/*  804:1216 */           return;
/*  805:     */         }
/*  806:1220 */         Utils.normalize(this.m_Prop);
/*  807:     */         
/*  808:     */ 
/*  809:1223 */         Instances[] subsets = splitData(data);
/*  810:1226 */         for (int i = 0; i < subsets.length; i++)
/*  811:     */         {
/*  812:1229 */           double[] dist = new double[data.numClasses()];
/*  813:1230 */           double sumOfWeights = 0.0D;
/*  814:1231 */           for (int j = 0; j < subsets[i].numInstances(); j++) {
/*  815:1232 */             if (data.classAttribute().isNominal())
/*  816:     */             {
/*  817:1233 */               dist[((int)subsets[i].instance(j).classValue())] += subsets[i].instance(j).weight();
/*  818:     */             }
/*  819:     */             else
/*  820:     */             {
/*  821:1236 */               dist[0] += subsets[i].instance(j).classValue() * subsets[i].instance(j).weight();
/*  822:     */               
/*  823:1238 */               sumOfWeights += subsets[i].instance(j).weight();
/*  824:     */             }
/*  825:     */           }
/*  826:1242 */           if (sumOfWeights > 0.0D) {
/*  827:1243 */             dist[0] /= sumOfWeights;
/*  828:     */           }
/*  829:1247 */           this.m_Successors[i].backfitData(subsets[i], dist, totalWeight);
/*  830:     */         }
/*  831:1252 */         if (RandomTree.this.getAllowUnclassifiedInstances())
/*  832:     */         {
/*  833:1253 */           this.m_ClassDistribution = null;
/*  834:1254 */           return;
/*  835:     */         }
/*  836:1257 */         for (int i = 0; i < subsets.length; i++) {
/*  837:1258 */           if (this.m_Successors[i].m_ClassDistribution == null) {
/*  838:1259 */             return;
/*  839:     */           }
/*  840:     */         }
/*  841:1262 */         this.m_ClassDistribution = null;
/*  842:     */       }
/*  843:     */     }
/*  844:     */     
/*  845:     */     protected void buildTree(Instances data, double[] classProbs, int[] attIndicesWindow, double totalWeight, Random random, int depth, double minVariance)
/*  846:     */       throws Exception
/*  847:     */     {
/*  848:1291 */       if (data.numInstances() == 0)
/*  849:     */       {
/*  850:1292 */         this.m_Attribute = -1;
/*  851:1293 */         this.m_ClassDistribution = null;
/*  852:1294 */         this.m_Prop = null;
/*  853:1296 */         if (data.classAttribute().isNumeric()) {
/*  854:1297 */           this.m_Distribution = new double[2];
/*  855:     */         }
/*  856:1299 */         return;
/*  857:     */       }
/*  858:1302 */       double priorVar = 0.0D;
/*  859:1303 */       if (data.classAttribute().isNumeric())
/*  860:     */       {
/*  861:1306 */         double totalSum = 0.0D;double totalSumSquared = 0.0D;double totalSumOfWeights = 0.0D;
/*  862:1307 */         for (int i = 0; i < data.numInstances(); i++)
/*  863:     */         {
/*  864:1308 */           Instance inst = data.instance(i);
/*  865:1309 */           totalSum += inst.classValue() * inst.weight();
/*  866:1310 */           totalSumSquared += inst.classValue() * inst.classValue() * inst.weight();
/*  867:     */           
/*  868:1312 */           totalSumOfWeights += inst.weight();
/*  869:     */         }
/*  870:1314 */         priorVar = RandomTree.singleVariance(totalSum, totalSumSquared, totalSumOfWeights);
/*  871:     */       }
/*  872:1320 */       if (data.classAttribute().isNominal()) {
/*  873:1321 */         totalWeight = Utils.sum(classProbs);
/*  874:     */       }
/*  875:1325 */       if ((totalWeight < 2.0D * RandomTree.this.m_MinNum) || ((data.classAttribute().isNominal()) && (Utils.eq(classProbs[Utils.maxIndex(classProbs)], Utils.sum(classProbs)))) || ((data.classAttribute().isNumeric()) && (priorVar / totalWeight < minVariance)) || ((RandomTree.this.getMaxDepth() > 0) && (depth >= RandomTree.this.getMaxDepth())))
/*  876:     */       {
/*  877:1342 */         this.m_Attribute = -1;
/*  878:1343 */         this.m_ClassDistribution = ((double[])classProbs.clone());
/*  879:1344 */         if (data.classAttribute().isNumeric())
/*  880:     */         {
/*  881:1345 */           this.m_Distribution = new double[2];
/*  882:1346 */           this.m_Distribution[0] = priorVar;
/*  883:1347 */           this.m_Distribution[1] = totalWeight;
/*  884:     */         }
/*  885:1350 */         this.m_Prop = null;
/*  886:1351 */         return;
/*  887:     */       }
/*  888:1356 */       double val = -1.797693134862316E+308D;
/*  889:1357 */       double split = -1.797693134862316E+308D;
/*  890:1358 */       double[][] bestDists = (double[][])null;
/*  891:1359 */       double[] bestProps = null;
/*  892:1360 */       int bestIndex = 0;
/*  893:     */       
/*  894:     */ 
/*  895:1363 */       double[][] props = new double[1][0];
/*  896:1364 */       double[][][] dists = new double[1][0][0];
/*  897:1365 */       double[][] totalSubsetWeights = new double[data.numAttributes()][0];
/*  898:     */       
/*  899:     */ 
/*  900:1368 */       int attIndex = 0;
/*  901:1369 */       int windowSize = attIndicesWindow.length;
/*  902:1370 */       int k = RandomTree.this.m_KValue;
/*  903:1371 */       boolean gainFound = false;
/*  904:1372 */       double[] tempNumericVals = new double[data.numAttributes()];
/*  905:1373 */       while ((windowSize > 0) && ((k-- > 0) || (!gainFound)))
/*  906:     */       {
/*  907:1375 */         int chosenIndex = random.nextInt(windowSize);
/*  908:1376 */         attIndex = attIndicesWindow[chosenIndex];
/*  909:     */         
/*  910:     */ 
/*  911:1379 */         attIndicesWindow[chosenIndex] = attIndicesWindow[(windowSize - 1)];
/*  912:1380 */         attIndicesWindow[(windowSize - 1)] = attIndex;
/*  913:1381 */         windowSize--;
/*  914:     */         
/*  915:1383 */         double currSplit = data.classAttribute().isNominal() ? distribution(props, dists, attIndex, data) : numericDistribution(props, dists, attIndex, totalSubsetWeights, data, tempNumericVals);
/*  916:     */         
/*  917:     */ 
/*  918:     */ 
/*  919:1387 */         double currVal = data.classAttribute().isNominal() ? gain(dists[0], priorVal(dists[0])) : tempNumericVals[attIndex];
/*  920:1390 */         if (Utils.gr(currVal, 0.0D)) {
/*  921:1391 */           gainFound = true;
/*  922:     */         }
/*  923:1394 */         if ((currVal > val) || ((!RandomTree.this.getBreakTiesRandomly()) && (currVal == val) && (attIndex < bestIndex)))
/*  924:     */         {
/*  925:1395 */           val = currVal;
/*  926:1396 */           bestIndex = attIndex;
/*  927:1397 */           split = currSplit;
/*  928:1398 */           bestProps = props[0];
/*  929:1399 */           bestDists = dists[0];
/*  930:     */         }
/*  931:     */       }
/*  932:1404 */       this.m_Attribute = bestIndex;
/*  933:1407 */       if (Utils.gr(val, 0.0D))
/*  934:     */       {
/*  935:1410 */         this.m_SplitPoint = split;
/*  936:1411 */         this.m_Prop = bestProps;
/*  937:1412 */         Instances[] subsets = splitData(data);
/*  938:1413 */         this.m_Successors = new Tree[bestDists.length];
/*  939:1414 */         double[] attTotalSubsetWeights = totalSubsetWeights[bestIndex];
/*  940:1416 */         for (int i = 0; i < bestDists.length; i++)
/*  941:     */         {
/*  942:1417 */           this.m_Successors[i] = new Tree(RandomTree.this);
/*  943:1418 */           this.m_Successors[i].buildTree(subsets[i], bestDists[i], attIndicesWindow, data.classAttribute().isNominal() ? 0.0D : attTotalSubsetWeights[i], random, depth + 1, minVariance);
/*  944:     */         }
/*  945:1425 */         boolean emptySuccessor = false;
/*  946:1426 */         for (int i = 0; i < subsets.length; i++) {
/*  947:1427 */           if (this.m_Successors[i].m_ClassDistribution == null)
/*  948:     */           {
/*  949:1428 */             emptySuccessor = true;
/*  950:1429 */             break;
/*  951:     */           }
/*  952:     */         }
/*  953:1432 */         if (emptySuccessor) {
/*  954:1433 */           this.m_ClassDistribution = ((double[])classProbs.clone());
/*  955:     */         }
/*  956:     */       }
/*  957:     */       else
/*  958:     */       {
/*  959:1438 */         this.m_Attribute = -1;
/*  960:1439 */         this.m_ClassDistribution = ((double[])classProbs.clone());
/*  961:1440 */         if (data.classAttribute().isNumeric())
/*  962:     */         {
/*  963:1441 */           this.m_Distribution = new double[2];
/*  964:1442 */           this.m_Distribution[0] = priorVar;
/*  965:1443 */           this.m_Distribution[1] = totalWeight;
/*  966:     */         }
/*  967:     */       }
/*  968:     */     }
/*  969:     */     
/*  970:     */     public int numNodes()
/*  971:     */     {
/*  972:1455 */       if (this.m_Attribute == -1) {
/*  973:1456 */         return 1;
/*  974:     */       }
/*  975:1458 */       int size = 1;
/*  976:1459 */       for (Tree m_Successor : this.m_Successors) {
/*  977:1460 */         size += m_Successor.numNodes();
/*  978:     */       }
/*  979:1462 */       return size;
/*  980:     */     }
/*  981:     */     
/*  982:     */     protected Instances[] splitData(Instances data)
/*  983:     */       throws Exception
/*  984:     */     {
/*  985:1476 */       Instances[] subsets = new Instances[this.m_Prop.length];
/*  986:1477 */       for (int i = 0; i < this.m_Prop.length; i++) {
/*  987:1478 */         subsets[i] = new Instances(data, data.numInstances());
/*  988:     */       }
/*  989:1482 */       for (int i = 0; i < data.numInstances(); i++)
/*  990:     */       {
/*  991:1485 */         Instance inst = data.instance(i);
/*  992:1488 */         if (inst.isMissing(this.m_Attribute)) {
/*  993:1491 */           for (int k = 0; k < this.m_Prop.length; k++) {
/*  994:1492 */             if (this.m_Prop[k] > 0.0D)
/*  995:     */             {
/*  996:1493 */               Instance copy = (Instance)inst.copy();
/*  997:1494 */               copy.setWeight(this.m_Prop[k] * inst.weight());
/*  998:1495 */               subsets[k].add(copy);
/*  999:     */             }
/* 1000:     */           }
/* 1001:1504 */         } else if (data.attribute(this.m_Attribute).isNominal()) {
/* 1002:1505 */           subsets[((int)inst.value(this.m_Attribute))].add(inst);
/* 1003:1512 */         } else if (data.attribute(this.m_Attribute).isNumeric()) {
/* 1004:1513 */           subsets[1].add(inst);
/* 1005:     */         } else {
/* 1006:1520 */           throw new IllegalArgumentException("Unknown attribute type");
/* 1007:     */         }
/* 1008:     */       }
/* 1009:1524 */       for (int i = 0; i < this.m_Prop.length; i++) {
/* 1010:1525 */         subsets[i].compactify();
/* 1011:     */       }
/* 1012:1529 */       return subsets;
/* 1013:     */     }
/* 1014:     */     
/* 1015:     */     protected double numericDistribution(double[][] props, double[][][] dists, int att, double[][] subsetWeights, Instances data, double[] vals)
/* 1016:     */       throws Exception
/* 1017:     */     {
/* 1018:1548 */       double splitPoint = (0.0D / 0.0D);
/* 1019:1549 */       Attribute attribute = data.attribute(att);
/* 1020:1550 */       double[][] dist = (double[][])null;
/* 1021:1551 */       double[] sums = null;
/* 1022:1552 */       double[] sumSquared = null;
/* 1023:1553 */       double[] sumOfWeights = null;
/* 1024:1554 */       double totalSum = 0.0D;double totalSumSquared = 0.0D;double totalSumOfWeights = 0.0D;
/* 1025:1555 */       int indexOfFirstMissingValue = data.numInstances();
/* 1026:1557 */       if (attribute.isNominal())
/* 1027:     */       {
/* 1028:1558 */         sums = new double[attribute.numValues()];
/* 1029:1559 */         sumSquared = new double[attribute.numValues()];
/* 1030:1560 */         sumOfWeights = new double[attribute.numValues()];
/* 1031:1563 */         for (int i = 0; i < data.numInstances(); i++)
/* 1032:     */         {
/* 1033:1564 */           Instance inst = data.instance(i);
/* 1034:1565 */           if (inst.isMissing(att))
/* 1035:     */           {
/* 1036:1568 */             if (indexOfFirstMissingValue == data.numInstances()) {
/* 1037:1569 */               indexOfFirstMissingValue = i;
/* 1038:     */             }
/* 1039:     */           }
/* 1040:     */           else
/* 1041:     */           {
/* 1042:1574 */             int attVal = (int)inst.value(att);
/* 1043:1575 */             sums[attVal] += inst.classValue() * inst.weight();
/* 1044:1576 */             sumSquared[attVal] += inst.classValue() * inst.classValue() * inst.weight();
/* 1045:     */             
/* 1046:1578 */             sumOfWeights[attVal] += inst.weight();
/* 1047:     */           }
/* 1048:     */         }
/* 1049:1581 */         totalSum = Utils.sum(sums);
/* 1050:1582 */         totalSumSquared = Utils.sum(sumSquared);
/* 1051:1583 */         totalSumOfWeights = Utils.sum(sumOfWeights);
/* 1052:     */       }
/* 1053:     */       else
/* 1054:     */       {
/* 1055:1586 */         sums = new double[2];
/* 1056:1587 */         sumSquared = new double[2];
/* 1057:1588 */         sumOfWeights = new double[2];
/* 1058:1589 */         double[] currSums = new double[2];
/* 1059:1590 */         double[] currSumSquared = new double[2];
/* 1060:1591 */         double[] currSumOfWeights = new double[2];
/* 1061:     */         
/* 1062:     */ 
/* 1063:1594 */         data.sort(att);
/* 1064:1597 */         for (int j = 0; j < data.numInstances(); j++)
/* 1065:     */         {
/* 1066:1598 */           Instance inst = data.instance(j);
/* 1067:1599 */           if (inst.isMissing(att))
/* 1068:     */           {
/* 1069:1602 */             indexOfFirstMissingValue = j;
/* 1070:1603 */             break;
/* 1071:     */           }
/* 1072:1606 */           currSums[1] += inst.classValue() * inst.weight();
/* 1073:1607 */           currSumSquared[1] += inst.classValue() * inst.classValue() * inst.weight();
/* 1074:     */           
/* 1075:1609 */           currSumOfWeights[1] += inst.weight();
/* 1076:     */         }
/* 1077:1612 */         totalSum = currSums[1];
/* 1078:1613 */         totalSumSquared = currSumSquared[1];
/* 1079:1614 */         totalSumOfWeights = currSumOfWeights[1];
/* 1080:     */         
/* 1081:1616 */         sums[1] = currSums[1];
/* 1082:1617 */         sumSquared[1] = currSumSquared[1];
/* 1083:1618 */         sumOfWeights[1] = currSumOfWeights[1];
/* 1084:     */         
/* 1085:     */ 
/* 1086:1621 */         double currSplit = data.instance(0).value(att);
/* 1087:1622 */         double bestVal = 1.7976931348623157E+308D;
/* 1088:1624 */         for (int i = 0; i < indexOfFirstMissingValue; i++)
/* 1089:     */         {
/* 1090:1625 */           Instance inst = data.instance(i);
/* 1091:1627 */           if (inst.value(att) > currSplit)
/* 1092:     */           {
/* 1093:1628 */             double currVal = RandomTree.variance(currSums, currSumSquared, currSumOfWeights);
/* 1094:1630 */             if (currVal < bestVal)
/* 1095:     */             {
/* 1096:1631 */               bestVal = currVal;
/* 1097:1632 */               splitPoint = (inst.value(att) + currSplit) / 2.0D;
/* 1098:1635 */               if (splitPoint <= currSplit) {
/* 1099:1636 */                 splitPoint = inst.value(att);
/* 1100:     */               }
/* 1101:1639 */               for (int j = 0; j < 2; j++)
/* 1102:     */               {
/* 1103:1640 */                 sums[j] = currSums[j];
/* 1104:1641 */                 sumSquared[j] = currSumSquared[j];
/* 1105:1642 */                 sumOfWeights[j] = currSumOfWeights[j];
/* 1106:     */               }
/* 1107:     */             }
/* 1108:     */           }
/* 1109:1647 */           currSplit = inst.value(att);
/* 1110:     */           
/* 1111:1649 */           double classVal = inst.classValue() * inst.weight();
/* 1112:1650 */           double classValSquared = inst.classValue() * classVal;
/* 1113:     */           
/* 1114:1652 */           currSums[0] += classVal;
/* 1115:1653 */           currSumSquared[0] += classValSquared;
/* 1116:1654 */           currSumOfWeights[0] += inst.weight();
/* 1117:     */           
/* 1118:1656 */           currSums[1] -= classVal;
/* 1119:1657 */           currSumSquared[1] -= classValSquared;
/* 1120:1658 */           currSumOfWeights[1] -= inst.weight();
/* 1121:     */         }
/* 1122:     */       }
/* 1123:1663 */       props[0] = new double[sums.length];
/* 1124:1664 */       for (int k = 0; k < props[0].length; k++) {
/* 1125:1665 */         props[0][k] = sumOfWeights[k];
/* 1126:     */       }
/* 1127:1667 */       if (Utils.sum(props[0]) <= 0.0D) {
/* 1128:1668 */         for (int k = 0; k < props[0].length; k++) {
/* 1129:1669 */           props[0][k] = (1.0D / props[0].length);
/* 1130:     */         }
/* 1131:     */       } else {
/* 1132:1672 */         Utils.normalize(props[0]);
/* 1133:     */       }
/* 1134:1676 */       for (int i = indexOfFirstMissingValue; i < data.numInstances(); i++)
/* 1135:     */       {
/* 1136:1677 */         Instance inst = data.instance(i);
/* 1137:1679 */         for (int j = 0; j < sums.length; j++)
/* 1138:     */         {
/* 1139:1680 */           sums[j] += props[0][j] * inst.classValue() * inst.weight();
/* 1140:1681 */           sumSquared[j] += props[0][j] * inst.classValue() * inst.classValue() * inst.weight();
/* 1141:     */           
/* 1142:1683 */           sumOfWeights[j] += props[0][j] * inst.weight();
/* 1143:     */         }
/* 1144:1685 */         totalSum += inst.classValue() * inst.weight();
/* 1145:1686 */         totalSumSquared += inst.classValue() * inst.classValue() * inst.weight();
/* 1146:     */         
/* 1147:1688 */         totalSumOfWeights += inst.weight();
/* 1148:     */       }
/* 1149:1692 */       dist = new double[sums.length][data.numClasses()];
/* 1150:1693 */       for (int j = 0; j < sums.length; j++) {
/* 1151:1694 */         if (sumOfWeights[j] > 0.0D) {
/* 1152:1695 */           dist[j][0] = (sums[j] / sumOfWeights[j]);
/* 1153:     */         } else {
/* 1154:1697 */           dist[j][0] = (totalSum / totalSumOfWeights);
/* 1155:     */         }
/* 1156:     */       }
/* 1157:1702 */       double priorVar = RandomTree.singleVariance(totalSum, totalSumSquared, totalSumOfWeights);
/* 1158:     */       
/* 1159:1704 */       double var = RandomTree.variance(sums, sumSquared, sumOfWeights);
/* 1160:1705 */       double gain = priorVar - var;
/* 1161:     */       
/* 1162:     */ 
/* 1163:1708 */       subsetWeights[att] = sumOfWeights;
/* 1164:1709 */       dists[0] = dist;
/* 1165:1710 */       vals[att] = gain;
/* 1166:     */       
/* 1167:1712 */       return splitPoint;
/* 1168:     */     }
/* 1169:     */     
/* 1170:     */     protected double distribution(double[][] props, double[][][] dists, int att, Instances data)
/* 1171:     */       throws Exception
/* 1172:     */     {
/* 1173:1727 */       double splitPoint = (0.0D / 0.0D);
/* 1174:1728 */       Attribute attribute = data.attribute(att);
/* 1175:1729 */       double[][] dist = (double[][])null;
/* 1176:1730 */       int indexOfFirstMissingValue = data.numInstances();
/* 1177:1732 */       if (attribute.isNominal())
/* 1178:     */       {
/* 1179:1735 */         dist = new double[attribute.numValues()][data.numClasses()];
/* 1180:1736 */         for (int i = 0; i < data.numInstances(); i++)
/* 1181:     */         {
/* 1182:1737 */           Instance inst = data.instance(i);
/* 1183:1738 */           if (inst.isMissing(att))
/* 1184:     */           {
/* 1185:1741 */             if (indexOfFirstMissingValue == data.numInstances()) {
/* 1186:1742 */               indexOfFirstMissingValue = i;
/* 1187:     */             }
/* 1188:     */           }
/* 1189:     */           else {
/* 1190:1746 */             dist[((int)inst.value(att))][((int)inst.classValue())] += inst.weight();
/* 1191:     */           }
/* 1192:     */         }
/* 1193:     */       }
/* 1194:     */       else
/* 1195:     */       {
/* 1196:1751 */         double[][] currDist = new double[2][data.numClasses()];
/* 1197:1752 */         dist = new double[2][data.numClasses()];
/* 1198:     */         
/* 1199:     */ 
/* 1200:1755 */         data.sort(att);
/* 1201:1758 */         for (int j = 0; j < data.numInstances(); j++)
/* 1202:     */         {
/* 1203:1759 */           Instance inst = data.instance(j);
/* 1204:1760 */           if (inst.isMissing(att))
/* 1205:     */           {
/* 1206:1763 */             indexOfFirstMissingValue = j;
/* 1207:1764 */             break;
/* 1208:     */           }
/* 1209:1766 */           currDist[1][((int)inst.classValue())] += inst.weight();
/* 1210:     */         }
/* 1211:1770 */         double priorVal = priorVal(currDist);
/* 1212:1773 */         for (int j = 0; j < currDist.length; j++) {
/* 1213:1774 */           System.arraycopy(currDist[j], 0, dist[j], 0, dist[j].length);
/* 1214:     */         }
/* 1215:1778 */         double currSplit = data.instance(0).value(att);
/* 1216:1779 */         double bestVal = -1.797693134862316E+308D;
/* 1217:1780 */         for (int i = 0; i < indexOfFirstMissingValue; i++)
/* 1218:     */         {
/* 1219:1781 */           Instance inst = data.instance(i);
/* 1220:1782 */           double attVal = inst.value(att);
/* 1221:1785 */           if (attVal > currSplit)
/* 1222:     */           {
/* 1223:1788 */             double currVal = gain(currDist, priorVal);
/* 1224:1791 */             if (currVal > bestVal)
/* 1225:     */             {
/* 1226:1794 */               bestVal = currVal;
/* 1227:     */               
/* 1228:     */ 
/* 1229:1797 */               splitPoint = (attVal + currSplit) / 2.0D;
/* 1230:1800 */               if (splitPoint <= currSplit) {
/* 1231:1801 */                 splitPoint = attVal;
/* 1232:     */               }
/* 1233:1805 */               for (int j = 0; j < currDist.length; j++) {
/* 1234:1806 */                 System.arraycopy(currDist[j], 0, dist[j], 0, dist[j].length);
/* 1235:     */               }
/* 1236:     */             }
/* 1237:1811 */             currSplit = attVal;
/* 1238:     */           }
/* 1239:1815 */           int classVal = (int)inst.classValue();
/* 1240:1816 */           currDist[0][classVal] += inst.weight();
/* 1241:1817 */           currDist[1][classVal] -= inst.weight();
/* 1242:     */         }
/* 1243:     */       }
/* 1244:1822 */       props[0] = new double[dist.length];
/* 1245:1823 */       for (int k = 0; k < props[0].length; k++) {
/* 1246:1824 */         props[0][k] = Utils.sum(dist[k]);
/* 1247:     */       }
/* 1248:1826 */       if (Utils.eq(Utils.sum(props[0]), 0.0D)) {
/* 1249:1827 */         for (int k = 0; k < props[0].length; k++) {
/* 1250:1828 */           props[0][k] = (1.0D / props[0].length);
/* 1251:     */         }
/* 1252:     */       } else {
/* 1253:1831 */         Utils.normalize(props[0]);
/* 1254:     */       }
/* 1255:1835 */       for (int i = indexOfFirstMissingValue; i < data.numInstances(); i++)
/* 1256:     */       {
/* 1257:1836 */         Instance inst = data.instance(i);
/* 1258:1837 */         if (attribute.isNominal())
/* 1259:     */         {
/* 1260:1840 */           if (inst.isMissing(att)) {
/* 1261:1841 */             for (int j = 0; j < dist.length; j++) {
/* 1262:1842 */               dist[j][((int)inst.classValue())] += props[0][j] * inst.weight();
/* 1263:     */             }
/* 1264:     */           }
/* 1265:     */         }
/* 1266:     */         else {
/* 1267:1848 */           for (int j = 0; j < dist.length; j++) {
/* 1268:1849 */             dist[j][((int)inst.classValue())] += props[0][j] * inst.weight();
/* 1269:     */           }
/* 1270:     */         }
/* 1271:     */       }
/* 1272:1855 */       dists[0] = dist;
/* 1273:1856 */       return splitPoint;
/* 1274:     */     }
/* 1275:     */     
/* 1276:     */     protected double priorVal(double[][] dist)
/* 1277:     */     {
/* 1278:1867 */       return ContingencyTables.entropyOverColumns(dist);
/* 1279:     */     }
/* 1280:     */     
/* 1281:     */     protected double gain(double[][] dist, double priorVal)
/* 1282:     */     {
/* 1283:1879 */       return priorVal - ContingencyTables.entropyConditionedOnRows(dist);
/* 1284:     */     }
/* 1285:     */     
/* 1286:     */     public String getRevision()
/* 1287:     */     {
/* 1288:1888 */       return RevisionUtils.extract("$Revision: 12505 $");
/* 1289:     */     }
/* 1290:     */     
/* 1291:     */     protected int toGraph(StringBuffer text, int num, Tree parent)
/* 1292:     */       throws Exception
/* 1293:     */     {
/* 1294:     */       
/* 1295:1904 */       if (this.m_Attribute == -1)
/* 1296:     */       {
/* 1297:1905 */         text.append("N" + Integer.toHexString(hashCode()) + " [label=\"" + num + Utils.backQuoteChars(leafString()) + "\"" + " shape=box]\n");
/* 1298:     */       }
/* 1299:     */       else
/* 1300:     */       {
/* 1301:1910 */         text.append("N" + Integer.toHexString(hashCode()) + " [label=\"" + num + ": " + Utils.backQuoteChars(RandomTree.this.m_Info.attribute(this.m_Attribute).name()) + "\"]\n");
/* 1302:1914 */         for (int i = 0; i < this.m_Successors.length; i++)
/* 1303:     */         {
/* 1304:1915 */           text.append("N" + Integer.toHexString(hashCode()) + "->" + "N" + Integer.toHexString(this.m_Successors[i].hashCode()) + " [label=\"");
/* 1305:1918 */           if (RandomTree.this.m_Info.attribute(this.m_Attribute).isNumeric())
/* 1306:     */           {
/* 1307:1919 */             if (i == 0) {
/* 1308:1920 */               text.append(" < " + Utils.doubleToString(this.m_SplitPoint, 2));
/* 1309:     */             } else {
/* 1310:1922 */               text.append(" >= " + Utils.doubleToString(this.m_SplitPoint, 2));
/* 1311:     */             }
/* 1312:     */           }
/* 1313:     */           else {
/* 1314:1925 */             text.append(" = " + Utils.backQuoteChars(RandomTree.this.m_Info.attribute(this.m_Attribute).value(i)));
/* 1315:     */           }
/* 1316:1928 */           text.append("\"]\n");
/* 1317:1929 */           num = this.m_Successors[i].toGraph(text, num, this);
/* 1318:     */         }
/* 1319:     */       }
/* 1320:1933 */       return num;
/* 1321:     */     }
/* 1322:     */   }
/* 1323:     */   
/* 1324:     */   protected static double variance(double[] s, double[] sS, double[] sumOfWeights)
/* 1325:     */   {
/* 1326:1948 */     double var = 0.0D;
/* 1327:1950 */     for (int i = 0; i < s.length; i++) {
/* 1328:1951 */       if (sumOfWeights[i] > 0.0D) {
/* 1329:1952 */         var += singleVariance(s[i], sS[i], sumOfWeights[i]);
/* 1330:     */       }
/* 1331:     */     }
/* 1332:1956 */     return var;
/* 1333:     */   }
/* 1334:     */   
/* 1335:     */   protected static double singleVariance(double s, double sS, double weight)
/* 1336:     */   {
/* 1337:1969 */     return sS - s * s / weight;
/* 1338:     */   }
/* 1339:     */   
/* 1340:     */   public static void main(String[] argv)
/* 1341:     */   {
/* 1342:1978 */     runClassifier(new RandomTree(), argv);
/* 1343:     */   }
/* 1344:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.RandomTree
 * JD-Core Version:    0.7.0.1
 */