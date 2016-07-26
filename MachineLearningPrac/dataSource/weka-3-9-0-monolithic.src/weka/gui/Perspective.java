package weka.gui;

import java.util.List;
import javax.swing.Icon;
import javax.swing.JMenu;
import weka.core.Defaults;
import weka.core.Instances;

public abstract interface Perspective
{
  public abstract void instantiationComplete();
  
  public abstract boolean okToBeActive();
  
  public abstract void setActive(boolean paramBoolean);
  
  public abstract void setLoaded(boolean paramBoolean);
  
  public abstract void setMainApplication(GUIApplication paramGUIApplication);
  
  public abstract GUIApplication getMainApplication();
  
  public abstract String getPerspectiveID();
  
  public abstract String getPerspectiveTitle();
  
  public abstract Icon getPerspectiveIcon();
  
  public abstract String getPerspectiveTipText();
  
  public abstract List<JMenu> getMenus();
  
  public abstract Defaults getDefaultSettings();
  
  public abstract void settingsChanged();
  
  public abstract boolean acceptsInstances();
  
  public abstract void setInstances(Instances paramInstances);
  
  public abstract boolean requiresLog();
  
  public abstract void setLog(Logger paramLogger);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.Perspective
 * JD-Core Version:    0.7.0.1
 */