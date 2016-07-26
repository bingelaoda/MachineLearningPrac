package org.boon.json.serializers;

import org.boon.json.serializers.impl.JsonSerializerImpl;
import org.boon.primitive.CharBuf;

public abstract interface InstanceSerializer
{
  public abstract void serializeInstance(JsonSerializerInternal paramJsonSerializerInternal, Object paramObject, CharBuf paramCharBuf);
  
  public abstract void serializeSubtypeInstance(JsonSerializerInternal paramJsonSerializerInternal, Object paramObject, CharBuf paramCharBuf);
  
  public abstract void serializeInstance(JsonSerializerImpl paramJsonSerializerImpl, Object paramObject, CharBuf paramCharBuf, boolean paramBoolean);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.serializers.InstanceSerializer
 * JD-Core Version:    0.7.0.1
 */