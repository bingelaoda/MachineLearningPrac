/*    1:     */ package weka.classifiers.functions.pace;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.text.DecimalFormat;
/*    5:     */ import java.util.Random;
/*    6:     */ import weka.core.RevisionUtils;
/*    7:     */ import weka.core.matrix.DoubleVector;
/*    8:     */ import weka.core.matrix.FlexibleDecimalFormat;
/*    9:     */ import weka.core.matrix.IntVector;
/*   10:     */ import weka.core.matrix.Maths;
/*   11:     */ import weka.core.matrix.Matrix;
/*   12:     */ 
/*   13:     */ public class PaceMatrix
/*   14:     */   extends Matrix
/*   15:     */ {
/*   16:     */   static final long serialVersionUID = 2699925616857843973L;
/*   17:     */   
/*   18:     */   public PaceMatrix(int m, int n)
/*   19:     */   {
/*   20:  69 */     super(m, n);
/*   21:     */   }
/*   22:     */   
/*   23:     */   public PaceMatrix(int m, int n, double s)
/*   24:     */   {
/*   25:  80 */     super(m, n, s);
/*   26:     */   }
/*   27:     */   
/*   28:     */   public PaceMatrix(double[][] A)
/*   29:     */   {
/*   30:  90 */     super(A);
/*   31:     */   }
/*   32:     */   
/*   33:     */   public PaceMatrix(double[][] A, int m, int n)
/*   34:     */   {
/*   35: 101 */     super(A, m, n);
/*   36:     */   }
/*   37:     */   
/*   38:     */   public PaceMatrix(double[] vals, int m)
/*   39:     */   {
/*   40: 113 */     super(vals, m);
/*   41:     */   }
/*   42:     */   
/*   43:     */   public PaceMatrix(DoubleVector v)
/*   44:     */   {
/*   45: 122 */     this(v.size(), 1);
/*   46: 123 */     setMatrix(0, v.size() - 1, 0, v);
/*   47:     */   }
/*   48:     */   
/*   49:     */   public PaceMatrix(Matrix X)
/*   50:     */   {
/*   51: 132 */     super(X.getRowDimension(), X.getColumnDimension());
/*   52: 133 */     this.A = X.getArray();
/*   53:     */   }
/*   54:     */   
/*   55:     */   public void setRowDimension(int rowDimension)
/*   56:     */   {
/*   57: 146 */     this.m = rowDimension;
/*   58:     */   }
/*   59:     */   
/*   60:     */   public void setColumnDimension(int columnDimension)
/*   61:     */   {
/*   62: 155 */     this.n = columnDimension;
/*   63:     */   }
/*   64:     */   
/*   65:     */   public Object clone()
/*   66:     */   {
/*   67: 165 */     PaceMatrix X = new PaceMatrix(this.m, this.n);
/*   68: 166 */     double[][] C = X.getArray();
/*   69: 167 */     for (int i = 0; i < this.m; i++) {
/*   70: 168 */       for (int j = 0; j < this.n; j++) {
/*   71: 169 */         C[i][j] = this.A[i][j];
/*   72:     */       }
/*   73:     */     }
/*   74: 172 */     return X;
/*   75:     */   }
/*   76:     */   
/*   77:     */   public void setPlus(int i, int j, double s)
/*   78:     */   {
/*   79: 183 */     this.A[i][j] += s;
/*   80:     */   }
/*   81:     */   
/*   82:     */   public void setTimes(int i, int j, double s)
/*   83:     */   {
/*   84: 194 */     this.A[i][j] *= s;
/*   85:     */   }
/*   86:     */   
/*   87:     */   public void setMatrix(int i0, int i1, int j0, int j1, double s)
/*   88:     */   {
/*   89:     */     try
/*   90:     */     {
/*   91: 208 */       for (int i = i0; i <= i1; i++) {
/*   92: 209 */         for (int j = j0; j <= j1; j++) {
/*   93: 210 */           this.A[i][j] = s;
/*   94:     */         }
/*   95:     */       }
/*   96:     */     }
/*   97:     */     catch (ArrayIndexOutOfBoundsException e)
/*   98:     */     {
/*   99: 214 */       throw new ArrayIndexOutOfBoundsException("Index out of bounds");
/*  100:     */     }
/*  101:     */   }
/*  102:     */   
/*  103:     */   public void setMatrix(int i0, int i1, int j, DoubleVector v)
/*  104:     */   {
/*  105: 227 */     for (int i = i0; i <= i1; i++) {
/*  106: 228 */       this.A[i][j] = v.get(i - i0);
/*  107:     */     }
/*  108:     */   }
/*  109:     */   
/*  110:     */   public void setMatrix(double[] v, boolean columnFirst)
/*  111:     */   {
/*  112:     */     try
/*  113:     */     {
/*  114: 241 */       if (v.length != this.m * this.n) {
/*  115: 242 */         throw new IllegalArgumentException("sizes not match.");
/*  116:     */       }
/*  117: 244 */       int count = 0;
/*  118: 245 */       if (columnFirst) {
/*  119: 246 */         for (int i = 0; i < this.m; i++) {
/*  120: 247 */           for (int j = 0; j < this.n; j++)
/*  121:     */           {
/*  122: 248 */             this.A[i][j] = v[count];
/*  123: 249 */             count++;
/*  124:     */           }
/*  125:     */         }
/*  126:     */       }
/*  127: 253 */       for (int j = 0; j < this.n; j++) {
/*  128: 254 */         for (int i = 0; i < this.m; i++)
/*  129:     */         {
/*  130: 255 */           this.A[i][j] = v[count];
/*  131: 256 */           count++;
/*  132:     */         }
/*  133:     */       }
/*  134:     */     }
/*  135:     */     catch (ArrayIndexOutOfBoundsException e)
/*  136:     */     {
/*  137: 262 */       throw new ArrayIndexOutOfBoundsException("Submatrix indices");
/*  138:     */     }
/*  139:     */   }
/*  140:     */   
/*  141:     */   public double maxAbs()
/*  142:     */   {
/*  143: 272 */     double ma = Math.abs(this.A[0][0]);
/*  144: 273 */     for (int j = 0; j < this.n; j++) {
/*  145: 274 */       for (int i = 0; i < this.m; i++) {
/*  146: 275 */         ma = Math.max(ma, Math.abs(this.A[i][j]));
/*  147:     */       }
/*  148:     */     }
/*  149: 278 */     return ma;
/*  150:     */   }
/*  151:     */   
/*  152:     */   public double maxAbs(int i0, int i1, int j)
/*  153:     */   {
/*  154: 291 */     double m = Math.abs(this.A[i0][j]);
/*  155: 292 */     for (int i = i0 + 1; i <= i1; i++) {
/*  156: 293 */       m = Math.max(m, Math.abs(this.A[i][j]));
/*  157:     */     }
/*  158: 295 */     return m;
/*  159:     */   }
/*  160:     */   
/*  161:     */   public double minAbs(int i0, int i1, int column)
/*  162:     */   {
/*  163: 308 */     double m = Math.abs(this.A[i0][column]);
/*  164: 309 */     for (int i = i0 + 1; i <= i1; i++) {
/*  165: 310 */       m = Math.min(m, Math.abs(this.A[i][column]));
/*  166:     */     }
/*  167: 312 */     return m;
/*  168:     */   }
/*  169:     */   
/*  170:     */   public boolean isEmpty()
/*  171:     */   {
/*  172: 321 */     if ((this.m == 0) || (this.n == 0)) {
/*  173: 322 */       return true;
/*  174:     */     }
/*  175: 324 */     if (this.A == null) {
/*  176: 325 */       return true;
/*  177:     */     }
/*  178: 327 */     return false;
/*  179:     */   }
/*  180:     */   
/*  181:     */   public DoubleVector getColumn(int j)
/*  182:     */   {
/*  183: 337 */     DoubleVector v = new DoubleVector(this.m);
/*  184: 338 */     double[] a = v.getArray();
/*  185: 339 */     for (int i = 0; i < this.m; i++) {
/*  186: 340 */       a[i] = this.A[i][j];
/*  187:     */     }
/*  188: 342 */     return v;
/*  189:     */   }
/*  190:     */   
/*  191:     */   public DoubleVector getColumn(int i0, int i1, int j)
/*  192:     */   {
/*  193: 354 */     DoubleVector v = new DoubleVector(i1 - i0 + 1);
/*  194: 355 */     double[] a = v.getArray();
/*  195: 356 */     int count = 0;
/*  196: 357 */     for (int i = i0; i <= i1; i++)
/*  197:     */     {
/*  198: 358 */       a[count] = this.A[i][j];
/*  199: 359 */       count++;
/*  200:     */     }
/*  201: 361 */     return v;
/*  202:     */   }
/*  203:     */   
/*  204:     */   public double times(int i, int j0, int j1, PaceMatrix B, int l)
/*  205:     */   {
/*  206: 376 */     double s = 0.0D;
/*  207: 377 */     for (int j = j0; j <= j1; j++) {
/*  208: 378 */       s += this.A[i][j] * B.A[j][l];
/*  209:     */     }
/*  210: 380 */     return s;
/*  211:     */   }
/*  212:     */   
/*  213:     */   protected DecimalFormat[] format()
/*  214:     */   {
/*  215: 389 */     return format(0, this.m - 1, 0, this.n - 1, 7, false);
/*  216:     */   }
/*  217:     */   
/*  218:     */   protected DecimalFormat[] format(int digits)
/*  219:     */   {
/*  220: 399 */     return format(0, this.m - 1, 0, this.n - 1, digits, false);
/*  221:     */   }
/*  222:     */   
/*  223:     */   protected DecimalFormat[] format(int digits, boolean trailing)
/*  224:     */   {
/*  225: 410 */     return format(0, this.m - 1, 0, this.n - 1, digits, trailing);
/*  226:     */   }
/*  227:     */   
/*  228:     */   protected DecimalFormat format(int i0, int i1, int j, int digits, boolean trailing)
/*  229:     */   {
/*  230: 425 */     FlexibleDecimalFormat df = new FlexibleDecimalFormat(digits, trailing);
/*  231: 426 */     df.grouping(true);
/*  232: 427 */     for (int i = i0; i <= i1; i++) {
/*  233: 428 */       df.update(this.A[i][j]);
/*  234:     */     }
/*  235: 430 */     return df;
/*  236:     */   }
/*  237:     */   
/*  238:     */   protected DecimalFormat[] format(int i0, int i1, int j0, int j1, int digits, boolean trailing)
/*  239:     */   {
/*  240: 446 */     DecimalFormat[] f = new DecimalFormat[j1 - j0 + 1];
/*  241: 447 */     for (int j = j0; j <= j1; j++) {
/*  242: 448 */       f[j] = format(i0, i1, j, digits, trailing);
/*  243:     */     }
/*  244: 450 */     return f;
/*  245:     */   }
/*  246:     */   
/*  247:     */   public String toString()
/*  248:     */   {
/*  249: 460 */     return toString(5, false);
/*  250:     */   }
/*  251:     */   
/*  252:     */   public String toString(int digits, boolean trailing)
/*  253:     */   {
/*  254: 472 */     if (isEmpty()) {
/*  255: 473 */       return "null matrix";
/*  256:     */     }
/*  257: 476 */     StringBuffer text = new StringBuffer();
/*  258: 477 */     DecimalFormat[] nf = format(digits, trailing);
/*  259: 478 */     int numCols = 0;
/*  260: 479 */     int count = 0;
/*  261: 480 */     int width = 80;
/*  262:     */     
/*  263:     */ 
/*  264: 483 */     int[] nCols = new int[this.n];
/*  265: 484 */     int nk = 0;
/*  266: 485 */     for (int j = 0; j < this.n; j++)
/*  267:     */     {
/*  268: 486 */       int lenNumber = nf[j].format(this.A[0][j]).length();
/*  269: 487 */       if (count + 1 + lenNumber > width - 1)
/*  270:     */       {
/*  271: 488 */         nCols[(nk++)] = numCols;
/*  272: 489 */         count = 0;
/*  273: 490 */         numCols = 0;
/*  274:     */       }
/*  275: 492 */       count += 1 + lenNumber;
/*  276: 493 */       numCols++;
/*  277:     */     }
/*  278: 495 */     nCols[nk] = numCols;
/*  279:     */     
/*  280: 497 */     nk = 0;
/*  281: 498 */     for (int k = 0; k < this.n;)
/*  282:     */     {
/*  283: 499 */       for (int i = 0; i < this.m; i++)
/*  284:     */       {
/*  285: 500 */         for (int j = k; j < k + nCols[nk]; j++) {
/*  286: 501 */           text.append(" " + nf[j].format(this.A[i][j]));
/*  287:     */         }
/*  288: 503 */         text.append("\n");
/*  289:     */       }
/*  290: 505 */       k += nCols[nk];
/*  291: 506 */       nk++;
/*  292: 507 */       text.append("\n");
/*  293:     */     }
/*  294: 510 */     return text.toString();
/*  295:     */   }
/*  296:     */   
/*  297:     */   public double sum2(int j, int i0, int i1, boolean col)
/*  298:     */   {
/*  299: 523 */     double s2 = 0.0D;
/*  300: 524 */     if (col) {
/*  301: 525 */       for (int i = i0; i <= i1; i++) {
/*  302: 526 */         s2 += this.A[i][j] * this.A[i][j];
/*  303:     */       }
/*  304:     */     } else {
/*  305: 529 */       for (int i = i0; i <= i1; i++) {
/*  306: 530 */         s2 += this.A[j][i] * this.A[j][i];
/*  307:     */       }
/*  308:     */     }
/*  309: 533 */     return s2;
/*  310:     */   }
/*  311:     */   
/*  312:     */   public double[] sum2(boolean col)
/*  313:     */   {
/*  314: 543 */     int l = col ? this.n : this.m;
/*  315: 544 */     int p = col ? this.m : this.n;
/*  316: 545 */     double[] s2 = new double[l];
/*  317: 546 */     for (int i = 0; i < l; i++) {
/*  318: 547 */       s2[i] = sum2(i, 0, p - 1, col);
/*  319:     */     }
/*  320: 549 */     return s2;
/*  321:     */   }
/*  322:     */   
/*  323:     */   public double[] h1(int j, int k)
/*  324:     */   {
/*  325: 560 */     double[] dq = new double[2];
/*  326: 561 */     double s2 = sum2(j, k, this.m - 1, true);
/*  327: 562 */     dq[0] = (this.A[k][j] >= 0.0D ? -Math.sqrt(s2) : Math.sqrt(s2));
/*  328: 563 */     this.A[k][j] -= dq[0];
/*  329: 564 */     dq[1] = (this.A[k][j] * dq[0]);
/*  330: 565 */     return dq;
/*  331:     */   }
/*  332:     */   
/*  333:     */   public void h2(int j, int k, double q, PaceMatrix b, int l)
/*  334:     */   {
/*  335: 578 */     double s = 0.0D;
/*  336: 579 */     for (int i = k; i < this.m; i++) {
/*  337: 580 */       s += this.A[i][j] * b.A[i][l];
/*  338:     */     }
/*  339: 582 */     double alpha = s / q;
/*  340: 583 */     for (int i = k; i < this.m; i++) {
/*  341: 584 */       b.A[i][l] += alpha * this.A[i][j];
/*  342:     */     }
/*  343:     */   }
/*  344:     */   
/*  345:     */   public double[] g1(double a, double b)
/*  346:     */   {
/*  347: 596 */     double[] cs = new double[2];
/*  348: 597 */     double r = Maths.hypot(a, b);
/*  349: 598 */     if (r == 0.0D)
/*  350:     */     {
/*  351: 599 */       cs[0] = 1.0D;
/*  352: 600 */       cs[1] = 0.0D;
/*  353:     */     }
/*  354:     */     else
/*  355:     */     {
/*  356: 602 */       cs[0] = (a / r);
/*  357: 603 */       cs[1] = (b / r);
/*  358:     */     }
/*  359: 605 */     return cs;
/*  360:     */   }
/*  361:     */   
/*  362:     */   public void g2(double[] cs, int i0, int i1, int j)
/*  363:     */   {
/*  364: 617 */     double w = cs[0] * this.A[i0][j] + cs[1] * this.A[i1][j];
/*  365: 618 */     this.A[i1][j] = (-cs[1] * this.A[i0][j] + cs[0] * this.A[i1][j]);
/*  366: 619 */     this.A[i0][j] = w;
/*  367:     */   }
/*  368:     */   
/*  369:     */   public void forward(PaceMatrix b, IntVector pvt, int k0)
/*  370:     */   {
/*  371: 632 */     for (int j = k0; j < Math.min(pvt.size(), this.m); j++) {
/*  372: 633 */       steplsqr(b, pvt, j, mostExplainingColumn(b, pvt, j), true);
/*  373:     */     }
/*  374:     */   }
/*  375:     */   
/*  376:     */   public int mostExplainingColumn(PaceMatrix b, IntVector pvt, int ks)
/*  377:     */   {
/*  378: 650 */     double ma = columnResponseExplanation(b, pvt, ks, ks);
/*  379: 651 */     int jma = ks;
/*  380: 652 */     for (int i = ks + 1; i < pvt.size(); i++)
/*  381:     */     {
/*  382: 653 */       double val = columnResponseExplanation(b, pvt, i, ks);
/*  383: 654 */       if (val > ma)
/*  384:     */       {
/*  385: 655 */         ma = val;
/*  386: 656 */         jma = i;
/*  387:     */       }
/*  388:     */     }
/*  389: 659 */     return jma;
/*  390:     */   }
/*  391:     */   
/*  392:     */   public void backward(PaceMatrix b, IntVector pvt, int ks, int k0)
/*  393:     */   {
/*  394: 675 */     for (int j = ks; j > k0; j--) {
/*  395: 676 */       steplsqr(b, pvt, j, leastExplainingColumn(b, pvt, j, k0), false);
/*  396:     */     }
/*  397:     */   }
/*  398:     */   
/*  399:     */   public int leastExplainingColumn(PaceMatrix b, IntVector pvt, int ks, int k0)
/*  400:     */   {
/*  401: 694 */     double mi = columnResponseExplanation(b, pvt, ks - 1, ks);
/*  402: 695 */     int jmi = ks - 1;
/*  403: 696 */     for (int i = k0; i < ks - 1; i++)
/*  404:     */     {
/*  405: 697 */       double val = columnResponseExplanation(b, pvt, i, ks);
/*  406: 698 */       if (val <= mi)
/*  407:     */       {
/*  408: 699 */         mi = val;
/*  409: 700 */         jmi = i;
/*  410:     */       }
/*  411:     */     }
/*  412: 703 */     return jmi;
/*  413:     */   }
/*  414:     */   
/*  415:     */   public double columnResponseExplanation(PaceMatrix b, IntVector pvt, int j, int ks)
/*  416:     */   {
/*  417: 736 */     double[] xxx = new double[this.n];
/*  418: 737 */     int[] p = pvt.getArray();
/*  419:     */     double val;
/*  420:     */     double val;
/*  421: 740 */     if (j == ks - 1)
/*  422:     */     {
/*  423: 741 */       val = b.A[j][0];
/*  424:     */     }
/*  425:     */     else
/*  426:     */     {
/*  427:     */       double val;
/*  428: 742 */       if (j > ks - 1)
/*  429:     */       {
/*  430: 743 */         int jm = Math.min(this.n - 1, j);
/*  431: 744 */         DoubleVector u = getColumn(ks, jm, p[j]);
/*  432: 745 */         DoubleVector v = b.getColumn(ks, jm, 0);
/*  433: 746 */         val = v.innerProduct(u) / u.norm2();
/*  434:     */       }
/*  435:     */       else
/*  436:     */       {
/*  437: 748 */         for (int k = j + 1; k < ks; k++) {
/*  438: 749 */           xxx[k] = this.A[j][p[k]];
/*  439:     */         }
/*  440: 751 */         val = b.A[j][0];
/*  441: 753 */         for (k = j + 1; k < ks; k++)
/*  442:     */         {
/*  443: 754 */           double[] cs = g1(xxx[k], this.A[k][p[k]]);
/*  444: 755 */           for (int l = k + 1; l < ks; l++) {
/*  445: 756 */             xxx[l] = (-cs[1] * xxx[l] + cs[0] * this.A[k][p[l]]);
/*  446:     */           }
/*  447: 758 */           val = -cs[1] * val + cs[0] * b.A[k][0];
/*  448:     */         }
/*  449:     */       }
/*  450:     */     }
/*  451: 761 */     return val * val;
/*  452:     */   }
/*  453:     */   
/*  454:     */   public void lsqr(PaceMatrix b, IntVector pvt, int k0)
/*  455:     */   {
/*  456: 779 */     double TINY = 1.E-015D;
/*  457: 780 */     int[] p = pvt.getArray();
/*  458: 781 */     int ks = 0;
/*  459: 782 */     for (int j = 0; j < k0; j++) {
/*  460: 783 */       if (sum2(p[j], ks, this.m - 1, true) > 1.E-015D)
/*  461:     */       {
/*  462: 784 */         steplsqr(b, pvt, ks, j, true);
/*  463: 785 */         ks++;
/*  464:     */       }
/*  465:     */       else
/*  466:     */       {
/*  467: 787 */         pvt.shiftToEnd(j);
/*  468: 788 */         pvt.setSize(pvt.size() - 1);
/*  469: 789 */         k0--;
/*  470: 790 */         j--;
/*  471:     */       }
/*  472:     */     }
/*  473: 795 */     for (int j = k0; j < Math.min(pvt.size(), this.m); j++) {
/*  474: 796 */       if (sum2(p[j], ks, this.m - 1, true) > 1.E-015D)
/*  475:     */       {
/*  476: 797 */         steplsqr(b, pvt, ks, j, true);
/*  477: 798 */         ks++;
/*  478:     */       }
/*  479:     */       else
/*  480:     */       {
/*  481: 800 */         pvt.shiftToEnd(j);
/*  482: 801 */         pvt.setSize(pvt.size() - 1);
/*  483: 802 */         j--;
/*  484:     */       }
/*  485:     */     }
/*  486: 806 */     b.m = (this.m = ks);
/*  487: 807 */     pvt.setSize(ks);
/*  488:     */   }
/*  489:     */   
/*  490:     */   public void lsqrSelection(PaceMatrix b, IntVector pvt, int k0)
/*  491:     */   {
/*  492: 825 */     int numObs = this.m;
/*  493: 826 */     int numXs = pvt.size();
/*  494:     */     
/*  495: 828 */     lsqr(b, pvt, k0);
/*  496: 830 */     if ((numXs > 200) || (numXs > numObs)) {
/*  497: 831 */       forward(b, pvt, k0);
/*  498:     */     }
/*  499: 833 */     backward(b, pvt, pvt.size(), k0);
/*  500:     */   }
/*  501:     */   
/*  502:     */   public void positiveDiagonal(PaceMatrix Y, IntVector pvt)
/*  503:     */   {
/*  504: 845 */     int[] p = pvt.getArray();
/*  505: 846 */     for (int i = 0; i < pvt.size(); i++) {
/*  506: 847 */       if (this.A[i][p[i]] < 0.0D)
/*  507:     */       {
/*  508: 848 */         for (int j = i; j < pvt.size(); j++) {
/*  509: 849 */           this.A[i][p[j]] = (-this.A[i][p[j]]);
/*  510:     */         }
/*  511: 851 */         Y.A[i][0] = (-Y.A[i][0]);
/*  512:     */       }
/*  513:     */     }
/*  514:     */   }
/*  515:     */   
/*  516:     */   public void steplsqr(PaceMatrix b, IntVector pvt, int ks, int j, boolean adjoin)
/*  517:     */   {
/*  518: 867 */     int kp = pvt.size();
/*  519: 868 */     int[] p = pvt.getArray();
/*  520: 870 */     if (adjoin)
/*  521:     */     {
/*  522: 871 */       int pj = p[j];
/*  523: 872 */       pvt.swap(ks, j);
/*  524: 873 */       double[] dq = h1(pj, ks);
/*  525: 875 */       for (int k = ks + 1; k < kp; k++)
/*  526:     */       {
/*  527: 876 */         int pk = p[k];
/*  528: 877 */         h2(pj, ks, dq[1], this, pk);
/*  529:     */       }
/*  530: 879 */       h2(pj, ks, dq[1], b, 0);
/*  531: 880 */       this.A[ks][pj] = dq[0];
/*  532: 881 */       for (int k = ks + 1; k < this.m; k++) {
/*  533: 882 */         this.A[k][pj] = 0.0D;
/*  534:     */       }
/*  535:     */     }
/*  536:     */     else
/*  537:     */     {
/*  538: 885 */       int pj = p[j];
/*  539: 886 */       for (int i = j; i < ks - 1; i++) {
/*  540: 887 */         p[i] = p[(i + 1)];
/*  541:     */       }
/*  542: 889 */       p[(ks - 1)] = pj;
/*  543: 891 */       for (int i = j; i < ks - 1; i++)
/*  544:     */       {
/*  545: 892 */         double[] cs = g1(this.A[i][p[i]], this.A[(i + 1)][p[i]]);
/*  546: 893 */         for (int l = i; l < kp; l++) {
/*  547: 894 */           g2(cs, i, i + 1, p[l]);
/*  548:     */         }
/*  549: 896 */         for (int l = 0; l < b.n; l++) {
/*  550: 897 */           b.g2(cs, i, i + 1, l);
/*  551:     */         }
/*  552:     */       }
/*  553:     */     }
/*  554:     */   }
/*  555:     */   
/*  556:     */   public void rsolve(PaceMatrix b, IntVector pvt, int kp)
/*  557:     */   {
/*  558: 913 */     if (kp == 0) {
/*  559: 914 */       b.m = 0;
/*  560:     */     }
/*  561: 917 */     int[] p = pvt.getArray();
/*  562:     */     
/*  563: 919 */     double[][] ba = b.getArray();
/*  564: 920 */     for (int k = 0; k < b.n; k++)
/*  565:     */     {
/*  566: 921 */       ba[(kp - 1)][k] /= this.A[(kp - 1)][p[(kp - 1)]];
/*  567: 922 */       for (int i = kp - 2; i >= 0; i--)
/*  568:     */       {
/*  569: 923 */         double s = 0.0D;
/*  570: 924 */         for (int j = i + 1; j < kp; j++) {
/*  571: 925 */           s += this.A[i][p[j]] * ba[j][k];
/*  572:     */         }
/*  573: 927 */         ba[i][k] -= s;
/*  574: 928 */         ba[i][k] /= this.A[i][p[i]];
/*  575:     */       }
/*  576:     */     }
/*  577: 931 */     b.m = kp;
/*  578:     */   }
/*  579:     */   
/*  580:     */   public PaceMatrix rbind(PaceMatrix b)
/*  581:     */   {
/*  582: 941 */     if (this.n != b.n) {
/*  583: 942 */       throw new IllegalArgumentException("unequal numbers of rows.");
/*  584:     */     }
/*  585: 944 */     PaceMatrix c = new PaceMatrix(this.m + b.m, this.n);
/*  586: 945 */     c.setMatrix(0, this.m - 1, 0, this.n - 1, this);
/*  587: 946 */     c.setMatrix(this.m, this.m + b.m - 1, 0, this.n - 1, b);
/*  588: 947 */     return c;
/*  589:     */   }
/*  590:     */   
/*  591:     */   public PaceMatrix cbind(PaceMatrix b)
/*  592:     */   {
/*  593: 957 */     if (this.m != b.m) {
/*  594: 958 */       throw new IllegalArgumentException("unequal numbers of rows: " + this.m + " and " + b.m);
/*  595:     */     }
/*  596: 961 */     PaceMatrix c = new PaceMatrix(this.m, this.n + b.n);
/*  597: 962 */     c.setMatrix(0, this.m - 1, 0, this.n - 1, this);
/*  598: 963 */     c.setMatrix(0, this.m - 1, this.n, this.n + b.n - 1, b);
/*  599: 964 */     return c;
/*  600:     */   }
/*  601:     */   
/*  602:     */   public DoubleVector nnls(PaceMatrix b, IntVector pvt)
/*  603:     */   {
/*  604: 981 */     int counter = 0;int jm = -1;int n = pvt.size();
/*  605:     */     
/*  606: 983 */     int[] p = pvt.getArray();
/*  607: 984 */     DoubleVector x = new DoubleVector(n);
/*  608: 985 */     double[] xA = x.getArray();
/*  609: 986 */     PaceMatrix z = new PaceMatrix(n, 1);
/*  610:     */     
/*  611:     */ 
/*  612:     */ 
/*  613: 990 */     int kp = 0;
/*  614:     */     
/*  615: 992 */     counter++;
/*  616: 992 */     if (counter > 3 * n) {
/*  617: 993 */       throw new RuntimeException("Does not converge");
/*  618:     */     }
/*  619: 995 */     int t = -1;
/*  620: 996 */     double max = 0.0D;
/*  621: 997 */     PaceMatrix bt = new PaceMatrix(b.transpose());
/*  622: 998 */     for (int j = kp; j <= n - 1; j++)
/*  623:     */     {
/*  624: 999 */       double wj = bt.times(0, kp, this.m - 1, this, p[j]);
/*  625:1000 */       if (wj > max)
/*  626:     */       {
/*  627:1001 */         max = wj;
/*  628:1002 */         t = j;
/*  629:     */       }
/*  630:     */     }
/*  631:1007 */     if (t != -1)
/*  632:     */     {
/*  633:1012 */       pvt.swap(kp, t);
/*  634:1013 */       kp++;
/*  635:1014 */       xA[(kp - 1)] = 0.0D;
/*  636:1015 */       steplsqr(b, pvt, kp - 1, kp - 1, true);
/*  637:     */       
/*  638:1017 */       double ma = 0.0D;
/*  639:1018 */       while (ma < 1.5D)
/*  640:     */       {
/*  641:1019 */         for (j = 0; j <= kp - 1; j++) {
/*  642:1020 */           z.A[j][0] = b.A[j][0];
/*  643:     */         }
/*  644:1022 */         rsolve(z, pvt, kp);
/*  645:1023 */         ma = 2.0D;
/*  646:1024 */         jm = -1;
/*  647:1025 */         for (j = 0; j <= kp - 1; j++) {
/*  648:1026 */           if (z.A[j][0] <= 0.0D)
/*  649:     */           {
/*  650:1027 */             double alpha = xA[j] / (xA[j] - z.A[j][0]);
/*  651:1028 */             if (alpha < ma)
/*  652:     */             {
/*  653:1029 */               ma = alpha;
/*  654:1030 */               jm = j;
/*  655:     */             }
/*  656:     */           }
/*  657:     */         }
/*  658:1034 */         if (ma > 1.5D) {
/*  659:1035 */           for (j = 0; j <= kp - 1; j++) {
/*  660:1036 */             xA[j] = z.A[j][0];
/*  661:     */           }
/*  662:     */         } else {
/*  663:1039 */           for (j = kp - 1; j >= 0; j--) {
/*  664:1042 */             if (j == jm)
/*  665:     */             {
/*  666:1043 */               xA[j] = 0.0D;
/*  667:1044 */               steplsqr(b, pvt, kp, j, false);
/*  668:1045 */               kp--;
/*  669:     */             }
/*  670:     */             else
/*  671:     */             {
/*  672:1047 */               xA[j] += ma * (z.A[j][0] - xA[j]);
/*  673:     */             }
/*  674:     */           }
/*  675:     */         }
/*  676:     */       }
/*  677:     */     }
/*  678:1053 */     x.setSize(kp);
/*  679:1054 */     pvt.setSize(kp);
/*  680:1055 */     return x;
/*  681:     */   }
/*  682:     */   
/*  683:     */   public DoubleVector nnlse(PaceMatrix b, PaceMatrix c, PaceMatrix d, IntVector pvt)
/*  684:     */   {
/*  685:1073 */     double eps = 1.0E-010D * Math.max(c.maxAbs(), d.maxAbs()) / Math.max(maxAbs(), b.maxAbs());
/*  686:     */     
/*  687:     */ 
/*  688:1076 */     PaceMatrix e = c.rbind(new PaceMatrix(times(eps)));
/*  689:1077 */     PaceMatrix f = d.rbind(new PaceMatrix(b.times(eps)));
/*  690:     */     
/*  691:1079 */     return e.nnls(f, pvt);
/*  692:     */   }
/*  693:     */   
/*  694:     */   public DoubleVector nnlse1(PaceMatrix b, IntVector pvt)
/*  695:     */   {
/*  696:1094 */     PaceMatrix c = new PaceMatrix(1, this.n, 1.0D);
/*  697:1095 */     PaceMatrix d = new PaceMatrix(1, b.n, 1.0D);
/*  698:     */     
/*  699:1097 */     return nnlse(b, c, d, pvt);
/*  700:     */   }
/*  701:     */   
/*  702:     */   public static Matrix randomNormal(int m, int n)
/*  703:     */   {
/*  704:1108 */     Random random = new Random();
/*  705:     */     
/*  706:1110 */     Matrix A = new Matrix(m, n);
/*  707:1111 */     double[][] X = A.getArray();
/*  708:1112 */     for (int i = 0; i < m; i++) {
/*  709:1113 */       for (int j = 0; j < n; j++) {
/*  710:1114 */         X[i][j] = random.nextGaussian();
/*  711:     */       }
/*  712:     */     }
/*  713:1117 */     return A;
/*  714:     */   }
/*  715:     */   
/*  716:     */   public String getRevision()
/*  717:     */   {
/*  718:1127 */     return RevisionUtils.extract("$Revision: 10374 $");
/*  719:     */   }
/*  720:     */   
/*  721:     */   public static void main(String[] args)
/*  722:     */   {
/*  723:1136 */     System.out.println("===========================================================");
/*  724:     */     
/*  725:1138 */     System.out.println("To test the pace estimators of linear model\ncoefficients.\n");
/*  726:     */     
/*  727:     */ 
/*  728:1141 */     double sd = 2.0D;
/*  729:1142 */     int n = 200;
/*  730:1143 */     double beta0 = 100.0D;
/*  731:1144 */     int k1 = 20;
/*  732:1145 */     double beta1 = 0.0D;
/*  733:1146 */     int k2 = 20;
/*  734:1147 */     double beta2 = 5.0D;
/*  735:1148 */     int k = 1 + k1 + k2;
/*  736:     */     
/*  737:1150 */     DoubleVector beta = new DoubleVector(1 + k1 + k2);
/*  738:1151 */     beta.set(0, beta0);
/*  739:1152 */     beta.set(1, k1, beta1);
/*  740:1153 */     beta.set(k1 + 1, k1 + k2, beta2);
/*  741:     */     
/*  742:1155 */     System.out.println("The data set contains " + n + " observations plus " + (k1 + k2) + " variables.\n\nThe coefficients of the true model" + " are:\n\n" + beta);
/*  743:     */     
/*  744:     */ 
/*  745:     */ 
/*  746:1159 */     System.out.println("\nThe standard deviation of the error term is " + sd);
/*  747:     */     
/*  748:1161 */     System.out.println("===========================================================");
/*  749:     */     
/*  750:     */ 
/*  751:1164 */     PaceMatrix X = new PaceMatrix(n, k1 + k2 + 1);
/*  752:1165 */     X.setMatrix(0, n - 1, 0, 0, 1.0D);
/*  753:1166 */     X.setMatrix(0, n - 1, 1, k1 + k2, random(n, k1 + k2));
/*  754:     */     
/*  755:1168 */     PaceMatrix Y = new PaceMatrix(X.times(new PaceMatrix(beta)).plusEquals(randomNormal(n, 1).times(sd)));
/*  756:     */     
/*  757:     */ 
/*  758:1171 */     IntVector pvt = IntVector.seq(0, k1 + k2);
/*  759:     */     
/*  760:     */ 
/*  761:     */ 
/*  762:     */ 
/*  763:     */ 
/*  764:     */ 
/*  765:1178 */     X.lsqrSelection(Y, pvt, 1);
/*  766:1179 */     X.positiveDiagonal(Y, pvt);
/*  767:     */     
/*  768:1181 */     PaceMatrix sol = (PaceMatrix)Y.clone();
/*  769:1182 */     X.rsolve(sol, pvt, pvt.size());
/*  770:1183 */     DoubleVector betaHat = sol.getColumn(0).unpivoting(pvt, k);
/*  771:1184 */     System.out.println("\nThe OLS estimate (through lsqr()) is: \n\n" + betaHat);
/*  772:     */     
/*  773:     */ 
/*  774:1187 */     System.out.println("\nQuadratic loss of the OLS estimate (||X b - X bHat||^2) = " + new PaceMatrix(X.times(new PaceMatrix(beta.minus(betaHat)))).getColumn(0).sum2());
/*  775:     */     
/*  776:     */ 
/*  777:     */ 
/*  778:     */ 
/*  779:1192 */     System.out.println("===========================================================");
/*  780:     */     
/*  781:1194 */     System.out.println("             *** Pace estimation *** \n");
/*  782:1195 */     DoubleVector r = Y.getColumn(pvt.size(), n - 1, 0);
/*  783:1196 */     double sde = Math.sqrt(r.sum2() / r.size());
/*  784:     */     
/*  785:1198 */     System.out.println("Estimated standard deviation = " + sde);
/*  786:     */     
/*  787:1200 */     DoubleVector aHat = Y.getColumn(0, pvt.size() - 1, 0).times(1.0D / sde);
/*  788:1201 */     System.out.println("\naHat = \n" + aHat);
/*  789:     */     
/*  790:1203 */     System.out.println("\n========= Based on chi-square mixture ============");
/*  791:     */     
/*  792:1205 */     ChisqMixture d2 = new ChisqMixture();
/*  793:1206 */     int method = 1;
/*  794:1207 */     DoubleVector AHat = aHat.square();
/*  795:1208 */     d2.fit(AHat, method);
/*  796:1209 */     System.out.println("\nEstimated mixing distribution is:\n" + d2);
/*  797:     */     
/*  798:1211 */     DoubleVector ATilde = d2.pace2(AHat);
/*  799:1212 */     DoubleVector aTilde = ATilde.sqrt().times(aHat.sign());
/*  800:1213 */     PaceMatrix YTilde = new PaceMatrix(new PaceMatrix(aTilde).times(sde));
/*  801:1214 */     X.rsolve(YTilde, pvt, pvt.size());
/*  802:1215 */     DoubleVector betaTilde = YTilde.getColumn(0).unpivoting(pvt, k);
/*  803:1216 */     System.out.println("\nThe pace2 estimate of coefficients = \n" + betaTilde);
/*  804:1217 */     System.out.println("Quadratic loss = " + new PaceMatrix(X.times(new PaceMatrix(beta.minus(betaTilde)))).getColumn(0).sum2());
/*  805:     */     
/*  806:     */ 
/*  807:     */ 
/*  808:1221 */     ATilde = d2.pace4(AHat);
/*  809:1222 */     aTilde = ATilde.sqrt().times(aHat.sign());
/*  810:1223 */     YTilde = new PaceMatrix(new PaceMatrix(aTilde).times(sde));
/*  811:1224 */     X.rsolve(YTilde, pvt, pvt.size());
/*  812:1225 */     betaTilde = YTilde.getColumn(0).unpivoting(pvt, k);
/*  813:1226 */     System.out.println("\nThe pace4 estimate of coefficients = \n" + betaTilde);
/*  814:1227 */     System.out.println("Quadratic loss = " + new PaceMatrix(X.times(new PaceMatrix(beta.minus(betaTilde)))).getColumn(0).sum2());
/*  815:     */     
/*  816:     */ 
/*  817:     */ 
/*  818:1231 */     ATilde = d2.pace6(AHat);
/*  819:1232 */     aTilde = ATilde.sqrt().times(aHat.sign());
/*  820:1233 */     YTilde = new PaceMatrix(new PaceMatrix(aTilde).times(sde));
/*  821:1234 */     X.rsolve(YTilde, pvt, pvt.size());
/*  822:1235 */     betaTilde = YTilde.getColumn(0).unpivoting(pvt, k);
/*  823:1236 */     System.out.println("\nThe pace6 estimate of coefficients = \n" + betaTilde);
/*  824:1237 */     System.out.println("Quadratic loss = " + new PaceMatrix(X.times(new PaceMatrix(beta.minus(betaTilde)))).getColumn(0).sum2());
/*  825:     */     
/*  826:     */ 
/*  827:     */ 
/*  828:1241 */     System.out.println("\n========= Based on normal mixture ============");
/*  829:     */     
/*  830:1243 */     NormalMixture d = new NormalMixture();
/*  831:1244 */     d.fit(aHat, method);
/*  832:1245 */     System.out.println("\nEstimated mixing distribution is:\n" + d);
/*  833:     */     
/*  834:1247 */     aTilde = d.nestedEstimate(aHat);
/*  835:1248 */     YTilde = new PaceMatrix(new PaceMatrix(aTilde).times(sde));
/*  836:1249 */     X.rsolve(YTilde, pvt, pvt.size());
/*  837:1250 */     betaTilde = YTilde.getColumn(0).unpivoting(pvt, k);
/*  838:1251 */     System.out.println("The nested estimate of coefficients = \n" + betaTilde);
/*  839:1252 */     System.out.println("Quadratic loss = " + new PaceMatrix(X.times(new PaceMatrix(beta.minus(betaTilde)))).getColumn(0).sum2());
/*  840:     */     
/*  841:     */ 
/*  842:     */ 
/*  843:1256 */     aTilde = d.subsetEstimate(aHat);
/*  844:1257 */     YTilde = new PaceMatrix(new PaceMatrix(aTilde).times(sde));
/*  845:1258 */     X.rsolve(YTilde, pvt, pvt.size());
/*  846:1259 */     betaTilde = YTilde.getColumn(0).unpivoting(pvt, k);
/*  847:1260 */     System.out.println("\nThe subset estimate of coefficients = \n" + betaTilde);
/*  848:     */     
/*  849:1262 */     System.out.println("Quadratic loss = " + new PaceMatrix(X.times(new PaceMatrix(beta.minus(betaTilde)))).getColumn(0).sum2());
/*  850:     */     
/*  851:     */ 
/*  852:     */ 
/*  853:1266 */     aTilde = d.empiricalBayesEstimate(aHat);
/*  854:1267 */     YTilde = new PaceMatrix(new PaceMatrix(aTilde).times(sde));
/*  855:1268 */     X.rsolve(YTilde, pvt, pvt.size());
/*  856:1269 */     betaTilde = YTilde.getColumn(0).unpivoting(pvt, k);
/*  857:1270 */     System.out.println("\nThe empirical Bayes estimate of coefficients = \n" + betaTilde);
/*  858:     */     
/*  859:     */ 
/*  860:1273 */     System.out.println("Quadratic loss = " + new PaceMatrix(X.times(new PaceMatrix(beta.minus(betaTilde)))).getColumn(0).sum2());
/*  861:     */   }
/*  862:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.pace.PaceMatrix
 * JD-Core Version:    0.7.0.1
 */