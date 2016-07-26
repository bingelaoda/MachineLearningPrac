/*    1:     */ package weka.classifiers.mi;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.io.Serializable;
/*    5:     */ import java.util.ArrayList;
/*    6:     */ import java.util.Collections;
/*    7:     */ import java.util.Enumeration;
/*    8:     */ import java.util.Random;
/*    9:     */ import java.util.Vector;
/*   10:     */ import weka.classifiers.AbstractClassifier;
/*   11:     */ import weka.classifiers.functions.Logistic;
/*   12:     */ import weka.classifiers.functions.supportVector.Kernel;
/*   13:     */ import weka.classifiers.functions.supportVector.SMOset;
/*   14:     */ import weka.classifiers.mi.supportVector.MIPolyKernel;
/*   15:     */ import weka.core.Attribute;
/*   16:     */ import weka.core.Capabilities;
/*   17:     */ import weka.core.Capabilities.Capability;
/*   18:     */ import weka.core.DenseInstance;
/*   19:     */ import weka.core.Instance;
/*   20:     */ import weka.core.Instances;
/*   21:     */ import weka.core.MultiInstanceCapabilitiesHandler;
/*   22:     */ import weka.core.Option;
/*   23:     */ import weka.core.OptionHandler;
/*   24:     */ import weka.core.RevisionHandler;
/*   25:     */ import weka.core.RevisionUtils;
/*   26:     */ import weka.core.SelectedTag;
/*   27:     */ import weka.core.SerializedObject;
/*   28:     */ import weka.core.Tag;
/*   29:     */ import weka.core.TechnicalInformation;
/*   30:     */ import weka.core.TechnicalInformation.Field;
/*   31:     */ import weka.core.TechnicalInformation.Type;
/*   32:     */ import weka.core.TechnicalInformationHandler;
/*   33:     */ import weka.core.Utils;
/*   34:     */ import weka.core.WeightedInstancesHandler;
/*   35:     */ import weka.filters.Filter;
/*   36:     */ import weka.filters.unsupervised.attribute.MultiInstanceToPropositional;
/*   37:     */ import weka.filters.unsupervised.attribute.NominalToBinary;
/*   38:     */ import weka.filters.unsupervised.attribute.Normalize;
/*   39:     */ import weka.filters.unsupervised.attribute.PropositionalToMultiInstance;
/*   40:     */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*   41:     */ import weka.filters.unsupervised.attribute.Standardize;
/*   42:     */ 
/*   43:     */ public class MISMO
/*   44:     */   extends AbstractClassifier
/*   45:     */   implements WeightedInstancesHandler, MultiInstanceCapabilitiesHandler, TechnicalInformationHandler
/*   46:     */ {
/*   47:     */   static final long serialVersionUID = -5834036950143719712L;
/*   48:     */   public static final int FILTER_NORMALIZE = 0;
/*   49:     */   public static final int FILTER_STANDARDIZE = 1;
/*   50:     */   public static final int FILTER_NONE = 2;
/*   51:     */   
/*   52:     */   public String globalInfo()
/*   53:     */   {
/*   54: 235 */     return "Implements John Platt's sequential minimal optimization algorithm for training a support vector classifier.\n\nThis implementation globally replaces all missing values and transforms nominal attributes into binary ones. It also normalizes all attributes by default. (In that case the coefficients in the output are based on the normalized data, not the original data --- this is important for interpreting the classifier.)\n\nMulti-class problems are solved using pairwise classification.\n\nTo obtain proper probability estimates, use the option that fits logistic regression models to the outputs of the support vector machine. In the multi-class case the predicted probabilities are coupled using Hastie and Tibshirani's pairwise coupling method.\n\nNote: for improved speed normalization should be turned off when operating on SparseInstances.\n\nFor more information on the SMO algorithm, see\n\n" + getTechnicalInformation().toString();
/*   55:     */   }
/*   56:     */   
/*   57:     */   public TechnicalInformation getTechnicalInformation()
/*   58:     */   {
/*   59: 266 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INCOLLECTION);
/*   60: 267 */     result.setValue(TechnicalInformation.Field.AUTHOR, "J. Platt");
/*   61: 268 */     result.setValue(TechnicalInformation.Field.YEAR, "1998");
/*   62: 269 */     result.setValue(TechnicalInformation.Field.TITLE, "Machines using Sequential Minimal Optimization");
/*   63:     */     
/*   64: 271 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Advances in Kernel Methods - Support Vector Learning");
/*   65:     */     
/*   66: 273 */     result.setValue(TechnicalInformation.Field.EDITOR, "B. Schoelkopf and C. Burges and A. Smola");
/*   67: 274 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "MIT Press");
/*   68:     */     
/*   69: 276 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.ARTICLE);
/*   70: 277 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "S.S. Keerthi and S.K. Shevade and C. Bhattacharyya and K.R.K. Murthy");
/*   71:     */     
/*   72: 279 */     additional.setValue(TechnicalInformation.Field.YEAR, "2001");
/*   73: 280 */     additional.setValue(TechnicalInformation.Field.TITLE, "Improvements to Platt's SMO Algorithm for SVM Classifier Design");
/*   74:     */     
/*   75: 282 */     additional.setValue(TechnicalInformation.Field.JOURNAL, "Neural Computation");
/*   76: 283 */     additional.setValue(TechnicalInformation.Field.VOLUME, "13");
/*   77: 284 */     additional.setValue(TechnicalInformation.Field.NUMBER, "3");
/*   78: 285 */     additional.setValue(TechnicalInformation.Field.PAGES, "637-649");
/*   79:     */     
/*   80: 287 */     return result;
/*   81:     */   }
/*   82:     */   
/*   83:     */   protected class BinaryMISMO
/*   84:     */     implements Serializable, RevisionHandler
/*   85:     */   {
/*   86:     */     static final long serialVersionUID = -7107082483475433531L;
/*   87:     */     protected double[] m_alpha;
/*   88:     */     protected double m_b;
/*   89:     */     protected double m_bLow;
/*   90:     */     protected double m_bUp;
/*   91:     */     protected int m_iLow;
/*   92:     */     protected int m_iUp;
/*   93:     */     protected Instances m_data;
/*   94:     */     protected double[] m_weights;
/*   95:     */     protected double[] m_sparseWeights;
/*   96:     */     protected int[] m_sparseIndices;
/*   97:     */     protected Kernel m_kernel;
/*   98:     */     protected double[] m_class;
/*   99:     */     protected double[] m_errors;
/*  100:     */     protected SMOset m_I0;
/*  101:     */     protected SMOset m_I1;
/*  102:     */     protected SMOset m_I2;
/*  103:     */     protected SMOset m_I3;
/*  104:     */     protected SMOset m_I4;
/*  105:     */     protected SMOset m_supportVectors;
/*  106: 345 */     protected Logistic m_logistic = null;
/*  107: 348 */     protected double m_sumOfWeights = 0.0D;
/*  108:     */     
/*  109:     */     protected BinaryMISMO() {}
/*  110:     */     
/*  111:     */     protected void fitLogistic(Instances insts, int cl1, int cl2, int numFolds, Random random)
/*  112:     */       throws Exception
/*  113:     */     {
/*  114: 365 */       ArrayList<Attribute> atts = new ArrayList(2);
/*  115: 366 */       atts.add(new Attribute("pred"));
/*  116: 367 */       ArrayList<String> attVals = new ArrayList(2);
/*  117: 368 */       attVals.add(insts.classAttribute().value(cl1));
/*  118: 369 */       attVals.add(insts.classAttribute().value(cl2));
/*  119: 370 */       atts.add(new Attribute("class", attVals));
/*  120: 371 */       Instances data = new Instances("data", atts, insts.numInstances());
/*  121: 372 */       data.setClassIndex(1);
/*  122: 375 */       if (numFolds <= 0)
/*  123:     */       {
/*  124: 378 */         for (int j = 0; j < insts.numInstances(); j++)
/*  125:     */         {
/*  126: 379 */           Instance inst = insts.instance(j);
/*  127: 380 */           double[] vals = new double[2];
/*  128: 381 */           vals[0] = SVMOutput(-1, inst);
/*  129: 382 */           if (inst.classValue() == cl2) {
/*  130: 383 */             vals[1] = 1.0D;
/*  131:     */           }
/*  132: 385 */           data.add(new DenseInstance(inst.weight(), vals));
/*  133:     */         }
/*  134:     */       }
/*  135:     */       else
/*  136:     */       {
/*  137: 390 */         if (numFolds > insts.numInstances()) {
/*  138: 391 */           numFolds = insts.numInstances();
/*  139:     */         }
/*  140: 395 */         insts = new Instances(insts);
/*  141:     */         
/*  142:     */ 
/*  143:     */ 
/*  144: 399 */         insts.randomize(random);
/*  145: 400 */         insts.stratify(numFolds);
/*  146: 401 */         for (int i = 0; i < numFolds; i++)
/*  147:     */         {
/*  148: 402 */           Instances train = insts.trainCV(numFolds, i, random);
/*  149: 403 */           SerializedObject so = new SerializedObject(this);
/*  150: 404 */           BinaryMISMO smo = (BinaryMISMO)so.getObject();
/*  151: 405 */           smo.buildClassifier(train, cl1, cl2, false, -1, -1);
/*  152: 406 */           Instances test = insts.testCV(numFolds, i);
/*  153: 407 */           for (int j = 0; j < test.numInstances(); j++)
/*  154:     */           {
/*  155: 408 */             double[] vals = new double[2];
/*  156: 409 */             vals[0] = smo.SVMOutput(-1, test.instance(j));
/*  157: 410 */             if (test.instance(j).classValue() == cl2) {
/*  158: 411 */               vals[1] = 1.0D;
/*  159:     */             }
/*  160: 413 */             data.add(new DenseInstance(test.instance(j).weight(), vals));
/*  161:     */           }
/*  162:     */         }
/*  163:     */       }
/*  164: 419 */       this.m_logistic = new Logistic();
/*  165: 420 */       this.m_logistic.buildClassifier(data);
/*  166:     */     }
/*  167:     */     
/*  168:     */     public void setKernel(Kernel value)
/*  169:     */     {
/*  170: 429 */       this.m_kernel = value;
/*  171:     */     }
/*  172:     */     
/*  173:     */     public Kernel getKernel()
/*  174:     */     {
/*  175: 438 */       return this.m_kernel;
/*  176:     */     }
/*  177:     */     
/*  178:     */     protected void buildClassifier(Instances insts, int cl1, int cl2, boolean fitLogistic, int numFolds, int randomSeed)
/*  179:     */       throws Exception
/*  180:     */     {
/*  181: 457 */       this.m_bUp = -1.0D;
/*  182: 458 */       this.m_bLow = 1.0D;
/*  183: 459 */       this.m_b = 0.0D;
/*  184: 460 */       this.m_alpha = null;
/*  185: 461 */       this.m_data = null;
/*  186: 462 */       this.m_weights = null;
/*  187: 463 */       this.m_errors = null;
/*  188: 464 */       this.m_logistic = null;
/*  189: 465 */       this.m_I0 = null;
/*  190: 466 */       this.m_I1 = null;
/*  191: 467 */       this.m_I2 = null;
/*  192: 468 */       this.m_I3 = null;
/*  193: 469 */       this.m_I4 = null;
/*  194: 470 */       this.m_sparseWeights = null;
/*  195: 471 */       this.m_sparseIndices = null;
/*  196:     */       
/*  197:     */ 
/*  198: 474 */       this.m_sumOfWeights = insts.sumOfWeights();
/*  199:     */       
/*  200:     */ 
/*  201: 477 */       this.m_class = new double[insts.numInstances()];
/*  202: 478 */       this.m_iUp = -1;
/*  203: 479 */       this.m_iLow = -1;
/*  204: 480 */       for (int i = 0; i < this.m_class.length; i++) {
/*  205: 481 */         if ((int)insts.instance(i).classValue() == cl1)
/*  206:     */         {
/*  207: 482 */           this.m_class[i] = -1.0D;
/*  208: 483 */           this.m_iLow = i;
/*  209:     */         }
/*  210: 484 */         else if ((int)insts.instance(i).classValue() == cl2)
/*  211:     */         {
/*  212: 485 */           this.m_class[i] = 1.0D;
/*  213: 486 */           this.m_iUp = i;
/*  214:     */         }
/*  215:     */         else
/*  216:     */         {
/*  217: 488 */           throw new Exception("This should never happen!");
/*  218:     */         }
/*  219:     */       }
/*  220: 493 */       if ((this.m_iUp == -1) || (this.m_iLow == -1))
/*  221:     */       {
/*  222: 494 */         if (this.m_iUp != -1)
/*  223:     */         {
/*  224: 495 */           this.m_b = -1.0D;
/*  225:     */         }
/*  226: 496 */         else if (this.m_iLow != -1)
/*  227:     */         {
/*  228: 497 */           this.m_b = 1.0D;
/*  229:     */         }
/*  230:     */         else
/*  231:     */         {
/*  232: 499 */           this.m_class = null;
/*  233: 500 */           return;
/*  234:     */         }
/*  235: 502 */         this.m_supportVectors = new SMOset(0);
/*  236: 503 */         this.m_alpha = new double[0];
/*  237: 504 */         this.m_class = new double[0];
/*  238: 507 */         if (fitLogistic) {
/*  239: 508 */           fitLogistic(insts, cl1, cl2, numFolds, new Random(randomSeed));
/*  240:     */         }
/*  241: 510 */         return;
/*  242:     */       }
/*  243: 514 */       this.m_data = insts;
/*  244: 515 */       this.m_weights = null;
/*  245:     */       
/*  246:     */ 
/*  247: 518 */       this.m_alpha = new double[this.m_data.numInstances()];
/*  248:     */       
/*  249:     */ 
/*  250: 521 */       this.m_supportVectors = new SMOset(this.m_data.numInstances());
/*  251: 522 */       this.m_I0 = new SMOset(this.m_data.numInstances());
/*  252: 523 */       this.m_I1 = new SMOset(this.m_data.numInstances());
/*  253: 524 */       this.m_I2 = new SMOset(this.m_data.numInstances());
/*  254: 525 */       this.m_I3 = new SMOset(this.m_data.numInstances());
/*  255: 526 */       this.m_I4 = new SMOset(this.m_data.numInstances());
/*  256:     */       
/*  257:     */ 
/*  258: 529 */       this.m_sparseWeights = null;
/*  259: 530 */       this.m_sparseIndices = null;
/*  260:     */       
/*  261:     */ 
/*  262: 533 */       this.m_errors = new double[this.m_data.numInstances()];
/*  263: 534 */       this.m_errors[this.m_iLow] = 1.0D;
/*  264: 535 */       this.m_errors[this.m_iUp] = -1.0D;
/*  265:     */       
/*  266:     */ 
/*  267: 538 */       this.m_kernel.buildKernel(this.m_data);
/*  268: 541 */       for (int i = 0; i < this.m_class.length; i++) {
/*  269: 542 */         if (this.m_class[i] == 1.0D) {
/*  270: 543 */           this.m_I1.insert(i);
/*  271:     */         } else {
/*  272: 545 */           this.m_I4.insert(i);
/*  273:     */         }
/*  274:     */       }
/*  275: 550 */       int numChanged = 0;
/*  276: 551 */       boolean examineAll = true;
/*  277: 552 */       while ((numChanged > 0) || (examineAll))
/*  278:     */       {
/*  279: 553 */         numChanged = 0;
/*  280: 554 */         if (examineAll) {
/*  281: 555 */           for (int i = 0; i < this.m_alpha.length; i++) {
/*  282: 556 */             if (examineExample(i)) {
/*  283: 557 */               numChanged++;
/*  284:     */             }
/*  285:     */           }
/*  286:     */         } else {
/*  287: 563 */           for (int i = 0; i < this.m_alpha.length; i++) {
/*  288: 564 */             if ((this.m_alpha[i] > 0.0D) && (this.m_alpha[i] < MISMO.this.m_C * this.m_data.instance(i).weight()))
/*  289:     */             {
/*  290: 566 */               if (examineExample(i)) {
/*  291: 567 */                 numChanged++;
/*  292:     */               }
/*  293: 571 */               if (this.m_bUp > this.m_bLow - 2.0D * MISMO.this.m_tol)
/*  294:     */               {
/*  295: 572 */                 numChanged = 0;
/*  296: 573 */                 break;
/*  297:     */               }
/*  298:     */             }
/*  299:     */           }
/*  300:     */         }
/*  301: 586 */         if (examineAll) {
/*  302: 587 */           examineAll = false;
/*  303: 588 */         } else if (numChanged == 0) {
/*  304: 589 */           examineAll = true;
/*  305:     */         }
/*  306:     */       }
/*  307: 594 */       this.m_b = ((this.m_bLow + this.m_bUp) / 2.0D);
/*  308:     */       
/*  309:     */ 
/*  310: 597 */       this.m_kernel.clean();
/*  311:     */       
/*  312: 599 */       this.m_errors = null;
/*  313: 600 */       this.m_I0 = (this.m_I1 = this.m_I2 = this.m_I3 = this.m_I4 = null);
/*  314: 603 */       if (fitLogistic) {
/*  315: 604 */         fitLogistic(insts, cl1, cl2, numFolds, new Random(randomSeed));
/*  316:     */       }
/*  317:     */     }
/*  318:     */     
/*  319:     */     protected double SVMOutput(int index, Instance inst)
/*  320:     */       throws Exception
/*  321:     */     {
/*  322: 619 */       double result = 0.0D;
/*  323: 621 */       for (int i = this.m_supportVectors.getNext(-1); i != -1; i = this.m_supportVectors.getNext(i)) {
/*  324: 623 */         result += this.m_class[i] * this.m_alpha[i] * this.m_kernel.eval(index, i, inst);
/*  325:     */       }
/*  326: 625 */       result -= this.m_b;
/*  327:     */       
/*  328: 627 */       return result;
/*  329:     */     }
/*  330:     */     
/*  331:     */     public String toString()
/*  332:     */     {
/*  333: 638 */       StringBuffer text = new StringBuffer();
/*  334: 639 */       int printed = 0;
/*  335: 641 */       if ((this.m_alpha == null) && (this.m_sparseWeights == null)) {
/*  336: 642 */         return "BinaryMISMO: No model built yet.\n";
/*  337:     */       }
/*  338:     */       try
/*  339:     */       {
/*  340: 645 */         text.append("BinaryMISMO\n\n");
/*  341: 647 */         for (int i = 0; i < this.m_alpha.length; i++) {
/*  342: 648 */           if (this.m_supportVectors.contains(i))
/*  343:     */           {
/*  344: 649 */             double val = this.m_alpha[i];
/*  345: 650 */             if (this.m_class[i] == 1.0D)
/*  346:     */             {
/*  347: 651 */               if (printed > 0) {
/*  348: 652 */                 text.append(" + ");
/*  349:     */               }
/*  350:     */             }
/*  351:     */             else {
/*  352: 655 */               text.append(" - ");
/*  353:     */             }
/*  354: 657 */             text.append(Utils.doubleToString(val, 12, 4) + " * <");
/*  355: 658 */             for (int j = 0; j < this.m_data.numAttributes(); j++)
/*  356:     */             {
/*  357: 659 */               if (j != this.m_data.classIndex()) {
/*  358: 660 */                 text.append(this.m_data.instance(i).toString(j));
/*  359:     */               }
/*  360: 662 */               if (j != this.m_data.numAttributes() - 1) {
/*  361: 663 */                 text.append(" ");
/*  362:     */               }
/*  363:     */             }
/*  364: 666 */             text.append("> * X]\n");
/*  365: 667 */             printed++;
/*  366:     */           }
/*  367:     */         }
/*  368: 671 */         if (this.m_b > 0.0D) {
/*  369: 672 */           text.append(" - " + Utils.doubleToString(this.m_b, 12, 4));
/*  370:     */         } else {
/*  371: 674 */           text.append(" + " + Utils.doubleToString(-this.m_b, 12, 4));
/*  372:     */         }
/*  373: 677 */         text.append("\n\nNumber of support vectors: " + this.m_supportVectors.numElements());
/*  374:     */         
/*  375: 679 */         int numEval = 0;
/*  376: 680 */         int numCacheHits = -1;
/*  377: 681 */         if (this.m_kernel != null)
/*  378:     */         {
/*  379: 682 */           numEval = this.m_kernel.numEvals();
/*  380: 683 */           numCacheHits = this.m_kernel.numCacheHits();
/*  381:     */         }
/*  382: 685 */         text.append("\n\nNumber of kernel evaluations: " + numEval);
/*  383: 686 */         if ((numCacheHits >= 0) && (numEval > 0))
/*  384:     */         {
/*  385: 687 */           double hitRatio = 1.0D - numEval * 1.0D / (numCacheHits + numEval);
/*  386: 688 */           text.append(" (" + Utils.doubleToString(hitRatio * 100.0D, 7, 3).trim() + "% cached)");
/*  387:     */         }
/*  388:     */       }
/*  389:     */       catch (Exception e)
/*  390:     */       {
/*  391: 693 */         e.printStackTrace();
/*  392:     */         
/*  393: 695 */         return "Can't print BinaryMISMO classifier.";
/*  394:     */       }
/*  395: 698 */       return text.toString();
/*  396:     */     }
/*  397:     */     
/*  398:     */     protected boolean examineExample(int i2)
/*  399:     */       throws Exception
/*  400:     */     {
/*  401: 711 */       int i1 = -1;
/*  402:     */       
/*  403: 713 */       double y2 = this.m_class[i2];
/*  404:     */       double F2;
/*  405:     */       double F2;
/*  406: 714 */       if (this.m_I0.contains(i2))
/*  407:     */       {
/*  408: 715 */         F2 = this.m_errors[i2];
/*  409:     */       }
/*  410:     */       else
/*  411:     */       {
/*  412: 717 */         F2 = SVMOutput(i2, this.m_data.instance(i2)) + this.m_b - y2;
/*  413: 718 */         this.m_errors[i2] = F2;
/*  414: 721 */         if (((this.m_I1.contains(i2)) || (this.m_I2.contains(i2))) && (F2 < this.m_bUp))
/*  415:     */         {
/*  416: 722 */           this.m_bUp = F2;
/*  417: 723 */           this.m_iUp = i2;
/*  418:     */         }
/*  419: 724 */         else if (((this.m_I3.contains(i2)) || (this.m_I4.contains(i2))) && (F2 > this.m_bLow))
/*  420:     */         {
/*  421: 725 */           this.m_bLow = F2;
/*  422: 726 */           this.m_iLow = i2;
/*  423:     */         }
/*  424:     */       }
/*  425: 733 */       boolean optimal = true;
/*  426: 734 */       if (((this.m_I0.contains(i2)) || (this.m_I1.contains(i2)) || (this.m_I2.contains(i2))) && 
/*  427: 735 */         (this.m_bLow - F2 > 2.0D * MISMO.this.m_tol))
/*  428:     */       {
/*  429: 736 */         optimal = false;
/*  430: 737 */         i1 = this.m_iLow;
/*  431:     */       }
/*  432: 740 */       if (((this.m_I0.contains(i2)) || (this.m_I3.contains(i2)) || (this.m_I4.contains(i2))) && 
/*  433: 741 */         (F2 - this.m_bUp > 2.0D * MISMO.this.m_tol))
/*  434:     */       {
/*  435: 742 */         optimal = false;
/*  436: 743 */         i1 = this.m_iUp;
/*  437:     */       }
/*  438: 746 */       if (optimal) {
/*  439: 747 */         return false;
/*  440:     */       }
/*  441: 751 */       if (this.m_I0.contains(i2)) {
/*  442: 752 */         if (this.m_bLow - F2 > F2 - this.m_bUp) {
/*  443: 753 */           i1 = this.m_iLow;
/*  444:     */         } else {
/*  445: 755 */           i1 = this.m_iUp;
/*  446:     */         }
/*  447:     */       }
/*  448: 758 */       if (i1 == -1) {
/*  449: 759 */         throw new Exception("This should never happen!");
/*  450:     */       }
/*  451: 761 */       return takeStep(i1, i2, F2);
/*  452:     */     }
/*  453:     */     
/*  454:     */     protected boolean takeStep(int i1, int i2, double F2)
/*  455:     */       throws Exception
/*  456:     */     {
/*  457: 776 */       double C1 = MISMO.this.m_C * this.m_data.instance(i1).weight();
/*  458: 777 */       double C2 = MISMO.this.m_C * this.m_data.instance(i2).weight();
/*  459: 780 */       if (i1 == i2) {
/*  460: 781 */         return false;
/*  461:     */       }
/*  462: 785 */       double alph1 = this.m_alpha[i1];
/*  463: 786 */       double alph2 = this.m_alpha[i2];
/*  464: 787 */       double y1 = this.m_class[i1];
/*  465: 788 */       double y2 = this.m_class[i2];
/*  466: 789 */       double F1 = this.m_errors[i1];
/*  467: 790 */       double s = y1 * y2;
/*  468:     */       double H;
/*  469:     */       double L;
/*  470:     */       double H;
/*  471: 793 */       if (y1 != y2)
/*  472:     */       {
/*  473: 794 */         double L = Math.max(0.0D, alph2 - alph1);
/*  474: 795 */         H = Math.min(C2, C1 + alph2 - alph1);
/*  475:     */       }
/*  476:     */       else
/*  477:     */       {
/*  478: 797 */         L = Math.max(0.0D, alph1 + alph2 - C1);
/*  479: 798 */         H = Math.min(C2, alph1 + alph2);
/*  480:     */       }
/*  481: 800 */       if (L >= H) {
/*  482: 801 */         return false;
/*  483:     */       }
/*  484: 805 */       double k11 = this.m_kernel.eval(i1, i1, this.m_data.instance(i1));
/*  485: 806 */       double k12 = this.m_kernel.eval(i1, i2, this.m_data.instance(i1));
/*  486: 807 */       double k22 = this.m_kernel.eval(i2, i2, this.m_data.instance(i2));
/*  487: 808 */       double eta = 2.0D * k12 - k11 - k22;
/*  488:     */       double a2;
/*  489: 811 */       if (eta < 0.0D)
/*  490:     */       {
/*  491: 814 */         double a2 = alph2 - y2 * (F1 - F2) / eta;
/*  492: 817 */         if (a2 < L) {
/*  493: 818 */           a2 = L;
/*  494: 819 */         } else if (a2 > H) {
/*  495: 820 */           a2 = H;
/*  496:     */         }
/*  497:     */       }
/*  498:     */       else
/*  499:     */       {
/*  500: 825 */         double f1 = SVMOutput(i1, this.m_data.instance(i1));
/*  501: 826 */         double f2 = SVMOutput(i2, this.m_data.instance(i2));
/*  502: 827 */         double v1 = f1 + this.m_b - y1 * alph1 * k11 - y2 * alph2 * k12;
/*  503: 828 */         double v2 = f2 + this.m_b - y1 * alph1 * k12 - y2 * alph2 * k22;
/*  504: 829 */         double gamma = alph1 + s * alph2;
/*  505: 830 */         double Lobj = gamma - s * L + L - 0.5D * k11 * (gamma - s * L) * (gamma - s * L) - 0.5D * k22 * L * L - s * k12 * (gamma - s * L) * L - y1 * (gamma - s * L) * v1 - y2 * L * v2;
/*  506:     */         
/*  507:     */ 
/*  508: 833 */         double Hobj = gamma - s * H + H - 0.5D * k11 * (gamma - s * H) * (gamma - s * H) - 0.5D * k22 * H * H - s * k12 * (gamma - s * H) * H - y1 * (gamma - s * H) * v1 - y2 * H * v2;
/*  509:     */         double a2;
/*  510: 836 */         if (Lobj > Hobj + MISMO.this.m_eps)
/*  511:     */         {
/*  512: 837 */           a2 = L;
/*  513:     */         }
/*  514:     */         else
/*  515:     */         {
/*  516:     */           double a2;
/*  517: 838 */           if (Lobj < Hobj - MISMO.this.m_eps) {
/*  518: 839 */             a2 = H;
/*  519:     */           } else {
/*  520: 841 */             a2 = alph2;
/*  521:     */           }
/*  522:     */         }
/*  523:     */       }
/*  524: 844 */       if (Math.abs(a2 - alph2) < MISMO.this.m_eps * (a2 + alph2 + MISMO.this.m_eps)) {
/*  525: 845 */         return false;
/*  526:     */       }
/*  527: 849 */       if (a2 > C2 - MISMO.m_Del * C2) {
/*  528: 850 */         a2 = C2;
/*  529: 851 */       } else if (a2 <= MISMO.m_Del * C2) {
/*  530: 852 */         a2 = 0.0D;
/*  531:     */       }
/*  532: 856 */       double a1 = alph1 + s * (alph2 - a2);
/*  533: 859 */       if (a1 > C1 - MISMO.m_Del * C1) {
/*  534: 860 */         a1 = C1;
/*  535: 861 */       } else if (a1 <= MISMO.m_Del * C1) {
/*  536: 862 */         a1 = 0.0D;
/*  537:     */       }
/*  538: 866 */       if (a1 > 0.0D) {
/*  539: 867 */         this.m_supportVectors.insert(i1);
/*  540:     */       } else {
/*  541: 869 */         this.m_supportVectors.delete(i1);
/*  542:     */       }
/*  543: 871 */       if ((a1 > 0.0D) && (a1 < C1)) {
/*  544: 872 */         this.m_I0.insert(i1);
/*  545:     */       } else {
/*  546: 874 */         this.m_I0.delete(i1);
/*  547:     */       }
/*  548: 876 */       if ((y1 == 1.0D) && (a1 == 0.0D)) {
/*  549: 877 */         this.m_I1.insert(i1);
/*  550:     */       } else {
/*  551: 879 */         this.m_I1.delete(i1);
/*  552:     */       }
/*  553: 881 */       if ((y1 == -1.0D) && (a1 == C1)) {
/*  554: 882 */         this.m_I2.insert(i1);
/*  555:     */       } else {
/*  556: 884 */         this.m_I2.delete(i1);
/*  557:     */       }
/*  558: 886 */       if ((y1 == 1.0D) && (a1 == C1)) {
/*  559: 887 */         this.m_I3.insert(i1);
/*  560:     */       } else {
/*  561: 889 */         this.m_I3.delete(i1);
/*  562:     */       }
/*  563: 891 */       if ((y1 == -1.0D) && (a1 == 0.0D)) {
/*  564: 892 */         this.m_I4.insert(i1);
/*  565:     */       } else {
/*  566: 894 */         this.m_I4.delete(i1);
/*  567:     */       }
/*  568: 896 */       if (a2 > 0.0D) {
/*  569: 897 */         this.m_supportVectors.insert(i2);
/*  570:     */       } else {
/*  571: 899 */         this.m_supportVectors.delete(i2);
/*  572:     */       }
/*  573: 901 */       if ((a2 > 0.0D) && (a2 < C2)) {
/*  574: 902 */         this.m_I0.insert(i2);
/*  575:     */       } else {
/*  576: 904 */         this.m_I0.delete(i2);
/*  577:     */       }
/*  578: 906 */       if ((y2 == 1.0D) && (a2 == 0.0D)) {
/*  579: 907 */         this.m_I1.insert(i2);
/*  580:     */       } else {
/*  581: 909 */         this.m_I1.delete(i2);
/*  582:     */       }
/*  583: 911 */       if ((y2 == -1.0D) && (a2 == C2)) {
/*  584: 912 */         this.m_I2.insert(i2);
/*  585:     */       } else {
/*  586: 914 */         this.m_I2.delete(i2);
/*  587:     */       }
/*  588: 916 */       if ((y2 == 1.0D) && (a2 == C2)) {
/*  589: 917 */         this.m_I3.insert(i2);
/*  590:     */       } else {
/*  591: 919 */         this.m_I3.delete(i2);
/*  592:     */       }
/*  593: 921 */       if ((y2 == -1.0D) && (a2 == 0.0D)) {
/*  594: 922 */         this.m_I4.insert(i2);
/*  595:     */       } else {
/*  596: 924 */         this.m_I4.delete(i2);
/*  597:     */       }
/*  598: 928 */       for (int j = this.m_I0.getNext(-1); j != -1; j = this.m_I0.getNext(j)) {
/*  599: 929 */         if ((j != i1) && (j != i2)) {
/*  600: 930 */           this.m_errors[j] += y1 * (a1 - alph1) * this.m_kernel.eval(i1, j, this.m_data.instance(i1)) + y2 * (a2 - alph2) * this.m_kernel.eval(i2, j, this.m_data.instance(i2));
/*  601:     */         }
/*  602:     */       }
/*  603: 937 */       this.m_errors[i1] += y1 * (a1 - alph1) * k11 + y2 * (a2 - alph2) * k12;
/*  604: 938 */       this.m_errors[i2] += y1 * (a1 - alph1) * k12 + y2 * (a2 - alph2) * k22;
/*  605:     */       
/*  606:     */ 
/*  607: 941 */       this.m_alpha[i1] = a1;
/*  608: 942 */       this.m_alpha[i2] = a2;
/*  609:     */       
/*  610:     */ 
/*  611: 945 */       this.m_bLow = -1.797693134862316E+308D;
/*  612: 946 */       this.m_bUp = 1.7976931348623157E+308D;
/*  613: 947 */       this.m_iLow = -1;
/*  614: 948 */       this.m_iUp = -1;
/*  615: 949 */       for (int j = this.m_I0.getNext(-1); j != -1; j = this.m_I0.getNext(j))
/*  616:     */       {
/*  617: 950 */         if (this.m_errors[j] < this.m_bUp)
/*  618:     */         {
/*  619: 951 */           this.m_bUp = this.m_errors[j];
/*  620: 952 */           this.m_iUp = j;
/*  621:     */         }
/*  622: 954 */         if (this.m_errors[j] > this.m_bLow)
/*  623:     */         {
/*  624: 955 */           this.m_bLow = this.m_errors[j];
/*  625: 956 */           this.m_iLow = j;
/*  626:     */         }
/*  627:     */       }
/*  628: 959 */       if (!this.m_I0.contains(i1)) {
/*  629: 960 */         if ((this.m_I3.contains(i1)) || (this.m_I4.contains(i1)))
/*  630:     */         {
/*  631: 961 */           if (this.m_errors[i1] > this.m_bLow)
/*  632:     */           {
/*  633: 962 */             this.m_bLow = this.m_errors[i1];
/*  634: 963 */             this.m_iLow = i1;
/*  635:     */           }
/*  636:     */         }
/*  637: 966 */         else if (this.m_errors[i1] < this.m_bUp)
/*  638:     */         {
/*  639: 967 */           this.m_bUp = this.m_errors[i1];
/*  640: 968 */           this.m_iUp = i1;
/*  641:     */         }
/*  642:     */       }
/*  643: 972 */       if (!this.m_I0.contains(i2)) {
/*  644: 973 */         if ((this.m_I3.contains(i2)) || (this.m_I4.contains(i2)))
/*  645:     */         {
/*  646: 974 */           if (this.m_errors[i2] > this.m_bLow)
/*  647:     */           {
/*  648: 975 */             this.m_bLow = this.m_errors[i2];
/*  649: 976 */             this.m_iLow = i2;
/*  650:     */           }
/*  651:     */         }
/*  652: 979 */         else if (this.m_errors[i2] < this.m_bUp)
/*  653:     */         {
/*  654: 980 */           this.m_bUp = this.m_errors[i2];
/*  655: 981 */           this.m_iUp = i2;
/*  656:     */         }
/*  657:     */       }
/*  658: 985 */       if ((this.m_iLow == -1) || (this.m_iUp == -1)) {
/*  659: 986 */         throw new Exception("This should never happen!");
/*  660:     */       }
/*  661: 990 */       return true;
/*  662:     */     }
/*  663:     */     
/*  664:     */     protected void checkClassifier()
/*  665:     */       throws Exception
/*  666:     */     {
/*  667:1001 */       double sum = 0.0D;
/*  668:1002 */       for (int i = 0; i < this.m_alpha.length; i++) {
/*  669:1003 */         if (this.m_alpha[i] > 0.0D) {
/*  670:1004 */           sum += this.m_class[i] * this.m_alpha[i];
/*  671:     */         }
/*  672:     */       }
/*  673:1007 */       System.err.println("Sum of y(i) * alpha(i): " + sum);
/*  674:1009 */       for (int i = 0; i < this.m_alpha.length; i++)
/*  675:     */       {
/*  676:1010 */         double output = SVMOutput(i, this.m_data.instance(i));
/*  677:1011 */         if ((Utils.eq(this.m_alpha[i], 0.0D)) && 
/*  678:1012 */           (Utils.sm(this.m_class[i] * output, 1.0D))) {
/*  679:1013 */           System.err.println("KKT condition 1 violated: " + this.m_class[i] * output);
/*  680:     */         }
/*  681:1017 */         if ((Utils.gr(this.m_alpha[i], 0.0D)) && (Utils.sm(this.m_alpha[i], MISMO.this.m_C * this.m_data.instance(i).weight()))) {
/*  682:1019 */           if (!Utils.eq(this.m_class[i] * output, 1.0D)) {
/*  683:1020 */             System.err.println("KKT condition 2 violated: " + this.m_class[i] * output);
/*  684:     */           }
/*  685:     */         }
/*  686:1024 */         if ((Utils.eq(this.m_alpha[i], MISMO.this.m_C * this.m_data.instance(i).weight())) && 
/*  687:1025 */           (Utils.gr(this.m_class[i] * output, 1.0D))) {
/*  688:1026 */           System.err.println("KKT condition 3 violated: " + this.m_class[i] * output);
/*  689:     */         }
/*  690:     */       }
/*  691:     */     }
/*  692:     */     
/*  693:     */     public String getRevision()
/*  694:     */     {
/*  695:1040 */       return RevisionUtils.extract("$Revision: 12560 $");
/*  696:     */     }
/*  697:     */   }
/*  698:     */   
/*  699:1051 */   public static final Tag[] TAGS_FILTER = { new Tag(0, "Normalize training data"), new Tag(1, "Standardize training data"), new Tag(2, "No normalization/standardization") };
/*  700:1057 */   protected BinaryMISMO[][] m_classifiers = (BinaryMISMO[][])null;
/*  701:1060 */   protected double m_C = 1.0D;
/*  702:1063 */   protected double m_eps = 1.0E-012D;
/*  703:1066 */   protected double m_tol = 0.001D;
/*  704:1069 */   protected int m_filterType = 0;
/*  705:1072 */   protected boolean m_minimax = false;
/*  706:     */   protected NominalToBinary m_NominalToBinary;
/*  707:1078 */   protected Filter m_Filter = null;
/*  708:     */   protected ReplaceMissingValues m_Missing;
/*  709:1084 */   protected int m_classIndex = -1;
/*  710:     */   protected Attribute m_classAttribute;
/*  711:1090 */   protected Kernel m_kernel = new MIPolyKernel();
/*  712:     */   protected boolean m_checksTurnedOff;
/*  713:1102 */   protected static double m_Del = 4.940656458412465E-321D;
/*  714:1105 */   protected boolean m_fitLogisticModels = false;
/*  715:1108 */   protected int m_numFolds = -1;
/*  716:1111 */   protected int m_randomSeed = 1;
/*  717:     */   
/*  718:     */   public void turnChecksOff()
/*  719:     */   {
/*  720:1118 */     this.m_checksTurnedOff = true;
/*  721:     */   }
/*  722:     */   
/*  723:     */   public void turnChecksOn()
/*  724:     */   {
/*  725:1126 */     this.m_checksTurnedOff = false;
/*  726:     */   }
/*  727:     */   
/*  728:     */   public Capabilities getCapabilities()
/*  729:     */   {
/*  730:1136 */     Capabilities result = getKernel().getCapabilities();
/*  731:1137 */     result.setOwner(this);
/*  732:     */     
/*  733:     */ 
/*  734:1140 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  735:1141 */     result.enable(Capabilities.Capability.RELATIONAL_ATTRIBUTES);
/*  736:     */     
/*  737:     */ 
/*  738:1144 */     result.disableAllClasses();
/*  739:1145 */     result.disableAllClassDependencies();
/*  740:1146 */     result.disable(Capabilities.Capability.NO_CLASS);
/*  741:1147 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  742:1148 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  743:     */     
/*  744:     */ 
/*  745:1151 */     result.enable(Capabilities.Capability.ONLY_MULTIINSTANCE);
/*  746:     */     
/*  747:1153 */     return result;
/*  748:     */   }
/*  749:     */   
/*  750:     */   public Capabilities getMultiInstanceCapabilities()
/*  751:     */   {
/*  752:1165 */     Capabilities result = ((MultiInstanceCapabilitiesHandler)getKernel()).getMultiInstanceCapabilities();
/*  753:     */     
/*  754:1167 */     result.setOwner(this);
/*  755:     */     
/*  756:     */ 
/*  757:1170 */     result.enableAllAttributeDependencies();
/*  758:1173 */     if (result.handles(Capabilities.Capability.NUMERIC_ATTRIBUTES)) {
/*  759:1174 */       result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  760:     */     }
/*  761:1176 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  762:     */     
/*  763:1178 */     return result;
/*  764:     */   }
/*  765:     */   
/*  766:     */   public void buildClassifier(Instances insts)
/*  767:     */     throws Exception
/*  768:     */   {
/*  769:1190 */     if (!this.m_checksTurnedOff)
/*  770:     */     {
/*  771:1192 */       getCapabilities().testWithFail(insts);
/*  772:     */       
/*  773:     */ 
/*  774:1195 */       insts = new Instances(insts);
/*  775:1196 */       insts.deleteWithMissingClass();
/*  776:     */       
/*  777:     */ 
/*  778:     */ 
/*  779:     */ 
/*  780:     */ 
/*  781:     */ 
/*  782:1203 */       Instances data = new Instances(insts, insts.numInstances());
/*  783:1204 */       for (int i = 0; i < insts.numInstances(); i++) {
/*  784:1205 */         if (insts.instance(i).weight() > 0.0D) {
/*  785:1206 */           data.add(insts.instance(i));
/*  786:     */         }
/*  787:     */       }
/*  788:1209 */       if (data.numInstances() == 0) {
/*  789:1210 */         throw new Exception("No training instances left after removing instance with either a weight null or a missing class!");
/*  790:     */       }
/*  791:1213 */       insts = data;
/*  792:     */     }
/*  793:1217 */     if (!this.m_checksTurnedOff) {
/*  794:1218 */       this.m_Missing = new ReplaceMissingValues();
/*  795:     */     } else {
/*  796:1220 */       this.m_Missing = null;
/*  797:     */     }
/*  798:1223 */     if (getCapabilities().handles(Capabilities.Capability.NUMERIC_ATTRIBUTES))
/*  799:     */     {
/*  800:1224 */       boolean onlyNumeric = true;
/*  801:1225 */       if (!this.m_checksTurnedOff) {
/*  802:1226 */         for (int i = 0; i < insts.numAttributes(); i++) {
/*  803:1227 */           if ((i != insts.classIndex()) && 
/*  804:1228 */             (!insts.attribute(i).isNumeric()))
/*  805:     */           {
/*  806:1229 */             onlyNumeric = false;
/*  807:1230 */             break;
/*  808:     */           }
/*  809:     */         }
/*  810:     */       }
/*  811:1236 */       if (!onlyNumeric)
/*  812:     */       {
/*  813:1237 */         this.m_NominalToBinary = new NominalToBinary();
/*  814:     */         
/*  815:1239 */         this.m_NominalToBinary.setAttributeIndices("2-last");
/*  816:     */       }
/*  817:     */       else
/*  818:     */       {
/*  819:1241 */         this.m_NominalToBinary = null;
/*  820:     */       }
/*  821:     */     }
/*  822:     */     else
/*  823:     */     {
/*  824:1244 */       this.m_NominalToBinary = null;
/*  825:     */     }
/*  826:1247 */     if (this.m_filterType == 1) {
/*  827:1248 */       this.m_Filter = new Standardize();
/*  828:1249 */     } else if (this.m_filterType == 0) {
/*  829:1250 */       this.m_Filter = new Normalize();
/*  830:     */     } else {
/*  831:1252 */       this.m_Filter = null;
/*  832:     */     }
/*  833:1256 */     Filter convertToProp = new MultiInstanceToPropositional();
/*  834:1257 */     Filter convertToMI = new PropositionalToMultiInstance();
/*  835:     */     Instances transformedInsts;
/*  836:     */     Instances transformedInsts;
/*  837:1260 */     if (this.m_minimax)
/*  838:     */     {
/*  839:1265 */       SimpleMI transMinimax = new SimpleMI();
/*  840:1266 */       transMinimax.setTransformMethod(new SelectedTag(3, SimpleMI.TAGS_TRANSFORMMETHOD));
/*  841:     */       
/*  842:1268 */       transformedInsts = transMinimax.transform(insts);
/*  843:     */     }
/*  844:     */     else
/*  845:     */     {
/*  846:1270 */       convertToProp.setInputFormat(insts);
/*  847:1271 */       transformedInsts = Filter.useFilter(insts, convertToProp);
/*  848:     */     }
/*  849:1274 */     if (this.m_Missing != null)
/*  850:     */     {
/*  851:1275 */       this.m_Missing.setInputFormat(transformedInsts);
/*  852:1276 */       transformedInsts = Filter.useFilter(transformedInsts, this.m_Missing);
/*  853:     */     }
/*  854:1279 */     if (this.m_NominalToBinary != null)
/*  855:     */     {
/*  856:1280 */       this.m_NominalToBinary.setInputFormat(transformedInsts);
/*  857:1281 */       transformedInsts = Filter.useFilter(transformedInsts, this.m_NominalToBinary);
/*  858:     */     }
/*  859:1284 */     if (this.m_Filter != null)
/*  860:     */     {
/*  861:1285 */       this.m_Filter.setInputFormat(transformedInsts);
/*  862:1286 */       transformedInsts = Filter.useFilter(transformedInsts, this.m_Filter);
/*  863:     */     }
/*  864:1290 */     convertToMI.setInputFormat(transformedInsts);
/*  865:1291 */     insts = Filter.useFilter(transformedInsts, convertToMI);
/*  866:     */     
/*  867:1293 */     this.m_classIndex = insts.classIndex();
/*  868:1294 */     this.m_classAttribute = insts.classAttribute();
/*  869:     */     
/*  870:     */ 
/*  871:1297 */     Instances[] subsets = new Instances[insts.numClasses()];
/*  872:1298 */     for (int i = 0; i < insts.numClasses(); i++) {
/*  873:1299 */       subsets[i] = new Instances(insts, insts.numInstances());
/*  874:     */     }
/*  875:1301 */     for (int j = 0; j < insts.numInstances(); j++)
/*  876:     */     {
/*  877:1302 */       Instance inst = insts.instance(j);
/*  878:1303 */       subsets[((int)inst.classValue())].add(inst);
/*  879:     */     }
/*  880:1305 */     for (int i = 0; i < insts.numClasses(); i++) {
/*  881:1306 */       subsets[i].compactify();
/*  882:     */     }
/*  883:1310 */     Random rand = new Random(this.m_randomSeed);
/*  884:1311 */     this.m_classifiers = new BinaryMISMO[insts.numClasses()][insts.numClasses()];
/*  885:1312 */     for (int i = 0; i < insts.numClasses(); i++) {
/*  886:1313 */       for (int j = i + 1; j < insts.numClasses(); j++)
/*  887:     */       {
/*  888:1314 */         this.m_classifiers[i][j] = new BinaryMISMO();
/*  889:1315 */         this.m_classifiers[i][j].setKernel(Kernel.makeCopy(getKernel()));
/*  890:1316 */         Instances data = new Instances(insts, insts.numInstances());
/*  891:1317 */         for (int k = 0; k < subsets[i].numInstances(); k++) {
/*  892:1318 */           data.add(subsets[i].instance(k));
/*  893:     */         }
/*  894:1320 */         for (int k = 0; k < subsets[j].numInstances(); k++) {
/*  895:1321 */           data.add(subsets[j].instance(k));
/*  896:     */         }
/*  897:1323 */         data.compactify();
/*  898:1324 */         data.randomize(rand);
/*  899:1325 */         this.m_classifiers[i][j].buildClassifier(data, i, j, this.m_fitLogisticModels, this.m_numFolds, this.m_randomSeed);
/*  900:     */       }
/*  901:     */     }
/*  902:     */   }
/*  903:     */   
/*  904:     */   public double[] distributionForInstance(Instance inst)
/*  905:     */     throws Exception
/*  906:     */   {
/*  907:1343 */     Instances insts = new Instances(inst.dataset(), 0);
/*  908:1344 */     insts.add(inst);
/*  909:     */     
/*  910:     */ 
/*  911:1347 */     Filter convertToProp = new MultiInstanceToPropositional();
/*  912:1348 */     Filter convertToMI = new PropositionalToMultiInstance();
/*  913:1350 */     if (this.m_minimax)
/*  914:     */     {
/*  915:1351 */       SimpleMI transMinimax = new SimpleMI();
/*  916:1352 */       transMinimax.setTransformMethod(new SelectedTag(3, SimpleMI.TAGS_TRANSFORMMETHOD));
/*  917:     */       
/*  918:1354 */       insts = transMinimax.transform(insts);
/*  919:     */     }
/*  920:     */     else
/*  921:     */     {
/*  922:1356 */       convertToProp.setInputFormat(insts);
/*  923:1357 */       insts = Filter.useFilter(insts, convertToProp);
/*  924:     */     }
/*  925:1361 */     if (this.m_Missing != null) {
/*  926:1362 */       insts = Filter.useFilter(insts, this.m_Missing);
/*  927:     */     }
/*  928:1365 */     if (this.m_NominalToBinary != null) {
/*  929:1366 */       insts = Filter.useFilter(insts, this.m_NominalToBinary);
/*  930:     */     }
/*  931:1369 */     if (this.m_Filter != null) {
/*  932:1370 */       insts = Filter.useFilter(insts, this.m_Filter);
/*  933:     */     }
/*  934:1374 */     convertToMI.setInputFormat(insts);
/*  935:1375 */     insts = Filter.useFilter(insts, convertToMI);
/*  936:     */     
/*  937:1377 */     inst = insts.instance(0);
/*  938:1379 */     if (!this.m_fitLogisticModels)
/*  939:     */     {
/*  940:1380 */       double[] result = new double[inst.numClasses()];
/*  941:1381 */       for (int i = 0; i < inst.numClasses(); i++) {
/*  942:1382 */         for (int j = i + 1; j < inst.numClasses(); j++) {
/*  943:1383 */           if ((this.m_classifiers[i][j].m_alpha != null) || (this.m_classifiers[i][j].m_sparseWeights != null))
/*  944:     */           {
/*  945:1385 */             double output = this.m_classifiers[i][j].SVMOutput(-1, inst);
/*  946:1386 */             if (output > 0.0D) {
/*  947:1387 */               result[j] += 1.0D;
/*  948:     */             } else {
/*  949:1389 */               result[i] += 1.0D;
/*  950:     */             }
/*  951:     */           }
/*  952:     */         }
/*  953:     */       }
/*  954:1394 */       Utils.normalize(result);
/*  955:1395 */       return result;
/*  956:     */     }
/*  957:1400 */     if (inst.numClasses() == 2)
/*  958:     */     {
/*  959:1401 */       double[] newInst = new double[2];
/*  960:1402 */       newInst[0] = this.m_classifiers[0][1].SVMOutput(-1, inst);
/*  961:1403 */       newInst[1] = Utils.missingValue();
/*  962:1404 */       return this.m_classifiers[0][1].m_logistic.distributionForInstance(new DenseInstance(1.0D, newInst));
/*  963:     */     }
/*  964:1407 */     double[][] r = new double[inst.numClasses()][inst.numClasses()];
/*  965:1408 */     double[][] n = new double[inst.numClasses()][inst.numClasses()];
/*  966:1409 */     for (int i = 0; i < inst.numClasses(); i++) {
/*  967:1410 */       for (int j = i + 1; j < inst.numClasses(); j++) {
/*  968:1411 */         if ((this.m_classifiers[i][j].m_alpha != null) || (this.m_classifiers[i][j].m_sparseWeights != null))
/*  969:     */         {
/*  970:1413 */           double[] newInst = new double[2];
/*  971:1414 */           newInst[0] = this.m_classifiers[i][j].SVMOutput(-1, inst);
/*  972:1415 */           newInst[1] = Utils.missingValue();
/*  973:1416 */           r[i][j] = this.m_classifiers[i][j].m_logistic.distributionForInstance(new DenseInstance(1.0D, newInst))[0];
/*  974:     */           
/*  975:1418 */           n[i][j] = this.m_classifiers[i][j].m_sumOfWeights;
/*  976:     */         }
/*  977:     */       }
/*  978:     */     }
/*  979:1422 */     return pairwiseCoupling(n, r);
/*  980:     */   }
/*  981:     */   
/*  982:     */   public double[] pairwiseCoupling(double[][] n, double[][] r)
/*  983:     */   {
/*  984:1436 */     double[] p = new double[r.length];
/*  985:1437 */     for (int i = 0; i < p.length; i++) {
/*  986:1438 */       p[i] = (1.0D / p.length);
/*  987:     */     }
/*  988:1440 */     double[][] u = new double[r.length][r.length];
/*  989:1441 */     for (int i = 0; i < r.length; i++) {
/*  990:1442 */       for (int j = i + 1; j < r.length; j++) {
/*  991:1443 */         u[i][j] = 0.5D;
/*  992:     */       }
/*  993:     */     }
/*  994:1448 */     double[] firstSum = new double[p.length];
/*  995:1449 */     for (int i = 0; i < p.length; i++) {
/*  996:1450 */       for (int j = i + 1; j < p.length; j++)
/*  997:     */       {
/*  998:1451 */         firstSum[i] += n[i][j] * r[i][j];
/*  999:1452 */         firstSum[j] += n[i][j] * (1.0D - r[i][j]);
/* 1000:     */       }
/* 1001:     */     }
/* 1002:     */     boolean changed;
/* 1003:     */     do
/* 1004:     */     {
/* 1005:1459 */       changed = false;
/* 1006:1460 */       double[] secondSum = new double[p.length];
/* 1007:1461 */       for (int i = 0; i < p.length; i++) {
/* 1008:1462 */         for (int j = i + 1; j < p.length; j++)
/* 1009:     */         {
/* 1010:1463 */           secondSum[i] += n[i][j] * u[i][j];
/* 1011:1464 */           secondSum[j] += n[i][j] * (1.0D - u[i][j]);
/* 1012:     */         }
/* 1013:     */       }
/* 1014:1467 */       for (int i = 0; i < p.length; i++) {
/* 1015:1468 */         if ((firstSum[i] == 0.0D) || (secondSum[i] == 0.0D))
/* 1016:     */         {
/* 1017:1469 */           if (p[i] > 0.0D) {
/* 1018:1470 */             changed = true;
/* 1019:     */           }
/* 1020:1472 */           p[i] = 0.0D;
/* 1021:     */         }
/* 1022:     */         else
/* 1023:     */         {
/* 1024:1474 */           double factor = firstSum[i] / secondSum[i];
/* 1025:1475 */           double pOld = p[i];
/* 1026:1476 */           p[i] *= factor;
/* 1027:1477 */           if (Math.abs(pOld - p[i]) > 0.001D) {
/* 1028:1478 */             changed = true;
/* 1029:     */           }
/* 1030:     */         }
/* 1031:     */       }
/* 1032:1482 */       Utils.normalize(p);
/* 1033:1483 */       for (int i = 0; i < r.length; i++) {
/* 1034:1484 */         for (int j = i + 1; j < r.length; j++) {
/* 1035:1485 */           u[i][j] = (p[i] / (p[i] + p[j]));
/* 1036:     */         }
/* 1037:     */       }
/* 1038:1488 */     } while (changed);
/* 1039:1489 */     return p;
/* 1040:     */   }
/* 1041:     */   
/* 1042:     */   public double[][][] sparseWeights()
/* 1043:     */   {
/* 1044:1499 */     int numValues = this.m_classAttribute.numValues();
/* 1045:1500 */     double[][][] sparseWeights = new double[numValues][numValues][];
/* 1046:1502 */     for (int i = 0; i < numValues; i++) {
/* 1047:1503 */       for (int j = i + 1; j < numValues; j++) {
/* 1048:1504 */         sparseWeights[i][j] = this.m_classifiers[i][j].m_sparseWeights;
/* 1049:     */       }
/* 1050:     */     }
/* 1051:1508 */     return sparseWeights;
/* 1052:     */   }
/* 1053:     */   
/* 1054:     */   public int[][][] sparseIndices()
/* 1055:     */   {
/* 1056:1518 */     int numValues = this.m_classAttribute.numValues();
/* 1057:1519 */     int[][][] sparseIndices = new int[numValues][numValues][];
/* 1058:1521 */     for (int i = 0; i < numValues; i++) {
/* 1059:1522 */       for (int j = i + 1; j < numValues; j++) {
/* 1060:1523 */         sparseIndices[i][j] = this.m_classifiers[i][j].m_sparseIndices;
/* 1061:     */       }
/* 1062:     */     }
/* 1063:1527 */     return sparseIndices;
/* 1064:     */   }
/* 1065:     */   
/* 1066:     */   public double[][] bias()
/* 1067:     */   {
/* 1068:1537 */     int numValues = this.m_classAttribute.numValues();
/* 1069:1538 */     double[][] bias = new double[numValues][numValues];
/* 1070:1540 */     for (int i = 0; i < numValues; i++) {
/* 1071:1541 */       for (int j = i + 1; j < numValues; j++) {
/* 1072:1542 */         bias[i][j] = this.m_classifiers[i][j].m_b;
/* 1073:     */       }
/* 1074:     */     }
/* 1075:1546 */     return bias;
/* 1076:     */   }
/* 1077:     */   
/* 1078:     */   public int numClassAttributeValues()
/* 1079:     */   {
/* 1080:1556 */     return this.m_classAttribute.numValues();
/* 1081:     */   }
/* 1082:     */   
/* 1083:     */   public String[] classAttributeNames()
/* 1084:     */   {
/* 1085:1566 */     int numValues = this.m_classAttribute.numValues();
/* 1086:     */     
/* 1087:1568 */     String[] classAttributeNames = new String[numValues];
/* 1088:1570 */     for (int i = 0; i < numValues; i++) {
/* 1089:1571 */       classAttributeNames[i] = this.m_classAttribute.value(i);
/* 1090:     */     }
/* 1091:1574 */     return classAttributeNames;
/* 1092:     */   }
/* 1093:     */   
/* 1094:     */   public String[][][] attributeNames()
/* 1095:     */   {
/* 1096:1584 */     int numValues = this.m_classAttribute.numValues();
/* 1097:1585 */     String[][][] attributeNames = new String[numValues][numValues][];
/* 1098:1587 */     for (int i = 0; i < numValues; i++) {
/* 1099:1588 */       for (int j = i + 1; j < numValues; j++)
/* 1100:     */       {
/* 1101:1589 */         int numAttributes = this.m_classifiers[i][j].m_data.numAttributes();
/* 1102:1590 */         String[] attrNames = new String[numAttributes];
/* 1103:1591 */         for (int k = 0; k < numAttributes; k++) {
/* 1104:1592 */           attrNames[k] = this.m_classifiers[i][j].m_data.attribute(k).name();
/* 1105:     */         }
/* 1106:1594 */         attributeNames[i][j] = attrNames;
/* 1107:     */       }
/* 1108:     */     }
/* 1109:1597 */     return attributeNames;
/* 1110:     */   }
/* 1111:     */   
/* 1112:     */   public Enumeration<Option> listOptions()
/* 1113:     */   {
/* 1114:1608 */     Vector<Option> result = new Vector();
/* 1115:     */     
/* 1116:1610 */     result.addElement(new Option("\tTurns off all checks - use with caution!\n\tTurning them off assumes that data is purely numeric, doesn't\n\tcontain any missing values, and has a nominal class. Turning them\n\toff also means that no header information will be stored if the\n\tmachine is linear. Finally, it also assumes that no instance has\n\ta weight equal to 0.\n\t(default: checks on)", "no-checks", 0, "-no-checks"));
/* 1117:     */     
/* 1118:     */ 
/* 1119:     */ 
/* 1120:     */ 
/* 1121:     */ 
/* 1122:     */ 
/* 1123:     */ 
/* 1124:1618 */     result.addElement(new Option("\tThe complexity constant C. (default 1)", "C", 1, "-C <double>"));
/* 1125:     */     
/* 1126:     */ 
/* 1127:1621 */     result.addElement(new Option("\tWhether to 0=normalize/1=standardize/2=neither.\n\t(default 0=normalize)", "N", 1, "-N"));
/* 1128:     */     
/* 1129:     */ 
/* 1130:     */ 
/* 1131:1625 */     result.addElement(new Option("\tUse MIminimax feature space. ", "I", 0, "-I"));
/* 1132:     */     
/* 1133:     */ 
/* 1134:1628 */     result.addElement(new Option("\tThe tolerance parameter. (default 1.0e-3)", "L", 1, "-L <double>"));
/* 1135:     */     
/* 1136:     */ 
/* 1137:1631 */     result.addElement(new Option("\tThe epsilon for round-off error. (default 1.0e-12)", "P", 1, "-P <double>"));
/* 1138:     */     
/* 1139:     */ 
/* 1140:     */ 
/* 1141:1635 */     result.addElement(new Option("\tFit logistic models to SVM outputs. ", "M", 0, "-M"));
/* 1142:     */     
/* 1143:     */ 
/* 1144:1638 */     result.addElement(new Option("\tThe number of folds for the internal cross-validation. \n\t(default -1, use training data)", "V", 1, "-V <double>"));
/* 1145:     */     
/* 1146:     */ 
/* 1147:     */ 
/* 1148:1642 */     result.addElement(new Option("\tThe random number seed. (default 1)", "W", 1, "-W <double>"));
/* 1149:     */     
/* 1150:     */ 
/* 1151:1645 */     result.addElement(new Option("\tThe Kernel to use.\n\t(default: weka.classifiers.functions.supportVector.PolyKernel)", "K", 1, "-K <classname and parameters>"));
/* 1152:     */     
/* 1153:     */ 
/* 1154:     */ 
/* 1155:1649 */     result.addAll(Collections.list(super.listOptions()));
/* 1156:     */     
/* 1157:1651 */     result.addElement(new Option("", "", 0, "\nOptions specific to kernel " + getKernel().getClass().getName() + ":"));
/* 1158:     */     
/* 1159:     */ 
/* 1160:1654 */     result.addAll(Collections.list(getKernel().listOptions()));
/* 1161:     */     
/* 1162:     */ 
/* 1163:1657 */     return result.elements();
/* 1164:     */   }
/* 1165:     */   
/* 1166:     */   public void setOptions(String[] options)
/* 1167:     */     throws Exception
/* 1168:     */   {
/* 1169:1765 */     setChecksTurnedOff(Utils.getFlag("no-checks", options));
/* 1170:     */     
/* 1171:1767 */     String tmpStr = Utils.getOption('C', options);
/* 1172:1768 */     if (tmpStr.length() != 0) {
/* 1173:1769 */       setC(Double.parseDouble(tmpStr));
/* 1174:     */     } else {
/* 1175:1771 */       setC(1.0D);
/* 1176:     */     }
/* 1177:1774 */     tmpStr = Utils.getOption('L', options);
/* 1178:1775 */     if (tmpStr.length() != 0) {
/* 1179:1776 */       setToleranceParameter(Double.parseDouble(tmpStr));
/* 1180:     */     } else {
/* 1181:1778 */       setToleranceParameter(0.001D);
/* 1182:     */     }
/* 1183:1781 */     tmpStr = Utils.getOption('P', options);
/* 1184:1782 */     if (tmpStr.length() != 0) {
/* 1185:1783 */       setEpsilon(new Double(tmpStr).doubleValue());
/* 1186:     */     } else {
/* 1187:1785 */       setEpsilon(1.0E-012D);
/* 1188:     */     }
/* 1189:1788 */     setMinimax(Utils.getFlag('I', options));
/* 1190:     */     
/* 1191:1790 */     tmpStr = Utils.getOption('N', options);
/* 1192:1791 */     if (tmpStr.length() != 0) {
/* 1193:1792 */       setFilterType(new SelectedTag(Integer.parseInt(tmpStr), TAGS_FILTER));
/* 1194:     */     } else {
/* 1195:1794 */       setFilterType(new SelectedTag(0, TAGS_FILTER));
/* 1196:     */     }
/* 1197:1797 */     setBuildLogisticModels(Utils.getFlag('M', options));
/* 1198:     */     
/* 1199:1799 */     tmpStr = Utils.getOption('V', options);
/* 1200:1800 */     if (tmpStr.length() != 0) {
/* 1201:1801 */       this.m_numFolds = Integer.parseInt(tmpStr);
/* 1202:     */     } else {
/* 1203:1803 */       this.m_numFolds = -1;
/* 1204:     */     }
/* 1205:1806 */     tmpStr = Utils.getOption('W', options);
/* 1206:1807 */     if (tmpStr.length() != 0) {
/* 1207:1808 */       setRandomSeed(Integer.parseInt(tmpStr));
/* 1208:     */     } else {
/* 1209:1810 */       setRandomSeed(1);
/* 1210:     */     }
/* 1211:1813 */     tmpStr = Utils.getOption('K', options);
/* 1212:1814 */     String[] tmpOptions = Utils.splitOptions(tmpStr);
/* 1213:1815 */     if (tmpOptions.length != 0)
/* 1214:     */     {
/* 1215:1816 */       tmpStr = tmpOptions[0];
/* 1216:1817 */       tmpOptions[0] = "";
/* 1217:1818 */       setKernel(Kernel.forName(tmpStr, tmpOptions));
/* 1218:     */     }
/* 1219:1821 */     super.setOptions(options);
/* 1220:     */     
/* 1221:1823 */     Utils.checkForRemainingOptions(options);
/* 1222:     */   }
/* 1223:     */   
/* 1224:     */   public String[] getOptions()
/* 1225:     */   {
/* 1226:1834 */     Vector<String> result = new Vector();
/* 1227:1836 */     if (getChecksTurnedOff()) {
/* 1228:1837 */       result.add("-no-checks");
/* 1229:     */     }
/* 1230:1840 */     result.add("-C");
/* 1231:1841 */     result.add("" + getC());
/* 1232:     */     
/* 1233:1843 */     result.add("-L");
/* 1234:1844 */     result.add("" + getToleranceParameter());
/* 1235:     */     
/* 1236:1846 */     result.add("-P");
/* 1237:1847 */     result.add("" + getEpsilon());
/* 1238:     */     
/* 1239:1849 */     result.add("-N");
/* 1240:1850 */     result.add("" + this.m_filterType);
/* 1241:1852 */     if (getMinimax()) {
/* 1242:1853 */       result.add("-I");
/* 1243:     */     }
/* 1244:1856 */     if (getBuildLogisticModels()) {
/* 1245:1857 */       result.add("-M");
/* 1246:     */     }
/* 1247:1860 */     result.add("-V");
/* 1248:1861 */     result.add("" + getNumFolds());
/* 1249:     */     
/* 1250:1863 */     result.add("-W");
/* 1251:1864 */     result.add("" + getRandomSeed());
/* 1252:     */     
/* 1253:1866 */     result.add("-K");
/* 1254:1867 */     result.add("" + getKernel().getClass().getName() + " " + Utils.joinOptions(getKernel().getOptions()));
/* 1255:     */     
/* 1256:     */ 
/* 1257:1870 */     Collections.addAll(result, super.getOptions());
/* 1258:     */     
/* 1259:1872 */     return (String[])result.toArray(new String[result.size()]);
/* 1260:     */   }
/* 1261:     */   
/* 1262:     */   public void setChecksTurnedOff(boolean value)
/* 1263:     */   {
/* 1264:1882 */     if (value) {
/* 1265:1883 */       turnChecksOff();
/* 1266:     */     } else {
/* 1267:1885 */       turnChecksOn();
/* 1268:     */     }
/* 1269:     */   }
/* 1270:     */   
/* 1271:     */   public boolean getChecksTurnedOff()
/* 1272:     */   {
/* 1273:1895 */     return this.m_checksTurnedOff;
/* 1274:     */   }
/* 1275:     */   
/* 1276:     */   public String checksTurnedOffTipText()
/* 1277:     */   {
/* 1278:1905 */     return "Turns time-consuming checks off - use with caution.";
/* 1279:     */   }
/* 1280:     */   
/* 1281:     */   public String kernelTipText()
/* 1282:     */   {
/* 1283:1915 */     return "The kernel to use.";
/* 1284:     */   }
/* 1285:     */   
/* 1286:     */   public Kernel getKernel()
/* 1287:     */   {
/* 1288:1924 */     return this.m_kernel;
/* 1289:     */   }
/* 1290:     */   
/* 1291:     */   public void setKernel(Kernel value)
/* 1292:     */   {
/* 1293:1933 */     if (!(value instanceof MultiInstanceCapabilitiesHandler)) {
/* 1294:1934 */       throw new IllegalArgumentException("Kernel must be able to handle multi-instance data!\n(This one does not implement " + MultiInstanceCapabilitiesHandler.class.getName() + ")");
/* 1295:     */     }
/* 1296:1940 */     this.m_kernel = value;
/* 1297:     */   }
/* 1298:     */   
/* 1299:     */   public String cTipText()
/* 1300:     */   {
/* 1301:1950 */     return "The complexity parameter C.";
/* 1302:     */   }
/* 1303:     */   
/* 1304:     */   public double getC()
/* 1305:     */   {
/* 1306:1960 */     return this.m_C;
/* 1307:     */   }
/* 1308:     */   
/* 1309:     */   public void setC(double v)
/* 1310:     */   {
/* 1311:1970 */     this.m_C = v;
/* 1312:     */   }
/* 1313:     */   
/* 1314:     */   public String toleranceParameterTipText()
/* 1315:     */   {
/* 1316:1980 */     return "The tolerance parameter (shouldn't be changed).";
/* 1317:     */   }
/* 1318:     */   
/* 1319:     */   public double getToleranceParameter()
/* 1320:     */   {
/* 1321:1990 */     return this.m_tol;
/* 1322:     */   }
/* 1323:     */   
/* 1324:     */   public void setToleranceParameter(double v)
/* 1325:     */   {
/* 1326:2000 */     this.m_tol = v;
/* 1327:     */   }
/* 1328:     */   
/* 1329:     */   public String epsilonTipText()
/* 1330:     */   {
/* 1331:2010 */     return "The epsilon for round-off error (shouldn't be changed).";
/* 1332:     */   }
/* 1333:     */   
/* 1334:     */   public double getEpsilon()
/* 1335:     */   {
/* 1336:2020 */     return this.m_eps;
/* 1337:     */   }
/* 1338:     */   
/* 1339:     */   public void setEpsilon(double v)
/* 1340:     */   {
/* 1341:2030 */     this.m_eps = v;
/* 1342:     */   }
/* 1343:     */   
/* 1344:     */   public String filterTypeTipText()
/* 1345:     */   {
/* 1346:2040 */     return "Determines how/if the data will be transformed.";
/* 1347:     */   }
/* 1348:     */   
/* 1349:     */   public SelectedTag getFilterType()
/* 1350:     */   {
/* 1351:2051 */     return new SelectedTag(this.m_filterType, TAGS_FILTER);
/* 1352:     */   }
/* 1353:     */   
/* 1354:     */   public void setFilterType(SelectedTag newType)
/* 1355:     */   {
/* 1356:2062 */     if (newType.getTags() == TAGS_FILTER) {
/* 1357:2063 */       this.m_filterType = newType.getSelectedTag().getID();
/* 1358:     */     }
/* 1359:     */   }
/* 1360:     */   
/* 1361:     */   public String minimaxTipText()
/* 1362:     */   {
/* 1363:2074 */     return "Whether the MIMinimax feature space is to be used.";
/* 1364:     */   }
/* 1365:     */   
/* 1366:     */   public boolean getMinimax()
/* 1367:     */   {
/* 1368:2084 */     return this.m_minimax;
/* 1369:     */   }
/* 1370:     */   
/* 1371:     */   public void setMinimax(boolean v)
/* 1372:     */   {
/* 1373:2093 */     this.m_minimax = v;
/* 1374:     */   }
/* 1375:     */   
/* 1376:     */   public String buildLogisticModelsTipText()
/* 1377:     */   {
/* 1378:2103 */     return "Whether to fit logistic models to the outputs (for proper probability estimates).";
/* 1379:     */   }
/* 1380:     */   
/* 1381:     */   public boolean getBuildLogisticModels()
/* 1382:     */   {
/* 1383:2114 */     return this.m_fitLogisticModels;
/* 1384:     */   }
/* 1385:     */   
/* 1386:     */   public void setBuildLogisticModels(boolean newbuildLogisticModels)
/* 1387:     */   {
/* 1388:2124 */     this.m_fitLogisticModels = newbuildLogisticModels;
/* 1389:     */   }
/* 1390:     */   
/* 1391:     */   public String numFoldsTipText()
/* 1392:     */   {
/* 1393:2134 */     return "The number of folds for cross-validation used to generate training data for logistic models (-1 means use training data).";
/* 1394:     */   }
/* 1395:     */   
/* 1396:     */   public int getNumFolds()
/* 1397:     */   {
/* 1398:2145 */     return this.m_numFolds;
/* 1399:     */   }
/* 1400:     */   
/* 1401:     */   public void setNumFolds(int newnumFolds)
/* 1402:     */   {
/* 1403:2155 */     this.m_numFolds = newnumFolds;
/* 1404:     */   }
/* 1405:     */   
/* 1406:     */   public String randomSeedTipText()
/* 1407:     */   {
/* 1408:2165 */     return "Random number seed for the cross-validation.";
/* 1409:     */   }
/* 1410:     */   
/* 1411:     */   public int getRandomSeed()
/* 1412:     */   {
/* 1413:2175 */     return this.m_randomSeed;
/* 1414:     */   }
/* 1415:     */   
/* 1416:     */   public void setRandomSeed(int newrandomSeed)
/* 1417:     */   {
/* 1418:2185 */     this.m_randomSeed = newrandomSeed;
/* 1419:     */   }
/* 1420:     */   
/* 1421:     */   public String toString()
/* 1422:     */   {
/* 1423:2196 */     StringBuffer text = new StringBuffer();
/* 1424:2198 */     if (this.m_classAttribute == null) {
/* 1425:2199 */       return "SMO: No model built yet.";
/* 1426:     */     }
/* 1427:     */     try
/* 1428:     */     {
/* 1429:2202 */       text.append("SMO\n\n");
/* 1430:2203 */       for (int i = 0; i < this.m_classAttribute.numValues(); i++) {
/* 1431:2204 */         for (int j = i + 1; j < this.m_classAttribute.numValues(); j++)
/* 1432:     */         {
/* 1433:2205 */           text.append("Classifier for classes: " + this.m_classAttribute.value(i) + ", " + this.m_classAttribute.value(j) + "\n\n");
/* 1434:     */           
/* 1435:2207 */           text.append(this.m_classifiers[i][j]);
/* 1436:2208 */           if (this.m_fitLogisticModels)
/* 1437:     */           {
/* 1438:2209 */             text.append("\n\n");
/* 1439:2210 */             if (this.m_classifiers[i][j].m_logistic == null) {
/* 1440:2211 */               text.append("No logistic model has been fit.\n");
/* 1441:     */             } else {
/* 1442:2213 */               text.append(this.m_classifiers[i][j].m_logistic);
/* 1443:     */             }
/* 1444:     */           }
/* 1445:2216 */           text.append("\n\n");
/* 1446:     */         }
/* 1447:     */       }
/* 1448:     */     }
/* 1449:     */     catch (Exception e)
/* 1450:     */     {
/* 1451:2220 */       return "Can't print SMO classifier.";
/* 1452:     */     }
/* 1453:2223 */     return text.toString();
/* 1454:     */   }
/* 1455:     */   
/* 1456:     */   public String getRevision()
/* 1457:     */   {
/* 1458:2233 */     return RevisionUtils.extract("$Revision: 12560 $");
/* 1459:     */   }
/* 1460:     */   
/* 1461:     */   public static void main(String[] argv)
/* 1462:     */   {
/* 1463:2242 */     runClassifier(new MISMO(), argv);
/* 1464:     */   }
/* 1465:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.MISMO
 * JD-Core Version:    0.7.0.1
 */