/*    1:     */ package weka.classifiers.functions;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.util.Collections;
/*    5:     */ import java.util.Enumeration;
/*    6:     */ import java.util.HashSet;
/*    7:     */ import java.util.Random;
/*    8:     */ import java.util.Set;
/*    9:     */ import java.util.Vector;
/*   10:     */ import java.util.concurrent.Callable;
/*   11:     */ import java.util.concurrent.ExecutorService;
/*   12:     */ import java.util.concurrent.Executors;
/*   13:     */ import java.util.concurrent.Future;
/*   14:     */ import weka.classifiers.Classifier;
/*   15:     */ import weka.classifiers.RandomizableClassifier;
/*   16:     */ import weka.classifiers.rules.ZeroR;
/*   17:     */ import weka.clusterers.SimpleKMeans;
/*   18:     */ import weka.core.Capabilities;
/*   19:     */ import weka.core.Capabilities.Capability;
/*   20:     */ import weka.core.ConjugateGradientOptimization;
/*   21:     */ import weka.core.Instance;
/*   22:     */ import weka.core.Instances;
/*   23:     */ import weka.core.Optimization;
/*   24:     */ import weka.core.Option;
/*   25:     */ import weka.core.RevisionUtils;
/*   26:     */ import weka.core.SelectedTag;
/*   27:     */ import weka.core.Tag;
/*   28:     */ import weka.core.TechnicalInformation;
/*   29:     */ import weka.core.TechnicalInformation.Field;
/*   30:     */ import weka.core.TechnicalInformation.Type;
/*   31:     */ import weka.core.TechnicalInformationHandler;
/*   32:     */ import weka.core.Utils;
/*   33:     */ import weka.filters.Filter;
/*   34:     */ import weka.filters.unsupervised.attribute.NominalToBinary;
/*   35:     */ import weka.filters.unsupervised.attribute.Normalize;
/*   36:     */ import weka.filters.unsupervised.attribute.Remove;
/*   37:     */ import weka.filters.unsupervised.attribute.RemoveUseless;
/*   38:     */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*   39:     */ 
/*   40:     */ public abstract class RBFModel
/*   41:     */   extends RandomizableClassifier
/*   42:     */   implements TechnicalInformationHandler
/*   43:     */ {
/*   44:     */   private static final long serialVersionUID = -7847473336438394611L;
/*   45:     */   public static final int USE_GLOBAL_SCALE = 1;
/*   46:     */   public static final int USE_SCALE_PER_UNIT = 2;
/*   47:     */   public static final int USE_SCALE_PER_UNIT_AND_ATTRIBUTE = 3;
/*   48:     */   
/*   49:     */   public Capabilities getCapabilities()
/*   50:     */   {
/*   51:  76 */     Capabilities result = super.getCapabilities();
/*   52:  77 */     result.disableAll();
/*   53:     */     
/*   54:     */ 
/*   55:  80 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*   56:  81 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*   57:  82 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*   58:  83 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*   59:     */     
/*   60:  85 */     return result;
/*   61:     */   }
/*   62:     */   
/*   63:     */   public TechnicalInformation getTechnicalInformation()
/*   64:     */   {
/*   65: 100 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.TECHREPORT);
/*   66: 101 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Eibe Frank");
/*   67: 102 */     result.setValue(TechnicalInformation.Field.TITLE, "Fully supervised training of Gaussian radial basis function networks in WEKA");
/*   68:     */     
/*   69: 104 */     result.setValue(TechnicalInformation.Field.YEAR, "2014");
/*   70: 105 */     result.setValue(TechnicalInformation.Field.NUMBER, "04/14");
/*   71: 106 */     result.setValue(TechnicalInformation.Field.ADDRESS, "Department of Computer Science, University of Waikato");
/*   72:     */     
/*   73: 108 */     return result;
/*   74:     */   }
/*   75:     */   
/*   76:     */   protected class OptEng
/*   77:     */     extends Optimization
/*   78:     */   {
/*   79:     */     protected OptEng() {}
/*   80:     */     
/*   81:     */     protected double objectiveFunction(double[] x)
/*   82:     */     {
/*   83: 122 */       RBFModel.this.m_RBFParameters = x;
/*   84: 123 */       return RBFModel.this.calculateSE();
/*   85:     */     }
/*   86:     */     
/*   87:     */     protected double[] evaluateGradient(double[] x)
/*   88:     */     {
/*   89: 132 */       RBFModel.this.m_RBFParameters = x;
/*   90: 133 */       return RBFModel.this.calculateGradient();
/*   91:     */     }
/*   92:     */     
/*   93:     */     public String getRevision()
/*   94:     */     {
/*   95: 141 */       return RevisionUtils.extract("$Revision: 8966 $");
/*   96:     */     }
/*   97:     */   }
/*   98:     */   
/*   99:     */   protected class OptEngCGD
/*  100:     */     extends ConjugateGradientOptimization
/*  101:     */   {
/*  102:     */     protected OptEngCGD() {}
/*  103:     */     
/*  104:     */     protected double objectiveFunction(double[] x)
/*  105:     */     {
/*  106: 157 */       RBFModel.this.m_RBFParameters = x;
/*  107: 158 */       return RBFModel.this.calculateSE();
/*  108:     */     }
/*  109:     */     
/*  110:     */     protected double[] evaluateGradient(double[] x)
/*  111:     */     {
/*  112: 167 */       RBFModel.this.m_RBFParameters = x;
/*  113: 168 */       return RBFModel.this.calculateGradient();
/*  114:     */     }
/*  115:     */     
/*  116:     */     public String getRevision()
/*  117:     */     {
/*  118: 176 */       return RevisionUtils.extract("$Revision: 8966 $");
/*  119:     */     }
/*  120:     */   }
/*  121:     */   
/*  122: 184 */   public static final Tag[] TAGS_SCALE = { new Tag(1, "Use global scale"), new Tag(2, "Use scale per unit"), new Tag(3, "Use scale per unit and attribute") };
/*  123: 191 */   protected int m_scaleOptimizationOption = 2;
/*  124: 194 */   protected int m_numUnits = 2;
/*  125: 197 */   protected int m_classIndex = -1;
/*  126: 200 */   protected Instances m_data = null;
/*  127: 203 */   protected int m_numAttributes = -1;
/*  128: 206 */   protected double[] m_RBFParameters = null;
/*  129: 209 */   protected double m_ridge = 0.01D;
/*  130: 212 */   protected boolean m_useCGD = false;
/*  131: 215 */   protected boolean m_useNormalizedBasisFunctions = false;
/*  132: 218 */   protected boolean m_useAttributeWeights = false;
/*  133: 221 */   protected double m_tolerance = 1.0E-006D;
/*  134: 224 */   protected int m_numThreads = 1;
/*  135: 227 */   protected int m_poolSize = 1;
/*  136: 230 */   protected Filter m_Filter = null;
/*  137: 233 */   protected int OFFSET_WEIGHTS = -1;
/*  138: 234 */   protected int OFFSET_SCALES = -1;
/*  139: 235 */   protected int OFFSET_CENTERS = -1;
/*  140: 236 */   protected int OFFSET_ATTRIBUTE_WEIGHTS = -1;
/*  141:     */   protected RemoveUseless m_AttFilter;
/*  142:     */   protected NominalToBinary m_NominalToBinary;
/*  143:     */   protected ReplaceMissingValues m_ReplaceMissingValues;
/*  144:     */   protected Classifier m_ZeroR;
/*  145: 251 */   protected transient ExecutorService m_Pool = null;
/*  146: 254 */   protected int m_numClasses = -1;
/*  147: 258 */   protected double m_x1 = 1.0D;
/*  148: 259 */   protected double m_x0 = 0.0D;
/*  149:     */   
/*  150:     */   protected Instances initializeClassifier(Instances data)
/*  151:     */     throws Exception
/*  152:     */   {
/*  153: 268 */     getCapabilities().testWithFail(data);
/*  154:     */     
/*  155: 270 */     data = new Instances(data);
/*  156: 271 */     data.deleteWithMissingClass();
/*  157:     */     
/*  158:     */ 
/*  159: 274 */     Random random = new Random(this.m_Seed);
/*  160: 275 */     if (data.numInstances() > 2) {
/*  161: 276 */       random = data.getRandomNumberGenerator(this.m_Seed);
/*  162:     */     }
/*  163: 278 */     data.randomize(random);
/*  164:     */     
/*  165: 280 */     double y0 = data.instance(0).classValue();
/*  166:     */     
/*  167: 282 */     int index = 1;
/*  168: 284 */     while ((index < data.numInstances()) && (data.instance(index).classValue() == y0)) {
/*  169: 285 */       index++;
/*  170:     */     }
/*  171: 287 */     if (index == data.numInstances()) {
/*  172: 290 */       throw new Exception("All class values are the same. At least two class values should be different");
/*  173:     */     }
/*  174: 293 */     double y1 = data.instance(index).classValue();
/*  175:     */     
/*  176:     */ 
/*  177: 296 */     this.m_ReplaceMissingValues = new ReplaceMissingValues();
/*  178: 297 */     this.m_ReplaceMissingValues.setInputFormat(data);
/*  179: 298 */     data = Filter.useFilter(data, this.m_ReplaceMissingValues);
/*  180:     */     
/*  181:     */ 
/*  182: 301 */     this.m_AttFilter = new RemoveUseless();
/*  183: 302 */     this.m_AttFilter.setInputFormat(data);
/*  184: 303 */     data = Filter.useFilter(data, this.m_AttFilter);
/*  185: 306 */     if (data.numAttributes() == 1)
/*  186:     */     {
/*  187: 307 */       System.err.println("Cannot build model (only class attribute present in data after removing useless attributes!), using ZeroR model instead!");
/*  188:     */       
/*  189:     */ 
/*  190: 310 */       this.m_ZeroR = new ZeroR();
/*  191: 311 */       this.m_ZeroR.buildClassifier(data);
/*  192: 312 */       return data;
/*  193:     */     }
/*  194: 314 */     this.m_ZeroR = null;
/*  195:     */     
/*  196:     */ 
/*  197:     */ 
/*  198: 318 */     this.m_NominalToBinary = new NominalToBinary();
/*  199: 319 */     this.m_NominalToBinary.setInputFormat(data);
/*  200: 320 */     data = Filter.useFilter(data, this.m_NominalToBinary);
/*  201:     */     
/*  202: 322 */     this.m_Filter = new Normalize();
/*  203: 323 */     ((Normalize)this.m_Filter).setIgnoreClass(true);
/*  204: 324 */     this.m_Filter.setInputFormat(data);
/*  205: 325 */     data = Filter.useFilter(data, this.m_Filter);
/*  206: 326 */     double z0 = data.instance(0).classValue();
/*  207:     */     
/*  208: 328 */     double z1 = data.instance(index).classValue();
/*  209: 329 */     this.m_x1 = ((y0 - y1) / (z0 - z1));
/*  210:     */     
/*  211: 331 */     this.m_x0 = (y0 - this.m_x1 * z0);
/*  212:     */     
/*  213: 333 */     this.m_classIndex = data.classIndex();
/*  214: 334 */     this.m_numClasses = data.numClasses();
/*  215: 335 */     this.m_numAttributes = data.numAttributes();
/*  216:     */     
/*  217:     */ 
/*  218: 338 */     SimpleKMeans skm = new SimpleKMeans();
/*  219: 339 */     skm.setMaxIterations(10000);
/*  220: 340 */     skm.setNumClusters(this.m_numUnits);
/*  221: 341 */     Remove rm = new Remove();
/*  222: 342 */     data.setClassIndex(-1);
/*  223: 343 */     rm.setAttributeIndices(this.m_classIndex + 1 + "");
/*  224: 344 */     rm.setInputFormat(data);
/*  225: 345 */     Instances dataRemoved = Filter.useFilter(data, rm);
/*  226: 346 */     data.setClassIndex(this.m_classIndex);
/*  227: 347 */     skm.buildClusterer(dataRemoved);
/*  228: 348 */     Instances centers = skm.getClusterCentroids();
/*  229: 350 */     if (centers.numInstances() < this.m_numUnits) {
/*  230: 351 */       this.m_numUnits = centers.numInstances();
/*  231:     */     }
/*  232: 355 */     this.OFFSET_WEIGHTS = 0;
/*  233: 356 */     if (this.m_useAttributeWeights)
/*  234:     */     {
/*  235: 357 */       this.OFFSET_ATTRIBUTE_WEIGHTS = ((this.m_numUnits + 1) * this.m_numClasses);
/*  236: 358 */       this.OFFSET_CENTERS = (this.OFFSET_ATTRIBUTE_WEIGHTS + this.m_numAttributes);
/*  237:     */     }
/*  238:     */     else
/*  239:     */     {
/*  240: 360 */       this.OFFSET_ATTRIBUTE_WEIGHTS = -1;
/*  241: 361 */       this.OFFSET_CENTERS = ((this.m_numUnits + 1) * this.m_numClasses);
/*  242:     */     }
/*  243: 363 */     this.OFFSET_SCALES = (this.OFFSET_CENTERS + this.m_numUnits * this.m_numAttributes);
/*  244: 365 */     switch (this.m_scaleOptimizationOption)
/*  245:     */     {
/*  246:     */     case 1: 
/*  247: 367 */       this.m_RBFParameters = new double[this.OFFSET_SCALES + 1];
/*  248: 368 */       break;
/*  249:     */     case 3: 
/*  250: 370 */       this.m_RBFParameters = new double[this.OFFSET_SCALES + this.m_numUnits * this.m_numAttributes];
/*  251: 371 */       break;
/*  252:     */     default: 
/*  253: 373 */       this.m_RBFParameters = new double[this.OFFSET_SCALES + this.m_numUnits];
/*  254:     */     }
/*  255: 378 */     double maxMinDist = -1.0D;
/*  256: 382 */     if (centers.numInstances() == 1)
/*  257:     */     {
/*  258: 383 */       Instance center = centers.instance(0);
/*  259: 384 */       for (int i = 0; i < dataRemoved.numInstances(); i++)
/*  260:     */       {
/*  261: 385 */         double dist = 0.0D;
/*  262: 386 */         for (int k = 0; k < centers.numAttributes(); k++)
/*  263:     */         {
/*  264: 387 */           double diff = dataRemoved.instance(i).value(k) - center.value(k);
/*  265:     */           
/*  266: 389 */           dist += diff * diff;
/*  267:     */         }
/*  268: 391 */         if (dist > maxMinDist) {
/*  269: 392 */           maxMinDist = dist;
/*  270:     */         }
/*  271:     */       }
/*  272:     */     }
/*  273:     */     else
/*  274:     */     {
/*  275: 398 */       for (int i = 0; i < centers.numInstances(); i++)
/*  276:     */       {
/*  277: 399 */         double minDist = 1.7976931348623157E+308D;
/*  278: 400 */         for (int j = i + 1; j < centers.numInstances(); j++)
/*  279:     */         {
/*  280: 401 */           double dist = 0.0D;
/*  281: 402 */           for (int k = 0; k < centers.numAttributes(); k++) {
/*  282: 403 */             if (k != centers.classIndex())
/*  283:     */             {
/*  284: 404 */               double diff = centers.instance(i).value(k) - centers.instance(j).value(k);
/*  285:     */               
/*  286: 406 */               dist += diff * diff;
/*  287:     */             }
/*  288:     */           }
/*  289: 409 */           if (dist < minDist) {
/*  290: 410 */             minDist = dist;
/*  291:     */           }
/*  292:     */         }
/*  293: 413 */         if ((minDist != 1.7976931348623157E+308D) && (minDist > maxMinDist)) {
/*  294: 414 */           maxMinDist = minDist;
/*  295:     */         }
/*  296:     */       }
/*  297:     */     }
/*  298: 420 */     if (this.m_scaleOptimizationOption == 1) {
/*  299: 421 */       this.m_RBFParameters[this.OFFSET_SCALES] = Math.sqrt(maxMinDist);
/*  300:     */     }
/*  301: 423 */     for (int i = 0; i < this.m_numUnits; i++)
/*  302:     */     {
/*  303: 424 */       if (this.m_scaleOptimizationOption == 2) {
/*  304: 425 */         this.m_RBFParameters[(this.OFFSET_SCALES + i)] = Math.sqrt(maxMinDist);
/*  305:     */       }
/*  306: 427 */       int k = 0;
/*  307: 428 */       for (int j = 0; j < this.m_numAttributes; j++)
/*  308:     */       {
/*  309: 429 */         if (k == centers.classIndex()) {
/*  310: 430 */           k++;
/*  311:     */         }
/*  312: 432 */         if (j != data.classIndex())
/*  313:     */         {
/*  314: 433 */           if (this.m_scaleOptimizationOption == 3) {
/*  315: 434 */             this.m_RBFParameters[(this.OFFSET_SCALES + (i * this.m_numAttributes + j))] = Math.sqrt(maxMinDist);
/*  316:     */           }
/*  317: 437 */           this.m_RBFParameters[(this.OFFSET_CENTERS + i * this.m_numAttributes + j)] = centers.instance(i).value(k);
/*  318:     */           
/*  319: 439 */           k++;
/*  320:     */         }
/*  321:     */       }
/*  322:     */     }
/*  323: 444 */     if (this.m_useAttributeWeights) {
/*  324: 445 */       for (int j = 0; j < this.m_numAttributes; j++) {
/*  325: 446 */         if (j != data.classIndex()) {
/*  326: 447 */           this.m_RBFParameters[(this.OFFSET_ATTRIBUTE_WEIGHTS + j)] = 1.0D;
/*  327:     */         }
/*  328:     */       }
/*  329:     */     }
/*  330: 452 */     initializeOutputLayer(random);
/*  331:     */     
/*  332: 454 */     return data;
/*  333:     */   }
/*  334:     */   
/*  335:     */   protected abstract void initializeOutputLayer(Random paramRandom);
/*  336:     */   
/*  337:     */   public void buildClassifier(Instances data)
/*  338:     */     throws Exception
/*  339:     */   {
/*  340: 469 */     this.m_data = initializeClassifier(data);
/*  341: 471 */     if (this.m_ZeroR != null) {
/*  342: 472 */       return;
/*  343:     */     }
/*  344: 476 */     this.m_Pool = Executors.newFixedThreadPool(this.m_poolSize);
/*  345:     */     
/*  346:     */ 
/*  347: 479 */     Optimization opt = null;
/*  348: 480 */     if (!this.m_useCGD) {
/*  349: 481 */       opt = new OptEng();
/*  350:     */     } else {
/*  351: 483 */       opt = new OptEngCGD();
/*  352:     */     }
/*  353: 485 */     opt.setDebug(this.m_Debug);
/*  354:     */     
/*  355:     */ 
/*  356: 488 */     double[][] b = new double[2][this.m_RBFParameters.length];
/*  357: 489 */     for (int i = 0; i < 2; i++) {
/*  358: 490 */       for (int j = 0; j < this.m_RBFParameters.length; j++) {
/*  359: 491 */         b[i][j] = (0.0D / 0.0D);
/*  360:     */       }
/*  361:     */     }
/*  362: 495 */     this.m_RBFParameters = opt.findArgmin(this.m_RBFParameters, b);
/*  363: 496 */     while (this.m_RBFParameters == null)
/*  364:     */     {
/*  365: 497 */       this.m_RBFParameters = opt.getVarbValues();
/*  366: 498 */       if (this.m_Debug) {
/*  367: 499 */         System.out.println("200 iterations finished, not enough!");
/*  368:     */       }
/*  369: 501 */       this.m_RBFParameters = opt.findArgmin(this.m_RBFParameters, b);
/*  370:     */     }
/*  371: 503 */     if (this.m_Debug) {
/*  372: 504 */       System.out.println("SE (normalized space) after optimization: " + opt.getMinFunction());
/*  373:     */     }
/*  374: 508 */     this.m_data = new Instances(this.m_data, 0);
/*  375:     */     
/*  376:     */ 
/*  377: 511 */     this.m_Pool.shutdown();
/*  378:     */   }
/*  379:     */   
/*  380:     */   protected abstract double calculateError(double[] paramArrayOfDouble, Instance paramInstance);
/*  381:     */   
/*  382:     */   protected abstract double postprocessError(double paramDouble);
/*  383:     */   
/*  384:     */   protected abstract void postprocessGradient(double[] paramArrayOfDouble);
/*  385:     */   
/*  386:     */   protected double calculateSE()
/*  387:     */   {
/*  388: 536 */     int chunksize = this.m_data.numInstances() / this.m_numThreads;
/*  389: 537 */     Set<Future<Double>> results = new HashSet();
/*  390: 540 */     for (int j = 0; j < this.m_numThreads; j++)
/*  391:     */     {
/*  392: 543 */       final int lo = j * chunksize;
/*  393: 544 */       final int hi = j < this.m_numThreads - 1 ? lo + chunksize : this.m_data.numInstances();
/*  394:     */       
/*  395:     */ 
/*  396:     */ 
/*  397: 548 */       Future<Double> futureSE = this.m_Pool.submit(new Callable()
/*  398:     */       {
/*  399:     */         public Double call()
/*  400:     */         {
/*  401: 551 */           double[] outputs = new double[RBFModel.this.m_numUnits];
/*  402: 552 */           double SE = 0.0D;
/*  403: 553 */           for (int k = lo; k < hi; k++)
/*  404:     */           {
/*  405: 554 */             Instance inst = RBFModel.this.m_data.instance(k);
/*  406:     */             
/*  407:     */ 
/*  408: 557 */             RBFModel.this.calculateOutputs(inst, outputs, null);
/*  409: 558 */             SE += RBFModel.this.calculateError(outputs, inst);
/*  410:     */           }
/*  411: 560 */           return Double.valueOf(SE);
/*  412:     */         }
/*  413: 562 */       });
/*  414: 563 */       results.add(futureSE);
/*  415:     */     }
/*  416: 567 */     double SE = 0.0D;
/*  417:     */     try
/*  418:     */     {
/*  419: 569 */       for (Future<Double> futureSE : results) {
/*  420: 570 */         SE += ((Double)futureSE.get()).doubleValue();
/*  421:     */       }
/*  422:     */     }
/*  423:     */     catch (Exception e)
/*  424:     */     {
/*  425: 573 */       System.out.println("Squared error could not be calculated.");
/*  426:     */     }
/*  427: 576 */     return postprocessError(0.5D * SE);
/*  428:     */   }
/*  429:     */   
/*  430:     */   protected double[] calculateGradient()
/*  431:     */   {
/*  432: 585 */     int chunksize = this.m_data.numInstances() / this.m_numThreads;
/*  433: 586 */     Set<Future<double[]>> results = new HashSet();
/*  434: 589 */     for (int j = 0; j < this.m_numThreads; j++)
/*  435:     */     {
/*  436: 592 */       final int lo = j * chunksize;
/*  437: 593 */       final int hi = j < this.m_numThreads - 1 ? lo + chunksize : this.m_data.numInstances();
/*  438:     */       
/*  439:     */ 
/*  440:     */ 
/*  441: 597 */       Future<double[]> futureGrad = this.m_Pool.submit(new Callable()
/*  442:     */       {
/*  443:     */         public double[] call()
/*  444:     */         {
/*  445: 601 */           double[] outputs = new double[RBFModel.this.m_numUnits];
/*  446: 602 */           double[] deltaHidden = new double[RBFModel.this.m_numUnits];
/*  447: 603 */           double[] derivativesOutput = new double[1];
/*  448: 604 */           double[] derivativesHidden = new double[RBFModel.this.m_numUnits];
/*  449: 605 */           double[] localGrad = new double[RBFModel.this.m_RBFParameters.length];
/*  450: 606 */           for (int k = lo; k < hi; k++)
/*  451:     */           {
/*  452: 607 */             Instance inst = RBFModel.this.m_data.instance(k);
/*  453: 608 */             RBFModel.this.calculateOutputs(inst, outputs, derivativesHidden);
/*  454: 609 */             RBFModel.this.updateGradient(localGrad, inst, outputs, derivativesOutput, deltaHidden);
/*  455:     */             
/*  456: 611 */             RBFModel.this.updateGradientForHiddenUnits(localGrad, inst, derivativesHidden, deltaHidden);
/*  457:     */           }
/*  458: 614 */           return localGrad;
/*  459:     */         }
/*  460: 616 */       });
/*  461: 617 */       results.add(futureGrad);
/*  462:     */     }
/*  463: 621 */     double[] grad = new double[this.m_RBFParameters.length];
/*  464:     */     try
/*  465:     */     {
/*  466: 623 */       for (Future<double[]> futureGrad : results)
/*  467:     */       {
/*  468: 624 */         double[] lg = (double[])futureGrad.get();
/*  469: 625 */         for (int i = 0; i < lg.length; i++) {
/*  470: 626 */           grad[i] += lg[i];
/*  471:     */         }
/*  472:     */       }
/*  473:     */     }
/*  474:     */     catch (Exception e)
/*  475:     */     {
/*  476: 630 */       System.out.println("Gradient could not be calculated.");
/*  477:     */     }
/*  478: 634 */     postprocessGradient(grad);
/*  479:     */     
/*  480: 636 */     return grad;
/*  481:     */   }
/*  482:     */   
/*  483:     */   protected abstract void updateGradient(double[] paramArrayOfDouble1, Instance paramInstance, double[] paramArrayOfDouble2, double[] paramArrayOfDouble3, double[] paramArrayOfDouble4);
/*  484:     */   
/*  485:     */   protected void updateGradientForHiddenUnits(double[] grad, Instance inst, double[] derivativesHidden, double[] deltaHidden)
/*  486:     */   {
/*  487: 652 */     for (int i = 0; i < this.m_numUnits; i++) {
/*  488: 653 */       deltaHidden[i] *= derivativesHidden[i];
/*  489:     */     }
/*  490: 657 */     for (int i = 0; i < this.m_numUnits; i++) {
/*  491: 660 */       if ((deltaHidden[i] > this.m_tolerance) || (deltaHidden[i] < -this.m_tolerance)) {
/*  492: 665 */         switch (this.m_scaleOptimizationOption)
/*  493:     */         {
/*  494:     */         case 1: 
/*  495: 667 */           grad[this.OFFSET_SCALES] += derivativeOneScale(grad, deltaHidden, this.m_RBFParameters[this.OFFSET_SCALES], inst, i);
/*  496:     */           
/*  497: 669 */           break;
/*  498:     */         case 3: 
/*  499: 673 */           derivativeScalePerAttribute(grad, deltaHidden, inst, i);
/*  500: 674 */           break;
/*  501:     */         default: 
/*  502: 677 */           grad[(this.OFFSET_SCALES + i)] += derivativeOneScale(grad, deltaHidden, this.m_RBFParameters[(this.OFFSET_SCALES + i)], inst, i);
/*  503:     */         }
/*  504:     */       }
/*  505:     */     }
/*  506:     */   }
/*  507:     */   
/*  508:     */   protected void derivativeScalePerAttribute(double[] grad, double[] deltaHidden, Instance inst, int unitIndex)
/*  509:     */   {
/*  510: 692 */     double constant = deltaHidden[unitIndex];
/*  511: 693 */     int offsetC = this.OFFSET_CENTERS + unitIndex * this.m_numAttributes;
/*  512: 694 */     int offsetS = this.OFFSET_SCALES + unitIndex * this.m_numAttributes;
/*  513: 695 */     double attWeight = 1.0D;
/*  514: 696 */     for (int j = 0; j < this.m_classIndex; j++)
/*  515:     */     {
/*  516: 697 */       double diff = inst.value(j) - this.m_RBFParameters[(offsetC + j)];
/*  517: 698 */       double scalePart = this.m_RBFParameters[(offsetS + j)] * this.m_RBFParameters[(offsetS + j)];
/*  518: 700 */       if (this.m_useAttributeWeights)
/*  519:     */       {
/*  520: 701 */         attWeight = this.m_RBFParameters[(this.OFFSET_ATTRIBUTE_WEIGHTS + j)] * this.m_RBFParameters[(this.OFFSET_ATTRIBUTE_WEIGHTS + j)];
/*  521:     */         
/*  522: 703 */         grad[(this.OFFSET_ATTRIBUTE_WEIGHTS + j)] -= this.m_RBFParameters[(this.OFFSET_ATTRIBUTE_WEIGHTS + j)] * constant * diff * diff / scalePart;
/*  523:     */       }
/*  524: 707 */       grad[(offsetS + j)] += constant * attWeight * diff * diff / (scalePart * this.m_RBFParameters[(offsetS + j)]);
/*  525:     */       
/*  526: 709 */       grad[(offsetC + j)] += constant * attWeight * diff / scalePart;
/*  527:     */     }
/*  528: 711 */     for (int j = this.m_classIndex + 1; j < this.m_numAttributes; j++)
/*  529:     */     {
/*  530: 712 */       double diff = inst.value(j) - this.m_RBFParameters[(offsetC + j)];
/*  531: 713 */       double scalePart = this.m_RBFParameters[(offsetS + j)] * this.m_RBFParameters[(offsetS + j)];
/*  532: 715 */       if (this.m_useAttributeWeights)
/*  533:     */       {
/*  534: 716 */         attWeight = this.m_RBFParameters[(this.OFFSET_ATTRIBUTE_WEIGHTS + j)] * this.m_RBFParameters[(this.OFFSET_ATTRIBUTE_WEIGHTS + j)];
/*  535:     */         
/*  536: 718 */         grad[(this.OFFSET_ATTRIBUTE_WEIGHTS + j)] -= this.m_RBFParameters[(this.OFFSET_ATTRIBUTE_WEIGHTS + j)] * constant * diff * diff / scalePart;
/*  537:     */       }
/*  538: 722 */       grad[(offsetS + j)] += constant * attWeight * diff * diff / (scalePart * this.m_RBFParameters[(offsetS + j)]);
/*  539:     */       
/*  540: 724 */       grad[(offsetC + j)] += constant * attWeight * diff / scalePart;
/*  541:     */     }
/*  542:     */   }
/*  543:     */   
/*  544:     */   protected double derivativeOneScale(double[] grad, double[] deltaHidden, double scale, Instance inst, int unitIndex)
/*  545:     */   {
/*  546: 735 */     double constant = deltaHidden[unitIndex] / (scale * scale);
/*  547: 736 */     double sumDiffSquared = 0.0D;
/*  548: 737 */     int offsetC = this.OFFSET_CENTERS + unitIndex * this.m_numAttributes;
/*  549: 738 */     double attWeight = 1.0D;
/*  550: 739 */     for (int j = 0; j < this.m_classIndex; j++)
/*  551:     */     {
/*  552: 740 */       double diff = inst.value(j) - this.m_RBFParameters[(offsetC + j)];
/*  553: 741 */       double diffSquared = diff * diff;
/*  554: 742 */       if (this.m_useAttributeWeights)
/*  555:     */       {
/*  556: 743 */         attWeight = this.m_RBFParameters[(this.OFFSET_ATTRIBUTE_WEIGHTS + j)] * this.m_RBFParameters[(this.OFFSET_ATTRIBUTE_WEIGHTS + j)];
/*  557:     */         
/*  558: 745 */         grad[(this.OFFSET_ATTRIBUTE_WEIGHTS + j)] -= this.m_RBFParameters[(this.OFFSET_ATTRIBUTE_WEIGHTS + j)] * constant * diffSquared;
/*  559:     */       }
/*  560: 749 */       sumDiffSquared += attWeight * diffSquared;
/*  561: 750 */       grad[(offsetC + j)] += constant * attWeight * diff;
/*  562:     */     }
/*  563: 752 */     for (int j = this.m_classIndex + 1; j < this.m_numAttributes; j++)
/*  564:     */     {
/*  565: 753 */       double diff = inst.value(j) - this.m_RBFParameters[(offsetC + j)];
/*  566: 754 */       double diffSquared = diff * diff;
/*  567: 755 */       if (this.m_useAttributeWeights)
/*  568:     */       {
/*  569: 756 */         attWeight = this.m_RBFParameters[(this.OFFSET_ATTRIBUTE_WEIGHTS + j)] * this.m_RBFParameters[(this.OFFSET_ATTRIBUTE_WEIGHTS + j)];
/*  570:     */         
/*  571: 758 */         grad[(this.OFFSET_ATTRIBUTE_WEIGHTS + j)] -= this.m_RBFParameters[(this.OFFSET_ATTRIBUTE_WEIGHTS + j)] * constant * diffSquared;
/*  572:     */       }
/*  573: 762 */       sumDiffSquared += attWeight * diffSquared;
/*  574: 763 */       grad[(offsetC + j)] += constant * attWeight * diff;
/*  575:     */     }
/*  576: 765 */     return constant * sumDiffSquared / scale;
/*  577:     */   }
/*  578:     */   
/*  579:     */   protected void calculateOutputs(Instance inst, double[] o, double[] d)
/*  580:     */   {
/*  581: 774 */     for (int i = 0; i < this.m_numUnits; i++)
/*  582:     */     {
/*  583: 776 */       double sumSquaredDiff = 0.0D;
/*  584: 777 */       switch (this.m_scaleOptimizationOption)
/*  585:     */       {
/*  586:     */       case 1: 
/*  587: 779 */         sumSquaredDiff = sumSquaredDiffOneScale(this.m_RBFParameters[this.OFFSET_SCALES], inst, i);
/*  588:     */         
/*  589: 781 */         break;
/*  590:     */       case 3: 
/*  591: 784 */         sumSquaredDiff = sumSquaredDiffScalePerAttribute(inst, i);
/*  592: 785 */         break;
/*  593:     */       default: 
/*  594: 788 */         sumSquaredDiff = sumSquaredDiffOneScale(this.m_RBFParameters[(this.OFFSET_SCALES + i)], inst, i);
/*  595:     */       }
/*  596: 791 */       if (!this.m_useNormalizedBasisFunctions)
/*  597:     */       {
/*  598: 792 */         o[i] = Math.exp(-sumSquaredDiff);
/*  599: 793 */         if (d != null) {
/*  600: 794 */           d[i] = o[i];
/*  601:     */         }
/*  602:     */       }
/*  603:     */       else
/*  604:     */       {
/*  605: 797 */         o[i] = (-sumSquaredDiff);
/*  606:     */       }
/*  607:     */     }
/*  608: 801 */     if (this.m_useNormalizedBasisFunctions)
/*  609:     */     {
/*  610: 802 */       double max = o[Utils.maxIndex(o)];
/*  611: 803 */       double sum = 0.0D;
/*  612: 804 */       for (int i = 0; i < o.length; i++)
/*  613:     */       {
/*  614: 805 */         o[i] = Math.exp(o[i] - max);
/*  615: 806 */         sum += o[i];
/*  616:     */       }
/*  617: 808 */       for (int i = 0; i < o.length; i++) {
/*  618: 809 */         o[i] /= sum;
/*  619:     */       }
/*  620: 811 */       if (d != null) {
/*  621: 812 */         for (int i = 0; i < o.length; i++) {
/*  622: 813 */           o[i] *= (1.0D - o[i]);
/*  623:     */         }
/*  624:     */       }
/*  625:     */     }
/*  626:     */   }
/*  627:     */   
/*  628:     */   protected double sumSquaredDiffScalePerAttribute(Instance inst, int unitIndex)
/*  629:     */   {
/*  630: 825 */     int offsetS = this.OFFSET_SCALES + unitIndex * this.m_numAttributes;
/*  631: 826 */     int offsetC = this.OFFSET_CENTERS + unitIndex * this.m_numAttributes;
/*  632: 827 */     double sumSquaredDiff = 0.0D;
/*  633: 828 */     for (int j = 0; j < this.m_classIndex; j++)
/*  634:     */     {
/*  635: 829 */       double diff = this.m_RBFParameters[(offsetC + j)] - inst.value(j);
/*  636: 830 */       if (this.m_useAttributeWeights) {
/*  637: 831 */         diff *= this.m_RBFParameters[(this.OFFSET_ATTRIBUTE_WEIGHTS + j)];
/*  638:     */       }
/*  639: 833 */       sumSquaredDiff += diff * diff / (2.0D * this.m_RBFParameters[(offsetS + j)] * this.m_RBFParameters[(offsetS + j)]);
/*  640:     */     }
/*  641: 836 */     for (int j = this.m_classIndex + 1; j < this.m_numAttributes; j++)
/*  642:     */     {
/*  643: 837 */       double diff = this.m_RBFParameters[(offsetC + j)] - inst.value(j);
/*  644: 838 */       if (this.m_useAttributeWeights) {
/*  645: 839 */         diff *= this.m_RBFParameters[(this.OFFSET_ATTRIBUTE_WEIGHTS + j)];
/*  646:     */       }
/*  647: 841 */       sumSquaredDiff += diff * diff / (2.0D * this.m_RBFParameters[(offsetS + j)] * this.m_RBFParameters[(offsetS + j)]);
/*  648:     */     }
/*  649: 844 */     return sumSquaredDiff;
/*  650:     */   }
/*  651:     */   
/*  652:     */   protected double sumSquaredDiffOneScale(double scale, Instance inst, int unitIndex)
/*  653:     */   {
/*  654: 853 */     int offsetC = this.OFFSET_CENTERS + unitIndex * this.m_numAttributes;
/*  655: 854 */     double sumSquaredDiff = 0.0D;
/*  656: 855 */     for (int j = 0; j < this.m_classIndex; j++)
/*  657:     */     {
/*  658: 856 */       double diff = this.m_RBFParameters[(offsetC + j)] - inst.value(j);
/*  659: 857 */       if (this.m_useAttributeWeights) {
/*  660: 858 */         diff *= this.m_RBFParameters[(this.OFFSET_ATTRIBUTE_WEIGHTS + j)];
/*  661:     */       }
/*  662: 860 */       sumSquaredDiff += diff * diff;
/*  663:     */     }
/*  664: 862 */     for (int j = this.m_classIndex + 1; j < this.m_numAttributes; j++)
/*  665:     */     {
/*  666: 863 */       double diff = this.m_RBFParameters[(offsetC + j)] - inst.value(j);
/*  667: 864 */       if (this.m_useAttributeWeights) {
/*  668: 865 */         diff *= this.m_RBFParameters[(this.OFFSET_ATTRIBUTE_WEIGHTS + j)];
/*  669:     */       }
/*  670: 867 */       sumSquaredDiff += diff * diff;
/*  671:     */     }
/*  672: 869 */     return sumSquaredDiff / (2.0D * scale * scale);
/*  673:     */   }
/*  674:     */   
/*  675:     */   protected abstract double[] getDistribution(double[] paramArrayOfDouble);
/*  676:     */   
/*  677:     */   public double[] distributionForInstance(Instance inst)
/*  678:     */     throws Exception
/*  679:     */   {
/*  680: 884 */     this.m_ReplaceMissingValues.input(inst);
/*  681: 885 */     inst = this.m_ReplaceMissingValues.output();
/*  682: 886 */     this.m_AttFilter.input(inst);
/*  683: 887 */     inst = this.m_AttFilter.output();
/*  684: 890 */     if (this.m_ZeroR != null) {
/*  685: 891 */       return this.m_ZeroR.distributionForInstance(inst);
/*  686:     */     }
/*  687: 894 */     this.m_NominalToBinary.input(inst);
/*  688: 895 */     inst = this.m_NominalToBinary.output();
/*  689: 896 */     this.m_Filter.input(inst);
/*  690: 897 */     inst = this.m_Filter.output();
/*  691:     */     
/*  692: 899 */     double[] outputs = new double[this.m_numUnits];
/*  693: 900 */     calculateOutputs(inst, outputs, null);
/*  694: 901 */     return getDistribution(outputs);
/*  695:     */   }
/*  696:     */   
/*  697:     */   public String globalInfo()
/*  698:     */   {
/*  699: 911 */     return "Class implementing radial basis function networks, trained in a fully supervised manner using WEKA's Optimization class by minimizing squared error with the BFGS method. Note that all attributes are normalized into the [0,1] scale.\n\nThe initial centers for the Gaussian radial basis functions are found using WEKA's SimpleKMeans. The initial sigma values are set to the maximum distance between any center and its nearest neighbour in the set of centers.\n\nThere are several parameters. The ridge parameter is used to penalize the size of the weights in the output layer. The number of basis functions can also be specified. Note that large numbers produce long training times. Another option determines whether one global sigma value is used for all units (fastest), whether one value is used per unit (common practice, it seems, and set as the default), or a different value is learned for every unit/attribute combination. It is also possible to learn attribute weights for the distance function. (The square of the value shown in the output is used.)  Finally, it is possible to use conjugate gradient descent rather than BFGS updates, which can be faster for cases with many parameters, and to use normalized basis functions instead of unnormalized ones.\n\nTo improve speed, an approximate version of the logistic function is used as the activation function in the output layer. Also, if delta values in the backpropagation step are within the user-specified tolerance, the gradient is not updated for that particular instance, which saves some additional time.\n\nParalled calculation of squared error and gradient is possible when multiple CPU cores are present. Data is split into batches and processed in separate threads in this case. Note that this only improves runtime for larger datasets.\n\nNominal attributes are processed using the unsupervised  NominalToBinary filter and missing values are replaced globally using ReplaceMissingValues.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*  700:     */   }
/*  701:     */   
/*  702:     */   public String toleranceTipText()
/*  703:     */   {
/*  704: 948 */     return "The tolerance parameter for the delta values.";
/*  705:     */   }
/*  706:     */   
/*  707:     */   public double getTolerance()
/*  708:     */   {
/*  709: 956 */     return this.m_tolerance;
/*  710:     */   }
/*  711:     */   
/*  712:     */   public void setTolerance(double newTolerance)
/*  713:     */   {
/*  714: 964 */     this.m_tolerance = newTolerance;
/*  715:     */   }
/*  716:     */   
/*  717:     */   public String numFunctionsTipText()
/*  718:     */   {
/*  719: 972 */     return "The number of basis functions to use.";
/*  720:     */   }
/*  721:     */   
/*  722:     */   public int getNumFunctions()
/*  723:     */   {
/*  724: 980 */     return this.m_numUnits;
/*  725:     */   }
/*  726:     */   
/*  727:     */   public void setNumFunctions(int newNumFunctions)
/*  728:     */   {
/*  729: 988 */     this.m_numUnits = newNumFunctions;
/*  730:     */   }
/*  731:     */   
/*  732:     */   public String ridgeTipText()
/*  733:     */   {
/*  734: 996 */     return "The ridge penalty factor for the output layer.";
/*  735:     */   }
/*  736:     */   
/*  737:     */   public double getRidge()
/*  738:     */   {
/*  739:1004 */     return this.m_ridge;
/*  740:     */   }
/*  741:     */   
/*  742:     */   public void setRidge(double newRidge)
/*  743:     */   {
/*  744:1012 */     this.m_ridge = newRidge;
/*  745:     */   }
/*  746:     */   
/*  747:     */   public String useCGDTipText()
/*  748:     */   {
/*  749:1020 */     return "Whether to use conjugate gradient descent (recommended for many parameters).";
/*  750:     */   }
/*  751:     */   
/*  752:     */   public boolean getUseCGD()
/*  753:     */   {
/*  754:1028 */     return this.m_useCGD;
/*  755:     */   }
/*  756:     */   
/*  757:     */   public void setUseCGD(boolean newUseCGD)
/*  758:     */   {
/*  759:1036 */     this.m_useCGD = newUseCGD;
/*  760:     */   }
/*  761:     */   
/*  762:     */   public String useAttributeWeightsTipText()
/*  763:     */   {
/*  764:1044 */     return "Whether to use attribute weights.";
/*  765:     */   }
/*  766:     */   
/*  767:     */   public boolean getUseAttributeWeights()
/*  768:     */   {
/*  769:1052 */     return this.m_useAttributeWeights;
/*  770:     */   }
/*  771:     */   
/*  772:     */   public void setUseAttributeWeights(boolean newUseAttributeWeights)
/*  773:     */   {
/*  774:1060 */     this.m_useAttributeWeights = newUseAttributeWeights;
/*  775:     */   }
/*  776:     */   
/*  777:     */   public String useNormalizedBasisFunctionsTipText()
/*  778:     */   {
/*  779:1068 */     return "Whether to use normalized basis functions.";
/*  780:     */   }
/*  781:     */   
/*  782:     */   public boolean getUseNormalizedBasisFunctions()
/*  783:     */   {
/*  784:1076 */     return this.m_useNormalizedBasisFunctions;
/*  785:     */   }
/*  786:     */   
/*  787:     */   public void setUseNormalizedBasisFunctions(boolean newUseNormalizedBasisFunctions)
/*  788:     */   {
/*  789:1085 */     this.m_useNormalizedBasisFunctions = newUseNormalizedBasisFunctions;
/*  790:     */   }
/*  791:     */   
/*  792:     */   public String scaleOptimizationOptionTipText()
/*  793:     */   {
/*  794:1093 */     return "The number of sigma parameters to use.";
/*  795:     */   }
/*  796:     */   
/*  797:     */   public SelectedTag getScaleOptimizationOption()
/*  798:     */   {
/*  799:1101 */     return new SelectedTag(this.m_scaleOptimizationOption, TAGS_SCALE);
/*  800:     */   }
/*  801:     */   
/*  802:     */   public void setScaleOptimizationOption(SelectedTag newMethod)
/*  803:     */   {
/*  804:1109 */     if (newMethod.getTags() == TAGS_SCALE) {
/*  805:1110 */       this.m_scaleOptimizationOption = newMethod.getSelectedTag().getID();
/*  806:     */     }
/*  807:     */   }
/*  808:     */   
/*  809:     */   public String numThreadsTipText()
/*  810:     */   {
/*  811:1119 */     return "The number of threads to use, which should be >= size of thread pool.";
/*  812:     */   }
/*  813:     */   
/*  814:     */   public int getNumThreads()
/*  815:     */   {
/*  816:1127 */     return this.m_numThreads;
/*  817:     */   }
/*  818:     */   
/*  819:     */   public void setNumThreads(int nT)
/*  820:     */   {
/*  821:1135 */     this.m_numThreads = nT;
/*  822:     */   }
/*  823:     */   
/*  824:     */   public String poolSizeTipText()
/*  825:     */   {
/*  826:1143 */     return "The size of the thread pool, for example, the number of cores in the CPU.";
/*  827:     */   }
/*  828:     */   
/*  829:     */   public int getPoolSize()
/*  830:     */   {
/*  831:1151 */     return this.m_poolSize;
/*  832:     */   }
/*  833:     */   
/*  834:     */   public void setPoolSize(int nT)
/*  835:     */   {
/*  836:1159 */     this.m_poolSize = nT;
/*  837:     */   }
/*  838:     */   
/*  839:     */   public Enumeration<Option> listOptions()
/*  840:     */   {
/*  841:1170 */     Vector<Option> newVector = new Vector(9);
/*  842:     */     
/*  843:1172 */     newVector.addElement(new Option("\tNumber of Gaussian basis functions (default is 2).\n", "N", 1, "-N <int>"));
/*  844:     */     
/*  845:     */ 
/*  846:     */ 
/*  847:1176 */     newVector.addElement(new Option("\tRidge factor for quadratic penalty on output weights (default is 0.01).\n", "R", 1, "-R <double>"));
/*  848:     */     
/*  849:     */ 
/*  850:     */ 
/*  851:1180 */     newVector.addElement(new Option("\tTolerance parameter for delta values (default is 1.0e-6).\n", "L", 1, "-L <double>"));
/*  852:     */     
/*  853:     */ 
/*  854:1183 */     newVector.addElement(new Option("\tThe scale optimization option: global scale (1), one scale per unit (2), scale per unit and attribute (3) (default is 2).\n", "C", 1, "-C <1|2|3>"));
/*  855:     */     
/*  856:     */ 
/*  857:     */ 
/*  858:     */ 
/*  859:1188 */     newVector.addElement(new Option("\tUse conjugate gradient descent (recommended for many attributes).\n", "G", 0, "-G"));
/*  860:     */     
/*  861:     */ 
/*  862:     */ 
/*  863:1192 */     newVector.addElement(new Option("\tUse normalized basis functions.\n", "O", 0, "-O"));
/*  864:     */     
/*  865:1194 */     newVector.addElement(new Option("\tUse attribute weights.\n", "A", 0, "-A"));
/*  866:     */     
/*  867:1196 */     newVector.addElement(new Option("\t" + poolSizeTipText() + " (default 1)\n", "P", 1, "-P <int>"));
/*  868:     */     
/*  869:1198 */     newVector.addElement(new Option("\t" + numThreadsTipText() + " (default 1)\n", "E", 1, "-E <int>"));
/*  870:     */     
/*  871:     */ 
/*  872:1201 */     newVector.addAll(Collections.list(super.listOptions()));
/*  873:     */     
/*  874:1203 */     return newVector.elements();
/*  875:     */   }
/*  876:     */   
/*  877:     */   public void setOptions(String[] options)
/*  878:     */     throws Exception
/*  879:     */   {
/*  880:1260 */     String numFunctions = Utils.getOption('N', options);
/*  881:1261 */     if (numFunctions.length() != 0) {
/*  882:1262 */       setNumFunctions(Integer.parseInt(numFunctions));
/*  883:     */     } else {
/*  884:1264 */       setNumFunctions(2);
/*  885:     */     }
/*  886:1266 */     String Ridge = Utils.getOption('R', options);
/*  887:1267 */     if (Ridge.length() != 0) {
/*  888:1268 */       setRidge(Double.parseDouble(Ridge));
/*  889:     */     } else {
/*  890:1270 */       setRidge(0.01D);
/*  891:     */     }
/*  892:1272 */     String scale = Utils.getOption('C', options);
/*  893:1273 */     if (scale.length() != 0) {
/*  894:1274 */       setScaleOptimizationOption(new SelectedTag(Integer.parseInt(scale), TAGS_SCALE));
/*  895:     */     } else {
/*  896:1277 */       setScaleOptimizationOption(new SelectedTag(2, TAGS_SCALE));
/*  897:     */     }
/*  898:1279 */     String Tolerance = Utils.getOption('L', options);
/*  899:1280 */     if (Tolerance.length() != 0) {
/*  900:1281 */       setTolerance(Double.parseDouble(Tolerance));
/*  901:     */     } else {
/*  902:1283 */       setTolerance(1.0E-006D);
/*  903:     */     }
/*  904:1285 */     this.m_useCGD = Utils.getFlag('G', options);
/*  905:1286 */     this.m_useNormalizedBasisFunctions = Utils.getFlag('O', options);
/*  906:1287 */     this.m_useAttributeWeights = Utils.getFlag('A', options);
/*  907:1288 */     String PoolSize = Utils.getOption('P', options);
/*  908:1289 */     if (PoolSize.length() != 0) {
/*  909:1290 */       setPoolSize(Integer.parseInt(PoolSize));
/*  910:     */     } else {
/*  911:1292 */       setPoolSize(1);
/*  912:     */     }
/*  913:1294 */     String NumThreads = Utils.getOption('E', options);
/*  914:1295 */     if (NumThreads.length() != 0) {
/*  915:1296 */       setNumThreads(Integer.parseInt(NumThreads));
/*  916:     */     } else {
/*  917:1298 */       setNumThreads(1);
/*  918:     */     }
/*  919:1301 */     super.setOptions(options);
/*  920:     */     
/*  921:1303 */     Utils.checkForRemainingOptions(options);
/*  922:     */   }
/*  923:     */   
/*  924:     */   public String[] getOptions()
/*  925:     */   {
/*  926:1314 */     Vector<String> options = new Vector();
/*  927:     */     
/*  928:1316 */     options.add("-N");
/*  929:1317 */     options.add("" + getNumFunctions());
/*  930:     */     
/*  931:1319 */     options.add("-R");
/*  932:1320 */     options.add("" + getRidge());
/*  933:     */     
/*  934:1322 */     options.add("-L");
/*  935:1323 */     options.add("" + getTolerance());
/*  936:     */     
/*  937:1325 */     options.add("-C");
/*  938:1326 */     options.add("" + getScaleOptimizationOption().getSelectedTag().getID());
/*  939:1328 */     if (this.m_useCGD) {
/*  940:1329 */       options.add("-G");
/*  941:     */     }
/*  942:1332 */     if (this.m_useNormalizedBasisFunctions) {
/*  943:1333 */       options.add("-O");
/*  944:     */     }
/*  945:1336 */     if (this.m_useAttributeWeights) {
/*  946:1337 */       options.add("-A");
/*  947:     */     }
/*  948:1340 */     options.add("-P");
/*  949:1341 */     options.add("" + getPoolSize());
/*  950:     */     
/*  951:1343 */     options.add("-E");
/*  952:1344 */     options.add("" + getNumThreads());
/*  953:     */     
/*  954:1346 */     Collections.addAll(options, super.getOptions());
/*  955:     */     
/*  956:1348 */     return (String[])options.toArray(new String[0]);
/*  957:     */   }
/*  958:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.RBFModel
 * JD-Core Version:    0.7.0.1
 */