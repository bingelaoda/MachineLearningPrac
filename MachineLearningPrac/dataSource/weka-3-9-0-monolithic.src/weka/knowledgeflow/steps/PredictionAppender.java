/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.List;
/*   6:    */ import weka.classifiers.Classifier;
/*   7:    */ import weka.classifiers.UpdateableClassifier;
/*   8:    */ import weka.clusterers.Clusterer;
/*   9:    */ import weka.clusterers.DensityBasedClusterer;
/*  10:    */ import weka.core.Attribute;
/*  11:    */ import weka.core.DenseInstance;
/*  12:    */ import weka.core.Instance;
/*  13:    */ import weka.core.Instances;
/*  14:    */ import weka.core.OptionMetadata;
/*  15:    */ import weka.core.WekaException;
/*  16:    */ import weka.filters.Filter;
/*  17:    */ import weka.filters.unsupervised.attribute.Add;
/*  18:    */ import weka.knowledgeflow.Data;
/*  19:    */ import weka.knowledgeflow.StepManager;
/*  20:    */ 
/*  21:    */ @KFStep(name="PredictionAppender", category="Evaluation", toolTipText="Append predictions from classifiers or clusterers to incoming data ", iconPath="weka/gui/knowledgeflow/icons/PredictionAppender.gif")
/*  22:    */ public class PredictionAppender
/*  23:    */   extends BaseStep
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = 3558618759400903936L;
/*  26:    */   protected boolean m_appendProbabilities;
/*  27:    */   protected Instances m_streamingOutputStructure;
/*  28: 64 */   protected Data m_instanceData = new Data("instance");
/*  29:    */   protected List<Integer> m_stringAttIndexes;
/*  30:    */   
/*  31:    */   public void stepInit()
/*  32:    */     throws WekaException
/*  33:    */   {
/*  34: 76 */     this.m_streamingOutputStructure = null;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public List<String> getIncomingConnectionTypes()
/*  38:    */   {
/*  39: 86 */     if (getStepManager().numIncomingConnections() == 0) {
/*  40: 87 */       return Arrays.asList(new String[] { "batchClassifier", "incrementalClassifier" });
/*  41:    */     }
/*  42: 91 */     return new ArrayList();
/*  43:    */   }
/*  44:    */   
/*  45:    */   public List<String> getOutgoingConnectionTypes()
/*  46:    */   {
/*  47:102 */     List<String> result = new ArrayList();
/*  48:103 */     if ((getStepManager().numIncomingConnectionsOfType("batchClassifier") > 0) || (getStepManager().numIncomingConnectionsOfType("batchClusterer") > 0))
/*  49:    */     {
/*  50:107 */       result.add("trainingSet");
/*  51:108 */       result.add("testSet");
/*  52:    */     }
/*  53:109 */     else if (getStepManager().numIncomingConnectionsOfType("incrementalClassifier") > 0)
/*  54:    */     {
/*  55:111 */       result.add("instance");
/*  56:    */     }
/*  57:114 */     return result;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public void processIncoming(Data data)
/*  61:    */     throws WekaException
/*  62:    */   {
/*  63:125 */     Instances trainingData = (Instances)data.getPayloadElement("aux_trainingSet");
/*  64:    */     
/*  65:127 */     Instances testData = (Instances)data.getPayloadElement("aux_testsSet");
/*  66:    */     
/*  67:129 */     Instance streamInstance = (Instance)data.getPayloadElement("aux_testInstance");
/*  68:131 */     if (getStepManager().numIncomingConnectionsOfType("batchClassifier") > 0) {
/*  69:133 */       processBatchClassifierCase(data, trainingData, testData);
/*  70:134 */     } else if (getStepManager().numIncomingConnectionsOfType("incrementalClassifier") > 0) {
/*  71:136 */       processIncrementalClassifier(data, streamInstance);
/*  72:137 */     } else if (getStepManager().numIncomingConnectionsOfType("batchClusterer") > 0) {
/*  73:139 */       processBatchClustererCase(data, trainingData, testData);
/*  74:    */     }
/*  75:    */   }
/*  76:    */   
/*  77:    */   protected void processIncrementalClassifier(Data data, Instance inst)
/*  78:    */     throws WekaException
/*  79:    */   {
/*  80:152 */     if (isStopRequested()) {
/*  81:153 */       return;
/*  82:    */     }
/*  83:156 */     if (getStepManager().isStreamFinished(data))
/*  84:    */     {
/*  85:159 */       Data d = new Data("instance");
/*  86:160 */       getStepManager().throughputFinished(new Data[] { d });
/*  87:161 */       return;
/*  88:    */     }
/*  89:164 */     getStepManager().throughputUpdateStart();
/*  90:165 */     boolean labelOrNumeric = (!this.m_appendProbabilities) || (inst.classAttribute().isNumeric());
/*  91:    */     
/*  92:167 */     Classifier classifier = (Classifier)data.getPayloadElement("incrementalClassifier");
/*  93:170 */     if (this.m_streamingOutputStructure == null)
/*  94:    */     {
/*  95:172 */       if (classifier == null) {
/*  96:173 */         throw new WekaException("No classifier in incoming data object!");
/*  97:    */       }
/*  98:175 */       if (!(classifier instanceof UpdateableClassifier)) {
/*  99:176 */         throw new WekaException("Classifier in data object is not an UpdateableClassifier!");
/* 100:    */       }
/* 101:179 */       this.m_stringAttIndexes = new ArrayList();
/* 102:180 */       for (int i = 0; i < inst.numAttributes(); i++) {
/* 103:181 */         if (inst.attribute(i).isString()) {
/* 104:182 */           this.m_stringAttIndexes.add(Integer.valueOf(i));
/* 105:    */         }
/* 106:    */       }
/* 107:    */       try
/* 108:    */       {
/* 109:186 */         this.m_streamingOutputStructure = makeOutputDataClassifier(inst.dataset(), classifier, !labelOrNumeric, "_with_predictions");
/* 110:    */       }
/* 111:    */       catch (Exception ex)
/* 112:    */       {
/* 113:190 */         throw new WekaException(ex);
/* 114:    */       }
/* 115:    */     }
/* 116:194 */     double[] instanceVals = new double[this.m_streamingOutputStructure.numAttributes()];
/* 117:    */     
/* 118:196 */     Instance newInstance = null;
/* 119:197 */     for (int i = 0; i < inst.numAttributes(); i++) {
/* 120:198 */       instanceVals[i] = inst.value(i);
/* 121:    */     }
/* 122:200 */     if ((!this.m_appendProbabilities) || (inst.classAttribute().isNumeric())) {
/* 123:    */       try
/* 124:    */       {
/* 125:202 */         double predClass = classifier.classifyInstance(inst);
/* 126:203 */         instanceVals[(instanceVals.length - 1)] = predClass;
/* 127:    */       }
/* 128:    */       catch (Exception ex)
/* 129:    */       {
/* 130:205 */         throw new WekaException(ex);
/* 131:    */       }
/* 132:207 */     } else if (this.m_appendProbabilities) {
/* 133:    */       try
/* 134:    */       {
/* 135:209 */         double[] preds = classifier.distributionForInstance(inst);
/* 136:210 */         int index = 0;
/* 137:211 */         for (int i = instanceVals.length - inst.classAttribute().numValues(); i < instanceVals.length; i++) {
/* 138:212 */           instanceVals[i] = preds[(index++)];
/* 139:    */         }
/* 140:    */       }
/* 141:    */       catch (Exception ex)
/* 142:    */       {
/* 143:215 */         throw new WekaException(ex);
/* 144:    */       }
/* 145:    */     }
/* 146:219 */     Instance newInst = new DenseInstance(inst.weight(), instanceVals);
/* 147:220 */     newInst.setDataset(this.m_streamingOutputStructure);
/* 148:222 */     if (this.m_stringAttIndexes != null) {
/* 149:223 */       for (int i = 0; i < this.m_stringAttIndexes.size(); i++)
/* 150:    */       {
/* 151:224 */         int index = ((Integer)this.m_stringAttIndexes.get(i)).intValue();
/* 152:225 */         this.m_streamingOutputStructure.attribute(index).setStringValue(inst.stringValue(index));
/* 153:    */       }
/* 154:    */     }
/* 155:230 */     this.m_instanceData.setPayloadElement("instance", newInst);
/* 156:231 */     if (isStopRequested()) {
/* 157:232 */       return;
/* 158:    */     }
/* 159:234 */     getStepManager().throughputUpdateEnd();
/* 160:235 */     getStepManager().outputData(this.m_instanceData.getConnectionName(), this.m_instanceData);
/* 161:    */   }
/* 162:    */   
/* 163:    */   protected void processBatchClustererCase(Data data, Instances trainingData, Instances testData)
/* 164:    */     throws WekaException
/* 165:    */   {
/* 166:250 */     if (isStopRequested())
/* 167:    */     {
/* 168:251 */       getStepManager().interrupted();
/* 169:252 */       return;
/* 170:    */     }
/* 171:255 */     Clusterer clusterer = (Clusterer)data.getPayloadElement("batchClusterer");
/* 172:    */     
/* 173:    */ 
/* 174:258 */     int setNum = ((Integer)data.getPayloadElement("aux_set_num")).intValue();
/* 175:    */     
/* 176:260 */     int maxSetNum = ((Integer)data.getPayloadElement("aux_max_set_num")).intValue();
/* 177:    */     
/* 178:262 */     String relationNameModifier = "_set_" + setNum + "_of_" + maxSetNum;
/* 179:264 */     if ((this.m_appendProbabilities) && (!(clusterer instanceof DensityBasedClusterer))) {
/* 180:265 */       throw new WekaException("Only DensityBasedClusterers can append probabilities.");
/* 181:    */     }
/* 182:    */     try
/* 183:    */     {
/* 184:270 */       getStepManager().processing();
/* 185:271 */       boolean clusterLabel = (!this.m_appendProbabilities) || (!(clusterer instanceof DensityBasedClusterer));
/* 186:    */       
/* 187:273 */       Instances newTrainInstances = trainingData != null ? makeOutputDataClusterer(trainingData, clusterer, !clusterLabel, relationNameModifier) : null;
/* 188:    */       
/* 189:    */ 
/* 190:276 */       Instances newTestInstances = testData != null ? makeOutputDataClusterer(testData, clusterer, !clusterLabel, relationNameModifier) : null;
/* 191:280 */       if ((newTrainInstances != null) && (getStepManager().numOutgoingConnectionsOfType("trainingSet") > 0))
/* 192:    */       {
/* 193:283 */         for (int i = 0; i < newTrainInstances.numInstances(); i++) {
/* 194:284 */           if (clusterLabel) {
/* 195:285 */             predictLabelClusterer(clusterer, newTrainInstances.instance(i));
/* 196:    */           } else {
/* 197:287 */             predictProbabilitiesClusterer((DensityBasedClusterer)clusterer, newTrainInstances.instance(i));
/* 198:    */           }
/* 199:    */         }
/* 200:292 */         if (isStopRequested())
/* 201:    */         {
/* 202:293 */           getStepManager().interrupted();
/* 203:294 */           return;
/* 204:    */         }
/* 205:296 */         Data outTrain = new Data("trainingSet");
/* 206:297 */         outTrain.setPayloadElement("trainingSet", newTrainInstances);
/* 207:    */         
/* 208:299 */         outTrain.setPayloadElement("aux_set_num", Integer.valueOf(setNum));
/* 209:300 */         outTrain.setPayloadElement("aux_max_set_num", Integer.valueOf(maxSetNum));
/* 210:    */         
/* 211:302 */         getStepManager().outputData(new Data[] { outTrain });
/* 212:    */       }
/* 213:305 */       if ((newTestInstances != null) && ((getStepManager().numOutgoingConnectionsOfType("testSet") > 0) || (getStepManager().numOutgoingConnectionsOfType("dataSet") > 0)))
/* 214:    */       {
/* 215:310 */         for (int i = 0; i < newTestInstances.numInstances(); i++) {
/* 216:311 */           if (clusterLabel) {
/* 217:312 */             predictLabelClusterer(clusterer, newTestInstances.instance(i));
/* 218:    */           } else {
/* 219:314 */             predictProbabilitiesClusterer((DensityBasedClusterer)clusterer, newTestInstances.instance(i));
/* 220:    */           }
/* 221:    */         }
/* 222:319 */         if (isStopRequested())
/* 223:    */         {
/* 224:320 */           getStepManager().interrupted();
/* 225:321 */           return;
/* 226:    */         }
/* 227:323 */         if (getStepManager().numOutgoingConnectionsOfType("testSet") > 0)
/* 228:    */         {
/* 229:325 */           Data outTest = new Data("testSet");
/* 230:326 */           outTest.setPayloadElement("testSet", newTestInstances);
/* 231:327 */           outTest.setPayloadElement("aux_set_num", Integer.valueOf(setNum));
/* 232:328 */           outTest.setPayloadElement("aux_max_set_num", Integer.valueOf(maxSetNum));
/* 233:    */           
/* 234:330 */           getStepManager().outputData(new Data[] { outTest });
/* 235:    */         }
/* 236:332 */         if (getStepManager().numIncomingConnectionsOfType("dataSet") > 0)
/* 237:    */         {
/* 238:334 */           Data outData = new Data("dataSet");
/* 239:335 */           outData.setPayloadElement("dataSet", newTestInstances);
/* 240:336 */           outData.setPayloadElement("aux_set_num", Integer.valueOf(setNum));
/* 241:337 */           outData.setPayloadElement("aux_max_set_num", Integer.valueOf(maxSetNum));
/* 242:    */           
/* 243:339 */           getStepManager().outputData(new Data[] { outData });
/* 244:    */         }
/* 245:    */       }
/* 246:342 */       getStepManager().finished();
/* 247:    */     }
/* 248:    */     catch (Exception ex)
/* 249:    */     {
/* 250:344 */       throw new WekaException(ex);
/* 251:    */     }
/* 252:    */   }
/* 253:    */   
/* 254:    */   protected void processBatchClassifierCase(Data data, Instances trainingData, Instances testData)
/* 255:    */     throws WekaException
/* 256:    */   {
/* 257:359 */     if (isStopRequested())
/* 258:    */     {
/* 259:360 */       getStepManager().interrupted();
/* 260:361 */       return;
/* 261:    */     }
/* 262:364 */     Classifier classifier = (Classifier)data.getPayloadElement("batchClassifier");
/* 263:    */     
/* 264:    */ 
/* 265:367 */     int setNum = ((Integer)data.getPayloadElement("aux_set_num")).intValue();
/* 266:    */     
/* 267:369 */     int maxSetNum = ((Integer)data.getPayloadElement("aux_max_set_num")).intValue();
/* 268:    */     
/* 269:371 */     String relationNameModifier = "_set_" + setNum + "_of_" + maxSetNum;
/* 270:372 */     boolean classNumeric = trainingData != null ? trainingData.classAttribute().isNumeric() : testData.classAttribute().isNumeric();
/* 271:    */     
/* 272:    */ 
/* 273:    */ 
/* 274:376 */     boolean labelOrNumeric = (!this.m_appendProbabilities) || (classNumeric);
/* 275:    */     try
/* 276:    */     {
/* 277:378 */       getStepManager().processing();
/* 278:379 */       Instances newTrainInstances = trainingData != null ? makeOutputDataClassifier(trainingData, classifier, !labelOrNumeric, relationNameModifier) : null;
/* 279:    */       
/* 280:    */ 
/* 281:382 */       Instances newTestInstances = testData != null ? makeOutputDataClassifier(testData, classifier, !labelOrNumeric, relationNameModifier) : null;
/* 282:385 */       if ((newTrainInstances != null) && (getStepManager().numOutgoingConnectionsOfType("trainingSet") > 0))
/* 283:    */       {
/* 284:388 */         for (int i = 0; i < newTrainInstances.numInstances(); i++) {
/* 285:389 */           if (labelOrNumeric) {
/* 286:390 */             predictLabelClassifier(classifier, newTrainInstances.instance(i));
/* 287:    */           } else {
/* 288:392 */             predictProbabilitiesClassifier(classifier, newTrainInstances.instance(i));
/* 289:    */           }
/* 290:    */         }
/* 291:396 */         if (isStopRequested())
/* 292:    */         {
/* 293:397 */           getStepManager().interrupted();
/* 294:398 */           return;
/* 295:    */         }
/* 296:400 */         Data outTrain = new Data("trainingSet");
/* 297:401 */         outTrain.setPayloadElement("trainingSet", newTrainInstances);
/* 298:    */         
/* 299:403 */         outTrain.setPayloadElement("aux_set_num", Integer.valueOf(setNum));
/* 300:404 */         outTrain.setPayloadElement("aux_max_set_num", Integer.valueOf(maxSetNum));
/* 301:    */         
/* 302:406 */         getStepManager().outputData(new Data[] { outTrain });
/* 303:    */       }
/* 304:408 */       if ((newTestInstances != null) && ((getStepManager().numOutgoingConnectionsOfType("testSet") > 0) || (getStepManager().numOutgoingConnectionsOfType("dataSet") > 0)))
/* 305:    */       {
/* 306:412 */         for (int i = 0; i < newTestInstances.numInstances(); i++) {
/* 307:413 */           if (labelOrNumeric) {
/* 308:414 */             predictLabelClassifier(classifier, newTestInstances.instance(i));
/* 309:    */           } else {
/* 310:416 */             predictProbabilitiesClassifier(classifier, newTestInstances.instance(i));
/* 311:    */           }
/* 312:    */         }
/* 313:420 */         if (isStopRequested())
/* 314:    */         {
/* 315:421 */           getStepManager().interrupted();
/* 316:422 */           return;
/* 317:    */         }
/* 318:424 */         if (getStepManager().numOutgoingConnectionsOfType("testSet") > 0)
/* 319:    */         {
/* 320:426 */           Data outTest = new Data("testSet");
/* 321:427 */           outTest.setPayloadElement("testSet", newTestInstances);
/* 322:428 */           outTest.setPayloadElement("aux_set_num", Integer.valueOf(setNum));
/* 323:429 */           outTest.setPayloadElement("aux_max_set_num", Integer.valueOf(maxSetNum));
/* 324:    */           
/* 325:431 */           getStepManager().outputData(new Data[] { outTest });
/* 326:    */         }
/* 327:433 */         if (getStepManager().numIncomingConnectionsOfType("dataSet") > 0)
/* 328:    */         {
/* 329:435 */           Data outData = new Data("dataSet");
/* 330:436 */           outData.setPayloadElement("dataSet", newTestInstances);
/* 331:437 */           outData.setPayloadElement("aux_set_num", Integer.valueOf(setNum));
/* 332:438 */           outData.setPayloadElement("aux_max_set_num", Integer.valueOf(maxSetNum));
/* 333:    */           
/* 334:440 */           getStepManager().outputData(new Data[] { outData });
/* 335:    */         }
/* 336:    */       }
/* 337:443 */       getStepManager().finished();
/* 338:    */     }
/* 339:    */     catch (Exception ex)
/* 340:    */     {
/* 341:445 */       throw new WekaException(ex);
/* 342:    */     }
/* 343:    */   }
/* 344:    */   
/* 345:    */   protected void predictLabelClusterer(Clusterer clusterer, Instance inst)
/* 346:    */     throws WekaException
/* 347:    */   {
/* 348:    */     try
/* 349:    */     {
/* 350:459 */       int cluster = clusterer.clusterInstance(inst);
/* 351:460 */       inst.setValue(inst.numAttributes() - 1, cluster);
/* 352:    */     }
/* 353:    */     catch (Exception ex)
/* 354:    */     {
/* 355:462 */       throw new WekaException(ex);
/* 356:    */     }
/* 357:    */   }
/* 358:    */   
/* 359:    */   protected void predictProbabilitiesClusterer(DensityBasedClusterer clusterer, Instance inst)
/* 360:    */     throws WekaException
/* 361:    */   {
/* 362:    */     try
/* 363:    */     {
/* 364:477 */       double[] preds = clusterer.distributionForInstance(inst);
/* 365:478 */       for (int i = 0; i < preds.length; i++) {
/* 366:479 */         inst.setValue(inst.numAttributes() - preds.length + i, preds[i]);
/* 367:    */       }
/* 368:    */     }
/* 369:    */     catch (Exception ex)
/* 370:    */     {
/* 371:482 */       throw new WekaException(ex);
/* 372:    */     }
/* 373:    */   }
/* 374:    */   
/* 375:    */   protected void predictLabelClassifier(Classifier classifier, Instance inst)
/* 376:    */     throws WekaException
/* 377:    */   {
/* 378:    */     try
/* 379:    */     {
/* 380:497 */       double pred = classifier.classifyInstance(inst);
/* 381:498 */       inst.setValue(inst.numAttributes() - 1, pred);
/* 382:    */     }
/* 383:    */     catch (Exception ex)
/* 384:    */     {
/* 385:500 */       throw new WekaException(ex);
/* 386:    */     }
/* 387:    */   }
/* 388:    */   
/* 389:    */   protected void predictProbabilitiesClassifier(Classifier classifier, Instance inst)
/* 390:    */     throws WekaException
/* 391:    */   {
/* 392:    */     try
/* 393:    */     {
/* 394:514 */       double[] preds = classifier.distributionForInstance(inst);
/* 395:515 */       for (int i = 0; i < preds.length; i++) {
/* 396:516 */         inst.setValue(inst.numAttributes() - preds.length + i, preds[i]);
/* 397:    */       }
/* 398:    */     }
/* 399:    */     catch (Exception ex)
/* 400:    */     {
/* 401:519 */       throw new WekaException(ex);
/* 402:    */     }
/* 403:    */   }
/* 404:    */   
/* 405:    */   protected Instances makeOutputDataClusterer(Instances inputData, Clusterer clusterer, boolean distribution, String relationNameModifier)
/* 406:    */     throws Exception
/* 407:    */   {
/* 408:540 */     String clustererName = clusterer.getClass().getName();
/* 409:541 */     clustererName = clustererName.substring(clustererName.lastIndexOf('.') + 1, clustererName.length());
/* 410:    */     
/* 411:    */ 
/* 412:544 */     Instances newData = new Instances(inputData);
/* 413:546 */     if (distribution)
/* 414:    */     {
/* 415:547 */       for (int i = 0; i < clusterer.numberOfClusters(); i++)
/* 416:    */       {
/* 417:548 */         Add addF = new Add();
/* 418:549 */         addF.setAttributeIndex("last");
/* 419:550 */         addF.setAttributeName("prob_cluster" + i);
/* 420:551 */         addF.setInputFormat(newData);
/* 421:552 */         newData = Filter.useFilter(newData, addF);
/* 422:    */       }
/* 423:    */     }
/* 424:    */     else
/* 425:    */     {
/* 426:555 */       Add addF = new Add();
/* 427:556 */       addF.setAttributeIndex("last");
/* 428:557 */       addF.setAttributeName("assigned_cluster: " + clustererName);
/* 429:558 */       String clusterLabels = "0";
/* 430:559 */       for (int i = 1; i <= clusterer.numberOfClusters() - 1; i++) {
/* 431:560 */         clusterLabels = clusterLabels + "," + i;
/* 432:    */       }
/* 433:562 */       addF.setNominalLabels(clusterLabels);
/* 434:563 */       addF.setInputFormat(newData);
/* 435:564 */       newData = Filter.useFilter(newData, addF);
/* 436:    */     }
/* 437:567 */     newData.setRelationName(inputData.relationName() + relationNameModifier);
/* 438:568 */     return newData;
/* 439:    */   }
/* 440:    */   
/* 441:    */   protected Instances makeOutputDataClassifier(Instances inputData, Classifier classifier, boolean distribution, String relationNameModifier)
/* 442:    */     throws Exception
/* 443:    */   {
/* 444:588 */     String classifierName = classifier.getClass().getName();
/* 445:589 */     classifierName = classifierName.substring(classifierName.lastIndexOf('.') + 1, classifierName.length());
/* 446:    */     
/* 447:    */ 
/* 448:592 */     Instances newData = new Instances(inputData);
/* 449:594 */     if (distribution)
/* 450:    */     {
/* 451:595 */       for (int i = 0; i < inputData.classAttribute().numValues(); i++)
/* 452:    */       {
/* 453:596 */         Add addF = new Add();
/* 454:597 */         addF.setAttributeIndex("last");
/* 455:598 */         addF.setAttributeName(classifierName + "_prob_" + inputData.classAttribute().value(i));
/* 456:    */         
/* 457:600 */         addF.setInputFormat(newData);
/* 458:601 */         newData = Filter.useFilter(newData, addF);
/* 459:    */       }
/* 460:    */     }
/* 461:    */     else
/* 462:    */     {
/* 463:604 */       Add addF = new Add();
/* 464:605 */       addF.setAttributeIndex("last");
/* 465:606 */       addF.setAttributeName("class_predicted_by: " + classifierName);
/* 466:607 */       if (inputData.classAttribute().isNominal())
/* 467:    */       {
/* 468:608 */         String classLabels = inputData.classAttribute().value(0);
/* 469:609 */         for (int i = 1; i < inputData.classAttribute().numValues(); i++) {
/* 470:610 */           classLabels = classLabels + "," + inputData.classAttribute().value(i);
/* 471:    */         }
/* 472:612 */         addF.setNominalLabels(classLabels);
/* 473:    */       }
/* 474:614 */       addF.setInputFormat(inputData);
/* 475:615 */       newData = Filter.useFilter(inputData, addF);
/* 476:    */     }
/* 477:618 */     newData.setRelationName(inputData.relationName() + relationNameModifier);
/* 478:619 */     return newData;
/* 479:    */   }
/* 480:    */   
/* 481:    */   public void setAppendProbabilities(boolean append)
/* 482:    */   {
/* 483:628 */     this.m_appendProbabilities = append;
/* 484:    */   }
/* 485:    */   
/* 486:    */   @OptionMetadata(displayName="Append probabilities", description="Append probabilities")
/* 487:    */   public boolean getAppendProbabilities()
/* 488:    */   {
/* 489:639 */     return this.m_appendProbabilities;
/* 490:    */   }
/* 491:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.PredictionAppender
 * JD-Core Version:    0.7.0.1
 */