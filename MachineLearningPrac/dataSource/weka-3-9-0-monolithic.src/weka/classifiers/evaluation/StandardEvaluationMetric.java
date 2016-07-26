package weka.classifiers.evaluation;

import weka.core.Instance;

public abstract interface StandardEvaluationMetric
{
  public abstract String toSummaryString();
  
  public abstract void updateStatsForClassifier(double[] paramArrayOfDouble, Instance paramInstance)
    throws Exception;
  
  public abstract void updateStatsForPredictor(double paramDouble, Instance paramInstance)
    throws Exception;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.evaluation.StandardEvaluationMetric
 * JD-Core Version:    0.7.0.1
 */