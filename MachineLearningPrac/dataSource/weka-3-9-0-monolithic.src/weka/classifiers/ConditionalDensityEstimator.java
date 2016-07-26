package weka.classifiers;

import weka.core.Instance;

public abstract interface ConditionalDensityEstimator
{
  public abstract double logDensity(Instance paramInstance, double paramDouble)
    throws Exception;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.ConditionalDensityEstimator
 * JD-Core Version:    0.7.0.1
 */