/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.HashMap;
/*   6:    */ import java.util.HashSet;
/*   7:    */ import java.util.Iterator;
/*   8:    */ import java.util.LinkedList;
/*   9:    */ import java.util.List;
/*  10:    */ import java.util.Map;
/*  11:    */ import java.util.Map.Entry;
/*  12:    */ import java.util.Queue;
/*  13:    */ import java.util.Set;
/*  14:    */ import java.util.concurrent.atomic.AtomicInteger;
/*  15:    */ import weka.core.Attribute;
/*  16:    */ import weka.core.DenseInstance;
/*  17:    */ import weka.core.Instance;
/*  18:    */ import weka.core.Instances;
/*  19:    */ import weka.core.Range;
/*  20:    */ import weka.core.SerializedObject;
/*  21:    */ import weka.core.WekaException;
/*  22:    */ import weka.knowledgeflow.Data;
/*  23:    */ import weka.knowledgeflow.StepManager;
/*  24:    */ 
/*  25:    */ @KFStep(name="Join", category="Flow", toolTipText="Performs an inner join on two incoming datasets/instance streams (IMPORTANT: assumes that both datasets are sorted in ascending order of the key fields). If data is not sorted then usea Sorter step to sort both into ascending order of the key fields. Does not handle the case wherekeys are not unique in one or both inputs.", iconPath="weka/gui/knowledgeflow/icons/Join.gif")
/*  26:    */ public class Join
/*  27:    */   extends BaseStep
/*  28:    */ {
/*  29:    */   public static final String KEY_SPEC_SEPARATOR = "@@KS@@";
/*  30:    */   private static final long serialVersionUID = -8248954818247532014L;
/*  31:    */   protected StepManager m_firstInput;
/*  32:    */   protected StepManager m_secondInput;
/*  33:    */   protected transient boolean m_firstFinished;
/*  34:    */   protected transient boolean m_secondFinished;
/*  35: 81 */   protected String m_firstInputConnectionType = "";
/*  36: 84 */   protected String m_secondInputConnectionType = "";
/*  37:    */   protected transient Queue<Sorter.InstanceHolder> m_firstBuffer;
/*  38:    */   protected transient Queue<Sorter.InstanceHolder> m_secondBuffer;
/*  39:    */   protected Data m_streamingData;
/*  40:    */   protected transient Instances m_headerOne;
/*  41:    */   protected transient Instances m_headerTwo;
/*  42:    */   protected transient Instances m_mergedHeader;
/*  43:    */   protected transient List<Instances> m_headerPool;
/*  44:    */   protected transient AtomicInteger m_count;
/*  45:    */   protected boolean m_stringAttsPresent;
/*  46:    */   protected boolean m_runningIncrementally;
/*  47:    */   protected int[] m_keyIndexesOne;
/*  48:    */   protected int[] m_keyIndexesTwo;
/*  49:127 */   protected String m_keySpec = "";
/*  50:    */   protected Map<String, Integer> m_stringAttIndexesOne;
/*  51:    */   protected Map<String, Integer> m_stringAttIndexesTwo;
/*  52:    */   protected boolean m_firstIsWaiting;
/*  53:    */   protected boolean m_secondIsWaiting;
/*  54:    */   
/*  55:    */   public void setKeySpec(String ks)
/*  56:    */   {
/*  57:154 */     this.m_keySpec = ks;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public String getKeySpec()
/*  61:    */   {
/*  62:164 */     return this.m_keySpec;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public List<String> getConnectedInputNames()
/*  66:    */   {
/*  67:174 */     establishFirstAndSecondConnectedInputs();
/*  68:    */     
/*  69:176 */     List<String> connected = new ArrayList();
/*  70:177 */     connected.add(this.m_firstInput != null ? this.m_firstInput.getName() : null);
/*  71:178 */     connected.add(this.m_secondInput != null ? this.m_secondInput.getName() : null);
/*  72:    */     
/*  73:180 */     return connected;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public Instances getFirstInputStructure()
/*  77:    */     throws WekaException
/*  78:    */   {
/*  79:190 */     if (this.m_firstInput == null) {
/*  80:191 */       establishFirstAndSecondConnectedInputs();
/*  81:    */     }
/*  82:194 */     if (this.m_firstInput != null) {
/*  83:195 */       return getStepManager().getIncomingStructureFromStep(this.m_firstInput, this.m_firstInputConnectionType);
/*  84:    */     }
/*  85:199 */     return null;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public Instances getSecondInputStructure()
/*  89:    */     throws WekaException
/*  90:    */   {
/*  91:209 */     if (this.m_secondInput == null) {
/*  92:210 */       establishFirstAndSecondConnectedInputs();
/*  93:    */     }
/*  94:213 */     if (this.m_secondInput != null) {
/*  95:214 */       return getStepManager().getIncomingStructureFromStep(this.m_secondInput, this.m_secondInputConnectionType);
/*  96:    */     }
/*  97:218 */     return null;
/*  98:    */   }
/*  99:    */   
/* 100:    */   protected void establishFirstAndSecondConnectedInputs()
/* 101:    */   {
/* 102:225 */     this.m_firstInput = null;
/* 103:226 */     this.m_secondInput = null;
/* 104:227 */     for (Iterator i$ = getStepManager().getIncomingConnections().entrySet().iterator(); i$.hasNext();)
/* 105:    */     {
/* 106:227 */       e = (Map.Entry)i$.next();
/* 107:230 */       if ((this.m_firstInput != null) && (this.m_secondInput != null)) {
/* 108:    */         break;
/* 109:    */       }
/* 110:234 */       for (StepManager m : (List)e.getValue())
/* 111:    */       {
/* 112:235 */         if (this.m_firstInput == null)
/* 113:    */         {
/* 114:236 */           this.m_firstInput = m;
/* 115:237 */           this.m_firstInputConnectionType = ((String)e.getKey());
/* 116:    */         }
/* 117:238 */         else if (this.m_secondInput == null)
/* 118:    */         {
/* 119:239 */           this.m_secondInput = m;
/* 120:240 */           this.m_secondInputConnectionType = ((String)e.getKey());
/* 121:    */         }
/* 122:243 */         if ((this.m_firstInput != null) && (this.m_secondInput != null)) {
/* 123:    */           break;
/* 124:    */         }
/* 125:    */       }
/* 126:    */     }
/* 127:    */     Map.Entry<String, List<StepManager>> e;
/* 128:    */   }
/* 129:    */   
/* 130:    */   public void stepInit()
/* 131:    */     throws WekaException
/* 132:    */   {
/* 133:257 */     this.m_firstBuffer = new LinkedList();
/* 134:258 */     this.m_secondBuffer = new LinkedList();
/* 135:259 */     this.m_streamingData = new Data("instance");
/* 136:260 */     this.m_firstInput = null;
/* 137:261 */     this.m_secondInput = null;
/* 138:262 */     this.m_headerOne = null;
/* 139:263 */     this.m_headerTwo = null;
/* 140:264 */     this.m_firstFinished = false;
/* 141:265 */     this.m_secondFinished = false;
/* 142:267 */     if (getStepManager().numIncomingConnections() < 2) {
/* 143:268 */       throw new WekaException("Two incoming connections are required for the Join step");
/* 144:    */     }
/* 145:272 */     establishFirstAndSecondConnectedInputs();
/* 146:    */   }
/* 147:    */   
/* 148:    */   public void processIncoming(Data data)
/* 149:    */     throws WekaException
/* 150:    */   {
/* 151:284 */     if (data.getConnectionName().equals("instance"))
/* 152:    */     {
/* 153:285 */       processStreaming(data);
/* 154:286 */       if (isStopRequested()) {
/* 155:287 */         getStepManager().interrupted();
/* 156:    */       }
/* 157:    */     }
/* 158:    */     else
/* 159:    */     {
/* 160:290 */       processBatch(data);
/* 161:291 */       if (isStopRequested()) {
/* 162:292 */         getStepManager().interrupted();
/* 163:    */       }
/* 164:294 */       return;
/* 165:    */     }
/* 166:    */   }
/* 167:    */   
/* 168:    */   protected synchronized void processStreaming(Data data)
/* 169:    */     throws WekaException
/* 170:    */   {
/* 171:305 */     if (isStopRequested()) {
/* 172:306 */       return;
/* 173:    */     }
/* 174:309 */     if (getStepManager().isStreamFinished(data))
/* 175:    */     {
/* 176:310 */       if (data.getSourceStep().getStepManager() == this.m_firstInput)
/* 177:    */       {
/* 178:311 */         this.m_firstFinished = true;
/* 179:312 */         getStepManager().logBasic("Finished receiving from " + this.m_firstInput.getName());
/* 180:    */       }
/* 181:314 */       else if (data.getSourceStep().getStepManager() == this.m_secondInput)
/* 182:    */       {
/* 183:315 */         this.m_secondFinished = true;
/* 184:316 */         getStepManager().logBasic("Finished receiving from " + this.m_secondInput.getName());
/* 185:    */       }
/* 186:320 */       if ((this.m_firstFinished) && (this.m_secondFinished))
/* 187:    */       {
/* 188:321 */         clearBuffers();
/* 189:322 */         this.m_streamingData.clearPayload();
/* 190:323 */         getStepManager().throughputFinished(new Data[] { this.m_streamingData });
/* 191:    */       }
/* 192:326 */       return;
/* 193:    */     }
/* 194:329 */     Instance inst = (Instance)data.getPrimaryPayload();
/* 195:330 */     StepManager source = data.getSourceStep().getStepManager();
/* 196:331 */     if ((this.m_headerOne == null) || (this.m_headerTwo == null))
/* 197:    */     {
/* 198:332 */       if ((this.m_headerOne == null) && (source == this.m_firstInput))
/* 199:    */       {
/* 200:333 */         this.m_headerOne = new Instances(inst.dataset(), 0);
/* 201:334 */         getStepManager().logBasic("Initializing buffer for " + this.m_firstInput.getName());
/* 202:    */         
/* 203:336 */         this.m_stringAttIndexesOne = new HashMap();
/* 204:337 */         for (int i = 0; i < this.m_headerOne.numAttributes(); i++) {
/* 205:338 */           if (this.m_headerOne.attribute(i).isString()) {
/* 206:339 */             this.m_stringAttIndexesOne.put(this.m_headerOne.attribute(i).name(), Integer.valueOf(i));
/* 207:    */           }
/* 208:    */         }
/* 209:    */       }
/* 210:344 */       if ((this.m_headerTwo == null) && (source == this.m_secondInput))
/* 211:    */       {
/* 212:345 */         this.m_headerTwo = new Instances(inst.dataset(), 0);
/* 213:346 */         getStepManager().logBasic("Initializing buffer for " + this.m_secondInput.getName());
/* 214:    */         
/* 215:348 */         this.m_stringAttIndexesTwo = new HashMap();
/* 216:349 */         for (int i = 0; i < this.m_headerTwo.numAttributes(); i++) {
/* 217:350 */           if (this.m_headerTwo.attribute(i).isString()) {
/* 218:351 */             this.m_stringAttIndexesTwo.put(this.m_headerTwo.attribute(i).name(), Integer.valueOf(i));
/* 219:    */           }
/* 220:    */         }
/* 221:    */       }
/* 222:356 */       if (this.m_mergedHeader == null) {
/* 223:358 */         if ((this.m_headerOne != null) && (this.m_headerTwo != null) && (this.m_keySpec != null) && (this.m_keySpec.length() > 0)) {
/* 224:362 */           generateMergedHeader();
/* 225:    */         }
/* 226:    */       }
/* 227:    */     }
/* 228:367 */     if (source == this.m_firstInput) {
/* 229:368 */       addToFirstBuffer(inst);
/* 230:    */     } else {
/* 231:370 */       addToSecondBuffer(inst);
/* 232:    */     }
/* 233:373 */     if ((source == this.m_firstInput) && (this.m_secondBuffer.size() <= 100) && (this.m_secondIsWaiting))
/* 234:    */     {
/* 235:375 */       this.m_secondIsWaiting = false;
/* 236:376 */       notifyAll();
/* 237:    */     }
/* 238:377 */     else if ((source == this.m_secondInput) && (this.m_secondBuffer.size() <= 100) && (this.m_firstIsWaiting))
/* 239:    */     {
/* 240:379 */       this.m_firstIsWaiting = false;
/* 241:380 */       notifyAll();
/* 242:    */     }
/* 243:383 */     if (isStopRequested()) {
/* 244:384 */       return;
/* 245:    */     }
/* 246:387 */     Instance outputI = processBuffers();
/* 247:388 */     if (outputI != null)
/* 248:    */     {
/* 249:389 */       getStepManager().throughputUpdateStart();
/* 250:390 */       this.m_streamingData.setPayloadElement("instance", outputI);
/* 251:391 */       getStepManager().outputData(new Data[] { this.m_streamingData });
/* 252:392 */       getStepManager().throughputUpdateEnd();
/* 253:    */     }
/* 254:    */   }
/* 255:    */   
/* 256:    */   private static void copyStringAttVals(Sorter.InstanceHolder holder, Map<String, Integer> stringAttIndexes)
/* 257:    */   {
/* 258:407 */     for (String attName : stringAttIndexes.keySet())
/* 259:    */     {
/* 260:408 */       Attribute att = holder.m_instance.dataset().attribute(attName);
/* 261:409 */       String val = holder.m_instance.stringValue(att);
/* 262:411 */       if (holder.m_stringVals == null) {
/* 263:412 */         holder.m_stringVals = new HashMap();
/* 264:    */       }
/* 265:415 */       holder.m_stringVals.put(attName, val);
/* 266:    */     }
/* 267:    */   }
/* 268:    */   
/* 269:    */   protected synchronized void addToFirstBuffer(Instance inst)
/* 270:    */   {
/* 271:425 */     if (isStopRequested()) {
/* 272:426 */       return;
/* 273:    */     }
/* 274:429 */     Sorter.InstanceHolder newH = new Sorter.InstanceHolder();
/* 275:430 */     newH.m_instance = inst;
/* 276:431 */     copyStringAttVals(newH, this.m_stringAttIndexesOne);
/* 277:432 */     this.m_firstBuffer.add(newH);
/* 278:434 */     if ((this.m_firstBuffer.size() > 100) && (!this.m_secondFinished)) {
/* 279:    */       try
/* 280:    */       {
/* 281:436 */         this.m_firstIsWaiting = true;
/* 282:437 */         wait();
/* 283:    */       }
/* 284:    */       catch (InterruptedException ex) {}
/* 285:    */     }
/* 286:    */   }
/* 287:    */   
/* 288:    */   protected synchronized void addToSecondBuffer(Instance inst)
/* 289:    */   {
/* 290:450 */     if (isStopRequested()) {
/* 291:451 */       return;
/* 292:    */     }
/* 293:454 */     Sorter.InstanceHolder newH = new Sorter.InstanceHolder();
/* 294:455 */     newH.m_instance = inst;
/* 295:456 */     copyStringAttVals(newH, this.m_stringAttIndexesTwo);
/* 296:457 */     this.m_secondBuffer.add(newH);
/* 297:459 */     if ((this.m_secondBuffer.size() > 100) && (!this.m_firstFinished)) {
/* 298:    */       try
/* 299:    */       {
/* 300:461 */         this.m_secondIsWaiting = true;
/* 301:462 */         wait();
/* 302:    */       }
/* 303:    */       catch (InterruptedException e) {}
/* 304:    */     }
/* 305:    */   }
/* 306:    */   
/* 307:    */   protected synchronized void clearBuffers()
/* 308:    */     throws WekaException
/* 309:    */   {
/* 310:475 */     while ((this.m_firstBuffer.size() > 0) && (this.m_secondBuffer.size() > 0))
/* 311:    */     {
/* 312:476 */       if (isStopRequested()) {
/* 313:477 */         return;
/* 314:    */       }
/* 315:479 */       getStepManager().throughputUpdateStart();
/* 316:480 */       Instance newInst = processBuffers();
/* 317:481 */       getStepManager().throughputUpdateEnd();
/* 318:482 */       this.m_streamingData.setPayloadElement("instance", newInst);
/* 319:483 */       getStepManager().outputData(new Data[] { this.m_streamingData });
/* 320:    */     }
/* 321:    */   }
/* 322:    */   
/* 323:    */   protected synchronized void processBatch(Data data)
/* 324:    */     throws WekaException
/* 325:    */   {
/* 326:494 */     Instances insts = (Instances)data.getPrimaryPayload();
/* 327:496 */     if (data.getSourceStep().getStepManager() == this.m_firstInput)
/* 328:    */     {
/* 329:497 */       this.m_headerOne = new Instances(insts, 0);
/* 330:498 */       getStepManager().logDetailed("Receiving batch from " + this.m_firstInput.getName());
/* 331:501 */       for (int i = 0; (i < insts.numInstances()) && (!isStopRequested()); i++)
/* 332:    */       {
/* 333:502 */         Sorter.InstanceHolder tempH = new Sorter.InstanceHolder();
/* 334:503 */         tempH.m_instance = insts.instance(i);
/* 335:504 */         this.m_firstBuffer.add(tempH);
/* 336:    */       }
/* 337:    */     }
/* 338:506 */     else if (data.getSourceStep().getStepManager() == this.m_secondInput)
/* 339:    */     {
/* 340:507 */       this.m_headerTwo = new Instances(insts, 0);
/* 341:508 */       getStepManager().logDetailed("Receiving batch from " + this.m_secondInput.getName());
/* 342:510 */       for (int i = 0; (i < insts.numInstances()) && (!isStopRequested()); i++)
/* 343:    */       {
/* 344:511 */         Sorter.InstanceHolder tempH = new Sorter.InstanceHolder();
/* 345:512 */         tempH.m_instance = insts.instance(i);
/* 346:513 */         this.m_secondBuffer.add(tempH);
/* 347:    */       }
/* 348:    */     }
/* 349:    */     else
/* 350:    */     {
/* 351:516 */       throw new WekaException("This should never happen");
/* 352:    */     }
/* 353:519 */     if ((this.m_firstBuffer.size() > 0) && (this.m_secondBuffer.size() > 0))
/* 354:    */     {
/* 355:520 */       getStepManager().processing();
/* 356:521 */       generateMergedHeader();
/* 357:    */       
/* 358:523 */       Instances newData = new Instances(this.m_mergedHeader, 0);
/* 359:525 */       while ((!isStopRequested()) && (this.m_firstBuffer.size() > 0) && (this.m_secondBuffer.size() > 0))
/* 360:    */       {
/* 361:526 */         Instance newI = processBuffers();
/* 362:527 */         if (newI != null) {
/* 363:528 */           newData.add(newI);
/* 364:    */         }
/* 365:    */       }
/* 366:532 */       for (String outConnType : getStepManager().getOutgoingConnections().keySet())
/* 367:    */       {
/* 368:534 */         if (isStopRequested()) {
/* 369:535 */           return;
/* 370:    */         }
/* 371:537 */         Data outputD = new Data(outConnType, newData);
/* 372:538 */         outputD.setPayloadElement("aux_set_num", Integer.valueOf(1));
/* 373:539 */         outputD.setPayloadElement("aux_max_set_num", Integer.valueOf(1));
/* 374:    */         
/* 375:541 */         getStepManager().outputData(new Data[] { outputD });
/* 376:    */       }
/* 377:543 */       getStepManager().finished();
/* 378:    */     }
/* 379:    */   }
/* 380:    */   
/* 381:    */   protected synchronized Instance processBuffers()
/* 382:    */   {
/* 383:554 */     if ((this.m_firstBuffer.size() > 0) && (this.m_secondBuffer.size() > 0))
/* 384:    */     {
/* 385:555 */       Sorter.InstanceHolder firstH = (Sorter.InstanceHolder)this.m_firstBuffer.peek();
/* 386:556 */       Sorter.InstanceHolder secondH = (Sorter.InstanceHolder)this.m_secondBuffer.peek();
/* 387:557 */       Instance first = firstH.m_instance;
/* 388:558 */       Instance second = secondH.m_instance;
/* 389:    */       
/* 390:560 */       int cmp = compare(first, second, firstH, secondH);
/* 391:561 */       if (cmp == 0)
/* 392:    */       {
/* 393:563 */         Instance newInst = generateMergedInstance((Sorter.InstanceHolder)this.m_firstBuffer.remove(), (Sorter.InstanceHolder)this.m_secondBuffer.remove());
/* 394:    */         
/* 395:    */ 
/* 396:    */ 
/* 397:567 */         return newInst;
/* 398:    */       }
/* 399:568 */       if (cmp < 0) {
/* 400:    */         do
/* 401:    */         {
/* 402:571 */           this.m_firstBuffer.remove();
/* 403:572 */           if (this.m_firstBuffer.size() > 0)
/* 404:    */           {
/* 405:573 */             firstH = (Sorter.InstanceHolder)this.m_firstBuffer.peek();
/* 406:574 */             first = firstH.m_instance;
/* 407:575 */             cmp = compare(first, second, firstH, secondH);
/* 408:    */           }
/* 409:577 */           if (cmp >= 0) {
/* 410:    */             break;
/* 411:    */           }
/* 412:577 */         } while (this.m_firstBuffer.size() > 0);
/* 413:    */       } else {
/* 414:    */         do
/* 415:    */         {
/* 416:581 */           this.m_secondBuffer.remove();
/* 417:582 */           if (this.m_secondBuffer.size() > 0)
/* 418:    */           {
/* 419:583 */             secondH = (Sorter.InstanceHolder)this.m_secondBuffer.peek();
/* 420:584 */             second = secondH.m_instance;
/* 421:585 */             cmp = compare(first, second, firstH, secondH);
/* 422:    */           }
/* 423:587 */         } while ((cmp > 0) && (this.m_secondBuffer.size() > 0));
/* 424:    */       }
/* 425:    */     }
/* 426:591 */     return null;
/* 427:    */   }
/* 428:    */   
/* 429:    */   protected int compare(Instance one, Instance two, Sorter.InstanceHolder oneH, Sorter.InstanceHolder twoH)
/* 430:    */   {
/* 431:607 */     for (int i = 0; i < this.m_keyIndexesOne.length; i++) {
/* 432:608 */       if ((!one.isMissing(this.m_keyIndexesOne[i])) || (!two.isMissing(this.m_keyIndexesTwo[i])))
/* 433:    */       {
/* 434:613 */         if ((one.isMissing(this.m_keyIndexesOne[i])) || (two.isMissing(this.m_keyIndexesTwo[i])))
/* 435:    */         {
/* 436:617 */           if (one.isMissing(this.m_keyIndexesOne[i])) {
/* 437:618 */             return -1;
/* 438:    */           }
/* 439:620 */           return 1;
/* 440:    */         }
/* 441:624 */         if (this.m_mergedHeader.attribute(this.m_keyIndexesOne[i]).isNumeric())
/* 442:    */         {
/* 443:625 */           double v1 = one.value(this.m_keyIndexesOne[i]);
/* 444:626 */           double v2 = two.value(this.m_keyIndexesTwo[i]);
/* 445:628 */           if (v1 != v2) {
/* 446:629 */             return v1 < v2 ? -1 : 1;
/* 447:    */           }
/* 448:    */         }
/* 449:631 */         else if (this.m_mergedHeader.attribute(this.m_keyIndexesOne[i]).isNominal())
/* 450:    */         {
/* 451:632 */           String oneS = one.stringValue(this.m_keyIndexesOne[i]);
/* 452:633 */           String twoS = two.stringValue(this.m_keyIndexesTwo[i]);
/* 453:    */           
/* 454:635 */           int cmp = oneS.compareTo(twoS);
/* 455:637 */           if (cmp != 0) {
/* 456:638 */             return cmp;
/* 457:    */           }
/* 458:    */         }
/* 459:640 */         else if (this.m_mergedHeader.attribute(this.m_keyIndexesOne[i]).isString())
/* 460:    */         {
/* 461:641 */           String attNameOne = this.m_mergedHeader.attribute(this.m_keyIndexesOne[i]).name();
/* 462:642 */           String attNameTwo = this.m_mergedHeader.attribute(this.m_keyIndexesTwo[i]).name();
/* 463:    */           
/* 464:644 */           String oneS = (oneH.m_stringVals == null) || (oneH.m_stringVals.size() == 0) ? one.stringValue(this.m_keyIndexesOne[i]) : (String)oneH.m_stringVals.get(attNameOne);
/* 465:    */           
/* 466:    */ 
/* 467:    */ 
/* 468:648 */           String twoS = (twoH.m_stringVals == null) || (twoH.m_stringVals.size() == 0) ? two.stringValue(this.m_keyIndexesTwo[i]) : (String)twoH.m_stringVals.get(attNameTwo);
/* 469:    */           
/* 470:    */ 
/* 471:    */ 
/* 472:    */ 
/* 473:653 */           int cmp = oneS.compareTo(twoS);
/* 474:655 */           if (cmp != 0) {
/* 475:656 */             return cmp;
/* 476:    */           }
/* 477:    */         }
/* 478:    */       }
/* 479:    */     }
/* 480:661 */     return 0;
/* 481:    */   }
/* 482:    */   
/* 483:    */   protected synchronized Instance generateMergedInstance(Sorter.InstanceHolder one, Sorter.InstanceHolder two)
/* 484:    */   {
/* 485:675 */     double[] vals = new double[this.m_mergedHeader.numAttributes()];
/* 486:676 */     int count = 0;
/* 487:677 */     Instances currentStructure = this.m_mergedHeader;
/* 488:679 */     if ((this.m_runningIncrementally) && (this.m_stringAttsPresent)) {
/* 489:680 */       currentStructure = (Instances)this.m_headerPool.get(this.m_count.getAndIncrement() % 10);
/* 490:    */     }
/* 491:683 */     for (int i = 0; i < this.m_headerOne.numAttributes(); i++)
/* 492:    */     {
/* 493:684 */       vals[count] = one.m_instance.value(i);
/* 494:685 */       if ((one.m_stringVals != null) && (one.m_stringVals.size() > 0) && (this.m_mergedHeader.attribute(count).isString()))
/* 495:    */       {
/* 496:687 */         String valToSetInHeader = (String)one.m_stringVals.get(one.m_instance.attribute(i).name());
/* 497:    */         
/* 498:689 */         currentStructure.attribute(count).setStringValue(valToSetInHeader);
/* 499:690 */         vals[count] = 0.0D;
/* 500:    */       }
/* 501:692 */       count++;
/* 502:    */     }
/* 503:695 */     for (int i = 0; i < this.m_headerTwo.numAttributes(); i++)
/* 504:    */     {
/* 505:696 */       vals[count] = two.m_instance.value(i);
/* 506:697 */       if ((two.m_stringVals != null) && (two.m_stringVals.size() > 0) && (this.m_mergedHeader.attribute(count).isString()))
/* 507:    */       {
/* 508:699 */         String valToSetInHeader = (String)one.m_stringVals.get(two.m_instance.attribute(i).name());
/* 509:    */         
/* 510:701 */         currentStructure.attribute(count).setStringValue(valToSetInHeader);
/* 511:702 */         vals[count] = 0.0D;
/* 512:    */       }
/* 513:705 */       count++;
/* 514:    */     }
/* 515:708 */     Instance newInst = new DenseInstance(1.0D, vals);
/* 516:709 */     newInst.setDataset(currentStructure);
/* 517:    */     
/* 518:711 */     return newInst;
/* 519:    */   }
/* 520:    */   
/* 521:    */   protected void generateMergedHeader()
/* 522:    */     throws WekaException
/* 523:    */   {
/* 524:720 */     if ((this.m_keySpec == null) || (this.m_keySpec.length() == 0)) {
/* 525:721 */       throw new WekaException("Key fields are null!");
/* 526:    */     }
/* 527:724 */     String resolvedKeySpec = this.m_keySpec;
/* 528:725 */     resolvedKeySpec = environmentSubstitute(resolvedKeySpec);
/* 529:    */     
/* 530:727 */     String[] parts = resolvedKeySpec.split("@@KS@@");
/* 531:728 */     if (parts.length != 2) {
/* 532:729 */       throw new WekaException("Invalid key specification");
/* 533:    */     }
/* 534:733 */     for (int i = 0; i < 2; i++)
/* 535:    */     {
/* 536:734 */       String rangeS = parts[i].trim();
/* 537:    */       
/* 538:736 */       Range r = new Range();
/* 539:737 */       r.setUpper(i == 0 ? this.m_headerOne.numAttributes() : this.m_headerTwo.numAttributes());
/* 540:    */       try
/* 541:    */       {
/* 542:740 */         r.setRanges(rangeS);
/* 543:741 */         if (i == 0) {
/* 544:742 */           this.m_keyIndexesOne = r.getSelection();
/* 545:    */         } else {
/* 546:744 */           this.m_keyIndexesTwo = r.getSelection();
/* 547:    */         }
/* 548:    */       }
/* 549:    */       catch (IllegalArgumentException e)
/* 550:    */       {
/* 551:748 */         String[] names = rangeS.split(",");
/* 552:749 */         if (i == 0) {
/* 553:750 */           this.m_keyIndexesOne = new int[names.length];
/* 554:    */         } else {
/* 555:752 */           this.m_keyIndexesTwo = new int[names.length];
/* 556:    */         }
/* 557:755 */         for (int j = 0; j < names.length; j++)
/* 558:    */         {
/* 559:756 */           String aName = names[j].trim();
/* 560:757 */           Attribute anAtt = i == 0 ? this.m_headerOne.attribute(aName) : this.m_headerTwo.attribute(aName);
/* 561:761 */           if (anAtt == null) {
/* 562:762 */             throw new WekaException("Invalid key attribute name");
/* 563:    */           }
/* 564:765 */           if (i == 0) {
/* 565:766 */             this.m_keyIndexesOne[j] = anAtt.index();
/* 566:    */           } else {
/* 567:768 */             this.m_keyIndexesTwo[j] = anAtt.index();
/* 568:    */           }
/* 569:    */         }
/* 570:    */       }
/* 571:    */     }
/* 572:774 */     if ((this.m_keyIndexesOne == null) || (this.m_keyIndexesTwo == null)) {
/* 573:775 */       throw new WekaException("Key fields are null!");
/* 574:    */     }
/* 575:778 */     if (this.m_keyIndexesOne.length != this.m_keyIndexesTwo.length) {
/* 576:779 */       throw new WekaException("Number of key fields are different for each input");
/* 577:    */     }
/* 578:784 */     for (int i = 0; i < this.m_keyIndexesOne.length; i++) {
/* 579:785 */       if (this.m_headerOne.attribute(this.m_keyIndexesOne[i]).type() != this.m_headerTwo.attribute(this.m_keyIndexesTwo[i]).type()) {
/* 580:787 */         throw new WekaException("Type of key corresponding to key fields differ: input 1 - " + Attribute.typeToStringShort(this.m_headerOne.attribute(this.m_keyIndexesOne[i])) + " input 2 - " + Attribute.typeToStringShort(this.m_headerTwo.attribute(this.m_keyIndexesTwo[i])));
/* 581:    */       }
/* 582:    */     }
/* 583:797 */     ArrayList<Attribute> newAtts = new ArrayList();
/* 584:    */     
/* 585:799 */     Set<String> nameLookup = new HashSet();
/* 586:800 */     for (int i = 0; i < this.m_headerOne.numAttributes(); i++)
/* 587:    */     {
/* 588:801 */       newAtts.add((Attribute)this.m_headerOne.attribute(i).copy());
/* 589:802 */       nameLookup.add(this.m_headerOne.attribute(i).name());
/* 590:    */     }
/* 591:805 */     for (int i = 0; i < this.m_headerTwo.numAttributes(); i++)
/* 592:    */     {
/* 593:806 */       String name = this.m_headerTwo.attribute(i).name();
/* 594:807 */       if (nameLookup.contains(name)) {
/* 595:808 */         name = name + "_2";
/* 596:    */       }
/* 597:811 */       newAtts.add(this.m_headerTwo.attribute(i).copy(name));
/* 598:    */     }
/* 599:814 */     this.m_mergedHeader = new Instances(this.m_headerOne.relationName() + "+" + this.m_headerTwo.relationName(), newAtts, 0);
/* 600:    */     
/* 601:    */ 
/* 602:    */ 
/* 603:818 */     this.m_stringAttsPresent = false;
/* 604:819 */     if (this.m_mergedHeader.checkForStringAttributes())
/* 605:    */     {
/* 606:820 */       this.m_stringAttsPresent = true;
/* 607:821 */       this.m_headerPool = new ArrayList();
/* 608:822 */       this.m_count = new AtomicInteger();
/* 609:823 */       for (int i = 0; i < 10; i++) {
/* 610:    */         try
/* 611:    */         {
/* 612:825 */           this.m_headerPool.add((Instances)new SerializedObject(this.m_mergedHeader).getObject());
/* 613:    */         }
/* 614:    */         catch (Exception e)
/* 615:    */         {
/* 616:829 */           e.printStackTrace();
/* 617:    */         }
/* 618:    */       }
/* 619:    */     }
/* 620:    */   }
/* 621:    */   
/* 622:    */   public List<String> getIncomingConnectionTypes()
/* 623:    */   {
/* 624:846 */     List<String> result = new ArrayList();
/* 625:848 */     if (getStepManager().numIncomingConnections() == 0) {
/* 626:849 */       return Arrays.asList(new String[] { "instance", "dataSet", "trainingSet", "testSet" });
/* 627:    */     }
/* 628:853 */     if (getStepManager().numIncomingConnections() == 1)
/* 629:    */     {
/* 630:854 */       result.addAll(getStepManager().getIncomingConnections().keySet());
/* 631:855 */       return result;
/* 632:    */     }
/* 633:858 */     return null;
/* 634:    */   }
/* 635:    */   
/* 636:    */   public List<String> getOutgoingConnectionTypes()
/* 637:    */   {
/* 638:872 */     if (getStepManager().numIncomingConnections() > 0)
/* 639:    */     {
/* 640:874 */       List<String> result = new ArrayList();
/* 641:875 */       result.addAll(getStepManager().getIncomingConnections().keySet());
/* 642:876 */       return result;
/* 643:    */     }
/* 644:879 */     return null;
/* 645:    */   }
/* 646:    */   
/* 647:    */   public String getCustomEditorForStep()
/* 648:    */   {
/* 649:892 */     return "weka.gui.knowledgeflow.steps.JoinStepEditorDialog";
/* 650:    */   }
/* 651:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.Join
 * JD-Core Version:    0.7.0.1
 */