package org.netlib.lapack;

import org.netlib.util.Util;
import org.netlib.util.floatW;

public final class Slabad
{
  public static void slabad(floatW paramfloatW1, floatW paramfloatW2)
  {
    if (((float)Util.log10(paramfloatW2.val) <= 2000.0F ? 0 : 1) != 0)
    {
      paramfloatW1.val = ((float)Math.sqrt(paramfloatW1.val));
      paramfloatW2.val = ((float)Math.sqrt(paramfloatW2.val));
    }
  }
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.lapack.Slabad
 * JD-Core Version:    0.7.0.1
 */