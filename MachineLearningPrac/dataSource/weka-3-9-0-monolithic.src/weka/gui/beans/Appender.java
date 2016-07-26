/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.beans.EventSetDescriptor;
/*   5:    */ import java.io.BufferedInputStream;
/*   6:    */ import java.io.BufferedOutputStream;
/*   7:    */ import java.io.File;
/*   8:    */ import java.io.FileInputStream;
/*   9:    */ import java.io.FileOutputStream;
/*  10:    */ import java.io.IOException;
/*  11:    */ import java.io.ObjectInputStream;
/*  12:    */ import java.io.ObjectOutputStream;
/*  13:    */ import java.io.Serializable;
/*  14:    */ import java.util.ArrayList;
/*  15:    */ import java.util.HashMap;
/*  16:    */ import java.util.HashSet;
/*  17:    */ import java.util.List;
/*  18:    */ import java.util.Map;
/*  19:    */ import java.util.Set;
/*  20:    */ import java.util.TreeSet;
/*  21:    */ import javax.swing.JPanel;
/*  22:    */ import weka.core.Attribute;
/*  23:    */ import weka.core.DenseInstance;
/*  24:    */ import weka.core.Instance;
/*  25:    */ import weka.core.Instances;
/*  26:    */ import weka.core.Utils;
/*  27:    */ import weka.core.converters.ArffLoader;
/*  28:    */ import weka.core.converters.ArffSaver;
/*  29:    */ import weka.core.converters.SerializedInstancesLoader;
/*  30:    */ import weka.gui.Logger;
/*  31:    */ 
/*  32:    */ @KFStep(category="Flow", toolTipText="Append multiple sets of instances")
/*  33:    */ public class Appender
/*  34:    */   extends JPanel
/*  35:    */   implements BeanCommon, Visible, Serializable, DataSource, DataSourceListener, TrainingSetListener, TestSetListener, InstanceListener, EventConstraints
/*  36:    */ {
/*  37:    */   private static final long serialVersionUID = 9177433051794199463L;
/*  38:    */   protected transient Logger m_log;
/*  39: 83 */   protected Set<String> m_listeneeTypes = new HashSet();
/*  40: 84 */   protected Map<Object, Object> m_listenees = new HashMap();
/*  41:    */   protected transient Map<Object, Instances> m_completed;
/*  42:    */   protected transient Map<Object, File> m_tempBatchFiles;
/*  43:    */   protected transient Instances m_completeHeader;
/*  44:    */   protected transient Map<Object, ArffSaver> m_incrementalSavers;
/*  45:107 */   protected InstanceEvent m_ie = new InstanceEvent(this);
/*  46:    */   protected int m_finishedCount;
/*  47:    */   protected transient int m_incrementalCounter;
/*  48:    */   protected boolean m_busy;
/*  49:121 */   protected BeanVisual m_visual = new BeanVisual("Appender", "weka/gui/beans/icons/Appender.png", "weka/gui/beans/icons/Appender.png");
/*  50:126 */   protected ArrayList<DataSourceListener> m_dataListeners = new ArrayList();
/*  51:129 */   protected ArrayList<InstanceListener> m_instanceListeners = new ArrayList();
/*  52:    */   
/*  53:    */   public Appender()
/*  54:    */   {
/*  55:135 */     useDefaultVisual();
/*  56:136 */     setLayout(new BorderLayout());
/*  57:137 */     add(this.m_visual, "Center");
/*  58:    */   }
/*  59:    */   
/*  60:    */   public boolean eventGeneratable(String eventName)
/*  61:    */   {
/*  62:149 */     if (eventName.equals("instance"))
/*  63:    */     {
/*  64:151 */       if (!this.m_listeneeTypes.contains(eventName)) {
/*  65:152 */         return false;
/*  66:    */       }
/*  67:155 */       for (Object listenee : this.m_listenees.values()) {
/*  68:156 */         if (((listenee instanceof EventConstraints)) && (!((EventConstraints)listenee).eventGeneratable(eventName))) {
/*  69:158 */           return false;
/*  70:    */         }
/*  71:    */       }
/*  72:    */     }
/*  73:163 */     if ((eventName.equals("dataSet")) || (eventName.equals("trainingSet")) || (eventName.equals("testSet")))
/*  74:    */     {
/*  75:166 */       if ((!this.m_listeneeTypes.contains("dataSet")) && (!this.m_listeneeTypes.contains("trainingSet")) && (!this.m_listeneeTypes.contains("testSet"))) {
/*  76:169 */         return false;
/*  77:    */       }
/*  78:171 */       for (Object listenee : this.m_listenees.values()) {
/*  79:172 */         if (((listenee instanceof EventConstraints)) && 
/*  80:173 */           (!((EventConstraints)listenee).eventGeneratable("dataSet")) && (!((EventConstraints)listenee).eventGeneratable("trainingSet")) && (!((EventConstraints)listenee).eventGeneratable("testSet"))) {
/*  81:176 */           return false;
/*  82:    */         }
/*  83:    */       }
/*  84:    */     }
/*  85:182 */     return true;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public synchronized void acceptInstance(InstanceEvent e)
/*  89:    */   {
/*  90:192 */     this.m_busy = true;
/*  91:193 */     if (this.m_completed == null)
/*  92:    */     {
/*  93:194 */       this.m_completed = new HashMap();
/*  94:    */       
/*  95:    */ 
/*  96:    */ 
/*  97:    */ 
/*  98:    */ 
/*  99:    */ 
/* 100:    */ 
/* 101:    */ 
/* 102:203 */       this.m_incrementalSavers = new HashMap();
/* 103:204 */       this.m_finishedCount = 0;
/* 104:205 */       this.m_incrementalCounter = 0;
/* 105:    */     }
/* 106:208 */     if (e.getStatus() == 0)
/* 107:    */     {
/* 108:212 */       if (this.m_completed.containsKey(e.getSource()))
/* 109:    */       {
/* 110:213 */         if (this.m_log != null)
/* 111:    */         {
/* 112:214 */           String msg = statusMessagePrefix() + "Resetting appender.";
/* 113:215 */           this.m_log.statusMessage(msg);
/* 114:216 */           this.m_log.logMessage("[Appender] " + msg + " New start of stream detected before " + "all incoming streams have finished!");
/* 115:    */         }
/* 116:221 */         this.m_completed = new HashMap();
/* 117:222 */         this.m_incrementalSavers = new HashMap();
/* 118:223 */         this.m_incrementalCounter = 0;
/* 119:224 */         this.m_completeHeader = null;
/* 120:225 */         this.m_finishedCount = 0;
/* 121:    */       }
/* 122:228 */       this.m_completed.put(e.getSource(), e.getStructure());
/* 123:230 */       if (this.m_completed.size() == this.m_listenees.size()) {
/* 124:    */         try
/* 125:    */         {
/* 126:233 */           if (this.m_log != null)
/* 127:    */           {
/* 128:234 */             String msg = statusMessagePrefix() + "Making output header";
/* 129:235 */             this.m_log.statusMessage(msg);
/* 130:236 */             this.m_log.logMessage("[Appender] " + msg);
/* 131:    */           }
/* 132:239 */           this.m_completeHeader = makeOutputHeader();
/* 133:    */           
/* 134:241 */           this.m_ie.setStructure(this.m_completeHeader);
/* 135:242 */           notifyInstanceListeners(this.m_ie);
/* 136:245 */           if (this.m_incrementalSavers.size() > 0)
/* 137:    */           {
/* 138:247 */             for (ArffSaver s : this.m_incrementalSavers.values())
/* 139:    */             {
/* 140:249 */               s.writeIncremental(null);
/* 141:    */               
/* 142:251 */               File tmpFile = s.retrieveFile();
/* 143:252 */               ArffLoader loader = new ArffLoader();
/* 144:253 */               loader.setFile(tmpFile);
/* 145:254 */               Instances tempStructure = loader.getStructure();
/* 146:255 */               Instance tempLoaded = loader.getNextInstance(tempStructure);
/* 147:256 */               while (tempLoaded != null)
/* 148:    */               {
/* 149:257 */                 Instance converted = makeOutputInstance(this.m_completeHeader, tempLoaded);
/* 150:    */                 
/* 151:259 */                 this.m_ie.setStatus(1);
/* 152:260 */                 this.m_ie.setInstance(converted);
/* 153:261 */                 notifyInstanceListeners(this.m_ie);
/* 154:    */                 
/* 155:263 */                 this.m_incrementalCounter += 1;
/* 156:264 */                 if ((this.m_incrementalCounter % 10000 == 0) && 
/* 157:265 */                   (this.m_log != null)) {
/* 158:266 */                   this.m_log.statusMessage(statusMessagePrefix() + "Processed " + this.m_incrementalCounter + " instances");
/* 159:    */                 }
/* 160:270 */                 tempLoaded = loader.getNextInstance(tempStructure);
/* 161:    */               }
/* 162:    */             }
/* 163:273 */             this.m_incrementalSavers.clear();
/* 164:    */           }
/* 165:    */         }
/* 166:    */         catch (Exception e1)
/* 167:    */         {
/* 168:276 */           String msg = statusMessagePrefix() + "ERROR: unable to create output instances structure.";
/* 169:278 */           if (this.m_log != null)
/* 170:    */           {
/* 171:279 */             this.m_log.statusMessage(msg);
/* 172:280 */             this.m_log.logMessage("[Appender] " + e1.getMessage());
/* 173:    */           }
/* 174:282 */           stop();
/* 175:    */           
/* 176:284 */           e1.printStackTrace();
/* 177:285 */           this.m_busy = false;
/* 178:286 */           return;
/* 179:    */         }
/* 180:    */       }
/* 181:289 */       this.m_busy = false;
/* 182:290 */       return;
/* 183:    */     }
/* 184:293 */     if ((e.getStatus() == 2) || (e.getStatus() == 1))
/* 185:    */     {
/* 186:296 */       Instance currentI = e.getInstance();
/* 187:297 */       if (this.m_completeHeader == null)
/* 188:    */       {
/* 189:298 */         if (currentI != null)
/* 190:    */         {
/* 191:300 */           ArffSaver saver = (ArffSaver)this.m_incrementalSavers.get(e.getSource());
/* 192:301 */           if (saver == null)
/* 193:    */           {
/* 194:302 */             saver = new ArffSaver();
/* 195:    */             try
/* 196:    */             {
/* 197:304 */               File tmpFile = File.createTempFile("weka", ".arff");
/* 198:305 */               saver.setFile(tmpFile);
/* 199:306 */               saver.setRetrieval(2);
/* 200:307 */               saver.setInstances(new Instances(currentI.dataset(), 0));
/* 201:308 */               this.m_incrementalSavers.put(e.getSource(), saver);
/* 202:    */             }
/* 203:    */             catch (IOException e1)
/* 204:    */             {
/* 205:310 */               stop();
/* 206:311 */               e1.printStackTrace();
/* 207:312 */               String msg = statusMessagePrefix() + "ERROR: unable to save instance to temp file";
/* 208:314 */               if (this.m_log != null)
/* 209:    */               {
/* 210:315 */                 this.m_log.statusMessage(msg);
/* 211:316 */                 this.m_log.logMessage("[Appender] " + e1.getMessage());
/* 212:    */               }
/* 213:318 */               this.m_busy = false;
/* 214:319 */               return;
/* 215:    */             }
/* 216:    */           }
/* 217:    */           try
/* 218:    */           {
/* 219:323 */             saver.writeIncremental(currentI);
/* 220:325 */             if (e.getStatus() == 2) {
/* 221:326 */               this.m_finishedCount += 1;
/* 222:    */             }
/* 223:    */           }
/* 224:    */           catch (IOException e1)
/* 225:    */           {
/* 226:329 */             stop();
/* 227:330 */             e1.printStackTrace();
/* 228:    */             
/* 229:332 */             String msg = statusMessagePrefix() + "ERROR: unable to save instance to temp file";
/* 230:334 */             if (this.m_log != null)
/* 231:    */             {
/* 232:335 */               this.m_log.statusMessage(msg);
/* 233:336 */               this.m_log.logMessage("[Appender] " + e1.getMessage());
/* 234:    */             }
/* 235:339 */             this.m_busy = false;
/* 236:340 */             return;
/* 237:    */           }
/* 238:    */         }
/* 239:    */       }
/* 240:344 */       else if (currentI != null)
/* 241:    */       {
/* 242:345 */         int code = 1;
/* 243:346 */         if (e.getStatus() == 2)
/* 244:    */         {
/* 245:347 */           this.m_finishedCount += 1;
/* 246:348 */           if (this.m_finishedCount == this.m_listenees.size()) {
/* 247:350 */             code = 2;
/* 248:    */           }
/* 249:    */         }
/* 250:355 */         Instance newI = makeOutputInstance(this.m_completeHeader, currentI);
/* 251:356 */         this.m_ie.setStatus(code);
/* 252:357 */         this.m_ie.setInstance(newI);
/* 253:358 */         notifyInstanceListeners(this.m_ie);
/* 254:    */         
/* 255:360 */         this.m_incrementalCounter += 1;
/* 256:361 */         if ((this.m_incrementalCounter % 10000 == 0) && 
/* 257:362 */           (this.m_log != null)) {
/* 258:363 */           this.m_log.statusMessage(statusMessagePrefix() + "Processed " + this.m_incrementalCounter + " instances");
/* 259:    */         }
/* 260:368 */         if (code == 2)
/* 261:    */         {
/* 262:369 */           if (this.m_log != null) {
/* 263:370 */             this.m_log.statusMessage(statusMessagePrefix() + "Finished");
/* 264:    */           }
/* 265:372 */           this.m_completed = null;
/* 266:373 */           this.m_incrementalSavers = null;
/* 267:374 */           this.m_incrementalCounter = 0;
/* 268:375 */           this.m_completeHeader = null;
/* 269:376 */           this.m_finishedCount = 0;
/* 270:    */         }
/* 271:    */       }
/* 272:    */     }
/* 273:382 */     this.m_busy = false;
/* 274:    */   }
/* 275:    */   
/* 276:    */   public void acceptTestSet(TestSetEvent e)
/* 277:    */   {
/* 278:392 */     DataSetEvent de = new DataSetEvent(e.getSource(), e.getTestSet());
/* 279:393 */     acceptDataSet(de);
/* 280:    */   }
/* 281:    */   
/* 282:    */   public void acceptTrainingSet(TrainingSetEvent e)
/* 283:    */   {
/* 284:403 */     DataSetEvent de = new DataSetEvent(e.getSource(), e.getTrainingSet());
/* 285:404 */     acceptDataSet(de);
/* 286:    */   }
/* 287:    */   
/* 288:    */   public synchronized void acceptDataSet(DataSetEvent e)
/* 289:    */   {
/* 290:415 */     this.m_busy = true;
/* 291:417 */     if (this.m_completed == null)
/* 292:    */     {
/* 293:419 */       this.m_completed = new HashMap();
/* 294:420 */       this.m_tempBatchFiles = new HashMap();
/* 295:    */     }
/* 296:424 */     Object source = e.getSource();
/* 297:425 */     if (this.m_completed.containsKey(source))
/* 298:    */     {
/* 299:427 */       if ((this.m_log != null) && (!e.isStructureOnly()))
/* 300:    */       {
/* 301:428 */         String msg = statusMessagePrefix() + "Resetting appender.";
/* 302:429 */         this.m_log.statusMessage(msg);
/* 303:430 */         this.m_log.logMessage("[Appender] " + msg + " New batch for an incoming connection " + "detected before " + "all incoming connections have sent data!");
/* 304:    */       }
/* 305:435 */       this.m_completed = new HashMap();
/* 306:436 */       this.m_tempBatchFiles = new HashMap();
/* 307:    */     }
/* 308:439 */     Instances header = new Instances(e.getDataSet(), 0);
/* 309:440 */     this.m_completed.put(source, header);
/* 310:    */     try
/* 311:    */     {
/* 312:443 */       File tmpF = File.createTempFile("weka", SerializedInstancesLoader.FILE_EXTENSION);
/* 313:    */       
/* 314:445 */       tmpF.deleteOnExit();
/* 315:446 */       ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(tmpF)));
/* 316:    */       
/* 317:448 */       oos.writeObject(e.getDataSet());
/* 318:449 */       oos.flush();
/* 319:450 */       oos.close();
/* 320:    */       
/* 321:452 */       this.m_tempBatchFiles.put(source, tmpF);
/* 322:    */     }
/* 323:    */     catch (IOException e1)
/* 324:    */     {
/* 325:454 */       stop();
/* 326:455 */       e1.printStackTrace();
/* 327:    */       
/* 328:457 */       String msg = statusMessagePrefix() + "ERROR: unable to save batch instances to temp file";
/* 329:459 */       if (this.m_log != null)
/* 330:    */       {
/* 331:460 */         this.m_log.statusMessage(msg);
/* 332:461 */         this.m_log.logMessage("[Appender] " + e1.getMessage());
/* 333:    */       }
/* 334:464 */       this.m_busy = false;
/* 335:465 */       return;
/* 336:    */     }
/* 337:471 */     if (this.m_completed.size() == this.m_listenees.size())
/* 338:    */     {
/* 339:    */       try
/* 340:    */       {
/* 341:476 */         Instances output = makeOutputHeader();
/* 342:477 */         if (this.m_log != null)
/* 343:    */         {
/* 344:478 */           String msg = statusMessagePrefix() + "Making output header";
/* 345:479 */           this.m_log.statusMessage(msg);
/* 346:480 */           this.m_log.logMessage("[Appender] " + msg);
/* 347:    */         }
/* 348:483 */         for (File f : this.m_tempBatchFiles.values())
/* 349:    */         {
/* 350:484 */           ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)));
/* 351:    */           
/* 352:486 */           Instances temp = (Instances)ois.readObject();
/* 353:487 */           ois.close();
/* 354:490 */           for (int i = 0; i < temp.numInstances(); i++)
/* 355:    */           {
/* 356:491 */             Instance converted = makeOutputInstance(output, temp.instance(i));
/* 357:492 */             output.add(converted);
/* 358:    */           }
/* 359:    */         }
/* 360:496 */         DataSetEvent d = new DataSetEvent(this, output);
/* 361:497 */         notifyDataListeners(d);
/* 362:    */       }
/* 363:    */       catch (Exception ex)
/* 364:    */       {
/* 365:499 */         stop();
/* 366:500 */         ex.printStackTrace();
/* 367:    */         
/* 368:502 */         String msg = statusMessagePrefix() + "ERROR: unable to output appended data set";
/* 369:504 */         if (this.m_log != null)
/* 370:    */         {
/* 371:505 */           this.m_log.statusMessage(msg);
/* 372:506 */           this.m_log.logMessage("[Appender] " + ex.getMessage());
/* 373:    */         }
/* 374:    */       }
/* 375:511 */       this.m_completed = null;
/* 376:512 */       this.m_tempBatchFiles = null;
/* 377:514 */       if (this.m_log != null) {
/* 378:515 */         this.m_log.statusMessage(statusMessagePrefix() + "Finished");
/* 379:    */       }
/* 380:    */     }
/* 381:518 */     this.m_busy = false;
/* 382:    */   }
/* 383:    */   
/* 384:    */   private Instance makeOutputInstance(Instances output, Instance source)
/* 385:    */   {
/* 386:523 */     double[] newVals = new double[output.numAttributes()];
/* 387:524 */     for (int i = 0; i < newVals.length; i++) {
/* 388:525 */       newVals[i] = Utils.missingValue();
/* 389:    */     }
/* 390:528 */     for (int i = 0; i < source.numAttributes(); i++) {
/* 391:529 */       if (!source.isMissing(i))
/* 392:    */       {
/* 393:530 */         Attribute s = source.attribute(i);
/* 394:531 */         int outputIndex = output.attribute(s.name()).index();
/* 395:532 */         if (s.isNumeric())
/* 396:    */         {
/* 397:533 */           newVals[outputIndex] = source.value(s);
/* 398:    */         }
/* 399:534 */         else if (s.isString())
/* 400:    */         {
/* 401:535 */           String sVal = source.stringValue(s);
/* 402:536 */           newVals[outputIndex] = output.attribute(outputIndex).addStringValue(sVal);
/* 403:    */         }
/* 404:538 */         else if (s.isRelationValued())
/* 405:    */         {
/* 406:539 */           Instances rVal = source.relationalValue(s);
/* 407:540 */           newVals[outputIndex] = output.attribute(outputIndex).addRelation(rVal);
/* 408:    */         }
/* 409:542 */         else if (s.isNominal())
/* 410:    */         {
/* 411:543 */           String nomVal = source.stringValue(s);
/* 412:544 */           newVals[outputIndex] = output.attribute(outputIndex).indexOfValue(nomVal);
/* 413:    */         }
/* 414:    */       }
/* 415:    */     }
/* 416:550 */     Instance newInst = new DenseInstance(source.weight(), newVals);
/* 417:551 */     newInst.setDataset(output);
/* 418:    */     
/* 419:553 */     return newInst;
/* 420:    */   }
/* 421:    */   
/* 422:    */   private Instances makeOutputHeader()
/* 423:    */     throws Exception
/* 424:    */   {
/* 425:558 */     Map<String, Attribute> attLookup = new HashMap();
/* 426:559 */     List<Attribute> attList = new ArrayList();
/* 427:560 */     Map<String, Set<String>> nominalLookups = new HashMap();
/* 428:561 */     for (Instances h : this.m_completed.values()) {
/* 429:562 */       for (int i = 0; i < h.numAttributes(); i++)
/* 430:    */       {
/* 431:563 */         Attribute a = h.attribute(i);
/* 432:564 */         if (!attLookup.containsKey(a.name()))
/* 433:    */         {
/* 434:565 */           attLookup.put(a.name(), a);
/* 435:566 */           attList.add(a);
/* 436:567 */           if (a.isNominal())
/* 437:    */           {
/* 438:568 */             TreeSet<String> nVals = new TreeSet();
/* 439:569 */             for (int j = 0; j < a.numValues(); j++) {
/* 440:570 */               nVals.add(a.value(j));
/* 441:    */             }
/* 442:572 */             nominalLookups.put(a.name(), nVals);
/* 443:    */           }
/* 444:    */         }
/* 445:    */         else
/* 446:    */         {
/* 447:575 */           Attribute storedVersion = (Attribute)attLookup.get(a.name());
/* 448:576 */           if (storedVersion.type() != a.type()) {
/* 449:578 */             throw new Exception("Conflicting types for attribute name '" + a.name() + "' between incoming " + "instance sets");
/* 450:    */           }
/* 451:582 */           if (storedVersion.isNominal())
/* 452:    */           {
/* 453:583 */             Set<String> storedVals = (Set)nominalLookups.get(a.name());
/* 454:584 */             for (int j = 0; j < a.numValues(); j++) {
/* 455:585 */               storedVals.add(a.value(j));
/* 456:    */             }
/* 457:    */           }
/* 458:    */         }
/* 459:    */       }
/* 460:    */     }
/* 461:592 */     ArrayList<Attribute> finalAttList = new ArrayList();
/* 462:593 */     for (Attribute a : attList)
/* 463:    */     {
/* 464:594 */       Attribute newAtt = null;
/* 465:595 */       if (a.isDate())
/* 466:    */       {
/* 467:596 */         newAtt = new Attribute(a.name(), a.getDateFormat());
/* 468:    */       }
/* 469:597 */       else if (a.isNumeric())
/* 470:    */       {
/* 471:598 */         newAtt = new Attribute(a.name());
/* 472:    */       }
/* 473:599 */       else if (a.isRelationValued())
/* 474:    */       {
/* 475:600 */         newAtt = new Attribute(a.name(), a.relation());
/* 476:    */       }
/* 477:601 */       else if (a.isNominal())
/* 478:    */       {
/* 479:602 */         Set<String> vals = (Set)nominalLookups.get(a.name());
/* 480:603 */         List<String> newVals = new ArrayList();
/* 481:604 */         for (String v : vals) {
/* 482:605 */           newVals.add(v);
/* 483:    */         }
/* 484:607 */         newAtt = new Attribute(a.name(), newVals);
/* 485:    */       }
/* 486:608 */       else if (a.isString())
/* 487:    */       {
/* 488:609 */         newAtt = new Attribute(a.name(), (List)null);
/* 489:    */       }
/* 490:617 */       finalAttList.add(newAtt);
/* 491:    */     }
/* 492:620 */     Instances outputHeader = new Instances("Appended_" + this.m_listenees.size() + "_sets", finalAttList, 0);
/* 493:    */     
/* 494:    */ 
/* 495:623 */     return outputHeader;
/* 496:    */   }
/* 497:    */   
/* 498:    */   public synchronized void addDataSourceListener(DataSourceListener dsl)
/* 499:    */   {
/* 500:633 */     this.m_dataListeners.add(dsl);
/* 501:    */   }
/* 502:    */   
/* 503:    */   public synchronized void removeDataSourceListener(DataSourceListener dsl)
/* 504:    */   {
/* 505:643 */     this.m_dataListeners.remove(dsl);
/* 506:    */   }
/* 507:    */   
/* 508:    */   public synchronized void addInstanceListener(InstanceListener tsl)
/* 509:    */   {
/* 510:653 */     this.m_instanceListeners.add(tsl);
/* 511:    */   }
/* 512:    */   
/* 513:    */   public synchronized void removeInstanceListener(InstanceListener tsl)
/* 514:    */   {
/* 515:663 */     this.m_instanceListeners.remove(tsl);
/* 516:    */   }
/* 517:    */   
/* 518:    */   public void useDefaultVisual()
/* 519:    */   {
/* 520:671 */     this.m_visual.loadIcons("weka/gui/beans/icons/Appender.png", "weka/gui/beans/icons/Appender.png");
/* 521:    */     
/* 522:673 */     this.m_visual.setText("Appender");
/* 523:    */   }
/* 524:    */   
/* 525:    */   public void setVisual(BeanVisual newVisual)
/* 526:    */   {
/* 527:683 */     this.m_visual = newVisual;
/* 528:    */   }
/* 529:    */   
/* 530:    */   public BeanVisual getVisual()
/* 531:    */   {
/* 532:693 */     return this.m_visual;
/* 533:    */   }
/* 534:    */   
/* 535:    */   public void setCustomName(String name)
/* 536:    */   {
/* 537:703 */     this.m_visual.setText(name);
/* 538:    */   }
/* 539:    */   
/* 540:    */   public String getCustomName()
/* 541:    */   {
/* 542:713 */     return this.m_visual.getText();
/* 543:    */   }
/* 544:    */   
/* 545:    */   public void stop()
/* 546:    */   {
/* 547:722 */     if ((this.m_listenees != null) && (this.m_listenees.size() > 0)) {
/* 548:723 */       for (Object l : this.m_listenees.values()) {
/* 549:724 */         if ((l instanceof BeanCommon)) {
/* 550:725 */           ((BeanCommon)l).stop();
/* 551:    */         }
/* 552:    */       }
/* 553:    */     }
/* 554:730 */     this.m_busy = false;
/* 555:    */   }
/* 556:    */   
/* 557:    */   public boolean isBusy()
/* 558:    */   {
/* 559:741 */     return this.m_busy;
/* 560:    */   }
/* 561:    */   
/* 562:    */   public void setLog(Logger logger)
/* 563:    */   {
/* 564:751 */     this.m_log = logger;
/* 565:    */   }
/* 566:    */   
/* 567:    */   public boolean connectionAllowed(EventSetDescriptor esd)
/* 568:    */   {
/* 569:763 */     return connectionAllowed(esd.getName());
/* 570:    */   }
/* 571:    */   
/* 572:    */   public boolean connectionAllowed(String eventName)
/* 573:    */   {
/* 574:775 */     if ((!eventName.equals("dataSet")) && (!eventName.equals("trainingSet")) && (!eventName.equals("testSet")) && (!eventName.equals("instance"))) {
/* 575:777 */       return false;
/* 576:    */     }
/* 577:780 */     if (this.m_listeneeTypes.size() == 0) {
/* 578:781 */       return true;
/* 579:    */     }
/* 580:784 */     if ((this.m_listeneeTypes.contains("instance")) && (!eventName.equals("instance"))) {
/* 581:785 */       return false;
/* 582:    */     }
/* 583:788 */     if ((!this.m_listeneeTypes.contains("instance")) && (eventName.equals("instance"))) {
/* 584:789 */       return false;
/* 585:    */     }
/* 586:792 */     return true;
/* 587:    */   }
/* 588:    */   
/* 589:    */   public void connectionNotification(String eventName, Object source)
/* 590:    */   {
/* 591:806 */     if (connectionAllowed(eventName))
/* 592:    */     {
/* 593:807 */       this.m_listeneeTypes.add(eventName);
/* 594:808 */       this.m_listenees.put(source, source);
/* 595:    */     }
/* 596:    */   }
/* 597:    */   
/* 598:    */   public void disconnectionNotification(String eventName, Object source)
/* 599:    */   {
/* 600:822 */     this.m_listenees.remove(source);
/* 601:823 */     if (this.m_listenees.size() == 0) {
/* 602:824 */       this.m_listeneeTypes.clear();
/* 603:    */     }
/* 604:    */   }
/* 605:    */   
/* 606:    */   private String statusMessagePrefix()
/* 607:    */   {
/* 608:829 */     return getCustomName() + "$" + hashCode() + "|";
/* 609:    */   }
/* 610:    */   
/* 611:    */   private void notifyInstanceListeners(InstanceEvent e)
/* 612:    */   {
/* 613:    */     List<InstanceListener> l;
/* 614:835 */     synchronized (this)
/* 615:    */     {
/* 616:836 */       l = (List)this.m_instanceListeners.clone();
/* 617:    */     }
/* 618:838 */     if (l.size() > 0) {
/* 619:839 */       for (InstanceListener il : l) {
/* 620:840 */         il.acceptInstance(e);
/* 621:    */       }
/* 622:    */     }
/* 623:    */   }
/* 624:    */   
/* 625:    */   private void notifyDataListeners(DataSetEvent e)
/* 626:    */   {
/* 627:    */     List<DataSourceListener> l;
/* 628:848 */     synchronized (this)
/* 629:    */     {
/* 630:849 */       l = (List)this.m_dataListeners.clone();
/* 631:    */     }
/* 632:851 */     if (l.size() > 0) {
/* 633:852 */       for (DataSourceListener ds : l) {
/* 634:853 */         ds.acceptDataSet(e);
/* 635:    */       }
/* 636:    */     }
/* 637:    */   }
/* 638:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.Appender
 * JD-Core Version:    0.7.0.1
 */