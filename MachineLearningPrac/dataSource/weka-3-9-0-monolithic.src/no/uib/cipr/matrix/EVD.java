/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import com.github.fommil.netlib.LAPACK;
/*   4:    */ import org.netlib.util.intW;
/*   5:    */ 
/*   6:    */ public class EVD
/*   7:    */ {
/*   8:    */   private final double[] work;
/*   9:    */   private final int n;
/*  10:    */   private final JobEig jobLeft;
/*  11:    */   private final JobEig jobRight;
/*  12:    */   private final double[] Wr;
/*  13:    */   private final double[] Wi;
/*  14:    */   private final DenseMatrix Vl;
/*  15:    */   private final DenseMatrix Vr;
/*  16:    */   
/*  17:    */   public EVD(int n)
/*  18:    */   {
/*  19: 64 */     this(n, true, true);
/*  20:    */   }
/*  21:    */   
/*  22:    */   public EVD(int n, boolean left, boolean right)
/*  23:    */   {
/*  24: 78 */     this.n = n;
/*  25: 79 */     this.jobLeft = (left ? JobEig.All : JobEig.Eigenvalues);
/*  26: 80 */     this.jobRight = (right ? JobEig.All : JobEig.Eigenvalues);
/*  27:    */     
/*  28:    */ 
/*  29: 83 */     this.Wr = new double[n];
/*  30: 84 */     this.Wi = new double[n];
/*  31: 86 */     if (left) {
/*  32: 87 */       this.Vl = new DenseMatrix(n, n);
/*  33:    */     } else {
/*  34: 89 */       this.Vl = null;
/*  35:    */     }
/*  36: 91 */     if (right) {
/*  37: 92 */       this.Vr = new DenseMatrix(n, n);
/*  38:    */     } else {
/*  39: 94 */       this.Vr = null;
/*  40:    */     }
/*  41: 97 */     double[] worksize = new double[1];
/*  42: 98 */     intW info = new intW(0);
/*  43: 99 */     LAPACK.getInstance().dgeev(this.jobLeft.netlib(), this.jobRight.netlib(), n, new double[0], 
/*  44:100 */       Matrices.ld(n), new double[0], new double[0], new double[0], 
/*  45:101 */       Matrices.ld(n), new double[0], Matrices.ld(n), worksize, -1, info);
/*  46:    */     
/*  47:    */ 
/*  48:    */ 
/*  49:105 */     int lwork = 0;
/*  50:106 */     if (info.val != 0)
/*  51:    */     {
/*  52:107 */       if ((this.jobLeft == JobEig.All) || (this.jobRight == JobEig.All)) {
/*  53:108 */         lwork = 4 * n;
/*  54:    */       } else {
/*  55:110 */         lwork = 3 * n;
/*  56:    */       }
/*  57:    */     }
/*  58:    */     else {
/*  59:112 */       lwork = (int)worksize[0];
/*  60:    */     }
/*  61:114 */     lwork = Math.max(1, lwork);
/*  62:115 */     this.work = new double[lwork];
/*  63:    */   }
/*  64:    */   
/*  65:    */   public static EVD factorize(Matrix A)
/*  66:    */     throws NotConvergedException
/*  67:    */   {
/*  68:128 */     return new EVD(A.numRows()).factor(new DenseMatrix(A));
/*  69:    */   }
/*  70:    */   
/*  71:    */   public EVD factor(DenseMatrix A)
/*  72:    */     throws NotConvergedException
/*  73:    */   {
/*  74:140 */     if (!A.isSquare()) {
/*  75:141 */       throw new IllegalArgumentException("!A.isSquare()");
/*  76:    */     }
/*  77:142 */     if (A.numRows() != this.n) {
/*  78:143 */       throw new IllegalArgumentException("A.numRows() != n");
/*  79:    */     }
/*  80:145 */     intW info = new intW(0);
/*  81:146 */     LAPACK.getInstance().dgeev(this.jobLeft.netlib(), this.jobRight.netlib(), this.n, A
/*  82:147 */       .getData(), Matrices.ld(this.n), this.Wr, this.Wi, this.jobLeft == JobEig.All ? this.Vl
/*  83:148 */       .getData() : new double[0], 
/*  84:149 */       Matrices.ld(this.n), this.jobRight == JobEig.All ? this.Vr
/*  85:150 */       .getData() : new double[0], 
/*  86:151 */       Matrices.ld(this.n), this.work, this.work.length, info);
/*  87:153 */     if (info.val > 0) {
/*  88:154 */       throw new NotConvergedException(NotConvergedException.Reason.Iterations);
/*  89:    */     }
/*  90:156 */     if (info.val < 0) {
/*  91:157 */       throw new IllegalArgumentException();
/*  92:    */     }
/*  93:159 */     return this;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public DenseMatrix getLeftEigenvectors()
/*  97:    */   {
/*  98:166 */     return this.Vl;
/*  99:    */   }
/* 100:    */   
/* 101:    */   public DenseMatrix getRightEigenvectors()
/* 102:    */   {
/* 103:173 */     return this.Vr;
/* 104:    */   }
/* 105:    */   
/* 106:    */   public double[] getRealEigenvalues()
/* 107:    */   {
/* 108:180 */     return this.Wr;
/* 109:    */   }
/* 110:    */   
/* 111:    */   public double[] getImaginaryEigenvalues()
/* 112:    */   {
/* 113:187 */     return this.Wi;
/* 114:    */   }
/* 115:    */   
/* 116:    */   public boolean hasLeftEigenvectors()
/* 117:    */   {
/* 118:194 */     return this.Vl != null;
/* 119:    */   }
/* 120:    */   
/* 121:    */   public boolean hasRightEigenvectors()
/* 122:    */   {
/* 123:201 */     return this.Vr != null;
/* 124:    */   }
/* 125:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.EVD
 * JD-Core Version:    0.7.0.1
 */