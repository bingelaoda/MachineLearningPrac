package org.netlib.arpack;

public final class Iset
{
  public static void iset(int paramInt1, int paramInt2, int[] paramArrayOfInt, int paramInt3, int paramInt4)
  {
    int i = 0;
    i = 1;
    for (int j = paramInt1 - 1 + 1; j > 0; j--)
    {
      paramArrayOfInt[(i - 1 + paramInt3)] = paramInt2;
      i += 1;
    }
  }
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.arpack.Iset
 * JD-Core Version:    0.7.0.1
 */