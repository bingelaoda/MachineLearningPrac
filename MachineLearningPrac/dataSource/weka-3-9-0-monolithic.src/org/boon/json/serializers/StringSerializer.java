package org.boon.json.serializers;

import org.boon.primitive.CharBuf;

public abstract interface StringSerializer
{
  public abstract void serializeString(JsonSerializerInternal paramJsonSerializerInternal, String paramString, CharBuf paramCharBuf);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.serializers.StringSerializer
 * JD-Core Version:    0.7.0.1
 */