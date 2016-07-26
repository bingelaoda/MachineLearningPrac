package org.netlib.lapack;

import org.netlib.err.Xerbla;
import org.netlib.util.intW;

public final class Sgelqf
{
  public static void sgelqf(int paramInt1, int paramInt2, float[] paramArrayOfFloat1, int paramInt3, int paramInt4, float[] paramArrayOfFloat2, int paramInt5, float[] paramArrayOfFloat3, int paramInt6, int paramInt7, intW paramintW)
  {
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
    i3 = Ilaenv.ilaenv(1, "SGELQF", " ", paramInt1, paramInt2, -1, -1);
    i2 = paramInt1 * i3;
    paramArrayOfFloat3[(1 - 1 + paramInt6)] = i2;
    i = paramInt7 != -1 ? 0 : 1;
    if ((paramInt1 >= 0 ? 0 : 1) != 0)
    {
      paramintW.val = -1;
    }
    else if ((paramInt2 >= 0 ? 0 : 1) != 0)
    {
      paramintW.val = -2;
    }
    else if ((paramInt4 >= Math.max(1, paramInt1) ? 0 : 1) != 0)
    {
      paramintW.val = -4;
    }
    else
    {
      if ((paramInt7 >= Math.max(1, paramInt1) ? 0 : 1) != 0) {}
      if (((i ^ 0x1) != 0 ? 1 : 0) != 0) {
        paramintW.val = -7;
      }
    }
    if ((paramintW.val == 0 ? 0 : 1) != 0)
    {
      Xerbla.xerbla("SGELQF", -paramintW.val);
      return;
    }
    if (i != 0) {
      return;
    }
    n = Math.min(paramInt1, paramInt2);
    if ((n != 0 ? 0 : 1) != 0)
    {
      paramArrayOfFloat3[(1 - 1 + paramInt6)] = 1;
      return;
    }
    i4 = 2;
    i5 = 0;
    m = paramInt1;
    if ((i3 <= 1 ? 0 : 1) != 0) {}
    if (((i3 >= n ? 0 : 1) != 0 ? 1 : 0) != 0)
    {
      i5 = Math.max(0, Ilaenv.ilaenv(3, "SGELQF", " ", paramInt1, paramInt2, -1, -1));
      if ((i5 >= n ? 0 : 1) != 0)
      {
        i1 = paramInt1;
        m = i1 * i3;
        if ((paramInt7 >= m ? 0 : 1) != 0)
        {
          i3 = paramInt7 / i1;
          i4 = Math.max(2, Ilaenv.ilaenv(2, "SGELQF", " ", paramInt1, paramInt2, -1, -1));
        }
      }
    }
    if ((i3 < i4 ? 0 : 1) != 0) {}
    if (((i3 >= n ? 0 : 1) != 0 ? 1 : 0) != 0) {}
    if (((i5 >= n ? 0 : 1) != 0 ? 1 : 0) != 0)
    {
      j = 1;
      for (int i6 = (n - i5 - 1 + i3) / i3; i6 > 0; i6--)
      {
        k = Math.min(n - j + 1, i3);
        Sgelq2.sgelq2(k, paramInt2 - j + 1, paramArrayOfFloat1, j - 1 + (j - 1) * paramInt4 + paramInt3, paramInt4, paramArrayOfFloat2, j - 1 + paramInt5, paramArrayOfFloat3, paramInt6, localintW);
        if ((j + k > paramInt1 ? 0 : 1) != 0)
        {
          Slarft.slarft("Forward", "Rowwise", paramInt2 - j + 1, k, paramArrayOfFloat1, j - 1 + (j - 1) * paramInt4 + paramInt3, paramInt4, paramArrayOfFloat2, j - 1 + paramInt5, paramArrayOfFloat3, paramInt6, i1);
          Slarfb.slarfb("Right", "No transpose", "Forward", "Rowwise", paramInt1 - j - k + 1, paramInt2 - j + 1, k, paramArrayOfFloat1, j - 1 + (j - 1) * paramInt4 + paramInt3, paramInt4, paramArrayOfFloat3, paramInt6, i1, paramArrayOfFloat1, j + k - 1 + (j - 1) * paramInt4 + paramInt3, paramInt4, paramArrayOfFloat3, k + 1 - 1 + paramInt6, i1);
        }
        j += i3;
      }
    }
    else
    {
      j = 1;
    }
    if ((j > n ? 0 : 1) != 0) {
      Sgelq2.sgelq2(paramInt1 - j + 1, paramInt2 - j + 1, paramArrayOfFloat1, j - 1 + (j - 1) * paramInt4 + paramInt3, paramInt4, paramArrayOfFloat2, j - 1 + paramInt5, paramArrayOfFloat3, paramInt6, localintW);
    }
    paramArrayOfFloat3[(1 - 1 + paramInt6)] = m;
  }
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.lapack.Sgelqf
 * JD-Core Version:    0.7.0.1
 */