package jsyntaxpane.actions;

import javax.swing.Action;
import jsyntaxpane.util.Configuration;

public abstract interface SyntaxAction
  extends Action
{
  public abstract void config(Configuration paramConfiguration, String paramString);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.SyntaxAction
 * JD-Core Version:    0.7.0.1
 */