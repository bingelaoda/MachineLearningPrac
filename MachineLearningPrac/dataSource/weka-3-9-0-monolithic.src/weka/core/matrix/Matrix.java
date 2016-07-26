/*    1:     */ package weka.core.matrix;
/*    2:     */ 
/*    3:     */ import java.io.BufferedReader;
/*    4:     */ import java.io.IOException;
/*    5:     */ import java.io.LineNumberReader;
/*    6:     */ import java.io.PrintStream;
/*    7:     */ import java.io.PrintWriter;
/*    8:     */ import java.io.Reader;
/*    9:     */ import java.io.Serializable;
/*   10:     */ import java.io.StreamTokenizer;
/*   11:     */ import java.io.StringReader;
/*   12:     */ import java.io.StringWriter;
/*   13:     */ import java.io.Writer;
/*   14:     */ import java.text.DecimalFormat;
/*   15:     */ import java.text.DecimalFormatSymbols;
/*   16:     */ import java.text.NumberFormat;
/*   17:     */ import java.util.Locale;
/*   18:     */ import java.util.StringTokenizer;
/*   19:     */ import java.util.Vector;
/*   20:     */ import weka.core.RevisionHandler;
/*   21:     */ import weka.core.RevisionUtils;
/*   22:     */ import weka.core.Utils;
/*   23:     */ 
/*   24:     */ public class Matrix
/*   25:     */   implements Cloneable, Serializable, RevisionHandler
/*   26:     */ {
/*   27:     */   private static final long serialVersionUID = 7856794138418366180L;
/*   28:     */   protected double[][] A;
/*   29:     */   protected int m;
/*   30:     */   protected int n;
/*   31:     */   
/*   32:     */   public Matrix(int m, int n)
/*   33:     */   {
/*   34: 117 */     this.m = m;
/*   35: 118 */     this.n = n;
/*   36: 119 */     this.A = new double[m][n];
/*   37:     */   }
/*   38:     */   
/*   39:     */   public Matrix(int m, int n, double s)
/*   40:     */   {
/*   41: 130 */     this.m = m;
/*   42: 131 */     this.n = n;
/*   43: 132 */     this.A = new double[m][n];
/*   44: 133 */     for (int i = 0; i < m; i++) {
/*   45: 134 */       for (int j = 0; j < n; j++) {
/*   46: 135 */         this.A[i][j] = s;
/*   47:     */       }
/*   48:     */     }
/*   49:     */   }
/*   50:     */   
/*   51:     */   public Matrix(double[][] A)
/*   52:     */   {
/*   53: 148 */     this.m = A.length;
/*   54: 149 */     this.n = A[0].length;
/*   55: 150 */     for (int i = 0; i < this.m; i++) {
/*   56: 151 */       if (A[i].length != this.n) {
/*   57: 152 */         throw new IllegalArgumentException("All rows must have the same length.");
/*   58:     */       }
/*   59:     */     }
/*   60: 156 */     this.A = A;
/*   61:     */   }
/*   62:     */   
/*   63:     */   public Matrix(double[][] A, int m, int n)
/*   64:     */   {
/*   65: 167 */     this.A = A;
/*   66: 168 */     this.m = m;
/*   67: 169 */     this.n = n;
/*   68:     */   }
/*   69:     */   
/*   70:     */   public Matrix(double[] vals, int m)
/*   71:     */   {
/*   72: 181 */     this.m = m;
/*   73: 182 */     this.n = (m != 0 ? vals.length / m : 0);
/*   74: 183 */     if (m * this.n != vals.length) {
/*   75: 184 */       throw new IllegalArgumentException("Array length must be a multiple of m.");
/*   76:     */     }
/*   77: 187 */     this.A = new double[m][this.n];
/*   78: 188 */     for (int i = 0; i < m; i++) {
/*   79: 189 */       for (int j = 0; j < this.n; j++) {
/*   80: 190 */         this.A[i][j] = vals[(i + j * m)];
/*   81:     */       }
/*   82:     */     }
/*   83:     */   }
/*   84:     */   
/*   85:     */   public Matrix(Reader r)
/*   86:     */     throws Exception
/*   87:     */   {
/*   88: 205 */     LineNumberReader lnr = new LineNumberReader(r);
/*   89:     */     
/*   90: 207 */     int currentRow = -1;
/*   91:     */     String line;
/*   92: 209 */     while ((line = lnr.readLine()) != null) {
/*   93: 212 */       if (!line.startsWith("%"))
/*   94:     */       {
/*   95: 216 */         StringTokenizer st = new StringTokenizer(line);
/*   96: 218 */         if (st.hasMoreTokens()) {
/*   97: 222 */           if (currentRow < 0)
/*   98:     */           {
/*   99: 223 */             int rows = Integer.parseInt(st.nextToken());
/*  100: 224 */             if (!st.hasMoreTokens()) {
/*  101: 225 */               throw new Exception("Line " + lnr.getLineNumber() + ": expected number of columns");
/*  102:     */             }
/*  103: 229 */             int cols = Integer.parseInt(st.nextToken());
/*  104: 230 */             this.A = new double[rows][cols];
/*  105: 231 */             this.m = rows;
/*  106: 232 */             this.n = cols;
/*  107: 233 */             currentRow++;
/*  108:     */           }
/*  109:     */           else
/*  110:     */           {
/*  111: 237 */             if (currentRow == getRowDimension()) {
/*  112: 238 */               throw new Exception("Line " + lnr.getLineNumber() + ": too many rows provided");
/*  113:     */             }
/*  114: 242 */             for (int i = 0; i < getColumnDimension(); i++)
/*  115:     */             {
/*  116: 243 */               if (!st.hasMoreTokens()) {
/*  117: 244 */                 throw new Exception("Line " + lnr.getLineNumber() + ": too few matrix elements provided");
/*  118:     */               }
/*  119: 248 */               set(currentRow, i, Double.valueOf(st.nextToken()).doubleValue());
/*  120:     */             }
/*  121: 250 */             currentRow++;
/*  122:     */           }
/*  123:     */         }
/*  124:     */       }
/*  125:     */     }
/*  126: 254 */     if (currentRow == -1) {
/*  127: 255 */       throw new Exception("Line " + lnr.getLineNumber() + ": expected number of rows");
/*  128:     */     }
/*  129: 257 */     if (currentRow != getRowDimension()) {
/*  130: 258 */       throw new Exception("Line " + lnr.getLineNumber() + ": too few rows provided");
/*  131:     */     }
/*  132:     */   }
/*  133:     */   
/*  134:     */   public static Matrix constructWithCopy(double[][] A)
/*  135:     */   {
/*  136: 270 */     int m = A.length;
/*  137: 271 */     int n = A[0].length;
/*  138: 272 */     Matrix X = new Matrix(m, n);
/*  139: 273 */     double[][] C = X.getArray();
/*  140: 274 */     for (int i = 0; i < m; i++)
/*  141:     */     {
/*  142: 275 */       if (A[i].length != n) {
/*  143: 276 */         throw new IllegalArgumentException("All rows must have the same length.");
/*  144:     */       }
/*  145: 279 */       for (int j = 0; j < n; j++) {
/*  146: 280 */         C[i][j] = A[i][j];
/*  147:     */       }
/*  148:     */     }
/*  149: 283 */     return X;
/*  150:     */   }
/*  151:     */   
/*  152:     */   public Matrix copy()
/*  153:     */   {
/*  154: 290 */     Matrix X = new Matrix(this.m, this.n);
/*  155: 291 */     double[][] C = X.getArray();
/*  156: 292 */     for (int i = 0; i < this.m; i++) {
/*  157: 293 */       for (int j = 0; j < this.n; j++) {
/*  158: 294 */         C[i][j] = this.A[i][j];
/*  159:     */       }
/*  160:     */     }
/*  161: 297 */     return X;
/*  162:     */   }
/*  163:     */   
/*  164:     */   public Object clone()
/*  165:     */   {
/*  166: 305 */     return copy();
/*  167:     */   }
/*  168:     */   
/*  169:     */   public double[][] getArray()
/*  170:     */   {
/*  171: 314 */     return this.A;
/*  172:     */   }
/*  173:     */   
/*  174:     */   public double[][] getArrayCopy()
/*  175:     */   {
/*  176: 323 */     double[][] C = new double[this.m][this.n];
/*  177: 324 */     for (int i = 0; i < this.m; i++) {
/*  178: 325 */       for (int j = 0; j < this.n; j++) {
/*  179: 326 */         C[i][j] = this.A[i][j];
/*  180:     */       }
/*  181:     */     }
/*  182: 329 */     return C;
/*  183:     */   }
/*  184:     */   
/*  185:     */   public double[] getColumnPackedCopy()
/*  186:     */   {
/*  187: 338 */     double[] vals = new double[this.m * this.n];
/*  188: 339 */     for (int i = 0; i < this.m; i++) {
/*  189: 340 */       for (int j = 0; j < this.n; j++) {
/*  190: 341 */         vals[(i + j * this.m)] = this.A[i][j];
/*  191:     */       }
/*  192:     */     }
/*  193: 344 */     return vals;
/*  194:     */   }
/*  195:     */   
/*  196:     */   public double[] getRowPackedCopy()
/*  197:     */   {
/*  198: 353 */     double[] vals = new double[this.m * this.n];
/*  199: 354 */     for (int i = 0; i < this.m; i++) {
/*  200: 355 */       for (int j = 0; j < this.n; j++) {
/*  201: 356 */         vals[(i * this.n + j)] = this.A[i][j];
/*  202:     */       }
/*  203:     */     }
/*  204: 359 */     return vals;
/*  205:     */   }
/*  206:     */   
/*  207:     */   public int getRowDimension()
/*  208:     */   {
/*  209: 368 */     return this.m;
/*  210:     */   }
/*  211:     */   
/*  212:     */   public int getColumnDimension()
/*  213:     */   {
/*  214: 377 */     return this.n;
/*  215:     */   }
/*  216:     */   
/*  217:     */   public double get(int i, int j)
/*  218:     */   {
/*  219: 389 */     return this.A[i][j];
/*  220:     */   }
/*  221:     */   
/*  222:     */   public Matrix getMatrix(int i0, int i1, int j0, int j1)
/*  223:     */   {
/*  224: 403 */     Matrix X = new Matrix(i1 - i0 + 1, j1 - j0 + 1);
/*  225: 404 */     double[][] B = X.getArray();
/*  226:     */     try
/*  227:     */     {
/*  228: 406 */       for (int i = i0; i <= i1; i++) {
/*  229: 407 */         for (int j = j0; j <= j1; j++) {
/*  230: 408 */           B[(i - i0)][(j - j0)] = this.A[i][j];
/*  231:     */         }
/*  232:     */       }
/*  233:     */     }
/*  234:     */     catch (ArrayIndexOutOfBoundsException e)
/*  235:     */     {
/*  236: 412 */       throw new ArrayIndexOutOfBoundsException("Submatrix indices");
/*  237:     */     }
/*  238: 414 */     return X;
/*  239:     */   }
/*  240:     */   
/*  241:     */   public Matrix getMatrix(int[] r, int[] c)
/*  242:     */   {
/*  243: 426 */     Matrix X = new Matrix(r.length, c.length);
/*  244: 427 */     double[][] B = X.getArray();
/*  245:     */     try
/*  246:     */     {
/*  247: 429 */       for (int i = 0; i < r.length; i++) {
/*  248: 430 */         for (int j = 0; j < c.length; j++) {
/*  249: 431 */           B[i][j] = this.A[r[i]][c[j]];
/*  250:     */         }
/*  251:     */       }
/*  252:     */     }
/*  253:     */     catch (ArrayIndexOutOfBoundsException e)
/*  254:     */     {
/*  255: 435 */       throw new ArrayIndexOutOfBoundsException("Submatrix indices");
/*  256:     */     }
/*  257: 437 */     return X;
/*  258:     */   }
/*  259:     */   
/*  260:     */   public Matrix getMatrix(int i0, int i1, int[] c)
/*  261:     */   {
/*  262: 450 */     Matrix X = new Matrix(i1 - i0 + 1, c.length);
/*  263: 451 */     double[][] B = X.getArray();
/*  264:     */     try
/*  265:     */     {
/*  266: 453 */       for (int i = i0; i <= i1; i++) {
/*  267: 454 */         for (int j = 0; j < c.length; j++) {
/*  268: 455 */           B[(i - i0)][j] = this.A[i][c[j]];
/*  269:     */         }
/*  270:     */       }
/*  271:     */     }
/*  272:     */     catch (ArrayIndexOutOfBoundsException e)
/*  273:     */     {
/*  274: 459 */       throw new ArrayIndexOutOfBoundsException("Submatrix indices");
/*  275:     */     }
/*  276: 461 */     return X;
/*  277:     */   }
/*  278:     */   
/*  279:     */   public Matrix getMatrix(int[] r, int j0, int j1)
/*  280:     */   {
/*  281: 474 */     Matrix X = new Matrix(r.length, j1 - j0 + 1);
/*  282: 475 */     double[][] B = X.getArray();
/*  283:     */     try
/*  284:     */     {
/*  285: 477 */       for (int i = 0; i < r.length; i++) {
/*  286: 478 */         for (int j = j0; j <= j1; j++) {
/*  287: 479 */           B[i][(j - j0)] = this.A[r[i]][j];
/*  288:     */         }
/*  289:     */       }
/*  290:     */     }
/*  291:     */     catch (ArrayIndexOutOfBoundsException e)
/*  292:     */     {
/*  293: 483 */       throw new ArrayIndexOutOfBoundsException("Submatrix indices");
/*  294:     */     }
/*  295: 485 */     return X;
/*  296:     */   }
/*  297:     */   
/*  298:     */   public void set(int i, int j, double s)
/*  299:     */   {
/*  300: 497 */     this.A[i][j] = s;
/*  301:     */   }
/*  302:     */   
/*  303:     */   public void setMatrix(int i0, int i1, int j0, int j1, Matrix X)
/*  304:     */   {
/*  305:     */     try
/*  306:     */     {
/*  307: 512 */       for (int i = i0; i <= i1; i++) {
/*  308: 513 */         for (int j = j0; j <= j1; j++) {
/*  309: 514 */           this.A[i][j] = X.get(i - i0, j - j0);
/*  310:     */         }
/*  311:     */       }
/*  312:     */     }
/*  313:     */     catch (ArrayIndexOutOfBoundsException e)
/*  314:     */     {
/*  315: 518 */       throw new ArrayIndexOutOfBoundsException("Submatrix indices");
/*  316:     */     }
/*  317:     */   }
/*  318:     */   
/*  319:     */   public void setMatrix(int[] r, int[] c, Matrix X)
/*  320:     */   {
/*  321:     */     try
/*  322:     */     {
/*  323: 532 */       for (int i = 0; i < r.length; i++) {
/*  324: 533 */         for (int j = 0; j < c.length; j++) {
/*  325: 534 */           this.A[r[i]][c[j]] = X.get(i, j);
/*  326:     */         }
/*  327:     */       }
/*  328:     */     }
/*  329:     */     catch (ArrayIndexOutOfBoundsException e)
/*  330:     */     {
/*  331: 538 */       throw new ArrayIndexOutOfBoundsException("Submatrix indices");
/*  332:     */     }
/*  333:     */   }
/*  334:     */   
/*  335:     */   public void setMatrix(int[] r, int j0, int j1, Matrix X)
/*  336:     */   {
/*  337:     */     try
/*  338:     */     {
/*  339: 553 */       for (int i = 0; i < r.length; i++) {
/*  340: 554 */         for (int j = j0; j <= j1; j++) {
/*  341: 555 */           this.A[r[i]][j] = X.get(i, j - j0);
/*  342:     */         }
/*  343:     */       }
/*  344:     */     }
/*  345:     */     catch (ArrayIndexOutOfBoundsException e)
/*  346:     */     {
/*  347: 559 */       throw new ArrayIndexOutOfBoundsException("Submatrix indices");
/*  348:     */     }
/*  349:     */   }
/*  350:     */   
/*  351:     */   public void setMatrix(int i0, int i1, int[] c, Matrix X)
/*  352:     */   {
/*  353:     */     try
/*  354:     */     {
/*  355: 574 */       for (int i = i0; i <= i1; i++) {
/*  356: 575 */         for (int j = 0; j < c.length; j++) {
/*  357: 576 */           this.A[i][c[j]] = X.get(i - i0, j);
/*  358:     */         }
/*  359:     */       }
/*  360:     */     }
/*  361:     */     catch (ArrayIndexOutOfBoundsException e)
/*  362:     */     {
/*  363: 580 */       throw new ArrayIndexOutOfBoundsException("Submatrix indices");
/*  364:     */     }
/*  365:     */   }
/*  366:     */   
/*  367:     */   public boolean isSymmetric()
/*  368:     */   {
/*  369: 591 */     int nr = this.A.length;int nc = this.A[0].length;
/*  370: 592 */     if (nr != nc) {
/*  371: 593 */       return false;
/*  372:     */     }
/*  373: 596 */     for (int i = 0; i < nc; i++) {
/*  374: 597 */       for (int j = 0; j < i; j++) {
/*  375: 598 */         if (this.A[i][j] != this.A[j][i]) {
/*  376: 599 */           return false;
/*  377:     */         }
/*  378:     */       }
/*  379:     */     }
/*  380: 603 */     return true;
/*  381:     */   }
/*  382:     */   
/*  383:     */   public boolean isSquare()
/*  384:     */   {
/*  385: 612 */     return getRowDimension() == getColumnDimension();
/*  386:     */   }
/*  387:     */   
/*  388:     */   public Matrix transpose()
/*  389:     */   {
/*  390: 621 */     Matrix X = new Matrix(this.n, this.m);
/*  391: 622 */     double[][] C = X.getArray();
/*  392: 623 */     for (int i = 0; i < this.m; i++) {
/*  393: 624 */       for (int j = 0; j < this.n; j++) {
/*  394: 625 */         C[j][i] = this.A[i][j];
/*  395:     */       }
/*  396:     */     }
/*  397: 628 */     return X;
/*  398:     */   }
/*  399:     */   
/*  400:     */   public double norm1()
/*  401:     */   {
/*  402: 637 */     double f = 0.0D;
/*  403: 638 */     for (int j = 0; j < this.n; j++)
/*  404:     */     {
/*  405: 639 */       double s = 0.0D;
/*  406: 640 */       for (int i = 0; i < this.m; i++) {
/*  407: 641 */         s += Math.abs(this.A[i][j]);
/*  408:     */       }
/*  409: 643 */       f = Math.max(f, s);
/*  410:     */     }
/*  411: 645 */     return f;
/*  412:     */   }
/*  413:     */   
/*  414:     */   public double norm2()
/*  415:     */   {
/*  416: 654 */     return new SingularValueDecomposition(this).norm2();
/*  417:     */   }
/*  418:     */   
/*  419:     */   public double normInf()
/*  420:     */   {
/*  421: 663 */     double f = 0.0D;
/*  422: 664 */     for (int i = 0; i < this.m; i++)
/*  423:     */     {
/*  424: 665 */       double s = 0.0D;
/*  425: 666 */       for (int j = 0; j < this.n; j++) {
/*  426: 667 */         s += Math.abs(this.A[i][j]);
/*  427:     */       }
/*  428: 669 */       f = Math.max(f, s);
/*  429:     */     }
/*  430: 671 */     return f;
/*  431:     */   }
/*  432:     */   
/*  433:     */   public double normF()
/*  434:     */   {
/*  435: 680 */     double f = 0.0D;
/*  436: 681 */     for (int i = 0; i < this.m; i++) {
/*  437: 682 */       for (int j = 0; j < this.n; j++) {
/*  438: 683 */         f = Maths.hypot(f, this.A[i][j]);
/*  439:     */       }
/*  440:     */     }
/*  441: 686 */     return f;
/*  442:     */   }
/*  443:     */   
/*  444:     */   public Matrix uminus()
/*  445:     */   {
/*  446: 695 */     Matrix X = new Matrix(this.m, this.n);
/*  447: 696 */     double[][] C = X.getArray();
/*  448: 697 */     for (int i = 0; i < this.m; i++) {
/*  449: 698 */       for (int j = 0; j < this.n; j++) {
/*  450: 699 */         C[i][j] = (-this.A[i][j]);
/*  451:     */       }
/*  452:     */     }
/*  453: 702 */     return X;
/*  454:     */   }
/*  455:     */   
/*  456:     */   public Matrix plus(Matrix B)
/*  457:     */   {
/*  458: 712 */     checkMatrixDimensions(B);
/*  459: 713 */     Matrix X = new Matrix(this.m, this.n);
/*  460: 714 */     double[][] C = X.getArray();
/*  461: 715 */     for (int i = 0; i < this.m; i++) {
/*  462: 716 */       for (int j = 0; j < this.n; j++) {
/*  463: 717 */         C[i][j] = (this.A[i][j] + B.A[i][j]);
/*  464:     */       }
/*  465:     */     }
/*  466: 720 */     return X;
/*  467:     */   }
/*  468:     */   
/*  469:     */   public Matrix plusEquals(Matrix B)
/*  470:     */   {
/*  471: 730 */     checkMatrixDimensions(B);
/*  472: 731 */     for (int i = 0; i < this.m; i++) {
/*  473: 732 */       for (int j = 0; j < this.n; j++) {
/*  474: 733 */         this.A[i][j] += B.A[i][j];
/*  475:     */       }
/*  476:     */     }
/*  477: 736 */     return this;
/*  478:     */   }
/*  479:     */   
/*  480:     */   public Matrix minus(Matrix B)
/*  481:     */   {
/*  482: 746 */     checkMatrixDimensions(B);
/*  483: 747 */     Matrix X = new Matrix(this.m, this.n);
/*  484: 748 */     double[][] C = X.getArray();
/*  485: 749 */     for (int i = 0; i < this.m; i++) {
/*  486: 750 */       for (int j = 0; j < this.n; j++) {
/*  487: 751 */         C[i][j] = (this.A[i][j] - B.A[i][j]);
/*  488:     */       }
/*  489:     */     }
/*  490: 754 */     return X;
/*  491:     */   }
/*  492:     */   
/*  493:     */   public Matrix minusEquals(Matrix B)
/*  494:     */   {
/*  495: 764 */     checkMatrixDimensions(B);
/*  496: 765 */     for (int i = 0; i < this.m; i++) {
/*  497: 766 */       for (int j = 0; j < this.n; j++) {
/*  498: 767 */         this.A[i][j] -= B.A[i][j];
/*  499:     */       }
/*  500:     */     }
/*  501: 770 */     return this;
/*  502:     */   }
/*  503:     */   
/*  504:     */   public Matrix arrayTimes(Matrix B)
/*  505:     */   {
/*  506: 780 */     checkMatrixDimensions(B);
/*  507: 781 */     Matrix X = new Matrix(this.m, this.n);
/*  508: 782 */     double[][] C = X.getArray();
/*  509: 783 */     for (int i = 0; i < this.m; i++) {
/*  510: 784 */       for (int j = 0; j < this.n; j++) {
/*  511: 785 */         C[i][j] = (this.A[i][j] * B.A[i][j]);
/*  512:     */       }
/*  513:     */     }
/*  514: 788 */     return X;
/*  515:     */   }
/*  516:     */   
/*  517:     */   public Matrix arrayTimesEquals(Matrix B)
/*  518:     */   {
/*  519: 798 */     checkMatrixDimensions(B);
/*  520: 799 */     for (int i = 0; i < this.m; i++) {
/*  521: 800 */       for (int j = 0; j < this.n; j++) {
/*  522: 801 */         this.A[i][j] *= B.A[i][j];
/*  523:     */       }
/*  524:     */     }
/*  525: 804 */     return this;
/*  526:     */   }
/*  527:     */   
/*  528:     */   public Matrix arrayRightDivide(Matrix B)
/*  529:     */   {
/*  530: 814 */     checkMatrixDimensions(B);
/*  531: 815 */     Matrix X = new Matrix(this.m, this.n);
/*  532: 816 */     double[][] C = X.getArray();
/*  533: 817 */     for (int i = 0; i < this.m; i++) {
/*  534: 818 */       for (int j = 0; j < this.n; j++) {
/*  535: 819 */         C[i][j] = (this.A[i][j] / B.A[i][j]);
/*  536:     */       }
/*  537:     */     }
/*  538: 822 */     return X;
/*  539:     */   }
/*  540:     */   
/*  541:     */   public Matrix arrayRightDivideEquals(Matrix B)
/*  542:     */   {
/*  543: 832 */     checkMatrixDimensions(B);
/*  544: 833 */     for (int i = 0; i < this.m; i++) {
/*  545: 834 */       for (int j = 0; j < this.n; j++) {
/*  546: 835 */         this.A[i][j] /= B.A[i][j];
/*  547:     */       }
/*  548:     */     }
/*  549: 838 */     return this;
/*  550:     */   }
/*  551:     */   
/*  552:     */   public Matrix arrayLeftDivide(Matrix B)
/*  553:     */   {
/*  554: 848 */     checkMatrixDimensions(B);
/*  555: 849 */     Matrix X = new Matrix(this.m, this.n);
/*  556: 850 */     double[][] C = X.getArray();
/*  557: 851 */     for (int i = 0; i < this.m; i++) {
/*  558: 852 */       for (int j = 0; j < this.n; j++) {
/*  559: 853 */         C[i][j] = (B.A[i][j] / this.A[i][j]);
/*  560:     */       }
/*  561:     */     }
/*  562: 856 */     return X;
/*  563:     */   }
/*  564:     */   
/*  565:     */   public Matrix arrayLeftDivideEquals(Matrix B)
/*  566:     */   {
/*  567: 866 */     checkMatrixDimensions(B);
/*  568: 867 */     for (int i = 0; i < this.m; i++) {
/*  569: 868 */       for (int j = 0; j < this.n; j++) {
/*  570: 869 */         B.A[i][j] /= this.A[i][j];
/*  571:     */       }
/*  572:     */     }
/*  573: 872 */     return this;
/*  574:     */   }
/*  575:     */   
/*  576:     */   public Matrix times(double s)
/*  577:     */   {
/*  578: 882 */     Matrix X = new Matrix(this.m, this.n);
/*  579: 883 */     double[][] C = X.getArray();
/*  580: 884 */     for (int i = 0; i < this.m; i++) {
/*  581: 885 */       for (int j = 0; j < this.n; j++) {
/*  582: 886 */         C[i][j] = (s * this.A[i][j]);
/*  583:     */       }
/*  584:     */     }
/*  585: 889 */     return X;
/*  586:     */   }
/*  587:     */   
/*  588:     */   public Matrix timesEquals(double s)
/*  589:     */   {
/*  590: 899 */     for (int i = 0; i < this.m; i++) {
/*  591: 900 */       for (int j = 0; j < this.n; j++) {
/*  592: 901 */         this.A[i][j] = (s * this.A[i][j]);
/*  593:     */       }
/*  594:     */     }
/*  595: 904 */     return this;
/*  596:     */   }
/*  597:     */   
/*  598:     */   public Matrix times(Matrix B)
/*  599:     */   {
/*  600: 915 */     if (B.m != this.n) {
/*  601: 916 */       throw new IllegalArgumentException("Matrix inner dimensions must agree.");
/*  602:     */     }
/*  603: 918 */     Matrix X = new Matrix(this.m, B.n);
/*  604: 919 */     double[][] C = X.getArray();
/*  605: 920 */     double[] Bcolj = new double[this.n];
/*  606: 921 */     for (int j = 0; j < B.n; j++)
/*  607:     */     {
/*  608: 922 */       for (int k = 0; k < this.n; k++) {
/*  609: 923 */         Bcolj[k] = B.A[k][j];
/*  610:     */       }
/*  611: 925 */       for (int i = 0; i < this.m; i++)
/*  612:     */       {
/*  613: 926 */         double[] Arowi = this.A[i];
/*  614: 927 */         double s = 0.0D;
/*  615: 928 */         for (int k = 0; k < this.n; k++) {
/*  616: 929 */           s += Arowi[k] * Bcolj[k];
/*  617:     */         }
/*  618: 931 */         C[i][j] = s;
/*  619:     */       }
/*  620:     */     }
/*  621: 934 */     return X;
/*  622:     */   }
/*  623:     */   
/*  624:     */   public LUDecomposition lu()
/*  625:     */   {
/*  626: 944 */     return new LUDecomposition(this);
/*  627:     */   }
/*  628:     */   
/*  629:     */   public QRDecomposition qr()
/*  630:     */   {
/*  631: 954 */     return new QRDecomposition(this);
/*  632:     */   }
/*  633:     */   
/*  634:     */   public CholeskyDecomposition chol()
/*  635:     */   {
/*  636: 964 */     return new CholeskyDecomposition(this);
/*  637:     */   }
/*  638:     */   
/*  639:     */   public SingularValueDecomposition svd()
/*  640:     */   {
/*  641: 974 */     return new SingularValueDecomposition(this);
/*  642:     */   }
/*  643:     */   
/*  644:     */   public EigenvalueDecomposition eig()
/*  645:     */   {
/*  646: 984 */     return new EigenvalueDecomposition(this);
/*  647:     */   }
/*  648:     */   
/*  649:     */   public Matrix solve(Matrix B)
/*  650:     */   {
/*  651: 994 */     return this.m == this.n ? new LUDecomposition(this).solve(B) : new QRDecomposition(this).solve(B);
/*  652:     */   }
/*  653:     */   
/*  654:     */   public Matrix solveTranspose(Matrix B)
/*  655:     */   {
/*  656:1005 */     return transpose().solve(B.transpose());
/*  657:     */   }
/*  658:     */   
/*  659:     */   public Matrix inverse()
/*  660:     */   {
/*  661:1014 */     return solve(identity(this.m, this.m));
/*  662:     */   }
/*  663:     */   
/*  664:     */   public Matrix sqrt()
/*  665:     */   {
/*  666:1088 */     Matrix result = null;
/*  667:     */     
/*  668:     */ 
/*  669:     */ 
/*  670:1092 */     EigenvalueDecomposition evd = eig();
/*  671:1093 */     Matrix v = evd.getV();
/*  672:1094 */     Matrix d = evd.getD();
/*  673:     */     
/*  674:     */ 
/*  675:1097 */     Matrix s = new Matrix(d.getRowDimension(), d.getColumnDimension());
/*  676:1098 */     for (int i = 0; i < s.getRowDimension(); i++) {
/*  677:1099 */       for (int n = 0; n < s.getColumnDimension(); n++) {
/*  678:1100 */         s.set(i, n, StrictMath.sqrt(d.get(i, n)));
/*  679:     */       }
/*  680:     */     }
/*  681:1126 */     Matrix a = v.inverse();
/*  682:1127 */     Matrix b = v.times(s).inverse();
/*  683:1128 */     v = null;
/*  684:1129 */     d = null;
/*  685:1130 */     evd = null;
/*  686:1131 */     s = null;
/*  687:1132 */     result = a.solve(b).inverse();
/*  688:     */     
/*  689:1134 */     return result;
/*  690:     */   }
/*  691:     */   
/*  692:     */   public LinearRegression regression(Matrix y, double ridge)
/*  693:     */   {
/*  694:1147 */     return new LinearRegression(this, y, ridge);
/*  695:     */   }
/*  696:     */   
/*  697:     */   public final LinearRegression regression(Matrix y, double[] w, double ridge)
/*  698:     */   {
/*  699:1162 */     return new LinearRegression(this, y, w, ridge);
/*  700:     */   }
/*  701:     */   
/*  702:     */   public double det()
/*  703:     */   {
/*  704:1171 */     return new LUDecomposition(this).det();
/*  705:     */   }
/*  706:     */   
/*  707:     */   public int rank()
/*  708:     */   {
/*  709:1180 */     return new SingularValueDecomposition(this).rank();
/*  710:     */   }
/*  711:     */   
/*  712:     */   public double cond()
/*  713:     */   {
/*  714:1189 */     return new SingularValueDecomposition(this).cond();
/*  715:     */   }
/*  716:     */   
/*  717:     */   public double trace()
/*  718:     */   {
/*  719:1198 */     double t = 0.0D;
/*  720:1199 */     for (int i = 0; i < Math.min(this.m, this.n); i++) {
/*  721:1200 */       t += this.A[i][i];
/*  722:     */     }
/*  723:1202 */     return t;
/*  724:     */   }
/*  725:     */   
/*  726:     */   public static Matrix random(int m, int n)
/*  727:     */   {
/*  728:1213 */     Matrix A = new Matrix(m, n);
/*  729:1214 */     double[][] X = A.getArray();
/*  730:1215 */     for (int i = 0; i < m; i++) {
/*  731:1216 */       for (int j = 0; j < n; j++) {
/*  732:1217 */         X[i][j] = Math.random();
/*  733:     */       }
/*  734:     */     }
/*  735:1220 */     return A;
/*  736:     */   }
/*  737:     */   
/*  738:     */   public static Matrix identity(int m, int n)
/*  739:     */   {
/*  740:1231 */     Matrix A = new Matrix(m, n);
/*  741:1232 */     double[][] X = A.getArray();
/*  742:1233 */     for (int i = 0; i < m; i++) {
/*  743:1234 */       for (int j = 0; j < n; j++) {
/*  744:1235 */         X[i][j] = (i == j ? 1.0D : 0.0D);
/*  745:     */       }
/*  746:     */     }
/*  747:1238 */     return A;
/*  748:     */   }
/*  749:     */   
/*  750:     */   public void print(int w, int d)
/*  751:     */   {
/*  752:1249 */     print(new PrintWriter(System.out, true), w, d);
/*  753:     */   }
/*  754:     */   
/*  755:     */   public void print(PrintWriter output, int w, int d)
/*  756:     */   {
/*  757:1261 */     DecimalFormat format = new DecimalFormat();
/*  758:1262 */     format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
/*  759:1263 */     format.setMinimumIntegerDigits(1);
/*  760:1264 */     format.setMaximumFractionDigits(d);
/*  761:1265 */     format.setMinimumFractionDigits(d);
/*  762:1266 */     format.setGroupingUsed(false);
/*  763:1267 */     print(output, format, w + 2);
/*  764:     */   }
/*  765:     */   
/*  766:     */   public void print(NumberFormat format, int width)
/*  767:     */   {
/*  768:1281 */     print(new PrintWriter(System.out, true), format, width);
/*  769:     */   }
/*  770:     */   
/*  771:     */   public void print(PrintWriter output, NumberFormat format, int width)
/*  772:     */   {
/*  773:1301 */     output.println();
/*  774:1302 */     for (int i = 0; i < this.m; i++)
/*  775:     */     {
/*  776:1303 */       for (int j = 0; j < this.n; j++)
/*  777:     */       {
/*  778:1304 */         String s = format.format(this.A[i][j]);
/*  779:1305 */         int padding = Math.max(1, width - s.length());
/*  780:1306 */         for (int k = 0; k < padding; k++) {
/*  781:1307 */           output.print(' ');
/*  782:     */         }
/*  783:1309 */         output.print(s);
/*  784:     */       }
/*  785:1311 */       output.println();
/*  786:     */     }
/*  787:1313 */     output.println();
/*  788:     */   }
/*  789:     */   
/*  790:     */   public static Matrix read(BufferedReader input)
/*  791:     */     throws IOException
/*  792:     */   {
/*  793:1331 */     StreamTokenizer tokenizer = new StreamTokenizer(input);
/*  794:     */     
/*  795:     */ 
/*  796:     */ 
/*  797:     */ 
/*  798:     */ 
/*  799:     */ 
/*  800:     */ 
/*  801:1339 */     tokenizer.resetSyntax();
/*  802:1340 */     tokenizer.wordChars(0, 255);
/*  803:1341 */     tokenizer.whitespaceChars(0, 32);
/*  804:1342 */     tokenizer.eolIsSignificant(true);
/*  805:1343 */     Vector<Object> v = new Vector();
/*  806:1346 */     while (tokenizer.nextToken() == 10) {}
/*  807:1349 */     if (tokenizer.ttype == -1) {
/*  808:1350 */       throw new IOException("Unexpected EOF on matrix read.");
/*  809:     */     }
/*  810:     */     do
/*  811:     */     {
/*  812:1353 */       v.addElement(Double.valueOf(tokenizer.sval));
/*  813:1354 */     } while (tokenizer.nextToken() == -3);
/*  814:1356 */     int n = v.size();
/*  815:1357 */     double[] row = new double[n];
/*  816:1358 */     for (int j = 0; j < n; j++) {
/*  817:1359 */       row[j] = ((Double)v.elementAt(j)).doubleValue();
/*  818:     */     }
/*  819:1361 */     v.removeAllElements();
/*  820:1362 */     v.addElement(row);
/*  821:1363 */     while (tokenizer.nextToken() == -3)
/*  822:     */     {
/*  823:1365 */       v.addElement(row = new double[n]);
/*  824:1366 */       int j = 0;
/*  825:     */       do
/*  826:     */       {
/*  827:1368 */         if (j >= n) {
/*  828:1369 */           throw new IOException("Row " + v.size() + " is too long.");
/*  829:     */         }
/*  830:1371 */         row[(j++)] = Double.valueOf(tokenizer.sval).doubleValue();
/*  831:1372 */       } while (tokenizer.nextToken() == -3);
/*  832:1373 */       if (j < n) {
/*  833:1374 */         throw new IOException("Row " + v.size() + " is too short.");
/*  834:     */       }
/*  835:     */     }
/*  836:1377 */     int m = v.size();
/*  837:1378 */     double[][] A = new double[m][];
/*  838:1379 */     v.copyInto(A);
/*  839:1380 */     return new Matrix(A);
/*  840:     */   }
/*  841:     */   
/*  842:     */   private void checkMatrixDimensions(Matrix B)
/*  843:     */   {
/*  844:1387 */     if ((B.m != this.m) || (B.n != this.n)) {
/*  845:1388 */       throw new IllegalArgumentException("Matrix dimensions must agree.");
/*  846:     */     }
/*  847:     */   }
/*  848:     */   
/*  849:     */   public void write(Writer w)
/*  850:     */     throws Exception
/*  851:     */   {
/*  852:1401 */     w.write("% Rows\tColumns\n");
/*  853:1402 */     w.write("" + getRowDimension() + "\t" + getColumnDimension() + "\n");
/*  854:1403 */     w.write("% Matrix elements\n");
/*  855:1404 */     for (int i = 0; i < getRowDimension(); i++)
/*  856:     */     {
/*  857:1405 */       for (int j = 0; j < getColumnDimension(); j++) {
/*  858:1406 */         w.write("" + get(i, j) + "\t");
/*  859:     */       }
/*  860:1408 */       w.write("\n");
/*  861:     */     }
/*  862:1410 */     w.flush();
/*  863:     */   }
/*  864:     */   
/*  865:     */   public String toString()
/*  866:     */   {
/*  867:1423 */     double maxval = 0.0D;
/*  868:1424 */     boolean fractional = false;
/*  869:1425 */     for (int i = 0; i < getRowDimension(); i++) {
/*  870:1426 */       for (int j = 0; j < getColumnDimension(); j++)
/*  871:     */       {
/*  872:1427 */         double current = get(i, j);
/*  873:1428 */         if (current < 0.0D) {
/*  874:1429 */           current *= -11.0D;
/*  875:     */         }
/*  876:1431 */         if (current > maxval) {
/*  877:1432 */           maxval = current;
/*  878:     */         }
/*  879:1434 */         double fract = Math.abs(current - Math.rint(current));
/*  880:1435 */         if ((!fractional) && (Math.log(fract) / Math.log(10.0D) >= -2.0D)) {
/*  881:1436 */           fractional = true;
/*  882:     */         }
/*  883:     */       }
/*  884:     */     }
/*  885:1440 */     int width = (int)(Math.log(maxval) / Math.log(10.0D) + (fractional ? 4 : 1));
/*  886:     */     
/*  887:1442 */     StringBuffer text = new StringBuffer();
/*  888:1443 */     for (int i = 0; i < getRowDimension(); i++)
/*  889:     */     {
/*  890:1444 */       for (int j = 0; j < getColumnDimension(); j++) {
/*  891:1445 */         text.append(" ").append(Utils.doubleToString(get(i, j), width, fractional ? 2 : 0));
/*  892:     */       }
/*  893:1448 */       text.append("\n");
/*  894:     */     }
/*  895:1451 */     return text.toString();
/*  896:     */   }
/*  897:     */   
/*  898:     */   public String toMatlab()
/*  899:     */   {
/*  900:1466 */     StringBuffer result = new StringBuffer();
/*  901:     */     
/*  902:1468 */     result.append("[");
/*  903:1470 */     for (int i = 0; i < getRowDimension(); i++)
/*  904:     */     {
/*  905:1471 */       if (i > 0) {
/*  906:1472 */         result.append("; ");
/*  907:     */       }
/*  908:1475 */       for (int n = 0; n < getColumnDimension(); n++)
/*  909:     */       {
/*  910:1476 */         if (n > 0) {
/*  911:1477 */           result.append(" ");
/*  912:     */         }
/*  913:1479 */         result.append(Double.toString(get(i, n)));
/*  914:     */       }
/*  915:     */     }
/*  916:1483 */     result.append("]");
/*  917:     */     
/*  918:1485 */     return result.toString();
/*  919:     */   }
/*  920:     */   
/*  921:     */   public static Matrix parseMatlab(String matlab)
/*  922:     */     throws Exception
/*  923:     */   {
/*  924:1504 */     String cells = matlab.substring(matlab.indexOf("[") + 1, matlab.indexOf("]")).trim();
/*  925:     */     
/*  926:     */ 
/*  927:     */ 
/*  928:1508 */     StringTokenizer tokRow = new StringTokenizer(cells, ";");
/*  929:1509 */     int rows = tokRow.countTokens();
/*  930:1510 */     StringTokenizer tokCol = new StringTokenizer(tokRow.nextToken(), " ");
/*  931:1511 */     int cols = tokCol.countTokens();
/*  932:     */     
/*  933:     */ 
/*  934:1514 */     Matrix result = new Matrix(rows, cols);
/*  935:1515 */     tokRow = new StringTokenizer(cells, ";");
/*  936:1516 */     rows = 0;
/*  937:1517 */     while (tokRow.hasMoreTokens())
/*  938:     */     {
/*  939:1518 */       tokCol = new StringTokenizer(tokRow.nextToken(), " ");
/*  940:1519 */       cols = 0;
/*  941:1520 */       while (tokCol.hasMoreTokens())
/*  942:     */       {
/*  943:1521 */         result.set(rows, cols, Double.parseDouble(tokCol.nextToken()));
/*  944:1522 */         cols++;
/*  945:     */       }
/*  946:1524 */       rows++;
/*  947:     */     }
/*  948:1527 */     return result;
/*  949:     */   }
/*  950:     */   
/*  951:     */   public String getRevision()
/*  952:     */   {
/*  953:1537 */     return RevisionUtils.extract("$Revision: 10203 $");
/*  954:     */   }
/*  955:     */   
/*  956:     */   public static void main(String[] args)
/*  957:     */   {
/*  958:     */     try
/*  959:     */     {
/*  960:1550 */       System.out.println("\nIdentity\n");
/*  961:1551 */       Matrix I = identity(3, 5);
/*  962:1552 */       System.out.println("I(3,5)\n" + I);
/*  963:     */       
/*  964:     */ 
/*  965:1555 */       System.out.println("\nbasic operations - square\n");
/*  966:1556 */       Matrix A = random(3, 3);
/*  967:1557 */       Matrix B = random(3, 3);
/*  968:1558 */       System.out.println("A\n" + A);
/*  969:1559 */       System.out.println("B\n" + B);
/*  970:1560 */       System.out.println("A'\n" + A.inverse());
/*  971:1561 */       System.out.println("A^T\n" + A.transpose());
/*  972:1562 */       System.out.println("A+B\n" + A.plus(B));
/*  973:1563 */       System.out.println("A*B\n" + A.times(B));
/*  974:1564 */       System.out.println("X from A*X=B\n" + A.solve(B));
/*  975:     */       
/*  976:     */ 
/*  977:1567 */       System.out.println("\nbasic operations - non square\n");
/*  978:1568 */       A = random(2, 3);
/*  979:1569 */       B = random(3, 4);
/*  980:1570 */       System.out.println("A\n" + A);
/*  981:1571 */       System.out.println("B\n" + B);
/*  982:1572 */       System.out.println("A*B\n" + A.times(B));
/*  983:     */       
/*  984:     */ 
/*  985:1575 */       System.out.println("\nsqrt (1)\n");
/*  986:1576 */       A = new Matrix(new double[][] { { 5.0D, -4.0D, 1.0D, 0.0D, 0.0D }, { -4.0D, 6.0D, -4.0D, 1.0D, 0.0D }, { 1.0D, -4.0D, 6.0D, -4.0D, 1.0D }, { 0.0D, 1.0D, -4.0D, 6.0D, -4.0D }, { 0.0D, 0.0D, 1.0D, -4.0D, 5.0D } });
/*  987:     */       
/*  988:1578 */       System.out.println("A\n" + A);
/*  989:1579 */       System.out.println("sqrt(A)\n" + A.sqrt());
/*  990:     */       
/*  991:     */ 
/*  992:1582 */       System.out.println("\nsqrt (2)\n");
/*  993:1583 */       A = new Matrix(new double[][] { { 7.0D, 10.0D }, { 15.0D, 22.0D } });
/*  994:1584 */       System.out.println("A\n" + A);
/*  995:1585 */       System.out.println("sqrt(A)\n" + A.sqrt());
/*  996:1586 */       System.out.println("det(A)\n" + A.det() + "\n");
/*  997:     */       
/*  998:     */ 
/*  999:1589 */       System.out.println("\nEigenvalue Decomposition\n");
/* 1000:1590 */       EigenvalueDecomposition evd = A.eig();
/* 1001:1591 */       System.out.println("[V,D] = eig(A)");
/* 1002:1592 */       System.out.println("- V\n" + evd.getV());
/* 1003:1593 */       System.out.println("- D\n" + evd.getD());
/* 1004:     */       
/* 1005:     */ 
/* 1006:1596 */       System.out.println("\nLU Decomposition\n");
/* 1007:1597 */       LUDecomposition lud = A.lu();
/* 1008:1598 */       System.out.println("[L,U,P] = lu(A)");
/* 1009:1599 */       System.out.println("- L\n" + lud.getL());
/* 1010:1600 */       System.out.println("- U\n" + lud.getU());
/* 1011:1601 */       System.out.println("- P\n" + Utils.arrayToString(lud.getPivot()) + "\n");
/* 1012:     */       
/* 1013:     */ 
/* 1014:1604 */       System.out.println("\nRegression\n");
/* 1015:1605 */       B = new Matrix(new double[][] { { 3.0D }, { 2.0D } });
/* 1016:1606 */       double ridge = 0.5D;
/* 1017:1607 */       double[] weights = { 0.3D, 0.7D };
/* 1018:1608 */       System.out.println("A\n" + A);
/* 1019:1609 */       System.out.println("B\n" + B);
/* 1020:1610 */       System.out.println("ridge = " + ridge + "\n");
/* 1021:1611 */       System.out.println("weights = " + Utils.arrayToString(weights) + "\n");
/* 1022:1612 */       System.out.println("A.regression(B, ridge)\n" + A.regression(B, ridge) + "\n");
/* 1023:     */       
/* 1024:1614 */       System.out.println("A.regression(B, weights, ridge)\n" + A.regression(B, weights, ridge) + "\n");
/* 1025:     */       
/* 1026:     */ 
/* 1027:     */ 
/* 1028:1618 */       System.out.println("\nWriter/Reader\n");
/* 1029:1619 */       StringWriter writer = new StringWriter();
/* 1030:1620 */       A.write(writer);
/* 1031:1621 */       System.out.println("A.write(Writer)\n" + writer);
/* 1032:1622 */       A = new Matrix(new StringReader(writer.toString()));
/* 1033:1623 */       System.out.println("A = new Matrix.read(Reader)\n" + A);
/* 1034:     */       
/* 1035:     */ 
/* 1036:1626 */       System.out.println("\nMatlab-Format\n");
/* 1037:1627 */       String matlab = "[ 1   2;3 4 ]";
/* 1038:1628 */       System.out.println("Matlab: " + matlab);
/* 1039:1629 */       System.out.println("from Matlab:\n" + parseMatlab(matlab));
/* 1040:1630 */       System.out.println("to Matlab:\n" + parseMatlab(matlab).toMatlab());
/* 1041:     */       
/* 1042:1632 */       matlab = "[1 2 3 4;3 4 5 6;7 8 9 10]";
/* 1043:1633 */       System.out.println("Matlab: " + matlab);
/* 1044:1634 */       System.out.println("from Matlab:\n" + parseMatlab(matlab));
/* 1045:1635 */       System.out.println("to Matlab:\n" + parseMatlab(matlab).toMatlab() + "\n");
/* 1046:     */     }
/* 1047:     */     catch (Exception e)
/* 1048:     */     {
/* 1049:1638 */       e.printStackTrace();
/* 1050:     */     }
/* 1051:     */   }
/* 1052:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.matrix.Matrix
 * JD-Core Version:    0.7.0.1
 */