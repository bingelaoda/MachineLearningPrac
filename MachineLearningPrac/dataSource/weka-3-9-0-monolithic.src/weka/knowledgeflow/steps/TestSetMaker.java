/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.List;
/*   6:    */ import weka.core.Instances;
/*   7:    */ import weka.core.WekaException;
/*   8:    */ import weka.knowledgeflow.Data;
/*   9:    */ import weka.knowledgeflow.StepManager;
/*  10:    */ 
/*  11:    */ @KFStep(name="TestSetMaker", category="Evaluation", toolTipText="Make an incoming dataSet or trainingSet into a testSet", iconPath="weka/gui/knowledgeflow/icons/TestSetMaker.gif")
/*  12:    */ public class TestSetMaker
/*  13:    */   extends BaseStep
/*  14:    */ {
/*  15:    */   private static final long serialVersionUID = 6384920860783839811L;
/*  16:    */   
/*  17:    */   public void stepInit() {}
/*  18:    */   
/*  19:    */   public void processIncoming(Data data)
/*  20:    */     throws WekaException
/*  21:    */   {
/*  22: 62 */     getStepManager().processing();
/*  23: 63 */     String incomingConnName = data.getConnectionName();
/*  24: 64 */     Instances insts = (Instances)data.getPayloadElement(incomingConnName);
/*  25: 65 */     if (insts == null) {
/*  26: 66 */       throw new WekaException("Incoming instances should not be null!");
/*  27:    */     }
/*  28: 69 */     getStepManager().logBasic("Creating a test set for relation " + insts.relationName());
/*  29:    */     
/*  30: 71 */     Data newData = new Data();
/*  31: 72 */     newData.setPayloadElement("testSet", insts);
/*  32: 73 */     newData.setPayloadElement("aux_set_num", Integer.valueOf(1));
/*  33: 74 */     newData.setPayloadElement("aux_max_set_num", Integer.valueOf(1));
/*  34: 75 */     if (!isStopRequested()) {
/*  35: 76 */       getStepManager().outputData("testSet", newData);
/*  36:    */     }
/*  37: 78 */     getStepManager().finished();
/*  38:    */   }
/*  39:    */   
/*  40:    */   public List<String> getIncomingConnectionTypes()
/*  41:    */   {
/*  42: 92 */     if (getStepManager().numIncomingConnections() == 0) {
/*  43: 93 */       return Arrays.asList(new String[] { "dataSet", "trainingSet" });
/*  44:    */     }
/*  45: 97 */     return new ArrayList();
/*  46:    */   }
/*  47:    */   
/*  48:    */   public List<String> getOutgoingConnectionTypes()
/*  49:    */   {
/*  50:111 */     if (getStepManager().numIncomingConnections() > 0) {
/*  51:112 */       return Arrays.asList(new String[] { "testSet" });
/*  52:    */     }
/*  53:114 */     return new ArrayList();
/*  54:    */   }
/*  55:    */   
/*  56:    */   public Instances outputStructureForConnectionType(String connectionName)
/*  57:    */     throws WekaException
/*  58:    */   {
/*  59:130 */     if ((!connectionName.equals("testSet")) || (getStepManager().numIncomingConnections() == 0)) {
/*  60:132 */       return null;
/*  61:    */     }
/*  62:135 */     Instances strucForDatasetCon = getStepManager().getIncomingStructureForConnectionType("dataSet");
/*  63:138 */     if (strucForDatasetCon != null) {
/*  64:139 */       return strucForDatasetCon;
/*  65:    */     }
/*  66:142 */     Instances strucForTrainingSetCon = getStepManager().getIncomingStructureForConnectionType("trainingSet");
/*  67:145 */     if (strucForTrainingSetCon != null) {
/*  68:146 */       return strucForTrainingSetCon;
/*  69:    */     }
/*  70:149 */     return null;
/*  71:    */   }
/*  72:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.TestSetMaker
 * JD-Core Version:    0.7.0.1
 */