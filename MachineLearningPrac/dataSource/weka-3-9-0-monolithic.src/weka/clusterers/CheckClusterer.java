/*    1:     */ package weka.clusterers;
/*    2:     */ 
/*    3:     */ import java.util.Collections;
/*    4:     */ import java.util.Enumeration;
/*    5:     */ import java.util.Random;
/*    6:     */ import java.util.Vector;
/*    7:     */ import weka.core.CheckScheme;
/*    8:     */ import weka.core.Instance;
/*    9:     */ import weka.core.Instances;
/*   10:     */ import weka.core.MultiInstanceCapabilitiesHandler;
/*   11:     */ import weka.core.Option;
/*   12:     */ import weka.core.OptionHandler;
/*   13:     */ import weka.core.RevisionUtils;
/*   14:     */ import weka.core.SerializationHelper;
/*   15:     */ import weka.core.TestInstances;
/*   16:     */ import weka.core.Utils;
/*   17:     */ import weka.core.WeightedInstancesHandler;
/*   18:     */ 
/*   19:     */ public class CheckClusterer
/*   20:     */   extends CheckScheme
/*   21:     */ {
/*   22: 215 */   protected Clusterer m_Clusterer = new SimpleKMeans();
/*   23:     */   
/*   24:     */   public CheckClusterer()
/*   25:     */   {
/*   26: 223 */     setNumInstances(40);
/*   27:     */   }
/*   28:     */   
/*   29:     */   public Enumeration<Option> listOptions()
/*   30:     */   {
/*   31: 233 */     Vector<Option> result = new Vector();
/*   32:     */     
/*   33: 235 */     result.addElement(new Option("\tFull name of the clusterer analyzed.\n\teg: weka.clusterers.SimpleKMeans\n\t(default weka.clusterers.SimpleKMeans)", "W", 1, "-W"));
/*   34:     */     
/*   35:     */ 
/*   36:     */ 
/*   37: 239 */     result.addAll(Collections.list(super.listOptions()));
/*   38: 241 */     if ((this.m_Clusterer != null) && ((this.m_Clusterer instanceof OptionHandler)))
/*   39:     */     {
/*   40: 242 */       result.addElement(new Option("", "", 0, "\nOptions specific to clusterer " + this.m_Clusterer.getClass().getName() + ":"));
/*   41:     */       
/*   42:     */ 
/*   43:     */ 
/*   44: 246 */       result.addAll(Collections.list(((OptionHandler)this.m_Clusterer).listOptions()));
/*   45:     */     }
/*   46: 250 */     return result.elements();
/*   47:     */   }
/*   48:     */   
/*   49:     */   public void setOptions(String[] options)
/*   50:     */     throws Exception
/*   51:     */   {
/*   52: 362 */     String tmpStr = Utils.getOption('N', options);
/*   53: 364 */     if (tmpStr.length() != 0) {
/*   54: 365 */       setNumInstances(Integer.parseInt(tmpStr));
/*   55:     */     } else {
/*   56: 367 */       setNumInstances(40);
/*   57:     */     }
/*   58: 370 */     super.setOptions(options);
/*   59:     */     
/*   60: 372 */     tmpStr = Utils.getOption('W', options);
/*   61: 373 */     if (tmpStr.length() == 0) {
/*   62: 374 */       tmpStr = SimpleKMeans.class.getName();
/*   63:     */     }
/*   64: 376 */     setClusterer((Clusterer)forName("weka.clusterers", Clusterer.class, tmpStr, Utils.partitionOptions(options)));
/*   65:     */     
/*   66:     */ 
/*   67: 379 */     Utils.checkForRemainingOptions(options);
/*   68:     */   }
/*   69:     */   
/*   70:     */   public String[] getOptions()
/*   71:     */   {
/*   72: 389 */     Vector<String> result = new Vector();
/*   73: 391 */     if (getClusterer() != null)
/*   74:     */     {
/*   75: 392 */       result.add("-W");
/*   76: 393 */       result.add(getClusterer().getClass().getName());
/*   77:     */     }
/*   78: 396 */     Collections.addAll(result, super.getOptions());
/*   79: 398 */     if ((this.m_Clusterer != null) && ((this.m_Clusterer instanceof OptionHandler)))
/*   80:     */     {
/*   81: 399 */       String[] options = ((OptionHandler)this.m_Clusterer).getOptions();
/*   82: 401 */       if (options.length > 0)
/*   83:     */       {
/*   84: 402 */         result.add("--");
/*   85: 403 */         Collections.addAll(result, options);
/*   86:     */       }
/*   87:     */     }
/*   88: 407 */     return (String[])result.toArray(new String[result.size()]);
/*   89:     */   }
/*   90:     */   
/*   91:     */   public void doTests()
/*   92:     */   {
/*   93: 416 */     if (getClusterer() == null)
/*   94:     */     {
/*   95: 417 */       println("\n=== No clusterer set ===");
/*   96: 418 */       return;
/*   97:     */     }
/*   98: 420 */     println("\n=== Check on Clusterer: " + getClusterer().getClass().getName() + " ===\n");
/*   99:     */     
/*  100:     */ 
/*  101:     */ 
/*  102: 424 */     println("--> Checking for interfaces");
/*  103: 425 */     canTakeOptions();
/*  104: 426 */     boolean updateable = updateableClusterer()[0];
/*  105: 427 */     boolean weightedInstancesHandler = weightedInstancesHandler()[0];
/*  106: 428 */     boolean multiInstanceHandler = multiInstanceHandler()[0];
/*  107: 429 */     println("--> Clusterer tests");
/*  108: 430 */     declaresSerialVersionUID();
/*  109: 431 */     runTests(weightedInstancesHandler, multiInstanceHandler, updateable);
/*  110:     */   }
/*  111:     */   
/*  112:     */   public void setClusterer(Clusterer newClusterer)
/*  113:     */   {
/*  114: 440 */     this.m_Clusterer = newClusterer;
/*  115:     */   }
/*  116:     */   
/*  117:     */   public Clusterer getClusterer()
/*  118:     */   {
/*  119: 449 */     return this.m_Clusterer;
/*  120:     */   }
/*  121:     */   
/*  122:     */   protected void runTests(boolean weighted, boolean multiInstance, boolean updateable)
/*  123:     */   {
/*  124: 462 */     boolean PNom = canPredict(true, false, false, false, false, multiInstance)[0];
/*  125: 463 */     boolean PNum = canPredict(false, true, false, false, false, multiInstance)[0];
/*  126: 464 */     boolean PStr = canPredict(false, false, true, false, false, multiInstance)[0];
/*  127: 465 */     boolean PDat = canPredict(false, false, false, true, false, multiInstance)[0];
/*  128:     */     boolean PRel;
/*  129:     */     boolean PRel;
/*  130: 467 */     if (!multiInstance) {
/*  131: 468 */       PRel = canPredict(false, false, false, false, true, multiInstance)[0];
/*  132:     */     } else {
/*  133: 470 */       PRel = false;
/*  134:     */     }
/*  135: 473 */     if ((PNom) || (PNum) || (PStr) || (PDat) || (PRel))
/*  136:     */     {
/*  137: 474 */       if (weighted) {
/*  138: 475 */         instanceWeights(PNom, PNum, PStr, PDat, PRel, multiInstance);
/*  139:     */       }
/*  140: 478 */       canHandleZeroTraining(PNom, PNum, PStr, PDat, PRel, multiInstance);
/*  141: 479 */       boolean handleMissingPredictors = canHandleMissing(PNom, PNum, PStr, PDat, PRel, multiInstance, true, 20)[0];
/*  142: 481 */       if (handleMissingPredictors) {
/*  143: 482 */         canHandleMissing(PNom, PNum, PStr, PDat, PRel, multiInstance, true, 100);
/*  144:     */       }
/*  145: 485 */       correctBuildInitialisation(PNom, PNum, PStr, PDat, PRel, multiInstance);
/*  146: 486 */       datasetIntegrity(PNom, PNum, PStr, PDat, PRel, multiInstance, handleMissingPredictors);
/*  147: 488 */       if (updateable) {
/*  148: 489 */         updatingEquality(PNom, PNum, PStr, PDat, PRel, multiInstance);
/*  149:     */       }
/*  150:     */     }
/*  151:     */   }
/*  152:     */   
/*  153:     */   protected boolean[] canTakeOptions()
/*  154:     */   {
/*  155: 501 */     boolean[] result = new boolean[2];
/*  156:     */     
/*  157: 503 */     print("options...");
/*  158: 504 */     if ((this.m_Clusterer instanceof OptionHandler))
/*  159:     */     {
/*  160: 505 */       println("yes");
/*  161: 506 */       if (this.m_Debug)
/*  162:     */       {
/*  163: 507 */         println("\n=== Full report ===");
/*  164: 508 */         Enumeration<Option> enu = ((OptionHandler)this.m_Clusterer).listOptions();
/*  165: 509 */         while (enu.hasMoreElements())
/*  166:     */         {
/*  167: 510 */           Option option = (Option)enu.nextElement();
/*  168: 511 */           print(option.synopsis() + "\n" + option.description() + "\n");
/*  169:     */         }
/*  170: 513 */         println("\n");
/*  171:     */       }
/*  172: 515 */       result[0] = true;
/*  173:     */     }
/*  174:     */     else
/*  175:     */     {
/*  176: 517 */       println("no");
/*  177: 518 */       result[0] = false;
/*  178:     */     }
/*  179: 521 */     return result;
/*  180:     */   }
/*  181:     */   
/*  182:     */   protected boolean[] updateableClusterer()
/*  183:     */   {
/*  184: 531 */     boolean[] result = new boolean[2];
/*  185:     */     
/*  186: 533 */     print("updateable clusterer...");
/*  187: 534 */     if ((this.m_Clusterer instanceof UpdateableClusterer))
/*  188:     */     {
/*  189: 535 */       println("yes");
/*  190: 536 */       result[0] = true;
/*  191:     */     }
/*  192:     */     else
/*  193:     */     {
/*  194: 538 */       println("no");
/*  195: 539 */       result[0] = false;
/*  196:     */     }
/*  197: 542 */     return result;
/*  198:     */   }
/*  199:     */   
/*  200:     */   protected boolean[] weightedInstancesHandler()
/*  201:     */   {
/*  202: 552 */     boolean[] result = new boolean[2];
/*  203:     */     
/*  204: 554 */     print("weighted instances clusterer...");
/*  205: 555 */     if ((this.m_Clusterer instanceof WeightedInstancesHandler))
/*  206:     */     {
/*  207: 556 */       println("yes");
/*  208: 557 */       result[0] = true;
/*  209:     */     }
/*  210:     */     else
/*  211:     */     {
/*  212: 559 */       println("no");
/*  213: 560 */       result[0] = false;
/*  214:     */     }
/*  215: 563 */     return result;
/*  216:     */   }
/*  217:     */   
/*  218:     */   protected boolean[] multiInstanceHandler()
/*  219:     */   {
/*  220: 572 */     boolean[] result = new boolean[2];
/*  221:     */     
/*  222: 574 */     print("multi-instance clusterer...");
/*  223: 575 */     if ((this.m_Clusterer instanceof MultiInstanceCapabilitiesHandler))
/*  224:     */     {
/*  225: 576 */       println("yes");
/*  226: 577 */       result[0] = true;
/*  227:     */     }
/*  228:     */     else
/*  229:     */     {
/*  230: 579 */       println("no");
/*  231: 580 */       result[0] = false;
/*  232:     */     }
/*  233: 583 */     return result;
/*  234:     */   }
/*  235:     */   
/*  236:     */   protected boolean[] declaresSerialVersionUID()
/*  237:     */   {
/*  238: 593 */     boolean[] result = new boolean[2];
/*  239:     */     
/*  240: 595 */     print("serialVersionUID...");
/*  241:     */     
/*  242: 597 */     result[0] = (!SerializationHelper.needsUID(this.m_Clusterer.getClass()) ? 1 : false);
/*  243: 599 */     if (result[0] != 0) {
/*  244: 600 */       println("yes");
/*  245:     */     } else {
/*  246: 602 */       println("no");
/*  247:     */     }
/*  248: 605 */     return result;
/*  249:     */   }
/*  250:     */   
/*  251:     */   protected boolean[] canPredict(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance)
/*  252:     */   {
/*  253: 624 */     print("basic predict");
/*  254: 625 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance);
/*  255:     */     
/*  256: 627 */     print("...");
/*  257: 628 */     Vector<String> accepts = new Vector();
/*  258: 629 */     accepts.addElement("unary");
/*  259: 630 */     accepts.addElement("binary");
/*  260: 631 */     accepts.addElement("nominal");
/*  261: 632 */     accepts.addElement("numeric");
/*  262: 633 */     accepts.addElement("string");
/*  263: 634 */     accepts.addElement("date");
/*  264: 635 */     accepts.addElement("relational");
/*  265: 636 */     accepts.addElement("multi-instance");
/*  266: 637 */     accepts.addElement("not in classpath");
/*  267: 638 */     int numTrain = getNumInstances();int missingLevel = 0;
/*  268: 639 */     boolean predictorMissing = false;
/*  269:     */     
/*  270: 641 */     return runBasicTest(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, missingLevel, predictorMissing, numTrain, accepts);
/*  271:     */   }
/*  272:     */   
/*  273:     */   protected boolean[] canHandleZeroTraining(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance)
/*  274:     */   {
/*  275: 662 */     print("handle zero training instances");
/*  276: 663 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance);
/*  277:     */     
/*  278: 665 */     print("...");
/*  279: 666 */     Vector<String> accepts = new Vector();
/*  280: 667 */     accepts.addElement("train");
/*  281: 668 */     accepts.addElement("value");
/*  282: 669 */     int numTrain = 0;int missingLevel = 0;
/*  283: 670 */     boolean predictorMissing = false;
/*  284:     */     
/*  285: 672 */     return runBasicTest(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, missingLevel, predictorMissing, numTrain, accepts);
/*  286:     */   }
/*  287:     */   
/*  288:     */   protected boolean[] correctBuildInitialisation(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance)
/*  289:     */   {
/*  290: 697 */     boolean[] result = new boolean[2];
/*  291:     */     
/*  292: 699 */     print("correct initialisation during buildClusterer");
/*  293: 700 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance);
/*  294:     */     
/*  295: 702 */     print("...");
/*  296: 703 */     int numTrain = getNumInstances();int missingLevel = 0;
/*  297: 704 */     boolean predictorMissing = false;
/*  298:     */     
/*  299: 706 */     Instances train1 = null;
/*  300: 707 */     Instances train2 = null;
/*  301: 708 */     Clusterer clusterer = null;
/*  302: 709 */     ClusterEvaluation evaluation1A = null;
/*  303: 710 */     ClusterEvaluation evaluation1B = null;
/*  304: 711 */     ClusterEvaluation evaluation2 = null;
/*  305: 712 */     boolean built = false;
/*  306: 713 */     int stage = 0;
/*  307:     */     try
/*  308:     */     {
/*  309: 717 */       train1 = makeTestDataset(42, numTrain, nominalPredictor ? getNumNominal() : 0, numericPredictor ? getNumNumeric() : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, multiInstance);
/*  310:     */       
/*  311:     */ 
/*  312:     */ 
/*  313: 721 */       train2 = makeTestDataset(84, numTrain, nominalPredictor ? getNumNominal() + 1 : 0, numericPredictor ? getNumNumeric() + 1 : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, multiInstance);
/*  314: 726 */       if ((nominalPredictor) && (!multiInstance))
/*  315:     */       {
/*  316: 727 */         train1.deleteAttributeAt(0);
/*  317: 728 */         train2.deleteAttributeAt(0);
/*  318:     */       }
/*  319: 730 */       if (missingLevel > 0)
/*  320:     */       {
/*  321: 731 */         addMissing(train1, missingLevel, predictorMissing);
/*  322: 732 */         addMissing(train2, missingLevel, predictorMissing);
/*  323:     */       }
/*  324: 735 */       clusterer = AbstractClusterer.makeCopies(getClusterer(), 1)[0];
/*  325: 736 */       evaluation1A = new ClusterEvaluation();
/*  326: 737 */       evaluation1B = new ClusterEvaluation();
/*  327: 738 */       evaluation2 = new ClusterEvaluation();
/*  328:     */     }
/*  329:     */     catch (Exception ex)
/*  330:     */     {
/*  331: 740 */       throw new Error("Error setting up for tests: " + ex.getMessage());
/*  332:     */     }
/*  333:     */     try
/*  334:     */     {
/*  335: 743 */       stage = 0;
/*  336: 744 */       clusterer.buildClusterer(train1);
/*  337: 745 */       built = true;
/*  338: 746 */       evaluation1A.setClusterer(clusterer);
/*  339: 747 */       evaluation1A.evaluateClusterer(train1);
/*  340:     */       
/*  341: 749 */       stage = 1;
/*  342: 750 */       built = false;
/*  343: 751 */       clusterer.buildClusterer(train2);
/*  344: 752 */       built = true;
/*  345: 753 */       evaluation2.setClusterer(clusterer);
/*  346: 754 */       evaluation2.evaluateClusterer(train2);
/*  347:     */       
/*  348: 756 */       stage = 2;
/*  349: 757 */       built = false;
/*  350: 758 */       clusterer.buildClusterer(train1);
/*  351: 759 */       built = true;
/*  352: 760 */       evaluation1B.setClusterer(clusterer);
/*  353: 761 */       evaluation1B.evaluateClusterer(train1);
/*  354:     */       
/*  355: 763 */       stage = 3;
/*  356: 764 */       if (!evaluation1A.equals(evaluation1B))
/*  357:     */       {
/*  358: 765 */         if (this.m_Debug)
/*  359:     */         {
/*  360: 766 */           println("\n=== Full report ===\n");
/*  361: 767 */           println("First buildClusterer()");
/*  362: 768 */           println(evaluation1A.clusterResultsToString() + "\n\n");
/*  363: 769 */           println("Second buildClusterer()");
/*  364: 770 */           println(evaluation1B.clusterResultsToString() + "\n\n");
/*  365:     */         }
/*  366: 772 */         throw new Exception("Results differ between buildClusterer calls");
/*  367:     */       }
/*  368: 774 */       println("yes");
/*  369: 775 */       result[0] = true;
/*  370:     */     }
/*  371:     */     catch (Exception ex)
/*  372:     */     {
/*  373: 778 */       println("no");
/*  374: 779 */       result[0] = false;
/*  375: 780 */       if (this.m_Debug)
/*  376:     */       {
/*  377: 781 */         println("\n=== Full Report ===");
/*  378: 782 */         print("Problem during");
/*  379: 783 */         if (built) {
/*  380: 784 */           print(" testing");
/*  381:     */         } else {
/*  382: 786 */           print(" training");
/*  383:     */         }
/*  384: 788 */         switch (stage)
/*  385:     */         {
/*  386:     */         case 0: 
/*  387: 790 */           print(" of dataset 1");
/*  388: 791 */           break;
/*  389:     */         case 1: 
/*  390: 793 */           print(" of dataset 2");
/*  391: 794 */           break;
/*  392:     */         case 2: 
/*  393: 796 */           print(" of dataset 1 (2nd build)");
/*  394: 797 */           break;
/*  395:     */         case 3: 
/*  396: 799 */           print(", comparing results from builds of dataset 1");
/*  397:     */         }
/*  398: 802 */         println(": " + ex.getMessage() + "\n");
/*  399: 803 */         println("here are the datasets:\n");
/*  400: 804 */         println("=== Train1 Dataset ===\n" + train1.toString() + "\n");
/*  401: 805 */         println("=== Train2 Dataset ===\n" + train2.toString() + "\n");
/*  402:     */       }
/*  403:     */     }
/*  404: 809 */     return result;
/*  405:     */   }
/*  406:     */   
/*  407:     */   protected boolean[] canHandleMissing(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, boolean predictorMissing, int missingLevel)
/*  408:     */   {
/*  409: 832 */     if (missingLevel == 100) {
/*  410: 833 */       print("100% ");
/*  411:     */     }
/*  412: 835 */     print("missing");
/*  413: 836 */     if (predictorMissing) {
/*  414: 837 */       print(" predictor");
/*  415:     */     }
/*  416: 839 */     print(" values");
/*  417: 840 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance);
/*  418:     */     
/*  419: 842 */     print("...");
/*  420: 843 */     Vector<String> accepts = new Vector();
/*  421: 844 */     accepts.addElement("missing");
/*  422: 845 */     accepts.addElement("value");
/*  423: 846 */     accepts.addElement("train");
/*  424: 847 */     int numTrain = getNumInstances();
/*  425:     */     
/*  426: 849 */     return runBasicTest(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance, missingLevel, predictorMissing, numTrain, accepts);
/*  427:     */   }
/*  428:     */   
/*  429:     */   protected boolean[] instanceWeights(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance)
/*  430:     */   {
/*  431: 875 */     print("clusterer uses instance weights");
/*  432: 876 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance);
/*  433:     */     
/*  434: 878 */     print("...");
/*  435: 879 */     int numTrain = 2 * getNumInstances();int missingLevel = 0;
/*  436: 880 */     boolean predictorMissing = false;
/*  437:     */     
/*  438: 882 */     boolean[] result = new boolean[2];
/*  439: 883 */     Instances train = null;
/*  440: 884 */     Clusterer[] clusterers = null;
/*  441: 885 */     ClusterEvaluation evaluationB = null;
/*  442: 886 */     ClusterEvaluation evaluationI = null;
/*  443: 887 */     boolean built = false;
/*  444: 888 */     boolean evalFail = false;
/*  445:     */     try
/*  446:     */     {
/*  447: 890 */       train = makeTestDataset(42, numTrain, nominalPredictor ? getNumNominal() + 1 : 0, numericPredictor ? getNumNumeric() + 1 : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, multiInstance);
/*  448: 895 */       if ((nominalPredictor) && (!multiInstance)) {
/*  449: 896 */         train.deleteAttributeAt(0);
/*  450:     */       }
/*  451: 898 */       if (missingLevel > 0) {
/*  452: 899 */         addMissing(train, missingLevel, predictorMissing);
/*  453:     */       }
/*  454: 901 */       clusterers = AbstractClusterer.makeCopies(getClusterer(), 2);
/*  455: 902 */       evaluationB = new ClusterEvaluation();
/*  456: 903 */       evaluationI = new ClusterEvaluation();
/*  457: 904 */       clusterers[0].buildClusterer(train);
/*  458: 905 */       evaluationB.setClusterer(clusterers[0]);
/*  459: 906 */       evaluationB.evaluateClusterer(train);
/*  460:     */     }
/*  461:     */     catch (Exception ex)
/*  462:     */     {
/*  463: 908 */       throw new Error("Error setting up for tests: " + ex.getMessage());
/*  464:     */     }
/*  465:     */     try
/*  466:     */     {
/*  467: 913 */       for (int i = 0; i < train.numInstances(); i++) {
/*  468: 914 */         train.instance(i).setWeight(0.0D);
/*  469:     */       }
/*  470: 916 */       Random random = new Random(1L);
/*  471: 917 */       for (int i = 0; i < train.numInstances() / 2; i++)
/*  472:     */       {
/*  473: 918 */         int inst = random.nextInt(train.numInstances());
/*  474: 919 */         int weight = random.nextInt(10) + 1;
/*  475: 920 */         train.instance(inst).setWeight(weight);
/*  476:     */       }
/*  477: 922 */       clusterers[1].buildClusterer(train);
/*  478: 923 */       built = true;
/*  479: 924 */       evaluationI.setClusterer(clusterers[1]);
/*  480: 925 */       evaluationI.evaluateClusterer(train);
/*  481: 926 */       if (evaluationB.equals(evaluationI))
/*  482:     */       {
/*  483: 928 */         evalFail = true;
/*  484: 929 */         throw new Exception("evalFail");
/*  485:     */       }
/*  486: 932 */       println("yes");
/*  487: 933 */       result[0] = true;
/*  488:     */     }
/*  489:     */     catch (Exception ex)
/*  490:     */     {
/*  491: 935 */       println("no");
/*  492: 936 */       result[0] = false;
/*  493: 938 */       if (!this.m_Debug) {
/*  494:     */         break label657;
/*  495:     */       }
/*  496:     */     }
/*  497: 939 */     println("\n=== Full Report ===");
/*  498: 941 */     if (evalFail)
/*  499:     */     {
/*  500: 942 */       println("Results don't differ between non-weighted and weighted instance models.");
/*  501:     */       
/*  502: 944 */       println("Here are the results:\n");
/*  503: 945 */       println("\nboth methods\n");
/*  504: 946 */       println(evaluationB.clusterResultsToString());
/*  505:     */     }
/*  506:     */     else
/*  507:     */     {
/*  508: 948 */       print("Problem during");
/*  509: 949 */       if (built) {
/*  510: 950 */         print(" testing");
/*  511:     */       } else {
/*  512: 952 */         print(" training");
/*  513:     */       }
/*  514: 954 */       println(": " + ex.getMessage() + "\n");
/*  515:     */     }
/*  516: 956 */     println("Here is the dataset:\n");
/*  517: 957 */     println("=== Train Dataset ===\n" + train.toString() + "\n");
/*  518: 958 */     println("=== Train Weights ===\n");
/*  519: 959 */     for (int i = 0; i < train.numInstances(); i++) {
/*  520: 960 */       println(" " + (i + 1) + "    " + train.instance(i).weight());
/*  521:     */     }
/*  522:     */     label657:
/*  523: 965 */     return result;
/*  524:     */   }
/*  525:     */   
/*  526:     */   protected boolean[] datasetIntegrity(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, boolean predictorMissing)
/*  527:     */   {
/*  528: 988 */     print("clusterer doesn't alter original datasets");
/*  529: 989 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance);
/*  530:     */     
/*  531: 991 */     print("...");
/*  532: 992 */     int numTrain = getNumInstances();int missingLevel = 20;
/*  533:     */     
/*  534: 994 */     boolean[] result = new boolean[2];
/*  535: 995 */     Instances train = null;
/*  536: 996 */     Clusterer clusterer = null;
/*  537:     */     try
/*  538:     */     {
/*  539: 998 */       train = makeTestDataset(42, numTrain, nominalPredictor ? getNumNominal() : 0, numericPredictor ? getNumNumeric() : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, multiInstance);
/*  540:1002 */       if ((nominalPredictor) && (!multiInstance)) {
/*  541:1003 */         train.deleteAttributeAt(0);
/*  542:     */       }
/*  543:1005 */       if (missingLevel > 0) {
/*  544:1006 */         addMissing(train, missingLevel, predictorMissing);
/*  545:     */       }
/*  546:1008 */       clusterer = AbstractClusterer.makeCopies(getClusterer(), 1)[0];
/*  547:     */     }
/*  548:     */     catch (Exception ex)
/*  549:     */     {
/*  550:1010 */       throw new Error("Error setting up for tests: " + ex.getMessage());
/*  551:     */     }
/*  552:     */     try
/*  553:     */     {
/*  554:1013 */       Instances trainCopy = new Instances(train);
/*  555:1014 */       clusterer.buildClusterer(trainCopy);
/*  556:1015 */       compareDatasets(train, trainCopy);
/*  557:     */       
/*  558:1017 */       println("yes");
/*  559:1018 */       result[0] = true;
/*  560:     */     }
/*  561:     */     catch (Exception ex)
/*  562:     */     {
/*  563:1020 */       println("no");
/*  564:1021 */       result[0] = false;
/*  565:1023 */       if (this.m_Debug)
/*  566:     */       {
/*  567:1024 */         println("\n=== Full Report ===");
/*  568:1025 */         print("Problem during training");
/*  569:1026 */         println(": " + ex.getMessage() + "\n");
/*  570:1027 */         println("Here is the dataset:\n");
/*  571:1028 */         println("=== Train Dataset ===\n" + train.toString() + "\n");
/*  572:     */       }
/*  573:     */     }
/*  574:1032 */     return result;
/*  575:     */   }
/*  576:     */   
/*  577:     */   protected boolean[] updatingEquality(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance)
/*  578:     */   {
/*  579:1054 */     print("incremental training produces the same results as batch training");
/*  580:     */     
/*  581:1056 */     printAttributeSummary(nominalPredictor, numericPredictor, stringPredictor, datePredictor, relationalPredictor, multiInstance);
/*  582:     */     
/*  583:1058 */     print("...");
/*  584:1059 */     int numTrain = getNumInstances();int missingLevel = 0;
/*  585:1060 */     boolean predictorMissing = false;boolean classMissing = false;
/*  586:     */     
/*  587:1062 */     boolean[] result = new boolean[2];
/*  588:1063 */     Instances train = null;
/*  589:1064 */     Clusterer[] clusterers = null;
/*  590:1065 */     ClusterEvaluation evaluationB = null;
/*  591:1066 */     ClusterEvaluation evaluationI = null;
/*  592:1067 */     boolean built = false;
/*  593:     */     try
/*  594:     */     {
/*  595:1069 */       train = makeTestDataset(42, numTrain, nominalPredictor ? getNumNominal() : 0, numericPredictor ? getNumNumeric() : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, multiInstance);
/*  596:1073 */       if (missingLevel > 0) {
/*  597:1074 */         addMissing(train, missingLevel, predictorMissing, classMissing);
/*  598:     */       }
/*  599:1076 */       clusterers = AbstractClusterer.makeCopies(getClusterer(), 2);
/*  600:1077 */       evaluationB = new ClusterEvaluation();
/*  601:1078 */       evaluationI = new ClusterEvaluation();
/*  602:1079 */       clusterers[0].buildClusterer(train);
/*  603:1080 */       evaluationB.setClusterer(clusterers[0]);
/*  604:1081 */       evaluationB.evaluateClusterer(train);
/*  605:     */     }
/*  606:     */     catch (Exception ex)
/*  607:     */     {
/*  608:1083 */       throw new Error("Error setting up for tests: " + ex.getMessage());
/*  609:     */     }
/*  610:     */     try
/*  611:     */     {
/*  612:1086 */       clusterers[1].buildClusterer(new Instances(train, 0));
/*  613:1087 */       for (int i = 0; i < train.numInstances(); i++) {
/*  614:1088 */         ((UpdateableClusterer)clusterers[1]).updateClusterer(train.instance(i));
/*  615:     */       }
/*  616:1091 */       built = true;
/*  617:1092 */       evaluationI.setClusterer(clusterers[1]);
/*  618:1093 */       evaluationI.evaluateClusterer(train);
/*  619:1094 */       if (!evaluationB.equals(evaluationI))
/*  620:     */       {
/*  621:1095 */         println("no");
/*  622:1096 */         result[0] = false;
/*  623:1098 */         if (this.m_Debug)
/*  624:     */         {
/*  625:1099 */           println("\n=== Full Report ===");
/*  626:1100 */           println("Results differ between batch and incrementally built models.\nDepending on the classifier, this may be OK");
/*  627:     */           
/*  628:     */ 
/*  629:1103 */           println("Here are the results:\n");
/*  630:1104 */           println("\nbatch built results\n" + evaluationB.clusterResultsToString());
/*  631:     */           
/*  632:1106 */           println("\nincrementally built results\n" + evaluationI.clusterResultsToString());
/*  633:     */           
/*  634:1108 */           println("Here are the datasets:\n");
/*  635:1109 */           println("=== Train Dataset ===\n" + train.toString() + "\n");
/*  636:     */         }
/*  637:     */       }
/*  638:     */       else
/*  639:     */       {
/*  640:1112 */         println("yes");
/*  641:1113 */         result[0] = true;
/*  642:     */       }
/*  643:     */     }
/*  644:     */     catch (Exception ex)
/*  645:     */     {
/*  646:1116 */       result[0] = false;
/*  647:     */       
/*  648:1118 */       print("Problem during");
/*  649:1119 */       if (built) {
/*  650:1120 */         print(" testing");
/*  651:     */       } else {
/*  652:1122 */         print(" training");
/*  653:     */       }
/*  654:1124 */       println(": " + ex.getMessage() + "\n");
/*  655:     */     }
/*  656:1127 */     return result;
/*  657:     */   }
/*  658:     */   
/*  659:     */   protected boolean[] runBasicTest(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance, int missingLevel, boolean predictorMissing, int numTrain, Vector<String> accepts)
/*  660:     */   {
/*  661:1151 */     boolean[] result = new boolean[2];
/*  662:1152 */     Instances train = null;
/*  663:1153 */     Clusterer clusterer = null;
/*  664:     */     try
/*  665:     */     {
/*  666:1155 */       train = makeTestDataset(42, numTrain, nominalPredictor ? getNumNominal() : 0, numericPredictor ? getNumNumeric() : 0, stringPredictor ? getNumString() : 0, datePredictor ? getNumDate() : 0, relationalPredictor ? getNumRelational() : 0, multiInstance);
/*  667:1159 */       if ((nominalPredictor) && (!multiInstance)) {
/*  668:1160 */         train.deleteAttributeAt(0);
/*  669:     */       }
/*  670:1162 */       if (missingLevel > 0) {
/*  671:1163 */         addMissing(train, missingLevel, predictorMissing);
/*  672:     */       }
/*  673:1165 */       clusterer = AbstractClusterer.makeCopies(getClusterer(), 1)[0];
/*  674:     */     }
/*  675:     */     catch (Exception ex)
/*  676:     */     {
/*  677:1167 */       ex.printStackTrace();
/*  678:1168 */       throw new Error("Error setting up for tests: " + ex.getMessage());
/*  679:     */     }
/*  680:     */     try
/*  681:     */     {
/*  682:1171 */       clusterer.buildClusterer(train);
/*  683:1172 */       println("yes");
/*  684:1173 */       result[0] = true;
/*  685:     */     }
/*  686:     */     catch (Exception ex)
/*  687:     */     {
/*  688:1175 */       boolean acceptable = false;
/*  689:1176 */       String msg = ex.getMessage().toLowerCase();
/*  690:1177 */       for (int i = 0; i < accepts.size(); i++) {
/*  691:1178 */         if (msg.indexOf((String)accepts.elementAt(i)) >= 0) {
/*  692:1179 */           acceptable = true;
/*  693:     */         }
/*  694:     */       }
/*  695:1183 */       println("no" + (acceptable ? " (OK error message)" : ""));
/*  696:1184 */       result[1] = acceptable;
/*  697:1186 */       if (this.m_Debug)
/*  698:     */       {
/*  699:1187 */         println("\n=== Full Report ===");
/*  700:1188 */         print("Problem during training");
/*  701:1189 */         println(": " + ex.getMessage() + "\n");
/*  702:1190 */         if (!acceptable)
/*  703:     */         {
/*  704:1191 */           if (accepts.size() > 0)
/*  705:     */           {
/*  706:1192 */             print("Error message doesn't mention ");
/*  707:1193 */             for (int i = 0; i < accepts.size(); i++)
/*  708:     */             {
/*  709:1194 */               if (i != 0) {
/*  710:1195 */                 print(" or ");
/*  711:     */               }
/*  712:1197 */               print('"' + (String)accepts.elementAt(i) + '"');
/*  713:     */             }
/*  714:     */           }
/*  715:1200 */           println("here is the dataset:\n");
/*  716:1201 */           println("=== Train Dataset ===\n" + train.toString() + "\n");
/*  717:     */         }
/*  718:     */       }
/*  719:     */     }
/*  720:1206 */     return result;
/*  721:     */   }
/*  722:     */   
/*  723:     */   protected void addMissing(Instances data, int level, boolean predictorMissing)
/*  724:     */   {
/*  725:1220 */     Random random = new Random(1L);
/*  726:1221 */     for (int i = 0; i < data.numInstances(); i++)
/*  727:     */     {
/*  728:1222 */       Instance current = data.instance(i);
/*  729:1223 */       for (int j = 0; j < data.numAttributes(); j++) {
/*  730:1224 */         if ((predictorMissing) && 
/*  731:1225 */           (random.nextInt(100) < level)) {
/*  732:1226 */           current.setMissing(j);
/*  733:     */         }
/*  734:     */       }
/*  735:     */     }
/*  736:     */   }
/*  737:     */   
/*  738:     */   protected Instances makeTestDataset(int seed, int numInstances, int numNominal, int numNumeric, int numString, int numDate, int numRelational, boolean multiInstance)
/*  739:     */     throws Exception
/*  740:     */   {
/*  741:1253 */     TestInstances dataset = new TestInstances();
/*  742:     */     
/*  743:1255 */     dataset.setSeed(seed);
/*  744:1256 */     dataset.setNumInstances(numInstances);
/*  745:1257 */     dataset.setNumNominal(numNominal);
/*  746:1258 */     dataset.setNumNumeric(numNumeric);
/*  747:1259 */     dataset.setNumString(numString);
/*  748:1260 */     dataset.setNumDate(numDate);
/*  749:1261 */     dataset.setNumRelational(numRelational);
/*  750:1262 */     dataset.setClassIndex(-2);
/*  751:1263 */     dataset.setMultiInstance(multiInstance);
/*  752:     */     
/*  753:1265 */     return dataset.generate();
/*  754:     */   }
/*  755:     */   
/*  756:     */   protected void printAttributeSummary(boolean nominalPredictor, boolean numericPredictor, boolean stringPredictor, boolean datePredictor, boolean relationalPredictor, boolean multiInstance)
/*  757:     */   {
/*  758:1283 */     String str = "";
/*  759:1285 */     if (numericPredictor) {
/*  760:1286 */       str = str + "numeric";
/*  761:     */     }
/*  762:1289 */     if (nominalPredictor)
/*  763:     */     {
/*  764:1290 */       if (str.length() > 0) {
/*  765:1291 */         str = str + " & ";
/*  766:     */       }
/*  767:1293 */       str = str + "nominal";
/*  768:     */     }
/*  769:1296 */     if (stringPredictor)
/*  770:     */     {
/*  771:1297 */       if (str.length() > 0) {
/*  772:1298 */         str = str + " & ";
/*  773:     */       }
/*  774:1300 */       str = str + "string";
/*  775:     */     }
/*  776:1303 */     if (datePredictor)
/*  777:     */     {
/*  778:1304 */       if (str.length() > 0) {
/*  779:1305 */         str = str + " & ";
/*  780:     */       }
/*  781:1307 */       str = str + "date";
/*  782:     */     }
/*  783:1310 */     if (relationalPredictor)
/*  784:     */     {
/*  785:1311 */       if (str.length() > 0) {
/*  786:1312 */         str = str + " & ";
/*  787:     */       }
/*  788:1314 */       str = str + "relational";
/*  789:     */     }
/*  790:1317 */     str = " (" + str + " predictors)";
/*  791:     */     
/*  792:1319 */     print(str);
/*  793:     */   }
/*  794:     */   
/*  795:     */   public String getRevision()
/*  796:     */   {
/*  797:1329 */     return RevisionUtils.extract("$Revision: 11451 $");
/*  798:     */   }
/*  799:     */   
/*  800:     */   public static void main(String[] args)
/*  801:     */   {
/*  802:1338 */     runCheck(new CheckClusterer(), args);
/*  803:     */   }
/*  804:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.clusterers.CheckClusterer
 * JD-Core Version:    0.7.0.1
 */