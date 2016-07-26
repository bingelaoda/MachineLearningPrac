/*    1:     */ package weka.gui.knowledgeflow;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.awt.Cursor;
/*    5:     */ import java.awt.Dimension;
/*    6:     */ import java.awt.Point;
/*    7:     */ import java.awt.Rectangle;
/*    8:     */ import java.awt.event.MouseAdapter;
/*    9:     */ import java.awt.event.MouseEvent;
/*   10:     */ import java.io.File;
/*   11:     */ import java.io.PrintStream;
/*   12:     */ import java.text.SimpleDateFormat;
/*   13:     */ import java.util.ArrayList;
/*   14:     */ import java.util.Date;
/*   15:     */ import java.util.HashMap;
/*   16:     */ import java.util.Iterator;
/*   17:     */ import java.util.List;
/*   18:     */ import java.util.Map;
/*   19:     */ import java.util.Map.Entry;
/*   20:     */ import java.util.Set;
/*   21:     */ import java.util.Stack;
/*   22:     */ import javax.swing.JFileChooser;
/*   23:     */ import javax.swing.JPanel;
/*   24:     */ import javax.swing.JScrollBar;
/*   25:     */ import javax.swing.JScrollPane;
/*   26:     */ import javax.swing.JSplitPane;
/*   27:     */ import javax.swing.JTable;
/*   28:     */ import weka.core.Copyright;
/*   29:     */ import weka.core.Environment;
/*   30:     */ import weka.core.WekaException;
/*   31:     */ import weka.gui.GUIApplication;
/*   32:     */ import weka.gui.beans.LogPanel;
/*   33:     */ import weka.knowledgeflow.BaseExecutionEnvironment;
/*   34:     */ import weka.knowledgeflow.ExecutionFinishedCallback;
/*   35:     */ import weka.knowledgeflow.Flow;
/*   36:     */ import weka.knowledgeflow.FlowExecutor;
/*   37:     */ import weka.knowledgeflow.FlowRunner;
/*   38:     */ import weka.knowledgeflow.JSONFlowUtils;
/*   39:     */ import weka.knowledgeflow.KFDefaults;
/*   40:     */ import weka.knowledgeflow.LogManager;
/*   41:     */ import weka.knowledgeflow.StepManager;
/*   42:     */ import weka.knowledgeflow.StepManagerImpl;
/*   43:     */ import weka.knowledgeflow.steps.Step;
/*   44:     */ 
/*   45:     */ public class VisibleLayout
/*   46:     */   extends JPanel
/*   47:     */ {
/*   48:     */   protected static final int LAYOUT_WIDTH = 2560;
/*   49:     */   protected static final int LAYOUT_HEIGHT = 1440;
/*   50:     */   protected static final int SCROLLBAR_INCREMENT = 50;
/*   51:     */   private static final long serialVersionUID = -3644458365810712479L;
/*   52:     */   protected Flow m_flow;
/*   53:  77 */   protected KFLogPanel m_logPanel = new KFLogPanel();
/*   54:  80 */   protected int m_zoomSetting = 100;
/*   55:     */   protected File m_filePath;
/*   56:  86 */   protected List<StepVisual> m_selectedSteps = new ArrayList();
/*   57:  89 */   protected Stack<File> m_undoBuffer = new Stack();
/*   58:     */   protected boolean m_hasBeenEdited;
/*   59:     */   protected FlowExecutor m_flowExecutor;
/*   60:  98 */   protected Environment m_env = Environment.getSystemWide();
/*   61:     */   protected boolean m_isExecuting;
/*   62:     */   protected MainKFPerspective m_mainPerspective;
/*   63: 107 */   protected List<StepVisual> m_renderGraph = new ArrayList();
/*   64: 110 */   protected LayoutOperation m_userOpp = LayoutOperation.NONE;
/*   65:     */   protected LayoutPanel m_layout;
/*   66:     */   protected StepVisual m_editStep;
/*   67:     */   protected String m_editConnection;
/*   68:     */   
/*   69:     */   public VisibleLayout(MainKFPerspective mainPerspective)
/*   70:     */   {
/*   71: 131 */     setLayout(new BorderLayout());
/*   72:     */     
/*   73: 133 */     this.m_flow = new Flow();
/*   74: 134 */     this.m_mainPerspective = mainPerspective;
/*   75:     */     
/*   76: 136 */     this.m_layout = new LayoutPanel(this);
/*   77: 137 */     JPanel p1 = new JPanel();
/*   78: 138 */     p1.setLayout(new BorderLayout());
/*   79: 139 */     JScrollPane js = new JScrollPane(this.m_layout);
/*   80: 140 */     p1.add(js, "Center");
/*   81: 141 */     js.getVerticalScrollBar().setUnitIncrement(20);
/*   82:     */     
/*   83: 143 */     js.getHorizontalScrollBar().setUnitIncrement(20);
/*   84:     */     
/*   85:     */ 
/*   86: 146 */     this.m_layout.setSize(((Integer)this.m_mainPerspective.getSetting(KFDefaults.LAYOUT_WIDTH_KEY, Integer.valueOf(2560))).intValue(), ((Integer)this.m_mainPerspective.getSetting(KFDefaults.LAYOUT_HEIGHT_KEY, Integer.valueOf(1440))).intValue());
/*   87:     */     
/*   88:     */ 
/*   89: 149 */     Dimension d = this.m_layout.getPreferredSize();
/*   90: 150 */     this.m_layout.setMinimumSize(d);
/*   91: 151 */     this.m_layout.setPreferredSize(d);
/*   92:     */     
/*   93: 153 */     this.m_logPanel = new KFLogPanel();
/*   94: 154 */     setUpLogPanel(this.m_logPanel);
/*   95: 155 */     Dimension d2 = new Dimension(100, 170);
/*   96: 156 */     this.m_logPanel.setPreferredSize(d2);
/*   97: 157 */     this.m_logPanel.setMinimumSize(d2);
/*   98: 158 */     this.m_filePath = new File("-NONE-");
/*   99:     */     
/*  100: 160 */     JSplitPane p2 = new JSplitPane(0, p1, this.m_logPanel);
/*  101: 161 */     p2.setOneTouchExpandable(true);
/*  102: 162 */     p2.setDividerLocation(0.7D);
/*  103:     */     
/*  104: 164 */     p2.setResizeWeight(1.0D);
/*  105:     */     
/*  106: 166 */     add(p2, "Center");
/*  107:     */   }
/*  108:     */   
/*  109:     */   protected List<StepVisual> getRenderGraph()
/*  110:     */   {
/*  111: 176 */     return this.m_renderGraph;
/*  112:     */   }
/*  113:     */   
/*  114:     */   protected MainKFPerspective getMainPerspective()
/*  115:     */   {
/*  116: 185 */     return this.m_mainPerspective;
/*  117:     */   }
/*  118:     */   
/*  119:     */   protected StepVisual getEditStep()
/*  120:     */   {
/*  121: 195 */     return this.m_editStep;
/*  122:     */   }
/*  123:     */   
/*  124:     */   protected void setEditStep(StepVisual step)
/*  125:     */   {
/*  126: 204 */     this.m_editStep = step;
/*  127:     */   }
/*  128:     */   
/*  129:     */   protected String getEditConnection()
/*  130:     */   {
/*  131: 215 */     return this.m_editConnection;
/*  132:     */   }
/*  133:     */   
/*  134:     */   protected void setEditConnection(String connName)
/*  135:     */   {
/*  136: 224 */     this.m_editConnection = connName;
/*  137:     */   }
/*  138:     */   
/*  139:     */   protected List<StepVisual> getSelectedSteps()
/*  140:     */   {
/*  141: 234 */     return this.m_selectedSteps;
/*  142:     */   }
/*  143:     */   
/*  144:     */   protected void setSelectedSteps(List<StepVisual> selected)
/*  145:     */   {
/*  146: 245 */     for (StepVisual s : this.m_selectedSteps) {
/*  147: 246 */       s.setDisplayConnectors(false);
/*  148:     */     }
/*  149: 249 */     this.m_selectedSteps = selected;
/*  150: 252 */     for (StepVisual s : this.m_selectedSteps) {
/*  151: 253 */       s.setDisplayConnectors(true);
/*  152:     */     }
/*  153: 256 */     if (this.m_selectedSteps.size() > 0) {
/*  154: 257 */       this.m_mainPerspective.getMainToolBar().enableWidgets(new String[] { MainKFPerspectiveToolBar.Widgets.CUT_BUTTON.toString(), MainKFPerspectiveToolBar.Widgets.COPY_BUTTON.toString(), MainKFPerspectiveToolBar.Widgets.DELETE_BUTTON.toString() });
/*  155:     */     } else {
/*  156: 262 */       this.m_mainPerspective.getMainToolBar().disableWidgets(new String[] { MainKFPerspectiveToolBar.Widgets.CUT_BUTTON.toString(), MainKFPerspectiveToolBar.Widgets.COPY_BUTTON.toString(), MainKFPerspectiveToolBar.Widgets.DELETE_BUTTON.toString() });
/*  157:     */     }
/*  158:     */   }
/*  159:     */   
/*  160:     */   protected void removeSelectedSteps()
/*  161:     */     throws WekaException
/*  162:     */   {
/*  163: 275 */     addUndoPoint();
/*  164: 276 */     for (StepVisual v : this.m_selectedSteps)
/*  165:     */     {
/*  166: 277 */       this.m_flow.removeStep(v.getStepManager());
/*  167: 278 */       this.m_renderGraph.remove(v);
/*  168: 279 */       this.m_layout.remove(v);
/*  169:     */       
/*  170: 281 */       String key = v.getStepName() + "$" + v.getStepManager().getManagedStep().hashCode();
/*  171:     */       
/*  172: 283 */       this.m_logPanel.statusMessage(key + "|remove");
/*  173:     */     }
/*  174: 286 */     setSelectedSteps(new ArrayList());
/*  175: 287 */     this.m_mainPerspective.getMainToolBar().disableWidgets(new String[] { MainKFPerspectiveToolBar.Widgets.DELETE_BUTTON.toString() });
/*  176:     */     
/*  177: 289 */     this.m_mainPerspective.getMainToolBar().enableWidget(MainKFPerspectiveToolBar.Widgets.SELECT_ALL_BUTTON.toString(), this.m_flow.size() > 0);
/*  178:     */     
/*  179:     */ 
/*  180: 292 */     this.m_layout.repaint();
/*  181:     */   }
/*  182:     */   
/*  183:     */   protected void copySelectedStepsToClipboard()
/*  184:     */     throws WekaException
/*  185:     */   {
/*  186: 301 */     copyStepsToClipboard(getSelectedSteps());
/*  187:     */   }
/*  188:     */   
/*  189:     */   protected void copyStepsToClipboard(List<StepVisual> steps)
/*  190:     */     throws WekaException
/*  191:     */   {
/*  192: 312 */     this.m_mainPerspective.copyStepsToClipboard(steps);
/*  193:     */   }
/*  194:     */   
/*  195:     */   protected void pasteFromClipboard(int x, int y)
/*  196:     */     throws WekaException
/*  197:     */   {
/*  198: 324 */     addUndoPoint();
/*  199:     */     
/*  200: 326 */     Flow fromPaste = Flow.JSONToFlow(this.m_mainPerspective.getPasteBuffer(), true);
/*  201: 327 */     List<StepVisual> added = addAll(fromPaste.getSteps(), false);
/*  202:     */     
/*  203:     */ 
/*  204:     */ 
/*  205: 331 */     int minX = 2147483647;
/*  206: 332 */     int minY = 2147483647;
/*  207: 333 */     for (StepVisual v : added)
/*  208:     */     {
/*  209: 334 */       if (v.getX() < minX) {
/*  210: 335 */         minX = v.getX();
/*  211:     */       }
/*  212: 337 */       if (v.getY() < minY) {
/*  213: 338 */         minY = v.getY();
/*  214:     */       }
/*  215:     */     }
/*  216: 341 */     int deltaX = x - minX;
/*  217: 342 */     int deltaY = y - minY;
/*  218: 343 */     for (StepVisual v : added)
/*  219:     */     {
/*  220: 344 */       v.setX(v.getX() + deltaX);
/*  221: 345 */       v.setY(v.getY() + deltaY);
/*  222:     */     }
/*  223: 348 */     this.m_layout.revalidate();
/*  224: 349 */     this.m_layout.repaint();
/*  225:     */     
/*  226: 351 */     setSelectedSteps(added);
/*  227:     */   }
/*  228:     */   
/*  229:     */   protected void addUndoPoint()
/*  230:     */   {
/*  231:     */     try
/*  232:     */     {
/*  233: 359 */       File tempFile = File.createTempFile("knowledgeflow", ".kf");
/*  234:     */       
/*  235:     */ 
/*  236: 362 */       tempFile.deleteOnExit();
/*  237: 363 */       JSONFlowUtils.writeFlow(this.m_flow, tempFile);
/*  238: 364 */       this.m_undoBuffer.push(tempFile);
/*  239: 366 */       if (this.m_undoBuffer.size() > ((Integer)this.m_mainPerspective.getSetting(KFDefaults.MAX_UNDO_POINTS_KEY, Integer.valueOf(20))).intValue()) {
/*  240: 368 */         this.m_undoBuffer.remove(0);
/*  241:     */       }
/*  242: 370 */       this.m_mainPerspective.getMainToolBar().enableWidgets(new String[] { MainKFPerspectiveToolBar.Widgets.UNDO_BUTTON.toString() });
/*  243:     */     }
/*  244:     */     catch (Exception ex)
/*  245:     */     {
/*  246: 373 */       this.m_logPanel.logMessage("[KnowledgeFlow] a problem occurred while trying to create an undo point : " + ex.getMessage());
/*  247:     */     }
/*  248:     */   }
/*  249:     */   
/*  250:     */   protected int getUndoBufferSize()
/*  251:     */   {
/*  252: 385 */     return this.m_undoBuffer.size();
/*  253:     */   }
/*  254:     */   
/*  255:     */   protected void snapSelectedToGrid()
/*  256:     */   {
/*  257: 392 */     if (this.m_selectedSteps.size() > 0) {
/*  258: 393 */       this.m_layout.snapSelectedToGrid();
/*  259:     */     }
/*  260:     */   }
/*  261:     */   
/*  262:     */   protected void initiateAddNote()
/*  263:     */   {
/*  264: 401 */     this.m_layout.initiateAddNote();
/*  265:     */   }
/*  266:     */   
/*  267:     */   public Flow getFlow()
/*  268:     */   {
/*  269: 410 */     return this.m_flow;
/*  270:     */   }
/*  271:     */   
/*  272:     */   public void setFlow(Flow flow)
/*  273:     */   {
/*  274: 419 */     this.m_flow = flow;
/*  275:     */     
/*  276: 421 */     this.m_renderGraph.clear();
/*  277: 422 */     Iterator<StepManagerImpl> iter = this.m_flow.iterator();
/*  278: 423 */     this.m_layout.removeAll();
/*  279: 425 */     while (iter.hasNext())
/*  280:     */     {
/*  281: 426 */       StepManagerImpl manager = (StepManagerImpl)iter.next();
/*  282: 427 */       StepVisual visual = StepVisual.createVisual(manager);
/*  283: 428 */       manager.setStepVisual(visual);
/*  284: 429 */       this.m_renderGraph.add(visual);
/*  285: 430 */       this.m_layout.add(visual);
/*  286:     */     }
/*  287: 433 */     this.m_mainPerspective.getMainToolBar().enableWidget(MainKFPerspectiveToolBar.Widgets.SELECT_ALL_BUTTON.toString(), this.m_flow.size() > 0);
/*  288:     */     
/*  289:     */ 
/*  290:     */ 
/*  291: 437 */     this.m_layout.revalidate();
/*  292: 438 */     this.m_layout.repaint();
/*  293:     */   }
/*  294:     */   
/*  295:     */   protected List<StepVisual> addAll(List<StepManagerImpl> steps)
/*  296:     */   {
/*  297: 449 */     return addAll(steps, true);
/*  298:     */   }
/*  299:     */   
/*  300:     */   protected List<StepVisual> addAll(List<StepManagerImpl> steps, boolean revalidate)
/*  301:     */   {
/*  302: 462 */     List<StepVisual> added = new ArrayList();
/*  303: 463 */     this.m_flow.addAll(steps);
/*  304: 464 */     for (StepManagerImpl s : steps)
/*  305:     */     {
/*  306: 465 */       StepVisual visual = StepVisual.createVisual(s);
/*  307: 466 */       s.setStepVisual(visual);
/*  308: 467 */       added.add(visual);
/*  309: 468 */       this.m_renderGraph.add(visual);
/*  310: 469 */       this.m_layout.add(visual);
/*  311:     */     }
/*  312: 471 */     if (revalidate) {
/*  313: 472 */       this.m_layout.repaint();
/*  314:     */     }
/*  315: 475 */     return added;
/*  316:     */   }
/*  317:     */   
/*  318:     */   protected void addStep(StepManagerImpl manager, int x, int y)
/*  319:     */   {
/*  320: 487 */     this.m_flow.addStep(manager);
/*  321: 488 */     StepVisual visual = StepVisual.createVisual(manager);
/*  322:     */     
/*  323: 490 */     Dimension d = visual.getPreferredSize();
/*  324: 491 */     int dx = (int)(d.getWidth() / 2.0D);
/*  325: 492 */     int dy = (int)(d.getHeight() / 2.0D);
/*  326: 493 */     x -= dx;
/*  327: 494 */     y -= dy;
/*  328: 496 */     if ((x >= 0) && (y >= 0))
/*  329:     */     {
/*  330: 497 */       visual.setX(x);
/*  331: 498 */       visual.setY(y);
/*  332:     */     }
/*  333: 503 */     manager.setStepVisual(visual);
/*  334: 504 */     this.m_renderGraph.add(visual);
/*  335: 505 */     this.m_layout.add(visual);
/*  336: 506 */     visual.setLocation(x, y);
/*  337:     */     
/*  338: 508 */     this.m_mainPerspective.setCursor(Cursor.getPredefinedCursor(0));
/*  339:     */     
/*  340:     */ 
/*  341: 511 */     this.m_mainPerspective.getMainToolBar().enableWidget(MainKFPerspectiveToolBar.Widgets.SELECT_ALL_BUTTON.toString(), this.m_flow.size() > 0);
/*  342:     */   }
/*  343:     */   
/*  344:     */   public void connectSteps(StepManagerImpl source, StepManagerImpl target, String connectionType)
/*  345:     */   {
/*  346: 526 */     if (this.m_mainPerspective.getDebug()) {
/*  347: 527 */       System.err.println("[KF] connecting steps: " + source.getName() + " to " + target.getName());
/*  348:     */     }
/*  349: 530 */     boolean success = this.m_flow.connectSteps(source, target, connectionType);
/*  350: 531 */     if (this.m_mainPerspective.getDebug()) {
/*  351: 532 */       if (success) {
/*  352: 533 */         System.err.println("[KF] connection successful");
/*  353:     */       } else {
/*  354: 535 */         System.err.println("[KF] connection failed");
/*  355:     */       }
/*  356:     */     }
/*  357: 538 */     this.m_layout.repaint();
/*  358:     */   }
/*  359:     */   
/*  360:     */   protected void renameStep(String oldName, String newName)
/*  361:     */   {
/*  362:     */     try
/*  363:     */     {
/*  364: 549 */       this.m_flow.renameStep(oldName, newName);
/*  365:     */     }
/*  366:     */     catch (WekaException ex)
/*  367:     */     {
/*  368: 551 */       this.m_mainPerspective.showErrorDialog(ex);
/*  369:     */     }
/*  370:     */   }
/*  371:     */   
/*  372:     */   protected void removeStep(StepVisual step)
/*  373:     */     throws WekaException
/*  374:     */   {
/*  375: 562 */     this.m_flow.removeStep(step.getStepManager());
/*  376: 563 */     this.m_renderGraph.remove(step);
/*  377: 564 */     this.m_layout.remove(step);
/*  378:     */     
/*  379: 566 */     this.m_layout.repaint();
/*  380:     */   }
/*  381:     */   
/*  382:     */   protected int numSteps()
/*  383:     */   {
/*  384: 575 */     return this.m_renderGraph.size();
/*  385:     */   }
/*  386:     */   
/*  387:     */   public Environment getEnvironment()
/*  388:     */   {
/*  389: 584 */     return this.m_env;
/*  390:     */   }
/*  391:     */   
/*  392:     */   public void setEnvironment(Environment env)
/*  393:     */   {
/*  394: 593 */     this.m_env = env;
/*  395:     */   }
/*  396:     */   
/*  397:     */   public String environmentSubstitute(String source)
/*  398:     */   {
/*  399: 597 */     Environment env = this.m_env != null ? this.m_env : Environment.getSystemWide();
/*  400:     */     try
/*  401:     */     {
/*  402: 599 */       source = env.substitute(source);
/*  403:     */     }
/*  404:     */     catch (Exception ex) {}
/*  405: 602 */     return source;
/*  406:     */   }
/*  407:     */   
/*  408:     */   public FlowExecutor getFlowExecutor()
/*  409:     */   {
/*  410: 611 */     return this.m_flowExecutor;
/*  411:     */   }
/*  412:     */   
/*  413:     */   public void setFlowExecutor(FlowExecutor executor)
/*  414:     */   {
/*  415: 621 */     this.m_flowExecutor = executor;
/*  416:     */   }
/*  417:     */   
/*  418:     */   public File getFilePath()
/*  419:     */   {
/*  420: 630 */     return this.m_filePath;
/*  421:     */   }
/*  422:     */   
/*  423:     */   public void setFilePath(File path)
/*  424:     */   {
/*  425: 639 */     this.m_filePath = (path != null ? path : new File("-NONE-"));
/*  426: 641 */     if (path != null)
/*  427:     */     {
/*  428: 642 */       File absolute = new File(path.getAbsolutePath());
/*  429: 643 */       getEnvironment().addVariable("Internal.knowledgeflow.directory", absolute.getParent());
/*  430:     */     }
/*  431:     */   }
/*  432:     */   
/*  433:     */   public KFLogPanel getLogPanel()
/*  434:     */   {
/*  435: 654 */     return this.m_logPanel;
/*  436:     */   }
/*  437:     */   
/*  438:     */   public int getZoomSetting()
/*  439:     */   {
/*  440: 663 */     return this.m_zoomSetting;
/*  441:     */   }
/*  442:     */   
/*  443:     */   public void setZoomSetting(int zoom)
/*  444:     */   {
/*  445: 672 */     this.m_zoomSetting = zoom;
/*  446:     */   }
/*  447:     */   
/*  448:     */   public boolean getEdited()
/*  449:     */   {
/*  450: 681 */     return this.m_hasBeenEdited;
/*  451:     */   }
/*  452:     */   
/*  453:     */   public void setEdited(boolean edited)
/*  454:     */   {
/*  455: 690 */     this.m_hasBeenEdited = edited;
/*  456:     */     
/*  457:     */ 
/*  458:     */ 
/*  459: 694 */     this.m_mainPerspective.setCurrentTabTitleEditedStatus(edited);
/*  460:     */   }
/*  461:     */   
/*  462:     */   public boolean isExecuting()
/*  463:     */   {
/*  464: 703 */     return this.m_isExecuting;
/*  465:     */   }
/*  466:     */   
/*  467:     */   protected LayoutOperation getFlowLayoutOperation()
/*  468:     */   {
/*  469: 712 */     return this.m_userOpp;
/*  470:     */   }
/*  471:     */   
/*  472:     */   protected void setFlowLayoutOperation(LayoutOperation mode)
/*  473:     */   {
/*  474: 721 */     this.m_userOpp = mode;
/*  475:     */   }
/*  476:     */   
/*  477:     */   public synchronized void executeFlow(boolean sequential)
/*  478:     */     throws WekaException
/*  479:     */   {
/*  480: 732 */     if (isExecuting()) {
/*  481: 733 */       throw new WekaException("The flow is already executing!");
/*  482:     */     }
/*  483: 735 */     if (this.m_flowExecutor == null)
/*  484:     */     {
/*  485: 736 */       this.m_flowExecutor = new FlowRunner(this.m_mainPerspective.getMainApplication().getApplicationSettings());
/*  486:     */       
/*  487:     */ 
/*  488: 739 */       this.m_flowExecutor.setLogger(this.m_logPanel);
/*  489:     */     }
/*  490: 741 */     this.m_flowExecutor.setSettings(this.m_mainPerspective.getMainApplication().getApplicationSettings());
/*  491:     */     
/*  492: 743 */     this.m_mainPerspective.getMainToolBar().disableWidgets(new String[] { MainKFPerspectiveToolBar.Widgets.PLAY_PARALLEL_BUTTON.toString(), MainKFPerspectiveToolBar.Widgets.PLAY_SEQUENTIAL_BUTTON.toString() });
/*  493:     */     
/*  494:     */ 
/*  495: 746 */     this.m_mainPerspective.getMainToolBar().enableWidgets(new String[] { MainKFPerspectiveToolBar.Widgets.STOP_BUTTON.toString() });
/*  496:     */     
/*  497:     */ 
/*  498: 749 */     this.m_flowExecutor.getExecutionEnvironment().setEnvironmentVariables(this.m_env);
/*  499: 750 */     this.m_isExecuting = true;
/*  500: 751 */     this.m_flowExecutor.addExecutionFinishedCallback(new ExecutionFinishedCallback()
/*  501:     */     {
/*  502:     */       public void executionFinished()
/*  503:     */       {
/*  504: 755 */         VisibleLayout.this.m_isExecuting = false;
/*  505: 756 */         VisibleLayout.this.m_logPanel.statusMessage("@!@[KnowledgeFlow]|OK.");
/*  506: 757 */         if (VisibleLayout.this.m_flowExecutor.wasStopped()) {
/*  507: 758 */           VisibleLayout.this.m_logPanel.setMessageOnAll(false, "Stopped.");
/*  508:     */         }
/*  509: 760 */         VisibleLayout.this.m_mainPerspective.getMainToolBar().enableWidgets(new String[] { MainKFPerspectiveToolBar.Widgets.PLAY_PARALLEL_BUTTON.toString(), MainKFPerspectiveToolBar.Widgets.PLAY_SEQUENTIAL_BUTTON.toString() });
/*  510:     */         
/*  511:     */ 
/*  512: 763 */         VisibleLayout.this.m_mainPerspective.getMainToolBar().disableWidgets(new String[] { MainKFPerspectiveToolBar.Widgets.STOP_BUTTON.toString() });
/*  513:     */       }
/*  514: 767 */     });
/*  515: 768 */     this.m_flowExecutor.setFlow(this.m_flow);
/*  516: 769 */     this.m_logPanel.clearStatus();
/*  517: 770 */     this.m_logPanel.statusMessage("@!@[KnowledgeFlow]|Executing...");
/*  518: 771 */     if (sequential) {
/*  519: 772 */       this.m_flowExecutor.runSequentially();
/*  520:     */     } else {
/*  521: 774 */       this.m_flowExecutor.runParallel();
/*  522:     */     }
/*  523:     */   }
/*  524:     */   
/*  525:     */   public void stopFlow()
/*  526:     */   {
/*  527: 782 */     if (isExecuting()) {
/*  528: 783 */       this.m_flowExecutor.stopProcessing();
/*  529:     */     }
/*  530:     */   }
/*  531:     */   
/*  532:     */   protected StepVisual findStep(Point p)
/*  533:     */   {
/*  534: 796 */     Rectangle tempBounds = new Rectangle();
/*  535: 797 */     for (StepVisual v : this.m_renderGraph)
/*  536:     */     {
/*  537: 798 */       tempBounds = v.getBounds();
/*  538: 799 */       if (tempBounds.contains(p)) {
/*  539: 800 */         return v;
/*  540:     */       }
/*  541:     */     }
/*  542: 804 */     return null;
/*  543:     */   }
/*  544:     */   
/*  545:     */   protected List<StepVisual> findSteps(Rectangle boundingBox)
/*  546:     */   {
/*  547: 814 */     List<StepVisual> steps = new ArrayList();
/*  548: 816 */     for (StepVisual v : this.m_renderGraph)
/*  549:     */     {
/*  550: 817 */       int centerX = v.getX() + v.getWidth() / 2;
/*  551: 818 */       int centerY = v.getY() + v.getHeight() / 2;
/*  552: 819 */       if (boundingBox.contains(centerX, centerY)) {
/*  553: 820 */         steps.add(v);
/*  554:     */       }
/*  555:     */     }
/*  556: 824 */     return steps;
/*  557:     */   }
/*  558:     */   
/*  559:     */   protected List<StepManagerImpl> findStepsThatCanAcceptConnection(String connectionName)
/*  560:     */   {
/*  561: 837 */     List<StepManagerImpl> result = new ArrayList();
/*  562: 838 */     for (StepManagerImpl step : this.m_flow.getSteps())
/*  563:     */     {
/*  564: 839 */       List<String> incomingConnNames = step.getManagedStep().getIncomingConnectionTypes();
/*  565: 841 */       if ((incomingConnNames != null) && (incomingConnNames.contains(connectionName))) {
/*  566: 843 */         result.add(step);
/*  567:     */       }
/*  568:     */     }
/*  569: 847 */     return result;
/*  570:     */   }
/*  571:     */   
/*  572:     */   protected Map<String, List<StepManagerImpl[]>> findClosestConnections(Point point, int delta)
/*  573:     */   {
/*  574: 862 */     Map<String, List<StepManagerImpl[]>> closestConnections = new HashMap();
/*  575: 865 */     for (Iterator i$ = this.m_flow.getSteps().iterator(); i$.hasNext();)
/*  576:     */     {
/*  577: 865 */       sourceManager = (StepManagerImpl)i$.next();
/*  578: 866 */       for (i$ = sourceManager.getOutgoingConnections().entrySet().iterator(); i$.hasNext();)
/*  579:     */       {
/*  580: 866 */         outCons = (Map.Entry)i$.next();
/*  581:     */         
/*  582: 868 */         List<StepManager> targetsOfConnType = (List)outCons.getValue();
/*  583: 869 */         for (StepManager target : targetsOfConnType)
/*  584:     */         {
/*  585: 870 */           StepManagerImpl targetManager = (StepManagerImpl)target;
/*  586: 871 */           String connName = (String)outCons.getKey();
/*  587: 872 */           StepVisual sourceVisual = sourceManager.getStepVisual();
/*  588: 873 */           StepVisual targetVisual = targetManager.getStepVisual();
/*  589: 874 */           Point bestSourcePt = sourceVisual.getClosestConnectorPoint(new Point(targetVisual.getX() + targetVisual.getWidth() / 2, targetVisual.getY() + targetVisual.getHeight() / 2));
/*  590:     */           
/*  591:     */ 
/*  592:     */ 
/*  593: 878 */           Point bestTargetPt = targetVisual.getClosestConnectorPoint(new Point(sourceVisual.getX() + sourceVisual.getWidth() / 2, sourceVisual.getY() + sourceVisual.getHeight() / 2));
/*  594:     */           
/*  595:     */ 
/*  596:     */ 
/*  597:     */ 
/*  598: 883 */           int minx = (int)Math.min(bestSourcePt.getX(), bestTargetPt.getX());
/*  599: 884 */           int maxx = (int)Math.max(bestSourcePt.getX(), bestTargetPt.getX());
/*  600: 885 */           int miny = (int)Math.min(bestSourcePt.getY(), bestTargetPt.getY());
/*  601: 886 */           int maxy = (int)Math.max(bestSourcePt.getY(), bestTargetPt.getY());
/*  602: 889 */           if ((point.getX() >= minx - delta) && (point.getX() <= maxx + delta) && (point.getY() >= miny - delta) && (point.getY() <= maxy + delta))
/*  603:     */           {
/*  604: 893 */             double a = bestSourcePt.getY() - bestTargetPt.getY();
/*  605: 894 */             double b = bestTargetPt.getX() - bestSourcePt.getX();
/*  606: 895 */             double c = bestSourcePt.getX() * bestTargetPt.getY() - bestTargetPt.getX() * bestSourcePt.getY();
/*  607:     */             
/*  608:     */ 
/*  609:     */ 
/*  610: 899 */             double distance = Math.abs(a * point.getX() + b * point.getY() + c);
/*  611:     */             
/*  612: 901 */             distance /= Math.abs(Math.sqrt(a * a + b * b));
/*  613: 903 */             if (distance <= delta)
/*  614:     */             {
/*  615: 904 */               List<StepManagerImpl[]> conList = (List)closestConnections.get(connName);
/*  616: 906 */               if (conList == null)
/*  617:     */               {
/*  618: 907 */                 conList = new ArrayList();
/*  619: 908 */                 closestConnections.put(connName, conList);
/*  620:     */               }
/*  621: 910 */               StepManagerImpl[] conn = { sourceManager, targetManager };
/*  622:     */               
/*  623: 912 */               conList.add(conn);
/*  624:     */             }
/*  625:     */           }
/*  626:     */         }
/*  627:     */       }
/*  628:     */     }
/*  629:     */     StepManagerImpl sourceManager;
/*  630:     */     Iterator i$;
/*  631:     */     Map.Entry<String, List<StepManager>> outCons;
/*  632: 919 */     return closestConnections;
/*  633:     */   }
/*  634:     */   
/*  635:     */   protected boolean previousConn(Map<String, List<StepManager>> outConns, StepManagerImpl target, int index)
/*  636:     */   {
/*  637: 934 */     boolean result = false;
/*  638:     */     
/*  639: 936 */     int count = 0;
/*  640: 937 */     for (Map.Entry<String, List<StepManager>> e : outConns.entrySet())
/*  641:     */     {
/*  642: 938 */       List<StepManager> connectedSteps = (List)e.getValue();
/*  643: 939 */       for (StepManager c : connectedSteps)
/*  644:     */       {
/*  645: 940 */         StepManagerImpl cI = (StepManagerImpl)c;
/*  646: 941 */         if ((target.getManagedStep() == cI.getManagedStep()) && (count < index))
/*  647:     */         {
/*  648: 942 */           result = true;
/*  649: 943 */           break;
/*  650:     */         }
/*  651:     */       }
/*  652: 947 */       if (result) {
/*  653:     */         break;
/*  654:     */       }
/*  655: 950 */       count++;
/*  656:     */     }
/*  657: 953 */     return result;
/*  658:     */   }
/*  659:     */   
/*  660:     */   private void setUpLogPanel(final LogPanel logPanel)
/*  661:     */   {
/*  662: 957 */     String date = new SimpleDateFormat("EEEE, d MMMM yyyy").format(new Date());
/*  663:     */     
/*  664: 959 */     logPanel.logMessage("Weka Knowledge Flow was written by Mark Hall");
/*  665: 960 */     logPanel.logMessage("Weka Knowledge Flow");
/*  666: 961 */     logPanel.logMessage("(c) 2002-" + Copyright.getToYear() + " " + Copyright.getOwner() + ", " + Copyright.getAddress());
/*  667:     */     
/*  668: 963 */     logPanel.logMessage("web: " + Copyright.getURL());
/*  669: 964 */     logPanel.logMessage(date);
/*  670: 965 */     logPanel.statusMessage("@!@[KnowledgeFlow]|Welcome to the Weka Knowledge Flow");
/*  671:     */     
/*  672: 967 */     logPanel.getStatusTable().addMouseListener(new MouseAdapter()
/*  673:     */     {
/*  674:     */       public void mouseClicked(MouseEvent e)
/*  675:     */       {
/*  676: 970 */         if ((logPanel.getStatusTable().rowAtPoint(e.getPoint()) == 0) && (
/*  677: 971 */           ((e.getModifiers() & 0x10) != 16) || (e.isAltDown())))
/*  678:     */         {
/*  679: 973 */           System.gc();
/*  680: 974 */           Runtime currR = Runtime.getRuntime();
/*  681: 975 */           long freeM = currR.freeMemory();
/*  682: 976 */           long totalM = currR.totalMemory();
/*  683: 977 */           long maxM = currR.maxMemory();
/*  684: 978 */           logPanel.logMessage("[KnowledgeFlow] Memory (free/total/max.) in bytes: " + String.format("%,d", new Object[] { Long.valueOf(freeM) }) + " / " + String.format("%,d", new Object[] { Long.valueOf(totalM) }) + " / " + String.format("%,d", new Object[] { Long.valueOf(maxM) }));
/*  685:     */           
/*  686:     */ 
/*  687:     */ 
/*  688:     */ 
/*  689: 983 */           logPanel.statusMessage("@!@[KnowledgeFlow]|Memory (free/total/max.) in bytes: " + String.format("%,d", new Object[] { Long.valueOf(freeM) }) + " / " + String.format("%,d", new Object[] { Long.valueOf(totalM) }) + " / " + String.format("%,d", new Object[] { Long.valueOf(maxM) }));
/*  690:     */         }
/*  691:     */       }
/*  692:     */     });
/*  693:     */   }
/*  694:     */   
/*  695:     */   protected void saveLayout(boolean showDialog)
/*  696:     */   {
/*  697:1002 */     boolean shownDialog = false;
/*  698:1003 */     int returnVal = 0;
/*  699:1004 */     File sFile = getFilePath();
/*  700:1005 */     if ((showDialog) || (sFile.getName().equals("-NONE-")))
/*  701:     */     {
/*  702:1006 */       returnVal = this.m_mainPerspective.m_FileChooser.showSaveDialog(this);
/*  703:1007 */       shownDialog = true;
/*  704:     */     }
/*  705:1010 */     if (returnVal == 0)
/*  706:     */     {
/*  707:1011 */       if (shownDialog) {
/*  708:1012 */         sFile = this.m_mainPerspective.m_FileChooser.getSelectedFile();
/*  709:     */       }
/*  710:1016 */       if (!sFile.getName().toLowerCase().endsWith(".kf")) {
/*  711:1017 */         sFile = new File(sFile.getParent(), sFile.getName() + ".kf");
/*  712:     */       }
/*  713:     */       try
/*  714:     */       {
/*  715:1021 */         String fName = sFile.getName();
/*  716:1022 */         if (fName.indexOf(".") > 0) {
/*  717:1023 */           fName = fName.substring(0, fName.lastIndexOf('.'));
/*  718:     */         }
/*  719:1025 */         this.m_flow.setFlowName(fName.replace(".kf", ""));
/*  720:1026 */         this.m_flow.saveFlow(sFile);
/*  721:1027 */         setFilePath(sFile);
/*  722:1028 */         setEdited(false);
/*  723:1029 */         this.m_mainPerspective.setCurrentTabTitle(fName);
/*  724:     */       }
/*  725:     */       catch (WekaException e)
/*  726:     */       {
/*  727:1031 */         this.m_mainPerspective.showErrorDialog(e);
/*  728:     */       }
/*  729:     */     }
/*  730:     */   }
/*  731:     */   
/*  732:     */   protected void popAndLoadUndo()
/*  733:     */   {
/*  734:1040 */     if (this.m_undoBuffer.size() > 0)
/*  735:     */     {
/*  736:1041 */       File undo = (File)this.m_undoBuffer.pop();
/*  737:1042 */       if (this.m_undoBuffer.size() == 0) {
/*  738:1043 */         this.m_mainPerspective.getMainToolBar().disableWidgets(new String[] { MainKFPerspectiveToolBar.Widgets.UNDO_BUTTON.toString() });
/*  739:     */       }
/*  740:1046 */       loadLayout(undo, true);
/*  741:     */     }
/*  742:     */   }
/*  743:     */   
/*  744:     */   protected void loadLayout(File fFile, boolean isUndo)
/*  745:     */   {
/*  746:1057 */     stopFlow();
/*  747:     */     
/*  748:1059 */     this.m_mainPerspective.getMainToolBar().disableWidgets(new String[] { MainKFPerspectiveToolBar.Widgets.PLAY_PARALLEL_BUTTON.toString(), MainKFPerspectiveToolBar.Widgets.PLAY_SEQUENTIAL_BUTTON.toString(), MainKFPerspectiveToolBar.Widgets.SAVE_FLOW_BUTTON.toString(), MainKFPerspectiveToolBar.Widgets.SAVE_FLOW_AS_BUTTON.toString() });
/*  749:1065 */     if (!isUndo)
/*  750:     */     {
/*  751:1066 */       File absolute = new File(fFile.getAbsolutePath());
/*  752:1067 */       getEnvironment().addVariable("Internal.knowledgeflow.directory", absolute.getParent());
/*  753:     */     }
/*  754:     */     try
/*  755:     */     {
/*  756:1072 */       Flow flow = Flow.loadFlow(fFile, this.m_logPanel);
/*  757:1073 */       setFlow(flow);
/*  758:1074 */       if (!isUndo) {
/*  759:1075 */         setFilePath(fFile);
/*  760:     */       }
/*  761:1078 */       if (!getFlow().getFlowName().equals("Untitled")) {
/*  762:1079 */         this.m_mainPerspective.setCurrentTabTitle(getFlow().getFlowName());
/*  763:     */       }
/*  764:     */     }
/*  765:     */     catch (WekaException e)
/*  766:     */     {
/*  767:1083 */       this.m_logPanel.statusMessage("@!@[KnowledgeFlow]|Unable to load flow (see log).");
/*  768:     */       
/*  769:1085 */       this.m_logPanel.logMessage("[KnowledgeFlow] Unable to load flow\n" + LogManager.stackTraceToString(e));
/*  770:     */       
/*  771:1087 */       this.m_mainPerspective.showErrorDialog(e);
/*  772:     */     }
/*  773:1090 */     this.m_mainPerspective.getMainToolBar().enableWidgets(new String[] { MainKFPerspectiveToolBar.Widgets.PLAY_PARALLEL_BUTTON.toString(), MainKFPerspectiveToolBar.Widgets.PLAY_SEQUENTIAL_BUTTON.toString(), MainKFPerspectiveToolBar.Widgets.SAVE_FLOW_BUTTON.toString(), MainKFPerspectiveToolBar.Widgets.SAVE_FLOW_AS_BUTTON.toString() });
/*  774:     */     
/*  775:     */ 
/*  776:     */ 
/*  777:     */ 
/*  778:     */ 
/*  779:1096 */     this.m_mainPerspective.getMainToolBar().enableWidget(MainKFPerspectiveToolBar.Widgets.SELECT_ALL_BUTTON.toString(), this.m_flow.size() > 0);
/*  780:     */   }
/*  781:     */   
/*  782:     */   protected static enum LayoutOperation
/*  783:     */   {
/*  784:1105 */     NONE,  MOVING,  CONNECTING,  ADDING,  SELECTING,  PASTING;
/*  785:     */     
/*  786:     */     private LayoutOperation() {}
/*  787:     */   }
/*  788:     */   
/*  789:     */   protected class KFLogPanel
/*  790:     */     extends LogPanel
/*  791:     */   {
/*  792:     */     private static final long serialVersionUID = -2224509243343105276L;
/*  793:     */     
/*  794:     */     protected KFLogPanel() {}
/*  795:     */     
/*  796:     */     public synchronized void setMessageOnAll(boolean mainKFLine, String message)
/*  797:     */     {
/*  798:1118 */       for (String key : this.m_tableIndexes.keySet()) {
/*  799:1119 */         if ((mainKFLine) || (!key.equals("[KnowledgeFlow]")))
/*  800:     */         {
/*  801:1123 */           String tm = key + "|" + message;
/*  802:1124 */           statusMessage(tm);
/*  803:     */         }
/*  804:     */       }
/*  805:     */     }
/*  806:     */   }
/*  807:     */   
/*  808:     */   public static String serializeStepsToJSON(List<StepVisual> steps, String name)
/*  809:     */     throws WekaException
/*  810:     */   {
/*  811:1141 */     if (steps.size() > 0)
/*  812:     */     {
/*  813:1142 */       List<StepManagerImpl> toCopy = new ArrayList();
/*  814:1143 */       for (StepVisual s : steps) {
/*  815:1144 */         toCopy.add(s.getStepManager());
/*  816:     */       }
/*  817:1146 */       Flow temp = new Flow();
/*  818:1147 */       temp.setFlowName("Clipboard copy");
/*  819:1148 */       temp.addAll(toCopy);
/*  820:     */       
/*  821:1150 */       return JSONFlowUtils.flowToJSON(temp);
/*  822:     */     }
/*  823:1153 */     throw new WekaException("No steps to serialize!");
/*  824:     */   }
/*  825:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.VisibleLayout
 * JD-Core Version:    0.7.0.1
 */