/*   1:    */ package no.uib.cipr.matrix.sparse;
/*   2:    */ 
/*   3:    */ import no.uib.cipr.matrix.Vector;
/*   4:    */ import no.uib.cipr.matrix.Vector.Norm;
/*   5:    */ 
/*   6:    */ public abstract class AbstractIterationMonitor
/*   7:    */   implements IterationMonitor
/*   8:    */ {
/*   9:    */   protected int iter;
/*  10:    */   protected Vector.Norm normType;
/*  11:    */   protected IterationReporter reporter;
/*  12:    */   protected double residual;
/*  13:    */   
/*  14:    */   public AbstractIterationMonitor()
/*  15:    */   {
/*  16: 56 */     this.normType = Vector.Norm.Two;
/*  17: 57 */     this.reporter = new NoIterationReporter();
/*  18:    */   }
/*  19:    */   
/*  20:    */   public void setFirst()
/*  21:    */   {
/*  22: 61 */     this.iter = 0;
/*  23:    */   }
/*  24:    */   
/*  25:    */   public boolean isFirst()
/*  26:    */   {
/*  27: 65 */     return this.iter == 0;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public void next()
/*  31:    */   {
/*  32: 69 */     this.iter += 1;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public int iterations()
/*  36:    */   {
/*  37: 73 */     return this.iter;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public boolean converged(Vector r, Vector x)
/*  41:    */     throws IterativeSolverNotConvergedException
/*  42:    */   {
/*  43: 78 */     return converged(r.norm(this.normType), x);
/*  44:    */   }
/*  45:    */   
/*  46:    */   public boolean converged(double r, Vector x)
/*  47:    */     throws IterativeSolverNotConvergedException
/*  48:    */   {
/*  49: 83 */     this.reporter.monitor(r, x, this.iter);
/*  50: 84 */     this.residual = r;
/*  51: 85 */     return convergedI(r, x);
/*  52:    */   }
/*  53:    */   
/*  54:    */   public boolean converged(double r)
/*  55:    */     throws IterativeSolverNotConvergedException
/*  56:    */   {
/*  57: 90 */     this.reporter.monitor(r, this.iter);
/*  58: 91 */     this.residual = r;
/*  59: 92 */     return convergedI(r);
/*  60:    */   }
/*  61:    */   
/*  62:    */   protected abstract boolean convergedI(double paramDouble, Vector paramVector)
/*  63:    */     throws IterativeSolverNotConvergedException;
/*  64:    */   
/*  65:    */   protected abstract boolean convergedI(double paramDouble)
/*  66:    */     throws IterativeSolverNotConvergedException;
/*  67:    */   
/*  68:    */   public boolean converged(Vector r)
/*  69:    */     throws IterativeSolverNotConvergedException
/*  70:    */   {
/*  71:103 */     return converged(r.norm(this.normType));
/*  72:    */   }
/*  73:    */   
/*  74:    */   public Vector.Norm getNormType()
/*  75:    */   {
/*  76:107 */     return this.normType;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public void setNormType(Vector.Norm normType)
/*  80:    */   {
/*  81:111 */     this.normType = normType;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public IterationReporter getIterationReporter()
/*  85:    */   {
/*  86:115 */     return this.reporter;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void setIterationReporter(IterationReporter monitor)
/*  90:    */   {
/*  91:119 */     this.reporter = monitor;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public double residual()
/*  95:    */   {
/*  96:123 */     return this.residual;
/*  97:    */   }
/*  98:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.AbstractIterationMonitor
 * JD-Core Version:    0.7.0.1
 */