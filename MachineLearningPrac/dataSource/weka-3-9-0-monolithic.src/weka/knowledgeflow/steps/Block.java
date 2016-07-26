/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.List;
/*   6:    */ import java.util.Map;
/*   7:    */ import java.util.Set;
/*   8:    */ import weka.core.OptionMetadata;
/*   9:    */ import weka.core.WekaException;
/*  10:    */ import weka.knowledgeflow.Data;
/*  11:    */ import weka.knowledgeflow.StepManager;
/*  12:    */ 
/*  13:    */ @KFStep(name="Block", category="Flow", toolTipText="Block until a specific step has finished procesing", iconPath="weka/gui/knowledgeflow/icons/DiamondPlain.gif")
/*  14:    */ public class Block
/*  15:    */   extends BaseStep
/*  16:    */ {
/*  17:    */   private static final long serialVersionUID = 3204082191908877620L;
/*  18: 50 */   protected String m_stepToWaitFor = "";
/*  19:    */   protected transient StepManager m_smForStep;
/*  20:    */   
/*  21:    */   @OptionMetadata(displayName="Wait until this step has completed", description="This step will prevent data from passing downstream until the specified step has finished processing")
/*  22:    */   public void setStepToWaitFor(String stepToWaitFor)
/*  23:    */   {
/*  24: 64 */     this.m_stepToWaitFor = stepToWaitFor;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public String getStepToWaitFor()
/*  28:    */   {
/*  29: 73 */     return this.m_stepToWaitFor;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public void stepInit()
/*  33:    */     throws WekaException
/*  34:    */   {
/*  35: 83 */     if ((this.m_stepToWaitFor == null) || (this.m_stepToWaitFor.length() == 0)) {
/*  36: 84 */       getStepManager().logWarning("No step to wait for specified - will not block");
/*  37:    */     }
/*  38: 88 */     this.m_smForStep = getStepManager().findStepInFlow(environmentSubstitute(this.m_stepToWaitFor));
/*  39: 91 */     if (this.m_smForStep == getStepManager()) {
/*  40: 93 */       throw new WekaException("Blocking on oneself will cause deadlock!");
/*  41:    */     }
/*  42: 96 */     if (this.m_smForStep == null) {
/*  43: 97 */       throw new WekaException("Step '" + environmentSubstitute(this.m_stepToWaitFor) + "' does not seem " + "to exist in the flow!");
/*  44:    */     }
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void processIncoming(Data data)
/*  48:    */     throws WekaException
/*  49:    */   {
/*  50:110 */     if (this.m_smForStep == null)
/*  51:    */     {
/*  52:112 */       getStepManager().outputData(new Data[] { data });
/*  53:    */     }
/*  54:114 */     else if (this.m_smForStep.isStepBusy())
/*  55:    */     {
/*  56:115 */       getStepManager().processing();
/*  57:116 */       getStepManager().logBasic("Waiting for step '" + environmentSubstitute(this.m_stepToWaitFor) + "'");
/*  58:    */       
/*  59:118 */       getStepManager().statusMessage("Waiting for step '" + environmentSubstitute(this.m_stepToWaitFor) + "'");
/*  60:120 */       while ((this.m_smForStep.isStepBusy()) && 
/*  61:121 */         (!isStopRequested())) {
/*  62:    */         try
/*  63:    */         {
/*  64:125 */           Thread.sleep(300L);
/*  65:    */         }
/*  66:    */         catch (InterruptedException e)
/*  67:    */         {
/*  68:127 */           getStepManager().interrupted();
/*  69:128 */           return;
/*  70:    */         }
/*  71:    */       }
/*  72:131 */       getStepManager().logBasic("Releasing data");
/*  73:132 */       getStepManager().statusMessage("Releasing data");
/*  74:    */     }
/*  75:136 */     if (isStopRequested())
/*  76:    */     {
/*  77:137 */       getStepManager().interrupted();
/*  78:    */     }
/*  79:    */     else
/*  80:    */     {
/*  81:139 */       getStepManager().outputData(new Data[] { data });
/*  82:140 */       getStepManager().finished();
/*  83:    */     }
/*  84:    */   }
/*  85:    */   
/*  86:    */   public List<String> getIncomingConnectionTypes()
/*  87:    */   {
/*  88:152 */     return Arrays.asList(new String[] { "dataSet", "trainingSet", "instance", "testSet", "batchClassifier", "batchClusterer", "batchAssociator", "text" });
/*  89:    */   }
/*  90:    */   
/*  91:    */   public List<String> getOutgoingConnectionTypes()
/*  92:    */   {
/*  93:166 */     Set<String> inConnTypes = getStepManager().getIncomingConnections().keySet();
/*  94:    */     
/*  95:168 */     return new ArrayList(inConnTypes);
/*  96:    */   }
/*  97:    */   
/*  98:    */   public String getCustomEditorForStep()
/*  99:    */   {
/* 100:178 */     return "weka.gui.knowledgeflow.steps.BlockStepEditorDialog";
/* 101:    */   }
/* 102:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.Block
 * JD-Core Version:    0.7.0.1
 */