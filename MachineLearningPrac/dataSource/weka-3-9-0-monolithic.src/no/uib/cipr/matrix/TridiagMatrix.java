/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import com.github.fommil.netlib.LAPACK;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.Iterator;
/*   6:    */ import org.netlib.util.intW;
/*   7:    */ 
/*   8:    */ public class TridiagMatrix
/*   9:    */   extends AbstractMatrix
/*  10:    */ {
/*  11:    */   double[] diag;
/*  12:    */   double[] superDiag;
/*  13:    */   double[] subDiag;
/*  14:    */   private int n;
/*  15:    */   
/*  16:    */   public TridiagMatrix(int n)
/*  17:    */   {
/*  18: 54 */     super(n, n);
/*  19: 56 */     if (n < 1) {
/*  20: 57 */       throw new IllegalArgumentException("n must be >= 1");
/*  21:    */     }
/*  22: 59 */     this.n = n;
/*  23: 60 */     this.diag = new double[n];
/*  24: 61 */     this.superDiag = new double[n - 1];
/*  25: 62 */     this.subDiag = new double[n - 1];
/*  26:    */   }
/*  27:    */   
/*  28:    */   public TridiagMatrix(Matrix A)
/*  29:    */   {
/*  30: 72 */     this(A, true);
/*  31:    */   }
/*  32:    */   
/*  33:    */   public TridiagMatrix(Matrix A, boolean deep)
/*  34:    */   {
/*  35: 85 */     super(A);
/*  36: 87 */     if (!isSquare()) {
/*  37: 88 */       throw new IllegalArgumentException("Tridiagonal matrix must be square");
/*  38:    */     }
/*  39: 90 */     if (A.numRows() < 1) {
/*  40: 91 */       throw new IllegalArgumentException("numRows must be >= 1");
/*  41:    */     }
/*  42: 92 */     this.n = this.numRows;
/*  43: 94 */     if (deep)
/*  44:    */     {
/*  45: 95 */       this.diag = new double[this.n];
/*  46: 96 */       this.superDiag = new double[this.n - 1];
/*  47: 97 */       this.subDiag = new double[this.n - 1];
/*  48: 98 */       for (MatrixEntry e : A) {
/*  49: 99 */         if ((e.row() == e.column()) || (e.row() == e.column() - 1) || 
/*  50:100 */           (e.row() == e.column() + 1)) {
/*  51:101 */           set(e.row(), e.column(), e.get());
/*  52:    */         }
/*  53:    */       }
/*  54:    */     }
/*  55:    */     else
/*  56:    */     {
/*  57:103 */       TridiagMatrix B = (TridiagMatrix)A;
/*  58:104 */       this.diag = B.getDiagonal();
/*  59:105 */       this.subDiag = B.getSubDiagonal();
/*  60:106 */       this.superDiag = B.getSuperDiagonal();
/*  61:    */     }
/*  62:    */   }
/*  63:    */   
/*  64:    */   public double[] getDiagonal()
/*  65:    */   {
/*  66:114 */     return this.diag;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public double[] getSubDiagonal()
/*  70:    */   {
/*  71:121 */     return this.subDiag;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public double[] getSuperDiagonal()
/*  75:    */   {
/*  76:128 */     return this.superDiag;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public void add(int row, int column, double value)
/*  80:    */   {
/*  81:133 */     check(row, column);
/*  82:134 */     if (row == column) {
/*  83:135 */       this.diag[row] += value;
/*  84:136 */     } else if (row == column + 1) {
/*  85:137 */       this.subDiag[column] += value;
/*  86:138 */     } else if (row == column - 1) {
/*  87:139 */       this.superDiag[row] += value;
/*  88:    */     } else {
/*  89:141 */       throw new IndexOutOfBoundsException("Insertion index outside of band");
/*  90:    */     }
/*  91:    */   }
/*  92:    */   
/*  93:    */   public double get(int row, int column)
/*  94:    */   {
/*  95:147 */     check(row, column);
/*  96:148 */     if (row == column) {
/*  97:149 */       return this.diag[row];
/*  98:    */     }
/*  99:150 */     if (row == column + 1) {
/* 100:151 */       return this.subDiag[column];
/* 101:    */     }
/* 102:152 */     if (row == column - 1) {
/* 103:153 */       return this.superDiag[row];
/* 104:    */     }
/* 105:155 */     return 0.0D;
/* 106:    */   }
/* 107:    */   
/* 108:    */   public void set(int row, int column, double value)
/* 109:    */   {
/* 110:160 */     check(row, column);
/* 111:161 */     if (row == column) {
/* 112:162 */       this.diag[row] = value;
/* 113:163 */     } else if (row == column + 1) {
/* 114:164 */       this.subDiag[column] = value;
/* 115:165 */     } else if (row == column - 1) {
/* 116:166 */       this.superDiag[row] = value;
/* 117:    */     } else {
/* 118:168 */       throw new IndexOutOfBoundsException("Insertion index outside of band");
/* 119:    */     }
/* 120:    */   }
/* 121:    */   
/* 122:    */   public TridiagMatrix copy()
/* 123:    */   {
/* 124:174 */     return new TridiagMatrix(this);
/* 125:    */   }
/* 126:    */   
/* 127:    */   public TridiagMatrix zero()
/* 128:    */   {
/* 129:179 */     Arrays.fill(this.diag, 0.0D);
/* 130:180 */     Arrays.fill(this.subDiag, 0.0D);
/* 131:181 */     Arrays.fill(this.superDiag, 0.0D);
/* 132:182 */     return this;
/* 133:    */   }
/* 134:    */   
/* 135:    */   public Matrix solve(Matrix B, Matrix X)
/* 136:    */   {
/* 137:187 */     if (!(X instanceof DenseMatrix)) {
/* 138:188 */       throw new UnsupportedOperationException("X must be a DenseMatrix");
/* 139:    */     }
/* 140:190 */     checkSolve(B, X);
/* 141:    */     
/* 142:192 */     double[] Xd = ((DenseMatrix)X).getData();
/* 143:    */     
/* 144:194 */     X.set(B);
/* 145:    */     
/* 146:196 */     intW info = new intW(0);
/* 147:197 */     LAPACK.getInstance()
/* 148:198 */       .dgtsv(this.numRows, X.numColumns(), (double[])this.subDiag.clone(), (double[])this.diag.clone(), 
/* 149:199 */       (double[])this.superDiag.clone(), Xd, Matrices.ld(this.numRows), info);
/* 150:201 */     if (info.val > 0) {
/* 151:202 */       throw new MatrixSingularException();
/* 152:    */     }
/* 153:203 */     if (info.val < 0) {
/* 154:204 */       throw new IllegalArgumentException();
/* 155:    */     }
/* 156:206 */     return X;
/* 157:    */   }
/* 158:    */   
/* 159:    */   public Vector solve(Vector b, Vector x)
/* 160:    */   {
/* 161:211 */     DenseMatrix B = new DenseMatrix(b, false);DenseMatrix X = new DenseMatrix(x, false);
/* 162:212 */     solve(B, X);
/* 163:213 */     return x;
/* 164:    */   }
/* 165:    */   
/* 166:    */   public Matrix transpose()
/* 167:    */   {
/* 168:218 */     double[] otherDiag = this.subDiag;
/* 169:219 */     this.subDiag = this.superDiag;
/* 170:220 */     this.superDiag = otherDiag;
/* 171:221 */     return this;
/* 172:    */   }
/* 173:    */   
/* 174:    */   public Iterator<MatrixEntry> iterator()
/* 175:    */   {
/* 176:226 */     return new TridiagMatrixIterator(null);
/* 177:    */   }
/* 178:    */   
/* 179:    */   private class TridiagMatrixIterator
/* 180:    */     extends AbstractMatrix.RefMatrixIterator
/* 181:    */   {
/* 182:    */     private TridiagMatrixIterator()
/* 183:    */     {
/* 184:232 */       super();
/* 185:    */     }
/* 186:    */     
/* 187:237 */     private double[] band = TridiagMatrix.this.diag;
/* 188:    */     private int bandIndex;
/* 189:    */     private int whichBand;
/* 190:    */     
/* 191:    */     public boolean hasNext()
/* 192:    */     {
/* 193:251 */       return this.whichBand < 3;
/* 194:    */     }
/* 195:    */     
/* 196:    */     public MatrixEntry next()
/* 197:    */     {
/* 198:256 */       this.entry.update(this.row, this.column);
/* 199:259 */       if (this.bandIndex < this.band.length - 1)
/* 200:    */       {
/* 201:260 */         this.bandIndex += 1;
/* 202:    */       }
/* 203:    */       else
/* 204:    */       {
/* 205:263 */         this.bandIndex = 0;
/* 206:264 */         this.whichBand += 1;
/* 207:266 */         if (this.whichBand == 1) {
/* 208:267 */           this.band = TridiagMatrix.this.subDiag;
/* 209:268 */         } else if (this.whichBand == 2) {
/* 210:269 */           this.band = TridiagMatrix.this.superDiag;
/* 211:    */         }
/* 212:273 */         if (this.band.length == 0) {
/* 213:274 */           this.whichBand = 3;
/* 214:    */         }
/* 215:    */       }
/* 216:278 */       if (this.whichBand == 1) {
/* 217:279 */         this.row = (this.bandIndex + 1);
/* 218:    */       } else {
/* 219:281 */         this.row = this.bandIndex;
/* 220:    */       }
/* 221:284 */       if (this.whichBand == 2) {
/* 222:285 */         this.column = (this.bandIndex + 1);
/* 223:    */       } else {
/* 224:287 */         this.column = this.bandIndex;
/* 225:    */       }
/* 226:289 */       return this.entry;
/* 227:    */     }
/* 228:    */   }
/* 229:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.TridiagMatrix
 * JD-Core Version:    0.7.0.1
 */