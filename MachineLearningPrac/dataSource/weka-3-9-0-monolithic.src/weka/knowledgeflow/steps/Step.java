package weka.knowledgeflow.steps;

import java.util.List;
import java.util.Map;
import weka.core.Defaults;
import weka.core.Instances;
import weka.core.WekaException;
import weka.gui.knowledgeflow.StepInteractiveViewer;
import weka.knowledgeflow.Data;
import weka.knowledgeflow.StepManager;

public abstract interface Step
{
  public abstract StepManager getStepManager();
  
  public abstract void setStepManager(StepManager paramStepManager);
  
  public abstract String getName();
  
  public abstract void setName(String paramString);
  
  public abstract void stepInit()
    throws WekaException;
  
  public abstract List<String> getIncomingConnectionTypes();
  
  public abstract List<String> getOutgoingConnectionTypes();
  
  public abstract void start()
    throws WekaException;
  
  public abstract void stop();
  
  public abstract void processIncoming(Data paramData)
    throws WekaException;
  
  public abstract Instances outputStructureForConnectionType(String paramString)
    throws WekaException;
  
  public abstract String getCustomEditorForStep();
  
  public abstract Map<String, String> getInteractiveViewers();
  
  public abstract Map<String, StepInteractiveViewer> getInteractiveViewersImpls();
  
  public abstract Defaults getDefaultSettings();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.Step
 * JD-Core Version:    0.7.0.1
 */