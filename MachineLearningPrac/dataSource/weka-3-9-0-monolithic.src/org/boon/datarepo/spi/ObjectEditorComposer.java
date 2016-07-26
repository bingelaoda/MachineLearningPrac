package org.boon.datarepo.spi;

import java.util.Map;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.datarepo.SearchableCollection;

public abstract interface ObjectEditorComposer<KEY, ITEM>
{
  public abstract void setFields(Map<String, FieldAccess> paramMap);
  
  public abstract void setSearchableCollection(SearchableCollection<KEY, ITEM> paramSearchableCollection);
  
  public abstract void init();
  
  public abstract void hashCodeOptimizationOn();
  
  public abstract void setLookupAndExcept(boolean paramBoolean);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.spi.ObjectEditorComposer
 * JD-Core Version:    0.7.0.1
 */