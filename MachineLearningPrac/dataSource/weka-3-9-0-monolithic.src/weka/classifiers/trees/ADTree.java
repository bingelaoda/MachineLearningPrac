/*    1:     */ package weka.classifiers.trees;
/*    2:     */ 
/*    3:     */ import java.util.ArrayList;
/*    4:     */ import java.util.Collections;
/*    5:     */ import java.util.Enumeration;
/*    6:     */ import java.util.Random;
/*    7:     */ import java.util.Vector;
/*    8:     */ import weka.classifiers.IterativeClassifier;
/*    9:     */ import weka.classifiers.RandomizableClassifier;
/*   10:     */ import weka.classifiers.trees.adtree.PredictionNode;
/*   11:     */ import weka.classifiers.trees.adtree.ReferenceInstances;
/*   12:     */ import weka.classifiers.trees.adtree.Splitter;
/*   13:     */ import weka.classifiers.trees.adtree.TwoWayNominalSplit;
/*   14:     */ import weka.classifiers.trees.adtree.TwoWayNumericSplit;
/*   15:     */ import weka.core.AdditionalMeasureProducer;
/*   16:     */ import weka.core.Attribute;
/*   17:     */ import weka.core.Capabilities;
/*   18:     */ import weka.core.Capabilities.Capability;
/*   19:     */ import weka.core.Drawable;
/*   20:     */ import weka.core.Instance;
/*   21:     */ import weka.core.Instances;
/*   22:     */ import weka.core.Option;
/*   23:     */ import weka.core.OptionHandler;
/*   24:     */ import weka.core.RevisionUtils;
/*   25:     */ import weka.core.SelectedTag;
/*   26:     */ import weka.core.SerializedObject;
/*   27:     */ import weka.core.Tag;
/*   28:     */ import weka.core.TechnicalInformation;
/*   29:     */ import weka.core.TechnicalInformation.Field;
/*   30:     */ import weka.core.TechnicalInformation.Type;
/*   31:     */ import weka.core.TechnicalInformationHandler;
/*   32:     */ import weka.core.Utils;
/*   33:     */ import weka.core.WeightedInstancesHandler;
/*   34:     */ 
/*   35:     */ public class ADTree
/*   36:     */   extends RandomizableClassifier
/*   37:     */   implements OptionHandler, Drawable, AdditionalMeasureProducer, WeightedInstancesHandler, IterativeClassifier, TechnicalInformationHandler
/*   38:     */ {
/*   39:     */   static final long serialVersionUID = -1532264837167690683L;
/*   40:     */   public static final int SEARCHPATH_ALL = 0;
/*   41:     */   public static final int SEARCHPATH_HEAVIEST = 1;
/*   42:     */   public static final int SEARCHPATH_ZPURE = 2;
/*   43:     */   public static final int SEARCHPATH_RANDOM = 3;
/*   44:     */   
/*   45:     */   public String globalInfo()
/*   46:     */   {
/*   47: 129 */     return "Class for generating an alternating decision tree. The basic algorithm is based on:\n\n" + getTechnicalInformation().toString() + "\n\n" + "This version currently only supports two-class problems. The number of boosting " + "iterations needs to be manually tuned to suit the dataset and the desired " + "complexity/accuracy tradeoff. Induction of the trees has been optimized, and heuristic " + "search methods have been introduced to speed learning.";
/*   48:     */   }
/*   49:     */   
/*   50: 148 */   public static final Tag[] TAGS_SEARCHPATH = { new Tag(0, "Expand all paths"), new Tag(1, "Expand the heaviest path"), new Tag(2, "Expand the best z-pure path"), new Tag(3, "Expand a random path") };
/*   51:     */   protected Instances m_trainInstances;
/*   52: 158 */   protected PredictionNode m_root = null;
/*   53: 161 */   protected Random m_random = null;
/*   54: 164 */   protected int m_lastAddedSplitNum = 0;
/*   55:     */   protected int[] m_numericAttIndices;
/*   56:     */   protected int[] m_nominalAttIndices;
/*   57:     */   protected double m_trainTotalWeight;
/*   58:     */   protected ReferenceInstances m_posTrainInstances;
/*   59:     */   protected ReferenceInstances m_negTrainInstances;
/*   60:     */   protected PredictionNode m_search_bestInsertionNode;
/*   61:     */   protected Splitter m_search_bestSplitter;
/*   62:     */   protected double m_search_smallestZ;
/*   63:     */   protected Instances m_search_bestPathPosInstances;
/*   64:     */   protected Instances m_search_bestPathNegInstances;
/*   65: 203 */   protected int m_nodesExpanded = 0;
/*   66: 206 */   protected int m_examplesCounted = 0;
/*   67: 209 */   protected int m_boostingIterations = 10;
/*   68: 212 */   protected int m_searchPath = 0;
/*   69: 215 */   protected int m_randomSeed = 0;
/*   70: 218 */   protected boolean m_saveInstanceData = false;
/*   71:     */   
/*   72:     */   public TechnicalInformation getTechnicalInformation()
/*   73:     */   {
/*   74: 231 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*   75: 232 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Freund, Y. and Mason, L.");
/*   76: 233 */     result.setValue(TechnicalInformation.Field.YEAR, "1999");
/*   77: 234 */     result.setValue(TechnicalInformation.Field.TITLE, "The alternating decision tree learning algorithm");
/*   78:     */     
/*   79: 236 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Proceeding of the Sixteenth International Conference on Machine Learning");
/*   80:     */     
/*   81:     */ 
/*   82: 239 */     result.setValue(TechnicalInformation.Field.ADDRESS, "Bled, Slovenia");
/*   83: 240 */     result.setValue(TechnicalInformation.Field.PAGES, "124-133");
/*   84:     */     
/*   85: 242 */     return result;
/*   86:     */   }
/*   87:     */   
/*   88:     */   public void initializeClassifier(Instances instances)
/*   89:     */     throws Exception
/*   90:     */   {
/*   91: 255 */     this.m_nodesExpanded = 0;
/*   92: 256 */     this.m_examplesCounted = 0;
/*   93: 257 */     this.m_lastAddedSplitNum = 0;
/*   94:     */     
/*   95:     */ 
/*   96: 260 */     this.m_random = instances.getRandomNumberGenerator(this.m_Seed);
/*   97:     */     
/*   98:     */ 
/*   99: 263 */     this.m_trainInstances = new Instances(instances);
/*  100:     */     
/*  101:     */ 
/*  102: 266 */     this.m_posTrainInstances = new ReferenceInstances(this.m_trainInstances, this.m_trainInstances.numInstances());
/*  103:     */     
/*  104: 268 */     this.m_negTrainInstances = new ReferenceInstances(this.m_trainInstances, this.m_trainInstances.numInstances());
/*  105: 270 */     for (Instance instance : this.m_trainInstances)
/*  106:     */     {
/*  107: 271 */       Instance inst = instance;
/*  108: 272 */       if ((int)inst.classValue() == 0) {
/*  109: 273 */         this.m_negTrainInstances.addReference(inst);
/*  110:     */       } else {
/*  111: 275 */         this.m_posTrainInstances.addReference(inst);
/*  112:     */       }
/*  113:     */     }
/*  114: 278 */     this.m_posTrainInstances.compactify();
/*  115: 279 */     this.m_negTrainInstances.compactify();
/*  116:     */     
/*  117:     */ 
/*  118: 282 */     double rootPredictionValue = calcPredictionValue(this.m_posTrainInstances, this.m_negTrainInstances);
/*  119:     */     
/*  120: 284 */     this.m_root = new PredictionNode(rootPredictionValue);
/*  121:     */     
/*  122:     */ 
/*  123: 287 */     updateWeights(this.m_posTrainInstances, this.m_negTrainInstances, rootPredictionValue);
/*  124:     */     
/*  125:     */ 
/*  126: 290 */     generateAttributeIndicesSingle();
/*  127:     */   }
/*  128:     */   
/*  129:     */   public boolean next()
/*  130:     */     throws Exception
/*  131:     */   {
/*  132: 302 */     boost();
/*  133: 303 */     return true;
/*  134:     */   }
/*  135:     */   
/*  136:     */   public void boost()
/*  137:     */     throws Exception
/*  138:     */   {
/*  139: 316 */     if ((this.m_trainInstances == null) || (this.m_trainInstances.numInstances() == 0)) {
/*  140: 317 */       throw new Exception("Trying to boost with no training data");
/*  141:     */     }
/*  142: 321 */     searchForBestTestSingle();
/*  143: 323 */     if (this.m_search_bestSplitter == null) {
/*  144: 324 */       return;
/*  145:     */     }
/*  146: 328 */     for (int i = 0; i < 2; i++)
/*  147:     */     {
/*  148: 329 */       Instances posInstances = this.m_search_bestSplitter.instancesDownBranch(i, this.m_search_bestPathPosInstances);
/*  149:     */       
/*  150: 331 */       Instances negInstances = this.m_search_bestSplitter.instancesDownBranch(i, this.m_search_bestPathNegInstances);
/*  151:     */       
/*  152: 333 */       double predictionValue = calcPredictionValue(posInstances, negInstances);
/*  153: 334 */       PredictionNode newPredictor = new PredictionNode(predictionValue);
/*  154: 335 */       updateWeights(posInstances, negInstances, predictionValue);
/*  155: 336 */       this.m_search_bestSplitter.setChildForBranch(i, newPredictor);
/*  156:     */     }
/*  157: 340 */     this.m_search_bestInsertionNode.addChild(this.m_search_bestSplitter, this);
/*  158:     */     
/*  159:     */ 
/*  160: 343 */     this.m_search_bestPathPosInstances = null;
/*  161: 344 */     this.m_search_bestPathNegInstances = null;
/*  162: 345 */     this.m_search_bestSplitter = null;
/*  163:     */   }
/*  164:     */   
/*  165:     */   private void generateAttributeIndicesSingle()
/*  166:     */   {
/*  167: 356 */     ArrayList<Integer> nominalIndices = new ArrayList();
/*  168: 357 */     ArrayList<Integer> numericIndices = new ArrayList();
/*  169: 359 */     for (int i = 0; i < this.m_trainInstances.numAttributes(); i++) {
/*  170: 360 */       if (i != this.m_trainInstances.classIndex()) {
/*  171: 361 */         if (this.m_trainInstances.attribute(i).isNumeric()) {
/*  172: 362 */           numericIndices.add(new Integer(i));
/*  173:     */         } else {
/*  174: 364 */           nominalIndices.add(new Integer(i));
/*  175:     */         }
/*  176:     */       }
/*  177:     */     }
/*  178: 370 */     this.m_nominalAttIndices = new int[nominalIndices.size()];
/*  179: 371 */     for (int i = 0; i < nominalIndices.size(); i++) {
/*  180: 372 */       this.m_nominalAttIndices[i] = ((Integer)nominalIndices.get(i)).intValue();
/*  181:     */     }
/*  182: 376 */     this.m_numericAttIndices = new int[numericIndices.size()];
/*  183: 377 */     for (int i = 0; i < numericIndices.size(); i++) {
/*  184: 378 */       this.m_numericAttIndices[i] = ((Integer)numericIndices.get(i)).intValue();
/*  185:     */     }
/*  186:     */   }
/*  187:     */   
/*  188:     */   private void searchForBestTestSingle()
/*  189:     */     throws Exception
/*  190:     */   {
/*  191: 391 */     this.m_trainTotalWeight = this.m_trainInstances.sumOfWeights();
/*  192:     */     
/*  193: 393 */     this.m_search_smallestZ = (1.0D / 0.0D);
/*  194: 394 */     searchForBestTestSingle(this.m_root, this.m_posTrainInstances, this.m_negTrainInstances);
/*  195:     */   }
/*  196:     */   
/*  197:     */   private void searchForBestTestSingle(PredictionNode currentNode, Instances posInstances, Instances negInstances)
/*  198:     */     throws Exception
/*  199:     */   {
/*  200: 412 */     if ((posInstances.numInstances() == 0) || (negInstances.numInstances() == 0)) {
/*  201: 413 */       return;
/*  202:     */     }
/*  203: 417 */     if (calcZpure(posInstances, negInstances) >= this.m_search_smallestZ) {
/*  204: 418 */       return;
/*  205:     */     }
/*  206: 422 */     this.m_nodesExpanded += 1;
/*  207: 423 */     this.m_examplesCounted += posInstances.numInstances() + negInstances.numInstances();
/*  208: 427 */     for (int m_nominalAttIndice : this.m_nominalAttIndices) {
/*  209: 428 */       evaluateNominalSplitSingle(m_nominalAttIndice, currentNode, posInstances, negInstances);
/*  210:     */     }
/*  211: 433 */     if (this.m_numericAttIndices.length > 0)
/*  212:     */     {
/*  213: 436 */       Instances allInstances = new Instances(posInstances);
/*  214: 437 */       for (Instance instance : negInstances) {
/*  215: 438 */         allInstances.add(instance);
/*  216:     */       }
/*  217: 442 */       for (int m_numericAttIndice : this.m_numericAttIndices) {
/*  218: 443 */         evaluateNumericSplitSingle(m_numericAttIndice, currentNode, posInstances, negInstances, allInstances);
/*  219:     */       }
/*  220:     */     }
/*  221: 448 */     if (currentNode.getChildren().size() == 0) {
/*  222: 449 */       return;
/*  223:     */     }
/*  224: 453 */     switch (this.m_searchPath)
/*  225:     */     {
/*  226:     */     case 0: 
/*  227: 455 */       goDownAllPathsSingle(currentNode, posInstances, negInstances);
/*  228: 456 */       break;
/*  229:     */     case 1: 
/*  230: 458 */       goDownHeaviestPathSingle(currentNode, posInstances, negInstances);
/*  231: 459 */       break;
/*  232:     */     case 2: 
/*  233: 461 */       goDownZpurePathSingle(currentNode, posInstances, negInstances);
/*  234: 462 */       break;
/*  235:     */     case 3: 
/*  236: 464 */       goDownRandomPathSingle(currentNode, posInstances, negInstances);
/*  237:     */     }
/*  238:     */   }
/*  239:     */   
/*  240:     */   private void goDownAllPathsSingle(PredictionNode currentNode, Instances posInstances, Instances negInstances)
/*  241:     */     throws Exception
/*  242:     */   {
/*  243: 481 */     for (Enumeration<Splitter> e = currentNode.children(); e.hasMoreElements();)
/*  244:     */     {
/*  245: 482 */       Splitter split = (Splitter)e.nextElement();
/*  246: 483 */       for (int i = 0; i < split.getNumOfBranches(); i++) {
/*  247: 484 */         searchForBestTestSingle(split.getChildForBranch(i), split.instancesDownBranch(i, posInstances), split.instancesDownBranch(i, negInstances));
/*  248:     */       }
/*  249:     */     }
/*  250:     */   }
/*  251:     */   
/*  252:     */   private void goDownHeaviestPathSingle(PredictionNode currentNode, Instances posInstances, Instances negInstances)
/*  253:     */     throws Exception
/*  254:     */   {
/*  255: 503 */     Splitter heaviestSplit = null;
/*  256: 504 */     int heaviestBranch = 0;
/*  257: 505 */     double largestWeight = 0.0D;
/*  258: 506 */     for (Enumeration<Splitter> e = currentNode.children(); e.hasMoreElements();)
/*  259:     */     {
/*  260: 507 */       Splitter split = (Splitter)e.nextElement();
/*  261: 508 */       for (int i = 0; i < split.getNumOfBranches(); i++)
/*  262:     */       {
/*  263: 509 */         double weight = split.instancesDownBranch(i, posInstances).sumOfWeights() + split.instancesDownBranch(i, negInstances).sumOfWeights();
/*  264: 512 */         if (weight > largestWeight)
/*  265:     */         {
/*  266: 513 */           heaviestSplit = split;
/*  267: 514 */           heaviestBranch = i;
/*  268: 515 */           largestWeight = weight;
/*  269:     */         }
/*  270:     */       }
/*  271:     */     }
/*  272: 519 */     if (heaviestSplit != null) {
/*  273: 520 */       searchForBestTestSingle(heaviestSplit.getChildForBranch(heaviestBranch), heaviestSplit.instancesDownBranch(heaviestBranch, posInstances), heaviestSplit.instancesDownBranch(heaviestBranch, negInstances));
/*  274:     */     }
/*  275:     */   }
/*  276:     */   
/*  277:     */   private void goDownZpurePathSingle(PredictionNode currentNode, Instances posInstances, Instances negInstances)
/*  278:     */     throws Exception
/*  279:     */   {
/*  280: 538 */     double lowestZpure = this.m_search_smallestZ;
/*  281: 539 */     PredictionNode bestPath = null;
/*  282: 540 */     Instances bestPosSplit = null;Instances bestNegSplit = null;
/*  283: 543 */     for (Enumeration<Splitter> e = currentNode.children(); e.hasMoreElements();)
/*  284:     */     {
/*  285: 544 */       Splitter split = (Splitter)e.nextElement();
/*  286: 545 */       for (int i = 0; i < split.getNumOfBranches(); i++)
/*  287:     */       {
/*  288: 546 */         Instances posSplit = split.instancesDownBranch(i, posInstances);
/*  289: 547 */         Instances negSplit = split.instancesDownBranch(i, negInstances);
/*  290: 548 */         double newZpure = calcZpure(posSplit, negSplit);
/*  291: 549 */         if (newZpure < lowestZpure)
/*  292:     */         {
/*  293: 550 */           lowestZpure = newZpure;
/*  294: 551 */           bestPath = split.getChildForBranch(i);
/*  295: 552 */           bestPosSplit = posSplit;
/*  296: 553 */           bestNegSplit = negSplit;
/*  297:     */         }
/*  298:     */       }
/*  299:     */     }
/*  300: 558 */     if (bestPath != null) {
/*  301: 559 */       searchForBestTestSingle(bestPath, bestPosSplit, bestNegSplit);
/*  302:     */     }
/*  303:     */   }
/*  304:     */   
/*  305:     */   private void goDownRandomPathSingle(PredictionNode currentNode, Instances posInstances, Instances negInstances)
/*  306:     */     throws Exception
/*  307:     */   {
/*  308: 575 */     ArrayList<Splitter> children = currentNode.getChildren();
/*  309: 576 */     Splitter split = (Splitter)children.get(getRandom(children.size()));
/*  310: 577 */     int branch = getRandom(split.getNumOfBranches());
/*  311: 578 */     searchForBestTestSingle(split.getChildForBranch(branch), split.instancesDownBranch(branch, posInstances), split.instancesDownBranch(branch, negInstances));
/*  312:     */   }
/*  313:     */   
/*  314:     */   private void evaluateNominalSplitSingle(int attIndex, PredictionNode currentNode, Instances posInstances, Instances negInstances)
/*  315:     */   {
/*  316: 597 */     double[] indexAndZ = findLowestZNominalSplit(posInstances, negInstances, attIndex);
/*  317: 600 */     if (indexAndZ[1] < this.m_search_smallestZ)
/*  318:     */     {
/*  319: 601 */       this.m_search_smallestZ = indexAndZ[1];
/*  320: 602 */       this.m_search_bestInsertionNode = currentNode;
/*  321: 603 */       this.m_search_bestSplitter = new TwoWayNominalSplit(attIndex, (int)indexAndZ[0]);
/*  322:     */       
/*  323: 605 */       this.m_search_bestPathPosInstances = posInstances;
/*  324: 606 */       this.m_search_bestPathNegInstances = negInstances;
/*  325:     */     }
/*  326:     */   }
/*  327:     */   
/*  328:     */   private void evaluateNumericSplitSingle(int attIndex, PredictionNode currentNode, Instances posInstances, Instances negInstances, Instances allInstances)
/*  329:     */     throws Exception
/*  330:     */   {
/*  331: 628 */     double[] splitAndZ = findLowestZNumericSplit(allInstances, attIndex);
/*  332: 630 */     if (splitAndZ[1] < this.m_search_smallestZ)
/*  333:     */     {
/*  334: 631 */       this.m_search_smallestZ = splitAndZ[1];
/*  335: 632 */       this.m_search_bestInsertionNode = currentNode;
/*  336: 633 */       this.m_search_bestSplitter = new TwoWayNumericSplit(attIndex, splitAndZ[0]);
/*  337: 634 */       this.m_search_bestPathPosInstances = posInstances;
/*  338: 635 */       this.m_search_bestPathNegInstances = negInstances;
/*  339:     */     }
/*  340:     */   }
/*  341:     */   
/*  342:     */   private double calcPredictionValue(Instances posInstances, Instances negInstances)
/*  343:     */   {
/*  344: 649 */     return 0.5D * Math.log((posInstances.sumOfWeights() + 1.0D) / (negInstances.sumOfWeights() + 1.0D));
/*  345:     */   }
/*  346:     */   
/*  347:     */   private double calcZpure(Instances posInstances, Instances negInstances)
/*  348:     */   {
/*  349: 662 */     double posWeight = posInstances.sumOfWeights();
/*  350: 663 */     double negWeight = negInstances.sumOfWeights();
/*  351: 664 */     return 2.0D * (Math.sqrt(posWeight + 1.0D) + Math.sqrt(negWeight + 1.0D)) + (this.m_trainTotalWeight - (posWeight + negWeight));
/*  352:     */   }
/*  353:     */   
/*  354:     */   private void updateWeights(Instances posInstances, Instances negInstances, double predictionValue)
/*  355:     */   {
/*  356: 682 */     double weightMultiplier = Math.pow(2.718281828459045D, -predictionValue);
/*  357: 683 */     for (Instance instance : posInstances)
/*  358:     */     {
/*  359: 684 */       Instance inst = instance;
/*  360: 685 */       inst.setWeight(inst.weight() * weightMultiplier);
/*  361:     */     }
/*  362: 688 */     weightMultiplier = Math.pow(2.718281828459045D, predictionValue);
/*  363: 689 */     for (Instance instance : negInstances)
/*  364:     */     {
/*  365: 690 */       Instance inst = instance;
/*  366: 691 */       inst.setWeight(inst.weight() * weightMultiplier);
/*  367:     */     }
/*  368:     */   }
/*  369:     */   
/*  370:     */   private double[] findLowestZNominalSplit(Instances posInstances, Instances negInstances, int attIndex)
/*  371:     */   {
/*  372: 708 */     double lowestZ = 1.7976931348623157E+308D;
/*  373: 709 */     int bestIndex = 0;
/*  374:     */     
/*  375:     */ 
/*  376: 712 */     double[] posWeights = attributeValueWeights(posInstances, attIndex);
/*  377: 713 */     double[] negWeights = attributeValueWeights(negInstances, attIndex);
/*  378: 714 */     double posWeight = Utils.sum(posWeights);
/*  379: 715 */     double negWeight = Utils.sum(negWeights);
/*  380:     */     
/*  381: 717 */     int maxIndex = posWeights.length;
/*  382: 718 */     if (maxIndex == 2) {
/*  383: 719 */       maxIndex = 1;
/*  384:     */     }
/*  385: 722 */     for (int i = 0; i < maxIndex; i++)
/*  386:     */     {
/*  387: 724 */       double w1 = posWeights[i] + 1.0D;
/*  388: 725 */       double w2 = negWeights[i] + 1.0D;
/*  389: 726 */       double w3 = posWeight - w1 + 2.0D;
/*  390: 727 */       double w4 = negWeight - w2 + 2.0D;
/*  391: 728 */       double wRemainder = this.m_trainTotalWeight + 4.0D - (w1 + w2 + w3 + w4);
/*  392: 729 */       double newZ = 2.0D * (Math.sqrt(w1 * w2) + Math.sqrt(w3 * w4)) + wRemainder;
/*  393: 733 */       if (newZ < lowestZ)
/*  394:     */       {
/*  395: 734 */         lowestZ = newZ;
/*  396: 735 */         bestIndex = i;
/*  397:     */       }
/*  398:     */     }
/*  399: 740 */     double[] indexAndZ = new double[2];
/*  400: 741 */     indexAndZ[0] = bestIndex;
/*  401: 742 */     indexAndZ[1] = lowestZ;
/*  402: 743 */     return indexAndZ;
/*  403:     */   }
/*  404:     */   
/*  405:     */   private double[] attributeValueWeights(Instances instances, int attIndex)
/*  406:     */   {
/*  407: 755 */     double[] weights = new double[instances.attribute(attIndex).numValues()];
/*  408: 756 */     for (int i = 0; i < weights.length; i++) {
/*  409: 757 */       weights[i] = 0.0D;
/*  410:     */     }
/*  411: 760 */     for (Instance instance : instances)
/*  412:     */     {
/*  413: 761 */       Instance inst = instance;
/*  414: 762 */       if (!inst.isMissing(attIndex)) {
/*  415: 763 */         weights[((int)inst.value(attIndex))] += inst.weight();
/*  416:     */       }
/*  417:     */     }
/*  418: 766 */     return weights;
/*  419:     */   }
/*  420:     */   
/*  421:     */   private double[] findLowestZNumericSplit(Instances instances, int attIndex)
/*  422:     */     throws Exception
/*  423:     */   {
/*  424: 781 */     double splitPoint = 0.0D;
/*  425: 782 */     double bestVal = 1.7976931348623157E+308D;
/*  426: 783 */     int numMissing = 0;
/*  427: 784 */     double[][] distribution = new double[3][instances.numClasses()];
/*  428: 787 */     for (int i = 0; i < instances.numInstances(); i++)
/*  429:     */     {
/*  430: 788 */       Instance inst = instances.instance(i);
/*  431: 789 */       if (!inst.isMissing(attIndex))
/*  432:     */       {
/*  433: 790 */         distribution[1][((int)inst.classValue())] += inst.weight();
/*  434:     */       }
/*  435:     */       else
/*  436:     */       {
/*  437: 792 */         distribution[2][((int)inst.classValue())] += inst.weight();
/*  438: 793 */         numMissing++;
/*  439:     */       }
/*  440:     */     }
/*  441: 798 */     instances.sort(attIndex);
/*  442: 801 */     for (int i = 0; i < instances.numInstances() - (numMissing + 1); i++)
/*  443:     */     {
/*  444: 802 */       Instance inst = instances.instance(i);
/*  445: 803 */       Instance instPlusOne = instances.instance(i + 1);
/*  446: 804 */       distribution[0][((int)inst.classValue())] += inst.weight();
/*  447: 805 */       distribution[1][((int)inst.classValue())] -= inst.weight();
/*  448: 806 */       if (Utils.sm(inst.value(attIndex), instPlusOne.value(attIndex)))
/*  449:     */       {
/*  450: 807 */         double currCutPoint = (inst.value(attIndex) + instPlusOne.value(attIndex)) / 2.0D;
/*  451: 808 */         double currVal = conditionedZOnRows(distribution);
/*  452: 809 */         if (currVal < bestVal)
/*  453:     */         {
/*  454: 810 */           splitPoint = currCutPoint;
/*  455: 811 */           bestVal = currVal;
/*  456:     */         }
/*  457:     */       }
/*  458:     */     }
/*  459: 816 */     double[] splitAndZ = new double[2];
/*  460: 817 */     splitAndZ[0] = splitPoint;
/*  461: 818 */     splitAndZ[1] = bestVal;
/*  462: 819 */     return splitAndZ;
/*  463:     */   }
/*  464:     */   
/*  465:     */   private double conditionedZOnRows(double[][] distribution)
/*  466:     */   {
/*  467: 830 */     double w1 = distribution[0][0] + 1.0D;
/*  468: 831 */     double w2 = distribution[0][1] + 1.0D;
/*  469: 832 */     double w3 = distribution[1][0] + 1.0D;
/*  470: 833 */     double w4 = distribution[1][1] + 1.0D;
/*  471: 834 */     double wRemainder = this.m_trainTotalWeight + 4.0D - (w1 + w2 + w3 + w4);
/*  472: 835 */     return 2.0D * (Math.sqrt(w1 * w2) + Math.sqrt(w3 * w4)) + wRemainder;
/*  473:     */   }
/*  474:     */   
/*  475:     */   public double[] distributionForInstance(Instance instance)
/*  476:     */   {
/*  477: 847 */     double predVal = predictionValueForInstance(instance, this.m_root, 0.0D);
/*  478:     */     
/*  479: 849 */     double[] distribution = new double[2];
/*  480: 850 */     distribution[0] = (1.0D / (1.0D + Math.pow(2.718281828459045D, predVal)));
/*  481: 851 */     distribution[1] = (1.0D / (1.0D + Math.pow(2.718281828459045D, -predVal)));
/*  482:     */     
/*  483: 853 */     return distribution;
/*  484:     */   }
/*  485:     */   
/*  486:     */   protected double predictionValueForInstance(Instance inst, PredictionNode currentNode, double currentValue)
/*  487:     */   {
/*  488: 868 */     currentValue += currentNode.getValue();
/*  489: 869 */     for (Enumeration<Splitter> e = currentNode.children(); e.hasMoreElements();)
/*  490:     */     {
/*  491: 870 */       Splitter split = (Splitter)e.nextElement();
/*  492: 871 */       int branch = split.branchInstanceGoesDown(inst);
/*  493: 872 */       if (branch >= 0) {
/*  494: 873 */         currentValue = predictionValueForInstance(inst, split.getChildForBranch(branch), currentValue);
/*  495:     */       }
/*  496:     */     }
/*  497: 877 */     return currentValue;
/*  498:     */   }
/*  499:     */   
/*  500:     */   public String toString()
/*  501:     */   {
/*  502: 888 */     if (this.m_root == null) {
/*  503: 889 */       return "ADTree not built yet";
/*  504:     */     }
/*  505: 891 */     return "Alternating decision tree:\n\n" + toString(this.m_root, 1) + "\nLegend: " + legend() + "\nTree size (total number of nodes): " + numOfAllNodes(this.m_root) + "\nLeaves (number of predictor nodes): " + numOfPredictionNodes(this.m_root);
/*  506:     */   }
/*  507:     */   
/*  508:     */   protected String toString(PredictionNode currentNode, int level)
/*  509:     */   {
/*  510: 906 */     StringBuffer text = new StringBuffer();
/*  511:     */     
/*  512: 908 */     text.append(": " + Utils.doubleToString(currentNode.getValue(), 3));
/*  513: 910 */     for (Enumeration<Splitter> e = currentNode.children(); e.hasMoreElements();)
/*  514:     */     {
/*  515: 911 */       Splitter split = (Splitter)e.nextElement();
/*  516: 913 */       for (int j = 0; j < split.getNumOfBranches(); j++)
/*  517:     */       {
/*  518: 914 */         PredictionNode child = split.getChildForBranch(j);
/*  519: 915 */         if (child != null)
/*  520:     */         {
/*  521: 916 */           text.append("\n");
/*  522: 917 */           for (int k = 0; k < level; k++) {
/*  523: 918 */             text.append("|  ");
/*  524:     */           }
/*  525: 920 */           text.append("(" + split.orderAdded + ")");
/*  526: 921 */           text.append(split.attributeString(this.m_trainInstances) + " " + split.comparisonString(j, this.m_trainInstances));
/*  527:     */           
/*  528: 923 */           text.append(toString(child, level + 1));
/*  529:     */         }
/*  530:     */       }
/*  531:     */     }
/*  532: 927 */     return text.toString();
/*  533:     */   }
/*  534:     */   
/*  535:     */   public int graphType()
/*  536:     */   {
/*  537: 937 */     return 1;
/*  538:     */   }
/*  539:     */   
/*  540:     */   public String graph()
/*  541:     */     throws Exception
/*  542:     */   {
/*  543: 949 */     StringBuffer text = new StringBuffer();
/*  544: 950 */     text.append("digraph ADTree {\n");
/*  545: 951 */     graphTraverse(this.m_root, text, 0, 0, this.m_trainInstances);
/*  546: 952 */     return text.toString() + "}\n";
/*  547:     */   }
/*  548:     */   
/*  549:     */   protected void graphTraverse(PredictionNode currentNode, StringBuffer text, int splitOrder, int predOrder, Instances instances)
/*  550:     */     throws Exception
/*  551:     */   {
/*  552: 968 */     text.append("S" + splitOrder + "P" + predOrder + " [label=\"");
/*  553: 969 */     text.append(Utils.doubleToString(currentNode.getValue(), 3));
/*  554: 970 */     if (splitOrder == 0) {
/*  555: 971 */       text.append(" (" + legend() + ")");
/*  556:     */     }
/*  557: 973 */     text.append("\" shape=box style=filled");
/*  558: 974 */     if (instances.numInstances() > 0) {
/*  559: 975 */       text.append(" data=\n" + instances + "\n,\n");
/*  560:     */     }
/*  561: 977 */     text.append("]\n");
/*  562: 978 */     for (Enumeration<Splitter> e = currentNode.children(); e.hasMoreElements();)
/*  563:     */     {
/*  564: 979 */       Splitter split = (Splitter)e.nextElement();
/*  565: 980 */       text.append("S" + splitOrder + "P" + predOrder + "->" + "S" + split.orderAdded + " [style=dotted]\n");
/*  566:     */       
/*  567: 982 */       text.append("S" + split.orderAdded + " [label=\"" + split.orderAdded + ": " + Utils.backQuoteChars(split.attributeString(this.m_trainInstances)) + "\"]\n");
/*  568: 986 */       for (int i = 0; i < split.getNumOfBranches(); i++)
/*  569:     */       {
/*  570: 987 */         PredictionNode child = split.getChildForBranch(i);
/*  571: 988 */         if (child != null)
/*  572:     */         {
/*  573: 989 */           text.append("S" + split.orderAdded + "->" + "S" + split.orderAdded + "P" + i + " [label=\"" + Utils.backQuoteChars(split.comparisonString(i, this.m_trainInstances)) + "\"]\n");
/*  574:     */           
/*  575:     */ 
/*  576:     */ 
/*  577: 993 */           graphTraverse(child, text, split.orderAdded, i, split.instancesDownBranch(i, instances));
/*  578:     */         }
/*  579:     */       }
/*  580:     */     }
/*  581:     */   }
/*  582:     */   
/*  583:     */   public String legend()
/*  584:     */   {
/*  585:1008 */     Attribute classAttribute = null;
/*  586:1009 */     if (this.m_trainInstances == null) {
/*  587:1010 */       return "";
/*  588:     */     }
/*  589:     */     try
/*  590:     */     {
/*  591:1013 */       classAttribute = this.m_trainInstances.classAttribute();
/*  592:     */     }
/*  593:     */     catch (Exception x) {}
/*  594:1017 */     return "-ve = " + classAttribute.value(0) + ", +ve = " + classAttribute.value(1);
/*  595:     */   }
/*  596:     */   
/*  597:     */   public String numOfBoostingIterationsTipText()
/*  598:     */   {
/*  599:1027 */     return "Sets the number of boosting iterations to perform. You will need to manually tune this parameter to suit the dataset and the desired complexity/accuracy tradeoff. More boosting iterations will result in larger (potentially more  accurate) trees, but will make learning slower. Each iteration will add 3 nodes (1 split + 2 prediction) to the tree unless merging occurs.";
/*  600:     */   }
/*  601:     */   
/*  602:     */   public int getNumOfBoostingIterations()
/*  603:     */   {
/*  604:1041 */     return this.m_boostingIterations;
/*  605:     */   }
/*  606:     */   
/*  607:     */   public void setNumOfBoostingIterations(int b)
/*  608:     */   {
/*  609:1051 */     this.m_boostingIterations = b;
/*  610:     */   }
/*  611:     */   
/*  612:     */   public String searchPathTipText()
/*  613:     */   {
/*  614:1060 */     return "Sets the type of search to perform when building the tree. The default option (Expand all paths) will do an exhaustive search. The other search methods are heuristic, so they are not guaranteed to find an optimal solution but they are much faster. Expand the heaviest path: searches the path with the most heavily weighted instances. Expand the best z-pure path: searches the path determined by the best z-pure estimate. Expand a random path: the fastest method, simply searches down a single random path on each iteration.";
/*  615:     */   }
/*  616:     */   
/*  617:     */   public SelectedTag getSearchPath()
/*  618:     */   {
/*  619:1077 */     return new SelectedTag(this.m_searchPath, TAGS_SEARCHPATH);
/*  620:     */   }
/*  621:     */   
/*  622:     */   public void setSearchPath(SelectedTag newMethod)
/*  623:     */   {
/*  624:1088 */     if (newMethod.getTags() == TAGS_SEARCHPATH) {
/*  625:1089 */       this.m_searchPath = newMethod.getSelectedTag().getID();
/*  626:     */     }
/*  627:     */   }
/*  628:     */   
/*  629:     */   public String saveInstanceDataTipText()
/*  630:     */   {
/*  631:1099 */     return "Sets whether the tree is to save instance data - the model will take up more memory if it does. If enabled you will be able to visualize the instances at the prediction nodes when visualizing the tree.";
/*  632:     */   }
/*  633:     */   
/*  634:     */   public boolean getSaveInstanceData()
/*  635:     */   {
/*  636:1111 */     return this.m_saveInstanceData;
/*  637:     */   }
/*  638:     */   
/*  639:     */   public void setSaveInstanceData(boolean v)
/*  640:     */   {
/*  641:1121 */     this.m_saveInstanceData = v;
/*  642:     */   }
/*  643:     */   
/*  644:     */   public Enumeration<Option> listOptions()
/*  645:     */   {
/*  646:1132 */     Vector<Option> newVector = new Vector(3);
/*  647:1133 */     newVector.addElement(new Option("\tNumber of boosting iterations.\n\t(Default = 10)", "B", 1, "-B <number of boosting iterations>"));
/*  648:     */     
/*  649:1135 */     newVector.addElement(new Option("\tExpand nodes: -3(all), -2(weight), -1(z_pure), >=0 seed for random walk\n\t(Default = -3)", "E", 1, "-E <-3|-2|-1|>=0>"));
/*  650:     */     
/*  651:     */ 
/*  652:     */ 
/*  653:1139 */     newVector.addElement(new Option("\tSave the instance data with the model", "D", 0, "-D"));
/*  654:     */     
/*  655:     */ 
/*  656:1142 */     newVector.addAll(Collections.list(super.listOptions()));
/*  657:     */     
/*  658:1144 */     return newVector.elements();
/*  659:     */   }
/*  660:     */   
/*  661:     */   public void setOptions(String[] options)
/*  662:     */     throws Exception
/*  663:     */   {
/*  664:1170 */     String bString = Utils.getOption('B', options);
/*  665:1171 */     if (bString.length() != 0) {
/*  666:1172 */       setNumOfBoostingIterations(Integer.parseInt(bString));
/*  667:     */     }
/*  668:1175 */     String eString = Utils.getOption('E', options);
/*  669:1176 */     if (eString.length() != 0)
/*  670:     */     {
/*  671:1177 */       int value = Integer.parseInt(eString);
/*  672:1178 */       if (value >= 0)
/*  673:     */       {
/*  674:1179 */         setSearchPath(new SelectedTag(3, TAGS_SEARCHPATH));
/*  675:1180 */         setSeed(value);
/*  676:     */       }
/*  677:     */       else
/*  678:     */       {
/*  679:1182 */         setSearchPath(new SelectedTag(value + 3, TAGS_SEARCHPATH));
/*  680:     */       }
/*  681:     */     }
/*  682:1186 */     setSaveInstanceData(Utils.getFlag('D', options));
/*  683:     */     
/*  684:1188 */     super.setOptions(options);
/*  685:     */     
/*  686:1190 */     Utils.checkForRemainingOptions(options);
/*  687:     */   }
/*  688:     */   
/*  689:     */   public String[] getOptions()
/*  690:     */   {
/*  691:1201 */     ArrayList<String> options = new ArrayList();
/*  692:     */     
/*  693:1203 */     options.add("-B");
/*  694:1204 */     options.add("" + getNumOfBoostingIterations());
/*  695:1205 */     options.add("-E");
/*  696:1206 */     options.add("" + (this.m_searchPath == 3 ? this.m_Seed : this.m_searchPath - 3));
/*  697:1208 */     if (getSaveInstanceData()) {
/*  698:1209 */       options.add("-D");
/*  699:     */     }
/*  700:1212 */     Collections.addAll(options, super.getOptions());
/*  701:     */     
/*  702:1214 */     return (String[])options.toArray(new String[0]);
/*  703:     */   }
/*  704:     */   
/*  705:     */   public double measureTreeSize()
/*  706:     */   {
/*  707:1224 */     return numOfAllNodes(this.m_root);
/*  708:     */   }
/*  709:     */   
/*  710:     */   public double measureNumLeaves()
/*  711:     */   {
/*  712:1234 */     return numOfPredictionNodes(this.m_root);
/*  713:     */   }
/*  714:     */   
/*  715:     */   public double measureNumPredictionLeaves()
/*  716:     */   {
/*  717:1245 */     return numOfPredictionLeafNodes(this.m_root);
/*  718:     */   }
/*  719:     */   
/*  720:     */   public double measureNodesExpanded()
/*  721:     */   {
/*  722:1255 */     return this.m_nodesExpanded;
/*  723:     */   }
/*  724:     */   
/*  725:     */   public double measureExamplesProcessed()
/*  726:     */   {
/*  727:1266 */     return this.m_examplesCounted;
/*  728:     */   }
/*  729:     */   
/*  730:     */   public Enumeration<String> enumerateMeasures()
/*  731:     */   {
/*  732:1277 */     Vector<String> newVector = new Vector(5);
/*  733:1278 */     newVector.addElement("measureTreeSize");
/*  734:1279 */     newVector.addElement("measureNumLeaves");
/*  735:1280 */     newVector.addElement("measureNumPredictionLeaves");
/*  736:1281 */     newVector.addElement("measureNodesExpanded");
/*  737:1282 */     newVector.addElement("measureExamplesProcessed");
/*  738:1283 */     return newVector.elements();
/*  739:     */   }
/*  740:     */   
/*  741:     */   public double getMeasure(String additionalMeasureName)
/*  742:     */   {
/*  743:1296 */     if (additionalMeasureName.equalsIgnoreCase("measureTreeSize")) {
/*  744:1297 */       return measureTreeSize();
/*  745:     */     }
/*  746:1298 */     if (additionalMeasureName.equalsIgnoreCase("measureNumLeaves")) {
/*  747:1299 */       return measureNumLeaves();
/*  748:     */     }
/*  749:1300 */     if (additionalMeasureName.equalsIgnoreCase("measureNumPredictionLeaves")) {
/*  750:1302 */       return measureNumPredictionLeaves();
/*  751:     */     }
/*  752:1303 */     if (additionalMeasureName.equalsIgnoreCase("measureNodesExpanded")) {
/*  753:1304 */       return measureNodesExpanded();
/*  754:     */     }
/*  755:1305 */     if (additionalMeasureName.equalsIgnoreCase("measureExamplesProcessed")) {
/*  756:1307 */       return measureExamplesProcessed();
/*  757:     */     }
/*  758:1309 */     throw new IllegalArgumentException(additionalMeasureName + " not supported (ADTree)");
/*  759:     */   }
/*  760:     */   
/*  761:     */   protected int numOfAllNodes(PredictionNode root)
/*  762:     */   {
/*  763:1322 */     int numSoFar = 0;
/*  764:     */     Enumeration<Splitter> e;
/*  765:1323 */     if (root != null)
/*  766:     */     {
/*  767:1324 */       numSoFar++;
/*  768:1325 */       for (e = root.children(); e.hasMoreElements();)
/*  769:     */       {
/*  770:1326 */         numSoFar++;
/*  771:1327 */         Splitter split = (Splitter)e.nextElement();
/*  772:1328 */         for (int i = 0; i < split.getNumOfBranches(); i++) {
/*  773:1329 */           numSoFar += numOfAllNodes(split.getChildForBranch(i));
/*  774:     */         }
/*  775:     */       }
/*  776:     */     }
/*  777:1333 */     return numSoFar;
/*  778:     */   }
/*  779:     */   
/*  780:     */   protected int numOfPredictionNodes(PredictionNode root)
/*  781:     */   {
/*  782:1344 */     int numSoFar = 0;
/*  783:     */     Enumeration<Splitter> e;
/*  784:1345 */     if (root != null)
/*  785:     */     {
/*  786:1346 */       numSoFar++;
/*  787:1347 */       for (e = root.children(); e.hasMoreElements();)
/*  788:     */       {
/*  789:1348 */         Splitter split = (Splitter)e.nextElement();
/*  790:1349 */         for (int i = 0; i < split.getNumOfBranches(); i++) {
/*  791:1350 */           numSoFar += numOfPredictionNodes(split.getChildForBranch(i));
/*  792:     */         }
/*  793:     */       }
/*  794:     */     }
/*  795:1354 */     return numSoFar;
/*  796:     */   }
/*  797:     */   
/*  798:     */   protected int numOfPredictionLeafNodes(PredictionNode root)
/*  799:     */   {
/*  800:1366 */     int numSoFar = 0;
/*  801:     */     Enumeration<Splitter> e;
/*  802:1367 */     if (root.getChildren().size() > 0) {
/*  803:1368 */       for (e = root.children(); e.hasMoreElements();)
/*  804:     */       {
/*  805:1369 */         Splitter split = (Splitter)e.nextElement();
/*  806:1370 */         for (int i = 0; i < split.getNumOfBranches(); i++) {
/*  807:1371 */           numSoFar += numOfPredictionLeafNodes(split.getChildForBranch(i));
/*  808:     */         }
/*  809:     */       }
/*  810:     */     } else {
/*  811:1375 */       numSoFar = 1;
/*  812:     */     }
/*  813:1377 */     return numSoFar;
/*  814:     */   }
/*  815:     */   
/*  816:     */   protected int getRandom(int max)
/*  817:     */   {
/*  818:1388 */     return this.m_random.nextInt(max);
/*  819:     */   }
/*  820:     */   
/*  821:     */   public int nextSplitAddedOrder()
/*  822:     */   {
/*  823:1399 */     return ++this.m_lastAddedSplitNum;
/*  824:     */   }
/*  825:     */   
/*  826:     */   public Capabilities getCapabilities()
/*  827:     */   {
/*  828:1409 */     Capabilities result = super.getCapabilities();
/*  829:1410 */     result.disableAll();
/*  830:     */     
/*  831:     */ 
/*  832:1413 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  833:1414 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  834:1415 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  835:1416 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  836:     */     
/*  837:     */ 
/*  838:1419 */     result.enable(Capabilities.Capability.BINARY_CLASS);
/*  839:1420 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  840:     */     
/*  841:1422 */     return result;
/*  842:     */   }
/*  843:     */   
/*  844:     */   public void buildClassifier(Instances instances)
/*  845:     */     throws Exception
/*  846:     */   {
/*  847:1435 */     getCapabilities().testWithFail(instances);
/*  848:     */     
/*  849:     */ 
/*  850:1438 */     instances = new Instances(instances);
/*  851:1439 */     instances.deleteWithMissingClass();
/*  852:     */     
/*  853:     */ 
/*  854:1442 */     initializeClassifier(instances);
/*  855:1445 */     for (int T = 0; T < this.m_boostingIterations; T++) {
/*  856:1446 */       boost();
/*  857:     */     }
/*  858:1450 */     if (!this.m_saveInstanceData) {
/*  859:1451 */       done();
/*  860:     */     }
/*  861:     */   }
/*  862:     */   
/*  863:     */   public void done()
/*  864:     */   {
/*  865:1463 */     this.m_trainInstances = new Instances(this.m_trainInstances, 0);
/*  866:1464 */     this.m_random = null;
/*  867:1465 */     this.m_numericAttIndices = null;
/*  868:1466 */     this.m_nominalAttIndices = null;
/*  869:1467 */     this.m_posTrainInstances = null;
/*  870:1468 */     this.m_negTrainInstances = null;
/*  871:     */   }
/*  872:     */   
/*  873:     */   public Object clone()
/*  874:     */   {
/*  875:1483 */     ADTree clone = new ADTree();
/*  876:1485 */     if (this.m_root != null)
/*  877:     */     {
/*  878:1486 */       clone.m_root = ((PredictionNode)this.m_root.clone());
/*  879:     */       
/*  880:1488 */       clone.m_trainInstances = new Instances(this.m_trainInstances);
/*  881:1492 */       if (this.m_random != null)
/*  882:     */       {
/*  883:1493 */         SerializedObject randomSerial = null;
/*  884:     */         try
/*  885:     */         {
/*  886:1495 */           randomSerial = new SerializedObject(this.m_random);
/*  887:     */         }
/*  888:     */         catch (Exception ignored) {}
/*  889:1498 */         clone.m_random = ((Random)randomSerial.getObject());
/*  890:     */       }
/*  891:1501 */       clone.m_lastAddedSplitNum = this.m_lastAddedSplitNum;
/*  892:1502 */       clone.m_numericAttIndices = this.m_numericAttIndices;
/*  893:1503 */       clone.m_nominalAttIndices = this.m_nominalAttIndices;
/*  894:1504 */       clone.m_trainTotalWeight = this.m_trainTotalWeight;
/*  895:1507 */       if (this.m_posTrainInstances != null)
/*  896:     */       {
/*  897:1508 */         clone.m_posTrainInstances = new ReferenceInstances(this.m_trainInstances, this.m_posTrainInstances.numInstances());
/*  898:     */         
/*  899:1510 */         clone.m_negTrainInstances = new ReferenceInstances(this.m_trainInstances, this.m_negTrainInstances.numInstances());
/*  900:1512 */         for (Instance instance : clone.m_trainInstances)
/*  901:     */         {
/*  902:1513 */           Instance inst = instance;
/*  903:     */           try
/*  904:     */           {
/*  905:1515 */             if ((int)inst.classValue() == 0) {
/*  906:1516 */               clone.m_negTrainInstances.addReference(inst);
/*  907:     */             } else {
/*  908:1519 */               clone.m_posTrainInstances.addReference(inst);
/*  909:     */             }
/*  910:     */           }
/*  911:     */           catch (Exception ignored) {}
/*  912:     */         }
/*  913:     */       }
/*  914:     */     }
/*  915:1527 */     clone.m_nodesExpanded = this.m_nodesExpanded;
/*  916:1528 */     clone.m_examplesCounted = this.m_examplesCounted;
/*  917:1529 */     clone.m_boostingIterations = this.m_boostingIterations;
/*  918:1530 */     clone.m_searchPath = this.m_searchPath;
/*  919:1531 */     clone.m_Seed = this.m_Seed;
/*  920:     */     
/*  921:1533 */     return clone;
/*  922:     */   }
/*  923:     */   
/*  924:     */   public void merge(ADTree mergeWith)
/*  925:     */     throws Exception
/*  926:     */   {
/*  927:1547 */     if ((this.m_root == null) || (mergeWith.m_root == null)) {
/*  928:1548 */       throw new Exception("Trying to merge an uninitialized tree");
/*  929:     */     }
/*  930:1550 */     this.m_root.merge(mergeWith.m_root, this);
/*  931:     */   }
/*  932:     */   
/*  933:     */   public String getRevision()
/*  934:     */   {
/*  935:1560 */     return RevisionUtils.extract("$Revision: 10887 $");
/*  936:     */   }
/*  937:     */   
/*  938:     */   public static void main(String[] argv)
/*  939:     */   {
/*  940:1569 */     runClassifier(new ADTree(), argv);
/*  941:     */   }
/*  942:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.ADTree
 * JD-Core Version:    0.7.0.1
 */