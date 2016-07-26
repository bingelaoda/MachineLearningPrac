/*    1:     */ package weka.experiment;
/*    2:     */ 
/*    3:     */ import java.io.ByteArrayOutputStream;
/*    4:     */ import java.io.ObjectOutputStream;
/*    5:     */ import java.io.ObjectStreamClass;
/*    6:     */ import java.io.PrintStream;
/*    7:     */ import java.io.Serializable;
/*    8:     */ import java.lang.management.ManagementFactory;
/*    9:     */ import java.lang.management.ThreadMXBean;
/*   10:     */ import java.util.ArrayList;
/*   11:     */ import java.util.Arrays;
/*   12:     */ import java.util.Collections;
/*   13:     */ import java.util.Enumeration;
/*   14:     */ import java.util.Iterator;
/*   15:     */ import java.util.List;
/*   16:     */ import java.util.Vector;
/*   17:     */ import weka.classifiers.AbstractClassifier;
/*   18:     */ import weka.classifiers.Classifier;
/*   19:     */ import weka.classifiers.Evaluation;
/*   20:     */ import weka.classifiers.evaluation.AbstractEvaluationMetric;
/*   21:     */ import weka.classifiers.rules.ZeroR;
/*   22:     */ import weka.core.AdditionalMeasureProducer;
/*   23:     */ import weka.core.Attribute;
/*   24:     */ import weka.core.Instance;
/*   25:     */ import weka.core.Instances;
/*   26:     */ import weka.core.Option;
/*   27:     */ import weka.core.OptionHandler;
/*   28:     */ import weka.core.RevisionHandler;
/*   29:     */ import weka.core.RevisionUtils;
/*   30:     */ import weka.core.Summarizable;
/*   31:     */ import weka.core.Utils;
/*   32:     */ 
/*   33:     */ public class ClassifierSplitEvaluator
/*   34:     */   implements SplitEvaluator, OptionHandler, AdditionalMeasureProducer, RevisionHandler
/*   35:     */ {
/*   36:     */   static final long serialVersionUID = -8511241602760467265L;
/*   37: 118 */   protected Classifier m_Template = new ZeroR();
/*   38:     */   protected Classifier m_Classifier;
/*   39:     */   protected Evaluation m_Evaluation;
/*   40: 127 */   protected String[] m_AdditionalMeasures = null;
/*   41: 134 */   protected boolean[] m_doesProduce = null;
/*   42: 141 */   protected int m_numberAdditionalMeasures = 0;
/*   43: 144 */   protected String m_result = null;
/*   44: 147 */   protected String m_ClassifierOptions = "";
/*   45: 150 */   protected String m_ClassifierVersion = "";
/*   46:     */   private static final int KEY_SIZE = 3;
/*   47:     */   private static final int RESULT_SIZE = 32;
/*   48:     */   private static final int NUM_IR_STATISTICS = 16;
/*   49:     */   private static final int NUM_WEIGHTED_IR_STATISTICS = 10;
/*   50:     */   private static final int NUM_UNWEIGHTED_IR_STATISTICS = 2;
/*   51: 168 */   private int m_IRclass = 0;
/*   52: 171 */   private boolean m_predTargetColumn = false;
/*   53: 174 */   private int m_attID = -1;
/*   54:     */   private boolean m_NoSizeDetermination;
/*   55: 179 */   protected final List<AbstractEvaluationMetric> m_pluginMetrics = new ArrayList();
/*   56: 181 */   protected int m_numPluginStatistics = 0;
/*   57:     */   
/*   58:     */   public ClassifierSplitEvaluator()
/*   59:     */   {
/*   60: 188 */     updateOptions();
/*   61:     */     
/*   62: 190 */     List<AbstractEvaluationMetric> pluginMetrics = AbstractEvaluationMetric.getPluginMetrics();
/*   63: 192 */     if (pluginMetrics != null) {
/*   64: 193 */       for (AbstractEvaluationMetric m : pluginMetrics)
/*   65:     */       {
/*   66: 194 */         System.err.println(m.getMetricName());
/*   67: 195 */         if (m.appliesToNominalClass())
/*   68:     */         {
/*   69: 196 */           this.m_pluginMetrics.add(m);
/*   70: 197 */           this.m_numPluginStatistics += m.getStatisticNames().size();
/*   71:     */         }
/*   72:     */       }
/*   73:     */     }
/*   74:     */   }
/*   75:     */   
/*   76:     */   public String globalInfo()
/*   77:     */   {
/*   78: 210 */     return " A SplitEvaluator that produces results for a classification scheme on a nominal class attribute.";
/*   79:     */   }
/*   80:     */   
/*   81:     */   public Enumeration<Option> listOptions()
/*   82:     */   {
/*   83: 222 */     Vector<Option> newVector = new Vector(5);
/*   84:     */     
/*   85: 224 */     newVector.addElement(new Option("\tThe full class name of the classifier.\n\teg: weka.classifiers.bayes.NaiveBayes", "W", 1, "-W <class name>"));
/*   86:     */     
/*   87:     */ 
/*   88: 227 */     newVector.addElement(new Option("\tThe index of the class for which IR statistics\n\tare to be output. (default 1)", "C", 1, "-C <index>"));
/*   89:     */     
/*   90:     */ 
/*   91: 230 */     newVector.addElement(new Option("\tThe index of an attribute to output in the\n\tresults. This attribute should identify an\n\tinstance in order to know which instances are\n\tin the test set of a cross validation. if 0\n\tno output (default 0).", "I", 1, "-I <index>"));
/*   92:     */     
/*   93:     */ 
/*   94:     */ 
/*   95:     */ 
/*   96:     */ 
/*   97: 236 */     newVector.addElement(new Option("\tAdd target and prediction columns to the result\n\tfor each fold.", "P", 0, "-P"));
/*   98:     */     
/*   99:     */ 
/*  100: 239 */     newVector.addElement(new Option("\tSkips the determination of sizes (train/test/classifier)\n\t(default: sizes are determined)", "no-size", 0, "-no-size"));
/*  101: 243 */     if ((this.m_Template != null) && ((this.m_Template instanceof OptionHandler)))
/*  102:     */     {
/*  103: 244 */       newVector.addElement(new Option("", "", 0, "\nOptions specific to classifier " + this.m_Template.getClass().getName() + ":"));
/*  104:     */       
/*  105:     */ 
/*  106: 247 */       newVector.addAll(Collections.list(((OptionHandler)this.m_Template).listOptions()));
/*  107:     */     }
/*  108: 251 */     return newVector.elements();
/*  109:     */   }
/*  110:     */   
/*  111:     */   public void setOptions(String[] options)
/*  112:     */     throws Exception
/*  113:     */   {
/*  114: 314 */     String cName = Utils.getOption('W', options);
/*  115: 315 */     if (cName.length() == 0) {
/*  116: 316 */       throw new Exception("A classifier must be specified with the -W option.");
/*  117:     */     }
/*  118: 322 */     setClassifier(AbstractClassifier.forName(cName, null));
/*  119: 323 */     if ((getClassifier() instanceof OptionHandler))
/*  120:     */     {
/*  121: 324 */       ((OptionHandler)getClassifier()).setOptions(Utils.partitionOptions(options));
/*  122:     */       
/*  123: 326 */       updateOptions();
/*  124:     */     }
/*  125: 329 */     String indexName = Utils.getOption('C', options);
/*  126: 330 */     if (indexName.length() != 0) {
/*  127: 331 */       this.m_IRclass = (new Integer(indexName).intValue() - 1);
/*  128:     */     } else {
/*  129: 333 */       this.m_IRclass = 0;
/*  130:     */     }
/*  131: 336 */     String attID = Utils.getOption('I', options);
/*  132: 337 */     if (attID.length() != 0) {
/*  133: 338 */       this.m_attID = (new Integer(attID).intValue() - 1);
/*  134:     */     } else {
/*  135: 340 */       this.m_attID = -1;
/*  136:     */     }
/*  137: 343 */     this.m_predTargetColumn = Utils.getFlag('P', options);
/*  138: 344 */     this.m_NoSizeDetermination = Utils.getFlag("no-size", options);
/*  139:     */   }
/*  140:     */   
/*  141:     */   public String[] getOptions()
/*  142:     */   {
/*  143: 357 */     Vector<String> result = new Vector();
/*  144:     */     
/*  145: 359 */     String[] classifierOptions = new String[0];
/*  146: 360 */     if ((this.m_Template != null) && ((this.m_Template instanceof OptionHandler))) {
/*  147: 361 */       classifierOptions = ((OptionHandler)this.m_Template).getOptions();
/*  148:     */     }
/*  149: 364 */     if (getClassifier() != null)
/*  150:     */     {
/*  151: 365 */       result.add("-W");
/*  152: 366 */       result.add(getClassifier().getClass().getName());
/*  153:     */     }
/*  154: 368 */     result.add("-I");
/*  155: 369 */     result.add("" + (this.m_attID + 1));
/*  156: 371 */     if (getPredTargetColumn()) {
/*  157: 372 */       result.add("-P");
/*  158:     */     }
/*  159: 375 */     result.add("-C");
/*  160: 376 */     result.add("" + (this.m_IRclass + 1));
/*  161: 378 */     if (getNoSizeDetermination()) {
/*  162: 379 */       result.add("-no-size");
/*  163:     */     }
/*  164: 382 */     result.add("--");
/*  165: 383 */     result.addAll(Arrays.asList(classifierOptions));
/*  166:     */     
/*  167: 385 */     return (String[])result.toArray(new String[result.size()]);
/*  168:     */   }
/*  169:     */   
/*  170:     */   public void setAdditionalMeasures(String[] additionalMeasures)
/*  171:     */   {
/*  172: 399 */     this.m_AdditionalMeasures = additionalMeasures;
/*  173: 403 */     if ((this.m_AdditionalMeasures != null) && (this.m_AdditionalMeasures.length > 0))
/*  174:     */     {
/*  175: 404 */       this.m_doesProduce = new boolean[this.m_AdditionalMeasures.length];
/*  176: 406 */       if ((this.m_Template instanceof AdditionalMeasureProducer))
/*  177:     */       {
/*  178: 407 */         Enumeration<String> en = ((AdditionalMeasureProducer)this.m_Template).enumerateMeasures();
/*  179: 409 */         while (en.hasMoreElements())
/*  180:     */         {
/*  181: 410 */           String mname = (String)en.nextElement();
/*  182: 411 */           for (int j = 0; j < this.m_AdditionalMeasures.length; j++) {
/*  183: 412 */             if (mname.compareToIgnoreCase(this.m_AdditionalMeasures[j]) == 0) {
/*  184: 413 */               this.m_doesProduce[j] = true;
/*  185:     */             }
/*  186:     */           }
/*  187:     */         }
/*  188:     */       }
/*  189:     */     }
/*  190:     */     else
/*  191:     */     {
/*  192: 419 */       this.m_doesProduce = null;
/*  193:     */     }
/*  194:     */   }
/*  195:     */   
/*  196:     */   public Enumeration<String> enumerateMeasures()
/*  197:     */   {
/*  198: 431 */     Vector<String> newVector = new Vector();
/*  199: 432 */     if ((this.m_Template instanceof AdditionalMeasureProducer))
/*  200:     */     {
/*  201: 433 */       Enumeration<String> en = ((AdditionalMeasureProducer)this.m_Template).enumerateMeasures();
/*  202: 435 */       while (en.hasMoreElements())
/*  203:     */       {
/*  204: 436 */         String mname = (String)en.nextElement();
/*  205: 437 */         newVector.add(mname);
/*  206:     */       }
/*  207:     */     }
/*  208: 440 */     return newVector.elements();
/*  209:     */   }
/*  210:     */   
/*  211:     */   public double getMeasure(String additionalMeasureName)
/*  212:     */   {
/*  213: 452 */     if ((this.m_Template instanceof AdditionalMeasureProducer))
/*  214:     */     {
/*  215: 453 */       if (this.m_Classifier == null) {
/*  216: 454 */         throw new IllegalArgumentException("ClassifierSplitEvaluator: Can't return result for measure, classifier has not been built yet.");
/*  217:     */       }
/*  218: 458 */       return ((AdditionalMeasureProducer)this.m_Classifier).getMeasure(additionalMeasureName);
/*  219:     */     }
/*  220: 461 */     throw new IllegalArgumentException("ClassifierSplitEvaluator: Can't return value for : " + additionalMeasureName + ". " + this.m_Template.getClass().getName() + " " + "is not an AdditionalMeasureProducer");
/*  221:     */   }
/*  222:     */   
/*  223:     */   public Object[] getKeyTypes()
/*  224:     */   {
/*  225: 478 */     Object[] keyTypes = new Object[3];
/*  226: 479 */     keyTypes[0] = "";
/*  227: 480 */     keyTypes[1] = "";
/*  228: 481 */     keyTypes[2] = "";
/*  229: 482 */     return keyTypes;
/*  230:     */   }
/*  231:     */   
/*  232:     */   public String[] getKeyNames()
/*  233:     */   {
/*  234: 494 */     String[] keyNames = new String[3];
/*  235: 495 */     keyNames[0] = "Scheme";
/*  236: 496 */     keyNames[1] = "Scheme_options";
/*  237: 497 */     keyNames[2] = "Scheme_version_ID";
/*  238: 498 */     return keyNames;
/*  239:     */   }
/*  240:     */   
/*  241:     */   public Object[] getKey()
/*  242:     */   {
/*  243: 512 */     Object[] key = new Object[3];
/*  244: 513 */     key[0] = this.m_Template.getClass().getName();
/*  245: 514 */     key[1] = this.m_ClassifierOptions;
/*  246: 515 */     key[2] = this.m_ClassifierVersion;
/*  247: 516 */     return key;
/*  248:     */   }
/*  249:     */   
/*  250:     */   public Object[] getResultTypes()
/*  251:     */   {
/*  252: 529 */     int addm = this.m_AdditionalMeasures != null ? this.m_AdditionalMeasures.length : 0;
/*  253: 530 */     int overall_length = 32 + addm;
/*  254: 531 */     overall_length += 16;
/*  255: 532 */     overall_length += 10;
/*  256: 533 */     overall_length += 2;
/*  257: 535 */     if (getAttributeID() >= 0) {
/*  258: 536 */       overall_length++;
/*  259:     */     }
/*  260: 538 */     if (getPredTargetColumn()) {
/*  261: 539 */       overall_length += 2;
/*  262:     */     }
/*  263: 542 */     overall_length += this.m_numPluginStatistics;
/*  264:     */     
/*  265: 544 */     Object[] resultTypes = new Object[overall_length];
/*  266: 545 */     Double doub = new Double(0.0D);
/*  267: 546 */     int current = 0;
/*  268: 547 */     resultTypes[(current++)] = doub;
/*  269: 548 */     resultTypes[(current++)] = doub;
/*  270:     */     
/*  271: 550 */     resultTypes[(current++)] = doub;
/*  272: 551 */     resultTypes[(current++)] = doub;
/*  273: 552 */     resultTypes[(current++)] = doub;
/*  274: 553 */     resultTypes[(current++)] = doub;
/*  275: 554 */     resultTypes[(current++)] = doub;
/*  276: 555 */     resultTypes[(current++)] = doub;
/*  277:     */     
/*  278: 557 */     resultTypes[(current++)] = doub;
/*  279: 558 */     resultTypes[(current++)] = doub;
/*  280: 559 */     resultTypes[(current++)] = doub;
/*  281: 560 */     resultTypes[(current++)] = doub;
/*  282:     */     
/*  283: 562 */     resultTypes[(current++)] = doub;
/*  284: 563 */     resultTypes[(current++)] = doub;
/*  285: 564 */     resultTypes[(current++)] = doub;
/*  286: 565 */     resultTypes[(current++)] = doub;
/*  287: 566 */     resultTypes[(current++)] = doub;
/*  288: 567 */     resultTypes[(current++)] = doub;
/*  289:     */     
/*  290: 569 */     resultTypes[(current++)] = doub;
/*  291: 570 */     resultTypes[(current++)] = doub;
/*  292: 571 */     resultTypes[(current++)] = doub;
/*  293: 572 */     resultTypes[(current++)] = doub;
/*  294:     */     
/*  295:     */ 
/*  296: 575 */     resultTypes[(current++)] = doub;
/*  297: 576 */     resultTypes[(current++)] = doub;
/*  298: 577 */     resultTypes[(current++)] = doub;
/*  299: 578 */     resultTypes[(current++)] = doub;
/*  300: 579 */     resultTypes[(current++)] = doub;
/*  301: 580 */     resultTypes[(current++)] = doub;
/*  302: 581 */     resultTypes[(current++)] = doub;
/*  303: 582 */     resultTypes[(current++)] = doub;
/*  304: 583 */     resultTypes[(current++)] = doub;
/*  305: 584 */     resultTypes[(current++)] = doub;
/*  306: 585 */     resultTypes[(current++)] = doub;
/*  307: 586 */     resultTypes[(current++)] = doub;
/*  308: 587 */     resultTypes[(current++)] = doub;
/*  309: 588 */     resultTypes[(current++)] = doub;
/*  310:     */     
/*  311:     */ 
/*  312: 591 */     resultTypes[(current++)] = doub;
/*  313: 592 */     resultTypes[(current++)] = doub;
/*  314:     */     
/*  315:     */ 
/*  316: 595 */     resultTypes[(current++)] = doub;
/*  317: 596 */     resultTypes[(current++)] = doub;
/*  318: 597 */     resultTypes[(current++)] = doub;
/*  319: 598 */     resultTypes[(current++)] = doub;
/*  320: 599 */     resultTypes[(current++)] = doub;
/*  321: 600 */     resultTypes[(current++)] = doub;
/*  322: 601 */     resultTypes[(current++)] = doub;
/*  323: 602 */     resultTypes[(current++)] = doub;
/*  324: 603 */     resultTypes[(current++)] = doub;
/*  325: 604 */     resultTypes[(current++)] = doub;
/*  326:     */     
/*  327:     */ 
/*  328: 607 */     resultTypes[(current++)] = doub;
/*  329: 608 */     resultTypes[(current++)] = doub;
/*  330: 609 */     resultTypes[(current++)] = doub;
/*  331: 610 */     resultTypes[(current++)] = doub;
/*  332: 611 */     resultTypes[(current++)] = doub;
/*  333: 612 */     resultTypes[(current++)] = doub;
/*  334:     */     
/*  335:     */ 
/*  336: 615 */     resultTypes[(current++)] = doub;
/*  337: 616 */     resultTypes[(current++)] = doub;
/*  338: 617 */     resultTypes[(current++)] = doub;
/*  339:     */     
/*  340:     */ 
/*  341: 620 */     resultTypes[(current++)] = doub;
/*  342: 621 */     resultTypes[(current++)] = doub;
/*  343: 624 */     if (getAttributeID() >= 0) {
/*  344: 625 */       resultTypes[(current++)] = "";
/*  345:     */     }
/*  346: 627 */     if (getPredTargetColumn())
/*  347:     */     {
/*  348: 628 */       resultTypes[(current++)] = "";
/*  349: 629 */       resultTypes[(current++)] = "";
/*  350:     */     }
/*  351: 633 */     resultTypes[(current++)] = "";
/*  352: 636 */     for (int i = 0; i < addm; i++) {
/*  353: 637 */       resultTypes[(current++)] = doub;
/*  354:     */     }
/*  355: 641 */     for (int i = 0; i < this.m_numPluginStatistics; i++) {
/*  356: 642 */       resultTypes[(current++)] = doub;
/*  357:     */     }
/*  358: 645 */     if (current != overall_length) {
/*  359: 646 */       throw new Error("ResultTypes didn't fit RESULT_SIZE");
/*  360:     */     }
/*  361: 648 */     return resultTypes;
/*  362:     */   }
/*  363:     */   
/*  364:     */   public String[] getResultNames()
/*  365:     */   {
/*  366: 659 */     int addm = this.m_AdditionalMeasures != null ? this.m_AdditionalMeasures.length : 0;
/*  367: 660 */     int overall_length = 32 + addm;
/*  368: 661 */     overall_length += 16;
/*  369: 662 */     overall_length += 10;
/*  370: 663 */     overall_length += 2;
/*  371: 664 */     if (getAttributeID() >= 0) {
/*  372: 665 */       overall_length++;
/*  373:     */     }
/*  374: 667 */     if (getPredTargetColumn()) {
/*  375: 668 */       overall_length += 2;
/*  376:     */     }
/*  377: 671 */     overall_length += this.m_numPluginStatistics;
/*  378:     */     
/*  379: 673 */     String[] resultNames = new String[overall_length];
/*  380: 674 */     int current = 0;
/*  381: 675 */     resultNames[(current++)] = "Number_of_training_instances";
/*  382: 676 */     resultNames[(current++)] = "Number_of_testing_instances";
/*  383:     */     
/*  384:     */ 
/*  385: 679 */     resultNames[(current++)] = "Number_correct";
/*  386: 680 */     resultNames[(current++)] = "Number_incorrect";
/*  387: 681 */     resultNames[(current++)] = "Number_unclassified";
/*  388: 682 */     resultNames[(current++)] = "Percent_correct";
/*  389: 683 */     resultNames[(current++)] = "Percent_incorrect";
/*  390: 684 */     resultNames[(current++)] = "Percent_unclassified";
/*  391: 685 */     resultNames[(current++)] = "Kappa_statistic";
/*  392:     */     
/*  393:     */ 
/*  394: 688 */     resultNames[(current++)] = "Mean_absolute_error";
/*  395: 689 */     resultNames[(current++)] = "Root_mean_squared_error";
/*  396: 690 */     resultNames[(current++)] = "Relative_absolute_error";
/*  397: 691 */     resultNames[(current++)] = "Root_relative_squared_error";
/*  398:     */     
/*  399:     */ 
/*  400: 694 */     resultNames[(current++)] = "SF_prior_entropy";
/*  401: 695 */     resultNames[(current++)] = "SF_scheme_entropy";
/*  402: 696 */     resultNames[(current++)] = "SF_entropy_gain";
/*  403: 697 */     resultNames[(current++)] = "SF_mean_prior_entropy";
/*  404: 698 */     resultNames[(current++)] = "SF_mean_scheme_entropy";
/*  405: 699 */     resultNames[(current++)] = "SF_mean_entropy_gain";
/*  406:     */     
/*  407:     */ 
/*  408: 702 */     resultNames[(current++)] = "KB_information";
/*  409: 703 */     resultNames[(current++)] = "KB_mean_information";
/*  410: 704 */     resultNames[(current++)] = "KB_relative_information";
/*  411:     */     
/*  412:     */ 
/*  413: 707 */     resultNames[(current++)] = "True_positive_rate";
/*  414: 708 */     resultNames[(current++)] = "Num_true_positives";
/*  415: 709 */     resultNames[(current++)] = "False_positive_rate";
/*  416: 710 */     resultNames[(current++)] = "Num_false_positives";
/*  417: 711 */     resultNames[(current++)] = "True_negative_rate";
/*  418: 712 */     resultNames[(current++)] = "Num_true_negatives";
/*  419: 713 */     resultNames[(current++)] = "False_negative_rate";
/*  420: 714 */     resultNames[(current++)] = "Num_false_negatives";
/*  421: 715 */     resultNames[(current++)] = "IR_precision";
/*  422: 716 */     resultNames[(current++)] = "IR_recall";
/*  423: 717 */     resultNames[(current++)] = "F_measure";
/*  424: 718 */     resultNames[(current++)] = "Matthews_correlation";
/*  425: 719 */     resultNames[(current++)] = "Area_under_ROC";
/*  426: 720 */     resultNames[(current++)] = "Area_under_PRC";
/*  427:     */     
/*  428:     */ 
/*  429: 723 */     resultNames[(current++)] = "Weighted_avg_true_positive_rate";
/*  430: 724 */     resultNames[(current++)] = "Weighted_avg_false_positive_rate";
/*  431: 725 */     resultNames[(current++)] = "Weighted_avg_true_negative_rate";
/*  432: 726 */     resultNames[(current++)] = "Weighted_avg_false_negative_rate";
/*  433: 727 */     resultNames[(current++)] = "Weighted_avg_IR_precision";
/*  434: 728 */     resultNames[(current++)] = "Weighted_avg_IR_recall";
/*  435: 729 */     resultNames[(current++)] = "Weighted_avg_F_measure";
/*  436: 730 */     resultNames[(current++)] = "Weighted_avg_matthews_correlation";
/*  437: 731 */     resultNames[(current++)] = "Weighted_avg_area_under_ROC";
/*  438: 732 */     resultNames[(current++)] = "Weighted_avg_area_under_PRC";
/*  439:     */     
/*  440:     */ 
/*  441: 735 */     resultNames[(current++)] = "Unweighted_macro_avg_F_measure";
/*  442: 736 */     resultNames[(current++)] = "Unweighted_micro_avg_F_measure";
/*  443:     */     
/*  444:     */ 
/*  445: 739 */     resultNames[(current++)] = "Elapsed_Time_training";
/*  446: 740 */     resultNames[(current++)] = "Elapsed_Time_testing";
/*  447: 741 */     resultNames[(current++)] = "UserCPU_Time_training";
/*  448: 742 */     resultNames[(current++)] = "UserCPU_Time_testing";
/*  449: 743 */     resultNames[(current++)] = "UserCPU_Time_millis_training";
/*  450: 744 */     resultNames[(current++)] = "UserCPU_Time_millis_testing";
/*  451:     */     
/*  452:     */ 
/*  453: 747 */     resultNames[(current++)] = "Serialized_Model_Size";
/*  454: 748 */     resultNames[(current++)] = "Serialized_Train_Set_Size";
/*  455: 749 */     resultNames[(current++)] = "Serialized_Test_Set_Size";
/*  456:     */     
/*  457:     */ 
/*  458: 752 */     resultNames[(current++)] = "Coverage_of_Test_Cases_By_Regions";
/*  459: 753 */     resultNames[(current++)] = "Size_of_Predicted_Regions";
/*  460: 756 */     if (getAttributeID() >= 0) {
/*  461: 757 */       resultNames[(current++)] = "Instance_ID";
/*  462:     */     }
/*  463: 759 */     if (getPredTargetColumn())
/*  464:     */     {
/*  465: 760 */       resultNames[(current++)] = "Targets";
/*  466: 761 */       resultNames[(current++)] = "Predictions";
/*  467:     */     }
/*  468: 765 */     resultNames[(current++)] = "Summary";
/*  469: 767 */     for (int i = 0; i < addm; i++) {
/*  470: 768 */       resultNames[(current++)] = this.m_AdditionalMeasures[i];
/*  471:     */     }
/*  472: 771 */     for (AbstractEvaluationMetric m : this.m_pluginMetrics)
/*  473:     */     {
/*  474: 772 */       List<String> statNames = m.getStatisticNames();
/*  475: 773 */       for (String s : statNames) {
/*  476: 774 */         resultNames[(current++)] = s;
/*  477:     */       }
/*  478:     */     }
/*  479: 778 */     if (current != overall_length) {
/*  480: 779 */       throw new Error("ResultNames didn't fit RESULT_SIZE");
/*  481:     */     }
/*  482: 781 */     return resultNames;
/*  483:     */   }
/*  484:     */   
/*  485:     */   public Object[] getResult(Instances train, Instances test)
/*  486:     */     throws Exception
/*  487:     */   {
/*  488: 798 */     if (train.classAttribute().type() != 1) {
/*  489: 799 */       throw new Exception("Class attribute is not nominal!");
/*  490:     */     }
/*  491: 801 */     if (this.m_Template == null) {
/*  492: 802 */       throw new Exception("No classifier has been specified");
/*  493:     */     }
/*  494: 804 */     int addm = this.m_AdditionalMeasures != null ? this.m_AdditionalMeasures.length : 0;
/*  495: 805 */     int overall_length = 32 + addm;
/*  496: 806 */     overall_length += 16;
/*  497: 807 */     overall_length += 10;
/*  498: 808 */     overall_length += 2;
/*  499: 809 */     if (getAttributeID() >= 0) {
/*  500: 810 */       overall_length++;
/*  501:     */     }
/*  502: 812 */     if (getPredTargetColumn()) {
/*  503: 813 */       overall_length += 2;
/*  504:     */     }
/*  505: 816 */     overall_length += this.m_numPluginStatistics;
/*  506:     */     
/*  507: 818 */     ThreadMXBean thMonitor = ManagementFactory.getThreadMXBean();
/*  508: 819 */     boolean canMeasureCPUTime = thMonitor.isThreadCpuTimeSupported();
/*  509: 820 */     if ((canMeasureCPUTime) && (!thMonitor.isThreadCpuTimeEnabled())) {
/*  510: 821 */       thMonitor.setThreadCpuTimeEnabled(true);
/*  511:     */     }
/*  512: 824 */     Object[] result = new Object[overall_length];
/*  513: 825 */     Evaluation eval = new Evaluation(train);
/*  514: 826 */     this.m_Classifier = AbstractClassifier.makeCopy(this.m_Template);
/*  515:     */     
/*  516: 828 */     long thID = Thread.currentThread().getId();
/*  517: 829 */     long CPUStartTime = -1L;long trainCPUTimeElapsed = -1L;long testCPUTimeElapsed = -1L;
/*  518:     */     
/*  519:     */ 
/*  520: 832 */     long trainTimeStart = System.currentTimeMillis();
/*  521: 833 */     if (canMeasureCPUTime) {
/*  522: 834 */       CPUStartTime = thMonitor.getThreadUserTime(thID);
/*  523:     */     }
/*  524: 836 */     this.m_Classifier.buildClassifier(train);
/*  525: 837 */     if (canMeasureCPUTime) {
/*  526: 838 */       trainCPUTimeElapsed = thMonitor.getThreadUserTime(thID) - CPUStartTime;
/*  527:     */     }
/*  528: 840 */     long trainTimeElapsed = System.currentTimeMillis() - trainTimeStart;
/*  529:     */     
/*  530:     */ 
/*  531: 843 */     long testTimeStart = System.currentTimeMillis();
/*  532: 844 */     if (canMeasureCPUTime) {
/*  533: 845 */       CPUStartTime = thMonitor.getThreadUserTime(thID);
/*  534:     */     }
/*  535: 847 */     double[] predictions = eval.evaluateModel(this.m_Classifier, test, new Object[0]);
/*  536: 848 */     if (canMeasureCPUTime) {
/*  537: 849 */       testCPUTimeElapsed = thMonitor.getThreadUserTime(thID) - CPUStartTime;
/*  538:     */     }
/*  539: 851 */     long testTimeElapsed = System.currentTimeMillis() - testTimeStart;
/*  540: 852 */     thMonitor = null;
/*  541:     */     
/*  542: 854 */     this.m_result = eval.toSummaryString();
/*  543:     */     
/*  544:     */ 
/*  545: 857 */     int current = 0;
/*  546: 858 */     result[(current++)] = new Double(train.numInstances());
/*  547: 859 */     result[(current++)] = new Double(eval.numInstances());
/*  548: 860 */     result[(current++)] = new Double(eval.correct());
/*  549: 861 */     result[(current++)] = new Double(eval.incorrect());
/*  550: 862 */     result[(current++)] = new Double(eval.unclassified());
/*  551: 863 */     result[(current++)] = new Double(eval.pctCorrect());
/*  552: 864 */     result[(current++)] = new Double(eval.pctIncorrect());
/*  553: 865 */     result[(current++)] = new Double(eval.pctUnclassified());
/*  554: 866 */     result[(current++)] = new Double(eval.kappa());
/*  555:     */     
/*  556: 868 */     result[(current++)] = new Double(eval.meanAbsoluteError());
/*  557: 869 */     result[(current++)] = new Double(eval.rootMeanSquaredError());
/*  558: 870 */     result[(current++)] = new Double(eval.relativeAbsoluteError());
/*  559: 871 */     result[(current++)] = new Double(eval.rootRelativeSquaredError());
/*  560:     */     
/*  561: 873 */     result[(current++)] = new Double(eval.SFPriorEntropy());
/*  562: 874 */     result[(current++)] = new Double(eval.SFSchemeEntropy());
/*  563: 875 */     result[(current++)] = new Double(eval.SFEntropyGain());
/*  564: 876 */     result[(current++)] = new Double(eval.SFMeanPriorEntropy());
/*  565: 877 */     result[(current++)] = new Double(eval.SFMeanSchemeEntropy());
/*  566: 878 */     result[(current++)] = new Double(eval.SFMeanEntropyGain());
/*  567:     */     
/*  568:     */ 
/*  569: 881 */     result[(current++)] = new Double(eval.KBInformation());
/*  570: 882 */     result[(current++)] = new Double(eval.KBMeanInformation());
/*  571: 883 */     result[(current++)] = new Double(eval.KBRelativeInformation());
/*  572:     */     
/*  573:     */ 
/*  574: 886 */     result[(current++)] = new Double(eval.truePositiveRate(this.m_IRclass));
/*  575: 887 */     result[(current++)] = new Double(eval.numTruePositives(this.m_IRclass));
/*  576: 888 */     result[(current++)] = new Double(eval.falsePositiveRate(this.m_IRclass));
/*  577: 889 */     result[(current++)] = new Double(eval.numFalsePositives(this.m_IRclass));
/*  578: 890 */     result[(current++)] = new Double(eval.trueNegativeRate(this.m_IRclass));
/*  579: 891 */     result[(current++)] = new Double(eval.numTrueNegatives(this.m_IRclass));
/*  580: 892 */     result[(current++)] = new Double(eval.falseNegativeRate(this.m_IRclass));
/*  581: 893 */     result[(current++)] = new Double(eval.numFalseNegatives(this.m_IRclass));
/*  582: 894 */     result[(current++)] = new Double(eval.precision(this.m_IRclass));
/*  583: 895 */     result[(current++)] = new Double(eval.recall(this.m_IRclass));
/*  584: 896 */     result[(current++)] = new Double(eval.fMeasure(this.m_IRclass));
/*  585: 897 */     result[(current++)] = new Double(eval.matthewsCorrelationCoefficient(this.m_IRclass));
/*  586:     */     
/*  587: 899 */     result[(current++)] = new Double(eval.areaUnderROC(this.m_IRclass));
/*  588: 900 */     result[(current++)] = new Double(eval.areaUnderPRC(this.m_IRclass));
/*  589:     */     
/*  590:     */ 
/*  591: 903 */     result[(current++)] = new Double(eval.weightedTruePositiveRate());
/*  592: 904 */     result[(current++)] = new Double(eval.weightedFalsePositiveRate());
/*  593: 905 */     result[(current++)] = new Double(eval.weightedTrueNegativeRate());
/*  594: 906 */     result[(current++)] = new Double(eval.weightedFalseNegativeRate());
/*  595: 907 */     result[(current++)] = new Double(eval.weightedPrecision());
/*  596: 908 */     result[(current++)] = new Double(eval.weightedRecall());
/*  597: 909 */     result[(current++)] = new Double(eval.weightedFMeasure());
/*  598: 910 */     result[(current++)] = new Double(eval.weightedMatthewsCorrelation());
/*  599: 911 */     result[(current++)] = new Double(eval.weightedAreaUnderROC());
/*  600: 912 */     result[(current++)] = new Double(eval.weightedAreaUnderPRC());
/*  601:     */     
/*  602:     */ 
/*  603: 915 */     result[(current++)] = new Double(eval.unweightedMacroFmeasure());
/*  604: 916 */     result[(current++)] = new Double(eval.unweightedMicroFmeasure());
/*  605:     */     
/*  606:     */ 
/*  607: 919 */     result[(current++)] = new Double(trainTimeElapsed / 1000.0D);
/*  608: 920 */     result[(current++)] = new Double(testTimeElapsed / 1000.0D);
/*  609: 921 */     if (canMeasureCPUTime)
/*  610:     */     {
/*  611: 922 */       result[(current++)] = new Double(trainCPUTimeElapsed / 1000000.0D / 1000.0D);
/*  612:     */       
/*  613: 924 */       result[(current++)] = new Double(testCPUTimeElapsed / 1000000.0D / 1000.0D);
/*  614:     */       
/*  615: 926 */       result[(current++)] = new Double(trainCPUTimeElapsed / 1000000.0D);
/*  616:     */       
/*  617: 928 */       result[(current++)] = new Double(testCPUTimeElapsed / 1000000.0D);
/*  618:     */     }
/*  619:     */     else
/*  620:     */     {
/*  621: 930 */       result[(current++)] = new Double(Utils.missingValue());
/*  622: 931 */       result[(current++)] = new Double(Utils.missingValue());
/*  623: 932 */       result[(current++)] = new Double(Utils.missingValue());
/*  624: 933 */       result[(current++)] = new Double(Utils.missingValue());
/*  625:     */     }
/*  626: 937 */     if (this.m_NoSizeDetermination)
/*  627:     */     {
/*  628: 938 */       result[(current++)] = Double.valueOf(-1.0D);
/*  629: 939 */       result[(current++)] = Double.valueOf(-1.0D);
/*  630: 940 */       result[(current++)] = Double.valueOf(-1.0D);
/*  631:     */     }
/*  632:     */     else
/*  633:     */     {
/*  634: 942 */       ByteArrayOutputStream bastream = new ByteArrayOutputStream();
/*  635: 943 */       ObjectOutputStream oostream = new ObjectOutputStream(bastream);
/*  636: 944 */       oostream.writeObject(this.m_Classifier);
/*  637: 945 */       result[(current++)] = new Double(bastream.size());
/*  638: 946 */       bastream = new ByteArrayOutputStream();
/*  639: 947 */       oostream = new ObjectOutputStream(bastream);
/*  640: 948 */       oostream.writeObject(train);
/*  641: 949 */       result[(current++)] = new Double(bastream.size());
/*  642: 950 */       bastream = new ByteArrayOutputStream();
/*  643: 951 */       oostream = new ObjectOutputStream(bastream);
/*  644: 952 */       oostream.writeObject(test);
/*  645: 953 */       result[(current++)] = new Double(bastream.size());
/*  646:     */     }
/*  647: 957 */     result[(current++)] = new Double(eval.coverageOfTestCasesByPredictedRegions());
/*  648:     */     
/*  649: 959 */     result[(current++)] = new Double(eval.sizeOfPredictedRegions());
/*  650: 962 */     if (getAttributeID() >= 0)
/*  651:     */     {
/*  652: 963 */       String idsString = "";
/*  653: 964 */       if (test.attribute(this.m_attID).isNumeric())
/*  654:     */       {
/*  655: 965 */         if (test.numInstances() > 0) {
/*  656: 966 */           idsString = idsString + test.instance(0).value(this.m_attID);
/*  657:     */         }
/*  658: 968 */         for (int i = 1; i < test.numInstances(); i++) {
/*  659: 969 */           idsString = idsString + "|" + test.instance(i).value(this.m_attID);
/*  660:     */         }
/*  661:     */       }
/*  662:     */       else
/*  663:     */       {
/*  664: 972 */         if (test.numInstances() > 0) {
/*  665: 973 */           idsString = idsString + test.instance(0).stringValue(this.m_attID);
/*  666:     */         }
/*  667: 975 */         for (int i = 1; i < test.numInstances(); i++) {
/*  668: 976 */           idsString = idsString + "|" + test.instance(i).stringValue(this.m_attID);
/*  669:     */         }
/*  670:     */       }
/*  671: 979 */       result[(current++)] = idsString;
/*  672:     */     }
/*  673: 982 */     if (getPredTargetColumn()) {
/*  674: 983 */       if (test.classAttribute().isNumeric())
/*  675:     */       {
/*  676: 985 */         if (test.numInstances() > 0)
/*  677:     */         {
/*  678: 986 */           String targetsString = "";
/*  679: 987 */           targetsString = targetsString + test.instance(0).value(test.classIndex());
/*  680: 988 */           for (int i = 1; i < test.numInstances(); i++) {
/*  681: 989 */             targetsString = targetsString + "|" + test.instance(i).value(test.classIndex());
/*  682:     */           }
/*  683: 991 */           result[(current++)] = targetsString;
/*  684:     */         }
/*  685: 995 */         if (predictions.length > 0)
/*  686:     */         {
/*  687: 996 */           String predictionsString = "";
/*  688: 997 */           predictionsString = predictionsString + predictions[0];
/*  689: 998 */           for (int i = 1; i < predictions.length; i++) {
/*  690: 999 */             predictionsString = predictionsString + "|" + predictions[i];
/*  691:     */           }
/*  692:1001 */           result[(current++)] = predictionsString;
/*  693:     */         }
/*  694:     */       }
/*  695:     */       else
/*  696:     */       {
/*  697:1005 */         if (test.numInstances() > 0)
/*  698:     */         {
/*  699:1006 */           String targetsString = "";
/*  700:1007 */           targetsString = targetsString + test.instance(0).stringValue(test.classIndex());
/*  701:1008 */           for (int i = 1; i < test.numInstances(); i++) {
/*  702:1009 */             targetsString = targetsString + "|" + test.instance(i).stringValue(test.classIndex());
/*  703:     */           }
/*  704:1012 */           result[(current++)] = targetsString;
/*  705:     */         }
/*  706:1016 */         if (predictions.length > 0)
/*  707:     */         {
/*  708:1017 */           String predictionsString = "";
/*  709:1018 */           predictionsString = predictionsString + test.classAttribute().value((int)predictions[0]);
/*  710:1020 */           for (int i = 1; i < predictions.length; i++) {
/*  711:1021 */             predictionsString = predictionsString + "|" + test.classAttribute().value((int)predictions[i]);
/*  712:     */           }
/*  713:1024 */           result[(current++)] = predictionsString;
/*  714:     */         }
/*  715:     */       }
/*  716:     */     }
/*  717:1029 */     if ((this.m_Classifier instanceof Summarizable)) {
/*  718:1030 */       result[(current++)] = ((Summarizable)this.m_Classifier).toSummaryString();
/*  719:     */     } else {
/*  720:1032 */       result[(current++)] = null;
/*  721:     */     }
/*  722:1035 */     for (int i = 0; i < addm; i++) {
/*  723:1036 */       if (this.m_doesProduce[i] != 0) {
/*  724:     */         try
/*  725:     */         {
/*  726:1038 */           double dv = ((AdditionalMeasureProducer)this.m_Classifier).getMeasure(this.m_AdditionalMeasures[i]);
/*  727:1040 */           if (!Utils.isMissingValue(dv))
/*  728:     */           {
/*  729:1041 */             Double value = new Double(dv);
/*  730:1042 */             result[(current++)] = value;
/*  731:     */           }
/*  732:     */           else
/*  733:     */           {
/*  734:1044 */             result[(current++)] = null;
/*  735:     */           }
/*  736:     */         }
/*  737:     */         catch (Exception ex)
/*  738:     */         {
/*  739:1047 */           System.err.println(ex);
/*  740:     */         }
/*  741:     */       } else {
/*  742:1050 */         result[(current++)] = null;
/*  743:     */       }
/*  744:     */     }
/*  745:1055 */     List<AbstractEvaluationMetric> metrics = eval.getPluginMetrics();
/*  746:     */     Iterator i$;
/*  747:1056 */     if (metrics != null) {
/*  748:1057 */       for (i$ = metrics.iterator(); i$.hasNext();)
/*  749:     */       {
/*  750:1057 */         m = (AbstractEvaluationMetric)i$.next();
/*  751:1058 */         if (m.appliesToNominalClass())
/*  752:     */         {
/*  753:1059 */           List<String> statNames = m.getStatisticNames();
/*  754:1060 */           for (String s : statNames) {
/*  755:1061 */             result[(current++)] = new Double(m.getStatistic(s));
/*  756:     */           }
/*  757:     */         }
/*  758:     */       }
/*  759:     */     }
/*  760:     */     AbstractEvaluationMetric m;
/*  761:1067 */     if (current != overall_length) {
/*  762:1068 */       throw new Error("Results didn't fit RESULT_SIZE");
/*  763:     */     }
/*  764:1071 */     this.m_Evaluation = eval;
/*  765:     */     
/*  766:1073 */     return result;
/*  767:     */   }
/*  768:     */   
/*  769:     */   public String classifierTipText()
/*  770:     */   {
/*  771:1083 */     return "The classifier to use.";
/*  772:     */   }
/*  773:     */   
/*  774:     */   public Classifier getClassifier()
/*  775:     */   {
/*  776:1093 */     return this.m_Template;
/*  777:     */   }
/*  778:     */   
/*  779:     */   public void setClassifier(Classifier newClassifier)
/*  780:     */   {
/*  781:1103 */     this.m_Template = newClassifier;
/*  782:1104 */     updateOptions();
/*  783:     */   }
/*  784:     */   
/*  785:     */   public int getClassForIRStatistics()
/*  786:     */   {
/*  787:1113 */     return this.m_IRclass;
/*  788:     */   }
/*  789:     */   
/*  790:     */   public void setClassForIRStatistics(int v)
/*  791:     */   {
/*  792:1122 */     this.m_IRclass = v;
/*  793:     */   }
/*  794:     */   
/*  795:     */   public int getAttributeID()
/*  796:     */   {
/*  797:1131 */     return this.m_attID;
/*  798:     */   }
/*  799:     */   
/*  800:     */   public void setAttributeID(int v)
/*  801:     */   {
/*  802:1140 */     this.m_attID = v;
/*  803:     */   }
/*  804:     */   
/*  805:     */   public boolean getPredTargetColumn()
/*  806:     */   {
/*  807:1147 */     return this.m_predTargetColumn;
/*  808:     */   }
/*  809:     */   
/*  810:     */   public void setPredTargetColumn(boolean v)
/*  811:     */   {
/*  812:1156 */     this.m_predTargetColumn = v;
/*  813:     */   }
/*  814:     */   
/*  815:     */   public boolean getNoSizeDetermination()
/*  816:     */   {
/*  817:1165 */     return this.m_NoSizeDetermination;
/*  818:     */   }
/*  819:     */   
/*  820:     */   public void setNoSizeDetermination(boolean value)
/*  821:     */   {
/*  822:1174 */     this.m_NoSizeDetermination = value;
/*  823:     */   }
/*  824:     */   
/*  825:     */   public String noSizeDeterminationTipText()
/*  826:     */   {
/*  827:1184 */     return "If enabled, the size determination for train/test/classifier is skipped.";
/*  828:     */   }
/*  829:     */   
/*  830:     */   protected void updateOptions()
/*  831:     */   {
/*  832:1192 */     if ((this.m_Template instanceof OptionHandler)) {
/*  833:1193 */       this.m_ClassifierOptions = Utils.joinOptions(((OptionHandler)this.m_Template).getOptions());
/*  834:     */     } else {
/*  835:1196 */       this.m_ClassifierOptions = "";
/*  836:     */     }
/*  837:1198 */     if ((this.m_Template instanceof Serializable))
/*  838:     */     {
/*  839:1199 */       ObjectStreamClass obs = ObjectStreamClass.lookup(this.m_Template.getClass());
/*  840:1200 */       this.m_ClassifierVersion = ("" + obs.getSerialVersionUID());
/*  841:     */     }
/*  842:     */     else
/*  843:     */     {
/*  844:1202 */       this.m_ClassifierVersion = "";
/*  845:     */     }
/*  846:     */   }
/*  847:     */   
/*  848:     */   public void setClassifierName(String newClassifierName)
/*  849:     */     throws Exception
/*  850:     */   {
/*  851:     */     try
/*  852:     */     {
/*  853:1216 */       setClassifier((Classifier)Class.forName(newClassifierName).newInstance());
/*  854:     */     }
/*  855:     */     catch (Exception ex)
/*  856:     */     {
/*  857:1218 */       throw new Exception("Can't find Classifier with class name: " + newClassifierName);
/*  858:     */     }
/*  859:     */   }
/*  860:     */   
/*  861:     */   public String getRawResultOutput()
/*  862:     */   {
/*  863:1230 */     StringBuffer result = new StringBuffer();
/*  864:1232 */     if (this.m_Classifier == null) {
/*  865:1233 */       return "<null> classifier";
/*  866:     */     }
/*  867:1235 */     result.append(toString());
/*  868:1236 */     result.append("Classifier model: \n" + this.m_Classifier.toString() + '\n');
/*  869:1239 */     if (this.m_result != null)
/*  870:     */     {
/*  871:1240 */       result.append(this.m_result);
/*  872:1242 */       if (this.m_doesProduce != null) {
/*  873:1243 */         for (int i = 0; i < this.m_doesProduce.length; i++) {
/*  874:1244 */           if (this.m_doesProduce[i] != 0) {
/*  875:     */             try
/*  876:     */             {
/*  877:1246 */               double dv = ((AdditionalMeasureProducer)this.m_Classifier).getMeasure(this.m_AdditionalMeasures[i]);
/*  878:1248 */               if (!Utils.isMissingValue(dv))
/*  879:     */               {
/*  880:1249 */                 Double value = new Double(dv);
/*  881:1250 */                 result.append(this.m_AdditionalMeasures[i] + " : " + value + '\n');
/*  882:     */               }
/*  883:     */               else
/*  884:     */               {
/*  885:1252 */                 result.append(this.m_AdditionalMeasures[i] + " : " + '?' + '\n');
/*  886:     */               }
/*  887:     */             }
/*  888:     */             catch (Exception ex)
/*  889:     */             {
/*  890:1255 */               System.err.println(ex);
/*  891:     */             }
/*  892:     */           }
/*  893:     */         }
/*  894:     */       }
/*  895:     */     }
/*  896:1261 */     return result.toString();
/*  897:     */   }
/*  898:     */   
/*  899:     */   public String toString()
/*  900:     */   {
/*  901:1272 */     String result = "ClassifierSplitEvaluator: ";
/*  902:1273 */     if (this.m_Template == null) {
/*  903:1274 */       return result + "<null> classifier";
/*  904:     */     }
/*  905:1276 */     return result + this.m_Template.getClass().getName() + " " + this.m_ClassifierOptions + "(version " + this.m_ClassifierVersion + ")";
/*  906:     */   }
/*  907:     */   
/*  908:     */   public String getRevision()
/*  909:     */   {
/*  910:1287 */     return RevisionUtils.extract("$Revision: 11323 $");
/*  911:     */   }
/*  912:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.ClassifierSplitEvaluator
 * JD-Core Version:    0.7.0.1
 */