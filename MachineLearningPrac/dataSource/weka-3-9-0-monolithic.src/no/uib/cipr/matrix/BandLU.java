/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import com.github.fommil.netlib.LAPACK;
/*   4:    */ import org.netlib.util.doubleW;
/*   5:    */ import org.netlib.util.intW;
/*   6:    */ 
/*   7:    */ public class BandLU
/*   8:    */ {
/*   9:    */   private final int n;
/*  10:    */   private final int kl;
/*  11:    */   private final int ku;
/*  12:    */   private final BandMatrix LU;
/*  13:    */   private final int[] ipiv;
/*  14:    */   private boolean singular;
/*  15:    */   
/*  16:    */   public BandLU(int n, int kl, int ku)
/*  17:    */   {
/*  18: 70 */     this.n = n;
/*  19: 71 */     this.kl = kl;
/*  20: 72 */     this.ku = ku;
/*  21:    */     
/*  22: 74 */     this.LU = new BandMatrix(n, kl, ku + kl);
/*  23:    */     
/*  24: 76 */     this.ipiv = new int[n];
/*  25:    */   }
/*  26:    */   
/*  27:    */   public static BandLU factorize(BandMatrix A)
/*  28:    */   {
/*  29: 87 */     return new BandLU(A.numRows(), A.kl, A.ku).factor(A, false);
/*  30:    */   }
/*  31:    */   
/*  32:    */   public BandLU factor(BandMatrix A, boolean inplace)
/*  33:    */   {
/*  34:102 */     if (inplace) {
/*  35:103 */       return factor(A);
/*  36:    */     }
/*  37:105 */     return factor(new BandMatrix(A, this.kl, this.kl + this.ku));
/*  38:    */   }
/*  39:    */   
/*  40:    */   public BandLU factor(BandMatrix A)
/*  41:    */   {
/*  42:118 */     if (!A.isSquare()) {
/*  43:119 */       throw new IllegalArgumentException("!A.isSquare()");
/*  44:    */     }
/*  45:120 */     if (this.n != A.numRows()) {
/*  46:121 */       throw new IllegalArgumentException("n != A.numRows()");
/*  47:    */     }
/*  48:122 */     if (A.ku != this.ku + this.kl) {
/*  49:123 */       throw new IllegalArgumentException("A.ku != ku + kl");
/*  50:    */     }
/*  51:125 */     this.singular = false;
/*  52:    */     
/*  53:127 */     intW info = new intW(0);
/*  54:128 */     LAPACK.getInstance().dgbtrf(this.n, this.n, this.kl, this.ku, A.getData(), 2 * this.kl + this.ku + 1, this.ipiv, info);
/*  55:131 */     if (info.val > 0) {
/*  56:132 */       this.singular = true;
/*  57:133 */     } else if (info.val < 0) {
/*  58:134 */       throw new IllegalArgumentException();
/*  59:    */     }
/*  60:136 */     this.LU.set(A);
/*  61:    */     
/*  62:138 */     return this;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public UnitLowerTriangBandMatrix getL()
/*  66:    */   {
/*  67:145 */     return new UnitLowerTriangBandMatrix(this.LU, this.LU.numSubDiagonals(), false);
/*  68:    */   }
/*  69:    */   
/*  70:    */   public UpperTriangBandMatrix getU()
/*  71:    */   {
/*  72:152 */     return new UpperTriangBandMatrix(this.LU, this.LU.numSuperDiagonals(), false);
/*  73:    */   }
/*  74:    */   
/*  75:    */   public BandMatrix getLU()
/*  76:    */   {
/*  77:159 */     return this.LU;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public int[] getPivots()
/*  81:    */   {
/*  82:166 */     return this.ipiv;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public boolean isSingular()
/*  86:    */   {
/*  87:173 */     return this.singular;
/*  88:    */   }
/*  89:    */   
/*  90:    */   public double rcond(Matrix A, Matrix.Norm norm)
/*  91:    */   {
/*  92:188 */     if ((norm != Matrix.Norm.One) && (norm != Matrix.Norm.Infinity)) {
/*  93:189 */       throw new IllegalArgumentException("Only the 1 or the Infinity norms are supported");
/*  94:    */     }
/*  95:191 */     if (A.numRows() != this.n) {
/*  96:192 */       throw new IllegalArgumentException("A.numRows() != n");
/*  97:    */     }
/*  98:193 */     if (!A.isSquare()) {
/*  99:194 */       throw new IllegalArgumentException("!A.isSquare()");
/* 100:    */     }
/* 101:196 */     double anorm = A.norm(norm);
/* 102:    */     
/* 103:198 */     double[] work = new double[3 * this.n];
/* 104:199 */     int[] lwork = new int[this.n];
/* 105:    */     
/* 106:201 */     intW info = new intW(0);
/* 107:202 */     doubleW rcond = new doubleW(0.0D);
/* 108:203 */     LAPACK.getInstance().dgbcon(norm.netlib(), this.n, this.kl, this.ku, this.LU.getData(), 
/* 109:204 */       Matrices.ld(2 * this.kl + this.ku + 1), this.ipiv, anorm, rcond, work, lwork, info);
/* 110:207 */     if (info.val < 0) {
/* 111:208 */       throw new IllegalArgumentException();
/* 112:    */     }
/* 113:210 */     return rcond.val;
/* 114:    */   }
/* 115:    */   
/* 116:    */   public DenseMatrix solve(DenseMatrix B)
/* 117:    */     throws MatrixSingularException
/* 118:    */   {
/* 119:217 */     return solve(B, Transpose.NoTranspose);
/* 120:    */   }
/* 121:    */   
/* 122:    */   public DenseMatrix transSolve(DenseMatrix B)
/* 123:    */     throws MatrixSingularException
/* 124:    */   {
/* 125:224 */     return solve(B, Transpose.Transpose);
/* 126:    */   }
/* 127:    */   
/* 128:    */   private DenseMatrix solve(DenseMatrix B, Transpose trans)
/* 129:    */     throws MatrixSingularException
/* 130:    */   {
/* 131:229 */     if (this.singular) {
/* 132:230 */       throw new MatrixSingularException();
/* 133:    */     }
/* 134:231 */     if (B.numRows() != this.n) {
/* 135:232 */       throw new IllegalArgumentException("B.numRows() != n");
/* 136:    */     }
/* 137:234 */     intW info = new intW(0);
/* 138:235 */     LAPACK.getInstance().dgbtrs(trans.netlib(), this.n, this.kl, this.ku, B.numColumns(), this.LU
/* 139:236 */       .getData(), 2 * this.kl + this.ku + 1, this.ipiv, B.getData(), 
/* 140:237 */       Matrices.ld(this.n), info);
/* 141:239 */     if (info.val < 0) {
/* 142:240 */       throw new IllegalArgumentException();
/* 143:    */     }
/* 144:242 */     return B;
/* 145:    */   }
/* 146:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.BandLU
 * JD-Core Version:    0.7.0.1
 */