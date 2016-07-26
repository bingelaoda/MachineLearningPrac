package org.apache.commons.io.output;

import java.io.Writer;

public class NullWriter
  extends Writer
{
  public void write(int idx) {}
  
  public void write(char[] chr) {}
  
  public void write(char[] chr, int st, int end) {}
  
  public void write(String str) {}
  
  public void write(String str, int st, int end) {}
  
  public void flush() {}
  
  public void close() {}
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.output.NullWriter
 * JD-Core Version:    0.7.0.1
 */