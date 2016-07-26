/*  1:   */ package no.uib.cipr.matrix;
/*  2:   */ 
/*  3:   */ abstract class SymmEVD
/*  4:   */ {
/*  5:   */   final int n;
/*  6:   */   final double[] w;
/*  7:   */   final DenseMatrix Z;
/*  8:   */   final JobEig job;
/*  9:   */   
/* 10:   */   public SymmEVD(int n, boolean vectors)
/* 11:   */   {
/* 12:58 */     this.n = n;
/* 13:59 */     this.w = new double[n];
/* 14:60 */     this.job = (vectors ? JobEig.All : JobEig.Eigenvalues);
/* 15:62 */     if (vectors) {
/* 16:63 */       this.Z = new DenseMatrix(n, n);
/* 17:   */     } else {
/* 18:65 */       this.Z = null;
/* 19:   */     }
/* 20:   */   }
/* 21:   */   
/* 22:   */   public SymmEVD(int n)
/* 23:   */   {
/* 24:75 */     this(n, true);
/* 25:   */   }
/* 26:   */   
/* 27:   */   public double[] getEigenvalues()
/* 28:   */   {
/* 29:82 */     return this.w;
/* 30:   */   }
/* 31:   */   
/* 32:   */   public DenseMatrix getEigenvectors()
/* 33:   */   {
/* 34:89 */     return this.Z;
/* 35:   */   }
/* 36:   */   
/* 37:   */   public boolean hasEigenvectors()
/* 38:   */   {
/* 39:96 */     return this.Z != null;
/* 40:   */   }
/* 41:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.SymmEVD
 * JD-Core Version:    0.7.0.1
 */