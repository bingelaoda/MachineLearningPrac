package org.netlib.lapack;

public final class Dlaisnan
{
  public static boolean dlaisnan(double paramDouble1, double paramDouble2)
  {
    boolean bool = false;
    bool = paramDouble1 != paramDouble2;
    return bool;
  }
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.lapack.Dlaisnan
 * JD-Core Version:    0.7.0.1
 */