/*   1:    */ package weka.gui.explorer;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Color;
/*   5:    */ import java.awt.Container;
/*   6:    */ import java.awt.Dimension;
/*   7:    */ import java.awt.Font;
/*   8:    */ import java.awt.GridBagConstraints;
/*   9:    */ import java.awt.GridBagLayout;
/*  10:    */ import java.awt.GridLayout;
/*  11:    */ import java.awt.Point;
/*  12:    */ import java.awt.event.ActionEvent;
/*  13:    */ import java.awt.event.ActionListener;
/*  14:    */ import java.awt.event.MouseAdapter;
/*  15:    */ import java.awt.event.MouseEvent;
/*  16:    */ import java.awt.event.WindowAdapter;
/*  17:    */ import java.awt.event.WindowEvent;
/*  18:    */ import java.beans.PropertyChangeEvent;
/*  19:    */ import java.beans.PropertyChangeListener;
/*  20:    */ import java.io.BufferedReader;
/*  21:    */ import java.io.FileReader;
/*  22:    */ import java.io.PrintStream;
/*  23:    */ import java.io.Reader;
/*  24:    */ import java.text.SimpleDateFormat;
/*  25:    */ import java.util.Date;
/*  26:    */ import java.util.Map;
/*  27:    */ import java.util.Vector;
/*  28:    */ import javax.swing.BorderFactory;
/*  29:    */ import javax.swing.JButton;
/*  30:    */ import javax.swing.JCheckBox;
/*  31:    */ import javax.swing.JFrame;
/*  32:    */ import javax.swing.JList;
/*  33:    */ import javax.swing.JMenu;
/*  34:    */ import javax.swing.JMenuItem;
/*  35:    */ import javax.swing.JPanel;
/*  36:    */ import javax.swing.JPopupMenu;
/*  37:    */ import javax.swing.JScrollPane;
/*  38:    */ import javax.swing.JTextArea;
/*  39:    */ import javax.swing.JViewport;
/*  40:    */ import javax.swing.event.ChangeEvent;
/*  41:    */ import javax.swing.event.ChangeListener;
/*  42:    */ import weka.associations.AssociationRules;
/*  43:    */ import weka.associations.AssociationRulesProducer;
/*  44:    */ import weka.associations.Associator;
/*  45:    */ import weka.associations.FPGrowth;
/*  46:    */ import weka.core.Attribute;
/*  47:    */ import weka.core.Capabilities;
/*  48:    */ import weka.core.CapabilitiesHandler;
/*  49:    */ import weka.core.Defaults;
/*  50:    */ import weka.core.Drawable;
/*  51:    */ import weka.core.Environment;
/*  52:    */ import weka.core.Instances;
/*  53:    */ import weka.core.Memory;
/*  54:    */ import weka.core.OptionHandler;
/*  55:    */ import weka.core.Settings;
/*  56:    */ import weka.core.Settings.SettingKey;
/*  57:    */ import weka.core.Utils;
/*  58:    */ import weka.gui.AbstractPerspective;
/*  59:    */ import weka.gui.GUIApplication;
/*  60:    */ import weka.gui.GenericObjectEditor;
/*  61:    */ import weka.gui.LogPanel;
/*  62:    */ import weka.gui.Logger;
/*  63:    */ import weka.gui.PerspectiveInfo;
/*  64:    */ import weka.gui.PropertyPanel;
/*  65:    */ import weka.gui.ResultHistoryPanel;
/*  66:    */ import weka.gui.SaveBuffer;
/*  67:    */ import weka.gui.SysErrLog;
/*  68:    */ import weka.gui.TaskLogger;
/*  69:    */ import weka.gui.treevisualizer.PlaceNode2;
/*  70:    */ import weka.gui.treevisualizer.TreeVisualizer;
/*  71:    */ import weka.gui.visualize.plugins.AssociationRuleVisualizePlugin;
/*  72:    */ import weka.gui.visualize.plugins.TreeVisualizePlugin;
/*  73:    */ 
/*  74:    */ @PerspectiveInfo(ID="weka.gui.explorer.associationspanel", title="Associate", toolTipText="Discover association rules", iconPath="weka/gui/weka_icon_new_small.png")
/*  75:    */ public class AssociationsPanel
/*  76:    */   extends AbstractPerspective
/*  77:    */   implements Explorer.CapabilitiesFilterChangeListener, Explorer.ExplorerPanel, Explorer.LogHandler
/*  78:    */ {
/*  79:    */   static final long serialVersionUID = -6867871711865476971L;
/*  80:103 */   protected Explorer m_Explorer = null;
/*  81:106 */   protected GenericObjectEditor m_AssociatorEditor = new GenericObjectEditor();
/*  82:109 */   protected PropertyPanel m_CEPanel = new PropertyPanel(this.m_AssociatorEditor);
/*  83:112 */   protected JTextArea m_OutText = new JTextArea(20, 40);
/*  84:115 */   protected Logger m_Log = new SysErrLog();
/*  85:118 */   protected SaveBuffer m_SaveOut = new SaveBuffer(this.m_Log, this);
/*  86:121 */   protected ResultHistoryPanel m_History = new ResultHistoryPanel(this.m_OutText);
/*  87:124 */   protected JButton m_StartBut = new JButton("Start");
/*  88:127 */   protected JButton m_StopBut = new JButton("Stop");
/*  89:132 */   protected JCheckBox m_storeOutput = new JCheckBox("Store output for visualization");
/*  90:    */   protected Instances m_Instances;
/*  91:    */   protected Instances m_TestInstances;
/*  92:    */   protected Thread m_RunThread;
/*  93:    */   protected boolean m_initialSettingsSet;
/*  94:    */   
/*  95:    */   public AssociationsPanel()
/*  96:    */   {
/*  97:161 */     this.m_OutText.setEditable(false);
/*  98:162 */     this.m_OutText.setFont(new Font("Monospaced", 0, 12));
/*  99:163 */     this.m_OutText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/* 100:164 */     this.m_OutText.addMouseListener(new MouseAdapter()
/* 101:    */     {
/* 102:    */       public void mouseClicked(MouseEvent e)
/* 103:    */       {
/* 104:167 */         if ((e.getModifiers() & 0x10) != 16) {
/* 105:168 */           AssociationsPanel.this.m_OutText.selectAll();
/* 106:    */         }
/* 107:    */       }
/* 108:171 */     });
/* 109:172 */     JPanel historyHolder = new JPanel(new BorderLayout());
/* 110:173 */     historyHolder.setBorder(BorderFactory.createTitledBorder("Result list (right-click for options)"));
/* 111:    */     
/* 112:175 */     historyHolder.add(this.m_History, "Center");
/* 113:176 */     this.m_History.setHandleRightClicks(false);
/* 114:    */     
/* 115:178 */     this.m_History.getList().addMouseListener(new MouseAdapter()
/* 116:    */     {
/* 117:    */       public void mouseClicked(MouseEvent e)
/* 118:    */       {
/* 119:181 */         if (((e.getModifiers() & 0x10) != 16) || (e.isAltDown()))
/* 120:    */         {
/* 121:183 */           int index = AssociationsPanel.this.m_History.getList().locationToIndex(e.getPoint());
/* 122:184 */           if (index != -1)
/* 123:    */           {
/* 124:185 */             String name = AssociationsPanel.this.m_History.getNameAtIndex(index);
/* 125:186 */             AssociationsPanel.this.historyRightClickPopup(name, e.getX(), e.getY());
/* 126:    */           }
/* 127:    */           else
/* 128:    */           {
/* 129:188 */             AssociationsPanel.this.historyRightClickPopup(null, e.getX(), e.getY());
/* 130:    */           }
/* 131:    */         }
/* 132:    */       }
/* 133:193 */     });
/* 134:194 */     this.m_AssociatorEditor.setClassType(Associator.class);
/* 135:195 */     this.m_AssociatorEditor.setValue(ExplorerDefaults.getAssociator());
/* 136:196 */     this.m_AssociatorEditor.addPropertyChangeListener(new PropertyChangeListener()
/* 137:    */     {
/* 138:    */       public void propertyChange(PropertyChangeEvent e)
/* 139:    */       {
/* 140:199 */         AssociationsPanel.this.m_StartBut.setEnabled(true);
/* 141:    */         
/* 142:201 */         Capabilities currentFilter = AssociationsPanel.this.m_AssociatorEditor.getCapabilitiesFilter();
/* 143:202 */         Associator associator = (Associator)AssociationsPanel.this.m_AssociatorEditor.getValue();
/* 144:203 */         Capabilities currentSchemeCapabilities = null;
/* 145:204 */         if ((associator != null) && (currentFilter != null) && ((associator instanceof CapabilitiesHandler)))
/* 146:    */         {
/* 147:206 */           currentSchemeCapabilities = ((CapabilitiesHandler)associator).getCapabilities();
/* 148:209 */           if ((!currentSchemeCapabilities.supportsMaybe(currentFilter)) && (!currentSchemeCapabilities.supports(currentFilter))) {
/* 149:211 */             AssociationsPanel.this.m_StartBut.setEnabled(false);
/* 150:    */           }
/* 151:    */         }
/* 152:214 */         AssociationsPanel.this.repaint();
/* 153:    */       }
/* 154:217 */     });
/* 155:218 */     this.m_StartBut.setToolTipText("Starts the associator");
/* 156:219 */     this.m_StopBut.setToolTipText("Stops the associator");
/* 157:220 */     this.m_StartBut.setEnabled(false);
/* 158:221 */     this.m_StopBut.setEnabled(false);
/* 159:222 */     this.m_StartBut.addActionListener(new ActionListener()
/* 160:    */     {
/* 161:    */       public void actionPerformed(ActionEvent e)
/* 162:    */       {
/* 163:225 */         boolean proceed = true;
/* 164:226 */         if (Explorer.m_Memory.memoryIsLow()) {
/* 165:227 */           proceed = Explorer.m_Memory.showMemoryIsLow();
/* 166:    */         }
/* 167:230 */         if (proceed) {
/* 168:231 */           AssociationsPanel.this.startAssociator();
/* 169:    */         }
/* 170:    */       }
/* 171:234 */     });
/* 172:235 */     this.m_StopBut.addActionListener(new ActionListener()
/* 173:    */     {
/* 174:    */       public void actionPerformed(ActionEvent e)
/* 175:    */       {
/* 176:238 */         AssociationsPanel.this.stopAssociator();
/* 177:    */       }
/* 178:243 */     });
/* 179:244 */     boolean showStoreOutput = (GenericObjectEditor.getClassnames(AssociationRuleVisualizePlugin.class.getName()).size() > 0) || (GenericObjectEditor.getClassnames(TreeVisualizePlugin.class.getName()).size() > 0);
/* 180:    */     
/* 181:    */ 
/* 182:    */ 
/* 183:    */ 
/* 184:    */ 
/* 185:250 */     JPanel p1 = new JPanel();
/* 186:251 */     p1.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Associator"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
/* 187:    */     
/* 188:    */ 
/* 189:254 */     p1.setLayout(new BorderLayout());
/* 190:255 */     p1.add(this.m_CEPanel, "North");
/* 191:    */     
/* 192:257 */     JPanel buttons = new JPanel();
/* 193:258 */     buttons.setLayout(new BorderLayout());
/* 194:259 */     JPanel buttonsP = new JPanel();
/* 195:260 */     buttonsP.setLayout(new GridLayout(1, 2));
/* 196:    */     
/* 197:262 */     JPanel ssButs = new JPanel();
/* 198:263 */     ssButs.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/* 199:264 */     ssButs.setLayout(new GridLayout(1, 2, 5, 5));
/* 200:265 */     ssButs.add(this.m_StartBut);
/* 201:266 */     ssButs.add(this.m_StopBut);
/* 202:267 */     buttonsP.add(ssButs);
/* 203:268 */     buttons.add(buttonsP, "South");
/* 204:269 */     if (showStoreOutput) {
/* 205:270 */       buttons.add(this.m_storeOutput, "North");
/* 206:    */     }
/* 207:273 */     JPanel p3 = new JPanel();
/* 208:274 */     p3.setBorder(BorderFactory.createTitledBorder("Associator output"));
/* 209:275 */     p3.setLayout(new BorderLayout());
/* 210:276 */     JScrollPane js = new JScrollPane(this.m_OutText);
/* 211:277 */     p3.add(js, "Center");
/* 212:278 */     js.getViewport().addChangeListener(new ChangeListener()
/* 213:    */     {
/* 214:    */       private int lastHeight;
/* 215:    */       
/* 216:    */       public void stateChanged(ChangeEvent e)
/* 217:    */       {
/* 218:283 */         JViewport vp = (JViewport)e.getSource();
/* 219:284 */         int h = vp.getViewSize().height;
/* 220:285 */         if (h != this.lastHeight)
/* 221:    */         {
/* 222:286 */           this.lastHeight = h;
/* 223:287 */           int x = h - vp.getExtentSize().height;
/* 224:288 */           vp.setViewPosition(new Point(0, x));
/* 225:    */         }
/* 226:    */       }
/* 227:292 */     });
/* 228:293 */     GridBagLayout gbL = new GridBagLayout();
/* 229:294 */     GridBagConstraints gbC = new GridBagConstraints();
/* 230:295 */     JPanel mondo = new JPanel();
/* 231:296 */     gbL = new GridBagLayout();
/* 232:297 */     mondo.setLayout(gbL);
/* 233:298 */     gbC = new GridBagConstraints();
/* 234:299 */     gbC.anchor = 11;
/* 235:300 */     gbC.fill = 2;
/* 236:301 */     gbC.gridy = 1;
/* 237:302 */     gbC.gridx = 0;
/* 238:303 */     gbL.setConstraints(buttons, gbC);
/* 239:304 */     mondo.add(buttons);
/* 240:305 */     gbC = new GridBagConstraints();
/* 241:306 */     gbC.fill = 1;
/* 242:307 */     gbC.gridy = 2;
/* 243:308 */     gbC.gridx = 0;
/* 244:309 */     gbC.weightx = 0.0D;
/* 245:310 */     gbL.setConstraints(historyHolder, gbC);
/* 246:311 */     mondo.add(historyHolder);
/* 247:312 */     gbC = new GridBagConstraints();
/* 248:313 */     gbC.fill = 1;
/* 249:314 */     gbC.gridy = 0;
/* 250:315 */     gbC.gridx = 1;
/* 251:316 */     gbC.gridheight = 3;
/* 252:317 */     gbC.weightx = 100.0D;
/* 253:318 */     gbC.weighty = 100.0D;
/* 254:319 */     gbL.setConstraints(p3, gbC);
/* 255:320 */     mondo.add(p3);
/* 256:    */     
/* 257:322 */     setLayout(new BorderLayout());
/* 258:323 */     add(p1, "North");
/* 259:324 */     add(mondo, "Center");
/* 260:    */   }
/* 261:    */   
/* 262:    */   public void setLog(Logger newLog)
/* 263:    */   {
/* 264:335 */     this.m_Log = newLog;
/* 265:    */   }
/* 266:    */   
/* 267:    */   public void setInstances(Instances inst)
/* 268:    */   {
/* 269:346 */     this.m_Instances = inst;
/* 270:347 */     String[] attribNames = new String[this.m_Instances.numAttributes()];
/* 271:348 */     for (int i = 0; i < attribNames.length; i++)
/* 272:    */     {
/* 273:349 */       String type = "(" + Attribute.typeToStringShort(this.m_Instances.attribute(i)) + ") ";
/* 274:    */       
/* 275:351 */       attribNames[i] = (type + this.m_Instances.attribute(i).name());
/* 276:    */     }
/* 277:353 */     this.m_StartBut.setEnabled(this.m_RunThread == null);
/* 278:354 */     this.m_StopBut.setEnabled(this.m_RunThread != null);
/* 279:    */   }
/* 280:    */   
/* 281:    */   protected void startAssociator()
/* 282:    */   {
/* 283:365 */     if (this.m_RunThread == null)
/* 284:    */     {
/* 285:366 */       this.m_StartBut.setEnabled(false);
/* 286:367 */       this.m_StopBut.setEnabled(true);
/* 287:368 */       this.m_RunThread = new Thread()
/* 288:    */       {
/* 289:    */         public void run()
/* 290:    */         {
/* 291:371 */           AssociationsPanel.this.m_CEPanel.addToHistory();
/* 292:    */           
/* 293:    */ 
/* 294:374 */           AssociationsPanel.this.m_Log.statusMessage("Setting up...");
/* 295:375 */           Instances inst = new Instances(AssociationsPanel.this.m_Instances);
/* 296:376 */           String grph = null;
/* 297:    */           
/* 298:378 */           AssociationRules rulesList = null;
/* 299:379 */           Associator associator = (Associator)AssociationsPanel.this.m_AssociatorEditor.getValue();
/* 300:380 */           StringBuffer outBuff = new StringBuffer();
/* 301:381 */           String name = new SimpleDateFormat("HH:mm:ss - ").format(new Date());
/* 302:    */           
/* 303:383 */           String cname = associator.getClass().getName();
/* 304:384 */           if (cname.startsWith("weka.associations.")) {
/* 305:385 */             name = name + cname.substring("weka.associations.".length());
/* 306:    */           } else {
/* 307:387 */             name = name + cname;
/* 308:    */           }
/* 309:389 */           String cmd = AssociationsPanel.this.m_AssociatorEditor.getValue().getClass().getName();
/* 310:390 */           if ((AssociationsPanel.this.m_AssociatorEditor.getValue() instanceof OptionHandler)) {
/* 311:391 */             cmd = cmd + " " + Utils.joinOptions(((OptionHandler)AssociationsPanel.this.m_AssociatorEditor.getValue()).getOptions());
/* 312:    */           }
/* 313:    */           try
/* 314:    */           {
/* 315:399 */             AssociationsPanel.this.m_Log.logMessage("Started " + cname);
/* 316:400 */             AssociationsPanel.this.m_Log.logMessage("Command: " + cmd);
/* 317:401 */             if ((AssociationsPanel.this.m_Log instanceof TaskLogger)) {
/* 318:402 */               ((TaskLogger)AssociationsPanel.this.m_Log).taskStarted();
/* 319:    */             }
/* 320:404 */             outBuff.append("=== Run information ===\n\n");
/* 321:405 */             outBuff.append("Scheme:       " + cname);
/* 322:406 */             if ((associator instanceof OptionHandler))
/* 323:    */             {
/* 324:407 */               String[] o = ((OptionHandler)associator).getOptions();
/* 325:408 */               outBuff.append(" " + Utils.joinOptions(o));
/* 326:    */             }
/* 327:410 */             outBuff.append("\n");
/* 328:411 */             outBuff.append("Relation:     " + inst.relationName() + '\n');
/* 329:412 */             outBuff.append("Instances:    " + inst.numInstances() + '\n');
/* 330:413 */             outBuff.append("Attributes:   " + inst.numAttributes() + '\n');
/* 331:414 */             if (inst.numAttributes() < 100) {
/* 332:415 */               for (int i = 0; i < inst.numAttributes(); i++) {
/* 333:416 */                 outBuff.append("              " + inst.attribute(i).name() + '\n');
/* 334:    */               }
/* 335:    */             } else {
/* 336:420 */               outBuff.append("              [list of attributes omitted]\n");
/* 337:    */             }
/* 338:422 */             AssociationsPanel.this.m_History.addResult(name, outBuff);
/* 339:423 */             AssociationsPanel.this.m_History.setSingle(name);
/* 340:    */             
/* 341:    */ 
/* 342:426 */             AssociationsPanel.this.m_Log.statusMessage("Building model on training data...");
/* 343:427 */             associator.buildAssociations(inst);
/* 344:428 */             outBuff.append("=== Associator model (full training set) ===\n\n");
/* 345:429 */             outBuff.append(associator.toString() + '\n');
/* 346:430 */             AssociationsPanel.this.m_History.updateResult(name);
/* 347:431 */             if (AssociationsPanel.this.m_storeOutput.isSelected())
/* 348:    */             {
/* 349:432 */               if ((associator instanceof Drawable))
/* 350:    */               {
/* 351:433 */                 grph = null;
/* 352:    */                 try
/* 353:    */                 {
/* 354:435 */                   grph = ((Drawable)associator).graph();
/* 355:    */                 }
/* 356:    */                 catch (Exception ex) {}
/* 357:    */               }
/* 358:440 */               if ((associator instanceof AssociationRulesProducer))
/* 359:    */               {
/* 360:442 */                 rulesList = null;
/* 361:    */                 try
/* 362:    */                 {
/* 363:446 */                   rulesList = ((AssociationRulesProducer)associator).getAssociationRules();
/* 364:    */                 }
/* 365:    */                 catch (Exception ex) {}
/* 366:    */               }
/* 367:    */             }
/* 368:453 */             AssociationsPanel.this.m_Log.logMessage("Finished " + cname);
/* 369:454 */             AssociationsPanel.this.m_Log.statusMessage("OK");
/* 370:    */           }
/* 371:    */           catch (Exception ex)
/* 372:    */           {
/* 373:    */             Vector<Object> visVect;
/* 374:    */             Associator configCopy;
/* 375:456 */             AssociationsPanel.this.m_Log.logMessage(ex.getMessage());
/* 376:457 */             AssociationsPanel.this.m_Log.statusMessage("See error log");
/* 377:    */           }
/* 378:    */           finally
/* 379:    */           {
/* 380:    */             Vector<Object> visVect;
/* 381:    */             Associator configCopy;
/* 382:459 */             Vector<Object> visVect = new Vector();
/* 383:    */             try
/* 384:    */             {
/* 385:466 */               Associator configCopy = (Associator)associator.getClass().newInstance();
/* 386:467 */               if ((configCopy instanceof OptionHandler)) {
/* 387:468 */                 ((OptionHandler)configCopy).setOptions(((OptionHandler)associator).getOptions());
/* 388:    */               }
/* 389:471 */               visVect.add(configCopy);
/* 390:    */             }
/* 391:    */             catch (Exception ex)
/* 392:    */             {
/* 393:473 */               ex.printStackTrace();
/* 394:    */               
/* 395:    */ 
/* 396:476 */               visVect.add(associator);
/* 397:    */             }
/* 398:479 */             if ((grph != null) || (rulesList != null))
/* 399:    */             {
/* 400:481 */               if (grph != null) {
/* 401:482 */                 visVect.add(grph);
/* 402:    */               }
/* 403:485 */               if (rulesList != null) {
/* 404:486 */                 visVect.add(rulesList);
/* 405:    */               }
/* 406:    */             }
/* 407:489 */             AssociationsPanel.this.m_History.addObject(name, visVect);
/* 408:490 */             if (isInterrupted())
/* 409:    */             {
/* 410:491 */               AssociationsPanel.this.m_Log.logMessage("Interrupted " + cname);
/* 411:492 */               AssociationsPanel.this.m_Log.statusMessage("See error log");
/* 412:    */             }
/* 413:494 */             AssociationsPanel.this.m_RunThread = null;
/* 414:495 */             AssociationsPanel.this.m_StartBut.setEnabled(true);
/* 415:496 */             AssociationsPanel.this.m_StopBut.setEnabled(false);
/* 416:497 */             if ((AssociationsPanel.this.m_Log instanceof TaskLogger)) {
/* 417:498 */               ((TaskLogger)AssociationsPanel.this.m_Log).taskFinished();
/* 418:    */             }
/* 419:    */           }
/* 420:    */         }
/* 421:502 */       };
/* 422:503 */       this.m_RunThread.setPriority(1);
/* 423:504 */       this.m_RunThread.start();
/* 424:    */     }
/* 425:    */   }
/* 426:    */   
/* 427:    */   protected void stopAssociator()
/* 428:    */   {
/* 429:514 */     if (this.m_RunThread != null)
/* 430:    */     {
/* 431:515 */       this.m_RunThread.interrupt();
/* 432:    */       
/* 433:    */ 
/* 434:518 */       this.m_RunThread.stop();
/* 435:    */     }
/* 436:    */   }
/* 437:    */   
/* 438:    */   protected void saveBuffer(String name)
/* 439:    */   {
/* 440:529 */     StringBuffer sb = this.m_History.getNamedBuffer(name);
/* 441:530 */     if ((sb != null) && 
/* 442:531 */       (this.m_SaveOut.save(sb))) {
/* 443:532 */       this.m_Log.logMessage("Save successful.");
/* 444:    */     }
/* 445:    */   }
/* 446:    */   
/* 447:    */   protected void visualizeTree(String dottyString, String treeName)
/* 448:    */   {
/* 449:545 */     final JFrame jf = new JFrame("Weka Classifier Tree Visualizer: " + treeName);
/* 450:    */     
/* 451:547 */     jf.setSize(500, 400);
/* 452:548 */     jf.getContentPane().setLayout(new BorderLayout());
/* 453:549 */     TreeVisualizer tv = new TreeVisualizer(null, dottyString, new PlaceNode2());
/* 454:550 */     jf.getContentPane().add(tv, "Center");
/* 455:551 */     jf.addWindowListener(new WindowAdapter()
/* 456:    */     {
/* 457:    */       public void windowClosing(WindowEvent e)
/* 458:    */       {
/* 459:554 */         jf.dispose();
/* 460:    */       }
/* 461:557 */     });
/* 462:558 */     jf.setVisible(true);
/* 463:559 */     tv.fitToScreen();
/* 464:    */   }
/* 465:    */   
/* 466:    */   protected void historyRightClickPopup(String name, int x, int y)
/* 467:    */   {
/* 468:572 */     final String selectedName = name;
/* 469:573 */     JPopupMenu resultListMenu = new JPopupMenu();
/* 470:    */     
/* 471:575 */     JMenuItem visMainBuffer = new JMenuItem("View in main window");
/* 472:576 */     if (selectedName != null) {
/* 473:577 */       visMainBuffer.addActionListener(new ActionListener()
/* 474:    */       {
/* 475:    */         public void actionPerformed(ActionEvent e)
/* 476:    */         {
/* 477:580 */           AssociationsPanel.this.m_History.setSingle(selectedName);
/* 478:    */         }
/* 479:    */       });
/* 480:    */     } else {
/* 481:584 */       visMainBuffer.setEnabled(false);
/* 482:    */     }
/* 483:586 */     resultListMenu.add(visMainBuffer);
/* 484:    */     
/* 485:588 */     JMenuItem visSepBuffer = new JMenuItem("View in separate window");
/* 486:589 */     if (selectedName != null) {
/* 487:590 */       visSepBuffer.addActionListener(new ActionListener()
/* 488:    */       {
/* 489:    */         public void actionPerformed(ActionEvent e)
/* 490:    */         {
/* 491:593 */           AssociationsPanel.this.m_History.openFrame(selectedName);
/* 492:    */         }
/* 493:    */       });
/* 494:    */     } else {
/* 495:597 */       visSepBuffer.setEnabled(false);
/* 496:    */     }
/* 497:599 */     resultListMenu.add(visSepBuffer);
/* 498:    */     
/* 499:601 */     JMenuItem saveOutput = new JMenuItem("Save result buffer");
/* 500:602 */     if (selectedName != null) {
/* 501:603 */       saveOutput.addActionListener(new ActionListener()
/* 502:    */       {
/* 503:    */         public void actionPerformed(ActionEvent e)
/* 504:    */         {
/* 505:606 */           AssociationsPanel.this.saveBuffer(selectedName);
/* 506:    */         }
/* 507:    */       });
/* 508:    */     } else {
/* 509:610 */       saveOutput.setEnabled(false);
/* 510:    */     }
/* 511:612 */     resultListMenu.add(saveOutput);
/* 512:    */     
/* 513:614 */     JMenuItem deleteOutput = new JMenuItem("Delete result buffer");
/* 514:615 */     if (selectedName != null) {
/* 515:616 */       deleteOutput.addActionListener(new ActionListener()
/* 516:    */       {
/* 517:    */         public void actionPerformed(ActionEvent e)
/* 518:    */         {
/* 519:619 */           AssociationsPanel.this.m_History.removeResult(selectedName);
/* 520:    */         }
/* 521:    */       });
/* 522:    */     } else {
/* 523:623 */       deleteOutput.setEnabled(false);
/* 524:    */     }
/* 525:625 */     resultListMenu.add(deleteOutput);
/* 526:    */     
/* 527:627 */     Vector<Object> visVect = null;
/* 528:628 */     if (selectedName != null) {
/* 529:629 */       visVect = (Vector)this.m_History.getNamedObject(selectedName);
/* 530:    */     }
/* 531:633 */     if (visVect != null)
/* 532:    */     {
/* 533:635 */       Associator temp_model = null;
/* 534:636 */       if ((visVect.get(0) instanceof Associator)) {
/* 535:637 */         temp_model = (Associator)visVect.get(0);
/* 536:    */       }
/* 537:640 */       final Associator model = temp_model;
/* 538:641 */       JMenuItem reApplyConfig = new JMenuItem("Re-apply this model's configuration");
/* 539:643 */       if (model != null) {
/* 540:644 */         reApplyConfig.addActionListener(new ActionListener()
/* 541:    */         {
/* 542:    */           public void actionPerformed(ActionEvent e)
/* 543:    */           {
/* 544:647 */             AssociationsPanel.this.m_AssociatorEditor.setValue(model);
/* 545:    */           }
/* 546:    */         });
/* 547:    */       } else {
/* 548:651 */         reApplyConfig.setEnabled(false);
/* 549:    */       }
/* 550:653 */       resultListMenu.add(reApplyConfig);
/* 551:    */     }
/* 552:657 */     JMenu visPlugins = new JMenu("Plugins");
/* 553:658 */     boolean availablePlugins = false;
/* 554:661 */     if (visVect != null) {
/* 555:662 */       for (Object o : visVect) {
/* 556:663 */         if ((o instanceof AssociationRules))
/* 557:    */         {
/* 558:664 */           Vector<String> pluginsVector = GenericObjectEditor.getClassnames(AssociationRuleVisualizePlugin.class.getName());
/* 559:667 */           for (int i = 0; i < pluginsVector.size(); i++)
/* 560:    */           {
/* 561:668 */             String className = (String)pluginsVector.elementAt(i);
/* 562:    */             try
/* 563:    */             {
/* 564:670 */               AssociationRuleVisualizePlugin plugin = (AssociationRuleVisualizePlugin)Class.forName(className).newInstance();
/* 565:673 */               if (plugin != null)
/* 566:    */               {
/* 567:676 */                 availablePlugins = true;
/* 568:677 */                 JMenuItem pluginMenuItem = plugin.getVisualizeMenuItem((AssociationRules)o, selectedName);
/* 569:679 */                 if (pluginMenuItem != null) {
/* 570:680 */                   visPlugins.add(pluginMenuItem);
/* 571:    */                 }
/* 572:    */               }
/* 573:    */             }
/* 574:    */             catch (Exception ex) {}
/* 575:    */           }
/* 576:    */         }
/* 577:686 */         else if ((o instanceof String))
/* 578:    */         {
/* 579:687 */           Vector<String> pluginsVector = GenericObjectEditor.getClassnames(TreeVisualizePlugin.class.getName());
/* 580:690 */           for (int i = 0; i < pluginsVector.size(); i++)
/* 581:    */           {
/* 582:691 */             String className = (String)pluginsVector.elementAt(i);
/* 583:    */             try
/* 584:    */             {
/* 585:693 */               TreeVisualizePlugin plugin = (TreeVisualizePlugin)Class.forName(className).newInstance();
/* 586:695 */               if (plugin != null)
/* 587:    */               {
/* 588:698 */                 availablePlugins = true;
/* 589:699 */                 JMenuItem pluginMenuItem = plugin.getVisualizeMenuItem((String)o, selectedName);
/* 590:702 */                 if (pluginMenuItem != null) {
/* 591:711 */                   visPlugins.add(pluginMenuItem);
/* 592:    */                 }
/* 593:    */               }
/* 594:    */             }
/* 595:    */             catch (Exception e) {}
/* 596:    */           }
/* 597:    */         }
/* 598:    */       }
/* 599:    */     }
/* 600:721 */     if (availablePlugins) {
/* 601:722 */       resultListMenu.add(visPlugins);
/* 602:    */     }
/* 603:725 */     resultListMenu.show(this.m_History.getList(), x, y);
/* 604:    */   }
/* 605:    */   
/* 606:    */   protected void updateCapabilitiesFilter(Capabilities filter)
/* 607:    */   {
/* 608:737 */     if (filter == null)
/* 609:    */     {
/* 610:738 */       this.m_AssociatorEditor.setCapabilitiesFilter(new Capabilities(null)); return;
/* 611:    */     }
/* 612:    */     Instances tempInst;
/* 613:    */     Instances tempInst;
/* 614:742 */     if (!ExplorerDefaults.getInitGenericObjectEditorFilter()) {
/* 615:743 */       tempInst = new Instances(this.m_Instances, 0);
/* 616:    */     } else {
/* 617:745 */       tempInst = new Instances(this.m_Instances);
/* 618:    */     }
/* 619:747 */     tempInst.setClassIndex(-1);
/* 620:    */     Capabilities filterClass;
/* 621:    */     try
/* 622:    */     {
/* 623:750 */       filterClass = Capabilities.forInstances(tempInst);
/* 624:    */     }
/* 625:    */     catch (Exception e)
/* 626:    */     {
/* 627:752 */       filterClass = new Capabilities(null);
/* 628:    */     }
/* 629:755 */     this.m_AssociatorEditor.setCapabilitiesFilter(filterClass);
/* 630:    */     
/* 631:757 */     this.m_StartBut.setEnabled(true);
/* 632:    */     
/* 633:759 */     Capabilities currentFilter = this.m_AssociatorEditor.getCapabilitiesFilter();
/* 634:760 */     Associator associator = (Associator)this.m_AssociatorEditor.getValue();
/* 635:761 */     Capabilities currentSchemeCapabilities = null;
/* 636:762 */     if ((associator != null) && (currentFilter != null) && ((associator instanceof CapabilitiesHandler)))
/* 637:    */     {
/* 638:764 */       currentSchemeCapabilities = ((CapabilitiesHandler)associator).getCapabilities();
/* 639:767 */       if ((!currentSchemeCapabilities.supportsMaybe(currentFilter)) && (!currentSchemeCapabilities.supports(currentFilter))) {
/* 640:769 */         this.m_StartBut.setEnabled(false);
/* 641:    */       }
/* 642:    */     }
/* 643:    */   }
/* 644:    */   
/* 645:    */   public void capabilitiesFilterChanged(Explorer.CapabilitiesFilterChangeEvent e)
/* 646:    */   {
/* 647:781 */     if (e.getFilter() == null) {
/* 648:782 */       updateCapabilitiesFilter(null);
/* 649:    */     } else {
/* 650:784 */       updateCapabilitiesFilter((Capabilities)e.getFilter().clone());
/* 651:    */     }
/* 652:    */   }
/* 653:    */   
/* 654:    */   public void setExplorer(Explorer parent)
/* 655:    */   {
/* 656:796 */     this.m_Explorer = parent;
/* 657:    */   }
/* 658:    */   
/* 659:    */   public Explorer getExplorer()
/* 660:    */   {
/* 661:806 */     return this.m_Explorer;
/* 662:    */   }
/* 663:    */   
/* 664:    */   public String getTabTitle()
/* 665:    */   {
/* 666:816 */     return "Associate";
/* 667:    */   }
/* 668:    */   
/* 669:    */   public String getTabTitleToolTip()
/* 670:    */   {
/* 671:826 */     return "Discover association rules";
/* 672:    */   }
/* 673:    */   
/* 674:    */   public boolean requiresLog()
/* 675:    */   {
/* 676:831 */     return true;
/* 677:    */   }
/* 678:    */   
/* 679:    */   public boolean acceptsInstances()
/* 680:    */   {
/* 681:836 */     return true;
/* 682:    */   }
/* 683:    */   
/* 684:    */   public Defaults getDefaultSettings()
/* 685:    */   {
/* 686:841 */     return new AssociationsPanelDefaults();
/* 687:    */   }
/* 688:    */   
/* 689:    */   public boolean okToBeActive()
/* 690:    */   {
/* 691:846 */     return this.m_Instances != null;
/* 692:    */   }
/* 693:    */   
/* 694:    */   public void setActive(boolean active)
/* 695:    */   {
/* 696:850 */     super.setActive(active);
/* 697:851 */     if (this.m_isActive) {
/* 698:853 */       settingsChanged();
/* 699:    */     }
/* 700:    */   }
/* 701:    */   
/* 702:    */   public void settingsChanged()
/* 703:    */   {
/* 704:859 */     if (getMainApplication() != null)
/* 705:    */     {
/* 706:860 */       if (!this.m_initialSettingsSet)
/* 707:    */       {
/* 708:861 */         this.m_initialSettingsSet = true;
/* 709:    */         
/* 710:863 */         Object initialA = getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), AssociationsPanelDefaults.ASSOCIATOR_KEY, AssociationsPanelDefaults.ASSOCIATOR, Environment.getSystemWide());
/* 711:    */         
/* 712:    */ 
/* 713:    */ 
/* 714:867 */         this.m_AssociatorEditor.setValue(initialA);
/* 715:    */       }
/* 716:870 */       Font outputFont = (Font)getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), AssociationsPanelDefaults.OUTPUT_FONT_KEY, AssociationsPanelDefaults.OUTPUT_FONT, Environment.getSystemWide());
/* 717:    */       
/* 718:    */ 
/* 719:    */ 
/* 720:874 */       this.m_OutText.setFont(outputFont);
/* 721:875 */       Color textColor = (Color)getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), AssociationsPanelDefaults.OUTPUT_TEXT_COLOR_KEY, AssociationsPanelDefaults.OUTPUT_TEXT_COLOR, Environment.getSystemWide());
/* 722:    */       
/* 723:    */ 
/* 724:    */ 
/* 725:    */ 
/* 726:880 */       this.m_OutText.setForeground(textColor);
/* 727:881 */       Color outputBackgroundColor = (Color)getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), AssociationsPanelDefaults.OUTPUT_BACKGROUND_COLOR_KEY, AssociationsPanelDefaults.OUTPUT_BACKGROUND_COLOR, Environment.getSystemWide());
/* 728:    */       
/* 729:    */ 
/* 730:    */ 
/* 731:    */ 
/* 732:    */ 
/* 733:887 */       this.m_OutText.setBackground(outputBackgroundColor);
/* 734:888 */       this.m_History.setBackground(outputBackgroundColor);
/* 735:    */     }
/* 736:    */   }
/* 737:    */   
/* 738:    */   protected static final class AssociationsPanelDefaults
/* 739:    */     extends Defaults
/* 740:    */   {
/* 741:    */     public static final String ID = "weka.gui.explorer.associationspanel";
/* 742:898 */     protected static final Settings.SettingKey ASSOCIATOR_KEY = new Settings.SettingKey("weka.gui.explorer.associationspanel.initialAssociator", "Initial associator", "On startup, set this associator as the default one");
/* 743:901 */     protected static final Associator ASSOCIATOR = new FPGrowth();
/* 744:903 */     protected static final Settings.SettingKey OUTPUT_FONT_KEY = new Settings.SettingKey("weka.gui.explorer.associationspanel.outputFont", "Font for text output", "Font to use in the output area");
/* 745:906 */     protected static final Font OUTPUT_FONT = new Font("Monospaced", 0, 12);
/* 746:909 */     protected static final Settings.SettingKey OUTPUT_TEXT_COLOR_KEY = new Settings.SettingKey("weka.gui.explorer.associationspanel.outputFontColor", "Output text color", "Color of output text");
/* 747:912 */     protected static final Color OUTPUT_TEXT_COLOR = Color.black;
/* 748:914 */     protected static final Settings.SettingKey OUTPUT_BACKGROUND_COLOR_KEY = new Settings.SettingKey("weka.gui.explorer.associationspanel.outputBackgroundColor", "Output background color", "Output background color");
/* 749:917 */     protected static final Color OUTPUT_BACKGROUND_COLOR = Color.white;
/* 750:    */     private static final long serialVersionUID = 1108450683775771792L;
/* 751:    */     
/* 752:    */     public AssociationsPanelDefaults()
/* 753:    */     {
/* 754:921 */       super();
/* 755:922 */       this.m_defaults.put(ASSOCIATOR_KEY, ASSOCIATOR);
/* 756:923 */       this.m_defaults.put(OUTPUT_FONT_KEY, OUTPUT_FONT);
/* 757:924 */       this.m_defaults.put(OUTPUT_TEXT_COLOR_KEY, OUTPUT_TEXT_COLOR);
/* 758:925 */       this.m_defaults.put(OUTPUT_BACKGROUND_COLOR_KEY, OUTPUT_BACKGROUND_COLOR);
/* 759:    */     }
/* 760:    */   }
/* 761:    */   
/* 762:    */   public static void main(String[] args)
/* 763:    */   {
/* 764:    */     try
/* 765:    */     {
/* 766:937 */       JFrame jf = new JFrame("Weka Explorer: Associator");
/* 767:    */       
/* 768:939 */       jf.getContentPane().setLayout(new BorderLayout());
/* 769:940 */       AssociationsPanel sp = new AssociationsPanel();
/* 770:941 */       jf.getContentPane().add(sp, "Center");
/* 771:942 */       LogPanel lp = new LogPanel();
/* 772:943 */       sp.setLog(lp);
/* 773:944 */       jf.getContentPane().add(lp, "South");
/* 774:945 */       jf.addWindowListener(new WindowAdapter()
/* 775:    */       {
/* 776:    */         public void windowClosing(WindowEvent e)
/* 777:    */         {
/* 778:948 */           this.val$jf.dispose();
/* 779:949 */           System.exit(0);
/* 780:    */         }
/* 781:951 */       });
/* 782:952 */       jf.pack();
/* 783:953 */       jf.setVisible(true);
/* 784:954 */       if (args.length == 1)
/* 785:    */       {
/* 786:955 */         System.err.println("Loading instances from " + args[0]);
/* 787:956 */         Reader r = new BufferedReader(new FileReader(args[0]));
/* 788:    */         
/* 789:958 */         Instances i = new Instances(r);
/* 790:959 */         sp.setInstances(i);
/* 791:    */       }
/* 792:    */     }
/* 793:    */     catch (Exception ex)
/* 794:    */     {
/* 795:962 */       ex.printStackTrace();
/* 796:963 */       System.err.println(ex.getMessage());
/* 797:    */     }
/* 798:    */   }
/* 799:    */   
/* 800:    */   static {}
/* 801:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.explorer.AssociationsPanel
 * JD-Core Version:    0.7.0.1
 */