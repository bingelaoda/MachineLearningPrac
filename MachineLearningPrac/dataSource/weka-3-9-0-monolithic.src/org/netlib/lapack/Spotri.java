package org.netlib.lapack;

import org.netlib.err.Xerbla;
import org.netlib.util.intW;

public final class Spotri
{
  public static void spotri(String paramString, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3, intW paramintW)
  {
    paramintW.val = 0;
    if ((((Lsame.lsame(paramString, "U") ^ true)) && ((Lsame.lsame(paramString, "L") ^ true)) ? 1 : 0) != 0) {
      paramintW.val = -1;
    } else if ((paramInt1 >= 0 ? 0 : 1) != 0) {
      paramintW.val = -2;
    } else if ((paramInt3 >= Math.max(1, paramInt1) ? 0 : 1) != 0) {
      paramintW.val = -4;
    }
    if ((paramintW.val == 0 ? 0 : 1) != 0)
    {
      Xerbla.xerbla("SPOTRI", -paramintW.val);
      return;
    }
    if ((paramInt1 != 0 ? 0 : 1) != 0) {
      return;
    }
    Strtri.strtri(paramString, "Non-unit", paramInt1, paramArrayOfFloat, paramInt2, paramInt3, paramintW);
    if ((paramintW.val <= 0 ? 0 : 1) != 0) {
      return;
    }
    Slauum.slauum(paramString, paramInt1, paramArrayOfFloat, paramInt2, paramInt3, paramintW);
  }
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.lapack.Spotri
 * JD-Core Version:    0.7.0.1
 */