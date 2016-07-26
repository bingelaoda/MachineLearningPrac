/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import com.github.fommil.netlib.LAPACK;
/*   4:    */ import org.netlib.util.doubleW;
/*   5:    */ import org.netlib.util.intW;
/*   6:    */ 
/*   7:    */ public class BandCholesky
/*   8:    */ {
/*   9:    */   private final int n;
/*  10:    */   private final int kd;
/*  11:    */   private LowerTriangBandMatrix Cl;
/*  12:    */   private UpperTriangBandMatrix Cu;
/*  13:    */   private boolean notspd;
/*  14:    */   private final boolean upper;
/*  15:    */   
/*  16:    */   public BandCholesky(int n, int kd, boolean upper)
/*  17:    */   {
/*  18: 76 */     this.n = n;
/*  19: 77 */     this.kd = kd;
/*  20: 78 */     this.upper = upper;
/*  21: 80 */     if (upper) {
/*  22: 81 */       this.Cu = new UpperTriangBandMatrix(n, kd);
/*  23:    */     } else {
/*  24: 83 */       this.Cl = new LowerTriangBandMatrix(n, kd);
/*  25:    */     }
/*  26:    */   }
/*  27:    */   
/*  28:    */   public static BandCholesky factorize(LowerSPDBandMatrix A)
/*  29:    */   {
/*  30: 94 */     return new BandCholesky(A.numRows(), A.kl, false).factor(A);
/*  31:    */   }
/*  32:    */   
/*  33:    */   public static BandCholesky factorize(UpperSPDBandMatrix A)
/*  34:    */   {
/*  35:105 */     return new BandCholesky(A.numRows(), A.ku, true).factor(A);
/*  36:    */   }
/*  37:    */   
/*  38:    */   public BandCholesky factor(LowerSPDBandMatrix A)
/*  39:    */   {
/*  40:116 */     if (this.upper) {
/*  41:117 */       throw new IllegalArgumentException("Cholesky decomposition constructed for upper matrices");
/*  42:    */     }
/*  43:120 */     return decompose(A);
/*  44:    */   }
/*  45:    */   
/*  46:    */   public BandCholesky factor(UpperSPDBandMatrix A)
/*  47:    */   {
/*  48:131 */     if (!this.upper) {
/*  49:132 */       throw new IllegalArgumentException("Cholesky decomposition constructed for lower matrices");
/*  50:    */     }
/*  51:135 */     return decompose(A);
/*  52:    */   }
/*  53:    */   
/*  54:    */   private BandCholesky decompose(AbstractBandMatrix A)
/*  55:    */   {
/*  56:139 */     if (this.n != A.numRows()) {
/*  57:140 */       throw new IllegalArgumentException("n != A.numRows()");
/*  58:    */     }
/*  59:141 */     if ((this.upper) && (A.ku != this.kd)) {
/*  60:142 */       throw new IllegalArgumentException("A.ku != kd");
/*  61:    */     }
/*  62:143 */     if ((!this.upper) && (A.kl != this.kd)) {
/*  63:144 */       throw new IllegalArgumentException("A.kl != kd");
/*  64:    */     }
/*  65:146 */     this.notspd = false;
/*  66:    */     
/*  67:148 */     intW info = new intW(0);
/*  68:149 */     if (this.upper) {
/*  69:150 */       LAPACK.getInstance().dpbtrf(UpLo.Upper.netlib(), this.n, this.kd, A
/*  70:151 */         .getData(), Matrices.ld(this.kd + 1), info);
/*  71:    */     } else {
/*  72:153 */       LAPACK.getInstance().dpbtrf(UpLo.Lower.netlib(), this.n, this.kd, A
/*  73:154 */         .getData(), Matrices.ld(this.kd + 1), info);
/*  74:    */     }
/*  75:156 */     if (info.val > 0) {
/*  76:157 */       this.notspd = true;
/*  77:158 */     } else if (info.val < 0) {
/*  78:159 */       throw new IllegalArgumentException();
/*  79:    */     }
/*  80:161 */     if (this.upper) {
/*  81:162 */       this.Cu.set(A);
/*  82:    */     } else {
/*  83:164 */       this.Cl.set(A);
/*  84:    */     }
/*  85:166 */     return this;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public LowerTriangBandMatrix getL()
/*  89:    */   {
/*  90:174 */     if (!this.upper) {
/*  91:175 */       return this.Cl;
/*  92:    */     }
/*  93:177 */     throw new UnsupportedOperationException();
/*  94:    */   }
/*  95:    */   
/*  96:    */   public UpperTriangBandMatrix getU()
/*  97:    */   {
/*  98:185 */     if (this.upper) {
/*  99:186 */       return this.Cu;
/* 100:    */     }
/* 101:188 */     throw new UnsupportedOperationException();
/* 102:    */   }
/* 103:    */   
/* 104:    */   public boolean isSPD()
/* 105:    */   {
/* 106:195 */     return !this.notspd;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public double rcond(Matrix A)
/* 110:    */   {
/* 111:207 */     if (A.numRows() != this.n) {
/* 112:208 */       throw new IllegalArgumentException("A.numRows() != n");
/* 113:    */     }
/* 114:209 */     if (!A.isSquare()) {
/* 115:210 */       throw new IllegalArgumentException("!A.isSquare()");
/* 116:    */     }
/* 117:212 */     double anorm = A.norm(Matrix.Norm.One);
/* 118:    */     
/* 119:214 */     double[] work = new double[3 * this.n];
/* 120:215 */     int[] lwork = new int[this.n];
/* 121:    */     
/* 122:217 */     intW info = new intW(0);
/* 123:218 */     doubleW rcond = new doubleW(0.0D);
/* 124:219 */     if (this.upper) {
/* 125:220 */       LAPACK.getInstance().dpbcon(UpLo.Upper.netlib(), this.n, this.kd, this.Cu
/* 126:221 */         .getData(), Matrices.ld(this.kd + 1), anorm, rcond, work, lwork, info);
/* 127:    */     } else {
/* 128:224 */       LAPACK.getInstance().dpbcon(UpLo.Lower.netlib(), this.n, this.kd, this.Cl
/* 129:225 */         .getData(), Matrices.ld(this.kd + 1), anorm, rcond, work, lwork, info);
/* 130:    */     }
/* 131:228 */     if (info.val < 0) {
/* 132:229 */       throw new IllegalArgumentException();
/* 133:    */     }
/* 134:231 */     return rcond.val;
/* 135:    */   }
/* 136:    */   
/* 137:    */   public DenseMatrix solve(DenseMatrix B)
/* 138:    */     throws MatrixNotSPDException
/* 139:    */   {
/* 140:238 */     if (this.notspd) {
/* 141:239 */       throw new MatrixNotSPDException();
/* 142:    */     }
/* 143:240 */     if (B.numRows() != this.n) {
/* 144:241 */       throw new IllegalArgumentException("B.numRows() != n");
/* 145:    */     }
/* 146:243 */     intW info = new intW(0);
/* 147:244 */     if (this.upper) {
/* 148:245 */       LAPACK.getInstance().dpbtrs(UpLo.Upper.netlib(), this.n, this.kd, B
/* 149:246 */         .numColumns(), this.Cu.getData(), Matrices.ld(this.kd + 1), B
/* 150:247 */         .getData(), Matrices.ld(this.n), info);
/* 151:    */     } else {
/* 152:249 */       LAPACK.getInstance().dpbtrs(UpLo.Lower.netlib(), this.n, this.kd, B
/* 153:250 */         .numColumns(), this.Cl.getData(), Matrices.ld(this.kd + 1), B
/* 154:251 */         .getData(), Matrices.ld(this.n), info);
/* 155:    */     }
/* 156:253 */     if (info.val < 0) {
/* 157:254 */       throw new IllegalArgumentException();
/* 158:    */     }
/* 159:256 */     return B;
/* 160:    */   }
/* 161:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.BandCholesky
 * JD-Core Version:    0.7.0.1
 */