package weka.gui.sql.event;

import java.util.EventListener;

public abstract interface HistoryChangedListener
  extends EventListener
{
  public abstract void historyChanged(HistoryChangedEvent paramHistoryChangedEvent);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.sql.event.HistoryChangedListener
 * JD-Core Version:    0.7.0.1
 */