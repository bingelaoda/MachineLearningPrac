package weka.classifiers.functions.loss;

import java.io.Serializable;

public abstract interface LossFunction
  extends Serializable
{
  public abstract double loss(double paramDouble1, double paramDouble2);
  
  public abstract double derivative(double paramDouble1, double paramDouble2);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.loss.LossFunction
 * JD-Core Version:    0.7.0.1
 */