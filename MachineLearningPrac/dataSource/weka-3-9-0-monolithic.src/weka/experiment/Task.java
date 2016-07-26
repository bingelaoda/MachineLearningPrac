package weka.experiment;

import java.io.Serializable;

public abstract interface Task
  extends Serializable
{
  public abstract void execute();
  
  public abstract TaskStatusInfo getTaskStatus();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.Task
 * JD-Core Version:    0.7.0.1
 */