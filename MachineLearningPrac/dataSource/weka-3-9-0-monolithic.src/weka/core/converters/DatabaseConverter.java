package weka.core.converters;

public abstract interface DatabaseConverter
{
  public abstract String getUrl();
  
  public abstract String getUser();
  
  public abstract void setUrl(String paramString);
  
  public abstract void setUser(String paramString);
  
  public abstract void setPassword(String paramString);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.DatabaseConverter
 * JD-Core Version:    0.7.0.1
 */