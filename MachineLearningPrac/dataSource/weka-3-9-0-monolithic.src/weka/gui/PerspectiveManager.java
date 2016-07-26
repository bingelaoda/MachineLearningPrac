/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Component;
/*   5:    */ import java.awt.Frame;
/*   6:    */ import java.awt.event.ActionEvent;
/*   7:    */ import java.awt.event.ActionListener;
/*   8:    */ import java.io.IOException;
/*   9:    */ import java.io.Serializable;
/*  10:    */ import java.text.SimpleDateFormat;
/*  11:    */ import java.util.ArrayList;
/*  12:    */ import java.util.Date;
/*  13:    */ import java.util.HashMap;
/*  14:    */ import java.util.LinkedHashMap;
/*  15:    */ import java.util.LinkedHashSet;
/*  16:    */ import java.util.LinkedList;
/*  17:    */ import java.util.List;
/*  18:    */ import java.util.Map;
/*  19:    */ import java.util.Map.Entry;
/*  20:    */ import java.util.Set;
/*  21:    */ import javax.swing.BorderFactory;
/*  22:    */ import javax.swing.ButtonGroup;
/*  23:    */ import javax.swing.Icon;
/*  24:    */ import javax.swing.ImageIcon;
/*  25:    */ import javax.swing.JButton;
/*  26:    */ import javax.swing.JComponent;
/*  27:    */ import javax.swing.JFrame;
/*  28:    */ import javax.swing.JMenu;
/*  29:    */ import javax.swing.JMenuBar;
/*  30:    */ import javax.swing.JMenuItem;
/*  31:    */ import javax.swing.JPanel;
/*  32:    */ import javax.swing.JToggleButton;
/*  33:    */ import javax.swing.JToolBar;
/*  34:    */ import javax.swing.KeyStroke;
/*  35:    */ import weka.core.Copyright;
/*  36:    */ import weka.core.Defaults;
/*  37:    */ import weka.core.Environment;
/*  38:    */ import weka.core.PluginManager;
/*  39:    */ import weka.core.Settings;
/*  40:    */ import weka.core.Settings.SettingKey;
/*  41:    */ import weka.core.logging.Logger;
/*  42:    */ import weka.core.logging.Logger.Level;
/*  43:    */ import weka.gui.knowledgeflow.MainKFPerspectiveToolBar;
/*  44:    */ import weka.gui.knowledgeflow.StepVisual;
/*  45:    */ 
/*  46:    */ public class PerspectiveManager
/*  47:    */   extends JPanel
/*  48:    */ {
/*  49: 62 */   public static final String PERSPECTIVE_INTERFACE = Perspective.class.getCanonicalName();
/*  50: 66 */   public static final Settings.SettingKey VISIBLE_PERSPECTIVES_KEY = new Settings.SettingKey("perspective_manager.visible_perspectives", "Visible perspectives", "");
/*  51:    */   private static final long serialVersionUID = -6099806469970666208L;
/*  52: 73 */   protected JToolBar m_perspectiveToolBar = new JToolBar(0);
/*  53: 76 */   protected ButtonGroup m_perspectiveGroup = new ButtonGroup();
/*  54:    */   protected GUIApplication m_mainApp;
/*  55: 82 */   protected Map<String, Perspective> m_perspectiveCache = new LinkedHashMap();
/*  56: 86 */   protected Map<String, String> m_perspectiveNameLookup = new HashMap();
/*  57: 90 */   protected List<Perspective> m_perspectives = new ArrayList();
/*  58: 96 */   protected LinkedHashSet<String> m_visiblePerspectives = new LinkedHashSet();
/*  59:100 */   protected List<String> m_allowedPerspectiveClassPrefixes = new ArrayList();
/*  60:107 */   protected List<String> m_disallowedPerspectiveClassPrefixes = new ArrayList();
/*  61:    */   protected Perspective m_mainPerspective;
/*  62:    */   protected boolean m_configAndPerspectivesVisible;
/*  63:    */   protected JPanel m_configAndPerspectivesToolBar;
/*  64:122 */   protected JMenuBar m_appMenuBar = new JMenuBar();
/*  65:    */   protected JMenu m_programMenu;
/*  66:    */   protected JMenuItem m_togglePerspectivesToolBar;
/*  67:131 */   protected LogPanel m_LogPanel = new LogPanel(new WekaTaskMonitor());
/*  68:    */   protected boolean m_logVisible;
/*  69:    */   
/*  70:    */   public PerspectiveManager(GUIApplication mainApp, String... perspectivePrefixesToAllow)
/*  71:    */   {
/*  72:147 */     this(mainApp, perspectivePrefixesToAllow, new String[0]);
/*  73:    */   }
/*  74:    */   
/*  75:    */   public PerspectiveManager(GUIApplication mainApp, String[] perspectivePrefixesToAllow, String[] perspectivePrefixesToDisallow)
/*  76:    */   {
/*  77:169 */     if (perspectivePrefixesToAllow != null) {
/*  78:170 */       for (String prefix : perspectivePrefixesToAllow) {
/*  79:171 */         this.m_allowedPerspectiveClassPrefixes.add(prefix);
/*  80:    */       }
/*  81:    */     }
/*  82:175 */     if (perspectivePrefixesToDisallow != null) {
/*  83:176 */       for (String prefix : perspectivePrefixesToDisallow) {
/*  84:177 */         this.m_disallowedPerspectiveClassPrefixes.add(prefix);
/*  85:    */       }
/*  86:    */     }
/*  87:180 */     this.m_mainApp = mainApp;
/*  88:181 */     Settings settings = this.m_mainApp.getApplicationSettings();
/*  89:    */     
/*  90:183 */     this.m_mainPerspective = this.m_mainApp.getMainPerspective();
/*  91:    */     
/*  92:185 */     settings.applyDefaults(this.m_mainPerspective.getDefaultSettings());
/*  93:    */     
/*  94:187 */     setLayout(new BorderLayout());
/*  95:188 */     this.m_configAndPerspectivesToolBar = new JPanel();
/*  96:189 */     this.m_configAndPerspectivesToolBar.setLayout(new BorderLayout());
/*  97:190 */     this.m_perspectiveToolBar.setLayout(new WrapLayout(0, 0, 0));
/*  98:    */     
/*  99:192 */     this.m_configAndPerspectivesToolBar.add(this.m_perspectiveToolBar, "Center");
/* 100:    */     
/* 101:    */ 
/* 102:195 */     this.m_mainPerspective.setMainApplication(this.m_mainApp);
/* 103:196 */     this.m_perspectives.add(this.m_mainPerspective);
/* 104:197 */     initPerspectivesCache(settings);
/* 105:198 */     initVisiblePerspectives(settings);
/* 106:    */     
/* 107:200 */     add((JComponent)this.m_mainPerspective, "Center");
/* 108:    */     
/* 109:    */ 
/* 110:203 */     this.m_programMenu = initProgramMenu();
/* 111:    */     
/* 112:    */ 
/* 113:206 */     String titleM = this.m_mainPerspective.getPerspectiveTitle();
/* 114:207 */     Icon icon = this.m_mainPerspective.getPerspectiveIcon();
/* 115:208 */     JToggleButton tBut = new JToggleButton(titleM, icon, true);
/* 116:209 */     tBut.setToolTipText(this.m_mainPerspective.getPerspectiveTipText());
/* 117:210 */     tBut.addActionListener(new ActionListener()
/* 118:    */     {
/* 119:    */       public void actionPerformed(ActionEvent e)
/* 120:    */       {
/* 121:213 */         Component[] comps = PerspectiveManager.this.getComponents();
/* 122:214 */         Perspective current = null;
/* 123:215 */         int pIndex = 0;
/* 124:216 */         for (int i = 0; i < comps.length; i++) {
/* 125:217 */           if ((comps[i] instanceof Perspective))
/* 126:    */           {
/* 127:218 */             pIndex = i;
/* 128:219 */             current = (Perspective)comps[i];
/* 129:220 */             break;
/* 130:    */           }
/* 131:    */         }
/* 132:223 */         if (current == PerspectiveManager.this.m_mainPerspective) {
/* 133:224 */           return;
/* 134:    */         }
/* 135:227 */         current.setActive(false);
/* 136:228 */         PerspectiveManager.this.remove(pIndex);
/* 137:229 */         PerspectiveManager.this.add((JComponent)PerspectiveManager.this.m_mainPerspective, "Center");
/* 138:230 */         ((Perspective)PerspectiveManager.this.m_perspectives.get(0)).setActive(true);
/* 139:    */         
/* 140:232 */         PerspectiveManager.this.m_appMenuBar.removeAll();
/* 141:233 */         PerspectiveManager.this.m_appMenuBar.add(PerspectiveManager.this.m_programMenu);
/* 142:234 */         List<JMenu> mainMenus = ((Perspective)PerspectiveManager.this.m_perspectives.get(0)).getMenus();
/* 143:235 */         for (JMenu m : mainMenus) {
/* 144:236 */           PerspectiveManager.this.m_appMenuBar.add(m);
/* 145:    */         }
/* 146:239 */         PerspectiveManager.this.m_mainApp.revalidate();
/* 147:    */       }
/* 148:241 */     });
/* 149:242 */     this.m_perspectiveToolBar.add(tBut);
/* 150:243 */     this.m_perspectiveGroup.add(tBut);
/* 151:    */     
/* 152:245 */     setupUserPerspectives();
/* 153:    */     
/* 154:247 */     initLogPanel(settings);
/* 155:248 */     if (this.m_mainPerspective.requiresLog())
/* 156:    */     {
/* 157:249 */       this.m_mainPerspective.setLog(this.m_LogPanel);
/* 158:250 */       add(this.m_LogPanel, "South");
/* 159:251 */       this.m_logVisible = true;
/* 160:    */     }
/* 161:255 */     this.m_mainPerspective.setActive(true);
/* 162:    */     
/* 163:    */ 
/* 164:258 */     this.m_mainApp.settingsChanged();
/* 165:    */   }
/* 166:    */   
/* 167:    */   protected void setLogSettings(Settings settings)
/* 168:    */   {
/* 169:267 */     int fontSize = ((Integer)settings.getSetting(this.m_mainApp.getApplicationID(), new Settings.SettingKey(this.m_mainApp.getApplicationID() + ".logMessageFontSize", "", ""), Integer.valueOf(-1))).intValue();
/* 170:    */     
/* 171:    */ 
/* 172:    */ 
/* 173:271 */     this.m_LogPanel.setLoggingFontSize(fontSize);
/* 174:    */   }
/* 175:    */   
/* 176:    */   protected void initLogPanel(Settings settings)
/* 177:    */   {
/* 178:278 */     setLogSettings(settings);
/* 179:279 */     String date = new SimpleDateFormat("EEEE, d MMMM yyyy").format(new Date());
/* 180:    */     
/* 181:281 */     this.m_LogPanel.logMessage("Weka " + this.m_mainApp.getApplicationName());
/* 182:282 */     this.m_LogPanel.logMessage("(c) " + Copyright.getFromYear() + "-" + Copyright.getToYear() + " " + Copyright.getOwner() + ", " + Copyright.getAddress());
/* 183:    */     
/* 184:    */ 
/* 185:285 */     this.m_LogPanel.logMessage("web: " + Copyright.getURL());
/* 186:286 */     this.m_LogPanel.logMessage("Started on " + date);
/* 187:287 */     this.m_LogPanel.statusMessage("Welcome to the Weka " + this.m_mainApp.getApplicationName());
/* 188:    */   }
/* 189:    */   
/* 190:    */   public void setMainApplicationForAllPerspectives()
/* 191:    */   {
/* 192:297 */     for (Map.Entry<String, Perspective> e : this.m_perspectiveCache.entrySet()) {
/* 193:298 */       ((Perspective)e.getValue()).setMainApplication(this.m_mainApp);
/* 194:    */     }
/* 195:300 */     for (Map.Entry<String, Perspective> e : this.m_perspectiveCache.entrySet())
/* 196:    */     {
/* 197:303 */       ((Perspective)e.getValue()).instantiationComplete();
/* 198:305 */       if (((Perspective)e.getValue()).requiresLog()) {
/* 199:306 */         ((Perspective)e.getValue()).setLog(this.m_LogPanel);
/* 200:    */       }
/* 201:    */     }
/* 202:310 */     this.m_mainPerspective.setMainApplication(this.m_mainApp);
/* 203:311 */     this.m_mainPerspective.instantiationComplete();
/* 204:    */   }
/* 205:    */   
/* 206:    */   protected void notifySettingsChanged()
/* 207:    */   {
/* 208:318 */     this.m_mainApp.settingsChanged();
/* 209:319 */     this.m_mainPerspective.settingsChanged();
/* 210:320 */     for (Map.Entry<String, Perspective> e : this.m_perspectiveCache.entrySet()) {
/* 211:321 */       ((Perspective)e.getValue()).settingsChanged();
/* 212:    */     }
/* 213:323 */     setLogSettings(this.m_mainApp.getApplicationSettings());
/* 214:    */   }
/* 215:    */   
/* 216:    */   protected JMenu initProgramMenu()
/* 217:    */   {
/* 218:332 */     JMenu programMenu = new JMenu();
/* 219:333 */     this.m_togglePerspectivesToolBar = new JMenuItem("Toggle perspectives toolbar");
/* 220:334 */     KeyStroke hideKey = KeyStroke.getKeyStroke(80, 512);
/* 221:    */     
/* 222:336 */     this.m_togglePerspectivesToolBar.setAccelerator(hideKey);
/* 223:337 */     this.m_togglePerspectivesToolBar.addActionListener(new ActionListener()
/* 224:    */     {
/* 225:    */       public void actionPerformed(ActionEvent e)
/* 226:    */       {
/* 227:340 */         if (PerspectiveManager.this.m_mainApp.isPerspectivesToolBarVisible()) {
/* 228:341 */           PerspectiveManager.this.m_mainApp.hidePerspectivesToolBar();
/* 229:    */         } else {
/* 230:343 */           PerspectiveManager.this.m_mainApp.showPerspectivesToolBar();
/* 231:    */         }
/* 232:345 */         PerspectiveManager.this.m_mainApp.revalidate();
/* 233:    */       }
/* 234:347 */     });
/* 235:348 */     programMenu.add(this.m_togglePerspectivesToolBar);
/* 236:    */     
/* 237:350 */     programMenu.setText("Program");
/* 238:351 */     JMenuItem exitItem = new JMenuItem("Exit");
/* 239:352 */     KeyStroke exitKey = KeyStroke.getKeyStroke(81, 128);
/* 240:    */     
/* 241:354 */     exitItem.setAccelerator(exitKey);
/* 242:355 */     exitItem.addActionListener(new ActionListener()
/* 243:    */     {
/* 244:    */       public void actionPerformed(ActionEvent e)
/* 245:    */       {
/* 246:358 */         ((Frame)((JComponent)PerspectiveManager.this.m_mainApp).getTopLevelAncestor()).dispose();
/* 247:359 */         System.exit(0);
/* 248:    */       }
/* 249:361 */     });
/* 250:362 */     programMenu.add(exitItem);
/* 251:    */     
/* 252:364 */     this.m_appMenuBar.add(programMenu);
/* 253:365 */     List<JMenu> mainMenus = this.m_mainPerspective.getMenus();
/* 254:366 */     for (JMenu m : mainMenus) {
/* 255:367 */       this.m_appMenuBar.add(m);
/* 256:    */     }
/* 257:370 */     return programMenu;
/* 258:    */   }
/* 259:    */   
/* 260:    */   public void setPerspectiveToolbarAlwaysHidden(Settings settings)
/* 261:    */   {
/* 262:380 */     SelectedPerspectivePreferences userVisiblePerspectives = (SelectedPerspectivePreferences)settings.getSetting(this.m_mainApp.getApplicationID(), VISIBLE_PERSPECTIVES_KEY, new SelectedPerspectivePreferences(), Environment.getSystemWide());
/* 263:    */     
/* 264:    */ 
/* 265:    */ 
/* 266:384 */     userVisiblePerspectives.setPerspectivesToolbarAlwaysHidden(true);
/* 267:385 */     setPerspectiveToolBarIsVisible(false);
/* 268:    */     
/* 269:387 */     this.m_programMenu.remove(this.m_togglePerspectivesToolBar);
/* 270:    */   }
/* 271:    */   
/* 272:    */   public void addSettingsMenuItemToProgramMenu(final Settings settings)
/* 273:    */   {
/* 274:399 */     if (settings != null)
/* 275:    */     {
/* 276:400 */       JMenuItem settingsM = new JMenuItem("Settings...");
/* 277:401 */       settingsM.addActionListener(new ActionListener()
/* 278:    */       {
/* 279:    */         public void actionPerformed(ActionEvent e)
/* 280:    */         {
/* 281:404 */           PerspectiveManager.this.popupSettingsDialog(settings);
/* 282:    */         }
/* 283:406 */       });
/* 284:407 */       this.m_programMenu.insert(settingsM, 0);
/* 285:    */     }
/* 286:410 */     JButton configB = new JButton(new ImageIcon(StepVisual.loadIcon(MainKFPerspectiveToolBar.ICON_PATH + "cog.png").getImage()));
/* 287:    */     
/* 288:    */ 
/* 289:413 */     configB.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 1));
/* 290:414 */     configB.setToolTipText("Settings");
/* 291:415 */     this.m_configAndPerspectivesToolBar.add(configB, "West");
/* 292:416 */     configB.addActionListener(new ActionListener()
/* 293:    */     {
/* 294:    */       public void actionPerformed(ActionEvent e)
/* 295:    */       {
/* 296:419 */         PerspectiveManager.this.popupSettingsDialog(settings);
/* 297:    */       }
/* 298:    */     });
/* 299:    */   }
/* 300:    */   
/* 301:    */   protected void popupSettingsDialog(Settings settings)
/* 302:    */   {
/* 303:431 */     SettingsEditor settingsEditor = new SettingsEditor(settings, this.m_mainApp);
/* 304:    */     try
/* 305:    */     {
/* 306:435 */       int result = SettingsEditor.showApplicationSettingsEditor(settings, this.m_mainApp);
/* 307:437 */       if (result == 0)
/* 308:    */       {
/* 309:438 */         initVisiblePerspectives(settings);
/* 310:439 */         setupUserPerspectives();
/* 311:440 */         notifySettingsChanged();
/* 312:    */       }
/* 313:    */     }
/* 314:    */     catch (IOException ex)
/* 315:    */     {
/* 316:443 */       this.m_mainApp.showErrorDialog(ex);
/* 317:    */     }
/* 318:    */   }
/* 319:    */   
/* 320:    */   protected void setupUserPerspectives()
/* 321:    */   {
/* 322:452 */     for (int i = this.m_perspectiveToolBar.getComponentCount() - 1; i > 0; i--)
/* 323:    */     {
/* 324:453 */       this.m_perspectiveToolBar.remove(i);
/* 325:454 */       this.m_perspectives.remove(i);
/* 326:    */     }
/* 327:457 */     int index = 1;
/* 328:458 */     for (String c : this.m_visiblePerspectives)
/* 329:    */     {
/* 330:459 */       String impl = (String)this.m_perspectiveNameLookup.get(c);
/* 331:460 */       Perspective toAdd = (Perspective)this.m_perspectiveCache.get(impl);
/* 332:461 */       if ((toAdd instanceof JComponent))
/* 333:    */       {
/* 334:462 */         toAdd.setLoaded(true);
/* 335:463 */         this.m_perspectives.add(toAdd);
/* 336:464 */         String titleM = toAdd.getPerspectiveTitle();
/* 337:465 */         Icon icon = toAdd.getPerspectiveIcon();
/* 338:466 */         JToggleButton tBut = null;
/* 339:467 */         if (icon != null) {
/* 340:468 */           tBut = new JToggleButton(titleM, icon, false);
/* 341:    */         } else {
/* 342:470 */           tBut = new JToggleButton(titleM, false);
/* 343:    */         }
/* 344:472 */         tBut.setToolTipText(toAdd.getPerspectiveTipText());
/* 345:473 */         final int theIndex = index;
/* 346:474 */         tBut.setEnabled(toAdd.okToBeActive());
/* 347:475 */         tBut.addActionListener(new ActionListener()
/* 348:    */         {
/* 349:    */           public void actionPerformed(ActionEvent e)
/* 350:    */           {
/* 351:478 */             PerspectiveManager.this.setActivePerspective(theIndex);
/* 352:    */           }
/* 353:480 */         });
/* 354:481 */         this.m_perspectiveToolBar.add(tBut);
/* 355:482 */         this.m_perspectiveGroup.add(tBut);
/* 356:483 */         index++;
/* 357:    */       }
/* 358:    */     }
/* 359:487 */     Component[] comps = getComponents();
/* 360:488 */     Perspective current = null;
/* 361:489 */     int pIndex = 0;
/* 362:490 */     for (int i = 0; i < comps.length; i++) {
/* 363:491 */       if ((comps[i] instanceof Perspective))
/* 364:    */       {
/* 365:492 */         pIndex = i;
/* 366:493 */         current = (Perspective)comps[i];
/* 367:494 */         break;
/* 368:    */       }
/* 369:    */     }
/* 370:497 */     if (current != this.m_mainPerspective) {
/* 371:498 */       setActivePerspective(0);
/* 372:    */     }
/* 373:501 */     this.m_mainApp.revalidate();
/* 374:    */   }
/* 375:    */   
/* 376:    */   public void setActivePerspective(int theIndex)
/* 377:    */   {
/* 378:510 */     if ((theIndex < 0) || (theIndex > this.m_perspectives.size() - 1)) {
/* 379:511 */       return;
/* 380:    */     }
/* 381:514 */     Component[] comps = getComponents();
/* 382:515 */     Perspective current = null;
/* 383:516 */     int pIndex = 0;
/* 384:517 */     for (int i = 0; i < comps.length; i++) {
/* 385:518 */       if ((comps[i] instanceof Perspective))
/* 386:    */       {
/* 387:519 */         pIndex = i;
/* 388:520 */         current = (Perspective)comps[i];
/* 389:521 */         break;
/* 390:    */       }
/* 391:    */     }
/* 392:524 */     current.setActive(false);
/* 393:525 */     remove(pIndex);
/* 394:526 */     add((JComponent)this.m_perspectives.get(theIndex), "Center");
/* 395:527 */     ((Perspective)this.m_perspectives.get(theIndex)).setActive(true);
/* 396:528 */     ((JToggleButton)this.m_perspectiveToolBar.getComponent(theIndex)).setSelected(true);
/* 397:531 */     if ((((Perspective)this.m_perspectives.get(theIndex)).requiresLog()) && (!this.m_logVisible))
/* 398:    */     {
/* 399:533 */       add(this.m_LogPanel, "South");
/* 400:534 */       this.m_logVisible = true;
/* 401:    */     }
/* 402:535 */     else if ((!((Perspective)this.m_perspectives.get(theIndex)).requiresLog()) && (this.m_logVisible))
/* 403:    */     {
/* 404:537 */       remove(this.m_LogPanel);
/* 405:538 */       this.m_logVisible = false;
/* 406:    */     }
/* 407:541 */     JMenu programMenu = this.m_appMenuBar.getMenu(0);
/* 408:542 */     this.m_appMenuBar.removeAll();
/* 409:543 */     this.m_appMenuBar.add(programMenu);
/* 410:544 */     List<JMenu> mainMenus = ((Perspective)this.m_perspectives.get(theIndex)).getMenus();
/* 411:545 */     if (mainMenus != null) {
/* 412:546 */       for (JMenu m : mainMenus) {
/* 413:547 */         this.m_appMenuBar.add(m);
/* 414:    */       }
/* 415:    */     }
/* 416:551 */     this.m_mainApp.revalidate();
/* 417:    */   }
/* 418:    */   
/* 419:    */   public void setActivePerspective(String perspectiveID)
/* 420:    */   {
/* 421:560 */     int index = -1;
/* 422:561 */     for (int i = 0; i < this.m_perspectives.size(); i++) {
/* 423:562 */       if (((Perspective)this.m_perspectives.get(i)).getPerspectiveID().equals(perspectiveID))
/* 424:    */       {
/* 425:563 */         index = i;
/* 426:564 */         break;
/* 427:    */       }
/* 428:    */     }
/* 429:568 */     if (index >= 0) {
/* 430:569 */       setActivePerspective(index);
/* 431:    */     }
/* 432:    */   }
/* 433:    */   
/* 434:    */   public List<Perspective> getLoadedPerspectives()
/* 435:    */   {
/* 436:581 */     List<Perspective> available = new ArrayList();
/* 437:582 */     for (Map.Entry<String, Perspective> e : this.m_perspectiveCache.entrySet()) {
/* 438:583 */       available.add(e.getValue());
/* 439:    */     }
/* 440:586 */     return available;
/* 441:    */   }
/* 442:    */   
/* 443:    */   public List<Perspective> getVisiblePerspectives()
/* 444:    */   {
/* 445:596 */     List<Perspective> visible = new ArrayList();
/* 446:598 */     for (String pName : this.m_visiblePerspectives)
/* 447:    */     {
/* 448:599 */       String impl = (String)this.m_perspectiveNameLookup.get(pName);
/* 449:600 */       Perspective p = (Perspective)this.m_perspectiveCache.get(impl);
/* 450:601 */       if (p != null) {
/* 451:602 */         visible.add(p);
/* 452:    */       }
/* 453:    */     }
/* 454:606 */     return visible;
/* 455:    */   }
/* 456:    */   
/* 457:    */   protected void initPerspectivesCache(Settings settings)
/* 458:    */   {
/* 459:616 */     Set<String> pluginPerspectiveImpls = PluginManager.getPluginNamesOfType(PERSPECTIVE_INTERFACE);
/* 460:619 */     if (pluginPerspectiveImpls != null) {
/* 461:620 */       for (String impl : pluginPerspectiveImpls) {
/* 462:621 */         if (!impl.equals(this.m_mainPerspective.getClass().getCanonicalName())) {
/* 463:    */           try
/* 464:    */           {
/* 465:623 */             Object perspective = PluginManager.getPluginInstance(PERSPECTIVE_INTERFACE, impl);
/* 466:626 */             if (!(perspective instanceof Perspective)) {
/* 467:627 */               Logger.log(Logger.Level.WARNING, "[PerspectiveManager] " + impl + " is not an instance" + PERSPECTIVE_INTERFACE + ". Skipping...");
/* 468:    */             }
/* 469:632 */             boolean ok = true;
/* 470:633 */             if (this.m_allowedPerspectiveClassPrefixes.size() > 0)
/* 471:    */             {
/* 472:634 */               ok = false;
/* 473:635 */               for (String prefix : this.m_allowedPerspectiveClassPrefixes) {
/* 474:636 */                 if (impl.startsWith(prefix))
/* 475:    */                 {
/* 476:637 */                   ok = true;
/* 477:638 */                   break;
/* 478:    */                 }
/* 479:    */               }
/* 480:    */             }
/* 481:643 */             if (this.m_disallowedPerspectiveClassPrefixes.size() > 0) {
/* 482:644 */               for (String prefix : this.m_disallowedPerspectiveClassPrefixes) {
/* 483:645 */                 if (impl.startsWith(prefix))
/* 484:    */                 {
/* 485:646 */                   ok = false;
/* 486:647 */                   break;
/* 487:    */                 }
/* 488:    */               }
/* 489:    */             }
/* 490:652 */             if (impl.equals(this.m_mainPerspective.getClass().getCanonicalName())) {
/* 491:656 */               ok = false;
/* 492:    */             }
/* 493:659 */             if (ok)
/* 494:    */             {
/* 495:660 */               this.m_perspectiveCache.put(impl, (Perspective)perspective);
/* 496:661 */               String perspectiveTitle = ((Perspective)perspective).getPerspectiveTitle();
/* 497:    */               
/* 498:663 */               this.m_perspectiveNameLookup.put(perspectiveTitle, impl);
/* 499:664 */               settings.applyDefaults(((Perspective)perspective).getDefaultSettings());
/* 500:    */             }
/* 501:    */           }
/* 502:    */           catch (Exception e)
/* 503:    */           {
/* 504:668 */             e.printStackTrace();
/* 505:669 */             this.m_mainApp.showErrorDialog(e);
/* 506:    */           }
/* 507:    */         }
/* 508:    */       }
/* 509:    */     }
/* 510:    */   }
/* 511:    */   
/* 512:    */   protected void initVisiblePerspectives(Settings settings)
/* 513:    */   {
/* 514:683 */     this.m_visiblePerspectives.clear();
/* 515:684 */     if (this.m_perspectiveCache.size() > 0)
/* 516:    */     {
/* 517:685 */       Map<Settings.SettingKey, Object> defaults = new LinkedHashMap();
/* 518:    */       
/* 519:    */ 
/* 520:688 */       SelectedPerspectivePreferences defaultEmpty = new SelectedPerspectivePreferences();
/* 521:    */       
/* 522:690 */       defaults.put(VISIBLE_PERSPECTIVES_KEY, defaultEmpty);
/* 523:    */       
/* 524:692 */       Defaults mainAppPerspectiveDefaults = new Defaults(this.m_mainApp.getApplicationID(), defaults);
/* 525:    */       
/* 526:    */ 
/* 527:695 */       settings.applyDefaults(mainAppPerspectiveDefaults);
/* 528:    */       
/* 529:697 */       SelectedPerspectivePreferences userVisiblePerspectives = (SelectedPerspectivePreferences)settings.getSetting(this.m_mainApp.getApplicationID(), VISIBLE_PERSPECTIVES_KEY, new SelectedPerspectivePreferences(), Environment.getSystemWide());
/* 530:702 */       if (userVisiblePerspectives == defaultEmpty)
/* 531:    */       {
/* 532:705 */         for (Map.Entry<String, Perspective> e : this.m_perspectiveCache.entrySet()) {
/* 533:706 */           userVisiblePerspectives.getUserVisiblePerspectives().add(((Perspective)e.getValue()).getPerspectiveTitle());
/* 534:    */         }
/* 535:709 */         userVisiblePerspectives.setPerspectivesToolbarVisibleOnStartup(true);
/* 536:    */       }
/* 537:712 */       for (String userVisPer : userVisiblePerspectives.getUserVisiblePerspectives()) {
/* 538:714 */         this.m_visiblePerspectives.add(userVisPer);
/* 539:    */       }
/* 540:    */     }
/* 541:    */   }
/* 542:    */   
/* 543:    */   public JPanel getPerspectiveToolBar()
/* 544:    */   {
/* 545:726 */     return this.m_configAndPerspectivesToolBar;
/* 546:    */   }
/* 547:    */   
/* 548:    */   public void disableAllPerspectiveTabs()
/* 549:    */   {
/* 550:733 */     for (int i = 0; i < this.m_perspectiveToolBar.getComponentCount(); i++) {
/* 551:734 */       this.m_perspectiveToolBar.getComponent(i).setEnabled(false);
/* 552:    */     }
/* 553:    */   }
/* 554:    */   
/* 555:    */   public void enableAllPerspectiveTabs()
/* 556:    */   {
/* 557:742 */     for (int i = 0; i < this.m_perspectiveToolBar.getComponentCount(); i++) {
/* 558:743 */       this.m_perspectiveToolBar.getComponent(i).setEnabled(true);
/* 559:    */     }
/* 560:    */   }
/* 561:    */   
/* 562:    */   public void setEnablePerspectiveTabs(List<String> perspectiveIDs, boolean enabled)
/* 563:    */   {
/* 564:756 */     for (int i = 0; i < this.m_perspectives.size(); i++)
/* 565:    */     {
/* 566:757 */       Perspective p = (Perspective)this.m_perspectives.get(i);
/* 567:758 */       if (perspectiveIDs.contains(p.getPerspectiveID())) {
/* 568:759 */         this.m_perspectiveToolBar.getComponent(i).setEnabled(enabled);
/* 569:    */       }
/* 570:    */     }
/* 571:    */   }
/* 572:    */   
/* 573:    */   public void setEnablePerspectiveTab(String perspectiveID, boolean enabled)
/* 574:    */   {
/* 575:771 */     for (int i = 0; i < this.m_perspectives.size(); i++)
/* 576:    */     {
/* 577:772 */       Perspective p = (Perspective)this.m_perspectives.get(i);
/* 578:773 */       if ((p.getPerspectiveID().equals(perspectiveID)) && (p.okToBeActive())) {
/* 579:774 */         this.m_perspectiveToolBar.getComponent(i).setEnabled(enabled);
/* 580:    */       }
/* 581:    */     }
/* 582:    */   }
/* 583:    */   
/* 584:    */   public boolean perspectiveToolBarIsVisible()
/* 585:    */   {
/* 586:785 */     return this.m_configAndPerspectivesVisible;
/* 587:    */   }
/* 588:    */   
/* 589:    */   public void setPerspectiveToolBarIsVisible(boolean v)
/* 590:    */   {
/* 591:789 */     this.m_configAndPerspectivesVisible = v;
/* 592:    */   }
/* 593:    */   
/* 594:    */   public Perspective getMainPerspective()
/* 595:    */   {
/* 596:800 */     return this.m_mainPerspective;
/* 597:    */   }
/* 598:    */   
/* 599:    */   public Perspective getPerspective(String ID)
/* 600:    */   {
/* 601:811 */     Perspective perspective = null;
/* 602:812 */     for (Perspective p : this.m_perspectives) {
/* 603:813 */       if (p.getPerspectiveID().equals(ID))
/* 604:    */       {
/* 605:814 */         perspective = p;
/* 606:815 */         break;
/* 607:    */       }
/* 608:    */     }
/* 609:818 */     return perspective;
/* 610:    */   }
/* 611:    */   
/* 612:    */   public void showMenuBar(JFrame topLevelAncestor)
/* 613:    */   {
/* 614:827 */     topLevelAncestor.setJMenuBar(this.m_appMenuBar);
/* 615:    */   }
/* 616:    */   
/* 617:    */   public boolean userRequestedPerspectiveToolbarVisibleOnStartup(Settings settings)
/* 618:    */   {
/* 619:840 */     SelectedPerspectivePreferences perspectivePreferences = (SelectedPerspectivePreferences)settings.getSetting(this.m_mainApp.getApplicationID(), VISIBLE_PERSPECTIVES_KEY, new SelectedPerspectivePreferences(), Environment.getSystemWide());
/* 620:    */     
/* 621:    */ 
/* 622:    */ 
/* 623:844 */     return perspectivePreferences.getPerspectivesToolbarVisibleOnStartup();
/* 624:    */   }
/* 625:    */   
/* 626:    */   public static class SelectedPerspectivePreferences
/* 627:    */     implements Serializable
/* 628:    */   {
/* 629:    */     private static final long serialVersionUID = -2665480123235382483L;
/* 630:857 */     protected LinkedList<String> m_userVisiblePerspectives = new LinkedList();
/* 631:    */     protected boolean m_perspectivesToolbarVisibleOnStartup;
/* 632:    */     protected boolean m_perspectivesToolbarAlwaysHidden;
/* 633:    */     
/* 634:    */     public void setUserVisiblePerspectives(LinkedList<String> userVisiblePerspectives)
/* 635:    */     {
/* 636:876 */       this.m_userVisiblePerspectives = userVisiblePerspectives;
/* 637:    */     }
/* 638:    */     
/* 639:    */     public LinkedList<String> getUserVisiblePerspectives()
/* 640:    */     {
/* 641:886 */       return this.m_userVisiblePerspectives;
/* 642:    */     }
/* 643:    */     
/* 644:    */     public void setPerspectivesToolbarVisibleOnStartup(boolean v)
/* 645:    */     {
/* 646:897 */       this.m_perspectivesToolbarVisibleOnStartup = v;
/* 647:    */     }
/* 648:    */     
/* 649:    */     public boolean getPerspectivesToolbarVisibleOnStartup()
/* 650:    */     {
/* 651:908 */       return this.m_perspectivesToolbarVisibleOnStartup;
/* 652:    */     }
/* 653:    */     
/* 654:    */     public void setPerspectivesToolbarAlwaysHidden(boolean h)
/* 655:    */     {
/* 656:917 */       this.m_perspectivesToolbarAlwaysHidden = h;
/* 657:    */     }
/* 658:    */     
/* 659:    */     public boolean getPerspectivesToolbarAlwaysHidden()
/* 660:    */     {
/* 661:926 */       return this.m_perspectivesToolbarAlwaysHidden;
/* 662:    */     }
/* 663:    */   }
/* 664:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.PerspectiveManager
 * JD-Core Version:    0.7.0.1
 */