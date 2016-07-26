/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.List;
/*   6:    */ import java.util.Random;
/*   7:    */ import weka.core.Instances;
/*   8:    */ import weka.core.OptionMetadata;
/*   9:    */ import weka.core.WekaException;
/*  10:    */ import weka.knowledgeflow.Data;
/*  11:    */ import weka.knowledgeflow.StepManager;
/*  12:    */ 
/*  13:    */ @KFStep(name="TrainTestSplitMaker", category="Evaluation", toolTipText="A step that randomly splits incoming data into a training and test set", iconPath="weka/gui/knowledgeflow/icons/TrainTestSplitMaker.gif")
/*  14:    */ public class TrainTestSplitMaker
/*  15:    */   extends BaseStep
/*  16:    */ {
/*  17:    */   private static final long serialVersionUID = 7685026723199727685L;
/*  18: 52 */   protected String m_trainPercentageS = "66";
/*  19: 55 */   protected String m_seedS = "1";
/*  20: 58 */   protected double m_trainPercentage = 66.0D;
/*  21: 61 */   protected long m_seed = 1L;
/*  22:    */   
/*  23:    */   @OptionMetadata(displayName="Training percentage", description="The percentage of data to go into the training set", displayOrder=1)
/*  24:    */   public void setTrainPercent(String percent)
/*  25:    */   {
/*  26: 72 */     this.m_trainPercentageS = percent;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public String getTrainPercent()
/*  30:    */   {
/*  31: 81 */     return this.m_trainPercentageS;
/*  32:    */   }
/*  33:    */   
/*  34:    */   @OptionMetadata(displayName="Random seed", description="The random seed", displayOrder=2)
/*  35:    */   public void setSeed(String seed)
/*  36:    */   {
/*  37: 92 */     this.m_seedS = seed;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public String getSeed()
/*  41:    */   {
/*  42:101 */     return this.m_seedS;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void stepInit()
/*  46:    */     throws WekaException
/*  47:    */   {
/*  48:111 */     String seed = getStepManager().environmentSubstitute(getSeed());
/*  49:    */     try
/*  50:    */     {
/*  51:113 */       this.m_seed = Long.parseLong(seed);
/*  52:    */     }
/*  53:    */     catch (NumberFormatException ex)
/*  54:    */     {
/*  55:115 */       getStepManager().logWarning("Unable to parse seed value: " + seed);
/*  56:    */     }
/*  57:118 */     String tP = getStepManager().environmentSubstitute(getTrainPercent());
/*  58:    */     try
/*  59:    */     {
/*  60:120 */       this.m_trainPercentage = Double.parseDouble(tP);
/*  61:    */     }
/*  62:    */     catch (NumberFormatException ex)
/*  63:    */     {
/*  64:122 */       getStepManager().logWarning("Unable to parse train percentage value: " + tP);
/*  65:    */     }
/*  66:    */   }
/*  67:    */   
/*  68:    */   public void processIncoming(Data data)
/*  69:    */     throws WekaException
/*  70:    */   {
/*  71:135 */     getStepManager().processing();
/*  72:136 */     String incomingConnName = data.getConnectionName();
/*  73:137 */     Instances dataSet = (Instances)data.getPayloadElement(incomingConnName);
/*  74:138 */     if (dataSet == null) {
/*  75:139 */       throw new WekaException("Incoming instances should not be null!");
/*  76:    */     }
/*  77:142 */     getStepManager().logBasic("Creating train/test split");
/*  78:143 */     getStepManager().statusMessage("Creating train/test split");
/*  79:144 */     dataSet.randomize(new Random(this.m_seed));
/*  80:145 */     int trainSize = (int)Math.round(dataSet.numInstances() * this.m_trainPercentage / 100.0D);
/*  81:    */     
/*  82:147 */     int testSize = dataSet.numInstances() - trainSize;
/*  83:    */     
/*  84:149 */     Instances train = new Instances(dataSet, 0, trainSize);
/*  85:150 */     Instances test = new Instances(dataSet, trainSize, testSize);
/*  86:    */     
/*  87:152 */     Data trainData = new Data("trainingSet");
/*  88:153 */     trainData.setPayloadElement("trainingSet", train);
/*  89:154 */     trainData.setPayloadElement("aux_set_num", Integer.valueOf(1));
/*  90:155 */     trainData.setPayloadElement("aux_max_set_num", Integer.valueOf(1));
/*  91:156 */     Data testData = new Data("testSet");
/*  92:157 */     testData.setPayloadElement("testSet", test);
/*  93:158 */     testData.setPayloadElement("aux_set_num", Integer.valueOf(1));
/*  94:159 */     testData.setPayloadElement("aux_max_set_num", Integer.valueOf(1));
/*  95:161 */     if (!isStopRequested()) {
/*  96:162 */       getStepManager().outputData(new Data[] { trainData, testData });
/*  97:    */     }
/*  98:165 */     getStepManager().finished();
/*  99:    */   }
/* 100:    */   
/* 101:    */   public List<String> getIncomingConnectionTypes()
/* 102:    */   {
/* 103:179 */     if (getStepManager().numIncomingConnections() > 0) {
/* 104:180 */       return new ArrayList();
/* 105:    */     }
/* 106:183 */     return Arrays.asList(new String[] { "dataSet", "trainingSet", "testSet" });
/* 107:    */   }
/* 108:    */   
/* 109:    */   public List<String> getOutgoingConnectionTypes()
/* 110:    */   {
/* 111:198 */     return getStepManager().numIncomingConnections() > 0 ? Arrays.asList(new String[] { "trainingSet", "testSet" }) : new ArrayList();
/* 112:    */   }
/* 113:    */   
/* 114:    */   public Instances outputStructureForConnectionType(String connectionName)
/* 115:    */     throws WekaException
/* 116:    */   {
/* 117:218 */     if (((!connectionName.equals("trainingSet")) && (!connectionName.equals("testSet"))) || (getStepManager().numIncomingConnections() == 0)) {
/* 118:221 */       return null;
/* 119:    */     }
/* 120:225 */     Instances strucForDatasetCon = getStepManager().getIncomingStructureForConnectionType("dataSet");
/* 121:228 */     if (strucForDatasetCon != null) {
/* 122:229 */       return strucForDatasetCon;
/* 123:    */     }
/* 124:232 */     Instances strucForTestsetCon = getStepManager().getIncomingStructureForConnectionType("testSet");
/* 125:235 */     if (strucForTestsetCon != null) {
/* 126:236 */       return strucForTestsetCon;
/* 127:    */     }
/* 128:239 */     Instances strucForTrainingCon = getStepManager().getIncomingStructureForConnectionType("trainingSet");
/* 129:242 */     if (strucForTrainingCon != null) {
/* 130:243 */       return strucForTrainingCon;
/* 131:    */     }
/* 132:246 */     return null;
/* 133:    */   }
/* 134:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.TrainTestSplitMaker
 * JD-Core Version:    0.7.0.1
 */