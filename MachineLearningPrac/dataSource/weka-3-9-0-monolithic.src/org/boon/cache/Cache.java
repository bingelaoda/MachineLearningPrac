package org.boon.cache;

public abstract interface Cache<KEY, VALUE>
{
  public abstract void put(KEY paramKEY, VALUE paramVALUE);
  
  public abstract VALUE get(KEY paramKEY);
  
  public abstract VALUE getSilent(KEY paramKEY);
  
  public abstract void remove(KEY paramKEY);
  
  public abstract int size();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.cache.Cache
 * JD-Core Version:    0.7.0.1
 */