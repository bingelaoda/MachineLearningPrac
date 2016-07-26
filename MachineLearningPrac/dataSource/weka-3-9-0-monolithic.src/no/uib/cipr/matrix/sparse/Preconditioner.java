package no.uib.cipr.matrix.sparse;

import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;

public abstract interface Preconditioner
{
  public abstract Vector apply(Vector paramVector1, Vector paramVector2);
  
  public abstract Vector transApply(Vector paramVector1, Vector paramVector2);
  
  public abstract void setMatrix(Matrix paramMatrix);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.Preconditioner
 * JD-Core Version:    0.7.0.1
 */