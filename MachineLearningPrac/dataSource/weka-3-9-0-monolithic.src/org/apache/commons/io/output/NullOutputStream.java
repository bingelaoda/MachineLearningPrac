package org.apache.commons.io.output;

import java.io.IOException;
import java.io.OutputStream;

public class NullOutputStream
  extends OutputStream
{
  public void write(byte[] b, int off, int len) {}
  
  public void write(int b) {}
  
  public void write(byte[] b)
    throws IOException
  {}
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.output.NullOutputStream
 * JD-Core Version:    0.7.0.1
 */