package org.boon.datarepo;

import java.util.Collection;
import java.util.List;

public abstract interface Bag<ITEM>
{
  public abstract boolean add(ITEM paramITEM);
  
  public abstract boolean delete(ITEM paramITEM);
  
  public abstract List<ITEM> all();
  
  public abstract int size();
  
  public abstract Collection<ITEM> toCollection();
  
  public abstract void clear();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.Bag
 * JD-Core Version:    0.7.0.1
 */