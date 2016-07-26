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
/*  11:    */ @KFStep(name="TrainingSetMaker", category="Evaluation", toolTipText="Make an incoming dataSet or testSet into a trainingSet", iconPath="weka/gui/knowledgeflow/icons/TrainingSetMaker.gif")
/*  12:    */ public class TrainingSetMaker
/*  13:    */   extends BaseStep
/*  14:    */ {
/*  15:    */   private static final long serialVersionUID = 1082946912813721183L;
/*  16:    */   
/*  17:    */   public void stepInit() {}
/*  18:    */   
/*  19:    */   public void processIncoming(Data data)
/*  20:    */     throws WekaException
/*  21:    */   {
/*  22: 63 */     getStepManager().processing();
/*  23: 64 */     String incomingConnName = data.getConnectionName();
/*  24: 65 */     Instances insts = (Instances)data.getPayloadElement(incomingConnName);
/*  25: 66 */     if (insts == null) {
/*  26: 67 */       throw new WekaException("Incoming instances should not be null!");
/*  27:    */     }
/*  28: 70 */     getStepManager().logBasic("Creating a training set for relation " + insts.relationName());
/*  29:    */     
/*  30: 72 */     Data newData = new Data();
/*  31: 73 */     newData.setPayloadElement("trainingSet", insts);
/*  32: 74 */     newData.setPayloadElement("aux_set_num", Integer.valueOf(1));
/*  33: 75 */     newData.setPayloadElement("aux_max_set_num", Integer.valueOf(1));
/*  34: 77 */     if (!isStopRequested()) {
/*  35: 78 */       getStepManager().outputData("trainingSet", newData);
/*  36:    */     }
/*  37: 80 */     getStepManager().finished();
/*  38:    */   }
/*  39:    */   
/*  40:    */   public List<String> getIncomingConnectionTypes()
/*  41:    */   {
/*  42: 94 */     if (getStepManager().numIncomingConnections() == 0) {
/*  43: 95 */       return Arrays.asList(new String[] { "dataSet", "testSet" });
/*  44:    */     }
/*  45: 99 */     return new ArrayList();
/*  46:    */   }
/*  47:    */   
/*  48:    */   public List<String> getOutgoingConnectionTypes()
/*  49:    */   {
/*  50:113 */     if (getStepManager().numIncomingConnections() > 0) {
/*  51:114 */       return Arrays.asList(new String[] { "trainingSet" });
/*  52:    */     }
/*  53:116 */     return new ArrayList();
/*  54:    */   }
/*  55:    */   
/*  56:    */   public Instances outputStructureForConnectionType(String connectionName)
/*  57:    */     throws WekaException
/*  58:    */   {
/*  59:132 */     if ((!connectionName.equals("trainingSet")) || (getStepManager().numIncomingConnections() == 0)) {
/*  60:134 */       return null;
/*  61:    */     }
/*  62:137 */     Instances strucForDatasetCon = getStepManager().getIncomingStructureForConnectionType("dataSet");
/*  63:140 */     if (strucForDatasetCon != null) {
/*  64:141 */       return strucForDatasetCon;
/*  65:    */     }
/*  66:144 */     Instances strucForTestsetCon = getStepManager().getIncomingStructureForConnectionType("testSet");
/*  67:147 */     if (strucForTestsetCon != null) {
/*  68:148 */       return strucForTestsetCon;
/*  69:    */     }
/*  70:151 */     return null;
/*  71:    */   }
/*  72:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.TrainingSetMaker
 * JD-Core Version:    0.7.0.1
 */