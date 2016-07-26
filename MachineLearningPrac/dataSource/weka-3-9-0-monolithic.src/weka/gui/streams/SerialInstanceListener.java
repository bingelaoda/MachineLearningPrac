package weka.gui.streams;

import java.util.EventListener;

public abstract interface SerialInstanceListener
  extends EventListener
{
  public abstract void secondInstanceProduced(InstanceEvent paramInstanceEvent);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.streams.SerialInstanceListener
 * JD-Core Version:    0.7.0.1
 */