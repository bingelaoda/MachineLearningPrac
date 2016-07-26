/*    1:     */ package weka.gui.beans;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.awt.GraphicsEnvironment;
/*    5:     */ import java.beans.EventSetDescriptor;
/*    6:     */ import java.io.BufferedInputStream;
/*    7:     */ import java.io.BufferedOutputStream;
/*    8:     */ import java.io.File;
/*    9:     */ import java.io.FileInputStream;
/*   10:     */ import java.io.FileOutputStream;
/*   11:     */ import java.io.ObjectInputStream;
/*   12:     */ import java.io.ObjectOutputStream;
/*   13:     */ import java.io.PrintStream;
/*   14:     */ import java.io.Serializable;
/*   15:     */ import java.util.ArrayList;
/*   16:     */ import java.util.Date;
/*   17:     */ import java.util.Enumeration;
/*   18:     */ import java.util.HashMap;
/*   19:     */ import java.util.List;
/*   20:     */ import java.util.Map.Entry;
/*   21:     */ import java.util.Vector;
/*   22:     */ import java.util.concurrent.BlockingQueue;
/*   23:     */ import java.util.concurrent.LinkedBlockingQueue;
/*   24:     */ import java.util.concurrent.ThreadPoolExecutor;
/*   25:     */ import java.util.concurrent.TimeUnit;
/*   26:     */ import javax.swing.JCheckBox;
/*   27:     */ import javax.swing.JFileChooser;
/*   28:     */ import javax.swing.JOptionPane;
/*   29:     */ import javax.swing.JPanel;
/*   30:     */ import javax.swing.filechooser.FileFilter;
/*   31:     */ import weka.classifiers.AbstractClassifier;
/*   32:     */ import weka.classifiers.UpdateableBatchProcessor;
/*   33:     */ import weka.classifiers.UpdateableClassifier;
/*   34:     */ import weka.classifiers.misc.InputMappedClassifier;
/*   35:     */ import weka.classifiers.rules.ZeroR;
/*   36:     */ import weka.core.Attribute;
/*   37:     */ import weka.core.Drawable;
/*   38:     */ import weka.core.Environment;
/*   39:     */ import weka.core.EnvironmentHandler;
/*   40:     */ import weka.core.Instance;
/*   41:     */ import weka.core.Instances;
/*   42:     */ import weka.core.OptionHandler;
/*   43:     */ import weka.core.Utils;
/*   44:     */ import weka.core.xml.KOML;
/*   45:     */ import weka.core.xml.XStream;
/*   46:     */ import weka.experiment.Task;
/*   47:     */ import weka.experiment.TaskStatusInfo;
/*   48:     */ import weka.gui.ExtensionFileFilter;
/*   49:     */ import weka.gui.Logger;
/*   50:     */ 
/*   51:     */ public class Classifier
/*   52:     */   extends JPanel
/*   53:     */   implements BeanCommon, Visible, WekaWrapper, EventConstraints, Serializable, UserRequestAcceptor, TrainingSetListener, TestSetListener, InstanceListener, ConfigurationProducer, EnvironmentHandler
/*   54:     */ {
/*   55:     */   private static final long serialVersionUID = 659603893917736008L;
/*   56:  90 */   protected BeanVisual m_visual = new BeanVisual("Classifier", "weka/gui/beans/icons/DefaultClassifier.gif", "weka/gui/beans/icons/DefaultClassifier_animated.gif");
/*   57:  94 */   private static int IDLE = 0;
/*   58:  95 */   private static int BUILDING_MODEL = 1;
/*   59:  96 */   private int m_state = IDLE;
/*   60:     */   protected String m_globalInfo;
/*   61: 110 */   protected HashMap<String, List<Object>> m_listenees = new HashMap();
/*   62: 116 */   private final Vector<BatchClassifierListener> m_batchClassifierListeners = new Vector();
/*   63: 122 */   private final Vector<IncrementalClassifierListener> m_incrementalClassifierListeners = new Vector();
/*   64: 128 */   private final Vector<GraphListener> m_graphListeners = new Vector();
/*   65: 134 */   private final Vector<TextListener> m_textListeners = new Vector();
/*   66:     */   private Instances m_trainingSet;
/*   67: 143 */   private weka.classifiers.Classifier m_Classifier = new ZeroR();
/*   68: 145 */   private weka.classifiers.Classifier m_ClassifierTemplate = this.m_Classifier;
/*   69: 147 */   private final IncrementalClassifierEvent m_ie = new IncrementalClassifierEvent(this);
/*   70:     */   public static final String FILE_EXTENSION = "model";
/*   71: 153 */   private transient JFileChooser m_fileChooser = null;
/*   72: 155 */   protected FileFilter m_binaryFilter = new ExtensionFileFilter(".model", "Binary serialized model file (*model)");
/*   73: 158 */   protected FileFilter m_KOMLFilter = new ExtensionFileFilter(".komlmodel", "XML serialized model file (*.komlmodel)");
/*   74: 162 */   protected FileFilter m_XStreamFilter = new ExtensionFileFilter(".xstreammodel", "XML serialized model file (*.xstreammodel)");
/*   75:     */   protected transient Environment m_env;
/*   76: 175 */   private boolean m_resetIncrementalClassifier = false;
/*   77: 183 */   private boolean m_updateIncrementalClassifier = true;
/*   78: 185 */   private transient Logger m_log = null;
/*   79:     */   private InstanceEvent m_incrementalEvent;
/*   80: 195 */   protected int m_executionSlots = 2;
/*   81:     */   protected transient ThreadPoolExecutor m_executorPool;
/*   82:     */   protected transient BatchClassifierEvent[][] m_outputQueues;
/*   83:     */   protected transient boolean[][] m_completedSets;
/*   84:     */   protected transient Date m_currentBatchIdentifier;
/*   85: 218 */   protected transient boolean m_batchStarted = false;
/*   86: 223 */   protected String m_oldText = "";
/*   87: 230 */   protected boolean m_reject = false;
/*   88: 236 */   protected boolean m_block = false;
/*   89: 243 */   protected String m_loadModelFileName = "";
/*   90:     */   protected transient StreamThroughput m_throughput;
/*   91:     */   
/*   92:     */   public String globalInfo()
/*   93:     */   {
/*   94: 251 */     return this.m_globalInfo;
/*   95:     */   }
/*   96:     */   
/*   97:     */   public Classifier()
/*   98:     */   {
/*   99: 258 */     setLayout(new BorderLayout());
/*  100: 259 */     add(this.m_visual, "Center");
/*  101: 260 */     setClassifierTemplate(this.m_ClassifierTemplate);
/*  102:     */   }
/*  103:     */   
/*  104:     */   private void startExecutorPool()
/*  105:     */   {
/*  106: 267 */     if (this.m_executorPool != null) {
/*  107: 268 */       this.m_executorPool.shutdownNow();
/*  108:     */     }
/*  109: 271 */     this.m_executorPool = new ThreadPoolExecutor(this.m_executionSlots, this.m_executionSlots, 120L, TimeUnit.SECONDS, new LinkedBlockingQueue());
/*  110:     */   }
/*  111:     */   
/*  112:     */   public void setCustomName(String name)
/*  113:     */   {
/*  114: 283 */     this.m_visual.setText(name);
/*  115:     */   }
/*  116:     */   
/*  117:     */   public String getCustomName()
/*  118:     */   {
/*  119: 293 */     return this.m_visual.getText();
/*  120:     */   }
/*  121:     */   
/*  122:     */   protected void setupFileChooser()
/*  123:     */   {
/*  124: 297 */     if (this.m_fileChooser == null) {
/*  125: 298 */       this.m_fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
/*  126:     */     }
/*  127: 302 */     this.m_fileChooser.addChoosableFileFilter(this.m_binaryFilter);
/*  128: 303 */     if (KOML.isPresent()) {
/*  129: 304 */       this.m_fileChooser.addChoosableFileFilter(this.m_KOMLFilter);
/*  130:     */     }
/*  131: 306 */     if (XStream.isPresent()) {
/*  132: 307 */       this.m_fileChooser.addChoosableFileFilter(this.m_XStreamFilter);
/*  133:     */     }
/*  134: 309 */     this.m_fileChooser.setFileFilter(this.m_binaryFilter);
/*  135:     */   }
/*  136:     */   
/*  137:     */   public int getExecutionSlots()
/*  138:     */   {
/*  139: 318 */     return this.m_executionSlots;
/*  140:     */   }
/*  141:     */   
/*  142:     */   public void setExecutionSlots(int slots)
/*  143:     */   {
/*  144: 327 */     this.m_executionSlots = slots;
/*  145:     */   }
/*  146:     */   
/*  147:     */   public void setBlockOnLastFold(boolean block)
/*  148:     */   {
/*  149: 337 */     this.m_block = block;
/*  150:     */   }
/*  151:     */   
/*  152:     */   public boolean getBlockOnLastFold()
/*  153:     */   {
/*  154: 347 */     return this.m_block;
/*  155:     */   }
/*  156:     */   
/*  157:     */   public void setClassifierTemplate(weka.classifiers.Classifier c)
/*  158:     */   {
/*  159: 356 */     boolean loadImages = true;
/*  160: 357 */     if (c.getClass().getName().compareTo(this.m_ClassifierTemplate.getClass().getName()) == 0) {
/*  161: 359 */       loadImages = false;
/*  162:     */     } else {
/*  163: 363 */       this.m_trainingSet = null;
/*  164:     */     }
/*  165: 365 */     this.m_ClassifierTemplate = c;
/*  166: 366 */     String classifierName = c.getClass().toString();
/*  167: 367 */     classifierName = classifierName.substring(classifierName.lastIndexOf('.') + 1, classifierName.length());
/*  168: 370 */     if (loadImages)
/*  169:     */     {
/*  170: 371 */       if (!this.m_visual.loadIcons("weka/gui/beans/icons/" + classifierName + ".gif", "weka/gui/beans/icons/" + classifierName + "_animated.gif")) {
/*  171: 373 */         useDefaultVisual();
/*  172:     */       }
/*  173: 375 */       this.m_visual.setText(classifierName);
/*  174:     */     }
/*  175: 378 */     if ((!(this.m_ClassifierTemplate instanceof UpdateableClassifier)) && (this.m_listenees.containsKey("instance"))) {
/*  176: 380 */       if (this.m_log != null) {
/*  177: 381 */         this.m_log.logMessage("[Classifier] " + statusMessagePrefix() + " WARNING : " + getCustomName() + " is not an incremental classifier");
/*  178:     */       }
/*  179:     */     }
/*  180: 387 */     this.m_globalInfo = KnowledgeFlowApp.getGlobalInfo(this.m_ClassifierTemplate);
/*  181:     */     try
/*  182:     */     {
/*  183: 390 */       if ((this.m_ClassifierTemplate instanceof InputMappedClassifier)) {
/*  184: 391 */         this.m_Classifier = AbstractClassifier.makeCopy(this.m_ClassifierTemplate);
/*  185:     */       }
/*  186:     */     }
/*  187:     */     catch (Exception e)
/*  188:     */     {
/*  189: 396 */       e.printStackTrace();
/*  190:     */     }
/*  191:     */   }
/*  192:     */   
/*  193:     */   public weka.classifiers.Classifier getClassifierTemplate()
/*  194:     */   {
/*  195: 406 */     return this.m_ClassifierTemplate;
/*  196:     */   }
/*  197:     */   
/*  198:     */   private void setTrainedClassifier(weka.classifiers.Classifier tc)
/*  199:     */     throws Exception
/*  200:     */   {
/*  201: 413 */     weka.classifiers.Classifier newTemplate = null;
/*  202: 414 */     String[] options = ((OptionHandler)tc).getOptions();
/*  203: 415 */     newTemplate = AbstractClassifier.forName(tc.getClass().getName(), options);
/*  204: 419 */     if (!newTemplate.getClass().equals(this.m_ClassifierTemplate.getClass())) {
/*  205: 420 */       throw new Exception("Classifier model " + tc.getClass().getName() + " is not the same type " + "of classifier as this one (" + this.m_ClassifierTemplate.getClass().getName() + ")");
/*  206:     */     }
/*  207: 425 */     setClassifierTemplate(newTemplate);
/*  208:     */     
/*  209: 427 */     this.m_Classifier = tc;
/*  210:     */   }
/*  211:     */   
/*  212:     */   public boolean hasIncomingStreamInstances()
/*  213:     */   {
/*  214: 437 */     if (this.m_listenees.size() == 0) {
/*  215: 438 */       return false;
/*  216:     */     }
/*  217: 440 */     if (this.m_listenees.containsKey("instance")) {
/*  218: 441 */       return true;
/*  219:     */     }
/*  220: 443 */     return false;
/*  221:     */   }
/*  222:     */   
/*  223:     */   public boolean hasIncomingBatchInstances()
/*  224:     */   {
/*  225: 453 */     if (this.m_listenees.size() == 0) {
/*  226: 454 */       return false;
/*  227:     */     }
/*  228: 456 */     if ((this.m_listenees.containsKey("trainingSet")) || (this.m_listenees.containsKey("testSet"))) {
/*  229: 458 */       return true;
/*  230:     */     }
/*  231: 460 */     return false;
/*  232:     */   }
/*  233:     */   
/*  234:     */   public weka.classifiers.Classifier getClassifier()
/*  235:     */   {
/*  236: 469 */     return this.m_Classifier;
/*  237:     */   }
/*  238:     */   
/*  239:     */   public void setWrappedAlgorithm(Object algorithm)
/*  240:     */   {
/*  241: 481 */     if (!(algorithm instanceof weka.classifiers.Classifier)) {
/*  242: 482 */       throw new IllegalArgumentException(algorithm.getClass() + " : incorrect " + "type of algorithm (Classifier)");
/*  243:     */     }
/*  244: 485 */     setClassifierTemplate((weka.classifiers.Classifier)algorithm);
/*  245:     */   }
/*  246:     */   
/*  247:     */   public Object getWrappedAlgorithm()
/*  248:     */   {
/*  249: 495 */     return getClassifierTemplate();
/*  250:     */   }
/*  251:     */   
/*  252:     */   public void setLoadClassifierFileName(String filename)
/*  253:     */   {
/*  254: 506 */     this.m_loadModelFileName = filename;
/*  255:     */   }
/*  256:     */   
/*  257:     */   public String getLoadClassifierFileName()
/*  258:     */   {
/*  259: 517 */     return this.m_loadModelFileName;
/*  260:     */   }
/*  261:     */   
/*  262:     */   public void setResetIncrementalClassifier(boolean reset)
/*  263:     */   {
/*  264: 531 */     this.m_resetIncrementalClassifier = reset;
/*  265:     */   }
/*  266:     */   
/*  267:     */   public boolean getResetIncrementalClassifier()
/*  268:     */   {
/*  269: 545 */     return this.m_resetIncrementalClassifier;
/*  270:     */   }
/*  271:     */   
/*  272:     */   public boolean getUpdateIncrementalClassifier()
/*  273:     */   {
/*  274: 555 */     return this.m_updateIncrementalClassifier;
/*  275:     */   }
/*  276:     */   
/*  277:     */   public void setUpdateIncrementalClassifier(boolean update)
/*  278:     */   {
/*  279: 565 */     this.m_updateIncrementalClassifier = update;
/*  280:     */   }
/*  281:     */   
/*  282:     */   public void acceptInstance(InstanceEvent e)
/*  283:     */   {
/*  284: 575 */     if (this.m_log == null) {
/*  285: 576 */       System.err.println("Log is null");
/*  286:     */     }
/*  287: 578 */     this.m_incrementalEvent = e;
/*  288: 579 */     handleIncrementalEvent();
/*  289:     */   }
/*  290:     */   
/*  291:     */   private void handleIncrementalEvent()
/*  292:     */   {
/*  293: 588 */     if ((this.m_executorPool != null) && ((this.m_executorPool.getQueue().size() > 0) || (this.m_executorPool.getActiveCount() > 0)))
/*  294:     */     {
/*  295: 592 */       String messg = "[Classifier] " + statusMessagePrefix() + " is currently batch training!";
/*  296: 595 */       if (this.m_log != null)
/*  297:     */       {
/*  298: 596 */         this.m_log.logMessage(messg);
/*  299: 597 */         this.m_log.statusMessage(statusMessagePrefix() + "WARNING: " + "Can't accept instance - batch training in progress.");
/*  300:     */       }
/*  301:     */       else
/*  302:     */       {
/*  303: 600 */         System.err.println(messg);
/*  304:     */       }
/*  305: 602 */       return;
/*  306:     */     }
/*  307: 605 */     if (this.m_incrementalEvent.getStatus() == 0)
/*  308:     */     {
/*  309: 606 */       this.m_throughput = new StreamThroughput(statusMessagePrefix());
/*  310: 609 */       if (this.m_log != null) {
/*  311: 610 */         this.m_log.statusMessage(statusMessagePrefix() + "remove");
/*  312:     */       }
/*  313: 614 */       Instances dataset = this.m_incrementalEvent.getStructure();
/*  314: 616 */       if (dataset.classIndex() < 0)
/*  315:     */       {
/*  316: 617 */         stop();
/*  317: 618 */         String errorMessage = statusMessagePrefix() + "ERROR: no class attribute set in incoming stream!";
/*  318: 621 */         if (this.m_log != null)
/*  319:     */         {
/*  320: 622 */           this.m_log.statusMessage(errorMessage);
/*  321: 623 */           this.m_log.logMessage("[" + getCustomName() + "] " + errorMessage);
/*  322:     */         }
/*  323:     */         else
/*  324:     */         {
/*  325: 625 */           System.err.println("[" + getCustomName() + "] " + errorMessage);
/*  326:     */         }
/*  327: 627 */         return;
/*  328:     */       }
/*  329: 633 */       if ((this.m_loadModelFileName != null) && (this.m_loadModelFileName.length() > 0) && (this.m_state == IDLE) && (!this.m_listenees.containsKey("trainingSet")))
/*  330:     */       {
/*  331: 637 */         String resolvedFileName = this.m_loadModelFileName;
/*  332: 638 */         if (this.m_env != null) {
/*  333:     */           try
/*  334:     */           {
/*  335: 640 */             resolvedFileName = this.m_env.substitute(resolvedFileName);
/*  336:     */           }
/*  337:     */           catch (Exception ex) {}
/*  338:     */         }
/*  339: 644 */         File loadFrom = new File(resolvedFileName);
/*  340:     */         try
/*  341:     */         {
/*  342: 646 */           loadFromFile(loadFrom);
/*  343:     */         }
/*  344:     */         catch (Exception ex)
/*  345:     */         {
/*  346: 649 */           this.m_log.statusMessage(statusMessagePrefix() + "WARNING: unable to load " + "model (see log).");
/*  347:     */           
/*  348: 651 */           this.m_log.logMessage("[Classifier] " + statusMessagePrefix() + "Problem loading classifier - training from scratch... " + ex.getMessage());
/*  349:     */         }
/*  350:     */       }
/*  351:     */       try
/*  352:     */       {
/*  353: 662 */         if ((this.m_trainingSet == null) || (!this.m_trainingSet.equalHeaders(dataset)) || (this.m_resetIncrementalClassifier))
/*  354:     */         {
/*  355: 664 */           if ((!(this.m_ClassifierTemplate instanceof UpdateableClassifier)) && (!(this.m_ClassifierTemplate instanceof InputMappedClassifier)))
/*  356:     */           {
/*  357: 666 */             stop();
/*  358: 667 */             if (this.m_log != null)
/*  359:     */             {
/*  360: 668 */               String msg = statusMessagePrefix() + "ERROR: instance event's structure is different from " + "the data that " + "was used to batch train this classifier; can't continue.";
/*  361:     */               
/*  362:     */ 
/*  363:     */ 
/*  364:     */ 
/*  365:     */ 
/*  366:     */ 
/*  367:     */ 
/*  368: 676 */               this.m_log.logMessage("[Classifier] " + msg);
/*  369: 677 */               this.m_log.statusMessage(msg);
/*  370:     */             }
/*  371: 679 */             return;
/*  372:     */           }
/*  373: 682 */           if ((this.m_ClassifierTemplate instanceof InputMappedClassifier)) {
/*  374: 683 */             this.m_trainingSet = ((InputMappedClassifier)this.m_Classifier).getModelHeader(this.m_trainingSet);
/*  375:     */           }
/*  376: 698 */           if ((this.m_trainingSet != null) && (!dataset.equalHeaders(this.m_trainingSet)))
/*  377:     */           {
/*  378: 699 */             if (this.m_log != null)
/*  379:     */             {
/*  380: 700 */               String msg = statusMessagePrefix() + " WARNING : structure of instance events differ " + "from data used in batch training this " + "classifier. Resetting classifier...";
/*  381:     */               
/*  382:     */ 
/*  383:     */ 
/*  384:     */ 
/*  385: 705 */               this.m_log.logMessage("[Classifier] " + msg);
/*  386: 706 */               this.m_log.statusMessage(msg);
/*  387:     */             }
/*  388: 708 */             this.m_trainingSet = null;
/*  389:     */           }
/*  390: 711 */           if (this.m_resetIncrementalClassifier)
/*  391:     */           {
/*  392: 712 */             if (this.m_log != null)
/*  393:     */             {
/*  394: 713 */               String msg = statusMessagePrefix() + " Reseting incremental classifier";
/*  395:     */               
/*  396: 715 */               this.m_log.logMessage("[Classifier] " + msg);
/*  397: 716 */               this.m_log.statusMessage(msg);
/*  398:     */             }
/*  399: 719 */             this.m_trainingSet = null;
/*  400:     */           }
/*  401: 722 */           if (this.m_trainingSet == null)
/*  402:     */           {
/*  403: 724 */             this.m_trainingSet = new Instances(dataset, 0);
/*  404: 725 */             this.m_Classifier = AbstractClassifier.makeCopy(this.m_ClassifierTemplate);
/*  405: 728 */             if (((this.m_Classifier instanceof EnvironmentHandler)) && (this.m_env != null)) {
/*  406: 729 */               ((EnvironmentHandler)this.m_Classifier).setEnvironment(this.m_env);
/*  407:     */             }
/*  408: 731 */             this.m_Classifier.buildClassifier(this.m_trainingSet);
/*  409:     */           }
/*  410:     */         }
/*  411:     */       }
/*  412:     */       catch (Exception ex)
/*  413:     */       {
/*  414: 735 */         stop();
/*  415: 736 */         if (this.m_log != null)
/*  416:     */         {
/*  417: 737 */           this.m_log.statusMessage(statusMessagePrefix() + "ERROR (See log for details)");
/*  418:     */           
/*  419: 739 */           this.m_log.logMessage("[Classifier] " + statusMessagePrefix() + " problem during incremental processing. " + ex.getMessage());
/*  420:     */         }
/*  421: 742 */         ex.printStackTrace();
/*  422: 743 */         return;
/*  423:     */       }
/*  424: 746 */       if (!this.m_incrementalEvent.m_formatNotificationOnly)
/*  425:     */       {
/*  426: 747 */         String msg = statusMessagePrefix() + "Predicting incrementally...";
/*  427: 751 */         if (this.m_log != null) {
/*  428: 752 */           this.m_log.statusMessage(msg);
/*  429:     */         }
/*  430:     */       }
/*  431: 756 */       System.err.println("NOTIFYING NEW BATCH");
/*  432: 757 */       this.m_ie.setStructure(dataset);
/*  433: 758 */       this.m_ie.setClassifier(this.m_Classifier);
/*  434:     */       
/*  435: 760 */       notifyIncrementalClassifierListeners(this.m_ie);
/*  436: 761 */       return;
/*  437:     */     }
/*  438: 763 */     if (this.m_trainingSet == null) {
/*  439: 768 */       return;
/*  440:     */     }
/*  441:     */     try
/*  442:     */     {
/*  443: 774 */       if ((this.m_incrementalEvent.getInstance() != null) && 
/*  444: 775 */         (this.m_incrementalEvent.getInstance().dataset().classIndex() < 0)) {
/*  445: 777 */         this.m_incrementalEvent.getInstance().dataset().setClassIndex(this.m_incrementalEvent.getInstance().dataset().numAttributes() - 1);
/*  446:     */       }
/*  447: 785 */       int status = 1;
/*  448: 792 */       if ((this.m_incrementalEvent.getStatus() == 2) || (this.m_incrementalEvent.getInstance() == null)) {
/*  449: 794 */         status = 2;
/*  450:     */       }
/*  451: 797 */       if (this.m_incrementalEvent.getInstance() != null) {
/*  452: 798 */         this.m_throughput.updateStart();
/*  453:     */       }
/*  454: 801 */       this.m_ie.setStatus(status);
/*  455: 802 */       this.m_ie.setClassifier(this.m_Classifier);
/*  456: 803 */       this.m_ie.setCurrentInstance(this.m_incrementalEvent.getInstance());
/*  457: 804 */       if ((status == 2) && ((this.m_Classifier instanceof UpdateableBatchProcessor))) {
/*  458: 806 */         ((UpdateableBatchProcessor)this.m_Classifier).batchFinished();
/*  459:     */       }
/*  460: 809 */       notifyIncrementalClassifierListeners(this.m_ie);
/*  461: 814 */       if (((this.m_ClassifierTemplate instanceof UpdateableClassifier)) && (this.m_updateIncrementalClassifier == true) && (this.m_incrementalEvent.getInstance() != null) && (!this.m_incrementalEvent.getInstance().isMissing(this.m_incrementalEvent.getInstance().dataset().classIndex()))) {
/*  462: 819 */         ((UpdateableClassifier)this.m_Classifier).updateClassifier(this.m_incrementalEvent.getInstance());
/*  463:     */       }
/*  464: 823 */       if (this.m_incrementalEvent.getInstance() != null) {
/*  465: 824 */         this.m_throughput.updateEnd(this.m_log);
/*  466:     */       }
/*  467: 827 */       if ((this.m_incrementalEvent.getStatus() == 2) || (this.m_incrementalEvent.getInstance() == null))
/*  468:     */       {
/*  469: 829 */         if (this.m_textListeners.size() > 0)
/*  470:     */         {
/*  471: 830 */           String modelString = this.m_Classifier.toString();
/*  472: 831 */           String titleString = this.m_Classifier.getClass().getName();
/*  473:     */           
/*  474: 833 */           titleString = titleString.substring(titleString.lastIndexOf('.') + 1, titleString.length());
/*  475:     */           
/*  476:     */ 
/*  477: 836 */           modelString = "=== Classifier model ===\n\nScheme:   " + titleString + "\n" + "Relation: " + this.m_trainingSet.relationName() + "\n\n" + modelString;
/*  478:     */           
/*  479:     */ 
/*  480:     */ 
/*  481: 840 */           titleString = "Model: " + titleString;
/*  482: 841 */           TextEvent nt = new TextEvent(this, modelString, titleString);
/*  483: 842 */           notifyTextListeners(nt);
/*  484:     */         }
/*  485: 845 */         this.m_throughput.finished(this.m_log);
/*  486:     */       }
/*  487:     */     }
/*  488:     */     catch (Exception ex)
/*  489:     */     {
/*  490: 848 */       stop();
/*  491: 849 */       if (this.m_log != null)
/*  492:     */       {
/*  493: 850 */         this.m_log.logMessage("[Classifier] " + statusMessagePrefix() + ex.getMessage());
/*  494:     */         
/*  495: 852 */         this.m_log.statusMessage(statusMessagePrefix() + "ERROR (see log for details)");
/*  496:     */         
/*  497: 854 */         ex.printStackTrace();
/*  498:     */       }
/*  499:     */       else
/*  500:     */       {
/*  501: 856 */         ex.printStackTrace();
/*  502:     */       }
/*  503:     */     }
/*  504:     */   }
/*  505:     */   
/*  506:     */   protected class TrainingTask
/*  507:     */     implements Runnable, Task
/*  508:     */   {
/*  509:     */     private static final long serialVersionUID = -7918128680624169641L;
/*  510:     */     private final int m_runNum;
/*  511:     */     private final int m_maxRunNum;
/*  512:     */     private final int m_setNum;
/*  513:     */     private final int m_maxSetNum;
/*  514: 870 */     private Instances m_train = null;
/*  515: 871 */     private final TaskStatusInfo m_taskInfo = new TaskStatusInfo();
/*  516:     */     
/*  517:     */     public TrainingTask(int runNum, int maxRunNum, int setNum, int maxSetNum, Instances train)
/*  518:     */     {
/*  519: 875 */       this.m_runNum = runNum;
/*  520: 876 */       this.m_maxRunNum = maxRunNum;
/*  521: 877 */       this.m_setNum = setNum;
/*  522: 878 */       this.m_maxSetNum = maxSetNum;
/*  523: 879 */       this.m_train = train;
/*  524: 880 */       this.m_taskInfo.setExecutionStatus(0);
/*  525:     */     }
/*  526:     */     
/*  527:     */     public void run()
/*  528:     */     {
/*  529: 885 */       execute();
/*  530:     */     }
/*  531:     */     
/*  532:     */     public void execute()
/*  533:     */     {
/*  534:     */       try
/*  535:     */       {
/*  536: 892 */         if (this.m_train != null)
/*  537:     */         {
/*  538: 893 */           if (this.m_train.classIndex() < 0)
/*  539:     */           {
/*  540: 895 */             Classifier.this.stop();
/*  541: 896 */             String errorMessage = Classifier.this.statusMessagePrefix() + "ERROR: no class attribute set in test data!";
/*  542: 899 */             if (Classifier.this.m_log != null)
/*  543:     */             {
/*  544: 900 */               Classifier.this.m_log.statusMessage(errorMessage);
/*  545: 901 */               Classifier.this.m_log.logMessage("[Classifier] " + errorMessage);
/*  546:     */             }
/*  547:     */             else
/*  548:     */             {
/*  549: 903 */               System.err.println("[Classifier] " + errorMessage);
/*  550:     */             }
/*  551:     */             String titleString;
/*  552:     */             return;
/*  553:     */           }
/*  554: 914 */           if ((this.m_runNum == 1) && (this.m_setNum == 1))
/*  555:     */           {
/*  556: 917 */             Classifier.this.m_state = Classifier.BUILDING_MODEL;
/*  557:     */             
/*  558:     */ 
/*  559: 920 */             this.m_taskInfo.setExecutionStatus(1);
/*  560:     */           }
/*  561: 925 */           String msg = Classifier.this.statusMessagePrefix() + "Building model for run " + this.m_runNum + " fold " + this.m_setNum;
/*  562: 928 */           if (Classifier.this.m_log != null) {
/*  563: 929 */             Classifier.this.m_log.statusMessage(msg);
/*  564:     */           } else {
/*  565: 931 */             System.err.println(msg);
/*  566:     */           }
/*  567: 936 */           weka.classifiers.Classifier classifierCopy = AbstractClassifier.makeCopy(Classifier.this.m_ClassifierTemplate);
/*  568: 938 */           if (((classifierCopy instanceof EnvironmentHandler)) && (Classifier.this.m_env != null)) {
/*  569: 939 */             ((EnvironmentHandler)classifierCopy).setEnvironment(Classifier.this.m_env);
/*  570:     */           }
/*  571: 943 */           classifierCopy.buildClassifier(this.m_train);
/*  572: 944 */           if ((this.m_runNum == this.m_maxRunNum) && (this.m_setNum == this.m_maxSetNum))
/*  573:     */           {
/*  574: 947 */             Classifier.this.m_Classifier = classifierCopy;
/*  575: 948 */             Classifier.this.m_trainingSet = new Instances(this.m_train, 0);
/*  576:     */           }
/*  577: 954 */           BatchClassifierEvent ce = new BatchClassifierEvent(Classifier.this, classifierCopy, new DataSetEvent(this, this.m_train), null, this.m_setNum, this.m_maxSetNum);
/*  578:     */           
/*  579:     */ 
/*  580:     */ 
/*  581:     */ 
/*  582:     */ 
/*  583: 960 */           ce.setGroupIdentifier(Classifier.this.m_currentBatchIdentifier.getTime());
/*  584: 961 */           ce.setLabel(Classifier.this.getCustomName());
/*  585: 962 */           Classifier.this.notifyBatchClassifierListeners(ce);
/*  586:     */           
/*  587:     */ 
/*  588: 965 */           ce = new BatchClassifierEvent(Classifier.this, classifierCopy, new DataSetEvent(this, this.m_train), null, this.m_setNum, this.m_maxSetNum);
/*  589:     */           
/*  590:     */ 
/*  591:     */ 
/*  592: 969 */           ce.setGroupIdentifier(Classifier.this.m_currentBatchIdentifier.getTime());
/*  593: 970 */           ce.setLabel(Classifier.this.getCustomName());
/*  594: 971 */           Classifier.this.classifierTrainingComplete(ce);
/*  595: 974 */           if (((classifierCopy instanceof Drawable)) && (Classifier.this.m_graphListeners.size() > 0))
/*  596:     */           {
/*  597: 976 */             String grphString = ((Drawable)classifierCopy).graph();
/*  598: 977 */             int grphType = ((Drawable)classifierCopy).graphType();
/*  599: 978 */             String grphTitle = classifierCopy.getClass().getName();
/*  600: 979 */             grphTitle = grphTitle.substring(grphTitle.lastIndexOf('.') + 1, grphTitle.length());
/*  601:     */             
/*  602:     */ 
/*  603: 982 */             grphTitle = "Set " + this.m_setNum + " (" + this.m_train.relationName() + ") " + grphTitle;
/*  604:     */             
/*  605:     */ 
/*  606:     */ 
/*  607: 986 */             GraphEvent ge = new GraphEvent(Classifier.this, grphString, grphTitle, grphType);
/*  608:     */             
/*  609: 988 */             Classifier.this.notifyGraphListeners(ge);
/*  610:     */           }
/*  611: 991 */           if (Classifier.this.m_textListeners.size() > 0)
/*  612:     */           {
/*  613: 992 */             String modelString = classifierCopy.toString();
/*  614: 993 */             String titleString = classifierCopy.getClass().getName();
/*  615:     */             
/*  616: 995 */             titleString = titleString.substring(titleString.lastIndexOf('.') + 1, titleString.length());
/*  617:     */             
/*  618:     */ 
/*  619:     */ 
/*  620: 999 */             modelString = "=== Classifier model ===\n\nScheme:   " + titleString + "\n" + "Relation: " + this.m_train.relationName() + (this.m_maxSetNum > 1 ? "\nTraining Fold: " + this.m_setNum : "") + "\n\n" + modelString;
/*  621:     */             
/*  622:     */ 
/*  623:     */ 
/*  624:     */ 
/*  625:1004 */             titleString = "Model: " + titleString;
/*  626:     */             
/*  627:1006 */             TextEvent nt = new TextEvent(Classifier.this, modelString, titleString + (this.m_maxSetNum > 1 ? " (fold " + this.m_setNum + ")" : ""));
/*  628:     */             
/*  629:     */ 
/*  630:1009 */             Classifier.this.notifyTextListeners(nt);
/*  631:     */           }
/*  632:     */         }
/*  633:     */       }
/*  634:     */       catch (Exception ex)
/*  635:     */       {
/*  636:     */         String titleString;
/*  637:1013 */         ex.printStackTrace();
/*  638:1014 */         if (Classifier.this.m_log != null)
/*  639:     */         {
/*  640:1015 */           String titleString = "[Classifier] " + Classifier.this.statusMessagePrefix();
/*  641:     */           
/*  642:1017 */           titleString = titleString + " run " + this.m_runNum + " fold " + this.m_setNum + " failed to complete.";
/*  643:     */           
/*  644:1019 */           Classifier.this.m_log.logMessage(titleString + " (build classifier). " + ex.getMessage());
/*  645:     */           
/*  646:1021 */           Classifier.this.m_log.statusMessage(Classifier.this.statusMessagePrefix() + "ERROR (see log for details)");
/*  647:     */           
/*  648:1023 */           ex.printStackTrace();
/*  649:     */         }
/*  650:1025 */         this.m_taskInfo.setExecutionStatus(2);
/*  651:     */         
/*  652:1027 */         Classifier.this.stop();
/*  653:     */       }
/*  654:     */       finally
/*  655:     */       {
/*  656:     */         String titleString;
/*  657:1029 */         Classifier.this.m_visual.setStatic();
/*  658:1030 */         if ((Classifier.this.m_log != null) && 
/*  659:1031 */           (this.m_setNum == this.m_maxSetNum)) {
/*  660:1032 */           Classifier.this.m_log.statusMessage(Classifier.this.statusMessagePrefix() + "Finished.");
/*  661:     */         }
/*  662:1035 */         Classifier.this.m_state = Classifier.IDLE;
/*  663:1036 */         if (Thread.currentThread().isInterrupted())
/*  664:     */         {
/*  665:1038 */           Classifier.this.m_trainingSet = null;
/*  666:1039 */           if (Classifier.this.m_log != null)
/*  667:     */           {
/*  668:1040 */             String titleString = "[Classifier] " + Classifier.this.statusMessagePrefix();
/*  669:     */             
/*  670:1042 */             Classifier.this.m_log.logMessage(titleString + " (" + " run " + this.m_runNum + " fold " + this.m_setNum + ") interrupted!");
/*  671:     */             
/*  672:1044 */             Classifier.this.m_log.statusMessage(Classifier.this.statusMessagePrefix() + "INTERRUPTED");
/*  673:     */           }
/*  674:     */         }
/*  675:     */       }
/*  676:     */     }
/*  677:     */     
/*  678:     */     public TaskStatusInfo getTaskStatus()
/*  679:     */     {
/*  680:1081 */       return null;
/*  681:     */     }
/*  682:     */   }
/*  683:     */   
/*  684:     */   public void acceptTrainingSet(TrainingSetEvent e)
/*  685:     */   {
/*  686:1093 */     if (e.isStructureOnly())
/*  687:     */     {
/*  688:1098 */       BatchClassifierEvent ce = new BatchClassifierEvent(this, this.m_Classifier, new DataSetEvent(this, e.getTrainingSet()), new DataSetEvent(this, e.getTrainingSet()), e.getSetNumber(), e.getMaxSetNumber());
/*  689:     */       
/*  690:     */ 
/*  691:     */ 
/*  692:     */ 
/*  693:1103 */       notifyBatchClassifierListeners(ce);
/*  694:1104 */       return;
/*  695:     */     }
/*  696:1107 */     if (this.m_reject)
/*  697:     */     {
/*  698:1109 */       if (this.m_log != null)
/*  699:     */       {
/*  700:1110 */         this.m_log.statusMessage(statusMessagePrefix() + "BUSY. Can't accept data " + "at this time.");
/*  701:     */         
/*  702:1112 */         this.m_log.logMessage("[Classifier] " + statusMessagePrefix() + " BUSY. Can't accept data at this time.");
/*  703:     */       }
/*  704:1115 */       return;
/*  705:     */     }
/*  706:1119 */     if ((e.getRunNumber() == 1) && (e.getSetNumber() == 1))
/*  707:     */     {
/*  708:1122 */       this.m_trainingSet = new Instances(e.getTrainingSet(), 0);
/*  709:1123 */       this.m_state = BUILDING_MODEL;
/*  710:     */       
/*  711:1125 */       String msg = "[Classifier] " + statusMessagePrefix() + " starting executor pool (" + getExecutionSlots() + " slots)...";
/*  712:1128 */       if (this.m_log != null) {
/*  713:1129 */         this.m_log.logMessage(msg);
/*  714:     */       } else {
/*  715:1131 */         System.err.println(msg);
/*  716:     */       }
/*  717:1137 */       startExecutorPool();
/*  718:     */       
/*  719:     */ 
/*  720:     */ 
/*  721:1141 */       msg = "[Classifier] " + statusMessagePrefix() + " setup output queues.";
/*  722:1142 */       if (this.m_log != null) {
/*  723:1143 */         this.m_log.logMessage(msg);
/*  724:     */       } else {
/*  725:1145 */         System.err.println(msg);
/*  726:     */       }
/*  727:1148 */       if (!this.m_batchStarted)
/*  728:     */       {
/*  729:1149 */         this.m_outputQueues = new BatchClassifierEvent[e.getMaxRunNumber()][e.getMaxSetNumber()];
/*  730:     */         
/*  731:1151 */         this.m_completedSets = new boolean[e.getMaxRunNumber()][e.getMaxSetNumber()];
/*  732:1152 */         this.m_currentBatchIdentifier = new Date();
/*  733:1153 */         this.m_batchStarted = true;
/*  734:     */       }
/*  735:     */     }
/*  736:1158 */     TrainingTask newTask = new TrainingTask(e.getRunNumber(), e.getMaxRunNumber(), e.getSetNumber(), e.getMaxSetNumber(), e.getTrainingSet());
/*  737:     */     
/*  738:     */ 
/*  739:1161 */     String msg = "[Classifier] " + statusMessagePrefix() + " scheduling run " + e.getRunNumber() + " fold " + e.getSetNumber() + " for execution...";
/*  740:1164 */     if (this.m_log != null) {
/*  741:1165 */       this.m_log.logMessage(msg);
/*  742:     */     } else {
/*  743:1167 */       System.err.println(msg);
/*  744:     */     }
/*  745:1174 */     this.m_executorPool.execute(newTask);
/*  746:     */   }
/*  747:     */   
/*  748:     */   protected static boolean allMissingClass(Instances toCheck)
/*  749:     */   {
/*  750:1184 */     if (toCheck.classIndex() < 0) {
/*  751:1185 */       return false;
/*  752:     */     }
/*  753:1188 */     for (int i = 0; i < toCheck.numInstances(); i++) {
/*  754:1189 */       if (!toCheck.instance(i).classIsMissing()) {
/*  755:1190 */         return false;
/*  756:     */       }
/*  757:     */     }
/*  758:1194 */     return true;
/*  759:     */   }
/*  760:     */   
/*  761:     */   public synchronized void acceptTestSet(TestSetEvent e)
/*  762:     */   {
/*  763:1204 */     if (this.m_reject)
/*  764:     */     {
/*  765:1205 */       if (this.m_log != null)
/*  766:     */       {
/*  767:1206 */         this.m_log.statusMessage(statusMessagePrefix() + "BUSY. Can't accept data " + "at this time.");
/*  768:     */         
/*  769:1208 */         this.m_log.logMessage("[Classifier] " + statusMessagePrefix() + " BUSY. Can't accept data at this time.");
/*  770:     */       }
/*  771:1211 */       return;
/*  772:     */     }
/*  773:1214 */     Instances testSet = e.getTestSet();
/*  774:1215 */     if ((testSet != null) && 
/*  775:1216 */       (testSet.classIndex() < 0))
/*  776:     */     {
/*  777:1219 */       stop();
/*  778:1220 */       String errorMessage = statusMessagePrefix() + "ERROR: no class attribute set in test data!";
/*  779:1222 */       if (this.m_log != null)
/*  780:     */       {
/*  781:1223 */         this.m_log.statusMessage(errorMessage);
/*  782:1224 */         this.m_log.logMessage("[Classifier] " + errorMessage);
/*  783:     */       }
/*  784:     */       else
/*  785:     */       {
/*  786:1226 */         System.err.println("[Classifier] " + errorMessage);
/*  787:     */       }
/*  788:1228 */       return;
/*  789:     */     }
/*  790:1232 */     if ((this.m_loadModelFileName != null) && (this.m_loadModelFileName.length() > 0) && (this.m_state == IDLE) && (!this.m_listenees.containsKey("trainingSet")) && (e.getMaxRunNumber() == 1) && (e.getMaxSetNumber() == 1))
/*  791:     */     {
/*  792:1237 */       String resolvedFileName = this.m_loadModelFileName;
/*  793:1238 */       if (this.m_env != null) {
/*  794:     */         try
/*  795:     */         {
/*  796:1240 */           resolvedFileName = this.m_env.substitute(resolvedFileName);
/*  797:     */         }
/*  798:     */         catch (Exception ex) {}
/*  799:     */       }
/*  800:1244 */       File loadFrom = new File(resolvedFileName);
/*  801:     */       try
/*  802:     */       {
/*  803:1246 */         loadFromFile(loadFrom);
/*  804:     */       }
/*  805:     */       catch (Exception ex)
/*  806:     */       {
/*  807:1248 */         stop();
/*  808:1249 */         this.m_log.statusMessage(statusMessagePrefix() + "ERROR: unable to load " + "model (see log).");
/*  809:     */         
/*  810:1251 */         this.m_log.logMessage("[Classifier] " + statusMessagePrefix() + "Problem loading classifier. " + ex.getMessage());
/*  811:     */         
/*  812:1253 */         return;
/*  813:     */       }
/*  814:     */     }
/*  815:1257 */     weka.classifiers.Classifier classifierToUse = this.m_Classifier;
/*  816:1263 */     if ((classifierToUse != null) && (this.m_state == IDLE) && (!this.m_listenees.containsKey("trainingSet")))
/*  817:     */     {
/*  818:1270 */       if ((e.getTestSet() != null) && (e.isStructureOnly())) {
/*  819:1271 */         return;
/*  820:     */       }
/*  821:1274 */       if (((classifierToUse instanceof EnvironmentHandler)) && (this.m_env != null)) {
/*  822:1275 */         ((EnvironmentHandler)classifierToUse).setEnvironment(this.m_env);
/*  823:     */       }
/*  824:1278 */       if ((classifierToUse instanceof InputMappedClassifier)) {
/*  825:     */         try
/*  826:     */         {
/*  827:1283 */           this.m_trainingSet = ((InputMappedClassifier)classifierToUse).getModelHeader(this.m_trainingSet);
/*  828:     */         }
/*  829:     */         catch (Exception e1)
/*  830:     */         {
/*  831:1289 */           e1.printStackTrace();
/*  832:     */         }
/*  833:     */       }
/*  834:1295 */       if (this.m_trainingSet == null)
/*  835:     */       {
/*  836:1296 */         stop();
/*  837:1297 */         String errorMessage = statusMessagePrefix() + "ERROR: no trained/loaded classifier to use for prediction!";
/*  838:1300 */         if (this.m_log != null)
/*  839:     */         {
/*  840:1301 */           this.m_log.statusMessage(errorMessage);
/*  841:1302 */           this.m_log.logMessage("[Classifier] " + errorMessage);
/*  842:     */         }
/*  843:     */         else
/*  844:     */         {
/*  845:1304 */           System.err.println("[Classifier] " + errorMessage);
/*  846:     */         }
/*  847:1306 */         return;
/*  848:     */       }
/*  849:1309 */       testSet = e.getTestSet();
/*  850:1310 */       if ((e.getRunNumber() == 1) && (e.getSetNumber() == 1)) {
/*  851:1311 */         this.m_currentBatchIdentifier = new Date();
/*  852:     */       }
/*  853:1314 */       if (testSet != null)
/*  854:     */       {
/*  855:1315 */         if ((!this.m_trainingSet.equalHeaders(testSet)) && (!(classifierToUse instanceof InputMappedClassifier)))
/*  856:     */         {
/*  857:1317 */           boolean wrapClassifier = false;
/*  858:1318 */           if (!Utils.getDontShowDialog("weka.gui.beans.Classifier.AutoWrapInInputMappedClassifier"))
/*  859:     */           {
/*  860:1323 */             if (!GraphicsEnvironment.isHeadless())
/*  861:     */             {
/*  862:1324 */               JCheckBox dontShow = new JCheckBox("Do not show this message again");
/*  863:     */               
/*  864:1326 */               Object[] stuff = new Object[2];
/*  865:1327 */               stuff[0] = "Data used to train model and test set are not compatible.\nWould you like to automatically wrap the classifier in\nan \"InputMappedClassifier\" before proceeding?.\n";
/*  866:     */               
/*  867:     */ 
/*  868:     */ 
/*  869:1331 */               stuff[1] = dontShow;
/*  870:     */               
/*  871:1333 */               int result = JOptionPane.showConfirmDialog(this, stuff, "KnowledgeFlow:Classifier", 0);
/*  872:1337 */               if (result == 0) {
/*  873:1338 */                 wrapClassifier = true;
/*  874:     */               }
/*  875:1341 */               if (dontShow.isSelected())
/*  876:     */               {
/*  877:1342 */                 String response = wrapClassifier ? "yes" : "no";
/*  878:     */                 try
/*  879:     */                 {
/*  880:1344 */                   Utils.setDontShowDialogResponse("weka.gui.explorer.ClassifierPanel.AutoWrapInInputMappedClassifier", response);
/*  881:     */                 }
/*  882:     */                 catch (Exception e1)
/*  883:     */                 {
/*  884:1350 */                   e1.printStackTrace();
/*  885:     */                 }
/*  886:     */               }
/*  887:     */             }
/*  888:     */             else
/*  889:     */             {
/*  890:1355 */               wrapClassifier = true;
/*  891:     */             }
/*  892:     */           }
/*  893:     */           else {
/*  894:     */             try
/*  895:     */             {
/*  896:1361 */               String response = Utils.getDontShowDialogResponse("weka.gui.explorer.ClassifierPanel.AutoWrapInInputMappedClassifier");
/*  897:1364 */               if ((response != null) && (response.equalsIgnoreCase("yes"))) {
/*  898:1365 */                 wrapClassifier = true;
/*  899:     */               }
/*  900:     */             }
/*  901:     */             catch (Exception e1)
/*  902:     */             {
/*  903:1369 */               e1.printStackTrace();
/*  904:     */             }
/*  905:     */           }
/*  906:1373 */           if (wrapClassifier)
/*  907:     */           {
/*  908:1374 */             InputMappedClassifier temp = new InputMappedClassifier();
/*  909:     */             
/*  910:     */ 
/*  911:1377 */             temp.setClassifier(classifierToUse);
/*  912:1378 */             temp.setModelHeader(new Instances(this.m_trainingSet, 0));
/*  913:1379 */             classifierToUse = temp;
/*  914:     */           }
/*  915:     */         }
/*  916:1383 */         if ((this.m_trainingSet.equalHeaders(testSet)) || ((classifierToUse instanceof InputMappedClassifier)))
/*  917:     */         {
/*  918:1385 */           BatchClassifierEvent ce = new BatchClassifierEvent(this, classifierToUse, new DataSetEvent(this, this.m_trainingSet), new DataSetEvent(this, e.getTestSet()), e.getRunNumber(), e.getMaxRunNumber(), e.getSetNumber(), e.getMaxSetNumber());
/*  919:     */           
/*  920:     */ 
/*  921:     */ 
/*  922:     */ 
/*  923:1390 */           ce.setGroupIdentifier(this.m_currentBatchIdentifier.getTime());
/*  924:1391 */           ce.setLabel(getCustomName());
/*  925:1393 */           if ((this.m_log != null) && (!e.isStructureOnly())) {
/*  926:1394 */             this.m_log.statusMessage(statusMessagePrefix() + "Finished.");
/*  927:     */           }
/*  928:1396 */           this.m_batchStarted = false;
/*  929:1397 */           notifyBatchClassifierListeners(ce);
/*  930:     */         }
/*  931:1402 */         else if ((testSet.numInstances() > 0) && 
/*  932:1403 */           (testSet.classIndex() == this.m_trainingSet.classIndex()) && (allMissingClass(testSet)))
/*  933:     */         {
/*  934:1407 */           boolean ok = true;
/*  935:1408 */           for (int i = 0; i < testSet.numAttributes(); i++) {
/*  936:1409 */             if (i != testSet.classIndex())
/*  937:     */             {
/*  938:1410 */               ok = testSet.attribute(i).equals(this.m_trainingSet.attribute(i));
/*  939:1411 */               if (!ok) {
/*  940:     */                 break;
/*  941:     */               }
/*  942:     */             }
/*  943:     */           }
/*  944:1417 */           if (ok)
/*  945:     */           {
/*  946:1418 */             BatchClassifierEvent ce = new BatchClassifierEvent(this, classifierToUse, new DataSetEvent(this, this.m_trainingSet), new DataSetEvent(this, e.getTestSet()), e.getRunNumber(), e.getMaxRunNumber(), e.getSetNumber(), e.getMaxSetNumber());
/*  947:     */             
/*  948:     */ 
/*  949:     */ 
/*  950:     */ 
/*  951:1423 */             ce.setGroupIdentifier(this.m_currentBatchIdentifier.getTime());
/*  952:1424 */             ce.setLabel(getCustomName());
/*  953:1426 */             if ((this.m_log != null) && (!e.isStructureOnly())) {
/*  954:1427 */               this.m_log.statusMessage(statusMessagePrefix() + "Finished.");
/*  955:     */             }
/*  956:1429 */             this.m_batchStarted = false;
/*  957:1430 */             notifyBatchClassifierListeners(ce);
/*  958:     */           }
/*  959:     */           else
/*  960:     */           {
/*  961:1432 */             stop();
/*  962:1433 */             String errorMessage = statusMessagePrefix() + "ERROR: structure of training and test sets is not compatible!";
/*  963:1436 */             if (this.m_log != null)
/*  964:     */             {
/*  965:1437 */               this.m_log.statusMessage(errorMessage);
/*  966:1438 */               this.m_log.logMessage("[Classifier] " + errorMessage);
/*  967:     */             }
/*  968:     */             else
/*  969:     */             {
/*  970:1440 */               System.err.println("[Classifier] " + errorMessage);
/*  971:     */             }
/*  972:     */           }
/*  973:     */         }
/*  974:     */       }
/*  975:     */     }
/*  976:     */     else
/*  977:     */     {
/*  978:1452 */       if ((e.getRunNumber() == 1) && (e.getSetNumber() == 1) && 
/*  979:1453 */         (!this.m_batchStarted))
/*  980:     */       {
/*  981:1454 */         this.m_outputQueues = new BatchClassifierEvent[e.getMaxRunNumber()][e.getMaxSetNumber()];
/*  982:     */         
/*  983:1456 */         this.m_completedSets = new boolean[e.getMaxRunNumber()][e.getMaxSetNumber()];
/*  984:     */         
/*  985:1458 */         this.m_currentBatchIdentifier = new Date();
/*  986:1459 */         this.m_batchStarted = true;
/*  987:     */       }
/*  988:1463 */       if (this.m_outputQueues[(e.getRunNumber() - 1)][(e.getSetNumber() - 1)] == null)
/*  989:     */       {
/*  990:1465 */         if (!e.isStructureOnly())
/*  991:     */         {
/*  992:1468 */           this.m_outputQueues[(e.getRunNumber() - 1)][(e.getSetNumber() - 1)] = new BatchClassifierEvent(this, null, null, new DataSetEvent(this, e.getTestSet()), e.getRunNumber(), e.getMaxRunNumber(), e.getSetNumber(), e.getMaxSetNumber());
/*  993:     */           
/*  994:     */ 
/*  995:     */ 
/*  996:1472 */           this.m_outputQueues[(e.getRunNumber() - 1)][(e.getSetNumber() - 1)].setLabel(getCustomName());
/*  997:1474 */           if ((e.getRunNumber() == e.getMaxRunNumber()) && (e.getSetNumber() == e.getMaxSetNumber())) {
/*  998:1484 */             if (e.getMaxSetNumber() != 1)
/*  999:     */             {
/* 1000:1485 */               this.m_reject = true;
/* 1001:1486 */               if (this.m_block) {
/* 1002:1487 */                 block(true);
/* 1003:     */               }
/* 1004:     */             }
/* 1005:     */           }
/* 1006:     */         }
/* 1007:     */       }
/* 1008:     */       else
/* 1009:     */       {
/* 1010:1494 */         this.m_outputQueues[(e.getRunNumber() - 1)][(e.getSetNumber() - 1)].setTestSet(new DataSetEvent(this, e.getTestSet()));
/* 1011:     */         
/* 1012:1496 */         checkCompletedRun(e.getRunNumber(), e.getMaxRunNumber(), e.getMaxSetNumber());
/* 1013:     */       }
/* 1014:     */     }
/* 1015:     */   }
/* 1016:     */   
/* 1017:     */   private synchronized void classifierTrainingComplete(BatchClassifierEvent ce)
/* 1018:     */   {
/* 1019:1504 */     if (this.m_listenees.containsKey("testSet"))
/* 1020:     */     {
/* 1021:1505 */       String msg = "[Classifier] " + statusMessagePrefix() + " storing model for run " + ce.getRunNumber() + " fold " + ce.getSetNumber();
/* 1022:1508 */       if (this.m_log != null) {
/* 1023:1509 */         this.m_log.logMessage(msg);
/* 1024:     */       } else {
/* 1025:1511 */         System.err.println(msg);
/* 1026:     */       }
/* 1027:1514 */       if (this.m_outputQueues[(ce.getRunNumber() - 1)][(ce.getSetNumber() - 1)] == null)
/* 1028:     */       {
/* 1029:1516 */         this.m_outputQueues[(ce.getRunNumber() - 1)][(ce.getSetNumber() - 1)] = ce;
/* 1030:     */       }
/* 1031:     */       else
/* 1032:     */       {
/* 1033:1519 */         this.m_outputQueues[(ce.getRunNumber() - 1)][(ce.getSetNumber() - 1)].setClassifier(ce.getClassifier());
/* 1034:     */         
/* 1035:1521 */         this.m_outputQueues[(ce.getRunNumber() - 1)][(ce.getSetNumber() - 1)].setTrainSet(ce.getTrainSet());
/* 1036:     */       }
/* 1037:1525 */       checkCompletedRun(ce.getRunNumber(), ce.getMaxRunNumber(), ce.getMaxSetNumber());
/* 1038:     */     }
/* 1039:     */   }
/* 1040:     */   
/* 1041:     */   private synchronized void checkCompletedRun(int runNum, int maxRunNum, int maxSets)
/* 1042:     */   {
/* 1043:1534 */     for (int i = 0; i < maxSets; i++) {
/* 1044:1535 */       if ((this.m_outputQueues[(runNum - 1)][i] != null) && 
/* 1045:1536 */         (this.m_outputQueues[(runNum - 1)][i].getClassifier() != null) && (this.m_outputQueues[(runNum - 1)][i].getTestSet() != null))
/* 1046:     */       {
/* 1047:1538 */         String msg = "[Classifier] " + statusMessagePrefix() + " dispatching run/set " + runNum + "/" + (i + 1) + " to listeners.";
/* 1048:1541 */         if (this.m_log != null) {
/* 1049:1542 */           this.m_log.logMessage(msg);
/* 1050:     */         } else {
/* 1051:1544 */           System.err.println(msg);
/* 1052:     */         }
/* 1053:1548 */         this.m_outputQueues[(runNum - 1)][i].setGroupIdentifier(this.m_currentBatchIdentifier.getTime());
/* 1054:     */         
/* 1055:1550 */         this.m_outputQueues[(runNum - 1)][i].setLabel(getCustomName());
/* 1056:1551 */         notifyBatchClassifierListeners(this.m_outputQueues[(runNum - 1)][i]);
/* 1057:     */         
/* 1058:1553 */         this.m_outputQueues[(runNum - 1)][i] = null;
/* 1059:     */         
/* 1060:1555 */         this.m_completedSets[(runNum - 1)][i] = 1;
/* 1061:     */       }
/* 1062:     */     }
/* 1063:1561 */     boolean done = true;
/* 1064:1562 */     for (int i = 0; i < maxRunNum; i++)
/* 1065:     */     {
/* 1066:1563 */       for (int j = 0; j < maxSets; j++) {
/* 1067:1564 */         if (this.m_completedSets[i][j] == 0)
/* 1068:     */         {
/* 1069:1565 */           done = false;
/* 1070:1566 */           break;
/* 1071:     */         }
/* 1072:     */       }
/* 1073:1569 */       if (!done) {
/* 1074:     */         break;
/* 1075:     */       }
/* 1076:     */     }
/* 1077:1574 */     if (done)
/* 1078:     */     {
/* 1079:1575 */       String msg = "[Classifier] " + statusMessagePrefix() + " last classifier unblocking...";
/* 1080:1579 */       if (this.m_log != null) {
/* 1081:1580 */         this.m_log.logMessage(msg);
/* 1082:     */       } else {
/* 1083:1582 */         System.err.println(msg);
/* 1084:     */       }
/* 1085:1586 */       if (this.m_log != null) {
/* 1086:1587 */         this.m_log.statusMessage(statusMessagePrefix() + "Finished.");
/* 1087:     */       }
/* 1088:1590 */       this.m_reject = false;
/* 1089:1591 */       this.m_batchStarted = false;
/* 1090:1592 */       block(false);
/* 1091:1593 */       this.m_state = IDLE;
/* 1092:     */     }
/* 1093:     */   }
/* 1094:     */   
/* 1095:     */   public void setVisual(BeanVisual newVisual)
/* 1096:     */   {
/* 1097:1630 */     this.m_visual = newVisual;
/* 1098:     */   }
/* 1099:     */   
/* 1100:     */   public BeanVisual getVisual()
/* 1101:     */   {
/* 1102:1638 */     return this.m_visual;
/* 1103:     */   }
/* 1104:     */   
/* 1105:     */   public void useDefaultVisual()
/* 1106:     */   {
/* 1107:1647 */     String name = this.m_ClassifierTemplate.getClass().toString();
/* 1108:1648 */     String packageName = name.substring(0, name.lastIndexOf('.'));
/* 1109:1649 */     packageName = packageName.substring(packageName.lastIndexOf('.') + 1, packageName.length());
/* 1110:1652 */     if (!this.m_visual.loadIcons("weka/gui/beans/icons/Default_" + packageName + "Classifier.gif", "weka/gui/beans/icons/Default_" + packageName + "Classifier_animated.gif")) {
/* 1111:1655 */       this.m_visual.loadIcons("weka/gui/beans/icons/DefaultClassifier.gif", "weka/gui/beans/icons/DefaultClassifier_animated.gif");
/* 1112:     */     }
/* 1113:     */   }
/* 1114:     */   
/* 1115:     */   public synchronized void addBatchClassifierListener(BatchClassifierListener cl)
/* 1116:     */   {
/* 1117:1667 */     this.m_batchClassifierListeners.addElement(cl);
/* 1118:     */   }
/* 1119:     */   
/* 1120:     */   public synchronized void removeBatchClassifierListener(BatchClassifierListener cl)
/* 1121:     */   {
/* 1122:1677 */     this.m_batchClassifierListeners.remove(cl);
/* 1123:     */   }
/* 1124:     */   
/* 1125:     */   private synchronized void notifyBatchClassifierListeners(BatchClassifierEvent ce)
/* 1126:     */   {
/* 1127:1691 */     if (Thread.currentThread().isInterrupted()) {
/* 1128:     */       return;
/* 1129:     */     }
/* 1130:     */     Vector<BatchClassifierListener> l;
/* 1131:1695 */     synchronized (this)
/* 1132:     */     {
/* 1133:1696 */       l = (Vector)this.m_batchClassifierListeners.clone();
/* 1134:     */     }
/* 1135:1698 */     if (l.size() > 0) {
/* 1136:1699 */       for (int i = 0; i < l.size(); i++) {
/* 1137:1700 */         if (l.size() > 1) {
/* 1138:     */           try
/* 1139:     */           {
/* 1140:1708 */             weka.classifiers.Classifier newC = AbstractClassifier.makeCopy(ce.getClassifier());
/* 1141:     */             
/* 1142:1710 */             BatchClassifierEvent ne = new BatchClassifierEvent(this, newC, ce.getTrainSet(), ce.getTestSet(), ce.getRunNumber(), ce.getMaxRunNumber(), ce.getSetNumber(), ce.getMaxSetNumber());
/* 1143:     */             
/* 1144:     */ 
/* 1145:     */ 
/* 1146:1714 */             ((BatchClassifierListener)l.elementAt(i)).acceptClassifier(ne);
/* 1147:     */           }
/* 1148:     */           catch (Exception e)
/* 1149:     */           {
/* 1150:1716 */             stop();
/* 1151:1717 */             if (this.m_log != null)
/* 1152:     */             {
/* 1153:1718 */               String msg = statusMessagePrefix() + "ERROR: unable to make copy of classifier - see log ";
/* 1154:     */               
/* 1155:     */ 
/* 1156:     */ 
/* 1157:1722 */               this.m_log.logMessage("[Classifier] " + msg + " (" + e.getMessage() + ")");
/* 1158:     */               
/* 1159:1724 */               this.m_log.statusMessage(msg);
/* 1160:     */             }
/* 1161:1726 */             e.printStackTrace();
/* 1162:1727 */             break;
/* 1163:     */           }
/* 1164:     */         } else {
/* 1165:1730 */           ((BatchClassifierListener)l.elementAt(i)).acceptClassifier(ce);
/* 1166:     */         }
/* 1167:     */       }
/* 1168:     */     }
/* 1169:     */   }
/* 1170:     */   
/* 1171:     */   public synchronized void addGraphListener(GraphListener cl)
/* 1172:     */   {
/* 1173:1742 */     this.m_graphListeners.addElement(cl);
/* 1174:     */   }
/* 1175:     */   
/* 1176:     */   public synchronized void removeGraphListener(GraphListener cl)
/* 1177:     */   {
/* 1178:1751 */     this.m_graphListeners.remove(cl);
/* 1179:     */   }
/* 1180:     */   
/* 1181:     */   private void notifyGraphListeners(GraphEvent ge)
/* 1182:     */   {
/* 1183:     */     Vector<GraphListener> l;
/* 1184:1762 */     synchronized (this)
/* 1185:     */     {
/* 1186:1763 */       l = (Vector)this.m_graphListeners.clone();
/* 1187:     */     }
/* 1188:1765 */     if (l.size() > 0) {
/* 1189:1766 */       for (int i = 0; i < l.size(); i++) {
/* 1190:1767 */         ((GraphListener)l.elementAt(i)).acceptGraph(ge);
/* 1191:     */       }
/* 1192:     */     }
/* 1193:     */   }
/* 1194:     */   
/* 1195:     */   public synchronized void addTextListener(TextListener cl)
/* 1196:     */   {
/* 1197:1778 */     this.m_textListeners.addElement(cl);
/* 1198:     */   }
/* 1199:     */   
/* 1200:     */   public synchronized void removeTextListener(TextListener cl)
/* 1201:     */   {
/* 1202:1787 */     this.m_textListeners.remove(cl);
/* 1203:     */   }
/* 1204:     */   
/* 1205:     */   public synchronized void addConfigurationListener(ConfigurationListener cl) {}
/* 1206:     */   
/* 1207:     */   public synchronized void removeConfigurationListener(ConfigurationListener cl) {}
/* 1208:     */   
/* 1209:     */   private void notifyTextListeners(TextEvent ge)
/* 1210:     */   {
/* 1211:     */     Vector<TextListener> l;
/* 1212:1821 */     synchronized (this)
/* 1213:     */     {
/* 1214:1822 */       l = (Vector)this.m_textListeners.clone();
/* 1215:     */     }
/* 1216:1824 */     if (l.size() > 0) {
/* 1217:1825 */       for (int i = 0; i < l.size(); i++) {
/* 1218:1826 */         ((TextListener)l.elementAt(i)).acceptText(ge);
/* 1219:     */       }
/* 1220:     */     }
/* 1221:     */   }
/* 1222:     */   
/* 1223:     */   public synchronized void addIncrementalClassifierListener(IncrementalClassifierListener cl)
/* 1224:     */   {
/* 1225:1838 */     this.m_incrementalClassifierListeners.add(cl);
/* 1226:     */   }
/* 1227:     */   
/* 1228:     */   public synchronized void removeIncrementalClassifierListener(IncrementalClassifierListener cl)
/* 1229:     */   {
/* 1230:1848 */     this.m_incrementalClassifierListeners.remove(cl);
/* 1231:     */   }
/* 1232:     */   
/* 1233:     */   private void notifyIncrementalClassifierListeners(IncrementalClassifierEvent ce)
/* 1234:     */   {
/* 1235:1862 */     if (Thread.currentThread().isInterrupted()) {
/* 1236:     */       return;
/* 1237:     */     }
/* 1238:     */     Vector<IncrementalClassifierListener> l;
/* 1239:1867 */     synchronized (this)
/* 1240:     */     {
/* 1241:1868 */       l = (Vector)this.m_incrementalClassifierListeners.clone();
/* 1242:     */     }
/* 1243:1872 */     if (l.size() > 0) {
/* 1244:1873 */       for (int i = 0; i < l.size(); i++) {
/* 1245:1874 */         ((IncrementalClassifierListener)l.elementAt(i)).acceptClassifier(ce);
/* 1246:     */       }
/* 1247:     */     }
/* 1248:     */   }
/* 1249:     */   
/* 1250:     */   public boolean connectionAllowed(String eventName)
/* 1251:     */   {
/* 1252:1893 */     if ((eventName.equals("trainingSet")) && (this.m_listenees.containsKey(eventName))) {
/* 1253:1894 */       return false;
/* 1254:     */     }
/* 1255:1897 */     return true;
/* 1256:     */   }
/* 1257:     */   
/* 1258:     */   public boolean connectionAllowed(EventSetDescriptor esd)
/* 1259:     */   {
/* 1260:1909 */     return connectionAllowed(esd.getName());
/* 1261:     */   }
/* 1262:     */   
/* 1263:     */   public synchronized void connectionNotification(String eventName, Object source)
/* 1264:     */   {
/* 1265:1923 */     if ((eventName.compareTo("instance") == 0) && 
/* 1266:1924 */       (!(this.m_ClassifierTemplate instanceof UpdateableClassifier)) && 
/* 1267:1925 */       (this.m_log != null))
/* 1268:     */     {
/* 1269:1926 */       String msg = statusMessagePrefix() + "WARNING: " + this.m_ClassifierTemplate.getClass().getName() + " Is not an updateable classifier. This " + "classifier will only be evaluated on incoming " + "instance events and not trained on them.";
/* 1270:     */       
/* 1271:     */ 
/* 1272:     */ 
/* 1273:     */ 
/* 1274:     */ 
/* 1275:1932 */       this.m_log.logMessage("[Classifier] " + msg);
/* 1276:1933 */       this.m_log.statusMessage(msg);
/* 1277:     */     }
/* 1278:1938 */     if ((eventName.equals("testSet")) && (this.m_listenees.containsKey("testSet")) && (this.m_log != null)) {
/* 1279:1940 */       if ((!Utils.getDontShowDialog("weka.gui.beans.ClassifierMultipleTestSetConnections")) && (!GraphicsEnvironment.isHeadless()))
/* 1280:     */       {
/* 1281:1944 */         String msg = "You have more than one incoming test set connection to \n'" + getCustomName() + "'. In order for this setup to run properly\n" + "and generate correct evaluation results you MUST execute the flow\n" + "by launching start points sequentially (second play button). In order\n" + "to specify the order you'd like the start points launched in you can\n" + "set the name of each start point (right click on start point and select\n" + "'Set name') to include a number prefix - e.g. '1: load my arff file'.";
/* 1282:     */         
/* 1283:     */ 
/* 1284:     */ 
/* 1285:     */ 
/* 1286:     */ 
/* 1287:     */ 
/* 1288:     */ 
/* 1289:     */ 
/* 1290:     */ 
/* 1291:     */ 
/* 1292:1955 */         JCheckBox dontShow = new JCheckBox("Do not show this message again");
/* 1293:1956 */         Object[] stuff = new Object[2];
/* 1294:1957 */         stuff[0] = msg;
/* 1295:1958 */         stuff[1] = dontShow;
/* 1296:     */         
/* 1297:1960 */         JOptionPane.showMessageDialog(null, stuff, "Classifier test connection", 0);
/* 1298:1963 */         if (dontShow.isSelected()) {
/* 1299:     */           try
/* 1300:     */           {
/* 1301:1965 */             Utils.setDontShowDialog("weka.gui.beans.ClassifierMultipleTestSetConnections");
/* 1302:     */           }
/* 1303:     */           catch (Exception ex) {}
/* 1304:     */         }
/* 1305:     */       }
/* 1306:     */     }
/* 1307:1974 */     if (connectionAllowed(eventName))
/* 1308:     */     {
/* 1309:1975 */       List<Object> listenee = (List)this.m_listenees.get(eventName);
/* 1310:1976 */       if (listenee == null)
/* 1311:     */       {
/* 1312:1977 */         listenee = new ArrayList();
/* 1313:1978 */         this.m_listenees.put(eventName, listenee);
/* 1314:     */       }
/* 1315:1980 */       listenee.add(source);
/* 1316:     */     }
/* 1317:     */   }
/* 1318:     */   
/* 1319:     */   public synchronized void disconnectionNotification(String eventName, Object source)
/* 1320:     */   {
/* 1321:2001 */     List<Object> listenees = (List)this.m_listenees.get(eventName);
/* 1322:2003 */     if (listenees != null)
/* 1323:     */     {
/* 1324:2004 */       listenees.remove(source);
/* 1325:2006 */       if (listenees.size() == 0) {
/* 1326:2007 */         this.m_listenees.remove(eventName);
/* 1327:     */       }
/* 1328:     */     }
/* 1329:2011 */     if (eventName.compareTo("instance") == 0) {
/* 1330:2012 */       stop();
/* 1331:     */     }
/* 1332:     */   }
/* 1333:     */   
/* 1334:     */   private synchronized void block(boolean tf)
/* 1335:     */   {
/* 1336:2024 */     if (tf) {
/* 1337:     */       try
/* 1338:     */       {
/* 1339:2028 */         wait();
/* 1340:     */       }
/* 1341:     */       catch (InterruptedException ex) {}
/* 1342:     */     } else {
/* 1343:2033 */       notifyAll();
/* 1344:     */     }
/* 1345:     */   }
/* 1346:     */   
/* 1347:     */   public void stop()
/* 1348:     */   {
/* 1349:2045 */     for (Map.Entry<String, List<Object>> e : this.m_listenees.entrySet())
/* 1350:     */     {
/* 1351:2046 */       List<Object> l = (List)e.getValue();
/* 1352:2047 */       for (Object o : l) {
/* 1353:2048 */         if ((o instanceof BeanCommon)) {
/* 1354:2049 */           ((BeanCommon)o).stop();
/* 1355:     */         }
/* 1356:     */       }
/* 1357:     */     }
/* 1358:2061 */     if (this.m_executorPool != null)
/* 1359:     */     {
/* 1360:2062 */       this.m_executorPool.shutdownNow();
/* 1361:2063 */       this.m_executorPool.purge();
/* 1362:2064 */       this.m_executorPool = null;
/* 1363:     */     }
/* 1364:2066 */     this.m_reject = false;
/* 1365:2067 */     this.m_batchStarted = false;
/* 1366:2068 */     block(false);
/* 1367:2069 */     this.m_visual.setStatic();
/* 1368:2070 */     if (this.m_oldText.length() > 0) {}
/* 1369:     */   }
/* 1370:     */   
/* 1371:     */   public void loadModel()
/* 1372:     */   {
/* 1373:     */     try
/* 1374:     */     {
/* 1375:2083 */       if (this.m_fileChooser == null) {
/* 1376:2085 */         setupFileChooser();
/* 1377:     */       }
/* 1378:2087 */       int returnVal = this.m_fileChooser.showOpenDialog(this);
/* 1379:2088 */       if (returnVal == 0)
/* 1380:     */       {
/* 1381:2089 */         File loadFrom = this.m_fileChooser.getSelectedFile();
/* 1382:2092 */         if (this.m_fileChooser.getFileFilter() == this.m_binaryFilter)
/* 1383:     */         {
/* 1384:2093 */           if (!loadFrom.getName().toLowerCase().endsWith(".model")) {
/* 1385:2094 */             loadFrom = new File(loadFrom.getParent(), loadFrom.getName() + "." + "model");
/* 1386:     */           }
/* 1387:     */         }
/* 1388:2098 */         else if (this.m_fileChooser.getFileFilter() == this.m_KOMLFilter)
/* 1389:     */         {
/* 1390:2099 */           if (!loadFrom.getName().toLowerCase().endsWith(".komlmodel")) {
/* 1391:2101 */             loadFrom = new File(loadFrom.getParent(), loadFrom.getName() + ".koml" + "model");
/* 1392:     */           }
/* 1393:     */         }
/* 1394:2105 */         else if ((this.m_fileChooser.getFileFilter() == this.m_XStreamFilter) && 
/* 1395:2106 */           (!loadFrom.getName().toLowerCase().endsWith(".xstreammodel"))) {
/* 1396:2108 */           loadFrom = new File(loadFrom.getParent(), loadFrom.getName() + ".xstream" + "model");
/* 1397:     */         }
/* 1398:2114 */         loadFromFile(loadFrom);
/* 1399:     */       }
/* 1400:     */     }
/* 1401:     */     catch (Exception ex)
/* 1402:     */     {
/* 1403:2117 */       JOptionPane.showMessageDialog(this, "Problem loading classifier.\n" + ex.getMessage(), "Load Model", 0);
/* 1404:2120 */       if (this.m_log != null)
/* 1405:     */       {
/* 1406:2121 */         this.m_log.statusMessage(statusMessagePrefix() + "ERROR: unable to load " + "model (see log).");
/* 1407:     */         
/* 1408:2123 */         this.m_log.logMessage("[Classifier] " + statusMessagePrefix() + "Problem loading classifier. " + ex.getMessage());
/* 1409:     */       }
/* 1410:     */     }
/* 1411:     */   }
/* 1412:     */   
/* 1413:     */   protected void loadFromFile(File loadFrom)
/* 1414:     */     throws Exception
/* 1415:     */   {
/* 1416:2130 */     weka.classifiers.Classifier temp = null;
/* 1417:2131 */     Instances tempHeader = null;
/* 1418:2133 */     if ((KOML.isPresent()) && (loadFrom.getAbsolutePath().toLowerCase().endsWith(".komlmodel")))
/* 1419:     */     {
/* 1420:2137 */       Vector<Object> v = (Vector)KOML.read(loadFrom.getAbsolutePath());
/* 1421:2138 */       temp = (weka.classifiers.Classifier)v.elementAt(0);
/* 1422:2139 */       if (v.size() == 2) {
/* 1423:2141 */         tempHeader = (Instances)v.elementAt(1);
/* 1424:     */       }
/* 1425:     */     }
/* 1426:2143 */     else if ((XStream.isPresent()) && (loadFrom.getAbsolutePath().toLowerCase().endsWith(".xstreammodel")))
/* 1427:     */     {
/* 1428:2147 */       Vector<Object> v = (Vector)XStream.read(loadFrom.getAbsolutePath());
/* 1429:     */       
/* 1430:2149 */       temp = (weka.classifiers.Classifier)v.elementAt(0);
/* 1431:2150 */       if (v.size() == 2) {
/* 1432:2152 */         tempHeader = (Instances)v.elementAt(1);
/* 1433:     */       }
/* 1434:     */     }
/* 1435:     */     else
/* 1436:     */     {
/* 1437:2156 */       ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(new FileInputStream(loadFrom)));
/* 1438:     */       
/* 1439:     */ 
/* 1440:     */ 
/* 1441:2160 */       temp = (weka.classifiers.Classifier)is.readObject();
/* 1442:     */       try
/* 1443:     */       {
/* 1444:2163 */         tempHeader = (Instances)is.readObject();
/* 1445:     */       }
/* 1446:     */       catch (Exception ex) {}
/* 1447:2168 */       is.close();
/* 1448:     */     }
/* 1449:2172 */     setTrainedClassifier(temp);
/* 1450:     */     
/* 1451:2174 */     this.m_trainingSet = tempHeader;
/* 1452:2176 */     if (this.m_log != null)
/* 1453:     */     {
/* 1454:2177 */       this.m_log.statusMessage(statusMessagePrefix() + "Loaded model.");
/* 1455:2178 */       this.m_log.logMessage("[Classifier] " + statusMessagePrefix() + "Loaded classifier: " + this.m_Classifier.getClass().toString() + " from file '" + loadFrom.toString() + "'");
/* 1456:     */     }
/* 1457:     */   }
/* 1458:     */   
/* 1459:     */   public void saveModel()
/* 1460:     */   {
/* 1461:     */     try
/* 1462:     */     {
/* 1463:2186 */       if (this.m_fileChooser == null) {
/* 1464:2188 */         setupFileChooser();
/* 1465:     */       }
/* 1466:2190 */       int returnVal = this.m_fileChooser.showSaveDialog(this);
/* 1467:2191 */       if (returnVal == 0)
/* 1468:     */       {
/* 1469:2192 */         File saveTo = this.m_fileChooser.getSelectedFile();
/* 1470:2193 */         String fn = saveTo.getAbsolutePath();
/* 1471:2194 */         if (this.m_fileChooser.getFileFilter() == this.m_binaryFilter)
/* 1472:     */         {
/* 1473:2195 */           if (!fn.toLowerCase().endsWith(".model")) {
/* 1474:2196 */             fn = fn + ".model";
/* 1475:     */           }
/* 1476:     */         }
/* 1477:2198 */         else if (this.m_fileChooser.getFileFilter() == this.m_KOMLFilter)
/* 1478:     */         {
/* 1479:2199 */           if (!fn.toLowerCase().endsWith(".komlmodel")) {
/* 1480:2200 */             fn = fn + ".komlmodel";
/* 1481:     */           }
/* 1482:     */         }
/* 1483:2202 */         else if ((this.m_fileChooser.getFileFilter() == this.m_XStreamFilter) && 
/* 1484:2203 */           (!fn.toLowerCase().endsWith(".xstreammodel"))) {
/* 1485:2205 */           fn = fn + ".xstreammodel";
/* 1486:     */         }
/* 1487:2208 */         saveTo = new File(fn);
/* 1488:2212 */         if ((KOML.isPresent()) && (saveTo.getAbsolutePath().toLowerCase().endsWith(".komlmodel")))
/* 1489:     */         {
/* 1490:2215 */           SerializedModelSaver.saveKOML(saveTo, this.m_Classifier, this.m_trainingSet != null ? new Instances(this.m_trainingSet, 0) : null);
/* 1491:     */         }
/* 1492:2222 */         else if ((XStream.isPresent()) && (saveTo.getAbsolutePath().toLowerCase().endsWith(".xstreammodel")))
/* 1493:     */         {
/* 1494:2226 */           SerializedModelSaver.saveXStream(saveTo, this.m_Classifier, this.m_trainingSet != null ? new Instances(this.m_trainingSet, 0) : null);
/* 1495:     */         }
/* 1496:     */         else
/* 1497:     */         {
/* 1498:2234 */           ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(saveTo)));
/* 1499:     */           
/* 1500:     */ 
/* 1501:2237 */           os.writeObject(this.m_Classifier);
/* 1502:2238 */           if (this.m_trainingSet != null)
/* 1503:     */           {
/* 1504:2239 */             Instances header = new Instances(this.m_trainingSet, 0);
/* 1505:2240 */             os.writeObject(header);
/* 1506:     */           }
/* 1507:2242 */           os.close();
/* 1508:     */         }
/* 1509:2244 */         if (this.m_log != null)
/* 1510:     */         {
/* 1511:2245 */           this.m_log.statusMessage(statusMessagePrefix() + "Model saved.");
/* 1512:2246 */           this.m_log.logMessage("[Classifier] " + statusMessagePrefix() + " Saved classifier " + getCustomName());
/* 1513:     */         }
/* 1514:     */       }
/* 1515:     */     }
/* 1516:     */     catch (Exception ex)
/* 1517:     */     {
/* 1518:2251 */       JOptionPane.showMessageDialog(this, "Problem saving classifier.\n", "Save Model", 0);
/* 1519:2254 */       if (this.m_log != null)
/* 1520:     */       {
/* 1521:2255 */         this.m_log.statusMessage(statusMessagePrefix() + "ERROR: unable to" + " save model (see log).");
/* 1522:     */         
/* 1523:2257 */         this.m_log.logMessage("[Classifier] " + statusMessagePrefix() + " Problem saving classifier " + getCustomName() + ex.getMessage());
/* 1524:     */       }
/* 1525:     */     }
/* 1526:     */   }
/* 1527:     */   
/* 1528:     */   public void setLog(Logger logger)
/* 1529:     */   {
/* 1530:2270 */     this.m_log = logger;
/* 1531:     */   }
/* 1532:     */   
/* 1533:     */   public Enumeration<String> enumerateRequests()
/* 1534:     */   {
/* 1535:2280 */     Vector<String> newVector = new Vector(0);
/* 1536:2281 */     if ((this.m_executorPool != null) && ((this.m_executorPool.getQueue().size() > 0) || (this.m_executorPool.getActiveCount() > 0))) {
/* 1537:2284 */       newVector.addElement("Stop");
/* 1538:     */     }
/* 1539:2287 */     if (((this.m_executorPool == null) || ((this.m_executorPool.getQueue().size() == 0) && (this.m_executorPool.getActiveCount() == 0))) && (this.m_Classifier != null)) {
/* 1540:2290 */       newVector.addElement("Save model");
/* 1541:     */     }
/* 1542:2293 */     if ((this.m_executorPool == null) || ((this.m_executorPool.getQueue().size() == 0) && (this.m_executorPool.getActiveCount() == 0))) {
/* 1543:2296 */       newVector.addElement("Load model");
/* 1544:     */     }
/* 1545:2298 */     return newVector.elements();
/* 1546:     */   }
/* 1547:     */   
/* 1548:     */   public void performRequest(String request)
/* 1549:     */   {
/* 1550:2309 */     if (request.compareTo("Stop") == 0) {
/* 1551:2310 */       stop();
/* 1552:2311 */     } else if (request.compareTo("Save model") == 0) {
/* 1553:2312 */       saveModel();
/* 1554:2313 */     } else if (request.compareTo("Load model") == 0) {
/* 1555:2314 */       loadModel();
/* 1556:     */     } else {
/* 1557:2316 */       throw new IllegalArgumentException(request + " not supported (Classifier)");
/* 1558:     */     }
/* 1559:     */   }
/* 1560:     */   
/* 1561:     */   public boolean eventGeneratable(EventSetDescriptor esd)
/* 1562:     */   {
/* 1563:2329 */     String eventName = esd.getName();
/* 1564:2330 */     return eventGeneratable(eventName);
/* 1565:     */   }
/* 1566:     */   
/* 1567:     */   private boolean generatableEvent(String eventName)
/* 1568:     */   {
/* 1569:2339 */     if ((eventName.compareTo("graph") == 0) || (eventName.compareTo("text") == 0) || (eventName.compareTo("batchClassifier") == 0) || (eventName.compareTo("incrementalClassifier") == 0) || (eventName.compareTo("configuration") == 0)) {
/* 1570:2343 */       return true;
/* 1571:     */     }
/* 1572:2345 */     return false;
/* 1573:     */   }
/* 1574:     */   
/* 1575:     */   public boolean eventGeneratable(String eventName)
/* 1576:     */   {
/* 1577:2358 */     if (!generatableEvent(eventName)) {
/* 1578:2359 */       return false;
/* 1579:     */     }
/* 1580:2361 */     if (eventName.compareTo("graph") == 0)
/* 1581:     */     {
/* 1582:2363 */       if (!(this.m_ClassifierTemplate instanceof Drawable)) {
/* 1583:2364 */         return false;
/* 1584:     */       }
/* 1585:2368 */       if (!this.m_listenees.containsKey("trainingSet")) {
/* 1586:2369 */         return false;
/* 1587:     */       }
/* 1588:2373 */       Object source = this.m_listenees.get("trainingSet");
/* 1589:2374 */       if (((source instanceof EventConstraints)) && 
/* 1590:2375 */         (!((EventConstraints)source).eventGeneratable("trainingSet"))) {
/* 1591:2376 */         return false;
/* 1592:     */       }
/* 1593:     */     }
/* 1594:2381 */     if (eventName.compareTo("batchClassifier") == 0)
/* 1595:     */     {
/* 1596:2387 */       if ((!this.m_listenees.containsKey("testSet")) && (!this.m_listenees.containsKey("trainingSet"))) {
/* 1597:2389 */         return false;
/* 1598:     */       }
/* 1599:2391 */       Object source = this.m_listenees.get("testSet");
/* 1600:2392 */       if (((source instanceof EventConstraints)) && 
/* 1601:2393 */         (!((EventConstraints)source).eventGeneratable("testSet"))) {
/* 1602:2394 */         return false;
/* 1603:     */       }
/* 1604:     */     }
/* 1605:2405 */     if (eventName.compareTo("text") == 0)
/* 1606:     */     {
/* 1607:2406 */       if ((!this.m_listenees.containsKey("trainingSet")) && (!this.m_listenees.containsKey("instance"))) {
/* 1608:2408 */         return false;
/* 1609:     */       }
/* 1610:2410 */       Object source = this.m_listenees.get("trainingSet");
/* 1611:2411 */       if ((source != null) && ((source instanceof EventConstraints)) && 
/* 1612:2412 */         (!((EventConstraints)source).eventGeneratable("trainingSet"))) {
/* 1613:2413 */         return false;
/* 1614:     */       }
/* 1615:2416 */       source = this.m_listenees.get("instance");
/* 1616:2417 */       if ((source != null) && ((source instanceof EventConstraints)) && 
/* 1617:2418 */         (!((EventConstraints)source).eventGeneratable("instance"))) {
/* 1618:2419 */         return false;
/* 1619:     */       }
/* 1620:     */     }
/* 1621:2424 */     if (eventName.compareTo("incrementalClassifier") == 0)
/* 1622:     */     {
/* 1623:2429 */       if (!this.m_listenees.containsKey("instance")) {
/* 1624:2430 */         return false;
/* 1625:     */       }
/* 1626:2432 */       Object source = this.m_listenees.get("instance");
/* 1627:2433 */       if (((source instanceof EventConstraints)) && 
/* 1628:2434 */         (!((EventConstraints)source).eventGeneratable("instance"))) {
/* 1629:2435 */         return false;
/* 1630:     */       }
/* 1631:     */     }
/* 1632:2440 */     if ((eventName.equals("configuration")) && (this.m_Classifier == null)) {
/* 1633:2441 */       return false;
/* 1634:     */     }
/* 1635:2444 */     return true;
/* 1636:     */   }
/* 1637:     */   
/* 1638:     */   public boolean isBusy()
/* 1639:     */   {
/* 1640:2455 */     if ((this.m_executorPool == null) || ((this.m_executorPool.getQueue().size() == 0) && (this.m_executorPool.getActiveCount() == 0) && (this.m_state == IDLE))) {
/* 1641:2458 */       return false;
/* 1642:     */     }
/* 1643:2464 */     return true;
/* 1644:     */   }
/* 1645:     */   
/* 1646:     */   private String statusMessagePrefix()
/* 1647:     */   {
/* 1648:2468 */     return getCustomName() + "$" + hashCode() + "|" + (((this.m_ClassifierTemplate instanceof OptionHandler)) && (Utils.joinOptions(((OptionHandler)this.m_ClassifierTemplate).getOptions()).length() > 0) ? Utils.joinOptions(((OptionHandler)this.m_ClassifierTemplate).getOptions()) + "|" : "");
/* 1649:     */   }
/* 1650:     */   
/* 1651:     */   public void setEnvironment(Environment env)
/* 1652:     */   {
/* 1653:2485 */     this.m_env = env;
/* 1654:     */   }
/* 1655:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.Classifier
 * JD-Core Version:    0.7.0.1
 */