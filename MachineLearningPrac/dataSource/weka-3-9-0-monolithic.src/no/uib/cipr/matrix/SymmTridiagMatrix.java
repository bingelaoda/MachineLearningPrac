/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import com.github.fommil.netlib.LAPACK;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.Iterator;
/*   6:    */ import org.netlib.util.intW;
/*   7:    */ 
/*   8:    */ public class SymmTridiagMatrix
/*   9:    */   extends AbstractMatrix
/*  10:    */ {
/*  11:    */   double[] diag;
/*  12:    */   double[] offDiag;
/*  13:    */   int n;
/*  14:    */   
/*  15:    */   public SymmTridiagMatrix(double[] diag, double[] offDiag, int n)
/*  16:    */   {
/*  17: 59 */     super(n, n);
/*  18:    */     
/*  19: 61 */     this.n = n;
/*  20: 62 */     if (n < 1) {
/*  21: 63 */       throw new IllegalArgumentException("n must be >= 1");
/*  22:    */     }
/*  23: 65 */     if (diag.length < n) {
/*  24: 66 */       throw new IllegalArgumentException("diag.length < n");
/*  25:    */     }
/*  26: 67 */     if (offDiag.length < n - 1) {
/*  27: 68 */       throw new IllegalArgumentException("offDiag.length < n - 1");
/*  28:    */     }
/*  29: 70 */     this.diag = diag;
/*  30: 71 */     this.offDiag = offDiag;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public SymmTridiagMatrix(double[] diag, double[] offDiag)
/*  34:    */   {
/*  35: 83 */     this(diag, offDiag, diag.length);
/*  36:    */   }
/*  37:    */   
/*  38:    */   public SymmTridiagMatrix(int n)
/*  39:    */   {
/*  40: 95 */     super(n, n);
/*  41: 97 */     if (n < 1) {
/*  42: 98 */       throw new IllegalArgumentException("n must be >= 1");
/*  43:    */     }
/*  44:100 */     this.n = this.numRows;
/*  45:101 */     this.diag = new double[n];
/*  46:102 */     this.offDiag = new double[n - 1];
/*  47:    */   }
/*  48:    */   
/*  49:    */   public SymmTridiagMatrix(Matrix A)
/*  50:    */   {
/*  51:113 */     this(A, true);
/*  52:    */   }
/*  53:    */   
/*  54:    */   public SymmTridiagMatrix(Matrix A, boolean deep)
/*  55:    */   {
/*  56:128 */     super(A);
/*  57:130 */     if (!isSquare()) {
/*  58:131 */       throw new IllegalArgumentException("Symmetric matrix must be square");
/*  59:    */     }
/*  60:133 */     if (A.numRows() < 1) {
/*  61:134 */       throw new IllegalArgumentException("numRows must be >= 1");
/*  62:    */     }
/*  63:135 */     this.n = this.numRows;
/*  64:137 */     if (deep)
/*  65:    */     {
/*  66:138 */       this.diag = new double[this.n];
/*  67:139 */       this.offDiag = new double[Math.max(this.n - 1, 0)];
/*  68:140 */       for (MatrixEntry e : A) {
/*  69:141 */         if ((e.row() == e.column()) || (e.row() == e.column() + 1)) {
/*  70:142 */           set(e.row(), e.column(), e.get());
/*  71:    */         }
/*  72:    */       }
/*  73:    */     }
/*  74:    */     else
/*  75:    */     {
/*  76:144 */       SymmTridiagMatrix B = (SymmTridiagMatrix)A;
/*  77:145 */       this.diag = B.getDiagonal();
/*  78:146 */       this.offDiag = B.getOffDiagonal();
/*  79:    */     }
/*  80:    */   }
/*  81:    */   
/*  82:    */   public double[] getDiagonal()
/*  83:    */   {
/*  84:154 */     return this.diag;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public double[] getOffDiagonal()
/*  88:    */   {
/*  89:161 */     return this.offDiag;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public void add(int row, int column, double value)
/*  93:    */   {
/*  94:166 */     check(row, column);
/*  95:167 */     if (row == column) {
/*  96:168 */       this.diag[row] += value;
/*  97:169 */     } else if (row == column + 1) {
/*  98:170 */       this.offDiag[column] += value;
/*  99:171 */     } else if (row != column - 1) {
/* 100:172 */       throw new IndexOutOfBoundsException("Insertion index outside of band");
/* 101:    */     }
/* 102:    */   }
/* 103:    */   
/* 104:    */   public double get(int row, int column)
/* 105:    */   {
/* 106:178 */     check(row, column);
/* 107:179 */     if (row == column) {
/* 108:180 */       return this.diag[row];
/* 109:    */     }
/* 110:181 */     if (row == column + 1) {
/* 111:182 */       return this.offDiag[column];
/* 112:    */     }
/* 113:183 */     if (row == column - 1) {
/* 114:184 */       return this.offDiag[row];
/* 115:    */     }
/* 116:186 */     return 0.0D;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public void set(int row, int column, double value)
/* 120:    */   {
/* 121:191 */     check(row, column);
/* 122:192 */     if (row == column) {
/* 123:193 */       this.diag[row] = value;
/* 124:194 */     } else if (row == column + 1) {
/* 125:195 */       this.offDiag[column] = value;
/* 126:196 */     } else if (row != column - 1) {
/* 127:197 */       throw new IndexOutOfBoundsException("Insertion index outside of band");
/* 128:    */     }
/* 129:    */   }
/* 130:    */   
/* 131:    */   public SymmTridiagMatrix copy()
/* 132:    */   {
/* 133:203 */     return new SymmTridiagMatrix(this);
/* 134:    */   }
/* 135:    */   
/* 136:    */   public SymmTridiagMatrix zero()
/* 137:    */   {
/* 138:208 */     Arrays.fill(this.diag, 0.0D);
/* 139:209 */     Arrays.fill(this.offDiag, 0.0D);
/* 140:210 */     return this;
/* 141:    */   }
/* 142:    */   
/* 143:    */   public Matrix solve(Matrix B, Matrix X)
/* 144:    */   {
/* 145:215 */     if (!(X instanceof DenseMatrix)) {
/* 146:216 */       throw new UnsupportedOperationException("X must be a DenseMatrix");
/* 147:    */     }
/* 148:218 */     checkSolve(B, X);
/* 149:    */     
/* 150:220 */     double[] Xd = ((DenseMatrix)X).getData();
/* 151:    */     
/* 152:222 */     X.set(B);
/* 153:223 */     intW info = new intW(0);
/* 154:224 */     LAPACK.getInstance().dgtsv(this.numRows, X.numColumns(), (double[])this.offDiag.clone(), 
/* 155:225 */       (double[])this.diag.clone(), (double[])this.offDiag.clone(), Xd, Matrices.ld(this.numRows), info);
/* 156:227 */     if (info.val > 0) {
/* 157:228 */       throw new MatrixSingularException();
/* 158:    */     }
/* 159:229 */     if (info.val < 0) {
/* 160:230 */       throw new IllegalArgumentException();
/* 161:    */     }
/* 162:232 */     return X;
/* 163:    */   }
/* 164:    */   
/* 165:    */   public Vector solve(Vector b, Vector x)
/* 166:    */   {
/* 167:237 */     DenseMatrix B = new DenseMatrix(b, false);DenseMatrix X = new DenseMatrix(x, false);
/* 168:238 */     solve(B, X);
/* 169:239 */     return x;
/* 170:    */   }
/* 171:    */   
/* 172:    */   public Matrix transSolve(Matrix B, Matrix X)
/* 173:    */   {
/* 174:244 */     return solve(B, X);
/* 175:    */   }
/* 176:    */   
/* 177:    */   public Vector transSolve(Vector b, Vector x)
/* 178:    */   {
/* 179:249 */     return solve(b, x);
/* 180:    */   }
/* 181:    */   
/* 182:    */   public Matrix transpose()
/* 183:    */   {
/* 184:254 */     return this;
/* 185:    */   }
/* 186:    */   
/* 187:    */   public Iterator<MatrixEntry> iterator()
/* 188:    */   {
/* 189:259 */     return new SymmTridiagMatrixIterator(null);
/* 190:    */   }
/* 191:    */   
/* 192:    */   private class SymmTridiagMatrixIterator
/* 193:    */     extends AbstractMatrix.RefMatrixIterator
/* 194:    */   {
/* 195:    */     private SymmTridiagMatrixIterator()
/* 196:    */     {
/* 197:265 */       super();
/* 198:    */     }
/* 199:    */     
/* 200:270 */     private double[] band = SymmTridiagMatrix.this.diag;
/* 201:    */     private int bandIndex;
/* 202:    */     private int whichBand;
/* 203:    */     
/* 204:    */     public boolean hasNext()
/* 205:    */     {
/* 206:284 */       return this.whichBand < 3;
/* 207:    */     }
/* 208:    */     
/* 209:    */     public MatrixEntry next()
/* 210:    */     {
/* 211:289 */       this.entry.update(this.row, this.column);
/* 212:292 */       if (this.bandIndex < this.band.length - 1)
/* 213:    */       {
/* 214:293 */         this.bandIndex += 1;
/* 215:    */       }
/* 216:    */       else
/* 217:    */       {
/* 218:296 */         this.bandIndex = 0;
/* 219:297 */         this.whichBand += 1;
/* 220:    */         
/* 221:299 */         this.band = SymmTridiagMatrix.this.offDiag;
/* 222:303 */         if (this.band.length == 0) {
/* 223:304 */           this.whichBand = 3;
/* 224:    */         }
/* 225:    */       }
/* 226:308 */       if (this.whichBand == 1) {
/* 227:309 */         this.row = (this.bandIndex + 1);
/* 228:    */       } else {
/* 229:311 */         this.row = this.bandIndex;
/* 230:    */       }
/* 231:314 */       if (this.whichBand == 2) {
/* 232:315 */         this.column = (this.bandIndex + 1);
/* 233:    */       } else {
/* 234:317 */         this.column = this.bandIndex;
/* 235:    */       }
/* 236:319 */       return this.entry;
/* 237:    */     }
/* 238:    */   }
/* 239:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.SymmTridiagMatrix
 * JD-Core Version:    0.7.0.1
 */