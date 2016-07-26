package weka.associations;

import weka.core.Capabilities;
import weka.core.Instances;

public abstract interface Associator
{
  public abstract void buildAssociations(Instances paramInstances)
    throws Exception;
  
  public abstract Capabilities getCapabilities();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.associations.Associator
 * JD-Core Version:    0.7.0.1
 */