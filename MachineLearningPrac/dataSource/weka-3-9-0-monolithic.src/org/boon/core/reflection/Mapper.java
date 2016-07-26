package org.boon.core.reflection;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.boon.core.Value;

public abstract interface Mapper
{
  public abstract <T> List<T> convertListOfMapsToObjects(List<Map> paramList, Class<T> paramClass);
  
  public abstract <T> T fromMap(Map<String, Object> paramMap, Class<T> paramClass);
  
  public abstract <T> T fromList(List<?> paramList, Class<T> paramClass);
  
  public abstract Object fromValueMap(Map<String, Value> paramMap);
  
  public abstract <T> T fromValueMap(Map<String, Value> paramMap, Class<T> paramClass);
  
  public abstract Object fromMap(Map<String, Object> paramMap);
  
  public abstract Map<String, Object> toMap(Object paramObject);
  
  public abstract List<Map<String, Object>> toListOfMaps(Collection<?> paramCollection);
  
  public abstract List<?> toList(Object paramObject);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.reflection.Mapper
 * JD-Core Version:    0.7.0.1
 */