/*    1:     */ package weka.classifiers.bayes;
/*    2:     */ 
/*    3:     */ import java.io.Serializable;
/*    4:     */ import java.util.ArrayList;
/*    5:     */ import java.util.Collections;
/*    6:     */ import java.util.Enumeration;
/*    7:     */ import java.util.HashMap;
/*    8:     */ import java.util.HashSet;
/*    9:     */ import java.util.Iterator;
/*   10:     */ import java.util.LinkedHashMap;
/*   11:     */ import java.util.Map;
/*   12:     */ import java.util.Map.Entry;
/*   13:     */ import java.util.Set;
/*   14:     */ import java.util.Vector;
/*   15:     */ import weka.classifiers.AbstractClassifier;
/*   16:     */ import weka.classifiers.UpdateableBatchProcessor;
/*   17:     */ import weka.classifiers.UpdateableClassifier;
/*   18:     */ import weka.core.Aggregateable;
/*   19:     */ import weka.core.Attribute;
/*   20:     */ import weka.core.Capabilities;
/*   21:     */ import weka.core.Capabilities.Capability;
/*   22:     */ import weka.core.Instance;
/*   23:     */ import weka.core.Instances;
/*   24:     */ import weka.core.Option;
/*   25:     */ import weka.core.OptionHandler;
/*   26:     */ import weka.core.RevisionUtils;
/*   27:     */ import weka.core.Utils;
/*   28:     */ import weka.core.WeightedInstancesHandler;
/*   29:     */ import weka.core.stemmers.NullStemmer;
/*   30:     */ import weka.core.stemmers.Stemmer;
/*   31:     */ import weka.core.stopwords.Null;
/*   32:     */ import weka.core.stopwords.StopwordsHandler;
/*   33:     */ import weka.core.tokenizers.Tokenizer;
/*   34:     */ import weka.core.tokenizers.WordTokenizer;
/*   35:     */ 
/*   36:     */ public class NaiveBayesMultinomialText
/*   37:     */   extends AbstractClassifier
/*   38:     */   implements UpdateableClassifier, UpdateableBatchProcessor, WeightedInstancesHandler, Aggregateable<NaiveBayesMultinomialText>
/*   39:     */ {
/*   40:     */   private static final long serialVersionUID = 2139025532014821394L;
/*   41:     */   protected Instances m_data;
/*   42:     */   protected double[] m_probOfClass;
/*   43:     */   protected double[] m_wordsPerClass;
/*   44:     */   protected Map<Integer, LinkedHashMap<String, Count>> m_probOfWordGivenClass;
/*   45:     */   protected transient LinkedHashMap<String, Count> m_inputVector;
/*   46:     */   
/*   47:     */   private static class Count
/*   48:     */     implements Serializable
/*   49:     */   {
/*   50:     */     private static final long serialVersionUID = 2104201532017340967L;
/*   51:     */     public double m_count;
/*   52:     */     
/*   53:     */     public Count(double c)
/*   54:     */     {
/*   55: 129 */       this.m_count = c;
/*   56:     */     }
/*   57:     */   }
/*   58:     */   
/*   59: 147 */   protected StopwordsHandler m_StopwordsHandler = new Null();
/*   60: 150 */   protected Tokenizer m_tokenizer = new WordTokenizer();
/*   61:     */   protected boolean m_lowercaseTokens;
/*   62: 156 */   protected Stemmer m_stemmer = new NullStemmer();
/*   63: 163 */   protected int m_periodicP = 0;
/*   64: 169 */   protected double m_minWordP = 3.0D;
/*   65: 172 */   protected boolean m_wordFrequencies = false;
/*   66: 175 */   protected boolean m_normalize = false;
/*   67: 178 */   protected double m_norm = 1.0D;
/*   68: 181 */   protected double m_lnorm = 2.0D;
/*   69: 184 */   protected double m_leplace = 1.0D;
/*   70:     */   protected double m_t;
/*   71:     */   
/*   72:     */   public String globalInfo()
/*   73:     */   {
/*   74: 196 */     return "Multinomial naive bayes for text data. Operates directly (and only) on String attributes. Other types of input attributes are accepted but ignored during training and classification";
/*   75:     */   }
/*   76:     */   
/*   77:     */   public Capabilities getCapabilities()
/*   78:     */   {
/*   79: 209 */     Capabilities result = super.getCapabilities();
/*   80: 210 */     result.disableAll();
/*   81:     */     
/*   82:     */ 
/*   83: 213 */     result.enable(Capabilities.Capability.STRING_ATTRIBUTES);
/*   84: 214 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*   85: 215 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*   86: 216 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*   87: 217 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*   88:     */     
/*   89: 219 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*   90: 220 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*   91:     */     
/*   92:     */ 
/*   93: 223 */     result.setMinimumNumberInstances(0);
/*   94:     */     
/*   95: 225 */     return result;
/*   96:     */   }
/*   97:     */   
/*   98:     */   public void buildClassifier(Instances data)
/*   99:     */     throws Exception
/*  100:     */   {
/*  101: 236 */     reset();
/*  102:     */     
/*  103:     */ 
/*  104: 239 */     getCapabilities().testWithFail(data);
/*  105:     */     
/*  106: 241 */     this.m_data = new Instances(data, 0);
/*  107: 242 */     data = new Instances(data);
/*  108:     */     
/*  109: 244 */     this.m_wordsPerClass = new double[data.numClasses()];
/*  110: 245 */     this.m_probOfClass = new double[data.numClasses()];
/*  111: 246 */     this.m_probOfWordGivenClass = new HashMap();
/*  112:     */     
/*  113:     */ 
/*  114: 249 */     double laplace = 1.0D;
/*  115: 250 */     for (int i = 0; i < data.numClasses(); i++)
/*  116:     */     {
/*  117: 251 */       LinkedHashMap<String, Count> dict = new LinkedHashMap(10000 / data.numClasses());
/*  118:     */       
/*  119: 253 */       this.m_probOfWordGivenClass.put(Integer.valueOf(i), dict);
/*  120: 254 */       this.m_probOfClass[i] = laplace;
/*  121:     */       
/*  122:     */ 
/*  123:     */ 
/*  124: 258 */       this.m_wordsPerClass[i] = 0.0D;
/*  125:     */     }
/*  126: 261 */     for (int i = 0; i < data.numInstances(); i++) {
/*  127: 262 */       updateClassifier(data.instance(i));
/*  128:     */     }
/*  129: 265 */     if (data.numInstances() > 0) {
/*  130: 266 */       pruneDictionary(true);
/*  131:     */     }
/*  132:     */   }
/*  133:     */   
/*  134:     */   public void updateClassifier(Instance instance)
/*  135:     */     throws Exception
/*  136:     */   {
/*  137: 278 */     updateClassifier(instance, true);
/*  138:     */   }
/*  139:     */   
/*  140:     */   protected void updateClassifier(Instance instance, boolean updateDictionary)
/*  141:     */     throws Exception
/*  142:     */   {
/*  143: 284 */     if (!instance.classIsMissing())
/*  144:     */     {
/*  145: 285 */       int classIndex = (int)instance.classValue();
/*  146: 286 */       this.m_probOfClass[classIndex] += instance.weight();
/*  147:     */       
/*  148: 288 */       tokenizeInstance(instance, updateDictionary);
/*  149: 289 */       this.m_t += 1.0D;
/*  150:     */     }
/*  151:     */   }
/*  152:     */   
/*  153:     */   public double[] distributionForInstance(Instance instance)
/*  154:     */     throws Exception
/*  155:     */   {
/*  156: 303 */     tokenizeInstance(instance, false);
/*  157:     */     
/*  158: 305 */     double[] probOfClassGivenDoc = new double[this.m_data.numClasses()];
/*  159:     */     
/*  160: 307 */     double[] logDocGivenClass = new double[this.m_data.numClasses()];
/*  161: 308 */     for (int i = 0; i < this.m_data.numClasses(); i++)
/*  162:     */     {
/*  163: 309 */       logDocGivenClass[i] += Math.log(this.m_probOfClass[i]);
/*  164:     */       
/*  165: 311 */       LinkedHashMap<String, Count> dictForClass = (LinkedHashMap)this.m_probOfWordGivenClass.get(Integer.valueOf(i));
/*  166:     */       
/*  167: 313 */       int allWords = 0;
/*  168:     */       
/*  169: 315 */       double iNorm = 0.0D;
/*  170: 316 */       double fv = 0.0D;
/*  171: 318 */       if (this.m_normalize)
/*  172:     */       {
/*  173: 319 */         for (Map.Entry<String, Count> feature : this.m_inputVector.entrySet())
/*  174:     */         {
/*  175: 320 */           String word = (String)feature.getKey();
/*  176: 321 */           Count c = (Count)feature.getValue();
/*  177:     */           
/*  178:     */ 
/*  179: 324 */           boolean ok = false;
/*  180: 325 */           for (int clss = 0; clss < this.m_data.numClasses(); clss++) {
/*  181: 326 */             if (((LinkedHashMap)this.m_probOfWordGivenClass.get(Integer.valueOf(clss))).get(word) != null)
/*  182:     */             {
/*  183: 327 */               ok = true;
/*  184: 328 */               break;
/*  185:     */             }
/*  186:     */           }
/*  187: 335 */           if (ok)
/*  188:     */           {
/*  189: 337 */             fv = this.m_wordFrequencies ? c.m_count : 1.0D;
/*  190: 338 */             iNorm += Math.pow(Math.abs(fv), this.m_lnorm);
/*  191:     */           }
/*  192:     */         }
/*  193: 341 */         iNorm = Math.pow(iNorm, 1.0D / this.m_lnorm);
/*  194:     */       }
/*  195: 345 */       for (Map.Entry<String, Count> feature : this.m_inputVector.entrySet())
/*  196:     */       {
/*  197: 346 */         String word = (String)feature.getKey();
/*  198: 347 */         Count dictCount = (Count)dictForClass.get(word);
/*  199:     */         
/*  200:     */ 
/*  201:     */ 
/*  202:     */ 
/*  203:     */ 
/*  204:     */ 
/*  205: 354 */         boolean ok = false;
/*  206: 355 */         for (int clss = 0; clss < this.m_data.numClasses(); clss++) {
/*  207: 356 */           if (((LinkedHashMap)this.m_probOfWordGivenClass.get(Integer.valueOf(clss))).get(word) != null)
/*  208:     */           {
/*  209: 357 */             ok = true;
/*  210: 358 */             break;
/*  211:     */           }
/*  212:     */         }
/*  213: 363 */         if (ok)
/*  214:     */         {
/*  215: 364 */           double freq = this.m_wordFrequencies ? ((Count)feature.getValue()).m_count : 1.0D;
/*  216: 366 */           if (this.m_normalize) {
/*  217: 367 */             freq /= iNorm * this.m_norm;
/*  218:     */           }
/*  219: 369 */           allWords = (int)(allWords + freq);
/*  220: 371 */           if (dictCount != null) {
/*  221: 372 */             logDocGivenClass[i] += freq * Math.log(dictCount.m_count);
/*  222:     */           } else {
/*  223: 375 */             logDocGivenClass[i] += freq * Math.log(this.m_leplace);
/*  224:     */           }
/*  225:     */         }
/*  226:     */       }
/*  227: 380 */       if (this.m_wordsPerClass[i] > 0.0D) {
/*  228: 381 */         logDocGivenClass[i] -= allWords * Math.log(this.m_wordsPerClass[i]);
/*  229:     */       }
/*  230:     */     }
/*  231: 385 */     double max = logDocGivenClass[Utils.maxIndex(logDocGivenClass)];
/*  232: 387 */     for (int i = 0; i < this.m_data.numClasses(); i++) {
/*  233: 388 */       probOfClassGivenDoc[i] = Math.exp(logDocGivenClass[i] - max);
/*  234:     */     }
/*  235: 391 */     Utils.normalize(probOfClassGivenDoc);
/*  236:     */     
/*  237: 393 */     return probOfClassGivenDoc;
/*  238:     */   }
/*  239:     */   
/*  240:     */   protected void tokenizeInstance(Instance instance, boolean updateDictionary)
/*  241:     */   {
/*  242: 397 */     if (this.m_inputVector == null) {
/*  243: 398 */       this.m_inputVector = new LinkedHashMap();
/*  244:     */     } else {
/*  245: 400 */       this.m_inputVector.clear();
/*  246:     */     }
/*  247: 403 */     for (int i = 0; i < instance.numAttributes(); i++) {
/*  248: 404 */       if ((instance.attribute(i).isString()) && (!instance.isMissing(i)))
/*  249:     */       {
/*  250: 405 */         this.m_tokenizer.tokenize(instance.stringValue(i));
/*  251: 407 */         while (this.m_tokenizer.hasMoreElements())
/*  252:     */         {
/*  253: 408 */           String word = this.m_tokenizer.nextElement();
/*  254: 409 */           if (this.m_lowercaseTokens) {
/*  255: 410 */             word = word.toLowerCase();
/*  256:     */           }
/*  257: 413 */           word = this.m_stemmer.stem(word);
/*  258: 415 */           if (!this.m_StopwordsHandler.isStopword(word))
/*  259:     */           {
/*  260: 419 */             Count docCount = (Count)this.m_inputVector.get(word);
/*  261: 420 */             if (docCount == null) {
/*  262: 421 */               this.m_inputVector.put(word, new Count(instance.weight()));
/*  263:     */             } else {
/*  264: 423 */               docCount.m_count += instance.weight();
/*  265:     */             }
/*  266:     */           }
/*  267:     */         }
/*  268:     */       }
/*  269:     */     }
/*  270: 429 */     if (updateDictionary)
/*  271:     */     {
/*  272: 430 */       int classValue = (int)instance.classValue();
/*  273: 431 */       LinkedHashMap<String, Count> dictForClass = (LinkedHashMap)this.m_probOfWordGivenClass.get(Integer.valueOf(classValue));
/*  274:     */       
/*  275:     */ 
/*  276:     */ 
/*  277: 435 */       double iNorm = 0.0D;
/*  278: 436 */       double fv = 0.0D;
/*  279: 438 */       if (this.m_normalize)
/*  280:     */       {
/*  281: 439 */         for (Count c : this.m_inputVector.values())
/*  282:     */         {
/*  283: 441 */           fv = this.m_wordFrequencies ? c.m_count : 1.0D;
/*  284: 442 */           iNorm += Math.pow(Math.abs(fv), this.m_lnorm);
/*  285:     */         }
/*  286: 444 */         iNorm = Math.pow(iNorm, 1.0D / this.m_lnorm);
/*  287:     */       }
/*  288: 447 */       for (Map.Entry<String, Count> feature : this.m_inputVector.entrySet())
/*  289:     */       {
/*  290: 448 */         String word = (String)feature.getKey();
/*  291: 449 */         double freq = this.m_wordFrequencies ? ((Count)feature.getValue()).m_count : 1.0D;
/*  292: 452 */         if (this.m_normalize) {
/*  293: 453 */           freq /= iNorm * this.m_norm;
/*  294:     */         }
/*  295: 457 */         for (int i = 0; i < this.m_data.numClasses(); i++)
/*  296:     */         {
/*  297: 458 */           LinkedHashMap<String, Count> dict = (LinkedHashMap)this.m_probOfWordGivenClass.get(Integer.valueOf(i));
/*  298: 459 */           if (dict.get(word) == null)
/*  299:     */           {
/*  300: 460 */             dict.put(word, new Count(this.m_leplace));
/*  301: 461 */             this.m_wordsPerClass[i] += this.m_leplace;
/*  302:     */           }
/*  303:     */         }
/*  304: 465 */         Count dictCount = (Count)dictForClass.get(word);
/*  305:     */         
/*  306:     */ 
/*  307:     */ 
/*  308:     */ 
/*  309: 470 */         dictCount.m_count += freq;
/*  310: 471 */         this.m_wordsPerClass[classValue] += freq;
/*  311:     */       }
/*  312: 475 */       pruneDictionary(false);
/*  313:     */     }
/*  314:     */   }
/*  315:     */   
/*  316:     */   protected void pruneDictionary(boolean force)
/*  317:     */   {
/*  318: 480 */     if (((this.m_periodicP <= 0) || (this.m_t % this.m_periodicP > 0.0D)) && (!force)) {
/*  319: 481 */       return;
/*  320:     */     }
/*  321: 484 */     Set<Integer> classesSet = this.m_probOfWordGivenClass.keySet();
/*  322: 485 */     for (Integer classIndex : classesSet)
/*  323:     */     {
/*  324: 486 */       LinkedHashMap<String, Count> dictForClass = (LinkedHashMap)this.m_probOfWordGivenClass.get(classIndex);
/*  325:     */       
/*  326:     */ 
/*  327: 489 */       Iterator<Map.Entry<String, Count>> entries = dictForClass.entrySet().iterator();
/*  328: 491 */       while (entries.hasNext())
/*  329:     */       {
/*  330: 492 */         Map.Entry<String, Count> entry = (Map.Entry)entries.next();
/*  331: 493 */         if (((Count)entry.getValue()).m_count < this.m_minWordP)
/*  332:     */         {
/*  333: 494 */           this.m_wordsPerClass[classIndex.intValue()] -= ((Count)entry.getValue()).m_count;
/*  334: 495 */           entries.remove();
/*  335:     */         }
/*  336:     */       }
/*  337:     */     }
/*  338:     */   }
/*  339:     */   
/*  340:     */   public void reset()
/*  341:     */   {
/*  342: 505 */     this.m_t = 1.0D;
/*  343: 506 */     this.m_wordsPerClass = null;
/*  344: 507 */     this.m_probOfWordGivenClass = null;
/*  345: 508 */     this.m_probOfClass = null;
/*  346:     */   }
/*  347:     */   
/*  348:     */   public void setStemmer(Stemmer value)
/*  349:     */   {
/*  350: 519 */     if (value != null) {
/*  351: 520 */       this.m_stemmer = value;
/*  352:     */     } else {
/*  353: 522 */       this.m_stemmer = new NullStemmer();
/*  354:     */     }
/*  355:     */   }
/*  356:     */   
/*  357:     */   public Stemmer getStemmer()
/*  358:     */   {
/*  359: 532 */     return this.m_stemmer;
/*  360:     */   }
/*  361:     */   
/*  362:     */   public String stemmerTipText()
/*  363:     */   {
/*  364: 542 */     return "The stemming algorithm to use on the words.";
/*  365:     */   }
/*  366:     */   
/*  367:     */   public void setTokenizer(Tokenizer value)
/*  368:     */   {
/*  369: 551 */     this.m_tokenizer = value;
/*  370:     */   }
/*  371:     */   
/*  372:     */   public Tokenizer getTokenizer()
/*  373:     */   {
/*  374: 560 */     return this.m_tokenizer;
/*  375:     */   }
/*  376:     */   
/*  377:     */   public String tokenizerTipText()
/*  378:     */   {
/*  379: 570 */     return "The tokenizing algorithm to use on the strings.";
/*  380:     */   }
/*  381:     */   
/*  382:     */   public String useWordFrequenciesTipText()
/*  383:     */   {
/*  384: 580 */     return "Use word frequencies rather than binary bag of words representation";
/*  385:     */   }
/*  386:     */   
/*  387:     */   public void setUseWordFrequencies(boolean u)
/*  388:     */   {
/*  389: 591 */     this.m_wordFrequencies = u;
/*  390:     */   }
/*  391:     */   
/*  392:     */   public boolean getUseWordFrequencies()
/*  393:     */   {
/*  394: 601 */     return this.m_wordFrequencies;
/*  395:     */   }
/*  396:     */   
/*  397:     */   public String lowercaseTokensTipText()
/*  398:     */   {
/*  399: 611 */     return "Whether to convert all tokens to lowercase";
/*  400:     */   }
/*  401:     */   
/*  402:     */   public void setLowercaseTokens(boolean l)
/*  403:     */   {
/*  404: 620 */     this.m_lowercaseTokens = l;
/*  405:     */   }
/*  406:     */   
/*  407:     */   public boolean getLowercaseTokens()
/*  408:     */   {
/*  409: 629 */     return this.m_lowercaseTokens;
/*  410:     */   }
/*  411:     */   
/*  412:     */   public String periodicPruningTipText()
/*  413:     */   {
/*  414: 639 */     return "How often (number of instances) to prune the dictionary of low frequency terms. 0 means don't prune. Setting a positive integer n means prune after every n instances";
/*  415:     */   }
/*  416:     */   
/*  417:     */   public void setPeriodicPruning(int p)
/*  418:     */   {
/*  419: 651 */     this.m_periodicP = p;
/*  420:     */   }
/*  421:     */   
/*  422:     */   public int getPeriodicPruning()
/*  423:     */   {
/*  424: 660 */     return this.m_periodicP;
/*  425:     */   }
/*  426:     */   
/*  427:     */   public String minWordFrequencyTipText()
/*  428:     */   {
/*  429: 670 */     return "Ignore any words that don't occur at least min frequency times in the training data. If periodic pruning is turned on, then the dictionary is pruned according to this value";
/*  430:     */   }
/*  431:     */   
/*  432:     */   public void setMinWordFrequency(double minFreq)
/*  433:     */   {
/*  434: 685 */     this.m_minWordP = minFreq;
/*  435:     */   }
/*  436:     */   
/*  437:     */   public double getMinWordFrequency()
/*  438:     */   {
/*  439: 696 */     return this.m_minWordP;
/*  440:     */   }
/*  441:     */   
/*  442:     */   public String normalizeDocLengthTipText()
/*  443:     */   {
/*  444: 706 */     return "If true then document length is normalized according to the settings for norm and lnorm";
/*  445:     */   }
/*  446:     */   
/*  447:     */   public void setNormalizeDocLength(boolean norm)
/*  448:     */   {
/*  449: 716 */     this.m_normalize = norm;
/*  450:     */   }
/*  451:     */   
/*  452:     */   public boolean getNormalizeDocLength()
/*  453:     */   {
/*  454: 725 */     return this.m_normalize;
/*  455:     */   }
/*  456:     */   
/*  457:     */   public String normTipText()
/*  458:     */   {
/*  459: 735 */     return "The norm of the instances after normalization.";
/*  460:     */   }
/*  461:     */   
/*  462:     */   public double getNorm()
/*  463:     */   {
/*  464: 744 */     return this.m_norm;
/*  465:     */   }
/*  466:     */   
/*  467:     */   public void setNorm(double newNorm)
/*  468:     */   {
/*  469: 753 */     this.m_norm = newNorm;
/*  470:     */   }
/*  471:     */   
/*  472:     */   public String LNormTipText()
/*  473:     */   {
/*  474: 763 */     return "The LNorm to use for document length normalization.";
/*  475:     */   }
/*  476:     */   
/*  477:     */   public double getLNorm()
/*  478:     */   {
/*  479: 772 */     return this.m_lnorm;
/*  480:     */   }
/*  481:     */   
/*  482:     */   public void setLNorm(double newLNorm)
/*  483:     */   {
/*  484: 781 */     this.m_lnorm = newLNorm;
/*  485:     */   }
/*  486:     */   
/*  487:     */   public void setStopwordsHandler(StopwordsHandler value)
/*  488:     */   {
/*  489: 790 */     if (value != null) {
/*  490: 791 */       this.m_StopwordsHandler = value;
/*  491:     */     } else {
/*  492: 793 */       this.m_StopwordsHandler = new Null();
/*  493:     */     }
/*  494:     */   }
/*  495:     */   
/*  496:     */   public StopwordsHandler getStopwordsHandler()
/*  497:     */   {
/*  498: 803 */     return this.m_StopwordsHandler;
/*  499:     */   }
/*  500:     */   
/*  501:     */   public String stopwordsHandlerTipText()
/*  502:     */   {
/*  503: 813 */     return "The stopwords handler to use (Null means no stopwords are used).";
/*  504:     */   }
/*  505:     */   
/*  506:     */   public Enumeration<Option> listOptions()
/*  507:     */   {
/*  508: 824 */     Vector<Option> newVector = new Vector();
/*  509:     */     
/*  510: 826 */     newVector.add(new Option("\tUse word frequencies instead of binary bag of words.", "W", 0, "-W"));
/*  511:     */     
/*  512: 828 */     newVector.add(new Option("\tHow often to prune the dictionary of low frequency words (default = 0, i.e. don't prune)", "P", 1, "-P <# instances>"));
/*  513:     */     
/*  514:     */ 
/*  515: 831 */     newVector.add(new Option("\tMinimum word frequency. Words with less than this frequence are ignored.\n\tIf periodic pruning is turned on then this is also used to determine which\n\twords to remove from the dictionary (default = 3).", "M", 1, "-M <double>"));
/*  516:     */     
/*  517:     */ 
/*  518:     */ 
/*  519:     */ 
/*  520: 836 */     newVector.addElement(new Option("\tNormalize document length (use in conjunction with -norm and -lnorm)", "normalize", 0, "-normalize"));
/*  521:     */     
/*  522:     */ 
/*  523: 839 */     newVector.addElement(new Option("\tSpecify the norm that each instance must have (default 1.0)", "norm", 1, "-norm <num>"));
/*  524:     */     
/*  525:     */ 
/*  526: 842 */     newVector.addElement(new Option("\tSpecify L-norm to use (default 2.0)", "lnorm", 1, "-lnorm <num>"));
/*  527:     */     
/*  528:     */ 
/*  529: 845 */     newVector.addElement(new Option("\tConvert all tokens to lowercase before adding to the dictionary.", "lowercase", 0, "-lowercase"));
/*  530:     */     
/*  531: 847 */     newVector.addElement(new Option("\tThe stopwords handler to use (default Null).", "-stopwords-handler", 1, "-stopwords-handler"));
/*  532:     */     
/*  533:     */ 
/*  534: 850 */     newVector.addElement(new Option("\tThe tokenizing algorihtm (classname plus parameters) to use.\n\t(default: " + WordTokenizer.class.getName() + ")", "tokenizer", 1, "-tokenizer <spec>"));
/*  535:     */     
/*  536:     */ 
/*  537:     */ 
/*  538: 854 */     newVector.addElement(new Option("\tThe stemmering algorihtm (classname plus parameters) to use.", "stemmer", 1, "-stemmer <spec>"));
/*  539:     */     
/*  540:     */ 
/*  541:     */ 
/*  542: 858 */     newVector.addAll(Collections.list(super.listOptions()));
/*  543:     */     
/*  544: 860 */     return newVector.elements();
/*  545:     */   }
/*  546:     */   
/*  547:     */   public void setOptions(String[] options)
/*  548:     */     throws Exception
/*  549:     */   {
/*  550: 918 */     reset();
/*  551:     */     
/*  552: 920 */     super.setOptions(options);
/*  553:     */     
/*  554: 922 */     setUseWordFrequencies(Utils.getFlag("W", options));
/*  555:     */     
/*  556: 924 */     String pruneFreqS = Utils.getOption("P", options);
/*  557: 925 */     if (pruneFreqS.length() > 0) {
/*  558: 926 */       setPeriodicPruning(Integer.parseInt(pruneFreqS));
/*  559:     */     }
/*  560: 928 */     String minFreq = Utils.getOption("M", options);
/*  561: 929 */     if (minFreq.length() > 0) {
/*  562: 930 */       setMinWordFrequency(Double.parseDouble(minFreq));
/*  563:     */     }
/*  564: 933 */     setNormalizeDocLength(Utils.getFlag("normalize", options));
/*  565:     */     
/*  566: 935 */     String normFreqS = Utils.getOption("norm", options);
/*  567: 936 */     if (normFreqS.length() > 0) {
/*  568: 937 */       setNorm(Double.parseDouble(normFreqS));
/*  569:     */     }
/*  570: 939 */     String lnormFreqS = Utils.getOption("lnorm", options);
/*  571: 940 */     if (lnormFreqS.length() > 0) {
/*  572: 941 */       setLNorm(Double.parseDouble(lnormFreqS));
/*  573:     */     }
/*  574: 944 */     setLowercaseTokens(Utils.getFlag("lowercase", options));
/*  575:     */     
/*  576: 946 */     String stemmerString = Utils.getOption("stemmer", options);
/*  577: 947 */     if (stemmerString.length() == 0)
/*  578:     */     {
/*  579: 948 */       setStemmer(null);
/*  580:     */     }
/*  581:     */     else
/*  582:     */     {
/*  583: 950 */       String[] stemmerSpec = Utils.splitOptions(stemmerString);
/*  584: 951 */       if (stemmerSpec.length == 0) {
/*  585: 952 */         throw new Exception("Invalid stemmer specification string");
/*  586:     */       }
/*  587: 954 */       String stemmerName = stemmerSpec[0];
/*  588: 955 */       stemmerSpec[0] = "";
/*  589: 956 */       Stemmer stemmer = (Stemmer)Utils.forName(Class.forName("weka.core.stemmers.Stemmer"), stemmerName, stemmerSpec);
/*  590: 957 */       setStemmer(stemmer);
/*  591:     */     }
/*  592: 960 */     String stopwordsHandlerString = Utils.getOption("stopwords-handler", options);
/*  593: 961 */     if (stopwordsHandlerString.length() == 0)
/*  594:     */     {
/*  595: 962 */       setStopwordsHandler(null);
/*  596:     */     }
/*  597:     */     else
/*  598:     */     {
/*  599: 964 */       String[] stopwordsHandlerSpec = Utils.splitOptions(stopwordsHandlerString);
/*  600: 965 */       if (stopwordsHandlerSpec.length == 0) {
/*  601: 966 */         throw new Exception("Invalid StopwordsHandler specification string");
/*  602:     */       }
/*  603: 968 */       String stopwordsHandlerName = stopwordsHandlerSpec[0];
/*  604: 969 */       stopwordsHandlerSpec[0] = "";
/*  605: 970 */       StopwordsHandler stopwordsHandler = (StopwordsHandler)Utils.forName(Class.forName("weka.core.stopwords.StopwordsHandler"), stopwordsHandlerName, stopwordsHandlerSpec);
/*  606:     */       
/*  607:     */ 
/*  608: 973 */       setStopwordsHandler(stopwordsHandler);
/*  609:     */     }
/*  610: 976 */     String tokenizerString = Utils.getOption("tokenizer", options);
/*  611: 977 */     if (tokenizerString.length() == 0)
/*  612:     */     {
/*  613: 978 */       setTokenizer(new WordTokenizer());
/*  614:     */     }
/*  615:     */     else
/*  616:     */     {
/*  617: 980 */       String[] tokenizerSpec = Utils.splitOptions(tokenizerString);
/*  618: 981 */       if (tokenizerSpec.length == 0) {
/*  619: 982 */         throw new Exception("Invalid tokenizer specification string");
/*  620:     */       }
/*  621: 984 */       String tokenizerName = tokenizerSpec[0];
/*  622: 985 */       tokenizerSpec[0] = "";
/*  623: 986 */       Tokenizer tokenizer = (Tokenizer)Utils.forName(Class.forName("weka.core.tokenizers.Tokenizer"), tokenizerName, tokenizerSpec);
/*  624:     */       
/*  625: 988 */       setTokenizer(tokenizer);
/*  626:     */     }
/*  627: 991 */     Utils.checkForRemainingOptions(options);
/*  628:     */   }
/*  629:     */   
/*  630:     */   public String[] getOptions()
/*  631:     */   {
/*  632:1001 */     ArrayList<String> options = new ArrayList();
/*  633:1003 */     if (getUseWordFrequencies()) {
/*  634:1004 */       options.add("-W");
/*  635:     */     }
/*  636:1006 */     options.add("-P");
/*  637:1007 */     options.add("" + getPeriodicPruning());
/*  638:1008 */     options.add("-M");
/*  639:1009 */     options.add("" + getMinWordFrequency());
/*  640:1011 */     if (getNormalizeDocLength()) {
/*  641:1012 */       options.add("-normalize");
/*  642:     */     }
/*  643:1014 */     options.add("-norm");
/*  644:1015 */     options.add("" + getNorm());
/*  645:1016 */     options.add("-lnorm");
/*  646:1017 */     options.add("" + getLNorm());
/*  647:1018 */     if (getLowercaseTokens()) {
/*  648:1019 */       options.add("-lowercase");
/*  649:     */     }
/*  650:1022 */     if (getStopwordsHandler() != null)
/*  651:     */     {
/*  652:1023 */       options.add("-stopwords-handler");
/*  653:1024 */       String spec = getStopwordsHandler().getClass().getName();
/*  654:1025 */       if ((getStopwordsHandler() instanceof OptionHandler)) {
/*  655:1026 */         spec = spec + " " + Utils.joinOptions(((OptionHandler)getStopwordsHandler()).getOptions());
/*  656:     */       }
/*  657:1031 */       options.add(spec.trim());
/*  658:     */     }
/*  659:1034 */     options.add("-tokenizer");
/*  660:1035 */     String spec = getTokenizer().getClass().getName();
/*  661:1036 */     if ((getTokenizer() instanceof OptionHandler)) {
/*  662:1037 */       spec = spec + " " + Utils.joinOptions(getTokenizer().getOptions());
/*  663:     */     }
/*  664:1040 */     options.add(spec.trim());
/*  665:1042 */     if (getStemmer() != null)
/*  666:     */     {
/*  667:1043 */       options.add("-stemmer");
/*  668:1044 */       spec = getStemmer().getClass().getName();
/*  669:1045 */       if ((getStemmer() instanceof OptionHandler)) {
/*  670:1046 */         spec = spec + " " + Utils.joinOptions(((OptionHandler)getStemmer()).getOptions());
/*  671:     */       }
/*  672:1050 */       options.add(spec.trim());
/*  673:     */     }
/*  674:1053 */     Collections.addAll(options, super.getOptions());
/*  675:     */     
/*  676:1055 */     return (String[])options.toArray(new String[1]);
/*  677:     */   }
/*  678:     */   
/*  679:     */   public String toString()
/*  680:     */   {
/*  681:1066 */     if (this.m_probOfClass == null) {
/*  682:1067 */       return "NaiveBayesMultinomialText: No model built yet.\n";
/*  683:     */     }
/*  684:1070 */     StringBuffer result = new StringBuffer();
/*  685:     */     
/*  686:     */ 
/*  687:1073 */     HashSet<String> master = new HashSet();
/*  688:1074 */     for (int i = 0; i < this.m_data.numClasses(); i++)
/*  689:     */     {
/*  690:1075 */       LinkedHashMap<String, Count> classDict = (LinkedHashMap)this.m_probOfWordGivenClass.get(Integer.valueOf(i));
/*  691:1077 */       for (String key : classDict.keySet()) {
/*  692:1078 */         master.add(key);
/*  693:     */       }
/*  694:     */     }
/*  695:1082 */     result.append("Dictionary size: " + master.size()).append("\n\n");
/*  696:     */     
/*  697:1084 */     result.append("The independent frequency of a class\n");
/*  698:1085 */     result.append("--------------------------------------\n");
/*  699:1087 */     for (int i = 0; i < this.m_data.numClasses(); i++) {
/*  700:1088 */       result.append(this.m_data.classAttribute().value(i)).append("\t").append(Double.toString(this.m_probOfClass[i])).append("\n");
/*  701:     */     }
/*  702:1092 */     if (master.size() > 150000)
/*  703:     */     {
/*  704:1093 */       result.append("\nFrequency table ommitted due to size\n");
/*  705:1094 */       return result.toString();
/*  706:     */     }
/*  707:1097 */     result.append("\nThe frequency of a word given the class\n");
/*  708:1098 */     result.append("-----------------------------------------\n");
/*  709:1100 */     for (int i = 0; i < this.m_data.numClasses(); i++) {
/*  710:1101 */       result.append(Utils.padLeft(this.m_data.classAttribute().value(i), 11)).append("\t");
/*  711:     */     }
/*  712:1105 */     result.append("\n");
/*  713:     */     
/*  714:1107 */     Iterator<String> masterIter = master.iterator();
/*  715:1108 */     while (masterIter.hasNext())
/*  716:     */     {
/*  717:1109 */       String word = (String)masterIter.next();
/*  718:1111 */       for (int i = 0; i < this.m_data.numClasses(); i++)
/*  719:     */       {
/*  720:1112 */         LinkedHashMap<String, Count> classDict = (LinkedHashMap)this.m_probOfWordGivenClass.get(Integer.valueOf(i));
/*  721:1113 */         Count c = (Count)classDict.get(word);
/*  722:1114 */         if (c == null) {
/*  723:1115 */           result.append("<laplace=1>\t");
/*  724:     */         } else {
/*  725:1117 */           result.append(Utils.padLeft(Double.toString(c.m_count), 11)).append("\t");
/*  726:     */         }
/*  727:     */       }
/*  728:1121 */       result.append(word);
/*  729:1122 */       result.append("\n");
/*  730:     */     }
/*  731:1125 */     return result.toString();
/*  732:     */   }
/*  733:     */   
/*  734:     */   public String getRevision()
/*  735:     */   {
/*  736:1135 */     return RevisionUtils.extract("$Revision: 11973 $");
/*  737:     */   }
/*  738:     */   
/*  739:1138 */   protected int m_numModels = 0;
/*  740:     */   
/*  741:     */   public NaiveBayesMultinomialText aggregate(NaiveBayesMultinomialText toAggregate)
/*  742:     */     throws Exception
/*  743:     */   {
/*  744:1143 */     if (this.m_numModels == -2147483648) {
/*  745:1144 */       throw new Exception("Can't aggregate further - model has already been aggregated and finalized");
/*  746:     */     }
/*  747:1148 */     if (this.m_probOfClass == null) {
/*  748:1149 */       throw new Exception("No model built yet, can't aggregate");
/*  749:     */     }
/*  750:1154 */     if (!this.m_data.classAttribute().equals(toAggregate.m_data.classAttribute())) {
/*  751:1155 */       throw new Exception("Can't aggregate - class attribute in data headers does not match: " + this.m_data.classAttribute().equalsMsg(toAggregate.m_data.classAttribute()));
/*  752:     */     }
/*  753:1161 */     for (int i = 0; i < this.m_probOfClass.length; i++)
/*  754:     */     {
/*  755:1163 */       this.m_probOfClass[i] += toAggregate.m_probOfClass[i] - 1.0D;
/*  756:     */       
/*  757:1165 */       this.m_wordsPerClass[i] += toAggregate.m_wordsPerClass[i];
/*  758:     */     }
/*  759:1168 */     Map<Integer, LinkedHashMap<String, Count>> dicts = toAggregate.m_probOfWordGivenClass;
/*  760:     */     
/*  761:1170 */     Iterator<Map.Entry<Integer, LinkedHashMap<String, Count>>> perClass = dicts.entrySet().iterator();
/*  762:1172 */     while (perClass.hasNext())
/*  763:     */     {
/*  764:1173 */       Map.Entry<Integer, LinkedHashMap<String, Count>> currentClassDict = (Map.Entry)perClass.next();
/*  765:     */       
/*  766:     */ 
/*  767:1176 */       LinkedHashMap<String, Count> masterDict = (LinkedHashMap)this.m_probOfWordGivenClass.get(currentClassDict.getKey());
/*  768:1179 */       if (masterDict == null)
/*  769:     */       {
/*  770:1181 */         masterDict = new LinkedHashMap();
/*  771:1182 */         this.m_probOfWordGivenClass.put(currentClassDict.getKey(), masterDict);
/*  772:     */       }
/*  773:1186 */       Iterator<Map.Entry<String, Count>> perClassEntries = ((LinkedHashMap)currentClassDict.getValue()).entrySet().iterator();
/*  774:1188 */       while (perClassEntries.hasNext())
/*  775:     */       {
/*  776:1189 */         Map.Entry<String, Count> entry = (Map.Entry)perClassEntries.next();
/*  777:     */         
/*  778:1191 */         Count masterCount = (Count)masterDict.get(entry.getKey());
/*  779:1193 */         if (masterCount == null)
/*  780:     */         {
/*  781:1195 */           masterCount = new Count(((Count)entry.getValue()).m_count);
/*  782:1196 */           masterDict.put(entry.getKey(), masterCount);
/*  783:     */         }
/*  784:     */         else
/*  785:     */         {
/*  786:1199 */           masterCount.m_count += ((Count)entry.getValue()).m_count - 1.0D;
/*  787:     */         }
/*  788:     */       }
/*  789:     */     }
/*  790:1204 */     this.m_t += toAggregate.m_t;
/*  791:1205 */     this.m_numModels += 1;
/*  792:     */     
/*  793:1207 */     return this;
/*  794:     */   }
/*  795:     */   
/*  796:     */   public void finalizeAggregation()
/*  797:     */     throws Exception
/*  798:     */   {
/*  799:1212 */     if (this.m_numModels == 0) {
/*  800:1213 */       throw new Exception("Unable to finalize aggregation - haven't seen any models to aggregate");
/*  801:     */     }
/*  802:1217 */     if ((this.m_periodicP > 0) && (this.m_t > this.m_periodicP))
/*  803:     */     {
/*  804:1218 */       pruneDictionary(true);
/*  805:1219 */       this.m_t = 0.0D;
/*  806:     */     }
/*  807:     */   }
/*  808:     */   
/*  809:     */   public void batchFinished()
/*  810:     */     throws Exception
/*  811:     */   {
/*  812:1225 */     pruneDictionary(true);
/*  813:     */   }
/*  814:     */   
/*  815:     */   public static void main(String[] args)
/*  816:     */   {
/*  817:1234 */     runClassifier(new NaiveBayesMultinomialText(), args);
/*  818:     */   }
/*  819:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.NaiveBayesMultinomialText
 * JD-Core Version:    0.7.0.1
 */