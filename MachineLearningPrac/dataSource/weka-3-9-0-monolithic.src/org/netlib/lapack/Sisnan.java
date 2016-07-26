package org.netlib.lapack;

public final class Sisnan
{
  public static boolean sisnan(float paramFloat)
  {
    boolean bool = false;
    bool = Slaisnan.slaisnan(paramFloat, paramFloat);
    return bool;
  }
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.lapack.Sisnan
 * JD-Core Version:    0.7.0.1
 */