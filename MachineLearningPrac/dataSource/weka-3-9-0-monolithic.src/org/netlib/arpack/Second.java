package org.netlib.arpack;

import org.netlib.util.Etime;
import org.netlib.util.floatW;

public final class Second
{
  public static void second(floatW paramfloatW)
  {
    float f = 0.0F;
    float[] arrayOfFloat = new float[2];
    Etime.etime();
    f = Etime.etime(arrayOfFloat, 0);
    paramfloatW.val = arrayOfFloat[(1 - 1)];
  }
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.arpack.Second
 * JD-Core Version:    0.7.0.1
 */