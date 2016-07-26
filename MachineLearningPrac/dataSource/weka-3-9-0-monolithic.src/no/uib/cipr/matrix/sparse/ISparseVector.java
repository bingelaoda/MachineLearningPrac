package no.uib.cipr.matrix.sparse;

import no.uib.cipr.matrix.Vector;

public abstract interface ISparseVector
  extends Vector
{
  public abstract int[] getIndex();
  
  public abstract int getUsed();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.ISparseVector
 * JD-Core Version:    0.7.0.1
 */