package org.boon.expression;

public abstract interface ExpressionContext
{
  public abstract char idxChar(String paramString);
  
  public abstract byte idxByte(String paramString);
  
  public abstract short idxShort(String paramString);
  
  public abstract String idxString(String paramString);
  
  public abstract int idxInt(String paramString);
  
  public abstract float idxFloat(String paramString);
  
  public abstract double idxDouble(String paramString);
  
  public abstract long idxLong(String paramString);
  
  public abstract Object idx(String paramString);
  
  public abstract <T> T idx(Class<T> paramClass, String paramString);
  
  public abstract void initContext(Object[] paramArrayOfObject);
  
  public abstract Object lookup(String paramString);
  
  public abstract Object lookupWithDefault(String paramString, Object paramObject);
  
  public abstract void pushContext(Object paramObject);
  
  public abstract void removeLastContext();
  
  public abstract void put(String paramString, Object paramObject);
  
  public abstract void addFunctions(Class<?> paramClass);
  
  public abstract void addFunctions(String paramString, Class<?> paramClass);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.expression.ExpressionContext
 * JD-Core Version:    0.7.0.1
 */