package org.boon.core.reflection.fields;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import org.boon.core.TypeType;
import org.boon.core.Value;

public abstract interface FieldAccess
{
  public abstract boolean injectable();
  
  public abstract boolean requiresInjection();
  
  public abstract boolean isNamed();
  
  public abstract boolean hasAlias();
  
  public abstract String alias();
  
  public abstract String named();
  
  public abstract String name();
  
  public abstract Object getValue(Object paramObject);
  
  public abstract void setValue(Object paramObject1, Object paramObject2);
  
  public abstract void setFromValue(Object paramObject, Value paramValue);
  
  public abstract boolean getBoolean(Object paramObject);
  
  public abstract void setBoolean(Object paramObject, boolean paramBoolean);
  
  public abstract int getInt(Object paramObject);
  
  public abstract void setInt(Object paramObject, int paramInt);
  
  public abstract short getShort(Object paramObject);
  
  public abstract void setShort(Object paramObject, short paramShort);
  
  public abstract char getChar(Object paramObject);
  
  public abstract void setChar(Object paramObject, char paramChar);
  
  public abstract long getLong(Object paramObject);
  
  public abstract void setLong(Object paramObject, long paramLong);
  
  public abstract double getDouble(Object paramObject);
  
  public abstract void setDouble(Object paramObject, double paramDouble);
  
  public abstract float getFloat(Object paramObject);
  
  public abstract void setFloat(Object paramObject, float paramFloat);
  
  public abstract byte getByte(Object paramObject);
  
  public abstract void setByte(Object paramObject, byte paramByte);
  
  public abstract Object getObject(Object paramObject);
  
  public abstract void setObject(Object paramObject1, Object paramObject2);
  
  public abstract TypeType typeEnum();
  
  public abstract boolean isPrimitive();
  
  public abstract boolean isFinal();
  
  public abstract boolean isStatic();
  
  public abstract boolean isVolatile();
  
  public abstract boolean isQualified();
  
  public abstract boolean isReadOnly();
  
  public abstract boolean isWriteOnly();
  
  public abstract Class<?> type();
  
  public abstract Class<?> declaringParent();
  
  public abstract Object parent();
  
  public abstract Field getField();
  
  public abstract boolean include();
  
  public abstract boolean ignore();
  
  public abstract ParameterizedType getParameterizedType();
  
  public abstract Class<?> getComponentClass();
  
  public abstract boolean hasAnnotation(String paramString);
  
  public abstract Map<String, Object> getAnnotationData(String paramString);
  
  public abstract boolean isViewActive(String paramString);
  
  public abstract void setStaticValue(Object paramObject);
  
  public abstract TypeType componentType();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.reflection.fields.FieldAccess
 * JD-Core Version:    0.7.0.1
 */