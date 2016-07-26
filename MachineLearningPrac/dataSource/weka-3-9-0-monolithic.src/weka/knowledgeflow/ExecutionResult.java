/*  1:   */ package weka.knowledgeflow;
/*  2:   */ 
/*  3:   */ import java.io.Serializable;
/*  4:   */ 
/*  5:   */ public class ExecutionResult<T>
/*  6:   */   implements Serializable
/*  7:   */ {
/*  8:   */   private static final long serialVersionUID = -4495164361311877942L;
/*  9:   */   protected Exception m_error;
/* 10:   */   protected T m_executionResult;
/* 11:   */   
/* 12:   */   public void setError(Exception error)
/* 13:   */   {
/* 14:54 */     this.m_error = error;
/* 15:   */   }
/* 16:   */   
/* 17:   */   public Exception getError()
/* 18:   */   {
/* 19:64 */     return this.m_error;
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void setResult(T result)
/* 23:   */   {
/* 24:73 */     this.m_executionResult = result;
/* 25:   */   }
/* 26:   */   
/* 27:   */   public T getResult()
/* 28:   */   {
/* 29:82 */     return this.m_executionResult;
/* 30:   */   }
/* 31:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.ExecutionResult
 * JD-Core Version:    0.7.0.1
 */