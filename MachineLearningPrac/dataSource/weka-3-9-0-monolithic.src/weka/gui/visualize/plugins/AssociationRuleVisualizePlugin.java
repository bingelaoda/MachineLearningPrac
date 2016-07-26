package weka.gui.visualize.plugins;

import javax.swing.JMenuItem;
import weka.associations.AssociationRules;

public abstract interface AssociationRuleVisualizePlugin
{
  public abstract JMenuItem getVisualizeMenuItem(AssociationRules paramAssociationRules, String paramString);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.visualize.plugins.AssociationRuleVisualizePlugin
 * JD-Core Version:    0.7.0.1
 */