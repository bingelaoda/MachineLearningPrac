package weka.knowledgeflow;

import weka.core.WekaException;

public abstract interface StepOutputListener
{
  public abstract boolean dataFromStep(Data paramData)
    throws WekaException;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.StepOutputListener
 * JD-Core Version:    0.7.0.1
 */