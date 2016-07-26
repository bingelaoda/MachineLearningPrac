package weka.gui.visualize.plugins;

import javax.swing.JMenuItem;
import weka.core.Instances;

public abstract interface ErrorVisualizePlugin
{
  public abstract JMenuItem getVisualizeMenuItem(Instances paramInstances);
  
  public abstract String getMinVersion();
  
  public abstract String getMaxVersion();
  
  public abstract String getDesignVersion();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.visualize.plugins.ErrorVisualizePlugin
 * JD-Core Version:    0.7.0.1
 */