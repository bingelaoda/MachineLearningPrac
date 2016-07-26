/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.event.WindowAdapter;
/*   6:    */ import java.awt.event.WindowEvent;
/*   7:    */ import java.io.IOException;
/*   8:    */ import java.io.ObjectInputStream;
/*   9:    */ import java.io.PrintStream;
/*  10:    */ import javax.swing.JFrame;
/*  11:    */ import weka.core.Environment;
/*  12:    */ import weka.core.EnvironmentHandler;
/*  13:    */ import weka.core.Instances;
/*  14:    */ import weka.core.OptionHandler;
/*  15:    */ import weka.core.SerializedObject;
/*  16:    */ import weka.core.Utils;
/*  17:    */ import weka.core.converters.ArffSaver;
/*  18:    */ import weka.core.converters.DatabaseConverter;
/*  19:    */ import weka.core.converters.DatabaseSaver;
/*  20:    */ import weka.gui.Logger;
/*  21:    */ import weka.gui.visualize.PlotData2D;
/*  22:    */ 
/*  23:    */ public class Saver
/*  24:    */   extends AbstractDataSink
/*  25:    */   implements WekaWrapper, EnvironmentHandler
/*  26:    */ {
/*  27:    */   private static final long serialVersionUID = 5371716690308950755L;
/*  28:    */   private Instances m_dataSet;
/*  29:    */   private Instances m_structure;
/*  30:    */   protected String m_globalInfo;
/*  31:    */   private transient SaveBatchThread m_ioThread;
/*  32: 73 */   private weka.core.converters.Saver m_Saver = new ArffSaver();
/*  33: 74 */   private weka.core.converters.Saver m_SaverTemplate = this.m_Saver;
/*  34:    */   private String m_fileName;
/*  35:    */   private boolean m_isDBSaver;
/*  36: 94 */   private boolean m_relationNameForFilename = true;
/*  37:    */   private int m_count;
/*  38:    */   protected transient Environment m_env;
/*  39:    */   protected transient StreamThroughput m_throughput;
/*  40:    */   
/*  41:    */   private weka.core.converters.Saver makeCopy()
/*  42:    */     throws Exception
/*  43:    */   {
/*  44:107 */     return (weka.core.converters.Saver)new SerializedObject(this.m_SaverTemplate).getObject();
/*  45:    */   }
/*  46:    */   
/*  47:    */   private class SaveBatchThread
/*  48:    */     extends Thread
/*  49:    */   {
/*  50:    */     public SaveBatchThread(DataSink ds) {}
/*  51:    */     
/*  52:    */     public void run()
/*  53:    */     {
/*  54:    */       try
/*  55:    */       {
/*  56:119 */         Saver.this.m_visual.setAnimated();
/*  57:    */         
/*  58:121 */         Saver.this.m_Saver.setInstances(Saver.this.m_dataSet);
/*  59:122 */         if (Saver.this.m_logger != null) {
/*  60:123 */           Saver.this.m_logger.statusMessage(Saver.this.statusMessagePrefix() + "Saving " + Saver.this.m_dataSet.relationName() + "...");
/*  61:    */         }
/*  62:126 */         Saver.this.m_Saver.writeBatch();
/*  63:127 */         if (Saver.this.m_logger != null) {
/*  64:128 */           Saver.this.m_logger.logMessage("[Saver] " + Saver.this.statusMessagePrefix() + "Save successful.");
/*  65:    */         }
/*  66:    */       }
/*  67:    */       catch (Exception ex)
/*  68:    */       {
/*  69:133 */         if (Saver.this.m_logger != null)
/*  70:    */         {
/*  71:134 */           Saver.this.m_logger.statusMessage(Saver.this.statusMessagePrefix() + "ERROR (See log for details)");
/*  72:    */           
/*  73:136 */           Saver.this.m_logger.logMessage("[Saver] " + Saver.this.statusMessagePrefix() + " problem saving. " + ex.getMessage());
/*  74:    */         }
/*  75:139 */         ex.printStackTrace();
/*  76:    */       }
/*  77:    */       finally
/*  78:    */       {
/*  79:141 */         if ((Thread.currentThread().isInterrupted()) && 
/*  80:142 */           (Saver.this.m_logger != null)) {
/*  81:143 */           Saver.this.m_logger.logMessage("[Saver] " + Saver.this.statusMessagePrefix() + " Saving interrupted!!");
/*  82:    */         }
/*  83:147 */         if (Saver.this.m_logger != null) {
/*  84:148 */           Saver.this.m_logger.statusMessage(Saver.this.statusMessagePrefix() + "Finished.");
/*  85:    */         }
/*  86:150 */         Saver.this.block(false);
/*  87:151 */         Saver.this.m_visual.setStatic();
/*  88:152 */         Saver.this.m_ioThread = null;
/*  89:    */       }
/*  90:    */     }
/*  91:    */   }
/*  92:    */   
/*  93:    */   private synchronized void block(boolean tf)
/*  94:    */   {
/*  95:165 */     if (tf) {
/*  96:    */       try
/*  97:    */       {
/*  98:167 */         if (this.m_ioThread.isAlive()) {
/*  99:168 */           wait();
/* 100:    */         }
/* 101:    */       }
/* 102:    */       catch (InterruptedException ex) {}
/* 103:    */     } else {
/* 104:173 */       notifyAll();
/* 105:    */     }
/* 106:    */   }
/* 107:    */   
/* 108:    */   public boolean isBusy()
/* 109:    */   {
/* 110:185 */     return this.m_ioThread != null;
/* 111:    */   }
/* 112:    */   
/* 113:    */   public String globalInfo()
/* 114:    */   {
/* 115:194 */     return this.m_globalInfo;
/* 116:    */   }
/* 117:    */   
/* 118:    */   public Saver()
/* 119:    */   {
/* 120:200 */     setSaverTemplate(this.m_Saver);
/* 121:201 */     this.m_fileName = "";
/* 122:202 */     this.m_dataSet = null;
/* 123:203 */     this.m_count = 0;
/* 124:    */   }
/* 125:    */   
/* 126:    */   public void setCustomName(String name)
/* 127:    */   {
/* 128:214 */     this.m_visual.setText(name);
/* 129:    */   }
/* 130:    */   
/* 131:    */   public String getCustomName()
/* 132:    */   {
/* 133:224 */     return this.m_visual.getText();
/* 134:    */   }
/* 135:    */   
/* 136:    */   public void setEnvironment(Environment env)
/* 137:    */   {
/* 138:234 */     this.m_env = env;
/* 139:    */   }
/* 140:    */   
/* 141:    */   private void passEnvOnToSaver()
/* 142:    */   {
/* 143:242 */     if (((this.m_SaverTemplate instanceof EnvironmentHandler)) && (this.m_env != null)) {
/* 144:243 */       ((EnvironmentHandler)this.m_Saver).setEnvironment(this.m_env);
/* 145:    */     }
/* 146:    */   }
/* 147:    */   
/* 148:    */   public void setSaverTemplate(weka.core.converters.Saver saver)
/* 149:    */   {
/* 150:253 */     boolean loadImages = true;
/* 151:254 */     if (saver.getClass().getName().compareTo(this.m_SaverTemplate.getClass().getName()) == 0) {
/* 152:256 */       loadImages = false;
/* 153:    */     }
/* 154:258 */     this.m_SaverTemplate = saver;
/* 155:259 */     String saverName = saver.getClass().toString();
/* 156:260 */     saverName = saverName.substring(saverName.lastIndexOf('.') + 1, saverName.length());
/* 157:262 */     if (loadImages) {
/* 158:264 */       if (!this.m_visual.loadIcons("weka/gui/beans/icons/" + saverName + ".gif", "weka/gui/beans/icons/" + saverName + "_animated.gif")) {
/* 159:266 */         useDefaultVisual();
/* 160:    */       }
/* 161:    */     }
/* 162:269 */     this.m_visual.setText(saverName);
/* 163:    */     
/* 164:    */ 
/* 165:272 */     this.m_globalInfo = KnowledgeFlowApp.getGlobalInfo(this.m_SaverTemplate);
/* 166:273 */     if ((this.m_SaverTemplate instanceof DatabaseConverter)) {
/* 167:274 */       this.m_isDBSaver = true;
/* 168:    */     } else {
/* 169:276 */       this.m_isDBSaver = false;
/* 170:    */     }
/* 171:    */   }
/* 172:    */   
/* 173:    */   protected String sanitizeFilename(String filename)
/* 174:    */   {
/* 175:289 */     filename = filename.replaceAll("\\\\", "_").replaceAll(":", "_").replaceAll("/", "_");
/* 176:    */     
/* 177:291 */     filename = Utils.removeSubstring(filename, "weka.filters.supervised.instance.");
/* 178:    */     
/* 179:293 */     filename = Utils.removeSubstring(filename, "weka.filters.supervised.attribute.");
/* 180:    */     
/* 181:295 */     filename = Utils.removeSubstring(filename, "weka.filters.unsupervised.instance.");
/* 182:    */     
/* 183:297 */     filename = Utils.removeSubstring(filename, "weka.filters.unsupervised.attribute.");
/* 184:    */     
/* 185:299 */     filename = Utils.removeSubstring(filename, "weka.clusterers.");
/* 186:300 */     filename = Utils.removeSubstring(filename, "weka.associations.");
/* 187:301 */     filename = Utils.removeSubstring(filename, "weka.attributeSelection.");
/* 188:302 */     filename = Utils.removeSubstring(filename, "weka.estimators.");
/* 189:303 */     filename = Utils.removeSubstring(filename, "weka.datagenerators.");
/* 190:305 */     if ((!this.m_isDBSaver) && (!this.m_relationNameForFilename))
/* 191:    */     {
/* 192:306 */       filename = "";
/* 193:    */       try
/* 194:    */       {
/* 195:308 */         if (this.m_Saver.filePrefix().equals("")) {
/* 196:309 */           this.m_Saver.setFilePrefix("no-name");
/* 197:    */         }
/* 198:    */       }
/* 199:    */       catch (Exception ex)
/* 200:    */       {
/* 201:312 */         System.err.println(ex);
/* 202:    */       }
/* 203:    */     }
/* 204:316 */     return filename;
/* 205:    */   }
/* 206:    */   
/* 207:    */   public synchronized void acceptDataSet(DataSetEvent e)
/* 208:    */   {
/* 209:    */     try
/* 210:    */     {
/* 211:329 */       this.m_Saver = makeCopy();
/* 212:    */     }
/* 213:    */     catch (Exception ex)
/* 214:    */     {
/* 215:331 */       if (this.m_logger != null)
/* 216:    */       {
/* 217:332 */         this.m_logger.statusMessage(statusMessagePrefix() + "ERROR (See log for details)");
/* 218:    */         
/* 219:334 */         this.m_logger.logMessage("[Saver] " + statusMessagePrefix() + " unable to copy saver. " + ex.getMessage());
/* 220:    */       }
/* 221:    */     }
/* 222:338 */     passEnvOnToSaver();
/* 223:339 */     this.m_fileName = sanitizeFilename(e.getDataSet().relationName());
/* 224:340 */     this.m_dataSet = e.getDataSet();
/* 225:341 */     if ((e.isStructureOnly()) && (this.m_isDBSaver) && (((DatabaseSaver)this.m_SaverTemplate).getRelationForTableName())) {
/* 226:343 */       ((DatabaseSaver)this.m_Saver).setTableName(this.m_fileName);
/* 227:    */     }
/* 228:345 */     if (!e.isStructureOnly())
/* 229:    */     {
/* 230:346 */       if (!this.m_isDBSaver) {
/* 231:    */         try
/* 232:    */         {
/* 233:348 */           this.m_Saver.setDirAndPrefix(this.m_fileName, "");
/* 234:    */         }
/* 235:    */         catch (Exception ex)
/* 236:    */         {
/* 237:350 */           System.out.println(ex);
/* 238:    */         }
/* 239:    */       }
/* 240:353 */       saveBatch();
/* 241:354 */       System.out.println("...relation " + this.m_fileName + " saved.");
/* 242:    */     }
/* 243:    */   }
/* 244:    */   
/* 245:    */   public synchronized void acceptDataSet(ThresholdDataEvent e)
/* 246:    */   {
/* 247:    */     try
/* 248:    */     {
/* 249:367 */       this.m_Saver = makeCopy();
/* 250:    */     }
/* 251:    */     catch (Exception ex)
/* 252:    */     {
/* 253:369 */       if (this.m_logger != null)
/* 254:    */       {
/* 255:370 */         this.m_logger.statusMessage(statusMessagePrefix() + "ERROR (See log for details)");
/* 256:    */         
/* 257:372 */         this.m_logger.logMessage("[Saver] " + statusMessagePrefix() + " unable to copy saver. " + ex.getMessage());
/* 258:    */       }
/* 259:    */     }
/* 260:377 */     passEnvOnToSaver();
/* 261:378 */     this.m_fileName = sanitizeFilename(e.getDataSet().getPlotInstances().relationName());
/* 262:    */     
/* 263:380 */     this.m_dataSet = e.getDataSet().getPlotInstances();
/* 264:382 */     if ((this.m_isDBSaver) && (((DatabaseSaver)this.m_SaverTemplate).getRelationForTableName()))
/* 265:    */     {
/* 266:384 */       ((DatabaseSaver)this.m_Saver).setTableName(this.m_fileName);
/* 267:385 */       ((DatabaseSaver)this.m_Saver).setRelationForTableName(false);
/* 268:    */     }
/* 269:388 */     if (!this.m_isDBSaver) {
/* 270:    */       try
/* 271:    */       {
/* 272:390 */         this.m_Saver.setDirAndPrefix(this.m_fileName, "");
/* 273:    */       }
/* 274:    */       catch (Exception ex)
/* 275:    */       {
/* 276:392 */         System.out.println(ex);
/* 277:    */       }
/* 278:    */     }
/* 279:395 */     saveBatch();
/* 280:396 */     System.out.println("...relation " + this.m_fileName + " saved.");
/* 281:    */   }
/* 282:    */   
/* 283:    */   public synchronized void acceptTestSet(TestSetEvent e)
/* 284:    */   {
/* 285:    */     try
/* 286:    */     {
/* 287:409 */       this.m_Saver = makeCopy();
/* 288:    */     }
/* 289:    */     catch (Exception ex)
/* 290:    */     {
/* 291:411 */       if (this.m_logger != null)
/* 292:    */       {
/* 293:412 */         this.m_logger.statusMessage(statusMessagePrefix() + "ERROR (See log for details)");
/* 294:    */         
/* 295:414 */         this.m_logger.logMessage("[Saver] " + statusMessagePrefix() + " unable to copy saver. " + ex.getMessage());
/* 296:    */       }
/* 297:    */     }
/* 298:419 */     passEnvOnToSaver();
/* 299:420 */     this.m_fileName = sanitizeFilename(e.getTestSet().relationName());
/* 300:421 */     this.m_dataSet = e.getTestSet();
/* 301:422 */     if ((e.isStructureOnly()) && (this.m_isDBSaver) && (((DatabaseSaver)this.m_SaverTemplate).getRelationForTableName())) {
/* 302:424 */       ((DatabaseSaver)this.m_Saver).setTableName(this.m_fileName);
/* 303:    */     }
/* 304:426 */     if (!e.isStructureOnly())
/* 305:    */     {
/* 306:427 */       if (!this.m_isDBSaver)
/* 307:    */       {
/* 308:    */         try
/* 309:    */         {
/* 310:429 */           this.m_Saver.setDirAndPrefix(this.m_fileName, "_test_" + e.getSetNumber() + "_of_" + e.getMaxSetNumber());
/* 311:    */         }
/* 312:    */         catch (Exception ex)
/* 313:    */         {
/* 314:432 */           System.out.println(ex);
/* 315:    */         }
/* 316:    */       }
/* 317:    */       else
/* 318:    */       {
/* 319:435 */         ((DatabaseSaver)this.m_Saver).setRelationForTableName(false);
/* 320:436 */         String setName = ((DatabaseSaver)this.m_Saver).getTableName();
/* 321:437 */         setName = setName.replaceFirst("_[tT][eE][sS][tT]_[0-9]+_[oO][fF]_[0-9]+", "");
/* 322:    */         
/* 323:439 */         ((DatabaseSaver)this.m_Saver).setTableName(setName + "_test_" + e.getSetNumber() + "_of_" + e.getMaxSetNumber());
/* 324:    */       }
/* 325:442 */       saveBatch();
/* 326:443 */       System.out.println("... test set " + e.getSetNumber() + " of " + e.getMaxSetNumber() + " for relation " + this.m_fileName + " saved.");
/* 327:    */     }
/* 328:    */   }
/* 329:    */   
/* 330:    */   public synchronized void acceptTrainingSet(TrainingSetEvent e)
/* 331:    */   {
/* 332:    */     try
/* 333:    */     {
/* 334:457 */       this.m_Saver = makeCopy();
/* 335:    */     }
/* 336:    */     catch (Exception ex)
/* 337:    */     {
/* 338:459 */       if (this.m_logger != null)
/* 339:    */       {
/* 340:460 */         this.m_logger.statusMessage(statusMessagePrefix() + "ERROR (See log for details)");
/* 341:    */         
/* 342:462 */         this.m_logger.logMessage("[Saver] " + statusMessagePrefix() + " unable to copy saver. " + ex.getMessage());
/* 343:    */       }
/* 344:    */     }
/* 345:467 */     passEnvOnToSaver();
/* 346:468 */     this.m_fileName = sanitizeFilename(e.getTrainingSet().relationName());
/* 347:469 */     this.m_dataSet = e.getTrainingSet();
/* 348:470 */     if ((e.isStructureOnly()) && (this.m_isDBSaver) && (((DatabaseSaver)this.m_SaverTemplate).getRelationForTableName())) {
/* 349:472 */       ((DatabaseSaver)this.m_Saver).setTableName(this.m_fileName);
/* 350:    */     }
/* 351:474 */     if (!e.isStructureOnly())
/* 352:    */     {
/* 353:475 */       if (!this.m_isDBSaver)
/* 354:    */       {
/* 355:    */         try
/* 356:    */         {
/* 357:477 */           this.m_Saver.setDirAndPrefix(this.m_fileName, "_training_" + e.getSetNumber() + "_of_" + e.getMaxSetNumber());
/* 358:    */         }
/* 359:    */         catch (Exception ex)
/* 360:    */         {
/* 361:480 */           System.out.println(ex);
/* 362:    */         }
/* 363:    */       }
/* 364:    */       else
/* 365:    */       {
/* 366:483 */         ((DatabaseSaver)this.m_Saver).setRelationForTableName(false);
/* 367:484 */         String setName = ((DatabaseSaver)this.m_Saver).getTableName();
/* 368:485 */         setName = setName.replaceFirst("_[tT][rR][aA][iI][nN][iI][nN][gG]_[0-9]+_[oO][fF]_[0-9]+", "");
/* 369:    */         
/* 370:487 */         ((DatabaseSaver)this.m_Saver).setTableName(setName + "_training_" + e.getSetNumber() + "_of_" + e.getMaxSetNumber());
/* 371:    */       }
/* 372:490 */       saveBatch();
/* 373:491 */       System.out.println("... training set " + e.getSetNumber() + " of " + e.getMaxSetNumber() + " for relation " + this.m_fileName + " saved.");
/* 374:    */     }
/* 375:    */   }
/* 376:    */   
/* 377:    */   public synchronized void saveBatch()
/* 378:    */   {
/* 379:499 */     this.m_Saver.setRetrieval(1);
/* 380:    */     
/* 381:    */ 
/* 382:    */ 
/* 383:    */ 
/* 384:    */ 
/* 385:505 */     this.m_ioThread = new SaveBatchThread(this);
/* 386:506 */     this.m_ioThread.setPriority(1);
/* 387:507 */     this.m_ioThread.start();
/* 388:508 */     block(true);
/* 389:    */   }
/* 390:    */   
/* 391:    */   public synchronized void acceptInstance(InstanceEvent e)
/* 392:    */   {
/* 393:524 */     if (e.getStatus() == 0)
/* 394:    */     {
/* 395:525 */       this.m_throughput = new StreamThroughput(statusMessagePrefix());
/* 396:    */       try
/* 397:    */       {
/* 398:528 */         this.m_Saver = makeCopy();
/* 399:    */       }
/* 400:    */       catch (Exception ex)
/* 401:    */       {
/* 402:530 */         if (this.m_logger != null)
/* 403:    */         {
/* 404:531 */           this.m_logger.statusMessage(statusMessagePrefix() + "ERROR (See log for details)");
/* 405:    */           
/* 406:533 */           this.m_logger.logMessage("[Saver] " + statusMessagePrefix() + " unable to copy saver. " + ex.getMessage());
/* 407:    */         }
/* 408:    */       }
/* 409:537 */       this.m_Saver.setRetrieval(2);
/* 410:538 */       this.m_structure = e.getStructure();
/* 411:539 */       this.m_fileName = sanitizeFilename(this.m_structure.relationName());
/* 412:540 */       this.m_Saver.setInstances(this.m_structure);
/* 413:541 */       if ((this.m_isDBSaver) && 
/* 414:542 */         (((DatabaseSaver)this.m_SaverTemplate).getRelationForTableName()))
/* 415:    */       {
/* 416:543 */         ((DatabaseSaver)this.m_Saver).setTableName(this.m_fileName);
/* 417:544 */         ((DatabaseSaver)this.m_Saver).setRelationForTableName(false);
/* 418:    */       }
/* 419:    */     }
/* 420:548 */     if (e.getStatus() == 1)
/* 421:    */     {
/* 422:549 */       this.m_visual.setAnimated();
/* 423:550 */       this.m_throughput.updateStart();
/* 424:551 */       if (this.m_count == 0)
/* 425:    */       {
/* 426:552 */         passEnvOnToSaver();
/* 427:553 */         if (!this.m_isDBSaver) {
/* 428:    */           try
/* 429:    */           {
/* 430:555 */             this.m_Saver.setDirAndPrefix(this.m_fileName, "");
/* 431:    */           }
/* 432:    */           catch (Exception ex)
/* 433:    */           {
/* 434:557 */             System.out.println(ex);
/* 435:558 */             this.m_visual.setStatic();
/* 436:    */           }
/* 437:    */         }
/* 438:561 */         this.m_count += 1;
/* 439:    */       }
/* 440:    */       try
/* 441:    */       {
/* 442:568 */         this.m_Saver.writeIncremental(e.getInstance());
/* 443:569 */         this.m_throughput.updateEnd(this.m_logger);
/* 444:    */       }
/* 445:    */       catch (Exception ex)
/* 446:    */       {
/* 447:571 */         this.m_visual.setStatic();
/* 448:572 */         System.err.println("Instance " + e.getInstance() + " could not been saved");
/* 449:    */         
/* 450:574 */         ex.printStackTrace();
/* 451:    */       }
/* 452:    */     }
/* 453:577 */     if (e.getStatus() == 2) {
/* 454:    */       try
/* 455:    */       {
/* 456:579 */         if (this.m_count == 0)
/* 457:    */         {
/* 458:580 */           passEnvOnToSaver();
/* 459:581 */           if (!this.m_isDBSaver) {
/* 460:    */             try
/* 461:    */             {
/* 462:583 */               this.m_Saver.setDirAndPrefix(this.m_fileName, "");
/* 463:    */             }
/* 464:    */             catch (Exception ex)
/* 465:    */             {
/* 466:585 */               System.out.println(ex);
/* 467:586 */               this.m_visual.setStatic();
/* 468:    */             }
/* 469:    */           }
/* 470:589 */           this.m_count += 1;
/* 471:    */         }
/* 472:591 */         this.m_Saver.writeIncremental(e.getInstance());
/* 473:592 */         if (e.getInstance() != null)
/* 474:    */         {
/* 475:593 */           this.m_throughput.updateStart();
/* 476:594 */           this.m_Saver.writeIncremental(null);
/* 477:595 */           this.m_throughput.updateEnd(this.m_logger);
/* 478:    */         }
/* 479:598 */         this.m_visual.setStatic();
/* 480:    */         
/* 481:    */ 
/* 482:    */ 
/* 483:    */ 
/* 484:    */ 
/* 485:604 */         this.m_count = 0;
/* 486:605 */         this.m_throughput.finished(this.m_logger);
/* 487:    */       }
/* 488:    */       catch (Exception ex)
/* 489:    */       {
/* 490:607 */         this.m_visual.setStatic();
/* 491:608 */         System.err.println("File could not have been closed.");
/* 492:609 */         ex.printStackTrace();
/* 493:    */       }
/* 494:    */     }
/* 495:    */   }
/* 496:    */   
/* 497:    */   public weka.core.converters.Saver getSaverTemplate()
/* 498:    */   {
/* 499:620 */     return this.m_SaverTemplate;
/* 500:    */   }
/* 501:    */   
/* 502:    */   public void setWrappedAlgorithm(Object algorithm)
/* 503:    */   {
/* 504:630 */     if (!(algorithm instanceof weka.core.converters.Saver)) {
/* 505:631 */       throw new IllegalArgumentException(algorithm.getClass() + " : incorrect " + "type of algorithm (Loader)");
/* 506:    */     }
/* 507:634 */     setSaverTemplate((weka.core.converters.Saver)algorithm);
/* 508:    */   }
/* 509:    */   
/* 510:    */   public Object getWrappedAlgorithm()
/* 511:    */   {
/* 512:644 */     return getSaverTemplate();
/* 513:    */   }
/* 514:    */   
/* 515:    */   public void setRelationNameForFilename(boolean r)
/* 516:    */   {
/* 517:654 */     this.m_relationNameForFilename = r;
/* 518:    */   }
/* 519:    */   
/* 520:    */   public boolean getRelationNameForFilename()
/* 521:    */   {
/* 522:663 */     return this.m_relationNameForFilename;
/* 523:    */   }
/* 524:    */   
/* 525:    */   public void stop()
/* 526:    */   {
/* 527:672 */     if ((this.m_listenee instanceof BeanCommon)) {
/* 528:673 */       ((BeanCommon)this.m_listenee).stop();
/* 529:    */     }
/* 530:677 */     if (this.m_ioThread != null)
/* 531:    */     {
/* 532:678 */       this.m_ioThread.interrupt();
/* 533:679 */       this.m_ioThread.stop();
/* 534:680 */       this.m_ioThread = null;
/* 535:    */     }
/* 536:683 */     this.m_count = 0;
/* 537:    */     
/* 538:685 */     this.m_visual.setStatic();
/* 539:    */   }
/* 540:    */   
/* 541:    */   private String statusMessagePrefix()
/* 542:    */   {
/* 543:689 */     return getCustomName() + "$" + hashCode() + "|" + ((this.m_Saver instanceof OptionHandler) ? Utils.joinOptions(((OptionHandler)this.m_Saver).getOptions()) + "|" : "");
/* 544:    */   }
/* 545:    */   
/* 546:    */   private void readObject(ObjectInputStream aStream)
/* 547:    */     throws IOException, ClassNotFoundException
/* 548:    */   {
/* 549:701 */     aStream.defaultReadObject();
/* 550:    */     
/* 551:    */ 
/* 552:704 */     this.m_env = Environment.getSystemWide();
/* 553:    */   }
/* 554:    */   
/* 555:    */   public static void main(String[] args)
/* 556:    */   {
/* 557:    */     try
/* 558:    */     {
/* 559:714 */       JFrame jf = new JFrame();
/* 560:715 */       jf.getContentPane().setLayout(new BorderLayout());
/* 561:    */       
/* 562:717 */       Saver tv = new Saver();
/* 563:    */       
/* 564:719 */       jf.getContentPane().add(tv, "Center");
/* 565:720 */       jf.addWindowListener(new WindowAdapter()
/* 566:    */       {
/* 567:    */         public void windowClosing(WindowEvent e)
/* 568:    */         {
/* 569:723 */           this.val$jf.dispose();
/* 570:724 */           System.exit(0);
/* 571:    */         }
/* 572:726 */       });
/* 573:727 */       jf.setSize(800, 600);
/* 574:728 */       jf.setVisible(true);
/* 575:    */     }
/* 576:    */     catch (Exception ex)
/* 577:    */     {
/* 578:730 */       ex.printStackTrace();
/* 579:    */     }
/* 580:    */   }
/* 581:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.Saver
 * JD-Core Version:    0.7.0.1
 */