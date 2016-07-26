package org.netlib.lapack;

import org.netlib.blas.Isamax;
import org.netlib.blas.Saxpy;
import org.netlib.blas.Scopy;
import org.netlib.blas.Sdot;
import org.netlib.blas.Sgemv;
import org.netlib.blas.Sscal;
import org.netlib.err.Xerbla;
import org.netlib.util.Util;
import org.netlib.util.floatW;
import org.netlib.util.intW;

public final class Strevc
{
  public static void strevc(String paramString1, String paramString2, boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2, float[] paramArrayOfFloat1, int paramInt3, int paramInt4, float[] paramArrayOfFloat2, int paramInt5, int paramInt6, float[] paramArrayOfFloat3, int paramInt7, int paramInt8, int paramInt9, intW paramintW1, float[] paramArrayOfFloat4, int paramInt10, intW paramintW2)
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
    float f1 = 0.0F;
    float f2 = 0.0F;
    float f3 = 0.0F;
    floatW localfloatW1 = new floatW(0.0F);
    float f4 = 0.0F;
    float f5 = 0.0F;
    floatW localfloatW2 = new floatW(0.0F);
    float f6 = 0.0F;
    float f7 = 0.0F;
    float f8 = 0.0F;
    floatW localfloatW3 = new floatW(0.0F);
    float f9 = 0.0F;
    float f10 = 0.0F;
    float f11 = 0.0F;
    float f12 = 0.0F;
    floatW localfloatW4 = new floatW(0.0F);
    float[] arrayOfFloat = new float[2 * 2];
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
                  if ((paramArrayOfFloat1[(i3 + 1 - 1 + (i3 - 1) * paramInt4 + paramInt3)] != 0.0F ? 0 : 1) != 0)
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
      Xerbla.xerbla("STREVC", -paramintW2.val);
      return;
    }
    if ((paramInt2 != 0 ? 0 : 1) != 0) {
      return;
    }
    localfloatW3.val = Slamch.slamch("Safe minimum");
    localfloatW1.val = (1.0F / localfloatW3.val);
    Slabad.slabad(localfloatW3, localfloatW1);
    f8 = Slamch.slamch("Precision");
    f7 = localfloatW3.val * (paramInt2 / f8);
    f2 = (1.0F - f8) / f7;
    paramArrayOfFloat4[(1 - 1 + paramInt10)] = 0.0F;
    i3 = 2;
    int i11;
    for (int i10 = paramInt2 - 2 + 1; i10 > 0; i10--)
    {
      paramArrayOfFloat4[(i3 - 1 + paramInt10)] = 0.0F;
      m = 1;
      for (i11 = i3 - 1 - 1 + 1; i11 > 0; i11--)
      {
        paramArrayOfFloat4[(i3 - 1 + paramInt10)] += Math.abs(paramArrayOfFloat1[(m - 1 + (i3 - 1) * paramInt4 + paramInt3)]);
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
            if ((paramArrayOfFloat1[(i8 - 1 + (i8 - 1 - 1) * paramInt4 + paramInt3)] != 0.0F ? 0 : 1) == 0) {
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
            f12 = paramArrayOfFloat1[(i8 - 1 + (i8 - 1) * paramInt4 + paramInt3)];
            f11 = 0.0F;
            if ((i1 == 0 ? 0 : 1) != 0) {
              f11 = (float)Math.sqrt(Math.abs(paramArrayOfFloat1[(i8 - 1 + (i8 - 1 - 1) * paramInt4 + paramInt3)])) * (float)Math.sqrt(Math.abs(paramArrayOfFloat1[(i8 - 1 - 1 + (i8 - 1) * paramInt4 + paramInt3)]));
            }
            f6 = Math.max(f8 * (Math.abs(f12) + Math.abs(f11)), f7);
            if ((i1 != 0 ? 0 : 1) != 0)
            {
              paramArrayOfFloat4[(i8 + paramInt2 - 1 + paramInt10)] = 1.0F;
              i7 = 1;
              for (i11 = i8 - 1 - 1 + 1; i11 > 0; i11--)
              {
                paramArrayOfFloat4[(i7 + paramInt2 - 1 + paramInt10)] = (-paramArrayOfFloat1[(i7 - 1 + (i8 - 1) * paramInt4 + paramInt3)]);
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
                    if ((paramArrayOfFloat1[(i3 - 1 + (i3 - 1 - 1) * paramInt4 + paramInt3)] == 0.0F ? 0 : 1) != 0)
                    {
                      i4 = i3 - 1;
                      i6 = i3 - 2;
                    }
                  }
                  if ((i4 != i5 ? 0 : 1) != 0)
                  {
                    Slaln2.slaln2(false, 1, 1, f6, 1.0F, paramArrayOfFloat1, i3 - 1 + (i3 - 1) * paramInt4 + paramInt3, paramInt4, 1.0F, 1.0F, paramArrayOfFloat4, i3 + paramInt2 - 1 + paramInt10, paramInt2, f12, 0.0F, arrayOfFloat, 0, 2, localfloatW2, localfloatW4, localintW);
                    if ((localfloatW4.val <= 1.0F ? 0 : 1) != 0) {
                      if ((paramArrayOfFloat4[(i3 - 1 + paramInt10)] <= f2 / localfloatW4.val ? 0 : 1) != 0)
                      {
                        arrayOfFloat[(1 - 1 + (1 - 1) * 2)] /= localfloatW4.val;
                        localfloatW2.val /= localfloatW4.val;
                      }
                    }
                    if ((localfloatW2.val == 1.0F ? 0 : 1) != 0) {
                      Sscal.sscal(i8, localfloatW2.val, paramArrayOfFloat4, 1 + paramInt2 - 1 + paramInt10, 1);
                    }
                    paramArrayOfFloat4[(i3 + paramInt2 - 1 + paramInt10)] = arrayOfFloat[(1 - 1 + (1 - 1) * 2)];
                    Saxpy.saxpy(i3 - 1, -arrayOfFloat[(1 - 1 + (1 - 1) * 2)], paramArrayOfFloat1, 1 - 1 + (i3 - 1) * paramInt4 + paramInt3, 1, paramArrayOfFloat4, 1 + paramInt2 - 1 + paramInt10, 1);
                  }
                  else
                  {
                    Slaln2.slaln2(false, 2, 1, f6, 1.0F, paramArrayOfFloat1, i3 - 1 - 1 + (i3 - 1 - 1) * paramInt4 + paramInt3, paramInt4, 1.0F, 1.0F, paramArrayOfFloat4, i3 - 1 + paramInt2 - 1 + paramInt10, paramInt2, f12, 0.0F, arrayOfFloat, 0, 2, localfloatW2, localfloatW4, localintW);
                    if ((localfloatW4.val <= 1.0F ? 0 : 1) != 0)
                    {
                      f1 = Math.max(paramArrayOfFloat4[(i3 - 1 - 1 + paramInt10)], paramArrayOfFloat4[(i3 - 1 + paramInt10)]);
                      if ((f1 <= f2 / localfloatW4.val ? 0 : 1) != 0)
                      {
                        arrayOfFloat[(1 - 1 + (1 - 1) * 2)] /= localfloatW4.val;
                        arrayOfFloat[(2 - 1 + (1 - 1) * 2)] /= localfloatW4.val;
                        localfloatW2.val /= localfloatW4.val;
                      }
                    }
                    if ((localfloatW2.val == 1.0F ? 0 : 1) != 0) {
                      Sscal.sscal(i8, localfloatW2.val, paramArrayOfFloat4, 1 + paramInt2 - 1 + paramInt10, 1);
                    }
                    paramArrayOfFloat4[(i3 - 1 + paramInt2 - 1 + paramInt10)] = arrayOfFloat[(1 - 1 + (1 - 1) * 2)];
                    paramArrayOfFloat4[(i3 + paramInt2 - 1 + paramInt10)] = arrayOfFloat[(2 - 1 + (1 - 1) * 2)];
                    Saxpy.saxpy(i3 - 2, -arrayOfFloat[(1 - 1 + (1 - 1) * 2)], paramArrayOfFloat1, 1 - 1 + (i3 - 1 - 1) * paramInt4 + paramInt3, 1, paramArrayOfFloat4, 1 + paramInt2 - 1 + paramInt10, 1);
                    Saxpy.saxpy(i3 - 2, -arrayOfFloat[(2 - 1 + (1 - 1) * 2)], paramArrayOfFloat1, 1 - 1 + (i3 - 1) * paramInt4 + paramInt3, 1, paramArrayOfFloat4, 1 + paramInt2 - 1 + paramInt10, 1);
                  }
                }
                i3 += -1;
              }
              if ((bool3 ^ true))
              {
                Scopy.scopy(i8, paramArrayOfFloat4, 1 + paramInt2 - 1 + paramInt10, 1, paramArrayOfFloat3, 1 - 1 + (i2 - 1) * paramInt8 + paramInt7, 1);
                n = Isamax.isamax(i8, paramArrayOfFloat3, 1 - 1 + (i2 - 1) * paramInt8 + paramInt7, 1);
                f5 = 1.0F / Math.abs(paramArrayOfFloat3[(n - 1 + (i2 - 1) * paramInt8 + paramInt7)]);
                Sscal.sscal(i8, f5, paramArrayOfFloat3, 1 - 1 + (i2 - 1) * paramInt8 + paramInt7, 1);
                i7 = i8 + 1;
                for (i11 = paramInt2 - (i8 + 1) + 1; i11 > 0; i11--)
                {
                  paramArrayOfFloat3[(i7 - 1 + (i2 - 1) * paramInt8 + paramInt7)] = 0.0F;
                  i7 += 1;
                }
              }
              else
              {
                if ((i8 <= 1 ? 0 : 1) != 0) {
                  Sgemv.sgemv("N", paramInt2, i8 - 1, 1.0F, paramArrayOfFloat3, paramInt7, paramInt8, paramArrayOfFloat4, 1 + paramInt2 - 1 + paramInt10, 1, paramArrayOfFloat4[(i8 + paramInt2 - 1 + paramInt10)], paramArrayOfFloat3, 1 - 1 + (i8 - 1) * paramInt8 + paramInt7, 1);
                }
                n = Isamax.isamax(paramInt2, paramArrayOfFloat3, 1 - 1 + (i8 - 1) * paramInt8 + paramInt7, 1);
                f5 = 1.0F / Math.abs(paramArrayOfFloat3[(n - 1 + (i8 - 1) * paramInt8 + paramInt7)]);
                Sscal.sscal(paramInt2, f5, paramArrayOfFloat3, 1 - 1 + (i8 - 1) * paramInt8 + paramInt7, 1);
              }
            }
            else
            {
              if ((Math.abs(paramArrayOfFloat1[(i8 - 1 - 1 + (i8 - 1) * paramInt4 + paramInt3)]) < Math.abs(paramArrayOfFloat1[(i8 - 1 + (i8 - 1 - 1) * paramInt4 + paramInt3)]) ? 0 : 1) != 0)
              {
                paramArrayOfFloat4[(i8 - 1 + paramInt2 - 1 + paramInt10)] = 1.0F;
                paramArrayOfFloat4[(i8 + i9 - 1 + paramInt10)] = (f11 / paramArrayOfFloat1[(i8 - 1 - 1 + (i8 - 1) * paramInt4 + paramInt3)]);
              }
              else
              {
                paramArrayOfFloat4[(i8 - 1 + paramInt2 - 1 + paramInt10)] = (-(f11 / paramArrayOfFloat1[(i8 - 1 + (i8 - 1 - 1) * paramInt4 + paramInt3)]));
                paramArrayOfFloat4[(i8 + i9 - 1 + paramInt10)] = 1.0F;
              }
              paramArrayOfFloat4[(i8 + paramInt2 - 1 + paramInt10)] = 0.0F;
              paramArrayOfFloat4[(i8 - 1 + i9 - 1 + paramInt10)] = 0.0F;
              i7 = 1;
              for (i11 = i8 - 2 - 1 + 1; i11 > 0; i11--)
              {
                paramArrayOfFloat4[(i7 + paramInt2 - 1 + paramInt10)] = (-(paramArrayOfFloat4[(i8 - 1 + paramInt2 - 1 + paramInt10)] * paramArrayOfFloat1[(i7 - 1 + (i8 - 1 - 1) * paramInt4 + paramInt3)]));
                paramArrayOfFloat4[(i7 + i9 - 1 + paramInt10)] = (-(paramArrayOfFloat4[(i8 + i9 - 1 + paramInt10)] * paramArrayOfFloat1[(i7 - 1 + (i8 - 1) * paramInt4 + paramInt3)]));
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
                    if ((paramArrayOfFloat1[(i3 - 1 + (i3 - 1 - 1) * paramInt4 + paramInt3)] == 0.0F ? 0 : 1) != 0)
                    {
                      i4 = i3 - 1;
                      i6 = i3 - 2;
                    }
                  }
                  if ((i4 != i5 ? 0 : 1) != 0)
                  {
                    Slaln2.slaln2(false, 1, 2, f6, 1.0F, paramArrayOfFloat1, i3 - 1 + (i3 - 1) * paramInt4 + paramInt3, paramInt4, 1.0F, 1.0F, paramArrayOfFloat4, i3 + paramInt2 - 1 + paramInt10, paramInt2, f12, f11, arrayOfFloat, 0, 2, localfloatW2, localfloatW4, localintW);
                    if ((localfloatW4.val <= 1.0F ? 0 : 1) != 0) {
                      if ((paramArrayOfFloat4[(i3 - 1 + paramInt10)] <= f2 / localfloatW4.val ? 0 : 1) != 0)
                      {
                        arrayOfFloat[(1 - 1 + (1 - 1) * 2)] /= localfloatW4.val;
                        arrayOfFloat[(1 - 1 + (2 - 1) * 2)] /= localfloatW4.val;
                        localfloatW2.val /= localfloatW4.val;
                      }
                    }
                    if ((localfloatW2.val == 1.0F ? 0 : 1) != 0)
                    {
                      Sscal.sscal(i8, localfloatW2.val, paramArrayOfFloat4, 1 + paramInt2 - 1 + paramInt10, 1);
                      Sscal.sscal(i8, localfloatW2.val, paramArrayOfFloat4, 1 + i9 - 1 + paramInt10, 1);
                    }
                    paramArrayOfFloat4[(i3 + paramInt2 - 1 + paramInt10)] = arrayOfFloat[(1 - 1 + (1 - 1) * 2)];
                    paramArrayOfFloat4[(i3 + i9 - 1 + paramInt10)] = arrayOfFloat[(1 - 1 + (2 - 1) * 2)];
                    Saxpy.saxpy(i3 - 1, -arrayOfFloat[(1 - 1 + (1 - 1) * 2)], paramArrayOfFloat1, 1 - 1 + (i3 - 1) * paramInt4 + paramInt3, 1, paramArrayOfFloat4, 1 + paramInt2 - 1 + paramInt10, 1);
                    Saxpy.saxpy(i3 - 1, -arrayOfFloat[(1 - 1 + (2 - 1) * 2)], paramArrayOfFloat1, 1 - 1 + (i3 - 1) * paramInt4 + paramInt3, 1, paramArrayOfFloat4, 1 + i9 - 1 + paramInt10, 1);
                  }
                  else
                  {
                    Slaln2.slaln2(false, 2, 2, f6, 1.0F, paramArrayOfFloat1, i3 - 1 - 1 + (i3 - 1 - 1) * paramInt4 + paramInt3, paramInt4, 1.0F, 1.0F, paramArrayOfFloat4, i3 - 1 + paramInt2 - 1 + paramInt10, paramInt2, f12, f11, arrayOfFloat, 0, 2, localfloatW2, localfloatW4, localintW);
                    if ((localfloatW4.val <= 1.0F ? 0 : 1) != 0)
                    {
                      f1 = Math.max(paramArrayOfFloat4[(i3 - 1 - 1 + paramInt10)], paramArrayOfFloat4[(i3 - 1 + paramInt10)]);
                      if ((f1 <= f2 / localfloatW4.val ? 0 : 1) != 0)
                      {
                        f4 = 1.0F / localfloatW4.val;
                        arrayOfFloat[(1 - 1 + (1 - 1) * 2)] *= f4;
                        arrayOfFloat[(1 - 1 + (2 - 1) * 2)] *= f4;
                        arrayOfFloat[(2 - 1 + (1 - 1) * 2)] *= f4;
                        arrayOfFloat[(2 - 1 + (2 - 1) * 2)] *= f4;
                        localfloatW2.val *= f4;
                      }
                    }
                    if ((localfloatW2.val == 1.0F ? 0 : 1) != 0)
                    {
                      Sscal.sscal(i8, localfloatW2.val, paramArrayOfFloat4, 1 + paramInt2 - 1 + paramInt10, 1);
                      Sscal.sscal(i8, localfloatW2.val, paramArrayOfFloat4, 1 + i9 - 1 + paramInt10, 1);
                    }
                    paramArrayOfFloat4[(i3 - 1 + paramInt2 - 1 + paramInt10)] = arrayOfFloat[(1 - 1 + (1 - 1) * 2)];
                    paramArrayOfFloat4[(i3 + paramInt2 - 1 + paramInt10)] = arrayOfFloat[(2 - 1 + (1 - 1) * 2)];
                    paramArrayOfFloat4[(i3 - 1 + i9 - 1 + paramInt10)] = arrayOfFloat[(1 - 1 + (2 - 1) * 2)];
                    paramArrayOfFloat4[(i3 + i9 - 1 + paramInt10)] = arrayOfFloat[(2 - 1 + (2 - 1) * 2)];
                    Saxpy.saxpy(i3 - 2, -arrayOfFloat[(1 - 1 + (1 - 1) * 2)], paramArrayOfFloat1, 1 - 1 + (i3 - 1 - 1) * paramInt4 + paramInt3, 1, paramArrayOfFloat4, 1 + paramInt2 - 1 + paramInt10, 1);
                    Saxpy.saxpy(i3 - 2, -arrayOfFloat[(2 - 1 + (1 - 1) * 2)], paramArrayOfFloat1, 1 - 1 + (i3 - 1) * paramInt4 + paramInt3, 1, paramArrayOfFloat4, 1 + paramInt2 - 1 + paramInt10, 1);
                    Saxpy.saxpy(i3 - 2, -arrayOfFloat[(1 - 1 + (2 - 1) * 2)], paramArrayOfFloat1, 1 - 1 + (i3 - 1 - 1) * paramInt4 + paramInt3, 1, paramArrayOfFloat4, 1 + i9 - 1 + paramInt10, 1);
                    Saxpy.saxpy(i3 - 2, -arrayOfFloat[(2 - 1 + (2 - 1) * 2)], paramArrayOfFloat1, 1 - 1 + (i3 - 1) * paramInt4 + paramInt3, 1, paramArrayOfFloat4, 1 + i9 - 1 + paramInt10, 1);
                  }
                }
                i3 += -1;
              }
              if ((bool3 ^ true))
              {
                Scopy.scopy(i8, paramArrayOfFloat4, 1 + paramInt2 - 1 + paramInt10, 1, paramArrayOfFloat3, 1 - 1 + (i2 - 1 - 1) * paramInt8 + paramInt7, 1);
                Scopy.scopy(i8, paramArrayOfFloat4, 1 + i9 - 1 + paramInt10, 1, paramArrayOfFloat3, 1 - 1 + (i2 - 1) * paramInt8 + paramInt7, 1);
                f3 = 0.0F;
                i7 = 1;
                for (i11 = i8 - 1 + 1; i11 > 0; i11--)
                {
                  f3 = Math.max(f3, Math.abs(paramArrayOfFloat3[(i7 - 1 + (i2 - 1 - 1) * paramInt8 + paramInt7)]) + Math.abs(paramArrayOfFloat3[(i7 - 1 + (i2 - 1) * paramInt8 + paramInt7)]));
                  i7 += 1;
                }
                f5 = 1.0F / f3;
                Sscal.sscal(i8, f5, paramArrayOfFloat3, 1 - 1 + (i2 - 1 - 1) * paramInt8 + paramInt7, 1);
                Sscal.sscal(i8, f5, paramArrayOfFloat3, 1 - 1 + (i2 - 1) * paramInt8 + paramInt7, 1);
                i7 = i8 + 1;
                for (i11 = paramInt2 - (i8 + 1) + 1; i11 > 0; i11--)
                {
                  paramArrayOfFloat3[(i7 - 1 + (i2 - 1 - 1) * paramInt8 + paramInt7)] = 0.0F;
                  paramArrayOfFloat3[(i7 - 1 + (i2 - 1) * paramInt8 + paramInt7)] = 0.0F;
                  i7 += 1;
                }
              }
              else
              {
                if ((i8 <= 2 ? 0 : 1) != 0)
                {
                  Sgemv.sgemv("N", paramInt2, i8 - 2, 1.0F, paramArrayOfFloat3, paramInt7, paramInt8, paramArrayOfFloat4, 1 + paramInt2 - 1 + paramInt10, 1, paramArrayOfFloat4[(i8 - 1 + paramInt2 - 1 + paramInt10)], paramArrayOfFloat3, 1 - 1 + (i8 - 1 - 1) * paramInt8 + paramInt7, 1);
                  Sgemv.sgemv("N", paramInt2, i8 - 2, 1.0F, paramArrayOfFloat3, paramInt7, paramInt8, paramArrayOfFloat4, 1 + i9 - 1 + paramInt10, 1, paramArrayOfFloat4[(i8 + i9 - 1 + paramInt10)], paramArrayOfFloat3, 1 - 1 + (i8 - 1) * paramInt8 + paramInt7, 1);
                }
                else
                {
                  Sscal.sscal(paramInt2, paramArrayOfFloat4[(i8 - 1 + paramInt2 - 1 + paramInt10)], paramArrayOfFloat3, 1 - 1 + (i8 - 1 - 1) * paramInt8 + paramInt7, 1);
                  Sscal.sscal(paramInt2, paramArrayOfFloat4[(i8 + i9 - 1 + paramInt10)], paramArrayOfFloat3, 1 - 1 + (i8 - 1) * paramInt8 + paramInt7, 1);
                }
                f3 = 0.0F;
                i7 = 1;
                for (i11 = paramInt2 - 1 + 1; i11 > 0; i11--)
                {
                  f3 = Math.max(f3, Math.abs(paramArrayOfFloat3[(i7 - 1 + (i8 - 1 - 1) * paramInt8 + paramInt7)]) + Math.abs(paramArrayOfFloat3[(i7 - 1 + (i8 - 1) * paramInt8 + paramInt7)]));
                  i7 += 1;
                }
                f5 = 1.0F / f3;
                Sscal.sscal(paramInt2, f5, paramArrayOfFloat3, 1 - 1 + (i8 - 1 - 1) * paramInt8 + paramInt7, 1);
                Sscal.sscal(paramInt2, f5, paramArrayOfFloat3, 1 - 1 + (i8 - 1) * paramInt8 + paramInt7, 1);
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
            if ((paramArrayOfFloat1[(i8 + 1 - 1 + (i8 - 1) * paramInt4 + paramInt3)] != 0.0F ? 0 : 1) == 0) {
              i1 = 1;
            }
          }
          if ((!bool4) || ((paramArrayOfBoolean[(i8 - 1 + paramInt1)] ^ 0x1) == 0))
          {
            f12 = paramArrayOfFloat1[(i8 - 1 + (i8 - 1) * paramInt4 + paramInt3)];
            f11 = 0.0F;
            if ((i1 == 0 ? 0 : 1) != 0) {
              f11 = (float)Math.sqrt(Math.abs(paramArrayOfFloat1[(i8 - 1 + (i8 + 1 - 1) * paramInt4 + paramInt3)])) * (float)Math.sqrt(Math.abs(paramArrayOfFloat1[(i8 + 1 - 1 + (i8 - 1) * paramInt4 + paramInt3)]));
            }
            f6 = Math.max(f8 * (Math.abs(f12) + Math.abs(f11)), f7);
            if ((i1 != 0 ? 0 : 1) != 0)
            {
              paramArrayOfFloat4[(i8 + paramInt2 - 1 + paramInt10)] = 1.0F;
              i7 = i8 + 1;
              for (i11 = paramInt2 - (i8 + 1) + 1; i11 > 0; i11--)
              {
                paramArrayOfFloat4[(i7 + paramInt2 - 1 + paramInt10)] = (-paramArrayOfFloat1[(i8 - 1 + (i7 - 1) * paramInt4 + paramInt3)]);
                i7 += 1;
              }
              f10 = 1.0F;
              f9 = f2;
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
                    if ((paramArrayOfFloat1[(i3 + 1 - 1 + (i3 - 1) * paramInt4 + paramInt3)] == 0.0F ? 0 : 1) != 0)
                    {
                      i5 = i3 + 1;
                      i6 = i3 + 2;
                    }
                  }
                  if ((i4 != i5 ? 0 : 1) != 0)
                  {
                    if ((paramArrayOfFloat4[(i3 - 1 + paramInt10)] <= f9 ? 0 : 1) != 0)
                    {
                      f4 = 1.0F / f10;
                      Sscal.sscal(paramInt2 - i8 + 1, f4, paramArrayOfFloat4, i8 + paramInt2 - 1 + paramInt10, 1);
                      f10 = 1.0F;
                      f9 = f2;
                    }
                    paramArrayOfFloat4[(i3 + paramInt2 - 1 + paramInt10)] -= Sdot.sdot(i3 - i8 - 1, paramArrayOfFloat1, i8 + 1 - 1 + (i3 - 1) * paramInt4 + paramInt3, 1, paramArrayOfFloat4, i8 + 1 + paramInt2 - 1 + paramInt10, 1);
                    Slaln2.slaln2(false, 1, 1, f6, 1.0F, paramArrayOfFloat1, i3 - 1 + (i3 - 1) * paramInt4 + paramInt3, paramInt4, 1.0F, 1.0F, paramArrayOfFloat4, i3 + paramInt2 - 1 + paramInt10, paramInt2, f12, 0.0F, arrayOfFloat, 0, 2, localfloatW2, localfloatW4, localintW);
                    if ((localfloatW2.val == 1.0F ? 0 : 1) != 0) {
                      Sscal.sscal(paramInt2 - i8 + 1, localfloatW2.val, paramArrayOfFloat4, i8 + paramInt2 - 1 + paramInt10, 1);
                    }
                    paramArrayOfFloat4[(i3 + paramInt2 - 1 + paramInt10)] = arrayOfFloat[(1 - 1 + (1 - 1) * 2)];
                    f10 = Math.max(Math.abs(paramArrayOfFloat4[(i3 + paramInt2 - 1 + paramInt10)]), f10);
                    f9 = f2 / f10;
                  }
                  else
                  {
                    f1 = Math.max(paramArrayOfFloat4[(i3 - 1 + paramInt10)], paramArrayOfFloat4[(i3 + 1 - 1 + paramInt10)]);
                    if ((f1 <= f9 ? 0 : 1) != 0)
                    {
                      f4 = 1.0F / f10;
                      Sscal.sscal(paramInt2 - i8 + 1, f4, paramArrayOfFloat4, i8 + paramInt2 - 1 + paramInt10, 1);
                      f10 = 1.0F;
                      f9 = f2;
                    }
                    paramArrayOfFloat4[(i3 + paramInt2 - 1 + paramInt10)] -= Sdot.sdot(i3 - i8 - 1, paramArrayOfFloat1, i8 + 1 - 1 + (i3 - 1) * paramInt4 + paramInt3, 1, paramArrayOfFloat4, i8 + 1 + paramInt2 - 1 + paramInt10, 1);
                    paramArrayOfFloat4[(i3 + 1 + paramInt2 - 1 + paramInt10)] -= Sdot.sdot(i3 - i8 - 1, paramArrayOfFloat1, i8 + 1 - 1 + (i3 + 1 - 1) * paramInt4 + paramInt3, 1, paramArrayOfFloat4, i8 + 1 + paramInt2 - 1 + paramInt10, 1);
                    Slaln2.slaln2(true, 2, 1, f6, 1.0F, paramArrayOfFloat1, i3 - 1 + (i3 - 1) * paramInt4 + paramInt3, paramInt4, 1.0F, 1.0F, paramArrayOfFloat4, i3 + paramInt2 - 1 + paramInt10, paramInt2, f12, 0.0F, arrayOfFloat, 0, 2, localfloatW2, localfloatW4, localintW);
                    if ((localfloatW2.val == 1.0F ? 0 : 1) != 0) {
                      Sscal.sscal(paramInt2 - i8 + 1, localfloatW2.val, paramArrayOfFloat4, i8 + paramInt2 - 1 + paramInt10, 1);
                    }
                    paramArrayOfFloat4[(i3 + paramInt2 - 1 + paramInt10)] = arrayOfFloat[(1 - 1 + (1 - 1) * 2)];
                    paramArrayOfFloat4[(i3 + 1 + paramInt2 - 1 + paramInt10)] = arrayOfFloat[(2 - 1 + (1 - 1) * 2)];
                    f10 = Util.max(Math.abs(paramArrayOfFloat4[(i3 + paramInt2 - 1 + paramInt10)]), Math.abs(paramArrayOfFloat4[(i3 + 1 + paramInt2 - 1 + paramInt10)]), f10);
                    f9 = f2 / f10;
                  }
                }
                i3 += 1;
              }
              if ((bool3 ^ true))
              {
                Scopy.scopy(paramInt2 - i8 + 1, paramArrayOfFloat4, i8 + paramInt2 - 1 + paramInt10, 1, paramArrayOfFloat2, i8 - 1 + (i2 - 1) * paramInt6 + paramInt5, 1);
                n = Isamax.isamax(paramInt2 - i8 + 1, paramArrayOfFloat2, i8 - 1 + (i2 - 1) * paramInt6 + paramInt5, 1) + i8 - 1;
                f5 = 1.0F / Math.abs(paramArrayOfFloat2[(n - 1 + (i2 - 1) * paramInt6 + paramInt5)]);
                Sscal.sscal(paramInt2 - i8 + 1, f5, paramArrayOfFloat2, i8 - 1 + (i2 - 1) * paramInt6 + paramInt5, 1);
                i7 = 1;
                for (i11 = i8 - 1 - 1 + 1; i11 > 0; i11--)
                {
                  paramArrayOfFloat2[(i7 - 1 + (i2 - 1) * paramInt6 + paramInt5)] = 0.0F;
                  i7 += 1;
                }
              }
              else
              {
                if ((i8 >= paramInt2 ? 0 : 1) != 0) {
                  Sgemv.sgemv("N", paramInt2, paramInt2 - i8, 1.0F, paramArrayOfFloat2, 1 - 1 + (i8 + 1 - 1) * paramInt6 + paramInt5, paramInt6, paramArrayOfFloat4, i8 + 1 + paramInt2 - 1 + paramInt10, 1, paramArrayOfFloat4[(i8 + paramInt2 - 1 + paramInt10)], paramArrayOfFloat2, 1 - 1 + (i8 - 1) * paramInt6 + paramInt5, 1);
                }
                n = Isamax.isamax(paramInt2, paramArrayOfFloat2, 1 - 1 + (i8 - 1) * paramInt6 + paramInt5, 1);
                f5 = 1.0F / Math.abs(paramArrayOfFloat2[(n - 1 + (i8 - 1) * paramInt6 + paramInt5)]);
                Sscal.sscal(paramInt2, f5, paramArrayOfFloat2, 1 - 1 + (i8 - 1) * paramInt6 + paramInt5, 1);
              }
            }
            else
            {
              if ((Math.abs(paramArrayOfFloat1[(i8 - 1 + (i8 + 1 - 1) * paramInt4 + paramInt3)]) < Math.abs(paramArrayOfFloat1[(i8 + 1 - 1 + (i8 - 1) * paramInt4 + paramInt3)]) ? 0 : 1) != 0)
              {
                paramArrayOfFloat4[(i8 + paramInt2 - 1 + paramInt10)] = (f11 / paramArrayOfFloat1[(i8 - 1 + (i8 + 1 - 1) * paramInt4 + paramInt3)]);
                paramArrayOfFloat4[(i8 + 1 + i9 - 1 + paramInt10)] = 1.0F;
              }
              else
              {
                paramArrayOfFloat4[(i8 + paramInt2 - 1 + paramInt10)] = 1.0F;
                paramArrayOfFloat4[(i8 + 1 + i9 - 1 + paramInt10)] = (-(f11 / paramArrayOfFloat1[(i8 + 1 - 1 + (i8 - 1) * paramInt4 + paramInt3)]));
              }
              paramArrayOfFloat4[(i8 + 1 + paramInt2 - 1 + paramInt10)] = 0.0F;
              paramArrayOfFloat4[(i8 + i9 - 1 + paramInt10)] = 0.0F;
              i7 = i8 + 2;
              for (i11 = paramInt2 - (i8 + 2) + 1; i11 > 0; i11--)
              {
                paramArrayOfFloat4[(i7 + paramInt2 - 1 + paramInt10)] = (-(paramArrayOfFloat4[(i8 + paramInt2 - 1 + paramInt10)] * paramArrayOfFloat1[(i8 - 1 + (i7 - 1) * paramInt4 + paramInt3)]));
                paramArrayOfFloat4[(i7 + i9 - 1 + paramInt10)] = (-(paramArrayOfFloat4[(i8 + 1 + i9 - 1 + paramInt10)] * paramArrayOfFloat1[(i8 + 1 - 1 + (i7 - 1) * paramInt4 + paramInt3)]));
                i7 += 1;
              }
              f10 = 1.0F;
              f9 = f2;
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
                    if ((paramArrayOfFloat1[(i3 + 1 - 1 + (i3 - 1) * paramInt4 + paramInt3)] == 0.0F ? 0 : 1) != 0)
                    {
                      i5 = i3 + 1;
                      i6 = i3 + 2;
                    }
                  }
                  if ((i4 != i5 ? 0 : 1) != 0)
                  {
                    if ((paramArrayOfFloat4[(i3 - 1 + paramInt10)] <= f9 ? 0 : 1) != 0)
                    {
                      f4 = 1.0F / f10;
                      Sscal.sscal(paramInt2 - i8 + 1, f4, paramArrayOfFloat4, i8 + paramInt2 - 1 + paramInt10, 1);
                      Sscal.sscal(paramInt2 - i8 + 1, f4, paramArrayOfFloat4, i8 + i9 - 1 + paramInt10, 1);
                      f10 = 1.0F;
                      f9 = f2;
                    }
                    paramArrayOfFloat4[(i3 + paramInt2 - 1 + paramInt10)] -= Sdot.sdot(i3 - i8 - 2, paramArrayOfFloat1, i8 + 2 - 1 + (i3 - 1) * paramInt4 + paramInt3, 1, paramArrayOfFloat4, i8 + 2 + paramInt2 - 1 + paramInt10, 1);
                    paramArrayOfFloat4[(i3 + i9 - 1 + paramInt10)] -= Sdot.sdot(i3 - i8 - 2, paramArrayOfFloat1, i8 + 2 - 1 + (i3 - 1) * paramInt4 + paramInt3, 1, paramArrayOfFloat4, i8 + 2 + i9 - 1 + paramInt10, 1);
                    Slaln2.slaln2(false, 1, 2, f6, 1.0F, paramArrayOfFloat1, i3 - 1 + (i3 - 1) * paramInt4 + paramInt3, paramInt4, 1.0F, 1.0F, paramArrayOfFloat4, i3 + paramInt2 - 1 + paramInt10, paramInt2, f12, -f11, arrayOfFloat, 0, 2, localfloatW2, localfloatW4, localintW);
                    if ((localfloatW2.val == 1.0F ? 0 : 1) != 0)
                    {
                      Sscal.sscal(paramInt2 - i8 + 1, localfloatW2.val, paramArrayOfFloat4, i8 + paramInt2 - 1 + paramInt10, 1);
                      Sscal.sscal(paramInt2 - i8 + 1, localfloatW2.val, paramArrayOfFloat4, i8 + i9 - 1 + paramInt10, 1);
                    }
                    paramArrayOfFloat4[(i3 + paramInt2 - 1 + paramInt10)] = arrayOfFloat[(1 - 1 + (1 - 1) * 2)];
                    paramArrayOfFloat4[(i3 + i9 - 1 + paramInt10)] = arrayOfFloat[(1 - 1 + (2 - 1) * 2)];
                    f10 = Util.max(Math.abs(paramArrayOfFloat4[(i3 + paramInt2 - 1 + paramInt10)]), Math.abs(paramArrayOfFloat4[(i3 + i9 - 1 + paramInt10)]), f10);
                    f9 = f2 / f10;
                  }
                  else
                  {
                    f1 = Math.max(paramArrayOfFloat4[(i3 - 1 + paramInt10)], paramArrayOfFloat4[(i3 + 1 - 1 + paramInt10)]);
                    if ((f1 <= f9 ? 0 : 1) != 0)
                    {
                      f4 = 1.0F / f10;
                      Sscal.sscal(paramInt2 - i8 + 1, f4, paramArrayOfFloat4, i8 + paramInt2 - 1 + paramInt10, 1);
                      Sscal.sscal(paramInt2 - i8 + 1, f4, paramArrayOfFloat4, i8 + i9 - 1 + paramInt10, 1);
                      f10 = 1.0F;
                      f9 = f2;
                    }
                    paramArrayOfFloat4[(i3 + paramInt2 - 1 + paramInt10)] -= Sdot.sdot(i3 - i8 - 2, paramArrayOfFloat1, i8 + 2 - 1 + (i3 - 1) * paramInt4 + paramInt3, 1, paramArrayOfFloat4, i8 + 2 + paramInt2 - 1 + paramInt10, 1);
                    paramArrayOfFloat4[(i3 + i9 - 1 + paramInt10)] -= Sdot.sdot(i3 - i8 - 2, paramArrayOfFloat1, i8 + 2 - 1 + (i3 - 1) * paramInt4 + paramInt3, 1, paramArrayOfFloat4, i8 + 2 + i9 - 1 + paramInt10, 1);
                    paramArrayOfFloat4[(i3 + 1 + paramInt2 - 1 + paramInt10)] -= Sdot.sdot(i3 - i8 - 2, paramArrayOfFloat1, i8 + 2 - 1 + (i3 + 1 - 1) * paramInt4 + paramInt3, 1, paramArrayOfFloat4, i8 + 2 + paramInt2 - 1 + paramInt10, 1);
                    paramArrayOfFloat4[(i3 + 1 + i9 - 1 + paramInt10)] -= Sdot.sdot(i3 - i8 - 2, paramArrayOfFloat1, i8 + 2 - 1 + (i3 + 1 - 1) * paramInt4 + paramInt3, 1, paramArrayOfFloat4, i8 + 2 + i9 - 1 + paramInt10, 1);
                    Slaln2.slaln2(true, 2, 2, f6, 1.0F, paramArrayOfFloat1, i3 - 1 + (i3 - 1) * paramInt4 + paramInt3, paramInt4, 1.0F, 1.0F, paramArrayOfFloat4, i3 + paramInt2 - 1 + paramInt10, paramInt2, f12, -f11, arrayOfFloat, 0, 2, localfloatW2, localfloatW4, localintW);
                    if ((localfloatW2.val == 1.0F ? 0 : 1) != 0)
                    {
                      Sscal.sscal(paramInt2 - i8 + 1, localfloatW2.val, paramArrayOfFloat4, i8 + paramInt2 - 1 + paramInt10, 1);
                      Sscal.sscal(paramInt2 - i8 + 1, localfloatW2.val, paramArrayOfFloat4, i8 + i9 - 1 + paramInt10, 1);
                    }
                    paramArrayOfFloat4[(i3 + paramInt2 - 1 + paramInt10)] = arrayOfFloat[(1 - 1 + (1 - 1) * 2)];
                    paramArrayOfFloat4[(i3 + i9 - 1 + paramInt10)] = arrayOfFloat[(1 - 1 + (2 - 1) * 2)];
                    paramArrayOfFloat4[(i3 + 1 + paramInt2 - 1 + paramInt10)] = arrayOfFloat[(2 - 1 + (1 - 1) * 2)];
                    paramArrayOfFloat4[(i3 + 1 + i9 - 1 + paramInt10)] = arrayOfFloat[(2 - 1 + (2 - 1) * 2)];
                    f10 = Math.max(Math.max(Util.max(Math.abs(arrayOfFloat[(1 - 1 + (1 - 1) * 2)]), Math.abs(arrayOfFloat[(1 - 1 + (2 - 1) * 2)]), Math.abs(arrayOfFloat[(2 - 1 + (1 - 1) * 2)])), Math.abs(arrayOfFloat[(2 - 1 + (2 - 1) * 2)])), f10);
                    f9 = f2 / f10;
                  }
                }
                i3 += 1;
              }
              if ((bool3 ^ true))
              {
                Scopy.scopy(paramInt2 - i8 + 1, paramArrayOfFloat4, i8 + paramInt2 - 1 + paramInt10, 1, paramArrayOfFloat2, i8 - 1 + (i2 - 1) * paramInt6 + paramInt5, 1);
                Scopy.scopy(paramInt2 - i8 + 1, paramArrayOfFloat4, i8 + i9 - 1 + paramInt10, 1, paramArrayOfFloat2, i8 - 1 + (i2 + 1 - 1) * paramInt6 + paramInt5, 1);
                f3 = 0.0F;
                i7 = i8;
                for (i11 = paramInt2 - i8 + 1; i11 > 0; i11--)
                {
                  f3 = Math.max(f3, Math.abs(paramArrayOfFloat2[(i7 - 1 + (i2 - 1) * paramInt6 + paramInt5)]) + Math.abs(paramArrayOfFloat2[(i7 - 1 + (i2 + 1 - 1) * paramInt6 + paramInt5)]));
                  i7 += 1;
                }
                f5 = 1.0F / f3;
                Sscal.sscal(paramInt2 - i8 + 1, f5, paramArrayOfFloat2, i8 - 1 + (i2 - 1) * paramInt6 + paramInt5, 1);
                Sscal.sscal(paramInt2 - i8 + 1, f5, paramArrayOfFloat2, i8 - 1 + (i2 + 1 - 1) * paramInt6 + paramInt5, 1);
                i7 = 1;
                for (i11 = i8 - 1 - 1 + 1; i11 > 0; i11--)
                {
                  paramArrayOfFloat2[(i7 - 1 + (i2 - 1) * paramInt6 + paramInt5)] = 0.0F;
                  paramArrayOfFloat2[(i7 - 1 + (i2 + 1 - 1) * paramInt6 + paramInt5)] = 0.0F;
                  i7 += 1;
                }
              }
              else
              {
                if ((i8 >= paramInt2 - 1 ? 0 : 1) != 0)
                {
                  Sgemv.sgemv("N", paramInt2, paramInt2 - i8 - 1, 1.0F, paramArrayOfFloat2, 1 - 1 + (i8 + 2 - 1) * paramInt6 + paramInt5, paramInt6, paramArrayOfFloat4, i8 + 2 + paramInt2 - 1 + paramInt10, 1, paramArrayOfFloat4[(i8 + paramInt2 - 1 + paramInt10)], paramArrayOfFloat2, 1 - 1 + (i8 - 1) * paramInt6 + paramInt5, 1);
                  Sgemv.sgemv("N", paramInt2, paramInt2 - i8 - 1, 1.0F, paramArrayOfFloat2, 1 - 1 + (i8 + 2 - 1) * paramInt6 + paramInt5, paramInt6, paramArrayOfFloat4, i8 + 2 + i9 - 1 + paramInt10, 1, paramArrayOfFloat4[(i8 + 1 + i9 - 1 + paramInt10)], paramArrayOfFloat2, 1 - 1 + (i8 + 1 - 1) * paramInt6 + paramInt5, 1);
                }
                else
                {
                  Sscal.sscal(paramInt2, paramArrayOfFloat4[(i8 + paramInt2 - 1 + paramInt10)], paramArrayOfFloat2, 1 - 1 + (i8 - 1) * paramInt6 + paramInt5, 1);
                  Sscal.sscal(paramInt2, paramArrayOfFloat4[(i8 + 1 + i9 - 1 + paramInt10)], paramArrayOfFloat2, 1 - 1 + (i8 + 1 - 1) * paramInt6 + paramInt5, 1);
                }
                f3 = 0.0F;
                i7 = 1;
                for (i11 = paramInt2 - 1 + 1; i11 > 0; i11--)
                {
                  f3 = Math.max(f3, Math.abs(paramArrayOfFloat2[(i7 - 1 + (i8 - 1) * paramInt6 + paramInt5)]) + Math.abs(paramArrayOfFloat2[(i7 - 1 + (i8 + 1 - 1) * paramInt6 + paramInt5)]));
                  i7 += 1;
                }
                f5 = 1.0F / f3;
                Sscal.sscal(paramInt2, f5, paramArrayOfFloat2, 1 - 1 + (i8 - 1) * paramInt6 + paramInt5, 1);
                Sscal.sscal(paramInt2, f5, paramArrayOfFloat2, 1 - 1 + (i8 + 1 - 1) * paramInt6 + paramInt5, 1);
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
// INTERNAL ERROR //

/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.lapack.Strevc
 * JD-Core Version:    0.7.0.1
 */