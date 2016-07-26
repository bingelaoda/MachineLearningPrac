/*    1:     */ package weka.attributeSelection;
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
/*   14:     */ import weka.core.RevisionUtils;
/*   15:     */ import weka.core.SerializationHelper;
/*   16:     */ import weka.core.SerializedObject;
/*   17:     */ import weka.core.TestInstances;
/*   18:     */ import weka.core.Utils;
/*   19:     */ import weka.core.WeightedInstancesHandler;
/*   20:     */ 
/*   21:     */ public class CheckAttributeSelection
/*   22:     */   extends CheckScheme
/*   23:     */ {
/*   24: 239 */   protected ASEvaluation m_Evaluator = new CfsSubsetEval();
/*   25: 242 */   protected ASSearch m_Search = new Ranker();
/*   26: 245 */   protected boolean m_TestEvaluator = true;
/*   27:     */   
/*   28:     */   public Enumeration<Option> listOptions()
/*   29:     */   {
/*   30: 254 */     Vector<Option> result = new Vector();
/*   31:     */     
/*   32: 256 */     result.add(new Option("\tFull name and options of the evaluator analyzed.\n\teg: weka.attributeSelection.CfsSubsetEval", "eval", 1, "-eval name [options]"));
/*   33:     */     
/*   34:     */ 
/*   35:     */ 
/*   36:     */ 
/*   37: 261 */     result.add(new Option("\tFull name and options of the search method analyzed.\n\teg: weka.attributeSelection.Ranker", "search", 1, "-search name [options]"));
/*   38:     */     
/*   39:     */ 
/*   40:     */ 
/*   41:     */ 
/*   42: 266 */     result.add(new Option("\tThe scheme to test, either the evaluator or the search method.\n\t(Default: eval)", "test", 1, "-test <eval|search>"));
/*   43:     */     
/*   44:     */ 
/*   45:     */ 
/*   46: 270 */     result.addAll(Collections.list(super.listOptions()));
/*   47: 272 */     if ((this.m_Evaluator != null) && ((this.m_Evaluator instanceof OptionHandler)))
/*   48:     */     {
/*   49: 273 */       result.add(new Option("", "", 0, "\nOptions specific to evaluator " + this.m_Evaluator.getClass().getName() + ":"));
/*   50:     */       
/*   51: 275 */       result.addAll(Collections.list(((OptionHandler)this.m_Evaluator).listOptions()));
/*   52:     */     }
/*   53: 279 */     if ((this.m_Search != null) && ((this.m_Search instanceof OptionHandler)))
/*   54:     */     {
/*   55: 280 */       result.add(new Option("", "", 0, "\nOptions specific to search method " + this.m_Search.getClass().getName() + ":"));
/*   56:     */       
/*   57: 282 */       result.addAll(Collections.list(((OptionHandler)this.m_Search).listOptions()));
/*   58:     */     }
/*   59: 285 */     return result.elements();
/*   60:     */   }
/*   61:     */   
/*   62:     */   public void setOptions(String[] options)
/*   63:     */     throws Exception
/*   64:     */   {
/*   65: 420 */     super.setOptions(options);
/*   66:     */     
/*   67: 422 */     String tmpStr = Utils.getOption("eval", options);
/*   68: 423 */     String[] tmpOptions = Utils.splitOptions(tmpStr);
/*   69: 424 */     if (tmpOptions.length != 0)
/*   70:     */     {
/*   71: 425 */       tmpStr = tmpOptions[0];
/*   72: 426 */       tmpOptions[0] = "";
/*   73: 427 */       setEvaluator((ASEvaluation)forName("weka.attributeSelection", ASEvaluation.class, tmpStr, tmpOptions));
/*   74:     */     }
/*   75: 431 */     tmpStr = Utils.getOption("search", options);
/*   76: 432 */     tmpOptions = Utils.splitOptions(tmpStr);
/*   77: 433 */     if (tmpOptions.length != 0)
/*   78:     */     {
/*   79: 434 */       tmpStr = tmpOptions[0];
/*   80: 435 */       tmpOptions[0] = "";
/*   81: 436 */       setSearch((ASSearch)forName("weka.attributeSelection", ASSearch.class, tmpStr, tmpOptions));
/*   82:     */     }
/*   83: 440 */     tmpStr = Utils.getOption("test", options);
/*   84: 441 */     setTestEvaluator(!tmpStr.equalsIgnoreCase("search"));
/*   85:     */   }
/*   86:     */   
/*   87:     */   public String[] getOptions()
/*   88:     */   {
/*   89: 451 */     Vector<String> result = new Vector();
/*   90:     */     
/*   91: 453 */     Collections.addAll(result, super.getOptions());
/*   92:     */     
/*   93: 455 */     result.add("-eval");
/*   94: 456 */     if ((getEvaluator() instanceof OptionHandler)) {
/*   95: 457 */       result.add(getEvaluator().getClass().getName() + " " + Utils.joinOptions(((OptionHandler)getEvaluator()).getOptions()));
/*   96:     */     } else {
/*   97: 460 */       result.add(getEvaluator().getClass().getName());
/*   98:     */     }
/*   99: 463 */     result.add("-search");
/*  100: 464 */     if ((getSearch() instanceof OptionHandler)) {
/*  101: 465 */       result.add(getSearch().getClass().getName() + " " + Utils.joinOptions(((OptionHandler)getSearch()).getOptions()));
/*  102:     */     } else {
/*  103: 468 */       result.add(getSearch().getClass().getName());
/*  104:     */     }
/*  105: 471 */     result.add("-test");
/*  106: 472 */     if (getTestEvaluator()) {
/*  107: 473 */       result.add("eval");
/*  108:     */     } else {
/*  109: 475 */       result.add("search");
/*  110:     */     }
/*  111: 478 */     return (String[])result.toArray(new String[result.size()]);
/*  112:     */   }
/*  113:     */   
/*  114:     */   public void doTests()
/*  115:     */   {
/*  116: 487 */     if (getTestObject() == null)
/*  117:     */     {
/*  118: 488 */       println("\n=== No scheme set ===");
/*  119: 489 */       return;
/*  120:     */     }
/*  121: 491 */     println("\n=== Check on scheme: " + getTestObject().getClass().getName() + " ===\n");
/*  122:     */     
/*  123:     */ 
/*  124:     */ 
/*  125: 495 */     this.m_ClasspathProblems = false;
/*  126: 496 */     println("--> Checking for interfaces");
/*  127: 497 */     canTakeOptions();
/*  128: 498 */     boolean weightedInstancesHandler = weightedInstancesHandler()[0];
/*  129: 499 */     boolean multiInstanceHandler = multiInstanceHandler()[0];
/*  130: 500 */     println("--> Scheme tests");
/*  131: 501 */     declaresSerialVersionUID();
/*  132: 502 */     testsPerClassType(1, weightedInstancesHandler, multiInstanceHandler);
/*  133:     */     
/*  134: 504 */     testsPerClassType(0, weightedInstancesHandler, multiInstanceHandler);
/*  135:     */     
/*  136: 506 */     testsPerClassType(3, weightedInstancesHandler, multiInstanceHandler);
/*  137:     */     
/*  138: 508 */     testsPerClassType(2, weightedInstancesHandler, multiInstanceHandler);
/*  139:     */     
/*  140: 510 */     testsPerClassType(4, weightedInstancesHandler, multiInstanceHandler);
/*  141:     */   }
/*  142:     */   
/*  143:     */   public void setEvaluator(ASEvaluation value)
/*  144:     */   {
/*  145: 520 */     this.m_Evaluator = value;
/*  146:     */   }
/*  147:     */   
/*  148:     */   public ASEvaluation getEvaluator()
/*  149:     */   {
/*  150: 529 */     return this.m_Evaluator;
/*  151:     */   }
/*  152:     */   
/*  153:     */   public void setSearch(ASSearch value)
/*  154:     */   {
/*  155: 538 */     this.m_Search = value;
/*  156:     */   }
/*  157:     */   
/*  158:     */   public ASSearch getSearch()
/*  159:     */   {
/*  160: 547 */     return this.m_Search;
/*  161:     */   }
/*  162:     */   
/*  163:     */   public void setTestEvaluator(boolean value)
/*  164:     */   {
/*  165: 556 */     this.m_TestEvaluator = value;
/*  166:     */   }
/*  167:     */   
/*  168:     */   public boolean getTestEvaluator()
/*  169:     */   {
/*  170: 565 */     return this.m_TestEvaluator;
/*  171:     */   }
/*  172:     */   
/*  173:     */   protected Object getTestObject()
/*  174:     */   {
/*  175: 575 */     if (getTestEvaluator()) {
/*  176: 576 */       return getEvaluator();
/*  177:     */     }
/*  178: 578 */     return getSearch();
/*  179:     */   }
/*  180:     */   
/*  181:     */   protected Object[] makeCopies(Object obj, int num)
/*  182:     */     throws Exception
/*  183:     */   {
/*  184: 591 */     if (obj == null) {
/*  185: 592 */       throw new Exception("No object set");
/*  186:     */     }
/*  187: 595 */     Object[] objs = new Object[num];
/*  188: 596 */     SerializedObject so = new SerializedObject(obj);
/*  189: 597 */     for (int i = 0; i < objs.length; i++) {
/*  190: 598 */       objs[i] = so.getObject();
/*  191:     */     }
/*  192: 601 */     return objs;
/*  193:     */   }
/*  194:     */   
/*  195:     */   protected AttributeSelection search(ASSearch search, ASEvaluation eval, Instances data)
/*  196:     */     throws Exception
/*  197:     */   {
/*  198: 619 */     AttributeSelection result = new AttributeSelection();
/*  199: 620 */     result.setSeed(42);
/*  200: 621 */     result.setSearch(search);
/*  201: 622 */     result.setEvaluator(eval);
/*  202: 623 */     result.SelectAttributes(data);
/*  203:     */     
/*  204: 625 */     return result;
/*  205:     */   }
/*  206:     */   
/*  207:     */   protected void testsPerClassType(int classType, boolean weighted, boolean multiInstance)
/*  208:     */   {
/*  209: 638 */     boolean PNom = canPredict(true, false, false, false, false, multiInstance, classType)[0];
/*  210:     */     
/*  211: 640 */     boolean PNum = canPredict(false, true, false, false, false, multiInstance, classType)[0];
/*  212:     */     
/*  213: 642 */     boolean PStr = canPredict(false, false, true, false, false, multiInstance, classType)[0];
/*  214:     */     
/*  215: 644 */     boolean PDat = canPredict(false, false, false, true, false, multiInstance, classType)[0];
/*  216:     */     boolean PRel;
/*  217:     */     boolean PRel;
/*  218: 647 */     if (!multiInstance) {
/*  219: 648 */       PRel = canPredict(false, false, false, false, true, multiInstance, classType)[0];
/*  220:     */     } else {
/*  221: 651 */       PRel = false;
/*  222:     */     }
/*  223: 654 */     if ((PNom) || (PNum) || (PStr) || (PDat) || (PRel))
/*  224:     */     {
/*  225: 655 */       if (weighted) {
/*  226: 656 */         instanceWeights(PNom, PNum, PStr, PDat, PRel, multiInstance, classType);
/*  227:     */       }
/*  228: 659 */       if (classType == 1) {
/*  229: 660 */         canHandleNClasses(PNom, PNum, PStr, PDat, PRel, multiInstance, 4);
/*  230:     */       }
/*  231: 663 */       if (!multiInstance)
/*  232:     */       {
/*  233: 664 */         canHandleClassAsNthAttribute(PNom, PNum, PStr, PDat, PRel, multiInstance, classType, 0);
/*  234:     */         
/*  235: 666 */         canHandleClassAsNthAttribute(PNom, PNum, PStr, PDat, PRel, multiInstance, classType, 1);
/*  236:     */       }
/*  237: 670 */       canHandleZeroTraining(PNom, PNum, PStr, PDat, PRel, multiInstance, classType);
/*  238:     */       
/*  239: 672 */       boolean handleMissingPredictors = canHandleMissing(PNom, PNum, PStr, PDat, PRel, multiInstance, classType, true, false, 20)[0];
/*  240: 674 */       if (handleMissingPredictors) {
/*  241: 675 */         canHandleMissing(PNom, PNum, PStr, PDat, PRel, multiInstance, classType, true, false, 100);
/*  242:     */       }
/*  243: 679 */       boolean handleMissingClass = canHandleMissing(PNom, PNum, PStr, PDat, PRel, multiInstance, classType, false, true, 20)[0];
/*  244: 681 */       if (handleMissingClass) {
/*  245: 682 */         canHandleMissing(PNom, PNum, PStr, PDat, PRel, multiInstance, classType, false, true, 100);
/*  246:     */       }
/*  247: 686 */       correctSearchInitialisation(PNom, PNum, PStr, PDat, PRel, multiInstance, classType);
/*  248:     */       
/*  249: 688 */       datasetIntegrity(PNom, PNum, PStr, PDat, PRel, multiInstance, classType, handleMissingPredictors, handleMissingClass);
/*  250:     */     }
/*  251:     */   }
/*  252:     */   
/*  253:     */   protected boolean[] canTakeOptions()
/*  254:     */   {
/*  255: 700 */     boolean[] result = new boolean[2];
/*  256:     */     
/*  257: 702 */     print("options...");
/*  258: 703 */     if ((getTestObject() instanceof OptionHandler))
/*  259:     */     {
/*  260: 704 */       println("yes");
/*  261: 705 */       if (this.m_Debug)
/*  262:     */       {
/*  263: 706 */         println("\n=== Full report ===");
/*  264: 707 */         Enumeration<Option> enu = ((OptionHandler)getTestObject()).listOptions();
/*  265: 709 */         while (enu.hasMoreElements())
/*  266:     */         {
/*  267: 710 */           Option option = (Option)enu.nextElement();
/*  268: 711 */           print(option.synopsis() + "\n" + option.description() + "\n");
/*  269:     */         }
/*  270: 713 */         println("\n");
/*  271:     */       }
/*  272: 715 */       result[0] = true;
/*  273:     */     }
/*  274:     */     else
/*  275:     */     {
/*  276: 717 */       println("no");
/*  277: 718 */       result[0] = false;
/*  278:     */     }
/*  279: 721 */     return result;
/*  280:     */   }
/*  281:     */   
/*  282:     */   protected boolean[] weightedInstancesHandler()
/*  283:     */   {
/*  284: 731 */     boolean[] result = new boolean[2];
/*  285:     */     
/*  286: 733 */     print("weighted instances scheme...");
/*  287: 734 */     if ((getTestObject() instanceof WeightedInstancesHandler))
/*  288:     */     {
/*  289: 735 */       println("yes");
/*  290: 736 */       result[0] = true;
/*  291:     */     }
/*  292:     */     else
/*  293:     */     {
/*  294: 738 */       println("no");
/*  295: 739 */       result[0] = false;
/*  296:     */     }
/*  297: 742 */     return result;
/*  298:     */   }
/*  299:     */   
/*  300:     */   protected boolean[] multiInstanceHandler()
/*  301:     */   {
/*  302: 751 */     boolean[] result = new boolean[2];
/*  303:     */     
/*  304: 753 */     print("multi-instance scheme...");
/*  305: 754 */     if ((getTestObject() instanceof MultiInstanceCapabilitiesHandler))
/*  306:     */     {
/*  307: 755 */       println("yes");
/*  308: 756 */       result[0] = true;
/*  309:     */     }
/*  310:     */     else
/*  311:     */     {
/*  312: 758 */       println("no");
/*  313: 759 */       result[0] = false;
/*  314:     */     }
/*  315: 762 */     return result;
/*  316:     */   }
/*  317:     */   
/*  318:     */   protected boolean[] declaresSerialVersionUID()
/*  319:     */   {
/*  320: 772 */     boolean[] result = new boolean[2];
/*  321:     */     
/*  322:     */ 
/*  323:     */ 
/*  324: 776 */     print("serialVersionUID...");
/*  325:     */     
/*  326: 778 */     boolean eval = !SerializationHelper.needsUID(this.m_Evaluator.getClass());
/*  327: 779 */     boolean search = !SerializationHelper.needsUID(this.m_Search.getClass());
/*  328:     */     
/*  329: 781 */     result[0] = ((eval) && (search) ? 1 : false);
/*  330: 783 */     if (result[0] != 0) {
/*  331: 784 */       println("yes");
/*  332:     */     } else {
/*  333: 786 */       println("no");
/*  334:     */     }
/*  335: 789 */     return result;
/*  336:     */   }
/*  337:     */   
/*  338:     */   protected boolean[] canPredict(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType)
/*  339:     */   {
/*  340: 809 */     print("basic predict");
/*  341: 810 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType);
/*  342:     */     
/*  343: 812 */     print("...");
/*  344: 813 */     ArrayList<String> accepts = new ArrayList();
/*  345: 814 */     accepts.add("unary");
/*  346: 815 */     accepts.add("binary");
/*  347: 816 */     accepts.add("nominal");
/*  348: 817 */     accepts.add("numeric");
/*  349: 818 */     accepts.add("string");
/*  350: 819 */     accepts.add("date");
/*  351: 820 */     accepts.add("relational");
/*  352: 821 */     accepts.add("multi-instance");
/*  353: 822 */     accepts.add("not in classpath");
/*  354: 823 */     int numTrain = getNumInstances();int numClasses = 2;int missingLevel = 0;
/*  355: 824 */     boolean predictorMissing = false;boolean classMissing = false;
/*  356:     */     
/*  357: 826 */     return runBasicTest(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType, missingLevel, predictorMissing, classMissing, numTrain, numClasses, accepts);
/*  358:     */   }
/*  359:     */   
/*  360:     */   protected boolean[] canHandleNClasses(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int numClasses)
/*  361:     */   {
/*  362: 851 */     print("more than two class problems");
/*  363: 852 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, 1);
/*  364:     */     
/*  365: 854 */     print("...");
/*  366: 855 */     ArrayList<String> accepts = new ArrayList();
/*  367: 856 */     accepts.add("number");
/*  368: 857 */     accepts.add("class");
/*  369: 858 */     int numTrain = getNumInstances();int missingLevel = 0;
/*  370: 859 */     boolean predictorMissing = false;boolean classMissing = false;
/*  371:     */     
/*  372: 861 */     return runBasicTest(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, 1, missingLevel, predictorMissing, classMissing, numTrain, numClasses, accepts);
/*  373:     */   }
/*  374:     */   
/*  375:     */   protected boolean[] canHandleClassAsNthAttribute(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType, int classIndex)
/*  376:     */   {
/*  377: 888 */     if (classIndex == -1) {
/*  378: 889 */       print("class attribute as last attribute");
/*  379:     */     } else {
/*  380: 891 */       print("class attribute as " + (classIndex + 1) + ". attribute");
/*  381:     */     }
/*  382: 893 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType);
/*  383:     */     
/*  384: 895 */     print("...");
/*  385: 896 */     ArrayList<String> accepts = new ArrayList();
/*  386: 897 */     int numTrain = getNumInstances();int numClasses = 2;int missingLevel = 0;
/*  387: 898 */     boolean predictorMissing = false;boolean classMissing = false;
/*  388:     */     
/*  389: 900 */     return runBasicTest(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType, classIndex, missingLevel, predictorMissing, classMissing, numTrain, numClasses, accepts);
/*  390:     */   }
/*  391:     */   
/*  392:     */   protected boolean[] canHandleZeroTraining(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType)
/*  393:     */   {
/*  394: 923 */     print("handle zero training instances");
/*  395: 924 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType);
/*  396:     */     
/*  397: 926 */     print("...");
/*  398: 927 */     ArrayList<String> accepts = new ArrayList();
/*  399: 928 */     accepts.add("train");
/*  400: 929 */     accepts.add("value");
/*  401: 930 */     int numTrain = 0;int numClasses = 2;int missingLevel = 0;
/*  402: 931 */     boolean predictorMissing = false;boolean classMissing = false;
/*  403:     */     
/*  404: 933 */     return runBasicTest(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType, missingLevel, predictorMissing, classMissing, numTrain, numClasses, accepts);
/*  405:     */   }
/*  406:     */   
/*  407:     */   protected boolean[] correctSearchInitialisation(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType)
/*  408:     */   {
/*  409: 959 */     boolean[] result = new boolean[2];
/*  410: 960 */     print("correct initialisation during search");
/*  411: 961 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType);
/*  412:     */     
/*  413: 963 */     print("...");
/*  414: 964 */     int numTrain = getNumInstances();int numClasses = 2;int missingLevel = 0;
/*  415: 965 */     boolean predictorMissing = false;boolean classMissing = false;
/*  416:     */     
/*  417: 967 */     Instances train1 = null;
/*  418: 968 */     Instances train2 = null;
/*  419: 969 */     ASSearch search = null;
/*  420: 970 */     ASEvaluation evaluation1A = null;
/*  421: 971 */     ASEvaluation evaluation1B = null;
/*  422: 972 */     ASEvaluation evaluation2 = null;
/*  423: 973 */     AttributeSelection attsel1A = null;
/*  424: 974 */     AttributeSelection attsel1B = null;
/*  425: 975 */     int stage = 0;
/*  426:     */     try
/*  427:     */     {
/*  428: 979 */       train1 = makeTestDataset(42, numTrain, nominalPredictor ? getNumNominal() : 0, numericPredictor ? getNumNumeric() : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, numClasses, classType, multiInstance);
/*  429:     */       
/*  430:     */ 
/*  431:     */ 
/*  432:     */ 
/*  433: 984 */       train2 = makeTestDataset(84, numTrain, nominalPredictor ? getNumNominal() + 1 : 0, numericPredictor ? getNumNumeric() + 1 : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, numClasses, classType, multiInstance);
/*  434: 990 */       if (missingLevel > 0)
/*  435:     */       {
/*  436: 991 */         addMissing(train1, missingLevel, predictorMissing, classMissing);
/*  437: 992 */         addMissing(train2, missingLevel, predictorMissing, classMissing);
/*  438:     */       }
/*  439: 995 */       search = ASSearch.makeCopies(getSearch(), 1)[0];
/*  440: 996 */       evaluation1A = ASEvaluation.makeCopies(getEvaluator(), 1)[0];
/*  441: 997 */       evaluation1B = ASEvaluation.makeCopies(getEvaluator(), 1)[0];
/*  442: 998 */       evaluation2 = ASEvaluation.makeCopies(getEvaluator(), 1)[0];
/*  443:     */     }
/*  444:     */     catch (Exception ex)
/*  445:     */     {
/*  446:1000 */       throw new Error("Error setting up for tests: " + ex.getMessage());
/*  447:     */     }
/*  448:     */     try
/*  449:     */     {
/*  450:1003 */       stage = 0;
/*  451:1004 */       attsel1A = search(search, evaluation1A, train1);
/*  452:     */       
/*  453:1006 */       stage = 1;
/*  454:1007 */       search(search, evaluation2, train2);
/*  455:     */       
/*  456:1009 */       stage = 2;
/*  457:1010 */       attsel1B = search(search, evaluation1B, train1);
/*  458:     */       
/*  459:1012 */       stage = 3;
/*  460:1013 */       if (!attsel1A.toResultsString().equals(attsel1B.toResultsString()))
/*  461:     */       {
/*  462:1014 */         if (this.m_Debug)
/*  463:     */         {
/*  464:1015 */           println("\n=== Full report ===\n\nFirst search\n" + attsel1A.toResultsString() + "\n\n");
/*  465:     */           
/*  466:1017 */           println("\nSecond search\n" + attsel1B.toResultsString() + "\n\n");
/*  467:     */         }
/*  468:1019 */         throw new Exception("Results differ between search calls");
/*  469:     */       }
/*  470:1021 */       println("yes");
/*  471:1022 */       result[0] = true;
/*  472:     */     }
/*  473:     */     catch (Exception ex)
/*  474:     */     {
/*  475:1024 */       println("no");
/*  476:1025 */       result[0] = false;
/*  477:1026 */       if (this.m_Debug)
/*  478:     */       {
/*  479:1027 */         println("\n=== Full Report ===");
/*  480:1028 */         print("Problem during  training");
/*  481:1029 */         switch (stage)
/*  482:     */         {
/*  483:     */         case 0: 
/*  484:1031 */           print(" of dataset 1");
/*  485:1032 */           break;
/*  486:     */         case 1: 
/*  487:1034 */           print(" of dataset 2");
/*  488:1035 */           break;
/*  489:     */         case 2: 
/*  490:1037 */           print(" of dataset 1 (2nd build)");
/*  491:1038 */           break;
/*  492:     */         case 3: 
/*  493:1040 */           print(", comparing results from builds of dataset 1");
/*  494:     */         }
/*  495:1043 */         println(": " + ex.getMessage() + "\n");
/*  496:1044 */         println("here are the datasets:\n");
/*  497:1045 */         println("=== Train1 Dataset ===\n" + train1.toString() + "\n");
/*  498:1046 */         println("=== Train2 Dataset ===\n" + train2.toString() + "\n");
/*  499:     */       }
/*  500:     */     }
/*  501:1050 */     return result;
/*  502:     */   }
/*  503:     */   
/*  504:     */   protected boolean[] canHandleMissing(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType, boolean predictorMissing, boolean classMissing, int missingLevel)
/*  505:     */   {
/*  506:1075 */     if (missingLevel == 100) {
/*  507:1076 */       print("100% ");
/*  508:     */     }
/*  509:1078 */     print("missing");
/*  510:1079 */     if (predictorMissing)
/*  511:     */     {
/*  512:1080 */       print(" predictor");
/*  513:1081 */       if (classMissing) {
/*  514:1082 */         print(" and");
/*  515:     */       }
/*  516:     */     }
/*  517:1085 */     if (classMissing) {
/*  518:1086 */       print(" class");
/*  519:     */     }
/*  520:1088 */     print(" values");
/*  521:1089 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType);
/*  522:     */     
/*  523:1091 */     print("...");
/*  524:1092 */     ArrayList<String> accepts = new ArrayList();
/*  525:1093 */     accepts.add("missing");
/*  526:1094 */     accepts.add("value");
/*  527:1095 */     accepts.add("train");
/*  528:1096 */     accepts.add("no attributes");
/*  529:1097 */     int numTrain = getNumInstances();int numClasses = 2;
/*  530:     */     
/*  531:1099 */     return runBasicTest(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType, missingLevel, predictorMissing, classMissing, numTrain, numClasses, accepts);
/*  532:     */   }
/*  533:     */   
/*  534:     */   protected boolean[] instanceWeights(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType)
/*  535:     */   {
/*  536:1127 */     print("scheme uses instance weights");
/*  537:1128 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType);
/*  538:     */     
/*  539:1130 */     print("...");
/*  540:1131 */     int numTrain = 2 * getNumInstances();int numClasses = 2;int missingLevel = 0;
/*  541:1132 */     boolean predictorMissing = false;boolean classMissing = false;
/*  542:     */     
/*  543:1134 */     boolean[] result = new boolean[2];
/*  544:1135 */     Instances train = null;
/*  545:1136 */     ASSearch[] search = null;
/*  546:1137 */     ASEvaluation evaluationB = null;
/*  547:1138 */     ASEvaluation evaluationI = null;
/*  548:1139 */     AttributeSelection attselB = null;
/*  549:1140 */     AttributeSelection attselI = null;
/*  550:1141 */     boolean evalFail = false;
/*  551:     */     try
/*  552:     */     {
/*  553:1143 */       train = makeTestDataset(42, numTrain, nominalPredictor ? getNumNominal() + 1 : 0, numericPredictor ? getNumNumeric() + 1 : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, numClasses, classType, multiInstance);
/*  554:1149 */       if (missingLevel > 0) {
/*  555:1150 */         addMissing(train, missingLevel, predictorMissing, classMissing);
/*  556:     */       }
/*  557:1152 */       search = ASSearch.makeCopies(getSearch(), 2);
/*  558:1153 */       evaluationB = ASEvaluation.makeCopies(getEvaluator(), 1)[0];
/*  559:1154 */       evaluationI = ASEvaluation.makeCopies(getEvaluator(), 1)[0];
/*  560:1155 */       attselB = search(search[0], evaluationB, train);
/*  561:     */     }
/*  562:     */     catch (Exception ex)
/*  563:     */     {
/*  564:1157 */       throw new Error("Error setting up for tests: " + ex.getMessage());
/*  565:     */     }
/*  566:     */     try
/*  567:     */     {
/*  568:1162 */       for (int i = 0; i < train.numInstances(); i++) {
/*  569:1163 */         train.instance(i).setWeight(0.0D);
/*  570:     */       }
/*  571:1165 */       Random random = new Random(1L);
/*  572:1166 */       for (int i = 0; i < train.numInstances() / 2; i++)
/*  573:     */       {
/*  574:1167 */         int inst = random.nextInt(train.numInstances());
/*  575:1168 */         int weight = random.nextInt(10) + 1;
/*  576:1169 */         train.instance(inst).setWeight(weight);
/*  577:     */       }
/*  578:1171 */       attselI = search(search[1], evaluationI, train);
/*  579:1172 */       if (attselB.toResultsString().equals(attselI.toResultsString()))
/*  580:     */       {
/*  581:1174 */         evalFail = true;
/*  582:1175 */         throw new Exception("evalFail");
/*  583:     */       }
/*  584:1178 */       println("yes");
/*  585:1179 */       result[0] = true;
/*  586:     */     }
/*  587:     */     catch (Exception ex)
/*  588:     */     {
/*  589:1181 */       println("no");
/*  590:1182 */       result[0] = false;
/*  591:1184 */       if (!this.m_Debug) {
/*  592:     */         break label622;
/*  593:     */       }
/*  594:     */     }
/*  595:1185 */     println("\n=== Full Report ===");
/*  596:1187 */     if (evalFail)
/*  597:     */     {
/*  598:1188 */       println("Results don't differ between non-weighted and weighted instance models.");
/*  599:     */       
/*  600:1190 */       println("Here are the results:\n");
/*  601:1191 */       println("\nboth methods\n");
/*  602:1192 */       println(evaluationB.toString());
/*  603:     */     }
/*  604:     */     else
/*  605:     */     {
/*  606:1194 */       print("Problem during training");
/*  607:1195 */       println(": " + ex.getMessage() + "\n");
/*  608:     */     }
/*  609:1197 */     println("Here is the dataset:\n");
/*  610:1198 */     println("=== Train Dataset ===\n" + train.toString() + "\n");
/*  611:1199 */     println("=== Train Weights ===\n");
/*  612:1200 */     for (int i = 0; i < train.numInstances(); i++) {
/*  613:1201 */       println(" " + (i + 1) + "    " + train.instance(i).weight());
/*  614:     */     }
/*  615:     */     label622:
/*  616:1206 */     return result;
/*  617:     */   }
/*  618:     */   
/*  619:     */   protected boolean[] datasetIntegrity(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType, boolean predictorMissing, boolean classMissing)
/*  620:     */   {
/*  621:1233 */     print("scheme doesn't alter original datasets");
/*  622:1234 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType);
/*  623:     */     
/*  624:1236 */     print("...");
/*  625:1237 */     int numTrain = getNumInstances();int numClasses = 2;int missingLevel = 20;
/*  626:     */     
/*  627:1239 */     boolean[] result = new boolean[2];
/*  628:1240 */     Instances train = null;
/*  629:1241 */     Instances trainCopy = null;
/*  630:1242 */     ASSearch search = null;
/*  631:1243 */     ASEvaluation evaluation = null;
/*  632:     */     try
/*  633:     */     {
/*  634:1245 */       train = makeTestDataset(42, numTrain, nominalPredictor ? getNumNominal() : 0, numericPredictor ? getNumNumeric() : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, numClasses, classType, multiInstance);
/*  635:1250 */       if (missingLevel > 0) {
/*  636:1251 */         addMissing(train, missingLevel, predictorMissing, classMissing);
/*  637:     */       }
/*  638:1253 */       search = ASSearch.makeCopies(getSearch(), 1)[0];
/*  639:1254 */       evaluation = ASEvaluation.makeCopies(getEvaluator(), 1)[0];
/*  640:1255 */       trainCopy = new Instances(train);
/*  641:     */     }
/*  642:     */     catch (Exception ex)
/*  643:     */     {
/*  644:1257 */       throw new Error("Error setting up for tests: " + ex.getMessage());
/*  645:     */     }
/*  646:     */     try
/*  647:     */     {
/*  648:1260 */       search(search, evaluation, trainCopy);
/*  649:1261 */       compareDatasets(train, trainCopy);
/*  650:     */       
/*  651:1263 */       println("yes");
/*  652:1264 */       result[0] = true;
/*  653:     */     }
/*  654:     */     catch (Exception ex)
/*  655:     */     {
/*  656:1266 */       println("no");
/*  657:1267 */       result[0] = false;
/*  658:1269 */       if (this.m_Debug)
/*  659:     */       {
/*  660:1270 */         println("\n=== Full Report ===");
/*  661:1271 */         print("Problem during training");
/*  662:1272 */         println(": " + ex.getMessage() + "\n");
/*  663:1273 */         println("Here are the datasets:\n");
/*  664:1274 */         println("=== Train Dataset (original) ===\n" + trainCopy.toString() + "\n");
/*  665:     */         
/*  666:1276 */         println("=== Train Dataset ===\n" + train.toString() + "\n");
/*  667:     */       }
/*  668:     */     }
/*  669:1280 */     return result;
/*  670:     */   }
/*  671:     */   
/*  672:     */   protected boolean[] runBasicTest(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType, int missingLevel, boolean predictorMissing, boolean classMissing, int numTrain, int numClasses, ArrayList<String> accepts)
/*  673:     */   {
/*  674:1308 */     return runBasicTest(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType, -1, missingLevel, predictorMissing, classMissing, numTrain, numClasses, accepts);
/*  675:     */   }
/*  676:     */   
/*  677:     */   protected boolean[] runBasicTest(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType, int classIndex, int missingLevel, boolean predictorMissing, boolean classMissing, int numTrain, int numClasses, ArrayList<String> accepts)
/*  678:     */   {
/*  679:1341 */     boolean[] result = new boolean[2];
/*  680:1342 */     Instances train = null;
/*  681:1343 */     ASSearch search = null;
/*  682:1344 */     ASEvaluation evaluation = null;
/*  683:     */     try
/*  684:     */     {
/*  685:1346 */       train = makeTestDataset(42, numTrain, nominalPredictor ? getNumNominal() : 0, numericPredictor ? getNumNumeric() : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, numClasses, classType, classIndex, multiInstance);
/*  686:1351 */       if (missingLevel > 0) {
/*  687:1352 */         addMissing(train, missingLevel, predictorMissing, classMissing);
/*  688:     */       }
/*  689:1354 */       search = ASSearch.makeCopies(getSearch(), 1)[0];
/*  690:1355 */       evaluation = ASEvaluation.makeCopies(getEvaluator(), 1)[0];
/*  691:     */     }
/*  692:     */     catch (Exception ex)
/*  693:     */     {
/*  694:1357 */       ex.printStackTrace();
/*  695:1358 */       throw new Error("Error setting up for tests: " + ex.getMessage());
/*  696:     */     }
/*  697:     */     try
/*  698:     */     {
/*  699:1361 */       search(search, evaluation, train);
/*  700:1362 */       println("yes");
/*  701:1363 */       result[0] = true;
/*  702:     */     }
/*  703:     */     catch (Exception ex)
/*  704:     */     {
/*  705:1365 */       boolean acceptable = false;
/*  706:     */       String msg;
/*  707:     */       String msg;
/*  708:1367 */       if (ex.getMessage() == null) {
/*  709:1368 */         msg = "";
/*  710:     */       } else {
/*  711:1370 */         msg = ex.getMessage().toLowerCase();
/*  712:     */       }
/*  713:1372 */       if (msg.indexOf("not in classpath") > -1) {
/*  714:1373 */         this.m_ClasspathProblems = true;
/*  715:     */       }
/*  716:1375 */       for (int i = 0; i < accepts.size(); i++) {
/*  717:1376 */         if (msg.indexOf((String)accepts.get(i)) >= 0) {
/*  718:1377 */           acceptable = true;
/*  719:     */         }
/*  720:     */       }
/*  721:1381 */       println("no" + (acceptable ? " (OK error message)" : ""));
/*  722:1382 */       result[1] = acceptable;
/*  723:1384 */       if (this.m_Debug)
/*  724:     */       {
/*  725:1385 */         println("\n=== Full Report ===");
/*  726:1386 */         print("Problem during training");
/*  727:1387 */         println(": " + ex.getMessage() + "\n");
/*  728:1388 */         if (!acceptable)
/*  729:     */         {
/*  730:1389 */           if (accepts.size() > 0)
/*  731:     */           {
/*  732:1390 */             print("Error message doesn't mention ");
/*  733:1391 */             for (int i = 0; i < accepts.size(); i++)
/*  734:     */             {
/*  735:1392 */               if (i != 0) {
/*  736:1393 */                 print(" or ");
/*  737:     */               }
/*  738:1395 */               print('"' + (String)accepts.get(i) + '"');
/*  739:     */             }
/*  740:     */           }
/*  741:1398 */           println("here is the dataset:\n");
/*  742:1399 */           println("=== Train Dataset ===\n" + train.toString() + "\n");
/*  743:     */         }
/*  744:     */       }
/*  745:     */     }
/*  746:1404 */     return result;
/*  747:     */   }
/*  748:     */   
/*  749:     */   protected Instances makeTestDataset(int seed, int numInstances, int numNominal, int numNumeric, int numString, int numDate, int numRelational, int numClasses, int classType, boolean multiInstance)
/*  750:     */     throws Exception
/*  751:     */   {
/*  752:1430 */     return makeTestDataset(seed, numInstances, numNominal, numNumeric, numString, numDate, numRelational, numClasses, classType, -1, multiInstance);
/*  753:     */   }
/*  754:     */   
/*  755:     */   protected Instances makeTestDataset(int seed, int numInstances, int numNominal, int numNumeric, int numString, int numDate, int numRelational, int numClasses, int classType, int classIndex, boolean multiInstance)
/*  756:     */     throws Exception
/*  757:     */   {
/*  758:1460 */     TestInstances dataset = new TestInstances();
/*  759:     */     
/*  760:1462 */     dataset.setSeed(seed);
/*  761:1463 */     dataset.setNumInstances(numInstances);
/*  762:1464 */     dataset.setNumNominal(numNominal);
/*  763:1465 */     dataset.setNumNumeric(numNumeric);
/*  764:1466 */     dataset.setNumString(numString);
/*  765:1467 */     dataset.setNumDate(numDate);
/*  766:1468 */     dataset.setNumRelational(numRelational);
/*  767:1469 */     dataset.setNumClasses(numClasses);
/*  768:1470 */     dataset.setClassType(classType);
/*  769:1471 */     dataset.setClassIndex(classIndex);
/*  770:1472 */     dataset.setNumClasses(numClasses);
/*  771:1473 */     dataset.setMultiInstance(multiInstance);
/*  772:1474 */     dataset.setWords(getWords());
/*  773:1475 */     dataset.setWordSeparators(getWordSeparators());
/*  774:     */     
/*  775:1477 */     return process(dataset.generate());
/*  776:     */   }
/*  777:     */   
/*  778:     */   protected void printAttributeSummary(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType)
/*  779:     */   {
/*  780:1496 */     String str = "";
/*  781:1498 */     if (numericPredictor) {
/*  782:1499 */       str = str + " numeric";
/*  783:     */     }
/*  784:1502 */     if (nominalPredictor)
/*  785:     */     {
/*  786:1503 */       if (str.length() > 0) {
/*  787:1504 */         str = str + " &";
/*  788:     */       }
/*  789:1506 */       str = str + " nominal";
/*  790:     */     }
/*  791:1509 */     if (stringPredictor)
/*  792:     */     {
/*  793:1510 */       if (str.length() > 0) {
/*  794:1511 */         str = str + " &";
/*  795:     */       }
/*  796:1513 */       str = str + " string";
/*  797:     */     }
/*  798:1516 */     if (datePredictor)
/*  799:     */     {
/*  800:1517 */       if (str.length() > 0) {
/*  801:1518 */         str = str + " &";
/*  802:     */       }
/*  803:1520 */       str = str + " date";
/*  804:     */     }
/*  805:1523 */     if (relationalPredictor)
/*  806:     */     {
/*  807:1524 */       if (str.length() > 0) {
/*  808:1525 */         str = str + " &";
/*  809:     */       }
/*  810:1527 */       str = str + " relational";
/*  811:     */     }
/*  812:1530 */     str = str + " predictors)";
/*  813:1532 */     switch (classType)
/*  814:     */     {
/*  815:     */     case 0: 
/*  816:1534 */       str = " (numeric class," + str;
/*  817:1535 */       break;
/*  818:     */     case 1: 
/*  819:1537 */       str = " (nominal class," + str;
/*  820:1538 */       break;
/*  821:     */     case 2: 
/*  822:1540 */       str = " (string class," + str;
/*  823:1541 */       break;
/*  824:     */     case 3: 
/*  825:1543 */       str = " (date class," + str;
/*  826:1544 */       break;
/*  827:     */     case 4: 
/*  828:1546 */       str = " (relational class," + str;
/*  829:     */     }
/*  830:1550 */     print(str);
/*  831:     */   }
/*  832:     */   
/*  833:     */   public String getRevision()
/*  834:     */   {
/*  835:1560 */     return RevisionUtils.extract("$Revision: 11247 $");
/*  836:     */   }
/*  837:     */   
/*  838:     */   public static void main(String[] args)
/*  839:     */   {
/*  840:1569 */     runCheck(new CheckAttributeSelection(), args);
/*  841:     */   }
/*  842:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.CheckAttributeSelection
 * JD-Core Version:    0.7.0.1
 */