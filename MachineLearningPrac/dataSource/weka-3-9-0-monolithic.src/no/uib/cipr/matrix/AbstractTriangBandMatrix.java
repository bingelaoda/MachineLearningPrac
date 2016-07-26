/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import com.github.fommil.netlib.BLAS;
/*   4:    */ import com.github.fommil.netlib.LAPACK;
/*   5:    */ import org.netlib.util.intW;
/*   6:    */ 
/*   7:    */ abstract class AbstractTriangBandMatrix
/*   8:    */   extends AbstractBandMatrix
/*   9:    */ {
/*  10:    */   private UpLo uplo;
/*  11:    */   private Diag diag;
/*  12:    */   int kd;
/*  13:    */   
/*  14:    */   AbstractTriangBandMatrix(int n, int kl, int ku, UpLo uplo, Diag diag)
/*  15:    */   {
/*  16: 51 */     super(n, kl, ku);
/*  17: 52 */     this.kd = Math.max(kl, ku);
/*  18: 53 */     this.uplo = uplo;
/*  19: 54 */     this.diag = diag;
/*  20:    */   }
/*  21:    */   
/*  22:    */   AbstractTriangBandMatrix(Matrix A, int kl, int ku, UpLo uplo, Diag diag)
/*  23:    */   {
/*  24: 61 */     this(A, kl, ku, true, uplo, diag);
/*  25:    */   }
/*  26:    */   
/*  27:    */   AbstractTriangBandMatrix(Matrix A, int kl, int ku, boolean deep, UpLo uplo, Diag diag)
/*  28:    */   {
/*  29: 69 */     super(A, kl, ku, deep);
/*  30: 70 */     this.kd = Math.max(kl, ku);
/*  31: 71 */     this.uplo = uplo;
/*  32: 72 */     this.diag = diag;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public Vector mult(double alpha, Vector x, Vector y)
/*  36:    */   {
/*  37: 77 */     if (!(y instanceof DenseVector)) {
/*  38: 78 */       return super.mult(alpha, x, y);
/*  39:    */     }
/*  40: 80 */     checkMultAdd(x, y);
/*  41:    */     
/*  42: 82 */     double[] yd = ((DenseVector)y).getData();
/*  43:    */     
/*  44:    */ 
/*  45: 85 */     y.set(alpha, x);
/*  46:    */     
/*  47:    */ 
/*  48: 88 */     BLAS.getInstance().dtbmv(this.uplo.netlib(), Transpose.NoTranspose.netlib(), this.diag
/*  49: 89 */       .netlib(), this.numRows, this.kd, this.data, this.kd + 1, yd, 1);
/*  50:    */     
/*  51: 91 */     return y;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public Vector transMult(double alpha, Vector x, Vector y)
/*  55:    */   {
/*  56: 96 */     if (!(y instanceof DenseVector)) {
/*  57: 97 */       return super.transMult(alpha, x, y);
/*  58:    */     }
/*  59: 99 */     checkTransMultAdd(x, y);
/*  60:    */     
/*  61:101 */     double[] yd = ((DenseVector)y).getData();
/*  62:    */     
/*  63:    */ 
/*  64:104 */     y.set(alpha, x);
/*  65:    */     
/*  66:    */ 
/*  67:107 */     BLAS.getInstance().dtbmv(this.uplo.netlib(), Transpose.Transpose.netlib(), this.diag
/*  68:108 */       .netlib(), this.numRows, this.kd, this.data, this.kd + 1, yd, 1);
/*  69:    */     
/*  70:110 */     return y;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public Matrix solve(Matrix B, Matrix X)
/*  74:    */   {
/*  75:115 */     return solve(B, X, Transpose.NoTranspose);
/*  76:    */   }
/*  77:    */   
/*  78:    */   public Vector solve(Vector b, Vector x)
/*  79:    */   {
/*  80:120 */     DenseMatrix B = new DenseMatrix(b, false);DenseMatrix X = new DenseMatrix(x, false);
/*  81:121 */     solve(B, X);
/*  82:122 */     return x;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public Matrix transSolve(Matrix B, Matrix X)
/*  86:    */   {
/*  87:127 */     return solve(B, X, Transpose.Transpose);
/*  88:    */   }
/*  89:    */   
/*  90:    */   public Vector transSolve(Vector b, Vector x)
/*  91:    */   {
/*  92:132 */     DenseMatrix B = new DenseMatrix(b, false);DenseMatrix X = new DenseMatrix(x, false);
/*  93:133 */     transSolve(B, X);
/*  94:134 */     return x;
/*  95:    */   }
/*  96:    */   
/*  97:    */   Matrix solve(Matrix B, Matrix X, Transpose trans)
/*  98:    */   {
/*  99:138 */     if (!(X instanceof DenseMatrix)) {
/* 100:139 */       throw new UnsupportedOperationException("X must be a DenseMatrix");
/* 101:    */     }
/* 102:141 */     checkSolve(B, X);
/* 103:    */     
/* 104:143 */     double[] Xd = ((DenseMatrix)X).getData();
/* 105:    */     
/* 106:145 */     X.set(B);
/* 107:    */     
/* 108:147 */     intW info = new intW(0);
/* 109:148 */     LAPACK.getInstance().dtbtrs(this.uplo.netlib(), trans.netlib(), this.diag
/* 110:149 */       .netlib(), this.numRows, this.kd, X.numColumns(), this.data, 
/* 111:150 */       Matrices.ld(this.kd + 1), Xd, Matrices.ld(this.n), info);
/* 112:152 */     if (info.val > 0) {
/* 113:153 */       throw new MatrixSingularException();
/* 114:    */     }
/* 115:154 */     if (info.val < 0) {
/* 116:155 */       throw new IllegalArgumentException();
/* 117:    */     }
/* 118:157 */     return X;
/* 119:    */   }
/* 120:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.AbstractTriangBandMatrix
 * JD-Core Version:    0.7.0.1
 */