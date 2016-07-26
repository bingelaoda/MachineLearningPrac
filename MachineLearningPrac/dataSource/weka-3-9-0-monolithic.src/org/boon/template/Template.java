package org.boon.template;

public abstract interface Template
{
  public abstract String replace(String paramString, Object... paramVarArgs);
  
  public abstract String replaceFromResource(String paramString, Object... paramVarArgs);
  
  public abstract String replaceFromFile(String paramString, Object... paramVarArgs);
  
  public abstract String replaceFromURI(String paramString, Object... paramVarArgs);
  
  public abstract void addFunctions(Class<?> paramClass);
  
  public abstract void addFunctions(String paramString, Class<?> paramClass);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.template.Template
 * JD-Core Version:    0.7.0.1
 */