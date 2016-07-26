package org.boon.datarepo.spi;

import java.util.Comparator;
import java.util.List;
import org.boon.datarepo.LookupIndex;

public abstract interface SearchIndex<KEY, ITEM>
  extends LookupIndex<KEY, ITEM>
{
  public abstract ITEM findFirst();
  
  public abstract ITEM findLast();
  
  public abstract KEY findFirstKey();
  
  public abstract KEY findLastKey();
  
  public abstract List<ITEM> findEquals(KEY paramKEY);
  
  public abstract List<ITEM> findStartsWith(KEY paramKEY);
  
  public abstract List<ITEM> findEndsWith(KEY paramKEY);
  
  public abstract List<ITEM> findContains(KEY paramKEY);
  
  public abstract List<ITEM> findBetween(KEY paramKEY1, KEY paramKEY2);
  
  public abstract List<ITEM> findGreaterThan(KEY paramKEY);
  
  public abstract List<ITEM> findLessThan(KEY paramKEY);
  
  public abstract List<ITEM> findGreaterThanEqual(KEY paramKEY);
  
  public abstract List<ITEM> findLessThanEqual(KEY paramKEY);
  
  public abstract ITEM min();
  
  public abstract ITEM max();
  
  public abstract int count(KEY paramKEY);
  
  public abstract void setComparator(Comparator<KEY> paramComparator);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.spi.SearchIndex
 * JD-Core Version:    0.7.0.1
 */