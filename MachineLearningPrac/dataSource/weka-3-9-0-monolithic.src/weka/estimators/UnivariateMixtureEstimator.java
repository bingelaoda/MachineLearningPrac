/*    1:     */ package weka.estimators;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.io.Serializable;
/*    5:     */ import java.util.ArrayList;
/*    6:     */ import java.util.Arrays;
/*    7:     */ import java.util.Enumeration;
/*    8:     */ import java.util.Random;
/*    9:     */ import java.util.Vector;
/*   10:     */ import weka.core.ContingencyTables;
/*   11:     */ import weka.core.Option;
/*   12:     */ import weka.core.OptionHandler;
/*   13:     */ import weka.core.RevisionUtils;
/*   14:     */ import weka.core.Statistics;
/*   15:     */ import weka.core.Utils;
/*   16:     */ 
/*   17:     */ public class UnivariateMixtureEstimator
/*   18:     */   implements UnivariateDensityEstimator, UnivariateIntervalEstimator, UnivariateQuantileEstimator, OptionHandler, Serializable
/*   19:     */ {
/*   20:  52 */   private static double m_normConst = Math.log(Math.sqrt(6.283185307179586D));
/*   21:     */   private static final long serialVersionUID = -2035274930137353656L;
/*   22:     */   
/*   23:     */   public class MM
/*   24:     */   {
/*   25:  60 */     protected double[] m_Means = null;
/*   26:  63 */     protected double[] m_StdDevs = null;
/*   27:  66 */     protected double[] m_LogPriors = null;
/*   28:     */     protected int m_K;
/*   29:     */     
/*   30:     */     public MM() {}
/*   31:     */     
/*   32:     */     public String toString()
/*   33:     */     {
/*   34:  76 */       StringBuffer sb = new StringBuffer();
/*   35:  77 */       sb.append("Mixture model estimator\n\n");
/*   36:  78 */       for (int i = 0; i < this.m_LogPriors.length; i++) {
/*   37:  79 */         sb.append("Mean: " + this.m_Means[i] + "\tStd. dev.: " + this.m_StdDevs[i] + "\tPrior prob.: " + Math.exp(this.m_LogPriors[i]) + "\n");
/*   38:     */       }
/*   39:  83 */       return sb.toString();
/*   40:     */     }
/*   41:     */     
/*   42:     */     protected double smallestDistance(double val)
/*   43:     */     {
/*   44:  92 */       double min = Math.abs(val - this.m_Means[0]);
/*   45:  93 */       for (int i = 1; i < this.m_K; i++) {
/*   46:  94 */         if (Math.abs(val - this.m_Means[i]) < min) {
/*   47:  95 */           min = Math.abs(val - this.m_Means[i]);
/*   48:     */         }
/*   49:     */       }
/*   50:  98 */       return min;
/*   51:     */     }
/*   52:     */     
/*   53:     */     protected int nearestMean(double val)
/*   54:     */     {
/*   55: 107 */       double min = Math.abs(val - this.m_Means[0]);
/*   56: 108 */       int index = 0;
/*   57: 109 */       for (int i = 1; i < this.m_K; i++) {
/*   58: 110 */         if (Math.abs(val - this.m_Means[i]) < min)
/*   59:     */         {
/*   60: 111 */           min = Math.abs(val - this.m_Means[i]);
/*   61: 112 */           index = i;
/*   62:     */         }
/*   63:     */       }
/*   64: 115 */       return index;
/*   65:     */     }
/*   66:     */     
/*   67:     */     public void initializeModel(int K, double[] values, double[] weights, Random r)
/*   68:     */     {
/*   69: 125 */       this.m_Means = new double[K];
/*   70:     */       
/*   71:     */ 
/*   72: 128 */       double furthestVal = values[r.nextInt(values.length)];
/*   73:     */       
/*   74:     */ 
/*   75: 131 */       this.m_K = 0;
/*   76:     */       for (;;)
/*   77:     */       {
/*   78: 133 */         this.m_Means[this.m_K] = furthestVal;
/*   79: 134 */         this.m_K += 1;
/*   80: 135 */         if (this.m_K >= K) {
/*   81:     */           break;
/*   82:     */         }
/*   83: 138 */         double maxMinDist = smallestDistance(values[0]);
/*   84: 139 */         furthestVal = values[0];
/*   85: 140 */         for (int i = 1; i < values.length; i++)
/*   86:     */         {
/*   87: 141 */           double minDist = smallestDistance(values[i]);
/*   88: 142 */           if (minDist > maxMinDist)
/*   89:     */           {
/*   90: 143 */             maxMinDist = minDist;
/*   91: 144 */             furthestVal = values[i];
/*   92:     */           }
/*   93:     */         }
/*   94: 147 */         if (maxMinDist <= 0.0D) {
/*   95:     */           break;
/*   96:     */         }
/*   97:     */       }
/*   98: 153 */       if (this.m_K < K)
/*   99:     */       {
/*  100: 154 */         double[] tempMeans = new double[this.m_K];
/*  101: 155 */         System.arraycopy(this.m_Means, 0, tempMeans, 0, this.m_K);
/*  102: 156 */         this.m_Means = tempMeans;
/*  103:     */       }
/*  104: 160 */       double[][] probs = new double[this.m_K][values.length];
/*  105: 161 */       for (int i = 0; i < values.length; i++) {
/*  106: 162 */         probs[nearestMean(values[i])][i] = 1.0D;
/*  107:     */       }
/*  108: 166 */       this.m_StdDevs = new double[this.m_K];
/*  109: 167 */       this.m_LogPriors = new double[this.m_K];
/*  110: 168 */       estimateParameters(values, weights, probs);
/*  111:     */     }
/*  112:     */     
/*  113:     */     protected void estimateParameters(double[] values, double[] weights, double[][] probs)
/*  114:     */     {
/*  115: 176 */       double totalSumOfWeights = 0.0D;
/*  116: 177 */       for (int j = 0; j < this.m_K; j++)
/*  117:     */       {
/*  118: 178 */         double sum = 0.0D;
/*  119: 179 */         double sumWeights = 0.0D;
/*  120: 180 */         for (int i = 0; i < values.length; i++)
/*  121:     */         {
/*  122: 181 */           double weight = probs[j][i] * weights[i];
/*  123: 182 */           sum += weight * values[i];
/*  124: 183 */           sumWeights += weight;
/*  125:     */         }
/*  126: 185 */         if (sumWeights <= 0.0D) {
/*  127: 186 */           this.m_Means[j] = 0.0D;
/*  128:     */         } else {
/*  129: 188 */           this.m_Means[j] = (sum / sumWeights);
/*  130:     */         }
/*  131: 190 */         totalSumOfWeights += sumWeights;
/*  132:     */       }
/*  133: 193 */       for (int j = 0; j < this.m_K; j++)
/*  134:     */       {
/*  135: 194 */         double sum = 0.0D;
/*  136: 195 */         double sumWeights = 0.0D;
/*  137: 196 */         for (int i = 0; i < values.length; i++)
/*  138:     */         {
/*  139: 197 */           double weight = probs[j][i] * weights[i];
/*  140: 198 */           double diff = values[i] - this.m_Means[j];
/*  141: 199 */           sum += weight * diff * diff;
/*  142: 200 */           sumWeights += weight;
/*  143:     */         }
/*  144: 202 */         if ((sum <= 0.0D) || (sumWeights <= 0.0D))
/*  145:     */         {
/*  146: 203 */           this.m_StdDevs[j] = 1.0E-006D;
/*  147:     */         }
/*  148:     */         else
/*  149:     */         {
/*  150: 205 */           this.m_StdDevs[j] = Math.sqrt(sum / sumWeights);
/*  151: 206 */           if (this.m_StdDevs[j] < 1.0E-006D) {
/*  152: 207 */             this.m_StdDevs[j] = 1.0E-006D;
/*  153:     */           }
/*  154:     */         }
/*  155: 210 */         if (sumWeights <= 0.0D) {
/*  156: 211 */           this.m_LogPriors[j] = -1.797693134862316E+308D;
/*  157:     */         } else {
/*  158: 213 */           this.m_LogPriors[j] = Math.log(sumWeights / totalSumOfWeights);
/*  159:     */         }
/*  160:     */       }
/*  161:     */     }
/*  162:     */     
/*  163:     */     public double loglikelihood(double[] values, double[] weights)
/*  164:     */     {
/*  165: 223 */       double sum = 0.0D;
/*  166: 224 */       double sumOfWeights = 0.0D;
/*  167: 225 */       for (int i = 0; i < values.length; i++)
/*  168:     */       {
/*  169: 226 */         sum += weights[i] * logDensity(values[i]);
/*  170: 227 */         sumOfWeights += weights[i];
/*  171:     */       }
/*  172: 229 */       return sum / sumOfWeights;
/*  173:     */     }
/*  174:     */     
/*  175:     */     public double MSE()
/*  176:     */     {
/*  177: 237 */       double mse = 0.0D;
/*  178: 238 */       for (int i = 0; i < this.m_K; i++) {
/*  179: 239 */         mse += this.m_StdDevs[i] * this.m_StdDevs[i] * Math.exp(this.m_LogPriors[i]);
/*  180:     */       }
/*  181: 241 */       return mse;
/*  182:     */     }
/*  183:     */     
/*  184:     */     protected double logNormalDens(double x, double mean, double stdDev)
/*  185:     */     {
/*  186: 249 */       double diff = x - mean;
/*  187: 250 */       return -(diff * diff / (2.0D * stdDev * stdDev)) - UnivariateMixtureEstimator.m_normConst - Math.log(stdDev);
/*  188:     */     }
/*  189:     */     
/*  190:     */     protected double[] logJointDensities(double value)
/*  191:     */     {
/*  192: 258 */       double[] a = new double[this.m_K];
/*  193: 259 */       for (int i = 0; i < this.m_K; i++) {
/*  194: 260 */         a[i] = (this.m_LogPriors[i] + logNormalDens(value, this.m_Means[i], this.m_StdDevs[i]));
/*  195:     */       }
/*  196: 262 */       return a;
/*  197:     */     }
/*  198:     */     
/*  199:     */     public double logDensity(double value)
/*  200:     */     {
/*  201: 270 */       double[] a = logJointDensities(value);
/*  202: 271 */       double max = a[Utils.maxIndex(a)];
/*  203: 272 */       double sum = 0.0D;
/*  204: 273 */       for (int i = 0; i < a.length; i++) {
/*  205: 274 */         sum += Math.exp(a[i] - max);
/*  206:     */       }
/*  207: 277 */       return max + Math.log(sum);
/*  208:     */     }
/*  209:     */     
/*  210:     */     public double[][] predictIntervals(double conf)
/*  211:     */     {
/*  212: 289 */       double val = Statistics.normalInverse(1.0D - (1.0D - conf) / 2.0D);
/*  213: 290 */       double min = 1.7976931348623157E+308D;
/*  214: 291 */       double max = -1.797693134862316E+308D;
/*  215: 292 */       for (int i = 0; i < this.m_Means.length; i++)
/*  216:     */       {
/*  217: 293 */         double l = this.m_Means[i] - val * this.m_StdDevs[i];
/*  218: 294 */         if (l < min) {
/*  219: 295 */           min = l;
/*  220:     */         }
/*  221: 297 */         double r = this.m_Means[i] + val * this.m_StdDevs[i];
/*  222: 298 */         if (r > max) {
/*  223: 299 */           max = r;
/*  224:     */         }
/*  225:     */       }
/*  226: 302 */       double delta = (max - min) / UnivariateMixtureEstimator.this.m_NumIntervals;
/*  227:     */       
/*  228:     */ 
/*  229: 305 */       double[] probabilities = new double[UnivariateMixtureEstimator.this.m_NumIntervals];
/*  230: 306 */       double leftVal = Math.exp(logDensity(min));
/*  231: 307 */       for (int i = 0; i < UnivariateMixtureEstimator.this.m_NumIntervals; i++)
/*  232:     */       {
/*  233: 308 */         double rightVal = Math.exp(logDensity(min + (i + 1) * delta));
/*  234: 309 */         probabilities[i] = (0.5D * (leftVal + rightVal) * delta);
/*  235: 310 */         leftVal = rightVal;
/*  236:     */       }
/*  237: 314 */       int[] sortedIndices = Utils.sort(probabilities);
/*  238:     */       
/*  239:     */ 
/*  240: 317 */       double sum = 0.0D;
/*  241: 318 */       boolean[] toUse = new boolean[probabilities.length];
/*  242: 319 */       int k = 0;
/*  243: 320 */       while ((sum < conf) && (k < toUse.length))
/*  244:     */       {
/*  245: 321 */         toUse[sortedIndices[(toUse.length - (k + 1))]] = true;
/*  246: 322 */         sum += probabilities[sortedIndices[(toUse.length - (k + 1))]];
/*  247: 323 */         k++;
/*  248:     */       }
/*  249: 327 */       probabilities = null;
/*  250:     */       
/*  251:     */ 
/*  252: 330 */       ArrayList<double[]> intervals = new ArrayList();
/*  253:     */       
/*  254:     */ 
/*  255: 333 */       double[] interval = null;
/*  256:     */       
/*  257:     */ 
/*  258: 336 */       boolean haveStartedInterval = false;
/*  259: 337 */       for (int i = 0; i < UnivariateMixtureEstimator.this.m_NumIntervals; i++) {
/*  260: 340 */         if (toUse[i] != 0)
/*  261:     */         {
/*  262: 343 */           if (!haveStartedInterval)
/*  263:     */           {
/*  264: 344 */             haveStartedInterval = true;
/*  265: 345 */             interval = new double[2];
/*  266: 346 */             interval[0] = (min + i * delta);
/*  267:     */           }
/*  268: 350 */           interval[1] = (min + (i + 1) * delta);
/*  269:     */         }
/*  270: 355 */         else if (haveStartedInterval)
/*  271:     */         {
/*  272: 356 */           haveStartedInterval = false;
/*  273: 357 */           intervals.add(interval);
/*  274:     */         }
/*  275:     */       }
/*  276: 363 */       if (haveStartedInterval) {
/*  277: 364 */         intervals.add(interval);
/*  278:     */       }
/*  279: 367 */       return (double[][])intervals.toArray(new double[0][0]);
/*  280:     */     }
/*  281:     */     
/*  282:     */     public double predictQuantile(double percentage)
/*  283:     */     {
/*  284: 379 */       double valRight = Statistics.normalInverse(percentage);
/*  285: 380 */       double valLeft = Statistics.normalInverse(0.001D);
/*  286: 381 */       double min = 1.7976931348623157E+308D;
/*  287: 382 */       double max = -1.797693134862316E+308D;
/*  288: 383 */       for (int i = 0; i < this.m_Means.length; i++)
/*  289:     */       {
/*  290: 384 */         double l = this.m_Means[i] - valLeft * this.m_StdDevs[i];
/*  291: 385 */         if (l < min) {
/*  292: 386 */           min = l;
/*  293:     */         }
/*  294: 388 */         double r = this.m_Means[i] + valRight * this.m_StdDevs[i];
/*  295: 389 */         if (r > max) {
/*  296: 390 */           max = r;
/*  297:     */         }
/*  298:     */       }
/*  299: 393 */       double delta = (max - min) / UnivariateMixtureEstimator.this.m_NumIntervals;
/*  300:     */       
/*  301: 395 */       double sum = 0.0D;
/*  302: 396 */       double leftVal = Math.exp(logDensity(min));
/*  303: 397 */       for (int i = 0; i < UnivariateMixtureEstimator.this.m_NumIntervals; i++)
/*  304:     */       {
/*  305: 398 */         if (sum >= percentage) {
/*  306: 399 */           return min + i * delta;
/*  307:     */         }
/*  308: 401 */         double rightVal = Math.exp(logDensity(min + (i + 1) * delta));
/*  309: 402 */         sum += 0.5D * (leftVal + rightVal) * delta;
/*  310: 403 */         leftVal = rightVal;
/*  311:     */       }
/*  312: 405 */       return max;
/*  313:     */     }
/*  314:     */   }
/*  315:     */   
/*  316: 413 */   protected double[] m_Values = new double[1000];
/*  317: 416 */   protected double[] m_Weights = new double[1000];
/*  318:     */   protected int m_NumValues;
/*  319:     */   protected MM m_MixtureModel;
/*  320: 425 */   protected int m_NumComponents = -1;
/*  321: 428 */   protected int m_MaxNumComponents = 5;
/*  322: 431 */   protected int m_Seed = 1;
/*  323: 434 */   protected int m_NumBootstrapRuns = 10;
/*  324: 437 */   protected int m_NumIntervals = 1000;
/*  325: 440 */   protected boolean m_UseNormalizedEntropy = false;
/*  326: 443 */   protected boolean m_Debug = false;
/*  327: 446 */   protected Random m_Random = new Random(this.m_Seed);
/*  328:     */   
/*  329:     */   public String globalInfo()
/*  330:     */   {
/*  331: 452 */     return "Estimates a univariate mixture model.";
/*  332:     */   }
/*  333:     */   
/*  334:     */   public boolean getUseNormalizedEntropy()
/*  335:     */   {
/*  336: 459 */     return this.m_UseNormalizedEntropy;
/*  337:     */   }
/*  338:     */   
/*  339:     */   public void setUseNormalizedEntropy(boolean useNormalizedEntropy)
/*  340:     */   {
/*  341: 466 */     this.m_UseNormalizedEntropy = useNormalizedEntropy;
/*  342:     */   }
/*  343:     */   
/*  344:     */   public String numBootstrapRunsToolTipText()
/*  345:     */   {
/*  346: 473 */     return "The number of Bootstrap runs to choose the number of components.";
/*  347:     */   }
/*  348:     */   
/*  349:     */   public int getNumBootstrapRuns()
/*  350:     */   {
/*  351: 482 */     return this.m_NumBootstrapRuns;
/*  352:     */   }
/*  353:     */   
/*  354:     */   public void setNumBootstrapRuns(int numBootstrapRuns)
/*  355:     */   {
/*  356: 491 */     this.m_NumBootstrapRuns = numBootstrapRuns;
/*  357:     */   }
/*  358:     */   
/*  359:     */   public String numComponentsToolTipText()
/*  360:     */   {
/*  361: 498 */     return "The number of mixture components to use.";
/*  362:     */   }
/*  363:     */   
/*  364:     */   public int getNumComponents()
/*  365:     */   {
/*  366: 507 */     return this.m_NumComponents;
/*  367:     */   }
/*  368:     */   
/*  369:     */   public void setNumComponents(int numComponents)
/*  370:     */   {
/*  371: 516 */     this.m_NumComponents = numComponents;
/*  372:     */   }
/*  373:     */   
/*  374:     */   public String seedTipText()
/*  375:     */   {
/*  376: 525 */     return "The random number seed to be used.";
/*  377:     */   }
/*  378:     */   
/*  379:     */   public void setSeed(int seed)
/*  380:     */   {
/*  381: 535 */     this.m_Seed = seed;
/*  382: 536 */     this.m_Random = new Random(seed);
/*  383:     */   }
/*  384:     */   
/*  385:     */   public int getSeed()
/*  386:     */   {
/*  387: 546 */     return this.m_Seed;
/*  388:     */   }
/*  389:     */   
/*  390:     */   public String maxNumComponentsToolTipText()
/*  391:     */   {
/*  392: 552 */     return "The maximum number of mixture components to use.";
/*  393:     */   }
/*  394:     */   
/*  395:     */   public int getMaxNumComponents()
/*  396:     */   {
/*  397: 561 */     return this.m_MaxNumComponents;
/*  398:     */   }
/*  399:     */   
/*  400:     */   public void setMaxNumComponents(int maxNumComponents)
/*  401:     */   {
/*  402: 570 */     this.m_MaxNumComponents = maxNumComponents;
/*  403:     */   }
/*  404:     */   
/*  405:     */   public void addValue(double value, double weight)
/*  406:     */   {
/*  407: 582 */     if (!Utils.eq(weight, 0.0D))
/*  408:     */     {
/*  409: 585 */       this.m_MixtureModel = null;
/*  410: 588 */       if (this.m_NumValues == this.m_Values.length)
/*  411:     */       {
/*  412: 589 */         double[] newWeights = new double[2 * this.m_NumValues];
/*  413: 590 */         double[] newValues = new double[2 * this.m_NumValues];
/*  414: 591 */         System.arraycopy(this.m_Values, 0, newValues, 0, this.m_NumValues);
/*  415: 592 */         System.arraycopy(this.m_Weights, 0, newWeights, 0, this.m_NumValues);
/*  416: 593 */         this.m_Values = newValues;
/*  417: 594 */         this.m_Weights = newWeights;
/*  418:     */       }
/*  419: 598 */       this.m_Values[this.m_NumValues] = value;
/*  420: 599 */       this.m_Weights[this.m_NumValues] = weight;
/*  421: 600 */       this.m_NumValues += 1;
/*  422:     */     }
/*  423:     */   }
/*  424:     */   
/*  425:     */   public MM buildModel(int K, double[] values, double[] weights)
/*  426:     */   {
/*  427: 611 */     MM model = null;
/*  428: 612 */     double bestMSE = 1.7976931348623157E+308D;
/*  429: 613 */     int numAttempts = 0;
/*  430: 614 */     while (numAttempts < 5)
/*  431:     */     {
/*  432: 617 */       void tmp28_25 = new UnivariateMixtureEstimator();tmp28_25.getClass();MM tempModel = new MM(tmp28_25);
/*  433: 618 */       tempModel.initializeModel(K, values, weights, this.m_Random);
/*  434:     */       
/*  435:     */ 
/*  436: 621 */       double oldMSE = 1.7976931348623157E+308D;
/*  437: 622 */       double MSE = tempModel.MSE();
/*  438: 624 */       if (this.m_Debug) {
/*  439: 625 */         System.err.println("MSE: " + MSE);
/*  440:     */       }
/*  441: 628 */       double[][] probs = new double[tempModel.m_K][values.length];
/*  442: 629 */       while (Utils.sm(MSE, oldMSE))
/*  443:     */       {
/*  444: 632 */         for (int j = 0; j < probs.length; j++) {
/*  445: 633 */           Arrays.fill(probs[j], 0.0D);
/*  446:     */         }
/*  447: 635 */         for (int i = 0; i < values.length; i++) {
/*  448: 636 */           probs[tempModel.nearestMean(values[i])][i] = 1.0D;
/*  449:     */         }
/*  450: 640 */         tempModel.estimateParameters(values, weights, probs);
/*  451:     */         
/*  452:     */ 
/*  453: 643 */         oldMSE = MSE;
/*  454: 644 */         MSE = tempModel.MSE();
/*  455: 646 */         if (this.m_Debug) {
/*  456: 647 */           System.err.println("MSE: " + MSE);
/*  457:     */         }
/*  458:     */       }
/*  459: 650 */       if (MSE < bestMSE)
/*  460:     */       {
/*  461: 651 */         bestMSE = MSE;
/*  462: 652 */         model = tempModel;
/*  463:     */       }
/*  464: 655 */       if (this.m_Debug) {
/*  465: 656 */         System.err.println("Best MSE: " + bestMSE);
/*  466:     */       }
/*  467: 659 */       numAttempts++;
/*  468:     */     }
/*  469: 663 */     double oldLogLikelihood = -1.797693134862316E+308D;
/*  470: 664 */     double loglikelihood = model.loglikelihood(values, weights);
/*  471: 665 */     double[][] probs = new double[model.m_K][values.length];
/*  472: 666 */     while (Utils.gr(loglikelihood, oldLogLikelihood))
/*  473:     */     {
/*  474: 669 */       for (int i = 0; i < values.length; i++)
/*  475:     */       {
/*  476: 670 */         double[] p = Utils.logs2probs(model.logJointDensities(values[i]));
/*  477: 671 */         for (int j = 0; j < p.length; j++) {
/*  478: 672 */           probs[j][i] = p[j];
/*  479:     */         }
/*  480:     */       }
/*  481: 677 */       model.estimateParameters(values, weights, probs);
/*  482:     */       
/*  483:     */ 
/*  484: 680 */       oldLogLikelihood = loglikelihood;
/*  485: 681 */       loglikelihood = model.loglikelihood(values, weights);
/*  486:     */     }
/*  487: 684 */     return model;
/*  488:     */   }
/*  489:     */   
/*  490:     */   public double[][] resampleWithWeights(Random random, boolean[] sampled)
/*  491:     */   {
/*  492: 695 */     double[] P = new double[this.m_Weights.length];
/*  493: 696 */     System.arraycopy(this.m_Weights, 0, P, 0, this.m_Weights.length);
/*  494: 697 */     Utils.normalize(P);
/*  495: 698 */     double[] Q = new double[this.m_Weights.length];
/*  496: 699 */     int[] A = new int[this.m_Weights.length];
/*  497: 700 */     int[] W = new int[this.m_Weights.length];
/*  498: 701 */     int M = this.m_Weights.length;
/*  499: 702 */     int NN = -1;
/*  500: 703 */     int NP = M;
/*  501: 704 */     for (int I = 0; I < M; I++)
/*  502:     */     {
/*  503: 705 */       if (P[I] < 0.0D) {
/*  504: 706 */         throw new IllegalArgumentException("Weights have to be positive.");
/*  505:     */       }
/*  506: 708 */       Q[I] = (M * P[I]);
/*  507: 709 */       if (Q[I] < 1.0D) {
/*  508: 710 */         W[(++NN)] = I;
/*  509:     */       } else {
/*  510: 712 */         W[(--NP)] = I;
/*  511:     */       }
/*  512:     */     }
/*  513: 715 */     if ((NN > -1) && (NP < M)) {
/*  514: 716 */       for (int S = 0; S < M - 1; S++)
/*  515:     */       {
/*  516: 717 */         int I = W[S];
/*  517: 718 */         int J = W[NP];
/*  518: 719 */         A[I] = J;
/*  519: 720 */         Q[J] += Q[I] - 1.0D;
/*  520: 721 */         if (Q[J] < 1.0D) {
/*  521: 722 */           NP++;
/*  522:     */         }
/*  523: 724 */         if (NP >= M) {
/*  524:     */           break;
/*  525:     */         }
/*  526:     */       }
/*  527:     */     }
/*  528: 731 */     for (int I = 0; I < M; I++) {
/*  529: 732 */       Q[I] += I;
/*  530:     */     }
/*  531: 736 */     int[] counts = new int[M];
/*  532:     */     
/*  533: 738 */     int count = 0;
/*  534: 739 */     for (int i = 0; i < this.m_Weights.length; i++)
/*  535:     */     {
/*  536: 741 */       double U = M * random.nextDouble();
/*  537: 742 */       int I = (int)U;
/*  538:     */       int ALRV;
/*  539:     */       int ALRV;
/*  540: 743 */       if (U < Q[I]) {
/*  541: 744 */         ALRV = I;
/*  542:     */       } else {
/*  543: 746 */         ALRV = A[I];
/*  544:     */       }
/*  545: 748 */       counts[ALRV] += 1;
/*  546: 749 */       if (sampled[ALRV] == 0)
/*  547:     */       {
/*  548: 750 */         sampled[ALRV] = true;
/*  549: 751 */         count++;
/*  550:     */       }
/*  551:     */     }
/*  552: 756 */     double[][] output = new double[2][count];
/*  553: 757 */     int index = 0;
/*  554: 758 */     for (int i = 0; i < M; i++) {
/*  555: 759 */       if (counts[i] > 0)
/*  556:     */       {
/*  557: 760 */         output[0][index] = this.m_Values[i];
/*  558: 761 */         output[1][index] = counts[i];
/*  559: 762 */         index++;
/*  560:     */       }
/*  561:     */     }
/*  562: 766 */     return output;
/*  563:     */   }
/*  564:     */   
/*  565:     */   protected int findNumComponentsUsingBootStrap()
/*  566:     */   {
/*  567: 776 */     if (this.m_NumComponents > 0) {
/*  568: 777 */       return this.m_NumComponents;
/*  569:     */     }
/*  570: 779 */     if (this.m_MaxNumComponents <= 1) {
/*  571: 780 */       return 1;
/*  572:     */     }
/*  573: 783 */     double bestLogLikelihood = -1.797693134862316E+308D;
/*  574: 784 */     int bestNumComponents = 1;
/*  575: 785 */     for (int i = 1; i <= this.m_MaxNumComponents; i++)
/*  576:     */     {
/*  577: 786 */       double logLikelihood = 0.0D;
/*  578: 787 */       for (int k = 0; k < this.m_NumBootstrapRuns; k++)
/*  579:     */       {
/*  580: 788 */         boolean[] inBag = new boolean[this.m_NumValues];
/*  581: 789 */         double[][] output = resampleWithWeights(this.m_Random, inBag);
/*  582: 790 */         MM mixtureModel = buildModel(i, output[0], output[1]);
/*  583: 791 */         double locLogLikelihood = 0.0D;
/*  584: 792 */         double totalWeight = 0.0D;
/*  585: 793 */         for (int j = 0; j < this.m_NumValues; j++) {
/*  586: 794 */           if (inBag[j] == 0)
/*  587:     */           {
/*  588: 795 */             double weight = this.m_Weights[j];
/*  589: 796 */             locLogLikelihood += weight * mixtureModel.logDensity(this.m_Values[j]);
/*  590: 797 */             totalWeight += weight;
/*  591:     */           }
/*  592:     */         }
/*  593: 800 */         locLogLikelihood /= totalWeight;
/*  594: 801 */         logLikelihood += locLogLikelihood;
/*  595:     */       }
/*  596: 803 */       logLikelihood /= this.m_NumBootstrapRuns;
/*  597: 804 */       if (this.m_Debug) {
/*  598: 805 */         System.err.println("Loglikelihood: " + logLikelihood + "\tNumber of components: " + i);
/*  599:     */       }
/*  600: 807 */       if (logLikelihood > bestLogLikelihood)
/*  601:     */       {
/*  602: 808 */         bestNumComponents = i;
/*  603: 809 */         bestLogLikelihood = logLikelihood;
/*  604:     */       }
/*  605:     */     }
/*  606: 813 */     return bestNumComponents;
/*  607:     */   }
/*  608:     */   
/*  609:     */   protected double entropy(MM mixtureModel)
/*  610:     */   {
/*  611: 821 */     double entropy = 0.0D;
/*  612: 822 */     for (int j = 0; j < this.m_NumValues; j++) {
/*  613: 823 */       entropy += this.m_Weights[j] * ContingencyTables.entropy(Utils.logs2probs(mixtureModel.logJointDensities(this.m_Values[j])));
/*  614:     */     }
/*  615: 826 */     entropy *= Utils.log2;
/*  616:     */     
/*  617: 828 */     return entropy / this.m_NumValues;
/*  618:     */   }
/*  619:     */   
/*  620:     */   protected MM findModelUsingNormalizedEntropy()
/*  621:     */   {
/*  622: 838 */     if (this.m_NumComponents > 0) {
/*  623: 839 */       return buildModel(this.m_NumComponents, this.m_Values, this.m_Weights);
/*  624:     */     }
/*  625: 841 */     if (this.m_MaxNumComponents <= 1) {
/*  626: 842 */       return buildModel(1, this.m_Values, this.m_Weights);
/*  627:     */     }
/*  628: 846 */     MM bestMixtureModel = buildModel(1, this.m_Values, this.m_Weights);
/*  629: 847 */     double loglikelihoodForOneCluster = bestMixtureModel.loglikelihood(this.m_Values, this.m_Weights);
/*  630: 848 */     double bestNormalizedEntropy = 1.0D;
/*  631: 849 */     for (int i = 2; i <= this.m_MaxNumComponents; i++)
/*  632:     */     {
/*  633: 850 */       MM mixtureModel = buildModel(i, this.m_Values, this.m_Weights);
/*  634:     */       
/*  635: 852 */       double loglikelihood = mixtureModel.loglikelihood(this.m_Values, this.m_Weights);
/*  636: 853 */       if (loglikelihood < loglikelihoodForOneCluster)
/*  637:     */       {
/*  638: 855 */         if (this.m_Debug) {
/*  639: 856 */           System.err.println("Likelihood for one cluster greater than for " + i + " clusters.");
/*  640:     */         }
/*  641:     */       }
/*  642:     */       else
/*  643:     */       {
/*  644: 860 */         double entropy = entropy(mixtureModel);
/*  645: 861 */         double normalizedEntropy = entropy / (loglikelihood - loglikelihoodForOneCluster);
/*  646: 863 */         if (this.m_Debug) {
/*  647: 864 */           System.err.println("Entropy: " + entropy + "\tLogLikelihood: " + loglikelihood + "\tLoglikelihood for one cluster: " + loglikelihoodForOneCluster + "\tNormalized entropy: " + normalizedEntropy + "\tNumber of components: " + i);
/*  648:     */         }
/*  649: 867 */         if (normalizedEntropy < bestNormalizedEntropy)
/*  650:     */         {
/*  651: 868 */           bestMixtureModel = mixtureModel;
/*  652: 869 */           bestNormalizedEntropy = normalizedEntropy;
/*  653:     */         }
/*  654:     */       }
/*  655:     */     }
/*  656: 873 */     return bestMixtureModel;
/*  657:     */   }
/*  658:     */   
/*  659:     */   protected void updateModel()
/*  660:     */   {
/*  661: 882 */     if (this.m_MixtureModel != null) {
/*  662: 883 */       return;
/*  663:     */     }
/*  664: 884 */     if (this.m_NumValues > 0)
/*  665:     */     {
/*  666: 887 */       if (this.m_Values.length > this.m_NumValues)
/*  667:     */       {
/*  668: 888 */         double[] values = new double[this.m_NumValues];
/*  669: 889 */         double[] weights = new double[this.m_NumValues];
/*  670: 890 */         System.arraycopy(this.m_Values, 0, values, 0, this.m_NumValues);
/*  671: 891 */         System.arraycopy(this.m_Weights, 0, weights, 0, this.m_NumValues);
/*  672: 892 */         this.m_Values = values;
/*  673: 893 */         this.m_Weights = weights;
/*  674:     */       }
/*  675: 896 */       if (this.m_UseNormalizedEntropy) {
/*  676: 897 */         this.m_MixtureModel = findModelUsingNormalizedEntropy();
/*  677:     */       } else {
/*  678: 899 */         this.m_MixtureModel = buildModel(findNumComponentsUsingBootStrap(), this.m_Values, this.m_Weights);
/*  679:     */       }
/*  680:     */     }
/*  681:     */   }
/*  682:     */   
/*  683:     */   public double[][] predictIntervals(double conf)
/*  684:     */   {
/*  685: 912 */     updateModel();
/*  686:     */     
/*  687: 914 */     return this.m_MixtureModel.predictIntervals(conf);
/*  688:     */   }
/*  689:     */   
/*  690:     */   public double predictQuantile(double percentage)
/*  691:     */   {
/*  692: 925 */     updateModel();
/*  693:     */     
/*  694: 927 */     return this.m_MixtureModel.predictQuantile(percentage);
/*  695:     */   }
/*  696:     */   
/*  697:     */   public double logDensity(double value)
/*  698:     */   {
/*  699: 941 */     updateModel();
/*  700: 942 */     if (this.m_MixtureModel == null) {
/*  701: 943 */       return Math.log(4.9E-324D);
/*  702:     */     }
/*  703: 945 */     return this.m_MixtureModel.logDensity(value);
/*  704:     */   }
/*  705:     */   
/*  706:     */   public String toString()
/*  707:     */   {
/*  708: 953 */     updateModel();
/*  709: 954 */     if (this.m_MixtureModel == null) {
/*  710: 955 */       return "";
/*  711:     */     }
/*  712: 957 */     return this.m_MixtureModel.toString();
/*  713:     */   }
/*  714:     */   
/*  715:     */   public Enumeration<Option> listOptions()
/*  716:     */   {
/*  717: 968 */     Vector<Option> options = new Vector();
/*  718: 969 */     options.addElement(new Option("\tNumber of components to use (default: -1).", "N", 1, "-N"));
/*  719: 970 */     options.addElement(new Option("\tMaximum number of components to use (default: 5).", "M", 1, "-M"));
/*  720: 971 */     options.addElement(new Option("\tSeed for the random number generator (default: 1).", "S", 1, "-S"));
/*  721: 972 */     options.addElement(new Option("\tThe number of bootstrap runs to use (default: 10).", "B", 1, "-B"));
/*  722: 973 */     options.addElement(new Option("\tUse normalized entropy instead of bootstrap.", "E", 1, "-E"));
/*  723: 974 */     return options.elements();
/*  724:     */   }
/*  725:     */   
/*  726:     */   public void setOptions(String[] options)
/*  727:     */     throws Exception
/*  728:     */   {
/*  729: 985 */     String optionString = Utils.getOption("N", options);
/*  730: 986 */     if (optionString.length() > 0) {
/*  731: 987 */       setNumComponents(Integer.parseInt(optionString));
/*  732:     */     } else {
/*  733: 989 */       setNumComponents(-1);
/*  734:     */     }
/*  735: 991 */     optionString = Utils.getOption("M", options);
/*  736: 992 */     if (optionString.length() > 0) {
/*  737: 993 */       setMaxNumComponents(Integer.parseInt(optionString));
/*  738:     */     } else {
/*  739: 995 */       setMaxNumComponents(5);
/*  740:     */     }
/*  741: 997 */     optionString = Utils.getOption("S", options);
/*  742: 998 */     if (optionString.length() > 0) {
/*  743: 999 */       setSeed(Integer.parseInt(optionString));
/*  744:     */     } else {
/*  745:1001 */       setSeed(1);
/*  746:     */     }
/*  747:1003 */     optionString = Utils.getOption("B", options);
/*  748:1004 */     if (optionString.length() > 0) {
/*  749:1005 */       setNumBootstrapRuns(Integer.parseInt(optionString));
/*  750:     */     } else {
/*  751:1007 */       setNumBootstrapRuns(10);
/*  752:     */     }
/*  753:1009 */     this.m_UseNormalizedEntropy = Utils.getFlag("E", options);
/*  754:1010 */     Utils.checkForRemainingOptions(options);
/*  755:     */   }
/*  756:     */   
/*  757:     */   public String[] getOptions()
/*  758:     */   {
/*  759:1020 */     Vector<String> options = new Vector();
/*  760:     */     
/*  761:1022 */     options.add("-N");
/*  762:1023 */     options.add("" + getNumComponents());
/*  763:     */     
/*  764:1025 */     options.add("-M");
/*  765:1026 */     options.add("" + getMaxNumComponents());
/*  766:     */     
/*  767:1028 */     options.add("-S");
/*  768:1029 */     options.add("" + getSeed());
/*  769:     */     
/*  770:1031 */     options.add("-B");
/*  771:1032 */     options.add("" + getNumBootstrapRuns());
/*  772:1034 */     if (this.m_UseNormalizedEntropy) {
/*  773:1035 */       options.add("-E");
/*  774:     */     }
/*  775:1038 */     return (String[])options.toArray(new String[0]);
/*  776:     */   }
/*  777:     */   
/*  778:     */   public String getRevision()
/*  779:     */   {
/*  780:1048 */     return RevisionUtils.extract("$Revision: 10971 $");
/*  781:     */   }
/*  782:     */   
/*  783:     */   public static void main(String[] args)
/*  784:     */     throws Exception
/*  785:     */   {
/*  786:1057 */     Random r = new Random();
/*  787:     */     
/*  788:     */ 
/*  789:1060 */     UnivariateMixtureEstimator e = new UnivariateMixtureEstimator();
/*  790:1061 */     e.setOptions((String[])Arrays.copyOf(args, args.length));
/*  791:     */     
/*  792:     */ 
/*  793:1064 */     System.out.println(e);
/*  794:     */     
/*  795:     */ 
/*  796:1067 */     double sum = 0.0D;
/*  797:1068 */     for (int i = 0; i < 100000; i++) {
/*  798:1069 */       sum += Math.exp(e.logDensity(r.nextDouble() * 10.0D - 5.0D));
/*  799:     */     }
/*  800:1071 */     System.out.println("Approximate integral: " + 10.0D * sum / 100000.0D);
/*  801:1074 */     for (int i = 0; i < 100000; i++)
/*  802:     */     {
/*  803:1075 */       e.addValue(r.nextGaussian() * 0.5D - 1.0D, 1.0D);
/*  804:1076 */       e.addValue(r.nextGaussian() * 0.5D + 1.0D, 3.0D);
/*  805:     */     }
/*  806:1080 */     System.out.println(e);
/*  807:     */     
/*  808:     */ 
/*  809:1083 */     sum = 0.0D;
/*  810:1084 */     for (int i = 0; i < 100000; i++) {
/*  811:1085 */       sum += Math.exp(e.logDensity(r.nextDouble() * 10.0D - 5.0D));
/*  812:     */     }
/*  813:1087 */     System.out.println("Approximate integral: " + 10.0D * sum / 100000.0D);
/*  814:     */     
/*  815:     */ 
/*  816:1090 */     e = new UnivariateMixtureEstimator();
/*  817:1091 */     e.setOptions((String[])Arrays.copyOf(args, args.length));
/*  818:1094 */     for (int i = 0; i < 100000; i++)
/*  819:     */     {
/*  820:1095 */       e.addValue(r.nextGaussian() * 0.5D - 1.0D, 1.0D);
/*  821:1096 */       e.addValue(r.nextGaussian() * 0.5D + 1.0D, 1.0D);
/*  822:1097 */       e.addValue(r.nextGaussian() * 0.5D + 1.0D, 1.0D);
/*  823:1098 */       e.addValue(r.nextGaussian() * 0.5D + 1.0D, 1.0D);
/*  824:     */     }
/*  825:1102 */     System.out.println(e);
/*  826:     */     
/*  827:     */ 
/*  828:1105 */     sum = 0.0D;
/*  829:1106 */     for (int i = 0; i < 100000; i++) {
/*  830:1107 */       sum += Math.exp(e.logDensity(r.nextDouble() * 10.0D - 5.0D));
/*  831:     */     }
/*  832:1109 */     System.out.println("Approximate integral: " + 10.0D * sum / 100000.0D);
/*  833:     */     
/*  834:     */ 
/*  835:1112 */     e = new UnivariateMixtureEstimator();
/*  836:1113 */     e.setOptions((String[])Arrays.copyOf(args, args.length));
/*  837:1116 */     for (int i = 0; i < 100000; i++) {
/*  838:1117 */       e.addValue(r.nextGaussian() * 5.0D + 3.0D, 1.0D);
/*  839:     */     }
/*  840:1121 */     System.out.println(e);
/*  841:     */     
/*  842:     */ 
/*  843:1124 */     double[][] intervals = e.predictIntervals(0.95D);
/*  844:1125 */     System.out.println("Lower: " + intervals[0][0] + " Upper: " + intervals[0][1]);
/*  845:1126 */     double covered = 0.0D;
/*  846:1127 */     for (int i = 0; i < 100000; i++)
/*  847:     */     {
/*  848:1128 */       double val = r.nextGaussian() * 5.0D + 3.0D;
/*  849:1129 */       if ((val >= intervals[0][0]) && (val <= intervals[0][1])) {
/*  850:1130 */         covered += 1.0D;
/*  851:     */       }
/*  852:     */     }
/*  853:1133 */     System.out.println("Coverage: " + covered / 100000.0D);
/*  854:     */     
/*  855:1135 */     intervals = e.predictIntervals(0.8D);
/*  856:1136 */     System.out.println("Lower: " + intervals[0][0] + " Upper: " + intervals[0][1]);
/*  857:1137 */     covered = 0.0D;
/*  858:1138 */     for (int i = 0; i < 100000; i++)
/*  859:     */     {
/*  860:1139 */       double val = r.nextGaussian() * 5.0D + 3.0D;
/*  861:1140 */       if ((val >= intervals[0][0]) && (val <= intervals[0][1])) {
/*  862:1141 */         covered += 1.0D;
/*  863:     */       }
/*  864:     */     }
/*  865:1144 */     System.out.println("Coverage: " + covered / 100000.0D);
/*  866:     */     
/*  867:     */ 
/*  868:1147 */     System.out.println("95% quantile: " + e.predictQuantile(0.95D));
/*  869:     */   }
/*  870:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.estimators.UnivariateMixtureEstimator
 * JD-Core Version:    0.7.0.1
 */