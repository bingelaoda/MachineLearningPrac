/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.util.Arrays;
/*   4:    */ import java.util.List;
/*   5:    */ import weka.core.Instances;
/*   6:    */ import weka.core.WekaException;
/*   7:    */ import weka.knowledgeflow.Data;
/*   8:    */ import weka.knowledgeflow.StepManager;
/*   9:    */ 
/*  10:    */ @KFStep(name="MemoryDataSource", category="DataSources", toolTipText="Memory-based data", iconPath="weka/gui/knowledgeflow/icons/DefaultDataSource.gif")
/*  11:    */ public class MemoryBasedDataSource
/*  12:    */   extends BaseStep
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = -1901014330145130275L;
/*  15:    */   protected Instances m_instances;
/*  16:    */   
/*  17:    */   public void setInstances(Instances instances)
/*  18:    */   {
/*  19: 57 */     this.m_instances = instances;
/*  20:    */   }
/*  21:    */   
/*  22:    */   public Instances getInstances()
/*  23:    */   {
/*  24: 66 */     return this.m_instances;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public void stepInit()
/*  28:    */     throws WekaException
/*  29:    */   {
/*  30: 76 */     if (this.m_instances == null) {
/*  31: 77 */       throw new WekaException("Has not been initialized with a set of instances");
/*  32:    */     }
/*  33:    */   }
/*  34:    */   
/*  35:    */   public List<String> getIncomingConnectionTypes()
/*  36:    */   {
/*  37: 93 */     return null;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public List<String> getOutgoingConnectionTypes()
/*  41:    */   {
/*  42:107 */     return Arrays.asList(new String[] { "dataSet" });
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void start()
/*  46:    */     throws WekaException
/*  47:    */   {
/*  48:117 */     getStepManager().processing();
/*  49:118 */     Data output = new Data("dataSet", this.m_instances);
/*  50:119 */     getStepManager().outputData(new Data[] { output });
/*  51:120 */     getStepManager().finished();
/*  52:    */   }
/*  53:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.MemoryBasedDataSource
 * JD-Core Version:    0.7.0.1
 */