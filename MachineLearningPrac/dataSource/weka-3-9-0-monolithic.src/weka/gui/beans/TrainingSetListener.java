package weka.gui.beans;

import java.util.EventListener;

public abstract interface TrainingSetListener
  extends EventListener
{
  public abstract void acceptTrainingSet(TrainingSetEvent paramTrainingSetEvent);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.TrainingSetListener
 * JD-Core Version:    0.7.0.1
 */