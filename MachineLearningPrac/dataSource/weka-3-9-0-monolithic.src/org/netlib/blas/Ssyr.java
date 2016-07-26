package org.netlib.blas;

import org.netlib.err.Xerbla;

public final class Ssyr
{
  public static void ssyr(String paramString, int paramInt1, float paramFloat, float[] paramArrayOfFloat1, int paramInt2, int paramInt3, float[] paramArrayOfFloat2, int paramInt4, int paramInt5)
  {
    float f = 0.0F;
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    int i1 = 0;
    j = 0;
    if ((((Lsame.lsame(paramString, "U") ^ true)) && ((Lsame.lsame(paramString, "L") ^ true)) ? 1 : 0) != 0) {
      j = 1;
    } else if ((paramInt1 >= 0 ? 0 : 1) != 0) {
      j = 2;
    } else if ((paramInt3 != 0 ? 0 : 1) != 0) {
      j = 5;
    } else if ((paramInt5 >= Math.max(1, paramInt1) ? 0 : 1) != 0) {
      j = 7;
    }
    if ((j == 0 ? 0 : 1) != 0)
    {
      Xerbla.xerbla("SSYR  ", j);
      return;
    }
    if ((paramInt1 != 0 ? 0 : 1) == 0) {}
    if (((paramFloat != 0.0F ? 0 : 1) == 0 ? 0 : 1) != 0) {
      return;
    }
    if ((paramInt3 > 0 ? 0 : 1) != 0) {
      i1 = 1 - (paramInt1 - 1) * paramInt3;
    } else if ((paramInt3 == 1 ? 0 : 1) != 0) {
      i1 = 1;
    }
    int i2;
    int i3;
    if (Lsame.lsame(paramString, "U"))
    {
      if ((paramInt3 != 1 ? 0 : 1) != 0)
      {
        m = 1;
        for (i2 = paramInt1 - 1 + 1; i2 > 0; i2--)
        {
          if ((paramArrayOfFloat1[(m - 1 + paramInt2)] == 0.0F ? 0 : 1) != 0)
          {
            f = paramFloat * paramArrayOfFloat1[(m - 1 + paramInt2)];
            i = 1;
            for (i3 = m - 1 + 1; i3 > 0; i3--)
            {
              paramArrayOfFloat2[(i - 1 + (m - 1) * paramInt5 + paramInt4)] += paramArrayOfFloat1[(i - 1 + paramInt2)] * f;
              i += 1;
            }
          }
          m += 1;
        }
      }
      else
      {
        n = i1;
        m = 1;
        for (i2 = paramInt1 - 1 + 1; i2 > 0; i2--)
        {
          if ((paramArrayOfFloat1[(n - 1 + paramInt2)] == 0.0F ? 0 : 1) != 0)
          {
            f = paramFloat * paramArrayOfFloat1[(n - 1 + paramInt2)];
            k = i1;
            i = 1;
            for (i3 = m - 1 + 1; i3 > 0; i3--)
            {
              paramArrayOfFloat2[(i - 1 + (m - 1) * paramInt5 + paramInt4)] += paramArrayOfFloat1[(k - 1 + paramInt2)] * f;
              k += paramInt3;
              i += 1;
            }
          }
          n += paramInt3;
          m += 1;
        }
      }
    }
    else if ((paramInt3 != 1 ? 0 : 1) != 0)
    {
      m = 1;
      for (i2 = paramInt1 - 1 + 1; i2 > 0; i2--)
      {
        if ((paramArrayOfFloat1[(m - 1 + paramInt2)] == 0.0F ? 0 : 1) != 0)
        {
          f = paramFloat * paramArrayOfFloat1[(m - 1 + paramInt2)];
          i = m;
          for (i3 = paramInt1 - m + 1; i3 > 0; i3--)
          {
            paramArrayOfFloat2[(i - 1 + (m - 1) * paramInt5 + paramInt4)] += paramArrayOfFloat1[(i - 1 + paramInt2)] * f;
            i += 1;
          }
        }
        m += 1;
      }
    }
    else
    {
      n = i1;
      m = 1;
      for (i2 = paramInt1 - 1 + 1; i2 > 0; i2--)
      {
        if ((paramArrayOfFloat1[(n - 1 + paramInt2)] == 0.0F ? 0 : 1) != 0)
        {
          f = paramFloat * paramArrayOfFloat1[(n - 1 + paramInt2)];
          k = n;
          i = m;
          for (i3 = paramInt1 - m + 1; i3 > 0; i3--)
          {
            paramArrayOfFloat2[(i - 1 + (m - 1) * paramInt5 + paramInt4)] += paramArrayOfFloat1[(k - 1 + paramInt2)] * f;
            k += paramInt3;
            i += 1;
          }
        }
        n += paramInt3;
        m += 1;
      }
    }
  }
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.blas.Ssyr
 * JD-Core Version:    0.7.0.1
 */