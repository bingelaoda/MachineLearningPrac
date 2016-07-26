package org.netlib.lapack;

import org.netlib.err.Xerbla;
import org.netlib.util.intW;

public final class Dppsv
{
  public static void dppsv(String paramString, int paramInt1, int paramInt2, double[] paramArrayOfDouble1, int paramInt3, double[] paramArrayOfDouble2, int paramInt4, int paramInt5, intW paramintW)
  {
    paramintW.val = 0;
    if ((((Lsame.lsame(paramString, "U") ^ true)) && ((Lsame.lsame(paramString, "L") ^ true)) ? 1 : 0) != 0) {
      paramintW.val = -1;
    } else if ((paramInt1 >= 0 ? 0 : 1) != 0) {
      paramintW.val = -2;
    } else if ((paramInt2 >= 0 ? 0 : 1) != 0) {
      paramintW.val = -3;
    } else if ((paramInt5 >= Math.max(1, paramInt1) ? 0 : 1) != 0) {
      paramintW.val = -6;
    }
    if ((paramintW.val == 0 ? 0 : 1) != 0)
    {
      Xerbla.xerbla("DPPSV ", -paramintW.val);
      return;
    }
    Dpptrf.dpptrf(paramString, paramInt1, paramArrayOfDouble1, paramInt3, paramintW);
    if ((paramintW.val != 0 ? 0 : 1) != 0) {
      Dpptrs.dpptrs(paramString, paramInt1, paramInt2, paramArrayOfDouble1, paramInt3, paramArrayOfDouble2, paramInt4, paramInt5, paramintW);
    }
  }
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.lapack.Dppsv
 * JD-Core Version:    0.7.0.1
 */