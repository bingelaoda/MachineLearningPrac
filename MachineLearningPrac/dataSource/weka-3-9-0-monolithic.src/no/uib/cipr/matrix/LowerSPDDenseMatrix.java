/*  1:   */ package no.uib.cipr.matrix;
/*  2:   */ 
/*  3:   */ public class LowerSPDDenseMatrix
/*  4:   */   extends LowerSymmDenseMatrix
/*  5:   */ {
/*  6:   */   public LowerSPDDenseMatrix(int n)
/*  7:   */   {
/*  8:39 */     super(n);
/*  9:   */   }
/* 10:   */   
/* 11:   */   public LowerSPDDenseMatrix(Matrix A)
/* 12:   */   {
/* 13:50 */     this(A, true);
/* 14:   */   }
/* 15:   */   
/* 16:   */   public LowerSPDDenseMatrix(Matrix A, boolean deep)
/* 17:   */   {
/* 18:64 */     super(A, deep);
/* 19:   */   }
/* 20:   */   
/* 21:   */   public LowerSPDDenseMatrix copy()
/* 22:   */   {
/* 23:69 */     return new LowerSPDDenseMatrix(this);
/* 24:   */   }
/* 25:   */   
/* 26:   */   public Matrix solve(Matrix B, Matrix X)
/* 27:   */   {
/* 28:74 */     return SPDsolve(B, X);
/* 29:   */   }
/* 30:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.LowerSPDDenseMatrix
 * JD-Core Version:    0.7.0.1
 */