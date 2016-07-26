package weka.gui.sql.event;

import java.util.EventListener;

public abstract interface ConnectionListener
  extends EventListener
{
  public abstract void connectionChange(ConnectionEvent paramConnectionEvent);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.sql.event.ConnectionListener
 * JD-Core Version:    0.7.0.1
 */