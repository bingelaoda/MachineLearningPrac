package org.apache.commons.compress.archivers.zip;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract interface ZipEncoding
{
  public abstract boolean canEncode(String paramString);
  
  public abstract ByteBuffer encode(String paramString)
    throws IOException;
  
  public abstract String decode(byte[] paramArrayOfByte)
    throws IOException;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.ZipEncoding
 * JD-Core Version:    0.7.0.1
 */