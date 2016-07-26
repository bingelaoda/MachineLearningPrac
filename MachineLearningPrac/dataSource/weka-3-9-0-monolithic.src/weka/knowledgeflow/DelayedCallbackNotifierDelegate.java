/*  1:   */ package weka.knowledgeflow;
/*  2:   */ 
/*  3:   */ public class DelayedCallbackNotifierDelegate
/*  4:   */   implements CallbackNotifierDelegate
/*  5:   */ {
/*  6:   */   protected StepTaskCallback m_callback;
/*  7:   */   protected StepTask m_taskExecuted;
/*  8:   */   protected ExecutionResult m_result;
/*  9:   */   
/* 10:   */   public void notifyCallback(StepTaskCallback callback, StepTask taskExecuted, ExecutionResult result)
/* 11:   */     throws Exception
/* 12:   */   {
/* 13:55 */     this.m_callback = callback;
/* 14:56 */     this.m_taskExecuted = taskExecuted;
/* 15:57 */     this.m_result = result;
/* 16:   */   }
/* 17:   */   
/* 18:   */   public void notifyNow()
/* 19:   */     throws Exception
/* 20:   */   {
/* 21:67 */     if ((this.m_callback != null) && (this.m_result != null)) {
/* 22:68 */       if (this.m_result.getError() != null) {
/* 23:69 */         this.m_callback.taskFinished(this.m_result);
/* 24:   */       } else {
/* 25:71 */         this.m_callback.taskFailed(this.m_taskExecuted, this.m_result);
/* 26:   */       }
/* 27:   */     }
/* 28:   */   }
/* 29:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.DelayedCallbackNotifierDelegate
 * JD-Core Version:    0.7.0.1
 */