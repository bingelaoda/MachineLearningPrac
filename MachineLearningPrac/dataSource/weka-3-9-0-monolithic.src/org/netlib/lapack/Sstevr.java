package org.netlib.lapack;

import org.netlib.blas.Scopy;
import org.netlib.blas.Sscal;
import org.netlib.blas.Sswap;
import org.netlib.err.Xerbla;
import org.netlib.util.booleanW;
import org.netlib.util.intW;

public final class Sstevr
{
  public static void sstevr(String paramString1, String paramString2, int paramInt1, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3, float paramFloat1, float paramFloat2, int paramInt4, int paramInt5, float paramFloat3, intW paramintW1, float[] paramArrayOfFloat3, int paramInt6, float[] paramArrayOfFloat4, int paramInt7, int paramInt8, int[] paramArrayOfInt1, int paramInt9, float[] paramArrayOfFloat5, int paramInt10, int paramInt11, int[] paramArrayOfInt2, int paramInt12, int paramInt13, intW paramintW2)
  {
    boolean bool1 = false;
    boolean bool2 = false;
    int i = 0;
    int j = 0;
    boolean bool3 = false;
    boolean bool4 = false;
    booleanW localbooleanW = new booleanW(false);
    String str = new String(" ");
    int k = 0;
    int m = 0;
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    int i4 = 0;
    int i5 = 0;
    int i6 = 0;
    int i7 = 0;
    int i8 = 0;
    int i9 = 0;
    intW localintW = new intW(0);
    float f1 = 0.0F;
    float f2 = 0.0F;
    float f3 = 0.0F;
    float f4 = 0.0F;
    float f5 = 0.0F;
    float f6 = 0.0F;
    float f7 = 0.0F;
    float f8 = 0.0F;
    float f9 = 0.0F;
    float f10 = 0.0F;
    float f11 = 0.0F;
    m = Ilaenv.ilaenv(10, "SSTEVR", "N", 1, 2, 3, 4);
    bool4 = Lsame.lsame(paramString1, "V");
    bool1 = Lsame.lsame(paramString2, "A");
    bool3 = Lsame.lsame(paramString2, "V");
    bool2 = Lsame.lsame(paramString2, "I");
    if ((paramInt11 != -1 ? 0 : 1) == 0) {}
    j = (paramInt13 != -1 ? 0 : 1) == 0 ? 0 : 1;
    i9 = Math.max(1, 20 * paramInt1);
    i8 = Math.max(1, 10 * paramInt1);
    paramintW2.val = 0;
    if ((((!bool4) && (!Lsame.lsame(paramString1, "N")) ? 0 : 1) ^ 0x1) != 0)
    {
      paramintW2.val = -1;
    }
    else
    {
      if (((!bool1) && (!bool3) ? 0 : 1) == 0) {}
      if (((!bool2 ? 0 : 1) ^ 0x1) != 0)
      {
        paramintW2.val = -2;
      }
      else if ((paramInt1 >= 0 ? 0 : 1) != 0)
      {
        paramintW2.val = -3;
      }
      else if (bool3)
      {
        if ((paramInt1 <= 0 ? 0 : 1) != 0) {}
        if (((paramFloat2 > paramFloat1 ? 0 : 1) != 0 ? 1 : 0) != 0) {
          paramintW2.val = -7;
        }
      }
      else if (bool2)
      {
        if ((paramInt4 >= 1 ? 0 : 1) == 0) {}
        if (((paramInt4 <= Math.max(1, paramInt1) ? 0 : 1) == 0 ? 0 : 1) != 0)
        {
          paramintW2.val = -8;
        }
        else
        {
          if ((paramInt5 >= Math.min(paramInt1, paramInt4) ? 0 : 1) == 0) {}
          if (((paramInt5 <= paramInt1 ? 0 : 1) == 0 ? 0 : 1) != 0) {
            paramintW2.val = -9;
          }
        }
      }
    }
    if ((paramintW2.val != 0 ? 0 : 1) != 0)
    {
      if ((paramInt8 >= 1 ? 0 : 1) == 0) {
        if (!bool4) {}
      }
      if ((((paramInt8 >= paramInt1 ? 0 : 1) != 0 ? 1 : 0) == 0 ? 0 : 1) != 0) {
        paramintW2.val = -14;
      }
    }
    if ((paramintW2.val != 0 ? 0 : 1) != 0)
    {
      paramArrayOfFloat5[(1 - 1 + paramInt10)] = i9;
      paramArrayOfInt2[(1 - 1 + paramInt12)] = i8;
      if ((paramInt11 >= i9 ? 0 : 1) != 0) {}
      if (((j ^ 0x1) != 0 ? 1 : 0) != 0)
      {
        paramintW2.val = -17;
      }
      else
      {
        if ((paramInt13 >= i8 ? 0 : 1) != 0) {}
        if (((j ^ 0x1) != 0 ? 1 : 0) != 0) {
          paramintW2.val = -19;
        }
      }
    }
    if ((paramintW2.val == 0 ? 0 : 1) != 0)
    {
      Xerbla.xerbla("SSTEVR", -paramintW2.val);
      return;
    }
    if (j != 0) {
      return;
    }
    paramintW1.val = 0;
    if ((paramInt1 != 0 ? 0 : 1) != 0) {
      return;
    }
    if ((paramInt1 != 1 ? 0 : 1) != 0)
    {
      if (((!bool1) && (!bool2) ? 0 : 1) != 0)
      {
        paramintW1.val = 1;
        paramArrayOfFloat3[(1 - 1 + paramInt6)] = paramArrayOfFloat1[(1 - 1 + paramInt2)];
      }
      else
      {
        if ((paramFloat1 >= paramArrayOfFloat1[(1 - 1 + paramInt2)] ? 0 : 1) != 0) {}
        if (((paramFloat2 < paramArrayOfFloat1[(1 - 1 + paramInt2)] ? 0 : 1) != 0 ? 1 : 0) != 0)
        {
          paramintW1.val = 1;
          paramArrayOfFloat3[(1 - 1 + paramInt6)] = paramArrayOfFloat1[(1 - 1 + paramInt2)];
        }
      }
      if (bool4) {
        paramArrayOfFloat4[(1 - 1 + (1 - 1) * paramInt8 + paramInt7)] = 1.0F;
      }
      return;
    }
    f5 = Slamch.slamch("Safe minimum");
    f2 = Slamch.slamch("Precision");
    f7 = f5 / f2;
    f1 = 1.0F / f7;
    f4 = (float)Math.sqrt(f7);
    f3 = Math.min((float)Math.sqrt(f1), 1.0F / (float)Math.sqrt((float)Math.sqrt(f5)));
    i5 = 0;
    f10 = paramFloat1;
    f11 = paramFloat2;
    f9 = Slanst.slanst("M", paramInt1, paramArrayOfFloat1, paramInt2, paramArrayOfFloat2, paramInt3);
    if ((f9 <= 0.0F ? 0 : 1) != 0) {}
    if (((f9 >= f4 ? 0 : 1) != 0 ? 1 : 0) != 0)
    {
      i5 = 1;
      f6 = f4 / f9;
    }
    else if ((f9 <= f3 ? 0 : 1) != 0)
    {
      i5 = 1;
      f6 = f3 / f9;
    }
    if ((i5 != 1 ? 0 : 1) != 0)
    {
      Sscal.sscal(paramInt1, f6, paramArrayOfFloat1, paramInt2, 1);
      Sscal.sscal(paramInt1 - 1, f6, paramArrayOfFloat2, 1 - 1 + paramInt3, 1);
      if (bool3)
      {
        f10 = paramFloat1 * f6;
        f11 = paramFloat2 * f6;
      }
    }
    i1 = 1;
    i3 = i1 + paramInt1;
    i2 = i3 + paramInt1;
    i4 = i3 + paramInt1;
    i = 0;
    if (bool2)
    {
      if ((paramInt4 != 1 ? 0 : 1) != 0) {}
      if (((paramInt5 != paramInt1 ? 0 : 1) != 0 ? 1 : 0) != 0) {
        i = 1;
      }
    }
    if (((!bool1) && (i == 0) ? 0 : 1) != 0) {}
    if (((m != 1 ? 0 : 1) != 0 ? 1 : 0) != 0)
    {
      Scopy.scopy(paramInt1 - 1, paramArrayOfFloat2, 1 - 1 + paramInt3, 1, paramArrayOfFloat5, 1 - 1 + paramInt10, 1);
      if ((bool4 ^ true))
      {
        Scopy.scopy(paramInt1, paramArrayOfFloat1, paramInt2, 1, paramArrayOfFloat3, paramInt6, 1);
        Ssterf.ssterf(paramInt1, paramArrayOfFloat3, paramInt6, paramArrayOfFloat5, paramInt10, paramintW2);
      }
      else
      {
        Scopy.scopy(paramInt1, paramArrayOfFloat1, paramInt2, 1, paramArrayOfFloat5, paramInt1 + 1 - 1 + paramInt10, 1);
        if ((paramFloat3 > 2.0F * paramInt1 * f2 ? 0 : 1) != 0) {
          localbooleanW.val = true;
        } else {
          localbooleanW.val = false;
        }
        Sstemr.sstemr(paramString1, "A", paramInt1, paramArrayOfFloat5, paramInt1 + 1 - 1 + paramInt10, paramArrayOfFloat5, paramInt10, paramFloat1, paramFloat2, paramInt4, paramInt5, paramintW1, paramArrayOfFloat3, paramInt6, paramArrayOfFloat4, paramInt7, paramInt8, paramInt1, paramArrayOfInt1, paramInt9, localbooleanW, paramArrayOfFloat5, 2 * paramInt1 + 1 - 1 + paramInt10, paramInt11 - 2 * paramInt1, paramArrayOfInt2, paramInt12, paramInt13, paramintW2);
      }
      if ((paramintW2.val != 0 ? 0 : 1) != 0) {
        paramintW1.val = paramInt1;
      } else {
        paramintW2.val = 0;
      }
    }
    else
    {
      if (bool4) {
        str = "B";
      } else {
        str = "E";
      }
      Sstebz.sstebz(paramString2, str, paramInt1, f10, f11, paramInt4, paramInt5, paramFloat3, paramArrayOfFloat1, paramInt2, paramArrayOfFloat2, paramInt3, paramintW1, localintW, paramArrayOfFloat3, paramInt6, paramArrayOfInt2, i1 - 1 + paramInt12, paramArrayOfInt2, i3 - 1 + paramInt12, paramArrayOfFloat5, paramInt10, paramArrayOfInt2, i4 - 1 + paramInt12, paramintW2);
      if (bool4) {
        Sstein.sstein(paramInt1, paramArrayOfFloat1, paramInt2, paramArrayOfFloat2, paramInt3, paramintW1.val, paramArrayOfFloat3, paramInt6, paramArrayOfInt2, i1 - 1 + paramInt12, paramArrayOfInt2, i3 - 1 + paramInt12, paramArrayOfFloat4, paramInt7, paramInt8, paramArrayOfFloat5, paramInt10, paramArrayOfInt2, i4 - 1 + paramInt12, paramArrayOfInt2, i2 - 1 + paramInt12, paramintW2);
      }
    }
    if ((i5 != 1 ? 0 : 1) != 0)
    {
      if ((paramintW2.val != 0 ? 0 : 1) != 0) {
        n = paramintW1.val;
      } else {
        n = paramintW2.val - 1;
      }
      Sscal.sscal(n, 1.0F / f6, paramArrayOfFloat3, paramInt6, 1);
    }
    if (bool4)
    {
      i6 = 1;
      for (int i10 = paramintW1.val - 1 - 1 + 1; i10 > 0; i10--)
      {
        k = 0;
        f8 = paramArrayOfFloat3[(i6 - 1 + paramInt6)];
        i7 = i6 + 1;
        for (int i11 = paramintW1.val - (i6 + 1) + 1; i11 > 0; i11--)
        {
          if ((paramArrayOfFloat3[(i7 - 1 + paramInt6)] >= f8 ? 0 : 1) != 0)
          {
            k = i7;
            f8 = paramArrayOfFloat3[(i7 - 1 + paramInt6)];
          }
          i7 += 1;
        }
        if ((k == 0 ? 0 : 1) != 0)
        {
          paramArrayOfFloat3[(k - 1 + paramInt6)] = paramArrayOfFloat3[(i6 - 1 + paramInt6)];
          paramArrayOfFloat3[(i6 - 1 + paramInt6)] = f8;
          Sswap.sswap(paramInt1, paramArrayOfFloat4, 1 - 1 + (k - 1) * paramInt8 + paramInt7, 1, paramArrayOfFloat4, 1 - 1 + (i6 - 1) * paramInt8 + paramInt7, 1);
        }
        i6 += 1;
      }
    }
    paramArrayOfFloat5[(1 - 1 + paramInt10)] = i9;
    paramArrayOfInt2[(1 - 1 + paramInt12)] = i8;
  }
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.lapack.Sstevr
 * JD-Core Version:    0.7.0.1
 */