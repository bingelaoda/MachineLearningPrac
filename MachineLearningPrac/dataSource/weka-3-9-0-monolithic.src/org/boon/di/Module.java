package org.boon.di;

import org.boon.core.Supplier;

public abstract interface Module
{
  public abstract <T> T get(Class<T> paramClass);
  
  public abstract Object get(String paramString);
  
  public abstract <T> T get(Class<T> paramClass, String paramString);
  
  public abstract ProviderInfo getProviderInfo(Class<?> paramClass);
  
  public abstract ProviderInfo getProviderInfo(String paramString);
  
  public abstract ProviderInfo getProviderInfo(Class<?> paramClass, String paramString);
  
  public abstract boolean has(Class paramClass);
  
  public abstract boolean has(String paramString);
  
  public abstract <T> Supplier<T> getSupplier(Class<T> paramClass, String paramString);
  
  public abstract <T> Supplier<T> getSupplier(Class<T> paramClass);
  
  public abstract void parent(Context paramContext);
  
  public abstract Iterable<Object> values();
  
  public abstract Iterable<String> names();
  
  public abstract Iterable<Class<?>> types();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.di.Module
 * JD-Core Version:    0.7.0.1
 */