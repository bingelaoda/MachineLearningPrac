/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.beans.EventSetDescriptor;
/*   5:    */ import java.io.Serializable;
/*   6:    */ import java.util.ArrayList;
/*   7:    */ import java.util.List;
/*   8:    */ import javax.swing.JPanel;
/*   9:    */ import weka.core.Environment;
/*  10:    */ import weka.core.EnvironmentHandler;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.filters.unsupervised.attribute.Add;
/*  14:    */ import weka.gui.Logger;
/*  15:    */ 
/*  16:    */ @KFStep(category="Tools", toolTipText="Label instances according to substring matches in String attributes")
/*  17:    */ public class SubstringLabeler
/*  18:    */   extends JPanel
/*  19:    */   implements BeanCommon, Visible, Serializable, InstanceListener, TrainingSetListener, TestSetListener, DataSourceListener, EventConstraints, EnvironmentHandler, DataSource
/*  20:    */ {
/*  21:    */   private static final long serialVersionUID = 6297059699297260134L;
/*  22:    */   protected transient Environment m_env;
/*  23: 66 */   protected String m_matchDetails = "";
/*  24:    */   protected transient SubstringLabelerRules m_matches;
/*  25:    */   protected transient Logger m_log;
/*  26:    */   protected transient boolean m_busy;
/*  27:    */   protected Object m_listenee;
/*  28: 81 */   protected ArrayList<InstanceListener> m_instanceListeners = new ArrayList();
/*  29: 84 */   protected ArrayList<DataSourceListener> m_dataListeners = new ArrayList();
/*  30:    */   protected boolean m_nominalBinary;
/*  31:    */   protected boolean m_consumeNonMatchingInstances;
/*  32:    */   protected Add m_addFilter;
/*  33:102 */   protected String m_attName = "Match";
/*  34:108 */   protected InstanceEvent m_ie = new InstanceEvent(this);
/*  35:113 */   protected BeanVisual m_visual = new BeanVisual("SubstringLabeler", "weka/gui/beans/icons/DefaultFilter.gif", "weka/gui/beans/icons/DefaultFilter_animated.gif");
/*  36:    */   protected transient StreamThroughput m_throughput;
/*  37:    */   
/*  38:    */   public SubstringLabeler()
/*  39:    */   {
/*  40:121 */     useDefaultVisual();
/*  41:122 */     setLayout(new BorderLayout());
/*  42:123 */     add(this.m_visual, "Center");
/*  43:    */     
/*  44:125 */     this.m_env = Environment.getSystemWide();
/*  45:    */   }
/*  46:    */   
/*  47:    */   public String globalInfo()
/*  48:    */   {
/*  49:134 */     return "Matches substrings in String attributes using either literal or regular expression matches. The value of a new attribute is set to reflect the status of the match. The new attribute can be either binary (in which case values indicate match or no match) or multi-valued nominal, in which case a label must be associated with each distinct matching rule. In the case of labeled matches, the user can opt to have non matching instances output with missing value set for the new attribute or not output at all (i.e. consumed by the step).";
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void setMatchDetails(String details)
/*  53:    */   {
/*  54:153 */     this.m_matchDetails = details;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public String getMatchDetails()
/*  58:    */   {
/*  59:162 */     return this.m_matchDetails;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void setNominalBinary(boolean nom)
/*  63:    */   {
/*  64:172 */     this.m_nominalBinary = nom;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public boolean getNominalBinary()
/*  68:    */   {
/*  69:182 */     return this.m_nominalBinary;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void setConsumeNonMatching(boolean consume)
/*  73:    */   {
/*  74:194 */     this.m_consumeNonMatchingInstances = consume;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public boolean getConsumeNonMatching()
/*  78:    */   {
/*  79:205 */     return this.m_consumeNonMatchingInstances;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public void setMatchAttributeName(String name)
/*  83:    */   {
/*  84:209 */     this.m_attName = name;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public String getMatchAttributeName()
/*  88:    */   {
/*  89:213 */     return this.m_attName;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public void addDataSourceListener(DataSourceListener dsl)
/*  93:    */   {
/*  94:223 */     this.m_dataListeners.add(dsl);
/*  95:    */   }
/*  96:    */   
/*  97:    */   public void removeDataSourceListener(DataSourceListener dsl)
/*  98:    */   {
/*  99:233 */     this.m_dataListeners.remove(dsl);
/* 100:    */   }
/* 101:    */   
/* 102:    */   public void addInstanceListener(InstanceListener dsl)
/* 103:    */   {
/* 104:243 */     this.m_instanceListeners.add(dsl);
/* 105:    */   }
/* 106:    */   
/* 107:    */   public void removeInstanceListener(InstanceListener dsl)
/* 108:    */   {
/* 109:253 */     this.m_instanceListeners.remove(dsl);
/* 110:    */   }
/* 111:    */   
/* 112:    */   public void setEnvironment(Environment env)
/* 113:    */   {
/* 114:261 */     this.m_env = env;
/* 115:    */   }
/* 116:    */   
/* 117:    */   public boolean eventGeneratable(String eventName)
/* 118:    */   {
/* 119:272 */     if (this.m_listenee == null) {
/* 120:273 */       return false;
/* 121:    */     }
/* 122:276 */     if ((!eventName.equals("instance")) && (!eventName.equals("dataSet"))) {
/* 123:277 */       return false;
/* 124:    */     }
/* 125:280 */     if (((this.m_listenee instanceof DataSource)) && 
/* 126:281 */       ((this.m_listenee instanceof EventConstraints)))
/* 127:    */     {
/* 128:282 */       EventConstraints ec = (EventConstraints)this.m_listenee;
/* 129:283 */       return ec.eventGeneratable(eventName);
/* 130:    */     }
/* 131:287 */     if (((this.m_listenee instanceof TrainingSetProducer)) && 
/* 132:288 */       ((this.m_listenee instanceof EventConstraints)))
/* 133:    */     {
/* 134:289 */       EventConstraints ec = (EventConstraints)this.m_listenee;
/* 135:291 */       if (!eventName.equals("dataSet")) {
/* 136:292 */         return false;
/* 137:    */       }
/* 138:295 */       if (!ec.eventGeneratable("trainingSet")) {
/* 139:296 */         return false;
/* 140:    */       }
/* 141:    */     }
/* 142:301 */     if (((this.m_listenee instanceof TestSetProducer)) && 
/* 143:302 */       ((this.m_listenee instanceof EventConstraints)))
/* 144:    */     {
/* 145:303 */       EventConstraints ec = (EventConstraints)this.m_listenee;
/* 146:305 */       if (!eventName.equals("dataSet")) {
/* 147:306 */         return false;
/* 148:    */       }
/* 149:309 */       if (!ec.eventGeneratable("testSet")) {
/* 150:310 */         return false;
/* 151:    */       }
/* 152:    */     }
/* 153:315 */     return true;
/* 154:    */   }
/* 155:    */   
/* 156:    */   public void useDefaultVisual()
/* 157:    */   {
/* 158:323 */     this.m_visual.loadIcons("weka/gui/beans/icons/DefaultFilter.gif", "weka/gui/beans/icons/DefaultFilter_animated.gif");
/* 159:    */     
/* 160:325 */     this.m_visual.setText("SubstringLabeler");
/* 161:    */   }
/* 162:    */   
/* 163:    */   public void setVisual(BeanVisual newVisual)
/* 164:    */   {
/* 165:335 */     this.m_visual = newVisual;
/* 166:    */   }
/* 167:    */   
/* 168:    */   public BeanVisual getVisual()
/* 169:    */   {
/* 170:345 */     return this.m_visual;
/* 171:    */   }
/* 172:    */   
/* 173:    */   public void setCustomName(String name)
/* 174:    */   {
/* 175:355 */     this.m_visual.setText(name);
/* 176:    */   }
/* 177:    */   
/* 178:    */   public String getCustomName()
/* 179:    */   {
/* 180:365 */     return this.m_visual.getText();
/* 181:    */   }
/* 182:    */   
/* 183:    */   public void stop()
/* 184:    */   {
/* 185:373 */     if ((this.m_listenee != null) && 
/* 186:374 */       ((this.m_listenee instanceof BeanCommon))) {
/* 187:375 */       ((BeanCommon)this.m_listenee).stop();
/* 188:    */     }
/* 189:379 */     if (this.m_log != null) {
/* 190:380 */       this.m_log.statusMessage(statusMessagePrefix() + "Stopped");
/* 191:    */     }
/* 192:383 */     this.m_busy = false;
/* 193:    */   }
/* 194:    */   
/* 195:    */   public boolean isBusy()
/* 196:    */   {
/* 197:394 */     return this.m_busy;
/* 198:    */   }
/* 199:    */   
/* 200:    */   public void setLog(Logger logger)
/* 201:    */   {
/* 202:404 */     this.m_log = logger;
/* 203:    */   }
/* 204:    */   
/* 205:    */   public boolean connectionAllowed(EventSetDescriptor esd)
/* 206:    */   {
/* 207:416 */     return connectionAllowed(esd.getName());
/* 208:    */   }
/* 209:    */   
/* 210:    */   public boolean connectionAllowed(String eventName)
/* 211:    */   {
/* 212:428 */     if ((!eventName.equals("instance")) && (!eventName.equals("dataSet")) && (!eventName.equals("trainingSet")) && (!eventName.equals("testSet"))) {
/* 213:430 */       return false;
/* 214:    */     }
/* 215:433 */     if (this.m_listenee != null) {
/* 216:434 */       return false;
/* 217:    */     }
/* 218:437 */     return true;
/* 219:    */   }
/* 220:    */   
/* 221:    */   public void connectionNotification(String eventName, Object source)
/* 222:    */   {
/* 223:451 */     if (connectionAllowed(eventName)) {
/* 224:452 */       this.m_listenee = source;
/* 225:    */     }
/* 226:    */   }
/* 227:    */   
/* 228:    */   public void disconnectionNotification(String eventName, Object source)
/* 229:    */   {
/* 230:466 */     if (source == this.m_listenee) {
/* 231:467 */       this.m_listenee = null;
/* 232:    */     }
/* 233:    */   }
/* 234:    */   
/* 235:    */   protected void makeOutputStructure(Instances inputStructure)
/* 236:    */     throws Exception
/* 237:    */   {
/* 238:479 */     this.m_matches = new SubstringLabelerRules(this.m_matchDetails, this.m_attName, getConsumeNonMatching(), getNominalBinary(), inputStructure, statusMessagePrefix(), this.m_log, this.m_env);
/* 239:    */   }
/* 240:    */   
/* 241:    */   public void acceptInstance(InstanceEvent e)
/* 242:    */   {
/* 243:494 */     this.m_busy = true;
/* 244:496 */     if (e.getStatus() == 0)
/* 245:    */     {
/* 246:497 */       this.m_throughput = new StreamThroughput(statusMessagePrefix());
/* 247:    */       
/* 248:499 */       Instances structure = e.getStructure();
/* 249:    */       try
/* 250:    */       {
/* 251:502 */         makeOutputStructure(structure);
/* 252:    */       }
/* 253:    */       catch (Exception ex)
/* 254:    */       {
/* 255:504 */         String msg = statusMessagePrefix() + "ERROR: unable to create output instances structure.";
/* 256:506 */         if (this.m_log != null)
/* 257:    */         {
/* 258:507 */           this.m_log.statusMessage(msg);
/* 259:508 */           this.m_log.logMessage("[SubstringLabeler] " + ex.getMessage());
/* 260:    */         }
/* 261:510 */         stop();
/* 262:    */         
/* 263:512 */         ex.printStackTrace();
/* 264:513 */         this.m_busy = false;
/* 265:514 */         return;
/* 266:    */       }
/* 267:517 */       if ((!e.m_formatNotificationOnly) && 
/* 268:518 */         (this.m_log != null)) {
/* 269:519 */         this.m_log.statusMessage(statusMessagePrefix() + "Processing stream...");
/* 270:    */       }
/* 271:523 */       this.m_ie.setStructure(this.m_matches.getOutputStructure());
/* 272:524 */       this.m_ie.m_formatNotificationOnly = e.m_formatNotificationOnly;
/* 273:525 */       notifyInstanceListeners(this.m_ie);
/* 274:    */     }
/* 275:    */     else
/* 276:    */     {
/* 277:527 */       Instance inst = e.getInstance();
/* 278:528 */       Instance out = null;
/* 279:529 */       if (inst != null)
/* 280:    */       {
/* 281:530 */         this.m_throughput.updateStart();
/* 282:    */         try
/* 283:    */         {
/* 284:532 */           out = this.m_matches.makeOutputInstance(inst, false);
/* 285:    */         }
/* 286:    */         catch (Exception e1)
/* 287:    */         {
/* 288:535 */           e1.printStackTrace();
/* 289:    */         }
/* 290:537 */         this.m_throughput.updateEnd(this.m_log);
/* 291:    */       }
/* 292:540 */       if ((inst == null) || (out != null) || (e.getStatus() == 2))
/* 293:    */       {
/* 294:543 */         this.m_ie.setInstance(out);
/* 295:544 */         this.m_ie.setStatus(e.getStatus());
/* 296:545 */         notifyInstanceListeners(this.m_ie);
/* 297:    */       }
/* 298:548 */       if ((e.getStatus() == 2) || (inst == null)) {
/* 299:550 */         this.m_throughput.finished(this.m_log);
/* 300:    */       }
/* 301:    */     }
/* 302:554 */     this.m_busy = false;
/* 303:    */   }
/* 304:    */   
/* 305:    */   public void acceptDataSet(DataSetEvent e)
/* 306:    */   {
/* 307:565 */     this.m_busy = true;
/* 308:566 */     if (this.m_log != null) {
/* 309:567 */       this.m_log.statusMessage(statusMessagePrefix() + "Processing batch...");
/* 310:    */     }
/* 311:    */     try
/* 312:    */     {
/* 313:571 */       makeOutputStructure(new Instances(e.getDataSet(), 0));
/* 314:    */     }
/* 315:    */     catch (Exception ex)
/* 316:    */     {
/* 317:573 */       String msg = statusMessagePrefix() + "ERROR: unable to create output instances structure.";
/* 318:575 */       if (this.m_log != null)
/* 319:    */       {
/* 320:576 */         this.m_log.statusMessage(msg);
/* 321:577 */         this.m_log.logMessage("[SubstringLabeler] " + ex.getMessage());
/* 322:    */       }
/* 323:579 */       stop();
/* 324:    */       
/* 325:581 */       ex.printStackTrace();
/* 326:582 */       this.m_busy = false;
/* 327:583 */       return;
/* 328:    */     }
/* 329:586 */     Instances toProcess = e.getDataSet();
/* 330:588 */     for (int i = 0; i < toProcess.numInstances(); i++)
/* 331:    */     {
/* 332:589 */       Instance current = toProcess.instance(i);
/* 333:590 */       Instance result = null;
/* 334:    */       try
/* 335:    */       {
/* 336:592 */         result = this.m_matches.makeOutputInstance(current, true);
/* 337:    */       }
/* 338:    */       catch (Exception ex)
/* 339:    */       {
/* 340:594 */         ex.printStackTrace();
/* 341:    */       }
/* 342:597 */       if (result != null) {
/* 343:599 */         this.m_matches.getOutputStructure().add(result);
/* 344:    */       }
/* 345:    */     }
/* 346:603 */     if (this.m_log != null) {
/* 347:604 */       this.m_log.statusMessage(statusMessagePrefix() + "Finished.");
/* 348:    */     }
/* 349:608 */     DataSetEvent d = new DataSetEvent(this, this.m_matches.getOutputStructure());
/* 350:609 */     notifyDataListeners(d);
/* 351:    */     
/* 352:611 */     this.m_busy = false;
/* 353:    */   }
/* 354:    */   
/* 355:    */   public void acceptTestSet(TestSetEvent e)
/* 356:    */   {
/* 357:622 */     Instances test = e.getTestSet();
/* 358:623 */     DataSetEvent d = new DataSetEvent(this, test);
/* 359:624 */     acceptDataSet(d);
/* 360:    */   }
/* 361:    */   
/* 362:    */   public void acceptTrainingSet(TrainingSetEvent e)
/* 363:    */   {
/* 364:635 */     Instances train = e.getTrainingSet();
/* 365:636 */     DataSetEvent d = new DataSetEvent(this, train);
/* 366:637 */     acceptDataSet(d);
/* 367:    */   }
/* 368:    */   
/* 369:    */   private void notifyDataListeners(DataSetEvent e)
/* 370:    */   {
/* 371:    */     List<DataSourceListener> l;
/* 372:643 */     synchronized (this)
/* 373:    */     {
/* 374:644 */       l = (List)this.m_dataListeners.clone();
/* 375:    */     }
/* 376:646 */     if (l.size() > 0) {
/* 377:647 */       for (DataSourceListener ds : l) {
/* 378:648 */         ds.acceptDataSet(e);
/* 379:    */       }
/* 380:    */     }
/* 381:    */   }
/* 382:    */   
/* 383:    */   private void notifyInstanceListeners(InstanceEvent e)
/* 384:    */   {
/* 385:    */     List<InstanceListener> l;
/* 386:656 */     synchronized (this)
/* 387:    */     {
/* 388:657 */       l = (List)this.m_instanceListeners.clone();
/* 389:    */     }
/* 390:659 */     if (l.size() > 0) {
/* 391:660 */       for (InstanceListener il : l) {
/* 392:661 */         il.acceptInstance(e);
/* 393:    */       }
/* 394:    */     }
/* 395:    */   }
/* 396:    */   
/* 397:    */   protected String statusMessagePrefix()
/* 398:    */   {
/* 399:667 */     return getCustomName() + "$" + hashCode() + "|";
/* 400:    */   }
/* 401:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.SubstringLabeler
 * JD-Core Version:    0.7.0.1
 */