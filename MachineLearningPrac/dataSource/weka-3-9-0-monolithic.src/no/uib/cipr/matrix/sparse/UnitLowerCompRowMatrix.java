/*  1:   */ package no.uib.cipr.matrix.sparse;
/*  2:   */ 
/*  3:   */ import no.uib.cipr.matrix.AbstractMatrix;
/*  4:   */ import no.uib.cipr.matrix.DenseVector;
/*  5:   */ import no.uib.cipr.matrix.Vector;
/*  6:   */ 
/*  7:   */ class UnitLowerCompRowMatrix
/*  8:   */   extends AbstractMatrix
/*  9:   */ {
/* 10:   */   private int[] rowptr;
/* 11:   */   private int[] colind;
/* 12:   */   private double[] data;
/* 13:   */   private int[] diagind;
/* 14:   */   
/* 15:   */   public UnitLowerCompRowMatrix(CompRowMatrix LU, int[] diagind)
/* 16:   */   {
/* 17:41 */     super(LU);
/* 18:42 */     this.rowptr = LU.getRowPointers();
/* 19:43 */     this.colind = LU.getColumnIndices();
/* 20:44 */     this.data = LU.getData();
/* 21:45 */     this.diagind = diagind;
/* 22:   */   }
/* 23:   */   
/* 24:   */   public Vector solve(Vector b, Vector x)
/* 25:   */   {
/* 26:50 */     if ((!(b instanceof DenseVector)) || (!(x instanceof DenseVector))) {
/* 27:51 */       return super.solve(b, x);
/* 28:   */     }
/* 29:53 */     double[] bd = ((DenseVector)b).getData();
/* 30:54 */     double[] xd = ((DenseVector)x).getData();
/* 31:56 */     for (int i = 0; i < this.numRows; i++)
/* 32:   */     {
/* 33:59 */       double sum = 0.0D;
/* 34:60 */       for (int j = this.rowptr[i]; j < this.diagind[i]; j++) {
/* 35:61 */         sum += this.data[j] * xd[this.colind[j]];
/* 36:   */       }
/* 37:63 */       bd[i] -= sum;
/* 38:   */     }
/* 39:66 */     return x;
/* 40:   */   }
/* 41:   */   
/* 42:   */   public Vector transSolve(Vector b, Vector x)
/* 43:   */   {
/* 44:71 */     if (!(x instanceof DenseVector)) {
/* 45:72 */       return super.transSolve(b, x);
/* 46:   */     }
/* 47:74 */     x.set(b);
/* 48:   */     
/* 49:76 */     double[] xd = ((DenseVector)x).getData();
/* 50:78 */     for (int i = this.numRows - 1; i >= 0; i--) {
/* 51:82 */       for (int j = this.rowptr[i]; j < this.diagind[i]; j++) {
/* 52:83 */         xd[this.colind[j]] -= this.data[j] * xd[i];
/* 53:   */       }
/* 54:   */     }
/* 55:85 */     return x;
/* 56:   */   }
/* 57:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.UnitLowerCompRowMatrix
 * JD-Core Version:    0.7.0.1
 */