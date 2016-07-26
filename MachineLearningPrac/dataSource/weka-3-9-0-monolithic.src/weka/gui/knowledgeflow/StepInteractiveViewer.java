package weka.gui.knowledgeflow;

import java.awt.Window;
import weka.core.Settings;
import weka.core.WekaException;
import weka.knowledgeflow.steps.Step;

public abstract interface StepInteractiveViewer
{
  public abstract void setStep(Step paramStep);
  
  public abstract void setMainKFPerspective(MainKFPerspective paramMainKFPerspective);
  
  public abstract MainKFPerspective getMainKFPerspective();
  
  public abstract String getViewerName();
  
  public abstract void setParentWindow(Window paramWindow);
  
  public abstract Settings getSettings();
  
  public abstract void init()
    throws WekaException;
  
  public abstract void nowVisible();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.StepInteractiveViewer
 * JD-Core Version:    0.7.0.1
 */