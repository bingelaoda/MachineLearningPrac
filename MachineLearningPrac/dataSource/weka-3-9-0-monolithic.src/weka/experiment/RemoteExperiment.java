/*    1:     */ package weka.experiment;
/*    2:     */ 
/*    3:     */ import java.io.BufferedInputStream;
/*    4:     */ import java.io.BufferedOutputStream;
/*    5:     */ import java.io.File;
/*    6:     */ import java.io.FileInputStream;
/*    7:     */ import java.io.FileOutputStream;
/*    8:     */ import java.io.ObjectInputStream;
/*    9:     */ import java.io.ObjectOutputStream;
/*   10:     */ import java.io.PrintStream;
/*   11:     */ import java.rmi.Naming;
/*   12:     */ import java.util.ArrayList;
/*   13:     */ import java.util.Enumeration;
/*   14:     */ import javax.swing.DefaultListModel;
/*   15:     */ import weka.core.Option;
/*   16:     */ import weka.core.OptionHandler;
/*   17:     */ import weka.core.Queue;
/*   18:     */ import weka.core.RevisionUtils;
/*   19:     */ import weka.core.SerializedObject;
/*   20:     */ import weka.core.Utils;
/*   21:     */ import weka.core.WekaPackageManager;
/*   22:     */ import weka.core.xml.KOML;
/*   23:     */ import weka.core.xml.XMLOptions;
/*   24:     */ import weka.experiment.xml.XMLExperiment;
/*   25:     */ 
/*   26:     */ public class RemoteExperiment
/*   27:     */   extends Experiment
/*   28:     */ {
/*   29:     */   static final long serialVersionUID = -7357668825635314937L;
/*   30: 232 */   private final ArrayList<RemoteExperimentListener> m_listeners = new ArrayList();
/*   31: 235 */   protected DefaultListModel m_remoteHosts = new DefaultListModel();
/*   32: 238 */   private Queue m_remoteHostsQueue = new Queue();
/*   33:     */   private int[] m_remoteHostsStatus;
/*   34:     */   private int[] m_remoteHostFailureCounts;
/*   35:     */   protected static final int AVAILABLE = 0;
/*   36:     */   protected static final int IN_USE = 1;
/*   37:     */   protected static final int CONNECTION_FAILED = 2;
/*   38:     */   protected static final int SOME_OTHER_FAILURE = 3;
/*   39:     */   protected static final int MAX_FAILURES = 3;
/*   40: 270 */   private boolean m_experimentAborted = false;
/*   41:     */   private int m_removedHosts;
/*   42:     */   private int m_failedCount;
/*   43:     */   private int m_finishedCount;
/*   44: 284 */   private Experiment m_baseExperiment = null;
/*   45:     */   protected Experiment[] m_subExperiments;
/*   46: 290 */   private Queue m_subExpQueue = new Queue();
/*   47:     */   protected int[] m_subExpComplete;
/*   48: 296 */   protected boolean m_splitByDataSet = true;
/*   49: 299 */   protected boolean m_splitByProperty = false;
/*   50:     */   
/*   51:     */   public boolean getSplitByDataSet()
/*   52:     */   {
/*   53: 310 */     return this.m_splitByDataSet;
/*   54:     */   }
/*   55:     */   
/*   56:     */   public void setSplitByDataSet(boolean sd)
/*   57:     */   {
/*   58: 321 */     this.m_splitByDataSet = sd;
/*   59: 322 */     if (sd) {
/*   60: 323 */       this.m_splitByProperty = false;
/*   61:     */     }
/*   62:     */   }
/*   63:     */   
/*   64:     */   public boolean getSplitByProperty()
/*   65:     */   {
/*   66: 335 */     return this.m_splitByProperty;
/*   67:     */   }
/*   68:     */   
/*   69:     */   public void setSplitByProperty(boolean sd)
/*   70:     */   {
/*   71: 346 */     this.m_splitByProperty = sd;
/*   72: 347 */     if (sd) {
/*   73: 348 */       this.m_splitByDataSet = false;
/*   74:     */     }
/*   75:     */   }
/*   76:     */   
/*   77:     */   public RemoteExperiment()
/*   78:     */     throws Exception
/*   79:     */   {
/*   80: 359 */     this(new Experiment());
/*   81:     */   }
/*   82:     */   
/*   83:     */   public RemoteExperiment(Experiment base)
/*   84:     */     throws Exception
/*   85:     */   {
/*   86: 369 */     setBaseExperiment(base);
/*   87:     */   }
/*   88:     */   
/*   89:     */   public void addRemoteExperimentListener(RemoteExperimentListener r)
/*   90:     */   {
/*   91: 379 */     this.m_listeners.add(r);
/*   92:     */   }
/*   93:     */   
/*   94:     */   public Experiment getBaseExperiment()
/*   95:     */   {
/*   96: 388 */     return this.m_baseExperiment;
/*   97:     */   }
/*   98:     */   
/*   99:     */   public void setBaseExperiment(Experiment base)
/*  100:     */     throws Exception
/*  101:     */   {
/*  102: 399 */     if (base == null) {
/*  103: 400 */       throw new Exception("Base experiment is null!");
/*  104:     */     }
/*  105: 402 */     this.m_baseExperiment = base;
/*  106: 403 */     setRunLower(this.m_baseExperiment.getRunLower());
/*  107: 404 */     setRunUpper(this.m_baseExperiment.getRunUpper());
/*  108: 405 */     setResultListener(this.m_baseExperiment.getResultListener());
/*  109: 406 */     setResultProducer(this.m_baseExperiment.getResultProducer());
/*  110: 407 */     setDatasets(this.m_baseExperiment.getDatasets());
/*  111: 408 */     setUsePropertyIterator(this.m_baseExperiment.getUsePropertyIterator());
/*  112: 409 */     setPropertyPath(this.m_baseExperiment.getPropertyPath());
/*  113: 410 */     setPropertyArray(this.m_baseExperiment.getPropertyArray());
/*  114: 411 */     setNotes(this.m_baseExperiment.getNotes());
/*  115: 412 */     this.m_ClassFirst = this.m_baseExperiment.m_ClassFirst;
/*  116: 413 */     this.m_AdvanceDataSetFirst = this.m_baseExperiment.m_AdvanceDataSetFirst;
/*  117:     */   }
/*  118:     */   
/*  119:     */   public void setNotes(String newNotes)
/*  120:     */   {
/*  121: 424 */     super.setNotes(newNotes);
/*  122: 425 */     this.m_baseExperiment.setNotes(newNotes);
/*  123:     */   }
/*  124:     */   
/*  125:     */   public void setRunLower(int newRunLower)
/*  126:     */   {
/*  127: 436 */     super.setRunLower(newRunLower);
/*  128: 437 */     this.m_baseExperiment.setRunLower(newRunLower);
/*  129:     */   }
/*  130:     */   
/*  131:     */   public void setRunUpper(int newRunUpper)
/*  132:     */   {
/*  133: 448 */     super.setRunUpper(newRunUpper);
/*  134: 449 */     this.m_baseExperiment.setRunUpper(newRunUpper);
/*  135:     */   }
/*  136:     */   
/*  137:     */   public void setResultListener(ResultListener newResultListener)
/*  138:     */   {
/*  139: 460 */     super.setResultListener(newResultListener);
/*  140: 461 */     this.m_baseExperiment.setResultListener(newResultListener);
/*  141:     */   }
/*  142:     */   
/*  143:     */   public void setResultProducer(ResultProducer newResultProducer)
/*  144:     */   {
/*  145: 472 */     super.setResultProducer(newResultProducer);
/*  146: 473 */     this.m_baseExperiment.setResultProducer(newResultProducer);
/*  147:     */   }
/*  148:     */   
/*  149:     */   public void setDatasets(DefaultListModel ds)
/*  150:     */   {
/*  151: 483 */     super.setDatasets(ds);
/*  152: 484 */     this.m_baseExperiment.setDatasets(ds);
/*  153:     */   }
/*  154:     */   
/*  155:     */   public void setUsePropertyIterator(boolean newUsePropertyIterator)
/*  156:     */   {
/*  157: 495 */     super.setUsePropertyIterator(newUsePropertyIterator);
/*  158: 496 */     this.m_baseExperiment.setUsePropertyIterator(newUsePropertyIterator);
/*  159:     */   }
/*  160:     */   
/*  161:     */   public void setPropertyPath(PropertyNode[] newPropertyPath)
/*  162:     */   {
/*  163: 508 */     super.setPropertyPath(newPropertyPath);
/*  164: 509 */     this.m_baseExperiment.setPropertyPath(newPropertyPath);
/*  165:     */   }
/*  166:     */   
/*  167:     */   public void setPropertyArray(Object newPropArray)
/*  168:     */   {
/*  169: 520 */     super.setPropertyArray(newPropArray);
/*  170: 521 */     this.m_baseExperiment.setPropertyArray(newPropArray);
/*  171:     */   }
/*  172:     */   
/*  173:     */   public void initialize()
/*  174:     */     throws Exception
/*  175:     */   {
/*  176: 531 */     if (this.m_baseExperiment == null) {
/*  177: 532 */       throw new Exception("No base experiment specified!");
/*  178:     */     }
/*  179: 535 */     this.m_experimentAborted = false;
/*  180: 536 */     this.m_finishedCount = 0;
/*  181: 537 */     this.m_failedCount = 0;
/*  182: 538 */     this.m_RunNumber = getRunLower();
/*  183: 539 */     this.m_DatasetNumber = 0;
/*  184: 540 */     this.m_PropertyNumber = 0;
/*  185: 541 */     this.m_CurrentProperty = -1;
/*  186: 542 */     this.m_CurrentInstances = null;
/*  187: 543 */     this.m_Finished = false;
/*  188: 545 */     if (this.m_remoteHosts.size() == 0) {
/*  189: 546 */       throw new Exception("No hosts specified!");
/*  190:     */     }
/*  191: 549 */     this.m_remoteHostsStatus = new int[this.m_remoteHosts.size()];
/*  192: 550 */     this.m_remoteHostFailureCounts = new int[this.m_remoteHosts.size()];
/*  193:     */     
/*  194: 552 */     this.m_remoteHostsQueue = new Queue();
/*  195: 554 */     for (int i = 0; i < this.m_remoteHosts.size(); i++) {
/*  196: 555 */       this.m_remoteHostsQueue.push(new Integer(i));
/*  197:     */     }
/*  198: 559 */     this.m_subExpQueue = new Queue();
/*  199:     */     int numExps;
/*  200:     */     int numExps;
/*  201: 561 */     if (getSplitByDataSet())
/*  202:     */     {
/*  203: 562 */       numExps = this.m_baseExperiment.getDatasets().size();
/*  204:     */     }
/*  205:     */     else
/*  206:     */     {
/*  207:     */       int numExps;
/*  208: 563 */       if (getSplitByProperty()) {
/*  209: 564 */         numExps = this.m_baseExperiment.getPropertyArrayLength();
/*  210:     */       } else {
/*  211: 566 */         numExps = getRunUpper() - getRunLower() + 1;
/*  212:     */       }
/*  213:     */     }
/*  214: 568 */     this.m_subExperiments = new Experiment[numExps];
/*  215: 569 */     this.m_subExpComplete = new int[numExps];
/*  216:     */     
/*  217: 571 */     SerializedObject so = new SerializedObject(this.m_baseExperiment);
/*  218: 573 */     if (getSplitByDataSet()) {
/*  219: 574 */       for (int i = 0; i < this.m_baseExperiment.getDatasets().size(); i++)
/*  220:     */       {
/*  221: 575 */         this.m_subExperiments[i] = ((Experiment)so.getObject());
/*  222:     */         
/*  223: 577 */         DefaultListModel temp = new DefaultListModel();
/*  224: 578 */         temp.addElement(this.m_baseExperiment.getDatasets().get(i));
/*  225: 579 */         this.m_subExperiments[i].setDatasets(temp);
/*  226: 580 */         this.m_subExpQueue.push(new Integer(i));
/*  227:     */       }
/*  228: 582 */     } else if (getSplitByProperty()) {
/*  229: 583 */       for (int i = 0; i < this.m_baseExperiment.getPropertyArrayLength(); i++)
/*  230:     */       {
/*  231: 584 */         this.m_subExperiments[i] = ((Experiment)so.getObject());
/*  232: 585 */         Object[] a = new Object[1];
/*  233: 586 */         a[0] = this.m_baseExperiment.getPropertyArrayValue(i);
/*  234: 587 */         this.m_subExperiments[i].setPropertyArray(a);
/*  235: 588 */         this.m_subExpQueue.push(new Integer(i));
/*  236:     */       }
/*  237:     */     } else {
/*  238: 591 */       for (int i = getRunLower(); i <= getRunUpper(); i++)
/*  239:     */       {
/*  240: 592 */         this.m_subExperiments[(i - getRunLower())] = ((Experiment)so.getObject());
/*  241:     */         
/*  242: 594 */         this.m_subExperiments[(i - getRunLower())].setRunLower(i);
/*  243: 595 */         this.m_subExperiments[(i - getRunLower())].setRunUpper(i);
/*  244:     */         
/*  245: 597 */         this.m_subExpQueue.push(new Integer(i - getRunLower()));
/*  246:     */       }
/*  247:     */     }
/*  248:     */   }
/*  249:     */   
/*  250:     */   private synchronized void notifyListeners(boolean status, boolean log, boolean finished, String message)
/*  251:     */   {
/*  252: 612 */     if (this.m_listeners.size() > 0) {
/*  253: 613 */       for (int i = 0; i < this.m_listeners.size(); i++)
/*  254:     */       {
/*  255: 614 */         RemoteExperimentListener r = (RemoteExperimentListener)this.m_listeners.get(i);
/*  256: 615 */         r.remoteExperimentStatus(new RemoteExperimentEvent(status, log, finished, message));
/*  257:     */       }
/*  258:     */     } else {
/*  259: 619 */       System.err.println(message);
/*  260:     */     }
/*  261:     */   }
/*  262:     */   
/*  263:     */   public void abortExperiment()
/*  264:     */   {
/*  265: 627 */     this.m_experimentAborted = true;
/*  266:     */   }
/*  267:     */   
/*  268:     */   protected synchronized void incrementFinished()
/*  269:     */   {
/*  270: 634 */     this.m_finishedCount += 1;
/*  271:     */   }
/*  272:     */   
/*  273:     */   protected synchronized void incrementFailed(int hostNum)
/*  274:     */   {
/*  275: 644 */     this.m_failedCount += 1;
/*  276: 645 */     this.m_remoteHostFailureCounts[hostNum] += 1;
/*  277:     */   }
/*  278:     */   
/*  279:     */   protected synchronized void waitingExperiment(int expNum)
/*  280:     */   {
/*  281: 654 */     this.m_subExpQueue.push(new Integer(expNum));
/*  282:     */   }
/*  283:     */   
/*  284:     */   private boolean checkForAllFailedHosts()
/*  285:     */   {
/*  286: 663 */     boolean allbad = true;
/*  287: 664 */     for (int m_remoteHostsStatu : this.m_remoteHostsStatus) {
/*  288: 665 */       if (m_remoteHostsStatu != 2)
/*  289:     */       {
/*  290: 666 */         allbad = false;
/*  291: 667 */         break;
/*  292:     */       }
/*  293:     */     }
/*  294: 670 */     if (allbad)
/*  295:     */     {
/*  296: 671 */       abortExperiment();
/*  297: 672 */       notifyListeners(false, true, true, "Experiment aborted! All connections to remote hosts failed.");
/*  298:     */     }
/*  299: 675 */     return allbad;
/*  300:     */   }
/*  301:     */   
/*  302:     */   private String postExperimentInfo()
/*  303:     */   {
/*  304: 684 */     StringBuffer text = new StringBuffer();
/*  305: 685 */     text.append(this.m_finishedCount + (this.m_splitByDataSet ? " data sets" : " runs") + " completed successfully. " + this.m_failedCount + " failures during running.\n");
/*  306:     */     
/*  307:     */ 
/*  308: 688 */     System.err.print(text.toString());
/*  309: 689 */     return text.toString();
/*  310:     */   }
/*  311:     */   
/*  312:     */   protected synchronized void availableHost(int hostNum)
/*  313:     */   {
/*  314: 700 */     if (hostNum >= 0) {
/*  315: 701 */       if (this.m_remoteHostFailureCounts[hostNum] < 3)
/*  316:     */       {
/*  317: 702 */         this.m_remoteHostsQueue.push(new Integer(hostNum));
/*  318:     */       }
/*  319:     */       else
/*  320:     */       {
/*  321: 704 */         notifyListeners(false, true, false, "Max failures exceeded for host " + (String)this.m_remoteHosts.elementAt(hostNum) + ". Removed from host list.");
/*  322:     */         
/*  323:     */ 
/*  324: 707 */         this.m_removedHosts += 1;
/*  325:     */       }
/*  326:     */     }
/*  327: 713 */     if (this.m_failedCount == 3 * this.m_remoteHosts.size())
/*  328:     */     {
/*  329: 714 */       abortExperiment();
/*  330: 715 */       notifyListeners(false, true, true, "Experiment aborted! Max failures exceeded on all remote hosts.");
/*  331:     */       
/*  332: 717 */       return;
/*  333:     */     }
/*  334: 720 */     if (((getSplitByDataSet()) && (this.m_baseExperiment.getDatasets().size() == this.m_finishedCount)) || ((getSplitByProperty()) && (this.m_baseExperiment.getPropertyArrayLength() == this.m_finishedCount)) || ((!getSplitByDataSet()) && (!getSplitByProperty()) && (getRunUpper() - getRunLower() + 1 == this.m_finishedCount)))
/*  335:     */     {
/*  336: 723 */       notifyListeners(false, true, false, "Experiment completed successfully.");
/*  337: 724 */       notifyListeners(false, true, true, postExperimentInfo());
/*  338: 725 */       return;
/*  339:     */     }
/*  340: 728 */     if (checkForAllFailedHosts()) {
/*  341: 729 */       return;
/*  342:     */     }
/*  343: 732 */     if ((this.m_experimentAborted) && (this.m_remoteHostsQueue.size() + this.m_removedHosts == this.m_remoteHosts.size())) {
/*  344: 734 */       notifyListeners(false, true, true, "Experiment aborted. All remote tasks finished.");
/*  345:     */     }
/*  346: 738 */     if ((!this.m_subExpQueue.empty()) && (!this.m_experimentAborted) && 
/*  347: 739 */       (!this.m_remoteHostsQueue.empty())) {
/*  348:     */       try
/*  349:     */       {
/*  350: 742 */         int availHost = ((Integer)this.m_remoteHostsQueue.pop()).intValue();
/*  351: 743 */         int waitingExp = ((Integer)this.m_subExpQueue.pop()).intValue();
/*  352: 744 */         launchNext(waitingExp, availHost);
/*  353:     */       }
/*  354:     */       catch (Exception ex)
/*  355:     */       {
/*  356: 746 */         ex.printStackTrace();
/*  357:     */       }
/*  358:     */     }
/*  359:     */   }
/*  360:     */   
/*  361:     */   public void launchNext(final int wexp, final int ah)
/*  362:     */   {
/*  363: 761 */     Thread subExpThread = new Thread()
/*  364:     */     {
/*  365:     */       public void run()
/*  366:     */       {
/*  367: 764 */         RemoteExperiment.this.m_remoteHostsStatus[ah] = 1;
/*  368: 765 */         RemoteExperiment.this.m_subExpComplete[wexp] = 1;
/*  369: 766 */         RemoteExperimentSubTask expSubTsk = new RemoteExperimentSubTask();
/*  370: 767 */         expSubTsk.setExperiment(RemoteExperiment.this.m_subExperiments[wexp]);
/*  371: 768 */         String subTaskType = null;
/*  372: 769 */         if (RemoteExperiment.this.getSplitByDataSet()) {
/*  373: 770 */           subTaskType = "dataset: " + ((File)RemoteExperiment.this.m_subExperiments[wexp].getDatasets().elementAt(0)).getName();
/*  374: 773 */         } else if (RemoteExperiment.this.getSplitByProperty()) {
/*  375: 774 */           subTaskType = "property: " + RemoteExperiment.this.m_subExperiments[wexp].getPropertyArrayValue(0).getClass().getName() + " :" + RemoteExperiment.this.m_subExperiments[wexp].getPropertyArrayValue(0);
/*  376:     */         } else {
/*  377: 777 */           subTaskType = "run: " + RemoteExperiment.this.m_subExperiments[wexp].getRunLower();
/*  378:     */         }
/*  379:     */         try
/*  380:     */         {
/*  381: 780 */           String name = "//" + (String)RemoteExperiment.this.m_remoteHosts.elementAt(ah) + "/RemoteEngine";
/*  382:     */           
/*  383: 782 */           Compute comp = (Compute)Naming.lookup(name);
/*  384:     */           
/*  385: 784 */           RemoteExperiment.this.notifyListeners(false, true, false, "Starting " + subTaskType + " on host " + (String)RemoteExperiment.this.m_remoteHosts.elementAt(ah));
/*  386:     */           
/*  387: 786 */           Object subTaskId = comp.executeTask(expSubTsk);
/*  388: 787 */           boolean finished = false;
/*  389: 788 */           TaskStatusInfo is = null;
/*  390: 789 */           while (!finished) {
/*  391:     */             try
/*  392:     */             {
/*  393: 791 */               Thread.sleep(2000L);
/*  394:     */               
/*  395: 793 */               TaskStatusInfo cs = (TaskStatusInfo)comp.checkStatus(subTaskId);
/*  396: 794 */               if (cs.getExecutionStatus() == 3)
/*  397:     */               {
/*  398: 797 */                 RemoteExperiment.this.notifyListeners(false, true, false, cs.getStatusMessage());
/*  399: 798 */                 RemoteExperiment.this.m_remoteHostsStatus[ah] = 0;
/*  400: 799 */                 RemoteExperiment.this.incrementFinished();
/*  401: 800 */                 RemoteExperiment.this.availableHost(ah);
/*  402: 801 */                 finished = true;
/*  403:     */               }
/*  404: 802 */               else if (cs.getExecutionStatus() == 2)
/*  405:     */               {
/*  406: 806 */                 RemoteExperiment.this.notifyListeners(false, true, false, cs.getStatusMessage());
/*  407: 807 */                 RemoteExperiment.this.m_remoteHostsStatus[ah] = 3;
/*  408: 808 */                 RemoteExperiment.this.m_subExpComplete[wexp] = 2;
/*  409: 809 */                 RemoteExperiment.this.notifyListeners(false, true, false, subTaskType + " " + cs.getStatusMessage() + ". Scheduling for execution on another host.");
/*  410:     */                 
/*  411:     */ 
/*  412: 812 */                 RemoteExperiment.this.incrementFailed(ah);
/*  413:     */                 
/*  414: 814 */                 RemoteExperiment.this.waitingExperiment(wexp);
/*  415:     */                 
/*  416:     */ 
/*  417:     */ 
/*  418:     */ 
/*  419:     */ 
/*  420:     */ 
/*  421:     */ 
/*  422: 822 */                 RemoteExperiment.this.availableHost(ah);
/*  423: 823 */                 finished = true;
/*  424:     */               }
/*  425: 825 */               else if (is == null)
/*  426:     */               {
/*  427: 826 */                 is = cs;
/*  428: 827 */                 RemoteExperiment.this.notifyListeners(false, true, false, cs.getStatusMessage());
/*  429:     */               }
/*  430:     */               else
/*  431:     */               {
/*  432: 829 */                 if (cs.getStatusMessage().compareTo(is.getStatusMessage()) != 0) {
/*  433: 831 */                   RemoteExperiment.this.notifyListeners(false, true, false, cs.getStatusMessage());
/*  434:     */                 }
/*  435: 833 */                 is = cs;
/*  436:     */               }
/*  437:     */             }
/*  438:     */             catch (InterruptedException ie) {}
/*  439:     */           }
/*  440:     */         }
/*  441:     */         catch (Exception ce)
/*  442:     */         {
/*  443: 841 */           RemoteExperiment.this.m_remoteHostsStatus[ah] = 2;
/*  444: 842 */           RemoteExperiment.this.m_subExpComplete[wexp] = 0;
/*  445: 843 */           System.err.println(ce);
/*  446: 844 */           ce.printStackTrace();
/*  447: 845 */           RemoteExperiment.this.notifyListeners(false, true, false, "Connection to " + (String)RemoteExperiment.this.m_remoteHosts.elementAt(ah) + " failed. Scheduling " + subTaskType + " for execution on another host.");
/*  448:     */           
/*  449:     */ 
/*  450: 848 */           RemoteExperiment.this.checkForAllFailedHosts();
/*  451: 849 */           RemoteExperiment.this.waitingExperiment(wexp);
/*  452:     */         }
/*  453:     */         finally
/*  454:     */         {
/*  455: 851 */           if (isInterrupted()) {
/*  456: 852 */             System.err.println("Sub exp Interupted!");
/*  457:     */           }
/*  458:     */         }
/*  459:     */       }
/*  460: 856 */     };
/*  461: 857 */     subExpThread.setPriority(1);
/*  462: 858 */     subExpThread.start();
/*  463:     */   }
/*  464:     */   
/*  465:     */   public void nextIteration()
/*  466:     */     throws Exception
/*  467:     */   {}
/*  468:     */   
/*  469:     */   public void advanceCounters() {}
/*  470:     */   
/*  471:     */   public void postProcess() {}
/*  472:     */   
/*  473:     */   public void addRemoteHost(String hostname)
/*  474:     */   {
/*  475: 893 */     this.m_remoteHosts.addElement(hostname);
/*  476:     */   }
/*  477:     */   
/*  478:     */   public DefaultListModel getRemoteHosts()
/*  479:     */   {
/*  480: 902 */     return this.m_remoteHosts;
/*  481:     */   }
/*  482:     */   
/*  483:     */   public void setRemoteHosts(DefaultListModel list)
/*  484:     */   {
/*  485: 911 */     this.m_remoteHosts = list;
/*  486:     */   }
/*  487:     */   
/*  488:     */   public String toString()
/*  489:     */   {
/*  490: 921 */     String result = this.m_baseExperiment.toString();
/*  491:     */     
/*  492: 923 */     result = result + "\nRemote Hosts:\n";
/*  493: 924 */     for (int i = 0; i < this.m_remoteHosts.size(); i++) {
/*  494: 925 */       result = result + (String)this.m_remoteHosts.elementAt(i) + '\n';
/*  495:     */     }
/*  496: 927 */     return result;
/*  497:     */   }
/*  498:     */   
/*  499:     */   public void runExperiment()
/*  500:     */   {
/*  501: 935 */     int totalHosts = this.m_remoteHostsQueue.size();
/*  502: 937 */     for (int i = 0; i < totalHosts; i++) {
/*  503: 938 */       availableHost(-1);
/*  504:     */     }
/*  505:     */   }
/*  506:     */   
/*  507:     */   public String getRevision()
/*  508:     */   {
/*  509: 949 */     return RevisionUtils.extract("$Revision: 12590 $");
/*  510:     */   }
/*  511:     */   
/*  512:     */   public static void main(String[] args)
/*  513:     */   {
/*  514:     */     try
/*  515:     */     {
/*  516: 960 */       WekaPackageManager.loadPackages(false, true, false);
/*  517: 961 */       RemoteExperiment exp = null;
/*  518:     */       
/*  519:     */ 
/*  520: 964 */       String xmlOption = Utils.getOption("xml", args);
/*  521: 965 */       if (!xmlOption.equals("")) {
/*  522: 966 */         args = new XMLOptions(xmlOption).toArray();
/*  523:     */       }
/*  524: 969 */       Experiment base = null;
/*  525: 970 */       String expFile = Utils.getOption('l', args);
/*  526: 971 */       String saveFile = Utils.getOption('s', args);
/*  527: 972 */       boolean runExp = Utils.getFlag('r', args);
/*  528: 973 */       ArrayList<String> remoteHosts = new ArrayList();
/*  529: 974 */       String runHost = " ";
/*  530: 975 */       while (runHost.length() != 0)
/*  531:     */       {
/*  532: 976 */         runHost = Utils.getOption('h', args);
/*  533: 977 */         if (runHost.length() != 0) {
/*  534: 978 */           remoteHosts.add(runHost);
/*  535:     */         }
/*  536:     */       }
/*  537: 981 */       if (expFile.length() == 0)
/*  538:     */       {
/*  539: 982 */         base = new Experiment();
/*  540:     */         try
/*  541:     */         {
/*  542: 984 */           base.setOptions(args);
/*  543: 985 */           Utils.checkForRemainingOptions(args);
/*  544:     */         }
/*  545:     */         catch (Exception ex)
/*  546:     */         {
/*  547: 987 */           ex.printStackTrace();
/*  548: 988 */           String result = "Usage:\n\n-l <exp file>\n\tLoad experiment from file (default use cli options)\n-s <exp file>\n\tSave experiment to file after setting other options\n\t(default don't save)\n-h <remote host name>\n\tHost to run experiment on (may be specified more than once\n\tfor multiple remote hosts)\n-r \n\tRun experiment on (default don't run)\n-xml <filename | xml-string>\n\tget options from XML-Data instead from parameters\n\n";
/*  549:     */           
/*  550:     */ 
/*  551:     */ 
/*  552:     */ 
/*  553:     */ 
/*  554:     */ 
/*  555:     */ 
/*  556:     */ 
/*  557:     */ 
/*  558: 998 */           Enumeration<Option> enm = base.listOptions();
/*  559: 999 */           while (enm.hasMoreElements())
/*  560:     */           {
/*  561:1000 */             Option option = (Option)enm.nextElement();
/*  562:1001 */             result = result + option.synopsis() + "\n";
/*  563:1002 */             result = result + option.description() + "\n";
/*  564:     */           }
/*  565:1004 */           throw new Exception(result + "\n" + ex.getMessage());
/*  566:     */         }
/*  567:     */       }
/*  568:     */       else
/*  569:     */       {
/*  570:     */         Object tmp;
/*  571:     */         Object tmp;
/*  572:1010 */         if ((KOML.isPresent()) && (expFile.toLowerCase().endsWith(".koml")))
/*  573:     */         {
/*  574:1012 */           tmp = KOML.read(expFile);
/*  575:     */         }
/*  576:     */         else
/*  577:     */         {
/*  578:     */           Object tmp;
/*  579:1015 */           if (expFile.toLowerCase().endsWith(".xml"))
/*  580:     */           {
/*  581:1016 */             XMLExperiment xml = new XMLExperiment();
/*  582:1017 */             tmp = xml.read(expFile);
/*  583:     */           }
/*  584:     */           else
/*  585:     */           {
/*  586:1021 */             FileInputStream fi = new FileInputStream(expFile);
/*  587:1022 */             ObjectInputStream oi = new ObjectInputStream(new BufferedInputStream(fi));
/*  588:     */             
/*  589:1024 */             tmp = oi.readObject();
/*  590:1025 */             oi.close();
/*  591:     */           }
/*  592:     */         }
/*  593:1027 */         if ((tmp instanceof RemoteExperiment)) {
/*  594:1028 */           exp = (RemoteExperiment)tmp;
/*  595:     */         } else {
/*  596:1030 */           base = (Experiment)tmp;
/*  597:     */         }
/*  598:     */       }
/*  599:1033 */       if (base != null) {
/*  600:1034 */         exp = new RemoteExperiment(base);
/*  601:     */       }
/*  602:1036 */       for (int i = 0; i < remoteHosts.size(); i++) {
/*  603:1037 */         exp.addRemoteHost((String)remoteHosts.get(i));
/*  604:     */       }
/*  605:1039 */       System.err.println("Experiment:\n" + exp.toString());
/*  606:1041 */       if (saveFile.length() != 0) {
/*  607:1043 */         if ((KOML.isPresent()) && (saveFile.toLowerCase().endsWith(".koml")))
/*  608:     */         {
/*  609:1045 */           KOML.write(saveFile, exp);
/*  610:     */         }
/*  611:1048 */         else if (saveFile.toLowerCase().endsWith(".xml"))
/*  612:     */         {
/*  613:1049 */           XMLExperiment xml = new XMLExperiment();
/*  614:1050 */           xml.write(saveFile, exp);
/*  615:     */         }
/*  616:     */         else
/*  617:     */         {
/*  618:1054 */           FileOutputStream fo = new FileOutputStream(saveFile);
/*  619:1055 */           ObjectOutputStream oo = new ObjectOutputStream(new BufferedOutputStream(fo));
/*  620:     */           
/*  621:1057 */           oo.writeObject(exp);
/*  622:1058 */           oo.close();
/*  623:     */         }
/*  624:     */       }
/*  625:1062 */       if (runExp)
/*  626:     */       {
/*  627:1063 */         System.err.println("Initializing...");
/*  628:1064 */         exp.initialize();
/*  629:1065 */         System.err.println("Iterating...");
/*  630:1066 */         exp.runExperiment();
/*  631:1067 */         System.err.println("Postprocessing...");
/*  632:1068 */         exp.postProcess();
/*  633:     */       }
/*  634:     */     }
/*  635:     */     catch (Exception ex)
/*  636:     */     {
/*  637:1071 */       ex.printStackTrace();
/*  638:1072 */       System.err.println(ex.getMessage());
/*  639:     */     }
/*  640:     */   }
/*  641:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.RemoteExperiment
 * JD-Core Version:    0.7.0.1
 */