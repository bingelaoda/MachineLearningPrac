package org.boon.core;

public abstract interface Reducer<IN, SUM>
{
  public abstract SUM apply(SUM paramSUM, IN paramIN);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.Reducer
 * JD-Core Version:    0.7.0.1
 */