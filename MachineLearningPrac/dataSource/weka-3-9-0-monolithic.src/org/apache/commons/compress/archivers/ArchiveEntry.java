package org.apache.commons.compress.archivers;

import java.util.Date;

public abstract interface ArchiveEntry
{
  public static final long SIZE_UNKNOWN = -1L;
  
  public abstract String getName();
  
  public abstract long getSize();
  
  public abstract boolean isDirectory();
  
  public abstract Date getLastModifiedDate();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.ArchiveEntry
 * JD-Core Version:    0.7.0.1
 */