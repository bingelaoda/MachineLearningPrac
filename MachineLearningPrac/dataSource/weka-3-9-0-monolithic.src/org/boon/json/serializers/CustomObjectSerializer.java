package org.boon.json.serializers;

import org.boon.primitive.CharBuf;

public abstract interface CustomObjectSerializer<T>
{
  public abstract Class<T> type();
  
  public abstract void serializeObject(JsonSerializerInternal paramJsonSerializerInternal, T paramT, CharBuf paramCharBuf);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.serializers.CustomObjectSerializer
 * JD-Core Version:    0.7.0.1
 */