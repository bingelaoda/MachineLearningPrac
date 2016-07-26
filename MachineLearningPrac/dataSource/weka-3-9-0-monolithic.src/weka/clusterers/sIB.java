/*    1:     */ package weka.clusterers;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.io.Serializable;
/*    5:     */ import java.util.ArrayList;
/*    6:     */ import java.util.Collections;
/*    7:     */ import java.util.Enumeration;
/*    8:     */ import java.util.Random;
/*    9:     */ import java.util.Vector;
/*   10:     */ import weka.core.Attribute;
/*   11:     */ import weka.core.Capabilities;
/*   12:     */ import weka.core.Capabilities.Capability;
/*   13:     */ import weka.core.DenseInstance;
/*   14:     */ import weka.core.Instance;
/*   15:     */ import weka.core.Instances;
/*   16:     */ import weka.core.Option;
/*   17:     */ import weka.core.RevisionHandler;
/*   18:     */ import weka.core.RevisionUtils;
/*   19:     */ import weka.core.TechnicalInformation;
/*   20:     */ import weka.core.TechnicalInformation.Field;
/*   21:     */ import weka.core.TechnicalInformation.Type;
/*   22:     */ import weka.core.TechnicalInformationHandler;
/*   23:     */ import weka.core.Utils;
/*   24:     */ import weka.core.matrix.Matrix;
/*   25:     */ import weka.filters.Filter;
/*   26:     */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*   27:     */ 
/*   28:     */ public class sIB
/*   29:     */   extends RandomizableClusterer
/*   30:     */   implements TechnicalInformationHandler
/*   31:     */ {
/*   32:     */   private static final long serialVersionUID = -8652125897352654213L;
/*   33:     */   private Instances m_data;
/*   34:     */   
/*   35:     */   private class Input
/*   36:     */     implements Serializable, RevisionHandler
/*   37:     */   {
/*   38:     */     static final long serialVersionUID = -2464453171263384037L;
/*   39:     */     private double[] Px;
/*   40:     */     private double[] Py;
/*   41:     */     private Matrix Pyx;
/*   42:     */     private Matrix Py_x;
/*   43:     */     private double Ixy;
/*   44:     */     private double Hy;
/*   45:     */     private double Hx;
/*   46:     */     private double sumVals;
/*   47:     */     
/*   48:     */     private Input() {}
/*   49:     */     
/*   50:     */     public String getRevision()
/*   51:     */     {
/*   52: 176 */       return RevisionUtils.extract("$Revision: 10375 $");
/*   53:     */     }
/*   54:     */   }
/*   55:     */   
/*   56:     */   private class Partition
/*   57:     */     implements Serializable, RevisionHandler
/*   58:     */   {
/*   59:     */     static final long serialVersionUID = 4957194978951259946L;
/*   60:     */     private final int[] Pt_x;
/*   61:     */     private final double[] Pt;
/*   62:     */     private double L;
/*   63:     */     private int counter;
/*   64:     */     private final Matrix Py_t;
/*   65:     */     
/*   66:     */     public Partition()
/*   67:     */     {
/*   68: 209 */       this.Pt_x = new int[sIB.this.m_numInstances];
/*   69: 210 */       for (int i = 0; i < sIB.this.m_numInstances; i++) {
/*   70: 211 */         this.Pt_x[i] = -1;
/*   71:     */       }
/*   72: 213 */       this.Pt = new double[sIB.this.m_numCluster];
/*   73: 214 */       this.Py_t = new Matrix(sIB.this.m_numAttributes, sIB.this.m_numCluster);
/*   74: 215 */       this.counter = 0;
/*   75:     */     }
/*   76:     */     
/*   77:     */     private ArrayList<Integer> find(int i)
/*   78:     */     {
/*   79: 226 */       ArrayList<Integer> indices = new ArrayList();
/*   80: 227 */       for (int x = 0; x < this.Pt_x.length; x++) {
/*   81: 228 */         if (this.Pt_x[x] == i) {
/*   82: 229 */           indices.add(Integer.valueOf(x));
/*   83:     */         }
/*   84:     */       }
/*   85: 232 */       return indices;
/*   86:     */     }
/*   87:     */     
/*   88:     */     private int size(int i)
/*   89:     */     {
/*   90: 242 */       int count = 0;
/*   91: 243 */       for (int element : this.Pt_x) {
/*   92: 244 */         if (element == i) {
/*   93: 245 */           count++;
/*   94:     */         }
/*   95:     */       }
/*   96: 248 */       return count;
/*   97:     */     }
/*   98:     */     
/*   99:     */     private void copy(Partition T)
/*  100:     */     {
/*  101: 257 */       if (T == null) {
/*  102: 258 */         T = new Partition(sIB.this);
/*  103:     */       }
/*  104: 260 */       System.arraycopy(this.Pt_x, 0, T.Pt_x, 0, this.Pt_x.length);
/*  105: 261 */       System.arraycopy(this.Pt, 0, T.Pt, 0, this.Pt.length);
/*  106: 262 */       T.L = this.L;
/*  107: 263 */       T.counter = this.counter;
/*  108:     */       
/*  109: 265 */       double[][] mArray = this.Py_t.getArray();
/*  110: 266 */       double[][] tgtArray = T.Py_t.getArray();
/*  111: 267 */       for (int i = 0; i < mArray.length; i++) {
/*  112: 268 */         System.arraycopy(mArray[i], 0, tgtArray[i], 0, mArray[0].length);
/*  113:     */       }
/*  114:     */     }
/*  115:     */     
/*  116:     */     public String toString()
/*  117:     */     {
/*  118: 280 */       StringBuffer text = new StringBuffer();
/*  119: 281 */       text.append("score (L) : " + Utils.doubleToString(this.L, 4) + "\n");
/*  120: 282 */       text.append("number of changes : " + this.counter + "\n");
/*  121: 283 */       for (int i = 0; i < sIB.this.m_numCluster; i++)
/*  122:     */       {
/*  123: 284 */         text.append("\nCluster " + i + "\n");
/*  124: 285 */         text.append("size : " + size(i) + "\n");
/*  125: 286 */         text.append("prior prob : " + Utils.doubleToString(this.Pt[i], 4) + "\n");
/*  126:     */       }
/*  127: 288 */       return text.toString();
/*  128:     */     }
/*  129:     */     
/*  130:     */     public String getRevision()
/*  131:     */     {
/*  132: 298 */       return RevisionUtils.extract("$Revision: 10375 $");
/*  133:     */     }
/*  134:     */   }
/*  135:     */   
/*  136: 306 */   private int m_numCluster = 2;
/*  137: 309 */   private int m_numRestarts = 5;
/*  138: 312 */   private boolean m_verbose = false;
/*  139: 315 */   private boolean m_uniformPrior = true;
/*  140: 318 */   private int m_maxLoop = 100;
/*  141: 321 */   private int m_minChange = 0;
/*  142:     */   private ReplaceMissingValues m_replaceMissing;
/*  143:     */   private int m_numInstances;
/*  144:     */   private int m_numAttributes;
/*  145:     */   private Random random;
/*  146:     */   private Partition bestT;
/*  147:     */   private Input input;
/*  148:     */   
/*  149:     */   public void buildClusterer(Instances data)
/*  150:     */     throws Exception
/*  151:     */   {
/*  152: 350 */     getCapabilities().testWithFail(data);
/*  153:     */     
/*  154: 352 */     this.m_replaceMissing = new ReplaceMissingValues();
/*  155: 353 */     Instances instances = new Instances(data);
/*  156: 354 */     instances.setClassIndex(-1);
/*  157: 355 */     this.m_replaceMissing.setInputFormat(instances);
/*  158: 356 */     data = Filter.useFilter(instances, this.m_replaceMissing);
/*  159: 357 */     instances = null;
/*  160:     */     
/*  161:     */ 
/*  162: 360 */     this.m_data = data;
/*  163: 361 */     this.m_numInstances = this.m_data.numInstances();
/*  164: 362 */     this.m_numAttributes = this.m_data.numAttributes();
/*  165: 363 */     this.random = new Random(getSeed());
/*  166:     */     
/*  167:     */ 
/*  168: 366 */     this.input = sIB_ProcessInput();
/*  169:     */     
/*  170:     */ 
/*  171: 369 */     this.bestT = new Partition();
/*  172:     */     
/*  173:     */ 
/*  174: 372 */     double bestL = (-1.0D / 0.0D);
/*  175: 373 */     for (int k = 0; k < this.m_numRestarts; k++)
/*  176:     */     {
/*  177: 374 */       if (this.m_verbose) {
/*  178: 375 */         System.out.format("restart number %s...\n", new Object[] { Integer.valueOf(k) });
/*  179:     */       }
/*  180: 379 */       Partition tmpT = sIB_InitT(this.input);
/*  181: 380 */       tmpT = sIB_OptimizeT(tmpT, this.input);
/*  182: 383 */       if (tmpT.L > bestL)
/*  183:     */       {
/*  184: 384 */         tmpT.copy(this.bestT);
/*  185: 385 */         bestL = this.bestT.L;
/*  186:     */       }
/*  187: 388 */       if (this.m_verbose)
/*  188:     */       {
/*  189: 389 */         System.out.println("\nPartition status : ");
/*  190: 390 */         System.out.println("------------------");
/*  191: 391 */         System.out.println(tmpT.toString() + "\n");
/*  192:     */       }
/*  193:     */     }
/*  194: 395 */     if (this.m_verbose)
/*  195:     */     {
/*  196: 396 */       System.out.println("\nBest Partition");
/*  197: 397 */       System.out.println("===============");
/*  198: 398 */       System.out.println(this.bestT.toString());
/*  199:     */     }
/*  200: 402 */     this.m_data = new Instances(this.m_data, 0);
/*  201:     */   }
/*  202:     */   
/*  203:     */   public int clusterInstance(Instance instance)
/*  204:     */     throws Exception
/*  205:     */   {
/*  206: 411 */     double prior = 1.0D / this.input.sumVals;
/*  207: 412 */     double[] distances = new double[this.m_numCluster];
/*  208: 413 */     for (int i = 0; i < this.m_numCluster; i++)
/*  209:     */     {
/*  210: 414 */       double Pnew = this.bestT.Pt[i] + prior;
/*  211: 415 */       double pi1 = prior / Pnew;
/*  212: 416 */       double pi2 = this.bestT.Pt[i] / Pnew;
/*  213: 417 */       distances[i] = (Pnew * JS(instance, i, pi1, pi2));
/*  214:     */     }
/*  215: 419 */     return Utils.minIndex(distances);
/*  216:     */   }
/*  217:     */   
/*  218:     */   private Input sIB_ProcessInput()
/*  219:     */   {
/*  220: 428 */     double valSum = 0.0D;
/*  221: 429 */     for (int i = 0; i < this.m_numInstances; i++)
/*  222:     */     {
/*  223: 430 */       valSum = 0.0D;
/*  224: 431 */       for (int v = 0; v < this.m_data.instance(i).numValues(); v++) {
/*  225: 432 */         valSum += this.m_data.instance(i).valueSparse(v);
/*  226:     */       }
/*  227: 434 */       if (valSum <= 0.0D)
/*  228:     */       {
/*  229: 435 */         if (this.m_verbose) {
/*  230: 436 */           System.out.format("Instance %s sum of value = %s <= 0, removed.\n", new Object[] { Integer.valueOf(i), Double.valueOf(valSum) });
/*  231:     */         }
/*  232: 439 */         this.m_data.delete(i);
/*  233: 440 */         this.m_numInstances -= 1;
/*  234:     */       }
/*  235:     */     }
/*  236: 445 */     Input input = new Input(null);
/*  237: 446 */     input.Py_x = getTransposedNormedMatrix(this.m_data);
/*  238: 447 */     if (this.m_uniformPrior)
/*  239:     */     {
/*  240: 448 */       input.Pyx = input.Py_x.copy();
/*  241: 449 */       normalizePrior(this.m_data);
/*  242:     */     }
/*  243:     */     else
/*  244:     */     {
/*  245: 451 */       input.Pyx = getTransposedMatrix(this.m_data);
/*  246:     */     }
/*  247: 453 */     input.sumVals = getTotalSum(this.m_data);
/*  248: 454 */     input.Pyx.timesEquals(1.0D / input.sumVals);
/*  249:     */     
/*  250:     */ 
/*  251: 457 */     input.Px = new double[this.m_numInstances];
/*  252: 458 */     for (int i = 0; i < this.m_numInstances; i++) {
/*  253: 459 */       for (int j = 0; j < this.m_numAttributes; j++) {
/*  254: 460 */         input.Px[i] += input.Pyx.get(j, i);
/*  255:     */       }
/*  256:     */     }
/*  257: 465 */     input.Py = new double[this.m_numAttributes];
/*  258: 466 */     for (int i = 0; i < input.Pyx.getRowDimension(); i++) {
/*  259: 467 */       for (int j = 0; j < input.Pyx.getColumnDimension(); j++) {
/*  260: 468 */         input.Py[i] += input.Pyx.get(i, j);
/*  261:     */       }
/*  262:     */     }
/*  263: 472 */     MI(input.Pyx, input);
/*  264: 473 */     return input;
/*  265:     */   }
/*  266:     */   
/*  267:     */   private Partition sIB_InitT(Input input)
/*  268:     */   {
/*  269: 483 */     Partition T = new Partition();
/*  270: 484 */     int avgSize = (int)Math.ceil(this.m_numInstances / this.m_numCluster);
/*  271:     */     
/*  272: 486 */     ArrayList<Integer> permInstsIdx = new ArrayList();
/*  273: 487 */     ArrayList<Integer> unassigned = new ArrayList();
/*  274: 488 */     for (int i = 0; i < this.m_numInstances; i++) {
/*  275: 489 */       unassigned.add(Integer.valueOf(i));
/*  276:     */     }
/*  277: 491 */     while (unassigned.size() != 0)
/*  278:     */     {
/*  279: 492 */       int t = this.random.nextInt(unassigned.size());
/*  280: 493 */       permInstsIdx.add(unassigned.get(t));
/*  281: 494 */       unassigned.remove(t);
/*  282:     */     }
/*  283: 497 */     for (int i = 0; i < this.m_numCluster; i++)
/*  284:     */     {
/*  285: 498 */       int r2 = avgSize > permInstsIdx.size() ? permInstsIdx.size() : avgSize;
/*  286: 499 */       for (int j = 0; j < r2; j++) {
/*  287: 500 */         T.Pt_x[((Integer)permInstsIdx.get(j)).intValue()] = i;
/*  288:     */       }
/*  289: 502 */       for (int j = 0; j < r2; j++) {
/*  290: 503 */         permInstsIdx.remove(0);
/*  291:     */       }
/*  292:     */     }
/*  293: 509 */     for (int i = 0; i < this.m_numCluster; i++)
/*  294:     */     {
/*  295: 510 */       ArrayList<Integer> indices = T.find(i);
/*  296: 511 */       for (int j = 0; j < indices.size(); j++) {
/*  297: 512 */         T.Pt[i] += input.Px[((Integer)indices.get(j)).intValue()];
/*  298:     */       }
/*  299: 514 */       double[][] mArray = input.Pyx.getArray();
/*  300: 515 */       for (int j = 0; j < this.m_numAttributes; j++)
/*  301:     */       {
/*  302: 516 */         double sum = 0.0D;
/*  303: 517 */         for (int k = 0; k < indices.size(); k++) {
/*  304: 518 */           sum += mArray[j][((Integer)indices.get(k)).intValue()];
/*  305:     */         }
/*  306: 520 */         sum /= T.Pt[i];
/*  307: 521 */         T.Py_t.set(j, i, sum);
/*  308:     */       }
/*  309:     */     }
/*  310: 525 */     if (this.m_verbose) {
/*  311: 526 */       System.out.println("Initializing...");
/*  312:     */     }
/*  313: 528 */     return T;
/*  314:     */   }
/*  315:     */   
/*  316:     */   private Partition sIB_OptimizeT(Partition tmpT, Input input)
/*  317:     */   {
/*  318: 539 */     boolean done = false;
/*  319: 540 */     int change = 0;int loopCounter = 0;
/*  320: 541 */     if (this.m_verbose)
/*  321:     */     {
/*  322: 542 */       System.out.println("Optimizing...");
/*  323: 543 */       System.out.println("-------------");
/*  324:     */     }
/*  325: 545 */     while (!done)
/*  326:     */     {
/*  327: 546 */       change = 0;
/*  328: 547 */       for (int i = 0; i < this.m_numInstances; i++)
/*  329:     */       {
/*  330: 548 */         int old_t = tmpT.Pt_x[i];
/*  331: 550 */         if (tmpT.size(old_t) == 1)
/*  332:     */         {
/*  333: 551 */           if (this.m_verbose) {
/*  334: 552 */             System.out.format("cluster %s has only 1 doc remain\n", new Object[] { Integer.valueOf(old_t) });
/*  335:     */           }
/*  336:     */         }
/*  337:     */         else
/*  338:     */         {
/*  339: 557 */           reduce_x(i, old_t, tmpT, input);
/*  340:     */           
/*  341:     */ 
/*  342: 560 */           int new_t = clusterInstance(i, input, tmpT);
/*  343: 561 */           if (new_t != old_t)
/*  344:     */           {
/*  345: 562 */             change++;
/*  346: 563 */             updateAssignment(i, new_t, tmpT, input.Px[i], input.Py_x);
/*  347:     */           }
/*  348:     */         }
/*  349:     */       }
/*  350: 567 */       Partition.access$1612(tmpT, change);
/*  351: 568 */       if (this.m_verbose) {
/*  352: 569 */         System.out.format("iteration %s , changes : %s\n", new Object[] { Integer.valueOf(loopCounter), Integer.valueOf(change) });
/*  353:     */       }
/*  354: 571 */       done = checkConvergence(change, loopCounter);
/*  355: 572 */       loopCounter++;
/*  356:     */     }
/*  357: 576 */     tmpT.L = sIB_local_MI(tmpT.Py_t, tmpT.Pt);
/*  358: 577 */     if (this.m_verbose) {
/*  359: 578 */       System.out.format("score (L) : %s \n", new Object[] { Utils.doubleToString(tmpT.L, 4) });
/*  360:     */     }
/*  361: 580 */     return tmpT;
/*  362:     */   }
/*  363:     */   
/*  364:     */   private void reduce_x(int instIdx, int t, Partition T, Input input)
/*  365:     */   {
/*  366: 593 */     ArrayList<Integer> indices = T.find(t);
/*  367: 594 */     double sum = 0.0D;
/*  368: 595 */     for (int i = 0; i < indices.size(); i++) {
/*  369: 596 */       if (((Integer)indices.get(i)).intValue() != instIdx) {
/*  370: 599 */         sum += input.Px[((Integer)indices.get(i)).intValue()];
/*  371:     */       }
/*  372:     */     }
/*  373: 601 */     T.Pt[t] = sum;
/*  374: 603 */     if (T.Pt[t] < 0.0D)
/*  375:     */     {
/*  376: 604 */       System.out.format("Warning: probability < 0 (%s)\n", new Object[] { Double.valueOf(T.Pt[t]) });
/*  377: 605 */       T.Pt[t] = 0.0D;
/*  378:     */     }
/*  379: 609 */     double[][] mArray = input.Pyx.getArray();
/*  380: 610 */     for (int i = 0; i < this.m_numAttributes; i++)
/*  381:     */     {
/*  382: 611 */       sum = 0.0D;
/*  383: 612 */       for (int j = 0; j < indices.size(); j++) {
/*  384: 613 */         if (((Integer)indices.get(j)).intValue() != instIdx) {
/*  385: 616 */           sum += mArray[i][((Integer)indices.get(j)).intValue()];
/*  386:     */         }
/*  387:     */       }
/*  388: 618 */       T.Py_t.set(i, t, sum / T.Pt[t]);
/*  389:     */     }
/*  390:     */   }
/*  391:     */   
/*  392:     */   private void updateAssignment(int instIdx, int newt, Partition T, double Px, Matrix Py_x)
/*  393:     */   {
/*  394: 632 */     T.Pt_x[instIdx] = newt;
/*  395:     */     
/*  396:     */ 
/*  397: 635 */     double mass = Px + T.Pt[newt];
/*  398: 636 */     double pi1 = Px / mass;
/*  399: 637 */     double pi2 = T.Pt[newt] / mass;
/*  400: 638 */     for (int i = 0; i < this.m_numAttributes; i++) {
/*  401: 639 */       T.Py_t.set(i, newt, pi1 * Py_x.get(i, instIdx) + pi2 * T.Py_t.get(i, newt));
/*  402:     */     }
/*  403: 643 */     T.Pt[newt] = mass;
/*  404:     */   }
/*  405:     */   
/*  406:     */   private boolean checkConvergence(int change, int loops)
/*  407:     */   {
/*  408: 654 */     if ((change <= this.m_minChange) || (loops >= this.m_maxLoop))
/*  409:     */     {
/*  410: 655 */       if (this.m_verbose) {
/*  411: 656 */         System.out.format("\nsIB converged after %s iterations with %s changes\n", new Object[] { Integer.valueOf(loops), Integer.valueOf(change) });
/*  412:     */       }
/*  413: 660 */       return true;
/*  414:     */     }
/*  415: 662 */     return false;
/*  416:     */   }
/*  417:     */   
/*  418:     */   private int clusterInstance(int instIdx, Input input, Partition T)
/*  419:     */   {
/*  420: 674 */     double[] distances = new double[this.m_numCluster];
/*  421: 675 */     for (int i = 0; i < this.m_numCluster; i++)
/*  422:     */     {
/*  423: 676 */       double Pnew = input.Px[instIdx] + T.Pt[i];
/*  424: 677 */       double pi1 = input.Px[instIdx] / Pnew;
/*  425: 678 */       double pi2 = T.Pt[i] / Pnew;
/*  426: 679 */       distances[i] = (Pnew * JS(instIdx, input, T, i, pi1, pi2));
/*  427:     */     }
/*  428: 681 */     return Utils.minIndex(distances);
/*  429:     */   }
/*  430:     */   
/*  431:     */   private double JS(int instIdx, Input input, Partition T, int t, double pi1, double pi2)
/*  432:     */   {
/*  433: 698 */     if (Math.min(pi1, pi2) <= 0.0D)
/*  434:     */     {
/*  435: 699 */       System.out.format("Warning: zero or negative weights in JS calculation! (pi1 %s, pi2 %s)\n", new Object[] { Double.valueOf(pi1), Double.valueOf(pi2) });
/*  436:     */       
/*  437:     */ 
/*  438:     */ 
/*  439: 703 */       return 0.0D;
/*  440:     */     }
/*  441: 705 */     Instance inst = this.m_data.instance(instIdx);
/*  442: 706 */     double kl1 = 0.0D;double kl2 = 0.0D;double tmp = 0.0D;
/*  443: 707 */     for (int i = 0; i < inst.numValues(); i++)
/*  444:     */     {
/*  445: 708 */       tmp = input.Py_x.get(inst.index(i), instIdx);
/*  446: 709 */       if (tmp != 0.0D) {
/*  447: 710 */         kl1 += tmp * Math.log(tmp / (tmp * pi1 + pi2 * T.Py_t.get(inst.index(i), t)));
/*  448:     */       }
/*  449:     */     }
/*  450: 714 */     for (int i = 0; i < this.m_numAttributes; i++) {
/*  451: 715 */       if ((tmp = T.Py_t.get(i, t)) != 0.0D) {
/*  452: 716 */         kl2 += tmp * Math.log(tmp / (input.Py_x.get(i, instIdx) * pi1 + pi2 * tmp));
/*  453:     */       }
/*  454:     */     }
/*  455: 720 */     return pi1 * kl1 + pi2 * kl2;
/*  456:     */   }
/*  457:     */   
/*  458:     */   private double JS(Instance inst, int t, double pi1, double pi2)
/*  459:     */   {
/*  460: 734 */     if (Math.min(pi1, pi2) <= 0.0D)
/*  461:     */     {
/*  462: 735 */       System.out.format("Warning: zero or negative weights in JS calculation! (pi1 %s, pi2 %s)\n", new Object[] { Double.valueOf(pi1), Double.valueOf(pi2) });
/*  463:     */       
/*  464:     */ 
/*  465:     */ 
/*  466: 739 */       return 0.0D;
/*  467:     */     }
/*  468: 741 */     double sum = Utils.sum(inst.toDoubleArray());
/*  469: 742 */     double kl1 = 0.0D;double kl2 = 0.0D;double tmp = 0.0D;
/*  470: 743 */     for (int i = 0; i < inst.numValues(); i++)
/*  471:     */     {
/*  472: 744 */       tmp = inst.valueSparse(i) / sum;
/*  473: 745 */       if (tmp != 0.0D) {
/*  474: 746 */         kl1 += tmp * Math.log(tmp / (tmp * pi1 + pi2 * this.bestT.Py_t.get(inst.index(i), t)));
/*  475:     */       }
/*  476:     */     }
/*  477: 751 */     for (int i = 0; i < this.m_numAttributes; i++) {
/*  478: 752 */       if ((tmp = this.bestT.Py_t.get(i, t)) != 0.0D) {
/*  479: 753 */         kl2 += tmp * Math.log(tmp / (inst.value(i) * pi1 / sum + pi2 * tmp));
/*  480:     */       }
/*  481:     */     }
/*  482: 756 */     return pi1 * kl1 + pi2 * kl2;
/*  483:     */   }
/*  484:     */   
/*  485:     */   private double sIB_local_MI(Matrix m, double[] Pt)
/*  486:     */   {
/*  487: 768 */     double Hy = 0.0D;double Ht = 0.0D;
/*  488: 769 */     for (double element : Pt) {
/*  489: 770 */       Ht += element * Math.log(element);
/*  490:     */     }
/*  491: 772 */     Ht = -Ht;
/*  492: 774 */     for (int i = 0; i < this.m_numAttributes; i++)
/*  493:     */     {
/*  494: 775 */       double Py = 0.0D;
/*  495: 776 */       for (int j = 0; j < this.m_numCluster; j++) {
/*  496: 777 */         Py += m.get(i, j) * Pt[j];
/*  497:     */       }
/*  498: 779 */       if (Py != 0.0D) {
/*  499: 782 */         Hy += Py * Math.log(Py);
/*  500:     */       }
/*  501:     */     }
/*  502: 784 */     Hy = -Hy;
/*  503:     */     
/*  504: 786 */     double Hyt = 0.0D;double tmp = 0.0D;
/*  505: 787 */     for (int i = 0; i < m.getRowDimension(); i++) {
/*  506: 788 */       for (int j = 0; j < m.getColumnDimension(); j++) {
/*  507: 789 */         if (((tmp = m.get(i, j)) != 0.0D) && (Pt[j] != 0.0D))
/*  508:     */         {
/*  509: 792 */           tmp *= Pt[j];
/*  510: 793 */           Hyt += tmp * Math.log(tmp);
/*  511:     */         }
/*  512:     */       }
/*  513:     */     }
/*  514: 796 */     return Hy + Ht + Hyt;
/*  515:     */   }
/*  516:     */   
/*  517:     */   private double getTotalSum(Instances data)
/*  518:     */   {
/*  519: 807 */     double sum = 0.0D;
/*  520: 808 */     for (int i = 0; i < data.numInstances(); i++) {
/*  521: 809 */       for (int v = 0; v < data.instance(i).numValues(); v++) {
/*  522: 810 */         sum += data.instance(i).valueSparse(v);
/*  523:     */       }
/*  524:     */     }
/*  525: 813 */     return sum;
/*  526:     */   }
/*  527:     */   
/*  528:     */   private Matrix getTransposedMatrix(Instances data)
/*  529:     */   {
/*  530: 823 */     double[][] temp = new double[data.numAttributes()][data.numInstances()];
/*  531: 824 */     for (int i = 0; i < data.numInstances(); i++)
/*  532:     */     {
/*  533: 825 */       Instance inst = data.instance(i);
/*  534: 826 */       for (int v = 0; v < inst.numValues(); v++) {
/*  535: 827 */         temp[inst.index(v)][i] = inst.valueSparse(v);
/*  536:     */       }
/*  537:     */     }
/*  538: 830 */     Matrix My_x = new Matrix(temp);
/*  539: 831 */     return My_x;
/*  540:     */   }
/*  541:     */   
/*  542:     */   private void normalizePrior(Instances data)
/*  543:     */   {
/*  544: 840 */     for (int i = 0; i < data.numInstances(); i++) {
/*  545: 841 */       normalizeInstance(data.instance(i));
/*  546:     */     }
/*  547:     */   }
/*  548:     */   
/*  549:     */   private Instance normalizeInstance(Instance inst)
/*  550:     */   {
/*  551: 852 */     double[] vals = inst.toDoubleArray();
/*  552: 853 */     double sum = Utils.sum(vals);
/*  553: 854 */     for (int i = 0; i < vals.length; i++) {
/*  554: 855 */       vals[i] /= sum;
/*  555:     */     }
/*  556: 857 */     return new DenseInstance(inst.weight(), vals);
/*  557:     */   }
/*  558:     */   
/*  559:     */   private Matrix getTransposedNormedMatrix(Instances data)
/*  560:     */   {
/*  561: 861 */     Matrix matrix = new Matrix(data.numAttributes(), data.numInstances());
/*  562: 862 */     for (int i = 0; i < data.numInstances(); i++)
/*  563:     */     {
/*  564: 863 */       double[] vals = data.instance(i).toDoubleArray();
/*  565: 864 */       double sum = Utils.sum(vals);
/*  566: 865 */       for (int v = 0; v < vals.length; v++)
/*  567:     */       {
/*  568: 866 */         vals[v] /= sum;
/*  569: 867 */         matrix.set(v, i, vals[v]);
/*  570:     */       }
/*  571:     */     }
/*  572: 870 */     return matrix;
/*  573:     */   }
/*  574:     */   
/*  575:     */   private void MI(Matrix m, Input input)
/*  576:     */   {
/*  577: 880 */     int minDimSize = m.getColumnDimension() < m.getRowDimension() ? m.getColumnDimension() : m.getRowDimension();
/*  578: 882 */     if (minDimSize < 2)
/*  579:     */     {
/*  580: 883 */       System.err.println("Warning : This is not a JOINT distribution");
/*  581: 884 */       input.Hx = Entropy(m);
/*  582: 885 */       input.Hy = 0.0D;
/*  583: 886 */       input.Ixy = 0.0D;
/*  584: 887 */       return;
/*  585:     */     }
/*  586: 890 */     input.Hx = Entropy(input.Px);
/*  587: 891 */     input.Hy = Entropy(input.Py);
/*  588:     */     
/*  589: 893 */     double entropy = input.Hx + input.Hy;
/*  590: 894 */     for (int i = 0; i < this.m_numInstances; i++)
/*  591:     */     {
/*  592: 895 */       Instance inst = this.m_data.instance(i);
/*  593: 896 */       for (int v = 0; v < inst.numValues(); v++)
/*  594:     */       {
/*  595: 897 */         double tmp = m.get(inst.index(v), i);
/*  596: 898 */         if (tmp > 0.0D) {
/*  597: 901 */           entropy += tmp * Math.log(tmp);
/*  598:     */         }
/*  599:     */       }
/*  600:     */     }
/*  601: 904 */     input.Ixy = entropy;
/*  602: 905 */     if (this.m_verbose) {
/*  603: 906 */       System.out.println("Ixy = " + input.Ixy);
/*  604:     */     }
/*  605:     */   }
/*  606:     */   
/*  607:     */   private double Entropy(double[] probs)
/*  608:     */   {
/*  609: 917 */     for (double prob : probs) {
/*  610: 918 */       if (prob <= 0.0D)
/*  611:     */       {
/*  612: 919 */         if (this.m_verbose) {
/*  613: 920 */           System.out.println("Warning: Negative probability.");
/*  614:     */         }
/*  615: 922 */         return (0.0D / 0.0D);
/*  616:     */       }
/*  617:     */     }
/*  618: 926 */     if (Math.abs(Utils.sum(probs) - 1.0D) >= 1.0E-006D)
/*  619:     */     {
/*  620: 927 */       if (this.m_verbose) {
/*  621: 928 */         System.out.println("Warning: Not normalized.");
/*  622:     */       }
/*  623: 930 */       return (0.0D / 0.0D);
/*  624:     */     }
/*  625: 933 */     double mi = 0.0D;
/*  626: 934 */     for (double prob : probs) {
/*  627: 935 */       mi += prob * Math.log(prob);
/*  628:     */     }
/*  629: 937 */     mi = -mi;
/*  630: 938 */     return mi;
/*  631:     */   }
/*  632:     */   
/*  633:     */   private double Entropy(Matrix p)
/*  634:     */   {
/*  635: 948 */     double mi = 0.0D;
/*  636: 949 */     for (int i = 0; i < p.getRowDimension(); i++) {
/*  637: 950 */       for (int j = 0; j < p.getColumnDimension(); j++) {
/*  638: 951 */         if (p.get(i, j) != 0.0D) {
/*  639: 954 */           mi += p.get(i, j) + Math.log(p.get(i, j));
/*  640:     */         }
/*  641:     */       }
/*  642:     */     }
/*  643: 957 */     mi = -mi;
/*  644: 958 */     return mi;
/*  645:     */   }
/*  646:     */   
/*  647:     */   public void setOptions(String[] options)
/*  648:     */     throws Exception
/*  649:     */   {
/*  650:1017 */     String optionString = Utils.getOption('I', options);
/*  651:1018 */     if (optionString.length() != 0) {
/*  652:1019 */       setMaxIterations(Integer.parseInt(optionString));
/*  653:     */     }
/*  654:1021 */     optionString = Utils.getOption('M', options);
/*  655:1022 */     if (optionString.length() != 0) {
/*  656:1023 */       setMinChange(new Integer(optionString).intValue());
/*  657:     */     }
/*  658:1025 */     optionString = Utils.getOption('N', options);
/*  659:1026 */     if (optionString.length() != 0) {
/*  660:1027 */       setNumClusters(Integer.parseInt(optionString));
/*  661:     */     }
/*  662:1029 */     optionString = Utils.getOption('R', options);
/*  663:1030 */     if (optionString.length() != 0) {
/*  664:1031 */       setNumRestarts(new Integer(optionString).intValue());
/*  665:     */     }
/*  666:1033 */     setNotUnifyNorm(Utils.getFlag('U', options));
/*  667:1034 */     setDebug(Utils.getFlag('V', options));
/*  668:     */     
/*  669:1036 */     super.setOptions(options);
/*  670:     */   }
/*  671:     */   
/*  672:     */   public Enumeration<Option> listOptions()
/*  673:     */   {
/*  674:1047 */     Vector<Option> result = new Vector();
/*  675:     */     
/*  676:1049 */     result.addElement(new Option("\tmaximum number of iterations\n\t(default 100).", "I", 1, "-I <num>"));
/*  677:     */     
/*  678:1051 */     result.addElement(new Option("\tminimum number of changes in a single iteration\n\t(default 0).", "M", 1, "-M <num>"));
/*  679:     */     
/*  680:     */ 
/*  681:1054 */     result.addElement(new Option("\tnumber of clusters.\n\t(default 2).", "N", 1, "-N <num>"));
/*  682:     */     
/*  683:1056 */     result.addElement(new Option("\tnumber of restarts.\n\t(default 5).", "R", 1, "-R <num>"));
/*  684:     */     
/*  685:1058 */     result.addElement(new Option("\tset not to normalize the data\n\t(default true).", "U", 0, "-U"));
/*  686:     */     
/*  687:1060 */     result.addElement(new Option("\tset to output debug info\n\t(default false).", "V", 0, "-V"));
/*  688:     */     
/*  689:     */ 
/*  690:1063 */     result.addAll(Collections.list(super.listOptions()));
/*  691:     */     
/*  692:1065 */     return result.elements();
/*  693:     */   }
/*  694:     */   
/*  695:     */   public String[] getOptions()
/*  696:     */   {
/*  697:1076 */     Vector<String> result = new Vector();
/*  698:     */     
/*  699:1078 */     result.add("-I");
/*  700:1079 */     result.add("" + getMaxIterations());
/*  701:1080 */     result.add("-M");
/*  702:1081 */     result.add("" + getMinChange());
/*  703:1082 */     result.add("-N");
/*  704:1083 */     result.add("" + getNumClusters());
/*  705:1084 */     result.add("-R");
/*  706:1085 */     result.add("" + getNumRestarts());
/*  707:1086 */     if (getNotUnifyNorm()) {
/*  708:1087 */       result.add("-U");
/*  709:     */     }
/*  710:1089 */     if (getDebug()) {
/*  711:1090 */       result.add("-V");
/*  712:     */     }
/*  713:1093 */     Collections.addAll(result, super.getOptions());
/*  714:     */     
/*  715:1095 */     return (String[])result.toArray(new String[result.size()]);
/*  716:     */   }
/*  717:     */   
/*  718:     */   public String debugTipText()
/*  719:     */   {
/*  720:1106 */     return "If set to true, clusterer may output additional info to the console.";
/*  721:     */   }
/*  722:     */   
/*  723:     */   public void setDebug(boolean v)
/*  724:     */   {
/*  725:1117 */     this.m_verbose = v;
/*  726:     */   }
/*  727:     */   
/*  728:     */   public boolean getDebug()
/*  729:     */   {
/*  730:1127 */     return this.m_verbose;
/*  731:     */   }
/*  732:     */   
/*  733:     */   public String maxIterationsTipText()
/*  734:     */   {
/*  735:1136 */     return "set maximum number of iterations (default 100)";
/*  736:     */   }
/*  737:     */   
/*  738:     */   public void setMaxIterations(int i)
/*  739:     */   {
/*  740:1145 */     this.m_maxLoop = i;
/*  741:     */   }
/*  742:     */   
/*  743:     */   public int getMaxIterations()
/*  744:     */   {
/*  745:1154 */     return this.m_maxLoop;
/*  746:     */   }
/*  747:     */   
/*  748:     */   public String minChangeTipText()
/*  749:     */   {
/*  750:1163 */     return "set minimum number of changes (default 0)";
/*  751:     */   }
/*  752:     */   
/*  753:     */   public void setMinChange(int m)
/*  754:     */   {
/*  755:1172 */     this.m_minChange = m;
/*  756:     */   }
/*  757:     */   
/*  758:     */   public int getMinChange()
/*  759:     */   {
/*  760:1181 */     return this.m_minChange;
/*  761:     */   }
/*  762:     */   
/*  763:     */   public String numClustersTipText()
/*  764:     */   {
/*  765:1190 */     return "set number of clusters (default 2)";
/*  766:     */   }
/*  767:     */   
/*  768:     */   public void setNumClusters(int n)
/*  769:     */   {
/*  770:1199 */     this.m_numCluster = n;
/*  771:     */   }
/*  772:     */   
/*  773:     */   public int getNumClusters()
/*  774:     */   {
/*  775:1208 */     return this.m_numCluster;
/*  776:     */   }
/*  777:     */   
/*  778:     */   public int numberOfClusters()
/*  779:     */   {
/*  780:1218 */     return this.m_numCluster;
/*  781:     */   }
/*  782:     */   
/*  783:     */   public String numRestartsTipText()
/*  784:     */   {
/*  785:1227 */     return "set number of restarts (default 5)";
/*  786:     */   }
/*  787:     */   
/*  788:     */   public void setNumRestarts(int i)
/*  789:     */   {
/*  790:1236 */     this.m_numRestarts = i;
/*  791:     */   }
/*  792:     */   
/*  793:     */   public int getNumRestarts()
/*  794:     */   {
/*  795:1245 */     return this.m_numRestarts;
/*  796:     */   }
/*  797:     */   
/*  798:     */   public String notUnifyNormTipText()
/*  799:     */   {
/*  800:1254 */     return "set whether to normalize each instance to a unify prior probability (eg. 1).";
/*  801:     */   }
/*  802:     */   
/*  803:     */   public void setNotUnifyNorm(boolean b)
/*  804:     */   {
/*  805:1264 */     this.m_uniformPrior = (!b);
/*  806:     */   }
/*  807:     */   
/*  808:     */   public boolean getNotUnifyNorm()
/*  809:     */   {
/*  810:1274 */     return !this.m_uniformPrior;
/*  811:     */   }
/*  812:     */   
/*  813:     */   public String globalInfo()
/*  814:     */   {
/*  815:1284 */     return "Cluster data using the sequential information bottleneck algorithm.\n\nNote: only hard clustering scheme is supported. sIB assign for each instance the cluster that have the minimum cost/distance to the instance. The trade-off beta is set to infinite so 1/beta is zero.\n\nFor more information, see:\n\n" + getTechnicalInformation().toString();
/*  816:     */   }
/*  817:     */   
/*  818:     */   public TechnicalInformation getTechnicalInformation()
/*  819:     */   {
/*  820:1302 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  821:1303 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Noam Slonim and Nir Friedman and Naftali Tishby");
/*  822:     */     
/*  823:1305 */     result.setValue(TechnicalInformation.Field.YEAR, "2002");
/*  824:1306 */     result.setValue(TechnicalInformation.Field.TITLE, "Unsupervised document classification using sequential information maximization");
/*  825:     */     
/*  826:     */ 
/*  827:     */ 
/*  828:1310 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Proceedings of the 25th International ACM SIGIR Conference on Research and Development in Information Retrieval");
/*  829:     */     
/*  830:     */ 
/*  831:     */ 
/*  832:1314 */     result.setValue(TechnicalInformation.Field.PAGES, "129-136");
/*  833:     */     
/*  834:1316 */     return result;
/*  835:     */   }
/*  836:     */   
/*  837:     */   public Capabilities getCapabilities()
/*  838:     */   {
/*  839:1326 */     Capabilities result = super.getCapabilities();
/*  840:1327 */     result.disableAll();
/*  841:1328 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  842:     */     
/*  843:     */ 
/*  844:1331 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  845:1332 */     return result;
/*  846:     */   }
/*  847:     */   
/*  848:     */   public String toString()
/*  849:     */   {
/*  850:1337 */     StringBuffer text = new StringBuffer();
/*  851:1338 */     text.append("\nsIB\n===\n");
/*  852:1339 */     text.append("\nNumber of clusters: " + this.m_numCluster + "\n");
/*  853:1341 */     for (int j = 0; j < this.m_numCluster; j++)
/*  854:     */     {
/*  855:1342 */       text.append("\nCluster: " + j + " Size : " + this.bestT.size(j) + " Prior probability: " + Utils.doubleToString(this.bestT.Pt[j], 4) + "\n\n");
/*  856:1345 */       for (int i = 0; i < this.m_numAttributes; i++)
/*  857:     */       {
/*  858:1346 */         text.append("Attribute: " + this.m_data.attribute(i).name() + "\n");
/*  859:1347 */         text.append("Probability given the cluster = " + Utils.doubleToString(this.bestT.Py_t.get(i, j), 4) + "\n");
/*  860:     */       }
/*  861:     */     }
/*  862:1351 */     return text.toString();
/*  863:     */   }
/*  864:     */   
/*  865:     */   public String getRevision()
/*  866:     */   {
/*  867:1361 */     return RevisionUtils.extract("$Revision: 10375 $");
/*  868:     */   }
/*  869:     */   
/*  870:     */   public static void main(String[] argv)
/*  871:     */   {
/*  872:1365 */     runClusterer(new sIB(), argv);
/*  873:     */   }
/*  874:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.clusterers.sIB
 * JD-Core Version:    0.7.0.1
 */