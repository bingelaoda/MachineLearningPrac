/*    1:     */ package weka.core;
/*    2:     */ 
/*    3:     */ import java.io.BufferedInputStream;
/*    4:     */ import java.io.BufferedOutputStream;
/*    5:     */ import java.io.BufferedReader;
/*    6:     */ import java.io.BufferedWriter;
/*    7:     */ import java.io.File;
/*    8:     */ import java.io.FileInputStream;
/*    9:     */ import java.io.FileOutputStream;
/*   10:     */ import java.io.FileReader;
/*   11:     */ import java.io.FileWriter;
/*   12:     */ import java.io.IOException;
/*   13:     */ import java.io.InputStream;
/*   14:     */ import java.io.ObjectInputStream;
/*   15:     */ import java.io.ObjectOutputStream;
/*   16:     */ import java.io.OutputStream;
/*   17:     */ import java.io.PrintStream;
/*   18:     */ import java.io.Reader;
/*   19:     */ import java.io.Serializable;
/*   20:     */ import java.io.Writer;
/*   21:     */ import java.util.ArrayList;
/*   22:     */ import java.util.Arrays;
/*   23:     */ import java.util.Enumeration;
/*   24:     */ import java.util.Iterator;
/*   25:     */ import java.util.LinkedHashMap;
/*   26:     */ import java.util.List;
/*   27:     */ import java.util.Map;
/*   28:     */ import java.util.Map.Entry;
/*   29:     */ import java.util.Set;
/*   30:     */ import java.util.TreeMap;
/*   31:     */ import java.util.Vector;
/*   32:     */ import weka.core.stemmers.NullStemmer;
/*   33:     */ import weka.core.stemmers.Stemmer;
/*   34:     */ import weka.core.stopwords.Null;
/*   35:     */ import weka.core.stopwords.StopwordsHandler;
/*   36:     */ import weka.core.tokenizers.Tokenizer;
/*   37:     */ import weka.core.tokenizers.WordTokenizer;
/*   38:     */ import weka.gui.ProgrammaticProperty;
/*   39:     */ 
/*   40:     */ public class DictionaryBuilder
/*   41:     */   implements Aggregateable<DictionaryBuilder>, OptionHandler, Serializable
/*   42:     */ {
/*   43:     */   private static final long serialVersionUID = 5579506627960356012L;
/*   44:     */   protected Instances m_inputFormat;
/*   45:     */   protected Instances m_outputFormat;
/*   46:     */   protected Map<String, int[]>[] m_dictsPerClass;
/*   47:     */   protected Map<String, int[]> m_consolidatedDict;
/*   48:     */   protected transient Map<String, int[]> m_inputVector;
/*   49:     */   protected boolean m_doNotOperateOnPerClassBasis;
/*   50:     */   protected boolean m_outputCounts;
/*   51:     */   protected boolean m_lowerCaseTokens;
/*   52: 120 */   protected Stemmer m_stemmer = new NullStemmer();
/*   53: 123 */   protected StopwordsHandler m_stopwordsHandler = new Null();
/*   54: 129 */   protected int m_wordsToKeep = 1000;
/*   55:     */   protected long m_periodicPruneRate;
/*   56: 138 */   protected int m_minFrequency = 1;
/*   57: 141 */   protected int m_count = 0;
/*   58: 144 */   protected Tokenizer m_tokenizer = new WordTokenizer();
/*   59: 147 */   protected Range m_selectedRange = new Range("first-last");
/*   60: 150 */   protected int m_classIndex = -1;
/*   61: 153 */   protected int m_numClasses = 1;
/*   62: 156 */   protected String m_Prefix = "";
/*   63:     */   protected boolean m_TFTransform;
/*   64:     */   protected boolean m_IDFTransform;
/*   65:     */   protected boolean m_normalize;
/*   66:     */   protected double m_docLengthSum;
/*   67:     */   protected double m_avgDocLength;
/*   68:     */   protected boolean m_sortDictionary;
/*   69:     */   protected boolean m_inputContainsStringAttributes;
/*   70:     */   
/*   71:     */   @ProgrammaticProperty
/*   72:     */   public void setAverageDocLength(double averageDocLength)
/*   73:     */   {
/*   74: 186 */     this.m_avgDocLength = averageDocLength;
/*   75:     */   }
/*   76:     */   
/*   77:     */   public double getAverageDocLength()
/*   78:     */   {
/*   79: 195 */     return this.m_avgDocLength;
/*   80:     */   }
/*   81:     */   
/*   82:     */   public String sortDictionaryTipText()
/*   83:     */   {
/*   84: 204 */     return "Sort the dictionary alphabetically";
/*   85:     */   }
/*   86:     */   
/*   87:     */   public void setSortDictionary(boolean sortDictionary)
/*   88:     */   {
/*   89: 215 */     this.m_sortDictionary = sortDictionary;
/*   90:     */   }
/*   91:     */   
/*   92:     */   public boolean getSortDictionary()
/*   93:     */   {
/*   94: 226 */     return this.m_sortDictionary;
/*   95:     */   }
/*   96:     */   
/*   97:     */   public boolean getOutputWordCounts()
/*   98:     */   {
/*   99: 236 */     return this.m_outputCounts;
/*  100:     */   }
/*  101:     */   
/*  102:     */   public void setOutputWordCounts(boolean outputWordCounts)
/*  103:     */   {
/*  104: 246 */     this.m_outputCounts = outputWordCounts;
/*  105:     */   }
/*  106:     */   
/*  107:     */   public String outputWordCountsTipText()
/*  108:     */   {
/*  109: 256 */     return "Output word counts rather than boolean 0 or 1(indicating presence or absence of a word).";
/*  110:     */   }
/*  111:     */   
/*  112:     */   public Range getSelectedRange()
/*  113:     */   {
/*  114: 266 */     return this.m_selectedRange;
/*  115:     */   }
/*  116:     */   
/*  117:     */   public void setSelectedRange(String newSelectedRange)
/*  118:     */   {
/*  119: 275 */     this.m_selectedRange = new Range(newSelectedRange);
/*  120:     */   }
/*  121:     */   
/*  122:     */   public String attributeIndicesTipText()
/*  123:     */   {
/*  124: 285 */     return "Specify range of attributes to act on. This is a comma separated list of attribute indices, with \"first\" and \"last\" valid values. Specify an inclusive range with \"-\". E.g: \"first-3,5,6-10,last\".";
/*  125:     */   }
/*  126:     */   
/*  127:     */   public String getAttributeIndices()
/*  128:     */   {
/*  129: 297 */     return this.m_selectedRange.getRanges();
/*  130:     */   }
/*  131:     */   
/*  132:     */   public void setAttributeIndices(String rangeList)
/*  133:     */   {
/*  134: 310 */     this.m_selectedRange.setRanges(rangeList);
/*  135:     */   }
/*  136:     */   
/*  137:     */   public void setAttributeIndicesArray(int[] attributes)
/*  138:     */   {
/*  139: 322 */     setAttributeIndices(Range.indicesToRangeList(attributes));
/*  140:     */   }
/*  141:     */   
/*  142:     */   public String invertSelectionTipText()
/*  143:     */   {
/*  144: 332 */     return "Set attribute selection mode. If false, only selected attributes in the range will be worked on; if true, only non-selected attributes will be processed.";
/*  145:     */   }
/*  146:     */   
/*  147:     */   public boolean getInvertSelection()
/*  148:     */   {
/*  149: 343 */     return this.m_selectedRange.getInvert();
/*  150:     */   }
/*  151:     */   
/*  152:     */   public void setInvertSelection(boolean invert)
/*  153:     */   {
/*  154: 352 */     this.m_selectedRange.setInvert(invert);
/*  155:     */   }
/*  156:     */   
/*  157:     */   public int getWordsToKeep()
/*  158:     */   {
/*  159: 363 */     return this.m_wordsToKeep;
/*  160:     */   }
/*  161:     */   
/*  162:     */   public void setWordsToKeep(int newWordsToKeep)
/*  163:     */   {
/*  164: 374 */     this.m_wordsToKeep = newWordsToKeep;
/*  165:     */   }
/*  166:     */   
/*  167:     */   public String wordsToKeepTipText()
/*  168:     */   {
/*  169: 384 */     return "The number of words (per class if there is a class attribute assigned) to attempt to keep.";
/*  170:     */   }
/*  171:     */   
/*  172:     */   public long getPeriodicPruning()
/*  173:     */   {
/*  174: 395 */     return this.m_periodicPruneRate;
/*  175:     */   }
/*  176:     */   
/*  177:     */   public void setPeriodicPruning(long newPeriodicPruning)
/*  178:     */   {
/*  179: 407 */     this.m_periodicPruneRate = newPeriodicPruning;
/*  180:     */   }
/*  181:     */   
/*  182:     */   public String periodicPruningTipText()
/*  183:     */   {
/*  184: 417 */     return "Specify the rate (x% of the input dataset) at which to periodically prune the dictionary. wordsToKeep prunes after creating a full dictionary. You may not have enough memory for this approach.";
/*  185:     */   }
/*  186:     */   
/*  187:     */   public boolean getTFTransform()
/*  188:     */   {
/*  189: 429 */     return this.m_TFTransform;
/*  190:     */   }
/*  191:     */   
/*  192:     */   public void setTFTransform(boolean TFTransform)
/*  193:     */   {
/*  194: 439 */     this.m_TFTransform = TFTransform;
/*  195:     */   }
/*  196:     */   
/*  197:     */   public String TFTransformTipText()
/*  198:     */   {
/*  199: 449 */     return "Sets whether if the word frequencies should be transformed into:\n    log(1+fij) \n       where fij is the frequency of word i in document (instance) j.";
/*  200:     */   }
/*  201:     */   
/*  202:     */   public String getAttributeNamePrefix()
/*  203:     */   {
/*  204: 460 */     return this.m_Prefix;
/*  205:     */   }
/*  206:     */   
/*  207:     */   public void setAttributeNamePrefix(String newPrefix)
/*  208:     */   {
/*  209: 469 */     this.m_Prefix = newPrefix;
/*  210:     */   }
/*  211:     */   
/*  212:     */   public String attributeNamePrefixTipText()
/*  213:     */   {
/*  214: 479 */     return "Prefix for the created attribute names. (default: \"\")";
/*  215:     */   }
/*  216:     */   
/*  217:     */   public boolean getIDFTransform()
/*  218:     */   {
/*  219: 491 */     return this.m_IDFTransform;
/*  220:     */   }
/*  221:     */   
/*  222:     */   public void setIDFTransform(boolean IDFTransform)
/*  223:     */   {
/*  224: 503 */     this.m_IDFTransform = IDFTransform;
/*  225:     */   }
/*  226:     */   
/*  227:     */   public String IDFTransformTipText()
/*  228:     */   {
/*  229: 513 */     return "Sets whether if the word frequencies in a document should be transformed into: \n   fij*log(num of Docs/num of Docs with word i) \n      where fij is the frequency of word i in document (instance) j.";
/*  230:     */   }
/*  231:     */   
/*  232:     */   public boolean getNormalize()
/*  233:     */   {
/*  234: 525 */     return this.m_normalize;
/*  235:     */   }
/*  236:     */   
/*  237:     */   public void setNormalize(boolean n)
/*  238:     */   {
/*  239: 534 */     this.m_normalize = n;
/*  240:     */   }
/*  241:     */   
/*  242:     */   public String normalizeTipText()
/*  243:     */   {
/*  244: 543 */     return "Whether word frequencies for a document (instance) should be normalized or not";
/*  245:     */   }
/*  246:     */   
/*  247:     */   public String normalizeDocLengthTipText()
/*  248:     */   {
/*  249: 554 */     return "Sets whether if the word frequencies for a document (instance) should be normalized or not.";
/*  250:     */   }
/*  251:     */   
/*  252:     */   public boolean getLowerCaseTokens()
/*  253:     */   {
/*  254: 564 */     return this.m_lowerCaseTokens;
/*  255:     */   }
/*  256:     */   
/*  257:     */   public void setLowerCaseTokens(boolean downCaseTokens)
/*  258:     */   {
/*  259: 575 */     this.m_lowerCaseTokens = downCaseTokens;
/*  260:     */   }
/*  261:     */   
/*  262:     */   public String lowerCaseTokensTipText()
/*  263:     */   {
/*  264: 585 */     return "If set then all the word tokens are converted to lower case before being added to the dictionary.";
/*  265:     */   }
/*  266:     */   
/*  267:     */   public String doNotOperateOnPerClassBasisTipText()
/*  268:     */   {
/*  269: 596 */     return "If this is set, the maximum number of words and the minimum term frequency is not enforced on a per-class basis but based on the documents in all the classes (even if a class attribute is set).";
/*  270:     */   }
/*  271:     */   
/*  272:     */   public boolean getDoNotOperateOnPerClassBasis()
/*  273:     */   {
/*  274: 608 */     return this.m_doNotOperateOnPerClassBasis;
/*  275:     */   }
/*  276:     */   
/*  277:     */   public void setDoNotOperateOnPerClassBasis(boolean newDoNotOperateOnPerClassBasis)
/*  278:     */   {
/*  279: 619 */     this.m_doNotOperateOnPerClassBasis = newDoNotOperateOnPerClassBasis;
/*  280:     */   }
/*  281:     */   
/*  282:     */   public String minTermFreqTipText()
/*  283:     */   {
/*  284: 629 */     return "Sets the minimum term frequency. This is enforced on a per-class basis.";
/*  285:     */   }
/*  286:     */   
/*  287:     */   public int getMinTermFreq()
/*  288:     */   {
/*  289: 639 */     return this.m_minFrequency;
/*  290:     */   }
/*  291:     */   
/*  292:     */   public void setMinTermFreq(int newMinTermFreq)
/*  293:     */   {
/*  294: 648 */     this.m_minFrequency = newMinTermFreq;
/*  295:     */   }
/*  296:     */   
/*  297:     */   public Stemmer getStemmer()
/*  298:     */   {
/*  299: 657 */     return this.m_stemmer;
/*  300:     */   }
/*  301:     */   
/*  302:     */   public void setStemmer(Stemmer value)
/*  303:     */   {
/*  304: 668 */     if (value != null) {
/*  305: 669 */       this.m_stemmer = value;
/*  306:     */     } else {
/*  307: 671 */       this.m_stemmer = new NullStemmer();
/*  308:     */     }
/*  309:     */   }
/*  310:     */   
/*  311:     */   public String stemmerTipText()
/*  312:     */   {
/*  313: 682 */     return "The stemming algorithm to use on the words.";
/*  314:     */   }
/*  315:     */   
/*  316:     */   public StopwordsHandler getStopwordsHandler()
/*  317:     */   {
/*  318: 691 */     return this.m_stopwordsHandler;
/*  319:     */   }
/*  320:     */   
/*  321:     */   public void setStopwordsHandler(StopwordsHandler value)
/*  322:     */   {
/*  323: 700 */     if (value != null) {
/*  324: 701 */       this.m_stopwordsHandler = value;
/*  325:     */     } else {
/*  326: 703 */       this.m_stopwordsHandler = new Null();
/*  327:     */     }
/*  328:     */   }
/*  329:     */   
/*  330:     */   public String stopwordsHandlerTipText()
/*  331:     */   {
/*  332: 714 */     return "The stopwords handler to use (Null means no stopwords are used).";
/*  333:     */   }
/*  334:     */   
/*  335:     */   public Tokenizer getTokenizer()
/*  336:     */   {
/*  337: 723 */     return this.m_tokenizer;
/*  338:     */   }
/*  339:     */   
/*  340:     */   public void setTokenizer(Tokenizer value)
/*  341:     */   {
/*  342: 732 */     this.m_tokenizer = value;
/*  343:     */   }
/*  344:     */   
/*  345:     */   public String tokenizerTipText()
/*  346:     */   {
/*  347: 742 */     return "The tokenizing algorithm to use on the strings.";
/*  348:     */   }
/*  349:     */   
/*  350:     */   public Enumeration<Option> listOptions()
/*  351:     */   {
/*  352: 753 */     Vector<Option> result = new Vector();
/*  353:     */     
/*  354: 755 */     result.addElement(new Option("\tOutput word counts rather than boolean word presence.\n", "C", 0, "-C"));
/*  355:     */     
/*  356:     */ 
/*  357:     */ 
/*  358:     */ 
/*  359: 760 */     result.addElement(new Option("\tSpecify list of string attributes to convert to words (as weka Range).\n\t(default: select all string attributes)", "R", 1, "-R <index1,index2-index4,...>"));
/*  360:     */     
/*  361:     */ 
/*  362:     */ 
/*  363:     */ 
/*  364: 765 */     result.addElement(new Option("\tInvert matching sense of column indexes.", "V", 0, "-V"));
/*  365:     */     
/*  366:     */ 
/*  367: 768 */     result.addElement(new Option("\tSpecify a prefix for the created attribute names.\n\t(default: \"\")", "P", 1, "-P <attribute name prefix>"));
/*  368:     */     
/*  369:     */ 
/*  370:     */ 
/*  371: 772 */     result.addElement(new Option("\tSpecify approximate number of word fields to create.\n\tSurplus words will be discarded..\n\t(default: 1000)", "W", 1, "-W <number of words to keep>"));
/*  372:     */     
/*  373:     */ 
/*  374:     */ 
/*  375:     */ 
/*  376: 777 */     result.addElement(new Option("\tSpecify the rate (e.g., every x instances) at which to periodically prune the dictionary.\n\t-W prunes after creating a full dictionary. You may not have enough memory for this approach.\n\t(default: no periodic pruning)", "prune-rate", 1, "-prune-rate <every x instances>"));
/*  377:     */     
/*  378:     */ 
/*  379:     */ 
/*  380:     */ 
/*  381:     */ 
/*  382:     */ 
/*  383: 784 */     result.addElement(new Option("\tTransform the word frequencies into log(1+fij)\n\twhere fij is the frequency of word i in jth document(instance).\n", "T", 0, "-T"));
/*  384:     */     
/*  385:     */ 
/*  386:     */ 
/*  387:     */ 
/*  388:     */ 
/*  389: 790 */     result.addElement(new Option("\tTransform each word frequency into:\n\tfij*log(num of Documents/num of documents containing word i)\n\t  where fij if frequency of word i in jth document(instance)", "I", 0, "-I"));
/*  390:     */     
/*  391:     */ 
/*  392:     */ 
/*  393:     */ 
/*  394: 795 */     result.addElement(new Option("\tWhether to 0=not normalize/1=normalize all data/2=normalize test data only\n\tto average length of training documents (default 0=don't normalize).", "N", 1, "-N"));
/*  395:     */     
/*  396:     */ 
/*  397:     */ 
/*  398:     */ 
/*  399:     */ 
/*  400: 801 */     result.addElement(new Option("\tConvert all tokens to lowercase before adding to the dictionary.", "L", 0, "-L"));
/*  401:     */     
/*  402:     */ 
/*  403: 804 */     result.addElement(new Option("\tThe stopwords handler to use (default Null).", "-stopwords-handler", 1, "-stopwords-handler"));
/*  404:     */     
/*  405:     */ 
/*  406:     */ 
/*  407: 808 */     result.addElement(new Option("\tThe stemming algorithm (classname plus parameters) to use.", "stemmer", 1, "-stemmer <spec>"));
/*  408:     */     
/*  409:     */ 
/*  410:     */ 
/*  411: 812 */     result.addElement(new Option("\tThe minimum term frequency (default = 1).", "M", 1, "-M <int>"));
/*  412:     */     
/*  413:     */ 
/*  414: 815 */     result.addElement(new Option("\tIf this is set, the maximum number of words and the \n\tminimum term frequency is not enforced on a per-class \n\tbasis but based on the documents in all the classes \n\t(even if a class attribute is set).", "O", 0, "-O"));
/*  415:     */     
/*  416:     */ 
/*  417:     */ 
/*  418:     */ 
/*  419:     */ 
/*  420: 821 */     result.addElement(new Option("\tThe tokenizing algorihtm (classname plus parameters) to use.\n\t(default: " + WordTokenizer.class.getName() + ")", "tokenizer", 1, "-tokenizer <spec>"));
/*  421:     */     
/*  422:     */ 
/*  423:     */ 
/*  424:     */ 
/*  425: 826 */     return result.elements();
/*  426:     */   }
/*  427:     */   
/*  428:     */   public String[] getOptions()
/*  429:     */   {
/*  430: 836 */     Vector<String> result = new Vector();
/*  431:     */     
/*  432: 838 */     result.add("-R");
/*  433: 839 */     result.add(getSelectedRange().getRanges());
/*  434: 841 */     if (getInvertSelection()) {
/*  435: 842 */       result.add("-V");
/*  436:     */     }
/*  437: 845 */     if (!"".equals(getAttributeNamePrefix()))
/*  438:     */     {
/*  439: 846 */       result.add("-P");
/*  440: 847 */       result.add(getAttributeNamePrefix());
/*  441:     */     }
/*  442: 850 */     result.add("-W");
/*  443: 851 */     result.add(String.valueOf(getWordsToKeep()));
/*  444:     */     
/*  445: 853 */     result.add("-prune-rate");
/*  446: 854 */     result.add(String.valueOf(getPeriodicPruning()));
/*  447: 856 */     if (getOutputWordCounts()) {
/*  448: 857 */       result.add("-C");
/*  449:     */     }
/*  450: 860 */     if (getTFTransform()) {
/*  451: 861 */       result.add("-T");
/*  452:     */     }
/*  453: 864 */     if (getIDFTransform()) {
/*  454: 865 */       result.add("-I");
/*  455:     */     }
/*  456: 868 */     if (getNormalize()) {
/*  457: 869 */       result.add("-N");
/*  458:     */     }
/*  459: 872 */     if (getLowerCaseTokens()) {
/*  460: 873 */       result.add("-L");
/*  461:     */     }
/*  462: 876 */     if (getStemmer() != null)
/*  463:     */     {
/*  464: 877 */       result.add("-stemmer");
/*  465: 878 */       String spec = getStemmer().getClass().getName();
/*  466: 879 */       if ((getStemmer() instanceof OptionHandler)) {
/*  467: 880 */         spec = spec + " " + Utils.joinOptions(((OptionHandler)getStemmer()).getOptions());
/*  468:     */       }
/*  469: 883 */       result.add(spec.trim());
/*  470:     */     }
/*  471: 886 */     if (getStopwordsHandler() != null)
/*  472:     */     {
/*  473: 887 */       result.add("-stopwords-handler");
/*  474: 888 */       String spec = getStopwordsHandler().getClass().getName();
/*  475: 889 */       if ((getStopwordsHandler() instanceof OptionHandler)) {
/*  476: 890 */         spec = spec + " " + Utils.joinOptions(((OptionHandler)getStopwordsHandler()).getOptions());
/*  477:     */       }
/*  478: 895 */       result.add(spec.trim());
/*  479:     */     }
/*  480: 898 */     result.add("-M");
/*  481: 899 */     result.add(String.valueOf(getMinTermFreq()));
/*  482: 901 */     if (getDoNotOperateOnPerClassBasis()) {
/*  483: 902 */       result.add("-O");
/*  484:     */     }
/*  485: 905 */     result.add("-tokenizer");
/*  486: 906 */     String spec = getTokenizer().getClass().getName();
/*  487:     */     
/*  488: 908 */     spec = spec + " " + Utils.joinOptions(getTokenizer().getOptions());
/*  489:     */     
/*  490:     */ 
/*  491: 911 */     result.add(spec.trim());
/*  492:     */     
/*  493: 913 */     return (String[])result.toArray(new String[result.size()]);
/*  494:     */   }
/*  495:     */   
/*  496:     */   public void setOptions(String[] options)
/*  497:     */     throws Exception
/*  498:     */   {
/*  499:1019 */     String value = Utils.getOption('R', options);
/*  500:1020 */     if (value.length() != 0) {
/*  501:1021 */       setSelectedRange(value);
/*  502:     */     } else {
/*  503:1023 */       setSelectedRange("first-last");
/*  504:     */     }
/*  505:1026 */     setInvertSelection(Utils.getFlag('V', options));
/*  506:     */     
/*  507:1028 */     value = Utils.getOption('P', options);
/*  508:1029 */     if (value.length() != 0) {
/*  509:1030 */       setAttributeNamePrefix(value);
/*  510:     */     } else {
/*  511:1032 */       setAttributeNamePrefix("");
/*  512:     */     }
/*  513:1035 */     value = Utils.getOption('W', options);
/*  514:1036 */     if (value.length() != 0) {
/*  515:1037 */       setWordsToKeep(Integer.valueOf(value).intValue());
/*  516:     */     } else {
/*  517:1039 */       setWordsToKeep(1000);
/*  518:     */     }
/*  519:1042 */     value = Utils.getOption("prune-rate", options);
/*  520:1043 */     if (value.length() > 0) {
/*  521:1044 */       setPeriodicPruning(Integer.parseInt(value));
/*  522:     */     } else {
/*  523:1046 */       setPeriodicPruning(-1L);
/*  524:     */     }
/*  525:1049 */     value = Utils.getOption('M', options);
/*  526:1050 */     if (value.length() != 0) {
/*  527:1051 */       setMinTermFreq(Integer.valueOf(value).intValue());
/*  528:     */     } else {
/*  529:1053 */       setMinTermFreq(1);
/*  530:     */     }
/*  531:1056 */     setOutputWordCounts(Utils.getFlag('C', options));
/*  532:     */     
/*  533:1058 */     setTFTransform(Utils.getFlag('T', options));
/*  534:     */     
/*  535:1060 */     setIDFTransform(Utils.getFlag('I', options));
/*  536:     */     
/*  537:1062 */     setDoNotOperateOnPerClassBasis(Utils.getFlag('O', options));
/*  538:     */     
/*  539:1064 */     setNormalize(Utils.getFlag('N', options));
/*  540:     */     
/*  541:1066 */     setLowerCaseTokens(Utils.getFlag('L', options));
/*  542:     */     
/*  543:1068 */     String stemmerString = Utils.getOption("stemmer", options);
/*  544:1069 */     if (stemmerString.length() == 0)
/*  545:     */     {
/*  546:1070 */       setStemmer(null);
/*  547:     */     }
/*  548:     */     else
/*  549:     */     {
/*  550:1072 */       String[] stemmerSpec = Utils.splitOptions(stemmerString);
/*  551:1073 */       if (stemmerSpec.length == 0) {
/*  552:1074 */         throw new Exception("Invalid stemmer specification string");
/*  553:     */       }
/*  554:1076 */       String stemmerName = stemmerSpec[0];
/*  555:1077 */       stemmerSpec[0] = "";
/*  556:1078 */       Stemmer stemmer = (Stemmer)Utils.forName(Stemmer.class, stemmerName, stemmerSpec);
/*  557:     */       
/*  558:     */ 
/*  559:1081 */       setStemmer(stemmer);
/*  560:     */     }
/*  561:1084 */     String stopwordsHandlerString = Utils.getOption("stopwords-handler", options);
/*  562:1086 */     if (stopwordsHandlerString.length() == 0)
/*  563:     */     {
/*  564:1087 */       setStopwordsHandler(null);
/*  565:     */     }
/*  566:     */     else
/*  567:     */     {
/*  568:1089 */       String[] stopwordsHandlerSpec = Utils.splitOptions(stopwordsHandlerString);
/*  569:1091 */       if (stopwordsHandlerSpec.length == 0) {
/*  570:1092 */         throw new Exception("Invalid StopwordsHandler specification string");
/*  571:     */       }
/*  572:1094 */       String stopwordsHandlerName = stopwordsHandlerSpec[0];
/*  573:1095 */       stopwordsHandlerSpec[0] = "";
/*  574:1096 */       StopwordsHandler stopwordsHandler = (StopwordsHandler)Utils.forName(StopwordsHandler.class, stopwordsHandlerName, stopwordsHandlerSpec);
/*  575:     */       
/*  576:     */ 
/*  577:     */ 
/*  578:1100 */       setStopwordsHandler(stopwordsHandler);
/*  579:     */     }
/*  580:1103 */     String tokenizerString = Utils.getOption("tokenizer", options);
/*  581:1104 */     if (tokenizerString.length() == 0)
/*  582:     */     {
/*  583:1105 */       setTokenizer(new WordTokenizer());
/*  584:     */     }
/*  585:     */     else
/*  586:     */     {
/*  587:1107 */       String[] tokenizerSpec = Utils.splitOptions(tokenizerString);
/*  588:1108 */       if (tokenizerSpec.length == 0) {
/*  589:1109 */         throw new Exception("Invalid tokenizer specification string");
/*  590:     */       }
/*  591:1111 */       String tokenizerName = tokenizerSpec[0];
/*  592:1112 */       tokenizerSpec[0] = "";
/*  593:1113 */       Tokenizer tokenizer = (Tokenizer)Utils.forName(Tokenizer.class, tokenizerName, tokenizerSpec);
/*  594:     */       
/*  595:     */ 
/*  596:     */ 
/*  597:1117 */       setTokenizer(tokenizer);
/*  598:     */     }
/*  599:1120 */     Utils.checkForRemainingOptions(options);
/*  600:     */   }
/*  601:     */   
/*  602:     */   public void setup(Instances inputFormat)
/*  603:     */     throws Exception
/*  604:     */   {
/*  605:1126 */     this.m_inputContainsStringAttributes = inputFormat.checkForStringAttributes();
/*  606:1127 */     this.m_inputFormat = inputFormat.stringFreeStructure();
/*  607:1129 */     if (!this.m_inputContainsStringAttributes) {
/*  608:1130 */       return;
/*  609:     */     }
/*  610:1133 */     this.m_numClasses = ((!this.m_doNotOperateOnPerClassBasis) && (this.m_inputFormat.classIndex() >= 0) ? this.m_inputFormat.numClasses() : 1);
/*  611:     */     
/*  612:     */ 
/*  613:1136 */     this.m_dictsPerClass = (this.m_sortDictionary ? new TreeMap[this.m_numClasses] : new LinkedHashMap[this.m_numClasses]);
/*  614:     */     
/*  615:     */ 
/*  616:1139 */     this.m_classIndex = this.m_inputFormat.classIndex();
/*  617:1141 */     for (int i = 0; i < this.m_numClasses; i++) {
/*  618:1142 */       this.m_dictsPerClass[i] = (this.m_sortDictionary ? new TreeMap() : new LinkedHashMap());
/*  619:     */     }
/*  620:1147 */     determineSelectedRange(inputFormat);
/*  621:     */   }
/*  622:     */   
/*  623:     */   public Instances getInputFormat()
/*  624:     */   {
/*  625:1156 */     return this.m_inputFormat;
/*  626:     */   }
/*  627:     */   
/*  628:     */   public boolean readyToVectorize()
/*  629:     */   {
/*  630:1166 */     return (this.m_inputFormat != null) && (this.m_consolidatedDict != null);
/*  631:     */   }
/*  632:     */   
/*  633:     */   private void determineSelectedRange(Instances inputFormat)
/*  634:     */   {
/*  635:1175 */     if (this.m_selectedRange == null)
/*  636:     */     {
/*  637:1176 */       StringBuffer fields = new StringBuffer();
/*  638:1177 */       for (int j = 0; j < inputFormat.numAttributes(); j++) {
/*  639:1178 */         if (inputFormat.attribute(j).type() == 2) {
/*  640:1179 */           fields.append(j + 1 + ",");
/*  641:     */         }
/*  642:     */       }
/*  643:1182 */       this.m_selectedRange = new Range(fields.toString());
/*  644:     */     }
/*  645:1184 */     this.m_selectedRange.setUpper(inputFormat.numAttributes() - 1);
/*  646:     */     
/*  647:     */ 
/*  648:1187 */     StringBuffer fields = new StringBuffer();
/*  649:1188 */     for (int j = 0; j < inputFormat.numAttributes(); j++) {
/*  650:1189 */       if ((this.m_selectedRange.isInRange(j)) && (inputFormat.attribute(j).type() == 2)) {
/*  651:1191 */         fields.append(j + 1 + ",");
/*  652:     */       }
/*  653:     */     }
/*  654:1194 */     this.m_selectedRange.setRanges(fields.toString());
/*  655:1195 */     this.m_selectedRange.setUpper(inputFormat.numAttributes() - 1);
/*  656:     */   }
/*  657:     */   
/*  658:     */   public Instances getVectorizedFormat()
/*  659:     */     throws Exception
/*  660:     */   {
/*  661:1207 */     if (this.m_inputFormat == null) {
/*  662:1208 */       throw new Exception("No input format available. Call setup() and make sure a dictionary has been built first.");
/*  663:     */     }
/*  664:1212 */     if (!this.m_inputContainsStringAttributes) {
/*  665:1213 */       return this.m_inputFormat;
/*  666:     */     }
/*  667:1216 */     if (this.m_consolidatedDict == null) {
/*  668:1217 */       throw new Exception("Dictionary hasn't been built or finalized yet!");
/*  669:     */     }
/*  670:1220 */     if (this.m_outputFormat != null) {
/*  671:1221 */       return this.m_outputFormat;
/*  672:     */     }
/*  673:1224 */     ArrayList<Attribute> newAtts = new ArrayList();
/*  674:     */     
/*  675:1226 */     int classIndex = -1;
/*  676:1227 */     for (int i = 0; i < this.m_inputFormat.numAttributes(); i++) {
/*  677:1232 */       if (!this.m_selectedRange.isInRange(i))
/*  678:     */       {
/*  679:1233 */         if (this.m_inputFormat.classIndex() == i) {
/*  680:1234 */           classIndex = newAtts.size();
/*  681:     */         }
/*  682:1236 */         newAtts.add((Attribute)this.m_inputFormat.attribute(i).copy());
/*  683:     */       }
/*  684:     */     }
/*  685:1241 */     for (Map.Entry<String, int[]> e : this.m_consolidatedDict.entrySet()) {
/*  686:1242 */       newAtts.add(new Attribute(this.m_Prefix + (String)e.getKey()));
/*  687:     */     }
/*  688:1249 */     Instances newFormat = new Instances(this.m_inputFormat.relationName(), newAtts, 0);
/*  689:1253 */     if (classIndex >= 0) {
/*  690:1254 */       newFormat.setClassIndex(classIndex);
/*  691:     */     }
/*  692:1257 */     return newFormat;
/*  693:     */   }
/*  694:     */   
/*  695:     */   public Instances vectorizeBatch(Instances batch, boolean setAvgDocLength)
/*  696:     */     throws Exception
/*  697:     */   {
/*  698:1277 */     if (this.m_inputFormat == null) {
/*  699:1278 */       throw new Exception("No input format available. Call setup() and make sure a dictionary has been built first.");
/*  700:     */     }
/*  701:1282 */     if (!this.m_inputContainsStringAttributes) {
/*  702:1284 */       return batch;
/*  703:     */     }
/*  704:1287 */     if (this.m_consolidatedDict == null) {
/*  705:1288 */       throw new Exception("Dictionary hasn't been built or consolidated yet!");
/*  706:     */     }
/*  707:1291 */     Instances vectorized = new Instances(this.m_outputFormat, batch.numInstances());
/*  708:     */     
/*  709:1293 */     boolean normTemp = this.m_normalize;
/*  710:1294 */     if (setAvgDocLength) {
/*  711:1297 */       this.m_normalize = false;
/*  712:     */     }
/*  713:1300 */     if (batch.numInstances() > 0)
/*  714:     */     {
/*  715:1301 */       int[] offsetHolder = new int[1];
/*  716:1302 */       vectorized.add(vectorizeInstance(batch.instance(0), offsetHolder, true));
/*  717:1303 */       for (int i = 1; i < batch.numInstances(); i++) {
/*  718:1304 */         vectorized.add(vectorizeInstance(batch.instance(i), offsetHolder, true));
/*  719:     */       }
/*  720:1308 */       if (setAvgDocLength)
/*  721:     */       {
/*  722:1309 */         this.m_avgDocLength = 0.0D;
/*  723:1310 */         for (int i = 0; i < vectorized.numInstances(); i++)
/*  724:     */         {
/*  725:1311 */           Instance inst = vectorized.instance(i);
/*  726:1312 */           double docLength = 0.0D;
/*  727:1313 */           for (int j = 0; j < inst.numValues(); j++) {
/*  728:1314 */             if (inst.index(j) >= offsetHolder[0]) {
/*  729:1315 */               docLength += inst.valueSparse(j) * inst.valueSparse(j);
/*  730:     */             }
/*  731:     */           }
/*  732:1318 */           this.m_avgDocLength += Math.sqrt(docLength);
/*  733:     */         }
/*  734:1320 */         this.m_avgDocLength /= vectorized.numInstances();
/*  735:1322 */         if (normTemp) {
/*  736:1323 */           for (int i = 0; i < vectorized.numInstances(); i++) {
/*  737:1324 */             normalizeInstance(vectorized.instance(i), offsetHolder[0]);
/*  738:     */           }
/*  739:     */         }
/*  740:     */       }
/*  741:     */     }
/*  742:1330 */     this.m_normalize = normTemp;
/*  743:     */     
/*  744:1332 */     vectorized.compactify();
/*  745:1333 */     return vectorized;
/*  746:     */   }
/*  747:     */   
/*  748:     */   public Instance vectorizeInstance(Instance input)
/*  749:     */     throws Exception
/*  750:     */   {
/*  751:1347 */     return vectorizeInstance(input, new int[1], false);
/*  752:     */   }
/*  753:     */   
/*  754:     */   public Instance vectorizeInstance(Instance input, boolean retainStringAttValuesInMemory)
/*  755:     */     throws Exception
/*  756:     */   {
/*  757:1362 */     return vectorizeInstance(input, new int[1], retainStringAttValuesInMemory);
/*  758:     */   }
/*  759:     */   
/*  760:     */   private Instance vectorizeInstance(Instance input, int[] offsetHolder, boolean retainStringAttValuesInMemory)
/*  761:     */     throws Exception
/*  762:     */   {
/*  763:1368 */     if (!this.m_inputContainsStringAttributes) {
/*  764:1369 */       return input;
/*  765:     */     }
/*  766:1372 */     if (this.m_inputFormat == null) {
/*  767:1373 */       throw new Exception("No input format available. Call setup() and make sure a dictionary has been built first.");
/*  768:     */     }
/*  769:1377 */     if (this.m_consolidatedDict == null) {
/*  770:1378 */       throw new Exception("Dictionary hasn't been built or consolidated yet!");
/*  771:     */     }
/*  772:1381 */     int indexOffset = 0;
/*  773:1382 */     int classIndex = this.m_outputFormat.classIndex();
/*  774:1383 */     Map<Integer, double[]> contained = new TreeMap();
/*  775:1384 */     for (int i = 0; i < this.m_inputFormat.numAttributes(); i++) {
/*  776:1385 */       if (!this.m_selectedRange.isInRange(i))
/*  777:     */       {
/*  778:1386 */         if ((!this.m_inputFormat.attribute(i).isString()) && (!this.m_inputFormat.attribute(i).isRelationValued()))
/*  779:     */         {
/*  780:1390 */           if (input.value(i) != 0.0D) {
/*  781:1391 */             contained.put(Integer.valueOf(indexOffset), new double[] { input.value(i) });
/*  782:     */           }
/*  783:     */         }
/*  784:1394 */         else if (input.isMissing(i))
/*  785:     */         {
/*  786:1395 */           contained.put(Integer.valueOf(indexOffset), new double[] { Utils.missingValue() });
/*  787:     */         }
/*  788:1396 */         else if (this.m_inputFormat.attribute(i).isString())
/*  789:     */         {
/*  790:1397 */           String strVal = input.stringValue(i);
/*  791:1398 */           if (retainStringAttValuesInMemory)
/*  792:     */           {
/*  793:1399 */             double strIndex = this.m_outputFormat.attribute(indexOffset).addStringValue(strVal);
/*  794:     */             
/*  795:1401 */             contained.put(Integer.valueOf(indexOffset), new double[] { strIndex });
/*  796:     */           }
/*  797:     */           else
/*  798:     */           {
/*  799:1403 */             this.m_outputFormat.attribute(indexOffset).setStringValue(strVal);
/*  800:1404 */             contained.put(Integer.valueOf(indexOffset), new double[] { 0.0D });
/*  801:     */           }
/*  802:     */         }
/*  803:     */         else
/*  804:     */         {
/*  805:1408 */           if (this.m_outputFormat.attribute(indexOffset).numValues() == 0)
/*  806:     */           {
/*  807:1409 */             Instances relationalHeader = this.m_outputFormat.attribute(indexOffset).relation();
/*  808:     */             
/*  809:     */ 
/*  810:     */ 
/*  811:1413 */             this.m_outputFormat.attribute(indexOffset).addRelation(relationalHeader);
/*  812:     */           }
/*  813:1416 */           int newIndex = this.m_outputFormat.attribute(indexOffset).addRelation(input.relationalValue(i));
/*  814:     */           
/*  815:     */ 
/*  816:1419 */           contained.put(Integer.valueOf(indexOffset), new double[] { newIndex });
/*  817:     */         }
/*  818:1422 */         indexOffset++;
/*  819:     */       }
/*  820:     */     }
/*  821:1426 */     offsetHolder[0] = indexOffset;
/*  822:1429 */     for (int i = 0; i < this.m_inputFormat.numAttributes(); i++) {
/*  823:1430 */       if ((this.m_selectedRange.isInRange(i)) && (!input.isMissing(i)))
/*  824:     */       {
/*  825:1431 */         this.m_tokenizer.tokenize(input.stringValue(i));
/*  826:1433 */         while (this.m_tokenizer.hasMoreElements())
/*  827:     */         {
/*  828:1434 */           String word = this.m_tokenizer.nextElement();
/*  829:1435 */           if (this.m_lowerCaseTokens) {
/*  830:1436 */             word = word.toLowerCase();
/*  831:     */           }
/*  832:1438 */           word = this.m_stemmer.stem(word);
/*  833:     */           
/*  834:1440 */           int[] idxAndDocCount = (int[])this.m_consolidatedDict.get(word);
/*  835:1441 */           if (idxAndDocCount != null) {
/*  836:1442 */             if (this.m_outputCounts)
/*  837:     */             {
/*  838:1443 */               double[] inputCount = (double[])contained.get(Integer.valueOf(idxAndDocCount[0] + indexOffset));
/*  839:1445 */               if (inputCount != null) {
/*  840:1446 */                 inputCount[0] += 1.0D;
/*  841:     */               } else {
/*  842:1448 */                 contained.put(Integer.valueOf(idxAndDocCount[0] + indexOffset), new double[] { 1.0D });
/*  843:     */               }
/*  844:     */             }
/*  845:     */             else
/*  846:     */             {
/*  847:1452 */               contained.put(Integer.valueOf(idxAndDocCount[0] + indexOffset), new double[] { 1.0D });
/*  848:     */             }
/*  849:     */           }
/*  850:     */         }
/*  851:     */       }
/*  852:     */     }
/*  853:1461 */     if (this.m_TFTransform) {
/*  854:1462 */       for (Map.Entry<Integer, double[]> e : contained.entrySet())
/*  855:     */       {
/*  856:1463 */         int index = ((Integer)e.getKey()).intValue();
/*  857:1464 */         if (index >= indexOffset)
/*  858:     */         {
/*  859:1465 */           double[] val = (double[])e.getValue();
/*  860:1466 */           val[0] = Math.log(val[0] + 1.0D);
/*  861:     */         }
/*  862:     */       }
/*  863:     */     }
/*  864:1472 */     if (this.m_IDFTransform) {
/*  865:1473 */       for (Map.Entry<Integer, double[]> e : contained.entrySet())
/*  866:     */       {
/*  867:1474 */         int index = ((Integer)e.getKey()).intValue();
/*  868:1475 */         if (index >= indexOffset)
/*  869:     */         {
/*  870:1476 */           double[] val = (double[])e.getValue();
/*  871:1477 */           String word = this.m_outputFormat.attribute(index).name();
/*  872:1478 */           int[] idxAndDocCount = (int[])this.m_consolidatedDict.get(word);
/*  873:1479 */           if (idxAndDocCount == null) {
/*  874:1480 */             throw new Exception("This should never occur");
/*  875:     */           }
/*  876:1482 */           if (idxAndDocCount.length != 2) {
/*  877:1483 */             throw new Exception("Can't compute IDF transform as document counts are not available");
/*  878:     */           }
/*  879:1486 */           val[0] *= Math.log(this.m_count / idxAndDocCount[1]);
/*  880:     */         }
/*  881:     */       }
/*  882:     */     }
/*  883:1491 */     double[] values = new double[contained.size()];
/*  884:1492 */     int[] indices = new int[contained.size()];
/*  885:1493 */     int i = 0;
/*  886:1494 */     for (Map.Entry<Integer, double[]> e : contained.entrySet())
/*  887:     */     {
/*  888:1495 */       values[i] = ((double[])e.getValue())[0];
/*  889:1496 */       indices[(i++)] = ((Integer)e.getKey()).intValue();
/*  890:     */     }
/*  891:1499 */     Instance inst = new SparseInstance(input.weight(), values, indices, this.m_outputFormat.numAttributes());
/*  892:     */     
/*  893:     */ 
/*  894:1502 */     inst.setDataset(this.m_outputFormat);
/*  895:1504 */     if (this.m_normalize) {
/*  896:1505 */       normalizeInstance(inst, indexOffset);
/*  897:     */     }
/*  898:1508 */     return inst;
/*  899:     */   }
/*  900:     */   
/*  901:     */   private void normalizeInstance(Instance inst, int offset)
/*  902:     */     throws Exception
/*  903:     */   {
/*  904:1520 */     if (this.m_avgDocLength <= 0.0D) {
/*  905:1521 */       throw new Exception("Average document length is not set!");
/*  906:     */     }
/*  907:1524 */     double docLength = 0.0D;
/*  908:1527 */     for (int i = 0; i < inst.numValues(); i++) {
/*  909:1528 */       if ((inst.index(i) >= offset) && (inst.index(i) != this.m_outputFormat.classIndex())) {
/*  910:1530 */         docLength += inst.valueSparse(i) * inst.valueSparse(i);
/*  911:     */       }
/*  912:     */     }
/*  913:1533 */     docLength = Math.sqrt(docLength);
/*  914:1536 */     for (int i = 0; i < inst.numValues(); i++) {
/*  915:1537 */       if ((inst.index(i) >= offset) && (inst.index(i) != this.m_outputFormat.classIndex()))
/*  916:     */       {
/*  917:1539 */         double val = inst.valueSparse(i) * this.m_avgDocLength / docLength;
/*  918:1540 */         inst.setValueSparse(i, val);
/*  919:1541 */         if (val == 0.0D)
/*  920:     */         {
/*  921:1542 */           System.err.println("setting value " + inst.index(i) + " to zero.");
/*  922:1543 */           i--;
/*  923:     */         }
/*  924:     */       }
/*  925:     */     }
/*  926:     */   }
/*  927:     */   
/*  928:     */   public void processInstance(Instance inst)
/*  929:     */   {
/*  930:1557 */     if (!this.m_inputContainsStringAttributes) {
/*  931:1558 */       return;
/*  932:     */     }
/*  933:1561 */     if (this.m_inputVector == null) {
/*  934:1562 */       this.m_inputVector = new LinkedHashMap();
/*  935:     */     } else {
/*  936:1564 */       this.m_inputVector.clear();
/*  937:     */     }
/*  938:1567 */     int dIndex = 0;
/*  939:1568 */     if ((!this.m_doNotOperateOnPerClassBasis) && (this.m_classIndex >= 0)) {
/*  940:1569 */       if (!inst.classIsMissing()) {
/*  941:1570 */         dIndex = (int)inst.classValue();
/*  942:     */       } else {
/*  943:1572 */         return;
/*  944:     */       }
/*  945:     */     }
/*  946:1576 */     for (int j = 0; j < inst.numAttributes(); j++) {
/*  947:1577 */       if ((this.m_selectedRange.isInRange(j)) && (!inst.isMissing(j)))
/*  948:     */       {
/*  949:1578 */         this.m_tokenizer.tokenize(inst.stringValue(j));
/*  950:1580 */         while (this.m_tokenizer.hasMoreElements())
/*  951:     */         {
/*  952:1581 */           String word = this.m_tokenizer.nextElement();
/*  953:1583 */           if (this.m_lowerCaseTokens) {
/*  954:1584 */             word = word.toLowerCase();
/*  955:     */           }
/*  956:1586 */           word = this.m_stemmer.stem(word);
/*  957:1587 */           if (!this.m_stopwordsHandler.isStopword(word))
/*  958:     */           {
/*  959:1591 */             int[] counts = (int[])this.m_inputVector.get(word);
/*  960:1592 */             if (counts == null)
/*  961:     */             {
/*  962:1593 */               counts = new int[2];
/*  963:1594 */               counts[0] = 1;
/*  964:1595 */               counts[1] = 1;
/*  965:1596 */               this.m_inputVector.put(word, counts);
/*  966:     */             }
/*  967:     */             else
/*  968:     */             {
/*  969:1598 */               counts[0] += 1;
/*  970:     */             }
/*  971:     */           }
/*  972:     */         }
/*  973:     */       }
/*  974:     */     }
/*  975:1606 */     double docLength = 0.0D;
/*  976:1607 */     for (Map.Entry<String, int[]> e : this.m_inputVector.entrySet())
/*  977:     */     {
/*  978:1608 */       int[] dictCounts = (int[])this.m_dictsPerClass[dIndex].get(e.getKey());
/*  979:1609 */       if (dictCounts == null)
/*  980:     */       {
/*  981:1610 */         dictCounts = new int[2];
/*  982:1611 */         this.m_dictsPerClass[dIndex].put(e.getKey(), dictCounts);
/*  983:     */       }
/*  984:1613 */       dictCounts[0] += ((int[])e.getValue())[0];
/*  985:1614 */       dictCounts[1] += ((int[])e.getValue())[1];
/*  986:1615 */       docLength += ((int[])e.getValue())[0] * ((int[])e.getValue())[0];
/*  987:     */     }
/*  988:1617 */     if (this.m_normalize) {
/*  989:1621 */       this.m_docLengthSum += Math.sqrt(docLength);
/*  990:     */     }
/*  991:1624 */     this.m_count += 1;
/*  992:     */     
/*  993:1626 */     pruneDictionary();
/*  994:     */   }
/*  995:     */   
/*  996:     */   protected void pruneDictionary()
/*  997:     */   {
/*  998:1633 */     if ((this.m_periodicPruneRate > 0L) && (this.m_count % this.m_periodicPruneRate == 0L)) {
/*  999:1634 */       for (Map<String, int[]> m_dictsPerClas : this.m_dictsPerClass)
/* 1000:     */       {
/* 1001:1635 */         Iterator<Map.Entry<String, int[]>> entries = m_dictsPerClas.entrySet().iterator();
/* 1002:1637 */         while (entries.hasNext())
/* 1003:     */         {
/* 1004:1638 */           Map.Entry<String, int[]> entry = (Map.Entry)entries.next();
/* 1005:1639 */           if (((int[])entry.getValue())[0] < this.m_minFrequency) {
/* 1006:1640 */             entries.remove();
/* 1007:     */           }
/* 1008:     */         }
/* 1009:     */       }
/* 1010:     */     }
/* 1011:     */   }
/* 1012:     */   
/* 1013:     */   public void reset()
/* 1014:     */   {
/* 1015:1651 */     this.m_dictsPerClass = null;
/* 1016:1652 */     this.m_count = 0;
/* 1017:1653 */     this.m_docLengthSum = 0.0D;
/* 1018:1654 */     this.m_avgDocLength = 0.0D;
/* 1019:1655 */     this.m_inputFormat = null;
/* 1020:1656 */     this.m_outputFormat = null;
/* 1021:1657 */     this.m_consolidatedDict = null;
/* 1022:     */   }
/* 1023:     */   
/* 1024:     */   public Map<String, int[]>[] getDictionaries(boolean minFrequencyPrune)
/* 1025:     */     throws WekaException
/* 1026:     */   {
/* 1027:1674 */     if (this.m_dictsPerClass == null) {
/* 1028:1675 */       throw new WekaException("No dictionaries have been built yet!");
/* 1029:     */     }
/* 1030:1678 */     if (minFrequencyPrune) {
/* 1031:1679 */       pruneDictionary();
/* 1032:     */     }
/* 1033:1682 */     return this.m_dictsPerClass;
/* 1034:     */   }
/* 1035:     */   
/* 1036:     */   public DictionaryBuilder aggregate(DictionaryBuilder toAgg)
/* 1037:     */     throws Exception
/* 1038:     */   {
/* 1039:1687 */     Map<String, int[]>[] toAggDicts = toAgg.getDictionaries(false);
/* 1040:1689 */     if (toAggDicts.length != this.m_dictsPerClass.length) {
/* 1041:1690 */       throw new Exception("Number of dictionaries from the builder to be aggregated does not match our number of dictionaries");
/* 1042:     */     }
/* 1043:1695 */     for (int i = 0; i < toAggDicts.length; i++)
/* 1044:     */     {
/* 1045:1696 */       Map<String, int[]> toAggDictForClass = toAggDicts[i];
/* 1046:1697 */       for (Map.Entry<String, int[]> e : toAggDictForClass.entrySet())
/* 1047:     */       {
/* 1048:1698 */         int[] ourCounts = (int[])this.m_dictsPerClass[i].get(e.getKey());
/* 1049:1699 */         if (ourCounts == null)
/* 1050:     */         {
/* 1051:1700 */           ourCounts = new int[2];
/* 1052:1701 */           this.m_dictsPerClass[i].put(e.getKey(), ourCounts);
/* 1053:     */         }
/* 1054:1703 */         ourCounts[0] += ((int[])e.getValue())[0];
/* 1055:1704 */         ourCounts[1] += ((int[])e.getValue())[1];
/* 1056:     */       }
/* 1057:     */     }
/* 1058:1708 */     this.m_count += toAgg.m_count;
/* 1059:1709 */     this.m_docLengthSum += toAgg.m_docLengthSum;
/* 1060:     */     
/* 1061:1711 */     return this;
/* 1062:     */   }
/* 1063:     */   
/* 1064:     */   public void finalizeAggregation()
/* 1065:     */     throws Exception
/* 1066:     */   {
/* 1067:1716 */     finalizeDictionary();
/* 1068:     */   }
/* 1069:     */   
/* 1070:     */   public Map<String, int[]> finalizeDictionary()
/* 1071:     */     throws Exception
/* 1072:     */   {
/* 1073:1732 */     if (!this.m_inputContainsStringAttributes) {
/* 1074:1733 */       return null;
/* 1075:     */     }
/* 1076:1738 */     if (this.m_consolidatedDict != null) {
/* 1077:1739 */       return this.m_consolidatedDict;
/* 1078:     */     }
/* 1079:1742 */     if (this.m_dictsPerClass == null)
/* 1080:     */     {
/* 1081:1743 */       System.err.println(hashCode());
/* 1082:1744 */       throw new WekaException("No dictionary built yet!");
/* 1083:     */     }
/* 1084:1747 */     int[] prune = new int[this.m_dictsPerClass.length];
/* 1085:1748 */     for (int z = 0; z < prune.length; z++)
/* 1086:     */     {
/* 1087:1749 */       int[] array = new int[this.m_dictsPerClass[z].size()];
/* 1088:1750 */       int index = 0;
/* 1089:1751 */       for (Map.Entry<String, int[]> e : this.m_dictsPerClass[z].entrySet()) {
/* 1090:1752 */         array[(index++)] = ((int[])e.getValue())[0];
/* 1091:     */       }
/* 1092:1755 */       if (array.length < this.m_wordsToKeep)
/* 1093:     */       {
/* 1094:1756 */         prune[z] = this.m_minFrequency;
/* 1095:     */       }
/* 1096:     */       else
/* 1097:     */       {
/* 1098:1758 */         Arrays.sort(array);
/* 1099:1759 */         prune[z] = Math.max(this.m_minFrequency, array[(array.length - this.m_wordsToKeep)]);
/* 1100:     */       }
/* 1101:     */     }
/* 1102:1765 */     Map<String, int[]> consolidated = new LinkedHashMap();
/* 1103:1766 */     int index = 0;
/* 1104:1767 */     for (int z = 0; z < prune.length; z++) {
/* 1105:1768 */       for (Map.Entry<String, int[]> e : this.m_dictsPerClass[z].entrySet()) {
/* 1106:1769 */         if (((int[])e.getValue())[0] >= prune[z])
/* 1107:     */         {
/* 1108:1770 */           int[] counts = (int[])consolidated.get(e.getKey());
/* 1109:1771 */           if (counts == null)
/* 1110:     */           {
/* 1111:1772 */             counts = new int[2];
/* 1112:1773 */             counts[0] = (index++);
/* 1113:1774 */             consolidated.put(e.getKey(), counts);
/* 1114:     */           }
/* 1115:1777 */           counts[1] += ((int[])e.getValue())[1];
/* 1116:     */         }
/* 1117:     */       }
/* 1118:     */     }
/* 1119:1782 */     this.m_consolidatedDict = consolidated;
/* 1120:1783 */     this.m_dictsPerClass = null;
/* 1121:1785 */     if (this.m_normalize) {
/* 1122:1786 */       this.m_avgDocLength = (this.m_docLengthSum / this.m_count);
/* 1123:     */     }
/* 1124:1789 */     this.m_outputFormat = getVectorizedFormat();
/* 1125:     */     
/* 1126:1791 */     return this.m_consolidatedDict;
/* 1127:     */   }
/* 1128:     */   
/* 1129:     */   public void loadDictionary(String filename, boolean plainText)
/* 1130:     */     throws IOException
/* 1131:     */   {
/* 1132:1803 */     loadDictionary(new File(filename), plainText);
/* 1133:     */   }
/* 1134:     */   
/* 1135:     */   public void loadDictionary(File toLoad, boolean plainText)
/* 1136:     */     throws IOException
/* 1137:     */   {
/* 1138:1814 */     if (plainText) {
/* 1139:1815 */       loadDictionary(new FileReader(toLoad));
/* 1140:     */     } else {
/* 1141:1817 */       loadDictionary(new FileInputStream(toLoad));
/* 1142:     */     }
/* 1143:     */   }
/* 1144:     */   
/* 1145:     */   public void loadDictionary(Reader reader)
/* 1146:     */     throws IOException
/* 1147:     */   {
/* 1148:1828 */     BufferedReader br = new BufferedReader(reader);
/* 1149:1829 */     this.m_consolidatedDict = new LinkedHashMap();
/* 1150:     */     try
/* 1151:     */     {
/* 1152:1832 */       String line = br.readLine();
/* 1153:1833 */       int index = 0;
/* 1154:1834 */       if (line != null)
/* 1155:     */       {
/* 1156:1835 */         if ((line.startsWith("@@@")) && (line.endsWith("@@@")))
/* 1157:     */         {
/* 1158:1836 */           String avgS = line.replace("@@@", "");
/* 1159:     */           try
/* 1160:     */           {
/* 1161:1838 */             this.m_avgDocLength = Double.parseDouble(avgS);
/* 1162:     */           }
/* 1163:     */           catch (NumberFormatException ex)
/* 1164:     */           {
/* 1165:1840 */             System.err.println("Unable to parse average document length '" + avgS + "'");
/* 1166:     */           }
/* 1167:1843 */           line = br.readLine();
/* 1168:1844 */           if (line == null) {
/* 1169:1845 */             throw new IOException("Empty dictionary file!");
/* 1170:     */           }
/* 1171:     */         }
/* 1172:1849 */         boolean hasDocCounts = false;
/* 1173:1850 */         if (line.lastIndexOf(",") > 0)
/* 1174:     */         {
/* 1175:1851 */           String countS = line.substring(line.lastIndexOf(",") + 1, line.length()).trim();
/* 1176:     */           try
/* 1177:     */           {
/* 1178:1854 */             int dCount = Integer.parseInt(countS);
/* 1179:1855 */             hasDocCounts = true;
/* 1180:1856 */             int[] holder = new int[2];
/* 1181:1857 */             holder[1] = dCount;
/* 1182:1858 */             holder[0] = (index++);
/* 1183:1859 */             this.m_consolidatedDict.put(line.substring(0, line.lastIndexOf(",")), holder);
/* 1184:     */           }
/* 1185:     */           catch (NumberFormatException ex) {}
/* 1186:     */         }
/* 1187:1866 */         while ((line = br.readLine()) != null)
/* 1188:     */         {
/* 1189:1867 */           int[] holder = new int[hasDocCounts ? 2 : 1];
/* 1190:1868 */           holder[0] = (index++);
/* 1191:1869 */           if (hasDocCounts)
/* 1192:     */           {
/* 1193:1870 */             String countS = line.substring(line.lastIndexOf(",") + 1, line.length()).trim();
/* 1194:     */             
/* 1195:1872 */             line = line.substring(0, line.lastIndexOf(","));
/* 1196:     */             try
/* 1197:     */             {
/* 1198:1874 */               int dCount = Integer.parseInt(countS);
/* 1199:1875 */               holder[1] = dCount;
/* 1200:     */             }
/* 1201:     */             catch (NumberFormatException e)
/* 1202:     */             {
/* 1203:1877 */               throw new IOException(e);
/* 1204:     */             }
/* 1205:     */           }
/* 1206:1880 */           this.m_consolidatedDict.put(line, holder);
/* 1207:     */         }
/* 1208:     */       }
/* 1209:     */       else
/* 1210:     */       {
/* 1211:1883 */         throw new IOException("Empty dictionary file!");
/* 1212:     */       }
/* 1213:     */     }
/* 1214:     */     finally
/* 1215:     */     {
/* 1216:1886 */       br.close();
/* 1217:     */     }
/* 1218:     */     try
/* 1219:     */     {
/* 1220:1889 */       this.m_outputFormat = getVectorizedFormat();
/* 1221:     */     }
/* 1222:     */     catch (Exception ex)
/* 1223:     */     {
/* 1224:1891 */       throw new IOException(ex);
/* 1225:     */     }
/* 1226:     */   }
/* 1227:     */   
/* 1228:     */   public void loadDictionary(InputStream is)
/* 1229:     */     throws IOException
/* 1230:     */   {
/* 1231:1903 */     ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(is));
/* 1232:     */     try
/* 1233:     */     {
/* 1234:1905 */       List<Object> holder = (List)ois.readObject();
/* 1235:1906 */       this.m_avgDocLength = ((Double)holder.get(0)).doubleValue();
/* 1236:1907 */       this.m_consolidatedDict = ((Map)holder.get(1));
/* 1237:     */     }
/* 1238:     */     catch (ClassNotFoundException ex)
/* 1239:     */     {
/* 1240:1909 */       throw new IOException(ex);
/* 1241:     */     }
/* 1242:     */     finally
/* 1243:     */     {
/* 1244:1911 */       ois.close();
/* 1245:     */     }
/* 1246:     */   }
/* 1247:     */   
/* 1248:     */   public void saveDictionary(String filename, boolean plainText)
/* 1249:     */     throws IOException
/* 1250:     */   {
/* 1251:1924 */     saveDictionary(new File(filename), plainText);
/* 1252:     */   }
/* 1253:     */   
/* 1254:     */   public void saveDictionary(File toSave, boolean plainText)
/* 1255:     */     throws IOException
/* 1256:     */   {
/* 1257:1935 */     if (plainText) {
/* 1258:1936 */       saveDictionary(new FileWriter(toSave));
/* 1259:     */     } else {
/* 1260:1938 */       saveDictionary(new FileOutputStream(toSave));
/* 1261:     */     }
/* 1262:     */   }
/* 1263:     */   
/* 1264:     */   public void saveDictionary(Writer writer)
/* 1265:     */     throws IOException
/* 1266:     */   {
/* 1267:1949 */     if (!this.m_inputContainsStringAttributes) {
/* 1268:1950 */       throw new IOException("Input did not contain any string attributes!");
/* 1269:     */     }
/* 1270:1953 */     if (this.m_consolidatedDict == null) {
/* 1271:1954 */       throw new IOException("No dictionary to save!");
/* 1272:     */     }
/* 1273:1957 */     BufferedWriter br = new BufferedWriter(writer);
/* 1274:     */     try
/* 1275:     */     {
/* 1276:1959 */       if (this.m_avgDocLength > 0.0D) {
/* 1277:1960 */         br.write("@@@" + this.m_avgDocLength + "@@@\n");
/* 1278:     */       }
/* 1279:1962 */       for (Map.Entry<String, int[]> e : this.m_consolidatedDict.entrySet())
/* 1280:     */       {
/* 1281:1963 */         int[] v = (int[])e.getValue();
/* 1282:1964 */         br.write((String)e.getKey() + "," + (v.length > 1 ? Integer.valueOf(v[1]) : "") + "\n");
/* 1283:     */       }
/* 1284:     */     }
/* 1285:     */     finally
/* 1286:     */     {
/* 1287:1967 */       br.flush();
/* 1288:1968 */       br.close();
/* 1289:     */     }
/* 1290:     */   }
/* 1291:     */   
/* 1292:     */   public void saveDictionary(OutputStream os)
/* 1293:     */     throws IOException
/* 1294:     */   {
/* 1295:1980 */     if (!this.m_inputContainsStringAttributes) {
/* 1296:1981 */       throw new IOException("Input did not contain any string attributes!");
/* 1297:     */     }
/* 1298:1984 */     if (this.m_consolidatedDict == null) {
/* 1299:1985 */       throw new IOException("No dictionary to save!");
/* 1300:     */     }
/* 1301:1987 */     ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(os));
/* 1302:     */     
/* 1303:1989 */     List<Object> holder = new ArrayList();
/* 1304:1990 */     holder.add(Double.valueOf(this.m_avgDocLength));
/* 1305:1991 */     holder.add(this.m_consolidatedDict);
/* 1306:     */     try
/* 1307:     */     {
/* 1308:1993 */       oos.writeObject(holder);
/* 1309:     */     }
/* 1310:     */     finally
/* 1311:     */     {
/* 1312:1995 */       oos.flush();
/* 1313:1996 */       oos.close();
/* 1314:     */     }
/* 1315:     */   }
/* 1316:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.DictionaryBuilder
 * JD-Core Version:    0.7.0.1
 */