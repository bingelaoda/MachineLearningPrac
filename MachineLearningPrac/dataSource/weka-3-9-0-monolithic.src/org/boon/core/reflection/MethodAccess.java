package org.boon.core.reflection;

import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.List;
import org.boon.core.TypeType;

public abstract interface MethodAccess
  extends BaseAccess, Comparable<MethodAccess>
{
  public abstract Object invokeDynamic(Object paramObject, Object... paramVarArgs);
  
  public abstract Object invoke(Object paramObject, Object... paramVarArgs);
  
  public abstract boolean isStatic();
  
  public abstract boolean isPublic();
  
  public abstract boolean isPrivate();
  
  public abstract String name();
  
  public abstract Class<?> declaringType();
  
  public abstract Class<?> returnType();
  
  public abstract boolean respondsTo(Class<?>... paramVarArgs);
  
  public abstract boolean respondsTo(Object... paramVarArgs);
  
  public abstract Object invokeStatic(Object... paramVarArgs);
  
  public abstract MethodAccess bind(Object paramObject);
  
  public abstract MethodHandle methodHandle();
  
  public abstract MethodAccess methodAccess();
  
  public abstract Object bound();
  
  public abstract <T> ConstantCallSite invokeReducerLongIntReturnLongMethodHandle(T paramT);
  
  public abstract Method method();
  
  public abstract int score();
  
  public abstract List<TypeType> paramTypeEnumList();
  
  public abstract Object invokeDynamicObject(Object paramObject1, Object paramObject2);
  
  public abstract List<List<AnnotationData>> annotationDataForParams();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.reflection.MethodAccess
 * JD-Core Version:    0.7.0.1
 */