package weka.knowledgeflow;

import weka.core.Settings;
import weka.core.WekaException;
import weka.gui.Logger;

public abstract interface FlowExecutor
{
  public abstract void setFlow(Flow paramFlow);
  
  public abstract Flow getFlow();
  
  public abstract void setLogger(Logger paramLogger);
  
  public abstract Logger getLogger();
  
  public abstract void setLoggingLevel(LoggingLevel paramLoggingLevel);
  
  public abstract LoggingLevel getLoggingLevel();
  
  public abstract void setExecutionEnvironment(BaseExecutionEnvironment paramBaseExecutionEnvironment);
  
  public abstract void setSettings(Settings paramSettings);
  
  public abstract Settings getSettings();
  
  public abstract BaseExecutionEnvironment getExecutionEnvironment();
  
  public abstract void runSequentially()
    throws WekaException;
  
  public abstract void runParallel()
    throws WekaException;
  
  public abstract void stopProcessing();
  
  public abstract boolean wasStopped();
  
  public abstract void addExecutionFinishedCallback(ExecutionFinishedCallback paramExecutionFinishedCallback);
  
  public abstract void removeExecutionFinishedCallback(ExecutionFinishedCallback paramExecutionFinishedCallback);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.FlowExecutor
 * JD-Core Version:    0.7.0.1
 */