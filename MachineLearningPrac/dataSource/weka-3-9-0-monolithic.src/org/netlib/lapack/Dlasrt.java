package org.netlib.lapack;

import org.netlib.err.Xerbla;
import org.netlib.util.intW;

public final class Dlasrt
{
  public static void dlasrt(String paramString, int paramInt1, double[] paramArrayOfDouble, int paramInt2, intW paramintW)
  {
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    int i1 = 0;
    double d1 = 0.0D;
    double d2 = 0.0D;
    double d3 = 0.0D;
    double d4 = 0.0D;
    double d5 = 0.0D;
    int[] arrayOfInt = new int[2 * 32];
    paramintW.val = 0;
    i = -1;
    if (Lsame.lsame(paramString, "D")) {
      i = 0;
    } else if (Lsame.lsame(paramString, "I")) {
      i = 1;
    }
    if ((i != -1 ? 0 : 1) != 0) {
      paramintW.val = -1;
    } else if ((paramInt1 >= 0 ? 0 : 1) != 0) {
      paramintW.val = -2;
    }
    if ((paramintW.val == 0 ? 0 : 1) != 0)
    {
      Xerbla.xerbla("DLASRT", -paramintW.val);
      return;
    }
    if ((paramInt1 > 1 ? 0 : 1) != 0) {
      return;
    }
    i1 = 1;
    arrayOfInt[(1 - 1 + (1 - 1) * 2)] = 1;
    arrayOfInt[(2 - 1 + (1 - 1) * 2)] = paramInt1;
    do
    {
      n = arrayOfInt[(1 - 1 + (i1 - 1) * 2)];
      j = arrayOfInt[(2 - 1 + (i1 - 1) * 2)];
      i1 -= 1;
      if ((j - n > 20 ? 0 : 1) != 0) {}
      if (((j - n <= 0 ? 0 : 1) != 0 ? 1 : 0) != 0)
      {
        int i2;
        int i3;
        if ((i != 0 ? 0 : 1) != 0)
        {
          k = n + 1;
          for (i2 = j - (n + 1) + 1; i2 > 0; i2--)
          {
            m = k;
            for (i3 = (n + 1 - k + -1) / -1; i3 > 0; i3--)
            {
              if ((paramArrayOfDouble[(m - 1 + paramInt2)] <= paramArrayOfDouble[(m - 1 - 1 + paramInt2)] ? 0 : 1) != 0)
              {
                d4 = paramArrayOfDouble[(m - 1 + paramInt2)];
                paramArrayOfDouble[(m - 1 + paramInt2)] = paramArrayOfDouble[(m - 1 - 1 + paramInt2)];
                paramArrayOfDouble[(m - 1 - 1 + paramInt2)] = d4;
              }
              else
              {
                break;
              }
              m += -1;
            }
            k += 1;
          }
        }
        else
        {
          k = n + 1;
          for (i2 = j - (n + 1) + 1; i2 > 0; i2--)
          {
            m = k;
            for (i3 = (n + 1 - k + -1) / -1; i3 > 0; i3--)
            {
              if ((paramArrayOfDouble[(m - 1 + paramInt2)] >= paramArrayOfDouble[(m - 1 - 1 + paramInt2)] ? 0 : 1) != 0)
              {
                d4 = paramArrayOfDouble[(m - 1 + paramInt2)];
                paramArrayOfDouble[(m - 1 + paramInt2)] = paramArrayOfDouble[(m - 1 - 1 + paramInt2)];
                paramArrayOfDouble[(m - 1 - 1 + paramInt2)] = d4;
              }
              else
              {
                break;
              }
              m += -1;
            }
            k += 1;
          }
        }
      }
      else if ((j - n <= 20 ? 0 : 1) != 0)
      {
        d1 = paramArrayOfDouble[(n - 1 + paramInt2)];
        d2 = paramArrayOfDouble[(j - 1 + paramInt2)];
        k = (n + j) / 2;
        d3 = paramArrayOfDouble[(k - 1 + paramInt2)];
        if ((d1 >= d2 ? 0 : 1) != 0)
        {
          if ((d3 >= d1 ? 0 : 1) != 0) {
            d4 = d1;
          } else if ((d3 >= d2 ? 0 : 1) != 0) {
            d4 = d3;
          } else {
            d4 = d2;
          }
        }
        else if ((d3 >= d2 ? 0 : 1) != 0) {
          d4 = d2;
        } else if ((d3 >= d1 ? 0 : 1) != 0) {
          d4 = d3;
        } else {
          d4 = d1;
        }
        if ((i != 0 ? 0 : 1) != 0)
        {
          k = n - 1;
          m = j + 1;
          for (;;)
          {
            m -= 1;
            if ((paramArrayOfDouble[(m - 1 + paramInt2)] >= d4 ? 0 : 1) == 0)
            {
              do
              {
                k += 1;
              } while ((paramArrayOfDouble[(k - 1 + paramInt2)] <= d4 ? 0 : 1) != 0);
              if ((k >= m ? 0 : 1) == 0) {
                break;
              }
              d5 = paramArrayOfDouble[(k - 1 + paramInt2)];
              paramArrayOfDouble[(k - 1 + paramInt2)] = paramArrayOfDouble[(m - 1 + paramInt2)];
              paramArrayOfDouble[(m - 1 + paramInt2)] = d5;
            }
          }
          if ((m - n <= j - m - 1 ? 0 : 1) != 0)
          {
            i1 += 1;
            arrayOfInt[(1 - 1 + (i1 - 1) * 2)] = n;
            arrayOfInt[(2 - 1 + (i1 - 1) * 2)] = m;
            i1 += 1;
            arrayOfInt[(1 - 1 + (i1 - 1) * 2)] = (m + 1);
            arrayOfInt[(2 - 1 + (i1 - 1) * 2)] = j;
          }
          else
          {
            i1 += 1;
            arrayOfInt[(1 - 1 + (i1 - 1) * 2)] = (m + 1);
            arrayOfInt[(2 - 1 + (i1 - 1) * 2)] = j;
            i1 += 1;
            arrayOfInt[(1 - 1 + (i1 - 1) * 2)] = n;
            arrayOfInt[(2 - 1 + (i1 - 1) * 2)] = m;
          }
        }
        else
        {
          k = n - 1;
          m = j + 1;
          for (;;)
          {
            m -= 1;
            if ((paramArrayOfDouble[(m - 1 + paramInt2)] <= d4 ? 0 : 1) == 0)
            {
              do
              {
                k += 1;
              } while ((paramArrayOfDouble[(k - 1 + paramInt2)] >= d4 ? 0 : 1) != 0);
              if ((k >= m ? 0 : 1) == 0) {
                break;
              }
              d5 = paramArrayOfDouble[(k - 1 + paramInt2)];
              paramArrayOfDouble[(k - 1 + paramInt2)] = paramArrayOfDouble[(m - 1 + paramInt2)];
              paramArrayOfDouble[(m - 1 + paramInt2)] = d5;
            }
          }
          if ((m - n <= j - m - 1 ? 0 : 1) != 0)
          {
            i1 += 1;
            arrayOfInt[(1 - 1 + (i1 - 1) * 2)] = n;
            arrayOfInt[(2 - 1 + (i1 - 1) * 2)] = m;
            i1 += 1;
            arrayOfInt[(1 - 1 + (i1 - 1) * 2)] = (m + 1);
            arrayOfInt[(2 - 1 + (i1 - 1) * 2)] = j;
          }
          else
          {
            i1 += 1;
            arrayOfInt[(1 - 1 + (i1 - 1) * 2)] = (m + 1);
            arrayOfInt[(2 - 1 + (i1 - 1) * 2)] = j;
            i1 += 1;
            arrayOfInt[(1 - 1 + (i1 - 1) * 2)] = n;
            arrayOfInt[(2 - 1 + (i1 - 1) * 2)] = m;
          }
        }
      }
    } while ((i1 <= 0 ? 0 : 1) != 0);
  }
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.lapack.Dlasrt
 * JD-Core Version:    0.7.0.1
 */