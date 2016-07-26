/*    1:     */ package weka.classifiers.functions;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.io.Serializable;
/*    5:     */ import java.util.ArrayList;
/*    6:     */ import java.util.Collections;
/*    7:     */ import java.util.Enumeration;
/*    8:     */ import java.util.Random;
/*    9:     */ import java.util.Vector;
/*   10:     */ import weka.classifiers.AbstractClassifier;
/*   11:     */ import weka.classifiers.Classifier;
/*   12:     */ import weka.classifiers.functions.supportVector.Kernel;
/*   13:     */ import weka.classifiers.functions.supportVector.PolyKernel;
/*   14:     */ import weka.classifiers.functions.supportVector.SMOset;
/*   15:     */ import weka.classifiers.meta.MultiClassClassifier;
/*   16:     */ import weka.core.Attribute;
/*   17:     */ import weka.core.Capabilities;
/*   18:     */ import weka.core.Capabilities.Capability;
/*   19:     */ import weka.core.DenseInstance;
/*   20:     */ import weka.core.Instance;
/*   21:     */ import weka.core.Instances;
/*   22:     */ import weka.core.Option;
/*   23:     */ import weka.core.OptionHandler;
/*   24:     */ import weka.core.RevisionUtils;
/*   25:     */ import weka.core.SelectedTag;
/*   26:     */ import weka.core.Tag;
/*   27:     */ import weka.core.TechnicalInformation;
/*   28:     */ import weka.core.TechnicalInformation.Field;
/*   29:     */ import weka.core.TechnicalInformation.Type;
/*   30:     */ import weka.core.TechnicalInformationHandler;
/*   31:     */ import weka.core.Utils;
/*   32:     */ import weka.core.WeightedInstancesHandler;
/*   33:     */ import weka.filters.Filter;
/*   34:     */ import weka.filters.unsupervised.attribute.NominalToBinary;
/*   35:     */ import weka.filters.unsupervised.attribute.Normalize;
/*   36:     */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*   37:     */ import weka.filters.unsupervised.attribute.Standardize;
/*   38:     */ 
/*   39:     */ public class SMO
/*   40:     */   extends AbstractClassifier
/*   41:     */   implements WeightedInstancesHandler, TechnicalInformationHandler
/*   42:     */ {
/*   43:     */   static final long serialVersionUID = -6585883636378691736L;
/*   44:     */   public static final int FILTER_NORMALIZE = 0;
/*   45:     */   public static final int FILTER_STANDARDIZE = 1;
/*   46:     */   public static final int FILTER_NONE = 2;
/*   47:     */   
/*   48:     */   public String globalInfo()
/*   49:     */   {
/*   50: 244 */     return "Implements John Platt's sequential minimal optimization algorithm for training a support vector classifier.\n\nThis implementation globally replaces all missing values and transforms nominal attributes into binary ones. It also normalizes all attributes by default. (In that case the coefficients in the output are based on the normalized data, not the original data --- this is important for interpreting the classifier.)\n\nMulti-class problems are solved using pairwise classification (aka 1-vs-1).\n\nTo obtain proper probability estimates, use the option that fits calibration models to the outputs of the support vector machine. In the multi-class case, the predicted probabilities are coupled using Hastie and Tibshirani's pairwise coupling method.\n\nNote: for improved speed normalization should be turned off when operating on SparseInstances.\n\nFor more information on the SMO algorithm, see\n\n" + getTechnicalInformation().toString();
/*   51:     */   }
/*   52:     */   
/*   53:     */   public TechnicalInformation getTechnicalInformation()
/*   54:     */   {
/*   55: 274 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INCOLLECTION);
/*   56: 275 */     result.setValue(TechnicalInformation.Field.AUTHOR, "J. Platt");
/*   57: 276 */     result.setValue(TechnicalInformation.Field.YEAR, "1998");
/*   58: 277 */     result.setValue(TechnicalInformation.Field.TITLE, "Fast Training of Support Vector Machines using Sequential Minimal Optimization");
/*   59: 278 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Advances in Kernel Methods - Support Vector Learning");
/*   60: 279 */     result.setValue(TechnicalInformation.Field.EDITOR, "B. Schoelkopf and C. Burges and A. Smola");
/*   61: 280 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "MIT Press");
/*   62: 281 */     result.setValue(TechnicalInformation.Field.URL, "http://research.microsoft.com/~jplatt/smo.html");
/*   63: 282 */     result.setValue(TechnicalInformation.Field.PDF, "http://research.microsoft.com/~jplatt/smo-book.pdf");
/*   64: 283 */     result.setValue(TechnicalInformation.Field.PS, "http://research.microsoft.com/~jplatt/smo-book.ps.gz");
/*   65:     */     
/*   66: 285 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.ARTICLE);
/*   67: 286 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "S.S. Keerthi and S.K. Shevade and C. Bhattacharyya and K.R.K. Murthy");
/*   68: 287 */     additional.setValue(TechnicalInformation.Field.YEAR, "2001");
/*   69: 288 */     additional.setValue(TechnicalInformation.Field.TITLE, "Improvements to Platt's SMO Algorithm for SVM Classifier Design");
/*   70: 289 */     additional.setValue(TechnicalInformation.Field.JOURNAL, "Neural Computation");
/*   71: 290 */     additional.setValue(TechnicalInformation.Field.VOLUME, "13");
/*   72: 291 */     additional.setValue(TechnicalInformation.Field.NUMBER, "3");
/*   73: 292 */     additional.setValue(TechnicalInformation.Field.PAGES, "637-649");
/*   74: 293 */     additional.setValue(TechnicalInformation.Field.PS, "http://guppy.mpe.nus.edu.sg/~mpessk/svm/smo_mod_nc.ps.gz");
/*   75:     */     
/*   76: 295 */     additional = result.add(TechnicalInformation.Type.INPROCEEDINGS);
/*   77: 296 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "Trevor Hastie and Robert Tibshirani");
/*   78: 297 */     additional.setValue(TechnicalInformation.Field.YEAR, "1998");
/*   79: 298 */     additional.setValue(TechnicalInformation.Field.TITLE, "Classification by Pairwise Coupling");
/*   80: 299 */     additional.setValue(TechnicalInformation.Field.BOOKTITLE, "Advances in Neural Information Processing Systems");
/*   81: 300 */     additional.setValue(TechnicalInformation.Field.VOLUME, "10");
/*   82: 301 */     additional.setValue(TechnicalInformation.Field.PUBLISHER, "MIT Press");
/*   83: 302 */     additional.setValue(TechnicalInformation.Field.EDITOR, "Michael I. Jordan and Michael J. Kearns and Sara A. Solla");
/*   84: 303 */     additional.setValue(TechnicalInformation.Field.PS, "http://www-stat.stanford.edu/~hastie/Papers/2class.ps");
/*   85:     */     
/*   86: 305 */     return result;
/*   87:     */   }
/*   88:     */   
/*   89:     */   public class BinarySMO
/*   90:     */     implements Serializable
/*   91:     */   {
/*   92:     */     static final long serialVersionUID = -8246163625699362456L;
/*   93:     */     protected double[] m_alpha;
/*   94:     */     protected double m_b;
/*   95:     */     protected double m_bLow;
/*   96:     */     protected double m_bUp;
/*   97:     */     protected int m_iLow;
/*   98:     */     protected int m_iUp;
/*   99:     */     protected Instances m_data;
/*  100:     */     protected double[] m_weights;
/*  101:     */     protected double[] m_sparseWeights;
/*  102:     */     protected int[] m_sparseIndices;
/*  103:     */     protected Kernel m_kernel;
/*  104:     */     protected double[] m_class;
/*  105:     */     protected double[] m_errors;
/*  106:     */     protected SMOset m_I0;
/*  107:     */     protected SMOset m_I1;
/*  108:     */     protected SMOset m_I2;
/*  109:     */     protected SMOset m_I3;
/*  110:     */     protected SMOset m_I4;
/*  111:     */     protected SMOset m_supportVectors;
/*  112: 361 */     protected Classifier m_calibrator = null;
/*  113: 364 */     protected Instances m_calibrationDataHeader = null;
/*  114: 367 */     protected double m_sumOfWeights = 0.0D;
/*  115: 370 */     protected long m_nEvals = -1L;
/*  116: 373 */     protected int m_nCacheHits = -1;
/*  117:     */     
/*  118:     */     public BinarySMO() {}
/*  119:     */     
/*  120:     */     protected void fitCalibrator(Instances insts, int cl1, int cl2, int numFolds, Random random)
/*  121:     */       throws Exception
/*  122:     */     {
/*  123: 389 */       ArrayList<Attribute> atts = new ArrayList(2);
/*  124: 390 */       atts.add(new Attribute("pred"));
/*  125: 391 */       ArrayList<String> attVals = new ArrayList(2);
/*  126: 392 */       attVals.add(insts.classAttribute().value(cl1));
/*  127: 393 */       attVals.add(insts.classAttribute().value(cl2));
/*  128: 394 */       atts.add(new Attribute("class", attVals));
/*  129: 395 */       Instances data = new Instances("data", atts, insts.numInstances());
/*  130: 396 */       data.setClassIndex(1);
/*  131: 397 */       this.m_calibrationDataHeader = data;
/*  132: 400 */       if (numFolds <= 0)
/*  133:     */       {
/*  134: 403 */         for (int j = 0; j < insts.numInstances(); j++)
/*  135:     */         {
/*  136: 404 */           Instance inst = insts.instance(j);
/*  137: 405 */           double[] vals = new double[2];
/*  138: 406 */           vals[0] = SVMOutput(-1, inst);
/*  139: 407 */           if (inst.classValue() == cl2) {
/*  140: 408 */             vals[1] = 1.0D;
/*  141:     */           }
/*  142: 410 */           data.add(new DenseInstance(inst.weight(), vals));
/*  143:     */         }
/*  144:     */       }
/*  145:     */       else
/*  146:     */       {
/*  147: 415 */         if (numFolds > insts.numInstances()) {
/*  148: 416 */           numFolds = insts.numInstances();
/*  149:     */         }
/*  150: 420 */         insts = new Instances(insts);
/*  151:     */         
/*  152:     */ 
/*  153:     */ 
/*  154: 424 */         insts.randomize(random);
/*  155: 425 */         insts.stratify(numFolds);
/*  156: 426 */         for (int i = 0; i < numFolds; i++)
/*  157:     */         {
/*  158: 427 */           Instances train = insts.trainCV(numFolds, i, random);
/*  159:     */           
/*  160:     */ 
/*  161: 430 */           BinarySMO smo = new BinarySMO(SMO.this);
/*  162: 431 */           smo.setKernel(Kernel.makeCopy(SMO.this.m_kernel));
/*  163: 432 */           smo.buildClassifier(train, cl1, cl2, false, -1, -1);
/*  164: 433 */           Instances test = insts.testCV(numFolds, i);
/*  165: 434 */           for (int j = 0; j < test.numInstances(); j++)
/*  166:     */           {
/*  167: 435 */             double[] vals = new double[2];
/*  168: 436 */             vals[0] = smo.SVMOutput(-1, test.instance(j));
/*  169: 437 */             if (test.instance(j).classValue() == cl2) {
/*  170: 438 */               vals[1] = 1.0D;
/*  171:     */             }
/*  172: 440 */             data.add(new DenseInstance(test.instance(j).weight(), vals));
/*  173:     */           }
/*  174:     */         }
/*  175:     */       }
/*  176: 446 */       this.m_calibrator = AbstractClassifier.makeCopy(SMO.this.getCalibrator());
/*  177: 447 */       this.m_calibrator.buildClassifier(data);
/*  178:     */     }
/*  179:     */     
/*  180:     */     public void setKernel(Kernel value)
/*  181:     */     {
/*  182: 456 */       this.m_kernel = value;
/*  183:     */     }
/*  184:     */     
/*  185:     */     public Kernel getKernel()
/*  186:     */     {
/*  187: 465 */       return this.m_kernel;
/*  188:     */     }
/*  189:     */     
/*  190:     */     protected void buildClassifier(Instances insts, int cl1, int cl2, boolean fitCalibrator, int numFolds, int randomSeed)
/*  191:     */       throws Exception
/*  192:     */     {
/*  193: 483 */       this.m_bUp = -1.0D;
/*  194: 484 */       this.m_bLow = 1.0D;
/*  195: 485 */       this.m_b = 0.0D;
/*  196: 486 */       this.m_alpha = null;
/*  197: 487 */       this.m_data = null;
/*  198: 488 */       this.m_weights = null;
/*  199: 489 */       this.m_errors = null;
/*  200: 490 */       this.m_calibrator = null;
/*  201: 491 */       this.m_I0 = null;
/*  202: 492 */       this.m_I1 = null;
/*  203: 493 */       this.m_I2 = null;
/*  204: 494 */       this.m_I3 = null;
/*  205: 495 */       this.m_I4 = null;
/*  206: 496 */       this.m_sparseWeights = null;
/*  207: 497 */       this.m_sparseIndices = null;
/*  208:     */       
/*  209:     */ 
/*  210: 500 */       this.m_sumOfWeights = insts.sumOfWeights();
/*  211:     */       
/*  212:     */ 
/*  213: 503 */       this.m_class = new double[insts.numInstances()];
/*  214: 504 */       this.m_iUp = -1;
/*  215: 505 */       this.m_iLow = -1;
/*  216: 506 */       for (int i = 0; i < this.m_class.length; i++) {
/*  217: 507 */         if ((int)insts.instance(i).classValue() == cl1)
/*  218:     */         {
/*  219: 508 */           this.m_class[i] = -1.0D;
/*  220: 509 */           this.m_iLow = i;
/*  221:     */         }
/*  222: 510 */         else if ((int)insts.instance(i).classValue() == cl2)
/*  223:     */         {
/*  224: 511 */           this.m_class[i] = 1.0D;
/*  225: 512 */           this.m_iUp = i;
/*  226:     */         }
/*  227:     */         else
/*  228:     */         {
/*  229: 514 */           throw new Exception("This should never happen!");
/*  230:     */         }
/*  231:     */       }
/*  232: 519 */       if ((this.m_iUp == -1) || (this.m_iLow == -1))
/*  233:     */       {
/*  234: 520 */         if (this.m_iUp != -1)
/*  235:     */         {
/*  236: 521 */           this.m_b = -1.0D;
/*  237:     */         }
/*  238: 522 */         else if (this.m_iLow != -1)
/*  239:     */         {
/*  240: 523 */           this.m_b = 1.0D;
/*  241:     */         }
/*  242:     */         else
/*  243:     */         {
/*  244: 525 */           this.m_class = null;
/*  245: 526 */           return;
/*  246:     */         }
/*  247: 528 */         if (SMO.this.m_KernelIsLinear)
/*  248:     */         {
/*  249: 529 */           this.m_sparseWeights = new double[0];
/*  250: 530 */           this.m_sparseIndices = new int[0];
/*  251: 531 */           this.m_class = null;
/*  252:     */         }
/*  253:     */         else
/*  254:     */         {
/*  255: 533 */           this.m_supportVectors = new SMOset(0);
/*  256: 534 */           this.m_alpha = new double[0];
/*  257: 535 */           this.m_class = new double[0];
/*  258:     */         }
/*  259: 539 */         if (fitCalibrator) {
/*  260: 540 */           fitCalibrator(insts, cl1, cl2, numFolds, new Random(randomSeed));
/*  261:     */         }
/*  262: 542 */         return;
/*  263:     */       }
/*  264: 546 */       this.m_data = insts;
/*  265: 549 */       if (SMO.this.m_KernelIsLinear) {
/*  266: 550 */         this.m_weights = new double[this.m_data.numAttributes()];
/*  267:     */       } else {
/*  268: 552 */         this.m_weights = null;
/*  269:     */       }
/*  270: 556 */       this.m_alpha = new double[this.m_data.numInstances()];
/*  271:     */       
/*  272:     */ 
/*  273: 559 */       this.m_supportVectors = new SMOset(this.m_data.numInstances());
/*  274: 560 */       this.m_I0 = new SMOset(this.m_data.numInstances());
/*  275: 561 */       this.m_I1 = new SMOset(this.m_data.numInstances());
/*  276: 562 */       this.m_I2 = new SMOset(this.m_data.numInstances());
/*  277: 563 */       this.m_I3 = new SMOset(this.m_data.numInstances());
/*  278: 564 */       this.m_I4 = new SMOset(this.m_data.numInstances());
/*  279:     */       
/*  280:     */ 
/*  281: 567 */       this.m_sparseWeights = null;
/*  282: 568 */       this.m_sparseIndices = null;
/*  283:     */       
/*  284:     */ 
/*  285: 571 */       this.m_kernel.buildKernel(this.m_data);
/*  286:     */       
/*  287:     */ 
/*  288: 574 */       this.m_errors = new double[this.m_data.numInstances()];
/*  289: 575 */       this.m_errors[this.m_iLow] = 1.0D;
/*  290: 576 */       this.m_errors[this.m_iUp] = -1.0D;
/*  291: 579 */       for (int i = 0; i < this.m_class.length; i++) {
/*  292: 580 */         if (this.m_class[i] == 1.0D) {
/*  293: 581 */           this.m_I1.insert(i);
/*  294:     */         } else {
/*  295: 583 */           this.m_I4.insert(i);
/*  296:     */         }
/*  297:     */       }
/*  298: 588 */       int numChanged = 0;
/*  299: 589 */       boolean examineAll = true;
/*  300: 590 */       while ((numChanged > 0) || (examineAll))
/*  301:     */       {
/*  302: 591 */         numChanged = 0;
/*  303: 592 */         if (examineAll) {
/*  304: 593 */           for (int i = 0; i < this.m_alpha.length; i++) {
/*  305: 594 */             if (examineExample(i)) {
/*  306: 595 */               numChanged++;
/*  307:     */             }
/*  308:     */           }
/*  309:     */         } else {
/*  310: 601 */           for (int i = 0; i < this.m_alpha.length; i++) {
/*  311: 602 */             if ((this.m_alpha[i] > 0.0D) && (this.m_alpha[i] < SMO.this.m_C * this.m_data.instance(i).weight()))
/*  312:     */             {
/*  313: 604 */               if (examineExample(i)) {
/*  314: 605 */                 numChanged++;
/*  315:     */               }
/*  316: 609 */               if (this.m_bUp > this.m_bLow - 2.0D * SMO.this.m_tol)
/*  317:     */               {
/*  318: 610 */                 numChanged = 0;
/*  319: 611 */                 break;
/*  320:     */               }
/*  321:     */             }
/*  322:     */           }
/*  323:     */         }
/*  324: 624 */         if (examineAll) {
/*  325: 625 */           examineAll = false;
/*  326: 626 */         } else if (numChanged == 0) {
/*  327: 627 */           examineAll = true;
/*  328:     */         }
/*  329:     */       }
/*  330: 632 */       this.m_b = ((this.m_bLow + this.m_bUp) / 2.0D);
/*  331:     */       
/*  332:     */ 
/*  333: 635 */       this.m_nEvals = this.m_kernel.numEvals();
/*  334: 636 */       this.m_nCacheHits = this.m_kernel.numCacheHits();
/*  335: 639 */       if (SMO.this.m_KernelIsLinear) {
/*  336: 640 */         this.m_kernel = null;
/*  337:     */       } else {
/*  338: 642 */         this.m_kernel.clean();
/*  339:     */       }
/*  340: 645 */       this.m_errors = null;
/*  341: 646 */       this.m_I0 = (this.m_I1 = this.m_I2 = this.m_I3 = this.m_I4 = null);
/*  342: 650 */       if (SMO.this.m_KernelIsLinear)
/*  343:     */       {
/*  344: 653 */         this.m_supportVectors = null;
/*  345:     */         
/*  346:     */ 
/*  347: 656 */         this.m_class = null;
/*  348: 659 */         if (!SMO.this.m_checksTurnedOff) {
/*  349: 660 */           this.m_data = new Instances(this.m_data, 0);
/*  350:     */         } else {
/*  351: 662 */           this.m_data = null;
/*  352:     */         }
/*  353: 666 */         double[] sparseWeights = new double[this.m_weights.length];
/*  354: 667 */         int[] sparseIndices = new int[this.m_weights.length];
/*  355: 668 */         int counter = 0;
/*  356: 669 */         for (int i = 0; i < this.m_weights.length; i++) {
/*  357: 670 */           if (this.m_weights[i] != 0.0D)
/*  358:     */           {
/*  359: 671 */             sparseWeights[counter] = this.m_weights[i];
/*  360: 672 */             sparseIndices[counter] = i;
/*  361: 673 */             counter++;
/*  362:     */           }
/*  363:     */         }
/*  364: 676 */         this.m_sparseWeights = new double[counter];
/*  365: 677 */         this.m_sparseIndices = new int[counter];
/*  366: 678 */         System.arraycopy(sparseWeights, 0, this.m_sparseWeights, 0, counter);
/*  367: 679 */         System.arraycopy(sparseIndices, 0, this.m_sparseIndices, 0, counter);
/*  368:     */         
/*  369:     */ 
/*  370: 682 */         this.m_weights = null;
/*  371:     */         
/*  372:     */ 
/*  373: 685 */         this.m_alpha = null;
/*  374:     */       }
/*  375: 689 */       if (fitCalibrator) {
/*  376: 690 */         fitCalibrator(insts, cl1, cl2, numFolds, new Random(randomSeed));
/*  377:     */       }
/*  378:     */     }
/*  379:     */     
/*  380:     */     public double SVMOutput(int index, Instance inst)
/*  381:     */       throws Exception
/*  382:     */     {
/*  383: 704 */       double result = 0.0D;
/*  384:     */       int p1;
/*  385:     */       int p2;
/*  386: 707 */       if (SMO.this.m_KernelIsLinear)
/*  387:     */       {
/*  388: 710 */         if (this.m_sparseWeights == null)
/*  389:     */         {
/*  390: 711 */           int n1 = inst.numValues();
/*  391: 712 */           for (int p = 0; p < n1; p++) {
/*  392: 713 */             if (inst.index(p) != SMO.this.m_classIndex) {
/*  393: 714 */               result += this.m_weights[inst.index(p)] * inst.valueSparse(p);
/*  394:     */             }
/*  395:     */           }
/*  396:     */         }
/*  397:     */         else
/*  398:     */         {
/*  399: 718 */           int n1 = inst.numValues();
/*  400: 719 */           int n2 = this.m_sparseWeights.length;
/*  401: 720 */           p1 = 0;
/*  402: 720 */           for (p2 = 0; (p1 < n1) && (p2 < n2);)
/*  403:     */           {
/*  404: 721 */             int ind1 = inst.index(p1);
/*  405: 722 */             int ind2 = this.m_sparseIndices[p2];
/*  406: 723 */             if (ind1 == ind2)
/*  407:     */             {
/*  408: 724 */               if (ind1 != SMO.this.m_classIndex) {
/*  409: 725 */                 result += inst.valueSparse(p1) * this.m_sparseWeights[p2];
/*  410:     */               }
/*  411: 727 */               p1++;
/*  412: 728 */               p2++;
/*  413:     */             }
/*  414: 729 */             else if (ind1 > ind2)
/*  415:     */             {
/*  416: 730 */               p2++;
/*  417:     */             }
/*  418:     */             else
/*  419:     */             {
/*  420: 732 */               p1++;
/*  421:     */             }
/*  422:     */           }
/*  423:     */         }
/*  424:     */       }
/*  425:     */       else {
/*  426: 737 */         for (int i = this.m_supportVectors.getNext(-1); i != -1; i = this.m_supportVectors.getNext(i)) {
/*  427: 739 */           result += this.m_class[i] * this.m_alpha[i] * this.m_kernel.eval(index, i, inst);
/*  428:     */         }
/*  429:     */       }
/*  430: 742 */       result -= this.m_b;
/*  431:     */       
/*  432: 744 */       return result;
/*  433:     */     }
/*  434:     */     
/*  435:     */     public String toString()
/*  436:     */     {
/*  437: 754 */       StringBuffer text = new StringBuffer();
/*  438: 755 */       int printed = 0;
/*  439: 757 */       if ((this.m_alpha == null) && (this.m_sparseWeights == null)) {
/*  440: 758 */         return "BinarySMO: No model built yet.\n";
/*  441:     */       }
/*  442:     */       try
/*  443:     */       {
/*  444: 761 */         text.append("BinarySMO\n\n");
/*  445: 764 */         if (SMO.this.m_KernelIsLinear)
/*  446:     */         {
/*  447: 765 */           text.append("Machine linear: showing attribute weights, ");
/*  448: 766 */           text.append("not support vectors.\n\n");
/*  449: 770 */           for (int i = 0; i < this.m_sparseWeights.length; i++) {
/*  450: 771 */             if (this.m_sparseIndices[i] != SMO.this.m_classIndex)
/*  451:     */             {
/*  452: 772 */               if (printed > 0) {
/*  453: 773 */                 text.append(" + ");
/*  454:     */               } else {
/*  455: 775 */                 text.append("   ");
/*  456:     */               }
/*  457: 777 */               text.append(Utils.doubleToString(this.m_sparseWeights[i], 12, 4) + " * ");
/*  458: 779 */               if (SMO.this.m_filterType == 1) {
/*  459: 780 */                 text.append("(standardized) ");
/*  460: 781 */               } else if (SMO.this.m_filterType == 0) {
/*  461: 782 */                 text.append("(normalized) ");
/*  462:     */               }
/*  463: 784 */               if (!SMO.this.m_checksTurnedOff) {
/*  464: 785 */                 text.append(this.m_data.attribute(this.m_sparseIndices[i]).name() + "\n");
/*  465:     */               } else {
/*  466: 787 */                 text.append("attribute with index " + this.m_sparseIndices[i] + "\n");
/*  467:     */               }
/*  468: 790 */               printed++;
/*  469:     */             }
/*  470:     */           }
/*  471:     */         }
/*  472:     */         else
/*  473:     */         {
/*  474: 794 */           for (int i = 0; i < this.m_alpha.length; i++) {
/*  475: 795 */             if (this.m_supportVectors.contains(i))
/*  476:     */             {
/*  477: 796 */               double val = this.m_alpha[i];
/*  478: 797 */               if (this.m_class[i] == 1.0D)
/*  479:     */               {
/*  480: 798 */                 if (printed > 0) {
/*  481: 799 */                   text.append(" + ");
/*  482:     */                 }
/*  483:     */               }
/*  484:     */               else {
/*  485: 802 */                 text.append(" - ");
/*  486:     */               }
/*  487: 804 */               text.append(Utils.doubleToString(val, 12, 4) + " * <");
/*  488: 806 */               for (int j = 0; j < this.m_data.numAttributes(); j++)
/*  489:     */               {
/*  490: 807 */                 if (j != this.m_data.classIndex()) {
/*  491: 808 */                   text.append(this.m_data.instance(i).toString(j));
/*  492:     */                 }
/*  493: 810 */                 if (j != this.m_data.numAttributes() - 1) {
/*  494: 811 */                   text.append(" ");
/*  495:     */                 }
/*  496:     */               }
/*  497: 814 */               text.append("> * X]\n");
/*  498: 815 */               printed++;
/*  499:     */             }
/*  500:     */           }
/*  501:     */         }
/*  502: 819 */         if (this.m_b > 0.0D) {
/*  503: 820 */           text.append(" - " + Utils.doubleToString(this.m_b, 12, 4));
/*  504:     */         } else {
/*  505: 822 */           text.append(" + " + Utils.doubleToString(-this.m_b, 12, 4));
/*  506:     */         }
/*  507: 825 */         if (!SMO.this.m_KernelIsLinear) {
/*  508: 826 */           text.append("\n\nNumber of support vectors: " + this.m_supportVectors.numElements());
/*  509:     */         }
/*  510: 829 */         long numEval = this.m_nEvals;
/*  511: 830 */         int numCacheHits = this.m_nCacheHits;
/*  512:     */         
/*  513: 832 */         text.append("\n\nNumber of kernel evaluations: " + numEval);
/*  514: 833 */         if ((numCacheHits >= 0) && (numEval > 0L))
/*  515:     */         {
/*  516: 834 */           double hitRatio = 1.0D - numEval * 1.0D / (numCacheHits + numEval);
/*  517: 835 */           text.append(" (" + Utils.doubleToString(hitRatio * 100.0D, 7, 3).trim() + "% cached)");
/*  518:     */         }
/*  519:     */       }
/*  520:     */       catch (Exception e)
/*  521:     */       {
/*  522: 839 */         e.printStackTrace();
/*  523:     */         
/*  524: 841 */         return "Can't print BinarySMO classifier.";
/*  525:     */       }
/*  526: 844 */       return text.toString();
/*  527:     */     }
/*  528:     */     
/*  529:     */     protected boolean examineExample(int i2)
/*  530:     */       throws Exception
/*  531:     */     {
/*  532: 857 */       int i1 = -1;
/*  533:     */       
/*  534: 859 */       double y2 = this.m_class[i2];
/*  535:     */       double F2;
/*  536:     */       double F2;
/*  537: 860 */       if (this.m_I0.contains(i2))
/*  538:     */       {
/*  539: 861 */         F2 = this.m_errors[i2];
/*  540:     */       }
/*  541:     */       else
/*  542:     */       {
/*  543: 863 */         F2 = SVMOutput(i2, this.m_data.instance(i2)) + this.m_b - y2;
/*  544: 864 */         this.m_errors[i2] = F2;
/*  545: 867 */         if (((this.m_I1.contains(i2)) || (this.m_I2.contains(i2))) && (F2 < this.m_bUp))
/*  546:     */         {
/*  547: 868 */           this.m_bUp = F2;
/*  548: 869 */           this.m_iUp = i2;
/*  549:     */         }
/*  550: 870 */         else if (((this.m_I3.contains(i2)) || (this.m_I4.contains(i2))) && (F2 > this.m_bLow))
/*  551:     */         {
/*  552: 871 */           this.m_bLow = F2;
/*  553: 872 */           this.m_iLow = i2;
/*  554:     */         }
/*  555:     */       }
/*  556: 879 */       boolean optimal = true;
/*  557: 880 */       if (((this.m_I0.contains(i2)) || (this.m_I1.contains(i2)) || (this.m_I2.contains(i2))) && 
/*  558: 881 */         (this.m_bLow - F2 > 2.0D * SMO.this.m_tol))
/*  559:     */       {
/*  560: 882 */         optimal = false;
/*  561: 883 */         i1 = this.m_iLow;
/*  562:     */       }
/*  563: 886 */       if (((this.m_I0.contains(i2)) || (this.m_I3.contains(i2)) || (this.m_I4.contains(i2))) && 
/*  564: 887 */         (F2 - this.m_bUp > 2.0D * SMO.this.m_tol))
/*  565:     */       {
/*  566: 888 */         optimal = false;
/*  567: 889 */         i1 = this.m_iUp;
/*  568:     */       }
/*  569: 892 */       if (optimal) {
/*  570: 893 */         return false;
/*  571:     */       }
/*  572: 897 */       if (this.m_I0.contains(i2)) {
/*  573: 898 */         if (this.m_bLow - F2 > F2 - this.m_bUp) {
/*  574: 899 */           i1 = this.m_iLow;
/*  575:     */         } else {
/*  576: 901 */           i1 = this.m_iUp;
/*  577:     */         }
/*  578:     */       }
/*  579: 904 */       if (i1 == -1) {
/*  580: 905 */         throw new Exception("This should never happen!");
/*  581:     */       }
/*  582: 907 */       return takeStep(i1, i2, F2);
/*  583:     */     }
/*  584:     */     
/*  585:     */     protected boolean takeStep(int i1, int i2, double F2)
/*  586:     */       throws Exception
/*  587:     */     {
/*  588: 924 */       double C1 = SMO.this.m_C * this.m_data.instance(i1).weight();
/*  589: 925 */       double C2 = SMO.this.m_C * this.m_data.instance(i2).weight();
/*  590: 928 */       if (i1 == i2) {
/*  591: 929 */         return false;
/*  592:     */       }
/*  593: 933 */       double alph1 = this.m_alpha[i1];
/*  594: 934 */       double alph2 = this.m_alpha[i2];
/*  595: 935 */       double y1 = this.m_class[i1];
/*  596: 936 */       double y2 = this.m_class[i2];
/*  597: 937 */       double F1 = this.m_errors[i1];
/*  598: 938 */       double s = y1 * y2;
/*  599:     */       double H;
/*  600:     */       double L;
/*  601:     */       double H;
/*  602: 941 */       if (y1 != y2)
/*  603:     */       {
/*  604: 942 */         double L = Math.max(0.0D, alph2 - alph1);
/*  605: 943 */         H = Math.min(C2, C1 + alph2 - alph1);
/*  606:     */       }
/*  607:     */       else
/*  608:     */       {
/*  609: 945 */         L = Math.max(0.0D, alph1 + alph2 - C1);
/*  610: 946 */         H = Math.min(C2, alph1 + alph2);
/*  611:     */       }
/*  612: 948 */       if (L >= H) {
/*  613: 949 */         return false;
/*  614:     */       }
/*  615: 953 */       double k11 = this.m_kernel.eval(i1, i1, this.m_data.instance(i1));
/*  616: 954 */       double k12 = this.m_kernel.eval(i1, i2, this.m_data.instance(i1));
/*  617: 955 */       double k22 = this.m_kernel.eval(i2, i2, this.m_data.instance(i2));
/*  618: 956 */       double eta = 2.0D * k12 - k11 - k22;
/*  619:     */       double a2;
/*  620: 959 */       if (eta < 0.0D)
/*  621:     */       {
/*  622: 962 */         double a2 = alph2 - y2 * (F1 - F2) / eta;
/*  623: 965 */         if (a2 < L) {
/*  624: 966 */           a2 = L;
/*  625: 967 */         } else if (a2 > H) {
/*  626: 968 */           a2 = H;
/*  627:     */         }
/*  628:     */       }
/*  629:     */       else
/*  630:     */       {
/*  631: 973 */         double f1 = SVMOutput(i1, this.m_data.instance(i1));
/*  632: 974 */         double f2 = SVMOutput(i2, this.m_data.instance(i2));
/*  633: 975 */         double v1 = f1 + this.m_b - y1 * alph1 * k11 - y2 * alph2 * k12;
/*  634: 976 */         double v2 = f2 + this.m_b - y1 * alph1 * k12 - y2 * alph2 * k22;
/*  635: 977 */         double gamma = alph1 + s * alph2;
/*  636: 978 */         double Lobj = gamma - s * L + L - 0.5D * k11 * (gamma - s * L) * (gamma - s * L) - 0.5D * k22 * L * L - s * k12 * (gamma - s * L) * L - y1 * (gamma - s * L) * v1 - y2 * L * v2;
/*  637:     */         
/*  638:     */ 
/*  639: 981 */         double Hobj = gamma - s * H + H - 0.5D * k11 * (gamma - s * H) * (gamma - s * H) - 0.5D * k22 * H * H - s * k12 * (gamma - s * H) * H - y1 * (gamma - s * H) * v1 - y2 * H * v2;
/*  640:     */         double a2;
/*  641: 984 */         if (Lobj > Hobj + SMO.this.m_eps)
/*  642:     */         {
/*  643: 985 */           a2 = L;
/*  644:     */         }
/*  645:     */         else
/*  646:     */         {
/*  647:     */           double a2;
/*  648: 986 */           if (Lobj < Hobj - SMO.this.m_eps) {
/*  649: 987 */             a2 = H;
/*  650:     */           } else {
/*  651: 989 */             a2 = alph2;
/*  652:     */           }
/*  653:     */         }
/*  654:     */       }
/*  655: 992 */       if (Math.abs(a2 - alph2) < SMO.this.m_eps * (a2 + alph2 + SMO.this.m_eps)) {
/*  656: 993 */         return false;
/*  657:     */       }
/*  658: 997 */       if (a2 > C2 - SMO.m_Del * C2) {
/*  659: 998 */         a2 = C2;
/*  660: 999 */       } else if (a2 <= SMO.m_Del * C2) {
/*  661:1000 */         a2 = 0.0D;
/*  662:     */       }
/*  663:1004 */       double a1 = alph1 + s * (alph2 - a2);
/*  664:1007 */       if (a1 > C1 - SMO.m_Del * C1) {
/*  665:1008 */         a1 = C1;
/*  666:1009 */       } else if (a1 <= SMO.m_Del * C1) {
/*  667:1010 */         a1 = 0.0D;
/*  668:     */       }
/*  669:1014 */       if (a1 > 0.0D) {
/*  670:1015 */         this.m_supportVectors.insert(i1);
/*  671:     */       } else {
/*  672:1017 */         this.m_supportVectors.delete(i1);
/*  673:     */       }
/*  674:1019 */       if ((a1 > 0.0D) && (a1 < C1)) {
/*  675:1020 */         this.m_I0.insert(i1);
/*  676:     */       } else {
/*  677:1022 */         this.m_I0.delete(i1);
/*  678:     */       }
/*  679:1024 */       if ((y1 == 1.0D) && (a1 == 0.0D)) {
/*  680:1025 */         this.m_I1.insert(i1);
/*  681:     */       } else {
/*  682:1027 */         this.m_I1.delete(i1);
/*  683:     */       }
/*  684:1029 */       if ((y1 == -1.0D) && (a1 == C1)) {
/*  685:1030 */         this.m_I2.insert(i1);
/*  686:     */       } else {
/*  687:1032 */         this.m_I2.delete(i1);
/*  688:     */       }
/*  689:1034 */       if ((y1 == 1.0D) && (a1 == C1)) {
/*  690:1035 */         this.m_I3.insert(i1);
/*  691:     */       } else {
/*  692:1037 */         this.m_I3.delete(i1);
/*  693:     */       }
/*  694:1039 */       if ((y1 == -1.0D) && (a1 == 0.0D)) {
/*  695:1040 */         this.m_I4.insert(i1);
/*  696:     */       } else {
/*  697:1042 */         this.m_I4.delete(i1);
/*  698:     */       }
/*  699:1044 */       if (a2 > 0.0D) {
/*  700:1045 */         this.m_supportVectors.insert(i2);
/*  701:     */       } else {
/*  702:1047 */         this.m_supportVectors.delete(i2);
/*  703:     */       }
/*  704:1049 */       if ((a2 > 0.0D) && (a2 < C2)) {
/*  705:1050 */         this.m_I0.insert(i2);
/*  706:     */       } else {
/*  707:1052 */         this.m_I0.delete(i2);
/*  708:     */       }
/*  709:1054 */       if ((y2 == 1.0D) && (a2 == 0.0D)) {
/*  710:1055 */         this.m_I1.insert(i2);
/*  711:     */       } else {
/*  712:1057 */         this.m_I1.delete(i2);
/*  713:     */       }
/*  714:1059 */       if ((y2 == -1.0D) && (a2 == C2)) {
/*  715:1060 */         this.m_I2.insert(i2);
/*  716:     */       } else {
/*  717:1062 */         this.m_I2.delete(i2);
/*  718:     */       }
/*  719:1064 */       if ((y2 == 1.0D) && (a2 == C2)) {
/*  720:1065 */         this.m_I3.insert(i2);
/*  721:     */       } else {
/*  722:1067 */         this.m_I3.delete(i2);
/*  723:     */       }
/*  724:1069 */       if ((y2 == -1.0D) && (a2 == 0.0D)) {
/*  725:1070 */         this.m_I4.insert(i2);
/*  726:     */       } else {
/*  727:1072 */         this.m_I4.delete(i2);
/*  728:     */       }
/*  729:1076 */       if (SMO.this.m_KernelIsLinear)
/*  730:     */       {
/*  731:1077 */         Instance inst1 = this.m_data.instance(i1);
/*  732:1078 */         for (int p1 = 0; p1 < inst1.numValues(); p1++) {
/*  733:1079 */           if (inst1.index(p1) != this.m_data.classIndex()) {
/*  734:1080 */             this.m_weights[inst1.index(p1)] += y1 * (a1 - alph1) * inst1.valueSparse(p1);
/*  735:     */           }
/*  736:     */         }
/*  737:1084 */         Instance inst2 = this.m_data.instance(i2);
/*  738:1085 */         for (int p2 = 0; p2 < inst2.numValues(); p2++) {
/*  739:1086 */           if (inst2.index(p2) != this.m_data.classIndex()) {
/*  740:1087 */             this.m_weights[inst2.index(p2)] += y2 * (a2 - alph2) * inst2.valueSparse(p2);
/*  741:     */           }
/*  742:     */         }
/*  743:     */       }
/*  744:1094 */       for (int j = this.m_I0.getNext(-1); j != -1; j = this.m_I0.getNext(j)) {
/*  745:1095 */         if ((j != i1) && (j != i2)) {
/*  746:1096 */           this.m_errors[j] += y1 * (a1 - alph1) * this.m_kernel.eval(i1, j, this.m_data.instance(i1)) + y2 * (a2 - alph2) * this.m_kernel.eval(i2, j, this.m_data.instance(i2));
/*  747:     */         }
/*  748:     */       }
/*  749:1103 */       this.m_errors[i1] += y1 * (a1 - alph1) * k11 + y2 * (a2 - alph2) * k12;
/*  750:1104 */       this.m_errors[i2] += y1 * (a1 - alph1) * k12 + y2 * (a2 - alph2) * k22;
/*  751:     */       
/*  752:     */ 
/*  753:1107 */       this.m_alpha[i1] = a1;
/*  754:1108 */       this.m_alpha[i2] = a2;
/*  755:     */       
/*  756:     */ 
/*  757:1111 */       this.m_bLow = -1.797693134862316E+308D;
/*  758:1112 */       this.m_bUp = 1.7976931348623157E+308D;
/*  759:1113 */       this.m_iLow = -1;
/*  760:1114 */       this.m_iUp = -1;
/*  761:1115 */       for (int j = this.m_I0.getNext(-1); j != -1; j = this.m_I0.getNext(j))
/*  762:     */       {
/*  763:1116 */         if (this.m_errors[j] < this.m_bUp)
/*  764:     */         {
/*  765:1117 */           this.m_bUp = this.m_errors[j];
/*  766:1118 */           this.m_iUp = j;
/*  767:     */         }
/*  768:1120 */         if (this.m_errors[j] > this.m_bLow)
/*  769:     */         {
/*  770:1121 */           this.m_bLow = this.m_errors[j];
/*  771:1122 */           this.m_iLow = j;
/*  772:     */         }
/*  773:     */       }
/*  774:1125 */       if (!this.m_I0.contains(i1)) {
/*  775:1126 */         if ((this.m_I3.contains(i1)) || (this.m_I4.contains(i1)))
/*  776:     */         {
/*  777:1127 */           if (this.m_errors[i1] > this.m_bLow)
/*  778:     */           {
/*  779:1128 */             this.m_bLow = this.m_errors[i1];
/*  780:1129 */             this.m_iLow = i1;
/*  781:     */           }
/*  782:     */         }
/*  783:1132 */         else if (this.m_errors[i1] < this.m_bUp)
/*  784:     */         {
/*  785:1133 */           this.m_bUp = this.m_errors[i1];
/*  786:1134 */           this.m_iUp = i1;
/*  787:     */         }
/*  788:     */       }
/*  789:1138 */       if (!this.m_I0.contains(i2)) {
/*  790:1139 */         if ((this.m_I3.contains(i2)) || (this.m_I4.contains(i2)))
/*  791:     */         {
/*  792:1140 */           if (this.m_errors[i2] > this.m_bLow)
/*  793:     */           {
/*  794:1141 */             this.m_bLow = this.m_errors[i2];
/*  795:1142 */             this.m_iLow = i2;
/*  796:     */           }
/*  797:     */         }
/*  798:1145 */         else if (this.m_errors[i2] < this.m_bUp)
/*  799:     */         {
/*  800:1146 */           this.m_bUp = this.m_errors[i2];
/*  801:1147 */           this.m_iUp = i2;
/*  802:     */         }
/*  803:     */       }
/*  804:1151 */       if ((this.m_iLow == -1) || (this.m_iUp == -1)) {
/*  805:1152 */         throw new Exception("This should never happen!");
/*  806:     */       }
/*  807:1156 */       return true;
/*  808:     */     }
/*  809:     */     
/*  810:     */     protected void checkClassifier()
/*  811:     */       throws Exception
/*  812:     */     {
/*  813:1166 */       double sum = 0.0D;
/*  814:1167 */       for (int i = 0; i < this.m_alpha.length; i++) {
/*  815:1168 */         if (this.m_alpha[i] > 0.0D) {
/*  816:1169 */           sum += this.m_class[i] * this.m_alpha[i];
/*  817:     */         }
/*  818:     */       }
/*  819:1172 */       System.err.println("Sum of y(i) * alpha(i): " + sum);
/*  820:1174 */       for (int i = 0; i < this.m_alpha.length; i++)
/*  821:     */       {
/*  822:1175 */         double output = SVMOutput(i, this.m_data.instance(i));
/*  823:1176 */         if ((Utils.eq(this.m_alpha[i], 0.0D)) && 
/*  824:1177 */           (Utils.sm(this.m_class[i] * output, 1.0D))) {
/*  825:1178 */           System.err.println("KKT condition 1 violated: " + this.m_class[i] * output);
/*  826:     */         }
/*  827:1181 */         if ((Utils.gr(this.m_alpha[i], 0.0D)) && (Utils.sm(this.m_alpha[i], SMO.this.m_C * this.m_data.instance(i).weight()))) {
/*  828:1183 */           if (!Utils.eq(this.m_class[i] * output, 1.0D)) {
/*  829:1184 */             System.err.println("KKT condition 2 violated: " + this.m_class[i] * output);
/*  830:     */           }
/*  831:     */         }
/*  832:1187 */         if ((Utils.eq(this.m_alpha[i], SMO.this.m_C * this.m_data.instance(i).weight())) && 
/*  833:1188 */           (Utils.gr(this.m_class[i] * output, 1.0D))) {
/*  834:1189 */           System.err.println("KKT condition 3 violated: " + this.m_class[i] * output);
/*  835:     */         }
/*  836:     */       }
/*  837:     */     }
/*  838:     */     
/*  839:     */     public String getRevision()
/*  840:     */     {
/*  841:1201 */       return RevisionUtils.extract("$Revision: 12558 $");
/*  842:     */     }
/*  843:     */   }
/*  844:     */   
/*  845:1212 */   public static final Tag[] TAGS_FILTER = { new Tag(0, "Normalize training data"), new Tag(1, "Standardize training data"), new Tag(2, "No normalization/standardization") };
/*  846:1219 */   protected BinarySMO[][] m_classifiers = (BinarySMO[][])null;
/*  847:1222 */   protected double m_C = 1.0D;
/*  848:1225 */   protected double m_eps = 1.0E-012D;
/*  849:1228 */   protected double m_tol = 0.001D;
/*  850:1231 */   protected int m_filterType = 0;
/*  851:     */   protected NominalToBinary m_NominalToBinary;
/*  852:1237 */   protected Filter m_Filter = null;
/*  853:     */   protected ReplaceMissingValues m_Missing;
/*  854:1243 */   protected int m_classIndex = -1;
/*  855:     */   protected Attribute m_classAttribute;
/*  856:1249 */   protected boolean m_KernelIsLinear = false;
/*  857:     */   protected boolean m_checksTurnedOff;
/*  858:1259 */   protected static double m_Del = 4.940656458412465E-321D;
/*  859:1262 */   protected boolean m_fitCalibratorModels = false;
/*  860:1265 */   protected Classifier m_calibrator = new Logistic();
/*  861:1268 */   protected int m_numFolds = -1;
/*  862:1271 */   protected int m_randomSeed = 1;
/*  863:1274 */   protected Kernel m_kernel = new PolyKernel();
/*  864:     */   
/*  865:     */   public void turnChecksOff()
/*  866:     */   {
/*  867:1281 */     this.m_checksTurnedOff = true;
/*  868:     */   }
/*  869:     */   
/*  870:     */   public void turnChecksOn()
/*  871:     */   {
/*  872:1289 */     this.m_checksTurnedOff = false;
/*  873:     */   }
/*  874:     */   
/*  875:     */   public Capabilities getCapabilities()
/*  876:     */   {
/*  877:1298 */     Capabilities result = getKernel().getCapabilities();
/*  878:1299 */     result.setOwner(this);
/*  879:     */     
/*  880:     */ 
/*  881:1302 */     result.enableAllAttributeDependencies();
/*  882:1305 */     if (result.handles(Capabilities.Capability.NUMERIC_ATTRIBUTES)) {
/*  883:1306 */       result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  884:     */     }
/*  885:1307 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  886:     */     
/*  887:     */ 
/*  888:1310 */     result.disableAllClasses();
/*  889:1311 */     result.disableAllClassDependencies();
/*  890:1312 */     result.disable(Capabilities.Capability.NO_CLASS);
/*  891:1313 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  892:1314 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  893:     */     
/*  894:1316 */     return result;
/*  895:     */   }
/*  896:     */   
/*  897:     */   public void buildClassifier(Instances insts)
/*  898:     */     throws Exception
/*  899:     */   {
/*  900:1328 */     if (!this.m_checksTurnedOff)
/*  901:     */     {
/*  902:1330 */       getCapabilities().testWithFail(insts);
/*  903:     */       
/*  904:     */ 
/*  905:1333 */       insts = new Instances(insts);
/*  906:1334 */       insts.deleteWithMissingClass();
/*  907:     */       
/*  908:     */ 
/*  909:     */ 
/*  910:     */ 
/*  911:1339 */       Instances data = new Instances(insts, insts.numInstances());
/*  912:1340 */       for (int i = 0; i < insts.numInstances(); i++) {
/*  913:1341 */         if (insts.instance(i).weight() > 0.0D) {
/*  914:1342 */           data.add(insts.instance(i));
/*  915:     */         }
/*  916:     */       }
/*  917:1344 */       if (data.numInstances() == 0) {
/*  918:1345 */         throw new Exception("No training instances left after removing instances with weight 0!");
/*  919:     */       }
/*  920:1348 */       insts = data;
/*  921:     */     }
/*  922:1351 */     if (!this.m_checksTurnedOff)
/*  923:     */     {
/*  924:1352 */       this.m_Missing = new ReplaceMissingValues();
/*  925:1353 */       this.m_Missing.setInputFormat(insts);
/*  926:1354 */       insts = Filter.useFilter(insts, this.m_Missing);
/*  927:     */     }
/*  928:     */     else
/*  929:     */     {
/*  930:1356 */       this.m_Missing = null;
/*  931:     */     }
/*  932:1359 */     if (getCapabilities().handles(Capabilities.Capability.NUMERIC_ATTRIBUTES))
/*  933:     */     {
/*  934:1360 */       boolean onlyNumeric = true;
/*  935:1361 */       if (!this.m_checksTurnedOff) {
/*  936:1362 */         for (int i = 0; i < insts.numAttributes(); i++) {
/*  937:1363 */           if ((i != insts.classIndex()) && 
/*  938:1364 */             (!insts.attribute(i).isNumeric()))
/*  939:     */           {
/*  940:1365 */             onlyNumeric = false;
/*  941:1366 */             break;
/*  942:     */           }
/*  943:     */         }
/*  944:     */       }
/*  945:1372 */       if (!onlyNumeric)
/*  946:     */       {
/*  947:1373 */         this.m_NominalToBinary = new NominalToBinary();
/*  948:1374 */         this.m_NominalToBinary.setInputFormat(insts);
/*  949:1375 */         insts = Filter.useFilter(insts, this.m_NominalToBinary);
/*  950:     */       }
/*  951:     */       else
/*  952:     */       {
/*  953:1377 */         this.m_NominalToBinary = null;
/*  954:     */       }
/*  955:     */     }
/*  956:     */     else
/*  957:     */     {
/*  958:1380 */       this.m_NominalToBinary = null;
/*  959:     */     }
/*  960:1383 */     if (this.m_filterType == 1)
/*  961:     */     {
/*  962:1384 */       this.m_Filter = new Standardize();
/*  963:1385 */       this.m_Filter.setInputFormat(insts);
/*  964:1386 */       insts = Filter.useFilter(insts, this.m_Filter);
/*  965:     */     }
/*  966:1387 */     else if (this.m_filterType == 0)
/*  967:     */     {
/*  968:1388 */       this.m_Filter = new Normalize();
/*  969:1389 */       this.m_Filter.setInputFormat(insts);
/*  970:1390 */       insts = Filter.useFilter(insts, this.m_Filter);
/*  971:     */     }
/*  972:     */     else
/*  973:     */     {
/*  974:1392 */       this.m_Filter = null;
/*  975:     */     }
/*  976:1395 */     this.m_classIndex = insts.classIndex();
/*  977:1396 */     this.m_classAttribute = insts.classAttribute();
/*  978:1397 */     this.m_KernelIsLinear = (((this.m_kernel instanceof PolyKernel)) && (((PolyKernel)this.m_kernel).getExponent() == 1.0D));
/*  979:     */     
/*  980:     */ 
/*  981:1400 */     Instances[] subsets = new Instances[insts.numClasses()];
/*  982:1401 */     for (int i = 0; i < insts.numClasses(); i++) {
/*  983:1402 */       subsets[i] = new Instances(insts, insts.numInstances());
/*  984:     */     }
/*  985:1404 */     for (int j = 0; j < insts.numInstances(); j++)
/*  986:     */     {
/*  987:1405 */       Instance inst = insts.instance(j);
/*  988:1406 */       subsets[((int)inst.classValue())].add(inst);
/*  989:     */     }
/*  990:1408 */     for (int i = 0; i < insts.numClasses(); i++) {
/*  991:1409 */       subsets[i].compactify();
/*  992:     */     }
/*  993:1413 */     Random rand = new Random(this.m_randomSeed);
/*  994:1414 */     this.m_classifiers = new BinarySMO[insts.numClasses()][insts.numClasses()];
/*  995:1415 */     for (int i = 0; i < insts.numClasses(); i++) {
/*  996:1416 */       for (int j = i + 1; j < insts.numClasses(); j++)
/*  997:     */       {
/*  998:1417 */         this.m_classifiers[i][j] = new BinarySMO();
/*  999:1418 */         this.m_classifiers[i][j].setKernel(Kernel.makeCopy(getKernel()));
/* 1000:1419 */         Instances data = new Instances(insts, insts.numInstances());
/* 1001:1420 */         for (int k = 0; k < subsets[i].numInstances(); k++) {
/* 1002:1421 */           data.add(subsets[i].instance(k));
/* 1003:     */         }
/* 1004:1423 */         for (int k = 0; k < subsets[j].numInstances(); k++) {
/* 1005:1424 */           data.add(subsets[j].instance(k));
/* 1006:     */         }
/* 1007:1426 */         data.compactify();
/* 1008:1427 */         data.randomize(rand);
/* 1009:1428 */         this.m_classifiers[i][j].buildClassifier(data, i, j, this.m_fitCalibratorModels, this.m_numFolds, this.m_randomSeed);
/* 1010:     */       }
/* 1011:     */     }
/* 1012:     */   }
/* 1013:     */   
/* 1014:     */   public double[] distributionForInstance(Instance inst)
/* 1015:     */     throws Exception
/* 1016:     */   {
/* 1017:1444 */     if (!this.m_checksTurnedOff)
/* 1018:     */     {
/* 1019:1445 */       this.m_Missing.input(inst);
/* 1020:1446 */       this.m_Missing.batchFinished();
/* 1021:1447 */       inst = this.m_Missing.output();
/* 1022:     */     }
/* 1023:1450 */     if (this.m_NominalToBinary != null)
/* 1024:     */     {
/* 1025:1451 */       this.m_NominalToBinary.input(inst);
/* 1026:1452 */       this.m_NominalToBinary.batchFinished();
/* 1027:1453 */       inst = this.m_NominalToBinary.output();
/* 1028:     */     }
/* 1029:1456 */     if (this.m_Filter != null)
/* 1030:     */     {
/* 1031:1457 */       this.m_Filter.input(inst);
/* 1032:1458 */       this.m_Filter.batchFinished();
/* 1033:1459 */       inst = this.m_Filter.output();
/* 1034:     */     }
/* 1035:1462 */     if (!this.m_fitCalibratorModels)
/* 1036:     */     {
/* 1037:1463 */       double[] result = new double[inst.numClasses()];
/* 1038:1464 */       for (int i = 0; i < inst.numClasses(); i++) {
/* 1039:1465 */         for (int j = i + 1; j < inst.numClasses(); j++) {
/* 1040:1466 */           if ((this.m_classifiers[i][j].m_alpha != null) || (this.m_classifiers[i][j].m_sparseWeights != null))
/* 1041:     */           {
/* 1042:1468 */             double output = this.m_classifiers[i][j].SVMOutput(-1, inst);
/* 1043:1469 */             if (output > 0.0D) {
/* 1044:1470 */               result[j] += 1.0D;
/* 1045:     */             } else {
/* 1046:1472 */               result[i] += 1.0D;
/* 1047:     */             }
/* 1048:     */           }
/* 1049:     */         }
/* 1050:     */       }
/* 1051:1477 */       Utils.normalize(result);
/* 1052:1478 */       return result;
/* 1053:     */     }
/* 1054:1483 */     if (inst.numClasses() == 2)
/* 1055:     */     {
/* 1056:1484 */       double[] newInst = new double[2];
/* 1057:1485 */       newInst[0] = this.m_classifiers[0][1].SVMOutput(-1, inst);
/* 1058:1486 */       newInst[1] = Utils.missingValue();
/* 1059:1487 */       DenseInstance d = new DenseInstance(1.0D, newInst);
/* 1060:1488 */       d.setDataset(this.m_classifiers[0][1].m_calibrationDataHeader);
/* 1061:1489 */       return this.m_classifiers[0][1].m_calibrator.distributionForInstance(d);
/* 1062:     */     }
/* 1063:1491 */     double[][] r = new double[inst.numClasses()][inst.numClasses()];
/* 1064:1492 */     double[][] n = new double[inst.numClasses()][inst.numClasses()];
/* 1065:1493 */     for (int i = 0; i < inst.numClasses(); i++) {
/* 1066:1494 */       for (int j = i + 1; j < inst.numClasses(); j++) {
/* 1067:1495 */         if ((this.m_classifiers[i][j].m_alpha != null) || (this.m_classifiers[i][j].m_sparseWeights != null))
/* 1068:     */         {
/* 1069:1497 */           double[] newInst = new double[2];
/* 1070:1498 */           newInst[0] = this.m_classifiers[i][j].SVMOutput(-1, inst);
/* 1071:1499 */           newInst[1] = Utils.missingValue();
/* 1072:1500 */           DenseInstance d = new DenseInstance(1.0D, newInst);
/* 1073:1501 */           d.setDataset(this.m_classifiers[i][j].m_calibrationDataHeader);
/* 1074:1502 */           r[i][j] = this.m_classifiers[i][j].m_calibrator.distributionForInstance(d)[0];
/* 1075:1503 */           n[i][j] = this.m_classifiers[i][j].m_sumOfWeights;
/* 1076:     */         }
/* 1077:     */       }
/* 1078:     */     }
/* 1079:1507 */     return MultiClassClassifier.pairwiseCoupling(n, r);
/* 1080:     */   }
/* 1081:     */   
/* 1082:     */   public int[] obtainVotes(Instance inst)
/* 1083:     */     throws Exception
/* 1084:     */   {
/* 1085:1520 */     if (!this.m_checksTurnedOff)
/* 1086:     */     {
/* 1087:1521 */       this.m_Missing.input(inst);
/* 1088:1522 */       this.m_Missing.batchFinished();
/* 1089:1523 */       inst = this.m_Missing.output();
/* 1090:     */     }
/* 1091:1526 */     if (this.m_NominalToBinary != null)
/* 1092:     */     {
/* 1093:1527 */       this.m_NominalToBinary.input(inst);
/* 1094:1528 */       this.m_NominalToBinary.batchFinished();
/* 1095:1529 */       inst = this.m_NominalToBinary.output();
/* 1096:     */     }
/* 1097:1532 */     if (this.m_Filter != null)
/* 1098:     */     {
/* 1099:1533 */       this.m_Filter.input(inst);
/* 1100:1534 */       this.m_Filter.batchFinished();
/* 1101:1535 */       inst = this.m_Filter.output();
/* 1102:     */     }
/* 1103:1538 */     int[] votes = new int[inst.numClasses()];
/* 1104:1539 */     for (int i = 0; i < inst.numClasses(); i++) {
/* 1105:1540 */       for (int j = i + 1; j < inst.numClasses(); j++)
/* 1106:     */       {
/* 1107:1541 */         double output = this.m_classifiers[i][j].SVMOutput(-1, inst);
/* 1108:1542 */         if (output > 0.0D) {
/* 1109:1543 */           votes[j] += 1;
/* 1110:     */         } else {
/* 1111:1545 */           votes[i] += 1;
/* 1112:     */         }
/* 1113:     */       }
/* 1114:     */     }
/* 1115:1549 */     return votes;
/* 1116:     */   }
/* 1117:     */   
/* 1118:     */   public double[][][] sparseWeights()
/* 1119:     */   {
/* 1120:1557 */     int numValues = this.m_classAttribute.numValues();
/* 1121:1558 */     double[][][] sparseWeights = new double[numValues][numValues][];
/* 1122:1560 */     for (int i = 0; i < numValues; i++) {
/* 1123:1561 */       for (int j = i + 1; j < numValues; j++) {
/* 1124:1562 */         sparseWeights[i][j] = this.m_classifiers[i][j].m_sparseWeights;
/* 1125:     */       }
/* 1126:     */     }
/* 1127:1566 */     return sparseWeights;
/* 1128:     */   }
/* 1129:     */   
/* 1130:     */   public int[][][] sparseIndices()
/* 1131:     */   {
/* 1132:1574 */     int numValues = this.m_classAttribute.numValues();
/* 1133:1575 */     int[][][] sparseIndices = new int[numValues][numValues][];
/* 1134:1577 */     for (int i = 0; i < numValues; i++) {
/* 1135:1578 */       for (int j = i + 1; j < numValues; j++) {
/* 1136:1579 */         sparseIndices[i][j] = this.m_classifiers[i][j].m_sparseIndices;
/* 1137:     */       }
/* 1138:     */     }
/* 1139:1583 */     return sparseIndices;
/* 1140:     */   }
/* 1141:     */   
/* 1142:     */   public double[][] bias()
/* 1143:     */   {
/* 1144:1591 */     int numValues = this.m_classAttribute.numValues();
/* 1145:1592 */     double[][] bias = new double[numValues][numValues];
/* 1146:1594 */     for (int i = 0; i < numValues; i++) {
/* 1147:1595 */       for (int j = i + 1; j < numValues; j++) {
/* 1148:1596 */         bias[i][j] = this.m_classifiers[i][j].m_b;
/* 1149:     */       }
/* 1150:     */     }
/* 1151:1600 */     return bias;
/* 1152:     */   }
/* 1153:     */   
/* 1154:     */   public int numClassAttributeValues()
/* 1155:     */   {
/* 1156:1608 */     return this.m_classAttribute.numValues();
/* 1157:     */   }
/* 1158:     */   
/* 1159:     */   public String[] classAttributeNames()
/* 1160:     */   {
/* 1161:1616 */     int numValues = this.m_classAttribute.numValues();
/* 1162:     */     
/* 1163:1618 */     String[] classAttributeNames = new String[numValues];
/* 1164:1620 */     for (int i = 0; i < numValues; i++) {
/* 1165:1621 */       classAttributeNames[i] = this.m_classAttribute.value(i);
/* 1166:     */     }
/* 1167:1624 */     return classAttributeNames;
/* 1168:     */   }
/* 1169:     */   
/* 1170:     */   public String[][][] attributeNames()
/* 1171:     */   {
/* 1172:1632 */     int numValues = this.m_classAttribute.numValues();
/* 1173:1633 */     String[][][] attributeNames = new String[numValues][numValues][];
/* 1174:1635 */     for (int i = 0; i < numValues; i++) {
/* 1175:1636 */       for (int j = i + 1; j < numValues; j++)
/* 1176:     */       {
/* 1177:1638 */         int numAttributes = this.m_classifiers[i][j].m_sparseIndices.length;
/* 1178:1639 */         String[] attrNames = new String[numAttributes];
/* 1179:1640 */         for (int k = 0; k < numAttributes; k++) {
/* 1180:1641 */           attrNames[k] = this.m_classifiers[i][j].m_data.attribute(this.m_classifiers[i][j].m_sparseIndices[k]).name();
/* 1181:     */         }
/* 1182:1644 */         attributeNames[i][j] = attrNames;
/* 1183:     */       }
/* 1184:     */     }
/* 1185:1647 */     return attributeNames;
/* 1186:     */   }
/* 1187:     */   
/* 1188:     */   public Enumeration<Option> listOptions()
/* 1189:     */   {
/* 1190:1657 */     Vector<Option> result = new Vector();
/* 1191:     */     
/* 1192:1659 */     result.addElement(new Option("\tTurns off all checks - use with caution!\n\tTurning them off assumes that data is purely numeric, doesn't\n\tcontain any missing values, and has a nominal class. Turning them\n\toff also means that no header information will be stored if the\n\tmachine is linear. Finally, it also assumes that no instance has\n\ta weight equal to 0.\n\t(default: checks on)", "no-checks", 0, "-no-checks"));
/* 1193:     */     
/* 1194:     */ 
/* 1195:     */ 
/* 1196:     */ 
/* 1197:     */ 
/* 1198:     */ 
/* 1199:     */ 
/* 1200:     */ 
/* 1201:     */ 
/* 1202:1669 */     result.addElement(new Option("\tThe complexity constant C. (default 1)", "C", 1, "-C <double>"));
/* 1203:     */     
/* 1204:     */ 
/* 1205:     */ 
/* 1206:1673 */     result.addElement(new Option("\tWhether to 0=normalize/1=standardize/2=neither. (default 0=normalize)", "N", 1, "-N"));
/* 1207:     */     
/* 1208:     */ 
/* 1209:     */ 
/* 1210:     */ 
/* 1211:1678 */     result.addElement(new Option("\tThe tolerance parameter. (default 1.0e-3)", "L", 1, "-L <double>"));
/* 1212:     */     
/* 1213:     */ 
/* 1214:     */ 
/* 1215:     */ 
/* 1216:1683 */     result.addElement(new Option("\tThe epsilon for round-off error. (default 1.0e-12)", "P", 1, "-P <double>"));
/* 1217:     */     
/* 1218:     */ 
/* 1219:     */ 
/* 1220:     */ 
/* 1221:1688 */     result.addElement(new Option("\tFit calibration models to SVM outputs. ", "M", 0, "-M"));
/* 1222:     */     
/* 1223:     */ 
/* 1224:     */ 
/* 1225:1692 */     result.addElement(new Option("\tThe number of folds for the internal\n\tcross-validation. (default -1, use training data)", "V", 1, "-V <double>"));
/* 1226:     */     
/* 1227:     */ 
/* 1228:     */ 
/* 1229:     */ 
/* 1230:     */ 
/* 1231:1698 */     result.addElement(new Option("\tThe random number seed. (default 1)", "W", 1, "-W <double>"));
/* 1232:     */     
/* 1233:     */ 
/* 1234:     */ 
/* 1235:     */ 
/* 1236:1703 */     result.addElement(new Option("\tThe Kernel to use.\n\t(default: weka.classifiers.functions.supportVector.PolyKernel)", "K", 1, "-K <classname and parameters>"));
/* 1237:     */     
/* 1238:     */ 
/* 1239:     */ 
/* 1240:     */ 
/* 1241:1708 */     result.addElement(new Option("\tFull name of calibration model, followed by options.\n\t(default: \"weka.classifiers.functions.Logistic\")", "calibrator", 0, "-calibrator <scheme specification>"));
/* 1242:     */     
/* 1243:     */ 
/* 1244:     */ 
/* 1245:     */ 
/* 1246:1713 */     result.addAll(Collections.list(super.listOptions()));
/* 1247:     */     
/* 1248:1715 */     result.addElement(new Option("", "", 0, "\nOptions specific to kernel " + getKernel().getClass().getName() + ":"));
/* 1249:     */     
/* 1250:     */ 
/* 1251:     */ 
/* 1252:     */ 
/* 1253:1720 */     result.addAll(Collections.list(getKernel().listOptions()));
/* 1254:1722 */     if ((getCalibrator() instanceof OptionHandler))
/* 1255:     */     {
/* 1256:1723 */       result.addElement(new Option("", "", 0, "\nOptions specific to calibrator " + getCalibrator().getClass().getName() + ":"));
/* 1257:     */       
/* 1258:     */ 
/* 1259:     */ 
/* 1260:1727 */       result.addAll(Collections.list(((OptionHandler)getCalibrator()).listOptions()));
/* 1261:     */     }
/* 1262:1729 */     return result.elements();
/* 1263:     */   }
/* 1264:     */   
/* 1265:     */   public void setOptions(String[] options)
/* 1266:     */     throws Exception
/* 1267:     */   {
/* 1268:1846 */     setChecksTurnedOff(Utils.getFlag("no-checks", options));
/* 1269:     */     
/* 1270:1848 */     String tmpStr = Utils.getOption('C', options);
/* 1271:1849 */     if (tmpStr.length() != 0) {
/* 1272:1850 */       setC(Double.parseDouble(tmpStr));
/* 1273:     */     } else {
/* 1274:1852 */       setC(1.0D);
/* 1275:     */     }
/* 1276:1854 */     tmpStr = Utils.getOption('L', options);
/* 1277:1855 */     if (tmpStr.length() != 0) {
/* 1278:1856 */       setToleranceParameter(Double.parseDouble(tmpStr));
/* 1279:     */     } else {
/* 1280:1858 */       setToleranceParameter(0.001D);
/* 1281:     */     }
/* 1282:1860 */     tmpStr = Utils.getOption('P', options);
/* 1283:1861 */     if (tmpStr.length() != 0) {
/* 1284:1862 */       setEpsilon(Double.parseDouble(tmpStr));
/* 1285:     */     } else {
/* 1286:1864 */       setEpsilon(1.0E-012D);
/* 1287:     */     }
/* 1288:1866 */     tmpStr = Utils.getOption('N', options);
/* 1289:1867 */     if (tmpStr.length() != 0) {
/* 1290:1868 */       setFilterType(new SelectedTag(Integer.parseInt(tmpStr), TAGS_FILTER));
/* 1291:     */     } else {
/* 1292:1870 */       setFilterType(new SelectedTag(0, TAGS_FILTER));
/* 1293:     */     }
/* 1294:1872 */     setBuildCalibrationModels(Utils.getFlag('M', options));
/* 1295:     */     
/* 1296:1874 */     tmpStr = Utils.getOption('V', options);
/* 1297:1875 */     if (tmpStr.length() != 0) {
/* 1298:1876 */       setNumFolds(Integer.parseInt(tmpStr));
/* 1299:     */     } else {
/* 1300:1878 */       setNumFolds(-1);
/* 1301:     */     }
/* 1302:1880 */     tmpStr = Utils.getOption('W', options);
/* 1303:1881 */     if (tmpStr.length() != 0) {
/* 1304:1882 */       setRandomSeed(Integer.parseInt(tmpStr));
/* 1305:     */     } else {
/* 1306:1884 */       setRandomSeed(1);
/* 1307:     */     }
/* 1308:1886 */     tmpStr = Utils.getOption('K', options);
/* 1309:1887 */     String[] tmpOptions = Utils.splitOptions(tmpStr);
/* 1310:1888 */     if (tmpOptions.length != 0)
/* 1311:     */     {
/* 1312:1889 */       tmpStr = tmpOptions[0];
/* 1313:1890 */       tmpOptions[0] = "";
/* 1314:1891 */       setKernel(Kernel.forName(tmpStr, tmpOptions));
/* 1315:     */     }
/* 1316:1894 */     String classifierString = Utils.getOption("calibrator", options);
/* 1317:1895 */     String[] classifierSpec = Utils.splitOptions(classifierString);
/* 1318:     */     String classifierName;
/* 1319:     */     String classifierName;
/* 1320:1897 */     if (classifierSpec.length == 0)
/* 1321:     */     {
/* 1322:1898 */       classifierName = "weka.classifiers.functions.Logistic";
/* 1323:     */     }
/* 1324:     */     else
/* 1325:     */     {
/* 1326:1900 */       classifierName = classifierSpec[0];
/* 1327:1901 */       classifierSpec[0] = "";
/* 1328:     */     }
/* 1329:1903 */     setCalibrator(AbstractClassifier.forName(classifierName, classifierSpec));
/* 1330:     */     
/* 1331:1905 */     super.setOptions(options);
/* 1332:     */     
/* 1333:1907 */     Utils.checkForRemainingOptions(options);
/* 1334:     */   }
/* 1335:     */   
/* 1336:     */   public String[] getOptions()
/* 1337:     */   {
/* 1338:1917 */     Vector<String> result = new Vector();
/* 1339:1919 */     if (getChecksTurnedOff()) {
/* 1340:1920 */       result.add("-no-checks");
/* 1341:     */     }
/* 1342:1922 */     result.add("-C");
/* 1343:1923 */     result.add("" + getC());
/* 1344:     */     
/* 1345:1925 */     result.add("-L");
/* 1346:1926 */     result.add("" + getToleranceParameter());
/* 1347:     */     
/* 1348:1928 */     result.add("-P");
/* 1349:1929 */     result.add("" + getEpsilon());
/* 1350:     */     
/* 1351:1931 */     result.add("-N");
/* 1352:1932 */     result.add("" + this.m_filterType);
/* 1353:1934 */     if (getBuildCalibrationModels()) {
/* 1354:1935 */       result.add("-M");
/* 1355:     */     }
/* 1356:1937 */     result.add("-V");
/* 1357:1938 */     result.add("" + getNumFolds());
/* 1358:     */     
/* 1359:1940 */     result.add("-W");
/* 1360:1941 */     result.add("" + getRandomSeed());
/* 1361:     */     
/* 1362:1943 */     result.add("-K");
/* 1363:1944 */     result.add("" + getKernel().getClass().getName() + " " + Utils.joinOptions(getKernel().getOptions()));
/* 1364:     */     
/* 1365:1946 */     result.add("-calibrator");
/* 1366:1947 */     result.add(getCalibrator().getClass().getName() + " " + Utils.joinOptions(((OptionHandler)getCalibrator()).getOptions()));
/* 1367:     */     
/* 1368:     */ 
/* 1369:1950 */     Collections.addAll(result, super.getOptions());
/* 1370:     */     
/* 1371:1952 */     return (String[])result.toArray(new String[result.size()]);
/* 1372:     */   }
/* 1373:     */   
/* 1374:     */   public void setChecksTurnedOff(boolean value)
/* 1375:     */   {
/* 1376:1962 */     if (value) {
/* 1377:1963 */       turnChecksOff();
/* 1378:     */     } else {
/* 1379:1965 */       turnChecksOn();
/* 1380:     */     }
/* 1381:     */   }
/* 1382:     */   
/* 1383:     */   public boolean getChecksTurnedOff()
/* 1384:     */   {
/* 1385:1974 */     return this.m_checksTurnedOff;
/* 1386:     */   }
/* 1387:     */   
/* 1388:     */   public String checksTurnedOffTipText()
/* 1389:     */   {
/* 1390:1984 */     return "Turns time-consuming checks off - use with caution.";
/* 1391:     */   }
/* 1392:     */   
/* 1393:     */   public String kernelTipText()
/* 1394:     */   {
/* 1395:1994 */     return "The kernel to use.";
/* 1396:     */   }
/* 1397:     */   
/* 1398:     */   public void setKernel(Kernel value)
/* 1399:     */   {
/* 1400:2003 */     this.m_kernel = value;
/* 1401:     */   }
/* 1402:     */   
/* 1403:     */   public Kernel getKernel()
/* 1404:     */   {
/* 1405:2012 */     return this.m_kernel;
/* 1406:     */   }
/* 1407:     */   
/* 1408:     */   public String calibratorTipText()
/* 1409:     */   {
/* 1410:2022 */     return "The calibration method to use.";
/* 1411:     */   }
/* 1412:     */   
/* 1413:     */   public void setCalibrator(Classifier value)
/* 1414:     */   {
/* 1415:2031 */     this.m_calibrator = value;
/* 1416:     */   }
/* 1417:     */   
/* 1418:     */   public Classifier getCalibrator()
/* 1419:     */   {
/* 1420:2040 */     return this.m_calibrator;
/* 1421:     */   }
/* 1422:     */   
/* 1423:     */   public String cTipText()
/* 1424:     */   {
/* 1425:2049 */     return "The complexity parameter C.";
/* 1426:     */   }
/* 1427:     */   
/* 1428:     */   public double getC()
/* 1429:     */   {
/* 1430:2059 */     return this.m_C;
/* 1431:     */   }
/* 1432:     */   
/* 1433:     */   public void setC(double v)
/* 1434:     */   {
/* 1435:2069 */     this.m_C = v;
/* 1436:     */   }
/* 1437:     */   
/* 1438:     */   public String toleranceParameterTipText()
/* 1439:     */   {
/* 1440:2078 */     return "The tolerance parameter (shouldn't be changed).";
/* 1441:     */   }
/* 1442:     */   
/* 1443:     */   public double getToleranceParameter()
/* 1444:     */   {
/* 1445:2087 */     return this.m_tol;
/* 1446:     */   }
/* 1447:     */   
/* 1448:     */   public void setToleranceParameter(double v)
/* 1449:     */   {
/* 1450:2096 */     this.m_tol = v;
/* 1451:     */   }
/* 1452:     */   
/* 1453:     */   public String epsilonTipText()
/* 1454:     */   {
/* 1455:2105 */     return "The epsilon for round-off error (shouldn't be changed).";
/* 1456:     */   }
/* 1457:     */   
/* 1458:     */   public double getEpsilon()
/* 1459:     */   {
/* 1460:2114 */     return this.m_eps;
/* 1461:     */   }
/* 1462:     */   
/* 1463:     */   public void setEpsilon(double v)
/* 1464:     */   {
/* 1465:2123 */     this.m_eps = v;
/* 1466:     */   }
/* 1467:     */   
/* 1468:     */   public String filterTypeTipText()
/* 1469:     */   {
/* 1470:2132 */     return "Determines how/if the data will be transformed.";
/* 1471:     */   }
/* 1472:     */   
/* 1473:     */   public SelectedTag getFilterType()
/* 1474:     */   {
/* 1475:2143 */     return new SelectedTag(this.m_filterType, TAGS_FILTER);
/* 1476:     */   }
/* 1477:     */   
/* 1478:     */   public void setFilterType(SelectedTag newType)
/* 1479:     */   {
/* 1480:2154 */     if (newType.getTags() == TAGS_FILTER) {
/* 1481:2155 */       this.m_filterType = newType.getSelectedTag().getID();
/* 1482:     */     }
/* 1483:     */   }
/* 1484:     */   
/* 1485:     */   public String buildCalibrationModelsTipText()
/* 1486:     */   {
/* 1487:2165 */     return "Whether to fit calibration models to the SVM's outputs (for proper probability estimates).";
/* 1488:     */   }
/* 1489:     */   
/* 1490:     */   public boolean getBuildCalibrationModels()
/* 1491:     */   {
/* 1492:2175 */     return this.m_fitCalibratorModels;
/* 1493:     */   }
/* 1494:     */   
/* 1495:     */   public void setBuildCalibrationModels(boolean newbuildCalibrationModels)
/* 1496:     */   {
/* 1497:2185 */     this.m_fitCalibratorModels = newbuildCalibrationModels;
/* 1498:     */   }
/* 1499:     */   
/* 1500:     */   public String numFoldsTipText()
/* 1501:     */   {
/* 1502:2194 */     return "The number of folds for cross-validation used to generate training data for calibration models (-1 means use training data).";
/* 1503:     */   }
/* 1504:     */   
/* 1505:     */   public int getNumFolds()
/* 1506:     */   {
/* 1507:2205 */     return this.m_numFolds;
/* 1508:     */   }
/* 1509:     */   
/* 1510:     */   public void setNumFolds(int newnumFolds)
/* 1511:     */   {
/* 1512:2215 */     this.m_numFolds = newnumFolds;
/* 1513:     */   }
/* 1514:     */   
/* 1515:     */   public String randomSeedTipText()
/* 1516:     */   {
/* 1517:2224 */     return "Random number seed for the cross-validation.";
/* 1518:     */   }
/* 1519:     */   
/* 1520:     */   public int getRandomSeed()
/* 1521:     */   {
/* 1522:2234 */     return this.m_randomSeed;
/* 1523:     */   }
/* 1524:     */   
/* 1525:     */   public void setRandomSeed(int newrandomSeed)
/* 1526:     */   {
/* 1527:2244 */     this.m_randomSeed = newrandomSeed;
/* 1528:     */   }
/* 1529:     */   
/* 1530:     */   public String toString()
/* 1531:     */   {
/* 1532:2254 */     StringBuffer text = new StringBuffer();
/* 1533:2256 */     if (this.m_classAttribute == null) {
/* 1534:2257 */       return "SMO: No model built yet.";
/* 1535:     */     }
/* 1536:     */     try
/* 1537:     */     {
/* 1538:2260 */       text.append("SMO\n\n");
/* 1539:2261 */       text.append("Kernel used:\n  " + this.m_kernel.toString() + "\n\n");
/* 1540:2263 */       for (int i = 0; i < this.m_classAttribute.numValues(); i++) {
/* 1541:2264 */         for (int j = i + 1; j < this.m_classAttribute.numValues(); j++)
/* 1542:     */         {
/* 1543:2265 */           text.append("Classifier for classes: " + this.m_classAttribute.value(i) + ", " + this.m_classAttribute.value(j) + "\n\n");
/* 1544:     */           
/* 1545:     */ 
/* 1546:2268 */           text.append(this.m_classifiers[i][j]);
/* 1547:2269 */           if (this.m_fitCalibratorModels)
/* 1548:     */           {
/* 1549:2270 */             text.append("\n\n");
/* 1550:2271 */             if (this.m_classifiers[i][j].m_calibrator == null)
/* 1551:     */             {
/* 1552:2272 */               text.append("No calibration model has been fit.\n");
/* 1553:     */             }
/* 1554:     */             else
/* 1555:     */             {
/* 1556:2274 */               text.append("Calibration model fit to the output:\n");
/* 1557:2275 */               text.append(this.m_classifiers[i][j].m_calibrator);
/* 1558:     */             }
/* 1559:     */           }
/* 1560:2278 */           text.append("\n\n");
/* 1561:     */         }
/* 1562:     */       }
/* 1563:     */     }
/* 1564:     */     catch (Exception e)
/* 1565:     */     {
/* 1566:2282 */       return "Can't print SMO classifier.";
/* 1567:     */     }
/* 1568:2285 */     return text.toString();
/* 1569:     */   }
/* 1570:     */   
/* 1571:     */   public String getRevision()
/* 1572:     */   {
/* 1573:2294 */     return RevisionUtils.extract("$Revision: 12558 $");
/* 1574:     */   }
/* 1575:     */   
/* 1576:     */   public static void main(String[] argv)
/* 1577:     */   {
/* 1578:2301 */     runClassifier(new SMO(), argv);
/* 1579:     */   }
/* 1580:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.SMO
 * JD-Core Version:    0.7.0.1
 */