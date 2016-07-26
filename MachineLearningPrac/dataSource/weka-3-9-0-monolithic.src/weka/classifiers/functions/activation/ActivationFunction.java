package weka.classifiers.functions.activation;

import java.io.Serializable;

public abstract interface ActivationFunction
  extends Serializable
{
  public abstract double activation(double paramDouble, double[] paramArrayOfDouble, int paramInt);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.activation.ActivationFunction
 * JD-Core Version:    0.7.0.1
 */