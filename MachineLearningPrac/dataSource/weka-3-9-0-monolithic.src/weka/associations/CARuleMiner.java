package weka.associations;

import java.util.ArrayList;
import weka.core.Instances;
import weka.core.OptionHandler;

public abstract interface CARuleMiner
  extends OptionHandler
{
  public abstract ArrayList<Object>[] mineCARs(Instances paramInstances)
    throws Exception;
  
  public abstract Instances getInstancesNoClass();
  
  public abstract Instances getInstancesOnlyClass();
  
  public abstract String metricString();
  
  public abstract void setClassIndex(int paramInt);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.associations.CARuleMiner
 * JD-Core Version:    0.7.0.1
 */