package org.boon.core.value;

import java.util.Map;
import java.util.Map.Entry;
import org.boon.core.Value;

public abstract interface ValueMap<K, V>
  extends Map<K, V>
{
  public abstract void add(MapItemValue paramMapItemValue);
  
  public abstract int len();
  
  public abstract boolean hydrated();
  
  public abstract Map.Entry<String, Value>[] items();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.value.ValueMap
 * JD-Core Version:    0.7.0.1
 */