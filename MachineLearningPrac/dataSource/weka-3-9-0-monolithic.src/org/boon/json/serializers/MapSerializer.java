package org.boon.json.serializers;

import java.util.Map;
import org.boon.primitive.CharBuf;

public abstract interface MapSerializer
{
  public abstract void serializeMap(JsonSerializerInternal paramJsonSerializerInternal, Map<Object, Object> paramMap, CharBuf paramCharBuf);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.serializers.MapSerializer
 * JD-Core Version:    0.7.0.1
 */