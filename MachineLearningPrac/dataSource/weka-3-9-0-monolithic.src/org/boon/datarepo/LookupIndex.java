package org.boon.datarepo;

import java.util.List;
import org.boon.core.Function;

public abstract interface LookupIndex<KEY, ITEM>
  extends Bag<ITEM>
{
  public abstract ITEM get(KEY paramKEY);
  
  public abstract void setKeyGetter(Function<ITEM, KEY> paramFunction);
  
  public abstract List<ITEM> getAll(KEY paramKEY);
  
  public abstract boolean deleteByKey(KEY paramKEY);
  
  public abstract boolean isPrimaryKeyOnly();
  
  public abstract void setInputKeyTransformer(Function<Object, KEY> paramFunction);
  
  public abstract void setBucketSize(int paramInt);
  
  public abstract void init();
  
  public abstract boolean has(KEY paramKEY);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.LookupIndex
 * JD-Core Version:    0.7.0.1
 */