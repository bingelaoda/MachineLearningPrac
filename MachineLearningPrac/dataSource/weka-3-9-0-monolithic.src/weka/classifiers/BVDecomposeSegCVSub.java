/*    1:     */ package weka.classifiers;
/*    2:     */ 
/*    3:     */ import java.io.BufferedReader;
/*    4:     */ import java.io.FileReader;
/*    5:     */ import java.io.PrintStream;
/*    6:     */ import java.io.Reader;
/*    7:     */ import java.util.Collections;
/*    8:     */ import java.util.Enumeration;
/*    9:     */ import java.util.Random;
/*   10:     */ import java.util.Vector;
/*   11:     */ import weka.classifiers.rules.ZeroR;
/*   12:     */ import weka.core.Attribute;
/*   13:     */ import weka.core.Instance;
/*   14:     */ import weka.core.Instances;
/*   15:     */ import weka.core.Option;
/*   16:     */ import weka.core.OptionHandler;
/*   17:     */ import weka.core.RevisionHandler;
/*   18:     */ import weka.core.RevisionUtils;
/*   19:     */ import weka.core.TechnicalInformation;
/*   20:     */ import weka.core.TechnicalInformation.Field;
/*   21:     */ import weka.core.TechnicalInformation.Type;
/*   22:     */ import weka.core.TechnicalInformationHandler;
/*   23:     */ import weka.core.Utils;
/*   24:     */ 
/*   25:     */ public class BVDecomposeSegCVSub
/*   26:     */   implements OptionHandler, TechnicalInformationHandler, RevisionHandler
/*   27:     */ {
/*   28:     */   protected boolean m_Debug;
/*   29: 165 */   protected Classifier m_Classifier = new ZeroR();
/*   30:     */   protected String[] m_ClassifierOptions;
/*   31:     */   protected int m_ClassifyIterations;
/*   32:     */   protected String m_DataFileName;
/*   33: 177 */   protected int m_ClassIndex = -1;
/*   34: 180 */   protected int m_Seed = 1;
/*   35:     */   protected double m_KWBias;
/*   36:     */   protected double m_KWVariance;
/*   37:     */   protected double m_KWSigma;
/*   38:     */   protected double m_WBias;
/*   39:     */   protected double m_WVariance;
/*   40:     */   protected double m_Error;
/*   41:     */   protected int m_TrainSize;
/*   42:     */   protected double m_P;
/*   43:     */   
/*   44:     */   public String globalInfo()
/*   45:     */   {
/*   46: 212 */     return "This class performs Bias-Variance decomposion on any classifier using the sub-sampled cross-validation procedure as specified in (1).\nThe Kohavi and Wolpert definition of bias and variance is specified in (2).\nThe Webb definition of bias and variance is specified in (3).\n\n" + getTechnicalInformation().toString();
/*   47:     */   }
/*   48:     */   
/*   49:     */   public TechnicalInformation getTechnicalInformation()
/*   50:     */   {
/*   51: 231 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.MISC);
/*   52: 232 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Geoffrey I. Webb and Paul Conilione");
/*   53: 233 */     result.setValue(TechnicalInformation.Field.YEAR, "2002");
/*   54: 234 */     result.setValue(TechnicalInformation.Field.TITLE, "Estimating bias and variance from data");
/*   55: 235 */     result.setValue(TechnicalInformation.Field.INSTITUTION, "Monash University");
/*   56: 236 */     result.setValue(TechnicalInformation.Field.ADDRESS, "School of Computer Science and Software Engineering, Victoria, Australia");
/*   57: 237 */     result.setValue(TechnicalInformation.Field.PDF, "http://www.csse.monash.edu.au/~webb/Files/WebbConilione04.pdf");
/*   58:     */     
/*   59: 239 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.INPROCEEDINGS);
/*   60: 240 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "Ron Kohavi and David H. Wolpert");
/*   61: 241 */     additional.setValue(TechnicalInformation.Field.YEAR, "1996");
/*   62: 242 */     additional.setValue(TechnicalInformation.Field.TITLE, "Bias Plus Variance Decomposition for Zero-One Loss Functions");
/*   63: 243 */     additional.setValue(TechnicalInformation.Field.BOOKTITLE, "Machine Learning: Proceedings of the Thirteenth International Conference");
/*   64: 244 */     additional.setValue(TechnicalInformation.Field.PUBLISHER, "Morgan Kaufmann");
/*   65: 245 */     additional.setValue(TechnicalInformation.Field.EDITOR, "Lorenza Saitta");
/*   66: 246 */     additional.setValue(TechnicalInformation.Field.PAGES, "275-283");
/*   67: 247 */     additional.setValue(TechnicalInformation.Field.PS, "http://robotics.stanford.edu/~ronnyk/biasVar.ps");
/*   68:     */     
/*   69: 249 */     additional = result.add(TechnicalInformation.Type.ARTICLE);
/*   70: 250 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "Geoffrey I. Webb");
/*   71: 251 */     additional.setValue(TechnicalInformation.Field.YEAR, "2000");
/*   72: 252 */     additional.setValue(TechnicalInformation.Field.TITLE, "MultiBoosting: A Technique for Combining Boosting and Wagging");
/*   73: 253 */     additional.setValue(TechnicalInformation.Field.JOURNAL, "Machine Learning");
/*   74: 254 */     additional.setValue(TechnicalInformation.Field.VOLUME, "40");
/*   75: 255 */     additional.setValue(TechnicalInformation.Field.NUMBER, "2");
/*   76: 256 */     additional.setValue(TechnicalInformation.Field.PAGES, "159-196");
/*   77:     */     
/*   78: 258 */     return result;
/*   79:     */   }
/*   80:     */   
/*   81:     */   public Enumeration<Option> listOptions()
/*   82:     */   {
/*   83: 268 */     Vector<Option> newVector = new Vector(8);
/*   84:     */     
/*   85: 270 */     newVector.addElement(new Option("\tThe index of the class attribute.\n\t(default last)", "c", 1, "-c <class index>"));
/*   86:     */     
/*   87:     */ 
/*   88:     */ 
/*   89: 274 */     newVector.addElement(new Option("\tTurn on debugging output.", "D", 0, "-D"));
/*   90:     */     
/*   91:     */ 
/*   92: 277 */     newVector.addElement(new Option("\tThe number of times each instance is classified.\n\t(default 10)", "l", 1, "-l <num>"));
/*   93:     */     
/*   94:     */ 
/*   95:     */ 
/*   96: 281 */     newVector.addElement(new Option("\tThe average proportion of instances common between any two training sets", "p", 1, "-p <proportion of objects in common>"));
/*   97:     */     
/*   98:     */ 
/*   99: 284 */     newVector.addElement(new Option("\tThe random number seed used.", "s", 1, "-s <seed>"));
/*  100:     */     
/*  101:     */ 
/*  102: 287 */     newVector.addElement(new Option("\tThe name of the arff file used for the decomposition.", "t", 1, "-t <name of arff file>"));
/*  103:     */     
/*  104:     */ 
/*  105: 290 */     newVector.addElement(new Option("\tThe number of instances in the training set.", "T", 1, "-T <number of instances in training set>"));
/*  106:     */     
/*  107:     */ 
/*  108: 293 */     newVector.addElement(new Option("\tFull class name of the learner used in the decomposition.\n\teg: weka.classifiers.bayes.NaiveBayes", "W", 1, "-W <classifier class name>"));
/*  109: 298 */     if ((this.m_Classifier != null) && ((this.m_Classifier instanceof OptionHandler)))
/*  110:     */     {
/*  111: 300 */       newVector.addElement(new Option("", "", 0, "\nOptions specific to learner " + this.m_Classifier.getClass().getName() + ":"));
/*  112:     */       
/*  113:     */ 
/*  114:     */ 
/*  115:     */ 
/*  116: 305 */       newVector.addAll(Collections.list(((OptionHandler)this.m_Classifier).listOptions()));
/*  117:     */     }
/*  118: 307 */     return newVector.elements();
/*  119:     */   }
/*  120:     */   
/*  121:     */   public void setOptions(String[] options)
/*  122:     */     throws Exception
/*  123:     */   {
/*  124: 360 */     setDebug(Utils.getFlag('D', options));
/*  125:     */     
/*  126: 362 */     String classIndex = Utils.getOption('c', options);
/*  127: 363 */     if (classIndex.length() != 0)
/*  128:     */     {
/*  129: 364 */       if (classIndex.toLowerCase().equals("last")) {
/*  130: 365 */         setClassIndex(0);
/*  131: 366 */       } else if (classIndex.toLowerCase().equals("first")) {
/*  132: 367 */         setClassIndex(1);
/*  133:     */       } else {
/*  134: 369 */         setClassIndex(Integer.parseInt(classIndex));
/*  135:     */       }
/*  136:     */     }
/*  137:     */     else {
/*  138: 372 */       setClassIndex(0);
/*  139:     */     }
/*  140: 375 */     String classifyIterations = Utils.getOption('l', options);
/*  141: 376 */     if (classifyIterations.length() != 0) {
/*  142: 377 */       setClassifyIterations(Integer.parseInt(classifyIterations));
/*  143:     */     } else {
/*  144: 379 */       setClassifyIterations(10);
/*  145:     */     }
/*  146: 382 */     String prob = Utils.getOption('p', options);
/*  147: 383 */     if (prob.length() != 0) {
/*  148: 384 */       setP(Double.parseDouble(prob));
/*  149:     */     } else {
/*  150: 386 */       setP(-1.0D);
/*  151:     */     }
/*  152: 390 */     String seedString = Utils.getOption('s', options);
/*  153: 391 */     if (seedString.length() != 0) {
/*  154: 392 */       setSeed(Integer.parseInt(seedString));
/*  155:     */     } else {
/*  156: 394 */       setSeed(1);
/*  157:     */     }
/*  158: 397 */     String dataFile = Utils.getOption('t', options);
/*  159: 398 */     if (dataFile.length() != 0) {
/*  160: 399 */       setDataFileName(dataFile);
/*  161:     */     } else {
/*  162: 401 */       throw new Exception("An arff file must be specified with the -t option.");
/*  163:     */     }
/*  164: 405 */     String trainSize = Utils.getOption('T', options);
/*  165: 406 */     if (trainSize.length() != 0) {
/*  166: 407 */       setTrainSize(Integer.parseInt(trainSize));
/*  167:     */     } else {
/*  168: 409 */       setTrainSize(-1);
/*  169:     */     }
/*  170: 413 */     String classifierName = Utils.getOption('W', options);
/*  171: 414 */     if (classifierName.length() != 0) {
/*  172: 415 */       setClassifier(AbstractClassifier.forName(classifierName, Utils.partitionOptions(options)));
/*  173:     */     } else {
/*  174: 417 */       throw new Exception("A learner must be specified with the -W option.");
/*  175:     */     }
/*  176:     */   }
/*  177:     */   
/*  178:     */   public String[] getOptions()
/*  179:     */   {
/*  180: 428 */     String[] classifierOptions = new String[0];
/*  181: 429 */     if ((this.m_Classifier != null) && ((this.m_Classifier instanceof OptionHandler))) {
/*  182: 431 */       classifierOptions = ((OptionHandler)this.m_Classifier).getOptions();
/*  183:     */     }
/*  184: 433 */     String[] options = new String[classifierOptions.length + 14];
/*  185: 434 */     int current = 0;
/*  186: 435 */     if (getDebug()) {
/*  187: 436 */       options[(current++)] = "-D";
/*  188:     */     }
/*  189: 438 */     options[(current++)] = "-c";options[(current++)] = ("" + getClassIndex());
/*  190: 439 */     options[(current++)] = "-l";options[(current++)] = ("" + getClassifyIterations());
/*  191: 440 */     options[(current++)] = "-p";options[(current++)] = ("" + getP());
/*  192: 441 */     options[(current++)] = "-s";options[(current++)] = ("" + getSeed());
/*  193: 442 */     if (getDataFileName() != null)
/*  194:     */     {
/*  195: 443 */       options[(current++)] = "-t";options[(current++)] = ("" + getDataFileName());
/*  196:     */     }
/*  197: 445 */     options[(current++)] = "-T";options[(current++)] = ("" + getTrainSize());
/*  198: 446 */     if (getClassifier() != null)
/*  199:     */     {
/*  200: 447 */       options[(current++)] = "-W";
/*  201: 448 */       options[(current++)] = getClassifier().getClass().getName();
/*  202:     */     }
/*  203: 451 */     options[(current++)] = "--";
/*  204: 452 */     System.arraycopy(classifierOptions, 0, options, current, classifierOptions.length);
/*  205:     */     
/*  206: 454 */     current += classifierOptions.length;
/*  207: 455 */     while (current < options.length) {
/*  208: 456 */       options[(current++)] = "";
/*  209:     */     }
/*  210: 458 */     return options;
/*  211:     */   }
/*  212:     */   
/*  213:     */   public void setClassifier(Classifier newClassifier)
/*  214:     */   {
/*  215: 468 */     this.m_Classifier = newClassifier;
/*  216:     */   }
/*  217:     */   
/*  218:     */   public Classifier getClassifier()
/*  219:     */   {
/*  220: 478 */     return this.m_Classifier;
/*  221:     */   }
/*  222:     */   
/*  223:     */   public void setDebug(boolean debug)
/*  224:     */   {
/*  225: 488 */     this.m_Debug = debug;
/*  226:     */   }
/*  227:     */   
/*  228:     */   public boolean getDebug()
/*  229:     */   {
/*  230: 498 */     return this.m_Debug;
/*  231:     */   }
/*  232:     */   
/*  233:     */   public void setSeed(int seed)
/*  234:     */   {
/*  235: 509 */     this.m_Seed = seed;
/*  236:     */   }
/*  237:     */   
/*  238:     */   public int getSeed()
/*  239:     */   {
/*  240: 519 */     return this.m_Seed;
/*  241:     */   }
/*  242:     */   
/*  243:     */   public void setClassifyIterations(int classifyIterations)
/*  244:     */   {
/*  245: 529 */     this.m_ClassifyIterations = classifyIterations;
/*  246:     */   }
/*  247:     */   
/*  248:     */   public int getClassifyIterations()
/*  249:     */   {
/*  250: 539 */     return this.m_ClassifyIterations;
/*  251:     */   }
/*  252:     */   
/*  253:     */   public void setDataFileName(String dataFileName)
/*  254:     */   {
/*  255: 549 */     this.m_DataFileName = dataFileName;
/*  256:     */   }
/*  257:     */   
/*  258:     */   public String getDataFileName()
/*  259:     */   {
/*  260: 559 */     return this.m_DataFileName;
/*  261:     */   }
/*  262:     */   
/*  263:     */   public int getClassIndex()
/*  264:     */   {
/*  265: 569 */     return this.m_ClassIndex + 1;
/*  266:     */   }
/*  267:     */   
/*  268:     */   public void setClassIndex(int classIndex)
/*  269:     */   {
/*  270: 579 */     this.m_ClassIndex = (classIndex - 1);
/*  271:     */   }
/*  272:     */   
/*  273:     */   public double getKWBias()
/*  274:     */   {
/*  275: 589 */     return this.m_KWBias;
/*  276:     */   }
/*  277:     */   
/*  278:     */   public double getWBias()
/*  279:     */   {
/*  280: 600 */     return this.m_WBias;
/*  281:     */   }
/*  282:     */   
/*  283:     */   public double getKWVariance()
/*  284:     */   {
/*  285: 611 */     return this.m_KWVariance;
/*  286:     */   }
/*  287:     */   
/*  288:     */   public double getWVariance()
/*  289:     */   {
/*  290: 622 */     return this.m_WVariance;
/*  291:     */   }
/*  292:     */   
/*  293:     */   public double getKWSigma()
/*  294:     */   {
/*  295: 633 */     return this.m_KWSigma;
/*  296:     */   }
/*  297:     */   
/*  298:     */   public void setTrainSize(int size)
/*  299:     */   {
/*  300: 644 */     this.m_TrainSize = size;
/*  301:     */   }
/*  302:     */   
/*  303:     */   public int getTrainSize()
/*  304:     */   {
/*  305: 655 */     return this.m_TrainSize;
/*  306:     */   }
/*  307:     */   
/*  308:     */   public void setP(double proportion)
/*  309:     */   {
/*  310: 668 */     this.m_P = proportion;
/*  311:     */   }
/*  312:     */   
/*  313:     */   public double getP()
/*  314:     */   {
/*  315: 679 */     return this.m_P;
/*  316:     */   }
/*  317:     */   
/*  318:     */   public double getError()
/*  319:     */   {
/*  320: 689 */     return this.m_Error;
/*  321:     */   }
/*  322:     */   
/*  323:     */   public void decompose()
/*  324:     */     throws Exception
/*  325:     */   {
/*  326: 706 */     Reader dataReader = new BufferedReader(new FileReader(this.m_DataFileName));
/*  327: 707 */     Instances data = new Instances(dataReader);
/*  328: 709 */     if (this.m_ClassIndex < 0) {
/*  329: 710 */       data.setClassIndex(data.numAttributes() - 1);
/*  330:     */     } else {
/*  331: 712 */       data.setClassIndex(this.m_ClassIndex);
/*  332:     */     }
/*  333: 715 */     if (data.classAttribute().type() != 1) {
/*  334: 716 */       throw new Exception("Class attribute must be nominal");
/*  335:     */     }
/*  336: 718 */     int numClasses = data.numClasses();
/*  337:     */     
/*  338: 720 */     data.deleteWithMissingClass();
/*  339: 721 */     if (data.checkForStringAttributes()) {
/*  340: 722 */       throw new Exception("Can't handle string attributes!");
/*  341:     */     }
/*  342: 726 */     if (data.numInstances() <= 2) {
/*  343: 727 */       throw new Exception("Dataset size must be greater than 2.");
/*  344:     */     }
/*  345: 730 */     if (this.m_TrainSize == -1) {
/*  346: 731 */       this.m_TrainSize = ((int)Math.floor(data.numInstances() / 2.0D));
/*  347: 732 */     } else if ((this.m_TrainSize < 0) || (this.m_TrainSize >= data.numInstances() - 1)) {
/*  348: 733 */       throw new Exception("Training set size of " + this.m_TrainSize + " is invalid.");
/*  349:     */     }
/*  350: 736 */     if (this.m_P == -1.0D) {
/*  351: 737 */       this.m_P = (this.m_TrainSize / (data.numInstances() - 1.0D));
/*  352: 738 */     } else if ((this.m_P < this.m_TrainSize / (data.numInstances() - 1.0D)) || (this.m_P >= 1.0D)) {
/*  353: 739 */       throw new Exception("Proportion is not in range: " + this.m_TrainSize / (data.numInstances() - 1.0D) + " <= p < 1.0 ");
/*  354:     */     }
/*  355: 743 */     int tps = (int)Math.ceil(this.m_TrainSize / this.m_P + 1.0D);
/*  356: 744 */     int k = (int)Math.ceil(tps / (tps - this.m_TrainSize));
/*  357: 747 */     if (k > tps) {
/*  358: 748 */       throw new Exception("The required number of folds is too many.Change p or the size of the training set.");
/*  359:     */     }
/*  360: 753 */     int q = (int)Math.floor(data.numInstances() / tps);
/*  361:     */     
/*  362:     */ 
/*  363: 756 */     double[][] instanceProbs = new double[data.numInstances()][numClasses];
/*  364: 757 */     int[][] foldIndex = new int[k][2];
/*  365: 758 */     Vector<int[]> segmentList = new Vector(q + 1);
/*  366:     */     
/*  367:     */ 
/*  368: 761 */     Random random = new Random(this.m_Seed);
/*  369:     */     
/*  370: 763 */     data.randomize(random);
/*  371:     */     
/*  372:     */ 
/*  373:     */ 
/*  374: 767 */     int currentDataIndex = 0;
/*  375: 769 */     for (int count = 1; count <= q + 1; count++) {
/*  376: 770 */       if (count > q)
/*  377:     */       {
/*  378: 771 */         int[] segmentIndex = new int[data.numInstances() - q * tps];
/*  379: 772 */         for (int index = 0; index < segmentIndex.length; currentDataIndex++)
/*  380:     */         {
/*  381: 774 */           segmentIndex[index] = currentDataIndex;index++;
/*  382:     */         }
/*  383: 776 */         segmentList.add(segmentIndex);
/*  384:     */       }
/*  385:     */       else
/*  386:     */       {
/*  387: 778 */         int[] segmentIndex = new int[tps];
/*  388: 780 */         for (int index = 0; index < segmentIndex.length; currentDataIndex++)
/*  389:     */         {
/*  390: 781 */           segmentIndex[index] = currentDataIndex;index++;
/*  391:     */         }
/*  392: 783 */         segmentList.add(segmentIndex);
/*  393:     */       }
/*  394:     */     }
/*  395: 787 */     int remainder = tps % k;
/*  396:     */     
/*  397:     */ 
/*  398: 790 */     int foldSize = (int)Math.ceil(tps / k);
/*  399: 791 */     int index = 0;
/*  400: 794 */     for (int count = 0; count < k; count++)
/*  401:     */     {
/*  402: 795 */       if ((remainder != 0) && (count == remainder)) {
/*  403: 796 */         foldSize--;
/*  404:     */       }
/*  405: 798 */       foldIndex[count][0] = index;
/*  406: 799 */       foldIndex[count][1] = foldSize;
/*  407: 800 */       index += foldSize;
/*  408:     */     }
/*  409: 803 */     for (int l = 0; l < this.m_ClassifyIterations; l++) {
/*  410: 805 */       for (int i = 1; i <= q; i++)
/*  411:     */       {
/*  412: 807 */         int[] currentSegment = (int[])segmentList.get(i - 1);
/*  413:     */         
/*  414: 809 */         randomize(currentSegment, random);
/*  415: 812 */         for (int j = 1; j <= k; j++)
/*  416:     */         {
/*  417: 814 */           Instances TP = null;
/*  418: 815 */           for (int foldNum = 1; foldNum <= k; foldNum++) {
/*  419: 816 */             if (foldNum != j)
/*  420:     */             {
/*  421: 818 */               int startFoldIndex = foldIndex[(foldNum - 1)][0];
/*  422: 819 */               foldSize = foldIndex[(foldNum - 1)][1];
/*  423: 820 */               int endFoldIndex = startFoldIndex + foldSize - 1;
/*  424: 822 */               for (int currentFoldIndex = startFoldIndex; currentFoldIndex <= endFoldIndex; currentFoldIndex++) {
/*  425: 824 */                 if (TP == null) {
/*  426: 825 */                   TP = new Instances(data, currentSegment[currentFoldIndex], 1);
/*  427:     */                 } else {
/*  428: 827 */                   TP.add(data.instance(currentSegment[currentFoldIndex]));
/*  429:     */                 }
/*  430:     */               }
/*  431:     */             }
/*  432:     */           }
/*  433: 833 */           TP.randomize(random);
/*  434: 835 */           if (getTrainSize() > TP.numInstances()) {
/*  435: 836 */             throw new Exception("The training set size of " + getTrainSize() + ", is greater than the training pool " + TP.numInstances());
/*  436:     */           }
/*  437: 840 */           Instances train = new Instances(TP, 0, this.m_TrainSize);
/*  438:     */           
/*  439: 842 */           Classifier current = AbstractClassifier.makeCopy(this.m_Classifier);
/*  440: 843 */           current.buildClassifier(train);
/*  441:     */           
/*  442: 845 */           int currentTestIndex = foldIndex[(j - 1)][0];
/*  443: 846 */           int testFoldSize = foldIndex[(j - 1)][1];
/*  444: 847 */           int endTestIndex = currentTestIndex + testFoldSize - 1;
/*  445: 849 */           while (currentTestIndex <= endTestIndex)
/*  446:     */           {
/*  447: 851 */             Instance testInst = data.instance(currentSegment[currentTestIndex]);
/*  448: 852 */             int pred = (int)current.classifyInstance(testInst);
/*  449: 855 */             if (pred != testInst.classValue()) {
/*  450: 856 */               this.m_Error += 1.0D;
/*  451:     */             }
/*  452: 858 */             instanceProbs[currentSegment[currentTestIndex]][pred] += 1.0D;
/*  453: 859 */             currentTestIndex++;
/*  454:     */           }
/*  455: 862 */           if ((i == 1) && (j == 1))
/*  456:     */           {
/*  457: 863 */             int[] segmentElast = (int[])segmentList.lastElement();
/*  458: 864 */             for (int currentIndex = 0; currentIndex < segmentElast.length; currentIndex++)
/*  459:     */             {
/*  460: 865 */               Instance testInst = data.instance(segmentElast[currentIndex]);
/*  461: 866 */               int pred = (int)current.classifyInstance(testInst);
/*  462: 867 */               if (pred != testInst.classValue()) {
/*  463: 868 */                 this.m_Error += 1.0D;
/*  464:     */               }
/*  465: 871 */               instanceProbs[segmentElast[currentIndex]][pred] += 1.0D;
/*  466:     */             }
/*  467:     */           }
/*  468:     */         }
/*  469:     */       }
/*  470:     */     }
/*  471: 878 */     this.m_Error /= this.m_ClassifyIterations * data.numInstances();
/*  472:     */     
/*  473: 880 */     this.m_KWBias = 0.0D;
/*  474: 881 */     this.m_KWVariance = 0.0D;
/*  475: 882 */     this.m_KWSigma = 0.0D;
/*  476:     */     
/*  477: 884 */     this.m_WBias = 0.0D;
/*  478: 885 */     this.m_WVariance = 0.0D;
/*  479: 887 */     for (int i = 0; i < data.numInstances(); i++)
/*  480:     */     {
/*  481: 889 */       Instance current = data.instance(i);
/*  482:     */       
/*  483: 891 */       double[] predProbs = instanceProbs[i];
/*  484:     */       
/*  485: 893 */       double bsum = 0.0D;double vsum = 0.0D;double ssum = 0.0D;
/*  486: 894 */       double wBSum = 0.0D;double wVSum = 0.0D;
/*  487:     */       
/*  488: 896 */       Vector<Integer> centralTendencies = findCentralTendencies(predProbs);
/*  489: 898 */       if (centralTendencies == null) {
/*  490: 899 */         throw new Exception("Central tendency was null.");
/*  491:     */       }
/*  492: 902 */       for (int j = 0; j < numClasses; j++)
/*  493:     */       {
/*  494: 903 */         double pActual = current.classValue() == j ? 1.0D : 0.0D;
/*  495: 904 */         double pPred = predProbs[j] / this.m_ClassifyIterations;
/*  496: 905 */         bsum += (pActual - pPred) * (pActual - pPred) - pPred * (1.0D - pPred) / (this.m_ClassifyIterations - 1);
/*  497: 906 */         vsum += pPred * pPred;
/*  498: 907 */         ssum += pActual * pActual;
/*  499:     */       }
/*  500: 910 */       this.m_KWBias += bsum;
/*  501: 911 */       this.m_KWVariance += 1.0D - vsum;
/*  502: 912 */       this.m_KWSigma += 1.0D - ssum;
/*  503: 914 */       for (int count = 0; count < centralTendencies.size(); count++)
/*  504:     */       {
/*  505: 916 */         int wB = 0;int wV = 0;
/*  506: 917 */         int centralTendency = ((Integer)centralTendencies.get(count)).intValue();
/*  507: 920 */         for (int j = 0; j < numClasses; j++)
/*  508:     */         {
/*  509: 923 */           if ((j != (int)current.classValue()) && (j == centralTendency)) {
/*  510: 924 */             wB = (int)(wB + predProbs[j]);
/*  511:     */           }
/*  512: 926 */           if ((j != (int)current.classValue()) && (j != centralTendency)) {
/*  513: 927 */             wV = (int)(wV + predProbs[j]);
/*  514:     */           }
/*  515:     */         }
/*  516: 931 */         wBSum += wB;
/*  517: 932 */         wVSum += wV;
/*  518:     */       }
/*  519: 939 */       this.m_WBias += wBSum / (centralTendencies.size() * this.m_ClassifyIterations);
/*  520:     */       
/*  521: 941 */       this.m_WVariance += wVSum / (centralTendencies.size() * this.m_ClassifyIterations);
/*  522:     */     }
/*  523: 945 */     this.m_KWBias /= 2.0D * data.numInstances();
/*  524: 946 */     this.m_KWVariance /= 2.0D * data.numInstances();
/*  525: 947 */     this.m_KWSigma /= 2.0D * data.numInstances();
/*  526:     */     
/*  527:     */ 
/*  528: 950 */     this.m_WBias /= data.numInstances();
/*  529:     */     
/*  530: 952 */     this.m_WVariance /= data.numInstances();
/*  531: 954 */     if (this.m_Debug) {
/*  532: 955 */       System.err.println("Decomposition finished");
/*  533:     */     }
/*  534:     */   }
/*  535:     */   
/*  536:     */   public Vector<Integer> findCentralTendencies(double[] predProbs)
/*  537:     */   {
/*  538: 983 */     int centralTValue = 0;
/*  539: 984 */     int currentValue = 0;
/*  540:     */     
/*  541:     */ 
/*  542:     */ 
/*  543: 988 */     Vector<Integer> centralTClasses = new Vector();
/*  544: 991 */     for (int i = 0; i < predProbs.length; i++)
/*  545:     */     {
/*  546: 992 */       currentValue = (int)predProbs[i];
/*  547: 995 */       if (currentValue > centralTValue)
/*  548:     */       {
/*  549: 996 */         centralTClasses.clear();
/*  550: 997 */         centralTClasses.addElement(new Integer(i));
/*  551: 998 */         centralTValue = currentValue;
/*  552:     */       }
/*  553: 999 */       else if ((currentValue != 0) && (currentValue == centralTValue))
/*  554:     */       {
/*  555:1000 */         centralTClasses.addElement(new Integer(i));
/*  556:     */       }
/*  557:     */     }
/*  558:1004 */     if (centralTValue != 0) {
/*  559:1005 */       return centralTClasses;
/*  560:     */     }
/*  561:1007 */     return null;
/*  562:     */   }
/*  563:     */   
/*  564:     */   public String toString()
/*  565:     */   {
/*  566:1019 */     String result = "\nBias-Variance Decomposition Segmentation, Cross Validation\nwith subsampling.\n";
/*  567:1022 */     if (getClassifier() == null) {
/*  568:1023 */       return "Invalid setup";
/*  569:     */     }
/*  570:1026 */     result = result + "\nClassifier    : " + getClassifier().getClass().getName();
/*  571:1027 */     if ((getClassifier() instanceof OptionHandler)) {
/*  572:1028 */       result = result + Utils.joinOptions(((OptionHandler)this.m_Classifier).getOptions());
/*  573:     */     }
/*  574:1030 */     result = result + "\nData File     : " + getDataFileName();
/*  575:1031 */     result = result + "\nClass Index   : ";
/*  576:1032 */     if (getClassIndex() == 0) {
/*  577:1033 */       result = result + "last";
/*  578:     */     } else {
/*  579:1035 */       result = result + getClassIndex();
/*  580:     */     }
/*  581:1037 */     result = result + "\nIterations    : " + getClassifyIterations();
/*  582:1038 */     result = result + "\np             : " + getP();
/*  583:1039 */     result = result + "\nTraining Size : " + getTrainSize();
/*  584:1040 */     result = result + "\nSeed          : " + getSeed();
/*  585:     */     
/*  586:1042 */     result = result + "\n\nDefinition   : Kohavi and Wolpert";
/*  587:1043 */     result = result + "\nError         :" + Utils.doubleToString(getError(), 4);
/*  588:1044 */     result = result + "\nBias^2        :" + Utils.doubleToString(getKWBias(), 4);
/*  589:1045 */     result = result + "\nVariance      :" + Utils.doubleToString(getKWVariance(), 4);
/*  590:1046 */     result = result + "\nSigma^2       :" + Utils.doubleToString(getKWSigma(), 4);
/*  591:     */     
/*  592:1048 */     result = result + "\n\nDefinition   : Webb";
/*  593:1049 */     result = result + "\nError         :" + Utils.doubleToString(getError(), 4);
/*  594:1050 */     result = result + "\nBias          :" + Utils.doubleToString(getWBias(), 4);
/*  595:1051 */     result = result + "\nVariance      :" + Utils.doubleToString(getWVariance(), 4);
/*  596:     */     
/*  597:1053 */     return result;
/*  598:     */   }
/*  599:     */   
/*  600:     */   public String getRevision()
/*  601:     */   {
/*  602:1062 */     return RevisionUtils.extract("$Revision: 10141 $");
/*  603:     */   }
/*  604:     */   
/*  605:     */   public static void main(String[] args)
/*  606:     */   {
/*  607:     */     try
/*  608:     */     {
/*  609:1073 */       BVDecomposeSegCVSub bvd = new BVDecomposeSegCVSub();
/*  610:     */       try
/*  611:     */       {
/*  612:1076 */         bvd.setOptions(args);
/*  613:1077 */         Utils.checkForRemainingOptions(args);
/*  614:     */       }
/*  615:     */       catch (Exception ex)
/*  616:     */       {
/*  617:1079 */         String result = ex.getMessage() + "\nBVDecompose Options:\n\n";
/*  618:1080 */         Enumeration<Option> enu = bvd.listOptions();
/*  619:1081 */         while (enu.hasMoreElements())
/*  620:     */         {
/*  621:1082 */           Option option = (Option)enu.nextElement();
/*  622:1083 */           result = result + option.synopsis() + "\n" + option.description() + "\n";
/*  623:     */         }
/*  624:1085 */         throw new Exception(result);
/*  625:     */       }
/*  626:1088 */       bvd.decompose();
/*  627:     */       
/*  628:1090 */       System.out.println(bvd.toString());
/*  629:     */     }
/*  630:     */     catch (Exception ex)
/*  631:     */     {
/*  632:1093 */       System.err.println(ex.getMessage());
/*  633:     */     }
/*  634:     */   }
/*  635:     */   
/*  636:     */   public final void randomize(int[] index, Random random)
/*  637:     */   {
/*  638:1106 */     for (int j = index.length - 1; j > 0; j--)
/*  639:     */     {
/*  640:1107 */       int k = random.nextInt(j + 1);
/*  641:1108 */       int temp = index[j];
/*  642:1109 */       index[j] = index[k];
/*  643:1110 */       index[k] = temp;
/*  644:     */     }
/*  645:     */   }
/*  646:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.BVDecomposeSegCVSub
 * JD-Core Version:    0.7.0.1
 */