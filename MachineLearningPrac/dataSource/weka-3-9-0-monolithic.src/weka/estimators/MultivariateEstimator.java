package weka.estimators;

public abstract interface MultivariateEstimator
{
  public abstract void estimate(double[][] paramArrayOfDouble, double[] paramArrayOfDouble1);
  
  public abstract double logDensity(double[] paramArrayOfDouble);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.estimators.MultivariateEstimator
 * JD-Core Version:    0.7.0.1
 */