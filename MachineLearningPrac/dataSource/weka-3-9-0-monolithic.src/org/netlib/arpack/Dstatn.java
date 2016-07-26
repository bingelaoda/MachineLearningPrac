package org.netlib.arpack;

public final class Dstatn
{
  public static float t0 = 0.0F;
  public static float t1 = 0.0F;
  public static float t2 = 0.0F;
  public static float t3 = 0.0F;
  public static float t4 = 0.0F;
  public static float t5 = 0.0F;
  
  public static void dstatn()
  {
    arpack_timing.nopx.val = 0;
    arpack_timing.nbx.val = 0;
    arpack_timing.nrorth.val = 0;
    arpack_timing.nitref.val = 0;
    arpack_timing.nrstrt.val = 0;
    arpack_timing.tnaupd.val = ((float)0.0D);
    arpack_timing.tnaup2.val = ((float)0.0D);
    arpack_timing.tnaitr.val = ((float)0.0D);
    arpack_timing.tneigh.val = ((float)0.0D);
    arpack_timing.tngets.val = ((float)0.0D);
    arpack_timing.tnapps.val = ((float)0.0D);
    arpack_timing.tnconv.val = ((float)0.0D);
    arpack_timing.titref.val = ((float)0.0D);
    arpack_timing.tgetv0.val = ((float)0.0D);
    arpack_timing.trvec.val = ((float)0.0D);
    arpack_timing.tmvopx.val = ((float)0.0D);
    arpack_timing.tmvbx.val = ((float)0.0D);
  }
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.arpack.Dstatn
 * JD-Core Version:    0.7.0.1
 */