/*   1:    */ package no.uib.cipr.matrix.sparse;
/*   2:    */ 
/*   3:    */ import no.uib.cipr.matrix.NotConvergedException.Reason;
/*   4:    */ import no.uib.cipr.matrix.Vector;
/*   5:    */ 
/*   6:    */ public class MatrixIterationMonitor
/*   7:    */   extends DefaultIterationMonitor
/*   8:    */ {
/*   9:    */   private double normA;
/*  10:    */   private double normb;
/*  11:    */   
/*  12:    */   public MatrixIterationMonitor(double normA, double normb, int maxIter, double rtol, double atol, double dtol)
/*  13:    */   {
/*  14: 62 */     this.normA = normA;
/*  15: 63 */     this.normb = normb;
/*  16: 64 */     this.maxIter = maxIter;
/*  17: 65 */     this.rtol = rtol;
/*  18: 66 */     this.atol = atol;
/*  19: 67 */     this.dtol = dtol;
/*  20:    */   }
/*  21:    */   
/*  22:    */   public MatrixIterationMonitor(double normA, double normb)
/*  23:    */   {
/*  24: 76 */     this.normA = normA;
/*  25: 77 */     this.normb = normb;
/*  26:    */   }
/*  27:    */   
/*  28:    */   public void setMatrixNorm(double normA)
/*  29:    */   {
/*  30: 87 */     this.normA = normA;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public void setVectorNorm(double normb)
/*  34:    */   {
/*  35: 97 */     this.normb = normb;
/*  36:    */   }
/*  37:    */   
/*  38:    */   protected boolean convergedI(double r, Vector x)
/*  39:    */     throws IterativeSolverNotConvergedException
/*  40:    */   {
/*  41:104 */     if (isFirst()) {
/*  42:105 */       this.initR = r;
/*  43:    */     }
/*  44:108 */     if (r < Math.max(this.rtol * (this.normA * x.norm(this.normType) + this.normb), this.atol)) {
/*  45:109 */       return true;
/*  46:    */     }
/*  47:112 */     if (r > this.dtol * this.initR) {
/*  48:113 */       throw new IterativeSolverNotConvergedException(NotConvergedException.Reason.Divergence, this);
/*  49:    */     }
/*  50:115 */     if (this.iter >= this.maxIter) {
/*  51:116 */       throw new IterativeSolverNotConvergedException(NotConvergedException.Reason.Iterations, this);
/*  52:    */     }
/*  53:118 */     if (Double.isNaN(r)) {
/*  54:119 */       throw new IterativeSolverNotConvergedException(NotConvergedException.Reason.Divergence, this);
/*  55:    */     }
/*  56:123 */     return false;
/*  57:    */   }
/*  58:    */   
/*  59:    */   protected boolean convergedI(double r)
/*  60:    */   {
/*  61:128 */     throw new UnsupportedOperationException();
/*  62:    */   }
/*  63:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.MatrixIterationMonitor
 * JD-Core Version:    0.7.0.1
 */