package org.boon.json;

import org.boon.primitive.CharBuf;

public abstract interface JsonSerializer
{
  public abstract CharBuf serialize(Object paramObject);
  
  public abstract void serialize(CharBuf paramCharBuf, Object paramObject);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.JsonSerializer
 * JD-Core Version:    0.7.0.1
 */