package weka.clusterers;

import weka.core.Instance;

public abstract interface UpdateableClusterer
{
  public abstract void updateClusterer(Instance paramInstance)
    throws Exception;
  
  public abstract void updateFinished();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.clusterers.UpdateableClusterer
 * JD-Core Version:    0.7.0.1
 */