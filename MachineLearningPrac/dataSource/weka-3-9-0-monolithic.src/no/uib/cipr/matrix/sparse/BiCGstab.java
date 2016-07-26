/*   1:    */ package no.uib.cipr.matrix.sparse;
/*   2:    */ 
/*   3:    */ import no.uib.cipr.matrix.Matrix;
/*   4:    */ import no.uib.cipr.matrix.NotConvergedException.Reason;
/*   5:    */ import no.uib.cipr.matrix.Vector;
/*   6:    */ 
/*   7:    */ public class BiCGstab
/*   8:    */   extends AbstractIterativeSolver
/*   9:    */ {
/*  10:    */   private Vector p;
/*  11:    */   private Vector s;
/*  12:    */   private Vector phat;
/*  13:    */   private Vector shat;
/*  14:    */   private Vector t;
/*  15:    */   private Vector v;
/*  16:    */   private Vector temp;
/*  17:    */   private Vector r;
/*  18:    */   private Vector rtilde;
/*  19:    */   
/*  20:    */   public BiCGstab(Vector template)
/*  21:    */   {
/*  22: 54 */     this.p = template.copy();
/*  23: 55 */     this.s = template.copy();
/*  24: 56 */     this.phat = template.copy();
/*  25: 57 */     this.shat = template.copy();
/*  26: 58 */     this.t = template.copy();
/*  27: 59 */     this.v = template.copy();
/*  28: 60 */     this.temp = template.copy();
/*  29: 61 */     this.r = template.copy();
/*  30: 62 */     this.rtilde = template.copy();
/*  31:    */   }
/*  32:    */   
/*  33:    */   public Vector solve(Matrix A, Vector b, Vector x)
/*  34:    */     throws IterativeSolverNotConvergedException
/*  35:    */   {
/*  36: 67 */     checkSizes(A, b, x);
/*  37:    */     
/*  38: 69 */     double rho_1 = 1.0D;double rho_2 = 1.0D;double alpha = 1.0D;double beta = 1.0D;double omega = 1.0D;
/*  39:    */     
/*  40: 71 */     A.multAdd(-1.0D, x, this.r.set(b));
/*  41: 72 */     this.rtilde.set(this.r);
/*  42: 74 */     for (this.iter.setFirst(); !this.iter.converged(this.r, x); this.iter.next())
/*  43:    */     {
/*  44: 75 */       rho_1 = this.rtilde.dot(this.r);
/*  45: 77 */       if (rho_1 == 0.0D) {
/*  46: 78 */         throw new IterativeSolverNotConvergedException(NotConvergedException.Reason.Breakdown, "rho", this.iter);
/*  47:    */       }
/*  48: 81 */       if (omega == 0.0D) {
/*  49: 82 */         throw new IterativeSolverNotConvergedException(NotConvergedException.Reason.Breakdown, "omega", this.iter);
/*  50:    */       }
/*  51: 85 */       if (this.iter.isFirst())
/*  52:    */       {
/*  53: 86 */         this.p.set(this.r);
/*  54:    */       }
/*  55:    */       else
/*  56:    */       {
/*  57: 88 */         beta = rho_1 / rho_2 * (alpha / omega);
/*  58:    */         
/*  59:    */ 
/*  60: 91 */         this.temp.set(-omega, this.v).add(this.p);
/*  61:    */         
/*  62:    */ 
/*  63: 94 */         this.p.set(this.r).add(beta, this.temp);
/*  64:    */       }
/*  65: 97 */       this.M.apply(this.p, this.phat);
/*  66: 98 */       A.mult(this.phat, this.v);
/*  67: 99 */       alpha = rho_1 / this.rtilde.dot(this.v);
/*  68:100 */       this.s.set(this.r).add(-alpha, this.v);
/*  69:    */       
/*  70:102 */       x.add(alpha, this.phat);
/*  71:103 */       if (this.iter.converged(this.s, x)) {
/*  72:104 */         return x;
/*  73:    */       }
/*  74:106 */       this.M.apply(this.s, this.shat);
/*  75:107 */       A.mult(this.shat, this.t);
/*  76:108 */       omega = this.t.dot(this.s) / this.t.dot(this.t);
/*  77:109 */       x.add(omega, this.shat);
/*  78:110 */       this.r.set(this.s).add(-omega, this.t);
/*  79:    */       
/*  80:112 */       rho_2 = rho_1;
/*  81:    */     }
/*  82:115 */     return x;
/*  83:    */   }
/*  84:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.BiCGstab
 * JD-Core Version:    0.7.0.1
 */