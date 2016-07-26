package org.netlib.lapack;

import org.netlib.err.Xerbla;
import org.netlib.util.intW;

public final class Dgbsv
{
  public static void dgbsv(int paramInt1, int paramInt2, int paramInt3, int paramInt4, double[] paramArrayOfDouble1, int paramInt5, int paramInt6, int[] paramArrayOfInt, int paramInt7, double[] paramArrayOfDouble2, int paramInt8, int paramInt9, intW paramintW)
  {
    paramintW.val = 0;
    if ((paramInt1 >= 0 ? 0 : 1) != 0) {
      paramintW.val = -1;
    } else if ((paramInt2 >= 0 ? 0 : 1) != 0) {
      paramintW.val = -2;
    } else if ((paramInt3 >= 0 ? 0 : 1) != 0) {
      paramintW.val = -3;
    } else if ((paramInt4 >= 0 ? 0 : 1) != 0) {
      paramintW.val = -4;
    } else if ((paramInt6 >= 2 * paramInt2 + paramInt3 + 1 ? 0 : 1) != 0) {
      paramintW.val = -6;
    } else if ((paramInt9 >= Math.max(paramInt1, 1) ? 0 : 1) != 0) {
      paramintW.val = -9;
    }
    if ((paramintW.val == 0 ? 0 : 1) != 0)
    {
      Xerbla.xerbla("DGBSV ", -paramintW.val);
      return;
    }
    Dgbtrf.dgbtrf(paramInt1, paramInt1, paramInt2, paramInt3, paramArrayOfDouble1, paramInt5, paramInt6, paramArrayOfInt, paramInt7, paramintW);
    if ((paramintW.val != 0 ? 0 : 1) != 0) {
      Dgbtrs.dgbtrs("No transpose", paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfDouble1, paramInt5, paramInt6, paramArrayOfInt, paramInt7, paramArrayOfDouble2, paramInt8, paramInt9, paramintW);
    }
  }
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.lapack.Dgbsv
 * JD-Core Version:    0.7.0.1
 */