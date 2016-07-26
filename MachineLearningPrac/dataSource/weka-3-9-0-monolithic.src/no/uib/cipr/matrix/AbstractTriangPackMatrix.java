/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import com.github.fommil.netlib.BLAS;
/*   4:    */ import com.github.fommil.netlib.LAPACK;
/*   5:    */ import java.util.Iterator;
/*   6:    */ import org.netlib.util.intW;
/*   7:    */ 
/*   8:    */ abstract class AbstractTriangPackMatrix
/*   9:    */   extends AbstractPackMatrix
/*  10:    */ {
/*  11:    */   UpLo uplo;
/*  12:    */   Diag diag;
/*  13:    */   
/*  14:    */   AbstractTriangPackMatrix(int n, UpLo uplo, Diag diag)
/*  15:    */   {
/*  16: 48 */     super(n);
/*  17: 49 */     this.uplo = uplo;
/*  18: 50 */     this.diag = diag;
/*  19:    */   }
/*  20:    */   
/*  21:    */   AbstractTriangPackMatrix(Matrix A, UpLo uplo, Diag diag)
/*  22:    */   {
/*  23: 57 */     this(A, false, uplo, diag);
/*  24:    */   }
/*  25:    */   
/*  26:    */   AbstractTriangPackMatrix(Matrix A, boolean deep, UpLo uplo, Diag diag)
/*  27:    */   {
/*  28: 64 */     super(A, deep);
/*  29: 65 */     this.uplo = uplo;
/*  30: 66 */     this.diag = diag;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public Vector mult(double alpha, Vector x, Vector y)
/*  34:    */   {
/*  35: 71 */     if (!(y instanceof DenseVector)) {
/*  36: 72 */       return super.mult(alpha, x, y);
/*  37:    */     }
/*  38: 74 */     checkMultAdd(x, y);
/*  39:    */     
/*  40: 76 */     double[] yd = ((DenseVector)y).getData();
/*  41:    */     
/*  42:    */ 
/*  43: 79 */     y.set(alpha, x);
/*  44:    */     
/*  45:    */ 
/*  46: 82 */     BLAS.getInstance().dtpmv(this.uplo.netlib(), Transpose.NoTranspose.netlib(), this.diag
/*  47: 83 */       .netlib(), this.numRows, this.data, yd, 1);
/*  48:    */     
/*  49: 85 */     return y;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public Vector transMult(double alpha, Vector x, Vector y)
/*  53:    */   {
/*  54: 90 */     if (!(y instanceof DenseVector)) {
/*  55: 91 */       return super.transMult(alpha, x, y);
/*  56:    */     }
/*  57: 93 */     checkTransMultAdd(x, y);
/*  58:    */     
/*  59: 95 */     double[] yd = ((DenseVector)y).getData();
/*  60:    */     
/*  61:    */ 
/*  62: 98 */     y.set(alpha, x);
/*  63:    */     
/*  64:    */ 
/*  65:101 */     BLAS.getInstance().dtpmv(this.uplo.netlib(), Transpose.Transpose.netlib(), this.diag
/*  66:102 */       .netlib(), this.numRows, this.data, yd, 1);
/*  67:    */     
/*  68:104 */     return y;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public Matrix solve(Matrix B, Matrix X)
/*  72:    */   {
/*  73:109 */     return solve(B, X, Transpose.NoTranspose);
/*  74:    */   }
/*  75:    */   
/*  76:    */   public Vector solve(Vector b, Vector x)
/*  77:    */   {
/*  78:114 */     DenseMatrix B = new DenseMatrix(b, false);DenseMatrix X = new DenseMatrix(x, false);
/*  79:115 */     solve(B, X);
/*  80:116 */     return x;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public Matrix transSolve(Matrix B, Matrix X)
/*  84:    */   {
/*  85:121 */     return solve(B, X, Transpose.Transpose);
/*  86:    */   }
/*  87:    */   
/*  88:    */   public Vector transSolve(Vector b, Vector x)
/*  89:    */   {
/*  90:126 */     DenseMatrix B = new DenseMatrix(b, false);DenseMatrix X = new DenseMatrix(x, false);
/*  91:127 */     transSolve(B, X);
/*  92:128 */     return x;
/*  93:    */   }
/*  94:    */   
/*  95:    */   Matrix solve(Matrix B, Matrix X, Transpose trans)
/*  96:    */   {
/*  97:132 */     if (!(X instanceof DenseMatrix)) {
/*  98:133 */       throw new UnsupportedOperationException("X must be a DenseMatrix");
/*  99:    */     }
/* 100:135 */     checkSolve(B, X);
/* 101:    */     
/* 102:137 */     double[] Xd = ((DenseMatrix)X).getData();
/* 103:    */     
/* 104:139 */     X.set(B);
/* 105:    */     
/* 106:141 */     intW info = new intW(0);
/* 107:142 */     LAPACK.getInstance().dtptrs(this.uplo.netlib(), trans.netlib(), this.diag
/* 108:143 */       .netlib(), this.numRows, X.numColumns(), this.data, Xd, 
/* 109:144 */       Matrices.ld(this.numRows), info);
/* 110:146 */     if (info.val > 0) {
/* 111:147 */       throw new MatrixSingularException();
/* 112:    */     }
/* 113:148 */     if (info.val < 0) {
/* 114:149 */       throw new IllegalArgumentException();
/* 115:    */     }
/* 116:151 */     return X;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public Iterator<MatrixEntry> iterator()
/* 120:    */   {
/* 121:156 */     return new TriangPackMatrixIterator(null);
/* 122:    */   }
/* 123:    */   
/* 124:    */   private class TriangPackMatrixIterator
/* 125:    */     extends AbstractMatrix.RefMatrixIterator
/* 126:    */   {
/* 127:    */     private TriangPackMatrixIterator()
/* 128:    */     {
/* 129:159 */       super();
/* 130:    */     }
/* 131:    */     
/* 132:    */     public MatrixEntry next()
/* 133:    */     {
/* 134:163 */       this.entry.update(this.row, this.column);
/* 135:165 */       if (AbstractTriangPackMatrix.this.uplo == UpLo.Lower)
/* 136:    */       {
/* 137:166 */         if (this.row < AbstractTriangPackMatrix.this.numRows - 1)
/* 138:    */         {
/* 139:167 */           this.row += 1;
/* 140:    */         }
/* 141:    */         else
/* 142:    */         {
/* 143:169 */           this.column += 1;
/* 144:170 */           this.row = this.column;
/* 145:    */         }
/* 146:    */       }
/* 147:173 */       else if (this.row < this.column)
/* 148:    */       {
/* 149:174 */         this.row += 1;
/* 150:    */       }
/* 151:    */       else
/* 152:    */       {
/* 153:176 */         this.column += 1;
/* 154:177 */         this.row = 0;
/* 155:    */       }
/* 156:181 */       return this.entry;
/* 157:    */     }
/* 158:    */   }
/* 159:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.AbstractTriangPackMatrix
 * JD-Core Version:    0.7.0.1
 */