/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import com.github.fommil.netlib.BLAS;
/*   4:    */ import com.github.fommil.netlib.LAPACK;
/*   5:    */ import java.util.Iterator;
/*   6:    */ import org.netlib.util.intW;
/*   7:    */ 
/*   8:    */ abstract class AbstractTriangDenseMatrix
/*   9:    */   extends AbstractDenseMatrix
/*  10:    */ {
/*  11:    */   UpLo uplo;
/*  12:    */   Diag diag;
/*  13:    */   int ld;
/*  14:    */   
/*  15:    */   AbstractTriangDenseMatrix(int n, UpLo uplo, Diag diag)
/*  16:    */   {
/*  17: 57 */     super(n, n);
/*  18: 58 */     this.ld = n;
/*  19: 59 */     this.uplo = uplo;
/*  20: 60 */     this.diag = diag;
/*  21:    */   }
/*  22:    */   
/*  23:    */   AbstractTriangDenseMatrix(Matrix A, UpLo uplo, Diag diag)
/*  24:    */   {
/*  25: 70 */     this(A, Math.min(A.numRows(), A.numColumns()), uplo, diag);
/*  26:    */   }
/*  27:    */   
/*  28:    */   AbstractTriangDenseMatrix(Matrix A, boolean deep, UpLo uplo, Diag diag)
/*  29:    */   {
/*  30: 84 */     this(A, Math.min(A.numRows(), A.numColumns()), deep, uplo, diag);
/*  31:    */   }
/*  32:    */   
/*  33:    */   AbstractTriangDenseMatrix(Matrix A, int k, UpLo uplo, Diag diag)
/*  34:    */   {
/*  35: 97 */     this(A, k, true, uplo, diag);
/*  36:    */   }
/*  37:    */   
/*  38:    */   AbstractTriangDenseMatrix(Matrix A, int k, boolean deep, UpLo uplo, Diag diag)
/*  39:    */   {
/*  40:115 */     super(A, deep);
/*  41:117 */     if ((deep) && (!A.isSquare())) {
/*  42:118 */       throw new IllegalArgumentException("deep && !A.isSquare()");
/*  43:    */     }
/*  44:120 */     this.ld = A.numRows();
/*  45:121 */     this.numRows = (this.numColumns = k);
/*  46:122 */     this.uplo = uplo;
/*  47:123 */     this.diag = diag;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public Vector mult(double alpha, Vector x, Vector y)
/*  51:    */   {
/*  52:128 */     if (!(y instanceof DenseVector)) {
/*  53:129 */       return super.mult(alpha, x, y);
/*  54:    */     }
/*  55:131 */     checkMultAdd(x, y);
/*  56:    */     
/*  57:133 */     double[] yd = ((DenseVector)y).getData();
/*  58:    */     
/*  59:    */ 
/*  60:136 */     y.set(alpha, x);
/*  61:    */     
/*  62:    */ 
/*  63:139 */     BLAS.getInstance().dtrmv(this.uplo.netlib(), Transpose.NoTranspose.netlib(), this.diag
/*  64:140 */       .netlib(), this.numRows, this.data, Math.max(1, this.ld), yd, 1);
/*  65:    */     
/*  66:142 */     return y;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public Vector transMult(double alpha, Vector x, Vector y)
/*  70:    */   {
/*  71:147 */     if (!(y instanceof DenseVector)) {
/*  72:148 */       return super.transMult(alpha, x, y);
/*  73:    */     }
/*  74:150 */     checkTransMultAdd(x, y);
/*  75:    */     
/*  76:152 */     double[] yd = ((DenseVector)y).getData();
/*  77:    */     
/*  78:    */ 
/*  79:155 */     y.set(alpha, x);
/*  80:    */     
/*  81:    */ 
/*  82:158 */     BLAS.getInstance().dtrmv(this.uplo.netlib(), Transpose.Transpose.netlib(), this.diag
/*  83:159 */       .netlib(), this.numRows, this.data, Math.max(1, this.ld), yd, 1);
/*  84:    */     
/*  85:161 */     return y;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public Matrix mult(double alpha, Matrix B, Matrix C)
/*  89:    */   {
/*  90:166 */     if (!(C instanceof DenseMatrix)) {
/*  91:167 */       return super.mult(alpha, B, C);
/*  92:    */     }
/*  93:169 */     checkMultAdd(B, C);
/*  94:    */     
/*  95:171 */     double[] Cd = ((DenseMatrix)C).getData();
/*  96:    */     
/*  97:173 */     C.set(B);
/*  98:    */     
/*  99:    */ 
/* 100:176 */     BLAS.getInstance().dtrmm(Side.Left.netlib(), this.uplo.netlib(), Transpose.NoTranspose
/* 101:177 */       .netlib(), this.diag.netlib(), C.numRows(), C
/* 102:178 */       .numColumns(), alpha, this.data, Math.max(1, this.ld), Cd, 
/* 103:179 */       Math.max(1, C.numRows()));
/* 104:    */     
/* 105:181 */     return C;
/* 106:    */   }
/* 107:    */   
/* 108:    */   public Matrix transAmult(double alpha, Matrix B, Matrix C)
/* 109:    */   {
/* 110:186 */     if (!(C instanceof DenseMatrix)) {
/* 111:187 */       return super.transAmult(alpha, B, C);
/* 112:    */     }
/* 113:189 */     checkTransAmultAdd(B, C);
/* 114:    */     
/* 115:191 */     double[] Cd = ((DenseMatrix)C).getData();
/* 116:    */     
/* 117:193 */     C.set(B);
/* 118:    */     
/* 119:    */ 
/* 120:196 */     BLAS.getInstance().dtrmm(Side.Left.netlib(), this.uplo.netlib(), Transpose.Transpose
/* 121:197 */       .netlib(), this.diag.netlib(), C.numRows(), C
/* 122:198 */       .numColumns(), alpha, this.data, Math.max(1, this.ld), Cd, 
/* 123:199 */       Math.max(1, C.numRows()));
/* 124:    */     
/* 125:201 */     return C;
/* 126:    */   }
/* 127:    */   
/* 128:    */   public Matrix solve(Matrix B, Matrix X)
/* 129:    */   {
/* 130:206 */     return solve(B, X, Transpose.NoTranspose);
/* 131:    */   }
/* 132:    */   
/* 133:    */   public Vector solve(Vector b, Vector x)
/* 134:    */   {
/* 135:211 */     DenseMatrix B = new DenseMatrix(b, false);DenseMatrix X = new DenseMatrix(x, false);
/* 136:212 */     solve(B, X);
/* 137:213 */     return x;
/* 138:    */   }
/* 139:    */   
/* 140:    */   public Matrix transSolve(Matrix B, Matrix X)
/* 141:    */   {
/* 142:218 */     return solve(B, X, Transpose.Transpose);
/* 143:    */   }
/* 144:    */   
/* 145:    */   public Vector transSolve(Vector b, Vector x)
/* 146:    */   {
/* 147:223 */     DenseMatrix B = new DenseMatrix(b, false);DenseMatrix X = new DenseMatrix(x, false);
/* 148:224 */     transSolve(B, X);
/* 149:225 */     return x;
/* 150:    */   }
/* 151:    */   
/* 152:    */   Matrix solve(Matrix B, Matrix X, Transpose trans)
/* 153:    */   {
/* 154:229 */     if (!(X instanceof DenseMatrix)) {
/* 155:230 */       throw new UnsupportedOperationException("X must be a DenseMatrix");
/* 156:    */     }
/* 157:234 */     if (B.numRows() < this.numRows) {
/* 158:235 */       throw new IllegalArgumentException("B.numRows() < A.numRows()");
/* 159:    */     }
/* 160:236 */     if (B.numColumns() != X.numColumns()) {
/* 161:237 */       throw new IllegalArgumentException("B.numColumns() != X.numColumns()");
/* 162:    */     }
/* 163:239 */     if (X.numRows() < this.numRows) {
/* 164:240 */       throw new IllegalArgumentException("X.numRows() < A.numRows()");
/* 165:    */     }
/* 166:242 */     double[] Xd = ((DenseMatrix)X).getData();
/* 167:    */     
/* 168:244 */     X.set(B);
/* 169:    */     
/* 170:246 */     intW info = new intW(0);
/* 171:247 */     LAPACK.getInstance().dtrtrs(this.uplo.netlib(), trans.netlib(), this.diag
/* 172:248 */       .netlib(), this.numRows, X.numColumns(), this.data, Math.max(1, this.ld), Xd, 
/* 173:249 */       Matrices.ld(this.numRows), info);
/* 174:251 */     if (info.val > 0) {
/* 175:252 */       throw new MatrixSingularException();
/* 176:    */     }
/* 177:253 */     if (info.val < 0) {
/* 178:254 */       throw new IllegalArgumentException();
/* 179:    */     }
/* 180:256 */     return X;
/* 181:    */   }
/* 182:    */   
/* 183:    */   int getIndex(int row, int column)
/* 184:    */   {
/* 185:261 */     check(row, column);
/* 186:262 */     return row + column * Math.max(this.ld, this.numRows);
/* 187:    */   }
/* 188:    */   
/* 189:    */   public Iterator<MatrixEntry> iterator()
/* 190:    */   {
/* 191:267 */     return new TriangDenseMatrixIterator(null);
/* 192:    */   }
/* 193:    */   
/* 194:    */   private class TriangDenseMatrixIterator
/* 195:    */     extends AbstractMatrix.RefMatrixIterator
/* 196:    */   {
/* 197:    */     private TriangDenseMatrixIterator()
/* 198:    */     {
/* 199:270 */       super();
/* 200:    */     }
/* 201:    */     
/* 202:    */     public MatrixEntry next()
/* 203:    */     {
/* 204:274 */       this.entry.update(this.row, this.column);
/* 205:276 */       if (AbstractTriangDenseMatrix.this.uplo == UpLo.Lower)
/* 206:    */       {
/* 207:277 */         if (this.row < AbstractTriangDenseMatrix.this.numRows - 1)
/* 208:    */         {
/* 209:278 */           this.row += 1;
/* 210:    */         }
/* 211:    */         else
/* 212:    */         {
/* 213:280 */           this.column += 1;
/* 214:281 */           this.row = this.column;
/* 215:    */         }
/* 216:    */       }
/* 217:284 */       else if (this.row < this.column)
/* 218:    */       {
/* 219:285 */         this.row += 1;
/* 220:    */       }
/* 221:    */       else
/* 222:    */       {
/* 223:287 */         this.column += 1;
/* 224:288 */         this.row = 0;
/* 225:    */       }
/* 226:292 */       return this.entry;
/* 227:    */     }
/* 228:    */   }
/* 229:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.AbstractTriangDenseMatrix
 * JD-Core Version:    0.7.0.1
 */