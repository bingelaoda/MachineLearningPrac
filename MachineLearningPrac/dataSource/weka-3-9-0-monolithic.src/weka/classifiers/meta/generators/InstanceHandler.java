package weka.classifiers.meta.generators;

import weka.core.CapabilitiesHandler;
import weka.core.Instances;

public abstract interface InstanceHandler
  extends CapabilitiesHandler
{
  public abstract void buildGenerator(Instances paramInstances)
    throws Exception;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.generators.InstanceHandler
 * JD-Core Version:    0.7.0.1
 */