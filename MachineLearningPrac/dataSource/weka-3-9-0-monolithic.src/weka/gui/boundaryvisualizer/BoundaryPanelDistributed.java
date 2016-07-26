/*   1:    */ package weka.gui.boundaryvisualizer;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Color;
/*   5:    */ import java.awt.Container;
/*   6:    */ import java.awt.event.WindowAdapter;
/*   7:    */ import java.awt.event.WindowEvent;
/*   8:    */ import java.io.BufferedReader;
/*   9:    */ import java.io.FileInputStream;
/*  10:    */ import java.io.FileReader;
/*  11:    */ import java.io.ObjectInputStream;
/*  12:    */ import java.io.PrintStream;
/*  13:    */ import java.io.Reader;
/*  14:    */ import java.rmi.Naming;
/*  15:    */ import java.util.ArrayList;
/*  16:    */ import java.util.Vector;
/*  17:    */ import javax.swing.JFrame;
/*  18:    */ import weka.classifiers.AbstractClassifier;
/*  19:    */ import weka.classifiers.Classifier;
/*  20:    */ import weka.core.Attribute;
/*  21:    */ import weka.core.Instances;
/*  22:    */ import weka.core.Queue;
/*  23:    */ import weka.core.Utils;
/*  24:    */ import weka.experiment.Compute;
/*  25:    */ import weka.experiment.RemoteExperimentEvent;
/*  26:    */ import weka.experiment.RemoteExperimentListener;
/*  27:    */ import weka.experiment.TaskStatusInfo;
/*  28:    */ 
/*  29:    */ public class BoundaryPanelDistributed
/*  30:    */   extends BoundaryPanel
/*  31:    */ {
/*  32:    */   private static final long serialVersionUID = -1743284397893937776L;
/*  33: 60 */   protected Vector<RemoteExperimentListener> m_listeners = new Vector();
/*  34: 63 */   protected Vector<String> m_remoteHosts = new Vector();
/*  35: 66 */   private Queue m_remoteHostsQueue = new Queue();
/*  36:    */   private int[] m_remoteHostsStatus;
/*  37:    */   private int[] m_remoteHostFailureCounts;
/*  38:    */   protected static final int AVAILABLE = 0;
/*  39:    */   protected static final int IN_USE = 1;
/*  40:    */   protected static final int CONNECTION_FAILED = 2;
/*  41:    */   protected static final int SOME_OTHER_FAILURE = 3;
/*  42:    */   protected static final int MAX_FAILURES = 3;
/*  43: 85 */   private boolean m_plottingAborted = false;
/*  44:    */   private int m_removedHosts;
/*  45:    */   private int m_failedCount;
/*  46: 94 */   private Queue m_subExpQueue = new Queue();
/*  47: 97 */   private final int m_minTaskPollTime = 1000;
/*  48:    */   private int[] m_hostPollingTime;
/*  49:    */   
/*  50:    */   public BoundaryPanelDistributed(int panelWidth, int panelHeight)
/*  51:    */   {
/*  52:108 */     super(panelWidth, panelHeight);
/*  53:    */   }
/*  54:    */   
/*  55:    */   public void setRemoteHosts(Vector<String> remHosts)
/*  56:    */   {
/*  57:117 */     this.m_remoteHosts = remHosts;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public void addRemoteExperimentListener(RemoteExperimentListener r)
/*  61:    */   {
/*  62:127 */     this.m_listeners.addElement(r);
/*  63:    */   }
/*  64:    */   
/*  65:    */   protected void initialize()
/*  66:    */   {
/*  67:132 */     super.initialize();
/*  68:    */     
/*  69:134 */     this.m_plottingAborted = false;
/*  70:135 */     this.m_failedCount = 0;
/*  71:    */     
/*  72:    */ 
/*  73:138 */     this.m_remoteHostsStatus = new int[this.m_remoteHosts.size()];
/*  74:139 */     this.m_remoteHostFailureCounts = new int[this.m_remoteHosts.size()];
/*  75:    */     
/*  76:141 */     this.m_remoteHostsQueue = new Queue();
/*  77:143 */     if (this.m_remoteHosts.size() == 0)
/*  78:    */     {
/*  79:144 */       System.err.println("No hosts specified!");
/*  80:145 */       System.exit(1);
/*  81:    */     }
/*  82:149 */     this.m_hostPollingTime = new int[this.m_remoteHosts.size()];
/*  83:150 */     for (int i = 0; i < this.m_remoteHosts.size(); i++)
/*  84:    */     {
/*  85:151 */       this.m_remoteHostsQueue.push(new Integer(i));
/*  86:152 */       this.m_hostPollingTime[i] = 1000;
/*  87:    */     }
/*  88:156 */     this.m_subExpQueue = new Queue();
/*  89:157 */     for (int i = 0; i < this.m_panelHeight; i++) {
/*  90:158 */       this.m_subExpQueue.push(new Integer(i));
/*  91:    */     }
/*  92:    */     try
/*  93:    */     {
/*  94:163 */       this.m_classifier.buildClassifier(this.m_trainingData);
/*  95:    */     }
/*  96:    */     catch (Exception ex)
/*  97:    */     {
/*  98:165 */       ex.printStackTrace();
/*  99:166 */       System.exit(1);
/* 100:    */     }
/* 101:171 */     boolean[] attsToWeightOn = new boolean[this.m_trainingData.numAttributes()];
/* 102:172 */     attsToWeightOn[this.m_xAttribute] = true;
/* 103:173 */     attsToWeightOn[this.m_yAttribute] = true;
/* 104:    */     
/* 105:175 */     this.m_dataGenerator.setWeightingDimensions(attsToWeightOn);
/* 106:    */     try
/* 107:    */     {
/* 108:177 */       this.m_dataGenerator.buildGenerator(this.m_trainingData);
/* 109:    */     }
/* 110:    */     catch (Exception ex)
/* 111:    */     {
/* 112:179 */       ex.printStackTrace();
/* 113:180 */       System.exit(1);
/* 114:    */     }
/* 115:    */   }
/* 116:    */   
/* 117:    */   public void start()
/* 118:    */     throws Exception
/* 119:    */   {
/* 120:197 */     this.m_stopReplotting = true;
/* 121:198 */     if (this.m_trainingData == null) {
/* 122:199 */       throw new Exception("No training data set (BoundaryPanel)");
/* 123:    */     }
/* 124:201 */     if (this.m_classifier == null) {
/* 125:202 */       throw new Exception("No classifier set (BoundaryPanel)");
/* 126:    */     }
/* 127:204 */     if (this.m_dataGenerator == null) {
/* 128:205 */       throw new Exception("No data generator set (BoundaryPanel)");
/* 129:    */     }
/* 130:207 */     if ((this.m_trainingData.attribute(this.m_xAttribute).isNominal()) || (this.m_trainingData.attribute(this.m_yAttribute).isNominal())) {
/* 131:209 */       throw new Exception("Visualization dimensions must be numeric (BoundaryPanel)");
/* 132:    */     }
/* 133:213 */     computeMinMaxAtts();
/* 134:214 */     initialize();
/* 135:    */     
/* 136:    */ 
/* 137:217 */     int totalHosts = this.m_remoteHostsQueue.size();
/* 138:218 */     for (int i = 0; i < totalHosts; i++)
/* 139:    */     {
/* 140:219 */       availableHost(-1);
/* 141:220 */       Thread.sleep(70L);
/* 142:    */     }
/* 143:    */   }
/* 144:    */   
/* 145:    */   protected synchronized void availableHost(int hostNum)
/* 146:    */   {
/* 147:232 */     if (hostNum >= 0) {
/* 148:233 */       if (this.m_remoteHostFailureCounts[hostNum] < 3)
/* 149:    */       {
/* 150:234 */         this.m_remoteHostsQueue.push(new Integer(hostNum));
/* 151:    */       }
/* 152:    */       else
/* 153:    */       {
/* 154:236 */         notifyListeners(false, true, false, "Max failures exceeded for host " + (String)this.m_remoteHosts.elementAt(hostNum) + ". Removed from host list.");
/* 155:    */         
/* 156:238 */         this.m_removedHosts += 1;
/* 157:    */       }
/* 158:    */     }
/* 159:244 */     if (this.m_failedCount == 3 * this.m_remoteHosts.size())
/* 160:    */     {
/* 161:245 */       this.m_plottingAborted = true;
/* 162:246 */       notifyListeners(false, true, true, "Plotting aborted! Max failures exceeded on all remote hosts.");
/* 163:    */       
/* 164:248 */       return;
/* 165:    */     }
/* 166:258 */     if ((this.m_subExpQueue.size() == 0) && (this.m_remoteHosts.size() == this.m_remoteHostsQueue.size() + this.m_removedHosts))
/* 167:    */     {
/* 168:260 */       if (this.m_plotTrainingData) {
/* 169:261 */         plotTrainingData();
/* 170:    */       }
/* 171:263 */       notifyListeners(false, true, true, "Plotting completed successfully.");
/* 172:    */       
/* 173:265 */       return;
/* 174:    */     }
/* 175:268 */     if (checkForAllFailedHosts()) {
/* 176:269 */       return;
/* 177:    */     }
/* 178:272 */     if ((this.m_plottingAborted) && (this.m_remoteHostsQueue.size() + this.m_removedHosts == this.m_remoteHosts.size())) {
/* 179:274 */       notifyListeners(false, true, true, "Plotting aborted. All remote tasks finished.");
/* 180:    */     }
/* 181:278 */     if ((!this.m_subExpQueue.empty()) && (!this.m_plottingAborted) && 
/* 182:279 */       (!this.m_remoteHostsQueue.empty())) {
/* 183:    */       try
/* 184:    */       {
/* 185:282 */         int availHost = ((Integer)this.m_remoteHostsQueue.pop()).intValue();
/* 186:283 */         int waitingTask = ((Integer)this.m_subExpQueue.pop()).intValue();
/* 187:284 */         launchNext(waitingTask, availHost);
/* 188:    */       }
/* 189:    */       catch (Exception ex)
/* 190:    */       {
/* 191:286 */         ex.printStackTrace();
/* 192:    */       }
/* 193:    */     }
/* 194:    */   }
/* 195:    */   
/* 196:    */   private synchronized void notifyListeners(boolean status, boolean log, boolean finished, String message)
/* 197:    */   {
/* 198:302 */     if (this.m_listeners.size() > 0) {
/* 199:303 */       for (int i = 0; i < this.m_listeners.size(); i++)
/* 200:    */       {
/* 201:304 */         RemoteExperimentListener r = (RemoteExperimentListener)this.m_listeners.elementAt(i);
/* 202:305 */         r.remoteExperimentStatus(new RemoteExperimentEvent(status, log, finished, message));
/* 203:    */       }
/* 204:    */     } else {
/* 205:309 */       System.err.println(message);
/* 206:    */     }
/* 207:    */   }
/* 208:    */   
/* 209:    */   private boolean checkForAllFailedHosts()
/* 210:    */   {
/* 211:317 */     boolean allbad = true;
/* 212:318 */     for (int m_remoteHostsStatu : this.m_remoteHostsStatus) {
/* 213:319 */       if (m_remoteHostsStatu != 2)
/* 214:    */       {
/* 215:320 */         allbad = false;
/* 216:321 */         break;
/* 217:    */       }
/* 218:    */     }
/* 219:324 */     if (allbad)
/* 220:    */     {
/* 221:325 */       this.m_plottingAborted = true;
/* 222:326 */       notifyListeners(false, true, true, "Plotting aborted! All connections to remote hosts failed.");
/* 223:    */     }
/* 224:329 */     return allbad;
/* 225:    */   }
/* 226:    */   
/* 227:    */   protected synchronized void incrementFinished() {}
/* 228:    */   
/* 229:    */   protected synchronized void incrementFailed(int hostNum)
/* 230:    */   {
/* 231:345 */     this.m_failedCount += 1;
/* 232:346 */     this.m_remoteHostFailureCounts[hostNum] += 1;
/* 233:    */   }
/* 234:    */   
/* 235:    */   protected synchronized void waitingTask(int expNum)
/* 236:    */   {
/* 237:355 */     this.m_subExpQueue.push(new Integer(expNum));
/* 238:    */   }
/* 239:    */   
/* 240:    */   protected void launchNext(final int wtask, final int ah)
/* 241:    */   {
/* 242:360 */     Thread subTaskThread = new Thread()
/* 243:    */     {
/* 244:    */       public void run()
/* 245:    */       {
/* 246:363 */         BoundaryPanelDistributed.this.m_remoteHostsStatus[ah] = 1;
/* 247:    */         
/* 248:365 */         RemoteBoundaryVisualizerSubTask vSubTask = new RemoteBoundaryVisualizerSubTask();
/* 249:366 */         vSubTask.setXAttribute(BoundaryPanelDistributed.this.m_xAttribute);
/* 250:367 */         vSubTask.setYAttribute(BoundaryPanelDistributed.this.m_yAttribute);
/* 251:368 */         vSubTask.setRowNumber(wtask);
/* 252:369 */         vSubTask.setPanelWidth(BoundaryPanelDistributed.this.m_panelWidth);
/* 253:370 */         vSubTask.setPanelHeight(BoundaryPanelDistributed.this.m_panelHeight);
/* 254:371 */         vSubTask.setPixHeight(BoundaryPanelDistributed.this.m_pixHeight);
/* 255:372 */         vSubTask.setPixWidth(BoundaryPanelDistributed.this.m_pixWidth);
/* 256:373 */         vSubTask.setClassifier(BoundaryPanelDistributed.this.m_classifier);
/* 257:374 */         vSubTask.setDataGenerator(BoundaryPanelDistributed.this.m_dataGenerator);
/* 258:375 */         vSubTask.setInstances(BoundaryPanelDistributed.this.m_trainingData);
/* 259:376 */         vSubTask.setMinMaxX(BoundaryPanelDistributed.this.m_minX, BoundaryPanelDistributed.this.m_maxX);
/* 260:377 */         vSubTask.setMinMaxY(BoundaryPanelDistributed.this.m_minY, BoundaryPanelDistributed.this.m_maxY);
/* 261:378 */         vSubTask.setNumSamplesPerRegion(BoundaryPanelDistributed.this.m_numOfSamplesPerRegion);
/* 262:379 */         vSubTask.setGeneratorSamplesBase(BoundaryPanelDistributed.this.m_samplesBase);
/* 263:    */         try
/* 264:    */         {
/* 265:381 */           String name = "//" + (String)BoundaryPanelDistributed.this.m_remoteHosts.elementAt(ah) + "/RemoteEngine";
/* 266:382 */           Compute comp = (Compute)Naming.lookup(name);
/* 267:    */           
/* 268:384 */           BoundaryPanelDistributed.this.notifyListeners(false, true, false, "Starting row " + wtask + " on host " + (String)BoundaryPanelDistributed.this.m_remoteHosts.elementAt(ah));
/* 269:    */           
/* 270:386 */           Object subTaskId = comp.executeTask(vSubTask);
/* 271:387 */           boolean finished = false;
/* 272:388 */           TaskStatusInfo is = null;
/* 273:389 */           long startTime = System.currentTimeMillis();
/* 274:390 */           while (!finished) {
/* 275:    */             try
/* 276:    */             {
/* 277:392 */               Thread.sleep(Math.max(1000, BoundaryPanelDistributed.this.m_hostPollingTime[ah]));
/* 278:    */               
/* 279:394 */               TaskStatusInfo cs = (TaskStatusInfo)comp.checkStatus(subTaskId);
/* 280:395 */               if (cs.getExecutionStatus() == 3)
/* 281:    */               {
/* 282:398 */                 long runTime = System.currentTimeMillis() - startTime;
/* 283:399 */                 runTime /= 4L;
/* 284:400 */                 if (runTime < 1000L) {
/* 285:401 */                   runTime = 1000L;
/* 286:    */                 }
/* 287:403 */                 BoundaryPanelDistributed.this.m_hostPollingTime[ah] = ((int)runTime);
/* 288:    */                 
/* 289:    */ 
/* 290:406 */                 RemoteResult rr = (RemoteResult)cs.getTaskResult();
/* 291:407 */                 double[][] probs = rr.getProbabilities();
/* 292:409 */                 for (int i = 0; i < BoundaryPanelDistributed.this.m_panelWidth; i++)
/* 293:    */                 {
/* 294:410 */                   BoundaryPanelDistributed.this.m_probabilityCache[wtask][i] = probs[i];
/* 295:411 */                   if (i < BoundaryPanelDistributed.this.m_panelWidth - 1) {
/* 296:412 */                     BoundaryPanelDistributed.this.plotPoint(i, wtask, probs[i], false);
/* 297:    */                   } else {
/* 298:414 */                     BoundaryPanelDistributed.this.plotPoint(i, wtask, probs[i], true);
/* 299:    */                   }
/* 300:    */                 }
/* 301:417 */                 BoundaryPanelDistributed.this.notifyListeners(false, true, false, cs.getStatusMessage());
/* 302:418 */                 BoundaryPanelDistributed.this.m_remoteHostsStatus[ah] = 0;
/* 303:419 */                 BoundaryPanelDistributed.this.incrementFinished();
/* 304:420 */                 BoundaryPanelDistributed.this.availableHost(ah);
/* 305:421 */                 finished = true;
/* 306:    */               }
/* 307:422 */               else if (cs.getExecutionStatus() == 2)
/* 308:    */               {
/* 309:426 */                 BoundaryPanelDistributed.this.notifyListeners(false, true, false, cs.getStatusMessage());
/* 310:427 */                 BoundaryPanelDistributed.this.m_remoteHostsStatus[ah] = 3;
/* 311:    */                 
/* 312:429 */                 BoundaryPanelDistributed.this.notifyListeners(false, true, false, "Row " + wtask + " " + cs.getStatusMessage() + ". Scheduling for execution on another host.");
/* 313:    */                 
/* 314:    */ 
/* 315:432 */                 BoundaryPanelDistributed.this.incrementFailed(ah);
/* 316:    */                 
/* 317:434 */                 BoundaryPanelDistributed.this.waitingTask(wtask);
/* 318:    */                 
/* 319:    */ 
/* 320:    */ 
/* 321:438 */                 BoundaryPanelDistributed.this.availableHost(ah);
/* 322:439 */                 finished = true;
/* 323:    */               }
/* 324:441 */               else if (is == null)
/* 325:    */               {
/* 326:442 */                 is = cs;
/* 327:443 */                 BoundaryPanelDistributed.this.notifyListeners(false, true, false, cs.getStatusMessage());
/* 328:    */               }
/* 329:    */               else
/* 330:    */               {
/* 331:445 */                 RemoteResult rr = (RemoteResult)cs.getTaskResult();
/* 332:446 */                 if (rr != null)
/* 333:    */                 {
/* 334:447 */                   int percentComplete = rr.getPercentCompleted();
/* 335:448 */                   String timeRemaining = "";
/* 336:449 */                   if ((percentComplete > 0) && (percentComplete < 100))
/* 337:    */                   {
/* 338:450 */                     double timeSoFar = System.currentTimeMillis() - startTime;
/* 339:    */                     
/* 340:452 */                     double timeToGo = (100.0D - percentComplete) / percentComplete * timeSoFar;
/* 341:454 */                     if (timeToGo < BoundaryPanelDistributed.this.m_hostPollingTime[ah]) {
/* 342:455 */                       BoundaryPanelDistributed.this.m_hostPollingTime[ah] = ((int)timeToGo);
/* 343:    */                     }
/* 344:457 */                     String units = "seconds";
/* 345:458 */                     timeToGo /= 1000.0D;
/* 346:459 */                     if (timeToGo > 60.0D)
/* 347:    */                     {
/* 348:460 */                       units = "minutes";
/* 349:461 */                       timeToGo /= 60.0D;
/* 350:    */                     }
/* 351:463 */                     if (timeToGo > 60.0D)
/* 352:    */                     {
/* 353:464 */                       units = "hours";
/* 354:465 */                       timeToGo /= 60.0D;
/* 355:    */                     }
/* 356:467 */                     timeRemaining = " (approx. time remaining " + Utils.doubleToString(timeToGo, 1) + " " + units + ")";
/* 357:    */                   }
/* 358:470 */                   if (percentComplete < 25)
/* 359:    */                   {
/* 360:472 */                     if (percentComplete > 0) {
/* 361:473 */                       BoundaryPanelDistributed.this.m_hostPollingTime[ah] = ((int)(25.0D / percentComplete * BoundaryPanelDistributed.this.m_hostPollingTime[ah]));
/* 362:    */                     } else {
/* 363:475 */                       BoundaryPanelDistributed.this.m_hostPollingTime[ah] *= 2;
/* 364:    */                     }
/* 365:477 */                     if (BoundaryPanelDistributed.this.m_hostPollingTime[ah] > 60000) {
/* 366:478 */                       BoundaryPanelDistributed.this.m_hostPollingTime[ah] = 60000;
/* 367:    */                     }
/* 368:    */                   }
/* 369:481 */                   BoundaryPanelDistributed.this.notifyListeners(false, true, false, "Row " + wtask + " " + percentComplete + "% complete" + timeRemaining + ".");
/* 370:    */                 }
/* 371:    */                 else
/* 372:    */                 {
/* 373:484 */                   BoundaryPanelDistributed.this.notifyListeners(false, true, false, "Row " + wtask + " queued on " + (String)BoundaryPanelDistributed.this.m_remoteHosts.elementAt(ah));
/* 374:486 */                   if (BoundaryPanelDistributed.this.m_hostPollingTime[ah] < 60000) {
/* 375:487 */                     BoundaryPanelDistributed.this.m_hostPollingTime[ah] *= 2;
/* 376:    */                   }
/* 377:    */                 }
/* 378:491 */                 is = cs;
/* 379:    */               }
/* 380:    */             }
/* 381:    */             catch (InterruptedException ie)
/* 382:    */             {
/* 383:495 */               ie.printStackTrace();
/* 384:    */             }
/* 385:    */           }
/* 386:    */         }
/* 387:    */         catch (Exception ce)
/* 388:    */         {
/* 389:499 */           BoundaryPanelDistributed.this.m_remoteHostsStatus[ah] = 2;
/* 390:500 */           BoundaryPanelDistributed.access$308(BoundaryPanelDistributed.this);
/* 391:501 */           System.err.println(ce);
/* 392:502 */           ce.printStackTrace();
/* 393:503 */           BoundaryPanelDistributed.this.notifyListeners(false, true, false, "Connection to " + (String)BoundaryPanelDistributed.this.m_remoteHosts.elementAt(ah) + " failed. Scheduling row " + wtask + " for execution on another host.");
/* 394:    */           
/* 395:    */ 
/* 396:    */ 
/* 397:507 */           BoundaryPanelDistributed.this.checkForAllFailedHosts();
/* 398:508 */           BoundaryPanelDistributed.this.waitingTask(wtask);
/* 399:    */         }
/* 400:    */         finally
/* 401:    */         {
/* 402:510 */           if (isInterrupted()) {
/* 403:511 */             System.err.println("Sub exp Interupted!");
/* 404:    */           }
/* 405:    */         }
/* 406:    */       }
/* 407:515 */     };
/* 408:516 */     subTaskThread.setPriority(1);
/* 409:517 */     subTaskThread.start();
/* 410:    */   }
/* 411:    */   
/* 412:    */   public static void main(String[] args)
/* 413:    */   {
/* 414:    */     try
/* 415:    */     {
/* 416:527 */       if (args.length < 8)
/* 417:    */       {
/* 418:528 */         System.err.println("Usage : BoundaryPanelDistributed <dataset> <class col> <xAtt> <yAtt> <base> <# loc/pixel> <kernel bandwidth> <display width> <display height> <classifier [classifier options]>");
/* 419:    */         
/* 420:    */ 
/* 421:    */ 
/* 422:532 */         System.exit(1);
/* 423:    */       }
/* 424:535 */       Vector<String> hostNames = new Vector();
/* 425:    */       try
/* 426:    */       {
/* 427:538 */         BufferedReader br = new BufferedReader(new FileReader("hosts.vis"));
/* 428:539 */         String hostName = br.readLine();
/* 429:540 */         while (hostName != null)
/* 430:    */         {
/* 431:541 */           System.out.println("Adding host " + hostName);
/* 432:542 */           hostNames.add(hostName);
/* 433:543 */           hostName = br.readLine();
/* 434:    */         }
/* 435:545 */         br.close();
/* 436:    */       }
/* 437:    */       catch (Exception ex)
/* 438:    */       {
/* 439:547 */         System.err.println("No hosts.vis file - create this file in the current directory with one host name per line, or use BoundaryPanel instead.");
/* 440:    */         
/* 441:    */ 
/* 442:550 */         System.exit(1);
/* 443:    */       }
/* 444:553 */       JFrame jf = new JFrame("Weka classification boundary visualizer");
/* 445:    */       
/* 446:555 */       jf.getContentPane().setLayout(new BorderLayout());
/* 447:    */       
/* 448:557 */       System.err.println("Loading instances from : " + args[0]);
/* 449:558 */       Reader r = new BufferedReader(new FileReader(args[0]));
/* 450:    */       
/* 451:560 */       final Instances i = new Instances(r);
/* 452:561 */       i.setClassIndex(Integer.parseInt(args[1]));
/* 453:    */       
/* 454:    */ 
/* 455:564 */       final int xatt = Integer.parseInt(args[2]);
/* 456:565 */       final int yatt = Integer.parseInt(args[3]);
/* 457:566 */       int base = Integer.parseInt(args[4]);
/* 458:567 */       int loc = Integer.parseInt(args[5]);
/* 459:    */       
/* 460:569 */       int bandWidth = Integer.parseInt(args[6]);
/* 461:570 */       int panelWidth = Integer.parseInt(args[7]);
/* 462:571 */       int panelHeight = Integer.parseInt(args[8]);
/* 463:    */       
/* 464:573 */       String classifierName = args[9];
/* 465:574 */       final BoundaryPanelDistributed bv = new BoundaryPanelDistributed(panelWidth, panelHeight);
/* 466:    */       
/* 467:576 */       bv.addRemoteExperimentListener(new RemoteExperimentListener()
/* 468:    */       {
/* 469:    */         public void remoteExperimentStatus(RemoteExperimentEvent e)
/* 470:    */         {
/* 471:579 */           if (e.m_experimentFinished)
/* 472:    */           {
/* 473:580 */             String classifierNameNew = this.val$classifierName.substring(this.val$classifierName.lastIndexOf('.') + 1, this.val$classifierName.length());
/* 474:    */             
/* 475:582 */             bv.saveImage(classifierNameNew + "_" + i.relationName() + "_X" + xatt + "_Y" + yatt + ".jpg");
/* 476:    */           }
/* 477:    */           else
/* 478:    */           {
/* 479:585 */             System.err.println(e.m_messageString);
/* 480:    */           }
/* 481:    */         }
/* 482:588 */       });
/* 483:589 */       bv.setRemoteHosts(hostNames);
/* 484:    */       
/* 485:591 */       jf.getContentPane().add(bv, "Center");
/* 486:592 */       jf.setSize(bv.getMinimumSize());
/* 487:    */       
/* 488:594 */       jf.addWindowListener(new WindowAdapter()
/* 489:    */       {
/* 490:    */         public void windowClosing(WindowEvent e)
/* 491:    */         {
/* 492:597 */           this.val$jf.dispose();
/* 493:598 */           System.exit(0);
/* 494:    */         }
/* 495:601 */       });
/* 496:602 */       jf.pack();
/* 497:603 */       jf.setVisible(true);
/* 498:    */       
/* 499:605 */       bv.repaint();
/* 500:    */       
/* 501:607 */       String[] argsR = null;
/* 502:608 */       if (args.length > 10)
/* 503:    */       {
/* 504:609 */         argsR = new String[args.length - 10];
/* 505:610 */         for (int j = 10; j < args.length; j++) {
/* 506:611 */           argsR[(j - 10)] = args[j];
/* 507:    */         }
/* 508:    */       }
/* 509:614 */       Classifier c = AbstractClassifier.forName(args[9], argsR);
/* 510:615 */       KDDataGenerator dataGen = new KDDataGenerator();
/* 511:616 */       dataGen.setKernelBandwidth(bandWidth);
/* 512:617 */       bv.setDataGenerator(dataGen);
/* 513:618 */       bv.setNumSamplesPerRegion(loc);
/* 514:619 */       bv.setGeneratorSamplesBase(base);
/* 515:620 */       bv.setClassifier(c);
/* 516:621 */       bv.setTrainingData(i);
/* 517:622 */       bv.setXAttribute(xatt);
/* 518:623 */       bv.setYAttribute(yatt);
/* 519:    */       try
/* 520:    */       {
/* 521:627 */         FileInputStream fis = new FileInputStream("colors.ser");
/* 522:628 */         ObjectInputStream ois = new ObjectInputStream(fis);
/* 523:    */         
/* 524:630 */         ArrayList<Color> colors = (ArrayList)ois.readObject();
/* 525:631 */         bv.setColors(colors);
/* 526:632 */         ois.close();
/* 527:    */       }
/* 528:    */       catch (Exception ex)
/* 529:    */       {
/* 530:634 */         System.err.println("No color map file");
/* 531:    */       }
/* 532:636 */       bv.start();
/* 533:    */     }
/* 534:    */     catch (Exception ex)
/* 535:    */     {
/* 536:638 */       ex.printStackTrace();
/* 537:    */     }
/* 538:    */   }
/* 539:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.boundaryvisualizer.BoundaryPanelDistributed
 * JD-Core Version:    0.7.0.1
 */