/*    1:     */ package weka.attributeSelection;
/*    2:     */ 
/*    3:     */ import java.util.BitSet;
/*    4:     */ import java.util.Collections;
/*    5:     */ import java.util.Enumeration;
/*    6:     */ import java.util.Iterator;
/*    7:     */ import java.util.List;
/*    8:     */ import java.util.Random;
/*    9:     */ import java.util.Vector;
/*   10:     */ import weka.classifiers.AbstractClassifier;
/*   11:     */ import weka.classifiers.Classifier;
/*   12:     */ import weka.classifiers.Evaluation;
/*   13:     */ import weka.classifiers.evaluation.AbstractEvaluationMetric;
/*   14:     */ import weka.classifiers.evaluation.InformationRetrievalEvaluationMetric;
/*   15:     */ import weka.classifiers.rules.ZeroR;
/*   16:     */ import weka.core.Attribute;
/*   17:     */ import weka.core.Capabilities;
/*   18:     */ import weka.core.Capabilities.Capability;
/*   19:     */ import weka.core.Instances;
/*   20:     */ import weka.core.Option;
/*   21:     */ import weka.core.OptionHandler;
/*   22:     */ import weka.core.RevisionUtils;
/*   23:     */ import weka.core.SelectedTag;
/*   24:     */ import weka.core.Tag;
/*   25:     */ import weka.core.TechnicalInformation;
/*   26:     */ import weka.core.TechnicalInformation.Field;
/*   27:     */ import weka.core.TechnicalInformation.Type;
/*   28:     */ import weka.core.TechnicalInformationHandler;
/*   29:     */ import weka.core.Utils;
/*   30:     */ import weka.filters.Filter;
/*   31:     */ import weka.filters.unsupervised.attribute.Remove;
/*   32:     */ 
/*   33:     */ public class WrapperSubsetEval
/*   34:     */   extends ASEvaluation
/*   35:     */   implements SubsetEvaluator, OptionHandler, TechnicalInformationHandler
/*   36:     */ {
/*   37:     */   static final long serialVersionUID = -4573057658746728675L;
/*   38:     */   private Instances m_trainInstances;
/*   39:     */   private int m_classIndex;
/*   40:     */   private int m_numAttribs;
/*   41:     */   private Evaluation m_Evaluation;
/*   42:     */   private Classifier m_BaseClassifier;
/*   43:     */   private int m_folds;
/*   44:     */   private int m_seed;
/*   45:     */   private double m_threshold;
/*   46:     */   public static final int EVAL_DEFAULT = 1;
/*   47:     */   public static final int EVAL_ACCURACY = 2;
/*   48:     */   public static final int EVAL_RMSE = 3;
/*   49:     */   public static final int EVAL_MAE = 4;
/*   50:     */   public static final int EVAL_FMEASURE = 5;
/*   51:     */   public static final int EVAL_AUC = 6;
/*   52:     */   public static final int EVAL_AUPRC = 7;
/*   53:     */   public static final int EVAL_CORRELATION = 8;
/*   54:     */   public static final int EVAL_PLUGIN = 9;
/*   55:     */   public static final Tag[] TAGS_EVALUATION;
/*   56:     */   
/*   57:     */   protected static class PluginTag
/*   58:     */     extends Tag
/*   59:     */   {
/*   60:     */     private static final long serialVersionUID = -6978438858413428382L;
/*   61:     */     protected AbstractEvaluationMetric m_metric;
/*   62:     */     protected String m_statisticName;
/*   63:     */     
/*   64:     */     public PluginTag(int ID, AbstractEvaluationMetric metric, String statisticName)
/*   65:     */     {
/*   66: 201 */       super(statisticName, statisticName);
/*   67: 202 */       this.m_metric = metric;
/*   68: 203 */       this.m_statisticName = statisticName;
/*   69:     */     }
/*   70:     */     
/*   71:     */     public String getMetricName()
/*   72:     */     {
/*   73: 212 */       return this.m_metric.getMetricName();
/*   74:     */     }
/*   75:     */     
/*   76:     */     public String getStatisticName()
/*   77:     */     {
/*   78: 221 */       return this.m_statisticName;
/*   79:     */     }
/*   80:     */     
/*   81:     */     public AbstractEvaluationMetric getMetric()
/*   82:     */     {
/*   83: 230 */       return this.m_metric;
/*   84:     */     }
/*   85:     */   }
/*   86:     */   
/*   87: 241 */   protected int m_IRClassVal = -1;
/*   88: 244 */   protected String m_IRClassValS = "";
/*   89: 246 */   protected static List<AbstractEvaluationMetric> PLUGIN_METRICS = ;
/*   90:     */   
/*   91:     */   static
/*   92:     */   {
/*   93: 250 */     int totalPluginCount = 0;
/*   94: 251 */     if (PLUGIN_METRICS != null) {
/*   95: 252 */       for (AbstractEvaluationMetric m : PLUGIN_METRICS) {
/*   96: 253 */         totalPluginCount += m.getStatisticNames().size();
/*   97:     */       }
/*   98:     */     }
/*   99: 257 */     TAGS_EVALUATION = new Tag[8 + totalPluginCount];
/*  100: 258 */     TAGS_EVALUATION[0] = new Tag(1, "default", "Default: accuracy (discrete class); RMSE (numeric class)");
/*  101:     */     
/*  102: 260 */     TAGS_EVALUATION[1] = new Tag(2, "acc", "Accuracy (discrete class only)");
/*  103:     */     
/*  104: 262 */     TAGS_EVALUATION[2] = new Tag(3, "rmse", "RMSE (of the class probabilities for discrete class)");
/*  105:     */     
/*  106: 264 */     TAGS_EVALUATION[3] = new Tag(4, "mae", "MAE (of the class probabilities for discrete class)");
/*  107:     */     
/*  108: 266 */     TAGS_EVALUATION[4] = new Tag(5, "f-meas", "F-measure (discrete class only)");
/*  109:     */     
/*  110: 268 */     TAGS_EVALUATION[5] = new Tag(6, "auc", "AUC (area under the ROC curve - discrete class only)");
/*  111:     */     
/*  112: 270 */     TAGS_EVALUATION[6] = new Tag(7, "auprc", "AUPRC (area under the precision-recall curve - discrete class only)");
/*  113:     */     
/*  114: 272 */     TAGS_EVALUATION[7] = new Tag(8, "corr-coeff", "Correlation coefficient - numeric class only");
/*  115:     */     int index;
/*  116:     */     Iterator i$;
/*  117: 275 */     if (PLUGIN_METRICS != null)
/*  118:     */     {
/*  119: 276 */       index = 8;
/*  120: 277 */       for (i$ = PLUGIN_METRICS.iterator(); i$.hasNext();)
/*  121:     */       {
/*  122: 277 */         m = (AbstractEvaluationMetric)i$.next();
/*  123: 278 */         for (String stat : m.getStatisticNames()) {
/*  124: 279 */           TAGS_EVALUATION[(index++)] = new PluginTag(index + 1, m, stat);
/*  125:     */         }
/*  126:     */       }
/*  127:     */     }
/*  128:     */     AbstractEvaluationMetric m;
/*  129:     */   }
/*  130:     */   
/*  131: 286 */   protected Tag m_evaluationMeasure = TAGS_EVALUATION[0];
/*  132:     */   
/*  133:     */   public String globalInfo()
/*  134:     */   {
/*  135: 295 */     return "WrapperSubsetEval:\n\nEvaluates attribute sets by using a learning scheme. Cross validation is used to estimate the accuracy of the learning scheme for a set of attributes.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*  136:     */   }
/*  137:     */   
/*  138:     */   public TechnicalInformation getTechnicalInformation()
/*  139:     */   {
/*  140: 313 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  141: 314 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Ron Kohavi and George H. John");
/*  142: 315 */     result.setValue(TechnicalInformation.Field.YEAR, "1997");
/*  143: 316 */     result.setValue(TechnicalInformation.Field.TITLE, "Wrappers for feature subset selection");
/*  144: 317 */     result.setValue(TechnicalInformation.Field.JOURNAL, "Artificial Intelligence");
/*  145: 318 */     result.setValue(TechnicalInformation.Field.VOLUME, "97");
/*  146: 319 */     result.setValue(TechnicalInformation.Field.NUMBER, "1-2");
/*  147: 320 */     result.setValue(TechnicalInformation.Field.PAGES, "273-324");
/*  148: 321 */     result.setValue(TechnicalInformation.Field.NOTE, "Special issue on relevance");
/*  149: 322 */     result.setValue(TechnicalInformation.Field.ISSN, "0004-3702");
/*  150:     */     
/*  151: 324 */     return result;
/*  152:     */   }
/*  153:     */   
/*  154:     */   public WrapperSubsetEval()
/*  155:     */   {
/*  156: 331 */     resetOptions();
/*  157:     */   }
/*  158:     */   
/*  159:     */   public Enumeration<Option> listOptions()
/*  160:     */   {
/*  161: 341 */     Vector<Option> newVector = new Vector(4);
/*  162: 342 */     newVector.addElement(new Option("\tclass name of base learner to use for \taccuracy estimation.\n\tPlace any classifier options LAST on the command line\n\tfollowing a \"--\". eg.:\n\t\t-B weka.classifiers.bayes.NaiveBayes ... -- -K\n\t(default: weka.classifiers.rules.ZeroR)", "B", 1, "-B <base learner>"));
/*  163:     */     
/*  164:     */ 
/*  165:     */ 
/*  166:     */ 
/*  167:     */ 
/*  168:     */ 
/*  169:     */ 
/*  170: 350 */     newVector.addElement(new Option("\tnumber of cross validation folds to use for estimating accuracy.\n\t(default=5)", "F", 1, "-F <num>"));
/*  171:     */     
/*  172:     */ 
/*  173:     */ 
/*  174:     */ 
/*  175: 355 */     newVector.addElement(new Option("\tSeed for cross validation accuracy testimation.\n\t(default = 1)", "R", 1, "-R <seed>"));
/*  176:     */     
/*  177:     */ 
/*  178:     */ 
/*  179: 359 */     newVector.addElement(new Option("\tthreshold by which to execute another cross validation\n\t(standard deviation---expressed as a percentage of the mean).\n\t(default: 0.01 (1%))", "T", 1, "-T <num>"));
/*  180:     */     
/*  181:     */ 
/*  182:     */ 
/*  183:     */ 
/*  184: 364 */     newVector.addElement(new Option("\tPerformance evaluation measure to use for selecting attributes.\n\t(Default = default: accuracy for discrete class and rmse for numeric class)", "E", 1, "-E " + Tag.toOptionList(TAGS_EVALUATION)));
/*  185:     */     
/*  186:     */ 
/*  187:     */ 
/*  188:     */ 
/*  189:     */ 
/*  190:     */ 
/*  191: 371 */     newVector.addElement(new Option("\tOptional class value (label or 1-based index) to use in conjunction with\n\tIR statistics (f-meas, auc or auprc). Omitting this option will use\n\tthe class-weighted average.", "IRclass", 1, "-IRclass <label | index>"));
/*  192: 377 */     if ((this.m_BaseClassifier != null) && ((this.m_BaseClassifier instanceof OptionHandler)))
/*  193:     */     {
/*  194: 379 */       newVector.addElement(new Option("", "", 0, "\nOptions specific to scheme " + this.m_BaseClassifier.getClass().getName() + ":"));
/*  195:     */       
/*  196: 381 */       newVector.addAll(Collections.list(((OptionHandler)this.m_BaseClassifier).listOptions()));
/*  197:     */     }
/*  198: 385 */     return newVector.elements();
/*  199:     */   }
/*  200:     */   
/*  201:     */   public void setOptions(String[] options)
/*  202:     */     throws Exception
/*  203:     */   {
/*  204: 455 */     resetOptions();
/*  205: 456 */     String optionString = Utils.getOption('B', options);
/*  206: 458 */     if (optionString.length() == 0) {
/*  207: 459 */       optionString = ZeroR.class.getName();
/*  208:     */     }
/*  209: 461 */     setClassifier(AbstractClassifier.forName(optionString, Utils.partitionOptions(options)));
/*  210:     */     
/*  211: 463 */     optionString = Utils.getOption('F', options);
/*  212: 465 */     if (optionString.length() != 0) {
/*  213: 466 */       setFolds(Integer.parseInt(optionString));
/*  214:     */     }
/*  215: 469 */     optionString = Utils.getOption('R', options);
/*  216: 470 */     if (optionString.length() != 0) {
/*  217: 471 */       setSeed(Integer.parseInt(optionString));
/*  218:     */     }
/*  219: 479 */     optionString = Utils.getOption('T', options);
/*  220: 481 */     if (optionString.length() != 0)
/*  221:     */     {
/*  222: 483 */       Double temp = Double.valueOf(optionString);
/*  223: 484 */       setThreshold(temp.doubleValue());
/*  224:     */     }
/*  225: 487 */     optionString = Utils.getOption('E', options);
/*  226: 488 */     if (optionString.length() != 0) {
/*  227: 489 */       for (Tag t : TAGS_EVALUATION) {
/*  228: 490 */         if (t.getIDStr().equalsIgnoreCase(optionString))
/*  229:     */         {
/*  230: 491 */           setEvaluationMeasure(new SelectedTag(t.getIDStr(), TAGS_EVALUATION));
/*  231: 492 */           break;
/*  232:     */         }
/*  233:     */       }
/*  234:     */     }
/*  235: 497 */     optionString = Utils.getOption("IRClass", options);
/*  236: 498 */     if (optionString.length() > 0) {
/*  237: 499 */       setIRClassValue(optionString);
/*  238:     */     }
/*  239:     */   }
/*  240:     */   
/*  241:     */   public void setIRClassValue(String val)
/*  242:     */   {
/*  243: 512 */     this.m_IRClassValS = val;
/*  244:     */   }
/*  245:     */   
/*  246:     */   public String getIRClassValue()
/*  247:     */   {
/*  248: 524 */     return this.m_IRClassValS;
/*  249:     */   }
/*  250:     */   
/*  251:     */   public String IRClassValueTipText()
/*  252:     */   {
/*  253: 534 */     return "The class label, or 1-based index of the class label, to use when evaluating subsets with an IR metric (such as f-measure or AUC. Leaving this unset will result in the class frequency weighted average of the metric being used.";
/*  254:     */   }
/*  255:     */   
/*  256:     */   public String evaluationMeasureTipText()
/*  257:     */   {
/*  258: 547 */     return "The measure used to evaluate the performance of attribute combinations.";
/*  259:     */   }
/*  260:     */   
/*  261:     */   public SelectedTag getEvaluationMeasure()
/*  262:     */   {
/*  263: 557 */     return new SelectedTag(this.m_evaluationMeasure.getIDStr(), TAGS_EVALUATION);
/*  264:     */   }
/*  265:     */   
/*  266:     */   public void setEvaluationMeasure(SelectedTag newMethod)
/*  267:     */   {
/*  268: 567 */     if (newMethod.getTags() == TAGS_EVALUATION) {
/*  269: 568 */       this.m_evaluationMeasure = newMethod.getSelectedTag();
/*  270:     */     }
/*  271:     */   }
/*  272:     */   
/*  273:     */   public String thresholdTipText()
/*  274:     */   {
/*  275: 579 */     return "Repeat xval if stdev of mean exceeds this value.";
/*  276:     */   }
/*  277:     */   
/*  278:     */   public void setThreshold(double t)
/*  279:     */   {
/*  280: 588 */     this.m_threshold = t;
/*  281:     */   }
/*  282:     */   
/*  283:     */   public double getThreshold()
/*  284:     */   {
/*  285: 597 */     return this.m_threshold;
/*  286:     */   }
/*  287:     */   
/*  288:     */   public String foldsTipText()
/*  289:     */   {
/*  290: 607 */     return "Number of xval folds to use when estimating subset accuracy.";
/*  291:     */   }
/*  292:     */   
/*  293:     */   public void setFolds(int f)
/*  294:     */   {
/*  295: 616 */     this.m_folds = f;
/*  296:     */   }
/*  297:     */   
/*  298:     */   public int getFolds()
/*  299:     */   {
/*  300: 625 */     return this.m_folds;
/*  301:     */   }
/*  302:     */   
/*  303:     */   public String seedTipText()
/*  304:     */   {
/*  305: 635 */     return "Seed to use for randomly generating xval splits.";
/*  306:     */   }
/*  307:     */   
/*  308:     */   public void setSeed(int s)
/*  309:     */   {
/*  310: 644 */     this.m_seed = s;
/*  311:     */   }
/*  312:     */   
/*  313:     */   public int getSeed()
/*  314:     */   {
/*  315: 653 */     return this.m_seed;
/*  316:     */   }
/*  317:     */   
/*  318:     */   public String classifierTipText()
/*  319:     */   {
/*  320: 663 */     return "Classifier to use for estimating the accuracy of subsets";
/*  321:     */   }
/*  322:     */   
/*  323:     */   public void setClassifier(Classifier newClassifier)
/*  324:     */   {
/*  325: 672 */     this.m_BaseClassifier = newClassifier;
/*  326:     */   }
/*  327:     */   
/*  328:     */   public Classifier getClassifier()
/*  329:     */   {
/*  330: 681 */     return this.m_BaseClassifier;
/*  331:     */   }
/*  332:     */   
/*  333:     */   public String[] getOptions()
/*  334:     */   {
/*  335: 691 */     String[] classifierOptions = new String[0];
/*  336: 693 */     if ((this.m_BaseClassifier != null) && ((this.m_BaseClassifier instanceof OptionHandler))) {
/*  337: 695 */       classifierOptions = ((OptionHandler)this.m_BaseClassifier).getOptions();
/*  338:     */     }
/*  339: 698 */     String[] options = new String[13 + classifierOptions.length];
/*  340: 699 */     int current = 0;
/*  341: 701 */     if (getClassifier() != null)
/*  342:     */     {
/*  343: 702 */       options[(current++)] = "-B";
/*  344: 703 */       options[(current++)] = getClassifier().getClass().getName();
/*  345:     */     }
/*  346: 706 */     options[(current++)] = "-F";
/*  347: 707 */     options[(current++)] = ("" + getFolds());
/*  348: 708 */     options[(current++)] = "-T";
/*  349: 709 */     options[(current++)] = ("" + getThreshold());
/*  350: 710 */     options[(current++)] = "-R";
/*  351: 711 */     options[(current++)] = ("" + getSeed());
/*  352:     */     
/*  353: 713 */     options[(current++)] = "-E";
/*  354: 714 */     options[(current++)] = this.m_evaluationMeasure.getIDStr();
/*  355: 716 */     if ((this.m_IRClassValS != null) && (this.m_IRClassValS.length() > 0))
/*  356:     */     {
/*  357: 717 */       options[(current++)] = "-IRClass";
/*  358: 718 */       options[(current++)] = this.m_IRClassValS;
/*  359:     */     }
/*  360: 721 */     options[(current++)] = "--";
/*  361: 722 */     System.arraycopy(classifierOptions, 0, options, current, classifierOptions.length);
/*  362:     */     
/*  363: 724 */     current += classifierOptions.length;
/*  364: 726 */     while (current < options.length) {
/*  365: 727 */       options[(current++)] = "";
/*  366:     */     }
/*  367: 730 */     return options;
/*  368:     */   }
/*  369:     */   
/*  370:     */   protected void resetOptions()
/*  371:     */   {
/*  372: 734 */     this.m_trainInstances = null;
/*  373: 735 */     this.m_Evaluation = null;
/*  374: 736 */     this.m_BaseClassifier = new ZeroR();
/*  375: 737 */     this.m_folds = 5;
/*  376: 738 */     this.m_seed = 1;
/*  377: 739 */     this.m_threshold = 0.01D;
/*  378:     */   }
/*  379:     */   
/*  380:     */   public Capabilities getCapabilities()
/*  381:     */   {
/*  382:     */     Capabilities result;
/*  383: 752 */     if (getClassifier() == null)
/*  384:     */     {
/*  385: 753 */       Capabilities result = super.getCapabilities();
/*  386: 754 */       result.disableAll();
/*  387:     */     }
/*  388:     */     else
/*  389:     */     {
/*  390: 756 */       result = getClassifier().getCapabilities();
/*  391:     */     }
/*  392: 760 */     for (Capabilities.Capability cap : Capabilities.Capability.values()) {
/*  393: 761 */       result.enableDependency(cap);
/*  394:     */     }
/*  395: 765 */     result.disable(Capabilities.Capability.NUMERIC_CLASS);
/*  396: 766 */     result.disable(Capabilities.Capability.DATE_CLASS);
/*  397: 767 */     boolean pluginMetricNominalClass = false;
/*  398:     */     String metricName;
/*  399: 768 */     if (this.m_evaluationMeasure.getID() >= 9)
/*  400:     */     {
/*  401: 769 */       metricName = ((PluginTag)this.m_evaluationMeasure).getMetricName();
/*  402: 770 */       for (AbstractEvaluationMetric m : PLUGIN_METRICS) {
/*  403: 771 */         if (m.getMetricName().equals(metricName))
/*  404:     */         {
/*  405: 772 */           pluginMetricNominalClass = m.appliesToNominalClass();
/*  406: 773 */           break;
/*  407:     */         }
/*  408:     */       }
/*  409:     */     }
/*  410: 777 */     if ((this.m_evaluationMeasure.getID() != 2) && (this.m_evaluationMeasure.getID() != 5) && (this.m_evaluationMeasure.getID() != 6) && (this.m_evaluationMeasure.getID() != 7) && (!pluginMetricNominalClass))
/*  411:     */     {
/*  412: 782 */       result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*  413: 783 */       result.enable(Capabilities.Capability.DATE_CLASS);
/*  414:     */     }
/*  415: 786 */     result.setMinimumNumberInstances(getFolds());
/*  416:     */     
/*  417: 788 */     return result;
/*  418:     */   }
/*  419:     */   
/*  420:     */   public void buildEvaluator(Instances data)
/*  421:     */     throws Exception
/*  422:     */   {
/*  423: 802 */     getCapabilities().testWithFail(data);
/*  424:     */     
/*  425: 804 */     this.m_trainInstances = data;
/*  426: 805 */     this.m_classIndex = this.m_trainInstances.classIndex();
/*  427: 806 */     this.m_numAttribs = this.m_trainInstances.numAttributes();
/*  428: 808 */     if ((this.m_IRClassValS != null) && (this.m_IRClassValS.length() > 0)) {
/*  429:     */       try
/*  430:     */       {
/*  431: 811 */         this.m_IRClassVal = Integer.parseInt(this.m_IRClassValS);
/*  432:     */         
/*  433: 813 */         this.m_IRClassVal -= 1;
/*  434:     */       }
/*  435:     */       catch (NumberFormatException e)
/*  436:     */       {
/*  437: 816 */         this.m_IRClassVal = this.m_trainInstances.classAttribute().indexOfValue(this.m_IRClassValS);
/*  438:     */       }
/*  439:     */     }
/*  440:     */   }
/*  441:     */   
/*  442:     */   public double evaluateSubset(BitSet subset)
/*  443:     */     throws Exception
/*  444:     */   {
/*  445: 831 */     double evalMetric = 0.0D;
/*  446: 832 */     double[] repError = new double[5];
/*  447: 833 */     int numAttributes = 0;
/*  448:     */     
/*  449: 835 */     Random Rnd = new Random(this.m_seed);
/*  450: 836 */     Remove delTransform = new Remove();
/*  451: 837 */     delTransform.setInvertSelection(true);
/*  452:     */     
/*  453: 839 */     Instances trainCopy = new Instances(this.m_trainInstances);
/*  454: 842 */     for (int i = 0; i < this.m_numAttribs; i++) {
/*  455: 843 */       if (subset.get(i)) {
/*  456: 844 */         numAttributes++;
/*  457:     */       }
/*  458:     */     }
/*  459: 849 */     int[] featArray = new int[numAttributes + 1];
/*  460:     */     
/*  461: 851 */     i = 0;
/*  462: 851 */     for (int j = 0; i < this.m_numAttribs; i++) {
/*  463: 852 */       if (subset.get(i)) {
/*  464: 853 */         featArray[(j++)] = i;
/*  465:     */       }
/*  466:     */     }
/*  467: 857 */     featArray[j] = this.m_classIndex;
/*  468: 858 */     delTransform.setAttributeIndicesArray(featArray);
/*  469: 859 */     delTransform.setInputFormat(trainCopy);
/*  470: 860 */     trainCopy = Filter.useFilter(trainCopy, delTransform);
/*  471:     */     
/*  472: 862 */     AbstractEvaluationMetric pluginMetric = null;
/*  473: 863 */     String statName = null;
/*  474: 864 */     String metricName = null;
/*  475: 867 */     for (i = 0; i < 5; i++)
/*  476:     */     {
/*  477: 868 */       this.m_Evaluation = new Evaluation(trainCopy);
/*  478: 869 */       this.m_Evaluation.crossValidateModel(this.m_BaseClassifier, trainCopy, this.m_folds, Rnd, new Object[0]);
/*  479: 872 */       switch (this.m_evaluationMeasure.getID())
/*  480:     */       {
/*  481:     */       case 1: 
/*  482: 874 */         repError[i] = this.m_Evaluation.errorRate();
/*  483: 875 */         break;
/*  484:     */       case 2: 
/*  485: 877 */         repError[i] = this.m_Evaluation.errorRate();
/*  486: 878 */         break;
/*  487:     */       case 3: 
/*  488: 880 */         repError[i] = this.m_Evaluation.rootMeanSquaredError();
/*  489: 881 */         break;
/*  490:     */       case 4: 
/*  491: 883 */         repError[i] = this.m_Evaluation.meanAbsoluteError();
/*  492: 884 */         break;
/*  493:     */       case 5: 
/*  494: 886 */         if (this.m_IRClassVal < 0) {
/*  495: 887 */           repError[i] = this.m_Evaluation.weightedFMeasure();
/*  496:     */         } else {
/*  497: 889 */           repError[i] = this.m_Evaluation.fMeasure(this.m_IRClassVal);
/*  498:     */         }
/*  499: 891 */         break;
/*  500:     */       case 6: 
/*  501: 893 */         if (this.m_IRClassVal < 0) {
/*  502: 894 */           repError[i] = this.m_Evaluation.weightedAreaUnderROC();
/*  503:     */         } else {
/*  504: 896 */           repError[i] = this.m_Evaluation.areaUnderROC(this.m_IRClassVal);
/*  505:     */         }
/*  506: 898 */         break;
/*  507:     */       case 7: 
/*  508: 900 */         if (this.m_IRClassVal < 0) {
/*  509: 901 */           repError[i] = this.m_Evaluation.weightedAreaUnderPRC();
/*  510:     */         } else {
/*  511: 903 */           repError[i] = this.m_Evaluation.areaUnderPRC(this.m_IRClassVal);
/*  512:     */         }
/*  513: 905 */         break;
/*  514:     */       case 8: 
/*  515: 907 */         repError[i] = this.m_Evaluation.correlationCoefficient();
/*  516: 908 */         break;
/*  517:     */       default: 
/*  518: 910 */         if (this.m_evaluationMeasure.getID() >= 9)
/*  519:     */         {
/*  520: 911 */           metricName = ((PluginTag)this.m_evaluationMeasure).getMetricName();
/*  521: 912 */           statName = ((PluginTag)this.m_evaluationMeasure).getStatisticName();
/*  522: 913 */           statName = ((PluginTag)this.m_evaluationMeasure).getStatisticName();
/*  523: 914 */           pluginMetric = this.m_Evaluation.getPluginMetric(metricName);
/*  524: 915 */           if (pluginMetric == null) {
/*  525: 916 */             throw new Exception("Metric  " + metricName + " does not seem to be " + "available");
/*  526:     */           }
/*  527:     */         }
/*  528: 921 */         if ((pluginMetric instanceof InformationRetrievalEvaluationMetric))
/*  529:     */         {
/*  530: 922 */           if (this.m_IRClassVal < 0) {
/*  531: 923 */             repError[i] = ((InformationRetrievalEvaluationMetric)pluginMetric).getClassWeightedAverageStatistic(statName);
/*  532:     */           } else {
/*  533: 926 */             repError[i] = ((InformationRetrievalEvaluationMetric)pluginMetric).getStatistic(statName, this.m_IRClassVal);
/*  534:     */           }
/*  535:     */         }
/*  536:     */         else {
/*  537: 930 */           repError[i] = pluginMetric.getStatistic(statName);
/*  538:     */         }
/*  539:     */         break;
/*  540:     */       }
/*  541: 936 */       if (!repeat(repError, i + 1))
/*  542:     */       {
/*  543: 937 */         i++;
/*  544: 938 */         break;
/*  545:     */       }
/*  546:     */     }
/*  547: 942 */     for (j = 0; j < i; j++) {
/*  548: 943 */       evalMetric += repError[j];
/*  549:     */     }
/*  550: 946 */     evalMetric /= i;
/*  551: 947 */     this.m_Evaluation = null;
/*  552: 949 */     switch (this.m_evaluationMeasure.getID())
/*  553:     */     {
/*  554:     */     case 1: 
/*  555:     */     case 2: 
/*  556:     */     case 3: 
/*  557:     */     case 4: 
/*  558: 954 */       if ((this.m_trainInstances.classAttribute().isNominal()) && ((this.m_evaluationMeasure.getID() == 1) || (this.m_evaluationMeasure.getID() == 2))) {
/*  559: 957 */         evalMetric = 1.0D - evalMetric;
/*  560:     */       } else {
/*  561: 959 */         evalMetric = -evalMetric;
/*  562:     */       }
/*  563: 961 */       break;
/*  564:     */     default: 
/*  565: 963 */       if ((pluginMetric != null) && (!pluginMetric.statisticIsMaximisable(statName))) {
/*  566: 965 */         evalMetric = -evalMetric;
/*  567:     */       }
/*  568:     */       break;
/*  569:     */     }
/*  570: 969 */     return evalMetric;
/*  571:     */   }
/*  572:     */   
/*  573:     */   public String toString()
/*  574:     */   {
/*  575: 979 */     StringBuffer text = new StringBuffer();
/*  576: 981 */     if (this.m_trainInstances == null)
/*  577:     */     {
/*  578: 982 */       text.append("\tWrapper subset evaluator has not been built yet\n");
/*  579:     */     }
/*  580:     */     else
/*  581:     */     {
/*  582: 984 */       text.append("\tWrapper Subset Evaluator\n");
/*  583: 985 */       text.append("\tLearning scheme: " + getClassifier().getClass().getName() + "\n");
/*  584:     */       
/*  585: 987 */       text.append("\tScheme options: ");
/*  586: 988 */       String[] classifierOptions = new String[0];
/*  587: 990 */       if ((this.m_BaseClassifier instanceof OptionHandler))
/*  588:     */       {
/*  589: 991 */         classifierOptions = ((OptionHandler)this.m_BaseClassifier).getOptions();
/*  590: 993 */         for (String classifierOption : classifierOptions) {
/*  591: 994 */           text.append(classifierOption + " ");
/*  592:     */         }
/*  593:     */       }
/*  594: 998 */       text.append("\n");
/*  595: 999 */       String IRClassL = "";
/*  596:1000 */       if (this.m_IRClassVal >= 0) {
/*  597:1001 */         IRClassL = "(class value: " + this.m_trainInstances.classAttribute().value(this.m_IRClassVal) + ")";
/*  598:     */       }
/*  599:1004 */       switch (this.m_evaluationMeasure.getID())
/*  600:     */       {
/*  601:     */       case 1: 
/*  602:     */       case 2: 
/*  603:1007 */         if (this.m_trainInstances.attribute(this.m_classIndex).isNumeric()) {
/*  604:1008 */           text.append("\tSubset evaluation: RMSE\n");
/*  605:     */         } else {
/*  606:1010 */           text.append("\tSubset evaluation: classification accuracy\n");
/*  607:     */         }
/*  608:1012 */         break;
/*  609:     */       case 3: 
/*  610:1014 */         if (this.m_trainInstances.attribute(this.m_classIndex).isNumeric()) {
/*  611:1015 */           text.append("\tSubset evaluation: RMSE\n");
/*  612:     */         } else {
/*  613:1017 */           text.append("\tSubset evaluation: RMSE (probability estimates)\n");
/*  614:     */         }
/*  615:1019 */         break;
/*  616:     */       case 4: 
/*  617:1021 */         if (this.m_trainInstances.attribute(this.m_classIndex).isNumeric()) {
/*  618:1022 */           text.append("\tSubset evaluation: MAE\n");
/*  619:     */         } else {
/*  620:1024 */           text.append("\tSubset evaluation: MAE (probability estimates)\n");
/*  621:     */         }
/*  622:1026 */         break;
/*  623:     */       case 5: 
/*  624:1028 */         text.append("\tSubset evaluation: F-measure " + (this.m_IRClassVal >= 0 ? IRClassL : "") + "\n");
/*  625:     */         
/*  626:1030 */         break;
/*  627:     */       case 6: 
/*  628:1032 */         text.append("\tSubset evaluation: area under the ROC curve " + (this.m_IRClassVal >= 0 ? IRClassL : "") + "\n");
/*  629:     */         
/*  630:1034 */         break;
/*  631:     */       case 7: 
/*  632:1036 */         text.append("\tSubset evaluation: area under the precision-recall curve " + (this.m_IRClassVal >= 0 ? IRClassL : "") + "\n");
/*  633:     */         
/*  634:     */ 
/*  635:1039 */         break;
/*  636:     */       case 8: 
/*  637:1041 */         text.append("\tSubset evaluation: correlation coefficient\n");
/*  638:1042 */         break;
/*  639:     */       default: 
/*  640:1044 */         text.append("\tSubset evaluation: " + this.m_evaluationMeasure.getReadable());
/*  641:1046 */         if ((((PluginTag)this.m_evaluationMeasure).getMetric() instanceof InformationRetrievalEvaluationMetric)) {
/*  642:1048 */           text.append(" " + (this.m_IRClassVal > 0 ? IRClassL : ""));
/*  643:     */         }
/*  644:1050 */         text.append("\n");
/*  645:     */       }
/*  646:1054 */       text.append("\tNumber of folds for accuracy estimation: " + this.m_folds + "\n");
/*  647:     */     }
/*  648:1058 */     return text.toString();
/*  649:     */   }
/*  650:     */   
/*  651:     */   private boolean repeat(double[] repError, int entries)
/*  652:     */   {
/*  653:1072 */     double mean = 0.0D;
/*  654:1073 */     double variance = 0.0D;
/*  655:1077 */     if (this.m_threshold < 0.0D) {
/*  656:1078 */       return false;
/*  657:     */     }
/*  658:1081 */     if (entries == 1) {
/*  659:1082 */       return true;
/*  660:     */     }
/*  661:1085 */     for (int i = 0; i < entries; i++) {
/*  662:1086 */       mean += repError[i];
/*  663:     */     }
/*  664:1089 */     mean /= entries;
/*  665:1091 */     for (i = 0; i < entries; i++) {
/*  666:1092 */       variance += (repError[i] - mean) * (repError[i] - mean);
/*  667:     */     }
/*  668:1095 */     variance /= entries;
/*  669:1097 */     if (variance > 0.0D) {
/*  670:1098 */       variance = Math.sqrt(variance);
/*  671:     */     }
/*  672:1101 */     if (variance / mean > this.m_threshold) {
/*  673:1102 */       return true;
/*  674:     */     }
/*  675:1105 */     return false;
/*  676:     */   }
/*  677:     */   
/*  678:     */   public String getRevision()
/*  679:     */   {
/*  680:1115 */     return RevisionUtils.extract("$Revision: 12170 $");
/*  681:     */   }
/*  682:     */   
/*  683:     */   public void clean()
/*  684:     */   {
/*  685:1120 */     this.m_trainInstances = new Instances(this.m_trainInstances, 0);
/*  686:     */   }
/*  687:     */   
/*  688:     */   public static void main(String[] args)
/*  689:     */   {
/*  690:1129 */     runEvaluator(new WrapperSubsetEval(), args);
/*  691:     */   }
/*  692:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.WrapperSubsetEval
 * JD-Core Version:    0.7.0.1
 */