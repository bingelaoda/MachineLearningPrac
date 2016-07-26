/*   1:    */ package no.uib.cipr.matrix.sparse;
/*   2:    */ 
/*   3:    */ import no.uib.cipr.matrix.Matrix;
/*   4:    */ import no.uib.cipr.matrix.NotConvergedException.Reason;
/*   5:    */ import no.uib.cipr.matrix.Vector;
/*   6:    */ 
/*   7:    */ public class CGS
/*   8:    */   extends AbstractIterativeSolver
/*   9:    */ {
/*  10:    */   private Vector p;
/*  11:    */   private Vector q;
/*  12:    */   private Vector u;
/*  13:    */   private Vector phat;
/*  14:    */   private Vector qhat;
/*  15:    */   private Vector vhat;
/*  16:    */   private Vector uhat;
/*  17:    */   private Vector sum;
/*  18:    */   private Vector r;
/*  19:    */   private Vector rtilde;
/*  20:    */   
/*  21:    */   public CGS(Vector template)
/*  22:    */   {
/*  23: 53 */     this.p = template.copy();
/*  24: 54 */     this.q = template.copy();
/*  25: 55 */     this.u = template.copy();
/*  26: 56 */     this.phat = template.copy();
/*  27: 57 */     this.qhat = template.copy();
/*  28: 58 */     this.vhat = template.copy();
/*  29: 59 */     this.uhat = template.copy();
/*  30: 60 */     this.sum = template.copy();
/*  31: 61 */     this.r = template.copy();
/*  32: 62 */     this.rtilde = template.copy();
/*  33:    */   }
/*  34:    */   
/*  35:    */   public Vector solve(Matrix A, Vector b, Vector x)
/*  36:    */     throws IterativeSolverNotConvergedException
/*  37:    */   {
/*  38: 67 */     checkSizes(A, b, x);
/*  39:    */     
/*  40: 69 */     double rho_1 = 0.0D;double rho_2 = 0.0D;double alpha = 0.0D;double beta = 0.0D;
/*  41:    */     
/*  42: 71 */     A.multAdd(-1.0D, x, this.r.set(b));
/*  43: 72 */     this.rtilde.set(this.r);
/*  44: 74 */     for (this.iter.setFirst(); !this.iter.converged(this.r, x); this.iter.next())
/*  45:    */     {
/*  46: 75 */       rho_1 = this.rtilde.dot(this.r);
/*  47: 77 */       if (rho_1 == 0.0D) {
/*  48: 78 */         throw new IterativeSolverNotConvergedException(NotConvergedException.Reason.Breakdown, "rho", this.iter);
/*  49:    */       }
/*  50: 81 */       if (this.iter.isFirst())
/*  51:    */       {
/*  52: 82 */         this.u.set(this.r);
/*  53: 83 */         this.p.set(this.u);
/*  54:    */       }
/*  55:    */       else
/*  56:    */       {
/*  57: 85 */         beta = rho_1 / rho_2;
/*  58: 86 */         this.u.set(this.r).add(beta, this.q);
/*  59: 87 */         this.sum.set(this.q).add(beta, this.p);
/*  60: 88 */         this.p.set(this.u).add(beta, this.sum);
/*  61:    */       }
/*  62: 91 */       this.M.apply(this.p, this.phat);
/*  63: 92 */       A.mult(this.phat, this.vhat);
/*  64: 93 */       alpha = rho_1 / this.rtilde.dot(this.vhat);
/*  65: 94 */       this.q.set(-alpha, this.vhat).add(this.u);
/*  66:    */       
/*  67: 96 */       this.M.apply(this.sum.set(this.u).add(this.q), this.uhat);
/*  68: 97 */       x.add(alpha, this.uhat);
/*  69: 98 */       A.mult(this.uhat, this.qhat);
/*  70: 99 */       this.r.add(-alpha, this.qhat);
/*  71:    */       
/*  72:101 */       rho_2 = rho_1;
/*  73:    */     }
/*  74:104 */     return x;
/*  75:    */   }
/*  76:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.CGS
 * JD-Core Version:    0.7.0.1
 */