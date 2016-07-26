/*   1:    */ package weka.estimators;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Iterator;
/*   7:    */ import java.util.Map.Entry;
/*   8:    */ import java.util.NavigableMap;
/*   9:    */ import java.util.Random;
/*  10:    */ import java.util.Set;
/*  11:    */ import java.util.TreeMap;
/*  12:    */ import weka.core.RevisionUtils;
/*  13:    */ import weka.core.Statistics;
/*  14:    */ import weka.core.Utils;
/*  15:    */ 
/*  16:    */ public class UnivariateKernelEstimator
/*  17:    */   implements UnivariateDensityEstimator, UnivariateIntervalEstimator, UnivariateQuantileEstimator, Serializable
/*  18:    */ {
/*  19:    */   private static final long serialVersionUID = -1163983347810498880L;
/*  20: 49 */   protected TreeMap<Double, Double> m_TM = new TreeMap();
/*  21: 52 */   protected double m_WeightedSum = 0.0D;
/*  22: 55 */   protected double m_WeightedSumSquared = 0.0D;
/*  23: 58 */   protected double m_SumOfWeights = 0.0D;
/*  24: 61 */   protected double m_Width = 1.7976931348623157E+308D;
/*  25: 64 */   protected double m_Exponent = -0.25D;
/*  26: 67 */   protected double m_MinWidth = 1.0E-006D;
/*  27: 70 */   public static final double CONST = -0.5D * Math.log(6.283185307179586D);
/*  28: 73 */   protected double m_Threshold = 1.0E-006D;
/*  29: 76 */   protected int m_NumIntervals = 1000;
/*  30:    */   
/*  31:    */   public String globalInfo()
/*  32:    */   {
/*  33: 82 */     return "Provides a univariate kernel estimator.";
/*  34:    */   }
/*  35:    */   
/*  36:    */   public void addValue(double value, double weight)
/*  37:    */   {
/*  38: 93 */     this.m_WeightedSum += value * weight;
/*  39: 94 */     this.m_WeightedSumSquared += value * value * weight;
/*  40: 95 */     this.m_SumOfWeights += weight;
/*  41: 96 */     if (this.m_TM.get(Double.valueOf(value)) == null) {
/*  42: 97 */       this.m_TM.put(Double.valueOf(value), Double.valueOf(weight));
/*  43:    */     } else {
/*  44: 99 */       this.m_TM.put(Double.valueOf(value), Double.valueOf(((Double)this.m_TM.get(Double.valueOf(value))).doubleValue() + weight));
/*  45:    */     }
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void updateWidth()
/*  49:    */   {
/*  50:114 */     if (this.m_SumOfWeights > 0.0D)
/*  51:    */     {
/*  52:117 */       double mean = this.m_WeightedSum / this.m_SumOfWeights;
/*  53:118 */       double variance = this.m_WeightedSumSquared / this.m_SumOfWeights - mean * mean;
/*  54:119 */       if (variance < 0.0D) {
/*  55:120 */         variance = 0.0D;
/*  56:    */       }
/*  57:124 */       this.m_Width = (Math.sqrt(variance) * Math.pow(this.m_SumOfWeights, this.m_Exponent));
/*  58:126 */       if (this.m_Width <= this.m_MinWidth) {
/*  59:127 */         this.m_Width = this.m_MinWidth;
/*  60:    */       }
/*  61:    */     }
/*  62:    */     else
/*  63:    */     {
/*  64:130 */       this.m_Width = 1.7976931348623157E+308D;
/*  65:    */     }
/*  66:    */   }
/*  67:    */   
/*  68:    */   public double[][] predictIntervals(double conf)
/*  69:    */   {
/*  70:144 */     updateWidth();
/*  71:    */     
/*  72:    */ 
/*  73:147 */     double val = Statistics.normalInverse(1.0D - (1.0D - conf) / 2.0D);
/*  74:148 */     double min = ((Double)this.m_TM.firstKey()).doubleValue() - val * this.m_Width;
/*  75:149 */     double max = ((Double)this.m_TM.lastKey()).doubleValue() + val * this.m_Width;
/*  76:150 */     double delta = (max - min) / this.m_NumIntervals;
/*  77:    */     
/*  78:    */ 
/*  79:153 */     double[] probabilities = new double[this.m_NumIntervals];
/*  80:154 */     double leftVal = Math.exp(logDensity(min));
/*  81:155 */     for (int i = 0; i < this.m_NumIntervals; i++)
/*  82:    */     {
/*  83:156 */       double rightVal = Math.exp(logDensity(min + (i + 1) * delta));
/*  84:157 */       probabilities[i] = (0.5D * (leftVal + rightVal) * delta);
/*  85:158 */       leftVal = rightVal;
/*  86:    */     }
/*  87:162 */     int[] sortedIndices = Utils.sort(probabilities);
/*  88:    */     
/*  89:    */ 
/*  90:165 */     double sum = 0.0D;
/*  91:166 */     boolean[] toUse = new boolean[probabilities.length];
/*  92:167 */     int k = 0;
/*  93:168 */     while ((sum < conf) && (k < toUse.length))
/*  94:    */     {
/*  95:169 */       toUse[sortedIndices[(toUse.length - (k + 1))]] = true;
/*  96:170 */       sum += probabilities[sortedIndices[(toUse.length - (k + 1))]];
/*  97:171 */       k++;
/*  98:    */     }
/*  99:175 */     probabilities = null;
/* 100:    */     
/* 101:    */ 
/* 102:178 */     ArrayList<double[]> intervals = new ArrayList();
/* 103:    */     
/* 104:    */ 
/* 105:181 */     double[] interval = null;
/* 106:    */     
/* 107:    */ 
/* 108:184 */     boolean haveStartedInterval = false;
/* 109:185 */     for (int i = 0; i < this.m_NumIntervals; i++) {
/* 110:188 */       if (toUse[i] != 0)
/* 111:    */       {
/* 112:191 */         if (!haveStartedInterval)
/* 113:    */         {
/* 114:192 */           haveStartedInterval = true;
/* 115:193 */           interval = new double[2];
/* 116:194 */           interval[0] = (min + i * delta);
/* 117:    */         }
/* 118:198 */         interval[1] = (min + (i + 1) * delta);
/* 119:    */       }
/* 120:203 */       else if (haveStartedInterval)
/* 121:    */       {
/* 122:204 */         haveStartedInterval = false;
/* 123:205 */         intervals.add(interval);
/* 124:    */       }
/* 125:    */     }
/* 126:211 */     if (haveStartedInterval) {
/* 127:212 */       intervals.add(interval);
/* 128:    */     }
/* 129:215 */     return (double[][])intervals.toArray(new double[0][0]);
/* 130:    */   }
/* 131:    */   
/* 132:    */   public double predictQuantile(double percentage)
/* 133:    */   {
/* 134:228 */     updateWidth();
/* 135:    */     
/* 136:    */ 
/* 137:231 */     double val = Statistics.normalInverse(0.975D);
/* 138:232 */     double min = ((Double)this.m_TM.firstKey()).doubleValue() - val * this.m_Width;
/* 139:233 */     double max = ((Double)this.m_TM.lastKey()).doubleValue() + val * this.m_Width;
/* 140:234 */     double delta = (max - min) / this.m_NumIntervals;
/* 141:    */     
/* 142:236 */     double sum = 0.0D;
/* 143:237 */     double leftVal = Math.exp(logDensity(min));
/* 144:238 */     for (int i = 0; i < this.m_NumIntervals; i++)
/* 145:    */     {
/* 146:239 */       if (sum >= percentage) {
/* 147:240 */         return min + i * delta;
/* 148:    */       }
/* 149:242 */       double rightVal = Math.exp(logDensity(min + (i + 1) * delta));
/* 150:243 */       sum += 0.5D * (leftVal + rightVal) * delta;
/* 151:244 */       leftVal = rightVal;
/* 152:    */     }
/* 153:246 */     return max;
/* 154:    */   }
/* 155:    */   
/* 156:    */   protected double logOfSum(double logOfX, double logOfY)
/* 157:    */   {
/* 158:258 */     if (Double.isNaN(logOfX)) {
/* 159:259 */       return logOfY;
/* 160:    */     }
/* 161:261 */     if (Double.isNaN(logOfY)) {
/* 162:262 */       return logOfX;
/* 163:    */     }
/* 164:266 */     if (logOfX > logOfY) {
/* 165:267 */       return logOfX + Math.log(1.0D + Math.exp(logOfY - logOfX));
/* 166:    */     }
/* 167:269 */     return logOfY + Math.log(1.0D + Math.exp(logOfX - logOfY));
/* 168:    */   }
/* 169:    */   
/* 170:    */   protected void runningSum(Set<Map.Entry<Double, Double>> c, double value, double[] sums)
/* 171:    */   {
/* 172:280 */     double offset = CONST - Math.log(this.m_Width);
/* 173:281 */     double logFactor = Math.log(this.m_Threshold) - Math.log(1.0D - this.m_Threshold);
/* 174:282 */     double logSumOfWeights = Math.log(this.m_SumOfWeights);
/* 175:    */     
/* 176:    */ 
/* 177:285 */     Iterator<Map.Entry<Double, Double>> itr = c.iterator();
/* 178:286 */     while (itr.hasNext())
/* 179:    */     {
/* 180:287 */       Map.Entry<Double, Double> entry = (Map.Entry)itr.next();
/* 181:290 */       if (((Double)entry.getValue()).doubleValue() > 0.0D)
/* 182:    */       {
/* 183:291 */         double diff = (((Double)entry.getKey()).doubleValue() - value) / this.m_Width;
/* 184:292 */         double logDensity = offset - 0.5D * diff * diff;
/* 185:293 */         double logWeight = Math.log(((Double)entry.getValue()).doubleValue());
/* 186:294 */         sums[0] = logOfSum(sums[0], logWeight + logDensity);
/* 187:295 */         sums[1] = logOfSum(sums[1], logWeight);
/* 188:298 */         if (logDensity + logSumOfWeights < logOfSum(logFactor + sums[0], logDensity + sums[1])) {
/* 189:    */           break;
/* 190:    */         }
/* 191:    */       }
/* 192:    */     }
/* 193:    */   }
/* 194:    */   
/* 195:    */   public double logDensity(double value)
/* 196:    */   {
/* 197:316 */     updateWidth();
/* 198:    */     
/* 199:    */ 
/* 200:319 */     double[] sums = new double[2];
/* 201:320 */     sums[0] = (0.0D / 0.0D);
/* 202:321 */     sums[1] = (0.0D / 0.0D);
/* 203:    */     
/* 204:    */ 
/* 205:324 */     runningSum(this.m_TM.tailMap(Double.valueOf(value), true).entrySet(), value, sums);
/* 206:    */     
/* 207:    */ 
/* 208:327 */     runningSum(this.m_TM.headMap(Double.valueOf(value), false).descendingMap().entrySet(), value, sums);
/* 209:    */     
/* 210:    */ 
/* 211:    */ 
/* 212:331 */     return sums[0] - Math.log(this.m_SumOfWeights);
/* 213:    */   }
/* 214:    */   
/* 215:    */   public String toString()
/* 216:    */   {
/* 217:340 */     return "Kernel estimator with bandwidth " + this.m_Width + " and total weight " + this.m_SumOfWeights + " based on\n" + this.m_TM.toString();
/* 218:    */   }
/* 219:    */   
/* 220:    */   public String getRevision()
/* 221:    */   {
/* 222:351 */     return RevisionUtils.extract("$Revision: 11318 $");
/* 223:    */   }
/* 224:    */   
/* 225:    */   public static void main(String[] args)
/* 226:    */   {
/* 227:360 */     Random r = new Random();
/* 228:    */     
/* 229:    */ 
/* 230:363 */     UnivariateKernelEstimator e = new UnivariateKernelEstimator();
/* 231:    */     
/* 232:    */ 
/* 233:366 */     System.out.println(e);
/* 234:    */     
/* 235:    */ 
/* 236:369 */     double sum = 0.0D;
/* 237:370 */     for (int i = 0; i < 1000; i++) {
/* 238:371 */       sum += Math.exp(e.logDensity(r.nextDouble() * 10.0D - 5.0D));
/* 239:    */     }
/* 240:373 */     System.out.println("Approximate integral: " + 10.0D * sum / 1000.0D);
/* 241:376 */     for (int i = 0; i < 1000; i++)
/* 242:    */     {
/* 243:377 */       e.addValue(0.1D * r.nextGaussian() - 3.0D, 1.0D);
/* 244:378 */       e.addValue(r.nextGaussian() * 0.25D, 3.0D);
/* 245:    */     }
/* 246:382 */     sum = 0.0D;
/* 247:383 */     int points = 10000;
/* 248:384 */     for (int i = 0; i < points; i++)
/* 249:    */     {
/* 250:385 */       double value = r.nextDouble() * 10.0D - 5.0D;
/* 251:386 */       sum += Math.exp(e.logDensity(value));
/* 252:    */     }
/* 253:388 */     System.out.println("Approximate integral: " + 10.0D * sum / points);
/* 254:    */     
/* 255:    */ 
/* 256:391 */     double[][] Intervals = e.predictIntervals(0.9D);
/* 257:    */     
/* 258:393 */     System.out.println("Printing kernel intervals ---------------------");
/* 259:395 */     for (double[] interval : Intervals) {
/* 260:396 */       System.out.println("Left: " + interval[0] + "\t Right: " + interval[1]);
/* 261:    */     }
/* 262:399 */     System.out.println("Finished kernel printing intervals ---------------------");
/* 263:    */     
/* 264:    */ 
/* 265:402 */     double Covered = 0.0D;
/* 266:403 */     for (int i = 0; i < 1000; i++)
/* 267:    */     {
/* 268:404 */       double val = -1.0D;
/* 269:405 */       if (r.nextDouble() < 0.25D) {
/* 270:406 */         val = 0.1D * r.nextGaussian() - 3.0D;
/* 271:    */       } else {
/* 272:408 */         val = r.nextGaussian() * 0.25D;
/* 273:    */       }
/* 274:410 */       for (double[] interval : Intervals) {
/* 275:411 */         if ((val >= interval[0]) && (val <= interval[1]))
/* 276:    */         {
/* 277:412 */           Covered += 1.0D;
/* 278:413 */           break;
/* 279:    */         }
/* 280:    */       }
/* 281:    */     }
/* 282:417 */     System.out.println("Coverage at 0.9 level for kernel intervals: " + Covered / 1000.0D);
/* 283:    */     
/* 284:    */ 
/* 285:    */ 
/* 286:421 */     UnivariateKernelEstimator eKernel = new UnivariateKernelEstimator();
/* 287:422 */     UnivariateNormalEstimator eNormal = new UnivariateNormalEstimator();
/* 288:424 */     for (int j = 1; j < 5; j++)
/* 289:    */     {
/* 290:425 */       double numTrain = Math.pow(10.0D, j);
/* 291:426 */       System.out.println("Number of training cases: " + numTrain);
/* 292:429 */       for (int i = 0; i < numTrain; i++)
/* 293:    */       {
/* 294:430 */         double val = r.nextGaussian() * 1.5D + 0.5D;
/* 295:431 */         eKernel.addValue(val, 1.0D);
/* 296:432 */         eNormal.addValue(val, 1.0D);
/* 297:    */       }
/* 298:436 */       sum = 0.0D;
/* 299:437 */       points = 10000;
/* 300:438 */       for (int i = 0; i < points; i++)
/* 301:    */       {
/* 302:439 */         double value = r.nextDouble() * 20.0D - 10.0D;
/* 303:440 */         sum += Math.exp(eKernel.logDensity(value));
/* 304:    */       }
/* 305:442 */       System.out.println("Approximate integral for kernel estimator: " + 20.0D * sum / points);
/* 306:    */       
/* 307:    */ 
/* 308:    */ 
/* 309:446 */       double loglikelihoodKernel = 0.0D;double loglikelihoodNormal = 0.0D;
/* 310:447 */       for (int i = 0; i < 1000; i++)
/* 311:    */       {
/* 312:448 */         double val = r.nextGaussian() * 1.5D + 0.5D;
/* 313:449 */         loglikelihoodKernel += eKernel.logDensity(val);
/* 314:450 */         loglikelihoodNormal += eNormal.logDensity(val);
/* 315:    */       }
/* 316:452 */       System.out.println("Loglikelihood for kernel estimator: " + loglikelihoodKernel / 1000.0D);
/* 317:    */       
/* 318:454 */       System.out.println("Loglikelihood for normal estimator: " + loglikelihoodNormal / 1000.0D);
/* 319:    */       
/* 320:    */ 
/* 321:    */ 
/* 322:458 */       double[][] kernelIntervals = eKernel.predictIntervals(0.95D);
/* 323:459 */       double[][] normalIntervals = eNormal.predictIntervals(0.95D);
/* 324:    */       
/* 325:461 */       System.out.println("Printing kernel intervals ---------------------");
/* 326:463 */       for (double[] kernelInterval : kernelIntervals) {
/* 327:464 */         System.out.println("Left: " + kernelInterval[0] + "\t Right: " + kernelInterval[1]);
/* 328:    */       }
/* 329:468 */       System.out.println("Finished kernel printing intervals ---------------------");
/* 330:    */       
/* 331:    */ 
/* 332:471 */       System.out.println("Printing normal intervals ---------------------");
/* 333:473 */       for (double[] normalInterval : normalIntervals) {
/* 334:474 */         System.out.println("Left: " + normalInterval[0] + "\t Right: " + normalInterval[1]);
/* 335:    */       }
/* 336:478 */       System.out.println("Finished normal printing intervals ---------------------");
/* 337:    */       
/* 338:    */ 
/* 339:481 */       double kernelCovered = 0.0D;
/* 340:482 */       double normalCovered = 0.0D;
/* 341:483 */       for (int i = 0; i < 1000; i++)
/* 342:    */       {
/* 343:484 */         double val = r.nextGaussian() * 1.5D + 0.5D;
/* 344:485 */         for (double[] kernelInterval : kernelIntervals) {
/* 345:486 */           if ((val >= kernelInterval[0]) && (val <= kernelInterval[1]))
/* 346:    */           {
/* 347:487 */             kernelCovered += 1.0D;
/* 348:488 */             break;
/* 349:    */           }
/* 350:    */         }
/* 351:491 */         for (double[] normalInterval : normalIntervals) {
/* 352:492 */           if ((val >= normalInterval[0]) && (val <= normalInterval[1]))
/* 353:    */           {
/* 354:493 */             normalCovered += 1.0D;
/* 355:494 */             break;
/* 356:    */           }
/* 357:    */         }
/* 358:    */       }
/* 359:498 */       System.out.println("Coverage at 0.95 level for kernel intervals: " + kernelCovered / 1000.0D);
/* 360:    */       
/* 361:500 */       System.out.println("Coverage at 0.95 level for normal intervals: " + normalCovered / 1000.0D);
/* 362:    */       
/* 363:    */ 
/* 364:503 */       kernelIntervals = eKernel.predictIntervals(0.8D);
/* 365:504 */       normalIntervals = eNormal.predictIntervals(0.8D);
/* 366:505 */       kernelCovered = 0.0D;
/* 367:506 */       normalCovered = 0.0D;
/* 368:507 */       for (int i = 0; i < 1000; i++)
/* 369:    */       {
/* 370:508 */         double val = r.nextGaussian() * 1.5D + 0.5D;
/* 371:509 */         for (double[] kernelInterval : kernelIntervals) {
/* 372:510 */           if ((val >= kernelInterval[0]) && (val <= kernelInterval[1]))
/* 373:    */           {
/* 374:511 */             kernelCovered += 1.0D;
/* 375:512 */             break;
/* 376:    */           }
/* 377:    */         }
/* 378:515 */         for (double[] normalInterval : normalIntervals) {
/* 379:516 */           if ((val >= normalInterval[0]) && (val <= normalInterval[1]))
/* 380:    */           {
/* 381:517 */             normalCovered += 1.0D;
/* 382:518 */             break;
/* 383:    */           }
/* 384:    */         }
/* 385:    */       }
/* 386:522 */       System.out.println("Coverage at 0.8 level for kernel intervals: " + kernelCovered / 1000.0D);
/* 387:    */       
/* 388:524 */       System.out.println("Coverage at 0.8 level for normal intervals: " + normalCovered / 1000.0D);
/* 389:    */     }
/* 390:    */   }
/* 391:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.estimators.UnivariateKernelEstimator
 * JD-Core Version:    0.7.0.1
 */