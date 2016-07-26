package weka.classifiers.evaluation;

import weka.core.Instance;

public abstract interface InformationRetrievalEvaluationMetric
{
  public abstract void updateStatsForClassifier(double[] paramArrayOfDouble, Instance paramInstance)
    throws Exception;
  
  public abstract double getStatistic(String paramString, int paramInt);
  
  public abstract double getClassWeightedAverageStatistic(String paramString);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.evaluation.InformationRetrievalEvaluationMetric
 * JD-Core Version:    0.7.0.1
 */