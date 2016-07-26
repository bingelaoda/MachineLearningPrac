package weka.core;

public abstract interface PartitionGenerator
  extends CapabilitiesHandler
{
  public abstract void generatePartition(Instances paramInstances)
    throws Exception;
  
  public abstract double[] getMembershipValues(Instance paramInstance)
    throws Exception;
  
  public abstract int numElements()
    throws Exception;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.PartitionGenerator
 * JD-Core Version:    0.7.0.1
 */