package no.uib.cipr.matrix;

public abstract interface MatrixEntry
{
  public abstract int row();
  
  public abstract int column();
  
  public abstract double get();
  
  public abstract void set(double paramDouble);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.MatrixEntry
 * JD-Core Version:    0.7.0.1
 */