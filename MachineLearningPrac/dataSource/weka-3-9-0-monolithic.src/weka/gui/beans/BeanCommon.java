package weka.gui.beans;

import java.beans.EventSetDescriptor;
import weka.gui.Logger;

public abstract interface BeanCommon
{
  public abstract void setCustomName(String paramString);
  
  public abstract String getCustomName();
  
  public abstract void stop();
  
  public abstract boolean isBusy();
  
  public abstract void setLog(Logger paramLogger);
  
  public abstract boolean connectionAllowed(EventSetDescriptor paramEventSetDescriptor);
  
  public abstract boolean connectionAllowed(String paramString);
  
  public abstract void connectionNotification(String paramString, Object paramObject);
  
  public abstract void disconnectionNotification(String paramString, Object paramObject);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.BeanCommon
 * JD-Core Version:    0.7.0.1
 */