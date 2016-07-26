/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.List;
/*   6:    */ import weka.core.Attribute;
/*   7:    */ import weka.core.Instances;
/*   8:    */ import weka.core.OptionMetadata;
/*   9:    */ import weka.core.WekaException;
/*  10:    */ import weka.filters.Filter;
/*  11:    */ import weka.filters.unsupervised.attribute.SwapValues;
/*  12:    */ import weka.knowledgeflow.Data;
/*  13:    */ import weka.knowledgeflow.StepManager;
/*  14:    */ 
/*  15:    */ @KFStep(name="ClassValuePicker", category="Evaluation", toolTipText="Designate which class value is considered the \"positive\" class value (useful for ROC analysis)", iconPath="weka/gui/knowledgeflow/icons/ClassValuePicker.gif")
/*  16:    */ public class ClassValuePicker
/*  17:    */   extends BaseStep
/*  18:    */ {
/*  19:    */   private static final long serialVersionUID = 8558445535347028472L;
/*  20: 57 */   protected String m_classValueS = "/first";
/*  21: 60 */   protected String m_classValue = "/first";
/*  22:    */   protected boolean m_classIsSet;
/*  23:    */   protected boolean m_classIsNominal;
/*  24:    */   
/*  25:    */   @OptionMetadata(displayName="Class value", description="The class value to consider as the 'positive' class", displayOrder=1)
/*  26:    */   public void setClassValue(String value)
/*  27:    */   {
/*  28: 77 */     this.m_classValueS = value;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public String getClassValue()
/*  32:    */   {
/*  33: 86 */     return this.m_classValueS;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public void stepInit()
/*  37:    */     throws WekaException
/*  38:    */   {
/*  39: 96 */     this.m_classIsSet = true;
/*  40: 97 */     this.m_classIsNominal = true;
/*  41:    */     
/*  42: 99 */     this.m_classValue = getStepManager().environmentSubstitute(this.m_classValueS).trim();
/*  43:100 */     if (this.m_classValue.length() == 0) {
/*  44:101 */       throw new WekaException("No class label specified as the positive class!");
/*  45:    */     }
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void processIncoming(Data data)
/*  49:    */     throws WekaException
/*  50:    */   {
/*  51:113 */     getStepManager().processing();
/*  52:114 */     Instances dataSet = (Instances)data.getPayloadElement(data.getConnectionName());
/*  53:116 */     if (dataSet == null) {
/*  54:117 */       throw new WekaException("Data should not be null!");
/*  55:    */     }
/*  56:120 */     if (dataSet.classAttribute() == null)
/*  57:    */     {
/*  58:121 */       getStepManager().logWarning("No class attribute set in the data");
/*  59:122 */       this.m_classIsSet = false;
/*  60:    */     }
/*  61:125 */     if ((this.m_classIsSet) && (dataSet.classAttribute().isNumeric()))
/*  62:    */     {
/*  63:126 */       getStepManager().logWarning("Class is numeric");
/*  64:127 */       this.m_classIsNominal = false;
/*  65:    */     }
/*  66:130 */     Instances newDataSet = dataSet;
/*  67:131 */     if ((this.m_classIsSet) && (this.m_classIsNominal)) {
/*  68:132 */       newDataSet = assignClassValue(dataSet);
/*  69:    */     }
/*  70:135 */     Data newData = new Data(data.getConnectionName());
/*  71:136 */     newData.setPayloadElement(data.getConnectionName(), newDataSet);
/*  72:137 */     getStepManager().outputData(new Data[] { newData });
/*  73:    */     
/*  74:139 */     getStepManager().finished();
/*  75:    */   }
/*  76:    */   
/*  77:    */   protected Instances assignClassValue(Instances dataSet)
/*  78:    */     throws WekaException
/*  79:    */   {
/*  80:151 */     Attribute classAtt = dataSet.classAttribute();
/*  81:152 */     int classValueIndex = classAtt.indexOfValue(this.m_classValue);
/*  82:154 */     if (classValueIndex == -1) {
/*  83:155 */       if ((this.m_classValue.equalsIgnoreCase("last")) || (this.m_classValue.equalsIgnoreCase("/last")))
/*  84:    */       {
/*  85:157 */         classValueIndex = classAtt.numValues() - 1;
/*  86:    */       }
/*  87:158 */       else if ((this.m_classValue.equalsIgnoreCase("first")) || (this.m_classValue.equalsIgnoreCase("/first")))
/*  88:    */       {
/*  89:160 */         classValueIndex = 0;
/*  90:    */       }
/*  91:    */       else
/*  92:    */       {
/*  93:163 */         String clV = this.m_classValue;
/*  94:164 */         if ((this.m_classValue.startsWith("/")) && (this.m_classValue.length() > 1)) {
/*  95:165 */           clV = clV.substring(1);
/*  96:    */         }
/*  97:    */         try
/*  98:    */         {
/*  99:168 */           classValueIndex = Integer.parseInt(clV);
/* 100:169 */           classValueIndex--;
/* 101:    */         }
/* 102:    */         catch (NumberFormatException ex) {}
/* 103:    */       }
/* 104:    */     }
/* 105:174 */     if ((classValueIndex < 0) || (classValueIndex > classAtt.numValues() - 1)) {
/* 106:175 */       throw new WekaException("Class label/index '" + this.m_classValue + "' is unknown or out of range!");
/* 107:    */     }
/* 108:179 */     if (classValueIndex != 0) {
/* 109:    */       try
/* 110:    */       {
/* 111:181 */         SwapValues sv = new SwapValues();
/* 112:182 */         sv.setAttributeIndex("" + (dataSet.classIndex() + 1));
/* 113:183 */         sv.setFirstValueIndex("first");
/* 114:184 */         sv.setSecondValueIndex("" + (classValueIndex + 1));
/* 115:185 */         sv.setInputFormat(dataSet);
/* 116:186 */         Instances newDataSet = Filter.useFilter(dataSet, sv);
/* 117:187 */         newDataSet.setRelationName(dataSet.relationName());
/* 118:    */         
/* 119:189 */         getStepManager().logBasic("New class value: " + newDataSet.classAttribute().value(0));
/* 120:    */         
/* 121:191 */         return newDataSet;
/* 122:    */       }
/* 123:    */       catch (Exception ex)
/* 124:    */       {
/* 125:193 */         throw new WekaException(ex);
/* 126:    */       }
/* 127:    */     }
/* 128:197 */     return dataSet;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public List<String> getIncomingConnectionTypes()
/* 132:    */   {
/* 133:211 */     if (getStepManager().numIncomingConnections() > 0) {
/* 134:212 */       return new ArrayList();
/* 135:    */     }
/* 136:214 */     return Arrays.asList(new String[] { "dataSet", "trainingSet", "testSet" });
/* 137:    */   }
/* 138:    */   
/* 139:    */   public List<String> getOutgoingConnectionTypes()
/* 140:    */   {
/* 141:229 */     List<String> result = new ArrayList();
/* 142:231 */     if (getStepManager().numIncomingConnectionsOfType("dataSet") > 0) {
/* 143:232 */       result.add("dataSet");
/* 144:233 */     } else if (getStepManager().numIncomingConnectionsOfType("trainingSet") > 0) {
/* 145:235 */       result.add("trainingSet");
/* 146:236 */     } else if (getStepManager().numIncomingConnectionsOfType("testSet") > 0) {
/* 147:238 */       result.add("testSet");
/* 148:    */     }
/* 149:241 */     return result;
/* 150:    */   }
/* 151:    */   
/* 152:    */   public Instances outputStructureForConnectionType(String connectionName)
/* 153:    */     throws WekaException
/* 154:    */   {
/* 155:258 */     this.m_classValue = getStepManager().environmentSubstitute(this.m_classValueS).trim();
/* 156:259 */     if (((!connectionName.equals("dataSet")) && (!connectionName.equals("trainingSet")) && (!connectionName.equals("testSet")) && (!connectionName.equals("instance"))) || (getStepManager().numIncomingConnections() == 0)) {
/* 157:264 */       return null;
/* 158:    */     }
/* 159:268 */     Instances strucForDatasetCon = getStepManager().getIncomingStructureForConnectionType("dataSet");
/* 160:271 */     if (strucForDatasetCon != null) {
/* 161:273 */       return strucForDatasetCon;
/* 162:    */     }
/* 163:276 */     Instances strucForTestsetCon = getStepManager().getIncomingStructureForConnectionType("testSet");
/* 164:279 */     if (strucForTestsetCon != null) {
/* 165:281 */       return strucForTestsetCon;
/* 166:    */     }
/* 167:284 */     Instances strucForTrainingCon = getStepManager().getIncomingStructureForConnectionType("trainingSet");
/* 168:287 */     if (strucForTrainingCon != null) {
/* 169:289 */       return strucForTrainingCon;
/* 170:    */     }
/* 171:292 */     Instances strucForInstanceCon = getStepManager().getIncomingStructureForConnectionType("instance");
/* 172:295 */     if (strucForInstanceCon != null) {
/* 173:297 */       return strucForInstanceCon;
/* 174:    */     }
/* 175:300 */     return null;
/* 176:    */   }
/* 177:    */   
/* 178:    */   public String getCustomEditorForStep()
/* 179:    */   {
/* 180:313 */     return "weka.gui.knowledgeflow.steps.ClassValuePickerStepEditorDialog";
/* 181:    */   }
/* 182:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.ClassValuePicker
 * JD-Core Version:    0.7.0.1
 */