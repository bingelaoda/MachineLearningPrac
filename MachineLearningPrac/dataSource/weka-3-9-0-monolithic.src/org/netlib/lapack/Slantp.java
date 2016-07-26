package org.netlib.lapack;

import org.netlib.util.floatW;

public final class Slantp
{
  public static float slantp(String paramString1, String paramString2, String paramString3, int paramInt1, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3)
  {
    boolean bool = false;
    int i = 0;
    int j = 0;
    int k = 0;
    floatW localfloatW1 = new floatW(0.0F);
    floatW localfloatW2 = new floatW(0.0F);
    float f1 = 0.0F;
    float f2 = 0.0F;
    if ((paramInt1 != 0 ? 0 : 1) != 0)
    {
      f1 = 0.0F;
    }
    else
    {
      int m;
      int n;
      if (Lsame.lsame(paramString1, "M"))
      {
        k = 1;
        if (Lsame.lsame(paramString3, "U"))
        {
          f1 = 1.0F;
          if (Lsame.lsame(paramString2, "U"))
          {
            j = 1;
            for (m = paramInt1 - 1 + 1; m > 0; m--)
            {
              i = k;
              for (n = k + j - 2 - k + 1; n > 0; n--)
              {
                f1 = Math.max(f1, Math.abs(paramArrayOfFloat1[(i - 1 + paramInt2)]));
                i += 1;
              }
              k += j;
              j += 1;
            }
          }
          else
          {
            j = 1;
            for (m = paramInt1 - 1 + 1; m > 0; m--)
            {
              i = k + 1;
              for (n = k + paramInt1 - j - (k + 1) + 1; n > 0; n--)
              {
                f1 = Math.max(f1, Math.abs(paramArrayOfFloat1[(i - 1 + paramInt2)]));
                i += 1;
              }
              k = k + paramInt1 - j + 1;
              j += 1;
            }
          }
        }
        else
        {
          f1 = 0.0F;
          if (Lsame.lsame(paramString2, "U"))
          {
            j = 1;
            for (m = paramInt1 - 1 + 1; m > 0; m--)
            {
              i = k;
              for (n = k + j - 1 - k + 1; n > 0; n--)
              {
                f1 = Math.max(f1, Math.abs(paramArrayOfFloat1[(i - 1 + paramInt2)]));
                i += 1;
              }
              k += j;
              j += 1;
            }
          }
          else
          {
            j = 1;
            for (m = paramInt1 - 1 + 1; m > 0; m--)
            {
              i = k;
              for (n = k + paramInt1 - j - k + 1; n > 0; n--)
              {
                f1 = Math.max(f1, Math.abs(paramArrayOfFloat1[(i - 1 + paramInt2)]));
                i += 1;
              }
              k = k + paramInt1 - j + 1;
              j += 1;
            }
          }
        }
      }
      else if (((!Lsame.lsame(paramString1, "O")) && (!paramString1.regionMatches(0, "1", 0, 1)) ? 0 : 1) != 0)
      {
        f1 = 0.0F;
        k = 1;
        bool = Lsame.lsame(paramString3, "U");
        if (Lsame.lsame(paramString2, "U"))
        {
          j = 1;
          for (m = paramInt1 - 1 + 1; m > 0; m--)
          {
            if (bool)
            {
              localfloatW2.val = 1.0F;
              i = k;
              for (n = k + j - 2 - k + 1; n > 0; n--)
              {
                localfloatW2.val += Math.abs(paramArrayOfFloat1[(i - 1 + paramInt2)]);
                i += 1;
              }
            }
            else
            {
              localfloatW2.val = 0.0F;
              i = k;
              for (n = k + j - 1 - k + 1; n > 0; n--)
              {
                localfloatW2.val += Math.abs(paramArrayOfFloat1[(i - 1 + paramInt2)]);
                i += 1;
              }
            }
            k += j;
            f1 = Math.max(f1, localfloatW2.val);
            j += 1;
          }
        }
        else
        {
          j = 1;
          for (m = paramInt1 - 1 + 1; m > 0; m--)
          {
            if (bool)
            {
              localfloatW2.val = 1.0F;
              i = k + 1;
              for (n = k + paramInt1 - j - (k + 1) + 1; n > 0; n--)
              {
                localfloatW2.val += Math.abs(paramArrayOfFloat1[(i - 1 + paramInt2)]);
                i += 1;
              }
            }
            else
            {
              localfloatW2.val = 0.0F;
              i = k;
              for (n = k + paramInt1 - j - k + 1; n > 0; n--)
              {
                localfloatW2.val += Math.abs(paramArrayOfFloat1[(i - 1 + paramInt2)]);
                i += 1;
              }
            }
            k = k + paramInt1 - j + 1;
            f1 = Math.max(f1, localfloatW2.val);
            j += 1;
          }
        }
      }
      else if (Lsame.lsame(paramString1, "I"))
      {
        k = 1;
        if (Lsame.lsame(paramString2, "U"))
        {
          if (Lsame.lsame(paramString3, "U"))
          {
            i = 1;
            for (m = paramInt1 - 1 + 1; m > 0; m--)
            {
              paramArrayOfFloat2[(i - 1 + paramInt3)] = 1.0F;
              i += 1;
            }
            j = 1;
            for (m = paramInt1 - 1 + 1; m > 0; m--)
            {
              i = 1;
              for (n = j - 1 - 1 + 1; n > 0; n--)
              {
                paramArrayOfFloat2[(i - 1 + paramInt3)] += Math.abs(paramArrayOfFloat1[(k - 1 + paramInt2)]);
                k += 1;
                i += 1;
              }
              k += 1;
              j += 1;
            }
          }
          else
          {
            i = 1;
            for (m = paramInt1 - 1 + 1; m > 0; m--)
            {
              paramArrayOfFloat2[(i - 1 + paramInt3)] = 0.0F;
              i += 1;
            }
            j = 1;
            for (m = paramInt1 - 1 + 1; m > 0; m--)
            {
              i = 1;
              for (n = j - 1 + 1; n > 0; n--)
              {
                paramArrayOfFloat2[(i - 1 + paramInt3)] += Math.abs(paramArrayOfFloat1[(k - 1 + paramInt2)]);
                k += 1;
                i += 1;
              }
              j += 1;
            }
          }
        }
        else if (Lsame.lsame(paramString3, "U"))
        {
          i = 1;
          for (m = paramInt1 - 1 + 1; m > 0; m--)
          {
            paramArrayOfFloat2[(i - 1 + paramInt3)] = 1.0F;
            i += 1;
          }
          j = 1;
          for (m = paramInt1 - 1 + 1; m > 0; m--)
          {
            k += 1;
            i = j + 1;
            for (n = paramInt1 - (j + 1) + 1; n > 0; n--)
            {
              paramArrayOfFloat2[(i - 1 + paramInt3)] += Math.abs(paramArrayOfFloat1[(k - 1 + paramInt2)]);
              k += 1;
              i += 1;
            }
            j += 1;
          }
        }
        else
        {
          i = 1;
          for (m = paramInt1 - 1 + 1; m > 0; m--)
          {
            paramArrayOfFloat2[(i - 1 + paramInt3)] = 0.0F;
            i += 1;
          }
          j = 1;
          for (m = paramInt1 - 1 + 1; m > 0; m--)
          {
            i = j;
            for (n = paramInt1 - j + 1; n > 0; n--)
            {
              paramArrayOfFloat2[(i - 1 + paramInt3)] += Math.abs(paramArrayOfFloat1[(k - 1 + paramInt2)]);
              k += 1;
              i += 1;
            }
            j += 1;
          }
        }
        f1 = 0.0F;
        i = 1;
        for (m = paramInt1 - 1 + 1; m > 0; m--)
        {
          f1 = Math.max(f1, paramArrayOfFloat2[(i - 1 + paramInt3)]);
          i += 1;
        }
      }
      else if (((!Lsame.lsame(paramString1, "F")) && (!Lsame.lsame(paramString1, "E")) ? 0 : 1) != 0)
      {
        if (Lsame.lsame(paramString2, "U"))
        {
          if (Lsame.lsame(paramString3, "U"))
          {
            localfloatW1.val = 1.0F;
            localfloatW2.val = paramInt1;
            k = 2;
            j = 2;
            for (m = paramInt1 - 2 + 1; m > 0; m--)
            {
              Slassq.slassq(j - 1, paramArrayOfFloat1, k - 1 + paramInt2, 1, localfloatW1, localfloatW2);
              k += j;
              j += 1;
            }
          }
          else
          {
            localfloatW1.val = 0.0F;
            localfloatW2.val = 1.0F;
            k = 1;
            j = 1;
            for (m = paramInt1 - 1 + 1; m > 0; m--)
            {
              Slassq.slassq(j, paramArrayOfFloat1, k - 1 + paramInt2, 1, localfloatW1, localfloatW2);
              k += j;
              j += 1;
            }
          }
        }
        else if (Lsame.lsame(paramString3, "U"))
        {
          localfloatW1.val = 1.0F;
          localfloatW2.val = paramInt1;
          k = 2;
          j = 1;
          for (m = paramInt1 - 1 - 1 + 1; m > 0; m--)
          {
            Slassq.slassq(paramInt1 - j, paramArrayOfFloat1, k - 1 + paramInt2, 1, localfloatW1, localfloatW2);
            k = k + paramInt1 - j + 1;
            j += 1;
          }
        }
        else
        {
          localfloatW1.val = 0.0F;
          localfloatW2.val = 1.0F;
          k = 1;
          j = 1;
          for (m = paramInt1 - 1 + 1; m > 0; m--)
          {
            Slassq.slassq(paramInt1 - j + 1, paramArrayOfFloat1, k - 1 + paramInt2, 1, localfloatW1, localfloatW2);
            k = k + paramInt1 - j + 1;
            j += 1;
          }
        }
        f1 = localfloatW1.val * (float)Math.sqrt(localfloatW2.val);
      }
    }
    f2 = f1;
    return f2;
  }
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.lapack.Slantp
 * JD-Core Version:    0.7.0.1
 */