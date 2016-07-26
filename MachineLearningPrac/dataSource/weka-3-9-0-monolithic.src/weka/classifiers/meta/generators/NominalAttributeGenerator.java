package weka.classifiers.meta.generators;

import weka.core.Attribute;
import weka.core.Instances;

public abstract interface NominalAttributeGenerator
{
  public abstract void buildGenerator(Instances paramInstances, Attribute paramAttribute);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.generators.NominalAttributeGenerator
 * JD-Core Version:    0.7.0.1
 */