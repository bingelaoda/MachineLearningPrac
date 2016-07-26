/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.io.BufferedInputStream;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.FileInputStream;
/*   6:    */ import java.io.ObjectInputStream;
/*   7:    */ import java.util.ArrayList;
/*   8:    */ import java.util.List;
/*   9:    */ import weka.clusterers.AbstractClusterer;
/*  10:    */ import weka.core.Drawable;
/*  11:    */ import weka.core.EnvironmentHandler;
/*  12:    */ import weka.core.Instance;
/*  13:    */ import weka.core.Instances;
/*  14:    */ import weka.core.OptionHandler;
/*  15:    */ import weka.core.OptionMetadata;
/*  16:    */ import weka.core.Utils;
/*  17:    */ import weka.core.WekaException;
/*  18:    */ import weka.gui.FilePropertyMetadata;
/*  19:    */ import weka.gui.ProgrammaticProperty;
/*  20:    */ import weka.knowledgeflow.Data;
/*  21:    */ import weka.knowledgeflow.ExecutionEnvironment;
/*  22:    */ import weka.knowledgeflow.StepManager;
/*  23:    */ 
/*  24:    */ @KFStep(name="Clusterer", category="Clusterers", toolTipText="Weka clusterer wrapper", iconPath="", resourceIntensive=true)
/*  25:    */ public class Clusterer
/*  26:    */   extends WekaAlgorithmWrapper
/*  27:    */   implements PairedDataHelper.PairedProcessor<weka.clusterers.Clusterer>
/*  28:    */ {
/*  29:    */   private static final long serialVersionUID = 3275754421525338036L;
/*  30:    */   protected weka.clusterers.Clusterer m_clustererTemplate;
/*  31:    */   protected weka.clusterers.Clusterer m_trainedClusterer;
/*  32:    */   protected Instances m_trainedClustererHeader;
/*  33:    */   protected transient PairedDataHelper<weka.clusterers.Clusterer> m_trainTestHelper;
/*  34: 79 */   protected File m_loadModelFileName = new File("");
/*  35:    */   protected boolean m_isReset;
/*  36:    */   protected Data m_incrementalData;
/*  37:    */   protected boolean m_streaming;
/*  38:    */   
/*  39:    */   public weka.clusterers.Clusterer getClusterer()
/*  40:    */   {
/*  41: 96 */     return (weka.clusterers.Clusterer)getWrappedAlgorithm();
/*  42:    */   }
/*  43:    */   
/*  44:    */   @ProgrammaticProperty
/*  45:    */   public void setClusterer(weka.clusterers.Clusterer clusterer)
/*  46:    */   {
/*  47:106 */     setWrappedAlgorithm(clusterer);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public File getLoadClustererFileName()
/*  51:    */   {
/*  52:117 */     return this.m_loadModelFileName;
/*  53:    */   }
/*  54:    */   
/*  55:    */   @OptionMetadata(displayName="Clusterer model to load", description="Optional path to a clusterer to load at execution time (only applies when using testSet connections)")
/*  56:    */   @FilePropertyMetadata(fileChooserDialogType=0, directoriesOnly=false)
/*  57:    */   public void setLoadClustererFileName(File filename)
/*  58:    */   {
/*  59:135 */     this.m_loadModelFileName = filename;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public Class getWrappedAlgorithmClass()
/*  63:    */   {
/*  64:145 */     return weka.clusterers.Clusterer.class;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void setWrappedAlgorithm(Object algo)
/*  68:    */   {
/*  69:155 */     super.setWrappedAlgorithm(algo);
/*  70:156 */     this.m_defaultIconPath = "weka/gui/knowledgeflow/icons/DefaultClusterer.gif";
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void stepInit()
/*  74:    */     throws WekaException
/*  75:    */   {
/*  76:166 */     if (!(getWrappedAlgorithm() instanceof weka.clusterers.Clusterer)) {
/*  77:167 */       throw new WekaException("Incorrect type of algorithm");
/*  78:    */     }
/*  79:    */     try
/*  80:    */     {
/*  81:171 */       this.m_clustererTemplate = AbstractClusterer.makeCopy((weka.clusterers.Clusterer)getWrappedAlgorithm());
/*  82:175 */       if ((this.m_clustererTemplate instanceof EnvironmentHandler)) {
/*  83:176 */         ((EnvironmentHandler)this.m_clustererTemplate).setEnvironment(getStepManager().getExecutionEnvironment().getEnvironmentVariables());
/*  84:    */       }
/*  85:    */     }
/*  86:    */     catch (Exception ex)
/*  87:    */     {
/*  88:181 */       throw new WekaException(ex);
/*  89:    */     }
/*  90:185 */     if (getStepManager().numIncomingConnectionsOfType("trainingSet") > 0) {
/*  91:187 */       this.m_trainTestHelper = new PairedDataHelper(this, this, "trainingSet", getStepManager().numIncomingConnectionsOfType("testSet") > 0 ? "testSet" : null);
/*  92:    */     }
/*  93:197 */     this.m_isReset = true;
/*  94:198 */     this.m_streaming = false;
/*  95:199 */     this.m_incrementalData = new Data("incrementalClusterer");
/*  96:201 */     if ((getLoadClustererFileName() != null) && (getLoadClustererFileName().toString().length() > 0) && (getStepManager().numIncomingConnectionsOfType("trainingSet") == 0))
/*  97:    */     {
/*  98:205 */       String resolvedFileName = getStepManager().environmentSubstitute(getLoadClustererFileName().toString());
/*  99:    */       try
/* 100:    */       {
/* 101:209 */         loadModel(resolvedFileName);
/* 102:    */       }
/* 103:    */       catch (Exception ex)
/* 104:    */       {
/* 105:211 */         throw new WekaException(ex);
/* 106:    */       }
/* 107:    */     }
/* 108:    */   }
/* 109:    */   
/* 110:    */   public void processIncoming(Data data)
/* 111:    */     throws WekaException
/* 112:    */   {
/* 113:    */     try
/* 114:    */     {
/* 115:225 */       if (this.m_isReset)
/* 116:    */       {
/* 117:226 */         this.m_isReset = false;
/* 118:227 */         getStepManager().processing();
/* 119:228 */         Instances incomingStructure = null;
/* 120:229 */         if (data.getConnectionName().equals("instance")) {
/* 121:230 */           incomingStructure = ((Instance)data.getPayloadElement("instance")).dataset();
/* 122:    */         } else {
/* 123:234 */           incomingStructure = (Instances)data.getPayloadElement(data.getConnectionName());
/* 124:    */         }
/* 125:238 */         if (data.getConnectionName().equals("instance"))
/* 126:    */         {
/* 127:239 */           this.m_streaming = true;
/* 128:240 */           if (this.m_trainedClusterer == null)
/* 129:    */           {
/* 130:241 */             this.m_trainedClusterer = AbstractClusterer.makeCopy(this.m_clustererTemplate);
/* 131:243 */             if ((this.m_trainedClusterer instanceof EnvironmentHandler)) {
/* 132:244 */               ((EnvironmentHandler)this.m_trainedClusterer).setEnvironment(getStepManager().getExecutionEnvironment().getEnvironmentVariables());
/* 133:    */             }
/* 134:    */           }
/* 135:    */         }
/* 136:250 */         else if (data.getConnectionName().equals("trainingSet"))
/* 137:    */         {
/* 138:251 */           this.m_trainedClustererHeader = incomingStructure;
/* 139:    */         }
/* 140:254 */         if ((this.m_trainedClustererHeader != null) && (!incomingStructure.equalHeaders(this.m_trainedClustererHeader))) {
/* 141:256 */           throw new WekaException("Structure of incoming data does not match that of the trained clusterer");
/* 142:    */         }
/* 143:    */       }
/* 144:261 */       if (!this.m_streaming) {
/* 145:263 */         if (this.m_trainTestHelper != null) {
/* 146:265 */           this.m_trainTestHelper.process(data);
/* 147:    */         } else {
/* 148:267 */           processOnlyTestSet(data);
/* 149:    */         }
/* 150:    */       }
/* 151:    */     }
/* 152:    */     catch (Exception ex)
/* 153:    */     {
/* 154:270 */       throw new WekaException(ex);
/* 155:    */     }
/* 156:    */   }
/* 157:    */   
/* 158:    */   protected void processOnlyTestSet(Data data)
/* 159:    */     throws WekaException
/* 160:    */   {
/* 161:    */     try
/* 162:    */     {
/* 163:283 */       weka.clusterers.Clusterer tempToTest = AbstractClusterer.makeCopy(this.m_trainedClusterer);
/* 164:    */       
/* 165:285 */       Data batchClusterer = new Data("batchClusterer", tempToTest);
/* 166:    */       
/* 167:287 */       batchClusterer.setPayloadElement("aux_testsSet", data.getPayloadElement("aux_testsSet"));
/* 168:    */       
/* 169:289 */       batchClusterer.setPayloadElement("aux_set_num", data.getPayloadElement("aux_set_num", Integer.valueOf(1)));
/* 170:    */       
/* 171:291 */       batchClusterer.setPayloadElement("aux_max_set_num", data.getPayloadElement("aux_max_set_num", Integer.valueOf(1)));
/* 172:    */       
/* 173:293 */       getStepManager().outputData(new Data[] { batchClusterer });
/* 174:294 */       if (isStopRequested()) {
/* 175:295 */         getStepManager().interrupted();
/* 176:    */       } else {
/* 177:297 */         getStepManager().finished();
/* 178:    */       }
/* 179:    */     }
/* 180:    */     catch (Exception ex)
/* 181:    */     {
/* 182:300 */       throw new WekaException(ex);
/* 183:    */     }
/* 184:    */   }
/* 185:    */   
/* 186:    */   public List<String> getIncomingConnectionTypes()
/* 187:    */   {
/* 188:312 */     List<String> result = new ArrayList();
/* 189:313 */     int numTraining = getStepManager().numIncomingConnectionsOfType("trainingSet");
/* 190:    */     
/* 191:    */ 
/* 192:316 */     int numTesting = getStepManager().numIncomingConnectionsOfType("testSet");
/* 193:    */     
/* 194:    */ 
/* 195:319 */     int numInstance = getStepManager().numIncomingConnectionsOfType("instance");
/* 196:322 */     if (numTraining == 0) {
/* 197:323 */       result.add("trainingSet");
/* 198:    */     }
/* 199:326 */     if (numTesting == 0) {
/* 200:327 */       result.add("testSet");
/* 201:    */     }
/* 202:331 */     if ((numTraining == 0) && (numTesting == 0)) {
/* 203:332 */       result.add("instance");
/* 204:    */     }
/* 205:335 */     return result;
/* 206:    */   }
/* 207:    */   
/* 208:    */   public List<String> getOutgoingConnectionTypes()
/* 209:    */   {
/* 210:347 */     int numTraining = getStepManager().numIncomingConnectionsOfType("trainingSet");
/* 211:    */     
/* 212:    */ 
/* 213:350 */     int numTesting = getStepManager().numIncomingConnectionsOfType("testSet");
/* 214:    */     
/* 215:    */ 
/* 216:353 */     List<String> result = new ArrayList();
/* 217:354 */     if ((numTraining > 0) || (numTesting > 0)) {
/* 218:355 */       result.add("batchClusterer");
/* 219:    */     }
/* 220:358 */     result.add("text");
/* 221:360 */     if (((getClusterer() instanceof Drawable)) && (numTraining > 0)) {
/* 222:361 */       result.add("graph");
/* 223:    */     }
/* 224:366 */     result.add("info");
/* 225:    */     
/* 226:368 */     return result;
/* 227:    */   }
/* 228:    */   
/* 229:    */   protected void loadModel(String filePath)
/* 230:    */     throws Exception
/* 231:    */   {
/* 232:378 */     ObjectInputStream is = null;
/* 233:    */     try
/* 234:    */     {
/* 235:380 */       is = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(filePath))));
/* 236:    */       
/* 237:    */ 
/* 238:    */ 
/* 239:384 */       this.m_trainedClusterer = ((weka.clusterers.Clusterer)is.readObject());
/* 240:    */       try
/* 241:    */       {
/* 242:388 */         this.m_trainedClustererHeader = ((Instances)is.readObject());
/* 243:    */       }
/* 244:    */       catch (Exception ex)
/* 245:    */       {
/* 246:390 */         getStepManager().logWarning("Model file '" + filePath + "' does not seem to contain an Instances header");
/* 247:    */       }
/* 248:    */     }
/* 249:    */     finally
/* 250:    */     {
/* 251:395 */       if (is != null) {
/* 252:396 */         is.close();
/* 253:    */       }
/* 254:    */     }
/* 255:    */   }
/* 256:    */   
/* 257:    */   protected void outputGraphData(weka.clusterers.Clusterer clusterer, int setNum)
/* 258:    */     throws WekaException
/* 259:    */   {
/* 260:412 */     if ((clusterer instanceof Drawable))
/* 261:    */     {
/* 262:413 */       if (getStepManager().numOutgoingConnectionsOfType("graph") == 0) {
/* 263:414 */         return;
/* 264:    */       }
/* 265:    */       try
/* 266:    */       {
/* 267:418 */         String graphString = ((Drawable)clusterer).graph();
/* 268:419 */         int graphType = ((Drawable)clusterer).graphType();
/* 269:420 */         String grphTitle = clusterer.getClass().getCanonicalName();
/* 270:421 */         grphTitle = grphTitle.substring(grphTitle.lastIndexOf('.') + 1, grphTitle.length());
/* 271:    */         
/* 272:    */ 
/* 273:424 */         grphTitle = "Set " + setNum + " (" + this.m_trainedClustererHeader.relationName() + ") " + grphTitle;
/* 274:    */         
/* 275:    */ 
/* 276:427 */         Data graphData = new Data("graph");
/* 277:428 */         graphData.setPayloadElement("graph", graphString);
/* 278:429 */         graphData.setPayloadElement("graph_title", grphTitle);
/* 279:    */         
/* 280:431 */         graphData.setPayloadElement("graph_type", Integer.valueOf(graphType));
/* 281:    */         
/* 282:433 */         getStepManager().outputData(new Data[] { graphData });
/* 283:    */       }
/* 284:    */       catch (Exception ex)
/* 285:    */       {
/* 286:435 */         throw new WekaException(ex);
/* 287:    */       }
/* 288:    */     }
/* 289:    */   }
/* 290:    */   
/* 291:    */   protected void outputTextData(weka.clusterers.Clusterer clusterer, int setNum)
/* 292:    */     throws WekaException
/* 293:    */   {
/* 294:452 */     if (getStepManager().numOutgoingConnectionsOfType("text") == 0) {
/* 295:453 */       return;
/* 296:    */     }
/* 297:456 */     Data textData = new Data("text");
/* 298:    */     
/* 299:458 */     String modelString = clusterer.toString();
/* 300:459 */     String titleString = clusterer.getClass().getName();
/* 301:    */     
/* 302:461 */     titleString = titleString.substring(titleString.lastIndexOf('.') + 1, titleString.length());
/* 303:    */     
/* 304:    */ 
/* 305:464 */     modelString = "=== Clusterer model ===\n\nScheme:   " + titleString + "\n" + "Relation: " + this.m_trainedClustererHeader.relationName() + "\n\n" + modelString;
/* 306:    */     
/* 307:    */ 
/* 308:    */ 
/* 309:468 */     titleString = "Model: " + titleString;
/* 310:    */     
/* 311:470 */     textData.setPayloadElement("text", modelString);
/* 312:471 */     textData.setPayloadElement("aux_textTitle", titleString);
/* 313:474 */     if (setNum != -1) {
/* 314:475 */       textData.setPayloadElement("aux_set_num", Integer.valueOf(setNum));
/* 315:    */     }
/* 316:478 */     getStepManager().outputData(new Data[] { textData });
/* 317:    */   }
/* 318:    */   
/* 319:    */   protected void outputBatchClusterer(weka.clusterers.Clusterer clusterer, int setNum, int maxSetNum, Instances trainingSplit, Instances testSplit)
/* 320:    */     throws WekaException
/* 321:    */   {
/* 322:494 */     Data batchClusterer = new Data("batchClusterer", clusterer);
/* 323:495 */     batchClusterer.setPayloadElement("aux_trainingSet", trainingSplit);
/* 324:497 */     if (testSplit != null) {
/* 325:498 */       batchClusterer.setPayloadElement("aux_testsSet", testSplit);
/* 326:    */     }
/* 327:501 */     batchClusterer.setPayloadElement("aux_set_num", Integer.valueOf(setNum));
/* 328:502 */     batchClusterer.setPayloadElement("aux_max_set_num", Integer.valueOf(maxSetNum));
/* 329:    */     
/* 330:504 */     batchClusterer.setPayloadElement("aux_label", getName());
/* 331:505 */     getStepManager().outputData(new Data[] { batchClusterer });
/* 332:    */   }
/* 333:    */   
/* 334:    */   public weka.clusterers.Clusterer processPrimary(Integer setNum, Integer maxSetNum, Data data, PairedDataHelper<weka.clusterers.Clusterer> helper)
/* 335:    */     throws WekaException
/* 336:    */   {
/* 337:522 */     Instances trainingData = (Instances)data.getPrimaryPayload();
/* 338:    */     try
/* 339:    */     {
/* 340:524 */       weka.clusterers.Clusterer clusterer = AbstractClusterer.makeCopy(this.m_clustererTemplate);
/* 341:    */       
/* 342:    */ 
/* 343:527 */       String clustererDesc = clusterer.getClass().getCanonicalName();
/* 344:528 */       clustererDesc = clustererDesc.substring(clustererDesc.lastIndexOf('.') + 1);
/* 345:530 */       if ((clusterer instanceof OptionHandler))
/* 346:    */       {
/* 347:531 */         String optsString = Utils.joinOptions(((OptionHandler)clusterer).getOptions());
/* 348:    */         
/* 349:533 */         clustererDesc = clustererDesc + " " + optsString;
/* 350:    */       }
/* 351:536 */       if ((clusterer instanceof EnvironmentHandler)) {
/* 352:537 */         ((EnvironmentHandler)clusterer).setEnvironment(getStepManager().getExecutionEnvironment().getEnvironmentVariables());
/* 353:    */       }
/* 354:542 */       helper.addIndexedValueToNamedStore("trainingSplits", setNum, trainingData);
/* 355:545 */       if (!isStopRequested())
/* 356:    */       {
/* 357:546 */         getStepManager().logBasic("Building " + clustererDesc + " on " + trainingData.relationName() + " for fold/set " + setNum + " out of " + maxSetNum);
/* 358:550 */         if (maxSetNum.intValue() == 1) {
/* 359:553 */           this.m_trainedClusterer = clusterer;
/* 360:    */         }
/* 361:556 */         clusterer.buildClusterer(trainingData);
/* 362:    */         
/* 363:558 */         getStepManager().logDetailed("Finished building " + clustererDesc + "on " + trainingData.relationName() + " for fold/set " + setNum + " out of " + maxSetNum);
/* 364:    */         
/* 365:    */ 
/* 366:    */ 
/* 367:    */ 
/* 368:563 */         outputTextData(clusterer, setNum.intValue());
/* 369:564 */         outputGraphData(clusterer, setNum.intValue());
/* 370:566 */         if (getStepManager().numIncomingConnectionsOfType("testSet") == 0) {
/* 371:569 */           outputBatchClusterer(clusterer, setNum.intValue(), maxSetNum.intValue(), trainingData, null);
/* 372:    */         }
/* 373:    */       }
/* 374:572 */       return clusterer;
/* 375:    */     }
/* 376:    */     catch (Exception ex)
/* 377:    */     {
/* 378:574 */       throw new WekaException(ex);
/* 379:    */     }
/* 380:    */   }
/* 381:    */   
/* 382:    */   public void processSecondary(Integer setNum, Integer maxSetNum, Data data, PairedDataHelper<weka.clusterers.Clusterer> helper)
/* 383:    */     throws WekaException
/* 384:    */   {
/* 385:592 */     weka.clusterers.Clusterer clusterer = (weka.clusterers.Clusterer)helper.getIndexedPrimaryResult(setNum.intValue());
/* 386:    */     
/* 387:    */ 
/* 388:    */ 
/* 389:596 */     Instances testSplit = (Instances)data.getPrimaryPayload();
/* 390:    */     
/* 391:    */ 
/* 392:599 */     Instances trainingSplit = (Instances)helper.getIndexedValueFromNamedStore("trainingSplits", setNum);
/* 393:    */     
/* 394:    */ 
/* 395:602 */     getStepManager().logBasic("Dispatching model for set " + setNum + " out of " + maxSetNum + " to output");
/* 396:    */     
/* 397:    */ 
/* 398:    */ 
/* 399:606 */     outputBatchClusterer(clusterer, setNum.intValue(), maxSetNum.intValue(), trainingSplit, testSplit);
/* 400:    */   }
/* 401:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.Clusterer
 * JD-Core Version:    0.7.0.1
 */