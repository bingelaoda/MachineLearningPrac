/*   1:    */ package no.uib.cipr.matrix.sparse;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collection;
/*   5:    */ import java.util.HashMap;
/*   6:    */ import java.util.HashSet;
/*   7:    */ import java.util.Iterator;
/*   8:    */ import java.util.LinkedList;
/*   9:    */ import java.util.List;
/*  10:    */ import java.util.Map;
/*  11:    */ import java.util.Map.Entry;
/*  12:    */ import java.util.Set;
/*  13:    */ import no.uib.cipr.matrix.DenseLU;
/*  14:    */ import no.uib.cipr.matrix.DenseMatrix;
/*  15:    */ import no.uib.cipr.matrix.DenseVector;
/*  16:    */ import no.uib.cipr.matrix.Matrix;
/*  17:    */ import no.uib.cipr.matrix.Vector;
/*  18:    */ 
/*  19:    */ public class AMG
/*  20:    */   implements Preconditioner
/*  21:    */ {
/*  22:    */   private SSOR[] preM;
/*  23:    */   private SSOR[] postM;
/*  24:    */   private int m;
/*  25:    */   private CompRowMatrix[] A;
/*  26:    */   private DenseLU lu;
/*  27:    */   private DenseVector[] u;
/*  28:    */   private DenseVector[] f;
/*  29:    */   private DenseVector[] r;
/*  30:    */   private CompColMatrix[] I;
/*  31:    */   private final int min;
/*  32:    */   private final int nu1;
/*  33:    */   private final int nu2;
/*  34:    */   private final int gamma;
/*  35:    */   private final double omegaPreF;
/*  36:    */   private final double omegaPreR;
/*  37:    */   private final double omegaPostF;
/*  38:    */   private final double omegaPostR;
/*  39:    */   private final boolean reverse;
/*  40:    */   private final double omega;
/*  41:    */   private boolean transpose;
/*  42:    */   
/*  43:    */   public AMG(double omegaPreF, double omegaPreR, double omegaPostF, double omegaPostR, int nu1, int nu2, int gamma, int min, double omega)
/*  44:    */   {
/*  45:143 */     this.omegaPreF = omegaPreF;
/*  46:144 */     this.omegaPreR = omegaPreR;
/*  47:145 */     this.omegaPostF = omegaPostF;
/*  48:146 */     this.omegaPostR = omegaPostR;
/*  49:    */     
/*  50:148 */     this.reverse = true;
/*  51:    */     
/*  52:150 */     this.nu1 = nu1;
/*  53:151 */     this.nu2 = nu2;
/*  54:152 */     this.gamma = gamma;
/*  55:153 */     this.min = min;
/*  56:    */     
/*  57:155 */     this.omega = omega;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public AMG(double omegaPre, double omegaPost, int nu1, int nu2, int gamma, int min, double omega)
/*  61:    */   {
/*  62:181 */     this.omegaPreF = omegaPre;
/*  63:182 */     this.omegaPreR = omegaPre;
/*  64:183 */     this.omegaPostF = omegaPost;
/*  65:184 */     this.omegaPostR = omegaPost;
/*  66:    */     
/*  67:186 */     this.reverse = false;
/*  68:    */     
/*  69:188 */     this.nu1 = nu1;
/*  70:189 */     this.nu2 = nu2;
/*  71:190 */     this.gamma = gamma;
/*  72:191 */     this.min = min;
/*  73:    */     
/*  74:193 */     this.omega = omega;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public AMG()
/*  78:    */   {
/*  79:205 */     this(1.0D, 1.85D, 1.85D, 1.0D, 1, 1, 1, 40, 0.6666666666666666D);
/*  80:    */   }
/*  81:    */   
/*  82:    */   public Vector apply(Vector b, Vector x)
/*  83:    */   {
/*  84:209 */     this.u[0].set(x);
/*  85:210 */     this.f[0].set(b);
/*  86:    */     
/*  87:212 */     this.transpose = false;
/*  88:213 */     cycle(0);
/*  89:    */     
/*  90:215 */     return x.set(this.u[0]);
/*  91:    */   }
/*  92:    */   
/*  93:    */   public Vector transApply(Vector b, Vector x)
/*  94:    */   {
/*  95:219 */     this.u[0].set(x);
/*  96:220 */     this.f[0].set(b);
/*  97:    */     
/*  98:222 */     this.transpose = true;
/*  99:223 */     cycle(0);
/* 100:    */     
/* 101:225 */     return x.set(this.u[0]);
/* 102:    */   }
/* 103:    */   
/* 104:    */   public void setMatrix(Matrix A)
/* 105:    */   {
/* 106:229 */     List<CompRowMatrix> Al = new LinkedList();
/* 107:230 */     List<CompColMatrix> Il = new LinkedList();
/* 108:    */     
/* 109:232 */     Al.add(new CompRowMatrix(A));
/* 110:234 */     for (int k = 0; ((CompRowMatrix)Al.get(k)).numRows() > this.min; k++)
/* 111:    */     {
/* 112:236 */       CompRowMatrix Af = (CompRowMatrix)Al.get(k);
/* 113:    */       
/* 114:238 */       double eps = 0.08D * Math.pow(0.5D, k);
/* 115:    */       
/* 116:    */ 
/* 117:241 */       Aggregator aggregator = new Aggregator(Af, eps);
/* 118:245 */       if (aggregator.getAggregates().size() == 0) {
/* 119:    */         break;
/* 120:    */       }
/* 121:250 */       Interpolator sa = new Interpolator(aggregator, Af, this.omega);
/* 122:    */       
/* 123:252 */       Al.add(sa.getGalerkinOperator());
/* 124:253 */       Il.add(sa.getInterpolationOperator());
/* 125:    */     }
/* 126:257 */     this.m = Al.size();
/* 127:258 */     if (this.m == 0) {
/* 128:259 */       throw new RuntimeException("Matrix too small for AMG");
/* 129:    */     }
/* 130:261 */     this.I = new CompColMatrix[this.m - 1];
/* 131:262 */     this.A = new CompRowMatrix[this.m - 1];
/* 132:    */     
/* 133:264 */     Il.toArray(this.I);
/* 134:265 */     for (int i = 0; i < Al.size() - 1; i++) {
/* 135:266 */       this.A[i] = ((CompRowMatrix)Al.get(i));
/* 136:    */     }
/* 137:269 */     DenseMatrix Ac = new DenseMatrix((Matrix)Al.get(Al.size() - 1));
/* 138:270 */     this.lu = new DenseLU(Ac.numRows(), Ac.numColumns());
/* 139:271 */     this.lu.factor(Ac);
/* 140:    */     
/* 141:    */ 
/* 142:274 */     this.u = new DenseVector[this.m];
/* 143:275 */     this.f = new DenseVector[this.m];
/* 144:276 */     this.r = new DenseVector[this.m];
/* 145:277 */     for (int k = 0; k < this.m; k++)
/* 146:    */     {
/* 147:278 */       int n = ((CompRowMatrix)Al.get(k)).numRows();
/* 148:279 */       this.u[k] = new DenseVector(n);
/* 149:280 */       this.f[k] = new DenseVector(n);
/* 150:281 */       this.r[k] = new DenseVector(n);
/* 151:    */     }
/* 152:285 */     this.preM = new SSOR[this.m - 1];
/* 153:286 */     this.postM = new SSOR[this.m - 1];
/* 154:287 */     for (int k = 0; k < this.m - 1; k++)
/* 155:    */     {
/* 156:288 */       CompRowMatrix Ak = this.A[k];
/* 157:289 */       this.preM[k] = new SSOR(Ak, this.reverse, this.omegaPreF, this.omegaPreR);
/* 158:290 */       this.postM[k] = new SSOR(Ak, this.reverse, this.omegaPostF, this.omegaPostR);
/* 159:291 */       this.preM[k].setMatrix(Ak);
/* 160:292 */       this.postM[k].setMatrix(Ak);
/* 161:    */     }
/* 162:    */   }
/* 163:    */   
/* 164:    */   private void cycle(int k)
/* 165:    */   {
/* 166:303 */     if (k == this.m - 1)
/* 167:    */     {
/* 168:304 */       directSolve();
/* 169:    */     }
/* 170:    */     else
/* 171:    */     {
/* 172:308 */       preRelax(k);
/* 173:    */       
/* 174:310 */       this.u[(k + 1)].zero();
/* 175:    */       
/* 176:    */ 
/* 177:313 */       this.A[k].multAdd(-1.0D, this.u[k], this.r[k].set(this.f[k]));
/* 178:    */       
/* 179:    */ 
/* 180:316 */       this.I[k].transMult(this.r[k], this.f[(k + 1)]);
/* 181:319 */       for (int i = 0; i < this.gamma; i++) {
/* 182:320 */         cycle(k + 1);
/* 183:    */       }
/* 184:323 */       this.I[k].multAdd(this.u[(k + 1)], this.u[k]);
/* 185:    */       
/* 186:    */ 
/* 187:326 */       postRelax(k);
/* 188:    */     }
/* 189:    */   }
/* 190:    */   
/* 191:    */   private void directSolve()
/* 192:    */   {
/* 193:334 */     int k = this.m - 1;
/* 194:335 */     this.u[k].set(this.f[k]);
/* 195:336 */     DenseMatrix U = new DenseMatrix(this.u[k], false);
/* 196:338 */     if (this.transpose) {
/* 197:339 */       this.lu.transSolve(U);
/* 198:    */     } else {
/* 199:341 */       this.lu.solve(U);
/* 200:    */     }
/* 201:    */   }
/* 202:    */   
/* 203:    */   private void preRelax(int k)
/* 204:    */   {
/* 205:351 */     for (int i = 0; i < this.nu1; i++) {
/* 206:352 */       if (this.transpose) {
/* 207:353 */         this.preM[k].transApply(this.f[k], this.u[k]);
/* 208:    */       } else {
/* 209:355 */         this.preM[k].apply(this.f[k], this.u[k]);
/* 210:    */       }
/* 211:    */     }
/* 212:    */   }
/* 213:    */   
/* 214:    */   private void postRelax(int k)
/* 215:    */   {
/* 216:365 */     for (int i = 0; i < this.nu2; i++) {
/* 217:366 */       if (this.transpose) {
/* 218:367 */         this.postM[k].transApply(this.f[k], this.u[k]);
/* 219:    */       } else {
/* 220:369 */         this.postM[k].apply(this.f[k], this.u[k]);
/* 221:    */       }
/* 222:    */     }
/* 223:    */   }
/* 224:    */   
/* 225:    */   private static class Aggregator
/* 226:    */   {
/* 227:    */     private List<Set<Integer>> C;
/* 228:    */     private int[] diagind;
/* 229:    */     private List<Set<Integer>> N;
/* 230:    */     
/* 231:    */     public Aggregator(CompRowMatrix A, double eps)
/* 232:    */     {
/* 233:404 */       this.diagind = findDiagonalIndices(A);
/* 234:405 */       this.N = findNodeNeighborhood(A, this.diagind, eps);
/* 235:    */       
/* 236:    */ 
/* 237:    */ 
/* 238:    */ 
/* 239:    */ 
/* 240:411 */       boolean[] R = createInitialR(A);
/* 241:    */       
/* 242:    */ 
/* 243:    */ 
/* 244:    */ 
/* 245:    */ 
/* 246:    */ 
/* 247:418 */       this.C = createInitialAggregates(this.N, R);
/* 248:    */       
/* 249:    */ 
/* 250:    */ 
/* 251:    */ 
/* 252:    */ 
/* 253:    */ 
/* 254:425 */       this.C = enlargeAggregates(this.C, this.N, R);
/* 255:    */       
/* 256:    */ 
/* 257:    */ 
/* 258:    */ 
/* 259:    */ 
/* 260:    */ 
/* 261:432 */       this.C = createFinalAggregates(this.C, this.N, R);
/* 262:    */     }
/* 263:    */     
/* 264:    */     public List<Set<Integer>> getAggregates()
/* 265:    */     {
/* 266:439 */       return this.C;
/* 267:    */     }
/* 268:    */     
/* 269:    */     public int[] getDiagonalIndices()
/* 270:    */     {
/* 271:447 */       return this.diagind;
/* 272:    */     }
/* 273:    */     
/* 274:    */     public List<Set<Integer>> getNodeNeighborhoods()
/* 275:    */     {
/* 276:455 */       return this.N;
/* 277:    */     }
/* 278:    */     
/* 279:    */     private int[] findDiagonalIndices(CompRowMatrix A)
/* 280:    */     {
/* 281:462 */       int[] rowptr = A.getRowPointers();
/* 282:463 */       int[] colind = A.getColumnIndices();
/* 283:    */       
/* 284:465 */       int[] diagind = new int[A.numRows()];
/* 285:467 */       for (int i = 0; i < A.numRows(); i++)
/* 286:    */       {
/* 287:468 */         diagind[i] = Arrays.binarySearch(colind, i, rowptr[i], rowptr[(i + 1)]);
/* 288:470 */         if (diagind[i] < 0) {
/* 289:471 */           throw new RuntimeException("Matrix is missing a diagonal entry on row " + (i + 1));
/* 290:    */         }
/* 291:    */       }
/* 292:476 */       return diagind;
/* 293:    */     }
/* 294:    */     
/* 295:    */     private List<Set<Integer>> findNodeNeighborhood(CompRowMatrix A, int[] diagind, double eps)
/* 296:    */     {
/* 297:485 */       this.N = new ArrayList(A.numRows());
/* 298:    */       
/* 299:487 */       int[] rowptr = A.getRowPointers();
/* 300:488 */       int[] colind = A.getColumnIndices();
/* 301:489 */       double[] data = A.getData();
/* 302:491 */       for (int i = 0; i < A.numRows(); i++)
/* 303:    */       {
/* 304:492 */         Set<Integer> Ni = new HashSet();
/* 305:    */         
/* 306:494 */         double aii = data[diagind[i]];
/* 307:495 */         for (int j = rowptr[i]; j < rowptr[(i + 1)]; j++)
/* 308:    */         {
/* 309:496 */           double aij = data[j];
/* 310:497 */           double ajj = data[diagind[colind[j]]];
/* 311:499 */           if (Math.abs(aij) >= eps * Math.sqrt(aii * ajj)) {
/* 312:500 */             Ni.add(Integer.valueOf(colind[j]));
/* 313:    */           }
/* 314:    */         }
/* 315:503 */         this.N.add(Ni);
/* 316:    */       }
/* 317:506 */       return this.N;
/* 318:    */     }
/* 319:    */     
/* 320:    */     private boolean[] createInitialR(CompRowMatrix A)
/* 321:    */     {
/* 322:513 */       boolean[] R = new boolean[A.numRows()];
/* 323:    */       
/* 324:515 */       int[] rowptr = A.getRowPointers();
/* 325:516 */       int[] colind = A.getColumnIndices();
/* 326:517 */       double[] data = A.getData();
/* 327:519 */       for (int i = 0; i < A.numRows(); i++)
/* 328:    */       {
/* 329:520 */         boolean hasOffDiagonal = false;
/* 330:522 */         for (int j = rowptr[i]; j < rowptr[(i + 1)]; j++) {
/* 331:523 */           if ((colind[j] != i) && (data[j] != 0.0D))
/* 332:    */           {
/* 333:524 */             hasOffDiagonal = true;
/* 334:525 */             break;
/* 335:    */           }
/* 336:    */         }
/* 337:528 */         R[i] = hasOffDiagonal;
/* 338:    */       }
/* 339:531 */       return R;
/* 340:    */     }
/* 341:    */     
/* 342:    */     private List<Set<Integer>> createInitialAggregates(List<Set<Integer>> N, boolean[] R)
/* 343:    */     {
/* 344:539 */       this.C = new ArrayList();
/* 345:    */       Iterator localIterator;
/* 346:541 */       for (int i = 0; i < R.length; i++) {
/* 347:544 */         if (R[i] != 0)
/* 348:    */         {
/* 349:548 */           boolean free = true;
/* 350:549 */           for (localIterator = ((Set)N.get(i)).iterator(); localIterator.hasNext();)
/* 351:    */           {
/* 352:549 */             int j = ((Integer)localIterator.next()).intValue();
/* 353:550 */             free &= R[j];
/* 354:    */           }
/* 355:553 */           if (free)
/* 356:    */           {
/* 357:554 */             this.C.add(new HashSet((Collection)N.get(i)));
/* 358:555 */             for (localIterator = ((Set)N.get(i)).iterator(); localIterator.hasNext();)
/* 359:    */             {
/* 360:555 */               int j = ((Integer)localIterator.next()).intValue();
/* 361:556 */               R[j] = false;
/* 362:    */             }
/* 363:    */           }
/* 364:    */         }
/* 365:    */       }
/* 366:561 */       return this.C;
/* 367:    */     }
/* 368:    */     
/* 369:    */     private List<Set<Integer>> enlargeAggregates(List<Set<Integer>> C, List<Set<Integer>> N, boolean[] R)
/* 370:    */     {
/* 371:571 */       List<List<Integer>> belong = new ArrayList(R.length);
/* 372:572 */       for (int i = 0; i < R.length; i++) {
/* 373:573 */         belong.add(new ArrayList());
/* 374:    */       }
/* 375:    */       Iterator localIterator1;
/* 376:577 */       for (int k = 0; k < C.size(); k++) {
/* 377:578 */         for (localIterator1 = ((Set)C.get(k)).iterator(); localIterator1.hasNext();)
/* 378:    */         {
/* 379:578 */           int j = ((Integer)localIterator1.next()).intValue();
/* 380:579 */           ((List)belong.get(j)).add(Integer.valueOf(k));
/* 381:    */         }
/* 382:    */       }
/* 383:582 */       int[] intersect = new int[C.size()];
/* 384:584 */       for (int i = 0; i < R.length; i++) {
/* 385:587 */         if (R[i] != 0)
/* 386:    */         {
/* 387:592 */           java.util.Arrays.fill(intersect, 0);
/* 388:593 */           int largest = 0;int maxValue = 0;
/* 389:594 */           for (Iterator localIterator2 = ((Set)N.get(i)).iterator(); localIterator2.hasNext();)
/* 390:    */           {
/* 391:594 */             int j = ((Integer)localIterator2.next()).intValue();
/* 392:597 */             for (localIterator3 = ((List)belong.get(j)).iterator(); localIterator3.hasNext();)
/* 393:    */             {
/* 394:597 */               int k = ((Integer)localIterator3.next()).intValue();
/* 395:598 */               intersect[k] += 1;
/* 396:599 */               if (intersect[k] > maxValue)
/* 397:    */               {
/* 398:600 */                 largest = k;
/* 399:601 */                 maxValue = intersect[largest];
/* 400:    */               }
/* 401:    */             }
/* 402:    */           }
/* 403:    */           Iterator localIterator3;
/* 404:607 */           if (maxValue > 0)
/* 405:    */           {
/* 406:608 */             R[i] = false;
/* 407:609 */             ((Set)C.get(largest)).add(Integer.valueOf(i));
/* 408:    */           }
/* 409:    */         }
/* 410:    */       }
/* 411:613 */       return C;
/* 412:    */     }
/* 413:    */     
/* 414:    */     private List<Set<Integer>> createFinalAggregates(List<Set<Integer>> C, List<Set<Integer>> N, boolean[] R)
/* 415:    */     {
/* 416:622 */       for (int i = 0; i < R.length; i++) {
/* 417:625 */         if (R[i] != 0)
/* 418:    */         {
/* 419:629 */           Set<Integer> Cn = new HashSet();
/* 420:630 */           for (Iterator localIterator = ((Set)N.get(i)).iterator(); localIterator.hasNext();)
/* 421:    */           {
/* 422:630 */             int j = ((Integer)localIterator.next()).intValue();
/* 423:631 */             if (R[j] != 0)
/* 424:    */             {
/* 425:632 */               R[j] = false;
/* 426:633 */               Cn.add(Integer.valueOf(j));
/* 427:    */             }
/* 428:    */           }
/* 429:636 */           if (!Cn.isEmpty()) {
/* 430:637 */             C.add(Cn);
/* 431:    */           }
/* 432:    */         }
/* 433:    */       }
/* 434:640 */       return C;
/* 435:    */     }
/* 436:    */   }
/* 437:    */   
/* 438:    */   private static class Interpolator
/* 439:    */   {
/* 440:    */     private CompRowMatrix Ac;
/* 441:    */     private CompColMatrix I;
/* 442:    */     
/* 443:    */     public Interpolator(AMG.Aggregator aggregator, CompRowMatrix A, double omega)
/* 444:    */     {
/* 445:673 */       List<Set<Integer>> C = aggregator.getAggregates();
/* 446:674 */       List<Set<Integer>> N = aggregator.getNodeNeighborhoods();
/* 447:675 */       int[] diagind = aggregator.getDiagonalIndices();
/* 448:    */       
/* 449:    */ 
/* 450:678 */       int[] pt = createTentativeProlongation(C, A.numRows());
/* 451:684 */       if (omega != 0.0D)
/* 452:    */       {
/* 453:687 */         List<Map<Integer, Double>> P = createSmoothedProlongation(C, N, A, diagind, omega, pt);
/* 454:    */         
/* 455:    */ 
/* 456:    */ 
/* 457:691 */         this.I = createInterpolationMatrix(P, A.numRows());
/* 458:    */         
/* 459:    */ 
/* 460:694 */         this.Ac = createGalerkinSlow(this.I, A);
/* 461:    */       }
/* 462:    */       else
/* 463:    */       {
/* 464:704 */         this.Ac = createGalerkinFast(A, pt, C.size());
/* 465:    */         
/* 466:    */ 
/* 467:707 */         this.I = createInterpolationMatrix(pt, C.size());
/* 468:    */       }
/* 469:    */     }
/* 470:    */     
/* 471:    */     private int[] createTentativeProlongation(List<Set<Integer>> C, int n)
/* 472:    */     {
/* 473:718 */       int[] pt = new int[n];
/* 474:719 */       java.util.Arrays.fill(pt, -1);
/* 475:    */       Iterator localIterator;
/* 476:721 */       for (int i = 0; i < C.size(); i++) {
/* 477:722 */         for (localIterator = ((Set)C.get(i)).iterator(); localIterator.hasNext();)
/* 478:    */         {
/* 479:722 */           int j = ((Integer)localIterator.next()).intValue();
/* 480:723 */           pt[j] = i;
/* 481:    */         }
/* 482:    */       }
/* 483:725 */       return pt;
/* 484:    */     }
/* 485:    */     
/* 486:    */     private CompRowMatrix createGalerkinFast(CompRowMatrix A, int[] pt, int c)
/* 487:    */     {
/* 488:734 */       int n = pt.length;
/* 489:    */       
/* 490:736 */       FlexCompRowMatrix Ac = new FlexCompRowMatrix(c, c);
/* 491:    */       
/* 492:738 */       int[] rowptr = A.getRowPointers();
/* 493:739 */       int[] colind = A.getColumnIndices();
/* 494:740 */       double[] data = A.getData();
/* 495:742 */       for (int i = 0; i < n; i++) {
/* 496:743 */         if (pt[i] != -1) {
/* 497:744 */           for (int j = rowptr[i]; j < rowptr[(i + 1)]; j++) {
/* 498:745 */             if (pt[colind[j]] != -1) {
/* 499:746 */               Ac.add(pt[i], pt[colind[j]], data[j]);
/* 500:    */             }
/* 501:    */           }
/* 502:    */         }
/* 503:    */       }
/* 504:748 */       return new CompRowMatrix(Ac);
/* 505:    */     }
/* 506:    */     
/* 507:    */     private CompColMatrix createInterpolationMatrix(List<Map<Integer, Double>> P, int n)
/* 508:    */     {
/* 509:759 */       int c = P.size();
/* 510:760 */       int[][] nz = new int[c][];
/* 511:    */       int l;
/* 512:    */       Iterator localIterator;
/* 513:761 */       for (int j = 0; j < c; j++)
/* 514:    */       {
/* 515:763 */         Map<Integer, Double> Pj = (Map)P.get(j);
/* 516:764 */         nz[j] = new int[Pj.size()];
/* 517:    */         
/* 518:766 */         l = 0;
/* 519:767 */         for (localIterator = Pj.keySet().iterator(); localIterator.hasNext();)
/* 520:    */         {
/* 521:767 */           int k = ((Integer)localIterator.next()).intValue();
/* 522:768 */           nz[j][(l++)] = k;
/* 523:    */         }
/* 524:    */       }
/* 525:771 */       this.I = new CompColMatrix(n, c, nz);
/* 526:774 */       for (int j = 0; j < c; j++)
/* 527:    */       {
/* 528:776 */         Map<Integer, Double> Pj = (Map)P.get(j);
/* 529:778 */         for (Object e : Pj.entrySet()) {
/* 530:779 */           this.I.set(((Integer)((Map.Entry)e).getKey()).intValue(), j, ((Double)((Map.Entry)e).getValue()).doubleValue());
/* 531:    */         }
/* 532:    */       }
/* 533:782 */       return this.I;
/* 534:    */     }
/* 535:    */     
/* 536:    */     private CompColMatrix createInterpolationMatrix(int[] pt, int c)
/* 537:    */     {
/* 538:790 */       FlexCompColMatrix If = new FlexCompColMatrix(pt.length, c);
/* 539:792 */       for (int i = 0; i < pt.length; i++) {
/* 540:793 */         if (pt[i] != -1) {
/* 541:794 */           If.set(i, pt[i], 1.0D);
/* 542:    */         }
/* 543:    */       }
/* 544:796 */       return new CompColMatrix(If);
/* 545:    */     }
/* 546:    */     
/* 547:    */     public CompColMatrix getInterpolationOperator()
/* 548:    */     {
/* 549:803 */       return this.I;
/* 550:    */     }
/* 551:    */     
/* 552:    */     private List<Map<Integer, Double>> createSmoothedProlongation(List<Set<Integer>> C, List<Set<Integer>> N, CompRowMatrix A, int[] diagind, double omega, int[] pt)
/* 553:    */     {
/* 554:814 */       int n = A.numRows();int c = C.size();
/* 555:    */       
/* 556:    */ 
/* 557:    */ 
/* 558:818 */       List<Map<Integer, Double>> P = new ArrayList(c);
/* 559:820 */       for (int i = 0; i < c; i++) {
/* 560:821 */         P.add(new HashMap());
/* 561:    */       }
/* 562:823 */       int[] rowptr = A.getRowPointers();
/* 563:824 */       int[] colind = A.getColumnIndices();
/* 564:825 */       double[] data = A.getData();
/* 565:    */       
/* 566:827 */       double[] dot = new double[c];
/* 567:830 */       for (int i = 0; i < n; i++) {
/* 568:832 */         if (pt[i] != -1)
/* 569:    */         {
/* 570:835 */           java.util.Arrays.fill(dot, 0.0D);
/* 571:836 */           Set<Integer> Ni = (Set)N.get(i);
/* 572:    */           
/* 573:    */ 
/* 574:839 */           double weakAij = 0.0D;
/* 575:840 */           for (int j = rowptr[i]; j < rowptr[(i + 1)]; j++) {
/* 576:842 */             if (pt[colind[j]] != -1)
/* 577:    */             {
/* 578:845 */               double aij = data[j];
/* 579:849 */               if ((aij != 0.0D) && (!Ni.contains(Integer.valueOf(colind[j])))) {
/* 580:850 */                 weakAij += aij;
/* 581:    */               } else {
/* 582:854 */                 dot[pt[colind[j]]] += aij;
/* 583:    */               }
/* 584:    */             }
/* 585:    */           }
/* 586:858 */           dot[pt[i]] -= weakAij;
/* 587:    */           
/* 588:    */ 
/* 589:861 */           double scale = -omega / data[diagind[i]];
/* 590:862 */           for (int j = 0; j < dot.length; j++) {
/* 591:863 */             dot[j] *= scale;
/* 592:    */           }
/* 593:866 */           dot[pt[i]] += 1.0D;
/* 594:870 */           for (int j = 0; j < dot.length; j++) {
/* 595:871 */             if (dot[j] != 0.0D) {
/* 596:872 */               ((Map)P.get(j)).put(Integer.valueOf(i), Double.valueOf(dot[j]));
/* 597:    */             }
/* 598:    */           }
/* 599:    */         }
/* 600:    */       }
/* 601:875 */       return P;
/* 602:    */     }
/* 603:    */     
/* 604:    */     private CompRowMatrix createGalerkinSlow(CompColMatrix I, CompRowMatrix A)
/* 605:    */     {
/* 606:885 */       int n = I.numRows();int c = I.numColumns();
/* 607:886 */       FlexCompRowMatrix Ac = new FlexCompRowMatrix(c, c);
/* 608:    */       
/* 609:888 */       double[] aiCol = new double[n];
/* 610:889 */       double[] iCol = new double[n];
/* 611:890 */       DenseVector aiV = new DenseVector(aiCol, false);
/* 612:891 */       DenseVector iV = new DenseVector(iCol, false);
/* 613:892 */       double[] itaiCol = new double[c];
/* 614:893 */       DenseVector itaiV = new DenseVector(itaiCol, false);
/* 615:    */       
/* 616:895 */       int[] colptr = I.getColumnPointers();
/* 617:896 */       int[] rowind = I.getRowIndices();
/* 618:897 */       double[] Idata = I.getData();
/* 619:899 */       for (int k = 0; k < c; k++)
/* 620:    */       {
/* 621:902 */         iV.zero();
/* 622:903 */         for (int i = colptr[k]; i < colptr[(k + 1)]; i++) {
/* 623:904 */           iCol[rowind[i]] = Idata[i];
/* 624:    */         }
/* 625:907 */         A.mult(iV, aiV);
/* 626:    */         
/* 627:    */ 
/* 628:910 */         I.transMult(aiV, itaiV);
/* 629:913 */         for (int i = 0; i < c; i++) {
/* 630:914 */           if (itaiCol[i] != 0.0D) {
/* 631:915 */             Ac.set(i, k, itaiCol[i]);
/* 632:    */           }
/* 633:    */         }
/* 634:    */       }
/* 635:918 */       return new CompRowMatrix(Ac);
/* 636:    */     }
/* 637:    */     
/* 638:    */     public CompRowMatrix getGalerkinOperator()
/* 639:    */     {
/* 640:925 */       return this.Ac;
/* 641:    */     }
/* 642:    */   }
/* 643:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.AMG
 * JD-Core Version:    0.7.0.1
 */