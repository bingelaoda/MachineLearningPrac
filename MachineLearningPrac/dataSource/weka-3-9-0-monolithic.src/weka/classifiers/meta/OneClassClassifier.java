/*    1:     */ package weka.classifiers.meta;
/*    2:     */ 
/*    3:     */ import java.io.StringReader;
/*    4:     */ import java.util.ArrayList;
/*    5:     */ import java.util.Arrays;
/*    6:     */ import java.util.Collections;
/*    7:     */ import java.util.Enumeration;
/*    8:     */ import java.util.Random;
/*    9:     */ import java.util.Vector;
/*   10:     */ import weka.classifiers.Classifier;
/*   11:     */ import weka.classifiers.RandomizableSingleClassifierEnhancer;
/*   12:     */ import weka.classifiers.meta.generators.GaussianGenerator;
/*   13:     */ import weka.classifiers.meta.generators.Generator;
/*   14:     */ import weka.classifiers.meta.generators.InstanceHandler;
/*   15:     */ import weka.classifiers.meta.generators.Mean;
/*   16:     */ import weka.classifiers.meta.generators.NominalAttributeGenerator;
/*   17:     */ import weka.classifiers.meta.generators.NominalGenerator;
/*   18:     */ import weka.classifiers.meta.generators.NumericAttributeGenerator;
/*   19:     */ import weka.classifiers.meta.generators.Ranged;
/*   20:     */ import weka.core.Attribute;
/*   21:     */ import weka.core.Capabilities;
/*   22:     */ import weka.core.Capabilities.Capability;
/*   23:     */ import weka.core.DenseInstance;
/*   24:     */ import weka.core.Instance;
/*   25:     */ import weka.core.Instances;
/*   26:     */ import weka.core.Option;
/*   27:     */ import weka.core.TechnicalInformation;
/*   28:     */ import weka.core.TechnicalInformation.Field;
/*   29:     */ import weka.core.TechnicalInformation.Type;
/*   30:     */ import weka.core.TechnicalInformationHandler;
/*   31:     */ import weka.core.Utils;
/*   32:     */ import weka.filters.Filter;
/*   33:     */ import weka.filters.unsupervised.attribute.AddValues;
/*   34:     */ import weka.filters.unsupervised.attribute.MergeManyValues;
/*   35:     */ 
/*   36:     */ public class OneClassClassifier
/*   37:     */   extends RandomizableSingleClassifierEnhancer
/*   38:     */   implements TechnicalInformationHandler
/*   39:     */ {
/*   40:     */   private static final long serialVersionUID = 6199125385010158931L;
/*   41: 274 */   protected double m_TargetRejectionRate = 0.1D;
/*   42: 280 */   protected double m_Threshold = 0.5D;
/*   43:     */   protected ArrayList<Generator> m_Generators;
/*   44: 290 */   protected String m_TargetClassLabel = "target";
/*   45: 295 */   protected int m_NumRepeats = 10;
/*   46: 300 */   protected double m_PercentHeldout = 10.0D;
/*   47: 305 */   protected double m_ProportionGenerated = 0.5D;
/*   48: 310 */   protected NumericAttributeGenerator m_DefaultNumericGenerator = new GaussianGenerator();
/*   49: 315 */   protected NominalAttributeGenerator m_DefaultNominalGenerator = new NominalGenerator();
/*   50:     */   protected AddValues m_AddOutlierFilter;
/*   51: 327 */   protected boolean m_UseLaplaceCorrection = false;
/*   52:     */   protected MergeManyValues m_MergeFilter;
/*   53:     */   public static final String OUTLIER_LABEL = "outlier";
/*   54: 343 */   protected boolean m_UseDensityOnly = false;
/*   55: 349 */   protected boolean m_UseInstanceWeights = false;
/*   56:     */   protected Random m_Random;
/*   57:     */   
/*   58:     */   public OneClassClassifier()
/*   59:     */   {
/*   60: 360 */     this.m_Classifier = new Bagging();
/*   61:     */   }
/*   62:     */   
/*   63:     */   public String globalInfo()
/*   64:     */   {
/*   65: 369 */     return "Performs one-class classification on a dataset.\n\nClassifier reduces the class being classified to just a single class, and learns the datawithout using any information from other classes.  The testing stage will classify as 'target'or 'outlier' - so in order to calculate the outlier pass rate the dataset must contain informationfrom more than one class.\n\nAlso, the output varies depending on whether the label 'outlier' exists in the instances usedto build the classifier.  If so, then 'outlier' will be predicted, if not, then the label willbe considered missing when the prediction does not favour the target class.  The 'outlier' classwill not be used to build the model if there are instances of this class in the dataset.  It cansimply be used as a flag, you do not need to relabel any classes.\n\nFor more information, see:\n\n" + getTechnicalInformation().toString();
/*   66:     */   }
/*   67:     */   
/*   68:     */   public TechnicalInformation getTechnicalInformation()
/*   69:     */   {
/*   70: 395 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.CONFERENCE);
/*   71: 396 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Kathryn Hempstalk and Eibe Frank and Ian H. Witten");
/*   72:     */     
/*   73: 398 */     result.setValue(TechnicalInformation.Field.YEAR, "2008");
/*   74: 399 */     result.setValue(TechnicalInformation.Field.TITLE, "One-Class Classification by Combining Density and Class Probability Estimation");
/*   75:     */     
/*   76:     */ 
/*   77:     */ 
/*   78: 403 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Proceedings of the 12th European Conference on Principles and Practice of Knowledge Discovery in Databases and 19th European Conference on Machine Learning, ECMLPKDD2008");
/*   79:     */     
/*   80:     */ 
/*   81:     */ 
/*   82: 407 */     result.setValue(TechnicalInformation.Field.VOLUME, "Vol. 5211");
/*   83: 408 */     result.setValue(TechnicalInformation.Field.PAGES, "505--519");
/*   84: 409 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Springer");
/*   85: 410 */     result.setValue(TechnicalInformation.Field.ADDRESS, "Berlin");
/*   86: 411 */     result.setValue(TechnicalInformation.Field.SERIES, "Lecture Notes in Computer Science");
/*   87: 412 */     result.setValue(TechnicalInformation.Field.LOCATION, "Antwerp, Belgium");
/*   88: 413 */     result.setValue(TechnicalInformation.Field.MONTH, "September");
/*   89:     */     
/*   90: 415 */     return result;
/*   91:     */   }
/*   92:     */   
/*   93:     */   public Enumeration<Option> listOptions()
/*   94:     */   {
/*   95: 426 */     Vector<Option> result = new Vector();
/*   96:     */     
/*   97: 428 */     result.addElement(new Option("\tSets the target rejection rate\n\t(default: 0.1)", "trr", 1, "-trr <rate>"));
/*   98:     */     
/*   99:     */ 
/*  100: 431 */     result.addElement(new Option("\tSets the target class label\n\t(default: 'target')", "tcl", 1, "-tcl <label>"));
/*  101:     */     
/*  102:     */ 
/*  103: 434 */     result.addElement(new Option("\tSets the number of times to repeat cross validation\n\tto find the threshold\n\t(default: 10)", "cvr", 1, "-cvr <rep>"));
/*  104:     */     
/*  105:     */ 
/*  106:     */ 
/*  107:     */ 
/*  108: 439 */     result.addElement(new Option("\tSets the proportion of generated data\n\t(default: 0.5)", "P", 1, "-P <prop>"));
/*  109:     */     
/*  110:     */ 
/*  111: 442 */     result.addElement(new Option("\tSets the percentage of heldout data for each cross validation\n\tfold\n\t(default: 10)", "cvf", 1, "-cvf <perc>"));
/*  112:     */     
/*  113:     */ 
/*  114:     */ 
/*  115: 446 */     result.addElement(new Option("\tSets the numeric generator\n\t(default: " + GaussianGenerator.class.getName() + ")", "num", 1, "-num <classname + options>"));
/*  116:     */     
/*  117:     */ 
/*  118:     */ 
/*  119: 450 */     result.addElement(new Option("\tSets the nominal generator\n\t(default: " + NominalGenerator.class.getName() + ")", "nom", 1, "-nom <classname + options>"));
/*  120:     */     
/*  121:     */ 
/*  122:     */ 
/*  123: 454 */     result.addElement(new Option("\tSets whether to correct the number of classes to two,\n\tif omitted no correction will be made.", "L", 1, "-L"));
/*  124:     */     
/*  125:     */ 
/*  126:     */ 
/*  127: 458 */     result.addElement(new Option("\tSets whether to exclusively use the density estimate.", "E", 0, "-E"));
/*  128:     */     
/*  129:     */ 
/*  130: 461 */     result.addElement(new Option("\tSets whether to use instance weights.", "I", 0, "-I"));
/*  131:     */     
/*  132:     */ 
/*  133: 464 */     result.addAll(Collections.list(super.listOptions()));
/*  134:     */     
/*  135: 466 */     return result.elements();
/*  136:     */   }
/*  137:     */   
/*  138:     */   public void setOptions(String[] options)
/*  139:     */     throws Exception
/*  140:     */   {
/*  141: 627 */     String tmpStr = Utils.getOption("num", options);
/*  142: 628 */     if (tmpStr.length() != 0)
/*  143:     */     {
/*  144: 629 */       String[] tmpOptions = Utils.splitOptions(tmpStr);
/*  145: 630 */       tmpStr = tmpOptions[0];
/*  146: 631 */       tmpOptions[0] = "";
/*  147: 632 */       setNumericGenerator((NumericAttributeGenerator)Utils.forName(Generator.class, tmpStr, tmpOptions));
/*  148:     */     }
/*  149:     */     else
/*  150:     */     {
/*  151: 635 */       setNumericGenerator((NumericAttributeGenerator)Utils.forName(Generator.class, defaultNumericGeneratorString(), null));
/*  152:     */     }
/*  153: 640 */     tmpStr = Utils.getOption("nom", options);
/*  154: 641 */     if (tmpStr.length() != 0)
/*  155:     */     {
/*  156: 642 */       String[] tmpOptions = Utils.splitOptions(tmpStr);
/*  157: 643 */       tmpStr = tmpOptions[0];
/*  158: 644 */       tmpOptions[0] = "";
/*  159: 645 */       setNominalGenerator((NominalAttributeGenerator)Utils.forName(Generator.class, tmpStr, tmpOptions));
/*  160:     */     }
/*  161:     */     else
/*  162:     */     {
/*  163: 648 */       setNominalGenerator((NominalAttributeGenerator)Utils.forName(Generator.class, defaultNominalGeneratorString(), null));
/*  164:     */     }
/*  165: 653 */     tmpStr = Utils.getOption("trr", options);
/*  166: 654 */     if (tmpStr.length() != 0) {
/*  167: 655 */       setTargetRejectionRate(Double.parseDouble(tmpStr));
/*  168:     */     } else {
/*  169: 657 */       setTargetRejectionRate(0.1D);
/*  170:     */     }
/*  171: 661 */     tmpStr = Utils.getOption("tcl", options);
/*  172: 662 */     if (tmpStr.length() != 0) {
/*  173: 663 */       setTargetClassLabel(tmpStr);
/*  174:     */     } else {
/*  175: 665 */       setTargetClassLabel("target");
/*  176:     */     }
/*  177: 669 */     tmpStr = Utils.getOption("cvr", options);
/*  178: 670 */     if (tmpStr.length() != 0) {
/*  179: 671 */       setNumRepeats(Integer.parseInt(tmpStr));
/*  180:     */     } else {
/*  181: 673 */       setNumRepeats(10);
/*  182:     */     }
/*  183: 677 */     tmpStr = Utils.getOption("cvf", options);
/*  184: 678 */     if (tmpStr.length() != 0) {
/*  185: 679 */       setPercentageHeldout(Double.parseDouble(tmpStr));
/*  186:     */     } else {
/*  187: 681 */       setPercentageHeldout(10.0D);
/*  188:     */     }
/*  189: 685 */     tmpStr = Utils.getOption("P", options);
/*  190: 686 */     if (tmpStr.length() != 0) {
/*  191: 687 */       setProportionGenerated(Double.parseDouble(tmpStr));
/*  192:     */     } else {
/*  193: 689 */       setProportionGenerated(0.5D);
/*  194:     */     }
/*  195: 693 */     setUseLaplaceCorrection(Utils.getFlag('L', options));
/*  196:     */     
/*  197:     */ 
/*  198: 696 */     setDensityOnly(Utils.getFlag('E', options));
/*  199:     */     
/*  200:     */ 
/*  201: 699 */     setUseInstanceWeights(Utils.getFlag('I', options));
/*  202:     */     
/*  203:     */ 
/*  204: 702 */     super.setOptions(options);
/*  205:     */     
/*  206: 704 */     Utils.checkForRemainingOptions(options);
/*  207:     */   }
/*  208:     */   
/*  209:     */   public String[] getOptions()
/*  210:     */   {
/*  211: 715 */     Vector<String> result = new Vector();
/*  212:     */     
/*  213: 717 */     result.add("-num");
/*  214: 718 */     result.add(this.m_DefaultNumericGenerator.getClass().getName() + " " + Utils.joinOptions(((Generator)this.m_DefaultNumericGenerator).getOptions()));
/*  215:     */     
/*  216:     */ 
/*  217:     */ 
/*  218:     */ 
/*  219:     */ 
/*  220: 724 */     result.add("-nom");
/*  221: 725 */     result.add(this.m_DefaultNominalGenerator.getClass().getName() + " " + Utils.joinOptions(((Generator)this.m_DefaultNominalGenerator).getOptions()));
/*  222:     */     
/*  223:     */ 
/*  224:     */ 
/*  225:     */ 
/*  226:     */ 
/*  227: 731 */     result.add("-trr");
/*  228: 732 */     result.add("" + this.m_TargetRejectionRate);
/*  229:     */     
/*  230: 734 */     result.add("-tcl");
/*  231: 735 */     result.add("" + this.m_TargetClassLabel);
/*  232:     */     
/*  233: 737 */     result.add("-cvr");
/*  234: 738 */     result.add("" + this.m_NumRepeats);
/*  235:     */     
/*  236: 740 */     result.add("-cvf");
/*  237: 741 */     result.add("" + this.m_PercentHeldout);
/*  238:     */     
/*  239: 743 */     result.add("-P");
/*  240: 744 */     result.add("" + this.m_ProportionGenerated);
/*  241: 746 */     if (this.m_UseLaplaceCorrection) {
/*  242: 747 */       result.add("-L");
/*  243:     */     }
/*  244: 750 */     if (this.m_UseDensityOnly) {
/*  245: 751 */       result.add("-E");
/*  246:     */     }
/*  247: 754 */     if (this.m_UseInstanceWeights) {
/*  248: 755 */       result.add("-I");
/*  249:     */     }
/*  250: 758 */     Collections.addAll(result, super.getOptions());
/*  251:     */     
/*  252: 760 */     return (String[])result.toArray(new String[result.size()]);
/*  253:     */   }
/*  254:     */   
/*  255:     */   public boolean getDensityOnly()
/*  256:     */   {
/*  257: 771 */     return this.m_UseDensityOnly;
/*  258:     */   }
/*  259:     */   
/*  260:     */   public void setDensityOnly(boolean density)
/*  261:     */   {
/*  262: 780 */     this.m_UseDensityOnly = density;
/*  263:     */   }
/*  264:     */   
/*  265:     */   public String densityOnlyTipText()
/*  266:     */   {
/*  267: 790 */     return "If true, the density estimate will be used by itself.";
/*  268:     */   }
/*  269:     */   
/*  270:     */   public double getTargetRejectionRate()
/*  271:     */   {
/*  272: 800 */     return this.m_TargetRejectionRate;
/*  273:     */   }
/*  274:     */   
/*  275:     */   public void setTargetRejectionRate(double rate)
/*  276:     */   {
/*  277: 809 */     this.m_TargetRejectionRate = rate;
/*  278:     */   }
/*  279:     */   
/*  280:     */   public String targetRejectionRateTipText()
/*  281:     */   {
/*  282: 819 */     return "The target rejection rate, ie, the proportion of target class samples that will be rejected in order to build a threshold.";
/*  283:     */   }
/*  284:     */   
/*  285:     */   public String getTargetClassLabel()
/*  286:     */   {
/*  287: 830 */     return this.m_TargetClassLabel;
/*  288:     */   }
/*  289:     */   
/*  290:     */   public void setTargetClassLabel(String label)
/*  291:     */   {
/*  292: 839 */     this.m_TargetClassLabel = label;
/*  293:     */   }
/*  294:     */   
/*  295:     */   public String targetClassLabelTipText()
/*  296:     */   {
/*  297: 849 */     return "The class label to perform one-class classification on.";
/*  298:     */   }
/*  299:     */   
/*  300:     */   public int getNumRepeats()
/*  301:     */   {
/*  302: 858 */     return this.m_NumRepeats;
/*  303:     */   }
/*  304:     */   
/*  305:     */   public void setNumRepeats(int repeats)
/*  306:     */   {
/*  307: 867 */     this.m_NumRepeats = repeats;
/*  308:     */   }
/*  309:     */   
/*  310:     */   public String numRepeatsTipText()
/*  311:     */   {
/*  312: 877 */     return "The number of repeats for (internal) cross-validation.";
/*  313:     */   }
/*  314:     */   
/*  315:     */   public void setProportionGenerated(double prop)
/*  316:     */   {
/*  317: 886 */     this.m_ProportionGenerated = prop;
/*  318:     */   }
/*  319:     */   
/*  320:     */   public double getProportionGenerated()
/*  321:     */   {
/*  322: 896 */     return this.m_ProportionGenerated;
/*  323:     */   }
/*  324:     */   
/*  325:     */   public String proportionGeneratedTipText()
/*  326:     */   {
/*  327: 906 */     return "The proportion of data that will be generated compared to the target class label.";
/*  328:     */   }
/*  329:     */   
/*  330:     */   public void setPercentageHeldout(double percent)
/*  331:     */   {
/*  332: 916 */     this.m_PercentHeldout = percent;
/*  333:     */   }
/*  334:     */   
/*  335:     */   public double getPercentageHeldout()
/*  336:     */   {
/*  337: 926 */     return this.m_PercentHeldout;
/*  338:     */   }
/*  339:     */   
/*  340:     */   public String percentageHeldoutTipText()
/*  341:     */   {
/*  342: 936 */     return "The percentage of data that will be heldout in each iteration of (internal) cross-validation.";
/*  343:     */   }
/*  344:     */   
/*  345:     */   public NumericAttributeGenerator getNumericGenerator()
/*  346:     */   {
/*  347: 947 */     return this.m_DefaultNumericGenerator;
/*  348:     */   }
/*  349:     */   
/*  350:     */   public void setNumericGenerator(NumericAttributeGenerator agen)
/*  351:     */   {
/*  352: 957 */     this.m_DefaultNumericGenerator = agen;
/*  353:     */   }
/*  354:     */   
/*  355:     */   public String numericGeneratorTipText()
/*  356:     */   {
/*  357: 967 */     return "The numeric data generator to use.";
/*  358:     */   }
/*  359:     */   
/*  360:     */   public NominalAttributeGenerator getNominalGenerator()
/*  361:     */   {
/*  362: 977 */     return this.m_DefaultNominalGenerator;
/*  363:     */   }
/*  364:     */   
/*  365:     */   public void setNominalGenerator(NominalAttributeGenerator agen)
/*  366:     */   {
/*  367: 987 */     this.m_DefaultNominalGenerator = agen;
/*  368:     */   }
/*  369:     */   
/*  370:     */   public String nominalGeneratorTipText()
/*  371:     */   {
/*  372: 997 */     return "The nominal data generator to use.";
/*  373:     */   }
/*  374:     */   
/*  375:     */   public boolean getUseLaplaceCorrection()
/*  376:     */   {
/*  377:1006 */     return this.m_UseLaplaceCorrection;
/*  378:     */   }
/*  379:     */   
/*  380:     */   public void setUseLaplaceCorrection(boolean newuse)
/*  381:     */   {
/*  382:1019 */     this.m_UseLaplaceCorrection = newuse;
/*  383:     */   }
/*  384:     */   
/*  385:     */   public String useLaplaceCorrectionTipText()
/*  386:     */   {
/*  387:1029 */     return "If true, then Laplace correction will be used (reduces the number of class labels to two, target and outlier class, regardless of how many class labels actually exist) - useful for classifiers that use the number of class labels to make use of a Laplace value based on the unseen class.";
/*  388:     */   }
/*  389:     */   
/*  390:     */   public void setUseInstanceWeights(boolean newuse)
/*  391:     */   {
/*  392:1043 */     this.m_UseInstanceWeights = newuse;
/*  393:     */   }
/*  394:     */   
/*  395:     */   public boolean getUseInstanceWeights()
/*  396:     */   {
/*  397:1052 */     return this.m_UseInstanceWeights;
/*  398:     */   }
/*  399:     */   
/*  400:     */   public String useInstanceWeightsTipText()
/*  401:     */   {
/*  402:1062 */     return "If true, the weighting on instances is based on their prevalence in the data.";
/*  403:     */   }
/*  404:     */   
/*  405:     */   protected String defaultClassifierString()
/*  406:     */   {
/*  407:1073 */     return "weka.classifiers.meta.Bagging";
/*  408:     */   }
/*  409:     */   
/*  410:     */   protected String defaultNumericGeneratorString()
/*  411:     */   {
/*  412:1082 */     return "weka.classifiers.meta.generators.GaussianGenerator";
/*  413:     */   }
/*  414:     */   
/*  415:     */   protected String defaultNominalGeneratorString()
/*  416:     */   {
/*  417:1091 */     return "weka.classifiers.meta.generators.NominalGenerator";
/*  418:     */   }
/*  419:     */   
/*  420:     */   public Capabilities getCapabilities()
/*  421:     */   {
/*  422:1103 */     Capabilities result = super.getCapabilities();
/*  423:     */     
/*  424:     */ 
/*  425:1106 */     result.disableAllClasses();
/*  426:1107 */     result.disableAllClassDependencies();
/*  427:1108 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  428:1109 */     result.enable(Capabilities.Capability.UNARY_CLASS);
/*  429:     */     
/*  430:1111 */     return result;
/*  431:     */   }
/*  432:     */   
/*  433:     */   public void buildClassifier(Instances data)
/*  434:     */     throws Exception
/*  435:     */   {
/*  436:1124 */     if (this.m_Classifier == null) {
/*  437:1125 */       throw new Exception("No base classifier has been set!");
/*  438:     */     }
/*  439:1128 */     getCapabilities().testWithFail(data);
/*  440:     */     
/*  441:     */ 
/*  442:1131 */     Instances newData = new Instances(data);
/*  443:     */     
/*  444:1133 */     this.m_Random = new Random(this.m_Seed);
/*  445:     */     
/*  446:     */ 
/*  447:1136 */     Attribute classAttribute = newData.classAttribute();
/*  448:1137 */     double targetClassValue = classAttribute.indexOfValue(this.m_TargetClassLabel);
/*  449:1138 */     if (targetClassValue == -1.0D) {
/*  450:1139 */       throw new Exception("Target class value doesn't exist!");
/*  451:     */     }
/*  452:1142 */     int index = 0;
/*  453:1143 */     while (index < newData.numInstances())
/*  454:     */     {
/*  455:1144 */       Instance aninst = newData.instance(index);
/*  456:1145 */       if (aninst.classValue() != targetClassValue) {
/*  457:1146 */         newData.delete(index);
/*  458:     */       } else {
/*  459:1148 */         index++;
/*  460:     */       }
/*  461:     */     }
/*  462:1152 */     if (newData.numInstances() == 0) {
/*  463:1153 */       throw new Exception("No instances found belonging to the target class!");
/*  464:     */     }
/*  465:1157 */     this.m_AddOutlierFilter = new AddValues();
/*  466:1158 */     this.m_AddOutlierFilter.setAttributeIndex("" + (newData.classIndex() + 1));
/*  467:1159 */     this.m_AddOutlierFilter.setLabels("outlier");
/*  468:1160 */     this.m_AddOutlierFilter.setInputFormat(newData);
/*  469:1161 */     newData = Filter.useFilter(newData, this.m_AddOutlierFilter);
/*  470:1163 */     if (this.m_UseLaplaceCorrection) {
/*  471:1164 */       newData = mergeToTwo(newData);
/*  472:     */     }
/*  473:1168 */     this.m_Generators = new ArrayList();
/*  474:     */     
/*  475:     */ 
/*  476:     */ 
/*  477:1172 */     int numAttributes = newData.numAttributes();
/*  478:     */     
/*  479:     */ 
/*  480:1175 */     double[] lowranges = new double[numAttributes - 1];
/*  481:1176 */     double[] highranges = new double[numAttributes - 1];
/*  482:1177 */     double[] means = new double[numAttributes - 1];
/*  483:1178 */     double[] stddevs = new double[numAttributes - 1];
/*  484:1179 */     double[] instanceCount = new double[numAttributes - 1];
/*  485:1180 */     int[] attrIndexes = new int[numAttributes - 1];
/*  486:1183 */     for (int i = 0; i < numAttributes - 1; i++)
/*  487:     */     {
/*  488:1184 */       lowranges[i] = 1.7976931348623157E+308D;
/*  489:1185 */       highranges[i] = -1.797693134862316E+308D;
/*  490:1186 */       means[i] = 0.0D;
/*  491:1187 */       stddevs[i] = 0.0D;
/*  492:1188 */       attrIndexes[i] = 0;
/*  493:1189 */       instanceCount[i] = 0.0D;
/*  494:     */     }
/*  495:1194 */     for (int i = 0; i < newData.numInstances(); i++)
/*  496:     */     {
/*  497:1195 */       Instance anInst = newData.instance(i);
/*  498:1196 */       int attCount = 0;
/*  499:1197 */       for (int j = 0; j < numAttributes; j++) {
/*  500:1198 */         if (j != newData.classIndex())
/*  501:     */         {
/*  502:1199 */           double attVal = anInst.value(j);
/*  503:1200 */           if (!anInst.isMissing(j))
/*  504:     */           {
/*  505:1201 */             if (attVal > highranges[attCount]) {
/*  506:1202 */               highranges[attCount] = attVal;
/*  507:     */             }
/*  508:1204 */             if (attVal < lowranges[attCount]) {
/*  509:1205 */               lowranges[attCount] = attVal;
/*  510:     */             }
/*  511:1208 */             means[attCount] += attVal;
/*  512:1209 */             instanceCount[attCount] += 1.0D;
/*  513:     */           }
/*  514:1211 */           attrIndexes[attCount] = j;
/*  515:1212 */           attCount++;
/*  516:     */         }
/*  517:     */       }
/*  518:     */     }
/*  519:1219 */     for (int i = 0; i < numAttributes - 1; i++) {
/*  520:1220 */       if (instanceCount[i] > 0.0D) {
/*  521:1221 */         means[i] /= instanceCount[i];
/*  522:     */       }
/*  523:     */     }
/*  524:1226 */     for (int i = 0; i < newData.numInstances(); i++)
/*  525:     */     {
/*  526:1227 */       Instance anInst = newData.instance(i);
/*  527:1228 */       int attCount = 0;
/*  528:1229 */       for (int j = 0; j < numAttributes - 1; j++) {
/*  529:1230 */         if (instanceCount[j] > 0.0D)
/*  530:     */         {
/*  531:1231 */           stddevs[attCount] += Math.pow(anInst.value(j) - means[attCount], 2.0D);
/*  532:1232 */           attCount++;
/*  533:     */         }
/*  534:     */       }
/*  535:     */     }
/*  536:1237 */     for (int i = 0; i < numAttributes - 1; i++) {
/*  537:1238 */       if (instanceCount[i] > 0.0D) {
/*  538:1239 */         stddevs[i] = Math.sqrt(stddevs[i] / instanceCount[i]);
/*  539:     */       }
/*  540:     */     }
/*  541:1244 */     for (int i = 0; i < numAttributes - 1; i++)
/*  542:     */     {
/*  543:     */       Generator agen;
/*  544:1246 */       if (newData.attribute(attrIndexes[i]).isNominal())
/*  545:     */       {
/*  546:1247 */         Generator agen = ((Generator)this.m_DefaultNominalGenerator).copy();
/*  547:1248 */         ((NominalAttributeGenerator)agen).buildGenerator(newData, newData.attribute(attrIndexes[i]));
/*  548:     */       }
/*  549:     */       else
/*  550:     */       {
/*  551:1251 */         agen = ((Generator)this.m_DefaultNumericGenerator).copy();
/*  552:1253 */         if ((agen instanceof Ranged))
/*  553:     */         {
/*  554:1254 */           ((Ranged)agen).setLowerRange(lowranges[i]);
/*  555:1255 */           ((Ranged)agen).setUpperRange(highranges[i]);
/*  556:     */         }
/*  557:1258 */         if ((agen instanceof Mean))
/*  558:     */         {
/*  559:1259 */           ((Mean)agen).setMean(means[i]);
/*  560:1260 */           ((Mean)agen).setStandardDeviation(stddevs[i]);
/*  561:     */         }
/*  562:1263 */         if ((agen instanceof InstanceHandler))
/*  563:     */         {
/*  564:1267 */           StringBuffer sb = new StringBuffer("@relation OneClass-SingleAttribute\n\n");
/*  565:     */           
/*  566:1269 */           sb.append("@attribute tempName numeric\n\n");
/*  567:1270 */           sb.append("@data\n\n");
/*  568:1271 */           Enumeration<Instance> instancesEnum = newData.enumerateInstances();
/*  569:1272 */           while (instancesEnum.hasMoreElements())
/*  570:     */           {
/*  571:1273 */             Instance aninst = (Instance)instancesEnum.nextElement();
/*  572:1274 */             if (!aninst.isMissing(attrIndexes[i])) {
/*  573:1275 */               sb.append("" + aninst.value(attrIndexes[i]) + "\n");
/*  574:     */             }
/*  575:     */           }
/*  576:1278 */           sb.append("\n\n");
/*  577:1279 */           Instances removed = new Instances(new StringReader(sb.toString()));
/*  578:1280 */           removed.deleteWithMissing(0);
/*  579:1281 */           ((InstanceHandler)agen).buildGenerator(removed);
/*  580:     */         }
/*  581:     */       }
/*  582:1285 */       this.m_Generators.add(agen);
/*  583:     */     }
/*  584:1289 */     ArrayList<Double> thresholds = new ArrayList();
/*  585:1290 */     for (int i = 0; i < this.m_NumRepeats; i++)
/*  586:     */     {
/*  587:1293 */       Instances copyData = new Instances(newData);
/*  588:1294 */       Instances heldout = new Instances(newData, 0);
/*  589:1295 */       for (int k = 0; k < newData.numInstances() / this.m_PercentHeldout; k++)
/*  590:     */       {
/*  591:1296 */         int anindex = this.m_Random.nextInt(copyData.numInstances());
/*  592:1297 */         heldout.add(copyData.instance(anindex));
/*  593:1298 */         copyData.delete(anindex);
/*  594:     */       }
/*  595:1302 */       generateData(copyData);
/*  596:1305 */       if (!this.m_UseDensityOnly) {
/*  597:1306 */         this.m_Classifier.buildClassifier(copyData);
/*  598:     */       }
/*  599:1310 */       double[] scores = new double[heldout.numInstances()];
/*  600:1311 */       Enumeration<Instance> iterInst = heldout.enumerateInstances();
/*  601:1312 */       int classIndex = heldout.classAttribute().indexOfValue(this.m_TargetClassLabel);
/*  602:     */       
/*  603:1314 */       int count = 0;
/*  604:1315 */       while (iterInst.hasMoreElements())
/*  605:     */       {
/*  606:1316 */         Instance anInst = (Instance)iterInst.nextElement();
/*  607:1317 */         scores[count] = getProbXGivenC(anInst, classIndex);
/*  608:1318 */         count++;
/*  609:     */       }
/*  610:1321 */       Arrays.sort(scores);
/*  611:     */       
/*  612:     */ 
/*  613:     */ 
/*  614:1325 */       int passposition = (int)(heldout.numInstances() * this.m_TargetRejectionRate);
/*  615:1326 */       if (passposition >= heldout.numInstances()) {
/*  616:1327 */         passposition = heldout.numInstances() - 1;
/*  617:     */       }
/*  618:1330 */       thresholds.add(new Double(scores[passposition]));
/*  619:     */     }
/*  620:1337 */     this.m_Threshold = 0.0D;
/*  621:1338 */     for (int k = 0; k < thresholds.size(); k++) {
/*  622:1339 */       this.m_Threshold += ((Double)thresholds.get(k)).doubleValue();
/*  623:     */     }
/*  624:1341 */     this.m_Threshold /= thresholds.size();
/*  625:     */     
/*  626:     */ 
/*  627:1344 */     generateData(newData);
/*  628:1346 */     if (!this.m_UseDensityOnly) {
/*  629:1347 */       this.m_Classifier.buildClassifier(newData);
/*  630:     */     }
/*  631:     */   }
/*  632:     */   
/*  633:     */   protected Instances mergeToTwo(Instances newData)
/*  634:     */     throws Exception
/*  635:     */   {
/*  636:1361 */     this.m_MergeFilter = new MergeManyValues();
/*  637:1362 */     this.m_MergeFilter.setAttributeIndex("" + (newData.classIndex() + 1));
/*  638:     */     
/*  639:     */ 
/*  640:     */ 
/*  641:1366 */     StringBuffer sb = new StringBuffer("");
/*  642:     */     
/*  643:1368 */     Attribute theAttr = newData.classAttribute();
/*  644:1369 */     for (int i = 0; i < theAttr.numValues(); i++) {
/*  645:1370 */       if ((!theAttr.value(i).equalsIgnoreCase("outlier")) && (!theAttr.value(i).equalsIgnoreCase(this.m_TargetClassLabel))) {
/*  646:1373 */         sb.append(i + 1 + ",");
/*  647:     */       }
/*  648:     */     }
/*  649:1376 */     String mergeList = sb.toString();
/*  650:1377 */     if (mergeList.length() != 0)
/*  651:     */     {
/*  652:1378 */       mergeList = mergeList.substring(0, mergeList.length() - 1);
/*  653:1379 */       int classIndex = newData.classIndex();
/*  654:1380 */       newData.setClassIndex(-1);
/*  655:1381 */       this.m_MergeFilter.setMergeValueRange(mergeList);
/*  656:1382 */       this.m_MergeFilter.setLabel("outlier");
/*  657:1383 */       this.m_MergeFilter.setInputFormat(newData);
/*  658:1384 */       newData = Filter.useFilter(newData, this.m_MergeFilter);
/*  659:1385 */       newData.setClassIndex(classIndex);
/*  660:     */     }
/*  661:     */     else
/*  662:     */     {
/*  663:1387 */       this.m_MergeFilter = null;
/*  664:     */     }
/*  665:1390 */     return newData;
/*  666:     */   }
/*  667:     */   
/*  668:     */   protected double getProbXGivenC(Instance instance, int targetClassIndex)
/*  669:     */     throws Exception
/*  670:     */   {
/*  671:1403 */     double probC = 1.0D - this.m_ProportionGenerated;
/*  672:1404 */     double probXgivenA = 0.0D;
/*  673:1405 */     int count = 0;
/*  674:1406 */     for (int i = 0; i < instance.numAttributes(); i++) {
/*  675:1407 */       if (i != instance.classIndex())
/*  676:     */       {
/*  677:1408 */         Generator agen = (Generator)this.m_Generators.get(count);
/*  678:1409 */         if (!instance.isMissing(i)) {
/*  679:1410 */           probXgivenA += agen.getLogProbabilityOf(instance.value(i));
/*  680:     */         }
/*  681:1412 */         count++;
/*  682:     */       }
/*  683:     */     }
/*  684:1416 */     if (this.m_UseDensityOnly) {
/*  685:1417 */       return probXgivenA;
/*  686:     */     }
/*  687:1420 */     double[] distribution = this.m_Classifier.distributionForInstance(instance);
/*  688:1421 */     double probCgivenX = distribution[targetClassIndex];
/*  689:1422 */     if (probCgivenX == 1.0D) {
/*  690:1423 */       return (1.0D / 0.0D);
/*  691:     */     }
/*  692:1427 */     double top = Math.log(1.0D - probC) + Math.log(probCgivenX);
/*  693:1428 */     double bottom = Math.log(probC) + Math.log(1.0D - probCgivenX);
/*  694:     */     
/*  695:1430 */     return top - bottom + probXgivenA;
/*  696:     */   }
/*  697:     */   
/*  698:     */   protected Instances generateData(Instances targetData)
/*  699:     */   {
/*  700:1441 */     double totalInstances = targetData.numInstances() / (1.0D - this.m_ProportionGenerated);
/*  701:     */     
/*  702:     */ 
/*  703:1444 */     int numInstances = (int)(totalInstances - targetData.numInstances());
/*  704:1447 */     if (this.m_UseInstanceWeights) {
/*  705:1448 */       for (int i = 0; i < targetData.numInstances(); i++) {
/*  706:1449 */         targetData.instance(i).setWeight(0.5D * (1.0D / (1.0D - this.m_ProportionGenerated)));
/*  707:     */       }
/*  708:     */     }
/*  709:1454 */     for (int j = 0; j < numInstances; j++)
/*  710:     */     {
/*  711:1456 */       Instance anInst = new DenseInstance(targetData.numAttributes());
/*  712:1457 */       anInst.setDataset(targetData);
/*  713:1458 */       int position = 0;
/*  714:1459 */       for (int i = 0; i < targetData.numAttributes(); i++) {
/*  715:1460 */         if (targetData.classIndex() != i)
/*  716:     */         {
/*  717:1462 */           Generator agen = (Generator)this.m_Generators.get(position);
/*  718:1463 */           anInst.setValue(i, agen.generate());
/*  719:1464 */           position++;
/*  720:     */         }
/*  721:     */         else
/*  722:     */         {
/*  723:1467 */           anInst.setValue(i, "outlier");
/*  724:1468 */           if (this.m_UseInstanceWeights) {
/*  725:1469 */             anInst.setWeight(0.5D * (1.0D / this.m_ProportionGenerated));
/*  726:     */           }
/*  727:     */         }
/*  728:     */       }
/*  729:1474 */       targetData.add(anInst);
/*  730:     */     }
/*  731:1477 */     return targetData;
/*  732:     */   }
/*  733:     */   
/*  734:     */   public double[] distributionForInstance(Instance instance)
/*  735:     */     throws Exception
/*  736:     */   {
/*  737:1488 */     Instance filtered = (Instance)instance.copy();
/*  738:     */     
/*  739:1490 */     this.m_AddOutlierFilter.input(instance);
/*  740:1491 */     filtered = this.m_AddOutlierFilter.output();
/*  741:1493 */     if ((this.m_UseLaplaceCorrection) && (this.m_MergeFilter != null))
/*  742:     */     {
/*  743:1494 */       this.m_MergeFilter.input(filtered);
/*  744:1495 */       filtered = this.m_MergeFilter.output();
/*  745:     */     }
/*  746:1498 */     double[] dist = new double[instance.numClasses()];
/*  747:1499 */     double probForOutlierClass = 1.0D / (1.0D + Math.exp(getProbXGivenC(filtered, filtered.classAttribute().indexOfValue(this.m_TargetClassLabel)) - this.m_Threshold));
/*  748:1502 */     if (getProbXGivenC(filtered, filtered.classAttribute().indexOfValue(this.m_TargetClassLabel)) == (1.0D / 0.0D)) {
/*  749:1504 */       probForOutlierClass = 0.0D;
/*  750:     */     }
/*  751:1507 */     dist[instance.classAttribute().indexOfValue(this.m_TargetClassLabel)] = (1.0D - probForOutlierClass);
/*  752:1508 */     if (instance.classAttribute().indexOfValue("outlier") == -1)
/*  753:     */     {
/*  754:1510 */       if (getProbXGivenC(filtered, filtered.classAttribute().indexOfValue(this.m_TargetClassLabel)) >= this.m_Threshold) {
/*  755:1512 */         dist[instance.classAttribute().indexOfValue(this.m_TargetClassLabel)] = 1.0D;
/*  756:     */       } else {
/*  757:1514 */         dist[instance.classAttribute().indexOfValue(this.m_TargetClassLabel)] = 0.0D;
/*  758:     */       }
/*  759:     */     }
/*  760:     */     else {
/*  761:1517 */       dist[instance.classAttribute().indexOfValue("outlier")] = probForOutlierClass;
/*  762:     */     }
/*  763:1521 */     return dist;
/*  764:     */   }
/*  765:     */   
/*  766:     */   public String toString()
/*  767:     */   {
/*  768:1532 */     StringBuffer result = new StringBuffer();
/*  769:1533 */     result.append("\n\nClassifier Model\n" + this.m_Classifier.toString());
/*  770:     */     
/*  771:1535 */     return result.toString();
/*  772:     */   }
/*  773:     */   
/*  774:     */   public String getRevision()
/*  775:     */   {
/*  776:1545 */     return "$Revision: 10374 $";
/*  777:     */   }
/*  778:     */   
/*  779:     */   public static void main(String[] args)
/*  780:     */   {
/*  781:1554 */     runClassifier(new OneClassClassifier(), args);
/*  782:     */   }
/*  783:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.OneClassClassifier
 * JD-Core Version:    0.7.0.1
 */