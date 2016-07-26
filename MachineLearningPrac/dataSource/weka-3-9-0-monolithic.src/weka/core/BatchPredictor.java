package weka.core;

public abstract interface BatchPredictor
{
  public abstract void setBatchSize(String paramString);
  
  public abstract String getBatchSize();
  
  public abstract double[][] distributionsForInstances(Instances paramInstances)
    throws Exception;
  
  public abstract boolean implementsMoreEfficientBatchPrediction();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.BatchPredictor
 * JD-Core Version:    0.7.0.1
 */