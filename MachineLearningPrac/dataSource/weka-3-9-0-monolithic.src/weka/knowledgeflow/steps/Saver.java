/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Arrays;
/*   6:    */ import java.util.List;
/*   7:    */ import weka.core.EnvironmentHandler;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.SerializedObject;
/*  11:    */ import weka.core.Utils;
/*  12:    */ import weka.core.WekaException;
/*  13:    */ import weka.core.converters.DatabaseConverter;
/*  14:    */ import weka.core.converters.DatabaseSaver;
/*  15:    */ import weka.gui.ProgrammaticProperty;
/*  16:    */ import weka.knowledgeflow.Data;
/*  17:    */ import weka.knowledgeflow.ExecutionEnvironment;
/*  18:    */ import weka.knowledgeflow.StepManager;
/*  19:    */ 
/*  20:    */ @KFStep(name="Saver", category="DataSinks", toolTipText="Weka saver wrapper", iconPath="")
/*  21:    */ public class Saver
/*  22:    */   extends WekaAlgorithmWrapper
/*  23:    */   implements Serializable
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = 6831606284211403465L;
/*  26:    */   protected Instances m_structure;
/*  27:    */   protected weka.core.converters.Saver m_saver;
/*  28:    */   protected boolean m_isDBSaver;
/*  29: 72 */   private boolean m_relationNameForFilename = true;
/*  30:    */   
/*  31:    */   public Class getWrappedAlgorithmClass()
/*  32:    */   {
/*  33: 81 */     return weka.core.converters.Saver.class;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public void setWrappedAlgorithm(Object algo)
/*  37:    */   {
/*  38: 91 */     super.setWrappedAlgorithm(algo);
/*  39: 92 */     this.m_defaultIconPath = "weka/gui/knowledgeflow/icons/DefaultDataSink.gif";
/*  40:    */   }
/*  41:    */   
/*  42:    */   public weka.core.converters.Saver getSaver()
/*  43:    */   {
/*  44:102 */     return (weka.core.converters.Saver)getWrappedAlgorithm();
/*  45:    */   }
/*  46:    */   
/*  47:    */   @ProgrammaticProperty
/*  48:    */   public void setSaver(weka.core.converters.Saver saver)
/*  49:    */   {
/*  50:113 */     setWrappedAlgorithm(saver);
/*  51:    */   }
/*  52:    */   
/*  53:    */   public boolean getRelationNameForFilename()
/*  54:    */   {
/*  55:122 */     return this.m_relationNameForFilename;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public void setRelationNameForFilename(boolean r)
/*  59:    */   {
/*  60:132 */     this.m_relationNameForFilename = r;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void stepInit()
/*  64:    */     throws WekaException
/*  65:    */   {
/*  66:142 */     this.m_saver = null;
/*  67:144 */     if (!(getWrappedAlgorithm() instanceof weka.core.converters.Saver)) {
/*  68:145 */       throw new WekaException("Incorrect type of algorithm");
/*  69:    */     }
/*  70:148 */     if ((getWrappedAlgorithm() instanceof DatabaseConverter)) {
/*  71:149 */       this.m_isDBSaver = true;
/*  72:    */     }
/*  73:152 */     int numNonInstanceInputs = getStepManager().numIncomingConnectionsOfType("dataSet") + getStepManager().numIncomingConnectionsOfType("trainingSet") + getStepManager().numIncomingConnectionsOfType("testSet");
/*  74:    */     
/*  75:    */ 
/*  76:    */ 
/*  77:    */ 
/*  78:    */ 
/*  79:    */ 
/*  80:159 */     int numInstanceInput = getStepManager().numIncomingConnectionsOfType("instance");
/*  81:162 */     if ((numNonInstanceInputs > 0) && (numInstanceInput > 0))
/*  82:    */     {
/*  83:163 */       WekaException cause = new WekaException("Can't have both instance and batch-based incomming connections!");
/*  84:    */       
/*  85:    */ 
/*  86:166 */       cause.fillInStackTrace();
/*  87:167 */       getStepManager().logError(cause.getMessage(), cause);
/*  88:168 */       throw new WekaException(cause);
/*  89:    */     }
/*  90:    */   }
/*  91:    */   
/*  92:    */   protected void saveBatch(Instances data, Integer setNum, Integer maxSetNum, String connectionName)
/*  93:    */     throws WekaException
/*  94:    */   {
/*  95:183 */     getStepManager().processing();
/*  96:    */     try
/*  97:    */     {
/*  98:186 */       weka.core.converters.Saver saver = (weka.core.converters.Saver)new SerializedObject(this.m_saver).getObject();
/*  99:188 */       if ((this.m_saver instanceof EnvironmentHandler)) {
/* 100:189 */         ((EnvironmentHandler)saver).setEnvironment(getStepManager().getExecutionEnvironment().getEnvironmentVariables());
/* 101:    */       }
/* 102:193 */       String fileName = sanitizeFilename(data.relationName());
/* 103:    */       
/* 104:195 */       String additional = (setNum != null) && (setNum.intValue() + maxSetNum.intValue() != 2) ? "_" + connectionName + "_" + setNum + "_of_" + maxSetNum : "";
/* 105:199 */       if (!this.m_isDBSaver)
/* 106:    */       {
/* 107:200 */         saver.setDirAndPrefix(fileName, additional);
/* 108:    */       }
/* 109:    */       else
/* 110:    */       {
/* 111:202 */         ((DatabaseSaver)saver).setTableName(fileName);
/* 112:203 */         ((DatabaseSaver)saver).setRelationForTableName(false);
/* 113:204 */         String setName = ((DatabaseSaver)saver).getTableName();
/* 114:205 */         setName = setName.replaceFirst("_" + connectionName + "_[0-9]+_of_[0-9]+", "");
/* 115:    */         
/* 116:207 */         ((DatabaseSaver)saver).setTableName(setName + additional);
/* 117:    */       }
/* 118:209 */       saver.setInstances(data);
/* 119:    */       
/* 120:211 */       getStepManager().logBasic("Saving " + data.relationName() + additional);
/* 121:212 */       getStepManager().statusMessage("Saving " + data.relationName() + additional);
/* 122:    */       
/* 123:214 */       saver.writeBatch();
/* 124:216 */       if (!isStopRequested())
/* 125:    */       {
/* 126:217 */         getStepManager().logBasic("Save successful");
/* 127:218 */         getStepManager().statusMessage("Finished.");
/* 128:    */       }
/* 129:    */       else
/* 130:    */       {
/* 131:220 */         getStepManager().interrupted();
/* 132:    */       }
/* 133:    */     }
/* 134:    */     catch (Exception ex)
/* 135:    */     {
/* 136:223 */       WekaException e = new WekaException(ex);
/* 137:    */       
/* 138:225 */       throw e;
/* 139:    */     }
/* 140:    */     finally
/* 141:    */     {
/* 142:227 */       getStepManager().finished();
/* 143:    */     }
/* 144:    */   }
/* 145:    */   
/* 146:    */   public synchronized void processIncoming(Data data)
/* 147:    */     throws WekaException
/* 148:    */   {
/* 149:240 */     if (this.m_saver == null)
/* 150:    */     {
/* 151:    */       try
/* 152:    */       {
/* 153:242 */         this.m_saver = ((weka.core.converters.Saver)new SerializedObject(getWrappedAlgorithm()).getObject());
/* 154:    */       }
/* 155:    */       catch (Exception ex)
/* 156:    */       {
/* 157:246 */         throw new WekaException(ex);
/* 158:    */       }
/* 159:249 */       if ((this.m_saver instanceof EnvironmentHandler)) {
/* 160:250 */         ((EnvironmentHandler)this.m_saver).setEnvironment(getStepManager().getExecutionEnvironment().getEnvironmentVariables());
/* 161:    */       }
/* 162:254 */       if (data.getConnectionName().equalsIgnoreCase("instance"))
/* 163:    */       {
/* 164:256 */         Instance forStructure = (Instance)data.getPayloadElement("instance");
/* 165:258 */         if (forStructure != null)
/* 166:    */         {
/* 167:260 */           this.m_saver.setRetrieval(2);
/* 168:261 */           String fileName = sanitizeFilename(forStructure.dataset().relationName());
/* 169:    */           try
/* 170:    */           {
/* 171:263 */             this.m_saver.setDirAndPrefix(fileName, "");
/* 172:    */           }
/* 173:    */           catch (Exception ex)
/* 174:    */           {
/* 175:265 */             throw new WekaException(ex);
/* 176:    */           }
/* 177:267 */           this.m_saver.setInstances(forStructure.dataset());
/* 178:269 */           if ((this.m_isDBSaver) && 
/* 179:270 */             (((DatabaseSaver)this.m_saver).getRelationForTableName()))
/* 180:    */           {
/* 181:271 */             ((DatabaseSaver)this.m_saver).setTableName(fileName);
/* 182:272 */             ((DatabaseSaver)this.m_saver).setRelationForTableName(false);
/* 183:    */           }
/* 184:    */         }
/* 185:    */       }
/* 186:    */     }
/* 187:279 */     if ((data.getConnectionName().equals("dataSet")) || (data.getConnectionName().equals("trainingSet")) || (data.getConnectionName().equals("testSet")))
/* 188:    */     {
/* 189:282 */       this.m_saver.setRetrieval(1);
/* 190:283 */       Instances theData = (Instances)data.getPayloadElement(data.getConnectionName());
/* 191:    */       
/* 192:285 */       Integer setNum = (Integer)data.getPayloadElement("aux_set_num");
/* 193:    */       
/* 194:287 */       Integer maxSetNum = (Integer)data.getPayloadElement("aux_max_set_num");
/* 195:    */       
/* 196:    */ 
/* 197:290 */       saveBatch(theData, setNum, maxSetNum, data.getConnectionName());
/* 198:    */       
/* 199:292 */       return;
/* 200:    */     }
/* 201:295 */     Instance toSave = (Instance)data.getPayloadElement("instance");
/* 202:    */     
/* 203:297 */     boolean streamEnd = getStepManager().isStreamFinished(data);
/* 204:    */     try
/* 205:    */     {
/* 206:299 */       if (streamEnd)
/* 207:    */       {
/* 208:300 */         this.m_saver.writeIncremental(null);
/* 209:301 */         getStepManager().throughputFinished(new Data[] { new Data("instance") });
/* 210:    */         
/* 211:303 */         return;
/* 212:    */       }
/* 213:306 */       if (!isStopRequested())
/* 214:    */       {
/* 215:307 */         getStepManager().throughputUpdateStart();
/* 216:308 */         this.m_saver.writeIncremental(toSave);
/* 217:    */       }
/* 218:    */       else
/* 219:    */       {
/* 220:311 */         this.m_saver.writeIncremental(null);
/* 221:    */       }
/* 222:313 */       getStepManager().throughputUpdateEnd();
/* 223:    */     }
/* 224:    */     catch (Exception ex)
/* 225:    */     {
/* 226:315 */       throw new WekaException(ex);
/* 227:    */     }
/* 228:    */   }
/* 229:    */   
/* 230:    */   public List<String> getIncomingConnectionTypes()
/* 231:    */   {
/* 232:328 */     int numInstance = getStepManager().getIncomingConnectedStepsOfConnectionType("instance").size();
/* 233:    */     
/* 234:    */ 
/* 235:    */ 
/* 236:332 */     int numNonInstance = getStepManager().getIncomingConnectedStepsOfConnectionType("dataSet").size() + getStepManager().getIncomingConnectedStepsOfConnectionType("trainingSet").size() + getStepManager().getIncomingConnectedStepsOfConnectionType("testSet").size();
/* 237:340 */     if (numInstance + numNonInstance == 0) {
/* 238:341 */       return Arrays.asList(new String[] { "dataSet", "trainingSet", "testSet", "instance" });
/* 239:    */     }
/* 240:346 */     return new ArrayList();
/* 241:    */   }
/* 242:    */   
/* 243:    */   public List<String> getOutgoingConnectionTypes()
/* 244:    */   {
/* 245:358 */     return new ArrayList();
/* 246:    */   }
/* 247:    */   
/* 248:    */   protected String sanitizeFilename(String filename)
/* 249:    */   {
/* 250:370 */     filename = filename.replaceAll("\\\\", "_").replaceAll(":", "_").replaceAll("/", "_");
/* 251:    */     
/* 252:    */ 
/* 253:373 */     filename = Utils.removeSubstring(filename, "weka.filters.supervised.instance.");
/* 254:    */     
/* 255:375 */     filename = Utils.removeSubstring(filename, "weka.filters.supervised.attribute.");
/* 256:    */     
/* 257:377 */     filename = Utils.removeSubstring(filename, "weka.filters.unsupervised.instance.");
/* 258:    */     
/* 259:379 */     filename = Utils.removeSubstring(filename, "weka.filters.unsupervised.attribute.");
/* 260:    */     
/* 261:381 */     filename = Utils.removeSubstring(filename, "weka.clusterers.");
/* 262:382 */     filename = Utils.removeSubstring(filename, "weka.associations.");
/* 263:383 */     filename = Utils.removeSubstring(filename, "weka.attributeSelection.");
/* 264:384 */     filename = Utils.removeSubstring(filename, "weka.estimators.");
/* 265:385 */     filename = Utils.removeSubstring(filename, "weka.datagenerators.");
/* 266:387 */     if ((!this.m_isDBSaver) && (!this.m_relationNameForFilename))
/* 267:    */     {
/* 268:388 */       filename = "";
/* 269:    */       try
/* 270:    */       {
/* 271:390 */         if (this.m_saver.filePrefix().equals("")) {
/* 272:391 */           this.m_saver.setFilePrefix("no-name");
/* 273:    */         }
/* 274:    */       }
/* 275:    */       catch (Exception ex)
/* 276:    */       {
/* 277:394 */         ex.printStackTrace();
/* 278:    */       }
/* 279:    */     }
/* 280:398 */     return filename;
/* 281:    */   }
/* 282:    */   
/* 283:    */   public String getCustomEditorForStep()
/* 284:    */   {
/* 285:411 */     return "weka.gui.knowledgeflow.steps.SaverStepEditorDialog";
/* 286:    */   }
/* 287:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.Saver
 * JD-Core Version:    0.7.0.1
 */