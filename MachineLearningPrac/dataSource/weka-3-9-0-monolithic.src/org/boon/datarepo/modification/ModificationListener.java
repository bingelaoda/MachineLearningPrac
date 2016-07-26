package org.boon.datarepo.modification;

public abstract interface ModificationListener<KEY, ITEM>
{
  public abstract void modification(ModificationEvent paramModificationEvent);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.modification.ModificationListener
 * JD-Core Version:    0.7.0.1
 */