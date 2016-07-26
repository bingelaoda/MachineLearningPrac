package weka.attributeSelection;

public abstract class AttributeSetEvaluator
  extends ASEvaluation
{
  private static final long serialVersionUID = -5744881009422257389L;
  
  public abstract double evaluateAttribute(int paramInt)
    throws Exception;
  
  public abstract double evaluateAttribute(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
    throws Exception;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.AttributeSetEvaluator
 * JD-Core Version:    0.7.0.1
 */