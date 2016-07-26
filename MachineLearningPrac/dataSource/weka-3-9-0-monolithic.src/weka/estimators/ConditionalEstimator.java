package weka.estimators;

import weka.core.RevisionHandler;

public abstract interface ConditionalEstimator
  extends RevisionHandler
{
  public abstract void addValue(double paramDouble1, double paramDouble2, double paramDouble3);
  
  public abstract Estimator getEstimator(double paramDouble);
  
  public abstract double getProbability(double paramDouble1, double paramDouble2);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.estimators.ConditionalEstimator
 * JD-Core Version:    0.7.0.1
 */