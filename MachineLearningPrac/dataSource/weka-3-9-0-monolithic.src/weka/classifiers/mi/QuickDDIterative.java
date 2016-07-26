/*    1:     */ package weka.classifiers.mi;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.util.Collections;
/*    5:     */ import java.util.Enumeration;
/*    6:     */ import java.util.Vector;
/*    7:     */ import weka.classifiers.AbstractClassifier;
/*    8:     */ import weka.core.Attribute;
/*    9:     */ import weka.core.Capabilities;
/*   10:     */ import weka.core.Capabilities.Capability;
/*   11:     */ import weka.core.Instance;
/*   12:     */ import weka.core.Instances;
/*   13:     */ import weka.core.MultiInstanceCapabilitiesHandler;
/*   14:     */ import weka.core.Optimization;
/*   15:     */ import weka.core.Option;
/*   16:     */ import weka.core.OptionHandler;
/*   17:     */ import weka.core.RevisionUtils;
/*   18:     */ import weka.core.SelectedTag;
/*   19:     */ import weka.core.Tag;
/*   20:     */ import weka.core.TechnicalInformation;
/*   21:     */ import weka.core.TechnicalInformation.Field;
/*   22:     */ import weka.core.TechnicalInformation.Type;
/*   23:     */ import weka.core.TechnicalInformationHandler;
/*   24:     */ import weka.core.Utils;
/*   25:     */ import weka.core.WeightedInstancesHandler;
/*   26:     */ import weka.filters.Filter;
/*   27:     */ import weka.filters.unsupervised.attribute.Normalize;
/*   28:     */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*   29:     */ import weka.filters.unsupervised.attribute.Standardize;
/*   30:     */ 
/*   31:     */ public class QuickDDIterative
/*   32:     */   extends AbstractClassifier
/*   33:     */   implements OptionHandler, MultiInstanceCapabilitiesHandler, TechnicalInformationHandler, WeightedInstancesHandler
/*   34:     */ {
/*   35:     */   static final long serialVersionUID = 4263507733600536170L;
/*   36:     */   protected int m_ClassIndex;
/*   37:     */   protected double[] m_Par;
/*   38:     */   protected double[] m_CurrentCandidate;
/*   39:     */   protected int m_NumClasses;
/*   40:     */   protected double[] m_BagWeights;
/*   41:     */   protected int[] m_Classes;
/*   42:     */   protected double[][][] m_Data;
/*   43:     */   protected Instances m_Attributes;
/*   44:     */   protected Filter m_Filter;
/*   45:     */   protected int m_filterType;
/*   46:     */   protected double m_scaleFactor;
/*   47:     */   protected int m_maxIterations;
/*   48:     */   protected double m_maxProbNegativeClass;
/*   49:     */   protected boolean m_considerBothClasses;
/*   50:     */   protected byte m_posClass;
/*   51:     */   public static final int FILTER_NORMALIZE = 0;
/*   52:     */   public static final int FILTER_STANDARDIZE = 1;
/*   53:     */   public static final int FILTER_NONE = 2;
/*   54: 187 */   public static final Tag[] TAGS_FILTER = { new Tag(0, "Normalize training data"), new Tag(1, "Standardize training data"), new Tag(2, "No normalization/standardization") };
/*   55: 195 */   protected static double m_Epsilon = 1.0D;
/*   56:     */   
/*   57:     */   static
/*   58:     */   {
/*   59: 196 */     while (1.0D + m_Epsilon > 1.0D) {
/*   60: 197 */       m_Epsilon /= 2.0D;
/*   61:     */     }
/*   62: 199 */     m_Epsilon *= 2.0D;
/*   63:     */   }
/*   64:     */   
/*   65: 200 */   protected static double m_Zero = Math.sqrt(m_Epsilon);
/*   66:     */   protected ReplaceMissingValues m_Missing;
/*   67:     */   
/*   68:     */   public QuickDDIterative()
/*   69:     */   {
/*   70: 160 */     this.m_Filter = null;
/*   71:     */     
/*   72:     */ 
/*   73: 163 */     this.m_filterType = 1;
/*   74:     */     
/*   75:     */ 
/*   76: 166 */     this.m_scaleFactor = 1.0D;
/*   77:     */     
/*   78:     */ 
/*   79: 169 */     this.m_maxIterations = 1;
/*   80:     */     
/*   81:     */ 
/*   82: 172 */     this.m_maxProbNegativeClass = 1.0D;
/*   83:     */     
/*   84:     */ 
/*   85: 175 */     this.m_considerBothClasses = false;
/*   86:     */     
/*   87:     */ 
/*   88: 178 */     this.m_posClass = 1;
/*   89:     */     
/*   90:     */ 
/*   91:     */ 
/*   92:     */ 
/*   93:     */ 
/*   94:     */ 
/*   95:     */ 
/*   96:     */ 
/*   97:     */ 
/*   98:     */ 
/*   99:     */ 
/*  100:     */ 
/*  101:     */ 
/*  102:     */ 
/*  103:     */ 
/*  104:     */ 
/*  105:     */ 
/*  106:     */ 
/*  107:     */ 
/*  108:     */ 
/*  109:     */ 
/*  110:     */ 
/*  111:     */ 
/*  112:     */ 
/*  113:     */ 
/*  114: 204 */     this.m_Missing = new ReplaceMissingValues();
/*  115:     */   }
/*  116:     */   
/*  117:     */   public String globalInfo()
/*  118:     */   {
/*  119: 213 */     return "Modified, faster, iterative version of the basic diverse density algorithm. Uses only instances from positive bags as candidate diverse density maxima. Picks best instance based on current scaling vector, then optimizes scaling vector. Once vector has been found, picks new best point based on new scaling vector (if the number of desired iterations is greater than one). Performs one iteration by default (Scaling Once). For good results, try boosting it with RealAdaBoost, setting the maximum probability of the negative class to 0.5 and enabling consideration of both classes as the positive class. Note that standardization of attributes is default, but normalization can work better.\n\n" + getTechnicalInformation().toString();
/*  120:     */   }
/*  121:     */   
/*  122:     */   public TechnicalInformation getTechnicalInformation()
/*  123:     */   {
/*  124: 236 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  125: 237 */     result.setValue(TechnicalInformation.Field.AUTHOR, "James R. Foulds and Eibe Frank");
/*  126: 238 */     result.setValue(TechnicalInformation.Field.TITLE, "Speeding up and boosting diverse density learning");
/*  127:     */     
/*  128: 240 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Proc 13th International Conference on Discovery Science");
/*  129:     */     
/*  130: 242 */     result.setValue(TechnicalInformation.Field.YEAR, "2010");
/*  131: 243 */     result.setValue(TechnicalInformation.Field.PAGES, "102-116");
/*  132: 244 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Springer");
/*  133:     */     
/*  134: 246 */     return result;
/*  135:     */   }
/*  136:     */   
/*  137:     */   public Enumeration<Option> listOptions()
/*  138:     */   {
/*  139: 257 */     Vector<Option> result = new Vector();
/*  140:     */     
/*  141: 259 */     result.addElement(new Option("\tWhether to 0=normalize/1=standardize/2=neither.\n\t(default 1=standardize)", "N", 1, "-N <num>"));
/*  142:     */     
/*  143:     */ 
/*  144:     */ 
/*  145: 263 */     result.addElement(new Option("\tThe initial scaling factor (constant for all attributes).", "S", 1, "-S <num>"));
/*  146:     */     
/*  147:     */ 
/*  148:     */ 
/*  149: 267 */     result.addElement(new Option("\tMaximum probability of negative class (default 1).", "M", 1, "-M <num>"));
/*  150:     */     
/*  151:     */ 
/*  152:     */ 
/*  153: 271 */     result.addElement(new Option("\tThe maximum number of iterations to perform (default 1).", "I", 1, "-I <num>"));
/*  154:     */     
/*  155:     */ 
/*  156:     */ 
/*  157: 275 */     result.addElement(new Option("\tConsider both classes as positive classes. (default: only last class).", "C", 0, "-C"));
/*  158:     */     
/*  159:     */ 
/*  160:     */ 
/*  161:     */ 
/*  162: 280 */     result.addAll(Collections.list(super.listOptions()));
/*  163:     */     
/*  164: 282 */     return result.elements();
/*  165:     */   }
/*  166:     */   
/*  167:     */   public void setOptions(String[] options)
/*  168:     */     throws Exception
/*  169:     */   {
/*  170: 326 */     String nString = Utils.getOption('N', options);
/*  171: 327 */     if (nString.length() != 0) {
/*  172: 328 */       setFilterType(new SelectedTag(Integer.parseInt(nString), TAGS_FILTER));
/*  173:     */     } else {
/*  174: 330 */       setFilterType(new SelectedTag(1, TAGS_FILTER));
/*  175:     */     }
/*  176: 332 */     String sString = Utils.getOption('S', options);
/*  177: 333 */     if (sString.length() != 0) {
/*  178: 334 */       setScalingFactor(Double.parseDouble(sString));
/*  179:     */     } else {
/*  180: 336 */       setScalingFactor(1.0D);
/*  181:     */     }
/*  182: 338 */     String rString = Utils.getOption('M', options);
/*  183: 339 */     if (rString.length() != 0) {
/*  184: 340 */       setMaxProbNegativeClass(Double.parseDouble(rString));
/*  185:     */     } else {
/*  186: 342 */       setMaxProbNegativeClass(1.0D);
/*  187:     */     }
/*  188: 344 */     String iString = Utils.getOption('I', options);
/*  189: 345 */     if (iString.length() != 0) {
/*  190: 346 */       setMaxIterations(Integer.parseInt(iString));
/*  191:     */     } else {
/*  192: 348 */       setMaxIterations(1);
/*  193:     */     }
/*  194: 351 */     setConsiderBothClasses(Utils.getFlag('C', options));
/*  195:     */     
/*  196: 353 */     super.setOptions(options);
/*  197:     */     
/*  198: 355 */     Utils.checkForRemainingOptions(options);
/*  199:     */   }
/*  200:     */   
/*  201:     */   public String[] getOptions()
/*  202:     */   {
/*  203: 366 */     Vector<String> result = new Vector();
/*  204:     */     
/*  205: 368 */     result.add("-N");
/*  206: 369 */     result.add("" + this.m_filterType);
/*  207: 370 */     result.add("-S");
/*  208: 371 */     result.add("" + this.m_scaleFactor);
/*  209: 372 */     result.add("-M");
/*  210: 373 */     result.add("" + this.m_maxProbNegativeClass);
/*  211: 374 */     result.add("-I");
/*  212: 375 */     result.add("" + this.m_maxIterations);
/*  213: 376 */     if (getConsiderBothClasses()) {
/*  214: 377 */       result.add("-C");
/*  215:     */     }
/*  216: 380 */     Collections.addAll(result, super.getOptions());
/*  217:     */     
/*  218: 382 */     return (String[])result.toArray(new String[result.size()]);
/*  219:     */   }
/*  220:     */   
/*  221:     */   public String filterTypeTipText()
/*  222:     */   {
/*  223: 392 */     return "The filter type for transforming the training data.";
/*  224:     */   }
/*  225:     */   
/*  226:     */   public SelectedTag getFilterType()
/*  227:     */   {
/*  228: 402 */     return new SelectedTag(this.m_filterType, TAGS_FILTER);
/*  229:     */   }
/*  230:     */   
/*  231:     */   public void setFilterType(SelectedTag newType)
/*  232:     */   {
/*  233: 413 */     if (newType.getTags() == TAGS_FILTER) {
/*  234: 414 */       this.m_filterType = newType.getSelectedTag().getID();
/*  235:     */     }
/*  236:     */   }
/*  237:     */   
/*  238:     */   public String scalingFactorTipText()
/*  239:     */   {
/*  240: 425 */     return "The initial value of the scaling factor for all attributes.";
/*  241:     */   }
/*  242:     */   
/*  243:     */   public void setScalingFactor(double scale)
/*  244:     */   {
/*  245: 436 */     this.m_scaleFactor = scale;
/*  246:     */   }
/*  247:     */   
/*  248:     */   public double getScalingFactor()
/*  249:     */   {
/*  250: 445 */     return this.m_scaleFactor;
/*  251:     */   }
/*  252:     */   
/*  253:     */   public String maxProbNegativeClassTipText()
/*  254:     */   {
/*  255: 455 */     return "The maximum probability for the negative class.";
/*  256:     */   }
/*  257:     */   
/*  258:     */   public void setMaxProbNegativeClass(double r)
/*  259:     */   {
/*  260: 462 */     this.m_maxProbNegativeClass = r;
/*  261:     */   }
/*  262:     */   
/*  263:     */   public double getMaxProbNegativeClass()
/*  264:     */   {
/*  265: 469 */     return this.m_maxProbNegativeClass;
/*  266:     */   }
/*  267:     */   
/*  268:     */   public String considerBothClassesTipText()
/*  269:     */   {
/*  270: 479 */     return "Whether to run the algorithm once for each class.";
/*  271:     */   }
/*  272:     */   
/*  273:     */   public void setConsiderBothClasses(boolean b)
/*  274:     */   {
/*  275: 486 */     this.m_considerBothClasses = b;
/*  276:     */   }
/*  277:     */   
/*  278:     */   public boolean getConsiderBothClasses()
/*  279:     */   {
/*  280: 493 */     return this.m_considerBothClasses;
/*  281:     */   }
/*  282:     */   
/*  283:     */   public String maxIterationsTipText()
/*  284:     */   {
/*  285: 503 */     return "The maximum number of iterations to perform.";
/*  286:     */   }
/*  287:     */   
/*  288:     */   public void setMaxIterations(int maxIterations)
/*  289:     */   {
/*  290: 510 */     this.m_maxIterations = maxIterations;
/*  291:     */   }
/*  292:     */   
/*  293:     */   public int getMaxIterations()
/*  294:     */   {
/*  295: 517 */     return this.m_maxIterations;
/*  296:     */   }
/*  297:     */   
/*  298:     */   protected double computeNegativeLogLikelihood(double[] x)
/*  299:     */   {
/*  300: 531 */     double nll = 0.0D;
/*  301: 532 */     for (int i = 0; i < this.m_Classes.length; i++)
/*  302:     */     {
/*  303: 533 */       int nI = this.m_Data[i][0].length;
/*  304: 534 */       double bag = 0.0D;
/*  305: 536 */       for (int j = 0; j < nI; j++)
/*  306:     */       {
/*  307: 537 */         double ins = 0.0D;
/*  308: 538 */         for (int k = 0; k < this.m_Data[i].length; k++)
/*  309:     */         {
/*  310: 539 */           double temp = (this.m_Data[i][k][j] - this.m_CurrentCandidate[k]) * x[k];
/*  311: 540 */           ins += temp * temp;
/*  312:     */         }
/*  313: 542 */         ins = Math.exp(-ins);
/*  314: 543 */         ins = 1.0D - ins;
/*  315: 545 */         if (this.m_Classes[i] == this.m_posClass)
/*  316:     */         {
/*  317: 546 */           bag += Math.log(ins);
/*  318:     */         }
/*  319:     */         else
/*  320:     */         {
/*  321: 548 */           if (ins <= m_Zero) {
/*  322: 549 */             ins = m_Zero;
/*  323:     */           }
/*  324: 551 */           bag += Math.log(ins);
/*  325:     */         }
/*  326:     */       }
/*  327: 555 */       if (this.m_Classes[i] == this.m_posClass)
/*  328:     */       {
/*  329: 556 */         bag = 1.0D - this.m_maxProbNegativeClass * Math.exp(bag);
/*  330: 557 */         if (bag <= m_Zero) {
/*  331: 558 */           bag = m_Zero;
/*  332:     */         }
/*  333: 560 */         nll -= this.m_BagWeights[i] * Math.log(bag);
/*  334:     */       }
/*  335:     */       else
/*  336:     */       {
/*  337: 562 */         bag = this.m_maxProbNegativeClass * Math.exp(bag);
/*  338: 563 */         if (bag <= m_Zero) {
/*  339: 564 */           bag = m_Zero;
/*  340:     */         }
/*  341: 566 */         nll -= this.m_BagWeights[i] * Math.log(bag);
/*  342:     */       }
/*  343:     */     }
/*  344: 570 */     return nll;
/*  345:     */   }
/*  346:     */   
/*  347:     */   private class OptEng
/*  348:     */     extends Optimization
/*  349:     */   {
/*  350:     */     private OptEng() {}
/*  351:     */     
/*  352:     */     protected double objectiveFunction(double[] x)
/*  353:     */     {
/*  354: 590 */       return QuickDDIterative.this.computeNegativeLogLikelihood(x);
/*  355:     */     }
/*  356:     */     
/*  357:     */     protected double[] evaluateGradient(double[] x)
/*  358:     */     {
/*  359: 602 */       double[] grad = new double[x.length];
/*  360: 603 */       for (int i = 0; i < QuickDDIterative.this.m_Classes.length; i++)
/*  361:     */       {
/*  362: 604 */         int nI = QuickDDIterative.this.m_Data[i][0].length;
/*  363:     */         
/*  364: 606 */         double denom = 0.0D;
/*  365: 607 */         double[] numrt = new double[x.length];
/*  366: 609 */         for (int j = 0; j < nI; j++)
/*  367:     */         {
/*  368: 610 */           double exp = 0.0D;
/*  369: 611 */           for (int k = 0; k < QuickDDIterative.this.m_Data[i].length; k++)
/*  370:     */           {
/*  371: 612 */             double temp = (QuickDDIterative.this.m_Data[i][k][j] - QuickDDIterative.this.m_CurrentCandidate[k]) * x[k];
/*  372: 613 */             exp += temp * temp;
/*  373:     */           }
/*  374: 615 */           exp = Math.exp(-exp);
/*  375: 616 */           exp = 1.0D - exp;
/*  376: 617 */           if (QuickDDIterative.this.m_Classes[i] == QuickDDIterative.this.m_posClass) {
/*  377: 618 */             denom += Math.log(exp);
/*  378:     */           }
/*  379: 621 */           if (exp <= m_Zero) {
/*  380: 622 */             exp = m_Zero;
/*  381:     */           }
/*  382: 625 */           double fact = 2.0D * (1.0D - exp) / exp;
/*  383: 626 */           for (int p = 0; p < QuickDDIterative.this.m_Data[i].length; p++)
/*  384:     */           {
/*  385: 627 */             double temp = QuickDDIterative.this.m_CurrentCandidate[p] - QuickDDIterative.this.m_Data[i][p][j];
/*  386: 628 */             numrt[p] += fact * temp * temp * x[p];
/*  387:     */           }
/*  388:     */         }
/*  389: 633 */         denom = 1.0D - QuickDDIterative.this.m_maxProbNegativeClass * Math.exp(denom);
/*  390: 634 */         if (denom <= m_Zero) {
/*  391: 635 */           denom = m_Zero;
/*  392:     */         }
/*  393: 637 */         for (int q = 0; q < QuickDDIterative.this.m_Data[i].length; q++) {
/*  394: 638 */           if (QuickDDIterative.this.m_Classes[i] == QuickDDIterative.this.m_posClass) {
/*  395: 639 */             grad[q] += QuickDDIterative.this.m_BagWeights[i] * numrt[q] * (1.0D - denom) / denom;
/*  396:     */           } else {
/*  397: 641 */             grad[q] -= QuickDDIterative.this.m_BagWeights[i] * numrt[q];
/*  398:     */           }
/*  399:     */         }
/*  400:     */       }
/*  401: 647 */       return grad;
/*  402:     */     }
/*  403:     */     
/*  404:     */     public String getRevision()
/*  405:     */     {
/*  406: 655 */       return RevisionUtils.extract("$Revision: 10369 $");
/*  407:     */     }
/*  408:     */   }
/*  409:     */   
/*  410:     */   public Capabilities getCapabilities()
/*  411:     */   {
/*  412: 666 */     Capabilities result = super.getCapabilities();
/*  413: 667 */     result.disableAll();
/*  414:     */     
/*  415:     */ 
/*  416: 670 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  417: 671 */     result.enable(Capabilities.Capability.RELATIONAL_ATTRIBUTES);
/*  418:     */     
/*  419:     */ 
/*  420: 674 */     result.enable(Capabilities.Capability.BINARY_CLASS);
/*  421: 675 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  422:     */     
/*  423:     */ 
/*  424: 678 */     result.enable(Capabilities.Capability.ONLY_MULTIINSTANCE);
/*  425:     */     
/*  426: 680 */     return result;
/*  427:     */   }
/*  428:     */   
/*  429:     */   public Capabilities getMultiInstanceCapabilities()
/*  430:     */   {
/*  431: 692 */     Capabilities result = super.getCapabilities();
/*  432:     */     
/*  433:     */ 
/*  434: 695 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  435: 696 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  436: 697 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  437: 698 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  438:     */     
/*  439:     */ 
/*  440: 701 */     result.disableAllClasses();
/*  441: 702 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  442:     */     
/*  443: 704 */     return result;
/*  444:     */   }
/*  445:     */   
/*  446:     */   public void buildClassifier(Instances train)
/*  447:     */     throws Exception
/*  448:     */   {
/*  449: 717 */     getCapabilities().testWithFail(train);
/*  450:     */     
/*  451:     */ 
/*  452: 720 */     train = new Instances(train);
/*  453: 721 */     train.deleteWithMissingClass();
/*  454:     */     
/*  455: 723 */     this.m_ClassIndex = train.classIndex();
/*  456: 724 */     this.m_NumClasses = train.numClasses();
/*  457:     */     
/*  458: 726 */     int nR = train.attribute(1).relation().numAttributes();
/*  459: 727 */     int nC = train.numInstances();
/*  460:     */     
/*  461:     */ 
/*  462: 730 */     int[] bagSize = new int[nC];
/*  463: 731 */     Instances datasets = new Instances(train.attribute(1).relation(), 0);
/*  464:     */     
/*  465: 733 */     this.m_Data = new double[nC][nR][];
/*  466: 734 */     this.m_Classes = new int[nC];
/*  467: 735 */     this.m_BagWeights = new double[nC];
/*  468: 736 */     this.m_Attributes = datasets.stringFreeStructure();
/*  469: 737 */     if (this.m_Debug) {
/*  470: 738 */       System.out.println("Extracting data...");
/*  471:     */     }
/*  472: 741 */     for (int h = 0; h < nC; h++)
/*  473:     */     {
/*  474: 742 */       Instance current = train.instance(h);
/*  475: 743 */       this.m_Classes[h] = ((int)current.classValue());
/*  476: 744 */       this.m_BagWeights[h] = current.weight();
/*  477: 745 */       Instances currInsts = current.relationalValue(1);
/*  478: 746 */       for (int i = 0; i < currInsts.numInstances(); i++)
/*  479:     */       {
/*  480: 747 */         Instance inst = currInsts.instance(i);
/*  481: 748 */         datasets.add(inst);
/*  482:     */       }
/*  483: 751 */       int nI = currInsts.numInstances();
/*  484: 752 */       bagSize[h] = nI;
/*  485:     */     }
/*  486: 756 */     if (this.m_filterType == 1) {
/*  487: 757 */       this.m_Filter = new Standardize();
/*  488: 758 */     } else if (this.m_filterType == 0) {
/*  489: 759 */       this.m_Filter = new Normalize();
/*  490:     */     } else {
/*  491: 761 */       this.m_Filter = null;
/*  492:     */     }
/*  493: 764 */     if (this.m_Filter != null)
/*  494:     */     {
/*  495: 765 */       this.m_Filter.setInputFormat(datasets);
/*  496: 766 */       datasets = Filter.useFilter(datasets, this.m_Filter);
/*  497:     */     }
/*  498: 769 */     this.m_Missing.setInputFormat(datasets);
/*  499: 770 */     datasets = Filter.useFilter(datasets, this.m_Missing);
/*  500:     */     
/*  501: 772 */     int instIndex = 0;
/*  502: 773 */     int start = 0;
/*  503: 774 */     for (int h = 0; h < nC; h++)
/*  504:     */     {
/*  505: 775 */       for (int i = 0; i < datasets.numAttributes(); i++)
/*  506:     */       {
/*  507: 777 */         this.m_Data[h][i] = new double[bagSize[h]];
/*  508: 778 */         instIndex = start;
/*  509: 779 */         for (int k = 0; k < bagSize[h]; k++)
/*  510:     */         {
/*  511: 780 */           this.m_Data[h][i][k] = datasets.instance(instIndex).value(i);
/*  512: 781 */           instIndex++;
/*  513:     */         }
/*  514:     */       }
/*  515: 784 */       start = instIndex;
/*  516:     */     }
/*  517: 787 */     if (this.m_Debug) {
/*  518: 788 */       System.out.println("\nIteration History...");
/*  519:     */     }
/*  520: 791 */     double bestOverall = 1.7976931348623157E+308D;
/*  521: 792 */     double[] bestPar = new double[2 * nR];
/*  522: 793 */     byte bestPosClass = 1;
/*  523: 794 */     for (this.m_posClass = 1; ((this.m_posClass >= 0) && (this.m_considerBothClasses)) || (this.m_posClass == 1); this.m_posClass = ((byte)(this.m_posClass - 1)))
/*  524:     */     {
/*  525: 797 */       double[] tmp = new double[nR];
/*  526: 798 */       this.m_CurrentCandidate = new double[nR];
/*  527: 799 */       double[][] b = new double[2][nR];
/*  528:     */       
/*  529:     */ 
/*  530: 802 */       double bestnll = 1.7976931348623157E+308D;
/*  531: 803 */       for (int t = 0; t < nR; t++)
/*  532:     */       {
/*  533: 804 */         b[0][t] = (0.0D / 0.0D);
/*  534: 805 */         b[1][t] = (0.0D / 0.0D);
/*  535:     */       }
/*  536: 808 */       double[] scalingVector = new double[nR];
/*  537: 809 */       for (int i = 0; i < scalingVector.length; i++) {
/*  538: 810 */         scalingVector[i] = this.m_scaleFactor;
/*  539:     */       }
/*  540: 814 */       int numIterations = 0;
/*  541:     */       double lastnll;
/*  542:     */       do
/*  543:     */       {
/*  544: 816 */         numIterations++;
/*  545: 817 */         if (this.m_Debug) {
/*  546: 818 */           System.err.println("iteration " + numIterations);
/*  547:     */         }
/*  548: 820 */         lastnll = bestnll;
/*  549: 823 */         for (int exIdx = 0; exIdx < this.m_Data.length; exIdx++) {
/*  550: 826 */           if (this.m_Classes[exIdx] != this.m_posClass)
/*  551:     */           {
/*  552: 829 */             if (this.m_Debug) {
/*  553: 830 */               System.err.println(exIdx + " " + this.m_BagWeights[exIdx]);
/*  554:     */             }
/*  555:     */           }
/*  556:     */           else {
/*  557: 834 */             for (int p = 0; p < this.m_Data[exIdx][0].length; p++)
/*  558:     */             {
/*  559: 835 */               for (int q = 0; q < nR; q++) {
/*  560: 836 */                 this.m_CurrentCandidate[q] = this.m_Data[exIdx][q][p];
/*  561:     */               }
/*  562: 840 */               double nll = computeNegativeLogLikelihood(scalingVector);
/*  563: 841 */               if (this.m_Debug)
/*  564:     */               {
/*  565: 842 */                 System.err.print(exIdx + " " + p + " " + this.m_BagWeights[exIdx] + " " + nll + " ");
/*  566: 844 */                 for (int i = 0; i < nR; i++) {
/*  567: 845 */                   System.err.print(this.m_Data[exIdx][i][p] + ", ");
/*  568:     */                 }
/*  569: 847 */                 System.err.println();
/*  570:     */               }
/*  571: 850 */               if (nll < bestnll)
/*  572:     */               {
/*  573: 851 */                 bestnll = nll;
/*  574: 852 */                 this.m_Par = new double[this.m_CurrentCandidate.length * 2];
/*  575: 853 */                 for (int i = 0; i < this.m_CurrentCandidate.length; i++)
/*  576:     */                 {
/*  577: 854 */                   this.m_Par[(2 * i)] = this.m_CurrentCandidate[i];
/*  578: 855 */                   this.m_Par[(2 * i + 1)] = scalingVector[i];
/*  579:     */                 }
/*  580:     */               }
/*  581:     */             }
/*  582:     */           }
/*  583:     */         }
/*  584: 863 */         for (int i = 0; i < nR; i++) {
/*  585: 864 */           this.m_CurrentCandidate[i] = this.m_Par[(2 * i)];
/*  586:     */         }
/*  587: 866 */         if (this.m_Debug) {
/*  588: 867 */           System.err.println("********* Finding best scaling vector");
/*  589:     */         }
/*  590: 871 */         OptEng opt = new OptEng(null);
/*  591:     */         
/*  592: 873 */         tmp = opt.findArgmin(scalingVector, b);
/*  593: 874 */         while (tmp == null)
/*  594:     */         {
/*  595: 875 */           tmp = opt.getVarbValues();
/*  596: 876 */           if (this.m_Debug) {
/*  597: 877 */             System.out.println("200 iterations finished, not enough!");
/*  598:     */           }
/*  599: 879 */           tmp = opt.findArgmin(tmp, b);
/*  600:     */         }
/*  601: 881 */         double nll = opt.getMinFunction();
/*  602: 882 */         scalingVector = tmp;
/*  603: 883 */         if (nll < bestnll)
/*  604:     */         {
/*  605: 884 */           bestnll = nll;
/*  606: 885 */           this.m_Par = new double[this.m_CurrentCandidate.length * 2];
/*  607: 886 */           for (int i = 0; i < this.m_CurrentCandidate.length; i++)
/*  608:     */           {
/*  609: 887 */             this.m_Par[(2 * i)] = this.m_CurrentCandidate[i];
/*  610: 888 */             this.m_Par[(2 * i + 1)] = scalingVector[i];
/*  611:     */           }
/*  612:     */         }
/*  613: 892 */         if (this.m_Debug) {
/*  614: 893 */           System.err.println("---------------     " + bestnll);
/*  615:     */         }
/*  616: 896 */       } while ((bestnll < lastnll) && (numIterations < this.m_maxIterations));
/*  617: 899 */       if (bestnll < bestOverall)
/*  618:     */       {
/*  619: 900 */         bestPosClass = this.m_posClass;
/*  620: 901 */         bestOverall = bestnll;
/*  621: 902 */         System.arraycopy(this.m_Par, 0, bestPar, 0, bestPar.length);
/*  622: 903 */         if (this.m_Debug)
/*  623:     */         {
/*  624: 904 */           System.err.println("New best class: " + bestPosClass);
/*  625: 905 */           System.err.println("New best nll: " + bestnll);
/*  626: 906 */           for (double element : bestPar) {
/*  627: 907 */             System.err.print(element + ",");
/*  628:     */           }
/*  629: 909 */           System.err.println();
/*  630:     */         }
/*  631:     */       }
/*  632:     */     }
/*  633: 914 */     this.m_Par = bestPar;
/*  634: 915 */     this.m_posClass = bestPosClass;
/*  635:     */     
/*  636: 917 */     this.m_Data = ((double[][][])null);
/*  637: 918 */     this.m_Classes = null;
/*  638: 919 */     this.m_CurrentCandidate = null;
/*  639: 920 */     this.m_BagWeights = null;
/*  640:     */   }
/*  641:     */   
/*  642:     */   public double[] distributionForInstance(Instance exmp)
/*  643:     */     throws Exception
/*  644:     */   {
/*  645: 934 */     Instances ins = exmp.relationalValue(1);
/*  646: 935 */     if (this.m_Filter != null) {
/*  647: 936 */       ins = Filter.useFilter(ins, this.m_Filter);
/*  648:     */     }
/*  649: 939 */     ins = Filter.useFilter(ins, this.m_Missing);
/*  650:     */     
/*  651: 941 */     int nI = ins.numInstances();int nA = ins.numAttributes();
/*  652: 942 */     double[][] dat = new double[nI][nA];
/*  653: 943 */     for (int j = 0; j < nI; j++) {
/*  654: 944 */       for (int k = 0; k < nA; k++) {
/*  655: 945 */         dat[j][k] = ins.instance(j).value(k);
/*  656:     */       }
/*  657:     */     }
/*  658: 950 */     double[] distribution = new double[2];
/*  659: 951 */     distribution[(1 - this.m_posClass)] = 0.0D;
/*  660: 953 */     for (int i = 0; i < nI; i++)
/*  661:     */     {
/*  662: 954 */       double exp = 0.0D;
/*  663: 955 */       for (int r = 0; r < nA; r++) {
/*  664: 956 */         exp += (this.m_Par[(r * 2)] - dat[i][r]) * (this.m_Par[(r * 2)] - dat[i][r]) * this.m_Par[(r * 2 + 1)] * this.m_Par[(r * 2 + 1)];
/*  665:     */       }
/*  666: 959 */       exp = Math.exp(-exp);
/*  667:     */       
/*  668:     */ 
/*  669: 962 */       distribution[(1 - this.m_posClass)] += Math.log(1.0D - exp);
/*  670:     */     }
/*  671: 965 */     distribution[(1 - this.m_posClass)] = (this.m_maxProbNegativeClass * Math.exp(distribution[(1 - this.m_posClass)]));
/*  672:     */     
/*  673: 967 */     distribution[this.m_posClass] = (1.0D - distribution[(1 - this.m_posClass)]);
/*  674:     */     
/*  675: 969 */     return distribution;
/*  676:     */   }
/*  677:     */   
/*  678:     */   public String toString()
/*  679:     */   {
/*  680: 980 */     String result = "Diverse Density";
/*  681: 981 */     if (this.m_Par == null) {
/*  682: 982 */       return result + ": No model built yet.";
/*  683:     */     }
/*  684: 985 */     result = result + "\nPositive Class:" + this.m_posClass + "\n";
/*  685:     */     
/*  686: 987 */     result = result + "\nCoefficients...\nVariable       Point       Scale\n";
/*  687: 988 */     int j = 0;
/*  688: 988 */     for (int idx = 0; j < this.m_Par.length / 2; idx++)
/*  689:     */     {
/*  690: 989 */       result = result + this.m_Attributes.attribute(idx).name();
/*  691: 990 */       result = result + " " + Utils.doubleToString(this.m_Par[(j * 2)], 12, 4);
/*  692: 991 */       result = result + " " + Utils.doubleToString(this.m_Par[(j * 2 + 1)], 12, 4) + "\n";j++;
/*  693:     */     }
/*  694: 994 */     return result;
/*  695:     */   }
/*  696:     */   
/*  697:     */   public static void main(String[] argv)
/*  698:     */   {
/*  699:1004 */     runClassifier(new QuickDDIterative(), argv);
/*  700:     */   }
/*  701:     */   
/*  702:     */   public String getRevision()
/*  703:     */   {
/*  704:1012 */     return RevisionUtils.extract("$Revision: 10369 $");
/*  705:     */   }
/*  706:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.QuickDDIterative
 * JD-Core Version:    0.7.0.1
 */