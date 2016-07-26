package no.uib.cipr.matrix.sparse;

import no.uib.cipr.matrix.Vector;

public abstract interface IterationReporter
{
  public abstract void monitor(double paramDouble, Vector paramVector, int paramInt);
  
  public abstract void monitor(double paramDouble, int paramInt);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.IterationReporter
 * JD-Core Version:    0.7.0.1
 */