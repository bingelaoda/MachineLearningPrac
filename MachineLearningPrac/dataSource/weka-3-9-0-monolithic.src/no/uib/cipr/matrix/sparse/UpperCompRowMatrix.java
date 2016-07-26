/*  1:   */ package no.uib.cipr.matrix.sparse;
/*  2:   */ 
/*  3:   */ import no.uib.cipr.matrix.AbstractMatrix;
/*  4:   */ import no.uib.cipr.matrix.DenseVector;
/*  5:   */ import no.uib.cipr.matrix.Vector;
/*  6:   */ 
/*  7:   */ class UpperCompRowMatrix
/*  8:   */   extends AbstractMatrix
/*  9:   */ {
/* 10:   */   private int[] rowptr;
/* 11:   */   private int[] colind;
/* 12:   */   private double[] data;
/* 13:   */   private int[] diagind;
/* 14:   */   
/* 15:   */   public UpperCompRowMatrix(CompRowMatrix LU, int[] diagind)
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
/* 31:56 */     for (int i = this.numRows - 1; i >= 0; i--)
/* 32:   */     {
/* 33:59 */       double sum = 0.0D;
/* 34:60 */       for (int j = this.diagind[i] + 1; j < this.rowptr[(i + 1)]; j++) {
/* 35:61 */         sum += this.data[j] * xd[this.colind[j]];
/* 36:   */       }
/* 37:63 */       xd[i] = ((bd[i] - sum) / this.data[this.diagind[i]]);
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
/* 50:78 */     for (int i = 0; i < this.numRows; i++)
/* 51:   */     {
/* 52:81 */       xd[i] /= this.data[this.diagind[i]];
/* 53:85 */       for (int j = this.diagind[i] + 1; j < this.rowptr[(i + 1)]; j++) {
/* 54:86 */         xd[this.colind[j]] -= this.data[j] * xd[i];
/* 55:   */       }
/* 56:   */     }
/* 57:89 */     return x;
/* 58:   */   }
/* 59:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.UpperCompRowMatrix
 * JD-Core Version:    0.7.0.1
 */