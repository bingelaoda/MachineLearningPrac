/*  1:   */ package no.uib.cipr.matrix;
/*  2:   */ 
/*  3:   */ public class UpperSPDPackMatrix
/*  4:   */   extends UpperSymmPackMatrix
/*  5:   */ {
/*  6:   */   public UpperSPDPackMatrix(int n)
/*  7:   */   {
/*  8:39 */     super(n);
/*  9:   */   }
/* 10:   */   
/* 11:   */   public UpperSPDPackMatrix(Matrix A)
/* 12:   */   {
/* 13:50 */     super(A);
/* 14:   */   }
/* 15:   */   
/* 16:   */   public UpperSPDPackMatrix(Matrix A, boolean deep)
/* 17:   */   {
/* 18:64 */     super(A, deep);
/* 19:   */   }
/* 20:   */   
/* 21:   */   public UpperSPDPackMatrix copy()
/* 22:   */   {
/* 23:69 */     return new UpperSPDPackMatrix(this);
/* 24:   */   }
/* 25:   */   
/* 26:   */   public Matrix solve(Matrix B, Matrix X)
/* 27:   */   {
/* 28:74 */     return SPDsolve(B, X);
/* 29:   */   }
/* 30:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.UpperSPDPackMatrix
 * JD-Core Version:    0.7.0.1
 */