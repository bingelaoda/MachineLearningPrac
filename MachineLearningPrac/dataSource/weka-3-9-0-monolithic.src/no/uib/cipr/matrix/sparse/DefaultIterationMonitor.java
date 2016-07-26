/*   1:    */ package no.uib.cipr.matrix.sparse;
/*   2:    */ 
/*   3:    */ import no.uib.cipr.matrix.NotConvergedException.Reason;
/*   4:    */ import no.uib.cipr.matrix.Vector;
/*   5:    */ 
/*   6:    */ public class DefaultIterationMonitor
/*   7:    */   extends AbstractIterationMonitor
/*   8:    */ {
/*   9:    */   double initR;
/*  10:    */   double rtol;
/*  11:    */   double atol;
/*  12:    */   double dtol;
/*  13:    */   int maxIter;
/*  14:    */   
/*  15:    */   public DefaultIterationMonitor(int maxIter, double rtol, double atol, double dtol)
/*  16:    */   {
/*  17: 74 */     this.maxIter = maxIter;
/*  18: 75 */     this.rtol = rtol;
/*  19: 76 */     this.atol = atol;
/*  20: 77 */     this.dtol = dtol;
/*  21:    */   }
/*  22:    */   
/*  23:    */   public DefaultIterationMonitor()
/*  24:    */   {
/*  25: 86 */     this.maxIter = 100000;
/*  26: 87 */     this.rtol = 1.E-005D;
/*  27: 88 */     this.atol = 1.0E-050D;
/*  28: 89 */     this.dtol = 100000.0D;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public void setMaxIterations(int maxIter)
/*  32:    */   {
/*  33: 99 */     this.maxIter = maxIter;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public void setRelativeTolerance(double rtol)
/*  37:    */   {
/*  38:109 */     this.rtol = rtol;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void setAbsoluteTolerance(double atol)
/*  42:    */   {
/*  43:119 */     this.atol = atol;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void setDivergenceTolerance(double dtol)
/*  47:    */   {
/*  48:129 */     this.dtol = dtol;
/*  49:    */   }
/*  50:    */   
/*  51:    */   protected boolean convergedI(double r)
/*  52:    */     throws IterativeSolverNotConvergedException
/*  53:    */   {
/*  54:136 */     if (isFirst()) {
/*  55:137 */       this.initR = r;
/*  56:    */     }
/*  57:140 */     if (r < Math.max(this.rtol * this.initR, this.atol)) {
/*  58:141 */       return true;
/*  59:    */     }
/*  60:144 */     if (r > this.dtol * this.initR) {
/*  61:145 */       throw new IterativeSolverNotConvergedException(NotConvergedException.Reason.Divergence, this);
/*  62:    */     }
/*  63:147 */     if (this.iter >= this.maxIter) {
/*  64:148 */       throw new IterativeSolverNotConvergedException(NotConvergedException.Reason.Iterations, this);
/*  65:    */     }
/*  66:150 */     if (Double.isNaN(r)) {
/*  67:151 */       throw new IterativeSolverNotConvergedException(NotConvergedException.Reason.Divergence, this);
/*  68:    */     }
/*  69:155 */     return false;
/*  70:    */   }
/*  71:    */   
/*  72:    */   protected boolean convergedI(double r, Vector x)
/*  73:    */     throws IterativeSolverNotConvergedException
/*  74:    */   {
/*  75:161 */     return convergedI(r);
/*  76:    */   }
/*  77:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.DefaultIterationMonitor
 * JD-Core Version:    0.7.0.1
 */