package weka.attributeSelection;

import weka.clusterers.Clusterer;

public abstract class UnsupervisedSubsetEvaluator
  extends ASEvaluation
  implements SubsetEvaluator
{
  static final long serialVersionUID = 627934376267488763L;
  
  public abstract int getNumClusters()
    throws Exception;
  
  public abstract Clusterer getClusterer();
  
  public abstract void setClusterer(Clusterer paramClusterer);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.UnsupervisedSubsetEvaluator
 * JD-Core Version:    0.7.0.1
 */