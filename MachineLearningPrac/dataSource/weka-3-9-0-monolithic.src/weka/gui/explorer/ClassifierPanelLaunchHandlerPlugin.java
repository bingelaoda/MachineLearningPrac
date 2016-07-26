package weka.gui.explorer;

public abstract interface ClassifierPanelLaunchHandlerPlugin
{
  public abstract void setClassifierPanel(ClassifierPanel paramClassifierPanel);
  
  public abstract String getLaunchCommand();
  
  public abstract void launch();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.explorer.ClassifierPanelLaunchHandlerPlugin
 * JD-Core Version:    0.7.0.1
 */