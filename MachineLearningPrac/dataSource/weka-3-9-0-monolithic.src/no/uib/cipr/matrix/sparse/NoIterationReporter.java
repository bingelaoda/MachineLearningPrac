package no.uib.cipr.matrix.sparse;

import no.uib.cipr.matrix.Vector;

public class NoIterationReporter
  implements IterationReporter
{
  public void monitor(double r, int i) {}
  
  public void monitor(double r, Vector x, int i) {}
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.NoIterationReporter
 * JD-Core Version:    0.7.0.1
 */