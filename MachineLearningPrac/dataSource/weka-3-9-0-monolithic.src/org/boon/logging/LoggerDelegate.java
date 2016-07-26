package org.boon.logging;

public abstract interface LoggerDelegate
{
  public abstract boolean infoOn();
  
  public abstract boolean debugOn();
  
  public abstract boolean traceOn();
  
  public abstract LoggerDelegate level(LogLevel paramLogLevel);
  
  public abstract LoggerDelegate turnOff();
  
  public abstract void fatal(Object... paramVarArgs);
  
  public abstract void fatal(Throwable paramThrowable, Object... paramVarArgs);
  
  public abstract void error(Object... paramVarArgs);
  
  public abstract void error(Throwable paramThrowable, Object... paramVarArgs);
  
  public abstract void warn(Object... paramVarArgs);
  
  public abstract void warn(Throwable paramThrowable, Object... paramVarArgs);
  
  public abstract void info(Object... paramVarArgs);
  
  public abstract void info(Throwable paramThrowable, Object... paramVarArgs);
  
  public abstract void config(Object... paramVarArgs);
  
  public abstract void config(Throwable paramThrowable, Object... paramVarArgs);
  
  public abstract void debug(Object... paramVarArgs);
  
  public abstract void debug(Throwable paramThrowable, Object... paramVarArgs);
  
  public abstract void trace(Object... paramVarArgs);
  
  public abstract void trace(Throwable paramThrowable, Object... paramVarArgs);
  
  public abstract void fatal(Object paramObject);
  
  public abstract void fatal(Object paramObject, Throwable paramThrowable);
  
  public abstract void error(Object paramObject);
  
  public abstract void error(Object paramObject, Throwable paramThrowable);
  
  public abstract void warn(Object paramObject);
  
  public abstract void warn(Object paramObject, Throwable paramThrowable);
  
  public abstract void info(Object paramObject);
  
  public abstract void info(Object paramObject, Throwable paramThrowable);
  
  public abstract void debug(Object paramObject);
  
  public abstract void debug(Object paramObject, Throwable paramThrowable);
  
  public abstract void trace(Object paramObject);
  
  public abstract void trace(Object paramObject, Throwable paramThrowable);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.logging.LoggerDelegate
 * JD-Core Version:    0.7.0.1
 */