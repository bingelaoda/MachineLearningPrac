/*   1:    */ package weka.experiment;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import weka.core.RevisionHandler;
/*   5:    */ import weka.core.RevisionUtils;
/*   6:    */ 
/*   7:    */ public class TaskStatusInfo
/*   8:    */   implements Serializable, RevisionHandler
/*   9:    */ {
/*  10:    */   private static final long serialVersionUID = -6129343303703560015L;
/*  11:    */   public static final int TO_BE_RUN = 0;
/*  12:    */   public static final int PROCESSING = 1;
/*  13:    */   public static final int FAILED = 2;
/*  14:    */   public static final int FINISHED = 3;
/*  15: 54 */   private int m_ExecutionStatus = 0;
/*  16: 59 */   private String m_StatusMessage = "New Task";
/*  17: 64 */   private Object m_TaskResult = null;
/*  18:    */   
/*  19:    */   public void setExecutionStatus(int newStatus)
/*  20:    */   {
/*  21: 72 */     this.m_ExecutionStatus = newStatus;
/*  22:    */   }
/*  23:    */   
/*  24:    */   public int getExecutionStatus()
/*  25:    */   {
/*  26: 80 */     return this.m_ExecutionStatus;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public void setStatusMessage(String newMessage)
/*  30:    */   {
/*  31: 89 */     this.m_StatusMessage = newMessage;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public String getStatusMessage()
/*  35:    */   {
/*  36: 98 */     return this.m_StatusMessage;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void setTaskResult(Object taskResult)
/*  40:    */   {
/*  41:108 */     this.m_TaskResult = taskResult;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public Object getTaskResult()
/*  45:    */   {
/*  46:119 */     return this.m_TaskResult;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public String getRevision()
/*  50:    */   {
/*  51:128 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  52:    */   }
/*  53:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.TaskStatusInfo
 * JD-Core Version:    0.7.0.1
 */