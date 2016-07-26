/*   1:    */ package weka.estimators;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Arrays;
/*   7:    */ import java.util.Iterator;
/*   8:    */ import java.util.Map.Entry;
/*   9:    */ import java.util.Random;
/*  10:    */ import java.util.Set;
/*  11:    */ import java.util.TreeMap;
/*  12:    */ import weka.core.RevisionUtils;
/*  13:    */ import weka.core.Statistics;
/*  14:    */ import weka.core.Utils;
/*  15:    */ 
/*  16:    */ public class UnivariateEqualFrequencyHistogramEstimator
/*  17:    */   implements UnivariateDensityEstimator, UnivariateIntervalEstimator, UnivariateQuantileEstimator, Serializable
/*  18:    */ {
/*  19:    */   private static final long serialVersionUID = -3180287591539683137L;
/*  20: 51 */   protected TreeMap<Double, Double> m_TM = new TreeMap();
/*  21: 54 */   protected double[] m_Boundaries = null;
/*  22: 57 */   protected double[] m_Weights = null;
/*  23: 60 */   protected double m_WeightedSum = 0.0D;
/*  24: 63 */   protected double m_WeightedSumSquared = 0.0D;
/*  25: 66 */   protected double m_SumOfWeights = 0.0D;
/*  26: 69 */   protected int m_NumBins = 10;
/*  27: 72 */   protected double m_Width = 1.7976931348623157E+308D;
/*  28: 75 */   protected double m_Exponent = -0.25D;
/*  29: 78 */   protected double m_MinWidth = 1.0E-006D;
/*  30: 81 */   public static final double CONST = -0.5D * Math.log(6.283185307179586D);
/*  31: 84 */   protected int m_NumIntervals = 1000;
/*  32: 87 */   protected boolean m_UpdateWeightsOnly = false;
/*  33:    */   
/*  34:    */   public String globalInfo()
/*  35:    */   {
/*  36: 93 */     return "Provides a univariate histogram estimator based on equal-frequency bins.";
/*  37:    */   }
/*  38:    */   
/*  39:    */   public int getNumBins()
/*  40:    */   {
/*  41:102 */     return this.m_NumBins;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public void setNumBins(int numBins)
/*  45:    */   {
/*  46:112 */     this.m_NumBins = numBins;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void initializeStatistics()
/*  50:    */   {
/*  51:121 */     updateBoundariesAndOrWeights();
/*  52:    */     
/*  53:123 */     this.m_TM = new TreeMap();
/*  54:124 */     this.m_WeightedSum = 0.0D;
/*  55:125 */     this.m_WeightedSumSquared = 0.0D;
/*  56:126 */     this.m_SumOfWeights = 0.0D;
/*  57:127 */     this.m_Weights = null;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public void setUpdateWeightsOnly(boolean flag)
/*  61:    */   {
/*  62:135 */     this.m_UpdateWeightsOnly = flag;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public boolean getUpdateWeightsOnly()
/*  66:    */   {
/*  67:143 */     return this.m_UpdateWeightsOnly;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public void addValue(double value, double weight)
/*  71:    */   {
/*  72:156 */     this.m_WeightedSum += value * weight;
/*  73:157 */     this.m_WeightedSumSquared += value * value * weight;
/*  74:158 */     this.m_SumOfWeights += weight;
/*  75:159 */     if (this.m_TM.get(Double.valueOf(value)) == null) {
/*  76:160 */       this.m_TM.put(Double.valueOf(value), Double.valueOf(weight));
/*  77:    */     } else {
/*  78:162 */       this.m_TM.put(Double.valueOf(value), Double.valueOf(((Double)this.m_TM.get(Double.valueOf(value))).doubleValue() + weight));
/*  79:    */     }
/*  80:166 */     if (!getUpdateWeightsOnly()) {
/*  81:167 */       this.m_Boundaries = null;
/*  82:    */     }
/*  83:169 */     this.m_Weights = null;
/*  84:    */   }
/*  85:    */   
/*  86:    */   protected void updateBoundariesAndOrWeights()
/*  87:    */   {
/*  88:178 */     if (this.m_Weights != null) {
/*  89:179 */       return;
/*  90:    */     }
/*  91:186 */     double mean = this.m_WeightedSum / this.m_SumOfWeights;
/*  92:187 */     double variance = this.m_WeightedSumSquared / this.m_SumOfWeights - mean * mean;
/*  93:188 */     if (variance < 0.0D) {
/*  94:189 */       variance = 0.0D;
/*  95:    */     }
/*  96:193 */     this.m_Width = (Math.sqrt(variance) * Math.pow(this.m_SumOfWeights, this.m_Exponent));
/*  97:195 */     if (this.m_Width <= this.m_MinWidth) {
/*  98:196 */       this.m_Width = this.m_MinWidth;
/*  99:    */     }
/* 100:200 */     if (getUpdateWeightsOnly()) {
/* 101:201 */       updateWeightsOnly();
/* 102:    */     } else {
/* 103:203 */       updateBoundariesAndWeights();
/* 104:    */     }
/* 105:    */   }
/* 106:    */   
/* 107:    */   protected void updateWeightsOnly()
/* 108:    */     throws IllegalArgumentException
/* 109:    */   {
/* 110:213 */     Iterator<Map.Entry<Double, Double>> itr = this.m_TM.entrySet().iterator();
/* 111:214 */     int j = 1;
/* 112:215 */     this.m_Weights = new double[this.m_Boundaries.length - 1];
/* 113:216 */     while (itr.hasNext())
/* 114:    */     {
/* 115:217 */       Map.Entry<Double, Double> entry = (Map.Entry)itr.next();
/* 116:218 */       double value = ((Double)entry.getKey()).doubleValue();
/* 117:219 */       double weight = ((Double)entry.getValue()).doubleValue();
/* 118:220 */       if ((value < this.m_Boundaries[0]) || (value > this.m_Boundaries[(this.m_Boundaries.length - 1)])) {
/* 119:222 */         throw new IllegalArgumentException("Out-of-range value during weight update");
/* 120:    */       }
/* 121:225 */       while (value > this.m_Boundaries[j]) {
/* 122:226 */         j++;
/* 123:    */       }
/* 124:228 */       this.m_Weights[(j - 1)] += weight;
/* 125:    */     }
/* 126:    */   }
/* 127:    */   
/* 128:    */   protected void updateBoundariesAndWeights()
/* 129:    */   {
/* 130:238 */     double[] values = new double[this.m_TM.size()];
/* 131:239 */     double[] weights = new double[this.m_TM.size()];
/* 132:240 */     Iterator<Map.Entry<Double, Double>> itr = this.m_TM.entrySet().iterator();
/* 133:241 */     int j = 0;
/* 134:242 */     while (itr.hasNext())
/* 135:    */     {
/* 136:243 */       Map.Entry<Double, Double> entry = (Map.Entry)itr.next();
/* 137:244 */       values[j] = ((Double)entry.getKey()).doubleValue();
/* 138:245 */       weights[j] = ((Double)entry.getValue()).doubleValue();
/* 139:246 */       j++;
/* 140:    */     }
/* 141:249 */     double freq = this.m_SumOfWeights / this.m_NumBins;
/* 142:250 */     double[] cutPoints = new double[this.m_NumBins - 1];
/* 143:251 */     double[] binWeights = new double[this.m_NumBins];
/* 144:252 */     double sumOfWeights = this.m_SumOfWeights;
/* 145:    */     
/* 146:    */ 
/* 147:255 */     double weightSumSoFar = 0.0D;double lastWeightSum = 0.0D;
/* 148:256 */     int cpindex = 0;int lastIndex = -1;
/* 149:257 */     for (int i = 0; i < values.length - 1; i++)
/* 150:    */     {
/* 151:260 */       weightSumSoFar += weights[i];
/* 152:261 */       sumOfWeights -= weights[i];
/* 153:264 */       if (weightSumSoFar >= freq)
/* 154:    */       {
/* 155:267 */         if ((freq - lastWeightSum < weightSumSoFar - freq) && (lastIndex != -1))
/* 156:    */         {
/* 157:269 */           cutPoints[cpindex] = ((values[lastIndex] + values[(lastIndex + 1)]) / 2.0D);
/* 158:270 */           weightSumSoFar -= lastWeightSum;
/* 159:271 */           binWeights[cpindex] = lastWeightSum;
/* 160:272 */           lastWeightSum = weightSumSoFar;
/* 161:273 */           lastIndex = i;
/* 162:    */         }
/* 163:    */         else
/* 164:    */         {
/* 165:275 */           cutPoints[cpindex] = ((values[i] + values[(i + 1)]) / 2.0D);
/* 166:276 */           binWeights[cpindex] = weightSumSoFar;
/* 167:277 */           weightSumSoFar = 0.0D;
/* 168:278 */           lastWeightSum = 0.0D;
/* 169:279 */           lastIndex = -1;
/* 170:    */         }
/* 171:281 */         cpindex++;
/* 172:282 */         freq = (sumOfWeights + weightSumSoFar) / (cutPoints.length + 1 - cpindex);
/* 173:    */       }
/* 174:    */       else
/* 175:    */       {
/* 176:285 */         lastIndex = i;
/* 177:286 */         lastWeightSum = weightSumSoFar;
/* 178:    */       }
/* 179:    */     }
/* 180:291 */     if ((cpindex < cutPoints.length) && (lastIndex != -1))
/* 181:    */     {
/* 182:292 */       cutPoints[cpindex] = ((values[lastIndex] + values[(lastIndex + 1)]) / 2.0D);
/* 183:293 */       binWeights[cpindex] = lastWeightSum;
/* 184:294 */       cpindex++;
/* 185:295 */       binWeights[cpindex] = (weightSumSoFar - lastWeightSum);
/* 186:    */     }
/* 187:    */     else
/* 188:    */     {
/* 189:297 */       binWeights[cpindex] = weightSumSoFar;
/* 190:    */     }
/* 191:301 */     if (cpindex == 0)
/* 192:    */     {
/* 193:302 */       this.m_Boundaries = null;
/* 194:303 */       this.m_Weights = null;
/* 195:    */     }
/* 196:    */     else
/* 197:    */     {
/* 198:307 */       binWeights[cpindex] += weights[(values.length - 1)];
/* 199:    */       
/* 200:    */ 
/* 201:310 */       this.m_Boundaries = new double[cpindex + 2];
/* 202:311 */       this.m_Boundaries[0] = ((Double)this.m_TM.firstKey()).doubleValue();
/* 203:312 */       this.m_Boundaries[(cpindex + 1)] = ((Double)this.m_TM.lastKey()).doubleValue();
/* 204:313 */       System.arraycopy(cutPoints, 0, this.m_Boundaries, 1, cpindex);
/* 205:314 */       this.m_Weights = new double[cpindex + 1];
/* 206:315 */       System.arraycopy(binWeights, 0, this.m_Weights, 0, cpindex + 1);
/* 207:    */     }
/* 208:    */   }
/* 209:    */   
/* 210:    */   public double[][] predictIntervals(double conf)
/* 211:    */   {
/* 212:329 */     updateBoundariesAndOrWeights();
/* 213:    */     
/* 214:    */ 
/* 215:332 */     double val = Statistics.normalInverse(1.0D - (1.0D - conf) / 2.0D);
/* 216:333 */     double min = ((Double)this.m_TM.firstKey()).doubleValue() - val * this.m_Width;
/* 217:334 */     double max = ((Double)this.m_TM.lastKey()).doubleValue() + val * this.m_Width;
/* 218:335 */     double delta = (max - min) / this.m_NumIntervals;
/* 219:    */     
/* 220:    */ 
/* 221:338 */     double[] probabilities = new double[this.m_NumIntervals];
/* 222:339 */     double leftVal = Math.exp(logDensity(min));
/* 223:340 */     for (int i = 0; i < this.m_NumIntervals; i++)
/* 224:    */     {
/* 225:341 */       double rightVal = Math.exp(logDensity(min + (i + 1) * delta));
/* 226:342 */       probabilities[i] = (0.5D * (leftVal + rightVal) * delta);
/* 227:343 */       leftVal = rightVal;
/* 228:    */     }
/* 229:347 */     int[] sortedIndices = Utils.sort(probabilities);
/* 230:    */     
/* 231:    */ 
/* 232:350 */     double sum = 0.0D;
/* 233:351 */     boolean[] toUse = new boolean[probabilities.length];
/* 234:352 */     int k = 0;
/* 235:353 */     while ((sum < conf) && (k < toUse.length))
/* 236:    */     {
/* 237:354 */       toUse[sortedIndices[(toUse.length - (k + 1))]] = true;
/* 238:355 */       sum += probabilities[sortedIndices[(toUse.length - (k + 1))]];
/* 239:356 */       k++;
/* 240:    */     }
/* 241:360 */     probabilities = null;
/* 242:    */     
/* 243:    */ 
/* 244:363 */     ArrayList<double[]> intervals = new ArrayList();
/* 245:    */     
/* 246:    */ 
/* 247:366 */     double[] interval = null;
/* 248:    */     
/* 249:    */ 
/* 250:369 */     boolean haveStartedInterval = false;
/* 251:370 */     for (int i = 0; i < this.m_NumIntervals; i++) {
/* 252:373 */       if (toUse[i] != 0)
/* 253:    */       {
/* 254:376 */         if (!haveStartedInterval)
/* 255:    */         {
/* 256:377 */           haveStartedInterval = true;
/* 257:378 */           interval = new double[2];
/* 258:379 */           interval[0] = (min + i * delta);
/* 259:    */         }
/* 260:383 */         interval[1] = (min + (i + 1) * delta);
/* 261:    */       }
/* 262:388 */       else if (haveStartedInterval)
/* 263:    */       {
/* 264:389 */         haveStartedInterval = false;
/* 265:390 */         intervals.add(interval);
/* 266:    */       }
/* 267:    */     }
/* 268:396 */     if (haveStartedInterval) {
/* 269:397 */       intervals.add(interval);
/* 270:    */     }
/* 271:400 */     return (double[][])intervals.toArray(new double[0][0]);
/* 272:    */   }
/* 273:    */   
/* 274:    */   public double predictQuantile(double percentage)
/* 275:    */   {
/* 276:413 */     updateBoundariesAndOrWeights();
/* 277:    */     
/* 278:    */ 
/* 279:416 */     double val = Statistics.normalInverse(0.975D);
/* 280:417 */     double min = ((Double)this.m_TM.firstKey()).doubleValue() - val * this.m_Width;
/* 281:418 */     double max = ((Double)this.m_TM.lastKey()).doubleValue() + val * this.m_Width;
/* 282:419 */     double delta = (max - min) / this.m_NumIntervals;
/* 283:    */     
/* 284:421 */     double sum = 0.0D;
/* 285:422 */     double leftVal = Math.exp(logDensity(min));
/* 286:423 */     for (int i = 0; i < this.m_NumIntervals; i++)
/* 287:    */     {
/* 288:424 */       if (sum >= percentage) {
/* 289:425 */         return min + i * delta;
/* 290:    */       }
/* 291:427 */       double rightVal = Math.exp(logDensity(min + (i + 1) * delta));
/* 292:428 */       sum += 0.5D * (leftVal + rightVal) * delta;
/* 293:429 */       leftVal = rightVal;
/* 294:    */     }
/* 295:431 */     return max;
/* 296:    */   }
/* 297:    */   
/* 298:    */   public double logDensity(double value)
/* 299:    */   {
/* 300:444 */     updateBoundariesAndOrWeights();
/* 301:446 */     if (this.m_Boundaries == null) {
/* 302:447 */       return Math.log(4.9E-324D);
/* 303:    */     }
/* 304:451 */     int index = Arrays.binarySearch(this.m_Boundaries, value);
/* 305:454 */     if ((index == -1) || (index == -this.m_Boundaries.length - 1))
/* 306:    */     {
/* 307:457 */       double val = 0.0D;
/* 308:458 */       if (index == -1) {
/* 309:459 */         val = ((Double)this.m_TM.firstKey()).doubleValue() - value;
/* 310:    */       } else {
/* 311:461 */         val = value - ((Double)this.m_TM.lastKey()).doubleValue();
/* 312:    */       }
/* 313:463 */       return CONST - Math.log(this.m_Width) - 0.5D * (val * val / (this.m_Width * this.m_Width)) - Math.log(this.m_SumOfWeights + 2.0D);
/* 314:    */     }
/* 315:468 */     if (index == this.m_Boundaries.length - 1) {
/* 316:469 */       index--;
/* 317:473 */     } else if (index < 0) {
/* 318:474 */       index = -index - 2;
/* 319:    */     }
/* 320:479 */     double width = this.m_Boundaries[(index + 1)] - this.m_Boundaries[index];
/* 321:    */     
/* 322:    */ 
/* 323:482 */     double densSmearedOut = 1.0D / ((this.m_SumOfWeights + 2.0D) * (this.m_Boundaries[(this.m_Boundaries.length - 1)] - this.m_Boundaries[0]));
/* 324:485 */     if (this.m_Weights[index] <= 0.0D) {
/* 325:491 */       return Math.log(densSmearedOut);
/* 326:    */     }
/* 327:493 */     return Math.log(densSmearedOut + this.m_Weights[index] / ((this.m_SumOfWeights + 2.0D) * width));
/* 328:    */   }
/* 329:    */   
/* 330:    */   public String getRevision()
/* 331:    */   {
/* 332:505 */     return RevisionUtils.extract("$Revision: 11318 $");
/* 333:    */   }
/* 334:    */   
/* 335:    */   public String toString()
/* 336:    */   {
/* 337:514 */     StringBuffer text = new StringBuffer();
/* 338:    */     
/* 339:516 */     text.append("EqualFrequencyHistogram estimator\n\nBandwidth for out of range cases " + this.m_Width + ", total weight " + this.m_SumOfWeights);
/* 340:520 */     if (this.m_Boundaries != null)
/* 341:    */     {
/* 342:521 */       text.append("\nLeft boundary\tRight boundary\tWeight\n");
/* 343:522 */       for (int i = 0; i < this.m_Boundaries.length - 1; i++) {
/* 344:523 */         text.append(this.m_Boundaries[i] + "\t" + this.m_Boundaries[(i + 1)] + "\t" + this.m_Weights[i] + "\t" + Math.exp(logDensity((this.m_Boundaries[(i + 1)] + this.m_Boundaries[i]) / 2.0D)) + "\n");
/* 345:    */       }
/* 346:    */     }
/* 347:530 */     return text.toString();
/* 348:    */   }
/* 349:    */   
/* 350:    */   public static void main(String[] args)
/* 351:    */   {
/* 352:539 */     Random r = new Random();
/* 353:    */     
/* 354:    */ 
/* 355:542 */     UnivariateEqualFrequencyHistogramEstimator e = new UnivariateEqualFrequencyHistogramEstimator();
/* 356:    */     
/* 357:    */ 
/* 358:545 */     System.out.println(e);
/* 359:    */     
/* 360:    */ 
/* 361:548 */     double sum = 0.0D;
/* 362:549 */     for (int i = 0; i < 1000; i++) {
/* 363:550 */       sum += Math.exp(e.logDensity(r.nextDouble() * 10.0D - 5.0D));
/* 364:    */     }
/* 365:552 */     System.out.println("Approximate integral: " + 10.0D * sum / 1000.0D);
/* 366:555 */     for (int i = 0; i < 1000; i++)
/* 367:    */     {
/* 368:556 */       e.addValue(0.1D * r.nextGaussian() - 3.0D, 1.0D);
/* 369:557 */       e.addValue(r.nextGaussian() * 0.25D, 3.0D);
/* 370:    */     }
/* 371:561 */     sum = 0.0D;
/* 372:562 */     int points = 10000000;
/* 373:563 */     for (int i = 0; i < points; i++)
/* 374:    */     {
/* 375:564 */       double value = r.nextDouble() * 20.0D - 10.0D;
/* 376:565 */       sum += Math.exp(e.logDensity(value));
/* 377:    */     }
/* 378:569 */     System.out.println(e);
/* 379:    */     
/* 380:571 */     System.out.println("Approximate integral: " + 20.0D * sum / points);
/* 381:    */     
/* 382:    */ 
/* 383:574 */     double[][] Intervals = e.predictIntervals(0.9D);
/* 384:    */     
/* 385:576 */     System.out.println("Printing histogram intervals ---------------------");
/* 386:578 */     for (double[] interval : Intervals) {
/* 387:579 */       System.out.println("Left: " + interval[0] + "\t Right: " + interval[1]);
/* 388:    */     }
/* 389:582 */     System.out.println("Finished histogram printing intervals ---------------------");
/* 390:    */     
/* 391:    */ 
/* 392:585 */     double Covered = 0.0D;
/* 393:586 */     for (int i = 0; i < 1000; i++)
/* 394:    */     {
/* 395:587 */       double val = -1.0D;
/* 396:588 */       if (r.nextDouble() < 0.25D) {
/* 397:589 */         val = 0.1D * r.nextGaussian() - 3.0D;
/* 398:    */       } else {
/* 399:591 */         val = r.nextGaussian() * 0.25D;
/* 400:    */       }
/* 401:593 */       for (double[] interval : Intervals) {
/* 402:594 */         if ((val >= interval[0]) && (val <= interval[1]))
/* 403:    */         {
/* 404:595 */           Covered += 1.0D;
/* 405:596 */           break;
/* 406:    */         }
/* 407:    */       }
/* 408:    */     }
/* 409:600 */     System.out.println("Coverage at 0.9 level for histogram intervals: " + Covered / 1000.0D);
/* 410:603 */     for (int j = 1; j < 5; j++)
/* 411:    */     {
/* 412:604 */       double numTrain = Math.pow(10.0D, j);
/* 413:605 */       System.out.println("Number of training cases: " + numTrain);
/* 414:    */       
/* 415:    */ 
/* 416:608 */       UnivariateEqualFrequencyHistogramEstimator eHistogram = new UnivariateEqualFrequencyHistogramEstimator();
/* 417:609 */       UnivariateNormalEstimator eNormal = new UnivariateNormalEstimator();
/* 418:612 */       for (int i = 0; i < numTrain; i++)
/* 419:    */       {
/* 420:613 */         double val = r.nextGaussian() * 1.5D + 0.5D;
/* 421:    */         
/* 422:    */ 
/* 423:    */ 
/* 424:617 */         eHistogram.addValue(val, 1.0D);
/* 425:618 */         eNormal.addValue(val, 1.0D);
/* 426:    */       }
/* 427:622 */       sum = 0.0D;
/* 428:623 */       points = 10000000;
/* 429:624 */       for (int i = 0; i < points; i++)
/* 430:    */       {
/* 431:625 */         double value = r.nextDouble() * 20.0D - 10.0D;
/* 432:626 */         sum += Math.exp(eHistogram.logDensity(value));
/* 433:    */       }
/* 434:628 */       System.out.println(eHistogram);
/* 435:629 */       System.out.println("Approximate integral for histogram estimator: " + 20.0D * sum / points);
/* 436:    */       
/* 437:    */ 
/* 438:    */ 
/* 439:633 */       double loglikelihoodHistogram = 0.0D;double loglikelihoodNormal = 0.0D;
/* 440:634 */       for (int i = 0; i < 1000; i++)
/* 441:    */       {
/* 442:635 */         double val = r.nextGaussian() * 1.5D + 0.5D;
/* 443:636 */         loglikelihoodHistogram += eHistogram.logDensity(val);
/* 444:637 */         loglikelihoodNormal += eNormal.logDensity(val);
/* 445:    */       }
/* 446:639 */       System.out.println("Loglikelihood for histogram estimator: " + loglikelihoodHistogram / 1000.0D);
/* 447:    */       
/* 448:641 */       System.out.println("Loglikelihood for normal estimator: " + loglikelihoodNormal / 1000.0D);
/* 449:    */       
/* 450:    */ 
/* 451:    */ 
/* 452:645 */       double[][] histogramIntervals = eHistogram.predictIntervals(0.95D);
/* 453:646 */       double[][] normalIntervals = eNormal.predictIntervals(0.95D);
/* 454:    */       
/* 455:648 */       System.out.println("Printing histogram intervals ---------------------");
/* 456:650 */       for (double[] histogramInterval : histogramIntervals) {
/* 457:651 */         System.out.println("Left: " + histogramInterval[0] + "\t Right: " + histogramInterval[1]);
/* 458:    */       }
/* 459:655 */       System.out.println("Finished histogram printing intervals ---------------------");
/* 460:    */       
/* 461:    */ 
/* 462:658 */       System.out.println("Printing normal intervals ---------------------");
/* 463:660 */       for (double[] normalInterval : normalIntervals) {
/* 464:661 */         System.out.println("Left: " + normalInterval[0] + "\t Right: " + normalInterval[1]);
/* 465:    */       }
/* 466:665 */       System.out.println("Finished normal printing intervals ---------------------");
/* 467:    */       
/* 468:    */ 
/* 469:668 */       double histogramCovered = 0.0D;
/* 470:669 */       double normalCovered = 0.0D;
/* 471:670 */       for (int i = 0; i < 1000; i++)
/* 472:    */       {
/* 473:671 */         double val = r.nextGaussian() * 1.5D + 0.5D;
/* 474:672 */         for (double[] histogramInterval : histogramIntervals) {
/* 475:673 */           if ((val >= histogramInterval[0]) && (val <= histogramInterval[1]))
/* 476:    */           {
/* 477:674 */             histogramCovered += 1.0D;
/* 478:675 */             break;
/* 479:    */           }
/* 480:    */         }
/* 481:678 */         for (double[] normalInterval : normalIntervals) {
/* 482:679 */           if ((val >= normalInterval[0]) && (val <= normalInterval[1]))
/* 483:    */           {
/* 484:680 */             normalCovered += 1.0D;
/* 485:681 */             break;
/* 486:    */           }
/* 487:    */         }
/* 488:    */       }
/* 489:685 */       System.out.println("Coverage at 0.95 level for histogram intervals: " + histogramCovered / 1000.0D);
/* 490:    */       
/* 491:687 */       System.out.println("Coverage at 0.95 level for normal intervals: " + normalCovered / 1000.0D);
/* 492:    */       
/* 493:    */ 
/* 494:690 */       histogramIntervals = eHistogram.predictIntervals(0.8D);
/* 495:691 */       normalIntervals = eNormal.predictIntervals(0.8D);
/* 496:692 */       histogramCovered = 0.0D;
/* 497:693 */       normalCovered = 0.0D;
/* 498:694 */       for (int i = 0; i < 1000; i++)
/* 499:    */       {
/* 500:695 */         double val = r.nextGaussian() * 1.5D + 0.5D;
/* 501:696 */         for (double[] histogramInterval : histogramIntervals) {
/* 502:697 */           if ((val >= histogramInterval[0]) && (val <= histogramInterval[1]))
/* 503:    */           {
/* 504:698 */             histogramCovered += 1.0D;
/* 505:699 */             break;
/* 506:    */           }
/* 507:    */         }
/* 508:702 */         for (double[] normalInterval : normalIntervals) {
/* 509:703 */           if ((val >= normalInterval[0]) && (val <= normalInterval[1]))
/* 510:    */           {
/* 511:704 */             normalCovered += 1.0D;
/* 512:705 */             break;
/* 513:    */           }
/* 514:    */         }
/* 515:    */       }
/* 516:709 */       System.out.println("Coverage at 0.8 level for histogram intervals: " + histogramCovered / 1000.0D);
/* 517:    */       
/* 518:711 */       System.out.println("Coverage at 0.8 level for normal intervals: " + normalCovered / 1000.0D);
/* 519:    */     }
/* 520:    */   }
/* 521:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.estimators.UnivariateEqualFrequencyHistogramEstimator
 * JD-Core Version:    0.7.0.1
 */