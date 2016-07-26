package weka.classifiers.functions.neural;

import java.io.Serializable;

public abstract interface NeuralMethod
  extends Serializable
{
  public abstract double outputValue(NeuralNode paramNeuralNode);
  
  public abstract double errorValue(NeuralNode paramNeuralNode);
  
  public abstract void updateWeights(NeuralNode paramNeuralNode, double paramDouble1, double paramDouble2);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.neural.NeuralMethod
 * JD-Core Version:    0.7.0.1
 */