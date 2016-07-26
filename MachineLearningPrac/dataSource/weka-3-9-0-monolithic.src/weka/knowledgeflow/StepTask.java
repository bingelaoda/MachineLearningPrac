/*   1:    */ package weka.knowledgeflow;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.concurrent.Callable;
/*   5:    */ import weka.knowledgeflow.steps.Step;
/*   6:    */ 
/*   7:    */ public abstract class StepTask<T>
/*   8:    */   implements Callable<ExecutionResult<T>>, Serializable
/*   9:    */ {
/*  10:    */   private static final long serialVersionUID = 2995081029283027784L;
/*  11: 37 */   protected ExecutionResult<T> m_result = new ExecutionResult();
/*  12:    */   protected transient StepTaskCallback<T> m_callback;
/*  13:    */   protected LogManager m_log;
/*  14: 46 */   protected boolean m_resourceIntensive = true;
/*  15: 52 */   protected CallbackNotifierDelegate m_callbackNotifier = new DefaultCallbackNotifierDelegate();
/*  16:    */   
/*  17:    */   public StepTask(Step source)
/*  18:    */   {
/*  19: 62 */     this(source, null, false);
/*  20:    */   }
/*  21:    */   
/*  22:    */   public StepTask(Step source, boolean resourceIntensive)
/*  23:    */   {
/*  24: 73 */     this(source, null, resourceIntensive);
/*  25:    */   }
/*  26:    */   
/*  27:    */   public StepTask(Step source, StepTaskCallback<T> callback)
/*  28:    */   {
/*  29: 84 */     this(source, callback, false);
/*  30:    */   }
/*  31:    */   
/*  32:    */   public StepTask(Step source, StepTaskCallback<T> callback, boolean resourceIntensive)
/*  33:    */   {
/*  34: 97 */     this.m_log = new LogManager(source);
/*  35: 98 */     this.m_callback = callback;
/*  36: 99 */     this.m_resourceIntensive = resourceIntensive;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void setResourceIntensive(boolean resourceIntensive)
/*  40:    */   {
/*  41:110 */     this.m_resourceIntensive = resourceIntensive;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public boolean isResourceIntensive()
/*  45:    */   {
/*  46:121 */     return this.m_resourceIntensive;
/*  47:    */   }
/*  48:    */   
/*  49:    */   protected final CallbackNotifierDelegate getCallbackNotifierDelegate()
/*  50:    */   {
/*  51:131 */     return this.m_callbackNotifier;
/*  52:    */   }
/*  53:    */   
/*  54:    */   protected final void setCallbackNotifierDelegate(CallbackNotifierDelegate delegate)
/*  55:    */   {
/*  56:142 */     this.m_callbackNotifier = delegate;
/*  57:    */   }
/*  58:    */   
/*  59:    */   protected final LogManager getLogHandler()
/*  60:    */   {
/*  61:151 */     return this.m_log;
/*  62:    */   }
/*  63:    */   
/*  64:    */   protected final void setLogHandler(LogManager log)
/*  65:    */   {
/*  66:161 */     this.m_log = log;
/*  67:    */   }
/*  68:    */   
/*  69:    */   protected final void notifyCallback()
/*  70:    */     throws Exception
/*  71:    */   {
/*  72:170 */     if (this.m_callback != null) {
/*  73:171 */       this.m_callbackNotifier.notifyCallback(this.m_callback, this, this.m_result);
/*  74:    */     }
/*  75:    */   }
/*  76:    */   
/*  77:    */   protected final ExecutionResult<T> getExecutionResult()
/*  78:    */   {
/*  79:181 */     return this.m_result;
/*  80:    */   }
/*  81:    */   
/*  82:    */   protected final void setExecutionResult(ExecutionResult<T> execResult)
/*  83:    */   {
/*  84:190 */     this.m_result = execResult;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public ExecutionResult<T> call()
/*  88:    */     throws Exception
/*  89:    */   {
/*  90:    */     try
/*  91:    */     {
/*  92:201 */       process();
/*  93:    */     }
/*  94:    */     catch (Exception ex)
/*  95:    */     {
/*  96:203 */       getExecutionResult().setError(ex);
/*  97:    */     }
/*  98:205 */     notifyCallback();
/*  99:    */     
/* 100:207 */     return this.m_result;
/* 101:    */   }
/* 102:    */   
/* 103:    */   public abstract void process()
/* 104:    */     throws Exception;
/* 105:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.StepTask
 * JD-Core Version:    0.7.0.1
 */