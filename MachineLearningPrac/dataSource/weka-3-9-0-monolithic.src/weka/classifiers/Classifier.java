package weka.classifiers;

import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;

public abstract interface Classifier
{
  public abstract void buildClassifier(Instances paramInstances)
    throws Exception;
  
  public abstract double classifyInstance(Instance paramInstance)
    throws Exception;
  
  public abstract double[] distributionForInstance(Instance paramInstance)
    throws Exception;
  
  public abstract Capabilities getCapabilities();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.Classifier
 * JD-Core Version:    0.7.0.1
 */