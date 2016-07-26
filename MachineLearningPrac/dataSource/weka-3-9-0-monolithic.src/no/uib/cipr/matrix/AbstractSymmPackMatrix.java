/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import com.github.fommil.netlib.BLAS;
/*   4:    */ import com.github.fommil.netlib.LAPACK;
/*   5:    */ import org.netlib.util.intW;
/*   6:    */ 
/*   7:    */ abstract class AbstractSymmPackMatrix
/*   8:    */   extends AbstractPackMatrix
/*   9:    */ {
/*  10:    */   private UpLo uplo;
/*  11:    */   
/*  12:    */   AbstractSymmPackMatrix(int n, UpLo uplo)
/*  13:    */   {
/*  14: 41 */     super(n);
/*  15: 42 */     this.uplo = uplo;
/*  16:    */   }
/*  17:    */   
/*  18:    */   AbstractSymmPackMatrix(Matrix A, UpLo uplo)
/*  19:    */   {
/*  20: 49 */     this(A, true, uplo);
/*  21:    */   }
/*  22:    */   
/*  23:    */   AbstractSymmPackMatrix(Matrix A, boolean deep, UpLo uplo)
/*  24:    */   {
/*  25: 56 */     super(A, deep);
/*  26: 57 */     this.uplo = uplo;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public Vector multAdd(double alpha, Vector x, Vector y)
/*  30:    */   {
/*  31: 62 */     if ((!(x instanceof DenseVector)) || (!(y instanceof DenseVector))) {
/*  32: 63 */       return super.multAdd(alpha, x, y);
/*  33:    */     }
/*  34: 65 */     checkMultAdd(x, y);
/*  35:    */     
/*  36: 67 */     double[] xd = ((DenseVector)x).getData();
/*  37: 68 */     double[] yd = ((DenseVector)y).getData();
/*  38:    */     
/*  39: 70 */     BLAS.getInstance().dspmv(this.uplo.netlib(), this.numRows, alpha, this.data, xd, 1, 1.0D, yd, 1);
/*  40:    */     
/*  41:    */ 
/*  42: 73 */     return y;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public Vector transMultAdd(double alpha, Vector x, Vector y)
/*  46:    */   {
/*  47: 78 */     return multAdd(alpha, x, y);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public Matrix rank1(double alpha, Vector x, Vector y)
/*  51:    */   {
/*  52: 83 */     if (x != y) {
/*  53: 84 */       throw new IllegalArgumentException("x != y");
/*  54:    */     }
/*  55: 85 */     if (!(x instanceof DenseVector)) {
/*  56: 86 */       return super.rank1(alpha, x, y);
/*  57:    */     }
/*  58: 88 */     checkRank1(x, y);
/*  59:    */     
/*  60: 90 */     double[] xd = ((DenseVector)x).getData();
/*  61:    */     
/*  62: 92 */     BLAS.getInstance().dspr(this.uplo.netlib(), this.numRows, alpha, xd, 1, this.data);
/*  63:    */     
/*  64: 94 */     return this;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public Matrix rank2(double alpha, Vector x, Vector y)
/*  68:    */   {
/*  69: 99 */     if ((!(x instanceof DenseVector)) || (!(y instanceof DenseVector))) {
/*  70:100 */       return super.rank2(alpha, x, y);
/*  71:    */     }
/*  72:102 */     checkRank2(x, y);
/*  73:    */     
/*  74:104 */     double[] xd = ((DenseVector)x).getData();
/*  75:105 */     double[] yd = ((DenseVector)y).getData();
/*  76:    */     
/*  77:107 */     BLAS.getInstance().dspr2(this.uplo.netlib(), this.numRows, alpha, xd, 1, yd, 1, this.data);
/*  78:    */     
/*  79:    */ 
/*  80:110 */     return this;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public Matrix solve(Matrix B, Matrix X)
/*  84:    */   {
/*  85:115 */     if (!(X instanceof DenseMatrix)) {
/*  86:116 */       throw new UnsupportedOperationException("X must be a DenseMatrix");
/*  87:    */     }
/*  88:118 */     checkSolve(B, X);
/*  89:    */     
/*  90:120 */     double[] Xd = ((DenseMatrix)X).getData();
/*  91:    */     
/*  92:122 */     X.set(B);
/*  93:    */     
/*  94:124 */     int[] ipiv = new int[this.numRows];
/*  95:    */     
/*  96:126 */     intW info = new intW(0);
/*  97:127 */     LAPACK.getInstance().dspsv(this.uplo.netlib(), this.numRows, X.numColumns(), 
/*  98:128 */       (double[])this.data.clone(), ipiv, Xd, Matrices.ld(this.numRows), info);
/*  99:130 */     if (info.val > 0) {
/* 100:131 */       throw new MatrixSingularException();
/* 101:    */     }
/* 102:132 */     if (info.val < 0) {
/* 103:133 */       throw new IllegalArgumentException();
/* 104:    */     }
/* 105:135 */     return X;
/* 106:    */   }
/* 107:    */   
/* 108:    */   public Vector solve(Vector b, Vector x)
/* 109:    */   {
/* 110:140 */     DenseMatrix B = new DenseMatrix(b, false);DenseMatrix X = new DenseMatrix(x, false);
/* 111:141 */     solve(B, X);
/* 112:142 */     return x;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public Matrix transSolve(Matrix B, Matrix X)
/* 116:    */   {
/* 117:147 */     return solve(B, X);
/* 118:    */   }
/* 119:    */   
/* 120:    */   public Vector transSolve(Vector b, Vector x)
/* 121:    */   {
/* 122:152 */     return solve(b, x);
/* 123:    */   }
/* 124:    */   
/* 125:    */   Matrix SPDsolve(Matrix B, Matrix X)
/* 126:    */   {
/* 127:156 */     if (!(X instanceof DenseMatrix)) {
/* 128:157 */       throw new UnsupportedOperationException("X must be a DenseMatrix");
/* 129:    */     }
/* 130:159 */     checkSolve(B, X);
/* 131:    */     
/* 132:161 */     double[] Xd = ((DenseMatrix)X).getData();
/* 133:    */     
/* 134:163 */     X.set(B);
/* 135:    */     
/* 136:165 */     intW info = new intW(0);
/* 137:166 */     LAPACK.getInstance().dppsv(this.uplo.netlib(), this.numRows, X.numColumns(), 
/* 138:167 */       (double[])this.data.clone(), Xd, Matrices.ld(this.numRows), info);
/* 139:169 */     if (info.val > 0) {
/* 140:170 */       throw new MatrixNotSPDException();
/* 141:    */     }
/* 142:171 */     if (info.val < 0) {
/* 143:172 */       throw new IllegalArgumentException();
/* 144:    */     }
/* 145:174 */     return X;
/* 146:    */   }
/* 147:    */   
/* 148:    */   public Matrix transpose()
/* 149:    */   {
/* 150:179 */     return this;
/* 151:    */   }
/* 152:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.AbstractSymmPackMatrix
 * JD-Core Version:    0.7.0.1
 */