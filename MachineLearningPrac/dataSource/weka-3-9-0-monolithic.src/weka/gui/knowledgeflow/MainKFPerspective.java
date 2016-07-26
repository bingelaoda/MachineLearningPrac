/*   1:    */ package weka.gui.knowledgeflow;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Dimension;
/*   5:    */ import java.io.File;
/*   6:    */ import java.util.ArrayList;
/*   7:    */ import java.util.List;
/*   8:    */ import javax.swing.JFileChooser;
/*   9:    */ import javax.swing.JMenu;
/*  10:    */ import javax.swing.JOptionPane;
/*  11:    */ import javax.swing.JSplitPane;
/*  12:    */ import javax.swing.JTabbedPane;
/*  13:    */ import javax.swing.JToggleButton;
/*  14:    */ import javax.swing.event.ChangeEvent;
/*  15:    */ import javax.swing.event.ChangeListener;
/*  16:    */ import javax.swing.filechooser.FileFilter;
/*  17:    */ import weka.core.Defaults;
/*  18:    */ import weka.core.Environment;
/*  19:    */ import weka.core.Instances;
/*  20:    */ import weka.core.Memory;
/*  21:    */ import weka.core.Settings;
/*  22:    */ import weka.core.Settings.SettingKey;
/*  23:    */ import weka.core.WekaException;
/*  24:    */ import weka.gui.AbstractPerspective;
/*  25:    */ import weka.gui.CloseableTabTitle;
/*  26:    */ import weka.gui.CloseableTabTitle.ClosingCallback;
/*  27:    */ import weka.gui.GUIApplication;
/*  28:    */ import weka.gui.PerspectiveInfo;
/*  29:    */ import weka.gui.explorer.PreprocessPanel.PreprocessDefaults;
/*  30:    */ import weka.knowledgeflow.Flow;
/*  31:    */ import weka.knowledgeflow.JSONFlowUtils;
/*  32:    */ import weka.knowledgeflow.KFDefaults;
/*  33:    */ import weka.knowledgeflow.LoggingLevel;
/*  34:    */ import weka.knowledgeflow.StepManagerImpl;
/*  35:    */ import weka.knowledgeflow.steps.MemoryBasedDataSource;
/*  36:    */ 
/*  37:    */ @PerspectiveInfo(ID="knowledgeflow.main", title="Data mining processes", toolTipText="Data mining processes", iconPath="weka/gui/weka_icon_new_small.png")
/*  38:    */ public class MainKFPerspective
/*  39:    */   extends AbstractPerspective
/*  40:    */ {
/*  41:    */   public static final String FLOW_PARENT_DIRECTORY_VARIABLE_KEY = "Internal.knowledgeflow.directory";
/*  42:    */   public static final String FILE_EXTENSION_JSON = ".kf";
/*  43:    */   private static final long serialVersionUID = 3986047323839299447L;
/*  44: 84 */   private static Memory m_memory = new Memory(true);
/*  45: 87 */   protected int m_untitledCount = 1;
/*  46: 90 */   protected boolean m_allowMultipleTabs = true;
/*  47:    */   protected StepManagerImpl m_palleteSelectedStep;
/*  48: 96 */   protected JTabbedPane m_flowTabs = new JTabbedPane();
/*  49: 99 */   protected List<VisibleLayout> m_flowGraphs = new ArrayList();
/*  50:    */   protected StepTree m_stepTree;
/*  51:    */   protected String m_pasteBuffer;
/*  52:108 */   protected JFileChooser m_FileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
/*  53:112 */   protected TemplateManager m_templateManager = new TemplateManager();
/*  54:    */   protected MainKFPerspectiveToolBar m_mainToolBar;
/*  55:    */   
/*  56:    */   public MainKFPerspective()
/*  57:    */   {
/*  58:121 */     this.m_isLoaded = true;
/*  59:122 */     this.m_isActive = true;
/*  60:123 */     setLayout(new BorderLayout());
/*  61:124 */     this.m_stepTree = new StepTree(this);
/*  62:125 */     DesignPanel designPanel = new DesignPanel(this.m_stepTree);
/*  63:    */     
/*  64:    */ 
/*  65:128 */     JSplitPane pane = new JSplitPane(1, designPanel, this.m_flowTabs);
/*  66:    */     
/*  67:130 */     pane.setOneTouchExpandable(true);
/*  68:131 */     add(pane, "Center");
/*  69:132 */     Dimension d = designPanel.getPreferredSize();
/*  70:133 */     d = new Dimension((int)(d.getWidth() * 1.5D), (int)d.getHeight());
/*  71:134 */     designPanel.setPreferredSize(d);
/*  72:135 */     designPanel.setMinimumSize(d);
/*  73:    */     
/*  74:137 */     this.m_flowTabs.addChangeListener(new ChangeListener()
/*  75:    */     {
/*  76:    */       public void stateChanged(ChangeEvent e)
/*  77:    */       {
/*  78:140 */         int selected = MainKFPerspective.this.m_flowTabs.getSelectedIndex();
/*  79:141 */         MainKFPerspective.this.setActiveTab(selected);
/*  80:    */       }
/*  81:144 */     });
/*  82:145 */     this.m_mainToolBar = new MainKFPerspectiveToolBar(this);
/*  83:146 */     add(this.m_mainToolBar, "North");
/*  84:    */     
/*  85:148 */     FileFilter nativeF = null;
/*  86:149 */     for (FileFilter f : Flow.FLOW_FILE_EXTENSIONS)
/*  87:    */     {
/*  88:150 */       this.m_FileChooser.addChoosableFileFilter(f);
/*  89:151 */       if (((weka.gui.ExtensionFileFilter)f).getExtensions()[0].equals(".kf")) {
/*  90:153 */         nativeF = f;
/*  91:    */       }
/*  92:    */     }
/*  93:157 */     if (nativeF != null) {
/*  94:158 */       this.m_FileChooser.setFileFilter(nativeF);
/*  95:    */     }
/*  96:    */   }
/*  97:    */   
/*  98:    */   public StepManagerImpl getPalleteSelectedStep()
/*  99:    */   {
/* 100:169 */     return this.m_palleteSelectedStep;
/* 101:    */   }
/* 102:    */   
/* 103:    */   protected void setPalleteSelectedStep(StepManagerImpl stepvisual)
/* 104:    */   {
/* 105:178 */     this.m_palleteSelectedStep = stepvisual;
/* 106:    */   }
/* 107:    */   
/* 108:    */   public void showErrorDialog(Exception cause)
/* 109:    */   {
/* 110:187 */     this.m_mainApplication.showErrorDialog(cause);
/* 111:    */   }
/* 112:    */   
/* 113:    */   public void showInfoDialog(Object information, String title, boolean isWarning)
/* 114:    */   {
/* 115:199 */     this.m_mainApplication.showInfoDialog(information, title, isWarning);
/* 116:    */   }
/* 117:    */   
/* 118:    */   public void setFlowLayoutOperation(VisibleLayout.LayoutOperation opp)
/* 119:    */   {
/* 120:211 */     if ((getCurrentTabIndex() < getNumTabs()) && (getCurrentTabIndex() >= 0)) {
/* 121:212 */       ((VisibleLayout)this.m_flowGraphs.get(getCurrentTabIndex())).setFlowLayoutOperation(opp);
/* 122:    */     }
/* 123:    */   }
/* 124:    */   
/* 125:    */   public boolean getSnapToGrid()
/* 126:    */   {
/* 127:222 */     return ((JToggleButton)this.m_mainToolBar.getWidget(MainKFPerspectiveToolBar.Widgets.SNAP_TO_GRID_BUTTON.toString())).isSelected();
/* 128:    */   }
/* 129:    */   
/* 130:    */   public void clearDesignPaletteSelection()
/* 131:    */   {
/* 132:231 */     this.m_stepTree.clearSelection();
/* 133:    */   }
/* 134:    */   
/* 135:    */   public String getPasteBuffer()
/* 136:    */   {
/* 137:240 */     return this.m_pasteBuffer;
/* 138:    */   }
/* 139:    */   
/* 140:    */   protected void setPasteBuffer(String serializedFlow)
/* 141:    */   {
/* 142:249 */     this.m_pasteBuffer = serializedFlow;
/* 143:    */   }
/* 144:    */   
/* 145:    */   public void copyStepsToClipboard(List<StepVisual> steps)
/* 146:    */     throws WekaException
/* 147:    */   {
/* 148:259 */     if (steps.size() > 0)
/* 149:    */     {
/* 150:260 */       this.m_pasteBuffer = VisibleLayout.serializeStepsToJSON(steps, "Clipboard copy");
/* 151:    */       
/* 152:262 */       getMainToolBar().enableWidgets(new String[] { MainKFPerspectiveToolBar.Widgets.PASTE_BUTTON.toString() });
/* 153:    */     }
/* 154:    */   }
/* 155:    */   
/* 156:    */   public void copyFlowToClipboard(Flow flow)
/* 157:    */     throws WekaException
/* 158:    */   {
/* 159:268 */     this.m_pasteBuffer = JSONFlowUtils.flowToJSON(flow);
/* 160:269 */     getMainToolBar().enableWidgets(new String[] { MainKFPerspectiveToolBar.Widgets.PASTE_BUTTON.toString() });
/* 161:    */   }
/* 162:    */   
/* 163:    */   public TemplateManager getTemplateManager()
/* 164:    */   {
/* 165:279 */     return this.m_templateManager;
/* 166:    */   }
/* 167:    */   
/* 168:    */   public synchronized void addUntitledTab()
/* 169:    */   {
/* 170:286 */     if ((getNumTabs() == 0) || (getAllowMultipleTabs()))
/* 171:    */     {
/* 172:287 */       addTab("Untitled" + this.m_untitledCount++);
/* 173:    */     }
/* 174:    */     else
/* 175:    */     {
/* 176:289 */       ((VisibleLayout)this.m_flowGraphs.get(getCurrentTabIndex())).stopFlow();
/* 177:290 */       ((VisibleLayout)this.m_flowGraphs.get(getCurrentTabIndex())).setFlow(new Flow());
/* 178:    */     }
/* 179:    */   }
/* 180:    */   
/* 181:    */   public synchronized void addTab(String tabTitle)
/* 182:    */   {
/* 183:300 */     VisibleLayout newLayout = new VisibleLayout(this);
/* 184:301 */     this.m_flowGraphs.add(newLayout);
/* 185:    */     
/* 186:303 */     this.m_flowTabs.add(tabTitle, newLayout);
/* 187:304 */     this.m_flowTabs.setTabComponentAt(getNumTabs() - 1, new CloseableTabTitle(this.m_flowTabs, "(Ctrl+W)", new CloseableTabTitle.ClosingCallback()
/* 188:    */     {
/* 189:    */       public void tabClosing(int tabIndex)
/* 190:    */       {
/* 191:308 */         if (MainKFPerspective.this.getAllowMultipleTabs()) {
/* 192:309 */           MainKFPerspective.this.removeTab(tabIndex);
/* 193:    */         }
/* 194:    */       }
/* 195:313 */     }));
/* 196:314 */     setActiveTab(getNumTabs() - 1);
/* 197:    */   }
/* 198:    */   
/* 199:    */   public void setCurrentTabTitleEditedStatus(boolean edited)
/* 200:    */   {
/* 201:323 */     CloseableTabTitle current = (CloseableTabTitle)this.m_flowTabs.getTabComponentAt(getCurrentTabIndex());
/* 202:    */     
/* 203:325 */     current.setBold(edited);
/* 204:    */   }
/* 205:    */   
/* 206:    */   public int getCurrentTabIndex()
/* 207:    */   {
/* 208:334 */     return this.m_flowTabs.getSelectedIndex();
/* 209:    */   }
/* 210:    */   
/* 211:    */   public synchronized int getNumTabs()
/* 212:    */   {
/* 213:343 */     return this.m_flowTabs.getTabCount();
/* 214:    */   }
/* 215:    */   
/* 216:    */   public synchronized String getTabTitle(int index)
/* 217:    */   {
/* 218:354 */     if ((index < getNumTabs()) && (index >= 0)) {
/* 219:355 */       return this.m_flowTabs.getTitleAt(index);
/* 220:    */     }
/* 221:358 */     throw new IndexOutOfBoundsException("Tab index " + index + " is out of range!");
/* 222:    */   }
/* 223:    */   
/* 224:    */   public synchronized void setActiveTab(int tabIndex)
/* 225:    */   {
/* 226:368 */     if ((tabIndex < getNumTabs()) && (tabIndex >= 0))
/* 227:    */     {
/* 228:369 */       this.m_flowTabs.setSelectedIndex(tabIndex);
/* 229:    */       
/* 230:371 */       VisibleLayout current = getCurrentLayout();
/* 231:372 */       this.m_mainToolBar.enableWidget(MainKFPerspectiveToolBar.Widgets.SAVE_FLOW_BUTTON.toString(), !current.isExecuting());
/* 232:    */       
/* 233:    */ 
/* 234:375 */       this.m_mainToolBar.enableWidget(MainKFPerspectiveToolBar.Widgets.SAVE_FLOW_AS_BUTTON.toString(), !current.isExecuting());
/* 235:    */       
/* 236:    */ 
/* 237:378 */       this.m_mainToolBar.enableWidget(MainKFPerspectiveToolBar.Widgets.PLAY_PARALLEL_BUTTON.toString(), !current.isExecuting());
/* 238:    */       
/* 239:    */ 
/* 240:381 */       this.m_mainToolBar.enableWidget(MainKFPerspectiveToolBar.Widgets.PLAY_SEQUENTIAL_BUTTON.toString(), !current.isExecuting());
/* 241:    */       
/* 242:    */ 
/* 243:384 */       this.m_mainToolBar.enableWidget(MainKFPerspectiveToolBar.Widgets.ZOOM_OUT_BUTTON.toString(), !current.isExecuting());
/* 244:    */       
/* 245:    */ 
/* 246:387 */       this.m_mainToolBar.enableWidget(MainKFPerspectiveToolBar.Widgets.ZOOM_IN_BUTTON.toString(), !current.isExecuting());
/* 247:391 */       if (current.getZoomSetting() == 50) {
/* 248:392 */         this.m_mainToolBar.enableWidget(MainKFPerspectiveToolBar.Widgets.ZOOM_OUT_BUTTON.toString(), false);
/* 249:    */       }
/* 250:395 */       if (current.getZoomSetting() == 200) {
/* 251:396 */         this.m_mainToolBar.enableWidget(MainKFPerspectiveToolBar.Widgets.ZOOM_IN_BUTTON.toString(), false);
/* 252:    */       }
/* 253:400 */       this.m_mainToolBar.enableWidget(MainKFPerspectiveToolBar.Widgets.CUT_BUTTON.toString(), (current.getSelectedSteps().size() > 0) && (!current.isExecuting()));
/* 254:    */       
/* 255:    */ 
/* 256:403 */       this.m_mainToolBar.enableWidget(MainKFPerspectiveToolBar.Widgets.COPY_BUTTON.toString(), (current.getSelectedSteps().size() > 0) && (!current.isExecuting()));
/* 257:    */       
/* 258:    */ 
/* 259:406 */       this.m_mainToolBar.enableWidget(MainKFPerspectiveToolBar.Widgets.DELETE_BUTTON.toString(), (current.getSelectedSteps().size() > 0) && (!current.isExecuting()));
/* 260:    */       
/* 261:    */ 
/* 262:409 */       this.m_mainToolBar.enableWidget(MainKFPerspectiveToolBar.Widgets.SELECT_ALL_BUTTON.toString(), (current.numSteps() > 0) && (!current.isExecuting()));
/* 263:    */       
/* 264:    */ 
/* 265:412 */       this.m_mainToolBar.enableWidget(MainKFPerspectiveToolBar.Widgets.PASTE_BUTTON.toString(), (getPasteBuffer() != null) && (getPasteBuffer().length() > 0) && (!current.isExecuting()));
/* 266:    */       
/* 267:    */ 
/* 268:    */ 
/* 269:416 */       this.m_mainToolBar.enableWidget(MainKFPerspectiveToolBar.Widgets.STOP_BUTTON.toString(), current.isExecuting());
/* 270:    */       
/* 271:    */ 
/* 272:419 */       this.m_mainToolBar.enableWidget(MainKFPerspectiveToolBar.Widgets.UNDO_BUTTON.toString(), (!current.isExecuting()) && (current.getUndoBufferSize() > 0));
/* 273:    */       
/* 274:    */ 
/* 275:    */ 
/* 276:423 */       this.m_mainToolBar.enableWidget(MainKFPerspectiveToolBar.Widgets.NEW_FLOW_BUTTON.toString(), (!current.isExecuting()) && (getAllowMultipleTabs()));
/* 277:    */     }
/* 278:    */   }
/* 279:    */   
/* 280:    */   public void closeAllTabs()
/* 281:    */   {
/* 282:433 */     for (int i = 0; i < getNumTabs(); i++) {
/* 283:434 */       removeTab(i);
/* 284:    */     }
/* 285:    */   }
/* 286:    */   
/* 287:    */   public synchronized void removeTab(int tabIndex)
/* 288:    */   {
/* 289:444 */     if ((tabIndex < 0) || (tabIndex >= getNumTabs())) {
/* 290:445 */       return;
/* 291:    */     }
/* 292:448 */     if (((VisibleLayout)this.m_flowGraphs.get(tabIndex)).getEdited())
/* 293:    */     {
/* 294:449 */       String tabTitle = this.m_flowTabs.getTitleAt(tabIndex);
/* 295:450 */       String message = "\"" + tabTitle + "\" has been modified. Save changes " + "before closing?";
/* 296:    */       
/* 297:    */ 
/* 298:453 */       int result = JOptionPane.showConfirmDialog(this, message, "Save changes", 1);
/* 299:457 */       if (result == 0) {
/* 300:458 */         saveLayout(tabIndex, false);
/* 301:459 */       } else if (result == 2) {
/* 302:460 */         return;
/* 303:    */       }
/* 304:    */     }
/* 305:463 */     this.m_flowTabs.remove(tabIndex);
/* 306:464 */     this.m_flowGraphs.remove(tabIndex);
/* 307:465 */     if (getCurrentTabIndex() < 0) {
/* 308:466 */       this.m_mainToolBar.disableWidgets(new String[] { MainKFPerspectiveToolBar.Widgets.SAVE_FLOW_BUTTON.toString(), MainKFPerspectiveToolBar.Widgets.SAVE_FLOW_AS_BUTTON.toString() });
/* 309:    */     }
/* 310:    */   }
/* 311:    */   
/* 312:    */   public VisibleLayout getCurrentLayout()
/* 313:    */   {
/* 314:478 */     if (getCurrentTabIndex() >= 0) {
/* 315:479 */       return (VisibleLayout)this.m_flowGraphs.get(getCurrentTabIndex());
/* 316:    */     }
/* 317:482 */     return null;
/* 318:    */   }
/* 319:    */   
/* 320:    */   public VisibleLayout getLayoutAt(int index)
/* 321:    */   {
/* 322:493 */     if ((index >= 0) && (index < this.m_flowGraphs.size())) {
/* 323:494 */       return (VisibleLayout)this.m_flowGraphs.get(index);
/* 324:    */     }
/* 325:497 */     throw new IndexOutOfBoundsException("Flow index " + index + " is out of range!");
/* 326:    */   }
/* 327:    */   
/* 328:    */   public boolean getAllowMultipleTabs()
/* 329:    */   {
/* 330:507 */     return this.m_allowMultipleTabs;
/* 331:    */   }
/* 332:    */   
/* 333:    */   public void setAllowMultipleTabs(boolean allow)
/* 334:    */   {
/* 335:516 */     this.m_allowMultipleTabs = allow;
/* 336:    */   }
/* 337:    */   
/* 338:    */   public <T> T getSetting(Settings.SettingKey propKey, T defaultVal)
/* 339:    */   {
/* 340:528 */     return this.m_mainApplication.getApplicationSettings().getSetting(getPerspectiveID(), propKey, defaultVal, null);
/* 341:    */   }
/* 342:    */   
/* 343:    */   public void notifyIsDirty()
/* 344:    */   {
/* 345:533 */     firePropertyChange("PROP_DIRTY", null, null);
/* 346:    */   }
/* 347:    */   
/* 348:    */   protected void saveLayout(int tabIndex, boolean showDialog)
/* 349:    */   {
/* 350:543 */     ((VisibleLayout)this.m_flowGraphs.get(tabIndex)).saveLayout(showDialog);
/* 351:    */   }
/* 352:    */   
/* 353:    */   public void loadLayout()
/* 354:    */   {
/* 355:550 */     File lFile = null;
/* 356:551 */     int returnVal = this.m_FileChooser.showOpenDialog(this);
/* 357:552 */     if (returnVal == 0)
/* 358:    */     {
/* 359:553 */       lFile = this.m_FileChooser.getSelectedFile();
/* 360:554 */       loadLayout(lFile, true);
/* 361:    */     }
/* 362:    */   }
/* 363:    */   
/* 364:    */   public void loadLayout(File fFile, boolean newTab)
/* 365:    */   {
/* 366:565 */     File absoluteF = fFile.getAbsoluteFile();
/* 367:566 */     if (!newTab)
/* 368:    */     {
/* 369:567 */       ((VisibleLayout)this.m_flowGraphs.get(getCurrentTabIndex())).loadLayout(fFile, false);
/* 370:568 */       ((VisibleLayout)this.m_flowGraphs.get(getCurrentTabIndex())).getEnvironment().addVariable("Internal.knowledgeflow.directory", absoluteF.getParent());
/* 371:    */     }
/* 372:    */     else
/* 373:    */     {
/* 374:571 */       String tabTitle = fFile.toString();
/* 375:572 */       tabTitle = tabTitle.substring(tabTitle.lastIndexOf(File.separator) + 1, tabTitle.length());
/* 376:575 */       if (tabTitle.lastIndexOf('.') > 0) {
/* 377:576 */         tabTitle = tabTitle.substring(0, tabTitle.lastIndexOf('.'));
/* 378:    */       }
/* 379:578 */       addTab(tabTitle);
/* 380:579 */       VisibleLayout current = getCurrentLayout();
/* 381:580 */       current.loadLayout(fFile, false);
/* 382:581 */       current.getEnvironment().addVariable("Internal.knowledgeflow.directory", absoluteF.getParent());
/* 383:    */       
/* 384:    */ 
/* 385:    */ 
/* 386:585 */       setActiveTab(getCurrentTabIndex());
/* 387:    */     }
/* 388:    */   }
/* 389:    */   
/* 390:    */   protected void setCurrentTabTitle(String title)
/* 391:    */   {
/* 392:595 */     this.m_flowTabs.setTitleAt(getCurrentTabIndex(), title);
/* 393:    */   }
/* 394:    */   
/* 395:    */   public boolean isMemoryLow()
/* 396:    */   {
/* 397:604 */     return m_memory.memoryIsLow();
/* 398:    */   }
/* 399:    */   
/* 400:    */   public boolean showMemoryIsLow()
/* 401:    */   {
/* 402:614 */     return m_memory.showMemoryIsLow();
/* 403:    */   }
/* 404:    */   
/* 405:    */   public boolean getDebug()
/* 406:    */   {
/* 407:618 */     return ((LoggingLevel)getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), KFDefaults.LOGGING_LEVEL_KEY, LoggingLevel.BASIC, Environment.getSystemWide())).ordinal() == LoggingLevel.DEBUGGING.ordinal();
/* 408:    */   }
/* 409:    */   
/* 410:    */   public MainKFPerspectiveToolBar getMainToolBar()
/* 411:    */   {
/* 412:631 */     return this.m_mainToolBar;
/* 413:    */   }
/* 414:    */   
/* 415:    */   public void setActive(boolean active)
/* 416:    */   {
/* 417:642 */     if (active) {
/* 418:645 */       if (!getMainApplication().getApplicationID().equalsIgnoreCase("knowledgeflow")) {
/* 419:648 */         if (getNumTabs() == 0) {
/* 420:649 */           addUntitledTab();
/* 421:    */         }
/* 422:    */       }
/* 423:    */     }
/* 424:    */   }
/* 425:    */   
/* 426:    */   public List<JMenu> getMenus()
/* 427:    */   {
/* 428:663 */     return this.m_mainToolBar.getMenus();
/* 429:    */   }
/* 430:    */   
/* 431:    */   public Defaults getDefaultSettings()
/* 432:    */   {
/* 433:674 */     return new KFDefaults();
/* 434:    */   }
/* 435:    */   
/* 436:    */   public boolean okToBeActive()
/* 437:    */   {
/* 438:687 */     return true;
/* 439:    */   }
/* 440:    */   
/* 441:    */   public void settingsChanged()
/* 442:    */   {
/* 443:696 */     Settings settings = getMainApplication().getApplicationSettings();
/* 444:697 */     int fontSize = ((Integer)settings.getSetting("knowledgeflow.main", new Settings.SettingKey("knowledgeflow.main.logMessageFontSize", "", ""), Integer.valueOf(-1))).intValue();
/* 445:701 */     for (VisibleLayout v : this.m_flowGraphs) {
/* 446:702 */       v.getLogPanel().setLoggingFontSize(fontSize);
/* 447:    */     }
/* 448:    */   }
/* 449:    */   
/* 450:    */   public boolean acceptsInstances()
/* 451:    */   {
/* 452:718 */     GUIApplication mainApp = getMainApplication();
/* 453:719 */     if (mainApp.getApplicationID().equals("workbench"))
/* 454:    */     {
/* 455:720 */       boolean sendToAll = ((Boolean)mainApp.getApplicationSettings().getSetting("weka.gui.explorer.preprocesspanel", PreprocessPanel.PreprocessDefaults.ALWAYS_SEND_INSTANCES_TO_ALL_KEY, Boolean.valueOf(PreprocessPanel.PreprocessDefaults.ALWAYS_SEND_INSTANCES_TO_ALL), Environment.getSystemWide())).booleanValue();
/* 456:    */       
/* 457:    */ 
/* 458:    */ 
/* 459:    */ 
/* 460:    */ 
/* 461:    */ 
/* 462:727 */       return !sendToAll;
/* 463:    */     }
/* 464:729 */     return false;
/* 465:    */   }
/* 466:    */   
/* 467:    */   public void setInstances(Instances instances)
/* 468:    */   {
/* 469:739 */     addUntitledTab();
/* 470:    */     
/* 471:741 */     VisibleLayout newL = (VisibleLayout)this.m_flowGraphs.get(this.m_flowGraphs.size() - 1);
/* 472:742 */     MemoryBasedDataSource memoryBasedDataSource = new MemoryBasedDataSource();
/* 473:743 */     memoryBasedDataSource.setInstances(instances);
/* 474:    */     
/* 475:745 */     StepManagerImpl newS = new StepManagerImpl(memoryBasedDataSource);
/* 476:746 */     newL.addStep(newS, 30, 30);
/* 477:    */   }
/* 478:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.MainKFPerspective
 * JD-Core Version:    0.7.0.1
 */