package weka.filters;

import weka.core.Instances;

public abstract interface Sourcable
{
  public abstract String toSource(String paramString, Instances paramInstances)
    throws Exception;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.Sourcable
 * JD-Core Version:    0.7.0.1
 */