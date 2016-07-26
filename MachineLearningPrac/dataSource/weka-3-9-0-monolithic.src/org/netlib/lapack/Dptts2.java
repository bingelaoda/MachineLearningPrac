package org.netlib.lapack;

import org.netlib.blas.Dscal;

public final class Dptts2
{
  public static void dptts2(int paramInt1, int paramInt2, double[] paramArrayOfDouble1, int paramInt3, double[] paramArrayOfDouble2, int paramInt4, double[] paramArrayOfDouble3, int paramInt5, int paramInt6)
  {
    int i = 0;
    int j = 0;
    if ((paramInt1 > 1 ? 0 : 1) != 0)
    {
      if ((paramInt1 != 1 ? 0 : 1) != 0) {
        Dscal.dscal(paramInt2, 1.0D / paramArrayOfDouble1[(1 - 1 + paramInt3)], paramArrayOfDouble3, paramInt5, paramInt6);
      }
      return;
    }
    j = 1;
    for (int k = paramInt2 - 1 + 1; k > 0; k--)
    {
      i = 2;
      for (int m = paramInt1 - 2 + 1; m > 0; m--)
      {
        paramArrayOfDouble3[(i - 1 + (j - 1) * paramInt6 + paramInt5)] -= paramArrayOfDouble3[(i - 1 - 1 + (j - 1) * paramInt6 + paramInt5)] * paramArrayOfDouble2[(i - 1 - 1 + paramInt4)];
        i += 1;
      }
      paramArrayOfDouble3[(paramInt1 - 1 + (j - 1) * paramInt6 + paramInt5)] /= paramArrayOfDouble1[(paramInt1 - 1 + paramInt3)];
      i = paramInt1 - 1;
      for (m = (1 - (paramInt1 - 1) + -1) / -1; m > 0; m--)
      {
        paramArrayOfDouble3[(i - 1 + (j - 1) * paramInt6 + paramInt5)] = (paramArrayOfDouble3[(i - 1 + (j - 1) * paramInt6 + paramInt5)] / paramArrayOfDouble1[(i - 1 + paramInt3)] - paramArrayOfDouble3[(i + 1 - 1 + (j - 1) * paramInt6 + paramInt5)] * paramArrayOfDouble2[(i - 1 + paramInt4)]);
        i += -1;
      }
      j += 1;
    }
  }
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.lapack.Dptts2
 * JD-Core Version:    0.7.0.1
 */