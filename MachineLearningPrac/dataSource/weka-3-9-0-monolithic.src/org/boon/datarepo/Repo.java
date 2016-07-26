package org.boon.datarepo;

import java.util.List;
import org.boon.criteria.Update;
import org.boon.criteria.internal.Criteria;

public abstract interface Repo<KEY, ITEM>
  extends ObjectEditor<KEY, ITEM>, SearchableCollection<KEY, ITEM>
{
  public abstract Repo init(List<ITEM> paramList);
  
  public abstract void updateByFilter(String paramString, Object paramObject, Criteria... paramVarArgs);
  
  public abstract void updateByFilterUsingValue(String paramString1, String paramString2, Criteria... paramVarArgs);
  
  public abstract void updateByFilter(String paramString, int paramInt, Criteria... paramVarArgs);
  
  public abstract void updateByFilter(String paramString, long paramLong, Criteria... paramVarArgs);
  
  public abstract void updateByFilter(String paramString, char paramChar, Criteria... paramVarArgs);
  
  public abstract void updateByFilter(String paramString, short paramShort, Criteria... paramVarArgs);
  
  public abstract void updateByFilter(String paramString, byte paramByte, Criteria... paramVarArgs);
  
  public abstract void updateByFilter(String paramString, float paramFloat, Criteria... paramVarArgs);
  
  public abstract void updateByFilter(String paramString, double paramDouble, Criteria... paramVarArgs);
  
  public abstract void updateByFilter(List<Update> paramList, Criteria... paramVarArgs);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.Repo
 * JD-Core Version:    0.7.0.1
 */