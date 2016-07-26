package weka.gui.beans;

import java.util.EventListener;

public abstract interface InstanceListener
  extends EventListener
{
  public abstract void acceptInstance(InstanceEvent paramInstanceEvent);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.InstanceListener
 * JD-Core Version:    0.7.0.1
 */