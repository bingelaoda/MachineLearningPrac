package org.boon.core.reflection;

public abstract interface ConstructorAccess<T>
  extends BaseAccess
{
  public abstract T create(Object... paramVarArgs);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.reflection.ConstructorAccess
 * JD-Core Version:    0.7.0.1
 */