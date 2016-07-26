package weka.knowledgeflow;

import java.util.concurrent.Future;
import weka.core.Defaults;
import weka.core.Environment;
import weka.core.Settings;
import weka.core.WekaException;
import weka.gui.Logger;

public abstract interface ExecutionEnvironment
{
  public abstract String getDescription();
  
  public abstract Defaults getDefaultSettings();
  
  public abstract void setHeadless(boolean paramBoolean);
  
  public abstract boolean isHeadless();
  
  public abstract void setEnvironmentVariables(Environment paramEnvironment);
  
  public abstract Environment getEnvironmentVariables();
  
  public abstract void setSettings(Settings paramSettings);
  
  public abstract Settings getSettings();
  
  public abstract void setLog(Logger paramLogger);
  
  public abstract Logger getLog();
  
  public abstract void setLoggingLevel(LoggingLevel paramLoggingLevel);
  
  public abstract LoggingLevel getLoggingLevel();
  
  public abstract <T> Future<ExecutionResult<T>> submitTask(StepTask<T> paramStepTask)
    throws WekaException;
  
  public abstract void stopProcessing();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.ExecutionEnvironment
 * JD-Core Version:    0.7.0.1
 */