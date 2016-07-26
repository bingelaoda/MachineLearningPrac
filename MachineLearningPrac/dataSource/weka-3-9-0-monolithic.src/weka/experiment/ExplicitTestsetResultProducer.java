/*    1:     */ package weka.experiment;
/*    2:     */ 
/*    3:     */ import java.io.File;
/*    4:     */ import java.io.PrintStream;
/*    5:     */ import java.util.Calendar;
/*    6:     */ import java.util.Collections;
/*    7:     */ import java.util.Enumeration;
/*    8:     */ import java.util.Random;
/*    9:     */ import java.util.TimeZone;
/*   10:     */ import java.util.Vector;
/*   11:     */ import weka.core.AdditionalMeasureProducer;
/*   12:     */ import weka.core.Environment;
/*   13:     */ import weka.core.Instances;
/*   14:     */ import weka.core.Option;
/*   15:     */ import weka.core.OptionHandler;
/*   16:     */ import weka.core.RevisionHandler;
/*   17:     */ import weka.core.RevisionUtils;
/*   18:     */ import weka.core.Utils;
/*   19:     */ import weka.core.WekaException;
/*   20:     */ import weka.core.converters.ConverterUtils.DataSource;
/*   21:     */ 
/*   22:     */ public class ExplicitTestsetResultProducer
/*   23:     */   implements ResultProducer, OptionHandler, AdditionalMeasureProducer, RevisionHandler
/*   24:     */ {
/*   25:     */   private static final long serialVersionUID = 2613585409333652530L;
/*   26:     */   public static final String DEFAULT_SUFFIX = "_test.arff";
/*   27:     */   protected Instances m_Instances;
/*   28: 179 */   protected ResultListener m_ResultListener = new CSVResultListener();
/*   29: 182 */   protected File m_TestsetDir = new File(System.getProperty("user.dir"));
/*   30: 185 */   protected String m_TestsetPrefix = "";
/*   31: 188 */   protected String m_TestsetSuffix = "_test.arff";
/*   32: 191 */   protected String m_RelationFind = "";
/*   33: 194 */   protected String m_RelationReplace = "";
/*   34: 197 */   protected boolean m_randomize = false;
/*   35: 200 */   protected SplitEvaluator m_SplitEvaluator = new ClassifierSplitEvaluator();
/*   36: 203 */   protected String[] m_AdditionalMeasures = null;
/*   37: 206 */   protected boolean m_debugOutput = false;
/*   38: 209 */   protected OutputZipper m_ZipDest = null;
/*   39: 212 */   protected File m_OutputFile = new File(new File(System.getProperty("user.dir")), "splitEvalutorOut.zip");
/*   40: 216 */   public static String DATASET_FIELD_NAME = "Dataset";
/*   41: 219 */   public static String RUN_FIELD_NAME = "Run";
/*   42: 222 */   public static String TIMESTAMP_FIELD_NAME = "Date_time";
/*   43:     */   protected transient Environment m_env;
/*   44:     */   
/*   45:     */   public String globalInfo()
/*   46:     */   {
/*   47: 233 */     return "Loads the external test set and calls the appropriate SplitEvaluator to generate some results.\nThe filename of the test set is constructed as follows:\n   <dir> + / + <prefix> + <relation-name> + <suffix>\nThe relation-name can be modified by using the regular expression to replace the matching sub-string with a specified replacement string. In order to get rid of the string that the Weka filters add to the end of the relation name, just use '.*-weka' as the regular expression to find.\nThe suffix determines the type of file to load, i.e., one is not restricted to ARFF files. As long as Weka recognizes the extension specified in the suffix, the data will be loaded with one of Weka's converters.";
/*   48:     */   }
/*   49:     */   
/*   50:     */   public Enumeration<Option> listOptions()
/*   51:     */   {
/*   52: 255 */     Vector<Option> result = new Vector();
/*   53:     */     
/*   54: 257 */     result.addElement(new Option("Save raw split evaluator output.", "D", 0, "-D"));
/*   55:     */     
/*   56:     */ 
/*   57: 260 */     result.addElement(new Option("\tThe filename where raw output will be stored.\n\tIf a directory name is specified then then individual\n\toutputs will be gzipped, otherwise all output will be\n\tzipped to the named file. Use in conjuction with -D.\n\t(default: splitEvalutorOut.zip)", "O", 1, "-O <file/directory name/path>"));
/*   58:     */     
/*   59:     */ 
/*   60:     */ 
/*   61:     */ 
/*   62:     */ 
/*   63:     */ 
/*   64:     */ 
/*   65: 268 */     result.addElement(new Option("\tThe full class name of a SplitEvaluator.\n\teg: weka.experiment.ClassifierSplitEvaluator", "W", 1, "-W <class name>"));
/*   66:     */     
/*   67:     */ 
/*   68:     */ 
/*   69: 272 */     result.addElement(new Option("\tSet when data is to be randomized.", "R", 0, "-R"));
/*   70:     */     
/*   71:     */ 
/*   72: 275 */     result.addElement(new Option("\tThe directory containing the test sets.\n\t(default: current directory)", "dir", 1, "-dir <directory>"));
/*   73:     */     
/*   74:     */ 
/*   75: 278 */     result.addElement(new Option("\tAn optional prefix for the test sets (before the relation name).\n(default: empty string)", "prefix", 1, "-prefix <string>"));
/*   76:     */     
/*   77:     */ 
/*   78:     */ 
/*   79: 282 */     result.addElement(new Option("\tThe suffix to append to the test set.\n\t(default: _test.arff)", "suffix", 1, "-suffix <string>"));
/*   80:     */     
/*   81:     */ 
/*   82:     */ 
/*   83:     */ 
/*   84: 287 */     result.addElement(new Option("\tThe regular expression to search the relation name with.\n\tNot used if an empty string.\n\t(default: empty string)", "find", 1, "-find <regular expression>"));
/*   85:     */     
/*   86:     */ 
/*   87:     */ 
/*   88:     */ 
/*   89: 292 */     result.addElement(new Option("\tThe replacement string for the all the matches of '-find'.\n\t(default: empty string)", "replace", 1, "-replace <string>"));
/*   90: 296 */     if ((this.m_SplitEvaluator != null) && ((this.m_SplitEvaluator instanceof OptionHandler)))
/*   91:     */     {
/*   92: 298 */       result.addElement(new Option("", "", 0, "\nOptions specific to split evaluator " + this.m_SplitEvaluator.getClass().getName() + ":"));
/*   93:     */       
/*   94:     */ 
/*   95: 301 */       result.addAll(Collections.list(((OptionHandler)this.m_SplitEvaluator).listOptions()));
/*   96:     */     }
/*   97: 305 */     return result.elements();
/*   98:     */   }
/*   99:     */   
/*  100:     */   public void setOptions(String[] options)
/*  101:     */     throws Exception
/*  102:     */   {
/*  103: 423 */     setRawOutput(Utils.getFlag('D', options));
/*  104: 424 */     setRandomizeData(!Utils.getFlag('R', options));
/*  105:     */     
/*  106: 426 */     String tmpStr = Utils.getOption('O', options);
/*  107: 427 */     if (tmpStr.length() != 0) {
/*  108: 428 */       setOutputFile(new File(tmpStr));
/*  109:     */     }
/*  110: 431 */     tmpStr = Utils.getOption("dir", options);
/*  111: 432 */     if (tmpStr.length() > 0) {
/*  112: 433 */       setTestsetDir(new File(tmpStr));
/*  113:     */     } else {
/*  114: 435 */       setTestsetDir(new File(System.getProperty("user.dir")));
/*  115:     */     }
/*  116: 438 */     tmpStr = Utils.getOption("prefix", options);
/*  117: 439 */     if (tmpStr.length() > 0) {
/*  118: 440 */       setTestsetPrefix(tmpStr);
/*  119:     */     } else {
/*  120: 442 */       setTestsetPrefix("");
/*  121:     */     }
/*  122: 445 */     tmpStr = Utils.getOption("suffix", options);
/*  123: 446 */     if (tmpStr.length() > 0) {
/*  124: 447 */       setTestsetSuffix(tmpStr);
/*  125:     */     } else {
/*  126: 449 */       setTestsetSuffix("_test.arff");
/*  127:     */     }
/*  128: 452 */     tmpStr = Utils.getOption("find", options);
/*  129: 453 */     if (tmpStr.length() > 0) {
/*  130: 454 */       setRelationFind(tmpStr);
/*  131:     */     } else {
/*  132: 456 */       setRelationFind("");
/*  133:     */     }
/*  134: 459 */     tmpStr = Utils.getOption("replace", options);
/*  135: 460 */     if ((tmpStr.length() > 0) && (getRelationFind().length() > 0)) {
/*  136: 461 */       setRelationReplace(tmpStr);
/*  137:     */     } else {
/*  138: 463 */       setRelationReplace("");
/*  139:     */     }
/*  140: 466 */     tmpStr = Utils.getOption('W', options);
/*  141: 467 */     if (tmpStr.length() == 0) {
/*  142: 468 */       throw new Exception("A SplitEvaluator must be specified with the -W option.");
/*  143:     */     }
/*  144: 475 */     setSplitEvaluator((SplitEvaluator)Utils.forName(SplitEvaluator.class, tmpStr, null));
/*  145: 477 */     if ((getSplitEvaluator() instanceof OptionHandler)) {
/*  146: 478 */       ((OptionHandler)getSplitEvaluator()).setOptions(Utils.partitionOptions(options));
/*  147:     */     }
/*  148:     */   }
/*  149:     */   
/*  150:     */   public String[] getOptions()
/*  151:     */   {
/*  152: 494 */     Vector<String> result = new Vector();
/*  153:     */     
/*  154: 496 */     String[] seOptions = new String[0];
/*  155: 497 */     if ((this.m_SplitEvaluator != null) && ((this.m_SplitEvaluator instanceof OptionHandler))) {
/*  156: 499 */       seOptions = ((OptionHandler)this.m_SplitEvaluator).getOptions();
/*  157:     */     }
/*  158: 502 */     if (getRawOutput()) {
/*  159: 503 */       result.add("-D");
/*  160:     */     }
/*  161: 506 */     if (!getRandomizeData()) {
/*  162: 507 */       result.add("-R");
/*  163:     */     }
/*  164: 510 */     result.add("-O");
/*  165: 511 */     result.add(getOutputFile().getName());
/*  166:     */     
/*  167: 513 */     result.add("-dir");
/*  168: 514 */     result.add(getTestsetDir().getPath());
/*  169: 516 */     if (getTestsetPrefix().length() > 0)
/*  170:     */     {
/*  171: 517 */       result.add("-prefix");
/*  172: 518 */       result.add(getTestsetPrefix());
/*  173:     */     }
/*  174: 521 */     result.add("-suffix");
/*  175: 522 */     result.add(getTestsetSuffix());
/*  176: 524 */     if (getRelationFind().length() > 0)
/*  177:     */     {
/*  178: 525 */       result.add("-find");
/*  179: 526 */       result.add(getRelationFind());
/*  180: 528 */       if (getRelationReplace().length() > 0)
/*  181:     */       {
/*  182: 529 */         result.add("-replace");
/*  183: 530 */         result.add(getRelationReplace());
/*  184:     */       }
/*  185:     */     }
/*  186: 534 */     if (getSplitEvaluator() != null)
/*  187:     */     {
/*  188: 535 */       result.add("-W");
/*  189: 536 */       result.add(getSplitEvaluator().getClass().getName());
/*  190:     */     }
/*  191: 539 */     if (seOptions.length > 0)
/*  192:     */     {
/*  193: 540 */       result.add("--");
/*  194: 541 */       for (int i = 0; i < seOptions.length; i++) {
/*  195: 542 */         result.add(seOptions[i]);
/*  196:     */       }
/*  197:     */     }
/*  198: 546 */     return (String[])result.toArray(new String[result.size()]);
/*  199:     */   }
/*  200:     */   
/*  201:     */   public void setInstances(Instances instances)
/*  202:     */   {
/*  203: 556 */     this.m_Instances = instances;
/*  204:     */   }
/*  205:     */   
/*  206:     */   public void setAdditionalMeasures(String[] additionalMeasures)
/*  207:     */   {
/*  208: 569 */     this.m_AdditionalMeasures = additionalMeasures;
/*  209: 571 */     if (this.m_SplitEvaluator != null)
/*  210:     */     {
/*  211: 572 */       System.err.println("ExplicitTestsetResultProducer: setting additional measures for split evaluator");
/*  212:     */       
/*  213: 574 */       this.m_SplitEvaluator.setAdditionalMeasures(this.m_AdditionalMeasures);
/*  214:     */     }
/*  215:     */   }
/*  216:     */   
/*  217:     */   public Enumeration<String> enumerateMeasures()
/*  218:     */   {
/*  219: 586 */     Vector<String> result = new Vector();
/*  220: 587 */     if ((this.m_SplitEvaluator instanceof AdditionalMeasureProducer))
/*  221:     */     {
/*  222: 588 */       Enumeration<String> en = ((AdditionalMeasureProducer)this.m_SplitEvaluator).enumerateMeasures();
/*  223: 590 */       while (en.hasMoreElements())
/*  224:     */       {
/*  225: 591 */         String mname = (String)en.nextElement();
/*  226: 592 */         result.add(mname);
/*  227:     */       }
/*  228:     */     }
/*  229: 595 */     return result.elements();
/*  230:     */   }
/*  231:     */   
/*  232:     */   public double getMeasure(String additionalMeasureName)
/*  233:     */   {
/*  234: 607 */     if ((this.m_SplitEvaluator instanceof AdditionalMeasureProducer)) {
/*  235: 608 */       return ((AdditionalMeasureProducer)this.m_SplitEvaluator).getMeasure(additionalMeasureName);
/*  236:     */     }
/*  237: 611 */     throw new IllegalArgumentException("ExplicitTestsetResultProducer: Can't return value for : " + additionalMeasureName + ". " + this.m_SplitEvaluator.getClass().getName() + " " + "is not an AdditionalMeasureProducer");
/*  238:     */   }
/*  239:     */   
/*  240:     */   public void setResultListener(ResultListener listener)
/*  241:     */   {
/*  242: 625 */     this.m_ResultListener = listener;
/*  243:     */   }
/*  244:     */   
/*  245:     */   public static Double getTimestamp()
/*  246:     */   {
/*  247: 635 */     Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
/*  248: 636 */     double timestamp = now.get(1) * 10000 + (now.get(2) + 1) * 100 + now.get(5) + now.get(11) / 100.0D + now.get(12) / 10000.0D;
/*  249:     */     
/*  250:     */ 
/*  251:     */ 
/*  252: 640 */     return new Double(timestamp);
/*  253:     */   }
/*  254:     */   
/*  255:     */   public void preProcess()
/*  256:     */     throws Exception
/*  257:     */   {
/*  258: 650 */     if (this.m_SplitEvaluator == null) {
/*  259: 651 */       throw new Exception("No SplitEvalutor set");
/*  260:     */     }
/*  261: 654 */     if (this.m_ResultListener == null) {
/*  262: 655 */       throw new Exception("No ResultListener set");
/*  263:     */     }
/*  264: 658 */     this.m_ResultListener.preProcess(this);
/*  265:     */   }
/*  266:     */   
/*  267:     */   public void postProcess()
/*  268:     */     throws Exception
/*  269:     */   {
/*  270: 670 */     this.m_ResultListener.postProcess(this);
/*  271: 671 */     if ((this.m_debugOutput) && 
/*  272: 672 */       (this.m_ZipDest != null))
/*  273:     */     {
/*  274: 673 */       this.m_ZipDest.finished();
/*  275: 674 */       this.m_ZipDest = null;
/*  276:     */     }
/*  277:     */   }
/*  278:     */   
/*  279:     */   public void doRunKeys(int run)
/*  280:     */     throws Exception
/*  281:     */   {
/*  282: 689 */     if (this.m_Instances == null) {
/*  283: 690 */       throw new Exception("No Instances set");
/*  284:     */     }
/*  285: 694 */     Object[] seKey = this.m_SplitEvaluator.getKey();
/*  286: 695 */     Object[] key = new Object[seKey.length + 2];
/*  287: 696 */     key[0] = Utils.backQuoteChars(this.m_Instances.relationName());
/*  288: 697 */     key[1] = ("" + run);
/*  289: 698 */     System.arraycopy(seKey, 0, key, 2, seKey.length);
/*  290: 699 */     if (this.m_ResultListener.isResultRequired(this, key)) {
/*  291:     */       try
/*  292:     */       {
/*  293: 701 */         this.m_ResultListener.acceptResult(this, key, null);
/*  294:     */       }
/*  295:     */       catch (Exception ex)
/*  296:     */       {
/*  297: 704 */         throw ex;
/*  298:     */       }
/*  299:     */     }
/*  300:     */   }
/*  301:     */   
/*  302:     */   protected String createFilename(Instances inst)
/*  303:     */   {
/*  304: 719 */     String name = inst.relationName();
/*  305: 720 */     if (getRelationFind().length() > 0) {
/*  306: 721 */       name = name.replaceAll(getRelationFind(), getRelationReplace());
/*  307:     */     }
/*  308: 724 */     String result = getTestsetDir().getPath() + File.separator;
/*  309: 725 */     result = result + getTestsetPrefix() + name + getTestsetSuffix();
/*  310:     */     try
/*  311:     */     {
/*  312: 730 */       result = this.m_env.substitute(result);
/*  313:     */     }
/*  314:     */     catch (Exception ex) {}
/*  315: 734 */     return result;
/*  316:     */   }
/*  317:     */   
/*  318:     */   public void doRun(int run)
/*  319:     */     throws Exception
/*  320:     */   {
/*  321: 747 */     if ((getRawOutput()) && 
/*  322: 748 */       (this.m_ZipDest == null)) {
/*  323: 749 */       this.m_ZipDest = new OutputZipper(this.m_OutputFile);
/*  324:     */     }
/*  325: 753 */     if (this.m_Instances == null) {
/*  326: 754 */       throw new Exception("No Instances set");
/*  327:     */     }
/*  328: 758 */     Object[] seKey = this.m_SplitEvaluator.getKey();
/*  329: 759 */     Object[] key = new Object[seKey.length + 2];
/*  330: 760 */     key[0] = Utils.backQuoteChars(this.m_Instances.relationName());
/*  331: 761 */     key[1] = ("" + run);
/*  332: 762 */     System.arraycopy(seKey, 0, key, 2, seKey.length);
/*  333: 763 */     if (this.m_ResultListener.isResultRequired(this, key))
/*  334:     */     {
/*  335: 765 */       Instances train = new Instances(this.m_Instances);
/*  336: 766 */       if (this.m_randomize)
/*  337:     */       {
/*  338: 767 */         Random rand = new Random(run);
/*  339: 768 */         train.randomize(rand);
/*  340:     */       }
/*  341: 771 */       if (this.m_env == null) {
/*  342: 772 */         this.m_env = new Environment();
/*  343:     */       }
/*  344: 774 */       this.m_env.addVariable("RUN_NUMBER", "" + run);
/*  345:     */       
/*  346:     */ 
/*  347: 777 */       String filename = createFilename(train);
/*  348: 778 */       File file = new File(filename);
/*  349: 779 */       if (!file.exists()) {
/*  350: 780 */         throw new WekaException("Test set '" + filename + "' not found!");
/*  351:     */       }
/*  352: 782 */       Instances test = ConverterUtils.DataSource.read(filename);
/*  353: 784 */       if (train.numAttributes() == test.numAttributes()) {
/*  354: 785 */         test.setClassIndex(train.classIndex());
/*  355:     */       } else {
/*  356: 787 */         throw new WekaException("Train and test set (= " + filename + ") " + "differ in number of attributes: " + train.numAttributes() + " != " + test.numAttributes());
/*  357:     */       }
/*  358: 792 */       if (!train.equalHeaders(test)) {
/*  359: 793 */         throw new WekaException("Train and test set (= " + filename + ") " + "are not compatible:\n" + train.equalHeadersMsg(test));
/*  360:     */       }
/*  361:     */       try
/*  362:     */       {
/*  363: 798 */         Object[] seResults = this.m_SplitEvaluator.getResult(train, test);
/*  364: 799 */         Object[] results = new Object[seResults.length + 1];
/*  365: 800 */         results[0] = getTimestamp();
/*  366: 801 */         System.arraycopy(seResults, 0, results, 1, seResults.length);
/*  367: 802 */         if (this.m_debugOutput)
/*  368:     */         {
/*  369: 803 */           String resultName = ("" + run + "." + Utils.backQuoteChars(train.relationName()) + "." + this.m_SplitEvaluator.toString()).replace(' ', '_');
/*  370:     */           
/*  371:     */ 
/*  372: 806 */           resultName = Utils.removeSubstring(resultName, "weka.classifiers.");
/*  373: 807 */           resultName = Utils.removeSubstring(resultName, "weka.filters.");
/*  374: 808 */           resultName = Utils.removeSubstring(resultName, "weka.attributeSelection.");
/*  375:     */           
/*  376: 810 */           this.m_ZipDest.zipit(this.m_SplitEvaluator.getRawResultOutput(), resultName);
/*  377:     */         }
/*  378: 812 */         this.m_ResultListener.acceptResult(this, key, results);
/*  379:     */       }
/*  380:     */       catch (Exception e)
/*  381:     */       {
/*  382: 815 */         throw e;
/*  383:     */       }
/*  384:     */     }
/*  385:     */   }
/*  386:     */   
/*  387:     */   public String[] getKeyNames()
/*  388:     */   {
/*  389: 828 */     String[] keyNames = this.m_SplitEvaluator.getKeyNames();
/*  390:     */     
/*  391: 830 */     String[] newKeyNames = new String[keyNames.length + 2];
/*  392: 831 */     newKeyNames[0] = DATASET_FIELD_NAME;
/*  393: 832 */     newKeyNames[1] = RUN_FIELD_NAME;
/*  394: 833 */     System.arraycopy(keyNames, 0, newKeyNames, 2, keyNames.length);
/*  395: 834 */     return newKeyNames;
/*  396:     */   }
/*  397:     */   
/*  398:     */   public Object[] getKeyTypes()
/*  399:     */   {
/*  400: 846 */     Object[] keyTypes = this.m_SplitEvaluator.getKeyTypes();
/*  401:     */     
/*  402: 848 */     Object[] newKeyTypes = new String[keyTypes.length + 2];
/*  403: 849 */     newKeyTypes[0] = new String();
/*  404: 850 */     newKeyTypes[1] = new String();
/*  405: 851 */     System.arraycopy(keyTypes, 0, newKeyTypes, 2, keyTypes.length);
/*  406: 852 */     return newKeyTypes;
/*  407:     */   }
/*  408:     */   
/*  409:     */   public String[] getResultNames()
/*  410:     */   {
/*  411: 863 */     String[] resultNames = this.m_SplitEvaluator.getResultNames();
/*  412:     */     
/*  413: 865 */     String[] newResultNames = new String[resultNames.length + 1];
/*  414: 866 */     newResultNames[0] = TIMESTAMP_FIELD_NAME;
/*  415: 867 */     System.arraycopy(resultNames, 0, newResultNames, 1, resultNames.length);
/*  416: 868 */     return newResultNames;
/*  417:     */   }
/*  418:     */   
/*  419:     */   public Object[] getResultTypes()
/*  420:     */   {
/*  421: 880 */     Object[] resultTypes = this.m_SplitEvaluator.getResultTypes();
/*  422:     */     
/*  423: 882 */     Object[] newResultTypes = new Object[resultTypes.length + 1];
/*  424: 883 */     newResultTypes[0] = new Double(0.0D);
/*  425: 884 */     System.arraycopy(resultTypes, 0, newResultTypes, 1, resultTypes.length);
/*  426: 885 */     return newResultTypes;
/*  427:     */   }
/*  428:     */   
/*  429:     */   public String getCompatibilityState()
/*  430:     */   {
/*  431: 905 */     String result = "";
/*  432: 906 */     if (getRandomizeData()) {
/*  433: 907 */       result = result + " -R";
/*  434:     */     }
/*  435: 910 */     result = result + " -dir " + getTestsetDir();
/*  436: 912 */     if (getTestsetPrefix().length() > 0) {
/*  437: 913 */       result = result + " -prefix " + getTestsetPrefix();
/*  438:     */     }
/*  439: 916 */     result = result + " -suffix " + getTestsetSuffix();
/*  440: 918 */     if (getRelationFind().length() > 0)
/*  441:     */     {
/*  442: 919 */       result = result + " -find " + getRelationFind();
/*  443: 921 */       if (getRelationReplace().length() > 0) {
/*  444: 922 */         result = result + " -replace " + getRelationReplace();
/*  445:     */       }
/*  446:     */     }
/*  447: 926 */     if (this.m_SplitEvaluator == null) {
/*  448: 927 */       result = result + " <null SplitEvaluator>";
/*  449:     */     } else {
/*  450: 929 */       result = result + " -W " + this.m_SplitEvaluator.getClass().getName();
/*  451:     */     }
/*  452: 932 */     return result + " --";
/*  453:     */   }
/*  454:     */   
/*  455:     */   public String outputFileTipText()
/*  456:     */   {
/*  457: 942 */     return "Set the destination for saving raw output. If the rawOutput option is selected, then output from the splitEvaluator for individual train-test splits is saved. If the destination is a directory, then each output is saved to an individual gzip file; if the destination is a file, then each output is saved as an entry in a zip file.";
/*  458:     */   }
/*  459:     */   
/*  460:     */   public File getOutputFile()
/*  461:     */   {
/*  462: 957 */     return this.m_OutputFile;
/*  463:     */   }
/*  464:     */   
/*  465:     */   public void setOutputFile(File value)
/*  466:     */   {
/*  467: 966 */     this.m_OutputFile = value;
/*  468:     */   }
/*  469:     */   
/*  470:     */   public String randomizeDataTipText()
/*  471:     */   {
/*  472: 976 */     return "Do not randomize dataset and do not perform probabilistic rounding if true";
/*  473:     */   }
/*  474:     */   
/*  475:     */   public boolean getRandomizeData()
/*  476:     */   {
/*  477: 986 */     return this.m_randomize;
/*  478:     */   }
/*  479:     */   
/*  480:     */   public void setRandomizeData(boolean value)
/*  481:     */   {
/*  482: 995 */     this.m_randomize = value;
/*  483:     */   }
/*  484:     */   
/*  485:     */   public String rawOutputTipText()
/*  486:     */   {
/*  487:1005 */     return "Save raw output (useful for debugging). If set, then output is sent to the destination specified by outputFile";
/*  488:     */   }
/*  489:     */   
/*  490:     */   public boolean getRawOutput()
/*  491:     */   {
/*  492:1015 */     return this.m_debugOutput;
/*  493:     */   }
/*  494:     */   
/*  495:     */   public void setRawOutput(boolean value)
/*  496:     */   {
/*  497:1024 */     this.m_debugOutput = value;
/*  498:     */   }
/*  499:     */   
/*  500:     */   public String splitEvaluatorTipText()
/*  501:     */   {
/*  502:1034 */     return "The evaluator to apply to the test data. This may be a classifier, regression scheme etc.";
/*  503:     */   }
/*  504:     */   
/*  505:     */   public SplitEvaluator getSplitEvaluator()
/*  506:     */   {
/*  507:1044 */     return this.m_SplitEvaluator;
/*  508:     */   }
/*  509:     */   
/*  510:     */   public void setSplitEvaluator(SplitEvaluator value)
/*  511:     */   {
/*  512:1053 */     this.m_SplitEvaluator = value;
/*  513:1054 */     this.m_SplitEvaluator.setAdditionalMeasures(this.m_AdditionalMeasures);
/*  514:     */   }
/*  515:     */   
/*  516:     */   public String testsetDirTipText()
/*  517:     */   {
/*  518:1064 */     return "The directory containing the test sets.";
/*  519:     */   }
/*  520:     */   
/*  521:     */   public File getTestsetDir()
/*  522:     */   {
/*  523:1073 */     return this.m_TestsetDir;
/*  524:     */   }
/*  525:     */   
/*  526:     */   public void setTestsetDir(File value)
/*  527:     */   {
/*  528:1082 */     this.m_TestsetDir = value;
/*  529:     */   }
/*  530:     */   
/*  531:     */   public String testsetPrefixTipText()
/*  532:     */   {
/*  533:1092 */     return "The prefix to use for the filename of the test sets.";
/*  534:     */   }
/*  535:     */   
/*  536:     */   public String getTestsetPrefix()
/*  537:     */   {
/*  538:1101 */     return this.m_TestsetPrefix;
/*  539:     */   }
/*  540:     */   
/*  541:     */   public void setTestsetPrefix(String value)
/*  542:     */   {
/*  543:1110 */     this.m_TestsetPrefix = value;
/*  544:     */   }
/*  545:     */   
/*  546:     */   public String testsetSuffixTipText()
/*  547:     */   {
/*  548:1120 */     return "The suffix to use for the filename of the test sets - must contain the file extension.";
/*  549:     */   }
/*  550:     */   
/*  551:     */   public String getTestsetSuffix()
/*  552:     */   {
/*  553:1130 */     return this.m_TestsetSuffix;
/*  554:     */   }
/*  555:     */   
/*  556:     */   public void setTestsetSuffix(String value)
/*  557:     */   {
/*  558:1139 */     if ((value == null) || (value.length() == 0)) {
/*  559:1140 */       value = "_test.arff";
/*  560:     */     }
/*  561:1142 */     this.m_TestsetSuffix = value;
/*  562:     */   }
/*  563:     */   
/*  564:     */   public String relationFindTipText()
/*  565:     */   {
/*  566:1152 */     return "The regular expression to use for removing parts of the relation name, ignored if empty.";
/*  567:     */   }
/*  568:     */   
/*  569:     */   public String getRelationFind()
/*  570:     */   {
/*  571:1162 */     return this.m_RelationFind;
/*  572:     */   }
/*  573:     */   
/*  574:     */   public void setRelationFind(String value)
/*  575:     */   {
/*  576:1171 */     this.m_RelationFind = value;
/*  577:     */   }
/*  578:     */   
/*  579:     */   public String relationReplaceTipText()
/*  580:     */   {
/*  581:1181 */     return "The string to replace all matches of the regular expression with.";
/*  582:     */   }
/*  583:     */   
/*  584:     */   public String getRelationReplace()
/*  585:     */   {
/*  586:1190 */     return this.m_RelationReplace;
/*  587:     */   }
/*  588:     */   
/*  589:     */   public void setRelationReplace(String value)
/*  590:     */   {
/*  591:1199 */     this.m_RelationReplace = value;
/*  592:     */   }
/*  593:     */   
/*  594:     */   public String toString()
/*  595:     */   {
/*  596:1209 */     String result = "ExplicitTestsetResultProducer: ";
/*  597:1210 */     result = result + getCompatibilityState();
/*  598:1211 */     if (this.m_Instances == null) {
/*  599:1212 */       result = result + ": <null Instances>";
/*  600:     */     } else {
/*  601:1214 */       result = result + ": " + Utils.backQuoteChars(this.m_Instances.relationName());
/*  602:     */     }
/*  603:1216 */     return result;
/*  604:     */   }
/*  605:     */   
/*  606:     */   public String getRevision()
/*  607:     */   {
/*  608:1226 */     return RevisionUtils.extract("$Revision: 10203 $");
/*  609:     */   }
/*  610:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.ExplicitTestsetResultProducer
 * JD-Core Version:    0.7.0.1
 */