package org.netlib.lapack;

import org.netlib.err.Xerbla;
import org.netlib.util.intW;

public final class Dormhr
{
  public static void dormhr(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, double[] paramArrayOfDouble1, int paramInt5, int paramInt6, double[] paramArrayOfDouble2, int paramInt7, double[] paramArrayOfDouble3, int paramInt8, int paramInt9, double[] paramArrayOfDouble4, int paramInt10, int paramInt11, intW paramintW)
  {
    boolean bool = false;
    int i = 0;
    int j = 0;
    int k = 0;
    intW localintW = new intW(0);
    int m = 0;
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    int i4 = 0;
    int i5 = 0;
    paramintW.val = 0;
    i2 = paramInt4 - paramInt3;
    bool = Lsame.lsame(paramString1, "L");
    i = paramInt11 != -1 ? 0 : 1;
    if (bool)
    {
      i4 = paramInt1;
      i5 = paramInt2;
    }
    else
    {
      i4 = paramInt2;
      i5 = paramInt1;
    }
    if ((((bool ^ true)) && ((Lsame.lsame(paramString1, "R") ^ true)) ? 1 : 0) != 0)
    {
      paramintW.val = -1;
    }
    else if ((((Lsame.lsame(paramString2, "N") ^ true)) && ((Lsame.lsame(paramString2, "T") ^ true)) ? 1 : 0) != 0)
    {
      paramintW.val = -2;
    }
    else if ((paramInt1 >= 0 ? 0 : 1) != 0)
    {
      paramintW.val = -3;
    }
    else if ((paramInt2 >= 0 ? 0 : 1) != 0)
    {
      paramintW.val = -4;
    }
    else
    {
      if ((paramInt3 >= 1 ? 0 : 1) == 0) {}
      if (((paramInt3 <= Math.max(1, i4) ? 0 : 1) == 0 ? 0 : 1) != 0)
      {
        paramintW.val = -5;
      }
      else
      {
        if ((paramInt4 >= Math.min(paramInt3, i4) ? 0 : 1) == 0) {}
        if (((paramInt4 <= i4 ? 0 : 1) == 0 ? 0 : 1) != 0)
        {
          paramintW.val = -6;
        }
        else if ((paramInt6 >= Math.max(1, i4) ? 0 : 1) != 0)
        {
          paramintW.val = -8;
        }
        else if ((paramInt9 >= Math.max(1, paramInt1) ? 0 : 1) != 0)
        {
          paramintW.val = -11;
        }
        else
        {
          if ((paramInt11 >= Math.max(1, i5) ? 0 : 1) != 0) {}
          if (((i ^ 0x1) != 0 ? 1 : 0) != 0) {
            paramintW.val = -13;
          }
        }
      }
    }
    if ((paramintW.val != 0 ? 0 : 1) != 0)
    {
      if (bool) {
        i1 = Ilaenv.ilaenv(1, "DORMQR", paramString1 + paramString2, i2, paramInt2, i2, -1);
      } else {
        i1 = Ilaenv.ilaenv(1, "DORMQR", paramString1 + paramString2, paramInt1, i2, i2, -1);
      }
      m = Math.max(1, i5) * i1;
      paramArrayOfDouble4[(1 - 1 + paramInt10)] = m;
    }
    if ((paramintW.val == 0 ? 0 : 1) != 0)
    {
      Xerbla.xerbla("DORMHR", -paramintW.val);
      return;
    }
    if (i != 0) {
      return;
    }
    if ((paramInt1 != 0 ? 0 : 1) == 0) {}
    if (((paramInt2 != 0 ? 0 : 1) == 0 ? 0 : 1) == 0) {}
    if (((i2 != 0 ? 0 : 1) == 0 ? 0 : 1) != 0)
    {
      paramArrayOfDouble4[(1 - 1 + paramInt10)] = 1;
      return;
    }
    if (bool)
    {
      n = i2;
      i3 = paramInt2;
      j = paramInt3 + 1;
      k = 1;
    }
    else
    {
      n = paramInt1;
      i3 = i2;
      j = 1;
      k = paramInt3 + 1;
    }
    Dormqr.dormqr(paramString1, paramString2, n, i3, i2, paramArrayOfDouble1, paramInt3 + 1 - 1 + (paramInt3 - 1) * paramInt6 + paramInt5, paramInt6, paramArrayOfDouble2, paramInt3 - 1 + paramInt7, paramArrayOfDouble3, j - 1 + (k - 1) * paramInt9 + paramInt8, paramInt9, paramArrayOfDouble4, paramInt10, paramInt11, localintW);
    paramArrayOfDouble4[(1 - 1 + paramInt10)] = m;
  }
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.lapack.Dormhr
 * JD-Core Version:    0.7.0.1
 */