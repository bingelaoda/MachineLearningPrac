package weka.gui.beans;

import java.util.EventListener;

public abstract interface TestSetListener
  extends EventListener
{
  public abstract void acceptTestSet(TestSetEvent paramTestSetEvent);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.TestSetListener
 * JD-Core Version:    0.7.0.1
 */