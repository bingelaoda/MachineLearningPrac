package org.boon.json.serializers;

import org.boon.core.reflection.fields.FieldAccess;

public abstract interface FieldFilter
{
  public abstract boolean include(Object paramObject, FieldAccess paramFieldAccess);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.serializers.FieldFilter
 * JD-Core Version:    0.7.0.1
 */