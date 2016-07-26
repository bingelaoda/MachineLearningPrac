/*   1:    */ package weka.knowledgeflow;
/*   2:    */ 
/*   3:    */ import java.util.List;
/*   4:    */ import weka.core.WekaException;
/*   5:    */ import weka.knowledgeflow.steps.Step;
/*   6:    */ 
/*   7:    */ public class StepInjectorFlowRunner
/*   8:    */   extends FlowRunner
/*   9:    */ {
/*  10: 38 */   protected boolean m_reset = true;
/*  11:    */   protected boolean m_streaming;
/*  12:    */   
/*  13:    */   public void reset()
/*  14:    */   {
/*  15: 47 */     this.m_reset = true;
/*  16: 48 */     this.m_streaming = false;
/*  17:    */   }
/*  18:    */   
/*  19:    */   public void injectWithExecutionFinishedCallback(final Data toInject, ExecutionFinishedCallback callback, final Step target)
/*  20:    */     throws WekaException
/*  21:    */   {
/*  22: 63 */     if (StepManagerImpl.connectionIsIncremental(toInject)) {
/*  23: 64 */       throw new WekaException("Only batch data can be injected via this method.");
/*  24:    */     }
/*  25: 68 */     addExecutionFinishedCallback(callback);
/*  26:    */     
/*  27: 70 */     String connName = toInject.getConnectionName();
/*  28: 71 */     List<String> accceptableInputs = target.getIncomingConnectionTypes();
/*  29: 72 */     if (!accceptableInputs.contains(connName)) {
/*  30: 73 */       throw new WekaException("Step '" + target.getName() + "' can't accept a " + connName + " input at present!");
/*  31:    */     }
/*  32: 77 */     initializeFlow();
/*  33: 78 */     this.m_execEnv.submitTask(new StepTask(null)
/*  34:    */     {
/*  35:    */       private static final long serialVersionUID = 663985401825979869L;
/*  36:    */       
/*  37:    */       public void process()
/*  38:    */         throws Exception
/*  39:    */       {
/*  40: 84 */         target.processIncoming(toInject);
/*  41:    */       }
/*  42: 86 */     });
/*  43: 87 */     this.m_logHandler.logDebug("StepInjectorFlowRunner: Launching shutdown monitor");
/*  44: 88 */     launchExecutorShutdownThread();
/*  45:    */   }
/*  46:    */   
/*  47:    */   public Step findStep(String stepName, Class stepClass)
/*  48:    */     throws WekaException
/*  49:    */   {
/*  50:101 */     if (this.m_flow == null) {
/*  51:102 */       throw new WekaException("No flow set!");
/*  52:    */     }
/*  53:105 */     StepManagerImpl manager = this.m_flow.findStep(stepName);
/*  54:106 */     if (manager == null) {
/*  55:107 */       throw new WekaException("Step '" + stepName + "' does not seem " + "to be part of the flow!");
/*  56:    */     }
/*  57:111 */     Step target = manager.getManagedStep();
/*  58:112 */     if (target.getClass() != stepClass) {
/*  59:113 */       throw new WekaException("Step '" + stepName + "' is not an instance of " + stepClass.getCanonicalName());
/*  60:    */     }
/*  61:117 */     if ((target.getIncomingConnectionTypes() == null) || (target.getIncomingConnectionTypes().size() == 0)) {
/*  62:119 */       throw new WekaException("Step '" + stepName + "' cannot process any incoming data!");
/*  63:    */     }
/*  64:123 */     return target;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void injectStreaming(Data toInject, Step target, boolean lastData)
/*  68:    */     throws WekaException
/*  69:    */   {
/*  70:136 */     if (this.m_reset)
/*  71:    */     {
/*  72:137 */       if (this.m_streaming) {
/*  73:138 */         this.m_execEnv.stopClientExecutionService();
/*  74:    */       }
/*  75:141 */       String connName = toInject.getConnectionName();
/*  76:142 */       List<String> accceptableInputs = target.getIncomingConnectionTypes();
/*  77:143 */       if (!accceptableInputs.contains(connName)) {
/*  78:144 */         throw new WekaException("Step '" + target.getName() + "' can't accept a " + connName + " input at present!");
/*  79:    */       }
/*  80:148 */       initializeFlow();
/*  81:149 */       toInject.setPayloadElement("incremental_stream_end", Boolean.valueOf(false));
/*  82:    */       
/*  83:151 */       this.m_streaming = true;
/*  84:152 */       this.m_reset = false;
/*  85:    */     }
/*  86:155 */     if (lastData) {
/*  87:156 */       toInject.setPayloadElement("incremental_stream_end", Boolean.valueOf(true));
/*  88:    */     }
/*  89:160 */     target.processIncoming(toInject);
/*  90:161 */     if (lastData)
/*  91:    */     {
/*  92:162 */       this.m_logHandler.logDebug("StepInjectorFlowRunner: Shutting down executor service");
/*  93:    */       
/*  94:164 */       this.m_execEnv.stopClientExecutionService();
/*  95:165 */       reset();
/*  96:    */     }
/*  97:    */   }
/*  98:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.StepInjectorFlowRunner
 * JD-Core Version:    0.7.0.1
 */