package org.boon.core.reflection;

import java.lang.reflect.Type;

public abstract interface BaseAccess
  extends Annotated
{
  public abstract Class<?>[] parameterTypes();
  
  public abstract Type[] getGenericParameterTypes();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.reflection.BaseAccess
 * JD-Core Version:    0.7.0.1
 */