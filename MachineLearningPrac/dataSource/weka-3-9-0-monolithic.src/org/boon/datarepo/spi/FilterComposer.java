package org.boon.datarepo.spi;

import java.util.Map;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.datarepo.LookupIndex;
import org.boon.datarepo.SearchableCollection;

public abstract interface FilterComposer
{
  public abstract void setSearchableCollection(SearchableCollection paramSearchableCollection);
  
  public abstract void setFields(Map<String, FieldAccess> paramMap);
  
  public abstract void setSearchIndexMap(Map<String, SearchIndex> paramMap);
  
  public abstract void setLookupIndexMap(Map<String, LookupIndex> paramMap);
  
  public abstract void init();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.spi.FilterComposer
 * JD-Core Version:    0.7.0.1
 */