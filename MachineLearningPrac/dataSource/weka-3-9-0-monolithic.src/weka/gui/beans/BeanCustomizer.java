package weka.gui.beans;

import java.beans.Customizer;

public abstract interface BeanCustomizer
  extends Customizer
{
  public abstract void setModifiedListener(ModifyListener paramModifyListener);
  
  public static abstract interface ModifyListener
  {
    public abstract void setModifiedStatus(Object paramObject, boolean paramBoolean);
  }
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.BeanCustomizer
 * JD-Core Version:    0.7.0.1
 */