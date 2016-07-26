package org.netlib.lapack;

public final class Disnan
{
  public static boolean disnan(double paramDouble)
  {
    boolean bool = false;
    bool = Dlaisnan.dlaisnan(paramDouble, paramDouble);
    return bool;
  }
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.lapack.Disnan
 * JD-Core Version:    0.7.0.1
 */