package weka.attributeSelection;

import weka.core.Instance;
import weka.core.Instances;

public abstract interface AttributeTransformer
{
  public abstract Instances transformedHeader()
    throws Exception;
  
  public abstract Instances transformedData(Instances paramInstances)
    throws Exception;
  
  public abstract Instance convertInstance(Instance paramInstance)
    throws Exception;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.AttributeTransformer
 * JD-Core Version:    0.7.0.1
 */