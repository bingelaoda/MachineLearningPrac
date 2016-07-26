/*    1:     */ package weka.classifiers.rules;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.util.Arrays;
/*    5:     */ import java.util.BitSet;
/*    6:     */ import java.util.Collections;
/*    7:     */ import java.util.Enumeration;
/*    8:     */ import java.util.Hashtable;
/*    9:     */ import java.util.Random;
/*   10:     */ import java.util.Vector;
/*   11:     */ import weka.attributeSelection.ASEvaluation;
/*   12:     */ import weka.attributeSelection.ASSearch;
/*   13:     */ import weka.attributeSelection.BestFirst;
/*   14:     */ import weka.attributeSelection.SubsetEvaluator;
/*   15:     */ import weka.classifiers.AbstractClassifier;
/*   16:     */ import weka.classifiers.Evaluation;
/*   17:     */ import weka.classifiers.lazy.IBk;
/*   18:     */ import weka.core.AdditionalMeasureProducer;
/*   19:     */ import weka.core.Attribute;
/*   20:     */ import weka.core.Capabilities;
/*   21:     */ import weka.core.Capabilities.Capability;
/*   22:     */ import weka.core.Instance;
/*   23:     */ import weka.core.Instances;
/*   24:     */ import weka.core.Option;
/*   25:     */ import weka.core.OptionHandler;
/*   26:     */ import weka.core.RevisionUtils;
/*   27:     */ import weka.core.SelectedTag;
/*   28:     */ import weka.core.Tag;
/*   29:     */ import weka.core.TechnicalInformation;
/*   30:     */ import weka.core.TechnicalInformation.Field;
/*   31:     */ import weka.core.TechnicalInformation.Type;
/*   32:     */ import weka.core.TechnicalInformationHandler;
/*   33:     */ import weka.core.Utils;
/*   34:     */ import weka.core.WeightedInstancesHandler;
/*   35:     */ import weka.filters.Filter;
/*   36:     */ import weka.filters.unsupervised.attribute.Remove;
/*   37:     */ 
/*   38:     */ public class DecisionTable
/*   39:     */   extends AbstractClassifier
/*   40:     */   implements OptionHandler, WeightedInstancesHandler, AdditionalMeasureProducer, TechnicalInformationHandler
/*   41:     */ {
/*   42:     */   static final long serialVersionUID = 2888557078165701326L;
/*   43:     */   protected Hashtable<DecisionTableHashKey, double[]> m_entries;
/*   44:     */   protected double[] m_classPriorCounts;
/*   45:     */   protected double[] m_classPriors;
/*   46:     */   protected int[] m_decisionFeatures;
/*   47:     */   protected Filter m_disTransform;
/*   48:     */   protected Remove m_delTransform;
/*   49:     */   protected IBk m_ibk;
/*   50:     */   protected Instances m_theInstances;
/*   51:     */   protected Instances m_dtInstances;
/*   52:     */   protected int m_numAttributes;
/*   53:     */   private int m_numInstances;
/*   54:     */   protected boolean m_classIsNominal;
/*   55:     */   protected boolean m_useIBk;
/*   56:     */   protected boolean m_displayRules;
/*   57:     */   private int m_CVFolds;
/*   58:     */   private Random m_rr;
/*   59:     */   protected double m_majority;
/*   60: 208 */   protected ASSearch m_search = new BestFirst();
/*   61:     */   protected ASEvaluation m_evaluator;
/*   62:     */   protected Evaluation m_evaluation;
/*   63:     */   public static final int EVAL_DEFAULT = 1;
/*   64:     */   public static final int EVAL_ACCURACY = 2;
/*   65:     */   public static final int EVAL_RMSE = 3;
/*   66:     */   public static final int EVAL_MAE = 4;
/*   67:     */   public static final int EVAL_AUC = 5;
/*   68: 223 */   public static final Tag[] TAGS_EVALUATION = { new Tag(1, "Default: accuracy (discrete class); RMSE (numeric class)"), new Tag(2, "Accuracy (discrete class only"), new Tag(3, "RMSE (of the class probabilities for discrete class)"), new Tag(4, "MAE (of the class probabilities for discrete class)"), new Tag(5, "AUC (area under the ROC curve - discrete class only)") };
/*   69: 231 */   protected int m_evaluationMeasure = 1;
/*   70:     */   
/*   71:     */   public String globalInfo()
/*   72:     */   {
/*   73: 241 */     return "Class for building and using a simple decision table majority classifier.\n\nFor more information see: \n\n" + getTechnicalInformation().toString();
/*   74:     */   }
/*   75:     */   
/*   76:     */   public TechnicalInformation getTechnicalInformation()
/*   77:     */   {
/*   78: 257 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*   79: 258 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Ron Kohavi");
/*   80: 259 */     result.setValue(TechnicalInformation.Field.TITLE, "The Power of Decision Tables");
/*   81: 260 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "8th European Conference on Machine Learning");
/*   82:     */     
/*   83: 262 */     result.setValue(TechnicalInformation.Field.YEAR, "1995");
/*   84: 263 */     result.setValue(TechnicalInformation.Field.PAGES, "174-189");
/*   85: 264 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Springer");
/*   86:     */     
/*   87: 266 */     return result;
/*   88:     */   }
/*   89:     */   
/*   90:     */   private void insertIntoTable(Instance inst, double[] instA)
/*   91:     */     throws Exception
/*   92:     */   {
/*   93:     */     DecisionTableHashKey thekey;
/*   94:     */     DecisionTableHashKey thekey;
/*   95: 282 */     if (instA != null) {
/*   96: 283 */       thekey = new DecisionTableHashKey(instA);
/*   97:     */     } else {
/*   98: 285 */       thekey = new DecisionTableHashKey(inst, inst.numAttributes(), false);
/*   99:     */     }
/*  100: 289 */     double[] tempClassDist2 = (double[])this.m_entries.get(thekey);
/*  101: 290 */     if (tempClassDist2 == null)
/*  102:     */     {
/*  103: 291 */       if (this.m_classIsNominal)
/*  104:     */       {
/*  105: 292 */         double[] newDist = new double[this.m_theInstances.classAttribute().numValues()];
/*  106: 295 */         for (int i = 0; i < this.m_theInstances.classAttribute().numValues(); i++) {
/*  107: 296 */           newDist[i] = 1.0D;
/*  108:     */         }
/*  109: 299 */         newDist[((int)inst.classValue())] = inst.weight();
/*  110:     */         
/*  111:     */ 
/*  112: 302 */         this.m_entries.put(thekey, newDist);
/*  113:     */       }
/*  114:     */       else
/*  115:     */       {
/*  116: 304 */         double[] newDist = new double[2];
/*  117: 305 */         newDist[0] = (inst.classValue() * inst.weight());
/*  118: 306 */         newDist[1] = inst.weight();
/*  119:     */         
/*  120:     */ 
/*  121: 309 */         this.m_entries.put(thekey, newDist);
/*  122:     */       }
/*  123:     */     }
/*  124: 314 */     else if (this.m_classIsNominal)
/*  125:     */     {
/*  126: 315 */       tempClassDist2[((int)inst.classValue())] += inst.weight();
/*  127:     */       
/*  128:     */ 
/*  129: 318 */       this.m_entries.put(thekey, tempClassDist2);
/*  130:     */     }
/*  131:     */     else
/*  132:     */     {
/*  133: 320 */       tempClassDist2[0] += inst.classValue() * inst.weight();
/*  134: 321 */       tempClassDist2[1] += inst.weight();
/*  135:     */       
/*  136:     */ 
/*  137: 324 */       this.m_entries.put(thekey, tempClassDist2);
/*  138:     */     }
/*  139:     */   }
/*  140:     */   
/*  141:     */   double evaluateInstanceLeaveOneOut(Instance instance, double[] instA)
/*  142:     */     throws Exception
/*  143:     */   {
/*  144: 345 */     DecisionTableHashKey thekey = new DecisionTableHashKey(instA);
/*  145: 346 */     if (this.m_classIsNominal)
/*  146:     */     {
/*  147:     */       double[] tempDist;
/*  148: 349 */       if ((tempDist = (double[])this.m_entries.get(thekey)) == null) {
/*  149: 350 */         throw new Error("This should never happen!");
/*  150:     */       }
/*  151: 352 */       double[] normDist = new double[tempDist.length];
/*  152: 353 */       System.arraycopy(tempDist, 0, normDist, 0, tempDist.length);
/*  153: 354 */       normDist[((int)instance.classValue())] -= instance.weight();
/*  154:     */       
/*  155:     */ 
/*  156:     */ 
/*  157: 358 */       boolean ok = false;
/*  158: 359 */       for (double element : normDist) {
/*  159: 360 */         if (Utils.gr(element, 1.0D))
/*  160:     */         {
/*  161: 361 */           ok = true;
/*  162: 362 */           break;
/*  163:     */         }
/*  164:     */       }
/*  165: 367 */       this.m_classPriorCounts[((int)instance.classValue())] -= instance.weight();
/*  166: 368 */       double[] classPriors = (double[])this.m_classPriorCounts.clone();
/*  167: 369 */       Utils.normalize(classPriors);
/*  168: 370 */       if (!ok) {
/*  169: 371 */         normDist = classPriors;
/*  170:     */       }
/*  171: 374 */       this.m_classPriorCounts[((int)instance.classValue())] += instance.weight();
/*  172:     */       
/*  173:     */ 
/*  174: 377 */       Utils.normalize(normDist);
/*  175: 378 */       if (this.m_evaluationMeasure == 5) {
/*  176: 379 */         this.m_evaluation.evaluateModelOnceAndRecordPrediction(normDist, instance);
/*  177:     */       } else {
/*  178: 381 */         this.m_evaluation.evaluateModelOnce(normDist, instance);
/*  179:     */       }
/*  180: 383 */       return Utils.maxIndex(normDist);
/*  181:     */     }
/*  182:     */     double[] tempDist;
/*  183: 396 */     if ((tempDist = (double[])this.m_entries.get(thekey)) != null)
/*  184:     */     {
/*  185: 397 */       double[] normDist = new double[tempDist.length];
/*  186: 398 */       System.arraycopy(tempDist, 0, normDist, 0, tempDist.length);
/*  187: 399 */       normDist[0] -= instance.classValue() * instance.weight();
/*  188: 400 */       normDist[1] -= instance.weight();
/*  189: 401 */       if (Utils.eq(normDist[1], 0.0D))
/*  190:     */       {
/*  191: 402 */         double[] temp = new double[1];
/*  192: 403 */         temp[0] = this.m_majority;
/*  193: 404 */         this.m_evaluation.evaluateModelOnce(temp, instance);
/*  194: 405 */         return this.m_majority;
/*  195:     */       }
/*  196: 407 */       double[] temp = new double[1];
/*  197: 408 */       normDist[0] /= normDist[1];
/*  198: 409 */       this.m_evaluation.evaluateModelOnce(temp, instance);
/*  199: 410 */       return temp[0];
/*  200:     */     }
/*  201: 413 */     throw new Error("This should never happen!");
/*  202:     */   }
/*  203:     */   
/*  204:     */   double evaluateFoldCV(Instances fold, int[] fs)
/*  205:     */     throws Exception
/*  206:     */   {
/*  207: 433 */     int numFold = fold.numInstances();
/*  208: 434 */     int numCl = this.m_theInstances.classAttribute().numValues();
/*  209: 435 */     double[][] class_distribs = new double[numFold][numCl];
/*  210: 436 */     double[] instA = new double[fs.length];
/*  211:     */     
/*  212:     */ 
/*  213: 439 */     double acc = 0.0D;
/*  214: 440 */     int classI = this.m_theInstances.classIndex();
/*  215:     */     double[] normDist;
/*  216:     */     double[] normDist;
/*  217: 443 */     if (this.m_classIsNominal) {
/*  218: 444 */       normDist = new double[numCl];
/*  219:     */     } else {
/*  220: 446 */       normDist = new double[2];
/*  221:     */     }
/*  222: 450 */     for (int i = 0; i < numFold; i++)
/*  223:     */     {
/*  224: 451 */       Instance inst = fold.instance(i);
/*  225: 452 */       for (int j = 0; j < fs.length; j++) {
/*  226: 453 */         if (fs[j] == classI) {
/*  227: 454 */           instA[j] = 1.7976931348623157E+308D;
/*  228: 455 */         } else if (inst.isMissing(fs[j])) {
/*  229: 456 */           instA[j] = 1.7976931348623157E+308D;
/*  230:     */         } else {
/*  231: 458 */           instA[j] = inst.value(fs[j]);
/*  232:     */         }
/*  233:     */       }
/*  234: 461 */       DecisionTableHashKey thekey = new DecisionTableHashKey(instA);
/*  235: 462 */       if ((class_distribs[i] =  = (double[])this.m_entries.get(thekey)) == null) {
/*  236: 463 */         throw new Error("This should never happen!");
/*  237:     */       }
/*  238: 465 */       if (this.m_classIsNominal)
/*  239:     */       {
/*  240: 466 */         class_distribs[i][((int)inst.classValue())] -= inst.weight();
/*  241:     */       }
/*  242:     */       else
/*  243:     */       {
/*  244: 468 */         class_distribs[i][0] -= inst.classValue() * inst.weight();
/*  245: 469 */         class_distribs[i][1] -= inst.weight();
/*  246:     */       }
/*  247: 472 */       this.m_classPriorCounts[((int)inst.classValue())] -= inst.weight();
/*  248:     */     }
/*  249: 474 */     double[] classPriors = (double[])this.m_classPriorCounts.clone();
/*  250: 475 */     Utils.normalize(classPriors);
/*  251: 478 */     for (i = 0; i < numFold; i++)
/*  252:     */     {
/*  253: 479 */       Instance inst = fold.instance(i);
/*  254: 480 */       System.arraycopy(class_distribs[i], 0, normDist, 0, normDist.length);
/*  255: 481 */       if (this.m_classIsNominal)
/*  256:     */       {
/*  257: 482 */         boolean ok = false;
/*  258: 483 */         for (double element : normDist) {
/*  259: 484 */           if (Utils.gr(element, 1.0D))
/*  260:     */           {
/*  261: 485 */             ok = true;
/*  262: 486 */             break;
/*  263:     */           }
/*  264:     */         }
/*  265: 490 */         if (!ok) {
/*  266: 491 */           normDist = (double[])classPriors.clone();
/*  267:     */         }
/*  268: 495 */         Utils.normalize(normDist);
/*  269: 496 */         if (this.m_evaluationMeasure == 5) {
/*  270: 497 */           this.m_evaluation.evaluateModelOnceAndRecordPrediction(normDist, inst);
/*  271:     */         } else {
/*  272: 499 */           this.m_evaluation.evaluateModelOnce(normDist, inst);
/*  273:     */         }
/*  274:     */       }
/*  275: 508 */       else if (Utils.eq(normDist[1], 0.0D))
/*  276:     */       {
/*  277: 509 */         double[] temp = new double[1];
/*  278: 510 */         temp[0] = this.m_majority;
/*  279: 511 */         this.m_evaluation.evaluateModelOnce(temp, inst);
/*  280:     */       }
/*  281:     */       else
/*  282:     */       {
/*  283: 513 */         double[] temp = new double[1];
/*  284: 514 */         normDist[0] /= normDist[1];
/*  285: 515 */         this.m_evaluation.evaluateModelOnce(temp, inst);
/*  286:     */       }
/*  287:     */     }
/*  288: 521 */     for (i = 0; i < numFold; i++)
/*  289:     */     {
/*  290: 522 */       Instance inst = fold.instance(i);
/*  291:     */       
/*  292: 524 */       this.m_classPriorCounts[((int)inst.classValue())] += inst.weight();
/*  293: 526 */       if (this.m_classIsNominal)
/*  294:     */       {
/*  295: 527 */         class_distribs[i][((int)inst.classValue())] += inst.weight();
/*  296:     */       }
/*  297:     */       else
/*  298:     */       {
/*  299: 529 */         class_distribs[i][0] += inst.classValue() * inst.weight();
/*  300: 530 */         class_distribs[i][1] += inst.weight();
/*  301:     */       }
/*  302:     */     }
/*  303: 533 */     return acc;
/*  304:     */   }
/*  305:     */   
/*  306:     */   protected double estimatePerformance(BitSet feature_set, int num_atts)
/*  307:     */     throws Exception
/*  308:     */   {
/*  309: 547 */     this.m_evaluation = new Evaluation(this.m_theInstances);
/*  310:     */     
/*  311: 549 */     int[] fs = new int[num_atts];
/*  312:     */     
/*  313: 551 */     double[] instA = new double[num_atts];
/*  314: 552 */     int classI = this.m_theInstances.classIndex();
/*  315:     */     
/*  316: 554 */     int index = 0;
/*  317: 555 */     for (int i = 0; i < this.m_numAttributes; i++) {
/*  318: 556 */       if (feature_set.get(i)) {
/*  319: 557 */         fs[(index++)] = i;
/*  320:     */       }
/*  321:     */     }
/*  322: 562 */     this.m_entries = new Hashtable((int)(this.m_theInstances.numInstances() * 1.5D));
/*  323: 566 */     for (i = 0; i < this.m_numInstances; i++)
/*  324:     */     {
/*  325: 568 */       Instance inst = this.m_theInstances.instance(i);
/*  326: 569 */       for (int j = 0; j < fs.length; j++) {
/*  327: 570 */         if (fs[j] == classI) {
/*  328: 571 */           instA[j] = 1.7976931348623157E+308D;
/*  329: 572 */         } else if (inst.isMissing(fs[j])) {
/*  330: 573 */           instA[j] = 1.7976931348623157E+308D;
/*  331:     */         } else {
/*  332: 575 */           instA[j] = inst.value(fs[j]);
/*  333:     */         }
/*  334:     */       }
/*  335: 578 */       insertIntoTable(inst, instA);
/*  336:     */     }
/*  337: 581 */     if (this.m_CVFolds == 1) {
/*  338: 584 */       for (i = 0; i < this.m_numInstances; i++)
/*  339:     */       {
/*  340: 585 */         Instance inst = this.m_theInstances.instance(i);
/*  341: 586 */         for (int j = 0; j < fs.length; j++) {
/*  342: 587 */           if (fs[j] == classI) {
/*  343: 588 */             instA[j] = 1.7976931348623157E+308D;
/*  344: 589 */           } else if (inst.isMissing(fs[j])) {
/*  345: 590 */             instA[j] = 1.7976931348623157E+308D;
/*  346:     */           } else {
/*  347: 592 */             instA[j] = inst.value(fs[j]);
/*  348:     */           }
/*  349:     */         }
/*  350: 595 */         evaluateInstanceLeaveOneOut(inst, instA);
/*  351:     */       }
/*  352:     */     }
/*  353: 598 */     this.m_theInstances.randomize(this.m_rr);
/*  354: 599 */     this.m_theInstances.stratify(this.m_CVFolds);
/*  355: 602 */     for (i = 0; i < this.m_CVFolds; i++)
/*  356:     */     {
/*  357: 603 */       Instances insts = this.m_theInstances.testCV(this.m_CVFolds, i);
/*  358: 604 */       evaluateFoldCV(insts, fs);
/*  359:     */     }
/*  360: 608 */     switch (this.m_evaluationMeasure)
/*  361:     */     {
/*  362:     */     case 1: 
/*  363: 610 */       if (this.m_classIsNominal) {
/*  364: 611 */         return this.m_evaluation.pctCorrect();
/*  365:     */       }
/*  366: 613 */       return -this.m_evaluation.rootMeanSquaredError();
/*  367:     */     case 2: 
/*  368: 615 */       return this.m_evaluation.pctCorrect();
/*  369:     */     case 3: 
/*  370: 617 */       return -this.m_evaluation.rootMeanSquaredError();
/*  371:     */     case 4: 
/*  372: 619 */       return -this.m_evaluation.meanAbsoluteError();
/*  373:     */     case 5: 
/*  374: 621 */       double[] classPriors = this.m_evaluation.getClassPriors();
/*  375: 622 */       Utils.normalize(classPriors);
/*  376: 623 */       double weightedAUC = 0.0D;
/*  377: 624 */       for (i = 0; i < this.m_theInstances.classAttribute().numValues(); i++)
/*  378:     */       {
/*  379: 625 */         double tempAUC = this.m_evaluation.areaUnderROC(i);
/*  380: 626 */         if (!Utils.isMissingValue(tempAUC)) {
/*  381: 627 */           weightedAUC += classPriors[i] * tempAUC;
/*  382:     */         } else {
/*  383: 629 */           System.err.println("Undefined AUC!!");
/*  384:     */         }
/*  385:     */       }
/*  386: 632 */       return weightedAUC;
/*  387:     */     }
/*  388: 635 */     return 0.0D;
/*  389:     */   }
/*  390:     */   
/*  391:     */   protected void resetOptions()
/*  392:     */   {
/*  393: 643 */     this.m_entries = null;
/*  394: 644 */     this.m_decisionFeatures = null;
/*  395: 645 */     this.m_useIBk = false;
/*  396: 646 */     this.m_CVFolds = 1;
/*  397: 647 */     this.m_displayRules = false;
/*  398: 648 */     this.m_evaluationMeasure = 1;
/*  399:     */   }
/*  400:     */   
/*  401:     */   public DecisionTable()
/*  402:     */   {
/*  403: 656 */     resetOptions();
/*  404:     */   }
/*  405:     */   
/*  406:     */   public Enumeration<Option> listOptions()
/*  407:     */   {
/*  408: 667 */     Vector<Option> newVector = new Vector(6);
/*  409:     */     
/*  410: 669 */     newVector.addElement(new Option("\tFull class name of search method, followed\n\tby its options.\n\teg: \"weka.attributeSelection.BestFirst -D 1\"\n\t(default weka.attributeSelection.BestFirst)", "S", 1, "-S <search method specification>"));
/*  411:     */     
/*  412:     */ 
/*  413:     */ 
/*  414:     */ 
/*  415:     */ 
/*  416: 675 */     newVector.addElement(new Option("\tUse cross validation to evaluate features.\n\tUse number of folds = 1 for leave one out CV.\n\t(Default = leave one out CV)", "X", 1, "-X <number of folds>"));
/*  417:     */     
/*  418:     */ 
/*  419:     */ 
/*  420:     */ 
/*  421: 680 */     newVector.addElement(new Option("\tPerformance evaluation measure to use for selecting attributes.\n\t(Default = accuracy for discrete class and rmse for numeric class)", "E", 1, "-E <acc | rmse | mae | auc>"));
/*  422:     */     
/*  423:     */ 
/*  424:     */ 
/*  425:     */ 
/*  426:     */ 
/*  427: 686 */     newVector.addElement(new Option("\tUse nearest neighbour instead of global table majority.", "I", 0, "-I"));
/*  428:     */     
/*  429:     */ 
/*  430:     */ 
/*  431:     */ 
/*  432: 691 */     newVector.addElement(new Option("\tDisplay decision table rules.\n", "R", 0, "-R"));
/*  433:     */     
/*  434:     */ 
/*  435: 694 */     newVector.addAll(Collections.list(super.listOptions()));
/*  436:     */     
/*  437: 696 */     newVector.addElement(new Option("", "", 0, "\nOptions specific to search method " + this.m_search.getClass().getName() + ":"));
/*  438:     */     
/*  439:     */ 
/*  440: 699 */     newVector.addAll(Collections.list(((OptionHandler)this.m_search).listOptions()));
/*  441:     */     
/*  442:     */ 
/*  443: 702 */     return newVector.elements();
/*  444:     */   }
/*  445:     */   
/*  446:     */   public String crossValTipText()
/*  447:     */   {
/*  448: 712 */     return "Sets the number of folds for cross validation (1 = leave one out).";
/*  449:     */   }
/*  450:     */   
/*  451:     */   public void setCrossVal(int folds)
/*  452:     */   {
/*  453: 722 */     this.m_CVFolds = folds;
/*  454:     */   }
/*  455:     */   
/*  456:     */   public int getCrossVal()
/*  457:     */   {
/*  458: 732 */     return this.m_CVFolds;
/*  459:     */   }
/*  460:     */   
/*  461:     */   public String useIBkTipText()
/*  462:     */   {
/*  463: 742 */     return "Sets whether IBk should be used instead of the majority class.";
/*  464:     */   }
/*  465:     */   
/*  466:     */   public void setUseIBk(boolean ibk)
/*  467:     */   {
/*  468: 752 */     this.m_useIBk = ibk;
/*  469:     */   }
/*  470:     */   
/*  471:     */   public boolean getUseIBk()
/*  472:     */   {
/*  473: 762 */     return this.m_useIBk;
/*  474:     */   }
/*  475:     */   
/*  476:     */   public String displayRulesTipText()
/*  477:     */   {
/*  478: 772 */     return "Sets whether rules are to be printed.";
/*  479:     */   }
/*  480:     */   
/*  481:     */   public void setDisplayRules(boolean rules)
/*  482:     */   {
/*  483: 782 */     this.m_displayRules = rules;
/*  484:     */   }
/*  485:     */   
/*  486:     */   public boolean getDisplayRules()
/*  487:     */   {
/*  488: 792 */     return this.m_displayRules;
/*  489:     */   }
/*  490:     */   
/*  491:     */   public String searchTipText()
/*  492:     */   {
/*  493: 802 */     return "The search method used to find good attribute combinations for the decision table.";
/*  494:     */   }
/*  495:     */   
/*  496:     */   public void setSearch(ASSearch search)
/*  497:     */   {
/*  498: 812 */     this.m_search = search;
/*  499:     */   }
/*  500:     */   
/*  501:     */   public ASSearch getSearch()
/*  502:     */   {
/*  503: 821 */     return this.m_search;
/*  504:     */   }
/*  505:     */   
/*  506:     */   public String evaluationMeasureTipText()
/*  507:     */   {
/*  508: 831 */     return "The measure used to evaluate the performance of attribute combinations used in the decision table.";
/*  509:     */   }
/*  510:     */   
/*  511:     */   public SelectedTag getEvaluationMeasure()
/*  512:     */   {
/*  513: 842 */     return new SelectedTag(this.m_evaluationMeasure, TAGS_EVALUATION);
/*  514:     */   }
/*  515:     */   
/*  516:     */   public void setEvaluationMeasure(SelectedTag newMethod)
/*  517:     */   {
/*  518: 852 */     if (newMethod.getTags() == TAGS_EVALUATION) {
/*  519: 853 */       this.m_evaluationMeasure = newMethod.getSelectedTag().getID();
/*  520:     */     }
/*  521:     */   }
/*  522:     */   
/*  523:     */   public void setOptions(String[] options)
/*  524:     */     throws Exception
/*  525:     */   {
/*  526: 933 */     resetOptions();
/*  527:     */     
/*  528: 935 */     super.setOptions(options);
/*  529:     */     
/*  530: 937 */     String optionString = Utils.getOption('X', options);
/*  531: 938 */     if (optionString.length() != 0) {
/*  532: 939 */       this.m_CVFolds = Integer.parseInt(optionString);
/*  533:     */     }
/*  534: 942 */     this.m_useIBk = Utils.getFlag('I', options);
/*  535:     */     
/*  536: 944 */     this.m_displayRules = Utils.getFlag('R', options);
/*  537:     */     
/*  538: 946 */     optionString = Utils.getOption('E', options);
/*  539: 947 */     if (optionString.length() != 0) {
/*  540: 948 */       if (optionString.equals("acc")) {
/*  541: 949 */         setEvaluationMeasure(new SelectedTag(2, TAGS_EVALUATION));
/*  542: 950 */       } else if (optionString.equals("rmse")) {
/*  543: 951 */         setEvaluationMeasure(new SelectedTag(3, TAGS_EVALUATION));
/*  544: 952 */       } else if (optionString.equals("mae")) {
/*  545: 953 */         setEvaluationMeasure(new SelectedTag(4, TAGS_EVALUATION));
/*  546: 954 */       } else if (optionString.equals("auc")) {
/*  547: 955 */         setEvaluationMeasure(new SelectedTag(5, TAGS_EVALUATION));
/*  548:     */       } else {
/*  549: 957 */         throw new IllegalArgumentException("Invalid evaluation measure");
/*  550:     */       }
/*  551:     */     }
/*  552: 961 */     String searchString = Utils.getOption('S', options);
/*  553: 962 */     if (searchString.length() == 0) {
/*  554: 963 */       searchString = BestFirst.class.getName();
/*  555:     */     }
/*  556: 965 */     String[] searchSpec = Utils.splitOptions(searchString);
/*  557: 966 */     if (searchSpec.length == 0) {
/*  558: 967 */       throw new IllegalArgumentException("Invalid search specification string");
/*  559:     */     }
/*  560: 969 */     String searchName = searchSpec[0];
/*  561: 970 */     searchSpec[0] = "";
/*  562: 971 */     setSearch(ASSearch.forName(searchName, searchSpec));
/*  563:     */     
/*  564: 973 */     Utils.checkForRemainingOptions(options);
/*  565:     */   }
/*  566:     */   
/*  567:     */   public String[] getOptions()
/*  568:     */   {
/*  569: 984 */     Vector<String> options = new Vector();
/*  570:     */     
/*  571: 986 */     options.add("-X");
/*  572: 987 */     options.add("" + this.m_CVFolds);
/*  573: 989 */     if (this.m_evaluationMeasure != 1)
/*  574:     */     {
/*  575: 990 */       options.add("-E");
/*  576: 991 */       switch (this.m_evaluationMeasure)
/*  577:     */       {
/*  578:     */       case 2: 
/*  579: 993 */         options.add("acc");
/*  580: 994 */         break;
/*  581:     */       case 3: 
/*  582: 996 */         options.add("rmse");
/*  583: 997 */         break;
/*  584:     */       case 4: 
/*  585: 999 */         options.add("mae");
/*  586:1000 */         break;
/*  587:     */       case 5: 
/*  588:1002 */         options.add("auc");
/*  589:     */       }
/*  590:     */     }
/*  591:1006 */     if (this.m_useIBk) {
/*  592:1007 */       options.add("-I");
/*  593:     */     }
/*  594:1009 */     if (this.m_displayRules) {
/*  595:1010 */       options.add("-R");
/*  596:     */     }
/*  597:1013 */     options.add("-S");
/*  598:1014 */     options.add("" + getSearchSpec());
/*  599:     */     
/*  600:1016 */     Collections.addAll(options, super.getOptions());
/*  601:     */     
/*  602:1018 */     return (String[])options.toArray(new String[0]);
/*  603:     */   }
/*  604:     */   
/*  605:     */   protected String getSearchSpec()
/*  606:     */   {
/*  607:1029 */     ASSearch s = getSearch();
/*  608:1030 */     if ((s instanceof OptionHandler)) {
/*  609:1031 */       return s.getClass().getName() + " " + Utils.joinOptions(((OptionHandler)s).getOptions());
/*  610:     */     }
/*  611:1034 */     return s.getClass().getName();
/*  612:     */   }
/*  613:     */   
/*  614:     */   public Capabilities getCapabilities()
/*  615:     */   {
/*  616:1044 */     Capabilities result = super.getCapabilities();
/*  617:1045 */     result.disableAll();
/*  618:     */     
/*  619:     */ 
/*  620:1048 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  621:1049 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  622:1050 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  623:1051 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  624:     */     
/*  625:     */ 
/*  626:1054 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  627:1055 */     if ((this.m_evaluationMeasure != 2) && (this.m_evaluationMeasure != 5))
/*  628:     */     {
/*  629:1056 */       result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*  630:1057 */       result.enable(Capabilities.Capability.DATE_CLASS);
/*  631:     */     }
/*  632:1060 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  633:     */     
/*  634:1062 */     return result;
/*  635:     */   }
/*  636:     */   
/*  637:     */   private class DummySubsetEvaluator
/*  638:     */     extends ASEvaluation
/*  639:     */     implements SubsetEvaluator
/*  640:     */   {
/*  641:     */     private static final long serialVersionUID = 3927442457704974150L;
/*  642:     */     
/*  643:     */     private DummySubsetEvaluator() {}
/*  644:     */     
/*  645:     */     public void buildEvaluator(Instances data)
/*  646:     */       throws Exception
/*  647:     */     {}
/*  648:     */     
/*  649:     */     public double evaluateSubset(BitSet subset)
/*  650:     */       throws Exception
/*  651:     */     {
/*  652:1077 */       int fc = 0;
/*  653:1078 */       for (int jj = 0; jj < DecisionTable.this.m_numAttributes; jj++) {
/*  654:1079 */         if (subset.get(jj)) {
/*  655:1080 */           fc++;
/*  656:     */         }
/*  657:     */       }
/*  658:1084 */       return DecisionTable.this.estimatePerformance(subset, fc);
/*  659:     */     }
/*  660:     */   }
/*  661:     */   
/*  662:     */   protected void setUpEvaluator()
/*  663:     */     throws Exception
/*  664:     */   {
/*  665:1093 */     this.m_evaluator = new DummySubsetEvaluator(null);
/*  666:     */   }
/*  667:     */   
/*  668:1096 */   protected boolean m_saveMemory = true;
/*  669:     */   
/*  670:     */   public void buildClassifier(Instances data)
/*  671:     */     throws Exception
/*  672:     */   {
/*  673:1108 */     getCapabilities().testWithFail(data);
/*  674:     */     
/*  675:     */ 
/*  676:1111 */     this.m_theInstances = new Instances(data);
/*  677:1112 */     this.m_theInstances.deleteWithMissingClass();
/*  678:     */     
/*  679:1114 */     this.m_rr = new Random(1L);
/*  680:1116 */     if (this.m_theInstances.classAttribute().isNominal())
/*  681:     */     {
/*  682:1117 */       this.m_classPriorCounts = new double[data.classAttribute().numValues()];
/*  683:1118 */       Arrays.fill(this.m_classPriorCounts, 1.0D);
/*  684:1119 */       for (int i = 0; i < data.numInstances(); i++)
/*  685:     */       {
/*  686:1120 */         Instance curr = data.instance(i);
/*  687:1121 */         this.m_classPriorCounts[((int)curr.classValue())] += curr.weight();
/*  688:     */       }
/*  689:1123 */       this.m_classPriors = ((double[])this.m_classPriorCounts.clone());
/*  690:1124 */       Utils.normalize(this.m_classPriors);
/*  691:     */     }
/*  692:1127 */     setUpEvaluator();
/*  693:1129 */     if (this.m_theInstances.classAttribute().isNumeric())
/*  694:     */     {
/*  695:1130 */       this.m_disTransform = new weka.filters.unsupervised.attribute.Discretize();
/*  696:1131 */       this.m_classIsNominal = false;
/*  697:     */       
/*  698:     */ 
/*  699:1134 */       ((weka.filters.unsupervised.attribute.Discretize)this.m_disTransform).setBins(10);
/*  700:     */       
/*  701:1136 */       ((weka.filters.unsupervised.attribute.Discretize)this.m_disTransform).setInvertSelection(true);
/*  702:     */       
/*  703:     */ 
/*  704:     */ 
/*  705:1140 */       String rangeList = "";
/*  706:1141 */       rangeList = rangeList + (this.m_theInstances.classIndex() + 1);
/*  707:     */       
/*  708:     */ 
/*  709:1144 */       ((weka.filters.unsupervised.attribute.Discretize)this.m_disTransform).setAttributeIndices(rangeList);
/*  710:     */     }
/*  711:     */     else
/*  712:     */     {
/*  713:1147 */       this.m_disTransform = new weka.filters.supervised.attribute.Discretize();
/*  714:1148 */       ((weka.filters.supervised.attribute.Discretize)this.m_disTransform).setUseBetterEncoding(true);
/*  715:     */       
/*  716:1150 */       this.m_classIsNominal = true;
/*  717:     */     }
/*  718:1153 */     this.m_disTransform.setInputFormat(this.m_theInstances);
/*  719:1154 */     this.m_theInstances = Filter.useFilter(this.m_theInstances, this.m_disTransform);
/*  720:     */     
/*  721:1156 */     this.m_numAttributes = this.m_theInstances.numAttributes();
/*  722:1157 */     this.m_numInstances = this.m_theInstances.numInstances();
/*  723:1158 */     this.m_majority = this.m_theInstances.meanOrMode(this.m_theInstances.classAttribute());
/*  724:     */     
/*  725:     */ 
/*  726:1161 */     int[] selected = this.m_search.search(this.m_evaluator, this.m_theInstances);
/*  727:     */     
/*  728:1163 */     this.m_decisionFeatures = new int[selected.length + 1];
/*  729:1164 */     System.arraycopy(selected, 0, this.m_decisionFeatures, 0, selected.length);
/*  730:1165 */     this.m_decisionFeatures[(this.m_decisionFeatures.length - 1)] = this.m_theInstances.classIndex();
/*  731:     */     
/*  732:     */ 
/*  733:     */ 
/*  734:1169 */     this.m_delTransform = new Remove();
/*  735:1170 */     this.m_delTransform.setInvertSelection(true);
/*  736:     */     
/*  737:     */ 
/*  738:1173 */     this.m_delTransform.setAttributeIndicesArray(this.m_decisionFeatures);
/*  739:1174 */     this.m_delTransform.setInputFormat(this.m_theInstances);
/*  740:1175 */     this.m_dtInstances = Filter.useFilter(this.m_theInstances, this.m_delTransform);
/*  741:     */     
/*  742:     */ 
/*  743:1178 */     this.m_numAttributes = this.m_dtInstances.numAttributes();
/*  744:     */     
/*  745:     */ 
/*  746:1181 */     this.m_entries = new Hashtable((int)(this.m_dtInstances.numInstances() * 1.5D));
/*  747:1185 */     for (int i = 0; i < this.m_numInstances; i++)
/*  748:     */     {
/*  749:1186 */       Instance inst = this.m_dtInstances.instance(i);
/*  750:1187 */       insertIntoTable(inst, null);
/*  751:     */     }
/*  752:1191 */     if (this.m_useIBk)
/*  753:     */     {
/*  754:1192 */       this.m_ibk = new IBk();
/*  755:1193 */       this.m_ibk.buildClassifier(this.m_dtInstances);
/*  756:     */     }
/*  757:1197 */     if (this.m_saveMemory)
/*  758:     */     {
/*  759:1198 */       this.m_theInstances = new Instances(this.m_theInstances, 0);
/*  760:1199 */       this.m_dtInstances = new Instances(this.m_dtInstances, 0);
/*  761:     */     }
/*  762:1201 */     this.m_evaluation = null;
/*  763:     */   }
/*  764:     */   
/*  765:     */   public double[] distributionForInstance(Instance instance)
/*  766:     */     throws Exception
/*  767:     */   {
/*  768:1218 */     this.m_disTransform.input(instance);
/*  769:1219 */     this.m_disTransform.batchFinished();
/*  770:1220 */     instance = this.m_disTransform.output();
/*  771:     */     
/*  772:1222 */     this.m_delTransform.input(instance);
/*  773:1223 */     this.m_delTransform.batchFinished();
/*  774:1224 */     instance = this.m_delTransform.output();
/*  775:     */     
/*  776:1226 */     DecisionTableHashKey thekey = new DecisionTableHashKey(instance, instance.numAttributes(), false);
/*  777:     */     double[] tempDist;
/*  778:1229 */     if ((tempDist = (double[])this.m_entries.get(thekey)) == null)
/*  779:     */     {
/*  780:1230 */       if (this.m_useIBk)
/*  781:     */       {
/*  782:1231 */         tempDist = this.m_ibk.distributionForInstance(instance);
/*  783:     */       }
/*  784:1233 */       else if (!this.m_classIsNominal)
/*  785:     */       {
/*  786:1234 */         tempDist = new double[1];
/*  787:1235 */         tempDist[0] = this.m_majority;
/*  788:     */       }
/*  789:     */       else
/*  790:     */       {
/*  791:1237 */         tempDist = (double[])this.m_classPriors.clone();
/*  792:     */       }
/*  793:     */     }
/*  794:1246 */     else if (!this.m_classIsNominal)
/*  795:     */     {
/*  796:1247 */       double[] normDist = new double[1];
/*  797:1248 */       tempDist[0] /= tempDist[1];
/*  798:1249 */       tempDist = normDist;
/*  799:     */     }
/*  800:     */     else
/*  801:     */     {
/*  802:1253 */       double[] normDist = new double[tempDist.length];
/*  803:1254 */       System.arraycopy(tempDist, 0, normDist, 0, tempDist.length);
/*  804:1255 */       Utils.normalize(normDist);
/*  805:1256 */       tempDist = normDist;
/*  806:     */     }
/*  807:1259 */     return tempDist;
/*  808:     */   }
/*  809:     */   
/*  810:     */   public String printFeatures()
/*  811:     */   {
/*  812:1270 */     String s = "";
/*  813:1272 */     for (int i = 0; i < this.m_decisionFeatures.length; i++) {
/*  814:1273 */       if (i == 0) {
/*  815:1274 */         s = "" + (this.m_decisionFeatures[i] + 1);
/*  816:     */       } else {
/*  817:1276 */         s = s + "," + (this.m_decisionFeatures[i] + 1);
/*  818:     */       }
/*  819:     */     }
/*  820:1279 */     return s;
/*  821:     */   }
/*  822:     */   
/*  823:     */   public double measureNumRules()
/*  824:     */   {
/*  825:1288 */     return this.m_entries.size();
/*  826:     */   }
/*  827:     */   
/*  828:     */   public Enumeration<String> enumerateMeasures()
/*  829:     */   {
/*  830:1298 */     Vector<String> newVector = new Vector(1);
/*  831:1299 */     newVector.addElement("measureNumRules");
/*  832:1300 */     return newVector.elements();
/*  833:     */   }
/*  834:     */   
/*  835:     */   public double getMeasure(String additionalMeasureName)
/*  836:     */   {
/*  837:1312 */     if (additionalMeasureName.compareToIgnoreCase("measureNumRules") == 0) {
/*  838:1313 */       return measureNumRules();
/*  839:     */     }
/*  840:1315 */     throw new IllegalArgumentException(additionalMeasureName + " not supported (DecisionTable)");
/*  841:     */   }
/*  842:     */   
/*  843:     */   public String toString()
/*  844:     */   {
/*  845:1328 */     if (this.m_entries == null) {
/*  846:1329 */       return "Decision Table: No model built yet.";
/*  847:     */     }
/*  848:1331 */     StringBuffer text = new StringBuffer();
/*  849:     */     
/*  850:1333 */     text.append("Decision Table:\n\nNumber of training instances: " + this.m_numInstances + "\nNumber of Rules : " + this.m_entries.size() + "\n");
/*  851:1336 */     if (this.m_useIBk) {
/*  852:1337 */       text.append("Non matches covered by IB1.\n");
/*  853:     */     } else {
/*  854:1339 */       text.append("Non matches covered by Majority class.\n");
/*  855:     */     }
/*  856:1342 */     text.append(this.m_search.toString());
/*  857:     */     
/*  858:     */ 
/*  859:     */ 
/*  860:     */ 
/*  861:     */ 
/*  862:1348 */     text.append("Evaluation (for feature selection): CV ");
/*  863:1349 */     if (this.m_CVFolds > 1) {
/*  864:1350 */       text.append("(" + this.m_CVFolds + " fold) ");
/*  865:     */     } else {
/*  866:1352 */       text.append("(leave one out) ");
/*  867:     */     }
/*  868:1354 */     text.append("\nFeature set: " + printFeatures());
/*  869:1356 */     if (this.m_displayRules)
/*  870:     */     {
/*  871:1359 */       int maxColWidth = 0;
/*  872:1360 */       for (int i = 0; i < this.m_dtInstances.numAttributes(); i++)
/*  873:     */       {
/*  874:1361 */         if (this.m_dtInstances.attribute(i).name().length() > maxColWidth) {
/*  875:1362 */           maxColWidth = this.m_dtInstances.attribute(i).name().length();
/*  876:     */         }
/*  877:1365 */         if ((this.m_classIsNominal) || (i != this.m_dtInstances.classIndex()))
/*  878:     */         {
/*  879:1366 */           Enumeration<Object> e = this.m_dtInstances.attribute(i).enumerateValues();
/*  880:1368 */           while (e.hasMoreElements())
/*  881:     */           {
/*  882:1369 */             String ss = (String)e.nextElement();
/*  883:1370 */             if (ss.length() > maxColWidth) {
/*  884:1371 */               maxColWidth = ss.length();
/*  885:     */             }
/*  886:     */           }
/*  887:     */         }
/*  888:     */       }
/*  889:1377 */       text.append("\n\nRules:\n");
/*  890:1378 */       StringBuffer tm = new StringBuffer();
/*  891:1379 */       for (int i = 0; i < this.m_dtInstances.numAttributes(); i++) {
/*  892:1380 */         if (this.m_dtInstances.classIndex() != i)
/*  893:     */         {
/*  894:1381 */           int d = maxColWidth - this.m_dtInstances.attribute(i).name().length();
/*  895:1382 */           tm.append(this.m_dtInstances.attribute(i).name());
/*  896:1383 */           for (int j = 0; j < d + 1; j++) {
/*  897:1384 */             tm.append(" ");
/*  898:     */           }
/*  899:     */         }
/*  900:     */       }
/*  901:1388 */       tm.append(this.m_dtInstances.attribute(this.m_dtInstances.classIndex()).name() + "  ");
/*  902:1391 */       for (int i = 0; i < tm.length() + 10; i++) {
/*  903:1392 */         text.append("=");
/*  904:     */       }
/*  905:1394 */       text.append("\n");
/*  906:1395 */       text.append(tm);
/*  907:1396 */       text.append("\n");
/*  908:1397 */       for (int i = 0; i < tm.length() + 10; i++) {
/*  909:1398 */         text.append("=");
/*  910:     */       }
/*  911:1400 */       text.append("\n");
/*  912:     */       
/*  913:1402 */       Enumeration<DecisionTableHashKey> e = this.m_entries.keys();
/*  914:1403 */       while (e.hasMoreElements())
/*  915:     */       {
/*  916:1404 */         DecisionTableHashKey tt = (DecisionTableHashKey)e.nextElement();
/*  917:1405 */         text.append(tt.toString(this.m_dtInstances, maxColWidth));
/*  918:1406 */         double[] ClassDist = (double[])this.m_entries.get(tt);
/*  919:1408 */         if (this.m_classIsNominal)
/*  920:     */         {
/*  921:1409 */           int m = Utils.maxIndex(ClassDist);
/*  922:     */           try
/*  923:     */           {
/*  924:1411 */             text.append(this.m_dtInstances.classAttribute().value(m) + "\n");
/*  925:     */           }
/*  926:     */           catch (Exception ee)
/*  927:     */           {
/*  928:1413 */             System.out.println(ee.getMessage());
/*  929:     */           }
/*  930:     */         }
/*  931:     */         else
/*  932:     */         {
/*  933:1416 */           text.append(ClassDist[0] / ClassDist[1] + "\n");
/*  934:     */         }
/*  935:     */       }
/*  936:1420 */       for (int i = 0; i < tm.length() + 10; i++) {
/*  937:1421 */         text.append("=");
/*  938:     */       }
/*  939:1423 */       text.append("\n");
/*  940:1424 */       text.append("\n");
/*  941:     */     }
/*  942:1426 */     return text.toString();
/*  943:     */   }
/*  944:     */   
/*  945:     */   public String getRevision()
/*  946:     */   {
/*  947:1437 */     return RevisionUtils.extract("$Revision: 12088 $");
/*  948:     */   }
/*  949:     */   
/*  950:     */   public static void main(String[] argv)
/*  951:     */   {
/*  952:1446 */     runClassifier(new DecisionTable(), argv);
/*  953:     */   }
/*  954:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.rules.DecisionTable
 * JD-Core Version:    0.7.0.1
 */