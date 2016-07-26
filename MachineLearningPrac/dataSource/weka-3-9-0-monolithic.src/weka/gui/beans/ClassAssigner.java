/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.beans.EventSetDescriptor;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.io.Serializable;
/*   7:    */ import java.util.Vector;
/*   8:    */ import javax.swing.JPanel;
/*   9:    */ import weka.core.Attribute;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.gui.Logger;
/*  12:    */ 
/*  13:    */ public class ClassAssigner
/*  14:    */   extends JPanel
/*  15:    */   implements Visible, DataSourceListener, TrainingSetListener, TestSetListener, DataSource, TrainingSetProducer, TestSetProducer, BeanCommon, EventConstraints, Serializable, InstanceListener, StructureProducer
/*  16:    */ {
/*  17:    */   private static final long serialVersionUID = 4011131665025817924L;
/*  18: 48 */   private String m_classColumn = "last";
/*  19:    */   private Instances m_connectedFormat;
/*  20:    */   private Object m_trainingProvider;
/*  21:    */   private Object m_testProvider;
/*  22:    */   private Object m_dataProvider;
/*  23:    */   private Object m_instanceProvider;
/*  24: 58 */   private final Vector<TrainingSetListener> m_trainingListeners = new Vector();
/*  25: 59 */   private final Vector<TestSetListener> m_testListeners = new Vector();
/*  26: 60 */   private final Vector<DataSourceListener> m_dataListeners = new Vector();
/*  27: 61 */   private final Vector<InstanceListener> m_instanceListeners = new Vector();
/*  28: 63 */   private final Vector<DataFormatListener> m_dataFormatListeners = new Vector();
/*  29: 65 */   protected transient Logger m_logger = null;
/*  30: 67 */   protected BeanVisual m_visual = new BeanVisual("ClassAssigner", "weka/gui/beans/icons/ClassAssigner.gif", "weka/gui/beans/icons/ClassAssigner_animated.gif");
/*  31:    */   
/*  32:    */   public String globalInfo()
/*  33:    */   {
/*  34: 77 */     return "Designate which column is to be considered the class column in incoming data.";
/*  35:    */   }
/*  36:    */   
/*  37:    */   public ClassAssigner()
/*  38:    */   {
/*  39: 82 */     setLayout(new BorderLayout());
/*  40: 83 */     add(this.m_visual, "Center");
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void setCustomName(String name)
/*  44:    */   {
/*  45: 93 */     this.m_visual.setText(name);
/*  46:    */   }
/*  47:    */   
/*  48:    */   public String getCustomName()
/*  49:    */   {
/*  50:103 */     return this.m_visual.getText();
/*  51:    */   }
/*  52:    */   
/*  53:    */   public String classColumnTipText()
/*  54:    */   {
/*  55:112 */     return "Specify the number of the column that contains the class attribute";
/*  56:    */   }
/*  57:    */   
/*  58:    */   private Instances getUpstreamStructure()
/*  59:    */   {
/*  60:116 */     if ((this.m_dataProvider != null) && ((this.m_dataProvider instanceof StructureProducer))) {
/*  61:117 */       return ((StructureProducer)this.m_dataProvider).getStructure("dataSet");
/*  62:    */     }
/*  63:119 */     if ((this.m_trainingProvider != null) && ((this.m_trainingProvider instanceof StructureProducer))) {
/*  64:121 */       return ((StructureProducer)this.m_trainingProvider).getStructure("trainingSet");
/*  65:    */     }
/*  66:124 */     if ((this.m_testProvider != null) && ((this.m_testProvider instanceof StructureProducer))) {
/*  67:125 */       return ((StructureProducer)this.m_testProvider).getStructure("testSet");
/*  68:    */     }
/*  69:127 */     if ((this.m_instanceProvider != null) && ((this.m_instanceProvider instanceof StructureProducer))) {
/*  70:129 */       return ((StructureProducer)this.m_instanceProvider).getStructure("instance");
/*  71:    */     }
/*  72:131 */     return null;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public Instances getStructure(String eventName)
/*  76:    */   {
/*  77:149 */     if ((!eventName.equals("trainingSet")) && (!eventName.equals("testSet")) && (!eventName.equals("dataSet")) && (!eventName.equals("instance"))) {
/*  78:151 */       return null;
/*  79:    */     }
/*  80:153 */     if ((this.m_trainingProvider == null) && (this.m_testProvider == null) && (this.m_dataProvider == null) && (this.m_instanceProvider == null)) {
/*  81:155 */       return null;
/*  82:    */     }
/*  83:158 */     if ((eventName.equals("dataSet")) && (this.m_dataListeners.size() == 0)) {
/*  84:161 */       return null;
/*  85:    */     }
/*  86:164 */     if ((eventName.equals("trainingSet")) && (this.m_trainingListeners.size() == 0)) {
/*  87:167 */       return null;
/*  88:    */     }
/*  89:170 */     if ((eventName.equals("testSet")) && (this.m_testListeners.size() == 0)) {
/*  90:173 */       return null;
/*  91:    */     }
/*  92:176 */     if ((eventName.equals("instance")) && (this.m_instanceListeners.size() == 0)) {
/*  93:179 */       return null;
/*  94:    */     }
/*  95:182 */     if (this.m_connectedFormat == null) {
/*  96:183 */       this.m_connectedFormat = getUpstreamStructure();
/*  97:    */     }
/*  98:186 */     if (this.m_connectedFormat != null) {
/*  99:187 */       assignClass(this.m_connectedFormat);
/* 100:    */     }
/* 101:190 */     return this.m_connectedFormat;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public Instances getConnectedFormat()
/* 105:    */   {
/* 106:208 */     if (this.m_connectedFormat == null) {
/* 107:211 */       this.m_connectedFormat = getUpstreamStructure();
/* 108:    */     }
/* 109:214 */     return this.m_connectedFormat;
/* 110:    */   }
/* 111:    */   
/* 112:    */   public void setClassColumn(String col)
/* 113:    */   {
/* 114:218 */     this.m_classColumn = col;
/* 115:219 */     if (this.m_connectedFormat != null) {
/* 116:220 */       assignClass(this.m_connectedFormat);
/* 117:    */     }
/* 118:    */   }
/* 119:    */   
/* 120:    */   public String getClassColumn()
/* 121:    */   {
/* 122:225 */     return this.m_classColumn;
/* 123:    */   }
/* 124:    */   
/* 125:    */   public void acceptDataSet(DataSetEvent e)
/* 126:    */   {
/* 127:230 */     Instances dataSet = e.getDataSet();
/* 128:231 */     assignClass(dataSet);
/* 129:232 */     notifyDataListeners(e);
/* 130:233 */     if (e.isStructureOnly())
/* 131:    */     {
/* 132:234 */       this.m_connectedFormat = e.getDataSet();
/* 133:    */       
/* 134:236 */       notifyDataFormatListeners();
/* 135:    */     }
/* 136:    */   }
/* 137:    */   
/* 138:    */   public void acceptTrainingSet(TrainingSetEvent e)
/* 139:    */   {
/* 140:242 */     Instances trainingSet = e.getTrainingSet();
/* 141:243 */     assignClass(trainingSet);
/* 142:244 */     notifyTrainingListeners(e);
/* 143:246 */     if (e.isStructureOnly())
/* 144:    */     {
/* 145:247 */       this.m_connectedFormat = e.getTrainingSet();
/* 146:    */       
/* 147:249 */       notifyDataFormatListeners();
/* 148:    */     }
/* 149:    */   }
/* 150:    */   
/* 151:    */   public void acceptTestSet(TestSetEvent e)
/* 152:    */   {
/* 153:255 */     Instances testSet = e.getTestSet();
/* 154:256 */     assignClass(testSet);
/* 155:257 */     notifyTestListeners(e);
/* 156:258 */     if (e.isStructureOnly())
/* 157:    */     {
/* 158:259 */       this.m_connectedFormat = e.getTestSet();
/* 159:    */       
/* 160:261 */       notifyDataFormatListeners();
/* 161:    */     }
/* 162:    */   }
/* 163:    */   
/* 164:    */   public void acceptInstance(InstanceEvent e)
/* 165:    */   {
/* 166:267 */     if (e.getStatus() == 0)
/* 167:    */     {
/* 168:269 */       this.m_connectedFormat = e.getStructure();
/* 169:    */       
/* 170:    */ 
/* 171:272 */       assignClass(this.m_connectedFormat);
/* 172:273 */       notifyInstanceListeners(e);
/* 173:    */       
/* 174:    */ 
/* 175:276 */       System.err.println("Notifying customizer...");
/* 176:277 */       notifyDataFormatListeners();
/* 177:    */     }
/* 178:    */     else
/* 179:    */     {
/* 180:281 */       notifyInstanceListeners(e);
/* 181:    */     }
/* 182:    */   }
/* 183:    */   
/* 184:    */   private void assignClass(Instances dataSet)
/* 185:    */   {
/* 186:286 */     int classCol = -1;
/* 187:288 */     if ((this.m_classColumn.trim().toLowerCase().compareTo("last") == 0) || (this.m_classColumn.equalsIgnoreCase("/last")))
/* 188:    */     {
/* 189:290 */       dataSet.setClassIndex(dataSet.numAttributes() - 1);
/* 190:    */     }
/* 191:291 */     else if ((this.m_classColumn.trim().toLowerCase().compareTo("first") == 0) || (this.m_classColumn.equalsIgnoreCase("/first")))
/* 192:    */     {
/* 193:293 */       dataSet.setClassIndex(0);
/* 194:    */     }
/* 195:    */     else
/* 196:    */     {
/* 197:296 */       Attribute classAtt = dataSet.attribute(this.m_classColumn.trim());
/* 198:297 */       if (classAtt != null)
/* 199:    */       {
/* 200:298 */         dataSet.setClass(classAtt);
/* 201:    */       }
/* 202:    */       else
/* 203:    */       {
/* 204:    */         try
/* 205:    */         {
/* 206:302 */           classCol = Integer.parseInt(this.m_classColumn.trim()) - 1;
/* 207:    */         }
/* 208:    */         catch (NumberFormatException ex)
/* 209:    */         {
/* 210:304 */           if (this.m_logger != null) {
/* 211:305 */             this.m_logger.logMessage("Warning : can't parse '" + this.m_classColumn.trim() + "' as a number " + " or find it as an attribute in the incoming data (ClassAssigner)");
/* 212:    */           }
/* 213:    */         }
/* 214:312 */         if (classCol > dataSet.numAttributes() - 1)
/* 215:    */         {
/* 216:313 */           if (this.m_logger != null) {
/* 217:314 */             this.m_logger.logMessage("Class column outside range of data (ClassAssigner)");
/* 218:    */           }
/* 219:    */         }
/* 220:    */         else {
/* 221:318 */           dataSet.setClassIndex(classCol);
/* 222:    */         }
/* 223:    */       }
/* 224:    */     }
/* 225:    */   }
/* 226:    */   
/* 227:    */   protected void notifyTestListeners(TestSetEvent tse)
/* 228:    */   {
/* 229:    */     Vector<TestSetListener> l;
/* 230:327 */     synchronized (this)
/* 231:    */     {
/* 232:328 */       l = (Vector)this.m_testListeners.clone();
/* 233:    */     }
/* 234:330 */     if (l.size() > 0) {
/* 235:331 */       for (int i = 0; i < l.size(); i++)
/* 236:    */       {
/* 237:332 */         System.err.println("Notifying test listeners (ClassAssigner)");
/* 238:333 */         ((TestSetListener)l.elementAt(i)).acceptTestSet(tse);
/* 239:    */       }
/* 240:    */     }
/* 241:    */   }
/* 242:    */   
/* 243:    */   protected void notifyTrainingListeners(TrainingSetEvent tse)
/* 244:    */   {
/* 245:    */     Vector<TrainingSetListener> l;
/* 246:341 */     synchronized (this)
/* 247:    */     {
/* 248:342 */       l = (Vector)this.m_trainingListeners.clone();
/* 249:    */     }
/* 250:344 */     if (l.size() > 0) {
/* 251:345 */       for (int i = 0; i < l.size(); i++)
/* 252:    */       {
/* 253:346 */         System.err.println("Notifying training listeners (ClassAssigner)");
/* 254:347 */         ((TrainingSetListener)l.elementAt(i)).acceptTrainingSet(tse);
/* 255:    */       }
/* 256:    */     }
/* 257:    */   }
/* 258:    */   
/* 259:    */   protected void notifyDataListeners(DataSetEvent tse)
/* 260:    */   {
/* 261:    */     Vector<DataSourceListener> l;
/* 262:355 */     synchronized (this)
/* 263:    */     {
/* 264:356 */       l = (Vector)this.m_dataListeners.clone();
/* 265:    */     }
/* 266:358 */     if (l.size() > 0) {
/* 267:359 */       for (int i = 0; i < l.size(); i++)
/* 268:    */       {
/* 269:360 */         System.err.println("Notifying data listeners (ClassAssigner)");
/* 270:361 */         ((DataSourceListener)l.elementAt(i)).acceptDataSet(tse);
/* 271:    */       }
/* 272:    */     }
/* 273:    */   }
/* 274:    */   
/* 275:    */   protected void notifyInstanceListeners(InstanceEvent tse)
/* 276:    */   {
/* 277:    */     Vector<InstanceListener> l;
/* 278:369 */     synchronized (this)
/* 279:    */     {
/* 280:370 */       l = (Vector)this.m_instanceListeners.clone();
/* 281:    */     }
/* 282:372 */     if (l.size() > 0) {
/* 283:373 */       for (int i = 0; i < l.size(); i++) {
/* 284:377 */         ((InstanceListener)l.elementAt(i)).acceptInstance(tse);
/* 285:    */       }
/* 286:    */     }
/* 287:    */   }
/* 288:    */   
/* 289:    */   protected void notifyDataFormatListeners()
/* 290:    */   {
/* 291:    */     Vector<DataFormatListener> l;
/* 292:385 */     synchronized (this)
/* 293:    */     {
/* 294:386 */       l = (Vector)this.m_dataFormatListeners.clone();
/* 295:    */     }
/* 296:388 */     if (l.size() > 0)
/* 297:    */     {
/* 298:389 */       DataSetEvent dse = new DataSetEvent(this, this.m_connectedFormat);
/* 299:390 */       for (int i = 0; i < l.size(); i++) {
/* 300:393 */         ((DataFormatListener)l.elementAt(i)).newDataFormat(dse);
/* 301:    */       }
/* 302:    */     }
/* 303:    */   }
/* 304:    */   
/* 305:    */   public synchronized void addInstanceListener(InstanceListener tsl)
/* 306:    */   {
/* 307:400 */     this.m_instanceListeners.addElement(tsl);
/* 308:401 */     if (this.m_connectedFormat != null)
/* 309:    */     {
/* 310:402 */       InstanceEvent e = new InstanceEvent(this, this.m_connectedFormat);
/* 311:403 */       tsl.acceptInstance(e);
/* 312:    */     }
/* 313:    */   }
/* 314:    */   
/* 315:    */   public synchronized void removeInstanceListener(InstanceListener tsl)
/* 316:    */   {
/* 317:409 */     this.m_instanceListeners.removeElement(tsl);
/* 318:    */   }
/* 319:    */   
/* 320:    */   public synchronized void addDataSourceListener(DataSourceListener tsl)
/* 321:    */   {
/* 322:414 */     this.m_dataListeners.addElement(tsl);
/* 323:416 */     if (this.m_connectedFormat != null)
/* 324:    */     {
/* 325:417 */       DataSetEvent e = new DataSetEvent(this, this.m_connectedFormat);
/* 326:418 */       tsl.acceptDataSet(e);
/* 327:    */     }
/* 328:    */   }
/* 329:    */   
/* 330:    */   public synchronized void removeDataSourceListener(DataSourceListener tsl)
/* 331:    */   {
/* 332:424 */     this.m_dataListeners.removeElement(tsl);
/* 333:    */   }
/* 334:    */   
/* 335:    */   public synchronized void addTrainingSetListener(TrainingSetListener tsl)
/* 336:    */   {
/* 337:429 */     this.m_trainingListeners.addElement(tsl);
/* 338:431 */     if (this.m_connectedFormat != null)
/* 339:    */     {
/* 340:432 */       TrainingSetEvent e = new TrainingSetEvent(this, this.m_connectedFormat);
/* 341:433 */       tsl.acceptTrainingSet(e);
/* 342:    */     }
/* 343:    */   }
/* 344:    */   
/* 345:    */   public synchronized void removeTrainingSetListener(TrainingSetListener tsl)
/* 346:    */   {
/* 347:439 */     this.m_trainingListeners.removeElement(tsl);
/* 348:    */   }
/* 349:    */   
/* 350:    */   public synchronized void addTestSetListener(TestSetListener tsl)
/* 351:    */   {
/* 352:444 */     this.m_testListeners.addElement(tsl);
/* 353:446 */     if (this.m_connectedFormat != null)
/* 354:    */     {
/* 355:447 */       TestSetEvent e = new TestSetEvent(this, this.m_connectedFormat);
/* 356:448 */       tsl.acceptTestSet(e);
/* 357:    */     }
/* 358:    */   }
/* 359:    */   
/* 360:    */   public synchronized void removeTestSetListener(TestSetListener tsl)
/* 361:    */   {
/* 362:454 */     this.m_testListeners.removeElement(tsl);
/* 363:    */   }
/* 364:    */   
/* 365:    */   public synchronized void addDataFormatListener(DataFormatListener dfl)
/* 366:    */   {
/* 367:458 */     this.m_dataFormatListeners.addElement(dfl);
/* 368:    */   }
/* 369:    */   
/* 370:    */   public synchronized void removeDataFormatListener(DataFormatListener dfl)
/* 371:    */   {
/* 372:462 */     this.m_dataFormatListeners.removeElement(dfl);
/* 373:    */   }
/* 374:    */   
/* 375:    */   public void setVisual(BeanVisual newVisual)
/* 376:    */   {
/* 377:467 */     this.m_visual = newVisual;
/* 378:    */   }
/* 379:    */   
/* 380:    */   public BeanVisual getVisual()
/* 381:    */   {
/* 382:472 */     return this.m_visual;
/* 383:    */   }
/* 384:    */   
/* 385:    */   public void useDefaultVisual()
/* 386:    */   {
/* 387:477 */     this.m_visual.loadIcons("weka/gui/beans/icons/ClassAssigner.gif", "weka/gui/beans/icons/ClassAssigner_animated.gif");
/* 388:    */   }
/* 389:    */   
/* 390:    */   public boolean connectionAllowed(String eventName)
/* 391:    */   {
/* 392:490 */     if ((eventName.compareTo("trainingSet") == 0) && ((this.m_trainingProvider != null) || (this.m_dataProvider != null) || (this.m_instanceProvider != null))) {
/* 393:492 */       return false;
/* 394:    */     }
/* 395:495 */     if ((eventName.compareTo("testSet") == 0) && (this.m_testProvider != null)) {
/* 396:496 */       return false;
/* 397:    */     }
/* 398:499 */     if (((eventName.compareTo("instance") == 0) && (this.m_instanceProvider != null)) || (this.m_trainingProvider != null) || (this.m_dataProvider != null)) {
/* 399:501 */       return false;
/* 400:    */     }
/* 401:503 */     return true;
/* 402:    */   }
/* 403:    */   
/* 404:    */   public boolean connectionAllowed(EventSetDescriptor esd)
/* 405:    */   {
/* 406:515 */     return connectionAllowed(esd.getName());
/* 407:    */   }
/* 408:    */   
/* 409:    */   public synchronized void connectionNotification(String eventName, Object source)
/* 410:    */   {
/* 411:529 */     if (connectionAllowed(eventName))
/* 412:    */     {
/* 413:530 */       if (eventName.compareTo("trainingSet") == 0) {
/* 414:531 */         this.m_trainingProvider = source;
/* 415:532 */       } else if (eventName.compareTo("testSet") == 0) {
/* 416:533 */         this.m_testProvider = source;
/* 417:534 */       } else if (eventName.compareTo("dataSet") == 0) {
/* 418:535 */         this.m_dataProvider = source;
/* 419:536 */       } else if (eventName.compareTo("instance") == 0) {
/* 420:537 */         this.m_instanceProvider = source;
/* 421:    */       }
/* 422:539 */       this.m_connectedFormat = null;
/* 423:    */     }
/* 424:    */   }
/* 425:    */   
/* 426:    */   public synchronized void disconnectionNotification(String eventName, Object source)
/* 427:    */   {
/* 428:555 */     if ((eventName.compareTo("trainingSet") == 0) && 
/* 429:556 */       (this.m_trainingProvider == source)) {
/* 430:557 */       this.m_trainingProvider = null;
/* 431:    */     }
/* 432:560 */     if ((eventName.compareTo("testSet") == 0) && 
/* 433:561 */       (this.m_testProvider == source)) {
/* 434:562 */       this.m_testProvider = null;
/* 435:    */     }
/* 436:565 */     if ((eventName.compareTo("dataSet") == 0) && 
/* 437:566 */       (this.m_dataProvider == source)) {
/* 438:567 */       this.m_dataProvider = null;
/* 439:    */     }
/* 440:571 */     if ((eventName.compareTo("instance") == 0) && 
/* 441:572 */       (this.m_instanceProvider == source)) {
/* 442:573 */       this.m_instanceProvider = null;
/* 443:    */     }
/* 444:576 */     this.m_connectedFormat = null;
/* 445:    */   }
/* 446:    */   
/* 447:    */   public void setLog(Logger logger)
/* 448:    */   {
/* 449:581 */     this.m_logger = logger;
/* 450:    */   }
/* 451:    */   
/* 452:    */   public void stop()
/* 453:    */   {
/* 454:587 */     if ((this.m_trainingProvider != null) && ((this.m_trainingProvider instanceof BeanCommon))) {
/* 455:588 */       ((BeanCommon)this.m_trainingProvider).stop();
/* 456:    */     }
/* 457:591 */     if ((this.m_testProvider != null) && ((this.m_testProvider instanceof BeanCommon))) {
/* 458:592 */       ((BeanCommon)this.m_testProvider).stop();
/* 459:    */     }
/* 460:595 */     if ((this.m_dataProvider != null) && ((this.m_dataProvider instanceof BeanCommon))) {
/* 461:596 */       ((BeanCommon)this.m_dataProvider).stop();
/* 462:    */     }
/* 463:599 */     if ((this.m_instanceProvider != null) && ((this.m_instanceProvider instanceof BeanCommon))) {
/* 464:600 */       ((BeanCommon)this.m_instanceProvider).stop();
/* 465:    */     }
/* 466:    */   }
/* 467:    */   
/* 468:    */   public boolean isBusy()
/* 469:    */   {
/* 470:612 */     return false;
/* 471:    */   }
/* 472:    */   
/* 473:    */   public boolean eventGeneratable(String eventName)
/* 474:    */   {
/* 475:625 */     if (eventName.compareTo("trainingSet") == 0)
/* 476:    */     {
/* 477:626 */       if (this.m_trainingProvider == null) {
/* 478:627 */         return false;
/* 479:    */       }
/* 480:629 */       if (((this.m_trainingProvider instanceof EventConstraints)) && 
/* 481:630 */         (!((EventConstraints)this.m_trainingProvider).eventGeneratable("trainingSet"))) {
/* 482:632 */         return false;
/* 483:    */       }
/* 484:    */     }
/* 485:638 */     if (eventName.compareTo("dataSet") == 0)
/* 486:    */     {
/* 487:639 */       if (this.m_dataProvider == null)
/* 488:    */       {
/* 489:640 */         if (this.m_instanceProvider == null)
/* 490:    */         {
/* 491:641 */           this.m_connectedFormat = null;
/* 492:642 */           notifyDataFormatListeners();
/* 493:    */         }
/* 494:644 */         return false;
/* 495:    */       }
/* 496:646 */       if (((this.m_dataProvider instanceof EventConstraints)) && 
/* 497:647 */         (!((EventConstraints)this.m_dataProvider).eventGeneratable("dataSet")))
/* 498:    */       {
/* 499:648 */         this.m_connectedFormat = null;
/* 500:649 */         notifyDataFormatListeners();
/* 501:650 */         return false;
/* 502:    */       }
/* 503:    */     }
/* 504:656 */     if (eventName.compareTo("instance") == 0)
/* 505:    */     {
/* 506:657 */       if (this.m_instanceProvider == null)
/* 507:    */       {
/* 508:658 */         if (this.m_dataProvider == null)
/* 509:    */         {
/* 510:659 */           this.m_connectedFormat = null;
/* 511:660 */           notifyDataFormatListeners();
/* 512:    */         }
/* 513:662 */         return false;
/* 514:    */       }
/* 515:664 */       if (((this.m_instanceProvider instanceof EventConstraints)) && 
/* 516:665 */         (!((EventConstraints)this.m_instanceProvider).eventGeneratable("instance")))
/* 517:    */       {
/* 518:667 */         this.m_connectedFormat = null;
/* 519:668 */         notifyDataFormatListeners();
/* 520:669 */         return false;
/* 521:    */       }
/* 522:    */     }
/* 523:675 */     if (eventName.compareTo("testSet") == 0)
/* 524:    */     {
/* 525:676 */       if (this.m_testProvider == null) {
/* 526:677 */         return false;
/* 527:    */       }
/* 528:679 */       if (((this.m_testProvider instanceof EventConstraints)) && 
/* 529:680 */         (!((EventConstraints)this.m_testProvider).eventGeneratable("testSet"))) {
/* 530:681 */         return false;
/* 531:    */       }
/* 532:    */     }
/* 533:686 */     return true;
/* 534:    */   }
/* 535:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.ClassAssigner
 * JD-Core Version:    0.7.0.1
 */