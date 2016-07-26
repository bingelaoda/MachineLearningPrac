package no.uib.cipr.matrix.sparse;

import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;

public abstract interface IterativeSolver
{
  public abstract Vector solve(Matrix paramMatrix, Vector paramVector1, Vector paramVector2)
    throws IterativeSolverNotConvergedException;
  
  public abstract void setPreconditioner(Preconditioner paramPreconditioner);
  
  public abstract Preconditioner getPreconditioner();
  
  public abstract void setIterationMonitor(IterationMonitor paramIterationMonitor);
  
  public abstract IterationMonitor getIterationMonitor();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.IterativeSolver
 * JD-Core Version:    0.7.0.1
 */