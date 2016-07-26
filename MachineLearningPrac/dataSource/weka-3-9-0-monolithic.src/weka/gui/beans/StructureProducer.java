package weka.gui.beans;

import weka.core.Instances;

public abstract interface StructureProducer
{
  public abstract Instances getStructure(String paramString);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.StructureProducer
 * JD-Core Version:    0.7.0.1
 */