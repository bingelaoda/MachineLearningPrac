/*    1:     */ package weka.gui;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.awt.Color;
/*    5:     */ import java.awt.Component;
/*    6:     */ import java.awt.Container;
/*    7:     */ import java.awt.Dimension;
/*    8:     */ import java.awt.Graphics;
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
/*   24:     */ import java.io.PrintStream;
/*   25:     */ import java.io.Reader;
/*   26:     */ import java.util.Collections;
/*   27:     */ import java.util.Enumeration;
/*   28:     */ import java.util.HashSet;
/*   29:     */ import java.util.Hashtable;
/*   30:     */ import java.util.Iterator;
/*   31:     */ import java.util.Properties;
/*   32:     */ import java.util.Vector;
/*   33:     */ import javax.swing.BorderFactory;
/*   34:     */ import javax.swing.ImageIcon;
/*   35:     */ import javax.swing.JDesktopPane;
/*   36:     */ import javax.swing.JFileChooser;
/*   37:     */ import javax.swing.JFrame;
/*   38:     */ import javax.swing.JInternalFrame;
/*   39:     */ import javax.swing.JLabel;
/*   40:     */ import javax.swing.JMenu;
/*   41:     */ import javax.swing.JMenuBar;
/*   42:     */ import javax.swing.JMenuItem;
/*   43:     */ import javax.swing.JOptionPane;
/*   44:     */ import javax.swing.JPanel;
/*   45:     */ import javax.swing.JScrollPane;
/*   46:     */ import javax.swing.JSeparator;
/*   47:     */ import javax.swing.JTable;
/*   48:     */ import javax.swing.event.InternalFrameAdapter;
/*   49:     */ import javax.swing.event.InternalFrameEvent;
/*   50:     */ import weka.classifiers.bayes.net.GUI;
/*   51:     */ import weka.classifiers.evaluation.ThresholdCurve;
/*   52:     */ import weka.core.Copyright;
/*   53:     */ import weka.core.Instances;
/*   54:     */ import weka.core.Memory;
/*   55:     */ import weka.core.Option;
/*   56:     */ import weka.core.OptionHandler;
/*   57:     */ import weka.core.SelectedTag;
/*   58:     */ import weka.core.SystemInfo;
/*   59:     */ import weka.core.Tag;
/*   60:     */ import weka.core.Utils;
/*   61:     */ import weka.core.Version;
/*   62:     */ import weka.core.logging.Logger;
/*   63:     */ import weka.core.logging.Logger.Level;
/*   64:     */ import weka.core.scripting.Groovy;
/*   65:     */ import weka.core.scripting.Jython;
/*   66:     */ import weka.gui.arffviewer.ArffViewerMainPanel;
/*   67:     */ import weka.gui.beans.KnowledgeFlowApp;
/*   68:     */ import weka.gui.beans.StartUpListener;
/*   69:     */ import weka.gui.boundaryvisualizer.BoundaryVisualizer;
/*   70:     */ import weka.gui.experiment.Experimenter;
/*   71:     */ import weka.gui.explorer.Explorer;
/*   72:     */ import weka.gui.graphvisualizer.GraphVisualizer;
/*   73:     */ import weka.gui.scripting.GroovyPanel;
/*   74:     */ import weka.gui.scripting.JythonPanel;
/*   75:     */ import weka.gui.sql.SqlViewer;
/*   76:     */ import weka.gui.treevisualizer.Node;
/*   77:     */ import weka.gui.treevisualizer.NodePlace;
/*   78:     */ import weka.gui.treevisualizer.PlaceNode2;
/*   79:     */ import weka.gui.treevisualizer.TreeBuild;
/*   80:     */ import weka.gui.treevisualizer.TreeVisualizer;
/*   81:     */ import weka.gui.visualize.PlotData2D;
/*   82:     */ import weka.gui.visualize.ThresholdVisualizePanel;
/*   83:     */ import weka.gui.visualize.VisualizePanel;
/*   84:     */ 
/*   85:     */ public class Main
/*   86:     */   extends JFrame
/*   87:     */   implements OptionHandler
/*   88:     */ {
/*   89:     */   private static final long serialVersionUID = 1453813254824253849L;
/*   90:     */   public static final int GUI_MDI = 0;
/*   91:     */   public static final int GUI_SDI = 1;
/*   92:     */   
/*   93:     */   public static class BackgroundDesktopPane
/*   94:     */     extends JDesktopPane
/*   95:     */   {
/*   96:     */     private static final long serialVersionUID = 2046713123452402745L;
/*   97:     */     protected Image m_Background;
/*   98:     */     
/*   99:     */     public BackgroundDesktopPane(String image)
/*  100:     */     {
/*  101:     */       try
/*  102:     */       {
/*  103: 151 */         this.m_Background = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(image));
/*  104:     */       }
/*  105:     */       catch (Exception e)
/*  106:     */       {
/*  107: 154 */         e.printStackTrace();
/*  108:     */       }
/*  109:     */     }
/*  110:     */     
/*  111:     */     public void paintComponent(Graphics g)
/*  112:     */     {
/*  113: 165 */       super.paintComponent(g);
/*  114: 167 */       if (this.m_Background != null)
/*  115:     */       {
/*  116: 168 */         g.setColor(Color.WHITE);
/*  117: 169 */         g.clearRect(0, 0, getWidth(), getHeight());
/*  118:     */         
/*  119: 171 */         int width = this.m_Background.getWidth(null);
/*  120: 172 */         int height = this.m_Background.getHeight(null);
/*  121: 173 */         int x = (getWidth() - width) / 2;
/*  122: 174 */         int y = (getHeight() - height) / 2;
/*  123: 175 */         g.drawImage(this.m_Background, x, y, width, height, this);
/*  124:     */       }
/*  125:     */     }
/*  126:     */   }
/*  127:     */   
/*  128:     */   public static class ChildFrameSDI
/*  129:     */     extends JFrame
/*  130:     */   {
/*  131:     */     private static final long serialVersionUID = 8588293938686425618L;
/*  132:     */     protected Main m_Parent;
/*  133:     */     
/*  134:     */     public ChildFrameSDI(Main parent, String title)
/*  135:     */     {
/*  136: 201 */       super();
/*  137:     */       
/*  138: 203 */       this.m_Parent = parent;
/*  139:     */       
/*  140: 205 */       addWindowListener(new WindowAdapter()
/*  141:     */       {
/*  142:     */         public void windowActivated(WindowEvent e)
/*  143:     */         {
/*  144: 209 */           if (Main.ChildFrameSDI.this.getParentFrame() != null) {
/*  145: 210 */             Main.ChildFrameSDI.this.getParentFrame().createTitle(Main.ChildFrameSDI.this.getTitle());
/*  146:     */           }
/*  147:     */         }
/*  148:     */       });
/*  149: 216 */       if (getParentFrame() != null)
/*  150:     */       {
/*  151: 217 */         getParentFrame().addChildFrame(this);
/*  152: 218 */         setIconImage(getParentFrame().getIconImage());
/*  153:     */       }
/*  154:     */     }
/*  155:     */     
/*  156:     */     public Main getParentFrame()
/*  157:     */     {
/*  158: 228 */       return this.m_Parent;
/*  159:     */     }
/*  160:     */     
/*  161:     */     public void dispose()
/*  162:     */     {
/*  163: 236 */       if (getParentFrame() != null)
/*  164:     */       {
/*  165: 237 */         getParentFrame().removeChildFrame(this);
/*  166: 238 */         getParentFrame().createTitle("");
/*  167:     */       }
/*  168: 241 */       super.dispose();
/*  169:     */     }
/*  170:     */   }
/*  171:     */   
/*  172:     */   public static class ChildFrameMDI
/*  173:     */     extends JInternalFrame
/*  174:     */   {
/*  175:     */     private static final long serialVersionUID = 3772573515346899959L;
/*  176:     */     protected Main m_Parent;
/*  177:     */     
/*  178:     */     public ChildFrameMDI(Main parent, String title)
/*  179:     */     {
/*  180: 266 */       super(true, true, true, true);
/*  181:     */       
/*  182: 268 */       this.m_Parent = parent;
/*  183:     */       
/*  184: 270 */       addInternalFrameListener(new InternalFrameAdapter()
/*  185:     */       {
/*  186:     */         public void internalFrameActivated(InternalFrameEvent e)
/*  187:     */         {
/*  188: 274 */           if (Main.ChildFrameMDI.this.getParentFrame() != null) {
/*  189: 275 */             Main.ChildFrameMDI.this.getParentFrame().createTitle(Main.ChildFrameMDI.this.getTitle());
/*  190:     */           }
/*  191:     */         }
/*  192:     */       });
/*  193: 281 */       if (getParentFrame() != null)
/*  194:     */       {
/*  195: 282 */         getParentFrame().addChildFrame(this);
/*  196: 283 */         getParentFrame().jDesktopPane.add(this);
/*  197:     */       }
/*  198:     */     }
/*  199:     */     
/*  200:     */     public Main getParentFrame()
/*  201:     */     {
/*  202: 293 */       return this.m_Parent;
/*  203:     */     }
/*  204:     */     
/*  205:     */     public void dispose()
/*  206:     */     {
/*  207: 301 */       if (getParentFrame() != null)
/*  208:     */       {
/*  209: 302 */         getParentFrame().removeChildFrame(this);
/*  210: 303 */         getParentFrame().createTitle("");
/*  211:     */       }
/*  212: 306 */       super.dispose();
/*  213:     */     }
/*  214:     */   }
/*  215:     */   
/*  216: 315 */   public static final Tag[] TAGS_GUI = { new Tag(0, "MDI", "MDI Layout"), new Tag(1, "SDI", "SDI Layout") };
/*  217:     */   protected Main m_Self;
/*  218: 322 */   protected int m_GUIType = 0;
/*  219:     */   protected static Main m_MainCommandline;
/*  220:     */   protected static Main m_MainSingleton;
/*  221: 337 */   protected static Vector<StartUpListener> m_StartupListeners = new Vector();
/*  222: 340 */   protected static Memory m_Memory = new Memory(true);
/*  223: 343 */   protected HashSet<Container> m_ChildFrames = new HashSet();
/*  224: 346 */   protected static LogWindow m_LogWindow = new LogWindow();
/*  225: 349 */   protected JFileChooser m_FileChooserTreeVisualizer = new JFileChooser(new File(System.getProperty("user.dir")));
/*  226: 353 */   protected JFileChooser m_FileChooserGraphVisualizer = new JFileChooser(new File(System.getProperty("user.dir")));
/*  227: 357 */   protected JFileChooser m_FileChooserPlot = new JFileChooser(new File(System.getProperty("user.dir")));
/*  228: 361 */   protected JFileChooser m_FileChooserROC = new JFileChooser(new File(System.getProperty("user.dir")));
/*  229:     */   private JMenu jMenuHelp;
/*  230:     */   private JMenu jMenuVisualization;
/*  231:     */   private JMenu jMenuTools;
/*  232:     */   private JDesktopPane jDesktopPane;
/*  233:     */   private JMenu jMenuApplications;
/*  234:     */   private JMenuItem jMenuItemHelpSystemInfo;
/*  235:     */   private JMenuItem jMenuItemHelpAbout;
/*  236:     */   private JMenuItem jMenuItemHelpHomepage;
/*  237:     */   private JMenuItem jMenuItemHelpWekaWiki;
/*  238:     */   private JMenuItem jMenuItemHelpSourceforge;
/*  239:     */   private JMenuItem jMenuItemVisualizationBoundaryVisualizer;
/*  240:     */   private JMenuItem jMenuItemVisualizationGraphVisualizer;
/*  241:     */   private JMenuItem jMenuItemVisualizationTreeVisualizer;
/*  242:     */   private JMenuItem jMenuItemVisualizationROC;
/*  243:     */   private JMenuItem jMenuItemVisualizationPlot;
/*  244:     */   private JMenuItem jMenuItemToolsSqlViewer;
/*  245:     */   private JMenuItem jMenuItemToolsGroovyConsole;
/*  246:     */   private JMenuItem jMenuItemToolsJythonConsole;
/*  247:     */   private JMenuItem jMenuItemToolsArffViewer;
/*  248:     */   private JMenuItem jMenuItemApplicationsSimpleCLI;
/*  249:     */   private JMenuItem jMenuItemApplicationsKnowledgeFlow;
/*  250:     */   private JMenuItem jMenuItemApplicationsExperimenter;
/*  251:     */   private JMenuItem jMenuItemApplicationsExplorer;
/*  252:     */   private JMenuItem jMenuItemProgramExit;
/*  253:     */   private JMenuItem jMenuItemProgramLogWindow;
/*  254:     */   private JMenuItem jMenuItemProgramMemoryUsage;
/*  255:     */   private JMenu jMenuProgram;
/*  256:     */   private JMenu jMenuExtensions;
/*  257:     */   private JMenu jMenuWindows;
/*  258:     */   private JMenuBar jMenuBar;
/*  259:     */   
/*  260:     */   protected Container createFrame(Main parent, String title, Component c, LayoutManager layout, Object layoutConstraints, int width, int height, JMenuBar menu, boolean listener, boolean visible)
/*  261:     */   {
/*  262: 423 */     Container result = null;
/*  263: 425 */     if (this.m_GUIType == 0)
/*  264:     */     {
/*  265: 426 */       final ChildFrameMDI frame = new ChildFrameMDI(parent, title);
/*  266:     */       
/*  267:     */ 
/*  268: 429 */       frame.setLayout(layout);
/*  269: 430 */       if (c != null) {
/*  270: 431 */         frame.getContentPane().add(c, layoutConstraints);
/*  271:     */       }
/*  272: 435 */       frame.setJMenuBar(menu);
/*  273:     */       
/*  274:     */ 
/*  275: 438 */       frame.pack();
/*  276: 439 */       if ((width > -1) && (height > -1)) {
/*  277: 440 */         frame.setSize(width, height);
/*  278:     */       }
/*  279: 442 */       frame.validate();
/*  280: 445 */       if (listener) {
/*  281: 446 */         frame.addInternalFrameListener(new InternalFrameAdapter()
/*  282:     */         {
/*  283:     */           public void internalFrameClosing(InternalFrameEvent e)
/*  284:     */           {
/*  285: 449 */             frame.dispose();
/*  286:     */           }
/*  287:     */         });
/*  288:     */       }
/*  289: 455 */       if (visible)
/*  290:     */       {
/*  291: 456 */         frame.setVisible(true);
/*  292:     */         try
/*  293:     */         {
/*  294: 458 */           frame.setSelected(true);
/*  295:     */         }
/*  296:     */         catch (Exception e)
/*  297:     */         {
/*  298: 460 */           e.printStackTrace();
/*  299:     */         }
/*  300:     */       }
/*  301: 464 */       result = frame;
/*  302:     */     }
/*  303: 465 */     else if (this.m_GUIType == 1)
/*  304:     */     {
/*  305: 466 */       final ChildFrameSDI frame = new ChildFrameSDI(parent, title);
/*  306:     */       
/*  307:     */ 
/*  308: 469 */       frame.setLayout(layout);
/*  309: 470 */       if (c != null) {
/*  310: 471 */         frame.getContentPane().add(c, layoutConstraints);
/*  311:     */       }
/*  312: 475 */       frame.setJMenuBar(menu);
/*  313:     */       
/*  314:     */ 
/*  315: 478 */       frame.pack();
/*  316: 479 */       if ((width > -1) && (height > -1)) {
/*  317: 480 */         frame.setSize(width, height);
/*  318:     */       }
/*  319: 482 */       frame.validate();
/*  320:     */       
/*  321:     */ 
/*  322: 485 */       int screenHeight = getGraphicsConfiguration().getBounds().height;
/*  323: 486 */       int screenWidth = getGraphicsConfiguration().getBounds().width;
/*  324: 487 */       frame.setLocation((screenWidth - frame.getBounds().width) / 2, (screenHeight - frame.getBounds().height) / 2);
/*  325: 491 */       if (listener) {
/*  326: 492 */         frame.addWindowListener(new WindowAdapter()
/*  327:     */         {
/*  328:     */           public void windowClosing(WindowEvent e)
/*  329:     */           {
/*  330: 495 */             frame.dispose();
/*  331:     */           }
/*  332:     */         });
/*  333:     */       }
/*  334: 501 */       if (visible) {
/*  335: 502 */         frame.setVisible(true);
/*  336:     */       }
/*  337: 505 */       result = frame;
/*  338:     */     }
/*  339: 508 */     return result;
/*  340:     */   }
/*  341:     */   
/*  342:     */   protected void insertMenuItem(JMenu menu, JMenuItem menuitem)
/*  343:     */   {
/*  344: 518 */     insertMenuItem(menu, menuitem, 0);
/*  345:     */   }
/*  346:     */   
/*  347:     */   protected void insertMenuItem(JMenu menu, JMenuItem menuitem, int startIndex)
/*  348:     */   {
/*  349: 535 */     boolean inserted = false;
/*  350: 536 */     String newStr = menuitem.getText().toLowerCase();
/*  351: 539 */     for (int i = startIndex; i < menu.getMenuComponentCount(); i++) {
/*  352: 540 */       if ((menu.getMenuComponent(i) instanceof JMenuItem))
/*  353:     */       {
/*  354: 544 */         JMenuItem current = (JMenuItem)menu.getMenuComponent(i);
/*  355: 545 */         String currentStr = current.getText().toLowerCase();
/*  356: 546 */         if (currentStr.compareTo(newStr) > 0)
/*  357:     */         {
/*  358: 547 */           inserted = true;
/*  359: 548 */           menu.insert(menuitem, i);
/*  360: 549 */           break;
/*  361:     */         }
/*  362:     */       }
/*  363:     */     }
/*  364: 554 */     if (!inserted) {
/*  365: 555 */       menu.add(menuitem);
/*  366:     */     }
/*  367:     */   }
/*  368:     */   
/*  369:     */   protected void initGUI()
/*  370:     */   {
/*  371: 563 */     this.m_Self = this;
/*  372:     */     try
/*  373:     */     {
/*  374: 567 */       createTitle("");
/*  375: 568 */       setDefaultCloseOperation(3);
/*  376: 569 */       setIconImage(new ImageIcon(getClass().getClassLoader().getResource("weka/gui/weka_icon_new_48.png")).getImage());
/*  377:     */       
/*  378:     */ 
/*  379:     */ 
/*  380: 573 */       this.m_FileChooserGraphVisualizer.addChoosableFileFilter(new ExtensionFileFilter(".bif", "BIF Files (*.bif)"));
/*  381:     */       
/*  382:     */ 
/*  383: 576 */       this.m_FileChooserGraphVisualizer.addChoosableFileFilter(new ExtensionFileFilter(".xml", "XML Files (*.xml)"));
/*  384:     */       
/*  385:     */ 
/*  386:     */ 
/*  387: 580 */       this.m_FileChooserPlot.addChoosableFileFilter(new ExtensionFileFilter(".arff", "ARFF Files (*.arff)"));
/*  388:     */       
/*  389:     */ 
/*  390: 583 */       this.m_FileChooserPlot.setMultiSelectionEnabled(true);
/*  391:     */       
/*  392: 585 */       this.m_FileChooserROC.addChoosableFileFilter(new ExtensionFileFilter(".arff", "ARFF Files (*.arff)"));
/*  393: 590 */       if (this.m_GUIType == 0)
/*  394:     */       {
/*  395: 591 */         this.jDesktopPane = new BackgroundDesktopPane("weka/gui/images/weka_background.gif");
/*  396:     */         
/*  397: 593 */         this.jDesktopPane.setDragMode(1);
/*  398: 594 */         setContentPane(this.jDesktopPane);
/*  399:     */       }
/*  400:     */       else
/*  401:     */       {
/*  402: 596 */         this.jDesktopPane = null;
/*  403:     */       }
/*  404: 600 */       this.jMenuBar = new JMenuBar();
/*  405: 601 */       setJMenuBar(this.jMenuBar);
/*  406:     */       
/*  407:     */ 
/*  408: 604 */       this.jMenuProgram = new JMenu();
/*  409: 605 */       this.jMenuBar.add(this.jMenuProgram);
/*  410: 606 */       this.jMenuProgram.setText("Program");
/*  411: 607 */       this.jMenuProgram.setMnemonic('P');
/*  412:     */       
/*  413:     */ 
/*  414:     */ 
/*  415:     */ 
/*  416:     */ 
/*  417:     */ 
/*  418:     */ 
/*  419:     */ 
/*  420:     */ 
/*  421:     */ 
/*  422:     */ 
/*  423:     */ 
/*  424:     */ 
/*  425:     */ 
/*  426:     */ 
/*  427:     */ 
/*  428: 624 */       this.jMenuItemProgramLogWindow = new JMenuItem();
/*  429: 625 */       this.jMenuProgram.add(this.jMenuItemProgramLogWindow);
/*  430: 626 */       this.jMenuItemProgramLogWindow.setText("LogWindow");
/*  431: 627 */       this.jMenuItemProgramLogWindow.setMnemonic('L');
/*  432: 628 */       this.jMenuItemProgramLogWindow.addActionListener(new ActionListener()
/*  433:     */       {
/*  434:     */         public void actionPerformed(ActionEvent evt)
/*  435:     */         {
/*  436: 631 */           Main.m_LogWindow.setVisible(true);
/*  437:     */         }
/*  438: 634 */       });
/*  439: 635 */       this.jMenuItemProgramMemoryUsage = new JMenuItem();
/*  440: 636 */       this.jMenuProgram.add(this.jMenuItemProgramMemoryUsage);
/*  441: 637 */       this.jMenuItemProgramMemoryUsage.setText("Memory usage");
/*  442: 638 */       this.jMenuItemProgramMemoryUsage.setMnemonic('M');
/*  443: 639 */       this.jMenuItemProgramMemoryUsage.addActionListener(new ActionListener()
/*  444:     */       {
/*  445:     */         public void actionPerformed(ActionEvent evt)
/*  446:     */         {
/*  447: 642 */           String title = Main.this.jMenuItemProgramMemoryUsage.getText();
/*  448: 643 */           if (!Main.this.containsWindow(title))
/*  449:     */           {
/*  450: 644 */             final MemoryUsagePanel panel = new MemoryUsagePanel();
/*  451: 645 */             Container c = Main.this.createFrame(Main.this.m_Self, title, panel, new BorderLayout(), "Center", 400, 50, null, true, true);
/*  452:     */             
/*  453:     */ 
/*  454:     */ 
/*  455: 649 */             Dimension size = c.getPreferredSize();
/*  456: 650 */             c.setSize(new Dimension((int)size.getWidth(), (int)size.getHeight()));
/*  457: 654 */             if (Main.this.m_GUIType == 0)
/*  458:     */             {
/*  459: 655 */               Main.ChildFrameMDI frame = (Main.ChildFrameMDI)c;
/*  460: 656 */               Point l = panel.getFrameLocation();
/*  461: 657 */               if ((l.x != -1) && (l.y != -1)) {
/*  462: 658 */                 frame.setLocation(l);
/*  463:     */               }
/*  464: 660 */               frame.addInternalFrameListener(new InternalFrameAdapter()
/*  465:     */               {
/*  466:     */                 public void internalFrameClosing(InternalFrameEvent e)
/*  467:     */                 {
/*  468: 663 */                   panel.stopMonitoring();
/*  469:     */                 }
/*  470:     */               });
/*  471:     */             }
/*  472:     */             else
/*  473:     */             {
/*  474: 667 */               Main.ChildFrameSDI frame = (Main.ChildFrameSDI)c;
/*  475: 668 */               Point l = panel.getFrameLocation();
/*  476: 669 */               if ((l.x != -1) && (l.y != -1)) {
/*  477: 670 */                 frame.setLocation(l);
/*  478:     */               }
/*  479: 672 */               frame.addWindowListener(new WindowAdapter()
/*  480:     */               {
/*  481:     */                 public void windowClosing(WindowEvent e)
/*  482:     */                 {
/*  483: 675 */                   panel.stopMonitoring();
/*  484:     */                 }
/*  485:     */               });
/*  486:     */             }
/*  487:     */           }
/*  488:     */           else
/*  489:     */           {
/*  490: 680 */             Main.this.showWindow(Main.this.getWindow(title));
/*  491:     */           }
/*  492:     */         }
/*  493: 684 */       });
/*  494: 685 */       this.jMenuProgram.add(new JSeparator());
/*  495:     */       
/*  496:     */ 
/*  497: 688 */       this.jMenuItemProgramExit = new JMenuItem();
/*  498: 689 */       this.jMenuProgram.add(this.jMenuItemProgramExit);
/*  499: 690 */       this.jMenuItemProgramExit.setText("Exit");
/*  500: 691 */       this.jMenuItemProgramExit.setMnemonic('E');
/*  501: 692 */       this.jMenuItemProgramExit.addActionListener(new ActionListener()
/*  502:     */       {
/*  503:     */         public void actionPerformed(ActionEvent evt)
/*  504:     */         {
/*  505: 696 */           Iterator<Container> iter = Main.this.getWindowList();
/*  506: 697 */           Vector<Container> list = new Vector();
/*  507: 698 */           while (iter.hasNext()) {
/*  508: 699 */             list.add(iter.next());
/*  509:     */           }
/*  510: 701 */           for (int i = 0; i < list.size(); i++)
/*  511:     */           {
/*  512: 702 */             Container c = (Container)list.get(i);
/*  513: 703 */             if ((c instanceof Main.ChildFrameMDI)) {
/*  514: 704 */               ((Main.ChildFrameMDI)c).dispose();
/*  515: 705 */             } else if ((c instanceof Main.ChildFrameSDI)) {
/*  516: 706 */               ((Main.ChildFrameSDI)c).dispose();
/*  517:     */             }
/*  518:     */           }
/*  519: 710 */           Main.m_LogWindow.dispose();
/*  520:     */           
/*  521: 712 */           Main.this.m_Self.dispose();
/*  522:     */           
/*  523: 714 */           System.exit(0);
/*  524:     */         }
/*  525: 718 */       });
/*  526: 719 */       this.jMenuApplications = new JMenu();
/*  527: 720 */       this.jMenuBar.add(this.jMenuApplications);
/*  528: 721 */       this.jMenuApplications.setText("Applications");
/*  529: 722 */       this.jMenuApplications.setMnemonic('A');
/*  530:     */       
/*  531:     */ 
/*  532: 725 */       this.jMenuItemApplicationsExplorer = new JMenuItem();
/*  533: 726 */       this.jMenuApplications.add(this.jMenuItemApplicationsExplorer);
/*  534: 727 */       this.jMenuItemApplicationsExplorer.setText("Explorer");
/*  535: 728 */       this.jMenuItemApplicationsExplorer.setMnemonic('E');
/*  536: 729 */       this.jMenuItemApplicationsExplorer.addActionListener(new ActionListener()
/*  537:     */       {
/*  538:     */         public void actionPerformed(ActionEvent evt)
/*  539:     */         {
/*  540: 732 */           String title = Main.this.jMenuItemApplicationsExplorer.getText();
/*  541: 733 */           if (!Main.this.containsWindow(title)) {
/*  542: 734 */             Main.this.createFrame(Main.this.m_Self, title, new Explorer(), new BorderLayout(), "Center", 800, 600, null, true, true);
/*  543:     */           } else {
/*  544: 737 */             Main.this.showWindow(Main.this.getWindow(title));
/*  545:     */           }
/*  546:     */         }
/*  547: 742 */       });
/*  548: 743 */       this.jMenuItemApplicationsExperimenter = new JMenuItem();
/*  549: 744 */       this.jMenuApplications.add(this.jMenuItemApplicationsExperimenter);
/*  550: 745 */       this.jMenuItemApplicationsExperimenter.setText("Experimenter");
/*  551: 746 */       this.jMenuItemApplicationsExperimenter.setMnemonic('X');
/*  552: 747 */       this.jMenuItemApplicationsExperimenter.addActionListener(new ActionListener()
/*  553:     */       {
/*  554:     */         public void actionPerformed(ActionEvent evt)
/*  555:     */         {
/*  556: 750 */           String title = Main.this.jMenuItemApplicationsExperimenter.getText();
/*  557: 751 */           if (!Main.this.containsWindow(title)) {
/*  558: 752 */             Main.this.createFrame(Main.this.m_Self, title, new Experimenter(false), new BorderLayout(), "Center", 800, 600, null, true, true);
/*  559:     */           } else {
/*  560: 756 */             Main.this.showWindow(Main.this.getWindow(title));
/*  561:     */           }
/*  562:     */         }
/*  563: 761 */       });
/*  564: 762 */       this.jMenuItemApplicationsKnowledgeFlow = new JMenuItem();
/*  565: 763 */       this.jMenuApplications.add(this.jMenuItemApplicationsKnowledgeFlow);
/*  566: 764 */       this.jMenuItemApplicationsKnowledgeFlow.setText("KnowledgeFlow");
/*  567: 765 */       this.jMenuItemApplicationsKnowledgeFlow.setMnemonic('K');
/*  568: 766 */       this.jMenuItemApplicationsKnowledgeFlow.addActionListener(new ActionListener()
/*  569:     */       {
/*  570:     */         public void actionPerformed(ActionEvent evt)
/*  571:     */         {
/*  572: 770 */           String title = Main.this.jMenuItemApplicationsKnowledgeFlow.getText();
/*  573: 771 */           if (!Main.this.containsWindow(title))
/*  574:     */           {
/*  575: 772 */             KnowledgeFlowApp.createSingleton(new String[0]);
/*  576: 773 */             Main.this.createFrame(Main.this.m_Self, title, KnowledgeFlowApp.getSingleton(), new BorderLayout(), "Center", 900, 600, null, true, true);
/*  577:     */           }
/*  578:     */           else
/*  579:     */           {
/*  580: 777 */             Main.this.showWindow(Main.this.getWindow(title));
/*  581:     */           }
/*  582:     */         }
/*  583: 782 */       });
/*  584: 783 */       this.jMenuItemApplicationsSimpleCLI = new JMenuItem();
/*  585: 784 */       this.jMenuApplications.add(this.jMenuItemApplicationsSimpleCLI);
/*  586: 785 */       this.jMenuItemApplicationsSimpleCLI.setText("SimpleCLI");
/*  587: 786 */       this.jMenuItemApplicationsSimpleCLI.setMnemonic('S');
/*  588: 787 */       this.jMenuItemApplicationsSimpleCLI.addActionListener(new ActionListener()
/*  589:     */       {
/*  590:     */         public void actionPerformed(ActionEvent evt)
/*  591:     */         {
/*  592: 790 */           String title = Main.this.jMenuItemApplicationsSimpleCLI.getText();
/*  593: 791 */           if (!Main.this.containsWindow(title)) {
/*  594:     */             try
/*  595:     */             {
/*  596: 793 */               Main.this.createFrame(Main.this.m_Self, title, new SimpleCLIPanel(), new BorderLayout(), "Center", 600, 500, null, true, true);
/*  597:     */             }
/*  598:     */             catch (Exception e)
/*  599:     */             {
/*  600: 797 */               e.printStackTrace();
/*  601: 798 */               JOptionPane.showMessageDialog(Main.this.m_Self, "Error instantiating SimpleCLI:\n" + e.getMessage());
/*  602:     */               
/*  603: 800 */               return;
/*  604:     */             }
/*  605:     */           } else {
/*  606: 803 */             Main.this.showWindow(Main.this.getWindow(title));
/*  607:     */           }
/*  608:     */         }
/*  609: 808 */       });
/*  610: 809 */       this.jMenuTools = new JMenu();
/*  611: 810 */       this.jMenuBar.add(this.jMenuTools);
/*  612: 811 */       this.jMenuTools.setText("Tools");
/*  613: 812 */       this.jMenuTools.setMnemonic('T');
/*  614:     */       
/*  615:     */ 
/*  616: 815 */       this.jMenuItemToolsArffViewer = new JMenuItem();
/*  617: 816 */       this.jMenuTools.add(this.jMenuItemToolsArffViewer);
/*  618: 817 */       this.jMenuItemToolsArffViewer.setText("ArffViewer");
/*  619: 818 */       this.jMenuItemToolsArffViewer.setMnemonic('A');
/*  620: 819 */       this.jMenuItemToolsArffViewer.addActionListener(new ActionListener()
/*  621:     */       {
/*  622:     */         public void actionPerformed(ActionEvent evt)
/*  623:     */         {
/*  624: 822 */           String title = Main.this.jMenuItemToolsArffViewer.getText();
/*  625: 823 */           if (!Main.this.containsWindow(title))
/*  626:     */           {
/*  627: 824 */             ArffViewerMainPanel panel = new ArffViewerMainPanel(null);
/*  628: 825 */             panel.setConfirmExit(false);
/*  629: 826 */             Container frame = Main.this.createFrame(Main.this.m_Self, title, panel, new BorderLayout(), "Center", 800, 600, panel.getMenu(), true, true);
/*  630:     */             
/*  631:     */ 
/*  632: 829 */             panel.setParent(frame);
/*  633:     */           }
/*  634:     */           else
/*  635:     */           {
/*  636: 831 */             Main.this.showWindow(Main.this.getWindow(title));
/*  637:     */           }
/*  638:     */         }
/*  639: 836 */       });
/*  640: 837 */       this.jMenuItemToolsSqlViewer = new JMenuItem();
/*  641: 838 */       this.jMenuTools.add(this.jMenuItemToolsSqlViewer);
/*  642: 839 */       this.jMenuItemToolsSqlViewer.setText("SqlViewer");
/*  643: 840 */       this.jMenuItemToolsSqlViewer.setMnemonic('S');
/*  644: 841 */       this.jMenuItemToolsSqlViewer.addActionListener(new ActionListener()
/*  645:     */       {
/*  646:     */         public void actionPerformed(ActionEvent evt)
/*  647:     */         {
/*  648: 844 */           String title = Main.this.jMenuItemToolsSqlViewer.getText();
/*  649: 845 */           if (!Main.this.containsWindow(title))
/*  650:     */           {
/*  651: 846 */             final SqlViewer sql = new SqlViewer(null);
/*  652: 847 */             final Container frame = Main.this.createFrame(Main.this.m_Self, title, sql, new BorderLayout(), "Center", -1, -1, null, false, true);
/*  653: 852 */             if ((frame instanceof Main.ChildFrameMDI)) {
/*  654: 853 */               ((Main.ChildFrameMDI)frame).addInternalFrameListener(new InternalFrameAdapter()
/*  655:     */               {
/*  656:     */                 public void internalFrameClosing(InternalFrameEvent e)
/*  657:     */                 {
/*  658: 857 */                   sql.saveSize();
/*  659: 858 */                   ((Main.ChildFrameMDI)frame).dispose();
/*  660:     */                 }
/*  661:     */               });
/*  662: 861 */             } else if ((frame instanceof Main.ChildFrameSDI)) {
/*  663: 862 */               ((Main.ChildFrameSDI)frame).addWindowListener(new WindowAdapter()
/*  664:     */               {
/*  665:     */                 public void windowClosing(WindowEvent e)
/*  666:     */                 {
/*  667: 865 */                   sql.saveSize();
/*  668: 866 */                   ((Main.ChildFrameSDI)frame).dispose();
/*  669:     */                 }
/*  670:     */               });
/*  671:     */             }
/*  672:     */           }
/*  673:     */           else
/*  674:     */           {
/*  675: 871 */             Main.this.showWindow(Main.this.getWindow(title));
/*  676:     */           }
/*  677:     */         }
/*  678: 877 */       });
/*  679: 878 */       final JMenuItem jMenuItemBayesNet = new JMenuItem();
/*  680: 879 */       this.jMenuTools.add(jMenuItemBayesNet);
/*  681: 880 */       jMenuItemBayesNet.setText("Bayes net editor");
/*  682: 881 */       jMenuItemBayesNet.setMnemonic('N');
/*  683:     */       
/*  684: 883 */       jMenuItemBayesNet.addActionListener(new ActionListener()
/*  685:     */       {
/*  686:     */         public void actionPerformed(ActionEvent e)
/*  687:     */         {
/*  688: 886 */           String title = jMenuItemBayesNet.getText();
/*  689: 888 */           if (!Main.this.containsWindow(title))
/*  690:     */           {
/*  691: 889 */             GUI bayesNetGUI = new GUI();
/*  692: 890 */             Main.this.createFrame(Main.this.m_Self, title, bayesNetGUI, new BorderLayout(), "Center", 800, 600, bayesNetGUI.getMenuBar(), false, true);
/*  693:     */           }
/*  694:     */           else
/*  695:     */           {
/*  696: 894 */             Main.this.showWindow(Main.this.getWindow(title));
/*  697:     */           }
/*  698:     */         }
/*  699:     */       });
/*  700: 900 */       if (Groovy.isPresent())
/*  701:     */       {
/*  702: 901 */         this.jMenuItemToolsGroovyConsole = new JMenuItem();
/*  703: 902 */         this.jMenuTools.add(this.jMenuItemToolsGroovyConsole);
/*  704: 903 */         this.jMenuItemToolsGroovyConsole.setText("Groovy console");
/*  705: 904 */         this.jMenuItemToolsGroovyConsole.setMnemonic('G');
/*  706: 905 */         this.jMenuItemToolsGroovyConsole.addActionListener(new ActionListener()
/*  707:     */         {
/*  708:     */           public void actionPerformed(ActionEvent evt)
/*  709:     */           {
/*  710: 908 */             String title = Main.this.jMenuItemToolsGroovyConsole.getText();
/*  711: 909 */             if (!Main.this.containsWindow(title))
/*  712:     */             {
/*  713: 910 */               GroovyPanel panel = new GroovyPanel();
/*  714: 911 */               final Container frame = Main.this.createFrame(Main.this.m_Self, title, panel, new BorderLayout(), "Center", 800, 600, panel.getMenuBar(), false, true);
/*  715: 916 */               if ((frame instanceof Main.ChildFrameMDI)) {
/*  716: 917 */                 ((Main.ChildFrameMDI)frame).addInternalFrameListener(new InternalFrameAdapter()
/*  717:     */                 {
/*  718:     */                   public void internalFrameClosing(InternalFrameEvent e)
/*  719:     */                   {
/*  720: 921 */                     ((Main.ChildFrameMDI)frame).dispose();
/*  721:     */                   }
/*  722:     */                 });
/*  723: 924 */               } else if ((frame instanceof Main.ChildFrameSDI)) {
/*  724: 925 */                 ((Main.ChildFrameSDI)frame).addWindowListener(new WindowAdapter()
/*  725:     */                 {
/*  726:     */                   public void windowClosing(WindowEvent e)
/*  727:     */                   {
/*  728: 928 */                     ((Main.ChildFrameSDI)frame).dispose();
/*  729:     */                   }
/*  730:     */                 });
/*  731:     */               }
/*  732:     */             }
/*  733:     */             else
/*  734:     */             {
/*  735: 933 */               Main.this.showWindow(Main.this.getWindow(title));
/*  736:     */             }
/*  737:     */           }
/*  738:     */         });
/*  739:     */       }
/*  740: 940 */       if (Jython.isPresent())
/*  741:     */       {
/*  742: 941 */         this.jMenuItemToolsJythonConsole = new JMenuItem();
/*  743: 942 */         this.jMenuTools.add(this.jMenuItemToolsJythonConsole);
/*  744: 943 */         this.jMenuItemToolsJythonConsole.setText("Jython console");
/*  745: 944 */         this.jMenuItemToolsJythonConsole.setMnemonic('J');
/*  746: 945 */         this.jMenuItemToolsJythonConsole.addActionListener(new ActionListener()
/*  747:     */         {
/*  748:     */           public void actionPerformed(ActionEvent evt)
/*  749:     */           {
/*  750: 948 */             String title = Main.this.jMenuItemToolsJythonConsole.getText();
/*  751: 949 */             if (!Main.this.containsWindow(title))
/*  752:     */             {
/*  753: 950 */               JythonPanel panel = new JythonPanel();
/*  754: 951 */               final Container frame = Main.this.createFrame(Main.this.m_Self, title, panel, new BorderLayout(), "Center", 800, 600, panel.getMenuBar(), false, true);
/*  755: 956 */               if ((frame instanceof Main.ChildFrameMDI)) {
/*  756: 957 */                 ((Main.ChildFrameMDI)frame).addInternalFrameListener(new InternalFrameAdapter()
/*  757:     */                 {
/*  758:     */                   public void internalFrameClosing(InternalFrameEvent e)
/*  759:     */                   {
/*  760: 961 */                     ((Main.ChildFrameMDI)frame).dispose();
/*  761:     */                   }
/*  762:     */                 });
/*  763: 964 */               } else if ((frame instanceof Main.ChildFrameSDI)) {
/*  764: 965 */                 ((Main.ChildFrameSDI)frame).addWindowListener(new WindowAdapter()
/*  765:     */                 {
/*  766:     */                   public void windowClosing(WindowEvent e)
/*  767:     */                   {
/*  768: 968 */                     ((Main.ChildFrameSDI)frame).dispose();
/*  769:     */                   }
/*  770:     */                 });
/*  771:     */               }
/*  772:     */             }
/*  773:     */             else
/*  774:     */             {
/*  775: 973 */               Main.this.showWindow(Main.this.getWindow(title));
/*  776:     */             }
/*  777:     */           }
/*  778:     */         });
/*  779:     */       }
/*  780: 998 */       this.jMenuVisualization = new JMenu();
/*  781: 999 */       this.jMenuBar.add(this.jMenuVisualization);
/*  782:1000 */       this.jMenuVisualization.setText("Visualization");
/*  783:1001 */       this.jMenuVisualization.setMnemonic('V');
/*  784:     */       
/*  785:     */ 
/*  786:1004 */       this.jMenuItemVisualizationPlot = new JMenuItem();
/*  787:1005 */       this.jMenuVisualization.add(this.jMenuItemVisualizationPlot);
/*  788:1006 */       this.jMenuItemVisualizationPlot.setText("Plot");
/*  789:1007 */       this.jMenuItemVisualizationPlot.setMnemonic('P');
/*  790:1008 */       this.jMenuItemVisualizationPlot.addActionListener(new ActionListener()
/*  791:     */       {
/*  792:     */         public void actionPerformed(ActionEvent evt)
/*  793:     */         {
/*  794:1012 */           int retVal = Main.this.m_FileChooserPlot.showOpenDialog(Main.this.m_Self);
/*  795:1013 */           if (retVal != 0) {
/*  796:1014 */             return;
/*  797:     */           }
/*  798:1018 */           VisualizePanel panel = new VisualizePanel();
/*  799:1019 */           String filenames = "";
/*  800:1020 */           File[] files = Main.this.m_FileChooserPlot.getSelectedFiles();
/*  801:1021 */           for (int j = 0; j < files.length; j++)
/*  802:     */           {
/*  803:1022 */             String filename = files[j].getAbsolutePath();
/*  804:1023 */             if (j > 0) {
/*  805:1024 */               filenames = filenames + ", ";
/*  806:     */             }
/*  807:1026 */             filenames = filenames + filename;
/*  808:1027 */             System.err.println("Loading instances from " + filename);
/*  809:     */             try
/*  810:     */             {
/*  811:1029 */               Reader r = new BufferedReader(new FileReader(filename));
/*  812:1030 */               Instances i = new Instances(r);
/*  813:1031 */               i.setClassIndex(i.numAttributes() - 1);
/*  814:1032 */               PlotData2D pd1 = new PlotData2D(i);
/*  815:1034 */               if (j == 0)
/*  816:     */               {
/*  817:1035 */                 pd1.setPlotName("Master plot");
/*  818:1036 */                 panel.setMasterPlot(pd1);
/*  819:     */               }
/*  820:     */               else
/*  821:     */               {
/*  822:1038 */                 pd1.setPlotName("Plot " + (j + 1));
/*  823:1039 */                 pd1.m_useCustomColour = true;
/*  824:1040 */                 pd1.m_customColour = (j % 2 == 0 ? Color.red : Color.blue);
/*  825:1041 */                 panel.addPlot(pd1);
/*  826:     */               }
/*  827:     */             }
/*  828:     */             catch (Exception e)
/*  829:     */             {
/*  830:1044 */               e.printStackTrace();
/*  831:1045 */               JOptionPane.showMessageDialog(Main.this.m_Self, "Error loading file '" + files[j] + "':\n" + e.getMessage());
/*  832:     */               
/*  833:1047 */               return;
/*  834:     */             }
/*  835:     */           }
/*  836:1052 */           Main.this.createFrame(Main.this.m_Self, Main.this.jMenuItemVisualizationPlot.getText() + " - " + filenames, panel, new BorderLayout(), "Center", 800, 600, null, true, true);
/*  837:     */         }
/*  838:1060 */       });
/*  839:1061 */       this.jMenuItemVisualizationROC = new JMenuItem();
/*  840:1062 */       this.jMenuVisualization.add(this.jMenuItemVisualizationROC);
/*  841:1063 */       this.jMenuItemVisualizationROC.setText("ROC");
/*  842:1064 */       this.jMenuItemVisualizationROC.setMnemonic('R');
/*  843:1065 */       this.jMenuItemVisualizationROC.addActionListener(new ActionListener()
/*  844:     */       {
/*  845:     */         public void actionPerformed(ActionEvent evt)
/*  846:     */         {
/*  847:1069 */           int retVal = Main.this.m_FileChooserROC.showOpenDialog(Main.this.m_Self);
/*  848:1070 */           if (retVal != 0) {
/*  849:1071 */             return;
/*  850:     */           }
/*  851:1075 */           String filename = Main.this.m_FileChooserROC.getSelectedFile().getAbsolutePath();
/*  852:     */           
/*  853:1077 */           Instances result = null;
/*  854:     */           try
/*  855:     */           {
/*  856:1079 */             result = new Instances(new BufferedReader(new FileReader(filename)));
/*  857:     */           }
/*  858:     */           catch (Exception e)
/*  859:     */           {
/*  860:1081 */             e.printStackTrace();
/*  861:1082 */             JOptionPane.showMessageDialog(Main.this.m_Self, "Error loading file '" + filename + "':\n" + e.getMessage());
/*  862:     */             
/*  863:1084 */             return;
/*  864:     */           }
/*  865:1086 */           result.setClassIndex(result.numAttributes() - 1);
/*  866:1087 */           ThresholdVisualizePanel vmc = new ThresholdVisualizePanel();
/*  867:1088 */           vmc.setROCString("(Area under ROC = " + Utils.doubleToString(ThresholdCurve.getROCArea(result), 4) + ")");
/*  868:     */           
/*  869:1090 */           vmc.setName(result.relationName());
/*  870:1091 */           PlotData2D tempd = new PlotData2D(result);
/*  871:1092 */           tempd.setPlotName(result.relationName());
/*  872:1093 */           tempd.addInstanceNumberAttribute();
/*  873:     */           try
/*  874:     */           {
/*  875:1095 */             vmc.addPlot(tempd);
/*  876:     */           }
/*  877:     */           catch (Exception e)
/*  878:     */           {
/*  879:1097 */             e.printStackTrace();
/*  880:1098 */             JOptionPane.showMessageDialog(Main.this.m_Self, "Error adding plot:\n" + e.getMessage());
/*  881:     */             
/*  882:1100 */             return;
/*  883:     */           }
/*  884:1103 */           Main.this.createFrame(Main.this.m_Self, Main.this.jMenuItemVisualizationROC.getText() + " - " + filename, vmc, new BorderLayout(), "Center", 800, 600, null, true, true);
/*  885:     */         }
/*  886:1109 */       });
/*  887:1110 */       this.jMenuItemVisualizationTreeVisualizer = new JMenuItem();
/*  888:1111 */       this.jMenuVisualization.add(this.jMenuItemVisualizationTreeVisualizer);
/*  889:1112 */       this.jMenuItemVisualizationTreeVisualizer.setText("TreeVisualizer");
/*  890:1113 */       this.jMenuItemVisualizationTreeVisualizer.setMnemonic('T');
/*  891:1114 */       this.jMenuItemVisualizationTreeVisualizer.addActionListener(new ActionListener()
/*  892:     */       {
/*  893:     */         public void actionPerformed(ActionEvent evt)
/*  894:     */         {
/*  895:1119 */           int retVal = Main.this.m_FileChooserTreeVisualizer.showOpenDialog(Main.this.m_Self);
/*  896:1120 */           if (retVal != 0) {
/*  897:1121 */             return;
/*  898:     */           }
/*  899:1125 */           String filename = Main.this.m_FileChooserTreeVisualizer.getSelectedFile().getAbsolutePath();
/*  900:     */           
/*  901:1127 */           TreeBuild builder = new TreeBuild();
/*  902:1128 */           Node top = null;
/*  903:1129 */           NodePlace arrange = new PlaceNode2();
/*  904:     */           try
/*  905:     */           {
/*  906:1131 */             top = builder.create(new FileReader(filename));
/*  907:     */           }
/*  908:     */           catch (Exception e)
/*  909:     */           {
/*  910:1133 */             e.printStackTrace();
/*  911:1134 */             JOptionPane.showMessageDialog(Main.this.m_Self, "Error loading file '" + filename + "':\n" + e.getMessage());
/*  912:     */             
/*  913:1136 */             return;
/*  914:     */           }
/*  915:1140 */           Main.this.createFrame(Main.this.m_Self, Main.this.jMenuItemVisualizationTreeVisualizer.getText() + " - " + filename, new TreeVisualizer(null, top, arrange), new BorderLayout(), "Center", 800, 600, null, true, true);
/*  916:     */         }
/*  917:1147 */       });
/*  918:1148 */       this.jMenuItemVisualizationGraphVisualizer = new JMenuItem();
/*  919:1149 */       this.jMenuVisualization.add(this.jMenuItemVisualizationGraphVisualizer);
/*  920:1150 */       this.jMenuItemVisualizationGraphVisualizer.setText("GraphVisualizer");
/*  921:1151 */       this.jMenuItemVisualizationGraphVisualizer.setMnemonic('G');
/*  922:1152 */       this.jMenuItemVisualizationGraphVisualizer.addActionListener(new ActionListener()
/*  923:     */       {
/*  924:     */         public void actionPerformed(ActionEvent evt)
/*  925:     */         {
/*  926:1157 */           int retVal = Main.this.m_FileChooserGraphVisualizer.showOpenDialog(Main.this.m_Self);
/*  927:1158 */           if (retVal != 0) {
/*  928:1159 */             return;
/*  929:     */           }
/*  930:1163 */           String filename = Main.this.m_FileChooserGraphVisualizer.getSelectedFile().getAbsolutePath();
/*  931:     */           
/*  932:1165 */           GraphVisualizer panel = new GraphVisualizer();
/*  933:     */           try
/*  934:     */           {
/*  935:1167 */             if ((filename.toLowerCase().endsWith(".xml")) || (filename.toLowerCase().endsWith(".bif"))) {
/*  936:1169 */               panel.readBIF(new FileInputStream(filename));
/*  937:     */             } else {
/*  938:1171 */               panel.readDOT(new FileReader(filename));
/*  939:     */             }
/*  940:     */           }
/*  941:     */           catch (Exception e)
/*  942:     */           {
/*  943:1174 */             e.printStackTrace();
/*  944:1175 */             JOptionPane.showMessageDialog(Main.this.m_Self, "Error loading file '" + filename + "':\n" + e.getMessage());
/*  945:     */             
/*  946:1177 */             return;
/*  947:     */           }
/*  948:1181 */           Main.this.createFrame(Main.this.m_Self, Main.this.jMenuItemVisualizationGraphVisualizer.getText() + " - " + filename, panel, new BorderLayout(), "Center", 800, 600, null, true, true);
/*  949:     */         }
/*  950:1187 */       });
/*  951:1188 */       this.jMenuItemVisualizationBoundaryVisualizer = new JMenuItem();
/*  952:1189 */       this.jMenuVisualization.add(this.jMenuItemVisualizationBoundaryVisualizer);
/*  953:1190 */       this.jMenuItemVisualizationBoundaryVisualizer.setText("BoundaryVisualizer");
/*  954:1191 */       this.jMenuItemVisualizationBoundaryVisualizer.setMnemonic('B');
/*  955:1192 */       this.jMenuItemVisualizationBoundaryVisualizer.addActionListener(new ActionListener()
/*  956:     */       {
/*  957:     */         public void actionPerformed(ActionEvent evt)
/*  958:     */         {
/*  959:1196 */           String title = Main.this.jMenuItemVisualizationBoundaryVisualizer.getText();
/*  960:1197 */           if (!Main.this.containsWindow(title))
/*  961:     */           {
/*  962:1198 */             Main.this.createFrame(Main.this.m_Self, title, new BoundaryVisualizer(), new BorderLayout(), "Center", 800, 600, null, true, true);
/*  963:     */             
/*  964:     */ 
/*  965:     */ 
/*  966:1202 */             BoundaryVisualizer.setExitIfNoWindowsOpen(false);
/*  967:     */           }
/*  968:     */           else
/*  969:     */           {
/*  970:1204 */             Main.this.showWindow(Main.this.getWindow(title));
/*  971:     */           }
/*  972:     */         }
/*  973:1209 */       });
/*  974:1210 */       this.jMenuExtensions = new JMenu("Extensions");
/*  975:1211 */       this.jMenuExtensions.setMnemonic(69);
/*  976:1212 */       this.jMenuBar.add(this.jMenuExtensions);
/*  977:1213 */       this.jMenuExtensions.setVisible(false);
/*  978:     */       
/*  979:1215 */       String extensions = GenericObjectEditor.EDITOR_PROPERTIES.getProperty(MainMenuExtension.class.getName(), "");
/*  980:1218 */       if (extensions.length() > 0)
/*  981:     */       {
/*  982:1219 */         this.jMenuExtensions.setVisible(true);
/*  983:1220 */         String[] classnames = GenericObjectEditor.EDITOR_PROPERTIES.getProperty(MainMenuExtension.class.getName(), "").split(",");
/*  984:     */         
/*  985:1222 */         Hashtable<String, JMenu> submenus = new Hashtable();
/*  986:1225 */         for (String classname : classnames) {
/*  987:     */           try
/*  988:     */           {
/*  989:1227 */             MainMenuExtension ext = (MainMenuExtension)Class.forName(classname).newInstance();
/*  990:     */             
/*  991:     */ 
/*  992:     */ 
/*  993:1231 */             JMenu submenu = null;
/*  994:1232 */             if (ext.getSubmenuTitle() != null)
/*  995:     */             {
/*  996:1233 */               submenu = (JMenu)submenus.get(ext.getSubmenuTitle());
/*  997:1234 */               if (submenu == null)
/*  998:     */               {
/*  999:1235 */                 submenu = new JMenu(ext.getSubmenuTitle());
/* 1000:1236 */                 submenus.put(ext.getSubmenuTitle(), submenu);
/* 1001:1237 */                 insertMenuItem(this.jMenuExtensions, submenu);
/* 1002:     */               }
/* 1003:     */             }
/* 1004:1242 */             JMenuItem menuitem = new JMenuItem();
/* 1005:1243 */             menuitem.setText(ext.getMenuTitle());
/* 1006:     */             
/* 1007:     */ 
/* 1008:1246 */             ActionListener listener = ext.getActionListener(this.m_Self);
/* 1009:1247 */             if (listener != null)
/* 1010:     */             {
/* 1011:1248 */               menuitem.addActionListener(listener);
/* 1012:     */             }
/* 1013:     */             else
/* 1014:     */             {
/* 1015:1250 */               final JMenuItem finalMenuitem = menuitem;
/* 1016:1251 */               final MainMenuExtension finalExt = ext;
/* 1017:1252 */               menuitem.addActionListener(new ActionListener()
/* 1018:     */               {
/* 1019:     */                 public void actionPerformed(ActionEvent e)
/* 1020:     */                 {
/* 1021:1255 */                   Component frame = Main.this.createFrame(Main.this.m_Self, finalMenuitem.getText(), null, null, null, -1, -1, null, false, false);
/* 1022:     */                   
/* 1023:     */ 
/* 1024:1258 */                   finalExt.fillFrame(frame);
/* 1025:1259 */                   frame.setVisible(true);
/* 1026:     */                 }
/* 1027:     */               });
/* 1028:     */             }
/* 1029:1265 */             if (submenu != null) {
/* 1030:1266 */               insertMenuItem(submenu, menuitem);
/* 1031:     */             } else {
/* 1032:1268 */               insertMenuItem(this.jMenuExtensions, menuitem);
/* 1033:     */             }
/* 1034:     */           }
/* 1035:     */           catch (Exception e)
/* 1036:     */           {
/* 1037:1271 */             e.printStackTrace();
/* 1038:     */           }
/* 1039:     */         }
/* 1040:     */       }
/* 1041:1277 */       this.jMenuWindows = new JMenu("Windows");
/* 1042:1278 */       this.jMenuWindows.setMnemonic(87);
/* 1043:1279 */       this.jMenuBar.add(this.jMenuWindows);
/* 1044:1280 */       this.jMenuWindows.setVisible(false);
/* 1045:     */       
/* 1046:     */ 
/* 1047:1283 */       this.jMenuHelp = new JMenu();
/* 1048:1284 */       this.jMenuBar.add(this.jMenuHelp);
/* 1049:1285 */       this.jMenuHelp.setText("Help");
/* 1050:1286 */       this.jMenuHelp.setMnemonic('H');
/* 1051:     */       
/* 1052:     */ 
/* 1053:1289 */       this.jMenuItemHelpHomepage = new JMenuItem();
/* 1054:1290 */       this.jMenuHelp.add(this.jMenuItemHelpHomepage);
/* 1055:1291 */       this.jMenuItemHelpHomepage.setText("Weka homepage");
/* 1056:1292 */       this.jMenuItemHelpHomepage.setMnemonic('H');
/* 1057:1293 */       this.jMenuItemHelpHomepage.addActionListener(new ActionListener()
/* 1058:     */       {
/* 1059:     */         public void actionPerformed(ActionEvent evt)
/* 1060:     */         {
/* 1061:1296 */           BrowserHelper.openURL(Main.this.m_Self, "http://www.cs.waikato.ac.nz/~ml/weka/");
/* 1062:     */         }
/* 1063:1300 */       });
/* 1064:1301 */       this.jMenuHelp.add(new JSeparator());
/* 1065:     */       
/* 1066:     */ 
/* 1067:     */ 
/* 1068:     */ 
/* 1069:     */ 
/* 1070:     */ 
/* 1071:     */ 
/* 1072:     */ 
/* 1073:     */ 
/* 1074:     */ 
/* 1075:     */ 
/* 1076:     */ 
/* 1077:1314 */       this.jMenuItemHelpWekaWiki = new JMenuItem();
/* 1078:1315 */       this.jMenuHelp.add(this.jMenuItemHelpWekaWiki);
/* 1079:1316 */       this.jMenuItemHelpWekaWiki.setText("HOWTOs, code snippets, etc.");
/* 1080:1317 */       this.jMenuItemHelpWekaWiki.setMnemonic('W');
/* 1081:1318 */       this.jMenuItemHelpWekaWiki.addActionListener(new ActionListener()
/* 1082:     */       {
/* 1083:     */         public void actionPerformed(ActionEvent evt)
/* 1084:     */         {
/* 1085:1321 */           BrowserHelper.openURL(Main.this.m_Self, "http://weka.wikispaces.com/");
/* 1086:     */         }
/* 1087:1325 */       });
/* 1088:1326 */       this.jMenuItemHelpSourceforge = new JMenuItem();
/* 1089:1327 */       this.jMenuHelp.add(this.jMenuItemHelpSourceforge);
/* 1090:1328 */       this.jMenuItemHelpSourceforge.setText("Weka on SourceForge");
/* 1091:1329 */       this.jMenuItemHelpSourceforge.setMnemonic('F');
/* 1092:1330 */       this.jMenuItemHelpSourceforge.addActionListener(new ActionListener()
/* 1093:     */       {
/* 1094:     */         public void actionPerformed(ActionEvent evt)
/* 1095:     */         {
/* 1096:1333 */           BrowserHelper.openURL(Main.this.m_Self, "http://sourceforge.net/projects/weka/");
/* 1097:     */         }
/* 1098:1337 */       });
/* 1099:1338 */       this.jMenuHelp.add(new JSeparator());
/* 1100:     */       
/* 1101:     */ 
/* 1102:1341 */       this.jMenuItemHelpSystemInfo = new JMenuItem();
/* 1103:1342 */       this.jMenuHelp.add(this.jMenuItemHelpSystemInfo);
/* 1104:1343 */       this.jMenuItemHelpSystemInfo.setText("SystemInfo");
/* 1105:1344 */       this.jMenuItemHelpHomepage.setMnemonic('S');
/* 1106:1345 */       this.jMenuItemHelpSystemInfo.addActionListener(new ActionListener()
/* 1107:     */       {
/* 1108:     */         public void actionPerformed(ActionEvent evt)
/* 1109:     */         {
/* 1110:1348 */           String title = Main.this.jMenuItemHelpSystemInfo.getText();
/* 1111:1349 */           if (!Main.this.containsWindow(title))
/* 1112:     */           {
/* 1113:1351 */             Hashtable<String, String> info = new SystemInfo().getSystemInfo();
/* 1114:     */             
/* 1115:     */ 
/* 1116:1354 */             Vector<String> names = new Vector();
/* 1117:1355 */             Enumeration<String> enm = info.keys();
/* 1118:1356 */             while (enm.hasMoreElements()) {
/* 1119:1357 */               names.add(enm.nextElement());
/* 1120:     */             }
/* 1121:1359 */             Collections.sort(names);
/* 1122:     */             
/* 1123:     */ 
/* 1124:1362 */             String[][] data = new String[info.size()][2];
/* 1125:1363 */             for (int i = 0; i < names.size(); i++)
/* 1126:     */             {
/* 1127:1364 */               data[i][0] = ((String)names.get(i)).toString();
/* 1128:1365 */               data[i][1] = ((String)info.get(data[i][0])).toString();
/* 1129:     */             }
/* 1130:1367 */             String[] titles = { "Key", "Value" };
/* 1131:1368 */             JTable table = new JTable(data, titles);
/* 1132:     */             
/* 1133:1370 */             Main.this.createFrame(Main.this.m_Self, title, new JScrollPane(table), new BorderLayout(), "Center", 800, 600, null, true, true);
/* 1134:     */           }
/* 1135:     */           else
/* 1136:     */           {
/* 1137:1374 */             Main.this.showWindow(Main.this.getWindow(title));
/* 1138:     */           }
/* 1139:     */         }
/* 1140:1378 */       });
/* 1141:1379 */       this.jMenuHelp.add(new JSeparator());
/* 1142:     */       
/* 1143:     */ 
/* 1144:1382 */       this.jMenuItemHelpAbout = new JMenuItem();
/* 1145:1383 */       this.jMenuHelp.add(this.jMenuItemHelpAbout);
/* 1146:1384 */       this.jMenuItemHelpAbout.setText("About");
/* 1147:1385 */       this.jMenuItemHelpAbout.setMnemonic('A');
/* 1148:1386 */       this.jMenuItemHelpAbout.addActionListener(new ActionListener()
/* 1149:     */       {
/* 1150:     */         public void actionPerformed(ActionEvent evt)
/* 1151:     */         {
/* 1152:1389 */           String title = Main.this.jMenuItemHelpAbout.getText();
/* 1153:1390 */           if (!Main.this.containsWindow(title))
/* 1154:     */           {
/* 1155:1391 */             JPanel wekaPan = new JPanel();
/* 1156:1392 */             wekaPan.setToolTipText("Weka, a native bird of New Zealand");
/* 1157:1393 */             ImageIcon wii = new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("weka/gui/weka3.gif")));
/* 1158:     */             
/* 1159:1395 */             JLabel wekaLab = new JLabel(wii);
/* 1160:1396 */             wekaPan.add(wekaLab);
/* 1161:1397 */             Container frame = Main.this.createFrame(Main.this.m_Self, title, wekaPan, new BorderLayout(), "Center", -1, -1, null, true, true);
/* 1162:     */             
/* 1163:     */ 
/* 1164:1400 */             JPanel titlePan = new JPanel();
/* 1165:1401 */             titlePan.setLayout(new GridLayout(8, 1));
/* 1166:1402 */             titlePan.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
/* 1167:1403 */             titlePan.add(new JLabel("Waikato Environment for", 0));
/* 1168:     */             
/* 1169:1405 */             titlePan.add(new JLabel("Knowledge Analysis", 0));
/* 1170:     */             
/* 1171:1407 */             titlePan.add(new JLabel(""));
/* 1172:1408 */             titlePan.add(new JLabel("Version " + Version.VERSION, 0));
/* 1173:     */             
/* 1174:1410 */             titlePan.add(new JLabel(""));
/* 1175:1411 */             titlePan.add(new JLabel("(c) " + Copyright.getFromYear() + " - " + Copyright.getToYear(), 0));
/* 1176:     */             
/* 1177:1413 */             titlePan.add(new JLabel(Copyright.getOwner(), 0));
/* 1178:1414 */             titlePan.add(new JLabel(Copyright.getAddress(), 0));
/* 1179:1417 */             if ((frame instanceof Main.ChildFrameMDI))
/* 1180:     */             {
/* 1181:1418 */               ((Main.ChildFrameMDI)frame).getContentPane().add(titlePan, "North");
/* 1182:     */               
/* 1183:1420 */               ((Main.ChildFrameMDI)frame).pack();
/* 1184:     */             }
/* 1185:1421 */             else if ((frame instanceof Main.ChildFrameSDI))
/* 1186:     */             {
/* 1187:1422 */               ((Main.ChildFrameSDI)frame).getContentPane().add(titlePan, "North");
/* 1188:     */               
/* 1189:1424 */               ((Main.ChildFrameSDI)frame).pack();
/* 1190:     */             }
/* 1191:     */           }
/* 1192:     */           else
/* 1193:     */           {
/* 1194:1427 */             Main.this.showWindow(Main.this.getWindow(title));
/* 1195:     */           }
/* 1196:     */         }
/* 1197:1432 */       });
/* 1198:1433 */       int screenHeight = getGraphicsConfiguration().getBounds().height;
/* 1199:1434 */       int screenWidth = getGraphicsConfiguration().getBounds().width;
/* 1200:1435 */       if (this.m_GUIType == 0)
/* 1201:     */       {
/* 1202:1436 */         int newHeight = (int)(screenHeight * 0.75D);
/* 1203:1437 */         int newWidth = (int)(screenWidth * 0.75D);
/* 1204:1438 */         setSize(1000 > newWidth ? newWidth : 1000, 800 > newHeight ? newHeight : 800);
/* 1205:     */         
/* 1206:1440 */         setLocation((screenWidth - getBounds().width) / 2, (screenHeight - getBounds().height) / 2);
/* 1207:     */       }
/* 1208:1442 */       else if (this.m_GUIType == 1)
/* 1209:     */       {
/* 1210:1443 */         pack();
/* 1211:1444 */         setSize(screenWidth, getHeight());
/* 1212:1445 */         setLocation(0, 0);
/* 1213:     */       }
/* 1214:     */     }
/* 1215:     */     catch (Exception e)
/* 1216:     */     {
/* 1217:1448 */       e.printStackTrace();
/* 1218:     */     }
/* 1219:     */   }
/* 1220:     */   
/* 1221:     */   protected void createTitle(String title)
/* 1222:     */   {
/* 1223:1460 */     String newTitle = "Weka " + new Version();
/* 1224:1461 */     if (title.length() != 0) {
/* 1225:1462 */       newTitle = newTitle + " - " + title;
/* 1226:     */     }
/* 1227:1465 */     setTitle(newTitle);
/* 1228:     */   }
/* 1229:     */   
/* 1230:     */   public void addChildFrame(Container c)
/* 1231:     */   {
/* 1232:1474 */     this.m_ChildFrames.add(c);
/* 1233:1475 */     windowListChanged();
/* 1234:     */   }
/* 1235:     */   
/* 1236:     */   public boolean removeChildFrame(Container c)
/* 1237:     */   {
/* 1238:1485 */     boolean result = this.m_ChildFrames.remove(c);
/* 1239:1486 */     windowListChanged();
/* 1240:1487 */     return result;
/* 1241:     */   }
/* 1242:     */   
/* 1243:     */   public boolean showWindow(Container c)
/* 1244:     */   {
/* 1245:     */     boolean result;
/* 1246:     */     boolean result;
/* 1247:1501 */     if (c != null)
/* 1248:     */     {
/* 1249:     */       try
/* 1250:     */       {
/* 1251:1503 */         if ((c instanceof ChildFrameMDI))
/* 1252:     */         {
/* 1253:1504 */           ChildFrameMDI mdiFrame = (ChildFrameMDI)c;
/* 1254:1505 */           mdiFrame.setIcon(false);
/* 1255:1506 */           mdiFrame.toFront();
/* 1256:1507 */           createTitle(mdiFrame.getTitle());
/* 1257:     */         }
/* 1258:1508 */         else if ((c instanceof ChildFrameSDI))
/* 1259:     */         {
/* 1260:1509 */           ChildFrameSDI sdiFrame = (ChildFrameSDI)c;
/* 1261:1510 */           sdiFrame.setExtendedState(0);
/* 1262:1511 */           sdiFrame.toFront();
/* 1263:1512 */           createTitle(sdiFrame.getTitle());
/* 1264:     */         }
/* 1265:     */       }
/* 1266:     */       catch (Exception e)
/* 1267:     */       {
/* 1268:1515 */         e.printStackTrace();
/* 1269:     */       }
/* 1270:1517 */       result = true;
/* 1271:     */     }
/* 1272:     */     else
/* 1273:     */     {
/* 1274:1519 */       result = false;
/* 1275:     */     }
/* 1276:1522 */     return result;
/* 1277:     */   }
/* 1278:     */   
/* 1279:     */   public boolean showWindow(Class<?> windowClass)
/* 1280:     */   {
/* 1281:1532 */     return showWindow(getWindow(windowClass));
/* 1282:     */   }
/* 1283:     */   
/* 1284:     */   public Iterator<Container> getWindowList()
/* 1285:     */   {
/* 1286:1541 */     return this.m_ChildFrames.iterator();
/* 1287:     */   }
/* 1288:     */   
/* 1289:     */   public Container getWindow(Class<?> windowClass)
/* 1290:     */   {
/* 1291:1556 */     Container result = null;
/* 1292:1557 */     Iterator<Container> iter = getWindowList();
/* 1293:1558 */     while (iter.hasNext())
/* 1294:     */     {
/* 1295:1559 */       Container current = (Container)iter.next();
/* 1296:1560 */       if (current.getClass() == windowClass) {
/* 1297:1561 */         result = current;
/* 1298:     */       }
/* 1299:     */     }
/* 1300:1566 */     return result;
/* 1301:     */   }
/* 1302:     */   
/* 1303:     */   public Container getWindow(String title)
/* 1304:     */   {
/* 1305:1581 */     Container result = null;
/* 1306:1582 */     Iterator<Container> iter = getWindowList();
/* 1307:1583 */     while (iter.hasNext())
/* 1308:     */     {
/* 1309:1584 */       Container current = (Container)iter.next();
/* 1310:1585 */       boolean found = false;
/* 1311:1587 */       if ((current instanceof ChildFrameMDI)) {
/* 1312:1588 */         found = ((ChildFrameMDI)current).getTitle().equals(title);
/* 1313:1589 */       } else if ((current instanceof ChildFrameSDI)) {
/* 1314:1590 */         found = ((ChildFrameSDI)current).getTitle().equals(title);
/* 1315:     */       }
/* 1316:1593 */       if (found) {
/* 1317:1594 */         result = current;
/* 1318:     */       }
/* 1319:     */     }
/* 1320:1599 */     return result;
/* 1321:     */   }
/* 1322:     */   
/* 1323:     */   public boolean containsWindow(Class<?> windowClass)
/* 1324:     */   {
/* 1325:1611 */     return getWindow(windowClass) != null;
/* 1326:     */   }
/* 1327:     */   
/* 1328:     */   public boolean containsWindow(String title)
/* 1329:     */   {
/* 1330:1623 */     return getWindow(title) != null;
/* 1331:     */   }
/* 1332:     */   
/* 1333:     */   public void minimizeWindows()
/* 1334:     */   {
/* 1335:1633 */     Iterator<Container> iter = getWindowList();
/* 1336:1634 */     while (iter.hasNext())
/* 1337:     */     {
/* 1338:1635 */       Container frame = (Container)iter.next();
/* 1339:     */       try
/* 1340:     */       {
/* 1341:1637 */         if ((frame instanceof ChildFrameMDI)) {
/* 1342:1638 */           ((ChildFrameMDI)frame).setIcon(true);
/* 1343:1639 */         } else if ((frame instanceof ChildFrameSDI)) {
/* 1344:1640 */           ((ChildFrameSDI)frame).setExtendedState(1);
/* 1345:     */         }
/* 1346:     */       }
/* 1347:     */       catch (Exception e)
/* 1348:     */       {
/* 1349:1643 */         e.printStackTrace();
/* 1350:     */       }
/* 1351:     */     }
/* 1352:     */   }
/* 1353:     */   
/* 1354:     */   public void restoreWindows()
/* 1355:     */   {
/* 1356:1655 */     Iterator<Container> iter = getWindowList();
/* 1357:1656 */     while (iter.hasNext())
/* 1358:     */     {
/* 1359:1657 */       Container frame = (Container)iter.next();
/* 1360:     */       try
/* 1361:     */       {
/* 1362:1659 */         if ((frame instanceof ChildFrameMDI)) {
/* 1363:1660 */           ((ChildFrameMDI)frame).setIcon(false);
/* 1364:1661 */         } else if ((frame instanceof ChildFrameSDI)) {
/* 1365:1662 */           ((ChildFrameSDI)frame).setExtendedState(0);
/* 1366:     */         }
/* 1367:     */       }
/* 1368:     */       catch (Exception e)
/* 1369:     */       {
/* 1370:1665 */         e.printStackTrace();
/* 1371:     */       }
/* 1372:     */     }
/* 1373:     */   }
/* 1374:     */   
/* 1375:     */   public void windowListChanged()
/* 1376:     */   {
/* 1377:1674 */     createWindowMenu();
/* 1378:     */   }
/* 1379:     */   
/* 1380:     */   protected synchronized void createWindowMenu()
/* 1381:     */   {
/* 1382:1686 */     this.jMenuWindows.removeAll();
/* 1383:     */     
/* 1384:     */ 
/* 1385:1689 */     JMenuItem menuItem = new JMenuItem("Minimize");
/* 1386:1690 */     menuItem.addActionListener(new ActionListener()
/* 1387:     */     {
/* 1388:     */       public void actionPerformed(ActionEvent evt)
/* 1389:     */       {
/* 1390:1693 */         Main.this.minimizeWindows();
/* 1391:     */       }
/* 1392:1695 */     });
/* 1393:1696 */     this.jMenuWindows.add(menuItem);
/* 1394:     */     
/* 1395:1698 */     menuItem = new JMenuItem("Restore");
/* 1396:1699 */     menuItem.addActionListener(new ActionListener()
/* 1397:     */     {
/* 1398:     */       public void actionPerformed(ActionEvent evt)
/* 1399:     */       {
/* 1400:1702 */         Main.this.restoreWindows();
/* 1401:     */       }
/* 1402:1704 */     });
/* 1403:1705 */     this.jMenuWindows.add(menuItem);
/* 1404:     */     
/* 1405:1707 */     this.jMenuWindows.addSeparator();
/* 1406:     */     
/* 1407:     */ 
/* 1408:1710 */     int startIndex = this.jMenuWindows.getMenuComponentCount() - 1;
/* 1409:1711 */     Iterator<Container> iter = getWindowList();
/* 1410:1712 */     this.jMenuWindows.setVisible(iter.hasNext());
/* 1411:1713 */     while (iter.hasNext())
/* 1412:     */     {
/* 1413:1714 */       Container frame = (Container)iter.next();
/* 1414:1715 */       if ((frame instanceof ChildFrameMDI)) {
/* 1415:1716 */         menuItem = new JMenuItem(((ChildFrameMDI)frame).getTitle());
/* 1416:1717 */       } else if ((frame instanceof ChildFrameSDI)) {
/* 1417:1718 */         menuItem = new JMenuItem(((ChildFrameSDI)frame).getTitle());
/* 1418:     */       }
/* 1419:1720 */       insertMenuItem(this.jMenuWindows, menuItem, startIndex);
/* 1420:1721 */       menuItem.setActionCommand(Integer.toString(frame.hashCode()));
/* 1421:1722 */       menuItem.addActionListener(new ActionListener()
/* 1422:     */       {
/* 1423:     */         public void actionPerformed(ActionEvent evt)
/* 1424:     */         {
/* 1425:1725 */           Container frame = null;
/* 1426:1726 */           Iterator<Container> iter = Main.this.getWindowList();
/* 1427:1727 */           while (iter.hasNext())
/* 1428:     */           {
/* 1429:1728 */             frame = (Container)iter.next();
/* 1430:1729 */             String hashFrame = Integer.toString(frame.hashCode());
/* 1431:1730 */             if (hashFrame.equals(evt.getActionCommand()))
/* 1432:     */             {
/* 1433:1731 */               Main.this.showWindow(frame);
/* 1434:1732 */               break;
/* 1435:     */             }
/* 1436:     */           }
/* 1437:1735 */           Main.this.showWindow(frame);
/* 1438:     */         }
/* 1439:     */       });
/* 1440:     */     }
/* 1441:     */   }
/* 1442:     */   
/* 1443:     */   public void setVisible(boolean b)
/* 1444:     */   {
/* 1445:1748 */     super.setVisible(b);
/* 1446:1750 */     if (b) {
/* 1447:1751 */       paint(getGraphics());
/* 1448:     */     }
/* 1449:     */   }
/* 1450:     */   
/* 1451:     */   public static void createSingleton(String[] args)
/* 1452:     */   {
/* 1453:1761 */     if (m_MainSingleton == null) {
/* 1454:1762 */       m_MainSingleton = new Main();
/* 1455:     */     }
/* 1456:     */     try
/* 1457:     */     {
/* 1458:1767 */       m_MainSingleton.setOptions(args);
/* 1459:     */     }
/* 1460:     */     catch (Exception e)
/* 1461:     */     {
/* 1462:1769 */       e.printStackTrace();
/* 1463:     */     }
/* 1464:1773 */     for (int i = 0; i < m_StartupListeners.size(); i++) {
/* 1465:1774 */       ((StartUpListener)m_StartupListeners.elementAt(i)).startUpComplete();
/* 1466:     */     }
/* 1467:     */   }
/* 1468:     */   
/* 1469:     */   public static Main getSingleton()
/* 1470:     */   {
/* 1471:1784 */     return m_MainSingleton;
/* 1472:     */   }
/* 1473:     */   
/* 1474:     */   public static void addStartupListener(StartUpListener s)
/* 1475:     */   {
/* 1476:1793 */     m_StartupListeners.add(s);
/* 1477:     */   }
/* 1478:     */   
/* 1479:     */   public Enumeration<Option> listOptions()
/* 1480:     */   {
/* 1481:1808 */     Vector<Option> result = new Vector();
/* 1482:     */     
/* 1483:1810 */     String desc = "";
/* 1484:1811 */     for (int i = 0; i < TAGS_GUI.length; i++)
/* 1485:     */     {
/* 1486:1812 */       SelectedTag tag = new SelectedTag(TAGS_GUI[i].getID(), TAGS_GUI);
/* 1487:1813 */       desc = desc + "\t" + tag.getSelectedTag().getIDStr() + " = " + tag.getSelectedTag().getReadable() + "\n";
/* 1488:     */     }
/* 1489:1816 */     result.addElement(new Option("\tDetermines the layout of the GUI:\n" + desc + "\t(default: " + new SelectedTag(0, TAGS_GUI) + ")", "gui", 1, "-gui " + Tag.toOptionList(TAGS_GUI)));
/* 1490:     */     
/* 1491:     */ 
/* 1492:     */ 
/* 1493:1820 */     return result.elements();
/* 1494:     */   }
/* 1495:     */   
/* 1496:     */   public String[] getOptions()
/* 1497:     */   {
/* 1498:1832 */     Vector<String> result = new Vector();
/* 1499:     */     
/* 1500:1834 */     result.add("-gui");
/* 1501:1835 */     result.add("" + getGUIType());
/* 1502:     */     
/* 1503:1837 */     return (String[])result.toArray(new String[result.size()]);
/* 1504:     */   }
/* 1505:     */   
/* 1506:     */   public void setOptions(String[] options)
/* 1507:     */     throws Exception
/* 1508:     */   {
/* 1509:1864 */     String tmpStr = Utils.getOption("gui", options);
/* 1510:1865 */     if (tmpStr.length() != 0) {
/* 1511:1866 */       setGUIType(new SelectedTag(tmpStr, TAGS_GUI));
/* 1512:     */     } else {
/* 1513:1868 */       setGUIType(new SelectedTag(0, TAGS_GUI));
/* 1514:     */     }
/* 1515:     */   }
/* 1516:     */   
/* 1517:     */   public void setGUIType(SelectedTag value)
/* 1518:     */   {
/* 1519:1878 */     if (value.getTags() == TAGS_GUI)
/* 1520:     */     {
/* 1521:1879 */       this.m_GUIType = value.getSelectedTag().getID();
/* 1522:1880 */       initGUI();
/* 1523:     */     }
/* 1524:     */   }
/* 1525:     */   
/* 1526:     */   public SelectedTag getGUIType()
/* 1527:     */   {
/* 1528:1890 */     return new SelectedTag(this.m_GUIType, TAGS_GUI);
/* 1529:     */   }
/* 1530:     */   
/* 1531:     */   public static void main(String[] args)
/* 1532:     */   {
/* 1533:1899 */     Logger.log(Logger.Level.INFO, "Logging started");
/* 1534:     */     
/* 1535:     */ 
/* 1536:1902 */     LookAndFeel.setLookAndFeel();
/* 1537:     */     try
/* 1538:     */     {
/* 1539:1909 */       if (Utils.getFlag('h', args))
/* 1540:     */       {
/* 1541:1910 */         System.out.println();
/* 1542:1911 */         System.out.println("Help requested.");
/* 1543:1912 */         System.out.println();
/* 1544:1913 */         System.out.println("General options:");
/* 1545:1914 */         System.out.println();
/* 1546:1915 */         System.out.println("-h");
/* 1547:1916 */         System.out.println("\tprints this help screen");
/* 1548:1917 */         System.out.println();
/* 1549:     */         
/* 1550:1919 */         Enumeration<Option> enu = new Main().listOptions();
/* 1551:1920 */         while (enu.hasMoreElements())
/* 1552:     */         {
/* 1553:1921 */           Option option = (Option)enu.nextElement();
/* 1554:1922 */           System.out.println(option.synopsis());
/* 1555:1923 */           System.out.println(option.description());
/* 1556:     */         }
/* 1557:1926 */         System.out.println();
/* 1558:1927 */         System.exit(0);
/* 1559:     */       }
/* 1560:1931 */       addStartupListener(new StartUpListener()
/* 1561:     */       {
/* 1562:     */         public void startUpComplete()
/* 1563:     */         {
/* 1564:1934 */           Main.m_MainCommandline = Main.getSingleton();
/* 1565:1935 */           Main.m_MainCommandline.setVisible(true);
/* 1566:     */         }
/* 1567:1937 */       });
/* 1568:1938 */       addStartupListener(new StartUpListener()
/* 1569:     */       {
/* 1570:     */         public void startUpComplete() {}
/* 1571:1943 */       });
/* 1572:1944 */       SplashWindow.splash(ClassLoader.getSystemResource("weka/gui/images/weka_splash.gif"));
/* 1573:     */       
/* 1574:     */ 
/* 1575:     */ 
/* 1576:1948 */       String[] options = (String[])args.clone();
/* 1577:1949 */       Thread nt = new Thread()
/* 1578:     */       {
/* 1579:     */         public void run()
/* 1580:     */         {
/* 1581:1952 */           SplashWindow.invokeMethod(Main.class.getName(), "createSingleton", this.val$options);
/* 1582:     */         }
/* 1583:1955 */       };
/* 1584:1956 */       nt.start();
/* 1585:     */       
/* 1586:1958 */       Thread memMonitor = new Thread()
/* 1587:     */       {
/* 1588:     */         public void run()
/* 1589:     */         {
/* 1590:     */           for (;;)
/* 1591:     */           {
/* 1592:1965 */             if (Main.m_Memory.isOutOfMemory())
/* 1593:     */             {
/* 1594:1967 */               Main.m_MainCommandline = null;
/* 1595:1968 */               System.gc();
/* 1596:     */               
/* 1597:     */ 
/* 1598:1971 */               System.err.println("\ndisplayed message:");
/* 1599:1972 */               Main.m_Memory.showOutOfMemory();
/* 1600:1973 */               System.err.println("\nexiting");
/* 1601:1974 */               System.exit(-1);
/* 1602:     */             }
/* 1603:     */           }
/* 1604:     */         }
/* 1605:1983 */       };
/* 1606:1984 */       memMonitor.setPriority(10);
/* 1607:1985 */       memMonitor.start();
/* 1608:     */     }
/* 1609:     */     catch (Exception ex)
/* 1610:     */     {
/* 1611:1987 */       ex.printStackTrace();
/* 1612:1988 */       System.err.println(ex.getMessage());
/* 1613:     */     }
/* 1614:     */   }
/* 1615:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.Main
 * JD-Core Version:    0.7.0.1
 */