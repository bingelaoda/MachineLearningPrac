/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.io.BufferedOutputStream;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.FileOutputStream;
/*   6:    */ import java.io.ObjectOutputStream;
/*   7:    */ import java.util.ArrayList;
/*   8:    */ import java.util.List;
/*   9:    */ import weka.classifiers.Classifier;
/*  10:    */ import weka.classifiers.UpdateableBatchProcessor;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.OptionMetadata;
/*  14:    */ import weka.core.WekaException;
/*  15:    */ import weka.gui.FilePropertyMetadata;
/*  16:    */ import weka.knowledgeflow.Data;
/*  17:    */ import weka.knowledgeflow.StepManager;
/*  18:    */ 
/*  19:    */ @KFStep(name="SerializedModelSaver", category="DataSinks", toolTipText="A step that saves models to the file system", iconPath="weka/gui/knowledgeflow/icons/SerializedModelSaver.gif")
/*  20:    */ public class SerializedModelSaver
/*  21:    */   extends BaseStep
/*  22:    */ {
/*  23:    */   private static final long serialVersionUID = -8343162241983197708L;
/*  24:    */   protected Instances m_incrementalHeader;
/*  25:    */   protected int m_incrementalSaveSchedule;
/*  26:    */   protected boolean m_includeRelationName;
/*  27: 73 */   private String m_filenamePrefix = "";
/*  28: 78 */   private File m_directory = new File(System.getProperty("user.dir"));
/*  29:    */   protected int m_counter;
/*  30:    */   
/*  31:    */   @FilePropertyMetadata(fileChooserDialogType=1, directoriesOnly=true)
/*  32:    */   @OptionMetadata(displayName="Output directory", description="The directory to save models to", displayOrder=0)
/*  33:    */   public void setOutputDirectory(File directory)
/*  34:    */   {
/*  35: 93 */     this.m_directory = directory;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public File getOutputDirectory()
/*  39:    */   {
/*  40:102 */     return this.m_directory;
/*  41:    */   }
/*  42:    */   
/*  43:    */   @OptionMetadata(displayName="Filename prefix", description="A prefix to prepend to the filename", displayOrder=1)
/*  44:    */   public void setFilenamePrefix(String filenamePrefix)
/*  45:    */   {
/*  46:113 */     this.m_filenamePrefix = filenamePrefix;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public String getFilenamePrefix()
/*  50:    */   {
/*  51:122 */     return this.m_filenamePrefix;
/*  52:    */   }
/*  53:    */   
/*  54:    */   @OptionMetadata(displayName="Incremental save schedule", description="How frequently to save incremental classifiers (<= 0 indicates that the save will happen just once, at the end of the stream", displayOrder=4)
/*  55:    */   public void setIncrementalSaveSchedule(int schedule)
/*  56:    */   {
/*  57:137 */     this.m_incrementalSaveSchedule = schedule;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public int getIncrementalSaveSchedule()
/*  61:    */   {
/*  62:147 */     return this.m_incrementalSaveSchedule;
/*  63:    */   }
/*  64:    */   
/*  65:    */   @OptionMetadata(displayName="Include relation name in file name", description="Whether to include the relation name of the data as part of the file name", displayOrder=2)
/*  66:    */   public void setIncludeRelationNameInFilename(boolean includeRelationName)
/*  67:    */   {
/*  68:162 */     this.m_includeRelationName = includeRelationName;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public boolean getIncludeRelationNameInFilename()
/*  72:    */   {
/*  73:171 */     return this.m_includeRelationName;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public List<String> getIncomingConnectionTypes()
/*  77:    */   {
/*  78:185 */     List<String> result = new ArrayList();
/*  79:186 */     result.add("batchClassifier");
/*  80:187 */     result.add("incrementalClassifier");
/*  81:188 */     result.add("batchClusterer");
/*  82:189 */     result.add("batchAssociator");
/*  83:    */     
/*  84:191 */     return result;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public List<String> getOutgoingConnectionTypes()
/*  88:    */   {
/*  89:205 */     return new ArrayList();
/*  90:    */   }
/*  91:    */   
/*  92:    */   public void stepInit()
/*  93:    */   {
/*  94:213 */     this.m_incrementalHeader = null;
/*  95:214 */     this.m_counter = 0;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public void processIncoming(Data data)
/*  99:    */     throws WekaException
/* 100:    */   {
/* 101:225 */     Object modelToSave = null;
/* 102:226 */     Instances modelHeader = null;
/* 103:227 */     Integer setNum = null;
/* 104:228 */     Integer maxSetNum = null;
/* 105:230 */     if (data.getConnectionName().equals("incrementalClassifier"))
/* 106:    */     {
/* 107:231 */       if ((this.m_incrementalHeader == null) && (!getStepManager().isStreamFinished(data))) {
/* 108:233 */         this.m_incrementalHeader = ((Instance)data.getPayloadElement("aux_testInstance")).dataset();
/* 109:    */       }
/* 110:238 */       if ((getStepManager().isStreamFinished(data)) || ((this.m_incrementalSaveSchedule > 0) && (this.m_counter % this.m_incrementalSaveSchedule == 0) && (this.m_counter > 0))) {
/* 111:241 */         modelToSave = (Classifier)data.getPayloadElement("incrementalClassifier");
/* 112:    */       }
/* 113:    */     }
/* 114:    */     else
/* 115:    */     {
/* 116:246 */       modelToSave = data.getPayloadElement(data.getConnectionName());
/* 117:247 */       modelHeader = (Instances)data.getPayloadElement("aux_trainingSet");
/* 118:    */       
/* 119:    */ 
/* 120:250 */       setNum = (Integer)data.getPayloadElement("aux_set_num");
/* 121:    */       
/* 122:252 */       maxSetNum = (Integer)data.getPayloadElement("aux_max_set_num");
/* 123:254 */       if (modelHeader == null) {
/* 124:255 */         modelHeader = (Instances)data.getPayloadElement("aux_testsSet");
/* 125:    */       }
/* 126:    */     }
/* 127:260 */     if (modelToSave != null)
/* 128:    */     {
/* 129:261 */       if ((modelToSave instanceof UpdateableBatchProcessor)) {
/* 130:    */         try
/* 131:    */         {
/* 132:264 */           ((UpdateableBatchProcessor)modelToSave).batchFinished();
/* 133:    */         }
/* 134:    */         catch (Exception ex)
/* 135:    */         {
/* 136:266 */           throw new WekaException(ex);
/* 137:    */         }
/* 138:    */       }
/* 139:270 */       if (modelHeader != null) {
/* 140:271 */         modelHeader = new Instances(modelHeader, 0);
/* 141:    */       }
/* 142:274 */       getStepManager().processing();
/* 143:275 */       String prefix = getStepManager().environmentSubstitute(this.m_filenamePrefix);
/* 144:276 */       String relationName = (this.m_includeRelationName) && (modelHeader != null) ? modelHeader.relationName() : "";
/* 145:    */       
/* 146:    */ 
/* 147:279 */       String setSpec = (maxSetNum != null) && (setNum != null) ? "_" + setNum + "_" + maxSetNum + "_" : "";
/* 148:    */       
/* 149:    */ 
/* 150:    */ 
/* 151:283 */       String modelName = modelToSave.getClass().getCanonicalName();
/* 152:284 */       modelName = modelName.substring(modelName.lastIndexOf(".") + 1, modelName.length());
/* 153:    */       
/* 154:286 */       String filename = "" + prefix + relationName + setSpec + modelName;
/* 155:287 */       filename = sanitizeFilename(filename);
/* 156:    */       
/* 157:289 */       String dirName = getStepManager().environmentSubstitute(this.m_directory.toString());
/* 158:    */       
/* 159:291 */       File tempFile = new File(dirName);
/* 160:292 */       filename = tempFile.getAbsolutePath() + File.separator + filename;
/* 161:    */       
/* 162:294 */       getStepManager().logBasic("Saving model " + modelToSave.getClass().getCanonicalName() + " to " + filename + ".model");
/* 163:    */       
/* 164:    */ 
/* 165:297 */       getStepManager().statusMessage("Saving model: " + modelToSave.getClass().getCanonicalName());
/* 166:    */       
/* 167:    */ 
/* 168:300 */       ObjectOutputStream oos = null;
/* 169:    */       try
/* 170:    */       {
/* 171:302 */         oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new File(filename + ".model"))));
/* 172:    */         
/* 173:    */ 
/* 174:305 */         oos.writeObject(modelToSave);
/* 175:306 */         if (modelHeader != null) {
/* 176:307 */           oos.writeObject(modelHeader);
/* 177:    */         }
/* 178:309 */         oos.close();
/* 179:313 */         if ((data.getConnectionName() != "incrementalClassifier") || (getStepManager().isStreamFinished(data))) {
/* 180:315 */           getStepManager().finished();
/* 181:    */         }
/* 182:317 */         if (oos != null) {
/* 183:    */           try
/* 184:    */           {
/* 185:319 */             oos.close();
/* 186:    */           }
/* 187:    */           catch (Exception ex)
/* 188:    */           {
/* 189:321 */             throw new WekaException(ex);
/* 190:    */           }
/* 191:    */         }
/* 192:327 */         this.m_counter += 1;
/* 193:    */       }
/* 194:    */       catch (Exception ex)
/* 195:    */       {
/* 196:311 */         throw new WekaException(ex);
/* 197:    */       }
/* 198:    */       finally
/* 199:    */       {
/* 200:313 */         if ((data.getConnectionName() != "incrementalClassifier") || (getStepManager().isStreamFinished(data))) {
/* 201:315 */           getStepManager().finished();
/* 202:    */         }
/* 203:317 */         if (oos != null) {
/* 204:    */           try
/* 205:    */           {
/* 206:319 */             oos.close();
/* 207:    */           }
/* 208:    */           catch (Exception ex)
/* 209:    */           {
/* 210:321 */             throw new WekaException(ex);
/* 211:    */           }
/* 212:    */         }
/* 213:    */       }
/* 214:    */     }
/* 215:    */   }
/* 216:    */   
/* 217:    */   protected static String sanitizeFilename(String filename)
/* 218:    */   {
/* 219:338 */     return filename.replaceAll("\\\\", "_").replaceAll(":", "_").replaceAll("/", "_");
/* 220:    */   }
/* 221:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.SerializedModelSaver
 * JD-Core Version:    0.7.0.1
 */