package org.boon.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public abstract interface MultiMap<K, V>
  extends Iterable<Map.Entry<K, Collection<V>>>, Map<K, V>
{
  public abstract Iterator<Map.Entry<K, Collection<V>>> iterator();
  
  public abstract void add(K paramK, V paramV);
  
  public abstract V getFirst(K paramK);
  
  public abstract Iterable<V> getAll(K paramK);
  
  public abstract boolean removeValueFrom(K paramK, V paramV);
  
  public abstract boolean removeMulti(K paramK);
  
  public abstract Iterable<K> keySetMulti();
  
  public abstract Iterable<V> valueMulti();
  
  public abstract void putAll(MultiMap<K, V> paramMultiMap);
  
  public abstract Map<? extends K, ? extends Collection<V>> baseMap();
  
  public abstract V getSingleObject(K paramK);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.collections.MultiMap
 * JD-Core Version:    0.7.0.1
 */