package org.boon.json;

import java.util.List;
import java.util.Map;

public abstract interface JsonParserEvents
{
  public abstract boolean objectStart(int paramInt);
  
  public abstract boolean objectEnd(int paramInt, Map<String, Object> paramMap);
  
  public abstract boolean objectFieldName(int paramInt, Map<String, Object> paramMap, CharSequence paramCharSequence);
  
  public abstract boolean objectField(int paramInt, Map<String, Object> paramMap, CharSequence paramCharSequence, Object paramObject);
  
  public abstract boolean arrayStart(int paramInt);
  
  public abstract boolean arrayEnd(int paramInt, List<Object> paramList);
  
  public abstract boolean arrayItem(int paramInt, List<Object> paramList, Object paramObject);
  
  public abstract boolean number(int paramInt1, int paramInt2, Number paramNumber);
  
  public abstract boolean string(int paramInt1, int paramInt2, CharSequence paramCharSequence);
  
  public abstract boolean bool(int paramInt, boolean paramBoolean);
  
  public abstract boolean nullValue(int paramInt);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.JsonParserEvents
 * JD-Core Version:    0.7.0.1
 */