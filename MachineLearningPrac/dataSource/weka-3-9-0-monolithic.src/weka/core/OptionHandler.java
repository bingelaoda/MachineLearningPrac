package weka.core;

import java.util.Enumeration;

public abstract interface OptionHandler
{
  public abstract Enumeration<Option> listOptions();
  
  public abstract void setOptions(String[] paramArrayOfString)
    throws Exception;
  
  public abstract String[] getOptions();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.OptionHandler
 * JD-Core Version:    0.7.0.1
 */