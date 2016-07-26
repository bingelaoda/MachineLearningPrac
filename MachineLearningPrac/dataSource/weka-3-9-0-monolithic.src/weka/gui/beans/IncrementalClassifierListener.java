package weka.gui.beans;

import java.util.EventListener;

public abstract interface IncrementalClassifierListener
  extends EventListener
{
  public abstract void acceptClassifier(IncrementalClassifierEvent paramIncrementalClassifierEvent);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.IncrementalClassifierListener
 * JD-Core Version:    0.7.0.1
 */