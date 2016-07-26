/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.List;
/*   7:    */ import weka.core.Environment;
/*   8:    */ import weka.core.EnvironmentHandler;
/*   9:    */ import weka.core.Instance;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.SerializedObject;
/*  12:    */ import weka.core.WekaException;
/*  13:    */ import weka.core.converters.FileSourcedConverter;
/*  14:    */ import weka.gui.Logger;
/*  15:    */ import weka.gui.ProgrammaticProperty;
/*  16:    */ import weka.gui.beans.StreamThroughput;
/*  17:    */ import weka.knowledgeflow.Data;
/*  18:    */ import weka.knowledgeflow.ExecutionEnvironment;
/*  19:    */ import weka.knowledgeflow.StepManager;
/*  20:    */ import weka.knowledgeflow.StepManagerImpl;
/*  21:    */ 
/*  22:    */ @KFStep(name="Loader", category="DataSources", toolTipText="Weka loader wrapper", iconPath="")
/*  23:    */ public class Loader
/*  24:    */   extends WekaAlgorithmWrapper
/*  25:    */   implements Serializable
/*  26:    */ {
/*  27:    */   private static final long serialVersionUID = -788869066035779154L;
/*  28:    */   protected String m_globalInfo;
/*  29:    */   protected boolean m_instanceGeneration;
/*  30:    */   protected boolean m_noOutputs;
/*  31:    */   protected Data m_instanceData;
/*  32:    */   protected StreamThroughput m_flowThroughput;
/*  33:    */   
/*  34:    */   public Class getWrappedAlgorithmClass()
/*  35:    */   {
/*  36: 78 */     return weka.core.converters.Loader.class;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void setWrappedAlgorithm(Object algo)
/*  40:    */   {
/*  41: 88 */     super.setWrappedAlgorithm(algo);
/*  42: 89 */     this.m_defaultIconPath = "weka/gui/knowledgeflow/icons/DefaultDataSource.gif";
/*  43:    */   }
/*  44:    */   
/*  45:    */   public weka.core.converters.Loader getLoader()
/*  46:    */   {
/*  47: 98 */     return (weka.core.converters.Loader)getWrappedAlgorithm();
/*  48:    */   }
/*  49:    */   
/*  50:    */   @ProgrammaticProperty
/*  51:    */   public void setLoader(weka.core.converters.Loader loader)
/*  52:    */   {
/*  53:108 */     setWrappedAlgorithm(loader);
/*  54:    */   }
/*  55:    */   
/*  56:    */   public void stepInit()
/*  57:    */     throws WekaException
/*  58:    */   {
/*  59:119 */     if (!(getWrappedAlgorithm() instanceof weka.core.converters.Loader)) {
/*  60:120 */       throw new WekaException("Incorrect type of algorithm");
/*  61:    */     }
/*  62:123 */     int numDatasetOutputs = getStepManager().numOutgoingConnectionsOfType("dataSet");
/*  63:    */     
/*  64:125 */     int numInstanceOutputs = getStepManager().numOutgoingConnectionsOfType("instance");
/*  65:    */     
/*  66:    */ 
/*  67:128 */     this.m_noOutputs = ((numInstanceOutputs == 0) && (numDatasetOutputs == 0));
/*  68:130 */     if ((numDatasetOutputs > 0) && (numInstanceOutputs > 0)) {
/*  69:131 */       throw new WekaException("Can't have both instance and dataSet outgoing connections!");
/*  70:    */     }
/*  71:135 */     if ((getWrappedAlgorithm() instanceof EnvironmentHandler)) {
/*  72:136 */       ((EnvironmentHandler)getWrappedAlgorithm()).setEnvironment(getStepManager().getExecutionEnvironment().getEnvironmentVariables());
/*  73:    */     }
/*  74:141 */     this.m_instanceGeneration = (numInstanceOutputs > 0);
/*  75:142 */     this.m_instanceData = new Data("instance");
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void start()
/*  79:    */     throws WekaException
/*  80:    */   {
/*  81:152 */     if (this.m_noOutputs) {
/*  82:153 */       return;
/*  83:    */     }
/*  84:155 */     getStepManager().processing();
/*  85:    */     
/*  86:157 */     weka.core.converters.Loader theLoader = (weka.core.converters.Loader)getWrappedAlgorithm();
/*  87:    */     
/*  88:    */ 
/*  89:160 */     String startMessage = (theLoader instanceof FileSourcedConverter) ? "Loading " + ((FileSourcedConverter)theLoader).retrieveFile().getName() : "Loading...";
/*  90:    */     
/*  91:    */ 
/*  92:    */ 
/*  93:    */ 
/*  94:165 */     getStepManager().logBasic(startMessage);
/*  95:166 */     getStepManager().statusMessage(startMessage);
/*  96:168 */     if (!this.m_instanceGeneration)
/*  97:    */     {
/*  98:    */       try
/*  99:    */       {
/* 100:170 */         theLoader.reset();
/* 101:171 */         theLoader.setRetrieval(1);
/* 102:172 */         Instances dataset = theLoader.getDataSet();
/* 103:173 */         getStepManager().logBasic("Loaded " + dataset.relationName());
/* 104:174 */         Data data = new Data();
/* 105:175 */         data.setPayloadElement("dataSet", dataset);
/* 106:176 */         getStepManager().outputData("dataSet", data);
/* 107:    */       }
/* 108:    */       catch (Exception ex)
/* 109:    */       {
/* 110:178 */         throw new WekaException(ex);
/* 111:    */       }
/* 112:    */       finally
/* 113:    */       {
/* 114:180 */         getStepManager().finished();
/* 115:    */       }
/* 116:    */     }
/* 117:    */     else
/* 118:    */     {
/* 119:183 */       String stm = getName() + "$" + hashCode() + 99 + "| overall flow throughput -|";
/* 120:    */       
/* 121:185 */       this.m_flowThroughput = new StreamThroughput(stm, "Starting flow...", ((StepManagerImpl)getStepManager()).getLog());
/* 122:    */       
/* 123:    */ 
/* 124:    */ 
/* 125:189 */       Instance nextInstance = null;
/* 126:190 */       Instances structure = null;
/* 127:191 */       Instances structureCopy = null;
/* 128:192 */       Instances currentStructure = null;
/* 129:193 */       boolean stringAttsPresent = false;
/* 130:    */       try
/* 131:    */       {
/* 132:196 */         theLoader.reset();
/* 133:197 */         theLoader.setRetrieval(2);
/* 134:198 */         structure = theLoader.getStructure();
/* 135:199 */         if (structure.checkForStringAttributes())
/* 136:    */         {
/* 137:200 */           structureCopy = (Instances)new SerializedObject(structure).getObject();
/* 138:    */           
/* 139:202 */           stringAttsPresent = true;
/* 140:    */         }
/* 141:204 */         currentStructure = structure;
/* 142:    */       }
/* 143:    */       catch (Exception ex)
/* 144:    */       {
/* 145:206 */         throw new WekaException(ex);
/* 146:    */       }
/* 147:209 */       if (isStopRequested()) {
/* 148:210 */         return;
/* 149:    */       }
/* 150:    */       try
/* 151:    */       {
/* 152:214 */         nextInstance = theLoader.getNextInstance(structure);
/* 153:    */       }
/* 154:    */       catch (Exception ex)
/* 155:    */       {
/* 156:217 */         throw new WekaException(ex);
/* 157:    */       }
/* 158:220 */       while ((!isStopRequested()) && (nextInstance != null))
/* 159:    */       {
/* 160:221 */         this.m_flowThroughput.updateStart();
/* 161:222 */         getStepManager().throughputUpdateStart();
/* 162:224 */         if (stringAttsPresent) {
/* 163:225 */           if (currentStructure == structure) {
/* 164:226 */             currentStructure = structureCopy;
/* 165:    */           } else {
/* 166:228 */             currentStructure = structure;
/* 167:    */           }
/* 168:    */         }
/* 169:232 */         this.m_instanceData.setPayloadElement("instance", nextInstance);
/* 170:    */         try
/* 171:    */         {
/* 172:236 */           nextInstance = theLoader.getNextInstance(currentStructure);
/* 173:    */         }
/* 174:    */         catch (Exception ex)
/* 175:    */         {
/* 176:238 */           getStepManager().throughputFinished(new Data[] { this.m_instanceData });
/* 177:239 */           throw new WekaException(ex);
/* 178:    */         }
/* 179:241 */         getStepManager().throughputUpdateEnd();
/* 180:242 */         getStepManager().outputData("instance", this.m_instanceData);
/* 181:    */         
/* 182:244 */         this.m_flowThroughput.updateEnd(((StepManagerImpl)getStepManager()).getLog());
/* 183:    */       }
/* 184:248 */       if (isStopRequested())
/* 185:    */       {
/* 186:249 */         ((StepManagerImpl)getStepManager()).getLog().statusMessage(stm + "remove");
/* 187:    */         
/* 188:251 */         return;
/* 189:    */       }
/* 190:253 */       this.m_flowThroughput.finished(((StepManagerImpl)getStepManager()).getLog());
/* 191:    */       
/* 192:    */ 
/* 193:256 */       this.m_instanceData.clearPayload();
/* 194:257 */       getStepManager().throughputFinished(new Data[] { this.m_instanceData });
/* 195:    */     }
/* 196:    */   }
/* 197:    */   
/* 198:    */   public Instances outputStructureForConnectionType(String connectionName)
/* 199:    */     throws WekaException
/* 200:    */   {
/* 201:280 */     if (getStepManager().isStepBusy()) {
/* 202:281 */       return null;
/* 203:    */     }
/* 204:    */     try
/* 205:    */     {
/* 206:285 */       weka.core.converters.Loader theLoader = (weka.core.converters.Loader)getWrappedAlgorithm();
/* 207:    */       
/* 208:287 */       theLoader.reset();
/* 209:288 */       if ((theLoader instanceof EnvironmentHandler)) {
/* 210:289 */         ((EnvironmentHandler)theLoader).setEnvironment(Environment.getSystemWide());
/* 211:    */       }
/* 212:292 */       return theLoader.getStructure();
/* 213:    */     }
/* 214:    */     catch (Exception ex)
/* 215:    */     {
/* 216:294 */       getStepManager().logError(ex.getMessage(), ex);
/* 217:    */     }
/* 218:297 */     return null;
/* 219:    */   }
/* 220:    */   
/* 221:    */   public List<String> getIncomingConnectionTypes()
/* 222:    */   {
/* 223:312 */     return null;
/* 224:    */   }
/* 225:    */   
/* 226:    */   public List<String> getOutgoingConnectionTypes()
/* 227:    */   {
/* 228:326 */     List<String> outgoing = new ArrayList();
/* 229:327 */     int numDatasetOutputs = getStepManager().numOutgoingConnectionsOfType("dataSet");
/* 230:    */     
/* 231:329 */     int numInstanceOutputs = getStepManager().numOutgoingConnectionsOfType("instance");
/* 232:332 */     if ((numDatasetOutputs == 0) && (numInstanceOutputs == 0))
/* 233:    */     {
/* 234:333 */       outgoing.add("dataSet");
/* 235:334 */       outgoing.add("instance");
/* 236:    */     }
/* 237:335 */     else if (numDatasetOutputs > 0)
/* 238:    */     {
/* 239:336 */       outgoing.add("dataSet");
/* 240:    */     }
/* 241:337 */     else if (numInstanceOutputs > 0)
/* 242:    */     {
/* 243:338 */       outgoing.add("instance");
/* 244:    */     }
/* 245:341 */     return outgoing;
/* 246:    */   }
/* 247:    */   
/* 248:    */   public String getCustomEditorForStep()
/* 249:    */   {
/* 250:354 */     return "weka.gui.knowledgeflow.steps.LoaderStepEditorDialog";
/* 251:    */   }
/* 252:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.Loader
 * JD-Core Version:    0.7.0.1
 */