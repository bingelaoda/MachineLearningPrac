package weka.core.pmml;

import weka.core.Instances;

public abstract interface PMMLProducer
{
  public abstract String toPMML(Instances paramInstances);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.PMMLProducer
 * JD-Core Version:    0.7.0.1
 */