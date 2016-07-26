package weka.clusterers;

import weka.core.Instance;

public abstract interface DensityBasedClusterer
  extends Clusterer
{
  public abstract double[] clusterPriors()
    throws Exception;
  
  public abstract double[] logDensityPerClusterForInstance(Instance paramInstance)
    throws Exception;
  
  public abstract double logDensityForInstance(Instance paramInstance)
    throws Exception;
  
  public abstract double[] logJointDensitiesForInstance(Instance paramInstance)
    throws Exception;
  
  public abstract double[] distributionForInstance(Instance paramInstance)
    throws Exception;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.clusterers.DensityBasedClusterer
 * JD-Core Version:    0.7.0.1
 */