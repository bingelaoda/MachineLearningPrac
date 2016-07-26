/*    1:     */ package weka.core;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.io.Serializable;
/*    5:     */ import java.util.ArrayList;
/*    6:     */ import java.util.Enumeration;
/*    7:     */ import java.util.Random;
/*    8:     */ import java.util.StringTokenizer;
/*    9:     */ import java.util.Vector;
/*   10:     */ 
/*   11:     */ public class TestInstances
/*   12:     */   implements Cloneable, Serializable, OptionHandler, RevisionHandler
/*   13:     */ {
/*   14:     */   private static final long serialVersionUID = -6263968936330390469L;
/*   15:     */   public static final int CLASS_IS_LAST = -1;
/*   16:     */   public static final int NO_CLASS = -2;
/*   17: 188 */   public static final String[] DEFAULT_WORDS = { "The", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog" };
/*   18:     */   public static final String DEFAULT_SEPARATORS = " ";
/*   19: 195 */   protected String[] m_Words = DEFAULT_WORDS;
/*   20: 198 */   protected String m_WordSeparators = " ";
/*   21: 201 */   protected String m_Relation = "Testdata";
/*   22: 204 */   protected int m_Seed = 1;
/*   23: 207 */   protected Random m_Random = new Random(this.m_Seed);
/*   24: 210 */   protected int m_NumInstances = 20;
/*   25: 213 */   protected int m_ClassType = 1;
/*   26: 216 */   protected int m_NumClasses = 2;
/*   27: 224 */   protected int m_ClassIndex = -1;
/*   28: 227 */   protected int m_NumNominal = 1;
/*   29: 230 */   protected int m_NumNominalValues = 2;
/*   30: 233 */   protected int m_NumNumeric = 0;
/*   31: 236 */   protected int m_NumString = 0;
/*   32: 239 */   protected int m_NumDate = 0;
/*   33: 242 */   protected int m_NumRelational = 0;
/*   34: 245 */   protected int m_NumRelationalNominal = 1;
/*   35: 248 */   protected int m_NumRelationalNominalValues = 2;
/*   36: 251 */   protected int m_NumRelationalNumeric = 0;
/*   37: 254 */   protected int m_NumRelationalString = 0;
/*   38: 257 */   protected int m_NumRelationalDate = 0;
/*   39: 260 */   protected boolean m_MultiInstance = false;
/*   40: 266 */   protected int m_NumInstancesRelational = 10;
/*   41: 269 */   protected Instances[] m_RelationalFormat = null;
/*   42: 272 */   protected Instances m_RelationalClassFormat = null;
/*   43: 275 */   protected Instances m_Data = null;
/*   44: 278 */   protected CapabilitiesHandler m_Handler = null;
/*   45:     */   
/*   46:     */   public TestInstances()
/*   47:     */   {
/*   48: 286 */     setRelation("Testdata");
/*   49: 287 */     setSeed(1);
/*   50: 288 */     setNumInstances(20);
/*   51: 289 */     setClassType(1);
/*   52: 290 */     setNumClasses(2);
/*   53: 291 */     setClassIndex(-1);
/*   54: 292 */     setNumNominal(1);
/*   55: 293 */     setNumNominalValues(2);
/*   56: 294 */     setNumNumeric(0);
/*   57: 295 */     setNumString(0);
/*   58: 296 */     setNumDate(0);
/*   59: 297 */     setNumRelational(0);
/*   60: 298 */     setNumRelationalNominal(1);
/*   61: 299 */     setNumRelationalNominalValues(2);
/*   62: 300 */     setNumRelationalNumeric(0);
/*   63: 301 */     setNumRelationalString(0);
/*   64: 302 */     setNumRelationalDate(0);
/*   65: 303 */     setNumInstancesRelational(10);
/*   66: 304 */     setMultiInstance(false);
/*   67: 305 */     setWords(arrayToList(DEFAULT_WORDS));
/*   68: 306 */     setWordSeparators(" ");
/*   69:     */   }
/*   70:     */   
/*   71:     */   public Object clone()
/*   72:     */   {
/*   73: 318 */     TestInstances result = new TestInstances();
/*   74: 319 */     result.assign(this);
/*   75:     */     
/*   76: 321 */     return result;
/*   77:     */   }
/*   78:     */   
/*   79:     */   public void assign(TestInstances t)
/*   80:     */   {
/*   81: 330 */     setRelation(t.getRelation());
/*   82: 331 */     setSeed(t.getSeed());
/*   83: 332 */     setNumInstances(t.getNumInstances());
/*   84: 333 */     setClassType(t.getClassType());
/*   85: 334 */     setNumClasses(t.getNumClasses());
/*   86: 335 */     setClassIndex(t.getClassIndex());
/*   87: 336 */     setNumNominal(t.getNumNominal());
/*   88: 337 */     setNumNominalValues(t.getNumNominalValues());
/*   89: 338 */     setNumNumeric(t.getNumNumeric());
/*   90: 339 */     setNumString(t.getNumString());
/*   91: 340 */     setNumDate(t.getNumDate());
/*   92: 341 */     setNumRelational(t.getNumRelational());
/*   93: 342 */     setNumRelationalNominal(t.getNumRelationalNominal());
/*   94: 343 */     setNumRelationalNominalValues(t.getNumRelationalNominalValues());
/*   95: 344 */     setNumRelationalNumeric(t.getNumRelationalNumeric());
/*   96: 345 */     setNumRelationalString(t.getNumRelationalString());
/*   97: 346 */     setNumRelationalDate(t.getNumRelationalDate());
/*   98: 347 */     setMultiInstance(t.getMultiInstance());
/*   99: 348 */     for (int i = 0; i < t.getNumRelational(); i++) {
/*  100: 349 */       setRelationalFormat(i, t.getRelationalFormat(i));
/*  101:     */     }
/*  102: 351 */     setRelationalClassFormat(t.getRelationalClassFormat());
/*  103: 352 */     setNumInstancesRelational(t.getNumInstancesRelational());
/*  104: 353 */     setWords(t.getWords());
/*  105: 354 */     setWordSeparators(t.getWordSeparators());
/*  106:     */   }
/*  107:     */   
/*  108:     */   public Enumeration<Option> listOptions()
/*  109:     */   {
/*  110: 364 */     Vector<Option> result = new Vector();
/*  111:     */     
/*  112: 366 */     result.add(new Option("\tThe name of the data set.", "relation", 1, "-relation <name>"));
/*  113:     */     
/*  114:     */ 
/*  115: 369 */     result.add(new Option("\tThe seed value.", "seed", 1, "-seed <num>"));
/*  116:     */     
/*  117: 371 */     result.add(new Option("\tThe number of instances in the datasets (default 20).", "num-instances", 1, "-num-instances <num>"));
/*  118:     */     
/*  119:     */ 
/*  120:     */ 
/*  121: 375 */     result.add(new Option("\tThe class type, see constants in weka.core.Attribute\n\t(default 1=nominal).", "class-type", 1, "-class-type <num>"));
/*  122:     */     
/*  123:     */ 
/*  124:     */ 
/*  125: 379 */     result.add(new Option("\tThe number of classes to generate (for nominal classes only)\n\t(default 2).", "class-values", 1, "-class-values <num>"));
/*  126:     */     
/*  127:     */ 
/*  128:     */ 
/*  129: 383 */     result.add(new Option("\tThe class index, with -1=last, (default -1).", "class-index", 1, "-class-index <num>"));
/*  130:     */     
/*  131:     */ 
/*  132: 386 */     result.add(new Option("\tDoesn't include a class attribute in the output.", "no-class", 0, "-no-class"));
/*  133:     */     
/*  134:     */ 
/*  135: 389 */     result.add(new Option("\tThe number of nominal attributes (default 1).", "nominal", 1, "-nominal <num>"));
/*  136:     */     
/*  137:     */ 
/*  138: 392 */     result.add(new Option("\tThe number of values for nominal attributes (default 2).", "nominal-values", 1, "-nominal-values <num>"));
/*  139:     */     
/*  140:     */ 
/*  141:     */ 
/*  142: 396 */     result.add(new Option("\tThe number of numeric attributes (default 0).", "numeric", 1, "-numeric <num>"));
/*  143:     */     
/*  144:     */ 
/*  145: 399 */     result.add(new Option("\tThe number of string attributes (default 0).", "string", 1, "-string <num>"));
/*  146:     */     
/*  147:     */ 
/*  148: 402 */     result.add(new Option("\tThe words to use in string attributes.", "words", 1, "-words <comma-separated-list>"));
/*  149:     */     
/*  150:     */ 
/*  151: 405 */     result.add(new Option("\tThe word separators to use in string attributes.", "word-separators", 1, "-word-separators <chars>"));
/*  152:     */     
/*  153:     */ 
/*  154: 408 */     result.add(new Option("\tThe number of date attributes (default 0).", "date", 1, "-date <num>"));
/*  155:     */     
/*  156:     */ 
/*  157: 411 */     result.add(new Option("\tThe number of relational attributes (default 0).", "relational", 1, "-relational <num>"));
/*  158:     */     
/*  159:     */ 
/*  160: 414 */     result.add(new Option("\tThe number of nominal attributes in a rel. attribute (default 1).", "relational-nominal", 1, "-relational-nominal <num>"));
/*  161:     */     
/*  162:     */ 
/*  163:     */ 
/*  164: 418 */     result.add(new Option("\tThe number of values for nominal attributes in a rel. attribute (default 2).", "relational-nominal-values", 1, "-relational-nominal-values <num>"));
/*  165:     */     
/*  166:     */ 
/*  167:     */ 
/*  168:     */ 
/*  169: 423 */     result.add(new Option("\tThe number of numeric attributes in a rel. attribute (default 0).", "relational-numeric", 1, "-relational-numeric <num>"));
/*  170:     */     
/*  171:     */ 
/*  172:     */ 
/*  173: 427 */     result.add(new Option("\tThe number of string attributes in a rel. attribute (default 0).", "relational-string", 1, "-relational-string <num>"));
/*  174:     */     
/*  175:     */ 
/*  176:     */ 
/*  177: 431 */     result.add(new Option("\tThe number of date attributes in a rel. attribute (default 0).", "relational-date", 1, "-relational-date <num>"));
/*  178:     */     
/*  179:     */ 
/*  180:     */ 
/*  181: 435 */     result.add(new Option("\tThe number of instances in relational/bag attributes (default 10).", "num-instances-relational", 1, "-num-instances-relational <num>"));
/*  182:     */     
/*  183:     */ 
/*  184:     */ 
/*  185: 439 */     result.add(new Option("\tGenerates multi-instance data.", "multi-instance", 0, "-multi-instance"));
/*  186:     */     
/*  187:     */ 
/*  188: 442 */     result.add(new Option("\tThe Capabilities handler to base the dataset on.\n\tThe other parameters can be used to override the ones\n\tdetermined from the handler. Additional parameters for\n\thandler can be passed on after the '--'.", "W", 1, "-W <classname>"));
/*  189:     */     
/*  190:     */ 
/*  191:     */ 
/*  192:     */ 
/*  193:     */ 
/*  194:     */ 
/*  195: 449 */     return result.elements();
/*  196:     */   }
/*  197:     */   
/*  198:     */   public void setOptions(String[] options)
/*  199:     */     throws Exception
/*  200:     */   {
/*  201: 591 */     boolean initialized = false;
/*  202:     */     
/*  203: 593 */     String tmpStr = Utils.getOption('W', options);
/*  204: 594 */     if (tmpStr.length() > 0)
/*  205:     */     {
/*  206: 595 */       Class<?> cls = Class.forName(tmpStr);
/*  207: 596 */       if (ClassDiscovery.hasInterface(CapabilitiesHandler.class, cls))
/*  208:     */       {
/*  209: 597 */         initialized = true;
/*  210: 598 */         CapabilitiesHandler handler = (CapabilitiesHandler)cls.newInstance();
/*  211: 599 */         if ((handler instanceof OptionHandler)) {
/*  212: 600 */           ((OptionHandler)handler).setOptions(Utils.partitionOptions(options));
/*  213:     */         }
/*  214: 602 */         setHandler(handler);
/*  215:     */         
/*  216: 604 */         assign(forCapabilities(handler.getCapabilities()));
/*  217:     */       }
/*  218:     */       else
/*  219:     */       {
/*  220: 606 */         throw new IllegalArgumentException("Class '" + tmpStr + "' is not a CapabilitiesHandler!");
/*  221:     */       }
/*  222:     */     }
/*  223: 611 */     tmpStr = Utils.getOption("relation", options);
/*  224: 612 */     if (tmpStr.length() != 0) {
/*  225: 613 */       setRelation(tmpStr);
/*  226: 614 */     } else if (!initialized) {
/*  227: 615 */       setRelation("Testdata");
/*  228:     */     }
/*  229: 618 */     tmpStr = Utils.getOption("seed", options);
/*  230: 619 */     if (tmpStr.length() != 0) {
/*  231: 620 */       setSeed(Integer.parseInt(tmpStr));
/*  232: 621 */     } else if (!initialized) {
/*  233: 622 */       setSeed(1);
/*  234:     */     }
/*  235: 625 */     tmpStr = Utils.getOption("num-instances", options);
/*  236: 626 */     if (tmpStr.length() != 0) {
/*  237: 627 */       setNumInstances(Integer.parseInt(tmpStr));
/*  238: 628 */     } else if (!initialized) {
/*  239: 629 */       setNumInstances(20);
/*  240:     */     }
/*  241: 632 */     setNoClass(Utils.getFlag("no-class", options));
/*  242: 634 */     if (!getNoClass())
/*  243:     */     {
/*  244: 635 */       tmpStr = Utils.getOption("class-type", options);
/*  245: 636 */       if (tmpStr.length() != 0) {
/*  246: 637 */         setClassType(Integer.parseInt(tmpStr));
/*  247: 638 */       } else if (!initialized) {
/*  248: 639 */         setClassType(1);
/*  249:     */       }
/*  250: 642 */       tmpStr = Utils.getOption("class-values", options);
/*  251: 643 */       if (tmpStr.length() != 0) {
/*  252: 644 */         setNumClasses(Integer.parseInt(tmpStr));
/*  253: 645 */       } else if (!initialized) {
/*  254: 646 */         setNumClasses(2);
/*  255:     */       }
/*  256: 649 */       tmpStr = Utils.getOption("class-index", options);
/*  257: 650 */       if (tmpStr.length() != 0) {
/*  258: 651 */         setClassIndex(Integer.parseInt(tmpStr));
/*  259: 652 */       } else if (!initialized) {
/*  260: 653 */         setClassIndex(-1);
/*  261:     */       }
/*  262:     */     }
/*  263: 657 */     tmpStr = Utils.getOption("nominal", options);
/*  264: 658 */     if (tmpStr.length() != 0) {
/*  265: 659 */       setNumNominal(Integer.parseInt(tmpStr));
/*  266: 660 */     } else if (!initialized) {
/*  267: 661 */       setNumNominal(1);
/*  268:     */     }
/*  269: 664 */     tmpStr = Utils.getOption("nominal-values", options);
/*  270: 665 */     if (tmpStr.length() != 0) {
/*  271: 666 */       setNumNominalValues(Integer.parseInt(tmpStr));
/*  272: 667 */     } else if (!initialized) {
/*  273: 668 */       setNumNominalValues(2);
/*  274:     */     }
/*  275: 671 */     tmpStr = Utils.getOption("numeric", options);
/*  276: 672 */     if (tmpStr.length() != 0) {
/*  277: 673 */       setNumNumeric(Integer.parseInt(tmpStr));
/*  278: 674 */     } else if (!initialized) {
/*  279: 675 */       setNumNumeric(0);
/*  280:     */     }
/*  281: 678 */     tmpStr = Utils.getOption("string", options);
/*  282: 679 */     if (tmpStr.length() != 0) {
/*  283: 680 */       setNumString(Integer.parseInt(tmpStr));
/*  284: 681 */     } else if (!initialized) {
/*  285: 682 */       setNumString(0);
/*  286:     */     }
/*  287: 685 */     tmpStr = Utils.getOption("words", options);
/*  288: 686 */     if (tmpStr.length() != 0) {
/*  289: 687 */       setWords(tmpStr);
/*  290: 688 */     } else if (!initialized) {
/*  291: 689 */       setWords(arrayToList(DEFAULT_WORDS));
/*  292:     */     }
/*  293: 692 */     if (Utils.getOptionPos("word-separators", options) > -1)
/*  294:     */     {
/*  295: 693 */       tmpStr = Utils.getOption("word-separators", options);
/*  296: 694 */       setWordSeparators(tmpStr);
/*  297:     */     }
/*  298: 695 */     else if (!initialized)
/*  299:     */     {
/*  300: 696 */       setWordSeparators(" ");
/*  301:     */     }
/*  302: 699 */     tmpStr = Utils.getOption("date", options);
/*  303: 700 */     if (tmpStr.length() != 0) {
/*  304: 701 */       setNumDate(Integer.parseInt(tmpStr));
/*  305: 702 */     } else if (!initialized) {
/*  306: 703 */       setNumDate(0);
/*  307:     */     }
/*  308: 706 */     tmpStr = Utils.getOption("relational", options);
/*  309: 707 */     if (tmpStr.length() != 0) {
/*  310: 708 */       setNumRelational(Integer.parseInt(tmpStr));
/*  311: 709 */     } else if (!initialized) {
/*  312: 710 */       setNumRelational(0);
/*  313:     */     }
/*  314: 713 */     tmpStr = Utils.getOption("relational-nominal", options);
/*  315: 714 */     if (tmpStr.length() != 0) {
/*  316: 715 */       setNumRelationalNominal(Integer.parseInt(tmpStr));
/*  317: 716 */     } else if (!initialized) {
/*  318: 717 */       setNumRelationalNominal(1);
/*  319:     */     }
/*  320: 720 */     tmpStr = Utils.getOption("relational-nominal-values", options);
/*  321: 721 */     if (tmpStr.length() != 0) {
/*  322: 722 */       setNumRelationalNominalValues(Integer.parseInt(tmpStr));
/*  323: 723 */     } else if (!initialized) {
/*  324: 724 */       setNumRelationalNominalValues(2);
/*  325:     */     }
/*  326: 727 */     tmpStr = Utils.getOption("relational-numeric", options);
/*  327: 728 */     if (tmpStr.length() != 0) {
/*  328: 729 */       setNumRelationalNumeric(Integer.parseInt(tmpStr));
/*  329: 730 */     } else if (!initialized) {
/*  330: 731 */       setNumRelationalNumeric(0);
/*  331:     */     }
/*  332: 734 */     tmpStr = Utils.getOption("relational-string", options);
/*  333: 735 */     if (tmpStr.length() != 0) {
/*  334: 736 */       setNumRelationalString(Integer.parseInt(tmpStr));
/*  335: 737 */     } else if (!initialized) {
/*  336: 738 */       setNumRelationalString(0);
/*  337:     */     }
/*  338: 741 */     tmpStr = Utils.getOption("num-instances-relational", options);
/*  339: 742 */     if (tmpStr.length() != 0) {
/*  340: 743 */       setNumInstancesRelational(Integer.parseInt(tmpStr));
/*  341: 744 */     } else if (!initialized) {
/*  342: 745 */       setNumInstancesRelational(10);
/*  343:     */     }
/*  344: 748 */     if (!initialized) {
/*  345: 749 */       setMultiInstance(Utils.getFlag("multi-instance", options));
/*  346:     */     }
/*  347:     */   }
/*  348:     */   
/*  349:     */   public String[] getOptions()
/*  350:     */   {
/*  351: 764 */     Vector<String> result = new Vector();
/*  352:     */     
/*  353: 766 */     result.add("-relation");
/*  354: 767 */     result.add(getRelation());
/*  355:     */     
/*  356: 769 */     result.add("-seed");
/*  357: 770 */     result.add("" + getSeed());
/*  358:     */     
/*  359: 772 */     result.add("-num-instances");
/*  360: 773 */     result.add("" + getNumInstances());
/*  361: 775 */     if (getNoClass())
/*  362:     */     {
/*  363: 776 */       result.add("-no-class");
/*  364:     */     }
/*  365:     */     else
/*  366:     */     {
/*  367: 778 */       result.add("-class-type");
/*  368: 779 */       result.add("" + getClassType());
/*  369:     */       
/*  370: 781 */       result.add("-class-values");
/*  371: 782 */       result.add("" + getNumClasses());
/*  372:     */       
/*  373: 784 */       result.add("-class-index");
/*  374: 785 */       result.add("" + getClassIndex());
/*  375:     */     }
/*  376: 788 */     result.add("-nominal");
/*  377: 789 */     result.add("" + getNumNominal());
/*  378:     */     
/*  379: 791 */     result.add("-nominal-values");
/*  380: 792 */     result.add("" + getNumNominalValues());
/*  381:     */     
/*  382: 794 */     result.add("-numeric");
/*  383: 795 */     result.add("" + getNumNumeric());
/*  384:     */     
/*  385: 797 */     result.add("-string");
/*  386: 798 */     result.add("" + getNumString());
/*  387:     */     
/*  388: 800 */     result.add("-words");
/*  389: 801 */     result.add("" + getWords());
/*  390:     */     
/*  391: 803 */     result.add("-word-separators");
/*  392: 804 */     result.add("" + getWordSeparators());
/*  393:     */     
/*  394: 806 */     result.add("-date");
/*  395: 807 */     result.add("" + getNumDate());
/*  396:     */     
/*  397: 809 */     result.add("-relational");
/*  398: 810 */     result.add("" + getNumRelational());
/*  399:     */     
/*  400: 812 */     result.add("-relational-nominal");
/*  401: 813 */     result.add("" + getNumRelationalNominal());
/*  402:     */     
/*  403: 815 */     result.add("-relational-nominal-values");
/*  404: 816 */     result.add("" + getNumRelationalNominalValues());
/*  405:     */     
/*  406: 818 */     result.add("-relational-numeric");
/*  407: 819 */     result.add("" + getNumRelationalNumeric());
/*  408:     */     
/*  409: 821 */     result.add("-relational-string");
/*  410: 822 */     result.add("" + getNumRelationalString());
/*  411:     */     
/*  412: 824 */     result.add("-relational-date");
/*  413: 825 */     result.add("" + getNumRelationalDate());
/*  414:     */     
/*  415: 827 */     result.add("-num-instances-relational");
/*  416: 828 */     result.add("" + getNumInstancesRelational());
/*  417: 830 */     if (getMultiInstance()) {
/*  418: 831 */       result.add("-multi-instance");
/*  419:     */     }
/*  420: 834 */     if (getHandler() != null)
/*  421:     */     {
/*  422: 835 */       result.add("-W");
/*  423: 836 */       result.add(getHandler().getClass().getName());
/*  424: 837 */       if ((getHandler() instanceof OptionHandler))
/*  425:     */       {
/*  426: 838 */         result.add("--");
/*  427: 839 */         String[] options = ((OptionHandler)getHandler()).getOptions();
/*  428: 840 */         for (int i = 0; i < options.length; i++) {
/*  429: 841 */           result.add(options[i]);
/*  430:     */         }
/*  431:     */       }
/*  432:     */     }
/*  433: 846 */     return (String[])result.toArray(new String[result.size()]);
/*  434:     */   }
/*  435:     */   
/*  436:     */   public void setRelation(String value)
/*  437:     */   {
/*  438: 855 */     this.m_Relation = value;
/*  439:     */   }
/*  440:     */   
/*  441:     */   public String getRelation()
/*  442:     */   {
/*  443: 864 */     return this.m_Relation;
/*  444:     */   }
/*  445:     */   
/*  446:     */   public void setSeed(int value)
/*  447:     */   {
/*  448: 873 */     this.m_Seed = value;
/*  449: 874 */     this.m_Random = new Random(this.m_Seed);
/*  450:     */   }
/*  451:     */   
/*  452:     */   public int getSeed()
/*  453:     */   {
/*  454: 883 */     return this.m_Seed;
/*  455:     */   }
/*  456:     */   
/*  457:     */   public void setNumInstances(int value)
/*  458:     */   {
/*  459: 892 */     this.m_NumInstances = value;
/*  460:     */   }
/*  461:     */   
/*  462:     */   public int getNumInstances()
/*  463:     */   {
/*  464: 901 */     return this.m_NumInstances;
/*  465:     */   }
/*  466:     */   
/*  467:     */   public void setClassType(int value)
/*  468:     */   {
/*  469: 910 */     this.m_ClassType = value;
/*  470: 911 */     this.m_RelationalClassFormat = null;
/*  471:     */   }
/*  472:     */   
/*  473:     */   public int getClassType()
/*  474:     */   {
/*  475: 920 */     return this.m_ClassType;
/*  476:     */   }
/*  477:     */   
/*  478:     */   public void setNumClasses(int value)
/*  479:     */   {
/*  480: 929 */     this.m_NumClasses = value;
/*  481:     */   }
/*  482:     */   
/*  483:     */   public int getNumClasses()
/*  484:     */   {
/*  485: 938 */     return this.m_NumClasses;
/*  486:     */   }
/*  487:     */   
/*  488:     */   public void setClassIndex(int value)
/*  489:     */   {
/*  490: 949 */     this.m_ClassIndex = value;
/*  491:     */   }
/*  492:     */   
/*  493:     */   public int getClassIndex()
/*  494:     */   {
/*  495: 960 */     return this.m_ClassIndex;
/*  496:     */   }
/*  497:     */   
/*  498:     */   public void setNoClass(boolean value)
/*  499:     */   {
/*  500: 972 */     if (value) {
/*  501: 973 */       setClassIndex(-2);
/*  502:     */     } else {
/*  503: 975 */       setClassIndex(-1);
/*  504:     */     }
/*  505:     */   }
/*  506:     */   
/*  507:     */   public boolean getNoClass()
/*  508:     */   {
/*  509: 985 */     return getClassIndex() == -2;
/*  510:     */   }
/*  511:     */   
/*  512:     */   public void setNumNominal(int value)
/*  513:     */   {
/*  514: 994 */     this.m_NumNominal = value;
/*  515:     */   }
/*  516:     */   
/*  517:     */   public int getNumNominal()
/*  518:     */   {
/*  519:1003 */     return this.m_NumNominal;
/*  520:     */   }
/*  521:     */   
/*  522:     */   public void setNumNominalValues(int value)
/*  523:     */   {
/*  524:1012 */     this.m_NumNominalValues = value;
/*  525:     */   }
/*  526:     */   
/*  527:     */   public int getNumNominalValues()
/*  528:     */   {
/*  529:1021 */     return this.m_NumNominalValues;
/*  530:     */   }
/*  531:     */   
/*  532:     */   public void setNumNumeric(int value)
/*  533:     */   {
/*  534:1030 */     this.m_NumNumeric = value;
/*  535:     */   }
/*  536:     */   
/*  537:     */   public int getNumNumeric()
/*  538:     */   {
/*  539:1039 */     return this.m_NumNumeric;
/*  540:     */   }
/*  541:     */   
/*  542:     */   public void setNumString(int value)
/*  543:     */   {
/*  544:1048 */     this.m_NumString = value;
/*  545:     */   }
/*  546:     */   
/*  547:     */   public int getNumString()
/*  548:     */   {
/*  549:1057 */     return this.m_NumString;
/*  550:     */   }
/*  551:     */   
/*  552:     */   protected static String[] listToArray(String value)
/*  553:     */   {
/*  554:1070 */     Vector<String> list = new Vector();
/*  555:1071 */     StringTokenizer tok = new StringTokenizer(value, ",");
/*  556:1072 */     while (tok.hasMoreTokens()) {
/*  557:1073 */       list.add(tok.nextToken());
/*  558:     */     }
/*  559:1076 */     return (String[])list.toArray(new String[list.size()]);
/*  560:     */   }
/*  561:     */   
/*  562:     */   protected static String arrayToList(String[] value)
/*  563:     */   {
/*  564:1089 */     String result = "";
/*  565:1091 */     for (int i = 0; i < value.length; i++)
/*  566:     */     {
/*  567:1092 */       if (i > 0) {
/*  568:1093 */         result = result + ",";
/*  569:     */       }
/*  570:1095 */       result = result + value[i];
/*  571:     */     }
/*  572:1098 */     return result;
/*  573:     */   }
/*  574:     */   
/*  575:     */   public void setWords(String value)
/*  576:     */   {
/*  577:1109 */     if (listToArray(value).length < 2) {
/*  578:1110 */       throw new IllegalArgumentException("At least 2 words must be provided!");
/*  579:     */     }
/*  580:1113 */     this.m_Words = listToArray(value);
/*  581:     */   }
/*  582:     */   
/*  583:     */   public String getWords()
/*  584:     */   {
/*  585:1122 */     return arrayToList(this.m_Words);
/*  586:     */   }
/*  587:     */   
/*  588:     */   public void setWordSeparators(String value)
/*  589:     */   {
/*  590:1131 */     this.m_WordSeparators = value;
/*  591:     */   }
/*  592:     */   
/*  593:     */   public String getWordSeparators()
/*  594:     */   {
/*  595:1140 */     return this.m_WordSeparators;
/*  596:     */   }
/*  597:     */   
/*  598:     */   public void setNumDate(int value)
/*  599:     */   {
/*  600:1149 */     this.m_NumDate = value;
/*  601:     */   }
/*  602:     */   
/*  603:     */   public int getNumDate()
/*  604:     */   {
/*  605:1158 */     return this.m_NumDate;
/*  606:     */   }
/*  607:     */   
/*  608:     */   public void setNumRelational(int value)
/*  609:     */   {
/*  610:1167 */     this.m_NumRelational = value;
/*  611:1168 */     this.m_RelationalFormat = new Instances[value];
/*  612:     */   }
/*  613:     */   
/*  614:     */   public int getNumRelational()
/*  615:     */   {
/*  616:1177 */     return this.m_NumRelational;
/*  617:     */   }
/*  618:     */   
/*  619:     */   public void setNumRelationalNominal(int value)
/*  620:     */   {
/*  621:1186 */     this.m_NumRelationalNominal = value;
/*  622:     */   }
/*  623:     */   
/*  624:     */   public int getNumRelationalNominal()
/*  625:     */   {
/*  626:1195 */     return this.m_NumRelationalNominal;
/*  627:     */   }
/*  628:     */   
/*  629:     */   public void setNumRelationalNominalValues(int value)
/*  630:     */   {
/*  631:1204 */     this.m_NumRelationalNominalValues = value;
/*  632:     */   }
/*  633:     */   
/*  634:     */   public int getNumRelationalNominalValues()
/*  635:     */   {
/*  636:1214 */     return this.m_NumRelationalNominalValues;
/*  637:     */   }
/*  638:     */   
/*  639:     */   public void setNumRelationalNumeric(int value)
/*  640:     */   {
/*  641:1223 */     this.m_NumRelationalNumeric = value;
/*  642:     */   }
/*  643:     */   
/*  644:     */   public int getNumRelationalNumeric()
/*  645:     */   {
/*  646:1232 */     return this.m_NumRelationalNumeric;
/*  647:     */   }
/*  648:     */   
/*  649:     */   public void setNumRelationalString(int value)
/*  650:     */   {
/*  651:1241 */     this.m_NumRelationalString = value;
/*  652:     */   }
/*  653:     */   
/*  654:     */   public int getNumRelationalString()
/*  655:     */   {
/*  656:1250 */     return this.m_NumRelationalString;
/*  657:     */   }
/*  658:     */   
/*  659:     */   public void setNumRelationalDate(int value)
/*  660:     */   {
/*  661:1259 */     this.m_NumRelationalDate = value;
/*  662:     */   }
/*  663:     */   
/*  664:     */   public int getNumRelationalDate()
/*  665:     */   {
/*  666:1268 */     return this.m_NumRelationalDate;
/*  667:     */   }
/*  668:     */   
/*  669:     */   public void setNumInstancesRelational(int value)
/*  670:     */   {
/*  671:1277 */     this.m_NumInstancesRelational = value;
/*  672:     */   }
/*  673:     */   
/*  674:     */   public int getNumInstancesRelational()
/*  675:     */   {
/*  676:1287 */     return this.m_NumInstancesRelational;
/*  677:     */   }
/*  678:     */   
/*  679:     */   public void setMultiInstance(boolean value)
/*  680:     */   {
/*  681:1297 */     this.m_MultiInstance = value;
/*  682:     */   }
/*  683:     */   
/*  684:     */   public boolean getMultiInstance()
/*  685:     */   {
/*  686:1306 */     return this.m_MultiInstance;
/*  687:     */   }
/*  688:     */   
/*  689:     */   public void setRelationalFormat(int index, Instances value)
/*  690:     */   {
/*  691:1316 */     if (value != null) {
/*  692:1317 */       this.m_RelationalFormat[index] = new Instances(value, 0);
/*  693:     */     } else {
/*  694:1319 */       this.m_RelationalFormat[index] = null;
/*  695:     */     }
/*  696:     */   }
/*  697:     */   
/*  698:     */   public Instances getRelationalFormat(int index)
/*  699:     */   {
/*  700:1330 */     return this.m_RelationalFormat[index];
/*  701:     */   }
/*  702:     */   
/*  703:     */   public void setRelationalClassFormat(Instances value)
/*  704:     */   {
/*  705:1339 */     if (value != null) {
/*  706:1340 */       this.m_RelationalClassFormat = new Instances(value, 0);
/*  707:     */     } else {
/*  708:1342 */       this.m_RelationalClassFormat = null;
/*  709:     */     }
/*  710:     */   }
/*  711:     */   
/*  712:     */   public Instances getRelationalClassFormat()
/*  713:     */   {
/*  714:1353 */     return this.m_RelationalClassFormat;
/*  715:     */   }
/*  716:     */   
/*  717:     */   public int getNumAttributes()
/*  718:     */   {
/*  719:1365 */     int result = this.m_NumNominal + this.m_NumNumeric + this.m_NumString + this.m_NumDate + this.m_NumRelational;
/*  720:1368 */     if (!getNoClass()) {
/*  721:1369 */       result++;
/*  722:     */     }
/*  723:1372 */     return result;
/*  724:     */   }
/*  725:     */   
/*  726:     */   public Instances getData()
/*  727:     */   {
/*  728:1381 */     return this.m_Data;
/*  729:     */   }
/*  730:     */   
/*  731:     */   public void setHandler(CapabilitiesHandler value)
/*  732:     */   {
/*  733:1390 */     this.m_Handler = value;
/*  734:     */   }
/*  735:     */   
/*  736:     */   public CapabilitiesHandler getHandler()
/*  737:     */   {
/*  738:1400 */     return this.m_Handler;
/*  739:     */   }
/*  740:     */   
/*  741:     */   protected Attribute generateAttribute(int index, int attType, String namePrefix)
/*  742:     */     throws Exception
/*  743:     */   {
/*  744:1423 */     Attribute result = null;
/*  745:     */     int nomCount;
/*  746:     */     int valIndex;
/*  747:     */     int nomCount;
/*  748:     */     String prefix;
/*  749:     */     String name;
/*  750:1426 */     if (index == -1)
/*  751:     */     {
/*  752:1427 */       int valIndex = 0;
/*  753:1428 */       String name = "Class";
/*  754:1429 */       String prefix = "class";
/*  755:1430 */       nomCount = getNumClasses();
/*  756:     */     }
/*  757:     */     else
/*  758:     */     {
/*  759:1432 */       valIndex = index;
/*  760:1433 */       nomCount = getNumNominalValues();
/*  761:1434 */       prefix = "att" + (valIndex + 1) + "val";
/*  762:1436 */       switch (attType)
/*  763:     */       {
/*  764:     */       case 1: 
/*  765:1438 */         name = "Nominal" + (valIndex + 1);
/*  766:1439 */         break;
/*  767:     */       case 0: 
/*  768:1442 */         name = "Numeric" + (valIndex + 1);
/*  769:1443 */         break;
/*  770:     */       case 2: 
/*  771:1446 */         name = "String" + (valIndex + 1);
/*  772:1447 */         break;
/*  773:     */       case 3: 
/*  774:1450 */         name = "Date" + (valIndex + 1);
/*  775:1451 */         break;
/*  776:     */       case 4: 
/*  777:1454 */         name = "Relational" + (valIndex + 1);
/*  778:1455 */         break;
/*  779:     */       default: 
/*  780:1458 */         throw new IllegalArgumentException("Attribute type '" + attType + "' unknown!");
/*  781:     */       }
/*  782:     */     }
/*  783:1463 */     switch (attType)
/*  784:     */     {
/*  785:     */     case 1: 
/*  786:1465 */       ArrayList<String> nomStrings = new ArrayList(valIndex + 1);
/*  787:1466 */       for (int j = 0; j < nomCount; j++) {
/*  788:1467 */         nomStrings.add(prefix + (j + 1));
/*  789:     */       }
/*  790:1469 */       result = new Attribute(namePrefix + name, nomStrings);
/*  791:1470 */       break;
/*  792:     */     case 0: 
/*  793:1473 */       result = new Attribute(namePrefix + name);
/*  794:1474 */       break;
/*  795:     */     case 2: 
/*  796:1477 */       result = new Attribute(namePrefix + name, (ArrayList)null);
/*  797:1478 */       break;
/*  798:     */     case 3: 
/*  799:1481 */       result = new Attribute(namePrefix + name, "yyyy-mm-dd");
/*  800:1482 */       break;
/*  801:     */     case 4: 
/*  802:     */       Instances rel;
/*  803:     */       Instances rel;
/*  804:1486 */       if (index == -1) {
/*  805:1487 */         rel = getRelationalClassFormat();
/*  806:     */       } else {
/*  807:1489 */         rel = getRelationalFormat(index);
/*  808:     */       }
/*  809:1492 */       if (rel == null)
/*  810:     */       {
/*  811:1493 */         TestInstances dataset = new TestInstances();
/*  812:1494 */         dataset.setNumNominal(getNumRelationalNominal());
/*  813:1495 */         dataset.setNumNominalValues(getNumRelationalNominalValues());
/*  814:1496 */         dataset.setNumNumeric(getNumRelationalNumeric());
/*  815:1497 */         dataset.setNumString(getNumRelationalString());
/*  816:1498 */         dataset.setNumDate(getNumRelationalDate());
/*  817:1499 */         dataset.setNumInstances(0);
/*  818:1500 */         dataset.setClassType(1);
/*  819:     */         
/*  820:     */ 
/*  821:1503 */         rel = new Instances(dataset.generate());
/*  822:1504 */         if (!getNoClass())
/*  823:     */         {
/*  824:1505 */           int clsIndex = rel.classIndex();
/*  825:1506 */           rel.setClassIndex(-1);
/*  826:1507 */           rel.deleteAttributeAt(clsIndex);
/*  827:     */         }
/*  828:     */       }
/*  829:1510 */       result = new Attribute(namePrefix + name, rel);
/*  830:1511 */       break;
/*  831:     */     default: 
/*  832:1514 */       throw new IllegalArgumentException("Attribute type '" + attType + "' unknown!");
/*  833:     */     }
/*  834:1518 */     return result;
/*  835:     */   }
/*  836:     */   
/*  837:     */   protected double generateClassValue(Instances data)
/*  838:     */     throws Exception
/*  839:     */   {
/*  840:1529 */     double result = (0.0D / 0.0D);
/*  841:1531 */     switch (this.m_ClassType)
/*  842:     */     {
/*  843:     */     case 0: 
/*  844:1533 */       result = this.m_Random.nextFloat() * 0.25D + this.m_Random.nextInt(Math.max(2, this.m_NumNominal));
/*  845:1534 */       break;
/*  846:     */     case 1: 
/*  847:1537 */       result = this.m_Random.nextInt(data.numClasses());
/*  848:1538 */       break;
/*  849:     */     case 2: 
/*  850:1541 */       String str = "";
/*  851:1542 */       for (int n = 0; n < this.m_Words.length; n++)
/*  852:     */       {
/*  853:1543 */         if ((n > 0) && (this.m_WordSeparators.length() != 0)) {
/*  854:1544 */           str = str + this.m_WordSeparators.charAt(this.m_Random.nextInt(this.m_WordSeparators.length()));
/*  855:     */         }
/*  856:1547 */         str = str + this.m_Words[this.m_Random.nextInt(this.m_Words.length)];
/*  857:     */       }
/*  858:1549 */       result = data.classAttribute().addStringValue(str);
/*  859:1550 */       break;
/*  860:     */     case 3: 
/*  861:1553 */       result = data.classAttribute().parseDate(2000 + this.m_Random.nextInt(100) + "-01-01");
/*  862:     */       
/*  863:1555 */       break;
/*  864:     */     case 4: 
/*  865:1558 */       if (getRelationalClassFormat() != null)
/*  866:     */       {
/*  867:1559 */         result = data.classAttribute().addRelation(getRelationalClassFormat());
/*  868:     */       }
/*  869:     */       else
/*  870:     */       {
/*  871:1561 */         TestInstances dataset = new TestInstances();
/*  872:1562 */         dataset.setNumNominal(getNumRelationalNominal());
/*  873:1563 */         dataset.setNumNominalValues(getNumRelationalNominalValues());
/*  874:1564 */         dataset.setNumNumeric(getNumRelationalNumeric());
/*  875:1565 */         dataset.setNumString(getNumRelationalString());
/*  876:1566 */         dataset.setNumDate(getNumRelationalDate());
/*  877:1567 */         dataset.setNumInstances(getNumInstancesRelational());
/*  878:1568 */         dataset.setClassType(1);
/*  879:     */         
/*  880:     */ 
/*  881:1571 */         Instances rel = new Instances(dataset.generate());
/*  882:1572 */         int clsIndex = rel.classIndex();
/*  883:1573 */         rel.setClassIndex(-1);
/*  884:1574 */         rel.deleteAttributeAt(clsIndex);
/*  885:1575 */         result = data.classAttribute().addRelation(rel);
/*  886:     */       }
/*  887:     */       break;
/*  888:     */     }
/*  889:1580 */     return result;
/*  890:     */   }
/*  891:     */   
/*  892:     */   protected double generateAttributeValue(Instances data, int index, double classVal)
/*  893:     */     throws Exception
/*  894:     */   {
/*  895:1596 */     double result = (0.0D / 0.0D);
/*  896:1598 */     switch (data.attribute(index).type())
/*  897:     */     {
/*  898:     */     case 0: 
/*  899:1600 */       result = classVal * 4.0D + this.m_Random.nextFloat() * 1.0F - 0.5D;
/*  900:1601 */       break;
/*  901:     */     case 1: 
/*  902:1604 */       if (this.m_Random.nextFloat() < 0.2D) {
/*  903:1605 */         result = this.m_Random.nextInt(data.attribute(index).numValues());
/*  904:     */       } else {
/*  905:1607 */         result = (int)classVal % data.attribute(index).numValues();
/*  906:     */       }
/*  907:1610 */       break;
/*  908:     */     case 2: 
/*  909:1613 */       String str = "";
/*  910:1614 */       for (int n = 0; n < this.m_Words.length; n++)
/*  911:     */       {
/*  912:1615 */         if ((n > 0) && (this.m_WordSeparators.length() != 0)) {
/*  913:1616 */           str = str + this.m_WordSeparators.charAt(this.m_Random.nextInt(this.m_WordSeparators.length()));
/*  914:     */         }
/*  915:1619 */         str = str + this.m_Words[this.m_Random.nextInt(this.m_Words.length)];
/*  916:     */       }
/*  917:1621 */       result = data.attribute(index).addStringValue(str);
/*  918:1622 */       break;
/*  919:     */     case 3: 
/*  920:1625 */       result = data.attribute(index).parseDate(2000 + this.m_Random.nextInt(100) + "-01-01");
/*  921:     */       
/*  922:1627 */       break;
/*  923:     */     case 4: 
/*  924:1630 */       Instances rel = new Instances(data.attribute(index).relation(), 0);
/*  925:1631 */       for (int n = 0; n < getNumInstancesRelational(); n++)
/*  926:     */       {
/*  927:1632 */         Instance inst = new DenseInstance(rel.numAttributes());
/*  928:1633 */         inst.setDataset(data);
/*  929:1634 */         for (int i = 0; i < rel.numAttributes(); i++) {
/*  930:1635 */           inst.setValue(i, generateAttributeValue(rel, i, 0.0D));
/*  931:     */         }
/*  932:1637 */         rel.add(inst);
/*  933:     */       }
/*  934:1639 */       result = data.attribute(index).addRelation(rel);
/*  935:     */     }
/*  936:1643 */     return result;
/*  937:     */   }
/*  938:     */   
/*  939:     */   public Instances generate()
/*  940:     */     throws Exception
/*  941:     */   {
/*  942:1653 */     return generate("");
/*  943:     */   }
/*  944:     */   
/*  945:     */   public Instances generate(String namePrefix)
/*  946:     */     throws Exception
/*  947:     */   {
/*  948:1664 */     if (getMultiInstance())
/*  949:     */     {
/*  950:1665 */       TestInstances bag = (TestInstances)clone();
/*  951:1666 */       bag.setMultiInstance(false);
/*  952:1667 */       bag.setNumInstances(0);
/*  953:1668 */       bag.setSeed(this.m_Random.nextInt());
/*  954:1669 */       Instances bagFormat = bag.generate("bagAtt_");
/*  955:1670 */       bagFormat.setClassIndex(-1);
/*  956:1671 */       bagFormat.deleteAttributeAt(bagFormat.numAttributes() - 1);
/*  957:     */       
/*  958:     */ 
/*  959:1674 */       TestInstances structure = new TestInstances();
/*  960:1675 */       structure.setSeed(this.m_Random.nextInt());
/*  961:1676 */       structure.setNumNominal(1);
/*  962:1677 */       structure.setNumRelational(1);
/*  963:1678 */       structure.setRelationalFormat(0, bagFormat);
/*  964:1679 */       structure.setClassType(getClassType());
/*  965:1680 */       structure.setNumClasses(getNumClasses());
/*  966:1681 */       structure.setRelationalClassFormat(getRelationalClassFormat());
/*  967:1682 */       structure.setNumInstances(getNumInstances());
/*  968:1683 */       this.m_Data = structure.generate();
/*  969:     */       
/*  970:     */ 
/*  971:1686 */       bag.setNumInstances(getNumInstancesRelational());
/*  972:1687 */       for (int i = 0; i < getNumInstances(); i++)
/*  973:     */       {
/*  974:1688 */         bag.setSeed(this.m_Random.nextInt());
/*  975:1689 */         Instances bagData = new Instances(bag.generate("bagAtt_"));
/*  976:1690 */         bagData.setClassIndex(-1);
/*  977:1691 */         bagData.deleteAttributeAt(bagData.numAttributes() - 1);
/*  978:1692 */         double val = this.m_Data.attribute(1).addRelation(bagData);
/*  979:1693 */         this.m_Data.instance(i).setValue(1, val);
/*  980:     */       }
/*  981:     */     }
/*  982:     */     else
/*  983:     */     {
/*  984:1697 */       int clsIndex = this.m_ClassIndex;
/*  985:1698 */       if (clsIndex == -1) {
/*  986:1699 */         clsIndex = getNumAttributes() - 1;
/*  987:     */       }
/*  988:1703 */       ArrayList<Attribute> attributes = new ArrayList(getNumAttributes());
/*  989:1706 */       for (int i = 0; i < getNumNominal(); i++) {
/*  990:1707 */         attributes.add(generateAttribute(i, 1, namePrefix));
/*  991:     */       }
/*  992:1711 */       for (int i = 0; i < getNumNumeric(); i++) {
/*  993:1712 */         attributes.add(generateAttribute(i, 0, namePrefix));
/*  994:     */       }
/*  995:1716 */       for (int i = 0; i < getNumString(); i++) {
/*  996:1717 */         attributes.add(generateAttribute(i, 2, namePrefix));
/*  997:     */       }
/*  998:1721 */       for (int i = 0; i < getNumDate(); i++) {
/*  999:1722 */         attributes.add(generateAttribute(i, 3, namePrefix));
/* 1000:     */       }
/* 1001:1726 */       for (int i = 0; i < getNumRelational(); i++) {
/* 1002:1727 */         attributes.add(generateAttribute(i, 4, namePrefix));
/* 1003:     */       }
/* 1004:1731 */       if (clsIndex != -2) {
/* 1005:1732 */         attributes.add(clsIndex, generateAttribute(-1, getClassType(), namePrefix));
/* 1006:     */       }
/* 1007:1736 */       this.m_Data = new Instances(getRelation(), attributes, getNumInstances());
/* 1008:1737 */       this.m_Data.setClassIndex(clsIndex);
/* 1009:1740 */       for (int i = 0; i < getNumInstances(); i++)
/* 1010:     */       {
/* 1011:1741 */         Instance current = new DenseInstance(getNumAttributes());
/* 1012:1742 */         current.setDataset(this.m_Data);
/* 1013:     */         double classVal;
/* 1014:1746 */         if (clsIndex != -2)
/* 1015:     */         {
/* 1016:1747 */           double classVal = generateClassValue(this.m_Data);
/* 1017:1748 */           current.setClassValue(classVal);
/* 1018:     */         }
/* 1019:     */         else
/* 1020:     */         {
/* 1021:1750 */           classVal = this.m_Random.nextFloat();
/* 1022:     */         }
/* 1023:1753 */         if ((clsIndex != -2) && (this.m_Data.classAttribute().isString())) {
/* 1024:1754 */           classVal += 1.0D;
/* 1025:     */         }
/* 1026:1757 */         for (int n = 0; n < getNumAttributes(); n++) {
/* 1027:1758 */           if (clsIndex != n) {
/* 1028:1762 */             current.setValue(n, generateAttributeValue(this.m_Data, n, classVal));
/* 1029:     */           }
/* 1030:     */         }
/* 1031:1765 */         this.m_Data.add(current);
/* 1032:     */       }
/* 1033:     */     }
/* 1034:1769 */     if (this.m_Data.classIndex() == -2) {
/* 1035:1770 */       this.m_Data.setClassIndex(-1);
/* 1036:     */     }
/* 1037:1773 */     return getData();
/* 1038:     */   }
/* 1039:     */   
/* 1040:     */   public static TestInstances forCapabilities(Capabilities c)
/* 1041:     */   {
/* 1042:1786 */     TestInstances result = new TestInstances();
/* 1043:1789 */     if ((c.getOwner() instanceof MultiInstanceCapabilitiesHandler))
/* 1044:     */     {
/* 1045:1790 */       Capabilities multi = (Capabilities)((MultiInstanceCapabilitiesHandler)c.getOwner()).getMultiInstanceCapabilities().clone();
/* 1046:     */       
/* 1047:1792 */       multi.setOwner(null);
/* 1048:1793 */       result = forCapabilities(multi);
/* 1049:1794 */       result.setMultiInstance(true);
/* 1050:     */     }
/* 1051:     */     else
/* 1052:     */     {
/* 1053:1797 */       if (c.handles(Capabilities.Capability.NO_CLASS)) {
/* 1054:1798 */         result.setClassIndex(-2);
/* 1055:1799 */       } else if (c.handles(Capabilities.Capability.NOMINAL_CLASS)) {
/* 1056:1800 */         result.setClassType(1);
/* 1057:1801 */       } else if (c.handles(Capabilities.Capability.BINARY_CLASS)) {
/* 1058:1802 */         result.setClassType(1);
/* 1059:1803 */       } else if (c.handles(Capabilities.Capability.NUMERIC_CLASS)) {
/* 1060:1804 */         result.setClassType(0);
/* 1061:1805 */       } else if (c.handles(Capabilities.Capability.DATE_CLASS)) {
/* 1062:1806 */         result.setClassType(3);
/* 1063:1807 */       } else if (c.handles(Capabilities.Capability.STRING_CLASS)) {
/* 1064:1808 */         result.setClassType(2);
/* 1065:1809 */       } else if (c.handles(Capabilities.Capability.RELATIONAL_CLASS)) {
/* 1066:1810 */         result.setClassType(4);
/* 1067:     */       }
/* 1068:1814 */       if (c.handles(Capabilities.Capability.UNARY_CLASS)) {
/* 1069:1815 */         result.setNumClasses(1);
/* 1070:     */       }
/* 1071:1817 */       if (c.handles(Capabilities.Capability.BINARY_CLASS)) {
/* 1072:1818 */         result.setNumClasses(2);
/* 1073:     */       }
/* 1074:1820 */       if (c.handles(Capabilities.Capability.NOMINAL_CLASS)) {
/* 1075:1821 */         result.setNumClasses(4);
/* 1076:     */       }
/* 1077:1825 */       if (c.handles(Capabilities.Capability.NOMINAL_ATTRIBUTES))
/* 1078:     */       {
/* 1079:1826 */         result.setNumNominal(1);
/* 1080:1827 */         result.setNumRelationalNominal(1);
/* 1081:     */       }
/* 1082:     */       else
/* 1083:     */       {
/* 1084:1829 */         result.setNumNominal(0);
/* 1085:1830 */         result.setNumRelationalNominal(0);
/* 1086:     */       }
/* 1087:1833 */       if (c.handles(Capabilities.Capability.NUMERIC_ATTRIBUTES))
/* 1088:     */       {
/* 1089:1834 */         result.setNumNumeric(1);
/* 1090:1835 */         result.setNumRelationalNumeric(1);
/* 1091:     */       }
/* 1092:     */       else
/* 1093:     */       {
/* 1094:1837 */         result.setNumNumeric(0);
/* 1095:1838 */         result.setNumRelationalNumeric(0);
/* 1096:     */       }
/* 1097:1841 */       if (c.handles(Capabilities.Capability.DATE_ATTRIBUTES))
/* 1098:     */       {
/* 1099:1842 */         result.setNumDate(1);
/* 1100:1843 */         result.setNumRelationalDate(1);
/* 1101:     */       }
/* 1102:     */       else
/* 1103:     */       {
/* 1104:1845 */         result.setNumDate(0);
/* 1105:1846 */         result.setNumRelationalDate(0);
/* 1106:     */       }
/* 1107:1849 */       if (c.handles(Capabilities.Capability.STRING_ATTRIBUTES))
/* 1108:     */       {
/* 1109:1850 */         result.setNumString(1);
/* 1110:1851 */         result.setNumRelationalString(1);
/* 1111:     */       }
/* 1112:     */       else
/* 1113:     */       {
/* 1114:1853 */         result.setNumString(0);
/* 1115:1854 */         result.setNumRelationalString(0);
/* 1116:     */       }
/* 1117:1857 */       if (c.handles(Capabilities.Capability.RELATIONAL_ATTRIBUTES)) {
/* 1118:1858 */         result.setNumRelational(1);
/* 1119:     */       } else {
/* 1120:1860 */         result.setNumRelational(0);
/* 1121:     */       }
/* 1122:     */     }
/* 1123:1864 */     return result;
/* 1124:     */   }
/* 1125:     */   
/* 1126:     */   public String toString()
/* 1127:     */   {
/* 1128:1876 */     String result = "";
/* 1129:1877 */     result = result + "Relation: " + getRelation() + "\n";
/* 1130:1878 */     result = result + "Seed: " + getSeed() + "\n";
/* 1131:1879 */     result = result + "# Instances: " + getNumInstances() + "\n";
/* 1132:1880 */     result = result + "ClassType: " + getClassType() + "\n";
/* 1133:1881 */     result = result + "# Classes: " + getNumClasses() + "\n";
/* 1134:1882 */     result = result + "Class index: " + getClassIndex() + "\n";
/* 1135:1883 */     result = result + "# Nominal: " + getNumNominal() + "\n";
/* 1136:1884 */     result = result + "# Nominal values: " + getNumNominalValues() + "\n";
/* 1137:1885 */     result = result + "# Numeric: " + getNumNumeric() + "\n";
/* 1138:1886 */     result = result + "# String: " + getNumString() + "\n";
/* 1139:1887 */     result = result + "# Date: " + getNumDate() + "\n";
/* 1140:1888 */     result = result + "# Relational: " + getNumRelational() + "\n";
/* 1141:1889 */     result = result + "  - # Nominal: " + getNumRelationalNominal() + "\n";
/* 1142:1890 */     result = result + "  - # Nominal values: " + getNumRelationalNominalValues() + "\n";
/* 1143:1891 */     result = result + "  - # Numeric: " + getNumRelationalNumeric() + "\n";
/* 1144:1892 */     result = result + "  - # String: " + getNumRelationalString() + "\n";
/* 1145:1893 */     result = result + "  - # Date: " + getNumRelationalDate() + "\n";
/* 1146:1894 */     result = result + "  - # Instances: " + getNumInstancesRelational() + "\n";
/* 1147:1895 */     result = result + "Multi-Instance: " + getMultiInstance() + "\n";
/* 1148:1896 */     result = result + "Words: " + getWords() + "\n";
/* 1149:1897 */     result = result + "Word separators: " + getWordSeparators() + "\n";
/* 1150:     */     
/* 1151:1899 */     return result;
/* 1152:     */   }
/* 1153:     */   
/* 1154:     */   public String getRevision()
/* 1155:     */   {
/* 1156:1909 */     return RevisionUtils.extract("$Revision: 11506 $");
/* 1157:     */   }
/* 1158:     */   
/* 1159:     */   public static void main(String[] args)
/* 1160:     */     throws Exception
/* 1161:     */   {
/* 1162:1921 */     TestInstances inst = new TestInstances();
/* 1163:1924 */     if ((Utils.getFlag("h", args)) || (Utils.getFlag("help", args)))
/* 1164:     */     {
/* 1165:1925 */       StringBuffer result = new StringBuffer();
/* 1166:1926 */       result.append("\nTest data generator options:\n\n");
/* 1167:     */       
/* 1168:1928 */       result.append("-h|-help\n\tprints this help\n");
/* 1169:     */       
/* 1170:1930 */       Enumeration<Option> enm = inst.listOptions();
/* 1171:1931 */       while (enm.hasMoreElements())
/* 1172:     */       {
/* 1173:1932 */         Option option = (Option)enm.nextElement();
/* 1174:1933 */         result.append(option.synopsis() + "\n" + option.description() + "\n");
/* 1175:     */       }
/* 1176:1936 */       System.out.println(result);
/* 1177:1937 */       System.exit(0);
/* 1178:     */     }
/* 1179:1941 */     inst.setOptions(args);
/* 1180:1942 */     System.out.println(inst.generate());
/* 1181:     */   }
/* 1182:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.TestInstances
 * JD-Core Version:    0.7.0.1
 */