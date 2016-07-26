/*   1:    */ package weka.core.matrix;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import weka.core.RevisionHandler;
/*   5:    */ import weka.core.RevisionUtils;
/*   6:    */ 
/*   7:    */ public class CholeskyDecomposition
/*   8:    */   implements Serializable, RevisionHandler
/*   9:    */ {
/*  10:    */   private static final long serialVersionUID = -8739775942782694701L;
/*  11:    */   private double[][] L;
/*  12:    */   private int n;
/*  13:    */   private boolean isspd;
/*  14:    */   
/*  15:    */   public CholeskyDecomposition(Matrix Arg)
/*  16:    */   {
/*  17: 69 */     double[][] A = Arg.getArray();
/*  18: 70 */     this.n = Arg.getRowDimension();
/*  19: 71 */     this.L = new double[this.n][this.n];
/*  20: 72 */     this.isspd = (Arg.getColumnDimension() == this.n);
/*  21: 74 */     for (int j = 0; j < this.n; j++)
/*  22:    */     {
/*  23: 75 */       double[] Lrowj = this.L[j];
/*  24: 76 */       double d = 0.0D;
/*  25: 77 */       for (int k = 0; k < j; k++)
/*  26:    */       {
/*  27: 78 */         double[] Lrowk = this.L[k];
/*  28: 79 */         double s = 0.0D;
/*  29: 80 */         for (int i = 0; i < k; i++) {
/*  30: 81 */           s += Lrowk[i] * Lrowj[i];
/*  31:    */         }
/*  32: 83 */         double tmp151_150 = ((A[j][k] - s) / this.L[k][k]);s = tmp151_150;Lrowj[k] = tmp151_150;
/*  33: 84 */         d += s * s;
/*  34: 85 */         this.isspd &= A[k][j] == A[j][k];
/*  35:    */       }
/*  36: 87 */       d = A[j][j] - d;
/*  37: 88 */       this.isspd &= d > 0.0D;
/*  38: 89 */       this.L[j][j] = Math.sqrt(Math.max(d, 0.0D));
/*  39: 90 */       for (int k = j + 1; k < this.n; k++) {
/*  40: 91 */         this.L[j][k] = 0.0D;
/*  41:    */       }
/*  42:    */     }
/*  43:    */   }
/*  44:    */   
/*  45:    */   public boolean isSPD()
/*  46:    */   {
/*  47:101 */     return this.isspd;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public Matrix getL()
/*  51:    */   {
/*  52:109 */     return new Matrix(this.L, this.n, this.n);
/*  53:    */   }
/*  54:    */   
/*  55:    */   public Matrix solve(Matrix B)
/*  56:    */   {
/*  57:120 */     if (B.getRowDimension() != this.n) {
/*  58:121 */       throw new IllegalArgumentException("Matrix row dimensions must agree.");
/*  59:    */     }
/*  60:123 */     if (!this.isspd) {
/*  61:124 */       throw new RuntimeException("Matrix is not symmetric positive definite.");
/*  62:    */     }
/*  63:128 */     double[][] X = B.getArrayCopy();
/*  64:129 */     int nx = B.getColumnDimension();
/*  65:132 */     for (int k = 0; k < this.n; k++) {
/*  66:133 */       for (int j = 0; j < nx; j++)
/*  67:    */       {
/*  68:134 */         for (int i = 0; i < k; i++) {
/*  69:135 */           X[k][j] -= X[i][j] * this.L[k][i];
/*  70:    */         }
/*  71:137 */         X[k][j] /= this.L[k][k];
/*  72:    */       }
/*  73:    */     }
/*  74:142 */     for (int k = this.n - 1; k >= 0; k--) {
/*  75:143 */       for (int j = 0; j < nx; j++)
/*  76:    */       {
/*  77:144 */         for (int i = k + 1; i < this.n; i++) {
/*  78:145 */           X[k][j] -= X[i][j] * this.L[i][k];
/*  79:    */         }
/*  80:147 */         X[k][j] /= this.L[k][k];
/*  81:    */       }
/*  82:    */     }
/*  83:151 */     return new Matrix(X, this.n, nx);
/*  84:    */   }
/*  85:    */   
/*  86:    */   public String getRevision()
/*  87:    */   {
/*  88:160 */     return RevisionUtils.extract("$Revision: 5953 $");
/*  89:    */   }
/*  90:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.matrix.CholeskyDecomposition
 * JD-Core Version:    0.7.0.1
 */