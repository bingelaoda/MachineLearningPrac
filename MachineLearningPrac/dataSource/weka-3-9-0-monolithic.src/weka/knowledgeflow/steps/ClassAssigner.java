/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.List;
/*   6:    */ import weka.core.Attribute;
/*   7:    */ import weka.core.Instance;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.WekaException;
/*  10:    */ import weka.knowledgeflow.Data;
/*  11:    */ import weka.knowledgeflow.StepManager;
/*  12:    */ 
/*  13:    */ @KFStep(name="ClassAssigner", category="Evaluation", toolTipText="Designate which column is to be considered the class column in incoming data.", iconPath="weka/gui/knowledgeflow/icons/ClassAssigner.gif")
/*  14:    */ public class ClassAssigner
/*  15:    */   extends BaseStep
/*  16:    */ {
/*  17:    */   private static final long serialVersionUID = -4269063233834866140L;
/*  18: 51 */   protected String m_classColumnS = "/last";
/*  19: 54 */   protected String m_classCol = "/last";
/*  20:    */   protected boolean m_classAssigned;
/*  21:    */   protected boolean m_isInstanceStream;
/*  22:    */   
/*  23:    */   public void setClassColumn(String col)
/*  24:    */   {
/*  25: 68 */     this.m_classColumnS = col;
/*  26:    */   }
/*  27:    */   
/*  28:    */   public String getClassColumn()
/*  29:    */   {
/*  30: 77 */     return this.m_classColumnS;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public void stepInit()
/*  34:    */     throws WekaException
/*  35:    */   {
/*  36: 87 */     if ((this.m_classColumnS == null) || (this.m_classColumnS.length() == 0)) {
/*  37: 88 */       throw new WekaException("No class column specified!");
/*  38:    */     }
/*  39: 91 */     this.m_classCol = getStepManager().environmentSubstitute(this.m_classColumnS).trim();
/*  40: 92 */     this.m_classAssigned = false;
/*  41: 93 */     this.m_isInstanceStream = false;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public void processIncoming(Data data)
/*  45:    */     throws WekaException
/*  46:    */   {
/*  47:104 */     Object payload = data.getPayloadElement(data.getConnectionName());
/*  48:105 */     if (!this.m_classAssigned)
/*  49:    */     {
/*  50:106 */       if (data.getConnectionName().equals("instance"))
/*  51:    */       {
/*  52:107 */         this.m_isInstanceStream = true;
/*  53:108 */         Instance inst = (Instance)payload;
/*  54:109 */         if (inst != null) {
/*  55:110 */           assignClass(inst.dataset());
/*  56:    */         }
/*  57:    */       }
/*  58:    */       else
/*  59:    */       {
/*  60:113 */         getStepManager().processing();
/*  61:114 */         if (payload == null) {
/*  62:115 */           throw new WekaException("Incoming data is null!");
/*  63:    */         }
/*  64:117 */         assignClass((Instances)payload);
/*  65:    */       }
/*  66:119 */       this.m_classAssigned = true;
/*  67:    */     }
/*  68:122 */     if (isStopRequested())
/*  69:    */     {
/*  70:123 */       if (!this.m_isInstanceStream) {
/*  71:124 */         getStepManager().interrupted();
/*  72:    */       }
/*  73:126 */       return;
/*  74:    */     }
/*  75:129 */     if (this.m_isInstanceStream)
/*  76:    */     {
/*  77:130 */       if (!getStepManager().isStreamFinished(data))
/*  78:    */       {
/*  79:131 */         getStepManager().throughputUpdateStart();
/*  80:    */       }
/*  81:    */       else
/*  82:    */       {
/*  83:133 */         getStepManager().throughputFinished(new Data[] { new Data(data.getConnectionName()) });
/*  84:134 */         return;
/*  85:    */       }
/*  86:136 */       getStepManager().throughputUpdateEnd();
/*  87:    */     }
/*  88:139 */     getStepManager().outputData(data.getConnectionName(), data);
/*  89:141 */     if ((!this.m_isInstanceStream) || (payload == null)) {
/*  90:142 */       if (!isStopRequested()) {
/*  91:143 */         getStepManager().finished();
/*  92:    */       } else {
/*  93:145 */         getStepManager().interrupted();
/*  94:    */       }
/*  95:    */     }
/*  96:    */   }
/*  97:    */   
/*  98:    */   protected void assignClass(Instances dataSet)
/*  99:    */     throws WekaException
/* 100:    */   {
/* 101:157 */     Attribute classAtt = dataSet.attribute(this.m_classCol);
/* 102:158 */     boolean assigned = false;
/* 103:159 */     if (classAtt != null)
/* 104:    */     {
/* 105:160 */       dataSet.setClass(classAtt);
/* 106:161 */       assigned = true;
/* 107:    */     }
/* 108:163 */     else if ((this.m_classCol.equalsIgnoreCase("last")) || (this.m_classCol.equalsIgnoreCase("/last")))
/* 109:    */     {
/* 110:165 */       dataSet.setClassIndex(dataSet.numAttributes() - 1);
/* 111:166 */       assigned = true;
/* 112:    */     }
/* 113:167 */     else if ((this.m_classCol.equalsIgnoreCase("first")) || (this.m_classCol.equalsIgnoreCase("/first")))
/* 114:    */     {
/* 115:169 */       dataSet.setClassIndex(0);
/* 116:170 */       assigned = true;
/* 117:    */     }
/* 118:    */     else
/* 119:    */     {
/* 120:    */       try
/* 121:    */       {
/* 122:174 */         int classIndex = Integer.parseInt(this.m_classCol);
/* 123:175 */         classIndex--;
/* 124:176 */         if ((classIndex >= 0) && (classIndex < dataSet.numAttributes()))
/* 125:    */         {
/* 126:177 */           dataSet.setClassIndex(classIndex);
/* 127:178 */           assigned = true;
/* 128:    */         }
/* 129:    */       }
/* 130:    */       catch (NumberFormatException ex) {}
/* 131:    */     }
/* 132:185 */     if (!assigned) {
/* 133:186 */       throw new WekaException("Unable to assign '" + this.m_classCol + "' as the class.");
/* 134:    */     }
/* 135:189 */     getStepManager().logBasic("Assigned '" + dataSet.classAttribute().name() + "' as class.");
/* 136:    */   }
/* 137:    */   
/* 138:    */   public List<String> getIncomingConnectionTypes()
/* 139:    */   {
/* 140:200 */     if (getStepManager().numIncomingConnections() == 0) {
/* 141:201 */       return Arrays.asList(new String[] { "dataSet", "trainingSet", "testSet", "instance" });
/* 142:    */     }
/* 143:205 */     return new ArrayList();
/* 144:    */   }
/* 145:    */   
/* 146:    */   public List<String> getOutgoingConnectionTypes()
/* 147:    */   {
/* 148:215 */     List<String> result = new ArrayList();
/* 149:216 */     if (getStepManager().numIncomingConnectionsOfType("instance") > 0) {
/* 150:218 */       result.add("instance");
/* 151:219 */     } else if (getStepManager().numIncomingConnectionsOfType("dataSet") > 0) {
/* 152:221 */       result.add("dataSet");
/* 153:222 */     } else if (getStepManager().numIncomingConnectionsOfType("trainingSet") > 0) {
/* 154:224 */       result.add("trainingSet");
/* 155:225 */     } else if (getStepManager().numIncomingConnectionsOfType("testSet") > 0) {
/* 156:227 */       result.add("testSet");
/* 157:    */     }
/* 158:230 */     return result;
/* 159:    */   }
/* 160:    */   
/* 161:    */   public Instances outputStructureForConnectionType(String connectionName)
/* 162:    */     throws WekaException
/* 163:    */   {
/* 164:245 */     this.m_classCol = getStepManager().environmentSubstitute(this.m_classColumnS).trim();
/* 165:246 */     if (((!connectionName.equals("dataSet")) && (!connectionName.equals("trainingSet")) && (!connectionName.equals("testSet")) && (!connectionName.equals("instance"))) || (getStepManager().numIncomingConnections() == 0)) {
/* 166:251 */       return null;
/* 167:    */     }
/* 168:255 */     Instances strucForDatasetCon = getStepManager().getIncomingStructureForConnectionType("dataSet");
/* 169:257 */     if (strucForDatasetCon != null)
/* 170:    */     {
/* 171:258 */       assignClass(strucForDatasetCon);
/* 172:259 */       return strucForDatasetCon;
/* 173:    */     }
/* 174:262 */     Instances strucForTestsetCon = getStepManager().getIncomingStructureForConnectionType("testSet");
/* 175:264 */     if (strucForTestsetCon != null)
/* 176:    */     {
/* 177:265 */       assignClass(strucForTestsetCon);
/* 178:266 */       return strucForTestsetCon;
/* 179:    */     }
/* 180:269 */     Instances strucForTrainingCon = getStepManager().getIncomingStructureForConnectionType("trainingSet");
/* 181:271 */     if (strucForTrainingCon != null)
/* 182:    */     {
/* 183:272 */       assignClass(strucForTrainingCon);
/* 184:273 */       return strucForTrainingCon;
/* 185:    */     }
/* 186:276 */     Instances strucForInstanceCon = getStepManager().getIncomingStructureForConnectionType("instance");
/* 187:278 */     if (strucForInstanceCon != null)
/* 188:    */     {
/* 189:279 */       assignClass(strucForInstanceCon);
/* 190:280 */       return strucForInstanceCon;
/* 191:    */     }
/* 192:283 */     return null;
/* 193:    */   }
/* 194:    */   
/* 195:    */   public String getCustomEditorForStep()
/* 196:    */   {
/* 197:294 */     return "weka.gui.knowledgeflow.steps.ClassAssignerStepEditorDialog";
/* 198:    */   }
/* 199:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.ClassAssigner
 * JD-Core Version:    0.7.0.1
 */