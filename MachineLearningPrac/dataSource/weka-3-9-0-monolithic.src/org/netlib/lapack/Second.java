package org.netlib.lapack;

import org.netlib.util.Etime;

public final class Second
{
  public static float second()
  {
    float f1 = 0.0F;
    float[] arrayOfFloat = new float[2];
    float f2 = 0.0F;
    Etime.etime();
    f1 = Etime.etime(arrayOfFloat, 0);
    f2 = arrayOfFloat[(1 - 1)];
    return f2;
  }
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.lapack.Second
 * JD-Core Version:    0.7.0.1
 */