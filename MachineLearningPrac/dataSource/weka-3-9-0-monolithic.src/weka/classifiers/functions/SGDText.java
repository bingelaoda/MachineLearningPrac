/*    1:     */ package weka.classifiers.functions;
/*    2:     */ 
/*    3:     */ import java.io.Serializable;
/*    4:     */ import java.util.ArrayList;
/*    5:     */ import java.util.Collections;
/*    6:     */ import java.util.Enumeration;
/*    7:     */ import java.util.Iterator;
/*    8:     */ import java.util.LinkedHashMap;
/*    9:     */ import java.util.Map;
/*   10:     */ import java.util.Map.Entry;
/*   11:     */ import java.util.Random;
/*   12:     */ import java.util.Set;
/*   13:     */ import java.util.Vector;
/*   14:     */ import weka.classifiers.RandomizableClassifier;
/*   15:     */ import weka.classifiers.UpdateableBatchProcessor;
/*   16:     */ import weka.classifiers.UpdateableClassifier;
/*   17:     */ import weka.core.Aggregateable;
/*   18:     */ import weka.core.Attribute;
/*   19:     */ import weka.core.Capabilities;
/*   20:     */ import weka.core.Capabilities.Capability;
/*   21:     */ import weka.core.DenseInstance;
/*   22:     */ import weka.core.Instance;
/*   23:     */ import weka.core.Instances;
/*   24:     */ import weka.core.Option;
/*   25:     */ import weka.core.OptionHandler;
/*   26:     */ import weka.core.RevisionUtils;
/*   27:     */ import weka.core.SelectedTag;
/*   28:     */ import weka.core.Tag;
/*   29:     */ import weka.core.Utils;
/*   30:     */ import weka.core.WeightedInstancesHandler;
/*   31:     */ import weka.core.stemmers.NullStemmer;
/*   32:     */ import weka.core.stemmers.Stemmer;
/*   33:     */ import weka.core.stopwords.Null;
/*   34:     */ import weka.core.stopwords.StopwordsHandler;
/*   35:     */ import weka.core.tokenizers.Tokenizer;
/*   36:     */ import weka.core.tokenizers.WordTokenizer;
/*   37:     */ 
/*   38:     */ public class SGDText
/*   39:     */   extends RandomizableClassifier
/*   40:     */   implements UpdateableClassifier, UpdateableBatchProcessor, WeightedInstancesHandler, Aggregateable<SGDText>
/*   41:     */ {
/*   42:     */   private static final long serialVersionUID = 7200171484002029584L;
/*   43:     */   
/*   44:     */   public static class Count
/*   45:     */     implements Serializable
/*   46:     */   {
/*   47:     */     private static final long serialVersionUID = 2104201532017340967L;
/*   48:     */     public double m_count;
/*   49:     */     public double m_weight;
/*   50:     */     
/*   51:     */     public Count(double c)
/*   52:     */     {
/*   53: 160 */       this.m_count = c;
/*   54:     */     }
/*   55:     */   }
/*   56:     */   
/*   57: 169 */   protected int m_periodicP = 0;
/*   58: 175 */   protected double m_minWordP = 3.0D;
/*   59: 180 */   protected double m_minAbsCoefficient = 0.001D;
/*   60: 183 */   protected boolean m_wordFrequencies = false;
/*   61: 186 */   protected boolean m_normalize = false;
/*   62: 189 */   protected double m_norm = 1.0D;
/*   63: 192 */   protected double m_lnorm = 2.0D;
/*   64:     */   protected LinkedHashMap<String, Count> m_dictionary;
/*   65: 198 */   protected StopwordsHandler m_StopwordsHandler = new Null();
/*   66: 201 */   protected Tokenizer m_tokenizer = new WordTokenizer();
/*   67:     */   protected boolean m_lowercaseTokens;
/*   68: 207 */   protected Stemmer m_stemmer = new NullStemmer();
/*   69: 210 */   protected double m_lambda = 0.0001D;
/*   70: 213 */   protected double m_learningRate = 0.01D;
/*   71:     */   protected double m_t;
/*   72:     */   protected double m_bias;
/*   73:     */   protected double m_numInstances;
/*   74:     */   protected Instances m_data;
/*   75: 231 */   protected int m_epochs = 500;
/*   76:     */   protected transient LinkedHashMap<String, Count> m_inputVector;
/*   77:     */   public static final int HINGE = 0;
/*   78:     */   public static final int LOGLOSS = 1;
/*   79: 246 */   protected int m_loss = 0;
/*   80: 249 */   public static final Tag[] TAGS_SELECTION = { new Tag(0, "Hinge loss (SVM)"), new Tag(1, "Log loss (logistic regression)") };
/*   81:     */   protected SGD m_svmProbs;
/*   82: 260 */   protected boolean m_fitLogistic = false;
/*   83:     */   protected Instances m_fitLogisticStructure;
/*   84:     */   
/*   85:     */   protected double dloss(double z)
/*   86:     */   {
/*   87: 264 */     if (this.m_loss == 0) {
/*   88: 265 */       return z < 1.0D ? 1.0D : 0.0D;
/*   89:     */     }
/*   90: 268 */     if (z < 0.0D) {
/*   91: 269 */       return 1.0D / (Math.exp(z) + 1.0D);
/*   92:     */     }
/*   93: 271 */     double t = Math.exp(-z);
/*   94: 272 */     return t / (t + 1.0D);
/*   95:     */   }
/*   96:     */   
/*   97:     */   public Capabilities getCapabilities()
/*   98:     */   {
/*   99: 284 */     Capabilities result = super.getCapabilities();
/*  100: 285 */     result.disableAll();
/*  101:     */     
/*  102:     */ 
/*  103: 288 */     result.enable(Capabilities.Capability.STRING_ATTRIBUTES);
/*  104: 289 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  105: 290 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  106: 291 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  107: 292 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  108:     */     
/*  109: 294 */     result.enable(Capabilities.Capability.BINARY_CLASS);
/*  110: 295 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  111:     */     
/*  112:     */ 
/*  113: 298 */     result.setMinimumNumberInstances(0);
/*  114:     */     
/*  115: 300 */     return result;
/*  116:     */   }
/*  117:     */   
/*  118:     */   public void setStemmer(Stemmer value)
/*  119:     */   {
/*  120: 311 */     if (value != null) {
/*  121: 312 */       this.m_stemmer = value;
/*  122:     */     } else {
/*  123: 314 */       this.m_stemmer = new NullStemmer();
/*  124:     */     }
/*  125:     */   }
/*  126:     */   
/*  127:     */   public Stemmer getStemmer()
/*  128:     */   {
/*  129: 324 */     return this.m_stemmer;
/*  130:     */   }
/*  131:     */   
/*  132:     */   public String stemmerTipText()
/*  133:     */   {
/*  134: 334 */     return "The stemming algorithm to use on the words.";
/*  135:     */   }
/*  136:     */   
/*  137:     */   public void setTokenizer(Tokenizer value)
/*  138:     */   {
/*  139: 343 */     this.m_tokenizer = value;
/*  140:     */   }
/*  141:     */   
/*  142:     */   public Tokenizer getTokenizer()
/*  143:     */   {
/*  144: 352 */     return this.m_tokenizer;
/*  145:     */   }
/*  146:     */   
/*  147:     */   public String tokenizerTipText()
/*  148:     */   {
/*  149: 362 */     return "The tokenizing algorithm to use on the strings.";
/*  150:     */   }
/*  151:     */   
/*  152:     */   public String useWordFrequenciesTipText()
/*  153:     */   {
/*  154: 372 */     return "Use word frequencies rather than binary bag of words representation";
/*  155:     */   }
/*  156:     */   
/*  157:     */   public void setUseWordFrequencies(boolean u)
/*  158:     */   {
/*  159: 383 */     this.m_wordFrequencies = u;
/*  160:     */   }
/*  161:     */   
/*  162:     */   public boolean getUseWordFrequencies()
/*  163:     */   {
/*  164: 393 */     return this.m_wordFrequencies;
/*  165:     */   }
/*  166:     */   
/*  167:     */   public String lowercaseTokensTipText()
/*  168:     */   {
/*  169: 403 */     return "Whether to convert all tokens to lowercase";
/*  170:     */   }
/*  171:     */   
/*  172:     */   public void setLowercaseTokens(boolean l)
/*  173:     */   {
/*  174: 412 */     this.m_lowercaseTokens = l;
/*  175:     */   }
/*  176:     */   
/*  177:     */   public boolean getLowercaseTokens()
/*  178:     */   {
/*  179: 421 */     return this.m_lowercaseTokens;
/*  180:     */   }
/*  181:     */   
/*  182:     */   public void setStopwordsHandler(StopwordsHandler value)
/*  183:     */   {
/*  184: 430 */     if (value != null) {
/*  185: 431 */       this.m_StopwordsHandler = value;
/*  186:     */     } else {
/*  187: 433 */       this.m_StopwordsHandler = new Null();
/*  188:     */     }
/*  189:     */   }
/*  190:     */   
/*  191:     */   public StopwordsHandler getStopwordsHandler()
/*  192:     */   {
/*  193: 443 */     return this.m_StopwordsHandler;
/*  194:     */   }
/*  195:     */   
/*  196:     */   public String stopwordsHandlerTipText()
/*  197:     */   {
/*  198: 453 */     return "The stopwords handler to use (Null means no stopwords are used).";
/*  199:     */   }
/*  200:     */   
/*  201:     */   public String periodicPruningTipText()
/*  202:     */   {
/*  203: 463 */     return "How often (number of instances) to prune the dictionary of low frequency terms. 0 means don't prune. Setting a positive integer n means prune after every n instances";
/*  204:     */   }
/*  205:     */   
/*  206:     */   public void setPeriodicPruning(int p)
/*  207:     */   {
/*  208: 475 */     this.m_periodicP = p;
/*  209:     */   }
/*  210:     */   
/*  211:     */   public int getPeriodicPruning()
/*  212:     */   {
/*  213: 484 */     return this.m_periodicP;
/*  214:     */   }
/*  215:     */   
/*  216:     */   public String minWordFrequencyTipText()
/*  217:     */   {
/*  218: 494 */     return "Ignore any words that don't occur at least min frequency times in the training data. If periodic pruning is turned on, then the dictionary is pruned according to this value";
/*  219:     */   }
/*  220:     */   
/*  221:     */   public void setMinWordFrequency(double minFreq)
/*  222:     */   {
/*  223: 509 */     this.m_minWordP = minFreq;
/*  224:     */   }
/*  225:     */   
/*  226:     */   public double getMinWordFrequency()
/*  227:     */   {
/*  228: 520 */     return this.m_minWordP;
/*  229:     */   }
/*  230:     */   
/*  231:     */   public String minAbsoluteCoefficientValueTipText()
/*  232:     */   {
/*  233: 530 */     return "The minimum absolute magnitude for model coefficients. Terms with weights smaller than this value are ignored. If periodic pruning is turned on then this is also used to determine if a word should be removed from the dictionary.";
/*  234:     */   }
/*  235:     */   
/*  236:     */   public void setMinAbsoluteCoefficientValue(double minCoeff)
/*  237:     */   {
/*  238: 545 */     this.m_minAbsCoefficient = minCoeff;
/*  239:     */   }
/*  240:     */   
/*  241:     */   public double getMinAbsoluteCoefficientValue()
/*  242:     */   {
/*  243: 557 */     return this.m_minAbsCoefficient;
/*  244:     */   }
/*  245:     */   
/*  246:     */   public String normalizeDocLengthTipText()
/*  247:     */   {
/*  248: 567 */     return "If true then document length is normalized according to the settings for norm and lnorm";
/*  249:     */   }
/*  250:     */   
/*  251:     */   public void setNormalizeDocLength(boolean norm)
/*  252:     */   {
/*  253: 577 */     this.m_normalize = norm;
/*  254:     */   }
/*  255:     */   
/*  256:     */   public boolean getNormalizeDocLength()
/*  257:     */   {
/*  258: 586 */     return this.m_normalize;
/*  259:     */   }
/*  260:     */   
/*  261:     */   public String normTipText()
/*  262:     */   {
/*  263: 596 */     return "The norm of the instances after normalization.";
/*  264:     */   }
/*  265:     */   
/*  266:     */   public double getNorm()
/*  267:     */   {
/*  268: 605 */     return this.m_norm;
/*  269:     */   }
/*  270:     */   
/*  271:     */   public void setNorm(double newNorm)
/*  272:     */   {
/*  273: 614 */     this.m_norm = newNorm;
/*  274:     */   }
/*  275:     */   
/*  276:     */   public String LNormTipText()
/*  277:     */   {
/*  278: 624 */     return "The LNorm to use for document length normalization.";
/*  279:     */   }
/*  280:     */   
/*  281:     */   public double getLNorm()
/*  282:     */   {
/*  283: 633 */     return this.m_lnorm;
/*  284:     */   }
/*  285:     */   
/*  286:     */   public void setLNorm(double newLNorm)
/*  287:     */   {
/*  288: 642 */     this.m_lnorm = newLNorm;
/*  289:     */   }
/*  290:     */   
/*  291:     */   public String lambdaTipText()
/*  292:     */   {
/*  293: 652 */     return "The regularization constant. (default = 0.0001)";
/*  294:     */   }
/*  295:     */   
/*  296:     */   public void setLambda(double lambda)
/*  297:     */   {
/*  298: 661 */     this.m_lambda = lambda;
/*  299:     */   }
/*  300:     */   
/*  301:     */   public double getLambda()
/*  302:     */   {
/*  303: 670 */     return this.m_lambda;
/*  304:     */   }
/*  305:     */   
/*  306:     */   public void setLearningRate(double lr)
/*  307:     */   {
/*  308: 679 */     this.m_learningRate = lr;
/*  309:     */   }
/*  310:     */   
/*  311:     */   public double getLearningRate()
/*  312:     */   {
/*  313: 688 */     return this.m_learningRate;
/*  314:     */   }
/*  315:     */   
/*  316:     */   public String learningRateTipText()
/*  317:     */   {
/*  318: 698 */     return "The learning rate.";
/*  319:     */   }
/*  320:     */   
/*  321:     */   public String epochsTipText()
/*  322:     */   {
/*  323: 708 */     return "The number of epochs to perform (batch learning). The total number of iterations is epochs * num instances.";
/*  324:     */   }
/*  325:     */   
/*  326:     */   public void setEpochs(int e)
/*  327:     */   {
/*  328: 718 */     this.m_epochs = e;
/*  329:     */   }
/*  330:     */   
/*  331:     */   public int getEpochs()
/*  332:     */   {
/*  333: 727 */     return this.m_epochs;
/*  334:     */   }
/*  335:     */   
/*  336:     */   public void setLossFunction(SelectedTag function)
/*  337:     */   {
/*  338: 736 */     if (function.getTags() == TAGS_SELECTION) {
/*  339: 737 */       this.m_loss = function.getSelectedTag().getID();
/*  340:     */     }
/*  341:     */   }
/*  342:     */   
/*  343:     */   public SelectedTag getLossFunction()
/*  344:     */   {
/*  345: 747 */     return new SelectedTag(this.m_loss, TAGS_SELECTION);
/*  346:     */   }
/*  347:     */   
/*  348:     */   public String lossFunctionTipText()
/*  349:     */   {
/*  350: 757 */     return "The loss function to use. Hinge loss (SVM), log loss (logistic regression) or squared loss (regression).";
/*  351:     */   }
/*  352:     */   
/*  353:     */   public void setOutputProbsForSVM(boolean o)
/*  354:     */   {
/*  355: 769 */     this.m_fitLogistic = o;
/*  356:     */   }
/*  357:     */   
/*  358:     */   public boolean getOutputProbsForSVM()
/*  359:     */   {
/*  360: 780 */     return this.m_fitLogistic;
/*  361:     */   }
/*  362:     */   
/*  363:     */   public String outputProbsForSVMTipText()
/*  364:     */   {
/*  365: 790 */     return "Fit a logistic regression to the output of SVM for producing probability estimates";
/*  366:     */   }
/*  367:     */   
/*  368:     */   public Enumeration<Option> listOptions()
/*  369:     */   {
/*  370: 802 */     Vector<Option> newVector = new Vector();
/*  371: 803 */     newVector.add(new Option("\tSet the loss function to minimize. 0 = hinge loss (SVM), 1 = log loss (logistic regression)\n\t(default = 0)", "F", 1, "-F"));
/*  372:     */     
/*  373:     */ 
/*  374: 806 */     newVector.add(new Option("\tOutput probabilities for SVMs (fits a logsitic\n\tmodel to the output of the SVM)", "output-probs", 0, "-outputProbs"));
/*  375:     */     
/*  376:     */ 
/*  377: 809 */     newVector.add(new Option("\tThe learning rate (default = 0.01).", "L", 1, "-L"));
/*  378:     */     
/*  379: 811 */     newVector.add(new Option("\tThe lambda regularization constant (default = 0.0001)", "R", 1, "-R <double>"));
/*  380:     */     
/*  381: 813 */     newVector.add(new Option("\tThe number of epochs to perform (batch learning only, default = 500)", "E", 1, "-E <integer>"));
/*  382:     */     
/*  383: 815 */     newVector.add(new Option("\tUse word frequencies instead of binary bag of words.", "W", 0, "-W"));
/*  384:     */     
/*  385: 817 */     newVector.add(new Option("\tHow often to prune the dictionary of low frequency words (default = 0, i.e. don't prune)", "P", 1, "-P <# instances>"));
/*  386:     */     
/*  387:     */ 
/*  388: 820 */     newVector.add(new Option("\tMinimum word frequency. Words with less than this frequence are ignored.\n\tIf periodic pruning is turned on then this is also used to determine which\n\twords to remove from the dictionary (default = 3).", "M", 1, "-M <double>"));
/*  389:     */     
/*  390:     */ 
/*  391:     */ 
/*  392:     */ 
/*  393:     */ 
/*  394: 826 */     newVector.add(new Option("\tMinimum absolute value of coefficients in the model.\n\tIf periodic pruning is turned on then this\n\tis also used to prune words from the dictionary\n\t(default = 0.001", "min-coeff", 1, "-min-coeff <double>"));
/*  395:     */     
/*  396:     */ 
/*  397:     */ 
/*  398:     */ 
/*  399: 831 */     newVector.addElement(new Option("\tNormalize document length (use in conjunction with -norm and -lnorm)", "normalize", 0, "-normalize"));
/*  400:     */     
/*  401:     */ 
/*  402: 834 */     newVector.addElement(new Option("\tSpecify the norm that each instance must have (default 1.0)", "norm", 1, "-norm <num>"));
/*  403:     */     
/*  404:     */ 
/*  405: 837 */     newVector.addElement(new Option("\tSpecify L-norm to use (default 2.0)", "lnorm", 1, "-lnorm <num>"));
/*  406:     */     
/*  407: 839 */     newVector.addElement(new Option("\tConvert all tokens to lowercase before adding to the dictionary.", "lowercase", 0, "-lowercase"));
/*  408:     */     
/*  409: 841 */     newVector.addElement(new Option("\tThe stopwords handler to use (default Null).", "-stopwords-handler", 1, "-stopwords-handler"));
/*  410:     */     
/*  411:     */ 
/*  412: 844 */     newVector.addElement(new Option("\tThe tokenizing algorihtm (classname plus parameters) to use.\n\t(default: " + WordTokenizer.class.getName() + ")", "tokenizer", 1, "-tokenizer <spec>"));
/*  413:     */     
/*  414:     */ 
/*  415:     */ 
/*  416: 848 */     newVector.addElement(new Option("\tThe stemmering algorihtm (classname plus parameters) to use.", "stemmer", 1, "-stemmer <spec>"));
/*  417:     */     
/*  418:     */ 
/*  419:     */ 
/*  420: 852 */     newVector.addAll(Collections.list(super.listOptions()));
/*  421:     */     
/*  422: 854 */     return newVector.elements();
/*  423:     */   }
/*  424:     */   
/*  425:     */   public void setOptions(String[] options)
/*  426:     */     throws Exception
/*  427:     */   {
/*  428: 939 */     reset();
/*  429:     */     
/*  430: 941 */     String lossString = Utils.getOption('F', options);
/*  431: 942 */     if (lossString.length() != 0) {
/*  432: 943 */       setLossFunction(new SelectedTag(Integer.parseInt(lossString), TAGS_SELECTION));
/*  433:     */     }
/*  434: 947 */     setOutputProbsForSVM(Utils.getFlag("output-probs", options));
/*  435:     */     
/*  436: 949 */     String lambdaString = Utils.getOption('R', options);
/*  437: 950 */     if (lambdaString.length() > 0) {
/*  438: 951 */       setLambda(Double.parseDouble(lambdaString));
/*  439:     */     }
/*  440: 954 */     String learningRateString = Utils.getOption('L', options);
/*  441: 955 */     if (learningRateString.length() > 0) {
/*  442: 956 */       setLearningRate(Double.parseDouble(learningRateString));
/*  443:     */     }
/*  444: 959 */     String epochsString = Utils.getOption("E", options);
/*  445: 960 */     if (epochsString.length() > 0) {
/*  446: 961 */       setEpochs(Integer.parseInt(epochsString));
/*  447:     */     }
/*  448: 964 */     setUseWordFrequencies(Utils.getFlag("W", options));
/*  449:     */     
/*  450: 966 */     String pruneFreqS = Utils.getOption("P", options);
/*  451: 967 */     if (pruneFreqS.length() > 0) {
/*  452: 968 */       setPeriodicPruning(Integer.parseInt(pruneFreqS));
/*  453:     */     }
/*  454: 970 */     String minFreq = Utils.getOption("M", options);
/*  455: 971 */     if (minFreq.length() > 0) {
/*  456: 972 */       setMinWordFrequency(Double.parseDouble(minFreq));
/*  457:     */     }
/*  458: 975 */     String minCoeff = Utils.getOption("min-coeff", options);
/*  459: 976 */     if (minCoeff.length() > 0) {
/*  460: 977 */       setMinAbsoluteCoefficientValue(Double.parseDouble(minCoeff));
/*  461:     */     }
/*  462: 980 */     setNormalizeDocLength(Utils.getFlag("normalize", options));
/*  463:     */     
/*  464: 982 */     String normFreqS = Utils.getOption("norm", options);
/*  465: 983 */     if (normFreqS.length() > 0) {
/*  466: 984 */       setNorm(Double.parseDouble(normFreqS));
/*  467:     */     }
/*  468: 986 */     String lnormFreqS = Utils.getOption("lnorm", options);
/*  469: 987 */     if (lnormFreqS.length() > 0) {
/*  470: 988 */       setLNorm(Double.parseDouble(lnormFreqS));
/*  471:     */     }
/*  472: 991 */     setLowercaseTokens(Utils.getFlag("lowercase", options));
/*  473:     */     
/*  474: 993 */     String stemmerString = Utils.getOption("stemmer", options);
/*  475: 994 */     if (stemmerString.length() == 0)
/*  476:     */     {
/*  477: 995 */       setStemmer(null);
/*  478:     */     }
/*  479:     */     else
/*  480:     */     {
/*  481: 997 */       String[] stemmerSpec = Utils.splitOptions(stemmerString);
/*  482: 998 */       if (stemmerSpec.length == 0) {
/*  483: 999 */         throw new Exception("Invalid stemmer specification string");
/*  484:     */       }
/*  485:1001 */       String stemmerName = stemmerSpec[0];
/*  486:1002 */       stemmerSpec[0] = "";
/*  487:1003 */       Stemmer stemmer = (Stemmer)Utils.forName(Class.forName("weka.core.stemmers.Stemmer"), stemmerName, stemmerSpec);
/*  488:1004 */       setStemmer(stemmer);
/*  489:     */     }
/*  490:1007 */     String stopwordsHandlerString = Utils.getOption("stopwords-handler", options);
/*  491:1008 */     if (stopwordsHandlerString.length() == 0)
/*  492:     */     {
/*  493:1009 */       setStopwordsHandler(null);
/*  494:     */     }
/*  495:     */     else
/*  496:     */     {
/*  497:1011 */       String[] stopwordsHandlerSpec = Utils.splitOptions(stopwordsHandlerString);
/*  498:1012 */       if (stopwordsHandlerSpec.length == 0) {
/*  499:1013 */         throw new Exception("Invalid StopwordsHandler specification string");
/*  500:     */       }
/*  501:1015 */       String stopwordsHandlerName = stopwordsHandlerSpec[0];
/*  502:1016 */       stopwordsHandlerSpec[0] = "";
/*  503:1017 */       StopwordsHandler stopwordsHandler = (StopwordsHandler)Utils.forName(Class.forName("weka.core.stopwords.StopwordsHandler"), stopwordsHandlerName, stopwordsHandlerSpec);
/*  504:     */       
/*  505:     */ 
/*  506:1020 */       setStopwordsHandler(stopwordsHandler);
/*  507:     */     }
/*  508:1023 */     String tokenizerString = Utils.getOption("tokenizer", options);
/*  509:1024 */     if (tokenizerString.length() == 0)
/*  510:     */     {
/*  511:1025 */       setTokenizer(new WordTokenizer());
/*  512:     */     }
/*  513:     */     else
/*  514:     */     {
/*  515:1027 */       String[] tokenizerSpec = Utils.splitOptions(tokenizerString);
/*  516:1028 */       if (tokenizerSpec.length == 0) {
/*  517:1029 */         throw new Exception("Invalid tokenizer specification string");
/*  518:     */       }
/*  519:1031 */       String tokenizerName = tokenizerSpec[0];
/*  520:1032 */       tokenizerSpec[0] = "";
/*  521:1033 */       Tokenizer tokenizer = (Tokenizer)Utils.forName(Class.forName("weka.core.tokenizers.Tokenizer"), tokenizerName, tokenizerSpec);
/*  522:     */       
/*  523:1035 */       setTokenizer(tokenizer);
/*  524:     */     }
/*  525:1038 */     super.setOptions(options);
/*  526:     */     
/*  527:1040 */     Utils.checkForRemainingOptions(options);
/*  528:     */   }
/*  529:     */   
/*  530:     */   public String[] getOptions()
/*  531:     */   {
/*  532:1050 */     ArrayList<String> options = new ArrayList();
/*  533:     */     
/*  534:1052 */     options.add("-F");
/*  535:1053 */     options.add("" + getLossFunction().getSelectedTag().getID());
/*  536:1054 */     if (getOutputProbsForSVM()) {
/*  537:1055 */       options.add("-output-probs");
/*  538:     */     }
/*  539:1057 */     options.add("-L");
/*  540:1058 */     options.add("" + getLearningRate());
/*  541:1059 */     options.add("-R");
/*  542:1060 */     options.add("" + getLambda());
/*  543:1061 */     options.add("-E");
/*  544:1062 */     options.add("" + getEpochs());
/*  545:1063 */     if (getUseWordFrequencies()) {
/*  546:1064 */       options.add("-W");
/*  547:     */     }
/*  548:1066 */     options.add("-P");
/*  549:1067 */     options.add("" + getPeriodicPruning());
/*  550:1068 */     options.add("-M");
/*  551:1069 */     options.add("" + getMinWordFrequency());
/*  552:     */     
/*  553:1071 */     options.add("-min-coeff");
/*  554:1072 */     options.add("" + getMinAbsoluteCoefficientValue());
/*  555:1074 */     if (getNormalizeDocLength()) {
/*  556:1075 */       options.add("-normalize");
/*  557:     */     }
/*  558:1077 */     options.add("-norm");
/*  559:1078 */     options.add("" + getNorm());
/*  560:1079 */     options.add("-lnorm");
/*  561:1080 */     options.add("" + getLNorm());
/*  562:1081 */     if (getLowercaseTokens()) {
/*  563:1082 */       options.add("-lowercase");
/*  564:     */     }
/*  565:1085 */     if (getStopwordsHandler() != null)
/*  566:     */     {
/*  567:1086 */       options.add("-stopwords-handler");
/*  568:1087 */       String spec = getStopwordsHandler().getClass().getName();
/*  569:1088 */       if ((getStopwordsHandler() instanceof OptionHandler)) {
/*  570:1089 */         spec = spec + " " + Utils.joinOptions(((OptionHandler)getStopwordsHandler()).getOptions());
/*  571:     */       }
/*  572:1094 */       options.add(spec.trim());
/*  573:     */     }
/*  574:1097 */     options.add("-tokenizer");
/*  575:1098 */     String spec = getTokenizer().getClass().getName();
/*  576:1099 */     if ((getTokenizer() instanceof OptionHandler)) {
/*  577:1100 */       spec = spec + " " + Utils.joinOptions(getTokenizer().getOptions());
/*  578:     */     }
/*  579:1103 */     options.add(spec.trim());
/*  580:1105 */     if (getStemmer() != null)
/*  581:     */     {
/*  582:1106 */       options.add("-stemmer");
/*  583:1107 */       spec = getStemmer().getClass().getName();
/*  584:1108 */       if ((getStemmer() instanceof OptionHandler)) {
/*  585:1109 */         spec = spec + " " + Utils.joinOptions(((OptionHandler)getStemmer()).getOptions());
/*  586:     */       }
/*  587:1113 */       options.add(spec.trim());
/*  588:     */     }
/*  589:1116 */     Collections.addAll(options, super.getOptions());
/*  590:     */     
/*  591:1118 */     return (String[])options.toArray(new String[1]);
/*  592:     */   }
/*  593:     */   
/*  594:     */   public String globalInfo()
/*  595:     */   {
/*  596:1128 */     return "Implements stochastic gradient descent for learning a linear binary class SVM or binary class logistic regression on text data. Operates directly (and only) on String attributes. Other types of input attributes are accepted but ignored during training and classification.";
/*  597:     */   }
/*  598:     */   
/*  599:     */   public void reset()
/*  600:     */   {
/*  601:1139 */     this.m_t = 1.0D;
/*  602:1140 */     this.m_bias = 0.0D;
/*  603:1141 */     this.m_dictionary = null;
/*  604:     */   }
/*  605:     */   
/*  606:     */   public void buildClassifier(Instances data)
/*  607:     */     throws Exception
/*  608:     */   {
/*  609:1152 */     reset();
/*  610:     */     
/*  611:     */ 
/*  612:     */ 
/*  613:     */ 
/*  614:     */ 
/*  615:     */ 
/*  616:     */ 
/*  617:     */ 
/*  618:     */ 
/*  619:     */ 
/*  620:     */ 
/*  621:1164 */     getCapabilities().testWithFail(data);
/*  622:     */     
/*  623:1166 */     this.m_dictionary = new LinkedHashMap(10000);
/*  624:     */     
/*  625:1168 */     this.m_numInstances = data.numInstances();
/*  626:1169 */     this.m_data = new Instances(data, 0);
/*  627:1170 */     data = new Instances(data);
/*  628:1172 */     if ((this.m_fitLogistic) && (this.m_loss == 0)) {
/*  629:1173 */       initializeSVMProbs(data);
/*  630:     */     }
/*  631:1176 */     if (data.numInstances() > 0)
/*  632:     */     {
/*  633:1177 */       data.randomize(new Random(getSeed()));
/*  634:1178 */       train(data);
/*  635:1179 */       pruneDictionary(true);
/*  636:     */     }
/*  637:     */   }
/*  638:     */   
/*  639:     */   protected void initializeSVMProbs(Instances data)
/*  640:     */     throws Exception
/*  641:     */   {
/*  642:1184 */     this.m_svmProbs = new SGD();
/*  643:1185 */     this.m_svmProbs.setLossFunction(new SelectedTag(1, TAGS_SELECTION));
/*  644:1186 */     this.m_svmProbs.setLearningRate(this.m_learningRate);
/*  645:1187 */     this.m_svmProbs.setLambda(this.m_lambda);
/*  646:1188 */     this.m_svmProbs.setEpochs(this.m_epochs);
/*  647:1189 */     ArrayList<Attribute> atts = new ArrayList(2);
/*  648:1190 */     atts.add(new Attribute("pred"));
/*  649:1191 */     ArrayList<String> attVals = new ArrayList(2);
/*  650:1192 */     attVals.add(data.classAttribute().value(0));
/*  651:1193 */     attVals.add(data.classAttribute().value(1));
/*  652:1194 */     atts.add(new Attribute("class", attVals));
/*  653:1195 */     this.m_fitLogisticStructure = new Instances("data", atts, 0);
/*  654:1196 */     this.m_fitLogisticStructure.setClassIndex(1);
/*  655:     */     
/*  656:1198 */     this.m_svmProbs.buildClassifier(this.m_fitLogisticStructure);
/*  657:     */   }
/*  658:     */   
/*  659:     */   protected void train(Instances data)
/*  660:     */     throws Exception
/*  661:     */   {
/*  662:1202 */     for (int e = 0; e < this.m_epochs; e++) {
/*  663:1203 */       for (int i = 0; i < data.numInstances(); i++) {
/*  664:1204 */         if (e == 0) {
/*  665:1205 */           updateClassifier(data.instance(i), true);
/*  666:     */         } else {
/*  667:1207 */           updateClassifier(data.instance(i), false);
/*  668:     */         }
/*  669:     */       }
/*  670:     */     }
/*  671:     */   }
/*  672:     */   
/*  673:     */   public void updateClassifier(Instance instance)
/*  674:     */     throws Exception
/*  675:     */   {
/*  676:1222 */     updateClassifier(instance, true);
/*  677:     */   }
/*  678:     */   
/*  679:     */   protected void updateClassifier(Instance instance, boolean updateDictionary)
/*  680:     */     throws Exception
/*  681:     */   {
/*  682:1228 */     if (!instance.classIsMissing())
/*  683:     */     {
/*  684:1231 */       tokenizeInstance(instance, updateDictionary);
/*  685:1235 */       if ((this.m_loss == 0) && (this.m_fitLogistic))
/*  686:     */       {
/*  687:1236 */         double pred = svmOutput();
/*  688:1237 */         double[] vals = new double[2];
/*  689:1238 */         vals[0] = pred;
/*  690:1239 */         vals[1] = instance.classValue();
/*  691:1240 */         DenseInstance metaI = new DenseInstance(instance.weight(), vals);
/*  692:1241 */         metaI.setDataset(this.m_fitLogisticStructure);
/*  693:1242 */         this.m_svmProbs.updateClassifier(metaI);
/*  694:     */       }
/*  695:1246 */       double wx = dotProd(this.m_inputVector);
/*  696:1247 */       double y = instance.classValue() == 0.0D ? -1.0D : 1.0D;
/*  697:1248 */       double z = y * (wx + this.m_bias);
/*  698:     */       
/*  699:     */ 
/*  700:1251 */       double multiplier = 1.0D;
/*  701:1252 */       if (this.m_numInstances == 0.0D) {
/*  702:1253 */         multiplier = 1.0D - this.m_learningRate * this.m_lambda / this.m_t;
/*  703:     */       } else {
/*  704:1255 */         multiplier = 1.0D - this.m_learningRate * this.m_lambda / this.m_numInstances;
/*  705:     */       }
/*  706:1258 */       for (Map.Entry<String, Count> c : this.m_dictionary.entrySet()) {
/*  707:1259 */         ((Count)c.getValue()).m_weight *= multiplier;
/*  708:     */       }
/*  709:1263 */       if ((this.m_loss != 0) || (z < 1.0D))
/*  710:     */       {
/*  711:1265 */         double dloss = dloss(z);
/*  712:1266 */         double factor = this.m_learningRate * y * dloss;
/*  713:1269 */         for (Map.Entry<String, Count> feature : this.m_inputVector.entrySet())
/*  714:     */         {
/*  715:1270 */           String word = (String)feature.getKey();
/*  716:1271 */           double value = this.m_wordFrequencies ? ((Count)feature.getValue()).m_count : 1.0D;
/*  717:     */           
/*  718:1273 */           Count c = (Count)this.m_dictionary.get(word);
/*  719:1274 */           if (c != null) {
/*  720:1275 */             c.m_weight += factor * value;
/*  721:     */           }
/*  722:     */         }
/*  723:1280 */         this.m_bias += factor;
/*  724:     */       }
/*  725:1283 */       this.m_t += 1.0D;
/*  726:     */     }
/*  727:     */   }
/*  728:     */   
/*  729:     */   protected void tokenizeInstance(Instance instance, boolean updateDictionary)
/*  730:     */   {
/*  731:1288 */     if (this.m_inputVector == null) {
/*  732:1289 */       this.m_inputVector = new LinkedHashMap();
/*  733:     */     } else {
/*  734:1291 */       this.m_inputVector.clear();
/*  735:     */     }
/*  736:1294 */     for (int i = 0; i < instance.numAttributes(); i++) {
/*  737:1295 */       if ((instance.attribute(i).isString()) && (!instance.isMissing(i)))
/*  738:     */       {
/*  739:1296 */         this.m_tokenizer.tokenize(instance.stringValue(i));
/*  740:1298 */         while (this.m_tokenizer.hasMoreElements())
/*  741:     */         {
/*  742:1299 */           String word = this.m_tokenizer.nextElement();
/*  743:1300 */           if (this.m_lowercaseTokens) {
/*  744:1301 */             word = word.toLowerCase();
/*  745:     */           }
/*  746:1304 */           word = this.m_stemmer.stem(word);
/*  747:1306 */           if (!this.m_StopwordsHandler.isStopword(word))
/*  748:     */           {
/*  749:1310 */             Count docCount = (Count)this.m_inputVector.get(word);
/*  750:1311 */             if (docCount == null) {
/*  751:1312 */               this.m_inputVector.put(word, new Count(instance.weight()));
/*  752:     */             } else {
/*  753:1314 */               docCount.m_count += instance.weight();
/*  754:     */             }
/*  755:1317 */             if (updateDictionary)
/*  756:     */             {
/*  757:1318 */               Count count = (Count)this.m_dictionary.get(word);
/*  758:1319 */               if (count == null) {
/*  759:1320 */                 this.m_dictionary.put(word, new Count(instance.weight()));
/*  760:     */               } else {
/*  761:1322 */                 count.m_count += instance.weight();
/*  762:     */               }
/*  763:     */             }
/*  764:     */           }
/*  765:     */         }
/*  766:     */       }
/*  767:     */     }
/*  768:1330 */     if (updateDictionary) {
/*  769:1331 */       pruneDictionary(false);
/*  770:     */     }
/*  771:     */   }
/*  772:     */   
/*  773:     */   protected void pruneDictionary(boolean force)
/*  774:     */   {
/*  775:1336 */     if (((this.m_periodicP <= 0) || (this.m_t % this.m_periodicP > 0.0D)) && (!force)) {
/*  776:1337 */       return;
/*  777:     */     }
/*  778:1340 */     Iterator<Map.Entry<String, Count>> entries = this.m_dictionary.entrySet().iterator();
/*  779:1342 */     while (entries.hasNext())
/*  780:     */     {
/*  781:1343 */       Map.Entry<String, Count> entry = (Map.Entry)entries.next();
/*  782:1344 */       if ((((Count)entry.getValue()).m_count < this.m_minWordP) || (Math.abs(((Count)entry.getValue()).m_weight) < this.m_minAbsCoefficient)) {
/*  783:1346 */         entries.remove();
/*  784:     */       }
/*  785:     */     }
/*  786:     */   }
/*  787:     */   
/*  788:     */   protected double svmOutput()
/*  789:     */   {
/*  790:1352 */     double wx = dotProd(this.m_inputVector);
/*  791:1353 */     double z = wx + this.m_bias;
/*  792:     */     
/*  793:1355 */     return z;
/*  794:     */   }
/*  795:     */   
/*  796:     */   public double[] distributionForInstance(Instance inst)
/*  797:     */     throws Exception
/*  798:     */   {
/*  799:1360 */     double[] result = new double[2];
/*  800:     */     
/*  801:1362 */     tokenizeInstance(inst, false);
/*  802:1363 */     double wx = dotProd(this.m_inputVector);
/*  803:1364 */     double z = wx + this.m_bias;
/*  804:1366 */     if ((this.m_loss == 0) && (this.m_fitLogistic))
/*  805:     */     {
/*  806:1367 */       double pred = z;
/*  807:1368 */       double[] vals = new double[2];
/*  808:1369 */       vals[0] = pred;
/*  809:1370 */       vals[1] = Utils.missingValue();
/*  810:1371 */       DenseInstance metaI = new DenseInstance(inst.weight(), vals);
/*  811:1372 */       metaI.setDataset(this.m_fitLogisticStructure);
/*  812:1373 */       return this.m_svmProbs.distributionForInstance(metaI);
/*  813:     */     }
/*  814:1376 */     if (z <= 0.0D)
/*  815:     */     {
/*  816:1377 */       if (this.m_loss == 1)
/*  817:     */       {
/*  818:1378 */         result[0] = (1.0D / (1.0D + Math.exp(z)));
/*  819:1379 */         result[1] = (1.0D - result[0]);
/*  820:     */       }
/*  821:     */       else
/*  822:     */       {
/*  823:1381 */         result[0] = 1.0D;
/*  824:     */       }
/*  825:     */     }
/*  826:1384 */     else if (this.m_loss == 1)
/*  827:     */     {
/*  828:1385 */       result[1] = (1.0D / (1.0D + Math.exp(-z)));
/*  829:1386 */       result[0] = (1.0D - result[1]);
/*  830:     */     }
/*  831:     */     else
/*  832:     */     {
/*  833:1388 */       result[1] = 1.0D;
/*  834:     */     }
/*  835:1392 */     return result;
/*  836:     */   }
/*  837:     */   
/*  838:     */   protected double dotProd(Map<String, Count> document)
/*  839:     */   {
/*  840:1396 */     double result = 0.0D;
/*  841:     */     
/*  842:     */ 
/*  843:1399 */     double iNorm = 0.0D;
/*  844:1400 */     double fv = 0.0D;
/*  845:1401 */     if (this.m_normalize)
/*  846:     */     {
/*  847:1402 */       for (Count c : document.values())
/*  848:     */       {
/*  849:1404 */         fv = this.m_wordFrequencies ? c.m_count : 1.0D;
/*  850:1405 */         iNorm += Math.pow(Math.abs(fv), this.m_lnorm);
/*  851:     */       }
/*  852:1407 */       iNorm = Math.pow(iNorm, 1.0D / this.m_lnorm);
/*  853:     */     }
/*  854:1410 */     for (Map.Entry<String, Count> feature : document.entrySet())
/*  855:     */     {
/*  856:1411 */       String word = (String)feature.getKey();
/*  857:1412 */       double freq = this.m_wordFrequencies ? ((Count)feature.getValue()).m_count : 1.0D;
/*  858:1414 */       if (this.m_normalize) {
/*  859:1415 */         freq /= iNorm * this.m_norm;
/*  860:     */       }
/*  861:1418 */       Count weight = (Count)this.m_dictionary.get(word);
/*  862:1420 */       if ((weight != null) && (weight.m_count >= this.m_minWordP) && (Math.abs(weight.m_weight) >= this.m_minAbsCoefficient)) {
/*  863:1422 */         result += freq * weight.m_weight;
/*  864:     */       }
/*  865:     */     }
/*  866:1426 */     return result;
/*  867:     */   }
/*  868:     */   
/*  869:     */   public String toString()
/*  870:     */   {
/*  871:1431 */     if (this.m_dictionary == null) {
/*  872:1432 */       return "SGDText: No model built yet.\n";
/*  873:     */     }
/*  874:1435 */     StringBuffer buff = new StringBuffer();
/*  875:1436 */     buff.append("SGDText:\n\n");
/*  876:1437 */     buff.append("Loss function: ");
/*  877:1438 */     if (this.m_loss == 0) {
/*  878:1439 */       buff.append("Hinge loss (SVM)\n\n");
/*  879:     */     } else {
/*  880:1441 */       buff.append("Log loss (logistic regression)\n\n");
/*  881:     */     }
/*  882:1444 */     int dictSize = 0;
/*  883:1445 */     Iterator<Map.Entry<String, Count>> entries = this.m_dictionary.entrySet().iterator();
/*  884:1447 */     while (entries.hasNext())
/*  885:     */     {
/*  886:1448 */       Map.Entry<String, Count> entry = (Map.Entry)entries.next();
/*  887:1449 */       if ((((Count)entry.getValue()).m_count >= this.m_minWordP) && (Math.abs(((Count)entry.getValue()).m_weight) >= this.m_minAbsCoefficient)) {
/*  888:1451 */         dictSize++;
/*  889:     */       }
/*  890:     */     }
/*  891:1455 */     buff.append("Dictionary size: " + dictSize + "\n\n");
/*  892:     */     
/*  893:1457 */     buff.append(this.m_data.classAttribute().name() + " = \n\n");
/*  894:1458 */     int printed = 0;
/*  895:     */     
/*  896:1460 */     entries = this.m_dictionary.entrySet().iterator();
/*  897:1461 */     while (entries.hasNext())
/*  898:     */     {
/*  899:1462 */       Map.Entry<String, Count> entry = (Map.Entry)entries.next();
/*  900:1464 */       if ((((Count)entry.getValue()).m_count >= this.m_minWordP) && (Math.abs(((Count)entry.getValue()).m_weight) >= this.m_minAbsCoefficient))
/*  901:     */       {
/*  902:1466 */         if (printed > 0) {
/*  903:1467 */           buff.append(" + ");
/*  904:     */         } else {
/*  905:1469 */           buff.append("   ");
/*  906:     */         }
/*  907:1472 */         buff.append(Utils.doubleToString(((Count)entry.getValue()).m_weight, 12, 4) + " " + (String)entry.getKey() + " " + ((Count)entry.getValue()).m_count + "\n");
/*  908:     */         
/*  909:1474 */         printed++;
/*  910:     */       }
/*  911:     */     }
/*  912:1478 */     if (this.m_bias > 0.0D) {
/*  913:1479 */       buff.append(" + " + Utils.doubleToString(this.m_bias, 12, 4));
/*  914:     */     } else {
/*  915:1481 */       buff.append(" - " + Utils.doubleToString(-this.m_bias, 12, 4));
/*  916:     */     }
/*  917:1484 */     return buff.toString();
/*  918:     */   }
/*  919:     */   
/*  920:     */   public LinkedHashMap<String, Count> getDictionary()
/*  921:     */   {
/*  922:1493 */     return this.m_dictionary;
/*  923:     */   }
/*  924:     */   
/*  925:     */   public int getDictionarySize()
/*  926:     */   {
/*  927:1503 */     int size = 0;
/*  928:1504 */     if (this.m_dictionary != null)
/*  929:     */     {
/*  930:1505 */       Iterator<Map.Entry<String, Count>> entries = this.m_dictionary.entrySet().iterator();
/*  931:1507 */       while (entries.hasNext())
/*  932:     */       {
/*  933:1508 */         Map.Entry<String, Count> entry = (Map.Entry)entries.next();
/*  934:1509 */         if ((((Count)entry.getValue()).m_count >= this.m_minWordP) && (Math.abs(((Count)entry.getValue()).m_weight) >= this.m_minAbsCoefficient)) {
/*  935:1511 */           size++;
/*  936:     */         }
/*  937:     */       }
/*  938:     */     }
/*  939:1516 */     return size;
/*  940:     */   }
/*  941:     */   
/*  942:     */   public double bias()
/*  943:     */   {
/*  944:1520 */     return this.m_bias;
/*  945:     */   }
/*  946:     */   
/*  947:     */   public void setBias(double bias)
/*  948:     */   {
/*  949:1524 */     this.m_bias = bias;
/*  950:     */   }
/*  951:     */   
/*  952:     */   public String getRevision()
/*  953:     */   {
/*  954:1534 */     return RevisionUtils.extract("$Revision: 12049 $");
/*  955:     */   }
/*  956:     */   
/*  957:1537 */   protected int m_numModels = 0;
/*  958:     */   
/*  959:     */   public SGDText aggregate(SGDText toAggregate)
/*  960:     */     throws Exception
/*  961:     */   {
/*  962:1550 */     if (this.m_dictionary == null) {
/*  963:1551 */       throw new Exception("No model built yet, can't aggregate");
/*  964:     */     }
/*  965:1553 */     LinkedHashMap<String, Count> tempDict = toAggregate.getDictionary();
/*  966:     */     
/*  967:1555 */     Iterator<Map.Entry<String, Count>> entries = tempDict.entrySet().iterator();
/*  968:1557 */     while (entries.hasNext())
/*  969:     */     {
/*  970:1558 */       Map.Entry<String, Count> entry = (Map.Entry)entries.next();
/*  971:     */       
/*  972:1560 */       Count masterCount = (Count)this.m_dictionary.get(entry.getKey());
/*  973:1561 */       if (masterCount == null)
/*  974:     */       {
/*  975:1563 */         masterCount = new Count(((Count)entry.getValue()).m_count);
/*  976:1564 */         masterCount.m_weight = ((Count)entry.getValue()).m_weight;
/*  977:1565 */         this.m_dictionary.put(entry.getKey(), masterCount);
/*  978:     */       }
/*  979:     */       else
/*  980:     */       {
/*  981:1568 */         masterCount.m_count += ((Count)entry.getValue()).m_count;
/*  982:1569 */         masterCount.m_weight += ((Count)entry.getValue()).m_weight;
/*  983:     */       }
/*  984:     */     }
/*  985:1574 */     this.m_bias += toAggregate.bias();
/*  986:     */     
/*  987:1576 */     this.m_numModels += 1;
/*  988:     */     
/*  989:1578 */     return this;
/*  990:     */   }
/*  991:     */   
/*  992:     */   public void finalizeAggregation()
/*  993:     */     throws Exception
/*  994:     */   {
/*  995:1590 */     if (this.m_numModels == 0) {
/*  996:1591 */       throw new Exception("Unable to finalize aggregation - haven't seen any models to aggregate");
/*  997:     */     }
/*  998:1595 */     pruneDictionary(true);
/*  999:     */     
/* 1000:1597 */     Iterator<Map.Entry<String, Count>> entries = this.m_dictionary.entrySet().iterator();
/* 1001:1600 */     while (entries.hasNext())
/* 1002:     */     {
/* 1003:1601 */       Map.Entry<String, Count> entry = (Map.Entry)entries.next();
/* 1004:1602 */       ((Count)entry.getValue()).m_count /= (this.m_numModels + 1);
/* 1005:1603 */       ((Count)entry.getValue()).m_weight /= (this.m_numModels + 1);
/* 1006:     */     }
/* 1007:1606 */     this.m_bias /= (this.m_numModels + 1);
/* 1008:     */     
/* 1009:     */ 
/* 1010:1609 */     this.m_numModels = 0;
/* 1011:     */   }
/* 1012:     */   
/* 1013:     */   public void batchFinished()
/* 1014:     */     throws Exception
/* 1015:     */   {
/* 1016:1614 */     pruneDictionary(true);
/* 1017:     */   }
/* 1018:     */   
/* 1019:     */   public static void main(String[] args)
/* 1020:     */   {
/* 1021:1621 */     runClassifier(new SGDText(), args);
/* 1022:     */   }
/* 1023:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.SGDText
 * JD-Core Version:    0.7.0.1
 */