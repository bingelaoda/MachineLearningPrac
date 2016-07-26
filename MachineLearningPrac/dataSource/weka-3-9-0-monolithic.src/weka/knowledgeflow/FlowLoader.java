package weka.knowledgeflow;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import weka.core.WekaException;
import weka.gui.Logger;

public abstract interface FlowLoader
{
  public abstract void setLog(Logger paramLogger);
  
  public abstract String getFlowFileExtension();
  
  public abstract String getFlowFileExtensionDescription();
  
  public abstract Flow readFlow(File paramFile)
    throws WekaException;
  
  public abstract Flow readFlow(InputStream paramInputStream)
    throws WekaException;
  
  public abstract Flow readFlow(Reader paramReader)
    throws WekaException;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.FlowLoader
 * JD-Core Version:    0.7.0.1
 */