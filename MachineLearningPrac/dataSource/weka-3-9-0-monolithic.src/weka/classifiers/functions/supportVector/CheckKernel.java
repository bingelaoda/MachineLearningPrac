/*    1:     */ package weka.classifiers.functions.supportVector;
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
/*   16:     */ import weka.core.TestInstances;
/*   17:     */ import weka.core.Utils;
/*   18:     */ import weka.core.WeightedInstancesHandler;
/*   19:     */ 
/*   20:     */ public class CheckKernel
/*   21:     */   extends CheckScheme
/*   22:     */ {
/*   23: 221 */   protected Kernel m_Kernel = new RBFKernel();
/*   24:     */   
/*   25:     */   public Enumeration<Option> listOptions()
/*   26:     */   {
/*   27: 230 */     Vector<Option> result = new Vector();
/*   28:     */     
/*   29: 232 */     result.addElement(new Option("\tFull name of the kernel analysed.\n\teg: weka.classifiers.functions.supportVector.RBFKernel\n\t(default weka.classifiers.functions.supportVector.RBFKernel)", "W", 1, "-W"));
/*   30:     */     
/*   31:     */ 
/*   32:     */ 
/*   33:     */ 
/*   34: 237 */     result.addAll(Collections.list(super.listOptions()));
/*   35: 239 */     if ((this.m_Kernel != null) && ((this.m_Kernel instanceof OptionHandler)))
/*   36:     */     {
/*   37: 240 */       result.addElement(new Option("", "", 0, "\nOptions specific to kernel " + this.m_Kernel.getClass().getName() + ":"));
/*   38:     */       
/*   39: 242 */       result.addAll(Collections.list(this.m_Kernel.listOptions()));
/*   40:     */     }
/*   41: 245 */     return result.elements();
/*   42:     */   }
/*   43:     */   
/*   44:     */   public void setOptions(String[] options)
/*   45:     */     throws Exception
/*   46:     */   {
/*   47: 359 */     super.setOptions(options);
/*   48:     */     
/*   49: 361 */     String tmpStr = Utils.getOption('W', options);
/*   50: 362 */     if (tmpStr.length() == 0) {
/*   51: 363 */       tmpStr = RBFKernel.class.getName();
/*   52:     */     }
/*   53: 366 */     setKernel((Kernel)forName("weka.classifiers.functions.supportVector", Kernel.class, tmpStr, Utils.partitionOptions(options)));
/*   54:     */   }
/*   55:     */   
/*   56:     */   public String[] getOptions()
/*   57:     */   {
/*   58: 377 */     Vector<String> result = new Vector();
/*   59:     */     
/*   60: 379 */     Collections.addAll(result, super.getOptions());
/*   61: 381 */     if (getKernel() != null)
/*   62:     */     {
/*   63: 382 */       result.add("-W");
/*   64: 383 */       result.add(getKernel().getClass().getName());
/*   65:     */     }
/*   66: 386 */     if ((this.m_Kernel != null) && ((this.m_Kernel instanceof OptionHandler)))
/*   67:     */     {
/*   68: 387 */       String[] options = this.m_Kernel.getOptions();
/*   69: 388 */       if (options.length > 0) {
/*   70: 389 */         result.add("--");
/*   71:     */       }
/*   72: 391 */       Collections.addAll(result, options);
/*   73:     */     }
/*   74: 394 */     return (String[])result.toArray(new String[result.size()]);
/*   75:     */   }
/*   76:     */   
/*   77:     */   public void doTests()
/*   78:     */   {
/*   79: 403 */     if (getKernel() == null)
/*   80:     */     {
/*   81: 404 */       println("\n=== No kernel set ===");
/*   82: 405 */       return;
/*   83:     */     }
/*   84: 407 */     println("\n=== Check on kernel: " + getKernel().getClass().getName() + " ===\n");
/*   85:     */     
/*   86:     */ 
/*   87:     */ 
/*   88: 411 */     this.m_ClasspathProblems = false;
/*   89: 412 */     println("--> Checking for interfaces");
/*   90: 413 */     canTakeOptions();
/*   91: 414 */     boolean weightedInstancesHandler = weightedInstancesHandler()[0];
/*   92: 415 */     boolean multiInstanceHandler = multiInstanceHandler()[0];
/*   93: 416 */     println("--> Kernel tests");
/*   94: 417 */     declaresSerialVersionUID();
/*   95: 418 */     testsPerClassType(1, weightedInstancesHandler, multiInstanceHandler);
/*   96:     */     
/*   97: 420 */     testsPerClassType(0, weightedInstancesHandler, multiInstanceHandler);
/*   98:     */     
/*   99: 422 */     testsPerClassType(3, weightedInstancesHandler, multiInstanceHandler);
/*  100:     */     
/*  101: 424 */     testsPerClassType(2, weightedInstancesHandler, multiInstanceHandler);
/*  102:     */     
/*  103: 426 */     testsPerClassType(4, weightedInstancesHandler, multiInstanceHandler);
/*  104:     */   }
/*  105:     */   
/*  106:     */   public void setKernel(Kernel value)
/*  107:     */   {
/*  108: 436 */     this.m_Kernel = value;
/*  109:     */   }
/*  110:     */   
/*  111:     */   public Kernel getKernel()
/*  112:     */   {
/*  113: 445 */     return this.m_Kernel;
/*  114:     */   }
/*  115:     */   
/*  116:     */   protected void testsPerClassType(int classType, boolean weighted, boolean multiInstance)
/*  117:     */   {
/*  118: 458 */     boolean PNom = canPredict(true, false, false, false, false, multiInstance, classType)[0];
/*  119:     */     
/*  120: 460 */     boolean PNum = canPredict(false, true, false, false, false, multiInstance, classType)[0];
/*  121:     */     
/*  122: 462 */     boolean PStr = canPredict(false, false, true, false, false, multiInstance, classType)[0];
/*  123:     */     
/*  124: 464 */     boolean PDat = canPredict(false, false, false, true, false, multiInstance, classType)[0];
/*  125:     */     boolean PRel;
/*  126:     */     boolean PRel;
/*  127: 467 */     if (!multiInstance) {
/*  128: 468 */       PRel = canPredict(false, false, false, false, true, multiInstance, classType)[0];
/*  129:     */     } else {
/*  130: 471 */       PRel = false;
/*  131:     */     }
/*  132: 474 */     if ((PNom) || (PNum) || (PStr) || (PDat) || (PRel))
/*  133:     */     {
/*  134: 475 */       if (weighted) {
/*  135: 476 */         instanceWeights(PNom, PNum, PStr, PDat, PRel, multiInstance, classType);
/*  136:     */       }
/*  137: 479 */       if (classType == 1) {
/*  138: 480 */         canHandleNClasses(PNom, PNum, PStr, PDat, PRel, multiInstance, 4);
/*  139:     */       }
/*  140: 483 */       if (!multiInstance)
/*  141:     */       {
/*  142: 484 */         canHandleClassAsNthAttribute(PNom, PNum, PStr, PDat, PRel, multiInstance, classType, 0);
/*  143:     */         
/*  144: 486 */         canHandleClassAsNthAttribute(PNom, PNum, PStr, PDat, PRel, multiInstance, classType, 1);
/*  145:     */       }
/*  146: 490 */       canHandleZeroTraining(PNom, PNum, PStr, PDat, PRel, multiInstance, classType);
/*  147:     */       
/*  148: 492 */       boolean handleMissingPredictors = canHandleMissing(PNom, PNum, PStr, PDat, PRel, multiInstance, classType, true, false, 20)[0];
/*  149: 494 */       if (handleMissingPredictors) {
/*  150: 495 */         canHandleMissing(PNom, PNum, PStr, PDat, PRel, multiInstance, classType, true, false, 100);
/*  151:     */       }
/*  152: 499 */       boolean handleMissingClass = canHandleMissing(PNom, PNum, PStr, PDat, PRel, multiInstance, classType, false, true, 20)[0];
/*  153: 501 */       if (handleMissingClass) {
/*  154: 502 */         canHandleMissing(PNom, PNum, PStr, PDat, PRel, multiInstance, classType, false, true, 100);
/*  155:     */       }
/*  156: 506 */       correctBuildInitialisation(PNom, PNum, PStr, PDat, PRel, multiInstance, classType);
/*  157:     */       
/*  158: 508 */       datasetIntegrity(PNom, PNum, PStr, PDat, PRel, multiInstance, classType, handleMissingPredictors, handleMissingClass);
/*  159:     */     }
/*  160:     */   }
/*  161:     */   
/*  162:     */   protected boolean[] canTakeOptions()
/*  163:     */   {
/*  164: 520 */     boolean[] result = new boolean[2];
/*  165:     */     
/*  166: 522 */     print("options...");
/*  167: 523 */     if ((this.m_Kernel instanceof OptionHandler))
/*  168:     */     {
/*  169: 524 */       println("yes");
/*  170: 525 */       if (this.m_Debug)
/*  171:     */       {
/*  172: 526 */         println("\n=== Full report ===");
/*  173: 527 */         Enumeration<Option> enu = this.m_Kernel.listOptions();
/*  174: 528 */         while (enu.hasMoreElements())
/*  175:     */         {
/*  176: 529 */           Option option = (Option)enu.nextElement();
/*  177: 530 */           print(option.synopsis() + "\n" + option.description() + "\n");
/*  178:     */         }
/*  179: 532 */         println("\n");
/*  180:     */       }
/*  181: 534 */       result[0] = true;
/*  182:     */     }
/*  183:     */     else
/*  184:     */     {
/*  185: 536 */       println("no");
/*  186: 537 */       result[0] = false;
/*  187:     */     }
/*  188: 540 */     return result;
/*  189:     */   }
/*  190:     */   
/*  191:     */   protected boolean[] weightedInstancesHandler()
/*  192:     */   {
/*  193: 550 */     boolean[] result = new boolean[2];
/*  194:     */     
/*  195: 552 */     print("weighted instances kernel...");
/*  196: 553 */     if ((this.m_Kernel instanceof WeightedInstancesHandler))
/*  197:     */     {
/*  198: 554 */       println("yes");
/*  199: 555 */       result[0] = true;
/*  200:     */     }
/*  201:     */     else
/*  202:     */     {
/*  203: 557 */       println("no");
/*  204: 558 */       result[0] = false;
/*  205:     */     }
/*  206: 561 */     return result;
/*  207:     */   }
/*  208:     */   
/*  209:     */   protected boolean[] multiInstanceHandler()
/*  210:     */   {
/*  211: 570 */     boolean[] result = new boolean[2];
/*  212:     */     
/*  213: 572 */     print("multi-instance kernel...");
/*  214: 573 */     if ((this.m_Kernel instanceof MultiInstanceCapabilitiesHandler))
/*  215:     */     {
/*  216: 574 */       println("yes");
/*  217: 575 */       result[0] = true;
/*  218:     */     }
/*  219:     */     else
/*  220:     */     {
/*  221: 577 */       println("no");
/*  222: 578 */       result[0] = false;
/*  223:     */     }
/*  224: 581 */     return result;
/*  225:     */   }
/*  226:     */   
/*  227:     */   protected boolean[] declaresSerialVersionUID()
/*  228:     */   {
/*  229: 591 */     boolean[] result = new boolean[2];
/*  230:     */     
/*  231: 593 */     print("serialVersionUID...");
/*  232:     */     
/*  233: 595 */     result[0] = (!SerializationHelper.needsUID(this.m_Kernel.getClass()) ? 1 : false);
/*  234: 597 */     if (result[0] != 0) {
/*  235: 598 */       println("yes");
/*  236:     */     } else {
/*  237: 600 */       println("no");
/*  238:     */     }
/*  239: 603 */     return result;
/*  240:     */   }
/*  241:     */   
/*  242:     */   protected boolean[] canPredict(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType)
/*  243:     */   {
/*  244: 623 */     print("basic predict");
/*  245: 624 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType);
/*  246:     */     
/*  247: 626 */     print("...");
/*  248: 627 */     ArrayList<String> accepts = new ArrayList();
/*  249: 628 */     accepts.add("unary");
/*  250: 629 */     accepts.add("binary");
/*  251: 630 */     accepts.add("nominal");
/*  252: 631 */     accepts.add("numeric");
/*  253: 632 */     accepts.add("string");
/*  254: 633 */     accepts.add("date");
/*  255: 634 */     accepts.add("relational");
/*  256: 635 */     accepts.add("multi-instance");
/*  257: 636 */     accepts.add("not in classpath");
/*  258: 637 */     int numTrain = getNumInstances();int numClasses = 2;int missingLevel = 0;
/*  259: 638 */     boolean predictorMissing = false;boolean classMissing = false;
/*  260:     */     
/*  261: 640 */     return runBasicTest(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType, missingLevel, predictorMissing, classMissing, numTrain, numClasses, accepts);
/*  262:     */   }
/*  263:     */   
/*  264:     */   protected boolean[] canHandleNClasses(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int numClasses)
/*  265:     */   {
/*  266: 665 */     print("more than two class problems");
/*  267: 666 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, 1);
/*  268:     */     
/*  269: 668 */     print("...");
/*  270: 669 */     ArrayList<String> accepts = new ArrayList();
/*  271: 670 */     accepts.add("number");
/*  272: 671 */     accepts.add("class");
/*  273: 672 */     int numTrain = getNumInstances();int missingLevel = 0;
/*  274: 673 */     boolean predictorMissing = false;boolean classMissing = false;
/*  275:     */     
/*  276: 675 */     return runBasicTest(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, 1, missingLevel, predictorMissing, classMissing, numTrain, numClasses, accepts);
/*  277:     */   }
/*  278:     */   
/*  279:     */   protected boolean[] canHandleClassAsNthAttribute(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType, int classIndex)
/*  280:     */   {
/*  281: 702 */     if (classIndex == -1) {
/*  282: 703 */       print("class attribute as last attribute");
/*  283:     */     } else {
/*  284: 705 */       print("class attribute as " + (classIndex + 1) + ". attribute");
/*  285:     */     }
/*  286: 707 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType);
/*  287:     */     
/*  288: 709 */     print("...");
/*  289: 710 */     ArrayList<String> accepts = new ArrayList();
/*  290: 711 */     int numTrain = getNumInstances();int numClasses = 2;int missingLevel = 0;
/*  291: 712 */     boolean predictorMissing = false;boolean classMissing = false;
/*  292:     */     
/*  293: 714 */     return runBasicTest(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType, classIndex, missingLevel, predictorMissing, classMissing, numTrain, numClasses, accepts);
/*  294:     */   }
/*  295:     */   
/*  296:     */   protected boolean[] canHandleZeroTraining(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType)
/*  297:     */   {
/*  298: 737 */     print("handle zero training instances");
/*  299: 738 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType);
/*  300:     */     
/*  301: 740 */     print("...");
/*  302: 741 */     ArrayList<String> accepts = new ArrayList();
/*  303: 742 */     accepts.add("train");
/*  304: 743 */     accepts.add("value");
/*  305: 744 */     int numTrain = 0;int numClasses = 2;int missingLevel = 0;
/*  306: 745 */     boolean predictorMissing = false;boolean classMissing = false;
/*  307:     */     
/*  308: 747 */     return runBasicTest(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType, missingLevel, predictorMissing, classMissing, numTrain, numClasses, accepts);
/*  309:     */   }
/*  310:     */   
/*  311:     */   protected boolean[] correctBuildInitialisation(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType)
/*  312:     */   {
/*  313: 774 */     boolean[] result = new boolean[2];
/*  314:     */     
/*  315: 776 */     print("correct initialisation during buildKernel");
/*  316: 777 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType);
/*  317:     */     
/*  318: 779 */     print("...");
/*  319: 780 */     int numTrain = getNumInstances();int numClasses = 2;int missingLevel = 0;
/*  320: 781 */     boolean predictorMissing = false;boolean classMissing = false;
/*  321:     */     
/*  322: 783 */     Instances train1 = null;
/*  323: 784 */     Instances train2 = null;
/*  324: 785 */     Kernel kernel = null;
/*  325: 786 */     KernelEvaluation evaluation1A = null;
/*  326: 787 */     KernelEvaluation evaluation1B = null;
/*  327: 788 */     KernelEvaluation evaluation2 = null;
/*  328: 789 */     int stage = 0;
/*  329:     */     try
/*  330:     */     {
/*  331: 794 */       train1 = makeTestDataset(42, numTrain, nominalPredictor ? getNumNominal() : 0, numericPredictor ? getNumNumeric() : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, numClasses, classType, multiInstance);
/*  332:     */       
/*  333:     */ 
/*  334:     */ 
/*  335:     */ 
/*  336: 799 */       train2 = makeTestDataset(84, numTrain, nominalPredictor ? getNumNominal() + 1 : 0, numericPredictor ? getNumNumeric() + 1 : 0, stringPredictor ? getNumString() + 1 : 0, datePredictor ? getNumDate() + 1 : 0, relationalPredictor ? getNumRelational() + 1 : 0, numClasses, classType, multiInstance);
/*  337: 806 */       if (missingLevel > 0)
/*  338:     */       {
/*  339: 807 */         addMissing(train1, missingLevel, predictorMissing, classMissing);
/*  340: 808 */         addMissing(train2, missingLevel, predictorMissing, classMissing);
/*  341:     */       }
/*  342: 811 */       kernel = Kernel.makeCopy(getKernel());
/*  343: 812 */       evaluation1A = new KernelEvaluation();
/*  344: 813 */       evaluation1B = new KernelEvaluation();
/*  345: 814 */       evaluation2 = new KernelEvaluation();
/*  346:     */     }
/*  347:     */     catch (Exception ex)
/*  348:     */     {
/*  349: 816 */       throw new Error("Error setting up for tests: " + ex.getMessage());
/*  350:     */     }
/*  351:     */     try
/*  352:     */     {
/*  353: 819 */       stage = 0;
/*  354: 820 */       evaluation1A.evaluate(kernel, train1);
/*  355:     */       
/*  356: 822 */       stage = 1;
/*  357: 823 */       evaluation2.evaluate(kernel, train2);
/*  358:     */       
/*  359: 825 */       stage = 2;
/*  360: 826 */       evaluation1B.evaluate(kernel, train1);
/*  361:     */       
/*  362: 828 */       stage = 3;
/*  363: 829 */       if (!evaluation1A.equals(evaluation1B))
/*  364:     */       {
/*  365: 830 */         if (this.m_Debug)
/*  366:     */         {
/*  367: 831 */           println("\n=== Full report ===\n" + evaluation1A.toSummaryString("\nFirst buildKernel()") + "\n\n");
/*  368:     */           
/*  369: 833 */           println(evaluation1B.toSummaryString("\nSecond buildKernel()") + "\n\n");
/*  370:     */         }
/*  371: 836 */         throw new Exception("Results differ between buildKernel calls");
/*  372:     */       }
/*  373: 838 */       println("yes");
/*  374: 839 */       result[0] = true;
/*  375:     */     }
/*  376:     */     catch (Exception ex)
/*  377:     */     {
/*  378: 842 */       println("no");
/*  379: 843 */       result[0] = false;
/*  380: 845 */       if (this.m_Debug)
/*  381:     */       {
/*  382: 846 */         println("\n=== Full Report ===");
/*  383: 847 */         print("Problem during building");
/*  384: 848 */         switch (stage)
/*  385:     */         {
/*  386:     */         case 0: 
/*  387: 850 */           print(" of dataset 1");
/*  388: 851 */           break;
/*  389:     */         case 1: 
/*  390: 853 */           print(" of dataset 2");
/*  391: 854 */           break;
/*  392:     */         case 2: 
/*  393: 856 */           print(" of dataset 1 (2nd build)");
/*  394: 857 */           break;
/*  395:     */         case 3: 
/*  396: 859 */           print(", comparing results from builds of dataset 1");
/*  397:     */         }
/*  398: 862 */         println(": " + ex.getMessage() + "\n");
/*  399: 863 */         println("here are the datasets:\n");
/*  400: 864 */         println("=== Train1 Dataset ===\n" + train1.toString() + "\n");
/*  401: 865 */         println("=== Train2 Dataset ===\n" + train2.toString() + "\n");
/*  402:     */       }
/*  403:     */     }
/*  404: 869 */     return result;
/*  405:     */   }
/*  406:     */   
/*  407:     */   protected boolean[] canHandleMissing(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType, boolean predictorMissing, boolean classMissing, int missingLevel)
/*  408:     */   {
/*  409: 894 */     if (missingLevel == 100) {
/*  410: 895 */       print("100% ");
/*  411:     */     }
/*  412: 897 */     print("missing");
/*  413: 898 */     if (predictorMissing)
/*  414:     */     {
/*  415: 899 */       print(" predictor");
/*  416: 900 */       if (classMissing) {
/*  417: 901 */         print(" and");
/*  418:     */       }
/*  419:     */     }
/*  420: 904 */     if (classMissing) {
/*  421: 905 */       print(" class");
/*  422:     */     }
/*  423: 907 */     print(" values");
/*  424: 908 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType);
/*  425:     */     
/*  426: 910 */     print("...");
/*  427: 911 */     ArrayList<String> accepts = new ArrayList();
/*  428: 912 */     accepts.add("missing");
/*  429: 913 */     accepts.add("value");
/*  430: 914 */     accepts.add("train");
/*  431: 915 */     int numTrain = getNumInstances();int numClasses = 2;
/*  432:     */     
/*  433: 917 */     return runBasicTest(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType, missingLevel, predictorMissing, classMissing, numTrain, numClasses, accepts);
/*  434:     */   }
/*  435:     */   
/*  436:     */   protected boolean[] instanceWeights(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType)
/*  437:     */   {
/*  438: 945 */     print("kernel uses instance weights");
/*  439: 946 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType);
/*  440:     */     
/*  441: 948 */     print("...");
/*  442: 949 */     int numTrain = 2 * getNumInstances();int numClasses = 2;int missingLevel = 0;
/*  443: 950 */     boolean predictorMissing = false;boolean classMissing = false;
/*  444:     */     
/*  445: 952 */     boolean[] result = new boolean[2];
/*  446: 953 */     Instances train = null;
/*  447: 954 */     Kernel[] kernels = null;
/*  448: 955 */     KernelEvaluation evaluationB = null;
/*  449: 956 */     KernelEvaluation evaluationI = null;
/*  450: 957 */     boolean evalFail = false;
/*  451:     */     try
/*  452:     */     {
/*  453: 959 */       train = makeTestDataset(42, numTrain, nominalPredictor ? getNumNominal() + 1 : 0, numericPredictor ? getNumNumeric() + 1 : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, numClasses, classType, multiInstance);
/*  454: 965 */       if (missingLevel > 0) {
/*  455: 966 */         addMissing(train, missingLevel, predictorMissing, classMissing);
/*  456:     */       }
/*  457: 968 */       kernels = Kernel.makeCopies(getKernel(), 2);
/*  458: 969 */       evaluationB = new KernelEvaluation();
/*  459: 970 */       evaluationI = new KernelEvaluation();
/*  460: 971 */       evaluationB.evaluate(kernels[0], train);
/*  461:     */     }
/*  462:     */     catch (Exception ex)
/*  463:     */     {
/*  464: 973 */       throw new Error("Error setting up for tests: " + ex.getMessage());
/*  465:     */     }
/*  466:     */     try
/*  467:     */     {
/*  468: 978 */       for (int i = 0; i < train.numInstances(); i++) {
/*  469: 979 */         train.instance(i).setWeight(0.0D);
/*  470:     */       }
/*  471: 981 */       Random random = new Random(1L);
/*  472: 982 */       for (int i = 0; i < train.numInstances() / 2; i++)
/*  473:     */       {
/*  474: 983 */         int inst = random.nextInt(train.numInstances());
/*  475: 984 */         int weight = random.nextInt(10) + 1;
/*  476: 985 */         train.instance(inst).setWeight(weight);
/*  477:     */       }
/*  478: 987 */       evaluationI.evaluate(kernels[1], train);
/*  479: 988 */       if (evaluationB.equals(evaluationI))
/*  480:     */       {
/*  481: 990 */         evalFail = true;
/*  482: 991 */         throw new Exception("evalFail");
/*  483:     */       }
/*  484: 994 */       println("yes");
/*  485: 995 */       result[0] = true;
/*  486:     */     }
/*  487:     */     catch (Exception ex)
/*  488:     */     {
/*  489: 997 */       println("no");
/*  490: 998 */       result[0] = false;
/*  491:1000 */       if (!this.m_Debug) {
/*  492:     */         break label596;
/*  493:     */       }
/*  494:     */     }
/*  495:1001 */     println("\n=== Full Report ===");
/*  496:1003 */     if (evalFail)
/*  497:     */     {
/*  498:1004 */       println("Results don't differ between non-weighted and weighted instance models.");
/*  499:     */       
/*  500:1006 */       println("Here are the results:\n");
/*  501:1007 */       println(evaluationB.toSummaryString("\nboth methods\n"));
/*  502:     */     }
/*  503:     */     else
/*  504:     */     {
/*  505:1009 */       print("Problem during building");
/*  506:1010 */       println(": " + ex.getMessage() + "\n");
/*  507:     */     }
/*  508:1012 */     println("Here is the dataset:\n");
/*  509:1013 */     println("=== Train Dataset ===\n" + train.toString() + "\n");
/*  510:1014 */     println("=== Train Weights ===\n");
/*  511:1015 */     for (int i = 0; i < train.numInstances(); i++) {
/*  512:1016 */       println(" " + (i + 1) + "    " + train.instance(i).weight());
/*  513:     */     }
/*  514:     */     label596:
/*  515:1021 */     return result;
/*  516:     */   }
/*  517:     */   
/*  518:     */   protected boolean[] datasetIntegrity(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType, boolean predictorMissing, boolean classMissing)
/*  519:     */   {
/*  520:1048 */     print("kernel doesn't alter original datasets");
/*  521:1049 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType);
/*  522:     */     
/*  523:1051 */     print("...");
/*  524:1052 */     int numTrain = getNumInstances();int numClasses = 2;int missingLevel = 20;
/*  525:     */     
/*  526:1054 */     boolean[] result = new boolean[2];
/*  527:1055 */     Instances train = null;
/*  528:1056 */     Kernel kernel = null;
/*  529:     */     try
/*  530:     */     {
/*  531:1058 */       train = makeTestDataset(42, numTrain, nominalPredictor ? getNumNominal() : 0, numericPredictor ? getNumNumeric() : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, numClasses, classType, multiInstance);
/*  532:1063 */       if (missingLevel > 0) {
/*  533:1064 */         addMissing(train, missingLevel, predictorMissing, classMissing);
/*  534:     */       }
/*  535:1066 */       kernel = Kernel.makeCopies(getKernel(), 1)[0];
/*  536:     */     }
/*  537:     */     catch (Exception ex)
/*  538:     */     {
/*  539:1068 */       throw new Error("Error setting up for tests: " + ex.getMessage());
/*  540:     */     }
/*  541:     */     try
/*  542:     */     {
/*  543:1071 */       Instances trainCopy = new Instances(train);
/*  544:1072 */       kernel.buildKernel(trainCopy);
/*  545:1073 */       compareDatasets(train, trainCopy);
/*  546:     */       
/*  547:1075 */       println("yes");
/*  548:1076 */       result[0] = true;
/*  549:     */     }
/*  550:     */     catch (Exception ex)
/*  551:     */     {
/*  552:1078 */       println("no");
/*  553:1079 */       result[0] = false;
/*  554:1081 */       if (this.m_Debug)
/*  555:     */       {
/*  556:1082 */         println("\n=== Full Report ===");
/*  557:1083 */         print("Problem during building");
/*  558:1084 */         println(": " + ex.getMessage() + "\n");
/*  559:1085 */         println("Here is the dataset:\n");
/*  560:1086 */         println("=== Train Dataset ===\n" + train.toString() + "\n");
/*  561:     */       }
/*  562:     */     }
/*  563:1090 */     return result;
/*  564:     */   }
/*  565:     */   
/*  566:     */   protected boolean[] runBasicTest(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType, int missingLevel, boolean predictorMissing, boolean classMissing, int numTrain, int numClasses, ArrayList<String> accepts)
/*  567:     */   {
/*  568:1118 */     return runBasicTest(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, classType, -1, missingLevel, predictorMissing, classMissing, numTrain, numClasses, accepts);
/*  569:     */   }
/*  570:     */   
/*  571:     */   protected boolean[] runBasicTest(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType, int classIndex, int missingLevel, boolean predictorMissing, boolean classMissing, int numTrain, int numClasses, ArrayList<String> accepts)
/*  572:     */   {
/*  573:1151 */     boolean[] result = new boolean[2];
/*  574:1152 */     Instances train = null;
/*  575:1153 */     Kernel kernel = null;
/*  576:     */     try
/*  577:     */     {
/*  578:1155 */       train = makeTestDataset(42, numTrain, nominalPredictor ? getNumNominal() : 0, numericPredictor ? getNumNumeric() : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, numClasses, classType, classIndex, multiInstance);
/*  579:1160 */       if (missingLevel > 0) {
/*  580:1161 */         addMissing(train, missingLevel, predictorMissing, classMissing);
/*  581:     */       }
/*  582:1163 */       kernel = Kernel.makeCopies(getKernel(), 1)[0];
/*  583:     */     }
/*  584:     */     catch (Exception ex)
/*  585:     */     {
/*  586:1165 */       ex.printStackTrace();
/*  587:1166 */       throw new Error("Error setting up for tests: " + ex.getMessage());
/*  588:     */     }
/*  589:     */     try
/*  590:     */     {
/*  591:1169 */       kernel.buildKernel(train);
/*  592:1170 */       println("yes");
/*  593:1171 */       result[0] = true;
/*  594:     */     }
/*  595:     */     catch (Exception ex)
/*  596:     */     {
/*  597:1173 */       boolean acceptable = false;
/*  598:     */       String msg;
/*  599:     */       String msg;
/*  600:1175 */       if (ex.getMessage() == null) {
/*  601:1176 */         msg = "";
/*  602:     */       } else {
/*  603:1178 */         msg = ex.getMessage().toLowerCase();
/*  604:     */       }
/*  605:1180 */       if (msg.indexOf("not in classpath") > -1) {
/*  606:1181 */         this.m_ClasspathProblems = true;
/*  607:     */       }
/*  608:1184 */       for (int i = 0; i < accepts.size(); i++) {
/*  609:1185 */         if (msg.indexOf((String)accepts.get(i)) >= 0) {
/*  610:1186 */           acceptable = true;
/*  611:     */         }
/*  612:     */       }
/*  613:1190 */       println("no" + (acceptable ? " (OK error message)" : ""));
/*  614:1191 */       result[1] = acceptable;
/*  615:1193 */       if (this.m_Debug)
/*  616:     */       {
/*  617:1194 */         println("\n=== Full Report ===");
/*  618:1195 */         print("Problem during building");
/*  619:1196 */         println(": " + ex.getMessage() + "\n");
/*  620:1197 */         if (!acceptable)
/*  621:     */         {
/*  622:1198 */           if (accepts.size() > 0)
/*  623:     */           {
/*  624:1199 */             print("Error message doesn't mention ");
/*  625:1200 */             for (int i = 0; i < accepts.size(); i++)
/*  626:     */             {
/*  627:1201 */               if (i != 0) {
/*  628:1202 */                 print(" or ");
/*  629:     */               }
/*  630:1204 */               print('"' + (String)accepts.get(i) + '"');
/*  631:     */             }
/*  632:     */           }
/*  633:1207 */           println("here is the dataset:\n");
/*  634:1208 */           println("=== Train Dataset ===\n" + train.toString() + "\n");
/*  635:     */         }
/*  636:     */       }
/*  637:     */     }
/*  638:1213 */     return result;
/*  639:     */   }
/*  640:     */   
/*  641:     */   protected Instances makeTestDataset(int seed, int numInstances, int numNominal, int numNumeric, int numString, int numDate, int numRelational, int numClasses, int classType, boolean multiInstance)
/*  642:     */     throws Exception
/*  643:     */   {
/*  644:1239 */     return makeTestDataset(seed, numInstances, numNominal, numNumeric, numString, numDate, numRelational, numClasses, classType, -1, multiInstance);
/*  645:     */   }
/*  646:     */   
/*  647:     */   protected Instances makeTestDataset(int seed, int numInstances, int numNominal, int numNumeric, int numString, int numDate, int numRelational, int numClasses, int classType, int classIndex, boolean multiInstance)
/*  648:     */     throws Exception
/*  649:     */   {
/*  650:1269 */     TestInstances dataset = new TestInstances();
/*  651:     */     
/*  652:1271 */     dataset.setSeed(seed);
/*  653:1272 */     dataset.setNumInstances(numInstances);
/*  654:1273 */     dataset.setNumNominal(numNominal);
/*  655:1274 */     dataset.setNumNumeric(numNumeric);
/*  656:1275 */     dataset.setNumString(numString);
/*  657:1276 */     dataset.setNumDate(numDate);
/*  658:1277 */     dataset.setNumRelational(numRelational);
/*  659:1278 */     dataset.setNumClasses(numClasses);
/*  660:1279 */     dataset.setClassType(classType);
/*  661:1280 */     dataset.setClassIndex(classIndex);
/*  662:1281 */     dataset.setNumClasses(numClasses);
/*  663:1282 */     dataset.setMultiInstance(multiInstance);
/*  664:1283 */     dataset.setWords(getWords());
/*  665:1284 */     dataset.setWordSeparators(getWordSeparators());
/*  666:     */     
/*  667:1286 */     return process(dataset.generate());
/*  668:     */   }
/*  669:     */   
/*  670:     */   protected void printAttributeSummary(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int classType)
/*  671:     */   {
/*  672:1305 */     String str = "";
/*  673:1307 */     if (numericPredictor) {
/*  674:1308 */       str = str + " numeric";
/*  675:     */     }
/*  676:1311 */     if (nominalPredictor)
/*  677:     */     {
/*  678:1312 */       if (str.length() > 0) {
/*  679:1313 */         str = str + " &";
/*  680:     */       }
/*  681:1315 */       str = str + " nominal";
/*  682:     */     }
/*  683:1318 */     if (stringPredictor)
/*  684:     */     {
/*  685:1319 */       if (str.length() > 0) {
/*  686:1320 */         str = str + " &";
/*  687:     */       }
/*  688:1322 */       str = str + " string";
/*  689:     */     }
/*  690:1325 */     if (datePredictor)
/*  691:     */     {
/*  692:1326 */       if (str.length() > 0) {
/*  693:1327 */         str = str + " &";
/*  694:     */       }
/*  695:1329 */       str = str + " date";
/*  696:     */     }
/*  697:1332 */     if (relationalPredictor)
/*  698:     */     {
/*  699:1333 */       if (str.length() > 0) {
/*  700:1334 */         str = str + " &";
/*  701:     */       }
/*  702:1336 */       str = str + " relational";
/*  703:     */     }
/*  704:1339 */     str = str + " predictors)";
/*  705:1341 */     switch (classType)
/*  706:     */     {
/*  707:     */     case 0: 
/*  708:1343 */       str = " (numeric class," + str;
/*  709:1344 */       break;
/*  710:     */     case 1: 
/*  711:1346 */       str = " (nominal class," + str;
/*  712:1347 */       break;
/*  713:     */     case 2: 
/*  714:1349 */       str = " (string class," + str;
/*  715:1350 */       break;
/*  716:     */     case 3: 
/*  717:1352 */       str = " (date class," + str;
/*  718:1353 */       break;
/*  719:     */     case 4: 
/*  720:1355 */       str = " (relational class," + str;
/*  721:     */     }
/*  722:1359 */     print(str);
/*  723:     */   }
/*  724:     */   
/*  725:     */   public String getRevision()
/*  726:     */   {
/*  727:1369 */     return RevisionUtils.extract("$Revision: 11247 $");
/*  728:     */   }
/*  729:     */   
/*  730:     */   public static void main(String[] args)
/*  731:     */   {
/*  732:1378 */     runCheck(new CheckKernel(), args);
/*  733:     */   }
/*  734:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.supportVector.CheckKernel
 * JD-Core Version:    0.7.0.1
 */