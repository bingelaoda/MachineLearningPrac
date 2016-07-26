package org.netlib.lapack;

import org.netlib.err.Xerbla;
import org.netlib.util.intW;

public final class Sptsv
{
  public static void sptsv(int paramInt1, int paramInt2, float[] paramArrayOfFloat1, int paramInt3, float[] paramArrayOfFloat2, int paramInt4, float[] paramArrayOfFloat3, int paramInt5, int paramInt6, intW paramintW)
  {
    paramintW.val = 0;
    if ((paramInt1 >= 0 ? 0 : 1) != 0) {
      paramintW.val = -1;
    } else if ((paramInt2 >= 0 ? 0 : 1) != 0) {
      paramintW.val = -2;
    } else if ((paramInt6 >= Math.max(1, paramInt1) ? 0 : 1) != 0) {
      paramintW.val = -6;
    }
    if ((paramintW.val == 0 ? 0 : 1) != 0)
    {
      Xerbla.xerbla("SPTSV ", -paramintW.val);
      return;
    }
    Spttrf.spttrf(paramInt1, paramArrayOfFloat1, paramInt3, paramArrayOfFloat2, paramInt4, paramintW);
    if ((paramintW.val != 0 ? 0 : 1) != 0) {
      Spttrs.spttrs(paramInt1, paramInt2, paramArrayOfFloat1, paramInt3, paramArrayOfFloat2, paramInt4, paramArrayOfFloat3, paramInt5, paramInt6, paramintW);
    }
  }
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.lapack.Sptsv
 * JD-Core Version:    0.7.0.1
 */