/*   1:    */ package weka.classifiers.functions;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.HashSet;
/*   7:    */ import java.util.Random;
/*   8:    */ import java.util.Set;
/*   9:    */ import java.util.Vector;
/*  10:    */ import java.util.concurrent.Callable;
/*  11:    */ import java.util.concurrent.ExecutorService;
/*  12:    */ import java.util.concurrent.Executors;
/*  13:    */ import java.util.concurrent.Future;
/*  14:    */ import weka.classifiers.RandomizableClassifier;
/*  15:    */ import weka.classifiers.functions.supportVector.CachedKernel;
/*  16:    */ import weka.classifiers.functions.supportVector.Kernel;
/*  17:    */ import weka.classifiers.functions.supportVector.PolyKernel;
/*  18:    */ import weka.core.Capabilities;
/*  19:    */ import weka.core.Capabilities.Capability;
/*  20:    */ import weka.core.ConjugateGradientOptimization;
/*  21:    */ import weka.core.Instance;
/*  22:    */ import weka.core.Instances;
/*  23:    */ import weka.core.Optimization;
/*  24:    */ import weka.core.Option;
/*  25:    */ import weka.core.OptionHandler;
/*  26:    */ import weka.core.RevisionUtils;
/*  27:    */ import weka.core.Utils;
/*  28:    */ import weka.filters.Filter;
/*  29:    */ import weka.filters.unsupervised.attribute.NominalToBinary;
/*  30:    */ import weka.filters.unsupervised.attribute.RemoveUseless;
/*  31:    */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*  32:    */ import weka.filters.unsupervised.attribute.Standardize;
/*  33:    */ 
/*  34:    */ public class KernelLogisticRegression
/*  35:    */   extends RandomizableClassifier
/*  36:    */ {
/*  37:    */   static final long serialVersionUID = 6332117032546553533L;
/*  38:    */   protected Kernel m_kernel;
/*  39:    */   protected double[] m_weights;
/*  40:    */   protected Instances m_data;
/*  41:    */   protected double m_lambda;
/*  42:    */   protected boolean m_useCGD;
/*  43:    */   protected Standardize m_standardize;
/*  44:    */   protected ReplaceMissingValues m_replaceMissing;
/*  45:    */   protected NominalToBinary m_nominalToBinary;
/*  46:    */   protected RemoveUseless m_removeUseless;
/*  47:    */   protected int m_numThreads;
/*  48:    */   protected int m_poolSize;
/*  49:    */   protected transient ExecutorService m_Pool;
/*  50:    */   protected double[][] m_kernelMatrix;
/*  51:    */   protected double[] m_classValues;
/*  52:    */   
/*  53:    */   public KernelLogisticRegression()
/*  54:    */   {
/*  55:168 */     this.m_kernel = new PolyKernel();
/*  56:    */     
/*  57:    */ 
/*  58:    */ 
/*  59:    */ 
/*  60:    */ 
/*  61:    */ 
/*  62:    */ 
/*  63:    */ 
/*  64:177 */     this.m_lambda = 0.01D;
/*  65:    */     
/*  66:    */ 
/*  67:180 */     this.m_useCGD = false;
/*  68:    */     
/*  69:    */ 
/*  70:183 */     this.m_standardize = new Standardize();
/*  71:184 */     this.m_replaceMissing = new ReplaceMissingValues();
/*  72:185 */     this.m_nominalToBinary = new NominalToBinary();
/*  73:186 */     this.m_removeUseless = new RemoveUseless();
/*  74:    */     
/*  75:    */ 
/*  76:189 */     this.m_numThreads = 1;
/*  77:    */     
/*  78:    */ 
/*  79:192 */     this.m_poolSize = 1;
/*  80:    */     
/*  81:    */ 
/*  82:195 */     this.m_Pool = null;
/*  83:    */     
/*  84:    */ 
/*  85:198 */     this.m_kernelMatrix = ((double[][])null);
/*  86:    */     
/*  87:    */ 
/*  88:201 */     this.m_classValues = null;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public String globalInfo()
/*  92:    */   {
/*  93:211 */     return "This classifier generates a two-class kernel logistic regression model. The model is fit by minimizing the negative log-likelihood with a quadratic penalty using BFGS optimization, as implemented in the Optimization class. Alternatively, conjugate gradient optimization can be applied. The user can specify the kernel function and the value of lambda, the multiplier for the quadractic penalty. Using a linear kernel (the default) this method should give the same result as ridge logistic regression implemented in Logistic, assuming the ridge parameter is set to the same value as lambda, and not too small. By replacing the kernel function, we can learn non-linear decision boundaries.\n\nNote that the data is filtered using ReplaceMissingValues, RemoveUseless, NominalToBinary, and Standardize (in that order).\n\nIf a CachedKernel is used, this class will overwrite the manually specified cache size and use a full cache instead.\n\nTo apply this classifier to multi-class problems, use the MultiClassClassifier.\n\nThis implementation stores the full kernel matrix at training time for speed reasons.";
/*  94:    */   }
/*  95:    */   
/*  96:    */   public Capabilities getCapabilities()
/*  97:    */   {
/*  98:236 */     Capabilities result = super.getCapabilities();
/*  99:237 */     result.disableAll();
/* 100:    */     
/* 101:    */ 
/* 102:240 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 103:241 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 104:242 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 105:    */     
/* 106:    */ 
/* 107:245 */     result.enable(Capabilities.Capability.BINARY_CLASS);
/* 108:246 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 109:    */     
/* 110:248 */     return result;
/* 111:    */   }
/* 112:    */   
/* 113:    */   public Enumeration<Option> listOptions()
/* 114:    */   {
/* 115:259 */     Vector<Option> result = new Vector();
/* 116:    */     
/* 117:261 */     result.addElement(new Option("\tThe Kernel to use.\n\t(default: weka.classifiers.functions.supportVector.PolyKernel)", "K", 1, "-K <classname and parameters>"));
/* 118:    */     
/* 119:    */ 
/* 120:    */ 
/* 121:265 */     result.addElement(new Option("\tThe lambda penalty parameter. (default 0.01)", "L", 1, "-L <double>"));
/* 122:    */     
/* 123:    */ 
/* 124:268 */     result.addElement(new Option("\tUse conjugate gradient descent instead of BFGS.\n", "G", 0, "-G"));
/* 125:    */     
/* 126:    */ 
/* 127:271 */     result.addElement(new Option("\t" + poolSizeTipText() + " (default 1)\n", "P", 1, "-P <int>"));
/* 128:    */     
/* 129:273 */     result.addElement(new Option("\t" + numThreadsTipText() + " (default 1)\n", "E", 1, "-E <int>"));
/* 130:    */     
/* 131:    */ 
/* 132:276 */     result.addAll(Collections.list(super.listOptions()));
/* 133:    */     
/* 134:278 */     result.addElement(new Option("", "", 0, "\nOptions specific to kernel " + getKernel().getClass().getName() + ":"));
/* 135:    */     
/* 136:    */ 
/* 137:281 */     result.addAll(Collections.list(getKernel().listOptions()));
/* 138:    */     
/* 139:    */ 
/* 140:284 */     return result.elements();
/* 141:    */   }
/* 142:    */   
/* 143:    */   public void setOptions(String[] options)
/* 144:    */     throws Exception
/* 145:    */   {
/* 146:377 */     String tmpStr = Utils.getOption('L', options);
/* 147:378 */     if (tmpStr.length() != 0) {
/* 148:379 */       setLambda(Double.parseDouble(tmpStr));
/* 149:    */     } else {
/* 150:381 */       setLambda(0.01D);
/* 151:    */     }
/* 152:384 */     this.m_useCGD = Utils.getFlag('G', options);
/* 153:    */     
/* 154:386 */     tmpStr = Utils.getOption('K', options);
/* 155:387 */     String[] tmpOptions = Utils.splitOptions(tmpStr);
/* 156:388 */     if (tmpOptions.length != 0)
/* 157:    */     {
/* 158:389 */       tmpStr = tmpOptions[0];
/* 159:390 */       tmpOptions[0] = "";
/* 160:391 */       setKernel(Kernel.forName(tmpStr, tmpOptions));
/* 161:    */     }
/* 162:393 */     String PoolSize = Utils.getOption('P', options);
/* 163:394 */     if (PoolSize.length() != 0) {
/* 164:395 */       setPoolSize(Integer.parseInt(PoolSize));
/* 165:    */     } else {
/* 166:397 */       setPoolSize(1);
/* 167:    */     }
/* 168:399 */     String NumThreads = Utils.getOption('E', options);
/* 169:400 */     if (NumThreads.length() != 0) {
/* 170:401 */       setNumThreads(Integer.parseInt(NumThreads));
/* 171:    */     } else {
/* 172:403 */       setNumThreads(1);
/* 173:    */     }
/* 174:406 */     super.setOptions(options);
/* 175:    */     
/* 176:408 */     Utils.checkForRemainingOptions(tmpOptions);
/* 177:    */   }
/* 178:    */   
/* 179:    */   public String[] getOptions()
/* 180:    */   {
/* 181:419 */     Vector<String> result = new Vector();
/* 182:    */     
/* 183:421 */     result.add("-K");
/* 184:422 */     result.add("" + getKernel().getClass().getName() + " " + Utils.joinOptions(getKernel().getOptions()));
/* 185:    */     
/* 186:    */ 
/* 187:425 */     result.add("-L");
/* 188:426 */     result.add("" + getLambda());
/* 189:428 */     if (this.m_useCGD) {
/* 190:429 */       result.add("-G");
/* 191:    */     }
/* 192:432 */     result.add("-P");
/* 193:433 */     result.add("" + getPoolSize());
/* 194:    */     
/* 195:435 */     result.add("-E");
/* 196:436 */     result.add("" + getNumThreads());
/* 197:    */     
/* 198:438 */     Collections.addAll(result, super.getOptions());
/* 199:    */     
/* 200:440 */     return (String[])result.toArray(new String[result.size()]);
/* 201:    */   }
/* 202:    */   
/* 203:    */   public String numThreadsTipText()
/* 204:    */   {
/* 205:448 */     return "The number of threads to use, which should be >= size of thread pool.";
/* 206:    */   }
/* 207:    */   
/* 208:    */   public int getNumThreads()
/* 209:    */   {
/* 210:456 */     return this.m_numThreads;
/* 211:    */   }
/* 212:    */   
/* 213:    */   public void setNumThreads(int nT)
/* 214:    */   {
/* 215:464 */     this.m_numThreads = nT;
/* 216:    */   }
/* 217:    */   
/* 218:    */   public String poolSizeTipText()
/* 219:    */   {
/* 220:472 */     return "The size of the thread pool, for example, the number of cores in the CPU.";
/* 221:    */   }
/* 222:    */   
/* 223:    */   public int getPoolSize()
/* 224:    */   {
/* 225:480 */     return this.m_poolSize;
/* 226:    */   }
/* 227:    */   
/* 228:    */   public void setPoolSize(int nT)
/* 229:    */   {
/* 230:488 */     this.m_poolSize = nT;
/* 231:    */   }
/* 232:    */   
/* 233:    */   public String lambdaTipText()
/* 234:    */   {
/* 235:499 */     return "The penalty parameter lambda.";
/* 236:    */   }
/* 237:    */   
/* 238:    */   public double getLambda()
/* 239:    */   {
/* 240:509 */     return this.m_lambda;
/* 241:    */   }
/* 242:    */   
/* 243:    */   public void setLambda(double v)
/* 244:    */   {
/* 245:519 */     this.m_lambda = v;
/* 246:    */   }
/* 247:    */   
/* 248:    */   public String kernelTipText()
/* 249:    */   {
/* 250:526 */     return "The kernel to use.";
/* 251:    */   }
/* 252:    */   
/* 253:    */   public void setKernel(Kernel value)
/* 254:    */   {
/* 255:533 */     this.m_kernel = value;
/* 256:    */   }
/* 257:    */   
/* 258:    */   public Kernel getKernel()
/* 259:    */   {
/* 260:540 */     return this.m_kernel;
/* 261:    */   }
/* 262:    */   
/* 263:    */   public String useCGDTipText()
/* 264:    */   {
/* 265:548 */     return "Whether to use conjugate gradient descent (potentially useful for many parameters).";
/* 266:    */   }
/* 267:    */   
/* 268:    */   public boolean getUseCGD()
/* 269:    */   {
/* 270:556 */     return this.m_useCGD;
/* 271:    */   }
/* 272:    */   
/* 273:    */   public void setUseCGD(boolean newUseCGD)
/* 274:    */   {
/* 275:564 */     this.m_useCGD = newUseCGD;
/* 276:    */   }
/* 277:    */   
/* 278:    */   protected double calculateLoss()
/* 279:    */     throws Exception
/* 280:    */   {
/* 281:573 */     final int N = this.m_classValues.length;
/* 282:574 */     int chunksize = N / this.m_numThreads;
/* 283:575 */     Set<Future<Double>> results = new HashSet();
/* 284:578 */     for (int k = 0; k < this.m_numThreads; k++)
/* 285:    */     {
/* 286:581 */       final int lo = k * chunksize;
/* 287:582 */       final int hi = k < this.m_numThreads - 1 ? lo + chunksize : N;
/* 288:    */       
/* 289:    */ 
/* 290:585 */       Future<Double> futureLoss = this.m_Pool.submit(new Callable()
/* 291:    */       {
/* 292:    */         public Double call()
/* 293:    */           throws Exception
/* 294:    */         {
/* 295:588 */           double loss = 0.0D;
/* 296:589 */           for (int i = lo; i < hi; i++)
/* 297:    */           {
/* 298:592 */             double weightedSum = 0.0D;
/* 299:593 */             for (int j = 0; j < N; j++) {
/* 300:594 */               weightedSum += KernelLogisticRegression.this.m_weights[j] * KernelLogisticRegression.this.m_kernelMatrix[i][j];
/* 301:    */             }
/* 302:598 */             loss += KernelLogisticRegression.this.m_lambda * KernelLogisticRegression.this.m_weights[i] * weightedSum;
/* 303:    */             
/* 304:    */ 
/* 305:601 */             weightedSum += KernelLogisticRegression.this.m_weights[N];
/* 306:    */             
/* 307:    */ 
/* 308:    */ 
/* 309:    */ 
/* 310:606 */             loss += Math.log(1.0D + Math.exp(-KernelLogisticRegression.this.m_classValues[i] * weightedSum));
/* 311:    */           }
/* 312:608 */           return Double.valueOf(loss);
/* 313:    */         }
/* 314:610 */       });
/* 315:611 */       results.add(futureLoss);
/* 316:    */     }
/* 317:615 */     double loss = 0.0D;
/* 318:    */     try
/* 319:    */     {
/* 320:617 */       for (Future<Double> futureLoss : results) {
/* 321:618 */         loss += ((Double)futureLoss.get()).doubleValue();
/* 322:    */       }
/* 323:    */     }
/* 324:    */     catch (Exception e)
/* 325:    */     {
/* 326:621 */       System.out.println("Loss could not be calculated.");
/* 327:622 */       e.printStackTrace();
/* 328:    */     }
/* 329:624 */     return loss;
/* 330:    */   }
/* 331:    */   
/* 332:    */   protected double[] calculateGradient()
/* 333:    */     throws Exception
/* 334:    */   {
/* 335:633 */     final int N = this.m_classValues.length;
/* 336:634 */     int chunksize = N / this.m_numThreads;
/* 337:635 */     Set<Future<double[]>> results = new HashSet();
/* 338:638 */     for (int k = 0; k < this.m_numThreads; k++)
/* 339:    */     {
/* 340:641 */       final int lo = k * chunksize;
/* 341:642 */       final int hi = k < this.m_numThreads - 1 ? lo + chunksize : N;
/* 342:    */       
/* 343:    */ 
/* 344:645 */       Future<double[]> futureGrad = this.m_Pool.submit(new Callable()
/* 345:    */       {
/* 346:    */         public double[] call()
/* 347:    */           throws Exception
/* 348:    */         {
/* 349:650 */           double[] grad = new double[N + 1];
/* 350:651 */           for (int i = lo; i < hi; i++)
/* 351:    */           {
/* 352:654 */             double weightedSum = 0.0D;
/* 353:655 */             for (int j = 0; j < N; j++) {
/* 354:656 */               weightedSum += KernelLogisticRegression.this.m_weights[j] * KernelLogisticRegression.this.m_kernelMatrix[i][j];
/* 355:    */             }
/* 356:660 */             grad[i] += 2.0D * KernelLogisticRegression.this.m_lambda * weightedSum;
/* 357:    */             
/* 358:    */ 
/* 359:663 */             weightedSum += KernelLogisticRegression.this.m_weights[N];
/* 360:    */             
/* 361:    */ 
/* 362:666 */             double multiplier = -KernelLogisticRegression.this.m_classValues[i] * (1.0D / (1.0D + Math.exp(KernelLogisticRegression.this.m_classValues[i] * weightedSum)));
/* 363:668 */             for (int j = 0; j < N; j++) {
/* 364:669 */               grad[j] += multiplier * KernelLogisticRegression.this.m_kernelMatrix[i][j];
/* 365:    */             }
/* 366:673 */             grad[N] += multiplier;
/* 367:    */           }
/* 368:675 */           return grad;
/* 369:    */         }
/* 370:677 */       });
/* 371:678 */       results.add(futureGrad);
/* 372:    */     }
/* 373:682 */     double[] grad = new double[N + 1];
/* 374:    */     try
/* 375:    */     {
/* 376:684 */       for (Future<double[]> futureGrad : results)
/* 377:    */       {
/* 378:685 */         double[] lg = (double[])futureGrad.get();
/* 379:686 */         for (int i = 0; i < lg.length; i++) {
/* 380:687 */           grad[i] += lg[i];
/* 381:    */         }
/* 382:    */       }
/* 383:    */     }
/* 384:    */     catch (Exception e)
/* 385:    */     {
/* 386:691 */       System.out.println("Gradient could not be calculated.");
/* 387:    */     }
/* 388:694 */     return grad;
/* 389:    */   }
/* 390:    */   
/* 391:    */   protected class OptEng
/* 392:    */     extends Optimization
/* 393:    */   {
/* 394:    */     protected OptEng() {}
/* 395:    */     
/* 396:    */     protected double objectiveFunction(double[] x)
/* 397:    */       throws Exception
/* 398:    */     {
/* 399:709 */       KernelLogisticRegression.this.m_weights = x;
/* 400:710 */       return KernelLogisticRegression.this.calculateLoss();
/* 401:    */     }
/* 402:    */     
/* 403:    */     protected double[] evaluateGradient(double[] x)
/* 404:    */       throws Exception
/* 405:    */     {
/* 406:719 */       KernelLogisticRegression.this.m_weights = x;
/* 407:720 */       return KernelLogisticRegression.this.calculateGradient();
/* 408:    */     }
/* 409:    */     
/* 410:    */     public String getRevision()
/* 411:    */     {
/* 412:728 */       return RevisionUtils.extract("$Revision: 1345 $");
/* 413:    */     }
/* 414:    */   }
/* 415:    */   
/* 416:    */   protected class OptEngCGD
/* 417:    */     extends ConjugateGradientOptimization
/* 418:    */   {
/* 419:    */     protected OptEngCGD() {}
/* 420:    */     
/* 421:    */     protected double objectiveFunction(double[] x)
/* 422:    */       throws Exception
/* 423:    */     {
/* 424:744 */       KernelLogisticRegression.this.m_weights = x;
/* 425:745 */       return KernelLogisticRegression.this.calculateLoss();
/* 426:    */     }
/* 427:    */     
/* 428:    */     protected double[] evaluateGradient(double[] x)
/* 429:    */       throws Exception
/* 430:    */     {
/* 431:754 */       KernelLogisticRegression.this.m_weights = x;
/* 432:755 */       return KernelLogisticRegression.this.calculateGradient();
/* 433:    */     }
/* 434:    */     
/* 435:    */     public String getRevision()
/* 436:    */     {
/* 437:763 */       return RevisionUtils.extract("$Revision: 9345 $");
/* 438:    */     }
/* 439:    */   }
/* 440:    */   
/* 441:    */   public void buildClassifier(Instances data)
/* 442:    */     throws Exception
/* 443:    */   {
/* 444:774 */     getCapabilities().testWithFail(data);
/* 445:    */     
/* 446:    */ 
/* 447:777 */     this.m_data = new Instances(data);
/* 448:    */     
/* 449:    */ 
/* 450:780 */     this.m_data.deleteWithMissingClass();
/* 451:    */     
/* 452:    */ 
/* 453:783 */     Random rand = this.m_data.getRandomNumberGenerator(getSeed());
/* 454:    */     
/* 455:    */ 
/* 456:786 */     this.m_data.randomize(rand);
/* 457:    */     
/* 458:    */ 
/* 459:789 */     this.m_replaceMissing = new ReplaceMissingValues();
/* 460:790 */     this.m_replaceMissing.setInputFormat(this.m_data);
/* 461:791 */     this.m_data = Filter.useFilter(this.m_data, this.m_replaceMissing);
/* 462:792 */     this.m_removeUseless = new RemoveUseless();
/* 463:793 */     this.m_removeUseless.setInputFormat(this.m_data);
/* 464:794 */     this.m_data = Filter.useFilter(this.m_data, this.m_removeUseless);
/* 465:795 */     this.m_nominalToBinary = new NominalToBinary();
/* 466:796 */     this.m_nominalToBinary.setInputFormat(this.m_data);
/* 467:797 */     this.m_data = Filter.useFilter(this.m_data, this.m_nominalToBinary);
/* 468:798 */     this.m_standardize = new Standardize();
/* 469:799 */     this.m_standardize.setInputFormat(this.m_data);
/* 470:800 */     this.m_data = Filter.useFilter(this.m_data, this.m_standardize);
/* 471:803 */     if ((this.m_kernel instanceof CachedKernel))
/* 472:    */     {
/* 473:804 */       CachedKernel cachedKernel = (CachedKernel)this.m_kernel;
/* 474:805 */       cachedKernel.setCacheSize(-1);
/* 475:    */     }
/* 476:807 */     this.m_kernel.buildKernel(this.m_data);
/* 477:    */     
/* 478:    */ 
/* 479:    */ 
/* 480:811 */     this.m_kernelMatrix = new double[this.m_data.numInstances()][this.m_data.numInstances()];
/* 481:812 */     this.m_classValues = new double[this.m_data.numInstances()];
/* 482:    */     
/* 483:    */ 
/* 484:815 */     this.m_Pool = Executors.newFixedThreadPool(this.m_poolSize);
/* 485:    */     
/* 486:    */ 
/* 487:818 */     int N = this.m_classValues.length;
/* 488:819 */     int chunksize = N / this.m_numThreads;
/* 489:820 */     Set<Future<Void>> results = new HashSet();
/* 490:823 */     for (int k = 0; k < this.m_numThreads; k++)
/* 491:    */     {
/* 492:826 */       final int lo = k * chunksize;
/* 493:827 */       final int hi = k < this.m_numThreads - 1 ? lo + chunksize : N;
/* 494:    */       
/* 495:    */ 
/* 496:830 */       Future<Void> futureMat = this.m_Pool.submit(new Callable()
/* 497:    */       {
/* 498:    */         public Void call()
/* 499:    */           throws Exception
/* 500:    */         {
/* 501:833 */           for (int i = lo; i < hi; i++)
/* 502:    */           {
/* 503:834 */             for (int j = 0; j < KernelLogisticRegression.this.m_data.numInstances(); j++) {
/* 504:835 */               if ((j >= i) || (KernelLogisticRegression.this.m_numThreads > 1)) {
/* 505:836 */                 KernelLogisticRegression.this.m_kernelMatrix[i][j] = KernelLogisticRegression.this.m_kernel.eval(-1, i, KernelLogisticRegression.this.m_data.instance(j));
/* 506:    */               } else {
/* 507:838 */                 KernelLogisticRegression.this.m_kernelMatrix[i][j] = KernelLogisticRegression.this.m_kernelMatrix[j][i];
/* 508:    */               }
/* 509:    */             }
/* 510:841 */             KernelLogisticRegression.this.m_classValues[i] = (2.0D * KernelLogisticRegression.this.m_data.instance(i).classValue() - 1.0D);
/* 511:    */           }
/* 512:843 */           return null;
/* 513:    */         }
/* 514:845 */       });
/* 515:846 */       results.add(futureMat);
/* 516:    */     }
/* 517:    */     try
/* 518:    */     {
/* 519:850 */       for (Future<Void> futureMat : results) {
/* 520:851 */         futureMat.get();
/* 521:    */       }
/* 522:    */     }
/* 523:    */     catch (Exception e)
/* 524:    */     {
/* 525:854 */       System.out.println("Kernel matrix could not be calculated.");
/* 526:    */     }
/* 527:858 */     this.m_weights = new double[this.m_data.numInstances() + 1];
/* 528:859 */     double initializer = 1.0D / this.m_data.numInstances();
/* 529:860 */     for (int i = 0; i < this.m_data.numInstances(); i++) {
/* 530:861 */       this.m_weights[i] = (this.m_data.instance(i).classValue() == 0.0D ? -initializer : initializer);
/* 531:    */     }
/* 532:866 */     double[] Nc = new double[2];
/* 533:867 */     for (int i = 0; i < this.m_data.numInstances(); i++) {
/* 534:868 */       Nc[((int)this.m_data.instance(i).classValue())] += 1.0D;
/* 535:    */     }
/* 536:870 */     this.m_weights[this.m_data.numInstances()] = (Math.log(Nc[1] + 1.0D) - Math.log(Nc[0] + 1.0D));
/* 537:    */     
/* 538:    */ 
/* 539:    */ 
/* 540:874 */     double[][] b = new double[2][this.m_weights.length];
/* 541:875 */     for (int p = 0; p < this.m_weights.length; p++)
/* 542:    */     {
/* 543:876 */       b[0][p] = (0.0D / 0.0D);
/* 544:877 */       b[1][p] = (0.0D / 0.0D);
/* 545:    */     }
/* 546:881 */     Optimization opt = null;
/* 547:882 */     if (this.m_useCGD) {
/* 548:883 */       opt = new OptEngCGD();
/* 549:    */     } else {
/* 550:885 */       opt = new OptEng();
/* 551:    */     }
/* 552:887 */     opt.setDebug(this.m_Debug);
/* 553:    */     
/* 554:889 */     this.m_weights = opt.findArgmin(this.m_weights, b);
/* 555:890 */     while (this.m_weights == null)
/* 556:    */     {
/* 557:891 */       this.m_weights = opt.getVarbValues();
/* 558:892 */       if (this.m_Debug) {
/* 559:893 */         System.out.println("First set of iterations finished, not enough!");
/* 560:    */       }
/* 561:895 */       this.m_weights = opt.findArgmin(this.m_weights, b);
/* 562:    */     }
/* 563:899 */     this.m_kernelMatrix = ((double[][])null);
/* 564:    */     
/* 565:    */ 
/* 566:902 */     this.m_Pool.shutdown();
/* 567:906 */     if ((this.m_kernel instanceof CachedKernel))
/* 568:    */     {
/* 569:907 */       this.m_kernel = Kernel.makeCopy(this.m_kernel);
/* 570:908 */       ((CachedKernel)this.m_kernel).setCacheSize(-1);
/* 571:909 */       this.m_kernel.buildKernel(this.m_data);
/* 572:    */     }
/* 573:    */   }
/* 574:    */   
/* 575:    */   public double[] distributionForInstance(Instance inst)
/* 576:    */     throws Exception
/* 577:    */   {
/* 578:920 */     this.m_replaceMissing.input(inst);
/* 579:921 */     inst = this.m_replaceMissing.output();
/* 580:922 */     this.m_removeUseless.input(inst);
/* 581:923 */     inst = this.m_removeUseless.output();
/* 582:924 */     this.m_nominalToBinary.input(inst);
/* 583:925 */     inst = this.m_nominalToBinary.output();
/* 584:926 */     this.m_standardize.input(inst);
/* 585:927 */     inst = this.m_standardize.output();
/* 586:    */     
/* 587:    */ 
/* 588:930 */     double weightedSum = this.m_weights[this.m_data.numInstances()];
/* 589:931 */     for (int j = 0; j < this.m_data.numInstances(); j++) {
/* 590:932 */       weightedSum += this.m_weights[j] * this.m_kernel.eval(-1, j, inst);
/* 591:    */     }
/* 592:934 */     double[] dist = new double[2];
/* 593:935 */     dist[1] = (1.0D / (1.0D + Math.exp(-weightedSum)));
/* 594:936 */     dist[0] = (1.0D - dist[1]);
/* 595:937 */     return dist;
/* 596:    */   }
/* 597:    */   
/* 598:    */   public String toString()
/* 599:    */   {
/* 600:946 */     if (this.m_data == null) {
/* 601:947 */       return "Classifier not built yet.";
/* 602:    */     }
/* 603:949 */     String s = "\nlog(p / (1 - p))\t=\n";
/* 604:950 */     for (int i = 0; i < this.m_data.numInstances(); i++)
/* 605:    */     {
/* 606:951 */       String name = "(standardized) X" + (i + 1);
/* 607:952 */       if (i > 0) {
/* 608:953 */         s = s + "\t+  ";
/* 609:    */       } else {
/* 610:955 */         s = s + "\t   ";
/* 611:    */       }
/* 612:957 */       s = s + Utils.doubleToString(this.m_weights[i], 4) + "   \t* " + name + "\n";
/* 613:    */     }
/* 614:959 */     s = s + "\t+  " + Utils.doubleToString(this.m_weights[this.m_data.numInstances()], 4) + "\n";
/* 615:    */     
/* 616:961 */     return s;
/* 617:    */   }
/* 618:    */   
/* 619:    */   public String getRevision()
/* 620:    */   {
/* 621:969 */     return RevisionUtils.extract("$Revision: ???? $");
/* 622:    */   }
/* 623:    */   
/* 624:    */   public static void main(String[] args)
/* 625:    */   {
/* 626:976 */     runClassifier(new KernelLogisticRegression(), args);
/* 627:    */   }
/* 628:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.KernelLogisticRegression
 * JD-Core Version:    0.7.0.1
 */