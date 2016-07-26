package org.apache.commons.compress.parallel;

import java.io.IOException;

public abstract interface ScatterGatherBackingStoreSupplier
{
  public abstract ScatterGatherBackingStore get()
    throws IOException;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.parallel.ScatterGatherBackingStoreSupplier
 * JD-Core Version:    0.7.0.1
 */