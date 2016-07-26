package weka.classifiers.mi.miti;

import java.util.HashMap;
import weka.core.Instance;

public abstract interface SufficientStatistics
{
  public abstract double positiveCountLeft();
  
  public abstract double positiveCountRight();
  
  public abstract double totalCountLeft();
  
  public abstract double totalCountRight();
  
  public abstract void updateStats(Instance paramInstance, HashMap<Instance, Bag> paramHashMap);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.miti.SufficientStatistics
 * JD-Core Version:    0.7.0.1
 */