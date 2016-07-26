/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.io.BufferedInputStream;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.FileInputStream;
/*   6:    */ import java.io.ObjectInputStream;
/*   7:    */ import java.util.ArrayList;
/*   8:    */ import java.util.List;
/*   9:    */ import weka.classifiers.AbstractClassifier;
/*  10:    */ import weka.classifiers.UpdateableBatchProcessor;
/*  11:    */ import weka.classifiers.UpdateableClassifier;
/*  12:    */ import weka.classifiers.misc.InputMappedClassifier;
/*  13:    */ import weka.core.Drawable;
/*  14:    */ import weka.core.EnvironmentHandler;
/*  15:    */ import weka.core.Instance;
/*  16:    */ import weka.core.Instances;
/*  17:    */ import weka.core.OptionHandler;
/*  18:    */ import weka.core.OptionMetadata;
/*  19:    */ import weka.core.Utils;
/*  20:    */ import weka.core.WekaException;
/*  21:    */ import weka.gui.FilePropertyMetadata;
/*  22:    */ import weka.gui.ProgrammaticProperty;
/*  23:    */ import weka.knowledgeflow.Data;
/*  24:    */ import weka.knowledgeflow.ExecutionEnvironment;
/*  25:    */ import weka.knowledgeflow.LoggingLevel;
/*  26:    */ import weka.knowledgeflow.StepManager;
/*  27:    */ 
/*  28:    */ @KFStep(name="Classifier", category="Classifiers", toolTipText="Weka classifier wrapper", iconPath="", resourceIntensive=true)
/*  29:    */ public class Classifier
/*  30:    */   extends WekaAlgorithmWrapper
/*  31:    */   implements PairedDataHelper.PairedProcessor<weka.classifiers.Classifier>
/*  32:    */ {
/*  33:    */   private static final long serialVersionUID = 8326706942962123155L;
/*  34:    */   protected weka.classifiers.Classifier m_classifierTemplate;
/*  35:    */   protected weka.classifiers.Classifier m_trainedClassifier;
/*  36:    */   protected Instances m_trainedClassifierHeader;
/*  37: 88 */   protected File m_loadModelFileName = new File("");
/*  38:    */   protected boolean m_resetIncrementalClassifier;
/*  39:100 */   protected boolean m_updateIncrementalClassifier = true;
/*  40:    */   protected boolean m_streaming;
/*  41:    */   protected boolean m_classifierIsIncremental;
/*  42:    */   protected transient PairedDataHelper<weka.classifiers.Classifier> m_trainTestHelper;
/*  43:112 */   protected Data m_incrementalData = new Data("incrementalClassifier");
/*  44:    */   protected boolean m_isReset;
/*  45:    */   
/*  46:    */   public Class getWrappedAlgorithmClass()
/*  47:    */   {
/*  48:120 */     return weka.classifiers.Classifier.class;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void setWrappedAlgorithm(Object algo)
/*  52:    */   {
/*  53:125 */     super.setWrappedAlgorithm(algo);
/*  54:126 */     this.m_defaultIconPath = "weka/gui/knowledgeflow/icons/DefaultClassifier.gif";
/*  55:    */   }
/*  56:    */   
/*  57:    */   public weka.classifiers.Classifier getClassifier()
/*  58:    */   {
/*  59:135 */     return (weka.classifiers.Classifier)getWrappedAlgorithm();
/*  60:    */   }
/*  61:    */   
/*  62:    */   @ProgrammaticProperty
/*  63:    */   public void setClassifier(weka.classifiers.Classifier classifier)
/*  64:    */   {
/*  65:145 */     setWrappedAlgorithm(classifier);
/*  66:    */   }
/*  67:    */   
/*  68:    */   public void stepInit()
/*  69:    */     throws WekaException
/*  70:    */   {
/*  71:    */     try
/*  72:    */     {
/*  73:151 */       this.m_trainedClassifier = null;
/*  74:152 */       this.m_trainTestHelper = null;
/*  75:153 */       this.m_incrementalData = new Data("incrementalClassifier");
/*  76:154 */       this.m_classifierTemplate = AbstractClassifier.makeCopy((weka.classifiers.Classifier)getWrappedAlgorithm());
/*  77:158 */       if ((this.m_classifierTemplate instanceof EnvironmentHandler)) {
/*  78:159 */         ((EnvironmentHandler)this.m_classifierTemplate).setEnvironment(getStepManager().getExecutionEnvironment().getEnvironmentVariables());
/*  79:    */       }
/*  80:    */     }
/*  81:    */     catch (Exception ex)
/*  82:    */     {
/*  83:164 */       throw new WekaException(ex);
/*  84:    */     }
/*  85:168 */     if (getStepManager().numIncomingConnectionsOfType("trainingSet") > 0) {
/*  86:170 */       this.m_trainTestHelper = new PairedDataHelper(this, this, "trainingSet", getStepManager().numIncomingConnectionsOfType("testSet") > 0 ? "testSet" : null);
/*  87:    */     }
/*  88:180 */     this.m_isReset = true;
/*  89:181 */     this.m_classifierIsIncremental = (this.m_classifierTemplate instanceof UpdateableClassifier);
/*  90:184 */     if ((getLoadClassifierFileName() != null) && (getLoadClassifierFileName().toString().length() > 0) && (getStepManager().numIncomingConnectionsOfType("trainingSet") == 0))
/*  91:    */     {
/*  92:188 */       String resolvedFileName = getStepManager().environmentSubstitute(getLoadClassifierFileName().toString());
/*  93:    */       try
/*  94:    */       {
/*  95:192 */         getStepManager().logBasic("Loading classifier: " + resolvedFileName);
/*  96:193 */         loadModel(resolvedFileName);
/*  97:    */       }
/*  98:    */       catch (Exception ex)
/*  99:    */       {
/* 100:195 */         throw new WekaException(ex);
/* 101:    */       }
/* 102:    */     }
/* 103:199 */     if ((this.m_trainedClassifier != null) && (getStepManager().numIncomingConnectionsOfType("instance") > 0) && (!this.m_classifierIsIncremental)) {
/* 104:203 */       getStepManager().logWarning("Loaded classifier is not an incremental one - will only be able to evaluate, and not update, on the incoming instance stream.");
/* 105:    */     }
/* 106:    */   }
/* 107:    */   
/* 108:    */   public File getLoadClassifierFileName()
/* 109:    */   {
/* 110:218 */     return this.m_loadModelFileName;
/* 111:    */   }
/* 112:    */   
/* 113:    */   @OptionMetadata(displayName="Classifier model to load", description="Optional Path to a classifier to load at execution time (only applies when using testSet or instance connections)")
/* 114:    */   @FilePropertyMetadata(fileChooserDialogType=0, directoriesOnly=false)
/* 115:    */   public void setLoadClassifierFileName(File filename)
/* 116:    */   {
/* 117:236 */     this.m_loadModelFileName = filename;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public boolean getResetIncrementalClassifier()
/* 121:    */   {
/* 122:246 */     return this.m_resetIncrementalClassifier;
/* 123:    */   }
/* 124:    */   
/* 125:    */   @OptionMetadata(displayName="Reset incremental classifier", description="Reset classifier (if it is incremental) at the start of the incoming instance stream")
/* 126:    */   public void setResetIncrementalClassifier(boolean reset)
/* 127:    */   {
/* 128:261 */     this.m_resetIncrementalClassifier = reset;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public boolean getUpdateIncrementalClassifier()
/* 132:    */   {
/* 133:271 */     return this.m_updateIncrementalClassifier;
/* 134:    */   }
/* 135:    */   
/* 136:    */   @OptionMetadata(displayName="Update incremental classifier", description=" Update an incremental classifier on incoming instance stream")
/* 137:    */   public void setUpdateIncrementalClassifier(boolean update)
/* 138:    */   {
/* 139:285 */     this.m_updateIncrementalClassifier = true;
/* 140:    */   }
/* 141:    */   
/* 142:    */   public void processIncoming(Data data)
/* 143:    */     throws WekaException
/* 144:    */   {
/* 145:    */     try
/* 146:    */     {
/* 147:291 */       if (this.m_isReset)
/* 148:    */       {
/* 149:292 */         this.m_isReset = false;
/* 150:293 */         Instances incomingStructure = null;
/* 151:294 */         if (data.getConnectionName().equals("instance")) {
/* 152:295 */           incomingStructure = new Instances(((Instance)data.getPayloadElement("instance")).dataset(), 0);
/* 153:    */         } else {
/* 154:301 */           incomingStructure = (Instances)data.getPayloadElement(data.getConnectionName());
/* 155:    */         }
/* 156:304 */         if (incomingStructure.classAttribute() == null)
/* 157:    */         {
/* 158:305 */           getStepManager().logWarning("No class index is set in the data - using last attribute as class");
/* 159:    */           
/* 160:    */ 
/* 161:308 */           incomingStructure.setClassIndex(incomingStructure.numAttributes() - 1);
/* 162:    */         }
/* 163:312 */         if (data.getConnectionName().equals("instance"))
/* 164:    */         {
/* 165:313 */           this.m_streaming = true;
/* 166:314 */           if (this.m_trainedClassifier == null)
/* 167:    */           {
/* 168:315 */             this.m_trainedClassifier = AbstractClassifier.makeCopy(this.m_classifierTemplate);
/* 169:    */             
/* 170:    */ 
/* 171:318 */             getStepManager().logBasic("Initialising incremental classifier");
/* 172:319 */             this.m_trainedClassifier.buildClassifier(incomingStructure);
/* 173:320 */             this.m_trainedClassifierHeader = incomingStructure;
/* 174:    */           }
/* 175:321 */           else if ((this.m_resetIncrementalClassifier) && (this.m_classifierIsIncremental))
/* 176:    */           {
/* 177:324 */             this.m_trainedClassifier = AbstractClassifier.makeCopy(this.m_classifierTemplate);
/* 178:    */             
/* 179:    */ 
/* 180:327 */             this.m_trainedClassifierHeader = incomingStructure;
/* 181:328 */             getStepManager().logBasic("Resetting incremental classifier");
/* 182:329 */             this.m_trainedClassifier.buildClassifier(this.m_trainedClassifierHeader);
/* 183:    */           }
/* 184:332 */           getStepManager().logBasic((this.m_updateIncrementalClassifier) && (this.m_classifierIsIncremental) ? "Training incrementally" : "Predicting incrementally");
/* 185:    */         }
/* 186:336 */         else if (data.getConnectionName().equals("trainingSet"))
/* 187:    */         {
/* 188:337 */           this.m_trainedClassifierHeader = incomingStructure;
/* 189:    */         }
/* 190:340 */         if ((this.m_trainedClassifierHeader != null) && (!incomingStructure.equalHeaders(this.m_trainedClassifierHeader))) {
/* 191:342 */           if (!(this.m_trainedClassifier instanceof InputMappedClassifier)) {
/* 192:343 */             throw new WekaException("Structure of incoming data does not match that of the trained classifier");
/* 193:    */           }
/* 194:    */         }
/* 195:    */       }
/* 196:350 */       if (this.m_streaming) {
/* 197:351 */         processStreaming(data);
/* 198:352 */       } else if (this.m_trainTestHelper != null) {
/* 199:354 */         this.m_trainTestHelper.process(data);
/* 200:    */       } else {
/* 201:357 */         processOnlyTestSet(data);
/* 202:    */       }
/* 203:    */     }
/* 204:    */     catch (Exception ex)
/* 205:    */     {
/* 206:360 */       throw new WekaException(ex);
/* 207:    */     }
/* 208:    */   }
/* 209:    */   
/* 210:    */   public weka.classifiers.Classifier processPrimary(Integer setNum, Integer maxSetNum, Data data, PairedDataHelper<weka.classifiers.Classifier> helper)
/* 211:    */     throws WekaException
/* 212:    */   {
/* 213:379 */     Instances trainingData = (Instances)data.getPrimaryPayload();
/* 214:    */     try
/* 215:    */     {
/* 216:381 */       weka.classifiers.Classifier classifier = AbstractClassifier.makeCopy(this.m_classifierTemplate);
/* 217:    */       
/* 218:    */ 
/* 219:384 */       String classifierDesc = classifier.getClass().getCanonicalName();
/* 220:385 */       classifierDesc = classifierDesc.substring(classifierDesc.lastIndexOf(".") + 1);
/* 221:387 */       if ((classifier instanceof OptionHandler))
/* 222:    */       {
/* 223:388 */         String optsString = Utils.joinOptions(((OptionHandler)classifier).getOptions());
/* 224:    */         
/* 225:390 */         classifierDesc = classifierDesc + " " + optsString;
/* 226:    */       }
/* 227:392 */       if ((classifier instanceof EnvironmentHandler)) {
/* 228:393 */         ((EnvironmentHandler)classifier).setEnvironment(getStepManager().getExecutionEnvironment().getEnvironmentVariables());
/* 229:    */       }
/* 230:398 */       helper.addIndexedValueToNamedStore("trainingSplits", setNum, trainingData);
/* 231:400 */       if (!isStopRequested())
/* 232:    */       {
/* 233:401 */         getStepManager().logBasic("Building " + classifierDesc + " on " + trainingData.relationName() + " for fold/set " + setNum + " out of " + maxSetNum);
/* 234:404 */         if (getStepManager().getLoggingLevel().ordinal() > LoggingLevel.LOW.ordinal()) {
/* 235:406 */           getStepManager().statusMessage("Building " + classifierDesc + " on fold/set " + setNum);
/* 236:    */         }
/* 237:410 */         if (maxSetNum.intValue() == 1) {
/* 238:413 */           this.m_trainedClassifier = classifier;
/* 239:    */         }
/* 240:415 */         classifier.buildClassifier(trainingData);
/* 241:416 */         getStepManager().logDetailed("Finished building " + classifierDesc + "on " + trainingData.relationName() + " for fold/set " + setNum + " out of " + maxSetNum);
/* 242:    */         
/* 243:    */ 
/* 244:    */ 
/* 245:    */ 
/* 246:421 */         outputTextData(classifier, setNum.intValue());
/* 247:422 */         outputGraphData(classifier, setNum.intValue());
/* 248:424 */         if (getStepManager().numIncomingConnectionsOfType("testSet") == 0)
/* 249:    */         {
/* 250:427 */           Data batchClassifier = new Data("batchClassifier", classifier);
/* 251:    */           
/* 252:429 */           batchClassifier.setPayloadElement("aux_trainingSet", trainingData);
/* 253:    */           
/* 254:431 */           batchClassifier.setPayloadElement("aux_set_num", setNum);
/* 255:    */           
/* 256:433 */           batchClassifier.setPayloadElement("aux_max_set_num", maxSetNum);
/* 257:    */           
/* 258:435 */           batchClassifier.setPayloadElement("aux_label", getName());
/* 259:    */           
/* 260:437 */           getStepManager().outputData(new Data[] { batchClassifier });
/* 261:    */         }
/* 262:    */       }
/* 263:440 */       return classifier;
/* 264:    */     }
/* 265:    */     catch (Exception ex)
/* 266:    */     {
/* 267:442 */       throw new WekaException(ex);
/* 268:    */     }
/* 269:    */   }
/* 270:    */   
/* 271:    */   public void processSecondary(Integer setNum, Integer maxSetNum, Data data, PairedDataHelper<weka.classifiers.Classifier> helper)
/* 272:    */     throws WekaException
/* 273:    */   {
/* 274:460 */     weka.classifiers.Classifier classifier = (weka.classifiers.Classifier)helper.getIndexedPrimaryResult(setNum.intValue());
/* 275:    */     
/* 276:    */ 
/* 277:    */ 
/* 278:464 */     Instances testSplit = (Instances)data.getPrimaryPayload();
/* 279:    */     
/* 280:    */ 
/* 281:467 */     Instances trainingSplit = (Instances)helper.getIndexedValueFromNamedStore("trainingSplits", setNum);
/* 282:    */     
/* 283:    */ 
/* 284:470 */     getStepManager().logBasic("Dispatching model for set " + setNum + " out of " + maxSetNum + " to output");
/* 285:    */     
/* 286:    */ 
/* 287:    */ 
/* 288:474 */     Data batchClassifier = new Data("batchClassifier", classifier);
/* 289:    */     
/* 290:476 */     batchClassifier.setPayloadElement("aux_trainingSet", trainingSplit);
/* 291:    */     
/* 292:478 */     batchClassifier.setPayloadElement("aux_testsSet", testSplit);
/* 293:    */     
/* 294:480 */     batchClassifier.setPayloadElement("aux_set_num", setNum);
/* 295:481 */     batchClassifier.setPayloadElement("aux_max_set_num", maxSetNum);
/* 296:    */     
/* 297:483 */     batchClassifier.setPayloadElement("aux_label", getName());
/* 298:    */     
/* 299:485 */     getStepManager().outputData(new Data[] { batchClassifier });
/* 300:    */   }
/* 301:    */   
/* 302:    */   protected void processOnlyTestSet(Data data)
/* 303:    */     throws WekaException
/* 304:    */   {
/* 305:    */     try
/* 306:    */     {
/* 307:498 */       weka.classifiers.Classifier tempToTest = AbstractClassifier.makeCopy(this.m_trainedClassifier);
/* 308:    */       
/* 309:500 */       Data batchClassifier = new Data("batchClassifier");
/* 310:501 */       batchClassifier.setPayloadElement("batchClassifier", tempToTest);
/* 311:    */       
/* 312:503 */       batchClassifier.setPayloadElement("aux_testsSet", data.getPayloadElement("testSet"));
/* 313:    */       
/* 314:505 */       batchClassifier.setPayloadElement("aux_set_num", data.getPayloadElement("aux_set_num", Integer.valueOf(1)));
/* 315:    */       
/* 316:507 */       batchClassifier.setPayloadElement("aux_max_set_num", data.getPayloadElement("aux_max_set_num", Integer.valueOf(1)));
/* 317:    */       
/* 318:509 */       batchClassifier.setPayloadElement("aux_label", getName());
/* 319:    */       
/* 320:511 */       getStepManager().outputData(new Data[] { batchClassifier });
/* 321:512 */       if (isStopRequested()) {
/* 322:513 */         getStepManager().interrupted();
/* 323:    */       } else {
/* 324:515 */         getStepManager().finished();
/* 325:    */       }
/* 326:    */     }
/* 327:    */     catch (Exception ex)
/* 328:    */     {
/* 329:518 */       throw new WekaException(ex);
/* 330:    */     }
/* 331:    */   }
/* 332:    */   
/* 333:    */   protected void processStreaming(Data data)
/* 334:    */     throws WekaException
/* 335:    */   {
/* 336:530 */     if (isStopRequested()) {
/* 337:531 */       return;
/* 338:    */     }
/* 339:533 */     Instance inst = (Instance)data.getPayloadElement("instance");
/* 340:534 */     if (getStepManager().isStreamFinished(data))
/* 341:    */     {
/* 342:536 */       if ((this.m_trainedClassifier instanceof UpdateableBatchProcessor)) {
/* 343:    */         try
/* 344:    */         {
/* 345:538 */           ((UpdateableBatchProcessor)this.m_trainedClassifier).batchFinished();
/* 346:    */         }
/* 347:    */         catch (Exception ex)
/* 348:    */         {
/* 349:540 */           throw new WekaException(ex);
/* 350:    */         }
/* 351:    */       }
/* 352:546 */       this.m_incrementalData.setPayloadElement("incrementalClassifier", this.m_trainedClassifier);
/* 353:    */       
/* 354:548 */       this.m_incrementalData.setPayloadElement("aux_testInstance", null);
/* 355:    */       
/* 356:    */ 
/* 357:    */ 
/* 358:552 */       outputTextData(this.m_trainedClassifier, -1);
/* 359:553 */       outputGraphData(this.m_trainedClassifier, 0);
/* 360:555 */       if (!isStopRequested()) {
/* 361:556 */         getStepManager().throughputFinished(new Data[] { this.m_incrementalData });
/* 362:    */       }
/* 363:558 */       return;
/* 364:    */     }
/* 365:562 */     this.m_incrementalData.setPayloadElement("aux_testInstance", inst);
/* 366:    */     
/* 367:564 */     this.m_incrementalData.setPayloadElement("incrementalClassifier", this.m_trainedClassifier);
/* 368:    */     
/* 369:566 */     getStepManager().outputData(this.m_incrementalData.getConnectionName(), this.m_incrementalData);
/* 370:    */     
/* 371:    */ 
/* 372:    */ 
/* 373:570 */     getStepManager().throughputUpdateStart();
/* 374:571 */     if ((this.m_classifierIsIncremental) && (this.m_updateIncrementalClassifier) && 
/* 375:572 */       (!inst.classIsMissing())) {
/* 376:    */       try
/* 377:    */       {
/* 378:574 */         ((UpdateableClassifier)this.m_trainedClassifier).updateClassifier(inst);
/* 379:    */       }
/* 380:    */       catch (Exception ex)
/* 381:    */       {
/* 382:576 */         throw new WekaException(ex);
/* 383:    */       }
/* 384:    */     }
/* 385:580 */     getStepManager().throughputUpdateEnd();
/* 386:    */   }
/* 387:    */   
/* 388:    */   protected void outputTextData(weka.classifiers.Classifier classifier, int setNum)
/* 389:    */     throws WekaException
/* 390:    */   {
/* 391:594 */     if (getStepManager().numOutgoingConnectionsOfType("text") == 0) {
/* 392:595 */       return;
/* 393:    */     }
/* 394:598 */     Data textData = new Data("text");
/* 395:    */     
/* 396:600 */     String modelString = classifier.toString();
/* 397:601 */     String titleString = classifier.getClass().getName();
/* 398:    */     
/* 399:603 */     titleString = titleString.substring(titleString.lastIndexOf('.') + 1, titleString.length());
/* 400:    */     
/* 401:    */ 
/* 402:606 */     modelString = "=== Classifier model ===\n\nScheme:   " + titleString + "\n" + "Relation: " + this.m_trainedClassifierHeader.relationName() + "\n\n" + modelString;
/* 403:    */     
/* 404:    */ 
/* 405:    */ 
/* 406:610 */     titleString = "Model: " + titleString;
/* 407:    */     
/* 408:612 */     textData.setPayloadElement("text", modelString);
/* 409:613 */     textData.setPayloadElement("aux_textTitle", titleString);
/* 410:616 */     if (setNum != -1) {
/* 411:617 */       textData.setPayloadElement("aux_set_num", Integer.valueOf(setNum));
/* 412:    */     }
/* 413:620 */     getStepManager().outputData(new Data[] { textData });
/* 414:    */   }
/* 415:    */   
/* 416:    */   protected void outputGraphData(weka.classifiers.Classifier classifier, int setNum)
/* 417:    */     throws WekaException
/* 418:    */   {
/* 419:633 */     if ((classifier instanceof Drawable))
/* 420:    */     {
/* 421:634 */       if (getStepManager().numOutgoingConnectionsOfType("graph") == 0) {
/* 422:635 */         return;
/* 423:    */       }
/* 424:    */       try
/* 425:    */       {
/* 426:639 */         String graphString = ((Drawable)classifier).graph();
/* 427:640 */         int graphType = ((Drawable)classifier).graphType();
/* 428:641 */         String grphTitle = classifier.getClass().getCanonicalName();
/* 429:642 */         grphTitle = grphTitle.substring(grphTitle.lastIndexOf('.') + 1, grphTitle.length());
/* 430:    */         
/* 431:    */ 
/* 432:645 */         grphTitle = "Set " + setNum + " (" + this.m_trainedClassifierHeader.relationName() + ") " + grphTitle;
/* 433:    */         
/* 434:    */ 
/* 435:648 */         Data graphData = new Data("graph");
/* 436:649 */         graphData.setPayloadElement("graph", graphString);
/* 437:650 */         graphData.setPayloadElement("graph_title", grphTitle);
/* 438:    */         
/* 439:652 */         graphData.setPayloadElement("graph_type", Integer.valueOf(graphType));
/* 440:    */         
/* 441:654 */         getStepManager().outputData(new Data[] { graphData });
/* 442:    */       }
/* 443:    */       catch (Exception ex)
/* 444:    */       {
/* 445:656 */         throw new WekaException(ex);
/* 446:    */       }
/* 447:    */     }
/* 448:    */   }
/* 449:    */   
/* 450:    */   public List<String> getIncomingConnectionTypes()
/* 451:    */   {
/* 452:663 */     List<String> result = new ArrayList();
/* 453:664 */     int numTraining = getStepManager().numIncomingConnectionsOfType("trainingSet");
/* 454:    */     
/* 455:    */ 
/* 456:667 */     int numTesting = getStepManager().numIncomingConnectionsOfType("testSet");
/* 457:    */     
/* 458:669 */     int numInstance = getStepManager().numIncomingConnectionsOfType("instance");
/* 459:672 */     if ((numTraining == 0) && (numTesting == 0)) {
/* 460:673 */       result.add("instance");
/* 461:    */     }
/* 462:676 */     if ((numInstance == 0) && (numTraining == 0)) {
/* 463:677 */       result.add("trainingSet");
/* 464:    */     }
/* 465:680 */     if ((numInstance == 0) && (numTesting == 0)) {
/* 466:681 */       result.add("testSet");
/* 467:    */     }
/* 468:684 */     return result;
/* 469:    */   }
/* 470:    */   
/* 471:    */   public List<String> getOutgoingConnectionTypes()
/* 472:    */   {
/* 473:689 */     List<String> result = new ArrayList();
/* 474:690 */     if (getStepManager().numIncomingConnections() > 0)
/* 475:    */     {
/* 476:691 */       int numTraining = getStepManager().numIncomingConnectionsOfType("trainingSet");
/* 477:    */       
/* 478:    */ 
/* 479:694 */       int numTesting = getStepManager().numIncomingConnectionsOfType("testSet");
/* 480:    */       
/* 481:696 */       int numInstance = getStepManager().numIncomingConnectionsOfType("instance");
/* 482:699 */       if (numInstance > 0) {
/* 483:700 */         result.add("incrementalClassifier");
/* 484:701 */       } else if ((numTraining > 0) || (numTesting > 0)) {
/* 485:702 */         result.add("batchClassifier");
/* 486:    */       }
/* 487:705 */       result.add("text");
/* 488:707 */       if (((getClassifier() instanceof Drawable)) && (numTraining > 0)) {
/* 489:708 */         result.add("graph");
/* 490:    */       }
/* 491:    */     }
/* 492:714 */     result.add("info");
/* 493:715 */     return result;
/* 494:    */   }
/* 495:    */   
/* 496:    */   protected void loadModel(String filePath)
/* 497:    */     throws Exception
/* 498:    */   {
/* 499:725 */     ObjectInputStream is = null;
/* 500:    */     try
/* 501:    */     {
/* 502:727 */       is = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(filePath))));
/* 503:    */       
/* 504:    */ 
/* 505:    */ 
/* 506:731 */       this.m_trainedClassifier = ((weka.classifiers.Classifier)is.readObject());
/* 507:733 */       if (!this.m_trainedClassifier.getClass().getCanonicalName().equals(getClassifier().getClass().getCanonicalName())) {
/* 508:735 */         throw new Exception("The loaded model '" + this.m_trainedClassifier.getClass().getCanonicalName() + "' is not a '" + getClassifier().getClass().getCanonicalName() + "'");
/* 509:    */       }
/* 510:    */       try
/* 511:    */       {
/* 512:742 */         this.m_trainedClassifierHeader = ((Instances)is.readObject());
/* 513:    */       }
/* 514:    */       catch (Exception ex)
/* 515:    */       {
/* 516:744 */         getStepManager().logWarning("Model file '" + filePath + "' does not seem to contain an Instances header");
/* 517:    */       }
/* 518:    */     }
/* 519:    */     finally
/* 520:    */     {
/* 521:749 */       if (is != null) {
/* 522:750 */         is.close();
/* 523:    */       }
/* 524:    */     }
/* 525:    */   }
/* 526:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.Classifier
 * JD-Core Version:    0.7.0.1
 */