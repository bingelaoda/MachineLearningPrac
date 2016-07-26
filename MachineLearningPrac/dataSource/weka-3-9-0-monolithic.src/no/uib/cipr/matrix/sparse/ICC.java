/*   1:    */ package no.uib.cipr.matrix.sparse;
/*   2:    */ 
/*   3:    */ import no.uib.cipr.matrix.DenseVector;
/*   4:    */ import no.uib.cipr.matrix.Matrix;
/*   5:    */ import no.uib.cipr.matrix.Vector;
/*   6:    */ 
/*   7:    */ public class ICC
/*   8:    */   implements Preconditioner
/*   9:    */ {
/*  10:    */   private final CompRowMatrix R;
/*  11:    */   private Matrix Rt;
/*  12:    */   private final Vector y;
/*  13:    */   
/*  14:    */   public ICC(CompRowMatrix R)
/*  15:    */   {
/*  16: 58 */     if (!R.isSquare()) {
/*  17: 59 */       throw new IllegalArgumentException("ICC only applies to square matrices");
/*  18:    */     }
/*  19: 62 */     this.R = R;
/*  20: 63 */     int n = R.numRows();
/*  21: 64 */     this.y = new DenseVector(n);
/*  22:    */   }
/*  23:    */   
/*  24:    */   public Vector apply(Vector b, Vector x)
/*  25:    */   {
/*  26: 69 */     this.Rt.transSolve(b, this.y);
/*  27:    */     
/*  28:    */ 
/*  29: 72 */     return this.Rt.solve(this.y, x);
/*  30:    */   }
/*  31:    */   
/*  32:    */   public Vector transApply(Vector b, Vector x)
/*  33:    */   {
/*  34: 76 */     return apply(b, x);
/*  35:    */   }
/*  36:    */   
/*  37:    */   public void setMatrix(Matrix A)
/*  38:    */   {
/*  39: 80 */     this.R.set(A);
/*  40:    */     
/*  41: 82 */     factor();
/*  42:    */   }
/*  43:    */   
/*  44:    */   private void factor()
/*  45:    */   {
/*  46: 86 */     int n = this.R.numRows();
/*  47:    */     
/*  48:    */ 
/*  49: 89 */     int[] colind = this.R.getColumnIndices();
/*  50: 90 */     int[] rowptr = this.R.getRowPointers();
/*  51: 91 */     double[] data = this.R.getData();
/*  52:    */     
/*  53:    */ 
/*  54: 94 */     double[] Rk = new double[n];
/*  55:    */     
/*  56:    */ 
/*  57: 97 */     int[] diagind = findDiagonalIndices(n, colind, rowptr);
/*  58:100 */     for (int k = 0; k < n; k++)
/*  59:    */     {
/*  60:103 */       java.util.Arrays.fill(Rk, 0.0D);
/*  61:104 */       for (int i = rowptr[k]; i < rowptr[(k + 1)]; i++) {
/*  62:105 */         Rk[colind[i]] = data[i];
/*  63:    */       }
/*  64:107 */       for (int i = 0; i < k; i++)
/*  65:    */       {
/*  66:110 */         double Rii = data[diagind[i]];
/*  67:112 */         if (Rii == 0.0D) {
/*  68:113 */           throw new RuntimeException("Zero pivot encountered on row " + (i + 1) + " during ICC process");
/*  69:    */         }
/*  70:117 */         double Rki = Rk[i] / Rii;
/*  71:119 */         if (Rki != 0.0D) {
/*  72:123 */           for (int j = diagind[i] + 1; j < rowptr[(i + 1)]; j++) {
/*  73:124 */             Rk[colind[j]] -= Rki * data[j];
/*  74:    */           }
/*  75:    */         }
/*  76:    */       }
/*  77:128 */       if (Rk[k] == 0.0D) {
/*  78:129 */         throw new RuntimeException("Zero diagonal entry encountered on row " + (k + 1) + " during ICC process");
/*  79:    */       }
/*  80:132 */       double sqRkk = Math.sqrt(Rk[k]);
/*  81:134 */       for (int i = diagind[k]; i < rowptr[(k + 1)]; i++) {
/*  82:135 */         data[i] = (Rk[colind[i]] / sqRkk);
/*  83:    */       }
/*  84:    */     }
/*  85:138 */     this.Rt = new UpperCompRowMatrix(this.R, diagind);
/*  86:    */   }
/*  87:    */   
/*  88:    */   private int[] findDiagonalIndices(int m, int[] colind, int[] rowptr)
/*  89:    */   {
/*  90:142 */     int[] diagind = new int[m];
/*  91:144 */     for (int k = 0; k < m; k++)
/*  92:    */     {
/*  93:145 */       diagind[k] = Arrays.binarySearch(colind, k, rowptr[k], rowptr[(k + 1)]);
/*  94:148 */       if (diagind[k] < 0) {
/*  95:149 */         throw new RuntimeException("Missing diagonal entry on row " + (k + 1));
/*  96:    */       }
/*  97:    */     }
/*  98:153 */     return diagind;
/*  99:    */   }
/* 100:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.ICC
 * JD-Core Version:    0.7.0.1
 */