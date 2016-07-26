package weka.core.converters;

public abstract interface URLSourcedLoader
{
  public abstract void setURL(String paramString)
    throws Exception;
  
  public abstract String retrieveURL();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.URLSourcedLoader
 * JD-Core Version:    0.7.0.1
 */