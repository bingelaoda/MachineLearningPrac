package weka.gui.beans;

public abstract interface ConnectionNotificationConsumer
{
  public abstract void connectionNotification(String paramString, Object paramObject);
  
  public abstract void disconnectionNotification(String paramString, Object paramObject);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.ConnectionNotificationConsumer
 * JD-Core Version:    0.7.0.1
 */