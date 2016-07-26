package weka.gui.visualize;

import java.util.Vector;
import weka.core.Instances;

public abstract interface InstanceInfo
{
  public abstract void setInfoText(String paramString);
  
  public abstract String getInfoText();
  
  public abstract void setInfoData(Vector<Instances> paramVector);
  
  public abstract Vector<Instances> getInfoData();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.visualize.InstanceInfo
 * JD-Core Version:    0.7.0.1
 */