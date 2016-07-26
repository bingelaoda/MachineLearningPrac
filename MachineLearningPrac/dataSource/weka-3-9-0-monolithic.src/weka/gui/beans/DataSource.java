package weka.gui.beans;

public abstract interface DataSource
{
  public abstract void addDataSourceListener(DataSourceListener paramDataSourceListener);
  
  public abstract void removeDataSourceListener(DataSourceListener paramDataSourceListener);
  
  public abstract void addInstanceListener(InstanceListener paramInstanceListener);
  
  public abstract void removeInstanceListener(InstanceListener paramInstanceListener);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.DataSource
 * JD-Core Version:    0.7.0.1
 */