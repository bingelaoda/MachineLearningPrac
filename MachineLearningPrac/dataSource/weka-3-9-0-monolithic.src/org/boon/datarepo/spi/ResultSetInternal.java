package org.boon.datarepo.spi;

import java.util.List;
import org.boon.criteria.internal.Criteria;
import org.boon.datarepo.ResultSet;

public abstract interface ResultSetInternal<T>
  extends ResultSet<T>
{
  public abstract void addResults(List<T> paramList);
  
  public abstract void filterAndPrune(Criteria paramCriteria);
  
  public abstract int lastSize();
  
  public abstract void andResults();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.spi.ResultSetInternal
 * JD-Core Version:    0.7.0.1
 */