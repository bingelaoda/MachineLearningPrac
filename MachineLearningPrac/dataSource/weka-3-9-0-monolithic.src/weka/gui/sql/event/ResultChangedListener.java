package weka.gui.sql.event;

import java.util.EventListener;

public abstract interface ResultChangedListener
  extends EventListener
{
  public abstract void resultChanged(ResultChangedEvent paramResultChangedEvent);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.sql.event.ResultChangedListener
 * JD-Core Version:    0.7.0.1
 */