/*    1:     */ package weka.classifiers.trees;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.util.Arrays;
/*    5:     */ import java.util.Collections;
/*    6:     */ import java.util.Enumeration;
/*    7:     */ import java.util.Random;
/*    8:     */ import java.util.Vector;
/*    9:     */ import weka.classifiers.Evaluation;
/*   10:     */ import weka.classifiers.RandomizableClassifier;
/*   11:     */ import weka.core.AdditionalMeasureProducer;
/*   12:     */ import weka.core.Attribute;
/*   13:     */ import weka.core.Capabilities;
/*   14:     */ import weka.core.Capabilities.Capability;
/*   15:     */ import weka.core.Instance;
/*   16:     */ import weka.core.Instances;
/*   17:     */ import weka.core.Option;
/*   18:     */ import weka.core.RevisionUtils;
/*   19:     */ import weka.core.TechnicalInformation;
/*   20:     */ import weka.core.TechnicalInformation.Field;
/*   21:     */ import weka.core.TechnicalInformation.Type;
/*   22:     */ import weka.core.TechnicalInformationHandler;
/*   23:     */ import weka.core.Utils;
/*   24:     */ import weka.core.matrix.EigenvalueDecomposition;
/*   25:     */ import weka.core.matrix.Matrix;
/*   26:     */ 
/*   27:     */ public class SimpleCart
/*   28:     */   extends RandomizableClassifier
/*   29:     */   implements AdditionalMeasureProducer, TechnicalInformationHandler
/*   30:     */ {
/*   31:     */   private static final long serialVersionUID = 4154189200352566053L;
/*   32:     */   protected Instances m_train;
/*   33:     */   protected SimpleCart[] m_Successors;
/*   34:     */   protected Attribute m_Attribute;
/*   35:     */   protected double m_SplitValue;
/*   36:     */   protected String m_SplitString;
/*   37:     */   protected double m_ClassValue;
/*   38:     */   protected Attribute m_ClassAttribute;
/*   39: 158 */   protected double m_minNumObj = 2.0D;
/*   40: 161 */   protected int m_numFoldsPruning = 5;
/*   41:     */   protected double m_Alpha;
/*   42:     */   protected double m_numIncorrectModel;
/*   43:     */   protected double m_numIncorrectTree;
/*   44:     */   protected boolean m_isLeaf;
/*   45: 179 */   protected boolean m_Prune = true;
/*   46:     */   protected int m_totalTrainInstances;
/*   47:     */   protected double[] m_Props;
/*   48: 188 */   protected double[] m_ClassProbs = null;
/*   49:     */   protected double[] m_Distribution;
/*   50: 200 */   protected boolean m_Heuristic = true;
/*   51: 203 */   protected boolean m_UseOneSE = false;
/*   52: 206 */   protected double m_SizePer = 1.0D;
/*   53:     */   
/*   54:     */   public String globalInfo()
/*   55:     */   {
/*   56: 214 */     return "Class implementing minimal cost-complexity pruning.\nNote when dealing with missing values, use \"fractional instances\" method instead of surrogate split method.\n\nFor more information, see:\n\n" + getTechnicalInformation().toString();
/*   57:     */   }
/*   58:     */   
/*   59:     */   public TechnicalInformation getTechnicalInformation()
/*   60:     */   {
/*   61: 231 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.BOOK);
/*   62: 232 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Leo Breiman and Jerome H. Friedman and Richard A. Olshen and Charles J. Stone");
/*   63:     */     
/*   64:     */ 
/*   65: 235 */     result.setValue(TechnicalInformation.Field.YEAR, "1984");
/*   66: 236 */     result.setValue(TechnicalInformation.Field.TITLE, "Classification and Regression Trees");
/*   67: 237 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Wadsworth International Group");
/*   68: 238 */     result.setValue(TechnicalInformation.Field.ADDRESS, "Belmont, California");
/*   69:     */     
/*   70: 240 */     return result;
/*   71:     */   }
/*   72:     */   
/*   73:     */   public Capabilities getCapabilities()
/*   74:     */   {
/*   75: 250 */     Capabilities result = super.getCapabilities();
/*   76: 251 */     result.disableAll();
/*   77:     */     
/*   78:     */ 
/*   79: 254 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*   80: 255 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*   81: 256 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*   82:     */     
/*   83:     */ 
/*   84: 259 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*   85:     */     
/*   86: 261 */     return result;
/*   87:     */   }
/*   88:     */   
/*   89:     */   public void buildClassifier(Instances data)
/*   90:     */     throws Exception
/*   91:     */   {
/*   92: 273 */     getCapabilities().testWithFail(data);
/*   93: 274 */     data = new Instances(data);
/*   94: 275 */     data.deleteWithMissingClass();
/*   95: 278 */     if (!this.m_Prune)
/*   96:     */     {
/*   97: 281 */       int[][] sortedIndices = new int[data.numAttributes()][0];
/*   98: 282 */       double[][] weights = new double[data.numAttributes()][0];
/*   99: 283 */       double[] classProbs = new double[data.numClasses()];
/*  100: 284 */       double totalWeight = computeSortedInfo(data, sortedIndices, weights, classProbs);
/*  101:     */       
/*  102:     */ 
/*  103: 287 */       makeTree(data, data.numInstances(), sortedIndices, weights, classProbs, totalWeight, this.m_minNumObj, this.m_Heuristic);
/*  104:     */       
/*  105: 289 */       return;
/*  106:     */     }
/*  107: 292 */     Random random = new Random(this.m_Seed);
/*  108: 293 */     Instances cvData = new Instances(data);
/*  109: 294 */     cvData.randomize(random);
/*  110: 295 */     cvData = new Instances(cvData, 0, (int)(cvData.numInstances() * this.m_SizePer) - 1);
/*  111:     */     
/*  112: 297 */     cvData.stratify(this.m_numFoldsPruning);
/*  113:     */     
/*  114: 299 */     double[][] alphas = new double[this.m_numFoldsPruning][];
/*  115: 300 */     double[][] errors = new double[this.m_numFoldsPruning][];
/*  116: 303 */     for (int i = 0; i < this.m_numFoldsPruning; i++)
/*  117:     */     {
/*  118: 306 */       Instances train = cvData.trainCV(this.m_numFoldsPruning, i);
/*  119: 307 */       Instances test = cvData.testCV(this.m_numFoldsPruning, i);
/*  120:     */       
/*  121:     */ 
/*  122:     */ 
/*  123: 311 */       int[][] sortedIndices = new int[train.numAttributes()][0];
/*  124: 312 */       double[][] weights = new double[train.numAttributes()][0];
/*  125: 313 */       double[] classProbs = new double[train.numClasses()];
/*  126: 314 */       double totalWeight = computeSortedInfo(train, sortedIndices, weights, classProbs);
/*  127:     */       
/*  128:     */ 
/*  129: 317 */       makeTree(train, train.numInstances(), sortedIndices, weights, classProbs, totalWeight, this.m_minNumObj, this.m_Heuristic);
/*  130:     */       
/*  131:     */ 
/*  132: 320 */       int numNodes = numInnerNodes();
/*  133: 321 */       alphas[i] = new double[numNodes + 2];
/*  134: 322 */       errors[i] = new double[numNodes + 2];
/*  135:     */       
/*  136:     */ 
/*  137: 325 */       prune(alphas[i], errors[i], test);
/*  138:     */     }
/*  139: 330 */     int[][] sortedIndices = new int[data.numAttributes()][0];
/*  140: 331 */     double[][] weights = new double[data.numAttributes()][0];
/*  141: 332 */     double[] classProbs = new double[data.numClasses()];
/*  142: 333 */     double totalWeight = computeSortedInfo(data, sortedIndices, weights, classProbs);
/*  143:     */     
/*  144:     */ 
/*  145:     */ 
/*  146: 337 */     makeTree(data, data.numInstances(), sortedIndices, weights, classProbs, totalWeight, this.m_minNumObj, this.m_Heuristic);
/*  147:     */     
/*  148:     */ 
/*  149: 340 */     int numNodes = numInnerNodes();
/*  150:     */     
/*  151: 342 */     double[] treeAlphas = new double[numNodes + 2];
/*  152:     */     
/*  153:     */ 
/*  154: 345 */     int iterations = prune(treeAlphas, null, null);
/*  155:     */     
/*  156: 347 */     double[] treeErrors = new double[numNodes + 2];
/*  157: 350 */     for (int i = 0; i <= iterations; i++)
/*  158:     */     {
/*  159: 352 */       double alpha = Math.sqrt(treeAlphas[i] * treeAlphas[(i + 1)]);
/*  160: 353 */       double error = 0.0D;
/*  161: 354 */       for (int k = 0; k < this.m_numFoldsPruning; k++)
/*  162:     */       {
/*  163: 355 */         int l = 0;
/*  164: 356 */         while (alphas[k][l] <= alpha) {
/*  165: 357 */           l++;
/*  166:     */         }
/*  167: 359 */         error += errors[k][(l - 1)];
/*  168:     */       }
/*  169: 361 */       treeErrors[i] = (error / this.m_numFoldsPruning);
/*  170:     */     }
/*  171: 365 */     int best = -1;
/*  172: 366 */     double bestError = 1.7976931348623157E+308D;
/*  173: 367 */     for (int i = iterations; i >= 0; i--) {
/*  174: 368 */       if (treeErrors[i] < bestError)
/*  175:     */       {
/*  176: 369 */         bestError = treeErrors[i];
/*  177: 370 */         best = i;
/*  178:     */       }
/*  179:     */     }
/*  180: 375 */     if (this.m_UseOneSE)
/*  181:     */     {
/*  182: 376 */       double oneSE = Math.sqrt(bestError * (1.0D - bestError) / data.numInstances());
/*  183: 378 */       for (int i = iterations; i >= 0; i--) {
/*  184: 379 */         if (treeErrors[i] <= bestError + oneSE)
/*  185:     */         {
/*  186: 380 */           best = i;
/*  187: 381 */           break;
/*  188:     */         }
/*  189:     */       }
/*  190:     */     }
/*  191: 386 */     double bestAlpha = Math.sqrt(treeAlphas[best] * treeAlphas[(best + 1)]);
/*  192:     */     
/*  193:     */ 
/*  194: 389 */     unprune();
/*  195: 390 */     prune(bestAlpha);
/*  196:     */   }
/*  197:     */   
/*  198:     */   protected void makeTree(Instances data, int totalInstances, int[][] sortedIndices, double[][] weights, double[] classProbs, double totalWeight, double minNumObj, boolean useHeuristic)
/*  199:     */     throws Exception
/*  200:     */   {
/*  201: 413 */     if (totalWeight == 0.0D)
/*  202:     */     {
/*  203: 414 */       this.m_Attribute = null;
/*  204: 415 */       this.m_ClassValue = Utils.missingValue();
/*  205: 416 */       this.m_Distribution = new double[data.numClasses()];
/*  206: 417 */       return;
/*  207:     */     }
/*  208: 420 */     this.m_totalTrainInstances = totalInstances;
/*  209: 421 */     this.m_isLeaf = true;
/*  210: 422 */     this.m_Successors = null;
/*  211:     */     
/*  212: 424 */     this.m_ClassProbs = new double[classProbs.length];
/*  213: 425 */     this.m_Distribution = new double[classProbs.length];
/*  214: 426 */     System.arraycopy(classProbs, 0, this.m_ClassProbs, 0, classProbs.length);
/*  215: 427 */     System.arraycopy(classProbs, 0, this.m_Distribution, 0, classProbs.length);
/*  216: 428 */     if (Utils.sum(this.m_ClassProbs) != 0.0D) {
/*  217: 429 */       Utils.normalize(this.m_ClassProbs);
/*  218:     */     }
/*  219: 434 */     double[][][] dists = new double[data.numAttributes()][0][0];
/*  220: 435 */     double[][] props = new double[data.numAttributes()][0];
/*  221: 436 */     double[][] totalSubsetWeights = new double[data.numAttributes()][2];
/*  222: 437 */     double[] splits = new double[data.numAttributes()];
/*  223: 438 */     String[] splitString = new String[data.numAttributes()];
/*  224: 439 */     double[] giniGains = new double[data.numAttributes()];
/*  225: 442 */     for (int i = 0; i < data.numAttributes(); i++)
/*  226:     */     {
/*  227: 443 */       Attribute att = data.attribute(i);
/*  228: 444 */       if (i != data.classIndex()) {
/*  229: 447 */         if (att.isNumeric()) {
/*  230: 449 */           splits[i] = numericDistribution(props, dists, att, sortedIndices[i], weights[i], totalSubsetWeights, giniGains, data);
/*  231:     */         } else {
/*  232: 453 */           splitString[i] = nominalDistribution(props, dists, att, sortedIndices[i], weights[i], totalSubsetWeights, giniGains, data, useHeuristic);
/*  233:     */         }
/*  234:     */       }
/*  235:     */     }
/*  236: 460 */     int attIndex = Utils.maxIndex(giniGains);
/*  237: 461 */     this.m_Attribute = data.attribute(attIndex);
/*  238:     */     
/*  239: 463 */     this.m_train = new Instances(data, sortedIndices[attIndex].length);
/*  240: 464 */     for (int i = 0; i < sortedIndices[attIndex].length; i++)
/*  241:     */     {
/*  242: 465 */       Instance inst = data.instance(sortedIndices[attIndex][i]);
/*  243: 466 */       Instance instCopy = (Instance)inst.copy();
/*  244: 467 */       instCopy.setWeight(weights[attIndex][i]);
/*  245: 468 */       this.m_train.add(instCopy);
/*  246:     */     }
/*  247: 474 */     if ((totalWeight < 2.0D * minNumObj) || (giniGains[attIndex] == 0.0D) || (props[attIndex][0] == 0.0D) || (props[attIndex][1] == 0.0D))
/*  248:     */     {
/*  249: 476 */       makeLeaf(data);
/*  250:     */     }
/*  251:     */     else
/*  252:     */     {
/*  253: 480 */       this.m_Props = props[attIndex];
/*  254: 481 */       int[][][] subsetIndices = new int[2][data.numAttributes()][0];
/*  255: 482 */       double[][][] subsetWeights = new double[2][data.numAttributes()][0];
/*  256: 485 */       if (this.m_Attribute.isNumeric()) {
/*  257: 486 */         this.m_SplitValue = splits[attIndex];
/*  258:     */       } else {
/*  259: 488 */         this.m_SplitString = splitString[attIndex];
/*  260:     */       }
/*  261: 491 */       splitData(subsetIndices, subsetWeights, this.m_Attribute, this.m_SplitValue, this.m_SplitString, sortedIndices, weights, data);
/*  262: 497 */       if ((subsetIndices[0][attIndex].length < minNumObj) || (subsetIndices[1][attIndex].length < minNumObj))
/*  263:     */       {
/*  264: 499 */         makeLeaf(data);
/*  265: 500 */         return;
/*  266:     */       }
/*  267: 504 */       this.m_isLeaf = false;
/*  268: 505 */       this.m_Successors = new SimpleCart[2];
/*  269: 506 */       for (int i = 0; i < 2; i++)
/*  270:     */       {
/*  271: 507 */         this.m_Successors[i] = new SimpleCart();
/*  272: 508 */         this.m_Successors[i].makeTree(data, this.m_totalTrainInstances, subsetIndices[i], subsetWeights[i], dists[attIndex][i], totalSubsetWeights[attIndex][i], minNumObj, useHeuristic);
/*  273:     */       }
/*  274:     */     }
/*  275:     */   }
/*  276:     */   
/*  277:     */   public void prune(double alpha)
/*  278:     */     throws Exception
/*  279:     */   {
/*  280: 529 */     modelErrors();
/*  281: 530 */     treeErrors();
/*  282: 531 */     calculateAlphas();
/*  283:     */     
/*  284:     */ 
/*  285: 534 */     Vector<SimpleCart> nodeList = getInnerNodes();
/*  286:     */     
/*  287: 536 */     boolean prune = nodeList.size() > 0;
/*  288: 537 */     double preAlpha = 1.7976931348623157E+308D;
/*  289: 538 */     while (prune)
/*  290:     */     {
/*  291: 541 */       SimpleCart nodeToPrune = nodeToPrune(nodeList);
/*  292: 544 */       if (nodeToPrune.m_Alpha > alpha) {
/*  293:     */         break;
/*  294:     */       }
/*  295: 548 */       nodeToPrune.makeLeaf(nodeToPrune.m_train);
/*  296: 551 */       if (nodeToPrune.m_Alpha == preAlpha)
/*  297:     */       {
/*  298: 552 */         nodeToPrune.makeLeaf(nodeToPrune.m_train);
/*  299: 553 */         treeErrors();
/*  300: 554 */         calculateAlphas();
/*  301: 555 */         nodeList = getInnerNodes();
/*  302: 556 */         prune = nodeList.size() > 0;
/*  303:     */       }
/*  304:     */       else
/*  305:     */       {
/*  306: 559 */         preAlpha = nodeToPrune.m_Alpha;
/*  307:     */         
/*  308:     */ 
/*  309: 562 */         treeErrors();
/*  310: 563 */         calculateAlphas();
/*  311:     */         
/*  312: 565 */         nodeList = getInnerNodes();
/*  313: 566 */         prune = nodeList.size() > 0;
/*  314:     */       }
/*  315:     */     }
/*  316:     */   }
/*  317:     */   
/*  318:     */   public int prune(double[] alphas, double[] errors, Instances test)
/*  319:     */     throws Exception
/*  320:     */   {
/*  321: 590 */     modelErrors();
/*  322: 591 */     treeErrors();
/*  323: 592 */     calculateAlphas();
/*  324:     */     
/*  325:     */ 
/*  326: 595 */     Vector<SimpleCart> nodeList = getInnerNodes();
/*  327:     */     
/*  328: 597 */     boolean prune = nodeList.size() > 0;
/*  329:     */     
/*  330:     */ 
/*  331: 600 */     alphas[0] = 0.0D;
/*  332: 605 */     if (errors != null)
/*  333:     */     {
/*  334: 606 */       Evaluation eval = new Evaluation(test);
/*  335: 607 */       eval.evaluateModel(this, test, new Object[0]);
/*  336: 608 */       errors[0] = eval.errorRate();
/*  337:     */     }
/*  338: 611 */     int iteration = 0;
/*  339: 612 */     double preAlpha = 1.7976931348623157E+308D;
/*  340: 613 */     while (prune)
/*  341:     */     {
/*  342: 615 */       iteration++;
/*  343:     */       
/*  344:     */ 
/*  345: 618 */       SimpleCart nodeToPrune = nodeToPrune(nodeList);
/*  346:     */       
/*  347:     */ 
/*  348: 621 */       nodeToPrune.m_isLeaf = true;
/*  349: 624 */       if (nodeToPrune.m_Alpha == preAlpha)
/*  350:     */       {
/*  351: 625 */         iteration--;
/*  352: 626 */         treeErrors();
/*  353: 627 */         calculateAlphas();
/*  354: 628 */         nodeList = getInnerNodes();
/*  355: 629 */         prune = nodeList.size() > 0;
/*  356:     */       }
/*  357:     */       else
/*  358:     */       {
/*  359: 634 */         alphas[iteration] = nodeToPrune.m_Alpha;
/*  360: 637 */         if (errors != null)
/*  361:     */         {
/*  362: 638 */           Evaluation eval = new Evaluation(test);
/*  363: 639 */           eval.evaluateModel(this, test, new Object[0]);
/*  364: 640 */           errors[iteration] = eval.errorRate();
/*  365:     */         }
/*  366: 642 */         preAlpha = nodeToPrune.m_Alpha;
/*  367:     */         
/*  368:     */ 
/*  369: 645 */         treeErrors();
/*  370: 646 */         calculateAlphas();
/*  371:     */         
/*  372: 648 */         nodeList = getInnerNodes();
/*  373: 649 */         prune = nodeList.size() > 0;
/*  374:     */       }
/*  375:     */     }
/*  376: 653 */     alphas[(iteration + 1)] = 1.0D;
/*  377: 654 */     return iteration;
/*  378:     */   }
/*  379:     */   
/*  380:     */   protected void unprune()
/*  381:     */   {
/*  382: 662 */     if (this.m_Successors != null)
/*  383:     */     {
/*  384: 663 */       this.m_isLeaf = false;
/*  385: 664 */       for (SimpleCart m_Successor : this.m_Successors) {
/*  386: 665 */         m_Successor.unprune();
/*  387:     */       }
/*  388:     */     }
/*  389:     */   }
/*  390:     */   
/*  391:     */   protected double numericDistribution(double[][] props, double[][][] dists, Attribute att, int[] sortedIndices, double[] weights, double[][] subsetWeights, double[] giniGains, Instances data)
/*  392:     */     throws Exception
/*  393:     */   {
/*  394: 691 */     double splitPoint = (0.0D / 0.0D);
/*  395: 692 */     double[][] dist = (double[][])null;
/*  396: 693 */     int numClasses = data.numClasses();
/*  397:     */     
/*  398:     */ 
/*  399: 696 */     double[][] currDist = new double[2][numClasses];
/*  400: 697 */     dist = new double[2][numClasses];
/*  401:     */     
/*  402:     */ 
/*  403: 700 */     double[] parentDist = new double[numClasses];
/*  404: 701 */     int missingStart = 0;
/*  405: 702 */     for (int j = 0; j < sortedIndices.length; j++)
/*  406:     */     {
/*  407: 703 */       Instance inst = data.instance(sortedIndices[j]);
/*  408: 704 */       if (!inst.isMissing(att))
/*  409:     */       {
/*  410: 705 */         missingStart++;
/*  411: 706 */         currDist[1][((int)inst.classValue())] += weights[j];
/*  412:     */       }
/*  413: 708 */       parentDist[((int)inst.classValue())] += weights[j];
/*  414:     */     }
/*  415: 710 */     System.arraycopy(currDist[1], 0, dist[1], 0, dist[1].length);
/*  416:     */     
/*  417:     */ 
/*  418: 713 */     double currSplit = data.instance(sortedIndices[0]).value(att);
/*  419:     */     
/*  420: 715 */     double bestGiniGain = -1.797693134862316E+308D;
/*  421: 717 */     for (int i = 0; i < sortedIndices.length; i++)
/*  422:     */     {
/*  423: 718 */       Instance inst = data.instance(sortedIndices[i]);
/*  424: 719 */       if (inst.isMissing(att)) {
/*  425:     */         break;
/*  426:     */       }
/*  427: 722 */       if (inst.value(att) > currSplit)
/*  428:     */       {
/*  429: 724 */         double[][] tempDist = new double[2][numClasses];
/*  430: 725 */         for (int k = 0; k < 2; k++) {
/*  431: 727 */           System.arraycopy(currDist[k], 0, tempDist[k], 0, tempDist[k].length);
/*  432:     */         }
/*  433: 730 */         double[] tempProps = new double[2];
/*  434: 731 */         for (int k = 0; k < 2; k++) {
/*  435: 732 */           tempProps[k] = Utils.sum(tempDist[k]);
/*  436:     */         }
/*  437: 735 */         if (Utils.sum(tempProps) != 0.0D) {
/*  438: 736 */           Utils.normalize(tempProps);
/*  439:     */         }
/*  440: 740 */         int index = missingStart;
/*  441: 741 */         while (index < sortedIndices.length)
/*  442:     */         {
/*  443: 742 */           Instance insta = data.instance(sortedIndices[index]);
/*  444: 743 */           for (int j = 0; j < 2; j++) {
/*  445: 744 */             tempDist[j][((int)insta.classValue())] += tempProps[j] * weights[index];
/*  446:     */           }
/*  447: 747 */           index++;
/*  448:     */         }
/*  449: 750 */         double currGiniGain = computeGiniGain(parentDist, tempDist);
/*  450: 752 */         if (currGiniGain > bestGiniGain)
/*  451:     */         {
/*  452: 753 */           bestGiniGain = currGiniGain;
/*  453:     */           
/*  454:     */ 
/*  455:     */ 
/*  456:     */ 
/*  457: 758 */           splitPoint = (inst.value(att) + currSplit) / 2.0D;
/*  458: 760 */           for (int j = 0; j < currDist.length; j++) {
/*  459: 761 */             System.arraycopy(tempDist[j], 0, dist[j], 0, dist[j].length);
/*  460:     */           }
/*  461:     */         }
/*  462:     */       }
/*  463: 765 */       currSplit = inst.value(att);
/*  464: 766 */       currDist[0][((int)inst.classValue())] += weights[i];
/*  465: 767 */       currDist[1][((int)inst.classValue())] -= weights[i];
/*  466:     */     }
/*  467: 771 */     int attIndex = att.index();
/*  468: 772 */     props[attIndex] = new double[2];
/*  469: 773 */     for (int k = 0; k < 2; k++) {
/*  470: 774 */       props[attIndex][k] = Utils.sum(dist[k]);
/*  471:     */     }
/*  472: 776 */     if (Utils.sum(props[attIndex]) != 0.0D) {
/*  473: 777 */       Utils.normalize(props[attIndex]);
/*  474:     */     }
/*  475: 781 */     subsetWeights[attIndex] = new double[2];
/*  476: 782 */     for (int j = 0; j < 2; j++) {
/*  477: 783 */       subsetWeights[attIndex][j] += Utils.sum(dist[j]);
/*  478:     */     }
/*  479: 788 */     giniGains[attIndex] = bestGiniGain;
/*  480: 789 */     dists[attIndex] = dist;
/*  481:     */     
/*  482: 791 */     return splitPoint;
/*  483:     */   }
/*  484:     */   
/*  485:     */   protected String nominalDistribution(double[][] props, double[][][] dists, Attribute att, int[] sortedIndices, double[] weights, double[][] subsetWeights, double[] giniGains, Instances data, boolean useHeuristic)
/*  486:     */     throws Exception
/*  487:     */   {
/*  488: 816 */     String[] values = new String[att.numValues()];
/*  489: 817 */     int numCat = values.length;
/*  490: 818 */     int numClasses = data.numClasses();
/*  491:     */     
/*  492: 820 */     String bestSplitString = "";
/*  493: 821 */     double bestGiniGain = -1.797693134862316E+308D;
/*  494:     */     
/*  495:     */ 
/*  496: 824 */     int[] classFreq = new int[numCat];
/*  497: 825 */     for (int j = 0; j < numCat; j++) {
/*  498: 826 */       classFreq[j] = 0;
/*  499:     */     }
/*  500: 829 */     double[] parentDist = new double[numClasses];
/*  501: 830 */     double[][] currDist = new double[2][numClasses];
/*  502: 831 */     double[][] dist = new double[2][numClasses];
/*  503: 832 */     int missingStart = 0;
/*  504: 834 */     for (int i = 0; i < sortedIndices.length; i++)
/*  505:     */     {
/*  506: 835 */       Instance inst = data.instance(sortedIndices[i]);
/*  507: 836 */       if (!inst.isMissing(att))
/*  508:     */       {
/*  509: 837 */         missingStart++;
/*  510: 838 */         classFreq[((int)inst.value(att))] += 1;
/*  511:     */       }
/*  512: 840 */       parentDist[((int)inst.classValue())] += weights[i];
/*  513:     */     }
/*  514: 844 */     int nonEmpty = 0;
/*  515: 845 */     for (int j = 0; j < numCat; j++) {
/*  516: 846 */       if (classFreq[j] != 0) {
/*  517: 847 */         nonEmpty++;
/*  518:     */       }
/*  519:     */     }
/*  520: 852 */     String[] nonEmptyValues = new String[nonEmpty];
/*  521: 853 */     int nonEmptyIndex = 0;
/*  522: 854 */     for (int j = 0; j < numCat; j++) {
/*  523: 855 */       if (classFreq[j] != 0)
/*  524:     */       {
/*  525: 856 */         nonEmptyValues[nonEmptyIndex] = att.value(j);
/*  526: 857 */         nonEmptyIndex++;
/*  527:     */       }
/*  528:     */     }
/*  529: 862 */     int empty = numCat - nonEmpty;
/*  530: 863 */     String[] emptyValues = new String[empty];
/*  531: 864 */     int emptyIndex = 0;
/*  532: 865 */     for (int j = 0; j < numCat; j++) {
/*  533: 866 */       if (classFreq[j] == 0)
/*  534:     */       {
/*  535: 867 */         emptyValues[emptyIndex] = att.value(j);
/*  536: 868 */         emptyIndex++;
/*  537:     */       }
/*  538:     */     }
/*  539: 872 */     if (nonEmpty <= 1)
/*  540:     */     {
/*  541: 873 */       giniGains[att.index()] = 0.0D;
/*  542: 874 */       return "";
/*  543:     */     }
/*  544: 878 */     if (data.numClasses() == 2)
/*  545:     */     {
/*  546: 883 */       double[] pClass0 = new double[nonEmpty];
/*  547:     */       
/*  548: 885 */       double[][] valDist = new double[nonEmpty][2];
/*  549: 887 */       for (int j = 0; j < nonEmpty; j++) {
/*  550: 888 */         for (int k = 0; k < 2; k++) {
/*  551: 889 */           valDist[j][k] = 0.0D;
/*  552:     */         }
/*  553:     */       }
/*  554: 893 */       for (int sortedIndice : sortedIndices)
/*  555:     */       {
/*  556: 894 */         Instance inst = data.instance(sortedIndice);
/*  557: 895 */         if (inst.isMissing(att)) {
/*  558:     */           break;
/*  559:     */         }
/*  560: 899 */         for (int j = 0; j < nonEmpty; j++) {
/*  561: 900 */           if (att.value((int)inst.value(att)).compareTo(nonEmptyValues[j]) == 0)
/*  562:     */           {
/*  563: 901 */             valDist[j][((int)inst.classValue())] += inst.weight();
/*  564: 902 */             break;
/*  565:     */           }
/*  566:     */         }
/*  567:     */       }
/*  568: 907 */       for (int j = 0; j < nonEmpty; j++)
/*  569:     */       {
/*  570: 908 */         double distSum = Utils.sum(valDist[j]);
/*  571: 909 */         if (distSum == 0.0D) {
/*  572: 910 */           pClass0[j] = 0.0D;
/*  573:     */         } else {
/*  574: 912 */           pClass0[j] = (valDist[j][0] / distSum);
/*  575:     */         }
/*  576:     */       }
/*  577: 917 */       String[] sortedValues = new String[nonEmpty];
/*  578: 918 */       for (int j = 0; j < nonEmpty; j++)
/*  579:     */       {
/*  580: 919 */         sortedValues[j] = nonEmptyValues[Utils.minIndex(pClass0)];
/*  581: 920 */         pClass0[Utils.minIndex(pClass0)] = 1.7976931348623157E+308D;
/*  582:     */       }
/*  583: 926 */       String tempStr = "";
/*  584: 928 */       for (int j = 0; j < nonEmpty - 1; j++)
/*  585:     */       {
/*  586: 929 */         currDist = new double[2][numClasses];
/*  587: 930 */         if (tempStr == "") {
/*  588: 931 */           tempStr = "(" + sortedValues[j] + ")";
/*  589:     */         } else {
/*  590: 933 */           tempStr = tempStr + "|(" + sortedValues[j] + ")";
/*  591:     */         }
/*  592: 935 */         for (int i = 0; i < sortedIndices.length; i++)
/*  593:     */         {
/*  594: 936 */           Instance inst = data.instance(sortedIndices[i]);
/*  595: 937 */           if (inst.isMissing(att)) {
/*  596:     */             break;
/*  597:     */           }
/*  598: 941 */           if (tempStr.indexOf("(" + att.value((int)inst.value(att)) + ")") != -1) {
/*  599: 942 */             currDist[0][((int)inst.classValue())] += weights[i];
/*  600:     */           } else {
/*  601: 944 */             currDist[1][((int)inst.classValue())] += weights[i];
/*  602:     */           }
/*  603:     */         }
/*  604: 948 */         double[][] tempDist = new double[2][numClasses];
/*  605: 949 */         for (int kk = 0; kk < 2; kk++) {
/*  606: 950 */           tempDist[kk] = currDist[kk];
/*  607:     */         }
/*  608: 953 */         double[] tempProps = new double[2];
/*  609: 954 */         for (int kk = 0; kk < 2; kk++) {
/*  610: 955 */           tempProps[kk] = Utils.sum(tempDist[kk]);
/*  611:     */         }
/*  612: 958 */         if (Utils.sum(tempProps) != 0.0D) {
/*  613: 959 */           Utils.normalize(tempProps);
/*  614:     */         }
/*  615: 963 */         int mstart = missingStart;
/*  616: 964 */         while (mstart < sortedIndices.length)
/*  617:     */         {
/*  618: 965 */           Instance insta = data.instance(sortedIndices[mstart]);
/*  619: 966 */           for (int jj = 0; jj < 2; jj++) {
/*  620: 967 */             tempDist[jj][((int)insta.classValue())] += tempProps[jj] * weights[mstart];
/*  621:     */           }
/*  622: 970 */           mstart++;
/*  623:     */         }
/*  624: 973 */         double currGiniGain = computeGiniGain(parentDist, tempDist);
/*  625: 975 */         if (currGiniGain > bestGiniGain)
/*  626:     */         {
/*  627: 976 */           bestGiniGain = currGiniGain;
/*  628: 977 */           bestSplitString = tempStr;
/*  629: 978 */           for (int jj = 0; jj < 2; jj++) {
/*  630: 980 */             System.arraycopy(tempDist[jj], 0, dist[jj], 0, dist[jj].length);
/*  631:     */           }
/*  632:     */         }
/*  633:     */       }
/*  634:     */     }
/*  635: 987 */     else if ((!useHeuristic) || (nonEmpty <= 4))
/*  636:     */     {
/*  637: 990 */       for (int i = 0; i < (int)Math.pow(2.0D, nonEmpty - 1); i++)
/*  638:     */       {
/*  639: 991 */         String tempStr = "";
/*  640: 992 */         currDist = new double[2][numClasses];
/*  641:     */         
/*  642: 994 */         int bit10 = i;
/*  643: 995 */         for (int j = nonEmpty - 1; j >= 0; j--)
/*  644:     */         {
/*  645: 996 */           int mod = bit10 % 2;
/*  646: 997 */           if (mod == 1) {
/*  647: 998 */             if (tempStr == "") {
/*  648: 999 */               tempStr = "(" + nonEmptyValues[j] + ")";
/*  649:     */             } else {
/*  650:1001 */               tempStr = tempStr + "|(" + nonEmptyValues[j] + ")";
/*  651:     */             }
/*  652:     */           }
/*  653:1004 */           bit10 /= 2;
/*  654:     */         }
/*  655:1006 */         for (int j = 0; j < sortedIndices.length; j++)
/*  656:     */         {
/*  657:1007 */           Instance inst = data.instance(sortedIndices[j]);
/*  658:1008 */           if (inst.isMissing(att)) {
/*  659:     */             break;
/*  660:     */           }
/*  661:1012 */           if (tempStr.indexOf("(" + att.value((int)inst.value(att)) + ")") != -1) {
/*  662:1013 */             currDist[0][((int)inst.classValue())] += weights[j];
/*  663:     */           } else {
/*  664:1015 */             currDist[1][((int)inst.classValue())] += weights[j];
/*  665:     */           }
/*  666:     */         }
/*  667:1019 */         double[][] tempDist = new double[2][numClasses];
/*  668:1020 */         for (int k = 0; k < 2; k++) {
/*  669:1021 */           tempDist[k] = currDist[k];
/*  670:     */         }
/*  671:1024 */         double[] tempProps = new double[2];
/*  672:1025 */         for (int k = 0; k < 2; k++) {
/*  673:1026 */           tempProps[k] = Utils.sum(tempDist[k]);
/*  674:     */         }
/*  675:1029 */         if (Utils.sum(tempProps) != 0.0D) {
/*  676:1030 */           Utils.normalize(tempProps);
/*  677:     */         }
/*  678:1034 */         int index = missingStart;
/*  679:1035 */         while (index < sortedIndices.length)
/*  680:     */         {
/*  681:1036 */           Instance insta = data.instance(sortedIndices[index]);
/*  682:1037 */           for (int j = 0; j < 2; j++) {
/*  683:1038 */             tempDist[j][((int)insta.classValue())] += tempProps[j] * weights[index];
/*  684:     */           }
/*  685:1041 */           index++;
/*  686:     */         }
/*  687:1044 */         double currGiniGain = computeGiniGain(parentDist, tempDist);
/*  688:1046 */         if (currGiniGain > bestGiniGain)
/*  689:     */         {
/*  690:1047 */           bestGiniGain = currGiniGain;
/*  691:1048 */           bestSplitString = tempStr;
/*  692:1049 */           for (int j = 0; j < 2; j++) {
/*  693:1051 */             System.arraycopy(tempDist[j], 0, dist[j], 0, dist[j].length);
/*  694:     */           }
/*  695:     */         }
/*  696:     */       }
/*  697:     */     }
/*  698:     */     else
/*  699:     */     {
/*  700:1060 */       int n = nonEmpty;
/*  701:1061 */       int k = data.numClasses();
/*  702:1062 */       double[][] P = new double[n][k];
/*  703:1063 */       int[] numInstancesValue = new int[n];
/*  704:     */       
/*  705:1065 */       double[] meanClass = new double[k];
/*  706:1066 */       int numInstances = data.numInstances();
/*  707:1069 */       for (int j = 0; j < meanClass.length; j++) {
/*  708:1070 */         meanClass[j] = 0.0D;
/*  709:     */       }
/*  710:1073 */       for (int j = 0; j < numInstances; j++)
/*  711:     */       {
/*  712:1074 */         Instance inst = data.instance(j);
/*  713:1075 */         int valueIndex = 0;
/*  714:1076 */         for (int i = 0; i < nonEmpty; i++) {
/*  715:1077 */           if (att.value((int)inst.value(att)).compareToIgnoreCase(nonEmptyValues[i]) == 0)
/*  716:     */           {
/*  717:1079 */             valueIndex = i;
/*  718:1080 */             break;
/*  719:     */           }
/*  720:     */         }
/*  721:1083 */         P[valueIndex][((int)inst.classValue())] += 1.0D;
/*  722:1084 */         numInstancesValue[valueIndex] += 1;
/*  723:1085 */         meanClass[((int)inst.classValue())] += 1.0D;
/*  724:     */       }
/*  725:1089 */       for (int i = 0; i < P.length; i++) {
/*  726:1090 */         for (int j = 0; j < P[0].length; j++) {
/*  727:1091 */           if (numInstancesValue[i] == 0) {
/*  728:1092 */             P[i][j] = 0.0D;
/*  729:     */           } else {
/*  730:1094 */             P[i][j] /= numInstancesValue[i];
/*  731:     */           }
/*  732:     */         }
/*  733:     */       }
/*  734:1100 */       for (int i = 0; i < meanClass.length; i++) {
/*  735:1101 */         meanClass[i] /= numInstances;
/*  736:     */       }
/*  737:1105 */       double[][] covariance = new double[k][k];
/*  738:1106 */       for (int i1 = 0; i1 < k; i1++) {
/*  739:1107 */         for (int i2 = 0; i2 < k; i2++)
/*  740:     */         {
/*  741:1108 */           double element = 0.0D;
/*  742:1109 */           for (int j = 0; j < n; j++) {
/*  743:1110 */             element += (P[j][i2] - meanClass[i2]) * (P[j][i1] - meanClass[i1]) * numInstancesValue[j];
/*  744:     */           }
/*  745:1113 */           covariance[i1][i2] = element;
/*  746:     */         }
/*  747:     */       }
/*  748:1117 */       Matrix matrix = new Matrix(covariance);
/*  749:1118 */       EigenvalueDecomposition eigen = new EigenvalueDecomposition(matrix);
/*  750:     */       
/*  751:1120 */       double[] eigenValues = eigen.getRealEigenvalues();
/*  752:     */       
/*  753:     */ 
/*  754:1123 */       int index = 0;
/*  755:1124 */       double largest = eigenValues[0];
/*  756:1125 */       for (int i = 1; i < eigenValues.length; i++) {
/*  757:1126 */         if (eigenValues[i] > largest)
/*  758:     */         {
/*  759:1127 */           index = i;
/*  760:1128 */           largest = eigenValues[i];
/*  761:     */         }
/*  762:     */       }
/*  763:1133 */       double[] FPC = new double[k];
/*  764:1134 */       Matrix eigenVector = eigen.getV();
/*  765:1135 */       double[][] vectorArray = eigenVector.getArray();
/*  766:1136 */       for (int i = 0; i < FPC.length; i++) {
/*  767:1137 */         FPC[i] = vectorArray[i][index];
/*  768:     */       }
/*  769:1142 */       double[] Sa = new double[n];
/*  770:1143 */       for (int i = 0; i < Sa.length; i++)
/*  771:     */       {
/*  772:1144 */         Sa[i] = 0.0D;
/*  773:1145 */         for (int j = 0; j < k; j++) {
/*  774:1146 */           Sa[i] += FPC[j] * P[i][j];
/*  775:     */         }
/*  776:     */       }
/*  777:1151 */       double[] pCopy = new double[n];
/*  778:1152 */       System.arraycopy(Sa, 0, pCopy, 0, n);
/*  779:1153 */       String[] sortedValues = new String[n];
/*  780:1154 */       Arrays.sort(Sa);
/*  781:1156 */       for (int j = 0; j < n; j++)
/*  782:     */       {
/*  783:1157 */         sortedValues[j] = nonEmptyValues[Utils.minIndex(pCopy)];
/*  784:1158 */         pCopy[Utils.minIndex(pCopy)] = 1.7976931348623157E+308D;
/*  785:     */       }
/*  786:1162 */       String tempStr = "";
/*  787:1164 */       for (int j = 0; j < nonEmpty - 1; j++)
/*  788:     */       {
/*  789:1165 */         currDist = new double[2][numClasses];
/*  790:1166 */         if (tempStr == "") {
/*  791:1167 */           tempStr = "(" + sortedValues[j] + ")";
/*  792:     */         } else {
/*  793:1169 */           tempStr = tempStr + "|(" + sortedValues[j] + ")";
/*  794:     */         }
/*  795:1171 */         for (int i = 0; i < sortedIndices.length; i++)
/*  796:     */         {
/*  797:1172 */           Instance inst = data.instance(sortedIndices[i]);
/*  798:1173 */           if (inst.isMissing(att)) {
/*  799:     */             break;
/*  800:     */           }
/*  801:1177 */           if (tempStr.indexOf("(" + att.value((int)inst.value(att)) + ")") != -1) {
/*  802:1178 */             currDist[0][((int)inst.classValue())] += weights[i];
/*  803:     */           } else {
/*  804:1180 */             currDist[1][((int)inst.classValue())] += weights[i];
/*  805:     */           }
/*  806:     */         }
/*  807:1184 */         double[][] tempDist = new double[2][numClasses];
/*  808:1185 */         for (int kk = 0; kk < 2; kk++) {
/*  809:1186 */           tempDist[kk] = currDist[kk];
/*  810:     */         }
/*  811:1189 */         double[] tempProps = new double[2];
/*  812:1190 */         for (int kk = 0; kk < 2; kk++) {
/*  813:1191 */           tempProps[kk] = Utils.sum(tempDist[kk]);
/*  814:     */         }
/*  815:1194 */         if (Utils.sum(tempProps) != 0.0D) {
/*  816:1195 */           Utils.normalize(tempProps);
/*  817:     */         }
/*  818:1199 */         int mstart = missingStart;
/*  819:1200 */         while (mstart < sortedIndices.length)
/*  820:     */         {
/*  821:1201 */           Instance insta = data.instance(sortedIndices[mstart]);
/*  822:1202 */           for (int jj = 0; jj < 2; jj++) {
/*  823:1203 */             tempDist[jj][((int)insta.classValue())] += tempProps[jj] * weights[mstart];
/*  824:     */           }
/*  825:1206 */           mstart++;
/*  826:     */         }
/*  827:1209 */         double currGiniGain = computeGiniGain(parentDist, tempDist);
/*  828:1211 */         if (currGiniGain > bestGiniGain)
/*  829:     */         {
/*  830:1212 */           bestGiniGain = currGiniGain;
/*  831:1213 */           bestSplitString = tempStr;
/*  832:1214 */           for (int jj = 0; jj < 2; jj++) {
/*  833:1216 */             System.arraycopy(tempDist[jj], 0, dist[jj], 0, dist[jj].length);
/*  834:     */           }
/*  835:     */         }
/*  836:     */       }
/*  837:     */     }
/*  838:1223 */     int attIndex = att.index();
/*  839:1224 */     props[attIndex] = new double[2];
/*  840:1225 */     for (int k = 0; k < 2; k++) {
/*  841:1226 */       props[attIndex][k] = Utils.sum(dist[k]);
/*  842:     */     }
/*  843:1229 */     if (Utils.sum(props[attIndex]) <= 0.0D) {
/*  844:1230 */       for (int k = 0; k < props[attIndex].length; k++) {
/*  845:1231 */         props[attIndex][k] = (1.0D / props[attIndex].length);
/*  846:     */       }
/*  847:     */     } else {
/*  848:1234 */       Utils.normalize(props[attIndex]);
/*  849:     */     }
/*  850:1238 */     subsetWeights[attIndex] = new double[2];
/*  851:1239 */     for (int j = 0; j < 2; j++) {
/*  852:1240 */       subsetWeights[attIndex][j] += Utils.sum(dist[j]);
/*  853:     */     }
/*  854:1246 */     for (int j = 0; j < empty; j++) {
/*  855:1247 */       if (props[attIndex][0] >= props[attIndex][1]) {
/*  856:1248 */         if (bestSplitString == "") {
/*  857:1249 */           bestSplitString = "(" + emptyValues[j] + ")";
/*  858:     */         } else {
/*  859:1251 */           bestSplitString = bestSplitString + "|(" + emptyValues[j] + ")";
/*  860:     */         }
/*  861:     */       }
/*  862:     */     }
/*  863:1258 */     giniGains[attIndex] = bestGiniGain;
/*  864:     */     
/*  865:1260 */     dists[attIndex] = dist;
/*  866:1261 */     return bestSplitString;
/*  867:     */   }
/*  868:     */   
/*  869:     */   protected void splitData(int[][][] subsetIndices, double[][][] subsetWeights, Attribute att, double splitPoint, String splitStr, int[][] sortedIndices, double[][] weights, Instances data)
/*  870:     */     throws Exception
/*  871:     */   {
/*  872:1286 */     for (int i = 0; i < data.numAttributes(); i++) {
/*  873:1287 */       if (i != data.classIndex())
/*  874:     */       {
/*  875:1290 */         int[] num = new int[2];
/*  876:1291 */         for (int k = 0; k < 2; k++)
/*  877:     */         {
/*  878:1292 */           subsetIndices[k][i] = new int[sortedIndices[i].length];
/*  879:1293 */           subsetWeights[k][i] = new double[weights[i].length];
/*  880:     */         }
/*  881:1296 */         for (int j = 0; j < sortedIndices[i].length; j++)
/*  882:     */         {
/*  883:1297 */           Instance inst = data.instance(sortedIndices[i][j]);
/*  884:1298 */           if (inst.isMissing(att))
/*  885:     */           {
/*  886:1300 */             for (int k = 0; k < 2; k++) {
/*  887:1301 */               if (this.m_Props[k] > 0.0D)
/*  888:     */               {
/*  889:1302 */                 subsetIndices[k][i][num[k]] = sortedIndices[i][j];
/*  890:1303 */                 subsetWeights[k][i][num[k]] = (this.m_Props[k] * weights[i][j]);
/*  891:1304 */                 num[k] += 1;
/*  892:     */               }
/*  893:     */             }
/*  894:     */           }
/*  895:     */           else
/*  896:     */           {
/*  897:     */             int subset;
/*  898:     */             int subset;
/*  899:1309 */             if (att.isNumeric())
/*  900:     */             {
/*  901:1310 */               subset = inst.value(att) < splitPoint ? 0 : 1;
/*  902:     */             }
/*  903:     */             else
/*  904:     */             {
/*  905:     */               int subset;
/*  906:1312 */               if (splitStr.indexOf("(" + att.value((int)inst.value(att.index())) + ")") != -1) {
/*  907:1314 */                 subset = 0;
/*  908:     */               } else {
/*  909:1316 */                 subset = 1;
/*  910:     */               }
/*  911:     */             }
/*  912:1319 */             subsetIndices[subset][i][num[subset]] = sortedIndices[i][j];
/*  913:1320 */             subsetWeights[subset][i][num[subset]] = weights[i][j];
/*  914:1321 */             num[subset] += 1;
/*  915:     */           }
/*  916:     */         }
/*  917:1326 */         for (int k = 0; k < 2; k++)
/*  918:     */         {
/*  919:1327 */           int[] copy = new int[num[k]];
/*  920:1328 */           System.arraycopy(subsetIndices[k][i], 0, copy, 0, num[k]);
/*  921:1329 */           subsetIndices[k][i] = copy;
/*  922:1330 */           double[] copyWeights = new double[num[k]];
/*  923:1331 */           System.arraycopy(subsetWeights[k][i], 0, copyWeights, 0, num[k]);
/*  924:1332 */           subsetWeights[k][i] = copyWeights;
/*  925:     */         }
/*  926:     */       }
/*  927:     */     }
/*  928:     */   }
/*  929:     */   
/*  930:     */   public void modelErrors()
/*  931:     */     throws Exception
/*  932:     */   {
/*  933:1344 */     Evaluation eval = new Evaluation(this.m_train);
/*  934:1346 */     if (!this.m_isLeaf)
/*  935:     */     {
/*  936:1347 */       this.m_isLeaf = true;
/*  937:     */       
/*  938:     */ 
/*  939:1350 */       eval.evaluateModel(this, this.m_train, new Object[0]);
/*  940:1351 */       this.m_numIncorrectModel = eval.incorrect();
/*  941:     */       
/*  942:1353 */       this.m_isLeaf = false;
/*  943:1355 */       for (SimpleCart m_Successor : this.m_Successors) {
/*  944:1356 */         m_Successor.modelErrors();
/*  945:     */       }
/*  946:     */     }
/*  947:     */     else
/*  948:     */     {
/*  949:1360 */       eval.evaluateModel(this, this.m_train, new Object[0]);
/*  950:1361 */       this.m_numIncorrectModel = eval.incorrect();
/*  951:     */     }
/*  952:     */   }
/*  953:     */   
/*  954:     */   public void treeErrors()
/*  955:     */     throws Exception
/*  956:     */   {
/*  957:1372 */     if (this.m_isLeaf)
/*  958:     */     {
/*  959:1373 */       this.m_numIncorrectTree = this.m_numIncorrectModel;
/*  960:     */     }
/*  961:     */     else
/*  962:     */     {
/*  963:1375 */       this.m_numIncorrectTree = 0.0D;
/*  964:1376 */       for (SimpleCart m_Successor : this.m_Successors)
/*  965:     */       {
/*  966:1377 */         m_Successor.treeErrors();
/*  967:1378 */         this.m_numIncorrectTree += m_Successor.m_numIncorrectTree;
/*  968:     */       }
/*  969:     */     }
/*  970:     */   }
/*  971:     */   
/*  972:     */   public void calculateAlphas()
/*  973:     */     throws Exception
/*  974:     */   {
/*  975:1390 */     if (!this.m_isLeaf)
/*  976:     */     {
/*  977:1391 */       double errorDiff = this.m_numIncorrectModel - this.m_numIncorrectTree;
/*  978:1392 */       if (errorDiff <= 0.0D)
/*  979:     */       {
/*  980:1395 */         makeLeaf(this.m_train);
/*  981:1396 */         this.m_Alpha = 1.7976931348623157E+308D;
/*  982:     */       }
/*  983:     */       else
/*  984:     */       {
/*  985:1399 */         errorDiff /= this.m_totalTrainInstances;
/*  986:1400 */         this.m_Alpha = (errorDiff / (numLeaves() - 1));
/*  987:1401 */         long alphaLong = Math.round(this.m_Alpha * Math.pow(10.0D, 10.0D));
/*  988:1402 */         this.m_Alpha = (alphaLong / Math.pow(10.0D, 10.0D));
/*  989:1403 */         for (SimpleCart m_Successor : this.m_Successors) {
/*  990:1404 */           m_Successor.calculateAlphas();
/*  991:     */         }
/*  992:     */       }
/*  993:     */     }
/*  994:     */     else
/*  995:     */     {
/*  996:1409 */       this.m_Alpha = 1.7976931348623157E+308D;
/*  997:     */     }
/*  998:     */   }
/*  999:     */   
/* 1000:     */   protected SimpleCart nodeToPrune(Vector<SimpleCart> nodeList)
/* 1001:     */   {
/* 1002:1421 */     if (nodeList.size() == 0) {
/* 1003:1422 */       return null;
/* 1004:     */     }
/* 1005:1424 */     if (nodeList.size() == 1) {
/* 1006:1425 */       return (SimpleCart)nodeList.elementAt(0);
/* 1007:     */     }
/* 1008:1427 */     SimpleCart returnNode = (SimpleCart)nodeList.elementAt(0);
/* 1009:1428 */     double baseAlpha = returnNode.m_Alpha;
/* 1010:1429 */     for (int i = 1; i < nodeList.size(); i++)
/* 1011:     */     {
/* 1012:1430 */       SimpleCart node = (SimpleCart)nodeList.elementAt(i);
/* 1013:1431 */       if (node.m_Alpha < baseAlpha)
/* 1014:     */       {
/* 1015:1432 */         baseAlpha = node.m_Alpha;
/* 1016:1433 */         returnNode = node;
/* 1017:     */       }
/* 1018:1434 */       else if ((node.m_Alpha == baseAlpha) && 
/* 1019:1435 */         (node.numLeaves() > returnNode.numLeaves()))
/* 1020:     */       {
/* 1021:1436 */         returnNode = node;
/* 1022:     */       }
/* 1023:     */     }
/* 1024:1440 */     return returnNode;
/* 1025:     */   }
/* 1026:     */   
/* 1027:     */   protected double computeSortedInfo(Instances data, int[][] sortedIndices, double[][] weights, double[] classProbs)
/* 1028:     */     throws Exception
/* 1029:     */   {
/* 1030:1458 */     double[] vals = new double[data.numInstances()];
/* 1031:1459 */     for (int j = 0; j < data.numAttributes(); j++) {
/* 1032:1460 */       if (j != data.classIndex())
/* 1033:     */       {
/* 1034:1463 */         weights[j] = new double[data.numInstances()];
/* 1035:1465 */         if (data.attribute(j).isNominal())
/* 1036:     */         {
/* 1037:1469 */           sortedIndices[j] = new int[data.numInstances()];
/* 1038:1470 */           int count = 0;
/* 1039:1471 */           for (int i = 0; i < data.numInstances(); i++)
/* 1040:     */           {
/* 1041:1472 */             Instance inst = data.instance(i);
/* 1042:1473 */             if (!inst.isMissing(j))
/* 1043:     */             {
/* 1044:1474 */               sortedIndices[j][count] = i;
/* 1045:1475 */               weights[j][count] = inst.weight();
/* 1046:1476 */               count++;
/* 1047:     */             }
/* 1048:     */           }
/* 1049:1479 */           for (int i = 0; i < data.numInstances(); i++)
/* 1050:     */           {
/* 1051:1480 */             Instance inst = data.instance(i);
/* 1052:1481 */             if (inst.isMissing(j))
/* 1053:     */             {
/* 1054:1482 */               sortedIndices[j][count] = i;
/* 1055:1483 */               weights[j][count] = inst.weight();
/* 1056:1484 */               count++;
/* 1057:     */             }
/* 1058:     */           }
/* 1059:     */         }
/* 1060:     */         else
/* 1061:     */         {
/* 1062:1491 */           for (int i = 0; i < data.numInstances(); i++)
/* 1063:     */           {
/* 1064:1492 */             Instance inst = data.instance(i);
/* 1065:1493 */             vals[i] = inst.value(j);
/* 1066:     */           }
/* 1067:1495 */           sortedIndices[j] = Utils.sort(vals);
/* 1068:1496 */           for (int i = 0; i < data.numInstances(); i++) {
/* 1069:1497 */             weights[j][i] = data.instance(sortedIndices[j][i]).weight();
/* 1070:     */           }
/* 1071:     */         }
/* 1072:     */       }
/* 1073:     */     }
/* 1074:1503 */     double totalWeight = 0.0D;
/* 1075:1504 */     for (int i = 0; i < data.numInstances(); i++)
/* 1076:     */     {
/* 1077:1505 */       Instance inst = data.instance(i);
/* 1078:1506 */       classProbs[((int)inst.classValue())] += inst.weight();
/* 1079:1507 */       totalWeight += inst.weight();
/* 1080:     */     }
/* 1081:1510 */     return totalWeight;
/* 1082:     */   }
/* 1083:     */   
/* 1084:     */   protected double computeGiniGain(double[] parentDist, double[][] childDist)
/* 1085:     */   {
/* 1086:1522 */     double totalWeight = Utils.sum(parentDist);
/* 1087:1523 */     if (totalWeight == 0.0D) {
/* 1088:1524 */       return 0.0D;
/* 1089:     */     }
/* 1090:1527 */     double leftWeight = Utils.sum(childDist[0]);
/* 1091:1528 */     double rightWeight = Utils.sum(childDist[1]);
/* 1092:     */     
/* 1093:1530 */     double parentGini = computeGini(parentDist, totalWeight);
/* 1094:1531 */     double leftGini = computeGini(childDist[0], leftWeight);
/* 1095:1532 */     double rightGini = computeGini(childDist[1], rightWeight);
/* 1096:     */     
/* 1097:1534 */     return parentGini - leftWeight / totalWeight * leftGini - rightWeight / totalWeight * rightGini;
/* 1098:     */   }
/* 1099:     */   
/* 1100:     */   protected double computeGini(double[] dist, double total)
/* 1101:     */   {
/* 1102:1546 */     if (total == 0.0D) {
/* 1103:1547 */       return 0.0D;
/* 1104:     */     }
/* 1105:1549 */     double val = 0.0D;
/* 1106:1550 */     for (double element : dist) {
/* 1107:1551 */       val += element / total * (element / total);
/* 1108:     */     }
/* 1109:1553 */     return 1.0D - val;
/* 1110:     */   }
/* 1111:     */   
/* 1112:     */   public double[] distributionForInstance(Instance instance)
/* 1113:     */     throws Exception
/* 1114:     */   {
/* 1115:1566 */     if (!this.m_isLeaf)
/* 1116:     */     {
/* 1117:1568 */       if (instance.isMissing(this.m_Attribute))
/* 1118:     */       {
/* 1119:1569 */         double[] returnedDist = new double[this.m_ClassProbs.length];
/* 1120:1571 */         for (int i = 0; i < this.m_Successors.length; i++)
/* 1121:     */         {
/* 1122:1572 */           double[] help = this.m_Successors[i].distributionForInstance(instance);
/* 1123:1573 */           if (help != null) {
/* 1124:1574 */             for (int j = 0; j < help.length; j++) {
/* 1125:1575 */               returnedDist[j] += this.m_Props[i] * help[j];
/* 1126:     */             }
/* 1127:     */           }
/* 1128:     */         }
/* 1129:1579 */         return returnedDist;
/* 1130:     */       }
/* 1131:1583 */       if (this.m_Attribute.isNominal())
/* 1132:     */       {
/* 1133:1584 */         if (this.m_SplitString.indexOf("(" + this.m_Attribute.value((int)instance.value(this.m_Attribute)) + ")") != -1) {
/* 1134:1586 */           return this.m_Successors[0].distributionForInstance(instance);
/* 1135:     */         }
/* 1136:1588 */         return this.m_Successors[1].distributionForInstance(instance);
/* 1137:     */       }
/* 1138:1594 */       if (instance.value(this.m_Attribute) < this.m_SplitValue) {
/* 1139:1595 */         return this.m_Successors[0].distributionForInstance(instance);
/* 1140:     */       }
/* 1141:1597 */       return this.m_Successors[1].distributionForInstance(instance);
/* 1142:     */     }
/* 1143:1601 */     return this.m_ClassProbs;
/* 1144:     */   }
/* 1145:     */   
/* 1146:     */   protected void makeLeaf(Instances data)
/* 1147:     */   {
/* 1148:1611 */     this.m_Attribute = null;
/* 1149:1612 */     this.m_isLeaf = true;
/* 1150:1613 */     this.m_ClassValue = Utils.maxIndex(this.m_ClassProbs);
/* 1151:1614 */     this.m_ClassAttribute = data.classAttribute();
/* 1152:     */   }
/* 1153:     */   
/* 1154:     */   public String toString()
/* 1155:     */   {
/* 1156:1624 */     if ((this.m_ClassProbs == null) && (this.m_Successors == null)) {
/* 1157:1625 */       return "CART Tree: No model built yet.";
/* 1158:     */     }
/* 1159:1628 */     return "CART Decision Tree\n" + toString(0) + "\n\n" + "Number of Leaf Nodes: " + numLeaves() + "\n\n" + "Size of the Tree: " + numNodes();
/* 1160:     */   }
/* 1161:     */   
/* 1162:     */   protected String toString(int level)
/* 1163:     */   {
/* 1164:1641 */     StringBuffer text = new StringBuffer();
/* 1165:1643 */     if (this.m_Attribute == null)
/* 1166:     */     {
/* 1167:1644 */       if (Utils.isMissingValue(this.m_ClassValue))
/* 1168:     */       {
/* 1169:1645 */         text.append(": null");
/* 1170:     */       }
/* 1171:     */       else
/* 1172:     */       {
/* 1173:1647 */         double correctNum = (int)(this.m_Distribution[Utils.maxIndex(this.m_Distribution)] * 100.0D) / 100.0D;
/* 1174:     */         
/* 1175:1649 */         double wrongNum = (int)((Utils.sum(this.m_Distribution) - this.m_Distribution[Utils.maxIndex(this.m_Distribution)]) * 100.0D) / 100.0D;
/* 1176:     */         
/* 1177:1651 */         String str = "(" + correctNum + "/" + wrongNum + ")";
/* 1178:1652 */         text.append(": " + this.m_ClassAttribute.value((int)this.m_ClassValue) + str);
/* 1179:     */       }
/* 1180:     */     }
/* 1181:     */     else {
/* 1182:1655 */       for (int j = 0; j < 2; j++)
/* 1183:     */       {
/* 1184:1656 */         text.append("\n");
/* 1185:1657 */         for (int i = 0; i < level; i++) {
/* 1186:1658 */           text.append("|  ");
/* 1187:     */         }
/* 1188:1660 */         if (j == 0)
/* 1189:     */         {
/* 1190:1661 */           if (this.m_Attribute.isNumeric()) {
/* 1191:1662 */             text.append(this.m_Attribute.name() + " < " + this.m_SplitValue);
/* 1192:     */           } else {
/* 1193:1664 */             text.append(this.m_Attribute.name() + "=" + this.m_SplitString);
/* 1194:     */           }
/* 1195:     */         }
/* 1196:1667 */         else if (this.m_Attribute.isNumeric()) {
/* 1197:1668 */           text.append(this.m_Attribute.name() + " >= " + this.m_SplitValue);
/* 1198:     */         } else {
/* 1199:1670 */           text.append(this.m_Attribute.name() + "!=" + this.m_SplitString);
/* 1200:     */         }
/* 1201:1673 */         text.append(this.m_Successors[j].toString(level + 1));
/* 1202:     */       }
/* 1203:     */     }
/* 1204:1676 */     return text.toString();
/* 1205:     */   }
/* 1206:     */   
/* 1207:     */   public int numNodes()
/* 1208:     */   {
/* 1209:1685 */     if (this.m_isLeaf) {
/* 1210:1686 */       return 1;
/* 1211:     */     }
/* 1212:1688 */     int size = 1;
/* 1213:1689 */     for (SimpleCart m_Successor : this.m_Successors) {
/* 1214:1690 */       size += m_Successor.numNodes();
/* 1215:     */     }
/* 1216:1692 */     return size;
/* 1217:     */   }
/* 1218:     */   
/* 1219:     */   public int numInnerNodes()
/* 1220:     */   {
/* 1221:1702 */     if (this.m_Attribute == null) {
/* 1222:1703 */       return 0;
/* 1223:     */     }
/* 1224:1705 */     int numNodes = 1;
/* 1225:1706 */     for (SimpleCart m_Successor : this.m_Successors) {
/* 1226:1707 */       numNodes += m_Successor.numInnerNodes();
/* 1227:     */     }
/* 1228:1709 */     return numNodes;
/* 1229:     */   }
/* 1230:     */   
/* 1231:     */   protected Vector<SimpleCart> getInnerNodes()
/* 1232:     */   {
/* 1233:1718 */     Vector<SimpleCart> nodeList = new Vector();
/* 1234:1719 */     fillInnerNodes(nodeList);
/* 1235:1720 */     return nodeList;
/* 1236:     */   }
/* 1237:     */   
/* 1238:     */   protected void fillInnerNodes(Vector<SimpleCart> nodeList)
/* 1239:     */   {
/* 1240:1729 */     if (!this.m_isLeaf)
/* 1241:     */     {
/* 1242:1730 */       nodeList.add(this);
/* 1243:1731 */       for (SimpleCart m_Successor : this.m_Successors) {
/* 1244:1732 */         m_Successor.fillInnerNodes(nodeList);
/* 1245:     */       }
/* 1246:     */     }
/* 1247:     */   }
/* 1248:     */   
/* 1249:     */   public int numLeaves()
/* 1250:     */   {
/* 1251:1743 */     if (this.m_isLeaf) {
/* 1252:1744 */       return 1;
/* 1253:     */     }
/* 1254:1746 */     int size = 0;
/* 1255:1747 */     for (SimpleCart m_Successor : this.m_Successors) {
/* 1256:1748 */       size += m_Successor.numLeaves();
/* 1257:     */     }
/* 1258:1750 */     return size;
/* 1259:     */   }
/* 1260:     */   
/* 1261:     */   public Enumeration<Option> listOptions()
/* 1262:     */   {
/* 1263:1762 */     Vector<Option> result = new Vector(6);
/* 1264:     */     
/* 1265:1764 */     result.addElement(new Option("\tThe minimal number of instances at the terminal nodes.\n\t(default 2)", "M", 1, "-M <min no>"));
/* 1266:     */     
/* 1267:     */ 
/* 1268:     */ 
/* 1269:1768 */     result.addElement(new Option("\tThe number of folds used in the minimal cost-complexity pruning.\n\t(default 5)", "N", 1, "-N <num folds>"));
/* 1270:     */     
/* 1271:     */ 
/* 1272:     */ 
/* 1273:1772 */     result.addElement(new Option("\tDon't use the minimal cost-complexity pruning.\n\t(default yes).", "U", 0, "-U"));
/* 1274:     */     
/* 1275:     */ 
/* 1276:     */ 
/* 1277:     */ 
/* 1278:1777 */     result.addElement(new Option("\tDon't use the heuristic method for binary split.\n\t(default true).", "H", 0, "-H"));
/* 1279:     */     
/* 1280:     */ 
/* 1281:     */ 
/* 1282:1781 */     result.addElement(new Option("\tUse 1 SE rule to make pruning decision.\n\t(default no).", "A", 0, "-A"));
/* 1283:     */     
/* 1284:     */ 
/* 1285:1784 */     result.addElement(new Option("\tPercentage of training data size (0-1].\n\t(default 1).", "C", 1, "-C"));
/* 1286:     */     
/* 1287:     */ 
/* 1288:1787 */     result.addAll(Collections.list(super.listOptions()));
/* 1289:     */     
/* 1290:1789 */     return result.elements();
/* 1291:     */   }
/* 1292:     */   
/* 1293:     */   public void setOptions(String[] options)
/* 1294:     */     throws Exception
/* 1295:     */   {
/* 1296:1850 */     String tmpStr = Utils.getOption('M', options);
/* 1297:1851 */     if (tmpStr.length() != 0) {
/* 1298:1852 */       setMinNumObj(Double.parseDouble(tmpStr));
/* 1299:     */     } else {
/* 1300:1854 */       setMinNumObj(2.0D);
/* 1301:     */     }
/* 1302:1857 */     tmpStr = Utils.getOption('N', options);
/* 1303:1858 */     if (tmpStr.length() != 0) {
/* 1304:1859 */       setNumFoldsPruning(Integer.parseInt(tmpStr));
/* 1305:     */     } else {
/* 1306:1861 */       setNumFoldsPruning(5);
/* 1307:     */     }
/* 1308:1864 */     setUsePrune(!Utils.getFlag('U', options));
/* 1309:1865 */     setHeuristic(!Utils.getFlag('H', options));
/* 1310:1866 */     setUseOneSE(Utils.getFlag('A', options));
/* 1311:     */     
/* 1312:1868 */     tmpStr = Utils.getOption('C', options);
/* 1313:1869 */     if (tmpStr.length() != 0) {
/* 1314:1870 */       setSizePer(Double.parseDouble(tmpStr));
/* 1315:     */     } else {
/* 1316:1872 */       setSizePer(1.0D);
/* 1317:     */     }
/* 1318:1875 */     super.setOptions(options);
/* 1319:     */     
/* 1320:1877 */     Utils.checkForRemainingOptions(options);
/* 1321:     */   }
/* 1322:     */   
/* 1323:     */   public String[] getOptions()
/* 1324:     */   {
/* 1325:1888 */     Vector<String> result = new Vector();
/* 1326:     */     
/* 1327:1890 */     result.add("-M");
/* 1328:1891 */     result.add("" + getMinNumObj());
/* 1329:     */     
/* 1330:1893 */     result.add("-N");
/* 1331:1894 */     result.add("" + getNumFoldsPruning());
/* 1332:1896 */     if (!getUsePrune()) {
/* 1333:1897 */       result.add("-U");
/* 1334:     */     }
/* 1335:1900 */     if (!getHeuristic()) {
/* 1336:1901 */       result.add("-H");
/* 1337:     */     }
/* 1338:1904 */     if (getUseOneSE()) {
/* 1339:1905 */       result.add("-A");
/* 1340:     */     }
/* 1341:1908 */     result.add("-C");
/* 1342:1909 */     result.add("" + getSizePer());
/* 1343:     */     
/* 1344:1911 */     Collections.addAll(result, super.getOptions());
/* 1345:     */     
/* 1346:1913 */     return (String[])result.toArray(new String[result.size()]);
/* 1347:     */   }
/* 1348:     */   
/* 1349:     */   public Enumeration<String> enumerateMeasures()
/* 1350:     */   {
/* 1351:1923 */     Vector<String> result = new Vector();
/* 1352:     */     
/* 1353:1925 */     result.addElement("measureTreeSize");
/* 1354:     */     
/* 1355:1927 */     return result.elements();
/* 1356:     */   }
/* 1357:     */   
/* 1358:     */   public double measureTreeSize()
/* 1359:     */   {
/* 1360:1936 */     return numNodes();
/* 1361:     */   }
/* 1362:     */   
/* 1363:     */   public double getMeasure(String additionalMeasureName)
/* 1364:     */   {
/* 1365:1948 */     if (additionalMeasureName.compareToIgnoreCase("measureTreeSize") == 0) {
/* 1366:1949 */       return measureTreeSize();
/* 1367:     */     }
/* 1368:1951 */     throw new IllegalArgumentException(additionalMeasureName + " not supported (Cart pruning)");
/* 1369:     */   }
/* 1370:     */   
/* 1371:     */   public String minNumObjTipText()
/* 1372:     */   {
/* 1373:1963 */     return "The minimal number of observations at the terminal nodes (default 2).";
/* 1374:     */   }
/* 1375:     */   
/* 1376:     */   public void setMinNumObj(double value)
/* 1377:     */   {
/* 1378:1972 */     this.m_minNumObj = value;
/* 1379:     */   }
/* 1380:     */   
/* 1381:     */   public double getMinNumObj()
/* 1382:     */   {
/* 1383:1981 */     return this.m_minNumObj;
/* 1384:     */   }
/* 1385:     */   
/* 1386:     */   public String numFoldsPruningTipText()
/* 1387:     */   {
/* 1388:1991 */     return "The number of folds in the internal cross-validation (default 5).";
/* 1389:     */   }
/* 1390:     */   
/* 1391:     */   public void setNumFoldsPruning(int value)
/* 1392:     */   {
/* 1393:2000 */     this.m_numFoldsPruning = value;
/* 1394:     */   }
/* 1395:     */   
/* 1396:     */   public int getNumFoldsPruning()
/* 1397:     */   {
/* 1398:2009 */     return this.m_numFoldsPruning;
/* 1399:     */   }
/* 1400:     */   
/* 1401:     */   public String usePruneTipText()
/* 1402:     */   {
/* 1403:2019 */     return "Use minimal cost-complexity pruning (default yes).";
/* 1404:     */   }
/* 1405:     */   
/* 1406:     */   public void setUsePrune(boolean value)
/* 1407:     */   {
/* 1408:2028 */     this.m_Prune = value;
/* 1409:     */   }
/* 1410:     */   
/* 1411:     */   public boolean getUsePrune()
/* 1412:     */   {
/* 1413:2037 */     return this.m_Prune;
/* 1414:     */   }
/* 1415:     */   
/* 1416:     */   public String heuristicTipText()
/* 1417:     */   {
/* 1418:2047 */     return "If heuristic search is used for binary split for nominal attributes in multi-class problems (default yes).";
/* 1419:     */   }
/* 1420:     */   
/* 1421:     */   public void setHeuristic(boolean value)
/* 1422:     */   {
/* 1423:2058 */     this.m_Heuristic = value;
/* 1424:     */   }
/* 1425:     */   
/* 1426:     */   public boolean getHeuristic()
/* 1427:     */   {
/* 1428:2068 */     return this.m_Heuristic;
/* 1429:     */   }
/* 1430:     */   
/* 1431:     */   public String useOneSETipText()
/* 1432:     */   {
/* 1433:2078 */     return "Use the 1SE rule to make pruning decisoin.";
/* 1434:     */   }
/* 1435:     */   
/* 1436:     */   public void setUseOneSE(boolean value)
/* 1437:     */   {
/* 1438:2087 */     this.m_UseOneSE = value;
/* 1439:     */   }
/* 1440:     */   
/* 1441:     */   public boolean getUseOneSE()
/* 1442:     */   {
/* 1443:2096 */     return this.m_UseOneSE;
/* 1444:     */   }
/* 1445:     */   
/* 1446:     */   public String sizePerTipText()
/* 1447:     */   {
/* 1448:2106 */     return "The percentage of the training set size (0-1, 0 not included).";
/* 1449:     */   }
/* 1450:     */   
/* 1451:     */   public void setSizePer(double value)
/* 1452:     */   {
/* 1453:2115 */     if ((value <= 0.0D) || (value > 1.0D)) {
/* 1454:2116 */       System.err.println("The percentage of the training set size must be in range 0 to 1 (0 not included) - ignored!");
/* 1455:     */     } else {
/* 1456:2120 */       this.m_SizePer = value;
/* 1457:     */     }
/* 1458:     */   }
/* 1459:     */   
/* 1460:     */   public double getSizePer()
/* 1461:     */   {
/* 1462:2130 */     return this.m_SizePer;
/* 1463:     */   }
/* 1464:     */   
/* 1465:     */   public String getRevision()
/* 1466:     */   {
/* 1467:2140 */     return RevisionUtils.extract("$Revision: 10490 $");
/* 1468:     */   }
/* 1469:     */   
/* 1470:     */   public static void main(String[] args)
/* 1471:     */   {
/* 1472:2149 */     runClassifier(new SimpleCart(), args);
/* 1473:     */   }
/* 1474:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.SimpleCart
 * JD-Core Version:    0.7.0.1
 */