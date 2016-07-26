package weka.knowledgeflow;

public abstract interface CallbackNotifierDelegate
{
  public abstract void notifyCallback(StepTaskCallback paramStepTaskCallback, StepTask paramStepTask, ExecutionResult paramExecutionResult)
    throws Exception;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.CallbackNotifierDelegate
 * JD-Core Version:    0.7.0.1
 */