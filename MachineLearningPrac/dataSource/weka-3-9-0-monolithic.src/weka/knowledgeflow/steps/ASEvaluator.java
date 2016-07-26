/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.HashMap;
/*   5:    */ import java.util.List;
/*   6:    */ import java.util.Map;
/*   7:    */ import java.util.concurrent.atomic.AtomicInteger;
/*   8:    */ import weka.attributeSelection.ASEvaluation;
/*   9:    */ import weka.attributeSelection.ASSearch;
/*  10:    */ import weka.attributeSelection.AttributeEvaluator;
/*  11:    */ import weka.attributeSelection.AttributeSelection;
/*  12:    */ import weka.attributeSelection.AttributeTransformer;
/*  13:    */ import weka.attributeSelection.RankedOutputSearch;
/*  14:    */ import weka.attributeSelection.Ranker;
/*  15:    */ import weka.attributeSelection.SubsetEvaluator;
/*  16:    */ import weka.attributeSelection.UnsupervisedAttributeEvaluator;
/*  17:    */ import weka.attributeSelection.UnsupervisedSubsetEvaluator;
/*  18:    */ import weka.core.Instances;
/*  19:    */ import weka.core.OptionHandler;
/*  20:    */ import weka.core.OptionMetadata;
/*  21:    */ import weka.core.Utils;
/*  22:    */ import weka.core.WekaException;
/*  23:    */ import weka.filters.Filter;
/*  24:    */ import weka.filters.unsupervised.attribute.Remove;
/*  25:    */ import weka.gui.ProgrammaticProperty;
/*  26:    */ import weka.knowledgeflow.Data;
/*  27:    */ import weka.knowledgeflow.StepManager;
/*  28:    */ 
/*  29:    */ @KFStep(name="ASEvaluator", category="AttSelection", toolTipText="Weka attribute selection evaluator wrapper", iconPath="", resourceIntensive=true)
/*  30:    */ public class ASEvaluator
/*  31:    */   extends WekaAlgorithmWrapper
/*  32:    */ {
/*  33:    */   private static final long serialVersionUID = -1280208826860871742L;
/*  34:    */   protected ASEvaluation m_evaluatorTemplate;
/*  35:    */   protected ASSearch m_searchTemplate;
/*  36: 83 */   protected Map<Integer, Instances> m_waitingTestData = new HashMap();
/*  37: 87 */   protected Map<Integer, int[]> m_selectedAttsStore = new HashMap();
/*  38: 94 */   protected Map<Integer, Integer> m_numToSelectStore = new HashMap();
/*  39:101 */   protected Map<Integer, AttributeTransformer> m_transformerStore = new HashMap();
/*  40:    */   protected boolean m_isReset;
/*  41:    */   protected boolean m_isDoingXVal;
/*  42:    */   protected AtomicInteger m_setCount;
/*  43:    */   protected boolean m_treatXValFoldsSeparately;
/*  44:    */   protected boolean m_isRanking;
/*  45:    */   protected AttributeSelection m_eval;
/*  46:    */   
/*  47:    */   public Class getWrappedAlgorithmClass()
/*  48:    */   {
/*  49:138 */     return ASEvaluation.class;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void setWrappedAlgorithm(Object algo)
/*  53:    */   {
/*  54:148 */     super.setWrappedAlgorithm(algo);
/*  55:149 */     this.m_defaultIconPath = "weka/gui/knowledgeflow/icons/filters.supervised.attribute.AttributeSelection.gif";
/*  56:    */   }
/*  57:    */   
/*  58:    */   public ASEvaluation getEvaluator()
/*  59:    */   {
/*  60:159 */     return (ASEvaluation)getWrappedAlgorithm();
/*  61:    */   }
/*  62:    */   
/*  63:    */   @ProgrammaticProperty
/*  64:    */   public void setEvaluator(ASEvaluation eval)
/*  65:    */   {
/*  66:169 */     setWrappedAlgorithm(eval);
/*  67:    */   }
/*  68:    */   
/*  69:    */   @OptionMetadata(displayName="Treat x-val folds separately", description="Output separate attribute selection results for each fold of a cross-validation (rather than averaging across folds)")
/*  70:    */   public void setTreatXValFoldsSeparately(boolean treatSeparately)
/*  71:    */   {
/*  72:182 */     this.m_treatXValFoldsSeparately = treatSeparately;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public boolean getTreatXValFoldsSeparately()
/*  76:    */   {
/*  77:192 */     return this.m_treatXValFoldsSeparately;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public void stepInit()
/*  81:    */     throws WekaException
/*  82:    */   {
/*  83:204 */     if (!(getWrappedAlgorithm() instanceof ASEvaluation)) {
/*  84:205 */       throw new WekaException("Incorrect type of algorithm");
/*  85:    */     }
/*  86:    */     try
/*  87:    */     {
/*  88:209 */       this.m_evaluatorTemplate = ASEvaluation.makeCopies((ASEvaluation)getWrappedAlgorithm(), 1)[0];
/*  89:    */     }
/*  90:    */     catch (Exception ex)
/*  91:    */     {
/*  92:212 */       throw new WekaException(ex);
/*  93:    */     }
/*  94:215 */     List<StepManager> infos = getStepManager().getIncomingConnectedStepsOfConnectionType("info");
/*  95:217 */     if (infos.size() == 0) {
/*  96:218 */       throw new WekaException("A search strategy needs to be supplied via an 'info' connection type");
/*  97:    */     }
/*  98:223 */     ASSearchStrategy searchStrategy = (ASSearchStrategy)((StepManager)infos.get(0)).getInfoStep(ASSearchStrategy.class);
/*  99:    */     
/* 100:225 */     this.m_searchTemplate = searchStrategy.getSearchStrategy();
/* 101:227 */     if ((this.m_searchTemplate instanceof RankedOutputSearch)) {
/* 102:229 */       this.m_isRanking = ((RankedOutputSearch)this.m_searchTemplate).getGenerateRanking();
/* 103:    */     }
/* 104:233 */     if (((this.m_evaluatorTemplate instanceof SubsetEvaluator)) && ((this.m_searchTemplate instanceof Ranker))) {
/* 105:235 */       throw new WekaException("The Ranker search strategy cannot be used with a subset evaluator");
/* 106:    */     }
/* 107:240 */     if (((this.m_evaluatorTemplate instanceof AttributeEvaluator)) && (!(this.m_searchTemplate instanceof Ranker))) {
/* 108:242 */       throw new WekaException("The Ranker search strategy must be used in conjunction with an attribute evaluator");
/* 109:    */     }
/* 110:246 */     this.m_isReset = true;
/* 111:247 */     this.m_waitingTestData.clear();
/* 112:248 */     this.m_selectedAttsStore.clear();
/* 113:249 */     this.m_numToSelectStore.clear();
/* 114:250 */     this.m_transformerStore.clear();
/* 115:251 */     this.m_eval = new AttributeSelection();
/* 116:    */   }
/* 117:    */   
/* 118:    */   public void processIncoming(Data data)
/* 119:    */     throws WekaException
/* 120:    */   {
/* 121:263 */     Instances train = (Instances)data.getPayloadElement("trainingSet");
/* 122:264 */     Instances test = (Instances)data.getPayloadElement("testSet");
/* 123:265 */     Integer setNum = (Integer)data.getPayloadElement("aux_set_num");
/* 124:266 */     Integer maxSetNum = (Integer)data.getPayloadElement("aux_max_set_num");
/* 125:268 */     if (this.m_isReset)
/* 126:    */     {
/* 127:269 */       this.m_isReset = false;
/* 128:270 */       getStepManager().processing();
/* 129:271 */       this.m_setCount = new AtomicInteger(maxSetNum != null ? maxSetNum.intValue() : 1);
/* 130:272 */       if ((setNum != null) && (maxSetNum != null))
/* 131:    */       {
/* 132:273 */         this.m_isDoingXVal = ((maxSetNum.intValue() > 1) && (!this.m_treatXValFoldsSeparately));
/* 133:275 */         if (((this.m_evaluatorTemplate instanceof AttributeTransformer)) && (this.m_isDoingXVal) && (!this.m_treatXValFoldsSeparately)) {
/* 134:277 */           throw new WekaException("Can't cross-validate an attribute transformer");
/* 135:    */         }
/* 136:281 */         if (this.m_isDoingXVal) {
/* 137:282 */           this.m_eval.setFolds(maxSetNum.intValue());
/* 138:    */         }
/* 139:284 */         if (this.m_isRanking) {
/* 140:285 */           this.m_eval.setRanking(this.m_isRanking);
/* 141:    */         }
/* 142:    */       }
/* 143:    */     }
/* 144:290 */     if (this.m_isDoingXVal) {
/* 145:291 */       processXVal(train, test, setNum, maxSetNum);
/* 146:    */     } else {
/* 147:293 */       processNonXVal(train, test, setNum, maxSetNum);
/* 148:    */     }
/* 149:296 */     if (isStopRequested())
/* 150:    */     {
/* 151:297 */       getStepManager().interrupted();
/* 152:    */     }
/* 153:298 */     else if (this.m_setCount.get() == 0)
/* 154:    */     {
/* 155:299 */       if (this.m_isDoingXVal) {
/* 156:    */         try
/* 157:    */         {
/* 158:302 */           StringBuilder builder = new StringBuilder();
/* 159:303 */           builder.append("Search method: ");
/* 160:304 */           String evalS = this.m_evaluatorTemplate.getClass().getCanonicalName();
/* 161:305 */           evalS = evalS.substring(evalS.lastIndexOf('.') + 1, evalS.length());
/* 162:306 */           builder.append(evalS).append(" ").append((this.m_evaluatorTemplate instanceof OptionHandler) ? Utils.joinOptions(((OptionHandler)this.m_evaluatorTemplate).getOptions()) : "").append("\nEvaluator: ");
/* 163:    */           
/* 164:    */ 
/* 165:    */ 
/* 166:    */ 
/* 167:311 */           String searchS = this.m_searchTemplate.getClass().getCanonicalName();
/* 168:312 */           searchS = searchS.substring(searchS.lastIndexOf('.') + 1, searchS.length());
/* 169:    */           
/* 170:314 */           builder.append(searchS).append(" ").append((this.m_searchTemplate instanceof OptionHandler) ? Utils.joinOptions(((OptionHandler)this.m_searchTemplate).getOptions()) : "").append("\n");
/* 171:    */           
/* 172:    */ 
/* 173:    */ 
/* 174:    */ 
/* 175:    */ 
/* 176:320 */           builder.append(this.m_eval.CVResultsString());
/* 177:    */           
/* 178:322 */           outputTextData(builder.toString(), null);
/* 179:    */         }
/* 180:    */         catch (Exception ex)
/* 181:    */         {
/* 182:324 */           throw new WekaException(ex);
/* 183:    */         }
/* 184:    */       }
/* 185:328 */       getStepManager().finished();
/* 186:    */       
/* 187:330 */       this.m_waitingTestData.clear();
/* 188:331 */       this.m_selectedAttsStore.clear();
/* 189:332 */       this.m_numToSelectStore.clear();
/* 190:    */     }
/* 191:    */   }
/* 192:    */   
/* 193:    */   protected void outputTextData(String text, Integer setNum)
/* 194:    */     throws WekaException
/* 195:    */   {
/* 196:345 */     if (isStopRequested()) {
/* 197:346 */       return;
/* 198:    */     }
/* 199:348 */     if (getStepManager().numOutgoingConnectionsOfType("text") == 0) {
/* 200:350 */       return;
/* 201:    */     }
/* 202:353 */     Data textData = new Data("text", text);
/* 203:354 */     String titleString = this.m_evaluatorTemplate.getClass().getCanonicalName();
/* 204:355 */     titleString = titleString.substring(titleString.lastIndexOf('.') + 1, titleString.length());
/* 205:    */     
/* 206:357 */     String searchString = this.m_searchTemplate.getClass().getCanonicalName();
/* 207:358 */     searchString = searchString.substring(searchString.lastIndexOf('.') + 1, searchString.length());
/* 208:    */     
/* 209:360 */     titleString = titleString + " (" + searchString + ")";
/* 210:361 */     textData.setPayloadElement("aux_textTitle", titleString);
/* 211:363 */     if (setNum != null) {
/* 212:364 */       textData.setPayloadElement("aux_set_num", setNum);
/* 213:    */     }
/* 214:367 */     getStepManager().outputData(new Data[] { textData });
/* 215:    */   }
/* 216:    */   
/* 217:    */   protected void processNonXVal(Instances train, Instances test, Integer setNum, Integer maxSetNum)
/* 218:    */     throws WekaException
/* 219:    */   {
/* 220:383 */     if (train != null) {
/* 221:    */       try
/* 222:    */       {
/* 223:385 */         AttributeSelection eval = new AttributeSelection();
/* 224:386 */         ASEvaluation evalCopy = ASEvaluation.makeCopies(this.m_evaluatorTemplate, 1)[0];
/* 225:    */         
/* 226:388 */         ASSearch searchCopy = ASSearch.makeCopies(this.m_searchTemplate, 1)[0];
/* 227:389 */         eval.setEvaluator(evalCopy);
/* 228:390 */         eval.setSearch(searchCopy);
/* 229:391 */         eval.setRanking(this.m_isRanking);
/* 230:393 */         if (!isStopRequested())
/* 231:    */         {
/* 232:394 */           String message = "Selecting attributes (" + train.relationName();
/* 233:395 */           if ((setNum != null) && (maxSetNum != null)) {
/* 234:396 */             message = message + ", set " + setNum + " of " + maxSetNum;
/* 235:    */           }
/* 236:398 */           message = message + ")";
/* 237:399 */           getStepManager().statusMessage(message);
/* 238:400 */           getStepManager().logBasic(message);
/* 239:401 */           eval.SelectAttributes(train);
/* 240:402 */           if ((evalCopy instanceof AttributeTransformer)) {
/* 241:403 */             this.m_transformerStore.put(Integer.valueOf(setNum != null ? setNum.intValue() : -1), (AttributeTransformer)evalCopy);
/* 242:    */           }
/* 243:409 */           int[] selectedAtts = eval.selectedAttributes();
/* 244:411 */           if (this.m_isRanking) {
/* 245:412 */             this.m_numToSelectStore.put(Integer.valueOf(setNum != null ? setNum.intValue() : -1), Integer.valueOf(((RankedOutputSearch)searchCopy).getCalculatedNumToSelect()));
/* 246:    */           }
/* 247:417 */           if (getStepManager().numIncomingConnections() > 2) {
/* 248:418 */             this.m_selectedAttsStore.put(Integer.valueOf(setNum != null ? setNum.intValue() : -1), selectedAtts);
/* 249:    */           }
/* 250:420 */           String results = eval.toResultsString();
/* 251:421 */           outputTextData(results, setNum);
/* 252:422 */           applyFiltering("trainingSet", selectedAtts, train, setNum, maxSetNum);
/* 253:426 */           if (getStepManager().numIncomingConnections() > 2)
/* 254:    */           {
/* 255:427 */             Instances waitingTest = (Instances)this.m_waitingTestData.get(Integer.valueOf(setNum != null ? setNum.intValue() : -1));
/* 256:429 */             if (waitingTest != null) {
/* 257:430 */               checkTestFiltering(waitingTest, Integer.valueOf(setNum != null ? setNum.intValue() : -1), maxSetNum);
/* 258:    */             }
/* 259:    */           }
/* 260:    */           else
/* 261:    */           {
/* 262:434 */             this.m_setCount.decrementAndGet();
/* 263:    */           }
/* 264:436 */           evalCopy.clean();
/* 265:    */         }
/* 266:    */       }
/* 267:    */       catch (Exception ex)
/* 268:    */       {
/* 269:439 */         throw new WekaException(ex);
/* 270:    */       }
/* 271:    */     } else {
/* 272:442 */       checkTestFiltering(test, Integer.valueOf(setNum != null ? setNum.intValue() : -1), maxSetNum);
/* 273:    */     }
/* 274:    */   }
/* 275:    */   
/* 276:    */   protected void processXVal(Instances train, Instances test, Integer setNum, Integer maxSetNum)
/* 277:    */     throws WekaException
/* 278:    */   {
/* 279:459 */     if (train != null) {
/* 280:    */       try
/* 281:    */       {
/* 282:461 */         ASEvaluation evalCopy = ASEvaluation.makeCopies(this.m_evaluatorTemplate, 1)[0];
/* 283:    */         
/* 284:463 */         ASSearch searchCopy = ASSearch.makeCopies(this.m_searchTemplate, 1)[0];
/* 285:464 */         if (!isStopRequested())
/* 286:    */         {
/* 287:465 */           String message = "Selecting attributes x-val mode (" + train.relationName();
/* 288:467 */           if ((setNum != null) && (maxSetNum != null)) {
/* 289:468 */             message = message + ", set " + setNum + " of " + maxSetNum;
/* 290:    */           }
/* 291:470 */           message = message + ")";
/* 292:471 */           getStepManager().statusMessage(message);
/* 293:472 */           getStepManager().logBasic(message);
/* 294:473 */           evalCopy.buildEvaluator(train);
/* 295:474 */           if ((evalCopy instanceof AttributeTransformer)) {
/* 296:475 */             this.m_transformerStore.put(Integer.valueOf(setNum != null ? setNum.intValue() : -1), (AttributeTransformer)evalCopy);
/* 297:    */           }
/* 298:479 */           int[] selectedAtts = searchCopy.search(evalCopy, train);
/* 299:480 */           selectedAtts = evalCopy.postProcess(selectedAtts);
/* 300:481 */           if (this.m_isRanking)
/* 301:    */           {
/* 302:482 */             double[][] ranked = ((RankedOutputSearch)searchCopy).rankedAttributes();
/* 303:    */             
/* 304:484 */             selectedAtts = new int[ranked.length];
/* 305:485 */             for (int i = 0; i < ranked.length; i++) {
/* 306:486 */               selectedAtts[i] = ((int)ranked[i][0]);
/* 307:    */             }
/* 308:    */           }
/* 309:490 */           updateXValStats(train, evalCopy, searchCopy, selectedAtts);
/* 310:493 */           if (getStepManager().numIncomingConnections() > 2) {
/* 311:494 */             this.m_selectedAttsStore.put(setNum, selectedAtts);
/* 312:    */           }
/* 313:496 */           if (this.m_isRanking) {
/* 314:497 */             this.m_numToSelectStore.put(setNum, Integer.valueOf(((RankedOutputSearch)searchCopy).getCalculatedNumToSelect()));
/* 315:    */           }
/* 316:501 */           applyFiltering("trainingSet", selectedAtts, train, setNum, maxSetNum);
/* 317:505 */           if (getStepManager().numIncomingConnections() > 2)
/* 318:    */           {
/* 319:506 */             Instances waitingTest = (Instances)this.m_waitingTestData.get(setNum);
/* 320:507 */             if (waitingTest != null) {
/* 321:508 */               checkTestFiltering(waitingTest, setNum, maxSetNum);
/* 322:    */             }
/* 323:    */           }
/* 324:    */           else
/* 325:    */           {
/* 326:511 */             this.m_setCount.decrementAndGet();
/* 327:    */           }
/* 328:513 */           evalCopy.clean();
/* 329:    */         }
/* 330:    */       }
/* 331:    */       catch (Exception ex)
/* 332:    */       {
/* 333:516 */         throw new WekaException(ex);
/* 334:    */       }
/* 335:    */     } else {
/* 336:519 */       checkTestFiltering(test, setNum, maxSetNum);
/* 337:    */     }
/* 338:    */   }
/* 339:    */   
/* 340:    */   protected synchronized void checkTestFiltering(Instances test, Integer setNum, Integer maxSetNum)
/* 341:    */     throws WekaException
/* 342:    */   {
/* 343:534 */     if (isStopRequested()) {
/* 344:535 */       return;
/* 345:    */     }
/* 346:538 */     int[] selectedForSet = (int[])this.m_selectedAttsStore.get(setNum);
/* 347:539 */     if (selectedForSet == null)
/* 348:    */     {
/* 349:540 */       this.m_waitingTestData.put(setNum, test);
/* 350:    */     }
/* 351:    */     else
/* 352:    */     {
/* 353:542 */       applyFiltering("testSet", selectedForSet, test, setNum, maxSetNum);
/* 354:    */       
/* 355:544 */       this.m_setCount.decrementAndGet();
/* 356:    */     }
/* 357:    */   }
/* 358:    */   
/* 359:    */   protected void applyFiltering(String connType, int[] selectedAtts, Instances data, Integer setNum, Integer maxSetNum)
/* 360:    */     throws WekaException
/* 361:    */   {
/* 362:561 */     if (isStopRequested()) {
/* 363:562 */       return;
/* 364:    */     }
/* 365:565 */     if (getStepManager().numOutgoingConnectionsOfType(connType) == 0) {
/* 366:566 */       return;
/* 367:    */     }
/* 368:569 */     int[] finalSet = new int[selectedAtts.length];
/* 369:570 */     boolean adjust = ((this.m_isDoingXVal) || (this.m_isRanking)) && (((!(this.m_evaluatorTemplate instanceof UnsupervisedSubsetEvaluator)) && (!(this.m_evaluatorTemplate instanceof UnsupervisedAttributeEvaluator))) || ((this.m_evaluatorTemplate instanceof AttributeTransformer)));
/* 370:575 */     if (this.m_isRanking)
/* 371:    */     {
/* 372:576 */       int numToSelect = ((Integer)this.m_numToSelectStore.get(Integer.valueOf(setNum != null ? setNum.intValue() : -1))).intValue();
/* 373:577 */       finalSet = new int[numToSelect];
/* 374:578 */       if (data.classIndex() >= 0) {
/* 375:579 */         if (adjust)
/* 376:    */         {
/* 377:581 */           finalSet = new int[numToSelect + 1];
/* 378:582 */           finalSet[numToSelect] = data.classIndex();
/* 379:    */         }
/* 380:    */         else
/* 381:    */         {
/* 382:584 */           finalSet = new int[numToSelect];
/* 383:    */         }
/* 384:    */       }
/* 385:587 */       for (int i = 0; i < numToSelect; i++) {
/* 386:588 */         finalSet[i] = selectedAtts[i];
/* 387:    */       }
/* 388:    */     }
/* 389:    */     else
/* 390:    */     {
/* 391:591 */       if (adjust)
/* 392:    */       {
/* 393:593 */         finalSet = new int[selectedAtts.length + 1];
/* 394:594 */         finalSet[selectedAtts.length] = data.classIndex();
/* 395:    */       }
/* 396:596 */       for (int i = 0; i < selectedAtts.length; i++) {
/* 397:597 */         finalSet[i] = selectedAtts[i];
/* 398:    */       }
/* 399:    */     }
/* 400:    */     try
/* 401:    */     {
/* 402:601 */       Instances reduced = null;
/* 403:602 */       AttributeTransformer transformer = (AttributeTransformer)this.m_transformerStore.get(Integer.valueOf(setNum != null ? setNum.intValue() : -1));
/* 404:604 */       if (transformer != null)
/* 405:    */       {
/* 406:605 */         reduced = new Instances(transformer.transformedHeader(), data.numInstances());
/* 407:607 */         for (int i = 0; i < data.numInstances(); i++) {
/* 408:608 */           reduced.add(transformer.convertInstance(data.instance(i)));
/* 409:    */         }
/* 410:    */       }
/* 411:    */       else
/* 412:    */       {
/* 413:611 */         Remove r = new Remove();
/* 414:612 */         r.setAttributeIndicesArray(finalSet);
/* 415:613 */         r.setInvertSelection(true);
/* 416:614 */         r.setInputFormat(data);
/* 417:    */         
/* 418:616 */         reduced = Filter.useFilter(data, r);
/* 419:    */       }
/* 420:618 */       if (!isStopRequested())
/* 421:    */       {
/* 422:619 */         String message = "Filtering " + connType + " (" + data.relationName();
/* 423:620 */         if ((setNum != null) && (maxSetNum != null)) {
/* 424:621 */           message = message + ", set " + setNum + " of " + maxSetNum;
/* 425:    */         }
/* 426:623 */         message = message + ")";
/* 427:624 */         getStepManager().statusMessage(message);
/* 428:625 */         getStepManager().logBasic(message);
/* 429:    */         
/* 430:627 */         Data output = new Data(connType, reduced);
/* 431:628 */         output.setPayloadElement("aux_set_num", setNum);
/* 432:629 */         output.setPayloadElement("aux_max_set_num", maxSetNum);
/* 433:    */         
/* 434:631 */         getStepManager().outputData(new Data[] { output });
/* 435:    */       }
/* 436:    */     }
/* 437:    */     catch (Exception ex)
/* 438:    */     {
/* 439:634 */       throw new WekaException(ex);
/* 440:    */     }
/* 441:    */   }
/* 442:    */   
/* 443:    */   protected synchronized void updateXValStats(Instances train, ASEvaluation evaluator, ASSearch search, int[] selectedAtts)
/* 444:    */     throws Exception
/* 445:    */   {
/* 446:652 */     this.m_eval.updateStatsForModelCVSplit(train, evaluator, search, selectedAtts, this.m_isRanking);
/* 447:    */   }
/* 448:    */   
/* 449:    */   public List<String> getIncomingConnectionTypes()
/* 450:    */   {
/* 451:663 */     List<String> result = new ArrayList();
/* 452:665 */     if (getStepManager().numIncomingConnectionsOfType("trainingSet") == 0) {
/* 453:667 */       result.add("trainingSet");
/* 454:    */     }
/* 455:670 */     if ((getStepManager().numIncomingConnectionsOfType("testSet") == 0) && (getStepManager().numIncomingConnectionsOfType("trainingSet") == 1)) {
/* 456:674 */       result.add("testSet");
/* 457:    */     }
/* 458:677 */     if (getStepManager().numIncomingConnectionsOfType("info") == 0) {
/* 459:679 */       result.add("info");
/* 460:    */     }
/* 461:682 */     return result;
/* 462:    */   }
/* 463:    */   
/* 464:    */   public List<String> getOutgoingConnectionTypes()
/* 465:    */   {
/* 466:693 */     List<String> result = new ArrayList();
/* 467:695 */     if ((getStepManager().numIncomingConnections() > 1) && (getStepManager().numIncomingConnectionsOfType("info") == 1)) {
/* 468:697 */       result.add("text");
/* 469:    */     }
/* 470:700 */     if ((getStepManager().numIncomingConnectionsOfType("trainingSet") == 1) && (getStepManager().numIncomingConnectionsOfType("info") == 1)) {
/* 471:704 */       result.add("trainingSet");
/* 472:    */     }
/* 473:707 */     if ((getStepManager().numIncomingConnectionsOfType("testSet") == 1) && (getStepManager().numIncomingConnectionsOfType("info") == 1)) {
/* 474:711 */       result.add("testSet");
/* 475:    */     }
/* 476:714 */     return result;
/* 477:    */   }
/* 478:    */   
/* 479:    */   public String getCustomEditorForStep()
/* 480:    */   {
/* 481:724 */     return "weka.gui.knowledgeflow.steps.ASEvaluatorStepEditorDialog";
/* 482:    */   }
/* 483:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.ASEvaluator
 * JD-Core Version:    0.7.0.1
 */