/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import com.github.fommil.netlib.LAPACK;
/*   4:    */ import org.netlib.util.doubleW;
/*   5:    */ import org.netlib.util.intW;
/*   6:    */ 
/*   7:    */ public class PackCholesky
/*   8:    */ {
/*   9:    */   private final int n;
/*  10:    */   private LowerTriangPackMatrix Cl;
/*  11:    */   private UpperTriangPackMatrix Cu;
/*  12:    */   private boolean notspd;
/*  13:    */   private final boolean upper;
/*  14:    */   
/*  15:    */   public PackCholesky(int n, boolean upper)
/*  16:    */   {
/*  17: 69 */     this.n = n;
/*  18: 70 */     this.upper = upper;
/*  19: 72 */     if (upper) {
/*  20: 73 */       this.Cu = new UpperTriangPackMatrix(n);
/*  21:    */     } else {
/*  22: 75 */       this.Cl = new LowerTriangPackMatrix(n);
/*  23:    */     }
/*  24:    */   }
/*  25:    */   
/*  26:    */   public static PackCholesky factorize(Matrix A)
/*  27:    */   {
/*  28: 87 */     return new PackCholesky(A.numRows(), true).factor(new UpperSPDPackMatrix(A));
/*  29:    */   }
/*  30:    */   
/*  31:    */   public PackCholesky factor(LowerSPDPackMatrix A)
/*  32:    */   {
/*  33: 98 */     if (this.upper) {
/*  34: 99 */       throw new IllegalArgumentException("Cholesky decomposition constructed for upper matrices");
/*  35:    */     }
/*  36:102 */     return decompose(A);
/*  37:    */   }
/*  38:    */   
/*  39:    */   public PackCholesky factor(UpperSPDPackMatrix A)
/*  40:    */   {
/*  41:113 */     if (!this.upper) {
/*  42:114 */       throw new IllegalArgumentException("Cholesky decomposition constructed for lower matrices");
/*  43:    */     }
/*  44:117 */     return decompose(A);
/*  45:    */   }
/*  46:    */   
/*  47:    */   private PackCholesky decompose(AbstractPackMatrix A)
/*  48:    */   {
/*  49:121 */     if (this.n != A.numRows()) {
/*  50:122 */       throw new IllegalArgumentException("n != A.numRows()");
/*  51:    */     }
/*  52:124 */     this.notspd = false;
/*  53:    */     
/*  54:126 */     intW info = new intW(0);
/*  55:127 */     if (this.upper) {
/*  56:128 */       LAPACK.getInstance().dpptrf(UpLo.Upper.netlib(), A.numRows(), A
/*  57:129 */         .getData(), info);
/*  58:    */     } else {
/*  59:131 */       LAPACK.getInstance().dpptrf(UpLo.Lower.netlib(), A.numRows(), A
/*  60:132 */         .getData(), info);
/*  61:    */     }
/*  62:134 */     if (info.val > 0) {
/*  63:135 */       this.notspd = true;
/*  64:136 */     } else if (info.val < 0) {
/*  65:137 */       throw new IllegalArgumentException();
/*  66:    */     }
/*  67:139 */     if (this.upper) {
/*  68:140 */       this.Cu.set(A);
/*  69:    */     } else {
/*  70:142 */       this.Cl.set(A);
/*  71:    */     }
/*  72:144 */     return this;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public boolean isSPD()
/*  76:    */   {
/*  77:151 */     return !this.notspd;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public LowerTriangPackMatrix getL()
/*  81:    */   {
/*  82:159 */     if (!this.upper) {
/*  83:160 */       return this.Cl;
/*  84:    */     }
/*  85:162 */     throw new UnsupportedOperationException();
/*  86:    */   }
/*  87:    */   
/*  88:    */   public UpperTriangPackMatrix getU()
/*  89:    */   {
/*  90:170 */     if (this.upper) {
/*  91:171 */       return this.Cu;
/*  92:    */     }
/*  93:173 */     throw new UnsupportedOperationException();
/*  94:    */   }
/*  95:    */   
/*  96:    */   public DenseMatrix solve(DenseMatrix B)
/*  97:    */     throws MatrixNotSPDException
/*  98:    */   {
/*  99:180 */     if (this.notspd) {
/* 100:181 */       throw new MatrixNotSPDException();
/* 101:    */     }
/* 102:182 */     if (B.numRows() != this.n) {
/* 103:183 */       throw new IllegalArgumentException("B.numRows() != n");
/* 104:    */     }
/* 105:185 */     intW info = new intW(0);
/* 106:186 */     if (this.upper) {
/* 107:187 */       LAPACK.getInstance().dpptrs(UpLo.Upper.netlib(), this.Cu.numRows(), B
/* 108:188 */         .numColumns(), this.Cu.getData(), B.getData(), 
/* 109:189 */         Matrices.ld(this.Cu.numRows()), info);
/* 110:    */     } else {
/* 111:191 */       LAPACK.getInstance().dpptrs(UpLo.Lower.netlib(), this.Cl.numRows(), B
/* 112:192 */         .numColumns(), this.Cl.getData(), B.getData(), 
/* 113:193 */         Matrices.ld(this.Cl.numRows()), info);
/* 114:    */     }
/* 115:195 */     if (info.val < 0) {
/* 116:196 */       throw new IllegalArgumentException();
/* 117:    */     }
/* 118:198 */     return B;
/* 119:    */   }
/* 120:    */   
/* 121:    */   public double rcond(Matrix A)
/* 122:    */   {
/* 123:210 */     if (A.numRows() != this.n) {
/* 124:211 */       throw new IllegalArgumentException("A.numRows() != n");
/* 125:    */     }
/* 126:212 */     if (!A.isSquare()) {
/* 127:213 */       throw new IllegalArgumentException("!A.isSquare()");
/* 128:    */     }
/* 129:215 */     double anorm = A.norm(Matrix.Norm.One);
/* 130:    */     
/* 131:217 */     double[] work = new double[3 * this.n];
/* 132:218 */     int[] iwork = new int[this.n];
/* 133:    */     
/* 134:220 */     intW info = new intW(0);
/* 135:221 */     doubleW rcond = new doubleW(0.0D);
/* 136:222 */     if (this.upper) {
/* 137:223 */       LAPACK.getInstance().dppcon(UpLo.Upper.netlib(), this.n, this.Cu.getData(), anorm, rcond, work, iwork, info);
/* 138:    */     } else {
/* 139:226 */       LAPACK.getInstance().dppcon(UpLo.Lower.netlib(), this.n, this.Cl.getData(), anorm, rcond, work, iwork, info);
/* 140:    */     }
/* 141:229 */     if (info.val < 0) {
/* 142:230 */       throw new IllegalArgumentException();
/* 143:    */     }
/* 144:232 */     return rcond.val;
/* 145:    */   }
/* 146:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.PackCholesky
 * JD-Core Version:    0.7.0.1
 */