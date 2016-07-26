/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.beans.EventSetDescriptor;
/*   5:    */ import java.io.BufferedInputStream;
/*   6:    */ import java.io.BufferedOutputStream;
/*   7:    */ import java.io.File;
/*   8:    */ import java.io.FileInputStream;
/*   9:    */ import java.io.FileOutputStream;
/*  10:    */ import java.io.ObjectInputStream;
/*  11:    */ import java.io.ObjectOutputStream;
/*  12:    */ import java.util.Enumeration;
/*  13:    */ import java.util.Hashtable;
/*  14:    */ import java.util.Vector;
/*  15:    */ import javax.swing.JFileChooser;
/*  16:    */ import javax.swing.JOptionPane;
/*  17:    */ import javax.swing.JPanel;
/*  18:    */ import weka.clusterers.EM;
/*  19:    */ import weka.core.Drawable;
/*  20:    */ import weka.core.Instances;
/*  21:    */ import weka.core.OptionHandler;
/*  22:    */ import weka.core.Utils;
/*  23:    */ import weka.filters.Filter;
/*  24:    */ import weka.filters.unsupervised.attribute.Remove;
/*  25:    */ import weka.gui.ExtensionFileFilter;
/*  26:    */ import weka.gui.Logger;
/*  27:    */ 
/*  28:    */ public class Clusterer
/*  29:    */   extends JPanel
/*  30:    */   implements BeanCommon, Visible, WekaWrapper, EventConstraints, UserRequestAcceptor, TrainingSetListener, TestSetListener, ConfigurationProducer
/*  31:    */ {
/*  32:    */   private static final long serialVersionUID = 7729795159836843810L;
/*  33: 72 */   protected BeanVisual m_visual = new BeanVisual("Clusterer", "weka/gui/beans/icons/EM.gif", "weka/gui/beans/icons/EM_animated.gif");
/*  34: 75 */   private static int IDLE = 0;
/*  35: 76 */   private static int BUILDING_MODEL = 1;
/*  36: 77 */   private static int CLUSTERING = 2;
/*  37: 79 */   private int m_state = IDLE;
/*  38: 81 */   private Thread m_buildThread = null;
/*  39:    */   protected String m_globalInfo;
/*  40: 91 */   private final Hashtable<String, Object> m_listenees = new Hashtable();
/*  41: 96 */   private final Vector<BatchClustererListener> m_batchClustererListeners = new Vector();
/*  42:101 */   private final Vector<GraphListener> m_graphListeners = new Vector();
/*  43:106 */   private final Vector<TextListener> m_textListeners = new Vector();
/*  44:    */   private Instances m_trainingSet;
/*  45:    */   private transient Instances m_testingSet;
/*  46:113 */   private weka.clusterers.Clusterer m_Clusterer = new EM();
/*  47:115 */   private transient Logger m_log = null;
/*  48:117 */   private final Double m_dummy = new Double(0.0D);
/*  49:120 */   private transient JFileChooser m_fileChooser = null;
/*  50:    */   
/*  51:    */   public String globalInfo()
/*  52:    */   {
/*  53:128 */     return this.m_globalInfo;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public Clusterer()
/*  57:    */   {
/*  58:135 */     setLayout(new BorderLayout());
/*  59:136 */     add(this.m_visual, "Center");
/*  60:137 */     setClusterer(this.m_Clusterer);
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void setCustomName(String name)
/*  64:    */   {
/*  65:147 */     this.m_visual.setText(name);
/*  66:    */   }
/*  67:    */   
/*  68:    */   public String getCustomName()
/*  69:    */   {
/*  70:157 */     return this.m_visual.getText();
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void setClusterer(weka.clusterers.Clusterer c)
/*  74:    */   {
/*  75:166 */     boolean loadImages = true;
/*  76:167 */     if (c.getClass().getName().compareTo(this.m_Clusterer.getClass().getName()) == 0) {
/*  77:168 */       loadImages = false;
/*  78:    */     } else {
/*  79:172 */       this.m_trainingSet = null;
/*  80:    */     }
/*  81:174 */     this.m_Clusterer = c;
/*  82:175 */     String clustererName = c.getClass().toString();
/*  83:176 */     clustererName = clustererName.substring(clustererName.lastIndexOf('.') + 1, clustererName.length());
/*  84:178 */     if ((loadImages) && 
/*  85:179 */       (!this.m_visual.loadIcons("weka/gui/beans/icons/" + clustererName + ".gif", "weka/gui/beans/icons/" + clustererName + "_animated.gif"))) {
/*  86:181 */       useDefaultVisual();
/*  87:    */     }
/*  88:184 */     this.m_visual.setText(clustererName);
/*  89:    */     
/*  90:    */ 
/*  91:187 */     this.m_globalInfo = KnowledgeFlowApp.getGlobalInfo(this.m_Clusterer);
/*  92:    */   }
/*  93:    */   
/*  94:    */   public boolean hasIncomingBatchInstances()
/*  95:    */   {
/*  96:197 */     if (this.m_listenees.size() == 0) {
/*  97:198 */       return false;
/*  98:    */     }
/*  99:200 */     if ((this.m_listenees.containsKey("trainingSet")) || (this.m_listenees.containsKey("testSet")) || (this.m_listenees.containsKey("dataSet"))) {
/* 100:203 */       return true;
/* 101:    */     }
/* 102:205 */     return false;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public weka.clusterers.Clusterer getClusterer()
/* 106:    */   {
/* 107:214 */     return this.m_Clusterer;
/* 108:    */   }
/* 109:    */   
/* 110:    */   public void setWrappedAlgorithm(Object algorithm)
/* 111:    */   {
/* 112:226 */     if (!(algorithm instanceof weka.clusterers.Clusterer)) {
/* 113:227 */       throw new IllegalArgumentException(algorithm.getClass() + " : incorrect " + "type of algorithm (Clusterer)");
/* 114:    */     }
/* 115:230 */     setClusterer((weka.clusterers.Clusterer)algorithm);
/* 116:    */   }
/* 117:    */   
/* 118:    */   public Object getWrappedAlgorithm()
/* 119:    */   {
/* 120:240 */     return getClusterer();
/* 121:    */   }
/* 122:    */   
/* 123:    */   public void acceptTrainingSet(final TrainingSetEvent e)
/* 124:    */   {
/* 125:250 */     if (e.isStructureOnly())
/* 126:    */     {
/* 127:254 */       BatchClustererEvent ce = new BatchClustererEvent(this, this.m_Clusterer, new DataSetEvent(this, e.getTrainingSet()), e.getSetNumber(), e.getMaxSetNumber(), 1);
/* 128:    */       
/* 129:    */ 
/* 130:    */ 
/* 131:258 */       notifyBatchClustererListeners(ce);
/* 132:259 */       return;
/* 133:    */     }
/* 134:261 */     if (this.m_buildThread == null) {
/* 135:    */       try
/* 136:    */       {
/* 137:263 */         if (this.m_state == IDLE)
/* 138:    */         {
/* 139:264 */           synchronized (this)
/* 140:    */           {
/* 141:265 */             this.m_state = BUILDING_MODEL;
/* 142:    */           }
/* 143:267 */           this.m_trainingSet = e.getTrainingSet();
/* 144:    */           
/* 145:269 */           this.m_buildThread = new Thread()
/* 146:    */           {
/* 147:    */             public void run()
/* 148:    */             {
/* 149:    */               try
/* 150:    */               {
/* 151:274 */                 if (Clusterer.this.m_trainingSet != null)
/* 152:    */                 {
/* 153:275 */                   Clusterer.this.m_visual.setAnimated();
/* 154:277 */                   if (Clusterer.this.m_log != null) {
/* 155:278 */                     Clusterer.this.m_log.statusMessage(Clusterer.this.statusMessagePrefix() + "Building clusters...");
/* 156:    */                   }
/* 157:281 */                   Clusterer.this.buildClusterer();
/* 158:282 */                   if (Clusterer.this.m_batchClustererListeners.size() > 0)
/* 159:    */                   {
/* 160:283 */                     BatchClustererEvent ce = new BatchClustererEvent(this, Clusterer.this.m_Clusterer, new DataSetEvent(this, e.getTrainingSet()), e.getSetNumber(), e.getMaxSetNumber(), 1);
/* 161:    */                     
/* 162:    */ 
/* 163:286 */                     Clusterer.this.notifyBatchClustererListeners(ce);
/* 164:    */                   }
/* 165:288 */                   if (((Clusterer.this.m_Clusterer instanceof Drawable)) && (Clusterer.this.m_graphListeners.size() > 0))
/* 166:    */                   {
/* 167:290 */                     String grphString = ((Drawable)Clusterer.this.m_Clusterer).graph();
/* 168:    */                     
/* 169:292 */                     int grphType = ((Drawable)Clusterer.this.m_Clusterer).graphType();
/* 170:    */                     
/* 171:294 */                     String grphTitle = Clusterer.this.m_Clusterer.getClass().getName();
/* 172:295 */                     grphTitle = grphTitle.substring(grphTitle.lastIndexOf('.') + 1, grphTitle.length());
/* 173:    */                     
/* 174:297 */                     grphTitle = "Set " + e.getSetNumber() + " (" + e.getTrainingSet().relationName() + ") " + grphTitle;
/* 175:    */                     
/* 176:    */ 
/* 177:300 */                     GraphEvent ge = new GraphEvent(Clusterer.this, grphString, grphTitle, grphType);
/* 178:    */                     
/* 179:302 */                     Clusterer.this.notifyGraphListeners(ge);
/* 180:    */                   }
/* 181:305 */                   if (Clusterer.this.m_textListeners.size() > 0)
/* 182:    */                   {
/* 183:306 */                     String modelString = Clusterer.this.m_Clusterer.toString();
/* 184:307 */                     String titleString = Clusterer.this.m_Clusterer.getClass().getName();
/* 185:    */                     
/* 186:309 */                     titleString = titleString.substring(titleString.lastIndexOf('.') + 1, titleString.length());
/* 187:    */                     
/* 188:311 */                     modelString = "=== Clusterer model ===\n\nScheme:   " + titleString + "\n" + "Relation: " + Clusterer.this.m_trainingSet.relationName() + (e.getMaxSetNumber() > 1 ? "\nTraining Fold: " + e.getSetNumber() : "") + "\n\n" + modelString;
/* 189:    */                     
/* 190:    */ 
/* 191:    */ 
/* 192:    */ 
/* 193:    */ 
/* 194:    */ 
/* 195:    */ 
/* 196:319 */                     titleString = "Model: " + titleString;
/* 197:    */                     
/* 198:321 */                     TextEvent nt = new TextEvent(Clusterer.this, modelString, titleString);
/* 199:    */                     
/* 200:323 */                     Clusterer.this.notifyTextListeners(nt);
/* 201:    */                   }
/* 202:    */                 }
/* 203:    */               }
/* 204:    */               catch (Exception ex)
/* 205:    */               {
/* 206:327 */                 Clusterer.this.stop();
/* 207:328 */                 if (Clusterer.this.m_log != null)
/* 208:    */                 {
/* 209:329 */                   Clusterer.this.m_log.statusMessage(Clusterer.this.statusMessagePrefix() + "ERROR (See log for details");
/* 210:    */                   
/* 211:331 */                   Clusterer.this.m_log.logMessage("[Clusterer] " + Clusterer.this.statusMessagePrefix() + " problem training clusterer. " + ex.getMessage());
/* 212:    */                 }
/* 213:334 */                 ex.printStackTrace();
/* 214:    */               }
/* 215:    */               finally
/* 216:    */               {
/* 217:337 */                 Clusterer.this.m_visual.setStatic();
/* 218:338 */                 Clusterer.this.m_state = Clusterer.IDLE;
/* 219:339 */                 if (isInterrupted())
/* 220:    */                 {
/* 221:341 */                   Clusterer.this.m_trainingSet = null;
/* 222:342 */                   if (Clusterer.this.m_log != null)
/* 223:    */                   {
/* 224:343 */                     Clusterer.this.m_log.logMessage("[Clusterer]" + Clusterer.this.statusMessagePrefix() + " Build clusterer interrupted!");
/* 225:    */                     
/* 226:345 */                     Clusterer.this.m_log.statusMessage(Clusterer.this.statusMessagePrefix() + "INTERRUPTED");
/* 227:    */                   }
/* 228:    */                 }
/* 229:    */                 else
/* 230:    */                 {
/* 231:349 */                   Clusterer.this.m_trainingSet = new Instances(Clusterer.this.m_trainingSet, 0);
/* 232:350 */                   if (Clusterer.this.m_log != null) {
/* 233:351 */                     Clusterer.this.m_log.statusMessage(Clusterer.this.statusMessagePrefix() + "Finished.");
/* 234:    */                   }
/* 235:    */                 }
/* 236:354 */                 Clusterer.this.block(false);
/* 237:    */               }
/* 238:    */             }
/* 239:357 */           };
/* 240:358 */           this.m_buildThread.setPriority(1);
/* 241:359 */           this.m_buildThread.start();
/* 242:    */           
/* 243:    */ 
/* 244:362 */           block(true);
/* 245:    */           
/* 246:364 */           this.m_buildThread = null;
/* 247:365 */           this.m_state = IDLE;
/* 248:    */         }
/* 249:    */       }
/* 250:    */       catch (Exception ex)
/* 251:    */       {
/* 252:368 */         ex.printStackTrace();
/* 253:    */       }
/* 254:    */     }
/* 255:    */   }
/* 256:    */   
/* 257:    */   public void acceptTestSet(TestSetEvent e)
/* 258:    */   {
/* 259:381 */     if (this.m_trainingSet != null) {
/* 260:    */       try
/* 261:    */       {
/* 262:383 */         if (this.m_state == IDLE)
/* 263:    */         {
/* 264:384 */           synchronized (this)
/* 265:    */           {
/* 266:385 */             this.m_state = CLUSTERING;
/* 267:    */           }
/* 268:387 */           this.m_testingSet = e.getTestSet();
/* 269:388 */           if (this.m_trainingSet.equalHeaders(this.m_testingSet))
/* 270:    */           {
/* 271:389 */             BatchClustererEvent ce = new BatchClustererEvent(this, this.m_Clusterer, new DataSetEvent(this, e.getTestSet()), e.getSetNumber(), e.getMaxSetNumber(), 0);
/* 272:    */             
/* 273:    */ 
/* 274:    */ 
/* 275:393 */             notifyBatchClustererListeners(ce);
/* 276:    */           }
/* 277:396 */           this.m_state = IDLE;
/* 278:    */         }
/* 279:    */       }
/* 280:    */       catch (Exception ex)
/* 281:    */       {
/* 282:399 */         stop();
/* 283:400 */         if (this.m_log != null)
/* 284:    */         {
/* 285:401 */           this.m_log.statusMessage(statusMessagePrefix() + "ERROR (see log for details");
/* 286:    */           
/* 287:403 */           this.m_log.logMessage("[Clusterer] " + statusMessagePrefix() + " problem during testing. " + ex.getMessage());
/* 288:    */         }
/* 289:406 */         ex.printStackTrace();
/* 290:    */       }
/* 291:    */     }
/* 292:    */   }
/* 293:    */   
/* 294:    */   private void buildClusterer()
/* 295:    */     throws Exception
/* 296:    */   {
/* 297:415 */     if (this.m_trainingSet.classIndex() < 0)
/* 298:    */     {
/* 299:416 */       this.m_Clusterer.buildClusterer(this.m_trainingSet);
/* 300:    */     }
/* 301:    */     else
/* 302:    */     {
/* 303:418 */       Remove removeClass = new Remove();
/* 304:419 */       removeClass.setAttributeIndices("" + (this.m_trainingSet.classIndex() + 1));
/* 305:420 */       removeClass.setInvertSelection(false);
/* 306:421 */       removeClass.setInputFormat(this.m_trainingSet);
/* 307:422 */       Instances clusterTrain = Filter.useFilter(this.m_trainingSet, removeClass);
/* 308:423 */       this.m_Clusterer.buildClusterer(clusterTrain);
/* 309:    */     }
/* 310:    */   }
/* 311:    */   
/* 312:    */   public void setVisual(BeanVisual newVisual)
/* 313:    */   {
/* 314:434 */     this.m_visual = newVisual;
/* 315:    */   }
/* 316:    */   
/* 317:    */   public BeanVisual getVisual()
/* 318:    */   {
/* 319:442 */     return this.m_visual;
/* 320:    */   }
/* 321:    */   
/* 322:    */   public void useDefaultVisual()
/* 323:    */   {
/* 324:450 */     this.m_visual.loadIcons("weka/gui/beans/icons/DefaultClusterer.gif", "weka/gui/beans/icons/DefaultClusterer_animated.gif");
/* 325:    */   }
/* 326:    */   
/* 327:    */   public synchronized void addBatchClustererListener(BatchClustererListener cl)
/* 328:    */   {
/* 329:460 */     this.m_batchClustererListeners.addElement(cl);
/* 330:    */   }
/* 331:    */   
/* 332:    */   public synchronized void removeBatchClustererListener(BatchClustererListener cl)
/* 333:    */   {
/* 334:470 */     this.m_batchClustererListeners.remove(cl);
/* 335:    */   }
/* 336:    */   
/* 337:    */   private void notifyBatchClustererListeners(BatchClustererEvent ce)
/* 338:    */   {
/* 339:    */     Vector<BatchClustererListener> l;
/* 340:481 */     synchronized (this)
/* 341:    */     {
/* 342:482 */       l = (Vector)this.m_batchClustererListeners.clone();
/* 343:    */     }
/* 344:484 */     if (l.size() > 0) {
/* 345:485 */       for (int i = 0; i < l.size(); i++) {
/* 346:486 */         ((BatchClustererListener)l.elementAt(i)).acceptClusterer(ce);
/* 347:    */       }
/* 348:    */     }
/* 349:    */   }
/* 350:    */   
/* 351:    */   public synchronized void addGraphListener(GraphListener cl)
/* 352:    */   {
/* 353:497 */     this.m_graphListeners.addElement(cl);
/* 354:    */   }
/* 355:    */   
/* 356:    */   public synchronized void removeGraphListener(GraphListener cl)
/* 357:    */   {
/* 358:506 */     this.m_graphListeners.remove(cl);
/* 359:    */   }
/* 360:    */   
/* 361:    */   private void notifyGraphListeners(GraphEvent ge)
/* 362:    */   {
/* 363:    */     Vector<GraphListener> l;
/* 364:517 */     synchronized (this)
/* 365:    */     {
/* 366:518 */       l = (Vector)this.m_graphListeners.clone();
/* 367:    */     }
/* 368:520 */     if (l.size() > 0) {
/* 369:521 */       for (int i = 0; i < l.size(); i++) {
/* 370:522 */         ((GraphListener)l.elementAt(i)).acceptGraph(ge);
/* 371:    */       }
/* 372:    */     }
/* 373:    */   }
/* 374:    */   
/* 375:    */   public synchronized void addTextListener(TextListener cl)
/* 376:    */   {
/* 377:533 */     this.m_textListeners.addElement(cl);
/* 378:    */   }
/* 379:    */   
/* 380:    */   public synchronized void removeTextListener(TextListener cl)
/* 381:    */   {
/* 382:542 */     this.m_textListeners.remove(cl);
/* 383:    */   }
/* 384:    */   
/* 385:    */   private void notifyTextListeners(TextEvent ge)
/* 386:    */   {
/* 387:    */     Vector<TextListener> l;
/* 388:553 */     synchronized (this)
/* 389:    */     {
/* 390:554 */       l = (Vector)this.m_textListeners.clone();
/* 391:    */     }
/* 392:556 */     if (l.size() > 0) {
/* 393:557 */       for (int i = 0; i < l.size(); i++) {
/* 394:558 */         ((TextListener)l.elementAt(i)).acceptText(ge);
/* 395:    */       }
/* 396:    */     }
/* 397:    */   }
/* 398:    */   
/* 399:    */   public synchronized void addConfigurationListener(ConfigurationListener cl) {}
/* 400:    */   
/* 401:    */   public synchronized void removeConfigurationListener(ConfigurationListener cl) {}
/* 402:    */   
/* 403:    */   public boolean connectionAllowed(String eventName)
/* 404:    */   {
/* 405:598 */     if (this.m_listenees.containsKey(eventName)) {
/* 406:599 */       return false;
/* 407:    */     }
/* 408:601 */     return true;
/* 409:    */   }
/* 410:    */   
/* 411:    */   public boolean connectionAllowed(EventSetDescriptor esd)
/* 412:    */   {
/* 413:613 */     return connectionAllowed(esd.getName());
/* 414:    */   }
/* 415:    */   
/* 416:    */   public synchronized void connectionNotification(String eventName, Object source)
/* 417:    */   {
/* 418:628 */     if (connectionAllowed(eventName)) {
/* 419:629 */       this.m_listenees.put(eventName, source);
/* 420:    */     }
/* 421:    */   }
/* 422:    */   
/* 423:    */   public synchronized void disconnectionNotification(String eventName, Object source)
/* 424:    */   {
/* 425:648 */     this.m_listenees.remove(eventName);
/* 426:    */   }
/* 427:    */   
/* 428:    */   private synchronized void block(boolean tf)
/* 429:    */   {
/* 430:659 */     if (tf) {
/* 431:    */       try
/* 432:    */       {
/* 433:662 */         if ((this.m_buildThread.isAlive()) && (this.m_state != IDLE)) {
/* 434:663 */           wait();
/* 435:    */         }
/* 436:    */       }
/* 437:    */       catch (InterruptedException ex) {}
/* 438:    */     } else {
/* 439:668 */       notifyAll();
/* 440:    */     }
/* 441:    */   }
/* 442:    */   
/* 443:    */   public boolean isBusy()
/* 444:    */   {
/* 445:680 */     return this.m_buildThread != null;
/* 446:    */   }
/* 447:    */   
/* 448:    */   public void stop()
/* 449:    */   {
/* 450:690 */     Enumeration<String> en = this.m_listenees.keys();
/* 451:691 */     while (en.hasMoreElements())
/* 452:    */     {
/* 453:692 */       Object tempO = this.m_listenees.get(en.nextElement());
/* 454:693 */       if ((tempO instanceof BeanCommon)) {
/* 455:694 */         ((BeanCommon)tempO).stop();
/* 456:    */       }
/* 457:    */     }
/* 458:699 */     if (this.m_buildThread != null)
/* 459:    */     {
/* 460:700 */       this.m_buildThread.interrupt();
/* 461:701 */       this.m_buildThread.stop();
/* 462:702 */       this.m_buildThread = null;
/* 463:703 */       this.m_visual.setStatic();
/* 464:    */     }
/* 465:    */   }
/* 466:    */   
/* 467:    */   public void setLog(Logger logger)
/* 468:    */   {
/* 469:714 */     this.m_log = logger;
/* 470:    */   }
/* 471:    */   
/* 472:    */   public void saveModel()
/* 473:    */   {
/* 474:    */     try
/* 475:    */     {
/* 476:719 */       if (this.m_fileChooser == null)
/* 477:    */       {
/* 478:721 */         this.m_fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
/* 479:    */         
/* 480:723 */         ExtensionFileFilter ef = new ExtensionFileFilter("model", "Serialized weka clusterer");
/* 481:    */         
/* 482:725 */         this.m_fileChooser.setFileFilter(ef);
/* 483:    */       }
/* 484:727 */       int returnVal = this.m_fileChooser.showSaveDialog(this);
/* 485:728 */       if (returnVal == 0)
/* 486:    */       {
/* 487:729 */         File saveTo = this.m_fileChooser.getSelectedFile();
/* 488:730 */         String fn = saveTo.getAbsolutePath();
/* 489:731 */         if (!fn.endsWith(".model"))
/* 490:    */         {
/* 491:732 */           fn = fn + ".model";
/* 492:733 */           saveTo = new File(fn);
/* 493:    */         }
/* 494:735 */         ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(saveTo)));
/* 495:    */         
/* 496:737 */         os.writeObject(this.m_Clusterer);
/* 497:738 */         if (this.m_trainingSet != null)
/* 498:    */         {
/* 499:739 */           Instances header = new Instances(this.m_trainingSet, 0);
/* 500:740 */           os.writeObject(header);
/* 501:    */         }
/* 502:742 */         os.close();
/* 503:743 */         if (this.m_log != null) {
/* 504:744 */           this.m_log.logMessage("[Clusterer] Saved clusterer " + getCustomName());
/* 505:    */         }
/* 506:    */       }
/* 507:    */     }
/* 508:    */     catch (Exception ex)
/* 509:    */     {
/* 510:748 */       JOptionPane.showMessageDialog(this, "Problem saving clusterer.\n", "Save Model", 0);
/* 511:750 */       if (this.m_log != null) {
/* 512:751 */         this.m_log.logMessage("[Clusterer] Problem saving clusterer. " + getCustomName() + ex.getMessage());
/* 513:    */       }
/* 514:    */     }
/* 515:    */   }
/* 516:    */   
/* 517:    */   public void loadModel()
/* 518:    */   {
/* 519:    */     try
/* 520:    */     {
/* 521:759 */       if (this.m_fileChooser == null)
/* 522:    */       {
/* 523:761 */         this.m_fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
/* 524:    */         
/* 525:763 */         ExtensionFileFilter ef = new ExtensionFileFilter("model", "Serialized weka clusterer");
/* 526:    */         
/* 527:765 */         this.m_fileChooser.setFileFilter(ef);
/* 528:    */       }
/* 529:767 */       int returnVal = this.m_fileChooser.showOpenDialog(this);
/* 530:768 */       if (returnVal == 0)
/* 531:    */       {
/* 532:769 */         File loadFrom = this.m_fileChooser.getSelectedFile();
/* 533:770 */         ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(new FileInputStream(loadFrom)));
/* 534:    */         
/* 535:    */ 
/* 536:773 */         weka.clusterers.Clusterer temp = (weka.clusterers.Clusterer)is.readObject();
/* 537:    */         
/* 538:    */ 
/* 539:    */ 
/* 540:777 */         setClusterer(temp);
/* 541:    */         try
/* 542:    */         {
/* 543:781 */           this.m_trainingSet = ((Instances)is.readObject());
/* 544:    */         }
/* 545:    */         catch (Exception ex) {}
/* 546:785 */         is.close();
/* 547:786 */         if (this.m_log != null) {
/* 548:787 */           this.m_log.logMessage("[Clusterer] Loaded clusterer: " + this.m_Clusterer.getClass().toString());
/* 549:    */         }
/* 550:    */       }
/* 551:    */     }
/* 552:    */     catch (Exception ex)
/* 553:    */     {
/* 554:792 */       JOptionPane.showMessageDialog(this, "Problem loading classifier.\n", "Load Model", 0);
/* 555:795 */       if (this.m_log != null) {
/* 556:796 */         this.m_log.logMessage("[Clusterer] Problem loading classifier. " + ex.getMessage());
/* 557:    */       }
/* 558:    */     }
/* 559:    */   }
/* 560:    */   
/* 561:    */   public Enumeration<String> enumerateRequests()
/* 562:    */   {
/* 563:809 */     Vector<String> newVector = new Vector(0);
/* 564:810 */     if (this.m_buildThread != null) {
/* 565:811 */       newVector.addElement("Stop");
/* 566:    */     }
/* 567:814 */     if ((this.m_buildThread == null) && (this.m_Clusterer != null)) {
/* 568:815 */       newVector.addElement("Save model");
/* 569:    */     }
/* 570:818 */     if (this.m_buildThread == null) {
/* 571:819 */       newVector.addElement("Load model");
/* 572:    */     }
/* 573:822 */     return newVector.elements();
/* 574:    */   }
/* 575:    */   
/* 576:    */   public void performRequest(String request)
/* 577:    */   {
/* 578:833 */     if (request.compareTo("Stop") == 0) {
/* 579:834 */       stop();
/* 580:835 */     } else if (request.compareTo("Save model") == 0) {
/* 581:836 */       saveModel();
/* 582:837 */     } else if (request.compareTo("Load model") == 0) {
/* 583:838 */       loadModel();
/* 584:    */     } else {
/* 585:840 */       throw new IllegalArgumentException(request + " not supported (Clusterer)");
/* 586:    */     }
/* 587:    */   }
/* 588:    */   
/* 589:    */   public boolean eventGeneratable(EventSetDescriptor esd)
/* 590:    */   {
/* 591:852 */     String eventName = esd.getName();
/* 592:853 */     return eventGeneratable(eventName);
/* 593:    */   }
/* 594:    */   
/* 595:    */   public boolean eventGeneratable(String eventName)
/* 596:    */   {
/* 597:866 */     if (eventName.compareTo("graph") == 0)
/* 598:    */     {
/* 599:868 */       if (!(this.m_Clusterer instanceof Drawable)) {
/* 600:869 */         return false;
/* 601:    */       }
/* 602:873 */       if (!this.m_listenees.containsKey("trainingSet")) {
/* 603:874 */         return false;
/* 604:    */       }
/* 605:878 */       Object source = this.m_listenees.get("trainingSet");
/* 606:879 */       if (((source instanceof EventConstraints)) && 
/* 607:880 */         (!((EventConstraints)source).eventGeneratable("trainingSet"))) {
/* 608:881 */         return false;
/* 609:    */       }
/* 610:    */     }
/* 611:886 */     if (eventName.compareTo("batchClusterer") == 0)
/* 612:    */     {
/* 613:887 */       if (!this.m_listenees.containsKey("trainingSet")) {
/* 614:888 */         return false;
/* 615:    */       }
/* 616:891 */       Object source = this.m_listenees.get("trainingSet");
/* 617:892 */       if ((source != null) && ((source instanceof EventConstraints)) && 
/* 618:893 */         (!((EventConstraints)source).eventGeneratable("trainingSet"))) {
/* 619:894 */         return false;
/* 620:    */       }
/* 621:    */     }
/* 622:899 */     if (eventName.compareTo("text") == 0)
/* 623:    */     {
/* 624:900 */       if (!this.m_listenees.containsKey("trainingSet")) {
/* 625:901 */         return false;
/* 626:    */       }
/* 627:903 */       Object source = this.m_listenees.get("trainingSet");
/* 628:904 */       if ((source != null) && ((source instanceof EventConstraints)) && 
/* 629:905 */         (!((EventConstraints)source).eventGeneratable("trainingSet"))) {
/* 630:906 */         return false;
/* 631:    */       }
/* 632:    */     }
/* 633:911 */     if (eventName.compareTo("batchClassifier") == 0) {
/* 634:912 */       return false;
/* 635:    */     }
/* 636:914 */     if (eventName.compareTo("incrementalClassifier") == 0) {
/* 637:915 */       return false;
/* 638:    */     }
/* 639:918 */     return true;
/* 640:    */   }
/* 641:    */   
/* 642:    */   private String statusMessagePrefix()
/* 643:    */   {
/* 644:922 */     return getCustomName() + "$" + hashCode() + "|" + (((this.m_Clusterer instanceof OptionHandler)) && (Utils.joinOptions(((OptionHandler)this.m_Clusterer).getOptions()).length() > 0) ? Utils.joinOptions(((OptionHandler)this.m_Clusterer).getOptions()) + "|" : "");
/* 645:    */   }
/* 646:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.Clusterer
 * JD-Core Version:    0.7.0.1
 */