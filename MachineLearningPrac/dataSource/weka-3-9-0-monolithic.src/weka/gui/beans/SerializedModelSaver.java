/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.beans.EventSetDescriptor;
/*   5:    */ import java.io.BufferedOutputStream;
/*   6:    */ import java.io.File;
/*   7:    */ import java.io.FileOutputStream;
/*   8:    */ import java.io.IOException;
/*   9:    */ import java.io.ObjectInputStream;
/*  10:    */ import java.io.ObjectOutputStream;
/*  11:    */ import java.io.PrintStream;
/*  12:    */ import java.io.Serializable;
/*  13:    */ import java.util.ArrayList;
/*  14:    */ import java.util.Vector;
/*  15:    */ import javax.swing.JPanel;
/*  16:    */ import weka.classifiers.UpdateableBatchProcessor;
/*  17:    */ import weka.classifiers.misc.InputMappedClassifier;
/*  18:    */ import weka.core.Environment;
/*  19:    */ import weka.core.EnvironmentHandler;
/*  20:    */ import weka.core.Instances;
/*  21:    */ import weka.core.Tag;
/*  22:    */ import weka.core.Utils;
/*  23:    */ import weka.core.xml.KOML;
/*  24:    */ import weka.core.xml.XStream;
/*  25:    */ import weka.gui.Logger;
/*  26:    */ 
/*  27:    */ @KFStep(category="DataSinks", toolTipText="Save a batch or incremental model to file")
/*  28:    */ public class SerializedModelSaver
/*  29:    */   extends JPanel
/*  30:    */   implements BeanCommon, Visible, BatchClassifierListener, IncrementalClassifierListener, BatchClustererListener, EnvironmentHandler, Serializable
/*  31:    */ {
/*  32:    */   private static final long serialVersionUID = 3956528599473814287L;
/*  33: 65 */   protected BeanVisual m_visual = new BeanVisual("AbstractDataSink", "weka/gui/beans/icons/SerializedModelSaver.gif", "weka/gui/beans/icons/SerializedModelSaver_animated.gif");
/*  34: 73 */   protected Object m_listenee = null;
/*  35: 78 */   protected transient Logger m_logger = null;
/*  36: 83 */   private String m_filenamePrefix = "";
/*  37:    */   protected transient int m_counter;
/*  38: 92 */   protected int m_incrementalSaveSchedule = 0;
/*  39: 97 */   private File m_directory = new File(System.getProperty("user.dir"));
/*  40:    */   private Tag m_fileFormat;
/*  41:    */   public static final int BINARY = 0;
/*  42:    */   public static final int KOMLV = 1;
/*  43:    */   public static final int XSTREAM = 2;
/*  44:    */   public static final String FILE_EXTENSION = "model";
/*  45:115 */   private boolean m_useRelativePath = false;
/*  46:118 */   private boolean m_includeRelationName = false;
/*  47:126 */   public static ArrayList<Tag> s_fileFormatsAvailable = new ArrayList();
/*  48:    */   protected transient Environment m_env;
/*  49:    */   
/*  50:    */   static
/*  51:    */   {
/*  52:127 */     s_fileFormatsAvailable.add(new Tag(0, "Binary serialized model file (*model)", "", false));
/*  53:129 */     if (KOML.isPresent()) {
/*  54:130 */       s_fileFormatsAvailable.add(new Tag(1, "XML serialized model file (*.komlmodel)", "", false));
/*  55:    */     }
/*  56:134 */     if (XStream.isPresent()) {
/*  57:135 */       s_fileFormatsAvailable.add(new Tag(2, "XML serialized model file (*.xstreammodel)", "", false));
/*  58:    */     }
/*  59:    */   }
/*  60:    */   
/*  61:    */   public SerializedModelSaver()
/*  62:    */   {
/*  63:150 */     useDefaultVisual();
/*  64:151 */     setLayout(new BorderLayout());
/*  65:152 */     add(this.m_visual, "Center");
/*  66:153 */     this.m_fileFormat = ((Tag)s_fileFormatsAvailable.get(0));
/*  67:    */     
/*  68:155 */     this.m_env = Environment.getSystemWide();
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void setCustomName(String name)
/*  72:    */   {
/*  73:165 */     this.m_visual.setText(name);
/*  74:    */   }
/*  75:    */   
/*  76:    */   public String getCustomName()
/*  77:    */   {
/*  78:175 */     return this.m_visual.getText();
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void useDefaultVisual()
/*  82:    */   {
/*  83:184 */     this.m_visual.loadIcons("weka/gui/beans/icons/SerializedModelSaver.gif", "weka/gui/beans/icons/SerializedModelSaver_animated.gif");
/*  84:    */     
/*  85:186 */     this.m_visual.setText("SerializedModelSaver");
/*  86:    */   }
/*  87:    */   
/*  88:    */   public void setVisual(BeanVisual newVisual)
/*  89:    */   {
/*  90:196 */     this.m_visual = newVisual;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public BeanVisual getVisual()
/*  94:    */   {
/*  95:205 */     return this.m_visual;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public boolean connectionAllowed(EventSetDescriptor esd)
/*  99:    */   {
/* 100:217 */     return connectionAllowed(esd.getName());
/* 101:    */   }
/* 102:    */   
/* 103:    */   public boolean connectionAllowed(String eventName)
/* 104:    */   {
/* 105:229 */     return this.m_listenee == null;
/* 106:    */   }
/* 107:    */   
/* 108:    */   public synchronized void connectionNotification(String eventName, Object source)
/* 109:    */   {
/* 110:243 */     if (connectionAllowed(eventName)) {
/* 111:244 */       this.m_listenee = source;
/* 112:    */     }
/* 113:    */   }
/* 114:    */   
/* 115:    */   public synchronized void disconnectionNotification(String eventName, Object source)
/* 116:    */   {
/* 117:259 */     if (this.m_listenee == source) {
/* 118:260 */       this.m_listenee = null;
/* 119:    */     }
/* 120:    */   }
/* 121:    */   
/* 122:    */   public void setLog(Logger logger)
/* 123:    */   {
/* 124:271 */     this.m_logger = logger;
/* 125:    */   }
/* 126:    */   
/* 127:    */   public void stop()
/* 128:    */   {
/* 129:280 */     if ((this.m_listenee instanceof BeanCommon)) {
/* 130:281 */       ((BeanCommon)this.m_listenee).stop();
/* 131:    */     }
/* 132:    */   }
/* 133:    */   
/* 134:    */   public boolean isBusy()
/* 135:    */   {
/* 136:293 */     return false;
/* 137:    */   }
/* 138:    */   
/* 139:    */   protected String sanitizeFilename(String filename)
/* 140:    */   {
/* 141:304 */     return filename.replaceAll("\\\\", "_").replaceAll(":", "_").replaceAll("/", "_");
/* 142:    */   }
/* 143:    */   
/* 144:    */   public void acceptClusterer(BatchClustererEvent ce)
/* 145:    */   {
/* 146:315 */     if ((ce.getTestSet() == null) || (ce.getTestOrTrain() == BatchClustererEvent.TEST) || (ce.getTestSet().isStructureOnly())) {
/* 147:318 */       return;
/* 148:    */     }
/* 149:321 */     Instances trainHeader = new Instances(ce.getTestSet().getDataSet(), 0);
/* 150:322 */     String titleString = ce.getClusterer().getClass().getName();
/* 151:323 */     titleString = titleString.substring(titleString.lastIndexOf('.') + 1, titleString.length());
/* 152:    */     
/* 153:    */ 
/* 154:326 */     String prefix = "";
/* 155:327 */     String relationName = this.m_includeRelationName ? trainHeader.relationName() : "";
/* 156:    */     try
/* 157:    */     {
/* 158:330 */       prefix = this.m_env.substitute(this.m_filenamePrefix);
/* 159:    */     }
/* 160:    */     catch (Exception ex)
/* 161:    */     {
/* 162:332 */       stop();
/* 163:333 */       String message = "[SerializedModelSaver] " + statusMessagePrefix() + " Can't save model. Reason: " + ex.getMessage();
/* 164:335 */       if (this.m_logger != null)
/* 165:    */       {
/* 166:336 */         this.m_logger.logMessage(message);
/* 167:337 */         this.m_logger.statusMessage(statusMessagePrefix() + "ERROR (See log for details)");
/* 168:    */       }
/* 169:    */       else
/* 170:    */       {
/* 171:340 */         System.err.println(message);
/* 172:    */       }
/* 173:342 */       return;
/* 174:    */     }
/* 175:344 */     String fileName = "" + prefix + relationName + titleString + "_" + ce.getSetNumber() + "_" + ce.getMaxSetNumber();
/* 176:    */     
/* 177:346 */     fileName = sanitizeFilename(fileName);
/* 178:    */     
/* 179:348 */     String dirName = this.m_directory.getPath();
/* 180:    */     try
/* 181:    */     {
/* 182:350 */       dirName = this.m_env.substitute(dirName);
/* 183:    */     }
/* 184:    */     catch (Exception ex)
/* 185:    */     {
/* 186:352 */       String message = "[SerializedModelSaver] " + statusMessagePrefix() + " Can't save model. Reason: " + ex.getMessage();
/* 187:354 */       if (this.m_logger != null)
/* 188:    */       {
/* 189:355 */         this.m_logger.logMessage(message);
/* 190:356 */         this.m_logger.statusMessage(statusMessagePrefix() + "ERROR (See log for details)");
/* 191:    */       }
/* 192:    */       else
/* 193:    */       {
/* 194:359 */         System.err.println(message);
/* 195:    */       }
/* 196:361 */       return;
/* 197:    */     }
/* 198:363 */     File tempFile = new File(dirName);
/* 199:364 */     fileName = tempFile.getAbsolutePath() + File.separator + fileName;
/* 200:    */     
/* 201:366 */     saveModel(fileName, trainHeader, ce.getClusterer());
/* 202:    */   }
/* 203:    */   
/* 204:    */   public void acceptClassifier(IncrementalClassifierEvent ce)
/* 205:    */   {
/* 206:376 */     if ((ce.getStatus() == 2) || ((this.m_incrementalSaveSchedule > 0) && (this.m_counter % this.m_incrementalSaveSchedule == 0) && (this.m_counter > 0)))
/* 207:    */     {
/* 208:381 */       Instances header = ce.getStructure();
/* 209:382 */       String titleString = ce.getClassifier().getClass().getName();
/* 210:383 */       titleString = titleString.substring(titleString.lastIndexOf('.') + 1, titleString.length());
/* 211:    */       
/* 212:    */ 
/* 213:386 */       String prefix = "";
/* 214:387 */       String relationName = this.m_includeRelationName ? header.relationName() : "";
/* 215:    */       try
/* 216:    */       {
/* 217:391 */         prefix = this.m_env.substitute(this.m_filenamePrefix);
/* 218:    */       }
/* 219:    */       catch (Exception ex)
/* 220:    */       {
/* 221:393 */         String message = "[SerializedModelSaver] " + statusMessagePrefix() + " Can't save model. Reason: " + ex.getMessage();
/* 222:395 */         if (this.m_logger != null)
/* 223:    */         {
/* 224:396 */           this.m_logger.logMessage(message);
/* 225:397 */           this.m_logger.statusMessage(statusMessagePrefix() + "ERROR (See log for details)");
/* 226:    */         }
/* 227:    */         else
/* 228:    */         {
/* 229:400 */           System.err.println(message);
/* 230:    */         }
/* 231:402 */         return;
/* 232:    */       }
/* 233:405 */       String fileName = "" + prefix + relationName + titleString;
/* 234:406 */       fileName = sanitizeFilename(fileName);
/* 235:    */       
/* 236:408 */       String dirName = this.m_directory.getPath();
/* 237:    */       try
/* 238:    */       {
/* 239:410 */         dirName = this.m_env.substitute(dirName);
/* 240:    */       }
/* 241:    */       catch (Exception ex)
/* 242:    */       {
/* 243:412 */         String message = "[SerializedModelSaver] " + statusMessagePrefix() + " Can't save model. Reason: " + ex.getMessage();
/* 244:414 */         if (this.m_logger != null)
/* 245:    */         {
/* 246:415 */           this.m_logger.logMessage(message);
/* 247:416 */           this.m_logger.statusMessage(statusMessagePrefix() + "ERROR (See log for details)");
/* 248:    */         }
/* 249:    */         else
/* 250:    */         {
/* 251:419 */           System.err.println(message);
/* 252:    */         }
/* 253:421 */         return;
/* 254:    */       }
/* 255:423 */       File tempFile = new File(dirName);
/* 256:    */       
/* 257:425 */       fileName = tempFile.getAbsolutePath() + File.separator + fileName;
/* 258:    */       
/* 259:427 */       saveModel(fileName, header, ce.getClassifier());
/* 260:    */     }
/* 261:428 */     else if (ce.getStatus() == 0)
/* 262:    */     {
/* 263:429 */       this.m_counter = 0;
/* 264:    */     }
/* 265:431 */     this.m_counter += 1;
/* 266:    */   }
/* 267:    */   
/* 268:    */   public void acceptClassifier(BatchClassifierEvent ce)
/* 269:    */   {
/* 270:441 */     if ((ce.getTrainSet() == null) || (ce.getTrainSet().isStructureOnly())) {
/* 271:442 */       return;
/* 272:    */     }
/* 273:444 */     Instances trainHeader = ce.getTrainSet().getDataSet().stringFreeStructure();
/* 274:447 */     if ((ce.getClassifier() instanceof InputMappedClassifier)) {
/* 275:    */       try
/* 276:    */       {
/* 277:449 */         trainHeader = ((InputMappedClassifier)ce.getClassifier()).getModelHeader(trainHeader);
/* 278:    */       }
/* 279:    */       catch (Exception e)
/* 280:    */       {
/* 281:452 */         e.printStackTrace();
/* 282:    */       }
/* 283:    */     }
/* 284:455 */     String titleString = ce.getClassifier().getClass().getName();
/* 285:456 */     titleString = titleString.substring(titleString.lastIndexOf('.') + 1, titleString.length());
/* 286:    */     
/* 287:    */ 
/* 288:459 */     String prefix = "";
/* 289:460 */     String relationName = this.m_includeRelationName ? trainHeader.relationName() : "";
/* 290:    */     try
/* 291:    */     {
/* 292:463 */       prefix = this.m_env.substitute(this.m_filenamePrefix);
/* 293:    */     }
/* 294:    */     catch (Exception ex)
/* 295:    */     {
/* 296:465 */       String message = "[SerializedModelSaver] " + statusMessagePrefix() + " Can't save model. Reason: " + ex.getMessage();
/* 297:467 */       if (this.m_logger != null)
/* 298:    */       {
/* 299:468 */         this.m_logger.logMessage(message);
/* 300:469 */         this.m_logger.statusMessage(statusMessagePrefix() + "ERROR (See log for details)");
/* 301:    */       }
/* 302:    */       else
/* 303:    */       {
/* 304:472 */         System.err.println(message);
/* 305:    */       }
/* 306:474 */       return;
/* 307:    */     }
/* 308:477 */     String fileName = "" + prefix + relationName + titleString + "_" + ce.getSetNumber() + "_" + ce.getMaxSetNumber();
/* 309:    */     
/* 310:479 */     fileName = sanitizeFilename(fileName);
/* 311:    */     
/* 312:481 */     String dirName = this.m_directory.getPath();
/* 313:    */     try
/* 314:    */     {
/* 315:483 */       dirName = this.m_env.substitute(dirName);
/* 316:    */     }
/* 317:    */     catch (Exception ex)
/* 318:    */     {
/* 319:485 */       String message = "[SerializedModelSaver] " + statusMessagePrefix() + " Can't save model. Reason: " + ex.getMessage();
/* 320:487 */       if (this.m_logger != null)
/* 321:    */       {
/* 322:488 */         this.m_logger.logMessage(message);
/* 323:489 */         this.m_logger.statusMessage(statusMessagePrefix() + "ERROR (See log for details)");
/* 324:    */       }
/* 325:    */       else
/* 326:    */       {
/* 327:492 */         System.err.println(message);
/* 328:    */       }
/* 329:494 */       return;
/* 330:    */     }
/* 331:496 */     File tempFile = new File(dirName);
/* 332:    */     
/* 333:498 */     fileName = tempFile.getAbsolutePath() + File.separator + fileName;
/* 334:    */     
/* 335:500 */     saveModel(fileName, trainHeader, ce.getClassifier());
/* 336:    */   }
/* 337:    */   
/* 338:    */   private void saveModel(String fileName, Instances trainHeader, Object model)
/* 339:    */   {
/* 340:507 */     this.m_fileFormat = validateFileFormat(this.m_fileFormat);
/* 341:508 */     if (this.m_fileFormat == null) {
/* 342:510 */       this.m_fileFormat = ((Tag)s_fileFormatsAvailable.get(0));
/* 343:    */     }
/* 344:513 */     if ((model instanceof UpdateableBatchProcessor)) {
/* 345:    */       try
/* 346:    */       {
/* 347:516 */         ((UpdateableBatchProcessor)model).batchFinished();
/* 348:    */       }
/* 349:    */       catch (Exception ex)
/* 350:    */       {
/* 351:518 */         System.err.println("[SerializedModelSaver] Problem saving model");
/* 352:519 */         if (this.m_logger != null)
/* 353:    */         {
/* 354:520 */           this.m_logger.logMessage("[SerializedModelSaver] " + statusMessagePrefix() + " Problem saving model. Reason: " + ex.getMessage());
/* 355:    */           
/* 356:522 */           this.m_logger.statusMessage(statusMessagePrefix() + "ERROR (See log for details)");
/* 357:    */         }
/* 358:    */       }
/* 359:    */     }
/* 360:527 */     this.m_logger.logMessage("[SerializedModelSaver] " + statusMessagePrefix() + " Saving model " + model.getClass().getName());
/* 361:    */     try
/* 362:    */     {
/* 363:530 */       switch (this.m_fileFormat.getID())
/* 364:    */       {
/* 365:    */       case 1: 
/* 366:532 */         fileName = fileName + ".koml" + "model";
/* 367:533 */         saveKOML(new File(fileName), model, trainHeader);
/* 368:534 */         break;
/* 369:    */       case 2: 
/* 370:536 */         fileName = fileName + ".xstream" + "model";
/* 371:537 */         saveXStream(new File(fileName), model, trainHeader);
/* 372:538 */         break;
/* 373:    */       default: 
/* 374:540 */         fileName = fileName + "." + "model";
/* 375:541 */         saveBinary(new File(fileName), model, trainHeader);
/* 376:    */       }
/* 377:    */     }
/* 378:    */     catch (Exception ex)
/* 379:    */     {
/* 380:545 */       System.err.println("[SerializedModelSaver] Problem saving model");
/* 381:546 */       if (this.m_logger != null)
/* 382:    */       {
/* 383:547 */         this.m_logger.logMessage("[SerializedModelSaver] " + statusMessagePrefix() + " Problem saving model. Reason: " + ex.getMessage());
/* 384:    */         
/* 385:549 */         this.m_logger.statusMessage(statusMessagePrefix() + "ERROR (See log for details)");
/* 386:    */       }
/* 387:    */     }
/* 388:    */   }
/* 389:    */   
/* 390:    */   public static void saveBinary(File saveTo, Object model, Instances header)
/* 391:    */     throws IOException
/* 392:    */   {
/* 393:565 */     ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(saveTo)));
/* 394:    */     
/* 395:567 */     os.writeObject(model);
/* 396:569 */     if (header != null) {
/* 397:570 */       os.writeObject(header);
/* 398:    */     }
/* 399:572 */     os.close();
/* 400:    */   }
/* 401:    */   
/* 402:    */   public static void saveKOML(File saveTo, Object model, Instances header)
/* 403:    */     throws Exception
/* 404:    */   {
/* 405:585 */     Vector<Object> v = new Vector();
/* 406:586 */     v.add(model);
/* 407:587 */     if (header != null) {
/* 408:588 */       v.add(header);
/* 409:    */     }
/* 410:590 */     v.trimToSize();
/* 411:591 */     KOML.write(saveTo.getAbsolutePath(), v);
/* 412:    */   }
/* 413:    */   
/* 414:    */   public static void saveXStream(File saveTo, Object model, Instances header)
/* 415:    */     throws Exception
/* 416:    */   {
/* 417:604 */     Vector<Object> v = new Vector();
/* 418:605 */     v.add(model);
/* 419:606 */     if (header != null) {
/* 420:607 */       v.add(header);
/* 421:    */     }
/* 422:609 */     v.trimToSize();
/* 423:610 */     XStream.write(saveTo.getAbsolutePath(), v);
/* 424:    */   }
/* 425:    */   
/* 426:    */   public File getDirectory()
/* 427:    */   {
/* 428:619 */     return this.m_directory;
/* 429:    */   }
/* 430:    */   
/* 431:    */   public void setDirectory(File d)
/* 432:    */   {
/* 433:628 */     this.m_directory = d;
/* 434:629 */     if (this.m_useRelativePath) {
/* 435:    */       try
/* 436:    */       {
/* 437:631 */         this.m_directory = Utils.convertToRelativePath(this.m_directory);
/* 438:    */       }
/* 439:    */       catch (Exception ex) {}
/* 440:    */     }
/* 441:    */   }
/* 442:    */   
/* 443:    */   public void setUseRelativePath(boolean rp)
/* 444:    */   {
/* 445:644 */     this.m_useRelativePath = rp;
/* 446:    */   }
/* 447:    */   
/* 448:    */   public boolean getUseRelativePath()
/* 449:    */   {
/* 450:654 */     return this.m_useRelativePath;
/* 451:    */   }
/* 452:    */   
/* 453:    */   public void setIncludeRelationName(boolean rn)
/* 454:    */   {
/* 455:664 */     this.m_includeRelationName = rn;
/* 456:    */   }
/* 457:    */   
/* 458:    */   public boolean getIncludeRelationName()
/* 459:    */   {
/* 460:674 */     return this.m_includeRelationName;
/* 461:    */   }
/* 462:    */   
/* 463:    */   public String getPrefix()
/* 464:    */   {
/* 465:683 */     return this.m_filenamePrefix;
/* 466:    */   }
/* 467:    */   
/* 468:    */   public void setPrefix(String p)
/* 469:    */   {
/* 470:692 */     this.m_filenamePrefix = p;
/* 471:    */   }
/* 472:    */   
/* 473:    */   public void setIncrementalSaveSchedule(int s)
/* 474:    */   {
/* 475:702 */     this.m_incrementalSaveSchedule = s;
/* 476:    */   }
/* 477:    */   
/* 478:    */   public int getIncrementalSaveSchedule()
/* 479:    */   {
/* 480:712 */     return this.m_incrementalSaveSchedule;
/* 481:    */   }
/* 482:    */   
/* 483:    */   public String globalInfo()
/* 484:    */   {
/* 485:721 */     return "Save trained models to serialized object files.";
/* 486:    */   }
/* 487:    */   
/* 488:    */   public void setFileFormat(Tag ff)
/* 489:    */   {
/* 490:730 */     this.m_fileFormat = ff;
/* 491:    */   }
/* 492:    */   
/* 493:    */   public Tag getFileFormat()
/* 494:    */   {
/* 495:739 */     return this.m_fileFormat;
/* 496:    */   }
/* 497:    */   
/* 498:    */   public Tag validateFileFormat(Tag ff)
/* 499:    */   {
/* 500:749 */     Tag r = ff;
/* 501:750 */     if (ff.getID() == 0) {
/* 502:751 */       return ff;
/* 503:    */     }
/* 504:754 */     if ((ff.getID() == 1) && (!KOML.isPresent())) {
/* 505:755 */       r = null;
/* 506:    */     }
/* 507:758 */     if ((ff.getID() == 2) && (!XStream.isPresent())) {
/* 508:759 */       r = null;
/* 509:    */     }
/* 510:762 */     return r;
/* 511:    */   }
/* 512:    */   
/* 513:    */   private String statusMessagePrefix()
/* 514:    */   {
/* 515:766 */     return getCustomName() + "$" + hashCode() + "|";
/* 516:    */   }
/* 517:    */   
/* 518:    */   public void setEnvironment(Environment env)
/* 519:    */   {
/* 520:776 */     this.m_env = env;
/* 521:    */   }
/* 522:    */   
/* 523:    */   private void readObject(ObjectInputStream aStream)
/* 524:    */     throws IOException, ClassNotFoundException
/* 525:    */   {
/* 526:783 */     aStream.defaultReadObject();
/* 527:    */     
/* 528:    */ 
/* 529:786 */     this.m_env = Environment.getSystemWide();
/* 530:    */   }
/* 531:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.SerializedModelSaver
 * JD-Core Version:    0.7.0.1
 */