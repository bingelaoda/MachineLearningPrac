/*    1:     */ package weka.filters.unsupervised.attribute;
/*    2:     */ 
/*    3:     */ import java.io.File;
/*    4:     */ import java.io.PrintStream;
/*    5:     */ import java.io.PrintWriter;
/*    6:     */ import java.util.ArrayList;
/*    7:     */ import java.util.Arrays;
/*    8:     */ import java.util.Collections;
/*    9:     */ import java.util.Enumeration;
/*   10:     */ import java.util.HashSet;
/*   11:     */ import java.util.Random;
/*   12:     */ import java.util.Set;
/*   13:     */ import java.util.Vector;
/*   14:     */ import java.util.concurrent.Callable;
/*   15:     */ import java.util.concurrent.ExecutorService;
/*   16:     */ import java.util.concurrent.Executors;
/*   17:     */ import java.util.concurrent.Future;
/*   18:     */ import weka.core.Attribute;
/*   19:     */ import weka.core.Capabilities;
/*   20:     */ import weka.core.Capabilities.Capability;
/*   21:     */ import weka.core.ConjugateGradientOptimization;
/*   22:     */ import weka.core.DenseInstance;
/*   23:     */ import weka.core.Instance;
/*   24:     */ import weka.core.Instances;
/*   25:     */ import weka.core.Optimization;
/*   26:     */ import weka.core.Option;
/*   27:     */ import weka.core.RevisionUtils;
/*   28:     */ import weka.core.SelectedTag;
/*   29:     */ import weka.core.SparseInstance;
/*   30:     */ import weka.core.Tag;
/*   31:     */ import weka.core.TechnicalInformation;
/*   32:     */ import weka.core.TechnicalInformation.Field;
/*   33:     */ import weka.core.TechnicalInformation.Type;
/*   34:     */ import weka.core.TechnicalInformationHandler;
/*   35:     */ import weka.core.Utils;
/*   36:     */ import weka.filters.Filter;
/*   37:     */ import weka.filters.SimpleBatchFilter;
/*   38:     */ import weka.filters.UnsupervisedFilter;
/*   39:     */ 
/*   40:     */ public class MLPAutoencoder
/*   41:     */   extends SimpleBatchFilter
/*   42:     */   implements UnsupervisedFilter, TechnicalInformationHandler
/*   43:     */ {
/*   44:     */   private static final long serialVersionUID = -277474276438394612L;
/*   45:     */   
/*   46:     */   public Capabilities getCapabilities()
/*   47:     */   {
/*   48: 163 */     Capabilities result = super.getCapabilities();
/*   49: 164 */     result.disableAll();
/*   50:     */     
/*   51:     */ 
/*   52: 167 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*   53: 168 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*   54: 169 */     result.enable(Capabilities.Capability.NO_CLASS);
/*   55: 170 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*   56: 171 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*   57: 172 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*   58:     */     
/*   59: 174 */     return result;
/*   60:     */   }
/*   61:     */   
/*   62:     */   protected class OptEng
/*   63:     */     extends Optimization
/*   64:     */   {
/*   65:     */     protected OptEng() {}
/*   66:     */     
/*   67:     */     protected double objectiveFunction(double[] x)
/*   68:     */     {
/*   69: 189 */       MLPAutoencoder.this.m_MLPParameters = x;
/*   70: 190 */       return MLPAutoencoder.this.calculateSE();
/*   71:     */     }
/*   72:     */     
/*   73:     */     protected double[] evaluateGradient(double[] x)
/*   74:     */     {
/*   75: 199 */       MLPAutoencoder.this.m_MLPParameters = x;
/*   76: 200 */       return MLPAutoencoder.this.calculateGradient();
/*   77:     */     }
/*   78:     */     
/*   79:     */     public String getRevision()
/*   80:     */     {
/*   81: 208 */       return RevisionUtils.extract("$Revision: 9346 $");
/*   82:     */     }
/*   83:     */   }
/*   84:     */   
/*   85:     */   protected class OptEngCGD
/*   86:     */     extends ConjugateGradientOptimization
/*   87:     */   {
/*   88:     */     protected OptEngCGD() {}
/*   89:     */     
/*   90:     */     protected double objectiveFunction(double[] x)
/*   91:     */     {
/*   92: 224 */       MLPAutoencoder.this.m_MLPParameters = x;
/*   93: 225 */       return MLPAutoencoder.this.calculateSE();
/*   94:     */     }
/*   95:     */     
/*   96:     */     protected double[] evaluateGradient(double[] x)
/*   97:     */     {
/*   98: 234 */       MLPAutoencoder.this.m_MLPParameters = x;
/*   99: 235 */       return MLPAutoencoder.this.calculateGradient();
/*  100:     */     }
/*  101:     */     
/*  102:     */     public String getRevision()
/*  103:     */     {
/*  104: 243 */       return RevisionUtils.extract("$Revision: 9346 $");
/*  105:     */     }
/*  106:     */   }
/*  107:     */   
/*  108: 248 */   protected int m_numUnits = 2;
/*  109: 251 */   protected Instances m_data = null;
/*  110: 254 */   protected int m_numAttributes = -1;
/*  111: 257 */   protected double[] m_MLPParameters = null;
/*  112: 260 */   protected int OFFSET_O_BIASES = -1;
/*  113: 263 */   protected int OFFSET_H_BIASES = -1;
/*  114: 266 */   protected double m_lambda = 0.01D;
/*  115: 269 */   protected boolean m_useCGD = false;
/*  116: 272 */   protected double m_tolerance = 1.0E-006D;
/*  117: 275 */   protected int m_numThreads = 1;
/*  118: 278 */   protected int m_poolSize = 1;
/*  119:     */   public static final int FILTER_NORMALIZE = 0;
/*  120:     */   public static final int FILTER_STANDARDIZE = 1;
/*  121:     */   public static final int FILTER_NONE = 2;
/*  122: 290 */   public static final Tag[] TAGS_FILTER = { new Tag(0, "Normalize training data"), new Tag(1, "Standardize training data"), new Tag(2, "No normalization/standardization") };
/*  123: 296 */   protected int m_filterType = 1;
/*  124: 299 */   protected Filter m_Filter = null;
/*  125: 302 */   protected Remove m_Remove = null;
/*  126: 305 */   protected transient ExecutorService m_Pool = null;
/*  127: 308 */   protected boolean m_useContractive = false;
/*  128: 311 */   protected boolean m_useExactSigmoid = false;
/*  129: 314 */   protected boolean m_outputInOriginalSpace = false;
/*  130: 317 */   protected File m_WeightsFile = new File(System.getProperty("user.dir"));
/*  131: 320 */   protected double[] m_offsets = null;
/*  132: 321 */   protected double[] m_factors = null;
/*  133:     */   
/*  134:     */   protected double[][] minAndMax(Instances data)
/*  135:     */   {
/*  136: 328 */     double[][] result = new double[2][this.m_numAttributes];
/*  137: 329 */     for (int j = 0; j < this.m_numAttributes; j++)
/*  138:     */     {
/*  139: 330 */       result[0][j] = 1.7976931348623157E+308D;
/*  140: 331 */       result[1][j] = -1.797693134862316E+308D;
/*  141:     */     }
/*  142: 333 */     for (int i = 0; i < data.numInstances(); i++) {
/*  143: 334 */       for (int j = 0; j < this.m_numAttributes; j++)
/*  144:     */       {
/*  145: 335 */         double value = data.instance(i).value(j);
/*  146: 336 */         if (value < result[0][j]) {
/*  147: 337 */           result[0][j] = value;
/*  148:     */         }
/*  149: 339 */         if (value > result[1][j]) {
/*  150: 340 */           result[1][j] = value;
/*  151:     */         }
/*  152:     */       }
/*  153:     */     }
/*  154: 344 */     return result;
/*  155:     */   }
/*  156:     */   
/*  157:     */   protected Instances initParameters(Instances data)
/*  158:     */     throws Exception
/*  159:     */   {
/*  160: 354 */     getCapabilities().testWithFail(data);
/*  161:     */     
/*  162: 356 */     data = new Instances(data);
/*  163:     */     
/*  164:     */ 
/*  165: 359 */     Random random = new Random(1L);
/*  166: 360 */     if (data.numInstances() > 1) {
/*  167: 361 */       random = data.getRandomNumberGenerator(1L);
/*  168:     */     }
/*  169: 363 */     data.randomize(random);
/*  170:     */     
/*  171:     */ 
/*  172: 366 */     this.m_Remove = null;
/*  173: 367 */     if (data.classIndex() >= 0)
/*  174:     */     {
/*  175: 368 */       this.m_Remove = new Remove();
/*  176: 369 */       this.m_Remove.setAttributeIndices("" + (data.classIndex() + 1));
/*  177: 370 */       this.m_Remove.setInputFormat(data);
/*  178: 371 */       data = Filter.useFilter(data, this.m_Remove);
/*  179:     */     }
/*  180: 374 */     this.m_numAttributes = data.numAttributes();
/*  181: 376 */     if ((this.m_filterType == 1) || (this.m_filterType == 0))
/*  182:     */     {
/*  183: 377 */       double[][] beforeTransformation = minAndMax(data);
/*  184: 378 */       if (this.m_filterType == 1) {
/*  185: 379 */         this.m_Filter = new Standardize();
/*  186:     */       } else {
/*  187: 381 */         this.m_Filter = new Normalize();
/*  188:     */       }
/*  189: 383 */       this.m_Filter.setInputFormat(data);
/*  190: 384 */       data = Filter.useFilter(data, this.m_Filter);
/*  191: 385 */       double[][] afterTransformation = minAndMax(data);
/*  192: 386 */       this.m_offsets = new double[this.m_numAttributes];
/*  193: 387 */       this.m_factors = new double[this.m_numAttributes];
/*  194: 388 */       for (int j = 0; j < this.m_numAttributes; j++)
/*  195:     */       {
/*  196: 389 */         if (beforeTransformation[1][j] > beforeTransformation[0][j]) {
/*  197: 390 */           this.m_factors[j] = ((beforeTransformation[1][j] - beforeTransformation[0][j]) / (afterTransformation[1][j] - afterTransformation[0][j]));
/*  198:     */         } else {
/*  199: 393 */           this.m_factors[j] = 1.0D;
/*  200:     */         }
/*  201: 395 */         this.m_offsets[j] = (beforeTransformation[0][j] - afterTransformation[0][j] * this.m_factors[j]);
/*  202:     */       }
/*  203:     */     }
/*  204:     */     else
/*  205:     */     {
/*  206: 399 */       this.m_Filter = null;
/*  207: 400 */       this.m_offsets = null;
/*  208: 401 */       this.m_factors = null;
/*  209:     */     }
/*  210: 405 */     this.OFFSET_O_BIASES = (this.m_numAttributes * this.m_numUnits);
/*  211: 406 */     this.OFFSET_H_BIASES = (this.OFFSET_O_BIASES + this.m_numAttributes);
/*  212: 407 */     this.m_MLPParameters = new double[this.OFFSET_H_BIASES + this.m_numUnits];
/*  213: 410 */     for (int i = 0; i < this.m_numUnits; i++)
/*  214:     */     {
/*  215: 411 */       int offset = i * this.m_numAttributes;
/*  216: 412 */       for (int j = 0; j < this.m_numAttributes; j++) {
/*  217: 413 */         this.m_MLPParameters[(offset + j)] = (0.1D * random.nextGaussian());
/*  218:     */       }
/*  219:     */     }
/*  220: 418 */     for (int i = 0; i < this.m_numAttributes; i++) {
/*  221: 419 */       this.m_MLPParameters[(this.OFFSET_O_BIASES + i)] = (0.1D * random.nextGaussian());
/*  222:     */     }
/*  223: 421 */     for (int i = 0; i < this.m_numUnits; i++) {
/*  224: 422 */       this.m_MLPParameters[(this.OFFSET_H_BIASES + i)] = (0.1D * random.nextGaussian());
/*  225:     */     }
/*  226: 425 */     return data;
/*  227:     */   }
/*  228:     */   
/*  229:     */   public void initFilter(Instances data)
/*  230:     */     throws Exception
/*  231:     */   {
/*  232: 434 */     this.m_data = initParameters(data);
/*  233: 435 */     if (this.m_data == null) {
/*  234: 436 */       return;
/*  235:     */     }
/*  236: 440 */     this.m_Pool = Executors.newFixedThreadPool(this.m_poolSize);
/*  237:     */     
/*  238:     */ 
/*  239: 443 */     Optimization opt = null;
/*  240: 444 */     if (!this.m_useCGD) {
/*  241: 445 */       opt = new OptEng();
/*  242:     */     } else {
/*  243: 447 */       opt = new OptEngCGD();
/*  244:     */     }
/*  245: 449 */     opt.setDebug(this.m_Debug);
/*  246:     */     
/*  247:     */ 
/*  248: 452 */     double[][] b = new double[2][this.m_MLPParameters.length];
/*  249: 453 */     for (int i = 0; i < 2; i++) {
/*  250: 454 */       for (int j = 0; j < this.m_MLPParameters.length; j++) {
/*  251: 455 */         b[i][j] = (0.0D / 0.0D);
/*  252:     */       }
/*  253:     */     }
/*  254: 459 */     this.m_MLPParameters = opt.findArgmin(this.m_MLPParameters, b);
/*  255: 460 */     while (this.m_MLPParameters == null)
/*  256:     */     {
/*  257: 461 */       this.m_MLPParameters = opt.getVarbValues();
/*  258: 462 */       if (this.m_Debug) {
/*  259: 463 */         System.out.println("First set of iterations finished, not enough!");
/*  260:     */       }
/*  261: 465 */       this.m_MLPParameters = opt.findArgmin(this.m_MLPParameters, b);
/*  262:     */     }
/*  263: 467 */     if (this.m_Debug) {
/*  264: 468 */       System.out.println("SE (normalized space) after optimization: " + opt.getMinFunction());
/*  265:     */     }
/*  266: 472 */     this.m_data = new Instances(this.m_data, 0);
/*  267:     */     
/*  268:     */ 
/*  269: 475 */     this.m_Pool.shutdown();
/*  270:     */   }
/*  271:     */   
/*  272:     */   protected double calculateSE()
/*  273:     */   {
/*  274: 485 */     int chunksize = this.m_data.numInstances() / this.m_numThreads;
/*  275: 486 */     Set<Future<Double>> results = new HashSet();
/*  276: 489 */     for (int j = 0; j < this.m_numThreads; j++)
/*  277:     */     {
/*  278: 492 */       final int lo = j * chunksize;
/*  279: 493 */       final int hi = j < this.m_numThreads - 1 ? lo + chunksize : this.m_data.numInstances();
/*  280:     */       
/*  281:     */ 
/*  282:     */ 
/*  283: 497 */       Future<Double> futureSE = this.m_Pool.submit(new Callable()
/*  284:     */       {
/*  285:     */         public Double call()
/*  286:     */         {
/*  287: 500 */           double[] outputsHidden = new double[MLPAutoencoder.this.m_numUnits];
/*  288: 501 */           double[] outputsOut = new double[MLPAutoencoder.this.m_numAttributes];
/*  289: 502 */           double SE = 0.0D;
/*  290: 503 */           for (int k = lo; k < hi; k++)
/*  291:     */           {
/*  292: 504 */             Instance inst = MLPAutoencoder.this.m_data.instance(k);
/*  293:     */             
/*  294:     */ 
/*  295: 507 */             MLPAutoencoder.this.calculateOutputsHidden(inst, outputsHidden, null);
/*  296: 508 */             MLPAutoencoder.this.calculateOutputsOut(outputsHidden, outputsOut);
/*  297: 511 */             if ((inst instanceof SparseInstance))
/*  298:     */             {
/*  299: 512 */               int instIndex = 0;
/*  300: 513 */               for (int index = 0; index < MLPAutoencoder.this.m_numAttributes; index++) {
/*  301: 514 */                 if ((instIndex < inst.numValues()) && (inst.index(instIndex) == index))
/*  302:     */                 {
/*  303: 516 */                   double err = outputsOut[index] - inst.valueSparse(instIndex++);
/*  304:     */                   
/*  305: 518 */                   SE += 0.5D * inst.weight() * err * err;
/*  306:     */                 }
/*  307:     */                 else
/*  308:     */                 {
/*  309: 520 */                   double err = outputsOut[index];
/*  310: 521 */                   SE += 0.5D * inst.weight() * err * err;
/*  311:     */                 }
/*  312:     */               }
/*  313:     */             }
/*  314:     */             else
/*  315:     */             {
/*  316: 525 */               for (int index = 0; index < MLPAutoencoder.this.m_numAttributes; index++)
/*  317:     */               {
/*  318: 526 */                 double err = outputsOut[index] - inst.value(index);
/*  319: 527 */                 SE += 0.5D * inst.weight() * err * err;
/*  320:     */               }
/*  321:     */             }
/*  322: 532 */             if (MLPAutoencoder.this.m_useContractive) {
/*  323: 535 */               for (int i = 0; i < outputsHidden.length; i++)
/*  324:     */               {
/*  325: 536 */                 double sum = 0.0D;
/*  326: 537 */                 int offset = i * MLPAutoencoder.this.m_numAttributes;
/*  327: 538 */                 for (int index = 0; index < MLPAutoencoder.this.m_numAttributes; index++) {
/*  328: 539 */                   sum += MLPAutoencoder.this.m_MLPParameters[(offset + index)] * MLPAutoencoder.this.m_MLPParameters[(offset + index)];
/*  329:     */                 }
/*  330: 542 */                 SE += MLPAutoencoder.this.m_lambda * inst.weight() * outputsHidden[i] * (1.0D - outputsHidden[i]) * outputsHidden[i] * (1.0D - outputsHidden[i]) * sum;
/*  331:     */               }
/*  332:     */             }
/*  333:     */           }
/*  334: 548 */           return Double.valueOf(SE);
/*  335:     */         }
/*  336: 550 */       });
/*  337: 551 */       results.add(futureSE);
/*  338:     */     }
/*  339: 555 */     double SE = 0.0D;
/*  340:     */     try
/*  341:     */     {
/*  342: 557 */       for (Future<Double> futureSE : results) {
/*  343: 558 */         SE += ((Double)futureSE.get()).doubleValue();
/*  344:     */       }
/*  345:     */     }
/*  346:     */     catch (Exception e)
/*  347:     */     {
/*  348: 561 */       System.out.println("Squared error could not be calculated.");
/*  349:     */     }
/*  350: 565 */     if (!this.m_useContractive)
/*  351:     */     {
/*  352: 568 */       double squaredSumOfWeights = 0.0D;
/*  353: 569 */       for (int i = 0; i < this.m_numUnits; i++)
/*  354:     */       {
/*  355: 570 */         int offset = i * this.m_numAttributes;
/*  356: 571 */         for (int j = 0; j < this.m_numAttributes; j++) {
/*  357: 572 */           squaredSumOfWeights += this.m_MLPParameters[(offset + j)] * this.m_MLPParameters[(offset + j)];
/*  358:     */         }
/*  359:     */       }
/*  360: 576 */       SE += this.m_lambda * squaredSumOfWeights;
/*  361:     */     }
/*  362: 579 */     return SE / this.m_data.sumOfWeights();
/*  363:     */   }
/*  364:     */   
/*  365:     */   protected double[] calculateGradient()
/*  366:     */   {
/*  367: 588 */     int chunksize = this.m_data.numInstances() / this.m_numThreads;
/*  368: 589 */     Set<Future<double[]>> results = new HashSet();
/*  369: 592 */     for (int j = 0; j < this.m_numThreads; j++)
/*  370:     */     {
/*  371: 595 */       final int lo = j * chunksize;
/*  372: 596 */       final int hi = j < this.m_numThreads - 1 ? lo + chunksize : this.m_data.numInstances();
/*  373:     */       
/*  374:     */ 
/*  375:     */ 
/*  376: 600 */       Future<double[]> futureGrad = this.m_Pool.submit(new Callable()
/*  377:     */       {
/*  378:     */         public double[] call()
/*  379:     */         {
/*  380: 604 */           double[] outputsHidden = new double[MLPAutoencoder.this.m_numUnits];
/*  381: 605 */           double[] outputsOut = new double[MLPAutoencoder.this.m_numAttributes];
/*  382: 606 */           double[] deltaHidden = new double[MLPAutoencoder.this.m_numUnits];
/*  383: 607 */           double[] deltaOut = new double[MLPAutoencoder.this.m_numAttributes];
/*  384: 608 */           double[] sigmoidDerivativesHidden = new double[MLPAutoencoder.this.m_numUnits];
/*  385: 609 */           double[] localGrad = new double[MLPAutoencoder.this.m_MLPParameters.length];
/*  386: 610 */           for (int k = lo; k < hi; k++)
/*  387:     */           {
/*  388: 611 */             Instance inst = MLPAutoencoder.this.m_data.instance(k);
/*  389: 612 */             MLPAutoencoder.this.calculateOutputsHidden(inst, outputsHidden, sigmoidDerivativesHidden);
/*  390:     */             
/*  391: 614 */             MLPAutoencoder.this.updateGradient(localGrad, inst, outputsHidden, deltaHidden, outputsOut, deltaOut);
/*  392:     */             
/*  393: 616 */             MLPAutoencoder.this.updateGradientForHiddenUnits(localGrad, inst, sigmoidDerivativesHidden, deltaHidden);
/*  394: 620 */             if (MLPAutoencoder.this.m_useContractive) {
/*  395: 623 */               for (int i = 0; i < outputsHidden.length; i++)
/*  396:     */               {
/*  397: 624 */                 double sum = 0.0D;
/*  398: 625 */                 int offset = i * MLPAutoencoder.this.m_numAttributes;
/*  399: 626 */                 for (int index = 0; index < MLPAutoencoder.this.m_numAttributes; index++) {
/*  400: 627 */                   sum += MLPAutoencoder.this.m_MLPParameters[(offset + index)] * MLPAutoencoder.this.m_MLPParameters[(offset + index)];
/*  401:     */                 }
/*  402: 630 */                 double multiplier = MLPAutoencoder.this.m_lambda * inst.weight() * 2.0D * outputsHidden[i] * (1.0D - outputsHidden[i]) * outputsHidden[i] * (1.0D - outputsHidden[i]);
/*  403: 633 */                 for (int index = 0; index < MLPAutoencoder.this.m_numAttributes; index++) {
/*  404: 634 */                   localGrad[(offset + index)] += multiplier * MLPAutoencoder.this.m_MLPParameters[(offset + index)];
/*  405:     */                 }
/*  406: 637 */                 double multiplier2 = multiplier * (1.0D - 2.0D * outputsHidden[i]) * sum;
/*  407: 639 */                 if ((inst instanceof SparseInstance)) {
/*  408: 640 */                   for (int index = 0; index < inst.numValues(); index++) {
/*  409: 641 */                     localGrad[(offset + inst.index(index))] += multiplier2 * inst.valueSparse(index);
/*  410:     */                   }
/*  411:     */                 } else {
/*  412: 645 */                   for (int index = 0; index < MLPAutoencoder.this.m_numAttributes; index++) {
/*  413: 646 */                     localGrad[(offset + index)] += multiplier2 * inst.value(index);
/*  414:     */                   }
/*  415:     */                 }
/*  416: 652 */                 localGrad[(MLPAutoencoder.this.OFFSET_H_BIASES + i)] += multiplier2;
/*  417:     */               }
/*  418:     */             }
/*  419:     */           }
/*  420: 656 */           return localGrad;
/*  421:     */         }
/*  422: 658 */       });
/*  423: 659 */       results.add(futureGrad);
/*  424:     */     }
/*  425: 663 */     double[] grad = new double[this.m_MLPParameters.length];
/*  426:     */     try
/*  427:     */     {
/*  428: 665 */       for (Future<double[]> futureGrad : results)
/*  429:     */       {
/*  430: 666 */         double[] lg = (double[])futureGrad.get();
/*  431: 667 */         for (int i = 0; i < lg.length; i++) {
/*  432: 668 */           grad[i] += lg[i];
/*  433:     */         }
/*  434:     */       }
/*  435:     */     }
/*  436:     */     catch (Exception e)
/*  437:     */     {
/*  438: 672 */       System.out.println("Gradient could not be calculated.");
/*  439:     */     }
/*  440: 676 */     if (!this.m_useContractive) {
/*  441: 679 */       for (int k = 0; k < this.m_numUnits; k++)
/*  442:     */       {
/*  443: 680 */         int offset = k * this.m_numAttributes;
/*  444: 681 */         for (int j = 0; j < this.m_numAttributes; j++) {
/*  445: 682 */           grad[(offset + j)] += this.m_lambda * 2.0D * this.m_MLPParameters[(offset + j)];
/*  446:     */         }
/*  447:     */       }
/*  448:     */     }
/*  449: 687 */     double factor = 1.0D / this.m_data.sumOfWeights();
/*  450: 688 */     for (int i = 0; i < grad.length; i++) {
/*  451: 689 */       grad[i] *= factor;
/*  452:     */     }
/*  453: 693 */     if (this.m_Debug)
/*  454:     */     {
/*  455: 694 */       double MULTIPLIER = 1000.0D;
/*  456: 695 */       double EPSILON = 0.0001D;
/*  457: 696 */       for (int i = 0; i < this.m_MLPParameters.length; i++)
/*  458:     */       {
/*  459: 697 */         double backup = this.m_MLPParameters[i];
/*  460: 698 */         this.m_MLPParameters[i] = (backup + EPSILON);
/*  461: 699 */         double val1 = calculateSE();
/*  462: 700 */         this.m_MLPParameters[i] = (backup - EPSILON);
/*  463: 701 */         double val2 = calculateSE();
/*  464: 702 */         this.m_MLPParameters[i] = backup;
/*  465: 703 */         double empirical = Math.round((val1 - val2) / (2.0D * EPSILON) * MULTIPLIER) / MULTIPLIER;
/*  466:     */         
/*  467:     */ 
/*  468: 706 */         double derived = Math.round(grad[i] * MULTIPLIER) / MULTIPLIER;
/*  469: 707 */         if (empirical != derived)
/*  470:     */         {
/*  471: 708 */           System.err.println("Empirical gradient: " + empirical + " Derived gradient: " + derived);
/*  472:     */           
/*  473: 710 */           System.exit(1);
/*  474:     */         }
/*  475:     */       }
/*  476:     */     }
/*  477: 715 */     return grad;
/*  478:     */   }
/*  479:     */   
/*  480:     */   protected void updateGradient(double[] grad, Instance inst, double[] outputsHidden, double[] deltaHidden, double[] outputsOut, double[] deltaOut)
/*  481:     */   {
/*  482: 726 */     Arrays.fill(deltaHidden, 0.0D);
/*  483:     */     
/*  484:     */ 
/*  485: 729 */     calculateOutputsOut(outputsHidden, outputsOut);
/*  486: 732 */     if ((inst instanceof SparseInstance))
/*  487:     */     {
/*  488: 733 */       int instIndex = 0;
/*  489: 734 */       for (int index = 0; index < this.m_numAttributes; index++) {
/*  490: 735 */         if ((instIndex < inst.numValues()) && (inst.index(instIndex) == index)) {
/*  491: 736 */           deltaOut[index] = (inst.weight() * (outputsOut[index] - inst.valueSparse(instIndex++)));
/*  492:     */         } else {
/*  493: 738 */           deltaOut[index] = (inst.weight() * outputsOut[index]);
/*  494:     */         }
/*  495:     */       }
/*  496:     */     }
/*  497:     */     else
/*  498:     */     {
/*  499: 742 */       for (int j = 0; j < this.m_numAttributes; j++) {
/*  500: 743 */         deltaOut[j] = (inst.weight() * (outputsOut[j] - inst.value(j)));
/*  501:     */       }
/*  502:     */     }
/*  503: 748 */     for (int j = 0; j < this.m_numAttributes; j++) {
/*  504: 751 */       if ((deltaOut[j] > this.m_tolerance) || (deltaOut[j] < -this.m_tolerance))
/*  505:     */       {
/*  506: 756 */         for (int i = 0; i < this.m_numUnits; i++) {
/*  507: 757 */           deltaHidden[i] += deltaOut[j] * this.m_MLPParameters[(i * this.m_numAttributes + j)];
/*  508:     */         }
/*  509: 762 */         for (int i = 0; i < this.m_numUnits; i++) {
/*  510: 763 */           grad[(i * this.m_numAttributes + j)] += deltaOut[j] * outputsHidden[i];
/*  511:     */         }
/*  512: 767 */         grad[(this.OFFSET_O_BIASES + j)] += deltaOut[j];
/*  513:     */       }
/*  514:     */     }
/*  515:     */   }
/*  516:     */   
/*  517:     */   protected void updateGradientForHiddenUnits(double[] grad, Instance inst, double[] sigmoidDerivativesHidden, double[] deltaHidden)
/*  518:     */   {
/*  519: 778 */     for (int i = 0; i < this.m_numUnits; i++) {
/*  520: 779 */       deltaHidden[i] *= sigmoidDerivativesHidden[i];
/*  521:     */     }
/*  522: 783 */     for (int i = 0; i < this.m_numUnits; i++) {
/*  523: 786 */       if ((deltaHidden[i] > this.m_tolerance) || (deltaHidden[i] < -this.m_tolerance))
/*  524:     */       {
/*  525: 791 */         int offset = i * this.m_numAttributes;
/*  526: 792 */         if ((inst instanceof SparseInstance)) {
/*  527: 793 */           for (int index = 0; index < inst.numValues(); index++) {
/*  528: 794 */             grad[(offset + inst.index(index))] += deltaHidden[i] * inst.valueSparse(index);
/*  529:     */           }
/*  530:     */         } else {
/*  531: 798 */           for (int l = 0; l < this.m_numAttributes; l++) {
/*  532: 799 */             grad[(offset + l)] += deltaHidden[i] * inst.value(l);
/*  533:     */           }
/*  534:     */         }
/*  535: 802 */         grad[(this.OFFSET_H_BIASES + i)] += deltaHidden[i];
/*  536:     */       }
/*  537:     */     }
/*  538:     */   }
/*  539:     */   
/*  540:     */   protected void calculateOutputsHidden(Instance inst, double[] out, double[] d)
/*  541:     */   {
/*  542: 811 */     for (int i = 0; i < this.m_numUnits; i++)
/*  543:     */     {
/*  544: 812 */       double sum = 0.0D;
/*  545: 813 */       int offset = i * this.m_numAttributes;
/*  546: 814 */       if ((inst instanceof SparseInstance)) {
/*  547: 815 */         for (int index = 0; index < inst.numValues(); index++) {
/*  548: 816 */           sum += inst.valueSparse(index) * this.m_MLPParameters[(offset + inst.index(index))];
/*  549:     */         }
/*  550:     */       } else {
/*  551: 820 */         for (int j = 0; j < this.m_numAttributes; j++) {
/*  552: 821 */           sum += inst.value(j) * this.m_MLPParameters[(offset + j)];
/*  553:     */         }
/*  554:     */       }
/*  555: 824 */       sum += this.m_MLPParameters[(this.OFFSET_H_BIASES + i)];
/*  556: 825 */       out[i] = sigmoid(-sum, d, i);
/*  557:     */     }
/*  558:     */   }
/*  559:     */   
/*  560:     */   protected void calculateOutputsOut(double[] outputsHidden, double[] outputsOut)
/*  561:     */   {
/*  562: 836 */     Arrays.fill(outputsOut, 0.0D);
/*  563: 839 */     for (int i = 0; i < this.m_numUnits; i++)
/*  564:     */     {
/*  565: 840 */       int offset = i * this.m_numAttributes;
/*  566: 841 */       for (int j = 0; j < this.m_numAttributes; j++) {
/*  567: 842 */         outputsOut[j] += this.m_MLPParameters[(offset + j)] * outputsHidden[i];
/*  568:     */       }
/*  569:     */     }
/*  570: 847 */     for (int j = 0; j < this.m_numAttributes; j++) {
/*  571: 848 */       outputsOut[j] += this.m_MLPParameters[(this.OFFSET_O_BIASES + j)];
/*  572:     */     }
/*  573:     */   }
/*  574:     */   
/*  575:     */   protected double sigmoid(double x, double[] d, int index)
/*  576:     */   {
/*  577: 858 */     double output = 0.0D;
/*  578: 859 */     if (this.m_useExactSigmoid)
/*  579:     */     {
/*  580: 860 */       output = 1.0D / (1.0D + Math.exp(x));
/*  581: 861 */       if (d != null) {
/*  582: 862 */         d[index] = (output * (1.0D - output));
/*  583:     */       }
/*  584:     */     }
/*  585:     */     else
/*  586:     */     {
/*  587: 867 */       double y = 1.0D + x / 4096.0D;
/*  588: 868 */       x = y * y;
/*  589: 869 */       x *= x;
/*  590: 870 */       x *= x;
/*  591: 871 */       x *= x;
/*  592: 872 */       x *= x;
/*  593: 873 */       x *= x;
/*  594: 874 */       x *= x;
/*  595: 875 */       x *= x;
/*  596: 876 */       x *= x;
/*  597: 877 */       x *= x;
/*  598: 878 */       x *= x;
/*  599: 879 */       x *= x;
/*  600: 880 */       output = 1.0D / (1.0D + x);
/*  601: 883 */       if (d != null) {
/*  602: 884 */         d[index] = (output * (1.0D - output) / y);
/*  603:     */       }
/*  604:     */     }
/*  605: 888 */     return output;
/*  606:     */   }
/*  607:     */   
/*  608:     */   protected Instances determineOutputFormat(Instances inputFormat)
/*  609:     */   {
/*  610: 901 */     if (!this.m_outputInOriginalSpace)
/*  611:     */     {
/*  612: 904 */       ArrayList<Attribute> atts = new ArrayList(this.m_numUnits + 1);
/*  613: 905 */       for (int i = 0; i < this.m_numUnits; i++) {
/*  614: 906 */         atts.add(new Attribute("hidden_unit_" + i + "output"));
/*  615:     */       }
/*  616: 910 */       if (inputFormat.classIndex() >= 0) {
/*  617: 911 */         atts.add((Attribute)inputFormat.classAttribute().copy());
/*  618:     */       }
/*  619: 914 */       Instances outputFormat = new Instances(inputFormat.relationName() + "_autoencoded", atts, 0);
/*  620: 916 */       if (inputFormat.classIndex() >= 0) {
/*  621: 917 */         outputFormat.setClassIndex(outputFormat.numAttributes() - 1);
/*  622:     */       }
/*  623: 919 */       return outputFormat;
/*  624:     */     }
/*  625: 921 */     return new Instances(inputFormat, 0);
/*  626:     */   }
/*  627:     */   
/*  628:     */   protected Instances process(Instances instances)
/*  629:     */     throws Exception
/*  630:     */   {
/*  631: 936 */     if (!isFirstBatchDone())
/*  632:     */     {
/*  633: 937 */       initFilter(instances);
/*  634: 940 */       if (!this.m_WeightsFile.isDirectory())
/*  635:     */       {
/*  636: 941 */         PrintWriter pw = new PrintWriter(this.m_WeightsFile);
/*  637: 942 */         pw.println(new Instances(this.m_data, 0));
/*  638: 943 */         for (int i = 0; i < this.m_numUnits; i++)
/*  639:     */         {
/*  640: 944 */           int offset = i * this.m_numAttributes;
/*  641: 945 */           for (int j = 0; j < this.m_numAttributes; j++)
/*  642:     */           {
/*  643: 946 */             if (j > 0) {
/*  644: 947 */               pw.print(",");
/*  645:     */             }
/*  646: 949 */             pw.print(this.m_MLPParameters[(offset + j)]);
/*  647:     */           }
/*  648: 951 */           pw.println();
/*  649:     */         }
/*  650: 953 */         pw.close();
/*  651:     */       }
/*  652:     */     }
/*  653: 958 */     Instances result = determineOutputFormat(instances);
/*  654: 959 */     for (int i = 0; i < instances.numInstances(); i++)
/*  655:     */     {
/*  656: 960 */       Instance inst = instances.instance(i);
/*  657:     */       
/*  658:     */ 
/*  659: 963 */       double classVal = 0.0D;
/*  660: 964 */       if (instances.classIndex() >= 0)
/*  661:     */       {
/*  662: 965 */         classVal = inst.classValue();
/*  663: 966 */         this.m_Remove.input(inst);
/*  664: 967 */         inst = this.m_Remove.output();
/*  665:     */       }
/*  666: 971 */       if (this.m_Filter != null)
/*  667:     */       {
/*  668: 972 */         this.m_Filter.input(inst);
/*  669: 973 */         this.m_Filter.batchFinished();
/*  670: 974 */         inst = this.m_Filter.output();
/*  671:     */       }
/*  672: 978 */       double[] outputsHidden = new double[this.m_numUnits];
/*  673: 979 */       calculateOutputsHidden(inst, outputsHidden, null);
/*  674: 982 */       if (!this.m_outputInOriginalSpace)
/*  675:     */       {
/*  676: 985 */         if (instances.classIndex() >= 0)
/*  677:     */         {
/*  678: 986 */           double[] newVals = new double[this.m_numUnits + 1];
/*  679: 987 */           System.arraycopy(outputsHidden, 0, newVals, 0, this.m_numUnits);
/*  680: 988 */           newVals[(newVals.length - 1)] = classVal;
/*  681: 989 */           outputsHidden = newVals;
/*  682:     */         }
/*  683: 991 */         result.add(new DenseInstance(inst.weight(), outputsHidden));
/*  684:     */       }
/*  685:     */       else
/*  686:     */       {
/*  687: 993 */         double[] newVals = new double[instances.numAttributes()];
/*  688: 994 */         double[] outputsOut = new double[this.m_numAttributes];
/*  689: 995 */         calculateOutputsOut(outputsHidden, outputsOut);
/*  690: 996 */         int j = 0;
/*  691: 997 */         for (int index = 0; index < newVals.length; index++) {
/*  692: 998 */           if (index != instances.classIndex())
/*  693:     */           {
/*  694: 999 */             if ((this.m_filterType == 1) || (this.m_filterType == 0)) {
/*  695:1001 */               newVals[index] = (outputsOut[j] * this.m_factors[j] + this.m_offsets[j]);
/*  696:     */             } else {
/*  697:1003 */               newVals[index] = outputsOut[j];
/*  698:     */             }
/*  699:1005 */             j++;
/*  700:     */           }
/*  701:     */           else
/*  702:     */           {
/*  703:1007 */             newVals[index] = classVal;
/*  704:     */           }
/*  705:     */         }
/*  706:1010 */         result.add(new DenseInstance(inst.weight(), newVals));
/*  707:     */       }
/*  708:     */     }
/*  709:1013 */     return result;
/*  710:     */   }
/*  711:     */   
/*  712:     */   public String globalInfo()
/*  713:     */   {
/*  714:1024 */     return "Implements an autoencoder with one hidden layer and tied weights using WEKA's Optimization class by minimizing the squared error plus a quadratic penalty (weight decay) with the BFGS method. Provides contractive autoencoder as an alternative to weight decay. Note that all attributes are standardized, including the target. There are several parameters. The lambda parameter is used to determine the penalty on the size of the weights. The number of hidden units can also be specified. Note that large numbers produce long training times. Finally, it is possible to use conjugate gradient descent rather than BFGS updates, which may be faster for cases with many parameters. To improve speed, an approximate version of the logistic function is used as the activation function. Also, if delta values in the backpropagation step are  within the user-specified tolerance, the gradient is not updated for that particular instance, which saves some additional time. Paralled calculation of squared error and gradient is possible when multiple CPU cores are present. Data is split into batches and processed in separate threads in this case. Note that this only improves runtime for larger datasets. For more information on autoencoders, see\n\n" + getTechnicalInformation().toString();
/*  715:     */   }
/*  716:     */   
/*  717:     */   public TechnicalInformation getTechnicalInformation()
/*  718:     */   {
/*  719:1053 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  720:1054 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Salah Rifai and Pascal Vincent and Xavier Muller and Xavier Glorot and Yoshua Bengio");
/*  721:     */     
/*  722:     */ 
/*  723:     */ 
/*  724:1058 */     result.setValue(TechnicalInformation.Field.TITLE, "Contractive Auto-Encoders: Explicit Invariance During Feature Extraction");
/*  725:     */     
/*  726:     */ 
/*  727:1061 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "International Conference on Machine Learning");
/*  728:     */     
/*  729:1063 */     result.setValue(TechnicalInformation.Field.YEAR, "2011");
/*  730:1064 */     result.setValue(TechnicalInformation.Field.PAGES, "833-840");
/*  731:1065 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Omnipress");
/*  732:     */     
/*  733:1067 */     return result;
/*  734:     */   }
/*  735:     */   
/*  736:     */   public String toleranceTipText()
/*  737:     */   {
/*  738:1075 */     return "The tolerance parameter for the delta values.";
/*  739:     */   }
/*  740:     */   
/*  741:     */   public double getTolerance()
/*  742:     */   {
/*  743:1083 */     return this.m_tolerance;
/*  744:     */   }
/*  745:     */   
/*  746:     */   public void setTolerance(double newTolerance)
/*  747:     */   {
/*  748:1091 */     this.m_tolerance = newTolerance;
/*  749:     */   }
/*  750:     */   
/*  751:     */   public String numFunctionsTipText()
/*  752:     */   {
/*  753:1099 */     return "The number of hidden units to use.";
/*  754:     */   }
/*  755:     */   
/*  756:     */   public int getNumFunctions()
/*  757:     */   {
/*  758:1107 */     return this.m_numUnits;
/*  759:     */   }
/*  760:     */   
/*  761:     */   public void setNumFunctions(int newNumFunctions)
/*  762:     */   {
/*  763:1115 */     this.m_numUnits = newNumFunctions;
/*  764:     */   }
/*  765:     */   
/*  766:     */   public String lambdaTipText()
/*  767:     */   {
/*  768:1123 */     return "The lambda penalty factor for the penalty on the weights.";
/*  769:     */   }
/*  770:     */   
/*  771:     */   public double getLambda()
/*  772:     */   {
/*  773:1131 */     return this.m_lambda;
/*  774:     */   }
/*  775:     */   
/*  776:     */   public void setLambda(double newLambda)
/*  777:     */   {
/*  778:1139 */     this.m_lambda = newLambda;
/*  779:     */   }
/*  780:     */   
/*  781:     */   public String useCGDTipText()
/*  782:     */   {
/*  783:1147 */     return "Whether to use conjugate gradient descent (potentially useful for many parameters).";
/*  784:     */   }
/*  785:     */   
/*  786:     */   public boolean getUseCGD()
/*  787:     */   {
/*  788:1155 */     return this.m_useCGD;
/*  789:     */   }
/*  790:     */   
/*  791:     */   public void setUseCGD(boolean newUseCGD)
/*  792:     */   {
/*  793:1163 */     this.m_useCGD = newUseCGD;
/*  794:     */   }
/*  795:     */   
/*  796:     */   public String useContractiveAutoencoderTipText()
/*  797:     */   {
/*  798:1171 */     return "Whether to use contractive autoencoder rather than autoencoder with weight decay.";
/*  799:     */   }
/*  800:     */   
/*  801:     */   public boolean getUseContractiveAutoencoder()
/*  802:     */   {
/*  803:1179 */     return this.m_useContractive;
/*  804:     */   }
/*  805:     */   
/*  806:     */   public void setUseContractiveAutoencoder(boolean newUseContractiveAutoencoder)
/*  807:     */   {
/*  808:1187 */     this.m_useContractive = newUseContractiveAutoencoder;
/*  809:     */   }
/*  810:     */   
/*  811:     */   public String useExactSigmoidTipText()
/*  812:     */   {
/*  813:1195 */     return "Whether to use exact sigmoid function rather than approximation.";
/*  814:     */   }
/*  815:     */   
/*  816:     */   public boolean getUseExactSigmoid()
/*  817:     */   {
/*  818:1203 */     return this.m_useExactSigmoid;
/*  819:     */   }
/*  820:     */   
/*  821:     */   public void setUseExactSigmoid(boolean newUseExactSigmoid)
/*  822:     */   {
/*  823:1211 */     this.m_useExactSigmoid = newUseExactSigmoid;
/*  824:     */   }
/*  825:     */   
/*  826:     */   public String outputInOriginalSpaceTipText()
/*  827:     */   {
/*  828:1219 */     return "Whether to output data in original space, so not reduced.";
/*  829:     */   }
/*  830:     */   
/*  831:     */   public boolean getOutputInOriginalSpace()
/*  832:     */   {
/*  833:1227 */     return this.m_outputInOriginalSpace;
/*  834:     */   }
/*  835:     */   
/*  836:     */   public void setOutputInOriginalSpace(boolean newOutputInOriginalSpace)
/*  837:     */   {
/*  838:1235 */     this.m_outputInOriginalSpace = newOutputInOriginalSpace;
/*  839:     */   }
/*  840:     */   
/*  841:     */   public String numThreadsTipText()
/*  842:     */   {
/*  843:1243 */     return "The number of threads to use, which should be >= size of thread pool.";
/*  844:     */   }
/*  845:     */   
/*  846:     */   public int getNumThreads()
/*  847:     */   {
/*  848:1251 */     return this.m_numThreads;
/*  849:     */   }
/*  850:     */   
/*  851:     */   public void setNumThreads(int nT)
/*  852:     */   {
/*  853:1259 */     this.m_numThreads = nT;
/*  854:     */   }
/*  855:     */   
/*  856:     */   public String poolSizeTipText()
/*  857:     */   {
/*  858:1267 */     return "The size of the thread pool, for example, the number of cores in the CPU.";
/*  859:     */   }
/*  860:     */   
/*  861:     */   public int getPoolSize()
/*  862:     */   {
/*  863:1275 */     return this.m_poolSize;
/*  864:     */   }
/*  865:     */   
/*  866:     */   public void setPoolSize(int nT)
/*  867:     */   {
/*  868:1283 */     this.m_poolSize = nT;
/*  869:     */   }
/*  870:     */   
/*  871:     */   public String weightsFileTipText()
/*  872:     */   {
/*  873:1293 */     return "The file to write weights to in ARFF format. Nothing is written if this is a directory.";
/*  874:     */   }
/*  875:     */   
/*  876:     */   public File getWeightsFile()
/*  877:     */   {
/*  878:1302 */     return this.m_WeightsFile;
/*  879:     */   }
/*  880:     */   
/*  881:     */   public void setWeightsFile(File value)
/*  882:     */   {
/*  883:1311 */     this.m_WeightsFile = value;
/*  884:     */   }
/*  885:     */   
/*  886:     */   public String filterTypeTipText()
/*  887:     */   {
/*  888:1321 */     return "Determines how/if the data will be transformed.";
/*  889:     */   }
/*  890:     */   
/*  891:     */   public SelectedTag getFilterType()
/*  892:     */   {
/*  893:1332 */     return new SelectedTag(this.m_filterType, TAGS_FILTER);
/*  894:     */   }
/*  895:     */   
/*  896:     */   public void setFilterType(SelectedTag newType)
/*  897:     */   {
/*  898:1343 */     if (newType.getTags() == TAGS_FILTER) {
/*  899:1344 */       this.m_filterType = newType.getSelectedTag().getID();
/*  900:     */     }
/*  901:     */   }
/*  902:     */   
/*  903:     */   public Enumeration<Option> listOptions()
/*  904:     */   {
/*  905:1356 */     Vector<Option> newVector = new Vector(7);
/*  906:     */     
/*  907:1358 */     newVector.addElement(new Option("\tNumber of hidden units (default is 2).\n", "N", 1, "-N <int>"));
/*  908:     */     
/*  909:     */ 
/*  910:1361 */     newVector.addElement(new Option("\tLambda factor for penalty on weights (default is 0.01).\n", "L", 1, "-L <double>"));
/*  911:     */     
/*  912:     */ 
/*  913:1364 */     newVector.addElement(new Option("\tTolerance parameter for delta values (default is 1.0e-6).\n", "O", 1, "-O <double>"));
/*  914:     */     
/*  915:     */ 
/*  916:1367 */     newVector.addElement(new Option("\tUse conjugate gradient descent (recommended for many attributes).\n", "G", 0, "-G"));
/*  917:     */     
/*  918:     */ 
/*  919:1370 */     newVector.addElement(new Option("\tUse contractive autoencoder instead of autoencoder with weight decay.\n", "C", 0, "-C"));
/*  920:     */     
/*  921:     */ 
/*  922:     */ 
/*  923:1374 */     newVector.addElement(new Option("\tUse exact sigmoid function rather than approximation.\n", "X", 0, "-X"));
/*  924:     */     
/*  925:     */ 
/*  926:     */ 
/*  927:1378 */     newVector.addElement(new Option("\tOutput data in original space, so do not output reduced data.\n", "F", 0, "-F"));
/*  928:     */     
/*  929:     */ 
/*  930:1381 */     newVector.addElement(new Option("\t" + poolSizeTipText() + " (default 1)\n", "P", 1, "-P <int>"));
/*  931:     */     
/*  932:1383 */     newVector.addElement(new Option("\t" + numThreadsTipText() + " (default 1)\n", "E", 1, "-E <int>"));
/*  933:     */     
/*  934:     */ 
/*  935:1386 */     newVector.addElement(new Option("\tThe file to write weight vectors to in ARFF format.\n\t(default: none)", "weights-file", 1, "-weights-file <filename>"));
/*  936:     */     
/*  937:     */ 
/*  938:     */ 
/*  939:1390 */     newVector.addElement(new Option("\tWhether to 0=normalize/1=standardize/2=neither. (default 1=standardize)", "S", 1, "-S"));
/*  940:     */     
/*  941:     */ 
/*  942:     */ 
/*  943:1394 */     newVector.addAll(Collections.list(super.listOptions()));
/*  944:     */     
/*  945:1396 */     return newVector.elements();
/*  946:     */   }
/*  947:     */   
/*  948:     */   public void setOptions(String[] options)
/*  949:     */     throws Exception
/*  950:     */   {
/*  951:1473 */     String numFunctions = Utils.getOption('N', options);
/*  952:1474 */     if (numFunctions.length() != 0) {
/*  953:1475 */       setNumFunctions(Integer.parseInt(numFunctions));
/*  954:     */     } else {
/*  955:1477 */       setNumFunctions(2);
/*  956:     */     }
/*  957:1479 */     String Lambda = Utils.getOption('L', options);
/*  958:1480 */     if (Lambda.length() != 0) {
/*  959:1481 */       setLambda(Double.parseDouble(Lambda));
/*  960:     */     } else {
/*  961:1483 */       setLambda(0.01D);
/*  962:     */     }
/*  963:1485 */     String Tolerance = Utils.getOption('O', options);
/*  964:1486 */     if (Tolerance.length() != 0) {
/*  965:1487 */       setTolerance(Double.parseDouble(Tolerance));
/*  966:     */     } else {
/*  967:1489 */       setTolerance(1.0E-006D);
/*  968:     */     }
/*  969:1491 */     this.m_useCGD = Utils.getFlag('G', options);
/*  970:1492 */     this.m_useContractive = Utils.getFlag('C', options);
/*  971:1493 */     this.m_useExactSigmoid = Utils.getFlag('X', options);
/*  972:1494 */     this.m_outputInOriginalSpace = Utils.getFlag('F', options);
/*  973:1495 */     String PoolSize = Utils.getOption('P', options);
/*  974:1496 */     if (PoolSize.length() != 0) {
/*  975:1497 */       setPoolSize(Integer.parseInt(PoolSize));
/*  976:     */     } else {
/*  977:1499 */       setPoolSize(1);
/*  978:     */     }
/*  979:1501 */     String NumThreads = Utils.getOption('E', options);
/*  980:1502 */     if (NumThreads.length() != 0) {
/*  981:1503 */       setNumThreads(Integer.parseInt(NumThreads));
/*  982:     */     } else {
/*  983:1505 */       setNumThreads(1);
/*  984:     */     }
/*  985:1507 */     String tmpStr = Utils.getOption("weights-file", options);
/*  986:1508 */     if (tmpStr.length() != 0) {
/*  987:1509 */       setWeightsFile(new File(tmpStr));
/*  988:     */     } else {
/*  989:1511 */       setWeightsFile(new File(System.getProperty("user.dir")));
/*  990:     */     }
/*  991:1513 */     tmpStr = Utils.getOption('S', options);
/*  992:1514 */     if (tmpStr.length() != 0) {
/*  993:1515 */       setFilterType(new SelectedTag(Integer.parseInt(tmpStr), TAGS_FILTER));
/*  994:     */     } else {
/*  995:1517 */       setFilterType(new SelectedTag(1, TAGS_FILTER));
/*  996:     */     }
/*  997:1520 */     super.setOptions(options);
/*  998:     */     
/*  999:1522 */     Utils.checkForRemainingOptions(options);
/* 1000:     */   }
/* 1001:     */   
/* 1002:     */   public String[] getOptions()
/* 1003:     */   {
/* 1004:1533 */     Vector<String> options = new Vector();
/* 1005:     */     
/* 1006:1535 */     options.add("-N");
/* 1007:1536 */     options.add("" + getNumFunctions());
/* 1008:     */     
/* 1009:1538 */     options.add("-L");
/* 1010:1539 */     options.add("" + getLambda());
/* 1011:     */     
/* 1012:1541 */     options.add("-O");
/* 1013:1542 */     options.add("" + getTolerance());
/* 1014:1544 */     if (this.m_useCGD) {
/* 1015:1545 */       options.add("-G");
/* 1016:     */     }
/* 1017:1548 */     if (this.m_useContractive) {
/* 1018:1549 */       options.add("-C");
/* 1019:     */     }
/* 1020:1552 */     if (this.m_useExactSigmoid) {
/* 1021:1553 */       options.add("-X");
/* 1022:     */     }
/* 1023:1556 */     if (this.m_outputInOriginalSpace) {
/* 1024:1557 */       options.add("-F");
/* 1025:     */     }
/* 1026:1560 */     options.add("-P");
/* 1027:1561 */     options.add("" + getPoolSize());
/* 1028:     */     
/* 1029:1563 */     options.add("-E");
/* 1030:1564 */     options.add("" + getNumThreads());
/* 1031:     */     
/* 1032:1566 */     options.add("-weights-file");
/* 1033:1567 */     options.add("" + getWeightsFile());
/* 1034:     */     
/* 1035:1569 */     options.add("-S");
/* 1036:1570 */     options.add("" + this.m_filterType);
/* 1037:     */     
/* 1038:1572 */     Collections.addAll(options, super.getOptions());
/* 1039:     */     
/* 1040:1574 */     return (String[])options.toArray(new String[0]);
/* 1041:     */   }
/* 1042:     */   
/* 1043:     */   public static void main(String[] argv)
/* 1044:     */   {
/* 1045:1583 */     runFilter(new MLPAutoencoder(), argv);
/* 1046:     */   }
/* 1047:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.MLPAutoencoder
 * JD-Core Version:    0.7.0.1
 */