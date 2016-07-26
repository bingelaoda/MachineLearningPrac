package weka.experiment;

public abstract interface InstanceQueryAdapter
{
  public abstract String attributeCaseFix(String paramString);
  
  public abstract boolean getDebug();
  
  public abstract boolean getSparseData();
  
  public abstract int translateDBColumnType(String paramString);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.InstanceQueryAdapter
 * JD-Core Version:    0.7.0.1
 */