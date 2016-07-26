package org.netlib.blas;

public final class Daxpy
{
  public static void daxpy(int paramInt1, double paramDouble, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2, int paramInt4, int paramInt5)
  {
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    if ((paramInt1 > 0 ? 0 : 1) != 0) {
      return;
    }
    if ((paramDouble != 0.0D ? 0 : 1) != 0) {
      return;
    }
    if ((paramInt3 != 1 ? 0 : 1) != 0) {}
    if (((paramInt5 != 1 ? 0 : 1) != 0 ? 1 : 0) == 0)
    {
      j = 1;
      k = 1;
      if ((paramInt3 >= 0 ? 0 : 1) != 0) {
        j = (-paramInt1 + 1) * paramInt3 + 1;
      }
      if ((paramInt5 >= 0 ? 0 : 1) != 0) {
        k = (-paramInt1 + 1) * paramInt5 + 1;
      }
      i = 1;
      for (i1 = paramInt1 - 1 + 1; i1 > 0; i1--)
      {
        paramArrayOfDouble2[(k - 1 + paramInt4)] += paramDouble * paramArrayOfDouble1[(j - 1 + paramInt2)];
        j += paramInt3;
        k += paramInt5;
        i += 1;
      }
      return;
    }
    m = paramInt1 % 4;
    if ((m != 0 ? 0 : 1) == 0)
    {
      i = 1;
      for (i1 = m - 1 + 1; i1 > 0; i1--)
      {
        paramArrayOfDouble2[(i - 1 + paramInt4)] += paramDouble * paramArrayOfDouble1[(i - 1 + paramInt2)];
        i += 1;
      }
      if ((paramInt1 >= 4 ? 0 : 1) != 0) {
        return;
      }
    }
    n = m + 1;
    i = n;
    for (int i1 = (paramInt1 - n + 4) / 4; i1 > 0; i1--)
    {
      paramArrayOfDouble2[(i - 1 + paramInt4)] += paramDouble * paramArrayOfDouble1[(i - 1 + paramInt2)];
      paramArrayOfDouble2[(i + 1 - 1 + paramInt4)] += paramDouble * paramArrayOfDouble1[(i + 1 - 1 + paramInt2)];
      paramArrayOfDouble2[(i + 2 - 1 + paramInt4)] += paramDouble * paramArrayOfDouble1[(i + 2 - 1 + paramInt2)];
      paramArrayOfDouble2[(i + 3 - 1 + paramInt4)] += paramDouble * paramArrayOfDouble1[(i + 3 - 1 + paramInt2)];
      i += 4;
    }
  }
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.blas.Daxpy
 * JD-Core Version:    0.7.0.1
 */