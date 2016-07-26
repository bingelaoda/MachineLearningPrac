package weka.classifiers;

import weka.core.Instance;

public abstract interface UpdateableClassifier
{
  public abstract void updateClassifier(Instance paramInstance)
    throws Exception;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.UpdateableClassifier
 * JD-Core Version:    0.7.0.1
 */