/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.io.BufferedInputStream;
/*   4:    */ import java.io.BufferedOutputStream;
/*   5:    */ import java.io.File;
/*   6:    */ import java.io.FileInputStream;
/*   7:    */ import java.io.FileOutputStream;
/*   8:    */ import java.io.ObjectInputStream;
/*   9:    */ import java.io.ObjectOutputStream;
/*  10:    */ import java.io.Serializable;
/*  11:    */ import java.util.ArrayList;
/*  12:    */ import java.util.Arrays;
/*  13:    */ import java.util.Collections;
/*  14:    */ import java.util.Comparator;
/*  15:    */ import java.util.HashMap;
/*  16:    */ import java.util.Iterator;
/*  17:    */ import java.util.List;
/*  18:    */ import java.util.Map;
/*  19:    */ import weka.core.Attribute;
/*  20:    */ import weka.core.Environment;
/*  21:    */ import weka.core.Instance;
/*  22:    */ import weka.core.Instances;
/*  23:    */ import weka.core.OptionMetadata;
/*  24:    */ import weka.core.WekaException;
/*  25:    */ import weka.gui.FilePropertyMetadata;
/*  26:    */ import weka.gui.ProgrammaticProperty;
/*  27:    */ import weka.knowledgeflow.Data;
/*  28:    */ import weka.knowledgeflow.ExecutionEnvironment;
/*  29:    */ import weka.knowledgeflow.StepManager;
/*  30:    */ 
/*  31:    */ @KFStep(name="Sorter", category="Tools", toolTipText="Sort instances in ascending or descending order according to the values of user-specified attributes. Instances can be sorted according to multiple attributes (defined in order). Handles datasets larger than can be fit into main memory via instance connections and specifying the in-memory buffer size. Implements a merge-sort by writing the sorted in-memory buffer to a file when full and then interleaving instances from the disk-based file(s) when the incoming stream has finished.", iconPath="weka/gui/knowledgeflow/icons/Sorter.gif")
/*  32:    */ public class Sorter
/*  33:    */   extends BaseStep
/*  34:    */ {
/*  35:    */   private static final long serialVersionUID = 3373283983192467264L;
/*  36:    */   protected transient SortComparator m_sortComparator;
/*  37:    */   protected transient List<InstanceHolder> m_incrementalBuffer;
/*  38:    */   protected transient List<File> m_bufferFiles;
/*  39:    */   protected String m_bufferSize;
/*  40:    */   protected int m_bufferSizeI;
/*  41:    */   protected Map<String, Integer> m_stringAttIndexes;
/*  42:    */   protected String m_sortDetails;
/*  43:    */   protected File m_tempDirectory;
/*  44:    */   protected Instances m_connectedFormat;
/*  45:    */   protected boolean m_isReset;
/*  46:    */   protected boolean m_streaming;
/*  47:    */   protected Data m_streamingData;
/*  48:    */   
/*  49:    */   public Sorter()
/*  50:    */   {
/*  51: 82 */     this.m_bufferSize = "10000";
/*  52:    */     
/*  53:    */ 
/*  54: 85 */     this.m_bufferSizeI = 10000;
/*  55:    */     
/*  56:    */ 
/*  57:    */ 
/*  58:    */ 
/*  59:    */ 
/*  60:    */ 
/*  61:    */ 
/*  62:    */ 
/*  63:    */ 
/*  64:    */ 
/*  65:    */ 
/*  66: 97 */     this.m_tempDirectory = new File("");
/*  67:    */   }
/*  68:    */   
/*  69:    */   public String getBufferSize()
/*  70:    */   {
/*  71:117 */     return this.m_bufferSize;
/*  72:    */   }
/*  73:    */   
/*  74:    */   @OptionMetadata(displayName="Size of in-mem streaming buffer", description="Number of instances to sort in memory before writing to a temp file (instance connections only)", displayOrder=1)
/*  75:    */   public void setBufferSize(String buffSize)
/*  76:    */   {
/*  77:129 */     this.m_bufferSize = buffSize;
/*  78:    */   }
/*  79:    */   
/*  80:    */   @FilePropertyMetadata(fileChooserDialogType=0, directoriesOnly=true)
/*  81:    */   @OptionMetadata(displayName="Directory for temp files", description="Where to store temporary files when spilling to disk", displayOrder=2)
/*  82:    */   public void setTempDirectory(File tempDir)
/*  83:    */   {
/*  84:143 */     this.m_tempDirectory = tempDir;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public File getTempDirectory()
/*  88:    */   {
/*  89:152 */     return this.m_tempDirectory;
/*  90:    */   }
/*  91:    */   
/*  92:    */   @ProgrammaticProperty
/*  93:    */   public void setSortDetails(String sortDetails)
/*  94:    */   {
/*  95:162 */     this.m_sortDetails = sortDetails;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public String getSortDetails()
/*  99:    */   {
/* 100:171 */     return this.m_sortDetails;
/* 101:    */   }
/* 102:    */   
/* 103:    */   public void stepInit()
/* 104:    */     throws WekaException
/* 105:    */   {
/* 106:181 */     this.m_isReset = true;
/* 107:182 */     this.m_streaming = false;
/* 108:183 */     this.m_stringAttIndexes = new HashMap();
/* 109:184 */     this.m_bufferFiles = new ArrayList();
/* 110:185 */     this.m_streamingData = new Data("instance");
/* 111:    */   }
/* 112:    */   
/* 113:    */   public List<String> getIncomingConnectionTypes()
/* 114:    */   {
/* 115:199 */     if (getStepManager().numIncomingConnections() == 0) {
/* 116:200 */       return Arrays.asList(new String[] { "instance", "dataSet", "trainingSet", "testSet" });
/* 117:    */     }
/* 118:204 */     return null;
/* 119:    */   }
/* 120:    */   
/* 121:    */   public List<String> getOutgoingConnectionTypes()
/* 122:    */   {
/* 123:218 */     List<String> result = new ArrayList();
/* 124:219 */     if (getStepManager().numIncomingConnectionsOfType("instance") > 0) {
/* 125:220 */       result.add("instance");
/* 126:    */     }
/* 127:223 */     if (getStepManager().numIncomingConnectionsOfType("dataSet") > 0) {
/* 128:224 */       result.add("dataSet");
/* 129:    */     }
/* 130:227 */     if (getStepManager().numIncomingConnectionsOfType("trainingSet") > 0) {
/* 131:229 */       result.add("trainingSet");
/* 132:    */     }
/* 133:232 */     if (getStepManager().numIncomingConnectionsOfType("testSet") > 0) {
/* 134:233 */       result.add("testSet");
/* 135:    */     }
/* 136:236 */     return result;
/* 137:    */   }
/* 138:    */   
/* 139:    */   protected void init(Instances structure)
/* 140:    */   {
/* 141:245 */     this.m_connectedFormat = structure;
/* 142:246 */     List<SortRule> sortRules = new ArrayList();
/* 143:248 */     if ((this.m_sortDetails != null) && (this.m_sortDetails.length() > 0))
/* 144:    */     {
/* 145:249 */       String[] sortParts = this.m_sortDetails.split("@@sort-rule@@");
/* 146:251 */       for (String s : sortParts)
/* 147:    */       {
/* 148:252 */         SortRule r = new SortRule(s.trim());
/* 149:    */         
/* 150:254 */         r.init(getStepManager().getExecutionEnvironment().getEnvironmentVariables(), structure);
/* 151:    */         
/* 152:256 */         sortRules.add(r);
/* 153:    */       }
/* 154:259 */       this.m_sortComparator = new SortComparator(sortRules);
/* 155:    */     }
/* 156:263 */     this.m_stringAttIndexes = new HashMap();
/* 157:264 */     for (int i = 0; i < structure.numAttributes(); i++) {
/* 158:265 */       if (structure.attribute(i).isString()) {
/* 159:266 */         this.m_stringAttIndexes.put(structure.attribute(i).name(), new Integer(i));
/* 160:    */       }
/* 161:    */     }
/* 162:269 */     if (this.m_stringAttIndexes.size() == 0) {
/* 163:270 */       this.m_stringAttIndexes = null;
/* 164:    */     }
/* 165:273 */     if (this.m_streaming)
/* 166:    */     {
/* 167:274 */       String buffSize = environmentSubstitute(this.m_bufferSize);
/* 168:275 */       this.m_bufferSizeI = Integer.parseInt(buffSize);
/* 169:276 */       this.m_incrementalBuffer = new ArrayList(this.m_bufferSizeI);
/* 170:    */     }
/* 171:    */   }
/* 172:    */   
/* 173:    */   public void processIncoming(Data data)
/* 174:    */     throws WekaException
/* 175:    */   {
/* 176:288 */     if (this.m_isReset)
/* 177:    */     {
/* 178:    */       Instances structure;
/* 179:290 */       if (data.getConnectionName().equals("instance"))
/* 180:    */       {
/* 181:291 */         Instance inst = (Instance)data.getPrimaryPayload();
/* 182:292 */         Instances structure = new Instances(inst.dataset(), 0);
/* 183:293 */         this.m_streaming = true;
/* 184:294 */         getStepManager().logBasic("Starting streaming sort. Using streaming buffer size: " + this.m_bufferSizeI);
/* 185:    */         
/* 186:    */ 
/* 187:297 */         this.m_isReset = false;
/* 188:    */       }
/* 189:    */       else
/* 190:    */       {
/* 191:299 */         structure = (Instances)data.getPrimaryPayload();
/* 192:300 */         structure = new Instances(structure, 0);
/* 193:    */       }
/* 194:302 */       init(structure);
/* 195:    */     }
/* 196:305 */     if (this.m_streaming) {
/* 197:306 */       processIncremental(data);
/* 198:    */     } else {
/* 199:308 */       processBatch(data);
/* 200:    */     }
/* 201:311 */     if (isStopRequested()) {
/* 202:312 */       getStepManager().interrupted();
/* 203:313 */     } else if (!this.m_streaming) {
/* 204:314 */       getStepManager().finished();
/* 205:    */     }
/* 206:    */   }
/* 207:    */   
/* 208:    */   protected void processBatch(Data data)
/* 209:    */     throws WekaException
/* 210:    */   {
/* 211:325 */     getStepManager().processing();
/* 212:    */     
/* 213:327 */     Instances insts = (Instances)data.getPrimaryPayload();
/* 214:328 */     getStepManager().logBasic("Sorting " + insts.relationName());
/* 215:329 */     List<InstanceHolder> instances = new ArrayList();
/* 216:330 */     for (int i = 0; i < insts.numInstances(); i++)
/* 217:    */     {
/* 218:331 */       InstanceHolder h = new InstanceHolder();
/* 219:332 */       h.m_instance = insts.instance(i);
/* 220:333 */       instances.add(h);
/* 221:    */     }
/* 222:335 */     Collections.sort(instances, this.m_sortComparator);
/* 223:336 */     Instances output = new Instances(insts, 0);
/* 224:337 */     for (int i = 0; i < instances.size(); i++) {
/* 225:338 */       output.add(((InstanceHolder)instances.get(i)).m_instance);
/* 226:    */     }
/* 227:341 */     Data outputD = new Data(data.getConnectionName(), output);
/* 228:342 */     outputD.setPayloadElement("aux_set_num", data.getPayloadElement("aux_set_num"));
/* 229:    */     
/* 230:344 */     outputD.setPayloadElement("aux_max_set_num", data.getPayloadElement("aux_max_set_num"));
/* 231:    */     
/* 232:346 */     getStepManager().outputData(new Data[] { outputD });
/* 233:    */   }
/* 234:    */   
/* 235:    */   protected void processIncremental(Data data)
/* 236:    */     throws WekaException
/* 237:    */   {
/* 238:356 */     if (isStopRequested()) {
/* 239:357 */       return;
/* 240:    */     }
/* 241:360 */     if (getStepManager().isStreamFinished(data))
/* 242:    */     {
/* 243:361 */       emitBufferedInstances();
/* 244:    */     }
/* 245:    */     else
/* 246:    */     {
/* 247:363 */       getStepManager().throughputUpdateStart();
/* 248:364 */       InstanceHolder tempH = new InstanceHolder();
/* 249:365 */       tempH.m_instance = ((Instance)data.getPrimaryPayload());
/* 250:366 */       tempH.m_fileNumber = -1;
/* 251:367 */       if (this.m_stringAttIndexes != null) {
/* 252:368 */         copyStringAttVals(tempH);
/* 253:    */       }
/* 254:370 */       this.m_incrementalBuffer.add(tempH);
/* 255:372 */       if (this.m_incrementalBuffer.size() == this.m_bufferSizeI) {
/* 256:    */         try
/* 257:    */         {
/* 258:375 */           sortBuffer(true);
/* 259:    */         }
/* 260:    */         catch (Exception ex)
/* 261:    */         {
/* 262:377 */           throw new WekaException(ex);
/* 263:    */         }
/* 264:    */       }
/* 265:380 */       getStepManager().throughputUpdateEnd();
/* 266:    */     }
/* 267:    */   }
/* 268:    */   
/* 269:    */   protected void emitBufferedInstances()
/* 270:    */     throws WekaException
/* 271:    */   {
/* 272:390 */     if (isStopRequested()) {
/* 273:391 */       return;
/* 274:    */     }
/* 275:394 */     if (this.m_incrementalBuffer.size() > 0)
/* 276:    */     {
/* 277:    */       try
/* 278:    */       {
/* 279:396 */         getStepManager().throughputUpdateStart();
/* 280:397 */         sortBuffer(false);
/* 281:398 */         getStepManager().throughputUpdateEnd();
/* 282:    */       }
/* 283:    */       catch (Exception ex)
/* 284:    */       {
/* 285:400 */         throw new WekaException(ex);
/* 286:    */       }
/* 287:403 */       if (this.m_bufferFiles.size() == 0)
/* 288:    */       {
/* 289:405 */         getStepManager().logDetailed("Emitting in memory buffer");
/* 290:406 */         Instances newHeader = new Instances(((InstanceHolder)this.m_incrementalBuffer.get(0)).m_instance.dataset(), 0);
/* 291:408 */         for (int i = 0; i < this.m_incrementalBuffer.size(); i++)
/* 292:    */         {
/* 293:409 */           getStepManager().throughputUpdateStart();
/* 294:410 */           InstanceHolder currentH = (InstanceHolder)this.m_incrementalBuffer.get(i);
/* 295:411 */           currentH.m_instance.setDataset(newHeader);
/* 296:412 */           if (this.m_stringAttIndexes != null) {
/* 297:413 */             for (String attName : this.m_stringAttIndexes.keySet())
/* 298:    */             {
/* 299:414 */               boolean setValToZero = newHeader.attribute(attName).numValues() > 0;
/* 300:    */               
/* 301:416 */               newHeader.attribute(attName).setStringValue((String)currentH.m_stringVals.get(attName));
/* 302:418 */               if (setValToZero) {
/* 303:419 */                 currentH.m_instance.setValue(newHeader.attribute(attName), 0.0D);
/* 304:    */               }
/* 305:    */             }
/* 306:    */           }
/* 307:423 */           if (isStopRequested()) {
/* 308:424 */             return;
/* 309:    */           }
/* 310:426 */           this.m_streamingData.setPayloadElement("instance", currentH.m_instance);
/* 311:    */           
/* 312:428 */           getStepManager().throughputUpdateEnd();
/* 313:429 */           getStepManager().outputData(new Data[] { this.m_streamingData });
/* 314:430 */           if (i == this.m_incrementalBuffer.size() - 1)
/* 315:    */           {
/* 316:432 */             this.m_streamingData.clearPayload();
/* 317:433 */             getStepManager().throughputFinished(new Data[] { this.m_streamingData });
/* 318:    */           }
/* 319:    */         }
/* 320:436 */         return;
/* 321:    */       }
/* 322:    */     }
/* 323:440 */     List<ObjectInputStream> inputStreams = new ArrayList();
/* 324:    */     
/* 325:442 */     List<InstanceHolder> merger = new ArrayList();
/* 326:    */     
/* 327:444 */     Instances tempHeader = new Instances(this.m_connectedFormat, 0);
/* 328:447 */     if (this.m_incrementalBuffer.size() > 0)
/* 329:    */     {
/* 330:448 */       InstanceHolder tempH = (InstanceHolder)this.m_incrementalBuffer.remove(0);
/* 331:449 */       merger.add(tempH);
/* 332:    */     }
/* 333:452 */     if (isStopRequested()) {
/* 334:453 */       return;
/* 335:    */     }
/* 336:456 */     if (this.m_bufferFiles.size() > 0) {
/* 337:457 */       getStepManager().logDetailed("Merging temp files");
/* 338:    */     }
/* 339:460 */     for (int i = 0; i < this.m_bufferFiles.size(); i++)
/* 340:    */     {
/* 341:461 */       ObjectInputStream ois = null;
/* 342:    */       try
/* 343:    */       {
/* 344:463 */         FileInputStream fis = new FileInputStream((File)this.m_bufferFiles.get(i));
/* 345:464 */         BufferedInputStream bis = new BufferedInputStream(fis, 50000);
/* 346:465 */         ois = new ObjectInputStream(bis);
/* 347:    */         
/* 348:467 */         InstanceHolder tempH = (InstanceHolder)ois.readObject();
/* 349:468 */         if (tempH != null)
/* 350:    */         {
/* 351:469 */           inputStreams.add(ois);
/* 352:    */           
/* 353:471 */           tempH.m_fileNumber = i;
/* 354:472 */           merger.add(tempH);
/* 355:    */         }
/* 356:    */         else
/* 357:    */         {
/* 358:475 */           ois.close();
/* 359:    */         }
/* 360:    */       }
/* 361:    */       catch (Exception ex)
/* 362:    */       {
/* 363:478 */         if (ois != null) {
/* 364:    */           try
/* 365:    */           {
/* 366:480 */             ois.close();
/* 367:    */           }
/* 368:    */           catch (Exception e)
/* 369:    */           {
/* 370:482 */             throw new WekaException(e);
/* 371:    */           }
/* 372:    */         }
/* 373:485 */         throw new WekaException(ex);
/* 374:    */       }
/* 375:    */     }
/* 376:488 */     Collections.sort(merger, this.m_sortComparator);
/* 377:    */     
/* 378:490 */     int mergeCount = 0;
/* 379:    */     do
/* 380:    */     {
/* 381:492 */       if (isStopRequested()) {
/* 382:493 */         return;
/* 383:    */       }
/* 384:495 */       InstanceHolder holder = (InstanceHolder)merger.remove(0);
/* 385:496 */       holder.m_instance.setDataset(tempHeader);
/* 386:498 */       if (this.m_stringAttIndexes != null) {
/* 387:499 */         for (String attName : this.m_stringAttIndexes.keySet())
/* 388:    */         {
/* 389:500 */           boolean setValToZero = tempHeader.attribute(attName).numValues() > 1;
/* 390:    */           
/* 391:502 */           tempHeader.attribute(attName).setStringValue((String)holder.m_stringVals.get(attName));
/* 392:504 */           if (setValToZero) {
/* 393:505 */             holder.m_instance.setValue(tempHeader.attribute(attName), 0.0D);
/* 394:    */           }
/* 395:    */         }
/* 396:    */       }
/* 397:510 */       this.m_streamingData.setPayloadElement("instance", holder.m_instance);
/* 398:    */       
/* 399:512 */       mergeCount++;
/* 400:513 */       getStepManager().outputData(new Data[] { this.m_streamingData });
/* 401:514 */       getStepManager().throughputUpdateStart();
/* 402:516 */       if (mergeCount % this.m_bufferSizeI == 0) {
/* 403:517 */         getStepManager().logDetailed("Merged " + mergeCount + " instances");
/* 404:    */       }
/* 405:519 */       int smallest = holder.m_fileNumber;
/* 406:    */       
/* 407:    */ 
/* 408:522 */       InstanceHolder nextH = null;
/* 409:523 */       if (smallest == -1)
/* 410:    */       {
/* 411:524 */         if (this.m_incrementalBuffer.size() > 0)
/* 412:    */         {
/* 413:525 */           nextH = (InstanceHolder)this.m_incrementalBuffer.remove(0);
/* 414:526 */           nextH.m_fileNumber = -1;
/* 415:    */         }
/* 416:    */       }
/* 417:    */       else
/* 418:    */       {
/* 419:529 */         ObjectInputStream tis = (ObjectInputStream)inputStreams.get(smallest);
/* 420:    */         Iterator i$;
/* 421:    */         try
/* 422:    */         {
/* 423:532 */           InstanceHolder tempH = (InstanceHolder)tis.readObject();
/* 424:533 */           if (tempH != null)
/* 425:    */           {
/* 426:534 */             nextH = tempH;
/* 427:535 */             nextH.m_fileNumber = smallest;
/* 428:    */           }
/* 429:    */           else
/* 430:    */           {
/* 431:537 */             throw new Exception("end of buffer");
/* 432:    */           }
/* 433:    */         }
/* 434:    */         catch (Exception ex)
/* 435:    */         {
/* 436:    */           try
/* 437:    */           {
/* 438:542 */             getStepManager().logDetailed("Closing temp file");
/* 439:543 */             tis.close();
/* 440:    */           }
/* 441:    */           catch (Exception e)
/* 442:    */           {
/* 443:545 */             throw new WekaException(ex);
/* 444:    */           }
/* 445:547 */           File file = (File)this.m_bufferFiles.remove(smallest);
/* 446:    */           
/* 447:549 */           inputStreams.remove(smallest);
/* 448:    */           
/* 449:    */ 
/* 450:552 */           i$ = merger.iterator();
/* 451:    */         }
/* 452:552 */         while (i$.hasNext())
/* 453:    */         {
/* 454:552 */           InstanceHolder h = (InstanceHolder)i$.next();
/* 455:553 */           if ((h.m_fileNumber != -1) && (h.m_fileNumber > smallest)) {
/* 456:554 */             h.m_fileNumber -= 1;
/* 457:    */           }
/* 458:    */         }
/* 459:    */       }
/* 460:560 */       if (nextH != null)
/* 461:    */       {
/* 462:562 */         int index = Collections.binarySearch(merger, nextH, this.m_sortComparator);
/* 463:563 */         if (index < 0) {
/* 464:564 */           merger.add(index * -1 - 1, nextH);
/* 465:    */         } else {
/* 466:566 */           merger.add(index, nextH);
/* 467:    */         }
/* 468:568 */         nextH = null;
/* 469:    */       }
/* 470:570 */       getStepManager().throughputUpdateEnd();
/* 471:571 */     } while ((merger.size() > 0) && (!isStopRequested()));
/* 472:573 */     if (!isStopRequested())
/* 473:    */     {
/* 474:575 */       this.m_streamingData.clearPayload();
/* 475:576 */       getStepManager().throughputFinished(new Data[] { this.m_streamingData });
/* 476:    */     }
/* 477:    */     else
/* 478:    */     {
/* 479:579 */       for (ObjectInputStream is : inputStreams) {
/* 480:    */         try
/* 481:    */         {
/* 482:581 */           is.close();
/* 483:    */         }
/* 484:    */         catch (Exception ex) {}
/* 485:    */       }
/* 486:    */     }
/* 487:    */   }
/* 488:    */   
/* 489:    */   private void sortBuffer(boolean write)
/* 490:    */     throws Exception
/* 491:    */   {
/* 492:596 */     getStepManager().logBasic("Sorting in memory buffer");
/* 493:597 */     Collections.sort(this.m_incrementalBuffer, this.m_sortComparator);
/* 494:598 */     if (!write) {
/* 495:599 */       return;
/* 496:    */     }
/* 497:602 */     if (isStopRequested()) {
/* 498:603 */       return;
/* 499:    */     }
/* 500:606 */     String tmpDir = this.m_tempDirectory.toString();
/* 501:607 */     File tempFile = File.createTempFile("Sorter", ".tmp");
/* 502:609 */     if ((tmpDir != null) && (tmpDir.length() > 0))
/* 503:    */     {
/* 504:610 */       tmpDir = environmentSubstitute(tmpDir);
/* 505:611 */       File tempDir = new File(tmpDir);
/* 506:612 */       if ((tempDir.exists()) && (tempDir.canWrite()))
/* 507:    */       {
/* 508:613 */         String filename = tempFile.getName();
/* 509:614 */         tempFile = new File(tmpDir + File.separator + filename);
/* 510:615 */         tempFile.deleteOnExit();
/* 511:    */       }
/* 512:    */     }
/* 513:618 */     getStepManager().logDebug("Temp file: " + tempFile.toString());
/* 514:    */     
/* 515:620 */     this.m_bufferFiles.add(tempFile);
/* 516:621 */     FileOutputStream fos = new FileOutputStream(tempFile);
/* 517:622 */     BufferedOutputStream bos = new BufferedOutputStream(fos, 50000);
/* 518:623 */     ObjectOutputStream oos = new ObjectOutputStream(bos);
/* 519:624 */     getStepManager().logDetailed("Writing buffer to temp file " + this.m_bufferFiles.size() + ". Buffer contains " + this.m_incrementalBuffer.size() + " instances");
/* 520:628 */     for (int i = 0; i < this.m_incrementalBuffer.size(); i++)
/* 521:    */     {
/* 522:629 */       InstanceHolder temp = (InstanceHolder)this.m_incrementalBuffer.get(i);
/* 523:630 */       temp.m_instance.setDataset(null);
/* 524:631 */       oos.writeObject(temp);
/* 525:632 */       if (i % (this.m_bufferSizeI / 10) == 0) {
/* 526:633 */         oos.reset();
/* 527:    */       }
/* 528:    */     }
/* 529:637 */     bos.flush();
/* 530:638 */     oos.close();
/* 531:639 */     this.m_incrementalBuffer.clear();
/* 532:    */   }
/* 533:    */   
/* 534:    */   private void copyStringAttVals(InstanceHolder holder)
/* 535:    */   {
/* 536:643 */     for (String attName : this.m_stringAttIndexes.keySet())
/* 537:    */     {
/* 538:644 */       Attribute att = holder.m_instance.dataset().attribute(attName);
/* 539:645 */       String val = holder.m_instance.stringValue(att);
/* 540:647 */       if (holder.m_stringVals == null) {
/* 541:648 */         holder.m_stringVals = new HashMap();
/* 542:    */       }
/* 543:651 */       holder.m_stringVals.put(attName, val);
/* 544:    */     }
/* 545:    */   }
/* 546:    */   
/* 547:    */   protected static class InstanceHolder
/* 548:    */     implements Serializable
/* 549:    */   {
/* 550:    */     private static final long serialVersionUID = -3985730394250172995L;
/* 551:    */     protected Instance m_instance;
/* 552:    */     protected int m_fileNumber;
/* 553:    */     protected Map<String, String> m_stringVals;
/* 554:    */   }
/* 555:    */   
/* 556:    */   protected static class SortComparator
/* 557:    */     implements Comparator<Sorter.InstanceHolder>
/* 558:    */   {
/* 559:    */     protected List<Sorter.SortRule> m_sortRules;
/* 560:    */     
/* 561:    */     public SortComparator(List<Sorter.SortRule> sortRules)
/* 562:    */     {
/* 563:693 */       this.m_sortRules = sortRules;
/* 564:    */     }
/* 565:    */     
/* 566:    */     public int compare(Sorter.InstanceHolder o1, Sorter.InstanceHolder o2)
/* 567:    */     {
/* 568:705 */       int cmp = 0;
/* 569:706 */       for (Sorter.SortRule sr : this.m_sortRules)
/* 570:    */       {
/* 571:707 */         cmp = sr.compare(o1, o2);
/* 572:708 */         if (cmp != 0) {
/* 573:709 */           return cmp;
/* 574:    */         }
/* 575:    */       }
/* 576:713 */       return 0;
/* 577:    */     }
/* 578:    */   }
/* 579:    */   
/* 580:    */   public static class SortRule
/* 581:    */     implements Comparator<Sorter.InstanceHolder>
/* 582:    */   {
/* 583:    */     protected String m_attributeNameOrIndex;
/* 584:    */     protected Attribute m_attribute;
/* 585:    */     protected boolean m_descending;
/* 586:    */     
/* 587:    */     public SortRule(String att, boolean descending)
/* 588:    */     {
/* 589:738 */       this.m_attributeNameOrIndex = att;
/* 590:739 */       this.m_descending = descending;
/* 591:    */     }
/* 592:    */     
/* 593:    */     public SortRule() {}
/* 594:    */     
/* 595:    */     public SortRule(String setup)
/* 596:    */     {
/* 597:754 */       parseFromInternal(setup);
/* 598:    */     }
/* 599:    */     
/* 600:    */     protected void parseFromInternal(String setup)
/* 601:    */     {
/* 602:758 */       String[] parts = setup.split("@@SR@@");
/* 603:760 */       if (parts.length != 2) {
/* 604:761 */         throw new IllegalArgumentException("Malformed sort rule: " + setup);
/* 605:    */       }
/* 606:764 */       this.m_attributeNameOrIndex = parts[0].trim();
/* 607:765 */       this.m_descending = parts[1].equalsIgnoreCase("Y");
/* 608:    */     }
/* 609:    */     
/* 610:    */     public String toStringInternal()
/* 611:    */     {
/* 612:774 */       return this.m_attributeNameOrIndex + "@@SR@@" + (this.m_descending ? "Y" : "N");
/* 613:    */     }
/* 614:    */     
/* 615:    */     public String toString()
/* 616:    */     {
/* 617:784 */       StringBuffer res = new StringBuffer();
/* 618:    */       
/* 619:786 */       res.append("Attribute: " + this.m_attributeNameOrIndex + " - sort " + (this.m_descending ? "descending" : "ascending"));
/* 620:    */       
/* 621:    */ 
/* 622:789 */       return res.toString();
/* 623:    */     }
/* 624:    */     
/* 625:    */     public void setAttribute(String att)
/* 626:    */     {
/* 627:798 */       this.m_attributeNameOrIndex = att;
/* 628:    */     }
/* 629:    */     
/* 630:    */     public String getAttribute()
/* 631:    */     {
/* 632:807 */       return this.m_attributeNameOrIndex;
/* 633:    */     }
/* 634:    */     
/* 635:    */     public void setDescending(boolean d)
/* 636:    */     {
/* 637:816 */       this.m_descending = d;
/* 638:    */     }
/* 639:    */     
/* 640:    */     public boolean getDescending()
/* 641:    */     {
/* 642:825 */       return this.m_descending;
/* 643:    */     }
/* 644:    */     
/* 645:    */     public void init(Environment env, Instances structure)
/* 646:    */     {
/* 647:836 */       String attNameI = this.m_attributeNameOrIndex;
/* 648:    */       try
/* 649:    */       {
/* 650:838 */         attNameI = env.substitute(attNameI);
/* 651:    */       }
/* 652:    */       catch (Exception ex) {}
/* 653:842 */       if (attNameI.equalsIgnoreCase("/first"))
/* 654:    */       {
/* 655:843 */         this.m_attribute = structure.attribute(0);
/* 656:    */       }
/* 657:844 */       else if (attNameI.equalsIgnoreCase("/last"))
/* 658:    */       {
/* 659:845 */         this.m_attribute = structure.attribute(structure.numAttributes() - 1);
/* 660:    */       }
/* 661:    */       else
/* 662:    */       {
/* 663:848 */         this.m_attribute = structure.attribute(attNameI);
/* 664:850 */         if (this.m_attribute == null) {
/* 665:    */           try
/* 666:    */           {
/* 667:853 */             int index = Integer.parseInt(attNameI);
/* 668:854 */             this.m_attribute = structure.attribute(index);
/* 669:    */           }
/* 670:    */           catch (NumberFormatException n)
/* 671:    */           {
/* 672:856 */             throw new IllegalArgumentException("Unable to locate attribute " + attNameI + " as either a named attribute or as a valid " + "attribute index");
/* 673:    */           }
/* 674:    */         }
/* 675:    */       }
/* 676:    */     }
/* 677:    */     
/* 678:    */     public int compare(Sorter.InstanceHolder o1, Sorter.InstanceHolder o2)
/* 679:    */     {
/* 680:875 */       if ((o1.m_instance.isMissing(this.m_attribute)) && (o2.m_instance.isMissing(this.m_attribute))) {
/* 681:877 */         return 0;
/* 682:    */       }
/* 683:882 */       if (o1.m_instance.isMissing(this.m_attribute)) {
/* 684:883 */         return 1;
/* 685:    */       }
/* 686:886 */       if (o2.m_instance.isMissing(this.m_attribute)) {
/* 687:887 */         return -1;
/* 688:    */       }
/* 689:890 */       int cmp = 0;
/* 690:892 */       if ((!this.m_attribute.isString()) && (!this.m_attribute.isRelationValued()))
/* 691:    */       {
/* 692:893 */         double val1 = o1.m_instance.value(this.m_attribute);
/* 693:894 */         double val2 = o2.m_instance.value(this.m_attribute);
/* 694:    */         
/* 695:896 */         cmp = Double.compare(val1, val2);
/* 696:    */       }
/* 697:897 */       else if (this.m_attribute.isString())
/* 698:    */       {
/* 699:898 */         String val1 = (String)o1.m_stringVals.get(this.m_attribute.name());
/* 700:899 */         String val2 = (String)o2.m_stringVals.get(this.m_attribute.name());
/* 701:    */         
/* 702:    */ 
/* 703:    */ 
/* 704:    */ 
/* 705:    */ 
/* 706:    */ 
/* 707:    */ 
/* 708:907 */         cmp = val1.compareTo(val2);
/* 709:    */       }
/* 710:    */       else
/* 711:    */       {
/* 712:909 */         throw new IllegalArgumentException("Can't sort according to relation-valued attribute values!");
/* 713:    */       }
/* 714:913 */       if (this.m_descending) {
/* 715:914 */         return -cmp;
/* 716:    */       }
/* 717:917 */       return cmp;
/* 718:    */     }
/* 719:    */   }
/* 720:    */   
/* 721:    */   public String getCustomEditorForStep()
/* 722:    */   {
/* 723:931 */     return "weka.gui.knowledgeflow.steps.SorterStepEditorDialog";
/* 724:    */   }
/* 725:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.Sorter
 * JD-Core Version:    0.7.0.1
 */