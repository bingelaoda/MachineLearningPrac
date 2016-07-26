package weka.core;

public abstract interface CommandlineRunnable
{
  public abstract void preExecution()
    throws Exception;
  
  public abstract void run(Object paramObject, String[] paramArrayOfString)
    throws Exception;
  
  public abstract void postExecution()
    throws Exception;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.CommandlineRunnable
 * JD-Core Version:    0.7.0.1
 */