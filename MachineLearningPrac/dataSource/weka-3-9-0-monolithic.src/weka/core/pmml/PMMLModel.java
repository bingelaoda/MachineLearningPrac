package weka.core.pmml;

import org.w3c.dom.Document;
import weka.gui.Logger;

public abstract interface PMMLModel
{
  public abstract void setPMMLVersion(Document paramDocument);
  
  public abstract String getPMMLVersion();
  
  public abstract void setCreatorApplication(Document paramDocument);
  
  public abstract String getCreatorApplication();
  
  public abstract MiningSchema getMiningSchema();
  
  public abstract void setLog(Logger paramLogger);
  
  public abstract Logger getLog();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.PMMLModel
 * JD-Core Version:    0.7.0.1
 */