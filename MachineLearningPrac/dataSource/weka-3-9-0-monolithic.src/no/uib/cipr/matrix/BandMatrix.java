/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import com.github.fommil.netlib.BLAS;
/*   4:    */ import com.github.fommil.netlib.LAPACK;
/*   5:    */ import java.util.Arrays;
/*   6:    */ import org.netlib.util.intW;
/*   7:    */ 
/*   8:    */ public class BandMatrix
/*   9:    */   extends AbstractBandMatrix
/*  10:    */ {
/*  11:    */   public BandMatrix(int n, int kl, int ku)
/*  12:    */   {
/*  13:121 */     super(n, kl, ku);
/*  14:    */   }
/*  15:    */   
/*  16:    */   public BandMatrix(Matrix A, int kl, int ku)
/*  17:    */   {
/*  18:137 */     super(A, kl, ku);
/*  19:    */   }
/*  20:    */   
/*  21:    */   public BandMatrix(Matrix A, int kl, int ku, boolean deep)
/*  22:    */   {
/*  23:156 */     super(A, kl, ku, deep);
/*  24:    */   }
/*  25:    */   
/*  26:    */   public BandMatrix copy()
/*  27:    */   {
/*  28:161 */     return new BandMatrix(this, this.kl, this.ku);
/*  29:    */   }
/*  30:    */   
/*  31:    */   public Matrix zero()
/*  32:    */   {
/*  33:166 */     Arrays.fill(this.data, 0.0D);
/*  34:167 */     return this;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public Vector multAdd(double alpha, Vector x, Vector y)
/*  38:    */   {
/*  39:172 */     if ((!(x instanceof DenseVector)) || (!(y instanceof DenseVector))) {
/*  40:173 */       return super.multAdd(alpha, x, y);
/*  41:    */     }
/*  42:175 */     checkMultAdd(x, y);
/*  43:    */     
/*  44:177 */     double[] xd = ((DenseVector)x).getData();
/*  45:178 */     double[] yd = ((DenseVector)y).getData();
/*  46:    */     
/*  47:180 */     BLAS.getInstance().dgbmv(Transpose.NoTranspose.netlib(), this.numRows, this.numColumns, this.kl, this.ku, alpha, this.data, this.kl + this.ku + 1, xd, 1, 1.0D, yd, 1);
/*  48:    */     
/*  49:    */ 
/*  50:183 */     return y;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public Vector transMultAdd(double alpha, Vector x, Vector y)
/*  54:    */   {
/*  55:188 */     if ((!(x instanceof DenseVector)) || (!(y instanceof DenseVector))) {
/*  56:189 */       return super.transMultAdd(alpha, x, y);
/*  57:    */     }
/*  58:191 */     checkTransMultAdd(x, y);
/*  59:    */     
/*  60:193 */     double[] xd = ((DenseVector)x).getData();
/*  61:194 */     double[] yd = ((DenseVector)y).getData();
/*  62:    */     
/*  63:196 */     BLAS.getInstance().dgbmv(Transpose.Transpose.netlib(), this.numRows, this.numColumns, this.kl, this.ku, alpha, this.data, this.kl + this.ku + 1, xd, 1, 1.0D, yd, 1);
/*  64:    */     
/*  65:    */ 
/*  66:199 */     return y;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public Matrix solve(Matrix B, Matrix X)
/*  70:    */   {
/*  71:204 */     if (!(X instanceof DenseMatrix)) {
/*  72:205 */       throw new UnsupportedOperationException("X must be a DenseMatrix");
/*  73:    */     }
/*  74:207 */     checkSolve(B, X);
/*  75:    */     
/*  76:209 */     double[] Xd = ((DenseMatrix)X).getData();
/*  77:    */     
/*  78:211 */     X.set(B);
/*  79:    */     
/*  80:    */ 
/*  81:    */ 
/*  82:215 */     BandMatrix Af = new BandMatrix(this, this.kl, this.ku + this.kl);
/*  83:216 */     int[] ipiv = new int[this.numRows];
/*  84:    */     
/*  85:218 */     intW info = new intW(0);
/*  86:219 */     LAPACK.getInstance().dgbsv(this.numRows, this.kl, this.ku, X.numColumns(), Af
/*  87:220 */       .getData(), Matrices.ld(2 * this.kl + this.ku + 1), ipiv, Xd, 
/*  88:221 */       Matrices.ld(this.numRows), info);
/*  89:223 */     if (info.val > 0) {
/*  90:224 */       throw new MatrixSingularException();
/*  91:    */     }
/*  92:225 */     if (info.val < 0) {
/*  93:226 */       throw new IllegalArgumentException();
/*  94:    */     }
/*  95:228 */     return X;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public Vector solve(Vector b, Vector x)
/*  99:    */   {
/* 100:233 */     DenseMatrix B = new DenseMatrix(b, false);DenseMatrix X = new DenseMatrix(x, false);
/* 101:234 */     solve(B, X);
/* 102:235 */     return x;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public Matrix transpose()
/* 106:    */   {
/* 107:240 */     checkTranspose();
/* 108:242 */     if (this.kl != this.ku) {
/* 109:243 */       throw new IllegalArgumentException("kl != ku");
/* 110:    */     }
/* 111:245 */     for (int j = 0; j < this.numColumns; j++) {
/* 112:246 */       for (int i = j + 1; i < Math.min(j + this.kl + 1, this.numRows); i++)
/* 113:    */       {
/* 114:247 */         double value = get(i, j);
/* 115:248 */         set(i, j, get(j, i));
/* 116:249 */         set(j, i, value);
/* 117:    */       }
/* 118:    */     }
/* 119:252 */     return this;
/* 120:    */   }
/* 121:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.BandMatrix
 * JD-Core Version:    0.7.0.1
 */