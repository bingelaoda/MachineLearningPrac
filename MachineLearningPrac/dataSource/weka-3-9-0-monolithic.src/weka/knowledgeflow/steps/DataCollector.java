package weka.knowledgeflow.steps;

import weka.core.WekaException;

public abstract interface DataCollector
{
  public abstract Object retrieveData();
  
  public abstract void restoreData(Object paramObject)
    throws WekaException;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.DataCollector
 * JD-Core Version:    0.7.0.1
 */