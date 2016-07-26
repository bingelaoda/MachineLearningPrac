package weka.classifiers.mi.miti;

public abstract interface IBestSplitMeasure
{
  public abstract double getScore(SufficientStatistics paramSufficientStatistics, int paramInt, boolean paramBoolean);
  
  public abstract double getScore(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, int paramInt, boolean paramBoolean);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.miti.IBestSplitMeasure
 * JD-Core Version:    0.7.0.1
 */