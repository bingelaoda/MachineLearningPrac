package weka.core.expressionlanguage.core;

public abstract interface Macro
{
  public abstract Node evaluate(Node... paramVarArgs)
    throws SemanticException;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.expressionlanguage.core.Macro
 * JD-Core Version:    0.7.0.1
 */