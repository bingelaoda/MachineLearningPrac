package weka.gui.beans;

import java.util.EventListener;

public abstract interface DataSourceListener
  extends EventListener
{
  public abstract void acceptDataSet(DataSetEvent paramDataSetEvent);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.DataSourceListener
 * JD-Core Version:    0.7.0.1
 */