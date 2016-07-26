/*   1:    */ package no.uib.cipr.matrix.sparse;
/*   2:    */ 
/*   3:    */ import no.uib.cipr.matrix.Matrix;
/*   4:    */ import no.uib.cipr.matrix.NotConvergedException.Reason;
/*   5:    */ import no.uib.cipr.matrix.Vector;
/*   6:    */ 
/*   7:    */ public class BiCG
/*   8:    */   extends AbstractIterativeSolver
/*   9:    */ {
/*  10:    */   private Vector z;
/*  11:    */   private Vector p;
/*  12:    */   private Vector q;
/*  13:    */   private Vector r;
/*  14:    */   private Vector ztilde;
/*  15:    */   private Vector ptilde;
/*  16:    */   private Vector qtilde;
/*  17:    */   private Vector rtilde;
/*  18:    */   
/*  19:    */   public BiCG(Vector template)
/*  20:    */   {
/*  21: 53 */     this.z = template.copy();
/*  22: 54 */     this.p = template.copy();
/*  23: 55 */     this.q = template.copy();
/*  24: 56 */     this.r = template.copy();
/*  25: 57 */     this.ztilde = template.copy();
/*  26: 58 */     this.ptilde = template.copy();
/*  27: 59 */     this.qtilde = template.copy();
/*  28: 60 */     this.rtilde = template.copy();
/*  29:    */   }
/*  30:    */   
/*  31:    */   public Vector solve(Matrix A, Vector b, Vector x)
/*  32:    */     throws IterativeSolverNotConvergedException
/*  33:    */   {
/*  34: 65 */     checkSizes(A, b, x);
/*  35:    */     
/*  36: 67 */     double rho_1 = 1.0D;double rho_2 = 1.0D;double alpha = 1.0D;double beta = 1.0D;
/*  37:    */     
/*  38: 69 */     A.multAdd(-1.0D, x, this.r.set(b));
/*  39: 70 */     this.rtilde.set(this.r);
/*  40: 72 */     for (this.iter.setFirst(); !this.iter.converged(this.r, x); this.iter.next())
/*  41:    */     {
/*  42: 73 */       this.M.apply(this.r, this.z);
/*  43: 74 */       this.M.transApply(this.rtilde, this.ztilde);
/*  44: 75 */       rho_1 = this.z.dot(this.rtilde);
/*  45: 77 */       if (rho_1 == 0.0D) {
/*  46: 78 */         throw new IterativeSolverNotConvergedException(NotConvergedException.Reason.Breakdown, "rho", this.iter);
/*  47:    */       }
/*  48: 81 */       if (this.iter.isFirst())
/*  49:    */       {
/*  50: 82 */         this.p.set(this.z);
/*  51: 83 */         this.ptilde.set(this.ztilde);
/*  52:    */       }
/*  53:    */       else
/*  54:    */       {
/*  55: 85 */         beta = rho_1 / rho_2;
/*  56: 86 */         this.p.scale(beta).add(this.z);
/*  57: 87 */         this.ptilde.scale(beta).add(this.ztilde);
/*  58:    */       }
/*  59: 90 */       A.mult(this.p, this.q);
/*  60: 91 */       A.transMult(this.ptilde, this.qtilde);
/*  61:    */       
/*  62: 93 */       alpha = rho_1 / this.ptilde.dot(this.q);
/*  63: 94 */       x.add(alpha, this.p);
/*  64: 95 */       this.r.add(-alpha, this.q);
/*  65: 96 */       this.rtilde.add(-alpha, this.qtilde);
/*  66:    */       
/*  67: 98 */       rho_2 = rho_1;
/*  68:    */     }
/*  69:101 */     return x;
/*  70:    */   }
/*  71:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.BiCG
 * JD-Core Version:    0.7.0.1
 */