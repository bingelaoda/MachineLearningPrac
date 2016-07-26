package weka.knowledgeflow.steps;

import java.util.List;
import weka.core.WekaException;
import weka.knowledgeflow.Data;

public abstract interface BaseStepExtender
{
  public abstract void stepInit()
    throws WekaException;
  
  public abstract List<String> getIncomingConnectionTypes();
  
  public abstract List<String> getOutgoingConnectionTypes();
  
  public abstract void start()
    throws WekaException;
  
  public abstract void processIncoming(Data paramData)
    throws WekaException;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.BaseStepExtender
 * JD-Core Version:    0.7.0.1
 */