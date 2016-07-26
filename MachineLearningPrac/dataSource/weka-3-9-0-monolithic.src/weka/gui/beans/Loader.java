/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.event.ActionEvent;
/*   6:    */ import java.awt.event.ActionListener;
/*   7:    */ import java.awt.event.WindowAdapter;
/*   8:    */ import java.awt.event.WindowEvent;
/*   9:    */ import java.beans.EventSetDescriptor;
/*  10:    */ import java.beans.beancontext.BeanContext;
/*  11:    */ import java.io.File;
/*  12:    */ import java.io.IOException;
/*  13:    */ import java.io.ObjectInputStream;
/*  14:    */ import java.io.ObjectStreamException;
/*  15:    */ import java.io.PrintStream;
/*  16:    */ import java.util.Vector;
/*  17:    */ import javax.swing.JButton;
/*  18:    */ import javax.swing.JFrame;
/*  19:    */ import weka.core.Environment;
/*  20:    */ import weka.core.EnvironmentHandler;
/*  21:    */ import weka.core.Instance;
/*  22:    */ import weka.core.Instances;
/*  23:    */ import weka.core.OptionHandler;
/*  24:    */ import weka.core.SerializedObject;
/*  25:    */ import weka.core.Utils;
/*  26:    */ import weka.core.converters.ArffLoader;
/*  27:    */ import weka.core.converters.BatchConverter;
/*  28:    */ import weka.core.converters.DatabaseLoader;
/*  29:    */ import weka.core.converters.FileSourcedConverter;
/*  30:    */ import weka.core.converters.IncrementalConverter;
/*  31:    */ import weka.core.converters.Loader.StructureNotReadyException;
/*  32:    */ import weka.gui.Logger;
/*  33:    */ 
/*  34:    */ public class Loader
/*  35:    */   extends AbstractDataSource
/*  36:    */   implements Startable, WekaWrapper, EventConstraints, BeanCommon, EnvironmentHandler, StructureProducer
/*  37:    */ {
/*  38:    */   private static final long serialVersionUID = 1993738191961163027L;
/*  39:    */   private transient Instances m_dataSet;
/*  40:    */   private transient Instances m_dataFormat;
/*  41:    */   protected String m_globalInfo;
/*  42:    */   private LoadThread m_ioThread;
/*  43: 86 */   private static int IDLE = 0;
/*  44: 87 */   private static int BATCH_LOADING = 1;
/*  45: 88 */   private static int INCREMENTAL_LOADING = 2;
/*  46: 89 */   private int m_state = IDLE;
/*  47: 94 */   private weka.core.converters.Loader m_Loader = new ArffLoader();
/*  48: 96 */   private final InstanceEvent m_ie = new InstanceEvent(this);
/*  49:101 */   private int m_instanceEventTargets = 0;
/*  50:102 */   private int m_dataSetEventTargets = 0;
/*  51:105 */   private boolean m_dbSet = false;
/*  52:    */   protected transient Logger m_log;
/*  53:    */   protected transient Environment m_env;
/*  54:120 */   protected boolean m_stopped = false;
/*  55:    */   
/*  56:    */   private class LoadThread
/*  57:    */     extends Thread
/*  58:    */   {
/*  59:    */     private final DataSource m_DP;
/*  60:    */     private StreamThroughput m_throughput;
/*  61:    */     private StreamThroughput m_flowThroughput;
/*  62:    */     
/*  63:    */     public LoadThread(DataSource dp)
/*  64:    */     {
/*  65:128 */       this.m_DP = dp;
/*  66:    */     }
/*  67:    */     
/*  68:    */     public void run()
/*  69:    */     {
/*  70:134 */       String stm = Loader.this.getCustomName() + "$" + hashCode() + 99 + "| - overall flow throughput -|";
/*  71:    */       try
/*  72:    */       {
/*  73:137 */         Loader.this.m_visual.setAnimated();
/*  74:    */         
/*  75:    */ 
/*  76:140 */         boolean instanceGeneration = true;
/*  77:147 */         if (Loader.this.m_dataSetEventTargets > 0)
/*  78:    */         {
/*  79:148 */           instanceGeneration = false;
/*  80:149 */           Loader.this.m_state = Loader.BATCH_LOADING;
/*  81:    */         }
/*  82:153 */         if (((Loader.this.m_Loader instanceof EnvironmentHandler)) && (Loader.this.m_env != null)) {
/*  83:154 */           ((EnvironmentHandler)Loader.this.m_Loader).setEnvironment(Loader.this.m_env);
/*  84:    */         }
/*  85:157 */         String msg = Loader.this.statusMessagePrefix();
/*  86:158 */         if ((Loader.this.m_Loader instanceof FileSourcedConverter)) {
/*  87:159 */           msg = msg + "Loading " + ((FileSourcedConverter)Loader.this.m_Loader).retrieveFile().getName();
/*  88:    */         } else {
/*  89:162 */           msg = msg + "Loading...";
/*  90:    */         }
/*  91:164 */         if (Loader.this.m_log != null) {
/*  92:165 */           Loader.this.m_log.statusMessage(msg);
/*  93:    */         }
/*  94:168 */         if (instanceGeneration)
/*  95:    */         {
/*  96:169 */           this.m_throughput = new StreamThroughput(Loader.this.statusMessagePrefix());
/*  97:    */           
/*  98:171 */           this.m_flowThroughput = new StreamThroughput(stm, "Starting flow...", Loader.this.m_log);
/*  99:    */           
/* 100:    */ 
/* 101:174 */           Loader.this.m_state = Loader.INCREMENTAL_LOADING;
/* 102:    */           
/* 103:176 */           Instance nextInstance = null;
/* 104:    */           
/* 105:178 */           Instances structure = null;
/* 106:179 */           Instances structureCopy = null;
/* 107:180 */           Instances currentStructure = null;
/* 108:181 */           boolean stringAttsPresent = false;
/* 109:    */           try
/* 110:    */           {
/* 111:183 */             Loader.this.m_Loader.reset();
/* 112:184 */             Loader.this.m_Loader.setRetrieval(2);
/* 113:    */             
/* 114:186 */             structure = Loader.this.m_Loader.getStructure();
/* 115:187 */             if (structure.checkForStringAttributes())
/* 116:    */             {
/* 117:188 */               structureCopy = (Instances)new SerializedObject(structure).getObject();
/* 118:    */               
/* 119:190 */               stringAttsPresent = true;
/* 120:    */             }
/* 121:192 */             currentStructure = structure;
/* 122:193 */             Loader.this.m_ie.m_formatNotificationOnly = false;
/* 123:194 */             Loader.this.notifyStructureAvailable(structure);
/* 124:    */           }
/* 125:    */           catch (IOException e)
/* 126:    */           {
/* 127:196 */             if (Loader.this.m_log != null)
/* 128:    */             {
/* 129:197 */               Loader.this.m_log.statusMessage(Loader.this.statusMessagePrefix() + "ERROR (See log for details");
/* 130:    */               
/* 131:199 */               Loader.this.m_log.logMessage("[Loader] " + Loader.this.statusMessagePrefix() + " " + e.getMessage());
/* 132:    */             }
/* 133:202 */             e.printStackTrace();
/* 134:    */           }
/* 135:    */           try
/* 136:    */           {
/* 137:205 */             nextInstance = Loader.this.m_Loader.getNextInstance(structure);
/* 138:    */           }
/* 139:    */           catch (IOException e)
/* 140:    */           {
/* 141:207 */             if (Loader.this.m_log != null)
/* 142:    */             {
/* 143:208 */               Loader.this.m_log.statusMessage(Loader.this.statusMessagePrefix() + "ERROR (See log for details");
/* 144:    */               
/* 145:210 */               Loader.this.m_log.logMessage("[Loader] " + Loader.this.statusMessagePrefix() + " " + e.getMessage());
/* 146:    */             }
/* 147:213 */             e.printStackTrace();
/* 148:    */           }
/* 149:216 */           while ((nextInstance != null) && 
/* 150:217 */             (!Loader.this.m_stopped))
/* 151:    */           {
/* 152:220 */             this.m_throughput.updateStart();
/* 153:221 */             this.m_flowThroughput.updateStart();
/* 154:    */             
/* 155:    */ 
/* 156:    */ 
/* 157:    */ 
/* 158:    */ 
/* 159:    */ 
/* 160:    */ 
/* 161:    */ 
/* 162:    */ 
/* 163:    */ 
/* 164:    */ 
/* 165:233 */             Loader.this.m_ie.setStatus(1);
/* 166:    */             
/* 167:235 */             Loader.this.m_ie.setInstance(nextInstance);
/* 168:246 */             if (stringAttsPresent) {
/* 169:247 */               if (currentStructure == structure) {
/* 170:248 */                 currentStructure = structureCopy;
/* 171:    */               } else {
/* 172:250 */                 currentStructure = structure;
/* 173:    */               }
/* 174:    */             }
/* 175:253 */             nextInstance = Loader.this.m_Loader.getNextInstance(currentStructure);
/* 176:255 */             if (nextInstance == null) {
/* 177:256 */               Loader.this.m_ie.setStatus(2);
/* 178:    */             }
/* 179:258 */             this.m_throughput.updateEnd(Loader.this.m_log);
/* 180:259 */             Loader.this.notifyInstanceLoaded(Loader.this.m_ie);
/* 181:260 */             this.m_flowThroughput.updateEnd(Loader.this.m_log);
/* 182:    */           }
/* 183:262 */           Loader.this.m_visual.setStatic();
/* 184:    */         }
/* 185:    */         else
/* 186:    */         {
/* 187:265 */           Loader.this.m_Loader.reset();
/* 188:266 */           Loader.this.m_Loader.setRetrieval(1);
/* 189:267 */           Loader.this.m_dataSet = Loader.this.m_Loader.getDataSet();
/* 190:268 */           Loader.this.m_visual.setStatic();
/* 191:269 */           if (Loader.this.m_log != null) {
/* 192:270 */             Loader.this.m_log.logMessage("[Loader] " + Loader.this.statusMessagePrefix() + " loaded " + Loader.this.m_dataSet.relationName());
/* 193:    */           }
/* 194:274 */           Loader.this.notifyDataSetLoaded(new DataSetEvent(this.m_DP, Loader.this.m_dataSet));
/* 195:    */         }
/* 196:    */       }
/* 197:    */       catch (Exception ex)
/* 198:    */       {
/* 199:    */         String finalMessage;
/* 200:    */         int flowSpeed;
/* 201:277 */         if (Loader.this.m_log != null)
/* 202:    */         {
/* 203:278 */           Loader.this.m_log.statusMessage(Loader.this.statusMessagePrefix() + "ERROR (See log for details");
/* 204:    */           
/* 205:280 */           Loader.this.m_log.logMessage("[Loader] " + Loader.this.statusMessagePrefix() + " " + ex.getMessage());
/* 206:    */         }
/* 207:283 */         ex.printStackTrace();
/* 208:    */       }
/* 209:    */       finally
/* 210:    */       {
/* 211:    */         String finalMessage;
/* 212:    */         int flowSpeed;
/* 213:285 */         if ((Thread.currentThread().isInterrupted()) && 
/* 214:286 */           (Loader.this.m_log != null)) {
/* 215:287 */           Loader.this.m_log.logMessage("[Loader] " + Loader.this.statusMessagePrefix() + " loading interrupted!");
/* 216:    */         }
/* 217:291 */         Loader.this.m_ioThread = null;
/* 218:    */         
/* 219:    */ 
/* 220:294 */         Loader.this.m_visual.setStatic();
/* 221:295 */         Loader.this.m_state = Loader.IDLE;
/* 222:296 */         Loader.this.m_stopped = false;
/* 223:297 */         if (Loader.this.m_log != null) {
/* 224:298 */           if (this.m_throughput != null)
/* 225:    */           {
/* 226:299 */             String finalMessage = this.m_throughput.finished() + " (read speed); ";
/* 227:300 */             this.m_flowThroughput.finished(Loader.this.m_log);
/* 228:301 */             Loader.this.m_log.statusMessage(stm + "remove");
/* 229:302 */             int flowSpeed = this.m_flowThroughput.getAverageInstancesPerSecond();
/* 230:303 */             finalMessage = finalMessage + "" + flowSpeed + " insts/sec (flow throughput)";
/* 231:304 */             Loader.this.m_log.statusMessage(Loader.this.statusMessagePrefix() + finalMessage);
/* 232:    */           }
/* 233:    */           else
/* 234:    */           {
/* 235:306 */             Loader.this.m_log.statusMessage(Loader.this.statusMessagePrefix() + "Finished.");
/* 236:    */           }
/* 237:    */         }
/* 238:309 */         Loader.this.block(false);
/* 239:    */       }
/* 240:    */     }
/* 241:    */   }
/* 242:    */   
/* 243:    */   public String globalInfo()
/* 244:    */   {
/* 245:320 */     return this.m_globalInfo;
/* 246:    */   }
/* 247:    */   
/* 248:    */   public Loader()
/* 249:    */   {
/* 250:325 */     setLoader(this.m_Loader);
/* 251:326 */     appearanceFinal();
/* 252:    */   }
/* 253:    */   
/* 254:    */   public void setDB(boolean flag)
/* 255:    */   {
/* 256:331 */     this.m_dbSet = flag;
/* 257:332 */     if (this.m_dbSet) {
/* 258:    */       try
/* 259:    */       {
/* 260:334 */         newStructure(new boolean[0]);
/* 261:    */       }
/* 262:    */       catch (Exception e)
/* 263:    */       {
/* 264:336 */         e.printStackTrace();
/* 265:    */       }
/* 266:    */     }
/* 267:    */   }
/* 268:    */   
/* 269:    */   protected void appearanceFinal()
/* 270:    */   {
/* 271:342 */     removeAll();
/* 272:343 */     setLayout(new BorderLayout());
/* 273:344 */     JButton goButton = new JButton("Start...");
/* 274:345 */     add(goButton, "Center");
/* 275:346 */     goButton.addActionListener(new ActionListener()
/* 276:    */     {
/* 277:    */       public void actionPerformed(ActionEvent e)
/* 278:    */       {
/* 279:349 */         Loader.this.startLoading();
/* 280:    */       }
/* 281:    */     });
/* 282:    */   }
/* 283:    */   
/* 284:    */   protected void appearanceDesign()
/* 285:    */   {
/* 286:355 */     removeAll();
/* 287:356 */     setLayout(new BorderLayout());
/* 288:357 */     add(this.m_visual, "Center");
/* 289:    */   }
/* 290:    */   
/* 291:    */   public void setBeanContext(BeanContext bc)
/* 292:    */   {
/* 293:367 */     super.setBeanContext(bc);
/* 294:368 */     if (this.m_design) {
/* 295:369 */       appearanceDesign();
/* 296:    */     } else {
/* 297:371 */       appearanceFinal();
/* 298:    */     }
/* 299:    */   }
/* 300:    */   
/* 301:    */   public void setLoader(weka.core.converters.Loader loader)
/* 302:    */   {
/* 303:381 */     boolean loadImages = true;
/* 304:382 */     if (loader.getClass().getName().compareTo(this.m_Loader.getClass().getName()) == 0) {
/* 305:383 */       loadImages = false;
/* 306:    */     }
/* 307:385 */     this.m_Loader = loader;
/* 308:386 */     String loaderName = loader.getClass().toString();
/* 309:387 */     loaderName = loaderName.substring(loaderName.lastIndexOf('.') + 1, loaderName.length());
/* 310:389 */     if (loadImages) {
/* 311:390 */       if ((this.m_Loader instanceof Visible)) {
/* 312:391 */         this.m_visual = ((Visible)this.m_Loader).getVisual();
/* 313:394 */       } else if (!this.m_visual.loadIcons("weka/gui/beans/icons/" + loaderName + ".gif", "weka/gui/beans/icons/" + loaderName + "_animated.gif")) {
/* 314:396 */         useDefaultVisual();
/* 315:    */       }
/* 316:    */     }
/* 317:400 */     this.m_visual.setText(loaderName);
/* 318:    */     
/* 319:    */ 
/* 320:403 */     this.m_globalInfo = KnowledgeFlowApp.getGlobalInfo(this.m_Loader);
/* 321:    */   }
/* 322:    */   
/* 323:    */   protected void newFileSelected()
/* 324:    */     throws Exception
/* 325:    */   {
/* 326:407 */     if (!(this.m_Loader instanceof DatabaseLoader)) {
/* 327:408 */       newStructure(new boolean[] { true });
/* 328:    */     }
/* 329:    */   }
/* 330:    */   
/* 331:    */   protected void newStructure(boolean... notificationOnly)
/* 332:    */     throws Exception
/* 333:    */   {
/* 334:425 */     if ((notificationOnly != null) && (notificationOnly.length > 0)) {
/* 335:429 */       this.m_ie.m_formatNotificationOnly = notificationOnly[0];
/* 336:    */     } else {
/* 337:431 */       this.m_ie.m_formatNotificationOnly = false;
/* 338:    */     }
/* 339:    */     try
/* 340:    */     {
/* 341:435 */       this.m_Loader.reset();
/* 342:438 */       if (((this.m_Loader instanceof EnvironmentHandler)) && (this.m_env != null)) {
/* 343:    */         try
/* 344:    */         {
/* 345:440 */           ((EnvironmentHandler)this.m_Loader).setEnvironment(this.m_env);
/* 346:    */         }
/* 347:    */         catch (Exception ex) {}
/* 348:    */       }
/* 349:444 */       this.m_dataFormat = this.m_Loader.getStructure();
/* 350:445 */       System.out.println("[Loader] Notifying listeners of instance structure avail.");
/* 351:    */       
/* 352:447 */       notifyStructureAvailable(this.m_dataFormat);
/* 353:    */     }
/* 354:    */     catch (Loader.StructureNotReadyException e)
/* 355:    */     {
/* 356:449 */       if (this.m_log != null)
/* 357:    */       {
/* 358:450 */         this.m_log.statusMessage(statusMessagePrefix() + "WARNING: " + e.getMessage());
/* 359:    */         
/* 360:452 */         this.m_log.logMessage("[Loader] " + statusMessagePrefix() + " " + e.getMessage());
/* 361:    */       }
/* 362:    */     }
/* 363:    */   }
/* 364:    */   
/* 365:    */   public Instances getStructure(String eventName)
/* 366:    */   {
/* 367:474 */     if ((!eventName.equals("dataSet")) && (!eventName.equals("instance"))) {
/* 368:475 */       return null;
/* 369:    */     }
/* 370:477 */     if ((this.m_dataSetEventTargets > 0) && (!eventName.equals("dataSet"))) {
/* 371:478 */       return null;
/* 372:    */     }
/* 373:480 */     if ((this.m_dataSetEventTargets == 0) && (!eventName.equals("instance"))) {
/* 374:481 */       return null;
/* 375:    */     }
/* 376:    */     try
/* 377:    */     {
/* 378:485 */       newStructure(new boolean[0]);
/* 379:    */     }
/* 380:    */     catch (Exception ex)
/* 381:    */     {
/* 382:489 */       System.err.println("[KnowledgeFlow/Loader] Warning: " + ex.getMessage());
/* 383:490 */       this.m_dataFormat = null;
/* 384:    */     }
/* 385:492 */     return this.m_dataFormat;
/* 386:    */   }
/* 387:    */   
/* 388:    */   public weka.core.converters.Loader getLoader()
/* 389:    */   {
/* 390:501 */     return this.m_Loader;
/* 391:    */   }
/* 392:    */   
/* 393:    */   public void setWrappedAlgorithm(Object algorithm)
/* 394:    */   {
/* 395:513 */     if (!(algorithm instanceof weka.core.converters.Loader)) {
/* 396:514 */       throw new IllegalArgumentException(algorithm.getClass() + " : incorrect " + "type of algorithm (Loader)");
/* 397:    */     }
/* 398:517 */     setLoader((weka.core.converters.Loader)algorithm);
/* 399:    */   }
/* 400:    */   
/* 401:    */   public Object getWrappedAlgorithm()
/* 402:    */   {
/* 403:527 */     return getLoader();
/* 404:    */   }
/* 405:    */   
/* 406:    */   protected void notifyStructureAvailable(Instances structure)
/* 407:    */   {
/* 408:536 */     if ((this.m_dataSetEventTargets > 0) && (structure != null))
/* 409:    */     {
/* 410:537 */       DataSetEvent dse = new DataSetEvent(this, structure);
/* 411:538 */       notifyDataSetLoaded(dse);
/* 412:    */     }
/* 413:539 */     else if ((this.m_instanceEventTargets > 0) && (structure != null))
/* 414:    */     {
/* 415:540 */       this.m_ie.setStructure(structure);
/* 416:541 */       notifyInstanceLoaded(this.m_ie);
/* 417:    */     }
/* 418:    */   }
/* 419:    */   
/* 420:    */   protected void notifyDataSetLoaded(DataSetEvent e)
/* 421:    */   {
/* 422:    */     Vector<DataSourceListener> l;
/* 423:553 */     synchronized (this)
/* 424:    */     {
/* 425:554 */       l = (Vector)this.m_listeners.clone();
/* 426:    */     }
/* 427:557 */     if (l.size() > 0)
/* 428:    */     {
/* 429:558 */       for (int i = 0; i < l.size(); i++) {
/* 430:559 */         ((DataSourceListener)l.elementAt(i)).acceptDataSet(e);
/* 431:    */       }
/* 432:561 */       this.m_dataSet = null;
/* 433:    */     }
/* 434:    */   }
/* 435:    */   
/* 436:    */   protected void notifyInstanceLoaded(InstanceEvent e)
/* 437:    */   {
/* 438:    */     Vector<InstanceListener> l;
/* 439:573 */     synchronized (this)
/* 440:    */     {
/* 441:574 */       l = (Vector)this.m_listeners.clone();
/* 442:    */     }
/* 443:577 */     if (l.size() > 0)
/* 444:    */     {
/* 445:578 */       for (int i = 0; i < l.size(); i++) {
/* 446:579 */         ((InstanceListener)l.elementAt(i)).acceptInstance(e);
/* 447:    */       }
/* 448:581 */       this.m_dataSet = null;
/* 449:    */     }
/* 450:    */   }
/* 451:    */   
/* 452:    */   public void startLoading()
/* 453:    */   {
/* 454:589 */     if (this.m_ioThread == null)
/* 455:    */     {
/* 456:591 */       this.m_state = BATCH_LOADING;
/* 457:592 */       this.m_stopped = false;
/* 458:593 */       this.m_ioThread = new LoadThread(this);
/* 459:594 */       this.m_ioThread.setPriority(1);
/* 460:595 */       this.m_ioThread.start();
/* 461:    */     }
/* 462:    */     else
/* 463:    */     {
/* 464:597 */       this.m_ioThread = null;
/* 465:598 */       this.m_state = IDLE;
/* 466:    */     }
/* 467:    */   }
/* 468:    */   
/* 469:    */   public void start()
/* 470:    */     throws Exception
/* 471:    */   {
/* 472:637 */     startLoading();
/* 473:638 */     block(true);
/* 474:    */   }
/* 475:    */   
/* 476:    */   public String getStartMessage()
/* 477:    */   {
/* 478:651 */     boolean ok = true;
/* 479:652 */     String entry = "Start loading";
/* 480:653 */     if (this.m_ioThread == null)
/* 481:    */     {
/* 482:654 */       if ((this.m_Loader instanceof FileSourcedConverter))
/* 483:    */       {
/* 484:655 */         String temp = ((FileSourcedConverter)this.m_Loader).retrieveFile().getPath();
/* 485:    */         
/* 486:657 */         Environment env = this.m_env == null ? Environment.getSystemWide() : this.m_env;
/* 487:    */         try
/* 488:    */         {
/* 489:659 */           temp = env.substitute(temp);
/* 490:    */         }
/* 491:    */         catch (Exception ex) {}
/* 492:662 */         File tempF = new File(temp);
/* 493:    */         
/* 494:    */ 
/* 495:    */ 
/* 496:666 */         String tempFixedPathSepForResource = temp.replace(File.separatorChar, '/');
/* 497:668 */         if ((!tempF.isFile()) && (getClass().getClassLoader().getResource(tempFixedPathSepForResource) == null)) {
/* 498:671 */           ok = false;
/* 499:    */         }
/* 500:    */       }
/* 501:674 */       if (!ok) {
/* 502:675 */         entry = "$" + entry;
/* 503:    */       }
/* 504:    */     }
/* 505:678 */     return entry;
/* 506:    */   }
/* 507:    */   
/* 508:    */   private synchronized void block(boolean tf)
/* 509:    */   {
/* 510:689 */     if (tf) {
/* 511:    */       try
/* 512:    */       {
/* 513:692 */         if ((this.m_ioThread.isAlive()) && (this.m_state != IDLE)) {
/* 514:693 */           wait();
/* 515:    */         }
/* 516:    */       }
/* 517:    */       catch (InterruptedException ex) {}
/* 518:    */     } else {
/* 519:698 */       notifyAll();
/* 520:    */     }
/* 521:    */   }
/* 522:    */   
/* 523:    */   public boolean eventGeneratable(String eventName)
/* 524:    */   {
/* 525:710 */     if (eventName.compareTo("instance") == 0)
/* 526:    */     {
/* 527:711 */       if (!(this.m_Loader instanceof IncrementalConverter)) {
/* 528:712 */         return false;
/* 529:    */       }
/* 530:714 */       if (this.m_dataSetEventTargets > 0) {
/* 531:715 */         return false;
/* 532:    */       }
/* 533:    */     }
/* 534:724 */     if (eventName.compareTo("dataSet") == 0)
/* 535:    */     {
/* 536:725 */       if (!(this.m_Loader instanceof BatchConverter)) {
/* 537:726 */         return false;
/* 538:    */       }
/* 539:728 */       if (this.m_instanceEventTargets > 0) {
/* 540:729 */         return false;
/* 541:    */       }
/* 542:    */     }
/* 543:737 */     return true;
/* 544:    */   }
/* 545:    */   
/* 546:    */   public synchronized void addDataSourceListener(DataSourceListener dsl)
/* 547:    */   {
/* 548:747 */     super.addDataSourceListener(dsl);
/* 549:748 */     this.m_dataSetEventTargets += 1;
/* 550:    */     try
/* 551:    */     {
/* 552:751 */       if ((((this.m_Loader instanceof DatabaseLoader)) && (this.m_dbSet) && (this.m_dataFormat == null)) || ((!(this.m_Loader instanceof DatabaseLoader)) && (this.m_dataFormat == null)))
/* 553:    */       {
/* 554:753 */         this.m_dataFormat = this.m_Loader.getStructure();
/* 555:754 */         this.m_dbSet = false;
/* 556:    */       }
/* 557:    */     }
/* 558:    */     catch (Exception ex) {}
/* 559:758 */     notifyStructureAvailable(this.m_dataFormat);
/* 560:    */   }
/* 561:    */   
/* 562:    */   public synchronized void removeDataSourceListener(DataSourceListener dsl)
/* 563:    */   {
/* 564:768 */     super.removeDataSourceListener(dsl);
/* 565:769 */     this.m_dataSetEventTargets -= 1;
/* 566:    */   }
/* 567:    */   
/* 568:    */   public synchronized void addInstanceListener(InstanceListener dsl)
/* 569:    */   {
/* 570:779 */     super.addInstanceListener(dsl);
/* 571:780 */     this.m_instanceEventTargets += 1;
/* 572:    */     try
/* 573:    */     {
/* 574:782 */       if ((((this.m_Loader instanceof DatabaseLoader)) && (this.m_dbSet) && (this.m_dataFormat == null)) || ((!(this.m_Loader instanceof DatabaseLoader)) && (this.m_dataFormat == null)))
/* 575:    */       {
/* 576:784 */         this.m_dataFormat = this.m_Loader.getStructure();
/* 577:785 */         this.m_dbSet = false;
/* 578:    */       }
/* 579:    */     }
/* 580:    */     catch (Exception ex) {}
/* 581:790 */     this.m_ie.m_formatNotificationOnly = true;
/* 582:791 */     notifyStructureAvailable(this.m_dataFormat);
/* 583:    */   }
/* 584:    */   
/* 585:    */   public synchronized void removeInstanceListener(InstanceListener dsl)
/* 586:    */   {
/* 587:801 */     super.removeInstanceListener(dsl);
/* 588:802 */     this.m_instanceEventTargets -= 1;
/* 589:    */   }
/* 590:    */   
/* 591:    */   public static void main(String[] args)
/* 592:    */   {
/* 593:    */     try
/* 594:    */     {
/* 595:807 */       JFrame jf = new JFrame();
/* 596:808 */       jf.getContentPane().setLayout(new BorderLayout());
/* 597:    */       
/* 598:810 */       Loader tv = new Loader();
/* 599:    */       
/* 600:812 */       jf.getContentPane().add(tv, "Center");
/* 601:813 */       jf.addWindowListener(new WindowAdapter()
/* 602:    */       {
/* 603:    */         public void windowClosing(WindowEvent e)
/* 604:    */         {
/* 605:816 */           this.val$jf.dispose();
/* 606:817 */           System.exit(0);
/* 607:    */         }
/* 608:819 */       });
/* 609:820 */       jf.setSize(800, 600);
/* 610:821 */       jf.setVisible(true);
/* 611:    */     }
/* 612:    */     catch (Exception ex)
/* 613:    */     {
/* 614:823 */       ex.printStackTrace();
/* 615:    */     }
/* 616:    */   }
/* 617:    */   
/* 618:    */   private Object readResolve()
/* 619:    */     throws ObjectStreamException
/* 620:    */   {
/* 621:829 */     if (this.m_Loader != null) {
/* 622:    */       try
/* 623:    */       {
/* 624:831 */         this.m_Loader.reset();
/* 625:    */       }
/* 626:    */       catch (Exception ex) {}
/* 627:    */     }
/* 628:835 */     return this;
/* 629:    */   }
/* 630:    */   
/* 631:    */   public void setCustomName(String name)
/* 632:    */   {
/* 633:845 */     this.m_visual.setText(name);
/* 634:    */   }
/* 635:    */   
/* 636:    */   public String getCustomName()
/* 637:    */   {
/* 638:855 */     return this.m_visual.getText();
/* 639:    */   }
/* 640:    */   
/* 641:    */   public void setLog(Logger logger)
/* 642:    */   {
/* 643:865 */     this.m_log = logger;
/* 644:    */   }
/* 645:    */   
/* 646:    */   public void setEnvironment(Environment env)
/* 647:    */   {
/* 648:875 */     this.m_env = env;
/* 649:    */   }
/* 650:    */   
/* 651:    */   public boolean connectionAllowed(EventSetDescriptor esd)
/* 652:    */   {
/* 653:887 */     return false;
/* 654:    */   }
/* 655:    */   
/* 656:    */   public boolean connectionAllowed(String eventName)
/* 657:    */   {
/* 658:899 */     return false;
/* 659:    */   }
/* 660:    */   
/* 661:    */   public void connectionNotification(String eventName, Object source) {}
/* 662:    */   
/* 663:    */   public void disconnectionNotification(String eventName, Object source) {}
/* 664:    */   
/* 665:    */   public void stop()
/* 666:    */   {
/* 667:934 */     this.m_stopped = true;
/* 668:    */   }
/* 669:    */   
/* 670:    */   public boolean isBusy()
/* 671:    */   {
/* 672:945 */     return this.m_ioThread != null;
/* 673:    */   }
/* 674:    */   
/* 675:    */   private String statusMessagePrefix()
/* 676:    */   {
/* 677:949 */     return getCustomName() + "$" + hashCode() + "|" + ((this.m_Loader instanceof OptionHandler) ? Utils.joinOptions(((OptionHandler)this.m_Loader).getOptions()) + "|" : "");
/* 678:    */   }
/* 679:    */   
/* 680:    */   private void readObject(ObjectInputStream aStream)
/* 681:    */     throws IOException, ClassNotFoundException
/* 682:    */   {
/* 683:961 */     aStream.defaultReadObject();
/* 684:    */     
/* 685:    */ 
/* 686:964 */     this.m_env = Environment.getSystemWide();
/* 687:    */   }
/* 688:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.Loader
 * JD-Core Version:    0.7.0.1
 */