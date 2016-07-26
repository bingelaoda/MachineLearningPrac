/*    1:     */ package weka.datagenerators.classifiers.classification;
/*    2:     */ 
/*    3:     */ import java.io.Serializable;
/*    4:     */ import java.util.ArrayList;
/*    5:     */ import java.util.Collections;
/*    6:     */ import java.util.Enumeration;
/*    7:     */ import java.util.Random;
/*    8:     */ import java.util.Vector;
/*    9:     */ import weka.core.Attribute;
/*   10:     */ import weka.core.DenseInstance;
/*   11:     */ import weka.core.Instance;
/*   12:     */ import weka.core.Instances;
/*   13:     */ import weka.core.Option;
/*   14:     */ import weka.core.RevisionHandler;
/*   15:     */ import weka.core.RevisionUtils;
/*   16:     */ import weka.core.Utils;
/*   17:     */ import weka.core.WekaEnumeration;
/*   18:     */ import weka.datagenerators.ClassificationGenerator;
/*   19:     */ import weka.datagenerators.Test;
/*   20:     */ 
/*   21:     */ public class RDG1
/*   22:     */   extends ClassificationGenerator
/*   23:     */ {
/*   24:     */   static final long serialVersionUID = 7751005204635320414L;
/*   25:     */   protected int m_NumAttributes;
/*   26:     */   protected int m_NumClasses;
/*   27:     */   private int m_MaxRuleSize;
/*   28:     */   private int m_MinRuleSize;
/*   29:     */   private int m_NumIrrelevant;
/*   30:     */   private int m_NumNumeric;
/*   31:     */   
/*   32:     */   private class RuleList
/*   33:     */     implements Serializable, RevisionHandler
/*   34:     */   {
/*   35:     */     static final long serialVersionUID = 2830125413361938177L;
/*   36: 177 */     private ArrayList<Test> m_RuleList = null;
/*   37: 180 */     double m_ClassValue = 0.0D;
/*   38:     */     
/*   39:     */     private RuleList() {}
/*   40:     */     
/*   41:     */     public double getClassValue()
/*   42:     */     {
/*   43: 188 */       return this.m_ClassValue;
/*   44:     */     }
/*   45:     */     
/*   46:     */     public void setClassValue(double newClassValue)
/*   47:     */     {
/*   48: 197 */       this.m_ClassValue = newClassValue;
/*   49:     */     }
/*   50:     */     
/*   51:     */     private void addTest(Test newTest)
/*   52:     */     {
/*   53: 206 */       if (this.m_RuleList == null) {
/*   54: 207 */         this.m_RuleList = new ArrayList();
/*   55:     */       }
/*   56: 210 */       this.m_RuleList.add(newTest);
/*   57:     */     }
/*   58:     */     
/*   59:     */     private double classifyInstance(Instance example)
/*   60:     */       throws Exception
/*   61:     */     {
/*   62: 221 */       boolean passedAllTests = true;
/*   63: 222 */       Enumeration<Test> e = new WekaEnumeration(this.m_RuleList);
/*   64: 223 */       while ((passedAllTests) && (e.hasMoreElements()))
/*   65:     */       {
/*   66: 224 */         Test test = (Test)e.nextElement();
/*   67: 225 */         passedAllTests = test.passesTest(example);
/*   68:     */       }
/*   69: 227 */       if (passedAllTests) {
/*   70: 228 */         return this.m_ClassValue;
/*   71:     */       }
/*   72: 230 */       return -1.0D;
/*   73:     */     }
/*   74:     */     
/*   75:     */     public String toString()
/*   76:     */     {
/*   77: 241 */       StringBuffer str = new StringBuffer();
/*   78: 242 */       str = str.append("  c" + (int)this.m_ClassValue + " := ");
/*   79: 243 */       Enumeration<Test> e = new WekaEnumeration(this.m_RuleList);
/*   80: 244 */       if (e.hasMoreElements())
/*   81:     */       {
/*   82: 245 */         Test test = (Test)e.nextElement();
/*   83: 246 */         str = str.append(test.toPrologString());
/*   84:     */       }
/*   85: 248 */       while (e.hasMoreElements())
/*   86:     */       {
/*   87: 249 */         Test test = (Test)e.nextElement();
/*   88: 250 */         str = str.append(", " + test.toPrologString());
/*   89:     */       }
/*   90: 252 */       return str.toString();
/*   91:     */     }
/*   92:     */     
/*   93:     */     public String getRevision()
/*   94:     */     {
/*   95: 262 */       return RevisionUtils.extract("$Revision: 10203 $");
/*   96:     */     }
/*   97:     */   }
/*   98:     */   
/*   99: 285 */   private boolean m_VoteFlag = false;
/*  100: 288 */   private ArrayList<RuleList> m_DecisionList = null;
/*  101:     */   boolean[] m_AttList_Irr;
/*  102:     */   
/*  103:     */   public RDG1()
/*  104:     */   {
/*  105: 302 */     setNumAttributes(defaultNumAttributes());
/*  106: 303 */     setNumClasses(defaultNumClasses());
/*  107: 304 */     setMaxRuleSize(defaultMaxRuleSize());
/*  108: 305 */     setMinRuleSize(defaultMinRuleSize());
/*  109: 306 */     setNumIrrelevant(defaultNumIrrelevant());
/*  110: 307 */     setNumNumeric(defaultNumNumeric());
/*  111:     */   }
/*  112:     */   
/*  113:     */   public String globalInfo()
/*  114:     */   {
/*  115: 317 */     return "A data generator that produces data randomly by producing a decision list.\nThe decision list consists of rules.\nInstances are generated randomly one by one. If decision list fails to classify the current instance, a new rule according to this current instance is generated and added to the decision list.\n\nThe option -V switches on voting, which means that at the end of the generation all instances are reclassified to the class value that is supported by the most rules.\n\nThis data generator can generate 'boolean' attributes (= nominal with the values {true, false}) and numeric attributes. The rules can be 'A' or 'NOT A' for boolean values and 'B < random_value' or 'B >= random_value' for numeric values.";
/*  116:     */   }
/*  117:     */   
/*  118:     */   public Enumeration<Option> listOptions()
/*  119:     */   {
/*  120: 338 */     Vector<Option> result = enumToVector(super.listOptions());
/*  121:     */     
/*  122: 340 */     result.addElement(new Option("\tThe number of attributes (default " + defaultNumAttributes() + ").", "a", 1, "-a <num>"));
/*  123:     */     
/*  124:     */ 
/*  125: 343 */     result.addElement(new Option("\tThe number of classes (default " + defaultNumClasses() + ")", "c", 1, "-c <num>"));
/*  126:     */     
/*  127:     */ 
/*  128: 346 */     result.addElement(new Option("\tmaximum size for rules (default " + defaultMaxRuleSize() + ") ", "R", 1, "-R <num>"));
/*  129:     */     
/*  130:     */ 
/*  131: 349 */     result.addElement(new Option("\tminimum size for rules (default " + defaultMinRuleSize() + ") ", "M", 1, "-M <num>"));
/*  132:     */     
/*  133:     */ 
/*  134: 352 */     result.addElement(new Option("\tnumber of irrelevant attributes (default " + defaultNumIrrelevant() + ")", "I", 1, "-I <num>"));
/*  135:     */     
/*  136:     */ 
/*  137: 355 */     result.addElement(new Option("\tnumber of numeric attributes (default " + defaultNumNumeric() + ")", "N", 1, "-N"));
/*  138:     */     
/*  139:     */ 
/*  140: 358 */     result.addElement(new Option("\tswitch on voting (default is no voting)", "V", 1, "-V"));
/*  141:     */     
/*  142:     */ 
/*  143: 361 */     return result.elements();
/*  144:     */   }
/*  145:     */   
/*  146:     */   public void setOptions(String[] options)
/*  147:     */     throws Exception
/*  148:     */   {
/*  149: 446 */     super.setOptions(options);
/*  150:     */     
/*  151: 448 */     String tmpStr = Utils.getOption('a', options);
/*  152: 449 */     if (tmpStr.length() != 0) {
/*  153: 450 */       setNumAttributes(Integer.parseInt(tmpStr));
/*  154:     */     } else {
/*  155: 452 */       setNumAttributes(defaultNumAttributes());
/*  156:     */     }
/*  157: 455 */     tmpStr = Utils.getOption('c', options);
/*  158: 456 */     if (tmpStr.length() != 0) {
/*  159: 457 */       setNumClasses(Integer.parseInt(tmpStr));
/*  160:     */     } else {
/*  161: 459 */       setNumClasses(defaultNumClasses());
/*  162:     */     }
/*  163: 462 */     tmpStr = Utils.getOption('R', options);
/*  164: 463 */     if (tmpStr.length() != 0) {
/*  165: 464 */       setMaxRuleSize(Integer.parseInt(tmpStr));
/*  166:     */     } else {
/*  167: 466 */       setMaxRuleSize(defaultMaxRuleSize());
/*  168:     */     }
/*  169: 469 */     tmpStr = Utils.getOption('M', options);
/*  170: 470 */     if (tmpStr.length() != 0) {
/*  171: 471 */       setMinRuleSize(Integer.parseInt(tmpStr));
/*  172:     */     } else {
/*  173: 473 */       setMinRuleSize(defaultMinRuleSize());
/*  174:     */     }
/*  175: 476 */     tmpStr = Utils.getOption('I', options);
/*  176: 477 */     if (tmpStr.length() != 0) {
/*  177: 478 */       setNumIrrelevant(Integer.parseInt(tmpStr));
/*  178:     */     } else {
/*  179: 480 */       setNumIrrelevant(defaultNumIrrelevant());
/*  180:     */     }
/*  181: 483 */     if (getNumAttributes() - getNumIrrelevant() < getMinRuleSize()) {
/*  182: 484 */       throw new Exception("Possible rule size is below minimal rule size.");
/*  183:     */     }
/*  184: 487 */     tmpStr = Utils.getOption('N', options);
/*  185: 488 */     if (tmpStr.length() != 0) {
/*  186: 489 */       setNumNumeric(Integer.parseInt(tmpStr));
/*  187:     */     } else {
/*  188: 491 */       setNumNumeric(defaultNumNumeric());
/*  189:     */     }
/*  190: 494 */     setVoteFlag(Utils.getFlag('V', options));
/*  191:     */   }
/*  192:     */   
/*  193:     */   public String[] getOptions()
/*  194:     */   {
/*  195: 504 */     Vector<String> result = new Vector();
/*  196:     */     
/*  197: 506 */     Collections.addAll(result, super.getOptions());
/*  198:     */     
/*  199: 508 */     result.add("-a");
/*  200: 509 */     result.add("" + getNumAttributes());
/*  201:     */     
/*  202: 511 */     result.add("-c");
/*  203: 512 */     result.add("" + getNumClasses());
/*  204:     */     
/*  205: 514 */     result.add("-N");
/*  206: 515 */     result.add("" + getNumNumeric());
/*  207:     */     
/*  208: 517 */     result.add("-I");
/*  209: 518 */     result.add("" + getNumIrrelevant());
/*  210:     */     
/*  211: 520 */     result.add("-M");
/*  212: 521 */     result.add("" + getMinRuleSize());
/*  213:     */     
/*  214: 523 */     result.add("-R");
/*  215: 524 */     result.add("" + getMaxRuleSize());
/*  216: 526 */     if (getVoteFlag()) {
/*  217: 527 */       result.add("-V");
/*  218:     */     }
/*  219: 530 */     return (String[])result.toArray(new String[result.size()]);
/*  220:     */   }
/*  221:     */   
/*  222:     */   protected int defaultNumAttributes()
/*  223:     */   {
/*  224: 539 */     return 10;
/*  225:     */   }
/*  226:     */   
/*  227:     */   public void setNumAttributes(int numAttributes)
/*  228:     */   {
/*  229: 548 */     this.m_NumAttributes = numAttributes;
/*  230:     */   }
/*  231:     */   
/*  232:     */   public int getNumAttributes()
/*  233:     */   {
/*  234: 557 */     return this.m_NumAttributes;
/*  235:     */   }
/*  236:     */   
/*  237:     */   public String numAttributesTipText()
/*  238:     */   {
/*  239: 567 */     return "The number of attributes the generated data will contain.";
/*  240:     */   }
/*  241:     */   
/*  242:     */   protected int defaultNumClasses()
/*  243:     */   {
/*  244: 576 */     return 2;
/*  245:     */   }
/*  246:     */   
/*  247:     */   public void setNumClasses(int numClasses)
/*  248:     */   {
/*  249: 585 */     this.m_NumClasses = numClasses;
/*  250:     */   }
/*  251:     */   
/*  252:     */   public int getNumClasses()
/*  253:     */   {
/*  254: 594 */     return this.m_NumClasses;
/*  255:     */   }
/*  256:     */   
/*  257:     */   public String numClassesTipText()
/*  258:     */   {
/*  259: 604 */     return "The number of classes to generate.";
/*  260:     */   }
/*  261:     */   
/*  262:     */   protected int defaultMaxRuleSize()
/*  263:     */   {
/*  264: 613 */     return 10;
/*  265:     */   }
/*  266:     */   
/*  267:     */   public int getMaxRuleSize()
/*  268:     */   {
/*  269: 622 */     return this.m_MaxRuleSize;
/*  270:     */   }
/*  271:     */   
/*  272:     */   public void setMaxRuleSize(int newMaxRuleSize)
/*  273:     */   {
/*  274: 631 */     this.m_MaxRuleSize = newMaxRuleSize;
/*  275:     */   }
/*  276:     */   
/*  277:     */   public String maxRuleSizeTipText()
/*  278:     */   {
/*  279: 641 */     return "The maximum number of tests in rules.";
/*  280:     */   }
/*  281:     */   
/*  282:     */   protected int defaultMinRuleSize()
/*  283:     */   {
/*  284: 650 */     return 1;
/*  285:     */   }
/*  286:     */   
/*  287:     */   public int getMinRuleSize()
/*  288:     */   {
/*  289: 659 */     return this.m_MinRuleSize;
/*  290:     */   }
/*  291:     */   
/*  292:     */   public void setMinRuleSize(int newMinRuleSize)
/*  293:     */   {
/*  294: 668 */     this.m_MinRuleSize = newMinRuleSize;
/*  295:     */   }
/*  296:     */   
/*  297:     */   public String minRuleSizeTipText()
/*  298:     */   {
/*  299: 678 */     return "The minimum number of tests in rules.";
/*  300:     */   }
/*  301:     */   
/*  302:     */   protected int defaultNumIrrelevant()
/*  303:     */   {
/*  304: 687 */     return 0;
/*  305:     */   }
/*  306:     */   
/*  307:     */   public int getNumIrrelevant()
/*  308:     */   {
/*  309: 696 */     return this.m_NumIrrelevant;
/*  310:     */   }
/*  311:     */   
/*  312:     */   public void setNumIrrelevant(int newNumIrrelevant)
/*  313:     */   {
/*  314: 705 */     this.m_NumIrrelevant = newNumIrrelevant;
/*  315:     */   }
/*  316:     */   
/*  317:     */   public String numIrrelevantTipText()
/*  318:     */   {
/*  319: 715 */     return "The number of irrelevant attributes.";
/*  320:     */   }
/*  321:     */   
/*  322:     */   protected int defaultNumNumeric()
/*  323:     */   {
/*  324: 724 */     return 0;
/*  325:     */   }
/*  326:     */   
/*  327:     */   public int getNumNumeric()
/*  328:     */   {
/*  329: 733 */     return this.m_NumNumeric;
/*  330:     */   }
/*  331:     */   
/*  332:     */   public void setNumNumeric(int newNumNumeric)
/*  333:     */   {
/*  334: 742 */     this.m_NumNumeric = newNumNumeric;
/*  335:     */   }
/*  336:     */   
/*  337:     */   public String numNumericTipText()
/*  338:     */   {
/*  339: 752 */     return "The number of numerical attributes.";
/*  340:     */   }
/*  341:     */   
/*  342:     */   public boolean getVoteFlag()
/*  343:     */   {
/*  344: 761 */     return this.m_VoteFlag;
/*  345:     */   }
/*  346:     */   
/*  347:     */   public void setVoteFlag(boolean newVoteFlag)
/*  348:     */   {
/*  349: 770 */     this.m_VoteFlag = newVoteFlag;
/*  350:     */   }
/*  351:     */   
/*  352:     */   public String voteFlagTipText()
/*  353:     */   {
/*  354: 780 */     return "Whether to use voting or not.";
/*  355:     */   }
/*  356:     */   
/*  357:     */   public boolean getSingleModeFlag()
/*  358:     */   {
/*  359: 790 */     return !getVoteFlag();
/*  360:     */   }
/*  361:     */   
/*  362:     */   public boolean[] getAttList_Irr()
/*  363:     */   {
/*  364: 800 */     return this.m_AttList_Irr;
/*  365:     */   }
/*  366:     */   
/*  367:     */   public void setAttList_Irr(boolean[] newAttList_Irr)
/*  368:     */   {
/*  369: 810 */     this.m_AttList_Irr = newAttList_Irr;
/*  370:     */   }
/*  371:     */   
/*  372:     */   public String attList_IrrTipText()
/*  373:     */   {
/*  374: 820 */     return "The array with the indices of the irrelevant attributes.";
/*  375:     */   }
/*  376:     */   
/*  377:     */   public Instances defineDataFormat()
/*  378:     */     throws Exception
/*  379:     */   {
/*  380: 832 */     Random random = new Random(getSeed());
/*  381: 833 */     setRandom(random);
/*  382:     */     
/*  383: 835 */     this.m_DecisionList = new ArrayList();
/*  384:     */     
/*  385:     */ 
/*  386: 838 */     setNumExamplesAct(getNumExamples());
/*  387:     */     
/*  388:     */ 
/*  389: 841 */     Instances dataset = defineDataset(random);
/*  390: 842 */     return dataset;
/*  391:     */   }
/*  392:     */   
/*  393:     */   public Instance generateExample()
/*  394:     */     throws Exception
/*  395:     */   {
/*  396: 854 */     Random random = getRandom();
/*  397: 855 */     Instances format = getDatasetFormat();
/*  398: 857 */     if (format == null) {
/*  399: 858 */       throw new Exception("Dataset format not defined.");
/*  400:     */     }
/*  401: 860 */     if (getVoteFlag()) {
/*  402: 861 */       throw new Exception("Examples cannot be generated one by one.");
/*  403:     */     }
/*  404: 865 */     format = generateExamples(1, random, format);
/*  405:     */     
/*  406: 867 */     return format.lastInstance();
/*  407:     */   }
/*  408:     */   
/*  409:     */   public Instances generateExamples()
/*  410:     */     throws Exception
/*  411:     */   {
/*  412: 879 */     Random random = getRandom();
/*  413: 880 */     Instances format = getDatasetFormat();
/*  414: 881 */     if (format == null) {
/*  415: 882 */       throw new Exception("Dataset format not defined.");
/*  416:     */     }
/*  417: 886 */     format = generateExamples(getNumExamplesAct(), random, format);
/*  418: 889 */     if (getVoteFlag()) {
/*  419: 890 */       format = voteDataset(format);
/*  420:     */     }
/*  421: 893 */     return format;
/*  422:     */   }
/*  423:     */   
/*  424:     */   public Instances generateExamples(int num, Random random, Instances format)
/*  425:     */     throws Exception
/*  426:     */   {
/*  427: 909 */     if (format == null) {
/*  428: 910 */       throw new Exception("Dataset format not defined.");
/*  429:     */     }
/*  430: 914 */     for (int i = 0; i < num; i++)
/*  431:     */     {
/*  432: 916 */       Instance example = generateExample(random, format);
/*  433:     */       
/*  434:     */ 
/*  435: 919 */       boolean classDefined = classifyExample(example);
/*  436: 920 */       if (!classDefined) {
/*  437: 922 */         example = updateDecisionList(random, example);
/*  438:     */       }
/*  439: 924 */       example.setDataset(format);
/*  440: 925 */       format.add(example);
/*  441:     */     }
/*  442: 928 */     return format;
/*  443:     */   }
/*  444:     */   
/*  445:     */   private Instance updateDecisionList(Random random, Instance example)
/*  446:     */     throws Exception
/*  447:     */   {
/*  448: 943 */     Instances format = getDatasetFormat();
/*  449: 944 */     if (format == null) {
/*  450: 945 */       throw new Exception("Dataset format not defined.");
/*  451:     */     }
/*  452: 948 */     ArrayList<Test> TestList = generateTestList(random, example);
/*  453:     */     
/*  454: 950 */     int maxSize = getMaxRuleSize() < TestList.size() ? getMaxRuleSize() : TestList.size();
/*  455:     */     
/*  456: 952 */     int ruleSize = (int)(random.nextDouble() * (maxSize - getMinRuleSize())) + getMinRuleSize();
/*  457:     */     
/*  458:     */ 
/*  459: 955 */     RuleList newRule = new RuleList(null);
/*  460: 956 */     for (int i = 0; i < ruleSize; i++)
/*  461:     */     {
/*  462: 957 */       int testIndex = (int)(random.nextDouble() * TestList.size());
/*  463: 958 */       Test test = (Test)TestList.get(testIndex);
/*  464:     */       
/*  465: 960 */       newRule.addTest(test);
/*  466: 961 */       TestList.remove(testIndex);
/*  467:     */     }
/*  468: 963 */     double newClassValue = 0.0D;
/*  469: 964 */     if (this.m_DecisionList.size() > 0)
/*  470:     */     {
/*  471: 965 */       RuleList r = (RuleList)this.m_DecisionList.get(this.m_DecisionList.size() - 1);
/*  472: 966 */       double oldClassValue = r.getClassValue();
/*  473: 967 */       newClassValue = ((int)oldClassValue + 1) % getNumClasses();
/*  474:     */     }
/*  475: 969 */     newRule.setClassValue(newClassValue);
/*  476: 970 */     this.m_DecisionList.add(newRule);
/*  477: 971 */     example = (Instance)example.copy();
/*  478: 972 */     example.setDataset(format);
/*  479: 973 */     example.setClassValue(newClassValue);
/*  480: 974 */     return example;
/*  481:     */   }
/*  482:     */   
/*  483:     */   private ArrayList<Test> generateTestList(Random random, Instance example)
/*  484:     */     throws Exception
/*  485:     */   {
/*  486: 988 */     Instances format = getDatasetFormat();
/*  487: 989 */     if (format == null) {
/*  488: 990 */       throw new Exception("Dataset format not defined.");
/*  489:     */     }
/*  490: 993 */     int numTests = getNumAttributes() - getNumIrrelevant();
/*  491: 994 */     ArrayList<Test> TestList = new ArrayList(numTests);
/*  492: 995 */     boolean[] irrelevant = getAttList_Irr();
/*  493: 997 */     for (int i = 0; i < getNumAttributes(); i++) {
/*  494: 998 */       if (irrelevant[i] == 0)
/*  495:     */       {
/*  496: 999 */         Test newTest = null;
/*  497:1000 */         Attribute att = example.attribute(i);
/*  498:1001 */         if (att.isNumeric())
/*  499:     */         {
/*  500:1002 */           double newSplit = random.nextDouble();
/*  501:1003 */           boolean newNot = newSplit < example.value(i);
/*  502:1004 */           newTest = new Test(i, newSplit, format, newNot);
/*  503:     */         }
/*  504:     */         else
/*  505:     */         {
/*  506:1006 */           newTest = new Test(i, example.value(i), format, false);
/*  507:     */         }
/*  508:1008 */         TestList.add(newTest);
/*  509:     */       }
/*  510:     */     }
/*  511:1012 */     return TestList;
/*  512:     */   }
/*  513:     */   
/*  514:     */   private Instance generateExample(Random random, Instances format)
/*  515:     */     throws Exception
/*  516:     */   {
/*  517:1029 */     double[] attributes = new double[getNumAttributes() + 1];
/*  518:1030 */     for (int i = 0; i < getNumAttributes(); i++)
/*  519:     */     {
/*  520:1031 */       double value = random.nextDouble();
/*  521:1032 */       if (format.attribute(i).isNumeric()) {
/*  522:1033 */         attributes[i] = value;
/*  523:1035 */       } else if (format.attribute(i).isNominal()) {
/*  524:1036 */         attributes[i] = (value > 0.5D ? 1.0D : 0.0D);
/*  525:     */       } else {
/*  526:1038 */         throw new Exception("Attribute type is not supported.");
/*  527:     */       }
/*  528:     */     }
/*  529:1042 */     Instance example = new DenseInstance(1.0D, attributes);
/*  530:1043 */     example.setDataset(format);
/*  531:1044 */     example.setClassMissing();
/*  532:     */     
/*  533:1046 */     return example;
/*  534:     */   }
/*  535:     */   
/*  536:     */   private boolean classifyExample(Instance example)
/*  537:     */     throws Exception
/*  538:     */   {
/*  539:1057 */     double classValue = -1.0D;
/*  540:     */     
/*  541:1059 */     Enumeration<RuleList> e = new WekaEnumeration(this.m_DecisionList);
/*  542:1060 */     while ((e.hasMoreElements()) && (classValue < 0.0D))
/*  543:     */     {
/*  544:1061 */       RuleList rl = (RuleList)e.nextElement();
/*  545:1062 */       classValue = rl.classifyInstance(example);
/*  546:     */     }
/*  547:1064 */     if (classValue >= 0.0D)
/*  548:     */     {
/*  549:1065 */       example.setClassValue(classValue);
/*  550:1066 */       return true;
/*  551:     */     }
/*  552:1068 */     return false;
/*  553:     */   }
/*  554:     */   
/*  555:     */   private Instance votedReclassifyExample(Instance example)
/*  556:     */     throws Exception
/*  557:     */   {
/*  558:1083 */     int[] classVotes = new int[getNumClasses()];
/*  559:1084 */     for (int i = 0; i < classVotes.length; i++) {
/*  560:1085 */       classVotes[i] = 0;
/*  561:     */     }
/*  562:1088 */     Enumeration<RuleList> e = new WekaEnumeration(this.m_DecisionList);
/*  563:1089 */     while (e.hasMoreElements())
/*  564:     */     {
/*  565:1090 */       RuleList rl = (RuleList)e.nextElement();
/*  566:1091 */       int classValue = (int)rl.classifyInstance(example);
/*  567:1092 */       if (classValue >= 0) {
/*  568:1093 */         classVotes[classValue] += 1;
/*  569:     */       }
/*  570:     */     }
/*  571:1096 */     int maxVote = 0;
/*  572:1097 */     int vote = -1;
/*  573:1098 */     for (int i = 0; i < classVotes.length; i++) {
/*  574:1099 */       if (classVotes[i] > maxVote)
/*  575:     */       {
/*  576:1100 */         maxVote = classVotes[i];
/*  577:1101 */         vote = i;
/*  578:     */       }
/*  579:     */     }
/*  580:1104 */     if (vote >= 0) {
/*  581:1105 */       example.setClassValue(vote);
/*  582:     */     } else {
/*  583:1107 */       throw new Exception("Error in instance classification.");
/*  584:     */     }
/*  585:1110 */     return example;
/*  586:     */   }
/*  587:     */   
/*  588:     */   private Instances defineDataset(Random random)
/*  589:     */     throws Exception
/*  590:     */   {
/*  591:1124 */     ArrayList<Attribute> attributes = new ArrayList();
/*  592:     */     
/*  593:1126 */     ArrayList<String> nominalValues = new ArrayList(2);
/*  594:1127 */     nominalValues.add("false");
/*  595:1128 */     nominalValues.add("true");
/*  596:1129 */     ArrayList<String> classValues = new ArrayList(getNumClasses());
/*  597:     */     
/*  598:     */ 
/*  599:     */ 
/*  600:1133 */     boolean[] attList_Irr = defineIrrelevant(random);
/*  601:1134 */     setAttList_Irr(attList_Irr);
/*  602:     */     
/*  603:     */ 
/*  604:1137 */     int[] attList_Num = defineNumeric(random);
/*  605:1140 */     for (int i = 0; i < getNumAttributes(); i++)
/*  606:     */     {
/*  607:     */       Attribute attribute;
/*  608:     */       Attribute attribute;
/*  609:1141 */       if (attList_Num[i] == 0) {
/*  610:1142 */         attribute = new Attribute("a" + i);
/*  611:     */       } else {
/*  612:1144 */         attribute = new Attribute("a" + i, nominalValues);
/*  613:     */       }
/*  614:1146 */       attributes.add(attribute);
/*  615:     */     }
/*  616:1148 */     for (int i = 0; i < getNumClasses(); i++) {
/*  617:1149 */       classValues.add("c" + i);
/*  618:     */     }
/*  619:1151 */     Attribute attribute = new Attribute("class", classValues);
/*  620:1152 */     attributes.add(attribute);
/*  621:     */     
/*  622:1154 */     Instances dataset = new Instances(getRelationNameToUse(), attributes, getNumExamplesAct());
/*  623:     */     
/*  624:1156 */     dataset.setClassIndex(getNumAttributes());
/*  625:     */     
/*  626:     */ 
/*  627:1159 */     Instances format = new Instances(dataset, 0);
/*  628:1160 */     setDatasetFormat(format);
/*  629:     */     
/*  630:1162 */     return dataset;
/*  631:     */   }
/*  632:     */   
/*  633:     */   private boolean[] defineIrrelevant(Random random)
/*  634:     */   {
/*  635:1177 */     boolean[] irr = new boolean[getNumAttributes()];
/*  636:1180 */     for (int i = 0; i < irr.length; i++) {
/*  637:1181 */       irr[i] = false;
/*  638:     */     }
/*  639:1185 */     int numIrr = 0;
/*  640:1186 */     for (int i = 0; (numIrr < getNumIrrelevant()) && (i < getNumAttributes() * 5); i++)
/*  641:     */     {
/*  642:1188 */       int maybeNext = (int)(random.nextDouble() * irr.length);
/*  643:1189 */       if (irr[maybeNext] == 0)
/*  644:     */       {
/*  645:1190 */         irr[maybeNext] = true;
/*  646:1191 */         numIrr++;
/*  647:     */       }
/*  648:     */     }
/*  649:1195 */     return irr;
/*  650:     */   }
/*  651:     */   
/*  652:     */   private int[] defineNumeric(Random random)
/*  653:     */   {
/*  654:1207 */     int[] num = new int[getNumAttributes()];
/*  655:1210 */     for (int i = 0; i < num.length; i++) {
/*  656:1211 */       num[i] = 1;
/*  657:     */     }
/*  658:1214 */     int numNum = 0;
/*  659:1215 */     for (int i = 0; (numNum < getNumNumeric()) && (i < getNumAttributes() * 5); i++)
/*  660:     */     {
/*  661:1216 */       int maybeNext = (int)(random.nextDouble() * num.length);
/*  662:1217 */       if (num[maybeNext] != 0)
/*  663:     */       {
/*  664:1218 */         num[maybeNext] = 0;
/*  665:1219 */         numNum++;
/*  666:     */       }
/*  667:     */     }
/*  668:1223 */     return num;
/*  669:     */   }
/*  670:     */   
/*  671:     */   public String generateStart()
/*  672:     */   {
/*  673:1235 */     return "";
/*  674:     */   }
/*  675:     */   
/*  676:     */   public String generateFinished()
/*  677:     */     throws Exception
/*  678:     */   {
/*  679:1250 */     StringBuffer dLString = new StringBuffer();
/*  680:     */     
/*  681:     */ 
/*  682:1253 */     boolean[] attList_Irr = getAttList_Irr();
/*  683:1254 */     Instances format = getDatasetFormat();
/*  684:1255 */     dLString.append("%\n% Number of attributes chosen as irrelevant = " + getNumIrrelevant() + "\n");
/*  685:1257 */     for (int i = 0; i < attList_Irr.length; i++) {
/*  686:1258 */       if (attList_Irr[i] != 0) {
/*  687:1259 */         dLString.append("% " + format.attribute(i).name() + "\n");
/*  688:     */       }
/*  689:     */     }
/*  690:1263 */     dLString.append("%\n% DECISIONLIST (number of rules = " + this.m_DecisionList.size() + "):\n");
/*  691:1266 */     for (int i = 0; i < this.m_DecisionList.size(); i++)
/*  692:     */     {
/*  693:1267 */       RuleList rl = (RuleList)this.m_DecisionList.get(i);
/*  694:1268 */       dLString.append("% RULE " + i + ": " + rl.toString() + "\n");
/*  695:     */     }
/*  696:1271 */     return dLString.toString();
/*  697:     */   }
/*  698:     */   
/*  699:     */   private Instances voteDataset(Instances dataset)
/*  700:     */     throws Exception
/*  701:     */   {
/*  702:1284 */     for (int i = 0; i < dataset.numInstances(); i++)
/*  703:     */     {
/*  704:1285 */       Instance inst = dataset.firstInstance();
/*  705:1286 */       inst = votedReclassifyExample(inst);
/*  706:1287 */       dataset.add(inst);
/*  707:1288 */       dataset.delete(0);
/*  708:     */     }
/*  709:1291 */     return dataset;
/*  710:     */   }
/*  711:     */   
/*  712:     */   public String getRevision()
/*  713:     */   {
/*  714:1301 */     return RevisionUtils.extract("$Revision: 10203 $");
/*  715:     */   }
/*  716:     */   
/*  717:     */   public static void main(String[] args)
/*  718:     */   {
/*  719:1310 */     runDataGenerator(new RDG1(), args);
/*  720:     */   }
/*  721:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.datagenerators.classifiers.classification.RDG1
 * JD-Core Version:    0.7.0.1
 */