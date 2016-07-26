/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.Font;
/*   6:    */ import java.awt.GraphicsEnvironment;
/*   7:    */ import java.awt.event.ActionEvent;
/*   8:    */ import java.awt.event.ActionListener;
/*   9:    */ import java.awt.event.MouseAdapter;
/*  10:    */ import java.awt.event.MouseEvent;
/*  11:    */ import java.awt.event.WindowAdapter;
/*  12:    */ import java.awt.event.WindowEvent;
/*  13:    */ import java.beans.EventSetDescriptor;
/*  14:    */ import java.beans.PropertyChangeListener;
/*  15:    */ import java.beans.VetoableChangeListener;
/*  16:    */ import java.beans.beancontext.BeanContext;
/*  17:    */ import java.beans.beancontext.BeanContextChild;
/*  18:    */ import java.beans.beancontext.BeanContextChildSupport;
/*  19:    */ import java.text.SimpleDateFormat;
/*  20:    */ import java.util.ArrayList;
/*  21:    */ import java.util.Date;
/*  22:    */ import java.util.Enumeration;
/*  23:    */ import java.util.EventObject;
/*  24:    */ import java.util.List;
/*  25:    */ import java.util.Vector;
/*  26:    */ import javax.swing.BorderFactory;
/*  27:    */ import javax.swing.JFrame;
/*  28:    */ import javax.swing.JList;
/*  29:    */ import javax.swing.JMenuItem;
/*  30:    */ import javax.swing.JPanel;
/*  31:    */ import javax.swing.JPopupMenu;
/*  32:    */ import javax.swing.JScrollPane;
/*  33:    */ import javax.swing.JSplitPane;
/*  34:    */ import javax.swing.JTextArea;
/*  35:    */ import weka.core.Instances;
/*  36:    */ import weka.gui.Logger;
/*  37:    */ import weka.gui.ResultHistoryPanel;
/*  38:    */ import weka.gui.SaveBuffer;
/*  39:    */ 
/*  40:    */ public class TextViewer
/*  41:    */   extends JPanel
/*  42:    */   implements TextListener, DataSourceListener, TrainingSetListener, TestSetListener, Visible, UserRequestAcceptor, BeanContextChild, BeanCommon, EventConstraints, HeadlessEventCollector
/*  43:    */ {
/*  44:    */   private static final long serialVersionUID = 104838186352536832L;
/*  45:    */   protected BeanVisual m_visual;
/*  46: 75 */   private transient JFrame m_resultsFrame = null;
/*  47:    */   protected List<EventObject> m_headlessEvents;
/*  48: 82 */   private transient JTextArea m_outText = null;
/*  49:    */   protected transient ResultHistoryPanel m_history;
/*  50:    */   protected boolean m_design;
/*  51: 97 */   protected transient BeanContext m_beanContext = null;
/*  52:102 */   protected BeanContextChildSupport m_bcSupport = new BeanContextChildSupport(this);
/*  53:108 */   private final Vector<TextListener> m_textListeners = new Vector();
/*  54:    */   
/*  55:    */   public TextViewer()
/*  56:    */   {
/*  57:112 */     GraphicsEnvironment.getLocalGraphicsEnvironment();
/*  58:113 */     if (!GraphicsEnvironment.isHeadless()) {
/*  59:114 */       appearanceFinal();
/*  60:    */     } else {
/*  61:116 */       this.m_headlessEvents = new ArrayList();
/*  62:    */     }
/*  63:    */   }
/*  64:    */   
/*  65:    */   protected void appearanceDesign()
/*  66:    */   {
/*  67:121 */     setUpResultHistory();
/*  68:122 */     removeAll();
/*  69:123 */     if (this.m_visual == null) {
/*  70:124 */       this.m_visual = new BeanVisual("TextViewer", "weka/gui/beans/icons/DefaultText.gif", "weka/gui/beans/icons/DefaultText_animated.gif");
/*  71:    */     }
/*  72:128 */     setLayout(new BorderLayout());
/*  73:129 */     add(this.m_visual, "Center");
/*  74:    */   }
/*  75:    */   
/*  76:    */   protected void appearanceFinal()
/*  77:    */   {
/*  78:133 */     removeAll();
/*  79:134 */     setLayout(new BorderLayout());
/*  80:135 */     setUpFinal();
/*  81:    */   }
/*  82:    */   
/*  83:    */   protected void setUpFinal()
/*  84:    */   {
/*  85:139 */     setUpResultHistory();
/*  86:140 */     JPanel holder = new JPanel();
/*  87:141 */     holder.setLayout(new BorderLayout());
/*  88:142 */     JScrollPane js = new JScrollPane(this.m_outText);
/*  89:143 */     js.setBorder(BorderFactory.createTitledBorder("Text"));
/*  90:144 */     holder.add(js, "Center");
/*  91:145 */     holder.add(this.m_history, "West");
/*  92:    */     
/*  93:147 */     add(holder, "Center");
/*  94:    */   }
/*  95:    */   
/*  96:    */   public String globalInfo()
/*  97:    */   {
/*  98:156 */     return "General purpose text display.";
/*  99:    */   }
/* 100:    */   
/* 101:    */   private void setUpResultHistory()
/* 102:    */   {
/* 103:160 */     GraphicsEnvironment.getLocalGraphicsEnvironment();
/* 104:161 */     if (!GraphicsEnvironment.isHeadless())
/* 105:    */     {
/* 106:162 */       if (this.m_outText == null)
/* 107:    */       {
/* 108:163 */         this.m_outText = new JTextArea(20, 80);
/* 109:164 */         this.m_history = new ResultHistoryPanel(this.m_outText);
/* 110:    */       }
/* 111:166 */       this.m_outText.setEditable(false);
/* 112:167 */       this.m_outText.setFont(new Font("Monospaced", 0, 12));
/* 113:168 */       this.m_outText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/* 114:169 */       this.m_history.setBorder(BorderFactory.createTitledBorder("Result list"));
/* 115:170 */       this.m_history.setHandleRightClicks(false);
/* 116:171 */       this.m_history.getList().addMouseListener(new MouseAdapter()
/* 117:    */       {
/* 118:    */         public void mouseClicked(MouseEvent e)
/* 119:    */         {
/* 120:174 */           if (((e.getModifiers() & 0x10) != 16) || (e.isAltDown()))
/* 121:    */           {
/* 122:176 */             int index = TextViewer.this.m_history.getList().locationToIndex(e.getPoint());
/* 123:177 */             if (index != -1)
/* 124:    */             {
/* 125:178 */               String name = TextViewer.this.m_history.getNameAtIndex(index);
/* 126:179 */               TextViewer.this.visualize(name, e.getX(), e.getY());
/* 127:    */             }
/* 128:    */             else
/* 129:    */             {
/* 130:181 */               TextViewer.this.visualize(null, e.getX(), e.getY());
/* 131:    */             }
/* 132:    */           }
/* 133:    */         }
/* 134:    */       });
/* 135:    */     }
/* 136:    */   }
/* 137:    */   
/* 138:    */   protected void visualize(String name, int x, int y)
/* 139:    */   {
/* 140:198 */     final JPanel panel = this;
/* 141:199 */     final String selectedName = name;
/* 142:200 */     JPopupMenu resultListMenu = new JPopupMenu();
/* 143:    */     
/* 144:202 */     JMenuItem visMainBuffer = new JMenuItem("View in main window");
/* 145:203 */     if (selectedName != null) {
/* 146:204 */       visMainBuffer.addActionListener(new ActionListener()
/* 147:    */       {
/* 148:    */         public void actionPerformed(ActionEvent e)
/* 149:    */         {
/* 150:207 */           TextViewer.this.m_history.setSingle(selectedName);
/* 151:    */         }
/* 152:    */       });
/* 153:    */     } else {
/* 154:211 */       visMainBuffer.setEnabled(false);
/* 155:    */     }
/* 156:213 */     resultListMenu.add(visMainBuffer);
/* 157:    */     
/* 158:215 */     JMenuItem visSepBuffer = new JMenuItem("View in separate window");
/* 159:216 */     if (selectedName != null) {
/* 160:217 */       visSepBuffer.addActionListener(new ActionListener()
/* 161:    */       {
/* 162:    */         public void actionPerformed(ActionEvent e)
/* 163:    */         {
/* 164:220 */           TextViewer.this.m_history.openFrame(selectedName);
/* 165:    */         }
/* 166:    */       });
/* 167:    */     } else {
/* 168:224 */       visSepBuffer.setEnabled(false);
/* 169:    */     }
/* 170:226 */     resultListMenu.add(visSepBuffer);
/* 171:    */     
/* 172:228 */     JMenuItem saveOutput = new JMenuItem("Save result buffer");
/* 173:229 */     if (selectedName != null) {
/* 174:230 */       saveOutput.addActionListener(new ActionListener()
/* 175:    */       {
/* 176:    */         public void actionPerformed(ActionEvent e)
/* 177:    */         {
/* 178:233 */           SaveBuffer m_SaveOut = new SaveBuffer(null, panel);
/* 179:234 */           StringBuffer sb = TextViewer.this.m_history.getNamedBuffer(selectedName);
/* 180:235 */           if (sb != null) {
/* 181:236 */             m_SaveOut.save(sb);
/* 182:    */           }
/* 183:    */         }
/* 184:    */       });
/* 185:    */     } else {
/* 186:241 */       saveOutput.setEnabled(false);
/* 187:    */     }
/* 188:243 */     resultListMenu.add(saveOutput);
/* 189:    */     
/* 190:245 */     JMenuItem deleteOutput = new JMenuItem("Delete result buffer");
/* 191:246 */     if (selectedName != null) {
/* 192:247 */       deleteOutput.addActionListener(new ActionListener()
/* 193:    */       {
/* 194:    */         public void actionPerformed(ActionEvent e)
/* 195:    */         {
/* 196:250 */           TextViewer.this.m_history.removeResult(selectedName);
/* 197:    */         }
/* 198:    */       });
/* 199:    */     } else {
/* 200:254 */       deleteOutput.setEnabled(false);
/* 201:    */     }
/* 202:256 */     resultListMenu.add(deleteOutput);
/* 203:    */     
/* 204:258 */     resultListMenu.show(this.m_history.getList(), x, y);
/* 205:    */   }
/* 206:    */   
/* 207:    */   public synchronized void acceptDataSet(DataSetEvent e)
/* 208:    */   {
/* 209:268 */     TextEvent nt = new TextEvent(e.getSource(), e.getDataSet().toString(), e.getDataSet().relationName());
/* 210:    */     
/* 211:    */ 
/* 212:271 */     acceptText(nt);
/* 213:    */   }
/* 214:    */   
/* 215:    */   public synchronized void acceptTrainingSet(TrainingSetEvent e)
/* 216:    */   {
/* 217:281 */     TextEvent nt = new TextEvent(e.getSource(), e.getTrainingSet().toString(), e.getTrainingSet().relationName());
/* 218:    */     
/* 219:    */ 
/* 220:284 */     acceptText(nt);
/* 221:    */   }
/* 222:    */   
/* 223:    */   public synchronized void acceptTestSet(TestSetEvent e)
/* 224:    */   {
/* 225:294 */     TextEvent nt = new TextEvent(e.getSource(), e.getTestSet().toString(), e.getTestSet().relationName());
/* 226:    */     
/* 227:    */ 
/* 228:297 */     acceptText(nt);
/* 229:    */   }
/* 230:    */   
/* 231:    */   public synchronized void acceptText(TextEvent e)
/* 232:    */   {
/* 233:307 */     if (this.m_outText == null) {
/* 234:308 */       setUpResultHistory();
/* 235:    */     }
/* 236:310 */     StringBuffer result = new StringBuffer();
/* 237:311 */     result.append(e.getText());
/* 238:    */     
/* 239:    */ 
/* 240:314 */     String name = new SimpleDateFormat("HH:mm:ss - ").format(new Date());
/* 241:315 */     name = name + e.getTextTitle();
/* 242:317 */     if (this.m_outText != null)
/* 243:    */     {
/* 244:320 */       int mod = 2;
/* 245:321 */       String nameOrig = new String(name);
/* 246:322 */       while (this.m_history.getNamedBuffer(name) != null)
/* 247:    */       {
/* 248:323 */         name = new String(nameOrig + "" + mod);
/* 249:324 */         mod++;
/* 250:    */       }
/* 251:326 */       this.m_history.addResult(name, result);
/* 252:327 */       this.m_history.setSingle(name);
/* 253:    */     }
/* 254:330 */     if (this.m_headlessEvents != null) {
/* 255:331 */       this.m_headlessEvents.add(e);
/* 256:    */     }
/* 257:335 */     notifyTextListeners(e);
/* 258:    */   }
/* 259:    */   
/* 260:    */   public List<EventObject> retrieveHeadlessEvents()
/* 261:    */   {
/* 262:346 */     return this.m_headlessEvents;
/* 263:    */   }
/* 264:    */   
/* 265:    */   public void processHeadlessEvents(List<EventObject> headless)
/* 266:    */   {
/* 267:358 */     if (!GraphicsEnvironment.isHeadless()) {
/* 268:359 */       for (EventObject e : headless) {
/* 269:360 */         if ((e instanceof TextEvent)) {
/* 270:361 */           acceptText((TextEvent)e);
/* 271:    */         }
/* 272:    */       }
/* 273:    */     }
/* 274:    */   }
/* 275:    */   
/* 276:    */   public void setVisual(BeanVisual newVisual)
/* 277:    */   {
/* 278:374 */     this.m_visual = newVisual;
/* 279:    */   }
/* 280:    */   
/* 281:    */   public BeanVisual getVisual()
/* 282:    */   {
/* 283:382 */     return this.m_visual;
/* 284:    */   }
/* 285:    */   
/* 286:    */   public void useDefaultVisual()
/* 287:    */   {
/* 288:390 */     this.m_visual.loadIcons("weka/gui/beans/icons/DefaultText.gif", "weka/gui/beans/icons/DefaultText_animated.gif");
/* 289:    */   }
/* 290:    */   
/* 291:    */   public void showResults()
/* 292:    */   {
/* 293:398 */     if (this.m_resultsFrame == null)
/* 294:    */     {
/* 295:399 */       if (this.m_outText == null) {
/* 296:400 */         setUpResultHistory();
/* 297:    */       }
/* 298:402 */       this.m_resultsFrame = new JFrame("Text Viewer");
/* 299:403 */       this.m_resultsFrame.getContentPane().setLayout(new BorderLayout());
/* 300:404 */       JScrollPane js = new JScrollPane(this.m_outText);
/* 301:405 */       js.setBorder(BorderFactory.createTitledBorder("Text"));
/* 302:    */       
/* 303:407 */       JSplitPane p2 = new JSplitPane(1, this.m_history, js);
/* 304:    */       
/* 305:409 */       this.m_resultsFrame.getContentPane().add(p2, "Center");
/* 306:    */       
/* 307:    */ 
/* 308:412 */       this.m_resultsFrame.addWindowListener(new WindowAdapter()
/* 309:    */       {
/* 310:    */         public void windowClosing(WindowEvent e)
/* 311:    */         {
/* 312:415 */           TextViewer.this.m_resultsFrame.dispose();
/* 313:416 */           TextViewer.this.m_resultsFrame = null;
/* 314:    */         }
/* 315:418 */       });
/* 316:419 */       this.m_resultsFrame.pack();
/* 317:420 */       this.m_resultsFrame.setVisible(true);
/* 318:    */     }
/* 319:    */     else
/* 320:    */     {
/* 321:422 */       this.m_resultsFrame.toFront();
/* 322:    */     }
/* 323:    */   }
/* 324:    */   
/* 325:    */   public Enumeration<String> enumerateRequests()
/* 326:    */   {
/* 327:433 */     Vector<String> newVector = new Vector(0);
/* 328:    */     
/* 329:435 */     newVector.addElement("Show results");
/* 330:    */     
/* 331:437 */     newVector.addElement("?Clear results");
/* 332:438 */     return newVector.elements();
/* 333:    */   }
/* 334:    */   
/* 335:    */   public void performRequest(String request)
/* 336:    */   {
/* 337:449 */     if (request.compareTo("Show results") == 0)
/* 338:    */     {
/* 339:450 */       showResults();
/* 340:    */     }
/* 341:451 */     else if (request.compareTo("Clear results") == 0)
/* 342:    */     {
/* 343:452 */       this.m_outText.setText("");
/* 344:453 */       this.m_history.clearResults();
/* 345:    */     }
/* 346:    */     else
/* 347:    */     {
/* 348:455 */       throw new IllegalArgumentException(request + " not supported (TextViewer)");
/* 349:    */     }
/* 350:    */   }
/* 351:    */   
/* 352:    */   public void addPropertyChangeListener(String name, PropertyChangeListener pcl)
/* 353:    */   {
/* 354:468 */     this.m_bcSupport.addPropertyChangeListener(name, pcl);
/* 355:    */   }
/* 356:    */   
/* 357:    */   public void removePropertyChangeListener(String name, PropertyChangeListener pcl)
/* 358:    */   {
/* 359:480 */     this.m_bcSupport.removePropertyChangeListener(name, pcl);
/* 360:    */   }
/* 361:    */   
/* 362:    */   public void addVetoableChangeListener(String name, VetoableChangeListener vcl)
/* 363:    */   {
/* 364:491 */     this.m_bcSupport.addVetoableChangeListener(name, vcl);
/* 365:    */   }
/* 366:    */   
/* 367:    */   public void removeVetoableChangeListener(String name, VetoableChangeListener vcl)
/* 368:    */   {
/* 369:503 */     this.m_bcSupport.removeVetoableChangeListener(name, vcl);
/* 370:    */   }
/* 371:    */   
/* 372:    */   public void setBeanContext(BeanContext bc)
/* 373:    */   {
/* 374:513 */     this.m_beanContext = bc;
/* 375:514 */     this.m_design = this.m_beanContext.isDesignTime();
/* 376:515 */     if (this.m_design)
/* 377:    */     {
/* 378:516 */       appearanceDesign();
/* 379:    */     }
/* 380:    */     else
/* 381:    */     {
/* 382:518 */       GraphicsEnvironment.getLocalGraphicsEnvironment();
/* 383:519 */       if (!GraphicsEnvironment.isHeadless()) {
/* 384:520 */         appearanceFinal();
/* 385:    */       }
/* 386:    */     }
/* 387:    */   }
/* 388:    */   
/* 389:    */   private void notifyTextListeners(TextEvent ge)
/* 390:    */   {
/* 391:    */     Vector<TextListener> l;
/* 392:533 */     synchronized (this)
/* 393:    */     {
/* 394:534 */       l = (Vector)this.m_textListeners.clone();
/* 395:    */     }
/* 396:536 */     if (l.size() > 0) {
/* 397:537 */       for (int i = 0; i < l.size(); i++) {
/* 398:538 */         ((TextListener)l.elementAt(i)).acceptText(ge);
/* 399:    */       }
/* 400:    */     }
/* 401:    */   }
/* 402:    */   
/* 403:    */   public BeanContext getBeanContext()
/* 404:    */   {
/* 405:550 */     return this.m_beanContext;
/* 406:    */   }
/* 407:    */   
/* 408:    */   public void stop() {}
/* 409:    */   
/* 410:    */   public boolean isBusy()
/* 411:    */   {
/* 412:568 */     return false;
/* 413:    */   }
/* 414:    */   
/* 415:    */   public void setLog(Logger logger) {}
/* 416:    */   
/* 417:    */   public void setCustomName(String name)
/* 418:    */   {
/* 419:587 */     this.m_visual.setText(name);
/* 420:    */   }
/* 421:    */   
/* 422:    */   public String getCustomName()
/* 423:    */   {
/* 424:597 */     return this.m_visual.getText();
/* 425:    */   }
/* 426:    */   
/* 427:    */   public boolean connectionAllowed(EventSetDescriptor esd)
/* 428:    */   {
/* 429:609 */     return connectionAllowed(esd.getName());
/* 430:    */   }
/* 431:    */   
/* 432:    */   public boolean connectionAllowed(String eventName)
/* 433:    */   {
/* 434:621 */     return true;
/* 435:    */   }
/* 436:    */   
/* 437:    */   public void connectionNotification(String eventName, Object source) {}
/* 438:    */   
/* 439:    */   public void disconnectionNotification(String eventName, Object source) {}
/* 440:    */   
/* 441:    */   public boolean eventGeneratable(String eventName)
/* 442:    */   {
/* 443:659 */     if (eventName.equals("text")) {
/* 444:660 */       return true;
/* 445:    */     }
/* 446:662 */     return false;
/* 447:    */   }
/* 448:    */   
/* 449:    */   public synchronized void addTextListener(TextListener cl)
/* 450:    */   {
/* 451:671 */     this.m_textListeners.addElement(cl);
/* 452:    */   }
/* 453:    */   
/* 454:    */   public synchronized void removeTextListener(TextListener cl)
/* 455:    */   {
/* 456:680 */     this.m_textListeners.remove(cl);
/* 457:    */   }
/* 458:    */   
/* 459:    */   public static void main(String[] args)
/* 460:    */   {
/* 461:    */     try
/* 462:    */     {
/* 463:685 */       JFrame jf = new JFrame();
/* 464:686 */       jf.getContentPane().setLayout(new BorderLayout());
/* 465:    */       
/* 466:688 */       TextViewer tv = new TextViewer();
/* 467:    */       
/* 468:690 */       tv.acceptText(new TextEvent(tv, "Here is some test text from the main method of this class.", "The Title"));
/* 469:    */       
/* 470:692 */       jf.getContentPane().add(tv, "Center");
/* 471:693 */       jf.addWindowListener(new WindowAdapter()
/* 472:    */       {
/* 473:    */         public void windowClosing(WindowEvent e)
/* 474:    */         {
/* 475:696 */           this.val$jf.dispose();
/* 476:697 */           System.exit(0);
/* 477:    */         }
/* 478:699 */       });
/* 479:700 */       jf.setSize(800, 600);
/* 480:701 */       jf.setVisible(true);
/* 481:    */     }
/* 482:    */     catch (Exception ex)
/* 483:    */     {
/* 484:703 */       ex.printStackTrace();
/* 485:    */     }
/* 486:    */   }
/* 487:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.TextViewer
 * JD-Core Version:    0.7.0.1
 */