/*    1:     */ package weka.classifiers.trees;
/*    2:     */ 
/*    3:     */ import java.io.Serializable;
/*    4:     */ import java.util.ArrayList;
/*    5:     */ import java.util.Collections;
/*    6:     */ import java.util.Enumeration;
/*    7:     */ import java.util.List;
/*    8:     */ import java.util.Map;
/*    9:     */ import java.util.Vector;
/*   10:     */ import weka.classifiers.AbstractClassifier;
/*   11:     */ import weka.classifiers.UpdateableClassifier;
/*   12:     */ import weka.classifiers.trees.ht.ActiveHNode;
/*   13:     */ import weka.classifiers.trees.ht.GiniSplitMetric;
/*   14:     */ import weka.classifiers.trees.ht.HNode;
/*   15:     */ import weka.classifiers.trees.ht.InactiveHNode;
/*   16:     */ import weka.classifiers.trees.ht.InfoGainSplitMetric;
/*   17:     */ import weka.classifiers.trees.ht.LeafNode;
/*   18:     */ import weka.classifiers.trees.ht.LearningNode;
/*   19:     */ import weka.classifiers.trees.ht.NBNode;
/*   20:     */ import weka.classifiers.trees.ht.NBNodeAdaptive;
/*   21:     */ import weka.classifiers.trees.ht.Split;
/*   22:     */ import weka.classifiers.trees.ht.SplitCandidate;
/*   23:     */ import weka.classifiers.trees.ht.SplitMetric;
/*   24:     */ import weka.classifiers.trees.ht.SplitNode;
/*   25:     */ import weka.core.Attribute;
/*   26:     */ import weka.core.Capabilities;
/*   27:     */ import weka.core.Capabilities.Capability;
/*   28:     */ import weka.core.Drawable;
/*   29:     */ import weka.core.Instance;
/*   30:     */ import weka.core.Instances;
/*   31:     */ import weka.core.Option;
/*   32:     */ import weka.core.OptionHandler;
/*   33:     */ import weka.core.RevisionHandler;
/*   34:     */ import weka.core.RevisionUtils;
/*   35:     */ import weka.core.SelectedTag;
/*   36:     */ import weka.core.Tag;
/*   37:     */ import weka.core.TechnicalInformation;
/*   38:     */ import weka.core.TechnicalInformation.Field;
/*   39:     */ import weka.core.TechnicalInformation.Type;
/*   40:     */ import weka.core.TechnicalInformationHandler;
/*   41:     */ import weka.core.Utils;
/*   42:     */ import weka.core.WeightedInstancesHandler;
/*   43:     */ 
/*   44:     */ public class HoeffdingTree
/*   45:     */   extends AbstractClassifier
/*   46:     */   implements UpdateableClassifier, WeightedInstancesHandler, OptionHandler, RevisionHandler, TechnicalInformationHandler, Drawable, Serializable
/*   47:     */ {
/*   48:     */   private static final long serialVersionUID = 7117521775722396251L;
/*   49:     */   protected Instances m_header;
/*   50:     */   protected HNode m_root;
/*   51: 173 */   protected double m_gracePeriod = 200.0D;
/*   52: 179 */   protected double m_splitConfidence = 1.0E-007D;
/*   53: 182 */   protected double m_hoeffdingTieThreshold = 0.05D;
/*   54: 188 */   protected double m_minFracWeightForTwoBranchesGain = 0.01D;
/*   55: 191 */   protected int m_selectedSplitMetric = 1;
/*   56: 192 */   protected SplitMetric m_splitMetric = new InfoGainSplitMetric(this.m_minFracWeightForTwoBranchesGain);
/*   57: 196 */   protected int m_leafStrategy = 2;
/*   58: 202 */   protected double m_nbThreshold = 0.0D;
/*   59:     */   protected int m_activeLeafCount;
/*   60:     */   protected int m_inactiveLeafCount;
/*   61:     */   protected int m_decisionNodeCount;
/*   62:     */   public static final int GINI_SPLIT = 0;
/*   63:     */   public static final int INFO_GAIN_SPLIT = 1;
/*   64: 211 */   public static final Tag[] TAGS_SELECTION = { new Tag(0, "Gini split"), new Tag(1, "Info gain split") };
/*   65:     */   public static final int LEAF_MAJ_CLASS = 0;
/*   66:     */   public static final int LEAF_NB = 1;
/*   67:     */   public static final int LEAF_NB_ADAPTIVE = 2;
/*   68: 219 */   public static final Tag[] TAGS_SELECTION2 = { new Tag(0, "Majority class"), new Tag(1, "Naive Bayes"), new Tag(2, "Naive Bayes adaptive") };
/*   69:     */   protected boolean m_printLeafModels;
/*   70:     */   
/*   71:     */   public String globalInfo()
/*   72:     */   {
/*   73: 237 */     return "A Hoeffding tree (VFDT) is an incremental, anytime decision tree induction algorithm that is capable of learning from massive data streams, assuming that the distribution generating examples does not change over time. Hoeffding trees exploit the fact that a small sample can often be enough to choose an optimal splitting attribute. This idea is supported mathematically by the Hoeffding bound, which quantifies the number of observations (in our case, examples) needed to estimate some statistics within a prescribed precision (in our case, the goodness of an attribute).\n\nA theoretically appealing feature  of Hoeffding Trees not shared by otherincremental decision tree learners is that  it has sound guarantees of performance. Using the Hoeffding bound one can show that  its output is asymptotically nearly identical to that of a non-incremental learner  using infinitely many examples. For more information see: \n\n" + getTechnicalInformation().toString();
/*   74:     */   }
/*   75:     */   
/*   76:     */   public TechnicalInformation getTechnicalInformation()
/*   77:     */   {
/*   78: 263 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*   79: 264 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Geoff Hulten and Laurie Spencer and Pedro Domingos");
/*   80:     */     
/*   81: 266 */     result.setValue(TechnicalInformation.Field.TITLE, "Mining time-changing data streams");
/*   82: 267 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "ACM SIGKDD Intl. Conf. on Knowledge Discovery and Data Mining");
/*   83:     */     
/*   84: 269 */     result.setValue(TechnicalInformation.Field.YEAR, "2001");
/*   85: 270 */     result.setValue(TechnicalInformation.Field.PAGES, "97-106");
/*   86: 271 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "ACM Press");
/*   87:     */     
/*   88: 273 */     return result;
/*   89:     */   }
/*   90:     */   
/*   91:     */   protected void reset()
/*   92:     */   {
/*   93: 277 */     this.m_root = null;
/*   94:     */     
/*   95: 279 */     this.m_activeLeafCount = 0;
/*   96: 280 */     this.m_inactiveLeafCount = 0;
/*   97: 281 */     this.m_decisionNodeCount = 0;
/*   98:     */   }
/*   99:     */   
/*  100:     */   public Capabilities getCapabilities()
/*  101:     */   {
/*  102: 291 */     Capabilities result = super.getCapabilities();
/*  103: 292 */     result.disableAll();
/*  104:     */     
/*  105:     */ 
/*  106: 295 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  107: 296 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  108: 297 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  109: 298 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  110:     */     
/*  111: 300 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  112: 301 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  113:     */     
/*  114: 303 */     result.setMinimumNumberInstances(0);
/*  115:     */     
/*  116: 305 */     return result;
/*  117:     */   }
/*  118:     */   
/*  119:     */   public Enumeration<Option> listOptions()
/*  120:     */   {
/*  121: 315 */     Vector<Option> newVector = new Vector();
/*  122:     */     
/*  123: 317 */     newVector.add(new Option("\tThe leaf prediction strategy to use. 0 = majority class, 1 = naive Bayes, 2 = naive Bayes adaptive.\n\t(default = 2)", "L", 1, "-L"));
/*  124:     */     
/*  125:     */ 
/*  126:     */ 
/*  127: 321 */     newVector.add(new Option("\tThe splitting criterion to use. 0 = Gini, 1 = Info gain\n\t(default = 1)", "S", 1, "-S"));
/*  128:     */     
/*  129: 323 */     newVector.add(new Option("\tThe allowable error in a split decision - values closer to zero will take longer to decide\n\t(default = 1e-7)", "E", 1, "-E"));
/*  130:     */     
/*  131:     */ 
/*  132: 326 */     newVector.add(new Option("\tThreshold below which a split will be forced to break ties\n\t(default = 0.05)", "H", 1, "-H"));
/*  133:     */     
/*  134:     */ 
/*  135: 329 */     newVector.add(new Option("\tMinimum fraction of weight required down at least two branches for info gain splitting\n\t(default = 0.01)", "M", 1, "-M"));
/*  136:     */     
/*  137:     */ 
/*  138:     */ 
/*  139: 333 */     newVector.add(new Option("\tGrace period - the number of instances a leaf should observe between split attempts\n\t(default = 200)", "G", 1, "-G"));
/*  140:     */     
/*  141:     */ 
/*  142: 336 */     newVector.add(new Option("\tThe number of instances (weight) a leaf should observe before allowing naive Bayes to make predictions (NB or NB adaptive only)\n\t(default = 0)", "N", 1, "-N"));
/*  143:     */     
/*  144:     */ 
/*  145:     */ 
/*  146:     */ 
/*  147: 341 */     newVector.add(new Option("\tPrint leaf models when using naive Bayes at the leaves.", "P", 0, "-P"));
/*  148:     */     
/*  149:     */ 
/*  150: 344 */     return newVector.elements();
/*  151:     */   }
/*  152:     */   
/*  153:     */   public void setOptions(String[] options)
/*  154:     */     throws Exception
/*  155:     */   {
/*  156: 409 */     reset();
/*  157:     */     
/*  158: 411 */     super.setOptions(options);
/*  159:     */     
/*  160: 413 */     String opt = Utils.getOption('L', options);
/*  161: 414 */     if (opt.length() > 0) {
/*  162: 415 */       setLeafPredictionStrategy(new SelectedTag(Integer.parseInt(opt), TAGS_SELECTION2));
/*  163:     */     }
/*  164: 419 */     opt = Utils.getOption('S', options);
/*  165: 420 */     if (opt.length() > 0) {
/*  166: 421 */       setSplitCriterion(new SelectedTag(Integer.parseInt(opt), TAGS_SELECTION));
/*  167:     */     }
/*  168: 424 */     opt = Utils.getOption('E', options);
/*  169: 425 */     if (opt.length() > 0) {
/*  170: 426 */       setSplitConfidence(Double.parseDouble(opt));
/*  171:     */     }
/*  172: 429 */     opt = Utils.getOption('H', options);
/*  173: 430 */     if (opt.length() > 0) {
/*  174: 431 */       setHoeffdingTieThreshold(Double.parseDouble(opt));
/*  175:     */     }
/*  176: 434 */     opt = Utils.getOption('M', options);
/*  177: 435 */     if (opt.length() > 0) {
/*  178: 436 */       setMinimumFractionOfWeightInfoGain(Double.parseDouble(opt));
/*  179:     */     }
/*  180: 439 */     opt = Utils.getOption('G', options);
/*  181: 440 */     if (opt.length() > 0) {
/*  182: 441 */       setGracePeriod(Double.parseDouble(opt));
/*  183:     */     }
/*  184: 444 */     opt = Utils.getOption('N', options);
/*  185: 445 */     if (opt.length() > 0) {
/*  186: 446 */       setNaiveBayesPredictionThreshold(Double.parseDouble(opt));
/*  187:     */     }
/*  188: 449 */     this.m_printLeafModels = Utils.getFlag('P', options);
/*  189:     */   }
/*  190:     */   
/*  191:     */   public String[] getOptions()
/*  192:     */   {
/*  193: 459 */     ArrayList<String> options = new ArrayList();
/*  194:     */     
/*  195: 461 */     options.add("-L");
/*  196: 462 */     options.add("" + getLeafPredictionStrategy().getSelectedTag().getID());
/*  197:     */     
/*  198: 464 */     options.add("-S");
/*  199: 465 */     options.add("" + getSplitCriterion().getSelectedTag().getID());
/*  200:     */     
/*  201: 467 */     options.add("-E");
/*  202: 468 */     options.add("" + getSplitConfidence());
/*  203:     */     
/*  204: 470 */     options.add("-H");
/*  205: 471 */     options.add("" + getHoeffdingTieThreshold());
/*  206:     */     
/*  207: 473 */     options.add("-M");
/*  208: 474 */     options.add("" + getMinimumFractionOfWeightInfoGain());
/*  209:     */     
/*  210: 476 */     options.add("-G");
/*  211: 477 */     options.add("" + getGracePeriod());
/*  212:     */     
/*  213: 479 */     options.add("-N");
/*  214: 480 */     options.add("" + getNaiveBayesPredictionThreshold());
/*  215: 482 */     if (this.m_printLeafModels) {
/*  216: 483 */       options.add("-P");
/*  217:     */     }
/*  218: 486 */     return (String[])options.toArray(new String[1]);
/*  219:     */   }
/*  220:     */   
/*  221:     */   public String printLeafModelsTipText()
/*  222:     */   {
/*  223: 496 */     return "Print leaf models (naive bayes leaves only)";
/*  224:     */   }
/*  225:     */   
/*  226:     */   public void setPrintLeafModels(boolean p)
/*  227:     */   {
/*  228: 500 */     this.m_printLeafModels = p;
/*  229:     */   }
/*  230:     */   
/*  231:     */   public boolean getPrintLeafModels()
/*  232:     */   {
/*  233: 504 */     return this.m_printLeafModels;
/*  234:     */   }
/*  235:     */   
/*  236:     */   public String minimumFractionOfWeightInfoGainTipText()
/*  237:     */   {
/*  238: 514 */     return "Minimum fraction of weight required down at least two branches for info gain splitting.";
/*  239:     */   }
/*  240:     */   
/*  241:     */   public void setMinimumFractionOfWeightInfoGain(double m)
/*  242:     */   {
/*  243: 525 */     this.m_minFracWeightForTwoBranchesGain = m;
/*  244:     */   }
/*  245:     */   
/*  246:     */   public double getMinimumFractionOfWeightInfoGain()
/*  247:     */   {
/*  248: 535 */     return this.m_minFracWeightForTwoBranchesGain;
/*  249:     */   }
/*  250:     */   
/*  251:     */   public String gracePeriodTipText()
/*  252:     */   {
/*  253: 545 */     return "Number of instances (or total weight of instances) a leaf should observe between split attempts.";
/*  254:     */   }
/*  255:     */   
/*  256:     */   public void setGracePeriod(double grace)
/*  257:     */   {
/*  258: 556 */     this.m_gracePeriod = grace;
/*  259:     */   }
/*  260:     */   
/*  261:     */   public double getGracePeriod()
/*  262:     */   {
/*  263: 566 */     return this.m_gracePeriod;
/*  264:     */   }
/*  265:     */   
/*  266:     */   public String hoeffdingTieThresholdTipText()
/*  267:     */   {
/*  268: 576 */     return "Theshold below which a split will be forced to break ties.";
/*  269:     */   }
/*  270:     */   
/*  271:     */   public void setHoeffdingTieThreshold(double ht)
/*  272:     */   {
/*  273: 585 */     this.m_hoeffdingTieThreshold = ht;
/*  274:     */   }
/*  275:     */   
/*  276:     */   public double getHoeffdingTieThreshold()
/*  277:     */   {
/*  278: 594 */     return this.m_hoeffdingTieThreshold;
/*  279:     */   }
/*  280:     */   
/*  281:     */   public String splitConfidenceTipText()
/*  282:     */   {
/*  283: 604 */     return "The allowable error in a split decision. Values closer to zero will take longer to decide.";
/*  284:     */   }
/*  285:     */   
/*  286:     */   public void setSplitConfidence(double sc)
/*  287:     */   {
/*  288: 615 */     this.m_splitConfidence = sc;
/*  289:     */   }
/*  290:     */   
/*  291:     */   public double getSplitConfidence()
/*  292:     */   {
/*  293: 625 */     return this.m_splitConfidence;
/*  294:     */   }
/*  295:     */   
/*  296:     */   public String splitCriterionTipText()
/*  297:     */   {
/*  298: 635 */     return "The splitting criterion to use";
/*  299:     */   }
/*  300:     */   
/*  301:     */   public void setSplitCriterion(SelectedTag crit)
/*  302:     */   {
/*  303: 644 */     if (crit.getTags() == TAGS_SELECTION) {
/*  304: 645 */       this.m_selectedSplitMetric = crit.getSelectedTag().getID();
/*  305:     */     }
/*  306:     */   }
/*  307:     */   
/*  308:     */   public SelectedTag getSplitCriterion()
/*  309:     */   {
/*  310: 655 */     return new SelectedTag(this.m_selectedSplitMetric, TAGS_SELECTION);
/*  311:     */   }
/*  312:     */   
/*  313:     */   public String leafPredictionStrategyTipText()
/*  314:     */   {
/*  315: 665 */     return "The leaf prediction strategy to use";
/*  316:     */   }
/*  317:     */   
/*  318:     */   public void setLeafPredictionStrategy(SelectedTag strat)
/*  319:     */   {
/*  320: 675 */     if (strat.getTags() == TAGS_SELECTION2) {
/*  321: 676 */       this.m_leafStrategy = strat.getSelectedTag().getID();
/*  322:     */     }
/*  323:     */   }
/*  324:     */   
/*  325:     */   public SelectedTag getLeafPredictionStrategy()
/*  326:     */   {
/*  327: 687 */     return new SelectedTag(this.m_leafStrategy, TAGS_SELECTION2);
/*  328:     */   }
/*  329:     */   
/*  330:     */   public String naiveBayesPredictionThresholdTipText()
/*  331:     */   {
/*  332: 697 */     return "The number of instances (weight) a leaf should observe before allowing naive Bayes (adaptive) to make predictions";
/*  333:     */   }
/*  334:     */   
/*  335:     */   public void setNaiveBayesPredictionThreshold(double n)
/*  336:     */   {
/*  337: 708 */     this.m_nbThreshold = n;
/*  338:     */   }
/*  339:     */   
/*  340:     */   public double getNaiveBayesPredictionThreshold()
/*  341:     */   {
/*  342: 718 */     return this.m_nbThreshold;
/*  343:     */   }
/*  344:     */   
/*  345:     */   protected static double computeHoeffdingBound(double max, double confidence, double weight)
/*  346:     */   {
/*  347: 723 */     return Math.sqrt(max * max * Math.log(1.0D / confidence) / (2.0D * weight));
/*  348:     */   }
/*  349:     */   
/*  350:     */   public void buildClassifier(Instances data)
/*  351:     */     throws Exception
/*  352:     */   {
/*  353: 735 */     reset();
/*  354:     */     
/*  355: 737 */     this.m_header = new Instances(data, 0);
/*  356: 738 */     if (this.m_selectedSplitMetric == 0) {
/*  357: 739 */       this.m_splitMetric = new GiniSplitMetric();
/*  358:     */     } else {
/*  359: 741 */       this.m_splitMetric = new InfoGainSplitMetric(this.m_minFracWeightForTwoBranchesGain);
/*  360:     */     }
/*  361: 744 */     data = new Instances(data);
/*  362: 745 */     data.deleteWithMissingClass();
/*  363: 746 */     for (int i = 0; i < data.numInstances(); i++) {
/*  364: 747 */       updateClassifier(data.instance(i));
/*  365:     */     }
/*  366: 751 */     getCapabilities().testWithFail(data);
/*  367:     */   }
/*  368:     */   
/*  369:     */   public void updateClassifier(Instance inst)
/*  370:     */     throws Exception
/*  371:     */   {
/*  372: 765 */     if (inst.classIsMissing()) {
/*  373: 766 */       return;
/*  374:     */     }
/*  375: 769 */     if (this.m_root == null) {
/*  376: 770 */       this.m_root = newLearningNode();
/*  377:     */     }
/*  378: 773 */     LeafNode l = this.m_root.leafForInstance(inst, null, null);
/*  379: 774 */     HNode actualNode = l.m_theNode;
/*  380: 775 */     if (actualNode == null)
/*  381:     */     {
/*  382: 776 */       actualNode = new ActiveHNode();
/*  383: 777 */       l.m_parentNode.setChild(l.m_parentBranch, actualNode);
/*  384:     */     }
/*  385: 780 */     if ((actualNode instanceof LearningNode))
/*  386:     */     {
/*  387: 781 */       actualNode.updateNode(inst);
/*  388: 783 */       if ((actualNode instanceof ActiveHNode))
/*  389:     */       {
/*  390: 784 */         double totalWeight = actualNode.totalWeight();
/*  391: 785 */         if (totalWeight - ((ActiveHNode)actualNode).m_weightSeenAtLastSplitEval > this.m_gracePeriod)
/*  392:     */         {
/*  393: 789 */           trySplit((ActiveHNode)actualNode, l.m_parentNode, l.m_parentBranch);
/*  394:     */           
/*  395: 791 */           ((ActiveHNode)actualNode).m_weightSeenAtLastSplitEval = totalWeight;
/*  396:     */         }
/*  397:     */       }
/*  398:     */     }
/*  399:     */   }
/*  400:     */   
/*  401:     */   public double[] distributionForInstance(Instance inst)
/*  402:     */     throws Exception
/*  403:     */   {
/*  404: 807 */     Attribute classAtt = inst.classAttribute();
/*  405: 808 */     double[] pred = new double[classAtt.numValues()];
/*  406: 810 */     if (this.m_root != null)
/*  407:     */     {
/*  408: 811 */       LeafNode l = this.m_root.leafForInstance(inst, null, null);
/*  409: 812 */       HNode actualNode = l.m_theNode;
/*  410: 814 */       if (actualNode == null) {
/*  411: 815 */         actualNode = l.m_parentNode;
/*  412:     */       }
/*  413: 818 */       pred = actualNode.getDistribution(inst, classAtt);
/*  414:     */     }
/*  415:     */     else
/*  416:     */     {
/*  417: 822 */       for (int i = 0; i < classAtt.numValues(); i++) {
/*  418: 823 */         pred[i] = 1.0D;
/*  419:     */       }
/*  420: 825 */       Utils.normalize(pred);
/*  421:     */     }
/*  422: 829 */     return pred;
/*  423:     */   }
/*  424:     */   
/*  425:     */   protected void deactivateNode(ActiveHNode toDeactivate, SplitNode parent, String parentBranch)
/*  426:     */   {
/*  427: 841 */     HNode leaf = new InactiveHNode(toDeactivate.m_classDistribution);
/*  428: 843 */     if (parent == null) {
/*  429: 844 */       this.m_root = leaf;
/*  430:     */     } else {
/*  431: 846 */       parent.setChild(parentBranch, leaf);
/*  432:     */     }
/*  433: 848 */     this.m_activeLeafCount -= 1;
/*  434: 849 */     this.m_inactiveLeafCount += 1;
/*  435:     */   }
/*  436:     */   
/*  437:     */   protected void activateNode(InactiveHNode toActivate, SplitNode parent, String parentBranch)
/*  438:     */   {
/*  439: 861 */     HNode leaf = new ActiveHNode();
/*  440: 862 */     leaf.m_classDistribution = toActivate.m_classDistribution;
/*  441: 864 */     if (parent == null) {
/*  442: 865 */       this.m_root = leaf;
/*  443:     */     } else {
/*  444: 867 */       parent.setChild(parentBranch, leaf);
/*  445:     */     }
/*  446: 870 */     this.m_activeLeafCount += 1;
/*  447: 871 */     this.m_inactiveLeafCount -= 1;
/*  448:     */   }
/*  449:     */   
/*  450:     */   protected void trySplit(ActiveHNode node, SplitNode parent, String parentBranch)
/*  451:     */     throws Exception
/*  452:     */   {
/*  453: 886 */     if (node.numEntriesInClassDistribution() > 1)
/*  454:     */     {
/*  455: 887 */       List<SplitCandidate> bestSplits = node.getPossibleSplits(this.m_splitMetric);
/*  456: 888 */       Collections.sort(bestSplits);
/*  457:     */       
/*  458: 890 */       boolean doSplit = false;
/*  459: 891 */       if (bestSplits.size() < 2)
/*  460:     */       {
/*  461: 892 */         doSplit = bestSplits.size() > 0;
/*  462:     */       }
/*  463:     */       else
/*  464:     */       {
/*  465: 895 */         double metricMax = this.m_splitMetric.getMetricRange(node.m_classDistribution);
/*  466: 896 */         double hoeffdingBound = computeHoeffdingBound(metricMax, this.m_splitConfidence, node.totalWeight());
/*  467:     */         
/*  468:     */ 
/*  469: 899 */         SplitCandidate best = (SplitCandidate)bestSplits.get(bestSplits.size() - 1);
/*  470: 900 */         SplitCandidate secondBest = (SplitCandidate)bestSplits.get(bestSplits.size() - 2);
/*  471: 902 */         if ((best.m_splitMerit - secondBest.m_splitMerit > hoeffdingBound) || (hoeffdingBound < this.m_hoeffdingTieThreshold)) {
/*  472: 904 */           doSplit = true;
/*  473:     */         }
/*  474:     */       }
/*  475: 910 */       if (doSplit)
/*  476:     */       {
/*  477: 911 */         SplitCandidate best = (SplitCandidate)bestSplits.get(bestSplits.size() - 1);
/*  478: 913 */         if (best.m_splitTest == null)
/*  479:     */         {
/*  480: 915 */           deactivateNode(node, parent, parentBranch);
/*  481:     */         }
/*  482:     */         else
/*  483:     */         {
/*  484: 917 */           SplitNode newSplit = new SplitNode(node.m_classDistribution, best.m_splitTest);
/*  485: 920 */           for (int i = 0; i < best.numSplits(); i++)
/*  486:     */           {
/*  487: 921 */             ActiveHNode newChild = newLearningNode();
/*  488: 922 */             newChild.m_classDistribution = ((Map)best.m_postSplitClassDistributions.get(i));
/*  489:     */             
/*  490: 924 */             newChild.m_weightSeenAtLastSplitEval = newChild.totalWeight();
/*  491: 925 */             String branchName = "";
/*  492: 926 */             if (this.m_header.attribute((String)best.m_splitTest.splitAttributes().get(0)).isNumeric())
/*  493:     */             {
/*  494: 928 */               branchName = i == 0 ? "left" : "right";
/*  495:     */             }
/*  496:     */             else
/*  497:     */             {
/*  498: 930 */               Attribute splitAtt = this.m_header.attribute((String)best.m_splitTest.splitAttributes().get(0));
/*  499:     */               
/*  500: 932 */               branchName = splitAtt.value(i);
/*  501:     */             }
/*  502: 934 */             newSplit.setChild(branchName, newChild);
/*  503:     */           }
/*  504: 937 */           this.m_activeLeafCount -= 1;
/*  505: 938 */           this.m_decisionNodeCount += 1;
/*  506: 939 */           this.m_activeLeafCount += best.numSplits();
/*  507: 941 */           if (parent == null) {
/*  508: 942 */             this.m_root = newSplit;
/*  509:     */           } else {
/*  510: 944 */             parent.setChild(parentBranch, newSplit);
/*  511:     */           }
/*  512:     */         }
/*  513:     */       }
/*  514:     */     }
/*  515:     */   }
/*  516:     */   
/*  517:     */   protected ActiveHNode newLearningNode()
/*  518:     */     throws Exception
/*  519:     */   {
/*  520:     */     ActiveHNode newChild;
/*  521:     */     ActiveHNode newChild;
/*  522: 961 */     if (this.m_leafStrategy == 0)
/*  523:     */     {
/*  524: 962 */       newChild = new ActiveHNode();
/*  525:     */     }
/*  526:     */     else
/*  527:     */     {
/*  528:     */       ActiveHNode newChild;
/*  529: 963 */       if (this.m_leafStrategy == 1) {
/*  530: 964 */         newChild = new NBNode(this.m_header, this.m_nbThreshold);
/*  531:     */       } else {
/*  532: 966 */         newChild = new NBNodeAdaptive(this.m_header, this.m_nbThreshold);
/*  533:     */       }
/*  534:     */     }
/*  535: 969 */     return newChild;
/*  536:     */   }
/*  537:     */   
/*  538:     */   public String toString()
/*  539:     */   {
/*  540: 979 */     if (this.m_root == null) {
/*  541: 980 */       return "No model built yet!";
/*  542:     */     }
/*  543: 983 */     return this.m_root.toString(this.m_printLeafModels);
/*  544:     */   }
/*  545:     */   
/*  546:     */   public String getRevision()
/*  547:     */   {
/*  548: 993 */     return RevisionUtils.extract("$Revision: 11006 $");
/*  549:     */   }
/*  550:     */   
/*  551:     */   public static void main(String[] args)
/*  552:     */   {
/*  553: 997 */     runClassifier(new HoeffdingTree(), args);
/*  554:     */   }
/*  555:     */   
/*  556:     */   public int graphType()
/*  557:     */   {
/*  558:1002 */     return 1;
/*  559:     */   }
/*  560:     */   
/*  561:     */   public String graph()
/*  562:     */     throws Exception
/*  563:     */   {
/*  564:1007 */     if (this.m_root == null) {
/*  565:1008 */       throw new Exception("No model built yet!");
/*  566:     */     }
/*  567:1010 */     this.m_root.installNodeNums(0);
/*  568:1011 */     StringBuffer buff = new StringBuffer();
/*  569:1012 */     buff.append("digraph HoeffdingTree {\n");
/*  570:1013 */     this.m_root.graphTree(buff);
/*  571:1014 */     buff.append("}\n");
/*  572:     */     
/*  573:1016 */     return buff.toString();
/*  574:     */   }
/*  575:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.HoeffdingTree
 * JD-Core Version:    0.7.0.1
 */