/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import com.github.fommil.netlib.BLAS;
/*   4:    */ import com.github.fommil.netlib.LAPACK;
/*   5:    */ import org.netlib.util.intW;
/*   6:    */ 
/*   7:    */ abstract class AbstractSymmDenseMatrix
/*   8:    */   extends AbstractDenseMatrix
/*   9:    */ {
/*  10:    */   private UpLo uplo;
/*  11:    */   
/*  12:    */   AbstractSymmDenseMatrix(int n, UpLo uplo)
/*  13:    */   {
/*  14: 41 */     super(n, n);
/*  15: 42 */     this.uplo = uplo;
/*  16:    */   }
/*  17:    */   
/*  18:    */   AbstractSymmDenseMatrix(Matrix A, UpLo uplo)
/*  19:    */   {
/*  20: 49 */     this(A, true, uplo);
/*  21:    */   }
/*  22:    */   
/*  23:    */   AbstractSymmDenseMatrix(Matrix A, boolean deep, UpLo uplo)
/*  24:    */   {
/*  25: 56 */     super(A, deep);
/*  26: 57 */     if (!isSquare()) {
/*  27: 58 */       throw new IllegalArgumentException("Symmetric matrix must be square");
/*  28:    */     }
/*  29: 60 */     this.uplo = uplo;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public Matrix multAdd(double alpha, Matrix B, Matrix C)
/*  33:    */   {
/*  34: 65 */     if ((!(B instanceof DenseMatrix)) || (!(C instanceof DenseMatrix))) {
/*  35: 66 */       return super.multAdd(alpha, B, C);
/*  36:    */     }
/*  37: 68 */     checkMultAdd(B, C);
/*  38:    */     
/*  39: 70 */     double[] Bd = ((DenseMatrix)B).getData();
/*  40: 71 */     double[] Cd = ((DenseMatrix)C).getData();
/*  41:    */     
/*  42: 73 */     BLAS.getInstance().dsymm(Side.Left.netlib(), this.uplo.netlib(), C
/*  43: 74 */       .numRows(), C.numColumns(), alpha, this.data, 
/*  44: 75 */       Math.max(1, C.numRows()), Bd, Math.max(1, C.numRows()), 1.0D, Cd, 
/*  45: 76 */       Math.max(1, C.numRows()));
/*  46:    */     
/*  47: 78 */     return C;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public Matrix transAmultAdd(double alpha, Matrix B, Matrix C)
/*  51:    */   {
/*  52: 83 */     return multAdd(alpha, B, C);
/*  53:    */   }
/*  54:    */   
/*  55:    */   public Matrix rank1(double alpha, Vector x, Vector y)
/*  56:    */   {
/*  57: 88 */     if (x != y) {
/*  58: 89 */       throw new IllegalArgumentException("x != y");
/*  59:    */     }
/*  60: 90 */     if (!(x instanceof DenseVector)) {
/*  61: 91 */       return super.rank1(alpha, x, y);
/*  62:    */     }
/*  63: 93 */     checkRank1(x, y);
/*  64:    */     
/*  65: 95 */     double[] xd = ((DenseVector)x).getData();
/*  66:    */     
/*  67: 97 */     BLAS.getInstance().dsyr(this.uplo.netlib(), this.numRows, alpha, xd, 1, this.data, 
/*  68: 98 */       Math.max(1, this.numRows));
/*  69:    */     
/*  70:100 */     return this;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public Matrix rank2(double alpha, Vector x, Vector y)
/*  74:    */   {
/*  75:105 */     if ((!(x instanceof DenseVector)) || (!(y instanceof DenseVector))) {
/*  76:106 */       return super.rank2(alpha, x, y);
/*  77:    */     }
/*  78:108 */     checkRank2(x, y);
/*  79:    */     
/*  80:110 */     double[] xd = ((DenseVector)x).getData();
/*  81:111 */     double[] yd = ((DenseVector)y).getData();
/*  82:    */     
/*  83:113 */     BLAS.getInstance().dsyr2(this.uplo.netlib(), this.numRows, alpha, xd, 1, yd, 1, this.data, 
/*  84:114 */       Math.max(1, this.numRows));
/*  85:    */     
/*  86:116 */     return this;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public Vector multAdd(double alpha, Vector x, Vector y)
/*  90:    */   {
/*  91:121 */     if ((!(x instanceof DenseVector)) || (!(y instanceof DenseVector))) {
/*  92:122 */       return super.multAdd(alpha, x, y);
/*  93:    */     }
/*  94:124 */     checkMultAdd(x, y);
/*  95:    */     
/*  96:126 */     double[] xd = ((DenseVector)x).getData();
/*  97:127 */     double[] yd = ((DenseVector)y).getData();
/*  98:    */     
/*  99:129 */     BLAS.getInstance().dsymv(this.uplo.netlib(), this.numRows, alpha, this.data, 
/* 100:130 */       Math.max(1, this.numRows), xd, 1, 1.0D, yd, 1);
/* 101:    */     
/* 102:132 */     return y;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public Vector transMultAdd(double alpha, Vector x, Vector y)
/* 106:    */   {
/* 107:137 */     return multAdd(alpha, x, y);
/* 108:    */   }
/* 109:    */   
/* 110:    */   public Matrix rank1(double alpha, Matrix C)
/* 111:    */   {
/* 112:142 */     if (!(C instanceof DenseMatrix)) {
/* 113:143 */       return super.rank1(alpha, C);
/* 114:    */     }
/* 115:145 */     checkRank1(C);
/* 116:    */     
/* 117:147 */     double[] Cd = ((DenseMatrix)C).getData();
/* 118:    */     
/* 119:149 */     BLAS.getInstance().dsyrk(this.uplo.netlib(), Transpose.NoTranspose.netlib(), this.numRows, C
/* 120:150 */       .numColumns(), alpha, Cd, Math.max(1, this.numRows), 1.0D, this.data, 
/* 121:151 */       Math.max(1, this.numRows));
/* 122:    */     
/* 123:153 */     return this;
/* 124:    */   }
/* 125:    */   
/* 126:    */   public Matrix transRank1(double alpha, Matrix C)
/* 127:    */   {
/* 128:158 */     if (!(C instanceof DenseMatrix)) {
/* 129:159 */       return super.transRank1(alpha, C);
/* 130:    */     }
/* 131:161 */     checkTransRank1(C);
/* 132:    */     
/* 133:163 */     double[] Cd = ((DenseMatrix)C).getData();
/* 134:    */     
/* 135:165 */     BLAS.getInstance().dsyrk(this.uplo.netlib(), Transpose.Transpose.netlib(), this.numRows, this.numRows, alpha, Cd, 
/* 136:166 */       Math.max(1, this.numRows), 1.0D, this.data, 
/* 137:167 */       Math.max(1, this.numRows));
/* 138:    */     
/* 139:169 */     return this;
/* 140:    */   }
/* 141:    */   
/* 142:    */   public Matrix rank2(double alpha, Matrix B, Matrix C)
/* 143:    */   {
/* 144:174 */     if ((!(B instanceof DenseMatrix)) || (!(C instanceof DenseMatrix))) {
/* 145:175 */       return super.rank2(alpha, B, C);
/* 146:    */     }
/* 147:177 */     checkRank2(B, C);
/* 148:    */     
/* 149:179 */     double[] Bd = ((DenseMatrix)B).getData();
/* 150:180 */     double[] Cd = ((DenseMatrix)C).getData();
/* 151:    */     
/* 152:182 */     BLAS.getInstance().dsyr2k(this.uplo.netlib(), Transpose.NoTranspose
/* 153:183 */       .netlib(), this.numRows, B.numColumns(), alpha, Bd, 
/* 154:184 */       Math.max(1, this.numRows), Cd, Math.max(1, this.numRows), 1.0D, this.data, 
/* 155:185 */       Math.max(1, this.numRows));
/* 156:    */     
/* 157:187 */     return this;
/* 158:    */   }
/* 159:    */   
/* 160:    */   public Matrix transRank2(double alpha, Matrix B, Matrix C)
/* 161:    */   {
/* 162:192 */     if ((!(B instanceof DenseMatrix)) || (!(C instanceof DenseMatrix))) {
/* 163:193 */       return super.transRank2(alpha, B, C);
/* 164:    */     }
/* 165:195 */     checkTransRank2(B, C);
/* 166:    */     
/* 167:197 */     double[] Bd = ((DenseMatrix)B).getData();
/* 168:198 */     double[] Cd = ((DenseMatrix)C).getData();
/* 169:    */     
/* 170:200 */     BLAS.getInstance().dsyr2k(this.uplo.netlib(), Transpose.Transpose.netlib(), this.numRows, B
/* 171:201 */       .numRows(), alpha, Bd, Math.max(1, B.numRows()), Cd, 
/* 172:202 */       Math.max(1, B.numRows()), 1.0D, this.data, Math.max(1, this.numRows));
/* 173:    */     
/* 174:204 */     return this;
/* 175:    */   }
/* 176:    */   
/* 177:    */   public Matrix solve(Matrix B, Matrix X)
/* 178:    */   {
/* 179:209 */     if (!(X instanceof DenseMatrix)) {
/* 180:210 */       throw new UnsupportedOperationException("X must be a DenseMatrix");
/* 181:    */     }
/* 182:212 */     checkSolve(B, X);
/* 183:    */     
/* 184:214 */     double[] Xd = ((DenseMatrix)X).getData();
/* 185:    */     
/* 186:216 */     X.set(B);
/* 187:    */     
/* 188:    */ 
/* 189:219 */     double[] newData = (double[])this.data.clone();
/* 190:220 */     int[] ipiv = new int[this.numRows];
/* 191:    */     
/* 192:    */ 
/* 193:223 */     double[] work = new double[1];
/* 194:224 */     intW info = new intW(0);
/* 195:225 */     LAPACK.getInstance().dsysv(this.uplo.netlib(), this.numRows, X.numColumns(), newData, 
/* 196:226 */       Matrices.ld(this.numRows), ipiv, Xd, Matrices.ld(this.numRows), work, -1, info);
/* 197:    */     
/* 198:    */ 
/* 199:    */ 
/* 200:230 */     int lwork = -1;
/* 201:231 */     if (info.val != 0) {
/* 202:232 */       lwork = 1;
/* 203:    */     } else {
/* 204:234 */       lwork = Math.max((int)work[0], 1);
/* 205:    */     }
/* 206:235 */     work = new double[lwork];
/* 207:    */     
/* 208:    */ 
/* 209:238 */     info.val = 0;
/* 210:239 */     LAPACK.getInstance().dsysv(this.uplo.netlib(), this.numRows, X.numColumns(), newData, 
/* 211:240 */       Matrices.ld(this.numRows), ipiv, Xd, Matrices.ld(this.numRows), work, lwork, info);
/* 212:243 */     if (info.val > 0) {
/* 213:244 */       throw new MatrixSingularException();
/* 214:    */     }
/* 215:245 */     if (info.val < 0) {
/* 216:246 */       throw new IllegalArgumentException();
/* 217:    */     }
/* 218:248 */     return X;
/* 219:    */   }
/* 220:    */   
/* 221:    */   public Vector solve(Vector b, Vector x)
/* 222:    */   {
/* 223:253 */     DenseMatrix B = new DenseMatrix(b, false);DenseMatrix X = new DenseMatrix(x, false);
/* 224:254 */     solve(B, X);
/* 225:255 */     return x;
/* 226:    */   }
/* 227:    */   
/* 228:    */   public Matrix transSolve(Matrix B, Matrix X)
/* 229:    */   {
/* 230:260 */     return solve(B, X);
/* 231:    */   }
/* 232:    */   
/* 233:    */   public Vector transSolve(Vector b, Vector x)
/* 234:    */   {
/* 235:265 */     return solve(b, x);
/* 236:    */   }
/* 237:    */   
/* 238:    */   Matrix SPDsolve(Matrix B, Matrix X)
/* 239:    */   {
/* 240:269 */     if (!(X instanceof DenseMatrix)) {
/* 241:270 */       throw new UnsupportedOperationException("X must be a DenseMatrix");
/* 242:    */     }
/* 243:272 */     checkSolve(B, X);
/* 244:    */     
/* 245:274 */     double[] Xd = ((DenseMatrix)X).getData();
/* 246:    */     
/* 247:276 */     X.set(B);
/* 248:    */     
/* 249:278 */     intW info = new intW(0);
/* 250:279 */     LAPACK.getInstance().dposv(this.uplo.netlib(), this.numRows, X.numColumns(), 
/* 251:280 */       (double[])this.data.clone(), Matrices.ld(this.numRows), Xd, Matrices.ld(this.numRows), info);
/* 252:283 */     if (info.val > 0) {
/* 253:284 */       throw new MatrixNotSPDException();
/* 254:    */     }
/* 255:285 */     if (info.val < 0) {
/* 256:286 */       throw new IllegalArgumentException();
/* 257:    */     }
/* 258:288 */     return X;
/* 259:    */   }
/* 260:    */   
/* 261:    */   public Matrix transpose()
/* 262:    */   {
/* 263:293 */     return this;
/* 264:    */   }
/* 265:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.AbstractSymmDenseMatrix
 * JD-Core Version:    0.7.0.1
 */