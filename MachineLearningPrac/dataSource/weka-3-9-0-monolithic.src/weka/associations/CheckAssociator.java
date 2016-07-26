/*    1:     */ package weka.associations;
/*    2:     */ 
/*    3:     */ import java.util.ArrayList;
/*    4:     */ import java.util.Collections;
/*    5:     */ import java.util.Enumeration;
/*    6:     */ import java.util.Random;
/*    7:     */ import java.util.Vector;
/*    8:     */ import weka.core.CheckScheme;
/*    9:     */ import weka.core.Instance;
/*   10:     */ import weka.core.Instances;
/*   11:     */ import weka.core.MultiInstanceCapabilitiesHandler;
/*   12:     */ import weka.core.Option;
/*   13:     */ import weka.core.OptionHandler;
/*   14:     */ import weka.core.RevisionHandler;
/*   15:     */ import weka.core.RevisionUtils;
/*   16:     */ import weka.core.SerializationHelper;
/*   17:     */ import weka.core.TestInstances;
/*   18:     */ import weka.core.Utils;
/*   19:     */ import weka.core.WeightedInstancesHandler;
/*   20:     */ 
/*   21:     */ public class CheckAssociator
/*   22:     */   extends CheckScheme
/*   23:     */   implements RevisionHandler
/*   24:     */ {
/*   25:     */   public static final int NO_CLASS = -1;
/*   26: 262 */   protected Associator m_Associator = new Apriori();
/*   27:     */   
/*   28:     */   public Enumeration<Option> listOptions()
/*   29:     */   {
/*   30: 271 */     Vector<Option> result = new Vector();
/*   31:     */     
/*   32: 273 */     result.addAll(Collections.list(super.listOptions()));
/*   33:     */     
/*   34: 275 */     result.add(new Option("\tFull name of the associator analysed.\n\teg: weka.associations.Apriori\n\t(default weka.associations.Apriori)", "W", 1, "-W"));
/*   35: 279 */     if ((this.m_Associator != null) && ((this.m_Associator instanceof OptionHandler)))
/*   36:     */     {
/*   37: 280 */       result.add(new Option("", "", 0, "\nOptions specific to associator " + this.m_Associator.getClass().getName() + ":"));
/*   38:     */       
/*   39: 282 */       result.addAll(Collections.list(((OptionHandler)this.m_Associator).listOptions()));
/*   40:     */     }
/*   41: 286 */     return result.elements();
/*   42:     */   }
/*   43:     */   
/*   44:     */   public void setOptions(String[] options)
/*   45:     */     throws Exception
/*   46:     */   {
/*   47: 437 */     super.setOptions(options);
/*   48:     */     
/*   49: 439 */     String tmpStr = Utils.getOption('W', options);
/*   50: 440 */     if (tmpStr.length() == 0) {
/*   51: 441 */       tmpStr = Apriori.class.getName();
/*   52:     */     }
/*   53: 443 */     setAssociator((Associator)forName("weka.associations", Associator.class, tmpStr, Utils.partitionOptions(options)));
/*   54:     */   }
/*   55:     */   
/*   56:     */   public String[] getOptions()
/*   57:     */   {
/*   58: 454 */     Vector<String> result = new Vector();
/*   59:     */     
/*   60: 456 */     Collections.addAll(result, super.getOptions());
/*   61: 458 */     if (getAssociator() != null)
/*   62:     */     {
/*   63: 459 */       result.add("-W");
/*   64: 460 */       result.add(getAssociator().getClass().getName());
/*   65:     */     }
/*   66: 463 */     if ((this.m_Associator != null) && ((this.m_Associator instanceof OptionHandler)))
/*   67:     */     {
/*   68: 464 */       String[] options = ((OptionHandler)this.m_Associator).getOptions();
/*   69: 466 */       if (options.length > 0)
/*   70:     */       {
/*   71: 467 */         result.add("--");
/*   72: 468 */         Collections.addAll(result, options);
/*   73:     */       }
/*   74:     */     }
/*   75: 472 */     return (String[])result.toArray(new String[result.size()]);
/*   76:     */   }
/*   77:     */   
/*   78:     */   public void doTests()
/*   79:     */   {
/*   80: 481 */     if (getAssociator() == null)
/*   81:     */     {
/*   82: 482 */       println("\n=== No associator set ===");
/*   83: 483 */       return;
/*   84:     */     }
/*   85: 485 */     println("\n=== Check on Associator: " + getAssociator().getClass().getName() + " ===\n");
/*   86:     */     
/*   87:     */ 
/*   88:     */ 
/*   89: 489 */     this.m_ClasspathProblems = false;
/*   90: 490 */     println("--> Checking for interfaces");
/*   91: 491 */     canTakeOptions();
/*   92: 492 */     boolean weightedInstancesHandler = weightedInstancesHandler()[0];
/*   93: 493 */     boolean multiInstanceHandler = multiInstanceHandler()[0];
/*   94: 494 */     println("--> Associator tests");
/*   95: 495 */     declaresSerialVersionUID();
/*   96: 496 */     println("--> no class attribute");
/*   97: 497 */     testsWithoutClass(weightedInstancesHandler, multiInstanceHandler);
/*   98: 498 */     println("--> with class attribute");
/*   99: 499 */     testsPerClassType(1, weightedInstancesHandler, multiInstanceHandler);
/*  100:     */     
/*  101: 501 */     testsPerClassType(0, weightedInstancesHandler, multiInstanceHandler);
/*  102:     */     
/*  103: 503 */     testsPerClassType(3, weightedInstancesHandler, multiInstanceHandler);
/*  104:     */     
/*  105: 505 */     testsPerClassType(2, weightedInstancesHandler, multiInstanceHandler);
/*  106:     */     
/*  107: 507 */     testsPerClassType(4, weightedInstancesHandler, multiInstanceHandler);
/*  108:     */   }
/*  109:     */   
/*  110:     */   public void setAssociator(Associator newAssociator)
/*  111:     */   {
/*  112: 517 */     this.m_Associator = newAssociator;
/*  113:     */   }
/*  114:     */   
/*  115:     */   public Associator getAssociator()
/*  116:     */   {
/*  117: 526 */     return this.m_Associator;
/*  118:     */   }
/*  119:     */   
/*  120:     */   protected void testsPerClassType(int classType, boolean weighted, boolean multiInstance)
/*  121:     */   {
/*  122: 539 */     boolean PNom = canPredict(true, false, false, false, false, multiInstance, classType)[0];
/*  123:     */     
/*  124: 541 */     boolean PNum = canPredict(false, true, false, false, false, multiInstance, classType)[0];
/*  125:     */     
/*  126: 543 */     boolean PStr = canPredict(false, false, true, false, false, multiInstance, classType)[0];
/*  127:     */     
/*  128: 545 */     boolean PDat = canPredict(false, false, false, true, false, multiInstance, classType)[0];
/*  129:     */     boolean PRel;
/*  130:     */     boolean PRel;
/*  131: 548 */     if (!multiInstance) {
/*  132: 549 */       PRel = canPredict(false, false, false, false, true, multiInstance, classType)[0];
/*  133:     */     } else {
/*  134: 552 */       PRel = false;
/*  135:     */     }
/*  136: 555 */     if ((PNom) || (PNum) || (PStr) || (PDat) || (PRel))
/*  137:     */     {
/*  138: 556 */       if (weighted) {
/*  139: 557 */         instanceWeights(PNom, PNum, PStr, PDat, PRel, multiInstance, classType);
/*  140:     */       }
/*  141: 560 */       if (classType == 1) {
/*  142: 561 */         canHandleNClasses(PNom, PNum, PStr, PDat, PRel, multiInstance, 4);
/*  143:     */       }
/*  144: 564 */       if (!multiInstance)
/*  145:     */       {
/*  146: 565 */         canHandleClassAsNthAttribute(PNom, PNum, PStr, PDat, PRel, multiInstance, classType, 0);
/*  147:     */         
/*  148: 567 */         canHandleClassAsNthAttribute(PNom, PNum, PStr, PDat, PRel, multiInstance, classType, 1);
/*  149:     */       }
/*  150: 571 */       canHandleZeroTraining(PNom, PNum, PStr, PDat, PRel, multiInstance, classType);
/*  151:     */       
/*  152: 573 */       boolean handleMissingPredictors = canHandleMissing(PNom, PNum, PStr, PDat, PRel, multiInstance, classType, true, false, 20)[0];
/*  153: 575 */       if (handleMissingPredictors) {
/*  154: 576 */         canHandleMissing(PNom, PNum, PStr, PDat, PRel, multiInstance, classType, true, false, 100);
/*  155:     */       }
/*  156: 580 */       boolean handleMissingClass = canHandleMissing(PNom, PNum, PStr, PDat, PRel, multiInstance, classType, false, true, 20)[0];
/*  157: 582 */       if (handleMissingClass) {
/*  158: 583 */         canHandleMissing(PNom, PNum, PStr, PDat, PRel, multiInstance, classType, false, true, 100);
/*  159:     */       }
/*  160: 587 */       correctBuildInitialisation(PNom, PNum, PStr, PDat, PRel, multiInstance, classType);
/*  161:     */       
/*  162: 589 */       datasetIntegrity(PNom, PNum, PStr, PDat, PRel, multiInstance, classType, handleMissingPredictors, handleMissingClass);
/*  163:     */     }
/*  164:     */   }
/*  165:     */   
/*  166:     */   protected void testsWithoutClass(boolean weighted, boolean multiInstance)
/*  167:     */   {
/*  168: 602 */     boolean PNom = canPredict(true, false, false, false, false, multiInstance, -1)[0];
/*  169:     */     
/*  170: 604 */     boolean PNum = canPredict(false, true, false, false, false, multiInstance, -1)[0];
/*  171:     */     
/*  172: 606 */     boolean PStr = canPredict(false, false, true, false, false, multiInstance, -1)[0];
/*  173:     */     
/*  174: 608 */     boolean PDat = canPredict(false, false, false, true, false, multiInstance, -1)[0];
/*  175:     */     boolean PRel;
/*  176:     */     boolean PRel;
/*  177: 611 */     if (!multiInstance) {
/*  178: 612 */       PRel = canPredict(false, false, false, false, true, multiInstance, -1)[0];
/*  179:     */     } else {
/*  180: 615 */       PRel = false;
/*  181:     */     }
/*  182: 618 */     if ((PNom) || (PNum) || (PStr) || (PDat) || (PRel))
/*  183:     */     {
/*  184: 619 */       if (weighted) {
/*  185: 620 */         instanceWeights(PNom, PNum, PStr, PDat, PRel, multiInstance, -1);
/*  186:     */       }
/*  187: 623 */       canHandleZeroTraining(PNom, PNum, PStr, PDat, PRel, multiInstance, -1);
/*  188:     */       
/*  189: 625 */       boolean handleMissingPredictors = canHandleMissing(PNom, PNum, PStr, PDat, PRel, multiInstance, -1, true, false, 20)[0];
/*  190: 627 */       if (handleMissingPredictors) {
/*  191: 628 */         canHandleMissing(PNom, PNum, PStr, PDat, PRel, multiInstance, -1, true, false, 100);
/*  192:     */       }
/*  193: 632 */       correctBuildInitialisation(PNom, PNum, PStr, PDat, PRel, multiInstance, -1);
/*  194:     */       
/*  195: 634 */       datasetIntegrity(PNom, PNum, PStr, PDat, PRel, multiInstance, -1, handleMissingPredictors, false);
/*  196:     */     }
/*  197:     */   }
/*  198:     */   
/*  199:     */   protected boolean[] canTakeOptions()
/*  200:     */   {
/*  201: 646 */     boolean[] result = new boolean[2];
/*  202:     */     
/*  203: 648 */     print("options...");
/*  204: 649 */     if ((this.m_Associator instanceof OptionHandler))
/*  205:     */     {
/*  206: 650 */       println("yes");
/*  207: 651 */       if (this.m_Debug)
/*  208:     */       {
/*  209: 652 */         println("\n=== Full report ===");
/*  210: 653 */         Enumeration<Option> enu = ((OptionHandler)this.m_Associator).listOptions();
/*  211: 654 */         while (enu.hasMoreElements())
/*  212:     */         {
/*  213: 655 */           Option option = (Option)enu.nextElement();
/*  214: 656 */           print(option.synopsis() + "\n" + option.description() + "\n");
/*  215:     */         }
/*  216: 658 */         println("\n");
/*  217:     */       }
/*  218: 660 */       result[0] = true;
/*  219:     */     }
/*  220:     */     else
/*  221:     */     {
/*  222: 662 */       println("no");
/*  223: 663 */       result[0] = false;
/*  224:     */     }
/*  225: 666 */     return result;
/*  226:     */   }
/*  227:     */   
/*  228:     */   protected boolean[] weightedInstancesHandler()
/*  229:     */   {
/*  230: 676 */     boolean[] result = new boolean[2];
/*  231:     */     
/*  232: 678 */     print("weighted instances associator...");
/*  233: 679 */     if ((this.m_Associator instanceof WeightedInstancesHandler))
/*  234:     */     {
/*  235: 680 */       println("yes");
/*  236: 681 */       result[0] = true;
/*  237:     */     }
/*  238:     */     else
/*  239:     */     {
/*  240: 683 */       println("no");
/*  241: 684 */       result[0] = false;
/*  242:     */     }
/*  243: 687 */     return result;
/*  244:     */   }
/*  245:     */   
/*  246:     */   protected boolean[] multiInstanceHandler()
/*  247:     */   {
/*  248: 696 */     boolean[] result = new boolean[2];
/*  249:     */     
/*  250: 698 */     print("multi-instance associator...");
/*  251: 699 */     if ((this.m_Associator instanceof MultiInstanceCapabilitiesHandler))
/*  252:     */     {
/*  253: 700 */       println("yes");
/*  254: 701 */       result[0] = true;
/*  255:     */     }
/*  256:     */     else
/*  257:     */     {
/*  258: 703 */       println("no");
/*  259: 704 */       result[0] = false;
/*  260:     */     }
/*  261: 707 */     return result;
/*  262:     */   }
/*  263:     */   
/*  264:     */   protected boolean[] declaresSerialVersionUID()
/*  265:     */   {
/*  266: 717 */     boolean[] result = new boolean[2];
/*  267:     */     
/*  268: 719 */     print("serialVersionUID...");
/*  269:     */     
/*  270: 721 */     result[0] = (!SerializationHelper.needsUID(this.m_Associator.getClass()) ? 1 : false);
/*  271: 723 */     if (result[0] != 0) {
/*  272: 724 */       println("yes");
/*  273:     */     } else {
/*  274: 726 */       println("no");
/*  275:     */     }
/*  276: 729 */     return result;
/*  277:     */   }
/*  278:     */   
/*  279:     */   protected boolean[] canPredict(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType)
/*  280:     */   {
/*  281: 749 */     print("basic predict");
/*  282: 750 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType);
/*  283:     */     
/*  284: 752 */     print("...");
/*  285: 753 */     ArrayList<String> accepts = new ArrayList();
/*  286: 754 */     accepts.add("any");
/*  287: 755 */     accepts.add("unary");
/*  288: 756 */     accepts.add("binary");
/*  289: 757 */     accepts.add("nominal");
/*  290: 758 */     accepts.add("numeric");
/*  291: 759 */     accepts.add("string");
/*  292: 760 */     accepts.add("date");
/*  293: 761 */     accepts.add("relational");
/*  294: 762 */     accepts.add("multi-instance");
/*  295: 763 */     accepts.add("not in classpath");
/*  296: 764 */     int numTrain = getNumInstances();int numClasses = 2;int missingLevel = 0;
/*  297: 765 */     boolean predictorMissing = false;boolean classMissing = false;
/*  298:     */     
/*  299: 767 */     return runBasicTest(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType, missingLevel, predictorMissing, classMissing, numTrain, numClasses, accepts);
/*  300:     */   }
/*  301:     */   
/*  302:     */   protected boolean[] canHandleNClasses(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int numClasses)
/*  303:     */   {
/*  304: 792 */     print("more than two class problems");
/*  305: 793 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, 1);
/*  306:     */     
/*  307: 795 */     print("...");
/*  308: 796 */     ArrayList<String> accepts = new ArrayList();
/*  309: 797 */     accepts.add("number");
/*  310: 798 */     accepts.add("class");
/*  311: 799 */     int numTrain = getNumInstances();int missingLevel = 0;
/*  312: 800 */     boolean predictorMissing = false;boolean classMissing = false;
/*  313:     */     
/*  314: 802 */     return runBasicTest(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, 1, missingLevel, predictorMissing, classMissing, numTrain, numClasses, accepts);
/*  315:     */   }
/*  316:     */   
/*  317:     */   protected boolean[] canHandleClassAsNthAttribute(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType, int classIndex)
/*  318:     */   {
/*  319: 829 */     if (classIndex == -1) {
/*  320: 830 */       print("class attribute as last attribute");
/*  321:     */     } else {
/*  322: 832 */       print("class attribute as " + (classIndex + 1) + ". attribute");
/*  323:     */     }
/*  324: 834 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType);
/*  325:     */     
/*  326: 836 */     print("...");
/*  327: 837 */     ArrayList<String> accepts = new ArrayList();
/*  328: 838 */     int numTrain = getNumInstances();int numClasses = 2;int missingLevel = 0;
/*  329: 839 */     boolean predictorMissing = false;boolean classMissing = false;
/*  330:     */     
/*  331: 841 */     return runBasicTest(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType, classIndex, missingLevel, predictorMissing, classMissing, numTrain, numClasses, accepts);
/*  332:     */   }
/*  333:     */   
/*  334:     */   protected boolean[] canHandleZeroTraining(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType)
/*  335:     */   {
/*  336: 864 */     print("handle zero training instances");
/*  337: 865 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType);
/*  338:     */     
/*  339: 867 */     print("...");
/*  340: 868 */     ArrayList<String> accepts = new ArrayList();
/*  341: 869 */     accepts.add("train");
/*  342: 870 */     accepts.add("value");
/*  343: 871 */     int numTrain = 0;int numClasses = 2;int missingLevel = 0;
/*  344: 872 */     boolean predictorMissing = false;boolean classMissing = false;
/*  345:     */     
/*  346: 874 */     return runBasicTest(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType, missingLevel, predictorMissing, classMissing, numTrain, numClasses, accepts);
/*  347:     */   }
/*  348:     */   
/*  349:     */   protected boolean[] correctBuildInitialisation(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType)
/*  350:     */   {
/*  351: 901 */     boolean[] result = new boolean[2];
/*  352:     */     
/*  353: 903 */     print("correct initialisation during buildAssociations");
/*  354: 904 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType);
/*  355:     */     
/*  356: 906 */     print("...");
/*  357: 907 */     int numTrain = getNumInstances();int numClasses = 2;int missingLevel = 0;
/*  358: 908 */     boolean predictorMissing = false;boolean classMissing = false;
/*  359:     */     
/*  360: 910 */     Instances train1 = null;
/*  361: 911 */     Instances train2 = null;
/*  362: 912 */     Associator associator = null;
/*  363: 913 */     AssociatorEvaluation evaluation1A = null;
/*  364: 914 */     AssociatorEvaluation evaluation1B = null;
/*  365: 915 */     AssociatorEvaluation evaluation2 = null;
/*  366: 916 */     int stage = 0;
/*  367:     */     try
/*  368:     */     {
/*  369: 920 */       train1 = makeTestDataset(42, numTrain, nominalPredictor ? getNumNominal() : 0, numericPredictor ? getNumNumeric() : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, numClasses, classType, multiInstance);
/*  370:     */       
/*  371:     */ 
/*  372:     */ 
/*  373:     */ 
/*  374: 925 */       train2 = makeTestDataset(84, numTrain, nominalPredictor ? getNumNominal() + 1 : 0, numericPredictor ? getNumNumeric() + 1 : 0, stringPredictor ? getNumString() + 1 : 0, datePredictor ? getNumDate() + 1 : 0, relationalPredictor ? getNumRelational() + 1 : 0, numClasses, classType, multiInstance);
/*  375: 932 */       if (missingLevel > 0)
/*  376:     */       {
/*  377: 933 */         addMissing(train1, missingLevel, predictorMissing, classMissing);
/*  378: 934 */         addMissing(train2, missingLevel, predictorMissing, classMissing);
/*  379:     */       }
/*  380: 937 */       associator = AbstractAssociator.makeCopies(getAssociator(), 1)[0];
/*  381: 938 */       evaluation1A = new AssociatorEvaluation();
/*  382: 939 */       evaluation1B = new AssociatorEvaluation();
/*  383: 940 */       evaluation2 = new AssociatorEvaluation();
/*  384:     */     }
/*  385:     */     catch (Exception ex)
/*  386:     */     {
/*  387: 942 */       throw new Error("Error setting up for tests: " + ex.getMessage());
/*  388:     */     }
/*  389:     */     try
/*  390:     */     {
/*  391: 945 */       stage = 0;
/*  392: 946 */       evaluation1A.evaluate(associator, train1);
/*  393:     */       
/*  394: 948 */       stage = 1;
/*  395: 949 */       evaluation2.evaluate(associator, train2);
/*  396:     */       
/*  397: 951 */       stage = 2;
/*  398: 952 */       evaluation1B.evaluate(associator, train1);
/*  399:     */       
/*  400: 954 */       stage = 3;
/*  401: 955 */       if (!evaluation1A.equals(evaluation1B))
/*  402:     */       {
/*  403: 956 */         if (this.m_Debug)
/*  404:     */         {
/*  405: 957 */           println("\n=== Full report ===\n" + evaluation1A.toSummaryString("\nFirst buildAssociations()") + "\n\n");
/*  406:     */           
/*  407:     */ 
/*  408: 960 */           println(evaluation1B.toSummaryString("\nSecond buildAssociations()") + "\n\n");
/*  409:     */         }
/*  410: 963 */         throw new Exception("Results differ between buildAssociations calls");
/*  411:     */       }
/*  412: 965 */       println("yes");
/*  413: 966 */       result[0] = true;
/*  414:     */     }
/*  415:     */     catch (Exception ex)
/*  416:     */     {
/*  417: 969 */       println("no");
/*  418: 970 */       result[0] = false;
/*  419: 972 */       if (this.m_Debug)
/*  420:     */       {
/*  421: 973 */         println("\n=== Full Report ===");
/*  422: 974 */         print("Problem during building");
/*  423: 975 */         switch (stage)
/*  424:     */         {
/*  425:     */         case 0: 
/*  426: 977 */           print(" of dataset 1");
/*  427: 978 */           break;
/*  428:     */         case 1: 
/*  429: 980 */           print(" of dataset 2");
/*  430: 981 */           break;
/*  431:     */         case 2: 
/*  432: 983 */           print(" of dataset 1 (2nd build)");
/*  433: 984 */           break;
/*  434:     */         case 3: 
/*  435: 986 */           print(", comparing results from builds of dataset 1");
/*  436:     */         }
/*  437: 989 */         println(": " + ex.getMessage() + "\n");
/*  438: 990 */         println("here are the datasets:\n");
/*  439: 991 */         println("=== Train1 Dataset ===\n" + train1.toString() + "\n");
/*  440: 992 */         println("=== Train2 Dataset ===\n" + train2.toString() + "\n");
/*  441:     */       }
/*  442:     */     }
/*  443: 996 */     return result;
/*  444:     */   }
/*  445:     */   
/*  446:     */   protected boolean[] canHandleMissing(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType, boolean predictorMissing, boolean classMissing, int missingLevel)
/*  447:     */   {
/*  448:1021 */     if (missingLevel == 100) {
/*  449:1022 */       print("100% ");
/*  450:     */     }
/*  451:1024 */     print("missing");
/*  452:1025 */     if (predictorMissing)
/*  453:     */     {
/*  454:1026 */       print(" predictor");
/*  455:1027 */       if (classMissing) {
/*  456:1028 */         print(" and");
/*  457:     */       }
/*  458:     */     }
/*  459:1031 */     if (classMissing) {
/*  460:1032 */       print(" class");
/*  461:     */     }
/*  462:1034 */     print(" values");
/*  463:1035 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType);
/*  464:     */     
/*  465:1037 */     print("...");
/*  466:1038 */     ArrayList<String> accepts = new ArrayList();
/*  467:1039 */     accepts.add("missing");
/*  468:1040 */     accepts.add("value");
/*  469:1041 */     accepts.add("train");
/*  470:1042 */     int numTrain = getNumInstances();int numClasses = 2;
/*  471:     */     
/*  472:1044 */     return runBasicTest(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType, missingLevel, predictorMissing, classMissing, numTrain, numClasses, accepts);
/*  473:     */   }
/*  474:     */   
/*  475:     */   protected boolean[] instanceWeights(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType)
/*  476:     */   {
/*  477:1072 */     print("associator uses instance weights");
/*  478:1073 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType);
/*  479:     */     
/*  480:1075 */     print("...");
/*  481:1076 */     int numTrain = 2 * getNumInstances();int numClasses = 2;int missingLevel = 0;
/*  482:1077 */     boolean predictorMissing = false;boolean classMissing = false;
/*  483:     */     
/*  484:1079 */     boolean[] result = new boolean[2];
/*  485:1080 */     Instances train = null;
/*  486:1081 */     Associator[] associators = null;
/*  487:1082 */     AssociatorEvaluation evaluationB = null;
/*  488:1083 */     AssociatorEvaluation evaluationI = null;
/*  489:1084 */     boolean evalFail = false;
/*  490:     */     try
/*  491:     */     {
/*  492:1086 */       train = makeTestDataset(42, numTrain, nominalPredictor ? getNumNominal() + 1 : 0, numericPredictor ? getNumNumeric() + 1 : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, numClasses, classType, multiInstance);
/*  493:1092 */       if (missingLevel > 0) {
/*  494:1093 */         addMissing(train, missingLevel, predictorMissing, classMissing);
/*  495:     */       }
/*  496:1095 */       associators = AbstractAssociator.makeCopies(getAssociator(), 2);
/*  497:1096 */       evaluationB = new AssociatorEvaluation();
/*  498:1097 */       evaluationI = new AssociatorEvaluation();
/*  499:1098 */       evaluationB.evaluate(associators[0], train);
/*  500:     */     }
/*  501:     */     catch (Exception ex)
/*  502:     */     {
/*  503:1100 */       throw new Error("Error setting up for tests: " + ex.getMessage());
/*  504:     */     }
/*  505:     */     try
/*  506:     */     {
/*  507:1105 */       for (int i = 0; i < train.numInstances(); i++) {
/*  508:1106 */         train.instance(i).setWeight(0.0D);
/*  509:     */       }
/*  510:1108 */       Random random = new Random(1L);
/*  511:1109 */       for (int i = 0; i < train.numInstances() / 2; i++)
/*  512:     */       {
/*  513:1110 */         int inst = random.nextInt(train.numInstances());
/*  514:1111 */         int weight = random.nextInt(10) + 1;
/*  515:1112 */         train.instance(inst).setWeight(weight);
/*  516:     */       }
/*  517:1114 */       evaluationI.evaluate(associators[1], train);
/*  518:1115 */       if (evaluationB.equals(evaluationI))
/*  519:     */       {
/*  520:1117 */         evalFail = true;
/*  521:1118 */         throw new Exception("evalFail");
/*  522:     */       }
/*  523:1121 */       println("yes");
/*  524:1122 */       result[0] = true;
/*  525:     */     }
/*  526:     */     catch (Exception ex)
/*  527:     */     {
/*  528:1124 */       println("no");
/*  529:1125 */       result[0] = false;
/*  530:1127 */       if (!this.m_Debug) {
/*  531:     */         break label596;
/*  532:     */       }
/*  533:     */     }
/*  534:1128 */     println("\n=== Full Report ===");
/*  535:1130 */     if (evalFail)
/*  536:     */     {
/*  537:1131 */       println("Results don't differ between non-weighted and weighted instance models.");
/*  538:     */       
/*  539:1133 */       println("Here are the results:\n");
/*  540:1134 */       println(evaluationB.toSummaryString("\nboth methods\n"));
/*  541:     */     }
/*  542:     */     else
/*  543:     */     {
/*  544:1136 */       print("Problem during building");
/*  545:1137 */       println(": " + ex.getMessage() + "\n");
/*  546:     */     }
/*  547:1139 */     println("Here is the dataset:\n");
/*  548:1140 */     println("=== Train Dataset ===\n" + train.toString() + "\n");
/*  549:1141 */     println("=== Train Weights ===\n");
/*  550:1142 */     for (int i = 0; i < train.numInstances(); i++) {
/*  551:1143 */       println(" " + (i + 1) + "    " + train.instance(i).weight());
/*  552:     */     }
/*  553:     */     label596:
/*  554:1148 */     return result;
/*  555:     */   }
/*  556:     */   
/*  557:     */   protected boolean[] datasetIntegrity(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType, boolean predictorMissing, boolean classMissing)
/*  558:     */   {
/*  559:1175 */     print("associator doesn't alter original datasets");
/*  560:1176 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType);
/*  561:     */     
/*  562:1178 */     print("...");
/*  563:1179 */     int numTrain = getNumInstances();int numClasses = 2;int missingLevel = 20;
/*  564:     */     
/*  565:1181 */     boolean[] result = new boolean[2];
/*  566:1182 */     Instances train = null;
/*  567:1183 */     Associator associator = null;
/*  568:     */     try
/*  569:     */     {
/*  570:1185 */       train = makeTestDataset(42, numTrain, nominalPredictor ? getNumNominal() : 0, numericPredictor ? getNumNumeric() : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, numClasses, classType, multiInstance);
/*  571:1190 */       if (missingLevel > 0) {
/*  572:1191 */         addMissing(train, missingLevel, predictorMissing, classMissing);
/*  573:     */       }
/*  574:1193 */       associator = AbstractAssociator.makeCopies(getAssociator(), 1)[0];
/*  575:     */     }
/*  576:     */     catch (Exception ex)
/*  577:     */     {
/*  578:1195 */       throw new Error("Error setting up for tests: " + ex.getMessage());
/*  579:     */     }
/*  580:     */     try
/*  581:     */     {
/*  582:1198 */       Instances trainCopy = new Instances(train);
/*  583:1199 */       associator.buildAssociations(trainCopy);
/*  584:1200 */       compareDatasets(train, trainCopy);
/*  585:     */       
/*  586:1202 */       println("yes");
/*  587:1203 */       result[0] = true;
/*  588:     */     }
/*  589:     */     catch (Exception ex)
/*  590:     */     {
/*  591:1205 */       println("no");
/*  592:1206 */       result[0] = false;
/*  593:1208 */       if (this.m_Debug)
/*  594:     */       {
/*  595:1209 */         println("\n=== Full Report ===");
/*  596:1210 */         print("Problem during building");
/*  597:1211 */         println(": " + ex.getMessage() + "\n");
/*  598:1212 */         println("Here is the dataset:\n");
/*  599:1213 */         println("=== Train Dataset ===\n" + train.toString() + "\n");
/*  600:     */       }
/*  601:     */     }
/*  602:1217 */     return result;
/*  603:     */   }
/*  604:     */   
/*  605:     */   protected boolean[] runBasicTest(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType, int missingLevel, boolean predictorMissing, boolean classMissing, int numTrain, int numClasses, ArrayList<String> accepts)
/*  606:     */   {
/*  607:1245 */     return runBasicTest(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType, -1, missingLevel, predictorMissing, classMissing, numTrain, numClasses, accepts);
/*  608:     */   }
/*  609:     */   
/*  610:     */   protected boolean[] runBasicTest(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType, int classIndex, int missingLevel, boolean predictorMissing, boolean classMissing, int numTrain, int numClasses, ArrayList<String> accepts)
/*  611:     */   {
/*  612:1278 */     boolean[] result = new boolean[2];
/*  613:1279 */     Instances train = null;
/*  614:1280 */     Associator associator = null;
/*  615:     */     try
/*  616:     */     {
/*  617:1282 */       train = makeTestDataset(42, numTrain, nominalPredictor ? getNumNominal() : 0, numericPredictor ? getNumNumeric() : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, numClasses, classType, classIndex, multiInstance);
/*  618:1287 */       if (missingLevel > 0) {
/*  619:1288 */         addMissing(train, missingLevel, predictorMissing, classMissing);
/*  620:     */       }
/*  621:1290 */       associator = AbstractAssociator.makeCopies(getAssociator(), 1)[0];
/*  622:     */     }
/*  623:     */     catch (Exception ex)
/*  624:     */     {
/*  625:1292 */       ex.printStackTrace();
/*  626:1293 */       throw new Error("Error setting up for tests: " + ex.getMessage());
/*  627:     */     }
/*  628:     */     try
/*  629:     */     {
/*  630:1296 */       associator.buildAssociations(train);
/*  631:1297 */       println("yes");
/*  632:1298 */       result[0] = true;
/*  633:     */     }
/*  634:     */     catch (Exception ex)
/*  635:     */     {
/*  636:1300 */       boolean acceptable = false;
/*  637:     */       String msg;
/*  638:     */       String msg;
/*  639:1302 */       if (ex.getMessage() == null) {
/*  640:1303 */         msg = "";
/*  641:     */       } else {
/*  642:1305 */         msg = ex.getMessage().toLowerCase();
/*  643:     */       }
/*  644:1307 */       if (msg.indexOf("not in classpath") > -1) {
/*  645:1308 */         this.m_ClasspathProblems = true;
/*  646:     */       }
/*  647:1311 */       for (int i = 0; i < accepts.size(); i++) {
/*  648:1312 */         if (msg.indexOf((String)accepts.get(i)) >= 0) {
/*  649:1313 */           acceptable = true;
/*  650:     */         }
/*  651:     */       }
/*  652:1317 */       println("no" + (acceptable ? " (OK error message)" : ""));
/*  653:1318 */       result[1] = acceptable;
/*  654:1320 */       if (this.m_Debug)
/*  655:     */       {
/*  656:1321 */         println("\n=== Full Report ===");
/*  657:1322 */         print("Problem during building");
/*  658:1323 */         println(": " + ex.getMessage() + "\n");
/*  659:1324 */         if (!acceptable)
/*  660:     */         {
/*  661:1325 */           if (accepts.size() > 0)
/*  662:     */           {
/*  663:1326 */             print("Error message doesn't mention ");
/*  664:1327 */             for (int i = 0; i < accepts.size(); i++)
/*  665:     */             {
/*  666:1328 */               if (i != 0) {
/*  667:1329 */                 print(" or ");
/*  668:     */               }
/*  669:1331 */               print('"' + (String)accepts.get(i) + '"');
/*  670:     */             }
/*  671:     */           }
/*  672:1334 */           println("here is the dataset:\n");
/*  673:1335 */           println("=== Train Dataset ===\n" + train.toString() + "\n");
/*  674:     */         }
/*  675:     */       }
/*  676:     */     }
/*  677:1340 */     return result;
/*  678:     */   }
/*  679:     */   
/*  680:     */   protected Instances makeTestDataset(int seed, int numInstances, int numNominal, int numNumeric, int numString, int numDate, int numRelational, int numClasses, int classType, boolean multiInstance)
/*  681:     */     throws Exception
/*  682:     */   {
/*  683:1366 */     return makeTestDataset(seed, numInstances, numNominal, numNumeric, numString, numDate, numRelational, numClasses, classType, -1, multiInstance);
/*  684:     */   }
/*  685:     */   
/*  686:     */   protected Instances makeTestDataset(int seed, int numInstances, int numNominal, int numNumeric, int numString, int numDate, int numRelational, int numClasses, int classType, int classIndex, boolean multiInstance)
/*  687:     */     throws Exception
/*  688:     */   {
/*  689:1396 */     TestInstances dataset = new TestInstances();
/*  690:     */     
/*  691:1398 */     dataset.setSeed(seed);
/*  692:1399 */     dataset.setNumInstances(numInstances);
/*  693:1400 */     dataset.setNumNominal(numNominal);
/*  694:1401 */     dataset.setNumNumeric(numNumeric);
/*  695:1402 */     dataset.setNumString(numString);
/*  696:1403 */     dataset.setNumDate(numDate);
/*  697:1404 */     dataset.setNumRelational(numRelational);
/*  698:1405 */     dataset.setNumClasses(numClasses);
/*  699:1406 */     if (classType == -1)
/*  700:     */     {
/*  701:1407 */       dataset.setClassType(1);
/*  702:1408 */       dataset.setClassIndex(-2);
/*  703:     */     }
/*  704:     */     else
/*  705:     */     {
/*  706:1410 */       dataset.setClassType(classType);
/*  707:1411 */       dataset.setClassIndex(classIndex);
/*  708:     */     }
/*  709:1413 */     dataset.setNumClasses(numClasses);
/*  710:1414 */     dataset.setMultiInstance(multiInstance);
/*  711:1415 */     dataset.setWords(getWords());
/*  712:1416 */     dataset.setWordSeparators(getWordSeparators());
/*  713:     */     
/*  714:1418 */     return process(dataset.generate());
/*  715:     */   }
/*  716:     */   
/*  717:     */   protected void printAttributeSummary(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType)
/*  718:     */   {
/*  719:1437 */     String str = "";
/*  720:1439 */     if (numericPredictor) {
/*  721:1440 */       str = str + " numeric";
/*  722:     */     }
/*  723:1443 */     if (nominalPredictor)
/*  724:     */     {
/*  725:1444 */       if (str.length() > 0) {
/*  726:1445 */         str = str + " &";
/*  727:     */       }
/*  728:1447 */       str = str + " nominal";
/*  729:     */     }
/*  730:1450 */     if (stringPredictor)
/*  731:     */     {
/*  732:1451 */       if (str.length() > 0) {
/*  733:1452 */         str = str + " &";
/*  734:     */       }
/*  735:1454 */       str = str + " string";
/*  736:     */     }
/*  737:1457 */     if (datePredictor)
/*  738:     */     {
/*  739:1458 */       if (str.length() > 0) {
/*  740:1459 */         str = str + " &";
/*  741:     */       }
/*  742:1461 */       str = str + " date";
/*  743:     */     }
/*  744:1464 */     if (relationalPredictor)
/*  745:     */     {
/*  746:1465 */       if (str.length() > 0) {
/*  747:1466 */         str = str + " &";
/*  748:     */       }
/*  749:1468 */       str = str + " relational";
/*  750:     */     }
/*  751:1471 */     str = str + " predictors)";
/*  752:1473 */     switch (classType)
/*  753:     */     {
/*  754:     */     case 0: 
/*  755:1475 */       str = " (numeric class," + str;
/*  756:1476 */       break;
/*  757:     */     case 1: 
/*  758:1478 */       str = " (nominal class," + str;
/*  759:1479 */       break;
/*  760:     */     case 2: 
/*  761:1481 */       str = " (string class," + str;
/*  762:1482 */       break;
/*  763:     */     case 3: 
/*  764:1484 */       str = " (date class," + str;
/*  765:1485 */       break;
/*  766:     */     case 4: 
/*  767:1487 */       str = " (relational class," + str;
/*  768:1488 */       break;
/*  769:     */     case -1: 
/*  770:1490 */       str = " (no class," + str;
/*  771:     */     }
/*  772:1494 */     print(str);
/*  773:     */   }
/*  774:     */   
/*  775:     */   public String getRevision()
/*  776:     */   {
/*  777:1504 */     return RevisionUtils.extract("$Revision: 11247 $");
/*  778:     */   }
/*  779:     */   
/*  780:     */   public static void main(String[] args)
/*  781:     */   {
/*  782:1513 */     runCheck(new CheckAssociator(), args);
/*  783:     */   }
/*  784:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.associations.CheckAssociator
 * JD-Core Version:    0.7.0.1
 */