/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ abstract class OrthogonalComputer
/*   4:    */ {
/*   5:    */   final DenseMatrix Q;
/*   6:    */   final LowerTriangDenseMatrix L;
/*   7:    */   final UpperTriangDenseMatrix R;
/*   8:    */   final int m;
/*   9:    */   final int n;
/*  10:    */   final int k;
/*  11:    */   double[] work;
/*  12:    */   double[] workGen;
/*  13:    */   final double[] tau;
/*  14:    */   
/*  15:    */   OrthogonalComputer(int m, int n, boolean upper)
/*  16:    */   {
/*  17: 70 */     this.m = m;
/*  18: 71 */     this.n = n;
/*  19: 72 */     this.k = Math.min(m, n);
/*  20:    */     
/*  21: 74 */     this.tau = new double[this.k];
/*  22:    */     
/*  23: 76 */     this.Q = new DenseMatrix(m, n);
/*  24: 77 */     if (upper)
/*  25:    */     {
/*  26: 78 */       this.R = new UpperTriangDenseMatrix(Math.min(m, n));
/*  27: 79 */       this.L = null;
/*  28:    */     }
/*  29:    */     else
/*  30:    */     {
/*  31: 81 */       this.L = new LowerTriangDenseMatrix(Math.min(m, n));
/*  32: 82 */       this.R = null;
/*  33:    */     }
/*  34:    */   }
/*  35:    */   
/*  36:    */   public abstract OrthogonalComputer factor(DenseMatrix paramDenseMatrix);
/*  37:    */   
/*  38:    */   public DenseMatrix getQ()
/*  39:    */   {
/*  40:100 */     return this.Q;
/*  41:    */   }
/*  42:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.OrthogonalComputer
 * JD-Core Version:    0.7.0.1
 */