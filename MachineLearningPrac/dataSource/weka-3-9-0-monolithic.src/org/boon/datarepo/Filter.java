package org.boon.datarepo;

import org.boon.criteria.internal.Criteria;

public abstract interface Filter
{
  public abstract ResultSet filter(Criteria... paramVarArgs);
  
  public abstract void invalidate();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.Filter
 * JD-Core Version:    0.7.0.1
 */