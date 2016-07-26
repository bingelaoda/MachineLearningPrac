package weka.attributeSelection;

import java.util.BitSet;

public abstract interface SubsetEvaluator
{
  public abstract double evaluateSubset(BitSet paramBitSet)
    throws Exception;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.SubsetEvaluator
 * JD-Core Version:    0.7.0.1
 */