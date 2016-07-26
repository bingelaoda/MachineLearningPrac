package org.netlib.lapack;

public final class Lsamen
{
  public static boolean lsamen(int paramInt, String paramString1, String paramString2)
  {
    int i = 0;
    boolean bool = false;
    bool = false;
    if ((paramString1.length() >= paramInt ? 0 : 1) == 0) {}
    if (((paramString2.length() >= paramInt ? 0 : 1) == 0 ? 0 : 1) == 0)
    {
      i = 1;
      int j = paramInt - 1 + 1;
      while (!(Lsame.lsame(paramString1.substring(i + -1, i), paramString2.substring(i + -1, i)) ^ true))
      {
        i += 1;
        j--;
        if (j <= 0) {
          bool = true;
        }
      }
    }
    return bool;
  }
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.lapack.Lsamen
 * JD-Core Version:    0.7.0.1
 */