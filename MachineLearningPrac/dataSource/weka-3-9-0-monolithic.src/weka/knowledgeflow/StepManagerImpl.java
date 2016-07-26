/*    1:     */ package weka.knowledgeflow;
/*    2:     */ 
/*    3:     */ import java.lang.annotation.Annotation;
/*    4:     */ import java.util.ArrayList;
/*    5:     */ import java.util.HashMap;
/*    6:     */ import java.util.Iterator;
/*    7:     */ import java.util.LinkedHashMap;
/*    8:     */ import java.util.List;
/*    9:     */ import java.util.Map;
/*   10:     */ import java.util.Map.Entry;
/*   11:     */ import weka.core.Environment;
/*   12:     */ import weka.core.Instances;
/*   13:     */ import weka.core.OptionHandler;
/*   14:     */ import weka.core.Settings;
/*   15:     */ import weka.core.Utils;
/*   16:     */ import weka.core.WekaException;
/*   17:     */ import weka.gui.Logger;
/*   18:     */ import weka.gui.beans.StreamThroughput;
/*   19:     */ import weka.gui.knowledgeflow.StepVisual;
/*   20:     */ import weka.knowledgeflow.steps.KFStep;
/*   21:     */ import weka.knowledgeflow.steps.Step;
/*   22:     */ import weka.knowledgeflow.steps.WekaAlgorithmWrapper;
/*   23:     */ 
/*   24:     */ public class StepManagerImpl
/*   25:     */   implements StepManager
/*   26:     */ {
/*   27:     */   protected Step m_managedStep;
/*   28:     */   protected boolean m_stopRequested;
/*   29:     */   protected boolean m_stepIsBusy;
/*   30:  72 */   protected Map<String, Object> m_stepProperties = new HashMap();
/*   31:     */   protected String m_managedStepEditor;
/*   32:  83 */   protected Map<String, List<StepManager>> m_connectedByTypeIncoming = new LinkedHashMap();
/*   33:  87 */   protected Map<String, List<StepManager>> m_connectedByTypeOutgoing = new LinkedHashMap();
/*   34:  91 */   protected Map<String, List<StepOutputListener>> m_outputListeners = new LinkedHashMap();
/*   35:     */   protected StepVisual m_stepVisual;
/*   36: 105 */   protected int m_x = -1;
/*   37: 113 */   protected int m_y = -1;
/*   38:     */   protected BaseExecutionEnvironment m_executionEnvironment;
/*   39: 122 */   protected Settings m_settings = new Settings("weka", "knowledgeflow");
/*   40:     */   protected LogManager m_log;
/*   41:     */   protected transient StreamThroughput m_throughput;
/*   42:     */   protected boolean m_adjustForGraphicalRendering;
/*   43:     */   protected boolean m_stepIsResourceIntensive;
/*   44:     */   
/*   45:     */   public StepManagerImpl(Step step)
/*   46:     */   {
/*   47: 152 */     setManagedStep(step);
/*   48:     */   }
/*   49:     */   
/*   50:     */   public String getName()
/*   51:     */   {
/*   52: 162 */     return this.m_managedStep.getName();
/*   53:     */   }
/*   54:     */   
/*   55:     */   public Step getManagedStep()
/*   56:     */   {
/*   57: 172 */     return this.m_managedStep;
/*   58:     */   }
/*   59:     */   
/*   60:     */   public void setManagedStep(Step step)
/*   61:     */   {
/*   62: 181 */     this.m_managedStep = step;
/*   63: 182 */     step.setStepManager(this);
/*   64: 183 */     setManagedStepEditorClass(step.getCustomEditorForStep());
/*   65:     */     
/*   66: 185 */     Annotation a = step.getClass().getAnnotation(KFStep.class);
/*   67: 186 */     this.m_stepIsResourceIntensive = ((a != null) && (((KFStep)a).resourceIntensive()));
/*   68:     */   }
/*   69:     */   
/*   70:     */   public void setStepIsResourceIntensive(boolean resourceIntensive)
/*   71:     */   {
/*   72: 196 */     this.m_stepIsResourceIntensive = resourceIntensive;
/*   73:     */   }
/*   74:     */   
/*   75:     */   public boolean stepIsResourceIntensive()
/*   76:     */   {
/*   77: 206 */     return this.m_stepIsResourceIntensive;
/*   78:     */   }
/*   79:     */   
/*   80:     */   public StepVisual getStepVisual()
/*   81:     */   {
/*   82: 215 */     return this.m_stepVisual;
/*   83:     */   }
/*   84:     */   
/*   85:     */   public void setStepVisual(StepVisual visual)
/*   86:     */   {
/*   87: 224 */     this.m_stepVisual = visual;
/*   88: 225 */     if ((this.m_x != -1) && (this.m_y != -1))
/*   89:     */     {
/*   90: 226 */       this.m_stepVisual.setX(this.m_x);
/*   91: 227 */       this.m_stepVisual.setY(this.m_y);
/*   92:     */     }
/*   93:     */   }
/*   94:     */   
/*   95:     */   public void setStepProperty(String name, Object value)
/*   96:     */   {
/*   97: 238 */     this.m_stepProperties.put(name, value);
/*   98:     */   }
/*   99:     */   
/*  100:     */   public Object getStepProperty(String name)
/*  101:     */   {
/*  102: 248 */     return this.m_stepProperties.get(name);
/*  103:     */   }
/*  104:     */   
/*  105:     */   protected String getManagedStepEditorClass()
/*  106:     */   {
/*  107: 259 */     return this.m_managedStepEditor;
/*  108:     */   }
/*  109:     */   
/*  110:     */   protected void setManagedStepEditorClass(String editor)
/*  111:     */   {
/*  112: 270 */     this.m_managedStepEditor = editor;
/*  113:     */   }
/*  114:     */   
/*  115:     */   public ExecutionEnvironment getExecutionEnvironment()
/*  116:     */   {
/*  117: 280 */     return this.m_executionEnvironment;
/*  118:     */   }
/*  119:     */   
/*  120:     */   public Settings getSettings()
/*  121:     */   {
/*  122: 292 */     if (getExecutionEnvironment() == null) {
/*  123: 293 */       throw new IllegalStateException("There is no execution environment available!");
/*  124:     */     }
/*  125: 296 */     return getExecutionEnvironment().getSettings();
/*  126:     */   }
/*  127:     */   
/*  128:     */   protected void setExecutionEnvironment(ExecutionEnvironment env)
/*  129:     */     throws WekaException
/*  130:     */   {
/*  131: 308 */     if (!(env instanceof BaseExecutionEnvironment)) {
/*  132: 309 */       throw new WekaException("Execution environments need to be BaseExecutionEnvironment (or subclass thereof)");
/*  133:     */     }
/*  134: 314 */     this.m_executionEnvironment = ((BaseExecutionEnvironment)env);
/*  135: 315 */     setLog(this.m_executionEnvironment.getLog());
/*  136: 316 */     setLoggingLevel(this.m_executionEnvironment.getLoggingLevel());
/*  137:     */   }
/*  138:     */   
/*  139:     */   public LoggingLevel getLoggingLevel()
/*  140:     */   {
/*  141: 326 */     return this.m_log != null ? this.m_log.getLoggingLevel() : LoggingLevel.BASIC;
/*  142:     */   }
/*  143:     */   
/*  144:     */   public void setLoggingLevel(LoggingLevel newLevel)
/*  145:     */   {
/*  146: 335 */     if (this.m_log == null) {
/*  147: 336 */       this.m_log = new LogManager(getManagedStep());
/*  148:     */     }
/*  149: 338 */     this.m_log.setLoggingLevel(newLevel);
/*  150:     */   }
/*  151:     */   
/*  152:     */   public Logger getLog()
/*  153:     */   {
/*  154: 348 */     return this.m_log != null ? this.m_log.getLog() : null;
/*  155:     */   }
/*  156:     */   
/*  157:     */   public void setLog(Logger log)
/*  158:     */   {
/*  159: 357 */     this.m_log = new LogManager(getManagedStep());
/*  160:     */     
/*  161: 359 */     this.m_log.setLog(log);
/*  162:     */   }
/*  163:     */   
/*  164:     */   protected boolean initStep()
/*  165:     */   {
/*  166: 368 */     boolean initializedOK = false;
/*  167: 369 */     this.m_stepIsBusy = false;
/*  168: 370 */     this.m_stopRequested = false;
/*  169:     */     try
/*  170:     */     {
/*  171: 372 */       getManagedStep().stepInit();
/*  172:     */       
/*  173: 374 */       initializedOK = true;
/*  174:     */     }
/*  175:     */     catch (WekaException ex)
/*  176:     */     {
/*  177: 376 */       logError(ex.getMessage(), ex);
/*  178:     */     }
/*  179:     */     catch (Throwable ex)
/*  180:     */     {
/*  181: 378 */       logError(ex.getMessage(), ex);
/*  182:     */     }
/*  183: 381 */     this.m_throughput = null;
/*  184:     */     
/*  185: 383 */     return initializedOK;
/*  186:     */   }
/*  187:     */   
/*  188:     */   public boolean isStepBusy()
/*  189:     */   {
/*  190: 394 */     return this.m_stepIsBusy;
/*  191:     */   }
/*  192:     */   
/*  193:     */   public boolean isStopRequested()
/*  194:     */   {
/*  195: 404 */     return this.m_stopRequested;
/*  196:     */   }
/*  197:     */   
/*  198:     */   public void setStopRequested(boolean stopRequested)
/*  199:     */   {
/*  200: 413 */     this.m_stopRequested = stopRequested;
/*  201:     */   }
/*  202:     */   
/*  203:     */   public void processing()
/*  204:     */   {
/*  205: 421 */     this.m_stepIsBusy = true;
/*  206:     */   }
/*  207:     */   
/*  208:     */   public void finished()
/*  209:     */   {
/*  210: 430 */     this.m_stepIsBusy = false;
/*  211: 431 */     if (!isStopRequested()) {
/*  212: 432 */       statusMessage("Finished.");
/*  213:     */     }
/*  214:     */   }
/*  215:     */   
/*  216:     */   public void interrupted()
/*  217:     */   {
/*  218: 442 */     this.m_stepIsBusy = false;
/*  219:     */   }
/*  220:     */   
/*  221:     */   public boolean isStreamFinished(Data data)
/*  222:     */   {
/*  223: 456 */     return ((Boolean)data.getPayloadElement("incremental_stream_end", Boolean.valueOf(false))).booleanValue();
/*  224:     */   }
/*  225:     */   
/*  226:     */   public void throughputUpdateStart()
/*  227:     */   {
/*  228: 465 */     if (this.m_throughput == null) {
/*  229: 466 */       this.m_throughput = new StreamThroughput(stepStatusMessagePrefix());
/*  230:     */     }
/*  231: 468 */     processing();
/*  232: 469 */     this.m_throughput.updateStart();
/*  233:     */   }
/*  234:     */   
/*  235:     */   public void throughputUpdateEnd()
/*  236:     */   {
/*  237: 478 */     if (this.m_throughput != null)
/*  238:     */     {
/*  239: 479 */       this.m_throughput.updateEnd(this.m_log.getLog());
/*  240: 481 */       if (isStopRequested()) {
/*  241: 482 */         finished();
/*  242:     */       }
/*  243:     */     }
/*  244:     */   }
/*  245:     */   
/*  246:     */   public void throughputFinished(Data... data)
/*  247:     */     throws WekaException
/*  248:     */   {
/*  249: 500 */     finished();
/*  250: 501 */     if (data.length > 0)
/*  251:     */     {
/*  252: 502 */       for (Data d : data) {
/*  253: 503 */         d.setPayloadElement("incremental_stream_end", Boolean.valueOf(true));
/*  254:     */       }
/*  255: 505 */       outputData(data);
/*  256:     */     }
/*  257: 507 */     if (this.m_throughput != null) {
/*  258: 508 */       this.m_throughput.finished(this.m_log.getLog());
/*  259:     */     }
/*  260: 512 */     interrupted();
/*  261:     */   }
/*  262:     */   
/*  263:     */   private void disconnectStep(List<StepManager> connList, Step toDisconnect)
/*  264:     */   {
/*  265: 516 */     Iterator<StepManager> iter = connList.iterator();
/*  266: 517 */     while (iter.hasNext())
/*  267:     */     {
/*  268: 518 */       StepManagerImpl candidate = (StepManagerImpl)iter.next();
/*  269: 519 */       if (toDisconnect == candidate.getManagedStep())
/*  270:     */       {
/*  271: 520 */         iter.remove();
/*  272: 521 */         break;
/*  273:     */       }
/*  274:     */     }
/*  275:     */   }
/*  276:     */   
/*  277:     */   public void disconnectStepWithConnection(Step toDisconnect, String connType)
/*  278:     */   {
/*  279: 538 */     List<StepManager> connectedWithType = (List)this.m_connectedByTypeIncoming.get(connType);
/*  280: 540 */     if (connectedWithType != null)
/*  281:     */     {
/*  282: 541 */       disconnectStep(connectedWithType, toDisconnect);
/*  283: 542 */       if (connectedWithType.size() == 0) {
/*  284: 543 */         this.m_connectedByTypeIncoming.remove(connType);
/*  285:     */       }
/*  286:     */     }
/*  287: 548 */     connectedWithType = (List)this.m_connectedByTypeOutgoing.get(connType);
/*  288: 549 */     if (connectedWithType != null)
/*  289:     */     {
/*  290: 550 */       disconnectStep(connectedWithType, toDisconnect);
/*  291: 551 */       if (connectedWithType.size() == 0) {
/*  292: 552 */         this.m_connectedByTypeOutgoing.remove(connType);
/*  293:     */       }
/*  294:     */     }
/*  295:     */   }
/*  296:     */   
/*  297:     */   public void disconnectStep(Step toDisconnect)
/*  298:     */   {
/*  299: 567 */     List<String> emptyCons = new ArrayList();
/*  300: 568 */     for (Map.Entry<String, List<StepManager>> e : this.m_connectedByTypeIncoming.entrySet())
/*  301:     */     {
/*  302: 571 */       List<StepManager> sList = (List)e.getValue();
/*  303: 572 */       disconnectStep(sList, toDisconnect);
/*  304: 573 */       if (sList.size() == 0) {
/*  305: 574 */         emptyCons.add(e.getKey());
/*  306:     */       }
/*  307:     */     }
/*  308: 577 */     for (String conn : emptyCons) {
/*  309: 578 */       this.m_connectedByTypeIncoming.remove(conn);
/*  310:     */     }
/*  311: 580 */     emptyCons.clear();
/*  312: 583 */     for (Map.Entry<String, List<StepManager>> e : this.m_connectedByTypeOutgoing.entrySet())
/*  313:     */     {
/*  314: 586 */       List<StepManager> sList = (List)e.getValue();
/*  315: 587 */       disconnectStep(sList, toDisconnect);
/*  316: 588 */       if (sList.size() == 0) {
/*  317: 589 */         emptyCons.add(e.getKey());
/*  318:     */       }
/*  319:     */     }
/*  320: 592 */     for (String conn : emptyCons) {
/*  321: 593 */       this.m_connectedByTypeOutgoing.remove(conn);
/*  322:     */     }
/*  323:     */   }
/*  324:     */   
/*  325:     */   public void clearAllConnections()
/*  326:     */   {
/*  327: 603 */     this.m_connectedByTypeIncoming.clear();
/*  328: 604 */     this.m_connectedByTypeOutgoing.clear();
/*  329:     */   }
/*  330:     */   
/*  331:     */   public void addIncomingConnection(String connectionName, StepManagerImpl step)
/*  332:     */   {
/*  333: 617 */     List<StepManager> steps = (List)this.m_connectedByTypeIncoming.get(connectionName);
/*  334: 618 */     if (steps == null)
/*  335:     */     {
/*  336: 619 */       steps = new ArrayList();
/*  337: 620 */       this.m_connectedByTypeIncoming.put(connectionName, steps);
/*  338:     */     }
/*  339: 622 */     steps.add(step);
/*  340:     */   }
/*  341:     */   
/*  342:     */   public void removeIncomingConnection(String connectionName, StepManagerImpl step)
/*  343:     */   {
/*  344: 634 */     List<StepManager> steps = (List)this.m_connectedByTypeIncoming.get(connectionName);
/*  345: 635 */     steps.remove(step);
/*  346:     */   }
/*  347:     */   
/*  348:     */   public boolean addOutgoingConnection(String connectionName, StepManagerImpl step)
/*  349:     */   {
/*  350: 650 */     return addOutgoingConnection(connectionName, step, false);
/*  351:     */   }
/*  352:     */   
/*  353:     */   public boolean addOutgoingConnection(String connectionName, StepManagerImpl step, boolean force)
/*  354:     */   {
/*  355: 671 */     boolean connSuccessful = false;
/*  356: 672 */     List<String> targetCanAccept = step.getManagedStep().getIncomingConnectionTypes();
/*  357: 674 */     if ((targetCanAccept.contains(connectionName)) || (force))
/*  358:     */     {
/*  359: 675 */       List<StepManager> steps = (List)this.m_connectedByTypeOutgoing.get(connectionName);
/*  360: 676 */       if (steps == null)
/*  361:     */       {
/*  362: 677 */         steps = new ArrayList();
/*  363: 678 */         this.m_connectedByTypeOutgoing.put(connectionName, steps);
/*  364:     */       }
/*  365: 680 */       step.addIncomingConnection(connectionName, this);
/*  366: 681 */       steps.add(step);
/*  367: 682 */       connSuccessful = true;
/*  368:     */     }
/*  369: 684 */     return connSuccessful;
/*  370:     */   }
/*  371:     */   
/*  372:     */   public void removeOutgoingConnection(String connectionName, StepManagerImpl step)
/*  373:     */   {
/*  374: 696 */     List<StepManager> steps = (List)this.m_connectedByTypeOutgoing.get(connectionName);
/*  375: 697 */     steps.remove(step);
/*  376:     */     
/*  377:     */ 
/*  378: 700 */     step.removeIncomingConnection(connectionName, this);
/*  379:     */   }
/*  380:     */   
/*  381:     */   public List<StepManager> getIncomingConnectedStepsOfConnectionType(String connectionName)
/*  382:     */   {
/*  383: 712 */     return this.m_connectedByTypeIncoming.get(connectionName) != null ? (List)this.m_connectedByTypeIncoming.get(connectionName) : new ArrayList();
/*  384:     */   }
/*  385:     */   
/*  386:     */   public List<StepManager> getOutgoingConnectedStepsOfConnectionType(String connectionName)
/*  387:     */   {
/*  388: 719 */     return this.m_connectedByTypeOutgoing.get(connectionName) != null ? (List)this.m_connectedByTypeOutgoing.get(connectionName) : new ArrayList();
/*  389:     */   }
/*  390:     */   
/*  391:     */   private StepManager getConnectedStepWithName(String stepName, Map<String, List<StepManager>> connectedSteps)
/*  392:     */   {
/*  393: 725 */     StepManager result = null;
/*  394: 727 */     for (Map.Entry<String, List<StepManager>> e : connectedSteps.entrySet())
/*  395:     */     {
/*  396: 728 */       List<StepManager> stepsOfConnType = (List)e.getValue();
/*  397: 729 */       for (StepManager s : stepsOfConnType) {
/*  398: 730 */         if (((StepManagerImpl)s).getManagedStep().getName().equals(stepName))
/*  399:     */         {
/*  400: 731 */           result = s;
/*  401: 732 */           break;
/*  402:     */         }
/*  403:     */       }
/*  404:     */     }
/*  405: 737 */     return result;
/*  406:     */   }
/*  407:     */   
/*  408:     */   public StepManager getIncomingConnectedStepWithName(String stepName)
/*  409:     */   {
/*  410: 748 */     return getConnectedStepWithName(stepName, this.m_connectedByTypeIncoming);
/*  411:     */   }
/*  412:     */   
/*  413:     */   public StepManager getOutgoingConnectedStepWithName(String stepName)
/*  414:     */   {
/*  415: 759 */     return getConnectedStepWithName(stepName, this.m_connectedByTypeOutgoing);
/*  416:     */   }
/*  417:     */   
/*  418:     */   public Map<String, List<StepManager>> getOutgoingConnections()
/*  419:     */   {
/*  420: 769 */     return this.m_connectedByTypeOutgoing;
/*  421:     */   }
/*  422:     */   
/*  423:     */   public Map<String, List<StepManager>> getIncomingConnections()
/*  424:     */   {
/*  425: 779 */     return this.m_connectedByTypeIncoming;
/*  426:     */   }
/*  427:     */   
/*  428:     */   public void addStepOutputListener(StepOutputListener listener, String outputConnectionName)
/*  429:     */   {
/*  430: 792 */     List<StepOutputListener> listenersForConnectionType = (List)this.m_outputListeners.get(outputConnectionName);
/*  431: 794 */     if (listenersForConnectionType == null)
/*  432:     */     {
/*  433: 795 */       listenersForConnectionType = new ArrayList();
/*  434: 796 */       this.m_outputListeners.put(outputConnectionName, listenersForConnectionType);
/*  435:     */     }
/*  436: 799 */     if (!listenersForConnectionType.contains(listener)) {
/*  437: 800 */       listenersForConnectionType.add(listener);
/*  438:     */     }
/*  439:     */   }
/*  440:     */   
/*  441:     */   public void removeStepOutputListener(StepOutputListener listener, String outputConnectionName)
/*  442:     */   {
/*  443: 813 */     List<StepOutputListener> listenersForConnectionType = (List)this.m_outputListeners.get(outputConnectionName);
/*  444: 816 */     if (listenersForConnectionType != null) {
/*  445: 817 */       listenersForConnectionType.remove(listener);
/*  446:     */     }
/*  447:     */   }
/*  448:     */   
/*  449:     */   public void clearAllStepOutputListeners()
/*  450:     */   {
/*  451: 825 */     this.m_outputListeners.clear();
/*  452:     */   }
/*  453:     */   
/*  454:     */   public void clearStepOutputListeners(String outputConnectionName)
/*  455:     */   {
/*  456: 836 */     List<StepOutputListener> listenersForConnectionType = (List)this.m_outputListeners.get(outputConnectionName);
/*  457: 839 */     if (listenersForConnectionType != null) {
/*  458: 840 */       listenersForConnectionType.clear();
/*  459:     */     }
/*  460:     */   }
/*  461:     */   
/*  462:     */   protected void notifyOutputListeners(Data data)
/*  463:     */     throws WekaException
/*  464:     */   {
/*  465: 850 */     List<StepOutputListener> listenersForType = (List)this.m_outputListeners.get(data.getConnectionName());
/*  466: 852 */     if (listenersForType != null) {
/*  467: 853 */       for (StepOutputListener l : listenersForType) {
/*  468: 854 */         if (!l.dataFromStep(data)) {
/*  469: 855 */           logWarning("StepOutputListener '" + l.getClass().getCanonicalName() + "' " + "did not process data '" + data.getConnectionName() + "' successfully'");
/*  470:     */         }
/*  471:     */       }
/*  472:     */     }
/*  473:     */   }
/*  474:     */   
/*  475:     */   public void outputData(String outgoingConnectionName, Data data)
/*  476:     */     throws WekaException
/*  477:     */   {
/*  478: 877 */     if (!isStopRequested())
/*  479:     */     {
/*  480: 878 */       data.setConnectionName(outgoingConnectionName);
/*  481: 879 */       data.setSourceStep(this.m_managedStep);
/*  482:     */       
/*  483: 881 */       List<StepManager> toNotify = (List)this.m_connectedByTypeOutgoing.get(outgoingConnectionName);
/*  484: 883 */       if (toNotify != null) {
/*  485: 884 */         for (StepManager s : toNotify) {
/*  486: 885 */           if (!isStopRequested()) {
/*  487: 886 */             this.m_executionEnvironment.sendDataToStep((StepManagerImpl)s, new Data[] { data });
/*  488:     */           }
/*  489:     */         }
/*  490:     */       }
/*  491: 891 */       notifyOutputListeners(data);
/*  492:     */     }
/*  493:     */   }
/*  494:     */   
/*  495:     */   public void outputData(Data... data)
/*  496:     */     throws WekaException
/*  497:     */   {
/*  498: 912 */     if (!isStopRequested())
/*  499:     */     {
/*  500: 913 */       Map<StepManagerImpl, List<Data>> stepsToSendTo = new LinkedHashMap();
/*  501: 916 */       for (Data d : data)
/*  502:     */       {
/*  503: 917 */         d.setSourceStep(this.m_managedStep);
/*  504: 918 */         if ((d.getConnectionName() == null) || (d.getConnectionName().length() == 0)) {
/*  505: 920 */           throw new WekaException("Data does not have a connection name set.");
/*  506:     */         }
/*  507: 922 */         List<StepManager> candidates = (List)this.m_connectedByTypeOutgoing.get(d.getConnectionName());
/*  508: 924 */         if (candidates != null) {
/*  509: 925 */           for (StepManager s : candidates)
/*  510:     */           {
/*  511: 926 */             List<Data> toReceive = (List)stepsToSendTo.get(s);
/*  512: 927 */             if (toReceive == null)
/*  513:     */             {
/*  514: 928 */               toReceive = new ArrayList();
/*  515: 929 */               stepsToSendTo.put((StepManagerImpl)s, toReceive);
/*  516:     */             }
/*  517: 931 */             toReceive.add(d);
/*  518:     */           }
/*  519:     */         }
/*  520: 935 */         notifyOutputListeners(d);
/*  521:     */       }
/*  522: 938 */       for (Map.Entry<StepManagerImpl, List<Data>> e : stepsToSendTo.entrySet()) {
/*  523: 939 */         if (!((StepManagerImpl)e.getKey()).isStopRequested()) {
/*  524: 940 */           this.m_executionEnvironment.sendDataToStep((StepManagerImpl)e.getKey(), (Data[])((List)e.getValue()).toArray(new Data[((List)e.getValue()).size()]));
/*  525:     */         }
/*  526:     */       }
/*  527:     */     }
/*  528:     */   }
/*  529:     */   
/*  530:     */   public void outputData(String outgoingConnectionName, String stepName, Data data)
/*  531:     */     throws WekaException
/*  532:     */   {
/*  533: 961 */     if (!isStopRequested())
/*  534:     */     {
/*  535: 962 */       data.setConnectionName(outgoingConnectionName);
/*  536: 963 */       data.setSourceStep(this.m_managedStep);
/*  537:     */       
/*  538: 965 */       List<StepManager> outConnsOfType = (List)this.m_connectedByTypeOutgoing.get(outgoingConnectionName);
/*  539:     */       
/*  540: 967 */       StepManagerImpl namedTarget = null;
/*  541: 968 */       for (StepManager c : outConnsOfType) {
/*  542: 969 */         if (((StepManagerImpl)c).getManagedStep().getName().equals(stepName)) {
/*  543: 970 */           namedTarget = (StepManagerImpl)c;
/*  544:     */         }
/*  545:     */       }
/*  546: 974 */       if ((namedTarget != null) && (!namedTarget.isStopRequested())) {
/*  547: 975 */         this.m_executionEnvironment.sendDataToStep(namedTarget, new Data[] { data });
/*  548:     */       }
/*  549: 980 */       notifyOutputListeners(data);
/*  550:     */     }
/*  551:     */   }
/*  552:     */   
/*  553:     */   protected void startStep()
/*  554:     */   {
/*  555:     */     try
/*  556:     */     {
/*  557: 989 */       getManagedStep().start();
/*  558:     */     }
/*  559:     */     catch (WekaException ex)
/*  560:     */     {
/*  561: 991 */       interrupted();
/*  562: 992 */       logError(ex.getMessage(), ex);
/*  563:     */     }
/*  564:     */     catch (Throwable ex)
/*  565:     */     {
/*  566: 994 */       interrupted();
/*  567: 995 */       logError(ex.getMessage(), ex);
/*  568:     */     }
/*  569:     */   }
/*  570:     */   
/*  571:     */   protected void stopStep()
/*  572:     */   {
/*  573:1003 */     this.m_stopRequested = true;
/*  574:1004 */     getManagedStep().stop();
/*  575:     */   }
/*  576:     */   
/*  577:     */   protected void processIncoming(Data data)
/*  578:     */   {
/*  579:     */     try
/*  580:     */     {
/*  581:1014 */       getManagedStep().processIncoming(data);
/*  582:     */     }
/*  583:     */     catch (WekaException ex)
/*  584:     */     {
/*  585:1016 */       interrupted();
/*  586:1017 */       logError(ex.getMessage(), ex);
/*  587:     */     }
/*  588:     */     catch (Throwable e)
/*  589:     */     {
/*  590:1019 */       interrupted();
/*  591:1020 */       logError(e.getMessage(), e);
/*  592:     */     }
/*  593:     */   }
/*  594:     */   
/*  595:     */   public List<String> getStepOutgoingConnectionTypes()
/*  596:     */   {
/*  597:1033 */     this.m_adjustForGraphicalRendering = true;
/*  598:1034 */     List<String> results = getManagedStep().getOutgoingConnectionTypes();
/*  599:1035 */     this.m_adjustForGraphicalRendering = false;
/*  600:1036 */     return results;
/*  601:     */   }
/*  602:     */   
/*  603:     */   public int numIncomingConnections()
/*  604:     */   {
/*  605:1046 */     int size = 0;
/*  606:1048 */     for (Map.Entry<String, List<StepManager>> e : this.m_connectedByTypeIncoming.entrySet()) {
/*  607:1050 */       if (this.m_adjustForGraphicalRendering) {
/*  608:1051 */         size += numIncomingConnectionsOfType((String)e.getKey());
/*  609:     */       } else {
/*  610:1053 */         size += ((List)e.getValue()).size();
/*  611:     */       }
/*  612:     */     }
/*  613:1057 */     return size;
/*  614:     */   }
/*  615:     */   
/*  616:     */   public int numIncomingConnectionsOfType(String connectionName)
/*  617:     */   {
/*  618:1068 */     int num = 0;
/*  619:1069 */     List<StepManager> inOfType = (List)this.m_connectedByTypeIncoming.get(connectionName);
/*  620:1070 */     if (inOfType != null) {
/*  621:1071 */       if (this.m_adjustForGraphicalRendering) {
/*  622:1074 */         for (StepManager connS : inOfType)
/*  623:     */         {
/*  624:1075 */           List<String> generatableOutputCons = ((StepManagerImpl)connS).getStepOutgoingConnectionTypes();
/*  625:1077 */           if (generatableOutputCons.contains(connectionName)) {
/*  626:1078 */             num++;
/*  627:     */           }
/*  628:     */         }
/*  629:     */       } else {
/*  630:1082 */         num = inOfType.size();
/*  631:     */       }
/*  632:     */     }
/*  633:1086 */     return num;
/*  634:     */   }
/*  635:     */   
/*  636:     */   public int numOutgoingConnections()
/*  637:     */   {
/*  638:1096 */     int size = 0;
/*  639:1098 */     for (Map.Entry<String, List<StepManager>> e : this.m_connectedByTypeOutgoing.entrySet()) {
/*  640:1100 */       size += ((List)e.getValue()).size() - (this.m_adjustForGraphicalRendering ? 1 : 0);
/*  641:     */     }
/*  642:1102 */     if (size < 0) {
/*  643:1103 */       size = 0;
/*  644:     */     }
/*  645:1105 */     return size;
/*  646:     */   }
/*  647:     */   
/*  648:     */   public int numOutgoingConnectionsOfType(String connectionName)
/*  649:     */   {
/*  650:1117 */     int num = 0;
/*  651:1118 */     List<StepManager> outOfType = (List)this.m_connectedByTypeOutgoing.get(connectionName);
/*  652:1119 */     if (outOfType != null)
/*  653:     */     {
/*  654:1120 */       num = outOfType.size();
/*  655:1121 */       if (this.m_adjustForGraphicalRendering) {
/*  656:1122 */         num--;
/*  657:     */       }
/*  658:     */     }
/*  659:1126 */     return num;
/*  660:     */   }
/*  661:     */   
/*  662:     */   public Instances getIncomingStructureForConnectionType(String connectionName)
/*  663:     */     throws WekaException
/*  664:     */   {
/*  665:1148 */     if (getIncomingConnectedStepsOfConnectionType(connectionName).size() == 1) {
/*  666:1149 */       return ((StepManagerImpl)getIncomingConnectedStepsOfConnectionType(connectionName).get(0)).getManagedStep().outputStructureForConnectionType(connectionName);
/*  667:     */     }
/*  668:1154 */     return null;
/*  669:     */   }
/*  670:     */   
/*  671:     */   public Instances getIncomingStructureFromStep(StepManager sourceStep, String connectionName)
/*  672:     */     throws WekaException
/*  673:     */   {
/*  674:1172 */     return ((StepManagerImpl)sourceStep).getManagedStep().outputStructureForConnectionType(connectionName);
/*  675:     */   }
/*  676:     */   
/*  677:     */   public void logLow(String message)
/*  678:     */   {
/*  679:1183 */     if (this.m_log != null) {
/*  680:1184 */       this.m_log.logLow(message);
/*  681:     */     }
/*  682:     */   }
/*  683:     */   
/*  684:     */   public void logBasic(String message)
/*  685:     */   {
/*  686:1195 */     if (this.m_log != null) {
/*  687:1196 */       this.m_log.logBasic(message);
/*  688:     */     }
/*  689:     */   }
/*  690:     */   
/*  691:     */   public void logDetailed(String message)
/*  692:     */   {
/*  693:1207 */     if (this.m_log != null) {
/*  694:1208 */       this.m_log.logDetailed(message);
/*  695:     */     }
/*  696:     */   }
/*  697:     */   
/*  698:     */   public void logDebug(String message)
/*  699:     */   {
/*  700:1219 */     if (this.m_log != null) {
/*  701:1220 */       this.m_log.logDebug(message);
/*  702:     */     }
/*  703:     */   }
/*  704:     */   
/*  705:     */   public void logWarning(String message)
/*  706:     */   {
/*  707:1231 */     if (this.m_log != null)
/*  708:     */     {
/*  709:1232 */       this.m_log.logWarning(message);
/*  710:1233 */       this.m_log.statusMessage("WARNING: " + message);
/*  711:     */     }
/*  712:     */   }
/*  713:     */   
/*  714:     */   public void logError(String message, Throwable cause)
/*  715:     */   {
/*  716:1245 */     if (this.m_log != null)
/*  717:     */     {
/*  718:1246 */       this.m_log.log(message, LoggingLevel.ERROR, cause);
/*  719:1247 */       this.m_log.statusMessage("ERROR: " + message);
/*  720:     */     }
/*  721:1249 */     if (this.m_executionEnvironment != null) {
/*  722:1251 */       this.m_executionEnvironment.stopProcessing();
/*  723:     */     }
/*  724:     */   }
/*  725:     */   
/*  726:     */   public void statusMessage(String message)
/*  727:     */   {
/*  728:1262 */     if (this.m_log != null) {
/*  729:1263 */       this.m_log.statusMessage(message);
/*  730:     */     }
/*  731:     */   }
/*  732:     */   
/*  733:     */   public void log(String message, LoggingLevel level)
/*  734:     */   {
/*  735:1275 */     if (this.m_log != null) {
/*  736:1276 */       this.m_log.log(message, level, null);
/*  737:     */     }
/*  738:     */   }
/*  739:     */   
/*  740:     */   public String environmentSubstitute(String source)
/*  741:     */   {
/*  742:1288 */     Environment toUse = Environment.getSystemWide();
/*  743:1290 */     if (getExecutionEnvironment() != null) {
/*  744:1291 */       toUse = getExecutionEnvironment().getEnvironmentVariables();
/*  745:     */     }
/*  746:1294 */     String result = source;
/*  747:1296 */     if (source != null) {
/*  748:     */       try
/*  749:     */       {
/*  750:1298 */         result = toUse.substitute(source);
/*  751:     */       }
/*  752:     */       catch (Exception ex) {}
/*  753:     */     }
/*  754:1304 */     return result;
/*  755:     */   }
/*  756:     */   
/*  757:     */   public Step getInfoStep(Class stepClass)
/*  758:     */     throws WekaException
/*  759:     */   {
/*  760:1319 */     Step info = getInfoStep();
/*  761:1320 */     if (info.getClass() != stepClass) {
/*  762:1321 */       throw new WekaException("The managed step (" + info.getClass().getCanonicalName() + ") is not " + "not an instance of the required class: " + stepClass.getCanonicalName());
/*  763:     */     }
/*  764:1327 */     return info;
/*  765:     */   }
/*  766:     */   
/*  767:     */   public Step getInfoStep()
/*  768:     */     throws WekaException
/*  769:     */   {
/*  770:1339 */     if (numOutgoingConnectionsOfType("info") > 0) {
/*  771:1340 */       return getManagedStep();
/*  772:     */     }
/*  773:1343 */     throw new WekaException("There are no outgoing info connections from this step!");
/*  774:     */   }
/*  775:     */   
/*  776:     */   public StepManager findStepInFlow(String stepNameToFind)
/*  777:     */   {
/*  778:1356 */     Flow flow = this.m_executionEnvironment.getFlowExecutor().getFlow();
/*  779:     */     
/*  780:1358 */     return flow.findStep(stepNameToFind);
/*  781:     */   }
/*  782:     */   
/*  783:     */   public String stepStatusMessagePrefix()
/*  784:     */   {
/*  785:1368 */     String prefix = (getManagedStep() != null ? getManagedStep().getName() : "Unknown") + "$";
/*  786:     */     
/*  787:     */ 
/*  788:1371 */     prefix = prefix + (getManagedStep() != null ? getManagedStep().hashCode() : 1) + "|";
/*  789:1373 */     if ((getManagedStep() instanceof WekaAlgorithmWrapper))
/*  790:     */     {
/*  791:1374 */       Object wrappedAlgo = ((WekaAlgorithmWrapper)getManagedStep()).getWrappedAlgorithm();
/*  792:1376 */       if ((wrappedAlgo instanceof OptionHandler)) {
/*  793:1377 */         prefix = prefix + Utils.joinOptions(((OptionHandler)wrappedAlgo).getOptions()) + "|";
/*  794:     */       }
/*  795:     */     }
/*  796:1382 */     return prefix;
/*  797:     */   }
/*  798:     */   
/*  799:     */   protected static boolean connectionIsIncremental(Data conn)
/*  800:     */   {
/*  801:1396 */     return (conn.getConnectionName().equalsIgnoreCase("instance")) || (conn.getConnectionName().equalsIgnoreCase("incrementalClassifier")) || (conn.getConnectionName().equalsIgnoreCase("chart")) || (((Boolean)conn.getPayloadElement("incremental_stream", Boolean.valueOf(false))).booleanValue());
/*  802:     */   }
/*  803:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.StepManagerImpl
 * JD-Core Version:    0.7.0.1
 */