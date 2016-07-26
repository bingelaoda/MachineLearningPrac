package weka.gui.beans;

import java.util.Enumeration;

public abstract interface UserRequestAcceptor
{
  public abstract Enumeration<String> enumerateRequests();
  
  public abstract void performRequest(String paramString);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.UserRequestAcceptor
 * JD-Core Version:    0.7.0.1
 */