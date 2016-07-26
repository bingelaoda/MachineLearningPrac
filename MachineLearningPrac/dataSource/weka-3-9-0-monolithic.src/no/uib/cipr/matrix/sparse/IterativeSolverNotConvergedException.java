/*  1:   */ package no.uib.cipr.matrix.sparse;
/*  2:   */ 
/*  3:   */ import no.uib.cipr.matrix.NotConvergedException;
/*  4:   */ import no.uib.cipr.matrix.NotConvergedException.Reason;
/*  5:   */ 
/*  6:   */ public class IterativeSolverNotConvergedException
/*  7:   */   extends NotConvergedException
/*  8:   */ {
/*  9:   */   private static final long serialVersionUID = 5354102050137093202L;
/* 10:   */   private int iterations;
/* 11:   */   private double r;
/* 12:   */   
/* 13:   */   public IterativeSolverNotConvergedException(NotConvergedException.Reason reason, String message, IterationMonitor iter)
/* 14:   */   {
/* 15:56 */     super(reason, message);
/* 16:57 */     this.r = iter.residual();
/* 17:58 */     this.iterations = iter.iterations();
/* 18:   */   }
/* 19:   */   
/* 20:   */   public IterativeSolverNotConvergedException(NotConvergedException.Reason reason, IterationMonitor iter)
/* 21:   */   {
/* 22:72 */     super(reason);
/* 23:73 */     this.r = iter.residual();
/* 24:74 */     this.iterations = iter.iterations();
/* 25:   */   }
/* 26:   */   
/* 27:   */   public double getResidual()
/* 28:   */   {
/* 29:81 */     return this.r;
/* 30:   */   }
/* 31:   */   
/* 32:   */   public int getIterations()
/* 33:   */   {
/* 34:88 */     return this.iterations;
/* 35:   */   }
/* 36:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.IterativeSolverNotConvergedException
 * JD-Core Version:    0.7.0.1
 */