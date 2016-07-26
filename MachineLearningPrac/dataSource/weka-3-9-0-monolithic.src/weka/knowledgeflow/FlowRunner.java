/*   1:    */ package weka.knowledgeflow;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.text.SimpleDateFormat;
/*   6:    */ import java.util.ArrayList;
/*   7:    */ import java.util.Date;
/*   8:    */ import java.util.Iterator;
/*   9:    */ import java.util.List;
/*  10:    */ import java.util.TreeMap;
/*  11:    */ import weka.core.Environment;
/*  12:    */ import weka.core.PluginManager;
/*  13:    */ import weka.core.Settings;
/*  14:    */ import weka.core.Utils;
/*  15:    */ import weka.core.WekaException;
/*  16:    */ import weka.core.WekaPackageManager;
/*  17:    */ import weka.core.logging.Logger.Level;
/*  18:    */ import weka.gui.knowledgeflow.KnowledgeFlowApp.KnowledgeFlowGeneralDefaults;
/*  19:    */ import weka.knowledgeflow.steps.Note;
/*  20:    */ import weka.knowledgeflow.steps.Step;
/*  21:    */ 
/*  22:    */ public class FlowRunner
/*  23:    */   implements FlowExecutor
/*  24:    */ {
/*  25:    */   protected Flow m_flow;
/*  26: 55 */   protected transient BaseExecutionEnvironment m_execEnv = new BaseExecutionEnvironment();
/*  27: 59 */   protected transient weka.gui.Logger m_log = new SimpleLogger();
/*  28:    */   protected transient LogManager m_logHandler;
/*  29: 65 */   protected LoggingLevel m_loggingLevel = LoggingLevel.BASIC;
/*  30:    */   protected boolean m_startSequentially;
/*  31: 71 */   protected int m_numThreads = 50;
/*  32: 77 */   protected int m_resourceIntensiveNumThreads = 0;
/*  33: 81 */   protected List<ExecutionFinishedCallback> m_callbacks = new ArrayList();
/*  34:    */   protected boolean m_wasStopped;
/*  35:    */   
/*  36:    */   public FlowRunner()
/*  37:    */   {
/*  38: 91 */     Settings settings = new Settings("weka", "knowledgeflow");
/*  39: 92 */     settings.applyDefaults(new KFDefaults());
/*  40: 93 */     init(settings);
/*  41:    */   }
/*  42:    */   
/*  43:    */   public FlowRunner(Settings settings)
/*  44:    */   {
/*  45:100 */     init(settings);
/*  46:    */   }
/*  47:    */   
/*  48:    */   protected void init(Settings settings)
/*  49:    */   {
/*  50:107 */     String execName = (String)settings.getSetting("knowledgeflow", KnowledgeFlowApp.KnowledgeFlowGeneralDefaults.EXECUTION_ENV_KEY, "Default execution environment");
/*  51:    */     
/*  52:    */ 
/*  53:    */ 
/*  54:111 */     BaseExecutionEnvironment execE = null;
/*  55:    */     try
/*  56:    */     {
/*  57:113 */       execE = (BaseExecutionEnvironment)PluginManager.getPluginInstance(BaseExecutionEnvironment.class.getCanonicalName(), execName);
/*  58:    */     }
/*  59:    */     catch (Exception ex)
/*  60:    */     {
/*  61:117 */       ex.printStackTrace();
/*  62:    */     }
/*  63:120 */     if (execE != null) {
/*  64:121 */       this.m_execEnv = execE;
/*  65:    */     }
/*  66:124 */     this.m_execEnv = new BaseExecutionEnvironment();
/*  67:125 */     this.m_execEnv.setHeadless(true);
/*  68:126 */     this.m_execEnv.setFlowExecutor(this);
/*  69:127 */     this.m_execEnv.setLog(this.m_log);
/*  70:128 */     this.m_execEnv.setSettings(settings);
/*  71:    */     
/*  72:130 */     this.m_numThreads = ((Integer)settings.getSetting("knowledgeflow", BaseExecutionEnvironment.BaseExecutionEnvironmentDefaults.STEP_EXECUTOR_SERVICE_NUM_THREADS_KEY, Integer.valueOf(50))).intValue();
/*  73:    */     
/*  74:    */ 
/*  75:    */ 
/*  76:    */ 
/*  77:    */ 
/*  78:136 */     this.m_resourceIntensiveNumThreads = ((Integer)settings.getSetting("knowledgeflow", BaseExecutionEnvironment.BaseExecutionEnvironmentDefaults.RESOURCE_INTENSIVE_EXECUTOR_SERVICE_NUM_THREADS_KEY, Integer.valueOf(0))).intValue();
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void setSettings(Settings settings)
/*  82:    */   {
/*  83:151 */     init(settings);
/*  84:    */   }
/*  85:    */   
/*  86:    */   public Settings getSettings()
/*  87:    */   {
/*  88:161 */     return this.m_execEnv.getSettings();
/*  89:    */   }
/*  90:    */   
/*  91:    */   public static void main(String[] args)
/*  92:    */   {
/*  93:170 */     weka.core.logging.Logger.log(Logger.Level.INFO, "Logging started");
/*  94:172 */     if (args.length < 1) {
/*  95:173 */       System.err.println("Usage:\n\nFlowRunner <json flow file> [-s]\n\n\tUse -s to launch start points sequentially (default launches in parallel).");
/*  96:    */     } else {
/*  97:    */       try
/*  98:    */       {
/*  99:178 */         WekaPackageManager.loadPackages(false, true, false);
/* 100:179 */         Settings settings = new Settings("weka", "knowledgeflow");
/* 101:180 */         settings.loadSettings();
/* 102:181 */         settings.applyDefaults(new KFDefaults());
/* 103:    */         
/* 104:183 */         FlowRunner fr = new FlowRunner(settings);
/* 105:184 */         String fileName = args[0];
/* 106:185 */         args[0] = "";
/* 107:186 */         fr.setLaunchStartPointsSequentially(Utils.getFlag("s", args));
/* 108:    */         
/* 109:188 */         Flow toRun = Flow.loadFlow(new File(fileName), new SimpleLogger());
/* 110:    */         
/* 111:190 */         fr.setFlow(toRun);
/* 112:191 */         fr.run();
/* 113:192 */         fr.waitUntilFinished();
/* 114:193 */         fr.m_logHandler.logLow("FlowRunner: Finished all flows.");
/* 115:194 */         System.exit(0);
/* 116:    */       }
/* 117:    */       catch (Exception ex)
/* 118:    */       {
/* 119:196 */         ex.printStackTrace();
/* 120:    */       }
/* 121:    */     }
/* 122:    */   }
/* 123:    */   
/* 124:    */   public void addExecutionFinishedCallback(ExecutionFinishedCallback callback)
/* 125:    */   {
/* 126:208 */     if (!this.m_callbacks.contains(callback)) {
/* 127:209 */       this.m_callbacks.add(callback);
/* 128:    */     }
/* 129:    */   }
/* 130:    */   
/* 131:    */   public void removeExecutionFinishedCallback(ExecutionFinishedCallback callback)
/* 132:    */   {
/* 133:221 */     this.m_callbacks.remove(callback);
/* 134:    */   }
/* 135:    */   
/* 136:    */   public Flow getFlow()
/* 137:    */   {
/* 138:231 */     return this.m_flow;
/* 139:    */   }
/* 140:    */   
/* 141:    */   public void setFlow(Flow flow)
/* 142:    */   {
/* 143:241 */     this.m_flow = flow;
/* 144:    */   }
/* 145:    */   
/* 146:    */   public weka.gui.Logger getLogger()
/* 147:    */   {
/* 148:251 */     return this.m_log;
/* 149:    */   }
/* 150:    */   
/* 151:    */   public void setLogger(weka.gui.Logger logger)
/* 152:    */   {
/* 153:261 */     this.m_log = logger;
/* 154:262 */     if (this.m_execEnv != null) {
/* 155:263 */       this.m_execEnv.setLog(logger);
/* 156:    */     }
/* 157:    */   }
/* 158:    */   
/* 159:    */   public LoggingLevel getLoggingLevel()
/* 160:    */   {
/* 161:274 */     return this.m_loggingLevel;
/* 162:    */   }
/* 163:    */   
/* 164:    */   public void setLoggingLevel(LoggingLevel level)
/* 165:    */   {
/* 166:284 */     this.m_loggingLevel = level;
/* 167:285 */     if (this.m_execEnv != null) {
/* 168:286 */       this.m_execEnv.setLoggingLevel(level);
/* 169:    */     }
/* 170:    */   }
/* 171:    */   
/* 172:    */   public boolean getLaunchStartPointsSequentially()
/* 173:    */   {
/* 174:296 */     return this.m_startSequentially;
/* 175:    */   }
/* 176:    */   
/* 177:    */   public void setLaunchStartPointsSequentially(boolean s)
/* 178:    */   {
/* 179:305 */     this.m_startSequentially = s;
/* 180:    */   }
/* 181:    */   
/* 182:    */   public BaseExecutionEnvironment getExecutionEnvironment()
/* 183:    */   {
/* 184:310 */     return this.m_execEnv;
/* 185:    */   }
/* 186:    */   
/* 187:    */   public void setExecutionEnvironment(BaseExecutionEnvironment env)
/* 188:    */   {
/* 189:320 */     this.m_execEnv = env;
/* 190:    */   }
/* 191:    */   
/* 192:    */   public void run()
/* 193:    */     throws WekaException
/* 194:    */   {
/* 195:329 */     if (this.m_flow == null) {
/* 196:330 */       throw new WekaException("No flow to execute!");
/* 197:    */     }
/* 198:333 */     if (this.m_startSequentially) {
/* 199:334 */       runSequentially();
/* 200:    */     } else {
/* 201:336 */       runParallel();
/* 202:    */     }
/* 203:    */   }
/* 204:    */   
/* 205:    */   protected List<StepManagerImpl> initializeFlow()
/* 206:    */     throws WekaException
/* 207:    */   {
/* 208:347 */     this.m_wasStopped = false;
/* 209:348 */     if (this.m_flow == null)
/* 210:    */     {
/* 211:349 */       this.m_wasStopped = true;
/* 212:350 */       for (ExecutionFinishedCallback c : this.m_callbacks) {
/* 213:351 */         c.executionFinished();
/* 214:    */       }
/* 215:354 */       throw new WekaException("No flow to execute!");
/* 216:    */     }
/* 217:357 */     this.m_logHandler = new LogManager(this.m_log);
/* 218:358 */     this.m_logHandler.m_statusMessagePrefix = ("FlowRunner$" + hashCode() + "|");
/* 219:359 */     setLoggingLevel((LoggingLevel)this.m_execEnv.getSettings().getSetting("knowledgeflow.main", KFDefaults.LOGGING_LEVEL_KEY, LoggingLevel.BASIC, Environment.getSystemWide()));
/* 220:    */     
/* 221:    */ 
/* 222:362 */     this.m_logHandler.setLoggingLevel(this.m_loggingLevel);
/* 223:    */     
/* 224:364 */     List<StepManagerImpl> startPoints = this.m_flow.findPotentialStartPoints();
/* 225:365 */     if (startPoints.size() == 0)
/* 226:    */     {
/* 227:366 */       this.m_wasStopped = true;
/* 228:367 */       this.m_logHandler.logError("FlowRunner: there don't appear to be any start points to launch!", null);
/* 229:369 */       for (ExecutionFinishedCallback c : this.m_callbacks) {
/* 230:370 */         c.executionFinished();
/* 231:    */       }
/* 232:373 */       return null;
/* 233:    */     }
/* 234:376 */     this.m_wasStopped = false;
/* 235:377 */     this.m_execEnv.startClientExecutionService(this.m_numThreads, this.m_resourceIntensiveNumThreads);
/* 236:380 */     if (!this.m_flow.initFlow(this))
/* 237:    */     {
/* 238:381 */       this.m_wasStopped = true;
/* 239:382 */       for (ExecutionFinishedCallback c : this.m_callbacks) {
/* 240:383 */         c.executionFinished();
/* 241:    */       }
/* 242:385 */       throw new WekaException("Flow did not initializeFlow properly - check log.");
/* 243:    */     }
/* 244:389 */     return startPoints;
/* 245:    */   }
/* 246:    */   
/* 247:    */   public void runSequentially()
/* 248:    */     throws WekaException
/* 249:    */   {
/* 250:399 */     List<StepManagerImpl> startPoints = initializeFlow();
/* 251:400 */     if (startPoints == null) {
/* 252:401 */       return;
/* 253:    */     }
/* 254:403 */     runSequentially(startPoints);
/* 255:    */   }
/* 256:    */   
/* 257:    */   public void runParallel()
/* 258:    */     throws WekaException
/* 259:    */   {
/* 260:413 */     List<StepManagerImpl> startPoints = initializeFlow();
/* 261:414 */     if (startPoints == null) {
/* 262:415 */       return;
/* 263:    */     }
/* 264:417 */     runParallel(startPoints);
/* 265:    */   }
/* 266:    */   
/* 267:    */   protected void runSequentially(List<StepManagerImpl> startPoints)
/* 268:    */     throws WekaException
/* 269:    */   {
/* 270:428 */     this.m_logHandler.logDetailed("Flow runner: using execution environment - " + this.m_execEnv.getDescription());
/* 271:    */     
/* 272:    */ 
/* 273:431 */     TreeMap<Integer, StepManagerImpl> sortedStartPoints = new TreeMap();
/* 274:    */     
/* 275:433 */     List<StepManagerImpl> unNumbered = new ArrayList();
/* 276:435 */     for (StepManagerImpl s : startPoints)
/* 277:    */     {
/* 278:436 */       String stepName = s.getManagedStep().getName();
/* 279:437 */       if (!stepName.startsWith("!")) {
/* 280:440 */         if (stepName.indexOf(":") > 0) {
/* 281:    */           try
/* 282:    */           {
/* 283:442 */             Integer num = Integer.valueOf(Integer.parseInt(stepName.split(":")[0]));
/* 284:443 */             sortedStartPoints.put(num, s);
/* 285:    */           }
/* 286:    */           catch (NumberFormatException ex)
/* 287:    */           {
/* 288:445 */             unNumbered.add(s);
/* 289:    */           }
/* 290:    */         } else {
/* 291:448 */           unNumbered.add(s);
/* 292:    */         }
/* 293:    */       }
/* 294:    */     }
/* 295:452 */     int biggest = 0;
/* 296:453 */     if (sortedStartPoints.size() > 0) {
/* 297:454 */       biggest = ((Integer)sortedStartPoints.lastKey()).intValue();
/* 298:    */     }
/* 299:456 */     for (StepManagerImpl s : unNumbered)
/* 300:    */     {
/* 301:457 */       biggest++;
/* 302:458 */       sortedStartPoints.put(Integer.valueOf(biggest), s);
/* 303:    */     }
/* 304:461 */     for (StepManagerImpl stepToStart : sortedStartPoints.values()) {
/* 305:462 */       if (!(stepToStart.getManagedStep() instanceof Note))
/* 306:    */       {
/* 307:466 */         this.m_logHandler.logLow("FlowRunner: Launching start point: " + stepToStart.getManagedStep().getName());
/* 308:    */         
/* 309:    */ 
/* 310:469 */         this.m_execEnv.launchStartPoint(stepToStart);
/* 311:    */       }
/* 312:    */     }
/* 313:481 */     this.m_logHandler.logDebug("FlowRunner: Launching shutdown monitor");
/* 314:482 */     launchExecutorShutdownThread();
/* 315:    */   }
/* 316:    */   
/* 317:    */   protected void runParallel(List<StepManagerImpl> startPoints)
/* 318:    */     throws WekaException
/* 319:    */   {
/* 320:494 */     this.m_logHandler.logDetailed("Flow runner: using execution environment - " + this.m_execEnv.getDescription());
/* 321:497 */     for (StepManagerImpl startP : startPoints) {
/* 322:498 */       if ((!startP.getManagedStep().getName().startsWith("!")) && (!(startP.getManagedStep() instanceof Note)))
/* 323:    */       {
/* 324:503 */         this.m_logHandler.logLow("FlowRunner: Launching start point: " + startP.getManagedStep().getName());
/* 325:    */         
/* 326:    */ 
/* 327:506 */         this.m_execEnv.launchStartPoint(startP);
/* 328:    */       }
/* 329:    */     }
/* 330:516 */     this.m_logHandler.logDebug("FlowRunner: Launching shutdown monitor");
/* 331:517 */     launchExecutorShutdownThread();
/* 332:    */   }
/* 333:    */   
/* 334:    */   protected void launchExecutorShutdownThread()
/* 335:    */   {
/* 336:525 */     if (this.m_execEnv != null)
/* 337:    */     {
/* 338:526 */       Thread shutdownThread = new Thread()
/* 339:    */       {
/* 340:    */         public void run()
/* 341:    */         {
/* 342:529 */           FlowRunner.this.waitUntilFinished();
/* 343:530 */           FlowRunner.this.m_logHandler.logDebug("FlowRunner: Shutting down executor service");
/* 344:531 */           FlowRunner.this.m_execEnv.stopClientExecutionService();
/* 345:532 */           for (ExecutionFinishedCallback c : FlowRunner.this.m_callbacks) {
/* 346:533 */             c.executionFinished();
/* 347:    */           }
/* 348:    */         }
/* 349:536 */       };
/* 350:537 */       shutdownThread.start();
/* 351:    */     }
/* 352:    */   }
/* 353:    */   
/* 354:    */   public void waitUntilFinished()
/* 355:    */   {
/* 356:    */     try
/* 357:    */     {
/* 358:546 */       Thread.sleep(500L);
/* 359:    */       for (;;)
/* 360:    */       {
/* 361:548 */         boolean busy = flowBusy();
/* 362:549 */         if (!busy) {
/* 363:    */           break;
/* 364:    */         }
/* 365:550 */         Thread.sleep(3000L);
/* 366:    */       }
/* 367:    */     }
/* 368:    */     catch (Exception ex)
/* 369:    */     {
/* 370:556 */       this.m_logHandler.logDetailed("FlowRunner: Attempting to stop all steps...");
/* 371:    */     }
/* 372:    */   }
/* 373:    */   
/* 374:    */   public boolean flowBusy()
/* 375:    */   {
/* 376:566 */     boolean busy = false;
/* 377:567 */     Iterator<StepManagerImpl> iter = this.m_flow.iterator();
/* 378:568 */     while (iter.hasNext())
/* 379:    */     {
/* 380:569 */       StepManagerImpl s = (StepManagerImpl)iter.next();
/* 381:570 */       if (s.isStepBusy())
/* 382:    */       {
/* 383:571 */         this.m_logHandler.logDebug(s.getName() + " is still busy.");
/* 384:572 */         busy = true;
/* 385:    */       }
/* 386:    */     }
/* 387:576 */     return busy;
/* 388:    */   }
/* 389:    */   
/* 390:    */   public synchronized void stopProcessing()
/* 391:    */   {
/* 392:584 */     Iterator<StepManagerImpl> iter = this.m_flow.iterator();
/* 393:585 */     while (iter.hasNext()) {
/* 394:586 */       ((StepManagerImpl)iter.next()).stopStep();
/* 395:    */     }
/* 396:588 */     System.err.println("Asked all steps to stop...");
/* 397:589 */     this.m_wasStopped = true;
/* 398:    */   }
/* 399:    */   
/* 400:    */   public boolean wasStopped()
/* 401:    */   {
/* 402:599 */     return this.m_wasStopped;
/* 403:    */   }
/* 404:    */   
/* 405:    */   public static class SimpleLogger
/* 406:    */     implements weka.gui.Logger
/* 407:    */   {
/* 408:610 */     SimpleDateFormat m_DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
/* 409:    */     
/* 410:    */     public void logMessage(String lm)
/* 411:    */     {
/* 412:614 */       System.out.println(this.m_DateFormat.format(new Date()) + ": " + lm);
/* 413:    */     }
/* 414:    */     
/* 415:    */     public void statusMessage(String lm) {}
/* 416:    */   }
/* 417:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.FlowRunner
 * JD-Core Version:    0.7.0.1
 */