package weka.estimators;

import weka.core.RevisionHandler;

public abstract interface UnivariateDensityEstimator
  extends RevisionHandler
{
  public abstract void addValue(double paramDouble1, double paramDouble2);
  
  public abstract double logDensity(double paramDouble);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.estimators.UnivariateDensityEstimator
 * JD-Core Version:    0.7.0.1
 */