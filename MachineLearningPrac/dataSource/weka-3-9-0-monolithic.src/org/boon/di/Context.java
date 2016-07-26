package org.boon.di;

public abstract interface Context
  extends Module
{
  public abstract Object invoke(String paramString1, String paramString2, Object paramObject);
  
  public abstract Object invokeOverload(String paramString1, String paramString2, Object paramObject);
  
  public abstract Object invokeFromJson(String paramString1, String paramString2, String paramString3);
  
  public abstract Object invokeOverloadFromJson(String paramString1, String paramString2, String paramString3);
  
  public abstract Context add(Module paramModule);
  
  public abstract Context remove(Module paramModule);
  
  public abstract Context addFirst(Module paramModule);
  
  public abstract Iterable<Module> children();
  
  public abstract void resolveProperties(Object paramObject);
  
  public abstract void resolvePropertiesIgnoreRequired(Object paramObject);
  
  public abstract void debug();
  
  public abstract Context combine(Context paramContext);
  
  public abstract Context combineFirst(Context paramContext);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.di.Context
 * JD-Core Version:    0.7.0.1
 */