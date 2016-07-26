package org.netlib.lapack;

import org.netlib.blas.Ddot;
import org.netlib.blas.Dgemv;
import org.netlib.blas.Dscal;
import org.netlib.err.Xerbla;
import org.netlib.util.intW;

public final class Dpotf2
{
  public static void dpotf2(String paramString, int paramInt1, double[] paramArrayOfDouble, int paramInt2, int paramInt3, intW paramintW)
  {
    boolean bool = false;
    int i = 0;
    double d = 0.0D;
    paramintW.val = 0;
    bool = Lsame.lsame(paramString, "U");
    if ((((bool ^ true)) && ((Lsame.lsame(paramString, "L") ^ true)) ? 1 : 0) != 0) {
      paramintW.val = -1;
    } else if ((paramInt1 >= 0 ? 0 : 1) != 0) {
      paramintW.val = -2;
    } else if ((paramInt3 >= Math.max(1, paramInt1) ? 0 : 1) != 0) {
      paramintW.val = -4;
    }
    if ((paramintW.val == 0 ? 0 : 1) != 0)
    {
      Xerbla.xerbla("DPOTF2", -paramintW.val);
      return;
    }
    if ((paramInt1 != 0 ? 0 : 1) != 0) {
      return;
    }
    int j;
    if (bool)
    {
      i = 1;
      for (j = paramInt1 - 1 + 1; j > 0; j--)
      {
        d = paramArrayOfDouble[(i - 1 + (i - 1) * paramInt3 + paramInt2)] - Ddot.ddot(i - 1, paramArrayOfDouble, 1 - 1 + (i - 1) * paramInt3 + paramInt2, 1, paramArrayOfDouble, 1 - 1 + (i - 1) * paramInt3 + paramInt2, 1);
        if ((d > 0.0D ? 0 : 1) != 0)
        {
          paramArrayOfDouble[(i - 1 + (i - 1) * paramInt3 + paramInt2)] = d;
          break;
        }
        d = Math.sqrt(d);
        paramArrayOfDouble[(i - 1 + (i - 1) * paramInt3 + paramInt2)] = d;
        if ((i >= paramInt1 ? 0 : 1) != 0)
        {
          Dgemv.dgemv("Transpose", i - 1, paramInt1 - i, -1.0D, paramArrayOfDouble, 1 - 1 + (i + 1 - 1) * paramInt3 + paramInt2, paramInt3, paramArrayOfDouble, 1 - 1 + (i - 1) * paramInt3 + paramInt2, 1, 1.0D, paramArrayOfDouble, i - 1 + (i + 1 - 1) * paramInt3 + paramInt2, paramInt3);
          Dscal.dscal(paramInt1 - i, 1.0D / d, paramArrayOfDouble, i - 1 + (i + 1 - 1) * paramInt3 + paramInt2, paramInt3);
        }
        i += 1;
      }
    }
    else
    {
      i = 1;
      for (j = paramInt1 - 1 + 1; j > 0; j--)
      {
        d = paramArrayOfDouble[(i - 1 + (i - 1) * paramInt3 + paramInt2)] - Ddot.ddot(i - 1, paramArrayOfDouble, i - 1 + (1 - 1) * paramInt3 + paramInt2, paramInt3, paramArrayOfDouble, i - 1 + (1 - 1) * paramInt3 + paramInt2, paramInt3);
        if ((d > 0.0D ? 0 : 1) != 0)
        {
          paramArrayOfDouble[(i - 1 + (i - 1) * paramInt3 + paramInt2)] = d;
          break;
        }
        d = Math.sqrt(d);
        paramArrayOfDouble[(i - 1 + (i - 1) * paramInt3 + paramInt2)] = d;
        if ((i >= paramInt1 ? 0 : 1) != 0)
        {
          Dgemv.dgemv("No transpose", paramInt1 - i, i - 1, -1.0D, paramArrayOfDouble, i + 1 - 1 + (1 - 1) * paramInt3 + paramInt2, paramInt3, paramArrayOfDouble, i - 1 + (1 - 1) * paramInt3 + paramInt2, paramInt3, 1.0D, paramArrayOfDouble, i + 1 - 1 + (i - 1) * paramInt3 + paramInt2, 1);
          Dscal.dscal(paramInt1 - i, 1.0D / d, paramArrayOfDouble, i + 1 - 1 + (i - 1) * paramInt3 + paramInt2, 1);
        }
        i += 1;
      }
    }
    return;
    paramintW.val = i;
  }
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.lapack.Dpotf2
 * JD-Core Version:    0.7.0.1
 */