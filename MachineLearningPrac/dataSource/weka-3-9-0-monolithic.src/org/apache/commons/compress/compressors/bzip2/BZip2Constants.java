package org.apache.commons.compress.compressors.bzip2;

abstract interface BZip2Constants
{
  public static final int BASEBLOCKSIZE = 100000;
  public static final int MAX_ALPHA_SIZE = 258;
  public static final int MAX_CODE_LEN = 23;
  public static final int RUNA = 0;
  public static final int RUNB = 1;
  public static final int N_GROUPS = 6;
  public static final int G_SIZE = 50;
  public static final int N_ITERS = 4;
  public static final int MAX_SELECTORS = 18002;
  public static final int NUM_OVERSHOOT_BYTES = 20;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.compressors.bzip2.BZip2Constants
 * JD-Core Version:    0.7.0.1
 */