/*    1:     */ package weka.classifiers.functions.supportVector;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.util.Collections;
/*    5:     */ import java.util.Enumeration;
/*    6:     */ import java.util.Vector;
/*    7:     */ import weka.core.Attribute;
/*    8:     */ import weka.core.Capabilities;
/*    9:     */ import weka.core.Capabilities.Capability;
/*   10:     */ import weka.core.Instance;
/*   11:     */ import weka.core.Instances;
/*   12:     */ import weka.core.Option;
/*   13:     */ import weka.core.RevisionUtils;
/*   14:     */ import weka.core.SelectedTag;
/*   15:     */ import weka.core.Tag;
/*   16:     */ import weka.core.TechnicalInformation;
/*   17:     */ import weka.core.TechnicalInformation.Field;
/*   18:     */ import weka.core.TechnicalInformation.Type;
/*   19:     */ import weka.core.TechnicalInformationHandler;
/*   20:     */ import weka.core.Utils;
/*   21:     */ 
/*   22:     */ public class StringKernel
/*   23:     */   extends Kernel
/*   24:     */   implements TechnicalInformationHandler
/*   25:     */ {
/*   26:     */   private static final long serialVersionUID = -4902954211202690123L;
/*   27: 328 */   private int m_cacheSize = 250007;
/*   28: 331 */   private int m_internalCacheSize = 200003;
/*   29:     */   private int m_strAttr;
/*   30:     */   private double[] m_storage;
/*   31:     */   private long[] m_keys;
/*   32:     */   private int m_kernelEvals;
/*   33:     */   private int m_numInsts;
/*   34:     */   public static final int PRUNING_NONE = 0;
/*   35:     */   public static final int PRUNING_LAMBDA = 1;
/*   36: 351 */   public static final Tag[] TAGS_PRUNING = { new Tag(0, "No pruning"), new Tag(1, "Lambda pruning") };
/*   37: 356 */   protected int m_PruningMethod = 0;
/*   38: 362 */   protected double m_lambda = 0.5D;
/*   39: 365 */   private int m_subsequenceLength = 3;
/*   40: 368 */   private int m_maxSubsequenceLength = 9;
/*   41:     */   protected static final int MAX_POWER_OF_LAMBDA = 10000;
/*   42: 377 */   protected double[] m_powersOflambda = null;
/*   43: 383 */   private boolean m_normalize = false;
/*   44:     */   private int maxCache;
/*   45:     */   private double[] cachekh;
/*   46:     */   private int[] cachekhK;
/*   47:     */   private double[] cachekh2;
/*   48:     */   private int[] cachekh2K;
/*   49:     */   private int m_multX;
/*   50:     */   private int m_multY;
/*   51:     */   private int m_multZ;
/*   52:     */   private int m_multZZ;
/*   53: 397 */   private boolean m_useRecursionCache = true;
/*   54:     */   
/*   55:     */   public StringKernel() {}
/*   56:     */   
/*   57:     */   public StringKernel(Instances data, int cacheSize, int subsequenceLength, double lambda, boolean debug)
/*   58:     */     throws Exception
/*   59:     */   {
/*   60: 421 */     setDebug(debug);
/*   61: 422 */     setCacheSize(cacheSize);
/*   62: 423 */     setInternalCacheSize(200003);
/*   63: 424 */     setSubsequenceLength(subsequenceLength);
/*   64: 425 */     setMaxSubsequenceLength(-1);
/*   65: 426 */     setLambda(lambda);
/*   66:     */     
/*   67: 428 */     buildKernel(data);
/*   68:     */   }
/*   69:     */   
/*   70:     */   public String globalInfo()
/*   71:     */   {
/*   72: 439 */     return "Implementation of the subsequence kernel (SSK) as described in [1] and of the subsequence kernel with lambda pruning (SSK-LP) as described in [2].\n\nFor more information, see\n\n" + getTechnicalInformation().toString();
/*   73:     */   }
/*   74:     */   
/*   75:     */   public TechnicalInformation getTechnicalInformation()
/*   76:     */   {
/*   77: 458 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*   78: 459 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Huma Lodhi and Craig Saunders and John Shawe-Taylor and Nello Cristianini and Christopher J. C. H. Watkins");
/*   79:     */     
/*   80:     */ 
/*   81:     */ 
/*   82: 463 */     result.setValue(TechnicalInformation.Field.YEAR, "2002");
/*   83: 464 */     result.setValue(TechnicalInformation.Field.TITLE, "Text Classification using String Kernels");
/*   84: 465 */     result.setValue(TechnicalInformation.Field.JOURNAL, "Journal of Machine Learning Research");
/*   85: 466 */     result.setValue(TechnicalInformation.Field.VOLUME, "2");
/*   86: 467 */     result.setValue(TechnicalInformation.Field.PAGES, "419-444");
/*   87: 468 */     result.setValue(TechnicalInformation.Field.HTTP, "http://www.jmlr.org/papers/v2/lodhi02a.html");
/*   88:     */     
/*   89: 470 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.TECHREPORT);
/*   90: 471 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "F. Kleedorfer and A. Seewald");
/*   91: 472 */     additional.setValue(TechnicalInformation.Field.YEAR, "2005");
/*   92: 473 */     additional.setValue(TechnicalInformation.Field.TITLE, "Implementation of a String Kernel for WEKA");
/*   93:     */     
/*   94: 475 */     additional.setValue(TechnicalInformation.Field.INSTITUTION, "Oesterreichisches Forschungsinstitut fuer Artificial Intelligence");
/*   95:     */     
/*   96: 477 */     additional.setValue(TechnicalInformation.Field.ADDRESS, "Wien, Austria");
/*   97: 478 */     additional.setValue(TechnicalInformation.Field.NUMBER, "TR-2005-13");
/*   98:     */     
/*   99: 480 */     return result;
/*  100:     */   }
/*  101:     */   
/*  102:     */   public Enumeration<Option> listOptions()
/*  103:     */   {
/*  104: 490 */     Vector<Option> result = new Vector();
/*  105:     */     
/*  106:     */ 
/*  107:     */ 
/*  108:     */ 
/*  109:     */ 
/*  110:     */ 
/*  111: 497 */     result.addAll(Collections.list(super.listOptions()));
/*  112:     */     
/*  113: 499 */     String desc = "";
/*  114: 500 */     String param = "";
/*  115: 501 */     for (int i = 0; i < TAGS_PRUNING.length; i++)
/*  116:     */     {
/*  117: 502 */       if (i > 0) {
/*  118: 503 */         param = param + "|";
/*  119:     */       }
/*  120: 505 */       SelectedTag tag = new SelectedTag(TAGS_PRUNING[i].getID(), TAGS_PRUNING);
/*  121: 506 */       param = param + "" + tag.getSelectedTag().getID();
/*  122: 507 */       desc = desc + "\t" + tag.getSelectedTag().getID() + " = " + tag.getSelectedTag().getReadable() + "\n";
/*  123:     */     }
/*  124: 511 */     result.addElement(new Option("\tThe pruning method to use:\n" + desc + "\t(default: " + 0 + ")", "P", 1, "-P <" + param + ">"));
/*  125:     */     
/*  126:     */ 
/*  127: 514 */     result.addElement(new Option("\tThe size of the cache (a prime number).\n\t(default: 250007)", "C", 1, "-C <num>"));
/*  128:     */     
/*  129:     */ 
/*  130: 517 */     result.addElement(new Option("\tThe size of the internal cache (a prime number).\n\t(default: 200003)", "IC", 1, "-IC <num>"));
/*  131:     */     
/*  132:     */ 
/*  133:     */ 
/*  134: 521 */     result.addElement(new Option("\tThe lambda constant. Penalizes non-continuous subsequence\n\tmatches. Must be in (0,1).\n\t(default: 0.5)", "L", 1, "-L <num>"));
/*  135:     */     
/*  136:     */ 
/*  137:     */ 
/*  138:     */ 
/*  139: 526 */     result.addElement(new Option("\tThe length of the subsequence.\n\t(default: 3)", "ssl", 1, "-ssl <num>"));
/*  140:     */     
/*  141:     */ 
/*  142: 529 */     result.addElement(new Option("\tThe maximum length of the subsequence.\n\t(default: 9)", "ssl-max", 1, "-ssl-max <num>"));
/*  143:     */     
/*  144:     */ 
/*  145: 532 */     result.addElement(new Option("\tUse normalization.\n\t(default: no)", "N", 0, "-N"));
/*  146:     */     
/*  147:     */ 
/*  148: 535 */     return result.elements();
/*  149:     */   }
/*  150:     */   
/*  151:     */   public void setOptions(String[] options)
/*  152:     */     throws Exception
/*  153:     */   {
/*  154: 611 */     String tmpStr = Utils.getOption('P', options);
/*  155: 612 */     if (tmpStr.length() != 0) {
/*  156: 613 */       setPruningMethod(new SelectedTag(Integer.parseInt(tmpStr), TAGS_PRUNING));
/*  157:     */     } else {
/*  158: 615 */       setPruningMethod(new SelectedTag(0, TAGS_PRUNING));
/*  159:     */     }
/*  160: 618 */     tmpStr = Utils.getOption('C', options);
/*  161: 619 */     if (tmpStr.length() != 0) {
/*  162: 620 */       setCacheSize(Integer.parseInt(tmpStr));
/*  163:     */     } else {
/*  164: 622 */       setCacheSize(250007);
/*  165:     */     }
/*  166: 625 */     tmpStr = Utils.getOption("IC", options);
/*  167: 626 */     if (tmpStr.length() != 0) {
/*  168: 627 */       setInternalCacheSize(Integer.parseInt(tmpStr));
/*  169:     */     } else {
/*  170: 629 */       setInternalCacheSize(200003);
/*  171:     */     }
/*  172: 632 */     tmpStr = Utils.getOption('L', options);
/*  173: 633 */     if (tmpStr.length() != 0) {
/*  174: 634 */       setLambda(Double.parseDouble(tmpStr));
/*  175:     */     } else {
/*  176: 636 */       setLambda(0.5D);
/*  177:     */     }
/*  178: 639 */     tmpStr = Utils.getOption("ssl", options);
/*  179: 640 */     if (tmpStr.length() != 0) {
/*  180: 641 */       setSubsequenceLength(Integer.parseInt(tmpStr));
/*  181:     */     } else {
/*  182: 643 */       setSubsequenceLength(3);
/*  183:     */     }
/*  184: 646 */     tmpStr = Utils.getOption("ssl-max", options);
/*  185: 647 */     if (tmpStr.length() != 0) {
/*  186: 648 */       setMaxSubsequenceLength(Integer.parseInt(tmpStr));
/*  187:     */     } else {
/*  188: 650 */       setMaxSubsequenceLength(9);
/*  189:     */     }
/*  190: 653 */     setUseNormalization(Utils.getFlag('N', options));
/*  191: 655 */     if (getMaxSubsequenceLength() < 2 * getSubsequenceLength()) {
/*  192: 656 */       throw new IllegalArgumentException("Lambda Pruning forbids even contiguous substring matches! Use a bigger value for ssl-max (at least 2*ssl).");
/*  193:     */     }
/*  194: 661 */     super.setOptions(options);
/*  195:     */   }
/*  196:     */   
/*  197:     */   public String[] getOptions()
/*  198:     */   {
/*  199: 672 */     Vector<String> result = new Vector();
/*  200:     */     
/*  201: 674 */     Collections.addAll(result, super.getOptions());
/*  202:     */     
/*  203: 676 */     result.add("-P");
/*  204: 677 */     result.add("" + this.m_PruningMethod);
/*  205:     */     
/*  206: 679 */     result.add("-C");
/*  207: 680 */     result.add("" + getCacheSize());
/*  208:     */     
/*  209: 682 */     result.add("-IC");
/*  210: 683 */     result.add("" + getInternalCacheSize());
/*  211:     */     
/*  212: 685 */     result.add("-L");
/*  213: 686 */     result.add("" + getLambda());
/*  214:     */     
/*  215: 688 */     result.add("-ssl");
/*  216: 689 */     result.add("" + getSubsequenceLength());
/*  217:     */     
/*  218: 691 */     result.add("-ssl-max");
/*  219: 692 */     result.add("" + getMaxSubsequenceLength());
/*  220: 694 */     if (getUseNormalization()) {
/*  221: 695 */       result.add("-L");
/*  222:     */     }
/*  223: 698 */     return (String[])result.toArray(new String[result.size()]);
/*  224:     */   }
/*  225:     */   
/*  226:     */   public String pruningMethodTipText()
/*  227:     */   {
/*  228: 708 */     return "The pruning method.";
/*  229:     */   }
/*  230:     */   
/*  231:     */   public void setPruningMethod(SelectedTag value)
/*  232:     */   {
/*  233: 717 */     if (value.getTags() == TAGS_PRUNING) {
/*  234: 718 */       this.m_PruningMethod = value.getSelectedTag().getID();
/*  235:     */     }
/*  236:     */   }
/*  237:     */   
/*  238:     */   public SelectedTag getPruningMethod()
/*  239:     */   {
/*  240: 728 */     return new SelectedTag(this.m_PruningMethod, TAGS_PRUNING);
/*  241:     */   }
/*  242:     */   
/*  243:     */   public void setCacheSize(int value)
/*  244:     */   {
/*  245: 737 */     if (value >= 0)
/*  246:     */     {
/*  247: 738 */       this.m_cacheSize = value;
/*  248: 739 */       clean();
/*  249:     */     }
/*  250:     */     else
/*  251:     */     {
/*  252: 741 */       System.out.println("Cache size cannot be smaller than 0 (provided: " + value + ")!");
/*  253:     */     }
/*  254:     */   }
/*  255:     */   
/*  256:     */   public int getCacheSize()
/*  257:     */   {
/*  258: 752 */     return this.m_cacheSize;
/*  259:     */   }
/*  260:     */   
/*  261:     */   public String cacheSizeTipText()
/*  262:     */   {
/*  263: 762 */     return "The size of the cache (a prime number).";
/*  264:     */   }
/*  265:     */   
/*  266:     */   public void setInternalCacheSize(int value)
/*  267:     */   {
/*  268: 773 */     if (value >= 0)
/*  269:     */     {
/*  270: 774 */       this.m_internalCacheSize = value;
/*  271: 775 */       clean();
/*  272:     */     }
/*  273:     */     else
/*  274:     */     {
/*  275: 777 */       System.out.println("Cache size cannot be smaller than 0 (provided: " + value + ")!");
/*  276:     */     }
/*  277:     */   }
/*  278:     */   
/*  279:     */   public int getInternalCacheSize()
/*  280:     */   {
/*  281: 788 */     return this.m_internalCacheSize;
/*  282:     */   }
/*  283:     */   
/*  284:     */   public String internalCacheSizeTipText()
/*  285:     */   {
/*  286: 798 */     return "The size of the internal cache (a prime number).";
/*  287:     */   }
/*  288:     */   
/*  289:     */   public void setSubsequenceLength(int value)
/*  290:     */   {
/*  291: 807 */     this.m_subsequenceLength = value;
/*  292:     */   }
/*  293:     */   
/*  294:     */   public int getSubsequenceLength()
/*  295:     */   {
/*  296: 816 */     return this.m_subsequenceLength;
/*  297:     */   }
/*  298:     */   
/*  299:     */   public String subsequenceLengthTipText()
/*  300:     */   {
/*  301: 826 */     return "The subsequence length.";
/*  302:     */   }
/*  303:     */   
/*  304:     */   public void setMaxSubsequenceLength(int value)
/*  305:     */   {
/*  306: 835 */     this.m_maxSubsequenceLength = value;
/*  307:     */   }
/*  308:     */   
/*  309:     */   public int getMaxSubsequenceLength()
/*  310:     */   {
/*  311: 844 */     return this.m_maxSubsequenceLength;
/*  312:     */   }
/*  313:     */   
/*  314:     */   public String maxSubsequenceLengthTipText()
/*  315:     */   {
/*  316: 854 */     return "The maximum subsequence length (theta in the paper)";
/*  317:     */   }
/*  318:     */   
/*  319:     */   public void setLambda(double value)
/*  320:     */   {
/*  321: 863 */     this.m_lambda = value;
/*  322:     */   }
/*  323:     */   
/*  324:     */   public double getLambda()
/*  325:     */   {
/*  326: 872 */     return this.m_lambda;
/*  327:     */   }
/*  328:     */   
/*  329:     */   public String lambdaTipText()
/*  330:     */   {
/*  331: 882 */     return "Penalizes non-continuous subsequence matches, from (0,1)";
/*  332:     */   }
/*  333:     */   
/*  334:     */   public void setUseNormalization(boolean value)
/*  335:     */   {
/*  336: 892 */     if (value != this.m_normalize) {
/*  337: 893 */       clean();
/*  338:     */     }
/*  339: 896 */     this.m_normalize = value;
/*  340:     */   }
/*  341:     */   
/*  342:     */   public boolean getUseNormalization()
/*  343:     */   {
/*  344: 905 */     return this.m_normalize;
/*  345:     */   }
/*  346:     */   
/*  347:     */   public String useNormalizationTipText()
/*  348:     */   {
/*  349: 915 */     return "Whether to use normalization.";
/*  350:     */   }
/*  351:     */   
/*  352:     */   public double eval(int id1, int id2, Instance inst1)
/*  353:     */     throws Exception
/*  354:     */   {
/*  355: 930 */     if ((this.m_Debug) && (id1 > -1) && (id2 > -1))
/*  356:     */     {
/*  357: 931 */       System.err.println("\nEvaluation of string kernel for");
/*  358: 932 */       System.err.println(this.m_data.instance(id1).stringValue(this.m_strAttr));
/*  359: 933 */       System.err.println("and");
/*  360: 934 */       System.err.println(this.m_data.instance(id2).stringValue(this.m_strAttr));
/*  361:     */     }
/*  362: 939 */     if ((id1 == id2) && (this.m_normalize)) {
/*  363: 940 */       return 1.0D;
/*  364:     */     }
/*  365: 943 */     double result = 0.0D;
/*  366: 944 */     long key = -1L;
/*  367: 945 */     int location = -1;
/*  368: 948 */     if ((id1 >= 0) && (this.m_keys != null))
/*  369:     */     {
/*  370: 949 */       if (id1 > id2) {
/*  371: 950 */         key = id1 * this.m_numInsts + id2;
/*  372:     */       } else {
/*  373: 952 */         key = id2 * this.m_numInsts + id1;
/*  374:     */       }
/*  375: 954 */       if (key < 0L) {
/*  376: 955 */         throw new Exception("Cache overflow detected!");
/*  377:     */       }
/*  378: 957 */       location = (int)(key % this.m_keys.length);
/*  379: 958 */       if (this.m_keys[location] == key + 1L)
/*  380:     */       {
/*  381: 959 */         if (this.m_Debug) {
/*  382: 960 */           System.err.println("result (cached): " + this.m_storage[location]);
/*  383:     */         }
/*  384: 962 */         return this.m_storage[location];
/*  385:     */       }
/*  386:     */     }
/*  387: 966 */     this.m_kernelEvals += 1;
/*  388: 967 */     long start = System.currentTimeMillis();
/*  389:     */     
/*  390: 969 */     Instance inst2 = this.m_data.instance(id2);
/*  391: 970 */     char[] s1 = inst1.stringValue(this.m_strAttr).toCharArray();
/*  392: 971 */     char[] s2 = inst2.stringValue(this.m_strAttr).toCharArray();
/*  393: 974 */     if ((s1.length == 0) || (s2.length == 0)) {
/*  394: 975 */       return 0.0D;
/*  395:     */     }
/*  396: 978 */     if (this.m_normalize) {
/*  397: 979 */       result = normalizedKernel(s1, s2);
/*  398:     */     } else {
/*  399: 981 */       result = unnormalizedKernel(s1, s2);
/*  400:     */     }
/*  401: 984 */     if (this.m_Debug)
/*  402:     */     {
/*  403: 985 */       long duration = System.currentTimeMillis() - start;
/*  404: 986 */       System.err.println("result: " + result);
/*  405: 987 */       System.err.println("evaluation time:" + duration + "\n");
/*  406:     */     }
/*  407: 991 */     if (key != -1L)
/*  408:     */     {
/*  409: 992 */       this.m_storage[location] = result;
/*  410: 993 */       this.m_keys[location] = (key + 1L);
/*  411:     */     }
/*  412: 995 */     return result;
/*  413:     */   }
/*  414:     */   
/*  415:     */   public void clean()
/*  416:     */   {
/*  417:1005 */     this.m_storage = null;
/*  418:1006 */     this.m_keys = null;
/*  419:     */   }
/*  420:     */   
/*  421:     */   public int numEvals()
/*  422:     */   {
/*  423:1016 */     return this.m_kernelEvals;
/*  424:     */   }
/*  425:     */   
/*  426:     */   public int numCacheHits()
/*  427:     */   {
/*  428:1028 */     return -1;
/*  429:     */   }
/*  430:     */   
/*  431:     */   public double normalizedKernel(char[] s, char[] t)
/*  432:     */   {
/*  433:1040 */     double k1 = unnormalizedKernel(s, s);
/*  434:1041 */     double k2 = unnormalizedKernel(t, t);
/*  435:1042 */     double normTerm = Math.sqrt(k1 * k2);
/*  436:1043 */     return unnormalizedKernel(s, t) / normTerm;
/*  437:     */   }
/*  438:     */   
/*  439:     */   public double unnormalizedKernel(char[] s, char[] t)
/*  440:     */   {
/*  441:1055 */     if (t.length > s.length)
/*  442:     */     {
/*  443:1058 */       char[] buf = s;
/*  444:1059 */       s = t;
/*  445:1060 */       t = buf;
/*  446:     */     }
/*  447:1062 */     if (this.m_PruningMethod == 0)
/*  448:     */     {
/*  449:1063 */       this.m_multX = ((s.length + 1) * (t.length + 1));
/*  450:1064 */       this.m_multY = (t.length + 1);
/*  451:1065 */       this.m_multZ = 1;
/*  452:1066 */       this.maxCache = this.m_internalCacheSize;
/*  453:1067 */       if (this.maxCache == 0) {
/*  454:1068 */         this.maxCache = ((this.m_subsequenceLength + 1) * this.m_multX);
/*  455:1069 */       } else if ((this.m_subsequenceLength + 1) * this.m_multX < this.maxCache) {
/*  456:1070 */         this.maxCache = ((this.m_subsequenceLength + 1) * this.m_multX);
/*  457:     */       }
/*  458:1072 */       this.m_useRecursionCache = true;
/*  459:1073 */       this.cachekhK = new int[this.maxCache];
/*  460:1074 */       this.cachekh2K = new int[this.maxCache];
/*  461:1075 */       this.cachekh = new double[this.maxCache];
/*  462:1076 */       this.cachekh2 = new double[this.maxCache];
/*  463:     */     }
/*  464:1077 */     else if (this.m_PruningMethod == 1)
/*  465:     */     {
/*  466:1078 */       this.maxCache = 0;
/*  467:1079 */       this.m_useRecursionCache = false;
/*  468:     */     }
/*  469:     */     double res;
/*  470:     */     double res;
/*  471:1083 */     if (this.m_PruningMethod == 1) {
/*  472:1084 */       res = kernelLP(this.m_subsequenceLength, s, s.length - 1, t, t.length - 1, this.m_maxSubsequenceLength);
/*  473:     */     } else {
/*  474:1087 */       res = kernel(this.m_subsequenceLength, s, s.length - 1, t, t.length - 1);
/*  475:     */     }
/*  476:1089 */     this.cachekh = null;
/*  477:1090 */     this.cachekhK = null;
/*  478:1091 */     this.cachekh2 = null;
/*  479:1092 */     this.cachekh2K = null;
/*  480:     */     
/*  481:1094 */     return res;
/*  482:     */   }
/*  483:     */   
/*  484:     */   protected double getReturnValue(int n)
/*  485:     */   {
/*  486:1105 */     if (n == 0) {
/*  487:1106 */       return 1.0D;
/*  488:     */     }
/*  489:1108 */     return 0.0D;
/*  490:     */   }
/*  491:     */   
/*  492:     */   protected double kernel(int n, char[] s, int endIndexS, char[] t, int endIndexT)
/*  493:     */   {
/*  494:1132 */     if (Math.min(endIndexS + 1, endIndexT + 1) < n) {
/*  495:1133 */       return getReturnValue(n);
/*  496:     */     }
/*  497:1137 */     double result = 0.0D;
/*  498:1142 */     for (int iS = endIndexS; iS > n - 2; iS--)
/*  499:     */     {
/*  500:1143 */       double buf = 0.0D;
/*  501:     */       
/*  502:1145 */       char x = s[iS];
/*  503:1147 */       for (int j = 0; j <= endIndexT; j++) {
/*  504:1148 */         if (t[j] == x) {
/*  505:1153 */           buf += kernelHelper(n - 1, s, iS - 1, t, j - 1);
/*  506:     */         }
/*  507:     */       }
/*  508:1159 */       result += buf * this.m_powersOflambda[2];
/*  509:     */     }
/*  510:1161 */     return result;
/*  511:     */   }
/*  512:     */   
/*  513:     */   protected double kernelHelper(int n, char[] s, int endIndexS, char[] t, int endIndexT)
/*  514:     */   {
/*  515:1179 */     if (n <= 0) {
/*  516:1180 */       return getReturnValue(n);
/*  517:     */     }
/*  518:1188 */     if (Math.min(endIndexS + 1, endIndexT + 1) < n) {
/*  519:1189 */       return getReturnValue(n);
/*  520:     */     }
/*  521:1191 */     int adr = 0;
/*  522:1192 */     if (this.m_useRecursionCache)
/*  523:     */     {
/*  524:1193 */       adr = this.m_multX * n + this.m_multY * endIndexS + this.m_multZ * endIndexT;
/*  525:1194 */       if (this.cachekhK[(adr % this.maxCache)] == adr + 1) {
/*  526:1195 */         return this.cachekh[(adr % this.maxCache)];
/*  527:     */       }
/*  528:     */     }
/*  529:1206 */     double result = 0.0D;
/*  530:     */     
/*  531:     */ 
/*  532:     */ 
/*  533:     */ 
/*  534:     */ 
/*  535:     */ 
/*  536:     */ 
/*  537:1214 */     result = this.m_lambda * kernelHelper(n, s, endIndexS - 1, t, endIndexT) + kernelHelper2(n, s, endIndexS, t, endIndexT);
/*  538:1216 */     if (this.m_useRecursionCache)
/*  539:     */     {
/*  540:1217 */       this.cachekhK[(adr % this.maxCache)] = (adr + 1);
/*  541:1218 */       this.cachekh[(adr % this.maxCache)] = result;
/*  542:     */     }
/*  543:1220 */     return result;
/*  544:     */   }
/*  545:     */   
/*  546:     */   protected double kernelHelper2(int n, char[] s, int endIndexS, char[] t, int endIndexT)
/*  547:     */   {
/*  548:1238 */     if ((endIndexS < 0) || (endIndexT < 0)) {
/*  549:1239 */       return getReturnValue(n);
/*  550:     */     }
/*  551:1242 */     int adr = 0;
/*  552:1243 */     if (this.m_useRecursionCache)
/*  553:     */     {
/*  554:1244 */       adr = this.m_multX * n + this.m_multY * endIndexS + this.m_multZ * endIndexT;
/*  555:1245 */       if (this.cachekh2K[(adr % this.maxCache)] == adr + 1) {
/*  556:1246 */         return this.cachekh2[(adr % this.maxCache)];
/*  557:     */       }
/*  558:     */     }
/*  559:1251 */     char x = s[endIndexS];
/*  560:1265 */     if (x == t[endIndexT])
/*  561:     */     {
/*  562:1266 */       double ret = this.m_lambda * (kernelHelper2(n, s, endIndexS, t, endIndexT - 1) + this.m_lambda * kernelHelper(n - 1, s, endIndexS - 1, t, endIndexT - 1));
/*  563:1269 */       if (this.m_useRecursionCache)
/*  564:     */       {
/*  565:1270 */         this.cachekh2K[(adr % this.maxCache)] = (adr + 1);
/*  566:1271 */         this.cachekh2[(adr % this.maxCache)] = ret;
/*  567:     */       }
/*  568:1273 */       return ret;
/*  569:     */     }
/*  570:1275 */     double ret = this.m_lambda * kernelHelper2(n, s, endIndexS, t, endIndexT - 1);
/*  571:1276 */     if (this.m_useRecursionCache)
/*  572:     */     {
/*  573:1277 */       this.cachekh2K[(adr % this.maxCache)] = (adr + 1);
/*  574:1278 */       this.cachekh2[(adr % this.maxCache)] = ret;
/*  575:     */     }
/*  576:1280 */     return ret;
/*  577:     */   }
/*  578:     */   
/*  579:     */   protected double kernelLP(int n, char[] s, int endIndexS, char[] t, int endIndexT, int remainingMatchLength)
/*  580:     */   {
/*  581:1320 */     if (Math.min(endIndexS + 1, endIndexT + 1) < n) {
/*  582:1321 */       return getReturnValue(n);
/*  583:     */     }
/*  584:1328 */     if (remainingMatchLength == 0) {
/*  585:1329 */       return getReturnValue(n);
/*  586:     */     }
/*  587:1331 */     double result = 0.0D;
/*  588:1333 */     for (int iS = endIndexS; iS > n - 2; iS--)
/*  589:     */     {
/*  590:1334 */       double buf = 0.0D;
/*  591:1335 */       char x = s[iS];
/*  592:1336 */       for (int j = 0; j <= endIndexT; j++) {
/*  593:1337 */         if (t[j] == x) {
/*  594:1340 */           buf += kernelHelperLP(n - 1, s, iS - 1, t, j - 1, remainingMatchLength - 2);
/*  595:     */         }
/*  596:     */       }
/*  597:1344 */       result += buf * this.m_powersOflambda[2];
/*  598:     */     }
/*  599:1346 */     return result;
/*  600:     */   }
/*  601:     */   
/*  602:     */   protected double kernelHelperLP(int n, char[] s, int endIndexS, char[] t, int endIndexT, int remainingMatchLength)
/*  603:     */   {
/*  604:1365 */     if (n == 0) {
/*  605:1366 */       return getReturnValue(n);
/*  606:     */     }
/*  607:1370 */     if (Math.min(endIndexS + 1, endIndexT + 1) < n) {
/*  608:1372 */       return getReturnValue(n);
/*  609:     */     }
/*  610:1380 */     if (remainingMatchLength < 2 * n) {
/*  611:1381 */       return getReturnValue(n);
/*  612:     */     }
/*  613:1383 */     int adr = 0;
/*  614:1384 */     if (this.m_useRecursionCache)
/*  615:     */     {
/*  616:1385 */       adr = this.m_multX * n + this.m_multY * endIndexS + this.m_multZ * endIndexT + this.m_multZZ * remainingMatchLength;
/*  617:1387 */       if (this.cachekh2K[(adr % this.maxCache)] == adr + 1) {
/*  618:1388 */         return this.cachekh2[(adr % this.maxCache)];
/*  619:     */       }
/*  620:     */     }
/*  621:1392 */     int rml = 0;
/*  622:1393 */     double result = 0.0D;
/*  623:1400 */     for (int iS = endIndexS - remainingMatchLength; iS <= endIndexS; iS++)
/*  624:     */     {
/*  625:1401 */       result *= this.m_lambda;
/*  626:1402 */       result += kernelHelper2LP(n, s, iS, t, endIndexT, rml++);
/*  627:     */     }
/*  628:1405 */     if ((this.m_useRecursionCache) && (endIndexS >= 0) && (endIndexT >= 0) && (n >= 0))
/*  629:     */     {
/*  630:1406 */       this.cachekhK[(adr % this.maxCache)] = (adr + 1);
/*  631:1407 */       this.cachekh[(adr % this.maxCache)] = result;
/*  632:     */     }
/*  633:1409 */     return result;
/*  634:     */   }
/*  635:     */   
/*  636:     */   protected double kernelHelper2LP(int n, char[] s, int endIndexS, char[] t, int endIndexT, int remainingMatchLength)
/*  637:     */   {
/*  638:1434 */     if (remainingMatchLength < 2 * n) {
/*  639:1435 */       return getReturnValue(n);
/*  640:     */     }
/*  641:1439 */     if ((endIndexS < 0) || (endIndexT < 0)) {
/*  642:1440 */       return getReturnValue(n);
/*  643:     */     }
/*  644:1442 */     int adr = 0;
/*  645:1443 */     if (this.m_useRecursionCache)
/*  646:     */     {
/*  647:1444 */       adr = this.m_multX * n + this.m_multY * endIndexS + this.m_multZ * endIndexT + this.m_multZZ * remainingMatchLength;
/*  648:1446 */       if (this.cachekh2K[(adr % this.maxCache)] == adr + 1) {
/*  649:1447 */         return this.cachekh2[(adr % this.maxCache)];
/*  650:     */       }
/*  651:     */     }
/*  652:1451 */     char x = s[endIndexS];
/*  653:1452 */     if (x == t[endIndexT])
/*  654:     */     {
/*  655:1453 */       double ret = this.m_lambda * (kernelHelper2LP(n, s, endIndexS, t, endIndexT - 1, remainingMatchLength - 1) + this.m_lambda * kernelHelperLP(n - 1, s, endIndexS - 1, t, endIndexT - 1, remainingMatchLength - 2));
/*  656:1458 */       if ((this.m_useRecursionCache) && (endIndexS >= 0) && (endIndexT >= 0) && (n >= 0))
/*  657:     */       {
/*  658:1459 */         this.cachekh2K[(adr % this.maxCache)] = (adr + 1);
/*  659:1460 */         this.cachekh2[(adr % this.maxCache)] = ret;
/*  660:     */       }
/*  661:1462 */       return ret;
/*  662:     */     }
/*  663:1470 */     int minIndex = endIndexT - remainingMatchLength;
/*  664:1471 */     if (minIndex < 0) {
/*  665:1472 */       minIndex = 0;
/*  666:     */     }
/*  667:1474 */     for (int i = endIndexT; i >= minIndex; i--) {
/*  668:1475 */       if (x == t[i])
/*  669:     */       {
/*  670:1476 */         int skipLength = endIndexT - i;
/*  671:1477 */         double ret = getPowerOfLambda(skipLength) * kernelHelper2LP(n, s, endIndexS, t, i, remainingMatchLength - skipLength);
/*  672:1480 */         if ((this.m_useRecursionCache) && (endIndexS >= 0) && (endIndexT >= 0) && (n >= 0))
/*  673:     */         {
/*  674:1481 */           this.cachekh2K[(adr % this.maxCache)] = (adr + 1);
/*  675:1482 */           this.cachekh2[(adr % this.maxCache)] = ret;
/*  676:     */         }
/*  677:1484 */         return ret;
/*  678:     */       }
/*  679:     */     }
/*  680:1487 */     double ret = getReturnValue(n);
/*  681:1488 */     if ((this.m_useRecursionCache) && (endIndexS >= 0) && (endIndexT >= 0) && (n >= 0))
/*  682:     */     {
/*  683:1489 */       this.cachekh2K[(adr % this.maxCache)] = (adr + 1);
/*  684:1490 */       this.cachekh2[(adr % this.maxCache)] = ret;
/*  685:     */     }
/*  686:1492 */     return ret;
/*  687:     */   }
/*  688:     */   
/*  689:     */   private double[] calculatePowersOfLambda()
/*  690:     */   {
/*  691:1501 */     double[] powers = new double[10001];
/*  692:1502 */     powers[0] = 1.0D;
/*  693:1503 */     double val = 1.0D;
/*  694:1504 */     for (int i = 1; i <= 10000; i++)
/*  695:     */     {
/*  696:1505 */       val *= this.m_lambda;
/*  697:1506 */       powers[i] = val;
/*  698:     */     }
/*  699:1508 */     return powers;
/*  700:     */   }
/*  701:     */   
/*  702:     */   private double getPowerOfLambda(int exponent)
/*  703:     */   {
/*  704:1518 */     if (exponent > 10000) {
/*  705:1519 */       return Math.pow(this.m_lambda, exponent);
/*  706:     */     }
/*  707:1522 */     if (exponent < 0) {
/*  708:1523 */       throw new IllegalArgumentException("only positive powers of lambda may be computed");
/*  709:     */     }
/*  710:1527 */     return this.m_powersOflambda[exponent];
/*  711:     */   }
/*  712:     */   
/*  713:     */   protected void initVars(Instances data)
/*  714:     */   {
/*  715:1537 */     super.initVars(data);
/*  716:     */     
/*  717:1539 */     this.m_kernelEvals = 0;
/*  718:     */     
/*  719:1541 */     this.m_strAttr = -1;
/*  720:1542 */     for (int i = 0; i < data.numAttributes(); i++) {
/*  721:1543 */       if (i != data.classIndex()) {
/*  722:1546 */         if (data.attribute(i).type() == 2)
/*  723:     */         {
/*  724:1547 */           this.m_strAttr = i;
/*  725:1548 */           break;
/*  726:     */         }
/*  727:     */       }
/*  728:     */     }
/*  729:1551 */     this.m_numInsts = this.m_data.numInstances();
/*  730:1552 */     this.m_storage = new double[this.m_cacheSize];
/*  731:1553 */     this.m_keys = new long[this.m_cacheSize];
/*  732:1554 */     this.m_powersOflambda = calculatePowersOfLambda();
/*  733:     */   }
/*  734:     */   
/*  735:     */   public Capabilities getCapabilities()
/*  736:     */   {
/*  737:1565 */     Capabilities result = super.getCapabilities();
/*  738:1566 */     result.disableAll();
/*  739:     */     
/*  740:1568 */     result.enable(Capabilities.Capability.STRING_ATTRIBUTES);
/*  741:1569 */     result.enableAllClasses();
/*  742:1570 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  743:1571 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  744:     */     
/*  745:1573 */     return result;
/*  746:     */   }
/*  747:     */   
/*  748:     */   public void buildKernel(Instances data)
/*  749:     */     throws Exception
/*  750:     */   {
/*  751:1585 */     super.buildKernel(data);
/*  752:     */   }
/*  753:     */   
/*  754:     */   public String getRevision()
/*  755:     */   {
/*  756:1595 */     return RevisionUtils.extract("$Revision: 12518 $");
/*  757:     */   }
/*  758:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.supportVector.StringKernel
 * JD-Core Version:    0.7.0.1
 */