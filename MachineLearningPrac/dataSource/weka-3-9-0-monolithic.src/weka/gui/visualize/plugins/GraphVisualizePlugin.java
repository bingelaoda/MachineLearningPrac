package weka.gui.visualize.plugins;

import javax.swing.JMenuItem;

public abstract interface GraphVisualizePlugin
{
  public abstract JMenuItem getVisualizeMenuItem(String paramString1, String paramString2);
  
  public abstract String getMinVersion();
  
  public abstract String getMaxVersion();
  
  public abstract String getDesignVersion();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.visualize.plugins.GraphVisualizePlugin
 * JD-Core Version:    0.7.0.1
 */