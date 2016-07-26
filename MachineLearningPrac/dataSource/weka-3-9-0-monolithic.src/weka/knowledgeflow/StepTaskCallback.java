package weka.knowledgeflow;

public abstract interface StepTaskCallback<T>
{
  public abstract void taskFinished(ExecutionResult<T> paramExecutionResult)
    throws Exception;
  
  public abstract void taskFailed(StepTask<T> paramStepTask, ExecutionResult<T> paramExecutionResult)
    throws Exception;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.StepTaskCallback
 * JD-Core Version:    0.7.0.1
 */