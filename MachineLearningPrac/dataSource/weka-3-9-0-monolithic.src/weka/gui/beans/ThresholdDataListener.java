package weka.gui.beans;

import java.util.EventListener;

public abstract interface ThresholdDataListener
  extends EventListener
{
  public abstract void acceptDataSet(ThresholdDataEvent paramThresholdDataEvent);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.ThresholdDataListener
 * JD-Core Version:    0.7.0.1
 */