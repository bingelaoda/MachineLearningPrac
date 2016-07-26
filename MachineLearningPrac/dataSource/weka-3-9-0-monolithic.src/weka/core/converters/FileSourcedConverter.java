package weka.core.converters;

import java.io.File;
import java.io.IOException;

public abstract interface FileSourcedConverter
{
  public abstract String getFileExtension();
  
  public abstract String[] getFileExtensions();
  
  public abstract String getFileDescription();
  
  public abstract void setFile(File paramFile)
    throws IOException;
  
  public abstract File retrieveFile();
  
  public abstract void setUseRelativePath(boolean paramBoolean);
  
  public abstract boolean getUseRelativePath();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.FileSourcedConverter
 * JD-Core Version:    0.7.0.1
 */