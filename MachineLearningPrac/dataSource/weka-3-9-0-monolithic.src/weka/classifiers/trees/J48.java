/*    1:     */ package weka.classifiers.trees;
/*    2:     */ 
/*    3:     */ import java.util.Collections;
/*    4:     */ import java.util.Enumeration;
/*    5:     */ import java.util.Vector;
/*    6:     */ import weka.classifiers.AbstractClassifier;
/*    7:     */ import weka.classifiers.Sourcable;
/*    8:     */ import weka.classifiers.trees.j48.BinC45ModelSelection;
/*    9:     */ import weka.classifiers.trees.j48.C45ModelSelection;
/*   10:     */ import weka.classifiers.trees.j48.C45PruneableClassifierTree;
/*   11:     */ import weka.classifiers.trees.j48.ClassifierTree;
/*   12:     */ import weka.classifiers.trees.j48.ModelSelection;
/*   13:     */ import weka.classifiers.trees.j48.PruneableClassifierTree;
/*   14:     */ import weka.core.AdditionalMeasureProducer;
/*   15:     */ import weka.core.Capabilities;
/*   16:     */ import weka.core.Capabilities.Capability;
/*   17:     */ import weka.core.Drawable;
/*   18:     */ import weka.core.Instance;
/*   19:     */ import weka.core.Instances;
/*   20:     */ import weka.core.Matchable;
/*   21:     */ import weka.core.Option;
/*   22:     */ import weka.core.OptionHandler;
/*   23:     */ import weka.core.PartitionGenerator;
/*   24:     */ import weka.core.RevisionUtils;
/*   25:     */ import weka.core.Summarizable;
/*   26:     */ import weka.core.TechnicalInformation;
/*   27:     */ import weka.core.TechnicalInformation.Field;
/*   28:     */ import weka.core.TechnicalInformation.Type;
/*   29:     */ import weka.core.TechnicalInformationHandler;
/*   30:     */ import weka.core.Utils;
/*   31:     */ import weka.core.WeightedInstancesHandler;
/*   32:     */ 
/*   33:     */ public class J48
/*   34:     */   extends AbstractClassifier
/*   35:     */   implements OptionHandler, Drawable, Matchable, Sourcable, WeightedInstancesHandler, Summarizable, AdditionalMeasureProducer, TechnicalInformationHandler, PartitionGenerator
/*   36:     */ {
/*   37:     */   static final long serialVersionUID = -217733168393644444L;
/*   38:     */   protected ClassifierTree m_root;
/*   39: 167 */   protected boolean m_unpruned = false;
/*   40: 170 */   protected boolean m_collapseTree = true;
/*   41: 173 */   protected float m_CF = 0.25F;
/*   42: 176 */   protected int m_minNumObj = 2;
/*   43: 179 */   protected boolean m_useMDLcorrection = true;
/*   44: 185 */   protected boolean m_useLaplace = false;
/*   45: 188 */   protected boolean m_reducedErrorPruning = false;
/*   46: 191 */   protected int m_numFolds = 3;
/*   47: 194 */   protected boolean m_binarySplits = false;
/*   48: 197 */   protected boolean m_subtreeRaising = true;
/*   49: 200 */   protected boolean m_noCleanup = false;
/*   50: 203 */   protected int m_Seed = 1;
/*   51:     */   protected boolean m_doNotMakeSplitPointActualValue;
/*   52:     */   
/*   53:     */   public String globalInfo()
/*   54:     */   {
/*   55: 216 */     return "Class for generating a pruned or unpruned C4.5 decision tree. For more information, see\n\n" + getTechnicalInformation().toString();
/*   56:     */   }
/*   57:     */   
/*   58:     */   public TechnicalInformation getTechnicalInformation()
/*   59:     */   {
/*   60: 231 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.BOOK);
/*   61: 232 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Ross Quinlan");
/*   62: 233 */     result.setValue(TechnicalInformation.Field.YEAR, "1993");
/*   63: 234 */     result.setValue(TechnicalInformation.Field.TITLE, "C4.5: Programs for Machine Learning");
/*   64: 235 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Morgan Kaufmann Publishers");
/*   65: 236 */     result.setValue(TechnicalInformation.Field.ADDRESS, "San Mateo, CA");
/*   66:     */     
/*   67: 238 */     return result;
/*   68:     */   }
/*   69:     */   
/*   70:     */   public Capabilities getCapabilities()
/*   71:     */   {
/*   72: 250 */     Capabilities result = new Capabilities(this);
/*   73: 251 */     result.disableAll();
/*   74:     */     
/*   75: 253 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*   76: 254 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*   77: 255 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*   78: 256 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*   79:     */     
/*   80:     */ 
/*   81: 259 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*   82: 260 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*   83:     */     
/*   84:     */ 
/*   85: 263 */     result.setMinimumNumberInstances(0);
/*   86:     */     
/*   87: 265 */     return result;
/*   88:     */   }
/*   89:     */   
/*   90:     */   public void buildClassifier(Instances instances)
/*   91:     */     throws Exception
/*   92:     */   {
/*   93:     */     ModelSelection modSelection;
/*   94:     */     ModelSelection modSelection;
/*   95: 279 */     if (this.m_binarySplits) {
/*   96: 280 */       modSelection = new BinC45ModelSelection(this.m_minNumObj, instances, this.m_useMDLcorrection, this.m_doNotMakeSplitPointActualValue);
/*   97:     */     } else {
/*   98: 283 */       modSelection = new C45ModelSelection(this.m_minNumObj, instances, this.m_useMDLcorrection, this.m_doNotMakeSplitPointActualValue);
/*   99:     */     }
/*  100: 286 */     if (!this.m_reducedErrorPruning) {
/*  101: 287 */       this.m_root = new C45PruneableClassifierTree(modSelection, !this.m_unpruned, this.m_CF, this.m_subtreeRaising, !this.m_noCleanup, this.m_collapseTree);
/*  102:     */     } else {
/*  103: 290 */       this.m_root = new PruneableClassifierTree(modSelection, !this.m_unpruned, this.m_numFolds, !this.m_noCleanup, this.m_Seed);
/*  104:     */     }
/*  105: 293 */     this.m_root.buildClassifier(instances);
/*  106: 294 */     if (this.m_binarySplits) {
/*  107: 295 */       ((BinC45ModelSelection)modSelection).cleanup();
/*  108:     */     } else {
/*  109: 297 */       ((C45ModelSelection)modSelection).cleanup();
/*  110:     */     }
/*  111:     */   }
/*  112:     */   
/*  113:     */   public double classifyInstance(Instance instance)
/*  114:     */     throws Exception
/*  115:     */   {
/*  116: 311 */     return this.m_root.classifyInstance(instance);
/*  117:     */   }
/*  118:     */   
/*  119:     */   public final double[] distributionForInstance(Instance instance)
/*  120:     */     throws Exception
/*  121:     */   {
/*  122: 325 */     return this.m_root.distributionForInstance(instance, this.m_useLaplace);
/*  123:     */   }
/*  124:     */   
/*  125:     */   public int graphType()
/*  126:     */   {
/*  127: 335 */     return 1;
/*  128:     */   }
/*  129:     */   
/*  130:     */   public String graph()
/*  131:     */     throws Exception
/*  132:     */   {
/*  133: 347 */     return this.m_root.graph();
/*  134:     */   }
/*  135:     */   
/*  136:     */   public String prefix()
/*  137:     */     throws Exception
/*  138:     */   {
/*  139: 359 */     return this.m_root.prefix();
/*  140:     */   }
/*  141:     */   
/*  142:     */   public String toSource(String className)
/*  143:     */     throws Exception
/*  144:     */   {
/*  145: 372 */     StringBuffer[] source = this.m_root.toSource(className);
/*  146: 373 */     return "class " + className + " {\n\n" + "  public static double classify(Object[] i)\n" + "    throws Exception {\n\n" + "    double p = Double.NaN;\n" + source[0] + "    return p;\n" + "  }\n" + source[1] + "}\n";
/*  147:     */   }
/*  148:     */   
/*  149:     */   public Enumeration<Option> listOptions()
/*  150:     */   {
/*  151: 432 */     Vector<Option> newVector = new Vector(13);
/*  152:     */     
/*  153: 434 */     newVector.addElement(new Option("\tUse unpruned tree.", "U", 0, "-U"));
/*  154: 435 */     newVector.addElement(new Option("\tDo not collapse tree.", "O", 0, "-O"));
/*  155: 436 */     newVector.addElement(new Option("\tSet confidence threshold for pruning.\n\t(default 0.25)", "C", 1, "-C <pruning confidence>"));
/*  156:     */     
/*  157: 438 */     newVector.addElement(new Option("\tSet minimum number of instances per leaf.\n\t(default 2)", "M", 1, "-M <minimum number of instances>"));
/*  158:     */     
/*  159:     */ 
/*  160: 441 */     newVector.addElement(new Option("\tUse reduced error pruning.", "R", 0, "-R"));
/*  161:     */     
/*  162: 443 */     newVector.addElement(new Option("\tSet number of folds for reduced error\n\tpruning. One fold is used as pruning set.\n\t(default 3)", "N", 1, "-N <number of folds>"));
/*  163:     */     
/*  164:     */ 
/*  165: 446 */     newVector.addElement(new Option("\tUse binary splits only.", "B", 0, "-B"));
/*  166: 447 */     newVector.addElement(new Option("\tDo not perform subtree raising.", "S", 0, "-S"));
/*  167:     */     
/*  168: 449 */     newVector.addElement(new Option("\tDo not clean up after the tree has been built.", "L", 0, "-L"));
/*  169:     */     
/*  170: 451 */     newVector.addElement(new Option("\tLaplace smoothing for predicted probabilities.", "A", 0, "-A"));
/*  171:     */     
/*  172: 453 */     newVector.addElement(new Option("\tDo not use MDL correction for info gain on numeric attributes.", "J", 0, "-J"));
/*  173:     */     
/*  174:     */ 
/*  175: 456 */     newVector.addElement(new Option("\tSeed for random data shuffling (default 1).", "Q", 1, "-Q <seed>"));
/*  176:     */     
/*  177: 458 */     newVector.addElement(new Option("\tDo not make split point actual value.", "-doNotMakeSplitPointActualValue", 0, "-doNotMakeSplitPointActualValue"));
/*  178:     */     
/*  179:     */ 
/*  180: 461 */     newVector.addAll(Collections.list(super.listOptions()));
/*  181:     */     
/*  182: 463 */     return newVector.elements();
/*  183:     */   }
/*  184:     */   
/*  185:     */   public void setOptions(String[] options)
/*  186:     */     throws Exception
/*  187:     */   {
/*  188: 550 */     String minNumString = Utils.getOption('M', options);
/*  189: 551 */     if (minNumString.length() != 0) {
/*  190: 552 */       this.m_minNumObj = Integer.parseInt(minNumString);
/*  191:     */     } else {
/*  192: 554 */       this.m_minNumObj = 2;
/*  193:     */     }
/*  194: 556 */     this.m_binarySplits = Utils.getFlag('B', options);
/*  195: 557 */     this.m_useLaplace = Utils.getFlag('A', options);
/*  196: 558 */     this.m_useMDLcorrection = (!Utils.getFlag('J', options));
/*  197:     */     
/*  198:     */ 
/*  199: 561 */     this.m_unpruned = Utils.getFlag('U', options);
/*  200: 562 */     this.m_collapseTree = (!Utils.getFlag('O', options));
/*  201: 563 */     this.m_subtreeRaising = (!Utils.getFlag('S', options));
/*  202: 564 */     this.m_noCleanup = Utils.getFlag('L', options);
/*  203: 565 */     this.m_doNotMakeSplitPointActualValue = Utils.getFlag("doNotMakeSplitPointActualValue", options);
/*  204: 567 */     if ((this.m_unpruned) && (!this.m_subtreeRaising)) {
/*  205: 568 */       throw new Exception("Subtree raising doesn't need to be unset for unpruned tree!");
/*  206:     */     }
/*  207: 571 */     this.m_reducedErrorPruning = Utils.getFlag('R', options);
/*  208: 572 */     if ((this.m_unpruned) && (this.m_reducedErrorPruning)) {
/*  209: 573 */       throw new Exception("Unpruned tree and reduced error pruning can't be selected simultaneously!");
/*  210:     */     }
/*  211: 577 */     String confidenceString = Utils.getOption('C', options);
/*  212: 578 */     if (confidenceString.length() != 0)
/*  213:     */     {
/*  214: 579 */       if (this.m_reducedErrorPruning) {
/*  215: 580 */         throw new Exception("Setting the confidence doesn't make sense for reduced error pruning.");
/*  216:     */       }
/*  217: 582 */       if (this.m_unpruned) {
/*  218: 583 */         throw new Exception("Doesn't make sense to change confidence for unpruned tree!");
/*  219:     */       }
/*  220: 586 */       this.m_CF = new Float(confidenceString).floatValue();
/*  221: 587 */       if ((this.m_CF <= 0.0F) || (this.m_CF >= 1.0F)) {
/*  222: 588 */         throw new Exception("Confidence has to be greater than zero and smaller than one!");
/*  223:     */       }
/*  224:     */     }
/*  225:     */     else
/*  226:     */     {
/*  227: 593 */       this.m_CF = 0.25F;
/*  228:     */     }
/*  229: 595 */     String numFoldsString = Utils.getOption('N', options);
/*  230: 596 */     if (numFoldsString.length() != 0)
/*  231:     */     {
/*  232: 597 */       if (!this.m_reducedErrorPruning) {
/*  233: 598 */         throw new Exception("Setting the number of folds doesn't make sense if reduced error pruning is not selected.");
/*  234:     */       }
/*  235: 602 */       this.m_numFolds = Integer.parseInt(numFoldsString);
/*  236:     */     }
/*  237:     */     else
/*  238:     */     {
/*  239: 605 */       this.m_numFolds = 3;
/*  240:     */     }
/*  241: 607 */     String seedString = Utils.getOption('Q', options);
/*  242: 608 */     if (seedString.length() != 0) {
/*  243: 609 */       this.m_Seed = Integer.parseInt(seedString);
/*  244:     */     } else {
/*  245: 611 */       this.m_Seed = 1;
/*  246:     */     }
/*  247: 614 */     super.setOptions(options);
/*  248:     */     
/*  249: 616 */     Utils.checkForRemainingOptions(options);
/*  250:     */   }
/*  251:     */   
/*  252:     */   public String[] getOptions()
/*  253:     */   {
/*  254: 627 */     Vector<String> options = new Vector();
/*  255: 629 */     if (this.m_noCleanup) {
/*  256: 630 */       options.add("-L");
/*  257:     */     }
/*  258: 632 */     if (!this.m_collapseTree) {
/*  259: 633 */       options.add("-O");
/*  260:     */     }
/*  261: 635 */     if (this.m_unpruned)
/*  262:     */     {
/*  263: 636 */       options.add("-U");
/*  264:     */     }
/*  265:     */     else
/*  266:     */     {
/*  267: 638 */       if (!this.m_subtreeRaising) {
/*  268: 639 */         options.add("-S");
/*  269:     */       }
/*  270: 641 */       if (this.m_reducedErrorPruning)
/*  271:     */       {
/*  272: 642 */         options.add("-R");
/*  273: 643 */         options.add("-N");
/*  274: 644 */         options.add("" + this.m_numFolds);
/*  275: 645 */         options.add("-Q");
/*  276: 646 */         options.add("" + this.m_Seed);
/*  277:     */       }
/*  278:     */       else
/*  279:     */       {
/*  280: 648 */         options.add("-C");
/*  281: 649 */         options.add("" + this.m_CF);
/*  282:     */       }
/*  283:     */     }
/*  284: 652 */     if (this.m_binarySplits) {
/*  285: 653 */       options.add("-B");
/*  286:     */     }
/*  287: 655 */     options.add("-M");
/*  288: 656 */     options.add("" + this.m_minNumObj);
/*  289: 657 */     if (this.m_useLaplace) {
/*  290: 658 */       options.add("-A");
/*  291:     */     }
/*  292: 660 */     if (!this.m_useMDLcorrection) {
/*  293: 661 */       options.add("-J");
/*  294:     */     }
/*  295: 663 */     if (this.m_doNotMakeSplitPointActualValue) {
/*  296: 664 */       options.add("-doNotMakeSplitPointActualValue");
/*  297:     */     }
/*  298: 667 */     Collections.addAll(options, super.getOptions());
/*  299:     */     
/*  300: 669 */     return (String[])options.toArray(new String[0]);
/*  301:     */   }
/*  302:     */   
/*  303:     */   public String seedTipText()
/*  304:     */   {
/*  305: 679 */     return "The seed used for randomizing the data when reduced-error pruning is used.";
/*  306:     */   }
/*  307:     */   
/*  308:     */   public int getSeed()
/*  309:     */   {
/*  310: 690 */     return this.m_Seed;
/*  311:     */   }
/*  312:     */   
/*  313:     */   public void setSeed(int newSeed)
/*  314:     */   {
/*  315: 700 */     this.m_Seed = newSeed;
/*  316:     */   }
/*  317:     */   
/*  318:     */   public String useLaplaceTipText()
/*  319:     */   {
/*  320: 710 */     return "Whether counts at leaves are smoothed based on Laplace.";
/*  321:     */   }
/*  322:     */   
/*  323:     */   public boolean getUseLaplace()
/*  324:     */   {
/*  325: 720 */     return this.m_useLaplace;
/*  326:     */   }
/*  327:     */   
/*  328:     */   public void setUseLaplace(boolean newuseLaplace)
/*  329:     */   {
/*  330: 730 */     this.m_useLaplace = newuseLaplace;
/*  331:     */   }
/*  332:     */   
/*  333:     */   public String useMDLcorrectionTipText()
/*  334:     */   {
/*  335: 740 */     return "Whether MDL correction is used when finding splits on numeric attributes.";
/*  336:     */   }
/*  337:     */   
/*  338:     */   public boolean getUseMDLcorrection()
/*  339:     */   {
/*  340: 750 */     return this.m_useMDLcorrection;
/*  341:     */   }
/*  342:     */   
/*  343:     */   public void setUseMDLcorrection(boolean newuseMDLcorrection)
/*  344:     */   {
/*  345: 760 */     this.m_useMDLcorrection = newuseMDLcorrection;
/*  346:     */   }
/*  347:     */   
/*  348:     */   public String toString()
/*  349:     */   {
/*  350: 771 */     if (this.m_root == null) {
/*  351: 772 */       return "No classifier built";
/*  352:     */     }
/*  353: 774 */     if (this.m_unpruned) {
/*  354: 775 */       return "J48 unpruned tree\n------------------\n" + this.m_root.toString();
/*  355:     */     }
/*  356: 777 */     return "J48 pruned tree\n------------------\n" + this.m_root.toString();
/*  357:     */   }
/*  358:     */   
/*  359:     */   public String toSummaryString()
/*  360:     */   {
/*  361: 789 */     return "Number of leaves: " + this.m_root.numLeaves() + "\n" + "Size of the tree: " + this.m_root.numNodes() + "\n";
/*  362:     */   }
/*  363:     */   
/*  364:     */   public double measureTreeSize()
/*  365:     */   {
/*  366: 799 */     return this.m_root.numNodes();
/*  367:     */   }
/*  368:     */   
/*  369:     */   public double measureNumLeaves()
/*  370:     */   {
/*  371: 808 */     return this.m_root.numLeaves();
/*  372:     */   }
/*  373:     */   
/*  374:     */   public double measureNumRules()
/*  375:     */   {
/*  376: 817 */     return this.m_root.numLeaves();
/*  377:     */   }
/*  378:     */   
/*  379:     */   public Enumeration<String> enumerateMeasures()
/*  380:     */   {
/*  381: 827 */     Vector<String> newVector = new Vector(3);
/*  382: 828 */     newVector.addElement("measureTreeSize");
/*  383: 829 */     newVector.addElement("measureNumLeaves");
/*  384: 830 */     newVector.addElement("measureNumRules");
/*  385: 831 */     return newVector.elements();
/*  386:     */   }
/*  387:     */   
/*  388:     */   public double getMeasure(String additionalMeasureName)
/*  389:     */   {
/*  390: 843 */     if (additionalMeasureName.compareToIgnoreCase("measureNumRules") == 0) {
/*  391: 844 */       return measureNumRules();
/*  392:     */     }
/*  393: 845 */     if (additionalMeasureName.compareToIgnoreCase("measureTreeSize") == 0) {
/*  394: 846 */       return measureTreeSize();
/*  395:     */     }
/*  396: 847 */     if (additionalMeasureName.compareToIgnoreCase("measureNumLeaves") == 0) {
/*  397: 848 */       return measureNumLeaves();
/*  398:     */     }
/*  399: 850 */     throw new IllegalArgumentException(additionalMeasureName + " not supported (j48)");
/*  400:     */   }
/*  401:     */   
/*  402:     */   public String unprunedTipText()
/*  403:     */   {
/*  404: 862 */     return "Whether pruning is performed.";
/*  405:     */   }
/*  406:     */   
/*  407:     */   public boolean getUnpruned()
/*  408:     */   {
/*  409: 872 */     return this.m_unpruned;
/*  410:     */   }
/*  411:     */   
/*  412:     */   public void setUnpruned(boolean v)
/*  413:     */   {
/*  414: 882 */     if (v) {
/*  415: 883 */       this.m_reducedErrorPruning = false;
/*  416:     */     }
/*  417: 885 */     this.m_unpruned = v;
/*  418:     */   }
/*  419:     */   
/*  420:     */   public String collapseTreeTipText()
/*  421:     */   {
/*  422: 895 */     return "Whether parts are removed that do not reduce training error.";
/*  423:     */   }
/*  424:     */   
/*  425:     */   public boolean getCollapseTree()
/*  426:     */   {
/*  427: 905 */     return this.m_collapseTree;
/*  428:     */   }
/*  429:     */   
/*  430:     */   public void setCollapseTree(boolean v)
/*  431:     */   {
/*  432: 915 */     this.m_collapseTree = v;
/*  433:     */   }
/*  434:     */   
/*  435:     */   public String confidenceFactorTipText()
/*  436:     */   {
/*  437: 925 */     return "The confidence factor used for pruning (smaller values incur more pruning).";
/*  438:     */   }
/*  439:     */   
/*  440:     */   public float getConfidenceFactor()
/*  441:     */   {
/*  442: 936 */     return this.m_CF;
/*  443:     */   }
/*  444:     */   
/*  445:     */   public void setConfidenceFactor(float v)
/*  446:     */   {
/*  447: 946 */     this.m_CF = v;
/*  448:     */   }
/*  449:     */   
/*  450:     */   public String minNumObjTipText()
/*  451:     */   {
/*  452: 956 */     return "The minimum number of instances per leaf.";
/*  453:     */   }
/*  454:     */   
/*  455:     */   public int getMinNumObj()
/*  456:     */   {
/*  457: 966 */     return this.m_minNumObj;
/*  458:     */   }
/*  459:     */   
/*  460:     */   public void setMinNumObj(int v)
/*  461:     */   {
/*  462: 976 */     this.m_minNumObj = v;
/*  463:     */   }
/*  464:     */   
/*  465:     */   public String reducedErrorPruningTipText()
/*  466:     */   {
/*  467: 986 */     return "Whether reduced-error pruning is used instead of C.4.5 pruning.";
/*  468:     */   }
/*  469:     */   
/*  470:     */   public boolean getReducedErrorPruning()
/*  471:     */   {
/*  472: 996 */     return this.m_reducedErrorPruning;
/*  473:     */   }
/*  474:     */   
/*  475:     */   public void setReducedErrorPruning(boolean v)
/*  476:     */   {
/*  477:1006 */     if (v) {
/*  478:1007 */       this.m_unpruned = false;
/*  479:     */     }
/*  480:1009 */     this.m_reducedErrorPruning = v;
/*  481:     */   }
/*  482:     */   
/*  483:     */   public String numFoldsTipText()
/*  484:     */   {
/*  485:1019 */     return "Determines the amount of data used for reduced-error pruning.  One fold is used for pruning, the rest for growing the tree.";
/*  486:     */   }
/*  487:     */   
/*  488:     */   public int getNumFolds()
/*  489:     */   {
/*  490:1030 */     return this.m_numFolds;
/*  491:     */   }
/*  492:     */   
/*  493:     */   public void setNumFolds(int v)
/*  494:     */   {
/*  495:1040 */     this.m_numFolds = v;
/*  496:     */   }
/*  497:     */   
/*  498:     */   public String binarySplitsTipText()
/*  499:     */   {
/*  500:1050 */     return "Whether to use binary splits on nominal attributes when building the trees.";
/*  501:     */   }
/*  502:     */   
/*  503:     */   public boolean getBinarySplits()
/*  504:     */   {
/*  505:1061 */     return this.m_binarySplits;
/*  506:     */   }
/*  507:     */   
/*  508:     */   public void setBinarySplits(boolean v)
/*  509:     */   {
/*  510:1071 */     this.m_binarySplits = v;
/*  511:     */   }
/*  512:     */   
/*  513:     */   public String subtreeRaisingTipText()
/*  514:     */   {
/*  515:1081 */     return "Whether to consider the subtree raising operation when pruning.";
/*  516:     */   }
/*  517:     */   
/*  518:     */   public boolean getSubtreeRaising()
/*  519:     */   {
/*  520:1091 */     return this.m_subtreeRaising;
/*  521:     */   }
/*  522:     */   
/*  523:     */   public void setSubtreeRaising(boolean v)
/*  524:     */   {
/*  525:1101 */     this.m_subtreeRaising = v;
/*  526:     */   }
/*  527:     */   
/*  528:     */   public String saveInstanceDataTipText()
/*  529:     */   {
/*  530:1111 */     return "Whether to save the training data for visualization.";
/*  531:     */   }
/*  532:     */   
/*  533:     */   public boolean getSaveInstanceData()
/*  534:     */   {
/*  535:1121 */     return this.m_noCleanup;
/*  536:     */   }
/*  537:     */   
/*  538:     */   public void setSaveInstanceData(boolean v)
/*  539:     */   {
/*  540:1131 */     this.m_noCleanup = v;
/*  541:     */   }
/*  542:     */   
/*  543:     */   public String doNotMakeSplitPointActualValueTipText()
/*  544:     */   {
/*  545:1141 */     return "If true, the split point is not relocated to an actual data value. This can yield substantial speed-ups for large datasets with numeric attributes.";
/*  546:     */   }
/*  547:     */   
/*  548:     */   public boolean getDoNotMakeSplitPointActualValue()
/*  549:     */   {
/*  550:1151 */     return this.m_doNotMakeSplitPointActualValue;
/*  551:     */   }
/*  552:     */   
/*  553:     */   public void setDoNotMakeSplitPointActualValue(boolean m_doNotMakeSplitPointActualValue)
/*  554:     */   {
/*  555:1161 */     this.m_doNotMakeSplitPointActualValue = m_doNotMakeSplitPointActualValue;
/*  556:     */   }
/*  557:     */   
/*  558:     */   public String getRevision()
/*  559:     */   {
/*  560:1171 */     return RevisionUtils.extract("$Revision: 11194 $");
/*  561:     */   }
/*  562:     */   
/*  563:     */   public void generatePartition(Instances data)
/*  564:     */     throws Exception
/*  565:     */   {
/*  566:1180 */     buildClassifier(data);
/*  567:     */   }
/*  568:     */   
/*  569:     */   public double[] getMembershipValues(Instance inst)
/*  570:     */     throws Exception
/*  571:     */   {
/*  572:1189 */     return this.m_root.getMembershipValues(inst);
/*  573:     */   }
/*  574:     */   
/*  575:     */   public int numElements()
/*  576:     */     throws Exception
/*  577:     */   {
/*  578:1198 */     return this.m_root.numNodes();
/*  579:     */   }
/*  580:     */   
/*  581:     */   public static void main(String[] argv)
/*  582:     */   {
/*  583:1207 */     runClassifier(new J48(), argv);
/*  584:     */   }
/*  585:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.J48
 * JD-Core Version:    0.7.0.1
 */