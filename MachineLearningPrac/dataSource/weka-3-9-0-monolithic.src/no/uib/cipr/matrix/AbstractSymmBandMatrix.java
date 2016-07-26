/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import com.github.fommil.netlib.BLAS;
/*   4:    */ import com.github.fommil.netlib.LAPACK;
/*   5:    */ import java.util.Iterator;
/*   6:    */ import org.netlib.util.intW;
/*   7:    */ 
/*   8:    */ abstract class AbstractSymmBandMatrix
/*   9:    */   extends AbstractBandMatrix
/*  10:    */ {
/*  11:    */   private UpLo uplo;
/*  12:    */   int kd;
/*  13:    */   
/*  14:    */   AbstractSymmBandMatrix(int n, int kl, int ku, UpLo uplo)
/*  15:    */   {
/*  16: 48 */     super(n, kl, ku);
/*  17: 49 */     this.kd = Math.max(kl, ku);
/*  18: 50 */     this.uplo = uplo;
/*  19:    */   }
/*  20:    */   
/*  21:    */   AbstractSymmBandMatrix(Matrix A, int kl, int ku, UpLo uplo)
/*  22:    */   {
/*  23: 57 */     this(A, kl, ku, true, uplo);
/*  24:    */   }
/*  25:    */   
/*  26:    */   AbstractSymmBandMatrix(Matrix A, int kl, int ku, boolean deep, UpLo uplo)
/*  27:    */   {
/*  28: 64 */     super(A, kl, ku, deep);
/*  29: 65 */     this.kd = Math.max(kl, ku);
/*  30: 66 */     this.uplo = uplo;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public Vector multAdd(double alpha, Vector x, Vector y)
/*  34:    */   {
/*  35: 71 */     if ((!(x instanceof DenseVector)) || (!(y instanceof DenseVector))) {
/*  36: 72 */       return super.multAdd(alpha, x, y);
/*  37:    */     }
/*  38: 74 */     checkMultAdd(x, y);
/*  39:    */     
/*  40: 76 */     double[] xd = ((DenseVector)x).getData();
/*  41: 77 */     double[] yd = ((DenseVector)y).getData();
/*  42:    */     
/*  43: 79 */     BLAS.getInstance().dsbmv(this.uplo.netlib(), this.numRows, this.kd, alpha, this.data, this.kd + 1, xd, 1, 1.0D, yd, 1);
/*  44:    */     
/*  45:    */ 
/*  46: 82 */     return y;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public Vector transMultAdd(double alpha, Vector x, Vector y)
/*  50:    */   {
/*  51: 87 */     return multAdd(alpha, x, y);
/*  52:    */   }
/*  53:    */   
/*  54:    */   public Iterator<MatrixEntry> iterator()
/*  55:    */   {
/*  56: 92 */     return new AbstractBandMatrix.BandMatrixIterator(this, this.kd, this.kd);
/*  57:    */   }
/*  58:    */   
/*  59:    */   public Matrix solve(Matrix B, Matrix X)
/*  60:    */   {
/*  61: 97 */     if (!(X instanceof DenseMatrix)) {
/*  62: 98 */       throw new UnsupportedOperationException("X must be a DenseMatrix");
/*  63:    */     }
/*  64:100 */     checkSolve(B, X);
/*  65:    */     
/*  66:102 */     double[] Xd = ((DenseMatrix)X).getData();
/*  67:    */     
/*  68:104 */     X.set(B);
/*  69:    */     
/*  70:    */ 
/*  71:    */ 
/*  72:108 */     BandMatrix Af = new BandMatrix(this, this.kd, this.kd + this.kd);
/*  73:109 */     int[] ipiv = new int[this.numRows];
/*  74:    */     
/*  75:111 */     intW info = new intW(0);
/*  76:112 */     LAPACK.getInstance().dgbsv(this.numRows, this.kd, this.kd, X.numColumns(), Af
/*  77:113 */       .getData(), Matrices.ld(2 * this.kd + this.kd + 1), ipiv, Xd, 
/*  78:114 */       Matrices.ld(this.numRows), info);
/*  79:116 */     if (info.val > 0) {
/*  80:117 */       throw new MatrixSingularException();
/*  81:    */     }
/*  82:118 */     if (info.val < 0) {
/*  83:119 */       throw new IllegalArgumentException();
/*  84:    */     }
/*  85:121 */     return X;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public Vector solve(Vector b, Vector x)
/*  89:    */   {
/*  90:126 */     DenseMatrix B = new DenseMatrix(b, false);DenseMatrix X = new DenseMatrix(x, false);
/*  91:127 */     solve(B, X);
/*  92:128 */     return x;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public Matrix transSolve(Matrix B, Matrix X)
/*  96:    */   {
/*  97:133 */     return solve(B, X);
/*  98:    */   }
/*  99:    */   
/* 100:    */   public Vector transSolve(Vector b, Vector x)
/* 101:    */   {
/* 102:138 */     return solve(b, x);
/* 103:    */   }
/* 104:    */   
/* 105:    */   Matrix SPDsolve(Matrix B, Matrix X)
/* 106:    */   {
/* 107:142 */     if (!(X instanceof DenseMatrix)) {
/* 108:143 */       throw new UnsupportedOperationException("X must be a DenseMatrix");
/* 109:    */     }
/* 110:145 */     checkSolve(B, X);
/* 111:    */     
/* 112:147 */     double[] Xd = ((DenseMatrix)X).getData();
/* 113:    */     
/* 114:149 */     X.set(B);
/* 115:    */     
/* 116:151 */     intW info = new intW(0);
/* 117:152 */     LAPACK.getInstance().dpbsv(this.uplo.netlib(), this.numRows, this.kd, X.numColumns(), 
/* 118:153 */       (double[])this.data.clone(), Matrices.ld(this.kd + 1), Xd, Matrices.ld(this.numRows), info);
/* 119:156 */     if (info.val > 0) {
/* 120:157 */       throw new MatrixNotSPDException();
/* 121:    */     }
/* 122:158 */     if (info.val < 0) {
/* 123:159 */       throw new IllegalArgumentException();
/* 124:    */     }
/* 125:161 */     return X;
/* 126:    */   }
/* 127:    */   
/* 128:    */   public Matrix transpose()
/* 129:    */   {
/* 130:166 */     return this;
/* 131:    */   }
/* 132:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.AbstractSymmBandMatrix
 * JD-Core Version:    0.7.0.1
 */