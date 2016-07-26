package weka.gui;

import java.awt.Component;
import java.awt.event.ActionListener;
import javax.swing.JFrame;

public abstract interface MainMenuExtension
{
  public abstract String getSubmenuTitle();
  
  public abstract String getMenuTitle();
  
  public abstract ActionListener getActionListener(JFrame paramJFrame);
  
  public abstract void fillFrame(Component paramComponent);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.MainMenuExtension
 * JD-Core Version:    0.7.0.1
 */