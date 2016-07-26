package weka.gui.beans;

import java.util.EventListener;

public abstract interface BatchClustererListener
  extends EventListener
{
  public abstract void acceptClusterer(BatchClustererEvent paramBatchClustererEvent);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.BatchClustererListener
 * JD-Core Version:    0.7.0.1
 */