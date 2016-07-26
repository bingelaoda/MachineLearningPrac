package org.boon;

import java.io.Serializable;
import java.util.Map.Entry;

public abstract interface Entry<K, V>
  extends Comparable<Entry>, Map.Entry<K, V>, Serializable, Cloneable
{
  public abstract K key();
  
  public abstract V value();
  
  public abstract boolean equals(Entry paramEntry);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.Entry
 * JD-Core Version:    0.7.0.1
 */