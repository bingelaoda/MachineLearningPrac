/*    1:     */ package weka.gui.explorer;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.awt.Color;
/*    5:     */ import java.awt.Container;
/*    6:     */ import java.awt.Dimension;
/*    7:     */ import java.awt.FlowLayout;
/*    8:     */ import java.awt.Font;
/*    9:     */ import java.awt.GridBagConstraints;
/*   10:     */ import java.awt.GridBagLayout;
/*   11:     */ import java.awt.GridLayout;
/*   12:     */ import java.awt.Insets;
/*   13:     */ import java.awt.MenuItem;
/*   14:     */ import java.awt.Point;
/*   15:     */ import java.awt.PopupMenu;
/*   16:     */ import java.awt.event.ActionEvent;
/*   17:     */ import java.awt.event.ActionListener;
/*   18:     */ import java.awt.event.MouseAdapter;
/*   19:     */ import java.awt.event.MouseEvent;
/*   20:     */ import java.awt.event.WindowAdapter;
/*   21:     */ import java.awt.event.WindowEvent;
/*   22:     */ import java.beans.PropertyChangeEvent;
/*   23:     */ import java.beans.PropertyChangeListener;
/*   24:     */ import java.io.BufferedReader;
/*   25:     */ import java.io.File;
/*   26:     */ import java.io.FileInputStream;
/*   27:     */ import java.io.FileOutputStream;
/*   28:     */ import java.io.FileReader;
/*   29:     */ import java.io.InputStream;
/*   30:     */ import java.io.ObjectInputStream;
/*   31:     */ import java.io.ObjectOutputStream;
/*   32:     */ import java.io.OutputStream;
/*   33:     */ import java.io.PrintStream;
/*   34:     */ import java.io.Reader;
/*   35:     */ import java.text.SimpleDateFormat;
/*   36:     */ import java.util.ArrayList;
/*   37:     */ import java.util.Date;
/*   38:     */ import java.util.Map;
/*   39:     */ import java.util.Random;
/*   40:     */ import java.util.Vector;
/*   41:     */ import java.util.zip.GZIPInputStream;
/*   42:     */ import java.util.zip.GZIPOutputStream;
/*   43:     */ import javax.swing.BorderFactory;
/*   44:     */ import javax.swing.ButtonGroup;
/*   45:     */ import javax.swing.DefaultComboBoxModel;
/*   46:     */ import javax.swing.DefaultListModel;
/*   47:     */ import javax.swing.JButton;
/*   48:     */ import javax.swing.JCheckBox;
/*   49:     */ import javax.swing.JComboBox;
/*   50:     */ import javax.swing.JFileChooser;
/*   51:     */ import javax.swing.JFrame;
/*   52:     */ import javax.swing.JLabel;
/*   53:     */ import javax.swing.JList;
/*   54:     */ import javax.swing.JMenu;
/*   55:     */ import javax.swing.JMenuItem;
/*   56:     */ import javax.swing.JOptionPane;
/*   57:     */ import javax.swing.JPanel;
/*   58:     */ import javax.swing.JPopupMenu;
/*   59:     */ import javax.swing.JRadioButton;
/*   60:     */ import javax.swing.JScrollPane;
/*   61:     */ import javax.swing.JTextArea;
/*   62:     */ import javax.swing.JTextField;
/*   63:     */ import javax.swing.JViewport;
/*   64:     */ import javax.swing.event.ChangeEvent;
/*   65:     */ import javax.swing.event.ChangeListener;
/*   66:     */ import javax.swing.filechooser.FileFilter;
/*   67:     */ import weka.clusterers.ClusterEvaluation;
/*   68:     */ import weka.clusterers.Clusterer;
/*   69:     */ import weka.clusterers.SimpleKMeans;
/*   70:     */ import weka.core.Attribute;
/*   71:     */ import weka.core.Capabilities;
/*   72:     */ import weka.core.CapabilitiesHandler;
/*   73:     */ import weka.core.Defaults;
/*   74:     */ import weka.core.Drawable;
/*   75:     */ import weka.core.Environment;
/*   76:     */ import weka.core.Instances;
/*   77:     */ import weka.core.Memory;
/*   78:     */ import weka.core.OptionHandler;
/*   79:     */ import weka.core.SerializedObject;
/*   80:     */ import weka.core.Settings;
/*   81:     */ import weka.core.Settings.SettingKey;
/*   82:     */ import weka.core.Utils;
/*   83:     */ import weka.core.Version;
/*   84:     */ import weka.filters.Filter;
/*   85:     */ import weka.filters.unsupervised.attribute.Remove;
/*   86:     */ import weka.gui.AbstractPerspective;
/*   87:     */ import weka.gui.ExtensionFileFilter;
/*   88:     */ import weka.gui.GUIApplication;
/*   89:     */ import weka.gui.GenericObjectEditor;
/*   90:     */ import weka.gui.InstancesSummaryPanel;
/*   91:     */ import weka.gui.ListSelectorDialog;
/*   92:     */ import weka.gui.LogPanel;
/*   93:     */ import weka.gui.Logger;
/*   94:     */ import weka.gui.PerspectiveInfo;
/*   95:     */ import weka.gui.PropertyPanel;
/*   96:     */ import weka.gui.ResultHistoryPanel;
/*   97:     */ import weka.gui.SaveBuffer;
/*   98:     */ import weka.gui.SetInstancesPanel;
/*   99:     */ import weka.gui.SysErrLog;
/*  100:     */ import weka.gui.TaskLogger;
/*  101:     */ import weka.gui.hierarchyvisualizer.HierarchyVisualizer;
/*  102:     */ import weka.gui.treevisualizer.PlaceNode2;
/*  103:     */ import weka.gui.treevisualizer.TreeVisualizer;
/*  104:     */ import weka.gui.visualize.VisualizePanel;
/*  105:     */ import weka.gui.visualize.plugins.TreeVisualizePlugin;
/*  106:     */ 
/*  107:     */ @PerspectiveInfo(ID="weka.gui.explorer.clustererpanel", title="Cluster", toolTipText="Cluster instances", iconPath="weka/gui/weka_icon_new_small.png")
/*  108:     */ public class ClustererPanel
/*  109:     */   extends AbstractPerspective
/*  110:     */   implements Explorer.CapabilitiesFilterChangeListener, Explorer.ExplorerPanel, Explorer.LogHandler
/*  111:     */ {
/*  112:     */   static final long serialVersionUID = -2474932792950820990L;
/*  113: 143 */   protected Explorer m_Explorer = null;
/*  114: 146 */   public static String MODEL_FILE_EXTENSION = ".model";
/*  115: 149 */   protected GenericObjectEditor m_ClustererEditor = new GenericObjectEditor();
/*  116: 152 */   protected PropertyPanel m_CLPanel = new PropertyPanel(this.m_ClustererEditor);
/*  117: 155 */   protected JTextArea m_OutText = new JTextArea(20, 40);
/*  118: 158 */   protected Logger m_Log = new SysErrLog();
/*  119: 161 */   SaveBuffer m_SaveOut = new SaveBuffer(this.m_Log, this);
/*  120: 164 */   protected ResultHistoryPanel m_History = new ResultHistoryPanel(this.m_OutText);
/*  121: 167 */   protected JRadioButton m_PercentBut = new JRadioButton("Percentage split");
/*  122: 170 */   protected JRadioButton m_TrainBut = new JRadioButton("Use training set");
/*  123: 173 */   protected JRadioButton m_TestSplitBut = new JRadioButton("Supplied test set");
/*  124: 176 */   protected JRadioButton m_ClassesToClustersBut = new JRadioButton("Classes to clusters evaluation");
/*  125: 183 */   protected JComboBox m_ClassCombo = new JComboBox();
/*  126: 186 */   protected JLabel m_PercentLab = new JLabel("%", 4);
/*  127: 189 */   protected JTextField m_PercentText = new JTextField("66");
/*  128: 192 */   protected JButton m_SetTestBut = new JButton("Set...");
/*  129:     */   protected JFrame m_SetTestFrame;
/*  130: 201 */   protected JButton m_ignoreBut = new JButton("Ignore attributes");
/*  131: 203 */   protected DefaultListModel m_ignoreKeyModel = new DefaultListModel();
/*  132: 204 */   protected JList m_ignoreKeyList = new JList(this.m_ignoreKeyModel);
/*  133: 212 */   ActionListener m_RadioListener = new ActionListener()
/*  134:     */   {
/*  135:     */     public void actionPerformed(ActionEvent e)
/*  136:     */     {
/*  137: 215 */       ClustererPanel.this.updateRadioLinks();
/*  138:     */     }
/*  139:     */   };
/*  140: 220 */   protected JButton m_StartBut = new JButton("Start");
/*  141: 223 */   private final Dimension COMBO_SIZE = new Dimension(250, this.m_StartBut.getPreferredSize().height);
/*  142: 227 */   protected JButton m_StopBut = new JButton("Stop");
/*  143:     */   protected Instances m_Instances;
/*  144:     */   protected Instances m_TestInstances;
/*  145: 236 */   protected VisualizePanel m_CurrentVis = null;
/*  146: 241 */   protected JCheckBox m_StorePredictionsBut = new JCheckBox("Store clusters for visualization");
/*  147:     */   protected Thread m_RunThread;
/*  148:     */   protected InstancesSummaryPanel m_Summary;
/*  149: 251 */   protected FileFilter m_ModelFilter = new ExtensionFileFilter(MODEL_FILE_EXTENSION, "Model object files");
/*  150: 255 */   protected JFileChooser m_FileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
/*  151:     */   protected boolean m_initialSettingsSet;
/*  152:     */   
/*  153:     */   static
/*  154:     */   {
/*  155: 263 */     GenericObjectEditor.registerEditors();
/*  156:     */   }
/*  157:     */   
/*  158:     */   public ClustererPanel()
/*  159:     */   {
/*  160: 272 */     this.m_OutText.setEditable(false);
/*  161: 273 */     this.m_OutText.setFont(new Font("Monospaced", 0, 12));
/*  162: 274 */     this.m_OutText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*  163: 275 */     this.m_OutText.addMouseListener(new MouseAdapter()
/*  164:     */     {
/*  165:     */       public void mouseClicked(MouseEvent e)
/*  166:     */       {
/*  167: 278 */         if ((e.getModifiers() & 0x10) != 16) {
/*  168: 279 */           ClustererPanel.this.m_OutText.selectAll();
/*  169:     */         }
/*  170:     */       }
/*  171: 282 */     });
/*  172: 283 */     JPanel historyHolder = new JPanel(new BorderLayout());
/*  173: 284 */     historyHolder.setBorder(BorderFactory.createTitledBorder("Result list (right-click for options)"));
/*  174:     */     
/*  175: 286 */     historyHolder.add(this.m_History, "Center");
/*  176: 287 */     this.m_ClustererEditor.setClassType(Clusterer.class);
/*  177: 288 */     this.m_ClustererEditor.setValue(ExplorerDefaults.getClusterer());
/*  178: 289 */     this.m_ClustererEditor.addPropertyChangeListener(new PropertyChangeListener()
/*  179:     */     {
/*  180:     */       public void propertyChange(PropertyChangeEvent e)
/*  181:     */       {
/*  182: 292 */         ClustererPanel.this.m_StartBut.setEnabled(true);
/*  183: 293 */         Capabilities currentFilter = ClustererPanel.this.m_ClustererEditor.getCapabilitiesFilter();
/*  184: 294 */         Clusterer clusterer = (Clusterer)ClustererPanel.this.m_ClustererEditor.getValue();
/*  185: 295 */         Capabilities currentSchemeCapabilities = null;
/*  186: 296 */         if ((clusterer != null) && (currentFilter != null) && ((clusterer instanceof CapabilitiesHandler)))
/*  187:     */         {
/*  188: 298 */           currentSchemeCapabilities = ((CapabilitiesHandler)clusterer).getCapabilities();
/*  189: 301 */           if ((!currentSchemeCapabilities.supportsMaybe(currentFilter)) && (!currentSchemeCapabilities.supports(currentFilter))) {
/*  190: 303 */             ClustererPanel.this.m_StartBut.setEnabled(false);
/*  191:     */           }
/*  192:     */         }
/*  193: 306 */         ClustererPanel.this.repaint();
/*  194:     */       }
/*  195: 309 */     });
/*  196: 310 */     this.m_TrainBut.setToolTipText("Cluster the same set that the clusterer is trained on");
/*  197:     */     
/*  198: 312 */     this.m_PercentBut.setToolTipText("Train on a percentage of the data and cluster the remainder");
/*  199:     */     
/*  200: 314 */     this.m_TestSplitBut.setToolTipText("Cluster a user-specified dataset");
/*  201: 315 */     this.m_ClassesToClustersBut.setToolTipText("Evaluate clusters with respect to a class");
/*  202:     */     
/*  203: 317 */     this.m_ClassCombo.setToolTipText("Select the class attribute for class based evaluation");
/*  204:     */     
/*  205: 319 */     this.m_StartBut.setToolTipText("Starts the clustering");
/*  206: 320 */     this.m_StopBut.setToolTipText("Stops a running clusterer");
/*  207: 321 */     this.m_StorePredictionsBut.setToolTipText("Store predictions in the result list for later visualization");
/*  208:     */     
/*  209:     */ 
/*  210: 324 */     this.m_ignoreBut.setToolTipText("Ignore attributes during clustering");
/*  211:     */     
/*  212: 326 */     this.m_FileChooser.setFileFilter(this.m_ModelFilter);
/*  213: 327 */     this.m_FileChooser.setFileSelectionMode(0);
/*  214:     */     
/*  215: 329 */     this.m_ClassCombo.setPreferredSize(this.COMBO_SIZE);
/*  216: 330 */     this.m_ClassCombo.setMaximumSize(this.COMBO_SIZE);
/*  217: 331 */     this.m_ClassCombo.setMinimumSize(this.COMBO_SIZE);
/*  218: 332 */     this.m_ClassCombo.setEnabled(false);
/*  219:     */     
/*  220: 334 */     this.m_PercentBut.setSelected(ExplorerDefaults.getClustererTestMode() == 2);
/*  221: 335 */     this.m_TrainBut.setSelected(ExplorerDefaults.getClustererTestMode() == 3);
/*  222: 336 */     this.m_TestSplitBut.setSelected(ExplorerDefaults.getClustererTestMode() == 4);
/*  223: 337 */     this.m_ClassesToClustersBut.setSelected(ExplorerDefaults.getClustererTestMode() == 5);
/*  224:     */     
/*  225: 339 */     this.m_StorePredictionsBut.setSelected(ExplorerDefaults.getClustererStoreClustersForVis());
/*  226:     */     
/*  227: 341 */     updateRadioLinks();
/*  228: 342 */     ButtonGroup bg = new ButtonGroup();
/*  229: 343 */     bg.add(this.m_TrainBut);
/*  230: 344 */     bg.add(this.m_PercentBut);
/*  231: 345 */     bg.add(this.m_TestSplitBut);
/*  232: 346 */     bg.add(this.m_ClassesToClustersBut);
/*  233: 347 */     this.m_TrainBut.addActionListener(this.m_RadioListener);
/*  234: 348 */     this.m_PercentBut.addActionListener(this.m_RadioListener);
/*  235: 349 */     this.m_TestSplitBut.addActionListener(this.m_RadioListener);
/*  236: 350 */     this.m_ClassesToClustersBut.addActionListener(this.m_RadioListener);
/*  237: 351 */     this.m_SetTestBut.addActionListener(new ActionListener()
/*  238:     */     {
/*  239:     */       public void actionPerformed(ActionEvent e)
/*  240:     */       {
/*  241: 354 */         ClustererPanel.this.setTestSet();
/*  242:     */       }
/*  243: 357 */     });
/*  244: 358 */     this.m_StartBut.setEnabled(false);
/*  245: 359 */     this.m_StopBut.setEnabled(false);
/*  246: 360 */     this.m_ignoreBut.setEnabled(false);
/*  247: 361 */     this.m_StartBut.addActionListener(new ActionListener()
/*  248:     */     {
/*  249:     */       public void actionPerformed(ActionEvent e)
/*  250:     */       {
/*  251: 364 */         boolean proceed = true;
/*  252: 365 */         if (Explorer.m_Memory.memoryIsLow()) {
/*  253: 366 */           proceed = Explorer.m_Memory.showMemoryIsLow();
/*  254:     */         }
/*  255: 369 */         if (proceed) {
/*  256: 370 */           ClustererPanel.this.startClusterer();
/*  257:     */         }
/*  258:     */       }
/*  259: 373 */     });
/*  260: 374 */     this.m_StopBut.addActionListener(new ActionListener()
/*  261:     */     {
/*  262:     */       public void actionPerformed(ActionEvent e)
/*  263:     */       {
/*  264: 377 */         ClustererPanel.this.stopClusterer();
/*  265:     */       }
/*  266: 380 */     });
/*  267: 381 */     this.m_ignoreBut.addActionListener(new ActionListener()
/*  268:     */     {
/*  269:     */       public void actionPerformed(ActionEvent e)
/*  270:     */       {
/*  271: 384 */         ClustererPanel.this.setIgnoreColumns();
/*  272:     */       }
/*  273: 387 */     });
/*  274: 388 */     this.m_History.setHandleRightClicks(false);
/*  275:     */     
/*  276: 390 */     this.m_History.getList().addMouseListener(new MouseAdapter()
/*  277:     */     {
/*  278:     */       public void mouseClicked(MouseEvent e)
/*  279:     */       {
/*  280: 393 */         if (((e.getModifiers() & 0x10) != 16) || (e.isAltDown()))
/*  281:     */         {
/*  282: 395 */           int index = ClustererPanel.this.m_History.getList().locationToIndex(e.getPoint());
/*  283: 396 */           if (index != -1)
/*  284:     */           {
/*  285: 397 */             String name = ClustererPanel.this.m_History.getNameAtIndex(index);
/*  286: 398 */             ClustererPanel.this.visualizeClusterer(name, e.getX(), e.getY());
/*  287:     */           }
/*  288:     */           else
/*  289:     */           {
/*  290: 400 */             ClustererPanel.this.visualizeClusterer(null, e.getX(), e.getY());
/*  291:     */           }
/*  292:     */         }
/*  293:     */       }
/*  294: 405 */     });
/*  295: 406 */     this.m_ClassCombo.addActionListener(new ActionListener()
/*  296:     */     {
/*  297:     */       public void actionPerformed(ActionEvent e)
/*  298:     */       {
/*  299: 409 */         ClustererPanel.this.updateCapabilitiesFilter(ClustererPanel.this.m_ClustererEditor.getCapabilitiesFilter());
/*  300:     */       }
/*  301: 413 */     });
/*  302: 414 */     JPanel p1 = new JPanel();
/*  303: 415 */     p1.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Clusterer"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
/*  304:     */     
/*  305:     */ 
/*  306: 418 */     p1.setLayout(new BorderLayout());
/*  307: 419 */     p1.add(this.m_CLPanel, "North");
/*  308:     */     
/*  309: 421 */     JPanel p2 = new JPanel();
/*  310: 422 */     GridBagLayout gbL = new GridBagLayout();
/*  311: 423 */     p2.setLayout(gbL);
/*  312: 424 */     p2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Cluster mode"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
/*  313:     */     
/*  314:     */ 
/*  315: 427 */     GridBagConstraints gbC = new GridBagConstraints();
/*  316: 428 */     gbC.anchor = 17;
/*  317: 429 */     gbC.gridy = 0;
/*  318: 430 */     gbC.gridx = 0;
/*  319: 431 */     gbL.setConstraints(this.m_TrainBut, gbC);
/*  320: 432 */     p2.add(this.m_TrainBut);
/*  321:     */     
/*  322: 434 */     gbC = new GridBagConstraints();
/*  323: 435 */     gbC.anchor = 17;
/*  324: 436 */     gbC.gridy = 1;
/*  325: 437 */     gbC.gridx = 0;
/*  326: 438 */     gbL.setConstraints(this.m_TestSplitBut, gbC);
/*  327: 439 */     p2.add(this.m_TestSplitBut);
/*  328:     */     
/*  329: 441 */     gbC = new GridBagConstraints();
/*  330: 442 */     gbC.anchor = 13;
/*  331: 443 */     gbC.fill = 2;
/*  332: 444 */     gbC.gridy = 1;
/*  333: 445 */     gbC.gridx = 1;
/*  334: 446 */     gbC.gridwidth = 2;
/*  335: 447 */     gbC.insets = new Insets(2, 10, 2, 0);
/*  336: 448 */     gbL.setConstraints(this.m_SetTestBut, gbC);
/*  337: 449 */     p2.add(this.m_SetTestBut);
/*  338:     */     
/*  339: 451 */     gbC = new GridBagConstraints();
/*  340: 452 */     gbC.anchor = 17;
/*  341: 453 */     gbC.gridy = 2;
/*  342: 454 */     gbC.gridx = 0;
/*  343: 455 */     gbL.setConstraints(this.m_PercentBut, gbC);
/*  344: 456 */     p2.add(this.m_PercentBut);
/*  345:     */     
/*  346: 458 */     gbC = new GridBagConstraints();
/*  347: 459 */     gbC.anchor = 13;
/*  348: 460 */     gbC.fill = 2;
/*  349: 461 */     gbC.gridy = 2;
/*  350: 462 */     gbC.gridx = 1;
/*  351: 463 */     gbC.insets = new Insets(2, 10, 2, 10);
/*  352: 464 */     gbL.setConstraints(this.m_PercentLab, gbC);
/*  353: 465 */     p2.add(this.m_PercentLab);
/*  354:     */     
/*  355: 467 */     gbC = new GridBagConstraints();
/*  356: 468 */     gbC.anchor = 13;
/*  357: 469 */     gbC.fill = 2;
/*  358: 470 */     gbC.gridy = 2;
/*  359: 471 */     gbC.gridx = 2;
/*  360: 472 */     gbC.weightx = 100.0D;
/*  361: 473 */     gbC.ipadx = 20;
/*  362: 474 */     gbL.setConstraints(this.m_PercentText, gbC);
/*  363: 475 */     p2.add(this.m_PercentText);
/*  364:     */     
/*  365: 477 */     gbC = new GridBagConstraints();
/*  366: 478 */     gbC.anchor = 17;
/*  367: 479 */     gbC.gridy = 3;
/*  368: 480 */     gbC.gridx = 0;
/*  369: 481 */     gbC.gridwidth = 2;
/*  370: 482 */     gbL.setConstraints(this.m_ClassesToClustersBut, gbC);
/*  371: 483 */     p2.add(this.m_ClassesToClustersBut);
/*  372:     */     
/*  373: 485 */     this.m_ClassCombo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
/*  374: 486 */     gbC = new GridBagConstraints();
/*  375: 487 */     gbC.anchor = 17;
/*  376: 488 */     gbC.gridy = 4;
/*  377: 489 */     gbC.gridx = 0;
/*  378: 490 */     gbC.gridwidth = 2;
/*  379: 491 */     gbL.setConstraints(this.m_ClassCombo, gbC);
/*  380: 492 */     p2.add(this.m_ClassCombo);
/*  381:     */     
/*  382: 494 */     gbC = new GridBagConstraints();
/*  383: 495 */     gbC.anchor = 17;
/*  384: 496 */     gbC.gridy = 5;
/*  385: 497 */     gbC.gridx = 0;
/*  386: 498 */     gbC.gridwidth = 2;
/*  387: 499 */     gbL.setConstraints(this.m_StorePredictionsBut, gbC);
/*  388: 500 */     p2.add(this.m_StorePredictionsBut);
/*  389:     */     
/*  390:     */ 
/*  391: 503 */     Vector<String> pluginsVector = GenericObjectEditor.getClassnames(ClustererPanelLaunchHandlerPlugin.class.getName());
/*  392:     */     
/*  393:     */ 
/*  394: 506 */     JButton pluginBut = null;
/*  395: 507 */     if (pluginsVector.size() == 1)
/*  396:     */     {
/*  397:     */       try
/*  398:     */       {
/*  399: 510 */         String className = (String)pluginsVector.elementAt(0);
/*  400: 511 */         final ClustererPanelLaunchHandlerPlugin plugin = (ClustererPanelLaunchHandlerPlugin)Class.forName(className).newInstance();
/*  401: 514 */         if (plugin != null)
/*  402:     */         {
/*  403: 515 */           plugin.setClustererPanel(this);
/*  404: 516 */           pluginBut = new JButton(plugin.getLaunchCommand());
/*  405: 517 */           pluginBut.addActionListener(new ActionListener()
/*  406:     */           {
/*  407:     */             public void actionPerformed(ActionEvent e)
/*  408:     */             {
/*  409: 520 */               plugin.launch();
/*  410:     */             }
/*  411:     */           });
/*  412:     */         }
/*  413:     */       }
/*  414:     */       catch (Exception ex)
/*  415:     */       {
/*  416: 525 */         ex.printStackTrace();
/*  417:     */       }
/*  418:     */     }
/*  419: 527 */     else if (pluginsVector.size() > 1)
/*  420:     */     {
/*  421: 529 */       int okPluginCount = 0;
/*  422: 530 */       final PopupMenu pluginPopup = new PopupMenu();
/*  423: 532 */       for (int i = 0; i < pluginsVector.size(); i++)
/*  424:     */       {
/*  425: 533 */         String className = (String)pluginsVector.elementAt(i);
/*  426:     */         try
/*  427:     */         {
/*  428: 535 */           final ClustererPanelLaunchHandlerPlugin plugin = (ClustererPanelLaunchHandlerPlugin)Class.forName(className).newInstance();
/*  429: 539 */           if (plugin != null)
/*  430:     */           {
/*  431: 542 */             okPluginCount++;
/*  432: 543 */             plugin.setClustererPanel(this);
/*  433: 544 */             MenuItem popI = new MenuItem(plugin.getLaunchCommand());
/*  434:     */             
/*  435: 546 */             popI.addActionListener(new ActionListener()
/*  436:     */             {
/*  437:     */               public void actionPerformed(ActionEvent e)
/*  438:     */               {
/*  439: 550 */                 plugin.launch();
/*  440:     */               }
/*  441: 552 */             });
/*  442: 553 */             pluginPopup.add(popI);
/*  443:     */           }
/*  444:     */         }
/*  445:     */         catch (Exception ex)
/*  446:     */         {
/*  447: 555 */           ex.printStackTrace();
/*  448:     */         }
/*  449:     */       }
/*  450: 559 */       if (okPluginCount > 0)
/*  451:     */       {
/*  452: 560 */         pluginBut = new JButton("Launchers...");
/*  453: 561 */         final JButton copyB = pluginBut;
/*  454: 562 */         copyB.add(pluginPopup);
/*  455: 563 */         pluginBut.addActionListener(new ActionListener()
/*  456:     */         {
/*  457:     */           public void actionPerformed(ActionEvent e)
/*  458:     */           {
/*  459: 566 */             pluginPopup.show(copyB, 0, 0);
/*  460:     */           }
/*  461:     */         });
/*  462:     */       }
/*  463:     */       else
/*  464:     */       {
/*  465: 570 */         pluginBut = null;
/*  466:     */       }
/*  467:     */     }
/*  468: 574 */     JPanel buttons = new JPanel();
/*  469: 575 */     buttons.setLayout(new GridLayout(2, 1));
/*  470: 576 */     JPanel ssButs = new JPanel();
/*  471: 577 */     ssButs.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*  472: 578 */     if (pluginBut == null) {
/*  473: 579 */       ssButs.setLayout(new GridLayout(1, 2, 5, 5));
/*  474:     */     } else {
/*  475: 581 */       ssButs.setLayout(new FlowLayout(0));
/*  476:     */     }
/*  477: 583 */     ssButs.add(this.m_StartBut);
/*  478: 584 */     ssButs.add(this.m_StopBut);
/*  479: 585 */     if (pluginBut != null) {
/*  480: 586 */       ssButs.add(pluginBut);
/*  481:     */     }
/*  482: 589 */     JPanel ib = new JPanel();
/*  483: 590 */     ib.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*  484: 591 */     ib.setLayout(new GridLayout(1, 1, 5, 5));
/*  485: 592 */     ib.add(this.m_ignoreBut);
/*  486: 593 */     buttons.add(ib);
/*  487: 594 */     buttons.add(ssButs);
/*  488:     */     
/*  489: 596 */     JPanel p3 = new JPanel();
/*  490: 597 */     p3.setBorder(BorderFactory.createTitledBorder("Clusterer output"));
/*  491: 598 */     p3.setLayout(new BorderLayout());
/*  492: 599 */     JScrollPane js = new JScrollPane(this.m_OutText);
/*  493: 600 */     p3.add(js, "Center");
/*  494: 601 */     js.getViewport().addChangeListener(new ChangeListener()
/*  495:     */     {
/*  496:     */       private int lastHeight;
/*  497:     */       
/*  498:     */       public void stateChanged(ChangeEvent e)
/*  499:     */       {
/*  500: 606 */         JViewport vp = (JViewport)e.getSource();
/*  501: 607 */         int h = vp.getViewSize().height;
/*  502: 608 */         if (h != this.lastHeight)
/*  503:     */         {
/*  504: 609 */           this.lastHeight = h;
/*  505: 610 */           int x = h - vp.getExtentSize().height;
/*  506: 611 */           vp.setViewPosition(new Point(0, x));
/*  507:     */         }
/*  508:     */       }
/*  509: 615 */     });
/*  510: 616 */     JPanel mondo = new JPanel();
/*  511: 617 */     gbL = new GridBagLayout();
/*  512: 618 */     mondo.setLayout(gbL);
/*  513: 619 */     gbC = new GridBagConstraints();
/*  514:     */     
/*  515: 621 */     gbC.fill = 2;
/*  516: 622 */     gbC.gridy = 0;
/*  517: 623 */     gbC.gridx = 0;
/*  518: 624 */     gbL.setConstraints(p2, gbC);
/*  519: 625 */     mondo.add(p2);
/*  520: 626 */     gbC = new GridBagConstraints();
/*  521: 627 */     gbC.anchor = 11;
/*  522: 628 */     gbC.fill = 2;
/*  523: 629 */     gbC.gridy = 1;
/*  524: 630 */     gbC.gridx = 0;
/*  525: 631 */     gbL.setConstraints(buttons, gbC);
/*  526: 632 */     mondo.add(buttons);
/*  527: 633 */     gbC = new GridBagConstraints();
/*  528:     */     
/*  529: 635 */     gbC.fill = 1;
/*  530: 636 */     gbC.gridy = 2;
/*  531: 637 */     gbC.gridx = 0;
/*  532: 638 */     gbC.weightx = 0.0D;
/*  533: 639 */     gbL.setConstraints(historyHolder, gbC);
/*  534: 640 */     mondo.add(historyHolder);
/*  535: 641 */     gbC = new GridBagConstraints();
/*  536: 642 */     gbC.fill = 1;
/*  537: 643 */     gbC.gridy = 0;
/*  538: 644 */     gbC.gridx = 1;
/*  539: 645 */     gbC.gridheight = 3;
/*  540: 646 */     gbC.weightx = 100.0D;
/*  541: 647 */     gbC.weighty = 100.0D;
/*  542: 648 */     gbL.setConstraints(p3, gbC);
/*  543: 649 */     mondo.add(p3);
/*  544:     */     
/*  545: 651 */     setLayout(new BorderLayout());
/*  546: 652 */     add(p1, "North");
/*  547: 653 */     add(mondo, "Center");
/*  548:     */   }
/*  549:     */   
/*  550:     */   protected void updateRadioLinks()
/*  551:     */   {
/*  552: 661 */     this.m_SetTestBut.setEnabled(this.m_TestSplitBut.isSelected());
/*  553: 662 */     if ((this.m_SetTestFrame != null) && (!this.m_TestSplitBut.isSelected())) {
/*  554: 663 */       this.m_SetTestFrame.setVisible(false);
/*  555:     */     }
/*  556: 665 */     this.m_PercentText.setEnabled(this.m_PercentBut.isSelected());
/*  557: 666 */     this.m_PercentLab.setEnabled(this.m_PercentBut.isSelected());
/*  558: 667 */     this.m_ClassCombo.setEnabled(this.m_ClassesToClustersBut.isSelected());
/*  559:     */     
/*  560: 669 */     updateCapabilitiesFilter(this.m_ClustererEditor.getCapabilitiesFilter());
/*  561:     */   }
/*  562:     */   
/*  563:     */   public void setLog(Logger newLog)
/*  564:     */   {
/*  565: 680 */     this.m_Log = newLog;
/*  566:     */   }
/*  567:     */   
/*  568:     */   public void setInstances(Instances inst)
/*  569:     */   {
/*  570: 691 */     this.m_Instances = inst;
/*  571:     */     
/*  572: 693 */     this.m_ignoreKeyModel.removeAllElements();
/*  573:     */     
/*  574: 695 */     String[] attribNames = new String[this.m_Instances.numAttributes()];
/*  575: 696 */     for (int i = 0; i < this.m_Instances.numAttributes(); i++)
/*  576:     */     {
/*  577: 697 */       String name = this.m_Instances.attribute(i).name();
/*  578: 698 */       this.m_ignoreKeyModel.addElement(name);
/*  579: 699 */       String type = "(" + Attribute.typeToStringShort(this.m_Instances.attribute(i)) + ") ";
/*  580:     */       
/*  581: 701 */       String attnm = this.m_Instances.attribute(i).name();
/*  582: 702 */       attribNames[i] = (type + attnm);
/*  583:     */     }
/*  584: 705 */     this.m_StartBut.setEnabled(this.m_RunThread == null);
/*  585: 706 */     this.m_StopBut.setEnabled(this.m_RunThread != null);
/*  586: 707 */     this.m_ignoreBut.setEnabled(true);
/*  587: 708 */     this.m_ClassCombo.setModel(new DefaultComboBoxModel(attribNames));
/*  588: 709 */     if (inst.classIndex() == -1) {
/*  589: 710 */       this.m_ClassCombo.setSelectedIndex(attribNames.length - 1);
/*  590:     */     } else {
/*  591: 712 */       this.m_ClassCombo.setSelectedIndex(inst.classIndex());
/*  592:     */     }
/*  593: 714 */     updateRadioLinks();
/*  594:     */   }
/*  595:     */   
/*  596:     */   protected void setTestSet()
/*  597:     */   {
/*  598: 725 */     if (this.m_SetTestFrame == null)
/*  599:     */     {
/*  600: 726 */       final SetInstancesPanel sp = new SetInstancesPanel();
/*  601: 727 */       sp.setReadIncrementally(false);
/*  602: 728 */       this.m_Summary = sp.getSummary();
/*  603: 729 */       if (this.m_TestInstances != null) {
/*  604: 730 */         sp.setInstances(this.m_TestInstances);
/*  605:     */       }
/*  606: 732 */       sp.addPropertyChangeListener(new PropertyChangeListener()
/*  607:     */       {
/*  608:     */         public void propertyChange(PropertyChangeEvent e)
/*  609:     */         {
/*  610: 735 */           ClustererPanel.this.m_TestInstances = sp.getInstances();
/*  611: 736 */           ClustererPanel.this.m_TestInstances.setClassIndex(-1);
/*  612:     */         }
/*  613: 741 */       });
/*  614: 742 */       this.m_SetTestFrame = new JFrame("Test Instances");
/*  615: 743 */       sp.setParentFrame(this.m_SetTestFrame);
/*  616: 744 */       this.m_SetTestFrame.getContentPane().setLayout(new BorderLayout());
/*  617: 745 */       this.m_SetTestFrame.getContentPane().add(sp, "Center");
/*  618: 746 */       this.m_SetTestFrame.pack();
/*  619:     */     }
/*  620: 748 */     this.m_SetTestFrame.setVisible(true);
/*  621:     */   }
/*  622:     */   
/*  623:     */   protected void startClusterer()
/*  624:     */   {
/*  625: 759 */     if (this.m_RunThread == null)
/*  626:     */     {
/*  627: 760 */       this.m_StartBut.setEnabled(false);
/*  628: 761 */       this.m_StopBut.setEnabled(true);
/*  629: 762 */       this.m_ignoreBut.setEnabled(false);
/*  630: 763 */       this.m_RunThread = new Thread()
/*  631:     */       {
/*  632:     */         public void run()
/*  633:     */         {
/*  634: 766 */           ClustererPanel.this.m_CLPanel.addToHistory();
/*  635:     */           
/*  636:     */ 
/*  637: 769 */           long trainTimeStart = 0L;long trainTimeElapsed = 0L;
/*  638:     */           
/*  639:     */ 
/*  640: 772 */           ClustererPanel.this.m_Log.statusMessage("Setting up...");
/*  641: 773 */           Instances inst = new Instances(ClustererPanel.this.m_Instances);
/*  642: 774 */           inst.setClassIndex(-1);
/*  643: 775 */           Instances userTest = null;
/*  644: 776 */           ClustererAssignmentsPlotInstances plotInstances = ExplorerDefaults.getClustererAssignmentsPlotInstances();
/*  645:     */           
/*  646: 778 */           plotInstances.setClusterer((Clusterer)ClustererPanel.this.m_ClustererEditor.getValue());
/*  647: 779 */           if (ClustererPanel.this.m_TestInstances != null) {
/*  648: 780 */             userTest = new Instances(ClustererPanel.this.m_TestInstances);
/*  649:     */           }
/*  650: 783 */           boolean saveVis = ClustererPanel.this.m_StorePredictionsBut.isSelected();
/*  651: 784 */           String grph = null;
/*  652: 785 */           int[] ignoredAtts = null;
/*  653:     */           
/*  654: 787 */           int testMode = 0;
/*  655: 788 */           int percent = 66;
/*  656: 789 */           Clusterer clusterer = (Clusterer)ClustererPanel.this.m_ClustererEditor.getValue();
/*  657: 790 */           Clusterer fullClusterer = null;
/*  658: 791 */           StringBuffer outBuff = new StringBuffer();
/*  659: 792 */           String name = new SimpleDateFormat("HH:mm:ss - ").format(new Date());
/*  660:     */           
/*  661: 794 */           String cname = clusterer.getClass().getName();
/*  662: 795 */           if (cname.startsWith("weka.clusterers.")) {
/*  663: 796 */             name = name + cname.substring("weka.clusterers.".length());
/*  664:     */           } else {
/*  665: 798 */             name = name + cname;
/*  666:     */           }
/*  667: 800 */           String cmd = ClustererPanel.this.m_ClustererEditor.getValue().getClass().getName();
/*  668: 801 */           if ((ClustererPanel.this.m_ClustererEditor.getValue() instanceof OptionHandler)) {
/*  669: 802 */             cmd = cmd + " " + Utils.joinOptions(((OptionHandler)ClustererPanel.this.m_ClustererEditor.getValue()).getOptions());
/*  670:     */           }
/*  671:     */           try
/*  672:     */           {
/*  673: 808 */             ClustererPanel.this.m_Log.logMessage("Started " + cname);
/*  674: 809 */             ClustererPanel.this.m_Log.logMessage("Command: " + cmd);
/*  675: 810 */             if ((ClustererPanel.this.m_Log instanceof TaskLogger)) {
/*  676: 811 */               ((TaskLogger)ClustererPanel.this.m_Log).taskStarted();
/*  677:     */             }
/*  678: 813 */             if (ClustererPanel.this.m_PercentBut.isSelected())
/*  679:     */             {
/*  680: 814 */               testMode = 2;
/*  681: 815 */               percent = Integer.parseInt(ClustererPanel.this.m_PercentText.getText());
/*  682: 816 */               if ((percent <= 0) || (percent >= 100)) {
/*  683: 817 */                 throw new Exception("Percentage must be between 0 and 100");
/*  684:     */               }
/*  685:     */             }
/*  686: 819 */             else if (ClustererPanel.this.m_TrainBut.isSelected())
/*  687:     */             {
/*  688: 820 */               testMode = 3;
/*  689:     */             }
/*  690: 821 */             else if (ClustererPanel.this.m_TestSplitBut.isSelected())
/*  691:     */             {
/*  692: 822 */               testMode = 4;
/*  693: 824 */               if (userTest == null) {
/*  694: 825 */                 throw new Exception("No user test set has been opened");
/*  695:     */               }
/*  696: 827 */               if (!inst.equalHeaders(userTest)) {
/*  697: 828 */                 throw new Exception("Train and test set are not compatible\n" + inst.equalHeadersMsg(userTest));
/*  698:     */               }
/*  699:     */             }
/*  700: 831 */             else if (ClustererPanel.this.m_ClassesToClustersBut.isSelected())
/*  701:     */             {
/*  702: 832 */               testMode = 5;
/*  703:     */             }
/*  704:     */             else
/*  705:     */             {
/*  706: 834 */               throw new Exception("Unknown test mode");
/*  707:     */             }
/*  708: 837 */             Instances trainInst = new Instances(inst);
/*  709: 838 */             if (ClustererPanel.this.m_ClassesToClustersBut.isSelected())
/*  710:     */             {
/*  711: 839 */               trainInst.setClassIndex(ClustererPanel.this.m_ClassCombo.getSelectedIndex());
/*  712: 840 */               inst.setClassIndex(ClustererPanel.this.m_ClassCombo.getSelectedIndex());
/*  713: 841 */               if (inst.classAttribute().isNumeric()) {
/*  714: 842 */                 throw new Exception("Class must be nominal for class based evaluation!");
/*  715:     */               }
/*  716:     */             }
/*  717: 846 */             if (!ClustererPanel.this.m_ignoreKeyList.isSelectionEmpty()) {
/*  718: 847 */               trainInst = ClustererPanel.this.removeIgnoreCols(trainInst);
/*  719:     */             }
/*  720: 851 */             outBuff.append("=== Run information ===\n\n");
/*  721: 852 */             outBuff.append("Scheme:       " + cname);
/*  722: 853 */             if ((clusterer instanceof OptionHandler))
/*  723:     */             {
/*  724: 854 */               String[] o = ((OptionHandler)clusterer).getOptions();
/*  725: 855 */               outBuff.append(" " + Utils.joinOptions(o));
/*  726:     */             }
/*  727: 857 */             outBuff.append("\n");
/*  728: 858 */             outBuff.append("Relation:     " + inst.relationName() + '\n');
/*  729: 859 */             outBuff.append("Instances:    " + inst.numInstances() + '\n');
/*  730: 860 */             outBuff.append("Attributes:   " + inst.numAttributes() + '\n');
/*  731: 861 */             if (inst.numAttributes() < 100)
/*  732:     */             {
/*  733: 862 */               boolean[] selected = new boolean[inst.numAttributes()];
/*  734: 863 */               for (int i = 0; i < inst.numAttributes(); i++) {
/*  735: 864 */                 selected[i] = true;
/*  736:     */               }
/*  737: 866 */               if (!ClustererPanel.this.m_ignoreKeyList.isSelectionEmpty())
/*  738:     */               {
/*  739: 867 */                 int[] indices = ClustererPanel.this.m_ignoreKeyList.getSelectedIndices();
/*  740: 868 */                 for (int i = 0; i < indices.length; i++) {
/*  741: 869 */                   selected[indices[i]] = false;
/*  742:     */                 }
/*  743:     */               }
/*  744: 872 */               if (ClustererPanel.this.m_ClassesToClustersBut.isSelected()) {
/*  745: 873 */                 selected[ClustererPanel.this.m_ClassCombo.getSelectedIndex()] = false;
/*  746:     */               }
/*  747: 875 */               for (int i = 0; i < inst.numAttributes(); i++) {
/*  748: 876 */                 if (selected[i] != 0) {
/*  749: 877 */                   outBuff.append("              " + inst.attribute(i).name() + '\n');
/*  750:     */                 }
/*  751:     */               }
/*  752: 881 */               if ((!ClustererPanel.this.m_ignoreKeyList.isSelectionEmpty()) || (ClustererPanel.this.m_ClassesToClustersBut.isSelected()))
/*  753:     */               {
/*  754: 883 */                 outBuff.append("Ignored:\n");
/*  755: 884 */                 for (int i = 0; i < inst.numAttributes(); i++) {
/*  756: 885 */                   if (selected[i] == 0) {
/*  757: 886 */                     outBuff.append("              " + inst.attribute(i).name() + '\n');
/*  758:     */                   }
/*  759:     */                 }
/*  760:     */               }
/*  761:     */             }
/*  762:     */             else
/*  763:     */             {
/*  764: 892 */               outBuff.append("              [list of attributes omitted]\n");
/*  765:     */             }
/*  766: 895 */             if (!ClustererPanel.this.m_ignoreKeyList.isSelectionEmpty()) {
/*  767: 896 */               ignoredAtts = ClustererPanel.this.m_ignoreKeyList.getSelectedIndices();
/*  768:     */             }
/*  769: 899 */             if (ClustererPanel.this.m_ClassesToClustersBut.isSelected()) {
/*  770: 901 */               if (ignoredAtts == null)
/*  771:     */               {
/*  772: 902 */                 ignoredAtts = new int[1];
/*  773: 903 */                 ignoredAtts[0] = ClustererPanel.this.m_ClassCombo.getSelectedIndex();
/*  774:     */               }
/*  775:     */               else
/*  776:     */               {
/*  777: 905 */                 int[] newIgnoredAtts = new int[ignoredAtts.length + 1];
/*  778: 906 */                 System.arraycopy(ignoredAtts, 0, newIgnoredAtts, 0, ignoredAtts.length);
/*  779:     */                 
/*  780: 908 */                 newIgnoredAtts[ignoredAtts.length] = ClustererPanel.this.m_ClassCombo.getSelectedIndex();
/*  781:     */                 
/*  782: 910 */                 ignoredAtts = newIgnoredAtts;
/*  783:     */               }
/*  784:     */             }
/*  785: 914 */             outBuff.append("Test mode:    ");
/*  786: 915 */             switch (testMode)
/*  787:     */             {
/*  788:     */             case 3: 
/*  789: 917 */               outBuff.append("evaluate on training data\n");
/*  790: 918 */               break;
/*  791:     */             case 2: 
/*  792: 920 */               outBuff.append("split " + percent + "% train, remainder test\n");
/*  793: 921 */               break;
/*  794:     */             case 4: 
/*  795: 923 */               outBuff.append("user supplied test set: " + userTest.numInstances() + " instances\n");
/*  796:     */               
/*  797: 925 */               break;
/*  798:     */             case 5: 
/*  799: 927 */               outBuff.append("Classes to clusters evaluation on training data");
/*  800:     */             }
/*  801: 931 */             outBuff.append("\n");
/*  802: 932 */             ClustererPanel.this.m_History.addResult(name, outBuff);
/*  803: 933 */             ClustererPanel.this.m_History.setSingle(name);
/*  804:     */             
/*  805:     */ 
/*  806: 936 */             ClustererPanel.this.m_Log.statusMessage("Building model on training data...");
/*  807:     */             
/*  808:     */ 
/*  809: 939 */             trainTimeStart = System.currentTimeMillis();
/*  810: 940 */             clusterer.buildClusterer(ClustererPanel.this.removeClass(trainInst));
/*  811: 941 */             trainTimeElapsed = System.currentTimeMillis() - trainTimeStart;
/*  812:     */             
/*  813:     */ 
/*  814: 944 */             outBuff.append("\n=== Clustering model (full training set) ===\n\n");
/*  815:     */             
/*  816:     */ 
/*  817: 947 */             outBuff.append(clusterer.toString() + '\n');
/*  818: 948 */             outBuff.append("\nTime taken to build model (full training data) : " + Utils.doubleToString(trainTimeElapsed / 1000.0D, 2) + " seconds\n\n");
/*  819:     */             
/*  820:     */ 
/*  821:     */ 
/*  822:     */ 
/*  823: 953 */             ClustererPanel.this.m_History.updateResult(name);
/*  824: 954 */             if ((clusterer instanceof Drawable)) {
/*  825:     */               try
/*  826:     */               {
/*  827: 956 */                 grph = ((Drawable)clusterer).graph();
/*  828:     */               }
/*  829:     */               catch (Exception ex) {}
/*  830:     */             }
/*  831: 961 */             SerializedObject so = new SerializedObject(clusterer);
/*  832: 962 */             fullClusterer = (Clusterer)so.getObject();
/*  833:     */             
/*  834: 964 */             ClusterEvaluation eval = new ClusterEvaluation();
/*  835: 965 */             eval.setClusterer(clusterer);
/*  836: 966 */             switch (testMode)
/*  837:     */             {
/*  838:     */             case 3: 
/*  839:     */             case 5: 
/*  840: 969 */               ClustererPanel.this.m_Log.statusMessage("Clustering training data...");
/*  841: 970 */               eval.evaluateClusterer(trainInst, "", false);
/*  842: 971 */               plotInstances.setInstances(inst);
/*  843: 972 */               plotInstances.setClusterEvaluation(eval);
/*  844: 973 */               outBuff.append("=== Model and evaluation on training set ===\n\n");
/*  845:     */               
/*  846: 975 */               break;
/*  847:     */             case 2: 
/*  848: 978 */               ClustererPanel.this.m_Log.statusMessage("Randomizing instances...");
/*  849: 979 */               inst.randomize(new Random(1L));
/*  850: 980 */               trainInst.randomize(new Random(1L));
/*  851: 981 */               int trainSize = trainInst.numInstances() * percent / 100;
/*  852: 982 */               int testSize = trainInst.numInstances() - trainSize;
/*  853: 983 */               Instances train = new Instances(trainInst, 0, trainSize);
/*  854: 984 */               Instances test = new Instances(trainInst, trainSize, testSize);
/*  855: 985 */               Instances testVis = new Instances(inst, trainSize, testSize);
/*  856: 986 */               ClustererPanel.this.m_Log.statusMessage("Building model on training split...");
/*  857: 987 */               trainTimeStart = System.currentTimeMillis();
/*  858: 988 */               clusterer.buildClusterer(train);
/*  859: 989 */               trainTimeElapsed = System.currentTimeMillis() - trainTimeStart;
/*  860: 990 */               ClustererPanel.this.m_Log.statusMessage("Evaluating on test split...");
/*  861: 991 */               eval.evaluateClusterer(test, "", false);
/*  862: 992 */               plotInstances.setInstances(testVis);
/*  863: 993 */               plotInstances.setClusterEvaluation(eval);
/*  864: 994 */               outBuff.append("=== Model and evaluation on test split ===\n");
/*  865: 995 */               outBuff.append(clusterer.toString() + "\n");
/*  866: 996 */               outBuff.append("\nTime taken to build model (percentage split) : " + Utils.doubleToString(trainTimeElapsed / 1000.0D, 2) + " seconds\n\n");
/*  867:     */               
/*  868:     */ 
/*  869:     */ 
/*  870:1000 */               break;
/*  871:     */             case 4: 
/*  872:1003 */               ClustererPanel.this.m_Log.statusMessage("Evaluating on test data...");
/*  873:1004 */               Instances userTestT = new Instances(userTest);
/*  874:1005 */               if (!ClustererPanel.this.m_ignoreKeyList.isSelectionEmpty()) {
/*  875:1006 */                 userTestT = ClustererPanel.this.removeIgnoreCols(userTestT);
/*  876:     */               }
/*  877:1008 */               eval.evaluateClusterer(userTestT, "", false);
/*  878:1009 */               plotInstances.setInstances(userTest);
/*  879:1010 */               plotInstances.setClusterEvaluation(eval);
/*  880:1011 */               outBuff.append("=== Evaluation on test set ===\n");
/*  881:1012 */               break;
/*  882:     */             default: 
/*  883:1015 */               throw new Exception("Test mode not implemented");
/*  884:     */             }
/*  885:1017 */             outBuff.append(eval.clusterResultsToString());
/*  886:1018 */             outBuff.append("\n");
/*  887:1019 */             ClustererPanel.this.m_History.updateResult(name);
/*  888:1020 */             ClustererPanel.this.m_Log.logMessage("Finished " + cname);
/*  889:1021 */             ClustererPanel.this.m_Log.statusMessage("OK");
/*  890:     */           }
/*  891:     */           catch (Exception ex)
/*  892:     */           {
/*  893:     */             Settings settings;
/*  894:     */             ArrayList<Object> vv;
/*  895:     */             Instances trainHeader;
/*  896:1023 */             ex.printStackTrace();
/*  897:1024 */             ClustererPanel.this.m_Log.logMessage(ex.getMessage());
/*  898:1025 */             JOptionPane.showMessageDialog(ClustererPanel.this, "Problem evaluating clusterer:\n" + ex.getMessage(), "Evaluate clusterer", 0);
/*  899:     */             
/*  900:     */ 
/*  901:1028 */             ClustererPanel.this.m_Log.statusMessage("Problem evaluating clusterer");
/*  902:     */           }
/*  903:     */           finally
/*  904:     */           {
/*  905:     */             Settings settings;
/*  906:     */             ArrayList<Object> vv;
/*  907:     */             Instances trainHeader;
/*  908:1030 */             if ((plotInstances != null) && (plotInstances.canPlot(true)))
/*  909:     */             {
/*  910:1031 */               ClustererPanel.this.m_CurrentVis = new VisualizePanel();
/*  911:1032 */               if (ClustererPanel.this.getMainApplication() != null)
/*  912:     */               {
/*  913:1033 */                 Settings settings = ClustererPanel.this.getMainApplication().getApplicationSettings();
/*  914:1034 */                 ClustererPanel.this.m_CurrentVis.applySettings(settings, "weka.gui.workbench.visualizepanel");
/*  915:     */               }
/*  916:1037 */               ClustererPanel.this.m_CurrentVis.setName(name + " (" + inst.relationName() + ")");
/*  917:1038 */               ClustererPanel.this.m_CurrentVis.setLog(ClustererPanel.this.m_Log);
/*  918:     */               try
/*  919:     */               {
/*  920:1040 */                 ClustererPanel.this.m_CurrentVis.addPlot(plotInstances.getPlotData(name));
/*  921:     */               }
/*  922:     */               catch (Exception ex)
/*  923:     */               {
/*  924:1042 */                 System.err.println(ex);
/*  925:     */               }
/*  926:1044 */               plotInstances.cleanUp();
/*  927:     */               
/*  928:1046 */               ArrayList<Object> vv = new ArrayList();
/*  929:1047 */               vv.add(fullClusterer);
/*  930:1048 */               Instances trainHeader = new Instances(ClustererPanel.this.m_Instances, 0);
/*  931:1049 */               vv.add(trainHeader);
/*  932:1050 */               if (ignoredAtts != null) {
/*  933:1051 */                 vv.add(ignoredAtts);
/*  934:     */               }
/*  935:1053 */               if (saveVis)
/*  936:     */               {
/*  937:1054 */                 vv.add(ClustererPanel.this.m_CurrentVis);
/*  938:1055 */                 if (grph != null) {
/*  939:1056 */                   vv.add(grph);
/*  940:     */                 }
/*  941:     */               }
/*  942:1060 */               ClustererPanel.this.m_History.addObject(name, vv);
/*  943:     */             }
/*  944:1062 */             if (isInterrupted())
/*  945:     */             {
/*  946:1063 */               ClustererPanel.this.m_Log.logMessage("Interrupted " + cname);
/*  947:1064 */               ClustererPanel.this.m_Log.statusMessage("See error log");
/*  948:     */             }
/*  949:1066 */             ClustererPanel.this.m_RunThread = null;
/*  950:1067 */             ClustererPanel.this.m_StartBut.setEnabled(true);
/*  951:1068 */             ClustererPanel.this.m_StopBut.setEnabled(false);
/*  952:1069 */             ClustererPanel.this.m_ignoreBut.setEnabled(true);
/*  953:1070 */             if ((ClustererPanel.this.m_Log instanceof TaskLogger)) {
/*  954:1071 */               ((TaskLogger)ClustererPanel.this.m_Log).taskFinished();
/*  955:     */             }
/*  956:     */           }
/*  957:     */         }
/*  958:1075 */       };
/*  959:1076 */       this.m_RunThread.setPriority(1);
/*  960:1077 */       this.m_RunThread.start();
/*  961:     */     }
/*  962:     */   }
/*  963:     */   
/*  964:     */   private Instances removeClass(Instances inst)
/*  965:     */   {
/*  966:1082 */     Remove af = new Remove();
/*  967:1083 */     Instances retI = null;
/*  968:     */     try
/*  969:     */     {
/*  970:1086 */       if (inst.classIndex() < 0)
/*  971:     */       {
/*  972:1087 */         retI = inst;
/*  973:     */       }
/*  974:     */       else
/*  975:     */       {
/*  976:1089 */         af.setAttributeIndices("" + (inst.classIndex() + 1));
/*  977:1090 */         af.setInvertSelection(false);
/*  978:1091 */         af.setInputFormat(inst);
/*  979:1092 */         retI = Filter.useFilter(inst, af);
/*  980:     */       }
/*  981:     */     }
/*  982:     */     catch (Exception e)
/*  983:     */     {
/*  984:1095 */       e.printStackTrace();
/*  985:     */     }
/*  986:1097 */     return retI;
/*  987:     */   }
/*  988:     */   
/*  989:     */   private Instances removeIgnoreCols(Instances inst)
/*  990:     */   {
/*  991:1105 */     if (this.m_ClassesToClustersBut.isSelected())
/*  992:     */     {
/*  993:1106 */       int classIndex = this.m_ClassCombo.getSelectedIndex();
/*  994:1107 */       if (this.m_ignoreKeyList.isSelectedIndex(classIndex)) {
/*  995:1108 */         this.m_ignoreKeyList.removeSelectionInterval(classIndex, classIndex);
/*  996:     */       }
/*  997:     */     }
/*  998:1111 */     int[] selected = this.m_ignoreKeyList.getSelectedIndices();
/*  999:1112 */     Remove af = new Remove();
/* 1000:1113 */     Instances retI = null;
/* 1001:     */     try
/* 1002:     */     {
/* 1003:1116 */       af.setAttributeIndicesArray(selected);
/* 1004:1117 */       af.setInvertSelection(false);
/* 1005:1118 */       af.setInputFormat(inst);
/* 1006:1119 */       retI = Filter.useFilter(inst, af);
/* 1007:     */     }
/* 1008:     */     catch (Exception e)
/* 1009:     */     {
/* 1010:1121 */       e.printStackTrace();
/* 1011:     */     }
/* 1012:1124 */     return retI;
/* 1013:     */   }
/* 1014:     */   
/* 1015:     */   private Instances removeIgnoreCols(Instances inst, int[] toIgnore)
/* 1016:     */   {
/* 1017:1129 */     Remove af = new Remove();
/* 1018:1130 */     Instances retI = null;
/* 1019:     */     try
/* 1020:     */     {
/* 1021:1133 */       af.setAttributeIndicesArray(toIgnore);
/* 1022:1134 */       af.setInvertSelection(false);
/* 1023:1135 */       af.setInputFormat(inst);
/* 1024:1136 */       retI = Filter.useFilter(inst, af);
/* 1025:     */     }
/* 1026:     */     catch (Exception e)
/* 1027:     */     {
/* 1028:1138 */       e.printStackTrace();
/* 1029:     */     }
/* 1030:1141 */     return retI;
/* 1031:     */   }
/* 1032:     */   
/* 1033:     */   protected void stopClusterer()
/* 1034:     */   {
/* 1035:1150 */     if (this.m_RunThread != null)
/* 1036:     */     {
/* 1037:1151 */       this.m_RunThread.interrupt();
/* 1038:     */       
/* 1039:     */ 
/* 1040:1154 */       this.m_RunThread.stop();
/* 1041:     */     }
/* 1042:     */   }
/* 1043:     */   
/* 1044:     */   protected void visualizeTree(String graphString, String treeName)
/* 1045:     */   {
/* 1046:1167 */     final JFrame jf = new JFrame("Weka Classifier Tree Visualizer: " + treeName);
/* 1047:     */     
/* 1048:1169 */     jf.setSize(500, 400);
/* 1049:1170 */     jf.getContentPane().setLayout(new BorderLayout());
/* 1050:1171 */     if (graphString.contains("digraph"))
/* 1051:     */     {
/* 1052:1172 */       TreeVisualizer tv = new TreeVisualizer(null, graphString, new PlaceNode2());
/* 1053:     */       
/* 1054:1174 */       jf.getContentPane().add(tv, "Center");
/* 1055:1175 */       jf.addWindowListener(new WindowAdapter()
/* 1056:     */       {
/* 1057:     */         public void windowClosing(WindowEvent e)
/* 1058:     */         {
/* 1059:1178 */           jf.dispose();
/* 1060:     */         }
/* 1061:1180 */       });
/* 1062:1181 */       jf.setVisible(true);
/* 1063:1182 */       tv.fitToScreen();
/* 1064:     */     }
/* 1065:1183 */     else if (graphString.startsWith("Newick:"))
/* 1066:     */     {
/* 1067:1184 */       HierarchyVisualizer tv = new HierarchyVisualizer(graphString.substring(7));
/* 1068:     */       
/* 1069:1186 */       jf.getContentPane().add(tv, "Center");
/* 1070:1187 */       jf.addWindowListener(new WindowAdapter()
/* 1071:     */       {
/* 1072:     */         public void windowClosing(WindowEvent e)
/* 1073:     */         {
/* 1074:1190 */           jf.dispose();
/* 1075:     */         }
/* 1076:1192 */       });
/* 1077:1193 */       jf.setVisible(true);
/* 1078:1194 */       tv.fitToScreen();
/* 1079:     */     }
/* 1080:     */   }
/* 1081:     */   
/* 1082:     */   protected void visualizeClusterAssignments(VisualizePanel sp)
/* 1083:     */   {
/* 1084:1204 */     if (sp != null)
/* 1085:     */     {
/* 1086:1205 */       String plotName = sp.getName();
/* 1087:1206 */       final JFrame jf = new JFrame("Weka Clusterer Visualize: " + plotName);
/* 1088:     */       
/* 1089:1208 */       jf.setSize(500, 400);
/* 1090:1209 */       jf.getContentPane().setLayout(new BorderLayout());
/* 1091:1210 */       jf.getContentPane().add(sp, "Center");
/* 1092:1211 */       jf.addWindowListener(new WindowAdapter()
/* 1093:     */       {
/* 1094:     */         public void windowClosing(WindowEvent e)
/* 1095:     */         {
/* 1096:1214 */           jf.dispose();
/* 1097:     */         }
/* 1098:1217 */       });
/* 1099:1218 */       jf.setVisible(true);
/* 1100:     */     }
/* 1101:     */   }
/* 1102:     */   
/* 1103:     */   protected void visualizeClusterer(String name, int x, int y)
/* 1104:     */   {
/* 1105:1232 */     final String selectedName = name;
/* 1106:1233 */     JPopupMenu resultListMenu = new JPopupMenu();
/* 1107:     */     
/* 1108:1235 */     JMenuItem visMainBuffer = new JMenuItem("View in main window");
/* 1109:1236 */     if (selectedName != null) {
/* 1110:1237 */       visMainBuffer.addActionListener(new ActionListener()
/* 1111:     */       {
/* 1112:     */         public void actionPerformed(ActionEvent e)
/* 1113:     */         {
/* 1114:1240 */           ClustererPanel.this.m_History.setSingle(selectedName);
/* 1115:     */         }
/* 1116:     */       });
/* 1117:     */     } else {
/* 1118:1244 */       visMainBuffer.setEnabled(false);
/* 1119:     */     }
/* 1120:1246 */     resultListMenu.add(visMainBuffer);
/* 1121:     */     
/* 1122:1248 */     JMenuItem visSepBuffer = new JMenuItem("View in separate window");
/* 1123:1249 */     if (selectedName != null) {
/* 1124:1250 */       visSepBuffer.addActionListener(new ActionListener()
/* 1125:     */       {
/* 1126:     */         public void actionPerformed(ActionEvent e)
/* 1127:     */         {
/* 1128:1253 */           ClustererPanel.this.m_History.openFrame(selectedName);
/* 1129:     */         }
/* 1130:     */       });
/* 1131:     */     } else {
/* 1132:1257 */       visSepBuffer.setEnabled(false);
/* 1133:     */     }
/* 1134:1259 */     resultListMenu.add(visSepBuffer);
/* 1135:     */     
/* 1136:1261 */     JMenuItem saveOutput = new JMenuItem("Save result buffer");
/* 1137:1262 */     if (selectedName != null) {
/* 1138:1263 */       saveOutput.addActionListener(new ActionListener()
/* 1139:     */       {
/* 1140:     */         public void actionPerformed(ActionEvent e)
/* 1141:     */         {
/* 1142:1266 */           ClustererPanel.this.saveBuffer(selectedName);
/* 1143:     */         }
/* 1144:     */       });
/* 1145:     */     } else {
/* 1146:1270 */       saveOutput.setEnabled(false);
/* 1147:     */     }
/* 1148:1272 */     resultListMenu.add(saveOutput);
/* 1149:     */     
/* 1150:1274 */     JMenuItem deleteOutput = new JMenuItem("Delete result buffer");
/* 1151:1275 */     if (selectedName != null) {
/* 1152:1276 */       deleteOutput.addActionListener(new ActionListener()
/* 1153:     */       {
/* 1154:     */         public void actionPerformed(ActionEvent e)
/* 1155:     */         {
/* 1156:1279 */           ClustererPanel.this.m_History.removeResult(selectedName);
/* 1157:     */         }
/* 1158:     */       });
/* 1159:     */     } else {
/* 1160:1283 */       deleteOutput.setEnabled(false);
/* 1161:     */     }
/* 1162:1285 */     resultListMenu.add(deleteOutput);
/* 1163:     */     
/* 1164:1287 */     resultListMenu.addSeparator();
/* 1165:     */     
/* 1166:1289 */     JMenuItem loadModel = new JMenuItem("Load model");
/* 1167:1290 */     loadModel.addActionListener(new ActionListener()
/* 1168:     */     {
/* 1169:     */       public void actionPerformed(ActionEvent e)
/* 1170:     */       {
/* 1171:1293 */         ClustererPanel.this.loadClusterer();
/* 1172:     */       }
/* 1173:1295 */     });
/* 1174:1296 */     resultListMenu.add(loadModel);
/* 1175:     */     
/* 1176:1298 */     ArrayList<Object> o = null;
/* 1177:1299 */     if (selectedName != null) {
/* 1178:1300 */       o = (ArrayList)this.m_History.getNamedObject(selectedName);
/* 1179:     */     }
/* 1180:1303 */     VisualizePanel temp_vp = null;
/* 1181:1304 */     String temp_grph = null;
/* 1182:1305 */     Clusterer temp_clusterer = null;
/* 1183:1306 */     Instances temp_trainHeader = null;
/* 1184:1307 */     int[] temp_ignoreAtts = null;
/* 1185:1309 */     if (o != null) {
/* 1186:1310 */       for (int i = 0; i < o.size(); i++)
/* 1187:     */       {
/* 1188:1311 */         Object temp = o.get(i);
/* 1189:1312 */         if ((temp instanceof Clusterer)) {
/* 1190:1313 */           temp_clusterer = (Clusterer)temp;
/* 1191:1314 */         } else if ((temp instanceof Instances)) {
/* 1192:1315 */           temp_trainHeader = (Instances)temp;
/* 1193:1316 */         } else if ((temp instanceof int[])) {
/* 1194:1317 */           temp_ignoreAtts = (int[])temp;
/* 1195:1318 */         } else if ((temp instanceof VisualizePanel)) {
/* 1196:1319 */           temp_vp = (VisualizePanel)temp;
/* 1197:1320 */         } else if ((temp instanceof String)) {
/* 1198:1321 */           temp_grph = (String)temp;
/* 1199:     */         }
/* 1200:     */       }
/* 1201:     */     }
/* 1202:1326 */     final VisualizePanel vp = temp_vp;
/* 1203:1327 */     final String grph = temp_grph;
/* 1204:1328 */     final Clusterer clusterer = temp_clusterer;
/* 1205:1329 */     final Instances trainHeader = temp_trainHeader;
/* 1206:1330 */     final int[] ignoreAtts = temp_ignoreAtts;
/* 1207:     */     
/* 1208:1332 */     JMenuItem saveModel = new JMenuItem("Save model");
/* 1209:1333 */     if (clusterer != null) {
/* 1210:1334 */       saveModel.addActionListener(new ActionListener()
/* 1211:     */       {
/* 1212:     */         public void actionPerformed(ActionEvent e)
/* 1213:     */         {
/* 1214:1337 */           ClustererPanel.this.saveClusterer(selectedName, clusterer, trainHeader, ignoreAtts);
/* 1215:     */         }
/* 1216:     */       });
/* 1217:     */     } else {
/* 1218:1341 */       saveModel.setEnabled(false);
/* 1219:     */     }
/* 1220:1343 */     resultListMenu.add(saveModel);
/* 1221:     */     
/* 1222:1345 */     JMenuItem reEvaluate = new JMenuItem("Re-evaluate model on current test set");
/* 1223:1347 */     if ((clusterer != null) && (this.m_TestInstances != null)) {
/* 1224:1348 */       reEvaluate.addActionListener(new ActionListener()
/* 1225:     */       {
/* 1226:     */         public void actionPerformed(ActionEvent e)
/* 1227:     */         {
/* 1228:1351 */           ClustererPanel.this.reevaluateModel(selectedName, clusterer, trainHeader, ignoreAtts);
/* 1229:     */         }
/* 1230:     */       });
/* 1231:     */     } else {
/* 1232:1355 */       reEvaluate.setEnabled(false);
/* 1233:     */     }
/* 1234:1357 */     resultListMenu.add(reEvaluate);
/* 1235:     */     
/* 1236:1359 */     JMenuItem reApplyConfig = new JMenuItem("Re-apply this model's configuration");
/* 1237:1361 */     if (clusterer != null) {
/* 1238:1362 */       reApplyConfig.addActionListener(new ActionListener()
/* 1239:     */       {
/* 1240:     */         public void actionPerformed(ActionEvent e)
/* 1241:     */         {
/* 1242:1365 */           ClustererPanel.this.m_ClustererEditor.setValue(clusterer);
/* 1243:     */         }
/* 1244:     */       });
/* 1245:     */     } else {
/* 1246:1369 */       reApplyConfig.setEnabled(false);
/* 1247:     */     }
/* 1248:1371 */     resultListMenu.add(reApplyConfig);
/* 1249:     */     
/* 1250:1373 */     resultListMenu.addSeparator();
/* 1251:     */     
/* 1252:1375 */     JMenuItem visClusts = new JMenuItem("Visualize cluster assignments");
/* 1253:1376 */     if (vp != null) {
/* 1254:1377 */       visClusts.addActionListener(new ActionListener()
/* 1255:     */       {
/* 1256:     */         public void actionPerformed(ActionEvent e)
/* 1257:     */         {
/* 1258:1380 */           ClustererPanel.this.visualizeClusterAssignments(vp);
/* 1259:     */         }
/* 1260:     */       });
/* 1261:     */     } else {
/* 1262:1385 */       visClusts.setEnabled(false);
/* 1263:     */     }
/* 1264:1387 */     resultListMenu.add(visClusts);
/* 1265:     */     
/* 1266:1389 */     JMenuItem visTree = new JMenuItem("Visualize tree");
/* 1267:1390 */     if (grph != null) {
/* 1268:1391 */       visTree.addActionListener(new ActionListener()
/* 1269:     */       {
/* 1270:     */         public void actionPerformed(ActionEvent e)
/* 1271:     */         {
/* 1272:     */           String title;
/* 1273:     */           String title;
/* 1274:1395 */           if (vp != null) {
/* 1275:1396 */             title = vp.getName();
/* 1276:     */           } else {
/* 1277:1398 */             title = selectedName;
/* 1278:     */           }
/* 1279:1400 */           ClustererPanel.this.visualizeTree(grph, title);
/* 1280:     */         }
/* 1281:     */       });
/* 1282:     */     } else {
/* 1283:1404 */       visTree.setEnabled(false);
/* 1284:     */     }
/* 1285:1406 */     resultListMenu.add(visTree);
/* 1286:     */     
/* 1287:     */ 
/* 1288:1409 */     JMenu visPlugins = new JMenu("Plugins");
/* 1289:1410 */     boolean availablePlugins = false;
/* 1290:1413 */     if (grph != null)
/* 1291:     */     {
/* 1292:1415 */       Vector<String> pluginsVector = GenericObjectEditor.getClassnames(TreeVisualizePlugin.class.getName());
/* 1293:1417 */       for (int i = 0; i < pluginsVector.size(); i++)
/* 1294:     */       {
/* 1295:1418 */         String className = (String)pluginsVector.elementAt(i);
/* 1296:     */         try
/* 1297:     */         {
/* 1298:1420 */           TreeVisualizePlugin plugin = (TreeVisualizePlugin)Class.forName(className).newInstance();
/* 1299:1422 */           if (plugin != null)
/* 1300:     */           {
/* 1301:1425 */             availablePlugins = true;
/* 1302:1426 */             JMenuItem pluginMenuItem = plugin.getVisualizeMenuItem(grph, selectedName);
/* 1303:     */             
/* 1304:1428 */             Version version = new Version();
/* 1305:1429 */             if (pluginMenuItem != null)
/* 1306:     */             {
/* 1307:1430 */               if (version.compareTo(plugin.getMinVersion()) < 0) {
/* 1308:1431 */                 pluginMenuItem.setText(pluginMenuItem.getText() + " (weka outdated)");
/* 1309:     */               }
/* 1310:1434 */               if (version.compareTo(plugin.getMaxVersion()) >= 0) {
/* 1311:1435 */                 pluginMenuItem.setText(pluginMenuItem.getText() + " (plugin outdated)");
/* 1312:     */               }
/* 1313:1438 */               visPlugins.add(pluginMenuItem);
/* 1314:     */             }
/* 1315:     */           }
/* 1316:     */         }
/* 1317:     */         catch (Exception e) {}
/* 1318:     */       }
/* 1319:     */     }
/* 1320:1446 */     if (availablePlugins) {
/* 1321:1447 */       resultListMenu.add(visPlugins);
/* 1322:     */     }
/* 1323:1450 */     resultListMenu.show(this.m_History.getList(), x, y);
/* 1324:     */   }
/* 1325:     */   
/* 1326:     */   protected void saveBuffer(String name)
/* 1327:     */   {
/* 1328:1459 */     StringBuffer sb = this.m_History.getNamedBuffer(name);
/* 1329:1460 */     if ((sb != null) && 
/* 1330:1461 */       (this.m_SaveOut.save(sb))) {
/* 1331:1462 */       this.m_Log.logMessage("Save successful.");
/* 1332:     */     }
/* 1333:     */   }
/* 1334:     */   
/* 1335:     */   private void setIgnoreColumns()
/* 1336:     */   {
/* 1337:1468 */     ListSelectorDialog jd = new ListSelectorDialog(null, this.m_ignoreKeyList);
/* 1338:     */     
/* 1339:     */ 
/* 1340:1471 */     int result = jd.showDialog();
/* 1341:1473 */     if (result != 0) {
/* 1342:1475 */       this.m_ignoreKeyList.clearSelection();
/* 1343:     */     }
/* 1344:1477 */     updateCapabilitiesFilter(this.m_ClustererEditor.getCapabilitiesFilter());
/* 1345:     */   }
/* 1346:     */   
/* 1347:     */   protected void saveClusterer(String name, Clusterer clusterer, Instances trainHeader, int[] ignoredAtts)
/* 1348:     */   {
/* 1349:1486 */     File sFile = null;
/* 1350:1487 */     boolean saveOK = true;
/* 1351:     */     
/* 1352:1489 */     int returnVal = this.m_FileChooser.showSaveDialog(this);
/* 1353:1490 */     if (returnVal == 0)
/* 1354:     */     {
/* 1355:1491 */       sFile = this.m_FileChooser.getSelectedFile();
/* 1356:1492 */       if (!sFile.getName().toLowerCase().endsWith(MODEL_FILE_EXTENSION)) {
/* 1357:1493 */         sFile = new File(sFile.getParent(), sFile.getName() + MODEL_FILE_EXTENSION);
/* 1358:     */       }
/* 1359:1496 */       this.m_Log.statusMessage("Saving model to file...");
/* 1360:     */       try
/* 1361:     */       {
/* 1362:1499 */         OutputStream os = new FileOutputStream(sFile);
/* 1363:1500 */         if (sFile.getName().endsWith(".gz")) {
/* 1364:1501 */           os = new GZIPOutputStream(os);
/* 1365:     */         }
/* 1366:1503 */         ObjectOutputStream objectOutputStream = new ObjectOutputStream(os);
/* 1367:1504 */         objectOutputStream.writeObject(clusterer);
/* 1368:1505 */         if (trainHeader != null) {
/* 1369:1506 */           objectOutputStream.writeObject(trainHeader);
/* 1370:     */         }
/* 1371:1508 */         if (ignoredAtts != null) {
/* 1372:1509 */           objectOutputStream.writeObject(ignoredAtts);
/* 1373:     */         }
/* 1374:1511 */         objectOutputStream.flush();
/* 1375:1512 */         objectOutputStream.close();
/* 1376:     */       }
/* 1377:     */       catch (Exception e)
/* 1378:     */       {
/* 1379:1515 */         JOptionPane.showMessageDialog(null, e, "Save Failed", 0);
/* 1380:     */         
/* 1381:1517 */         saveOK = false;
/* 1382:     */       }
/* 1383:1519 */       if (saveOK) {
/* 1384:1520 */         this.m_Log.logMessage("Saved model (" + name + ") to file '" + sFile.getName() + "'");
/* 1385:     */       }
/* 1386:1523 */       this.m_Log.statusMessage("OK");
/* 1387:     */     }
/* 1388:     */   }
/* 1389:     */   
/* 1390:     */   protected void loadClusterer()
/* 1391:     */   {
/* 1392:1532 */     int returnVal = this.m_FileChooser.showOpenDialog(this);
/* 1393:1533 */     if (returnVal == 0)
/* 1394:     */     {
/* 1395:1534 */       File selected = this.m_FileChooser.getSelectedFile();
/* 1396:1535 */       Clusterer clusterer = null;
/* 1397:1536 */       Instances trainHeader = null;
/* 1398:1537 */       int[] ignoredAtts = null;
/* 1399:     */       
/* 1400:1539 */       this.m_Log.statusMessage("Loading model from file...");
/* 1401:     */       try
/* 1402:     */       {
/* 1403:1542 */         InputStream is = new FileInputStream(selected);
/* 1404:1543 */         if (selected.getName().endsWith(".gz")) {
/* 1405:1544 */           is = new GZIPInputStream(is);
/* 1406:     */         }
/* 1407:1546 */         ObjectInputStream objectInputStream = new ObjectInputStream(is);
/* 1408:1547 */         clusterer = (Clusterer)objectInputStream.readObject();
/* 1409:     */         try
/* 1410:     */         {
/* 1411:1549 */           trainHeader = (Instances)objectInputStream.readObject();
/* 1412:1550 */           ignoredAtts = (int[])objectInputStream.readObject();
/* 1413:     */         }
/* 1414:     */         catch (Exception e) {}
/* 1415:1553 */         objectInputStream.close();
/* 1416:     */       }
/* 1417:     */       catch (Exception e)
/* 1418:     */       {
/* 1419:1556 */         JOptionPane.showMessageDialog(null, e, "Load Failed", 0);
/* 1420:     */       }
/* 1421:1560 */       this.m_Log.statusMessage("OK");
/* 1422:1562 */       if (clusterer != null)
/* 1423:     */       {
/* 1424:1563 */         this.m_Log.logMessage("Loaded model from file '" + selected.getName() + "'");
/* 1425:1564 */         String name = new SimpleDateFormat("HH:mm:ss - ").format(new Date());
/* 1426:1565 */         String cname = clusterer.getClass().getName();
/* 1427:1566 */         if (cname.startsWith("weka.clusterers.")) {
/* 1428:1567 */           cname = cname.substring("weka.clusterers.".length());
/* 1429:     */         }
/* 1430:1569 */         name = name + cname + " from file '" + selected.getName() + "'";
/* 1431:1570 */         StringBuffer outBuff = new StringBuffer();
/* 1432:     */         
/* 1433:1572 */         outBuff.append("=== Model information ===\n\n");
/* 1434:1573 */         outBuff.append("Filename:     " + selected.getName() + "\n");
/* 1435:1574 */         outBuff.append("Scheme:       " + clusterer.getClass().getName());
/* 1436:1575 */         if ((clusterer instanceof OptionHandler))
/* 1437:     */         {
/* 1438:1576 */           String[] o = ((OptionHandler)clusterer).getOptions();
/* 1439:1577 */           outBuff.append(" " + Utils.joinOptions(o));
/* 1440:     */         }
/* 1441:1579 */         outBuff.append("\n");
/* 1442:1581 */         if (trainHeader != null)
/* 1443:     */         {
/* 1444:1583 */           outBuff.append("Relation:     " + trainHeader.relationName() + '\n');
/* 1445:1584 */           outBuff.append("Attributes:   " + trainHeader.numAttributes() + '\n');
/* 1446:1585 */           if (trainHeader.numAttributes() < 100)
/* 1447:     */           {
/* 1448:1586 */             boolean[] selectedAtts = new boolean[trainHeader.numAttributes()];
/* 1449:1587 */             for (int i = 0; i < trainHeader.numAttributes(); i++) {
/* 1450:1588 */               selectedAtts[i] = true;
/* 1451:     */             }
/* 1452:1591 */             if (ignoredAtts != null) {
/* 1453:1592 */               for (int i = 0; i < ignoredAtts.length; i++) {
/* 1454:1593 */                 selectedAtts[ignoredAtts[i]] = false;
/* 1455:     */               }
/* 1456:     */             }
/* 1457:1597 */             for (int i = 0; i < trainHeader.numAttributes(); i++) {
/* 1458:1598 */               if (selectedAtts[i] != 0) {
/* 1459:1599 */                 outBuff.append("              " + trainHeader.attribute(i).name() + '\n');
/* 1460:     */               }
/* 1461:     */             }
/* 1462:1603 */             if (ignoredAtts != null)
/* 1463:     */             {
/* 1464:1604 */               outBuff.append("Ignored:\n");
/* 1465:1605 */               for (int ignoredAtt : ignoredAtts) {
/* 1466:1606 */                 outBuff.append("              " + trainHeader.attribute(ignoredAtt).name() + '\n');
/* 1467:     */               }
/* 1468:     */             }
/* 1469:     */           }
/* 1470:     */           else
/* 1471:     */           {
/* 1472:1611 */             outBuff.append("              [list of attributes omitted]\n");
/* 1473:     */           }
/* 1474:     */         }
/* 1475:     */         else
/* 1476:     */         {
/* 1477:1614 */           outBuff.append("\nTraining data unknown\n");
/* 1478:     */         }
/* 1479:1617 */         outBuff.append("\n=== Clustering model ===\n\n");
/* 1480:1618 */         outBuff.append(clusterer.toString() + "\n");
/* 1481:     */         
/* 1482:1620 */         this.m_History.addResult(name, outBuff);
/* 1483:1621 */         this.m_History.setSingle(name);
/* 1484:1622 */         ArrayList<Object> vv = new ArrayList();
/* 1485:1623 */         vv.add(clusterer);
/* 1486:1624 */         if (trainHeader != null) {
/* 1487:1625 */           vv.add(trainHeader);
/* 1488:     */         }
/* 1489:1627 */         if (ignoredAtts != null) {
/* 1490:1628 */           vv.add(ignoredAtts);
/* 1491:     */         }
/* 1492:1631 */         String grph = null;
/* 1493:1632 */         if ((clusterer instanceof Drawable)) {
/* 1494:     */           try
/* 1495:     */           {
/* 1496:1634 */             grph = ((Drawable)clusterer).graph();
/* 1497:     */           }
/* 1498:     */           catch (Exception ex) {}
/* 1499:     */         }
/* 1500:1638 */         if (grph != null) {
/* 1501:1639 */           vv.add(grph);
/* 1502:     */         }
/* 1503:1642 */         this.m_History.addObject(name, vv);
/* 1504:     */       }
/* 1505:     */     }
/* 1506:     */   }
/* 1507:     */   
/* 1508:     */   protected void reevaluateModel(final String name, final Clusterer clusterer, final Instances trainHeader, final int[] ignoredAtts)
/* 1509:     */   {
/* 1510:1660 */     if (this.m_RunThread == null)
/* 1511:     */     {
/* 1512:1661 */       this.m_StartBut.setEnabled(false);
/* 1513:1662 */       this.m_StopBut.setEnabled(true);
/* 1514:1663 */       this.m_ignoreBut.setEnabled(false);
/* 1515:1664 */       this.m_RunThread = new Thread()
/* 1516:     */       {
/* 1517:     */         public void run()
/* 1518:     */         {
/* 1519:1668 */           ClustererPanel.this.m_Log.statusMessage("Setting up...");
/* 1520:     */           
/* 1521:1670 */           StringBuffer outBuff = ClustererPanel.this.m_History.getNamedBuffer(name);
/* 1522:1671 */           Instances userTest = null;
/* 1523:     */           
/* 1524:1673 */           ClustererAssignmentsPlotInstances plotInstances = ExplorerDefaults.getClustererAssignmentsPlotInstances();
/* 1525:     */           
/* 1526:1675 */           plotInstances.setClusterer(clusterer);
/* 1527:1676 */           if (ClustererPanel.this.m_TestInstances != null) {
/* 1528:1677 */             userTest = new Instances(ClustererPanel.this.m_TestInstances);
/* 1529:     */           }
/* 1530:1680 */           boolean saveVis = ClustererPanel.this.m_StorePredictionsBut.isSelected();
/* 1531:1681 */           String grph = null;
/* 1532:     */           try
/* 1533:     */           {
/* 1534:1684 */             if (userTest == null) {
/* 1535:1685 */               throw new Exception("No user test set has been opened");
/* 1536:     */             }
/* 1537:1687 */             if ((trainHeader != null) && (!trainHeader.equalHeaders(userTest))) {
/* 1538:1688 */               throw new Exception("Train and test set are not compatible\n" + trainHeader.equalHeadersMsg(userTest));
/* 1539:     */             }
/* 1540:1692 */             ClustererPanel.this.m_Log.statusMessage("Evaluating on test data...");
/* 1541:1693 */             ClustererPanel.this.m_Log.logMessage("Re-evaluating clusterer (" + name + ") on test set");
/* 1542:     */             
/* 1543:     */ 
/* 1544:1696 */             ClustererPanel.this.m_Log.logMessage("Started reevaluate model");
/* 1545:1697 */             if ((ClustererPanel.this.m_Log instanceof TaskLogger)) {
/* 1546:1698 */               ((TaskLogger)ClustererPanel.this.m_Log).taskStarted();
/* 1547:     */             }
/* 1548:1700 */             ClusterEvaluation eval = new ClusterEvaluation();
/* 1549:1701 */             eval.setClusterer(clusterer);
/* 1550:     */             
/* 1551:1703 */             Instances userTestT = new Instances(userTest);
/* 1552:1704 */             if (ignoredAtts != null) {
/* 1553:1705 */               userTestT = ClustererPanel.this.removeIgnoreCols(userTestT, ignoredAtts);
/* 1554:     */             }
/* 1555:1708 */             eval.evaluateClusterer(userTestT);
/* 1556:     */             
/* 1557:1710 */             plotInstances.setClusterEvaluation(eval);
/* 1558:1711 */             plotInstances.setInstances(userTest);
/* 1559:1712 */             plotInstances.setUp();
/* 1560:     */             
/* 1561:1714 */             outBuff.append("\n=== Re-evaluation on test set ===\n\n");
/* 1562:1715 */             outBuff.append("User supplied test set\n");
/* 1563:1716 */             outBuff.append("Relation:     " + userTest.relationName() + '\n');
/* 1564:1717 */             outBuff.append("Instances:    " + userTest.numInstances() + '\n');
/* 1565:1718 */             outBuff.append("Attributes:   " + userTest.numAttributes() + "\n\n");
/* 1566:1720 */             if (trainHeader == null) {
/* 1567:1721 */               outBuff.append("NOTE - if test set is not compatible then results are unpredictable\n\n");
/* 1568:     */             }
/* 1569:1726 */             outBuff.append(eval.clusterResultsToString());
/* 1570:1727 */             outBuff.append("\n");
/* 1571:1728 */             ClustererPanel.this.m_History.updateResult(name);
/* 1572:1729 */             ClustererPanel.this.m_Log.logMessage("Finished re-evaluation");
/* 1573:1730 */             ClustererPanel.this.m_Log.statusMessage("OK");
/* 1574:     */           }
/* 1575:     */           catch (Exception ex)
/* 1576:     */           {
/* 1577:     */             Settings settings;
/* 1578:     */             ArrayList<Object> vv;
/* 1579:1732 */             ex.printStackTrace();
/* 1580:1733 */             ClustererPanel.this.m_Log.logMessage(ex.getMessage());
/* 1581:1734 */             JOptionPane.showMessageDialog(ClustererPanel.this, "Problem evaluating clusterer:\n" + ex.getMessage(), "Evaluate clusterer", 0);
/* 1582:     */             
/* 1583:     */ 
/* 1584:1737 */             ClustererPanel.this.m_Log.statusMessage("Problem evaluating clusterer");
/* 1585:     */           }
/* 1586:     */           finally
/* 1587:     */           {
/* 1588:     */             Settings settings;
/* 1589:     */             ArrayList<Object> vv;
/* 1590:1740 */             if (plotInstances != null)
/* 1591:     */             {
/* 1592:1741 */               ClustererPanel.this.m_CurrentVis = new VisualizePanel();
/* 1593:1742 */               if (ClustererPanel.this.getMainApplication() != null)
/* 1594:     */               {
/* 1595:1743 */                 Settings settings = ClustererPanel.this.getMainApplication().getApplicationSettings();
/* 1596:1744 */                 ClustererPanel.this.m_CurrentVis.applySettings(settings, "weka.gui.workbench.visualizepanel");
/* 1597:     */               }
/* 1598:1747 */               ClustererPanel.this.m_CurrentVis.setName(name + " (" + userTest.relationName() + ")");
/* 1599:1748 */               ClustererPanel.this.m_CurrentVis.setLog(ClustererPanel.this.m_Log);
/* 1600:     */               try
/* 1601:     */               {
/* 1602:1750 */                 ClustererPanel.this.m_CurrentVis.addPlot(plotInstances.getPlotData(name));
/* 1603:     */               }
/* 1604:     */               catch (Exception ex)
/* 1605:     */               {
/* 1606:1752 */                 System.err.println(ex);
/* 1607:     */               }
/* 1608:1755 */               ArrayList<Object> vv = new ArrayList();
/* 1609:1756 */               vv.add(clusterer);
/* 1610:1757 */               if (trainHeader != null) {
/* 1611:1758 */                 vv.add(trainHeader);
/* 1612:     */               }
/* 1613:1760 */               if (ignoredAtts != null) {
/* 1614:1761 */                 vv.add(ignoredAtts);
/* 1615:     */               }
/* 1616:1763 */               if (saveVis)
/* 1617:     */               {
/* 1618:1764 */                 vv.add(ClustererPanel.this.m_CurrentVis);
/* 1619:1765 */                 if (grph != null) {
/* 1620:1766 */                   vv.add(grph);
/* 1621:     */                 }
/* 1622:     */               }
/* 1623:1770 */               ClustererPanel.this.m_History.addObject(name, vv);
/* 1624:     */             }
/* 1625:1773 */             if (isInterrupted())
/* 1626:     */             {
/* 1627:1774 */               ClustererPanel.this.m_Log.logMessage("Interrupted reevaluate model");
/* 1628:1775 */               ClustererPanel.this.m_Log.statusMessage("See error log");
/* 1629:     */             }
/* 1630:1777 */             ClustererPanel.this.m_RunThread = null;
/* 1631:1778 */             ClustererPanel.this.m_StartBut.setEnabled(true);
/* 1632:1779 */             ClustererPanel.this.m_StopBut.setEnabled(false);
/* 1633:1780 */             ClustererPanel.this.m_ignoreBut.setEnabled(true);
/* 1634:1781 */             if ((ClustererPanel.this.m_Log instanceof TaskLogger)) {
/* 1635:1782 */               ((TaskLogger)ClustererPanel.this.m_Log).taskFinished();
/* 1636:     */             }
/* 1637:     */           }
/* 1638:     */         }
/* 1639:1787 */       };
/* 1640:1788 */       this.m_RunThread.setPriority(1);
/* 1641:1789 */       this.m_RunThread.start();
/* 1642:     */     }
/* 1643:     */   }
/* 1644:     */   
/* 1645:     */   protected void updateCapabilitiesFilter(Capabilities filter)
/* 1646:     */   {
/* 1647:1802 */     if (filter == null)
/* 1648:     */     {
/* 1649:1803 */       this.m_ClustererEditor.setCapabilitiesFilter(new Capabilities(null)); return;
/* 1650:     */     }
/* 1651:     */     Instances tempInst;
/* 1652:     */     Instances tempInst;
/* 1653:1807 */     if (!ExplorerDefaults.getInitGenericObjectEditorFilter()) {
/* 1654:1808 */       tempInst = new Instances(this.m_Instances, 0);
/* 1655:     */     } else {
/* 1656:1810 */       tempInst = new Instances(this.m_Instances);
/* 1657:     */     }
/* 1658:1812 */     tempInst.setClassIndex(-1);
/* 1659:1814 */     if (!this.m_ignoreKeyList.isSelectionEmpty()) {
/* 1660:1815 */       tempInst = removeIgnoreCols(tempInst);
/* 1661:     */     }
/* 1662:1818 */     if (this.m_ClassesToClustersBut.isSelected())
/* 1663:     */     {
/* 1664:1820 */       String classSelection = this.m_ClassCombo.getSelectedItem().toString();
/* 1665:1821 */       classSelection = classSelection.substring(classSelection.indexOf(")") + 1).trim();
/* 1666:     */       
/* 1667:1823 */       int classIndex = tempInst.attribute(classSelection).index();
/* 1668:     */       
/* 1669:1825 */       Remove rm = new Remove();
/* 1670:1826 */       rm.setAttributeIndices("" + (classIndex + 1));
/* 1671:     */       try
/* 1672:     */       {
/* 1673:1828 */         rm.setInputFormat(tempInst);
/* 1674:1829 */         tempInst = Filter.useFilter(tempInst, rm);
/* 1675:     */       }
/* 1676:     */       catch (Exception e)
/* 1677:     */       {
/* 1678:1831 */         e.printStackTrace();
/* 1679:     */       }
/* 1680:     */     }
/* 1681:     */     Capabilities filterClass;
/* 1682:     */     try
/* 1683:     */     {
/* 1684:1836 */       filterClass = Capabilities.forInstances(tempInst);
/* 1685:     */     }
/* 1686:     */     catch (Exception e)
/* 1687:     */     {
/* 1688:1838 */       filterClass = new Capabilities(null);
/* 1689:     */     }
/* 1690:1841 */     this.m_ClustererEditor.setCapabilitiesFilter(filterClass);
/* 1691:     */     
/* 1692:     */ 
/* 1693:1844 */     this.m_StartBut.setEnabled(true);
/* 1694:1845 */     Capabilities currentFilter = this.m_ClustererEditor.getCapabilitiesFilter();
/* 1695:1846 */     Clusterer clusterer = (Clusterer)this.m_ClustererEditor.getValue();
/* 1696:1847 */     Capabilities currentSchemeCapabilities = null;
/* 1697:1848 */     if ((clusterer != null) && (currentFilter != null) && ((clusterer instanceof CapabilitiesHandler)))
/* 1698:     */     {
/* 1699:1850 */       currentSchemeCapabilities = ((CapabilitiesHandler)clusterer).getCapabilities();
/* 1700:1853 */       if ((!currentSchemeCapabilities.supportsMaybe(currentFilter)) && (!currentSchemeCapabilities.supports(currentFilter))) {
/* 1701:1855 */         this.m_StartBut.setEnabled(false);
/* 1702:     */       }
/* 1703:     */     }
/* 1704:     */   }
/* 1705:     */   
/* 1706:     */   public void capabilitiesFilterChanged(Explorer.CapabilitiesFilterChangeEvent e)
/* 1707:     */   {
/* 1708:1867 */     if (e.getFilter() == null) {
/* 1709:1868 */       updateCapabilitiesFilter(null);
/* 1710:     */     } else {
/* 1711:1870 */       updateCapabilitiesFilter((Capabilities)e.getFilter().clone());
/* 1712:     */     }
/* 1713:     */   }
/* 1714:     */   
/* 1715:     */   public void setExplorer(Explorer parent)
/* 1716:     */   {
/* 1717:1882 */     this.m_Explorer = parent;
/* 1718:     */   }
/* 1719:     */   
/* 1720:     */   public Explorer getExplorer()
/* 1721:     */   {
/* 1722:1892 */     return this.m_Explorer;
/* 1723:     */   }
/* 1724:     */   
/* 1725:     */   public String getTabTitle()
/* 1726:     */   {
/* 1727:1902 */     return "Cluster";
/* 1728:     */   }
/* 1729:     */   
/* 1730:     */   public String getTabTitleToolTip()
/* 1731:     */   {
/* 1732:1912 */     return "Identify instance clusters";
/* 1733:     */   }
/* 1734:     */   
/* 1735:     */   public boolean requiresLog()
/* 1736:     */   {
/* 1737:1917 */     return true;
/* 1738:     */   }
/* 1739:     */   
/* 1740:     */   public boolean acceptsInstances()
/* 1741:     */   {
/* 1742:1922 */     return true;
/* 1743:     */   }
/* 1744:     */   
/* 1745:     */   public Defaults getDefaultSettings()
/* 1746:     */   {
/* 1747:1927 */     return new ClustererPanelDefaults();
/* 1748:     */   }
/* 1749:     */   
/* 1750:     */   public boolean okToBeActive()
/* 1751:     */   {
/* 1752:1932 */     return this.m_Instances != null;
/* 1753:     */   }
/* 1754:     */   
/* 1755:     */   public void setActive(boolean active)
/* 1756:     */   {
/* 1757:1937 */     super.setActive(active);
/* 1758:1938 */     if (this.m_isActive) {
/* 1759:1939 */       settingsChanged();
/* 1760:     */     }
/* 1761:     */   }
/* 1762:     */   
/* 1763:     */   public void settingsChanged()
/* 1764:     */   {
/* 1765:1945 */     if (getMainApplication() != null)
/* 1766:     */     {
/* 1767:1946 */       if (!this.m_initialSettingsSet)
/* 1768:     */       {
/* 1769:1947 */         this.m_initialSettingsSet = true;
/* 1770:1948 */         Object initialC = getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), ClustererPanelDefaults.CLUSTERER_KEY, ClustererPanelDefaults.CLUSTERER, Environment.getSystemWide());
/* 1771:     */         
/* 1772:     */ 
/* 1773:     */ 
/* 1774:1952 */         this.m_ClustererEditor.setValue(initialC);
/* 1775:     */         
/* 1776:1954 */         TestMode iniitalTestMode = (TestMode)getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), ClustererPanelDefaults.TEST_MODE_KEY, ClustererPanelDefaults.TEST_MODE, Environment.getSystemWide());
/* 1777:     */         
/* 1778:     */ 
/* 1779:     */ 
/* 1780:1958 */         this.m_TrainBut.setSelected(iniitalTestMode == TestMode.USE_TRAINING_SET);
/* 1781:1959 */         this.m_PercentBut.setSelected(iniitalTestMode == TestMode.PERCENTAGE_SPLIT);
/* 1782:1960 */         this.m_TestSplitBut.setSelected(iniitalTestMode == TestMode.SUPPLIED_TEST_SET);
/* 1783:     */         
/* 1784:1962 */         this.m_ClassesToClustersBut.setSelected(iniitalTestMode == TestMode.CLASSES_TO_CLUSTERS_EVAL);
/* 1785:     */         
/* 1786:1964 */         this.m_StorePredictionsBut.setSelected(((Boolean)getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), ClustererPanelDefaults.STORE_CLUSTERS_FOR_VIS_KEY, Boolean.valueOf(true), Environment.getSystemWide())).booleanValue());
/* 1787:     */       }
/* 1788:1970 */       Font outputFont = (Font)getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), ClustererPanelDefaults.OUTPUT_FONT_KEY, ClustererPanelDefaults.OUTPUT_FONT, Environment.getSystemWide());
/* 1789:     */       
/* 1790:     */ 
/* 1791:     */ 
/* 1792:1974 */       this.m_OutText.setFont(outputFont);
/* 1793:1975 */       Color textColor = (Color)getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), ClustererPanelDefaults.OUTPUT_TEXT_COLOR_KEY, ClustererPanelDefaults.OUTPUT_TEXT_COLOR, Environment.getSystemWide());
/* 1794:     */       
/* 1795:     */ 
/* 1796:     */ 
/* 1797:     */ 
/* 1798:     */ 
/* 1799:1981 */       this.m_OutText.setForeground(textColor);
/* 1800:1982 */       Color outputBackgroundColor = (Color)getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), ClustererPanelDefaults.OUTPUT_BACKGROUND_COLOR_KEY, ClustererPanelDefaults.OUTPUT_BACKGROUND_COLOR, Environment.getSystemWide());
/* 1801:     */       
/* 1802:     */ 
/* 1803:     */ 
/* 1804:     */ 
/* 1805:     */ 
/* 1806:1988 */       this.m_OutText.setBackground(outputBackgroundColor);
/* 1807:1989 */       this.m_History.setBackground(outputBackgroundColor);
/* 1808:     */     }
/* 1809:     */   }
/* 1810:     */   
/* 1811:     */   public static enum TestMode
/* 1812:     */   {
/* 1813:1994 */     PERCENTAGE_SPLIT,  USE_TRAINING_SET,  SUPPLIED_TEST_SET,  CLASSES_TO_CLUSTERS_EVAL;
/* 1814:     */     
/* 1815:     */     private TestMode() {}
/* 1816:     */   }
/* 1817:     */   
/* 1818:     */   protected static final class ClustererPanelDefaults
/* 1819:     */     extends Defaults
/* 1820:     */   {
/* 1821:     */     public static final String ID = "weka.gui.explorer.clustererpanel";
/* 1822:2004 */     protected static final Settings.SettingKey CLUSTERER_KEY = new Settings.SettingKey("weka.gui.explorer.clustererpanel.initialClusterer", "Initial clusterer", "On startup, set this clusterer as the default one");
/* 1823:2007 */     protected static final Clusterer CLUSTERER = new SimpleKMeans();
/* 1824:2009 */     protected static final Settings.SettingKey TEST_MODE_KEY = new Settings.SettingKey("weka.gui.explorer.clustererpanel.initialTestMode", "Default test mode", "");
/* 1825:2011 */     protected static final ClustererPanel.TestMode TEST_MODE = ClustererPanel.TestMode.USE_TRAINING_SET;
/* 1826:2013 */     protected static final Settings.SettingKey STORE_CLUSTERS_FOR_VIS_KEY = new Settings.SettingKey("weka.gui.explorer.clustererpanel.storeClusterersForVis", "Store clusters for visualization", "");
/* 1827:     */     protected static final boolean STORE_CLUSTERS_VIS = true;
/* 1828:2018 */     protected static final Settings.SettingKey OUTPUT_FONT_KEY = new Settings.SettingKey("weka.gui.explorer.clustererpanel.outputFont", "Font for text output", "Font to use in the output area");
/* 1829:2021 */     protected static final Font OUTPUT_FONT = new Font("Monospaced", 0, 12);
/* 1830:2024 */     protected static final Settings.SettingKey OUTPUT_TEXT_COLOR_KEY = new Settings.SettingKey("weka.gui.explorer.clustererpanel.outputFontColor", "Output text color", "Color of output text");
/* 1831:2027 */     protected static final Color OUTPUT_TEXT_COLOR = Color.black;
/* 1832:2029 */     protected static final Settings.SettingKey OUTPUT_BACKGROUND_COLOR_KEY = new Settings.SettingKey("weka.gui.explorer.clustererpanel.outputBackgroundColor", "Output background color", "Output background color");
/* 1833:2032 */     protected static final Color OUTPUT_BACKGROUND_COLOR = Color.white;
/* 1834:     */     private static final long serialVersionUID = 2708388782229179493L;
/* 1835:     */     
/* 1836:     */     public ClustererPanelDefaults()
/* 1837:     */     {
/* 1838:2037 */       super();
/* 1839:     */       
/* 1840:2039 */       this.m_defaults.put(CLUSTERER_KEY, CLUSTERER);
/* 1841:2040 */       this.m_defaults.put(TEST_MODE_KEY, TEST_MODE);
/* 1842:2041 */       this.m_defaults.put(STORE_CLUSTERS_FOR_VIS_KEY, Boolean.valueOf(true));
/* 1843:2042 */       this.m_defaults.put(OUTPUT_FONT_KEY, OUTPUT_FONT);
/* 1844:2043 */       this.m_defaults.put(OUTPUT_TEXT_COLOR_KEY, OUTPUT_TEXT_COLOR);
/* 1845:2044 */       this.m_defaults.put(OUTPUT_BACKGROUND_COLOR_KEY, OUTPUT_BACKGROUND_COLOR);
/* 1846:     */     }
/* 1847:     */   }
/* 1848:     */   
/* 1849:     */   public static void main(String[] args)
/* 1850:     */   {
/* 1851:     */     try
/* 1852:     */     {
/* 1853:2056 */       JFrame jf = new JFrame("Weka Explorer: Cluster");
/* 1854:     */       
/* 1855:2058 */       jf.getContentPane().setLayout(new BorderLayout());
/* 1856:2059 */       ClustererPanel sp = new ClustererPanel();
/* 1857:2060 */       jf.getContentPane().add(sp, "Center");
/* 1858:2061 */       LogPanel lp = new LogPanel();
/* 1859:2062 */       sp.setLog(lp);
/* 1860:2063 */       jf.getContentPane().add(lp, "South");
/* 1861:2064 */       jf.addWindowListener(new WindowAdapter()
/* 1862:     */       {
/* 1863:     */         public void windowClosing(WindowEvent e)
/* 1864:     */         {
/* 1865:2067 */           this.val$jf.dispose();
/* 1866:2068 */           System.exit(0);
/* 1867:     */         }
/* 1868:2070 */       });
/* 1869:2071 */       jf.pack();
/* 1870:2072 */       jf.setSize(800, 600);
/* 1871:2073 */       jf.setVisible(true);
/* 1872:2074 */       if (args.length == 1)
/* 1873:     */       {
/* 1874:2075 */         System.err.println("Loading instances from " + args[0]);
/* 1875:2076 */         Reader r = new BufferedReader(new FileReader(args[0]));
/* 1876:     */         
/* 1877:2078 */         Instances i = new Instances(r);
/* 1878:2079 */         sp.setInstances(i);
/* 1879:     */       }
/* 1880:     */     }
/* 1881:     */     catch (Exception ex)
/* 1882:     */     {
/* 1883:2082 */       ex.printStackTrace();
/* 1884:2083 */       System.err.println(ex.getMessage());
/* 1885:     */     }
/* 1886:     */   }
/* 1887:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.explorer.ClustererPanel
 * JD-Core Version:    0.7.0.1
 */