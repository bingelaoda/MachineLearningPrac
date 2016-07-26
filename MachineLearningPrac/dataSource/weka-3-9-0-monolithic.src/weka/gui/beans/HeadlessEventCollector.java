package weka.gui.beans;

import java.util.EventObject;
import java.util.List;

public abstract interface HeadlessEventCollector
{
  public abstract List<EventObject> retrieveHeadlessEvents();
  
  public abstract void processHeadlessEvents(List<EventObject> paramList);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.HeadlessEventCollector
 * JD-Core Version:    0.7.0.1
 */