package weka.core;

import java.util.Enumeration;

public abstract interface Instance
  extends Copyable
{
  public abstract Attribute attribute(int paramInt);
  
  public abstract Attribute attributeSparse(int paramInt);
  
  public abstract Attribute classAttribute();
  
  public abstract int classIndex();
  
  public abstract boolean classIsMissing();
  
  public abstract double classValue();
  
  public abstract Instance copy(double[] paramArrayOfDouble);
  
  public abstract Instances dataset();
  
  public abstract void deleteAttributeAt(int paramInt);
  
  public abstract Enumeration<Attribute> enumerateAttributes();
  
  public abstract boolean equalHeaders(Instance paramInstance);
  
  public abstract String equalHeadersMsg(Instance paramInstance);
  
  public abstract boolean hasMissingValue();
  
  public abstract int index(int paramInt);
  
  public abstract void insertAttributeAt(int paramInt);
  
  public abstract boolean isMissing(int paramInt);
  
  public abstract boolean isMissingSparse(int paramInt);
  
  public abstract boolean isMissing(Attribute paramAttribute);
  
  public abstract Instance mergeInstance(Instance paramInstance);
  
  public abstract int numAttributes();
  
  public abstract int numClasses();
  
  public abstract int numValues();
  
  public abstract void replaceMissingValues(double[] paramArrayOfDouble);
  
  public abstract void setClassMissing();
  
  public abstract void setClassValue(double paramDouble);
  
  public abstract void setClassValue(String paramString);
  
  public abstract void setDataset(Instances paramInstances);
  
  public abstract void setMissing(int paramInt);
  
  public abstract void setMissing(Attribute paramAttribute);
  
  public abstract void setValue(int paramInt, double paramDouble);
  
  public abstract void setValueSparse(int paramInt, double paramDouble);
  
  public abstract void setValue(int paramInt, String paramString);
  
  public abstract void setValue(Attribute paramAttribute, double paramDouble);
  
  public abstract void setValue(Attribute paramAttribute, String paramString);
  
  public abstract void setWeight(double paramDouble);
  
  public abstract Instances relationalValue(int paramInt);
  
  public abstract Instances relationalValue(Attribute paramAttribute);
  
  public abstract String stringValue(int paramInt);
  
  public abstract String stringValue(Attribute paramAttribute);
  
  public abstract double[] toDoubleArray();
  
  public abstract String toStringNoWeight(int paramInt);
  
  public abstract String toStringNoWeight();
  
  public abstract String toStringMaxDecimalDigits(int paramInt);
  
  public abstract String toString(int paramInt1, int paramInt2);
  
  public abstract String toString(int paramInt);
  
  public abstract String toString(Attribute paramAttribute, int paramInt);
  
  public abstract String toString(Attribute paramAttribute);
  
  public abstract double value(int paramInt);
  
  public abstract double valueSparse(int paramInt);
  
  public abstract double value(Attribute paramAttribute);
  
  public abstract double weight();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.Instance
 * JD-Core Version:    0.7.0.1
 */