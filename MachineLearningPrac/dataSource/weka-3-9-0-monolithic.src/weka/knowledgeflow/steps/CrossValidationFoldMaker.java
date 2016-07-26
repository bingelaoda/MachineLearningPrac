/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.List;
/*   6:    */ import java.util.Random;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.OptionMetadata;
/*  10:    */ import weka.core.WekaException;
/*  11:    */ import weka.knowledgeflow.Data;
/*  12:    */ import weka.knowledgeflow.StepManager;
/*  13:    */ 
/*  14:    */ @KFStep(name="CrossValidationFoldMaker", category="Evaluation", toolTipText="A Step that creates stratified cross-validation folds from incoming data", iconPath="weka/gui/knowledgeflow/icons/CrossValidationFoldMaker.gif")
/*  15:    */ public class CrossValidationFoldMaker
/*  16:    */   extends BaseStep
/*  17:    */ {
/*  18:    */   private static final long serialVersionUID = 6090713408437825355L;
/*  19:    */   protected boolean m_preserveOrder;
/*  20: 52 */   protected String m_numFoldsS = "10";
/*  21: 55 */   protected String m_seedS = "1";
/*  22: 58 */   protected int m_numFolds = 10;
/*  23: 61 */   protected long m_seed = 1L;
/*  24:    */   
/*  25:    */   @OptionMetadata(displayName="Number of folds", description="THe number of folds to create", displayOrder=0)
/*  26:    */   public void setNumFolds(String folds)
/*  27:    */   {
/*  28: 71 */     this.m_numFoldsS = folds;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public String getNumFolds()
/*  32:    */   {
/*  33: 80 */     return this.m_numFoldsS;
/*  34:    */   }
/*  35:    */   
/*  36:    */   @OptionMetadata(displayName="Preserve instances order", description="Preserve the order of instances rather than randomly shuffling", displayOrder=1)
/*  37:    */   public void setPreserveOrder(boolean preserve)
/*  38:    */   {
/*  39: 93 */     this.m_preserveOrder = preserve;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public boolean getPreserveOrder()
/*  43:    */   {
/*  44:103 */     return this.m_preserveOrder;
/*  45:    */   }
/*  46:    */   
/*  47:    */   @OptionMetadata(displayName="Random seed", description="The random seed to use for shuffling", displayOrder=3)
/*  48:    */   public void setSeed(String seed)
/*  49:    */   {
/*  50:114 */     this.m_seedS = seed;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public String getSeed()
/*  54:    */   {
/*  55:123 */     return this.m_seedS;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public void stepInit()
/*  59:    */     throws WekaException
/*  60:    */   {
/*  61:133 */     String seed = getStepManager().environmentSubstitute(getSeed());
/*  62:    */     try
/*  63:    */     {
/*  64:135 */       this.m_seed = Long.parseLong(seed);
/*  65:    */     }
/*  66:    */     catch (NumberFormatException ex)
/*  67:    */     {
/*  68:137 */       getStepManager().logWarning("Unable to parse seed value: " + seed);
/*  69:    */     }
/*  70:140 */     String folds = getStepManager().environmentSubstitute(getNumFolds());
/*  71:    */     try
/*  72:    */     {
/*  73:142 */       this.m_numFolds = Integer.parseInt(folds);
/*  74:    */     }
/*  75:    */     catch (NumberFormatException e)
/*  76:    */     {
/*  77:144 */       getStepManager().logWarning("Unable to parse number of folds value: " + folds);
/*  78:    */     }
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void processIncoming(Data data)
/*  82:    */     throws WekaException
/*  83:    */   {
/*  84:157 */     getStepManager().processing();
/*  85:158 */     String incomingConnName = data.getConnectionName();
/*  86:159 */     Instances dataSet = (Instances)data.getPayloadElement(incomingConnName);
/*  87:160 */     if (dataSet == null) {
/*  88:161 */       throw new WekaException("Incoming instances should not be null!");
/*  89:    */     }
/*  90:163 */     dataSet = new Instances(dataSet);
/*  91:164 */     getStepManager().logBasic("Creating cross-validation folds");
/*  92:165 */     getStepManager().statusMessage("Creating cross-validation folds");
/*  93:    */     
/*  94:167 */     Random random = new Random(this.m_seed);
/*  95:168 */     if (!getPreserveOrder()) {
/*  96:169 */       dataSet.randomize(random);
/*  97:    */     }
/*  98:171 */     if ((dataSet.classIndex() >= 0) && (dataSet.attribute(dataSet.classIndex()).isNominal()) && (!getPreserveOrder()))
/*  99:    */     {
/* 100:174 */       getStepManager().logBasic("Stratifying data");
/* 101:175 */       dataSet.stratify(this.m_numFolds);
/* 102:    */     }
/* 103:178 */     for (int i = 0; i < this.m_numFolds; i++)
/* 104:    */     {
/* 105:179 */       if (isStopRequested()) {
/* 106:    */         break;
/* 107:    */       }
/* 108:182 */       Instances train = !this.m_preserveOrder ? dataSet.trainCV(this.m_numFolds, i, random) : dataSet.trainCV(this.m_numFolds, i);
/* 109:    */       
/* 110:    */ 
/* 111:185 */       Instances test = dataSet.testCV(this.m_numFolds, i);
/* 112:    */       
/* 113:187 */       Data trainData = new Data("trainingSet");
/* 114:188 */       trainData.setPayloadElement("trainingSet", train);
/* 115:189 */       trainData.setPayloadElement("aux_set_num", Integer.valueOf(i + 1));
/* 116:190 */       trainData.setPayloadElement("aux_max_set_num", Integer.valueOf(this.m_numFolds));
/* 117:    */       
/* 118:192 */       Data testData = new Data("testSet");
/* 119:193 */       testData.setPayloadElement("testSet", test);
/* 120:194 */       testData.setPayloadElement("aux_set_num", Integer.valueOf(i + 1));
/* 121:195 */       testData.setPayloadElement("aux_max_set_num", Integer.valueOf(this.m_numFolds));
/* 122:197 */       if (!isStopRequested()) {
/* 123:198 */         getStepManager().outputData(new Data[] { trainData, testData });
/* 124:    */       }
/* 125:    */     }
/* 126:202 */     getStepManager().finished();
/* 127:    */   }
/* 128:    */   
/* 129:    */   public List<String> getIncomingConnectionTypes()
/* 130:    */   {
/* 131:216 */     if (getStepManager().numIncomingConnections() > 0) {
/* 132:217 */       return new ArrayList();
/* 133:    */     }
/* 134:220 */     return Arrays.asList(new String[] { "dataSet", "trainingSet", "testSet" });
/* 135:    */   }
/* 136:    */   
/* 137:    */   public List<String> getOutgoingConnectionTypes()
/* 138:    */   {
/* 139:235 */     return getStepManager().numIncomingConnections() > 0 ? Arrays.asList(new String[] { "trainingSet", "testSet" }) : new ArrayList();
/* 140:    */   }
/* 141:    */   
/* 142:    */   public Instances outputStructureForConnectionType(String connectionName)
/* 143:    */     throws WekaException
/* 144:    */   {
/* 145:255 */     if (((!connectionName.equals("trainingSet")) && (!connectionName.equals("testSet"))) || (getStepManager().numIncomingConnections() == 0)) {
/* 146:258 */       return null;
/* 147:    */     }
/* 148:262 */     Instances strucForDatasetCon = getStepManager().getIncomingStructureForConnectionType("dataSet");
/* 149:264 */     if (strucForDatasetCon != null) {
/* 150:265 */       return strucForDatasetCon;
/* 151:    */     }
/* 152:268 */     Instances strucForTestsetCon = getStepManager().getIncomingStructureForConnectionType("testSet");
/* 153:270 */     if (strucForTestsetCon != null) {
/* 154:271 */       return strucForTestsetCon;
/* 155:    */     }
/* 156:274 */     Instances strucForTrainingCon = getStepManager().getIncomingStructureForConnectionType("trainingSet");
/* 157:276 */     if (strucForTrainingCon != null) {
/* 158:277 */       return strucForTrainingCon;
/* 159:    */     }
/* 160:280 */     return null;
/* 161:    */   }
/* 162:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.CrossValidationFoldMaker
 * JD-Core Version:    0.7.0.1
 */