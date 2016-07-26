/*    1:     */ package weka.classifiers.meta;
/*    2:     */ 
/*    3:     */ import java.io.BufferedInputStream;
/*    4:     */ import java.io.File;
/*    5:     */ import java.io.FileInputStream;
/*    6:     */ import java.io.ObjectInputStream;
/*    7:     */ import java.io.PrintStream;
/*    8:     */ import java.util.ArrayList;
/*    9:     */ import java.util.Collections;
/*   10:     */ import java.util.Enumeration;
/*   11:     */ import java.util.List;
/*   12:     */ import java.util.Vector;
/*   13:     */ import weka.classifiers.Classifier;
/*   14:     */ import weka.classifiers.RandomizableMultipleClassifiersCombiner;
/*   15:     */ import weka.classifiers.rules.ZeroR;
/*   16:     */ import weka.core.Aggregateable;
/*   17:     */ import weka.core.Attribute;
/*   18:     */ import weka.core.Capabilities;
/*   19:     */ import weka.core.Capabilities.Capability;
/*   20:     */ import weka.core.Environment;
/*   21:     */ import weka.core.EnvironmentHandler;
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
/*   34:     */ 
/*   35:     */ public class Vote
/*   36:     */   extends RandomizableMultipleClassifiersCombiner
/*   37:     */   implements TechnicalInformationHandler, EnvironmentHandler, Aggregateable<Classifier>
/*   38:     */ {
/*   39:     */   static final long serialVersionUID = -637891196294399624L;
/*   40:     */   public static final int AVERAGE_RULE = 1;
/*   41:     */   public static final int PRODUCT_RULE = 2;
/*   42:     */   public static final int MAJORITY_VOTING_RULE = 3;
/*   43:     */   public static final int MIN_RULE = 4;
/*   44:     */   public static final int MAX_RULE = 5;
/*   45:     */   public static final int MEDIAN_RULE = 6;
/*   46: 182 */   public static final Tag[] TAGS_RULES = { new Tag(1, "AVG", "Average of Probabilities"), new Tag(2, "PROD", "Product of Probabilities"), new Tag(3, "MAJ", "Majority Voting"), new Tag(4, "MIN", "Minimum Probability"), new Tag(5, "MAX", "Maximum Probability"), new Tag(6, "MED", "Median") };
/*   47: 191 */   protected int m_CombinationRule = 1;
/*   48: 194 */   protected List<String> m_classifiersToLoad = new ArrayList();
/*   49: 197 */   protected List<Classifier> m_preBuiltClassifiers = new ArrayList();
/*   50: 201 */   protected transient Environment m_env = Environment.getSystemWide();
/*   51:     */   protected Instances m_structure;
/*   52:     */   protected boolean m_dontPrintModels;
/*   53:     */   
/*   54:     */   public String globalInfo()
/*   55:     */   {
/*   56: 216 */     return "Class for combining classifiers. Different combinations of probability estimates for classification are available.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*   57:     */   }
/*   58:     */   
/*   59:     */   public Enumeration<Option> listOptions()
/*   60:     */   {
/*   61: 229 */     Vector<Option> result = new Vector();
/*   62:     */     
/*   63: 231 */     result.addElement(new Option("\tFull path to serialized classifier to include.\n\tMay be specified multiple times to include\n\tmultiple serialized classifiers. Note: it does\n\tnot make sense to use pre-built classifiers in\n\ta cross-validation.", "P", 1, "-P <path to serialized classifier>"));
/*   64:     */     
/*   65:     */ 
/*   66:     */ 
/*   67:     */ 
/*   68:     */ 
/*   69:     */ 
/*   70:     */ 
/*   71: 239 */     result.addElement(new Option("\tThe combination rule to use\n\t(default: AVG)", "R", 1, "-R " + Tag.toOptionList(TAGS_RULES)));
/*   72:     */     
/*   73:     */ 
/*   74: 242 */     result.addElement(new Option("\tSuppress the printing of the individual models in the output", "do-not-print", 0, "-do-not-print"));
/*   75:     */     
/*   76:     */ 
/*   77:     */ 
/*   78: 246 */     result.addAll(Collections.list(super.listOptions()));
/*   79:     */     
/*   80: 248 */     return result.elements();
/*   81:     */   }
/*   82:     */   
/*   83:     */   public String[] getOptions()
/*   84:     */   {
/*   85: 259 */     Vector<String> result = new Vector();
/*   86:     */     
/*   87:     */ 
/*   88: 262 */     String[] options = super.getOptions();
/*   89: 263 */     for (int i = 0; i < options.length; i++) {
/*   90: 264 */       result.add(options[i]);
/*   91:     */     }
/*   92: 267 */     result.add("-R");
/*   93: 268 */     result.add("" + getCombinationRule());
/*   94: 270 */     for (i = 0; i < this.m_classifiersToLoad.size(); i++)
/*   95:     */     {
/*   96: 271 */       result.add("-P");
/*   97: 272 */       result.add(this.m_classifiersToLoad.get(i));
/*   98:     */     }
/*   99: 275 */     if (this.m_dontPrintModels) {
/*  100: 276 */       result.add("-do-not-print");
/*  101:     */     }
/*  102: 279 */     return (String[])result.toArray(new String[result.size()]);
/*  103:     */   }
/*  104:     */   
/*  105:     */   public void setOptions(String[] options)
/*  106:     */     throws Exception
/*  107:     */   {
/*  108: 359 */     String tmpStr = Utils.getOption('R', options);
/*  109: 360 */     if (tmpStr.length() != 0) {
/*  110: 361 */       setCombinationRule(new SelectedTag(tmpStr, TAGS_RULES));
/*  111:     */     } else {
/*  112: 363 */       setCombinationRule(new SelectedTag(1, TAGS_RULES));
/*  113:     */     }
/*  114: 366 */     this.m_classifiersToLoad.clear();
/*  115:     */     for (;;)
/*  116:     */     {
/*  117: 368 */       String loadString = Utils.getOption('P', options);
/*  118: 369 */       if (loadString.length() == 0) {
/*  119:     */         break;
/*  120:     */       }
/*  121: 373 */       this.m_classifiersToLoad.add(loadString);
/*  122:     */     }
/*  123: 376 */     setDoNotPrintModels(Utils.getFlag("-do-not-print", options));
/*  124:     */     
/*  125: 378 */     super.setOptions(options);
/*  126:     */   }
/*  127:     */   
/*  128:     */   public TechnicalInformation getTechnicalInformation()
/*  129:     */   {
/*  130: 393 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.BOOK);
/*  131: 394 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Ludmila I. Kuncheva");
/*  132: 395 */     result.setValue(TechnicalInformation.Field.TITLE, "Combining Pattern Classifiers: Methods and Algorithms");
/*  133:     */     
/*  134: 397 */     result.setValue(TechnicalInformation.Field.YEAR, "2004");
/*  135: 398 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "John Wiley and Sons, Inc.");
/*  136:     */     
/*  137: 400 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.ARTICLE);
/*  138: 401 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "J. Kittler and M. Hatef and Robert P.W. Duin and J. Matas");
/*  139:     */     
/*  140: 403 */     additional.setValue(TechnicalInformation.Field.YEAR, "1998");
/*  141: 404 */     additional.setValue(TechnicalInformation.Field.TITLE, "On combining classifiers");
/*  142: 405 */     additional.setValue(TechnicalInformation.Field.JOURNAL, "IEEE Transactions on Pattern Analysis and Machine Intelligence");
/*  143:     */     
/*  144: 407 */     additional.setValue(TechnicalInformation.Field.VOLUME, "20");
/*  145: 408 */     additional.setValue(TechnicalInformation.Field.NUMBER, "3");
/*  146: 409 */     additional.setValue(TechnicalInformation.Field.PAGES, "226-239");
/*  147:     */     
/*  148: 411 */     return result;
/*  149:     */   }
/*  150:     */   
/*  151:     */   public Capabilities getCapabilities()
/*  152:     */   {
/*  153: 421 */     Capabilities result = super.getCapabilities();
/*  154: 423 */     if ((this.m_preBuiltClassifiers.size() == 0) && (this.m_classifiersToLoad.size() > 0)) {
/*  155:     */       try
/*  156:     */       {
/*  157: 425 */         loadClassifiers(null);
/*  158:     */       }
/*  159:     */       catch (Exception e)
/*  160:     */       {
/*  161: 427 */         e.printStackTrace();
/*  162:     */       }
/*  163:     */     }
/*  164: 431 */     if (this.m_preBuiltClassifiers.size() > 0)
/*  165:     */     {
/*  166: 432 */       if (this.m_Classifiers.length == 0) {
/*  167: 433 */         result = (Capabilities)((Classifier)this.m_preBuiltClassifiers.get(0)).getCapabilities().clone();
/*  168:     */       }
/*  169: 436 */       for (int i = 1; i < this.m_preBuiltClassifiers.size(); i++) {
/*  170: 437 */         result.and(((Classifier)this.m_preBuiltClassifiers.get(i)).getCapabilities());
/*  171:     */       }
/*  172: 440 */       for (Capabilities.Capability cap : Capabilities.Capability.values()) {
/*  173: 441 */         result.enableDependency(cap);
/*  174:     */       }
/*  175:     */     }
/*  176: 446 */     if ((this.m_CombinationRule == 2) || (this.m_CombinationRule == 3))
/*  177:     */     {
/*  178: 448 */       result.disableAllClasses();
/*  179: 449 */       result.disableAllClassDependencies();
/*  180: 450 */       result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  181: 451 */       result.enableDependency(Capabilities.Capability.NOMINAL_CLASS);
/*  182:     */     }
/*  183: 452 */     else if (this.m_CombinationRule == 6)
/*  184:     */     {
/*  185: 453 */       result.disableAllClasses();
/*  186: 454 */       result.disableAllClassDependencies();
/*  187: 455 */       result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*  188: 456 */       result.enableDependency(Capabilities.Capability.NUMERIC_CLASS);
/*  189:     */     }
/*  190: 459 */     return result;
/*  191:     */   }
/*  192:     */   
/*  193:     */   public void buildClassifier(Instances data)
/*  194:     */     throws Exception
/*  195:     */   {
/*  196: 474 */     Instances newData = new Instances(data);
/*  197: 475 */     newData.deleteWithMissingClass();
/*  198: 476 */     this.m_structure = new Instances(newData, 0);
/*  199: 478 */     if (this.m_classifiersToLoad.size() > 0)
/*  200:     */     {
/*  201: 479 */       this.m_preBuiltClassifiers.clear();
/*  202: 480 */       loadClassifiers(data);
/*  203: 482 */       if ((this.m_Classifiers.length == 1) && ((this.m_Classifiers[0] instanceof ZeroR))) {
/*  204: 485 */         this.m_Classifiers = new Classifier[0];
/*  205:     */       }
/*  206:     */     }
/*  207: 490 */     getCapabilities().testWithFail(data);
/*  208: 492 */     for (int i = 0; i < this.m_Classifiers.length; i++) {
/*  209: 493 */       getClassifier(i).buildClassifier(newData);
/*  210:     */     }
/*  211:     */   }
/*  212:     */   
/*  213:     */   private void loadClassifiers(Instances data)
/*  214:     */     throws Exception
/*  215:     */   {
/*  216: 506 */     for (String path : this.m_classifiersToLoad)
/*  217:     */     {
/*  218: 507 */       if (Environment.containsEnvVariables(path)) {
/*  219:     */         try
/*  220:     */         {
/*  221: 509 */           path = this.m_env.substitute(path);
/*  222:     */         }
/*  223:     */         catch (Exception ex) {}
/*  224:     */       }
/*  225: 514 */       File toLoad = new File(path);
/*  226: 515 */       if (!toLoad.isFile()) {
/*  227: 516 */         throw new Exception("\"" + path + "\" does not seem to be a valid file!");
/*  228:     */       }
/*  229: 519 */       ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(new FileInputStream(toLoad)));
/*  230:     */       
/*  231:     */ 
/*  232: 522 */       Object c = is.readObject();
/*  233: 523 */       if (!(c instanceof Classifier))
/*  234:     */       {
/*  235: 524 */         is.close();
/*  236: 525 */         throw new Exception("\"" + path + "\" does not contain a classifier!");
/*  237:     */       }
/*  238: 527 */       Object header = null;
/*  239: 528 */       header = is.readObject();
/*  240: 529 */       if (((header instanceof Instances)) && 
/*  241: 530 */         (data != null) && (!data.equalHeaders((Instances)header)))
/*  242:     */       {
/*  243: 531 */         is.close();
/*  244: 532 */         throw new Exception("\"" + path + "\" was trained with data that is " + "of a differnet structure than the incoming training data");
/*  245:     */       }
/*  246: 536 */       if (header == null) {
/*  247: 537 */         System.out.println("[Vote] warning: no header instances for \"" + path + "\"");
/*  248:     */       }
/*  249: 540 */       is.close();
/*  250: 541 */       addPreBuiltClassifier((Classifier)c);
/*  251:     */     }
/*  252:     */   }
/*  253:     */   
/*  254:     */   public void addPreBuiltClassifier(Classifier c)
/*  255:     */   {
/*  256: 551 */     this.m_preBuiltClassifiers.add(c);
/*  257:     */   }
/*  258:     */   
/*  259:     */   public void removePreBuiltClassifier(Classifier c)
/*  260:     */   {
/*  261: 560 */     this.m_preBuiltClassifiers.remove(c);
/*  262:     */   }
/*  263:     */   
/*  264:     */   public double classifyInstance(Instance instance)
/*  265:     */     throws Exception
/*  266:     */   {
/*  267:     */     double result;
/*  268: 577 */     switch (this.m_CombinationRule)
/*  269:     */     {
/*  270:     */     case 1: 
/*  271:     */     case 2: 
/*  272:     */     case 3: 
/*  273:     */     case 4: 
/*  274:     */     case 5: 
/*  275: 583 */       double[] dist = distributionForInstance(instance);
/*  276:     */       double result;
/*  277: 584 */       if (instance.classAttribute().isNominal())
/*  278:     */       {
/*  279: 585 */         int index = Utils.maxIndex(dist);
/*  280:     */         double result;
/*  281: 586 */         if (dist[index] == 0.0D) {
/*  282: 587 */           result = Utils.missingValue();
/*  283:     */         } else {
/*  284: 589 */           result = index;
/*  285:     */         }
/*  286:     */       }
/*  287:     */       else
/*  288:     */       {
/*  289:     */         double result;
/*  290: 591 */         if (instance.classAttribute().isNumeric()) {
/*  291: 592 */           result = dist[0];
/*  292:     */         } else {
/*  293: 594 */           result = Utils.missingValue();
/*  294:     */         }
/*  295:     */       }
/*  296: 596 */       break;
/*  297:     */     case 6: 
/*  298: 598 */       result = classifyInstanceMedian(instance);
/*  299: 599 */       break;
/*  300:     */     default: 
/*  301: 601 */       throw new IllegalStateException("Unknown combination rule '" + this.m_CombinationRule + "'!");
/*  302:     */     }
/*  303: 605 */     return result;
/*  304:     */   }
/*  305:     */   
/*  306:     */   protected double classifyInstanceMedian(Instance instance)
/*  307:     */     throws Exception
/*  308:     */   {
/*  309: 618 */     double[] results = new double[this.m_Classifiers.length + this.m_preBuiltClassifiers.size()];
/*  310:     */     
/*  311:     */ 
/*  312: 621 */     int numResults = 0;
/*  313: 622 */     for (Classifier m_Classifier : this.m_Classifiers)
/*  314:     */     {
/*  315: 623 */       double pred = m_Classifier.classifyInstance(instance);
/*  316: 624 */       if (!Utils.isMissingValue(pred)) {
/*  317: 625 */         results[(numResults++)] = pred;
/*  318:     */       }
/*  319:     */     }
/*  320: 629 */     for (int i = 0; i < this.m_preBuiltClassifiers.size(); i++)
/*  321:     */     {
/*  322: 630 */       double pred = ((Classifier)this.m_preBuiltClassifiers.get(i)).classifyInstance(instance);
/*  323: 631 */       if (!Utils.isMissingValue(pred)) {
/*  324: 632 */         results[(numResults++)] = pred;
/*  325:     */       }
/*  326:     */     }
/*  327: 636 */     if (numResults == 0) {
/*  328: 637 */       return Utils.missingValue();
/*  329:     */     }
/*  330: 638 */     if (numResults == 1) {
/*  331: 639 */       return results[0];
/*  332:     */     }
/*  333: 641 */     double[] actualResults = new double[numResults];
/*  334: 642 */     System.arraycopy(results, 0, actualResults, 0, numResults);
/*  335: 643 */     return Utils.kthSmallestValue(actualResults, actualResults.length / 2);
/*  336:     */   }
/*  337:     */   
/*  338:     */   public double[] distributionForInstance(Instance instance)
/*  339:     */     throws Exception
/*  340:     */   {
/*  341: 656 */     double[] result = new double[instance.numClasses()];
/*  342: 658 */     switch (this.m_CombinationRule)
/*  343:     */     {
/*  344:     */     case 1: 
/*  345: 660 */       result = distributionForInstanceAverage(instance);
/*  346: 661 */       break;
/*  347:     */     case 2: 
/*  348: 663 */       result = distributionForInstanceProduct(instance);
/*  349: 664 */       break;
/*  350:     */     case 3: 
/*  351: 666 */       result = distributionForInstanceMajorityVoting(instance);
/*  352: 667 */       break;
/*  353:     */     case 4: 
/*  354: 669 */       result = distributionForInstanceMin(instance);
/*  355: 670 */       break;
/*  356:     */     case 5: 
/*  357: 672 */       result = distributionForInstanceMax(instance);
/*  358: 673 */       break;
/*  359:     */     case 6: 
/*  360: 675 */       result[0] = classifyInstance(instance);
/*  361: 676 */       break;
/*  362:     */     default: 
/*  363: 678 */       throw new IllegalStateException("Unknown combination rule '" + this.m_CombinationRule + "'!");
/*  364:     */     }
/*  365: 682 */     if ((!instance.classAttribute().isNumeric()) && (Utils.sum(result) > 0.0D)) {
/*  366: 683 */       Utils.normalize(result);
/*  367:     */     }
/*  368: 686 */     return result;
/*  369:     */   }
/*  370:     */   
/*  371:     */   protected double[] distributionForInstanceAverage(Instance instance)
/*  372:     */     throws Exception
/*  373:     */   {
/*  374: 700 */     double[] probs = new double[instance.numClasses()];
/*  375:     */     
/*  376: 702 */     double numPredictions = 0.0D;
/*  377: 703 */     for (int i = 0; i < this.m_Classifiers.length; i++)
/*  378:     */     {
/*  379: 704 */       double[] dist = getClassifier(i).distributionForInstance(instance);
/*  380: 705 */       if ((!instance.classAttribute().isNumeric()) || (!Utils.isMissingValue(dist[0])))
/*  381:     */       {
/*  382: 707 */         for (int j = 0; j < dist.length; j++) {
/*  383: 708 */           probs[j] += dist[j];
/*  384:     */         }
/*  385: 710 */         numPredictions += 1.0D;
/*  386:     */       }
/*  387:     */     }
/*  388: 714 */     for (int i = 0; i < this.m_preBuiltClassifiers.size(); i++)
/*  389:     */     {
/*  390: 715 */       double[] dist = ((Classifier)this.m_preBuiltClassifiers.get(i)).distributionForInstance(instance);
/*  391: 717 */       if ((!instance.classAttribute().isNumeric()) || (!Utils.isMissingValue(dist[0])))
/*  392:     */       {
/*  393: 719 */         for (int j = 0; j < dist.length; j++) {
/*  394: 720 */           probs[j] += dist[j];
/*  395:     */         }
/*  396: 722 */         numPredictions += 1.0D;
/*  397:     */       }
/*  398:     */     }
/*  399: 726 */     if (instance.classAttribute().isNumeric())
/*  400:     */     {
/*  401: 727 */       if (numPredictions == 0.0D) {
/*  402: 728 */         probs[0] = Utils.missingValue();
/*  403:     */       } else {
/*  404: 730 */         for (int j = 0; j < probs.length; j++) {
/*  405: 731 */           probs[j] /= numPredictions;
/*  406:     */         }
/*  407:     */       }
/*  408:     */     }
/*  409: 737 */     else if (Utils.sum(probs) > 0.0D) {
/*  410: 738 */       Utils.normalize(probs);
/*  411:     */     }
/*  412: 742 */     return probs;
/*  413:     */   }
/*  414:     */   
/*  415:     */   protected double[] distributionForInstanceProduct(Instance instance)
/*  416:     */     throws Exception
/*  417:     */   {
/*  418: 756 */     double[] probs = new double[instance.numClasses()];
/*  419: 757 */     for (int i = 0; i < probs.length; i++) {
/*  420: 758 */       probs[i] = 1.0D;
/*  421:     */     }
/*  422: 761 */     int numPredictions = 0;
/*  423: 762 */     for (int i = 0; i < this.m_Classifiers.length; i++)
/*  424:     */     {
/*  425: 763 */       double[] dist = getClassifier(i).distributionForInstance(instance);
/*  426: 764 */       if (Utils.sum(dist) > 0.0D)
/*  427:     */       {
/*  428: 765 */         for (int j = 0; j < dist.length; j++) {
/*  429: 766 */           probs[j] *= dist[j];
/*  430:     */         }
/*  431: 768 */         numPredictions++;
/*  432:     */       }
/*  433:     */     }
/*  434: 772 */     for (int i = 0; i < this.m_preBuiltClassifiers.size(); i++)
/*  435:     */     {
/*  436: 773 */       double[] dist = ((Classifier)this.m_preBuiltClassifiers.get(i)).distributionForInstance(instance);
/*  437: 775 */       if (Utils.sum(dist) > 0.0D)
/*  438:     */       {
/*  439: 776 */         for (int j = 0; j < dist.length; j++) {
/*  440: 777 */           probs[j] *= dist[j];
/*  441:     */         }
/*  442: 779 */         numPredictions++;
/*  443:     */       }
/*  444:     */     }
/*  445: 784 */     if (numPredictions == 0) {
/*  446: 785 */       return new double[instance.numClasses()];
/*  447:     */     }
/*  448: 789 */     if (Utils.sum(probs) > 0.0D) {
/*  449: 790 */       Utils.normalize(probs);
/*  450:     */     }
/*  451: 793 */     return probs;
/*  452:     */   }
/*  453:     */   
/*  454:     */   protected double[] distributionForInstanceMajorityVoting(Instance instance)
/*  455:     */     throws Exception
/*  456:     */   {
/*  457: 807 */     double[] probs = new double[instance.classAttribute().numValues()];
/*  458: 808 */     double[] votes = new double[probs.length];
/*  459: 810 */     for (int i = 0; i < this.m_Classifiers.length; i++)
/*  460:     */     {
/*  461: 811 */       probs = getClassifier(i).distributionForInstance(instance);
/*  462: 812 */       int maxIndex = 0;
/*  463: 813 */       for (int j = 0; j < probs.length; j++) {
/*  464: 814 */         if (probs[j] > probs[maxIndex]) {
/*  465: 815 */           maxIndex = j;
/*  466:     */         }
/*  467:     */       }
/*  468: 821 */       if (probs[maxIndex] > 0.0D) {
/*  469: 822 */         for (int j = 0; j < probs.length; j++) {
/*  470: 823 */           if (probs[j] == probs[maxIndex]) {
/*  471: 824 */             votes[j] += 1.0D;
/*  472:     */           }
/*  473:     */         }
/*  474:     */       }
/*  475:     */     }
/*  476: 830 */     for (int i = 0; i < this.m_preBuiltClassifiers.size(); i++)
/*  477:     */     {
/*  478: 831 */       probs = ((Classifier)this.m_preBuiltClassifiers.get(i)).distributionForInstance(instance);
/*  479: 832 */       int maxIndex = 0;
/*  480: 834 */       for (int j = 0; j < probs.length; j++) {
/*  481: 835 */         if (probs[j] > probs[maxIndex]) {
/*  482: 836 */           maxIndex = j;
/*  483:     */         }
/*  484:     */       }
/*  485: 842 */       if (probs[maxIndex] > 0.0D) {
/*  486: 843 */         for (int j = 0; j < probs.length; j++) {
/*  487: 844 */           if (probs[j] == probs[maxIndex]) {
/*  488: 845 */             votes[j] += 1.0D;
/*  489:     */           }
/*  490:     */         }
/*  491:     */       }
/*  492:     */     }
/*  493: 851 */     int tmpMajorityIndex = 0;
/*  494: 852 */     for (int k = 1; k < votes.length; k++) {
/*  495: 853 */       if (votes[k] > votes[tmpMajorityIndex]) {
/*  496: 854 */         tmpMajorityIndex = k;
/*  497:     */       }
/*  498:     */     }
/*  499: 859 */     if (votes[tmpMajorityIndex] == 0.0D) {
/*  500: 860 */       return new double[instance.numClasses()];
/*  501:     */     }
/*  502: 864 */     Vector<Integer> majorityIndexes = new Vector();
/*  503: 865 */     for (int k = 0; k < votes.length; k++) {
/*  504: 866 */       if (votes[k] == votes[tmpMajorityIndex]) {
/*  505: 867 */         majorityIndexes.add(Integer.valueOf(k));
/*  506:     */       }
/*  507:     */     }
/*  508: 870 */     int majorityIndex = tmpMajorityIndex;
/*  509: 871 */     if (majorityIndexes.size() > 1)
/*  510:     */     {
/*  511: 873 */       double[] distPreds = distributionForInstanceAverage(instance);
/*  512: 874 */       majorityIndex = Utils.maxIndex(distPreds);
/*  513:     */     }
/*  514: 880 */     probs = new double[probs.length];
/*  515:     */     
/*  516: 882 */     probs[majorityIndex] = 1.0D;
/*  517:     */     
/*  518:     */ 
/*  519: 885 */     return probs;
/*  520:     */   }
/*  521:     */   
/*  522:     */   protected double[] distributionForInstanceMax(Instance instance)
/*  523:     */     throws Exception
/*  524:     */   {
/*  525: 898 */     double[] probs = new double[instance.numClasses()];
/*  526:     */     
/*  527: 900 */     double numPredictions = 0.0D;
/*  528: 901 */     for (int i = 0; i < this.m_Classifiers.length; i++)
/*  529:     */     {
/*  530: 902 */       double[] dist = getClassifier(i).distributionForInstance(instance);
/*  531: 903 */       if ((!instance.classAttribute().isNumeric()) || (!Utils.isMissingValue(dist[0])))
/*  532:     */       {
/*  533: 905 */         for (int j = 0; j < dist.length; j++) {
/*  534: 906 */           if ((probs[j] < dist[j]) || (numPredictions == 0.0D)) {
/*  535: 907 */             probs[j] = dist[j];
/*  536:     */           }
/*  537:     */         }
/*  538: 910 */         numPredictions += 1.0D;
/*  539:     */       }
/*  540:     */     }
/*  541: 914 */     for (int i = 0; i < this.m_preBuiltClassifiers.size(); i++)
/*  542:     */     {
/*  543: 915 */       double[] dist = ((Classifier)this.m_preBuiltClassifiers.get(i)).distributionForInstance(instance);
/*  544: 917 */       if ((!instance.classAttribute().isNumeric()) || (!Utils.isMissingValue(dist[0])))
/*  545:     */       {
/*  546: 919 */         for (int j = 0; j < dist.length; j++) {
/*  547: 920 */           if ((probs[j] < dist[j]) || (numPredictions == 0.0D)) {
/*  548: 921 */             probs[j] = dist[j];
/*  549:     */           }
/*  550:     */         }
/*  551: 924 */         numPredictions += 1.0D;
/*  552:     */       }
/*  553:     */     }
/*  554: 928 */     if (instance.classAttribute().isNumeric())
/*  555:     */     {
/*  556: 929 */       if (numPredictions == 0.0D) {
/*  557: 930 */         probs[0] = Utils.missingValue();
/*  558:     */       }
/*  559:     */     }
/*  560: 935 */     else if (Utils.sum(probs) > 0.0D) {
/*  561: 936 */       Utils.normalize(probs);
/*  562:     */     }
/*  563: 940 */     return probs;
/*  564:     */   }
/*  565:     */   
/*  566:     */   protected double[] distributionForInstanceMin(Instance instance)
/*  567:     */     throws Exception
/*  568:     */   {
/*  569: 953 */     double[] probs = new double[instance.numClasses()];
/*  570:     */     
/*  571: 955 */     double numPredictions = 0.0D;
/*  572: 956 */     for (int i = 0; i < this.m_Classifiers.length; i++)
/*  573:     */     {
/*  574: 957 */       double[] dist = getClassifier(i).distributionForInstance(instance);
/*  575: 958 */       if ((!instance.classAttribute().isNumeric()) || (!Utils.isMissingValue(dist[0])))
/*  576:     */       {
/*  577: 960 */         for (int j = 0; j < dist.length; j++) {
/*  578: 961 */           if ((probs[j] > dist[j]) || (numPredictions == 0.0D)) {
/*  579: 962 */             probs[j] = dist[j];
/*  580:     */           }
/*  581:     */         }
/*  582: 965 */         numPredictions += 1.0D;
/*  583:     */       }
/*  584:     */     }
/*  585: 969 */     for (int i = 0; i < this.m_preBuiltClassifiers.size(); i++)
/*  586:     */     {
/*  587: 970 */       double[] dist = ((Classifier)this.m_preBuiltClassifiers.get(i)).distributionForInstance(instance);
/*  588: 972 */       if ((!instance.classAttribute().isNumeric()) || (!Utils.isMissingValue(dist[0])))
/*  589:     */       {
/*  590: 974 */         for (int j = 0; j < dist.length; j++) {
/*  591: 975 */           if ((probs[j] > dist[j]) || (numPredictions == 0.0D)) {
/*  592: 976 */             probs[j] = dist[j];
/*  593:     */           }
/*  594:     */         }
/*  595: 979 */         numPredictions += 1.0D;
/*  596:     */       }
/*  597:     */     }
/*  598: 983 */     if (instance.classAttribute().isNumeric())
/*  599:     */     {
/*  600: 984 */       if (numPredictions == 0.0D) {
/*  601: 985 */         probs[0] = Utils.missingValue();
/*  602:     */       }
/*  603:     */     }
/*  604: 990 */     else if (Utils.sum(probs) > 0.0D) {
/*  605: 991 */       Utils.normalize(probs);
/*  606:     */     }
/*  607: 995 */     return probs;
/*  608:     */   }
/*  609:     */   
/*  610:     */   public String combinationRuleTipText()
/*  611:     */   {
/*  612:1005 */     return "The combination rule used.";
/*  613:     */   }
/*  614:     */   
/*  615:     */   public SelectedTag getCombinationRule()
/*  616:     */   {
/*  617:1014 */     return new SelectedTag(this.m_CombinationRule, TAGS_RULES);
/*  618:     */   }
/*  619:     */   
/*  620:     */   public void setCombinationRule(SelectedTag newRule)
/*  621:     */   {
/*  622:1023 */     if (newRule.getTags() == TAGS_RULES) {
/*  623:1024 */       this.m_CombinationRule = newRule.getSelectedTag().getID();
/*  624:     */     }
/*  625:     */   }
/*  626:     */   
/*  627:     */   public String preBuiltClassifiersTipText()
/*  628:     */   {
/*  629:1035 */     return "The pre-built serialized classifiers to include. Multiple serialized classifiers can be included alongside those that are built from scratch when this classifier runs. Note that it does not make sense to include pre-built classifiers in a cross-validation since they are static and their models do not change from fold to fold.";
/*  630:     */   }
/*  631:     */   
/*  632:     */   public void setPreBuiltClassifiers(File[] preBuilt)
/*  633:     */   {
/*  634:1050 */     this.m_classifiersToLoad.clear();
/*  635:1051 */     if ((preBuilt != null) && (preBuilt.length > 0)) {
/*  636:1052 */       for (File element : preBuilt)
/*  637:     */       {
/*  638:1053 */         String path = element.toString();
/*  639:1054 */         this.m_classifiersToLoad.add(path);
/*  640:     */       }
/*  641:     */     }
/*  642:     */   }
/*  643:     */   
/*  644:     */   public File[] getPreBuiltClassifiers()
/*  645:     */   {
/*  646:1066 */     File[] result = new File[this.m_classifiersToLoad.size()];
/*  647:1068 */     for (int i = 0; i < this.m_classifiersToLoad.size(); i++) {
/*  648:1069 */       result[i] = new File((String)this.m_classifiersToLoad.get(i));
/*  649:     */     }
/*  650:1072 */     return result;
/*  651:     */   }
/*  652:     */   
/*  653:     */   public String doNotPrintModelsTipText()
/*  654:     */   {
/*  655:1082 */     return "Do not print the individual trees in the output";
/*  656:     */   }
/*  657:     */   
/*  658:     */   public void setDoNotPrintModels(boolean print)
/*  659:     */   {
/*  660:1091 */     this.m_dontPrintModels = print;
/*  661:     */   }
/*  662:     */   
/*  663:     */   public boolean getDoNotPrintModels()
/*  664:     */   {
/*  665:1100 */     return this.m_dontPrintModels;
/*  666:     */   }
/*  667:     */   
/*  668:     */   public String toString()
/*  669:     */   {
/*  670:1111 */     if (this.m_Classifiers == null) {
/*  671:1112 */       return "Vote: No model built yet.";
/*  672:     */     }
/*  673:1115 */     String result = "Vote combines";
/*  674:1116 */     result = result + " the probability distributions of these base learners:\n";
/*  675:1117 */     for (int i = 0; i < this.m_Classifiers.length; i++) {
/*  676:1118 */       result = result + '\t' + getClassifierSpec(i) + '\n';
/*  677:     */     }
/*  678:1121 */     for (Classifier c : this.m_preBuiltClassifiers) {
/*  679:1122 */       result = result + "\t" + c.getClass().getName() + Utils.joinOptions(((OptionHandler)c).getOptions()) + "\n";
/*  680:     */     }
/*  681:1127 */     result = result + "using the '";
/*  682:1129 */     switch (this.m_CombinationRule)
/*  683:     */     {
/*  684:     */     case 1: 
/*  685:1131 */       result = result + "Average";
/*  686:1132 */       break;
/*  687:     */     case 2: 
/*  688:1135 */       result = result + "Product";
/*  689:1136 */       break;
/*  690:     */     case 3: 
/*  691:1139 */       result = result + "Majority Voting";
/*  692:1140 */       break;
/*  693:     */     case 4: 
/*  694:1143 */       result = result + "Minimum";
/*  695:1144 */       break;
/*  696:     */     case 5: 
/*  697:1147 */       result = result + "Maximum";
/*  698:1148 */       break;
/*  699:     */     case 6: 
/*  700:1151 */       result = result + "Median";
/*  701:1152 */       break;
/*  702:     */     default: 
/*  703:1155 */       throw new IllegalStateException("Unknown combination rule '" + this.m_CombinationRule + "'!");
/*  704:     */     }
/*  705:1159 */     result = result + "' combination rule \n";
/*  706:     */     
/*  707:1161 */     StringBuilder resultBuilder = null;
/*  708:1162 */     if (!this.m_dontPrintModels)
/*  709:     */     {
/*  710:1163 */       resultBuilder = new StringBuilder();
/*  711:1164 */       resultBuilder.append(result).append("\nAll the models:\n\n");
/*  712:1165 */       for (Classifier c : this.m_Classifiers) {
/*  713:1166 */         resultBuilder.append(c).append("\n");
/*  714:     */       }
/*  715:1169 */       for (Classifier c : this.m_preBuiltClassifiers) {
/*  716:1170 */         resultBuilder.append(c).append("\n");
/*  717:     */       }
/*  718:     */     }
/*  719:1174 */     return resultBuilder == null ? result : resultBuilder.toString();
/*  720:     */   }
/*  721:     */   
/*  722:     */   public String getRevision()
/*  723:     */   {
/*  724:1184 */     return RevisionUtils.extract("$Revision: 12424 $");
/*  725:     */   }
/*  726:     */   
/*  727:     */   public void setEnvironment(Environment env)
/*  728:     */   {
/*  729:1195 */     this.m_env = env;
/*  730:     */   }
/*  731:     */   
/*  732:     */   public Classifier aggregate(Classifier toAggregate)
/*  733:     */     throws Exception
/*  734:     */   {
/*  735:1209 */     if ((this.m_structure == null) && (this.m_Classifiers.length == 1) && ((this.m_Classifiers[0] instanceof ZeroR))) {
/*  736:1212 */       setClassifiers(new Classifier[0]);
/*  737:     */     }
/*  738:1216 */     addPreBuiltClassifier(toAggregate);
/*  739:     */     
/*  740:1218 */     return this;
/*  741:     */   }
/*  742:     */   
/*  743:     */   public void finalizeAggregation()
/*  744:     */     throws Exception
/*  745:     */   {}
/*  746:     */   
/*  747:     */   public static void main(String[] argv)
/*  748:     */   {
/*  749:1239 */     runClassifier(new Vote(), argv);
/*  750:     */   }
/*  751:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.Vote
 * JD-Core Version:    0.7.0.1
 */