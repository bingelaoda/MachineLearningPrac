package org.boon.criteria.internal;

import java.util.List;

public abstract interface Visitor<KEY, ITEM>
{
  public abstract void visit(KEY paramKEY, ITEM paramITEM, Object paramObject, List<String> paramList);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.criteria.internal.Visitor
 * JD-Core Version:    0.7.0.1
 */