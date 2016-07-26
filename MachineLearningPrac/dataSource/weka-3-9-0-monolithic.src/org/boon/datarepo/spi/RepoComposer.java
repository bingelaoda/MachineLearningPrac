package org.boon.datarepo.spi;

import org.boon.datarepo.ObjectEditor;
import org.boon.datarepo.SearchableCollection;

public abstract interface RepoComposer<KEY, ITEM>
{
  public abstract void setSearchableCollection(SearchableCollection<KEY, ITEM> paramSearchableCollection);
  
  public abstract void init();
  
  public abstract void setObjectEditor(ObjectEditor paramObjectEditor);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.spi.RepoComposer
 * JD-Core Version:    0.7.0.1
 */