package weka.attributeSelection;

import java.util.BitSet;
import weka.core.Instance;
import weka.core.Instances;

public abstract class HoldOutSubsetEvaluator
  extends ASEvaluation
  implements SubsetEvaluator
{
  private static final long serialVersionUID = 8280529785412054174L;
  
  public abstract double evaluateSubset(BitSet paramBitSet, Instances paramInstances)
    throws Exception;
  
  public abstract double evaluateSubset(BitSet paramBitSet, Instance paramInstance, boolean paramBoolean)
    throws Exception;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.HoldOutSubsetEvaluator
 * JD-Core Version:    0.7.0.1
 */