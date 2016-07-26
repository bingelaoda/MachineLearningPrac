package org.boon.core;

public abstract interface HandlerWithErrorHandling<T>
  extends Handler<T>
{
  public abstract Handler<Throwable> errorHandler();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.HandlerWithErrorHandling
 * JD-Core Version:    0.7.0.1
 */