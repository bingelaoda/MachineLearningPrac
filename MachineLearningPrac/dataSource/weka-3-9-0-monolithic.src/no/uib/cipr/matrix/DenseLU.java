/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import com.github.fommil.netlib.LAPACK;
/*   4:    */ import org.netlib.util.doubleW;
/*   5:    */ import org.netlib.util.intW;
/*   6:    */ 
/*   7:    */ public class DenseLU
/*   8:    */ {
/*   9:    */   private DenseMatrix LU;
/*  10:    */   private int[] piv;
/*  11:    */   private boolean singular;
/*  12:    */   
/*  13:    */   public DenseLU(int m, int n)
/*  14:    */   {
/*  15: 57 */     this.LU = new DenseMatrix(m, n);
/*  16: 58 */     this.piv = new int[Math.min(m, n)];
/*  17:    */   }
/*  18:    */   
/*  19:    */   public static DenseLU factorize(Matrix A)
/*  20:    */   {
/*  21: 69 */     return new DenseLU(A.numRows(), A.numColumns()).factor(new DenseMatrix(A));
/*  22:    */   }
/*  23:    */   
/*  24:    */   public DenseLU factor(DenseMatrix A)
/*  25:    */   {
/*  26: 81 */     this.singular = false;
/*  27:    */     
/*  28: 83 */     intW info = new intW(0);
/*  29: 84 */     LAPACK.getInstance().dgetrf(A.numRows(), A.numColumns(), A.getData(), 
/*  30: 85 */       Matrices.ld(A.numRows()), this.piv, info);
/*  31: 87 */     if (info.val > 0) {
/*  32: 88 */       this.singular = true;
/*  33: 89 */     } else if (info.val < 0) {
/*  34: 90 */       throw new IllegalArgumentException();
/*  35:    */     }
/*  36: 92 */     this.LU.set(A);
/*  37:    */     
/*  38: 94 */     return this;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public PermutationMatrix getP()
/*  42:    */   {
/*  43:101 */     PermutationMatrix perm = PermutationMatrix.fromPartialPivots(this.piv);
/*  44:102 */     perm.transpose();
/*  45:103 */     return perm;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public UnitLowerTriangDenseMatrix getL()
/*  49:    */   {
/*  50:110 */     return new UnitLowerTriangDenseMatrix(getLU(), false);
/*  51:    */   }
/*  52:    */   
/*  53:    */   public UpperTriangDenseMatrix getU()
/*  54:    */   {
/*  55:117 */     return new UpperTriangDenseMatrix(getLU(), false);
/*  56:    */   }
/*  57:    */   
/*  58:    */   protected DenseMatrix getLU()
/*  59:    */   {
/*  60:124 */     return this.LU;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public double rcond(Matrix A, Matrix.Norm norm)
/*  64:    */   {
/*  65:139 */     if ((norm != Matrix.Norm.One) && (norm != Matrix.Norm.Infinity)) {
/*  66:140 */       throw new IllegalArgumentException("Only the 1 or the Infinity norms are supported");
/*  67:    */     }
/*  68:143 */     double anorm = A.norm(norm);
/*  69:    */     
/*  70:145 */     int n = A.numRows();
/*  71:    */     
/*  72:147 */     intW info = new intW(0);
/*  73:148 */     doubleW rcond = new doubleW(0.0D);
/*  74:149 */     LAPACK.getInstance().dgecon(norm.netlib(), n, this.LU.getData(), 
/*  75:150 */       Matrices.ld(n), anorm, rcond, new double[4 * n], new int[n], info);
/*  76:153 */     if (info.val < 0) {
/*  77:154 */       throw new IllegalArgumentException();
/*  78:    */     }
/*  79:156 */     return rcond.val;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public int[] getPivots()
/*  83:    */   {
/*  84:163 */     return this.piv;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public boolean isSingular()
/*  88:    */   {
/*  89:170 */     return this.singular;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public DenseMatrix solve(DenseMatrix B)
/*  93:    */     throws MatrixSingularException
/*  94:    */   {
/*  95:177 */     return solve(B, Transpose.NoTranspose);
/*  96:    */   }
/*  97:    */   
/*  98:    */   public DenseMatrix transSolve(DenseMatrix B)
/*  99:    */     throws MatrixSingularException
/* 100:    */   {
/* 101:184 */     return solve(B, Transpose.Transpose);
/* 102:    */   }
/* 103:    */   
/* 104:    */   private DenseMatrix solve(DenseMatrix B, Transpose trans)
/* 105:    */     throws MatrixSingularException
/* 106:    */   {
/* 107:189 */     if (this.singular) {
/* 108:190 */       throw new MatrixSingularException();
/* 109:    */     }
/* 110:191 */     if (B.numRows() != this.LU.numRows()) {
/* 111:192 */       throw new IllegalArgumentException("B.numRows() != LU.numRows()");
/* 112:    */     }
/* 113:194 */     intW info = new intW(0);
/* 114:195 */     LAPACK.getInstance().dgetrs(trans.netlib(), this.LU.numRows(), B
/* 115:196 */       .numColumns(), this.LU.getData(), Matrices.ld(this.LU.numRows()), this.piv, B
/* 116:197 */       .getData(), Matrices.ld(this.LU.numRows()), info);
/* 117:199 */     if (info.val < 0) {
/* 118:200 */       throw new IllegalArgumentException();
/* 119:    */     }
/* 120:202 */     return B;
/* 121:    */   }
/* 122:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.DenseLU
 * JD-Core Version:    0.7.0.1
 */