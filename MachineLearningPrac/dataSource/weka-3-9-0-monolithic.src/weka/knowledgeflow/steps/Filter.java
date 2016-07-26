/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.HashMap;
/*   5:    */ import java.util.List;
/*   6:    */ import java.util.Map;
/*   7:    */ import java.util.concurrent.atomic.AtomicInteger;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.EnvironmentHandler;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.WekaException;
/*  13:    */ import weka.filters.StreamableFilter;
/*  14:    */ import weka.gui.ProgrammaticProperty;
/*  15:    */ import weka.knowledgeflow.Data;
/*  16:    */ import weka.knowledgeflow.ExecutionEnvironment;
/*  17:    */ import weka.knowledgeflow.StepManager;
/*  18:    */ 
/*  19:    */ @KFStep(name="Filter", category="Filters", toolTipText="Weka filter wrapper", iconPath="")
/*  20:    */ public class Filter
/*  21:    */   extends WekaAlgorithmWrapper
/*  22:    */ {
/*  23:    */   private static final long serialVersionUID = 6857031910153224479L;
/*  24:    */   protected weka.filters.Filter m_filterTemplate;
/*  25:    */   protected weka.filters.Filter m_streamingFilter;
/*  26:    */   protected boolean m_isReset;
/*  27:    */   protected boolean m_streaming;
/*  28:    */   protected boolean m_stringAttsPresent;
/*  29: 68 */   protected Map<Integer, weka.filters.Filter> m_filterMap = new HashMap();
/*  30: 72 */   protected Map<Integer, Instances> m_waitingTestData = new HashMap();
/*  31:    */   protected Data m_incrementalData;
/*  32:    */   protected AtomicInteger m_setCount;
/*  33:    */   
/*  34:    */   public Class getWrappedAlgorithmClass()
/*  35:    */   {
/*  36: 88 */     return weka.filters.Filter.class;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void setWrappedAlgorithm(Object algo)
/*  40:    */   {
/*  41: 98 */     super.setWrappedAlgorithm(algo);
/*  42: 99 */     this.m_defaultIconPath = "weka/gui/knowledgeflow/icons/DefaultFilter.gif";
/*  43:    */   }
/*  44:    */   
/*  45:    */   @ProgrammaticProperty
/*  46:    */   public void setFilter(weka.filters.Filter filter)
/*  47:    */   {
/*  48:109 */     setWrappedAlgorithm(filter);
/*  49:    */   }
/*  50:    */   
/*  51:    */   public weka.filters.Filter getFilter()
/*  52:    */   {
/*  53:118 */     return (weka.filters.Filter)getWrappedAlgorithm();
/*  54:    */   }
/*  55:    */   
/*  56:    */   public List<String> getIncomingConnectionTypes()
/*  57:    */   {
/*  58:132 */     List<String> result = new ArrayList();
/*  59:133 */     int numDataset = getStepManager().numIncomingConnectionsOfType("dataSet");
/*  60:    */     
/*  61:135 */     int numTraining = getStepManager().numIncomingConnectionsOfType("trainingSet");
/*  62:    */     
/*  63:    */ 
/*  64:138 */     int numTesting = getStepManager().numIncomingConnectionsOfType("testSet");
/*  65:    */     
/*  66:140 */     int numInstance = getStepManager().numIncomingConnectionsOfType("instance");
/*  67:143 */     if ((numDataset == 0) && (numTraining == 0) && (numTesting == 0) && ((getFilter() instanceof StreamableFilter))) {
/*  68:145 */       result.add("instance");
/*  69:    */     }
/*  70:148 */     if ((numInstance == 0) && (numDataset == 0) && (numTraining == 0))
/*  71:    */     {
/*  72:149 */       result.add("dataSet");
/*  73:150 */       result.add("trainingSet");
/*  74:    */     }
/*  75:153 */     if ((numInstance == 0) && (numTesting == 0)) {
/*  76:154 */       result.add("testSet");
/*  77:    */     }
/*  78:157 */     return result;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public List<String> getOutgoingConnectionTypes()
/*  82:    */   {
/*  83:171 */     List<String> result = new ArrayList();
/*  84:    */     
/*  85:173 */     int numDataset = getStepManager().numIncomingConnectionsOfType("dataSet");
/*  86:    */     
/*  87:175 */     int numTraining = getStepManager().numIncomingConnectionsOfType("trainingSet");
/*  88:    */     
/*  89:    */ 
/*  90:178 */     int numTesting = getStepManager().numIncomingConnectionsOfType("testSet");
/*  91:    */     
/*  92:180 */     int numInstance = getStepManager().numIncomingConnectionsOfType("instance");
/*  93:183 */     if (numInstance > 0) {
/*  94:184 */       result.add("instance");
/*  95:    */     }
/*  96:187 */     if (numDataset > 0) {
/*  97:188 */       result.add("dataSet");
/*  98:    */     }
/*  99:191 */     if (numTraining > 0) {
/* 100:192 */       result.add("trainingSet");
/* 101:    */     }
/* 102:195 */     if (numTesting > 0) {
/* 103:196 */       result.add("testSet");
/* 104:    */     }
/* 105:201 */     result.add("info");
/* 106:    */     
/* 107:203 */     return result;
/* 108:    */   }
/* 109:    */   
/* 110:    */   public void stepInit()
/* 111:    */     throws WekaException
/* 112:    */   {
/* 113:213 */     if (!(getWrappedAlgorithm() instanceof weka.filters.Filter)) {
/* 114:214 */       throw new WekaException("Incorrect type of algorithm");
/* 115:    */     }
/* 116:    */     try
/* 117:    */     {
/* 118:218 */       this.m_filterTemplate = weka.filters.Filter.makeCopy(getFilter());
/* 119:220 */       if ((this.m_filterTemplate instanceof EnvironmentHandler)) {
/* 120:221 */         ((EnvironmentHandler)this.m_filterTemplate).setEnvironment(getStepManager().getExecutionEnvironment().getEnvironmentVariables());
/* 121:    */       }
/* 122:    */     }
/* 123:    */     catch (Exception ex)
/* 124:    */     {
/* 125:225 */       throw new WekaException(ex);
/* 126:    */     }
/* 127:228 */     this.m_incrementalData = new Data("instance");
/* 128:229 */     this.m_filterMap.clear();
/* 129:230 */     this.m_waitingTestData.clear();
/* 130:231 */     this.m_streaming = false;
/* 131:232 */     this.m_stringAttsPresent = false;
/* 132:233 */     this.m_isReset = true;
/* 133:    */   }
/* 134:    */   
/* 135:    */   public void processIncoming(Data data)
/* 136:    */     throws WekaException
/* 137:    */   {
/* 138:244 */     Integer setNum = (Integer)data.getPayloadElement("aux_set_num");
/* 139:245 */     Integer maxSetNum = (Integer)data.getPayloadElement("aux_max_set_num");
/* 140:248 */     if (this.m_isReset)
/* 141:    */     {
/* 142:249 */       this.m_isReset = false;
/* 143:250 */       this.m_setCount = new AtomicInteger(maxSetNum != null ? maxSetNum.intValue() : 1);
/* 144:251 */       getStepManager().processing();
/* 145:252 */       if (data.getConnectionName().equals("instance"))
/* 146:    */       {
/* 147:253 */         Instances incomingStructure = ((Instance)data.getPayloadElement("instance")).dataset();
/* 148:    */         
/* 149:    */ 
/* 150:256 */         this.m_streaming = true;
/* 151:257 */         getStepManager().logBasic("Initializing streaming filter");
/* 152:    */         try
/* 153:    */         {
/* 154:259 */           this.m_streamingFilter = weka.filters.Filter.makeCopy(this.m_filterTemplate);
/* 155:260 */           this.m_streamingFilter.setInputFormat(incomingStructure);
/* 156:261 */           this.m_stringAttsPresent = this.m_streamingFilter.getOutputFormat().checkForStringAttributes();
/* 157:    */         }
/* 158:    */         catch (Exception ex)
/* 159:    */         {
/* 160:264 */           throw new WekaException(ex);
/* 161:    */         }
/* 162:    */       }
/* 163:    */     }
/* 164:273 */     if (this.m_streaming)
/* 165:    */     {
/* 166:274 */       if (getStepManager().isStreamFinished(data))
/* 167:    */       {
/* 168:275 */         checkPendingStreaming();
/* 169:    */         
/* 170:277 */         this.m_incrementalData.clearPayload();
/* 171:278 */         getStepManager().throughputFinished(new Data[] { this.m_incrementalData });
/* 172:    */       }
/* 173:    */       else
/* 174:    */       {
/* 175:280 */         processStreaming(data);
/* 176:    */       }
/* 177:    */     }
/* 178:282 */     else if ((data.getConnectionName().equals("trainingSet")) || (data.getConnectionName().equals("dataSet")))
/* 179:    */     {
/* 180:284 */       Instances d = (Instances)data.getPrimaryPayload();
/* 181:285 */       processFirstBatch(d, data.getConnectionName(), setNum, maxSetNum);
/* 182:    */     }
/* 183:    */     else
/* 184:    */     {
/* 185:289 */       Instances d = (Instances)data.getPrimaryPayload();
/* 186:290 */       if ((getStepManager().numIncomingConnectionsOfType("trainingSet") == 0) && (getStepManager().numIncomingConnectionsOfType("dataSet") == 0)) {
/* 187:294 */         processFirstBatch(d, data.getConnectionName(), setNum, maxSetNum);
/* 188:    */       } else {
/* 189:296 */         processSubsequentBatch(d, data.getConnectionName(), setNum, maxSetNum);
/* 190:    */       }
/* 191:    */     }
/* 192:300 */     if (isStopRequested())
/* 193:    */     {
/* 194:301 */       getStepManager().interrupted();
/* 195:    */     }
/* 196:302 */     else if ((!this.m_streaming) && 
/* 197:303 */       (this.m_setCount.get() == 0))
/* 198:    */     {
/* 199:304 */       getStepManager().finished();
/* 200:    */       
/* 201:    */ 
/* 202:307 */       this.m_waitingTestData.clear();
/* 203:308 */       this.m_filterMap.clear();
/* 204:    */     }
/* 205:    */   }
/* 206:    */   
/* 207:    */   protected void processFirstBatch(Instances batch, String conType, Integer setNum, Integer maxSetNum)
/* 208:    */     throws WekaException
/* 209:    */   {
/* 210:    */     try
/* 211:    */     {
/* 212:326 */       weka.filters.Filter filterToUse = weka.filters.Filter.makeCopy(this.m_filterTemplate);
/* 213:328 */       if (!isStopRequested())
/* 214:    */       {
/* 215:329 */         filterToUse.setInputFormat(batch);
/* 216:330 */         String message = "Filtering " + conType + " (" + batch.relationName();
/* 217:331 */         if ((setNum != null) && (maxSetNum != null)) {
/* 218:332 */           message = message + ", set " + setNum + " of " + maxSetNum;
/* 219:    */         }
/* 220:334 */         message = message + ")";
/* 221:335 */         getStepManager().statusMessage(message);
/* 222:336 */         getStepManager().logBasic(message);
/* 223:337 */         processBatch(batch, conType, filterToUse, setNum, maxSetNum);
/* 224:339 */         if (setNum != null) {
/* 225:340 */           this.m_filterMap.put(setNum, filterToUse);
/* 226:    */         } else {
/* 227:342 */           this.m_filterMap.put(Integer.valueOf(-1), filterToUse);
/* 228:    */         }
/* 229:345 */         Instances waitingTest = (Instances)this.m_waitingTestData.get(setNum);
/* 230:346 */         if (waitingTest != null) {
/* 231:347 */           processSubsequentBatch(waitingTest, "testSet", setNum, maxSetNum);
/* 232:349 */         } else if (getStepManager().numIncomingConnections() == 1) {
/* 233:350 */           this.m_setCount.decrementAndGet();
/* 234:    */         }
/* 235:    */       }
/* 236:    */     }
/* 237:    */     catch (Exception ex)
/* 238:    */     {
/* 239:354 */       throw new WekaException(ex);
/* 240:    */     }
/* 241:    */   }
/* 242:    */   
/* 243:    */   protected synchronized void processSubsequentBatch(Instances batch, String conType, Integer setNum, Integer maxSetNum)
/* 244:    */     throws WekaException
/* 245:    */   {
/* 246:370 */     Integer sN = Integer.valueOf(setNum != null ? setNum.intValue() : -1);
/* 247:371 */     weka.filters.Filter filterToUse = (weka.filters.Filter)this.m_filterMap.get(sN);
/* 248:372 */     if (filterToUse == null)
/* 249:    */     {
/* 250:374 */       this.m_waitingTestData.put(setNum, batch);
/* 251:375 */       return;
/* 252:    */     }
/* 253:378 */     if (!isStopRequested())
/* 254:    */     {
/* 255:379 */       String message = "Filtering " + conType + " (" + batch.relationName();
/* 256:380 */       if ((setNum != null) && (maxSetNum != null)) {
/* 257:381 */         message = message + ", set " + setNum + " of " + maxSetNum;
/* 258:    */       }
/* 259:383 */       message = message + ") - batch mode";
/* 260:384 */       getStepManager().statusMessage(message);
/* 261:385 */       getStepManager().logBasic(message);
/* 262:386 */       processBatch(batch, conType, filterToUse, setNum, maxSetNum);
/* 263:    */     }
/* 264:389 */     this.m_setCount.decrementAndGet();
/* 265:    */   }
/* 266:    */   
/* 267:    */   protected void processBatch(Instances batch, String conType, weka.filters.Filter filterToUse, Integer setNum, Integer maxSetNum)
/* 268:    */     throws WekaException
/* 269:    */   {
/* 270:    */     try
/* 271:    */     {
/* 272:406 */       Instances filtered = weka.filters.Filter.useFilter(batch, filterToUse);
/* 273:407 */       String title = conType + ": " + filtered.relationName();
/* 274:408 */       Data output = new Data(conType, filtered);
/* 275:409 */       if ((setNum != null) && (maxSetNum != null))
/* 276:    */       {
/* 277:410 */         output.setPayloadElement("aux_set_num", setNum);
/* 278:411 */         output.setPayloadElement("aux_max_set_num", maxSetNum);
/* 279:    */         
/* 280:413 */         output.setPayloadElement("aux_textTitle", title);
/* 281:    */       }
/* 282:415 */       getStepManager().outputData(new Data[] { output });
/* 283:    */     }
/* 284:    */     catch (Exception ex)
/* 285:    */     {
/* 286:417 */       throw new WekaException(ex);
/* 287:    */     }
/* 288:    */   }
/* 289:    */   
/* 290:    */   protected void processStreaming(Data data)
/* 291:    */     throws WekaException
/* 292:    */   {
/* 293:428 */     Instance toFilter = (Instance)data.getPrimaryPayload();
/* 294:429 */     getStepManager().throughputUpdateStart();
/* 295:    */     try
/* 296:    */     {
/* 297:431 */       if (this.m_streamingFilter.input(toFilter))
/* 298:    */       {
/* 299:432 */         Instance filteredI = this.m_streamingFilter.output();
/* 300:433 */         if (this.m_stringAttsPresent) {
/* 301:434 */           for (int i = 0; i < filteredI.numAttributes(); i++) {
/* 302:435 */             if ((filteredI.dataset().attribute(i).isString()) && (!filteredI.isMissing(i)))
/* 303:    */             {
/* 304:437 */               String val = filteredI.stringValue(i);
/* 305:438 */               filteredI.dataset().attribute(i).setStringValue(val);
/* 306:439 */               filteredI.setValue(i, 0.0D);
/* 307:    */             }
/* 308:    */           }
/* 309:    */         }
/* 310:443 */         this.m_incrementalData.setPayloadElement("instance", filteredI);
/* 311:445 */         if (!isStopRequested()) {
/* 312:446 */           getStepManager().outputData(new Data[] { this.m_incrementalData });
/* 313:    */         }
/* 314:    */       }
/* 315:    */     }
/* 316:    */     catch (Exception ex)
/* 317:    */     {
/* 318:450 */       throw new WekaException(ex);
/* 319:    */     }
/* 320:452 */     getStepManager().throughputUpdateEnd();
/* 321:    */   }
/* 322:    */   
/* 323:    */   protected void checkPendingStreaming()
/* 324:    */     throws WekaException
/* 325:    */   {
/* 326:    */     try
/* 327:    */     {
/* 328:462 */       this.m_streamingFilter.batchFinished();
/* 329:463 */       Instances structureCopy = this.m_streamingFilter.getOutputFormat().stringFreeStructure();
/* 330:465 */       while (this.m_streamingFilter.numPendingOutput() > 0)
/* 331:    */       {
/* 332:466 */         getStepManager().throughputUpdateStart();
/* 333:467 */         Instance filteredI = this.m_streamingFilter.output();
/* 334:468 */         if (this.m_stringAttsPresent)
/* 335:    */         {
/* 336:469 */           for (int i = 0; i < filteredI.numAttributes(); i++)
/* 337:    */           {
/* 338:470 */             String val = filteredI.stringValue(i);
/* 339:471 */             structureCopy.attribute(i).setStringValue(val);
/* 340:472 */             filteredI.setValue(i, 0.0D);
/* 341:    */           }
/* 342:474 */           filteredI.setDataset(structureCopy);
/* 343:    */         }
/* 344:476 */         this.m_incrementalData.setPayloadElement("instance", filteredI);
/* 345:478 */         if (!isStopRequested()) {
/* 346:479 */           getStepManager().outputData(new Data[] { this.m_incrementalData });
/* 347:    */         }
/* 348:481 */         getStepManager().throughputUpdateEnd();
/* 349:    */       }
/* 350:    */     }
/* 351:    */     catch (Exception ex)
/* 352:    */     {
/* 353:484 */       throw new WekaException(ex);
/* 354:    */     }
/* 355:    */   }
/* 356:    */   
/* 357:    */   public Instances outputStructureForConnectionType(String connectionName)
/* 358:    */     throws WekaException
/* 359:    */   {
/* 360:502 */     Instances incomingStructure = null;
/* 361:503 */     String incomingConType = null;
/* 362:504 */     if (getStepManager().numIncomingConnectionsOfType("trainingSet") > 0) {
/* 363:506 */       incomingConType = "trainingSet";
/* 364:507 */     } else if (getStepManager().numIncomingConnectionsOfType("testSet") > 0) {
/* 365:509 */       incomingConType = "testSet";
/* 366:510 */     } else if (getStepManager().numIncomingConnectionsOfType("dataSet") > 0) {
/* 367:512 */       incomingConType = "dataSet";
/* 368:513 */     } else if (getStepManager().numIncomingConnectionsOfType("instance") > 0) {
/* 369:515 */       incomingConType = "instance";
/* 370:    */     }
/* 371:518 */     if (incomingConType != null) {
/* 372:519 */       incomingStructure = getStepManager().getIncomingStructureForConnectionType(incomingConType);
/* 373:    */     }
/* 374:523 */     if (incomingStructure != null) {
/* 375:    */       try
/* 376:    */       {
/* 377:525 */         weka.filters.Filter tempFilter = weka.filters.Filter.makeCopy(this.m_filterTemplate);
/* 378:527 */         if (tempFilter.setInputFormat(incomingStructure)) {
/* 379:528 */           return tempFilter.getOutputFormat();
/* 380:    */         }
/* 381:    */       }
/* 382:    */       catch (Exception ex)
/* 383:    */       {
/* 384:531 */         throw new WekaException(ex);
/* 385:    */       }
/* 386:    */     }
/* 387:535 */     return null;
/* 388:    */   }
/* 389:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.Filter
 * JD-Core Version:    0.7.0.1
 */