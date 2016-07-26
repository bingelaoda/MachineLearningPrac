package org.netlib.lapack;

import org.netlib.blas.Dsyr2k;
import org.netlib.err.Xerbla;
import org.netlib.util.intW;

public final class Dsytrd
{
  public static void dsytrd(String paramString, int paramInt1, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2, int paramInt4, double[] paramArrayOfDouble3, int paramInt5, double[] paramArrayOfDouble4, int paramInt6, double[] paramArrayOfDouble5, int paramInt7, int paramInt8, intW paramintW)
  {
    int i = 0;
    boolean bool = false;
    int j = 0;
    intW localintW = new intW(0);
    int k = 0;
    int m = 0;
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    int i4 = 0;
    int i5 = 0;
    paramintW.val = 0;
    bool = Lsame.lsame(paramString, "U");
    i = paramInt8 != -1 ? 0 : 1;
    if ((((bool ^ true)) && ((Lsame.lsame(paramString, "L") ^ true)) ? 1 : 0) != 0)
    {
      paramintW.val = -1;
    }
    else if ((paramInt1 >= 0 ? 0 : 1) != 0)
    {
      paramintW.val = -2;
    }
    else if ((paramInt3 >= Math.max(1, paramInt1) ? 0 : 1) != 0)
    {
      paramintW.val = -4;
    }
    else
    {
      if ((paramInt8 >= 1 ? 0 : 1) != 0) {}
      if (((i ^ 0x1) != 0 ? 1 : 0) != 0) {
        paramintW.val = -9;
      }
    }
    if ((paramintW.val != 0 ? 0 : 1) != 0)
    {
      i3 = Ilaenv.ilaenv(1, "DSYTRD", paramString, paramInt1, -1, -1, -1);
      i2 = paramInt1 * i3;
      paramArrayOfDouble5[(1 - 1 + paramInt7)] = i2;
    }
    if ((paramintW.val == 0 ? 0 : 1) != 0)
    {
      Xerbla.xerbla("DSYTRD", -paramintW.val);
      return;
    }
    if (i != 0) {
      return;
    }
    if ((paramInt1 != 0 ? 0 : 1) != 0)
    {
      paramArrayOfDouble5[(1 - 1 + paramInt7)] = 1;
      return;
    }
    i5 = paramInt1;
    k = 1;
    if ((i3 <= 1 ? 0 : 1) != 0) {}
    if (((i3 >= paramInt1 ? 0 : 1) != 0 ? 1 : 0) != 0)
    {
      i5 = Math.max(i3, Ilaenv.ilaenv(3, "DSYTRD", paramString, paramInt1, -1, -1, -1));
      if ((i5 >= paramInt1 ? 0 : 1) != 0)
      {
        i1 = paramInt1;
        k = i1 * i3;
        if ((paramInt8 >= k ? 0 : 1) != 0)
        {
          i3 = Math.max(paramInt8 / i1, 1);
          i4 = Ilaenv.ilaenv(2, "DSYTRD", paramString, paramInt1, -1, -1, -1);
          if ((i3 >= i4 ? 0 : 1) != 0) {
            i5 = paramInt1;
          }
        }
      }
      else
      {
        i5 = paramInt1;
      }
    }
    else
    {
      i3 = 1;
    }
    int i6;
    int i7;
    if (bool)
    {
      n = paramInt1 - (paramInt1 - i5 + i3 - 1) / i3 * i3;
      j = paramInt1 - i3 + 1;
      for (i6 = (n + 1 - (paramInt1 - i3 + 1) + -i3) / -i3; i6 > 0; i6--)
      {
        Dlatrd.dlatrd(paramString, j + i3 - 1, i3, paramArrayOfDouble1, paramInt2, paramInt3, paramArrayOfDouble3, paramInt5, paramArrayOfDouble4, paramInt6, paramArrayOfDouble5, paramInt7, i1);
        Dsyr2k.dsyr2k(paramString, "No transpose", j - 1, i3, -1.0D, paramArrayOfDouble1, 1 - 1 + (j - 1) * paramInt3 + paramInt2, paramInt3, paramArrayOfDouble5, paramInt7, i1, 1.0D, paramArrayOfDouble1, paramInt2, paramInt3);
        m = j;
        for (i7 = j + i3 - 1 - j + 1; i7 > 0; i7--)
        {
          paramArrayOfDouble1[(m - 1 - 1 + (m - 1) * paramInt3 + paramInt2)] = paramArrayOfDouble3[(m - 1 - 1 + paramInt5)];
          paramArrayOfDouble2[(m - 1 + paramInt4)] = paramArrayOfDouble1[(m - 1 + (m - 1) * paramInt3 + paramInt2)];
          m += 1;
        }
        j += -i3;
      }
      Dsytd2.dsytd2(paramString, n, paramArrayOfDouble1, paramInt2, paramInt3, paramArrayOfDouble2, paramInt4, paramArrayOfDouble3, paramInt5, paramArrayOfDouble4, paramInt6, localintW);
    }
    else
    {
      j = 1;
      for (i6 = (paramInt1 - i5 - 1 + i3) / i3; i6 > 0; i6--)
      {
        Dlatrd.dlatrd(paramString, paramInt1 - j + 1, i3, paramArrayOfDouble1, j - 1 + (j - 1) * paramInt3 + paramInt2, paramInt3, paramArrayOfDouble3, j - 1 + paramInt5, paramArrayOfDouble4, j - 1 + paramInt6, paramArrayOfDouble5, paramInt7, i1);
        Dsyr2k.dsyr2k(paramString, "No transpose", paramInt1 - j - i3 + 1, i3, -1.0D, paramArrayOfDouble1, j + i3 - 1 + (j - 1) * paramInt3 + paramInt2, paramInt3, paramArrayOfDouble5, i3 + 1 - 1 + paramInt7, i1, 1.0D, paramArrayOfDouble1, j + i3 - 1 + (j + i3 - 1) * paramInt3 + paramInt2, paramInt3);
        m = j;
        for (i7 = j + i3 - 1 - j + 1; i7 > 0; i7--)
        {
          paramArrayOfDouble1[(m + 1 - 1 + (m - 1) * paramInt3 + paramInt2)] = paramArrayOfDouble3[(m - 1 + paramInt5)];
          paramArrayOfDouble2[(m - 1 + paramInt4)] = paramArrayOfDouble1[(m - 1 + (m - 1) * paramInt3 + paramInt2)];
          m += 1;
        }
        j += i3;
      }
      Dsytd2.dsytd2(paramString, paramInt1 - j + 1, paramArrayOfDouble1, j - 1 + (j - 1) * paramInt3 + paramInt2, paramInt3, paramArrayOfDouble2, j - 1 + paramInt4, paramArrayOfDouble3, j - 1 + paramInt5, paramArrayOfDouble4, j - 1 + paramInt6, localintW);
    }
    paramArrayOfDouble5[(1 - 1 + paramInt7)] = i2;
  }
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.lapack.Dsytrd
 * JD-Core Version:    0.7.0.1
 */