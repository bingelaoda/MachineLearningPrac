package org.netlib.lapack;

import org.netlib.blas.Daxpy;
import org.netlib.blas.Dcopy;
import org.netlib.blas.Ddot;
import org.netlib.blas.Dgemv;
import org.netlib.blas.Dscal;
import org.netlib.blas.Idamax;
import org.netlib.err.Xerbla;
import org.netlib.util.Util;
import org.netlib.util.doubleW;
import org.netlib.util.intW;

public final class Dtrevc
{
  public static void dtrevc(String paramString1, String paramString2, boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2, double[] paramArrayOfDouble1, int paramInt3, int paramInt4, double[] paramArrayOfDouble2, int paramInt5, int paramInt6, double[] paramArrayOfDouble3, int paramInt7, int paramInt8, int paramInt9, intW paramintW1, double[] paramArrayOfDouble4, int paramInt10, intW paramintW2)
  {
    boolean bool1 = false;
    boolean bool2 = false;
    int i = 0;
    boolean bool3 = false;
    int j = 0;
    int k = 0;
    boolean bool4 = false;
    int m = 0;
    intW localintW = new intW(0);
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
    double d1 = 0.0D;
    double d2 = 0.0D;
    double d3 = 0.0D;
    doubleW localdoubleW1 = new doubleW(0.0D);
    double d4 = 0.0D;
    double d5 = 0.0D;
    doubleW localdoubleW2 = new doubleW(0.0D);
    double d6 = 0.0D;
    double d7 = 0.0D;
    double d8 = 0.0D;
    doubleW localdoubleW3 = new doubleW(0.0D);
    double d9 = 0.0D;
    double d10 = 0.0D;
    double d11 = 0.0D;
    double d12 = 0.0D;
    doubleW localdoubleW4 = new doubleW(0.0D);
    double[] arrayOfDouble = new double[2 * 2];
    bool2 = Lsame.lsame(paramString1, "B");
    k = (!Lsame.lsame(paramString1, "R")) && (!bool2) ? 0 : 1;
    i = (!Lsame.lsame(paramString1, "L")) && (!bool2) ? 0 : 1;
    bool1 = Lsame.lsame(paramString2, "A");
    bool3 = Lsame.lsame(paramString2, "B");
    bool4 = Lsame.lsame(paramString2, "S");
    paramintW2.val = 0;
    if ((((k ^ 0x1) != 0) && ((i ^ 0x1) != 0) ? 1 : 0) != 0)
    {
      paramintW2.val = -1;
    }
    else
    {
      if ((((bool1 ^ true)) && ((bool3 ^ true)) ? 1 : 0) != 0) {}
      if (((bool4 ^ true) ? 1 : 0) != 0)
      {
        paramintW2.val = -2;
      }
      else if ((paramInt2 >= 0 ? 0 : 1) != 0)
      {
        paramintW2.val = -4;
      }
      else if ((paramInt4 >= Math.max(1, paramInt2) ? 0 : 1) != 0)
      {
        paramintW2.val = -6;
      }
      else
      {
        if ((paramInt6 >= 1 ? 0 : 1) == 0) {
          if (i == 0) {}
        }
        if ((((paramInt6 >= paramInt2 ? 0 : 1) != 0 ? 1 : 0) == 0 ? 0 : 1) != 0)
        {
          paramintW2.val = -8;
        }
        else
        {
          if ((paramInt8 >= 1 ? 0 : 1) == 0) {
            if (k == 0) {}
          }
          if ((((paramInt8 >= paramInt2 ? 0 : 1) != 0 ? 1 : 0) == 0 ? 0 : 1) != 0)
          {
            paramintW2.val = -10;
          }
          else
          {
            if (bool4)
            {
              paramintW1.val = 0;
              j = 0;
              i3 = 1;
              for (i10 = paramInt2 - 1 + 1; i10 > 0; i10--)
              {
                if (j != 0)
                {
                  j = 0;
                  paramArrayOfBoolean[(i3 - 1 + paramInt1)] = false;
                }
                else if ((i3 >= paramInt2 ? 0 : 1) != 0)
                {
                  if ((paramArrayOfDouble1[(i3 + 1 - 1 + (i3 - 1) * paramInt4 + paramInt3)] != 0.0D ? 0 : 1) != 0)
                  {
                    if (paramArrayOfBoolean[(i3 - 1 + paramInt1)] != 0) {
                      paramintW1.val += 1;
                    }
                  }
                  else
                  {
                    j = 1;
                    if (((paramArrayOfBoolean[(i3 - 1 + paramInt1)] == 0) && (paramArrayOfBoolean[(i3 + 1 - 1 + paramInt1)] == 0) ? 0 : 1) != 0)
                    {
                      paramArrayOfBoolean[(i3 - 1 + paramInt1)] = true;
                      paramintW1.val += 2;
                    }
                  }
                }
                else if (paramArrayOfBoolean[(paramInt2 - 1 + paramInt1)] != 0)
                {
                  paramintW1.val += 1;
                }
                i3 += 1;
              }
            }
            else
            {
              paramintW1.val = paramInt2;
            }
            if ((paramInt9 >= paramintW1.val ? 0 : 1) != 0) {
              paramintW2.val = -11;
            }
          }
        }
      }
    }
    if ((paramintW2.val == 0 ? 0 : 1) != 0)
    {
      Xerbla.xerbla("DTREVC", -paramintW2.val);
      return;
    }
    if ((paramInt2 != 0 ? 0 : 1) != 0) {
      return;
    }
    localdoubleW3.val = Dlamch.dlamch("Safe minimum");
    localdoubleW1.val = (1.0D / localdoubleW3.val);
    Dlabad.dlabad(localdoubleW3, localdoubleW1);
    d8 = Dlamch.dlamch("Precision");
    d7 = localdoubleW3.val * (paramInt2 / d8);
    d2 = (1.0D - d8) / d7;
    paramArrayOfDouble4[(1 - 1 + paramInt10)] = 0.0D;
    i3 = 2;
    int i11;
    for (int i10 = paramInt2 - 2 + 1; i10 > 0; i10--)
    {
      paramArrayOfDouble4[(i3 - 1 + paramInt10)] = 0.0D;
      m = 1;
      for (i11 = i3 - 1 - 1 + 1; i11 > 0; i11--)
      {
        paramArrayOfDouble4[(i3 - 1 + paramInt10)] += Math.abs(paramArrayOfDouble1[(m - 1 + (i3 - 1) * paramInt4 + paramInt3)]);
        m += 1;
      }
      i3 += 1;
    }
    i9 = 2 * paramInt2;
    if (k != 0)
    {
      i1 = 0;
      i2 = paramintW1.val;
      i8 = paramInt2;
      for (i10 = (1 - paramInt2 + -1) / -1; i10 > 0; i10--)
      {
        if ((i1 != 1 ? 0 : 1) == 0)
        {
          if ((i8 != 1 ? 0 : 1) == 0) {
            if ((paramArrayOfDouble1[(i8 - 1 + (i8 - 1 - 1) * paramInt4 + paramInt3)] != 0.0D ? 0 : 1) == 0) {
              i1 = -1;
            }
          }
          if (bool4)
          {
            if ((i1 != 0 ? 0 : 1) != 0) {}
            if (((paramArrayOfBoolean[(i8 - 1 + paramInt1)] ^ 0x1) != 0) || ((goto 1122) && ((paramArrayOfBoolean[(i8 - 1 - 1 + paramInt1)] ^ 0x1) != 0))) {}
          }
          else
          {
            d12 = paramArrayOfDouble1[(i8 - 1 + (i8 - 1) * paramInt4 + paramInt3)];
            d11 = 0.0D;
            if ((i1 == 0 ? 0 : 1) != 0) {
              d11 = Math.sqrt(Math.abs(paramArrayOfDouble1[(i8 - 1 + (i8 - 1 - 1) * paramInt4 + paramInt3)])) * Math.sqrt(Math.abs(paramArrayOfDouble1[(i8 - 1 - 1 + (i8 - 1) * paramInt4 + paramInt3)]));
            }
            d6 = Math.max(d8 * (Math.abs(d12) + Math.abs(d11)), d7);
            if ((i1 != 0 ? 0 : 1) != 0)
            {
              paramArrayOfDouble4[(i8 + paramInt2 - 1 + paramInt10)] = 1.0D;
              i7 = 1;
              for (i11 = i8 - 1 - 1 + 1; i11 > 0; i11--)
              {
                paramArrayOfDouble4[(i7 + paramInt2 - 1 + paramInt10)] = (-paramArrayOfDouble1[(i7 - 1 + (i8 - 1) * paramInt4 + paramInt3)]);
                i7 += 1;
              }
              i6 = i8 - 1;
              i3 = i8 - 1;
              for (i11 = (1 - (i8 - 1) + -1) / -1; i11 > 0; i11--)
              {
                if ((i3 <= i6 ? 0 : 1) == 0)
                {
                  i4 = i3;
                  i5 = i3;
                  i6 = i3 - 1;
                  if ((i3 <= 1 ? 0 : 1) != 0) {
                    if ((paramArrayOfDouble1[(i3 - 1 + (i3 - 1 - 1) * paramInt4 + paramInt3)] == 0.0D ? 0 : 1) != 0)
                    {
                      i4 = i3 - 1;
                      i6 = i3 - 2;
                    }
                  }
                  if ((i4 != i5 ? 0 : 1) != 0)
                  {
                    Dlaln2.dlaln2(false, 1, 1, d6, 1.0D, paramArrayOfDouble1, i3 - 1 + (i3 - 1) * paramInt4 + paramInt3, paramInt4, 1.0D, 1.0D, paramArrayOfDouble4, i3 + paramInt2 - 1 + paramInt10, paramInt2, d12, 0.0D, arrayOfDouble, 0, 2, localdoubleW2, localdoubleW4, localintW);
                    if ((localdoubleW4.val <= 1.0D ? 0 : 1) != 0) {
                      if ((paramArrayOfDouble4[(i3 - 1 + paramInt10)] <= d2 / localdoubleW4.val ? 0 : 1) != 0)
                      {
                        arrayOfDouble[(1 - 1 + (1 - 1) * 2)] /= localdoubleW4.val;
                        localdoubleW2.val /= localdoubleW4.val;
                      }
                    }
                    if ((localdoubleW2.val == 1.0D ? 0 : 1) != 0) {
                      Dscal.dscal(i8, localdoubleW2.val, paramArrayOfDouble4, 1 + paramInt2 - 1 + paramInt10, 1);
                    }
                    paramArrayOfDouble4[(i3 + paramInt2 - 1 + paramInt10)] = arrayOfDouble[(1 - 1 + (1 - 1) * 2)];
                    Daxpy.daxpy(i3 - 1, -arrayOfDouble[(1 - 1 + (1 - 1) * 2)], paramArrayOfDouble1, 1 - 1 + (i3 - 1) * paramInt4 + paramInt3, 1, paramArrayOfDouble4, 1 + paramInt2 - 1 + paramInt10, 1);
                  }
                  else
                  {
                    Dlaln2.dlaln2(false, 2, 1, d6, 1.0D, paramArrayOfDouble1, i3 - 1 - 1 + (i3 - 1 - 1) * paramInt4 + paramInt3, paramInt4, 1.0D, 1.0D, paramArrayOfDouble4, i3 - 1 + paramInt2 - 1 + paramInt10, paramInt2, d12, 0.0D, arrayOfDouble, 0, 2, localdoubleW2, localdoubleW4, localintW);
                    if ((localdoubleW4.val <= 1.0D ? 0 : 1) != 0)
                    {
                      d1 = Math.max(paramArrayOfDouble4[(i3 - 1 - 1 + paramInt10)], paramArrayOfDouble4[(i3 - 1 + paramInt10)]);
                      if ((d1 <= d2 / localdoubleW4.val ? 0 : 1) != 0)
                      {
                        arrayOfDouble[(1 - 1 + (1 - 1) * 2)] /= localdoubleW4.val;
                        arrayOfDouble[(2 - 1 + (1 - 1) * 2)] /= localdoubleW4.val;
                        localdoubleW2.val /= localdoubleW4.val;
                      }
                    }
                    if ((localdoubleW2.val == 1.0D ? 0 : 1) != 0) {
                      Dscal.dscal(i8, localdoubleW2.val, paramArrayOfDouble4, 1 + paramInt2 - 1 + paramInt10, 1);
                    }
                    paramArrayOfDouble4[(i3 - 1 + paramInt2 - 1 + paramInt10)] = arrayOfDouble[(1 - 1 + (1 - 1) * 2)];
                    paramArrayOfDouble4[(i3 + paramInt2 - 1 + paramInt10)] = arrayOfDouble[(2 - 1 + (1 - 1) * 2)];
                    Daxpy.daxpy(i3 - 2, -arrayOfDouble[(1 - 1 + (1 - 1) * 2)], paramArrayOfDouble1, 1 - 1 + (i3 - 1 - 1) * paramInt4 + paramInt3, 1, paramArrayOfDouble4, 1 + paramInt2 - 1 + paramInt10, 1);
                    Daxpy.daxpy(i3 - 2, -arrayOfDouble[(2 - 1 + (1 - 1) * 2)], paramArrayOfDouble1, 1 - 1 + (i3 - 1) * paramInt4 + paramInt3, 1, paramArrayOfDouble4, 1 + paramInt2 - 1 + paramInt10, 1);
                  }
                }
                i3 += -1;
              }
              if ((bool3 ^ true))
              {
                Dcopy.dcopy(i8, paramArrayOfDouble4, 1 + paramInt2 - 1 + paramInt10, 1, paramArrayOfDouble3, 1 - 1 + (i2 - 1) * paramInt8 + paramInt7, 1);
                n = Idamax.idamax(i8, paramArrayOfDouble3, 1 - 1 + (i2 - 1) * paramInt8 + paramInt7, 1);
                d5 = 1.0D / Math.abs(paramArrayOfDouble3[(n - 1 + (i2 - 1) * paramInt8 + paramInt7)]);
                Dscal.dscal(i8, d5, paramArrayOfDouble3, 1 - 1 + (i2 - 1) * paramInt8 + paramInt7, 1);
                i7 = i8 + 1;
                for (i11 = paramInt2 - (i8 + 1) + 1; i11 > 0; i11--)
                {
                  paramArrayOfDouble3[(i7 - 1 + (i2 - 1) * paramInt8 + paramInt7)] = 0.0D;
                  i7 += 1;
                }
              }
              else
              {
                if ((i8 <= 1 ? 0 : 1) != 0) {
                  Dgemv.dgemv("N", paramInt2, i8 - 1, 1.0D, paramArrayOfDouble3, paramInt7, paramInt8, paramArrayOfDouble4, 1 + paramInt2 - 1 + paramInt10, 1, paramArrayOfDouble4[(i8 + paramInt2 - 1 + paramInt10)], paramArrayOfDouble3, 1 - 1 + (i8 - 1) * paramInt8 + paramInt7, 1);
                }
                n = Idamax.idamax(paramInt2, paramArrayOfDouble3, 1 - 1 + (i8 - 1) * paramInt8 + paramInt7, 1);
                d5 = 1.0D / Math.abs(paramArrayOfDouble3[(n - 1 + (i8 - 1) * paramInt8 + paramInt7)]);
                Dscal.dscal(paramInt2, d5, paramArrayOfDouble3, 1 - 1 + (i8 - 1) * paramInt8 + paramInt7, 1);
              }
            }
            else
            {
              if ((Math.abs(paramArrayOfDouble1[(i8 - 1 - 1 + (i8 - 1) * paramInt4 + paramInt3)]) < Math.abs(paramArrayOfDouble1[(i8 - 1 + (i8 - 1 - 1) * paramInt4 + paramInt3)]) ? 0 : 1) != 0)
              {
                paramArrayOfDouble4[(i8 - 1 + paramInt2 - 1 + paramInt10)] = 1.0D;
                paramArrayOfDouble4[(i8 + i9 - 1 + paramInt10)] = (d11 / paramArrayOfDouble1[(i8 - 1 - 1 + (i8 - 1) * paramInt4 + paramInt3)]);
              }
              else
              {
                paramArrayOfDouble4[(i8 - 1 + paramInt2 - 1 + paramInt10)] = (-(d11 / paramArrayOfDouble1[(i8 - 1 + (i8 - 1 - 1) * paramInt4 + paramInt3)]));
                paramArrayOfDouble4[(i8 + i9 - 1 + paramInt10)] = 1.0D;
              }
              paramArrayOfDouble4[(i8 + paramInt2 - 1 + paramInt10)] = 0.0D;
              paramArrayOfDouble4[(i8 - 1 + i9 - 1 + paramInt10)] = 0.0D;
              i7 = 1;
              for (i11 = i8 - 2 - 1 + 1; i11 > 0; i11--)
              {
                paramArrayOfDouble4[(i7 + paramInt2 - 1 + paramInt10)] = (-(paramArrayOfDouble4[(i8 - 1 + paramInt2 - 1 + paramInt10)] * paramArrayOfDouble1[(i7 - 1 + (i8 - 1 - 1) * paramInt4 + paramInt3)]));
                paramArrayOfDouble4[(i7 + i9 - 1 + paramInt10)] = (-(paramArrayOfDouble4[(i8 + i9 - 1 + paramInt10)] * paramArrayOfDouble1[(i7 - 1 + (i8 - 1) * paramInt4 + paramInt3)]));
                i7 += 1;
              }
              i6 = i8 - 2;
              i3 = i8 - 2;
              for (i11 = (1 - (i8 - 2) + -1) / -1; i11 > 0; i11--)
              {
                if ((i3 <= i6 ? 0 : 1) == 0)
                {
                  i4 = i3;
                  i5 = i3;
                  i6 = i3 - 1;
                  if ((i3 <= 1 ? 0 : 1) != 0) {
                    if ((paramArrayOfDouble1[(i3 - 1 + (i3 - 1 - 1) * paramInt4 + paramInt3)] == 0.0D ? 0 : 1) != 0)
                    {
                      i4 = i3 - 1;
                      i6 = i3 - 2;
                    }
                  }
                  if ((i4 != i5 ? 0 : 1) != 0)
                  {
                    Dlaln2.dlaln2(false, 1, 2, d6, 1.0D, paramArrayOfDouble1, i3 - 1 + (i3 - 1) * paramInt4 + paramInt3, paramInt4, 1.0D, 1.0D, paramArrayOfDouble4, i3 + paramInt2 - 1 + paramInt10, paramInt2, d12, d11, arrayOfDouble, 0, 2, localdoubleW2, localdoubleW4, localintW);
                    if ((localdoubleW4.val <= 1.0D ? 0 : 1) != 0) {
                      if ((paramArrayOfDouble4[(i3 - 1 + paramInt10)] <= d2 / localdoubleW4.val ? 0 : 1) != 0)
                      {
                        arrayOfDouble[(1 - 1 + (1 - 1) * 2)] /= localdoubleW4.val;
                        arrayOfDouble[(1 - 1 + (2 - 1) * 2)] /= localdoubleW4.val;
                        localdoubleW2.val /= localdoubleW4.val;
                      }
                    }
                    if ((localdoubleW2.val == 1.0D ? 0 : 1) != 0)
                    {
                      Dscal.dscal(i8, localdoubleW2.val, paramArrayOfDouble4, 1 + paramInt2 - 1 + paramInt10, 1);
                      Dscal.dscal(i8, localdoubleW2.val, paramArrayOfDouble4, 1 + i9 - 1 + paramInt10, 1);
                    }
                    paramArrayOfDouble4[(i3 + paramInt2 - 1 + paramInt10)] = arrayOfDouble[(1 - 1 + (1 - 1) * 2)];
                    paramArrayOfDouble4[(i3 + i9 - 1 + paramInt10)] = arrayOfDouble[(1 - 1 + (2 - 1) * 2)];
                    Daxpy.daxpy(i3 - 1, -arrayOfDouble[(1 - 1 + (1 - 1) * 2)], paramArrayOfDouble1, 1 - 1 + (i3 - 1) * paramInt4 + paramInt3, 1, paramArrayOfDouble4, 1 + paramInt2 - 1 + paramInt10, 1);
                    Daxpy.daxpy(i3 - 1, -arrayOfDouble[(1 - 1 + (2 - 1) * 2)], paramArrayOfDouble1, 1 - 1 + (i3 - 1) * paramInt4 + paramInt3, 1, paramArrayOfDouble4, 1 + i9 - 1 + paramInt10, 1);
                  }
                  else
                  {
                    Dlaln2.dlaln2(false, 2, 2, d6, 1.0D, paramArrayOfDouble1, i3 - 1 - 1 + (i3 - 1 - 1) * paramInt4 + paramInt3, paramInt4, 1.0D, 1.0D, paramArrayOfDouble4, i3 - 1 + paramInt2 - 1 + paramInt10, paramInt2, d12, d11, arrayOfDouble, 0, 2, localdoubleW2, localdoubleW4, localintW);
                    if ((localdoubleW4.val <= 1.0D ? 0 : 1) != 0)
                    {
                      d1 = Math.max(paramArrayOfDouble4[(i3 - 1 - 1 + paramInt10)], paramArrayOfDouble4[(i3 - 1 + paramInt10)]);
                      if ((d1 <= d2 / localdoubleW4.val ? 0 : 1) != 0)
                      {
                        d4 = 1.0D / localdoubleW4.val;
                        arrayOfDouble[(1 - 1 + (1 - 1) * 2)] *= d4;
                        arrayOfDouble[(1 - 1 + (2 - 1) * 2)] *= d4;
                        arrayOfDouble[(2 - 1 + (1 - 1) * 2)] *= d4;
                        arrayOfDouble[(2 - 1 + (2 - 1) * 2)] *= d4;
                        localdoubleW2.val *= d4;
                      }
                    }
                    if ((localdoubleW2.val == 1.0D ? 0 : 1) != 0)
                    {
                      Dscal.dscal(i8, localdoubleW2.val, paramArrayOfDouble4, 1 + paramInt2 - 1 + paramInt10, 1);
                      Dscal.dscal(i8, localdoubleW2.val, paramArrayOfDouble4, 1 + i9 - 1 + paramInt10, 1);
                    }
                    paramArrayOfDouble4[(i3 - 1 + paramInt2 - 1 + paramInt10)] = arrayOfDouble[(1 - 1 + (1 - 1) * 2)];
                    paramArrayOfDouble4[(i3 + paramInt2 - 1 + paramInt10)] = arrayOfDouble[(2 - 1 + (1 - 1) * 2)];
                    paramArrayOfDouble4[(i3 - 1 + i9 - 1 + paramInt10)] = arrayOfDouble[(1 - 1 + (2 - 1) * 2)];
                    paramArrayOfDouble4[(i3 + i9 - 1 + paramInt10)] = arrayOfDouble[(2 - 1 + (2 - 1) * 2)];
                    Daxpy.daxpy(i3 - 2, -arrayOfDouble[(1 - 1 + (1 - 1) * 2)], paramArrayOfDouble1, 1 - 1 + (i3 - 1 - 1) * paramInt4 + paramInt3, 1, paramArrayOfDouble4, 1 + paramInt2 - 1 + paramInt10, 1);
                    Daxpy.daxpy(i3 - 2, -arrayOfDouble[(2 - 1 + (1 - 1) * 2)], paramArrayOfDouble1, 1 - 1 + (i3 - 1) * paramInt4 + paramInt3, 1, paramArrayOfDouble4, 1 + paramInt2 - 1 + paramInt10, 1);
                    Daxpy.daxpy(i3 - 2, -arrayOfDouble[(1 - 1 + (2 - 1) * 2)], paramArrayOfDouble1, 1 - 1 + (i3 - 1 - 1) * paramInt4 + paramInt3, 1, paramArrayOfDouble4, 1 + i9 - 1 + paramInt10, 1);
                    Daxpy.daxpy(i3 - 2, -arrayOfDouble[(2 - 1 + (2 - 1) * 2)], paramArrayOfDouble1, 1 - 1 + (i3 - 1) * paramInt4 + paramInt3, 1, paramArrayOfDouble4, 1 + i9 - 1 + paramInt10, 1);
                  }
                }
                i3 += -1;
              }
              if ((bool3 ^ true))
              {
                Dcopy.dcopy(i8, paramArrayOfDouble4, 1 + paramInt2 - 1 + paramInt10, 1, paramArrayOfDouble3, 1 - 1 + (i2 - 1 - 1) * paramInt8 + paramInt7, 1);
                Dcopy.dcopy(i8, paramArrayOfDouble4, 1 + i9 - 1 + paramInt10, 1, paramArrayOfDouble3, 1 - 1 + (i2 - 1) * paramInt8 + paramInt7, 1);
                d3 = 0.0D;
                i7 = 1;
                for (i11 = i8 - 1 + 1; i11 > 0; i11--)
                {
                  d3 = Math.max(d3, Math.abs(paramArrayOfDouble3[(i7 - 1 + (i2 - 1 - 1) * paramInt8 + paramInt7)]) + Math.abs(paramArrayOfDouble3[(i7 - 1 + (i2 - 1) * paramInt8 + paramInt7)]));
                  i7 += 1;
                }
                d5 = 1.0D / d3;
                Dscal.dscal(i8, d5, paramArrayOfDouble3, 1 - 1 + (i2 - 1 - 1) * paramInt8 + paramInt7, 1);
                Dscal.dscal(i8, d5, paramArrayOfDouble3, 1 - 1 + (i2 - 1) * paramInt8 + paramInt7, 1);
                i7 = i8 + 1;
                for (i11 = paramInt2 - (i8 + 1) + 1; i11 > 0; i11--)
                {
                  paramArrayOfDouble3[(i7 - 1 + (i2 - 1 - 1) * paramInt8 + paramInt7)] = 0.0D;
                  paramArrayOfDouble3[(i7 - 1 + (i2 - 1) * paramInt8 + paramInt7)] = 0.0D;
                  i7 += 1;
                }
              }
              else
              {
                if ((i8 <= 2 ? 0 : 1) != 0)
                {
                  Dgemv.dgemv("N", paramInt2, i8 - 2, 1.0D, paramArrayOfDouble3, paramInt7, paramInt8, paramArrayOfDouble4, 1 + paramInt2 - 1 + paramInt10, 1, paramArrayOfDouble4[(i8 - 1 + paramInt2 - 1 + paramInt10)], paramArrayOfDouble3, 1 - 1 + (i8 - 1 - 1) * paramInt8 + paramInt7, 1);
                  Dgemv.dgemv("N", paramInt2, i8 - 2, 1.0D, paramArrayOfDouble3, paramInt7, paramInt8, paramArrayOfDouble4, 1 + i9 - 1 + paramInt10, 1, paramArrayOfDouble4[(i8 + i9 - 1 + paramInt10)], paramArrayOfDouble3, 1 - 1 + (i8 - 1) * paramInt8 + paramInt7, 1);
                }
                else
                {
                  Dscal.dscal(paramInt2, paramArrayOfDouble4[(i8 - 1 + paramInt2 - 1 + paramInt10)], paramArrayOfDouble3, 1 - 1 + (i8 - 1 - 1) * paramInt8 + paramInt7, 1);
                  Dscal.dscal(paramInt2, paramArrayOfDouble4[(i8 + i9 - 1 + paramInt10)], paramArrayOfDouble3, 1 - 1 + (i8 - 1) * paramInt8 + paramInt7, 1);
                }
                d3 = 0.0D;
                i7 = 1;
                for (i11 = paramInt2 - 1 + 1; i11 > 0; i11--)
                {
                  d3 = Math.max(d3, Math.abs(paramArrayOfDouble3[(i7 - 1 + (i8 - 1 - 1) * paramInt8 + paramInt7)]) + Math.abs(paramArrayOfDouble3[(i7 - 1 + (i8 - 1) * paramInt8 + paramInt7)]));
                  i7 += 1;
                }
                d5 = 1.0D / d3;
                Dscal.dscal(paramInt2, d5, paramArrayOfDouble3, 1 - 1 + (i8 - 1 - 1) * paramInt8 + paramInt7, 1);
                Dscal.dscal(paramInt2, d5, paramArrayOfDouble3, 1 - 1 + (i8 - 1) * paramInt8 + paramInt7, 1);
              }
            }
            i2 -= 1;
            if ((i1 == 0 ? 0 : 1) != 0) {
              i2 -= 1;
            }
          }
        }
        if ((i1 != 1 ? 0 : 1) != 0) {
          i1 = 0;
        }
        if ((i1 != -1 ? 0 : 1) != 0) {
          i1 = 1;
        }
        i8 += -1;
      }
    }
    if (i != 0)
    {
      i1 = 0;
      i2 = 1;
      i8 = 1;
      for (i10 = paramInt2 - 1 + 1; i10 > 0; i10--)
      {
        if ((i1 != -1 ? 0 : 1) == 0)
        {
          if ((i8 != paramInt2 ? 0 : 1) == 0) {
            if ((paramArrayOfDouble1[(i8 + 1 - 1 + (i8 - 1) * paramInt4 + paramInt3)] != 0.0D ? 0 : 1) == 0) {
              i1 = 1;
            }
          }
          if ((!bool4) || ((paramArrayOfBoolean[(i8 - 1 + paramInt1)] ^ 0x1) == 0))
          {
            d12 = paramArrayOfDouble1[(i8 - 1 + (i8 - 1) * paramInt4 + paramInt3)];
            d11 = 0.0D;
            if ((i1 == 0 ? 0 : 1) != 0) {
              d11 = Math.sqrt(Math.abs(paramArrayOfDouble1[(i8 - 1 + (i8 + 1 - 1) * paramInt4 + paramInt3)])) * Math.sqrt(Math.abs(paramArrayOfDouble1[(i8 + 1 - 1 + (i8 - 1) * paramInt4 + paramInt3)]));
            }
            d6 = Math.max(d8 * (Math.abs(d12) + Math.abs(d11)), d7);
            if ((i1 != 0 ? 0 : 1) != 0)
            {
              paramArrayOfDouble4[(i8 + paramInt2 - 1 + paramInt10)] = 1.0D;
              i7 = i8 + 1;
              for (i11 = paramInt2 - (i8 + 1) + 1; i11 > 0; i11--)
              {
                paramArrayOfDouble4[(i7 + paramInt2 - 1 + paramInt10)] = (-paramArrayOfDouble1[(i8 - 1 + (i7 - 1) * paramInt4 + paramInt3)]);
                i7 += 1;
              }
              d10 = 1.0D;
              d9 = d2;
              i6 = i8 + 1;
              i3 = i8 + 1;
              for (i11 = paramInt2 - (i8 + 1) + 1; i11 > 0; i11--)
              {
                if ((i3 >= i6 ? 0 : 1) == 0)
                {
                  i4 = i3;
                  i5 = i3;
                  i6 = i3 + 1;
                  if ((i3 >= paramInt2 ? 0 : 1) != 0) {
                    if ((paramArrayOfDouble1[(i3 + 1 - 1 + (i3 - 1) * paramInt4 + paramInt3)] == 0.0D ? 0 : 1) != 0)
                    {
                      i5 = i3 + 1;
                      i6 = i3 + 2;
                    }
                  }
                  if ((i4 != i5 ? 0 : 1) != 0)
                  {
                    if ((paramArrayOfDouble4[(i3 - 1 + paramInt10)] <= d9 ? 0 : 1) != 0)
                    {
                      d4 = 1.0D / d10;
                      Dscal.dscal(paramInt2 - i8 + 1, d4, paramArrayOfDouble4, i8 + paramInt2 - 1 + paramInt10, 1);
                      d10 = 1.0D;
                      d9 = d2;
                    }
                    paramArrayOfDouble4[(i3 + paramInt2 - 1 + paramInt10)] -= Ddot.ddot(i3 - i8 - 1, paramArrayOfDouble1, i8 + 1 - 1 + (i3 - 1) * paramInt4 + paramInt3, 1, paramArrayOfDouble4, i8 + 1 + paramInt2 - 1 + paramInt10, 1);
                    Dlaln2.dlaln2(false, 1, 1, d6, 1.0D, paramArrayOfDouble1, i3 - 1 + (i3 - 1) * paramInt4 + paramInt3, paramInt4, 1.0D, 1.0D, paramArrayOfDouble4, i3 + paramInt2 - 1 + paramInt10, paramInt2, d12, 0.0D, arrayOfDouble, 0, 2, localdoubleW2, localdoubleW4, localintW);
                    if ((localdoubleW2.val == 1.0D ? 0 : 1) != 0) {
                      Dscal.dscal(paramInt2 - i8 + 1, localdoubleW2.val, paramArrayOfDouble4, i8 + paramInt2 - 1 + paramInt10, 1);
                    }
                    paramArrayOfDouble4[(i3 + paramInt2 - 1 + paramInt10)] = arrayOfDouble[(1 - 1 + (1 - 1) * 2)];
                    d10 = Math.max(Math.abs(paramArrayOfDouble4[(i3 + paramInt2 - 1 + paramInt10)]), d10);
                    d9 = d2 / d10;
                  }
                  else
                  {
                    d1 = Math.max(paramArrayOfDouble4[(i3 - 1 + paramInt10)], paramArrayOfDouble4[(i3 + 1 - 1 + paramInt10)]);
                    if ((d1 <= d9 ? 0 : 1) != 0)
                    {
                      d4 = 1.0D / d10;
                      Dscal.dscal(paramInt2 - i8 + 1, d4, paramArrayOfDouble4, i8 + paramInt2 - 1 + paramInt10, 1);
                      d10 = 1.0D;
                      d9 = d2;
                    }
                    paramArrayOfDouble4[(i3 + paramInt2 - 1 + paramInt10)] -= Ddot.ddot(i3 - i8 - 1, paramArrayOfDouble1, i8 + 1 - 1 + (i3 - 1) * paramInt4 + paramInt3, 1, paramArrayOfDouble4, i8 + 1 + paramInt2 - 1 + paramInt10, 1);
                    paramArrayOfDouble4[(i3 + 1 + paramInt2 - 1 + paramInt10)] -= Ddot.ddot(i3 - i8 - 1, paramArrayOfDouble1, i8 + 1 - 1 + (i3 + 1 - 1) * paramInt4 + paramInt3, 1, paramArrayOfDouble4, i8 + 1 + paramInt2 - 1 + paramInt10, 1);
                    Dlaln2.dlaln2(true, 2, 1, d6, 1.0D, paramArrayOfDouble1, i3 - 1 + (i3 - 1) * paramInt4 + paramInt3, paramInt4, 1.0D, 1.0D, paramArrayOfDouble4, i3 + paramInt2 - 1 + paramInt10, paramInt2, d12, 0.0D, arrayOfDouble, 0, 2, localdoubleW2, localdoubleW4, localintW);
                    if ((localdoubleW2.val == 1.0D ? 0 : 1) != 0) {
                      Dscal.dscal(paramInt2 - i8 + 1, localdoubleW2.val, paramArrayOfDouble4, i8 + paramInt2 - 1 + paramInt10, 1);
                    }
                    paramArrayOfDouble4[(i3 + paramInt2 - 1 + paramInt10)] = arrayOfDouble[(1 - 1 + (1 - 1) * 2)];
                    paramArrayOfDouble4[(i3 + 1 + paramInt2 - 1 + paramInt10)] = arrayOfDouble[(2 - 1 + (1 - 1) * 2)];
                    d10 = Util.max(Math.abs(paramArrayOfDouble4[(i3 + paramInt2 - 1 + paramInt10)]), Math.abs(paramArrayOfDouble4[(i3 + 1 + paramInt2 - 1 + paramInt10)]), d10);
                    d9 = d2 / d10;
                  }
                }
                i3 += 1;
              }
              if ((bool3 ^ true))
              {
                Dcopy.dcopy(paramInt2 - i8 + 1, paramArrayOfDouble4, i8 + paramInt2 - 1 + paramInt10, 1, paramArrayOfDouble2, i8 - 1 + (i2 - 1) * paramInt6 + paramInt5, 1);
                n = Idamax.idamax(paramInt2 - i8 + 1, paramArrayOfDouble2, i8 - 1 + (i2 - 1) * paramInt6 + paramInt5, 1) + i8 - 1;
                d5 = 1.0D / Math.abs(paramArrayOfDouble2[(n - 1 + (i2 - 1) * paramInt6 + paramInt5)]);
                Dscal.dscal(paramInt2 - i8 + 1, d5, paramArrayOfDouble2, i8 - 1 + (i2 - 1) * paramInt6 + paramInt5, 1);
                i7 = 1;
                for (i11 = i8 - 1 - 1 + 1; i11 > 0; i11--)
                {
                  paramArrayOfDouble2[(i7 - 1 + (i2 - 1) * paramInt6 + paramInt5)] = 0.0D;
                  i7 += 1;
                }
              }
              else
              {
                if ((i8 >= paramInt2 ? 0 : 1) != 0) {
                  Dgemv.dgemv("N", paramInt2, paramInt2 - i8, 1.0D, paramArrayOfDouble2, 1 - 1 + (i8 + 1 - 1) * paramInt6 + paramInt5, paramInt6, paramArrayOfDouble4, i8 + 1 + paramInt2 - 1 + paramInt10, 1, paramArrayOfDouble4[(i8 + paramInt2 - 1 + paramInt10)], paramArrayOfDouble2, 1 - 1 + (i8 - 1) * paramInt6 + paramInt5, 1);
                }
                n = Idamax.idamax(paramInt2, paramArrayOfDouble2, 1 - 1 + (i8 - 1) * paramInt6 + paramInt5, 1);
                d5 = 1.0D / Math.abs(paramArrayOfDouble2[(n - 1 + (i8 - 1) * paramInt6 + paramInt5)]);
                Dscal.dscal(paramInt2, d5, paramArrayOfDouble2, 1 - 1 + (i8 - 1) * paramInt6 + paramInt5, 1);
              }
            }
            else
            {
              if ((Math.abs(paramArrayOfDouble1[(i8 - 1 + (i8 + 1 - 1) * paramInt4 + paramInt3)]) < Math.abs(paramArrayOfDouble1[(i8 + 1 - 1 + (i8 - 1) * paramInt4 + paramInt3)]) ? 0 : 1) != 0)
              {
                paramArrayOfDouble4[(i8 + paramInt2 - 1 + paramInt10)] = (d11 / paramArrayOfDouble1[(i8 - 1 + (i8 + 1 - 1) * paramInt4 + paramInt3)]);
                paramArrayOfDouble4[(i8 + 1 + i9 - 1 + paramInt10)] = 1.0D;
              }
              else
              {
                paramArrayOfDouble4[(i8 + paramInt2 - 1 + paramInt10)] = 1.0D;
                paramArrayOfDouble4[(i8 + 1 + i9 - 1 + paramInt10)] = (-(d11 / paramArrayOfDouble1[(i8 + 1 - 1 + (i8 - 1) * paramInt4 + paramInt3)]));
              }
              paramArrayOfDouble4[(i8 + 1 + paramInt2 - 1 + paramInt10)] = 0.0D;
              paramArrayOfDouble4[(i8 + i9 - 1 + paramInt10)] = 0.0D;
              i7 = i8 + 2;
              for (i11 = paramInt2 - (i8 + 2) + 1; i11 > 0; i11--)
              {
                paramArrayOfDouble4[(i7 + paramInt2 - 1 + paramInt10)] = (-(paramArrayOfDouble4[(i8 + paramInt2 - 1 + paramInt10)] * paramArrayOfDouble1[(i8 - 1 + (i7 - 1) * paramInt4 + paramInt3)]));
                paramArrayOfDouble4[(i7 + i9 - 1 + paramInt10)] = (-(paramArrayOfDouble4[(i8 + 1 + i9 - 1 + paramInt10)] * paramArrayOfDouble1[(i8 + 1 - 1 + (i7 - 1) * paramInt4 + paramInt3)]));
                i7 += 1;
              }
              d10 = 1.0D;
              d9 = d2;
              i6 = i8 + 2;
              i3 = i8 + 2;
              for (i11 = paramInt2 - (i8 + 2) + 1; i11 > 0; i11--)
              {
                if ((i3 >= i6 ? 0 : 1) == 0)
                {
                  i4 = i3;
                  i5 = i3;
                  i6 = i3 + 1;
                  if ((i3 >= paramInt2 ? 0 : 1) != 0) {
                    if ((paramArrayOfDouble1[(i3 + 1 - 1 + (i3 - 1) * paramInt4 + paramInt3)] == 0.0D ? 0 : 1) != 0)
                    {
                      i5 = i3 + 1;
                      i6 = i3 + 2;
                    }
                  }
                  if ((i4 != i5 ? 0 : 1) != 0)
                  {
                    if ((paramArrayOfDouble4[(i3 - 1 + paramInt10)] <= d9 ? 0 : 1) != 0)
                    {
                      d4 = 1.0D / d10;
                      Dscal.dscal(paramInt2 - i8 + 1, d4, paramArrayOfDouble4, i8 + paramInt2 - 1 + paramInt10, 1);
                      Dscal.dscal(paramInt2 - i8 + 1, d4, paramArrayOfDouble4, i8 + i9 - 1 + paramInt10, 1);
                      d10 = 1.0D;
                      d9 = d2;
                    }
                    paramArrayOfDouble4[(i3 + paramInt2 - 1 + paramInt10)] -= Ddot.ddot(i3 - i8 - 2, paramArrayOfDouble1, i8 + 2 - 1 + (i3 - 1) * paramInt4 + paramInt3, 1, paramArrayOfDouble4, i8 + 2 + paramInt2 - 1 + paramInt10, 1);
                    paramArrayOfDouble4[(i3 + i9 - 1 + paramInt10)] -= Ddot.ddot(i3 - i8 - 2, paramArrayOfDouble1, i8 + 2 - 1 + (i3 - 1) * paramInt4 + paramInt3, 1, paramArrayOfDouble4, i8 + 2 + i9 - 1 + paramInt10, 1);
                    Dlaln2.dlaln2(false, 1, 2, d6, 1.0D, paramArrayOfDouble1, i3 - 1 + (i3 - 1) * paramInt4 + paramInt3, paramInt4, 1.0D, 1.0D, paramArrayOfDouble4, i3 + paramInt2 - 1 + paramInt10, paramInt2, d12, -d11, arrayOfDouble, 0, 2, localdoubleW2, localdoubleW4, localintW);
                    if ((localdoubleW2.val == 1.0D ? 0 : 1) != 0)
                    {
                      Dscal.dscal(paramInt2 - i8 + 1, localdoubleW2.val, paramArrayOfDouble4, i8 + paramInt2 - 1 + paramInt10, 1);
                      Dscal.dscal(paramInt2 - i8 + 1, localdoubleW2.val, paramArrayOfDouble4, i8 + i9 - 1 + paramInt10, 1);
                    }
                    paramArrayOfDouble4[(i3 + paramInt2 - 1 + paramInt10)] = arrayOfDouble[(1 - 1 + (1 - 1) * 2)];
                    paramArrayOfDouble4[(i3 + i9 - 1 + paramInt10)] = arrayOfDouble[(1 - 1 + (2 - 1) * 2)];
                    d10 = Util.max(Math.abs(paramArrayOfDouble4[(i3 + paramInt2 - 1 + paramInt10)]), Math.abs(paramArrayOfDouble4[(i3 + i9 - 1 + paramInt10)]), d10);
                    d9 = d2 / d10;
                  }
                  else
                  {
                    d1 = Math.max(paramArrayOfDouble4[(i3 - 1 + paramInt10)], paramArrayOfDouble4[(i3 + 1 - 1 + paramInt10)]);
                    if ((d1 <= d9 ? 0 : 1) != 0)
                    {
                      d4 = 1.0D / d10;
                      Dscal.dscal(paramInt2 - i8 + 1, d4, paramArrayOfDouble4, i8 + paramInt2 - 1 + paramInt10, 1);
                      Dscal.dscal(paramInt2 - i8 + 1, d4, paramArrayOfDouble4, i8 + i9 - 1 + paramInt10, 1);
                      d10 = 1.0D;
                      d9 = d2;
                    }
                    paramArrayOfDouble4[(i3 + paramInt2 - 1 + paramInt10)] -= Ddot.ddot(i3 - i8 - 2, paramArrayOfDouble1, i8 + 2 - 1 + (i3 - 1) * paramInt4 + paramInt3, 1, paramArrayOfDouble4, i8 + 2 + paramInt2 - 1 + paramInt10, 1);
                    paramArrayOfDouble4[(i3 + i9 - 1 + paramInt10)] -= Ddot.ddot(i3 - i8 - 2, paramArrayOfDouble1, i8 + 2 - 1 + (i3 - 1) * paramInt4 + paramInt3, 1, paramArrayOfDouble4, i8 + 2 + i9 - 1 + paramInt10, 1);
                    paramArrayOfDouble4[(i3 + 1 + paramInt2 - 1 + paramInt10)] -= Ddot.ddot(i3 - i8 - 2, paramArrayOfDouble1, i8 + 2 - 1 + (i3 + 1 - 1) * paramInt4 + paramInt3, 1, paramArrayOfDouble4, i8 + 2 + paramInt2 - 1 + paramInt10, 1);
                    paramArrayOfDouble4[(i3 + 1 + i9 - 1 + paramInt10)] -= Ddot.ddot(i3 - i8 - 2, paramArrayOfDouble1, i8 + 2 - 1 + (i3 + 1 - 1) * paramInt4 + paramInt3, 1, paramArrayOfDouble4, i8 + 2 + i9 - 1 + paramInt10, 1);
                    Dlaln2.dlaln2(true, 2, 2, d6, 1.0D, paramArrayOfDouble1, i3 - 1 + (i3 - 1) * paramInt4 + paramInt3, paramInt4, 1.0D, 1.0D, paramArrayOfDouble4, i3 + paramInt2 - 1 + paramInt10, paramInt2, d12, -d11, arrayOfDouble, 0, 2, localdoubleW2, localdoubleW4, localintW);
                    if ((localdoubleW2.val == 1.0D ? 0 : 1) != 0)
                    {
                      Dscal.dscal(paramInt2 - i8 + 1, localdoubleW2.val, paramArrayOfDouble4, i8 + paramInt2 - 1 + paramInt10, 1);
                      Dscal.dscal(paramInt2 - i8 + 1, localdoubleW2.val, paramArrayOfDouble4, i8 + i9 - 1 + paramInt10, 1);
                    }
                    paramArrayOfDouble4[(i3 + paramInt2 - 1 + paramInt10)] = arrayOfDouble[(1 - 1 + (1 - 1) * 2)];
                    paramArrayOfDouble4[(i3 + i9 - 1 + paramInt10)] = arrayOfDouble[(1 - 1 + (2 - 1) * 2)];
                    paramArrayOfDouble4[(i3 + 1 + paramInt2 - 1 + paramInt10)] = arrayOfDouble[(2 - 1 + (1 - 1) * 2)];
                    paramArrayOfDouble4[(i3 + 1 + i9 - 1 + paramInt10)] = arrayOfDouble[(2 - 1 + (2 - 1) * 2)];
                    d10 = Math.max(Math.max(Util.max(Math.abs(arrayOfDouble[(1 - 1 + (1 - 1) * 2)]), Math.abs(arrayOfDouble[(1 - 1 + (2 - 1) * 2)]), Math.abs(arrayOfDouble[(2 - 1 + (1 - 1) * 2)])), Math.abs(arrayOfDouble[(2 - 1 + (2 - 1) * 2)])), d10);
                    d9 = d2 / d10;
                  }
                }
                i3 += 1;
              }
              if ((bool3 ^ true))
              {
                Dcopy.dcopy(paramInt2 - i8 + 1, paramArrayOfDouble4, i8 + paramInt2 - 1 + paramInt10, 1, paramArrayOfDouble2, i8 - 1 + (i2 - 1) * paramInt6 + paramInt5, 1);
                Dcopy.dcopy(paramInt2 - i8 + 1, paramArrayOfDouble4, i8 + i9 - 1 + paramInt10, 1, paramArrayOfDouble2, i8 - 1 + (i2 + 1 - 1) * paramInt6 + paramInt5, 1);
                d3 = 0.0D;
                i7 = i8;
                for (i11 = paramInt2 - i8 + 1; i11 > 0; i11--)
                {
                  d3 = Math.max(d3, Math.abs(paramArrayOfDouble2[(i7 - 1 + (i2 - 1) * paramInt6 + paramInt5)]) + Math.abs(paramArrayOfDouble2[(i7 - 1 + (i2 + 1 - 1) * paramInt6 + paramInt5)]));
                  i7 += 1;
                }
                d5 = 1.0D / d3;
                Dscal.dscal(paramInt2 - i8 + 1, d5, paramArrayOfDouble2, i8 - 1 + (i2 - 1) * paramInt6 + paramInt5, 1);
                Dscal.dscal(paramInt2 - i8 + 1, d5, paramArrayOfDouble2, i8 - 1 + (i2 + 1 - 1) * paramInt6 + paramInt5, 1);
                i7 = 1;
                for (i11 = i8 - 1 - 1 + 1; i11 > 0; i11--)
                {
                  paramArrayOfDouble2[(i7 - 1 + (i2 - 1) * paramInt6 + paramInt5)] = 0.0D;
                  paramArrayOfDouble2[(i7 - 1 + (i2 + 1 - 1) * paramInt6 + paramInt5)] = 0.0D;
                  i7 += 1;
                }
              }
              else
              {
                if ((i8 >= paramInt2 - 1 ? 0 : 1) != 0)
                {
                  Dgemv.dgemv("N", paramInt2, paramInt2 - i8 - 1, 1.0D, paramArrayOfDouble2, 1 - 1 + (i8 + 2 - 1) * paramInt6 + paramInt5, paramInt6, paramArrayOfDouble4, i8 + 2 + paramInt2 - 1 + paramInt10, 1, paramArrayOfDouble4[(i8 + paramInt2 - 1 + paramInt10)], paramArrayOfDouble2, 1 - 1 + (i8 - 1) * paramInt6 + paramInt5, 1);
                  Dgemv.dgemv("N", paramInt2, paramInt2 - i8 - 1, 1.0D, paramArrayOfDouble2, 1 - 1 + (i8 + 2 - 1) * paramInt6 + paramInt5, paramInt6, paramArrayOfDouble4, i8 + 2 + i9 - 1 + paramInt10, 1, paramArrayOfDouble4[(i8 + 1 + i9 - 1 + paramInt10)], paramArrayOfDouble2, 1 - 1 + (i8 + 1 - 1) * paramInt6 + paramInt5, 1);
                }
                else
                {
                  Dscal.dscal(paramInt2, paramArrayOfDouble4[(i8 + paramInt2 - 1 + paramInt10)], paramArrayOfDouble2, 1 - 1 + (i8 - 1) * paramInt6 + paramInt5, 1);
                  Dscal.dscal(paramInt2, paramArrayOfDouble4[(i8 + 1 + i9 - 1 + paramInt10)], paramArrayOfDouble2, 1 - 1 + (i8 + 1 - 1) * paramInt6 + paramInt5, 1);
                }
                d3 = 0.0D;
                i7 = 1;
                for (i11 = paramInt2 - 1 + 1; i11 > 0; i11--)
                {
                  d3 = Math.max(d3, Math.abs(paramArrayOfDouble2[(i7 - 1 + (i8 - 1) * paramInt6 + paramInt5)]) + Math.abs(paramArrayOfDouble2[(i7 - 1 + (i8 + 1 - 1) * paramInt6 + paramInt5)]));
                  i7 += 1;
                }
                d5 = 1.0D / d3;
                Dscal.dscal(paramInt2, d5, paramArrayOfDouble2, 1 - 1 + (i8 - 1) * paramInt6 + paramInt5, 1);
                Dscal.dscal(paramInt2, d5, paramArrayOfDouble2, 1 - 1 + (i8 + 1 - 1) * paramInt6 + paramInt5, 1);
              }
            }
            i2 += 1;
            if ((i1 == 0 ? 0 : 1) != 0) {
              i2 += 1;
            }
          }
        }
        if ((i1 != -1 ? 0 : 1) != 0) {
          i1 = 0;
        }
        if ((i1 != 1 ? 0 : 1) != 0) {
          i1 = -1;
        }
        i8 += 1;
      }
    }
  }
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.lapack.Dtrevc
 * JD-Core Version:    0.7.0.1
 */