package weka.core.expressionlanguage.core;

import java.io.Serializable;

public abstract interface VariableDeclarations
  extends Serializable
{
  public abstract boolean hasVariable(String paramString);
  
  public abstract Node getVariable(String paramString);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.expressionlanguage.core.VariableDeclarations
 * JD-Core Version:    0.7.0.1
 */