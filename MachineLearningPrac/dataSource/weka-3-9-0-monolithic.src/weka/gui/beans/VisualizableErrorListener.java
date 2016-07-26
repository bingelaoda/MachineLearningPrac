package weka.gui.beans;

import java.util.EventListener;

public abstract interface VisualizableErrorListener
  extends EventListener
{
  public abstract void acceptDataSet(VisualizableErrorEvent paramVisualizableErrorEvent);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.VisualizableErrorListener
 * JD-Core Version:    0.7.0.1
 */