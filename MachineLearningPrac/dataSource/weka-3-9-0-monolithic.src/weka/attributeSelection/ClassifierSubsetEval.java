/*    1:     */ package weka.attributeSelection;
/*    2:     */ 
/*    3:     */ import java.io.BufferedReader;
/*    4:     */ import java.io.File;
/*    5:     */ import java.io.FileReader;
/*    6:     */ import java.io.Reader;
/*    7:     */ import java.util.BitSet;
/*    8:     */ import java.util.Collections;
/*    9:     */ import java.util.Enumeration;
/*   10:     */ import java.util.Random;
/*   11:     */ import java.util.Vector;
/*   12:     */ import weka.classifiers.AbstractClassifier;
/*   13:     */ import weka.classifiers.Classifier;
/*   14:     */ import weka.classifiers.Evaluation;
/*   15:     */ import weka.classifiers.rules.ZeroR;
/*   16:     */ import weka.core.Attribute;
/*   17:     */ import weka.core.Capabilities;
/*   18:     */ import weka.core.Capabilities.Capability;
/*   19:     */ import weka.core.Instance;
/*   20:     */ import weka.core.Instances;
/*   21:     */ import weka.core.Option;
/*   22:     */ import weka.core.OptionHandler;
/*   23:     */ import weka.core.RevisionUtils;
/*   24:     */ import weka.core.SelectedTag;
/*   25:     */ import weka.core.Tag;
/*   26:     */ import weka.core.Utils;
/*   27:     */ import weka.filters.Filter;
/*   28:     */ import weka.filters.unsupervised.attribute.Remove;
/*   29:     */ 
/*   30:     */ public class ClassifierSubsetEval
/*   31:     */   extends HoldOutSubsetEvaluator
/*   32:     */   implements OptionHandler, ErrorBasedMeritEvaluator
/*   33:     */ {
/*   34:     */   static final long serialVersionUID = 7532217899385278710L;
/*   35:     */   private Instances m_trainingInstances;
/*   36:     */   private int m_classIndex;
/*   37:     */   private int m_numAttribs;
/*   38: 142 */   private Classifier m_ClassifierTemplate = new ZeroR();
/*   39: 149 */   private Classifier m_Classifier = new ZeroR();
/*   40: 152 */   private File m_holdOutFile = new File("Click to set hold out or test instances");
/*   41: 156 */   private Instances m_holdOutInstances = null;
/*   42: 159 */   private boolean m_useTraining = true;
/*   43:     */   protected boolean m_usePercentageSplit;
/*   44: 165 */   protected int m_seed = 1;
/*   45: 168 */   protected String m_splitPercent = "90";
/*   46:     */   public static final int EVAL_DEFAULT = 1;
/*   47:     */   public static final int EVAL_ACCURACY = 2;
/*   48:     */   public static final int EVAL_RMSE = 3;
/*   49:     */   public static final int EVAL_MAE = 4;
/*   50:     */   public static final int EVAL_FMEASURE = 5;
/*   51:     */   public static final int EVAL_AUC = 6;
/*   52:     */   public static final int EVAL_AUPRC = 7;
/*   53: 178 */   public static final Tag[] TAGS_EVALUATION = { new Tag(1, "Default: accuracy (discrete class); RMSE (numeric class)"), new Tag(2, "Accuracy (discrete class only)"), new Tag(3, "RMSE (of the class probabilities for discrete class)"), new Tag(4, "MAE (of the class probabilities for discrete class)"), new Tag(5, "F-measure (discrete class only)"), new Tag(6, "AUC (area under the ROC curve - discrete class only)"), new Tag(7, "AUPRC (area under the precision-recall curve - discrete class only)") };
/*   54: 190 */   protected int m_evaluationMeasure = 1;
/*   55: 196 */   protected int m_IRClassVal = -1;
/*   56: 199 */   protected String m_IRClassValS = "";
/*   57:     */   
/*   58:     */   public String globalInfo()
/*   59:     */   {
/*   60: 208 */     return "Classifier subset evaluator:\n\nEvaluates attribute subsets on training data or a seperate hold out testing set. Uses a classifier to estimate the 'merit' of a set of attributes.";
/*   61:     */   }
/*   62:     */   
/*   63:     */   public Enumeration<Option> listOptions()
/*   64:     */   {
/*   65: 219 */     Vector<Option> newVector = new Vector(8);
/*   66:     */     
/*   67: 221 */     newVector.addElement(new Option("\tclass name of the classifier to use for accuracy estimation.\n\tPlace any classifier options LAST on the command line\n\tfollowing a \"--\". eg.:\n\t\t-B weka.classifiers.bayes.NaiveBayes ... -- -K\n\t(default: weka.classifiers.rules.ZeroR)", "B", 1, "-B <classifier>"));
/*   68:     */     
/*   69:     */ 
/*   70:     */ 
/*   71:     */ 
/*   72:     */ 
/*   73:     */ 
/*   74:     */ 
/*   75: 229 */     newVector.addElement(new Option("\tUse the training data to estimate accuracy.", "T", 0, "-T"));
/*   76:     */     
/*   77:     */ 
/*   78: 232 */     newVector.addElement(new Option("\tName of the hold out/test set to \n\testimate accuracy on.", "H", 1, "-H <filename>"));
/*   79:     */     
/*   80:     */ 
/*   81: 235 */     newVector.addElement(new Option("\tPerform a percentage split on the training data.\n\tUse in conjunction with -T.", "percentage-split", 0, "-percentage-split"));
/*   82:     */     
/*   83:     */ 
/*   84:     */ 
/*   85: 239 */     newVector.addElement(new Option("\tSplit percentage to use (default = 90).", "P", 1, "-P"));
/*   86:     */     
/*   87: 241 */     newVector.addElement(new Option("\tRandom seed for percentage split (default = 1).", "S", 1, "-S"));
/*   88:     */     
/*   89:     */ 
/*   90: 244 */     newVector.addElement(new Option("\tPerformance evaluation measure to use for selecting attributes.\n\t(Default = accuracy for discrete class and rmse for numeric class)", "E", 1, "-E <acc | rmse | mae | f-meas | auc | auprc>"));
/*   91:     */     
/*   92:     */ 
/*   93:     */ 
/*   94:     */ 
/*   95:     */ 
/*   96: 250 */     newVector.addElement(new Option("\tOptional class value (label or 1-based index) to use in conjunction with\n\tIR statistics (f-meas, auc or auprc). Omitting this option will use\n\tthe class-weighted average.", "IRclass", 1, "-IRclass <label | index>"));
/*   97: 257 */     if ((this.m_ClassifierTemplate != null) && ((this.m_ClassifierTemplate instanceof OptionHandler)))
/*   98:     */     {
/*   99: 260 */       newVector.addElement(new Option("", "", 0, "\nOptions specific to scheme " + this.m_ClassifierTemplate.getClass().getName() + ":"));
/*  100:     */       
/*  101:     */ 
/*  102: 263 */       newVector.addAll(Collections.list(((OptionHandler)this.m_ClassifierTemplate).listOptions()));
/*  103:     */     }
/*  104: 267 */     return newVector.elements();
/*  105:     */   }
/*  106:     */   
/*  107:     */   public void setOptions(String[] options)
/*  108:     */     throws Exception
/*  109:     */   {
/*  110: 344 */     resetOptions();
/*  111:     */     
/*  112: 346 */     String optionString = Utils.getOption('B', options);
/*  113: 347 */     if (optionString.length() == 0) {
/*  114: 348 */       optionString = ZeroR.class.getName();
/*  115:     */     }
/*  116: 350 */     setClassifier(AbstractClassifier.forName(optionString, Utils.partitionOptions(options)));
/*  117:     */     
/*  118:     */ 
/*  119: 353 */     optionString = Utils.getOption('H', options);
/*  120: 354 */     if (optionString.length() != 0) {
/*  121: 355 */       setHoldOutFile(new File(optionString));
/*  122:     */     }
/*  123: 358 */     setUsePercentageSplit(Utils.getFlag("percentage-split", options));
/*  124:     */     
/*  125: 360 */     optionString = Utils.getOption('P', options);
/*  126: 361 */     if (optionString.length() > 0) {
/*  127: 362 */       setSplitPercent(optionString);
/*  128:     */     }
/*  129: 365 */     setUseTraining(Utils.getFlag('T', options));
/*  130:     */     
/*  131: 367 */     optionString = Utils.getOption('E', options);
/*  132: 368 */     if (optionString.length() != 0) {
/*  133: 369 */       if (optionString.equals("acc")) {
/*  134: 370 */         setEvaluationMeasure(new SelectedTag(2, TAGS_EVALUATION));
/*  135: 371 */       } else if (optionString.equals("rmse")) {
/*  136: 372 */         setEvaluationMeasure(new SelectedTag(3, TAGS_EVALUATION));
/*  137: 373 */       } else if (optionString.equals("mae")) {
/*  138: 374 */         setEvaluationMeasure(new SelectedTag(4, TAGS_EVALUATION));
/*  139: 375 */       } else if (optionString.equals("f-meas")) {
/*  140: 376 */         setEvaluationMeasure(new SelectedTag(5, TAGS_EVALUATION));
/*  141: 377 */       } else if (optionString.equals("auc")) {
/*  142: 378 */         setEvaluationMeasure(new SelectedTag(6, TAGS_EVALUATION));
/*  143: 379 */       } else if (optionString.equals("auprc")) {
/*  144: 380 */         setEvaluationMeasure(new SelectedTag(7, TAGS_EVALUATION));
/*  145:     */       } else {
/*  146: 382 */         throw new IllegalArgumentException("Invalid evaluation measure");
/*  147:     */       }
/*  148:     */     }
/*  149: 386 */     optionString = Utils.getOption("IRClass", options);
/*  150: 387 */     if (optionString.length() > 0) {
/*  151: 388 */       setIRClassValue(optionString);
/*  152:     */     }
/*  153: 391 */     optionString = Utils.getOption("S", options);
/*  154: 392 */     if (optionString.length() > 0) {
/*  155: 393 */       setSeed(Integer.parseInt(optionString));
/*  156:     */     }
/*  157:     */   }
/*  158:     */   
/*  159:     */   public String seedTipText()
/*  160:     */   {
/*  161: 404 */     return "The random seed to use for randomizing the training data prior to performing a percentage split";
/*  162:     */   }
/*  163:     */   
/*  164:     */   public void setSeed(int s)
/*  165:     */   {
/*  166: 415 */     this.m_seed = s;
/*  167:     */   }
/*  168:     */   
/*  169:     */   public int getSeed()
/*  170:     */   {
/*  171: 425 */     return this.m_seed;
/*  172:     */   }
/*  173:     */   
/*  174:     */   public String usePercentageSplitTipText()
/*  175:     */   {
/*  176: 435 */     return "Evaluate using a percentage split on the training data";
/*  177:     */   }
/*  178:     */   
/*  179:     */   public void setUsePercentageSplit(boolean p)
/*  180:     */   {
/*  181: 445 */     this.m_usePercentageSplit = p;
/*  182:     */   }
/*  183:     */   
/*  184:     */   public boolean getUsePercentageSplit()
/*  185:     */   {
/*  186: 455 */     return this.m_usePercentageSplit;
/*  187:     */   }
/*  188:     */   
/*  189:     */   public String splitPercentTipText()
/*  190:     */   {
/*  191: 465 */     return "The percentage split to use";
/*  192:     */   }
/*  193:     */   
/*  194:     */   public void setSplitPercent(String sp)
/*  195:     */   {
/*  196: 474 */     this.m_splitPercent = sp;
/*  197:     */   }
/*  198:     */   
/*  199:     */   public String getSplitPercent()
/*  200:     */   {
/*  201: 483 */     return this.m_splitPercent;
/*  202:     */   }
/*  203:     */   
/*  204:     */   public void setIRClassValue(String val)
/*  205:     */   {
/*  206: 495 */     this.m_IRClassValS = val;
/*  207:     */   }
/*  208:     */   
/*  209:     */   public String getIRClassValue()
/*  210:     */   {
/*  211: 507 */     return this.m_IRClassValS;
/*  212:     */   }
/*  213:     */   
/*  214:     */   public String IRClassValueTipText()
/*  215:     */   {
/*  216: 517 */     return "The class label, or 1-based index of the class label, to use when evaluating subsets with an IR metric (such as f-measure or AUC. Leaving this unset will result in the class frequency weighted average of the metric being used.";
/*  217:     */   }
/*  218:     */   
/*  219:     */   public String evaluationMeasureTipText()
/*  220:     */   {
/*  221: 530 */     return "The measure used to evaluate the performance of attribute combinations.";
/*  222:     */   }
/*  223:     */   
/*  224:     */   public SelectedTag getEvaluationMeasure()
/*  225:     */   {
/*  226: 540 */     return new SelectedTag(this.m_evaluationMeasure, TAGS_EVALUATION);
/*  227:     */   }
/*  228:     */   
/*  229:     */   public void setEvaluationMeasure(SelectedTag newMethod)
/*  230:     */   {
/*  231: 550 */     if (newMethod.getTags() == TAGS_EVALUATION) {
/*  232: 551 */       this.m_evaluationMeasure = newMethod.getSelectedTag().getID();
/*  233:     */     }
/*  234:     */   }
/*  235:     */   
/*  236:     */   public String classifierTipText()
/*  237:     */   {
/*  238: 562 */     return "Classifier to use for estimating the accuracy of subsets";
/*  239:     */   }
/*  240:     */   
/*  241:     */   public void setClassifier(Classifier newClassifier)
/*  242:     */   {
/*  243: 571 */     this.m_ClassifierTemplate = newClassifier;
/*  244: 572 */     this.m_Classifier = newClassifier;
/*  245:     */   }
/*  246:     */   
/*  247:     */   public Classifier getClassifier()
/*  248:     */   {
/*  249: 581 */     return this.m_ClassifierTemplate;
/*  250:     */   }
/*  251:     */   
/*  252:     */   public String holdOutFileTipText()
/*  253:     */   {
/*  254: 591 */     return "File containing hold out/test instances.";
/*  255:     */   }
/*  256:     */   
/*  257:     */   public File getHoldOutFile()
/*  258:     */   {
/*  259: 600 */     return this.m_holdOutFile;
/*  260:     */   }
/*  261:     */   
/*  262:     */   public void setHoldOutFile(File h)
/*  263:     */   {
/*  264: 609 */     this.m_holdOutFile = h;
/*  265:     */   }
/*  266:     */   
/*  267:     */   public String useTrainingTipText()
/*  268:     */   {
/*  269: 619 */     return "Use training data instead of hold out/test instances.";
/*  270:     */   }
/*  271:     */   
/*  272:     */   public boolean getUseTraining()
/*  273:     */   {
/*  274: 628 */     return this.m_useTraining;
/*  275:     */   }
/*  276:     */   
/*  277:     */   public void setUseTraining(boolean t)
/*  278:     */   {
/*  279: 637 */     this.m_useTraining = t;
/*  280:     */   }
/*  281:     */   
/*  282:     */   public String[] getOptions()
/*  283:     */   {
/*  284: 648 */     Vector<String> options = new Vector();
/*  285: 650 */     if (getClassifier() != null)
/*  286:     */     {
/*  287: 651 */       options.add("-B");
/*  288: 652 */       options.add(getClassifier().getClass().getName());
/*  289:     */     }
/*  290: 655 */     if (getUseTraining()) {
/*  291: 656 */       options.add("-T");
/*  292:     */     }
/*  293: 658 */     options.add("-H");
/*  294: 659 */     options.add(getHoldOutFile().getPath());
/*  295: 661 */     if (getUsePercentageSplit())
/*  296:     */     {
/*  297: 662 */       options.add("-percentage-split");
/*  298: 663 */       options.add("-P");
/*  299: 664 */       options.add(this.m_splitPercent);
/*  300: 665 */       options.add("-S");
/*  301: 666 */       options.add("" + getSeed());
/*  302:     */     }
/*  303: 669 */     options.add("-E");
/*  304: 670 */     switch (this.m_evaluationMeasure)
/*  305:     */     {
/*  306:     */     case 1: 
/*  307:     */     case 2: 
/*  308: 673 */       options.add("acc");
/*  309: 674 */       break;
/*  310:     */     case 3: 
/*  311: 676 */       options.add("rmse");
/*  312: 677 */       break;
/*  313:     */     case 4: 
/*  314: 679 */       options.add("mae");
/*  315: 680 */       break;
/*  316:     */     case 5: 
/*  317: 682 */       options.add("f-meas");
/*  318: 683 */       break;
/*  319:     */     case 6: 
/*  320: 685 */       options.add("auc");
/*  321: 686 */       break;
/*  322:     */     case 7: 
/*  323: 688 */       options.add("auprc");
/*  324:     */     }
/*  325: 692 */     if ((this.m_IRClassValS != null) && (this.m_IRClassValS.length() > 0))
/*  326:     */     {
/*  327: 693 */       options.add("-IRClass");
/*  328: 694 */       options.add(this.m_IRClassValS);
/*  329:     */     }
/*  330: 697 */     if ((this.m_ClassifierTemplate != null) && ((this.m_ClassifierTemplate instanceof OptionHandler)))
/*  331:     */     {
/*  332: 699 */       String[] classifierOptions = ((OptionHandler)this.m_ClassifierTemplate).getOptions();
/*  333: 701 */       if (classifierOptions.length > 0)
/*  334:     */       {
/*  335: 702 */         options.add("--");
/*  336: 703 */         Collections.addAll(options, classifierOptions);
/*  337:     */       }
/*  338:     */     }
/*  339: 707 */     return (String[])options.toArray(new String[0]);
/*  340:     */   }
/*  341:     */   
/*  342:     */   public Capabilities getCapabilities()
/*  343:     */   {
/*  344:     */     Capabilities result;
/*  345: 720 */     if (getClassifier() == null)
/*  346:     */     {
/*  347: 721 */       Capabilities result = super.getCapabilities();
/*  348: 722 */       result.disableAll();
/*  349:     */     }
/*  350:     */     else
/*  351:     */     {
/*  352: 724 */       result = getClassifier().getCapabilities();
/*  353:     */     }
/*  354: 728 */     for (Capabilities.Capability cap : Capabilities.Capability.values()) {
/*  355: 729 */       result.enableDependency(cap);
/*  356:     */     }
/*  357: 732 */     return result;
/*  358:     */   }
/*  359:     */   
/*  360:     */   public void buildEvaluator(Instances data)
/*  361:     */     throws Exception
/*  362:     */   {
/*  363: 746 */     getCapabilities().testWithFail(data);
/*  364:     */     
/*  365: 748 */     this.m_trainingInstances = new Instances(data);
/*  366: 749 */     this.m_classIndex = this.m_trainingInstances.classIndex();
/*  367: 750 */     this.m_numAttribs = this.m_trainingInstances.numAttributes();
/*  368: 754 */     if ((!this.m_useTraining) && (!getHoldOutFile().getPath().startsWith("Click to set")))
/*  369:     */     {
/*  370: 756 */       Reader r = new BufferedReader(new FileReader(getHoldOutFile().getPath()));
/*  371:     */       
/*  372: 758 */       this.m_holdOutInstances = new Instances(r);
/*  373: 759 */       this.m_holdOutInstances.setClassIndex(this.m_trainingInstances.classIndex());
/*  374: 760 */       if (!this.m_trainingInstances.equalHeaders(this.m_holdOutInstances)) {
/*  375: 761 */         throw new Exception("Hold out/test set is not compatable with training data.\n" + this.m_trainingInstances.equalHeadersMsg(this.m_holdOutInstances));
/*  376:     */       }
/*  377:     */     }
/*  378: 765 */     else if (this.m_usePercentageSplit)
/*  379:     */     {
/*  380: 766 */       int splitPercentage = 90;
/*  381:     */       try
/*  382:     */       {
/*  383: 768 */         splitPercentage = Integer.parseInt(this.m_splitPercent);
/*  384:     */       }
/*  385:     */       catch (NumberFormatException n) {}
/*  386: 772 */       this.m_trainingInstances.randomize(new Random(this.m_seed));
/*  387: 773 */       int trainSize = Math.round(this.m_trainingInstances.numInstances() * splitPercentage / 100);
/*  388:     */       
/*  389: 775 */       int testSize = this.m_trainingInstances.numInstances() - trainSize;
/*  390:     */       
/*  391: 777 */       this.m_holdOutInstances = new Instances(this.m_trainingInstances, trainSize, testSize);
/*  392:     */       
/*  393: 779 */       this.m_trainingInstances = new Instances(this.m_trainingInstances, 0, trainSize);
/*  394:     */     }
/*  395: 782 */     if ((this.m_IRClassValS != null) && (this.m_IRClassValS.length() > 0)) {
/*  396:     */       try
/*  397:     */       {
/*  398: 785 */         this.m_IRClassVal = Integer.parseInt(this.m_IRClassValS);
/*  399:     */         
/*  400: 787 */         this.m_IRClassVal -= 1;
/*  401:     */       }
/*  402:     */       catch (NumberFormatException e)
/*  403:     */       {
/*  404: 790 */         this.m_IRClassVal = this.m_trainingInstances.classAttribute().indexOfValue(this.m_IRClassValS);
/*  405:     */       }
/*  406:     */     }
/*  407:     */   }
/*  408:     */   
/*  409:     */   public double evaluateSubset(BitSet subset)
/*  410:     */     throws Exception
/*  411:     */   {
/*  412: 806 */     double evalMetric = 0.0D;
/*  413: 807 */     int numAttributes = 0;
/*  414: 808 */     Instances trainCopy = null;
/*  415: 809 */     Instances testCopy = null;
/*  416: 810 */     String[] cOpts = null;
/*  417: 811 */     Evaluation evaluation = null;
/*  418: 812 */     if ((this.m_ClassifierTemplate instanceof OptionHandler)) {
/*  419: 813 */       cOpts = ((OptionHandler)this.m_ClassifierTemplate).getOptions();
/*  420:     */     }
/*  421: 815 */     Classifier classifier = AbstractClassifier.forName(this.m_ClassifierTemplate.getClass().getName(), cOpts);
/*  422:     */     
/*  423:     */ 
/*  424: 818 */     Remove delTransform = new Remove();
/*  425: 819 */     delTransform.setInvertSelection(true);
/*  426:     */     
/*  427: 821 */     trainCopy = new Instances(this.m_trainingInstances);
/*  428: 823 */     if (!this.m_useTraining)
/*  429:     */     {
/*  430: 824 */       if (this.m_holdOutInstances == null) {
/*  431: 825 */         throw new Exception("Must specify a set of hold out/test instances with -H");
/*  432:     */       }
/*  433: 829 */       testCopy = new Instances(this.m_holdOutInstances);
/*  434:     */     }
/*  435: 830 */     else if (this.m_usePercentageSplit)
/*  436:     */     {
/*  437: 831 */       testCopy = new Instances(this.m_holdOutInstances);
/*  438:     */     }
/*  439: 835 */     for (int i = 0; i < this.m_numAttribs; i++) {
/*  440: 836 */       if (subset.get(i)) {
/*  441: 837 */         numAttributes++;
/*  442:     */       }
/*  443:     */     }
/*  444: 842 */     int[] featArray = new int[numAttributes + 1];
/*  445:     */     
/*  446: 844 */     i = 0;
/*  447: 844 */     for (int j = 0; i < this.m_numAttribs; i++) {
/*  448: 845 */       if (subset.get(i)) {
/*  449: 846 */         featArray[(j++)] = i;
/*  450:     */       }
/*  451:     */     }
/*  452: 850 */     featArray[j] = this.m_classIndex;
/*  453: 851 */     delTransform.setAttributeIndicesArray(featArray);
/*  454: 852 */     delTransform.setInputFormat(trainCopy);
/*  455: 853 */     trainCopy = Filter.useFilter(trainCopy, delTransform);
/*  456: 854 */     if ((!this.m_useTraining) || (this.m_usePercentageSplit)) {
/*  457: 855 */       testCopy = Filter.useFilter(testCopy, delTransform);
/*  458:     */     }
/*  459: 859 */     classifier.buildClassifier(trainCopy);
/*  460:     */     
/*  461: 861 */     evaluation = new Evaluation(trainCopy);
/*  462: 862 */     if ((!this.m_useTraining) || (this.m_usePercentageSplit)) {
/*  463: 863 */       evaluation.evaluateModel(classifier, testCopy, new Object[0]);
/*  464:     */     } else {
/*  465: 865 */       evaluation.evaluateModel(classifier, trainCopy, new Object[0]);
/*  466:     */     }
/*  467: 868 */     switch (this.m_evaluationMeasure)
/*  468:     */     {
/*  469:     */     case 1: 
/*  470: 870 */       evalMetric = evaluation.errorRate();
/*  471: 871 */       break;
/*  472:     */     case 2: 
/*  473: 873 */       evalMetric = evaluation.errorRate();
/*  474: 874 */       break;
/*  475:     */     case 3: 
/*  476: 876 */       evalMetric = evaluation.rootMeanSquaredError();
/*  477: 877 */       break;
/*  478:     */     case 4: 
/*  479: 879 */       evalMetric = evaluation.meanAbsoluteError();
/*  480: 880 */       break;
/*  481:     */     case 5: 
/*  482: 882 */       if (this.m_IRClassVal < 0) {
/*  483: 883 */         evalMetric = evaluation.weightedFMeasure();
/*  484:     */       } else {
/*  485: 885 */         evalMetric = evaluation.fMeasure(this.m_IRClassVal);
/*  486:     */       }
/*  487: 887 */       break;
/*  488:     */     case 6: 
/*  489: 889 */       if (this.m_IRClassVal < 0) {
/*  490: 890 */         evalMetric = evaluation.weightedAreaUnderROC();
/*  491:     */       } else {
/*  492: 892 */         evalMetric = evaluation.areaUnderROC(this.m_IRClassVal);
/*  493:     */       }
/*  494: 894 */       break;
/*  495:     */     case 7: 
/*  496: 896 */       if (this.m_IRClassVal < 0) {
/*  497: 897 */         evalMetric = evaluation.weightedAreaUnderPRC();
/*  498:     */       } else {
/*  499: 899 */         evalMetric = evaluation.areaUnderPRC(this.m_IRClassVal);
/*  500:     */       }
/*  501:     */       break;
/*  502:     */     }
/*  503: 904 */     switch (this.m_evaluationMeasure)
/*  504:     */     {
/*  505:     */     case 1: 
/*  506:     */     case 2: 
/*  507:     */     case 3: 
/*  508:     */     case 4: 
/*  509: 909 */       evalMetric = -evalMetric;
/*  510:     */     }
/*  511: 913 */     return evalMetric;
/*  512:     */   }
/*  513:     */   
/*  514:     */   public double evaluateSubset(BitSet subset, Instances holdOut)
/*  515:     */     throws Exception
/*  516:     */   {
/*  517: 932 */     double evalMetric = 0.0D;
/*  518: 933 */     int numAttributes = 0;
/*  519: 934 */     Instances trainCopy = null;
/*  520: 935 */     Instances testCopy = null;
/*  521: 936 */     String[] cOpts = null;
/*  522: 937 */     Evaluation evaluation = null;
/*  523: 938 */     if ((this.m_ClassifierTemplate instanceof OptionHandler)) {
/*  524: 939 */       cOpts = ((OptionHandler)this.m_ClassifierTemplate).getOptions();
/*  525:     */     }
/*  526: 941 */     Classifier classifier = AbstractClassifier.forName(this.m_ClassifierTemplate.getClass().getName(), cOpts);
/*  527: 944 */     if (!this.m_trainingInstances.equalHeaders(holdOut)) {
/*  528: 945 */       throw new Exception("evaluateSubset : Incompatable instance types.\n" + this.m_trainingInstances.equalHeadersMsg(holdOut));
/*  529:     */     }
/*  530: 949 */     Remove delTransform = new Remove();
/*  531: 950 */     delTransform.setInvertSelection(true);
/*  532:     */     
/*  533: 952 */     trainCopy = new Instances(this.m_trainingInstances);
/*  534:     */     
/*  535: 954 */     testCopy = new Instances(holdOut);
/*  536: 957 */     for (int i = 0; i < this.m_numAttribs; i++) {
/*  537: 958 */       if (subset.get(i)) {
/*  538: 959 */         numAttributes++;
/*  539:     */       }
/*  540:     */     }
/*  541: 964 */     int[] featArray = new int[numAttributes + 1];
/*  542:     */     
/*  543: 966 */     i = 0;
/*  544: 966 */     for (int j = 0; i < this.m_numAttribs; i++) {
/*  545: 967 */       if (subset.get(i)) {
/*  546: 968 */         featArray[(j++)] = i;
/*  547:     */       }
/*  548:     */     }
/*  549: 972 */     featArray[j] = this.m_classIndex;
/*  550: 973 */     delTransform.setAttributeIndicesArray(featArray);
/*  551: 974 */     delTransform.setInputFormat(trainCopy);
/*  552: 975 */     trainCopy = Filter.useFilter(trainCopy, delTransform);
/*  553: 976 */     testCopy = Filter.useFilter(testCopy, delTransform);
/*  554:     */     
/*  555:     */ 
/*  556: 979 */     classifier.buildClassifier(trainCopy);
/*  557:     */     
/*  558: 981 */     evaluation = new Evaluation(trainCopy);
/*  559: 982 */     evaluation.evaluateModel(classifier, testCopy, new Object[0]);
/*  560: 984 */     switch (this.m_evaluationMeasure)
/*  561:     */     {
/*  562:     */     case 1: 
/*  563: 986 */       evalMetric = evaluation.errorRate();
/*  564: 987 */       break;
/*  565:     */     case 2: 
/*  566: 989 */       evalMetric = evaluation.errorRate();
/*  567: 990 */       break;
/*  568:     */     case 3: 
/*  569: 992 */       evalMetric = evaluation.rootMeanSquaredError();
/*  570: 993 */       break;
/*  571:     */     case 4: 
/*  572: 995 */       evalMetric = evaluation.meanAbsoluteError();
/*  573: 996 */       break;
/*  574:     */     case 5: 
/*  575: 998 */       if (this.m_IRClassVal < 0) {
/*  576: 999 */         evalMetric = evaluation.weightedFMeasure();
/*  577:     */       } else {
/*  578:1001 */         evalMetric = evaluation.fMeasure(this.m_IRClassVal);
/*  579:     */       }
/*  580:1003 */       break;
/*  581:     */     case 6: 
/*  582:1005 */       if (this.m_IRClassVal < 0) {
/*  583:1006 */         evalMetric = evaluation.weightedAreaUnderROC();
/*  584:     */       } else {
/*  585:1008 */         evalMetric = evaluation.areaUnderROC(this.m_IRClassVal);
/*  586:     */       }
/*  587:1010 */       break;
/*  588:     */     case 7: 
/*  589:1012 */       if (this.m_IRClassVal < 0) {
/*  590:1013 */         evalMetric = evaluation.weightedAreaUnderPRC();
/*  591:     */       } else {
/*  592:1015 */         evalMetric = evaluation.areaUnderPRC(this.m_IRClassVal);
/*  593:     */       }
/*  594:     */       break;
/*  595:     */     }
/*  596:1020 */     switch (this.m_evaluationMeasure)
/*  597:     */     {
/*  598:     */     case 1: 
/*  599:     */     case 2: 
/*  600:     */     case 3: 
/*  601:     */     case 4: 
/*  602:1025 */       evalMetric = -evalMetric;
/*  603:     */     }
/*  604:1029 */     return evalMetric;
/*  605:     */   }
/*  606:     */   
/*  607:     */   public double evaluateSubset(BitSet subset, Instance holdOut, boolean retrain)
/*  608:     */     throws Exception
/*  609:     */   {
/*  610:1050 */     if (this.m_evaluationMeasure != 1) {
/*  611:1051 */       throw new Exception("Can only use default evaluation measure in the method");
/*  612:     */     }
/*  613:1056 */     int numAttributes = 0;
/*  614:1057 */     Instances trainCopy = null;
/*  615:1058 */     Instance testCopy = null;
/*  616:1059 */     if (!this.m_trainingInstances.equalHeaders(holdOut.dataset())) {
/*  617:1060 */       throw new Exception("evaluateSubset : Incompatable instance types.\n" + this.m_trainingInstances.equalHeadersMsg(holdOut.dataset()));
/*  618:     */     }
/*  619:1064 */     Remove delTransform = new Remove();
/*  620:1065 */     delTransform.setInvertSelection(true);
/*  621:     */     
/*  622:1067 */     trainCopy = new Instances(this.m_trainingInstances);
/*  623:     */     
/*  624:1069 */     testCopy = (Instance)holdOut.copy();
/*  625:1072 */     for (int i = 0; i < this.m_numAttribs; i++) {
/*  626:1073 */       if (subset.get(i)) {
/*  627:1074 */         numAttributes++;
/*  628:     */       }
/*  629:     */     }
/*  630:1079 */     int[] featArray = new int[numAttributes + 1];
/*  631:     */     
/*  632:1081 */     i = 0;
/*  633:1081 */     for (int j = 0; i < this.m_numAttribs; i++) {
/*  634:1082 */       if (subset.get(i)) {
/*  635:1083 */         featArray[(j++)] = i;
/*  636:     */       }
/*  637:     */     }
/*  638:1086 */     featArray[j] = this.m_classIndex;
/*  639:1087 */     delTransform.setAttributeIndicesArray(featArray);
/*  640:1088 */     delTransform.setInputFormat(trainCopy);
/*  641:1090 */     if (retrain)
/*  642:     */     {
/*  643:1091 */       trainCopy = Filter.useFilter(trainCopy, delTransform);
/*  644:     */       
/*  645:1093 */       this.m_Classifier.buildClassifier(trainCopy);
/*  646:     */     }
/*  647:1096 */     delTransform.input(testCopy);
/*  648:1097 */     testCopy = delTransform.output();
/*  649:     */     
/*  650:     */ 
/*  651:     */ 
/*  652:1101 */     double[] distrib = this.m_Classifier.distributionForInstance(testCopy);
/*  653:     */     double pred;
/*  654:     */     double pred;
/*  655:1102 */     if (this.m_trainingInstances.classAttribute().isNominal()) {
/*  656:1103 */       pred = distrib[((int)testCopy.classValue())];
/*  657:     */     } else {
/*  658:1105 */       pred = distrib[0];
/*  659:     */     }
/*  660:     */     double error;
/*  661:     */     double error;
/*  662:1108 */     if (this.m_trainingInstances.classAttribute().isNominal()) {
/*  663:1109 */       error = 1.0D - pred;
/*  664:     */     } else {
/*  665:1111 */       error = testCopy.classValue() - pred;
/*  666:     */     }
/*  667:1116 */     return -error;
/*  668:     */   }
/*  669:     */   
/*  670:     */   public String toString()
/*  671:     */   {
/*  672:1126 */     StringBuffer text = new StringBuffer();
/*  673:1128 */     if (this.m_trainingInstances == null)
/*  674:     */     {
/*  675:1129 */       text.append("\tClassifier subset evaluator has not been built yet\n");
/*  676:     */     }
/*  677:     */     else
/*  678:     */     {
/*  679:1131 */       text.append("\tClassifier Subset Evaluator\n");
/*  680:1132 */       text.append("\tLearning scheme: " + getClassifier().getClass().getName() + "\n");
/*  681:     */       
/*  682:1134 */       text.append("\tScheme options: ");
/*  683:1135 */       String[] classifierOptions = new String[0];
/*  684:1137 */       if ((this.m_ClassifierTemplate instanceof OptionHandler))
/*  685:     */       {
/*  686:1138 */         classifierOptions = ((OptionHandler)this.m_ClassifierTemplate).getOptions();
/*  687:1140 */         for (String classifierOption : classifierOptions) {
/*  688:1141 */           text.append(classifierOption + " ");
/*  689:     */         }
/*  690:     */       }
/*  691:1145 */       text.append("\n");
/*  692:1146 */       text.append("\tHold out/test set: ");
/*  693:1147 */       if (!this.m_useTraining)
/*  694:     */       {
/*  695:1148 */         if (getHoldOutFile().getPath().startsWith("Click to set")) {
/*  696:1149 */           text.append("none\n");
/*  697:     */         } else {
/*  698:1151 */           text.append(getHoldOutFile().getPath() + '\n');
/*  699:     */         }
/*  700:     */       }
/*  701:1154 */       else if (this.m_usePercentageSplit) {
/*  702:1155 */         text.append("Percentage split: " + this.m_splitPercent + "\n");
/*  703:     */       } else {
/*  704:1157 */         text.append("Training data\n");
/*  705:     */       }
/*  706:1161 */       String IRClassL = "";
/*  707:1162 */       if (this.m_IRClassVal >= 0) {
/*  708:1163 */         IRClassL = "(class value: " + this.m_trainingInstances.classAttribute().value(this.m_IRClassVal) + ")";
/*  709:     */       }
/*  710:1166 */       switch (this.m_evaluationMeasure)
/*  711:     */       {
/*  712:     */       case 1: 
/*  713:     */       case 2: 
/*  714:1169 */         if (this.m_trainingInstances.attribute(this.m_classIndex).isNumeric()) {
/*  715:1170 */           text.append("\tSubset evaluation: RMSE\n");
/*  716:     */         } else {
/*  717:1172 */           text.append("\tSubset evaluation: classification error\n");
/*  718:     */         }
/*  719:1174 */         break;
/*  720:     */       case 3: 
/*  721:1176 */         if (this.m_trainingInstances.attribute(this.m_classIndex).isNumeric()) {
/*  722:1177 */           text.append("\tSubset evaluation: RMSE\n");
/*  723:     */         } else {
/*  724:1179 */           text.append("\tSubset evaluation: RMSE (probability estimates)\n");
/*  725:     */         }
/*  726:1181 */         break;
/*  727:     */       case 4: 
/*  728:1183 */         if (this.m_trainingInstances.attribute(this.m_classIndex).isNumeric()) {
/*  729:1184 */           text.append("\tSubset evaluation: MAE\n");
/*  730:     */         } else {
/*  731:1186 */           text.append("\tSubset evaluation: MAE (probability estimates)\n");
/*  732:     */         }
/*  733:1188 */         break;
/*  734:     */       case 5: 
/*  735:1190 */         text.append("\tSubset evaluation: F-measure " + (this.m_IRClassVal >= 0 ? IRClassL : "") + "\n");
/*  736:     */         
/*  737:1192 */         break;
/*  738:     */       case 6: 
/*  739:1194 */         text.append("\tSubset evaluation: area under the ROC curve " + (this.m_IRClassVal >= 0 ? IRClassL : "") + "\n");
/*  740:     */         
/*  741:1196 */         break;
/*  742:     */       case 7: 
/*  743:1198 */         text.append("\tSubset evalation: area under the precision-recal curve " + (this.m_IRClassVal >= 0 ? IRClassL : "") + "\n");
/*  744:     */       }
/*  745:     */     }
/*  746:1203 */     return text.toString();
/*  747:     */   }
/*  748:     */   
/*  749:     */   protected void resetOptions()
/*  750:     */   {
/*  751:1210 */     this.m_trainingInstances = null;
/*  752:1211 */     this.m_ClassifierTemplate = new ZeroR();
/*  753:1212 */     this.m_holdOutFile = new File("Click to set hold out or test instances");
/*  754:1213 */     this.m_holdOutInstances = null;
/*  755:1214 */     this.m_useTraining = false;
/*  756:1215 */     this.m_splitPercent = "90";
/*  757:1216 */     this.m_usePercentageSplit = false;
/*  758:1217 */     this.m_evaluationMeasure = 1;
/*  759:1218 */     this.m_IRClassVal = -1;
/*  760:     */   }
/*  761:     */   
/*  762:     */   public String getRevision()
/*  763:     */   {
/*  764:1228 */     return RevisionUtils.extract("$Revision: 10332 $");
/*  765:     */   }
/*  766:     */   
/*  767:     */   public static void main(String[] args)
/*  768:     */   {
/*  769:1237 */     runEvaluator(new ClassifierSubsetEval(), args);
/*  770:     */   }
/*  771:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.ClassifierSubsetEval
 * JD-Core Version:    0.7.0.1
 */