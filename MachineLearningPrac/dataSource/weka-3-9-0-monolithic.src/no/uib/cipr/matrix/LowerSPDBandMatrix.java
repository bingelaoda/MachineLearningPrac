/*  1:   */ package no.uib.cipr.matrix;
/*  2:   */ 
/*  3:   */ public class LowerSPDBandMatrix
/*  4:   */   extends LowerSymmBandMatrix
/*  5:   */ {
/*  6:   */   public LowerSPDBandMatrix(int n, int kd)
/*  7:   */   {
/*  8:40 */     super(n, kd);
/*  9:   */   }
/* 10:   */   
/* 11:   */   public LowerSPDBandMatrix(Matrix A, int kd)
/* 12:   */   {
/* 13:54 */     super(A, kd);
/* 14:   */   }
/* 15:   */   
/* 16:   */   public LowerSPDBandMatrix(Matrix A, int kd, boolean deep)
/* 17:   */   {
/* 18:71 */     super(A, kd, deep);
/* 19:   */   }
/* 20:   */   
/* 21:   */   public LowerSPDBandMatrix copy()
/* 22:   */   {
/* 23:76 */     return new LowerSPDBandMatrix(this, this.kd);
/* 24:   */   }
/* 25:   */   
/* 26:   */   public Matrix solve(Matrix B, Matrix X)
/* 27:   */   {
/* 28:81 */     return SPDsolve(B, X);
/* 29:   */   }
/* 30:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.LowerSPDBandMatrix
 * JD-Core Version:    0.7.0.1
 */