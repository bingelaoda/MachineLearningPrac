package weka.gui.beans;

import java.util.EventListener;

public abstract interface ChartListener
  extends EventListener
{
  public abstract void acceptDataPoint(ChartEvent paramChartEvent);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.ChartListener
 * JD-Core Version:    0.7.0.1
 */