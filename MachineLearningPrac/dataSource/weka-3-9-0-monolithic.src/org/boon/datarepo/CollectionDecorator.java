package org.boon.datarepo;

import java.util.Collection;

public abstract interface CollectionDecorator
{
  public abstract SearchableCollection searchCollection();
  
  public abstract Collection collection();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.CollectionDecorator
 * JD-Core Version:    0.7.0.1
 */