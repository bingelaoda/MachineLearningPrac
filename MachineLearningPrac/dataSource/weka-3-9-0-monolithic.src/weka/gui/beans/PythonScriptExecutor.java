/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.image.BufferedImage;
/*   5:    */ import java.beans.EventSetDescriptor;
/*   6:    */ import java.io.BufferedReader;
/*   7:    */ import java.io.FileReader;
/*   8:    */ import java.io.Serializable;
/*   9:    */ import java.util.ArrayList;
/*  10:    */ import java.util.List;
/*  11:    */ import javax.swing.JPanel;
/*  12:    */ import weka.core.Environment;
/*  13:    */ import weka.core.EnvironmentHandler;
/*  14:    */ import weka.core.Instances;
/*  15:    */ import weka.core.WekaException;
/*  16:    */ import weka.gui.Logger;
/*  17:    */ import weka.python.PythonSession;
/*  18:    */ import weka.python.PythonSession.PythonVariableType;
/*  19:    */ 
/*  20:    */ @KFStep(category="Scripting", toolTipText="CPython scripting step")
/*  21:    */ public class PythonScriptExecutor
/*  22:    */   extends JPanel
/*  23:    */   implements BeanCommon, Visible, EventConstraints, Serializable, TrainingSetListener, TestSetListener, DataSourceListener, EnvironmentHandler, Startable
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = 8220123645676178107L;
/*  26: 57 */   protected BeanVisual m_visual = new BeanVisual("PythonScriptExecutor", "weka/gui/beans/icons/PythonScriptExecutor.gif", "weka/gui/beans/icons/PythonScriptExecutor.gif");
/*  27: 62 */   protected ArrayList<TextListener> m_textListeners = new ArrayList();
/*  28: 66 */   protected ArrayList<DataSourceListener> m_dataListeners = new ArrayList();
/*  29: 70 */   protected ArrayList<ImageListener> m_imageListeners = new ArrayList();
/*  30:    */   protected Object m_listenee;
/*  31:    */   protected transient Environment m_env;
/*  32:    */   protected transient Logger m_logger;
/*  33: 80 */   protected String m_pyScript = "";
/*  34: 83 */   protected String m_scriptFile = "";
/*  35: 86 */   protected String m_varsToGet = "";
/*  36:    */   protected transient boolean m_busy;
/*  37:    */   protected boolean m_debug;
/*  38:    */   protected boolean m_continueOnSysErr;
/*  39:    */   
/*  40:    */   public String globalInfo()
/*  41:    */   {
/*  42:103 */     return "A Knowledge Flow component that executes a user-supplied CPython script. The script may be supplied via the GUI editor for the step or from a file at run time. Incoming instances will be transferred to pandas data frame (called py_data) in the Python environment.";
/*  43:    */   }
/*  44:    */   
/*  45:    */   public PythonScriptExecutor()
/*  46:    */   {
/*  47:113 */     setLayout(new BorderLayout());
/*  48:114 */     add(this.m_visual, "Center");
/*  49:    */     
/*  50:116 */     this.m_env = Environment.getSystemWide();
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void setPythonScript(String script)
/*  54:    */   {
/*  55:126 */     this.m_pyScript = script;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public String getPythonScript()
/*  59:    */   {
/*  60:136 */     return this.m_pyScript;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void setScriptFile(String scriptFile)
/*  64:    */   {
/*  65:145 */     this.m_scriptFile = scriptFile;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public String getScriptFile()
/*  69:    */   {
/*  70:154 */     return this.m_scriptFile;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void setVariablesToGetFromPython(String varsToGet)
/*  74:    */   {
/*  75:163 */     this.m_varsToGet = varsToGet;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public String getVariablesToGetFromPython()
/*  79:    */   {
/*  80:172 */     return this.m_varsToGet;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public void setDebug(boolean debug)
/*  84:    */   {
/*  85:181 */     this.m_debug = debug;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public boolean getDebug()
/*  89:    */   {
/*  90:190 */     return this.m_debug;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public void setCustomName(String name)
/*  94:    */   {
/*  95:195 */     this.m_visual.setText(name);
/*  96:    */   }
/*  97:    */   
/*  98:    */   public String getCustomName()
/*  99:    */   {
/* 100:200 */     return this.m_visual.getText();
/* 101:    */   }
/* 102:    */   
/* 103:    */   public void stop()
/* 104:    */   {
/* 105:205 */     if ((this.m_listenee != null) && 
/* 106:206 */       ((this.m_listenee instanceof BeanCommon))) {
/* 107:207 */       ((BeanCommon)this.m_listenee).stop();
/* 108:    */     }
/* 109:211 */     if (this.m_logger != null) {
/* 110:212 */       this.m_logger.statusMessage(statusMessagePrefix() + "Stopped");
/* 111:    */     }
/* 112:215 */     this.m_busy = false;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public boolean isBusy()
/* 116:    */   {
/* 117:220 */     return this.m_busy;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public void setLog(Logger logger)
/* 121:    */   {
/* 122:225 */     this.m_logger = logger;
/* 123:    */   }
/* 124:    */   
/* 125:    */   public boolean connectionAllowed(EventSetDescriptor esd)
/* 126:    */   {
/* 127:230 */     return connectionAllowed(esd.getName());
/* 128:    */   }
/* 129:    */   
/* 130:    */   public boolean connectionAllowed(String eventName)
/* 131:    */   {
/* 132:235 */     if ((!eventName.equals("instance")) && (!eventName.equals("dataSet")) && (!eventName.equals("trainingSet")) && (!eventName.equals("testSet"))) {
/* 133:237 */       return false;
/* 134:    */     }
/* 135:240 */     if (this.m_listenee != null) {
/* 136:241 */       return false;
/* 137:    */     }
/* 138:244 */     return true;
/* 139:    */   }
/* 140:    */   
/* 141:    */   public void connectionNotification(String eventName, Object source)
/* 142:    */   {
/* 143:249 */     if (connectionAllowed(eventName)) {
/* 144:250 */       this.m_listenee = source;
/* 145:    */     }
/* 146:    */   }
/* 147:    */   
/* 148:    */   public void disconnectionNotification(String eventName, Object source)
/* 149:    */   {
/* 150:256 */     if (source == this.m_listenee) {
/* 151:257 */       this.m_listenee = null;
/* 152:    */     }
/* 153:    */   }
/* 154:    */   
/* 155:    */   public void acceptDataSet(DataSetEvent e)
/* 156:    */   {
/* 157:263 */     if (e.isStructureOnly()) {
/* 158:264 */       return;
/* 159:    */     }
/* 160:267 */     acceptInstances(e.getDataSet());
/* 161:    */   }
/* 162:    */   
/* 163:    */   public void setEnvironment(Environment env)
/* 164:    */   {
/* 165:272 */     this.m_env = env;
/* 166:    */   }
/* 167:    */   
/* 168:    */   public boolean eventGeneratable(String eventName)
/* 169:    */   {
/* 170:277 */     if ((!eventName.equals("text")) && (!eventName.equals("dataSet")) && (!eventName.equals("image"))) {
/* 171:279 */       return false;
/* 172:    */     }
/* 173:282 */     if (((this.m_pyScript == null) || (this.m_pyScript.length() == 0)) && ((this.m_scriptFile == null) || (this.m_scriptFile.length() == 0))) {
/* 174:284 */       return false;
/* 175:    */     }
/* 176:287 */     return true;
/* 177:    */   }
/* 178:    */   
/* 179:    */   public void start()
/* 180:    */     throws Exception
/* 181:    */   {
/* 182:292 */     if ((this.m_listenee == null) && (
/* 183:293 */       ((this.m_pyScript != null) && (this.m_pyScript.length() > 0)) || ((this.m_scriptFile != null) && (this.m_scriptFile.length() > 0))))
/* 184:    */     {
/* 185:295 */       String script = this.m_pyScript;
/* 186:296 */       if ((this.m_scriptFile != null) && (this.m_scriptFile.length() > 0)) {
/* 187:297 */         script = loadScript();
/* 188:    */       }
/* 189:299 */       executeScript(getSession(), script);
/* 190:    */     }
/* 191:    */   }
/* 192:    */   
/* 193:    */   public String getStartMessage()
/* 194:    */   {
/* 195:306 */     String message = "Execute script";
/* 196:308 */     if (this.m_listenee != null) {
/* 197:309 */       message = "$" + message;
/* 198:    */     }
/* 199:312 */     return message;
/* 200:    */   }
/* 201:    */   
/* 202:    */   public void acceptTestSet(TestSetEvent e)
/* 203:    */   {
/* 204:317 */     if (e.isStructureOnly()) {
/* 205:318 */       return;
/* 206:    */     }
/* 207:321 */     acceptInstances(e.getTestSet());
/* 208:    */   }
/* 209:    */   
/* 210:    */   public void acceptTrainingSet(TrainingSetEvent e)
/* 211:    */   {
/* 212:326 */     if (e.isStructureOnly()) {
/* 213:327 */       return;
/* 214:    */     }
/* 215:330 */     acceptInstances(e.getTrainingSet());
/* 216:    */   }
/* 217:    */   
/* 218:    */   public void useDefaultVisual()
/* 219:    */   {
/* 220:335 */     this.m_visual.loadIcons("weka/gui/beans/icons/PythonScriptExecutor.gif", "weka/gui/beans/icons/PythonScriptExecutor.gif");
/* 221:    */     
/* 222:337 */     this.m_visual.setText("PythonScriptExcecutor");
/* 223:    */   }
/* 224:    */   
/* 225:    */   public void setVisual(BeanVisual newVisual)
/* 226:    */   {
/* 227:342 */     this.m_visual = newVisual;
/* 228:    */   }
/* 229:    */   
/* 230:    */   public BeanVisual getVisual()
/* 231:    */   {
/* 232:347 */     return this.m_visual;
/* 233:    */   }
/* 234:    */   
/* 235:    */   protected String statusMessagePrefix()
/* 236:    */   {
/* 237:351 */     return getCustomName() + "$" + hashCode() + "|";
/* 238:    */   }
/* 239:    */   
/* 240:    */   protected void log(String logMessage, String statusMessage, Exception ex)
/* 241:    */   {
/* 242:362 */     if (this.m_logger != null) {
/* 243:363 */       if (ex != null)
/* 244:    */       {
/* 245:364 */         this.m_logger.statusMessage(statusMessagePrefix() + "Error. See log for details.");
/* 246:    */         
/* 247:366 */         this.m_logger.logMessage(statusMessagePrefix() + ex.getMessage());
/* 248:367 */         ex.printStackTrace();
/* 249:    */       }
/* 250:    */       else
/* 251:    */       {
/* 252:369 */         if (logMessage != null) {
/* 253:370 */           this.m_logger.logMessage(statusMessagePrefix() + logMessage);
/* 254:    */         }
/* 255:372 */         if (statusMessage != null) {
/* 256:373 */           this.m_logger.statusMessage(statusMessagePrefix() + statusMessage);
/* 257:    */         }
/* 258:    */       }
/* 259:    */     }
/* 260:    */   }
/* 261:    */   
/* 262:    */   protected PythonSession getSession()
/* 263:    */     throws Exception
/* 264:    */   {
/* 265:381 */     if (!PythonSession.pythonAvailable()) {
/* 266:383 */       if (!PythonSession.initSession("python", getDebug()))
/* 267:    */       {
/* 268:384 */         String envEvalResults = PythonSession.getPythonEnvCheckResults();
/* 269:385 */         throw new Exception("Was unable to start python environment: " + envEvalResults);
/* 270:    */       }
/* 271:    */     }
/* 272:390 */     PythonSession session = PythonSession.acquireSession(this);
/* 273:391 */     session.setLog(this.m_logger);
/* 274:    */     
/* 275:393 */     return session;
/* 276:    */   }
/* 277:    */   
/* 278:    */   protected void acceptInstances(Instances insts)
/* 279:    */   {
/* 280:404 */     this.m_busy = true;
/* 281:405 */     PythonSession session = null;
/* 282:    */     try
/* 283:    */     {
/* 284:407 */       session = getSession();
/* 285:409 */       if (((this.m_pyScript != null) && (this.m_pyScript.length() > 0)) || ((this.m_scriptFile != null) && (this.m_scriptFile.length() > 0)))
/* 286:    */       {
/* 287:412 */         log("Converting incoming instances to pandas data frame", "Converting incoming instances to pandas data frame...", null);
/* 288:    */         
/* 289:414 */         session.instancesToPython(insts, "py_data", getDebug());
/* 290:    */         
/* 291:    */ 
/* 292:417 */         String script = this.m_pyScript;
/* 293:418 */         if ((this.m_scriptFile != null) && (this.m_scriptFile.length() > 0)) {
/* 294:419 */           script = loadScript();
/* 295:    */         }
/* 296:422 */         executeScript(session, script);
/* 297:    */         
/* 298:424 */         log(null, "Finished.", null);
/* 299:    */       }
/* 300:    */     }
/* 301:    */     catch (Exception ex)
/* 302:    */     {
/* 303:427 */       log(null, null, ex);
/* 304:428 */       stop();
/* 305:429 */       if (getDebug())
/* 306:    */       {
/* 307:430 */         if (session != null) {
/* 308:    */           try
/* 309:    */           {
/* 310:432 */             log("Getting debug info....", null, null);
/* 311:433 */             List<String> outAndErr = session.getPythonDebugBuffer(getDebug());
/* 312:434 */             log("Output from python:\n" + (String)outAndErr.get(0), null, null);
/* 313:435 */             log("Error from python:\n" + (String)outAndErr.get(1), null, null);
/* 314:    */           }
/* 315:    */           catch (WekaException e)
/* 316:    */           {
/* 317:437 */             log(null, null, e);
/* 318:438 */             e.printStackTrace();
/* 319:    */           }
/* 320:    */         }
/* 321:441 */         log("Releasing python session", null, null);
/* 322:    */       }
/* 323:443 */       PythonSession.releaseSession(this);
/* 324:    */     }
/* 325:    */     finally
/* 326:    */     {
/* 327:445 */       this.m_busy = false;
/* 328:    */     }
/* 329:    */   }
/* 330:    */   
/* 331:    */   protected String loadScript()
/* 332:    */     throws Exception
/* 333:    */   {
/* 334:457 */     String scriptFile = this.m_env.substitute(this.m_scriptFile);
/* 335:    */     
/* 336:459 */     StringBuilder sb = new StringBuilder();
/* 337:460 */     BufferedReader br = new BufferedReader(new FileReader(scriptFile));
/* 338:    */     String line;
/* 339:462 */     while ((line = br.readLine()) != null) {
/* 340:463 */       sb.append(line).append("\n");
/* 341:    */     }
/* 342:465 */     br.close();
/* 343:    */     
/* 344:467 */     return sb.toString();
/* 345:    */   }
/* 346:    */   
/* 347:    */   protected void executeScript(PythonSession session, String script)
/* 348:    */   {
/* 349:    */     try
/* 350:    */     {
/* 351:478 */       script = this.m_env.substitute(script);
/* 352:479 */       log("Executing user script", "Executing user script...", null);
/* 353:480 */       List<String> outAndErr = session.executeScript(script, getDebug());
/* 354:481 */       if ((outAndErr.size() == 2) && (((String)outAndErr.get(1)).length() > 0)) {
/* 355:482 */         if (this.m_continueOnSysErr) {
/* 356:483 */           log((String)outAndErr.get(1), null, null);
/* 357:    */         } else {
/* 358:485 */           throw new Exception((String)outAndErr.get(1));
/* 359:    */         }
/* 360:    */       }
/* 361:489 */       if ((this.m_varsToGet != null) && (this.m_varsToGet.length() > 0))
/* 362:    */       {
/* 363:490 */         String[] vars = this.m_env.substitute(this.m_varsToGet).split(",");
/* 364:491 */         boolean[] ok = new boolean[vars.length];
/* 365:492 */         PythonSession.PythonVariableType[] types = new PythonSession.PythonVariableType[vars.length];
/* 366:    */         
/* 367:    */ 
/* 368:    */ 
/* 369:496 */         int i = 0;
/* 370:497 */         for (String v : vars) {
/* 371:498 */           if (!session.checkIfPythonVariableIsSet(v.trim(), getDebug()))
/* 372:    */           {
/* 373:499 */             if (this.m_continueOnSysErr) {
/* 374:500 */               log("Requested output variable '" + v + "' does not seem " + "to be set in python", null, null);
/* 375:    */             } else {
/* 376:503 */               throw new Exception("Requested output variable '" + v + "' does not seem to be set in python");
/* 377:    */             }
/* 378:    */           }
/* 379:    */           else
/* 380:    */           {
/* 381:507 */             ok[i] = true;
/* 382:508 */             types[(i++)] = session.getPythonVariableType(v, getDebug());
/* 383:    */           }
/* 384:    */         }
/* 385:512 */         for (i = 0; i < vars.length; i++) {
/* 386:513 */           if (ok[i] != 0)
/* 387:    */           {
/* 388:514 */             if (getDebug()) {
/* 389:515 */               log(null, "Retrieving variable '" + vars[i].trim() + "' from python. Type: " + types[i].toString(), null);
/* 390:    */             }
/* 391:518 */             if (types[i] == PythonSession.PythonVariableType.DataFrame)
/* 392:    */             {
/* 393:521 */               if (this.m_dataListeners.size() > 0)
/* 394:    */               {
/* 395:522 */                 Instances pyFrame = session.getDataFrameAsInstances(vars[i].trim(), getDebug());
/* 396:    */                 
/* 397:524 */                 DataSetEvent d = new DataSetEvent(this, pyFrame);
/* 398:525 */                 notifyDataListeners(d);
/* 399:    */               }
/* 400:526 */               else if (this.m_textListeners.size() > 0)
/* 401:    */               {
/* 402:527 */                 String textPyFrame = session.getVariableValueFromPythonAsPlainString(vars[i].trim(), getDebug());
/* 403:    */                 
/* 404:    */ 
/* 405:530 */                 TextEvent t = new TextEvent(this, textPyFrame, vars[i].trim() + ": data frame");
/* 406:    */                 
/* 407:    */ 
/* 408:533 */                 notifyTextListeners(t);
/* 409:    */               }
/* 410:    */             }
/* 411:535 */             else if (types[i] == PythonSession.PythonVariableType.Image)
/* 412:    */             {
/* 413:536 */               if (this.m_imageListeners.size() > 0)
/* 414:    */               {
/* 415:537 */                 BufferedImage image = session.getImageFromPython(vars[i].trim(), getDebug());
/* 416:    */                 
/* 417:539 */                 ImageEvent ie = new ImageEvent(this, image, vars[i].trim());
/* 418:540 */                 notifyImageListeners(ie);
/* 419:    */               }
/* 420:    */             }
/* 421:542 */             else if ((types[i] == PythonSession.PythonVariableType.String) || (types[i] == PythonSession.PythonVariableType.Unknown)) {
/* 422:544 */               if (this.m_textListeners.size() > 0)
/* 423:    */               {
/* 424:545 */                 String varAsText = session.getVariableValueFromPythonAsPlainString(vars[i].trim(), getDebug());
/* 425:    */                 
/* 426:    */ 
/* 427:548 */                 TextEvent t = new TextEvent(this, varAsText, vars[i].trim());
/* 428:549 */                 notifyTextListeners(t);
/* 429:    */               }
/* 430:    */             }
/* 431:    */           }
/* 432:    */         }
/* 433:    */       }
/* 434:    */     }
/* 435:    */     catch (Exception ex)
/* 436:    */     {
/* 437:    */       List<String> outAndErr;
/* 438:556 */       log(null, null, ex);
/* 439:557 */       stop();
/* 440:    */     }
/* 441:    */     finally
/* 442:    */     {
/* 443:    */       List<String> outAndErr;
/* 444:559 */       if (getDebug())
/* 445:    */       {
/* 446:560 */         if (session != null) {
/* 447:    */           try
/* 448:    */           {
/* 449:562 */             log("Getting debug info....", null, null);
/* 450:563 */             List<String> outAndErr = session.getPythonDebugBuffer(getDebug());
/* 451:564 */             log("Output from python:\n" + (String)outAndErr.get(0), null, null);
/* 452:565 */             log("Error from python:\n" + (String)outAndErr.get(1), null, null);
/* 453:    */           }
/* 454:    */           catch (WekaException e)
/* 455:    */           {
/* 456:567 */             log(null, null, e);
/* 457:568 */             e.printStackTrace();
/* 458:    */           }
/* 459:    */         }
/* 460:571 */         log("Releasing python session", null, null);
/* 461:    */       }
/* 462:573 */       PythonSession.releaseSession(this);
/* 463:574 */       this.m_busy = false;
/* 464:    */     }
/* 465:    */   }
/* 466:    */   
/* 467:    */   private void notifyDataListeners(DataSetEvent d)
/* 468:    */   {
/* 469:    */     List<DataSourceListener> l;
/* 470:582 */     synchronized (this)
/* 471:    */     {
/* 472:583 */       l = (List)this.m_dataListeners.clone();
/* 473:    */     }
/* 474:586 */     if (l.size() > 0) {
/* 475:587 */       for (DataSourceListener t : l) {
/* 476:588 */         t.acceptDataSet(d);
/* 477:    */       }
/* 478:    */     }
/* 479:    */   }
/* 480:    */   
/* 481:    */   private void notifyTextListeners(TextEvent e)
/* 482:    */   {
/* 483:    */     List<TextListener> l;
/* 484:597 */     synchronized (this)
/* 485:    */     {
/* 486:598 */       l = (List)this.m_textListeners.clone();
/* 487:    */     }
/* 488:601 */     if (l.size() > 0) {
/* 489:602 */       for (TextListener t : l) {
/* 490:603 */         t.acceptText(e);
/* 491:    */       }
/* 492:    */     }
/* 493:    */   }
/* 494:    */   
/* 495:    */   private void notifyImageListeners(ImageEvent e)
/* 496:    */   {
/* 497:    */     List<ImageListener> l;
/* 498:612 */     synchronized (this)
/* 499:    */     {
/* 500:613 */       l = (List)this.m_imageListeners.clone();
/* 501:    */     }
/* 502:616 */     if (l.size() > 0) {
/* 503:617 */       for (ImageListener t : l) {
/* 504:618 */         t.acceptImage(e);
/* 505:    */       }
/* 506:    */     }
/* 507:    */   }
/* 508:    */   
/* 509:    */   public synchronized void addTextListener(TextListener cl)
/* 510:    */   {
/* 511:629 */     this.m_textListeners.add(cl);
/* 512:    */   }
/* 513:    */   
/* 514:    */   public synchronized void removeTextListener(TextListener cl)
/* 515:    */   {
/* 516:638 */     this.m_textListeners.remove(cl);
/* 517:    */   }
/* 518:    */   
/* 519:    */   public synchronized void addDataSourceListener(DataSourceListener dl)
/* 520:    */   {
/* 521:647 */     this.m_dataListeners.add(dl);
/* 522:    */   }
/* 523:    */   
/* 524:    */   public synchronized void removeDataSourceListener(DataSourceListener dl)
/* 525:    */   {
/* 526:656 */     this.m_dataListeners.remove(dl);
/* 527:    */   }
/* 528:    */   
/* 529:    */   public synchronized void addImageListener(ImageListener i)
/* 530:    */   {
/* 531:665 */     this.m_imageListeners.add(i);
/* 532:    */   }
/* 533:    */   
/* 534:    */   public synchronized void removeImageListener(ImageListener i)
/* 535:    */   {
/* 536:674 */     this.m_imageListeners.remove(i);
/* 537:    */   }
/* 538:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.PythonScriptExecutor
 * JD-Core Version:    0.7.0.1
 */