/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.StringReader;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.List;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.SerializedObject;
/*  11:    */ import weka.core.WekaException;
/*  12:    */ import weka.gui.Logger;
/*  13:    */ import weka.gui.ProgrammaticProperty;
/*  14:    */ import weka.gui.beans.StreamThroughput;
/*  15:    */ import weka.knowledgeflow.Data;
/*  16:    */ import weka.knowledgeflow.StepManager;
/*  17:    */ import weka.knowledgeflow.StepManagerImpl;
/*  18:    */ 
/*  19:    */ @KFStep(name="DataGrid", category="DataSources", toolTipText="Specify a grid of data to turn into instances", iconPath="weka/gui/knowledgeflow/icons/ArffLoader.gif")
/*  20:    */ public class DataGrid
/*  21:    */   extends BaseStep
/*  22:    */ {
/*  23:    */   private static final long serialVersionUID = 1318159328875458847L;
/*  24: 54 */   protected String m_data = "";
/*  25:    */   protected Data m_incrementalData;
/*  26:    */   protected StreamThroughput m_flowThroughput;
/*  27:    */   
/*  28:    */   @ProgrammaticProperty
/*  29:    */   public void setData(String data)
/*  30:    */   {
/*  31: 69 */     this.m_data = data;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public String getData()
/*  35:    */   {
/*  36: 78 */     return this.m_data;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void stepInit()
/*  40:    */     throws WekaException
/*  41:    */   {
/*  42: 88 */     if (getStepManager().numOutgoingConnectionsOfType("instance") > 0)
/*  43:    */     {
/*  44: 89 */       this.m_incrementalData = new Data("instance");
/*  45:    */     }
/*  46:    */     else
/*  47:    */     {
/*  48: 91 */       this.m_incrementalData = null;
/*  49: 92 */       this.m_flowThroughput = null;
/*  50:    */     }
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void start()
/*  54:    */     throws WekaException
/*  55:    */   {
/*  56:103 */     if (getStepManager().numOutgoingConnections() > 0)
/*  57:    */     {
/*  58:104 */       if (this.m_data.length() == 0) {
/*  59:105 */         getStepManager().logWarning("No data to output!");
/*  60:    */       } else {
/*  61:    */         try
/*  62:    */         {
/*  63:109 */           String data = environmentSubstitute(this.m_data);
/*  64:110 */           Instances toOutput = new Instances(new StringReader(data));
/*  65:111 */           if (getStepManager().numOutgoingConnectionsOfType("dataSet") > 0)
/*  66:    */           {
/*  67:113 */             getStepManager().processing();
/*  68:114 */             Data batch = new Data("dataSet", toOutput);
/*  69:115 */             getStepManager().outputData(new Data[] { batch });
/*  70:116 */             getStepManager().finished();
/*  71:    */           }
/*  72:    */           else
/*  73:    */           {
/*  74:119 */             String stm = getName() + "$" + hashCode() + 99 + "| overall flow throughput -|";
/*  75:    */             
/*  76:    */ 
/*  77:122 */             this.m_flowThroughput = new StreamThroughput(stm, "Starting flow...", ((StepManagerImpl)getStepManager()).getLog());
/*  78:    */             
/*  79:    */ 
/*  80:125 */             Instances structure = toOutput.stringFreeStructure();
/*  81:126 */             Instances structureCopy = null;
/*  82:127 */             Instances currentStructure = structure;
/*  83:128 */             boolean containsStrings = toOutput.checkForStringAttributes();
/*  84:129 */             if (containsStrings) {
/*  85:130 */               structureCopy = (Instances)new SerializedObject(structure).getObject();
/*  86:    */             }
/*  87:134 */             if (isStopRequested())
/*  88:    */             {
/*  89:135 */               getStepManager().interrupted();
/*  90:136 */               return;
/*  91:    */             }
/*  92:139 */             for (int i = 0; i < toOutput.numInstances(); i++)
/*  93:    */             {
/*  94:140 */               if (isStopRequested()) {
/*  95:    */                 break;
/*  96:    */               }
/*  97:143 */               Instance nextInst = toOutput.instance(i);
/*  98:144 */               this.m_flowThroughput.updateStart();
/*  99:145 */               getStepManager().throughputUpdateStart();
/* 100:146 */               if (containsStrings)
/* 101:    */               {
/* 102:147 */                 if (currentStructure == structure) {
/* 103:148 */                   currentStructure = structureCopy;
/* 104:    */                 } else {
/* 105:150 */                   currentStructure = structure;
/* 106:    */                 }
/* 107:153 */                 for (int j = 0; j < toOutput.numAttributes(); j++) {
/* 108:154 */                   if ((toOutput.attribute(j).isString()) && 
/* 109:155 */                     (!nextInst.isMissing(j)))
/* 110:    */                   {
/* 111:156 */                     currentStructure.attribute(j).setStringValue(nextInst.stringValue(j));
/* 112:    */                     
/* 113:158 */                     nextInst.setValue(j, 0.0D);
/* 114:    */                   }
/* 115:    */                 }
/* 116:    */               }
/* 117:164 */               nextInst.setDataset(currentStructure);
/* 118:165 */               this.m_incrementalData.setPayloadElement("instance", nextInst);
/* 119:    */               
/* 120:167 */               getStepManager().throughputUpdateEnd();
/* 121:168 */               getStepManager().outputData(new Data[] { this.m_incrementalData });
/* 122:169 */               this.m_flowThroughput.updateEnd(((StepManagerImpl)getStepManager()).getLog());
/* 123:    */             }
/* 124:173 */             if (isStopRequested())
/* 125:    */             {
/* 126:174 */               ((StepManagerImpl)getStepManager()).getLog().statusMessage(stm + "remove");
/* 127:    */               
/* 128:176 */               getStepManager().interrupted();
/* 129:177 */               return;
/* 130:    */             }
/* 131:179 */             this.m_flowThroughput.finished(((StepManagerImpl)getStepManager()).getLog());
/* 132:    */             
/* 133:    */ 
/* 134:    */ 
/* 135:183 */             this.m_incrementalData.clearPayload();
/* 136:184 */             getStepManager().throughputFinished(new Data[] { this.m_incrementalData });
/* 137:    */           }
/* 138:    */         }
/* 139:    */         catch (Exception ex)
/* 140:    */         {
/* 141:187 */           throw new WekaException(ex);
/* 142:    */         }
/* 143:    */       }
/* 144:    */     }
/* 145:    */     else {
/* 146:191 */       getStepManager().logWarning("No connected outputs");
/* 147:    */     }
/* 148:    */   }
/* 149:    */   
/* 150:    */   public Instances outputStructureForConnectionType(String connectionName)
/* 151:    */     throws WekaException
/* 152:    */   {
/* 153:208 */     if (getStepManager().isStepBusy()) {
/* 154:209 */       return null;
/* 155:    */     }
/* 156:212 */     if ((getStepManager().numOutgoingConnectionsOfType("dataSet") == 0) && (getStepManager().numOutgoingConnectionsOfType("instance") == 0)) {
/* 157:215 */       return null;
/* 158:    */     }
/* 159:    */     try
/* 160:    */     {
/* 161:219 */       return new Instances(new StringReader(this.m_data)).stringFreeStructure();
/* 162:    */     }
/* 163:    */     catch (IOException e)
/* 164:    */     {
/* 165:221 */       throw new WekaException(e);
/* 166:    */     }
/* 167:    */   }
/* 168:    */   
/* 169:    */   public List<String> getIncomingConnectionTypes()
/* 170:    */   {
/* 171:227 */     return null;
/* 172:    */   }
/* 173:    */   
/* 174:    */   public List<String> getOutgoingConnectionTypes()
/* 175:    */   {
/* 176:232 */     List<String> result = new ArrayList();
/* 177:233 */     if (getStepManager().numOutgoingConnections() == 0)
/* 178:    */     {
/* 179:234 */       result.add("dataSet");
/* 180:235 */       result.add("instance");
/* 181:    */     }
/* 182:236 */     else if (getStepManager().numOutgoingConnectionsOfType("dataSet") > 0)
/* 183:    */     {
/* 184:238 */       result.add("dataSet");
/* 185:    */     }
/* 186:    */     else
/* 187:    */     {
/* 188:240 */       result.add("instance");
/* 189:    */     }
/* 190:243 */     return result;
/* 191:    */   }
/* 192:    */   
/* 193:    */   public String getCustomEditorForStep()
/* 194:    */   {
/* 195:256 */     return "weka.gui.knowledgeflow.steps.DataGridStepEditorDialog";
/* 196:    */   }
/* 197:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.DataGrid
 * JD-Core Version:    0.7.0.1
 */