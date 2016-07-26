/*   1:    */ package weka.classifiers.functions;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.HashSet;
/*   8:    */ import java.util.Random;
/*   9:    */ import java.util.Set;
/*  10:    */ import java.util.Vector;
/*  11:    */ import java.util.concurrent.Callable;
/*  12:    */ import java.util.concurrent.ExecutorService;
/*  13:    */ import java.util.concurrent.Executors;
/*  14:    */ import java.util.concurrent.Future;
/*  15:    */ import weka.classifiers.RandomizableClassifier;
/*  16:    */ import weka.core.Attribute;
/*  17:    */ import weka.core.Capabilities;
/*  18:    */ import weka.core.Capabilities.Capability;
/*  19:    */ import weka.core.Instance;
/*  20:    */ import weka.core.Instances;
/*  21:    */ import weka.core.Optimization;
/*  22:    */ import weka.core.Option;
/*  23:    */ import weka.core.RevisionUtils;
/*  24:    */ import weka.core.Utils;
/*  25:    */ 
/*  26:    */ public class NonNegativeLogisticRegression
/*  27:    */   extends RandomizableClassifier
/*  28:    */ {
/*  29:    */   static final long serialVersionUID = -1223158323933117974L;
/*  30:    */   protected double[] m_weights;
/*  31:    */   protected Instances m_data;
/*  32:    */   protected double[][] m_matrix;
/*  33:    */   protected int m_numThreads;
/*  34:    */   protected int m_poolSize;
/*  35:    */   protected transient ExecutorService m_Pool;
/*  36:    */   
/*  37:    */   public NonNegativeLogisticRegression()
/*  38:    */   {
/*  39: 97 */     this.m_numThreads = 1;
/*  40:    */     
/*  41:    */ 
/*  42:100 */     this.m_poolSize = 1;
/*  43:    */     
/*  44:    */ 
/*  45:103 */     this.m_Pool = null;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public String globalInfo()
/*  49:    */   {
/*  50:112 */     return "Class for learning a logistic regression model that has non-negative coefficients. The first class value is assumed to be the positive class value (i.e. 1.0).";
/*  51:    */   }
/*  52:    */   
/*  53:    */   public double[] getCoefficients()
/*  54:    */   {
/*  55:121 */     return Arrays.copyOf(this.m_weights, this.m_weights.length);
/*  56:    */   }
/*  57:    */   
/*  58:    */   public String numThreadsTipText()
/*  59:    */   {
/*  60:129 */     return "The number of threads to use, which should be >= size of thread pool.";
/*  61:    */   }
/*  62:    */   
/*  63:    */   public int getNumThreads()
/*  64:    */   {
/*  65:137 */     return this.m_numThreads;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public void setNumThreads(int nT)
/*  69:    */   {
/*  70:145 */     this.m_numThreads = nT;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public String poolSizeTipText()
/*  74:    */   {
/*  75:153 */     return "The size of the thread pool, for example, the number of cores in the CPU.";
/*  76:    */   }
/*  77:    */   
/*  78:    */   public int getPoolSize()
/*  79:    */   {
/*  80:161 */     return this.m_poolSize;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public void setPoolSize(int nT)
/*  84:    */   {
/*  85:169 */     this.m_poolSize = nT;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public Enumeration<Option> listOptions()
/*  89:    */   {
/*  90:180 */     Vector<Option> newVector = new Vector(2);
/*  91:    */     
/*  92:182 */     newVector.addElement(new Option("\t" + poolSizeTipText() + " (default 1)\n", "P", 1, "-P <int>"));
/*  93:    */     
/*  94:184 */     newVector.addElement(new Option("\t" + numThreadsTipText() + " (default 1)\n", "E", 1, "-E <int>"));
/*  95:    */     
/*  96:    */ 
/*  97:187 */     newVector.addAll(Collections.list(super.listOptions()));
/*  98:    */     
/*  99:189 */     return newVector.elements();
/* 100:    */   }
/* 101:    */   
/* 102:    */   public void setOptions(String[] options)
/* 103:    */     throws Exception
/* 104:    */   {
/* 105:232 */     String PoolSize = Utils.getOption('P', options);
/* 106:233 */     if (PoolSize.length() != 0) {
/* 107:234 */       setPoolSize(Integer.parseInt(PoolSize));
/* 108:    */     } else {
/* 109:236 */       setPoolSize(1);
/* 110:    */     }
/* 111:238 */     String NumThreads = Utils.getOption('E', options);
/* 112:239 */     if (NumThreads.length() != 0) {
/* 113:240 */       setNumThreads(Integer.parseInt(NumThreads));
/* 114:    */     } else {
/* 115:242 */       setNumThreads(1);
/* 116:    */     }
/* 117:245 */     super.setOptions(options);
/* 118:    */     
/* 119:247 */     Utils.checkForRemainingOptions(options);
/* 120:    */   }
/* 121:    */   
/* 122:    */   public String[] getOptions()
/* 123:    */   {
/* 124:258 */     Vector<String> options = new Vector();
/* 125:    */     
/* 126:260 */     options.add("-P");
/* 127:261 */     options.add("" + getPoolSize());
/* 128:    */     
/* 129:263 */     options.add("-E");
/* 130:264 */     options.add("" + getNumThreads());
/* 131:    */     
/* 132:266 */     Collections.addAll(options, super.getOptions());
/* 133:    */     
/* 134:268 */     return (String[])options.toArray(new String[0]);
/* 135:    */   }
/* 136:    */   
/* 137:    */   public Capabilities getCapabilities()
/* 138:    */   {
/* 139:278 */     Capabilities result = super.getCapabilities();
/* 140:279 */     result.disableAll();
/* 141:    */     
/* 142:    */ 
/* 143:282 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 144:    */     
/* 145:    */ 
/* 146:285 */     result.enable(Capabilities.Capability.BINARY_CLASS);
/* 147:286 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 148:    */     
/* 149:288 */     return result;
/* 150:    */   }
/* 151:    */   
/* 152:    */   protected class OptEng
/* 153:    */     extends Optimization
/* 154:    */   {
/* 155:    */     protected OptEng() {}
/* 156:    */     
/* 157:    */     protected double objectiveFunction(double[] weights)
/* 158:    */       throws Exception
/* 159:    */     {
/* 160:304 */       NonNegativeLogisticRegression.this.m_weights = weights;
/* 161:    */       
/* 162:    */ 
/* 163:307 */       int chunksize = NonNegativeLogisticRegression.this.m_matrix.length / NonNegativeLogisticRegression.this.m_numThreads;
/* 164:308 */       Set<Future<Double>> results = new HashSet();
/* 165:311 */       for (int j = 0; j < NonNegativeLogisticRegression.this.m_numThreads; j++)
/* 166:    */       {
/* 167:314 */         final int lo = j * chunksize;
/* 168:315 */         final int hi = j < NonNegativeLogisticRegression.this.m_numThreads - 1 ? lo + chunksize : NonNegativeLogisticRegression.this.m_matrix.length;
/* 169:    */         
/* 170:    */ 
/* 171:    */ 
/* 172:319 */         Future<Double> futureNLL = NonNegativeLogisticRegression.this.m_Pool.submit(new Callable()
/* 173:    */         {
/* 174:    */           public Double call()
/* 175:    */           {
/* 176:324 */             double NLL = 0.0D;
/* 177:325 */             for (int i = lo; i < hi; i++)
/* 178:    */             {
/* 179:328 */               double weightedSum = 0.0D;
/* 180:329 */               for (int j = 0; j < NonNegativeLogisticRegression.this.m_matrix[i].length; j++) {
/* 181:330 */                 weightedSum += NonNegativeLogisticRegression.this.m_weights[j] * NonNegativeLogisticRegression.this.m_matrix[i][j];
/* 182:    */               }
/* 183:336 */               NLL -= -weightedSum * NonNegativeLogisticRegression.this.m_matrix[i][NonNegativeLogisticRegression.this.m_data.classIndex()] - Math.log(1.0D + Math.exp(-weightedSum));
/* 184:    */             }
/* 185:339 */             return Double.valueOf(NLL);
/* 186:    */           }
/* 187:341 */         });
/* 188:342 */         results.add(futureNLL);
/* 189:    */       }
/* 190:346 */       double NLL = 0.0D;
/* 191:    */       try
/* 192:    */       {
/* 193:348 */         for (Future<Double> futureNLL : results) {
/* 194:349 */           NLL += ((Double)futureNLL.get()).doubleValue();
/* 195:    */         }
/* 196:    */       }
/* 197:    */       catch (Exception e)
/* 198:    */       {
/* 199:352 */         System.out.println("NLL could not be calculated.");
/* 200:    */       }
/* 201:354 */       return NLL;
/* 202:    */     }
/* 203:    */     
/* 204:    */     protected double[] evaluateGradient(double[] weights)
/* 205:    */       throws Exception
/* 206:    */     {
/* 207:364 */       NonNegativeLogisticRegression.this.m_weights = weights;
/* 208:    */       
/* 209:    */ 
/* 210:367 */       int chunksize = NonNegativeLogisticRegression.this.m_matrix.length / NonNegativeLogisticRegression.this.m_numThreads;
/* 211:368 */       Set<Future<double[]>> results = new HashSet();
/* 212:371 */       for (int j = 0; j < NonNegativeLogisticRegression.this.m_numThreads; j++)
/* 213:    */       {
/* 214:374 */         final int lo = j * chunksize;
/* 215:375 */         final int hi = j < NonNegativeLogisticRegression.this.m_numThreads - 1 ? lo + chunksize : NonNegativeLogisticRegression.this.m_matrix.length;
/* 216:    */         
/* 217:    */ 
/* 218:    */ 
/* 219:379 */         Future<double[]> futureGrad = NonNegativeLogisticRegression.this.m_Pool.submit(new Callable()
/* 220:    */         {
/* 221:    */           public double[] call()
/* 222:    */           {
/* 223:384 */             double[] grad = new double[NonNegativeLogisticRegression.this.m_data.numAttributes()];
/* 224:385 */             for (int i = lo; i < hi; i++)
/* 225:    */             {
/* 226:388 */               double weightedSum = 0.0D;
/* 227:389 */               for (int j = 0; j < NonNegativeLogisticRegression.this.m_matrix[i].length; j++) {
/* 228:390 */                 weightedSum += NonNegativeLogisticRegression.this.m_weights[j] * NonNegativeLogisticRegression.this.m_matrix[i][j];
/* 229:    */               }
/* 230:394 */               double classVal = NonNegativeLogisticRegression.this.m_matrix[i][NonNegativeLogisticRegression.this.m_data.classIndex()];
/* 231:395 */               double expTerm = Math.exp(-weightedSum);
/* 232:396 */               for (int j = 0; j < NonNegativeLogisticRegression.this.m_matrix[i].length; j++) {
/* 233:397 */                 if (j != NonNegativeLogisticRegression.this.m_data.classIndex()) {
/* 234:398 */                   if (classVal == 0.0D) {
/* 235:399 */                     grad[j] -= expTerm / (1.0D + expTerm) * NonNegativeLogisticRegression.this.m_matrix[i][j];
/* 236:    */                   } else {
/* 237:401 */                     grad[j] += 1.0D / (1.0D + expTerm) * NonNegativeLogisticRegression.this.m_matrix[i][j];
/* 238:    */                   }
/* 239:    */                 }
/* 240:    */               }
/* 241:    */             }
/* 242:407 */             return grad;
/* 243:    */           }
/* 244:409 */         });
/* 245:410 */         results.add(futureGrad);
/* 246:    */       }
/* 247:414 */       double[] grad = new double[NonNegativeLogisticRegression.this.m_data.numAttributes()];
/* 248:    */       try
/* 249:    */       {
/* 250:416 */         for (Future<double[]> futureGrad : results)
/* 251:    */         {
/* 252:417 */           double[] lg = (double[])futureGrad.get();
/* 253:418 */           for (int i = 0; i < lg.length; i++) {
/* 254:419 */             grad[i] += lg[i];
/* 255:    */           }
/* 256:    */         }
/* 257:    */       }
/* 258:    */       catch (Exception e)
/* 259:    */       {
/* 260:423 */         System.out.println("Gradient could not be calculated.");
/* 261:    */       }
/* 262:425 */       return grad;
/* 263:    */     }
/* 264:    */     
/* 265:    */     public String getRevision()
/* 266:    */     {
/* 267:433 */       return RevisionUtils.extract("$Revision: 1111 $");
/* 268:    */     }
/* 269:    */   }
/* 270:    */   
/* 271:    */   public void buildClassifier(Instances data)
/* 272:    */     throws Exception
/* 273:    */   {
/* 274:444 */     getCapabilities().testWithFail(data);
/* 275:    */     
/* 276:    */ 
/* 277:447 */     this.m_data = new Instances(data);
/* 278:    */     
/* 279:    */ 
/* 280:450 */     this.m_data.deleteWithMissingClass();
/* 281:    */     
/* 282:    */ 
/* 283:453 */     Random rand = this.m_data.getRandomNumberGenerator(getSeed());
/* 284:    */     
/* 285:    */ 
/* 286:456 */     this.m_data.randomize(rand);
/* 287:    */     
/* 288:    */ 
/* 289:459 */     this.m_matrix = new double[data.numInstances()][data.numAttributes()];
/* 290:460 */     double[] scalingFactors = new double[this.m_data.numAttributes()];
/* 291:461 */     for (int i = 0; i < this.m_data.numInstances(); i++)
/* 292:    */     {
/* 293:462 */       Instance inst = this.m_data.instance(i);
/* 294:463 */       for (int j = 0; j < this.m_data.numAttributes(); j++)
/* 295:    */       {
/* 296:464 */         this.m_matrix[i][j] = inst.value(j);
/* 297:465 */         double absValue = Math.abs(this.m_matrix[i][j]);
/* 298:466 */         if ((i == 0) || (absValue > scalingFactors[j])) {
/* 299:467 */           scalingFactors[j] = absValue;
/* 300:    */         }
/* 301:    */       }
/* 302:    */     }
/* 303:471 */     for (int i = 0; i < this.m_data.numInstances(); i++) {
/* 304:472 */       for (int j = 0; j < this.m_data.numAttributes(); j++) {
/* 305:473 */         if ((scalingFactors[j] > 0.0D) && (j != this.m_data.classIndex())) {
/* 306:474 */           this.m_matrix[i][j] /= scalingFactors[j];
/* 307:    */         }
/* 308:    */       }
/* 309:    */     }
/* 310:480 */     this.m_data = new Instances(this.m_data, 0);
/* 311:    */     
/* 312:    */ 
/* 313:483 */     this.m_weights = new double[this.m_data.numAttributes()];
/* 314:484 */     for (int i = 0; i < this.m_weights.length; i++) {
/* 315:485 */       if ((i != this.m_data.classIndex()) && (scalingFactors[i] > 0.0D)) {
/* 316:486 */         this.m_weights[i] = (1.0D / (this.m_weights.length - 1));
/* 317:    */       }
/* 318:    */     }
/* 319:491 */     double[][] b = new double[2][this.m_weights.length];
/* 320:492 */     for (int p = 0; p < this.m_weights.length; p++)
/* 321:    */     {
/* 322:493 */       if (p == this.m_data.classIndex()) {
/* 323:494 */         b[0][p] = (0.0D / 0.0D);
/* 324:    */       } else {
/* 325:496 */         b[0][p] = 0.0D;
/* 326:    */       }
/* 327:498 */       b[1][p] = (0.0D / 0.0D);
/* 328:    */     }
/* 329:502 */     this.m_Pool = Executors.newFixedThreadPool(this.m_poolSize);
/* 330:    */     
/* 331:    */ 
/* 332:505 */     OptEng opt = new OptEng();
/* 333:506 */     opt.setDebug(this.m_Debug);
/* 334:507 */     this.m_weights = opt.findArgmin(this.m_weights, b);
/* 335:508 */     while (this.m_weights == null)
/* 336:    */     {
/* 337:509 */       this.m_weights = opt.getVarbValues();
/* 338:510 */       if (this.m_Debug) {
/* 339:511 */         System.out.println("First set of iterations finished, not enough!");
/* 340:    */       }
/* 341:513 */       this.m_weights = opt.findArgmin(this.m_weights, b);
/* 342:    */     }
/* 343:517 */     this.m_Pool.shutdown();
/* 344:520 */     for (int i = 0; i < this.m_weights.length; i++) {
/* 345:521 */       if ((i != this.m_data.classIndex()) && (scalingFactors[i] > 0.0D)) {
/* 346:522 */         this.m_weights[i] /= scalingFactors[i];
/* 347:    */       }
/* 348:    */     }
/* 349:527 */     this.m_matrix = ((double[][])null);
/* 350:    */   }
/* 351:    */   
/* 352:    */   public double[] distributionForInstance(Instance inst)
/* 353:    */     throws Exception
/* 354:    */   {
/* 355:536 */     double sum = 0.0D;
/* 356:537 */     for (int i = 0; i < inst.numAttributes(); i++) {
/* 357:538 */       if (i != this.m_data.classIndex()) {
/* 358:539 */         sum += this.m_weights[i] * inst.value(i);
/* 359:    */       }
/* 360:    */     }
/* 361:542 */     double[] dist = new double[2];
/* 362:543 */     dist[0] = (1.0D / (1.0D + Math.exp(-sum)));
/* 363:544 */     dist[1] = (1.0D - dist[0]);
/* 364:545 */     return dist;
/* 365:    */   }
/* 366:    */   
/* 367:    */   public String toString()
/* 368:    */   {
/* 369:554 */     if (this.m_data == null) {
/* 370:555 */       return "Classifier not built yet.";
/* 371:    */     }
/* 372:557 */     String s = "\nlog(x / (1 - x))\t=\n";
/* 373:558 */     for (int i = 0; i < this.m_data.numAttributes(); i++)
/* 374:    */     {
/* 375:559 */       String name = "1";
/* 376:560 */       if (i != this.m_data.classIndex())
/* 377:    */       {
/* 378:561 */         name = this.m_data.attribute(i).name();
/* 379:562 */         if (i > 0) {
/* 380:563 */           s = s + "\t+  ";
/* 381:    */         } else {
/* 382:565 */           s = s + "\t   ";
/* 383:    */         }
/* 384:567 */         s = s + name + "   \t* " + Utils.doubleToString(this.m_weights[i], 6) + "\n";
/* 385:    */       }
/* 386:    */     }
/* 387:570 */     return s;
/* 388:    */   }
/* 389:    */   
/* 390:    */   public static void main(String[] args)
/* 391:    */   {
/* 392:578 */     runClassifier(new NonNegativeLogisticRegression(), args);
/* 393:    */   }
/* 394:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.NonNegativeLogisticRegression
 * JD-Core Version:    0.7.0.1
 */