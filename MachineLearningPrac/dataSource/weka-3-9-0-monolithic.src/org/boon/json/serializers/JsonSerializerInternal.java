package org.boon.json.serializers;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.json.JsonSerializer;
import org.boon.primitive.CharBuf;

public abstract interface JsonSerializerInternal
  extends JsonSerializer
{
  public abstract CharBuf serialize(Object paramObject);
  
  public abstract void serializeDate(Date paramDate, CharBuf paramCharBuf);
  
  public abstract void serializeString(String paramString, CharBuf paramCharBuf);
  
  public abstract void serializeCollection(Collection<?> paramCollection, CharBuf paramCharBuf);
  
  public abstract void serializeMap(Map<Object, Object> paramMap, CharBuf paramCharBuf);
  
  public abstract void serializeArray(Object paramObject, CharBuf paramCharBuf);
  
  public abstract void serializeInstance(Object paramObject, CharBuf paramCharBuf);
  
  public abstract void serializeInstance(Object paramObject, CharBuf paramCharBuf, boolean paramBoolean);
  
  public abstract void serializeSubtypeInstance(Object paramObject, CharBuf paramCharBuf);
  
  public abstract void serializeUnknown(Object paramObject, CharBuf paramCharBuf);
  
  public abstract void serializeObject(Object paramObject, CharBuf paramCharBuf);
  
  public abstract Map<String, FieldAccess> getFields(Class<? extends Object> paramClass);
  
  public abstract boolean serializeField(Object paramObject, FieldAccess paramFieldAccess, CharBuf paramCharBuf);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.serializers.JsonSerializerInternal
 * JD-Core Version:    0.7.0.1
 */