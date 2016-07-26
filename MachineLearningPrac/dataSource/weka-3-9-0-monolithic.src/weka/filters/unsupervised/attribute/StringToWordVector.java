/*    1:     */ package weka.filters.unsupervised.attribute;
/*    2:     */ 
/*    3:     */ import java.io.File;
/*    4:     */ import java.util.Enumeration;
/*    5:     */ import java.util.Vector;
/*    6:     */ import weka.core.Capabilities;
/*    7:     */ import weka.core.Capabilities.Capability;
/*    8:     */ import weka.core.DictionaryBuilder;
/*    9:     */ import weka.core.Instance;
/*   10:     */ import weka.core.Instances;
/*   11:     */ import weka.core.Option;
/*   12:     */ import weka.core.OptionHandler;
/*   13:     */ import weka.core.Range;
/*   14:     */ import weka.core.RevisionUtils;
/*   15:     */ import weka.core.SelectedTag;
/*   16:     */ import weka.core.Tag;
/*   17:     */ import weka.core.Utils;
/*   18:     */ import weka.core.stemmers.NullStemmer;
/*   19:     */ import weka.core.stemmers.Stemmer;
/*   20:     */ import weka.core.stopwords.Null;
/*   21:     */ import weka.core.stopwords.StopwordsHandler;
/*   22:     */ import weka.core.tokenizers.Tokenizer;
/*   23:     */ import weka.core.tokenizers.WordTokenizer;
/*   24:     */ import weka.filters.Filter;
/*   25:     */ import weka.filters.UnsupervisedFilter;
/*   26:     */ 
/*   27:     */ public class StringToWordVector
/*   28:     */   extends Filter
/*   29:     */   implements UnsupervisedFilter, OptionHandler
/*   30:     */ {
/*   31: 149 */   protected DictionaryBuilder m_dictionaryBuilder = new DictionaryBuilder();
/*   32:     */   static final long serialVersionUID = 8249106275278565424L;
/*   33: 157 */   private double m_PeriodicPruningRate = -1.0D;
/*   34: 160 */   protected int m_filterType = 0;
/*   35:     */   public static final int FILTER_NONE = 0;
/*   36:     */   public static final int FILTER_NORMALIZE_ALL = 1;
/*   37:     */   public static final int FILTER_NORMALIZE_TEST_ONLY = 2;
/*   38: 174 */   public static final Tag[] TAGS_FILTER = { new Tag(0, "No normalization"), new Tag(1, "Normalize all data"), new Tag(2, "Normalize test data only") };
/*   39: 180 */   protected File m_dictionaryFile = new File("-- set me --");
/*   40:     */   protected boolean m_dictionaryIsBinary;
/*   41:     */   
/*   42:     */   public StringToWordVector() {}
/*   43:     */   
/*   44:     */   public Enumeration<Option> listOptions()
/*   45:     */   {
/*   46: 203 */     Vector<Option> result = new Vector();
/*   47:     */     
/*   48: 205 */     result.addElement(new Option("\tOutput word counts rather than boolean word presence.\n", "C", 0, "-C"));
/*   49:     */     
/*   50:     */ 
/*   51:     */ 
/*   52:     */ 
/*   53: 210 */     result.addElement(new Option("\tSpecify list of string attributes to convert to words (as weka Range).\n\t(default: select all string attributes)", "R", 1, "-R <index1,index2-index4,...>"));
/*   54:     */     
/*   55:     */ 
/*   56:     */ 
/*   57:     */ 
/*   58: 215 */     result.addElement(new Option("\tInvert matching sense of column indexes.", "V", 0, "-V"));
/*   59:     */     
/*   60:     */ 
/*   61: 218 */     result.addElement(new Option("\tSpecify a prefix for the created attribute names.\n\t(default: \"\")", "P", 1, "-P <attribute name prefix>"));
/*   62:     */     
/*   63:     */ 
/*   64:     */ 
/*   65: 222 */     result.addElement(new Option("\tSpecify approximate number of word fields to create.\n\tSurplus words will be discarded..\n\t(default: 1000)", "W", 1, "-W <number of words to keep>"));
/*   66:     */     
/*   67:     */ 
/*   68:     */ 
/*   69:     */ 
/*   70: 227 */     result.addElement(new Option("\tSpecify the rate (e.g., every 10% of the input dataset) at which to periodically prune the dictionary.\n\t-W prunes after creating a full dictionary. You may not have enough memory for this approach.\n\t(default: no periodic pruning)", "prune-rate", 1, "-prune-rate <rate as a percentage of dataset>"));
/*   71:     */     
/*   72:     */ 
/*   73:     */ 
/*   74:     */ 
/*   75:     */ 
/*   76:     */ 
/*   77: 234 */     result.addElement(new Option("\tTransform the word frequencies into log(1+fij)\n\twhere fij is the frequency of word i in jth document(instance).\n", "T", 0, "-T"));
/*   78:     */     
/*   79:     */ 
/*   80:     */ 
/*   81:     */ 
/*   82:     */ 
/*   83: 240 */     result.addElement(new Option("\tTransform each word frequency into:\n\tfij*log(num of Documents/num of documents containing word i)\n\t  where fij if frequency of word i in jth document(instance)", "I", 0, "-I"));
/*   84:     */     
/*   85:     */ 
/*   86:     */ 
/*   87:     */ 
/*   88: 245 */     result.addElement(new Option("\tWhether to 0=not normalize/1=normalize all data/2=normalize test data only\n\tto average length of training documents (default 0=don't normalize).", "N", 1, "-N"));
/*   89:     */     
/*   90:     */ 
/*   91:     */ 
/*   92:     */ 
/*   93:     */ 
/*   94: 251 */     result.addElement(new Option("\tConvert all tokens to lowercase before adding to the dictionary.", "L", 0, "-L"));
/*   95:     */     
/*   96:     */ 
/*   97: 254 */     result.addElement(new Option("\tThe stopwords handler to use (default Null).", "-stopwords-handler", 1, "-stopwords-handler"));
/*   98:     */     
/*   99:     */ 
/*  100: 257 */     result.addElement(new Option("\tThe stemming algorithm (classname plus parameters) to use.", "stemmer", 1, "-stemmer <spec>"));
/*  101:     */     
/*  102:     */ 
/*  103:     */ 
/*  104: 261 */     result.addElement(new Option("\tThe minimum term frequency (default = 1).", "M", 1, "-M <int>"));
/*  105:     */     
/*  106:     */ 
/*  107: 264 */     result.addElement(new Option("\tIf this is set, the maximum number of words and the \n\tminimum term frequency is not enforced on a per-class \n\tbasis but based on the documents in all the classes \n\t(even if a class attribute is set).", "O", 0, "-O"));
/*  108:     */     
/*  109:     */ 
/*  110:     */ 
/*  111:     */ 
/*  112:     */ 
/*  113: 270 */     result.addElement(new Option("\tThe tokenizing algorithm (classname plus parameters) to use.\n\t(default: " + WordTokenizer.class.getName() + ")", "tokenizer", 1, "-tokenizer <spec>"));
/*  114:     */     
/*  115:     */ 
/*  116:     */ 
/*  117:     */ 
/*  118: 275 */     result.addElement(new Option("\tThe file to save the dictionary to.\n\t(default is not to save the dictionary)", "dictionary", 1, "-dictionary <path to save to>"));
/*  119:     */     
/*  120:     */ 
/*  121:     */ 
/*  122: 279 */     result.addElement(new Option("\tSave the dictionary file as a binary serialized object\n\tinstead of in plain text form. Use in conjunction with\n\t-dictionary", "binary-dict", 0, "-binary-dict"));
/*  123:     */     
/*  124:     */ 
/*  125:     */ 
/*  126: 283 */     return result.elements();
/*  127:     */   }
/*  128:     */   
/*  129:     */   public void setOptions(String[] options)
/*  130:     */     throws Exception
/*  131:     */   {
/*  132: 371 */     String value = Utils.getOption('R', options);
/*  133: 372 */     if (value.length() != 0) {
/*  134: 373 */       setSelectedRange(value);
/*  135:     */     } else {
/*  136: 375 */       setSelectedRange("first-last");
/*  137:     */     }
/*  138: 378 */     setInvertSelection(Utils.getFlag('V', options));
/*  139:     */     
/*  140: 380 */     value = Utils.getOption('P', options);
/*  141: 381 */     if (value.length() != 0) {
/*  142: 382 */       setAttributeNamePrefix(value);
/*  143:     */     } else {
/*  144: 384 */       setAttributeNamePrefix("");
/*  145:     */     }
/*  146: 387 */     value = Utils.getOption('W', options);
/*  147: 388 */     if (value.length() != 0) {
/*  148: 389 */       setWordsToKeep(Integer.valueOf(value).intValue());
/*  149:     */     } else {
/*  150: 391 */       setWordsToKeep(1000);
/*  151:     */     }
/*  152: 394 */     value = Utils.getOption("prune-rate", options);
/*  153: 395 */     if (value.length() > 0) {
/*  154: 396 */       setPeriodicPruning(Double.parseDouble(value));
/*  155:     */     } else {
/*  156: 398 */       setPeriodicPruning(-1.0D);
/*  157:     */     }
/*  158: 401 */     value = Utils.getOption('M', options);
/*  159: 402 */     if (value.length() != 0) {
/*  160: 403 */       setMinTermFreq(Integer.valueOf(value).intValue());
/*  161:     */     } else {
/*  162: 405 */       setMinTermFreq(1);
/*  163:     */     }
/*  164: 408 */     setOutputWordCounts(Utils.getFlag('C', options));
/*  165:     */     
/*  166: 410 */     setTFTransform(Utils.getFlag('T', options));
/*  167:     */     
/*  168: 412 */     setIDFTransform(Utils.getFlag('I', options));
/*  169:     */     
/*  170: 414 */     setDoNotOperateOnPerClassBasis(Utils.getFlag('O', options));
/*  171:     */     
/*  172: 416 */     String nString = Utils.getOption('N', options);
/*  173: 417 */     if (nString.length() != 0) {
/*  174: 418 */       setNormalizeDocLength(new SelectedTag(Integer.parseInt(nString), TAGS_FILTER));
/*  175:     */     } else {
/*  176: 421 */       setNormalizeDocLength(new SelectedTag(0, TAGS_FILTER));
/*  177:     */     }
/*  178: 424 */     setLowerCaseTokens(Utils.getFlag('L', options));
/*  179:     */     
/*  180: 426 */     String stemmerString = Utils.getOption("stemmer", options);
/*  181: 427 */     if (stemmerString.length() == 0)
/*  182:     */     {
/*  183: 428 */       setStemmer(null);
/*  184:     */     }
/*  185:     */     else
/*  186:     */     {
/*  187: 430 */       String[] stemmerSpec = Utils.splitOptions(stemmerString);
/*  188: 431 */       if (stemmerSpec.length == 0) {
/*  189: 432 */         throw new Exception("Invalid stemmer specification string");
/*  190:     */       }
/*  191: 434 */       String stemmerName = stemmerSpec[0];
/*  192: 435 */       stemmerSpec[0] = "";
/*  193: 436 */       Stemmer stemmer = (Stemmer)Utils.forName(Class.forName("weka.core.stemmers.Stemmer"), stemmerName, stemmerSpec);
/*  194: 437 */       setStemmer(stemmer);
/*  195:     */     }
/*  196: 440 */     String stopwordsHandlerString = Utils.getOption("stopwords-handler", options);
/*  197: 441 */     if (stopwordsHandlerString.length() == 0)
/*  198:     */     {
/*  199: 442 */       setStopwordsHandler(null);
/*  200:     */     }
/*  201:     */     else
/*  202:     */     {
/*  203: 444 */       String[] stopwordsHandlerSpec = Utils.splitOptions(stopwordsHandlerString);
/*  204: 445 */       if (stopwordsHandlerSpec.length == 0) {
/*  205: 446 */         throw new Exception("Invalid StopwordsHandler specification string");
/*  206:     */       }
/*  207: 448 */       String stopwordsHandlerName = stopwordsHandlerSpec[0];
/*  208: 449 */       stopwordsHandlerSpec[0] = "";
/*  209: 450 */       StopwordsHandler stopwordsHandler = (StopwordsHandler)Utils.forName(Class.forName("weka.core.stopwords.StopwordsHandler"), stopwordsHandlerName, stopwordsHandlerSpec);
/*  210:     */       
/*  211:     */ 
/*  212: 453 */       setStopwordsHandler(stopwordsHandler);
/*  213:     */     }
/*  214: 457 */     String tokenizerString = Utils.getOption("tokenizer", options);
/*  215: 458 */     if (tokenizerString.length() == 0)
/*  216:     */     {
/*  217: 459 */       setTokenizer(new WordTokenizer());
/*  218:     */     }
/*  219:     */     else
/*  220:     */     {
/*  221: 461 */       String[] tokenizerSpec = Utils.splitOptions(tokenizerString);
/*  222: 462 */       if (tokenizerSpec.length == 0) {
/*  223: 463 */         throw new Exception("Invalid tokenizer specification string");
/*  224:     */       }
/*  225: 465 */       String tokenizerName = tokenizerSpec[0];
/*  226: 466 */       tokenizerSpec[0] = "";
/*  227: 467 */       Tokenizer tokenizer = (Tokenizer)Utils.forName(Class.forName("weka.core.tokenizers.Tokenizer"), tokenizerName, tokenizerSpec);
/*  228:     */       
/*  229: 469 */       setTokenizer(tokenizer);
/*  230:     */     }
/*  231: 472 */     String dictFile = Utils.getOption("dictionary", options);
/*  232: 473 */     setDictionaryFileToSaveTo(new File(dictFile));
/*  233:     */     
/*  234: 475 */     setSaveDictionaryInBinaryForm(Utils.getFlag("binary-dict", options));
/*  235:     */     
/*  236: 477 */     Utils.checkForRemainingOptions(options);
/*  237:     */   }
/*  238:     */   
/*  239:     */   public String[] getOptions()
/*  240:     */   {
/*  241: 488 */     Vector<String> result = new Vector();
/*  242:     */     
/*  243: 490 */     result.add("-R");
/*  244: 491 */     result.add(getSelectedRange().getRanges());
/*  245: 493 */     if (getInvertSelection()) {
/*  246: 494 */       result.add("-V");
/*  247:     */     }
/*  248: 497 */     if (!"".equals(getAttributeNamePrefix()))
/*  249:     */     {
/*  250: 498 */       result.add("-P");
/*  251: 499 */       result.add(getAttributeNamePrefix());
/*  252:     */     }
/*  253: 502 */     result.add("-W");
/*  254: 503 */     result.add(String.valueOf(getWordsToKeep()));
/*  255:     */     
/*  256: 505 */     result.add("-prune-rate");
/*  257: 506 */     result.add(String.valueOf(getPeriodicPruning()));
/*  258: 508 */     if (getOutputWordCounts()) {
/*  259: 509 */       result.add("-C");
/*  260:     */     }
/*  261: 512 */     if (getTFTransform()) {
/*  262: 513 */       result.add("-T");
/*  263:     */     }
/*  264: 516 */     if (getIDFTransform()) {
/*  265: 517 */       result.add("-I");
/*  266:     */     }
/*  267: 520 */     result.add("-N");
/*  268: 521 */     result.add("" + this.m_filterType);
/*  269: 523 */     if (getLowerCaseTokens()) {
/*  270: 524 */       result.add("-L");
/*  271:     */     }
/*  272: 527 */     if (getStemmer() != null)
/*  273:     */     {
/*  274: 528 */       result.add("-stemmer");
/*  275: 529 */       String spec = getStemmer().getClass().getName();
/*  276: 530 */       if ((getStemmer() instanceof OptionHandler)) {
/*  277: 531 */         spec = spec + " " + Utils.joinOptions(((OptionHandler)getStemmer()).getOptions());
/*  278:     */       }
/*  279: 534 */       result.add(spec.trim());
/*  280:     */     }
/*  281: 537 */     if (getStopwordsHandler() != null)
/*  282:     */     {
/*  283: 538 */       result.add("-stopwords-handler");
/*  284: 539 */       String spec = getStopwordsHandler().getClass().getName();
/*  285: 540 */       if ((getStopwordsHandler() instanceof OptionHandler)) {
/*  286: 541 */         spec = spec + " " + Utils.joinOptions(((OptionHandler)getStopwordsHandler()).getOptions());
/*  287:     */       }
/*  288: 544 */       result.add(spec.trim());
/*  289:     */     }
/*  290: 547 */     result.add("-M");
/*  291: 548 */     result.add(String.valueOf(getMinTermFreq()));
/*  292: 550 */     if (getDoNotOperateOnPerClassBasis()) {
/*  293: 551 */       result.add("-O");
/*  294:     */     }
/*  295: 554 */     result.add("-tokenizer");
/*  296: 555 */     String spec = getTokenizer().getClass().getName();
/*  297: 556 */     if ((getTokenizer() instanceof OptionHandler)) {
/*  298: 557 */       spec = spec + " " + Utils.joinOptions(getTokenizer().getOptions());
/*  299:     */     }
/*  300: 560 */     result.add(spec.trim());
/*  301: 562 */     if ((this.m_dictionaryFile != null) && (this.m_dictionaryFile.toString().length() > 0) && (!this.m_dictionaryFile.toString().equalsIgnoreCase("-- set me --")))
/*  302:     */     {
/*  303: 564 */       result.add("-dictionary");
/*  304: 565 */       result.add(this.m_dictionaryFile.toString());
/*  305: 567 */       if (getSaveDictionaryInBinaryForm()) {
/*  306: 568 */         result.add("-binary-dict");
/*  307:     */       }
/*  308:     */     }
/*  309: 573 */     return (String[])result.toArray(new String[result.size()]);
/*  310:     */   }
/*  311:     */   
/*  312:     */   public StringToWordVector(int wordsToKeep)
/*  313:     */   {
/*  314: 584 */     this.m_dictionaryBuilder.setWordsToKeep(wordsToKeep);
/*  315:     */   }
/*  316:     */   
/*  317:     */   public Capabilities getCapabilities()
/*  318:     */   {
/*  319: 595 */     Capabilities result = super.getCapabilities();
/*  320: 596 */     result.disableAll();
/*  321:     */     
/*  322:     */ 
/*  323: 599 */     result.enableAllAttributes();
/*  324: 600 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  325:     */     
/*  326:     */ 
/*  327: 603 */     result.enableAllClasses();
/*  328: 604 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  329: 605 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  330:     */     
/*  331: 607 */     return result;
/*  332:     */   }
/*  333:     */   
/*  334:     */   public boolean setInputFormat(Instances instanceInfo)
/*  335:     */     throws Exception
/*  336:     */   {
/*  337: 622 */     super.setInputFormat(instanceInfo);
/*  338:     */     
/*  339: 624 */     this.m_dictionaryBuilder.reset();
/*  340: 625 */     this.m_dictionaryBuilder.setSortDictionary(true);
/*  341: 626 */     this.m_dictionaryBuilder.setNormalize(false);
/*  342: 627 */     this.m_dictionaryBuilder.setup(instanceInfo);
/*  343:     */     
/*  344: 629 */     return false;
/*  345:     */   }
/*  346:     */   
/*  347:     */   public boolean input(Instance instance)
/*  348:     */     throws Exception
/*  349:     */   {
/*  350: 643 */     if (getInputFormat() == null) {
/*  351: 644 */       throw new IllegalStateException("No input instance format defined");
/*  352:     */     }
/*  353: 646 */     if (this.m_NewBatch)
/*  354:     */     {
/*  355: 647 */       resetQueue();
/*  356: 648 */       this.m_NewBatch = false;
/*  357:     */     }
/*  358: 650 */     if (isFirstBatchDone())
/*  359:     */     {
/*  360: 651 */       Instance inst = this.m_dictionaryBuilder.vectorizeInstance(instance);
/*  361: 652 */       push(inst, false);
/*  362: 653 */       return true;
/*  363:     */     }
/*  364: 655 */     bufferInput(instance);
/*  365: 656 */     return false;
/*  366:     */   }
/*  367:     */   
/*  368:     */   public boolean batchFinished()
/*  369:     */     throws Exception
/*  370:     */   {
/*  371: 671 */     if (getInputFormat() == null) {
/*  372: 672 */       throw new IllegalStateException("No input instance format defined");
/*  373:     */     }
/*  374: 678 */     if (!isFirstBatchDone())
/*  375:     */     {
/*  376: 680 */       long pruneRate = Math.round(this.m_PeriodicPruningRate / 100.0D * getInputFormat().numInstances());
/*  377:     */       
/*  378: 682 */       this.m_dictionaryBuilder.setPeriodicPruning(pruneRate);
/*  379: 685 */       for (int i = 0; i < getInputFormat().numInstances(); i++)
/*  380:     */       {
/*  381: 686 */         Instance toProcess = getInputFormat().instance(i);
/*  382: 687 */         this.m_dictionaryBuilder.processInstance(toProcess);
/*  383:     */       }
/*  384: 689 */       this.m_dictionaryBuilder.finalizeDictionary();
/*  385:     */       
/*  386: 691 */       setOutputFormat(this.m_dictionaryBuilder.getVectorizedFormat());
/*  387:     */       
/*  388: 693 */       this.m_dictionaryBuilder.setNormalize(this.m_filterType != 0);
/*  389: 694 */       Instances converted = this.m_dictionaryBuilder.vectorizeBatch(getInputFormat(), this.m_filterType != 0);
/*  390: 698 */       if ((this.m_dictionaryFile != null) && (this.m_dictionaryFile.toString().length() > 0) && (!this.m_dictionaryFile.toString().equalsIgnoreCase("-- set me --"))) {
/*  391: 700 */         this.m_dictionaryBuilder.saveDictionary(this.m_dictionaryFile, !this.m_dictionaryIsBinary);
/*  392:     */       }
/*  393: 704 */       for (int i = 0; i < converted.numInstances(); i++) {
/*  394: 705 */         push(converted.instance(i), false);
/*  395:     */       }
/*  396:     */     }
/*  397: 710 */     flushInput();
/*  398:     */     
/*  399: 712 */     this.m_NewBatch = true;
/*  400: 713 */     this.m_FirstBatchDone = true;
/*  401: 714 */     return numPendingOutput() != 0;
/*  402:     */   }
/*  403:     */   
/*  404:     */   public String dictionaryFileToSaveToTipText()
/*  405:     */   {
/*  406: 723 */     return "The path to save the dictionary file to - an empty path or a path '-- set me --' means do not save the dictionary.";
/*  407:     */   }
/*  408:     */   
/*  409:     */   public void setDictionaryFileToSaveTo(File toSaveTo)
/*  410:     */   {
/*  411: 735 */     this.m_dictionaryFile = toSaveTo;
/*  412:     */   }
/*  413:     */   
/*  414:     */   public File getDictionaryFileToSaveTo()
/*  415:     */   {
/*  416: 745 */     return this.m_dictionaryFile;
/*  417:     */   }
/*  418:     */   
/*  419:     */   public String saveDictionaryInBinaryFormTipText()
/*  420:     */   {
/*  421: 749 */     return "Save the dictionary as a binary serialized java object instead of in plain text form.";
/*  422:     */   }
/*  423:     */   
/*  424:     */   public void setSaveDictionaryInBinaryForm(boolean saveAsBinary)
/*  425:     */   {
/*  426: 760 */     this.m_dictionaryIsBinary = saveAsBinary;
/*  427:     */   }
/*  428:     */   
/*  429:     */   public boolean getSaveDictionaryInBinaryForm()
/*  430:     */   {
/*  431: 770 */     return this.m_dictionaryIsBinary;
/*  432:     */   }
/*  433:     */   
/*  434:     */   public String globalInfo()
/*  435:     */   {
/*  436: 780 */     return "Converts String attributes into a set of attributes representing word occurrence (depending on the tokenizer) information from the text contained in the strings. The set of words (attributes) is determined by the first batch filtered (typically training data).";
/*  437:     */   }
/*  438:     */   
/*  439:     */   public boolean getOutputWordCounts()
/*  440:     */   {
/*  441: 793 */     return this.m_dictionaryBuilder.getOutputWordCounts();
/*  442:     */   }
/*  443:     */   
/*  444:     */   public void setOutputWordCounts(boolean outputWordCounts)
/*  445:     */   {
/*  446: 803 */     this.m_dictionaryBuilder.setOutputWordCounts(outputWordCounts);
/*  447:     */   }
/*  448:     */   
/*  449:     */   public String outputWordCountsTipText()
/*  450:     */   {
/*  451: 813 */     return "Output word counts rather than boolean 0 or 1(indicating presence or absence of a word).";
/*  452:     */   }
/*  453:     */   
/*  454:     */   public Range getSelectedRange()
/*  455:     */   {
/*  456: 823 */     return this.m_dictionaryBuilder.getSelectedRange();
/*  457:     */   }
/*  458:     */   
/*  459:     */   public void setSelectedRange(String newSelectedRange)
/*  460:     */   {
/*  461: 832 */     this.m_dictionaryBuilder.setSelectedRange(newSelectedRange);
/*  462:     */   }
/*  463:     */   
/*  464:     */   public String attributeIndicesTipText()
/*  465:     */   {
/*  466: 842 */     return "Specify range of attributes to act on. This is a comma separated list of attribute indices, with \"first\" and \"last\" valid values. Specify an inclusive range with \"-\". E.g: \"first-3,5,6-10,last\".";
/*  467:     */   }
/*  468:     */   
/*  469:     */   public String getAttributeIndices()
/*  470:     */   {
/*  471: 854 */     return this.m_dictionaryBuilder.getAttributeIndices();
/*  472:     */   }
/*  473:     */   
/*  474:     */   public void setAttributeIndices(String rangeList)
/*  475:     */   {
/*  476: 867 */     this.m_dictionaryBuilder.setAttributeIndices(rangeList);
/*  477:     */   }
/*  478:     */   
/*  479:     */   public void setAttributeIndicesArray(int[] attributes)
/*  480:     */   {
/*  481: 879 */     this.m_dictionaryBuilder.setAttributeIndicesArray(attributes);
/*  482:     */   }
/*  483:     */   
/*  484:     */   public String invertSelectionTipText()
/*  485:     */   {
/*  486: 889 */     return "Set attribute selection mode. If false, only selected attributes in the range will be worked on; if true, only non-selected attributes will be processed.";
/*  487:     */   }
/*  488:     */   
/*  489:     */   public boolean getInvertSelection()
/*  490:     */   {
/*  491: 900 */     return this.m_dictionaryBuilder.getInvertSelection();
/*  492:     */   }
/*  493:     */   
/*  494:     */   public void setInvertSelection(boolean invert)
/*  495:     */   {
/*  496: 909 */     this.m_dictionaryBuilder.setInvertSelection(invert);
/*  497:     */   }
/*  498:     */   
/*  499:     */   public String getAttributeNamePrefix()
/*  500:     */   {
/*  501: 918 */     return this.m_dictionaryBuilder.getAttributeNamePrefix();
/*  502:     */   }
/*  503:     */   
/*  504:     */   public void setAttributeNamePrefix(String newPrefix)
/*  505:     */   {
/*  506: 927 */     this.m_dictionaryBuilder.setAttributeNamePrefix(newPrefix);
/*  507:     */   }
/*  508:     */   
/*  509:     */   public String attributeNamePrefixTipText()
/*  510:     */   {
/*  511: 937 */     return "Prefix for the created attribute names. (default: \"\")";
/*  512:     */   }
/*  513:     */   
/*  514:     */   public int getWordsToKeep()
/*  515:     */   {
/*  516: 948 */     return this.m_dictionaryBuilder.getWordsToKeep();
/*  517:     */   }
/*  518:     */   
/*  519:     */   public void setWordsToKeep(int newWordsToKeep)
/*  520:     */   {
/*  521: 959 */     this.m_dictionaryBuilder.setWordsToKeep(newWordsToKeep);
/*  522:     */   }
/*  523:     */   
/*  524:     */   public String wordsToKeepTipText()
/*  525:     */   {
/*  526: 969 */     return "The number of words (per class if there is a class attribute assigned) to attempt to keep.";
/*  527:     */   }
/*  528:     */   
/*  529:     */   public double getPeriodicPruning()
/*  530:     */   {
/*  531: 980 */     return this.m_PeriodicPruningRate;
/*  532:     */   }
/*  533:     */   
/*  534:     */   public void setPeriodicPruning(double newPeriodicPruning)
/*  535:     */   {
/*  536: 991 */     this.m_PeriodicPruningRate = newPeriodicPruning;
/*  537:     */   }
/*  538:     */   
/*  539:     */   public String periodicPruningTipText()
/*  540:     */   {
/*  541:1001 */     return "Specify the rate (x% of the input dataset) at which to periodically prune the dictionary. wordsToKeep prunes after creating a full dictionary. You may not have enough memory for this approach.";
/*  542:     */   }
/*  543:     */   
/*  544:     */   public boolean getTFTransform()
/*  545:     */   {
/*  546:1013 */     return this.m_dictionaryBuilder.getTFTransform();
/*  547:     */   }
/*  548:     */   
/*  549:     */   public void setTFTransform(boolean TFTransform)
/*  550:     */   {
/*  551:1023 */     this.m_dictionaryBuilder.setTFTransform(TFTransform);
/*  552:     */   }
/*  553:     */   
/*  554:     */   public String TFTransformTipText()
/*  555:     */   {
/*  556:1033 */     return "Sets whether if the word frequencies should be transformed into:\n    log(1+fij) \n       where fij is the frequency of word i in document (instance) j.";
/*  557:     */   }
/*  558:     */   
/*  559:     */   public boolean getIDFTransform()
/*  560:     */   {
/*  561:1047 */     return this.m_dictionaryBuilder.getIDFTransform();
/*  562:     */   }
/*  563:     */   
/*  564:     */   public void setIDFTransform(boolean IDFTransform)
/*  565:     */   {
/*  566:1059 */     this.m_dictionaryBuilder.setIDFTransform(IDFTransform);
/*  567:     */   }
/*  568:     */   
/*  569:     */   public String IDFTransformTipText()
/*  570:     */   {
/*  571:1069 */     return "Sets whether if the word frequencies in a document should be transformed into: \n   fij*log(num of Docs/num of Docs with word i) \n      where fij is the frequency of word i in document (instance) j.";
/*  572:     */   }
/*  573:     */   
/*  574:     */   public SelectedTag getNormalizeDocLength()
/*  575:     */   {
/*  576:1082 */     return new SelectedTag(this.m_filterType, TAGS_FILTER);
/*  577:     */   }
/*  578:     */   
/*  579:     */   public void setNormalizeDocLength(SelectedTag newType)
/*  580:     */   {
/*  581:1093 */     if (newType.getTags() == TAGS_FILTER) {
/*  582:1094 */       this.m_filterType = newType.getSelectedTag().getID();
/*  583:     */     }
/*  584:     */   }
/*  585:     */   
/*  586:     */   public String normalizeDocLengthTipText()
/*  587:     */   {
/*  588:1105 */     return "Sets whether if the word frequencies for a document (instance) should be normalized or not.";
/*  589:     */   }
/*  590:     */   
/*  591:     */   public boolean getLowerCaseTokens()
/*  592:     */   {
/*  593:1115 */     return this.m_dictionaryBuilder.getLowerCaseTokens();
/*  594:     */   }
/*  595:     */   
/*  596:     */   public void setLowerCaseTokens(boolean downCaseTokens)
/*  597:     */   {
/*  598:1126 */     this.m_dictionaryBuilder.setLowerCaseTokens(downCaseTokens);
/*  599:     */   }
/*  600:     */   
/*  601:     */   public String doNotOperateOnPerClassBasisTipText()
/*  602:     */   {
/*  603:1136 */     return "If this is set, the maximum number of words and the minimum term frequency is not enforced on a per-class basis but based on the documents in all the classes (even if a class attribute is set).";
/*  604:     */   }
/*  605:     */   
/*  606:     */   public boolean getDoNotOperateOnPerClassBasis()
/*  607:     */   {
/*  608:1148 */     return this.m_dictionaryBuilder.getDoNotOperateOnPerClassBasis();
/*  609:     */   }
/*  610:     */   
/*  611:     */   public void setDoNotOperateOnPerClassBasis(boolean newDoNotOperateOnPerClassBasis)
/*  612:     */   {
/*  613:1159 */     this.m_dictionaryBuilder.setDoNotOperateOnPerClassBasis(newDoNotOperateOnPerClassBasis);
/*  614:     */   }
/*  615:     */   
/*  616:     */   public String minTermFreqTipText()
/*  617:     */   {
/*  618:1169 */     return "Sets the minimum term frequency. This is enforced on a per-class basis.";
/*  619:     */   }
/*  620:     */   
/*  621:     */   public int getMinTermFreq()
/*  622:     */   {
/*  623:1179 */     return this.m_dictionaryBuilder.getMinTermFreq();
/*  624:     */   }
/*  625:     */   
/*  626:     */   public void setMinTermFreq(int newMinTermFreq)
/*  627:     */   {
/*  628:1188 */     this.m_dictionaryBuilder.setMinTermFreq(newMinTermFreq);
/*  629:     */   }
/*  630:     */   
/*  631:     */   public String lowerCaseTokensTipText()
/*  632:     */   {
/*  633:1198 */     return "If set then all the word tokens are converted to lower case before being added to the dictionary.";
/*  634:     */   }
/*  635:     */   
/*  636:     */   public void setStemmer(Stemmer value)
/*  637:     */   {
/*  638:1210 */     if (value != null) {
/*  639:1211 */       this.m_dictionaryBuilder.setStemmer(value);
/*  640:     */     } else {
/*  641:1213 */       this.m_dictionaryBuilder.setStemmer(new NullStemmer());
/*  642:     */     }
/*  643:     */   }
/*  644:     */   
/*  645:     */   public Stemmer getStemmer()
/*  646:     */   {
/*  647:1223 */     return this.m_dictionaryBuilder.getStemmer();
/*  648:     */   }
/*  649:     */   
/*  650:     */   public String stemmerTipText()
/*  651:     */   {
/*  652:1233 */     return "The stemming algorithm to use on the words.";
/*  653:     */   }
/*  654:     */   
/*  655:     */   public void setStopwordsHandler(StopwordsHandler value)
/*  656:     */   {
/*  657:1242 */     if (value != null) {
/*  658:1243 */       this.m_dictionaryBuilder.setStopwordsHandler(value);
/*  659:     */     } else {
/*  660:1245 */       this.m_dictionaryBuilder.setStopwordsHandler(new Null());
/*  661:     */     }
/*  662:     */   }
/*  663:     */   
/*  664:     */   public StopwordsHandler getStopwordsHandler()
/*  665:     */   {
/*  666:1255 */     return this.m_dictionaryBuilder.getStopwordsHandler();
/*  667:     */   }
/*  668:     */   
/*  669:     */   public String stopwordsHandlerTipText()
/*  670:     */   {
/*  671:1265 */     return "The stopwords handler to use (Null means no stopwords are used).";
/*  672:     */   }
/*  673:     */   
/*  674:     */   public void setTokenizer(Tokenizer value)
/*  675:     */   {
/*  676:1274 */     this.m_dictionaryBuilder.setTokenizer(value);
/*  677:     */   }
/*  678:     */   
/*  679:     */   public Tokenizer getTokenizer()
/*  680:     */   {
/*  681:1283 */     return this.m_dictionaryBuilder.getTokenizer();
/*  682:     */   }
/*  683:     */   
/*  684:     */   public String tokenizerTipText()
/*  685:     */   {
/*  686:1293 */     return "The tokenizing algorithm to use on the strings.";
/*  687:     */   }
/*  688:     */   
/*  689:     */   public String getRevision()
/*  690:     */   {
/*  691:1303 */     return RevisionUtils.extract("$Revision: 12074 $");
/*  692:     */   }
/*  693:     */   
/*  694:     */   public static void main(String[] argv)
/*  695:     */   {
/*  696:1312 */     runFilter(new StringToWordVector(), argv);
/*  697:     */   }
/*  698:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.StringToWordVector
 * JD-Core Version:    0.7.0.1
 */