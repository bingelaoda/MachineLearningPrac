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
/*  12:    */ import weka.knowledgeflow.StepManagerImpl;
/*  13:    */ 
/*  14:    */ @KFStep(name="MakeResourceIntensive", category="Flow", toolTipText="Makes downstream connected steps resource intensive (or not). This shifts processing of such steps between the main step executor<br>service and the high resource executor service or vice versa.", iconPath="weka/gui/knowledgeflow/icons/DiamondPlain.gif")
/*  15:    */ public class MakeResourceIntensive
/*  16:    */   extends BaseStep
/*  17:    */ {
/*  18:    */   private static final long serialVersionUID = -5670771681991035130L;
/*  19: 69 */   protected boolean m_setAsResourceIntensive = true;
/*  20:    */   
/*  21:    */   @OptionMetadata(displayName="Make downstream step(s) high resource", description="<html>Makes downstream connected steps resource intensive (or not)<br>This shifts processing of such steps between the main step executor service and the high resource executor service or vice versa.</html>")
/*  22:    */   public void setMakeResourceIntensive(boolean resourceIntensive)
/*  23:    */   {
/*  24: 85 */     this.m_setAsResourceIntensive = resourceIntensive;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public boolean getMakeResourceIntensive()
/*  28:    */   {
/*  29: 95 */     return this.m_setAsResourceIntensive;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public void stepInit()
/*  33:    */     throws WekaException
/*  34:    */   {}
/*  35:    */   
/*  36:    */   public List<String> getIncomingConnectionTypes()
/*  37:    */   {
/*  38:119 */     return Arrays.asList(new String[] { "dataSet", "trainingSet", "testSet", "batchClassifier", "batchClusterer", "batchAssociator" });
/*  39:    */   }
/*  40:    */   
/*  41:    */   public List<String> getOutgoingConnectionTypes()
/*  42:    */   {
/*  43:135 */     Set<String> inConnTypes = getStepManager().getIncomingConnections().keySet();
/*  44:    */     
/*  45:137 */     return new ArrayList(inConnTypes);
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void processIncoming(Data data)
/*  49:    */     throws WekaException
/*  50:    */   {
/*  51:148 */     String connType = data.getConnectionName();
/*  52:149 */     List<StepManager> connected = getStepManager().getOutgoingConnectedStepsOfConnectionType(connType);
/*  53:152 */     for (StepManager m : connected)
/*  54:    */     {
/*  55:153 */       getStepManager().logDetailed("Setting " + m.getName() + " as resource intensive: " + this.m_setAsResourceIntensive);
/*  56:    */       
/*  57:    */ 
/*  58:156 */       ((StepManagerImpl)m).setStepIsResourceIntensive(this.m_setAsResourceIntensive);
/*  59:    */     }
/*  60:159 */     getStepManager().outputData(new Data[] { data });
/*  61:    */   }
/*  62:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.MakeResourceIntensive
 * JD-Core Version:    0.7.0.1
 */