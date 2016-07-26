/*    1:     */ package weka.classifiers.functions;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.util.Arrays;
/*    5:     */ import java.util.Collections;
/*    6:     */ import java.util.Enumeration;
/*    7:     */ import java.util.HashSet;
/*    8:     */ import java.util.Random;
/*    9:     */ import java.util.Set;
/*   10:     */ import java.util.Vector;
/*   11:     */ import java.util.concurrent.Callable;
/*   12:     */ import java.util.concurrent.ExecutorService;
/*   13:     */ import java.util.concurrent.Executors;
/*   14:     */ import java.util.concurrent.Future;
/*   15:     */ import weka.classifiers.Classifier;
/*   16:     */ import weka.classifiers.RandomizableClassifier;
/*   17:     */ import weka.classifiers.functions.activation.ActivationFunction;
/*   18:     */ import weka.classifiers.functions.activation.ApproximateSigmoid;
/*   19:     */ import weka.classifiers.functions.loss.LossFunction;
/*   20:     */ import weka.classifiers.functions.loss.SquaredError;
/*   21:     */ import weka.classifiers.rules.ZeroR;
/*   22:     */ import weka.core.Attribute;
/*   23:     */ import weka.core.Capabilities;
/*   24:     */ import weka.core.Capabilities.Capability;
/*   25:     */ import weka.core.ConjugateGradientOptimization;
/*   26:     */ import weka.core.Instance;
/*   27:     */ import weka.core.Instances;
/*   28:     */ import weka.core.Optimization;
/*   29:     */ import weka.core.Option;
/*   30:     */ import weka.core.OptionHandler;
/*   31:     */ import weka.core.RevisionUtils;
/*   32:     */ import weka.core.Utils;
/*   33:     */ import weka.core.WeightedInstancesHandler;
/*   34:     */ import weka.filters.Filter;
/*   35:     */ import weka.filters.unsupervised.attribute.NominalToBinary;
/*   36:     */ import weka.filters.unsupervised.attribute.RemoveUseless;
/*   37:     */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*   38:     */ 
/*   39:     */ public abstract class MLPModel
/*   40:     */   extends RandomizableClassifier
/*   41:     */   implements WeightedInstancesHandler
/*   42:     */ {
/*   43:     */   private static final long serialVersionUID = -3377473376438394655L;
/*   44:     */   
/*   45:     */   public Capabilities getCapabilities()
/*   46:     */   {
/*   47:  68 */     Capabilities result = super.getCapabilities();
/*   48:  69 */     result.disableAll();
/*   49:     */     
/*   50:     */ 
/*   51:  72 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*   52:  73 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*   53:  74 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*   54:  75 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*   55:     */     
/*   56:  77 */     return result;
/*   57:     */   }
/*   58:     */   
/*   59:     */   protected class OptEng
/*   60:     */     extends Optimization
/*   61:     */   {
/*   62:     */     protected OptEng() {}
/*   63:     */     
/*   64:     */     protected double objectiveFunction(double[] x)
/*   65:     */     {
/*   66:  92 */       MLPModel.this.m_MLPParameters = x;
/*   67:  93 */       return MLPModel.this.calculateSE();
/*   68:     */     }
/*   69:     */     
/*   70:     */     protected double[] evaluateGradient(double[] x)
/*   71:     */     {
/*   72: 102 */       MLPModel.this.m_MLPParameters = x;
/*   73: 103 */       return MLPModel.this.calculateGradient();
/*   74:     */     }
/*   75:     */     
/*   76:     */     public String getRevision()
/*   77:     */     {
/*   78: 111 */       return RevisionUtils.extract("$Revision: 10949 $");
/*   79:     */     }
/*   80:     */   }
/*   81:     */   
/*   82:     */   protected class OptEngCGD
/*   83:     */     extends ConjugateGradientOptimization
/*   84:     */   {
/*   85:     */     protected OptEngCGD() {}
/*   86:     */     
/*   87:     */     protected double objectiveFunction(double[] x)
/*   88:     */     {
/*   89: 127 */       MLPModel.this.m_MLPParameters = x;
/*   90: 128 */       return MLPModel.this.calculateSE();
/*   91:     */     }
/*   92:     */     
/*   93:     */     protected double[] evaluateGradient(double[] x)
/*   94:     */     {
/*   95: 137 */       MLPModel.this.m_MLPParameters = x;
/*   96: 138 */       return MLPModel.this.calculateGradient();
/*   97:     */     }
/*   98:     */     
/*   99:     */     public String getRevision()
/*  100:     */     {
/*  101: 146 */       return RevisionUtils.extract("$Revision: 10949 $");
/*  102:     */     }
/*  103:     */   }
/*  104:     */   
/*  105: 151 */   protected LossFunction m_Loss = new SquaredError();
/*  106: 154 */   protected ActivationFunction m_ActivationFunction = new ApproximateSigmoid();
/*  107: 157 */   protected int m_numUnits = 2;
/*  108: 160 */   protected int m_classIndex = -1;
/*  109: 163 */   protected Instances m_data = null;
/*  110: 166 */   protected int m_numAttributes = -1;
/*  111: 169 */   protected int m_numClasses = -1;
/*  112: 172 */   protected double[] m_MLPParameters = null;
/*  113: 175 */   protected int OFFSET_WEIGHTS = -1;
/*  114: 178 */   protected int OFFSET_ATTRIBUTE_WEIGHTS = -1;
/*  115: 181 */   protected double m_ridge = 0.01D;
/*  116: 184 */   protected boolean m_useCGD = false;
/*  117: 187 */   protected double m_tolerance = 1.0E-006D;
/*  118: 190 */   protected int m_numThreads = 1;
/*  119: 193 */   protected int m_poolSize = 1;
/*  120: 196 */   protected Filter m_Filter = null;
/*  121:     */   protected RemoveUseless m_AttFilter;
/*  122:     */   protected NominalToBinary m_NominalToBinary;
/*  123:     */   protected ReplaceMissingValues m_ReplaceMissingValues;
/*  124:     */   private Classifier m_ZeroR;
/*  125: 211 */   protected transient ExecutorService m_Pool = null;
/*  126:     */   
/*  127:     */   protected Instances initializeClassifier(Instances data, Random random)
/*  128:     */     throws Exception
/*  129:     */   {
/*  130: 220 */     getCapabilities().testWithFail(data);
/*  131:     */     
/*  132: 222 */     data = new Instances(data);
/*  133: 223 */     data.deleteWithMissingClass();
/*  134: 226 */     if (data.numInstances() > 1) {
/*  135: 227 */       random = data.getRandomNumberGenerator(this.m_Seed);
/*  136:     */     }
/*  137: 229 */     data.randomize(random);
/*  138:     */     
/*  139:     */ 
/*  140: 232 */     this.m_ReplaceMissingValues = new ReplaceMissingValues();
/*  141: 233 */     this.m_ReplaceMissingValues.setInputFormat(data);
/*  142: 234 */     data = Filter.useFilter(data, this.m_ReplaceMissingValues);
/*  143:     */     
/*  144:     */ 
/*  145: 237 */     this.m_AttFilter = new RemoveUseless();
/*  146: 238 */     this.m_AttFilter.setInputFormat(data);
/*  147: 239 */     data = Filter.useFilter(data, this.m_AttFilter);
/*  148:     */     
/*  149:     */ 
/*  150: 242 */     this.m_numAttributes = -1;
/*  151:     */     
/*  152:     */ 
/*  153: 245 */     this.m_ZeroR = new ZeroR();
/*  154: 246 */     this.m_ZeroR.buildClassifier(data);
/*  155: 247 */     if (data.numAttributes() == 1)
/*  156:     */     {
/*  157: 248 */       System.err.println("Cannot build model (only class attribute present in data after removing useless attributes!), using ZeroR model instead!");
/*  158:     */       
/*  159:     */ 
/*  160: 251 */       return null;
/*  161:     */     }
/*  162: 255 */     this.m_NominalToBinary = new NominalToBinary();
/*  163: 256 */     this.m_NominalToBinary.setInputFormat(data);
/*  164: 257 */     data = Filter.useFilter(data, this.m_NominalToBinary);
/*  165:     */     
/*  166: 259 */     this.m_classIndex = data.classIndex();
/*  167: 260 */     this.m_numAttributes = data.numAttributes();
/*  168: 261 */     this.m_numClasses = data.numClasses();
/*  169:     */     
/*  170:     */ 
/*  171:     */ 
/*  172:     */ 
/*  173: 266 */     this.OFFSET_WEIGHTS = 0;
/*  174: 267 */     this.OFFSET_ATTRIBUTE_WEIGHTS = ((this.m_numUnits + 1) * this.m_numClasses);
/*  175: 268 */     this.m_MLPParameters = new double[this.OFFSET_ATTRIBUTE_WEIGHTS + this.m_numUnits * this.m_numAttributes];
/*  176: 272 */     for (int j = 0; j < this.m_numClasses; j++)
/*  177:     */     {
/*  178: 273 */       int offsetOW = this.OFFSET_WEIGHTS + j * (this.m_numUnits + 1);
/*  179: 274 */       for (int i = 0; i < this.m_numUnits; i++) {
/*  180: 275 */         this.m_MLPParameters[(offsetOW + i)] = (0.1D * random.nextGaussian());
/*  181:     */       }
/*  182: 277 */       this.m_MLPParameters[(offsetOW + this.m_numUnits)] = (0.1D * random.nextGaussian());
/*  183:     */     }
/*  184: 279 */     for (int i = 0; i < this.m_numUnits; i++)
/*  185:     */     {
/*  186: 280 */       int offsetW = this.OFFSET_ATTRIBUTE_WEIGHTS + i * this.m_numAttributes;
/*  187: 281 */       for (int j = 0; j < this.m_numAttributes; j++) {
/*  188: 282 */         this.m_MLPParameters[(offsetW + j)] = (0.1D * random.nextGaussian());
/*  189:     */       }
/*  190:     */     }
/*  191: 285 */     return data;
/*  192:     */   }
/*  193:     */   
/*  194:     */   public void buildClassifier(Instances data)
/*  195:     */     throws Exception
/*  196:     */   {
/*  197: 295 */     this.m_data = initializeClassifier(data, new Random(this.m_Seed));
/*  198: 296 */     if (this.m_data == null) {
/*  199: 297 */       return;
/*  200:     */     }
/*  201: 301 */     this.m_Pool = Executors.newFixedThreadPool(this.m_poolSize);
/*  202:     */     
/*  203:     */ 
/*  204: 304 */     Optimization opt = null;
/*  205: 305 */     if (!this.m_useCGD) {
/*  206: 306 */       opt = new OptEng();
/*  207:     */     } else {
/*  208: 308 */       opt = new OptEngCGD();
/*  209:     */     }
/*  210: 310 */     opt.setDebug(this.m_Debug);
/*  211:     */     
/*  212:     */ 
/*  213: 313 */     double[][] b = new double[2][this.m_MLPParameters.length];
/*  214: 314 */     for (int i = 0; i < 2; i++) {
/*  215: 315 */       for (int j = 0; j < this.m_MLPParameters.length; j++) {
/*  216: 316 */         b[i][j] = (0.0D / 0.0D);
/*  217:     */       }
/*  218:     */     }
/*  219: 320 */     this.m_MLPParameters = opt.findArgmin(this.m_MLPParameters, b);
/*  220: 321 */     while (this.m_MLPParameters == null)
/*  221:     */     {
/*  222: 322 */       this.m_MLPParameters = opt.getVarbValues();
/*  223: 323 */       if (this.m_Debug) {
/*  224: 324 */         System.out.println("First set of iterations finished, not enough!");
/*  225:     */       }
/*  226: 326 */       this.m_MLPParameters = opt.findArgmin(this.m_MLPParameters, b);
/*  227:     */     }
/*  228: 328 */     if (this.m_Debug) {
/*  229: 329 */       System.out.println("Loss (normalized space) after optimization: " + opt.getMinFunction());
/*  230:     */     }
/*  231: 333 */     this.m_data = new Instances(this.m_data, 0);
/*  232:     */     
/*  233:     */ 
/*  234: 336 */     this.m_Pool.shutdown();
/*  235:     */   }
/*  236:     */   
/*  237:     */   protected abstract double calculateErrorForOneInstance(double[] paramArrayOfDouble, Instance paramInstance);
/*  238:     */   
/*  239:     */   protected double calculateSE()
/*  240:     */   {
/*  241: 356 */     int chunksize = this.m_data.numInstances() / this.m_numThreads;
/*  242: 357 */     Set<Future<Double>> results = new HashSet();
/*  243: 360 */     for (int j = 0; j < this.m_numThreads; j++)
/*  244:     */     {
/*  245: 363 */       final int lo = j * chunksize;
/*  246: 364 */       final int hi = j < this.m_numThreads - 1 ? lo + chunksize : this.m_data.numInstances();
/*  247:     */       
/*  248:     */ 
/*  249:     */ 
/*  250: 368 */       Future<Double> futureSE = this.m_Pool.submit(new Callable()
/*  251:     */       {
/*  252:     */         public Double call()
/*  253:     */         {
/*  254: 371 */           double[] outputs = new double[MLPModel.this.m_numUnits];
/*  255: 372 */           double SE = 0.0D;
/*  256: 373 */           for (int k = lo; k < hi; k++)
/*  257:     */           {
/*  258: 374 */             Instance inst = MLPModel.this.m_data.instance(k);
/*  259:     */             
/*  260:     */ 
/*  261: 377 */             MLPModel.this.calculateOutputs(inst, outputs, null);
/*  262:     */             
/*  263:     */ 
/*  264: 380 */             SE += MLPModel.this.calculateErrorForOneInstance(outputs, inst);
/*  265:     */           }
/*  266: 382 */           return Double.valueOf(SE);
/*  267:     */         }
/*  268: 384 */       });
/*  269: 385 */       results.add(futureSE);
/*  270:     */     }
/*  271: 389 */     double SE = 0.0D;
/*  272:     */     try
/*  273:     */     {
/*  274: 391 */       for (Future<Double> futureSE : results) {
/*  275: 392 */         SE += ((Double)futureSE.get()).doubleValue();
/*  276:     */       }
/*  277:     */     }
/*  278:     */     catch (Exception e)
/*  279:     */     {
/*  280: 395 */       System.out.println("Loss could not be calculated.");
/*  281:     */     }
/*  282: 399 */     double squaredSumOfWeights = 0.0D;
/*  283: 400 */     for (int i = 0; i < this.m_numClasses; i++)
/*  284:     */     {
/*  285: 401 */       int offsetOW = this.OFFSET_WEIGHTS + i * (this.m_numUnits + 1);
/*  286: 402 */       for (int k = 0; k < this.m_numUnits; k++) {
/*  287: 403 */         squaredSumOfWeights += this.m_MLPParameters[(offsetOW + k)] * this.m_MLPParameters[(offsetOW + k)];
/*  288:     */       }
/*  289:     */     }
/*  290: 407 */     for (int k = 0; k < this.m_numUnits; k++)
/*  291:     */     {
/*  292: 408 */       int offsetW = this.OFFSET_ATTRIBUTE_WEIGHTS + k * this.m_numAttributes;
/*  293: 409 */       for (int j = 0; j < this.m_classIndex; j++) {
/*  294: 410 */         squaredSumOfWeights += this.m_MLPParameters[(offsetW + j)] * this.m_MLPParameters[(offsetW + j)];
/*  295:     */       }
/*  296: 413 */       for (int j = this.m_classIndex + 1; j < this.m_numAttributes; j++) {
/*  297: 414 */         squaredSumOfWeights += this.m_MLPParameters[(offsetW + j)] * this.m_MLPParameters[(offsetW + j)];
/*  298:     */       }
/*  299:     */     }
/*  300: 419 */     return (this.m_ridge * squaredSumOfWeights + 0.5D * SE) / this.m_data.sumOfWeights();
/*  301:     */   }
/*  302:     */   
/*  303:     */   protected double[] calculateGradient()
/*  304:     */   {
/*  305: 429 */     int chunksize = this.m_data.numInstances() / this.m_numThreads;
/*  306: 430 */     Set<Future<double[]>> results = new HashSet();
/*  307: 433 */     for (int j = 0; j < this.m_numThreads; j++)
/*  308:     */     {
/*  309: 436 */       final int lo = j * chunksize;
/*  310: 437 */       final int hi = j < this.m_numThreads - 1 ? lo + chunksize : this.m_data.numInstances();
/*  311:     */       
/*  312:     */ 
/*  313:     */ 
/*  314: 441 */       Future<double[]> futureGrad = this.m_Pool.submit(new Callable()
/*  315:     */       {
/*  316:     */         public double[] call()
/*  317:     */         {
/*  318: 445 */           double[] outputs = new double[MLPModel.this.m_numUnits];
/*  319: 446 */           double[] deltaHidden = new double[MLPModel.this.m_numUnits];
/*  320: 447 */           double[] activationDerivativesHidden = new double[MLPModel.this.m_numUnits];
/*  321: 448 */           double[] localGrad = new double[MLPModel.this.m_MLPParameters.length];
/*  322: 449 */           for (int k = lo; k < hi; k++)
/*  323:     */           {
/*  324: 450 */             Instance inst = MLPModel.this.m_data.instance(k);
/*  325: 451 */             MLPModel.this.calculateOutputs(inst, outputs, activationDerivativesHidden);
/*  326: 452 */             MLPModel.this.updateGradient(localGrad, inst, outputs, deltaHidden);
/*  327: 453 */             MLPModel.this.updateGradientForHiddenUnits(localGrad, inst, activationDerivativesHidden, deltaHidden);
/*  328:     */           }
/*  329: 456 */           return localGrad;
/*  330:     */         }
/*  331: 458 */       });
/*  332: 459 */       results.add(futureGrad);
/*  333:     */     }
/*  334: 463 */     double[] grad = new double[this.m_MLPParameters.length];
/*  335:     */     try
/*  336:     */     {
/*  337: 465 */       for (Future<double[]> futureGrad : results)
/*  338:     */       {
/*  339: 466 */         double[] lg = (double[])futureGrad.get();
/*  340: 467 */         for (int i = 0; i < lg.length; i++) {
/*  341: 468 */           grad[i] += lg[i];
/*  342:     */         }
/*  343:     */       }
/*  344:     */     }
/*  345:     */     catch (Exception e)
/*  346:     */     {
/*  347: 472 */       System.out.println("Gradient could not be calculated.");
/*  348:     */     }
/*  349: 476 */     for (int i = 0; i < this.m_numClasses; i++)
/*  350:     */     {
/*  351: 477 */       int offsetOW = this.OFFSET_WEIGHTS + i * (this.m_numUnits + 1);
/*  352: 478 */       for (int k = 0; k < this.m_numUnits; k++) {
/*  353: 479 */         grad[(offsetOW + k)] += this.m_ridge * 2.0D * this.m_MLPParameters[(offsetOW + k)];
/*  354:     */       }
/*  355:     */     }
/*  356: 482 */     for (int k = 0; k < this.m_numUnits; k++)
/*  357:     */     {
/*  358: 483 */       int offsetW = this.OFFSET_ATTRIBUTE_WEIGHTS + k * this.m_numAttributes;
/*  359: 484 */       for (int j = 0; j < this.m_classIndex; j++) {
/*  360: 485 */         grad[(offsetW + j)] += this.m_ridge * 2.0D * this.m_MLPParameters[(offsetW + j)];
/*  361:     */       }
/*  362: 487 */       for (int j = this.m_classIndex + 1; j < this.m_numAttributes; j++) {
/*  363: 488 */         grad[(offsetW + j)] += this.m_ridge * 2.0D * this.m_MLPParameters[(offsetW + j)];
/*  364:     */       }
/*  365:     */     }
/*  366: 492 */     double factor = 1.0D / this.m_data.sumOfWeights();
/*  367: 493 */     for (int i = 0; i < grad.length; i++) {
/*  368: 494 */       grad[i] *= factor;
/*  369:     */     }
/*  370: 497 */     return grad;
/*  371:     */   }
/*  372:     */   
/*  373:     */   protected abstract double[] computeDeltas(Instance paramInstance, double[] paramArrayOfDouble);
/*  374:     */   
/*  375:     */   protected void updateGradient(double[] grad, Instance inst, double[] outputs, double[] deltaHidden)
/*  376:     */   {
/*  377: 512 */     Arrays.fill(deltaHidden, 0.0D);
/*  378:     */     
/*  379:     */ 
/*  380: 515 */     double[] deltaOut = computeDeltas(inst, outputs);
/*  381: 518 */     for (int j = 0; j < this.m_numClasses; j++) {
/*  382: 521 */       if ((deltaOut[j] > this.m_tolerance) || (deltaOut[j] < -this.m_tolerance))
/*  383:     */       {
/*  384: 526 */         int offsetOW = this.OFFSET_WEIGHTS + j * (this.m_numUnits + 1);
/*  385: 529 */         for (int i = 0; i < this.m_numUnits; i++) {
/*  386: 530 */           deltaHidden[i] += deltaOut[j] * this.m_MLPParameters[(offsetOW + i)];
/*  387:     */         }
/*  388: 534 */         for (int i = 0; i < this.m_numUnits; i++) {
/*  389: 535 */           grad[(offsetOW + i)] += deltaOut[j] * outputs[i];
/*  390:     */         }
/*  391: 539 */         grad[(offsetOW + this.m_numUnits)] += deltaOut[j];
/*  392:     */       }
/*  393:     */     }
/*  394:     */   }
/*  395:     */   
/*  396:     */   protected void updateGradientForHiddenUnits(double[] grad, Instance inst, double[] activationDerivativesHidden, double[] deltaHidden)
/*  397:     */   {
/*  398: 550 */     for (int i = 0; i < this.m_numUnits; i++) {
/*  399: 551 */       deltaHidden[i] *= activationDerivativesHidden[i];
/*  400:     */     }
/*  401: 555 */     for (int i = 0; i < this.m_numUnits; i++) {
/*  402: 558 */       if ((deltaHidden[i] > this.m_tolerance) || (deltaHidden[i] < -this.m_tolerance))
/*  403:     */       {
/*  404: 563 */         int offsetW = this.OFFSET_ATTRIBUTE_WEIGHTS + i * this.m_numAttributes;
/*  405: 564 */         for (int l = 0; l < this.m_classIndex; l++) {
/*  406: 565 */           grad[(offsetW + l)] += deltaHidden[i] * inst.value(l);
/*  407:     */         }
/*  408: 567 */         grad[(offsetW + this.m_classIndex)] += deltaHidden[i];
/*  409: 568 */         for (int l = this.m_classIndex + 1; l < this.m_numAttributes; l++) {
/*  410: 569 */           grad[(offsetW + l)] += deltaHidden[i] * inst.value(l);
/*  411:     */         }
/*  412:     */       }
/*  413:     */     }
/*  414:     */   }
/*  415:     */   
/*  416:     */   protected void calculateOutputs(Instance inst, double[] o, double[] d)
/*  417:     */   {
/*  418: 579 */     for (int i = 0; i < this.m_numUnits; i++)
/*  419:     */     {
/*  420: 580 */       int offsetW = this.OFFSET_ATTRIBUTE_WEIGHTS + i * this.m_numAttributes;
/*  421: 581 */       double sum = 0.0D;
/*  422: 582 */       for (int j = 0; j < this.m_classIndex; j++) {
/*  423: 583 */         sum += inst.value(j) * this.m_MLPParameters[(offsetW + j)];
/*  424:     */       }
/*  425: 585 */       sum += this.m_MLPParameters[(offsetW + this.m_classIndex)];
/*  426: 586 */       for (int j = this.m_classIndex + 1; j < this.m_numAttributes; j++) {
/*  427: 587 */         sum += inst.value(j) * this.m_MLPParameters[(offsetW + j)];
/*  428:     */       }
/*  429: 589 */       o[i] = this.m_ActivationFunction.activation(sum, d, i);
/*  430:     */     }
/*  431:     */   }
/*  432:     */   
/*  433:     */   protected double getOutput(int unit, double[] outputs)
/*  434:     */   {
/*  435: 599 */     int offsetOW = this.OFFSET_WEIGHTS + unit * (this.m_numUnits + 1);
/*  436: 600 */     double result = 0.0D;
/*  437: 601 */     for (int i = 0; i < this.m_numUnits; i++) {
/*  438: 602 */       result += this.m_MLPParameters[(offsetOW + i)] * outputs[i];
/*  439:     */     }
/*  440: 604 */     result += this.m_MLPParameters[(offsetOW + this.m_numUnits)];
/*  441: 605 */     return result;
/*  442:     */   }
/*  443:     */   
/*  444:     */   protected abstract double[] postProcessDistribution(double[] paramArrayOfDouble);
/*  445:     */   
/*  446:     */   public double[] distributionForInstance(Instance inst)
/*  447:     */     throws Exception
/*  448:     */   {
/*  449: 620 */     this.m_ReplaceMissingValues.input(inst);
/*  450: 621 */     inst = this.m_ReplaceMissingValues.output();
/*  451: 622 */     this.m_AttFilter.input(inst);
/*  452: 623 */     inst = this.m_AttFilter.output();
/*  453: 626 */     if (this.m_numAttributes == -1) {
/*  454: 627 */       return this.m_ZeroR.distributionForInstance(inst);
/*  455:     */     }
/*  456: 630 */     this.m_NominalToBinary.input(inst);
/*  457: 631 */     inst = this.m_NominalToBinary.output();
/*  458: 632 */     this.m_Filter.input(inst);
/*  459: 633 */     inst = this.m_Filter.output();
/*  460:     */     
/*  461: 635 */     double[] dist = new double[this.m_numClasses];
/*  462: 636 */     double[] outputs = new double[this.m_numUnits];
/*  463: 637 */     calculateOutputs(inst, outputs, null);
/*  464: 638 */     for (int i = 0; i < this.m_numClasses; i++) {
/*  465: 639 */       dist[i] = getOutput(i, outputs);
/*  466:     */     }
/*  467: 641 */     dist = postProcessDistribution(dist);
/*  468: 642 */     if (dist == null) {
/*  469: 643 */       return this.m_ZeroR.distributionForInstance(inst);
/*  470:     */     }
/*  471: 645 */     return dist;
/*  472:     */   }
/*  473:     */   
/*  474:     */   public String globalInfo()
/*  475:     */   {
/*  476: 656 */     return "Trains a multilayer perceptron with one hidden layer using WEKA's Optimization class by minimizing the given loss function plus a quadratic penalty with the BFGS method. Note that all attributes are standardized, including the target. There are several parameters. The ridge parameter is used to determine the penalty on the size of the weights. The number of hidden units can also be specified. Note that large numbers produce long training times. Finally, it is possible to use conjugate gradient descent rather than BFGS updates, which may be faster for cases with many parameters. To improve speed, an approximate version of the logistic function is used as the default activation function for the hidden layer, but other activation functions can be specified. In the output layer, the sigmoid function is used for classification. If the approximate sigmoid is specified for the hidden layers, it is also used for the output layer. For regression, the identity function is used activation function in the output layer. Also, if delta values in the backpropagation step are within the user-specified tolerance, the gradient is not updated for that particular instance, which saves some additional time. Parallel calculation of loss function and gradient is possible when multiple CPU cores are present. Data is split into batches and processed in separate threads in this case. Note that this only improves runtime for larger datasets. Nominal attributes are processed using the unsupervised NominalToBinary filter and missing values are replaced globally using ReplaceMissingValues.";
/*  477:     */   }
/*  478:     */   
/*  479:     */   public String toleranceTipText()
/*  480:     */   {
/*  481: 683 */     return "The tolerance parameter for the delta values.";
/*  482:     */   }
/*  483:     */   
/*  484:     */   public double getTolerance()
/*  485:     */   {
/*  486: 691 */     return this.m_tolerance;
/*  487:     */   }
/*  488:     */   
/*  489:     */   public void setTolerance(double newTolerance)
/*  490:     */   {
/*  491: 699 */     this.m_tolerance = newTolerance;
/*  492:     */   }
/*  493:     */   
/*  494:     */   public String numFunctionsTipText()
/*  495:     */   {
/*  496: 707 */     return "The number of hidden units to use.";
/*  497:     */   }
/*  498:     */   
/*  499:     */   public int getNumFunctions()
/*  500:     */   {
/*  501: 715 */     return this.m_numUnits;
/*  502:     */   }
/*  503:     */   
/*  504:     */   public void setNumFunctions(int newNumFunctions)
/*  505:     */   {
/*  506: 723 */     this.m_numUnits = newNumFunctions;
/*  507:     */   }
/*  508:     */   
/*  509:     */   public String ridgeTipText()
/*  510:     */   {
/*  511: 731 */     return "The ridge penalty factor for the quadratic penalty on the weights.";
/*  512:     */   }
/*  513:     */   
/*  514:     */   public double getRidge()
/*  515:     */   {
/*  516: 739 */     return this.m_ridge;
/*  517:     */   }
/*  518:     */   
/*  519:     */   public void setRidge(double newRidge)
/*  520:     */   {
/*  521: 747 */     this.m_ridge = newRidge;
/*  522:     */   }
/*  523:     */   
/*  524:     */   public String useCGDTipText()
/*  525:     */   {
/*  526: 755 */     return "Whether to use conjugate gradient descent (potentially useful for many parameters).";
/*  527:     */   }
/*  528:     */   
/*  529:     */   public boolean getUseCGD()
/*  530:     */   {
/*  531: 763 */     return this.m_useCGD;
/*  532:     */   }
/*  533:     */   
/*  534:     */   public void setUseCGD(boolean newUseCGD)
/*  535:     */   {
/*  536: 771 */     this.m_useCGD = newUseCGD;
/*  537:     */   }
/*  538:     */   
/*  539:     */   public String numThreadsTipText()
/*  540:     */   {
/*  541: 779 */     return "The number of threads to use, which should be >= size of thread pool.";
/*  542:     */   }
/*  543:     */   
/*  544:     */   public int getNumThreads()
/*  545:     */   {
/*  546: 787 */     return this.m_numThreads;
/*  547:     */   }
/*  548:     */   
/*  549:     */   public void setNumThreads(int nT)
/*  550:     */   {
/*  551: 795 */     this.m_numThreads = nT;
/*  552:     */   }
/*  553:     */   
/*  554:     */   public String poolSizeTipText()
/*  555:     */   {
/*  556: 803 */     return "The size of the thread pool, for example, the number of cores in the CPU.";
/*  557:     */   }
/*  558:     */   
/*  559:     */   public int getPoolSize()
/*  560:     */   {
/*  561: 811 */     return this.m_poolSize;
/*  562:     */   }
/*  563:     */   
/*  564:     */   public void setPoolSize(int nT)
/*  565:     */   {
/*  566: 819 */     this.m_poolSize = nT;
/*  567:     */   }
/*  568:     */   
/*  569:     */   public String lossFunctionTipText()
/*  570:     */   {
/*  571: 827 */     return "The loss function to optimize.";
/*  572:     */   }
/*  573:     */   
/*  574:     */   public LossFunction getLossFunction()
/*  575:     */   {
/*  576: 835 */     return this.m_Loss;
/*  577:     */   }
/*  578:     */   
/*  579:     */   public void setLossFunction(LossFunction loss)
/*  580:     */   {
/*  581: 843 */     this.m_Loss = loss;
/*  582:     */   }
/*  583:     */   
/*  584:     */   public String activationFunctionTipText()
/*  585:     */   {
/*  586: 851 */     return "The activation function to use in the hidden layer.";
/*  587:     */   }
/*  588:     */   
/*  589:     */   public ActivationFunction getActivationFunction()
/*  590:     */   {
/*  591: 858 */     return this.m_ActivationFunction;
/*  592:     */   }
/*  593:     */   
/*  594:     */   public void setActivationFunction(ActivationFunction func)
/*  595:     */   {
/*  596: 866 */     this.m_ActivationFunction = func;
/*  597:     */   }
/*  598:     */   
/*  599:     */   public Enumeration<Option> listOptions()
/*  600:     */   {
/*  601: 877 */     Vector<Option> newVector = new Vector(6);
/*  602:     */     
/*  603: 879 */     newVector.addElement(new Option("\tNumber of hidden units (default is 2).", "N", 1, "-N <int>"));
/*  604:     */     
/*  605:     */ 
/*  606: 882 */     newVector.addElement(new Option("\tRidge factor for quadratic penalty on weights (default is 0.01).", "R", 1, "-R <double>"));
/*  607:     */     
/*  608:     */ 
/*  609: 885 */     newVector.addElement(new Option("\tTolerance parameter for delta values (default is 1.0e-6).", "O", 1, "-O <double>"));
/*  610:     */     
/*  611:     */ 
/*  612: 888 */     newVector.addElement(new Option("\tUse conjugate gradient descent (recommended for many attributes).", "G", 0, "-G"));
/*  613:     */     
/*  614:     */ 
/*  615: 891 */     newVector.addElement(new Option("\t" + poolSizeTipText() + " (default 1)", "P", 1, "-P <int>"));
/*  616:     */     
/*  617: 893 */     newVector.addElement(new Option("\t" + numThreadsTipText() + " (default 1)", "E", 1, "-E <int>"));
/*  618:     */     
/*  619:     */ 
/*  620: 896 */     newVector.addElement(new Option("\tThe loss function to use.\n\t(default: weka.classifiers.functions.loss.SquaredError)", "L", 1, "-L <classname and parameters>"));
/*  621:     */     
/*  622:     */ 
/*  623:     */ 
/*  624:     */ 
/*  625: 901 */     newVector.addElement(new Option("\tThe activation function to use.\n\t(default: weka.classifiers.functions.activation.ApproximateSigmoid)", "A", 1, "-A <classname and parameters>"));
/*  626:     */     
/*  627:     */ 
/*  628:     */ 
/*  629:     */ 
/*  630: 906 */     newVector.addAll(Collections.list(super.listOptions()));
/*  631: 908 */     if ((getLossFunction() instanceof OptionHandler))
/*  632:     */     {
/*  633: 909 */       newVector.addElement(new Option("", "", 0, "\nOptions specific to loss function " + getLossFunction().getClass().getName() + ":"));
/*  634:     */       
/*  635:     */ 
/*  636:     */ 
/*  637:     */ 
/*  638: 914 */       newVector.addAll(Collections.list(((OptionHandler)getLossFunction()).listOptions()));
/*  639:     */     }
/*  640: 917 */     if ((getActivationFunction() instanceof OptionHandler))
/*  641:     */     {
/*  642: 918 */       newVector.addElement(new Option("", "", 0, "\nOptions specific to activation function " + getLossFunction().getClass().getName() + ":"));
/*  643:     */       
/*  644:     */ 
/*  645:     */ 
/*  646:     */ 
/*  647: 923 */       newVector.addAll(Collections.list(((OptionHandler)getActivationFunction()).listOptions()));
/*  648:     */     }
/*  649: 925 */     return newVector.elements();
/*  650:     */   }
/*  651:     */   
/*  652:     */   public void setOptions(String[] options)
/*  653:     */     throws Exception
/*  654:     */   {
/*  655: 986 */     String numFunctions = Utils.getOption('N', options);
/*  656: 987 */     if (numFunctions.length() != 0) {
/*  657: 988 */       setNumFunctions(Integer.parseInt(numFunctions));
/*  658:     */     } else {
/*  659: 990 */       setNumFunctions(2);
/*  660:     */     }
/*  661: 992 */     String Ridge = Utils.getOption('R', options);
/*  662: 993 */     if (Ridge.length() != 0) {
/*  663: 994 */       setRidge(Double.parseDouble(Ridge));
/*  664:     */     } else {
/*  665: 996 */       setRidge(0.01D);
/*  666:     */     }
/*  667: 998 */     String Tolerance = Utils.getOption('O', options);
/*  668: 999 */     if (Tolerance.length() != 0) {
/*  669:1000 */       setTolerance(Double.parseDouble(Tolerance));
/*  670:     */     } else {
/*  671:1002 */       setTolerance(1.0E-006D);
/*  672:     */     }
/*  673:1004 */     this.m_useCGD = Utils.getFlag('G', options);
/*  674:1005 */     String PoolSize = Utils.getOption('P', options);
/*  675:1006 */     if (PoolSize.length() != 0) {
/*  676:1007 */       setPoolSize(Integer.parseInt(PoolSize));
/*  677:     */     } else {
/*  678:1009 */       setPoolSize(1);
/*  679:     */     }
/*  680:1011 */     String NumThreads = Utils.getOption('E', options);
/*  681:1012 */     if (NumThreads.length() != 0) {
/*  682:1013 */       setNumThreads(Integer.parseInt(NumThreads));
/*  683:     */     } else {
/*  684:1015 */       setNumThreads(1);
/*  685:     */     }
/*  686:1018 */     super.setOptions(options);
/*  687:     */     
/*  688:1020 */     String tmpStr = Utils.getOption('L', options);
/*  689:1021 */     String[] tmpOptions = Utils.splitOptions(tmpStr);
/*  690:1022 */     if (tmpOptions.length != 0)
/*  691:     */     {
/*  692:1023 */       tmpStr = tmpOptions[0];
/*  693:1024 */       tmpOptions[0] = "";
/*  694:1025 */       setLossFunction((LossFunction)Utils.forName(LossFunction.class, tmpStr, tmpOptions));
/*  695:     */     }
/*  696:1028 */     tmpStr = Utils.getOption('A', options);
/*  697:1029 */     tmpOptions = Utils.splitOptions(tmpStr);
/*  698:1030 */     if (tmpOptions.length != 0)
/*  699:     */     {
/*  700:1031 */       tmpStr = tmpOptions[0];
/*  701:1032 */       tmpOptions[0] = "";
/*  702:1033 */       setActivationFunction((ActivationFunction)Utils.forName(ActivationFunction.class, tmpStr, tmpOptions));
/*  703:     */     }
/*  704:1036 */     Utils.checkForRemainingOptions(options);
/*  705:     */   }
/*  706:     */   
/*  707:     */   public String[] getOptions()
/*  708:     */   {
/*  709:1047 */     Vector<String> options = new Vector();
/*  710:     */     
/*  711:1049 */     options.add("-N");
/*  712:1050 */     options.add("" + getNumFunctions());
/*  713:     */     
/*  714:1052 */     options.add("-R");
/*  715:1053 */     options.add("" + getRidge());
/*  716:     */     
/*  717:1055 */     options.add("-O");
/*  718:1056 */     options.add("" + getTolerance());
/*  719:1058 */     if (this.m_useCGD) {
/*  720:1059 */       options.add("-G");
/*  721:     */     }
/*  722:1062 */     options.add("-P");
/*  723:1063 */     options.add("" + getPoolSize());
/*  724:     */     
/*  725:1065 */     options.add("-E");
/*  726:1066 */     options.add("" + getNumThreads());
/*  727:     */     
/*  728:1068 */     Collections.addAll(options, super.getOptions());
/*  729:     */     
/*  730:1070 */     options.add("-L");
/*  731:1071 */     options.add("" + getLossFunction().getClass().getName());
/*  732:1072 */     if ((getLossFunction() instanceof OptionHandler)) {
/*  733:1073 */       options.add(" " + Utils.joinOptions(((OptionHandler)getLossFunction()).getOptions()));
/*  734:     */     }
/*  735:1076 */     options.add("-A");
/*  736:1077 */     options.add("" + getActivationFunction().getClass().getName());
/*  737:1078 */     if ((getActivationFunction() instanceof OptionHandler)) {
/*  738:1079 */       options.add(" " + Utils.joinOptions(((OptionHandler)getActivationFunction()).getOptions()));
/*  739:     */     }
/*  740:1082 */     return (String[])options.toArray(new String[0]);
/*  741:     */   }
/*  742:     */   
/*  743:     */   public abstract String modelType();
/*  744:     */   
/*  745:     */   public String toString()
/*  746:     */   {
/*  747:1096 */     if (this.m_ZeroR == null) {
/*  748:1097 */       return "Classifier not built yet.";
/*  749:     */     }
/*  750:1100 */     if (this.m_numAttributes == -1) {
/*  751:1101 */       return this.m_ZeroR.toString();
/*  752:     */     }
/*  753:1104 */     String s = modelType() + " with ridge value " + getRidge() + " and " + getNumFunctions() + " hidden units (useCGD=" + getUseCGD() + ")\n\n";
/*  754:1107 */     for (int i = 0; i < this.m_numUnits; i++)
/*  755:     */     {
/*  756:1108 */       for (int j = 0; j < this.m_numClasses; j++) {
/*  757:1109 */         s = s + "Output unit " + j + " weight for hidden unit " + i + ": " + this.m_MLPParameters[(this.OFFSET_WEIGHTS + j * (this.m_numUnits + 1) + i)] + "\n";
/*  758:     */       }
/*  759:1112 */       s = s + "\nHidden unit " + i + " weights:\n\n";
/*  760:1113 */       for (int j = 0; j < this.m_numAttributes; j++) {
/*  761:1114 */         if (j != this.m_classIndex) {
/*  762:1115 */           s = s + this.m_MLPParameters[(this.OFFSET_ATTRIBUTE_WEIGHTS + i * this.m_numAttributes + j)] + " " + this.m_data.attribute(j).name() + "\n";
/*  763:     */         }
/*  764:     */       }
/*  765:1120 */       s = s + "\nHidden unit " + i + " bias: " + this.m_MLPParameters[(this.OFFSET_ATTRIBUTE_WEIGHTS + (i * this.m_numAttributes + this.m_classIndex))] + "\n\n";
/*  766:     */     }
/*  767:1126 */     for (int j = 0; j < this.m_numClasses; j++) {
/*  768:1127 */       s = s + "Output unit " + j + " bias: " + this.m_MLPParameters[(this.OFFSET_WEIGHTS + j * (this.m_numUnits + 1) + this.m_numUnits)] + "\n";
/*  769:     */     }
/*  770:1132 */     return s;
/*  771:     */   }
/*  772:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.MLPModel
 * JD-Core Version:    0.7.0.1
 */