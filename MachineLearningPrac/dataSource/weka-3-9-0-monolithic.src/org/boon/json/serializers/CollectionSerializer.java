package org.boon.json.serializers;

import java.util.Collection;
import org.boon.primitive.CharBuf;

public abstract interface CollectionSerializer
{
  public abstract void serializeCollection(JsonSerializerInternal paramJsonSerializerInternal, Collection<?> paramCollection, CharBuf paramCharBuf);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.serializers.CollectionSerializer
 * JD-Core Version:    0.7.0.1
 */