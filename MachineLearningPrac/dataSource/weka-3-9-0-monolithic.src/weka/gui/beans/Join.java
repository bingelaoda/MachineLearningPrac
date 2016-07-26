/*    1:     */ package weka.gui.beans;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.beans.EventSetDescriptor;
/*    5:     */ import java.io.PrintStream;
/*    6:     */ import java.io.Serializable;
/*    7:     */ import java.util.ArrayList;
/*    8:     */ import java.util.HashMap;
/*    9:     */ import java.util.HashSet;
/*   10:     */ import java.util.LinkedList;
/*   11:     */ import java.util.List;
/*   12:     */ import java.util.Map;
/*   13:     */ import java.util.Queue;
/*   14:     */ import java.util.Set;
/*   15:     */ import java.util.concurrent.atomic.AtomicBoolean;
/*   16:     */ import java.util.concurrent.atomic.AtomicInteger;
/*   17:     */ import javax.swing.JPanel;
/*   18:     */ import weka.core.Attribute;
/*   19:     */ import weka.core.DenseInstance;
/*   20:     */ import weka.core.Environment;
/*   21:     */ import weka.core.EnvironmentHandler;
/*   22:     */ import weka.core.Instance;
/*   23:     */ import weka.core.Instances;
/*   24:     */ import weka.core.Range;
/*   25:     */ import weka.core.SerializedObject;
/*   26:     */ import weka.gui.Logger;
/*   27:     */ 
/*   28:     */ @KFStep(category="Flow", toolTipText="Inner join on one or more key fields")
/*   29:     */ public class Join
/*   30:     */   extends JPanel
/*   31:     */   implements BeanCommon, Visible, Serializable, DataSource, DataSourceListener, TrainingSetListener, TestSetListener, InstanceListener, EventConstraints, StructureProducer, EnvironmentHandler
/*   32:     */ {
/*   33:     */   protected static final String KEY_SPEC_SEPARATOR = "@@KS@@";
/*   34:     */   private static final long serialVersionUID = 398021880509558185L;
/*   35:     */   protected transient Logger m_log;
/*   36:     */   protected transient Environment m_env;
/*   37:     */   protected boolean m_incomingBatchConnections;
/*   38:     */   protected Object m_firstInput;
/*   39:     */   protected Object m_secondInput;
/*   40:     */   protected transient boolean m_firstFinished;
/*   41:     */   protected transient boolean m_secondFinished;
/*   42:  87 */   protected String m_firstInputConnectionType = "";
/*   43:  90 */   protected String m_secondInputConnectionType = "";
/*   44:     */   protected transient Queue<InstanceHolder> m_firstBuffer;
/*   45:     */   protected transient Queue<InstanceHolder> m_secondBuffer;
/*   46:  99 */   protected InstanceEvent m_ie = new InstanceEvent(this);
/*   47:     */   protected transient Instances m_headerOne;
/*   48:     */   protected transient Instances m_headerTwo;
/*   49:     */   protected transient Instances m_mergedHeader;
/*   50:     */   protected transient List<Instances> m_headerPool;
/*   51:     */   protected transient AtomicInteger m_count;
/*   52:     */   protected boolean m_stringAttsPresent;
/*   53:     */   protected boolean m_runningIncrementally;
/*   54:     */   protected int[] m_keyIndexesOne;
/*   55:     */   protected int[] m_keyIndexesTwo;
/*   56: 133 */   protected String m_keySpec = "";
/*   57:     */   protected boolean m_busy;
/*   58:     */   protected AtomicBoolean m_stopRequested;
/*   59:     */   protected Map<String, Integer> m_stringAttIndexesOne;
/*   60:     */   protected Map<String, Integer> m_stringAttIndexesTwo;
/*   61:     */   protected boolean m_firstIsWaiting;
/*   62:     */   protected boolean m_secondIsWaiting;
/*   63: 162 */   protected BeanVisual m_visual = new BeanVisual("Join", "weka/gui/beans/icons/Join.gif", "weka/gui/beans/icons/Join.gif");
/*   64: 166 */   protected ArrayList<DataSourceListener> m_dataListeners = new ArrayList();
/*   65: 170 */   protected ArrayList<InstanceListener> m_instanceListeners = new ArrayList();
/*   66:     */   protected transient StreamThroughput m_throughput;
/*   67:     */   
/*   68:     */   public Join()
/*   69:     */   {
/*   70: 210 */     useDefaultVisual();
/*   71: 211 */     setLayout(new BorderLayout());
/*   72: 212 */     add(this.m_visual, "Center");
/*   73:     */     
/*   74: 214 */     this.m_env = Environment.getSystemWide();
/*   75:     */     
/*   76: 216 */     this.m_stopRequested = new AtomicBoolean(false);
/*   77:     */   }
/*   78:     */   
/*   79:     */   public String globalInfo()
/*   80:     */   {
/*   81: 225 */     return "Performs an inner join on two incoming datasets/instance streams (IMPORTANT: assumes that both datasets are sorted in ascending order of the key fields). If data is not sorted then usea Sorter step to sort both into ascending order of the key fields. Does not handle the case wherekeys are not unique in one or both inputs.";
/*   82:     */   }
/*   83:     */   
/*   84:     */   public void setKeySpec(String ks)
/*   85:     */   {
/*   86: 238 */     this.m_keySpec = ks;
/*   87:     */   }
/*   88:     */   
/*   89:     */   public String getKeySpec()
/*   90:     */   {
/*   91: 248 */     return this.m_keySpec;
/*   92:     */   }
/*   93:     */   
/*   94:     */   public boolean eventGeneratable(String eventName)
/*   95:     */   {
/*   96: 255 */     if ((this.m_firstInput == null) || (this.m_secondInput == null)) {
/*   97: 256 */       return false;
/*   98:     */     }
/*   99: 259 */     if ((eventName.equals("instance")) && (this.m_incomingBatchConnections)) {
/*  100: 260 */       return false;
/*  101:     */     }
/*  102: 263 */     if ((!eventName.equals("instance")) && (!this.m_incomingBatchConnections)) {
/*  103: 264 */       return false;
/*  104:     */     }
/*  105: 267 */     return true;
/*  106:     */   }
/*  107:     */   
/*  108:     */   protected void generateMergedHeader()
/*  109:     */   {
/*  110: 276 */     if (((this.m_keySpec == null) || (this.m_keySpec.length() == 0)) && 
/*  111: 277 */       (this.m_log != null))
/*  112:     */     {
/*  113: 278 */       String msg = statusMessagePrefix() + "ERROR: Key fields are null!";
/*  114: 279 */       this.m_log.statusMessage(msg);
/*  115: 280 */       this.m_log.logMessage(msg);
/*  116: 281 */       stop();
/*  117: 282 */       this.m_busy = false;
/*  118: 283 */       return;
/*  119:     */     }
/*  120: 287 */     String resolvedKeySpec = this.m_keySpec;
/*  121:     */     try
/*  122:     */     {
/*  123: 289 */       resolvedKeySpec = this.m_env.substitute(this.m_keySpec);
/*  124:     */     }
/*  125:     */     catch (Exception ex) {}
/*  126: 293 */     String[] parts = resolvedKeySpec.split("@@KS@@");
/*  127: 294 */     if ((parts.length != 2) && 
/*  128: 295 */       (this.m_log != null))
/*  129:     */     {
/*  130: 296 */       String msg = statusMessagePrefix() + "ERROR: Invalid key specification: " + this.m_keySpec;
/*  131:     */       
/*  132:     */ 
/*  133: 299 */       this.m_log.statusMessage(msg);
/*  134: 300 */       this.m_log.logMessage(msg);
/*  135: 301 */       stop();
/*  136: 302 */       this.m_busy = false;
/*  137: 303 */       return;
/*  138:     */     }
/*  139: 308 */     for (int i = 0; i < 2; i++)
/*  140:     */     {
/*  141: 309 */       String rangeS = parts[i].trim();
/*  142:     */       
/*  143: 311 */       Range r = new Range();
/*  144: 312 */       r.setUpper(i == 0 ? this.m_headerOne.numAttributes() : this.m_headerTwo.numAttributes());
/*  145:     */       try
/*  146:     */       {
/*  147: 315 */         r.setRanges(rangeS);
/*  148: 316 */         if (i == 0) {
/*  149: 317 */           this.m_keyIndexesOne = r.getSelection();
/*  150:     */         } else {
/*  151: 319 */           this.m_keyIndexesTwo = r.getSelection();
/*  152:     */         }
/*  153:     */       }
/*  154:     */       catch (IllegalArgumentException e)
/*  155:     */       {
/*  156: 323 */         String[] names = rangeS.split(",");
/*  157: 324 */         if (i == 0) {
/*  158: 325 */           this.m_keyIndexesOne = new int[names.length];
/*  159:     */         } else {
/*  160: 327 */           this.m_keyIndexesTwo = new int[names.length];
/*  161:     */         }
/*  162: 330 */         for (int j = 0; j < names.length; j++)
/*  163:     */         {
/*  164: 331 */           String aName = names[j].trim();
/*  165: 332 */           Attribute anAtt = i == 0 ? this.m_headerOne.attribute(aName) : this.m_headerTwo.attribute(aName);
/*  166: 336 */           if (anAtt == null)
/*  167:     */           {
/*  168: 337 */             String msg = statusMessagePrefix() + "ERROR: Invalid key attribute name: " + aName;
/*  169: 340 */             if (this.m_log != null)
/*  170:     */             {
/*  171: 341 */               this.m_log.statusMessage(msg);
/*  172: 342 */               this.m_log.logMessage(msg);
/*  173:     */             }
/*  174:     */             else
/*  175:     */             {
/*  176: 344 */               System.err.println(msg);
/*  177:     */             }
/*  178: 346 */             stop();
/*  179: 347 */             this.m_busy = false;
/*  180: 348 */             return;
/*  181:     */           }
/*  182: 351 */           if (i == 0) {
/*  183: 352 */             this.m_keyIndexesOne[j] = anAtt.index();
/*  184:     */           } else {
/*  185: 354 */             this.m_keyIndexesTwo[j] = anAtt.index();
/*  186:     */           }
/*  187:     */         }
/*  188:     */       }
/*  189:     */     }
/*  190: 360 */     if (((this.m_keyIndexesOne == null) || (this.m_keyIndexesTwo == null)) && 
/*  191: 361 */       (this.m_log != null))
/*  192:     */     {
/*  193: 362 */       String msg = statusMessagePrefix() + "ERROR: Key fields are null!";
/*  194: 363 */       this.m_log.statusMessage(msg);
/*  195: 364 */       this.m_log.logMessage(msg);
/*  196: 365 */       stop();
/*  197: 366 */       this.m_busy = false;
/*  198: 367 */       return;
/*  199:     */     }
/*  200: 371 */     if ((this.m_keyIndexesOne.length != this.m_keyIndexesTwo.length) && 
/*  201: 372 */       (this.m_log != null))
/*  202:     */     {
/*  203: 373 */       String msg = statusMessagePrefix() + "ERROR: number of key fields are different for each input!";
/*  204:     */       
/*  205:     */ 
/*  206: 376 */       this.m_log.statusMessage(msg);
/*  207: 377 */       this.m_log.logMessage(msg);
/*  208: 378 */       stop();
/*  209: 379 */       this.m_busy = false;
/*  210: 380 */       return;
/*  211:     */     }
/*  212: 385 */     for (int i = 0; i < this.m_keyIndexesOne.length; i++) {
/*  213: 386 */       if (this.m_headerOne.attribute(this.m_keyIndexesOne[i]).type() != this.m_headerTwo.attribute(this.m_keyIndexesTwo[i]).type()) {
/*  214: 388 */         if (this.m_log != null)
/*  215:     */         {
/*  216: 389 */           String msg = statusMessagePrefix() + "ERROR: type of key corresponding key fields differ: " + "input 1 - " + Attribute.typeToStringShort(this.m_headerOne.attribute(this.m_keyIndexesOne[i])) + " input 2 - " + Attribute.typeToStringShort(this.m_headerTwo.attribute(this.m_keyIndexesTwo[i]));
/*  217:     */           
/*  218:     */ 
/*  219:     */ 
/*  220:     */ 
/*  221:     */ 
/*  222:     */ 
/*  223:     */ 
/*  224:     */ 
/*  225: 398 */           this.m_log.statusMessage(msg);
/*  226: 399 */           this.m_log.logMessage(msg);
/*  227: 400 */           stop();
/*  228: 401 */           this.m_busy = false;
/*  229: 402 */           return;
/*  230:     */         }
/*  231:     */       }
/*  232:     */     }
/*  233: 407 */     ArrayList<Attribute> newAtts = new ArrayList();
/*  234:     */     
/*  235: 409 */     Set<String> nameLookup = new HashSet();
/*  236: 410 */     for (int i = 0; i < this.m_headerOne.numAttributes(); i++)
/*  237:     */     {
/*  238: 411 */       newAtts.add((Attribute)this.m_headerOne.attribute(i).copy());
/*  239: 412 */       nameLookup.add(this.m_headerOne.attribute(i).name());
/*  240:     */     }
/*  241: 415 */     for (int i = 0; i < this.m_headerTwo.numAttributes(); i++)
/*  242:     */     {
/*  243: 416 */       String name = this.m_headerTwo.attribute(i).name();
/*  244: 417 */       if (nameLookup.contains(name)) {
/*  245: 418 */         name = name + "_2";
/*  246:     */       }
/*  247: 421 */       newAtts.add(this.m_headerTwo.attribute(i).copy(name));
/*  248:     */     }
/*  249: 424 */     this.m_mergedHeader = new Instances(this.m_headerOne.relationName() + "+" + this.m_headerTwo.relationName(), newAtts, 0);
/*  250:     */     
/*  251:     */ 
/*  252: 427 */     this.m_ie.setStructure(this.m_mergedHeader);
/*  253: 428 */     notifyInstanceListeners(this.m_ie);
/*  254:     */     
/*  255: 430 */     this.m_stringAttsPresent = false;
/*  256: 431 */     if (this.m_mergedHeader.checkForStringAttributes())
/*  257:     */     {
/*  258: 432 */       this.m_stringAttsPresent = true;
/*  259: 433 */       this.m_headerPool = new ArrayList();
/*  260: 434 */       this.m_count = new AtomicInteger();
/*  261: 435 */       for (int i = 0; i < 10; i++) {
/*  262:     */         try
/*  263:     */         {
/*  264: 437 */           this.m_headerPool.add((Instances)new SerializedObject(this.m_mergedHeader).getObject());
/*  265:     */         }
/*  266:     */         catch (Exception e)
/*  267:     */         {
/*  268: 441 */           e.printStackTrace();
/*  269:     */         }
/*  270:     */       }
/*  271:     */     }
/*  272:     */   }
/*  273:     */   
/*  274:     */   protected synchronized Instance generateMergedInstance(InstanceHolder one, InstanceHolder two)
/*  275:     */   {
/*  276: 458 */     double[] vals = new double[this.m_mergedHeader.numAttributes()];
/*  277: 459 */     int count = 0;
/*  278: 460 */     Instances currentStructure = this.m_mergedHeader;
/*  279: 462 */     if ((this.m_runningIncrementally) && (this.m_stringAttsPresent)) {
/*  280: 463 */       currentStructure = (Instances)this.m_headerPool.get(this.m_count.getAndIncrement() % 10);
/*  281:     */     }
/*  282: 466 */     for (int i = 0; i < this.m_headerOne.numAttributes(); i++)
/*  283:     */     {
/*  284: 467 */       vals[count] = one.m_instance.value(i);
/*  285: 468 */       if ((one.m_stringVals != null) && (one.m_stringVals.size() > 0) && (this.m_mergedHeader.attribute(count).isString()))
/*  286:     */       {
/*  287: 470 */         String valToSetInHeader = (String)one.m_stringVals.get(one.m_instance.attribute(i).name());
/*  288:     */         
/*  289: 472 */         currentStructure.attribute(count).setStringValue(valToSetInHeader);
/*  290: 473 */         vals[count] = 0.0D;
/*  291:     */       }
/*  292: 475 */       count++;
/*  293:     */     }
/*  294: 478 */     for (int i = 0; i < this.m_headerTwo.numAttributes(); i++)
/*  295:     */     {
/*  296: 479 */       vals[count] = two.m_instance.value(i);
/*  297: 480 */       if ((two.m_stringVals != null) && (two.m_stringVals.size() > 0) && (this.m_mergedHeader.attribute(count).isString()))
/*  298:     */       {
/*  299: 482 */         String valToSetInHeader = (String)one.m_stringVals.get(two.m_instance.attribute(i).name());
/*  300:     */         
/*  301: 484 */         currentStructure.attribute(count).setStringValue(valToSetInHeader);
/*  302: 485 */         vals[count] = 0.0D;
/*  303:     */       }
/*  304: 488 */       count++;
/*  305:     */     }
/*  306: 491 */     Instance newInst = new DenseInstance(1.0D, vals);
/*  307: 492 */     newInst.setDataset(currentStructure);
/*  308:     */     
/*  309: 494 */     return newInst;
/*  310:     */   }
/*  311:     */   
/*  312:     */   public synchronized void acceptInstance(InstanceEvent e)
/*  313:     */   {
/*  314: 500 */     if (e.m_formatNotificationOnly) {
/*  315: 501 */       return;
/*  316:     */     }
/*  317: 503 */     this.m_busy = true;
/*  318:     */     
/*  319: 505 */     Object source = e.getSource();
/*  320: 506 */     if (e.getStatus() == 0)
/*  321:     */     {
/*  322: 507 */       this.m_runningIncrementally = true;
/*  323: 508 */       this.m_stopRequested.set(false);
/*  324: 510 */       if ((!this.m_stopRequested.get()) && (source == this.m_firstInput) && (this.m_firstBuffer == null))
/*  325:     */       {
/*  326: 512 */         System.err.println("Allocating first buffer");
/*  327: 513 */         this.m_firstFinished = false;
/*  328: 514 */         this.m_firstBuffer = new LinkedList();
/*  329: 515 */         this.m_headerOne = e.getStructure();
/*  330: 516 */         this.m_stringAttIndexesOne = new HashMap();
/*  331: 517 */         for (int i = 0; i < this.m_headerOne.numAttributes(); i++) {
/*  332: 518 */           if (this.m_headerOne.attribute(i).isString()) {
/*  333: 519 */             this.m_stringAttIndexesOne.put(this.m_headerOne.attribute(i).name(), new Integer(i));
/*  334:     */           }
/*  335:     */         }
/*  336:     */       }
/*  337: 525 */       if ((!this.m_stopRequested.get()) && (source == this.m_secondInput) && (this.m_secondBuffer == null))
/*  338:     */       {
/*  339: 527 */         System.err.println("Allocating second buffer");
/*  340: 528 */         this.m_secondFinished = false;
/*  341: 529 */         this.m_secondBuffer = new LinkedList();
/*  342: 530 */         this.m_headerTwo = e.getStructure();
/*  343: 531 */         this.m_stringAttIndexesTwo = new HashMap();
/*  344: 532 */         for (int i = 0; i < this.m_headerTwo.numAttributes(); i++) {
/*  345: 533 */           if (this.m_headerTwo.attribute(i).isString()) {
/*  346: 534 */             this.m_stringAttIndexesTwo.put(this.m_headerTwo.attribute(i).name(), new Integer(i));
/*  347:     */           }
/*  348:     */         }
/*  349:     */       }
/*  350: 540 */       if (this.m_stopRequested.get()) {
/*  351: 541 */         return;
/*  352:     */       }
/*  353: 544 */       if (this.m_mergedHeader == null)
/*  354:     */       {
/*  355: 547 */         this.m_throughput = new StreamThroughput(statusMessagePrefix());
/*  356: 548 */         if ((this.m_headerOne != null) && (this.m_headerTwo != null) && (this.m_keySpec != null) && (this.m_keySpec.length() > 0)) {
/*  357: 552 */           generateMergedHeader();
/*  358:     */         }
/*  359:     */       }
/*  360:     */     }
/*  361:     */     else
/*  362:     */     {
/*  363: 556 */       if (this.m_stopRequested.get()) {
/*  364: 557 */         return;
/*  365:     */       }
/*  366: 560 */       Instance current = e.getInstance();
/*  367: 561 */       if ((current == null) || (e.getStatus() == 2))
/*  368:     */       {
/*  369: 562 */         if (source == this.m_firstInput)
/*  370:     */         {
/*  371: 563 */           System.err.println("Finished first");
/*  372: 564 */           this.m_firstFinished = true;
/*  373:     */         }
/*  374: 566 */         if (source == this.m_secondInput)
/*  375:     */         {
/*  376: 567 */           System.err.println("Finished second");
/*  377: 568 */           this.m_secondFinished = true;
/*  378:     */         }
/*  379:     */       }
/*  380: 572 */       if (current != null) {
/*  381: 573 */         if (source == this.m_firstInput) {
/*  382: 575 */           addToFirstBuffer(current);
/*  383: 576 */         } else if (source == this.m_secondInput) {
/*  384: 578 */           addToSecondBuffer(current);
/*  385:     */         }
/*  386:     */       }
/*  387: 582 */       if ((source == this.m_firstInput) && (this.m_secondBuffer != null) && (this.m_secondBuffer.size() <= 100) && (this.m_secondIsWaiting))
/*  388:     */       {
/*  389: 584 */         notifyAll();
/*  390: 585 */         this.m_secondIsWaiting = false;
/*  391:     */       }
/*  392: 586 */       else if ((source == this.m_secondInput) && (this.m_firstBuffer != null) && (this.m_firstBuffer.size() <= 100) && (this.m_firstIsWaiting))
/*  393:     */       {
/*  394: 588 */         notifyAll();
/*  395: 589 */         this.m_firstIsWaiting = false;
/*  396:     */       }
/*  397: 592 */       if ((this.m_firstFinished) && (this.m_secondFinished) && (!this.m_stopRequested.get()))
/*  398:     */       {
/*  399: 594 */         clearBuffers();
/*  400: 595 */         return;
/*  401:     */       }
/*  402: 598 */       if (this.m_stopRequested.get()) {
/*  403: 599 */         return;
/*  404:     */       }
/*  405: 602 */       this.m_throughput.updateStart();
/*  406: 603 */       Instance outputI = processBuffers();
/*  407: 604 */       this.m_throughput.updateEnd(this.m_log);
/*  408: 606 */       if ((outputI != null) && (!this.m_stopRequested.get()))
/*  409:     */       {
/*  410: 608 */         this.m_ie.setStatus(1);
/*  411: 609 */         this.m_ie.setInstance(outputI);
/*  412: 610 */         notifyInstanceListeners(this.m_ie);
/*  413:     */       }
/*  414:     */     }
/*  415:     */   }
/*  416:     */   
/*  417:     */   private static void copyStringAttVals(InstanceHolder holder, Map<String, Integer> stringAttIndexes)
/*  418:     */   {
/*  419: 626 */     for (String attName : stringAttIndexes.keySet())
/*  420:     */     {
/*  421: 627 */       Attribute att = holder.m_instance.dataset().attribute(attName);
/*  422: 628 */       String val = holder.m_instance.stringValue(att);
/*  423: 630 */       if (holder.m_stringVals == null) {
/*  424: 631 */         holder.m_stringVals = new HashMap();
/*  425:     */       }
/*  426: 634 */       holder.m_stringVals.put(attName, val);
/*  427:     */     }
/*  428:     */   }
/*  429:     */   
/*  430:     */   protected synchronized void addToFirstBuffer(Instance inst)
/*  431:     */   {
/*  432: 644 */     if (this.m_stopRequested.get()) {
/*  433: 645 */       return;
/*  434:     */     }
/*  435: 648 */     InstanceHolder newH = new InstanceHolder();
/*  436: 649 */     newH.m_instance = inst;
/*  437: 650 */     copyStringAttVals(newH, this.m_stringAttIndexesOne);
/*  438: 652 */     if (!this.m_stopRequested.get()) {
/*  439: 653 */       this.m_firstBuffer.add(newH);
/*  440:     */     } else {
/*  441: 655 */       return;
/*  442:     */     }
/*  443: 658 */     if ((this.m_firstBuffer.size() > 100) && (!this.m_secondFinished)) {
/*  444:     */       try
/*  445:     */       {
/*  446: 661 */         this.m_firstIsWaiting = true;
/*  447: 662 */         wait();
/*  448:     */       }
/*  449:     */       catch (InterruptedException ex) {}
/*  450:     */     }
/*  451:     */   }
/*  452:     */   
/*  453:     */   protected synchronized void addToSecondBuffer(Instance inst)
/*  454:     */   {
/*  455: 674 */     if (this.m_stopRequested.get()) {
/*  456: 675 */       return;
/*  457:     */     }
/*  458: 678 */     InstanceHolder newH = new InstanceHolder();
/*  459: 679 */     newH.m_instance = inst;
/*  460: 680 */     copyStringAttVals(newH, this.m_stringAttIndexesTwo);
/*  461: 682 */     if (!this.m_stopRequested.get()) {
/*  462: 683 */       this.m_secondBuffer.add(newH);
/*  463:     */     } else {
/*  464: 685 */       return;
/*  465:     */     }
/*  466: 688 */     if ((this.m_secondBuffer.size() > 100) && (!this.m_firstFinished)) {
/*  467:     */       try
/*  468:     */       {
/*  469: 691 */         this.m_secondIsWaiting = true;
/*  470: 692 */         wait();
/*  471:     */       }
/*  472:     */       catch (InterruptedException e) {}
/*  473:     */     }
/*  474:     */   }
/*  475:     */   
/*  476:     */   protected synchronized void clearBuffers()
/*  477:     */   {
/*  478: 702 */     while ((this.m_firstBuffer.size() > 0) && (this.m_secondBuffer.size() > 0))
/*  479:     */     {
/*  480: 703 */       this.m_throughput.updateStart();
/*  481: 704 */       Instance newInst = processBuffers();
/*  482: 705 */       this.m_throughput.updateEnd(this.m_log);
/*  483: 707 */       if (newInst != null)
/*  484:     */       {
/*  485: 708 */         this.m_ie.setInstance(newInst);
/*  486: 709 */         this.m_ie.setStatus(1);
/*  487: 710 */         notifyInstanceListeners(this.m_ie);
/*  488:     */       }
/*  489:     */     }
/*  490: 715 */     this.m_ie.setInstance(null);
/*  491: 716 */     this.m_ie.setStatus(2);
/*  492: 717 */     notifyInstanceListeners(this.m_ie);
/*  493: 719 */     if (this.m_log != null) {
/*  494: 720 */       this.m_log.statusMessage(statusMessagePrefix() + "Finished");
/*  495:     */     }
/*  496: 723 */     this.m_headerOne = null;
/*  497: 724 */     this.m_headerTwo = null;
/*  498: 725 */     this.m_mergedHeader = null;
/*  499: 726 */     this.m_firstBuffer = null;
/*  500: 727 */     this.m_secondBuffer = null;
/*  501: 728 */     this.m_firstFinished = false;
/*  502: 729 */     this.m_secondFinished = false;
/*  503: 730 */     this.m_busy = false;
/*  504:     */   }
/*  505:     */   
/*  506:     */   protected synchronized Instance processBuffers()
/*  507:     */   {
/*  508: 742 */     if ((this.m_firstBuffer != null) && (this.m_secondBuffer != null) && (this.m_firstBuffer.size() > 0) && (this.m_secondBuffer.size() > 0))
/*  509:     */     {
/*  510: 747 */       if (this.m_stopRequested.get()) {
/*  511: 748 */         return null;
/*  512:     */       }
/*  513: 751 */       InstanceHolder firstH = (InstanceHolder)this.m_firstBuffer.peek();
/*  514: 752 */       InstanceHolder secondH = (InstanceHolder)this.m_secondBuffer.peek();
/*  515: 753 */       Instance first = firstH.m_instance;
/*  516: 754 */       Instance second = secondH.m_instance;
/*  517:     */       
/*  518:     */ 
/*  519:     */ 
/*  520:     */ 
/*  521:     */ 
/*  522:     */ 
/*  523:     */ 
/*  524: 762 */       int cmp = compare(first, second, firstH, secondH);
/*  525: 763 */       if (cmp == 0)
/*  526:     */       {
/*  527: 765 */         Instance newInst = generateMergedInstance((InstanceHolder)this.m_firstBuffer.remove(), (InstanceHolder)this.m_secondBuffer.remove());
/*  528:     */         
/*  529:     */ 
/*  530:     */ 
/*  531: 769 */         return newInst;
/*  532:     */       }
/*  533: 770 */       if (cmp < 0) {
/*  534:     */         do
/*  535:     */         {
/*  536: 773 */           this.m_firstBuffer.remove();
/*  537: 774 */           if (this.m_firstBuffer.size() > 0)
/*  538:     */           {
/*  539: 775 */             firstH = (InstanceHolder)this.m_firstBuffer.peek();
/*  540: 776 */             first = firstH.m_instance;
/*  541: 777 */             cmp = compare(first, second, firstH, secondH);
/*  542:     */           }
/*  543: 779 */           if (cmp >= 0) {
/*  544:     */             break;
/*  545:     */           }
/*  546: 779 */         } while (this.m_firstBuffer.size() > 0);
/*  547:     */       } else {
/*  548:     */         do
/*  549:     */         {
/*  550: 783 */           this.m_secondBuffer.remove();
/*  551: 784 */           if (this.m_secondBuffer.size() > 0)
/*  552:     */           {
/*  553: 785 */             secondH = (InstanceHolder)this.m_secondBuffer.peek();
/*  554: 786 */             second = secondH.m_instance;
/*  555: 787 */             cmp = compare(first, second, firstH, secondH);
/*  556:     */           }
/*  557: 789 */         } while ((cmp > 0) && (this.m_secondBuffer.size() > 0));
/*  558:     */       }
/*  559:     */     }
/*  560: 793 */     return null;
/*  561:     */   }
/*  562:     */   
/*  563:     */   protected int compare(Instance one, Instance two, InstanceHolder oneH, InstanceHolder twoH)
/*  564:     */   {
/*  565: 809 */     for (int i = 0; i < this.m_keyIndexesOne.length; i++) {
/*  566: 810 */       if ((!one.isMissing(this.m_keyIndexesOne[i])) || (!two.isMissing(this.m_keyIndexesTwo[i])))
/*  567:     */       {
/*  568: 815 */         if ((one.isMissing(this.m_keyIndexesOne[i])) || (two.isMissing(this.m_keyIndexesTwo[i])))
/*  569:     */         {
/*  570: 819 */           if (one.isMissing(this.m_keyIndexesOne[i])) {
/*  571: 820 */             return -1;
/*  572:     */           }
/*  573: 822 */           return 1;
/*  574:     */         }
/*  575: 826 */         if (this.m_mergedHeader.attribute(this.m_keyIndexesOne[i]).isNumeric())
/*  576:     */         {
/*  577: 827 */           double v1 = one.value(this.m_keyIndexesOne[i]);
/*  578: 828 */           double v2 = two.value(this.m_keyIndexesTwo[i]);
/*  579: 830 */           if (v1 != v2) {
/*  580: 831 */             return v1 < v2 ? -1 : 1;
/*  581:     */           }
/*  582:     */         }
/*  583: 833 */         else if (this.m_mergedHeader.attribute(this.m_keyIndexesOne[i]).isNominal())
/*  584:     */         {
/*  585: 834 */           String oneS = one.stringValue(this.m_keyIndexesOne[i]);
/*  586: 835 */           String twoS = two.stringValue(this.m_keyIndexesTwo[i]);
/*  587:     */           
/*  588: 837 */           int cmp = oneS.compareTo(twoS);
/*  589: 839 */           if (cmp != 0) {
/*  590: 840 */             return cmp;
/*  591:     */           }
/*  592:     */         }
/*  593: 842 */         else if (this.m_mergedHeader.attribute(this.m_keyIndexesOne[i]).isString())
/*  594:     */         {
/*  595: 843 */           String attNameOne = this.m_mergedHeader.attribute(this.m_keyIndexesOne[i]).name();
/*  596: 844 */           String attNameTwo = this.m_mergedHeader.attribute(this.m_keyIndexesTwo[i]).name();
/*  597:     */           
/*  598: 846 */           String oneS = (oneH.m_stringVals == null) || (oneH.m_stringVals.size() == 0) ? one.stringValue(this.m_keyIndexesOne[i]) : (String)oneH.m_stringVals.get(attNameOne);
/*  599:     */           
/*  600:     */ 
/*  601:     */ 
/*  602: 850 */           String twoS = (twoH.m_stringVals == null) || (twoH.m_stringVals.size() == 0) ? two.stringValue(this.m_keyIndexesTwo[i]) : (String)twoH.m_stringVals.get(attNameTwo);
/*  603:     */           
/*  604:     */ 
/*  605:     */ 
/*  606:     */ 
/*  607: 855 */           int cmp = oneS.compareTo(twoS);
/*  608: 857 */           if (cmp != 0) {
/*  609: 858 */             return cmp;
/*  610:     */           }
/*  611:     */         }
/*  612:     */       }
/*  613:     */     }
/*  614: 863 */     return 0;
/*  615:     */   }
/*  616:     */   
/*  617:     */   public void acceptTestSet(TestSetEvent e)
/*  618:     */   {
/*  619: 873 */     DataSetEvent de = new DataSetEvent(e.getSource(), e.getTestSet());
/*  620: 874 */     acceptDataSet(de);
/*  621:     */   }
/*  622:     */   
/*  623:     */   public void acceptTrainingSet(TrainingSetEvent e)
/*  624:     */   {
/*  625: 884 */     DataSetEvent de = new DataSetEvent(e.getSource(), e.getTrainingSet());
/*  626: 885 */     acceptDataSet(de);
/*  627:     */   }
/*  628:     */   
/*  629:     */   public synchronized void acceptDataSet(DataSetEvent e)
/*  630:     */   {
/*  631: 895 */     this.m_runningIncrementally = false;
/*  632: 896 */     this.m_stopRequested.set(false);
/*  633: 898 */     if (e.getSource() == this.m_firstInput)
/*  634:     */     {
/*  635: 900 */       if ((e.isStructureOnly()) || (e.getDataSet().numInstances() == 0))
/*  636:     */       {
/*  637: 901 */         this.m_headerOne = e.getDataSet();
/*  638: 902 */         return;
/*  639:     */       }
/*  640: 905 */       if (this.m_headerOne == null) {
/*  641: 906 */         this.m_headerOne = new Instances(e.getDataSet(), 0);
/*  642:     */       }
/*  643: 908 */       this.m_firstBuffer = new LinkedList();
/*  644: 909 */       for (int i = 0; (i < e.getDataSet().numInstances()) && (!this.m_stopRequested.get()); i++)
/*  645:     */       {
/*  646: 911 */         InstanceHolder tempH = new InstanceHolder();
/*  647: 912 */         tempH.m_instance = e.getDataSet().instance(i);
/*  648: 913 */         this.m_firstBuffer.add(tempH);
/*  649:     */       }
/*  650:     */     }
/*  651: 915 */     else if (e.getSource() == this.m_secondInput)
/*  652:     */     {
/*  653: 916 */       if ((e.isStructureOnly()) || (e.getDataSet().numInstances() == 0))
/*  654:     */       {
/*  655: 917 */         this.m_headerTwo = e.getDataSet();
/*  656: 918 */         return;
/*  657:     */       }
/*  658: 921 */       if (this.m_headerTwo == null) {
/*  659: 922 */         this.m_headerTwo = new Instances(e.getDataSet(), 0);
/*  660:     */       }
/*  661: 924 */       this.m_secondBuffer = new LinkedList();
/*  662: 925 */       for (int i = 0; (i < e.getDataSet().numInstances()) && (!this.m_stopRequested.get()); i++)
/*  663:     */       {
/*  664: 927 */         InstanceHolder tempH = new InstanceHolder();
/*  665: 928 */         tempH.m_instance = e.getDataSet().instance(i);
/*  666: 929 */         this.m_secondBuffer.add(tempH);
/*  667:     */       }
/*  668:     */     }
/*  669: 933 */     if ((this.m_firstBuffer != null) && (this.m_firstBuffer.size() > 0) && (this.m_secondBuffer != null) && (this.m_secondBuffer.size() > 0))
/*  670:     */     {
/*  671: 935 */       this.m_busy = true;
/*  672:     */       
/*  673: 937 */       generateMergedHeader();
/*  674: 938 */       DataSetEvent dse = new DataSetEvent(this, this.m_mergedHeader);
/*  675: 939 */       notifyDataListeners(dse);
/*  676:     */       
/*  677: 941 */       Instances newData = new Instances(this.m_mergedHeader, 0);
/*  678: 943 */       while ((!this.m_stopRequested.get()) && (this.m_firstBuffer.size() > 0) && (this.m_secondBuffer.size() > 0))
/*  679:     */       {
/*  680: 944 */         Instance newI = processBuffers();
/*  681: 946 */         if (newI != null) {
/*  682: 947 */           newData.add(newI);
/*  683:     */         }
/*  684:     */       }
/*  685: 951 */       if (!this.m_stopRequested.get())
/*  686:     */       {
/*  687: 952 */         dse = new DataSetEvent(this, newData);
/*  688: 953 */         notifyDataListeners(dse);
/*  689:     */       }
/*  690: 955 */       this.m_busy = false;
/*  691: 956 */       this.m_headerOne = null;
/*  692: 957 */       this.m_headerTwo = null;
/*  693: 958 */       this.m_mergedHeader = null;
/*  694: 959 */       this.m_firstBuffer = null;
/*  695: 960 */       this.m_secondBuffer = null;
/*  696:     */     }
/*  697:     */   }
/*  698:     */   
/*  699:     */   public void addDataSourceListener(DataSourceListener dsl)
/*  700:     */   {
/*  701: 971 */     this.m_dataListeners.add(dsl);
/*  702:     */   }
/*  703:     */   
/*  704:     */   public void removeDataSourceListener(DataSourceListener dsl)
/*  705:     */   {
/*  706: 981 */     this.m_dataListeners.remove(dsl);
/*  707:     */   }
/*  708:     */   
/*  709:     */   public void addInstanceListener(InstanceListener dsl)
/*  710:     */   {
/*  711: 991 */     this.m_instanceListeners.add(dsl);
/*  712:     */   }
/*  713:     */   
/*  714:     */   public void removeInstanceListener(InstanceListener dsl)
/*  715:     */   {
/*  716:1001 */     this.m_instanceListeners.remove(dsl);
/*  717:     */   }
/*  718:     */   
/*  719:     */   public void useDefaultVisual()
/*  720:     */   {
/*  721:1009 */     this.m_visual.loadIcons("weka/gui/beans/icons/Join.gif", "weka/gui/beans/icons/Join.gif");
/*  722:     */     
/*  723:1011 */     this.m_visual.setText("Join");
/*  724:     */   }
/*  725:     */   
/*  726:     */   public void setVisual(BeanVisual newVisual)
/*  727:     */   {
/*  728:1021 */     this.m_visual = newVisual;
/*  729:     */   }
/*  730:     */   
/*  731:     */   public BeanVisual getVisual()
/*  732:     */   {
/*  733:1031 */     return this.m_visual;
/*  734:     */   }
/*  735:     */   
/*  736:     */   public void setCustomName(String name)
/*  737:     */   {
/*  738:1041 */     this.m_visual.setText(name);
/*  739:     */   }
/*  740:     */   
/*  741:     */   public String getCustomName()
/*  742:     */   {
/*  743:1051 */     return this.m_visual.getText();
/*  744:     */   }
/*  745:     */   
/*  746:     */   public void stop()
/*  747:     */   {
/*  748:1059 */     if ((this.m_firstInput != null) && ((this.m_firstInput instanceof BeanCommon))) {
/*  749:1060 */       ((BeanCommon)this.m_firstInput).stop();
/*  750:     */     }
/*  751:1063 */     if ((this.m_secondInput != null) && ((this.m_secondInput instanceof BeanCommon))) {
/*  752:1064 */       ((BeanCommon)this.m_secondInput).stop();
/*  753:     */     }
/*  754:1067 */     if (this.m_log != null) {
/*  755:1068 */       this.m_log.statusMessage(statusMessagePrefix() + "Stopped");
/*  756:     */     }
/*  757:1071 */     this.m_busy = false;
/*  758:1072 */     this.m_stopRequested.set(true);
/*  759:     */     try
/*  760:     */     {
/*  761:1074 */       Thread.sleep(500L);
/*  762:     */     }
/*  763:     */     catch (InterruptedException ex) {}
/*  764:1078 */     if ((this.m_firstIsWaiting) || (this.m_secondIsWaiting)) {
/*  765:1079 */       notifyAll();
/*  766:     */     }
/*  767:1082 */     this.m_firstBuffer = null;
/*  768:1083 */     this.m_secondBuffer = null;
/*  769:1084 */     this.m_headerOne = null;
/*  770:1085 */     this.m_headerTwo = null;
/*  771:1086 */     this.m_firstFinished = false;
/*  772:1087 */     this.m_secondFinished = false;
/*  773:1088 */     this.m_mergedHeader = null;
/*  774:     */   }
/*  775:     */   
/*  776:     */   public boolean isBusy()
/*  777:     */   {
/*  778:1098 */     return this.m_busy;
/*  779:     */   }
/*  780:     */   
/*  781:     */   public void setLog(Logger logger)
/*  782:     */   {
/*  783:1108 */     this.m_log = logger;
/*  784:     */   }
/*  785:     */   
/*  786:     */   public boolean connectionAllowed(EventSetDescriptor esd)
/*  787:     */   {
/*  788:1119 */     return connectionAllowed(esd.getName());
/*  789:     */   }
/*  790:     */   
/*  791:     */   public boolean connectionAllowed(String eventName)
/*  792:     */   {
/*  793:1130 */     if ((this.m_firstInput != null) && (this.m_secondInput != null)) {
/*  794:1131 */       return false;
/*  795:     */     }
/*  796:1134 */     if ((this.m_firstInput == null) || (this.m_secondInput == null))
/*  797:     */     {
/*  798:1135 */       if (this.m_firstInput != null)
/*  799:     */       {
/*  800:1136 */         if ((this.m_firstInputConnectionType.equals("instance")) && (!eventName.equals("instance"))) {
/*  801:1138 */           return false;
/*  802:     */         }
/*  803:1139 */         if ((!this.m_firstInputConnectionType.equals("instance")) && (eventName.equals("instance"))) {
/*  804:1141 */           return false;
/*  805:     */         }
/*  806:1144 */         return true;
/*  807:     */       }
/*  808:1145 */       if (this.m_secondInput != null)
/*  809:     */       {
/*  810:1146 */         if ((this.m_secondInputConnectionType.equals("instance")) && (!eventName.equals("instance"))) {
/*  811:1148 */           return false;
/*  812:     */         }
/*  813:1149 */         if ((!this.m_secondInputConnectionType.equals("instance")) && (eventName.equals("instance"))) {
/*  814:1151 */           return false;
/*  815:     */         }
/*  816:1153 */         return true;
/*  817:     */       }
/*  818:1157 */       return true;
/*  819:     */     }
/*  820:1160 */     return false;
/*  821:     */   }
/*  822:     */   
/*  823:     */   public void connectionNotification(String eventName, Object source)
/*  824:     */   {
/*  825:1171 */     if (connectionAllowed(eventName)) {
/*  826:1172 */       if (this.m_firstInput == null)
/*  827:     */       {
/*  828:1173 */         this.m_firstInput = source;
/*  829:1174 */         this.m_firstInputConnectionType = eventName;
/*  830:     */       }
/*  831:     */       else
/*  832:     */       {
/*  833:1176 */         this.m_secondInput = source;
/*  834:1177 */         this.m_secondInputConnectionType = eventName;
/*  835:     */       }
/*  836:     */     }
/*  837:1181 */     if ((this.m_firstInput != null) && (this.m_secondInput != null)) {
/*  838:1182 */       if ((this.m_firstInputConnectionType.length() > 0) || (this.m_secondInputConnectionType.length() > 0))
/*  839:     */       {
/*  840:1184 */         if ((!this.m_firstInputConnectionType.equals("instance")) && (!this.m_secondInputConnectionType.equals("instance"))) {
/*  841:1186 */           this.m_incomingBatchConnections = true;
/*  842:     */         } else {
/*  843:1188 */           this.m_incomingBatchConnections = false;
/*  844:     */         }
/*  845:     */       }
/*  846:     */       else {
/*  847:1191 */         this.m_incomingBatchConnections = false;
/*  848:     */       }
/*  849:     */     }
/*  850:     */   }
/*  851:     */   
/*  852:     */   public void disconnectionNotification(String eventName, Object source)
/*  853:     */   {
/*  854:1204 */     if (source == this.m_firstInput)
/*  855:     */     {
/*  856:1205 */       this.m_firstInput = null;
/*  857:1206 */       this.m_firstInputConnectionType = "";
/*  858:     */     }
/*  859:1207 */     else if (source == this.m_secondInput)
/*  860:     */     {
/*  861:1208 */       this.m_secondInput = null;
/*  862:1209 */       this.m_secondInputConnectionType = "";
/*  863:     */     }
/*  864:1212 */     if ((this.m_firstInput != null) && (this.m_secondInput != null)) {
/*  865:1213 */       if ((this.m_firstInputConnectionType.length() > 0) || (this.m_secondInputConnectionType.length() > 0))
/*  866:     */       {
/*  867:1215 */         if ((!this.m_firstInputConnectionType.equals("instance")) && (!this.m_secondInputConnectionType.equals("instance"))) {
/*  868:1217 */           this.m_incomingBatchConnections = true;
/*  869:     */         } else {
/*  870:1219 */           this.m_incomingBatchConnections = false;
/*  871:     */         }
/*  872:     */       }
/*  873:     */       else {
/*  874:1222 */         this.m_incomingBatchConnections = false;
/*  875:     */       }
/*  876:     */     }
/*  877:     */   }
/*  878:     */   
/*  879:     */   private String statusMessagePrefix()
/*  880:     */   {
/*  881:1233 */     return getCustomName() + "$" + hashCode() + "|";
/*  882:     */   }
/*  883:     */   
/*  884:     */   private void notifyInstanceListeners(InstanceEvent e)
/*  885:     */   {
/*  886:1242 */     for (InstanceListener il : this.m_instanceListeners) {
/*  887:1243 */       il.acceptInstance(e);
/*  888:     */     }
/*  889:     */   }
/*  890:     */   
/*  891:     */   private void notifyDataListeners(DataSetEvent e)
/*  892:     */   {
/*  893:1253 */     for (DataSourceListener l : this.m_dataListeners) {
/*  894:1254 */       l.acceptDataSet(e);
/*  895:     */     }
/*  896:     */   }
/*  897:     */   
/*  898:     */   protected Instances getUpstreamStructureFirst()
/*  899:     */   {
/*  900:1265 */     if ((this.m_firstInput != null) && ((this.m_firstInput instanceof StructureProducer))) {
/*  901:1266 */       return ((StructureProducer)this.m_firstInput).getStructure(this.m_firstInputConnectionType);
/*  902:     */     }
/*  903:1269 */     return null;
/*  904:     */   }
/*  905:     */   
/*  906:     */   protected Instances getUpstreamStructureSecond()
/*  907:     */   {
/*  908:1279 */     if ((this.m_secondInput != null) && ((this.m_secondInput instanceof StructureProducer))) {
/*  909:1280 */       return ((StructureProducer)this.m_secondInput).getStructure(this.m_secondInputConnectionType);
/*  910:     */     }
/*  911:1283 */     return null;
/*  912:     */   }
/*  913:     */   
/*  914:     */   protected Object getFirstInput()
/*  915:     */   {
/*  916:1292 */     return this.m_firstInput;
/*  917:     */   }
/*  918:     */   
/*  919:     */   protected Instances getFirstInputStructure()
/*  920:     */   {
/*  921:1301 */     Instances result = null;
/*  922:1303 */     if ((this.m_firstInput instanceof StructureProducer)) {
/*  923:1304 */       result = ((StructureProducer)this.m_firstInput).getStructure(this.m_firstInputConnectionType);
/*  924:     */     }
/*  925:1309 */     return result;
/*  926:     */   }
/*  927:     */   
/*  928:     */   protected Object getSecondInput()
/*  929:     */   {
/*  930:1318 */     return this.m_secondInput;
/*  931:     */   }
/*  932:     */   
/*  933:     */   protected Instances getSecondInputStructure()
/*  934:     */   {
/*  935:1327 */     Instances result = null;
/*  936:1329 */     if ((this.m_secondInput instanceof StructureProducer)) {
/*  937:1330 */       result = ((StructureProducer)this.m_secondInput).getStructure(this.m_secondInputConnectionType);
/*  938:     */     }
/*  939:1335 */     return result;
/*  940:     */   }
/*  941:     */   
/*  942:     */   public Instances getStructure(String eventName)
/*  943:     */   {
/*  944:1346 */     if ((!eventName.equals("dataSet")) && (!eventName.equals("instance"))) {
/*  945:1347 */       return null;
/*  946:     */     }
/*  947:1350 */     if ((eventName.equals("dataSet")) && (this.m_dataListeners.size() == 0)) {
/*  948:1351 */       return null;
/*  949:     */     }
/*  950:1354 */     if ((eventName.equals("instance")) && (this.m_instanceListeners.size() == 0)) {
/*  951:1355 */       return null;
/*  952:     */     }
/*  953:1358 */     if (this.m_mergedHeader == null) {
/*  954:1359 */       generateMergedHeader();
/*  955:     */     }
/*  956:1362 */     return this.m_mergedHeader;
/*  957:     */   }
/*  958:     */   
/*  959:     */   public void setEnvironment(Environment env)
/*  960:     */   {
/*  961:1372 */     this.m_env = env;
/*  962:     */   }
/*  963:     */   
/*  964:     */   protected static class InstanceHolder
/*  965:     */     implements Serializable
/*  966:     */   {
/*  967:     */     private static final long serialVersionUID = -2554438923824758088L;
/*  968:     */     protected Instance m_instance;
/*  969:     */     protected Map<String, String> m_stringVals;
/*  970:     */   }
/*  971:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.Join
 * JD-Core Version:    0.7.0.1
 */