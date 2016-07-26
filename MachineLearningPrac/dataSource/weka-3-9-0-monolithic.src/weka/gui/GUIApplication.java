package weka.gui;

import javax.swing.JFrame;
import weka.core.Defaults;
import weka.core.Settings;

public abstract interface GUIApplication
{
  public abstract String getApplicationName();
  
  public abstract String getApplicationID();
  
  public abstract PerspectiveManager getPerspectiveManager();
  
  public abstract Perspective getMainPerspective();
  
  public abstract boolean isPerspectivesToolBarVisible();
  
  public abstract void hidePerspectivesToolBar();
  
  public abstract void showPerspectivesToolBar();
  
  public abstract void showErrorDialog(Exception paramException);
  
  public abstract void showInfoDialog(Object paramObject, String paramString, boolean paramBoolean);
  
  public abstract Defaults getApplicationDefaults();
  
  public abstract Settings getApplicationSettings();
  
  public abstract void settingsChanged();
  
  public abstract void revalidate();
  
  public abstract void showMenuBar(JFrame paramJFrame);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.GUIApplication
 * JD-Core Version:    0.7.0.1
 */