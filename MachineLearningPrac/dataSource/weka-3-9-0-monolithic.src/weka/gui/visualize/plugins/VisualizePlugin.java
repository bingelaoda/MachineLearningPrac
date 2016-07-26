package weka.gui.visualize.plugins;

import java.util.ArrayList;
import javax.swing.JMenuItem;
import weka.classifiers.evaluation.Prediction;
import weka.core.Attribute;

public abstract interface VisualizePlugin
{
  public abstract JMenuItem getVisualizeMenuItem(ArrayList<Prediction> paramArrayList, Attribute paramAttribute);
  
  public abstract String getMinVersion();
  
  public abstract String getMaxVersion();
  
  public abstract String getDesignVersion();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.visualize.plugins.VisualizePlugin
 * JD-Core Version:    0.7.0.1
 */