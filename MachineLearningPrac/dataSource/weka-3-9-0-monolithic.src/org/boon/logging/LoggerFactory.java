package org.boon.logging;

import org.boon.core.Function;

public abstract interface LoggerFactory
  extends Function<String, LoggerDelegate>
{
  public abstract LoggerDelegate logger(String paramString);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.logging.LoggerFactory
 * JD-Core Version:    0.7.0.1
 */