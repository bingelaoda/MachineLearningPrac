package weka.gui.beans;

public abstract interface GOECustomizer
  extends BeanCustomizer
{
  public abstract void dontShowOKCancelButtons();
  
  public abstract void closingOK();
  
  public abstract void closingCancel();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.GOECustomizer
 * JD-Core Version:    0.7.0.1
 */