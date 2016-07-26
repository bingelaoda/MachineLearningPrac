/*   1:    */ package weka.estimators;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import no.uib.cipr.matrix.DenseCholesky;
/*   6:    */ import no.uib.cipr.matrix.DenseMatrix;
/*   7:    */ import no.uib.cipr.matrix.DenseVector;
/*   8:    */ import no.uib.cipr.matrix.Matrices;
/*   9:    */ import no.uib.cipr.matrix.Matrix;
/*  10:    */ import no.uib.cipr.matrix.UpperSPDDenseMatrix;
/*  11:    */ import no.uib.cipr.matrix.UpperTriangDenseMatrix;
/*  12:    */ import no.uib.cipr.matrix.Vector;
/*  13:    */ import weka.core.Utils;
/*  14:    */ 
/*  15:    */ public class MultivariateGaussianEstimator
/*  16:    */   implements MultivariateEstimator, Serializable
/*  17:    */ {
/*  18:    */   protected DenseVector mean;
/*  19:    */   protected UpperSPDDenseMatrix covarianceInverse;
/*  20:    */   protected double lnconstant;
/*  21: 50 */   protected double m_Ridge = 1.0E-006D;
/*  22: 55 */   public static final double Log2PI = Math.log(6.283185307179586D);
/*  23:    */   
/*  24:    */   public String toString()
/*  25:    */   {
/*  26: 62 */     StringBuffer sb = new StringBuffer();
/*  27: 63 */     sb.append("Natural logarithm of normalizing factor: " + this.lnconstant + "\n\n");
/*  28: 64 */     sb.append("Mean vector:\n\n" + this.mean + "\n");
/*  29: 65 */     sb.append("Inverse of covariance matrix:\n\n" + this.covarianceInverse + "\n");
/*  30: 66 */     return sb.toString();
/*  31:    */   }
/*  32:    */   
/*  33:    */   public double[] getMean()
/*  34:    */   {
/*  35: 74 */     return this.mean.getData();
/*  36:    */   }
/*  37:    */   
/*  38:    */   public double logDensity(double[] valuePassed)
/*  39:    */   {
/*  40: 87 */     Vector subtractedMean = new DenseVector(this.mean.size());
/*  41: 88 */     for (int i = 0; i < valuePassed.length; i++) {
/*  42: 89 */       subtractedMean.set(i, valuePassed[i] - this.mean.get(i));
/*  43:    */     }
/*  44: 92 */     return this.lnconstant - 0.5D * subtractedMean.dot(this.covarianceInverse.mult(subtractedMean, new DenseVector(subtractedMean.size())));
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void estimate(double[][] observations, double[] weights)
/*  48:    */   {
/*  49:103 */     if (weights == null)
/*  50:    */     {
/*  51:104 */       weights = new double[observations.length];
/*  52:105 */       for (int i = 0; i < weights.length; i++) {
/*  53:106 */         weights[i] = 1.0D;
/*  54:    */       }
/*  55:    */     }
/*  56:110 */     this.mean = weightedMean(observations, weights);
/*  57:111 */     Matrix cov = weightedCovariance(observations, weights, this.mean);
/*  58:    */     
/*  59:    */ 
/*  60:114 */     DenseCholesky chol = new DenseCholesky(observations[0].length, true).factor((UpperSPDDenseMatrix)cov);
/*  61:115 */     this.covarianceInverse = new UpperSPDDenseMatrix(chol.solve(Matrices.identity(observations[0].length)));
/*  62:    */     
/*  63:117 */     double logDeterminant = 0.0D;
/*  64:118 */     for (int i = 0; i < observations[0].length; i++) {
/*  65:119 */       logDeterminant += Math.log(chol.getU().get(i, i));
/*  66:    */     }
/*  67:121 */     logDeterminant *= 2.0D;
/*  68:122 */     this.lnconstant = (-(Log2PI * observations[0].length + logDeterminant) * 0.5D);
/*  69:    */   }
/*  70:    */   
/*  71:    */   public double[][] estimatePooled(double[][][] observations, double[][] weights)
/*  72:    */   {
/*  73:135 */     int m = -1;
/*  74:136 */     int c = observations.length;
/*  75:137 */     for (int i = 0; i < observations.length; i++) {
/*  76:138 */       if (observations[i].length > 0) {
/*  77:139 */         m = observations[i][0].length;
/*  78:    */       }
/*  79:    */     }
/*  80:142 */     if (m == -1) {
/*  81:143 */       throw new IllegalArgumentException("Cannot compute pooled estimates with no data.");
/*  82:    */     }
/*  83:147 */     Matrix[] groupCovariance = new Matrix[c];
/*  84:148 */     DenseVector[] groupMean = new DenseVector[c];
/*  85:149 */     double[] groupWeights = new double[c];
/*  86:150 */     for (int i = 0; i < groupCovariance.length; i++) {
/*  87:151 */       if (observations[i].length > 0)
/*  88:    */       {
/*  89:152 */         groupMean[i] = weightedMean(observations[i], weights[i]);
/*  90:153 */         groupCovariance[i] = weightedCovariance(observations[i], weights[i], groupMean[i]);
/*  91:154 */         groupWeights[i] = Utils.sum(weights[i]);
/*  92:    */       }
/*  93:    */     }
/*  94:157 */     Utils.normalize(groupWeights);
/*  95:    */     
/*  96:    */ 
/*  97:160 */     double[][] means = new double[c][];
/*  98:161 */     Matrix cov = new UpperSPDDenseMatrix(m);
/*  99:162 */     this.mean = new DenseVector(groupMean[0].size());
/* 100:163 */     for (int i = 0; i < c; i++) {
/* 101:164 */       if (observations[i].length > 0)
/* 102:    */       {
/* 103:165 */         cov = cov.add(groupWeights[i], groupCovariance[i]);
/* 104:166 */         this.mean = ((DenseVector)this.mean.add(groupWeights[i], groupMean[i]));
/* 105:167 */         means[i] = groupMean[i].getData();
/* 106:    */       }
/* 107:    */     }
/* 108:172 */     DenseCholesky chol = new DenseCholesky(m, true).factor((UpperSPDDenseMatrix)cov);
/* 109:173 */     this.covarianceInverse = new UpperSPDDenseMatrix(chol.solve(Matrices.identity(m)));
/* 110:    */     
/* 111:175 */     double logDeterminant = 0.0D;
/* 112:176 */     for (int i = 0; i < m; i++) {
/* 113:177 */       logDeterminant += Math.log(chol.getU().get(i, i));
/* 114:    */     }
/* 115:179 */     logDeterminant *= 2.0D;
/* 116:180 */     this.lnconstant = (-(Log2PI * m + logDeterminant) * 0.5D);
/* 117:    */     
/* 118:182 */     return means;
/* 119:    */   }
/* 120:    */   
/* 121:    */   private DenseVector weightedMean(double[][] matrix, double[] weights)
/* 122:    */   {
/* 123:193 */     int rows = matrix.length;
/* 124:194 */     int cols = matrix[0].length;
/* 125:    */     
/* 126:196 */     DenseVector mean = new DenseVector(cols);
/* 127:    */     
/* 128:    */ 
/* 129:199 */     double sumOfWeights = 0.0D;
/* 130:200 */     for (int i = 0; i < rows; i++)
/* 131:    */     {
/* 132:201 */       double[] row = matrix[i];
/* 133:202 */       double w = weights[i];
/* 134:205 */       for (int j = 0; j < cols; j++) {
/* 135:206 */         mean.add(j, row[j] * w);
/* 136:    */       }
/* 137:208 */       sumOfWeights += w;
/* 138:    */     }
/* 139:211 */     mean.scale(1.0D / sumOfWeights);
/* 140:    */     
/* 141:213 */     return mean;
/* 142:    */   }
/* 143:    */   
/* 144:    */   private UpperSPDDenseMatrix weightedCovariance(double[][] matrix, double[] weights, Vector mean)
/* 145:    */   {
/* 146:226 */     int rows = matrix.length;
/* 147:227 */     int cols = matrix[0].length;
/* 148:229 */     if (mean.size() != cols) {
/* 149:230 */       throw new IllegalArgumentException("Length of the mean vector must match matrix.");
/* 150:    */     }
/* 151:233 */     Matrix covT = new DenseMatrix(cols, cols);
/* 152:234 */     for (int i = 0; i < cols; i++) {
/* 153:235 */       for (int j = i; j < cols; j++)
/* 154:    */       {
/* 155:236 */         double s = 0.0D;
/* 156:237 */         double sumOfWeights = 0.0D;
/* 157:238 */         for (int k = 0; k < rows; k++)
/* 158:    */         {
/* 159:239 */           s += weights[k] * (matrix[k][j] - mean.get(j)) * (matrix[k][i] - mean.get(i));
/* 160:240 */           sumOfWeights += weights[k];
/* 161:    */         }
/* 162:242 */         s /= sumOfWeights;
/* 163:243 */         covT.set(i, j, s);
/* 164:244 */         covT.set(j, i, s);
/* 165:245 */         if (i == j) {
/* 166:246 */           covT.add(i, j, this.m_Ridge);
/* 167:    */         }
/* 168:    */       }
/* 169:    */     }
/* 170:251 */     return new UpperSPDDenseMatrix(covT);
/* 171:    */   }
/* 172:    */   
/* 173:    */   public String ridgeTipText()
/* 174:    */   {
/* 175:261 */     return "The value of the ridge parameter.";
/* 176:    */   }
/* 177:    */   
/* 178:    */   public double getRidge()
/* 179:    */   {
/* 180:271 */     return this.m_Ridge;
/* 181:    */   }
/* 182:    */   
/* 183:    */   public void setRidge(double newRidge)
/* 184:    */   {
/* 185:281 */     this.m_Ridge = newRidge;
/* 186:    */   }
/* 187:    */   
/* 188:    */   public static void main(String[] args)
/* 189:    */   {
/* 190:290 */     double[][] dataset1 = new double[4][1];
/* 191:291 */     dataset1[0][0] = 0.49D;
/* 192:292 */     dataset1[1][0] = 0.46D;
/* 193:293 */     dataset1[2][0] = 0.51D;
/* 194:294 */     dataset1[3][0] = 0.55D;
/* 195:    */     
/* 196:296 */     MultivariateEstimator mv1 = new MultivariateGaussianEstimator();
/* 197:297 */     mv1.estimate(dataset1, new double[] { 0.7D, 0.2D, 0.05D, 0.05D });
/* 198:    */     
/* 199:299 */     System.err.println(mv1);
/* 200:    */     
/* 201:301 */     double integral1 = 0.0D;
/* 202:302 */     int numVals = 1000;
/* 203:303 */     for (int i = 0; i < numVals; i++)
/* 204:    */     {
/* 205:304 */       double[] point = new double[1];
/* 206:305 */       point[0] = ((i + 0.5D) * (1.0D / numVals));
/* 207:306 */       double logdens = mv1.logDensity(point);
/* 208:307 */       if (!Double.isNaN(logdens)) {
/* 209:308 */         integral1 += Math.exp(logdens) * (1.0D / numVals);
/* 210:    */       }
/* 211:    */     }
/* 212:311 */     System.err.println("Approximate integral: " + integral1);
/* 213:    */     
/* 214:313 */     double[][] dataset = new double[4][3];
/* 215:314 */     dataset[0][0] = 0.49D;
/* 216:315 */     dataset[0][1] = 0.51D;
/* 217:316 */     dataset[0][2] = 0.53D;
/* 218:317 */     dataset[1][0] = 0.46D;
/* 219:318 */     dataset[1][1] = 0.47D;
/* 220:319 */     dataset[1][2] = 0.52D;
/* 221:320 */     dataset[2][0] = 0.51D;
/* 222:321 */     dataset[2][1] = 0.49D;
/* 223:322 */     dataset[2][2] = 0.47D;
/* 224:323 */     dataset[3][0] = 0.55D;
/* 225:324 */     dataset[3][1] = 0.52D;
/* 226:325 */     dataset[3][2] = 0.54D;
/* 227:    */     
/* 228:327 */     MultivariateEstimator mv = new MultivariateGaussianEstimator();
/* 229:328 */     mv.estimate(dataset, new double[] { 2.0D, 0.2D, 0.05D, 0.05D });
/* 230:    */     
/* 231:330 */     System.err.println(mv);
/* 232:    */     
/* 233:332 */     double integral = 0.0D;
/* 234:333 */     int numVals2 = 200;
/* 235:334 */     for (int i = 0; i < numVals2; i++) {
/* 236:335 */       for (int j = 0; j < numVals2; j++) {
/* 237:336 */         for (int k = 0; k < numVals2; k++)
/* 238:    */         {
/* 239:337 */           double[] point = new double[3];
/* 240:338 */           point[0] = ((i + 0.5D) * (1.0D / numVals2));
/* 241:339 */           point[1] = ((j + 0.5D) * (1.0D / numVals2));
/* 242:340 */           point[2] = ((k + 0.5D) * (1.0D / numVals2));
/* 243:341 */           double logdens = mv.logDensity(point);
/* 244:342 */           if (!Double.isNaN(logdens)) {
/* 245:343 */             integral += Math.exp(logdens) / (numVals2 * numVals2 * numVals2);
/* 246:    */           }
/* 247:    */         }
/* 248:    */       }
/* 249:    */     }
/* 250:348 */     System.err.println("Approximate integral: " + integral);
/* 251:    */     
/* 252:350 */     double[][] dataset3 = new double[5][3];
/* 253:351 */     dataset3[0][0] = 0.49D;
/* 254:352 */     dataset3[0][1] = 0.51D;
/* 255:353 */     dataset3[0][2] = 0.53D;
/* 256:354 */     dataset3[4][0] = 0.49D;
/* 257:355 */     dataset3[4][1] = 0.51D;
/* 258:356 */     dataset3[4][2] = 0.53D;
/* 259:357 */     dataset3[1][0] = 0.46D;
/* 260:358 */     dataset3[1][1] = 0.47D;
/* 261:359 */     dataset3[1][2] = 0.52D;
/* 262:360 */     dataset3[2][0] = 0.51D;
/* 263:361 */     dataset3[2][1] = 0.49D;
/* 264:362 */     dataset3[2][2] = 0.47D;
/* 265:363 */     dataset3[3][0] = 0.55D;
/* 266:364 */     dataset3[3][1] = 0.52D;
/* 267:365 */     dataset3[3][2] = 0.54D;
/* 268:    */     
/* 269:367 */     MultivariateEstimator mv3 = new MultivariateGaussianEstimator();
/* 270:368 */     mv3.estimate(dataset3, new double[] { 1.0D, 0.2D, 0.05D, 0.05D, 1.0D });
/* 271:    */     
/* 272:370 */     System.err.println(mv3);
/* 273:    */     
/* 274:372 */     double integral3 = 0.0D;
/* 275:373 */     int numVals3 = 200;
/* 276:374 */     for (int i = 0; i < numVals3; i++) {
/* 277:375 */       for (int j = 0; j < numVals3; j++) {
/* 278:376 */         for (int k = 0; k < numVals3; k++)
/* 279:    */         {
/* 280:377 */           double[] point = new double[3];
/* 281:378 */           point[0] = ((i + 0.5D) * (1.0D / numVals3));
/* 282:379 */           point[1] = ((j + 0.5D) * (1.0D / numVals3));
/* 283:380 */           point[2] = ((k + 0.5D) * (1.0D / numVals3));
/* 284:381 */           double logdens = mv.logDensity(point);
/* 285:382 */           if (!Double.isNaN(logdens)) {
/* 286:383 */             integral3 += Math.exp(logdens) / (numVals3 * numVals3 * numVals3);
/* 287:    */           }
/* 288:    */         }
/* 289:    */       }
/* 290:    */     }
/* 291:388 */     System.err.println("Approximate integral: " + integral3);
/* 292:    */     
/* 293:390 */     double[][][] dataset4 = new double[2][][];
/* 294:391 */     dataset4[0] = new double[2][3];
/* 295:392 */     dataset4[1] = new double[3][3];
/* 296:393 */     dataset4[0][0][0] = 0.49D;
/* 297:394 */     dataset4[0][0][1] = 0.51D;
/* 298:395 */     dataset4[0][0][2] = 0.53D;
/* 299:396 */     dataset4[0][1][0] = 0.49D;
/* 300:397 */     dataset4[0][1][1] = 0.51D;
/* 301:398 */     dataset4[0][1][2] = 0.53D;
/* 302:399 */     dataset4[1][0][0] = 0.46D;
/* 303:400 */     dataset4[1][0][1] = 0.47D;
/* 304:401 */     dataset4[1][0][2] = 0.52D;
/* 305:402 */     dataset4[1][1][0] = 0.51D;
/* 306:403 */     dataset4[1][1][1] = 0.49D;
/* 307:404 */     dataset4[1][1][2] = 0.47D;
/* 308:405 */     dataset4[1][2][0] = 0.55D;
/* 309:406 */     dataset4[1][2][1] = 0.52D;
/* 310:407 */     dataset4[1][2][2] = 0.54D;
/* 311:408 */     double[][] weights = new double[2][];
/* 312:409 */     weights[0] = { 1.0D, 3.0D };
/* 313:410 */     weights[1] = { 2.0D, 1.0D, 1.0D };
/* 314:    */     
/* 315:412 */     MultivariateGaussianEstimator mv4 = new MultivariateGaussianEstimator();
/* 316:413 */     mv4.estimatePooled(dataset4, weights);
/* 317:    */     
/* 318:415 */     System.err.println(mv4);
/* 319:    */     
/* 320:417 */     double integral4 = 0.0D;
/* 321:418 */     int numVals4 = 200;
/* 322:419 */     for (int i = 0; i < numVals4; i++) {
/* 323:420 */       for (int j = 0; j < numVals4; j++) {
/* 324:421 */         for (int k = 0; k < numVals4; k++)
/* 325:    */         {
/* 326:422 */           double[] point = new double[3];
/* 327:423 */           point[0] = ((i + 0.5D) * (1.0D / numVals4));
/* 328:424 */           point[1] = ((j + 0.5D) * (1.0D / numVals4));
/* 329:425 */           point[2] = ((k + 0.5D) * (1.0D / numVals4));
/* 330:426 */           double logdens = mv.logDensity(point);
/* 331:427 */           if (!Double.isNaN(logdens)) {
/* 332:428 */             integral4 += Math.exp(logdens) / (numVals4 * numVals4 * numVals4);
/* 333:    */           }
/* 334:    */         }
/* 335:    */       }
/* 336:    */     }
/* 337:433 */     System.err.println("Approximate integral: " + integral4);
/* 338:    */     
/* 339:435 */     double[][][] dataset5 = new double[2][][];
/* 340:436 */     dataset5[0] = new double[4][3];
/* 341:437 */     dataset5[1] = new double[4][3];
/* 342:438 */     dataset5[0][0][0] = 0.49D;
/* 343:439 */     dataset5[0][0][1] = 0.51D;
/* 344:440 */     dataset5[0][0][2] = 0.53D;
/* 345:441 */     dataset5[0][1][0] = 0.49D;
/* 346:442 */     dataset5[0][1][1] = 0.51D;
/* 347:443 */     dataset5[0][1][2] = 0.53D;
/* 348:444 */     dataset5[0][2][0] = 0.49D;
/* 349:445 */     dataset5[0][2][1] = 0.51D;
/* 350:446 */     dataset5[0][2][2] = 0.53D;
/* 351:447 */     dataset5[0][3][0] = 0.49D;
/* 352:448 */     dataset5[0][3][1] = 0.51D;
/* 353:449 */     dataset5[0][3][2] = 0.53D;
/* 354:450 */     dataset5[1][0][0] = 0.46D;
/* 355:451 */     dataset5[1][0][1] = 0.47D;
/* 356:452 */     dataset5[1][0][2] = 0.52D;
/* 357:453 */     dataset5[1][1][0] = 0.46D;
/* 358:454 */     dataset5[1][1][1] = 0.47D;
/* 359:455 */     dataset5[1][1][2] = 0.52D;
/* 360:456 */     dataset5[1][2][0] = 0.51D;
/* 361:457 */     dataset5[1][2][1] = 0.49D;
/* 362:458 */     dataset5[1][2][2] = 0.47D;
/* 363:459 */     dataset5[1][3][0] = 0.55D;
/* 364:460 */     dataset5[1][3][1] = 0.52D;
/* 365:461 */     dataset5[1][3][2] = 0.54D;
/* 366:462 */     double[][] weights2 = new double[2][];
/* 367:463 */     weights2[0] = { 1.0D, 1.0D, 1.0D, 1.0D };
/* 368:464 */     weights2[1] = { 1.0D, 1.0D, 1.0D, 1.0D };
/* 369:    */     
/* 370:466 */     MultivariateGaussianEstimator mv5 = new MultivariateGaussianEstimator();
/* 371:467 */     mv5.estimatePooled(dataset5, weights2);
/* 372:    */     
/* 373:469 */     System.err.println(mv5);
/* 374:    */     
/* 375:471 */     double integral5 = 0.0D;
/* 376:472 */     int numVals5 = 200;
/* 377:473 */     for (int i = 0; i < numVals5; i++) {
/* 378:474 */       for (int j = 0; j < numVals5; j++) {
/* 379:475 */         for (int k = 0; k < numVals5; k++)
/* 380:    */         {
/* 381:476 */           double[] point = new double[3];
/* 382:477 */           point[0] = ((i + 0.5D) * (1.0D / numVals5));
/* 383:478 */           point[1] = ((j + 0.5D) * (1.0D / numVals5));
/* 384:479 */           point[2] = ((k + 0.5D) * (1.0D / numVals5));
/* 385:480 */           double logdens = mv.logDensity(point);
/* 386:481 */           if (!Double.isNaN(logdens)) {
/* 387:482 */             integral5 += Math.exp(logdens) / (numVals5 * numVals5 * numVals5);
/* 388:    */           }
/* 389:    */         }
/* 390:    */       }
/* 391:    */     }
/* 392:487 */     System.err.println("Approximate integral: " + integral5);
/* 393:    */   }
/* 394:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.estimators.MultivariateGaussianEstimator
 * JD-Core Version:    0.7.0.1
 */