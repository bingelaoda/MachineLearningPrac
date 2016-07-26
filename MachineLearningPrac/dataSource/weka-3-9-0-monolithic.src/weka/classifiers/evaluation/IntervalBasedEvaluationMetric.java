package weka.classifiers.evaluation;

import weka.classifiers.IntervalEstimator;
import weka.core.Instance;

public abstract interface IntervalBasedEvaluationMetric
{
  public abstract void updateStatsForIntervalEstimator(IntervalEstimator paramIntervalEstimator, Instance paramInstance, double paramDouble)
    throws Exception;
  
  public abstract String toSummaryString();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.evaluation.IntervalBasedEvaluationMetric
 * JD-Core Version:    0.7.0.1
 */