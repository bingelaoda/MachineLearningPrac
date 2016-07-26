/*    1:     */ package weka.classifiers.trees.m5;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.util.ArrayList;
/*    5:     */ import weka.classifiers.AbstractClassifier;
/*    6:     */ import weka.classifiers.Evaluation;
/*    7:     */ import weka.classifiers.functions.LinearRegression;
/*    8:     */ import weka.core.Attribute;
/*    9:     */ import weka.core.Instance;
/*   10:     */ import weka.core.Instances;
/*   11:     */ import weka.core.RevisionUtils;
/*   12:     */ import weka.core.Utils;
/*   13:     */ import weka.filters.Filter;
/*   14:     */ import weka.filters.unsupervised.attribute.Remove;
/*   15:     */ 
/*   16:     */ public class RuleNode
/*   17:     */   extends AbstractClassifier
/*   18:     */ {
/*   19:     */   static final long serialVersionUID = 1979807611124337144L;
/*   20:     */   private Instances m_instances;
/*   21:     */   private int m_classIndex;
/*   22:     */   protected int m_numInstances;
/*   23:     */   private int m_numAttributes;
/*   24:     */   private boolean m_isLeaf;
/*   25:     */   private int m_splitAtt;
/*   26:     */   private double m_splitValue;
/*   27:     */   private PreConstructedLinearModel m_nodeModel;
/*   28:     */   public int m_numParameters;
/*   29:     */   private double m_rootMeanSquaredError;
/*   30:     */   protected RuleNode m_left;
/*   31:     */   protected RuleNode m_right;
/*   32:     */   private final RuleNode m_parent;
/*   33: 117 */   private double m_splitNum = 4.0D;
/*   34: 123 */   private final double m_devFraction = 0.05D;
/*   35: 124 */   private final double m_pruningMultiplier = 2.0D;
/*   36:     */   private int m_leafModelNum;
/*   37:     */   private final double m_globalDeviation;
/*   38:     */   private final double m_globalAbsDeviation;
/*   39:     */   private int[] m_indices;
/*   40:     */   private static final double SMOOTHING_CONSTANT = 15.0D;
/*   41:     */   private int m_id;
/*   42: 163 */   private boolean m_saveInstances = false;
/*   43:     */   private boolean m_regressionTree;
/*   44:     */   
/*   45:     */   public RuleNode(double globalDev, double globalAbsDev, RuleNode parent)
/*   46:     */   {
/*   47: 178 */     this.m_nodeModel = null;
/*   48: 179 */     this.m_right = null;
/*   49: 180 */     this.m_left = null;
/*   50: 181 */     this.m_parent = parent;
/*   51: 182 */     this.m_globalDeviation = globalDev;
/*   52: 183 */     this.m_globalAbsDeviation = globalAbsDev;
/*   53:     */   }
/*   54:     */   
/*   55:     */   public void buildClassifier(Instances data)
/*   56:     */     throws Exception
/*   57:     */   {
/*   58: 195 */     this.m_rootMeanSquaredError = 1.7976931348623157E+308D;
/*   59:     */     
/*   60: 197 */     this.m_instances = data;
/*   61: 198 */     this.m_classIndex = this.m_instances.classIndex();
/*   62: 199 */     this.m_numInstances = this.m_instances.numInstances();
/*   63: 200 */     this.m_numAttributes = this.m_instances.numAttributes();
/*   64: 201 */     this.m_nodeModel = null;
/*   65: 202 */     this.m_right = null;
/*   66: 203 */     this.m_left = null;
/*   67: 205 */     if ((this.m_numInstances < this.m_splitNum) || (Rule.stdDev(this.m_classIndex, this.m_instances) < this.m_globalDeviation * 0.05D)) {
/*   68: 207 */       this.m_isLeaf = true;
/*   69:     */     } else {
/*   70: 209 */       this.m_isLeaf = false;
/*   71:     */     }
/*   72: 212 */     split();
/*   73:     */   }
/*   74:     */   
/*   75:     */   public double classifyInstance(Instance inst)
/*   76:     */     throws Exception
/*   77:     */   {
/*   78: 225 */     if (this.m_isLeaf)
/*   79:     */     {
/*   80: 226 */       if (this.m_nodeModel == null) {
/*   81: 227 */         throw new Exception("Classifier has not been built correctly.");
/*   82:     */       }
/*   83: 230 */       return this.m_nodeModel.classifyInstance(inst);
/*   84:     */     }
/*   85: 233 */     if (inst.value(this.m_splitAtt) <= this.m_splitValue) {
/*   86: 234 */       return this.m_left.classifyInstance(inst);
/*   87:     */     }
/*   88: 236 */     return this.m_right.classifyInstance(inst);
/*   89:     */   }
/*   90:     */   
/*   91:     */   protected static double smoothingOriginal(double n, double pred, double supportPred)
/*   92:     */     throws Exception
/*   93:     */   {
/*   94: 254 */     double smoothed = (n * pred + 15.0D * supportPred) / (n + 15.0D);
/*   95:     */     
/*   96:     */ 
/*   97: 257 */     return smoothed;
/*   98:     */   }
/*   99:     */   
/*  100:     */   public void split()
/*  101:     */     throws Exception
/*  102:     */   {
/*  103: 271 */     if (!this.m_isLeaf)
/*  104:     */     {
/*  105: 273 */       SplitEvaluate bestSplit = new YongSplitInfo(0, this.m_numInstances - 1, -1);
/*  106: 274 */       SplitEvaluate currentSplit = new YongSplitInfo(0, this.m_numInstances - 1, -1);
/*  107: 277 */       for (int i = 0; i < this.m_numAttributes; i++) {
/*  108: 278 */         if (i != this.m_classIndex)
/*  109:     */         {
/*  110: 281 */           this.m_instances.sort(i);
/*  111: 282 */           currentSplit.attrSplit(i, this.m_instances);
/*  112: 284 */           if ((Math.abs(currentSplit.maxImpurity() - bestSplit.maxImpurity()) > 1.0E-006D) && (currentSplit.maxImpurity() > bestSplit.maxImpurity() + 1.0E-006D)) {
/*  113: 286 */             bestSplit = currentSplit.copy();
/*  114:     */           }
/*  115:     */         }
/*  116:     */       }
/*  117: 292 */       if ((bestSplit.splitAttr() < 0) || (bestSplit.position() < 1) || (bestSplit.position() > this.m_numInstances - 1))
/*  118:     */       {
/*  119: 294 */         this.m_isLeaf = true;
/*  120:     */       }
/*  121:     */       else
/*  122:     */       {
/*  123: 296 */         this.m_splitAtt = bestSplit.splitAttr();
/*  124: 297 */         this.m_splitValue = bestSplit.splitValue();
/*  125: 298 */         Instances leftSubset = new Instances(this.m_instances, this.m_numInstances);
/*  126: 299 */         Instances rightSubset = new Instances(this.m_instances, this.m_numInstances);
/*  127: 301 */         for (i = 0; i < this.m_numInstances; i++) {
/*  128: 302 */           if (this.m_instances.instance(i).value(this.m_splitAtt) <= this.m_splitValue) {
/*  129: 303 */             leftSubset.add(this.m_instances.instance(i));
/*  130:     */           } else {
/*  131: 305 */             rightSubset.add(this.m_instances.instance(i));
/*  132:     */           }
/*  133:     */         }
/*  134: 309 */         leftSubset.compactify();
/*  135: 310 */         rightSubset.compactify();
/*  136:     */         
/*  137:     */ 
/*  138: 313 */         this.m_left = new RuleNode(this.m_globalDeviation, this.m_globalAbsDeviation, this);
/*  139: 314 */         this.m_left.setMinNumInstances(this.m_splitNum);
/*  140: 315 */         this.m_left.setRegressionTree(this.m_regressionTree);
/*  141: 316 */         this.m_left.setSaveInstances(this.m_saveInstances);
/*  142: 317 */         this.m_left.buildClassifier(leftSubset);
/*  143:     */         
/*  144: 319 */         this.m_right = new RuleNode(this.m_globalDeviation, this.m_globalAbsDeviation, this);
/*  145: 320 */         this.m_right.setMinNumInstances(this.m_splitNum);
/*  146: 321 */         this.m_right.setRegressionTree(this.m_regressionTree);
/*  147: 322 */         this.m_right.setSaveInstances(this.m_saveInstances);
/*  148: 323 */         this.m_right.buildClassifier(rightSubset);
/*  149: 327 */         if (!this.m_regressionTree)
/*  150:     */         {
/*  151: 328 */           boolean[] attsBelow = attsTestedBelow();
/*  152: 329 */           attsBelow[this.m_classIndex] = true;
/*  153: 330 */           int count = 0;
/*  154: 332 */           for (int j = 0; j < this.m_numAttributes; j++) {
/*  155: 333 */             if (attsBelow[j] != 0) {
/*  156: 334 */               count++;
/*  157:     */             }
/*  158:     */           }
/*  159: 338 */           int[] indices = new int[count];
/*  160:     */           
/*  161: 340 */           count = 0;
/*  162: 342 */           for (j = 0; j < this.m_numAttributes; j++) {
/*  163: 343 */             if ((attsBelow[j] != 0) && (j != this.m_classIndex)) {
/*  164: 344 */               indices[(count++)] = j;
/*  165:     */             }
/*  166:     */           }
/*  167: 348 */           indices[count] = this.m_classIndex;
/*  168: 349 */           this.m_indices = indices;
/*  169:     */         }
/*  170:     */         else
/*  171:     */         {
/*  172: 351 */           this.m_indices = new int[1];
/*  173: 352 */           this.m_indices[0] = this.m_classIndex;
/*  174: 353 */           this.m_numParameters = 1;
/*  175:     */         }
/*  176:     */       }
/*  177:     */     }
/*  178: 358 */     if (this.m_isLeaf)
/*  179:     */     {
/*  180: 359 */       int[] indices = new int[1];
/*  181: 360 */       indices[0] = this.m_classIndex;
/*  182: 361 */       this.m_indices = indices;
/*  183: 362 */       this.m_numParameters = 1;
/*  184:     */     }
/*  185:     */   }
/*  186:     */   
/*  187:     */   private void buildLinearModel(int[] indices)
/*  188:     */     throws Exception
/*  189:     */   {
/*  190: 379 */     Instances reducedInst = new Instances(this.m_instances);
/*  191: 380 */     Remove attributeFilter = new Remove();
/*  192:     */     
/*  193: 382 */     attributeFilter.setInvertSelection(true);
/*  194: 383 */     attributeFilter.setAttributeIndicesArray(indices);
/*  195: 384 */     attributeFilter.setInputFormat(reducedInst);
/*  196:     */     
/*  197: 386 */     reducedInst = Filter.useFilter(reducedInst, attributeFilter);
/*  198:     */     
/*  199:     */ 
/*  200:     */ 
/*  201: 390 */     LinearRegression temp = new LinearRegression();
/*  202: 391 */     temp.buildClassifier(reducedInst);
/*  203:     */     
/*  204: 393 */     double[] lmCoeffs = temp.coefficients();
/*  205: 394 */     double[] coeffs = new double[this.m_instances.numAttributes()];
/*  206: 396 */     for (int i = 0; i < lmCoeffs.length - 1; i++) {
/*  207: 397 */       if (indices[i] != this.m_classIndex) {
/*  208: 398 */         coeffs[indices[i]] = lmCoeffs[i];
/*  209:     */       }
/*  210:     */     }
/*  211: 401 */     this.m_nodeModel = new PreConstructedLinearModel(coeffs, lmCoeffs[(lmCoeffs.length - 1)]);
/*  212:     */     
/*  213: 403 */     this.m_nodeModel.buildClassifier(this.m_instances);
/*  214:     */   }
/*  215:     */   
/*  216:     */   private boolean[] attsTestedBelow()
/*  217:     */   {
/*  218: 413 */     boolean[] attsBelow = new boolean[this.m_numAttributes];
/*  219: 414 */     boolean[] attsBelowLeft = null;
/*  220: 415 */     boolean[] attsBelowRight = null;
/*  221: 417 */     if (this.m_right != null) {
/*  222: 418 */       attsBelowRight = this.m_right.attsTestedBelow();
/*  223:     */     }
/*  224: 421 */     if (this.m_left != null) {
/*  225: 422 */       attsBelowLeft = this.m_left.attsTestedBelow();
/*  226:     */     }
/*  227: 425 */     for (int i = 0; i < this.m_numAttributes; i++)
/*  228:     */     {
/*  229: 426 */       if (attsBelowLeft != null) {
/*  230: 427 */         attsBelow[i] = ((attsBelow[i] != 0) || (attsBelowLeft[i] != 0) ? 1 : false);
/*  231:     */       }
/*  232: 430 */       if (attsBelowRight != null) {
/*  233: 431 */         attsBelow[i] = ((attsBelow[i] != 0) || (attsBelowRight[i] != 0) ? 1 : false);
/*  234:     */       }
/*  235:     */     }
/*  236: 435 */     if (!this.m_isLeaf) {
/*  237: 436 */       attsBelow[this.m_splitAtt] = true;
/*  238:     */     }
/*  239: 438 */     return attsBelow;
/*  240:     */   }
/*  241:     */   
/*  242:     */   public int numLeaves(int leafCounter)
/*  243:     */   {
/*  244: 449 */     if (!this.m_isLeaf)
/*  245:     */     {
/*  246: 451 */       this.m_leafModelNum = 0;
/*  247: 453 */       if (this.m_left != null) {
/*  248: 454 */         leafCounter = this.m_left.numLeaves(leafCounter);
/*  249:     */       }
/*  250: 457 */       if (this.m_right != null) {
/*  251: 458 */         leafCounter = this.m_right.numLeaves(leafCounter);
/*  252:     */       }
/*  253:     */     }
/*  254:     */     else
/*  255:     */     {
/*  256: 462 */       leafCounter++;
/*  257: 463 */       this.m_leafModelNum = leafCounter;
/*  258:     */     }
/*  259: 465 */     return leafCounter;
/*  260:     */   }
/*  261:     */   
/*  262:     */   public String toString()
/*  263:     */   {
/*  264: 475 */     return printNodeLinearModel();
/*  265:     */   }
/*  266:     */   
/*  267:     */   public String printNodeLinearModel()
/*  268:     */   {
/*  269: 484 */     return this.m_nodeModel.toString();
/*  270:     */   }
/*  271:     */   
/*  272:     */   public String printLeafModels()
/*  273:     */   {
/*  274: 493 */     StringBuffer text = new StringBuffer();
/*  275: 495 */     if (this.m_isLeaf)
/*  276:     */     {
/*  277: 496 */       text.append("\nLM num: " + this.m_leafModelNum);
/*  278: 497 */       text.append(this.m_nodeModel.toString());
/*  279: 498 */       text.append("\n");
/*  280:     */     }
/*  281:     */     else
/*  282:     */     {
/*  283: 500 */       text.append(this.m_left.printLeafModels());
/*  284: 501 */       text.append(this.m_right.printLeafModels());
/*  285:     */     }
/*  286: 503 */     return text.toString();
/*  287:     */   }
/*  288:     */   
/*  289:     */   public String nodeToString()
/*  290:     */   {
/*  291: 512 */     StringBuffer text = new StringBuffer();
/*  292:     */     
/*  293: 514 */     System.out.println("In to string");
/*  294: 515 */     text.append("Node:\n\tnum inst: " + this.m_numInstances);
/*  295: 517 */     if (this.m_isLeaf) {
/*  296: 518 */       text.append("\n\tleaf");
/*  297:     */     } else {
/*  298: 520 */       text.append("\tnode");
/*  299:     */     }
/*  300: 523 */     text.append("\n\tSplit att: " + this.m_instances.attribute(this.m_splitAtt).name());
/*  301: 524 */     text.append("\n\tSplit val: " + Utils.doubleToString(this.m_splitValue, 1, 3));
/*  302: 525 */     text.append("\n\tLM num: " + this.m_leafModelNum);
/*  303: 526 */     text.append("\n\tLinear model\n" + this.m_nodeModel.toString());
/*  304: 527 */     text.append("\n\n");
/*  305: 529 */     if (this.m_left != null) {
/*  306: 530 */       text.append(this.m_left.nodeToString());
/*  307:     */     }
/*  308: 533 */     if (this.m_right != null) {
/*  309: 534 */       text.append(this.m_right.nodeToString());
/*  310:     */     }
/*  311: 537 */     return text.toString();
/*  312:     */   }
/*  313:     */   
/*  314:     */   public String treeToString(int level)
/*  315:     */   {
/*  316: 548 */     StringBuffer text = new StringBuffer();
/*  317: 550 */     if (!this.m_isLeaf)
/*  318:     */     {
/*  319: 551 */       text.append("\n");
/*  320: 553 */       for (int i = 1; i <= level; i++) {
/*  321: 554 */         text.append("|   ");
/*  322:     */       }
/*  323: 557 */       if (this.m_instances.attribute(this.m_splitAtt).name().charAt(0) != '[') {
/*  324: 558 */         text.append(this.m_instances.attribute(this.m_splitAtt).name() + " <= " + Utils.doubleToString(this.m_splitValue, 1, 3) + " : ");
/*  325:     */       } else {
/*  326: 561 */         text.append(this.m_instances.attribute(this.m_splitAtt).name() + " false : ");
/*  327:     */       }
/*  328: 564 */       if (this.m_left != null) {
/*  329: 565 */         text.append(this.m_left.treeToString(level + 1));
/*  330:     */       } else {
/*  331: 567 */         text.append("NULL\n");
/*  332:     */       }
/*  333: 570 */       for (i = 1; i <= level; i++) {
/*  334: 571 */         text.append("|   ");
/*  335:     */       }
/*  336: 574 */       if (this.m_instances.attribute(this.m_splitAtt).name().charAt(0) != '[') {
/*  337: 575 */         text.append(this.m_instances.attribute(this.m_splitAtt).name() + " >  " + Utils.doubleToString(this.m_splitValue, 1, 3) + " : ");
/*  338:     */       } else {
/*  339: 578 */         text.append(this.m_instances.attribute(this.m_splitAtt).name() + " true : ");
/*  340:     */       }
/*  341: 581 */       if (this.m_right != null) {
/*  342: 582 */         text.append(this.m_right.treeToString(level + 1));
/*  343:     */       } else {
/*  344: 584 */         text.append("NULL\n");
/*  345:     */       }
/*  346:     */     }
/*  347:     */     else
/*  348:     */     {
/*  349: 587 */       text.append("LM" + this.m_leafModelNum);
/*  350: 589 */       if (this.m_globalDeviation > 0.0D) {
/*  351: 590 */         text.append(" (" + this.m_numInstances + "/" + Utils.doubleToString(100.0D * this.m_rootMeanSquaredError / this.m_globalDeviation, 1, 3) + "%)\n");
/*  352:     */       } else {
/*  353: 597 */         text.append(" (" + this.m_numInstances + ")\n");
/*  354:     */       }
/*  355:     */     }
/*  356: 600 */     return text.toString();
/*  357:     */   }
/*  358:     */   
/*  359:     */   public void installLinearModels()
/*  360:     */     throws Exception
/*  361:     */   {
/*  362: 611 */     if (this.m_isLeaf)
/*  363:     */     {
/*  364: 612 */       buildLinearModel(this.m_indices);
/*  365:     */     }
/*  366:     */     else
/*  367:     */     {
/*  368: 614 */       if (this.m_left != null) {
/*  369: 615 */         this.m_left.installLinearModels();
/*  370:     */       }
/*  371: 618 */       if (this.m_right != null) {
/*  372: 619 */         this.m_right.installLinearModels();
/*  373:     */       }
/*  374: 621 */       buildLinearModel(this.m_indices);
/*  375:     */     }
/*  376: 623 */     Evaluation nodeModelEval = new Evaluation(this.m_instances);
/*  377: 624 */     nodeModelEval.evaluateModel(this.m_nodeModel, this.m_instances, new Object[0]);
/*  378: 625 */     this.m_rootMeanSquaredError = nodeModelEval.rootMeanSquaredError();
/*  379: 627 */     if (!this.m_saveInstances) {
/*  380: 628 */       this.m_instances = new Instances(this.m_instances, 0);
/*  381:     */     }
/*  382:     */   }
/*  383:     */   
/*  384:     */   public void installSmoothedModels()
/*  385:     */     throws Exception
/*  386:     */   {
/*  387: 638 */     if (this.m_isLeaf)
/*  388:     */     {
/*  389: 639 */       double[] coefficients = new double[this.m_numAttributes];
/*  390:     */       
/*  391: 641 */       double[] coeffsUsedByLinearModel = this.m_nodeModel.coefficients();
/*  392: 642 */       RuleNode current = this;
/*  393: 645 */       for (int i = 0; i < coeffsUsedByLinearModel.length; i++) {
/*  394: 646 */         if (i != this.m_classIndex) {
/*  395: 647 */           coefficients[i] = coeffsUsedByLinearModel[i];
/*  396:     */         }
/*  397:     */       }
/*  398: 651 */       double intercept = this.m_nodeModel.intercept();
/*  399:     */       do
/*  400:     */       {
/*  401: 654 */         if (current.m_parent != null)
/*  402:     */         {
/*  403: 655 */           double n = current.m_numInstances;
/*  404: 657 */           for (int i = 0; i < coefficients.length; i++) {
/*  405: 658 */             coefficients[i] = (coefficients[i] * n / (n + 15.0D));
/*  406:     */           }
/*  407: 660 */           intercept = intercept * n / (n + 15.0D);
/*  408:     */           
/*  409:     */ 
/*  410: 663 */           coeffsUsedByLinearModel = current.m_parent.getModel().coefficients();
/*  411: 664 */           for (int i = 0; i < coeffsUsedByLinearModel.length; i++) {
/*  412: 665 */             if (i != this.m_classIndex) {
/*  413: 667 */               coefficients[i] += 15.0D * coeffsUsedByLinearModel[i] / (n + 15.0D);
/*  414:     */             }
/*  415:     */           }
/*  416: 671 */           intercept += 15.0D * current.m_parent.getModel().intercept() / (n + 15.0D);
/*  417:     */           
/*  418: 673 */           current = current.m_parent;
/*  419:     */         }
/*  420: 675 */       } while (current.m_parent != null);
/*  421: 676 */       this.m_nodeModel = new PreConstructedLinearModel(coefficients, intercept);
/*  422: 677 */       this.m_nodeModel.buildClassifier(this.m_instances);
/*  423:     */     }
/*  424: 679 */     if (this.m_left != null) {
/*  425: 680 */       this.m_left.installSmoothedModels();
/*  426:     */     }
/*  427: 682 */     if (this.m_right != null) {
/*  428: 683 */       this.m_right.installSmoothedModels();
/*  429:     */     }
/*  430:     */   }
/*  431:     */   
/*  432:     */   public void prune()
/*  433:     */     throws Exception
/*  434:     */   {
/*  435: 693 */     Evaluation nodeModelEval = null;
/*  436: 695 */     if (this.m_isLeaf)
/*  437:     */     {
/*  438: 696 */       buildLinearModel(this.m_indices);
/*  439: 697 */       nodeModelEval = new Evaluation(this.m_instances);
/*  440:     */       
/*  441:     */ 
/*  442:     */ 
/*  443: 701 */       nodeModelEval.evaluateModel(this.m_nodeModel, this.m_instances, new Object[0]);
/*  444:     */       
/*  445: 703 */       this.m_rootMeanSquaredError = nodeModelEval.rootMeanSquaredError();
/*  446:     */     }
/*  447:     */     else
/*  448:     */     {
/*  449: 707 */       if (this.m_left != null) {
/*  450: 708 */         this.m_left.prune();
/*  451:     */       }
/*  452: 711 */       if (this.m_right != null) {
/*  453: 712 */         this.m_right.prune();
/*  454:     */       }
/*  455: 715 */       buildLinearModel(this.m_indices);
/*  456: 716 */       nodeModelEval = new Evaluation(this.m_instances);
/*  457:     */       
/*  458:     */ 
/*  459:     */ 
/*  460:     */ 
/*  461: 721 */       nodeModelEval.evaluateModel(this.m_nodeModel, this.m_instances, new Object[0]);
/*  462:     */       
/*  463: 723 */       double rmsModel = nodeModelEval.rootMeanSquaredError();
/*  464: 724 */       double adjustedErrorModel = rmsModel * pruningFactor(this.m_numInstances, this.m_nodeModel.numParameters() + 1);
/*  465:     */       
/*  466:     */ 
/*  467:     */ 
/*  468: 728 */       Evaluation nodeEval = new Evaluation(this.m_instances);
/*  469:     */       
/*  470:     */ 
/*  471: 731 */       int l_params = 0;int r_params = 0;
/*  472:     */       
/*  473: 733 */       nodeEval.evaluateModel(this, this.m_instances, new Object[0]);
/*  474:     */       
/*  475: 735 */       double rmsSubTree = nodeEval.rootMeanSquaredError();
/*  476: 737 */       if (this.m_left != null) {
/*  477: 738 */         l_params = this.m_left.numParameters();
/*  478:     */       }
/*  479: 741 */       if (this.m_right != null) {
/*  480: 742 */         r_params = this.m_right.numParameters();
/*  481:     */       }
/*  482: 745 */       double adjustedErrorNode = rmsSubTree * pruningFactor(this.m_numInstances, l_params + r_params + 1);
/*  483: 748 */       if ((adjustedErrorModel <= adjustedErrorNode) || (adjustedErrorModel < this.m_globalDeviation * 1.E-005D))
/*  484:     */       {
/*  485: 752 */         this.m_isLeaf = true;
/*  486: 753 */         this.m_right = null;
/*  487: 754 */         this.m_left = null;
/*  488: 755 */         this.m_numParameters = (this.m_nodeModel.numParameters() + 1);
/*  489: 756 */         this.m_rootMeanSquaredError = rmsModel;
/*  490:     */       }
/*  491:     */       else
/*  492:     */       {
/*  493: 758 */         this.m_numParameters = (l_params + r_params + 1);
/*  494: 759 */         this.m_rootMeanSquaredError = rmsSubTree;
/*  495:     */       }
/*  496:     */     }
/*  497: 763 */     if (!this.m_saveInstances) {
/*  498: 764 */       this.m_instances = new Instances(this.m_instances, 0);
/*  499:     */     }
/*  500:     */   }
/*  501:     */   
/*  502:     */   private double pruningFactor(int num_instances, int num_params)
/*  503:     */   {
/*  504: 776 */     if (num_instances <= num_params) {
/*  505: 777 */       return 10.0D;
/*  506:     */     }
/*  507: 780 */     return (num_instances + 2.0D * num_params) / (num_instances - num_params);
/*  508:     */   }
/*  509:     */   
/*  510:     */   public void findBestLeaf(double[] maxCoverage, RuleNode[] bestLeaf)
/*  511:     */   {
/*  512: 790 */     if (!this.m_isLeaf)
/*  513:     */     {
/*  514: 791 */       if (this.m_left != null) {
/*  515: 792 */         this.m_left.findBestLeaf(maxCoverage, bestLeaf);
/*  516:     */       }
/*  517: 795 */       if (this.m_right != null) {
/*  518: 796 */         this.m_right.findBestLeaf(maxCoverage, bestLeaf);
/*  519:     */       }
/*  520:     */     }
/*  521: 799 */     else if (this.m_numInstances > maxCoverage[0])
/*  522:     */     {
/*  523: 800 */       maxCoverage[0] = this.m_numInstances;
/*  524: 801 */       bestLeaf[0] = this;
/*  525:     */     }
/*  526:     */   }
/*  527:     */   
/*  528:     */   public void returnLeaves(ArrayList<RuleNode>[] v)
/*  529:     */   {
/*  530: 812 */     if (this.m_isLeaf)
/*  531:     */     {
/*  532: 813 */       v[0].add(this);
/*  533:     */     }
/*  534:     */     else
/*  535:     */     {
/*  536: 815 */       if (this.m_left != null) {
/*  537: 816 */         this.m_left.returnLeaves(v);
/*  538:     */       }
/*  539: 819 */       if (this.m_right != null) {
/*  540: 820 */         this.m_right.returnLeaves(v);
/*  541:     */       }
/*  542:     */     }
/*  543:     */   }
/*  544:     */   
/*  545:     */   public RuleNode parentNode()
/*  546:     */   {
/*  547: 831 */     return this.m_parent;
/*  548:     */   }
/*  549:     */   
/*  550:     */   public RuleNode leftNode()
/*  551:     */   {
/*  552: 840 */     return this.m_left;
/*  553:     */   }
/*  554:     */   
/*  555:     */   public RuleNode rightNode()
/*  556:     */   {
/*  557: 849 */     return this.m_right;
/*  558:     */   }
/*  559:     */   
/*  560:     */   public int splitAtt()
/*  561:     */   {
/*  562: 858 */     return this.m_splitAtt;
/*  563:     */   }
/*  564:     */   
/*  565:     */   public double splitVal()
/*  566:     */   {
/*  567: 867 */     return this.m_splitValue;
/*  568:     */   }
/*  569:     */   
/*  570:     */   public int numberOfLinearModels()
/*  571:     */   {
/*  572: 876 */     if (this.m_isLeaf) {
/*  573: 877 */       return 1;
/*  574:     */     }
/*  575: 879 */     return this.m_left.numberOfLinearModels() + this.m_right.numberOfLinearModels();
/*  576:     */   }
/*  577:     */   
/*  578:     */   public boolean isLeaf()
/*  579:     */   {
/*  580: 889 */     return this.m_isLeaf;
/*  581:     */   }
/*  582:     */   
/*  583:     */   protected double rootMeanSquaredError()
/*  584:     */   {
/*  585: 898 */     return this.m_rootMeanSquaredError;
/*  586:     */   }
/*  587:     */   
/*  588:     */   public PreConstructedLinearModel getModel()
/*  589:     */   {
/*  590: 907 */     return this.m_nodeModel;
/*  591:     */   }
/*  592:     */   
/*  593:     */   public int getNumInstances()
/*  594:     */   {
/*  595: 916 */     return this.m_numInstances;
/*  596:     */   }
/*  597:     */   
/*  598:     */   private int numParameters()
/*  599:     */   {
/*  600: 925 */     return this.m_numParameters;
/*  601:     */   }
/*  602:     */   
/*  603:     */   public boolean getRegressionTree()
/*  604:     */   {
/*  605: 935 */     return this.m_regressionTree;
/*  606:     */   }
/*  607:     */   
/*  608:     */   public void setMinNumInstances(double minNum)
/*  609:     */   {
/*  610: 944 */     this.m_splitNum = minNum;
/*  611:     */   }
/*  612:     */   
/*  613:     */   public double getMinNumInstances()
/*  614:     */   {
/*  615: 953 */     return this.m_splitNum;
/*  616:     */   }
/*  617:     */   
/*  618:     */   public void setRegressionTree(boolean newregressionTree)
/*  619:     */   {
/*  620: 963 */     this.m_regressionTree = newregressionTree;
/*  621:     */   }
/*  622:     */   
/*  623:     */   public void printAllModels()
/*  624:     */   {
/*  625: 970 */     if (this.m_isLeaf)
/*  626:     */     {
/*  627: 971 */       System.out.println(this.m_nodeModel.toString());
/*  628:     */     }
/*  629:     */     else
/*  630:     */     {
/*  631: 973 */       System.out.println(this.m_nodeModel.toString());
/*  632: 974 */       this.m_left.printAllModels();
/*  633: 975 */       this.m_right.printAllModels();
/*  634:     */     }
/*  635:     */   }
/*  636:     */   
/*  637:     */   protected int assignIDs(int lastID)
/*  638:     */   {
/*  639: 986 */     int currLastID = lastID + 1;
/*  640: 987 */     this.m_id = currLastID;
/*  641: 989 */     if (this.m_left != null) {
/*  642: 990 */       currLastID = this.m_left.assignIDs(currLastID);
/*  643:     */     }
/*  644: 993 */     if (this.m_right != null) {
/*  645: 994 */       currLastID = this.m_right.assignIDs(currLastID);
/*  646:     */     }
/*  647: 996 */     return currLastID;
/*  648:     */   }
/*  649:     */   
/*  650:     */   public void graph(StringBuffer text)
/*  651:     */   {
/*  652:1006 */     assignIDs(-1);
/*  653:1007 */     graphTree(text);
/*  654:     */   }
/*  655:     */   
/*  656:     */   protected void graphTree(StringBuffer text)
/*  657:     */   {
/*  658:1016 */     text.append("N" + this.m_id + (this.m_isLeaf ? " [label=\"LM " + this.m_leafModelNum : new StringBuilder().append(" [label=\"").append(Utils.backQuoteChars(this.m_instances.attribute(this.m_splitAtt).name())).toString()) + (this.m_isLeaf ? " (" + (this.m_globalDeviation > 0.0D ? this.m_numInstances + "/" + Utils.doubleToString(100.0D * this.m_rootMeanSquaredError / this.m_globalDeviation, 1, 3) + "%)" : new StringBuilder().append(this.m_numInstances).append(")").toString()) + "\" shape=box style=filled " : "\"") + (this.m_saveInstances ? "data=\n" + this.m_instances + "\n,\n" : "") + "]\n");
/*  659:1028 */     if (this.m_left != null)
/*  660:     */     {
/*  661:1029 */       text.append("N" + this.m_id + "->" + "N" + this.m_left.m_id + " [label=\"<=" + Utils.doubleToString(this.m_splitValue, 1, 3) + "\"]\n");
/*  662:     */       
/*  663:1031 */       this.m_left.graphTree(text);
/*  664:     */     }
/*  665:1034 */     if (this.m_right != null)
/*  666:     */     {
/*  667:1035 */       text.append("N" + this.m_id + "->" + "N" + this.m_right.m_id + " [label=\">" + Utils.doubleToString(this.m_splitValue, 1, 3) + "\"]\n");
/*  668:     */       
/*  669:1037 */       this.m_right.graphTree(text);
/*  670:     */     }
/*  671:     */   }
/*  672:     */   
/*  673:     */   protected void setSaveInstances(boolean save)
/*  674:     */   {
/*  675:1048 */     this.m_saveInstances = save;
/*  676:     */   }
/*  677:     */   
/*  678:     */   public String getRevision()
/*  679:     */   {
/*  680:1058 */     return RevisionUtils.extract("$Revision: 10283 $");
/*  681:     */   }
/*  682:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.m5.RuleNode
 * JD-Core Version:    0.7.0.1
 */