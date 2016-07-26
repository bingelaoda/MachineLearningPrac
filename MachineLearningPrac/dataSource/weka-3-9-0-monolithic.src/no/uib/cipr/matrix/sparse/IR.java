/*  1:   */ package no.uib.cipr.matrix.sparse;
/*  2:   */ 
/*  3:   */ import no.uib.cipr.matrix.Matrix;
/*  4:   */ import no.uib.cipr.matrix.Vector;
/*  5:   */ 
/*  6:   */ public class IR
/*  7:   */   extends AbstractIterativeSolver
/*  8:   */ {
/*  9:   */   private Vector z;
/* 10:   */   private Vector r;
/* 11:   */   
/* 12:   */   public IR(Vector template)
/* 13:   */   {
/* 14:53 */     this.z = template.copy();
/* 15:54 */     this.r = template.copy();
/* 16:   */   }
/* 17:   */   
/* 18:   */   public Vector solve(Matrix A, Vector b, Vector x)
/* 19:   */     throws IterativeSolverNotConvergedException
/* 20:   */   {
/* 21:59 */     checkSizes(A, b, x);
/* 22:   */     
/* 23:61 */     A.multAdd(-1.0D, x, this.r.set(b));
/* 24:63 */     for (this.iter.setFirst(); !this.iter.converged(this.r, x); this.iter.next())
/* 25:   */     {
/* 26:64 */       this.M.apply(this.r, this.z);
/* 27:65 */       x.add(this.z);
/* 28:66 */       A.multAdd(-1.0D, x, this.r.set(b));
/* 29:   */     }
/* 30:69 */     return x;
/* 31:   */   }
/* 32:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.IR
 * JD-Core Version:    0.7.0.1
 */