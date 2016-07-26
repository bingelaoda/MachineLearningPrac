package org.apache.commons.compress.parallel;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public abstract interface ScatterGatherBackingStore
  extends Closeable
{
  public abstract InputStream getInputStream()
    throws IOException;
  
  public abstract void writeOut(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException;
  
  public abstract void closeForWriting()
    throws IOException;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.parallel.ScatterGatherBackingStore
 * JD-Core Version:    0.7.0.1
 */