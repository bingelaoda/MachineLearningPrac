/*  1:   */ package no.uib.cipr.matrix.sparse;
/*  2:   */ 
/*  3:   */ import no.uib.cipr.matrix.Matrix;
/*  4:   */ import no.uib.cipr.matrix.Vector;
/*  5:   */ 
/*  6:   */ public class CG
/*  7:   */   extends AbstractIterativeSolver
/*  8:   */ {
/*  9:   */   private Vector p;
/* 10:   */   private Vector z;
/* 11:   */   private Vector q;
/* 12:   */   private Vector r;
/* 13:   */   
/* 14:   */   public CG(Vector template)
/* 15:   */   {
/* 16:52 */     this.p = template.copy();
/* 17:53 */     this.z = template.copy();
/* 18:54 */     this.q = template.copy();
/* 19:55 */     this.r = template.copy();
/* 20:   */   }
/* 21:   */   
/* 22:   */   public Vector solve(Matrix A, Vector b, Vector x)
/* 23:   */     throws IterativeSolverNotConvergedException
/* 24:   */   {
/* 25:60 */     checkSizes(A, b, x);
/* 26:   */     
/* 27:62 */     double alpha = 0.0D;double beta = 0.0D;double rho = 0.0D;double rho_1 = 0.0D;
/* 28:   */     
/* 29:64 */     A.multAdd(-1.0D, x, this.r.set(b));
/* 30:66 */     for (this.iter.setFirst(); !this.iter.converged(this.r, x); this.iter.next())
/* 31:   */     {
/* 32:67 */       this.M.apply(this.r, this.z);
/* 33:68 */       rho = this.r.dot(this.z);
/* 34:70 */       if (this.iter.isFirst())
/* 35:   */       {
/* 36:71 */         this.p.set(this.z);
/* 37:   */       }
/* 38:   */       else
/* 39:   */       {
/* 40:73 */         beta = rho / rho_1;
/* 41:74 */         this.p.scale(beta).add(this.z);
/* 42:   */       }
/* 43:77 */       A.mult(this.p, this.q);
/* 44:78 */       alpha = rho / this.p.dot(this.q);
/* 45:   */       
/* 46:80 */       x.add(alpha, this.p);
/* 47:81 */       this.r.add(-alpha, this.q);
/* 48:   */       
/* 49:83 */       rho_1 = rho;
/* 50:   */     }
/* 51:86 */     return x;
/* 52:   */   }
/* 53:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.CG
 * JD-Core Version:    0.7.0.1
 */