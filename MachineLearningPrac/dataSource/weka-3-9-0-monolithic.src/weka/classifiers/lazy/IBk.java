/*    1:     */ package weka.classifiers.lazy;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.util.Collections;
/*    5:     */ import java.util.Enumeration;
/*    6:     */ import java.util.Vector;
/*    7:     */ import weka.classifiers.AbstractClassifier;
/*    8:     */ import weka.classifiers.UpdateableClassifier;
/*    9:     */ import weka.classifiers.rules.ZeroR;
/*   10:     */ import weka.core.AdditionalMeasureProducer;
/*   11:     */ import weka.core.Attribute;
/*   12:     */ import weka.core.Capabilities;
/*   13:     */ import weka.core.Capabilities.Capability;
/*   14:     */ import weka.core.Instance;
/*   15:     */ import weka.core.Instances;
/*   16:     */ import weka.core.Option;
/*   17:     */ import weka.core.OptionHandler;
/*   18:     */ import weka.core.RevisionUtils;
/*   19:     */ import weka.core.SelectedTag;
/*   20:     */ import weka.core.Tag;
/*   21:     */ import weka.core.TechnicalInformation;
/*   22:     */ import weka.core.TechnicalInformation.Field;
/*   23:     */ import weka.core.TechnicalInformation.Type;
/*   24:     */ import weka.core.TechnicalInformationHandler;
/*   25:     */ import weka.core.Utils;
/*   26:     */ import weka.core.WeightedInstancesHandler;
/*   27:     */ import weka.core.neighboursearch.CoverTree;
/*   28:     */ import weka.core.neighboursearch.LinearNNSearch;
/*   29:     */ import weka.core.neighboursearch.NearestNeighbourSearch;
/*   30:     */ 
/*   31:     */ public class IBk
/*   32:     */   extends AbstractClassifier
/*   33:     */   implements OptionHandler, UpdateableClassifier, WeightedInstancesHandler, TechnicalInformationHandler, AdditionalMeasureProducer
/*   34:     */ {
/*   35:     */   static final long serialVersionUID = -3080186098777067172L;
/*   36:     */   protected Instances m_Train;
/*   37:     */   protected int m_NumClasses;
/*   38:     */   protected int m_ClassType;
/*   39:     */   protected int m_kNN;
/*   40:     */   protected int m_kNNUpper;
/*   41:     */   protected boolean m_kNNValid;
/*   42:     */   protected int m_WindowSize;
/*   43:     */   protected int m_DistanceWeighting;
/*   44:     */   protected boolean m_CrossValidate;
/*   45:     */   protected boolean m_MeanSquared;
/*   46:     */   protected ZeroR m_defaultModel;
/*   47:     */   public static final int WEIGHT_NONE = 1;
/*   48:     */   public static final int WEIGHT_INVERSE = 2;
/*   49:     */   public static final int WEIGHT_SIMILARITY = 4;
/*   50: 177 */   public static final Tag[] TAGS_WEIGHTING = { new Tag(1, "No distance weighting"), new Tag(2, "Weight by 1/distance"), new Tag(4, "Weight by 1-distance") };
/*   51: 184 */   protected NearestNeighbourSearch m_NNSearch = new LinearNNSearch();
/*   52:     */   protected double m_NumAttributesUsed;
/*   53:     */   
/*   54:     */   public IBk(int k)
/*   55:     */   {
/*   56: 198 */     init();
/*   57: 199 */     setKNN(k);
/*   58:     */   }
/*   59:     */   
/*   60:     */   public IBk()
/*   61:     */   {
/*   62: 208 */     init();
/*   63:     */   }
/*   64:     */   
/*   65:     */   public String globalInfo()
/*   66:     */   {
/*   67: 218 */     return "K-nearest neighbours classifier. Can select appropriate value of K based on cross-validation. Can also do distance weighting.\n\nFor more information, see\n\n" + getTechnicalInformation().toString();
/*   68:     */   }
/*   69:     */   
/*   70:     */   public TechnicalInformation getTechnicalInformation()
/*   71:     */   {
/*   72: 235 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*   73: 236 */     result.setValue(TechnicalInformation.Field.AUTHOR, "D. Aha and D. Kibler");
/*   74: 237 */     result.setValue(TechnicalInformation.Field.YEAR, "1991");
/*   75: 238 */     result.setValue(TechnicalInformation.Field.TITLE, "Instance-based learning algorithms");
/*   76: 239 */     result.setValue(TechnicalInformation.Field.JOURNAL, "Machine Learning");
/*   77: 240 */     result.setValue(TechnicalInformation.Field.VOLUME, "6");
/*   78: 241 */     result.setValue(TechnicalInformation.Field.PAGES, "37-66");
/*   79:     */     
/*   80: 243 */     return result;
/*   81:     */   }
/*   82:     */   
/*   83:     */   public String KNNTipText()
/*   84:     */   {
/*   85: 252 */     return "The number of neighbours to use.";
/*   86:     */   }
/*   87:     */   
/*   88:     */   public void setKNN(int k)
/*   89:     */   {
/*   90: 261 */     this.m_kNN = k;
/*   91: 262 */     this.m_kNNUpper = k;
/*   92: 263 */     this.m_kNNValid = false;
/*   93:     */   }
/*   94:     */   
/*   95:     */   public int getKNN()
/*   96:     */   {
/*   97: 273 */     return this.m_kNN;
/*   98:     */   }
/*   99:     */   
/*  100:     */   public String windowSizeTipText()
/*  101:     */   {
/*  102: 282 */     return "Gets the maximum number of instances allowed in the training pool. The addition of new instances above this value will result in old instances being removed. A value of 0 signifies no limit to the number of training instances.";
/*  103:     */   }
/*  104:     */   
/*  105:     */   public int getWindowSize()
/*  106:     */   {
/*  107: 298 */     return this.m_WindowSize;
/*  108:     */   }
/*  109:     */   
/*  110:     */   public void setWindowSize(int newWindowSize)
/*  111:     */   {
/*  112: 311 */     this.m_WindowSize = newWindowSize;
/*  113:     */   }
/*  114:     */   
/*  115:     */   public String distanceWeightingTipText()
/*  116:     */   {
/*  117: 321 */     return "Gets the distance weighting method used.";
/*  118:     */   }
/*  119:     */   
/*  120:     */   public SelectedTag getDistanceWeighting()
/*  121:     */   {
/*  122: 332 */     return new SelectedTag(this.m_DistanceWeighting, TAGS_WEIGHTING);
/*  123:     */   }
/*  124:     */   
/*  125:     */   public void setDistanceWeighting(SelectedTag newMethod)
/*  126:     */   {
/*  127: 343 */     if (newMethod.getTags() == TAGS_WEIGHTING) {
/*  128: 344 */       this.m_DistanceWeighting = newMethod.getSelectedTag().getID();
/*  129:     */     }
/*  130:     */   }
/*  131:     */   
/*  132:     */   public String meanSquaredTipText()
/*  133:     */   {
/*  134: 355 */     return "Whether the mean squared error is used rather than mean absolute error when doing cross-validation for regression problems.";
/*  135:     */   }
/*  136:     */   
/*  137:     */   public boolean getMeanSquared()
/*  138:     */   {
/*  139: 367 */     return this.m_MeanSquared;
/*  140:     */   }
/*  141:     */   
/*  142:     */   public void setMeanSquared(boolean newMeanSquared)
/*  143:     */   {
/*  144: 378 */     this.m_MeanSquared = newMeanSquared;
/*  145:     */   }
/*  146:     */   
/*  147:     */   public String crossValidateTipText()
/*  148:     */   {
/*  149: 388 */     return "Whether hold-one-out cross-validation will be used to select the best k value between 1 and the value specified as the KNN parameter.";
/*  150:     */   }
/*  151:     */   
/*  152:     */   public boolean getCrossValidate()
/*  153:     */   {
/*  154: 401 */     return this.m_CrossValidate;
/*  155:     */   }
/*  156:     */   
/*  157:     */   public void setCrossValidate(boolean newCrossValidate)
/*  158:     */   {
/*  159: 412 */     this.m_CrossValidate = newCrossValidate;
/*  160:     */   }
/*  161:     */   
/*  162:     */   public String nearestNeighbourSearchAlgorithmTipText()
/*  163:     */   {
/*  164: 421 */     return "The nearest neighbour search algorithm to use (Default: weka.core.neighboursearch.LinearNNSearch).";
/*  165:     */   }
/*  166:     */   
/*  167:     */   public NearestNeighbourSearch getNearestNeighbourSearchAlgorithm()
/*  168:     */   {
/*  169: 430 */     return this.m_NNSearch;
/*  170:     */   }
/*  171:     */   
/*  172:     */   public void setNearestNeighbourSearchAlgorithm(NearestNeighbourSearch nearestNeighbourSearchAlgorithm)
/*  173:     */   {
/*  174: 439 */     this.m_NNSearch = nearestNeighbourSearchAlgorithm;
/*  175:     */   }
/*  176:     */   
/*  177:     */   public int getNumTraining()
/*  178:     */   {
/*  179: 449 */     return this.m_Train.numInstances();
/*  180:     */   }
/*  181:     */   
/*  182:     */   public Capabilities getCapabilities()
/*  183:     */   {
/*  184: 458 */     Capabilities result = super.getCapabilities();
/*  185: 459 */     result.disableAll();
/*  186:     */     
/*  187:     */ 
/*  188: 462 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  189: 463 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  190: 464 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  191: 465 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  192:     */     
/*  193:     */ 
/*  194: 468 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  195: 469 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*  196: 470 */     result.enable(Capabilities.Capability.DATE_CLASS);
/*  197: 471 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  198:     */     
/*  199:     */ 
/*  200: 474 */     result.setMinimumNumberInstances(0);
/*  201:     */     
/*  202: 476 */     return result;
/*  203:     */   }
/*  204:     */   
/*  205:     */   public void buildClassifier(Instances instances)
/*  206:     */     throws Exception
/*  207:     */   {
/*  208: 488 */     getCapabilities().testWithFail(instances);
/*  209:     */     
/*  210:     */ 
/*  211: 491 */     instances = new Instances(instances);
/*  212: 492 */     instances.deleteWithMissingClass();
/*  213:     */     
/*  214: 494 */     this.m_NumClasses = instances.numClasses();
/*  215: 495 */     this.m_ClassType = instances.classAttribute().type();
/*  216: 496 */     this.m_Train = new Instances(instances, 0, instances.numInstances());
/*  217: 499 */     if ((this.m_WindowSize > 0) && (instances.numInstances() > this.m_WindowSize)) {
/*  218: 500 */       this.m_Train = new Instances(this.m_Train, this.m_Train.numInstances() - this.m_WindowSize, this.m_WindowSize);
/*  219:     */     }
/*  220: 505 */     this.m_NumAttributesUsed = 0.0D;
/*  221: 506 */     for (int i = 0; i < this.m_Train.numAttributes(); i++) {
/*  222: 507 */       if ((i != this.m_Train.classIndex()) && ((this.m_Train.attribute(i).isNominal()) || (this.m_Train.attribute(i).isNumeric()))) {
/*  223: 510 */         this.m_NumAttributesUsed += 1.0D;
/*  224:     */       }
/*  225:     */     }
/*  226: 514 */     this.m_NNSearch.setInstances(this.m_Train);
/*  227:     */     
/*  228:     */ 
/*  229: 517 */     this.m_kNNValid = false;
/*  230:     */     
/*  231: 519 */     this.m_defaultModel = new ZeroR();
/*  232: 520 */     this.m_defaultModel.buildClassifier(instances);
/*  233:     */   }
/*  234:     */   
/*  235:     */   public void updateClassifier(Instance instance)
/*  236:     */     throws Exception
/*  237:     */   {
/*  238: 532 */     if (!this.m_Train.equalHeaders(instance.dataset())) {
/*  239: 533 */       throw new Exception("Incompatible instance types\n" + this.m_Train.equalHeadersMsg(instance.dataset()));
/*  240:     */     }
/*  241: 535 */     if (instance.classIsMissing()) {
/*  242: 536 */       return;
/*  243:     */     }
/*  244: 539 */     this.m_Train.add(instance);
/*  245: 540 */     this.m_NNSearch.update(instance);
/*  246: 541 */     this.m_kNNValid = false;
/*  247: 542 */     if ((this.m_WindowSize > 0) && (this.m_Train.numInstances() > this.m_WindowSize))
/*  248:     */     {
/*  249: 543 */       boolean deletedInstance = false;
/*  250: 544 */       while (this.m_Train.numInstances() > this.m_WindowSize)
/*  251:     */       {
/*  252: 545 */         this.m_Train.delete(0);
/*  253: 546 */         deletedInstance = true;
/*  254:     */       }
/*  255: 549 */       if (deletedInstance == true) {
/*  256: 550 */         this.m_NNSearch.setInstances(this.m_Train);
/*  257:     */       }
/*  258:     */     }
/*  259:     */   }
/*  260:     */   
/*  261:     */   public double[] distributionForInstance(Instance instance)
/*  262:     */     throws Exception
/*  263:     */   {
/*  264: 563 */     if (this.m_Train.numInstances() == 0) {
/*  265: 565 */       return this.m_defaultModel.distributionForInstance(instance);
/*  266:     */     }
/*  267: 567 */     if ((this.m_WindowSize > 0) && (this.m_Train.numInstances() > this.m_WindowSize))
/*  268:     */     {
/*  269: 568 */       this.m_kNNValid = false;
/*  270: 569 */       boolean deletedInstance = false;
/*  271: 570 */       while (this.m_Train.numInstances() > this.m_WindowSize) {
/*  272: 571 */         this.m_Train.delete(0);
/*  273:     */       }
/*  274: 574 */       if (deletedInstance == true) {
/*  275: 575 */         this.m_NNSearch.setInstances(this.m_Train);
/*  276:     */       }
/*  277:     */     }
/*  278: 579 */     if ((!this.m_kNNValid) && (this.m_CrossValidate) && (this.m_kNNUpper >= 1)) {
/*  279: 580 */       crossValidate();
/*  280:     */     }
/*  281: 583 */     this.m_NNSearch.addInstanceInfo(instance);
/*  282:     */     
/*  283: 585 */     Instances neighbours = this.m_NNSearch.kNearestNeighbours(instance, this.m_kNN);
/*  284: 586 */     double[] distances = this.m_NNSearch.getDistances();
/*  285: 587 */     double[] distribution = makeDistribution(neighbours, distances);
/*  286:     */     
/*  287: 589 */     return distribution;
/*  288:     */   }
/*  289:     */   
/*  290:     */   public Enumeration<Option> listOptions()
/*  291:     */   {
/*  292: 599 */     Vector<Option> newVector = new Vector(7);
/*  293:     */     
/*  294: 601 */     newVector.addElement(new Option("\tWeight neighbours by the inverse of their distance\n\t(use when k > 1)", "I", 0, "-I"));
/*  295:     */     
/*  296:     */ 
/*  297:     */ 
/*  298: 605 */     newVector.addElement(new Option("\tWeight neighbours by 1 - their distance\n\t(use when k > 1)", "F", 0, "-F"));
/*  299:     */     
/*  300:     */ 
/*  301:     */ 
/*  302: 609 */     newVector.addElement(new Option("\tNumber of nearest neighbours (k) used in classification.\n\t(Default = 1)", "K", 1, "-K <number of neighbors>"));
/*  303:     */     
/*  304:     */ 
/*  305:     */ 
/*  306: 613 */     newVector.addElement(new Option("\tMinimise mean squared error rather than mean absolute\n\terror when using -X option with numeric prediction.", "E", 0, "-E"));
/*  307:     */     
/*  308:     */ 
/*  309:     */ 
/*  310: 617 */     newVector.addElement(new Option("\tMaximum number of training instances maintained.\n\tTraining instances are dropped FIFO. (Default = no window)", "W", 1, "-W <window size>"));
/*  311:     */     
/*  312:     */ 
/*  313:     */ 
/*  314: 621 */     newVector.addElement(new Option("\tSelect the number of nearest neighbours between 1\n\tand the k value specified using hold-one-out evaluation\n\ton the training data (use when k > 1)", "X", 0, "-X"));
/*  315:     */     
/*  316:     */ 
/*  317:     */ 
/*  318:     */ 
/*  319: 626 */     newVector.addElement(new Option("\tThe nearest neighbour search algorithm to use (default: weka.core.neighboursearch.LinearNNSearch).\n", "A", 0, "-A"));
/*  320:     */     
/*  321:     */ 
/*  322:     */ 
/*  323:     */ 
/*  324: 631 */     newVector.addAll(Collections.list(super.listOptions()));
/*  325:     */     
/*  326: 633 */     return newVector.elements();
/*  327:     */   }
/*  328:     */   
/*  329:     */   public void setOptions(String[] options)
/*  330:     */     throws Exception
/*  331:     */   {
/*  332: 678 */     String knnString = Utils.getOption('K', options);
/*  333: 679 */     if (knnString.length() != 0) {
/*  334: 680 */       setKNN(Integer.parseInt(knnString));
/*  335:     */     } else {
/*  336: 682 */       setKNN(1);
/*  337:     */     }
/*  338: 684 */     String windowString = Utils.getOption('W', options);
/*  339: 685 */     if (windowString.length() != 0) {
/*  340: 686 */       setWindowSize(Integer.parseInt(windowString));
/*  341:     */     } else {
/*  342: 688 */       setWindowSize(0);
/*  343:     */     }
/*  344: 690 */     if (Utils.getFlag('I', options)) {
/*  345: 691 */       setDistanceWeighting(new SelectedTag(2, TAGS_WEIGHTING));
/*  346: 692 */     } else if (Utils.getFlag('F', options)) {
/*  347: 693 */       setDistanceWeighting(new SelectedTag(4, TAGS_WEIGHTING));
/*  348:     */     } else {
/*  349: 695 */       setDistanceWeighting(new SelectedTag(1, TAGS_WEIGHTING));
/*  350:     */     }
/*  351: 697 */     setCrossValidate(Utils.getFlag('X', options));
/*  352: 698 */     setMeanSquared(Utils.getFlag('E', options));
/*  353:     */     
/*  354: 700 */     String nnSearchClass = Utils.getOption('A', options);
/*  355: 701 */     if (nnSearchClass.length() != 0)
/*  356:     */     {
/*  357: 702 */       String[] nnSearchClassSpec = Utils.splitOptions(nnSearchClass);
/*  358: 703 */       if (nnSearchClassSpec.length == 0) {
/*  359: 704 */         throw new Exception("Invalid NearestNeighbourSearch algorithm specification string.");
/*  360:     */       }
/*  361: 707 */       String className = nnSearchClassSpec[0];
/*  362: 708 */       nnSearchClassSpec[0] = "";
/*  363:     */       
/*  364: 710 */       setNearestNeighbourSearchAlgorithm((NearestNeighbourSearch)Utils.forName(NearestNeighbourSearch.class, className, nnSearchClassSpec));
/*  365:     */     }
/*  366:     */     else
/*  367:     */     {
/*  368: 717 */       setNearestNeighbourSearchAlgorithm(new LinearNNSearch());
/*  369:     */     }
/*  370: 719 */     super.setOptions(options);
/*  371:     */     
/*  372: 721 */     Utils.checkForRemainingOptions(options);
/*  373:     */   }
/*  374:     */   
/*  375:     */   public String[] getOptions()
/*  376:     */   {
/*  377: 731 */     Vector<String> options = new Vector();
/*  378: 732 */     options.add("-K");options.add("" + getKNN());
/*  379: 733 */     options.add("-W");options.add("" + this.m_WindowSize);
/*  380: 734 */     if (getCrossValidate()) {
/*  381: 735 */       options.add("-X");
/*  382:     */     }
/*  383: 737 */     if (getMeanSquared()) {
/*  384: 738 */       options.add("-E");
/*  385:     */     }
/*  386: 740 */     if (this.m_DistanceWeighting == 2) {
/*  387: 741 */       options.add("-I");
/*  388: 742 */     } else if (this.m_DistanceWeighting == 4) {
/*  389: 743 */       options.add("-F");
/*  390:     */     }
/*  391: 746 */     options.add("-A");
/*  392: 747 */     options.add(this.m_NNSearch.getClass().getName() + " " + Utils.joinOptions(this.m_NNSearch.getOptions()));
/*  393:     */     
/*  394: 749 */     Collections.addAll(options, super.getOptions());
/*  395:     */     
/*  396: 751 */     return (String[])options.toArray(new String[0]);
/*  397:     */   }
/*  398:     */   
/*  399:     */   public Enumeration<String> enumerateMeasures()
/*  400:     */   {
/*  401: 762 */     if (this.m_CrossValidate)
/*  402:     */     {
/*  403: 763 */       Enumeration<String> enm = this.m_NNSearch.enumerateMeasures();
/*  404: 764 */       Vector<String> measures = new Vector();
/*  405: 765 */       while (enm.hasMoreElements()) {
/*  406: 766 */         measures.add(enm.nextElement());
/*  407:     */       }
/*  408: 767 */       measures.add("measureKNN");
/*  409: 768 */       return measures.elements();
/*  410:     */     }
/*  411: 771 */     return this.m_NNSearch.enumerateMeasures();
/*  412:     */   }
/*  413:     */   
/*  414:     */   public double getMeasure(String additionalMeasureName)
/*  415:     */   {
/*  416: 785 */     if (additionalMeasureName.equals("measureKNN")) {
/*  417: 786 */       return this.m_kNN;
/*  418:     */     }
/*  419: 788 */     return this.m_NNSearch.getMeasure(additionalMeasureName);
/*  420:     */   }
/*  421:     */   
/*  422:     */   public String toString()
/*  423:     */   {
/*  424: 799 */     if (this.m_Train == null) {
/*  425: 800 */       return "IBk: No model built yet.";
/*  426:     */     }
/*  427: 803 */     if (this.m_Train.numInstances() == 0) {
/*  428: 804 */       return "Warning: no training instances - ZeroR model used.";
/*  429:     */     }
/*  430: 807 */     if ((!this.m_kNNValid) && (this.m_CrossValidate)) {
/*  431: 808 */       crossValidate();
/*  432:     */     }
/*  433: 811 */     String result = "IB1 instance-based classifier\nusing " + this.m_kNN;
/*  434: 814 */     switch (this.m_DistanceWeighting)
/*  435:     */     {
/*  436:     */     case 2: 
/*  437: 816 */       result = result + " inverse-distance-weighted";
/*  438: 817 */       break;
/*  439:     */     case 4: 
/*  440: 819 */       result = result + " similarity-weighted";
/*  441:     */     }
/*  442: 822 */     result = result + " nearest neighbour(s) for classification\n";
/*  443: 824 */     if (this.m_WindowSize != 0) {
/*  444: 825 */       result = result + "using a maximum of " + this.m_WindowSize + " (windowed) training instances\n";
/*  445:     */     }
/*  446: 828 */     return result;
/*  447:     */   }
/*  448:     */   
/*  449:     */   protected void init()
/*  450:     */   {
/*  451: 836 */     setKNN(1);
/*  452: 837 */     this.m_WindowSize = 0;
/*  453: 838 */     this.m_DistanceWeighting = 1;
/*  454: 839 */     this.m_CrossValidate = false;
/*  455: 840 */     this.m_MeanSquared = false;
/*  456:     */   }
/*  457:     */   
/*  458:     */   protected double[] makeDistribution(Instances neighbours, double[] distances)
/*  459:     */     throws Exception
/*  460:     */   {
/*  461: 854 */     double total = 0.0D;
/*  462: 855 */     double[] distribution = new double[this.m_NumClasses];
/*  463: 858 */     if (this.m_ClassType == 1)
/*  464:     */     {
/*  465: 859 */       for (int i = 0; i < this.m_NumClasses; i++) {
/*  466: 860 */         distribution[i] = (1.0D / Math.max(1, this.m_Train.numInstances()));
/*  467:     */       }
/*  468: 862 */       total = this.m_NumClasses / Math.max(1, this.m_Train.numInstances());
/*  469:     */     }
/*  470: 865 */     for (int i = 0; i < neighbours.numInstances(); i++)
/*  471:     */     {
/*  472: 867 */       Instance current = neighbours.instance(i);
/*  473: 868 */       distances[i] *= distances[i];
/*  474: 869 */       distances[i] = Math.sqrt(distances[i] / this.m_NumAttributesUsed);
/*  475:     */       double weight;
/*  476: 870 */       switch (this.m_DistanceWeighting)
/*  477:     */       {
/*  478:     */       case 2: 
/*  479: 872 */         weight = 1.0D / (distances[i] + 0.001D);
/*  480: 873 */         break;
/*  481:     */       case 4: 
/*  482: 875 */         weight = 1.0D - distances[i];
/*  483: 876 */         break;
/*  484:     */       default: 
/*  485: 878 */         weight = 1.0D;
/*  486:     */       }
/*  487: 881 */       weight *= current.weight();
/*  488:     */       try
/*  489:     */       {
/*  490: 883 */         switch (this.m_ClassType)
/*  491:     */         {
/*  492:     */         case 1: 
/*  493: 885 */           distribution[((int)current.classValue())] += weight;
/*  494: 886 */           break;
/*  495:     */         case 0: 
/*  496: 888 */           distribution[0] += current.classValue() * weight;
/*  497:     */         }
/*  498:     */       }
/*  499:     */       catch (Exception ex)
/*  500:     */       {
/*  501: 892 */         throw new Error("Data has no class attribute!");
/*  502:     */       }
/*  503: 894 */       total += weight;
/*  504:     */     }
/*  505: 898 */     if (total > 0.0D) {
/*  506: 899 */       Utils.normalize(distribution, total);
/*  507:     */     }
/*  508: 901 */     return distribution;
/*  509:     */   }
/*  510:     */   
/*  511:     */   protected void crossValidate()
/*  512:     */   {
/*  513:     */     try
/*  514:     */     {
/*  515: 913 */       if ((this.m_NNSearch instanceof CoverTree)) {
/*  516: 914 */         throw new Exception("CoverTree doesn't support hold-one-out cross-validation. Use some other NN method.");
/*  517:     */       }
/*  518: 918 */       double[] performanceStats = new double[this.m_kNNUpper];
/*  519: 919 */       double[] performanceStatsSq = new double[this.m_kNNUpper];
/*  520: 921 */       for (int i = 0; i < this.m_kNNUpper; i++)
/*  521:     */       {
/*  522: 922 */         performanceStats[i] = 0.0D;
/*  523: 923 */         performanceStatsSq[i] = 0.0D;
/*  524:     */       }
/*  525: 927 */       this.m_kNN = this.m_kNNUpper;
/*  526: 931 */       for (int i = 0; i < this.m_Train.numInstances(); i++)
/*  527:     */       {
/*  528: 932 */         if ((this.m_Debug) && (i % 50 == 0)) {
/*  529: 933 */           System.err.print("Cross validating " + i + "/" + this.m_Train.numInstances() + "\r");
/*  530:     */         }
/*  531: 936 */         Instance instance = this.m_Train.instance(i);
/*  532: 937 */         Instances neighbours = this.m_NNSearch.kNearestNeighbours(instance, this.m_kNN);
/*  533: 938 */         double[] origDistances = this.m_NNSearch.getDistances();
/*  534: 940 */         for (int j = this.m_kNNUpper - 1; j >= 0; j--)
/*  535:     */         {
/*  536: 942 */           double[] convertedDistances = new double[origDistances.length];
/*  537: 943 */           System.arraycopy(origDistances, 0, convertedDistances, 0, origDistances.length);
/*  538:     */           
/*  539: 945 */           double[] distribution = makeDistribution(neighbours, convertedDistances);
/*  540:     */           
/*  541: 947 */           double thisPrediction = Utils.maxIndex(distribution);
/*  542: 948 */           if (this.m_Train.classAttribute().isNumeric())
/*  543:     */           {
/*  544: 949 */             thisPrediction = distribution[0];
/*  545: 950 */             double err = thisPrediction - instance.classValue();
/*  546: 951 */             performanceStatsSq[j] += err * err;
/*  547: 952 */             performanceStats[j] += Math.abs(err);
/*  548:     */           }
/*  549: 954 */           else if (thisPrediction != instance.classValue())
/*  550:     */           {
/*  551: 955 */             performanceStats[j] += 1.0D;
/*  552:     */           }
/*  553: 958 */           if (j >= 1) {
/*  554: 959 */             neighbours = pruneToK(neighbours, convertedDistances, j);
/*  555:     */           }
/*  556:     */         }
/*  557:     */       }
/*  558: 965 */       for (int i = 0; i < this.m_kNNUpper; i++)
/*  559:     */       {
/*  560: 966 */         if (this.m_Debug) {
/*  561: 967 */           System.err.print("Hold-one-out performance of " + (i + 1) + " neighbors ");
/*  562:     */         }
/*  563: 970 */         if (this.m_Train.classAttribute().isNumeric())
/*  564:     */         {
/*  565: 971 */           if (this.m_Debug) {
/*  566: 972 */             if (this.m_MeanSquared) {
/*  567: 973 */               System.err.println("(RMSE) = " + Math.sqrt(performanceStatsSq[i] / this.m_Train.numInstances()));
/*  568:     */             } else {
/*  569: 977 */               System.err.println("(MAE) = " + performanceStats[i] / this.m_Train.numInstances());
/*  570:     */             }
/*  571:     */           }
/*  572:     */         }
/*  573: 983 */         else if (this.m_Debug) {
/*  574: 984 */           System.err.println("(%ERR) = " + 100.0D * performanceStats[i] / this.m_Train.numInstances());
/*  575:     */         }
/*  576:     */       }
/*  577: 994 */       double[] searchStats = performanceStats;
/*  578: 995 */       if ((this.m_Train.classAttribute().isNumeric()) && (this.m_MeanSquared)) {
/*  579: 996 */         searchStats = performanceStatsSq;
/*  580:     */       }
/*  581: 998 */       double bestPerformance = (0.0D / 0.0D);
/*  582: 999 */       int bestK = 1;
/*  583:1000 */       for (int i = 0; i < this.m_kNNUpper; i++) {
/*  584:1001 */         if ((Double.isNaN(bestPerformance)) || (bestPerformance > searchStats[i]))
/*  585:     */         {
/*  586:1003 */           bestPerformance = searchStats[i];
/*  587:1004 */           bestK = i + 1;
/*  588:     */         }
/*  589:     */       }
/*  590:1007 */       this.m_kNN = bestK;
/*  591:1008 */       if (this.m_Debug) {
/*  592:1009 */         System.err.println("Selected k = " + bestK);
/*  593:     */       }
/*  594:1012 */       this.m_kNNValid = true;
/*  595:     */     }
/*  596:     */     catch (Exception ex)
/*  597:     */     {
/*  598:1014 */       throw new Error("Couldn't optimize by cross-validation: " + ex.getMessage());
/*  599:     */     }
/*  600:     */   }
/*  601:     */   
/*  602:     */   public Instances pruneToK(Instances neighbours, double[] distances, int k)
/*  603:     */   {
/*  604:1030 */     if ((neighbours == null) || (distances == null) || (neighbours.numInstances() == 0)) {
/*  605:1031 */       return null;
/*  606:     */     }
/*  607:1033 */     if (k < 1) {
/*  608:1034 */       k = 1;
/*  609:     */     }
/*  610:1037 */     int currentK = 0;
/*  611:1039 */     for (int i = 0; i < neighbours.numInstances(); i++)
/*  612:     */     {
/*  613:1040 */       currentK++;
/*  614:1041 */       double currentDist = distances[i];
/*  615:1042 */       if ((currentK > k) && (currentDist != distances[(i - 1)]))
/*  616:     */       {
/*  617:1043 */         currentK--;
/*  618:1044 */         neighbours = new Instances(neighbours, 0, currentK);
/*  619:1045 */         break;
/*  620:     */       }
/*  621:     */     }
/*  622:1049 */     return neighbours;
/*  623:     */   }
/*  624:     */   
/*  625:     */   public String getRevision()
/*  626:     */   {
/*  627:1058 */     return RevisionUtils.extract("$Revision: 10141 $");
/*  628:     */   }
/*  629:     */   
/*  630:     */   public static void main(String[] argv)
/*  631:     */   {
/*  632:1067 */     runClassifier(new IBk(), argv);
/*  633:     */   }
/*  634:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.lazy.IBk
 * JD-Core Version:    0.7.0.1
 */