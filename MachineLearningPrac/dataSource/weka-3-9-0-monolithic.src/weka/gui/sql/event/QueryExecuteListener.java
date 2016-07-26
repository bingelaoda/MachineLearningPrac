package weka.gui.sql.event;

import java.util.EventListener;

public abstract interface QueryExecuteListener
  extends EventListener
{
  public abstract void queryExecuted(QueryExecuteEvent paramQueryExecuteEvent);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.sql.event.QueryExecuteListener
 * JD-Core Version:    0.7.0.1
 */