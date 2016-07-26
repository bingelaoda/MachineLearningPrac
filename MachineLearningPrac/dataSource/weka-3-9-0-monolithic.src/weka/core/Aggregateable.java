package weka.core;

public abstract interface Aggregateable<E>
{
  public abstract E aggregate(E paramE)
    throws Exception;
  
  public abstract void finalizeAggregation()
    throws Exception;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.Aggregateable
 * JD-Core Version:    0.7.0.1
 */