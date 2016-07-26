package no.uib.cipr.matrix.sparse;

import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.Vector.Norm;

public abstract interface IterationMonitor
{
  public abstract void setFirst();
  
  public abstract boolean isFirst();
  
  public abstract void next();
  
  public abstract int iterations();
  
  public abstract double residual();
  
  public abstract boolean converged(Vector paramVector1, Vector paramVector2)
    throws IterativeSolverNotConvergedException;
  
  public abstract boolean converged(double paramDouble, Vector paramVector)
    throws IterativeSolverNotConvergedException;
  
  public abstract boolean converged(double paramDouble)
    throws IterativeSolverNotConvergedException;
  
  public abstract boolean converged(Vector paramVector)
    throws IterativeSolverNotConvergedException;
  
  public abstract void setIterationReporter(IterationReporter paramIterationReporter);
  
  public abstract IterationReporter getIterationReporter();
  
  public abstract void setNormType(Vector.Norm paramNorm);
  
  public abstract Vector.Norm getNormType();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.IterationMonitor
 * JD-Core Version:    0.7.0.1
 */