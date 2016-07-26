package weka.gui.beans;

import java.util.EventListener;

public abstract interface BatchClassifierListener
  extends EventListener
{
  public abstract void acceptClassifier(BatchClassifierEvent paramBatchClassifierEvent);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.BatchClassifierListener
 * JD-Core Version:    0.7.0.1
 */