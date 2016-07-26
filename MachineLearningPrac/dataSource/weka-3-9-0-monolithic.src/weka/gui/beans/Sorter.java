/*    1:     */ package weka.gui.beans;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.beans.EventSetDescriptor;
/*    5:     */ import java.io.BufferedInputStream;
/*    6:     */ import java.io.BufferedOutputStream;
/*    7:     */ import java.io.File;
/*    8:     */ import java.io.FileInputStream;
/*    9:     */ import java.io.FileOutputStream;
/*   10:     */ import java.io.ObjectInputStream;
/*   11:     */ import java.io.ObjectOutputStream;
/*   12:     */ import java.io.Serializable;
/*   13:     */ import java.util.ArrayList;
/*   14:     */ import java.util.Collections;
/*   15:     */ import java.util.Comparator;
/*   16:     */ import java.util.HashMap;
/*   17:     */ import java.util.Iterator;
/*   18:     */ import java.util.List;
/*   19:     */ import java.util.Map;
/*   20:     */ import java.util.concurrent.atomic.AtomicBoolean;
/*   21:     */ import javax.swing.JPanel;
/*   22:     */ import weka.core.Attribute;
/*   23:     */ import weka.core.Environment;
/*   24:     */ import weka.core.EnvironmentHandler;
/*   25:     */ import weka.core.Instance;
/*   26:     */ import weka.core.Instances;
/*   27:     */ import weka.gui.Logger;
/*   28:     */ 
/*   29:     */ @KFStep(category="Tools", toolTipText="Sort instances in ascending or descending order")
/*   30:     */ public class Sorter
/*   31:     */   extends JPanel
/*   32:     */   implements BeanCommon, Visible, Serializable, DataSource, DataSourceListener, TrainingSetListener, TestSetListener, InstanceListener, EventConstraints, StructureProducer, EnvironmentHandler
/*   33:     */ {
/*   34:     */   private static final long serialVersionUID = 4978227384322482115L;
/*   35:     */   protected transient Logger m_log;
/*   36:     */   protected Object m_listenee;
/*   37:     */   protected String m_connectionType;
/*   38:  85 */   protected InstanceEvent m_ie = new InstanceEvent(this);
/*   39:     */   protected boolean m_busy;
/*   40:     */   protected AtomicBoolean m_stopRequested;
/*   41:     */   protected String m_sortDetails;
/*   42:     */   protected transient Environment m_env;
/*   43:     */   protected transient SortComparator m_sortComparator;
/*   44:     */   protected transient List<InstanceHolder> m_incrementalBuffer;
/*   45:     */   protected transient List<File> m_bufferFiles;
/*   46: 109 */   protected String m_bufferSize = "10000";
/*   47: 112 */   protected int m_bufferSizeI = 10000;
/*   48:     */   protected Map<String, Integer> m_stringAttIndexes;
/*   49: 121 */   protected String m_tempDirectory = "";
/*   50: 123 */   protected transient int m_streamCounter = 0;
/*   51:     */   private Instances m_connectedFormat;
/*   52: 131 */   protected BeanVisual m_visual = new BeanVisual("Sorter", "weka/gui/beans/icons/Sorter.gif", "weka/gui/beans/icons/Sorter_animated.gif");
/*   53: 135 */   protected ArrayList<DataSourceListener> m_dataListeners = new ArrayList();
/*   54: 139 */   protected ArrayList<InstanceListener> m_instanceListeners = new ArrayList();
/*   55:     */   
/*   56:     */   protected static class InstanceHolder
/*   57:     */     implements Serializable
/*   58:     */   {
/*   59:     */     private static final long serialVersionUID = -3985730394250172995L;
/*   60:     */     protected Instance m_instance;
/*   61:     */     protected int m_fileNumber;
/*   62:     */     protected Map<String, String> m_stringVals;
/*   63:     */   }
/*   64:     */   
/*   65:     */   protected static class SortComparator
/*   66:     */     implements Comparator<Sorter.InstanceHolder>
/*   67:     */   {
/*   68:     */     protected List<Sorter.SortRule> m_sortRules;
/*   69:     */     
/*   70:     */     public SortComparator(List<Sorter.SortRule> sortRules)
/*   71:     */     {
/*   72: 174 */       this.m_sortRules = sortRules;
/*   73:     */     }
/*   74:     */     
/*   75:     */     public int compare(Sorter.InstanceHolder o1, Sorter.InstanceHolder o2)
/*   76:     */     {
/*   77: 180 */       int cmp = 0;
/*   78: 181 */       for (Sorter.SortRule sr : this.m_sortRules)
/*   79:     */       {
/*   80: 182 */         cmp = sr.compare(o1, o2);
/*   81: 183 */         if (cmp != 0) {
/*   82: 184 */           return cmp;
/*   83:     */         }
/*   84:     */       }
/*   85: 188 */       return 0;
/*   86:     */     }
/*   87:     */   }
/*   88:     */   
/*   89:     */   protected static class SortRule
/*   90:     */     implements Comparator<Sorter.InstanceHolder>
/*   91:     */   {
/*   92:     */     protected String m_attributeNameOrIndex;
/*   93:     */     protected Attribute m_attribute;
/*   94:     */     protected boolean m_descending;
/*   95:     */     
/*   96:     */     public SortRule(String att, boolean descending)
/*   97:     */     {
/*   98: 203 */       this.m_attributeNameOrIndex = att;
/*   99: 204 */       this.m_descending = descending;
/*  100:     */     }
/*  101:     */     
/*  102:     */     public SortRule() {}
/*  103:     */     
/*  104:     */     public SortRule(String setup)
/*  105:     */     {
/*  106: 211 */       parseFromInternal(setup);
/*  107:     */     }
/*  108:     */     
/*  109:     */     protected void parseFromInternal(String setup)
/*  110:     */     {
/*  111: 215 */       String[] parts = setup.split("@@SR@@");
/*  112: 217 */       if (parts.length != 2) {
/*  113: 218 */         throw new IllegalArgumentException("Malformed sort rule: " + setup);
/*  114:     */       }
/*  115: 221 */       this.m_attributeNameOrIndex = parts[0].trim();
/*  116: 222 */       this.m_descending = parts[1].equalsIgnoreCase("Y");
/*  117:     */     }
/*  118:     */     
/*  119:     */     protected String toStringInternal()
/*  120:     */     {
/*  121: 226 */       return this.m_attributeNameOrIndex + "@@SR@@" + (this.m_descending ? "Y" : "N");
/*  122:     */     }
/*  123:     */     
/*  124:     */     public String toString()
/*  125:     */     {
/*  126: 231 */       StringBuffer res = new StringBuffer();
/*  127:     */       
/*  128: 233 */       res.append("Attribute: " + this.m_attributeNameOrIndex + " - sort " + (this.m_descending ? "descending" : "ascending"));
/*  129:     */       
/*  130:     */ 
/*  131: 236 */       return res.toString();
/*  132:     */     }
/*  133:     */     
/*  134:     */     public void setAttribute(String att)
/*  135:     */     {
/*  136: 240 */       this.m_attributeNameOrIndex = att;
/*  137:     */     }
/*  138:     */     
/*  139:     */     public String getAttribute()
/*  140:     */     {
/*  141: 244 */       return this.m_attributeNameOrIndex;
/*  142:     */     }
/*  143:     */     
/*  144:     */     public void setDescending(boolean d)
/*  145:     */     {
/*  146: 248 */       this.m_descending = d;
/*  147:     */     }
/*  148:     */     
/*  149:     */     public boolean getDescending()
/*  150:     */     {
/*  151: 252 */       return this.m_descending;
/*  152:     */     }
/*  153:     */     
/*  154:     */     public void init(Environment env, Instances structure)
/*  155:     */     {
/*  156: 256 */       String attNameI = this.m_attributeNameOrIndex;
/*  157:     */       try
/*  158:     */       {
/*  159: 258 */         attNameI = env.substitute(attNameI);
/*  160:     */       }
/*  161:     */       catch (Exception ex) {}
/*  162: 262 */       if (attNameI.equalsIgnoreCase("/first"))
/*  163:     */       {
/*  164: 263 */         this.m_attribute = structure.attribute(0);
/*  165:     */       }
/*  166: 264 */       else if (attNameI.equalsIgnoreCase("/last"))
/*  167:     */       {
/*  168: 265 */         this.m_attribute = structure.attribute(structure.numAttributes() - 1);
/*  169:     */       }
/*  170:     */       else
/*  171:     */       {
/*  172: 268 */         this.m_attribute = structure.attribute(attNameI);
/*  173: 270 */         if (this.m_attribute == null) {
/*  174:     */           try
/*  175:     */           {
/*  176: 273 */             int index = Integer.parseInt(attNameI);
/*  177: 274 */             this.m_attribute = structure.attribute(index);
/*  178:     */           }
/*  179:     */           catch (NumberFormatException n)
/*  180:     */           {
/*  181: 276 */             throw new IllegalArgumentException("Unable to locate attribute " + attNameI + " as either a named attribute or as a valid " + "attribute index");
/*  182:     */           }
/*  183:     */         }
/*  184:     */       }
/*  185:     */     }
/*  186:     */     
/*  187:     */     public int compare(Sorter.InstanceHolder o1, Sorter.InstanceHolder o2)
/*  188:     */     {
/*  189: 288 */       if ((o1.m_instance.isMissing(this.m_attribute)) && (o2.m_instance.isMissing(this.m_attribute))) {
/*  190: 290 */         return 0;
/*  191:     */       }
/*  192: 295 */       if (o1.m_instance.isMissing(this.m_attribute)) {
/*  193: 296 */         return 1;
/*  194:     */       }
/*  195: 299 */       if (o2.m_instance.isMissing(this.m_attribute)) {
/*  196: 300 */         return -1;
/*  197:     */       }
/*  198: 303 */       int cmp = 0;
/*  199: 305 */       if ((!this.m_attribute.isString()) && (!this.m_attribute.isRelationValued()))
/*  200:     */       {
/*  201: 306 */         double val1 = o1.m_instance.value(this.m_attribute);
/*  202: 307 */         double val2 = o2.m_instance.value(this.m_attribute);
/*  203:     */         
/*  204: 309 */         cmp = Double.compare(val1, val2);
/*  205:     */       }
/*  206: 310 */       else if (this.m_attribute.isString())
/*  207:     */       {
/*  208: 311 */         String val1 = (String)o1.m_stringVals.get(this.m_attribute.name());
/*  209: 312 */         String val2 = (String)o2.m_stringVals.get(this.m_attribute.name());
/*  210:     */         
/*  211:     */ 
/*  212:     */ 
/*  213:     */ 
/*  214:     */ 
/*  215:     */ 
/*  216:     */ 
/*  217: 320 */         cmp = val1.compareTo(val2);
/*  218:     */       }
/*  219:     */       else
/*  220:     */       {
/*  221: 322 */         throw new IllegalArgumentException("Can't sort according to relation-valued attribute values!");
/*  222:     */       }
/*  223: 326 */       if (this.m_descending) {
/*  224: 327 */         return -cmp;
/*  225:     */       }
/*  226: 330 */       return cmp;
/*  227:     */     }
/*  228:     */   }
/*  229:     */   
/*  230:     */   public Sorter()
/*  231:     */   {
/*  232: 338 */     useDefaultVisual();
/*  233: 339 */     setLayout(new BorderLayout());
/*  234: 340 */     add(this.m_visual, "Center");
/*  235:     */     
/*  236: 342 */     this.m_env = Environment.getSystemWide();
/*  237: 343 */     this.m_stopRequested = new AtomicBoolean(false);
/*  238:     */   }
/*  239:     */   
/*  240:     */   public String globalInfo()
/*  241:     */   {
/*  242: 352 */     return "Sorts incoming instances in ascending or descending order according to the values of user specified attributes. Instances can be sorted according to multiple attributes (defined in order). Handles data sets larger than can be fit into main memory via instance connections and specifying the in-memory buffer size. Implements a merge-sort by writing the sorted in-memory buffer to a file when full and then interleaving instances from the disk based file(s) when the incoming stream has finished.";
/*  243:     */   }
/*  244:     */   
/*  245:     */   public boolean eventGeneratable(String eventName)
/*  246:     */   {
/*  247: 370 */     if (this.m_listenee == null) {
/*  248: 371 */       return false;
/*  249:     */     }
/*  250: 374 */     if ((!eventName.equals("instance")) && (!eventName.equals("dataSet"))) {
/*  251: 375 */       return false;
/*  252:     */     }
/*  253: 378 */     if (((this.m_listenee instanceof DataSource)) && 
/*  254: 379 */       ((this.m_listenee instanceof EventConstraints)))
/*  255:     */     {
/*  256: 380 */       EventConstraints ec = (EventConstraints)this.m_listenee;
/*  257: 381 */       return ec.eventGeneratable(eventName);
/*  258:     */     }
/*  259: 385 */     if (((this.m_listenee instanceof TrainingSetProducer)) && 
/*  260: 386 */       ((this.m_listenee instanceof EventConstraints)))
/*  261:     */     {
/*  262: 387 */       EventConstraints ec = (EventConstraints)this.m_listenee;
/*  263: 389 */       if (!eventName.equals("dataSet")) {
/*  264: 390 */         return false;
/*  265:     */       }
/*  266: 393 */       if (!ec.eventGeneratable("trainingSet")) {
/*  267: 394 */         return false;
/*  268:     */       }
/*  269:     */     }
/*  270: 399 */     if (((this.m_listenee instanceof TestSetProducer)) && 
/*  271: 400 */       ((this.m_listenee instanceof EventConstraints)))
/*  272:     */     {
/*  273: 401 */       EventConstraints ec = (EventConstraints)this.m_listenee;
/*  274: 403 */       if (!eventName.equals("dataSet")) {
/*  275: 404 */         return false;
/*  276:     */       }
/*  277: 407 */       if (!ec.eventGeneratable("testSet")) {
/*  278: 408 */         return false;
/*  279:     */       }
/*  280:     */     }
/*  281: 413 */     return true;
/*  282:     */   }
/*  283:     */   
/*  284:     */   private void copyStringAttVals(InstanceHolder holder)
/*  285:     */   {
/*  286: 417 */     for (String attName : this.m_stringAttIndexes.keySet())
/*  287:     */     {
/*  288: 418 */       Attribute att = holder.m_instance.dataset().attribute(attName);
/*  289: 419 */       String val = holder.m_instance.stringValue(att);
/*  290: 421 */       if (holder.m_stringVals == null) {
/*  291: 422 */         holder.m_stringVals = new HashMap();
/*  292:     */       }
/*  293: 425 */       holder.m_stringVals.put(attName, val);
/*  294:     */     }
/*  295:     */   }
/*  296:     */   
/*  297:     */   public void acceptInstance(InstanceEvent e)
/*  298:     */   {
/*  299: 437 */     if (e.getStatus() == 0)
/*  300:     */     {
/*  301: 438 */       this.m_connectedFormat = e.getStructure();
/*  302: 439 */       this.m_stopRequested.set(false);
/*  303:     */       try
/*  304:     */       {
/*  305: 441 */         init(new Instances(e.getStructure(), 0));
/*  306:     */       }
/*  307:     */       catch (IllegalArgumentException ex)
/*  308:     */       {
/*  309: 443 */         if (this.m_log != null)
/*  310:     */         {
/*  311: 444 */           String message = "ERROR: There is a problem with the incoming instance structure";
/*  312:     */           
/*  313:     */ 
/*  314:     */ 
/*  315:     */ 
/*  316:     */ 
/*  317:     */ 
/*  318:     */ 
/*  319: 452 */           stopWithErrorMessage(message, ex);
/*  320:     */           
/*  321: 454 */           return;
/*  322:     */         }
/*  323:     */       }
/*  324: 458 */       String buffSize = this.m_bufferSize;
/*  325:     */       try
/*  326:     */       {
/*  327: 460 */         buffSize = this.m_env.substitute(buffSize);
/*  328: 461 */         this.m_bufferSizeI = Integer.parseInt(buffSize);
/*  329:     */       }
/*  330:     */       catch (Exception ex)
/*  331:     */       {
/*  332: 463 */         ex.printStackTrace();
/*  333:     */       }
/*  334: 465 */       this.m_incrementalBuffer = new ArrayList(this.m_bufferSizeI);
/*  335: 466 */       this.m_bufferFiles = new ArrayList();
/*  336: 467 */       this.m_streamCounter = 0;
/*  337:     */       
/*  338: 469 */       return;
/*  339:     */     }
/*  340: 472 */     this.m_busy = true;
/*  341: 474 */     if (e.getInstance() != null)
/*  342:     */     {
/*  343: 475 */       if ((this.m_streamCounter == 0) && 
/*  344: 476 */         (this.m_log != null))
/*  345:     */       {
/*  346: 477 */         this.m_log.statusMessage(statusMessagePrefix() + "Starting streaming sort...");
/*  347:     */         
/*  348: 479 */         this.m_log.logMessage("[Sorter] " + statusMessagePrefix() + " Using streaming buffer size: " + this.m_bufferSizeI);
/*  349:     */       }
/*  350: 484 */       InstanceHolder tempH = new InstanceHolder();
/*  351: 485 */       tempH.m_instance = e.getInstance();
/*  352: 486 */       tempH.m_fileNumber = -1;
/*  353: 487 */       if (this.m_stringAttIndexes != null) {
/*  354: 488 */         copyStringAttVals(tempH);
/*  355:     */       }
/*  356: 490 */       this.m_incrementalBuffer.add(tempH);
/*  357: 491 */       this.m_streamCounter += 1;
/*  358:     */     }
/*  359: 494 */     if ((e.getInstance() == null) || (e.getStatus() == 2))
/*  360:     */     {
/*  361: 496 */       emitBufferedInstances();
/*  362:     */       
/*  363:     */ 
/*  364: 499 */       return;
/*  365:     */     }
/*  366: 500 */     if (this.m_incrementalBuffer.size() == this.m_bufferSizeI) {
/*  367:     */       try
/*  368:     */       {
/*  369: 503 */         sortBuffer(true);
/*  370:     */       }
/*  371:     */       catch (Exception ex)
/*  372:     */       {
/*  373: 505 */         String msg = statusMessagePrefix() + "ERROR: unable to write to temp file.";
/*  374:     */         
/*  375:     */ 
/*  376:     */ 
/*  377:     */ 
/*  378:     */ 
/*  379: 511 */         stopWithErrorMessage(msg, ex);
/*  380:     */         
/*  381:     */ 
/*  382: 514 */         this.m_busy = false;
/*  383: 515 */         return;
/*  384:     */       }
/*  385:     */     }
/*  386: 519 */     this.m_busy = false;
/*  387:     */   }
/*  388:     */   
/*  389:     */   protected void emitBufferedInstances()
/*  390:     */   {
/*  391: 527 */     Thread t = new Thread()
/*  392:     */     {
/*  393:     */       public void run()
/*  394:     */       {
/*  395: 531 */         int mergeCount = 0;
/*  396: 533 */         if ((Sorter.this.m_incrementalBuffer.size() > 0) && (!Sorter.this.m_stopRequested.get()))
/*  397:     */         {
/*  398:     */           try
/*  399:     */           {
/*  400: 535 */             Sorter.this.sortBuffer(false);
/*  401:     */           }
/*  402:     */           catch (Exception ex) {}
/*  403: 539 */           if (Sorter.this.m_bufferFiles.size() == 0)
/*  404:     */           {
/*  405: 541 */             if (Sorter.this.m_stopRequested.get())
/*  406:     */             {
/*  407: 542 */               Sorter.this.m_busy = false;
/*  408: 543 */               return;
/*  409:     */             }
/*  410: 545 */             String msg = Sorter.this.statusMessagePrefix() + "Emitting in memory buffer....";
/*  411: 547 */             if (Sorter.this.m_log != null)
/*  412:     */             {
/*  413: 548 */               Sorter.this.m_log.statusMessage(msg);
/*  414: 549 */               Sorter.this.m_log.logMessage("[" + Sorter.this.getCustomName() + "] " + msg);
/*  415:     */             }
/*  416: 552 */             Instances newHeader = new Instances(((Sorter.InstanceHolder)Sorter.this.m_incrementalBuffer.get(0)).m_instance.dataset(), 0);
/*  417:     */             
/*  418: 554 */             Sorter.this.m_ie.setStructure(newHeader);
/*  419: 555 */             Sorter.this.notifyInstanceListeners(Sorter.this.m_ie);
/*  420: 556 */             for (int i = 0; i < Sorter.this.m_incrementalBuffer.size(); i++)
/*  421:     */             {
/*  422: 557 */               Sorter.InstanceHolder currentH = (Sorter.InstanceHolder)Sorter.this.m_incrementalBuffer.get(i);
/*  423: 558 */               currentH.m_instance.setDataset(newHeader);
/*  424: 560 */               if (Sorter.this.m_stringAttIndexes != null) {
/*  425: 561 */                 for (String attName : Sorter.this.m_stringAttIndexes.keySet())
/*  426:     */                 {
/*  427: 562 */                   boolean setValToZero = newHeader.attribute(attName).numValues() > 0;
/*  428:     */                   
/*  429:     */ 
/*  430: 565 */                   String valToSetInHeader = (String)currentH.m_stringVals.get(attName);
/*  431: 566 */                   newHeader.attribute(attName).setStringValue(valToSetInHeader);
/*  432: 568 */                   if (setValToZero) {
/*  433: 569 */                     currentH.m_instance.setValue(newHeader.attribute(attName), 0.0D);
/*  434:     */                   }
/*  435:     */                 }
/*  436:     */               }
/*  437: 575 */               if (Sorter.this.m_stopRequested.get())
/*  438:     */               {
/*  439: 576 */                 Sorter.this.m_busy = false;
/*  440: 577 */                 return;
/*  441:     */               }
/*  442: 579 */               Sorter.this.m_ie.setInstance(currentH.m_instance);
/*  443: 580 */               Sorter.this.m_ie.setStatus(1);
/*  444: 581 */               if (i == Sorter.this.m_incrementalBuffer.size() - 1) {
/*  445: 582 */                 Sorter.this.m_ie.setStatus(2);
/*  446:     */               }
/*  447: 584 */               Sorter.this.notifyInstanceListeners(Sorter.this.m_ie);
/*  448:     */             }
/*  449: 587 */             msg = Sorter.this.statusMessagePrefix() + "Finished.";
/*  450: 588 */             if (Sorter.this.m_log != null)
/*  451:     */             {
/*  452: 589 */               Sorter.this.m_log.statusMessage(msg);
/*  453: 590 */               Sorter.this.m_log.logMessage("[" + Sorter.this.getCustomName() + "] " + msg);
/*  454:     */             }
/*  455: 592 */             Sorter.this.m_busy = false;
/*  456:     */             
/*  457: 594 */             return;
/*  458:     */           }
/*  459:     */         }
/*  460: 598 */         List<ObjectInputStream> inputStreams = new ArrayList();
/*  461:     */         
/*  462:     */ 
/*  463: 601 */         List<Sorter.InstanceHolder> merger = new ArrayList();
/*  464:     */         
/*  465: 603 */         Instances tempHeader = new Instances(Sorter.this.m_connectedFormat, 0);
/*  466: 604 */         Sorter.this.m_ie.setStructure(tempHeader);
/*  467: 605 */         Sorter.this.notifyInstanceListeners(Sorter.this.m_ie);
/*  468: 608 */         if (Sorter.this.m_incrementalBuffer.size() > 0)
/*  469:     */         {
/*  470: 609 */           Sorter.InstanceHolder tempH = (Sorter.InstanceHolder)Sorter.this.m_incrementalBuffer.remove(0);
/*  471: 610 */           merger.add(tempH);
/*  472:     */         }
/*  473: 613 */         if (Sorter.this.m_stopRequested.get())
/*  474:     */         {
/*  475: 614 */           Sorter.this.m_busy = false;
/*  476: 615 */           return;
/*  477:     */         }
/*  478: 618 */         if (Sorter.this.m_bufferFiles.size() > 0)
/*  479:     */         {
/*  480: 619 */           String msg = Sorter.this.statusMessagePrefix() + "Merging temp files...";
/*  481: 620 */           if (Sorter.this.m_log != null)
/*  482:     */           {
/*  483: 621 */             Sorter.this.m_log.statusMessage(msg);
/*  484: 622 */             Sorter.this.m_log.logMessage("[" + Sorter.this.getCustomName() + "] " + msg);
/*  485:     */           }
/*  486:     */         }
/*  487: 626 */         for (int i = 0; i < Sorter.this.m_bufferFiles.size(); i++)
/*  488:     */         {
/*  489: 628 */           ObjectInputStream ois = null;
/*  490:     */           try
/*  491:     */           {
/*  492: 631 */             FileInputStream fis = new FileInputStream((File)Sorter.this.m_bufferFiles.get(i));
/*  493:     */             
/*  494: 633 */             BufferedInputStream bis = new BufferedInputStream(fis, 50000);
/*  495: 634 */             ois = new ObjectInputStream(bis);
/*  496:     */             
/*  497: 636 */             Sorter.InstanceHolder tempH = (Sorter.InstanceHolder)ois.readObject();
/*  498: 637 */             if (tempH != null)
/*  499:     */             {
/*  500: 638 */               inputStreams.add(ois);
/*  501:     */               
/*  502: 640 */               tempH.m_fileNumber = i;
/*  503: 641 */               merger.add(tempH);
/*  504:     */             }
/*  505:     */             else
/*  506:     */             {
/*  507: 644 */               ois.close();
/*  508:     */             }
/*  509:     */           }
/*  510:     */           catch (Exception ex)
/*  511:     */           {
/*  512: 647 */             ex.printStackTrace();
/*  513: 648 */             if (ois != null) {
/*  514:     */               try
/*  515:     */               {
/*  516: 650 */                 ois.close();
/*  517:     */               }
/*  518:     */               catch (Exception e) {}
/*  519:     */             }
/*  520:     */           }
/*  521:     */         }
/*  522: 656 */         Collections.sort(merger, Sorter.this.m_sortComparator);
/*  523:     */         do
/*  524:     */         {
/*  525: 659 */           if (Sorter.this.m_stopRequested.get())
/*  526:     */           {
/*  527: 660 */             Sorter.this.m_busy = false;
/*  528: 661 */             break;
/*  529:     */           }
/*  530: 664 */           Sorter.InstanceHolder holder = (Sorter.InstanceHolder)merger.remove(0);
/*  531: 665 */           holder.m_instance.setDataset(tempHeader);
/*  532: 667 */           if (Sorter.this.m_stringAttIndexes != null) {
/*  533: 668 */             for (String attName : Sorter.this.m_stringAttIndexes.keySet())
/*  534:     */             {
/*  535: 669 */               boolean setValToZero = tempHeader.attribute(attName).numValues() > 1;
/*  536:     */               
/*  537: 671 */               String valToSetInHeader = (String)holder.m_stringVals.get(attName);
/*  538: 672 */               tempHeader.attribute(attName).setStringValue(valToSetInHeader);
/*  539: 674 */               if (setValToZero) {
/*  540: 675 */                 holder.m_instance.setValue(tempHeader.attribute(attName), 0.0D);
/*  541:     */               }
/*  542:     */             }
/*  543:     */           }
/*  544: 680 */           if (Sorter.this.m_stopRequested.get())
/*  545:     */           {
/*  546: 681 */             Sorter.this.m_busy = false;
/*  547: 682 */             break;
/*  548:     */           }
/*  549: 684 */           Sorter.this.m_ie.setInstance(holder.m_instance);
/*  550: 685 */           Sorter.this.m_ie.setStatus(1);
/*  551: 686 */           mergeCount++;
/*  552: 687 */           Sorter.this.notifyInstanceListeners(Sorter.this.m_ie);
/*  553: 689 */           if ((mergeCount % Sorter.this.m_bufferSizeI == 0) && (Sorter.this.m_log != null))
/*  554:     */           {
/*  555: 690 */             String msg = Sorter.this.statusMessagePrefix() + "Merged " + mergeCount + " instances";
/*  556: 692 */             if (Sorter.this.m_log != null) {
/*  557: 693 */               Sorter.this.m_log.statusMessage(msg);
/*  558:     */             }
/*  559:     */           }
/*  560: 697 */           int smallest = holder.m_fileNumber;
/*  561:     */           
/*  562:     */ 
/*  563: 700 */           Sorter.InstanceHolder nextH = null;
/*  564: 701 */           if (smallest == -1)
/*  565:     */           {
/*  566: 702 */             if (Sorter.this.m_incrementalBuffer.size() > 0)
/*  567:     */             {
/*  568: 703 */               nextH = (Sorter.InstanceHolder)Sorter.this.m_incrementalBuffer.remove(0);
/*  569: 704 */               nextH.m_fileNumber = -1;
/*  570:     */             }
/*  571:     */           }
/*  572:     */           else
/*  573:     */           {
/*  574: 707 */             ObjectInputStream tis = (ObjectInputStream)inputStreams.get(smallest);
/*  575:     */             Iterator i$;
/*  576:     */             try
/*  577:     */             {
/*  578: 710 */               Sorter.InstanceHolder tempH = (Sorter.InstanceHolder)tis.readObject();
/*  579: 711 */               if (tempH != null)
/*  580:     */               {
/*  581: 712 */                 nextH = tempH;
/*  582: 713 */                 nextH.m_fileNumber = smallest;
/*  583:     */               }
/*  584:     */               else
/*  585:     */               {
/*  586: 715 */                 throw new Exception("end of buffer");
/*  587:     */               }
/*  588:     */             }
/*  589:     */             catch (Exception ex)
/*  590:     */             {
/*  591:     */               try
/*  592:     */               {
/*  593: 720 */                 if (Sorter.this.m_log != null)
/*  594:     */                 {
/*  595: 721 */                   String msg = Sorter.this.statusMessagePrefix() + "Closing temp file";
/*  596: 722 */                   Sorter.this.m_log.statusMessage(msg);
/*  597:     */                 }
/*  598: 724 */                 tis.close();
/*  599:     */               }
/*  600:     */               catch (Exception e) {}
/*  601: 727 */               File file = (File)Sorter.this.m_bufferFiles.remove(smallest);
/*  602: 728 */               file.delete();
/*  603: 729 */               inputStreams.remove(smallest);
/*  604:     */               
/*  605:     */ 
/*  606: 732 */               i$ = merger.iterator();
/*  607:     */             }
/*  608: 732 */             while (i$.hasNext())
/*  609:     */             {
/*  610: 732 */               Sorter.InstanceHolder h = (Sorter.InstanceHolder)i$.next();
/*  611: 733 */               if ((h.m_fileNumber != -1) && (h.m_fileNumber > smallest)) {
/*  612: 734 */                 h.m_fileNumber -= 1;
/*  613:     */               }
/*  614:     */             }
/*  615:     */           }
/*  616: 740 */           if (nextH != null)
/*  617:     */           {
/*  618: 742 */             int index = Collections.binarySearch(merger, nextH, Sorter.this.m_sortComparator);
/*  619: 745 */             if (index < 0) {
/*  620: 746 */               merger.add(index * -1 - 1, nextH);
/*  621:     */             } else {
/*  622: 748 */               merger.add(index, nextH);
/*  623:     */             }
/*  624: 750 */             nextH = null;
/*  625:     */           }
/*  626: 752 */         } while ((merger.size() > 0) && (!Sorter.this.m_stopRequested.get()));
/*  627: 754 */         if (!Sorter.this.m_stopRequested.get())
/*  628:     */         {
/*  629: 756 */           Sorter.this.m_ie.setInstance(null);
/*  630: 757 */           Sorter.this.m_ie.setStatus(2);
/*  631: 758 */           Sorter.this.notifyInstanceListeners(Sorter.this.m_ie);
/*  632:     */           
/*  633: 760 */           String msg = Sorter.this.statusMessagePrefix() + "Finished.";
/*  634: 761 */           if (Sorter.this.m_log != null)
/*  635:     */           {
/*  636: 762 */             Sorter.this.m_log.statusMessage(msg);
/*  637: 763 */             Sorter.this.m_log.logMessage("[" + Sorter.this.getCustomName() + "] " + msg);
/*  638:     */           }
/*  639: 765 */           Sorter.this.m_busy = false;
/*  640:     */         }
/*  641:     */         else
/*  642:     */         {
/*  643: 768 */           for (ObjectInputStream is : inputStreams) {
/*  644:     */             try
/*  645:     */             {
/*  646: 770 */               is.close();
/*  647:     */             }
/*  648:     */             catch (Exception ex) {}
/*  649:     */           }
/*  650: 774 */           Sorter.this.m_busy = false;
/*  651:     */         }
/*  652:     */       }
/*  653: 778 */     };
/*  654: 779 */     t.setPriority(1);
/*  655: 780 */     t.start();
/*  656:     */   }
/*  657:     */   
/*  658:     */   protected void sortBuffer(boolean write)
/*  659:     */     throws Exception
/*  660:     */   {
/*  661: 791 */     String msg = statusMessagePrefix() + "Sorting in memory buffer....";
/*  662: 792 */     if (this.m_log != null)
/*  663:     */     {
/*  664: 793 */       this.m_log.statusMessage(msg);
/*  665: 794 */       this.m_log.logMessage("[" + getCustomName() + "] " + msg);
/*  666:     */     }
/*  667: 797 */     Collections.sort(this.m_incrementalBuffer, this.m_sortComparator);
/*  668: 799 */     if (!write) {
/*  669: 800 */       return;
/*  670:     */     }
/*  671: 803 */     String tmpDir = this.m_tempDirectory;
/*  672: 804 */     File tempFile = File.createTempFile("Sorter", ".tmp");
/*  673: 806 */     if ((tmpDir != null) && (tmpDir.length() > 0)) {
/*  674:     */       try
/*  675:     */       {
/*  676: 808 */         tmpDir = this.m_env.substitute(tmpDir);
/*  677:     */         
/*  678: 810 */         File tempDir = new File(tmpDir);
/*  679: 811 */         if ((tempDir.exists()) && (tempDir.canWrite()))
/*  680:     */         {
/*  681: 812 */           String filename = tempFile.getName();
/*  682: 813 */           File newFile = new File(tmpDir + File.separator + filename);
/*  683: 814 */           tempFile = newFile;
/*  684: 815 */           tempFile.deleteOnExit();
/*  685:     */         }
/*  686:     */       }
/*  687:     */       catch (Exception ex) {}
/*  688:     */     }
/*  689: 821 */     if (!this.m_stopRequested.get())
/*  690:     */     {
/*  691: 823 */       this.m_bufferFiles.add(tempFile);
/*  692: 824 */       FileOutputStream fos = new FileOutputStream(tempFile);
/*  693:     */       
/*  694: 826 */       BufferedOutputStream bos = new BufferedOutputStream(fos, 50000);
/*  695: 827 */       ObjectOutputStream oos = new ObjectOutputStream(bos);
/*  696:     */       
/*  697: 829 */       msg = statusMessagePrefix() + "Writing buffer to temp file " + this.m_bufferFiles.size() + "...";
/*  698: 831 */       if (this.m_log != null)
/*  699:     */       {
/*  700: 832 */         this.m_log.statusMessage(msg);
/*  701: 833 */         this.m_log.logMessage("[" + getCustomName() + "] " + msg);
/*  702:     */       }
/*  703: 836 */       for (int i = 0; i < this.m_incrementalBuffer.size(); i++)
/*  704:     */       {
/*  705: 837 */         InstanceHolder temp = (InstanceHolder)this.m_incrementalBuffer.get(i);
/*  706: 838 */         temp.m_instance.setDataset(null);
/*  707: 839 */         oos.writeObject(temp);
/*  708: 840 */         if (i % (this.m_bufferSizeI / 10) == 0) {
/*  709: 841 */           oos.reset();
/*  710:     */         }
/*  711:     */       }
/*  712: 845 */       bos.flush();
/*  713: 846 */       oos.close();
/*  714:     */     }
/*  715: 848 */     this.m_incrementalBuffer.clear();
/*  716:     */   }
/*  717:     */   
/*  718:     */   public void acceptTestSet(TestSetEvent e)
/*  719:     */   {
/*  720: 858 */     Instances test = e.getTestSet();
/*  721: 859 */     DataSetEvent d = new DataSetEvent(this, test);
/*  722: 860 */     acceptDataSet(d);
/*  723:     */   }
/*  724:     */   
/*  725:     */   public void acceptTrainingSet(TrainingSetEvent e)
/*  726:     */   {
/*  727: 870 */     Instances train = e.getTrainingSet();
/*  728: 871 */     DataSetEvent d = new DataSetEvent(this, train);
/*  729: 872 */     acceptDataSet(d);
/*  730:     */   }
/*  731:     */   
/*  732:     */   protected void init(Instances structure)
/*  733:     */   {
/*  734: 876 */     List<SortRule> sortRules = new ArrayList();
/*  735: 878 */     if ((this.m_sortDetails != null) && (this.m_sortDetails.length() > 0))
/*  736:     */     {
/*  737: 879 */       String[] sortParts = this.m_sortDetails.split("@@sort-rule@@");
/*  738: 881 */       for (String s : sortParts)
/*  739:     */       {
/*  740: 882 */         SortRule r = new SortRule(s.trim());
/*  741:     */         
/*  742: 884 */         r.init(this.m_env, structure);
/*  743: 885 */         sortRules.add(r);
/*  744:     */       }
/*  745: 888 */       this.m_sortComparator = new SortComparator(sortRules);
/*  746:     */     }
/*  747: 892 */     this.m_stringAttIndexes = new HashMap();
/*  748: 893 */     for (int i = 0; i < structure.numAttributes(); i++) {
/*  749: 894 */       if (structure.attribute(i).isString()) {
/*  750: 895 */         this.m_stringAttIndexes.put(structure.attribute(i).name(), new Integer(i));
/*  751:     */       }
/*  752:     */     }
/*  753: 898 */     if (this.m_stringAttIndexes.size() == 0) {
/*  754: 899 */       this.m_stringAttIndexes = null;
/*  755:     */     }
/*  756:     */   }
/*  757:     */   
/*  758:     */   public String getBufferSize()
/*  759:     */   {
/*  760: 909 */     return this.m_bufferSize;
/*  761:     */   }
/*  762:     */   
/*  763:     */   public void setBufferSize(String buffSize)
/*  764:     */   {
/*  765: 918 */     this.m_bufferSize = buffSize;
/*  766:     */   }
/*  767:     */   
/*  768:     */   public void setTempDirectory(String tempDir)
/*  769:     */   {
/*  770: 927 */     this.m_tempDirectory = tempDir;
/*  771:     */   }
/*  772:     */   
/*  773:     */   public String getTempDirectory()
/*  774:     */   {
/*  775: 936 */     return this.m_tempDirectory;
/*  776:     */   }
/*  777:     */   
/*  778:     */   public void setSortDetails(String sortDetails)
/*  779:     */   {
/*  780: 945 */     this.m_sortDetails = sortDetails;
/*  781:     */   }
/*  782:     */   
/*  783:     */   public String getSortDetails()
/*  784:     */   {
/*  785: 954 */     return this.m_sortDetails;
/*  786:     */   }
/*  787:     */   
/*  788:     */   public void acceptDataSet(DataSetEvent e)
/*  789:     */   {
/*  790: 964 */     this.m_busy = true;
/*  791: 965 */     this.m_stopRequested.set(false);
/*  792: 967 */     if ((this.m_log != null) && (e.getDataSet().numInstances() > 0)) {
/*  793: 968 */       this.m_log.statusMessage(statusMessagePrefix() + "Sorting batch...");
/*  794:     */     }
/*  795: 971 */     if (e.isStructureOnly())
/*  796:     */     {
/*  797: 975 */       DataSetEvent d = new DataSetEvent(this, e.getDataSet());
/*  798: 976 */       notifyDataListeners(d);
/*  799:     */       
/*  800: 978 */       this.m_busy = false;
/*  801: 979 */       return;
/*  802:     */     }
/*  803:     */     try
/*  804:     */     {
/*  805: 983 */       init(new Instances(e.getDataSet(), 0));
/*  806:     */     }
/*  807:     */     catch (IllegalArgumentException ex)
/*  808:     */     {
/*  809: 985 */       if (this.m_log != null)
/*  810:     */       {
/*  811: 986 */         String message = "ERROR: There is a problem with the incoming instance structure";
/*  812:     */         
/*  813:     */ 
/*  814:     */ 
/*  815:     */ 
/*  816:     */ 
/*  817:     */ 
/*  818: 993 */         stopWithErrorMessage(message, ex);
/*  819: 994 */         this.m_busy = false;
/*  820: 995 */         return;
/*  821:     */       }
/*  822:     */     }
/*  823: 999 */     List<InstanceHolder> instances = new ArrayList();
/*  824:1000 */     for (int i = 0; i < e.getDataSet().numInstances(); i++)
/*  825:     */     {
/*  826:1001 */       InstanceHolder h = new InstanceHolder();
/*  827:1002 */       h.m_instance = e.getDataSet().instance(i);
/*  828:1003 */       instances.add(h);
/*  829:     */     }
/*  830:1005 */     Collections.sort(instances, this.m_sortComparator);
/*  831:1006 */     Instances output = new Instances(e.getDataSet(), 0);
/*  832:1007 */     for (int i = 0; i < instances.size(); i++) {
/*  833:1008 */       output.add(((InstanceHolder)instances.get(i)).m_instance);
/*  834:     */     }
/*  835:1011 */     DataSetEvent d = new DataSetEvent(this, output);
/*  836:1012 */     notifyDataListeners(d);
/*  837:1014 */     if (this.m_log != null) {
/*  838:1015 */       this.m_log.statusMessage(statusMessagePrefix() + "Finished.");
/*  839:     */     }
/*  840:1017 */     this.m_busy = false;
/*  841:     */   }
/*  842:     */   
/*  843:     */   public void addDataSourceListener(DataSourceListener dsl)
/*  844:     */   {
/*  845:1027 */     this.m_dataListeners.add(dsl);
/*  846:     */   }
/*  847:     */   
/*  848:     */   public void removeDataSourceListener(DataSourceListener dsl)
/*  849:     */   {
/*  850:1037 */     this.m_dataListeners.remove(dsl);
/*  851:     */   }
/*  852:     */   
/*  853:     */   public void addInstanceListener(InstanceListener dsl)
/*  854:     */   {
/*  855:1047 */     this.m_instanceListeners.add(dsl);
/*  856:     */   }
/*  857:     */   
/*  858:     */   public void removeInstanceListener(InstanceListener dsl)
/*  859:     */   {
/*  860:1057 */     this.m_instanceListeners.remove(dsl);
/*  861:     */   }
/*  862:     */   
/*  863:     */   public void useDefaultVisual()
/*  864:     */   {
/*  865:1065 */     this.m_visual.loadIcons("weka/gui/beans/icons/Sorter.gif", "weka/gui/beans/icons/Sorter_animated.gif");
/*  866:     */     
/*  867:1067 */     this.m_visual.setText("Sorter");
/*  868:     */   }
/*  869:     */   
/*  870:     */   public void setVisual(BeanVisual newVisual)
/*  871:     */   {
/*  872:1077 */     this.m_visual = newVisual;
/*  873:     */   }
/*  874:     */   
/*  875:     */   public BeanVisual getVisual()
/*  876:     */   {
/*  877:1087 */     return this.m_visual;
/*  878:     */   }
/*  879:     */   
/*  880:     */   public void setCustomName(String name)
/*  881:     */   {
/*  882:1097 */     this.m_visual.setText(name);
/*  883:     */   }
/*  884:     */   
/*  885:     */   public String getCustomName()
/*  886:     */   {
/*  887:1107 */     return this.m_visual.getText();
/*  888:     */   }
/*  889:     */   
/*  890:     */   public void stop()
/*  891:     */   {
/*  892:1115 */     if ((this.m_listenee != null) && 
/*  893:1116 */       ((this.m_listenee instanceof BeanCommon))) {
/*  894:1117 */       ((BeanCommon)this.m_listenee).stop();
/*  895:     */     }
/*  896:1121 */     if (this.m_log != null) {
/*  897:1122 */       this.m_log.statusMessage(statusMessagePrefix() + "Stopped");
/*  898:     */     }
/*  899:1125 */     this.m_busy = false;
/*  900:1126 */     this.m_stopRequested.set(true);
/*  901:     */   }
/*  902:     */   
/*  903:     */   protected void stopWithErrorMessage(String error, Exception ex)
/*  904:     */   {
/*  905:1137 */     stop();
/*  906:1138 */     if (this.m_log != null)
/*  907:     */     {
/*  908:1139 */       this.m_log.statusMessage(statusMessagePrefix() + error + " - see log for details");
/*  909:     */       
/*  910:1141 */       this.m_log.logMessage(statusMessagePrefix() + error + (ex != null ? " " + ex.getMessage() : ""));
/*  911:     */     }
/*  912:     */   }
/*  913:     */   
/*  914:     */   public boolean isBusy()
/*  915:     */   {
/*  916:1154 */     return this.m_busy;
/*  917:     */   }
/*  918:     */   
/*  919:     */   public void setLog(Logger logger)
/*  920:     */   {
/*  921:1164 */     this.m_log = logger;
/*  922:     */   }
/*  923:     */   
/*  924:     */   public boolean connectionAllowed(EventSetDescriptor esd)
/*  925:     */   {
/*  926:1176 */     return connectionAllowed(esd.getName());
/*  927:     */   }
/*  928:     */   
/*  929:     */   public boolean connectionAllowed(String eventName)
/*  930:     */   {
/*  931:1188 */     if ((!eventName.equals("instance")) && (!eventName.equals("dataSet")) && (!eventName.equals("trainingSet")) && (!eventName.equals("testSet"))) {
/*  932:1190 */       return false;
/*  933:     */     }
/*  934:1193 */     if (this.m_listenee != null) {
/*  935:1194 */       return false;
/*  936:     */     }
/*  937:1197 */     return true;
/*  938:     */   }
/*  939:     */   
/*  940:     */   public void connectionNotification(String eventName, Object source)
/*  941:     */   {
/*  942:1211 */     if (connectionAllowed(eventName))
/*  943:     */     {
/*  944:1212 */       this.m_listenee = source;
/*  945:1213 */       this.m_connectionType = eventName;
/*  946:     */     }
/*  947:     */   }
/*  948:     */   
/*  949:     */   public void disconnectionNotification(String eventName, Object source)
/*  950:     */   {
/*  951:1227 */     if (source == this.m_listenee) {
/*  952:1228 */       this.m_listenee = null;
/*  953:     */     }
/*  954:     */   }
/*  955:     */   
/*  956:     */   private void notifyInstanceListeners(InstanceEvent e)
/*  957:     */   {
/*  958:     */     List<InstanceListener> l;
/*  959:1235 */     synchronized (this)
/*  960:     */     {
/*  961:1236 */       l = (List)this.m_instanceListeners.clone();
/*  962:     */     }
/*  963:1238 */     if (l.size() > 0) {
/*  964:1239 */       for (InstanceListener il : l) {
/*  965:1240 */         il.acceptInstance(e);
/*  966:     */       }
/*  967:     */     }
/*  968:     */   }
/*  969:     */   
/*  970:     */   private void notifyDataListeners(DataSetEvent e)
/*  971:     */   {
/*  972:     */     List<DataSourceListener> l;
/*  973:1248 */     synchronized (this)
/*  974:     */     {
/*  975:1249 */       l = (List)this.m_dataListeners.clone();
/*  976:     */     }
/*  977:1251 */     if (l.size() > 0) {
/*  978:1252 */       for (DataSourceListener ds : l) {
/*  979:1253 */         ds.acceptDataSet(e);
/*  980:     */       }
/*  981:     */     }
/*  982:     */   }
/*  983:     */   
/*  984:     */   protected String statusMessagePrefix()
/*  985:     */   {
/*  986:1259 */     return getCustomName() + "$" + hashCode() + "|";
/*  987:     */   }
/*  988:     */   
/*  989:     */   private Instances getUpstreamStructure()
/*  990:     */   {
/*  991:1263 */     if ((this.m_listenee != null) && ((this.m_listenee instanceof StructureProducer))) {
/*  992:1264 */       return ((StructureProducer)this.m_listenee).getStructure(this.m_connectionType);
/*  993:     */     }
/*  994:1266 */     return null;
/*  995:     */   }
/*  996:     */   
/*  997:     */   public Instances getStructure(String eventName)
/*  998:     */   {
/*  999:1284 */     if ((!eventName.equals("dataSet")) && (!eventName.equals("instance"))) {
/* 1000:1285 */       return null;
/* 1001:     */     }
/* 1002:1288 */     if ((eventName.equals("dataSet")) && (this.m_dataListeners.size() == 0)) {
/* 1003:1289 */       return null;
/* 1004:     */     }
/* 1005:1292 */     if ((eventName.equals("instance")) && (this.m_instanceListeners.size() == 0)) {
/* 1006:1293 */       return null;
/* 1007:     */     }
/* 1008:1296 */     if (this.m_connectedFormat == null) {
/* 1009:1297 */       this.m_connectedFormat = getUpstreamStructure();
/* 1010:     */     }
/* 1011:1300 */     return this.m_connectedFormat;
/* 1012:     */   }
/* 1013:     */   
/* 1014:     */   public Instances getConnectedFormat()
/* 1015:     */   {
/* 1016:1309 */     if (this.m_connectedFormat == null) {
/* 1017:1310 */       this.m_connectedFormat = getUpstreamStructure();
/* 1018:     */     }
/* 1019:1313 */     return this.m_connectedFormat;
/* 1020:     */   }
/* 1021:     */   
/* 1022:     */   public void setEnvironment(Environment env)
/* 1023:     */   {
/* 1024:1321 */     this.m_env = env;
/* 1025:     */   }
/* 1026:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.Sorter
 * JD-Core Version:    0.7.0.1
 */