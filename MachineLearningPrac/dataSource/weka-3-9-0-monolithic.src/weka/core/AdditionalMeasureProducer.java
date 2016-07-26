package weka.core;

import java.util.Enumeration;

public abstract interface AdditionalMeasureProducer
{
  public abstract Enumeration<String> enumerateMeasures();
  
  public abstract double getMeasure(String paramString);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.AdditionalMeasureProducer
 * JD-Core Version:    0.7.0.1
 */