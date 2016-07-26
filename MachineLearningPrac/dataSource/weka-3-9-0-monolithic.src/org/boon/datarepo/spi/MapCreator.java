package org.boon.datarepo.spi;

import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;

public abstract interface MapCreator
{
  public abstract NavigableMap createNavigableMap(Class<?> paramClass);
  
  public abstract NavigableMap createNavigableMap(Class<?> paramClass, Comparator paramComparator);
  
  public abstract Map createMap(Class<?> paramClass);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.spi.MapCreator
 * JD-Core Version:    0.7.0.1
 */