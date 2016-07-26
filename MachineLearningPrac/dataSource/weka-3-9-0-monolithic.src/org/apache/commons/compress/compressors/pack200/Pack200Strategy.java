package org.apache.commons.compress.compressors.pack200;

import java.io.IOException;

public enum Pack200Strategy
{
  IN_MEMORY,  TEMP_FILE;
  
  private Pack200Strategy() {}
  
  abstract StreamBridge newStreamBridge()
    throws IOException;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.compressors.pack200.Pack200Strategy
 * JD-Core Version:    0.7.0.1
 */