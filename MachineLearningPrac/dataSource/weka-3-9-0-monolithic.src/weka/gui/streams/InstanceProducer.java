package weka.gui.streams;

import weka.core.Instance;
import weka.core.Instances;

public abstract interface InstanceProducer
{
  public abstract void addInstanceListener(InstanceListener paramInstanceListener);
  
  public abstract void removeInstanceListener(InstanceListener paramInstanceListener);
  
  public abstract Instances outputFormat()
    throws Exception;
  
  public abstract Instance outputPeek()
    throws Exception;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.streams.InstanceProducer
 * JD-Core Version:    0.7.0.1
 */