package weka.knowledgeflow;

import java.util.List;
import java.util.Map;
import weka.core.Instances;
import weka.core.Settings;
import weka.core.WekaException;
import weka.gui.Logger;
import weka.knowledgeflow.steps.Step;

public abstract interface StepManager
{
  public static final String CON_DATASET = "dataSet";
  public static final String CON_INSTANCE = "instance";
  public static final String CON_TRAININGSET = "trainingSet";
  public static final String CON_TESTSET = "testSet";
  public static final String CON_BATCH_CLASSIFIER = "batchClassifier";
  public static final String CON_INCREMENTAL_CLASSIFIER = "incrementalClassifier";
  public static final String CON_INCREMENTAL_CLUSTERER = "incrementalClusterer";
  public static final String CON_BATCH_CLUSTERER = "batchClusterer";
  public static final String CON_BATCH_ASSOCIATOR = "batchAssociator";
  public static final String CON_VISUALIZABLE_ERROR = "visualizableError";
  public static final String CON_THRESHOLD_DATA = "thresholdData";
  public static final String CON_TEXT = "text";
  public static final String CON_IMAGE = "image";
  public static final String CON_GRAPH = "graph";
  public static final String CON_CHART = "chart";
  public static final String CON_INFO = "info";
  public static final String CON_AUX_DATA_SET_NUM = "aux_set_num";
  public static final String CON_AUX_DATA_MAX_SET_NUM = "aux_max_set_num";
  public static final String CON_AUX_DATA_TEST_INSTANCE = "aux_testInstance";
  public static final String CON_AUX_DATA_TESTSET = "aux_testsSet";
  public static final String CON_AUX_DATA_TRAININGSET = "aux_trainingSet";
  public static final String CON_AUX_DATA_TEXT_TITLE = "aux_textTitle";
  public static final String CON_AUX_DATA_LABEL = "aux_label";
  public static final String CON_AUX_DATA_CLASS_ATTRIBUTE = "class_attribute";
  public static final String CON_AUX_DATA_GRAPH_TITLE = "graph_title";
  public static final String CON_AUX_DATA_GRAPH_TYPE = "graph_type";
  public static final String CON_AUX_DATA_CHART_MAX = "chart_max";
  public static final String CON_AUX_DATA_CHART_MIN = "chart_min";
  public static final String CON_AUX_DATA_CHART_DATA_POINT = "chart_data_point";
  public static final String CON_AUX_DATA_CHART_LEGEND = "chart_legend";
  public static final String CON_AUX_DATA_BATCH_ASSOCIATION_RULES = "batch_association_rules";
  public static final String CON_AUX_DATA_INCREMENTAL_STREAM_END = "incremental_stream_end";
  public static final String CON_AUX_DATA_IS_INCREMENTAL = "incremental_stream";
  
  public abstract String getName();
  
  public abstract Step getManagedStep();
  
  public abstract ExecutionEnvironment getExecutionEnvironment();
  
  public abstract Settings getSettings();
  
  public abstract int numIncomingConnections();
  
  public abstract int numOutgoingConnections();
  
  public abstract int numIncomingConnectionsOfType(String paramString);
  
  public abstract int numOutgoingConnectionsOfType(String paramString);
  
  public abstract List<StepManager> getIncomingConnectedStepsOfConnectionType(String paramString);
  
  public abstract StepManager getIncomingConnectedStepWithName(String paramString);
  
  public abstract StepManager getOutgoingConnectedStepWithName(String paramString);
  
  public abstract List<StepManager> getOutgoingConnectedStepsOfConnectionType(String paramString);
  
  public abstract Map<String, List<StepManager>> getIncomingConnections();
  
  public abstract Map<String, List<StepManager>> getOutgoingConnections();
  
  public abstract void outputData(String paramString, Data paramData)
    throws WekaException;
  
  public abstract void outputData(Data... paramVarArgs)
    throws WekaException;
  
  public abstract void outputData(String paramString1, String paramString2, Data paramData)
    throws WekaException;
  
  public abstract Instances getIncomingStructureForConnectionType(String paramString)
    throws WekaException;
  
  public abstract Instances getIncomingStructureFromStep(StepManager paramStepManager, String paramString)
    throws WekaException;
  
  public abstract boolean isStepBusy();
  
  public abstract boolean isStopRequested();
  
  public abstract void processing();
  
  public abstract void finished();
  
  public abstract void interrupted();
  
  public abstract boolean isStreamFinished(Data paramData);
  
  public abstract void throughputUpdateStart();
  
  public abstract void throughputUpdateEnd();
  
  public abstract void throughputFinished(Data... paramVarArgs)
    throws WekaException;
  
  public abstract void logLow(String paramString);
  
  public abstract void logBasic(String paramString);
  
  public abstract void logDetailed(String paramString);
  
  public abstract void logDebug(String paramString);
  
  public abstract void logWarning(String paramString);
  
  public abstract void logError(String paramString, Throwable paramThrowable);
  
  public abstract void log(String paramString, LoggingLevel paramLoggingLevel);
  
  public abstract void statusMessage(String paramString);
  
  public abstract Logger getLog();
  
  public abstract LoggingLevel getLoggingLevel();
  
  public abstract String environmentSubstitute(String paramString);
  
  public abstract Step getInfoStep(Class paramClass)
    throws WekaException;
  
  public abstract Step getInfoStep()
    throws WekaException;
  
  public abstract StepManager findStepInFlow(String paramString);
  
  public abstract boolean stepIsResourceIntensive();
  
  public abstract void setStepIsResourceIntensive(boolean paramBoolean);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.StepManager
 * JD-Core Version:    0.7.0.1
 */