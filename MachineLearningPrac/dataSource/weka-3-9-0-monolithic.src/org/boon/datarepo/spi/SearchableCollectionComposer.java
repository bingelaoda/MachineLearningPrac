package org.boon.datarepo.spi;

import java.util.Map;
import org.boon.core.Function;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.datarepo.Filter;

public abstract interface SearchableCollectionComposer
{
  public abstract void setPrimaryKeyName(String paramString);
  
  public abstract void setPrimaryKeyGetter(Function paramFunction);
  
  public abstract void init();
  
  public abstract void setFields(Map<String, FieldAccess> paramMap);
  
  public abstract void setFilter(Filter paramFilter);
  
  public abstract void setRemoveDuplication(boolean paramBoolean);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.spi.SearchableCollectionComposer
 * JD-Core Version:    0.7.0.1
 */