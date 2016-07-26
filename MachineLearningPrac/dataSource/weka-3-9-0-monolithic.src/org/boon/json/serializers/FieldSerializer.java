package org.boon.json.serializers;

import org.boon.core.reflection.fields.FieldAccess;
import org.boon.primitive.CharBuf;

public abstract interface FieldSerializer
{
  public abstract boolean serializeField(JsonSerializerInternal paramJsonSerializerInternal, Object paramObject, FieldAccess paramFieldAccess, CharBuf paramCharBuf);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.serializers.FieldSerializer
 * JD-Core Version:    0.7.0.1
 */