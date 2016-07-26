/*    1:     */ package weka.classifiers;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.util.ArrayList;
/*    5:     */ import java.util.Collections;
/*    6:     */ import java.util.Enumeration;
/*    7:     */ import java.util.Random;
/*    8:     */ import java.util.Vector;
/*    9:     */ import weka.classifiers.rules.ZeroR;
/*   10:     */ import weka.core.CheckScheme;
/*   11:     */ import weka.core.Instance;
/*   12:     */ import weka.core.Instances;
/*   13:     */ import weka.core.MultiInstanceCapabilitiesHandler;
/*   14:     */ import weka.core.Option;
/*   15:     */ import weka.core.OptionHandler;
/*   16:     */ import weka.core.RevisionUtils;
/*   17:     */ import weka.core.SerializationHelper;
/*   18:     */ import weka.core.TestInstances;
/*   19:     */ import weka.core.Utils;
/*   20:     */ import weka.core.WeightedInstancesHandler;
/*   21:     */ 
/*   22:     */ public class CheckClassifier
/*   23:     */   extends CheckScheme
/*   24:     */ {
/*   25: 210 */   protected Classifier m_Classifier = new ZeroR();
/*   26:     */   
/*   27:     */   public Enumeration<Option> listOptions()
/*   28:     */   {
/*   29: 219 */     Vector<Option> result = new Vector();
/*   30:     */     
/*   31: 221 */     result.addAll(Collections.list(super.listOptions()));
/*   32:     */     
/*   33: 223 */     result.add(new Option("\tFull name of the classifier analysed.\n\teg: weka.classifiers.bayes.NaiveBayes\n\t(default weka.classifiers.rules.ZeroR)", "W", 1, "-W"));
/*   34: 227 */     if ((this.m_Classifier != null) && ((this.m_Classifier instanceof OptionHandler)))
/*   35:     */     {
/*   36: 228 */       result.add(new Option("", "", 0, "\nOptions specific to classifier " + this.m_Classifier.getClass().getName() + ":"));
/*   37:     */       
/*   38: 230 */       result.addAll(Collections.list(((OptionHandler)this.m_Classifier).listOptions()));
/*   39:     */     }
/*   40: 234 */     return result.elements();
/*   41:     */   }
/*   42:     */   
/*   43:     */   public void setOptions(String[] options)
/*   44:     */     throws Exception
/*   45:     */   {
/*   46: 329 */     super.setOptions(options);
/*   47:     */     
/*   48: 331 */     String tmpStr = Utils.getOption('W', options);
/*   49: 332 */     if (tmpStr.length() == 0) {
/*   50: 333 */       tmpStr = ZeroR.class.getName();
/*   51:     */     }
/*   52: 335 */     setClassifier((Classifier)forName("weka.classifiers", Classifier.class, tmpStr, Utils.partitionOptions(options)));
/*   53:     */   }
/*   54:     */   
/*   55:     */   public String[] getOptions()
/*   56:     */   {
/*   57: 349 */     Vector<String> result = new Vector();
/*   58:     */     
/*   59: 351 */     Collections.addAll(result, super.getOptions());
/*   60: 353 */     if (getClassifier() != null)
/*   61:     */     {
/*   62: 354 */       result.add("-W");
/*   63: 355 */       result.add(getClassifier().getClass().getName());
/*   64:     */     }
/*   65: 358 */     if ((this.m_Classifier != null) && ((this.m_Classifier instanceof OptionHandler)))
/*   66:     */     {
/*   67: 360 */       String[] options = ((OptionHandler)this.m_Classifier).getOptions();
/*   68: 361 */       if (options.length > 0)
/*   69:     */       {
/*   70: 362 */         result.add("--");
/*   71: 363 */         Collections.addAll(result, options);
/*   72:     */       }
/*   73:     */     }
/*   74: 367 */     return (String[])result.toArray(new String[result.size()]);
/*   75:     */   }
/*   76:     */   
/*   77:     */   public void doTests()
/*   78:     */   {
/*   79: 376 */     if (getClassifier() == null)
/*   80:     */     {
/*   81: 377 */       println("\n=== No classifier set ===");
/*   82: 378 */       return;
/*   83:     */     }
/*   84: 380 */     println("\n=== Check on Classifier: " + getClassifier().getClass().getName() + " ===\n");
/*   85:     */     
/*   86:     */ 
/*   87:     */ 
/*   88: 384 */     this.m_ClasspathProblems = false;
/*   89: 385 */     println("--> Checking for interfaces");
/*   90: 386 */     canTakeOptions();
/*   91: 387 */     boolean updateableClassifier = updateableClassifier()[0];
/*   92: 388 */     boolean weightedInstancesHandler = weightedInstancesHandler()[0];
/*   93: 389 */     boolean multiInstanceHandler = multiInstanceHandler()[0];
/*   94: 390 */     println("--> Classifier tests");
/*   95: 391 */     declaresSerialVersionUID();
/*   96: 392 */     testToString();
/*   97: 393 */     testsPerClassType(1, updateableClassifier, weightedInstancesHandler, multiInstanceHandler);
/*   98:     */     
/*   99: 395 */     testsPerClassType(0, updateableClassifier, weightedInstancesHandler, multiInstanceHandler);
/*  100:     */     
/*  101: 397 */     testsPerClassType(3, updateableClassifier, weightedInstancesHandler, multiInstanceHandler);
/*  102:     */     
/*  103: 399 */     testsPerClassType(2, updateableClassifier, weightedInstancesHandler, multiInstanceHandler);
/*  104:     */     
/*  105: 401 */     testsPerClassType(4, updateableClassifier, weightedInstancesHandler, multiInstanceHandler);
/*  106:     */   }
/*  107:     */   
/*  108:     */   public void setClassifier(Classifier newClassifier)
/*  109:     */   {
/*  110: 411 */     this.m_Classifier = newClassifier;
/*  111:     */   }
/*  112:     */   
/*  113:     */   public Classifier getClassifier()
/*  114:     */   {
/*  115: 420 */     return this.m_Classifier;
/*  116:     */   }
/*  117:     */   
/*  118:     */   protected void testsPerClassType(int classType, boolean updateable, boolean weighted, boolean multiInstance)
/*  119:     */   {
/*  120: 434 */     boolean PNom = canPredict(true, false, false, false, false, multiInstance, classType)[0];
/*  121:     */     
/*  122: 436 */     boolean PNum = canPredict(false, true, false, false, false, multiInstance, classType)[0];
/*  123:     */     
/*  124: 438 */     boolean PStr = canPredict(false, false, true, false, false, multiInstance, classType)[0];
/*  125:     */     
/*  126: 440 */     boolean PDat = canPredict(false, false, false, true, false, multiInstance, classType)[0];
/*  127:     */     boolean PRel;
/*  128:     */     boolean PRel;
/*  129: 443 */     if (!multiInstance) {
/*  130: 444 */       PRel = canPredict(false, false, false, false, true, multiInstance, classType)[0];
/*  131:     */     } else {
/*  132: 447 */       PRel = false;
/*  133:     */     }
/*  134: 450 */     if ((PNom) || (PNum) || (PStr) || (PDat) || (PRel))
/*  135:     */     {
/*  136: 451 */       if (weighted) {
/*  137: 452 */         instanceWeights(PNom, PNum, PStr, PDat, PRel, multiInstance, classType);
/*  138:     */       }
/*  139: 455 */       canHandleOnlyClass(PNom, PNum, PStr, PDat, PRel, classType);
/*  140: 457 */       if (classType == 1) {
/*  141: 458 */         canHandleNClasses(PNom, PNum, PStr, PDat, PRel, multiInstance, 4);
/*  142:     */       }
/*  143: 461 */       if (!multiInstance)
/*  144:     */       {
/*  145: 462 */         canHandleClassAsNthAttribute(PNom, PNum, PStr, PDat, PRel, multiInstance, classType, 0);
/*  146:     */         
/*  147: 464 */         canHandleClassAsNthAttribute(PNom, PNum, PStr, PDat, PRel, multiInstance, classType, 1);
/*  148:     */       }
/*  149: 468 */       canHandleZeroTraining(PNom, PNum, PStr, PDat, PRel, multiInstance, classType);
/*  150:     */       
/*  151: 470 */       boolean handleMissingPredictors = canHandleMissing(PNom, PNum, PStr, PDat, PRel, multiInstance, classType, true, false, 20)[0];
/*  152: 472 */       if (handleMissingPredictors) {
/*  153: 473 */         canHandleMissing(PNom, PNum, PStr, PDat, PRel, multiInstance, classType, true, false, 100);
/*  154:     */       }
/*  155: 477 */       boolean handleMissingClass = canHandleMissing(PNom, PNum, PStr, PDat, PRel, multiInstance, classType, false, true, 20)[0];
/*  156: 479 */       if (handleMissingClass) {
/*  157: 480 */         canHandleMissing(PNom, PNum, PStr, PDat, PRel, multiInstance, classType, false, true, 100);
/*  158:     */       }
/*  159: 484 */       correctBuildInitialisation(PNom, PNum, PStr, PDat, PRel, multiInstance, classType);
/*  160:     */       
/*  161: 486 */       datasetIntegrity(PNom, PNum, PStr, PDat, PRel, multiInstance, classType, handleMissingPredictors, handleMissingClass);
/*  162:     */       
/*  163: 488 */       doesntUseTestClassVal(PNom, PNum, PStr, PDat, PRel, multiInstance, classType);
/*  164: 490 */       if (updateable) {
/*  165: 491 */         updatingEquality(PNom, PNum, PStr, PDat, PRel, multiInstance, classType);
/*  166:     */       }
/*  167:     */     }
/*  168:     */   }
/*  169:     */   
/*  170:     */   protected boolean[] testToString()
/*  171:     */   {
/*  172: 503 */     boolean[] result = new boolean[2];
/*  173:     */     
/*  174: 505 */     print("toString...");
/*  175:     */     try
/*  176:     */     {
/*  177: 508 */       Classifier copy = (Classifier)this.m_Classifier.getClass().newInstance();
/*  178: 509 */       copy.toString();
/*  179: 510 */       result[0] = true;
/*  180: 511 */       println("yes");
/*  181:     */     }
/*  182:     */     catch (Exception e)
/*  183:     */     {
/*  184: 513 */       result[0] = false;
/*  185: 514 */       println("no");
/*  186: 515 */       if (this.m_Debug)
/*  187:     */       {
/*  188: 516 */         println("\n=== Full report ===");
/*  189: 517 */         e.printStackTrace();
/*  190: 518 */         println("\n");
/*  191:     */       }
/*  192:     */     }
/*  193: 522 */     return result;
/*  194:     */   }
/*  195:     */   
/*  196:     */   protected boolean[] declaresSerialVersionUID()
/*  197:     */   {
/*  198: 532 */     boolean[] result = new boolean[2];
/*  199:     */     
/*  200: 534 */     print("serialVersionUID...");
/*  201:     */     
/*  202: 536 */     result[0] = (!SerializationHelper.needsUID(this.m_Classifier.getClass()) ? 1 : false);
/*  203: 538 */     if (result[0] != 0) {
/*  204: 539 */       println("yes");
/*  205:     */     } else {
/*  206: 541 */       println("no");
/*  207:     */     }
/*  208: 544 */     return result;
/*  209:     */   }
/*  210:     */   
/*  211:     */   protected boolean[] canTakeOptions()
/*  212:     */   {
/*  213: 554 */     boolean[] result = new boolean[2];
/*  214:     */     
/*  215: 556 */     print("options...");
/*  216: 557 */     if ((this.m_Classifier instanceof OptionHandler))
/*  217:     */     {
/*  218: 558 */       println("yes");
/*  219: 559 */       if (this.m_Debug)
/*  220:     */       {
/*  221: 560 */         println("\n=== Full report ===");
/*  222: 561 */         Enumeration<Option> enu = ((OptionHandler)this.m_Classifier).listOptions();
/*  223: 562 */         while (enu.hasMoreElements())
/*  224:     */         {
/*  225: 563 */           Option option = (Option)enu.nextElement();
/*  226: 564 */           print(option.synopsis() + "\n" + option.description() + "\n");
/*  227:     */         }
/*  228: 566 */         println("\n");
/*  229:     */       }
/*  230: 568 */       result[0] = true;
/*  231:     */     }
/*  232:     */     else
/*  233:     */     {
/*  234: 570 */       println("no");
/*  235: 571 */       result[0] = false;
/*  236:     */     }
/*  237: 574 */     return result;
/*  238:     */   }
/*  239:     */   
/*  240:     */   protected boolean[] updateableClassifier()
/*  241:     */   {
/*  242: 584 */     boolean[] result = new boolean[2];
/*  243:     */     
/*  244: 586 */     print("updateable classifier...");
/*  245: 587 */     if ((this.m_Classifier instanceof UpdateableClassifier))
/*  246:     */     {
/*  247: 588 */       println("yes");
/*  248: 589 */       result[0] = true;
/*  249:     */     }
/*  250:     */     else
/*  251:     */     {
/*  252: 591 */       println("no");
/*  253: 592 */       result[0] = false;
/*  254:     */     }
/*  255: 595 */     return result;
/*  256:     */   }
/*  257:     */   
/*  258:     */   protected boolean[] weightedInstancesHandler()
/*  259:     */   {
/*  260: 605 */     boolean[] result = new boolean[2];
/*  261:     */     
/*  262: 607 */     print("weighted instances classifier...");
/*  263: 608 */     if ((this.m_Classifier instanceof WeightedInstancesHandler))
/*  264:     */     {
/*  265: 609 */       println("yes");
/*  266: 610 */       result[0] = true;
/*  267:     */     }
/*  268:     */     else
/*  269:     */     {
/*  270: 612 */       println("no");
/*  271: 613 */       result[0] = false;
/*  272:     */     }
/*  273: 616 */     return result;
/*  274:     */   }
/*  275:     */   
/*  276:     */   protected boolean[] multiInstanceHandler()
/*  277:     */   {
/*  278: 625 */     boolean[] result = new boolean[2];
/*  279:     */     
/*  280: 627 */     print("multi-instance classifier...");
/*  281: 628 */     if ((this.m_Classifier instanceof MultiInstanceCapabilitiesHandler))
/*  282:     */     {
/*  283: 629 */       println("yes");
/*  284: 630 */       result[0] = true;
/*  285:     */     }
/*  286:     */     else
/*  287:     */     {
/*  288: 632 */       println("no");
/*  289: 633 */       result[0] = false;
/*  290:     */     }
/*  291: 636 */     return result;
/*  292:     */   }
/*  293:     */   
/*  294:     */   protected boolean[] canPredict(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType)
/*  295:     */   {
/*  296: 656 */     print("basic predict");
/*  297: 657 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType);
/*  298:     */     
/*  299: 659 */     print("...");
/*  300: 660 */     ArrayList<String> accepts = new ArrayList();
/*  301: 661 */     accepts.add("unary");
/*  302: 662 */     accepts.add("binary");
/*  303: 663 */     accepts.add("nominal");
/*  304: 664 */     accepts.add("numeric");
/*  305: 665 */     accepts.add("string");
/*  306: 666 */     accepts.add("date");
/*  307: 667 */     accepts.add("relational");
/*  308: 668 */     accepts.add("multi-instance");
/*  309: 669 */     accepts.add("not in classpath");
/*  310: 670 */     int numTrain = getNumInstances();int numTest = getNumInstances();int numClasses = 2;
/*  311: 671 */     int missingLevel = 0;
/*  312: 672 */     boolean predictorMissing = false;boolean classMissing = false;
/*  313:     */     
/*  314: 674 */     return runBasicTest(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType, missingLevel, predictorMissing, classMissing, numTrain, numTest, numClasses, accepts);
/*  315:     */   }
/*  316:     */   
/*  317:     */   protected boolean[] canHandleOnlyClass(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, int classType)
/*  318:     */   {
/*  319: 698 */     print("only class in data");
/*  320: 699 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, false, classType);
/*  321:     */     
/*  322: 701 */     print("...");
/*  323: 702 */     ArrayList<String> accepts = new ArrayList();
/*  324: 703 */     accepts.add("class");
/*  325: 704 */     accepts.add("zeror");
/*  326: 705 */     int numTrain = getNumInstances();int numTest = getNumInstances();int missingLevel = 0;
/*  327:     */     
/*  328: 707 */     boolean predictorMissing = false;boolean classMissing = false;
/*  329:     */     
/*  330: 709 */     return runBasicTest(false, false, false, false, false, false, classType, missingLevel, predictorMissing, classMissing, numTrain, numTest, 2, accepts);
/*  331:     */   }
/*  332:     */   
/*  333:     */   protected boolean[] canHandleNClasses(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int numClasses)
/*  334:     */   {
/*  335: 733 */     print("more than two class problems");
/*  336: 734 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, 1);
/*  337:     */     
/*  338: 736 */     print("...");
/*  339: 737 */     ArrayList<String> accepts = new ArrayList();
/*  340: 738 */     accepts.add("number");
/*  341: 739 */     accepts.add("class");
/*  342: 740 */     int numTrain = getNumInstances();int numTest = getNumInstances();int missingLevel = 0;
/*  343:     */     
/*  344: 742 */     boolean predictorMissing = false;boolean classMissing = false;
/*  345:     */     
/*  346: 744 */     return runBasicTest(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, 1, missingLevel, predictorMissing, classMissing, numTrain, numTest, numClasses, accepts);
/*  347:     */   }
/*  348:     */   
/*  349:     */   protected boolean[] canHandleClassAsNthAttribute(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType, int classIndex)
/*  350:     */   {
/*  351: 771 */     if (classIndex == -1) {
/*  352: 772 */       print("class attribute as last attribute");
/*  353:     */     } else {
/*  354: 774 */       print("class attribute as " + (classIndex + 1) + ". attribute");
/*  355:     */     }
/*  356: 776 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType);
/*  357:     */     
/*  358: 778 */     print("...");
/*  359: 779 */     ArrayList<String> accepts = new ArrayList();
/*  360: 780 */     int numTrain = getNumInstances();int numTest = getNumInstances();int numClasses = 2;
/*  361: 781 */     int missingLevel = 0;
/*  362: 782 */     boolean predictorMissing = false;boolean classMissing = false;
/*  363:     */     
/*  364: 784 */     return runBasicTest(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType, classIndex, missingLevel, predictorMissing, classMissing, numTrain, numTest, numClasses, accepts);
/*  365:     */   }
/*  366:     */   
/*  367:     */   protected boolean[] canHandleZeroTraining(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType)
/*  368:     */   {
/*  369: 807 */     print("handle zero training instances");
/*  370: 808 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType);
/*  371:     */     
/*  372: 810 */     print("...");
/*  373: 811 */     ArrayList<String> accepts = new ArrayList();
/*  374: 812 */     accepts.add("train");
/*  375: 813 */     accepts.add("value");
/*  376: 814 */     int numTrain = 0;int numTest = getNumInstances();int numClasses = 2;int missingLevel = 0;
/*  377:     */     
/*  378: 816 */     boolean predictorMissing = false;boolean classMissing = false;
/*  379:     */     
/*  380: 818 */     return runBasicTest(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType, missingLevel, predictorMissing, classMissing, numTrain, numTest, numClasses, accepts);
/*  381:     */   }
/*  382:     */   
/*  383:     */   protected boolean[] correctBuildInitialisation(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType)
/*  384:     */   {
/*  385: 848 */     boolean[] result = new boolean[2];
/*  386:     */     
/*  387: 850 */     print("correct initialisation during buildClassifier");
/*  388: 851 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType);
/*  389:     */     
/*  390: 853 */     print("...");
/*  391: 854 */     int numTrain = getNumInstances();int numTest = getNumInstances();int numClasses = 2;
/*  392: 855 */     int missingLevel = 0;
/*  393: 856 */     boolean predictorMissing = false;boolean classMissing = false;
/*  394:     */     
/*  395: 858 */     Instances train1 = null;
/*  396: 859 */     Instances test1 = null;
/*  397: 860 */     Instances train2 = null;
/*  398: 861 */     Instances test2 = null;
/*  399: 862 */     Classifier classifier = null;
/*  400: 863 */     Evaluation evaluation1A = null;
/*  401: 864 */     Evaluation evaluation1B = null;
/*  402: 865 */     Evaluation evaluation2 = null;
/*  403: 866 */     boolean built = false;
/*  404: 867 */     int stage = 0;
/*  405:     */     try
/*  406:     */     {
/*  407: 872 */       train1 = makeTestDataset(42, numTrain, nominalPredictor ? getNumNominal() : 0, numericPredictor ? getNumNumeric() : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, numClasses, classType, multiInstance);
/*  408:     */       
/*  409:     */ 
/*  410:     */ 
/*  411:     */ 
/*  412: 877 */       train2 = makeTestDataset(84, numTrain, nominalPredictor ? getNumNominal() + 1 : 0, numericPredictor ? getNumNumeric() + 1 : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, numClasses, classType, multiInstance);
/*  413:     */       
/*  414:     */ 
/*  415:     */ 
/*  416:     */ 
/*  417:     */ 
/*  418: 883 */       test1 = makeTestDataset(24, numTest, nominalPredictor ? getNumNominal() : 0, numericPredictor ? getNumNumeric() : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, numClasses, classType, multiInstance);
/*  419:     */       
/*  420:     */ 
/*  421:     */ 
/*  422:     */ 
/*  423: 888 */       test2 = makeTestDataset(48, numTest, nominalPredictor ? getNumNominal() + 1 : 0, numericPredictor ? getNumNumeric() + 1 : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, numClasses, classType, multiInstance);
/*  424: 894 */       if (missingLevel > 0)
/*  425:     */       {
/*  426: 895 */         addMissing(train1, missingLevel, predictorMissing, classMissing);
/*  427: 896 */         addMissing(test1, Math.min(missingLevel, 50), predictorMissing, classMissing);
/*  428:     */         
/*  429: 898 */         addMissing(train2, missingLevel, predictorMissing, classMissing);
/*  430: 899 */         addMissing(test2, Math.min(missingLevel, 50), predictorMissing, classMissing);
/*  431:     */       }
/*  432: 903 */       classifier = AbstractClassifier.makeCopies(getClassifier(), 1)[0];
/*  433: 904 */       evaluation1A = new Evaluation(train1);
/*  434: 905 */       evaluation1B = new Evaluation(train1);
/*  435: 906 */       evaluation2 = new Evaluation(train2);
/*  436:     */     }
/*  437:     */     catch (Exception ex)
/*  438:     */     {
/*  439: 908 */       throw new Error("Error setting up for tests: " + ex.getMessage());
/*  440:     */     }
/*  441:     */     try
/*  442:     */     {
/*  443: 911 */       stage = 0;
/*  444: 912 */       classifier.buildClassifier(train1);
/*  445: 913 */       built = true;
/*  446: 914 */       if (testWRTZeroR(classifier, evaluation1A, train1, test1)[0] == 0) {
/*  447: 915 */         throw new Exception("Scheme performs worse than ZeroR");
/*  448:     */       }
/*  449: 918 */       stage = 1;
/*  450: 919 */       built = false;
/*  451: 920 */       classifier.buildClassifier(train2);
/*  452: 921 */       built = true;
/*  453: 922 */       if (testWRTZeroR(classifier, evaluation2, train2, test2)[0] == 0) {
/*  454: 923 */         throw new Exception("Scheme performs worse than ZeroR");
/*  455:     */       }
/*  456: 926 */       stage = 2;
/*  457: 927 */       built = false;
/*  458: 928 */       classifier.buildClassifier(train1);
/*  459: 929 */       built = true;
/*  460: 930 */       if (testWRTZeroR(classifier, evaluation1B, train1, test1)[0] == 0) {
/*  461: 931 */         throw new Exception("Scheme performs worse than ZeroR");
/*  462:     */       }
/*  463: 934 */       stage = 3;
/*  464: 935 */       if (!evaluation1A.equals(evaluation1B))
/*  465:     */       {
/*  466: 936 */         if (this.m_Debug)
/*  467:     */         {
/*  468: 937 */           println("\n=== Full report ===\n" + evaluation1A.toSummaryString("\nFirst buildClassifier()", true) + "\n\n");
/*  469:     */           
/*  470:     */ 
/*  471: 940 */           println(evaluation1B.toSummaryString("\nSecond buildClassifier()", true) + "\n\n");
/*  472:     */         }
/*  473: 943 */         throw new Exception("Results differ between buildClassifier calls");
/*  474:     */       }
/*  475: 945 */       println("yes");
/*  476: 946 */       result[0] = true;
/*  477:     */     }
/*  478:     */     catch (Exception ex)
/*  479:     */     {
/*  480: 949 */       String msg = ex.getMessage().toLowerCase();
/*  481: 950 */       if (msg.indexOf("worse than zeror") >= 0)
/*  482:     */       {
/*  483: 951 */         println("warning: performs worse than ZeroR");
/*  484:     */         
/*  485:     */ 
/*  486: 954 */         result[0] = true;
/*  487: 955 */         result[1] = true;
/*  488:     */       }
/*  489:     */       else
/*  490:     */       {
/*  491: 957 */         println("no");
/*  492: 958 */         result[0] = false;
/*  493:     */       }
/*  494: 960 */       if (this.m_Debug)
/*  495:     */       {
/*  496: 961 */         println("\n=== Full Report ===");
/*  497: 962 */         print("Problem during");
/*  498: 963 */         if (built) {
/*  499: 964 */           print(" testing");
/*  500:     */         } else {
/*  501: 966 */           print(" training");
/*  502:     */         }
/*  503: 968 */         switch (stage)
/*  504:     */         {
/*  505:     */         case 0: 
/*  506: 970 */           print(" of dataset 1");
/*  507: 971 */           break;
/*  508:     */         case 1: 
/*  509: 973 */           print(" of dataset 2");
/*  510: 974 */           break;
/*  511:     */         case 2: 
/*  512: 976 */           print(" of dataset 1 (2nd build)");
/*  513: 977 */           break;
/*  514:     */         case 3: 
/*  515: 979 */           print(", comparing results from builds of dataset 1");
/*  516:     */         }
/*  517: 982 */         println(": " + ex.getMessage() + "\n");
/*  518: 983 */         println("here are the datasets:\n");
/*  519: 984 */         println("=== Train1 Dataset ===\n" + train1.toString() + "\n");
/*  520: 985 */         println("=== Test1 Dataset ===\n" + test1.toString() + "\n\n");
/*  521: 986 */         println("=== Train2 Dataset ===\n" + train2.toString() + "\n");
/*  522: 987 */         println("=== Test2 Dataset ===\n" + test2.toString() + "\n\n");
/*  523:     */       }
/*  524:     */     }
/*  525: 991 */     return result;
/*  526:     */   }
/*  527:     */   
/*  528:     */   protected boolean[] canHandleMissing(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType, boolean predictorMissing, boolean classMissing, int missingLevel)
/*  529:     */   {
/*  530:1016 */     if (missingLevel == 100) {
/*  531:1017 */       print("100% ");
/*  532:     */     }
/*  533:1019 */     print("missing");
/*  534:1020 */     if (predictorMissing)
/*  535:     */     {
/*  536:1021 */       print(" predictor");
/*  537:1022 */       if (classMissing) {
/*  538:1023 */         print(" and");
/*  539:     */       }
/*  540:     */     }
/*  541:1026 */     if (classMissing) {
/*  542:1027 */       print(" class");
/*  543:     */     }
/*  544:1029 */     print(" values");
/*  545:1030 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType);
/*  546:     */     
/*  547:1032 */     print("...");
/*  548:1033 */     ArrayList<String> accepts = new ArrayList();
/*  549:1034 */     accepts.add("missing");
/*  550:1035 */     accepts.add("value");
/*  551:1036 */     accepts.add("train");
/*  552:1037 */     int numTrain = getNumInstances();int numTest = getNumInstances();int numClasses = 2;
/*  553:     */     
/*  554:     */ 
/*  555:1040 */     return runBasicTest(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType, missingLevel, predictorMissing, classMissing, numTrain, numTest, numClasses, accepts);
/*  556:     */   }
/*  557:     */   
/*  558:     */   protected boolean[] updatingEquality(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType)
/*  559:     */   {
/*  560:1066 */     print("incremental training produces the same results as batch training");
/*  561:     */     
/*  562:1068 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType);
/*  563:     */     
/*  564:1070 */     print("...");
/*  565:1071 */     int numTrain = getNumInstances();int numTest = getNumInstances();int numClasses = 2;
/*  566:1072 */     int missingLevel = 0;
/*  567:1073 */     boolean predictorMissing = false;boolean classMissing = false;
/*  568:     */     
/*  569:1075 */     boolean[] result = new boolean[2];
/*  570:1076 */     Instances train = null;
/*  571:1077 */     Instances test = null;
/*  572:1078 */     Classifier[] classifiers = null;
/*  573:1079 */     Evaluation evaluationB = null;
/*  574:1080 */     Evaluation evaluationI = null;
/*  575:1081 */     boolean built = false;
/*  576:     */     try
/*  577:     */     {
/*  578:1083 */       train = makeTestDataset(42, numTrain, nominalPredictor ? getNumNominal() : 0, numericPredictor ? getNumNumeric() : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, numClasses, classType, multiInstance);
/*  579:     */       
/*  580:     */ 
/*  581:     */ 
/*  582:     */ 
/*  583:1088 */       test = makeTestDataset(24, numTest, nominalPredictor ? getNumNominal() : 0, numericPredictor ? getNumNumeric() : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, numClasses, classType, multiInstance);
/*  584:1093 */       if (missingLevel > 0)
/*  585:     */       {
/*  586:1094 */         addMissing(train, missingLevel, predictorMissing, classMissing);
/*  587:1095 */         addMissing(test, Math.min(missingLevel, 50), predictorMissing, classMissing);
/*  588:     */       }
/*  589:1098 */       classifiers = AbstractClassifier.makeCopies(getClassifier(), 2);
/*  590:1099 */       evaluationB = new Evaluation(train);
/*  591:1100 */       evaluationI = new Evaluation(train);
/*  592:1101 */       classifiers[0].buildClassifier(train);
/*  593:1102 */       testWRTZeroR(classifiers[0], evaluationB, train, test);
/*  594:     */     }
/*  595:     */     catch (Exception ex)
/*  596:     */     {
/*  597:1104 */       throw new Error("Error setting up for tests: " + ex.getMessage());
/*  598:     */     }
/*  599:     */     try
/*  600:     */     {
/*  601:1107 */       classifiers[1].buildClassifier(new Instances(train, 0));
/*  602:1108 */       for (int i = 0; i < train.numInstances(); i++) {
/*  603:1109 */         ((UpdateableClassifier)classifiers[1]).updateClassifier(train.instance(i));
/*  604:     */       }
/*  605:1112 */       built = true;
/*  606:1113 */       testWRTZeroR(classifiers[1], evaluationI, train, test);
/*  607:1114 */       if (!evaluationB.equals(evaluationI))
/*  608:     */       {
/*  609:1115 */         println("no");
/*  610:1116 */         result[0] = false;
/*  611:1118 */         if (this.m_Debug)
/*  612:     */         {
/*  613:1119 */           println("\n=== Full Report ===");
/*  614:1120 */           println("Results differ between batch and incrementally built models.\nDepending on the classifier, this may be OK");
/*  615:     */           
/*  616:     */ 
/*  617:1123 */           println("Here are the results:\n");
/*  618:1124 */           println(evaluationB.toSummaryString("\nbatch built results\n", true));
/*  619:1125 */           println(evaluationI.toSummaryString("\nincrementally built results\n", true));
/*  620:     */           
/*  621:1127 */           println("Here are the datasets:\n");
/*  622:1128 */           println("=== Train Dataset ===\n" + train.toString() + "\n");
/*  623:1129 */           println("=== Test Dataset ===\n" + test.toString() + "\n\n");
/*  624:     */         }
/*  625:     */       }
/*  626:     */       else
/*  627:     */       {
/*  628:1132 */         println("yes");
/*  629:1133 */         result[0] = true;
/*  630:     */       }
/*  631:     */     }
/*  632:     */     catch (Exception ex)
/*  633:     */     {
/*  634:1136 */       result[0] = false;
/*  635:     */       
/*  636:1138 */       print("Problem during");
/*  637:1139 */       if (built) {
/*  638:1140 */         print(" testing");
/*  639:     */       } else {
/*  640:1142 */         print(" training");
/*  641:     */       }
/*  642:1144 */       println(": " + ex.getMessage() + "\n");
/*  643:     */     }
/*  644:1147 */     return result;
/*  645:     */   }
/*  646:     */   
/*  647:     */   protected boolean[] doesntUseTestClassVal(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType)
/*  648:     */   {
/*  649:1169 */     print("classifier ignores test instance class vals");
/*  650:1170 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType);
/*  651:     */     
/*  652:1172 */     print("...");
/*  653:1173 */     int numTrain = 2 * getNumInstances();int numTest = getNumInstances();int numClasses = 2;
/*  654:1174 */     int missingLevel = 0;
/*  655:1175 */     boolean predictorMissing = false;boolean classMissing = false;
/*  656:     */     
/*  657:1177 */     boolean[] result = new boolean[2];
/*  658:1178 */     Instances train = null;
/*  659:1179 */     Instances test = null;
/*  660:1180 */     Classifier[] classifiers = null;
/*  661:1181 */     boolean evalFail = false;
/*  662:     */     try
/*  663:     */     {
/*  664:1183 */       train = makeTestDataset(42, numTrain, nominalPredictor ? getNumNominal() + 1 : 0, numericPredictor ? getNumNumeric() + 1 : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, numClasses, classType, multiInstance);
/*  665:     */       
/*  666:     */ 
/*  667:     */ 
/*  668:     */ 
/*  669:     */ 
/*  670:1189 */       test = makeTestDataset(24, numTest, nominalPredictor ? getNumNominal() + 1 : 0, numericPredictor ? getNumNumeric() + 1 : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, numClasses, classType, multiInstance);
/*  671:1195 */       if (missingLevel > 0)
/*  672:     */       {
/*  673:1196 */         addMissing(train, missingLevel, predictorMissing, classMissing);
/*  674:1197 */         addMissing(test, Math.min(missingLevel, 50), predictorMissing, classMissing);
/*  675:     */       }
/*  676:1200 */       classifiers = AbstractClassifier.makeCopies(getClassifier(), 2);
/*  677:1201 */       classifiers[0].buildClassifier(train);
/*  678:1202 */       classifiers[1].buildClassifier(train);
/*  679:     */     }
/*  680:     */     catch (Exception ex)
/*  681:     */     {
/*  682:1204 */       throw new Error("Error setting up for tests: " + ex.getMessage());
/*  683:     */     }
/*  684:     */     try
/*  685:     */     {
/*  686:1209 */       for (int i = 0; i < test.numInstances(); i++)
/*  687:     */       {
/*  688:1210 */         Instance testInst = test.instance(i);
/*  689:1211 */         Instance classMissingInst = (Instance)testInst.copy();
/*  690:1212 */         classMissingInst.setDataset(test);
/*  691:1213 */         classMissingInst.setClassMissing();
/*  692:1214 */         double[] dist0 = classifiers[0].distributionForInstance(testInst);
/*  693:1215 */         double[] dist1 = classifiers[1].distributionForInstance(classMissingInst);
/*  694:1217 */         for (int j = 0; j < dist0.length; j++) {
/*  695:1219 */           if ((Double.isNaN(dist0[j])) && (Double.isNaN(dist1[j])))
/*  696:     */           {
/*  697:1220 */             if (getDebug()) {
/*  698:1221 */               System.out.println("Both predictions are NaN!");
/*  699:     */             }
/*  700:     */           }
/*  701:1226 */           else if (dist0[j] != dist1[j]) {
/*  702:1227 */             throw new Exception("Prediction different for instance " + (i + 1));
/*  703:     */           }
/*  704:     */         }
/*  705:     */       }
/*  706:1232 */       println("yes");
/*  707:1233 */       result[0] = true;
/*  708:     */     }
/*  709:     */     catch (Exception ex)
/*  710:     */     {
/*  711:1235 */       println("no");
/*  712:1236 */       result[0] = false;
/*  713:1238 */       if (this.m_Debug)
/*  714:     */       {
/*  715:1239 */         println("\n=== Full Report ===");
/*  716:1241 */         if (evalFail)
/*  717:     */         {
/*  718:1242 */           println("Results differ between non-missing and missing test class values.");
/*  719:     */         }
/*  720:     */         else
/*  721:     */         {
/*  722:1245 */           print("Problem during testing");
/*  723:1246 */           println(": " + ex.getMessage() + "\n");
/*  724:     */         }
/*  725:1248 */         println("Here are the datasets:\n");
/*  726:1249 */         println("=== Train Dataset ===\n" + train.toString() + "\n");
/*  727:1250 */         println("=== Train Weights ===\n");
/*  728:1251 */         for (int i = 0; i < train.numInstances(); i++) {
/*  729:1252 */           println(" " + (i + 1) + "    " + train.instance(i).weight());
/*  730:     */         }
/*  731:1254 */         println("=== Test Dataset ===\n" + test.toString() + "\n\n");
/*  732:1255 */         println("(test weights all 1.0\n");
/*  733:     */       }
/*  734:     */     }
/*  735:1259 */     return result;
/*  736:     */   }
/*  737:     */   
/*  738:     */   protected boolean[] instanceWeights(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType)
/*  739:     */   {
/*  740:1284 */     print("classifier uses instance weights");
/*  741:1285 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType);
/*  742:     */     
/*  743:1287 */     print("...");
/*  744:1288 */     int numTrain = 2 * getNumInstances();int numTest = getNumInstances();int numClasses = 2;
/*  745:1289 */     int missingLevel = 0;
/*  746:1290 */     boolean predictorMissing = false;boolean classMissing = false;
/*  747:     */     
/*  748:1292 */     boolean[] result = new boolean[2];
/*  749:1293 */     Instances train = null;
/*  750:1294 */     Instances test = null;
/*  751:1295 */     Classifier[] classifiers = null;
/*  752:1296 */     Evaluation evaluationB = null;
/*  753:1297 */     Evaluation evaluationI = null;
/*  754:1298 */     boolean built = false;
/*  755:1299 */     boolean evalFail = false;
/*  756:     */     try
/*  757:     */     {
/*  758:1301 */       train = makeTestDataset(42, numTrain, nominalPredictor ? getNumNominal() + 1 : 0, numericPredictor ? getNumNumeric() + 1 : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, numClasses, classType, multiInstance);
/*  759:     */       
/*  760:     */ 
/*  761:     */ 
/*  762:     */ 
/*  763:     */ 
/*  764:1307 */       test = makeTestDataset(24, numTest, nominalPredictor ? getNumNominal() + 1 : 0, numericPredictor ? getNumNumeric() + 1 : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, numClasses, classType, multiInstance);
/*  765:1313 */       if (missingLevel > 0)
/*  766:     */       {
/*  767:1314 */         addMissing(train, missingLevel, predictorMissing, classMissing);
/*  768:1315 */         addMissing(test, Math.min(missingLevel, 50), predictorMissing, classMissing);
/*  769:     */       }
/*  770:1318 */       classifiers = AbstractClassifier.makeCopies(getClassifier(), 2);
/*  771:1319 */       evaluationB = new Evaluation(train);
/*  772:1320 */       evaluationI = new Evaluation(train);
/*  773:1321 */       classifiers[0].buildClassifier(train);
/*  774:1322 */       testWRTZeroR(classifiers[0], evaluationB, train, test);
/*  775:     */     }
/*  776:     */     catch (Exception ex)
/*  777:     */     {
/*  778:1324 */       throw new Error("Error setting up for tests: " + ex.getMessage());
/*  779:     */     }
/*  780:     */     try
/*  781:     */     {
/*  782:1329 */       for (int i = 0; i < train.numInstances(); i++) {
/*  783:1330 */         train.instance(i).setWeight(0.0D);
/*  784:     */       }
/*  785:1332 */       Random random = new Random(1L);
/*  786:1333 */       for (int i = 0; i < train.numInstances() / 2; i++)
/*  787:     */       {
/*  788:1334 */         int inst = random.nextInt(train.numInstances());
/*  789:1335 */         int weight = random.nextInt(10) + 1;
/*  790:1336 */         train.instance(inst).setWeight(weight);
/*  791:     */       }
/*  792:1338 */       classifiers[1].buildClassifier(train);
/*  793:1339 */       built = true;
/*  794:1340 */       testWRTZeroR(classifiers[1], evaluationI, train, test);
/*  795:1341 */       if (evaluationB.equals(evaluationI))
/*  796:     */       {
/*  797:1343 */         evalFail = true;
/*  798:1344 */         throw new Exception("evalFail");
/*  799:     */       }
/*  800:1347 */       println("yes");
/*  801:1348 */       result[0] = true;
/*  802:     */     }
/*  803:     */     catch (Exception ex)
/*  804:     */     {
/*  805:1350 */       println("no");
/*  806:1351 */       result[0] = false;
/*  807:1353 */       if (this.m_Debug)
/*  808:     */       {
/*  809:1354 */         println("\n=== Full Report ===");
/*  810:1356 */         if (evalFail)
/*  811:     */         {
/*  812:1357 */           println("Results don't differ between non-weighted and weighted instance models.");
/*  813:     */           
/*  814:1359 */           println("Here are the results:\n");
/*  815:1360 */           println(evaluationB.toSummaryString("\nboth methods\n", true));
/*  816:     */         }
/*  817:     */         else
/*  818:     */         {
/*  819:1362 */           print("Problem during");
/*  820:1363 */           if (built) {
/*  821:1364 */             print(" testing");
/*  822:     */           } else {
/*  823:1366 */             print(" training");
/*  824:     */           }
/*  825:1368 */           println(": " + ex.getMessage() + "\n");
/*  826:     */         }
/*  827:1370 */         println("Here are the datasets:\n");
/*  828:1371 */         println("=== Train Dataset ===\n" + train.toString() + "\n");
/*  829:1372 */         println("=== Train Weights ===\n");
/*  830:1373 */         for (int i = 0; i < train.numInstances(); i++) {
/*  831:1374 */           println(" " + (i + 1) + "    " + train.instance(i).weight());
/*  832:     */         }
/*  833:1376 */         println("=== Test Dataset ===\n" + test.toString() + "\n\n");
/*  834:1377 */         println("(test weights all 1.0\n");
/*  835:     */       }
/*  836:     */     }
/*  837:1381 */     return result;
/*  838:     */   }
/*  839:     */   
/*  840:     */   protected boolean[] datasetIntegrity(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType, boolean predictorMissing, boolean classMissing)
/*  841:     */   {
/*  842:1408 */     print("classifier doesn't alter original datasets");
/*  843:1409 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType);
/*  844:     */     
/*  845:1411 */     print("...");
/*  846:1412 */     int numTrain = getNumInstances();int numTest = getNumInstances();int numClasses = 2;
/*  847:1413 */     int missingLevel = 20;
/*  848:     */     
/*  849:1415 */     boolean[] result = new boolean[2];
/*  850:1416 */     Instances train = null;
/*  851:1417 */     Instances test = null;
/*  852:1418 */     Classifier classifier = null;
/*  853:1419 */     Evaluation evaluation = null;
/*  854:1420 */     boolean built = false;
/*  855:     */     try
/*  856:     */     {
/*  857:1422 */       train = makeTestDataset(42, numTrain, nominalPredictor ? getNumNominal() : 0, numericPredictor ? getNumNumeric() : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, numClasses, classType, multiInstance);
/*  858:     */       
/*  859:     */ 
/*  860:     */ 
/*  861:     */ 
/*  862:1427 */       test = makeTestDataset(24, numTest, nominalPredictor ? getNumNominal() : 0, numericPredictor ? getNumNumeric() : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, numClasses, classType, multiInstance);
/*  863:1432 */       if (missingLevel > 0)
/*  864:     */       {
/*  865:1433 */         addMissing(train, missingLevel, predictorMissing, classMissing);
/*  866:1434 */         addMissing(test, Math.min(missingLevel, 50), predictorMissing, classMissing);
/*  867:     */       }
/*  868:1437 */       classifier = AbstractClassifier.makeCopies(getClassifier(), 1)[0];
/*  869:1438 */       evaluation = new Evaluation(train);
/*  870:     */     }
/*  871:     */     catch (Exception ex)
/*  872:     */     {
/*  873:1440 */       throw new Error("Error setting up for tests: " + ex.getMessage());
/*  874:     */     }
/*  875:     */     try
/*  876:     */     {
/*  877:1443 */       Instances trainCopy = new Instances(train);
/*  878:1444 */       Instances testCopy = new Instances(test);
/*  879:1445 */       classifier.buildClassifier(trainCopy);
/*  880:1446 */       compareDatasets(train, trainCopy);
/*  881:1447 */       built = true;
/*  882:1448 */       testWRTZeroR(classifier, evaluation, trainCopy, testCopy);
/*  883:1449 */       compareDatasets(test, testCopy);
/*  884:     */       
/*  885:1451 */       println("yes");
/*  886:1452 */       result[0] = true;
/*  887:     */     }
/*  888:     */     catch (Exception ex)
/*  889:     */     {
/*  890:1454 */       println("no");
/*  891:1455 */       result[0] = false;
/*  892:1457 */       if (this.m_Debug)
/*  893:     */       {
/*  894:1458 */         println("\n=== Full Report ===");
/*  895:1459 */         print("Problem during");
/*  896:1460 */         if (built) {
/*  897:1461 */           print(" testing");
/*  898:     */         } else {
/*  899:1463 */           print(" training");
/*  900:     */         }
/*  901:1465 */         println(": " + ex.getMessage() + "\n");
/*  902:1466 */         println("Here are the datasets:\n");
/*  903:1467 */         println("=== Train Dataset ===\n" + train.toString() + "\n");
/*  904:1468 */         println("=== Test Dataset ===\n" + test.toString() + "\n\n");
/*  905:     */       }
/*  906:     */     }
/*  907:1472 */     return result;
/*  908:     */   }
/*  909:     */   
/*  910:     */   protected boolean[] runBasicTest(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType, int missingLevel, boolean predictorMissing, boolean classMissing, int numTrain, int numTest, int numClasses, ArrayList<String> accepts)
/*  911:     */   {
/*  912:1501 */     return runBasicTest(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType, -1, missingLevel, predictorMissing, classMissing, numTrain, numTest, numClasses, accepts);
/*  913:     */   }
/*  914:     */   
/*  915:     */   protected boolean[] runBasicTest(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType, int classIndex, int missingLevel, boolean predictorMissing, boolean classMissing, int numTrain, int numTest, int numClasses, ArrayList<String> accepts)
/*  916:     */   {
/*  917:1535 */     boolean[] result = new boolean[2];
/*  918:1536 */     Instances train = null;
/*  919:1537 */     Instances test = null;
/*  920:1538 */     Classifier classifier = null;
/*  921:1539 */     Evaluation evaluation = null;
/*  922:1540 */     boolean built = false;
/*  923:     */     try
/*  924:     */     {
/*  925:1542 */       train = makeTestDataset(42, numTrain, nominalPredictor ? getNumNominal() : 0, numericPredictor ? getNumNumeric() : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, numClasses, classType, classIndex, multiInstance);
/*  926:     */       
/*  927:     */ 
/*  928:     */ 
/*  929:     */ 
/*  930:1547 */       test = makeTestDataset(24, numTest, nominalPredictor ? getNumNominal() : 0, numericPredictor ? getNumNumeric() : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, numClasses, classType, classIndex, multiInstance);
/*  931:1552 */       if (missingLevel > 0)
/*  932:     */       {
/*  933:1553 */         addMissing(train, missingLevel, predictorMissing, classMissing);
/*  934:1554 */         addMissing(test, Math.min(missingLevel, 50), predictorMissing, classMissing);
/*  935:     */       }
/*  936:1557 */       classifier = AbstractClassifier.makeCopies(getClassifier(), 1)[0];
/*  937:1558 */       evaluation = new Evaluation(train);
/*  938:     */     }
/*  939:     */     catch (Exception ex)
/*  940:     */     {
/*  941:1560 */       ex.printStackTrace();
/*  942:1561 */       throw new Error("Error setting up for tests: " + ex.getMessage());
/*  943:     */     }
/*  944:     */     try
/*  945:     */     {
/*  946:1564 */       classifier.buildClassifier(train);
/*  947:1565 */       built = true;
/*  948:1566 */       if (testWRTZeroR(classifier, evaluation, train, test)[0] == 0)
/*  949:     */       {
/*  950:1567 */         result[0] = true;
/*  951:1568 */         result[1] = true;
/*  952:1569 */         throw new Exception("Scheme performs worse than ZeroR");
/*  953:     */       }
/*  954:1572 */       println("yes");
/*  955:1573 */       result[0] = true;
/*  956:     */     }
/*  957:     */     catch (Exception ex)
/*  958:     */     {
/*  959:1575 */       boolean acceptable = false;
/*  960:     */       String msg;
/*  961:     */       String msg;
/*  962:1577 */       if (ex.getMessage() == null) {
/*  963:1578 */         msg = "";
/*  964:     */       } else {
/*  965:1580 */         msg = ex.getMessage().toLowerCase();
/*  966:     */       }
/*  967:1582 */       if (msg.indexOf("not in classpath") > -1) {
/*  968:1583 */         this.m_ClasspathProblems = true;
/*  969:     */       }
/*  970:1585 */       if (msg.indexOf("worse than zeror") >= 0)
/*  971:     */       {
/*  972:1586 */         println("warning: performs worse than ZeroR");
/*  973:1587 */         result[0] = true;
/*  974:1588 */         result[1] = true;
/*  975:     */       }
/*  976:     */       else
/*  977:     */       {
/*  978:1590 */         for (int i = 0; i < accepts.size(); i++) {
/*  979:1591 */           if (msg.indexOf((String)accepts.get(i)) >= 0) {
/*  980:1592 */             acceptable = true;
/*  981:     */           }
/*  982:     */         }
/*  983:1596 */         println("no" + (acceptable ? " (OK error message)" : ""));
/*  984:1597 */         result[1] = acceptable;
/*  985:     */       }
/*  986:1600 */       if (this.m_Debug)
/*  987:     */       {
/*  988:1601 */         println("\n=== Full Report ===");
/*  989:1602 */         print("Problem during");
/*  990:1603 */         if (built) {
/*  991:1604 */           print(" testing");
/*  992:     */         } else {
/*  993:1606 */           print(" training");
/*  994:     */         }
/*  995:1608 */         println(": " + ex.getMessage() + "\n");
/*  996:1609 */         if (!acceptable)
/*  997:     */         {
/*  998:1610 */           if (accepts.size() > 0)
/*  999:     */           {
/* 1000:1611 */             print("Error message doesn't mention ");
/* 1001:1612 */             for (int i = 0; i < accepts.size(); i++)
/* 1002:     */             {
/* 1003:1613 */               if (i != 0) {
/* 1004:1614 */                 print(" or ");
/* 1005:     */               }
/* 1006:1616 */               print('"' + (String)accepts.get(i) + '"');
/* 1007:     */             }
/* 1008:     */           }
/* 1009:1619 */           println("here are the datasets:\n");
/* 1010:1620 */           println("=== Train Dataset ===\n" + train.toString() + "\n");
/* 1011:1621 */           println("=== Test Dataset ===\n" + test.toString() + "\n\n");
/* 1012:     */         }
/* 1013:     */       }
/* 1014:     */     }
/* 1015:1626 */     return result;
/* 1016:     */   }
/* 1017:     */   
/* 1018:     */   protected boolean[] testWRTZeroR(Classifier classifier, Evaluation evaluation, Instances train, Instances test)
/* 1019:     */     throws Exception
/* 1020:     */   {
/* 1021:1642 */     boolean[] result = new boolean[2];
/* 1022:     */     
/* 1023:1644 */     evaluation.evaluateModel(classifier, test, new Object[0]);
/* 1024:     */     try
/* 1025:     */     {
/* 1026:1648 */       Classifier zeroR = new ZeroR();
/* 1027:1649 */       zeroR.buildClassifier(train);
/* 1028:1650 */       Evaluation zeroREval = new Evaluation(train);
/* 1029:1651 */       zeroREval.evaluateModel(zeroR, test, new Object[0]);
/* 1030:1652 */       result[0] = Utils.grOrEq(zeroREval.errorRate(), evaluation.errorRate());
/* 1031:     */     }
/* 1032:     */     catch (Exception ex)
/* 1033:     */     {
/* 1034:1654 */       throw new Error("Problem determining ZeroR performance: " + ex.getMessage());
/* 1035:     */     }
/* 1036:1658 */     return result;
/* 1037:     */   }
/* 1038:     */   
/* 1039:     */   protected Instances makeTestDataset(int seed, int numInstances, int numNominal, int numNumeric, int numString, int numDate, int numRelational, int numClasses, int classType, boolean multiInstance)
/* 1040:     */     throws Exception
/* 1041:     */   {
/* 1042:1684 */     return makeTestDataset(seed, numInstances, numNominal, numNumeric, numString, numDate, numRelational, numClasses, classType, -1, multiInstance);
/* 1043:     */   }
/* 1044:     */   
/* 1045:     */   protected Instances makeTestDataset(int seed, int numInstances, int numNominal, int numNumeric, int numString, int numDate, int numRelational, int numClasses, int classType, int classIndex, boolean multiInstance)
/* 1046:     */     throws Exception
/* 1047:     */   {
/* 1048:1714 */     TestInstances dataset = new TestInstances();
/* 1049:     */     
/* 1050:1716 */     dataset.setSeed(seed);
/* 1051:1717 */     dataset.setNumInstances(numInstances);
/* 1052:1718 */     dataset.setNumNominal(numNominal);
/* 1053:1719 */     dataset.setNumNumeric(numNumeric);
/* 1054:1720 */     dataset.setNumString(numString);
/* 1055:1721 */     dataset.setNumDate(numDate);
/* 1056:1722 */     dataset.setNumRelational(numRelational);
/* 1057:1723 */     dataset.setNumClasses(numClasses);
/* 1058:1724 */     dataset.setClassType(classType);
/* 1059:1725 */     dataset.setClassIndex(classIndex);
/* 1060:1726 */     dataset.setNumClasses(numClasses);
/* 1061:1727 */     dataset.setMultiInstance(multiInstance);
/* 1062:1728 */     dataset.setWords(getWords());
/* 1063:1729 */     dataset.setWordSeparators(getWordSeparators());
/* 1064:     */     
/* 1065:1731 */     return process(dataset.generate());
/* 1066:     */   }
/* 1067:     */   
/* 1068:     */   protected void printAttributeSummary(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType)
/* 1069:     */   {
/* 1070:1750 */     String str = "";
/* 1071:1752 */     if (numericPredictor) {
/* 1072:1753 */       str = str + " numeric";
/* 1073:     */     }
/* 1074:1756 */     if (nominalPredictor)
/* 1075:     */     {
/* 1076:1757 */       if (str.length() > 0) {
/* 1077:1758 */         str = str + " &";
/* 1078:     */       }
/* 1079:1760 */       str = str + " nominal";
/* 1080:     */     }
/* 1081:1763 */     if (stringPredictor)
/* 1082:     */     {
/* 1083:1764 */       if (str.length() > 0) {
/* 1084:1765 */         str = str + " &";
/* 1085:     */       }
/* 1086:1767 */       str = str + " string";
/* 1087:     */     }
/* 1088:1770 */     if (datePredictor)
/* 1089:     */     {
/* 1090:1771 */       if (str.length() > 0) {
/* 1091:1772 */         str = str + " &";
/* 1092:     */       }
/* 1093:1774 */       str = str + " date";
/* 1094:     */     }
/* 1095:1777 */     if (relationalPredictor)
/* 1096:     */     {
/* 1097:1778 */       if (str.length() > 0) {
/* 1098:1779 */         str = str + " &";
/* 1099:     */       }
/* 1100:1781 */       str = str + " relational";
/* 1101:     */     }
/* 1102:1784 */     str = str + " predictors)";
/* 1103:1786 */     switch (classType)
/* 1104:     */     {
/* 1105:     */     case 0: 
/* 1106:1788 */       str = " (numeric class," + str;
/* 1107:1789 */       break;
/* 1108:     */     case 1: 
/* 1109:1791 */       str = " (nominal class," + str;
/* 1110:1792 */       break;
/* 1111:     */     case 2: 
/* 1112:1794 */       str = " (string class," + str;
/* 1113:1795 */       break;
/* 1114:     */     case 3: 
/* 1115:1797 */       str = " (date class," + str;
/* 1116:1798 */       break;
/* 1117:     */     case 4: 
/* 1118:1800 */       str = " (relational class," + str;
/* 1119:     */     }
/* 1120:1804 */     print(str);
/* 1121:     */   }
/* 1122:     */   
/* 1123:     */   public String getRevision()
/* 1124:     */   {
/* 1125:1814 */     return RevisionUtils.extract("$Revision: 11253 $");
/* 1126:     */   }
/* 1127:     */   
/* 1128:     */   public static void main(String[] args)
/* 1129:     */   {
/* 1130:1823 */     runCheck(new CheckClassifier(), args);
/* 1131:     */   }
/* 1132:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.CheckClassifier
 * JD-Core Version:    0.7.0.1
 */