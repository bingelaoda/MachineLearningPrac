package weka.estimators;

public abstract interface UnivariateIntervalEstimator
{
  public abstract void addValue(double paramDouble1, double paramDouble2);
  
  public abstract double[][] predictIntervals(double paramDouble);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.estimators.UnivariateIntervalEstimator
 * JD-Core Version:    0.7.0.1
 */