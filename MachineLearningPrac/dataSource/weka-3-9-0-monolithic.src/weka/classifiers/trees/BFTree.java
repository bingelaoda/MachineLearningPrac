/*    1:     */ package weka.classifiers.trees;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.util.ArrayList;
/*    5:     */ import java.util.Arrays;
/*    6:     */ import java.util.Collections;
/*    7:     */ import java.util.Enumeration;
/*    8:     */ import java.util.Random;
/*    9:     */ import java.util.Vector;
/*   10:     */ import weka.classifiers.Evaluation;
/*   11:     */ import weka.classifiers.RandomizableClassifier;
/*   12:     */ import weka.core.AdditionalMeasureProducer;
/*   13:     */ import weka.core.Attribute;
/*   14:     */ import weka.core.Capabilities;
/*   15:     */ import weka.core.Capabilities.Capability;
/*   16:     */ import weka.core.Instance;
/*   17:     */ import weka.core.Instances;
/*   18:     */ import weka.core.Option;
/*   19:     */ import weka.core.RevisionUtils;
/*   20:     */ import weka.core.SelectedTag;
/*   21:     */ import weka.core.Tag;
/*   22:     */ import weka.core.TechnicalInformation;
/*   23:     */ import weka.core.TechnicalInformation.Field;
/*   24:     */ import weka.core.TechnicalInformation.Type;
/*   25:     */ import weka.core.TechnicalInformationHandler;
/*   26:     */ import weka.core.Utils;
/*   27:     */ import weka.core.matrix.EigenvalueDecomposition;
/*   28:     */ import weka.core.matrix.Matrix;
/*   29:     */ 
/*   30:     */ public class BFTree
/*   31:     */   extends RandomizableClassifier
/*   32:     */   implements AdditionalMeasureProducer, TechnicalInformationHandler
/*   33:     */ {
/*   34:     */   private static final long serialVersionUID = -7035607375962528217L;
/*   35:     */   public static final int PRUNING_UNPRUNED = 0;
/*   36:     */   public static final int PRUNING_POSTPRUNING = 1;
/*   37:     */   public static final int PRUNING_PREPRUNING = 2;
/*   38: 172 */   public static final Tag[] TAGS_PRUNING = { new Tag(0, "unpruned", "Un-pruned"), new Tag(1, "postpruned", "Post-pruning"), new Tag(2, "prepruned", "Pre-pruning") };
/*   39: 178 */   protected int m_PruningStrategy = 1;
/*   40:     */   protected BFTree[] m_Successors;
/*   41:     */   protected Attribute m_Attribute;
/*   42:     */   protected double m_SplitValue;
/*   43:     */   protected String m_SplitString;
/*   44:     */   protected double m_ClassValue;
/*   45:     */   protected Attribute m_ClassAttribute;
/*   46: 199 */   protected int m_minNumObj = 2;
/*   47: 202 */   protected int m_numFoldsPruning = 5;
/*   48:     */   protected boolean m_isLeaf;
/*   49:     */   protected static int m_Expansion;
/*   50: 214 */   protected int m_FixedExpansion = -1;
/*   51: 221 */   protected boolean m_Heuristic = true;
/*   52: 227 */   protected boolean m_UseGini = true;
/*   53: 233 */   protected boolean m_UseErrorRate = true;
/*   54: 236 */   protected boolean m_UseOneSE = false;
/*   55:     */   protected double[] m_Distribution;
/*   56:     */   protected double[] m_Props;
/*   57:     */   protected int[][] m_SortedIndices;
/*   58:     */   protected double[][] m_Weights;
/*   59:     */   protected double[][][] m_Dists;
/*   60:     */   protected double[] m_ClassProbs;
/*   61:     */   protected double m_TotalWeight;
/*   62: 260 */   protected double m_SizePer = 1.0D;
/*   63:     */   
/*   64:     */   public String globalInfo()
/*   65:     */   {
/*   66: 269 */     return "Class for building a best-first decision tree classifier. This class uses binary split for both nominal and numeric attributes. For missing values, the method of 'fractional' instances is used.\n\nFor more information, see:\n\n" + getTechnicalInformation().toString();
/*   67:     */   }
/*   68:     */   
/*   69:     */   public TechnicalInformation getTechnicalInformation()
/*   70:     */   {
/*   71: 287 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.MASTERSTHESIS);
/*   72: 288 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Haijian Shi");
/*   73: 289 */     result.setValue(TechnicalInformation.Field.YEAR, "2007");
/*   74: 290 */     result.setValue(TechnicalInformation.Field.TITLE, "Best-first decision tree learning");
/*   75: 291 */     result.setValue(TechnicalInformation.Field.SCHOOL, "University of Waikato");
/*   76: 292 */     result.setValue(TechnicalInformation.Field.ADDRESS, "Hamilton, NZ");
/*   77: 293 */     result.setValue(TechnicalInformation.Field.NOTE, "COMP594");
/*   78:     */     
/*   79: 295 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.ARTICLE);
/*   80: 296 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "Jerome Friedman and Trevor Hastie and Robert Tibshirani");
/*   81:     */     
/*   82: 298 */     additional.setValue(TechnicalInformation.Field.YEAR, "2000");
/*   83: 299 */     additional.setValue(TechnicalInformation.Field.TITLE, "Additive logistic regression : A statistical view of boosting");
/*   84:     */     
/*   85: 301 */     additional.setValue(TechnicalInformation.Field.JOURNAL, "Annals of statistics");
/*   86: 302 */     additional.setValue(TechnicalInformation.Field.VOLUME, "28");
/*   87: 303 */     additional.setValue(TechnicalInformation.Field.NUMBER, "2");
/*   88: 304 */     additional.setValue(TechnicalInformation.Field.PAGES, "337-407");
/*   89: 305 */     additional.setValue(TechnicalInformation.Field.ISSN, "0090-5364");
/*   90:     */     
/*   91: 307 */     return result;
/*   92:     */   }
/*   93:     */   
/*   94:     */   public Capabilities getCapabilities()
/*   95:     */   {
/*   96: 317 */     Capabilities result = super.getCapabilities();
/*   97: 318 */     result.disableAll();
/*   98:     */     
/*   99:     */ 
/*  100: 321 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  101: 322 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  102: 323 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  103:     */     
/*  104:     */ 
/*  105: 326 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  106:     */     
/*  107: 328 */     return result;
/*  108:     */   }
/*  109:     */   
/*  110:     */   public void buildClassifier(Instances data)
/*  111:     */     throws Exception
/*  112:     */   {
/*  113: 340 */     getCapabilities().testWithFail(data);
/*  114: 341 */     data = new Instances(data);
/*  115: 342 */     data.deleteWithMissingClass();
/*  116: 345 */     if (this.m_PruningStrategy == 0)
/*  117:     */     {
/*  118: 348 */       int[][] sortedIndices = new int[data.numAttributes()][0];
/*  119: 349 */       double[][] weights = new double[data.numAttributes()][0];
/*  120: 350 */       double[] classProbs = new double[data.numClasses()];
/*  121: 351 */       double totalWeight = computeSortedInfo(data, sortedIndices, weights, classProbs);
/*  122:     */       
/*  123:     */ 
/*  124:     */ 
/*  125:     */ 
/*  126:     */ 
/*  127:     */ 
/*  128:     */ 
/*  129: 359 */       double[][][] dists = new double[data.numAttributes()][2][data.numClasses()];
/*  130:     */       
/*  131: 361 */       double[][] props = new double[data.numAttributes()][2];
/*  132: 362 */       double[][] totalSubsetWeights = new double[data.numAttributes()][2];
/*  133: 363 */       ArrayList<Object> nodeInfo = computeSplitInfo(this, data, sortedIndices, weights, dists, props, totalSubsetWeights, this.m_Heuristic, this.m_UseGini);
/*  134:     */       
/*  135:     */ 
/*  136:     */ 
/*  137: 367 */       ArrayList<ArrayList<Object>> BestFirstElements = new ArrayList();
/*  138: 368 */       BestFirstElements.add(nodeInfo);
/*  139:     */       
/*  140:     */ 
/*  141: 371 */       int attIndex = ((Attribute)nodeInfo.get(1)).index();
/*  142: 372 */       m_Expansion = 0;
/*  143: 373 */       makeTree(BestFirstElements, data, sortedIndices, weights, dists, classProbs, totalWeight, props[attIndex], this.m_minNumObj, this.m_Heuristic, this.m_UseGini, this.m_FixedExpansion);
/*  144:     */       
/*  145:     */ 
/*  146:     */ 
/*  147: 377 */       return;
/*  148:     */     }
/*  149: 387 */     int expansion = 0;
/*  150:     */     
/*  151: 389 */     Random random = new Random(this.m_Seed);
/*  152: 390 */     Instances cvData = new Instances(data);
/*  153: 391 */     cvData.randomize(random);
/*  154: 392 */     cvData = new Instances(cvData, 0, (int)(cvData.numInstances() * this.m_SizePer) - 1);
/*  155:     */     
/*  156: 394 */     cvData.stratify(this.m_numFoldsPruning);
/*  157:     */     
/*  158: 396 */     Instances[] train = new Instances[this.m_numFoldsPruning];
/*  159: 397 */     Instances[] test = new Instances[this.m_numFoldsPruning];
/*  160:     */     
/*  161: 399 */     ArrayList<ArrayList<Object>>[] parallelBFElements = new ArrayList[this.m_numFoldsPruning];
/*  162: 400 */     BFTree[] m_roots = new BFTree[this.m_numFoldsPruning];
/*  163:     */     
/*  164: 402 */     int[][][] sortedIndices = new int[this.m_numFoldsPruning][data.numAttributes()][0];
/*  165: 403 */     double[][][] weights = new double[this.m_numFoldsPruning][data.numAttributes()][0];
/*  166: 404 */     double[][] classProbs = new double[this.m_numFoldsPruning][data.numClasses()];
/*  167: 405 */     double[] totalWeight = new double[this.m_numFoldsPruning];
/*  168:     */     
/*  169: 407 */     double[][][][] dists = new double[this.m_numFoldsPruning][data.numAttributes()][2][data.numClasses()];
/*  170:     */     
/*  171: 409 */     double[][][] props = new double[this.m_numFoldsPruning][data.numAttributes()][2];
/*  172: 410 */     double[][][] totalSubsetWeights = new double[this.m_numFoldsPruning][data.numAttributes()][2];
/*  173:     */     
/*  174:     */ 
/*  175: 413 */     ArrayList<Object>[] nodeInfo = new ArrayList[this.m_numFoldsPruning];
/*  176: 415 */     for (int i = 0; i < this.m_numFoldsPruning; i++)
/*  177:     */     {
/*  178: 416 */       train[i] = cvData.trainCV(this.m_numFoldsPruning, i);
/*  179: 417 */       test[i] = cvData.testCV(this.m_numFoldsPruning, i);
/*  180: 418 */       parallelBFElements[i] = new ArrayList();
/*  181: 419 */       m_roots[i] = new BFTree();
/*  182:     */       
/*  183:     */ 
/*  184:     */ 
/*  185: 423 */       totalWeight[i] = computeSortedInfo(train[i], sortedIndices[i], weights[i], classProbs[i]);
/*  186:     */       
/*  187:     */ 
/*  188:     */ 
/*  189:     */ 
/*  190:     */ 
/*  191: 429 */       nodeInfo[i] = computeSplitInfo(m_roots[i], train[i], sortedIndices[i], weights[i], dists[i], props[i], totalSubsetWeights[i], this.m_Heuristic, this.m_UseGini);
/*  192:     */       
/*  193:     */ 
/*  194:     */ 
/*  195:     */ 
/*  196:     */ 
/*  197: 435 */       int attIndex = ((Attribute)nodeInfo[i].get(1)).index();
/*  198:     */       
/*  199: 437 */       m_roots[i].m_SortedIndices = new int[sortedIndices[i].length][0];
/*  200: 438 */       m_roots[i].m_Weights = new double[weights[i].length][0];
/*  201: 439 */       m_roots[i].m_Dists = new double[dists[i].length][0][0];
/*  202: 440 */       m_roots[i].m_ClassProbs = new double[classProbs[i].length];
/*  203: 441 */       m_roots[i].m_Distribution = new double[classProbs[i].length];
/*  204: 442 */       m_roots[i].m_Props = new double[2];
/*  205: 444 */       for (int j = 0; j < m_roots[i].m_SortedIndices.length; j++)
/*  206:     */       {
/*  207: 445 */         m_roots[i].m_SortedIndices[j] = sortedIndices[i][j];
/*  208: 446 */         m_roots[i].m_Weights[j] = weights[i][j];
/*  209: 447 */         m_roots[i].m_Dists[j] = dists[i][j];
/*  210:     */       }
/*  211: 450 */       System.arraycopy(classProbs[i], 0, m_roots[i].m_ClassProbs, 0, classProbs[i].length);
/*  212: 452 */       if (Utils.sum(m_roots[i].m_ClassProbs) != 0.0D) {
/*  213: 453 */         Utils.normalize(m_roots[i].m_ClassProbs);
/*  214:     */       }
/*  215: 456 */       System.arraycopy(classProbs[i], 0, m_roots[i].m_Distribution, 0, classProbs[i].length);
/*  216:     */       
/*  217: 458 */       System.arraycopy(props[i][attIndex], 0, m_roots[i].m_Props, 0, props[i][attIndex].length);
/*  218:     */       
/*  219:     */ 
/*  220: 461 */       m_roots[i].m_TotalWeight = totalWeight[i];
/*  221:     */       
/*  222: 463 */       parallelBFElements[i].add(nodeInfo[i]);
/*  223:     */     }
/*  224: 467 */     if (this.m_PruningStrategy == 2)
/*  225:     */     {
/*  226: 469 */       double previousError = 1.7976931348623157E+308D;
/*  227: 470 */       double currentError = previousError;
/*  228: 471 */       double minError = 1.7976931348623157E+308D;
/*  229: 472 */       ArrayList<Double> errorList = new ArrayList();
/*  230:     */       for (;;)
/*  231:     */       {
/*  232: 475 */         double expansionError = 0.0D;
/*  233: 476 */         int count = 0;
/*  234: 478 */         for (int i = 0; i < this.m_numFoldsPruning; i++) {
/*  235: 482 */           if (expansion == 0)
/*  236:     */           {
/*  237: 483 */             m_roots[i].m_isLeaf = true;
/*  238: 484 */             Evaluation eval = new Evaluation(test[i]);
/*  239: 485 */             eval.evaluateModel(m_roots[i], test[i], new Object[0]);
/*  240: 486 */             if (this.m_UseErrorRate) {
/*  241: 487 */               expansionError += eval.errorRate();
/*  242:     */             } else {
/*  243: 489 */               expansionError += eval.rootMeanSquaredError();
/*  244:     */             }
/*  245: 491 */             count++;
/*  246:     */           }
/*  247: 496 */           else if (m_roots[i] != null)
/*  248:     */           {
/*  249: 499 */             m_roots[i].m_isLeaf = false;
/*  250: 500 */             BFTree nodeToSplit = (BFTree)((ArrayList)parallelBFElements[i].get(0)).get(0);
/*  251: 502 */             if (!m_roots[i].makeTree(parallelBFElements[i], m_roots[i], train[i], nodeToSplit.m_SortedIndices, nodeToSplit.m_Weights, nodeToSplit.m_Dists, nodeToSplit.m_ClassProbs, nodeToSplit.m_TotalWeight, nodeToSplit.m_Props, this.m_minNumObj, this.m_Heuristic, this.m_UseGini))
/*  252:     */             {
/*  253: 507 */               m_roots[i] = null;
/*  254:     */             }
/*  255:     */             else
/*  256:     */             {
/*  257: 510 */               Evaluation eval = new Evaluation(test[i]);
/*  258: 511 */               eval.evaluateModel(m_roots[i], test[i], new Object[0]);
/*  259: 512 */               if (this.m_UseErrorRate) {
/*  260: 513 */                 expansionError += eval.errorRate();
/*  261:     */               } else {
/*  262: 515 */                 expansionError += eval.rootMeanSquaredError();
/*  263:     */               }
/*  264: 517 */               count++;
/*  265:     */             }
/*  266:     */           }
/*  267:     */         }
/*  268: 522 */         if (count == 0) {
/*  269:     */           break;
/*  270:     */         }
/*  271: 526 */         expansionError /= count;
/*  272: 527 */         errorList.add(new Double(expansionError));
/*  273: 528 */         currentError = expansionError;
/*  274: 530 */         if (!this.m_UseOneSE)
/*  275:     */         {
/*  276: 531 */           if (currentError > previousError) {
/*  277:     */             break;
/*  278:     */           }
/*  279:     */         }
/*  280:     */         else
/*  281:     */         {
/*  282: 537 */           if (expansionError < minError) {
/*  283: 538 */             minError = expansionError;
/*  284:     */           }
/*  285: 541 */           if (currentError > previousError)
/*  286:     */           {
/*  287: 542 */             double oneSE = Math.sqrt(minError * (1.0D - minError) / data.numInstances());
/*  288: 544 */             if (currentError > minError + oneSE) {
/*  289:     */               break;
/*  290:     */             }
/*  291:     */           }
/*  292:     */         }
/*  293: 550 */         expansion++;
/*  294: 551 */         previousError = currentError;
/*  295:     */       }
/*  296: 554 */       if (!this.m_UseOneSE)
/*  297:     */       {
/*  298: 555 */         expansion -= 1;
/*  299:     */       }
/*  300:     */       else
/*  301:     */       {
/*  302: 557 */         double oneSE = Math.sqrt(minError * (1.0D - minError) / data.numInstances());
/*  303: 559 */         for (int i = 0; i < errorList.size(); i++)
/*  304:     */         {
/*  305: 560 */           double error = ((Double)errorList.get(i)).doubleValue();
/*  306: 561 */           if (error <= minError + oneSE)
/*  307:     */           {
/*  308: 563 */             expansion = i;
/*  309: 564 */             break;
/*  310:     */           }
/*  311:     */         }
/*  312:     */       }
/*  313:     */     }
/*  314:     */     else
/*  315:     */     {
/*  316: 573 */       ArrayList<Double>[] modelError = new ArrayList[this.m_numFoldsPruning];
/*  317: 576 */       for (int i = 0; i < this.m_numFoldsPruning; i++)
/*  318:     */       {
/*  319: 577 */         modelError[i] = new ArrayList();
/*  320:     */         
/*  321: 579 */         m_roots[i].m_isLeaf = true;
/*  322: 580 */         Evaluation eval = new Evaluation(test[i]);
/*  323: 581 */         eval.evaluateModel(m_roots[i], test[i], new Object[0]);
/*  324:     */         double error;
/*  325:     */         double error;
/*  326: 583 */         if (this.m_UseErrorRate) {
/*  327: 584 */           error = eval.errorRate();
/*  328:     */         } else {
/*  329: 586 */           error = eval.rootMeanSquaredError();
/*  330:     */         }
/*  331: 588 */         modelError[i].add(new Double(error));
/*  332:     */         
/*  333: 590 */         m_roots[i].m_isLeaf = false;
/*  334: 591 */         BFTree nodeToSplit = (BFTree)((ArrayList)parallelBFElements[i].get(0)).get(0);
/*  335:     */         
/*  336: 593 */         m_roots[i].makeTree(parallelBFElements[i], m_roots[i], train[i], test[i], modelError[i], nodeToSplit.m_SortedIndices, nodeToSplit.m_Weights, nodeToSplit.m_Dists, nodeToSplit.m_ClassProbs, nodeToSplit.m_TotalWeight, nodeToSplit.m_Props, this.m_minNumObj, this.m_Heuristic, this.m_UseGini, this.m_UseErrorRate);
/*  337:     */         
/*  338:     */ 
/*  339:     */ 
/*  340:     */ 
/*  341: 598 */         m_roots[i] = null;
/*  342:     */       }
/*  343: 602 */       double minError = 1.7976931348623157E+308D;
/*  344:     */       
/*  345: 604 */       int maxExpansion = modelError[0].size();
/*  346: 605 */       for (int i = 1; i < modelError.length; i++) {
/*  347: 606 */         if (modelError[i].size() > maxExpansion) {
/*  348: 607 */           maxExpansion = modelError[i].size();
/*  349:     */         }
/*  350:     */       }
/*  351: 611 */       double[] error = new double[maxExpansion];
/*  352: 612 */       int[] counts = new int[maxExpansion];
/*  353: 613 */       for (int i = 0; i < maxExpansion; i++)
/*  354:     */       {
/*  355: 614 */         counts[i] = 0;
/*  356: 615 */         error[i] = 0.0D;
/*  357: 616 */         for (int j = 0; j < this.m_numFoldsPruning; j++) {
/*  358: 617 */           if (i < modelError[j].size())
/*  359:     */           {
/*  360: 618 */             error[i] += ((Double)modelError[j].get(i)).doubleValue();
/*  361: 619 */             counts[i] += 1;
/*  362:     */           }
/*  363:     */         }
/*  364: 622 */         error[i] /= counts[i];
/*  365: 624 */         if (error[i] < minError)
/*  366:     */         {
/*  367: 625 */           minError = error[i];
/*  368: 626 */           expansion = i;
/*  369:     */         }
/*  370:     */       }
/*  371: 631 */       if (this.m_UseOneSE)
/*  372:     */       {
/*  373: 632 */         double oneSE = Math.sqrt(minError * (1.0D - minError) / data.numInstances());
/*  374: 634 */         for (int i = 0; i < maxExpansion; i++) {
/*  375: 635 */           if (error[i] <= minError + oneSE)
/*  376:     */           {
/*  377: 638 */             expansion = i;
/*  378: 639 */             break;
/*  379:     */           }
/*  380:     */         }
/*  381:     */       }
/*  382:     */     }
/*  383: 649 */     int[][] prune_sortedIndices = new int[data.numAttributes()][0];
/*  384: 650 */     double[][] prune_weights = new double[data.numAttributes()][0];
/*  385: 651 */     double[] prune_classProbs = new double[data.numClasses()];
/*  386: 652 */     double prune_totalWeight = computeSortedInfo(data, prune_sortedIndices, prune_weights, prune_classProbs);
/*  387:     */     
/*  388:     */ 
/*  389:     */ 
/*  390:     */ 
/*  391:     */ 
/*  392: 658 */     double[][][] prune_dists = new double[data.numAttributes()][2][data.numClasses()];
/*  393:     */     
/*  394: 660 */     double[][] prune_props = new double[data.numAttributes()][2];
/*  395: 661 */     double[][] prune_totalSubsetWeights = new double[data.numAttributes()][2];
/*  396: 662 */     ArrayList<Object> prune_nodeInfo = computeSplitInfo(this, data, prune_sortedIndices, prune_weights, prune_dists, prune_props, prune_totalSubsetWeights, this.m_Heuristic, this.m_UseGini);
/*  397:     */     
/*  398:     */ 
/*  399:     */ 
/*  400:     */ 
/*  401: 667 */     ArrayList<ArrayList<Object>> BestFirstElements = new ArrayList();
/*  402: 668 */     BestFirstElements.add(prune_nodeInfo);
/*  403:     */     
/*  404: 670 */     int attIndex = ((Attribute)prune_nodeInfo.get(1)).index();
/*  405: 671 */     m_Expansion = 0;
/*  406: 672 */     makeTree(BestFirstElements, data, prune_sortedIndices, prune_weights, prune_dists, prune_classProbs, prune_totalWeight, prune_props[attIndex], this.m_minNumObj, this.m_Heuristic, this.m_UseGini, expansion);
/*  407:     */   }
/*  408:     */   
/*  409:     */   protected void makeTree(ArrayList<ArrayList<Object>> BestFirstElements, Instances data, int[][] sortedIndices, double[][] weights, double[][][] dists, double[] classProbs, double totalWeight, double[] branchProps, int minNumObj, boolean useHeuristic, boolean useGini, int preExpansion)
/*  410:     */     throws Exception
/*  411:     */   {
/*  412: 707 */     if (BestFirstElements.size() == 0) {
/*  413: 708 */       return;
/*  414:     */     }
/*  415: 714 */     ArrayList<Object> firstElement = (ArrayList)BestFirstElements.get(0);
/*  416:     */     
/*  417:     */ 
/*  418: 717 */     Attribute att = (Attribute)firstElement.get(1);
/*  419:     */     
/*  420:     */ 
/*  421: 720 */     double splitValue = (0.0D / 0.0D);
/*  422: 721 */     String splitStr = null;
/*  423: 722 */     if (att.isNumeric()) {
/*  424: 723 */       splitValue = ((Double)firstElement.get(2)).doubleValue();
/*  425:     */     } else {
/*  426: 725 */       splitStr = ((String)firstElement.get(2)).toString();
/*  427:     */     }
/*  428: 729 */     double gain = ((Double)firstElement.get(3)).doubleValue();
/*  429: 732 */     if (this.m_ClassProbs == null)
/*  430:     */     {
/*  431: 733 */       this.m_SortedIndices = new int[sortedIndices.length][0];
/*  432: 734 */       this.m_Weights = new double[weights.length][0];
/*  433: 735 */       this.m_Dists = new double[dists.length][0][0];
/*  434: 736 */       this.m_ClassProbs = new double[classProbs.length];
/*  435: 737 */       this.m_Distribution = new double[classProbs.length];
/*  436: 738 */       this.m_Props = new double[2];
/*  437: 740 */       for (int i = 0; i < this.m_SortedIndices.length; i++)
/*  438:     */       {
/*  439: 741 */         this.m_SortedIndices[i] = sortedIndices[i];
/*  440: 742 */         this.m_Weights[i] = weights[i];
/*  441: 743 */         this.m_Dists[i] = dists[i];
/*  442:     */       }
/*  443: 746 */       System.arraycopy(classProbs, 0, this.m_ClassProbs, 0, classProbs.length);
/*  444: 747 */       System.arraycopy(classProbs, 0, this.m_Distribution, 0, classProbs.length);
/*  445: 748 */       System.arraycopy(branchProps, 0, this.m_Props, 0, this.m_Props.length);
/*  446: 749 */       this.m_TotalWeight = totalWeight;
/*  447: 750 */       if (Utils.sum(this.m_ClassProbs) != 0.0D) {
/*  448: 751 */         Utils.normalize(this.m_ClassProbs);
/*  449:     */       }
/*  450:     */     }
/*  451: 756 */     if ((totalWeight < 2 * minNumObj) || (branchProps[0] == 0.0D) || (branchProps[1] == 0.0D))
/*  452:     */     {
/*  453: 759 */       BestFirstElements.remove(0);
/*  454:     */       
/*  455: 761 */       makeLeaf(data);
/*  456: 762 */       if (BestFirstElements.size() != 0)
/*  457:     */       {
/*  458: 763 */         ArrayList<Object> nextSplitElement = (ArrayList)BestFirstElements.get(0);
/*  459: 764 */         BFTree nextSplitNode = (BFTree)nextSplitElement.get(0);
/*  460: 765 */         nextSplitNode.makeTree(BestFirstElements, data, nextSplitNode.m_SortedIndices, nextSplitNode.m_Weights, nextSplitNode.m_Dists, nextSplitNode.m_ClassProbs, nextSplitNode.m_TotalWeight, nextSplitNode.m_Props, minNumObj, useHeuristic, useGini, preExpansion);
/*  461:     */       }
/*  462: 771 */       return;
/*  463:     */     }
/*  464: 780 */     if ((gain == 0.0D) || (preExpansion == m_Expansion))
/*  465:     */     {
/*  466: 781 */       for (int i = 0; i < BestFirstElements.size(); i++)
/*  467:     */       {
/*  468: 782 */         ArrayList<Object> element = (ArrayList)BestFirstElements.get(i);
/*  469: 783 */         BFTree node = (BFTree)element.get(0);
/*  470: 784 */         node.makeLeaf(data);
/*  471:     */       }
/*  472: 786 */       BestFirstElements.clear();
/*  473:     */     }
/*  474:     */     else
/*  475:     */     {
/*  476: 792 */       BestFirstElements.remove(0);
/*  477:     */       
/*  478: 794 */       this.m_Attribute = att;
/*  479: 795 */       if (this.m_Attribute.isNumeric()) {
/*  480: 796 */         this.m_SplitValue = splitValue;
/*  481:     */       } else {
/*  482: 798 */         this.m_SplitString = splitStr;
/*  483:     */       }
/*  484: 801 */       int[][][] subsetIndices = new int[2][data.numAttributes()][0];
/*  485: 802 */       double[][][] subsetWeights = new double[2][data.numAttributes()][0];
/*  486:     */       
/*  487: 804 */       splitData(subsetIndices, subsetWeights, this.m_Attribute, this.m_SplitValue, this.m_SplitString, sortedIndices, weights, data);
/*  488:     */       
/*  489:     */ 
/*  490:     */ 
/*  491:     */ 
/*  492:     */ 
/*  493: 810 */       int attIndex = att.index();
/*  494: 811 */       if ((subsetIndices[0][attIndex].length < minNumObj) || (subsetIndices[1][attIndex].length < minNumObj))
/*  495:     */       {
/*  496: 813 */         makeLeaf(data);
/*  497:     */       }
/*  498:     */       else
/*  499:     */       {
/*  500: 818 */         this.m_isLeaf = false;
/*  501: 819 */         this.m_Attribute = att;
/*  502: 822 */         if ((this.m_PruningStrategy == 2) || (this.m_PruningStrategy == 1) || (preExpansion != -1)) {
/*  503: 824 */           m_Expansion += 1;
/*  504:     */         }
/*  505: 827 */         makeSuccessors(BestFirstElements, data, subsetIndices, subsetWeights, dists, att, useHeuristic, useGini);
/*  506:     */       }
/*  507: 832 */       if (BestFirstElements.size() != 0)
/*  508:     */       {
/*  509: 833 */         ArrayList<Object> nextSplitElement = (ArrayList)BestFirstElements.get(0);
/*  510: 834 */         BFTree nextSplitNode = (BFTree)nextSplitElement.get(0);
/*  511: 835 */         nextSplitNode.makeTree(BestFirstElements, data, nextSplitNode.m_SortedIndices, nextSplitNode.m_Weights, nextSplitNode.m_Dists, nextSplitNode.m_ClassProbs, nextSplitNode.m_TotalWeight, nextSplitNode.m_Props, minNumObj, useHeuristic, useGini, preExpansion);
/*  512:     */       }
/*  513:     */     }
/*  514:     */   }
/*  515:     */   
/*  516:     */   protected boolean makeTree(ArrayList<ArrayList<Object>> BestFirstElements, BFTree root, Instances train, int[][] sortedIndices, double[][] weights, double[][][] dists, double[] classProbs, double totalWeight, double[] branchProps, int minNumObj, boolean useHeuristic, boolean useGini)
/*  517:     */     throws Exception
/*  518:     */   {
/*  519: 876 */     if (BestFirstElements.size() == 0) {
/*  520: 877 */       return false;
/*  521:     */     }
/*  522: 883 */     ArrayList<Object> firstElement = (ArrayList)BestFirstElements.get(0);
/*  523:     */     
/*  524:     */ 
/*  525: 886 */     BFTree nodeToSplit = (BFTree)firstElement.get(0);
/*  526:     */     
/*  527:     */ 
/*  528: 889 */     Attribute att = (Attribute)firstElement.get(1);
/*  529:     */     
/*  530:     */ 
/*  531: 892 */     double splitValue = (0.0D / 0.0D);
/*  532: 893 */     String splitStr = null;
/*  533: 894 */     if (att.isNumeric()) {
/*  534: 895 */       splitValue = ((Double)firstElement.get(2)).doubleValue();
/*  535:     */     } else {
/*  536: 897 */       splitStr = ((String)firstElement.get(2)).toString();
/*  537:     */     }
/*  538: 901 */     double gain = ((Double)firstElement.get(3)).doubleValue();
/*  539: 906 */     if ((totalWeight < 2 * minNumObj) || (branchProps[0] == 0.0D) || (branchProps[1] == 0.0D))
/*  540:     */     {
/*  541: 909 */       BestFirstElements.remove(0);
/*  542: 910 */       nodeToSplit.makeLeaf(train);
/*  543: 911 */       if (BestFirstElements.size() == 0) {
/*  544: 912 */         return false;
/*  545:     */       }
/*  546: 914 */       BFTree nextNode = (BFTree)((ArrayList)BestFirstElements.get(0)).get(0);
/*  547: 915 */       return root.makeTree(BestFirstElements, root, train, nextNode.m_SortedIndices, nextNode.m_Weights, nextNode.m_Dists, nextNode.m_ClassProbs, nextNode.m_TotalWeight, nextNode.m_Props, minNumObj, useHeuristic, useGini);
/*  548:     */     }
/*  549: 927 */     if (gain == 0.0D)
/*  550:     */     {
/*  551: 928 */       for (int i = 0; i < BestFirstElements.size(); i++)
/*  552:     */       {
/*  553: 929 */         ArrayList<Object> element = (ArrayList)BestFirstElements.get(i);
/*  554: 930 */         BFTree node = (BFTree)element.get(0);
/*  555: 931 */         node.makeLeaf(train);
/*  556:     */       }
/*  557: 933 */       BestFirstElements.clear();
/*  558: 934 */       return false;
/*  559:     */     }
/*  560: 939 */     BestFirstElements.remove(0);
/*  561: 940 */     nodeToSplit.m_Attribute = att;
/*  562: 941 */     if (att.isNumeric()) {
/*  563: 942 */       nodeToSplit.m_SplitValue = splitValue;
/*  564:     */     } else {
/*  565: 944 */       nodeToSplit.m_SplitString = splitStr;
/*  566:     */     }
/*  567: 947 */     int[][][] subsetIndices = new int[2][train.numAttributes()][0];
/*  568: 948 */     double[][][] subsetWeights = new double[2][train.numAttributes()][0];
/*  569:     */     
/*  570: 950 */     splitData(subsetIndices, subsetWeights, nodeToSplit.m_Attribute, nodeToSplit.m_SplitValue, nodeToSplit.m_SplitString, nodeToSplit.m_SortedIndices, nodeToSplit.m_Weights, train);
/*  571:     */     
/*  572:     */ 
/*  573:     */ 
/*  574:     */ 
/*  575:     */ 
/*  576:     */ 
/*  577: 957 */     int attIndex = att.index();
/*  578: 958 */     if ((subsetIndices[0][attIndex].length < minNumObj) || (subsetIndices[1][attIndex].length < minNumObj))
/*  579:     */     {
/*  580: 961 */       nodeToSplit.makeLeaf(train);
/*  581: 962 */       BFTree nextNode = (BFTree)((ArrayList)BestFirstElements.get(0)).get(0);
/*  582: 963 */       return root.makeTree(BestFirstElements, root, train, nextNode.m_SortedIndices, nextNode.m_Weights, nextNode.m_Dists, nextNode.m_ClassProbs, nextNode.m_TotalWeight, nextNode.m_Props, minNumObj, useHeuristic, useGini);
/*  583:     */     }
/*  584: 971 */     nodeToSplit.m_isLeaf = false;
/*  585: 972 */     nodeToSplit.m_Attribute = att;
/*  586:     */     
/*  587: 974 */     nodeToSplit.makeSuccessors(BestFirstElements, train, subsetIndices, subsetWeights, dists, nodeToSplit.m_Attribute, useHeuristic, useGini);
/*  588: 977 */     for (int i = 0; i < 2; i++) {
/*  589: 978 */       nodeToSplit.m_Successors[i].makeLeaf(train);
/*  590:     */     }
/*  591: 981 */     return true;
/*  592:     */   }
/*  593:     */   
/*  594:     */   protected void makeTree(ArrayList<ArrayList<Object>> BestFirstElements, BFTree root, Instances train, Instances test, ArrayList<Double> modelError, int[][] sortedIndices, double[][] weights, double[][][] dists, double[] classProbs, double totalWeight, double[] branchProps, int minNumObj, boolean useHeuristic, boolean useGini, boolean useErrorRate)
/*  595:     */     throws Exception
/*  596:     */   {
/*  597:1018 */     if (BestFirstElements.size() == 0) {
/*  598:1019 */       return;
/*  599:     */     }
/*  600:1025 */     ArrayList<Object> firstElement = (ArrayList)BestFirstElements.get(0);
/*  601:     */     
/*  602:     */ 
/*  603:     */ 
/*  604:     */ 
/*  605:     */ 
/*  606:1031 */     Attribute att = (Attribute)firstElement.get(1);
/*  607:     */     
/*  608:     */ 
/*  609:1034 */     double splitValue = (0.0D / 0.0D);
/*  610:1035 */     String splitStr = null;
/*  611:1036 */     if (att.isNumeric()) {
/*  612:1037 */       splitValue = ((Double)firstElement.get(2)).doubleValue();
/*  613:     */     } else {
/*  614:1039 */       splitStr = ((String)firstElement.get(2)).toString();
/*  615:     */     }
/*  616:1043 */     double gain = ((Double)firstElement.get(3)).doubleValue();
/*  617:1046 */     if ((totalWeight < 2 * minNumObj) || (branchProps[0] == 0.0D) || (branchProps[1] == 0.0D))
/*  618:     */     {
/*  619:1049 */       BestFirstElements.remove(0);
/*  620:1050 */       makeLeaf(train);
/*  621:1051 */       if (BestFirstElements.size() == 0) {
/*  622:1052 */         return;
/*  623:     */       }
/*  624:1055 */       BFTree nextSplitNode = (BFTree)((ArrayList)BestFirstElements.get(0)).get(0);
/*  625:1056 */       nextSplitNode.makeTree(BestFirstElements, root, train, test, modelError, nextSplitNode.m_SortedIndices, nextSplitNode.m_Weights, nextSplitNode.m_Dists, nextSplitNode.m_ClassProbs, nextSplitNode.m_TotalWeight, nextSplitNode.m_Props, minNumObj, useHeuristic, useGini, useErrorRate);
/*  626:     */       
/*  627:     */ 
/*  628:     */ 
/*  629:     */ 
/*  630:1061 */       return;
/*  631:     */     }
/*  632:1071 */     if (gain == 0.0D)
/*  633:     */     {
/*  634:1072 */       for (int i = 0; i < BestFirstElements.size(); i++)
/*  635:     */       {
/*  636:1073 */         ArrayList<Object> element = (ArrayList)BestFirstElements.get(i);
/*  637:1074 */         BFTree node = (BFTree)element.get(0);
/*  638:1075 */         node.makeLeaf(train);
/*  639:     */       }
/*  640:1077 */       BestFirstElements.clear();
/*  641:     */     }
/*  642:     */     else
/*  643:     */     {
/*  644:1083 */       BestFirstElements.remove(0);
/*  645:1084 */       this.m_Attribute = att;
/*  646:1085 */       if (att.isNumeric()) {
/*  647:1086 */         this.m_SplitValue = splitValue;
/*  648:     */       } else {
/*  649:1088 */         this.m_SplitString = splitStr;
/*  650:     */       }
/*  651:1091 */       int[][][] subsetIndices = new int[2][train.numAttributes()][0];
/*  652:1092 */       double[][][] subsetWeights = new double[2][train.numAttributes()][0];
/*  653:     */       
/*  654:1094 */       splitData(subsetIndices, subsetWeights, this.m_Attribute, this.m_SplitValue, this.m_SplitString, sortedIndices, weights, train);
/*  655:     */       
/*  656:     */ 
/*  657:     */ 
/*  658:     */ 
/*  659:     */ 
/*  660:1100 */       int attIndex = att.index();
/*  661:1101 */       if ((subsetIndices[0][attIndex].length < minNumObj) || (subsetIndices[1][attIndex].length < minNumObj))
/*  662:     */       {
/*  663:1103 */         makeLeaf(train);
/*  664:     */       }
/*  665:     */       else
/*  666:     */       {
/*  667:1108 */         this.m_isLeaf = false;
/*  668:1109 */         this.m_Attribute = att;
/*  669:     */         
/*  670:1111 */         makeSuccessors(BestFirstElements, train, subsetIndices, subsetWeights, dists, this.m_Attribute, useHeuristic, useGini);
/*  671:1113 */         for (int i = 0; i < 2; i++) {
/*  672:1114 */           this.m_Successors[i].makeLeaf(train);
/*  673:     */         }
/*  674:1117 */         Evaluation eval = new Evaluation(test);
/*  675:1118 */         eval.evaluateModel(root, test, new Object[0]);
/*  676:     */         double error;
/*  677:     */         double error;
/*  678:1120 */         if (useErrorRate) {
/*  679:1121 */           error = eval.errorRate();
/*  680:     */         } else {
/*  681:1123 */           error = eval.rootMeanSquaredError();
/*  682:     */         }
/*  683:1125 */         modelError.add(new Double(error));
/*  684:     */       }
/*  685:1128 */       if (BestFirstElements.size() != 0)
/*  686:     */       {
/*  687:1129 */         ArrayList<Object> nextSplitElement = (ArrayList)BestFirstElements.get(0);
/*  688:1130 */         BFTree nextSplitNode = (BFTree)nextSplitElement.get(0);
/*  689:1131 */         nextSplitNode.makeTree(BestFirstElements, root, train, test, modelError, nextSplitNode.m_SortedIndices, nextSplitNode.m_Weights, nextSplitNode.m_Dists, nextSplitNode.m_ClassProbs, nextSplitNode.m_TotalWeight, nextSplitNode.m_Props, minNumObj, useHeuristic, useGini, useErrorRate);
/*  690:     */       }
/*  691:     */     }
/*  692:     */   }
/*  693:     */   
/*  694:     */   protected void makeSuccessors(ArrayList<ArrayList<Object>> BestFirstElements, Instances data, int[][][] subsetSortedIndices, double[][][] subsetWeights, double[][][] dists, Attribute att, boolean useHeuristic, boolean useGini)
/*  695:     */     throws Exception
/*  696:     */   {
/*  697:1160 */     this.m_Successors = new BFTree[2];
/*  698:1162 */     for (int i = 0; i < 2; i++)
/*  699:     */     {
/*  700:1163 */       this.m_Successors[i] = new BFTree();
/*  701:1164 */       this.m_Successors[i].m_isLeaf = true;
/*  702:     */       
/*  703:     */ 
/*  704:1167 */       this.m_Successors[i].m_ClassProbs = new double[data.numClasses()];
/*  705:1168 */       this.m_Successors[i].m_Distribution = new double[data.numClasses()];
/*  706:1169 */       System.arraycopy(dists[att.index()][i], 0, this.m_Successors[i].m_ClassProbs, 0, this.m_Successors[i].m_ClassProbs.length);
/*  707:     */       
/*  708:1171 */       System.arraycopy(dists[att.index()][i], 0, this.m_Successors[i].m_Distribution, 0, this.m_Successors[i].m_Distribution.length);
/*  709:1174 */       if (Utils.sum(this.m_Successors[i].m_ClassProbs) != 0.0D) {
/*  710:1175 */         Utils.normalize(this.m_Successors[i].m_ClassProbs);
/*  711:     */       }
/*  712:1179 */       double[][] props = new double[data.numAttributes()][2];
/*  713:1180 */       double[][][] subDists = new double[data.numAttributes()][2][data.numClasses()];
/*  714:     */       
/*  715:1182 */       double[][] totalSubsetWeights = new double[data.numAttributes()][2];
/*  716:1183 */       ArrayList<Object> splitInfo = this.m_Successors[i].computeSplitInfo(this.m_Successors[i], data, subsetSortedIndices[i], subsetWeights[i], subDists, props, totalSubsetWeights, useHeuristic, useGini);
/*  717:     */       
/*  718:     */ 
/*  719:     */ 
/*  720:     */ 
/*  721:1188 */       int splitIndex = ((Attribute)splitInfo.get(1)).index();
/*  722:1189 */       this.m_Successors[i].m_Props = new double[2];
/*  723:1190 */       System.arraycopy(props[splitIndex], 0, this.m_Successors[i].m_Props, 0, this.m_Successors[i].m_Props.length);
/*  724:     */       
/*  725:     */ 
/*  726:     */ 
/*  727:1194 */       this.m_Successors[i].m_SortedIndices = new int[data.numAttributes()][0];
/*  728:1195 */       this.m_Successors[i].m_Weights = new double[data.numAttributes()][0];
/*  729:1196 */       for (int j = 0; j < this.m_Successors[i].m_SortedIndices.length; j++)
/*  730:     */       {
/*  731:1197 */         this.m_Successors[i].m_SortedIndices[j] = subsetSortedIndices[i][j];
/*  732:1198 */         this.m_Successors[i].m_Weights[j] = subsetWeights[i][j];
/*  733:     */       }
/*  734:1202 */       this.m_Successors[i].m_Dists = new double[data.numAttributes()][2][data.numClasses()];
/*  735:1204 */       for (int j = 0; j < subDists.length; j++) {
/*  736:1205 */         this.m_Successors[i].m_Dists[j] = subDists[j];
/*  737:     */       }
/*  738:1209 */       this.m_Successors[i].m_TotalWeight = Utils.sum(totalSubsetWeights[splitIndex]);
/*  739:1214 */       if (BestFirstElements.size() == 0)
/*  740:     */       {
/*  741:1215 */         BestFirstElements.add(splitInfo);
/*  742:     */       }
/*  743:     */       else
/*  744:     */       {
/*  745:1217 */         double gGain = ((Double)splitInfo.get(3)).doubleValue();
/*  746:1218 */         int vectorSize = BestFirstElements.size();
/*  747:1219 */         ArrayList<Object> lastNode = (ArrayList)BestFirstElements.get(vectorSize - 1);
/*  748:1222 */         if (gGain < ((Double)lastNode.get(3)).doubleValue()) {
/*  749:1223 */           BestFirstElements.add(vectorSize, splitInfo);
/*  750:     */         } else {
/*  751:1225 */           for (int j = 0; j < vectorSize; j++)
/*  752:     */           {
/*  753:1226 */             ArrayList<Object> node = (ArrayList)BestFirstElements.get(j);
/*  754:1227 */             double nodeGain = ((Double)node.get(3)).doubleValue();
/*  755:1228 */             if (gGain >= nodeGain)
/*  756:     */             {
/*  757:1229 */               BestFirstElements.add(j, splitInfo);
/*  758:1230 */               break;
/*  759:     */             }
/*  760:     */           }
/*  761:     */         }
/*  762:     */       }
/*  763:     */     }
/*  764:     */   }
/*  765:     */   
/*  766:     */   protected double computeSortedInfo(Instances data, int[][] sortedIndices, double[][] weights, double[] classProbs)
/*  767:     */     throws Exception
/*  768:     */   {
/*  769:1253 */     double[] vals = new double[data.numInstances()];
/*  770:1254 */     for (int j = 0; j < data.numAttributes(); j++) {
/*  771:1255 */       if (j != data.classIndex())
/*  772:     */       {
/*  773:1258 */         weights[j] = new double[data.numInstances()];
/*  774:1260 */         if (data.attribute(j).isNominal())
/*  775:     */         {
/*  776:1264 */           sortedIndices[j] = new int[data.numInstances()];
/*  777:1265 */           int count = 0;
/*  778:1266 */           for (int i = 0; i < data.numInstances(); i++)
/*  779:     */           {
/*  780:1267 */             Instance inst = data.instance(i);
/*  781:1268 */             if (!inst.isMissing(j))
/*  782:     */             {
/*  783:1269 */               sortedIndices[j][count] = i;
/*  784:1270 */               weights[j][count] = inst.weight();
/*  785:1271 */               count++;
/*  786:     */             }
/*  787:     */           }
/*  788:1274 */           for (int i = 0; i < data.numInstances(); i++)
/*  789:     */           {
/*  790:1275 */             Instance inst = data.instance(i);
/*  791:1276 */             if (inst.isMissing(j))
/*  792:     */             {
/*  793:1277 */               sortedIndices[j][count] = i;
/*  794:1278 */               weights[j][count] = inst.weight();
/*  795:1279 */               count++;
/*  796:     */             }
/*  797:     */           }
/*  798:     */         }
/*  799:     */         else
/*  800:     */         {
/*  801:1286 */           for (int i = 0; i < data.numInstances(); i++)
/*  802:     */           {
/*  803:1287 */             Instance inst = data.instance(i);
/*  804:1288 */             vals[i] = inst.value(j);
/*  805:     */           }
/*  806:1290 */           sortedIndices[j] = Utils.sort(vals);
/*  807:1291 */           for (int i = 0; i < data.numInstances(); i++) {
/*  808:1292 */             weights[j][i] = data.instance(sortedIndices[j][i]).weight();
/*  809:     */           }
/*  810:     */         }
/*  811:     */       }
/*  812:     */     }
/*  813:1298 */     double totalWeight = 0.0D;
/*  814:1299 */     for (int i = 0; i < data.numInstances(); i++)
/*  815:     */     {
/*  816:1300 */       Instance inst = data.instance(i);
/*  817:1301 */       classProbs[((int)inst.classValue())] += inst.weight();
/*  818:1302 */       totalWeight += inst.weight();
/*  819:     */     }
/*  820:1305 */     return totalWeight;
/*  821:     */   }
/*  822:     */   
/*  823:     */   protected ArrayList<Object> computeSplitInfo(BFTree node, Instances data, int[][] sortedIndices, double[][] weights, double[][][] dists, double[][] props, double[][] totalSubsetWeights, boolean useHeuristic, boolean useGini)
/*  824:     */     throws Exception
/*  825:     */   {
/*  826:1330 */     double[] splits = new double[data.numAttributes()];
/*  827:1331 */     String[] splitString = new String[data.numAttributes()];
/*  828:1332 */     double[] gains = new double[data.numAttributes()];
/*  829:1334 */     for (int i = 0; i < data.numAttributes(); i++) {
/*  830:1335 */       if (i != data.classIndex())
/*  831:     */       {
/*  832:1338 */         Attribute att = data.attribute(i);
/*  833:1339 */         if (att.isNumeric()) {
/*  834:1341 */           splits[i] = numericDistribution(props, dists, att, sortedIndices[i], weights[i], totalSubsetWeights, gains, data, useGini);
/*  835:     */         } else {
/*  836:1345 */           splitString[i] = nominalDistribution(props, dists, att, sortedIndices[i], weights[i], totalSubsetWeights, gains, data, useHeuristic, useGini);
/*  837:     */         }
/*  838:     */       }
/*  839:     */     }
/*  840:1351 */     int index = Utils.maxIndex(gains);
/*  841:1352 */     double mBestGain = gains[index];
/*  842:     */     
/*  843:1354 */     Attribute att = data.attribute(index);
/*  844:1355 */     double mValue = (0.0D / 0.0D);
/*  845:1356 */     String mString = null;
/*  846:1357 */     if (att.isNumeric())
/*  847:     */     {
/*  848:1358 */       mValue = splits[index];
/*  849:     */     }
/*  850:     */     else
/*  851:     */     {
/*  852:1360 */       mString = splitString[index];
/*  853:1361 */       if (mString == null) {
/*  854:1362 */         mString = "";
/*  855:     */       }
/*  856:     */     }
/*  857:1367 */     ArrayList<Object> splitInfo = new ArrayList();
/*  858:1368 */     splitInfo.add(node);
/*  859:1369 */     splitInfo.add(att);
/*  860:1370 */     if (att.isNumeric()) {
/*  861:1371 */       splitInfo.add(new Double(mValue));
/*  862:     */     } else {
/*  863:1373 */       splitInfo.add(mString);
/*  864:     */     }
/*  865:1375 */     splitInfo.add(new Double(mBestGain));
/*  866:     */     
/*  867:1377 */     return splitInfo;
/*  868:     */   }
/*  869:     */   
/*  870:     */   protected double numericDistribution(double[][] props, double[][][] dists, Attribute att, int[] sortedIndices, double[] weights, double[][] subsetWeights, double[] gains, Instances data, boolean useGini)
/*  871:     */     throws Exception
/*  872:     */   {
/*  873:1402 */     double splitPoint = (0.0D / 0.0D);
/*  874:1403 */     double[][] dist = (double[][])null;
/*  875:1404 */     int numClasses = data.numClasses();
/*  876:     */     
/*  877:     */ 
/*  878:1407 */     double[][] currDist = new double[2][numClasses];
/*  879:1408 */     dist = new double[2][numClasses];
/*  880:     */     
/*  881:     */ 
/*  882:1411 */     double[] parentDist = new double[numClasses];
/*  883:1412 */     int missingStart = 0;
/*  884:1413 */     for (int j = 0; j < sortedIndices.length; j++)
/*  885:     */     {
/*  886:1414 */       Instance inst = data.instance(sortedIndices[j]);
/*  887:1415 */       if (!inst.isMissing(att))
/*  888:     */       {
/*  889:1416 */         missingStart++;
/*  890:1417 */         currDist[1][((int)inst.classValue())] += weights[j];
/*  891:     */       }
/*  892:1419 */       parentDist[((int)inst.classValue())] += weights[j];
/*  893:     */     }
/*  894:1421 */     System.arraycopy(currDist[1], 0, dist[1], 0, dist[1].length);
/*  895:     */     
/*  896:     */ 
/*  897:1424 */     double currSplit = data.instance(sortedIndices[0]).value(att);
/*  898:     */     
/*  899:1426 */     double bestGain = -1.797693134862316E+308D;
/*  900:1428 */     for (int i = 0; i < sortedIndices.length; i++)
/*  901:     */     {
/*  902:1429 */       Instance inst = data.instance(sortedIndices[i]);
/*  903:1430 */       if (inst.isMissing(att)) {
/*  904:     */         break;
/*  905:     */       }
/*  906:1433 */       if (inst.value(att) > currSplit)
/*  907:     */       {
/*  908:1435 */         double[][] tempDist = new double[2][numClasses];
/*  909:1436 */         for (int k = 0; k < 2; k++) {
/*  910:1438 */           System.arraycopy(currDist[k], 0, tempDist[k], 0, tempDist[k].length);
/*  911:     */         }
/*  912:1441 */         double[] tempProps = new double[2];
/*  913:1442 */         for (int k = 0; k < 2; k++) {
/*  914:1443 */           tempProps[k] = Utils.sum(tempDist[k]);
/*  915:     */         }
/*  916:1446 */         if (Utils.sum(tempProps) != 0.0D) {
/*  917:1447 */           Utils.normalize(tempProps);
/*  918:     */         }
/*  919:1451 */         int index = missingStart;
/*  920:1452 */         while (index < sortedIndices.length)
/*  921:     */         {
/*  922:1453 */           Instance insta = data.instance(sortedIndices[index]);
/*  923:1454 */           for (int j = 0; j < 2; j++) {
/*  924:1455 */             tempDist[j][((int)insta.classValue())] += tempProps[j] * weights[index];
/*  925:     */           }
/*  926:1458 */           index++;
/*  927:     */         }
/*  928:     */         double currGain;
/*  929:     */         double currGain;
/*  930:1461 */         if (useGini) {
/*  931:1462 */           currGain = computeGiniGain(parentDist, tempDist);
/*  932:     */         } else {
/*  933:1464 */           currGain = computeInfoGain(parentDist, tempDist);
/*  934:     */         }
/*  935:1467 */         if (currGain > bestGain)
/*  936:     */         {
/*  937:1468 */           bestGain = currGain;
/*  938:     */           
/*  939:1470 */           splitPoint = Math.rint((inst.value(att) + currSplit) / 2.0D * 100000.0D) / 100000.0D;
/*  940:1471 */           for (int j = 0; j < currDist.length; j++) {
/*  941:1472 */             System.arraycopy(tempDist[j], 0, dist[j], 0, dist[j].length);
/*  942:     */           }
/*  943:     */         }
/*  944:     */       }
/*  945:1476 */       currSplit = inst.value(att);
/*  946:1477 */       currDist[0][((int)inst.classValue())] += weights[i];
/*  947:1478 */       currDist[1][((int)inst.classValue())] -= weights[i];
/*  948:     */     }
/*  949:1482 */     int attIndex = att.index();
/*  950:1483 */     props[attIndex] = new double[2];
/*  951:1484 */     for (int k = 0; k < 2; k++) {
/*  952:1485 */       props[attIndex][k] = Utils.sum(dist[k]);
/*  953:     */     }
/*  954:1487 */     if (Utils.sum(props[attIndex]) != 0.0D) {
/*  955:1488 */       Utils.normalize(props[attIndex]);
/*  956:     */     }
/*  957:1492 */     subsetWeights[attIndex] = new double[2];
/*  958:1493 */     for (int j = 0; j < 2; j++) {
/*  959:1494 */       subsetWeights[attIndex][j] += Utils.sum(dist[j]);
/*  960:     */     }
/*  961:1498 */     gains[attIndex] = (Math.rint(bestGain * 10000000.0D) / 10000000.0D);
/*  962:1499 */     dists[attIndex] = dist;
/*  963:1500 */     return splitPoint;
/*  964:     */   }
/*  965:     */   
/*  966:     */   protected String nominalDistribution(double[][] props, double[][][] dists, Attribute att, int[] sortedIndices, double[] weights, double[][] subsetWeights, double[] gains, Instances data, boolean useHeuristic, boolean useGini)
/*  967:     */     throws Exception
/*  968:     */   {
/*  969:1526 */     String[] values = new String[att.numValues()];
/*  970:1527 */     int numCat = values.length;
/*  971:1528 */     int numClasses = data.numClasses();
/*  972:     */     
/*  973:1530 */     String bestSplitString = "";
/*  974:1531 */     double bestGain = -1.797693134862316E+308D;
/*  975:     */     
/*  976:     */ 
/*  977:1534 */     int[] classFreq = new int[numCat];
/*  978:1535 */     for (int j = 0; j < numCat; j++) {
/*  979:1536 */       classFreq[j] = 0;
/*  980:     */     }
/*  981:1539 */     double[] parentDist = new double[numClasses];
/*  982:1540 */     double[][] currDist = new double[2][numClasses];
/*  983:1541 */     double[][] dist = new double[2][numClasses];
/*  984:1542 */     int missingStart = 0;
/*  985:1544 */     for (int i = 0; i < sortedIndices.length; i++)
/*  986:     */     {
/*  987:1545 */       Instance inst = data.instance(sortedIndices[i]);
/*  988:1546 */       if (!inst.isMissing(att))
/*  989:     */       {
/*  990:1547 */         missingStart++;
/*  991:1548 */         classFreq[((int)inst.value(att))] += 1;
/*  992:     */       }
/*  993:1550 */       parentDist[((int)inst.classValue())] += weights[i];
/*  994:     */     }
/*  995:1554 */     int nonEmpty = 0;
/*  996:1555 */     for (int j = 0; j < numCat; j++) {
/*  997:1556 */       if (classFreq[j] != 0) {
/*  998:1557 */         nonEmpty++;
/*  999:     */       }
/* 1000:     */     }
/* 1001:1562 */     String[] nonEmptyValues = new String[nonEmpty];
/* 1002:1563 */     int nonEmptyIndex = 0;
/* 1003:1564 */     for (int j = 0; j < numCat; j++) {
/* 1004:1565 */       if (classFreq[j] != 0)
/* 1005:     */       {
/* 1006:1566 */         nonEmptyValues[nonEmptyIndex] = att.value(j);
/* 1007:1567 */         nonEmptyIndex++;
/* 1008:     */       }
/* 1009:     */     }
/* 1010:1572 */     int empty = numCat - nonEmpty;
/* 1011:1573 */     String[] emptyValues = new String[empty];
/* 1012:1574 */     int emptyIndex = 0;
/* 1013:1575 */     for (int j = 0; j < numCat; j++) {
/* 1014:1576 */       if (classFreq[j] == 0)
/* 1015:     */       {
/* 1016:1577 */         emptyValues[emptyIndex] = att.value(j);
/* 1017:1578 */         emptyIndex++;
/* 1018:     */       }
/* 1019:     */     }
/* 1020:1582 */     if (nonEmpty <= 1)
/* 1021:     */     {
/* 1022:1583 */       gains[att.index()] = 0.0D;
/* 1023:1584 */       return "";
/* 1024:     */     }
/* 1025:1588 */     if (data.numClasses() == 2)
/* 1026:     */     {
/* 1027:1593 */       double[] pClass0 = new double[nonEmpty];
/* 1028:     */       
/* 1029:1595 */       double[][] valDist = new double[nonEmpty][2];
/* 1030:1597 */       for (int j = 0; j < nonEmpty; j++) {
/* 1031:1598 */         for (int k = 0; k < 2; k++) {
/* 1032:1599 */           valDist[j][k] = 0.0D;
/* 1033:     */         }
/* 1034:     */       }
/* 1035:1603 */       for (int sortedIndice : sortedIndices)
/* 1036:     */       {
/* 1037:1604 */         Instance inst = data.instance(sortedIndice);
/* 1038:1605 */         if (inst.isMissing(att)) {
/* 1039:     */           break;
/* 1040:     */         }
/* 1041:1609 */         for (int j = 0; j < nonEmpty; j++) {
/* 1042:1610 */           if (att.value((int)inst.value(att)).compareTo(nonEmptyValues[j]) == 0)
/* 1043:     */           {
/* 1044:1611 */             valDist[j][((int)inst.classValue())] += inst.weight();
/* 1045:1612 */             break;
/* 1046:     */           }
/* 1047:     */         }
/* 1048:     */       }
/* 1049:1617 */       for (int j = 0; j < nonEmpty; j++)
/* 1050:     */       {
/* 1051:1618 */         double distSum = Utils.sum(valDist[j]);
/* 1052:1619 */         if (distSum == 0.0D) {
/* 1053:1620 */           pClass0[j] = 0.0D;
/* 1054:     */         } else {
/* 1055:1622 */           pClass0[j] = (valDist[j][0] / distSum);
/* 1056:     */         }
/* 1057:     */       }
/* 1058:1627 */       String[] sortedValues = new String[nonEmpty];
/* 1059:1628 */       for (int j = 0; j < nonEmpty; j++)
/* 1060:     */       {
/* 1061:1629 */         sortedValues[j] = nonEmptyValues[Utils.minIndex(pClass0)];
/* 1062:1630 */         pClass0[Utils.minIndex(pClass0)] = 1.7976931348623157E+308D;
/* 1063:     */       }
/* 1064:1636 */       String tempStr = "";
/* 1065:1638 */       for (int j = 0; j < nonEmpty - 1; j++)
/* 1066:     */       {
/* 1067:1639 */         currDist = new double[2][numClasses];
/* 1068:1640 */         if (tempStr == "") {
/* 1069:1641 */           tempStr = "(" + sortedValues[j] + ")";
/* 1070:     */         } else {
/* 1071:1643 */           tempStr = tempStr + "|(" + sortedValues[j] + ")";
/* 1072:     */         }
/* 1073:1646 */         for (int i = 0; i < sortedIndices.length; i++)
/* 1074:     */         {
/* 1075:1647 */           Instance inst = data.instance(sortedIndices[i]);
/* 1076:1648 */           if (inst.isMissing(att)) {
/* 1077:     */             break;
/* 1078:     */           }
/* 1079:1652 */           if (tempStr.indexOf("(" + att.value((int)inst.value(att)) + ")") != -1) {
/* 1080:1653 */             currDist[0][((int)inst.classValue())] += weights[i];
/* 1081:     */           } else {
/* 1082:1655 */             currDist[1][((int)inst.classValue())] += weights[i];
/* 1083:     */           }
/* 1084:     */         }
/* 1085:1659 */         double[][] tempDist = new double[2][numClasses];
/* 1086:1660 */         for (int kk = 0; kk < 2; kk++) {
/* 1087:1661 */           tempDist[kk] = currDist[kk];
/* 1088:     */         }
/* 1089:1664 */         double[] tempProps = new double[2];
/* 1090:1665 */         for (int kk = 0; kk < 2; kk++) {
/* 1091:1666 */           tempProps[kk] = Utils.sum(tempDist[kk]);
/* 1092:     */         }
/* 1093:1669 */         if (Utils.sum(tempProps) != 0.0D) {
/* 1094:1670 */           Utils.normalize(tempProps);
/* 1095:     */         }
/* 1096:1674 */         int mstart = missingStart;
/* 1097:1675 */         while (mstart < sortedIndices.length)
/* 1098:     */         {
/* 1099:1676 */           Instance insta = data.instance(sortedIndices[mstart]);
/* 1100:1677 */           for (int jj = 0; jj < 2; jj++) {
/* 1101:1678 */             tempDist[jj][((int)insta.classValue())] += tempProps[jj] * weights[mstart];
/* 1102:     */           }
/* 1103:1681 */           mstart++;
/* 1104:     */         }
/* 1105:     */         double currGain;
/* 1106:     */         double currGain;
/* 1107:1685 */         if (useGini) {
/* 1108:1686 */           currGain = computeGiniGain(parentDist, tempDist);
/* 1109:     */         } else {
/* 1110:1688 */           currGain = computeInfoGain(parentDist, tempDist);
/* 1111:     */         }
/* 1112:1691 */         if (currGain > bestGain)
/* 1113:     */         {
/* 1114:1692 */           bestGain = currGain;
/* 1115:1693 */           bestSplitString = tempStr;
/* 1116:1694 */           for (int jj = 0; jj < 2; jj++) {
/* 1117:1695 */             System.arraycopy(tempDist[jj], 0, dist[jj], 0, dist[jj].length);
/* 1118:     */           }
/* 1119:     */         }
/* 1120:     */       }
/* 1121:     */     }
/* 1122:1702 */     else if ((!useHeuristic) || (nonEmpty <= 4))
/* 1123:     */     {
/* 1124:1706 */       for (int i = 0; i < (int)Math.pow(2.0D, nonEmpty - 1); i++)
/* 1125:     */       {
/* 1126:1707 */         String tempStr = "";
/* 1127:1708 */         currDist = new double[2][numClasses];
/* 1128:     */         
/* 1129:1710 */         int bit10 = i;
/* 1130:1711 */         for (int j = nonEmpty - 1; j >= 0; j--)
/* 1131:     */         {
/* 1132:1712 */           int mod = bit10 % 2;
/* 1133:1713 */           if (mod == 1) {
/* 1134:1714 */             if (tempStr == "") {
/* 1135:1715 */               tempStr = "(" + nonEmptyValues[j] + ")";
/* 1136:     */             } else {
/* 1137:1717 */               tempStr = tempStr + "|(" + nonEmptyValues[j] + ")";
/* 1138:     */             }
/* 1139:     */           }
/* 1140:1720 */           bit10 /= 2;
/* 1141:     */         }
/* 1142:1722 */         for (int j = 0; j < sortedIndices.length; j++)
/* 1143:     */         {
/* 1144:1723 */           Instance inst = data.instance(sortedIndices[j]);
/* 1145:1724 */           if (inst.isMissing(att)) {
/* 1146:     */             break;
/* 1147:     */           }
/* 1148:1728 */           if (tempStr.indexOf("(" + att.value((int)inst.value(att)) + ")") != -1) {
/* 1149:1729 */             currDist[0][((int)inst.classValue())] += weights[j];
/* 1150:     */           } else {
/* 1151:1731 */             currDist[1][((int)inst.classValue())] += weights[j];
/* 1152:     */           }
/* 1153:     */         }
/* 1154:1735 */         double[][] tempDist = new double[2][numClasses];
/* 1155:1736 */         for (int k = 0; k < 2; k++) {
/* 1156:1737 */           tempDist[k] = currDist[k];
/* 1157:     */         }
/* 1158:1740 */         double[] tempProps = new double[2];
/* 1159:1741 */         for (int k = 0; k < 2; k++) {
/* 1160:1742 */           tempProps[k] = Utils.sum(tempDist[k]);
/* 1161:     */         }
/* 1162:1745 */         if (Utils.sum(tempProps) != 0.0D) {
/* 1163:1746 */           Utils.normalize(tempProps);
/* 1164:     */         }
/* 1165:1750 */         int index = missingStart;
/* 1166:1751 */         while (index < sortedIndices.length)
/* 1167:     */         {
/* 1168:1752 */           Instance insta = data.instance(sortedIndices[index]);
/* 1169:1753 */           for (int j = 0; j < 2; j++) {
/* 1170:1754 */             tempDist[j][((int)insta.classValue())] += tempProps[j] * weights[index];
/* 1171:     */           }
/* 1172:1757 */           index++;
/* 1173:     */         }
/* 1174:     */         double currGain;
/* 1175:     */         double currGain;
/* 1176:1761 */         if (useGini) {
/* 1177:1762 */           currGain = computeGiniGain(parentDist, tempDist);
/* 1178:     */         } else {
/* 1179:1764 */           currGain = computeInfoGain(parentDist, tempDist);
/* 1180:     */         }
/* 1181:1767 */         if (currGain > bestGain)
/* 1182:     */         {
/* 1183:1768 */           bestGain = currGain;
/* 1184:1769 */           bestSplitString = tempStr;
/* 1185:1770 */           for (int j = 0; j < 2; j++) {
/* 1186:1772 */             System.arraycopy(tempDist[j], 0, dist[j], 0, dist[j].length);
/* 1187:     */           }
/* 1188:     */         }
/* 1189:     */       }
/* 1190:     */     }
/* 1191:     */     else
/* 1192:     */     {
/* 1193:1781 */       int n = nonEmpty;
/* 1194:1782 */       int k = data.numClasses();
/* 1195:1783 */       double[][] P = new double[n][k];
/* 1196:1784 */       int[] numInstancesValue = new int[n];
/* 1197:     */       
/* 1198:1786 */       double[] meanClass = new double[k];
/* 1199:1787 */       int numInstances = data.numInstances();
/* 1200:1790 */       for (int j = 0; j < meanClass.length; j++) {
/* 1201:1791 */         meanClass[j] = 0.0D;
/* 1202:     */       }
/* 1203:1794 */       for (int j = 0; j < numInstances; j++)
/* 1204:     */       {
/* 1205:1795 */         Instance inst = data.instance(j);
/* 1206:1796 */         int valueIndex = 0;
/* 1207:1797 */         for (int i = 0; i < nonEmpty; i++) {
/* 1208:1798 */           if (att.value((int)inst.value(att)).compareToIgnoreCase(nonEmptyValues[i]) == 0)
/* 1209:     */           {
/* 1210:1800 */             valueIndex = i;
/* 1211:1801 */             break;
/* 1212:     */           }
/* 1213:     */         }
/* 1214:1804 */         P[valueIndex][((int)inst.classValue())] += 1.0D;
/* 1215:1805 */         numInstancesValue[valueIndex] += 1;
/* 1216:1806 */         meanClass[((int)inst.classValue())] += 1.0D;
/* 1217:     */       }
/* 1218:1810 */       for (int i = 0; i < P.length; i++) {
/* 1219:1811 */         for (int j = 0; j < P[0].length; j++) {
/* 1220:1812 */           if (numInstancesValue[i] == 0) {
/* 1221:1813 */             P[i][j] = 0.0D;
/* 1222:     */           } else {
/* 1223:1815 */             P[i][j] /= numInstancesValue[i];
/* 1224:     */           }
/* 1225:     */         }
/* 1226:     */       }
/* 1227:1821 */       for (int i = 0; i < meanClass.length; i++) {
/* 1228:1822 */         meanClass[i] /= numInstances;
/* 1229:     */       }
/* 1230:1826 */       double[][] covariance = new double[k][k];
/* 1231:1827 */       for (int i1 = 0; i1 < k; i1++) {
/* 1232:1828 */         for (int i2 = 0; i2 < k; i2++)
/* 1233:     */         {
/* 1234:1829 */           double element = 0.0D;
/* 1235:1830 */           for (int j = 0; j < n; j++) {
/* 1236:1831 */             element += (P[j][i2] - meanClass[i2]) * (P[j][i1] - meanClass[i1]) * numInstancesValue[j];
/* 1237:     */           }
/* 1238:1834 */           covariance[i1][i2] = element;
/* 1239:     */         }
/* 1240:     */       }
/* 1241:1838 */       Matrix matrix = new Matrix(covariance);
/* 1242:1839 */       EigenvalueDecomposition eigen = new EigenvalueDecomposition(matrix);
/* 1243:     */       
/* 1244:1841 */       double[] eigenValues = eigen.getRealEigenvalues();
/* 1245:     */       
/* 1246:     */ 
/* 1247:1844 */       int index = 0;
/* 1248:1845 */       double largest = eigenValues[0];
/* 1249:1846 */       for (int i = 1; i < eigenValues.length; i++) {
/* 1250:1847 */         if (eigenValues[i] > largest)
/* 1251:     */         {
/* 1252:1848 */           index = i;
/* 1253:1849 */           largest = eigenValues[i];
/* 1254:     */         }
/* 1255:     */       }
/* 1256:1854 */       double[] FPC = new double[k];
/* 1257:1855 */       Matrix eigenVector = eigen.getV();
/* 1258:1856 */       double[][] vectorArray = eigenVector.getArray();
/* 1259:1857 */       for (int i = 0; i < FPC.length; i++) {
/* 1260:1858 */         FPC[i] = vectorArray[i][index];
/* 1261:     */       }
/* 1262:1862 */       double[] Sa = new double[n];
/* 1263:1863 */       for (int i = 0; i < Sa.length; i++)
/* 1264:     */       {
/* 1265:1864 */         Sa[i] = 0.0D;
/* 1266:1865 */         for (int j = 0; j < k; j++) {
/* 1267:1866 */           Sa[i] += FPC[j] * P[i][j];
/* 1268:     */         }
/* 1269:     */       }
/* 1270:1871 */       double[] pCopy = new double[n];
/* 1271:1872 */       System.arraycopy(Sa, 0, pCopy, 0, n);
/* 1272:1873 */       String[] sortedValues = new String[n];
/* 1273:1874 */       Arrays.sort(Sa);
/* 1274:1876 */       for (int j = 0; j < n; j++)
/* 1275:     */       {
/* 1276:1877 */         sortedValues[j] = nonEmptyValues[Utils.minIndex(pCopy)];
/* 1277:1878 */         pCopy[Utils.minIndex(pCopy)] = 1.7976931348623157E+308D;
/* 1278:     */       }
/* 1279:1882 */       String tempStr = "";
/* 1280:1884 */       for (int j = 0; j < nonEmpty - 1; j++)
/* 1281:     */       {
/* 1282:1885 */         currDist = new double[2][numClasses];
/* 1283:1886 */         if (tempStr == "") {
/* 1284:1887 */           tempStr = "(" + sortedValues[j] + ")";
/* 1285:     */         } else {
/* 1286:1889 */           tempStr = tempStr + "|(" + sortedValues[j] + ")";
/* 1287:     */         }
/* 1288:1891 */         for (int i = 0; i < sortedIndices.length; i++)
/* 1289:     */         {
/* 1290:1892 */           Instance inst = data.instance(sortedIndices[i]);
/* 1291:1893 */           if (inst.isMissing(att)) {
/* 1292:     */             break;
/* 1293:     */           }
/* 1294:1897 */           if (tempStr.indexOf("(" + att.value((int)inst.value(att)) + ")") != -1) {
/* 1295:1898 */             currDist[0][((int)inst.classValue())] += weights[i];
/* 1296:     */           } else {
/* 1297:1900 */             currDist[1][((int)inst.classValue())] += weights[i];
/* 1298:     */           }
/* 1299:     */         }
/* 1300:1904 */         double[][] tempDist = new double[2][numClasses];
/* 1301:1905 */         for (int kk = 0; kk < 2; kk++) {
/* 1302:1906 */           tempDist[kk] = currDist[kk];
/* 1303:     */         }
/* 1304:1909 */         double[] tempProps = new double[2];
/* 1305:1910 */         for (int kk = 0; kk < 2; kk++) {
/* 1306:1911 */           tempProps[kk] = Utils.sum(tempDist[kk]);
/* 1307:     */         }
/* 1308:1914 */         if (Utils.sum(tempProps) != 0.0D) {
/* 1309:1915 */           Utils.normalize(tempProps);
/* 1310:     */         }
/* 1311:1919 */         int mstart = missingStart;
/* 1312:1920 */         while (mstart < sortedIndices.length)
/* 1313:     */         {
/* 1314:1921 */           Instance insta = data.instance(sortedIndices[mstart]);
/* 1315:1922 */           for (int jj = 0; jj < 2; jj++) {
/* 1316:1923 */             tempDist[jj][((int)insta.classValue())] += tempProps[jj] * weights[mstart];
/* 1317:     */           }
/* 1318:1926 */           mstart++;
/* 1319:     */         }
/* 1320:     */         double currGain;
/* 1321:     */         double currGain;
/* 1322:1930 */         if (useGini) {
/* 1323:1931 */           currGain = computeGiniGain(parentDist, tempDist);
/* 1324:     */         } else {
/* 1325:1933 */           currGain = computeInfoGain(parentDist, tempDist);
/* 1326:     */         }
/* 1327:1936 */         if (currGain > bestGain)
/* 1328:     */         {
/* 1329:1937 */           bestGain = currGain;
/* 1330:1938 */           bestSplitString = tempStr;
/* 1331:1939 */           for (int jj = 0; jj < 2; jj++) {
/* 1332:1941 */             System.arraycopy(tempDist[jj], 0, dist[jj], 0, dist[jj].length);
/* 1333:     */           }
/* 1334:     */         }
/* 1335:     */       }
/* 1336:     */     }
/* 1337:1948 */     int attIndex = att.index();
/* 1338:1949 */     props[attIndex] = new double[2];
/* 1339:1950 */     for (int k = 0; k < 2; k++) {
/* 1340:1951 */       props[attIndex][k] = Utils.sum(dist[k]);
/* 1341:     */     }
/* 1342:1953 */     if (Utils.sum(props[attIndex]) <= 0.0D) {
/* 1343:1954 */       for (int k = 0; k < props[attIndex].length; k++) {
/* 1344:1955 */         props[attIndex][k] = (1.0D / props[attIndex].length);
/* 1345:     */       }
/* 1346:     */     } else {
/* 1347:1958 */       Utils.normalize(props[attIndex]);
/* 1348:     */     }
/* 1349:1962 */     subsetWeights[attIndex] = new double[2];
/* 1350:1963 */     for (int j = 0; j < 2; j++) {
/* 1351:1964 */       subsetWeights[attIndex][j] += Utils.sum(dist[j]);
/* 1352:     */     }
/* 1353:1970 */     for (int j = 0; j < empty; j++) {
/* 1354:1971 */       if (props[attIndex][0] >= props[attIndex][1]) {
/* 1355:1972 */         if (bestSplitString == "") {
/* 1356:1973 */           bestSplitString = "(" + emptyValues[j] + ")";
/* 1357:     */         } else {
/* 1358:1975 */           bestSplitString = bestSplitString + "|(" + emptyValues[j] + ")";
/* 1359:     */         }
/* 1360:     */       }
/* 1361:     */     }
/* 1362:1981 */     gains[attIndex] = (Math.rint(bestGain * 10000000.0D) / 10000000.0D);
/* 1363:     */     
/* 1364:1983 */     dists[attIndex] = dist;
/* 1365:1984 */     return bestSplitString;
/* 1366:     */   }
/* 1367:     */   
/* 1368:     */   protected void splitData(int[][][] subsetIndices, double[][][] subsetWeights, Attribute att, double splitPoint, String splitStr, int[][] sortedIndices, double[][] weights, Instances data)
/* 1369:     */     throws Exception
/* 1370:     */   {
/* 1371:2009 */     for (int i = 0; i < data.numAttributes(); i++) {
/* 1372:2010 */       if (i != data.classIndex())
/* 1373:     */       {
/* 1374:2013 */         int[] num = new int[2];
/* 1375:2014 */         for (int k = 0; k < 2; k++)
/* 1376:     */         {
/* 1377:2015 */           subsetIndices[k][i] = new int[sortedIndices[i].length];
/* 1378:2016 */           subsetWeights[k][i] = new double[weights[i].length];
/* 1379:     */         }
/* 1380:2019 */         for (int j = 0; j < sortedIndices[i].length; j++)
/* 1381:     */         {
/* 1382:2020 */           Instance inst = data.instance(sortedIndices[i][j]);
/* 1383:2021 */           if (inst.isMissing(att))
/* 1384:     */           {
/* 1385:2023 */             for (int k = 0; k < 2; k++) {
/* 1386:2024 */               if (this.m_Props[k] > 0.0D)
/* 1387:     */               {
/* 1388:2025 */                 subsetIndices[k][i][num[k]] = sortedIndices[i][j];
/* 1389:2026 */                 subsetWeights[k][i][num[k]] = (this.m_Props[k] * weights[i][j]);
/* 1390:2027 */                 num[k] += 1;
/* 1391:     */               }
/* 1392:     */             }
/* 1393:     */           }
/* 1394:     */           else
/* 1395:     */           {
/* 1396:     */             int subset;
/* 1397:     */             int subset;
/* 1398:2032 */             if (att.isNumeric())
/* 1399:     */             {
/* 1400:2033 */               subset = inst.value(att) < splitPoint ? 0 : 1;
/* 1401:     */             }
/* 1402:     */             else
/* 1403:     */             {
/* 1404:     */               int subset;
/* 1405:2035 */               if (splitStr.indexOf("(" + att.value((int)inst.value(att.index())) + ")") != -1) {
/* 1406:2037 */                 subset = 0;
/* 1407:     */               } else {
/* 1408:2039 */                 subset = 1;
/* 1409:     */               }
/* 1410:     */             }
/* 1411:2042 */             subsetIndices[subset][i][num[subset]] = sortedIndices[i][j];
/* 1412:2043 */             subsetWeights[subset][i][num[subset]] = weights[i][j];
/* 1413:2044 */             num[subset] += 1;
/* 1414:     */           }
/* 1415:     */         }
/* 1416:2049 */         for (int k = 0; k < 2; k++)
/* 1417:     */         {
/* 1418:2050 */           int[] copy = new int[num[k]];
/* 1419:2051 */           System.arraycopy(subsetIndices[k][i], 0, copy, 0, num[k]);
/* 1420:2052 */           subsetIndices[k][i] = copy;
/* 1421:2053 */           double[] copyWeights = new double[num[k]];
/* 1422:2054 */           System.arraycopy(subsetWeights[k][i], 0, copyWeights, 0, num[k]);
/* 1423:2055 */           subsetWeights[k][i] = copyWeights;
/* 1424:     */         }
/* 1425:     */       }
/* 1426:     */     }
/* 1427:     */   }
/* 1428:     */   
/* 1429:     */   protected double computeGiniGain(double[] parentDist, double[][] childDist)
/* 1430:     */   {
/* 1431:2069 */     double totalWeight = Utils.sum(parentDist);
/* 1432:2070 */     if (totalWeight == 0.0D) {
/* 1433:2071 */       return 0.0D;
/* 1434:     */     }
/* 1435:2074 */     double leftWeight = Utils.sum(childDist[0]);
/* 1436:2075 */     double rightWeight = Utils.sum(childDist[1]);
/* 1437:     */     
/* 1438:2077 */     double parentGini = computeGini(parentDist, totalWeight);
/* 1439:2078 */     double leftGini = computeGini(childDist[0], leftWeight);
/* 1440:2079 */     double rightGini = computeGini(childDist[1], rightWeight);
/* 1441:     */     
/* 1442:2081 */     return parentGini - leftWeight / totalWeight * leftGini - rightWeight / totalWeight * rightGini;
/* 1443:     */   }
/* 1444:     */   
/* 1445:     */   protected double computeGini(double[] dist, double total)
/* 1446:     */   {
/* 1447:2093 */     if (total == 0.0D) {
/* 1448:2094 */       return 0.0D;
/* 1449:     */     }
/* 1450:2096 */     double val = 0.0D;
/* 1451:2097 */     for (double element : dist) {
/* 1452:2098 */       val += element / total * (element / total);
/* 1453:     */     }
/* 1454:2100 */     return 1.0D - val;
/* 1455:     */   }
/* 1456:     */   
/* 1457:     */   protected double computeInfoGain(double[] parentDist, double[][] childDist)
/* 1458:     */   {
/* 1459:2112 */     double totalWeight = Utils.sum(parentDist);
/* 1460:2113 */     if (totalWeight == 0.0D) {
/* 1461:2114 */       return 0.0D;
/* 1462:     */     }
/* 1463:2117 */     double leftWeight = Utils.sum(childDist[0]);
/* 1464:2118 */     double rightWeight = Utils.sum(childDist[1]);
/* 1465:     */     
/* 1466:2120 */     double parentInfo = computeEntropy(parentDist, totalWeight);
/* 1467:2121 */     double leftInfo = computeEntropy(childDist[0], leftWeight);
/* 1468:2122 */     double rightInfo = computeEntropy(childDist[1], rightWeight);
/* 1469:     */     
/* 1470:2124 */     return parentInfo - leftWeight / totalWeight * leftInfo - rightWeight / totalWeight * rightInfo;
/* 1471:     */   }
/* 1472:     */   
/* 1473:     */   protected double computeEntropy(double[] dist, double total)
/* 1474:     */   {
/* 1475:2136 */     if (total == 0.0D) {
/* 1476:2137 */       return 0.0D;
/* 1477:     */     }
/* 1478:2139 */     double entropy = 0.0D;
/* 1479:2140 */     for (double element : dist) {
/* 1480:2141 */       if (element != 0.0D) {
/* 1481:2142 */         entropy -= element / total * Utils.log2(element / total);
/* 1482:     */       }
/* 1483:     */     }
/* 1484:2145 */     return entropy;
/* 1485:     */   }
/* 1486:     */   
/* 1487:     */   protected void makeLeaf(Instances data)
/* 1488:     */   {
/* 1489:2154 */     this.m_Attribute = null;
/* 1490:2155 */     this.m_isLeaf = true;
/* 1491:2156 */     this.m_ClassValue = Utils.maxIndex(this.m_ClassProbs);
/* 1492:2157 */     this.m_ClassAttribute = data.classAttribute();
/* 1493:     */   }
/* 1494:     */   
/* 1495:     */   public double[] distributionForInstance(Instance instance)
/* 1496:     */     throws Exception
/* 1497:     */   {
/* 1498:2170 */     if (!this.m_isLeaf)
/* 1499:     */     {
/* 1500:2172 */       if (instance.isMissing(this.m_Attribute))
/* 1501:     */       {
/* 1502:2173 */         double[] returnedDist = new double[this.m_ClassProbs.length];
/* 1503:2175 */         for (int i = 0; i < this.m_Successors.length; i++)
/* 1504:     */         {
/* 1505:2176 */           double[] help = this.m_Successors[i].distributionForInstance(instance);
/* 1506:2177 */           if (help != null) {
/* 1507:2178 */             for (int j = 0; j < help.length; j++) {
/* 1508:2179 */               returnedDist[j] += this.m_Props[i] * help[j];
/* 1509:     */             }
/* 1510:     */           }
/* 1511:     */         }
/* 1512:2183 */         return returnedDist;
/* 1513:     */       }
/* 1514:2187 */       if (this.m_Attribute.isNominal())
/* 1515:     */       {
/* 1516:2188 */         if (this.m_SplitString.indexOf("(" + this.m_Attribute.value((int)instance.value(this.m_Attribute)) + ")") != -1) {
/* 1517:2190 */           return this.m_Successors[0].distributionForInstance(instance);
/* 1518:     */         }
/* 1519:2192 */         return this.m_Successors[1].distributionForInstance(instance);
/* 1520:     */       }
/* 1521:2198 */       if (instance.value(this.m_Attribute) < this.m_SplitValue) {
/* 1522:2199 */         return this.m_Successors[0].distributionForInstance(instance);
/* 1523:     */       }
/* 1524:2201 */       return this.m_Successors[1].distributionForInstance(instance);
/* 1525:     */     }
/* 1526:2205 */     return this.m_ClassProbs;
/* 1527:     */   }
/* 1528:     */   
/* 1529:     */   public String toString()
/* 1530:     */   {
/* 1531:2216 */     if ((this.m_Distribution == null) && (this.m_Successors == null)) {
/* 1532:2217 */       return "Best-First: No model built yet.";
/* 1533:     */     }
/* 1534:2219 */     return "Best-First Decision Tree\n" + toString(0) + "\n\n" + "Size of the Tree: " + numNodes() + "\n\n" + "Number of Leaf Nodes: " + numLeaves();
/* 1535:     */   }
/* 1536:     */   
/* 1537:     */   protected String toString(int level)
/* 1538:     */   {
/* 1539:2231 */     StringBuffer text = new StringBuffer();
/* 1540:2233 */     if (this.m_Attribute == null)
/* 1541:     */     {
/* 1542:2234 */       if (Utils.isMissingValue(this.m_ClassValue))
/* 1543:     */       {
/* 1544:2235 */         text.append(": null");
/* 1545:     */       }
/* 1546:     */       else
/* 1547:     */       {
/* 1548:2237 */         double correctNum = Math.rint(this.m_Distribution[Utils.maxIndex(this.m_Distribution)] * 100.0D) / 100.0D;
/* 1549:     */         
/* 1550:2239 */         double wrongNum = Math.rint((Utils.sum(this.m_Distribution) - this.m_Distribution[Utils.maxIndex(this.m_Distribution)]) * 100.0D) / 100.0D;
/* 1551:     */         
/* 1552:     */ 
/* 1553:2242 */         String str = "(" + correctNum + "/" + wrongNum + ")";
/* 1554:2243 */         text.append(": " + this.m_ClassAttribute.value((int)this.m_ClassValue) + str);
/* 1555:     */       }
/* 1556:     */     }
/* 1557:     */     else {
/* 1558:2246 */       for (int j = 0; j < 2; j++)
/* 1559:     */       {
/* 1560:2247 */         text.append("\n");
/* 1561:2248 */         for (int i = 0; i < level; i++) {
/* 1562:2249 */           text.append("|  ");
/* 1563:     */         }
/* 1564:2251 */         if (j == 0)
/* 1565:     */         {
/* 1566:2252 */           if (this.m_Attribute.isNumeric()) {
/* 1567:2253 */             text.append(this.m_Attribute.name() + " < " + this.m_SplitValue);
/* 1568:     */           } else {
/* 1569:2255 */             text.append(this.m_Attribute.name() + "=" + this.m_SplitString);
/* 1570:     */           }
/* 1571:     */         }
/* 1572:2258 */         else if (this.m_Attribute.isNumeric()) {
/* 1573:2259 */           text.append(this.m_Attribute.name() + " >= " + this.m_SplitValue);
/* 1574:     */         } else {
/* 1575:2261 */           text.append(this.m_Attribute.name() + "!=" + this.m_SplitString);
/* 1576:     */         }
/* 1577:2264 */         text.append(this.m_Successors[j].toString(level + 1));
/* 1578:     */       }
/* 1579:     */     }
/* 1580:2267 */     return text.toString();
/* 1581:     */   }
/* 1582:     */   
/* 1583:     */   public int numNodes()
/* 1584:     */   {
/* 1585:2276 */     if (this.m_isLeaf) {
/* 1586:2277 */       return 1;
/* 1587:     */     }
/* 1588:2279 */     int size = 1;
/* 1589:2280 */     for (BFTree m_Successor : this.m_Successors) {
/* 1590:2281 */       size += m_Successor.numNodes();
/* 1591:     */     }
/* 1592:2283 */     return size;
/* 1593:     */   }
/* 1594:     */   
/* 1595:     */   public int numLeaves()
/* 1596:     */   {
/* 1597:2293 */     if (this.m_isLeaf) {
/* 1598:2294 */       return 1;
/* 1599:     */     }
/* 1600:2296 */     int size = 0;
/* 1601:2297 */     for (BFTree m_Successor : this.m_Successors) {
/* 1602:2298 */       size += m_Successor.numLeaves();
/* 1603:     */     }
/* 1604:2300 */     return size;
/* 1605:     */   }
/* 1606:     */   
/* 1607:     */   public Enumeration<Option> listOptions()
/* 1608:     */   {
/* 1609:2312 */     Vector<Option> result = new Vector();
/* 1610:     */     
/* 1611:2314 */     result.addElement(new Option("\tThe pruning strategy.\n\t(default: " + new SelectedTag(1, TAGS_PRUNING) + ")", "P", 1, "-P " + Tag.toOptionList(TAGS_PRUNING)));
/* 1612:     */     
/* 1613:     */ 
/* 1614:     */ 
/* 1615:2318 */     result.addElement(new Option("\tThe minimal number of instances at the terminal nodes.\n\t(default 2)", "M", 1, "-M <min no>"));
/* 1616:     */     
/* 1617:     */ 
/* 1618:     */ 
/* 1619:2322 */     result.addElement(new Option("\tThe number of folds used in the pruning.\n\t(default 5)", "N", 5, "-N <num folds>"));
/* 1620:     */     
/* 1621:     */ 
/* 1622:2325 */     result.addElement(new Option("\tDon't use heuristic search for nominal attributes in multi-class\n\tproblem (default yes).\n", "H", 0, "-H"));
/* 1623:     */     
/* 1624:     */ 
/* 1625:     */ 
/* 1626:2329 */     result.addElement(new Option("\tDon't use Gini index for splitting (default yes),\n\tif not information is used.", "G", 0, "-G"));
/* 1627:     */     
/* 1628:     */ 
/* 1629:     */ 
/* 1630:2333 */     result.addElement(new Option("\tDon't use error rate in internal cross-validation (default yes), \n\tbut root mean squared error.", "R", 0, "-R"));
/* 1631:     */     
/* 1632:     */ 
/* 1633:     */ 
/* 1634:2337 */     result.addElement(new Option("\tUse the 1 SE rule to make pruning decision.\n\t(default no).", "A", 0, "-A"));
/* 1635:     */     
/* 1636:     */ 
/* 1637:     */ 
/* 1638:2341 */     result.addElement(new Option("\tPercentage of training data size (0-1]\n\t(default 1).", "C", 0, "-C"));
/* 1639:     */     
/* 1640:     */ 
/* 1641:2344 */     result.addAll(Collections.list(super.listOptions()));
/* 1642:     */     
/* 1643:2346 */     return result.elements();
/* 1644:     */   }
/* 1645:     */   
/* 1646:     */   public void setOptions(String[] options)
/* 1647:     */     throws Exception
/* 1648:     */   {
/* 1649:2425 */     String tmpStr = Utils.getOption('M', options);
/* 1650:2426 */     if (tmpStr.length() != 0) {
/* 1651:2427 */       setMinNumObj(Integer.parseInt(tmpStr));
/* 1652:     */     } else {
/* 1653:2429 */       setMinNumObj(2);
/* 1654:     */     }
/* 1655:2432 */     tmpStr = Utils.getOption('N', options);
/* 1656:2433 */     if (tmpStr.length() != 0) {
/* 1657:2434 */       setNumFoldsPruning(Integer.parseInt(tmpStr));
/* 1658:     */     } else {
/* 1659:2436 */       setNumFoldsPruning(5);
/* 1660:     */     }
/* 1661:2439 */     tmpStr = Utils.getOption('C', options);
/* 1662:2440 */     if (tmpStr.length() != 0) {
/* 1663:2441 */       setSizePer(Double.parseDouble(tmpStr));
/* 1664:     */     } else {
/* 1665:2443 */       setSizePer(1.0D);
/* 1666:     */     }
/* 1667:2446 */     tmpStr = Utils.getOption('P', options);
/* 1668:2447 */     if (tmpStr.length() != 0) {
/* 1669:2448 */       setPruningStrategy(new SelectedTag(tmpStr, TAGS_PRUNING));
/* 1670:     */     } else {
/* 1671:2450 */       setPruningStrategy(new SelectedTag(1, TAGS_PRUNING));
/* 1672:     */     }
/* 1673:2453 */     setHeuristic(!Utils.getFlag('H', options));
/* 1674:     */     
/* 1675:2455 */     setUseGini(!Utils.getFlag('G', options));
/* 1676:     */     
/* 1677:2457 */     setUseErrorRate(!Utils.getFlag('R', options));
/* 1678:     */     
/* 1679:2459 */     setUseOneSE(Utils.getFlag('A', options));
/* 1680:     */     
/* 1681:2461 */     super.setOptions(options);
/* 1682:     */     
/* 1683:2463 */     Utils.checkForRemainingOptions(options);
/* 1684:     */   }
/* 1685:     */   
/* 1686:     */   public String[] getOptions()
/* 1687:     */   {
/* 1688:2474 */     Vector<String> result = new Vector();
/* 1689:     */     
/* 1690:2476 */     result.add("-M");
/* 1691:2477 */     result.add("" + getMinNumObj());
/* 1692:     */     
/* 1693:2479 */     result.add("-N");
/* 1694:2480 */     result.add("" + getNumFoldsPruning());
/* 1695:2482 */     if (!getHeuristic()) {
/* 1696:2483 */       result.add("-H");
/* 1697:     */     }
/* 1698:2486 */     if (!getUseGini()) {
/* 1699:2487 */       result.add("-G");
/* 1700:     */     }
/* 1701:2490 */     if (!getUseErrorRate()) {
/* 1702:2491 */       result.add("-R");
/* 1703:     */     }
/* 1704:2494 */     if (getUseOneSE()) {
/* 1705:2495 */       result.add("-A");
/* 1706:     */     }
/* 1707:2498 */     result.add("-C");
/* 1708:2499 */     result.add("" + getSizePer());
/* 1709:     */     
/* 1710:2501 */     result.add("-P");
/* 1711:2502 */     result.add("" + getPruningStrategy());
/* 1712:     */     
/* 1713:2504 */     Collections.addAll(result, super.getOptions());
/* 1714:     */     
/* 1715:2506 */     return (String[])result.toArray(new String[result.size()]);
/* 1716:     */   }
/* 1717:     */   
/* 1718:     */   public Enumeration<String> enumerateMeasures()
/* 1719:     */   {
/* 1720:2516 */     Vector<String> result = new Vector();
/* 1721:     */     
/* 1722:2518 */     result.addElement("measureTreeSize");
/* 1723:     */     
/* 1724:2520 */     return result.elements();
/* 1725:     */   }
/* 1726:     */   
/* 1727:     */   public double measureTreeSize()
/* 1728:     */   {
/* 1729:2529 */     return numNodes();
/* 1730:     */   }
/* 1731:     */   
/* 1732:     */   public double getMeasure(String additionalMeasureName)
/* 1733:     */   {
/* 1734:2541 */     if (additionalMeasureName.compareToIgnoreCase("measureTreeSize") == 0) {
/* 1735:2542 */       return measureTreeSize();
/* 1736:     */     }
/* 1737:2544 */     throw new IllegalArgumentException(additionalMeasureName + " not supported (Best-First)");
/* 1738:     */   }
/* 1739:     */   
/* 1740:     */   public String pruningStrategyTipText()
/* 1741:     */   {
/* 1742:2556 */     return "Sets the pruning strategy.";
/* 1743:     */   }
/* 1744:     */   
/* 1745:     */   public void setPruningStrategy(SelectedTag value)
/* 1746:     */   {
/* 1747:2565 */     if (value.getTags() == TAGS_PRUNING) {
/* 1748:2566 */       this.m_PruningStrategy = value.getSelectedTag().getID();
/* 1749:     */     }
/* 1750:     */   }
/* 1751:     */   
/* 1752:     */   public SelectedTag getPruningStrategy()
/* 1753:     */   {
/* 1754:2576 */     return new SelectedTag(this.m_PruningStrategy, TAGS_PRUNING);
/* 1755:     */   }
/* 1756:     */   
/* 1757:     */   public String minNumObjTipText()
/* 1758:     */   {
/* 1759:2586 */     return "Set minimal number of instances at the terminal nodes.";
/* 1760:     */   }
/* 1761:     */   
/* 1762:     */   public void setMinNumObj(int value)
/* 1763:     */   {
/* 1764:2595 */     this.m_minNumObj = value;
/* 1765:     */   }
/* 1766:     */   
/* 1767:     */   public int getMinNumObj()
/* 1768:     */   {
/* 1769:2604 */     return this.m_minNumObj;
/* 1770:     */   }
/* 1771:     */   
/* 1772:     */   public String numFoldsPruningTipText()
/* 1773:     */   {
/* 1774:2614 */     return "Number of folds in internal cross-validation.";
/* 1775:     */   }
/* 1776:     */   
/* 1777:     */   public void setNumFoldsPruning(int value)
/* 1778:     */   {
/* 1779:2623 */     this.m_numFoldsPruning = value;
/* 1780:     */   }
/* 1781:     */   
/* 1782:     */   public int getNumFoldsPruning()
/* 1783:     */   {
/* 1784:2632 */     return this.m_numFoldsPruning;
/* 1785:     */   }
/* 1786:     */   
/* 1787:     */   public String heuristicTipText()
/* 1788:     */   {
/* 1789:2642 */     return "If heuristic search is used for binary split for nominal attributes.";
/* 1790:     */   }
/* 1791:     */   
/* 1792:     */   public void setHeuristic(boolean value)
/* 1793:     */   {
/* 1794:2652 */     this.m_Heuristic = value;
/* 1795:     */   }
/* 1796:     */   
/* 1797:     */   public boolean getHeuristic()
/* 1798:     */   {
/* 1799:2662 */     return this.m_Heuristic;
/* 1800:     */   }
/* 1801:     */   
/* 1802:     */   public String useGiniTipText()
/* 1803:     */   {
/* 1804:2672 */     return "If true the Gini index is used for splitting criterion, otherwise the information is used.";
/* 1805:     */   }
/* 1806:     */   
/* 1807:     */   public void setUseGini(boolean value)
/* 1808:     */   {
/* 1809:2681 */     this.m_UseGini = value;
/* 1810:     */   }
/* 1811:     */   
/* 1812:     */   public boolean getUseGini()
/* 1813:     */   {
/* 1814:2690 */     return this.m_UseGini;
/* 1815:     */   }
/* 1816:     */   
/* 1817:     */   public String useErrorRateTipText()
/* 1818:     */   {
/* 1819:2700 */     return "If error rate is used as error estimate. if not, root mean squared error is used.";
/* 1820:     */   }
/* 1821:     */   
/* 1822:     */   public void setUseErrorRate(boolean value)
/* 1823:     */   {
/* 1824:2709 */     this.m_UseErrorRate = value;
/* 1825:     */   }
/* 1826:     */   
/* 1827:     */   public boolean getUseErrorRate()
/* 1828:     */   {
/* 1829:2718 */     return this.m_UseErrorRate;
/* 1830:     */   }
/* 1831:     */   
/* 1832:     */   public String useOneSETipText()
/* 1833:     */   {
/* 1834:2728 */     return "Use the 1SE rule to make pruning decision.";
/* 1835:     */   }
/* 1836:     */   
/* 1837:     */   public void setUseOneSE(boolean value)
/* 1838:     */   {
/* 1839:2737 */     this.m_UseOneSE = value;
/* 1840:     */   }
/* 1841:     */   
/* 1842:     */   public boolean getUseOneSE()
/* 1843:     */   {
/* 1844:2746 */     return this.m_UseOneSE;
/* 1845:     */   }
/* 1846:     */   
/* 1847:     */   public String sizePerTipText()
/* 1848:     */   {
/* 1849:2756 */     return "The percentage of the training set size (0-1, 0 not included).";
/* 1850:     */   }
/* 1851:     */   
/* 1852:     */   public void setSizePer(double value)
/* 1853:     */   {
/* 1854:2765 */     if ((value <= 0.0D) || (value > 1.0D)) {
/* 1855:2766 */       System.err.println("The percentage of the training set size must be in range 0 to 1 (0 not included) - ignored!");
/* 1856:     */     } else {
/* 1857:2770 */       this.m_SizePer = value;
/* 1858:     */     }
/* 1859:     */   }
/* 1860:     */   
/* 1861:     */   public double getSizePer()
/* 1862:     */   {
/* 1863:2780 */     return this.m_SizePer;
/* 1864:     */   }
/* 1865:     */   
/* 1866:     */   public String getRevision()
/* 1867:     */   {
/* 1868:2790 */     return RevisionUtils.extract("$Revision: 10326 $");
/* 1869:     */   }
/* 1870:     */   
/* 1871:     */   public static void main(String[] args)
/* 1872:     */   {
/* 1873:2799 */     runClassifier(new BFTree(), args);
/* 1874:     */   }
/* 1875:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.BFTree
 * JD-Core Version:    0.7.0.1
 */