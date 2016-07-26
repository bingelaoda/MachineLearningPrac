/*    1:     */ package weka.gui;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.awt.Color;
/*    5:     */ import java.awt.Component;
/*    6:     */ import java.awt.Container;
/*    7:     */ import java.awt.Dimension;
/*    8:     */ import java.awt.Frame;
/*    9:     */ import java.awt.GraphicsConfiguration;
/*   10:     */ import java.awt.GridLayout;
/*   11:     */ import java.awt.Image;
/*   12:     */ import java.awt.LayoutManager;
/*   13:     */ import java.awt.Point;
/*   14:     */ import java.awt.Rectangle;
/*   15:     */ import java.awt.Toolkit;
/*   16:     */ import java.awt.event.ActionEvent;
/*   17:     */ import java.awt.event.ActionListener;
/*   18:     */ import java.awt.event.WindowAdapter;
/*   19:     */ import java.awt.event.WindowEvent;
/*   20:     */ import java.io.BufferedReader;
/*   21:     */ import java.io.File;
/*   22:     */ import java.io.FileInputStream;
/*   23:     */ import java.io.FileReader;
/*   24:     */ import java.io.IOException;
/*   25:     */ import java.io.PrintStream;
/*   26:     */ import java.io.Reader;
/*   27:     */ import java.lang.reflect.Method;
/*   28:     */ import java.security.Permission;
/*   29:     */ import java.util.ArrayList;
/*   30:     */ import java.util.Collections;
/*   31:     */ import java.util.Enumeration;
/*   32:     */ import java.util.HashSet;
/*   33:     */ import java.util.Hashtable;
/*   34:     */ import java.util.List;
/*   35:     */ import java.util.Map;
/*   36:     */ import java.util.Properties;
/*   37:     */ import java.util.Set;
/*   38:     */ import java.util.Vector;
/*   39:     */ import javax.swing.BorderFactory;
/*   40:     */ import javax.swing.ImageIcon;
/*   41:     */ import javax.swing.JButton;
/*   42:     */ import javax.swing.JCheckBox;
/*   43:     */ import javax.swing.JComponent;
/*   44:     */ import javax.swing.JFileChooser;
/*   45:     */ import javax.swing.JFrame;
/*   46:     */ import javax.swing.JLabel;
/*   47:     */ import javax.swing.JMenu;
/*   48:     */ import javax.swing.JMenuBar;
/*   49:     */ import javax.swing.JMenuItem;
/*   50:     */ import javax.swing.JOptionPane;
/*   51:     */ import javax.swing.JPanel;
/*   52:     */ import javax.swing.JScrollPane;
/*   53:     */ import javax.swing.JSeparator;
/*   54:     */ import javax.swing.JTable;
/*   55:     */ import javax.swing.KeyStroke;
/*   56:     */ import weka.classifiers.bayes.net.GUI;
/*   57:     */ import weka.classifiers.evaluation.ThresholdCurve;
/*   58:     */ import weka.core.Copyright;
/*   59:     */ import weka.core.Defaults;
/*   60:     */ import weka.core.Instances;
/*   61:     */ import weka.core.Memory;
/*   62:     */ import weka.core.PluginManager;
/*   63:     */ import weka.core.Settings;
/*   64:     */ import weka.core.Settings.SettingKey;
/*   65:     */ import weka.core.SystemInfo;
/*   66:     */ import weka.core.Utils;
/*   67:     */ import weka.core.Version;
/*   68:     */ import weka.core.WekaPackageManager;
/*   69:     */ import weka.core.converters.AbstractFileLoader;
/*   70:     */ import weka.core.converters.ConverterUtils;
/*   71:     */ import weka.core.logging.Logger;
/*   72:     */ import weka.core.logging.Logger.Level;
/*   73:     */ import weka.core.scripting.Groovy;
/*   74:     */ import weka.core.scripting.Jython;
/*   75:     */ import weka.gui.arffviewer.ArffViewer;
/*   76:     */ import weka.gui.boundaryvisualizer.BoundaryVisualizer;
/*   77:     */ import weka.gui.experiment.Experimenter;
/*   78:     */ import weka.gui.explorer.Explorer;
/*   79:     */ import weka.gui.explorer.PreprocessPanel;
/*   80:     */ import weka.gui.graphvisualizer.GraphVisualizer;
/*   81:     */ import weka.gui.knowledgeflow.KnowledgeFlowApp;
/*   82:     */ import weka.gui.knowledgeflow.MainKFPerspective;
/*   83:     */ import weka.gui.scripting.JythonPanel;
/*   84:     */ import weka.gui.sql.SqlViewer;
/*   85:     */ import weka.gui.treevisualizer.Node;
/*   86:     */ import weka.gui.treevisualizer.NodePlace;
/*   87:     */ import weka.gui.treevisualizer.PlaceNode2;
/*   88:     */ import weka.gui.treevisualizer.TreeBuild;
/*   89:     */ import weka.gui.treevisualizer.TreeVisualizer;
/*   90:     */ import weka.gui.visualize.PlotData2D;
/*   91:     */ import weka.gui.visualize.ThresholdVisualizePanel;
/*   92:     */ import weka.gui.visualize.VisualizePanel;
/*   93:     */ 
/*   94:     */ public class GUIChooserApp
/*   95:     */   extends JFrame
/*   96:     */ {
/*   97:     */   private static final long serialVersionUID = 9001529425230247914L;
/*   98:     */   private Settings m_settings;
/*   99:     */   protected GUIChooserApp m_Self;
/*  100:     */   private JMenuBar m_jMenuBar;
/*  101:     */   private JMenu m_jMenuProgram;
/*  102:     */   private JMenu m_jMenuVisualization;
/*  103:     */   private JMenu m_jMenuTools;
/*  104:     */   private JMenu m_jMenuHelp;
/*  105: 135 */   protected JPanel m_PanelApplications = new JPanel();
/*  106: 138 */   protected JButton m_WorkbenchBut = new JButton("Workbench");
/*  107:     */   protected JFrame m_WorkbenchFrame;
/*  108: 144 */   protected JButton m_ExplorerBut = new JButton("Explorer");
/*  109:     */   protected JFrame m_ExplorerFrame;
/*  110: 150 */   protected JButton m_ExperimenterBut = new JButton("Experimenter");
/*  111:     */   protected JFrame m_ExperimenterFrame;
/*  112: 156 */   protected JButton m_KnowledgeFlowBut = new JButton("KnowledgeFlow");
/*  113:     */   protected String m_pendingKnowledgeFlowLoad;
/*  114:     */   protected JFrame m_KnowledgeFlowFrame;
/*  115:     */   protected KnowledgeFlowApp m_knowledgeFlow;
/*  116: 168 */   protected JButton m_SimpleBut = new JButton("Simple CLI");
/*  117:     */   protected SimpleCLI m_SimpleCLI;
/*  118:     */   protected JFrame m_GroovyConsoleFrame;
/*  119:     */   protected JFrame m_JythonConsoleFrame;
/*  120: 180 */   protected Vector<ArffViewer> m_ArffViewers = new Vector();
/*  121: 183 */   protected List<GUIChooser.GUIChooserMenuPlugin> m_menuPlugins = new ArrayList();
/*  122:     */   protected JFrame m_SqlViewerFrame;
/*  123:     */   protected JFrame m_BayesNetGUIFrame;
/*  124:     */   protected JFrame m_EnsembleLibraryFrame;
/*  125:     */   protected JFrame m_PackageManagerFrame;
/*  126: 201 */   protected Vector<JFrame> m_Plots = new Vector();
/*  127: 204 */   protected Vector<JFrame> m_ROCs = new Vector();
/*  128: 207 */   protected Vector<JFrame> m_TreeVisualizers = new Vector();
/*  129: 210 */   protected Vector<JFrame> m_GraphVisualizers = new Vector();
/*  130:     */   protected JFrame m_BoundaryVisualizerFrame;
/*  131:     */   protected JFrame m_SystemInfoFrame;
/*  132:     */   protected JFrame m_MemoryUsageFrame;
/*  133: 226 */   protected static LogWindow m_LogWindow = new LogWindow();
/*  134: 229 */   Image m_weka = Toolkit.getDefaultToolkit().getImage(GUIChooserApp.class.getClassLoader().getResource("weka/gui/images/weka_background.gif"));
/*  135: 233 */   protected JFileChooser m_FileChooserTreeVisualizer = new JFileChooser(new File(System.getProperty("user.dir")));
/*  136: 237 */   protected JFileChooser m_FileChooserGraphVisualizer = new JFileChooser(new File(System.getProperty("user.dir")));
/*  137: 241 */   protected JFileChooser m_FileChooserPlot = new JFileChooser(new File(System.getProperty("user.dir")));
/*  138: 245 */   protected JFileChooser m_FileChooserROC = new JFileChooser(new File(System.getProperty("user.dir")));
/*  139:     */   protected Image m_Icon;
/*  140: 252 */   protected HashSet<Container> m_ChildFrames = new HashSet();
/*  141:     */   private static GUIChooserApp m_chooser;
/*  142:     */   
/*  143:     */   public static synchronized void createSingleton()
/*  144:     */   {
/*  145: 258 */     if (m_chooser == null) {
/*  146: 259 */       m_chooser = new GUIChooserApp();
/*  147:     */     }
/*  148:     */   }
/*  149:     */   
/*  150:     */   public static GUIChooserApp getSingleton()
/*  151:     */   {
/*  152: 269 */     return m_chooser;
/*  153:     */   }
/*  154:     */   
/*  155:     */   public GUIChooserApp()
/*  156:     */   {
/*  157: 277 */     super("Weka GUI Chooser");
/*  158:     */     
/*  159: 279 */     this.m_Self = this;
/*  160:     */     
/*  161: 281 */     this.m_settings = new Settings("weka", "guichooser");
/*  162: 282 */     GUIChooserDefaults guiChooserDefaults = new GUIChooserDefaults();
/*  163: 283 */     Defaults pmDefaults = WekaPackageManager.getUnderlyingPackageManager().getDefaultSettings();
/*  164:     */     
/*  165: 285 */     guiChooserDefaults.add(pmDefaults);
/*  166: 286 */     this.m_settings.applyDefaults(guiChooserDefaults);
/*  167: 287 */     WekaPackageManager.getUnderlyingPackageManager().applySettings(this.m_settings);
/*  168:     */     
/*  169:     */ 
/*  170: 290 */     this.m_FileChooserGraphVisualizer.addChoosableFileFilter(new ExtensionFileFilter(".bif", "BIF Files (*.bif)"));
/*  171:     */     
/*  172: 292 */     this.m_FileChooserGraphVisualizer.addChoosableFileFilter(new ExtensionFileFilter(".xml", "XML Files (*.xml)"));
/*  173:     */     
/*  174:     */ 
/*  175: 295 */     this.m_FileChooserPlot.addChoosableFileFilter(new ExtensionFileFilter(".arff", "ARFF Files (*.arff)"));
/*  176:     */     
/*  177:     */ 
/*  178: 298 */     this.m_FileChooserPlot.setMultiSelectionEnabled(true);
/*  179:     */     
/*  180: 300 */     this.m_FileChooserROC.addChoosableFileFilter(new ExtensionFileFilter(".arff", "ARFF Files (*.arff)"));
/*  181:     */     
/*  182:     */ 
/*  183:     */ 
/*  184:     */ 
/*  185: 305 */     this.m_Icon = Toolkit.getDefaultToolkit().getImage(GUIChooserApp.class.getClassLoader().getResource("weka/gui/weka_icon_new_48.png"));
/*  186:     */     
/*  187: 307 */     setIconImage(this.m_Icon);
/*  188: 308 */     getContentPane().setLayout(new BorderLayout());
/*  189:     */     
/*  190: 310 */     getContentPane().add(this.m_PanelApplications, "East");
/*  191:     */     
/*  192:     */ 
/*  193: 313 */     this.m_PanelApplications.setBorder(BorderFactory.createTitledBorder("Applications"));
/*  194:     */     
/*  195: 315 */     this.m_PanelApplications.setLayout(new GridLayout(0, 1));
/*  196: 316 */     this.m_PanelApplications.add(this.m_ExplorerBut);
/*  197: 317 */     this.m_PanelApplications.add(this.m_ExperimenterBut);
/*  198: 318 */     this.m_PanelApplications.add(this.m_KnowledgeFlowBut);
/*  199: 319 */     this.m_PanelApplications.add(this.m_WorkbenchBut);
/*  200: 320 */     this.m_PanelApplications.add(this.m_SimpleBut);
/*  201:     */     
/*  202:     */ 
/*  203: 323 */     JPanel wekaPan = new JPanel();
/*  204: 324 */     wekaPan.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*  205: 325 */     wekaPan.setLayout(new BorderLayout());
/*  206: 326 */     wekaPan.setToolTipText("Weka, a native bird of New Zealand");
/*  207: 327 */     ImageIcon wii = new ImageIcon(this.m_weka);
/*  208: 328 */     JLabel wekaLab = new JLabel(wii);
/*  209: 329 */     wekaPan.add(wekaLab, "Center");
/*  210: 330 */     String infoString = "<html><font size=-2>Waikato Environment for Knowledge Analysis<br>Version " + Version.VERSION + "<br>" + "(c) " + Copyright.getFromYear() + " - " + Copyright.getToYear() + "<br>" + Copyright.getOwner() + "<br>" + Copyright.getAddress() + "</font>" + "</html>";
/*  211:     */     
/*  212:     */ 
/*  213:     */ 
/*  214:     */ 
/*  215: 335 */     JLabel infoLab = new JLabel(infoString);
/*  216: 336 */     infoLab.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*  217: 337 */     wekaPan.add(infoLab, "South");
/*  218:     */     
/*  219: 339 */     getContentPane().add(wekaPan, "Center");
/*  220:     */     
/*  221:     */ 
/*  222: 342 */     this.m_jMenuBar = new JMenuBar();
/*  223:     */     
/*  224:     */ 
/*  225: 345 */     this.m_jMenuProgram = new JMenu();
/*  226: 346 */     this.m_jMenuBar.add(this.m_jMenuProgram);
/*  227: 347 */     this.m_jMenuProgram.setText("Program");
/*  228: 348 */     this.m_jMenuProgram.setMnemonic('P');
/*  229:     */     
/*  230:     */ 
/*  231: 351 */     JMenuItem jMenuItemProgramLogWindow = new JMenuItem();
/*  232: 352 */     this.m_jMenuProgram.add(jMenuItemProgramLogWindow);
/*  233: 353 */     jMenuItemProgramLogWindow.setText("LogWindow");
/*  234:     */     
/*  235: 355 */     jMenuItemProgramLogWindow.setAccelerator(KeyStroke.getKeyStroke(76, 2));
/*  236:     */     
/*  237: 357 */     m_LogWindow.setIconImage(this.m_Icon);
/*  238: 358 */     jMenuItemProgramLogWindow.addActionListener(new ActionListener()
/*  239:     */     {
/*  240:     */       public void actionPerformed(ActionEvent e)
/*  241:     */       {
/*  242: 361 */         GUIChooserApp.m_LogWindow.setVisible(true);
/*  243:     */       }
/*  244: 364 */     });
/*  245: 365 */     final JMenuItem jMenuItemProgramMemUsage = new JMenuItem();
/*  246: 366 */     this.m_jMenuProgram.add(jMenuItemProgramMemUsage);
/*  247: 367 */     jMenuItemProgramMemUsage.setText("Memory usage");
/*  248:     */     
/*  249: 369 */     jMenuItemProgramMemUsage.setAccelerator(KeyStroke.getKeyStroke(77, 2));
/*  250:     */     
/*  251:     */ 
/*  252: 372 */     jMenuItemProgramMemUsage.addActionListener(new ActionListener()
/*  253:     */     {
/*  254:     */       public void actionPerformed(ActionEvent e)
/*  255:     */       {
/*  256: 375 */         if (GUIChooserApp.this.m_MemoryUsageFrame == null)
/*  257:     */         {
/*  258: 376 */           final MemoryUsagePanel panel = new MemoryUsagePanel();
/*  259: 377 */           jMenuItemProgramMemUsage.setEnabled(false);
/*  260: 378 */           GUIChooserApp.this.m_MemoryUsageFrame = new JFrame("Memory usage");
/*  261: 379 */           GUIChooserApp.this.m_MemoryUsageFrame.setIconImage(GUIChooserApp.this.m_Icon);
/*  262: 380 */           GUIChooserApp.this.m_MemoryUsageFrame.getContentPane().setLayout(new BorderLayout());
/*  263: 381 */           GUIChooserApp.this.m_MemoryUsageFrame.getContentPane().add(panel, "Center");
/*  264: 382 */           GUIChooserApp.this.m_MemoryUsageFrame.addWindowListener(new WindowAdapter()
/*  265:     */           {
/*  266:     */             public void windowClosing(WindowEvent w)
/*  267:     */             {
/*  268: 385 */               panel.stopMonitoring();
/*  269: 386 */               GUIChooserApp.this.m_MemoryUsageFrame.dispose();
/*  270: 387 */               GUIChooserApp.this.m_MemoryUsageFrame = null;
/*  271: 388 */               GUIChooserApp.2.this.val$jMenuItemProgramMemUsage.setEnabled(true);
/*  272: 389 */               GUIChooserApp.this.checkExit();
/*  273:     */             }
/*  274: 391 */           });
/*  275: 392 */           GUIChooserApp.this.m_MemoryUsageFrame.pack();
/*  276: 393 */           GUIChooserApp.this.m_MemoryUsageFrame.setSize(400, 50);
/*  277: 394 */           Point l = panel.getFrameLocation();
/*  278: 395 */           if ((l.x != -1) && (l.y != -1)) {
/*  279: 396 */             GUIChooserApp.this.m_MemoryUsageFrame.setLocation(l);
/*  280:     */           }
/*  281: 398 */           GUIChooserApp.this.m_MemoryUsageFrame.setVisible(true);
/*  282: 399 */           Dimension size = GUIChooserApp.this.m_MemoryUsageFrame.getPreferredSize();
/*  283: 400 */           GUIChooserApp.this.m_MemoryUsageFrame.setSize(new Dimension((int)size.getWidth(), (int)size.getHeight()));
/*  284:     */         }
/*  285:     */       }
/*  286: 405 */     });
/*  287: 406 */     JMenuItem jMenuItemSettings = new JMenuItem();
/*  288: 407 */     this.m_jMenuProgram.add(jMenuItemSettings);
/*  289: 408 */     jMenuItemSettings.setText("Settings");
/*  290: 409 */     jMenuItemSettings.addActionListener(new ActionListener()
/*  291:     */     {
/*  292:     */       public void actionPerformed(ActionEvent e)
/*  293:     */       {
/*  294:     */         try
/*  295:     */         {
/*  296: 413 */           int result = SettingsEditor.showSingleSettingsEditor(GUIChooserApp.this.m_settings, "guichooser", "GUIChooser", (JComponent)GUIChooserApp.this.getContentPane().getComponent(0), 550, 100);
/*  297: 417 */           if (result == 0) {
/*  298: 418 */             WekaPackageManager.getUnderlyingPackageManager().applySettings(GUIChooserApp.this.m_settings);
/*  299:     */           }
/*  300:     */         }
/*  301:     */         catch (Exception ex)
/*  302:     */         {
/*  303: 422 */           ex.printStackTrace();
/*  304:     */         }
/*  305:     */       }
/*  306: 426 */     });
/*  307: 427 */     this.m_jMenuProgram.add(new JSeparator());
/*  308:     */     
/*  309:     */ 
/*  310: 430 */     JMenuItem jMenuItemProgramExit = new JMenuItem();
/*  311: 431 */     this.m_jMenuProgram.add(jMenuItemProgramExit);
/*  312: 432 */     jMenuItemProgramExit.setText("Exit");
/*  313:     */     
/*  314: 434 */     jMenuItemProgramExit.setAccelerator(KeyStroke.getKeyStroke(69, 2));
/*  315:     */     
/*  316: 436 */     jMenuItemProgramExit.addActionListener(new ActionListener()
/*  317:     */     {
/*  318:     */       public void actionPerformed(ActionEvent e)
/*  319:     */       {
/*  320: 439 */         GUIChooserApp.this.dispose();
/*  321: 440 */         GUIChooserApp.this.checkExit();
/*  322:     */       }
/*  323: 444 */     });
/*  324: 445 */     this.m_jMenuVisualization = new JMenu();
/*  325: 446 */     this.m_jMenuBar.add(this.m_jMenuVisualization);
/*  326: 447 */     this.m_jMenuVisualization.setText("Visualization");
/*  327: 448 */     this.m_jMenuVisualization.setMnemonic('V');
/*  328:     */     
/*  329:     */ 
/*  330: 451 */     JMenuItem jMenuItemVisualizationPlot = new JMenuItem();
/*  331: 452 */     this.m_jMenuVisualization.add(jMenuItemVisualizationPlot);
/*  332: 453 */     jMenuItemVisualizationPlot.setText("Plot");
/*  333:     */     
/*  334: 455 */     jMenuItemVisualizationPlot.setAccelerator(KeyStroke.getKeyStroke(80, 2));
/*  335:     */     
/*  336:     */ 
/*  337: 458 */     jMenuItemVisualizationPlot.addActionListener(new ActionListener()
/*  338:     */     {
/*  339:     */       public void actionPerformed(ActionEvent e)
/*  340:     */       {
/*  341: 462 */         int retVal = GUIChooserApp.this.m_FileChooserPlot.showOpenDialog(GUIChooserApp.this.m_Self);
/*  342: 463 */         if (retVal != 0) {
/*  343: 464 */           return;
/*  344:     */         }
/*  345: 468 */         VisualizePanel panel = new VisualizePanel();
/*  346: 469 */         String filenames = "";
/*  347: 470 */         File[] files = GUIChooserApp.this.m_FileChooserPlot.getSelectedFiles();
/*  348: 471 */         for (int j = 0; j < files.length; j++)
/*  349:     */         {
/*  350: 472 */           String filename = files[j].getAbsolutePath();
/*  351: 473 */           if (j > 0) {
/*  352: 474 */             filenames = filenames + ", ";
/*  353:     */           }
/*  354: 476 */           filenames = filenames + filename;
/*  355: 477 */           System.err.println("Loading instances from " + filename);
/*  356:     */           try
/*  357:     */           {
/*  358: 479 */             Reader r = new BufferedReader(new FileReader(filename));
/*  359: 480 */             Instances i = new Instances(r);
/*  360: 481 */             i.setClassIndex(i.numAttributes() - 1);
/*  361: 482 */             PlotData2D pd1 = new PlotData2D(i);
/*  362: 484 */             if (j == 0)
/*  363:     */             {
/*  364: 485 */               pd1.setPlotName("Master plot");
/*  365: 486 */               panel.setMasterPlot(pd1);
/*  366:     */             }
/*  367:     */             else
/*  368:     */             {
/*  369: 488 */               pd1.setPlotName("Plot " + (j + 1));
/*  370: 489 */               pd1.m_useCustomColour = true;
/*  371: 490 */               pd1.m_customColour = (j % 2 == 0 ? Color.red : Color.blue);
/*  372: 491 */               panel.addPlot(pd1);
/*  373:     */             }
/*  374:     */           }
/*  375:     */           catch (Exception ex)
/*  376:     */           {
/*  377: 494 */             ex.printStackTrace();
/*  378: 495 */             JOptionPane.showMessageDialog(GUIChooserApp.this.m_Self, "Error loading file '" + files[j] + "':\n" + ex.getMessage());
/*  379:     */             
/*  380: 497 */             return;
/*  381:     */           }
/*  382:     */         }
/*  383: 502 */         final JFrame frame = new JFrame("Plot - " + filenames);
/*  384: 503 */         frame.setIconImage(GUIChooserApp.this.m_Icon);
/*  385: 504 */         frame.getContentPane().setLayout(new BorderLayout());
/*  386: 505 */         frame.getContentPane().add(panel, "Center");
/*  387: 506 */         frame.addWindowListener(new WindowAdapter()
/*  388:     */         {
/*  389:     */           public void windowClosing(WindowEvent e)
/*  390:     */           {
/*  391: 509 */             GUIChooserApp.this.m_Plots.remove(frame);
/*  392: 510 */             frame.dispose();
/*  393: 511 */             GUIChooserApp.this.checkExit();
/*  394:     */           }
/*  395: 513 */         });
/*  396: 514 */         frame.pack();
/*  397: 515 */         frame.setSize(800, 600);
/*  398: 516 */         frame.setVisible(true);
/*  399: 517 */         GUIChooserApp.this.m_Plots.add(frame);
/*  400:     */       }
/*  401: 521 */     });
/*  402: 522 */     JMenuItem jMenuItemVisualizationROC = new JMenuItem();
/*  403: 523 */     this.m_jMenuVisualization.add(jMenuItemVisualizationROC);
/*  404: 524 */     jMenuItemVisualizationROC.setText("ROC");
/*  405:     */     
/*  406: 526 */     jMenuItemVisualizationROC.setAccelerator(KeyStroke.getKeyStroke(82, 2));
/*  407:     */     
/*  408:     */ 
/*  409: 529 */     jMenuItemVisualizationROC.addActionListener(new ActionListener()
/*  410:     */     {
/*  411:     */       public void actionPerformed(ActionEvent e)
/*  412:     */       {
/*  413: 533 */         int retVal = GUIChooserApp.this.m_FileChooserROC.showOpenDialog(GUIChooserApp.this.m_Self);
/*  414: 534 */         if (retVal != 0) {
/*  415: 535 */           return;
/*  416:     */         }
/*  417: 539 */         String filename = GUIChooserApp.this.m_FileChooserROC.getSelectedFile().getAbsolutePath();
/*  418: 540 */         Instances result = null;
/*  419:     */         try
/*  420:     */         {
/*  421: 542 */           result = new Instances(new BufferedReader(new FileReader(filename)));
/*  422:     */         }
/*  423:     */         catch (Exception ex)
/*  424:     */         {
/*  425: 544 */           ex.printStackTrace();
/*  426: 545 */           JOptionPane.showMessageDialog(GUIChooserApp.this.m_Self, "Error loading file '" + filename + "':\n" + ex.getMessage());
/*  427:     */           
/*  428: 547 */           return;
/*  429:     */         }
/*  430: 549 */         result.setClassIndex(result.numAttributes() - 1);
/*  431: 550 */         ThresholdVisualizePanel vmc = new ThresholdVisualizePanel();
/*  432: 551 */         vmc.setROCString("(Area under ROC = " + Utils.doubleToString(ThresholdCurve.getROCArea(result), 4) + ")");
/*  433:     */         
/*  434: 553 */         vmc.setName(result.relationName());
/*  435: 554 */         PlotData2D tempd = new PlotData2D(result);
/*  436: 555 */         tempd.setPlotName(result.relationName());
/*  437: 556 */         tempd.addInstanceNumberAttribute();
/*  438:     */         try
/*  439:     */         {
/*  440: 558 */           vmc.addPlot(tempd);
/*  441:     */         }
/*  442:     */         catch (Exception ex)
/*  443:     */         {
/*  444: 560 */           ex.printStackTrace();
/*  445: 561 */           JOptionPane.showMessageDialog(GUIChooserApp.this.m_Self, "Error adding plot:\n" + ex.getMessage());
/*  446:     */           
/*  447: 563 */           return;
/*  448:     */         }
/*  449: 566 */         final JFrame frame = new JFrame("ROC - " + filename);
/*  450: 567 */         frame.setIconImage(GUIChooserApp.this.m_Icon);
/*  451: 568 */         frame.getContentPane().setLayout(new BorderLayout());
/*  452: 569 */         frame.getContentPane().add(vmc, "Center");
/*  453: 570 */         frame.addWindowListener(new WindowAdapter()
/*  454:     */         {
/*  455:     */           public void windowClosing(WindowEvent e)
/*  456:     */           {
/*  457: 573 */             GUIChooserApp.this.m_ROCs.remove(frame);
/*  458: 574 */             frame.dispose();
/*  459: 575 */             GUIChooserApp.this.checkExit();
/*  460:     */           }
/*  461: 577 */         });
/*  462: 578 */         frame.pack();
/*  463: 579 */         frame.setSize(800, 600);
/*  464: 580 */         frame.setVisible(true);
/*  465: 581 */         GUIChooserApp.this.m_ROCs.add(frame);
/*  466:     */       }
/*  467: 585 */     });
/*  468: 586 */     JMenuItem jMenuItemVisualizationTree = new JMenuItem();
/*  469: 587 */     this.m_jMenuVisualization.add(jMenuItemVisualizationTree);
/*  470: 588 */     jMenuItemVisualizationTree.setText("TreeVisualizer");
/*  471:     */     
/*  472: 590 */     jMenuItemVisualizationTree.setAccelerator(KeyStroke.getKeyStroke(84, 2));
/*  473:     */     
/*  474:     */ 
/*  475: 593 */     jMenuItemVisualizationTree.addActionListener(new ActionListener()
/*  476:     */     {
/*  477:     */       public void actionPerformed(ActionEvent e)
/*  478:     */       {
/*  479: 597 */         int retVal = GUIChooserApp.this.m_FileChooserTreeVisualizer.showOpenDialog(GUIChooserApp.this.m_Self);
/*  480: 598 */         if (retVal != 0) {
/*  481: 599 */           return;
/*  482:     */         }
/*  483: 603 */         String filename = GUIChooserApp.this.m_FileChooserTreeVisualizer.getSelectedFile().getAbsolutePath();
/*  484:     */         
/*  485: 605 */         TreeBuild builder = new TreeBuild();
/*  486: 606 */         Node top = null;
/*  487: 607 */         NodePlace arrange = new PlaceNode2();
/*  488:     */         try
/*  489:     */         {
/*  490: 609 */           top = builder.create(new FileReader(filename));
/*  491:     */         }
/*  492:     */         catch (Exception ex)
/*  493:     */         {
/*  494: 611 */           ex.printStackTrace();
/*  495: 612 */           JOptionPane.showMessageDialog(GUIChooserApp.this.m_Self, "Error loading file '" + filename + "':\n" + ex.getMessage());
/*  496:     */           
/*  497: 614 */           return;
/*  498:     */         }
/*  499: 618 */         final JFrame frame = new JFrame("TreeVisualizer - " + filename);
/*  500: 619 */         frame.setIconImage(GUIChooserApp.this.m_Icon);
/*  501: 620 */         frame.getContentPane().setLayout(new BorderLayout());
/*  502: 621 */         frame.getContentPane().add(new TreeVisualizer(null, top, arrange), "Center");
/*  503:     */         
/*  504: 623 */         frame.addWindowListener(new WindowAdapter()
/*  505:     */         {
/*  506:     */           public void windowClosing(WindowEvent e)
/*  507:     */           {
/*  508: 626 */             GUIChooserApp.this.m_TreeVisualizers.remove(frame);
/*  509: 627 */             frame.dispose();
/*  510: 628 */             GUIChooserApp.this.checkExit();
/*  511:     */           }
/*  512: 630 */         });
/*  513: 631 */         frame.pack();
/*  514: 632 */         frame.setSize(800, 600);
/*  515: 633 */         frame.setVisible(true);
/*  516: 634 */         GUIChooserApp.this.m_TreeVisualizers.add(frame);
/*  517:     */       }
/*  518: 638 */     });
/*  519: 639 */     JMenuItem jMenuItemVisualizationGraph = new JMenuItem();
/*  520: 640 */     this.m_jMenuVisualization.add(jMenuItemVisualizationGraph);
/*  521: 641 */     jMenuItemVisualizationGraph.setText("GraphVisualizer");
/*  522:     */     
/*  523: 643 */     jMenuItemVisualizationGraph.setAccelerator(KeyStroke.getKeyStroke(71, 2));
/*  524:     */     
/*  525:     */ 
/*  526: 646 */     jMenuItemVisualizationGraph.addActionListener(new ActionListener()
/*  527:     */     {
/*  528:     */       public void actionPerformed(ActionEvent e)
/*  529:     */       {
/*  530: 650 */         int retVal = GUIChooserApp.this.m_FileChooserGraphVisualizer.showOpenDialog(GUIChooserApp.this.m_Self);
/*  531: 651 */         if (retVal != 0) {
/*  532: 652 */           return;
/*  533:     */         }
/*  534: 656 */         String filename = GUIChooserApp.this.m_FileChooserGraphVisualizer.getSelectedFile().getAbsolutePath();
/*  535:     */         
/*  536: 658 */         GraphVisualizer panel = new GraphVisualizer();
/*  537:     */         try
/*  538:     */         {
/*  539: 660 */           if ((filename.toLowerCase().endsWith(".xml")) || (filename.toLowerCase().endsWith(".bif"))) {
/*  540: 662 */             panel.readBIF(new FileInputStream(filename));
/*  541:     */           } else {
/*  542: 664 */             panel.readDOT(new FileReader(filename));
/*  543:     */           }
/*  544:     */         }
/*  545:     */         catch (Exception ex)
/*  546:     */         {
/*  547: 667 */           ex.printStackTrace();
/*  548: 668 */           JOptionPane.showMessageDialog(GUIChooserApp.this.m_Self, "Error loading file '" + filename + "':\n" + ex.getMessage());
/*  549:     */           
/*  550: 670 */           return;
/*  551:     */         }
/*  552: 674 */         final JFrame frame = new JFrame("GraphVisualizer - " + filename);
/*  553: 675 */         frame.setIconImage(GUIChooserApp.this.m_Icon);
/*  554: 676 */         frame.getContentPane().setLayout(new BorderLayout());
/*  555: 677 */         frame.getContentPane().add(panel, "Center");
/*  556: 678 */         frame.addWindowListener(new WindowAdapter()
/*  557:     */         {
/*  558:     */           public void windowClosing(WindowEvent e)
/*  559:     */           {
/*  560: 681 */             GUIChooserApp.this.m_GraphVisualizers.remove(frame);
/*  561: 682 */             frame.dispose();
/*  562: 683 */             GUIChooserApp.this.checkExit();
/*  563:     */           }
/*  564: 685 */         });
/*  565: 686 */         frame.pack();
/*  566: 687 */         frame.setSize(800, 600);
/*  567: 688 */         frame.setVisible(true);
/*  568: 689 */         GUIChooserApp.this.m_GraphVisualizers.add(frame);
/*  569:     */       }
/*  570: 693 */     });
/*  571: 694 */     final JMenuItem jMenuItemVisualizationBoundary = new JMenuItem();
/*  572: 695 */     this.m_jMenuVisualization.add(jMenuItemVisualizationBoundary);
/*  573: 696 */     jMenuItemVisualizationBoundary.setText("BoundaryVisualizer");
/*  574:     */     
/*  575: 698 */     jMenuItemVisualizationBoundary.setAccelerator(KeyStroke.getKeyStroke(66, 2));
/*  576:     */     
/*  577:     */ 
/*  578: 701 */     jMenuItemVisualizationBoundary.addActionListener(new ActionListener()
/*  579:     */     {
/*  580:     */       public void actionPerformed(ActionEvent e)
/*  581:     */       {
/*  582: 704 */         if (GUIChooserApp.this.m_BoundaryVisualizerFrame == null)
/*  583:     */         {
/*  584: 705 */           jMenuItemVisualizationBoundary.setEnabled(false);
/*  585: 706 */           GUIChooserApp.this.m_BoundaryVisualizerFrame = new JFrame("BoundaryVisualizer");
/*  586: 707 */           GUIChooserApp.this.m_BoundaryVisualizerFrame.setIconImage(GUIChooserApp.this.m_Icon);
/*  587: 708 */           GUIChooserApp.this.m_BoundaryVisualizerFrame.getContentPane().setLayout(new BorderLayout());
/*  588:     */           
/*  589: 710 */           final BoundaryVisualizer bv = new BoundaryVisualizer();
/*  590: 711 */           GUIChooserApp.this.m_BoundaryVisualizerFrame.getContentPane().add(bv, "Center");
/*  591:     */           
/*  592: 713 */           GUIChooserApp.this.m_BoundaryVisualizerFrame.setSize(bv.getMinimumSize());
/*  593: 714 */           GUIChooserApp.this.m_BoundaryVisualizerFrame.addWindowListener(new WindowAdapter()
/*  594:     */           {
/*  595:     */             public void windowClosing(WindowEvent w)
/*  596:     */             {
/*  597: 717 */               bv.stopPlotting();
/*  598: 718 */               GUIChooserApp.this.m_BoundaryVisualizerFrame.dispose();
/*  599: 719 */               GUIChooserApp.this.m_BoundaryVisualizerFrame = null;
/*  600: 720 */               GUIChooserApp.9.this.val$jMenuItemVisualizationBoundary.setEnabled(true);
/*  601: 721 */               GUIChooserApp.this.checkExit();
/*  602:     */             }
/*  603: 723 */           });
/*  604: 724 */           GUIChooserApp.this.m_BoundaryVisualizerFrame.pack();
/*  605:     */           
/*  606: 726 */           GUIChooserApp.this.m_BoundaryVisualizerFrame.setResizable(false);
/*  607: 727 */           GUIChooserApp.this.m_BoundaryVisualizerFrame.setVisible(true);
/*  608:     */           
/*  609: 729 */           BoundaryVisualizer.setExitIfNoWindowsOpen(false);
/*  610:     */         }
/*  611:     */       }
/*  612: 734 */     });
/*  613: 735 */     JMenu jMenuExtensions = new JMenu("Extensions");
/*  614: 736 */     jMenuExtensions.setMnemonic(69);
/*  615: 737 */     this.m_jMenuBar.add(jMenuExtensions);
/*  616: 738 */     jMenuExtensions.setVisible(false);
/*  617:     */     
/*  618: 740 */     String extensions = GenericObjectEditor.EDITOR_PROPERTIES.getProperty(MainMenuExtension.class.getName(), "");
/*  619: 743 */     if (extensions.length() > 0)
/*  620:     */     {
/*  621: 744 */       jMenuExtensions.setVisible(true);
/*  622: 745 */       String[] classnames = GenericObjectEditor.EDITOR_PROPERTIES.getProperty(MainMenuExtension.class.getName(), "").split(",");
/*  623:     */       
/*  624: 747 */       Hashtable<String, JMenu> submenus = new Hashtable();
/*  625: 750 */       for (String classname : classnames) {
/*  626:     */         try
/*  627:     */         {
/*  628: 752 */           MainMenuExtension ext = (MainMenuExtension)Class.forName(classname).newInstance();
/*  629:     */           
/*  630:     */ 
/*  631:     */ 
/*  632: 756 */           JMenu submenu = null;
/*  633: 757 */           if (ext.getSubmenuTitle() != null)
/*  634:     */           {
/*  635: 758 */             submenu = (JMenu)submenus.get(ext.getSubmenuTitle());
/*  636: 759 */             if (submenu == null)
/*  637:     */             {
/*  638: 760 */               submenu = new JMenu(ext.getSubmenuTitle());
/*  639: 761 */               submenus.put(ext.getSubmenuTitle(), submenu);
/*  640: 762 */               insertMenuItem(jMenuExtensions, submenu);
/*  641:     */             }
/*  642:     */           }
/*  643: 767 */           JMenuItem menuitem = new JMenuItem();
/*  644: 768 */           menuitem.setText(ext.getMenuTitle());
/*  645:     */           
/*  646:     */ 
/*  647: 771 */           ActionListener listener = ext.getActionListener(this.m_Self);
/*  648: 772 */           if (listener != null)
/*  649:     */           {
/*  650: 773 */             menuitem.addActionListener(listener);
/*  651:     */           }
/*  652:     */           else
/*  653:     */           {
/*  654: 775 */             final JMenuItem finalMenuitem = menuitem;
/*  655: 776 */             final MainMenuExtension finalExt = ext;
/*  656: 777 */             menuitem.addActionListener(new ActionListener()
/*  657:     */             {
/*  658:     */               public void actionPerformed(ActionEvent e)
/*  659:     */               {
/*  660: 780 */                 Component frame = GUIChooserApp.this.createFrame(GUIChooserApp.this.m_Self, finalMenuitem.getText(), null, null, null, -1, -1, null, false, false);
/*  661:     */                 
/*  662: 782 */                 finalExt.fillFrame(frame);
/*  663: 783 */                 frame.setVisible(true);
/*  664:     */               }
/*  665:     */             });
/*  666:     */           }
/*  667: 789 */           if (submenu != null) {
/*  668: 790 */             insertMenuItem(submenu, menuitem);
/*  669:     */           } else {
/*  670: 792 */             insertMenuItem(jMenuExtensions, menuitem);
/*  671:     */           }
/*  672:     */         }
/*  673:     */         catch (Exception e)
/*  674:     */         {
/*  675: 795 */           e.printStackTrace();
/*  676:     */         }
/*  677:     */       }
/*  678:     */     }
/*  679: 801 */     this.m_jMenuTools = new JMenu();
/*  680: 802 */     this.m_jMenuBar.add(this.m_jMenuTools);
/*  681: 803 */     this.m_jMenuTools.setText("Tools");
/*  682: 804 */     this.m_jMenuTools.setMnemonic('T');
/*  683:     */     
/*  684:     */ 
/*  685: 807 */     final JMenuItem jMenuItemToolsPackageManager = new JMenuItem();
/*  686: 808 */     this.m_jMenuTools.add(jMenuItemToolsPackageManager);
/*  687: 809 */     final String offline = WekaPackageManager.m_offline ? " (offline)" : "";
/*  688: 810 */     jMenuItemToolsPackageManager.setText("Package manager" + offline);
/*  689: 811 */     jMenuItemToolsPackageManager.setAccelerator(KeyStroke.getKeyStroke(85, 2));
/*  690:     */     
/*  691: 813 */     jMenuItemToolsPackageManager.addActionListener(new ActionListener()
/*  692:     */     {
/*  693:     */       public void actionPerformed(ActionEvent e)
/*  694:     */       {
/*  695: 816 */         if (GUIChooserApp.this.m_PackageManagerFrame == null)
/*  696:     */         {
/*  697: 817 */           jMenuItemToolsPackageManager.setEnabled(false);
/*  698: 818 */           Thread temp = new Thread()
/*  699:     */           {
/*  700:     */             public void run()
/*  701:     */             {
/*  702: 822 */               PackageManager pm = new PackageManager();
/*  703: 823 */               if (!WekaPackageManager.m_noPackageMetaDataAvailable)
/*  704:     */               {
/*  705: 824 */                 GUIChooserApp.this.m_PackageManagerFrame = new JFrame("Package Manager" + GUIChooserApp.11.this.val$offline);
/*  706: 825 */                 GUIChooserApp.this.m_PackageManagerFrame.setIconImage(GUIChooserApp.this.m_Icon);
/*  707: 826 */                 GUIChooserApp.this.m_PackageManagerFrame.getContentPane().setLayout(new BorderLayout());
/*  708:     */                 
/*  709: 828 */                 GUIChooserApp.this.m_PackageManagerFrame.getContentPane().add(pm, "Center");
/*  710:     */                 
/*  711: 830 */                 GUIChooserApp.this.m_PackageManagerFrame.addWindowListener(new WindowAdapter()
/*  712:     */                 {
/*  713:     */                   public void windowClosing(WindowEvent w)
/*  714:     */                   {
/*  715: 833 */                     GUIChooserApp.this.m_PackageManagerFrame.dispose();
/*  716: 834 */                     GUIChooserApp.this.m_PackageManagerFrame = null;
/*  717: 835 */                     GUIChooserApp.11.this.val$jMenuItemToolsPackageManager.setEnabled(true);
/*  718: 836 */                     GUIChooserApp.this.checkExit();
/*  719:     */                   }
/*  720: 838 */                 });
/*  721: 839 */                 Dimension screenSize = GUIChooserApp.this.m_PackageManagerFrame.getToolkit().getScreenSize();
/*  722:     */                 
/*  723: 841 */                 int width = screenSize.width * 8 / 10;
/*  724: 842 */                 int height = screenSize.height * 8 / 10;
/*  725: 843 */                 GUIChooserApp.this.m_PackageManagerFrame.setBounds(width / 8, height / 8, width, height);
/*  726:     */                 
/*  727: 845 */                 GUIChooserApp.this.m_PackageManagerFrame.setVisible(true);
/*  728: 846 */                 pm.setInitialSplitPaneDividerLocation();
/*  729:     */               }
/*  730:     */             }
/*  731: 849 */           };
/*  732: 850 */           temp.start();
/*  733:     */         }
/*  734:     */       }
/*  735: 855 */     });
/*  736: 856 */     JMenuItem jMenuItemToolsArffViewer = new JMenuItem();
/*  737: 857 */     this.m_jMenuTools.add(jMenuItemToolsArffViewer);
/*  738: 858 */     jMenuItemToolsArffViewer.setText("ArffViewer");
/*  739:     */     
/*  740: 860 */     jMenuItemToolsArffViewer.setAccelerator(KeyStroke.getKeyStroke(65, 2));
/*  741:     */     
/*  742:     */ 
/*  743: 863 */     jMenuItemToolsArffViewer.addActionListener(new ActionListener()
/*  744:     */     {
/*  745:     */       public void actionPerformed(ActionEvent e)
/*  746:     */       {
/*  747: 866 */         final ArffViewer av = new ArffViewer();
/*  748: 867 */         av.addWindowListener(new WindowAdapter()
/*  749:     */         {
/*  750:     */           public void windowClosing(WindowEvent w)
/*  751:     */           {
/*  752: 870 */             GUIChooserApp.this.m_ArffViewers.remove(av);
/*  753: 871 */             GUIChooserApp.this.checkExit();
/*  754:     */           }
/*  755: 873 */         });
/*  756: 874 */         av.setVisible(true);
/*  757: 875 */         GUIChooserApp.this.m_ArffViewers.add(av);
/*  758:     */       }
/*  759: 879 */     });
/*  760: 880 */     final JMenuItem jMenuItemToolsSql = new JMenuItem();
/*  761: 881 */     this.m_jMenuTools.add(jMenuItemToolsSql);
/*  762: 882 */     jMenuItemToolsSql.setText("SqlViewer");
/*  763:     */     
/*  764: 884 */     jMenuItemToolsSql.setAccelerator(KeyStroke.getKeyStroke(83, 2));
/*  765:     */     
/*  766:     */ 
/*  767: 887 */     jMenuItemToolsSql.addActionListener(new ActionListener()
/*  768:     */     {
/*  769:     */       public void actionPerformed(ActionEvent e)
/*  770:     */       {
/*  771: 890 */         if (GUIChooserApp.this.m_SqlViewerFrame == null)
/*  772:     */         {
/*  773: 891 */           jMenuItemToolsSql.setEnabled(false);
/*  774: 892 */           final SqlViewer sql = new SqlViewer(null);
/*  775: 893 */           GUIChooserApp.this.m_SqlViewerFrame = new JFrame("SqlViewer");
/*  776: 894 */           GUIChooserApp.this.m_SqlViewerFrame.setIconImage(GUIChooserApp.this.m_Icon);
/*  777: 895 */           GUIChooserApp.this.m_SqlViewerFrame.getContentPane().setLayout(new BorderLayout());
/*  778: 896 */           GUIChooserApp.this.m_SqlViewerFrame.getContentPane().add(sql, "Center");
/*  779: 897 */           GUIChooserApp.this.m_SqlViewerFrame.addWindowListener(new WindowAdapter()
/*  780:     */           {
/*  781:     */             public void windowClosing(WindowEvent w)
/*  782:     */             {
/*  783: 900 */               sql.saveSize();
/*  784: 901 */               GUIChooserApp.this.m_SqlViewerFrame.dispose();
/*  785: 902 */               GUIChooserApp.this.m_SqlViewerFrame = null;
/*  786: 903 */               GUIChooserApp.13.this.val$jMenuItemToolsSql.setEnabled(true);
/*  787: 904 */               GUIChooserApp.this.checkExit();
/*  788:     */             }
/*  789: 906 */           });
/*  790: 907 */           GUIChooserApp.this.m_SqlViewerFrame.pack();
/*  791: 908 */           GUIChooserApp.this.m_SqlViewerFrame.setVisible(true);
/*  792:     */         }
/*  793:     */       }
/*  794: 913 */     });
/*  795: 914 */     final JMenuItem jMenuItemBayesNet = new JMenuItem();
/*  796: 915 */     this.m_jMenuTools.add(jMenuItemBayesNet);
/*  797: 916 */     jMenuItemBayesNet.setText("Bayes net editor");
/*  798: 917 */     jMenuItemBayesNet.setAccelerator(KeyStroke.getKeyStroke(78, 2));
/*  799:     */     
/*  800: 919 */     jMenuItemBayesNet.addActionListener(new ActionListener()
/*  801:     */     {
/*  802:     */       public void actionPerformed(ActionEvent e)
/*  803:     */       {
/*  804: 922 */         if (GUIChooserApp.this.m_BayesNetGUIFrame == null)
/*  805:     */         {
/*  806: 923 */           jMenuItemBayesNet.setEnabled(false);
/*  807: 924 */           GUI bayesNetGUI = new GUI();
/*  808: 925 */           JMenuBar bayesBar = bayesNetGUI.getMenuBar();
/*  809: 926 */           GUIChooserApp.this.m_BayesNetGUIFrame = new JFrame("Bayes Network Editor");
/*  810: 927 */           GUIChooserApp.this.m_BayesNetGUIFrame.setIconImage(GUIChooserApp.this.m_Icon);
/*  811: 928 */           GUIChooserApp.this.m_BayesNetGUIFrame.setJMenuBar(bayesBar);
/*  812: 929 */           GUIChooserApp.this.m_BayesNetGUIFrame.getContentPane().add(bayesNetGUI, "Center");
/*  813:     */           
/*  814: 931 */           GUIChooserApp.this.m_BayesNetGUIFrame.addWindowListener(new WindowAdapter()
/*  815:     */           {
/*  816:     */             public void windowClosing(WindowEvent w)
/*  817:     */             {
/*  818: 934 */               GUIChooserApp.this.m_BayesNetGUIFrame.dispose();
/*  819: 935 */               GUIChooserApp.this.m_BayesNetGUIFrame = null;
/*  820: 936 */               GUIChooserApp.14.this.val$jMenuItemBayesNet.setEnabled(true);
/*  821: 937 */               GUIChooserApp.this.checkExit();
/*  822:     */             }
/*  823: 939 */           });
/*  824: 940 */           GUIChooserApp.this.m_BayesNetGUIFrame.setSize(800, 600);
/*  825: 941 */           GUIChooserApp.this.m_BayesNetGUIFrame.setVisible(true);
/*  826:     */         }
/*  827:     */       }
/*  828:     */     });
/*  829: 947 */     if (Groovy.isPresent())
/*  830:     */     {
/*  831: 948 */       JMenuItem jMenuItemGroovyConsole = new JMenuItem();
/*  832: 949 */       this.m_jMenuTools.add(jMenuItemGroovyConsole);
/*  833: 950 */       jMenuItemGroovyConsole.setText("Groovy console");
/*  834: 951 */       jMenuItemGroovyConsole.setAccelerator(KeyStroke.getKeyStroke(71, 2));
/*  835:     */       
/*  836: 953 */       jMenuItemGroovyConsole.addActionListener(new ActionListener()
/*  837:     */       {
/*  838:     */         public void actionPerformed(ActionEvent e)
/*  839:     */         {
/*  840:     */           try
/*  841:     */           {
/*  842: 957 */             Class groovyConsoleClass = Class.forName("groovy.ui.Console");
/*  843: 959 */             if (System.getProperty("os.name").toLowerCase().startsWith("mac"))
/*  844:     */             {
/*  845: 966 */               String realOS = System.getProperty("os.name");
/*  846: 967 */               System.setProperty("os.name", "pretending_not_to_be_an_apple");
/*  847: 968 */               groovyConsoleClass.getMethod("run", new Class[0]).invoke(groovyConsoleClass.newInstance(), new Object[0]);
/*  848:     */               
/*  849: 970 */               System.setProperty("os.name", realOS);
/*  850:     */             }
/*  851:     */             else
/*  852:     */             {
/*  853: 972 */               groovyConsoleClass.getMethod("run", new Class[0]).invoke(groovyConsoleClass.newInstance(), new Object[0]);
/*  854:     */             }
/*  855:     */           }
/*  856:     */           catch (Exception ex)
/*  857:     */           {
/*  858: 976 */             System.err.println("Failed to start Groovy console.");
/*  859:     */           }
/*  860:     */         }
/*  861:     */       });
/*  862:     */     }
/*  863: 983 */     if (Jython.isPresent())
/*  864:     */     {
/*  865: 984 */       final JMenuItem jMenuItemJythonConsole = new JMenuItem();
/*  866: 985 */       this.m_jMenuTools.add(jMenuItemJythonConsole);
/*  867: 986 */       jMenuItemJythonConsole.setText("Jython console");
/*  868: 987 */       jMenuItemJythonConsole.setAccelerator(KeyStroke.getKeyStroke(74, 2));
/*  869:     */       
/*  870: 989 */       jMenuItemJythonConsole.addActionListener(new ActionListener()
/*  871:     */       {
/*  872:     */         public void actionPerformed(ActionEvent e)
/*  873:     */         {
/*  874:     */           try
/*  875:     */           {
/*  876: 995 */             Class tigerJythonClass = Class.forName("tigerjython.core.TigerJython");
/*  877:     */             
/*  878: 997 */             Object[] args = new Object[1];
/*  879: 998 */             args[0] = new String[0];
/*  880:1000 */             if (System.getProperty("os.name").toLowerCase().startsWith("mac"))
/*  881:     */             {
/*  882:1007 */               String realOS = System.getProperty("os.name");
/*  883:1008 */               System.setProperty("os.name", "pretending_not_to_be_an_apple");
/*  884:1009 */               tigerJythonClass.getMethod("main", new Class[] { [Ljava.lang.String.class }).invoke(null, args);
/*  885:     */               
/*  886:1011 */               System.setProperty("os.name", realOS);
/*  887:     */             }
/*  888:     */             else
/*  889:     */             {
/*  890:1013 */               tigerJythonClass.getMethod("main", new Class[] { [Ljava.lang.String.class }).invoke(null, args);
/*  891:     */             }
/*  892:     */           }
/*  893:     */           catch (Exception ex)
/*  894:     */           {
/*  895:1020 */             if (GUIChooserApp.this.m_JythonConsoleFrame == null)
/*  896:     */             {
/*  897:1021 */               jMenuItemJythonConsole.setEnabled(false);
/*  898:1022 */               JythonPanel jythonPanel = new JythonPanel();
/*  899:1023 */               GUIChooserApp.this.m_JythonConsoleFrame = new JFrame(jythonPanel.getPlainTitle());
/*  900:1024 */               GUIChooserApp.this.m_JythonConsoleFrame.setIconImage(GUIChooserApp.this.m_Icon);
/*  901:1025 */               GUIChooserApp.this.m_JythonConsoleFrame.setDefaultCloseOperation(2);
/*  902:1026 */               GUIChooserApp.this.m_JythonConsoleFrame.setJMenuBar(jythonPanel.getMenuBar());
/*  903:1027 */               GUIChooserApp.this.m_JythonConsoleFrame.getContentPane().add(jythonPanel, "Center");
/*  904:     */               
/*  905:1029 */               GUIChooserApp.this.m_JythonConsoleFrame.addWindowListener(new WindowAdapter()
/*  906:     */               {
/*  907:     */                 public void windowClosed(WindowEvent w)
/*  908:     */                 {
/*  909:1032 */                   GUIChooserApp.this.m_JythonConsoleFrame = null;
/*  910:1033 */                   GUIChooserApp.16.this.val$jMenuItemJythonConsole.setEnabled(true);
/*  911:1034 */                   GUIChooserApp.this.checkExit();
/*  912:     */                 }
/*  913:1036 */               });
/*  914:1037 */               GUIChooserApp.this.m_JythonConsoleFrame.setSize(800, 600);
/*  915:1038 */               GUIChooserApp.this.m_JythonConsoleFrame.setVisible(true);
/*  916:     */             }
/*  917:     */           }
/*  918:     */         }
/*  919:     */       });
/*  920:     */     }
/*  921:1046 */     Set<String> pluginNames = PluginManager.getPluginNamesOfType("weka.gui.GUIChooser.GUIChooserMenuPlugin");
/*  922:     */     boolean firstVis;
/*  923:     */     boolean firstTools;
/*  924:1048 */     if (pluginNames != null)
/*  925:     */     {
/*  926:1049 */       firstVis = true;
/*  927:1050 */       firstTools = true;
/*  928:1051 */       for (String name : pluginNames) {
/*  929:     */         try
/*  930:     */         {
/*  931:1053 */           final GUIChooser.GUIChooserMenuPlugin p = (GUIChooser.GUIChooserMenuPlugin)PluginManager.getPluginInstance("weka.gui.GUIChooser.GUIChooserMenuPlugin", name);
/*  932:1057 */           if ((p instanceof JComponent))
/*  933:     */           {
/*  934:1058 */             JMenuItem mItem = new JMenuItem(p.getMenuEntryText());
/*  935:1059 */             mItem.addActionListener(new ActionListener()
/*  936:     */             {
/*  937:     */               public void actionPerformed(ActionEvent e)
/*  938:     */               {
/*  939:1062 */                 JFrame appFrame = new JFrame(p.getApplicationName());
/*  940:1063 */                 appFrame.setIconImage(GUIChooserApp.this.m_Icon);
/*  941:1064 */                 appFrame.setDefaultCloseOperation(2);
/*  942:1065 */                 JMenuBar appMenu = p.getMenuBar();
/*  943:1066 */                 if (appMenu != null) {
/*  944:1067 */                   appFrame.setJMenuBar(appMenu);
/*  945:     */                 }
/*  946:1070 */                 appFrame.getContentPane().add((JComponent)p, "Center");
/*  947:     */                 
/*  948:1072 */                 appFrame.addWindowListener(new WindowAdapter()
/*  949:     */                 {
/*  950:     */                   public void windowClosed(WindowEvent e)
/*  951:     */                   {
/*  952:1075 */                     GUIChooserApp.this.m_menuPlugins.remove(GUIChooserApp.17.this.val$p);
/*  953:1076 */                     GUIChooserApp.this.checkExit();
/*  954:     */                   }
/*  955:1078 */                 });
/*  956:1079 */                 appFrame.setSize(800, 600);
/*  957:1080 */                 appFrame.setVisible(true);
/*  958:     */               }
/*  959:     */             });
/*  960:1084 */             if (p.getMenuToDisplayIn() == GUIChooser.GUIChooserMenuPlugin.Menu.VISUALIZATION)
/*  961:     */             {
/*  962:1086 */               if (firstVis)
/*  963:     */               {
/*  964:1087 */                 this.m_jMenuVisualization.add(new JSeparator());
/*  965:1088 */                 firstVis = false;
/*  966:     */               }
/*  967:1090 */               this.m_jMenuVisualization.add(mItem);
/*  968:     */             }
/*  969:     */             else
/*  970:     */             {
/*  971:1092 */               if (firstTools)
/*  972:     */               {
/*  973:1093 */                 this.m_jMenuTools.add(new JSeparator());
/*  974:1094 */                 firstTools = false;
/*  975:     */               }
/*  976:1096 */               this.m_jMenuTools.add(mItem);
/*  977:     */             }
/*  978:1099 */             this.m_menuPlugins.add(p);
/*  979:     */           }
/*  980:     */         }
/*  981:     */         catch (Exception e1)
/*  982:     */         {
/*  983:1102 */           e1.printStackTrace();
/*  984:     */         }
/*  985:     */       }
/*  986:     */     }
/*  987:1108 */     this.m_jMenuHelp = new JMenu();
/*  988:1109 */     this.m_jMenuBar.add(this.m_jMenuHelp);
/*  989:1110 */     this.m_jMenuHelp.setText("Help");
/*  990:1111 */     this.m_jMenuHelp.setMnemonic('H');
/*  991:     */     
/*  992:     */ 
/*  993:1114 */     JMenuItem jMenuItemHelpHomepage = new JMenuItem();
/*  994:1115 */     this.m_jMenuHelp.add(jMenuItemHelpHomepage);
/*  995:1116 */     jMenuItemHelpHomepage.setText("Weka homepage");
/*  996:     */     
/*  997:1118 */     jMenuItemHelpHomepage.setAccelerator(KeyStroke.getKeyStroke(72, 2));
/*  998:     */     
/*  999:1120 */     jMenuItemHelpHomepage.addActionListener(new ActionListener()
/* 1000:     */     {
/* 1001:     */       public void actionPerformed(ActionEvent e)
/* 1002:     */       {
/* 1003:1123 */         BrowserHelper.openURL("http://www.cs.waikato.ac.nz/~ml/weka/");
/* 1004:     */       }
/* 1005:1126 */     });
/* 1006:1127 */     this.m_jMenuHelp.add(new JSeparator());
/* 1007:     */     
/* 1008:     */ 
/* 1009:1130 */     JMenuItem jMenuItemHelpWekaWiki = new JMenuItem();
/* 1010:1131 */     this.m_jMenuHelp.add(jMenuItemHelpWekaWiki);
/* 1011:1132 */     jMenuItemHelpWekaWiki.setText("HOWTOs, code snippets, etc.");
/* 1012:     */     
/* 1013:1134 */     jMenuItemHelpWekaWiki.setAccelerator(KeyStroke.getKeyStroke(87, 2));
/* 1014:     */     
/* 1015:     */ 
/* 1016:1137 */     jMenuItemHelpWekaWiki.addActionListener(new ActionListener()
/* 1017:     */     {
/* 1018:     */       public void actionPerformed(ActionEvent e)
/* 1019:     */       {
/* 1020:1140 */         BrowserHelper.openURL("http://weka.wikispaces.com/");
/* 1021:     */       }
/* 1022:1144 */     });
/* 1023:1145 */     JMenuItem jMenuItemHelpSourceforge = new JMenuItem();
/* 1024:1146 */     this.m_jMenuHelp.add(jMenuItemHelpSourceforge);
/* 1025:1147 */     jMenuItemHelpSourceforge.setText("Weka on Sourceforge");
/* 1026:     */     
/* 1027:1149 */     jMenuItemHelpSourceforge.setAccelerator(KeyStroke.getKeyStroke(70, 2));
/* 1028:     */     
/* 1029:     */ 
/* 1030:1152 */     jMenuItemHelpSourceforge.addActionListener(new ActionListener()
/* 1031:     */     {
/* 1032:     */       public void actionPerformed(ActionEvent e)
/* 1033:     */       {
/* 1034:1155 */         BrowserHelper.openURL("http://sourceforge.net/projects/weka/");
/* 1035:     */       }
/* 1036:1159 */     });
/* 1037:1160 */     final JMenuItem jMenuItemHelpSysInfo = new JMenuItem();
/* 1038:1161 */     this.m_jMenuHelp.add(jMenuItemHelpSysInfo);
/* 1039:1162 */     jMenuItemHelpSysInfo.setText("SystemInfo");
/* 1040:     */     
/* 1041:1164 */     jMenuItemHelpSysInfo.setAccelerator(KeyStroke.getKeyStroke(73, 2));
/* 1042:     */     
/* 1043:     */ 
/* 1044:1167 */     jMenuItemHelpSysInfo.addActionListener(new ActionListener()
/* 1045:     */     {
/* 1046:     */       public void actionPerformed(ActionEvent e)
/* 1047:     */       {
/* 1048:1170 */         if (GUIChooserApp.this.m_SystemInfoFrame == null)
/* 1049:     */         {
/* 1050:1171 */           jMenuItemHelpSysInfo.setEnabled(false);
/* 1051:1172 */           GUIChooserApp.this.m_SystemInfoFrame = new JFrame("SystemInfo");
/* 1052:1173 */           GUIChooserApp.this.m_SystemInfoFrame.setIconImage(GUIChooserApp.this.m_Icon);
/* 1053:1174 */           GUIChooserApp.this.m_SystemInfoFrame.getContentPane().setLayout(new BorderLayout());
/* 1054:     */           
/* 1055:     */ 
/* 1056:1177 */           Hashtable<String, String> info = new SystemInfo().getSystemInfo();
/* 1057:     */           
/* 1058:     */ 
/* 1059:1180 */           Vector<String> names = new Vector();
/* 1060:1181 */           Enumeration<String> enm = info.keys();
/* 1061:1182 */           while (enm.hasMoreElements()) {
/* 1062:1183 */             names.add(enm.nextElement());
/* 1063:     */           }
/* 1064:1185 */           Collections.sort(names);
/* 1065:     */           
/* 1066:     */ 
/* 1067:1188 */           String[][] data = new String[info.size()][2];
/* 1068:1189 */           for (int i = 0; i < names.size(); i++)
/* 1069:     */           {
/* 1070:1190 */             data[i][0] = ((String)names.get(i)).toString();
/* 1071:1191 */             data[i][1] = ((String)info.get(data[i][0])).toString();
/* 1072:     */           }
/* 1073:1193 */           String[] titles = { "Key", "Value" };
/* 1074:1194 */           JTable table = new JTable(data, titles);
/* 1075:     */           
/* 1076:1196 */           GUIChooserApp.this.m_SystemInfoFrame.getContentPane().add(new JScrollPane(table), "Center");
/* 1077:     */           
/* 1078:1198 */           GUIChooserApp.this.m_SystemInfoFrame.addWindowListener(new WindowAdapter()
/* 1079:     */           {
/* 1080:     */             public void windowClosing(WindowEvent w)
/* 1081:     */             {
/* 1082:1201 */               GUIChooserApp.this.m_SystemInfoFrame.dispose();
/* 1083:1202 */               GUIChooserApp.this.m_SystemInfoFrame = null;
/* 1084:1203 */               GUIChooserApp.21.this.val$jMenuItemHelpSysInfo.setEnabled(true);
/* 1085:1204 */               GUIChooserApp.this.checkExit();
/* 1086:     */             }
/* 1087:1206 */           });
/* 1088:1207 */           GUIChooserApp.this.m_SystemInfoFrame.pack();
/* 1089:1208 */           GUIChooserApp.this.m_SystemInfoFrame.setSize(800, 600);
/* 1090:1209 */           GUIChooserApp.this.m_SystemInfoFrame.setVisible(true);
/* 1091:     */         }
/* 1092:     */       }
/* 1093:1215 */     });
/* 1094:1216 */     this.m_ExplorerBut.addActionListener(new ActionListener()
/* 1095:     */     {
/* 1096:     */       public void actionPerformed(ActionEvent e)
/* 1097:     */       {
/* 1098:1219 */         GUIChooserApp.this.showExplorer(null);
/* 1099:     */       }
/* 1100:1222 */     });
/* 1101:1223 */     this.m_ExperimenterBut.addActionListener(new ActionListener()
/* 1102:     */     {
/* 1103:     */       public void actionPerformed(ActionEvent e)
/* 1104:     */       {
/* 1105:1226 */         if (GUIChooserApp.this.m_ExperimenterFrame == null)
/* 1106:     */         {
/* 1107:1227 */           GUIChooserApp.this.m_ExperimenterBut.setEnabled(false);
/* 1108:1228 */           GUIChooserApp.this.m_ExperimenterFrame = new JFrame("Weka Experiment Environment");
/* 1109:1229 */           GUIChooserApp.this.m_ExperimenterFrame.setIconImage(GUIChooserApp.this.m_Icon);
/* 1110:1230 */           GUIChooserApp.this.m_ExperimenterFrame.getContentPane().setLayout(new BorderLayout());
/* 1111:1231 */           GUIChooserApp.this.m_ExperimenterFrame.getContentPane().add(new Experimenter(false), "Center");
/* 1112:     */           
/* 1113:1233 */           GUIChooserApp.this.m_ExperimenterFrame.addWindowListener(new WindowAdapter()
/* 1114:     */           {
/* 1115:     */             public void windowClosing(WindowEvent w)
/* 1116:     */             {
/* 1117:1236 */               GUIChooserApp.this.m_ExperimenterFrame.dispose();
/* 1118:1237 */               GUIChooserApp.this.m_ExperimenterFrame = null;
/* 1119:1238 */               GUIChooserApp.this.m_ExperimenterBut.setEnabled(true);
/* 1120:1239 */               GUIChooserApp.this.checkExit();
/* 1121:     */             }
/* 1122:1241 */           });
/* 1123:1242 */           GUIChooserApp.this.m_ExperimenterFrame.pack();
/* 1124:1243 */           GUIChooserApp.this.m_ExperimenterFrame.setSize(800, 600);
/* 1125:1244 */           GUIChooserApp.this.m_ExperimenterFrame.setVisible(true);
/* 1126:     */         }
/* 1127:     */       }
/* 1128:1248 */     });
/* 1129:1249 */     this.m_KnowledgeFlowBut.addActionListener(new ActionListener()
/* 1130:     */     {
/* 1131:     */       public void actionPerformed(ActionEvent e)
/* 1132:     */       {
/* 1133:1252 */         GUIChooserApp.this.showKnowledgeFlow(null);
/* 1134:     */       }
/* 1135:1255 */     });
/* 1136:1256 */     this.m_WorkbenchBut.addActionListener(new ActionListener()
/* 1137:     */     {
/* 1138:     */       public void actionPerformed(ActionEvent e)
/* 1139:     */       {
/* 1140:1260 */         if (GUIChooserApp.this.m_WorkbenchFrame == null)
/* 1141:     */         {
/* 1142:1261 */           WorkbenchApp app = new WorkbenchApp();
/* 1143:1262 */           GUIChooserApp.this.m_WorkbenchBut.setEnabled(false);
/* 1144:1263 */           GUIChooserApp.this.m_WorkbenchFrame = new JFrame("Weka Workbench");
/* 1145:1264 */           GUIChooserApp.this.m_WorkbenchFrame.setIconImage(GUIChooserApp.this.m_Icon);
/* 1146:1265 */           GUIChooserApp.this.m_WorkbenchFrame.add(app, "Center");
/* 1147:1266 */           GUIChooserApp.this.m_WorkbenchFrame.addWindowListener(new WindowAdapter()
/* 1148:     */           {
/* 1149:     */             public void windowClosing(WindowEvent e)
/* 1150:     */             {
/* 1151:1269 */               GUIChooserApp.this.m_WorkbenchFrame.dispose();
/* 1152:1270 */               GUIChooserApp.this.m_WorkbenchFrame = null;
/* 1153:1271 */               GUIChooserApp.this.m_WorkbenchBut.setEnabled(true);
/* 1154:1272 */               GUIChooserApp.this.checkExit();
/* 1155:     */             }
/* 1156:1274 */           });
/* 1157:1275 */           app.showMenuBar(GUIChooserApp.this.m_WorkbenchFrame);
/* 1158:1276 */           GUIChooserApp.this.m_WorkbenchFrame.pack();
/* 1159:1277 */           GUIChooserApp.this.m_WorkbenchFrame.setSize(1024, 768);
/* 1160:1278 */           GUIChooserApp.this.m_WorkbenchFrame.setVisible(true);
/* 1161:     */         }
/* 1162:     */       }
/* 1163:1282 */     });
/* 1164:1283 */     this.m_SimpleBut.addActionListener(new ActionListener()
/* 1165:     */     {
/* 1166:     */       public void actionPerformed(ActionEvent e)
/* 1167:     */       {
/* 1168:1286 */         if (GUIChooserApp.this.m_SimpleCLI == null)
/* 1169:     */         {
/* 1170:1287 */           GUIChooserApp.this.m_SimpleBut.setEnabled(false);
/* 1171:     */           try
/* 1172:     */           {
/* 1173:1289 */             GUIChooserApp.this.m_SimpleCLI = new SimpleCLI();
/* 1174:1290 */             GUIChooserApp.this.m_SimpleCLI.setIconImage(GUIChooserApp.this.m_Icon);
/* 1175:     */           }
/* 1176:     */           catch (Exception ex)
/* 1177:     */           {
/* 1178:1292 */             throw new Error("Couldn't start SimpleCLI!");
/* 1179:     */           }
/* 1180:1294 */           GUIChooserApp.this.m_SimpleCLI.addWindowListener(new WindowAdapter()
/* 1181:     */           {
/* 1182:     */             public void windowClosing(WindowEvent w)
/* 1183:     */             {
/* 1184:1297 */               GUIChooserApp.this.m_SimpleCLI.dispose();
/* 1185:1298 */               GUIChooserApp.this.m_SimpleCLI = null;
/* 1186:1299 */               GUIChooserApp.this.m_SimpleBut.setEnabled(true);
/* 1187:1300 */               GUIChooserApp.this.checkExit();
/* 1188:     */             }
/* 1189:1302 */           });
/* 1190:1303 */           GUIChooserApp.this.m_SimpleCLI.setVisible(true);
/* 1191:     */         }
/* 1192:     */       }
/* 1193:1326 */     });
/* 1194:1327 */     setJMenuBar(this.m_jMenuBar);
/* 1195:     */     
/* 1196:1329 */     addWindowListener(new WindowAdapter()
/* 1197:     */     {
/* 1198:     */       public void windowClosing(WindowEvent w)
/* 1199:     */       {
/* 1200:1332 */         GUIChooserApp.this.dispose();
/* 1201:1333 */         GUIChooserApp.this.checkExit();
/* 1202:     */       }
/* 1203:1335 */     });
/* 1204:1336 */     pack();
/* 1205:1338 */     if (!Utils.getDontShowDialog("weka.gui.GUIChooser.HowToFindPackageManager"))
/* 1206:     */     {
/* 1207:1340 */       Thread tipThread = new Thread()
/* 1208:     */       {
/* 1209:     */         public void run()
/* 1210:     */         {
/* 1211:1343 */           JCheckBox dontShow = new JCheckBox("Do not show this message again");
/* 1212:1344 */           Object[] stuff = new Object[2];
/* 1213:1345 */           stuff[0] = "Weka has a package manager that you\ncan use to install many learning schemes and tools.\nThe package manager can be found under the \"Tools\" menu.\n";
/* 1214:     */           
/* 1215:     */ 
/* 1216:1348 */           stuff[1] = dontShow;
/* 1217:     */           
/* 1218:1350 */           JOptionPane.showMessageDialog(GUIChooserApp.this, stuff, "Weka GUIChooser", 0);
/* 1219:1353 */           if (dontShow.isSelected()) {
/* 1220:     */             try
/* 1221:     */             {
/* 1222:1355 */               Utils.setDontShowDialog("weka.gui.GUIChooser.HowToFindPackageManager");
/* 1223:     */             }
/* 1224:     */             catch (Exception ex) {}
/* 1225:     */           }
/* 1226:     */         }
/* 1227:1362 */       };
/* 1228:1363 */       tipThread.setPriority(1);
/* 1229:1364 */       tipThread.start();
/* 1230:     */     }
/* 1231:     */   }
/* 1232:     */   
/* 1233:     */   public void showKnowledgeFlow(String fileToLoad)
/* 1234:     */   {
/* 1235:1369 */     if (this.m_KnowledgeFlowFrame == null)
/* 1236:     */     {
/* 1237:1370 */       if (this.m_knowledgeFlow == null) {
/* 1238:1371 */         this.m_knowledgeFlow = new KnowledgeFlowApp();
/* 1239:     */       }
/* 1240:1373 */       this.m_KnowledgeFlowBut.setEnabled(false);
/* 1241:1374 */       if ((this.m_pendingKnowledgeFlowLoad != null) && (this.m_pendingKnowledgeFlowLoad.length() > 0))
/* 1242:     */       {
/* 1243:1380 */         ((MainKFPerspective)this.m_knowledgeFlow.getMainPerspective()).loadLayout(new File(this.m_pendingKnowledgeFlowLoad), true);
/* 1244:     */         
/* 1245:1382 */         this.m_pendingKnowledgeFlowLoad = null;
/* 1246:     */       }
/* 1247:1384 */       this.m_KnowledgeFlowFrame = new JFrame("Weka KnowledgeFlow Environment");
/* 1248:1385 */       this.m_KnowledgeFlowFrame.setIconImage(this.m_Icon);
/* 1249:1386 */       this.m_KnowledgeFlowFrame.getContentPane().setLayout(new BorderLayout());
/* 1250:1387 */       this.m_KnowledgeFlowFrame.getContentPane().add(this.m_knowledgeFlow, "Center");
/* 1251:     */       
/* 1252:1389 */       this.m_knowledgeFlow.showMenuBar(this.m_KnowledgeFlowFrame);
/* 1253:1390 */       this.m_KnowledgeFlowFrame.addWindowListener(new WindowAdapter()
/* 1254:     */       {
/* 1255:     */         public void windowClosing(WindowEvent w)
/* 1256:     */         {
/* 1257:1394 */           ((MainKFPerspective)GUIChooserApp.this.m_knowledgeFlow.getMainPerspective()).closeAllTabs();
/* 1258:     */           
/* 1259:1396 */           ((MainKFPerspective)GUIChooserApp.this.m_knowledgeFlow.getMainPerspective()).addUntitledTab();
/* 1260:     */           
/* 1261:     */ 
/* 1262:     */ 
/* 1263:     */ 
/* 1264:     */ 
/* 1265:     */ 
/* 1266:1403 */           GUIChooserApp.this.m_KnowledgeFlowFrame.dispose();
/* 1267:1404 */           GUIChooserApp.this.m_KnowledgeFlowFrame = null;
/* 1268:1405 */           GUIChooserApp.this.m_knowledgeFlow = null;
/* 1269:1406 */           GUIChooserApp.this.m_KnowledgeFlowBut.setEnabled(true);
/* 1270:1407 */           GUIChooserApp.this.checkExit();
/* 1271:     */         }
/* 1272:1409 */       });
/* 1273:1410 */       this.m_KnowledgeFlowFrame.pack();
/* 1274:1411 */       this.m_KnowledgeFlowFrame.setSize(1024, 768);
/* 1275:1412 */       this.m_KnowledgeFlowFrame.setVisible(true);
/* 1276:     */     }
/* 1277:     */   }
/* 1278:     */   
/* 1279:     */   public void showExplorer(String fileToLoad)
/* 1280:     */   {
/* 1281:1417 */     Explorer expl = null;
/* 1282:1418 */     if (this.m_ExplorerFrame == null)
/* 1283:     */     {
/* 1284:1419 */       this.m_ExplorerBut.setEnabled(false);
/* 1285:1420 */       this.m_ExplorerFrame = new JFrame("Weka Explorer");
/* 1286:1421 */       this.m_ExplorerFrame.setIconImage(this.m_Icon);
/* 1287:1422 */       this.m_ExplorerFrame.getContentPane().setLayout(new BorderLayout());
/* 1288:1423 */       expl = new Explorer();
/* 1289:     */       
/* 1290:1425 */       this.m_ExplorerFrame.getContentPane().add(expl, "Center");
/* 1291:1426 */       this.m_ExplorerFrame.addWindowListener(new WindowAdapter()
/* 1292:     */       {
/* 1293:     */         public void windowClosing(WindowEvent w)
/* 1294:     */         {
/* 1295:1429 */           GUIChooserApp.this.m_ExplorerFrame.dispose();
/* 1296:1430 */           GUIChooserApp.this.m_ExplorerFrame = null;
/* 1297:1431 */           GUIChooserApp.this.m_ExplorerBut.setEnabled(true);
/* 1298:1432 */           GUIChooserApp.this.checkExit();
/* 1299:     */         }
/* 1300:1434 */       });
/* 1301:1435 */       this.m_ExplorerFrame.pack();
/* 1302:1436 */       this.m_ExplorerFrame.setSize(800, 600);
/* 1303:1437 */       this.m_ExplorerFrame.setVisible(true);
/* 1304:     */     }
/* 1305:     */     else
/* 1306:     */     {
/* 1307:1439 */       Object o = this.m_ExplorerFrame.getContentPane().getComponent(0);
/* 1308:1440 */       if ((o instanceof Explorer)) {
/* 1309:1441 */         expl = (Explorer)o;
/* 1310:     */       }
/* 1311:     */     }
/* 1312:1445 */     if (fileToLoad != null) {
/* 1313:     */       try
/* 1314:     */       {
/* 1315:1447 */         AbstractFileLoader loader = ConverterUtils.getLoaderForFile(fileToLoad);
/* 1316:     */         
/* 1317:1449 */         loader.setFile(new File(fileToLoad));
/* 1318:1450 */         expl.getPreprocessPanel().setInstancesFromFile(loader);
/* 1319:     */       }
/* 1320:     */       catch (Exception ex)
/* 1321:     */       {
/* 1322:1452 */         ex.printStackTrace();
/* 1323:     */       }
/* 1324:     */     }
/* 1325:     */   }
/* 1326:     */   
/* 1327:     */   protected void insertMenuItem(JMenu menu, JMenuItem menuitem)
/* 1328:     */   {
/* 1329:1464 */     insertMenuItem(menu, menuitem, 0);
/* 1330:     */   }
/* 1331:     */   
/* 1332:     */   protected void insertMenuItem(JMenu menu, JMenuItem menuitem, int startIndex)
/* 1333:     */   {
/* 1334:1482 */     boolean inserted = false;
/* 1335:1483 */     String newStr = menuitem.getText().toLowerCase();
/* 1336:1486 */     for (int i = startIndex; i < menu.getMenuComponentCount(); i++) {
/* 1337:1487 */       if ((menu.getMenuComponent(i) instanceof JMenuItem))
/* 1338:     */       {
/* 1339:1491 */         JMenuItem current = (JMenuItem)menu.getMenuComponent(i);
/* 1340:1492 */         String currentStr = current.getText().toLowerCase();
/* 1341:1493 */         if (currentStr.compareTo(newStr) > 0)
/* 1342:     */         {
/* 1343:1494 */           inserted = true;
/* 1344:1495 */           menu.insert(menuitem, i);
/* 1345:1496 */           break;
/* 1346:     */         }
/* 1347:     */       }
/* 1348:     */     }
/* 1349:1501 */     if (!inserted) {
/* 1350:1502 */       menu.add(menuitem);
/* 1351:     */     }
/* 1352:     */   }
/* 1353:     */   
/* 1354:     */   protected Container createFrame(GUIChooserApp parent, String title, Component c, LayoutManager layout, Object layoutConstraints, int width, int height, JMenuBar menu, boolean listener, boolean visible)
/* 1355:     */   {
/* 1356:1525 */     Container result = null;
/* 1357:     */     
/* 1358:1527 */     final ChildFrameSDI frame = new ChildFrameSDI(parent, title);
/* 1359:     */     
/* 1360:     */ 
/* 1361:1530 */     frame.setLayout(layout);
/* 1362:1531 */     if (c != null) {
/* 1363:1532 */       frame.getContentPane().add(c, layoutConstraints);
/* 1364:     */     }
/* 1365:1536 */     frame.setJMenuBar(menu);
/* 1366:     */     
/* 1367:     */ 
/* 1368:1539 */     frame.pack();
/* 1369:1540 */     if ((width > -1) && (height > -1)) {
/* 1370:1541 */       frame.setSize(width, height);
/* 1371:     */     }
/* 1372:1543 */     frame.validate();
/* 1373:     */     
/* 1374:     */ 
/* 1375:1546 */     int screenHeight = getGraphicsConfiguration().getBounds().height;
/* 1376:1547 */     int screenWidth = getGraphicsConfiguration().getBounds().width;
/* 1377:1548 */     frame.setLocation((screenWidth - frame.getBounds().width) / 2, (screenHeight - frame.getBounds().height) / 2);
/* 1378:1552 */     if (listener) {
/* 1379:1553 */       frame.addWindowListener(new WindowAdapter()
/* 1380:     */       {
/* 1381:     */         public void windowClosing(WindowEvent e)
/* 1382:     */         {
/* 1383:1556 */           frame.dispose();
/* 1384:     */         }
/* 1385:     */       });
/* 1386:     */     }
/* 1387:1562 */     if (visible) {
/* 1388:1563 */       frame.setVisible(true);
/* 1389:     */     }
/* 1390:1566 */     result = frame;
/* 1391:     */     
/* 1392:1568 */     return result;
/* 1393:     */   }
/* 1394:     */   
/* 1395:     */   public static class ChildFrameSDI
/* 1396:     */     extends JFrame
/* 1397:     */   {
/* 1398:     */     private static final long serialVersionUID = 8588293938686425618L;
/* 1399:     */     protected GUIChooserApp m_Parent;
/* 1400:     */     
/* 1401:     */     public ChildFrameSDI(GUIChooserApp parent, String title)
/* 1402:     */     {
/* 1403:1592 */       super();
/* 1404:     */       
/* 1405:1594 */       this.m_Parent = parent;
/* 1406:     */       
/* 1407:1596 */       addWindowListener(new WindowAdapter()
/* 1408:     */       {
/* 1409:     */         public void windowActivated(WindowEvent e)
/* 1410:     */         {
/* 1411:1600 */           if (GUIChooserApp.ChildFrameSDI.this.getParentFrame() != null) {
/* 1412:1601 */             GUIChooserApp.ChildFrameSDI.this.getParentFrame().createTitle(GUIChooserApp.ChildFrameSDI.this.getTitle());
/* 1413:     */           }
/* 1414:     */         }
/* 1415:     */       });
/* 1416:1607 */       if (getParentFrame() != null)
/* 1417:     */       {
/* 1418:1608 */         getParentFrame().addChildFrame(this);
/* 1419:1609 */         setIconImage(getParentFrame().getIconImage());
/* 1420:     */       }
/* 1421:     */     }
/* 1422:     */     
/* 1423:     */     public GUIChooserApp getParentFrame()
/* 1424:     */     {
/* 1425:1619 */       return this.m_Parent;
/* 1426:     */     }
/* 1427:     */     
/* 1428:     */     public void dispose()
/* 1429:     */     {
/* 1430:1627 */       if (getParentFrame() != null)
/* 1431:     */       {
/* 1432:1628 */         getParentFrame().removeChildFrame(this);
/* 1433:1629 */         getParentFrame().createTitle("");
/* 1434:     */       }
/* 1435:1632 */       super.dispose();
/* 1436:     */     }
/* 1437:     */   }
/* 1438:     */   
/* 1439:     */   protected void createTitle(String title)
/* 1440:     */   {
/* 1441:1644 */     String newTitle = "Weka " + new Version();
/* 1442:1645 */     if (title.length() != 0) {
/* 1443:1646 */       newTitle = newTitle + " - " + title;
/* 1444:     */     }
/* 1445:1649 */     setTitle(newTitle);
/* 1446:     */   }
/* 1447:     */   
/* 1448:     */   public void addChildFrame(Container c)
/* 1449:     */   {
/* 1450:1658 */     this.m_ChildFrames.add(c);
/* 1451:     */   }
/* 1452:     */   
/* 1453:     */   public boolean removeChildFrame(Container c)
/* 1454:     */   {
/* 1455:1668 */     boolean result = this.m_ChildFrames.remove(c);
/* 1456:1669 */     return result;
/* 1457:     */   }
/* 1458:     */   
/* 1459:     */   private void checkExit()
/* 1460:     */   {
/* 1461:1677 */     if ((!isVisible()) && (this.m_ExplorerFrame == null) && (this.m_ExperimenterFrame == null) && (this.m_KnowledgeFlowFrame == null) && (this.m_SimpleCLI == null) && (this.m_ArffViewers.size() == 0) && (this.m_SqlViewerFrame == null) && (this.m_GroovyConsoleFrame == null) && (this.m_JythonConsoleFrame == null) && (this.m_EnsembleLibraryFrame == null) && (this.m_Plots.size() == 0) && (this.m_ROCs.size() == 0) && (this.m_TreeVisualizers.size() == 0) && (this.m_GraphVisualizers.size() == 0) && (this.m_BoundaryVisualizerFrame == null) && (this.m_SystemInfoFrame == null)) {
/* 1462:1691 */       System.exit(0);
/* 1463:     */     }
/* 1464:     */   }
/* 1465:     */   
/* 1466:     */   public static final class GUIChooserDefaults
/* 1467:     */     extends Defaults
/* 1468:     */   {
/* 1469:     */     public static final String APP_NAME = "GUIChooser";
/* 1470:     */     public static final String APP_ID = "guichooser";
/* 1471:1707 */     protected static final Settings.SettingKey LAF_KEY = new Settings.SettingKey("guichooser.lookAndFeel", "Look and feel for UI", "Note: a restart is required for this setting to come into effect");
/* 1472:     */     protected static final String LAF = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
/* 1473:     */     private static final long serialVersionUID = -8524894440289936685L;
/* 1474:     */     
/* 1475:     */     public GUIChooserDefaults()
/* 1476:     */     {
/* 1477:1720 */       super();
/* 1478:1721 */       List<String> lafs = LookAndFeel.getAvailableLookAndFeelClasses();
/* 1479:1722 */       lafs.add(0, "<use platform default>");
/* 1480:1723 */       LAF_KEY.setPickList(lafs);
/* 1481:1724 */       this.m_defaults.put(LAF_KEY, "javax.swing.plaf.nimbus.NimbusLookAndFeel");
/* 1482:     */     }
/* 1483:     */   }
/* 1484:     */   
/* 1485:1735 */   private static Memory m_Memory = new Memory(true);
/* 1486:     */   
/* 1487:     */   public static void main(String[] args)
/* 1488:     */   {
/* 1489:1744 */     Logger.log(Logger.Level.INFO, "Logging started");
/* 1490:     */     try
/* 1491:     */     {
/* 1492:1747 */       LookAndFeel.setLookAndFeel("guichooser", "guichooser.lookAndFeel", "javax.swing.plaf.nimbus.NimbusLookAndFeel");
/* 1493:     */     }
/* 1494:     */     catch (IOException ex)
/* 1495:     */     {
/* 1496:1750 */       ex.printStackTrace();
/* 1497:     */     }
/* 1498:1754 */     final PrintStream savedStdOut = System.out;
/* 1499:1755 */     final PrintStream savedStdErr = System.err;
/* 1500:     */     
/* 1501:     */ 
/* 1502:1758 */     SecurityManager sm = System.getSecurityManager();
/* 1503:1759 */     System.setSecurityManager(new SecurityManager()
/* 1504:     */     {
/* 1505:     */       public void checkExit(int status)
/* 1506:     */       {
/* 1507:1761 */         if (this.val$sm != null) {
/* 1508:1762 */           this.val$sm.checkExit(status);
/* 1509:     */         }
/* 1510:1766 */         for (Class cl : getClassContext()) {
/* 1511:1767 */           if (cl.getName().equals("tigerjython.gui.MainWindow"))
/* 1512:     */           {
/* 1513:1768 */             for (Frame frame : Frame.getFrames()) {
/* 1514:1769 */               if (frame.getTitle().toLowerCase().startsWith("tigerjython")) {
/* 1515:1770 */                 frame.dispose();
/* 1516:     */               }
/* 1517:     */             }
/* 1518:1775 */             System.setOut(savedStdOut);
/* 1519:1776 */             System.setErr(savedStdErr);
/* 1520:     */             
/* 1521:     */ 
/* 1522:1779 */             Logger.log(Logger.Level.INFO, "Intercepted System.exit() from TigerJython. Please ignore");
/* 1523:     */             
/* 1524:1781 */             throw new SecurityException("Intercepted System.exit() from TigerJython. Please ignore!");
/* 1525:     */           }
/* 1526:     */         }
/* 1527:     */       }
/* 1528:     */       
/* 1529:     */       public void checkPermission(Permission perm)
/* 1530:     */       {
/* 1531:1788 */         if (this.val$sm != null) {
/* 1532:1789 */           this.val$sm.checkPermission(perm);
/* 1533:     */         }
/* 1534:     */       }
/* 1535:     */       
/* 1536:     */       public void checkPermission(Permission perm, Object context)
/* 1537:     */       {
/* 1538:1794 */         if (this.val$sm != null) {
/* 1539:1795 */           this.val$sm.checkPermission(perm, context);
/* 1540:     */         }
/* 1541:     */       }
/* 1542:     */     });
/* 1543:     */     try
/* 1544:     */     {
/* 1545:1805 */       createSingleton();
/* 1546:1806 */       m_chooser.setVisible(true);
/* 1547:1808 */       if ((args != null) && (args.length > 0)) {
/* 1548:1809 */         m_chooser.showExplorer(args[0]);
/* 1549:     */       }
/* 1550:1812 */       Thread memMonitor = new Thread()
/* 1551:     */       {
/* 1552:     */         public void run()
/* 1553:     */         {
/* 1554:     */           for (;;)
/* 1555:     */           {
/* 1556:1821 */             if (GUIChooserApp.m_Memory.isOutOfMemory())
/* 1557:     */             {
/* 1558:1823 */               GUIChooserApp.m_chooser.dispose();
/* 1559:1824 */               if (GUIChooserApp.m_chooser.m_ExperimenterFrame != null)
/* 1560:     */               {
/* 1561:1825 */                 GUIChooserApp.m_chooser.m_ExperimenterFrame.dispose();
/* 1562:1826 */                 GUIChooserApp.m_chooser.m_ExperimenterFrame = null;
/* 1563:     */               }
/* 1564:1828 */               if (GUIChooserApp.m_chooser.m_ExplorerFrame != null)
/* 1565:     */               {
/* 1566:1829 */                 GUIChooserApp.m_chooser.m_ExplorerFrame.dispose();
/* 1567:1830 */                 GUIChooserApp.m_chooser.m_ExplorerFrame = null;
/* 1568:     */               }
/* 1569:1832 */               if (GUIChooserApp.m_chooser.m_KnowledgeFlowFrame != null)
/* 1570:     */               {
/* 1571:1833 */                 GUIChooserApp.m_chooser.m_KnowledgeFlowFrame.dispose();
/* 1572:1834 */                 GUIChooserApp.m_chooser.m_KnowledgeFlowFrame = null;
/* 1573:     */               }
/* 1574:1836 */               if (GUIChooserApp.m_chooser.m_SimpleCLI != null)
/* 1575:     */               {
/* 1576:1837 */                 GUIChooserApp.m_chooser.m_SimpleCLI.dispose();
/* 1577:1838 */                 GUIChooserApp.m_chooser.m_SimpleCLI = null;
/* 1578:     */               }
/* 1579:1840 */               if (GUIChooserApp.m_chooser.m_ArffViewers.size() > 0)
/* 1580:     */               {
/* 1581:1841 */                 for (int i = 0; i < GUIChooserApp.m_chooser.m_ArffViewers.size(); i++)
/* 1582:     */                 {
/* 1583:1842 */                   ArffViewer av = (ArffViewer)GUIChooserApp.m_chooser.m_ArffViewers.get(i);
/* 1584:1843 */                   av.dispose();
/* 1585:     */                 }
/* 1586:1845 */                 GUIChooserApp.m_chooser.m_ArffViewers.clear();
/* 1587:     */               }
/* 1588:1847 */               GUIChooserApp.access$302(null);
/* 1589:1848 */               System.gc();
/* 1590:     */               
/* 1591:     */ 
/* 1592:1851 */               GUIChooserApp.m_LogWindow.setVisible(true);
/* 1593:1852 */               GUIChooserApp.m_LogWindow.toFront();
/* 1594:1853 */               System.err.println("\ndisplayed message:");
/* 1595:1854 */               GUIChooserApp.m_Memory.showOutOfMemory();
/* 1596:1855 */               System.err.println("\nexiting...");
/* 1597:1856 */               System.exit(-1);
/* 1598:     */             }
/* 1599:     */           }
/* 1600:     */         }
/* 1601:1864 */       };
/* 1602:1865 */       memMonitor.setPriority(5);
/* 1603:1866 */       memMonitor.start();
/* 1604:     */     }
/* 1605:     */     catch (Exception ex)
/* 1606:     */     {
/* 1607:1868 */       ex.printStackTrace();
/* 1608:1869 */       System.err.println(ex.getMessage());
/* 1609:     */     }
/* 1610:     */   }
/* 1611:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.GUIChooserApp
 * JD-Core Version:    0.7.0.1
 */