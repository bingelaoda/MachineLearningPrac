package org.boon.core;

public abstract interface AsyncFunction<IN, OUT>
{
  public abstract void apply(IN paramIN, Handler<OUT> paramHandler);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.AsyncFunction
 * JD-Core Version:    0.7.0.1
 */