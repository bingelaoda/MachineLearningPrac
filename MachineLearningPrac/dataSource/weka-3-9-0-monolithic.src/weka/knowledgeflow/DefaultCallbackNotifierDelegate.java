/*  1:   */ package weka.knowledgeflow;
/*  2:   */ 
/*  3:   */ public class DefaultCallbackNotifierDelegate
/*  4:   */   implements CallbackNotifierDelegate
/*  5:   */ {
/*  6:   */   public void notifyCallback(StepTaskCallback callback, StepTask taskExecuted, ExecutionResult result)
/*  7:   */     throws Exception
/*  8:   */   {
/*  9:47 */     if (result.getError() == null) {
/* 10:48 */       callback.taskFinished(result);
/* 11:   */     } else {
/* 12:50 */       callback.taskFailed(taskExecuted, result);
/* 13:   */     }
/* 14:   */   }
/* 15:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.DefaultCallbackNotifierDelegate
 * JD-Core Version:    0.7.0.1
 */