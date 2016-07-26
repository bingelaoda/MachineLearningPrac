/*    1:     */ package weka.gui.knowledgeflow;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.awt.Container;
/*    5:     */ import java.awt.Cursor;
/*    6:     */ import java.awt.Dimension;
/*    7:     */ import java.awt.Font;
/*    8:     */ import java.awt.MenuItem;
/*    9:     */ import java.awt.PopupMenu;
/*   10:     */ import java.awt.event.ActionEvent;
/*   11:     */ import java.awt.event.ActionListener;
/*   12:     */ import java.awt.event.WindowAdapter;
/*   13:     */ import java.awt.event.WindowEvent;
/*   14:     */ import java.io.InputStream;
/*   15:     */ import java.io.InputStreamReader;
/*   16:     */ import java.io.LineNumberReader;
/*   17:     */ import java.util.ArrayList;
/*   18:     */ import java.util.HashMap;
/*   19:     */ import java.util.LinkedHashMap;
/*   20:     */ import java.util.List;
/*   21:     */ import java.util.Map;
/*   22:     */ import java.util.Map.Entry;
/*   23:     */ import javax.swing.AbstractAction;
/*   24:     */ import javax.swing.Action;
/*   25:     */ import javax.swing.ActionMap;
/*   26:     */ import javax.swing.BorderFactory;
/*   27:     */ import javax.swing.ImageIcon;
/*   28:     */ import javax.swing.InputMap;
/*   29:     */ import javax.swing.JButton;
/*   30:     */ import javax.swing.JCheckBox;
/*   31:     */ import javax.swing.JComponent;
/*   32:     */ import javax.swing.JFrame;
/*   33:     */ import javax.swing.JMenu;
/*   34:     */ import javax.swing.JMenuItem;
/*   35:     */ import javax.swing.JOptionPane;
/*   36:     */ import javax.swing.JPanel;
/*   37:     */ import javax.swing.JScrollPane;
/*   38:     */ import javax.swing.JTextArea;
/*   39:     */ import javax.swing.JToggleButton;
/*   40:     */ import javax.swing.JToolBar;
/*   41:     */ import javax.swing.KeyStroke;
/*   42:     */ import weka.core.Utils;
/*   43:     */ import weka.core.WekaException;
/*   44:     */ import weka.gui.GUIApplication;
/*   45:     */ import weka.knowledgeflow.Flow;
/*   46:     */ 
/*   47:     */ public class MainKFPerspectiveToolBar
/*   48:     */   extends JPanel
/*   49:     */ {
/*   50:     */   private static final long serialVersionUID = -157986423490835642L;
/*   51:  55 */   public static String ICON_PATH = "weka/gui/knowledgeflow/icons/";
/*   52:     */   protected MainKFPerspective m_mainPerspective;
/*   53:  61 */   protected Map<String, JComponent> m_widgetMap = new HashMap();
/*   54:  65 */   protected Map<String, JMenu> m_menuMap = new LinkedHashMap();
/*   55:  68 */   protected Map<String, JMenuItem> m_menuItemMap = new HashMap();
/*   56:     */   protected boolean m_showMenus;
/*   57:     */   
/*   58:     */   public MainKFPerspectiveToolBar(MainKFPerspective mainKFPerspective)
/*   59:     */   {
/*   60:  82 */     JMenu fileMenu = new JMenu();
/*   61:  83 */     fileMenu.setText("File");
/*   62:  84 */     this.m_menuMap.put("File", fileMenu);
/*   63:  85 */     JMenu editMenu = new JMenu();
/*   64:  86 */     editMenu.setText("Edit");
/*   65:  87 */     this.m_menuMap.put("Edit", editMenu);
/*   66:  88 */     JMenu insertMenu = new JMenu();
/*   67:  89 */     insertMenu.setText("Insert");
/*   68:  90 */     this.m_menuMap.put("Insert", insertMenu);
/*   69:  91 */     JMenu viewMenu = new JMenu();
/*   70:  92 */     viewMenu.setText("View");
/*   71:  93 */     this.m_menuMap.put("View", viewMenu);
/*   72:     */     
/*   73:  95 */     this.m_mainPerspective = mainKFPerspective;
/*   74:  96 */     setLayout(new BorderLayout());
/*   75:     */     
/*   76:     */ 
/*   77:  99 */     Action closeAction = new AbstractAction("Close")
/*   78:     */     {
/*   79:     */       private static final long serialVersionUID = 4762166880144590384L;
/*   80:     */       
/*   81:     */       public void actionPerformed(ActionEvent e)
/*   82:     */       {
/*   83: 105 */         if (MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentTabIndex() >= 0) {
/*   84: 106 */           MainKFPerspectiveToolBar.this.m_mainPerspective.removeTab(MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentTabIndex());
/*   85:     */         }
/*   86:     */       }
/*   87: 109 */     };
/*   88: 110 */     KeyStroke closeKey = KeyStroke.getKeyStroke(87, 128);
/*   89:     */     
/*   90: 112 */     this.m_mainPerspective.getActionMap().put("Close", closeAction);
/*   91: 113 */     this.m_mainPerspective.getInputMap(2).put(closeKey, "Close");
/*   92:     */     
/*   93:     */ 
/*   94: 116 */     setupLeftSideToolBar();
/*   95: 117 */     setupRightSideToolBar();
/*   96:     */   }
/*   97:     */   
/*   98:     */   private void setupLeftSideToolBar()
/*   99:     */   {
/*  100: 121 */     JToolBar fixedTools2 = new JToolBar();
/*  101: 122 */     fixedTools2.setOrientation(0);
/*  102: 123 */     fixedTools2.setFloatable(false);
/*  103:     */     
/*  104: 125 */     JButton playB = new JButton(new ImageIcon(StepVisual.loadIcon(ICON_PATH + "resultset_next.png").getImage()));
/*  105:     */     
/*  106:     */ 
/*  107: 128 */     playB.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
/*  108: 129 */     playB.setToolTipText("Run this flow (all start points launched in parallel)");
/*  109:     */     
/*  110: 131 */     final Action playParallelAction = new AbstractAction()
/*  111:     */     {
/*  112:     */       public void actionPerformed(ActionEvent e)
/*  113:     */       {
/*  114: 134 */         if (MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout() != null)
/*  115:     */         {
/*  116: 135 */           boolean proceed = true;
/*  117: 136 */           if (MainKFPerspectiveToolBar.this.m_mainPerspective.isMemoryLow()) {
/*  118: 137 */             proceed = MainKFPerspectiveToolBar.this.m_mainPerspective.showMemoryIsLow();
/*  119:     */           }
/*  120: 139 */           if (proceed) {
/*  121:     */             try
/*  122:     */             {
/*  123: 141 */               MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout().executeFlow(false);
/*  124:     */             }
/*  125:     */             catch (WekaException e1)
/*  126:     */             {
/*  127: 143 */               MainKFPerspectiveToolBar.this.m_mainPerspective.showErrorDialog(e1);
/*  128:     */             }
/*  129:     */           }
/*  130:     */         }
/*  131:     */       }
/*  132: 148 */     };
/*  133: 149 */     playB.addActionListener(new ActionListener()
/*  134:     */     {
/*  135:     */       public void actionPerformed(ActionEvent e)
/*  136:     */       {
/*  137: 152 */         playParallelAction.actionPerformed(e);
/*  138:     */       }
/*  139: 155 */     });
/*  140: 156 */     JButton playBB = new JButton(new ImageIcon(StepVisual.loadIcon(ICON_PATH + "resultset_last.png").getImage()));
/*  141:     */     
/*  142:     */ 
/*  143: 159 */     playBB.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
/*  144: 160 */     playBB.setToolTipText("Run this flow (start points launched sequentially)");
/*  145: 161 */     final Action playSequentialAction = new AbstractAction()
/*  146:     */     {
/*  147:     */       public void actionPerformed(ActionEvent e)
/*  148:     */       {
/*  149: 164 */         if (MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout() != null)
/*  150:     */         {
/*  151: 165 */           if (!Utils.getDontShowDialog("weka.gui.knowledgeflow.SequentialRunInfo"))
/*  152:     */           {
/*  153: 167 */             JCheckBox dontShow = new JCheckBox("Do not show this message again");
/*  154:     */             
/*  155: 169 */             Object[] stuff = new Object[2];
/*  156: 170 */             stuff[0] = "The order that data sources are launched in can be\nspecified by setting a custom name for each data source that\nthat includes a number. E.g. \"1:MyArffLoader\". To set a name,\nright-click over a data source and select \"Set name\"\n\nIf the prefix is not specified, then the order of execution\nwill correspond to the order that the components were added\nto the layout. Note that it is also possible to prevent a data\nsource from executing by prefixing its name with a \"!\". E.g\n\"!:MyArffLoader\"";
/*  157:     */             
/*  158:     */ 
/*  159:     */ 
/*  160:     */ 
/*  161:     */ 
/*  162:     */ 
/*  163:     */ 
/*  164:     */ 
/*  165:     */ 
/*  166: 180 */             stuff[1] = dontShow;
/*  167:     */             
/*  168: 182 */             JOptionPane.showMessageDialog(MainKFPerspectiveToolBar.this.m_mainPerspective, stuff, "Sequential execution information", 0);
/*  169: 185 */             if (dontShow.isSelected()) {
/*  170:     */               try
/*  171:     */               {
/*  172: 187 */                 Utils.setDontShowDialog("weka.gui.knowledgeFlow.SequentialRunInfo");
/*  173:     */               }
/*  174:     */               catch (Exception e1) {}
/*  175:     */             }
/*  176:     */           }
/*  177: 194 */           boolean proceed = true;
/*  178: 195 */           if (MainKFPerspectiveToolBar.this.m_mainPerspective.isMemoryLow()) {
/*  179: 196 */             proceed = MainKFPerspectiveToolBar.this.m_mainPerspective.showMemoryIsLow();
/*  180:     */           }
/*  181: 198 */           if (proceed) {
/*  182:     */             try
/*  183:     */             {
/*  184: 200 */               MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout().executeFlow(true);
/*  185:     */             }
/*  186:     */             catch (WekaException e1)
/*  187:     */             {
/*  188: 202 */               MainKFPerspectiveToolBar.this.m_mainPerspective.showErrorDialog(e1);
/*  189:     */             }
/*  190:     */           }
/*  191:     */         }
/*  192:     */       }
/*  193: 207 */     };
/*  194: 208 */     playBB.addActionListener(new ActionListener()
/*  195:     */     {
/*  196:     */       public void actionPerformed(ActionEvent e)
/*  197:     */       {
/*  198: 211 */         playSequentialAction.actionPerformed(e);
/*  199:     */       }
/*  200: 214 */     });
/*  201: 215 */     JButton stopB = new JButton(new ImageIcon(StepVisual.loadIcon(ICON_PATH + "shape_square.png").getImage()));
/*  202:     */     
/*  203:     */ 
/*  204: 218 */     stopB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/*  205: 219 */     stopB.setToolTipText("Stop all execution");
/*  206: 220 */     final Action stopAction = new AbstractAction()
/*  207:     */     {
/*  208:     */       public void actionPerformed(ActionEvent e)
/*  209:     */       {
/*  210: 223 */         if (MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout() != null)
/*  211:     */         {
/*  212: 224 */           MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout().getLogPanel().statusMessage("@!@[KnowledgeFlow]|Attempting to stop all components...");
/*  213:     */           
/*  214:     */ 
/*  215:     */ 
/*  216:     */ 
/*  217: 229 */           MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout().stopFlow();
/*  218: 230 */           MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout().getLogPanel().statusMessage("@!@[KnowledgeFlow]|OK.");
/*  219:     */         }
/*  220:     */       }
/*  221: 235 */     };
/*  222: 236 */     stopB.addActionListener(new ActionListener()
/*  223:     */     {
/*  224:     */       public void actionPerformed(ActionEvent e)
/*  225:     */       {
/*  226: 239 */         stopAction.actionPerformed(e);
/*  227:     */       }
/*  228: 242 */     });
/*  229: 243 */     JButton pointerB = new JButton(new ImageIcon(StepVisual.loadIcon(ICON_PATH + "cursor.png").getImage()));
/*  230:     */     
/*  231: 245 */     pointerB.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
/*  232: 246 */     pointerB.addActionListener(new ActionListener()
/*  233:     */     {
/*  234:     */       public void actionPerformed(ActionEvent e)
/*  235:     */       {
/*  236: 249 */         MainKFPerspectiveToolBar.this.m_mainPerspective.setPalleteSelectedStep(null);
/*  237: 250 */         MainKFPerspectiveToolBar.this.m_mainPerspective.setCursor(Cursor.getPredefinedCursor(0));
/*  238:     */         
/*  239:     */ 
/*  240: 253 */         MainKFPerspectiveToolBar.this.m_mainPerspective.clearDesignPaletteSelection();
/*  241: 255 */         if (MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout() != null) {
/*  242: 256 */           MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout().setFlowLayoutOperation(VisibleLayout.LayoutOperation.NONE);
/*  243:     */         }
/*  244:     */       }
/*  245: 261 */     });
/*  246: 262 */     addWidgetToToolBar(fixedTools2, Widgets.POINTER_BUTTON.toString(), pointerB);
/*  247: 263 */     addWidgetToToolBar(fixedTools2, Widgets.PLAY_PARALLEL_BUTTON.toString(), playB);
/*  248:     */     
/*  249: 265 */     addWidgetToToolBar(fixedTools2, Widgets.PLAY_SEQUENTIAL_BUTTON.toString(), playBB);
/*  250:     */     
/*  251: 267 */     addWidgetToToolBar(fixedTools2, Widgets.STOP_BUTTON.toString(), stopB);
/*  252: 268 */     Dimension d = playB.getPreferredSize();
/*  253: 269 */     Dimension d2 = fixedTools2.getMinimumSize();
/*  254: 270 */     Dimension d3 = new Dimension(d2.width, d.height + 4);
/*  255: 271 */     fixedTools2.setPreferredSize(d3);
/*  256: 272 */     fixedTools2.setMaximumSize(d3);
/*  257: 273 */     add(fixedTools2, "West");
/*  258:     */   }
/*  259:     */   
/*  260:     */   private void setupRightSideToolBar()
/*  261:     */   {
/*  262: 277 */     JToolBar fixedTools = new JToolBar();
/*  263: 278 */     fixedTools.setOrientation(0);
/*  264:     */     
/*  265: 280 */     JButton cutB = new JButton(new ImageIcon(StepVisual.loadIcon(ICON_PATH + "cut.png").getImage()));
/*  266:     */     
/*  267: 282 */     cutB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/*  268: 283 */     cutB.setToolTipText("Cut selected (Ctrl+X)");
/*  269: 284 */     JButton copyB = new JButton(new ImageIcon(StepVisual.loadIcon(ICON_PATH + "page_copy.png").getImage()));
/*  270:     */     
/*  271:     */ 
/*  272: 287 */     copyB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/*  273: 288 */     copyB.setToolTipText("Copy selected (Ctrl+C)");
/*  274: 289 */     JButton pasteB = new JButton(new ImageIcon(StepVisual.loadIcon(ICON_PATH + "paste_plain.png").getImage()));
/*  275:     */     
/*  276:     */ 
/*  277: 292 */     pasteB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/*  278: 293 */     pasteB.setToolTipText("Paste from clipboard (Ctrl+V)");
/*  279: 294 */     JButton deleteB = new JButton(new ImageIcon(StepVisual.loadIcon(ICON_PATH + "delete.png").getImage()));
/*  280:     */     
/*  281: 296 */     deleteB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/*  282: 297 */     deleteB.setToolTipText("Delete selected (DEL)");
/*  283: 298 */     final JToggleButton snapToGridB = new JToggleButton(new ImageIcon(StepVisual.loadIcon(ICON_PATH + "shape_handles.png").getImage()));
/*  284:     */     
/*  285:     */ 
/*  286:     */ 
/*  287: 302 */     snapToGridB.setToolTipText("Snap to grid (Ctrl+G)");
/*  288: 303 */     JButton saveB = new JButton(new ImageIcon(StepVisual.loadIcon(ICON_PATH + "disk.png").getImage()));
/*  289:     */     
/*  290: 305 */     saveB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/*  291: 306 */     saveB.setToolTipText("Save layout (Ctrl+S)");
/*  292: 307 */     JButton saveBB = new JButton(new ImageIcon(StepVisual.loadIcon(ICON_PATH + "disk_multiple.png").getImage()));
/*  293:     */     
/*  294:     */ 
/*  295: 310 */     saveBB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/*  296: 311 */     saveBB.setToolTipText("Save layout with new name");
/*  297: 312 */     JButton loadB = new JButton(new ImageIcon(StepVisual.loadIcon(ICON_PATH + "folder_add.png").getImage()));
/*  298:     */     
/*  299:     */ 
/*  300: 315 */     loadB.setToolTipText("Open (Ctrl+O)");
/*  301: 316 */     loadB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/*  302: 317 */     JButton newB = new JButton(new ImageIcon(StepVisual.loadIcon(ICON_PATH + "page_add.png").getImage()));
/*  303:     */     
/*  304:     */ 
/*  305: 320 */     newB.setToolTipText("New layout (Ctrl+N)");
/*  306: 321 */     newB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/*  307: 322 */     newB.setEnabled(this.m_mainPerspective.getAllowMultipleTabs());
/*  308: 323 */     final JButton helpB = new JButton(new ImageIcon(StepVisual.loadIcon(ICON_PATH + "help.png").getImage()));
/*  309:     */     
/*  310: 325 */     helpB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/*  311: 326 */     helpB.setToolTipText("Display help (Ctrl+H)");
/*  312: 327 */     JButton togglePerspectivesB = new JButton(new ImageIcon(StepVisual.loadIcon(ICON_PATH + "cog_go.png").getImage()));
/*  313:     */     
/*  314: 329 */     togglePerspectivesB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/*  315: 330 */     togglePerspectivesB.setToolTipText("Show/hide perspectives toolbar (Ctrl+P)");
/*  316:     */     
/*  317: 332 */     final JButton templatesB = new JButton(new ImageIcon(StepVisual.loadIcon(ICON_PATH + "application_view_tile.png").getImage()));
/*  318:     */     
/*  319:     */ 
/*  320: 335 */     templatesB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/*  321: 336 */     templatesB.setToolTipText("Load a template layout");
/*  322: 337 */     JButton noteB = new JButton(new ImageIcon(StepVisual.loadIcon(ICON_PATH + "note_add.png").getImage()));
/*  323:     */     
/*  324:     */ 
/*  325: 340 */     noteB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/*  326: 341 */     noteB.setToolTipText("Add a note to the layout (Ctrl+I)");
/*  327: 342 */     JButton selectAllB = new JButton(new ImageIcon(StepVisual.loadIcon(ICON_PATH + "shape_group.png").getImage()));
/*  328:     */     
/*  329:     */ 
/*  330: 345 */     selectAllB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/*  331: 346 */     selectAllB.setToolTipText("Select all (Ctrl+A)");
/*  332: 347 */     final JButton zoomInB = new JButton(new ImageIcon(StepVisual.loadIcon(ICON_PATH + "zoom_in.png").getImage()));
/*  333:     */     
/*  334: 349 */     zoomInB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/*  335: 350 */     zoomInB.setToolTipText("Zoom in (Ctrl++)");
/*  336: 351 */     final JButton zoomOutB = new JButton(new ImageIcon(StepVisual.loadIcon(ICON_PATH + "zoom_out.png").getImage()));
/*  337:     */     
/*  338:     */ 
/*  339: 354 */     zoomOutB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/*  340: 355 */     zoomOutB.setToolTipText("Zoom out (Ctrl+-)");
/*  341: 356 */     JButton undoB = new JButton(new ImageIcon(StepVisual.loadIcon(ICON_PATH + "arrow_undo.png").getImage()));
/*  342:     */     
/*  343:     */ 
/*  344: 359 */     undoB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/*  345: 360 */     undoB.setToolTipText("Undo (Ctrl+U)");
/*  346:     */     
/*  347:     */ 
/*  348: 363 */     final Action saveAction = new AbstractAction("Save")
/*  349:     */     {
/*  350:     */       public void actionPerformed(ActionEvent e)
/*  351:     */       {
/*  352: 366 */         if (MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentTabIndex() >= 0) {
/*  353: 367 */           MainKFPerspectiveToolBar.this.m_mainPerspective.saveLayout(MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentTabIndex(), false);
/*  354:     */         }
/*  355:     */       }
/*  356: 371 */     };
/*  357: 372 */     KeyStroke saveKey = KeyStroke.getKeyStroke(83, 128);
/*  358:     */     
/*  359: 374 */     this.m_mainPerspective.getActionMap().put("Save", saveAction);
/*  360: 375 */     this.m_mainPerspective.getInputMap(2).put(saveKey, "Save");
/*  361:     */     
/*  362: 377 */     saveB.addActionListener(new ActionListener()
/*  363:     */     {
/*  364:     */       public void actionPerformed(ActionEvent e)
/*  365:     */       {
/*  366: 380 */         saveAction.actionPerformed(e);
/*  367:     */       }
/*  368: 383 */     });
/*  369: 384 */     KeyStroke saveAsKey = KeyStroke.getKeyStroke(89, 128);
/*  370:     */     
/*  371: 386 */     final Action saveAsAction = new AbstractAction()
/*  372:     */     {
/*  373:     */       public void actionPerformed(ActionEvent e)
/*  374:     */       {
/*  375: 389 */         MainKFPerspectiveToolBar.this.m_mainPerspective.saveLayout(MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentTabIndex(), true);
/*  376:     */       }
/*  377: 392 */     };
/*  378: 393 */     this.m_mainPerspective.getActionMap().put("SaveAS", saveAsAction);
/*  379: 394 */     this.m_mainPerspective.getInputMap(2).put(saveAsKey, "SaveAS");
/*  380:     */     
/*  381:     */ 
/*  382: 397 */     saveBB.addActionListener(new ActionListener()
/*  383:     */     {
/*  384:     */       public void actionPerformed(ActionEvent e)
/*  385:     */       {
/*  386: 400 */         saveAsAction.actionPerformed(e);
/*  387:     */       }
/*  388: 403 */     });
/*  389: 404 */     final Action openAction = new AbstractAction("Open")
/*  390:     */     {
/*  391:     */       public void actionPerformed(ActionEvent e)
/*  392:     */       {
/*  393: 407 */         MainKFPerspectiveToolBar.this.m_mainPerspective.loadLayout();
/*  394:     */       }
/*  395: 409 */     };
/*  396: 410 */     KeyStroke openKey = KeyStroke.getKeyStroke(79, 128);
/*  397:     */     
/*  398: 412 */     this.m_mainPerspective.getActionMap().put("Open", openAction);
/*  399: 413 */     this.m_mainPerspective.getInputMap(2).put(openKey, "Open");
/*  400:     */     
/*  401: 415 */     loadB.addActionListener(new ActionListener()
/*  402:     */     {
/*  403:     */       public void actionPerformed(ActionEvent e)
/*  404:     */       {
/*  405: 418 */         openAction.actionPerformed(e);
/*  406:     */       }
/*  407: 421 */     });
/*  408: 422 */     final Action newAction = new AbstractAction("New")
/*  409:     */     {
/*  410:     */       public void actionPerformed(ActionEvent e)
/*  411:     */       {
/*  412: 425 */         MainKFPerspectiveToolBar.this.m_mainPerspective.addUntitledTab();
/*  413:     */       }
/*  414: 427 */     };
/*  415: 428 */     KeyStroke newKey = KeyStroke.getKeyStroke(78, 128);
/*  416:     */     
/*  417: 430 */     this.m_mainPerspective.getActionMap().put("New", newAction);
/*  418: 431 */     this.m_mainPerspective.getInputMap(2).put(newKey, "New");
/*  419:     */     
/*  420: 433 */     newB.addActionListener(new ActionListener()
/*  421:     */     {
/*  422:     */       public void actionPerformed(ActionEvent ae)
/*  423:     */       {
/*  424: 436 */         newAction.actionPerformed(ae);
/*  425:     */       }
/*  426: 439 */     });
/*  427: 440 */     final Action selectAllAction = new AbstractAction("SelectAll")
/*  428:     */     {
/*  429:     */       public void actionPerformed(ActionEvent e)
/*  430:     */       {
/*  431: 443 */         if ((MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout() != null) && (MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout().numSteps() > 0))
/*  432:     */         {
/*  433: 445 */           List<StepVisual> newSelected = newSelected = new ArrayList();
/*  434:     */           
/*  435: 447 */           newSelected.addAll(MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout().getRenderGraph());
/*  436: 451 */           if (newSelected.size() == MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout().getSelectedSteps().size()) {
/*  437: 454 */             MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout().setSelectedSteps(new ArrayList());
/*  438:     */           } else {
/*  439: 458 */             MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout().setSelectedSteps(newSelected);
/*  440:     */           }
/*  441: 460 */           MainKFPerspectiveToolBar.this.m_mainPerspective.revalidate();
/*  442: 461 */           MainKFPerspectiveToolBar.this.m_mainPerspective.repaint();
/*  443: 462 */           MainKFPerspectiveToolBar.this.m_mainPerspective.notifyIsDirty();
/*  444:     */         }
/*  445:     */       }
/*  446: 465 */     };
/*  447: 466 */     KeyStroke selectAllKey = KeyStroke.getKeyStroke(65, 128);
/*  448:     */     
/*  449: 468 */     this.m_mainPerspective.getActionMap().put("SelectAll", selectAllAction);
/*  450: 469 */     this.m_mainPerspective.getInputMap(2).put(selectAllKey, "SelectAll");
/*  451:     */     
/*  452: 471 */     selectAllB.addActionListener(new ActionListener()
/*  453:     */     {
/*  454:     */       public void actionPerformed(ActionEvent e)
/*  455:     */       {
/*  456: 474 */         selectAllAction.actionPerformed(e);
/*  457:     */       }
/*  458: 477 */     });
/*  459: 478 */     final Action zoomInAction = new AbstractAction("ZoomIn")
/*  460:     */     {
/*  461:     */       public void actionPerformed(ActionEvent e)
/*  462:     */       {
/*  463: 481 */         if (MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout() != null)
/*  464:     */         {
/*  465: 482 */           int z = MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout().getZoomSetting();
/*  466: 483 */           z += 25;
/*  467: 484 */           zoomOutB.setEnabled(true);
/*  468: 485 */           if (z >= 200)
/*  469:     */           {
/*  470: 486 */             z = 200;
/*  471: 487 */             zoomInB.setEnabled(false);
/*  472:     */           }
/*  473: 489 */           MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout().setZoomSetting(z);
/*  474: 490 */           MainKFPerspectiveToolBar.this.m_mainPerspective.revalidate();
/*  475: 491 */           MainKFPerspectiveToolBar.this.m_mainPerspective.repaint();
/*  476: 492 */           MainKFPerspectiveToolBar.this.m_mainPerspective.notifyIsDirty();
/*  477:     */         }
/*  478:     */       }
/*  479: 495 */     };
/*  480: 496 */     KeyStroke zoomInKey = KeyStroke.getKeyStroke(61, 128);
/*  481:     */     
/*  482: 498 */     this.m_mainPerspective.getActionMap().put("ZoomIn", zoomInAction);
/*  483: 499 */     this.m_mainPerspective.getInputMap(2).put(zoomInKey, "ZoomIn");
/*  484:     */     
/*  485: 501 */     zoomInB.addActionListener(new ActionListener()
/*  486:     */     {
/*  487:     */       public void actionPerformed(ActionEvent e)
/*  488:     */       {
/*  489: 504 */         zoomInAction.actionPerformed(e);
/*  490:     */       }
/*  491: 507 */     });
/*  492: 508 */     final Action zoomOutAction = new AbstractAction("ZoomOut")
/*  493:     */     {
/*  494:     */       public void actionPerformed(ActionEvent e)
/*  495:     */       {
/*  496: 511 */         if (MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout() != null)
/*  497:     */         {
/*  498: 512 */           int z = MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout().getZoomSetting();
/*  499: 513 */           z -= 25;
/*  500: 514 */           zoomInB.setEnabled(true);
/*  501: 515 */           if (z <= 50)
/*  502:     */           {
/*  503: 516 */             z = 50;
/*  504: 517 */             zoomOutB.setEnabled(false);
/*  505:     */           }
/*  506: 519 */           MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout().setZoomSetting(z);
/*  507: 520 */           MainKFPerspectiveToolBar.this.m_mainPerspective.revalidate();
/*  508: 521 */           MainKFPerspectiveToolBar.this.m_mainPerspective.repaint();
/*  509: 522 */           MainKFPerspectiveToolBar.this.m_mainPerspective.notifyIsDirty();
/*  510:     */         }
/*  511:     */       }
/*  512: 525 */     };
/*  513: 526 */     KeyStroke zoomOutKey = KeyStroke.getKeyStroke(45, 128);
/*  514:     */     
/*  515: 528 */     this.m_mainPerspective.getActionMap().put("ZoomOut", zoomOutAction);
/*  516: 529 */     this.m_mainPerspective.getInputMap(2).put(zoomOutKey, "ZoomOut");
/*  517:     */     
/*  518: 531 */     zoomOutB.addActionListener(new ActionListener()
/*  519:     */     {
/*  520:     */       public void actionPerformed(ActionEvent e)
/*  521:     */       {
/*  522: 534 */         zoomOutAction.actionPerformed(e);
/*  523:     */       }
/*  524: 537 */     });
/*  525: 538 */     final Action cutAction = new AbstractAction("Cut")
/*  526:     */     {
/*  527:     */       public void actionPerformed(ActionEvent e)
/*  528:     */       {
/*  529: 541 */         if ((MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout() != null) && (MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout().getSelectedSteps().size() > 0)) {
/*  530:     */           try
/*  531:     */           {
/*  532: 544 */             MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout().copySelectedStepsToClipboard();
/*  533: 545 */             MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout().removeSelectedSteps();
/*  534:     */           }
/*  535:     */           catch (WekaException e1)
/*  536:     */           {
/*  537: 547 */             MainKFPerspectiveToolBar.this.m_mainPerspective.showErrorDialog(e1);
/*  538:     */           }
/*  539:     */         }
/*  540:     */       }
/*  541: 551 */     };
/*  542: 552 */     KeyStroke cutKey = KeyStroke.getKeyStroke(88, 128);
/*  543:     */     
/*  544: 554 */     this.m_mainPerspective.getActionMap().put("Cut", cutAction);
/*  545: 555 */     this.m_mainPerspective.getInputMap(2).put(cutKey, "Cut");
/*  546:     */     
/*  547: 557 */     cutB.addActionListener(new ActionListener()
/*  548:     */     {
/*  549:     */       public void actionPerformed(ActionEvent e)
/*  550:     */       {
/*  551: 560 */         cutAction.actionPerformed(e);
/*  552:     */       }
/*  553: 563 */     });
/*  554: 564 */     final Action deleteAction = new AbstractAction("Delete")
/*  555:     */     {
/*  556:     */       public void actionPerformed(ActionEvent e)
/*  557:     */       {
/*  558: 567 */         if (MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout() != null) {
/*  559:     */           try
/*  560:     */           {
/*  561: 569 */             MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout().removeSelectedSteps();
/*  562:     */           }
/*  563:     */           catch (WekaException e1)
/*  564:     */           {
/*  565: 571 */             MainKFPerspectiveToolBar.this.m_mainPerspective.showErrorDialog(e1);
/*  566:     */           }
/*  567:     */         }
/*  568:     */       }
/*  569: 575 */     };
/*  570: 576 */     KeyStroke deleteKey = KeyStroke.getKeyStroke(127, 0);
/*  571: 577 */     this.m_mainPerspective.getActionMap().put("Delete", deleteAction);
/*  572: 578 */     this.m_mainPerspective.getInputMap(2).put(deleteKey, "Delete");
/*  573:     */     
/*  574: 580 */     deleteB.addActionListener(new ActionListener()
/*  575:     */     {
/*  576:     */       public void actionPerformed(ActionEvent e)
/*  577:     */       {
/*  578: 583 */         deleteAction.actionPerformed(e);
/*  579:     */       }
/*  580: 586 */     });
/*  581: 587 */     final Action copyAction = new AbstractAction("Copy")
/*  582:     */     {
/*  583:     */       public void actionPerformed(ActionEvent e)
/*  584:     */       {
/*  585: 590 */         if ((MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout() != null) && (MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout().getSelectedSteps().size() > 0)) {
/*  586:     */           try
/*  587:     */           {
/*  588: 593 */             MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout().copySelectedStepsToClipboard();
/*  589: 594 */             MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout().setSelectedSteps(new ArrayList());
/*  590:     */           }
/*  591:     */           catch (WekaException e1)
/*  592:     */           {
/*  593: 597 */             MainKFPerspectiveToolBar.this.m_mainPerspective.showErrorDialog(e1);
/*  594:     */           }
/*  595:     */         }
/*  596:     */       }
/*  597: 601 */     };
/*  598: 602 */     KeyStroke copyKey = KeyStroke.getKeyStroke(67, 128);
/*  599:     */     
/*  600: 604 */     this.m_mainPerspective.getActionMap().put("Copy", copyAction);
/*  601: 605 */     this.m_mainPerspective.getInputMap(2).put(copyKey, "Copy");
/*  602:     */     
/*  603: 607 */     copyB.addActionListener(new ActionListener()
/*  604:     */     {
/*  605:     */       public void actionPerformed(ActionEvent e)
/*  606:     */       {
/*  607: 610 */         copyAction.actionPerformed(e);
/*  608:     */       }
/*  609: 613 */     });
/*  610: 614 */     final Action pasteAction = new AbstractAction("Paste")
/*  611:     */     {
/*  612:     */       public void actionPerformed(ActionEvent e)
/*  613:     */       {
/*  614: 617 */         if ((MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout() != null) && (MainKFPerspectiveToolBar.this.m_mainPerspective.getPasteBuffer().length() > 0))
/*  615:     */         {
/*  616: 619 */           MainKFPerspectiveToolBar.this.m_mainPerspective.setCursor(Cursor.getPredefinedCursor(1));
/*  617:     */           
/*  618: 621 */           MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout().setFlowLayoutOperation(VisibleLayout.LayoutOperation.PASTING);
/*  619:     */         }
/*  620:     */       }
/*  621: 625 */     };
/*  622: 626 */     KeyStroke pasteKey = KeyStroke.getKeyStroke(86, 128);
/*  623:     */     
/*  624: 628 */     this.m_mainPerspective.getActionMap().put("Paste", pasteAction);
/*  625: 629 */     this.m_mainPerspective.getInputMap(2).put(pasteKey, "Paste");
/*  626:     */     
/*  627: 631 */     pasteB.addActionListener(new ActionListener()
/*  628:     */     {
/*  629:     */       public void actionPerformed(ActionEvent e)
/*  630:     */       {
/*  631: 634 */         pasteAction.actionPerformed(e);
/*  632:     */       }
/*  633: 637 */     });
/*  634: 638 */     final Action snapAction = new AbstractAction("Snap")
/*  635:     */     {
/*  636:     */       public void actionPerformed(ActionEvent e)
/*  637:     */       {
/*  638: 643 */         if ((snapToGridB.isSelected()) && 
/*  639: 644 */           (MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout() != null)) {
/*  640: 645 */           MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout().snapSelectedToGrid();
/*  641:     */         }
/*  642:     */       }
/*  643: 649 */     };
/*  644: 650 */     KeyStroke snapKey = KeyStroke.getKeyStroke(71, 128);
/*  645:     */     
/*  646: 652 */     this.m_mainPerspective.getActionMap().put("Snap", snapAction);
/*  647: 653 */     this.m_mainPerspective.getInputMap(2).put(snapKey, "Snap");
/*  648:     */     
/*  649: 655 */     snapToGridB.addActionListener(new ActionListener()
/*  650:     */     {
/*  651:     */       public void actionPerformed(ActionEvent e)
/*  652:     */       {
/*  653: 658 */         if (snapToGridB.isSelected()) {
/*  654: 659 */           snapAction.actionPerformed(e);
/*  655:     */         }
/*  656:     */       }
/*  657: 663 */     });
/*  658: 664 */     final Action noteAction = new AbstractAction("Note")
/*  659:     */     {
/*  660:     */       public void actionPerformed(ActionEvent e)
/*  661:     */       {
/*  662: 667 */         if (MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout() != null) {
/*  663: 668 */           MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout().initiateAddNote();
/*  664:     */         }
/*  665:     */       }
/*  666: 671 */     };
/*  667: 672 */     KeyStroke noteKey = KeyStroke.getKeyStroke(73, 128);
/*  668:     */     
/*  669: 674 */     this.m_mainPerspective.getActionMap().put("Note", noteAction);
/*  670: 675 */     this.m_mainPerspective.getInputMap(2).put(noteKey, "Note");
/*  671:     */     
/*  672: 677 */     noteB.addActionListener(new ActionListener()
/*  673:     */     {
/*  674:     */       public void actionPerformed(ActionEvent e)
/*  675:     */       {
/*  676: 680 */         noteAction.actionPerformed(e);
/*  677:     */       }
/*  678: 683 */     });
/*  679: 684 */     final Action undoAction = new AbstractAction("Undo")
/*  680:     */     {
/*  681:     */       public void actionPerformed(ActionEvent e)
/*  682:     */       {
/*  683: 687 */         if (MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout() != null) {
/*  684: 688 */           MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout().popAndLoadUndo();
/*  685:     */         }
/*  686:     */       }
/*  687: 691 */     };
/*  688: 692 */     KeyStroke undoKey = KeyStroke.getKeyStroke(85, 128);
/*  689:     */     
/*  690: 694 */     this.m_mainPerspective.getActionMap().put("Undo", undoAction);
/*  691: 695 */     this.m_mainPerspective.getInputMap(2).put(undoKey, "Undo");
/*  692:     */     
/*  693: 697 */     undoB.addActionListener(new ActionListener()
/*  694:     */     {
/*  695:     */       public void actionPerformed(ActionEvent e)
/*  696:     */       {
/*  697: 700 */         undoAction.actionPerformed(e);
/*  698:     */       }
/*  699: 703 */     });
/*  700: 704 */     final Action helpAction = new AbstractAction("Help")
/*  701:     */     {
/*  702:     */       public void actionPerformed(ActionEvent e)
/*  703:     */       {
/*  704: 707 */         MainKFPerspectiveToolBar.this.popupHelp(helpB);
/*  705:     */       }
/*  706: 709 */     };
/*  707: 710 */     KeyStroke helpKey = KeyStroke.getKeyStroke(72, 128);
/*  708:     */     
/*  709: 712 */     this.m_mainPerspective.getActionMap().put("Help", helpAction);
/*  710: 713 */     this.m_mainPerspective.getInputMap(2).put(helpKey, "Help");
/*  711:     */     
/*  712: 715 */     helpB.addActionListener(new ActionListener()
/*  713:     */     {
/*  714:     */       public void actionPerformed(ActionEvent ae)
/*  715:     */       {
/*  716: 718 */         helpAction.actionPerformed(ae);
/*  717:     */       }
/*  718: 721 */     });
/*  719: 722 */     templatesB.addActionListener(new ActionListener()
/*  720:     */     {
/*  721:     */       public void actionPerformed(ActionEvent e)
/*  722:     */       {
/*  723: 726 */         PopupMenu popupMenu = new PopupMenu();
/*  724: 727 */         List<String> builtinTemplates = MainKFPerspectiveToolBar.this.m_mainPerspective.getTemplateManager().getBuiltinTemplateDescriptions();
/*  725:     */         
/*  726:     */ 
/*  727: 730 */         List<String> pluginTemplates = MainKFPerspectiveToolBar.this.m_mainPerspective.getTemplateManager().getPluginTemplateDescriptions();
/*  728: 734 */         for (final String desc : builtinTemplates)
/*  729:     */         {
/*  730: 735 */           MenuItem menuItem = new MenuItem(desc);
/*  731: 736 */           menuItem.addActionListener(new ActionListener()
/*  732:     */           {
/*  733:     */             public void actionPerformed(ActionEvent e)
/*  734:     */             {
/*  735:     */               try
/*  736:     */               {
/*  737: 740 */                 Flow templateFlow = MainKFPerspectiveToolBar.this.m_mainPerspective.getTemplateManager().getTemplateFlow(desc);
/*  738:     */                 
/*  739: 742 */                 MainKFPerspectiveToolBar.this.m_mainPerspective.addTab(desc);
/*  740: 743 */                 MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout().setFlow(templateFlow);
/*  741:     */               }
/*  742:     */               catch (WekaException ex)
/*  743:     */               {
/*  744: 745 */                 MainKFPerspectiveToolBar.this.m_mainPerspective.showErrorDialog(ex);
/*  745:     */               }
/*  746:     */             }
/*  747: 748 */           });
/*  748: 749 */           popupMenu.add(menuItem);
/*  749:     */         }
/*  750: 751 */         if ((builtinTemplates.size() > 0) && (pluginTemplates.size() > 0)) {
/*  751: 752 */           popupMenu.addSeparator();
/*  752:     */         }
/*  753: 754 */         for (final String desc : pluginTemplates)
/*  754:     */         {
/*  755: 755 */           MenuItem menuItem = new MenuItem(desc);
/*  756: 756 */           menuItem.addActionListener(new ActionListener()
/*  757:     */           {
/*  758:     */             public void actionPerformed(ActionEvent e)
/*  759:     */             {
/*  760:     */               try
/*  761:     */               {
/*  762: 760 */                 Flow templateFlow = MainKFPerspectiveToolBar.this.m_mainPerspective.getTemplateManager().getTemplateFlow(desc);
/*  763:     */                 
/*  764: 762 */                 MainKFPerspectiveToolBar.this.m_mainPerspective.addTab(desc);
/*  765: 763 */                 MainKFPerspectiveToolBar.this.m_mainPerspective.getCurrentLayout().setFlow(templateFlow);
/*  766:     */               }
/*  767:     */               catch (WekaException ex)
/*  768:     */               {
/*  769: 765 */                 MainKFPerspectiveToolBar.this.m_mainPerspective.showErrorDialog(ex);
/*  770:     */               }
/*  771:     */             }
/*  772: 768 */           });
/*  773: 769 */           popupMenu.add(menuItem);
/*  774:     */         }
/*  775: 771 */         templatesB.add(popupMenu);
/*  776: 772 */         popupMenu.show(templatesB, 0, 0);
/*  777:     */       }
/*  778: 774 */     });
/*  779: 775 */     templatesB.setEnabled(this.m_mainPerspective.getTemplateManager().numTemplates() > 0);
/*  780:     */     
/*  781:     */ 
/*  782: 778 */     final Action togglePerspectivesAction = new AbstractAction("Toggle perspectives")
/*  783:     */     {
/*  784:     */       public void actionPerformed(ActionEvent e)
/*  785:     */       {
/*  786: 782 */         if (!Utils.getDontShowDialog("weka.gui.knowledgeflow.PerspectiveInfo"))
/*  787:     */         {
/*  788: 784 */           JCheckBox dontShow = new JCheckBox("Do not show this message again");
/*  789:     */           
/*  790: 786 */           Object[] stuff = new Object[2];
/*  791: 787 */           stuff[0] = "Perspectives are environments that take over the\nKnowledge Flow UI and provide major additional functionality.\nMany perspectives will operate on a set of instances. Instances\nCan be sent to a perspective by placing a DataSource on the\nlayout canvas, configuring it and then selecting \"Send to perspective\"\nfrom the contextual popup menu that appears when you right-click on\nit. Several perspectives are built in to the Knowledge Flow, others\ncan be installed via the package manager.\n";
/*  792:     */           
/*  793:     */ 
/*  794:     */ 
/*  795:     */ 
/*  796:     */ 
/*  797:     */ 
/*  798:     */ 
/*  799:     */ 
/*  800: 796 */           stuff[1] = dontShow;
/*  801:     */           
/*  802: 798 */           JOptionPane.showMessageDialog(MainKFPerspectiveToolBar.this.m_mainPerspective, stuff, "Perspective information", 0);
/*  803: 801 */           if (dontShow.isSelected()) {
/*  804:     */             try
/*  805:     */             {
/*  806: 803 */               Utils.setDontShowDialog("weka.gui.Knowledgeflow.PerspectiveInfo");
/*  807:     */             }
/*  808:     */             catch (Exception ex) {}
/*  809:     */           }
/*  810:     */         }
/*  811: 810 */         if (MainKFPerspectiveToolBar.this.m_mainPerspective.getMainApplication().isPerspectivesToolBarVisible()) {
/*  812: 812 */           MainKFPerspectiveToolBar.this.m_mainPerspective.getMainApplication().hidePerspectivesToolBar();
/*  813:     */         } else {
/*  814: 814 */           MainKFPerspectiveToolBar.this.m_mainPerspective.getMainApplication().showPerspectivesToolBar();
/*  815:     */         }
/*  816: 816 */         MainKFPerspectiveToolBar.this.m_mainPerspective.revalidate();
/*  817: 817 */         MainKFPerspectiveToolBar.this.m_mainPerspective.notifyIsDirty();
/*  818:     */       }
/*  819: 819 */     };
/*  820: 820 */     KeyStroke togglePerspectivesKey = KeyStroke.getKeyStroke(80, 128);
/*  821:     */     
/*  822: 822 */     this.m_mainPerspective.getActionMap().put("Toggle perspectives", togglePerspectivesAction);
/*  823:     */     
/*  824: 824 */     this.m_mainPerspective.getInputMap(2).put(togglePerspectivesKey, "Toggle perspectives");
/*  825:     */     
/*  826: 826 */     togglePerspectivesB.addActionListener(new ActionListener()
/*  827:     */     {
/*  828:     */       public void actionPerformed(ActionEvent e)
/*  829:     */       {
/*  830: 829 */         togglePerspectivesAction.actionPerformed(e);
/*  831:     */       }
/*  832: 832 */     });
/*  833: 833 */     addWidgetToToolBar(fixedTools, Widgets.ZOOM_IN_BUTTON.toString(), zoomInB);
/*  834: 834 */     addMenuItemToMenu("View", Widgets.ZOOM_IN_BUTTON.toString(), zoomInAction, zoomInKey);
/*  835:     */     
/*  836: 836 */     addWidgetToToolBar(fixedTools, Widgets.ZOOM_OUT_BUTTON.toString(), zoomOutB);
/*  837: 837 */     addMenuItemToMenu("View", Widgets.ZOOM_OUT_BUTTON.toString(), zoomOutAction, zoomOutKey);
/*  838:     */     
/*  839: 839 */     fixedTools.addSeparator();
/*  840: 840 */     addWidgetToToolBar(fixedTools, Widgets.SELECT_ALL_BUTTON.toString(), selectAllB);
/*  841:     */     
/*  842: 842 */     addWidgetToToolBar(fixedTools, Widgets.CUT_BUTTON.toString(), cutB);
/*  843: 843 */     addMenuItemToMenu("Edit", Widgets.CUT_BUTTON.toString(), cutAction, cutKey);
/*  844: 844 */     addWidgetToToolBar(fixedTools, Widgets.COPY_BUTTON.toString(), copyB);
/*  845: 845 */     addMenuItemToMenu("Edit", Widgets.COPY_BUTTON.toString(), copyAction, copyKey);
/*  846:     */     
/*  847: 847 */     addMenuItemToMenu("Edit", Widgets.PASTE_BUTTON.toString(), pasteAction, pasteKey);
/*  848:     */     
/*  849: 849 */     addWidgetToToolBar(fixedTools, Widgets.DELETE_BUTTON.toString(), deleteB);
/*  850: 850 */     addMenuItemToMenu("Edit", Widgets.DELETE_BUTTON.toString(), deleteAction, deleteKey);
/*  851:     */     
/*  852: 852 */     addWidgetToToolBar(fixedTools, Widgets.PASTE_BUTTON.toString(), pasteB);
/*  853: 853 */     addWidgetToToolBar(fixedTools, Widgets.UNDO_BUTTON.toString(), undoB);
/*  854: 854 */     addMenuItemToMenu("Edit", Widgets.UNDO_BUTTON.toString(), undoAction, undoKey);
/*  855:     */     
/*  856: 856 */     addWidgetToToolBar(fixedTools, Widgets.NOTE_BUTTON.toString(), noteB);
/*  857: 857 */     addMenuItemToMenu("Insert", Widgets.NOTE_BUTTON.toString(), noteAction, noteKey);
/*  858:     */     
/*  859: 859 */     fixedTools.addSeparator();
/*  860: 860 */     addWidgetToToolBar(fixedTools, Widgets.SNAP_TO_GRID_BUTTON.toString(), snapToGridB);
/*  861:     */     
/*  862: 862 */     fixedTools.addSeparator();
/*  863: 863 */     addWidgetToToolBar(fixedTools, Widgets.NEW_FLOW_BUTTON.toString(), newB);
/*  864: 864 */     addMenuItemToMenu("File", Widgets.NEW_FLOW_BUTTON.toString(), newAction, newKey);
/*  865:     */     
/*  866: 866 */     addWidgetToToolBar(fixedTools, Widgets.SAVE_FLOW_BUTTON.toString(), saveB);
/*  867: 867 */     addMenuItemToMenu("File", Widgets.LOAD_FLOW_BUTTON.toString(), openAction, openKey);
/*  868:     */     
/*  869: 869 */     addMenuItemToMenu("File", Widgets.SAVE_FLOW_BUTTON.toString(), saveAction, saveKey);
/*  870:     */     
/*  871: 871 */     addWidgetToToolBar(fixedTools, Widgets.SAVE_FLOW_AS_BUTTON.toString(), saveBB);
/*  872:     */     
/*  873: 873 */     addMenuItemToMenu("File", Widgets.SAVE_FLOW_AS_BUTTON.toString(), saveAction, saveAsKey);
/*  874:     */     
/*  875: 875 */     addWidgetToToolBar(fixedTools, Widgets.LOAD_FLOW_BUTTON.toString(), loadB);
/*  876: 876 */     addWidgetToToolBar(fixedTools, Widgets.TEMPLATES_BUTTON.toString(), templatesB);
/*  877:     */     
/*  878: 878 */     fixedTools.addSeparator();
/*  879: 879 */     addWidgetToToolBar(fixedTools, Widgets.TOGGLE_PERSPECTIVES_BUTTON.toString(), togglePerspectivesB);
/*  880:     */     
/*  881: 881 */     addWidgetToToolBar(fixedTools, Widgets.HELP_BUTTON.toString(), helpB);
/*  882: 882 */     Dimension d = undoB.getPreferredSize();
/*  883: 883 */     Dimension d2 = fixedTools.getMinimumSize();
/*  884: 884 */     Dimension d3 = new Dimension(d2.width, d.height + 4);
/*  885: 885 */     fixedTools.setPreferredSize(d3);
/*  886: 886 */     fixedTools.setMaximumSize(d3);
/*  887: 887 */     fixedTools.setFloatable(false);
/*  888: 888 */     add(fixedTools, "East");
/*  889:     */   }
/*  890:     */   
/*  891:     */   protected void addWidgetToToolBar(JToolBar toolBar, String widgetName, JComponent widget)
/*  892:     */   {
/*  893: 900 */     toolBar.add(widget);
/*  894: 901 */     this.m_widgetMap.put(widgetName, widget);
/*  895:     */   }
/*  896:     */   
/*  897:     */   protected void addMenuItemToMenu(String topMenu, String menuItem, final Action action, KeyStroke accelerator)
/*  898:     */   {
/*  899: 914 */     JMenuItem newItem = (JMenuItem)this.m_menuItemMap.get(menuItem);
/*  900: 915 */     if (newItem != null) {
/*  901: 916 */       throw new IllegalArgumentException("The menu item '" + menuItem + "' already exists!");
/*  902:     */     }
/*  903: 919 */     newItem = new JMenuItem(menuItem);
/*  904: 920 */     if (accelerator != null) {
/*  905: 921 */       newItem.setAccelerator(accelerator);
/*  906:     */     }
/*  907: 923 */     newItem.addActionListener(new ActionListener()
/*  908:     */     {
/*  909:     */       public void actionPerformed(ActionEvent e)
/*  910:     */       {
/*  911: 926 */         action.actionPerformed(e);
/*  912:     */       }
/*  913: 929 */     });
/*  914: 930 */     JMenu topJ = (JMenu)this.m_menuMap.get(topMenu);
/*  915: 931 */     if (topJ == null)
/*  916:     */     {
/*  917: 932 */       topJ = new JMenu();
/*  918: 933 */       topJ.setText(topMenu);
/*  919: 934 */       this.m_menuMap.put(topMenu, topJ);
/*  920:     */     }
/*  921: 937 */     topJ.add(newItem);
/*  922: 938 */     this.m_menuItemMap.put(menuItem, newItem);
/*  923:     */   }
/*  924:     */   
/*  925:     */   public void enableWidget(String widgetName, boolean enable)
/*  926:     */   {
/*  927: 949 */     JComponent widget = (JComponent)this.m_widgetMap.get(widgetName);
/*  928: 950 */     if (widget != null) {
/*  929: 951 */       widget.setEnabled(enable);
/*  930:     */     }
/*  931: 953 */     JMenuItem mI = (JMenuItem)this.m_menuItemMap.get(widgetName);
/*  932: 954 */     if (mI != null) {
/*  933: 955 */       mI.setEnabled(enable);
/*  934:     */     }
/*  935:     */   }
/*  936:     */   
/*  937:     */   public void enableWidgets(String... widgetNames)
/*  938:     */   {
/*  939: 960 */     for (String s : widgetNames) {
/*  940: 961 */       enableWidget(s, true);
/*  941:     */     }
/*  942:     */   }
/*  943:     */   
/*  944:     */   public void disableWidgets(String... widgetNames)
/*  945:     */   {
/*  946: 966 */     for (String s : widgetNames) {
/*  947: 967 */       enableWidget(s, false);
/*  948:     */     }
/*  949:     */   }
/*  950:     */   
/*  951:     */   private void popupHelp(final JButton helpB)
/*  952:     */   {
/*  953:     */     try
/*  954:     */     {
/*  955: 973 */       helpB.setEnabled(false);
/*  956:     */       
/*  957: 975 */       InputStream inR = getClass().getClassLoader().getResourceAsStream("weka/gui/knowledgeflow/README_KnowledgeFlow");
/*  958:     */       
/*  959:     */ 
/*  960:     */ 
/*  961: 979 */       StringBuilder helpHolder = new StringBuilder();
/*  962: 980 */       LineNumberReader lnr = new LineNumberReader(new InputStreamReader(inR));
/*  963:     */       String line;
/*  964: 984 */       while ((line = lnr.readLine()) != null) {
/*  965: 985 */         helpHolder.append(line + "\n");
/*  966:     */       }
/*  967: 988 */       lnr.close();
/*  968: 989 */       final JFrame jf = new JFrame();
/*  969: 990 */       jf.getContentPane().setLayout(new BorderLayout());
/*  970: 991 */       JTextArea ta = new JTextArea(helpHolder.toString());
/*  971: 992 */       ta.setFont(new Font("Monospaced", 0, 12));
/*  972: 993 */       ta.setEditable(false);
/*  973: 994 */       JScrollPane sp = new JScrollPane(ta);
/*  974: 995 */       jf.getContentPane().add(sp, "Center");
/*  975: 996 */       jf.addWindowListener(new WindowAdapter()
/*  976:     */       {
/*  977:     */         public void windowClosing(WindowEvent e)
/*  978:     */         {
/*  979: 999 */           helpB.setEnabled(true);
/*  980:1000 */           jf.dispose();
/*  981:     */         }
/*  982:1002 */       });
/*  983:1003 */       jf.setSize(600, 600);
/*  984:1004 */       jf.setVisible(true);
/*  985:     */     }
/*  986:     */     catch (Exception ex)
/*  987:     */     {
/*  988:1007 */       ex.printStackTrace();
/*  989:1008 */       helpB.setEnabled(true);
/*  990:     */     }
/*  991:     */   }
/*  992:     */   
/*  993:     */   public JComponent getWidget(String widgetName)
/*  994:     */   {
/*  995:1013 */     return (JComponent)this.m_widgetMap.get(widgetName);
/*  996:     */   }
/*  997:     */   
/*  998:     */   public JMenuItem getMenuItem(String menuItemName)
/*  999:     */   {
/* 1000:1017 */     return (JMenuItem)this.m_menuItemMap.get(menuItemName);
/* 1001:     */   }
/* 1002:     */   
/* 1003:     */   public static enum Widgets
/* 1004:     */   {
/* 1005:1025 */     ZOOM_IN_BUTTON("Zoom In"),  ZOOM_OUT_BUTTON("Zoom Out"),  SELECT_ALL_BUTTON("Select All"),  CUT_BUTTON("Cut"),  COPY_BUTTON("Copy"),  DELETE_BUTTON("Delete"),  PASTE_BUTTON("Paste"),  UNDO_BUTTON("Undo"),  NOTE_BUTTON("New Note"),  SNAP_TO_GRID_BUTTON("Snap to Grid"),  NEW_FLOW_BUTTON("New Layout"),  SAVE_FLOW_BUTTON("Save"),  SAVE_FLOW_AS_BUTTON("Save As..."),  LOAD_FLOW_BUTTON("Open..."),  TEMPLATES_BUTTON("Template"),  TOGGLE_PERSPECTIVES_BUTTON("Toggle Perspectives"),  HELP_BUTTON("help..."),  POINTER_BUTTON("Pointer"),  PLAY_PARALLEL_BUTTON("Launch"),  PLAY_SEQUENTIAL_BUTTON("Launch Squential"),  STOP_BUTTON("Stop");
/* 1006:     */     
/* 1007:     */     String m_name;
/* 1008:     */     
/* 1009:     */     private Widgets(String name)
/* 1010:     */     {
/* 1011:1039 */       this.m_name = name;
/* 1012:     */     }
/* 1013:     */     
/* 1014:     */     public String toString()
/* 1015:     */     {
/* 1016:1044 */       return this.m_name;
/* 1017:     */     }
/* 1018:     */   }
/* 1019:     */   
/* 1020:     */   public List<JMenu> getMenus()
/* 1021:     */   {
/* 1022:1054 */     List<JMenu> menuList = new ArrayList();
/* 1023:1055 */     for (Map.Entry<String, JMenu> e : this.m_menuMap.entrySet()) {
/* 1024:1056 */       menuList.add(e.getValue());
/* 1025:     */     }
/* 1026:1058 */     return menuList;
/* 1027:     */   }
/* 1028:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.MainKFPerspectiveToolBar
 * JD-Core Version:    0.7.0.1
 */