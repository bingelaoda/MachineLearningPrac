package weka.gui.explorer;

public abstract interface ClustererPanelLaunchHandlerPlugin
{
  public abstract void setClustererPanel(ClustererPanel paramClustererPanel);
  
  public abstract String getLaunchCommand();
  
  public abstract void launch();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.explorer.ClustererPanelLaunchHandlerPlugin
 * JD-Core Version:    0.7.0.1
 */