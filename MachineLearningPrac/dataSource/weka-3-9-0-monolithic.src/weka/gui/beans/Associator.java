/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.beans.EventSetDescriptor;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.io.Serializable;
/*   7:    */ import java.util.ArrayList;
/*   8:    */ import java.util.Enumeration;
/*   9:    */ import java.util.EventListener;
/*  10:    */ import java.util.Hashtable;
/*  11:    */ import java.util.Vector;
/*  12:    */ import javax.swing.JPanel;
/*  13:    */ import weka.associations.Apriori;
/*  14:    */ import weka.associations.AssociationRules;
/*  15:    */ import weka.associations.AssociationRulesProducer;
/*  16:    */ import weka.core.Attribute;
/*  17:    */ import weka.core.Drawable;
/*  18:    */ import weka.core.Environment;
/*  19:    */ import weka.core.EnvironmentHandler;
/*  20:    */ import weka.core.Instances;
/*  21:    */ import weka.core.OptionHandler;
/*  22:    */ import weka.core.Utils;
/*  23:    */ import weka.gui.Logger;
/*  24:    */ 
/*  25:    */ public class Associator
/*  26:    */   extends JPanel
/*  27:    */   implements BeanCommon, Visible, WekaWrapper, EventConstraints, Serializable, UserRequestAcceptor, DataSourceListener, TrainingSetListener, ConfigurationProducer, StructureProducer, EnvironmentHandler
/*  28:    */ {
/*  29:    */   private static final long serialVersionUID = -7843500322130210057L;
/*  30: 73 */   protected BeanVisual m_visual = new BeanVisual("Associator", "weka/gui/beans/icons/DefaultAssociator.gif", "weka/gui/beans/icons/DefaultAssociator_animated.gif");
/*  31: 77 */   private static int IDLE = 0;
/*  32: 78 */   private static int BUILDING_MODEL = 1;
/*  33: 80 */   private int m_state = IDLE;
/*  34: 82 */   private Thread m_buildThread = null;
/*  35:    */   protected String m_globalInfo;
/*  36: 92 */   private final Hashtable<String, Object> m_listenees = new Hashtable();
/*  37: 97 */   private final Vector<EventListener> m_textListeners = new Vector();
/*  38:102 */   private final Vector<EventListener> m_graphListeners = new Vector();
/*  39:105 */   private final Vector<BatchAssociationRulesListener> m_rulesListeners = new Vector();
/*  40:107 */   private weka.associations.Associator m_Associator = new Apriori();
/*  41:109 */   private transient Logger m_log = null;
/*  42:112 */   private transient Environment m_env = null;
/*  43:    */   
/*  44:    */   public String globalInfo()
/*  45:    */   {
/*  46:120 */     return this.m_globalInfo;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public Associator()
/*  50:    */   {
/*  51:127 */     setLayout(new BorderLayout());
/*  52:128 */     add(this.m_visual, "Center");
/*  53:129 */     setAssociator(this.m_Associator);
/*  54:    */   }
/*  55:    */   
/*  56:    */   public void setEnvironment(Environment env)
/*  57:    */   {
/*  58:139 */     this.m_env = env;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void setCustomName(String name)
/*  62:    */   {
/*  63:149 */     this.m_visual.setText(name);
/*  64:    */   }
/*  65:    */   
/*  66:    */   public String getCustomName()
/*  67:    */   {
/*  68:159 */     return this.m_visual.getText();
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void setAssociator(weka.associations.Associator c)
/*  72:    */   {
/*  73:168 */     boolean loadImages = true;
/*  74:169 */     if (c.getClass().getName().compareTo(this.m_Associator.getClass().getName()) == 0) {
/*  75:170 */       loadImages = false;
/*  76:    */     }
/*  77:172 */     this.m_Associator = c;
/*  78:173 */     String associatorName = c.getClass().toString();
/*  79:174 */     associatorName = associatorName.substring(associatorName.lastIndexOf('.') + 1, associatorName.length());
/*  80:176 */     if ((loadImages) && 
/*  81:177 */       (!this.m_visual.loadIcons("weka/gui/beans/icons/" + associatorName + ".gif", "weka/gui/beans/icons/" + associatorName + "_animated.gif"))) {
/*  82:179 */       useDefaultVisual();
/*  83:    */     }
/*  84:182 */     this.m_visual.setText(associatorName);
/*  85:    */     
/*  86:    */ 
/*  87:185 */     this.m_globalInfo = KnowledgeFlowApp.getGlobalInfo(this.m_Associator);
/*  88:    */   }
/*  89:    */   
/*  90:    */   public weka.associations.Associator getAssociator()
/*  91:    */   {
/*  92:194 */     return this.m_Associator;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public void setWrappedAlgorithm(Object algorithm)
/*  96:    */   {
/*  97:206 */     if (!(algorithm instanceof weka.associations.Associator)) {
/*  98:207 */       throw new IllegalArgumentException(algorithm.getClass() + " : incorrect " + "type of algorithm (Associator)");
/*  99:    */     }
/* 100:210 */     setAssociator((weka.associations.Associator)algorithm);
/* 101:    */   }
/* 102:    */   
/* 103:    */   public Object getWrappedAlgorithm()
/* 104:    */   {
/* 105:220 */     return getAssociator();
/* 106:    */   }
/* 107:    */   
/* 108:    */   public void acceptTrainingSet(TrainingSetEvent e)
/* 109:    */   {
/* 110:231 */     Instances trainingSet = e.getTrainingSet();
/* 111:232 */     DataSetEvent dse = new DataSetEvent(this, trainingSet);
/* 112:233 */     acceptDataSet(dse);
/* 113:    */   }
/* 114:    */   
/* 115:    */   public void acceptDataSet(final DataSetEvent e)
/* 116:    */   {
/* 117:238 */     if (e.isStructureOnly()) {
/* 118:240 */       return;
/* 119:    */     }
/* 120:243 */     if (this.m_buildThread == null) {
/* 121:    */       try
/* 122:    */       {
/* 123:245 */         if (this.m_state == IDLE)
/* 124:    */         {
/* 125:246 */           synchronized (this)
/* 126:    */           {
/* 127:247 */             this.m_state = BUILDING_MODEL;
/* 128:    */           }
/* 129:249 */           final Instances trainingData = e.getDataSet();
/* 130:    */           
/* 131:251 */           this.m_buildThread = new Thread()
/* 132:    */           {
/* 133:    */             public void run()
/* 134:    */             {
/* 135:    */               try
/* 136:    */               {
/* 137:256 */                 if (trainingData != null)
/* 138:    */                 {
/* 139:257 */                   Associator.this.m_visual.setAnimated();
/* 140:259 */                   if (Associator.this.m_log != null) {
/* 141:260 */                     Associator.this.m_log.statusMessage(Associator.this.statusMessagePrefix() + "Building model...");
/* 142:    */                   }
/* 143:263 */                   Associator.this.buildAssociations(trainingData);
/* 144:265 */                   if (Associator.this.m_textListeners.size() > 0)
/* 145:    */                   {
/* 146:266 */                     String modelString = Associator.this.m_Associator.toString();
/* 147:267 */                     String titleString = Associator.this.m_Associator.getClass().getName();
/* 148:    */                     
/* 149:269 */                     titleString = titleString.substring(titleString.lastIndexOf('.') + 1, titleString.length());
/* 150:    */                     
/* 151:271 */                     modelString = "=== Associator model ===\n\nScheme:   " + titleString + "\n" + "Relation: " + trainingData.relationName() + "\n\n" + modelString;
/* 152:    */                     
/* 153:    */ 
/* 154:274 */                     titleString = "Model: " + titleString;
/* 155:    */                     
/* 156:276 */                     TextEvent nt = new TextEvent(Associator.this, modelString, titleString);
/* 157:    */                     
/* 158:278 */                     Associator.this.notifyTextListeners(nt);
/* 159:    */                   }
/* 160:281 */                   if (((Associator.this.m_Associator instanceof Drawable)) && (Associator.this.m_graphListeners.size() > 0))
/* 161:    */                   {
/* 162:283 */                     String grphString = ((Drawable)Associator.this.m_Associator).graph();
/* 163:    */                     
/* 164:285 */                     int grphType = ((Drawable)Associator.this.m_Associator).graphType();
/* 165:    */                     
/* 166:287 */                     String grphTitle = Associator.this.m_Associator.getClass().getName();
/* 167:288 */                     grphTitle = grphTitle.substring(grphTitle.lastIndexOf('.') + 1, grphTitle.length());
/* 168:    */                     
/* 169:290 */                     grphTitle = " (" + e.getDataSet().relationName() + ") " + grphTitle;
/* 170:    */                     
/* 171:    */ 
/* 172:293 */                     GraphEvent ge = new GraphEvent(Associator.this, grphString, grphTitle, grphType);
/* 173:    */                     
/* 174:295 */                     Associator.this.notifyGraphListeners(ge);
/* 175:    */                   }
/* 176:298 */                   if (((Associator.this.m_Associator instanceof AssociationRulesProducer)) && (Associator.this.m_rulesListeners.size() > 0))
/* 177:    */                   {
/* 178:300 */                     AssociationRules rules = ((AssociationRulesProducer)Associator.this.m_Associator).getAssociationRules();
/* 179:    */                     
/* 180:    */ 
/* 181:303 */                     BatchAssociationRulesEvent bre = new BatchAssociationRulesEvent(Associator.this, rules);
/* 182:    */                     
/* 183:305 */                     Associator.this.notifyRulesListeners(bre);
/* 184:    */                   }
/* 185:    */                 }
/* 186:    */               }
/* 187:    */               catch (Exception ex)
/* 188:    */               {
/* 189:    */                 String titleString;
/* 190:309 */                 Associator.this.stop();
/* 191:310 */                 if (Associator.this.m_log != null)
/* 192:    */                 {
/* 193:311 */                   Associator.this.m_log.statusMessage(Associator.this.statusMessagePrefix() + "ERROR (See log for details)");
/* 194:    */                   
/* 195:313 */                   Associator.this.m_log.logMessage("[Associator] " + Associator.this.statusMessagePrefix() + " problem training associator. " + ex.getMessage());
/* 196:    */                 }
/* 197:316 */                 ex.printStackTrace();
/* 198:    */               }
/* 199:    */               finally
/* 200:    */               {
/* 201:    */                 String titleString;
/* 202:319 */                 Associator.this.m_visual.setStatic();
/* 203:320 */                 Associator.this.m_state = Associator.IDLE;
/* 204:321 */                 if (isInterrupted())
/* 205:    */                 {
/* 206:322 */                   if (Associator.this.m_log != null)
/* 207:    */                   {
/* 208:323 */                     String titleString = Associator.this.m_Associator.getClass().getName();
/* 209:324 */                     titleString = titleString.substring(titleString.lastIndexOf('.') + 1, titleString.length());
/* 210:    */                     
/* 211:326 */                     Associator.this.m_log.logMessage("[Associator] " + Associator.this.statusMessagePrefix() + " Build associator interrupted!");
/* 212:    */                     
/* 213:328 */                     Associator.this.m_log.statusMessage(Associator.this.statusMessagePrefix() + "INTERRUPTED");
/* 214:    */                   }
/* 215:    */                 }
/* 216:331 */                 else if (Associator.this.m_log != null) {
/* 217:332 */                   Associator.this.m_log.statusMessage(Associator.this.statusMessagePrefix() + "Finished.");
/* 218:    */                 }
/* 219:335 */                 Associator.this.block(false);
/* 220:    */               }
/* 221:    */             }
/* 222:338 */           };
/* 223:339 */           this.m_buildThread.setPriority(1);
/* 224:340 */           this.m_buildThread.start();
/* 225:    */           
/* 226:    */ 
/* 227:343 */           block(true);
/* 228:    */           
/* 229:345 */           this.m_buildThread = null;
/* 230:346 */           this.m_state = IDLE;
/* 231:    */         }
/* 232:    */       }
/* 233:    */       catch (Exception ex)
/* 234:    */       {
/* 235:349 */         ex.printStackTrace();
/* 236:    */       }
/* 237:    */     }
/* 238:    */   }
/* 239:    */   
/* 240:    */   private void buildAssociations(Instances data)
/* 241:    */     throws Exception
/* 242:    */   {
/* 243:358 */     if ((this.m_env != null) && ((this.m_Associator instanceof OptionHandler)))
/* 244:    */     {
/* 245:359 */       String opts = this.m_env.getVariableValue("weka.gui.beans.associator.schemeOptions");
/* 246:361 */       if ((opts != null) && (opts.length() > 0))
/* 247:    */       {
/* 248:362 */         String[] options = Utils.splitOptions(opts);
/* 249:363 */         if (options.length > 0) {
/* 250:    */           try
/* 251:    */           {
/* 252:365 */             ((OptionHandler)this.m_Associator).setOptions(options);
/* 253:    */           }
/* 254:    */           catch (Exception ex)
/* 255:    */           {
/* 256:367 */             String warningMessage = "[Associator] WARNING: unable to set options \"" + opts + "\"for " + this.m_Associator.getClass().getName();
/* 257:369 */             if (this.m_log != null) {
/* 258:370 */               this.m_log.logMessage(warningMessage);
/* 259:    */             } else {
/* 260:372 */               System.err.print(warningMessage);
/* 261:    */             }
/* 262:    */           }
/* 263:    */         }
/* 264:    */       }
/* 265:    */     }
/* 266:379 */     this.m_Associator.buildAssociations(data);
/* 267:    */   }
/* 268:    */   
/* 269:    */   public void setVisual(BeanVisual newVisual)
/* 270:    */   {
/* 271:389 */     this.m_visual = newVisual;
/* 272:    */   }
/* 273:    */   
/* 274:    */   public BeanVisual getVisual()
/* 275:    */   {
/* 276:397 */     return this.m_visual;
/* 277:    */   }
/* 278:    */   
/* 279:    */   public void useDefaultVisual()
/* 280:    */   {
/* 281:405 */     this.m_visual.loadIcons("weka/gui/beans/icons/DefaultAssociator.gif", "weka/gui/beans/icons/DefaultAssociator_animated.gif");
/* 282:    */   }
/* 283:    */   
/* 284:    */   public synchronized void addBatchAssociationRulesListener(BatchAssociationRulesListener al)
/* 285:    */   {
/* 286:416 */     this.m_rulesListeners.add(al);
/* 287:    */   }
/* 288:    */   
/* 289:    */   public synchronized void removeBatchAssociationRulesListener(BatchAssociationRulesListener al)
/* 290:    */   {
/* 291:426 */     this.m_rulesListeners.remove(al);
/* 292:    */   }
/* 293:    */   
/* 294:    */   public synchronized void addTextListener(TextListener cl)
/* 295:    */   {
/* 296:435 */     this.m_textListeners.addElement(cl);
/* 297:    */   }
/* 298:    */   
/* 299:    */   public synchronized void removeTextListener(TextListener cl)
/* 300:    */   {
/* 301:444 */     this.m_textListeners.remove(cl);
/* 302:    */   }
/* 303:    */   
/* 304:    */   public synchronized void addGraphListener(GraphListener cl)
/* 305:    */   {
/* 306:453 */     this.m_graphListeners.addElement(cl);
/* 307:    */   }
/* 308:    */   
/* 309:    */   public synchronized void removeGraphListener(GraphListener cl)
/* 310:    */   {
/* 311:462 */     this.m_graphListeners.remove(cl);
/* 312:    */   }
/* 313:    */   
/* 314:    */   public synchronized void addConfigurationListener(ConfigurationListener cl) {}
/* 315:    */   
/* 316:    */   public synchronized void removeConfigurationListener(ConfigurationListener cl) {}
/* 317:    */   
/* 318:    */   private void notifyTextListeners(TextEvent ge)
/* 319:    */   {
/* 320:    */     Vector<EventListener> l;
/* 321:495 */     synchronized (this)
/* 322:    */     {
/* 323:496 */       l = (Vector)this.m_textListeners.clone();
/* 324:    */     }
/* 325:498 */     if (l.size() > 0) {
/* 326:499 */       for (int i = 0; i < l.size(); i++) {
/* 327:500 */         ((TextListener)l.elementAt(i)).acceptText(ge);
/* 328:    */       }
/* 329:    */     }
/* 330:    */   }
/* 331:    */   
/* 332:    */   private void notifyGraphListeners(GraphEvent ge)
/* 333:    */   {
/* 334:    */     Vector<EventListener> l;
/* 335:513 */     synchronized (this)
/* 336:    */     {
/* 337:514 */       l = (Vector)this.m_graphListeners.clone();
/* 338:    */     }
/* 339:516 */     if (l.size() > 0) {
/* 340:517 */       for (int i = 0; i < l.size(); i++) {
/* 341:518 */         ((GraphListener)l.elementAt(i)).acceptGraph(ge);
/* 342:    */       }
/* 343:    */     }
/* 344:    */   }
/* 345:    */   
/* 346:    */   private void notifyRulesListeners(BatchAssociationRulesEvent are)
/* 347:    */   {
/* 348:532 */     synchronized (this)
/* 349:    */     {
/* 350:533 */       Vector<BatchAssociationRulesListener> l = (Vector)this.m_rulesListeners.clone();
/* 351:534 */       for (int i = 0; i < l.size(); i++) {
/* 352:535 */         ((BatchAssociationRulesListener)l.get(i)).acceptAssociationRules(are);
/* 353:    */       }
/* 354:    */     }
/* 355:    */   }
/* 356:    */   
/* 357:    */   public boolean connectionAllowed(String eventName)
/* 358:    */   {
/* 359:549 */     if (this.m_listenees.containsKey(eventName)) {
/* 360:550 */       return false;
/* 361:    */     }
/* 362:552 */     return true;
/* 363:    */   }
/* 364:    */   
/* 365:    */   public boolean connectionAllowed(EventSetDescriptor esd)
/* 366:    */   {
/* 367:564 */     return connectionAllowed(esd.getName());
/* 368:    */   }
/* 369:    */   
/* 370:    */   public synchronized void connectionNotification(String eventName, Object source)
/* 371:    */   {
/* 372:579 */     if (connectionAllowed(eventName)) {
/* 373:580 */       this.m_listenees.put(eventName, source);
/* 374:    */     }
/* 375:    */   }
/* 376:    */   
/* 377:    */   public synchronized void disconnectionNotification(String eventName, Object source)
/* 378:    */   {
/* 379:595 */     this.m_listenees.remove(eventName);
/* 380:    */   }
/* 381:    */   
/* 382:    */   private synchronized void block(boolean tf)
/* 383:    */   {
/* 384:606 */     if (tf) {
/* 385:    */       try
/* 386:    */       {
/* 387:609 */         if ((this.m_buildThread.isAlive()) && (this.m_state != IDLE)) {
/* 388:610 */           wait();
/* 389:    */         }
/* 390:    */       }
/* 391:    */       catch (InterruptedException ex) {}
/* 392:    */     } else {
/* 393:615 */       notifyAll();
/* 394:    */     }
/* 395:    */   }
/* 396:    */   
/* 397:    */   public boolean isBusy()
/* 398:    */   {
/* 399:627 */     return this.m_buildThread != null;
/* 400:    */   }
/* 401:    */   
/* 402:    */   public void stop()
/* 403:    */   {
/* 404:637 */     Enumeration<String> en = this.m_listenees.keys();
/* 405:638 */     while (en.hasMoreElements())
/* 406:    */     {
/* 407:639 */       Object tempO = this.m_listenees.get(en.nextElement());
/* 408:640 */       if ((tempO instanceof BeanCommon)) {
/* 409:641 */         ((BeanCommon)tempO).stop();
/* 410:    */       }
/* 411:    */     }
/* 412:646 */     if (this.m_buildThread != null)
/* 413:    */     {
/* 414:647 */       this.m_buildThread.interrupt();
/* 415:648 */       this.m_buildThread.stop();
/* 416:649 */       this.m_buildThread = null;
/* 417:650 */       this.m_visual.setStatic();
/* 418:    */     }
/* 419:    */   }
/* 420:    */   
/* 421:    */   public void setLog(Logger logger)
/* 422:    */   {
/* 423:661 */     this.m_log = logger;
/* 424:    */   }
/* 425:    */   
/* 426:    */   public Enumeration<String> enumerateRequests()
/* 427:    */   {
/* 428:671 */     Vector<String> newVector = new Vector(0);
/* 429:672 */     if (this.m_buildThread != null) {
/* 430:673 */       newVector.addElement("Stop");
/* 431:    */     }
/* 432:675 */     return newVector.elements();
/* 433:    */   }
/* 434:    */   
/* 435:    */   public void performRequest(String request)
/* 436:    */   {
/* 437:686 */     if (request.compareTo("Stop") == 0) {
/* 438:687 */       stop();
/* 439:    */     } else {
/* 440:689 */       throw new IllegalArgumentException(request + " not supported (Associator)");
/* 441:    */     }
/* 442:    */   }
/* 443:    */   
/* 444:    */   public boolean eventGeneratable(EventSetDescriptor esd)
/* 445:    */   {
/* 446:702 */     String eventName = esd.getName();
/* 447:703 */     return eventGeneratable(eventName);
/* 448:    */   }
/* 449:    */   
/* 450:    */   public Instances getStructure(String eventName)
/* 451:    */   {
/* 452:722 */     Instances structure = null;
/* 453:724 */     if (eventName.equals("text"))
/* 454:    */     {
/* 455:725 */       ArrayList<Attribute> attInfo = new ArrayList();
/* 456:726 */       attInfo.add(new Attribute("Title", (ArrayList)null));
/* 457:727 */       attInfo.add(new Attribute("Text", (ArrayList)null));
/* 458:728 */       structure = new Instances("TextEvent", attInfo, 0);
/* 459:    */     }
/* 460:729 */     else if ((eventName.equals("batchAssociationRules")) && 
/* 461:730 */       (this.m_Associator != null) && ((this.m_Associator instanceof AssociationRulesProducer)))
/* 462:    */     {
/* 463:738 */       String[] metricNames = ((AssociationRulesProducer)this.m_Associator).getRuleMetricNames();
/* 464:    */       
/* 465:740 */       ArrayList<Attribute> attInfo = new ArrayList();
/* 466:741 */       attInfo.add(new Attribute("LHS", (ArrayList)null));
/* 467:742 */       attInfo.add(new Attribute("RHS", (ArrayList)null));
/* 468:743 */       attInfo.add(new Attribute("Support"));
/* 469:744 */       for (String metricName : metricNames) {
/* 470:745 */         attInfo.add(new Attribute(metricName));
/* 471:    */       }
/* 472:747 */       structure = new Instances("batchAssociationRulesEvent", attInfo, 0);
/* 473:    */     }
/* 474:751 */     return structure;
/* 475:    */   }
/* 476:    */   
/* 477:    */   public boolean eventGeneratable(String eventName)
/* 478:    */   {
/* 479:764 */     if ((eventName.compareTo("text") == 0) || (eventName.compareTo("graph") == 0) || (eventName.equals("batchAssociationRules")))
/* 480:    */     {
/* 481:766 */       if ((!this.m_listenees.containsKey("dataSet")) && (!this.m_listenees.containsKey("trainingSet"))) {
/* 482:768 */         return false;
/* 483:    */       }
/* 484:770 */       Object source = this.m_listenees.get("trainingSet");
/* 485:771 */       if ((source != null) && ((source instanceof EventConstraints)) && 
/* 486:772 */         (!((EventConstraints)source).eventGeneratable("trainingSet"))) {
/* 487:773 */         return false;
/* 488:    */       }
/* 489:776 */       source = this.m_listenees.get("dataSet");
/* 490:777 */       if ((source != null) && ((source instanceof EventConstraints)) && 
/* 491:778 */         (!((EventConstraints)source).eventGeneratable("dataSet"))) {
/* 492:779 */         return false;
/* 493:    */       }
/* 494:783 */       if ((eventName.compareTo("graph") == 0) && (!(this.m_Associator instanceof Drawable))) {
/* 495:785 */         return false;
/* 496:    */       }
/* 497:788 */       if (eventName.equals("batchAssociationRules"))
/* 498:    */       {
/* 499:789 */         if (!(this.m_Associator instanceof AssociationRulesProducer)) {
/* 500:790 */           return false;
/* 501:    */         }
/* 502:793 */         if (!((AssociationRulesProducer)this.m_Associator).canProduceRules()) {
/* 503:794 */           return false;
/* 504:    */         }
/* 505:    */       }
/* 506:    */     }
/* 507:798 */     return true;
/* 508:    */   }
/* 509:    */   
/* 510:    */   private String statusMessagePrefix()
/* 511:    */   {
/* 512:802 */     return getCustomName() + "$" + hashCode() + "|" + (((this.m_Associator instanceof OptionHandler)) && (Utils.joinOptions(((OptionHandler)this.m_Associator).getOptions()).length() > 0) ? Utils.joinOptions(((OptionHandler)this.m_Associator).getOptions()) + "|" : "");
/* 513:    */   }
/* 514:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.Associator
 * JD-Core Version:    0.7.0.1
 */