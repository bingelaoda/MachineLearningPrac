package org.boon.json.serializers;

import org.boon.primitive.CharBuf;

public abstract interface UnknownSerializer
{
  public abstract void serializeUnknown(JsonSerializerInternal paramJsonSerializerInternal, Object paramObject, CharBuf paramCharBuf);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.serializers.UnknownSerializer
 * JD-Core Version:    0.7.0.1
 */