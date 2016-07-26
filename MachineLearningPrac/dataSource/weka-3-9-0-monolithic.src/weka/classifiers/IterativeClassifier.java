package weka.classifiers;

import weka.core.Instances;

public abstract interface IterativeClassifier
  extends Classifier
{
  public abstract void initializeClassifier(Instances paramInstances)
    throws Exception;
  
  public abstract boolean next()
    throws Exception;
  
  public abstract void done()
    throws Exception;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.IterativeClassifier
 * JD-Core Version:    0.7.0.1
 */