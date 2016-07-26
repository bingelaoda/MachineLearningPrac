/*    1:     */ package weka.estimators;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.util.ArrayList;
/*    5:     */ import java.util.Collections;
/*    6:     */ import java.util.Enumeration;
/*    7:     */ import java.util.Random;
/*    8:     */ import java.util.Vector;
/*    9:     */ import weka.core.Instance;
/*   10:     */ import weka.core.Instances;
/*   11:     */ import weka.core.Option;
/*   12:     */ import weka.core.OptionHandler;
/*   13:     */ import weka.core.RevisionHandler;
/*   14:     */ import weka.core.RevisionUtils;
/*   15:     */ import weka.core.TestInstances;
/*   16:     */ import weka.core.Utils;
/*   17:     */ import weka.core.WeightedInstancesHandler;
/*   18:     */ 
/*   19:     */ public class CheckEstimator
/*   20:     */   implements OptionHandler, RevisionHandler
/*   21:     */ {
/*   22:     */   protected Estimator m_Estimator;
/*   23:     */   protected String[] m_EstimatorOptions;
/*   24:     */   protected String m_AnalysisResults;
/*   25:     */   protected boolean m_Debug;
/*   26:     */   protected boolean m_Silent;
/*   27:     */   protected int m_NumInstances;
/*   28:     */   protected PostProcessor m_PostProcessor;
/*   29:     */   protected boolean m_ClasspathProblems;
/*   30:     */   
/*   31:     */   public class PostProcessor
/*   32:     */     implements RevisionHandler
/*   33:     */   {
/*   34:     */     public PostProcessor() {}
/*   35:     */     
/*   36:     */     protected Instances process(Instances data)
/*   37:     */     {
/*   38: 177 */       return data;
/*   39:     */     }
/*   40:     */     
/*   41:     */     public String getRevision()
/*   42:     */     {
/*   43: 187 */       return RevisionUtils.extract("$Revision: 11247 $");
/*   44:     */     }
/*   45:     */   }
/*   46:     */   
/*   47:     */   public CheckEstimator()
/*   48:     */   {
/*   49: 192 */     this.m_Estimator = new NormalEstimator(1.0E-006D);
/*   50:     */     
/*   51:     */ 
/*   52:     */ 
/*   53:     */ 
/*   54:     */ 
/*   55:     */ 
/*   56:     */ 
/*   57:     */ 
/*   58:     */ 
/*   59: 202 */     this.m_Debug = false;
/*   60:     */     
/*   61:     */ 
/*   62: 205 */     this.m_Silent = false;
/*   63:     */     
/*   64:     */ 
/*   65: 208 */     this.m_NumInstances = 100;
/*   66:     */     
/*   67:     */ 
/*   68: 211 */     this.m_PostProcessor = null;
/*   69:     */     
/*   70:     */ 
/*   71: 214 */     this.m_ClasspathProblems = false;
/*   72:     */   }
/*   73:     */   
/*   74:     */   public static class AttrTypes
/*   75:     */     implements RevisionHandler
/*   76:     */   {
/*   77: 222 */     boolean nominal = false;
/*   78: 223 */     boolean numeric = false;
/*   79: 224 */     boolean string = false;
/*   80: 225 */     boolean date = false;
/*   81: 226 */     boolean relational = false;
/*   82:     */     
/*   83:     */     AttrTypes() {}
/*   84:     */     
/*   85:     */     AttrTypes(AttrTypes newTypes)
/*   86:     */     {
/*   87: 232 */       this.nominal = newTypes.nominal;
/*   88: 233 */       this.numeric = newTypes.numeric;
/*   89: 234 */       this.string = newTypes.string;
/*   90: 235 */       this.date = newTypes.date;
/*   91: 236 */       this.relational = newTypes.relational;
/*   92:     */     }
/*   93:     */     
/*   94:     */     AttrTypes(int type)
/*   95:     */     {
/*   96: 240 */       if (type == 1) {
/*   97: 241 */         this.nominal = true;
/*   98:     */       }
/*   99: 243 */       if (type == 0) {
/*  100: 244 */         this.numeric = true;
/*  101:     */       }
/*  102: 246 */       if (type == 2) {
/*  103: 247 */         this.string = true;
/*  104:     */       }
/*  105: 249 */       if (type == 3) {
/*  106: 250 */         this.date = true;
/*  107:     */       }
/*  108: 252 */       if (type == 4) {
/*  109: 253 */         this.relational = true;
/*  110:     */       }
/*  111:     */     }
/*  112:     */     
/*  113:     */     int getSetType()
/*  114:     */       throws Exception
/*  115:     */     {
/*  116: 258 */       int sum = 0;
/*  117: 259 */       int type = -1;
/*  118: 260 */       if (this.nominal)
/*  119:     */       {
/*  120: 261 */         sum++;
/*  121: 262 */         type = 1;
/*  122:     */       }
/*  123: 264 */       if (this.numeric)
/*  124:     */       {
/*  125: 265 */         sum++;
/*  126: 266 */         type = 0;
/*  127:     */       }
/*  128: 268 */       if (this.string)
/*  129:     */       {
/*  130: 269 */         sum++;
/*  131: 270 */         type = 2;
/*  132:     */       }
/*  133: 272 */       if (this.date)
/*  134:     */       {
/*  135: 273 */         sum++;
/*  136: 274 */         type = 3;
/*  137:     */       }
/*  138: 276 */       if (this.relational)
/*  139:     */       {
/*  140: 277 */         sum++;
/*  141: 278 */         type = 4;
/*  142:     */       }
/*  143: 280 */       if (sum > 1) {
/*  144: 281 */         throw new Exception("Expected to have only one type set used wrongly.");
/*  145:     */       }
/*  146: 283 */       if (type < 0) {
/*  147: 284 */         throw new Exception("No type set.");
/*  148:     */       }
/*  149: 286 */       return type;
/*  150:     */     }
/*  151:     */     
/*  152:     */     boolean oneIsSet()
/*  153:     */     {
/*  154: 290 */       return (this.nominal) || (this.numeric) || (this.string) || (this.date) || (this.relational);
/*  155:     */     }
/*  156:     */     
/*  157:     */     public Vector<Integer> getVectorOfAttrTypes()
/*  158:     */     {
/*  159: 294 */       Vector<Integer> attrs = new Vector();
/*  160: 295 */       if (this.nominal) {
/*  161: 296 */         attrs.add(new Integer(1));
/*  162:     */       }
/*  163: 298 */       if (this.numeric) {
/*  164: 299 */         attrs.add(new Integer(0));
/*  165:     */       }
/*  166: 301 */       if (this.string) {
/*  167: 302 */         attrs.add(new Integer(2));
/*  168:     */       }
/*  169: 304 */       if (this.date) {
/*  170: 305 */         attrs.add(new Integer(3));
/*  171:     */       }
/*  172: 307 */       if (this.relational) {
/*  173: 308 */         attrs.add(new Integer(4));
/*  174:     */       }
/*  175: 310 */       return attrs;
/*  176:     */     }
/*  177:     */     
/*  178:     */     public String getRevision()
/*  179:     */     {
/*  180: 320 */       return RevisionUtils.extract("$Revision: 11247 $");
/*  181:     */     }
/*  182:     */   }
/*  183:     */   
/*  184:     */   public static class EstTypes
/*  185:     */     implements RevisionHandler
/*  186:     */   {
/*  187: 330 */     boolean incremental = false;
/*  188: 331 */     boolean weighted = false;
/*  189: 332 */     boolean supervised = false;
/*  190:     */     
/*  191:     */     public EstTypes() {}
/*  192:     */     
/*  193:     */     public EstTypes(boolean i, boolean w, boolean s)
/*  194:     */     {
/*  195: 344 */       this.incremental = i;
/*  196: 345 */       this.weighted = w;
/*  197: 346 */       this.supervised = s;
/*  198:     */     }
/*  199:     */     
/*  200:     */     public String getRevision()
/*  201:     */     {
/*  202: 356 */       return RevisionUtils.extract("$Revision: 11247 $");
/*  203:     */     }
/*  204:     */   }
/*  205:     */   
/*  206:     */   public Enumeration<Option> listOptions()
/*  207:     */   {
/*  208: 368 */     Vector<Option> newVector = new Vector(4);
/*  209:     */     
/*  210: 370 */     newVector.addElement(new Option("\tTurn on debugging output.", "D", 0, "-D"));
/*  211:     */     
/*  212:     */ 
/*  213: 373 */     newVector.addElement(new Option("\tSilent mode - prints nothing to stdout.", "S", 0, "-S"));
/*  214:     */     
/*  215:     */ 
/*  216: 376 */     newVector.addElement(new Option("\tThe number of instances in the datasets (default 100).", "N", 1, "-N <num>"));
/*  217:     */     
/*  218:     */ 
/*  219:     */ 
/*  220: 380 */     newVector.addElement(new Option("\tFull name of the estimator analysed.\n\teg: weka.estimators.NormalEstimator", "W", 1, "-W"));
/*  221: 383 */     if ((this.m_Estimator != null) && ((this.m_Estimator instanceof OptionHandler)))
/*  222:     */     {
/*  223: 384 */       newVector.addElement(new Option("", "", 0, "\nOptions specific to estimator " + this.m_Estimator.getClass().getName() + ":"));
/*  224:     */       
/*  225:     */ 
/*  226: 387 */       newVector.addAll(Collections.list(this.m_Estimator.listOptions()));
/*  227:     */     }
/*  228: 391 */     return newVector.elements();
/*  229:     */   }
/*  230:     */   
/*  231:     */   public void setOptions(String[] options)
/*  232:     */     throws Exception
/*  233:     */   {
/*  234: 440 */     setDebug(Utils.getFlag('D', options));
/*  235:     */     
/*  236: 442 */     setSilent(Utils.getFlag('S', options));
/*  237:     */     
/*  238: 444 */     String tmpStr = Utils.getOption('N', options);
/*  239: 445 */     if (tmpStr.length() != 0) {
/*  240: 446 */       setNumInstances(Integer.parseInt(tmpStr));
/*  241:     */     } else {
/*  242: 448 */       setNumInstances(100);
/*  243:     */     }
/*  244: 451 */     tmpStr = Utils.getOption('W', options);
/*  245: 452 */     if (tmpStr.length() == 0) {
/*  246: 453 */       throw new Exception("A estimator must be specified with the -W option.");
/*  247:     */     }
/*  248: 455 */     setEstimator(Estimator.forName(tmpStr, Utils.partitionOptions(options)));
/*  249:     */   }
/*  250:     */   
/*  251:     */   public String[] getOptions()
/*  252:     */   {
/*  253: 465 */     Vector<String> result = new Vector();
/*  254: 467 */     if (getDebug()) {
/*  255: 468 */       result.add("-D");
/*  256:     */     }
/*  257: 471 */     if (getSilent()) {
/*  258: 472 */       result.add("-S");
/*  259:     */     }
/*  260: 475 */     result.add("-N");
/*  261: 476 */     result.add("" + getNumInstances());
/*  262: 478 */     if (getEstimator() != null)
/*  263:     */     {
/*  264: 479 */       result.add("-W");
/*  265: 480 */       result.add(getEstimator().getClass().getName());
/*  266:     */     }
/*  267: 483 */     if ((this.m_Estimator != null) && ((this.m_Estimator instanceof OptionHandler)))
/*  268:     */     {
/*  269: 484 */       String[] options = this.m_Estimator.getOptions();
/*  270: 486 */       if (options.length > 0)
/*  271:     */       {
/*  272: 487 */         result.add("--");
/*  273: 488 */         Collections.addAll(result, options);
/*  274:     */       }
/*  275:     */     }
/*  276: 492 */     return (String[])result.toArray(new String[result.size()]);
/*  277:     */   }
/*  278:     */   
/*  279:     */   public void setPostProcessor(PostProcessor value)
/*  280:     */   {
/*  281: 502 */     this.m_PostProcessor = value;
/*  282:     */   }
/*  283:     */   
/*  284:     */   public PostProcessor getPostProcessor()
/*  285:     */   {
/*  286: 511 */     return this.m_PostProcessor;
/*  287:     */   }
/*  288:     */   
/*  289:     */   public boolean hasClasspathProblems()
/*  290:     */   {
/*  291: 520 */     return this.m_ClasspathProblems;
/*  292:     */   }
/*  293:     */   
/*  294:     */   public void doTests()
/*  295:     */   {
/*  296: 528 */     if (getEstimator() == null)
/*  297:     */     {
/*  298: 529 */       println("\n=== No estimator set ===");
/*  299: 530 */       return;
/*  300:     */     }
/*  301: 532 */     println("\n=== Check on Estimator: " + getEstimator().getClass().getName() + " ===\n");
/*  302:     */     
/*  303:     */ 
/*  304: 535 */     this.m_ClasspathProblems = false;
/*  305:     */     
/*  306:     */ 
/*  307: 538 */     canTakeOptions();
/*  308:     */     
/*  309:     */ 
/*  310: 541 */     EstTypes estTypes = new EstTypes();
/*  311: 542 */     estTypes.incremental = incrementalEstimator()[0];
/*  312: 543 */     estTypes.weighted = weightedInstancesHandler()[0];
/*  313: 544 */     estTypes.supervised = supervisedEstimator()[0];
/*  314:     */     
/*  315:     */ 
/*  316:     */ 
/*  317:     */ 
/*  318:     */ 
/*  319: 550 */     int classType = 1;
/*  320: 551 */     AttrTypes attrTypes = testsPerClassType(classType, estTypes);
/*  321:     */     
/*  322:     */ 
/*  323: 554 */     canSplitUpClass(attrTypes, classType);
/*  324:     */   }
/*  325:     */   
/*  326:     */   public void setDebug(boolean debug)
/*  327:     */   {
/*  328: 563 */     this.m_Debug = debug;
/*  329: 566 */     if (getDebug()) {
/*  330: 567 */       setSilent(false);
/*  331:     */     }
/*  332:     */   }
/*  333:     */   
/*  334:     */   public boolean getDebug()
/*  335:     */   {
/*  336: 577 */     return this.m_Debug;
/*  337:     */   }
/*  338:     */   
/*  339:     */   public void setSilent(boolean value)
/*  340:     */   {
/*  341: 586 */     this.m_Silent = value;
/*  342:     */   }
/*  343:     */   
/*  344:     */   public boolean getSilent()
/*  345:     */   {
/*  346: 595 */     return this.m_Silent;
/*  347:     */   }
/*  348:     */   
/*  349:     */   public void setNumInstances(int value)
/*  350:     */   {
/*  351: 605 */     this.m_NumInstances = value;
/*  352:     */   }
/*  353:     */   
/*  354:     */   public int getNumInstances()
/*  355:     */   {
/*  356: 614 */     return this.m_NumInstances;
/*  357:     */   }
/*  358:     */   
/*  359:     */   public void setEstimator(Estimator newEstimator)
/*  360:     */   {
/*  361: 623 */     this.m_Estimator = newEstimator;
/*  362:     */   }
/*  363:     */   
/*  364:     */   public Estimator getEstimator()
/*  365:     */   {
/*  366: 632 */     return this.m_Estimator;
/*  367:     */   }
/*  368:     */   
/*  369:     */   protected void print(Object msg)
/*  370:     */   {
/*  371: 641 */     if (!getSilent()) {
/*  372: 642 */       System.out.print(msg);
/*  373:     */     }
/*  374:     */   }
/*  375:     */   
/*  376:     */   protected void println(Object msg)
/*  377:     */   {
/*  378: 652 */     print(msg + "\n");
/*  379:     */   }
/*  380:     */   
/*  381:     */   protected void println()
/*  382:     */   {
/*  383: 659 */     print("\n");
/*  384:     */   }
/*  385:     */   
/*  386:     */   protected AttrTypes testsPerClassType(int classType, EstTypes estTypes)
/*  387:     */   {
/*  388: 678 */     AttrTypes attrTypes = new AttrTypes();
/*  389: 679 */     AttrTypes at = new AttrTypes(1);
/*  390: 680 */     attrTypes.nominal = canEstimate(at, estTypes.supervised, classType)[0];
/*  391: 681 */     at = new AttrTypes(0);
/*  392: 682 */     attrTypes.numeric = canEstimate(at, estTypes.supervised, classType)[0];
/*  393: 683 */     attrTypes.string = false;
/*  394: 684 */     attrTypes.date = false;
/*  395: 685 */     attrTypes.relational = false;
/*  396: 694 */     if (attrTypes.oneIsSet())
/*  397:     */     {
/*  398: 695 */       Vector<Integer> attributesSet = attrTypes.getVectorOfAttrTypes();
/*  399: 698 */       for (int i = 0; i < attributesSet.size(); i++)
/*  400:     */       {
/*  401: 699 */         AttrTypes workAttrTypes = new AttrTypes(((Integer)attributesSet.elementAt(i)).intValue());
/*  402: 703 */         if (estTypes.weighted) {
/*  403: 704 */           instanceWeights(workAttrTypes, classType);
/*  404:     */         }
/*  405: 707 */         if (classType == 1)
/*  406:     */         {
/*  407: 708 */           int numClasses = 4;
/*  408: 709 */           canHandleNClasses(workAttrTypes, numClasses);
/*  409:     */         }
/*  410: 716 */         int numAtt = 4;
/*  411:     */         
/*  412: 718 */         canHandleClassAsNthAttribute(workAttrTypes, numAtt, 0, classType, 1);
/*  413:     */         
/*  414:     */ 
/*  415:     */ 
/*  416:     */ 
/*  417:     */ 
/*  418: 724 */         canHandleZeroTraining(workAttrTypes, classType);
/*  419: 725 */         boolean handleMissingAttributes = canHandleMissing(workAttrTypes, classType, true, false, 20)[0];
/*  420: 727 */         if (handleMissingAttributes) {
/*  421: 728 */           canHandleMissing(workAttrTypes, classType, true, false, 100);
/*  422:     */         }
/*  423: 731 */         boolean handleMissingClass = canHandleMissing(workAttrTypes, classType, false, true, 20)[0];
/*  424: 733 */         if (handleMissingClass) {
/*  425: 734 */           canHandleMissing(workAttrTypes, classType, false, true, 100);
/*  426:     */         }
/*  427: 737 */         correctBuildInitialisation(workAttrTypes, classType);
/*  428: 738 */         datasetIntegrity(workAttrTypes, classType, handleMissingAttributes, handleMissingClass);
/*  429: 741 */         if (estTypes.incremental) {
/*  430: 742 */           incrementingEquality(workAttrTypes, classType);
/*  431:     */         }
/*  432:     */       }
/*  433:     */     }
/*  434: 746 */     return attrTypes;
/*  435:     */   }
/*  436:     */   
/*  437:     */   protected boolean[] canTakeOptions()
/*  438:     */   {
/*  439: 756 */     boolean[] result = new boolean[2];
/*  440:     */     
/*  441: 758 */     print("options...");
/*  442: 759 */     if ((this.m_Estimator instanceof OptionHandler))
/*  443:     */     {
/*  444: 760 */       println("yes");
/*  445: 761 */       if (this.m_Debug)
/*  446:     */       {
/*  447: 762 */         println("\n=== Full report ===");
/*  448: 763 */         Enumeration<Option> enu = this.m_Estimator.listOptions();
/*  449: 764 */         while (enu.hasMoreElements())
/*  450:     */         {
/*  451: 765 */           Option option = (Option)enu.nextElement();
/*  452: 766 */           print(option.synopsis() + "\n" + option.description() + "\n");
/*  453:     */         }
/*  454: 768 */         println("\n");
/*  455:     */       }
/*  456: 770 */       result[0] = true;
/*  457:     */     }
/*  458:     */     else
/*  459:     */     {
/*  460: 772 */       println("no");
/*  461: 773 */       result[0] = false;
/*  462:     */     }
/*  463: 776 */     return result;
/*  464:     */   }
/*  465:     */   
/*  466:     */   protected boolean[] incrementalEstimator()
/*  467:     */   {
/*  468: 786 */     boolean[] result = new boolean[2];
/*  469:     */     
/*  470: 788 */     print("incremental estimator...");
/*  471: 789 */     if ((this.m_Estimator instanceof IncrementalEstimator))
/*  472:     */     {
/*  473: 790 */       println("yes");
/*  474: 791 */       result[0] = true;
/*  475:     */     }
/*  476:     */     else
/*  477:     */     {
/*  478: 793 */       println("no");
/*  479: 794 */       result[0] = false;
/*  480:     */     }
/*  481: 797 */     return result;
/*  482:     */   }
/*  483:     */   
/*  484:     */   protected boolean[] weightedInstancesHandler()
/*  485:     */   {
/*  486: 807 */     boolean[] result = new boolean[2];
/*  487:     */     
/*  488: 809 */     print("weighted instances estimator...");
/*  489: 810 */     if ((this.m_Estimator instanceof WeightedInstancesHandler))
/*  490:     */     {
/*  491: 811 */       println("yes");
/*  492: 812 */       result[0] = true;
/*  493:     */     }
/*  494:     */     else
/*  495:     */     {
/*  496: 814 */       println("no");
/*  497: 815 */       result[0] = false;
/*  498:     */     }
/*  499: 818 */     return result;
/*  500:     */   }
/*  501:     */   
/*  502:     */   protected boolean[] supervisedEstimator()
/*  503:     */   {
/*  504: 827 */     boolean[] result = new boolean[2];
/*  505: 828 */     result[0] = false;
/*  506: 829 */     return result;
/*  507:     */   }
/*  508:     */   
/*  509:     */   protected boolean[] canEstimate(AttrTypes attrTypes, boolean supervised, int classType)
/*  510:     */   {
/*  511: 846 */     print("basic estimation");
/*  512: 847 */     printAttributeSummary(attrTypes, classType);
/*  513: 848 */     print("...");
/*  514: 849 */     ArrayList<String> accepts = new ArrayList();
/*  515: 850 */     accepts.add("nominal");
/*  516: 851 */     accepts.add("numeric");
/*  517: 852 */     accepts.add("string");
/*  518: 853 */     accepts.add("date");
/*  519: 854 */     accepts.add("relational");
/*  520: 855 */     accepts.add("not in classpath");
/*  521: 856 */     int numTrain = getNumInstances();int numTest = getNumInstances();int numClasses = 2;int missingLevel = 0;
/*  522: 857 */     boolean attributeMissing = false;boolean classMissing = false;
/*  523: 858 */     int numAtts = 1;int attrIndex = 0;
/*  524:     */     
/*  525: 860 */     return runBasicTest(attrTypes, numAtts, attrIndex, classType, missingLevel, attributeMissing, classMissing, numTrain, numTest, numClasses, accepts);
/*  526:     */   }
/*  527:     */   
/*  528:     */   protected void canSplitUpClass(AttrTypes attrTypes, int classType)
/*  529:     */   {
/*  530: 873 */     if (attrTypes.nominal) {
/*  531: 874 */       canSplitUpClass(1, classType);
/*  532:     */     }
/*  533: 876 */     if (attrTypes.numeric) {
/*  534: 877 */       canSplitUpClass(0, classType);
/*  535:     */     }
/*  536:     */   }
/*  537:     */   
/*  538:     */   protected boolean[] canSplitUpClass(int attrType, int classType)
/*  539:     */   {
/*  540: 892 */     boolean[] result = new boolean[2];
/*  541:     */     
/*  542: 894 */     ArrayList<String> accepts = new ArrayList();
/*  543: 895 */     accepts.add("not in classpath");
/*  544:     */     
/*  545:     */ 
/*  546: 898 */     print("split per class type ");
/*  547: 899 */     printAttributeSummary(attrType, 1);
/*  548: 900 */     print("...");
/*  549:     */     
/*  550: 902 */     int numTrain = getNumInstances();int numTest = getNumInstances();int numClasses = 2;
/*  551: 903 */     int numAtts = 3;int attrIndex = 0;int classIndex = 1;
/*  552: 904 */     Instances train = null;
/*  553:     */     
/*  554: 906 */     Estimator estimator = null;
/*  555: 907 */     boolean built = false;
/*  556:     */     Vector<Double> test;
/*  557:     */     try
/*  558:     */     {
/*  559: 910 */       AttrTypes at = new AttrTypes(attrType);
/*  560: 911 */       train = makeTestDataset(42, numTrain, numAtts, at, numClasses, classType, classIndex);
/*  561:     */       
/*  562:     */ 
/*  563:     */ 
/*  564: 915 */       test = makeTestValueList(24, numTest, train, attrIndex, attrType);
/*  565:     */       
/*  566: 917 */       estimator = Estimator.makeCopies(getEstimator(), 1)[0];
/*  567:     */     }
/*  568:     */     catch (Exception ex)
/*  569:     */     {
/*  570: 919 */       ex.printStackTrace();
/*  571: 920 */       throw new Error("Error setting up for tests: " + ex.getMessage());
/*  572:     */     }
/*  573:     */     try
/*  574:     */     {
/*  575: 923 */       estimator.addValues(train, attrIndex, classType, classIndex);
/*  576: 924 */       built = true;
/*  577:     */       
/*  578: 926 */       testWithTestValues(estimator, test);
/*  579:     */       
/*  580: 928 */       println("yes");
/*  581: 929 */       result[0] = true;
/*  582:     */     }
/*  583:     */     catch (Exception ex)
/*  584:     */     {
/*  585: 931 */       boolean acceptable = false;
/*  586:     */       String msg;
/*  587:     */       String msg;
/*  588: 933 */       if (ex.getMessage() == null) {
/*  589: 934 */         msg = "";
/*  590:     */       } else {
/*  591: 936 */         msg = ex.getMessage().toLowerCase();
/*  592:     */       }
/*  593: 938 */       if (msg.indexOf("not in classpath") > -1) {
/*  594: 939 */         this.m_ClasspathProblems = true;
/*  595:     */       }
/*  596: 942 */       for (int i = 0; i < accepts.size(); i++) {
/*  597: 943 */         if (msg.indexOf((String)accepts.get(i)) >= 0) {
/*  598: 944 */           acceptable = true;
/*  599:     */         }
/*  600:     */       }
/*  601: 948 */       println("no" + (acceptable ? " (OK error message)" : ""));
/*  602: 949 */       result[1] = acceptable;
/*  603: 951 */       if (this.m_Debug)
/*  604:     */       {
/*  605: 952 */         println("\n=== Full Report ===");
/*  606: 953 */         print("Problem during");
/*  607: 954 */         if (built) {
/*  608: 955 */           print(" testing");
/*  609:     */         } else {
/*  610: 957 */           print(" training");
/*  611:     */         }
/*  612: 959 */         println(": " + ex.getMessage() + "\n");
/*  613: 960 */         if (!acceptable)
/*  614:     */         {
/*  615: 961 */           if (accepts.size() > 0)
/*  616:     */           {
/*  617: 962 */             print("Error message doesn't mention ");
/*  618: 963 */             for (int i = 0; i < accepts.size(); i++)
/*  619:     */             {
/*  620: 964 */               if (i != 0) {
/*  621: 965 */                 print(" or ");
/*  622:     */               }
/*  623: 967 */               print('"' + (String)accepts.get(i) + '"');
/*  624:     */             }
/*  625:     */           }
/*  626: 970 */           println("here are the datasets:\n");
/*  627: 971 */           println("=== Train Dataset ===\n" + train.toString() + "\n");
/*  628: 972 */           println("=== Test Dataset ===\n" + test.toString() + "\n\n");
/*  629:     */         }
/*  630:     */       }
/*  631:     */     }
/*  632: 977 */     return result;
/*  633:     */   }
/*  634:     */   
/*  635:     */   protected boolean[] canHandleNClasses(AttrTypes attrTypes, int numClasses)
/*  636:     */   {
/*  637: 992 */     print("more than two class problems");
/*  638: 993 */     printAttributeSummary(attrTypes, 1);
/*  639: 994 */     print("...");
/*  640:     */     
/*  641: 996 */     ArrayList<String> accepts = new ArrayList();
/*  642: 997 */     accepts.add("number");
/*  643: 998 */     accepts.add("class");
/*  644:     */     
/*  645:1000 */     int numTrain = getNumInstances();int numTest = getNumInstances();int missingLevel = 0;
/*  646:1001 */     boolean attributeMissing = false;boolean classMissing = false;
/*  647:1002 */     int numAttr = 1;int attrIndex = 0;
/*  648:     */     
/*  649:1004 */     return runBasicTest(attrTypes, numAttr, attrIndex, 1, missingLevel, attributeMissing, classMissing, numTrain, numTest, numClasses, accepts);
/*  650:     */   }
/*  651:     */   
/*  652:     */   protected boolean[] canHandleClassAsNthAttribute(AttrTypes attrTypes, int numAtts, int attrIndex, int classType, int classIndex)
/*  653:     */   {
/*  654:1025 */     if (classIndex == -1) {
/*  655:1026 */       print("class attribute as last attribute");
/*  656:     */     } else {
/*  657:1028 */       print("class attribute as " + (classIndex + 1) + ". attribute");
/*  658:     */     }
/*  659:1030 */     printAttributeSummary(attrTypes, classType);
/*  660:1031 */     print("...");
/*  661:1032 */     ArrayList<String> accepts = new ArrayList();
/*  662:1033 */     int numTrain = getNumInstances();int numTest = getNumInstances();int numClasses = 2;int missingLevel = 0;
/*  663:1034 */     boolean attributeMissing = false;boolean classMissing = false;
/*  664:     */     
/*  665:1036 */     return runBasicTest(attrTypes, numAtts, attrIndex, classType, classIndex, missingLevel, attributeMissing, classMissing, numTrain, numTest, numClasses, accepts);
/*  666:     */   }
/*  667:     */   
/*  668:     */   protected boolean[] canHandleZeroTraining(AttrTypes attrTypes, int classType)
/*  669:     */   {
/*  670:1051 */     print("handle zero training instances");
/*  671:1052 */     printAttributeSummary(attrTypes, classType);
/*  672:     */     
/*  673:1054 */     print("...");
/*  674:1055 */     ArrayList<String> accepts = new ArrayList();
/*  675:1056 */     accepts.add("train");
/*  676:1057 */     accepts.add("value");
/*  677:1058 */     int numTrain = 0;int numTest = getNumInstances();int numClasses = 2;int missingLevel = 0;
/*  678:1059 */     boolean attributeMissing = false;boolean classMissing = false;
/*  679:1060 */     int numAtts = 1;
/*  680:1061 */     int attrIndex = 0;
/*  681:1062 */     return runBasicTest(attrTypes, numAtts, attrIndex, classType, missingLevel, attributeMissing, classMissing, numTrain, numTest, numClasses, accepts);
/*  682:     */   }
/*  683:     */   
/*  684:     */   protected boolean[] correctBuildInitialisation(AttrTypes attrTypes, int classType)
/*  685:     */   {
/*  686:1084 */     boolean[] result = new boolean[2];
/*  687:     */     
/*  688:1086 */     print("correct initialisation during buildEstimator");
/*  689:1087 */     printAttributeSummary(attrTypes, classType);
/*  690:     */     
/*  691:1089 */     print("...");
/*  692:1090 */     int numTrain = getNumInstances();
/*  693:1091 */     getNumInstances();
/*  694:1092 */     int numClasses = 2;int missingLevel = 0;
/*  695:1093 */     boolean attributeMissing = false;boolean classMissing = false;
/*  696:     */     
/*  697:1095 */     Instances train1 = null;
/*  698:1096 */     Instances train2 = null;
/*  699:1097 */     Estimator estimator = null;
/*  700:1098 */     Estimator estimator1 = null;
/*  701:     */     
/*  702:1100 */     boolean built = false;
/*  703:1101 */     int stage = 0;
/*  704:1102 */     int attrIndex1 = 1;
/*  705:1103 */     int attrIndex2 = 2;
/*  706:     */     try
/*  707:     */     {
/*  708:1109 */       train1 = makeTestDataset(42, numTrain, 2, attrTypes, numClasses, classType);
/*  709:     */       
/*  710:1111 */       train2 = makeTestDataset(84, numTrain, 3, attrTypes, numClasses, classType);
/*  711:1113 */       if (missingLevel > 0)
/*  712:     */       {
/*  713:1114 */         addMissing(train1, missingLevel, attributeMissing, classMissing, attrIndex1);
/*  714:     */         
/*  715:1116 */         addMissing(train2, missingLevel, attributeMissing, classMissing, attrIndex2);
/*  716:     */       }
/*  717:1120 */       estimator = Estimator.makeCopies(getEstimator(), 1)[0];
/*  718:     */     }
/*  719:     */     catch (Exception ex)
/*  720:     */     {
/*  721:1122 */       throw new Error("Error setting up for tests: " + ex.getMessage());
/*  722:     */     }
/*  723:     */     try
/*  724:     */     {
/*  725:1126 */       stage = 0;
/*  726:1127 */       estimator.addValues(train1, attrIndex1);
/*  727:1128 */       built = true;
/*  728:     */       
/*  729:1130 */       estimator1 = Estimator.makeCopies(getEstimator(), 1)[0];
/*  730:     */       
/*  731:1132 */       stage = 1;
/*  732:1133 */       built = false;
/*  733:1134 */       estimator.addValues(train2, attrIndex2);
/*  734:1135 */       built = true;
/*  735:     */       
/*  736:1137 */       stage = 2;
/*  737:1138 */       built = false;
/*  738:1139 */       estimator.addValues(train1, attrIndex1);
/*  739:1140 */       built = true;
/*  740:     */       
/*  741:1142 */       stage = 3;
/*  742:1143 */       if (!estimator.equals(estimator1))
/*  743:     */       {
/*  744:1144 */         if (this.m_Debug)
/*  745:     */         {
/*  746:1145 */           println("\n=== Full report ===\n\nFirst build estimator\n" + estimator.toString() + "\n\n");
/*  747:     */           
/*  748:1147 */           println("\nSecond build estimator\n" + estimator.toString() + "\n\n");
/*  749:     */         }
/*  750:1149 */         throw new Exception("Results differ between buildEstimator calls");
/*  751:     */       }
/*  752:1151 */       println("yes");
/*  753:1152 */       result[0] = true;
/*  754:     */     }
/*  755:     */     catch (Exception ex)
/*  756:     */     {
/*  757:1155 */       String msg = ex.getMessage().toLowerCase();
/*  758:1156 */       if (msg.indexOf("worse than zeror") >= 0)
/*  759:     */       {
/*  760:1157 */         println("warning: performs worse than ZeroR");
/*  761:1158 */         result[0] = true;
/*  762:1159 */         result[1] = true;
/*  763:     */       }
/*  764:     */       else
/*  765:     */       {
/*  766:1161 */         println("no");
/*  767:1162 */         result[0] = false;
/*  768:     */       }
/*  769:1164 */       if (this.m_Debug)
/*  770:     */       {
/*  771:1165 */         println("\n=== Full Report ===");
/*  772:1166 */         print("Problem during");
/*  773:1167 */         if (built) {
/*  774:1168 */           print(" testing");
/*  775:     */         } else {
/*  776:1170 */           print(" training");
/*  777:     */         }
/*  778:1172 */         switch (stage)
/*  779:     */         {
/*  780:     */         case 0: 
/*  781:1174 */           print(" of dataset 1");
/*  782:1175 */           break;
/*  783:     */         case 1: 
/*  784:1177 */           print(" of dataset 2");
/*  785:1178 */           break;
/*  786:     */         case 2: 
/*  787:1180 */           print(" of dataset 1 (2nd build)");
/*  788:1181 */           break;
/*  789:     */         case 3: 
/*  790:1183 */           print(", comparing results from builds of dataset 1");
/*  791:     */         }
/*  792:1186 */         println(": " + ex.getMessage() + "\n");
/*  793:1187 */         println("here are the datasets:\n");
/*  794:1188 */         println("=== Train1 Dataset ===\n" + train1.toString() + "\n");
/*  795:1189 */         println("=== Train2 Dataset ===\n" + train2.toString() + "\n");
/*  796:     */       }
/*  797:     */     }
/*  798:1193 */     return result;
/*  799:     */   }
/*  800:     */   
/*  801:     */   protected boolean[] canHandleMissing(AttrTypes attrTypes, int classType, boolean attributeMissing, boolean classMissing, int missingLevel)
/*  802:     */   {
/*  803:1211 */     if (missingLevel == 100) {
/*  804:1212 */       print("100% ");
/*  805:     */     }
/*  806:1214 */     print("missing");
/*  807:1215 */     if (attributeMissing)
/*  808:     */     {
/*  809:1216 */       print(" attribute");
/*  810:1217 */       if (classMissing) {
/*  811:1218 */         print(" and");
/*  812:     */       }
/*  813:     */     }
/*  814:1221 */     if (classMissing) {
/*  815:1222 */       print(" class");
/*  816:     */     }
/*  817:1224 */     print(" values");
/*  818:1225 */     printAttributeSummary(attrTypes, classType);
/*  819:     */     
/*  820:1227 */     print("...");
/*  821:1228 */     ArrayList<String> accepts = new ArrayList();
/*  822:1229 */     accepts.add("missing");
/*  823:1230 */     accepts.add("value");
/*  824:1231 */     accepts.add("train");
/*  825:1232 */     int numTrain = getNumInstances();int numTest = getNumInstances();int numClasses = 2;
/*  826:     */     
/*  827:1234 */     int numAtts = 1;int attrIndex = 0;
/*  828:1235 */     return runBasicTest(attrTypes, numAtts, attrIndex, classType, missingLevel, attributeMissing, classMissing, numTrain, numTest, numClasses, accepts);
/*  829:     */   }
/*  830:     */   
/*  831:     */   protected boolean[] incrementingEquality(AttrTypes attrTypes, int classType)
/*  832:     */   {
/*  833:1251 */     print("incremental training produces the same results as batch training");
/*  834:     */     
/*  835:1253 */     printAttributeSummary(attrTypes, classType);
/*  836:     */     
/*  837:1255 */     print("...");
/*  838:1256 */     int numTrain = getNumInstances();int numTest = getNumInstances();int numClasses = 2;int missingLevel = 0;
/*  839:1257 */     boolean attributeMissing = false;boolean classMissing = false;
/*  840:     */     
/*  841:1259 */     boolean[] result = new boolean[2];
/*  842:1260 */     Instances train = null;
/*  843:1261 */     Estimator[] estimators = null;
/*  844:1262 */     boolean built = false;
/*  845:1263 */     int attrIndex = 0;
/*  846:     */     Vector<Double> test;
/*  847:     */     try
/*  848:     */     {
/*  849:1266 */       train = makeTestDataset(42, numTrain, 1, attrTypes, numClasses, classType);
/*  850:     */       
/*  851:     */ 
/*  852:1269 */       test = makeTestValueList(24, numTest, train, attrIndex, attrTypes.getSetType());
/*  853:1272 */       if (missingLevel > 0) {
/*  854:1273 */         addMissing(train, missingLevel, attributeMissing, classMissing, attrIndex);
/*  855:     */       }
/*  856:1276 */       estimators = Estimator.makeCopies(getEstimator(), 2);
/*  857:1277 */       estimators[0].addValues(train, attrIndex);
/*  858:     */     }
/*  859:     */     catch (Exception ex)
/*  860:     */     {
/*  861:1279 */       throw new Error("Error setting up for tests: " + ex.getMessage());
/*  862:     */     }
/*  863:     */     try
/*  864:     */     {
/*  865:1282 */       for (int i = 0; i < train.numInstances(); i++) {
/*  866:1283 */         ((IncrementalEstimator)estimators[1]).addValue(train.instance(i).value(attrIndex), 1.0D);
/*  867:     */       }
/*  868:1286 */       built = true;
/*  869:1287 */       if (!estimators[0].equals(estimators[1]))
/*  870:     */       {
/*  871:1288 */         println("no");
/*  872:1289 */         result[0] = false;
/*  873:1291 */         if (this.m_Debug)
/*  874:     */         {
/*  875:1292 */           println("\n=== Full Report ===");
/*  876:1293 */           println("Results differ between batch and incrementally built models.\nDepending on the estimator, this may be OK");
/*  877:     */           
/*  878:     */ 
/*  879:1296 */           println("Here are the results:\n");
/*  880:1297 */           println("batch built results\n" + estimators[0].toString());
/*  881:1298 */           println("incrementally built results\n" + estimators[1].toString());
/*  882:1299 */           println("Here are the datasets:\n");
/*  883:1300 */           println("=== Train Dataset ===\n" + train.toString() + "\n");
/*  884:1301 */           println("=== Test Dataset ===\n" + test.toString() + "\n\n");
/*  885:     */         }
/*  886:     */       }
/*  887:     */       else
/*  888:     */       {
/*  889:1304 */         println("yes");
/*  890:1305 */         result[0] = true;
/*  891:     */       }
/*  892:     */     }
/*  893:     */     catch (Exception ex)
/*  894:     */     {
/*  895:1308 */       result[0] = false;
/*  896:     */       
/*  897:1310 */       print("Problem during");
/*  898:1311 */       if (built) {
/*  899:1312 */         print(" testing");
/*  900:     */       } else {
/*  901:1314 */         print(" training");
/*  902:     */       }
/*  903:1316 */       println(": " + ex.getMessage() + "\n");
/*  904:     */     }
/*  905:1319 */     return result;
/*  906:     */   }
/*  907:     */   
/*  908:     */   protected boolean[] instanceWeights(AttrTypes attrTypes, int classType)
/*  909:     */   {
/*  910:1337 */     print("estimator uses instance weights");
/*  911:1338 */     printAttributeSummary(attrTypes, classType);
/*  912:     */     
/*  913:1340 */     print("...");
/*  914:     */     
/*  915:1342 */     int numTrain = 2 * getNumInstances();int numTest = getNumInstances();int numClasses = 2;int missingLevel = 0;
/*  916:1343 */     boolean attributeMissing = false;boolean classMissing = false;
/*  917:     */     
/*  918:1345 */     boolean[] result = new boolean[2];
/*  919:1346 */     Instances train = null;
/*  920:1347 */     Vector<Double> test = null;
/*  921:1348 */     Estimator[] estimators = null;
/*  922:     */     
/*  923:1350 */     Vector<Double> resultProbsO = null;
/*  924:1351 */     Vector<Double> resultProbsW = null;
/*  925:1352 */     boolean built = false;
/*  926:1353 */     boolean evalFail = false;
/*  927:1354 */     int attrIndex = 0;
/*  928:     */     try
/*  929:     */     {
/*  930:1356 */       train = makeTestDataset(42, numTrain, 1, attrTypes, numClasses, classType);
/*  931:     */       
/*  932:     */ 
/*  933:1359 */       test = makeTestValueList(24, numTest, train, attrIndex, attrTypes.getSetType());
/*  934:1362 */       if (missingLevel > 0) {
/*  935:1363 */         addMissing(train, missingLevel, attributeMissing, classMissing, attrIndex);
/*  936:     */       }
/*  937:1367 */       estimators = Estimator.makeCopies(getEstimator(), 2);
/*  938:     */       
/*  939:1369 */       estimators[0].addValues(train, attrIndex);
/*  940:1370 */       resultProbsO = testWithTestValues(estimators[0], test);
/*  941:     */     }
/*  942:     */     catch (Exception ex)
/*  943:     */     {
/*  944:1373 */       throw new Error("Error setting up for tests: " + ex.getMessage());
/*  945:     */     }
/*  946:     */     try
/*  947:     */     {
/*  948:1378 */       for (int i = 0; i < train.numInstances(); i++) {
/*  949:1379 */         train.instance(i).setWeight(0.0D);
/*  950:     */       }
/*  951:1381 */       Random random = new Random(1L);
/*  952:1382 */       for (int i = 0; i < train.numInstances() / 2; i++)
/*  953:     */       {
/*  954:1383 */         int inst = random.nextInt(train.numInstances());
/*  955:1384 */         int weight = random.nextInt(10) + 1;
/*  956:1385 */         train.instance(inst).setWeight(weight);
/*  957:     */       }
/*  958:1387 */       estimators[1].addValues(train, attrIndex);
/*  959:1388 */       resultProbsW = testWithTestValues(estimators[1], test);
/*  960:     */       
/*  961:1390 */       built = true;
/*  962:1391 */       if (resultProbsO.equals(resultProbsW))
/*  963:     */       {
/*  964:1393 */         evalFail = true;
/*  965:1394 */         throw new Exception("evalFail");
/*  966:     */       }
/*  967:1397 */       println("yes");
/*  968:1398 */       result[0] = true;
/*  969:     */     }
/*  970:     */     catch (Exception ex)
/*  971:     */     {
/*  972:1400 */       println("no");
/*  973:1401 */       result[0] = false;
/*  974:1403 */       if (this.m_Debug)
/*  975:     */       {
/*  976:1404 */         println("\n=== Full Report ===");
/*  977:1406 */         if (evalFail)
/*  978:     */         {
/*  979:1407 */           println("Results don't differ between non-weighted and weighted instance models.");
/*  980:     */           
/*  981:1409 */           println("Here are the results:\n");
/*  982:1410 */           println(probsToString(resultProbsO));
/*  983:     */         }
/*  984:     */         else
/*  985:     */         {
/*  986:1412 */           print("Problem during");
/*  987:1413 */           if (built) {
/*  988:1414 */             print(" testing");
/*  989:     */           } else {
/*  990:1416 */             print(" training");
/*  991:     */           }
/*  992:1418 */           println(": " + ex.getMessage() + "\n");
/*  993:     */         }
/*  994:1420 */         println("Here are the datasets:\n");
/*  995:1421 */         println("=== Train Dataset ===\n" + train.toString() + "\n");
/*  996:1422 */         println("=== Train Weights ===\n");
/*  997:1423 */         for (int i = 0; i < train.numInstances(); i++) {
/*  998:1424 */           println(" " + (i + 1) + "    " + train.instance(i).weight());
/*  999:     */         }
/* 1000:1426 */         println("=== Test Dataset ===\n" + test.toString() + "\n\n");
/* 1001:1427 */         println("(test weights all 1.0\n");
/* 1002:     */       }
/* 1003:     */     }
/* 1004:1431 */     return result;
/* 1005:     */   }
/* 1006:     */   
/* 1007:     */   protected boolean[] datasetIntegrity(AttrTypes attrTypes, int classType, boolean attributeMissing, boolean classMissing)
/* 1008:     */   {
/* 1009:1451 */     Estimator estimator = null;
/* 1010:1452 */     print("estimator doesn't alter original datasets");
/* 1011:1453 */     printAttributeSummary(attrTypes, classType);
/* 1012:1454 */     print("...");
/* 1013:1455 */     int numTrain = getNumInstances();
/* 1014:1456 */     getNumInstances();
/* 1015:1457 */     int numClasses = 2;int missingLevel = 100;
/* 1016:     */     
/* 1017:1459 */     boolean[] result = new boolean[2];
/* 1018:1460 */     Instances train = null;
/* 1019:1461 */     boolean built = false;
/* 1020:     */     try
/* 1021:     */     {
/* 1022:1463 */       train = makeTestDataset(42, numTrain, 1, attrTypes, numClasses, classType);
/* 1023:1464 */       int attrIndex = 0;
/* 1024:1466 */       if (missingLevel > 0) {
/* 1025:1467 */         addMissing(train, missingLevel, attributeMissing, classMissing, attrIndex);
/* 1026:     */       }
/* 1027:1470 */       estimator = Estimator.makeCopies(getEstimator(), 1)[0];
/* 1028:     */     }
/* 1029:     */     catch (Exception ex)
/* 1030:     */     {
/* 1031:1472 */       throw new Error("Error setting up for tests: " + ex.getMessage());
/* 1032:     */     }
/* 1033:     */     try
/* 1034:     */     {
/* 1035:1475 */       Instances trainCopy = new Instances(train);
/* 1036:1476 */       int attrIndex = 0;
/* 1037:1477 */       estimator.addValues(trainCopy, attrIndex);
/* 1038:1478 */       compareDatasets(train, trainCopy);
/* 1039:1479 */       built = true;
/* 1040:     */       
/* 1041:1481 */       println("yes");
/* 1042:1482 */       result[0] = true;
/* 1043:     */     }
/* 1044:     */     catch (Exception ex)
/* 1045:     */     {
/* 1046:1484 */       println("no");
/* 1047:1485 */       result[0] = false;
/* 1048:1487 */       if (this.m_Debug)
/* 1049:     */       {
/* 1050:1488 */         println("\n=== Full Report ===");
/* 1051:1489 */         print("Problem during");
/* 1052:1490 */         if (built) {
/* 1053:1491 */           print(" testing");
/* 1054:     */         } else {
/* 1055:1493 */           print(" training");
/* 1056:     */         }
/* 1057:1495 */         println(": " + ex.getMessage() + "\n");
/* 1058:1496 */         println("Here are the datasets:\n");
/* 1059:1497 */         println("=== Train Dataset ===\n" + train.toString() + "\n");
/* 1060:     */       }
/* 1061:     */     }
/* 1062:1501 */     return result;
/* 1063:     */   }
/* 1064:     */   
/* 1065:     */   protected boolean[] runBasicTest(AttrTypes attrTypes, int numAtts, int attrIndex, int classType, int missingLevel, boolean attributeMissing, boolean classMissing, int numTrain, int numTest, int numClasses, ArrayList<String> accepts)
/* 1066:     */   {
/* 1067:1526 */     return runBasicTest(attrTypes, numAtts, attrIndex, classType, -1, missingLevel, attributeMissing, classMissing, numTrain, numTest, numClasses, accepts);
/* 1068:     */   }
/* 1069:     */   
/* 1070:     */   protected boolean[] runBasicTest(AttrTypes attrTypes, int numAtts, int attrIndex, int classType, int classIndex, int missingLevel, boolean attributeMissing, boolean classMissing, int numTrain, int numTest, int numClasses, ArrayList<String> accepts)
/* 1071:     */   {
/* 1072:1553 */     boolean[] result = new boolean[2];
/* 1073:1554 */     Instances train = null;
/* 1074:1555 */     Vector<Double> test = null;
/* 1075:1556 */     Estimator estimator = null;
/* 1076:1557 */     boolean built = false;
/* 1077:     */     try
/* 1078:     */     {
/* 1079:1560 */       train = makeTestDataset(42, numTrain, numAtts, attrTypes, numClasses, classType, classIndex);
/* 1080:1564 */       if (numTrain > 0)
/* 1081:     */       {
/* 1082:1565 */         test = makeTestValueList(24, numTest, train, attrIndex, attrTypes.getSetType());
/* 1083:     */       }
/* 1084:     */       else
/* 1085:     */       {
/* 1086:1569 */         double min = -10.0D;
/* 1087:1570 */         double max = 8.0D;
/* 1088:1571 */         test = makeTestValueList(24, numTest, min, max, attrTypes.getSetType());
/* 1089:     */       }
/* 1090:1574 */       if (missingLevel > 0) {
/* 1091:1575 */         addMissing(train, missingLevel, attributeMissing, classMissing, attrIndex);
/* 1092:     */       }
/* 1093:1578 */       estimator = Estimator.makeCopies(getEstimator(), 1)[0];
/* 1094:     */     }
/* 1095:     */     catch (Exception ex)
/* 1096:     */     {
/* 1097:1580 */       ex.printStackTrace();
/* 1098:1581 */       throw new Error("Error setting up for tests: " + ex.getMessage());
/* 1099:     */     }
/* 1100:     */     try
/* 1101:     */     {
/* 1102:1584 */       estimator.addValues(train, attrIndex);
/* 1103:1585 */       built = true;
/* 1104:     */       
/* 1105:1587 */       testWithTestValues(estimator, test);
/* 1106:     */       
/* 1107:1589 */       println("yes");
/* 1108:1590 */       result[0] = true;
/* 1109:     */     }
/* 1110:     */     catch (Exception ex)
/* 1111:     */     {
/* 1112:1592 */       boolean acceptable = false;
/* 1113:     */       String msg;
/* 1114:     */       String msg;
/* 1115:1594 */       if (ex.getMessage() == null) {
/* 1116:1595 */         msg = "";
/* 1117:     */       } else {
/* 1118:1597 */         msg = ex.getMessage().toLowerCase();
/* 1119:     */       }
/* 1120:1599 */       if (msg.indexOf("not in classpath") > -1) {
/* 1121:1600 */         this.m_ClasspathProblems = true;
/* 1122:     */       }
/* 1123:1603 */       for (int i = 0; i < accepts.size(); i++) {
/* 1124:1604 */         if (msg.indexOf((String)accepts.get(i)) >= 0) {
/* 1125:1605 */           acceptable = true;
/* 1126:     */         }
/* 1127:     */       }
/* 1128:1609 */       println("no" + (acceptable ? " (OK error message)" : ""));
/* 1129:1610 */       result[1] = acceptable;
/* 1130:1612 */       if (this.m_Debug)
/* 1131:     */       {
/* 1132:1613 */         println("\n=== Full Report ===");
/* 1133:1614 */         print("Problem during");
/* 1134:1615 */         if (built) {
/* 1135:1616 */           print(" testing");
/* 1136:     */         } else {
/* 1137:1618 */           print(" training");
/* 1138:     */         }
/* 1139:1620 */         println(": " + ex.getMessage() + "\n");
/* 1140:1621 */         if (!acceptable)
/* 1141:     */         {
/* 1142:1622 */           if (accepts.size() > 0)
/* 1143:     */           {
/* 1144:1623 */             print("Error message doesn't mention ");
/* 1145:1624 */             for (int i = 0; i < accepts.size(); i++)
/* 1146:     */             {
/* 1147:1625 */               if (i != 0) {
/* 1148:1626 */                 print(" or ");
/* 1149:     */               }
/* 1150:1628 */               print('"' + (String)accepts.get(i) + '"');
/* 1151:     */             }
/* 1152:     */           }
/* 1153:1631 */           println("here are the datasets:\n");
/* 1154:1632 */           println("=== Train Dataset ===\n" + train.toString() + "\n");
/* 1155:1633 */           println("=== Test Dataset ===\n" + test.toString() + "\n\n");
/* 1156:     */         }
/* 1157:     */       }
/* 1158:     */     }
/* 1159:1638 */     return result;
/* 1160:     */   }
/* 1161:     */   
/* 1162:     */   protected void compareDatasets(Instances data1, Instances data2)
/* 1163:     */     throws Exception
/* 1164:     */   {
/* 1165:1650 */     if (!data2.equalHeaders(data1)) {
/* 1166:1651 */       throw new Exception("header has been modified\n" + data2.equalHeadersMsg(data1));
/* 1167:     */     }
/* 1168:1654 */     if (data2.numInstances() != data1.numInstances()) {
/* 1169:1655 */       throw new Exception("number of instances has changed");
/* 1170:     */     }
/* 1171:1657 */     for (int i = 0; i < data2.numInstances(); i++)
/* 1172:     */     {
/* 1173:1658 */       Instance orig = data1.instance(i);
/* 1174:1659 */       Instance copy = data2.instance(i);
/* 1175:1660 */       for (int j = 0; j < orig.numAttributes(); j++)
/* 1176:     */       {
/* 1177:1661 */         if (orig.isMissing(j))
/* 1178:     */         {
/* 1179:1662 */           if (!copy.isMissing(j)) {
/* 1180:1663 */             throw new Exception("instances have changed");
/* 1181:     */           }
/* 1182:     */         }
/* 1183:1665 */         else if (orig.value(j) != copy.value(j)) {
/* 1184:1666 */           throw new Exception("instances have changed");
/* 1185:     */         }
/* 1186:1668 */         if (orig.weight() != copy.weight()) {
/* 1187:1669 */           throw new Exception("instance weights have changed");
/* 1188:     */         }
/* 1189:     */       }
/* 1190:     */     }
/* 1191:     */   }
/* 1192:     */   
/* 1193:     */   protected void addMissing(Instances data, int level, boolean attributeMissing, boolean classMissing, int attrIndex)
/* 1194:     */   {
/* 1195:1689 */     int classIndex = data.classIndex();
/* 1196:1690 */     Random random = new Random(1L);
/* 1197:1691 */     for (int i = 0; i < data.numInstances(); i++)
/* 1198:     */     {
/* 1199:1692 */       Instance current = data.instance(i);
/* 1200:1694 */       for (int j = 0; j < data.numAttributes(); j++) {
/* 1201:1695 */         if (((j == classIndex) && (classMissing)) || ((j == attrIndex) && (attributeMissing))) {
/* 1202:1697 */           if (random.nextInt(100) < level) {
/* 1203:1698 */             current.setMissing(j);
/* 1204:     */           }
/* 1205:     */         }
/* 1206:     */       }
/* 1207:     */     }
/* 1208:     */   }
/* 1209:     */   
/* 1210:     */   protected Instances makeTestDataset(int seed, int numInstances, int numAttr, AttrTypes attrTypes, int numClasses, int classType)
/* 1211:     */     throws Exception
/* 1212:     */   {
/* 1213:1722 */     return makeTestDataset(seed, numInstances, numAttr, attrTypes, numClasses, classType, -1);
/* 1214:     */   }
/* 1215:     */   
/* 1216:     */   protected Instances makeTestDataset(int seed, int numInstances, int numAttr, AttrTypes attrTypes, int numClasses, int classType, int classIndex)
/* 1217:     */     throws Exception
/* 1218:     */   {
/* 1219:1746 */     TestInstances dataset = new TestInstances();
/* 1220:     */     
/* 1221:1748 */     dataset.setSeed(seed);
/* 1222:1749 */     dataset.setNumInstances(numInstances);
/* 1223:1750 */     dataset.setNumNominal(attrTypes.nominal ? numAttr : 0);
/* 1224:1751 */     dataset.setNumNumeric(attrTypes.numeric ? numAttr : 0);
/* 1225:1752 */     dataset.setNumString(attrTypes.string ? numAttr : 0);
/* 1226:1753 */     dataset.setNumDate(attrTypes.date ? numAttr : 0);
/* 1227:1754 */     dataset.setNumRelational(attrTypes.relational ? numAttr : 0);
/* 1228:1755 */     dataset.setNumClasses(numClasses);
/* 1229:1756 */     dataset.setClassType(classType);
/* 1230:1757 */     dataset.setClassIndex(classIndex);
/* 1231:     */     
/* 1232:1759 */     return process(dataset.generate());
/* 1233:     */   }
/* 1234:     */   
/* 1235:     */   protected Vector<Double> makeTestValueList(int seed, int numValues, Instances data, int attrIndex, int attrType)
/* 1236:     */     throws Exception
/* 1237:     */   {
/* 1238:1779 */     double[] minMax = getMinimumMaximum(data, attrIndex);
/* 1239:1780 */     double minValue = minMax[0];
/* 1240:1781 */     double maxValue = minMax[1];
/* 1241:     */     
/* 1242:     */ 
/* 1243:1784 */     double range = maxValue - minValue;
/* 1244:1785 */     Vector<Double> values = new Vector(numValues);
/* 1245:1786 */     Random random = new Random(seed);
/* 1246:1788 */     if (attrType == 1) {
/* 1247:1789 */       for (int i = 0; i < numValues; i++)
/* 1248:     */       {
/* 1249:1790 */         Double v = new Double(random.nextInt((int)range) + (int)minValue);
/* 1250:     */         
/* 1251:1792 */         values.add(v);
/* 1252:     */       }
/* 1253:     */     }
/* 1254:1795 */     if (attrType == 0) {
/* 1255:1796 */       for (int i = 0; i < numValues; i++)
/* 1256:     */       {
/* 1257:1797 */         Double v = new Double(random.nextDouble() * range + minValue);
/* 1258:1798 */         values.add(v);
/* 1259:     */       }
/* 1260:     */     }
/* 1261:1801 */     return values;
/* 1262:     */   }
/* 1263:     */   
/* 1264:     */   protected Vector<Double> makeTestValueList(int seed, int numValues, double minValue, double maxValue, int attrType)
/* 1265:     */     throws Exception
/* 1266:     */   {
/* 1267:1821 */     double range = maxValue - minValue;
/* 1268:1822 */     Vector<Double> values = new Vector(numValues);
/* 1269:1823 */     Random random = new Random(seed);
/* 1270:1825 */     if (attrType == 1) {
/* 1271:1826 */       for (int i = 0; i < numValues; i++)
/* 1272:     */       {
/* 1273:1827 */         Double v = new Double(random.nextInt((int)range) + (int)minValue);
/* 1274:     */         
/* 1275:1829 */         values.add(v);
/* 1276:     */       }
/* 1277:     */     }
/* 1278:1832 */     if (attrType == 0) {
/* 1279:1833 */       for (int i = 0; i < numValues; i++)
/* 1280:     */       {
/* 1281:1834 */         Double v = new Double(random.nextDouble() * range + minValue);
/* 1282:1835 */         values.add(v);
/* 1283:     */       }
/* 1284:     */     }
/* 1285:1838 */     return values;
/* 1286:     */   }
/* 1287:     */   
/* 1288:     */   protected Vector<Double> testWithTestValues(Estimator est, Vector<Double> test)
/* 1289:     */   {
/* 1290:1850 */     Vector<Double> results = new Vector();
/* 1291:1851 */     for (int i = 0; i < test.size(); i++)
/* 1292:     */     {
/* 1293:1852 */       double testValue = ((Double)test.elementAt(i)).doubleValue();
/* 1294:1853 */       double prob = est.getProbability(testValue);
/* 1295:1854 */       Double p = new Double(prob);
/* 1296:1855 */       results.add(p);
/* 1297:     */     }
/* 1298:1857 */     return results;
/* 1299:     */   }
/* 1300:     */   
/* 1301:     */   protected double[] getMinimumMaximum(Instances inst, int attrIndex)
/* 1302:     */   {
/* 1303:1870 */     double[] minMax = new double[2];
/* 1304:     */     try
/* 1305:     */     {
/* 1306:1873 */       getMinMax(inst, attrIndex, minMax);
/* 1307:     */     }
/* 1308:     */     catch (Exception ex)
/* 1309:     */     {
/* 1310:1875 */       ex.printStackTrace();
/* 1311:1876 */       System.out.println(ex.getMessage());
/* 1312:     */     }
/* 1313:1878 */     return minMax;
/* 1314:     */   }
/* 1315:     */   
/* 1316:     */   public static int getMinMax(Instances inst, int attrIndex, double[] minMax)
/* 1317:     */     throws Exception
/* 1318:     */   {
/* 1319:1895 */     double min = (0.0D / 0.0D);
/* 1320:1896 */     double max = (0.0D / 0.0D);
/* 1321:1897 */     Instance instance = null;
/* 1322:1898 */     int numNotMissing = 0;
/* 1323:1899 */     if ((minMax == null) || (minMax.length < 2)) {
/* 1324:1900 */       throw new Exception("Error in Program, privat method getMinMax");
/* 1325:     */     }
/* 1326:1903 */     Enumeration<Instance> enumInst = inst.enumerateInstances();
/* 1327:1904 */     if (enumInst.hasMoreElements())
/* 1328:     */     {
/* 1329:     */       do
/* 1330:     */       {
/* 1331:1906 */         instance = (Instance)enumInst.nextElement();
/* 1332:1907 */       } while ((instance.isMissing(attrIndex)) && (enumInst.hasMoreElements()));
/* 1333:1910 */       if (!instance.isMissing(attrIndex))
/* 1334:     */       {
/* 1335:1911 */         numNotMissing++;
/* 1336:1912 */         min = instance.value(attrIndex);
/* 1337:1913 */         max = instance.value(attrIndex);
/* 1338:     */       }
/* 1339:1915 */       while (enumInst.hasMoreElements())
/* 1340:     */       {
/* 1341:1916 */         instance = (Instance)enumInst.nextElement();
/* 1342:1917 */         if (!instance.isMissing(attrIndex))
/* 1343:     */         {
/* 1344:1918 */           numNotMissing++;
/* 1345:1919 */           if (instance.value(attrIndex) < min) {
/* 1346:1920 */             min = instance.value(attrIndex);
/* 1347:1922 */           } else if (instance.value(attrIndex) > max) {
/* 1348:1923 */             max = instance.value(attrIndex);
/* 1349:     */           }
/* 1350:     */         }
/* 1351:     */       }
/* 1352:     */     }
/* 1353:1929 */     minMax[0] = min;
/* 1354:1930 */     minMax[1] = max;
/* 1355:1931 */     return numNotMissing;
/* 1356:     */   }
/* 1357:     */   
/* 1358:     */   private String probsToString(Vector<Double> probs)
/* 1359:     */   {
/* 1360:1941 */     StringBuffer txt = new StringBuffer(" ");
/* 1361:1942 */     for (int i = 0; i < probs.size(); i++) {
/* 1362:1943 */       txt.append("" + ((Double)probs.elementAt(i)).doubleValue() + " ");
/* 1363:     */     }
/* 1364:1945 */     return txt.toString();
/* 1365:     */   }
/* 1366:     */   
/* 1367:     */   protected Instances process(Instances data)
/* 1368:     */   {
/* 1369:1956 */     if (getPostProcessor() == null) {
/* 1370:1957 */       return data;
/* 1371:     */     }
/* 1372:1959 */     return getPostProcessor().process(data);
/* 1373:     */   }
/* 1374:     */   
/* 1375:     */   protected void printAttributeSummary(AttrTypes attrTypes, int classType)
/* 1376:     */   {
/* 1377:1971 */     String str = "";
/* 1378:1973 */     if (attrTypes.numeric) {
/* 1379:1974 */       str = str + " numeric";
/* 1380:     */     }
/* 1381:1977 */     if (attrTypes.nominal)
/* 1382:     */     {
/* 1383:1978 */       if (str.length() > 0) {
/* 1384:1979 */         str = str + " &";
/* 1385:     */       }
/* 1386:1981 */       str = str + " nominal";
/* 1387:     */     }
/* 1388:1984 */     if (attrTypes.string)
/* 1389:     */     {
/* 1390:1985 */       if (str.length() > 0) {
/* 1391:1986 */         str = str + " &";
/* 1392:     */       }
/* 1393:1988 */       str = str + " string";
/* 1394:     */     }
/* 1395:1991 */     if (attrTypes.date)
/* 1396:     */     {
/* 1397:1992 */       if (str.length() > 0) {
/* 1398:1993 */         str = str + " &";
/* 1399:     */       }
/* 1400:1995 */       str = str + " date";
/* 1401:     */     }
/* 1402:1998 */     if (attrTypes.relational)
/* 1403:     */     {
/* 1404:1999 */       if (str.length() > 0) {
/* 1405:2000 */         str = str + " &";
/* 1406:     */       }
/* 1407:2002 */       str = str + " relational";
/* 1408:     */     }
/* 1409:2005 */     str = str + " attributes)";
/* 1410:2007 */     switch (classType)
/* 1411:     */     {
/* 1412:     */     case 0: 
/* 1413:2009 */       str = " (numeric class," + str;
/* 1414:2010 */       break;
/* 1415:     */     case 1: 
/* 1416:2012 */       str = " (nominal class," + str;
/* 1417:2013 */       break;
/* 1418:     */     case 2: 
/* 1419:2015 */       str = " (string class," + str;
/* 1420:2016 */       break;
/* 1421:     */     case 3: 
/* 1422:2018 */       str = " (date class," + str;
/* 1423:2019 */       break;
/* 1424:     */     case 4: 
/* 1425:2021 */       str = " (relational class," + str;
/* 1426:     */     }
/* 1427:2025 */     print(str);
/* 1428:     */   }
/* 1429:     */   
/* 1430:     */   protected void printAttributeSummary(int attrType, int classType)
/* 1431:     */   {
/* 1432:2036 */     String str = "";
/* 1433:2038 */     switch (attrType)
/* 1434:     */     {
/* 1435:     */     case 0: 
/* 1436:2040 */       str = " numeric" + str;
/* 1437:2041 */       break;
/* 1438:     */     case 1: 
/* 1439:2043 */       str = " nominal" + str;
/* 1440:2044 */       break;
/* 1441:     */     case 2: 
/* 1442:2046 */       str = " string" + str;
/* 1443:2047 */       break;
/* 1444:     */     case 3: 
/* 1445:2049 */       str = " date" + str;
/* 1446:2050 */       break;
/* 1447:     */     case 4: 
/* 1448:2052 */       str = " relational" + str;
/* 1449:     */     }
/* 1450:2055 */     str = str + " attribute(s))";
/* 1451:2057 */     switch (classType)
/* 1452:     */     {
/* 1453:     */     case 0: 
/* 1454:2059 */       str = " (numeric class," + str;
/* 1455:2060 */       break;
/* 1456:     */     case 1: 
/* 1457:2062 */       str = " (nominal class," + str;
/* 1458:2063 */       break;
/* 1459:     */     case 2: 
/* 1460:2065 */       str = " (string class," + str;
/* 1461:2066 */       break;
/* 1462:     */     case 3: 
/* 1463:2068 */       str = " (date class," + str;
/* 1464:2069 */       break;
/* 1465:     */     case 4: 
/* 1466:2071 */       str = " (relational class," + str;
/* 1467:     */     }
/* 1468:2075 */     print(str);
/* 1469:     */   }
/* 1470:     */   
/* 1471:     */   public String getRevision()
/* 1472:     */   {
/* 1473:2085 */     return RevisionUtils.extract("$Revision: 11247 $");
/* 1474:     */   }
/* 1475:     */   
/* 1476:     */   public static void main(String[] args)
/* 1477:     */   {
/* 1478:     */     try
/* 1479:     */     {
/* 1480:2095 */       CheckEstimator check = new CheckEstimator();
/* 1481:     */       try
/* 1482:     */       {
/* 1483:2098 */         check.setOptions(args);
/* 1484:2099 */         Utils.checkForRemainingOptions(args);
/* 1485:     */       }
/* 1486:     */       catch (Exception ex)
/* 1487:     */       {
/* 1488:2101 */         String result = ex.getMessage() + "\n\n" + check.getClass().getName().replaceAll(".*\\.", "") + " Options:\n\n";
/* 1489:     */         
/* 1490:     */ 
/* 1491:2104 */         Enumeration<Option> enu = check.listOptions();
/* 1492:2105 */         while (enu.hasMoreElements())
/* 1493:     */         {
/* 1494:2106 */           Option option = (Option)enu.nextElement();
/* 1495:2107 */           result = result + option.synopsis() + "\n" + option.description() + "\n";
/* 1496:     */         }
/* 1497:2109 */         throw new Exception(result);
/* 1498:     */       }
/* 1499:2112 */       check.doTests();
/* 1500:     */     }
/* 1501:     */     catch (Exception ex)
/* 1502:     */     {
/* 1503:2114 */       System.err.println(ex.getMessage());
/* 1504:     */     }
/* 1505:     */   }
/* 1506:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.estimators.CheckEstimator
 * JD-Core Version:    0.7.0.1
 */