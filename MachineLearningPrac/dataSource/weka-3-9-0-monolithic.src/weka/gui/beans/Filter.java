/*    1:     */ package weka.gui.beans;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.beans.EventSetDescriptor;
/*    5:     */ import java.io.PrintStream;
/*    6:     */ import java.io.Serializable;
/*    7:     */ import java.util.Enumeration;
/*    8:     */ import java.util.EventObject;
/*    9:     */ import java.util.Hashtable;
/*   10:     */ import java.util.Vector;
/*   11:     */ import javax.swing.JPanel;
/*   12:     */ import weka.core.Attribute;
/*   13:     */ import weka.core.Instance;
/*   14:     */ import weka.core.Instances;
/*   15:     */ import weka.core.OptionHandler;
/*   16:     */ import weka.core.SerializedObject;
/*   17:     */ import weka.core.Utils;
/*   18:     */ import weka.filters.AllFilter;
/*   19:     */ import weka.filters.StreamableFilter;
/*   20:     */ import weka.filters.SupervisedFilter;
/*   21:     */ import weka.gui.Logger;
/*   22:     */ 
/*   23:     */ public class Filter
/*   24:     */   extends JPanel
/*   25:     */   implements BeanCommon, Visible, WekaWrapper, Serializable, UserRequestAcceptor, TrainingSetListener, TestSetListener, TrainingSetProducer, TestSetProducer, DataSource, DataSourceListener, InstanceListener, EventConstraints, ConfigurationProducer
/*   26:     */ {
/*   27:     */   private static final long serialVersionUID = 8249759470189439321L;
/*   28:  58 */   protected BeanVisual m_visual = new BeanVisual("Filter", "weka/gui/beans/icons/DefaultFilter.gif", "weka/gui/beans/icons/DefaultFilter_animated.gif");
/*   29:  61 */   private static int IDLE = 0;
/*   30:  62 */   private static int FILTERING_TRAINING = 1;
/*   31:  63 */   private static int FILTERING_TEST = 2;
/*   32:  64 */   private int m_state = IDLE;
/*   33:  66 */   protected transient Thread m_filterThread = null;
/*   34:     */   private transient Instances m_trainingSet;
/*   35:     */   private transient Instances m_testingSet;
/*   36:     */   protected String m_globalInfo;
/*   37:  79 */   private final Hashtable<String, Object> m_listenees = new Hashtable();
/*   38:  84 */   private final Vector<TrainingSetListener> m_trainingListeners = new Vector();
/*   39:  89 */   private final Vector<TestSetListener> m_testListeners = new Vector();
/*   40:  94 */   private final Vector<InstanceListener> m_instanceListeners = new Vector();
/*   41:  99 */   private final Vector<DataSourceListener> m_dataListeners = new Vector();
/*   42: 104 */   private weka.filters.Filter m_Filter = new AllFilter();
/*   43: 109 */   private final InstanceEvent m_ie = new InstanceEvent(this);
/*   44: 114 */   private transient Logger m_log = null;
/*   45:     */   private transient int m_instanceCount;
/*   46:     */   
/*   47:     */   public String globalInfo()
/*   48:     */   {
/*   49: 127 */     return this.m_globalInfo;
/*   50:     */   }
/*   51:     */   
/*   52:     */   public Filter()
/*   53:     */   {
/*   54: 131 */     setLayout(new BorderLayout());
/*   55: 132 */     add(this.m_visual, "Center");
/*   56: 133 */     setFilter(this.m_Filter);
/*   57:     */   }
/*   58:     */   
/*   59:     */   public void setCustomName(String name)
/*   60:     */   {
/*   61: 143 */     this.m_visual.setText(name);
/*   62:     */   }
/*   63:     */   
/*   64:     */   public String getCustomName()
/*   65:     */   {
/*   66: 153 */     return this.m_visual.getText();
/*   67:     */   }
/*   68:     */   
/*   69:     */   public void setFilter(weka.filters.Filter c)
/*   70:     */   {
/*   71: 162 */     boolean loadImages = true;
/*   72: 163 */     if (c.getClass().getName().compareTo(this.m_Filter.getClass().getName()) == 0) {
/*   73: 164 */       loadImages = false;
/*   74:     */     }
/*   75: 166 */     this.m_Filter = c;
/*   76: 167 */     String filterName = c.getClass().toString();
/*   77: 168 */     filterName = filterName.substring(filterName.indexOf('.') + 1, filterName.length());
/*   78: 170 */     if (loadImages) {
/*   79: 171 */       if ((this.m_Filter instanceof Visible)) {
/*   80: 172 */         this.m_visual = ((Visible)this.m_Filter).getVisual();
/*   81: 174 */       } else if (!this.m_visual.loadIcons("weka/gui/beans/icons/" + filterName + ".gif", "weka/gui/beans/icons/" + filterName + "_animated.gif")) {
/*   82: 176 */         useDefaultVisual();
/*   83:     */       }
/*   84:     */     }
/*   85: 180 */     this.m_visual.setText(filterName.substring(filterName.lastIndexOf('.') + 1, filterName.length()));
/*   86: 183 */     if (((this.m_Filter instanceof LogWriter)) && (this.m_log != null)) {
/*   87: 184 */       ((LogWriter)this.m_Filter).setLog(this.m_log);
/*   88:     */     }
/*   89: 187 */     if ((!(this.m_Filter instanceof StreamableFilter)) && (this.m_listenees.containsKey("instance"))) {
/*   90: 189 */       if (this.m_log != null)
/*   91:     */       {
/*   92: 190 */         this.m_log.logMessage("[Filter] " + statusMessagePrefix() + " WARNING : " + this.m_Filter.getClass().getName() + " is not an incremental filter");
/*   93:     */         
/*   94: 192 */         this.m_log.statusMessage(statusMessagePrefix() + "WARNING: Not an incremental filter.");
/*   95:     */       }
/*   96:     */     }
/*   97: 198 */     this.m_globalInfo = KnowledgeFlowApp.getGlobalInfo(this.m_Filter);
/*   98:     */   }
/*   99:     */   
/*  100:     */   public weka.filters.Filter getFilter()
/*  101:     */   {
/*  102: 202 */     return this.m_Filter;
/*  103:     */   }
/*  104:     */   
/*  105:     */   public void setWrappedAlgorithm(Object algorithm)
/*  106:     */   {
/*  107: 214 */     if (!(algorithm instanceof weka.filters.Filter)) {
/*  108: 215 */       throw new IllegalArgumentException(algorithm.getClass() + " : incorrect " + "type of algorithm (Filter)");
/*  109:     */     }
/*  110: 218 */     setFilter((weka.filters.Filter)algorithm);
/*  111:     */   }
/*  112:     */   
/*  113:     */   public Object getWrappedAlgorithm()
/*  114:     */   {
/*  115: 228 */     return getFilter();
/*  116:     */   }
/*  117:     */   
/*  118:     */   public void acceptTrainingSet(TrainingSetEvent e)
/*  119:     */   {
/*  120: 238 */     processTrainingOrDataSourceEvents(e);
/*  121:     */   }
/*  122:     */   
/*  123: 241 */   private boolean m_structurePassedOn = false;
/*  124:     */   
/*  125:     */   public void acceptInstance(InstanceEvent e)
/*  126:     */   {
/*  127: 251 */     if (this.m_filterThread != null)
/*  128:     */     {
/*  129: 252 */       String messg = "[Filter] " + statusMessagePrefix() + " is currently batch processing!";
/*  130: 254 */       if (this.m_log != null)
/*  131:     */       {
/*  132: 255 */         this.m_log.logMessage(messg);
/*  133: 256 */         this.m_log.statusMessage(statusMessagePrefix() + "WARNING: Filter is currently batch processing.");
/*  134:     */       }
/*  135:     */       else
/*  136:     */       {
/*  137: 259 */         System.err.println(messg);
/*  138:     */       }
/*  139: 261 */       return;
/*  140:     */     }
/*  141: 263 */     if (!(this.m_Filter instanceof StreamableFilter))
/*  142:     */     {
/*  143: 264 */       stop();
/*  144: 265 */       if (this.m_log != null)
/*  145:     */       {
/*  146: 266 */         this.m_log.logMessage("[Filter] " + statusMessagePrefix() + " ERROR : " + this.m_Filter.getClass().getName() + "can't process streamed instances; can't continue");
/*  147:     */         
/*  148:     */ 
/*  149: 269 */         this.m_log.statusMessage(statusMessagePrefix() + "ERROR: Can't process streamed instances; can't continue.");
/*  150:     */       }
/*  151: 272 */       return;
/*  152:     */     }
/*  153: 274 */     if (e.getStatus() == 0)
/*  154:     */     {
/*  155:     */       try
/*  156:     */       {
/*  157: 276 */         this.m_instanceCount = 0;
/*  158:     */         
/*  159:     */ 
/*  160: 279 */         Instances dataset = e.getStructure();
/*  161: 280 */         if ((this.m_Filter instanceof SupervisedFilter)) {
/*  162: 282 */           if (dataset.classIndex() < 0) {
/*  163: 283 */             dataset.setClassIndex(dataset.numAttributes() - 1);
/*  164:     */           }
/*  165:     */         }
/*  166: 287 */         this.m_Filter.setInputFormat(dataset);
/*  167:     */         
/*  168:     */ 
/*  169:     */ 
/*  170: 291 */         this.m_structurePassedOn = false;
/*  171:     */         try
/*  172:     */         {
/*  173: 293 */           if (this.m_Filter.isOutputFormatDefined())
/*  174:     */           {
/*  175: 296 */             this.m_ie.setStructure(new Instances(this.m_Filter.getOutputFormat(), 0));
/*  176: 297 */             this.m_ie.m_formatNotificationOnly = e.m_formatNotificationOnly;
/*  177: 298 */             notifyInstanceListeners(this.m_ie);
/*  178: 299 */             this.m_structurePassedOn = true;
/*  179:     */           }
/*  180:     */         }
/*  181:     */         catch (Exception ex)
/*  182:     */         {
/*  183: 302 */           stop();
/*  184: 303 */           if (this.m_log != null)
/*  185:     */           {
/*  186: 304 */             this.m_log.logMessage("[Filter] " + statusMessagePrefix() + " Error in obtaining post-filter structure. " + ex.getMessage());
/*  187:     */             
/*  188:     */ 
/*  189:     */ 
/*  190: 308 */             this.m_log.statusMessage(statusMessagePrefix() + "ERROR (See log for details).");
/*  191:     */           }
/*  192:     */           else
/*  193:     */           {
/*  194: 311 */             System.err.println("[Filter] " + statusMessagePrefix() + " Error in obtaining post-filter structure");
/*  195:     */           }
/*  196:     */         }
/*  197:     */       }
/*  198:     */       catch (Exception ex)
/*  199:     */       {
/*  200: 316 */         ex.printStackTrace();
/*  201:     */       }
/*  202: 318 */       return;
/*  203:     */     }
/*  204: 321 */     if ((e.getStatus() == 2) || (e.getInstance() == null))
/*  205:     */     {
/*  206:     */       try
/*  207:     */       {
/*  208: 325 */         if (this.m_log != null) {
/*  209: 326 */           this.m_log.statusMessage(statusMessagePrefix() + "Stream finished.");
/*  210:     */         }
/*  211: 328 */         if ((e.getInstance() != null) && 
/*  212: 329 */           (this.m_Filter.input(e.getInstance())))
/*  213:     */         {
/*  214: 330 */           Instance filteredInstance = this.m_Filter.output();
/*  215: 331 */           if (filteredInstance != null)
/*  216:     */           {
/*  217: 332 */             if (!this.m_structurePassedOn)
/*  218:     */             {
/*  219: 334 */               this.m_ie.setStructure(new Instances(filteredInstance.dataset(), 0));
/*  220: 335 */               notifyInstanceListeners(this.m_ie);
/*  221: 336 */               this.m_structurePassedOn = true;
/*  222:     */             }
/*  223: 339 */             this.m_ie.setInstance(filteredInstance);
/*  224: 344 */             if ((this.m_Filter.batchFinished()) && (this.m_Filter.numPendingOutput() > 0)) {
/*  225: 345 */               this.m_ie.setStatus(1);
/*  226:     */             } else {
/*  227: 347 */               this.m_ie.setStatus(e.getStatus());
/*  228:     */             }
/*  229: 349 */             notifyInstanceListeners(this.m_ie);
/*  230:     */           }
/*  231:     */         }
/*  232: 353 */         if (this.m_log != null) {
/*  233: 354 */           this.m_log.statusMessage(statusMessagePrefix() + "Finished.");
/*  234:     */         }
/*  235:     */       }
/*  236:     */       catch (Exception ex)
/*  237:     */       {
/*  238: 357 */         stop();
/*  239: 358 */         if (this.m_log != null)
/*  240:     */         {
/*  241: 359 */           this.m_log.logMessage("[Filter] " + statusMessagePrefix() + ex.getMessage());
/*  242:     */           
/*  243: 361 */           this.m_log.statusMessage(statusMessagePrefix() + "ERROR (See log for details).");
/*  244:     */         }
/*  245: 364 */         ex.printStackTrace();
/*  246:     */       }
/*  247:     */       try
/*  248:     */       {
/*  249: 369 */         if ((this.m_Filter.batchFinished()) && (this.m_Filter.numPendingOutput() > 0))
/*  250:     */         {
/*  251: 370 */           if (this.m_log != null) {
/*  252: 371 */             this.m_log.statusMessage(statusMessagePrefix() + "Passing on pending instances...");
/*  253:     */           }
/*  254: 374 */           Instance filteredInstance = this.m_Filter.output();
/*  255: 375 */           if (filteredInstance != null)
/*  256:     */           {
/*  257: 376 */             if (!this.m_structurePassedOn)
/*  258:     */             {
/*  259: 378 */               this.m_ie.setStructure((Instances)new SerializedObject(filteredInstance.dataset()).getObject());
/*  260:     */               
/*  261: 380 */               notifyInstanceListeners(this.m_ie);
/*  262: 381 */               this.m_structurePassedOn = true;
/*  263:     */             }
/*  264: 384 */             this.m_ie.setInstance(filteredInstance);
/*  265:     */             
/*  266:     */ 
/*  267: 387 */             this.m_ie.setStatus(1);
/*  268: 388 */             notifyInstanceListeners(this.m_ie);
/*  269:     */           }
/*  270: 390 */           while (this.m_Filter.numPendingOutput() > 0)
/*  271:     */           {
/*  272: 391 */             filteredInstance = this.m_Filter.output();
/*  273: 393 */             if (filteredInstance.dataset().checkForStringAttributes()) {
/*  274: 394 */               for (int i = 0; i < filteredInstance.dataset().numAttributes(); i++) {
/*  275: 395 */                 if ((filteredInstance.dataset().attribute(i).isString()) && (!filteredInstance.isMissing(i)))
/*  276:     */                 {
/*  277: 397 */                   String val = filteredInstance.stringValue(i);
/*  278:     */                   
/*  279: 399 */                   this.m_ie.getStructure().attribute(i).setStringValue(val);
/*  280: 400 */                   filteredInstance.setValue(i, 0.0D);
/*  281:     */                 }
/*  282:     */               }
/*  283:     */             }
/*  284: 404 */             filteredInstance.setDataset(this.m_ie.getStructure());
/*  285:     */             
/*  286: 406 */             this.m_ie.setInstance(filteredInstance);
/*  287: 408 */             if (this.m_Filter.numPendingOutput() == 0) {
/*  288: 409 */               this.m_ie.setStatus(2);
/*  289:     */             } else {
/*  290: 411 */               this.m_ie.setStatus(1);
/*  291:     */             }
/*  292: 413 */             notifyInstanceListeners(this.m_ie);
/*  293:     */           }
/*  294: 415 */           if (this.m_log != null) {
/*  295: 416 */             this.m_log.statusMessage(statusMessagePrefix() + "Finished.");
/*  296:     */           }
/*  297:     */         }
/*  298:     */       }
/*  299:     */       catch (Exception ex)
/*  300:     */       {
/*  301: 420 */         stop();
/*  302: 421 */         if (this.m_log != null)
/*  303:     */         {
/*  304: 422 */           this.m_log.logMessage("[Filter] " + statusMessagePrefix() + ex.toString());
/*  305: 423 */           this.m_log.statusMessage(statusMessagePrefix() + "ERROR (See log for details.");
/*  306:     */         }
/*  307: 426 */         ex.printStackTrace();
/*  308:     */       }
/*  309:     */     }
/*  310:     */     else
/*  311:     */     {
/*  312:     */       try
/*  313:     */       {
/*  314: 431 */         if (!this.m_Filter.input(e.getInstance())) {
/*  315: 440 */           return;
/*  316:     */         }
/*  317: 444 */         Instance filteredInstance = this.m_Filter.output();
/*  318: 445 */         if (filteredInstance == null) {
/*  319: 446 */           return;
/*  320:     */         }
/*  321: 448 */         this.m_instanceCount += 1;
/*  322: 450 */         if (!this.m_structurePassedOn)
/*  323:     */         {
/*  324: 452 */           this.m_ie.setStructure(new Instances(filteredInstance.dataset(), 0));
/*  325: 453 */           notifyInstanceListeners(this.m_ie);
/*  326: 454 */           this.m_structurePassedOn = true;
/*  327:     */         }
/*  328: 457 */         filteredInstance.setDataset(this.m_ie.getStructure());
/*  329: 459 */         if (filteredInstance.dataset().checkForStringAttributes()) {
/*  330: 460 */           for (int i = 0; i < filteredInstance.dataset().numAttributes(); i++) {
/*  331: 461 */             if ((filteredInstance.dataset().attribute(i).isString()) && (!filteredInstance.isMissing(i)))
/*  332:     */             {
/*  333: 463 */               String val = filteredInstance.stringValue(i);
/*  334:     */               
/*  335: 465 */               filteredInstance.dataset().attribute(i).setStringValue(val);
/*  336: 466 */               filteredInstance.setValue(i, 0.0D);
/*  337:     */             }
/*  338:     */           }
/*  339:     */         }
/*  340: 471 */         this.m_ie.setInstance(filteredInstance);
/*  341: 472 */         this.m_ie.setStatus(e.getStatus());
/*  342: 474 */         if ((this.m_log != null) && (this.m_instanceCount % 10000 == 0)) {
/*  343: 475 */           this.m_log.statusMessage(statusMessagePrefix() + "Received " + this.m_instanceCount + " instances.");
/*  344:     */         }
/*  345: 478 */         notifyInstanceListeners(this.m_ie);
/*  346:     */       }
/*  347:     */       catch (Exception ex)
/*  348:     */       {
/*  349: 480 */         stop();
/*  350: 481 */         if (this.m_log != null)
/*  351:     */         {
/*  352: 482 */           this.m_log.logMessage("[Filter] " + statusMessagePrefix() + ex.toString());
/*  353: 483 */           this.m_log.statusMessage(statusMessagePrefix() + "ERROR (See log for details).");
/*  354:     */         }
/*  355: 486 */         ex.printStackTrace();
/*  356:     */       }
/*  357:     */     }
/*  358:     */   }
/*  359:     */   
/*  360:     */   private void processTrainingOrDataSourceEvents(final EventObject e)
/*  361:     */   {
/*  362: 492 */     boolean structureOnly = false;
/*  363: 493 */     if ((e instanceof DataSetEvent))
/*  364:     */     {
/*  365: 494 */       structureOnly = ((DataSetEvent)e).isStructureOnly();
/*  366: 495 */       if (structureOnly) {
/*  367: 496 */         notifyDataOrTrainingListeners(e);
/*  368:     */       }
/*  369:     */     }
/*  370: 499 */     if ((e instanceof TrainingSetEvent))
/*  371:     */     {
/*  372: 500 */       structureOnly = ((TrainingSetEvent)e).isStructureOnly();
/*  373: 501 */       if (structureOnly) {
/*  374: 502 */         notifyDataOrTrainingListeners(e);
/*  375:     */       }
/*  376:     */     }
/*  377: 505 */     if ((structureOnly) && (!(this.m_Filter instanceof StreamableFilter))) {
/*  378: 506 */       return;
/*  379:     */     }
/*  380: 509 */     if (this.m_filterThread == null) {
/*  381:     */       try
/*  382:     */       {
/*  383: 511 */         if (this.m_state == IDLE)
/*  384:     */         {
/*  385: 512 */           synchronized (this)
/*  386:     */           {
/*  387: 513 */             this.m_state = FILTERING_TRAINING;
/*  388:     */           }
/*  389: 515 */           this.m_trainingSet = ((e instanceof TrainingSetEvent) ? ((TrainingSetEvent)e).getTrainingSet() : ((DataSetEvent)e).getDataSet());
/*  390:     */           
/*  391:     */ 
/*  392:     */ 
/*  393: 519 */           this.m_filterThread = new Thread()
/*  394:     */           {
/*  395:     */             public void run()
/*  396:     */             {
/*  397:     */               try
/*  398:     */               {
/*  399: 524 */                 if (Filter.this.m_trainingSet != null)
/*  400:     */                 {
/*  401: 525 */                   Filter.this.m_visual.setAnimated();
/*  402: 527 */                   if (Filter.this.m_log != null) {
/*  403: 528 */                     Filter.this.m_log.statusMessage(Filter.this.statusMessagePrefix() + "Filtering training data (" + Filter.this.m_trainingSet.relationName() + ")");
/*  404:     */                   }
/*  405: 532 */                   Filter.this.m_Filter.setInputFormat(Filter.this.m_trainingSet);
/*  406: 533 */                   Instances filteredData = weka.filters.Filter.useFilter(Filter.this.m_trainingSet, Filter.this.m_Filter);
/*  407:     */                   
/*  408:     */ 
/*  409: 536 */                   Filter.this.m_visual.setStatic();
/*  410:     */                   EventObject ne;
/*  411: 538 */                   if ((e instanceof TrainingSetEvent))
/*  412:     */                   {
/*  413: 539 */                     EventObject ne = new TrainingSetEvent(Filter.this, filteredData);
/*  414:     */                     
/*  415: 541 */                     ((TrainingSetEvent)ne).m_setNumber = ((TrainingSetEvent)e).m_setNumber;
/*  416: 542 */                     ((TrainingSetEvent)ne).m_maxSetNumber = ((TrainingSetEvent)e).m_maxSetNumber;
/*  417:     */                   }
/*  418:     */                   else
/*  419:     */                   {
/*  420: 544 */                     ne = new DataSetEvent(Filter.this, filteredData);
/*  421:     */                   }
/*  422: 548 */                   Filter.this.notifyDataOrTrainingListeners(ne);
/*  423:     */                 }
/*  424:     */               }
/*  425:     */               catch (Exception ex)
/*  426:     */               {
/*  427: 551 */                 ex.printStackTrace();
/*  428: 552 */                 if (Filter.this.m_log != null)
/*  429:     */                 {
/*  430: 553 */                   Filter.this.m_log.logMessage("[Filter] " + Filter.this.statusMessagePrefix() + ex.getMessage());
/*  431:     */                   
/*  432: 555 */                   Filter.this.m_log.statusMessage(Filter.this.statusMessagePrefix() + "ERROR (See log for details).");
/*  433:     */                 }
/*  434: 559 */                 Filter.this.stop();
/*  435:     */               }
/*  436:     */               finally
/*  437:     */               {
/*  438: 562 */                 Filter.this.m_visual.setStatic();
/*  439: 563 */                 Filter.this.m_state = Filter.IDLE;
/*  440: 564 */                 if (isInterrupted())
/*  441:     */                 {
/*  442: 565 */                   Filter.this.m_trainingSet = null;
/*  443: 566 */                   if (Filter.this.m_log != null)
/*  444:     */                   {
/*  445: 567 */                     Filter.this.m_log.logMessage("[Filter] " + Filter.this.statusMessagePrefix() + " training set interrupted!");
/*  446:     */                     
/*  447: 569 */                     Filter.this.m_log.statusMessage(Filter.this.statusMessagePrefix() + "INTERRUPTED");
/*  448:     */                   }
/*  449:     */                 }
/*  450: 572 */                 else if (Filter.this.m_log != null)
/*  451:     */                 {
/*  452: 573 */                   Filter.this.m_log.statusMessage(Filter.this.statusMessagePrefix() + "Finished.");
/*  453:     */                 }
/*  454: 576 */                 Filter.this.block(false);
/*  455: 577 */                 Filter.this.m_filterThread = null;
/*  456:     */               }
/*  457:     */             }
/*  458: 580 */           };
/*  459: 581 */           this.m_filterThread.setPriority(1);
/*  460: 582 */           this.m_filterThread.start();
/*  461: 583 */           block(true);
/*  462: 584 */           this.m_filterThread = null;
/*  463: 585 */           this.m_state = IDLE;
/*  464:     */         }
/*  465:     */       }
/*  466:     */       catch (Exception ex)
/*  467:     */       {
/*  468: 588 */         ex.printStackTrace();
/*  469:     */       }
/*  470:     */     }
/*  471:     */   }
/*  472:     */   
/*  473:     */   public void acceptTestSet(final TestSetEvent e)
/*  474:     */   {
/*  475: 600 */     if (e.isStructureOnly()) {
/*  476: 601 */       notifyTestListeners(e);
/*  477:     */     }
/*  478: 603 */     if ((this.m_trainingSet != null) && (this.m_trainingSet.equalHeaders(e.getTestSet())) && (this.m_filterThread == null)) {
/*  479:     */       try
/*  480:     */       {
/*  481: 606 */         if (this.m_state == IDLE) {
/*  482: 607 */           this.m_state = FILTERING_TEST;
/*  483:     */         }
/*  484: 609 */         this.m_testingSet = e.getTestSet();
/*  485:     */         
/*  486: 611 */         this.m_filterThread = new Thread()
/*  487:     */         {
/*  488:     */           public void run()
/*  489:     */           {
/*  490:     */             try
/*  491:     */             {
/*  492: 616 */               if (Filter.this.m_testingSet != null)
/*  493:     */               {
/*  494: 617 */                 Filter.this.m_visual.setAnimated();
/*  495: 619 */                 if (Filter.this.m_log != null) {
/*  496: 620 */                   Filter.this.m_log.statusMessage(Filter.this.statusMessagePrefix() + "Filtering test data (" + Filter.this.m_testingSet.relationName() + ")");
/*  497:     */                 }
/*  498: 624 */                 Instances filteredTest = weka.filters.Filter.useFilter(Filter.this.m_testingSet, Filter.this.m_Filter);
/*  499:     */                 
/*  500:     */ 
/*  501: 627 */                 Filter.this.m_visual.setStatic();
/*  502: 628 */                 TestSetEvent ne = new TestSetEvent(Filter.this, filteredTest);
/*  503:     */                 
/*  504: 630 */                 ne.m_setNumber = e.m_setNumber;
/*  505: 631 */                 ne.m_maxSetNumber = e.m_maxSetNumber;
/*  506: 632 */                 Filter.this.notifyTestListeners(ne);
/*  507:     */               }
/*  508:     */             }
/*  509:     */             catch (Exception ex)
/*  510:     */             {
/*  511: 635 */               ex.printStackTrace();
/*  512: 636 */               if (Filter.this.m_log != null)
/*  513:     */               {
/*  514: 637 */                 Filter.this.m_log.logMessage("[Filter] " + Filter.this.statusMessagePrefix() + ex.getMessage());
/*  515:     */                 
/*  516: 639 */                 Filter.this.m_log.statusMessage(Filter.this.statusMessagePrefix() + "ERROR (See log for details).");
/*  517:     */               }
/*  518: 642 */               Filter.this.stop();
/*  519:     */             }
/*  520:     */             finally
/*  521:     */             {
/*  522: 645 */               Filter.this.m_visual.setStatic();
/*  523: 646 */               Filter.this.m_state = Filter.IDLE;
/*  524: 647 */               if (isInterrupted())
/*  525:     */               {
/*  526: 648 */                 Filter.this.m_trainingSet = null;
/*  527: 649 */                 if (Filter.this.m_log != null)
/*  528:     */                 {
/*  529: 650 */                   Filter.this.m_log.logMessage("[Filter] " + Filter.this.statusMessagePrefix() + " test set interrupted!");
/*  530:     */                   
/*  531: 652 */                   Filter.this.m_log.statusMessage(Filter.this.statusMessagePrefix() + "INTERRUPTED");
/*  532:     */                 }
/*  533:     */               }
/*  534: 656 */               else if (Filter.this.m_log != null)
/*  535:     */               {
/*  536: 657 */                 Filter.this.m_log.statusMessage(Filter.this.statusMessagePrefix() + "Finished.");
/*  537:     */               }
/*  538: 660 */               Filter.this.block(false);
/*  539: 661 */               Filter.this.m_filterThread = null;
/*  540:     */             }
/*  541:     */           }
/*  542: 664 */         };
/*  543: 665 */         this.m_filterThread.setPriority(1);
/*  544: 666 */         this.m_filterThread.start();
/*  545: 667 */         block(true);
/*  546: 668 */         this.m_filterThread = null;
/*  547: 669 */         this.m_state = IDLE;
/*  548:     */       }
/*  549:     */       catch (Exception ex)
/*  550:     */       {
/*  551: 671 */         ex.printStackTrace();
/*  552:     */       }
/*  553:     */     }
/*  554:     */   }
/*  555:     */   
/*  556:     */   public void acceptDataSet(DataSetEvent e)
/*  557:     */   {
/*  558: 683 */     processTrainingOrDataSourceEvents(e);
/*  559:     */   }
/*  560:     */   
/*  561:     */   public void setVisual(BeanVisual newVisual)
/*  562:     */   {
/*  563: 693 */     this.m_visual = newVisual;
/*  564:     */   }
/*  565:     */   
/*  566:     */   public BeanVisual getVisual()
/*  567:     */   {
/*  568: 703 */     return this.m_visual;
/*  569:     */   }
/*  570:     */   
/*  571:     */   public void useDefaultVisual()
/*  572:     */   {
/*  573: 711 */     this.m_visual.loadIcons("weka/gui/beans/icons/DefaultFilter.gif", "weka/gui/beans/icons/DefaultFilter_animated.gif");
/*  574:     */   }
/*  575:     */   
/*  576:     */   public synchronized void addTrainingSetListener(TrainingSetListener tsl)
/*  577:     */   {
/*  578: 722 */     this.m_trainingListeners.addElement(tsl);
/*  579:     */   }
/*  580:     */   
/*  581:     */   public synchronized void removeTrainingSetListener(TrainingSetListener tsl)
/*  582:     */   {
/*  583: 732 */     this.m_trainingListeners.removeElement(tsl);
/*  584:     */   }
/*  585:     */   
/*  586:     */   public synchronized void addTestSetListener(TestSetListener tsl)
/*  587:     */   {
/*  588: 742 */     this.m_testListeners.addElement(tsl);
/*  589:     */   }
/*  590:     */   
/*  591:     */   public synchronized void removeTestSetListener(TestSetListener tsl)
/*  592:     */   {
/*  593: 752 */     this.m_testListeners.removeElement(tsl);
/*  594:     */   }
/*  595:     */   
/*  596:     */   public synchronized void addDataSourceListener(DataSourceListener dsl)
/*  597:     */   {
/*  598: 762 */     this.m_dataListeners.addElement(dsl);
/*  599:     */   }
/*  600:     */   
/*  601:     */   public synchronized void removeDataSourceListener(DataSourceListener dsl)
/*  602:     */   {
/*  603: 772 */     this.m_dataListeners.remove(dsl);
/*  604:     */   }
/*  605:     */   
/*  606:     */   public synchronized void addInstanceListener(InstanceListener tsl)
/*  607:     */   {
/*  608: 782 */     this.m_instanceListeners.addElement(tsl);
/*  609:     */   }
/*  610:     */   
/*  611:     */   public synchronized void removeInstanceListener(InstanceListener tsl)
/*  612:     */   {
/*  613: 792 */     this.m_instanceListeners.removeElement(tsl);
/*  614:     */   }
/*  615:     */   
/*  616:     */   public synchronized void addConfigurationListener(ConfigurationListener cl) {}
/*  617:     */   
/*  618:     */   public synchronized void removeConfigurationListener(ConfigurationListener cl) {}
/*  619:     */   
/*  620:     */   private void notifyDataOrTrainingListeners(EventObject ce)
/*  621:     */   {
/*  622:     */     Vector<?> l;
/*  623: 819 */     synchronized (this)
/*  624:     */     {
/*  625: 820 */       l = (ce instanceof TrainingSetEvent) ? (Vector)this.m_trainingListeners.clone() : (Vector)this.m_dataListeners.clone();
/*  626:     */     }
/*  627: 823 */     if (l.size() > 0) {
/*  628: 824 */       for (int i = 0; i < l.size(); i++) {
/*  629: 825 */         if ((ce instanceof TrainingSetEvent)) {
/*  630: 826 */           ((TrainingSetListener)l.elementAt(i)).acceptTrainingSet((TrainingSetEvent)ce);
/*  631:     */         } else {
/*  632: 829 */           ((DataSourceListener)l.elementAt(i)).acceptDataSet((DataSetEvent)ce);
/*  633:     */         }
/*  634:     */       }
/*  635:     */     }
/*  636:     */   }
/*  637:     */   
/*  638:     */   private void notifyTestListeners(TestSetEvent ce)
/*  639:     */   {
/*  640:     */     Vector<TestSetListener> l;
/*  641: 839 */     synchronized (this)
/*  642:     */     {
/*  643: 840 */       l = (Vector)this.m_testListeners.clone();
/*  644:     */     }
/*  645: 842 */     if (l.size() > 0) {
/*  646: 843 */       for (int i = 0; i < l.size(); i++) {
/*  647: 844 */         ((TestSetListener)l.elementAt(i)).acceptTestSet(ce);
/*  648:     */       }
/*  649:     */     }
/*  650:     */   }
/*  651:     */   
/*  652:     */   protected void notifyInstanceListeners(InstanceEvent tse)
/*  653:     */   {
/*  654:     */     Vector<InstanceListener> l;
/*  655: 852 */     synchronized (this)
/*  656:     */     {
/*  657: 853 */       l = (Vector)this.m_instanceListeners.clone();
/*  658:     */     }
/*  659: 855 */     if (l.size() > 0) {
/*  660: 856 */       for (int i = 0; i < l.size(); i++) {
/*  661: 859 */         ((InstanceListener)l.elementAt(i)).acceptInstance(tse);
/*  662:     */       }
/*  663:     */     }
/*  664:     */   }
/*  665:     */   
/*  666:     */   public boolean connectionAllowed(String eventName)
/*  667:     */   {
/*  668: 874 */     if (this.m_listenees.containsKey(eventName)) {
/*  669: 875 */       return false;
/*  670:     */     }
/*  671: 887 */     if ((this.m_listenees.containsKey("dataSet")) && ((eventName.compareTo("trainingSet") == 0) || (eventName.compareTo("testSet") == 0) || (eventName.compareTo("instance") == 0))) {
/*  672: 891 */       return false;
/*  673:     */     }
/*  674: 894 */     if (((this.m_listenees.containsKey("trainingSet")) || (this.m_listenees.containsKey("testSet"))) && ((eventName.compareTo("dataSet") == 0) || (eventName.compareTo("instance") == 0))) {
/*  675: 898 */       return false;
/*  676:     */     }
/*  677: 901 */     if ((this.m_listenees.containsKey("instance")) && ((eventName.compareTo("trainingSet") == 0) || (eventName.compareTo("testSet") == 0) || (eventName.compareTo("dataSet") == 0))) {
/*  678: 905 */       return false;
/*  679:     */     }
/*  680: 910 */     if ((eventName.compareTo("instance") == 0) && (!(this.m_Filter instanceof StreamableFilter))) {
/*  681: 912 */       return false;
/*  682:     */     }
/*  683: 914 */     return true;
/*  684:     */   }
/*  685:     */   
/*  686:     */   public boolean connectionAllowed(EventSetDescriptor esd)
/*  687:     */   {
/*  688: 926 */     return connectionAllowed(esd.getName());
/*  689:     */   }
/*  690:     */   
/*  691:     */   public synchronized void connectionNotification(String eventName, Object source)
/*  692:     */   {
/*  693: 940 */     if (connectionAllowed(eventName))
/*  694:     */     {
/*  695: 941 */       this.m_listenees.put(eventName, source);
/*  696: 942 */       if ((this.m_Filter instanceof ConnectionNotificationConsumer)) {
/*  697: 943 */         ((ConnectionNotificationConsumer)this.m_Filter).connectionNotification(eventName, source);
/*  698:     */       }
/*  699:     */     }
/*  700:     */   }
/*  701:     */   
/*  702:     */   public synchronized void disconnectionNotification(String eventName, Object source)
/*  703:     */   {
/*  704: 960 */     if ((this.m_Filter instanceof ConnectionNotificationConsumer)) {
/*  705: 961 */       ((ConnectionNotificationConsumer)this.m_Filter).disconnectionNotification(eventName, source);
/*  706:     */     }
/*  707: 964 */     this.m_listenees.remove(eventName);
/*  708:     */   }
/*  709:     */   
/*  710:     */   private synchronized void block(boolean tf)
/*  711:     */   {
/*  712: 975 */     if (tf) {
/*  713:     */       try
/*  714:     */       {
/*  715: 978 */         if ((this.m_filterThread.isAlive()) && (this.m_state != IDLE)) {
/*  716: 979 */           wait();
/*  717:     */         }
/*  718:     */       }
/*  719:     */       catch (InterruptedException ex) {}
/*  720:     */     } else {
/*  721: 984 */       notifyAll();
/*  722:     */     }
/*  723:     */   }
/*  724:     */   
/*  725:     */   public void stop()
/*  726:     */   {
/*  727: 995 */     Enumeration<String> en = this.m_listenees.keys();
/*  728: 996 */     while (en.hasMoreElements())
/*  729:     */     {
/*  730: 997 */       Object tempO = this.m_listenees.get(en.nextElement());
/*  731: 998 */       if ((tempO instanceof BeanCommon)) {
/*  732: 999 */         ((BeanCommon)tempO).stop();
/*  733:     */       }
/*  734:     */     }
/*  735:1004 */     if (this.m_filterThread != null)
/*  736:     */     {
/*  737:1005 */       this.m_filterThread.interrupt();
/*  738:1006 */       this.m_filterThread.stop();
/*  739:1007 */       this.m_filterThread = null;
/*  740:1008 */       this.m_visual.setStatic();
/*  741:     */     }
/*  742:     */   }
/*  743:     */   
/*  744:     */   public boolean isBusy()
/*  745:     */   {
/*  746:1020 */     return this.m_filterThread != null;
/*  747:     */   }
/*  748:     */   
/*  749:     */   public void setLog(Logger logger)
/*  750:     */   {
/*  751:1030 */     this.m_log = logger;
/*  752:1032 */     if ((this.m_Filter != null) && ((this.m_Filter instanceof LogWriter))) {
/*  753:1033 */       ((LogWriter)this.m_Filter).setLog(this.m_log);
/*  754:     */     }
/*  755:     */   }
/*  756:     */   
/*  757:     */   public Enumeration<String> enumerateRequests()
/*  758:     */   {
/*  759:1044 */     Vector<String> newVector = new Vector(0);
/*  760:1045 */     if (this.m_filterThread != null) {
/*  761:1046 */       newVector.addElement("Stop");
/*  762:     */     }
/*  763:1048 */     return newVector.elements();
/*  764:     */   }
/*  765:     */   
/*  766:     */   public void performRequest(String request)
/*  767:     */   {
/*  768:1059 */     if (request.compareTo("Stop") == 0) {
/*  769:1060 */       stop();
/*  770:     */     } else {
/*  771:1062 */       throw new IllegalArgumentException(request + " not supported (Filter)");
/*  772:     */     }
/*  773:     */   }
/*  774:     */   
/*  775:     */   public boolean eventGeneratable(String eventName)
/*  776:     */   {
/*  777:1077 */     if ((eventName.equals("configuration")) && (this.m_Filter != null)) {
/*  778:1078 */       return true;
/*  779:     */     }
/*  780:1083 */     if (!this.m_listenees.containsKey(eventName)) {
/*  781:1084 */       return false;
/*  782:     */     }
/*  783:1086 */     Object source = this.m_listenees.get(eventName);
/*  784:1087 */     if (((source instanceof EventConstraints)) && 
/*  785:1088 */       (!((EventConstraints)source).eventGeneratable(eventName))) {
/*  786:1089 */       return false;
/*  787:     */     }
/*  788:1092 */     if ((eventName.compareTo("instance") == 0) && 
/*  789:1093 */       (!(this.m_Filter instanceof StreamableFilter))) {
/*  790:1094 */       return false;
/*  791:     */     }
/*  792:1097 */     return true;
/*  793:     */   }
/*  794:     */   
/*  795:     */   private String statusMessagePrefix()
/*  796:     */   {
/*  797:1101 */     return getCustomName() + "$" + hashCode() + "|" + (((this.m_Filter instanceof OptionHandler)) && (Utils.joinOptions(this.m_Filter.getOptions()).length() > 0) ? Utils.joinOptions(this.m_Filter.getOptions()) + "|" : "");
/*  798:     */   }
/*  799:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.Filter
 * JD-Core Version:    0.7.0.1
 */