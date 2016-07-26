package weka.classifiers.evaluation;

import weka.classifiers.ConditionalDensityEstimator;
import weka.core.Instance;

public abstract interface InformationTheoreticEvaluationMetric
{
  public abstract void updateStatsForClassifier(double[] paramArrayOfDouble, Instance paramInstance)
    throws Exception;
  
  public abstract void updateStatsForPredictor(double paramDouble, Instance paramInstance)
    throws Exception;
  
  public abstract void updateStatsForConditionalDensityEstimator(ConditionalDensityEstimator paramConditionalDensityEstimator, Instance paramInstance, double paramDouble)
    throws Exception;
  
  public abstract String toSummaryString();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.evaluation.InformationTheoreticEvaluationMetric
 * JD-Core Version:    0.7.0.1
 */