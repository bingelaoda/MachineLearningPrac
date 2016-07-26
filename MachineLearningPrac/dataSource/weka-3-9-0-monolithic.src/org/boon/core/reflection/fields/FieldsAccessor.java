package org.boon.core.reflection.fields;

import java.util.Map;

public abstract interface FieldsAccessor
{
  public abstract Map<String, FieldAccess> getFields(Class<? extends Object> paramClass);
  
  public abstract boolean isCaseInsensitive();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.reflection.fields.FieldsAccessor
 * JD-Core Version:    0.7.0.1
 */